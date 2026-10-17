#!/usr/bin/env python3
# =============================================================================
# provisionar.py — Provisionamento declarativo do Superset (SUP-1 + SUP-3)
#
# Cria a conexão com o banco SQL Server e provisiona datasets, métricas
# e metadados a partir dos YAMLs em /app/datasets/.
#
# Idempotente: verifica existência antes de criar; atualiza se já existir.
#
# Uso:
#   docker compose exec superset python /app/provisionar.py
#   docker compose exec superset python /app/provisionar.py --datasets-only
#   docker compose exec superset python /app/provisionar.py --dry-run
# =============================================================================

import glob
import json
import logging
import os
import sys
import uuid as uuid_mod

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s [%(levelname)s] %(message)s",
)
logger = logging.getLogger(__name__)

NOME_CONEXAO = "GTW Muralha Digital (somente leitura)"
DATASETS_DIR = os.environ.get("SUPERSET_DATASETS_DIR", "/app/datasets")

# ---------------------------------------------------------------------------
# 1. Conexão com o banco GTW (SUP-1 — inalterado)
# ---------------------------------------------------------------------------

def _obter_uri_sqlserver() -> str:
    host = os.environ["GTW_DB_HOST"]
    porta = os.environ.get("GTW_DB_PORT", "1433")
    banco = os.environ["GTW_DB_NAME"]
    usuario = os.environ["GTW_DB_USER"]
    senha = os.environ["GTW_DB_PASSWORD"]
    return f"mssql+pymssql://{usuario}:{senha}@{host}:{porta}/{banco}"


def _obter_ou_criar_conexao(sessao):
    from superset.models.core import Database

    conexao = (
        sessao.query(Database)
        .filter(Database.database_name == NOME_CONEXAO)
        .first()
    )

    if conexao:
        logger.info("Conexão '%s' já existe (id=%d).", NOME_CONEXAO, conexao.id)
        return conexao

    uri = _obter_uri_sqlserver()

    extras = json.dumps({
        "metadata_params": {},
        "engine_params": {
            "connect_args": {"charset": "utf8"},
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
    sessao.add(conexao)
    sessao.flush()
    logger.info("Conexão '%s' criada (id=%d).", NOME_CONEXAO, conexao.id)
    return conexao


# ---------------------------------------------------------------------------
# 2. Provisionamento de datasets a partir de YAML (SUP-3)
# ---------------------------------------------------------------------------

def _carregar_yamls(diretorio: str) -> list:
    import yaml

    arquivos = sorted(glob.glob(os.path.join(diretorio, "*.yaml")))
    definicoes = []
    for arq in arquivos:
        with open(arq, "r", encoding="utf-8") as f:
            dados = yaml.safe_load(f)
        if dados:
            dados["_arquivo"] = os.path.basename(arq)
            definicoes.append(dados)
    logger.info("Carregados %d YAMLs de '%s'.", len(definicoes), diretorio)
    return definicoes


def _provisionar_dataset(sessao, conexao, definicao: dict, dry_run: bool = False):
    from superset.connectors.sqla.models import SqlaTable, SqlMetric, TableColumn

    nome_tabela = definicao["table_name"]
    schema = definicao.get("schema", "analise")
    ds_uuid = definicao.get("uuid")
    arquivo = definicao.get("_arquivo", "?")

    dataset = (
        sessao.query(SqlaTable)
        .filter(
            SqlaTable.table_name == nome_tabela,
            SqlaTable.schema == schema,
            SqlaTable.database_id == conexao.id,
        )
        .first()
    )

    if dry_run:
        acao = "ATUALIZAR" if dataset else "CRIAR"
        logger.info("[DRY-RUN] %s dataset '%s.%s' (%s)", acao, schema, nome_tabela, arquivo)
        return

    if not dataset:
        dataset = SqlaTable(
            table_name=nome_tabela,
            schema=schema,
            database_id=conexao.id,
        )
        if ds_uuid:
            dataset.uuid = uuid_mod.UUID(ds_uuid)
        sessao.add(dataset)
        sessao.flush()
        logger.info("Dataset '%s.%s' criado (id=%d).", schema, nome_tabela, dataset.id)
    else:
        logger.info("Dataset '%s.%s' já existe (id=%d) — atualizando.", schema, nome_tabela, dataset.id)

    dataset.description = definicao.get("description", "")
    dataset.main_dttm_col = definicao.get("main_dttm_col") or None
    dataset.cache_timeout = definicao.get("cache_timeout", 0) or None
    dataset.default_endpoint = definicao.get("default_endpoint", "")

    warning = definicao.get("warning_markdown", "")
    if warning:
        extra_dict = json.loads(dataset.extra or "{}")
        extra_dict["warning_markdown"] = warning.strip()
        dataset.extra = json.dumps(extra_dict, ensure_ascii=False)

    _sincronizar_colunas(sessao, dataset, definicao.get("columns", []))
    _sincronizar_metricas(sessao, dataset, definicao.get("metrics", []))

    sessao.flush()


def _sincronizar_colunas(sessao, dataset, colunas_yaml: list):
    from superset.connectors.sqla.models import TableColumn

    existentes = {c.column_name: c for c in dataset.columns}

    for col_def in colunas_yaml:
        nome = col_def["name"]
        coluna = existentes.get(nome)

        if not coluna:
            coluna = TableColumn(
                column_name=nome,
                table_id=dataset.id,
            )
            sessao.add(coluna)

        coluna.verbose_name = col_def.get("label", nome)
        coluna.type = col_def.get("type", "VARCHAR")
        coluna.is_dttm = col_def.get("is_dttm", False)
        coluna.groupby = col_def.get("groupby", True)
        coluna.filterable = col_def.get("filterable", True)
        coluna.description = col_def.get("description", "")

        d3 = col_def.get("d3_format")
        if d3:
            coluna.d3_format = d3


def _sincronizar_metricas(sessao, dataset, metricas_yaml: list):
    from superset.connectors.sqla.models import SqlMetric

    existentes = {m.metric_name: m for m in dataset.metrics}

    for met_def in metricas_yaml:
        nome = met_def["name"]
        metrica = existentes.get(nome)

        if not metrica:
            metrica = SqlMetric(
                metric_name=nome,
                table_id=dataset.id,
            )
            sessao.add(metrica)

        metrica.verbose_name = met_def.get("label", nome)
        metrica.expression = met_def["expression"].strip()
        metrica.metric_type = met_def.get("metric_type", "")
        metrica.description = met_def.get("description", "")
        metrica.d3format = met_def.get("d3_format", "")

        certified = met_def.get("certified_by")
        if certified:
            metrica.extra = json.dumps(
                {"certified_by": certified},
                ensure_ascii=False,
            )


# ---------------------------------------------------------------------------
# 3. Permissões: dataset Admin-only
# ---------------------------------------------------------------------------

def _restringir_admin_only(sessao, conexao, definicoes: list):
    from superset.connectors.sqla.models import SqlaTable

    for defn in definicoes:
        if not defn.get("is_admin_only"):
            continue

        nome = defn["table_name"]
        schema = defn.get("schema", "analise")

        dataset = (
            sessao.query(SqlaTable)
            .filter(
                SqlaTable.table_name == nome,
                SqlaTable.schema == schema,
                SqlaTable.database_id == conexao.id,
            )
            .first()
        )

        if not dataset:
            continue

        from flask_appbuilder.security.sqla.models import Role
        from superset.extensions import security_manager

        admin_role = sessao.query(Role).filter(Role.name == "Admin").first()
        if not admin_role:
            logger.warning("Papel 'Admin' não encontrado — dataset '%s' ficará sem restrição.", nome)
            continue

        perm_name = security_manager.get_dataset_perm(
            dataset.database_id, dataset.table_name, dataset.schema
        )
        pv = security_manager.find_permission_view_menu(
            "datasource_access", perm_name
        )

        if pv and pv not in admin_role.permissions:
            admin_role.permissions.append(pv)
            logger.info("Permissão do dataset '%s.%s' restrita ao Admin.", schema, nome)
        elif pv:
            logger.info("Permissão do dataset '%s.%s' já está no Admin.", schema, nome)

    sessao.flush()


# ---------------------------------------------------------------------------
# 4. Orquestração
# ---------------------------------------------------------------------------

def provisionar(datasets_only: bool = False, dry_run: bool = False):
    from superset.app import create_app

    app = create_app()

    with app.app_context():
        from superset.extensions import db as superset_db

        sessao = superset_db.session

        if not datasets_only:
            conexao = _obter_ou_criar_conexao(sessao)
        else:
            from superset.models.core import Database
            conexao = (
                sessao.query(Database)
                .filter(Database.database_name == NOME_CONEXAO)
                .first()
            )
            if not conexao:
                logger.error("Conexão '%s' não encontrada. Execute sem --datasets-only primeiro.", NOME_CONEXAO)
                sys.exit(1)

        definicoes = _carregar_yamls(DATASETS_DIR)

        for defn in definicoes:
            _provisionar_dataset(sessao, conexao, defn, dry_run=dry_run)

        if not dry_run:
            _restringir_admin_only(sessao, conexao, definicoes)
            sessao.commit()
            logger.info(
                "Provisionamento concluído: %d datasets processados.",
                len(definicoes),
            )
        else:
            logger.info("[DRY-RUN] Nenhuma alteração gravada.")


if __name__ == "__main__":
    args = sys.argv[1:]
    datasets_only = "--datasets-only" in args
    dry_run = "--dry-run" in args

    try:
        provisionar(datasets_only=datasets_only, dry_run=dry_run)
    except Exception as erro:
        logger.error("Erro no provisionamento: %s", erro, exc_info=True)
        sys.exit(1)
