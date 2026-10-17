#!/usr/bin/env python3
# =============================================================================
# demo_dashboards.py — Painéis de demonstração com dados reais do GTW
#
# Cria (ou atualiza) datasets virtuais somente leitura sobre o GTW_MURALHA_DEV,
# os gráficos e cinco dashboards de demonstração. Ao final executa a consulta
# de cada gráfico e informa quantas linhas retornou.
#
# Uso:
#   docker compose exec superset python /app/demo/demo_dashboards.py
#   docker compose exec superset python /app/demo/demo_dashboards.py --validar
# =============================================================================

import json
import logging
import os
import sys

logging.basicConfig(level=logging.INFO, format="%(asctime)s [%(levelname)s] %(message)s")
logger = logging.getLogger("demo")

NOME_CONEXAO = "GTW Muralha Digital (somente leitura)"
DEMO_DIR = os.environ.get("SUPERSET_DEMO_DIR", "/app/demo")
OSM = "https://tile.openstreetmap.org/{z}/{x}/{y}.png"
SEM_FILTRO = "No filter"

# ---------------------------------------------------------------------------
# Datasets virtuais
# ---------------------------------------------------------------------------
DATASETS = {
    "demo_passagens_hora": {
        "descricao": "Passagens veiculares agregadas por hora, últimos 30 dias com dados (muralha.veiculo_tempo_real).",
        "dttm": "data_hora",
        "cache": 3600,
        "rotulos": {
            "data_hora": "Data/hora", "hora_dia": "Hora do dia", "dia_semana": "Dia da semana",
            "equipamento": "Equipamento", "classificacao": "Classificação", "tipo_placa": "Tipo de placa",
            "passagens": "Passagens", "soma_velocidade": "Soma das velocidades", "acima_60kmh": "Acima de 60 km/h",
        },
        "metricas": [
            ("passagens", "Passagens", "SUM(passagens)", ",d"),
            ("velocidade_media", "Velocidade média (km/h)",
             "SUM(CAST(soma_velocidade AS float)) / NULLIF(SUM(CAST(passagens AS float)), 0)", ",.1f"),
            ("pct_mercosul", "% placas Mercosul",
             "SUM(CASE WHEN tipo_placa = N'Mercosul' THEN CAST(passagens AS float) ELSE 0 END) / NULLIF(SUM(CAST(passagens AS float)), 0)", ".1%"),
            ("pct_acima_60", "% acima de 60 km/h",
             "SUM(CAST(acima_60kmh AS float)) / NULLIF(SUM(CAST(passagens AS float)), 0)", ".1%"),
        ],
    },
    "demo_infracoes": {
        "descricao": "Infrações registradas pelos equipamentos (dbo.infracao), sem equipamentos de teste.",
        "dttm": "data_infracao",
        "cache": 1800,
        "rotulos": {
            "data_infracao": "Data da infração", "hora_dia": "Hora do dia", "dia_semana": "Dia da semana",
            "equipamento": "Equipamento", "faixa": "Faixa", "tipo_infracao": "Tipo de infração",
            "categoria": "Categoria", "enquadramento": "Enquadramento", "velocidade_limite": "Limite (km/h)",
            "velocidade_considerada": "Velocidade considerada (km/h)", "excesso_kmh": "Excesso (km/h)",
            "faixa_excesso": "Faixa de excesso",
        },
        "metricas": [
            ("infracoes", "Infrações", "COUNT(*)", ",d"),
            ("excesso_medio", "Excesso médio (km/h)", "AVG(CAST(excesso_kmh AS float))", ",.1f"),
            ("pct_velocidade", "% excesso de velocidade",
             "SUM(CASE WHEN categoria = N'Excesso de velocidade' THEN 1.0 ELSE 0 END) / NULLIF(COUNT(*), 0)", ".1%"),
            ("equipamentos", "Equipamentos", "COUNT(DISTINCT equipamento)", ",d"),
        ],
    },
    "demo_trafego_dia": {
        "descricao": "Tráfego diário por equipamento e classe (dbo.veiculo_sumarizado), série histórica.",
        "dttm": "dia",
        "cache": 3600,
        "rotulos": {
            "dia": "Dia", "equipamento": "Equipamento", "classe": "Classe do veículo", "trafego": "Tráfego",
            "soma_velocidade": "Soma ponderada das velocidades", "placas_lidas": "Placas lidas",
            "lat": "Latitude", "lon": "Longitude",
        },
        "metricas": [
            ("volume", "Volume de veículos", "SUM(CAST(trafego AS bigint))", ",d"),
            ("media_diaria", "Média diária", "SUM(CAST(trafego AS float)) / NULLIF(COUNT(DISTINCT dia), 0)", ",.0f"),
            ("velocidade_media", "Velocidade média (km/h)",
             "SUM(soma_velocidade) / NULLIF(SUM(CAST(trafego AS float)), 0)", ",.1f"),
            ("taxa_leitura", "Taxa de leitura de placas",
             "SUM(CAST(placas_lidas AS float)) / NULLIF(SUM(CAST(trafego AS float)), 0)", ".1%"),
            ("equipamentos", "Equipamentos", "COUNT(DISTINCT equipamento)", ",d"),
        ],
    },
    "demo_faixa_velocidade": {
        "descricao": "Tráfego mensal por faixa de velocidade e classe (dbo.veiculo_sumarizado).",
        "dttm": "mes",
        "cache": 3600,
        "rotulos": {"mes": "Mês", "faixa_velocidade": "Faixa de velocidade", "classe": "Classe do veículo",
                    "trafego": "Tráfego"},
        "metricas": [("volume", "Volume de veículos", "SUM(CAST(trafego AS bigint))", ",d")],
    },
    "demo_waze_alertas": {
        "descricao": "Alertas de mobilidade reportados no Waze (mobilidade.alertas).",
        "dttm": "data_hora",
        "cache": 900,
        "rotulos": {
            "data_hora": "Data/hora", "hora_dia": "Hora do dia", "dia_semana": "Dia da semana",
            "cidade": "Cidade", "via": "Via", "tipo": "Tipo", "subtipo": "Subtipo",
            "confiabilidade": "Confiabilidade", "confirmacoes": "Confirmações", "lat": "Latitude", "lon": "Longitude",
        },
        "metricas": [
            ("alertas", "Alertas", "COUNT(*)", ",d"),
            ("acidentes", "Acidentes", "SUM(CASE WHEN tipo = N'Acidente' THEN 1 ELSE 0 END)", ",d"),
            ("congestionamentos", "Congestionamentos", "SUM(CASE WHEN tipo = N'Congestionamento' THEN 1 ELSE 0 END)", ",d"),
            ("interdicoes", "Vias interditadas", "SUM(CASE WHEN tipo = N'Via interditada' THEN 1 ELSE 0 END)", ",d"),
        ],
    },
    "demo_alertas_veiculos": {
        "descricao": "Alertas de veículos de interesse gerados pela Muralha Digital (analise.v_alertas).",
        "dttm": "dt_alerta",
        "cache": 900,
        "rotulos": {
            "dt_alerta": "Data do alerta", "hora_dia": "Hora do dia", "dia_semana": "Dia da semana",
            "tipo_alerta": "Tipo de alerta", "criticidade": "Criticidade", "status": "Status", "origem": "Origem",
            "descartado": "Descartado", "tratado": "Tratado", "responsavel": "Responsável",
            "motivo_descarte": "Motivo do descarte",
        },
        "metricas": [
            ("alertas", "Alertas", "COUNT(*)", ",d"),
            ("criticos", "Alertas críticos", "SUM(CASE WHEN criticidade = N'Crítico' THEN 1 ELSE 0 END)", ",d"),
            ("pct_tratados", "% tratados", "SUM(CAST(tratado AS float)) / NULLIF(COUNT(*), 0)", ".1%"),
            ("descartados", "Descartados", "SUM(descartado)", ",d"),
        ],
    },
}

CORES = {
    "Acidente": "#E04355", "Congestionamento": "#FCC700", "Perigo na via": "#FF7F44",
    "Via interditada": "#5AC189", "Presença policial": "#1FA8C9",
    "Crítico": "#E04355", "Não crítico": "#1FA8C9",
    "Excesso de velocidade": "#E04355", "Circulação e sinalização": "#1FA8C9",
    "Mercosul": "#0D75BF", "Modelo antigo": "#A0A0A0",
}


# ---------------------------------------------------------------------------
# Construtores de form_data (Superset 6.0)
# ---------------------------------------------------------------------------
def _filtro_tempo(dttm):
    return [{"clause": "WHERE", "subject": dttm, "operator": "TEMPORAL_RANGE",
             "comparator": SEM_FILTRO, "expressionType": "SIMPLE"}]


def numero(nome, ds, metrica, subtitulo, formato):
    return {"nome": nome, "ds": ds, "tipo": "numero", "params": {
        "viz_type": "big_number_total", "metric": metrica, "subheader": subtitulo, "subtitle": subtitulo,
        "header_font_size": 0.4, "subheader_font_size": 0.15, "y_axis_format": formato,
        "time_format": "smart_date"}}


def serie(nome, ds, metricas, grao, groupby=None, viz="echarts_timeseries_bar", empilhar=True, formato="SMART_NUMBER"):
    params = {
        "viz_type": viz, "x_axis": DATASETS[ds]["dttm"], "time_grain_sqla": grao, "metrics": metricas,
        "groupby": groupby or [], "row_limit": 10000, "show_legend": True, "legendOrientation": "top",
        "rich_tooltip": True, "tooltipTimeFormat": "smart_date", "x_axis_time_format": "smart_date",
        "y_axis_format": formato, "truncateXAxis": True, "zoomable": False, "order_desc": True,
        "truncate_metric": True, "show_empty_columns": True,
    }
    if viz == "echarts_timeseries_bar":
        params["stack"] = "Stack" if empilhar and groupby else None
    if viz == "echarts_timeseries_line":
        params.update({"markerEnabled": True, "markerSize": 4, "seriesType": "line"})
    if viz == "echarts_area":
        params.update({"stack": "Stack", "opacity": 0.6, "seriesType": "line"})
    return {"nome": nome, "ds": ds, "tipo": "serie", "params": params}


def barras(nome, ds, eixo, metrica, limite=10000, horizontal=False, ordenar_por=None, crescente=None,
           formato="SMART_NUMBER"):
    ordenar_por = ordenar_por or metrica
    if crescente is None:
        crescente = horizontal
    return {"nome": nome, "ds": ds, "tipo": "barras", "params": {
        "viz_type": "echarts_timeseries_bar", "x_axis": eixo, "metrics": [metrica], "groupby": [],
        "row_limit": limite, "order_desc": True, "orientation": "horizontal" if horizontal else "vertical",
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
        "order_by_cols": [json.dumps([DATASETS[ds]["dttm"], False])], "row_limit": limite,
        "server_pagination": False, "page_length": 10, "include_search": True, "percent_metrics": [],
        "table_timestamp_format": "%d/%m/%Y %H:%M"}}


def mapa(nome, ds, viewport, metrica=None, dimensao=None, multiplicador=1, autozoom=True):
    params = {
        "viz_type": "deck_scatter", "spatial": {"type": "latlong", "latCol": "lat", "lonCol": "lon"},
        "mapbox_style": OSM, "row_limit": 5000, "viewport": viewport, "autozoom": autozoom,
        "color_picker": {"r": 13, "g": 117, "b": 191, "a": 0.8}, "groupby": [],
        "time_grain_sqla": None, "granularity_sqla": None,
    }
    if metrica:
        params.update({"point_radius_fixed": {"type": "metric", "value": metrica}, "size": metrica,
                       "point_unit": "square_m", "multiplier": multiplicador, "min_radius": 4, "max_radius": 45})
    else:
        params.update({"point_radius_fixed": {"type": "fix", "value": 6}, "point_unit": "pixels",
                       "multiplier": 1, "min_radius": 3, "max_radius": 10})
    if dimensao:
        params.update({"dimension": dimensao, "color_scheme": "supersetColors", "legend_position": "tr"})
    return {"nome": nome, "ds": ds, "tipo": "mapa", "params": params, "metrica": metrica, "dimensao": dimensao}


def md(texto, altura=12):
    return {"markdown": texto, "altura": altura}


VP_SETE_LAGOAS = {"latitude": -19.4650, "longitude": -44.2470, "zoom": 12.3, "bearing": 0, "pitch": 0}
VP_SALVADOR = {"latitude": -12.9714, "longitude": -38.4800, "zoom": 11, "bearing": 0, "pitch": 0}

FONTE = ("Consulta ao vivo ao banco **GTW_MURALHA_DEV** (somente leitura, cache de {cache}). "
         "Clique em uma barra ou fatia para aplicar filtro cruzado; use a barra de filtros à esquerda.")

# ---------------------------------------------------------------------------
# Dashboards — cada linha: lista de (componente, largura, altura)
# ---------------------------------------------------------------------------
DASHBOARDS = [
    {
        "slug": "demo-fluxo-veicular",
        "titulo": "Demonstração — Fluxo Veicular",
        "descricao": "Passagens capturadas pelos equipamentos nos últimos 30 dias com dados.",
        "filtros": [("Período", None), ("Equipamento", "equipamento"), ("Classificação", "classificacao"),
                    ("Tipo de placa", "tipo_placa")],
        "linhas": [
            [(md("### Fluxo veicular — últimos 30 dias com dados\nFonte: `muralha.veiculo_tempo_real` "
                 "agregada por hora. " + FONTE.format(cache="1 h")), 12, 12)],
            [(numero("Passagens no período", "demo_passagens_hora", "passagens", "veículos capturados", ",d"), 3, 22),
             (numero("Velocidade média", "demo_passagens_hora", "velocidade_media", "km/h", ",.1f"), 3, 22),
             (numero("Placas Mercosul", "demo_passagens_hora", "pct_mercosul", "das passagens", ".1%"), 3, 22),
             (numero("Acima de 60 km/h", "demo_passagens_hora", "pct_acima_60", "das passagens", ".1%"), 3, 22)],
            [(serie("Passagens por dia e classificação", "demo_passagens_hora", ["passagens"], "P1D",
                    groupby=["classificacao"]), 8, 50),
             (pizza("Distribuição por classificação", "demo_passagens_hora", "classificacao", "passagens"), 4, 50)],
            [(calor("Mapa de calor: dia da semana x hora", "demo_passagens_hora", "hora_dia", "dia_semana",
                    "passagens"), 8, 50),
             (pizza("Tipo de placa", "demo_passagens_hora", "tipo_placa", "passagens"), 4, 50)],
            [(barras("Perfil horário — passagens", "demo_passagens_hora", "hora_dia", "passagens",
                     ordenar_por="hora_dia", crescente=True), 6, 45),
             (barras("Perfil horário — velocidade média (km/h)", "demo_passagens_hora", "hora_dia",
                     "velocidade_media", ordenar_por="hora_dia", crescente=True, formato=",.1f"), 6, 45)],
            [(tabela("Resumo por equipamento", "demo_passagens_hora", ["equipamento"],
                     ["passagens", "velocidade_media", "pct_mercosul", "pct_acima_60"]), 12, 40)],
        ],
    },
    {
        "slug": "demo-infracoes",
        "titulo": "Demonstração — Fiscalização e Infrações",
        "descricao": "Infrações registradas pelos equipamentos de fiscalização eletrônica.",
        "filtros": [("Período", None), ("Equipamento", "equipamento"), ("Categoria", "categoria"),
                    ("Tipo de infração", "tipo_infracao")],
        "linhas": [
            [(md("### Fiscalização eletrônica\nFonte: `dbo.infracao` + `dbo.enquadramento` + `dbo.local` "
                 "(equipamentos de teste excluídos). " + FONTE.format(cache="30 min")), 12, 12)],
            [(numero("Infrações registradas", "demo_infracoes", "infracoes", "no período", ",d"), 3, 22),
             (numero("Excesso médio", "demo_infracoes", "excesso_medio", "km/h acima do limite", ",.1f"), 3, 22),
             (numero("Infrações de velocidade", "demo_infracoes", "pct_velocidade", "do total", ".1%"), 3, 22),
             (numero("Equipamentos com registro", "demo_infracoes", "equipamentos", "pontos de fiscalização", ",d"), 3, 22)],
            [(serie("Infrações por mês e categoria", "demo_infracoes", ["infracoes"], "P1M",
                    groupby=["categoria"]), 8, 50),
             (pizza("Infrações por tipo", "demo_infracoes", "tipo_infracao", "infracoes"), 4, 50)],
            [(barras("Infrações por equipamento", "demo_infracoes", "equipamento", "infracoes", horizontal=True), 6, 45),
             (barras("Distribuição do excesso de velocidade", "demo_infracoes", "faixa_excesso", "infracoes",
                     ordenar_por="faixa_excesso", crescente=True), 6, 45)],
            [(calor("Mapa de calor: dia da semana x hora", "demo_infracoes", "hora_dia", "dia_semana",
                    "infracoes"), 8, 50),
             (barras("Infrações por faixa", "demo_infracoes", "faixa", "infracoes", ordenar_por="faixa",
                     crescente=True), 4, 50)],
            [(tabela("Ranking de enquadramentos", "demo_infracoes", ["enquadramento"],
                     ["infracoes", "excesso_medio"]), 12, 40)],
        ],
    },
    {
        "slug": "demo-trafego-historico",
        "titulo": "Demonstração — Tráfego Histórico",
        "descricao": "Série histórica de volume, velocidade e leitura de placas por equipamento.",
        "filtros": [("Período", None), ("Equipamento", "equipamento"), ("Classe do veículo", "classe")],
        "linhas": [
            [(md("### Tráfego histórico\nFonte: `dbo.veiculo_sumarizado` (dados consolidados por dia, equipamento, "
                 "classe e faixa de velocidade). " + FONTE.format(cache="1 h")), 12, 12)],
            [(numero("Volume total", "demo_trafego_dia", "volume", "veículos contados", ",d"), 3, 22),
             (numero("Média diária", "demo_trafego_dia", "media_diaria", "veículos por dia", ",.0f"), 3, 22),
             (numero("Velocidade média", "demo_trafego_dia", "velocidade_media", "km/h", ",.1f"), 3, 22),
             (numero("Leitura de placas", "demo_trafego_dia", "taxa_leitura", "dos veículos", ".1%"), 3, 22)],
            [(serie("Volume mensal por classe", "demo_trafego_dia", ["volume"], "P1M", groupby=["classe"],
                    viz="echarts_area"), 8, 50),
             (pizza("Composição da frota", "demo_trafego_dia", "classe", "volume"), 4, 50)],
            [(barras("Volume por equipamento (top 15)", "demo_trafego_dia", "equipamento", "volume", limite=15,
                     horizontal=True), 6, 55),
             (mapa("Localização e volume dos equipamentos", "demo_trafego_dia", VP_SETE_LAGOAS, metrica="volume",
                   multiplicador=0.3, autozoom=False), 6, 55)],
            [(serie("Velocidade média mensal (km/h)", "demo_trafego_dia", ["velocidade_media"], "P1M",
                    viz="echarts_timeseries_line", formato=",.1f"), 6, 45),
             (barras("Distribuição por faixa de velocidade", "demo_faixa_velocidade", "faixa_velocidade", "volume",
                     ordenar_por="faixa_velocidade", crescente=True), 6, 45)],
            [(tabela("Resumo por equipamento", "demo_trafego_dia", ["equipamento"],
                     ["volume", "media_diaria", "velocidade_media", "taxa_leitura"]), 12, 40)],
        ],
    },
    {
        "slug": "demo-mobilidade-waze",
        "titulo": "Demonstração — Mobilidade Urbana (Waze)",
        "descricao": "Ocorrências de trânsito reportadas por usuários do Waze.",
        "filtros": [("Período", None), ("Cidade", "cidade", ["Salvador"]), ("Tipo", "tipo"), ("Via", "via")],
        "linhas": [
            [(md("### Mobilidade urbana — alertas do Waze\nFonte: `mobilidade.alertas`. O filtro **Cidade** inicia em "
                 "Salvador; limpe-o para ver todas as cidades. " + FONTE.format(cache="15 min")), 12, 12)],
            [(numero("Alertas reportados", "demo_waze_alertas", "alertas", "no período", ",d"), 3, 22),
             (numero("Acidentes", "demo_waze_alertas", "acidentes", "ocorrências", ",d"), 3, 22),
             (numero("Congestionamentos", "demo_waze_alertas", "congestionamentos", "ocorrências", ",d"), 3, 22),
             (numero("Vias interditadas", "demo_waze_alertas", "interdicoes", "ocorrências", ",d"), 3, 22)],
            [(mapa("Mapa de ocorrências", "demo_waze_alertas", VP_SALVADOR, dimensao="tipo"), 8, 60),
             (pizza("Alertas por tipo", "demo_waze_alertas", "tipo", "alertas"), 4, 60)],
            [(serie("Alertas por semana e tipo", "demo_waze_alertas", ["alertas"], "P1W", groupby=["tipo"]), 8, 50),
             (barras("Principais subtipos", "demo_waze_alertas", "subtipo", "alertas", limite=10,
                     horizontal=True), 4, 50)],
            [(barras("Vias com mais ocorrências (top 15)", "demo_waze_alertas", "via", "alertas", limite=15,
                     horizontal=True), 6, 55),
             (calor("Mapa de calor: dia da semana x hora", "demo_waze_alertas", "hora_dia", "dia_semana",
                    "alertas"), 6, 55)],
            [(tabela_registros("Ocorrências mais recentes", "demo_waze_alertas",
                               ["data_hora", "cidade", "via", "tipo", "subtipo", "confiabilidade"]), 12, 45)],
        ],
    },
    {
        "slug": "demo-alertas-veiculos",
        "titulo": "Demonstração — Alertas de Veículos de Interesse",
        "descricao": "Alertas gerados pela Muralha Digital para veículos monitorados, furtados, roubados e clonados.",
        "filtros": [("Período", None), ("Tipo de alerta", "tipo_alerta"), ("Criticidade", "criticidade"),
                    ("Status", "status")],
        "linhas": [
            [(md("### Alertas de veículos de interesse\nFonte: `analise.v_alertas` (tabela `muralha.alerta`). "
                 + FONTE.format(cache="15 min")), 12, 12)],
            [(numero("Alertas gerados", "demo_alertas_veiculos", "alertas", "no período", ",d"), 3, 22),
             (numero("Alertas críticos", "demo_alertas_veiculos", "criticos", "furto, roubo e sequestro", ",d"), 3, 22),
             (numero("Alertas tratados", "demo_alertas_veiculos", "pct_tratados", "com responsável atribuído", ".1%"), 3, 22),
             (numero("Alertas descartados", "demo_alertas_veiculos", "descartados", "após análise", ",d"), 3, 22)],
            [(serie("Alertas por mês e tipo", "demo_alertas_veiculos", ["alertas"], "P1M", groupby=["tipo_alerta"]), 8, 50),
             (pizza("Alertas por tipo", "demo_alertas_veiculos", "tipo_alerta", "alertas"), 4, 50)],
            [(calor("Mapa de calor: dia da semana x hora", "demo_alertas_veiculos", "hora_dia", "dia_semana",
                    "alertas"), 8, 50),
             (pizza("Criticidade", "demo_alertas_veiculos", "criticidade", "alertas"), 4, 50)],
            [(barras("Alertas por status", "demo_alertas_veiculos", "status", "alertas", horizontal=True), 6, 40),
             (tabela("Tipos de alerta — resumo", "demo_alertas_veiculos", ["tipo_alerta", "criticidade"],
                     ["alertas", "pct_tratados", "descartados"]), 6, 40)],
        ],
    },
]


# ---------------------------------------------------------------------------
# Persistência
# ---------------------------------------------------------------------------
def _ler_sql(nome):
    with open(os.path.join(DEMO_DIR, "datasets", f"{nome}.sql"), "r", encoding="utf-8") as f:
        return f.read().strip()


def _provisionar_dataset(sessao, conexao, nome, cfg, donos):
    from superset.connectors.sqla.models import SqlaTable, SqlMetric

    ds = sessao.query(SqlaTable).filter(
        SqlaTable.table_name == nome, SqlaTable.database_id == conexao.id).first()
    if not ds:
        ds = SqlaTable(table_name=nome, database=conexao, schema=None)
        sessao.add(ds)
        logger.info("Dataset virtual '%s' criado.", nome)
    else:
        logger.info("Dataset virtual '%s' já existe (id=%s) — atualizando.", nome, ds.id)

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
        col.groupby = col.column_name not in ("lat", "lon")
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
    params.setdefault("adhoc_filters", _filtro_tempo(DATASETS[spec["ds"]]["dttm"]))

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
        row_id = f"ROW-demo-{n_linha}"
        pos[row_id] = {"type": "ROW", "id": row_id, "children": [], "parents": ["ROOT_ID", "GRID_ID"],
                       "meta": {"background": "BACKGROUND_TRANSPARENT"}}
        pos["GRID_ID"]["children"].append(row_id)
        for comp, largura, altura in linha:
            pais = ["ROOT_ID", "GRID_ID", row_id]
            if "markdown" in comp:
                cid = f"MARKDOWN-demo-{n_linha}"
                pos[cid] = {"type": "MARKDOWN", "id": cid, "children": [], "parents": pais,
                            "meta": {"width": largura, "height": altura, "code": comp["markdown"]}}
            else:
                slc = graficos[i]
                i += 1
                cid = f"CHART-demo-{slc.id}"
                pos[cid] = {"type": "CHART", "id": cid, "children": [], "parents": pais,
                            "meta": {"chartId": slc.id, "sliceName": slc.slice_name, "uuid": str(slc.uuid),
                                     "width": largura, "height": altura}}
            pos[row_id]["children"].append(cid)
    return pos


def _filtros_nativos(dash_cfg, specs, graficos, datasets):
    filtros = []
    ids = [s.id for s in graficos]
    for n, filtro in enumerate(dash_cfg["filtros"], start=1):
        nome, coluna = filtro[0], filtro[1]
        padrao = filtro[2] if len(filtro) > 2 else None
        fid = f"NATIVE_FILTER-{dash_cfg['slug']}-{n}"
        base = {"id": fid, "name": nome, "type": "NATIVE_FILTER", "description": "", "cascadeParentIds": [],
                "tabsInScope": [], "chartsInScope": ids,
                "scope": {"rootPath": ["ROOT_ID"], "excluded": []}}
        if coluna is None:
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


def _provisionar_dashboard(sessao, dash_cfg, datasets, donos, papeis):
    from superset.models.dashboard import Dashboard

    specs = [c for linha in dash_cfg["linhas"] for c, _, _ in linha if "markdown" not in c]
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
    dash.slices = graficos
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


# ---------------------------------------------------------------------------
# Validação: executa a consulta de cada gráfico
# ---------------------------------------------------------------------------
def _consulta(spec, ds):
    p = spec["params"]
    dttm = DATASETS[spec["ds"]]["dttm"]
    q = {"filters": [{"col": dttm, "op": "TEMPORAL_RANGE", "val": SEM_FILTRO}], "extras": {},
         "row_limit": p.get("row_limit", 1000), "time_range": SEM_FILTRO}
    tipo = spec["tipo"]
    if tipo == "numero":
        q.update(metrics=[p["metric"]], columns=[])
    elif tipo == "serie":
        eixo = {"timeGrain": p["time_grain_sqla"], "columnType": "BASE_AXIS", "sqlExpression": p["x_axis"],
                "label": p["x_axis"], "expressionType": "SQL"}
        q.update(metrics=p["metrics"], columns=[eixo] + p["groupby"], orderby=[[p["metrics"][0], False]])
    elif tipo == "barras":
        q.update(metrics=p["metrics"], columns=[p["x_axis"]], orderby=[[p["metrics"][0], False]])
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
                    datasource={"id": ds.id, "type": "table"}, queries=[_consulta(spec, ds)],
                    form_data=json.loads(slc.params), result_type=ChartDataResultType.FULL,
                    result_format=ChartDataResultFormat.JSON, force=True)
                res = qc.get_payload()["queries"][0]
                erro = res.get("error")
                linhas = res.get("rowcount", 0)
            except Exception as exc:  # noqa: BLE001
                erro, linhas = str(exc), 0
            if erro or not linhas:
                falhas += 1
                logger.error("  [FALHA] %-45s linhas=%s erro=%s", slc.slice_name, linhas, erro)
            else:
                logger.info("  [ok]    %-45s linhas=%s", slc.slice_name, linhas)
    return falhas


def main(somente_validar=False):
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

        if somente_validar:
            datasets = {n: sessao.query(SqlaTable).filter(SqlaTable.table_name == n,
                                                          SqlaTable.database_id == conexao.id).one()
                        for n in DATASETS}
            resultados = []
            for cfg in DASHBOARDS:
                dash = sessao.query(Dashboard).filter(Dashboard.slug == cfg["slug"]).one()
                specs = [c for linha in cfg["linhas"] for c, _, _ in linha if "markdown" not in c]
                por_nome = {s.slice_name: s for s in dash.slices}
                resultados.append((cfg["titulo"], specs, [por_nome[s["nome"]] for s in specs], datasets))
        else:
            datasets = {n: _provisionar_dataset(sessao, conexao, n, cfg, donos) for n, cfg in DATASETS.items()}
            resultados = []
            for cfg in DASHBOARDS:
                specs, graficos = _provisionar_dashboard(sessao, cfg, datasets, donos, papeis)
                resultados.append((cfg["titulo"], specs, graficos, datasets))
            sessao.commit()
            logger.info("Datasets, gráficos e dashboards de demonstração gravados.")

        falhas = _validar(resultados, admin)
        if falhas:
            logger.error("%d gráfico(s) sem dados ou com erro.", falhas)
            sys.exit(2)
        logger.info("Todos os gráficos retornaram dados.")


if __name__ == "__main__":
    main(somente_validar="--validar" in sys.argv[1:])
