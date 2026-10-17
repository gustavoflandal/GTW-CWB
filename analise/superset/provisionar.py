#!/usr/bin/env python3
# =============================================================================
# provisionar.py — Provisionamento declarativo do Superset (SUP-1 a SUP-4)
#
# Cria a conexão com o banco SQL Server e provisiona datasets, métricas,
# dashboards e gráficos a partir dos YAMLs em /app/datasets/ e /app/dashboards/.
#
# Idempotente: verifica existência antes de criar; atualiza se já existir.
#
# Uso:
#   docker compose exec superset python /app/provisionar.py
#   docker compose exec superset python /app/provisionar.py --datasets-only
#   docker compose exec superset python /app/provisionar.py --dashboards-only
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
DASHBOARDS_DIR = os.environ.get("SUPERSET_DASHBOARDS_DIR", "/app/dashboards")

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
# 4. Provisionamento de dashboards a partir de YAML (SUP-4)
# ---------------------------------------------------------------------------

def _provisionar_dashboards(sessao, conexao, dry_run: bool = False):
    dashboards_yamls = _carregar_yamls(DASHBOARDS_DIR)
    if not dashboards_yamls:
        logger.info("Nenhum YAML de dashboard encontrado em '%s'.", DASHBOARDS_DIR)
        return

    for defn in dashboards_yamls:
        _provisionar_dashboard(sessao, conexao, defn, dry_run=dry_run)


def _provisionar_dashboard(sessao, conexao, definicao: dict, dry_run: bool = False):
    from superset.models.dashboard import Dashboard
    from superset.models.slice import Slice
    from superset.connectors.sqla.models import SqlaTable

    titulo = definicao["dashboard_title"]
    slug = definicao.get("slug", "")
    ds_uuid = definicao.get("uuid")
    arquivo = definicao.get("_arquivo", "?")

    dashboard = (
        sessao.query(Dashboard)
        .filter(Dashboard.slug == slug)
        .first()
    ) if slug else None

    if dry_run:
        acao = "ATUALIZAR" if dashboard else "CRIAR"
        logger.info("[DRY-RUN] %s dashboard '%s' (%s)", acao, titulo, arquivo)
        return

    if not dashboard:
        dashboard = Dashboard(
            dashboard_title=titulo,
            slug=slug,
        )
        if ds_uuid:
            dashboard.uuid = uuid_mod.UUID(ds_uuid)
        sessao.add(dashboard)
        sessao.flush()
        logger.info("Dashboard '%s' criado (id=%d).", titulo, dashboard.id)
    else:
        dashboard.dashboard_title = titulo
        logger.info("Dashboard '%s' já existe (id=%d) — atualizando.", titulo, dashboard.id)

    dashboard.published = definicao.get("published", True)
    dashboard.description = definicao.get("description", "")

    json_meta = _montar_json_metadata(definicao)
    dashboard.json_metadata = json.dumps(json_meta, ensure_ascii=False)

    charts_defs = definicao.get("charts", [])
    slices = []
    for chart_def in charts_defs:
        slice_obj = _provisionar_chart(sessao, conexao, dashboard, chart_def)
        if slice_obj:
            slices.append(slice_obj)

    dashboard.slices = slices

    position = _montar_position_json(definicao, slices)
    dashboard.position_json = json.dumps(position, ensure_ascii=False)

    sessao.flush()


def _montar_json_metadata(definicao: dict) -> dict:
    meta = {
        "timed_refresh_immune_slices": [],
        "expanded_slices": {},
        "refresh_frequency": definicao.get("refresh_frequency", 0),
        "color_scheme": "supersetColors",
        "label_colors": {},
        "shared_label_colors": {},
        "cross_filters_enabled": True,
    }

    filtros = definicao.get("filters", [])
    if filtros:
        native_filters = []
        for i, f in enumerate(filtros):
            filt_id = f"NATIVE_FILTER_{i+1}"
            filt = {
                "id": filt_id,
                "name": f["name"],
                "filterType": _map_filter_type(f.get("type", "select")),
                "targets": [{}] if f.get("targets") == ["__all__"] else f.get("targets", [{}]),
                "defaultDataMask": {},
                "cascadeParentIds": [],
                "scope": {"rootPath": ["ROOT_ID"], "excluded": []},
                "isInstant": True,
            }
            if f.get("type") == "time_range":
                filt["defaultDataMask"] = {
                    "filterState": {"value": f.get("default", "Last 7 days")}
                }
            if f.get("required"):
                filt["controlValues"] = {"enableEmptyFilter": False}
            if f.get("column"):
                filt["targets"] = [{"column": {"name": f["column"]}}]
                if f.get("dataset"):
                    filt["targets"][0]["datasetId"] = f"__ref:{f['dataset']}"

            native_filters.append(filt)

        meta["native_filter_configuration"] = native_filters

    return meta


def _map_filter_type(tipo: str) -> str:
    mapping = {
        "time_range": "filter_time",
        "select": "filter_select",
        "range": "filter_range",
    }
    return mapping.get(tipo, "filter_select")


def _provisionar_chart(sessao, conexao, dashboard, chart_def: dict):
    from superset.models.slice import Slice
    from superset.connectors.sqla.models import SqlaTable

    nome = chart_def["name"]
    viz_type = chart_def["viz_type"]
    dataset_name = chart_def["dataset"]

    dataset = (
        sessao.query(SqlaTable)
        .filter(
            SqlaTable.table_name == dataset_name,
            SqlaTable.database_id == conexao.id,
        )
        .first()
    )

    if not dataset:
        logger.warning("Dataset '%s' não encontrado para gráfico '%s' — ignorando.", dataset_name, nome)
        return None

    slice_obj = (
        sessao.query(Slice)
        .filter(Slice.slice_name == nome, Slice.datasource_id == dataset.id)
        .first()
    )

    if not slice_obj:
        slice_obj = Slice(
            slice_name=nome,
            viz_type=viz_type,
            datasource_id=dataset.id,
            datasource_type="table",
        )
        sessao.add(slice_obj)
        sessao.flush()
        logger.info("  Gráfico '%s' criado (id=%d).", nome, slice_obj.id)
    else:
        slice_obj.viz_type = viz_type
        logger.info("  Gráfico '%s' já existe (id=%d) — atualizando.", nome, slice_obj.id)

    params = _montar_chart_params(chart_def, dataset)
    slice_obj.params = json.dumps(params, ensure_ascii=False)

    sessao.flush()
    return slice_obj


def _montar_chart_params(chart_def: dict, dataset) -> dict:
    params = {
        "datasource": f"{dataset.id}__table",
        "viz_type": chart_def["viz_type"],
    }

    metric = chart_def.get("metric")
    metrics = chart_def.get("metrics", [])
    if metric and not metrics:
        metrics = [metric]
    if metrics:
        params["metrics"] = metrics

    groupby = chart_def.get("groupby", [])
    if groupby:
        params["groupby"] = groupby

    columns = chart_def.get("columns", [])
    if columns:
        params["all_columns"] = columns if chart_def["viz_type"] == "table" else columns
        if chart_def["viz_type"] == "table":
            params["groupby"] = columns

    time_column = chart_def.get("time_column")
    if time_column:
        params["granularity_sqla"] = time_column

    time_grain = chart_def.get("time_grain")
    if time_grain:
        params["time_grain_sqla"] = time_grain

    if chart_def.get("stack"):
        params["stack"] = True

    row_limit = chart_def.get("row_limit")
    if row_limit:
        params["row_limit"] = row_limit

    orderby = chart_def.get("orderby")
    if orderby:
        params["timeseries_limit_metric"] = orderby[0][0] if orderby else None
        params["order_desc"] = orderby[0][1] == "desc" if orderby else True

    y_axis_format = chart_def.get("y_axis_format")
    if y_axis_format:
        params["y_axis_format"] = y_axis_format

    ref_lines = chart_def.get("reference_lines", [])
    if ref_lines:
        params["annotation_layers"] = [
            {
                "annotationType": "FORMULA",
                "name": rl.get("label", ""),
                "value": str(rl["value"]),
                "style": rl.get("style", "dashed"),
                "color": rl.get("color", "#FF4136"),
                "show": True,
            }
            for rl in ref_lines
        ]

    cond_fmt = chart_def.get("conditional_formatting", [])
    if cond_fmt:
        params["conditional_formatting"] = [
            {
                "operator": cf.get("operator", "<"),
                "targetValue": cf.get("value"),
                "column": cf.get("column", ""),
                "colorScheme": cf.get("color", "#FF4136"),
            }
            for cf in cond_fmt
        ]

    spatial = chart_def.get("spatial")
    if spatial:
        params["spatial"] = {
            "latCol": spatial.get("lat", "lat"),
            "lonCol": spatial.get("lon", "lon"),
            "type": "latlong",
        }

    geojson_col = chart_def.get("geojson_column")
    if geojson_col:
        params["line_column"] = geojson_col

    tooltip = chart_def.get("tooltip", [])
    if tooltip:
        params["all_columns_tooltip"] = tooltip

    custom_metrics = chart_def.get("custom_metrics", [])
    if custom_metrics:
        params["metrics"] = [
            {"label": cm["label"], "expressionType": "SQL", "sqlExpression": cm["expression"]}
            for cm in custom_metrics
        ]

    return params


def _montar_position_json(definicao: dict, slices: list) -> dict:
    position = {
        "DASHBOARD_VERSION_KEY": "v2",
        "ROOT_ID": {"type": "ROOT", "id": "ROOT_ID", "children": ["GRID_ID"]},
        "GRID_ID": {
            "type": "GRID",
            "id": "GRID_ID",
            "children": [],
            "parents": ["ROOT_ID"],
        },
        "HEADER_ID": {
            "type": "HEADER",
            "id": "HEADER_ID",
            "meta": {"text": definicao.get("dashboard_title", "")},
        },
    }

    charts_defs = definicao.get("charts", [])
    row_id = 0
    col_offset = 0
    current_row_id = None

    for i, (chart_def, slice_obj) in enumerate(zip(charts_defs, slices)):
        if not slice_obj:
            continue

        width = chart_def.get("width", 6)
        height = chart_def.get("height", 2)

        if col_offset + width > 12 or current_row_id is None:
            row_id += 1
            col_offset = 0
            current_row_id = f"ROW-{row_id}"
            position[current_row_id] = {
                "type": "ROW",
                "id": current_row_id,
                "children": [],
                "parents": ["ROOT_ID", "GRID_ID"],
                "meta": {"background": "BACKGROUND_TRANSPARENT"},
            }
            position["GRID_ID"]["children"].append(current_row_id)

        chart_id = f"CHART-{slice_obj.id}"
        position[chart_id] = {
            "type": "CHART",
            "id": chart_id,
            "children": [],
            "parents": ["ROOT_ID", "GRID_ID", current_row_id],
            "meta": {
                "chartId": slice_obj.id,
                "sliceName": slice_obj.slice_name,
                "width": width,
                "height": height * 16,
            },
        }
        position[current_row_id]["children"].append(chart_id)
        col_offset += width

    return position


# ---------------------------------------------------------------------------
# 5. Orquestração
# ---------------------------------------------------------------------------

def provisionar(
    datasets_only: bool = False,
    dashboards_only: bool = False,
    dry_run: bool = False,
):
    from superset.app import create_app

    app = create_app()

    with app.app_context():
        from superset.extensions import db as superset_db

        sessao = superset_db.session

        skip_conexao = datasets_only or dashboards_only
        if not skip_conexao:
            conexao = _obter_ou_criar_conexao(sessao)
        else:
            from superset.models.core import Database
            conexao = (
                sessao.query(Database)
                .filter(Database.database_name == NOME_CONEXAO)
                .first()
            )
            if not conexao:
                logger.error(
                    "Conexão '%s' não encontrada. Execute sem flags primeiro.",
                    NOME_CONEXAO,
                )
                sys.exit(1)

        qtd_datasets = 0
        qtd_dashboards = 0

        if not dashboards_only:
            definicoes = _carregar_yamls(DATASETS_DIR)
            for defn in definicoes:
                _provisionar_dataset(sessao, conexao, defn, dry_run=dry_run)
            if not dry_run:
                _restringir_admin_only(sessao, conexao, definicoes)
            qtd_datasets = len(definicoes)

        if not datasets_only:
            _provisionar_dashboards(sessao, conexao, dry_run=dry_run)
            qtd_dashboards = len(_carregar_yamls(DASHBOARDS_DIR))

        if not dry_run:
            sessao.commit()
            logger.info(
                "Provisionamento concluído: %d datasets, %d dashboards.",
                qtd_datasets,
                qtd_dashboards,
            )
        else:
            logger.info("[DRY-RUN] Nenhuma alteração gravada.")


if __name__ == "__main__":
    args = sys.argv[1:]
    datasets_only = "--datasets-only" in args
    dashboards_only = "--dashboards-only" in args
    dry_run = "--dry-run" in args

    try:
        provisionar(
            datasets_only=datasets_only,
            dashboards_only=dashboards_only,
            dry_run=dry_run,
        )
    except Exception as erro:
        logger.error("Erro no provisionamento: %s", erro, exc_info=True)
        sys.exit(1)
