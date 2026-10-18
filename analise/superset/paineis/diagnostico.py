#!/usr/bin/env python3
# Diagnóstico somente leitura: executa uma consulta genérica a partir do form_data de cada
# gráfico dos painéis operacionais, inclusive gráficos criados ou editados à mão no Superset.
# Uso: docker compose exec superset python /app/paineis/diagnostico.py
import json
import sys

SLUGS = ["kpis-operacionais", "assertividade-dupla-analise", "sla-latencia", "disponibilidade-equipamentos",
         "preprocessamento-imagens", "alertas", "infracoes", "passagens-veiculares", "mapa-equipamentos-infracoes"]


def consulta(p, ds):
    metrics = p.get("metrics") or ([p["metric"]] if p.get("metric") else [])
    cols = list(p.get("groupby") or [])
    if p.get("query_mode") == "raw" or (p.get("viz_type") == "table" and p.get("all_columns") and not metrics):
        cols = p.get("all_columns") or cols
    tempo = p.get("granularity_sqla") or p.get("x_axis")
    if tempo and p.get("viz_type", "").startswith(("echarts_timeseries", "echarts_area", "line", "bar")):
        cols = [{"timeGrain": p.get("time_grain_sqla") or "P1D", "columnType": "BASE_AXIS",
                 "sqlExpression": tempo, "label": tempo, "expressionType": "SQL"}] + cols
    sp = p.get("spatial")
    if sp:
        cols = [sp.get("latCol"), sp.get("lonCol")]
    if p.get("line_column"):
        cols = [p["line_column"]]
    return {"metrics": metrics, "columns": cols, "row_limit": 100, "time_range": "No filter", "filters": []}


def main():
    from superset.app import create_app
    app = create_app()
    with app.app_context(), app.test_request_context():
        from flask import g
        from superset.extensions import security_manager, db
        from superset.models.dashboard import Dashboard
        from superset.common.query_context_factory import QueryContextFactory
        from superset.common.chart_data import ChartDataResultFormat, ChartDataResultType
        g.user = security_manager.find_user(username="admin")
        for slug in SLUGS:
            d = db.session.query(Dashboard).filter(Dashboard.slug == slug).first()
            if not d:
                print(f"## {slug}: DASHBOARD AUSENTE")
                continue
            print(f"## {slug} ({len(d.slices)} gráficos)")
            for s in d.slices:
                p = json.loads(s.params or "{}")
                ds = s.datasource
                nome_ds = f"{ds.schema}.{ds.table_name}" if ds else "?"
                try:
                    q = consulta(p, ds)
                    qc = QueryContextFactory().create(datasource={"id": ds.id, "type": "table"}, queries=[q],
                                                      form_data=p, result_type=ChartDataResultType.FULL,
                                                      result_format=ChartDataResultFormat.JSON, force=True)
                    r = qc.get_payload()["queries"][0]
                    erro, n = r.get("error"), r.get("rowcount", 0)
                except Exception as e:  # noqa: BLE001
                    erro, n = str(e)[:160], 0
                status = "ok   " if not erro and n else ("VAZIO" if not erro else "ERRO ")
                print(f"  [{status}] {s.viz_type:28} {s.slice_name[:42]:42} ds={nome_ds:40} linhas={n} {('erro=' + str(erro)[:150]) if erro else ''}")


if __name__ == "__main__":
    main()
