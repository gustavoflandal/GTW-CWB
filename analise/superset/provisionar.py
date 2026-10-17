#!/usr/bin/env python3
# =============================================================================
# provisionar.py — Provisionamento inicial da conexão GTW no Superset
#
# Cria a conexão com o banco SQL Server do GTW como somente leitura.
# Idempotente: verifica se a conexão já existe antes de criar.
#
# Uso:
#   docker compose exec superset python /app/provisionar.py
#
# Variáveis de ambiente necessárias (definidas no .env):
#   GTW_DB_HOST, GTW_DB_PORT, GTW_DB_NAME, GTW_DB_USER, GTW_DB_PASSWORD
# =============================================================================

import json
import logging
import os
import sys

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
)
logger = logging.getLogger(__name__)

# Nome exibido no Superset para a conexão
NOME_CONEXAO = "GTW Muralha Digital (somente leitura)"


def _obter_uri_sqlserver() -> str:
    """
    Monta a URI de conexão pymssql para o SQL Server do GTW.
    Formato: mssql+pymssql://usuario:senha@host:porta/banco
    """
    host = os.environ["GTW_DB_HOST"]
    porta = os.environ.get("GTW_DB_PORT", "1433")
    banco = os.environ["GTW_DB_NAME"]
    usuario = os.environ["GTW_DB_USER"]
    senha = os.environ["GTW_DB_PASSWORD"]
    return f"mssql+pymssql://{usuario}:{senha}@{host}:{porta}/{banco}"


def _conexao_existe(sessao_db) -> bool:
    """
    Verifica se a conexão com o nome esperado já existe no Superset.
    """
    from superset.models.core import Database

    existente = (
        sessao_db.query(Database)
        .filter(Database.database_name == NOME_CONEXAO)
        .first()
    )
    return existente is not None


def _criar_conexao(sessao_db):
    """
    Cria a conexão do GTW no Superset com configurações de segurança:
    - allow_dml=False (somente leitura)
    - allow_ctas=False (sem CREATE TABLE AS)
    - allow_cvas=False (sem CREATE VIEW AS)
    - allow_file_upload=False (sem upload de arquivos)
    """
    from superset.models.core import Database

    uri = _obter_uri_sqlserver()

    extras = json.dumps({
        "metadata_params": {},
        "engine_params": {
            "connect_args": {
                "charset": "utf8",
            }
        },
        "metadata_cache_timeout": {},
        "schemas_allowed_for_file_upload": [],
    })

    conexao = Database(
        database_name=NOME_CONEXAO,
        sqlalchemy_uri=uri,
        expose_in_sqllab=True,
        allow_run_async=True,
        allow_dml=False,
        allow_ctas=False,
        allow_cvas=False,
        allow_file_upload=False,
        extra=extras,
    )

    sessao_db.add(conexao)
    sessao_db.commit()
    logger.info("Conexão '%s' criada com sucesso.", NOME_CONEXAO)


def provisionar():
    """
    Função principal de provisionamento. Idempotente.
    """
    # Importações do Superset (disponíveis dentro do container)
    from superset.app import create_app

    app = create_app()

    with app.app_context():
        from superset.extensions import db as superset_db

        sessao = superset_db.session

        if _conexao_existe(sessao):
            logger.info(
                "Conexão '%s' já existe — nenhuma ação necessária.",
                NOME_CONEXAO,
            )
            return

        logger.info("Criando conexão '%s'...", NOME_CONEXAO)
        _criar_conexao(sessao)


if __name__ == "__main__":
    try:
        provisionar()
    except Exception as erro:
        logger.error("Erro no provisionamento: %s", erro, exc_info=True)
        sys.exit(1)

    logger.info("Provisionamento concluído.")
