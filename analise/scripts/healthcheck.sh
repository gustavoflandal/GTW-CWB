#!/bin/bash
# =============================================================================
# Health check do stack Superset — verifica todos os servicos
#
# Uso:
#   ./scripts/healthcheck.sh           # saida legivel
#   ./scripts/healthcheck.sh --json    # saida JSON (para monitoramento)
#
# Exit codes:
#   0 = todos saudaveis
#   1 = ao menos um servico com problema
# =============================================================================
set -uo pipefail

JSON_MODE=false
[ "${1:-}" = "--json" ] && JSON_MODE=true

STATUS=0

check_service() {
    local name="$1"
    local cmd="$2"

    if eval "$cmd" > /dev/null 2>&1; then
        if [ "$JSON_MODE" = true ]; then
            echo "    \"${name}\": \"ok\","
        else
            echo "  [OK]    $name"
        fi
    else
        STATUS=1
        if [ "$JSON_MODE" = true ]; then
            echo "    \"${name}\": \"erro\","
        else
            echo "  [ERRO]  $name"
        fi
    fi
}

if [ "$JSON_MODE" = true ]; then
    echo "{"
    echo "  \"timestamp\": \"$(date -Iseconds)\","
    echo "  \"servicos\": {"
fi

check_service "superset"      "docker compose exec -T superset curl -sf http://localhost:8088/health"
check_service "postgres"      "docker compose exec -T postgres pg_isready -U \${POSTGRES_USER:-superset}"
check_service "redis"         "docker compose exec -T redis redis-cli ping | grep -q PONG"
check_service "celery-worker" "docker compose exec -T celery-worker celery -A superset.tasks.celery_app:app inspect ping --timeout 10"
check_service "celery-beat"   "docker compose ps celery-beat | grep -q running"
check_service "nginx"         "curl -sf http://localhost:8088/nginx-health"

if [ "$JSON_MODE" = true ]; then
    echo "    \"_status\": \"$([ $STATUS -eq 0 ] && echo 'saudavel' || echo 'degradado')\""
    echo "  }"
    echo "}"
else
    echo ""
    if [ $STATUS -eq 0 ]; then
        echo "Resultado: TODOS OS SERVICOS SAUDAVEIS"
    else
        echo "Resultado: ATENCAO — ao menos um servico com problema"
    fi
fi

exit $STATUS
