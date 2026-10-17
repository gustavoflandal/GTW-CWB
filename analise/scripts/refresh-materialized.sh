#!/bin/bash
# =============================================================================
# Recarga das tabelas materializadas (analise.mat_*)
#
# Executa as stored procedures de carga no SQL Server.
# Agenda sugerida (crontab):
#   */30 * * * * cd /opt/analise && ./scripts/refresh-materialized.sh >> /var/log/superset-refresh.log 2>&1
#
# Pre-requisitos:
#   - sqlcmd ou mssql-tools instalado no host (ou via Docker)
#   - Variaveis GTW_DB_* definidas no .env
# =============================================================================
set -euo pipefail

if [ -f ".env" ]; then
    set -a
    source .env
    set +a
fi

DB_HOST="${GTW_DB_HOST:?GTW_DB_HOST nao definido}"
DB_NAME="${GTW_DB_NAME:-GTW_MURALHA_DEV}"
DB_USER="${GTW_DB_USER:?GTW_DB_USER nao definido}"
DB_PASS="${GTW_DB_PASSWORD:?GTW_DB_PASSWORD nao definido}"

echo "[$(date)] Iniciando recarga das tabelas materializadas..."

sqlcmd -S "$DB_HOST" -d "$DB_NAME" -U "$DB_USER" -P "$DB_PASS" -Q "
SET QUOTED_IDENTIFIER ON;
EXEC analise.sp_carga_passagens_hora;
EXEC analise.sp_carga_infracoes_dia;
" 2>&1

echo "[$(date)] Recarga concluida."
