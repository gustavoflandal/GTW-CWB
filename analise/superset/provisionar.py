#!/usr/bin/env python3
# =============================================================================
# provisionar.py — Provisionamento declarativo do Superset (SUP-1 a SUP-7)
#
# Cria a conexão com o banco SQL Server e provisiona datasets, métricas,
# tags, RLS, papéis e alertas/relatórios a partir dos YAMLs em /app/datasets/,
# /app/governanca/ e /app/alertas/.
#
# Os painéis (dashboards e gráficos) são publicados por /app/paineis/operacionais.py
# e /app/paineis/demonstracao.py. Os relatórios agendados apontam para os painéis pelo
# slug: numa instalação nova, publique os painéis e rode depois --alertas-only.
#
# Idempotente: verifica existência antes de criar; atualiza se já existir.
#
# Uso:
#   docker compose exec superset python /app/provisionar.py
#   docker compose exec superset python /app/provisionar.py --datasets-only
#   docker compose exec superset python /app/provisionar.py --alertas-only
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
GOVERNANCA_DIR = os.environ.get("SUPERSET_GOVERNANCA_DIR", "/app/governanca")
ALERTAS_DIR = os.environ.get("SUPERSET_ALERTAS_DIR", "/app/alertas")

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
# 4. Governança: tags, papéis e RLS (SUP-5)
# ---------------------------------------------------------------------------

def _provisionar_governanca(sessao, conexao, dry_run: bool = False):
    import yaml

    tags_path = os.path.join(GOVERNANCA_DIR, "tags.yaml")
    papeis_path = os.path.join(GOVERNANCA_DIR, "papeis.yaml")
    rls_path = os.path.join(GOVERNANCA_DIR, "rls.yaml")

    if os.path.isfile(tags_path):
        with open(tags_path, "r", encoding="utf-8") as f:
            tags_def = yaml.safe_load(f)
        if tags_def:
            _provisionar_tags(sessao, tags_def.get("tags", []), dry_run=dry_run)

    if os.path.isfile(papeis_path):
        with open(papeis_path, "r", encoding="utf-8") as f:
            papeis_def = yaml.safe_load(f)
        if papeis_def:
            _provisionar_papeis(sessao, papeis_def.get("papeis", []), dry_run=dry_run)

    if os.path.isfile(rls_path):
        with open(rls_path, "r", encoding="utf-8") as f:
            rls_def = yaml.safe_load(f)
        if rls_def:
            _provisionar_rls(sessao, conexao, rls_def.get("regras", []), dry_run=dry_run)


def _provisionar_tags(sessao, tags_list: list, dry_run: bool = False):
    from superset.tags.models import Tag, TagType

    for tag_def in tags_list:
        nome = tag_def["name"]
        descricao = tag_def.get("description", "")

        tag = sessao.query(Tag).filter(Tag.name == nome, Tag.type == TagType.custom).first()

        if dry_run:
            acao = "ATUALIZAR" if tag else "CRIAR"
            logger.info("[DRY-RUN] %s tag '%s'", acao, nome)
            continue

        if not tag:
            tag = Tag(name=nome, type=TagType.custom, description=descricao)
            sessao.add(tag)
            logger.info("Tag '%s' criada.", nome)
        else:
            tag.description = descricao
            logger.info("Tag '%s' já existe — atualizada.", nome)

    sessao.flush()


def _provisionar_papeis(sessao, papeis_list: list, dry_run: bool = False):
    from flask_appbuilder.security.sqla.models import Role
    from superset.extensions import security_manager

    for papel_def in papeis_list:
        nome = papel_def["name"]
        descricao = papel_def.get("description", "")

        papel = sessao.query(Role).filter(Role.name == nome).first()

        if dry_run:
            acao = "ATUALIZAR" if papel else "CRIAR"
            logger.info("[DRY-RUN] %s papel '%s'", acao, nome)
            continue

        if not papel:
            base_name = papel_def.get("copy_from")
            if base_name:
                base_role = sessao.query(Role).filter(Role.name == base_name).first()
                if base_role:
                    papel = security_manager.add_role(nome)
                    papel.permissions = list(base_role.permissions)
                    logger.info("Papel '%s' criado (cópia de '%s').", nome, base_name)
                else:
                    papel = security_manager.add_role(nome)
                    logger.warning("Papel base '%s' não encontrado — '%s' criado vazio.", base_name, nome)
            else:
                papel = security_manager.add_role(nome)
                logger.info("Papel '%s' criado.", nome)
        else:
            logger.info("Papel '%s' já existe.", nome)

    sessao.flush()


def _provisionar_rls(sessao, conexao, regras_list: list, dry_run: bool = False):
    from superset.connectors.sqla.models import SqlaTable, RowLevelSecurityFilter
    from flask_appbuilder.security.sqla.models import Role

    for regra in regras_list:
        nome = regra["name"]
        clause = regra["clause"]
        datasets_nomes = regra.get("datasets", [])
        roles_nomes = regra.get("roles", [])

        if not datasets_nomes:
            logger.info("RLS '%s' sem datasets — ignorando.", nome)
            continue

        rls = (
            sessao.query(RowLevelSecurityFilter)
            .filter(RowLevelSecurityFilter.name == nome)
            .first()
        )

        if dry_run:
            acao = "ATUALIZAR" if rls else "CRIAR"
            logger.info("[DRY-RUN] %s RLS '%s'", acao, nome)
            continue

        datasets = []
        for ds_name in datasets_nomes:
            ds = (
                sessao.query(SqlaTable)
                .filter(SqlaTable.table_name == ds_name, SqlaTable.database_id == conexao.id)
                .first()
            )
            if ds:
                datasets.append(ds)
            else:
                logger.warning("Dataset '%s' não encontrado para RLS '%s'.", ds_name, nome)

        roles = []
        for role_name in roles_nomes:
            role = sessao.query(Role).filter(Role.name == role_name).first()
            if role:
                roles.append(role)
            else:
                logger.warning("Papel '%s' não encontrado para RLS '%s'.", role_name, nome)

        if not rls:
            rls = RowLevelSecurityFilter(
                name=nome,
                clause=clause,
                filter_type="Regular",
                description=regra.get("description", ""),
            )
            sessao.add(rls)
            sessao.flush()
            logger.info("RLS '%s' criada.", nome)
        else:
            rls.clause = clause
            rls.description = regra.get("description", "")
            logger.info("RLS '%s' já existe — atualizada.", nome)

        rls.tables = datasets
        rls.roles = roles

    sessao.flush()


# ---------------------------------------------------------------------------
# 5. Auditoria: limpeza do action log (SUP-6)
# ---------------------------------------------------------------------------

def _limpar_action_log(sessao, dias_retencao: int = 90, dry_run: bool = False):
    from superset.models.core import Log
    from datetime import datetime, timedelta

    limite = datetime.utcnow() - timedelta(days=dias_retencao)

    qtd = sessao.query(Log).filter(Log.dttm < limite).count()

    if dry_run:
        logger.info("[DRY-RUN] LIMPARIA %d entradas do action log (>%d dias).", qtd, dias_retencao)
        return

    if qtd > 0:
        sessao.query(Log).filter(Log.dttm < limite).delete(synchronize_session=False)
        logger.info("Action log: %d entradas removidas (>%d dias).", qtd, dias_retencao)
    else:
        logger.info("Action log: nenhuma entrada para limpar (>%d dias).", dias_retencao)


# ---------------------------------------------------------------------------
# 6. Alertas e relatórios agendados (SUP-7)
# ---------------------------------------------------------------------------

def _provisionar_alertas(sessao, conexao, dry_run: bool = False):
    alertas_yamls = _carregar_yamls(ALERTAS_DIR)
    if not alertas_yamls:
        logger.info("Nenhum YAML de alerta/relatório encontrado em '%s'.", ALERTAS_DIR)
        return

    for defn in alertas_yamls:
        _provisionar_alerta(sessao, conexao, defn, dry_run=dry_run)


def _provisionar_alerta(sessao, conexao, definicao: dict, dry_run: bool = False):
    from superset.reports.models import (
        ReportSchedule,
        ReportScheduleType,
        ReportRecipients,
        ReportRecipientType,
    )
    from superset.models.dashboard import Dashboard

    nome = definicao["name"]
    tipo_str = definicao.get("type", "alert")
    al_uuid = definicao.get("uuid")
    arquivo = definicao.get("_arquivo", "?")

    tipo = (
        ReportScheduleType.ALERT
        if tipo_str == "alert"
        else ReportScheduleType.REPORT
    )

    report = (
        sessao.query(ReportSchedule)
        .filter(ReportSchedule.name == nome)
        .first()
    )

    if dry_run:
        acao = "ATUALIZAR" if report else "CRIAR"
        logger.info("[DRY-RUN] %s %s '%s' (%s)", acao, tipo_str, nome, arquivo)
        return

    if not report:
        report = ReportSchedule(
            name=nome,
            type=tipo,
            crontab=definicao.get("crontab", "0 * * * *"),
            timezone=definicao.get("timezone", "America/Bahia"),
        )
        if al_uuid:
            report.uuid = uuid_mod.UUID(al_uuid)
        sessao.add(report)
        sessao.flush()
        logger.info("%s '%s' criado (id=%d).", tipo_str.capitalize(), nome, report.id)
    else:
        report.type = tipo
        logger.info("%s '%s' já existe (id=%d) — atualizando.", tipo_str.capitalize(), nome, report.id)

    report.description = definicao.get("description", "")
    report.active = definicao.get("active", True)
    report.crontab = definicao.get("crontab", "0 * * * *")
    report.timezone = definicao.get("timezone", "America/Bahia")
    report.grace_period = definicao.get("grace_period", 3600)
    report.log_retention = definicao.get("log_retention", 90)

    if tipo == ReportScheduleType.ALERT:
        report.database_id = conexao.id
        report.sql = definicao.get("sql", "")
        report.validator_type = definicao.get("validator_type", "operator")
        validator_cfg = definicao.get("validator_config", {})
        report.validator_config_json = json.dumps(validator_cfg, ensure_ascii=False)

    if tipo == ReportScheduleType.REPORT:
        dash_slug = definicao.get("dashboard_slug")
        if dash_slug:
            dashboard = (
                sessao.query(Dashboard)
                .filter(Dashboard.slug == dash_slug)
                .first()
            )
            if dashboard:
                report.dashboard_id = dashboard.id
            else:
                logger.warning("Dashboard '%s' não encontrado para relatório '%s'.", dash_slug, nome)

        report.report_format = definicao.get("report_format", "png")

    recipients_defs = definicao.get("recipients", [])
    existing_recipients = {r.recipient_config_json: r for r in (report.recipients or [])}
    new_recipients = []

    for rec_def in recipients_defs:
        rec_type_str = rec_def.get("type", "email")
        rec_type = (
            ReportRecipientType.EMAIL
            if rec_type_str == "email"
            else ReportRecipientType.SLACK
        )
        config = json.dumps({"target": rec_def["target"]}, ensure_ascii=False)

        if config in existing_recipients:
            new_recipients.append(existing_recipients[config])
        else:
            recipient = ReportRecipients(
                type=rec_type,
                recipient_config_json=config,
            )
            new_recipients.append(recipient)

    report.recipients = new_recipients

    sessao.flush()


# ---------------------------------------------------------------------------
# 7. Orquestração
# ---------------------------------------------------------------------------

def provisionar(
    datasets_only: bool = False,
    alertas_only: bool = False,
    cleanup_log: bool = False,
    dry_run: bool = False,
):
    from superset.app import create_app

    app = create_app()

    with app.app_context():
        from superset.extensions import db as superset_db

        sessao = superset_db.session

        skip_conexao = datasets_only or alertas_only
        if not skip_conexao and not cleanup_log:
            conexao = _obter_ou_criar_conexao(sessao)
        else:
            from superset.models.core import Database
            conexao = (
                sessao.query(Database)
                .filter(Database.database_name == NOME_CONEXAO)
                .first()
            )
            if not conexao and not cleanup_log:
                logger.error(
                    "Conexão '%s' não encontrada. Execute sem flags primeiro.",
                    NOME_CONEXAO,
                )
                sys.exit(1)

        if cleanup_log:
            _limpar_action_log(sessao, dry_run=dry_run)
            if not dry_run:
                sessao.commit()
            return

        qtd_datasets = 0
        qtd_alertas = 0

        etapa_geral = not (datasets_only or alertas_only)

        if etapa_geral or datasets_only:
            definicoes = _carregar_yamls(DATASETS_DIR)
            for defn in definicoes:
                _provisionar_dataset(sessao, conexao, defn, dry_run=dry_run)
            if not dry_run:
                _restringir_admin_only(sessao, conexao, definicoes)
            qtd_datasets = len(definicoes)

        if etapa_geral:
            _provisionar_governanca(sessao, conexao, dry_run=dry_run)

        if etapa_geral or alertas_only:
            _provisionar_alertas(sessao, conexao, dry_run=dry_run)
            qtd_alertas = len(_carregar_yamls(ALERTAS_DIR))

        if not dry_run:
            sessao.commit()
            logger.info(
                "Provisionamento concluído: %d datasets, %d alertas/relatórios.",
                qtd_datasets,
                qtd_alertas,
            )
        else:
            logger.info("[DRY-RUN] Nenhuma alteração gravada.")


if __name__ == "__main__":
    args = sys.argv[1:]
    datasets_only = "--datasets-only" in args
    alertas_only = "--alertas-only" in args
    cleanup_log = "--cleanup-log" in args
    dry_run = "--dry-run" in args

    try:
        provisionar(
            datasets_only=datasets_only,
            alertas_only=alertas_only,
            cleanup_log=cleanup_log,
            dry_run=dry_run,
        )
    except Exception as erro:
        logger.error("Erro no provisionamento: %s", erro, exc_info=True)
        sys.exit(1)
