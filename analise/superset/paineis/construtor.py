#!/usr/bin/env python3
# =============================================================================
# construtor.py — Construtor dos painéis do Superset (Plano 23)
#
# Cria ou atualiza datasets virtuais (SELECT guardado no Superset, somente leitura no
# GTW_MURALHA_DEV), gráficos com form_data do Superset 6 e dashboards com filtros nativos.
# Ao final executa a consulta de cada gráfico e falha se algum vier sem dados.
#
# Usado por demonstracao.py e operacionais.py; as fontes estão em fontes.py e os SQLs
# em datasets/.
# =============================================================================

import json
import logging
import os
import sys

from fontes import CORES, DATASETS

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger("paineis")

NOME_CONEXAO = "GTW Muralha Digital (somente leitura)"
PAINEIS_DIR = os.path.dirname(os.path.abspath(__file__))
OSM = "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
SEM_FILTRO = "No filter"
PREFIXOS_OBSOLETOS = ("demo_", "op_")

VP_SETE_LAGOAS = {"latitude": -19.4650, "longitude": -44.2470, "zoom": 12.3, "bearing": 0, "pitch": 0}
VP_SALVADOR = {"latitude": -12.9714, "longitude": -38.4800, "zoom": 11, "bearing": 0, "pitch": 0}
VP_BRASIL = {"latitude": -20.5, "longitude": -46.5, "zoom": 4.6, "bearing": 0, "pitch": 0}


def _dttm(ds):
    return DATASETS[ds]["dttm"]


# ---------------------------------------------------------------------------
# Construtores de form_data (Superset 6.0)
# ---------------------------------------------------------------------------
def _filtro_tempo(ds):
    col = _dttm(ds)
    if not col:
        return []
    return [{"clause": "WHERE", "subject": col, "operator": "TEMPORAL_RANGE",
             "comparator": SEM_FILTRO, "expressionType": "SIMPLE"}]


def numero(nome, ds, metrica, subtitulo, formato):
    return {"nome": nome, "ds": ds, "tipo": "numero", "params": {
        "viz_type": "big_number_total", "metric": metrica, "subheader": subtitulo, "subtitle": subtitulo,
        "header_font_size": 0.4, "subheader_font_size": 0.15, "y_axis_format": formato,
        "time_format": "smart_date"}}


def serie(nome, ds, metricas, grao, groupby=None, viz="echarts_timeseries_bar", empilhar=False,
          formato="SMART_NUMBER"):
    params = {
        "viz_type": viz, "x_axis": _dttm(ds), "time_grain_sqla": grao, "metrics": metricas,
        "groupby": groupby or [], "row_limit": 10000, "show_legend": True, "legendOrientation": "top",
        "rich_tooltip": True, "tooltipTimeFormat": "smart_date", "x_axis_time_format": "smart_date",
        "y_axis_format": formato, "truncateXAxis": True, "zoomable": False, "order_desc": True,
        "truncate_metric": True, "show_empty_columns": True,
    }
    if viz == "echarts_timeseries_bar":
        params["stack"] = "Stack" if (empilhar or groupby) else None
    if viz == "echarts_timeseries_line":
        params.update({"markerEnabled": True, "markerSize": 4, "seriesType": "line"})
    if viz == "echarts_area":
        params.update({"stack": "Stack", "opacity": 0.6, "seriesType": "line"})
    return {"nome": nome, "ds": ds, "tipo": "serie", "params": params}


def barras(nome, ds, eixo, metrica, limite=10000, horizontal=False, ordenar_por=None, crescente=None,
           formato="SMART_NUMBER", menores=False):
    ordenar_por = ordenar_por or metrica
    if crescente is None:
        crescente = horizontal
    return {"nome": nome, "ds": ds, "tipo": "barras", "params": {
        "viz_type": "echarts_timeseries_bar", "x_axis": eixo, "metrics": [metrica], "groupby": [],
        "row_limit": limite, "order_desc": not menores, "orientation": "horizontal" if horizontal else "vertical",
        "x_axis_sort": ordenar_por, "x_axis_sort_asc": crescente, "show_legend": False,
        "rich_tooltip": True, "y_axis_format": formato, "show_value": True, "truncate_metric": True,
        "xAxisLabelRotation": 0, "truncateXAxis": True}}


def pizza(nome, ds, coluna, metrica, limite=10):
    return {"nome": nome, "ds": ds, "tipo": "pizza", "params": {
        "viz_type": "pie", "groupby": [coluna], "metric": metrica, "row_limit": limite,
        "sort_by_metric": True, "donut": True, "innerRadius": 40, "outerRadius": 75, "show_labels": True,
        "labels_outside": True, "label_line": True, "label_type": "key_percent", "number_format": "SMART_NUMBER",
        "show_legend": True, "legendOrientation": "bottom", "show_total": True}}


def calor(nome, ds, eixo_x, eixo_y, metrica):
    return {"nome": nome, "ds": ds, "tipo": "calor", "params": {
        "viz_type": "heatmap_v2", "x_axis": eixo_x, "groupby": eixo_y, "metric": metrica,
        "row_limit": 10000, "linear_color_scheme": "blue_white_yellow", "normalize_across": "heatmap",
        "legend_type": "continuous", "sort_x_axis": "alpha_asc", "sort_y_axis": "alpha_asc",
        "xscale_interval": -1, "yscale_interval": -1, "left_margin": "auto", "bottom_margin": "auto",
        "value_bounds": [None, None], "y_axis_format": "SMART_NUMBER", "show_legend": True,
        "show_percentage": True, "show_values": False}}


def tabela(nome, ds, colunas, metricas, limite=50):
    return {"nome": nome, "ds": ds, "tipo": "tabela", "params": {
        "viz_type": "table", "query_mode": "aggregate", "groupby": colunas, "metrics": metricas,
        "timeseries_limit_metric": metricas[0], "order_desc": True, "row_limit": limite,
        "server_pagination": False, "page_length": 10, "include_search": True, "show_cell_bars": True,
        "color_pn": True, "allow_rearrange_columns": False, "percent_metrics": [], "all_columns": [],
        "order_by_cols": [], "table_timestamp_format": "%d/%m/%Y %H:%M"}}


def tabela_registros(nome, ds, colunas, limite=100):
    return {"nome": nome, "ds": ds, "tipo": "registros", "params": {
        "viz_type": "table", "query_mode": "raw", "all_columns": colunas, "groupby": [], "metrics": [],
        "order_by_cols": [json.dumps([_dttm(ds), False])], "row_limit": limite,
        "server_pagination": False, "page_length": 10, "include_search": True, "percent_metrics": [],
        "table_timestamp_format": "%d/%m/%Y %H:%M"}}


def mapa(nome, ds, viewport, metrica=None, dimensao=None, multiplicador=1, autozoom=True,
         cor=(13, 117, 191), grade=False):
    params = {
        "viz_type": "deck_screengrid" if grade else "deck_scatter",
        "spatial": {"type": "latlong", "latCol": "lat", "lonCol": "lon"},
        "mapbox_style": OSM, "row_limit": 5000, "viewport": viewport, "autozoom": autozoom,
        "color_picker": {"r": cor[0], "g": cor[1], "b": cor[2], "a": 0.85}, "groupby": [],
        "time_grain_sqla": None, "granularity_sqla": None,
    }
    if grade:
        params.update({"size": metrica, "grid_size": 25,
                       "point_radius_fixed": {"type": "metric", "value": metrica}})
    elif metrica:
        params.update({"point_radius_fixed": {"type": "metric", "value": metrica}, "size": metrica,
                       "point_unit": "square_m", "multiplier": multiplicador, "min_radius": 4, "max_radius": 45})
    else:
        params.update({"point_radius_fixed": {"type": "fix", "value": 7}, "point_unit": "pixels",
                       "multiplier": 1, "min_radius": 3, "max_radius": 10})
    if dimensao:
        params.update({"dimension": dimensao, "color_scheme": "supersetColors", "legend_position": "tr"})
    return {"nome": nome, "ds": ds, "tipo": "mapa", "params": params, "metrica": metrica, "dimensao": dimensao}


def poligonos(nome, ds, viewport, coluna, metrica):
    return {"nome": nome, "ds": ds, "tipo": "poligono", "coluna": coluna, "params": {
        "viz_type": "deck_polygon", "line_column": coluna, "line_type": "json", "metric": metrica,
        "reverse_long_lat": False, "mapbox_style": OSM, "viewport": viewport, "autozoom": True,
        "filled": True, "stroked": True, "extruded": False, "line_width": 2, "line_width_unit": "pixels",
        "multiplier": 1, "fill_color_picker": {"r": 13, "g": 117, "b": 191, "a": 0.35},
        "stroke_color_picker": {"r": 13, "g": 70, "b": 140, "a": 1}, "row_limit": 5000, "groupby": [],
        "js_columns": [], "js_data_mutator": "", "js_tooltip": "", "js_onclick_href": "",
        "legend_position": "tr", "legend_format": ",d", "time_grain_sqla": None, "granularity_sqla": None}}


def md(texto, altura=12):
    return {"markdown": texto, "altura": altura}


def nota(texto):
    return (md("##### " + texto), 12, 8)


# ---------------------------------------------------------------------------
# Persistência
# ---------------------------------------------------------------------------
def _ler_sql(nome):
    with open(os.path.join(PAINEIS_DIR, "datasets", f"{nome}.sql"), "r", encoding="utf-8") as f:
        return f.read().strip()


def _provisionar_dataset(sessao, conexao, nome, cfg, donos):
    from superset.connectors.sqla.models import SqlaTable, SqlMetric

    ds = sessao.query(SqlaTable).filter(
        SqlaTable.table_name == nome, SqlaTable.database_id == conexao.id).first()
    if not ds:
        ds = SqlaTable(table_name=nome, database=conexao, schema=None)
        sessao.add(ds)
        logger.info("Dataset '%s' criado.", nome)
    else:
        logger.info("Dataset '%s' (id=%s) atualizado.", nome, ds.id)

    ds.sql = _ler_sql(nome)
    ds.description = cfg["descricao"]
    ds.cache_timeout = cfg["cache"]
    ds.owners = donos
    sessao.flush()
    ds.fetch_metadata()

    for col in ds.columns:
        col.verbose_name = cfg["rotulos"].get(col.column_name, col.column_name)
        col.is_dttm = col.column_name == cfg["dttm"]
        col.filterable = True
        col.groupby = col.column_name not in ("lat", "lon", "poligono")
    ds.main_dttm_col = cfg["dttm"]

    existentes = {m.metric_name: m for m in ds.metrics}
    for nome_m, rotulo, expr, fmt in cfg["metricas"]:
        m = existentes.get(nome_m)
        if not m:
            m = SqlMetric(metric_name=nome_m)
            ds.metrics.append(m)
        m.verbose_name = rotulo
        m.expression = expr
        m.d3format = fmt
    sessao.flush()
    return ds


def _provisionar_grafico(sessao, spec, datasets, donos):
    from superset.models.slice import Slice

    ds = datasets[spec["ds"]]
    params = dict(spec["params"])
    params["datasource"] = f"{ds.id}__table"
    params["time_range"] = SEM_FILTRO
    params.setdefault("adhoc_filters", _filtro_tempo(spec["ds"]))

    slc = sessao.query(Slice).filter(Slice.slice_name == spec["nome"], Slice.datasource_id == ds.id).first()
    if not slc:
        slc = Slice(slice_name=spec["nome"], datasource_id=ds.id, datasource_type="table")
        sessao.add(slc)
    slc.viz_type = params["viz_type"]
    slc.params = json.dumps(params, ensure_ascii=False)
    slc.owners = donos
    sessao.flush()
    return slc


def _layout(dash_cfg, graficos):
    pos = {
        "DASHBOARD_VERSION_KEY": "v2",
        "ROOT_ID": {"type": "ROOT", "id": "ROOT_ID", "children": ["GRID_ID"]},
        "GRID_ID": {"type": "GRID", "id": "GRID_ID", "children": [], "parents": ["ROOT_ID"]},
        "HEADER_ID": {"type": "HEADER", "id": "HEADER_ID", "meta": {"text": dash_cfg["titulo"]}},
    }
    i = 0
    for n_linha, linha in enumerate(dash_cfg["linhas"], start=1):
        row_id = f"ROW-p-{n_linha}"
        pos[row_id] = {"type": "ROW", "id": row_id, "children": [], "parents": ["ROOT_ID", "GRID_ID"],
                       "meta": {"background": "BACKGROUND_TRANSPARENT"}}
        pos["GRID_ID"]["children"].append(row_id)
        for comp, largura, altura in linha:
            pais = ["ROOT_ID", "GRID_ID", row_id]
            if "markdown" in comp:
                cid = f"MARKDOWN-p-{n_linha}"
                pos[cid] = {"type": "MARKDOWN", "id": cid, "children": [], "parents": pais,
                            "meta": {"width": largura, "height": altura, "code": comp["markdown"]}}
            else:
                slc = graficos[i]
                i += 1
                cid = f"CHART-p-{slc.id}"
                pos[cid] = {"type": "CHART", "id": cid, "children": [], "parents": pais,
                            "meta": {"chartId": slc.id, "sliceName": slc.slice_name, "uuid": str(slc.uuid),
                                     "width": largura, "height": altura}}
            pos[row_id]["children"].append(cid)
    return pos


def _filtros_nativos(dash_cfg, specs, graficos, datasets):
    filtros = []
    ids = [s.id for s in graficos]
    for n, filtro in enumerate(dash_cfg.get("filtros", []), start=1):
        nome, coluna = filtro[0], filtro[1]
        padrao = filtro[2] if len(filtro) > 2 else None
        fid = f"NATIVE_FILTER-{dash_cfg['slug']}-{n}"
        base = {"id": fid, "name": nome, "type": "NATIVE_FILTER", "description": "", "cascadeParentIds": [],
                "tabsInScope": [], "chartsInScope": ids,
                "scope": {"rootPath": ["ROOT_ID"], "excluded": []}}
        if coluna is None:
            sem_tempo = [g.id for s, g in zip(specs, graficos) if not _dttm(s["ds"])]
            base["scope"]["excluded"] = sem_tempo
            base["chartsInScope"] = [x for x in ids if x not in sem_tempo]
            base.update({"filterType": "filter_time", "targets": [{}], "controlValues": {"enableEmptyFilter": False},
                         "defaultDataMask": {"extraFormData": {}, "filterState": {}, "ownState": {}}})
            filtros.append(base)
            continue
        alvo = next(s["ds"] for s in specs if coluna in datasets[s["ds"]].column_names)
        sem_coluna = [g.id for s, g in zip(specs, graficos) if coluna not in datasets[s["ds"]].column_names]
        base["scope"]["excluded"] = sem_coluna
        base["chartsInScope"] = [x for x in ids if x not in sem_coluna]
        mascara = {"extraFormData": {}, "filterState": {}, "ownState": {}}
        if padrao:
            mascara = {"extraFormData": {"filters": [{"col": coluna, "op": "IN", "val": padrao}]},
                       "filterState": {"value": padrao, "label": ", ".join(padrao)}, "ownState": {}}
        base.update({
            "filterType": "filter_select",
            "targets": [{"datasetId": datasets[alvo].id, "column": {"name": coluna}}],
            "controlValues": {"enableEmptyFilter": False, "defaultToFirstItem": False, "multiSelect": True,
                              "searchAllOptions": False, "inverseSelection": False, "sortAscending": True},
            "defaultDataMask": mascara,
        })
        filtros.append(base)
    return filtros


def _specs(dash_cfg):
    return [c for linha in dash_cfg["linhas"] for c, _, _ in linha if "markdown" not in c]


def _provisionar_dashboard(sessao, dash_cfg, datasets, donos, papeis):
    from superset.models.dashboard import Dashboard

    specs = _specs(dash_cfg)
    graficos = [_provisionar_grafico(sessao, s, datasets, donos) for s in specs]

    dash = sessao.query(Dashboard).filter(Dashboard.slug == dash_cfg["slug"]).first()
    if not dash:
        dash = Dashboard(slug=dash_cfg["slug"])
        sessao.add(dash)
    dash.dashboard_title = dash_cfg["titulo"]
    dash.description = dash_cfg["descricao"]
    dash.published = True
    dash.owners = donos
    dash.roles = papeis
    antigos = [s for s in (dash.slices or []) if s not in graficos]
    dash.slices = graficos
    sessao.flush()
    for s in antigos:
        if not s.dashboards:
            logger.info("  Gráfico antigo '%s' (id=%s) removido.", s.slice_name, s.id)
            sessao.delete(s)
    dash.position_json = json.dumps(_layout(dash_cfg, graficos), ensure_ascii=False)
    dash.json_metadata = json.dumps({
        "color_scheme": "supersetColors", "label_colors": CORES, "shared_label_colors": [],
        "cross_filters_enabled": True, "refresh_frequency": 0, "timed_refresh_immune_slices": [],
        "expanded_slices": {}, "chart_configuration": {},
        "global_chart_configuration": {"scope": {"rootPath": ["ROOT_ID"], "excluded": []},
                                       "chartsInScope": [g.id for g in graficos]},
        "native_filter_configuration": _filtros_nativos(dash_cfg, specs, graficos, datasets),
    }, ensure_ascii=False)
    sessao.flush()
    logger.info("Dashboard '%s' (/superset/dashboard/%s/) com %d gráficos.",
                dash_cfg["titulo"], dash_cfg["slug"], len(graficos))
    return specs, graficos


def _remover_obsoletos(sessao, conexao):
    """Remove datasets virtuais de versões anteriores que não têm mais gráficos."""
    from superset.connectors.sqla.models import SqlaTable
    from superset.models.slice import Slice

    for ds in sessao.query(SqlaTable).filter(SqlaTable.database_id == conexao.id, SqlaTable.sql.isnot(None)).all():
        if not ds.table_name.startswith(PREFIXOS_OBSOLETOS):
            continue
        orfaos = sessao.query(Slice).filter(Slice.datasource_id == ds.id, Slice.datasource_type == "table").all()
        for s in orfaos:
            if not s.dashboards:
                sessao.delete(s)
        sessao.flush()
        restantes = sessao.query(Slice).filter(Slice.datasource_id == ds.id, Slice.datasource_type == "table").count()
        if restantes == 0:
            logger.info("Dataset obsoleto '%s' (id=%s) removido.", ds.table_name, ds.id)
            sessao.delete(ds)
    sessao.flush()


# ---------------------------------------------------------------------------
# Validação: executa a consulta de cada gráfico
# ---------------------------------------------------------------------------
def _consulta(spec):
    p = spec["params"]
    dttm = _dttm(spec["ds"])
    q = {"filters": [{"col": dttm, "op": "TEMPORAL_RANGE", "val": SEM_FILTRO}] if dttm else [],
         "extras": {}, "row_limit": p.get("row_limit", 1000), "time_range": SEM_FILTRO}
    tipo = spec["tipo"]
    if tipo == "numero":
        q.update(metrics=[p["metric"]], columns=[])
    elif tipo == "serie":
        eixo = {"timeGrain": p["time_grain_sqla"], "columnType": "BASE_AXIS", "sqlExpression": p["x_axis"],
                "label": p["x_axis"], "expressionType": "SQL"}
        q.update(metrics=p["metrics"], columns=[eixo] + p["groupby"], orderby=[[p["metrics"][0], False]])
    elif tipo == "barras":
        q.update(metrics=p["metrics"], columns=[p["x_axis"]], orderby=[[p["metrics"][0], not p["order_desc"]]])
    elif tipo == "pizza":
        q.update(metrics=[p["metric"]], columns=p["groupby"], orderby=[[p["metric"], False]])
    elif tipo == "calor":
        q.update(metrics=[p["metric"]], columns=[p["x_axis"], p["groupby"]])
    elif tipo == "tabela":
        q.update(metrics=p["metrics"], columns=p["groupby"], orderby=[[p["metrics"][0], False]])
    elif tipo == "registros":
        q.update(metrics=[], columns=p["all_columns"], orderby=[[dttm, False]])
    elif tipo == "mapa":
        if spec["metrica"]:
            q.update(metrics=[spec["metrica"]], columns=["lat", "lon"])
        else:
            q.update(metrics=[], columns=["lat", "lon"] + ([spec["dimensao"]] if spec["dimensao"] else []))
    elif tipo == "poligono":
        q.update(metrics=[p["metric"]], columns=[spec["coluna"]])
    return q


def _validar(resultados, usuario):
    from flask import g
    from superset.common.chart_data import ChartDataResultFormat, ChartDataResultType
    from superset.common.query_context_factory import QueryContextFactory

    g.user = usuario
    falhas = 0
    for titulo, specs, graficos, datasets in resultados:
        logger.info("Validando '%s'...", titulo)
        for spec, slc in zip(specs, graficos):
            ds = datasets[spec["ds"]]
            try:
                qc = QueryContextFactory().create(
                    datasource={"id": ds.id, "type": "table"}, queries=[_consulta(spec)],
                    form_data=json.loads(slc.params), result_type=ChartDataResultType.FULL,
                    result_format=ChartDataResultFormat.JSON, force=True)
                res = qc.get_payload()["queries"][0]
                erro = res.get("error")
                linhas = res.get("rowcount", 0)
            except Exception as exc:  # noqa: BLE001
                erro, linhas = str(exc), 0
            if erro or not linhas:
                falhas += 1
                logger.error("  [FALHA] %-48s linhas=%s erro=%s", slc.slice_name, linhas, erro)
            else:
                logger.info("  [ok]    %-48s linhas=%s", slc.slice_name, linhas)
    return falhas


def executar(dashboards, somente_validar=False):
    from superset.app import create_app

    app = create_app()
    with app.app_context(), app.test_request_context():
        from flask_appbuilder.security.sqla.models import Role
        from superset.connectors.sqla.models import SqlaTable
        from superset.extensions import db, security_manager
        from superset.models.core import Database
        from superset.models.dashboard import Dashboard

        sessao = db.session
        conexao = sessao.query(Database).filter(Database.database_name == NOME_CONEXAO).first()
        if not conexao:
            logger.error("Conexão '%s' não encontrada — execute antes /app/provisionar.py.", NOME_CONEXAO)
            sys.exit(1)

        admin = security_manager.find_user(username=os.environ.get("SUPERSET_ADMIN_USERNAME", "admin"))
        donos = [admin] if admin else []
        papeis = sessao.query(Role).filter(Role.name.in_(["Admin", "Alpha", "Gamma"])).all()
        usados = sorted({s["ds"] for cfg in dashboards for s in _specs(cfg)})

        if somente_validar:
            datasets = {n: sessao.query(SqlaTable).filter(SqlaTable.table_name == n,
                                                          SqlaTable.database_id == conexao.id).one()
                        for n in usados}
            resultados = []
            for cfg in dashboards:
                dash = sessao.query(Dashboard).filter(Dashboard.slug == cfg["slug"]).one()
                por_nome = {s.slice_name: s for s in dash.slices}
                specs = _specs(cfg)
                resultados.append((cfg["titulo"], specs, [por_nome[s["nome"]] for s in specs], datasets))
        else:
            datasets = {n: _provisionar_dataset(sessao, conexao, n, DATASETS[n], donos) for n in usados}
            resultados = []
            for cfg in dashboards:
                specs, graficos = _provisionar_dashboard(sessao, cfg, datasets, donos, papeis)
                resultados.append((cfg["titulo"], specs, graficos, datasets))
            _remover_obsoletos(sessao, conexao)
            sessao.commit()
            logger.info("Datasets, gráficos e dashboards gravados.")

        falhas = _validar(resultados, admin)
        if falhas:
            logger.error("%d gráfico(s) sem dados ou com erro.", falhas)
            sys.exit(2)
        logger.info("Todos os %d gráficos retornaram dados.", sum(len(r[1]) for r in resultados))
