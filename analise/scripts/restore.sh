#!/bin/bash
# =============================================================================
# Restauracao do stack Superset a partir de backup
#
# Uso:
#   ./scripts/restore.sh ./backups/superset_20261017_020000.tar.gz
#
# ATENCAO: Para o stack antes de restaurar. Os dados atuais serao sobrescritos.
# =============================================================================
set -euo pipefail

BACKUP_FILE="${1:?Uso: ./scripts/restore.sh <arquivo.tar.gz>}"

if [ ! -f "$BACKUP_FILE" ]; then
    echo "ERRO: Arquivo nao encontrado: $BACKUP_FILE"
    exit 1
fi

echo "========================================="
echo " RESTAURACAO DO SUPERSET"
echo " Arquivo: $BACKUP_FILE"
echo "========================================="
echo ""
echo "ATENCAO: Isso sobrescrevera os metadados atuais do Superset."
read -rp "Continuar? [s/N] " resposta
if [[ ! "$resposta" =~ ^[sS]$ ]]; then
    echo "Cancelado."
    exit 0
fi

# -------------------------------------------------------------------------
# 1. Extrair backup
# -------------------------------------------------------------------------
TEMP_DIR=$(mktemp -d)
echo "[$(date)] Extraindo backup..."
tar -xzf "$BACKUP_FILE" -C "$TEMP_DIR"
BACKUP_DIR=$(find "$TEMP_DIR" -maxdepth 1 -type d -name "superset_*" | head -1)

if [ -z "$BACKUP_DIR" ]; then
    echo "ERRO: Estrutura de backup invalida."
    rm -rf "$TEMP_DIR"
    exit 1
fi

# -------------------------------------------------------------------------
# 2. Verificar que stack esta parado (exceto postgres)
# -------------------------------------------------------------------------
echo "[$(date)] Parando servicos (exceto PostgreSQL)..."
docker compose stop superset celery-worker celery-beat nginx 2>/dev/null || true

echo "[$(date)] Garantindo que PostgreSQL esta ativo..."
docker compose up -d postgres
sleep 5

# -------------------------------------------------------------------------
# 3. Restaurar PostgreSQL
# -------------------------------------------------------------------------
DUMP_FILE="${BACKUP_DIR}/superset_metadata.dump"
if [ -f "$DUMP_FILE" ]; then
    echo "[$(date)] Restaurando PostgreSQL..."
    docker compose exec -T postgres dropdb -U "${POSTGRES_USER:-superset}" --if-exists superset
    docker compose exec -T postgres createdb -U "${POSTGRES_USER:-superset}" superset
    docker compose exec -T postgres pg_restore \
        -U "${POSTGRES_USER:-superset}" \
        --dbname=superset \
        --no-owner \
        --no-acl < "$DUMP_FILE"
    echo "[$(date)] PostgreSQL restaurado."
else
    echo "AVISO: Dump do PostgreSQL nao encontrado no backup."
fi

# -------------------------------------------------------------------------
# 4. Limpar e reiniciar
# -------------------------------------------------------------------------
rm -rf "$TEMP_DIR"

echo "[$(date)] Reiniciando stack completo..."
docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d

echo ""
echo "[$(date)] Restauracao concluida."
echo "Verifique: docker compose ps"
echo "Acesse: https://<servidor>:8088/"
