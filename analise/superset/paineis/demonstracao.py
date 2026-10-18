#!/usr/bin/env python3
# =============================================================================
# demonstracao.py — Painéis de demonstração com dados reais (grupo "Demonstração" da
# tela de Análises do GTW).
#
# Uso:
#   docker compose exec superset python /app/paineis/demonstracao.py
#   docker compose exec superset python /app/paineis/demonstracao.py --validar
# =============================================================================

import sys

from construtor import (VP_SALVADOR, barras, calor, executar, mapa, md, numero, pizza, serie, tabela,
                        tabela_registros)

FONTE = ("Consulta ao vivo ao banco **GTW_MURALHA_DEV** (somente leitura, cache de {cache}). "
         "Clique em uma barra ou fatia para aplicar filtro cruzado; use a barra de filtros à esquerda.")

DASHBOARDS = [
    {
        "slug": "demo-fluxo-veicular",
        "titulo": "Demonstração — Fluxo Veicular",
        "descricao": "Passagens capturadas pelos equipamentos, agregadas por hora.",
        "filtros": [("Período", None), ("Equipamento", "equipamento"), ("Classificação", "classificacao"),
                    ("Tipo de placa", "tipo_placa")],
        "linhas": [
            [(md("### Fluxo veicular\nFonte: `analise.mat_passagens_hora` — passagens agregadas por hora desde "
                 "jun/2025, com o nome do equipamento vigente na data. " + FONTE.format(cache="1 h")), 12, 12)],
            [(numero("Passagens no período", "gtw_passagens_hora", "passagens", "veículos capturados", ",d"), 3, 22),
             (numero("Velocidade média", "gtw_passagens_hora", "velocidade_media", "km/h", ",.1f"), 3, 22),
             (numero("Placas Mercosul", "gtw_passagens_hora", "pct_mercosul", "das passagens", ".1%"), 3, 22),
             (numero("Equipamentos com captura", "gtw_passagens_hora", "equipamentos", "no período", ",d"), 3, 22)],
            [(serie("Passagens por semana e classificação", "gtw_passagens_hora", ["passagens"], "P1W",
                    groupby=["classificacao"]), 8, 50),
             (pizza("Distribuição por classificação", "gtw_passagens_hora", "classificacao", "passagens"), 4, 50)],
            [(calor("Mapa de calor: dia da semana x hora", "gtw_passagens_hora", "hora_dia", "dia_semana",
                    "passagens"), 8, 50),
             (pizza("Tipo de placa", "gtw_passagens_hora", "tipo_placa", "passagens"), 4, 50)],
            [(barras("Perfil horário — passagens", "gtw_passagens_hora", "hora_dia", "passagens",
                     ordenar_por="hora_dia", crescente=True), 6, 45),
             (barras("Perfil horário — velocidade média (km/h)", "gtw_passagens_hora", "hora_dia",
                     "velocidade_media", ordenar_por="hora_dia", crescente=True, formato=",.1f"), 6, 45)],
            [(tabela("Resumo por equipamento", "gtw_passagens_hora", ["equipamento"],
                     ["passagens", "velocidade_media", "pct_mercosul"]), 12, 40)],
        ],
    },
    {
        "slug": "demo-infracoes",
        "titulo": "Demonstração — Fiscalização e Infrações",
        "descricao": "Infrações registradas pelos equipamentos de fiscalização eletrônica.",
        "filtros": [("Período", None), ("Equipamento", "equipamento"), ("Categoria", "categoria"),
                    ("Tipo de infração", "tipo_infracao")],
        "linhas": [
            [(md("### Fiscalização eletrônica\nFonte: `dbo.infracao` + `dbo.enquadramento`, com o equipamento da "
                 "versão vigente em cada infração (imagens de teste excluídas). " + FONTE.format(cache="30 min")), 12, 12)],
            [(numero("Infrações registradas", "gtw_infracoes", "infracoes", "no período", ",d"), 3, 22),
             (numero("Excesso médio", "gtw_infracoes", "excesso_medio", "km/h acima do limite", ",.1f"), 3, 22),
             (numero("Infrações de velocidade", "gtw_infracoes", "pct_velocidade", "do total", ".1%"), 3, 22),
             (numero("Equipamentos com registro", "gtw_infracoes", "equipamentos", "pontos de fiscalização", ",d"), 3, 22)],
            [(serie("Infrações por mês e categoria", "gtw_infracoes", ["infracoes"], "P1M",
                    groupby=["categoria"]), 8, 50),
             (pizza("Infrações por tipo", "gtw_infracoes", "tipo_infracao", "infracoes"), 4, 50)],
            [(barras("Infrações por equipamento", "gtw_infracoes", "equipamento", "infracoes", horizontal=True), 6, 45),
             (barras("Distribuição do excesso de velocidade", "gtw_infracoes", "faixa_excesso", "infracoes",
                     ordenar_por="faixa_excesso", crescente=True), 6, 45)],
            [(calor("Mapa de calor: dia da semana x hora", "gtw_infracoes", "hora_dia", "dia_semana",
                    "infracoes"), 8, 50),
             (barras("Infrações por faixa", "gtw_infracoes", "faixa", "infracoes", ordenar_por="faixa",
                     crescente=True), 4, 50)],
            [(tabela("Ranking de enquadramentos", "gtw_infracoes", ["enquadramento"],
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
            [(numero("Volume total", "gtw_trafego_dia", "volume", "veículos contados", ",d"), 3, 22),
             (numero("Média diária", "gtw_trafego_dia", "media_diaria", "veículos por dia", ",.0f"), 3, 22),
             (numero("Velocidade média", "gtw_trafego_dia", "velocidade_media", "km/h", ",.1f"), 3, 22),
             (numero("Leitura de placas", "gtw_trafego_dia", "taxa_leitura", "dos veículos", ".1%"), 3, 22)],
            [(serie("Volume mensal por classe", "gtw_trafego_dia", ["volume"], "P1M", groupby=["classe"],
                    viz="echarts_area"), 8, 50),
             (pizza("Composição da frota", "gtw_trafego_dia", "classe", "volume"), 4, 50)],
            [(barras("Volume por equipamento (top 15)", "gtw_trafego_dia", "equipamento", "volume", limite=15,
                     horizontal=True), 6, 55),
             (barras("Distribuição por faixa de velocidade", "gtw_faixa_velocidade", "faixa_velocidade", "volume",
                     ordenar_por="faixa_velocidade", crescente=True), 6, 55)],
            [(serie("Velocidade média mensal (km/h)", "gtw_trafego_dia", ["velocidade_media"], "P1M",
                    viz="echarts_timeseries_line", formato=",.1f"), 6, 45),
             (serie("Leitura de placas por mês", "gtw_trafego_dia", ["taxa_leitura"], "P1M",
                    viz="echarts_timeseries_line", formato=".1%"), 6, 45)],
            [(tabela("Resumo por equipamento", "gtw_trafego_dia", ["equipamento"],
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
            [(numero("Alertas reportados", "gtw_waze_alertas", "alertas", "no período", ",d"), 3, 22),
             (numero("Acidentes", "gtw_waze_alertas", "acidentes", "ocorrências", ",d"), 3, 22),
             (numero("Congestionamentos", "gtw_waze_alertas", "congestionamentos", "ocorrências", ",d"), 3, 22),
             (numero("Vias interditadas", "gtw_waze_alertas", "interdicoes", "ocorrências", ",d"), 3, 22)],
            [(mapa("Mapa de ocorrências", "gtw_waze_alertas", VP_SALVADOR, dimensao="tipo"), 8, 60),
             (pizza("Alertas por tipo", "gtw_waze_alertas", "tipo", "alertas"), 4, 60)],
            [(serie("Alertas por semana e tipo", "gtw_waze_alertas", ["alertas"], "P1W", groupby=["tipo"]), 8, 50),
             (barras("Principais subtipos", "gtw_waze_alertas", "subtipo", "alertas", limite=10,
                     horizontal=True), 4, 50)],
            [(barras("Vias com mais ocorrências (top 15)", "gtw_waze_alertas", "via", "alertas", limite=15,
                     horizontal=True), 6, 55),
             (calor("Mapa de calor: dia da semana x hora", "gtw_waze_alertas", "hora_dia", "dia_semana",
                    "alertas"), 6, 55)],
            [(tabela_registros("Ocorrências mais recentes", "gtw_waze_alertas",
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
            [(numero("Alertas gerados", "gtw_alertas", "alertas", "no período", ",d"), 3, 22),
             (numero("Alertas críticos", "gtw_alertas", "criticos", "furto, roubo e sequestro", ",d"), 3, 22),
             (numero("Alertas tratados", "gtw_alertas", "pct_tratados", "com responsável atribuído", ".1%"), 3, 22),
             (numero("Alertas descartados", "gtw_alertas", "descartados", "após análise", ",d"), 3, 22)],
            [(serie("Alertas por mês e tipo", "gtw_alertas", ["alertas"], "P1M", groupby=["tipo_alerta"]), 8, 50),
             (pizza("Alertas por tipo", "gtw_alertas", "tipo_alerta", "alertas"), 4, 50)],
            [(calor("Mapa de calor: dia da semana x hora", "gtw_alertas", "hora_dia", "dia_semana",
                    "alertas"), 8, 50),
             (pizza("Criticidade", "gtw_alertas", "criticidade", "alertas"), 4, 50)],
            [(barras("Alertas por status", "gtw_alertas", "status", "alertas", horizontal=True), 6, 40),
             (tabela("Tipos de alerta — resumo", "gtw_alertas", ["tipo_alerta", "criticidade"],
                     ["alertas", "pct_tratados", "descartados"]), 6, 40)],
        ],
    },
]


if __name__ == "__main__":
    executar(DASHBOARDS, somente_validar="--validar" in sys.argv[1:])
