# =============================================================================
# Dockerfile — Apache Superset 6.0 para o projeto Análises GTW-CWB
# Imagem fixada; atualização de versão requer sessão dedicada com regressão.
# =============================================================================
FROM apache/superset:6.0.0

USER root

# Drivers e dependências Python com versões fixadas
# Superset 6.0 usa venv em /app/.venv sem pip; instala com pip do sistema no target do venv
RUN pip install --no-cache-dir \
    --target=/app/.venv/lib/python3.10/site-packages \
    pymssql==2.3.0 \
    psycopg2-binary==2.9.9 \
    redis==5.0.8 \
    pyyaml==6.0.2

# Configuração customizada do Superset
COPY superset/superset_config.py /app/superset_config.py
COPY superset/gtw_security.py /app/gtw_security.py
COPY superset/provisionar.py /app/provisionar.py

# Definições declarativas (SUP-3 a SUP-7)
COPY superset/datasets/ /app/datasets/
COPY superset/dashboards/ /app/dashboards/
COPY superset/governanca/ /app/governanca/
COPY superset/alertas/ /app/alertas/

# Painéis de demonstração com dados reais
COPY superset/demo/ /app/demo/

# Variável que o Superset usa para localizar o arquivo de configuração
ENV SUPERSET_CONFIG_PATH=/app/superset_config.py

USER superset
