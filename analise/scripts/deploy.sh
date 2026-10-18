#!/bin/bash
# =============================================================================
# Deploy do stack Superset em producao
#
# Uso:
#   ./scripts/deploy.sh              # deploy padrao
#   ./scripts/deploy.sh --rebuild    # forca rebuild da imagem
#
# Pre-requisitos:
#   - Docker e Docker Compose instalados
#   - Arquivo .env preenchido (copiar de .env.exemplo)
#   - Certificado TLS em nginx/ssl/ (para HTTPS)
#   - Porta 8088 (ou 443) livre
# =============================================================================
set -euo pipefail

REBUILD=false
if [ "${1:-}" = "--rebuild" ]; then
    REBUILD=true
fi

echo "========================================="
echo " DEPLOY — Superset (Analises GTW-CWB)"
echo " $(date)"
echo "========================================="

# -------------------------------------------------------------------------
# 1. Verificacoes pre-deploy
# -------------------------------------------------------------------------
echo ""
echo "[1/6] Verificacoes..."

if [ ! -f ".env" ]; then
    echo "ERRO: Arquivo .env nao encontrado. Copie de .env.exemplo e preencha."
    exit 1
fi

# Verificar variaveis criticas
for var in SUPERSET_SECRET_KEY SUPERSET_DB_URI POSTGRES_PASSWORD GTW_DB_HOST; do
    val=$(grep "^${var}=" .env | cut -d= -f2-)
    if [ -z "$val" ] || echo "$val" | grep -q "ALTERE_AQUI"; then
        echo "ERRO: Variavel $var nao configurada no .env"
        exit 1
    fi
done

echo "  .env validado."

if ! docker info > /dev/null 2>&1; then
    echo "ERRO: Docker nao esta em execucao."
    exit 1
fi
echo "  Docker ativo."

# -------------------------------------------------------------------------
# 2. Backup pre-deploy
# -------------------------------------------------------------------------
echo ""
echo "[2/6] Backup pre-deploy..."

if docker compose ps postgres 2>/dev/null | grep -q "running"; then
    ./scripts/backup.sh ./backups 2>/dev/null && echo "  Backup concluido." || echo "  AVISO: Backup falhou (primeira execucao?)."
else
    echo "  Sem stack anterior — pulando backup."
fi

# -------------------------------------------------------------------------
# 3. Build da imagem
# -------------------------------------------------------------------------
echo ""
echo "[3/6] Build da imagem..."

if [ "$REBUILD" = true ]; then
    docker compose build --no-cache superset
else
    docker compose build superset
fi
echo "  Imagem construida."

# -------------------------------------------------------------------------
# 4. Subir stack de producao
# -------------------------------------------------------------------------
echo ""
echo "[4/6] Subindo stack de producao..."

docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d
echo "  Stack iniciado."

# -------------------------------------------------------------------------
# 5. Aguardar inicializacao
# -------------------------------------------------------------------------
echo ""
echo "[5/6] Aguardando inicializacao..."

echo "  Aguardando superset-init (migracao + admin)..."
timeout 180 docker compose logs -f superset-init 2>/dev/null | grep -m1 "Inicialização concluída" || true

echo "  Aguardando healthcheck do Superset..."
for i in $(seq 1 30); do
    if docker compose exec -T superset curl -sf http://localhost:8088/health > /dev/null 2>&1; then
        echo "  Superset saudavel apos ${i}0s."
        break
    fi
    sleep 10
done

# -------------------------------------------------------------------------
# 6. Provisionamento
# -------------------------------------------------------------------------
echo ""
echo "[6/6] Provisionamento (conexao, datasets, governanca, paineis, alertas)..."

docker compose exec -T superset python /app/provisionar.py
docker compose exec -T superset python /app/paineis/operacionais.py
docker compose exec -T superset python /app/paineis/demonstracao.py
# Relatorios agendados apontam para os paineis pelo slug: rodar depois dos paineis.
docker compose exec -T superset python /app/provisionar.py --alertas-only
echo "  Provisionamento concluido."

# -------------------------------------------------------------------------
# Resumo
# -------------------------------------------------------------------------
echo ""
echo "========================================="
echo " DEPLOY CONCLUIDO"
echo " $(date)"
echo "========================================="
echo ""
docker compose ps --format "table {{.Name}}\t{{.Status}}\t{{.Ports}}"
echo ""
echo "Acesse: http://localhost:8088/ (ou https se TLS configurado)"
