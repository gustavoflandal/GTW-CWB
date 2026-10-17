# =============================================================================
# Dockerfile — Apache Superset 6.0 para o projeto Análises GTW-CWB
# Imagem fixada; atualização de versão requer sessão dedicada com regressão.
# =============================================================================
FROM apache/superset:6.0.0

USER root

# Drivers e dependências Python com versões fixadas
RUN pip install --no-cache-dir \
    pymssql==2.3.0 \
    psycopg2-binary==2.9.9 \
    redis==5.0.8

# Configuração customizada do Superset
COPY superset/superset_config.py /app/superset_config.py
COPY superset/gtw_security.py /app/gtw_security.py
COPY superset/provisionar.py /app/provisionar.py

# Variável que o Superset usa para localizar o arquivo de configuração
ENV SUPERSET_CONFIG_PATH=/app/superset_config.py

USER superset
