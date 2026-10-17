#!/bin/bash
# =============================================================================
# Backup do stack Superset (PostgreSQL + volumes)
#
# Uso:
#   ./scripts/backup.sh                    # backup padrao em ./backups/
#   ./scripts/backup.sh /mnt/nfs/backups   # destino customizado
#
# Agenda sugerida (crontab):
#   0 2 * * * cd /opt/analise && ./scripts/backup.sh /mnt/backups >> /var/log/superset-backup.log 2>&1
# =============================================================================
set -euo pipefail

DEST="${1:-./backups}"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="${DEST}/superset_${TIMESTAMP}"

mkdir -p "$BACKUP_DIR"

echo "[$(date)] Iniciando backup do Superset..."

# -------------------------------------------------------------------------
# 1. Dump do PostgreSQL (metadados do Superset)
# -------------------------------------------------------------------------
echo "[$(date)] Exportando PostgreSQL..."
docker compose exec -T postgres pg_dump \
    -U "${POSTGRES_USER:-superset}" \
    --format=custom \
    --compress=6 \
    superset > "${BACKUP_DIR}/superset_metadata.dump"

echo "[$(date)] PostgreSQL exportado: $(du -h "${BACKUP_DIR}/superset_metadata.dump" | cut -f1)"

# -------------------------------------------------------------------------
# 2. Configuracoes (sem segredos)
# -------------------------------------------------------------------------
echo "[$(date)] Copiando configuracoes..."
cp docker-compose.yml "${BACKUP_DIR}/"
cp docker-compose.prod.yml "${BACKUP_DIR}/"
cp nginx/default.conf "${BACKUP_DIR}/nginx_default.conf"
[ -f nginx/ssl.conf ] && cp nginx/ssl.conf "${BACKUP_DIR}/nginx_ssl.conf"

# -------------------------------------------------------------------------
# 3. YAMLs declarativos (datasets, dashboards, governanca, alertas)
# -------------------------------------------------------------------------
echo "[$(date)] Copiando YAMLs declarativos..."
for dir in datasets dashboards governanca alertas; do
    if [ -d "superset/${dir}" ]; then
        cp -r "superset/${dir}" "${BACKUP_DIR}/${dir}"
    fi
done

# -------------------------------------------------------------------------
# 4. Compactar
# -------------------------------------------------------------------------
echo "[$(date)] Compactando..."
tar -czf "${BACKUP_DIR}.tar.gz" -C "$DEST" "superset_${TIMESTAMP}"
rm -rf "$BACKUP_DIR"

echo "[$(date)] Backup concluido: ${BACKUP_DIR}.tar.gz ($(du -h "${BACKUP_DIR}.tar.gz" | cut -f1))"

# -------------------------------------------------------------------------
# 5. Rotacao — manter ultimos 30 dias
# -------------------------------------------------------------------------
find "$DEST" -name "superset_*.tar.gz" -mtime +30 -delete 2>/dev/null || true
echo "[$(date)] Rotacao concluida (backups > 30 dias removidos)."
