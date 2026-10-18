#!/usr/bin/env python3
# =============================================================================
# operacionais.py — Os 9 painéis operacionais (grupo "Painéis operacionais" da tela de
# Análises do GTW), sobre a camada analise do GTW_MURALHA_DEV.
#
# Uso:
#   docker compose exec superset python /app/paineis/operacionais.py
#   docker compose exec superset python /app/paineis/operacionais.py --validar
# =============================================================================

import sys

from construtor import (VP_BRASIL, barras, calor, executar, mapa, nota, numero, pizza, poligonos, serie,
                        tabela)

DASHBOARDS = [
    {
        "slug": "kpis-operacionais",
        "titulo": "KPIs Operacionais",
        "descricao": "Visão consolidada dos principais indicadores operacionais.",
        "filtros": [("Período", None)],
        "linhas": [
            [nota("Indicadores consolidados da camada analise: passagens, infrações, alertas, disponibilidade, "
                  "SLA de latência e assertividade. Use o filtro Período para restringir.")],
            [(numero("KPI — Passagens", "gtw_passagens_hora", "passagens", "capturadas", ",d"), 2, 22),
             (numero("KPI — Infrações", "gtw_infracoes_dia", "infracoes", "registradas", ",d"), 2, 22),
             (numero("KPI — Alertas", "gtw_alertas", "alertas", "de veículos de interesse", ",d"), 2, 22),
             (numero("KPI — Disponibilidade", "gtw_disponibilidade", "disponibilidade", "horas com captura",
                     ".1%"), 2, 22),
             (numero("KPI — SLA de latência", "gtw_sla_latencia", "pct_sla", "janelas no prazo", ".1%"), 2, 22),
             (numero("KPI — Assertividade", "gtw_assertividade", "assertividade", "dupla análise", ".1%"), 2, 22)],
            [(serie("KPI — Passagens por mês", "gtw_passagens_hora", ["passagens"], "P1M"), 6, 45),
             (serie("KPI — Infrações por mês", "gtw_infracoes_dia", ["infracoes"], "P1M"), 6, 45)],
            [(serie("KPI — Disponibilidade mensal", "gtw_disponibilidade", ["disponibilidade"], "P1M",
                    viz="echarts_timeseries_line", formato=".1%"), 6, 45),
             (serie("KPI — Alertas por mês", "gtw_alertas", ["alertas"], "P1M"), 6, 45)],
        ],
    },
    {
        "slug": "assertividade-dupla-analise",
        "titulo": "Assertividade — Dupla Análise",
        "descricao": "Concordância entre a pré-classificação e a validação dos operadores.",
        "filtros": [("Período", None)],
        "linhas": [
            [nota("Fonte: analise.v_assertividade_dia (muralha.infracao_analise) — resultado das análises "
                  "registradas na dupla análise.")],
            [(numero("Assertividade atual", "gtw_assertividade", "assertividade", "pré-aprovadas / analisadas", ".1%"), 3, 22),
             (numero("Total analisadas", "gtw_assertividade", "analisadas", "registros", ",d"), 3, 22),
             (numero("Total de desempates", "gtw_assertividade", "desempates", "divergências", ",d"), 3, 22),
             (numero("Total reprovadas", "gtw_assertividade", "reprovadas", "registros", ",d"), 3, 22)],
            [(serie("Aprovadas x reprovadas x desempates", "gtw_assertividade",
                    ["aprovadas", "reprovadas", "desempates"], "P1D", empilhar=True), 7, 50),
             (serie("Evolução da assertividade", "gtw_assertividade", ["assertividade"], "P1D",
                    viz="echarts_timeseries_line", formato=".1%"), 5, 50)],
        ],
    },
    {
        "slug": "sla-latencia",
        "titulo": "SLA de Latência",
        "descricao": "Cumprimento do SLA de latência entre a passagem e a chegada ao servidor.",
        "filtros": [("Período", None)],
        "linhas": [
            [nota("Fonte: analise.v_sla_latencia_dia — janelas de 5 minutos avaliadas pelo job de SLA (P95 contra o limite).")],
            [(numero("SLA cumprido", "gtw_sla_latencia", "pct_sla", "janelas dentro do limite", ".1%"), 3, 22),
             (numero("Latência P95 média", "gtw_sla_latencia", "p95_medio", "milissegundos", ",.0f"), 3, 22),
             (numero("Janelas com violação", "gtw_sla_latencia", "violacoes", "no período", ",d"), 3, 22),
             (numero("Limite do SLA", "gtw_sla_latencia", "limite_ms", "milissegundos", ",d"), 3, 22)],
            [(serie("Evolução do SLA", "gtw_sla_latencia", ["pct_sla"], "P1D", viz="echarts_timeseries_line",
                    formato=".1%"), 6, 45),
             (serie("Latência P95 por dia (ms)", "gtw_sla_latencia", ["p95_medio"], "P1D", formato=",.0f"), 6, 45)],
            [(serie("Janelas no SLA x com violação", "gtw_sla_latencia", ["janelas_ok", "violacoes"], "P1D",
                    empilhar=True), 12, 45)],
        ],
    },
    {
        "slug": "disponibilidade-equipamentos",
        "titulo": "Disponibilidade de Equipamentos",
        "descricao": "Disponibilidade dos equipamentos medida pelas horas com captura.",
        "filtros": [("Período", None), ("Equipamento", "equipamento")],
        "linhas": [
            [nota("Disponibilidade = horas do dia com captura ÷ 24, incluindo os dias sem captura de cada "
                  "equipamento (fonte: analise.v_disponibilidade_dia).")],
            [(numero("Disponibilidade global", "gtw_disponibilidade", "disponibilidade", "horas com captura", ".1%"), 3, 22),
             (numero("Horas monitoradas", "gtw_disponibilidade", "horas_monitoradas", "no período", ",d"), 3, 22),
             (numero("Dias sem captura", "gtw_disponibilidade", "dias_sem_captura", "somados por equipamento", ",d"), 3, 22),
             (numero("Equipamentos avaliados", "gtw_disponibilidade", "equipamentos", "com operação", ",d"), 3, 22)],
            [(serie("Disponibilidade por mês", "gtw_disponibilidade", ["disponibilidade"], "P1M",
                    viz="echarts_timeseries_line", formato=".1%"), 6, 50),
             (barras("Equipamentos com menor disponibilidade", "gtw_disponibilidade", "equipamento",
                     "disponibilidade", limite=15, horizontal=True, crescente=False, formato=".1%",
                     menores=True), 6, 50)],
            [(tabela("Ranking de equipamentos por disponibilidade", "gtw_disponibilidade", ["equipamento"],
                     ["disponibilidade", "horas_captura", "dias_sem_captura", "dias_operacao"]), 12, 40)],
        ],
    },
    {
        "slug": "preprocessamento-imagens",
        "titulo": "Pré-processamento de Imagens",
        "descricao": "Volumes, aproveitamento, prazo e produtividade do pré-processamento das infrações.",
        "filtros": [("Período", None), ("Equipamento", "equipamento"), ("Operador", "operador")],
        "linhas": [
            [nota("Fonte: analise.v_preprocessamento (processamento de dbo.infracao) e motivos de descarte de "
                  "dbo.inconsistencia. SLA de 72h medido entre a captura e a entrada no processamento.")],
            [(numero("Total recebidas", "gtw_preprocessamento", "recebidas", "infrações", ",d"), 3, 22),
             (numero("Total disponibilizadas", "gtw_preprocessamento", "disponibilizadas", "após o processamento", ",d"), 3, 22),
             (numero("Aproveitamento", "gtw_preprocessamento", "aproveitamento", "disponibilizadas / recebidas", ".1%"), 3, 22),
             (numero("SLA de 72h", "gtw_preprocessamento", "sla_72h", "dentro do prazo", ".1%"), 3, 22)],
            [(serie("Disponibilizadas x descartadas por mês", "gtw_preprocessamento",
                    ["disponibilizadas", "descartadas"], "P1M", empilhar=True), 8, 50),
             (pizza("Recebidas por operador", "gtw_preprocessamento", "operador", "recebidas"), 4, 50)],
            [(serie("Tempo médio até a entrada (h)", "gtw_preprocessamento", ["horas_medias"], "P1M",
                    viz="echarts_timeseries_line", formato=",.1f"), 6, 45),
             (barras("Motivos de descarte", "gtw_descartes", "motivo", "descartes", limite=12,
                     horizontal=True), 6, 45)],
            [(tabela("Produtividade por operador", "gtw_preprocessamento", ["operador"],
                     ["recebidas", "disponibilizadas", "descartadas", "sla_72h"]), 12, 35)],
        ],
    },
    {
        "slug": "alertas",
        "titulo": "Alertas",
        "descricao": "Alertas gerados pela Muralha Digital e seu tratamento.",
        "filtros": [("Período", None), ("Tipo de alerta", "tipo_alerta"), ("Status", "status")],
        "linhas": [
            [nota("Fonte: analise.v_alertas (muralha.alerta). Clique em um tipo ou status para filtrar os demais gráficos.")],
            [(numero("Total de alertas", "gtw_alertas", "alertas", "no período", ",d"), 3, 22),
             (numero("Alertas críticos (total)", "gtw_alertas", "criticos", "furto, roubo e sequestro", ",d"), 3, 22),
             (numero("Alertas pendentes", "gtw_alertas", "pendentes", "aguardando tratamento", ",d"), 3, 22),
             (numero("Percentual tratado", "gtw_alertas", "pct_tratados", "com responsável", ".1%"), 3, 22)],
            [(serie("Evolução de alertas por tipo", "gtw_alertas", ["alertas"], "P1W", groupby=["tipo_alerta"]), 8, 50),
             (pizza("Distribuição por tipo de alerta", "gtw_alertas", "tipo_alerta", "alertas"), 4, 50)],
            [(pizza("Distribuição por status", "gtw_alertas", "status", "alertas"), 4, 45),
             (tabela("Principais responsáveis", "gtw_alertas", ["responsavel"],
                     ["alertas", "descartados"], limite=15), 8, 45)],
        ],
    },
    {
        "slug": "infracoes",
        "titulo": "Infrações",
        "descricao": "Infrações registradas pelos equipamentos de fiscalização.",
        "filtros": [("Período", None), ("Equipamento", "equipamento"), ("Categoria", "categoria")],
        "linhas": [
            [nota("Fonte: analise.mat_infracoes_dia (carga diária de dbo.infracao, sem imagens de teste), com o "
                  "equipamento vigente em cada dia.")],
            [(numero("Total de infrações", "gtw_infracoes_dia", "infracoes", "no período", ",d"), 3, 22),
             (numero("Excesso médio de velocidade", "gtw_infracoes_dia", "excesso_medio", "km/h acima do limite", ",.1f"), 3, 22),
             (numero("Equipamentos com infrações", "gtw_infracoes_dia", "equipamentos", "pontos de fiscalização", ",d"), 3, 22),
             (numero("Participação de velocidade", "gtw_infracoes_dia", "pct_velocidade", "do total", ".1%"), 3, 22)],
            [(serie("Infrações por mês e categoria", "gtw_infracoes_dia", ["infracoes"], "P1M",
                    groupby=["categoria"]), 8, 45),
             (pizza("Infrações por enquadramento", "gtw_infracoes_dia", "enquadramento", "infracoes"), 4, 45)],
            [(tabela("Ranking de equipamentos por infrações", "gtw_infracoes_dia", ["equipamento"],
                     ["infracoes", "excesso_medio"]), 6, 40),
             (barras("Infrações por faixa de rolamento", "gtw_infracoes_dia", "faixa", "infracoes",
                     ordenar_por="faixa", crescente=True), 6, 40)],
        ],
    },
    {
        "slug": "passagens-veiculares",
        "titulo": "Passagens Veiculares",
        "descricao": "Passagens capturadas pelos equipamentos, por hora, faixa e classificação.",
        "filtros": [("Período", None), ("Equipamento", "equipamento"), ("Classificação", "classificacao")],
        "linhas": [
            [nota("Fonte: analise.mat_passagens_hora — passagens agregadas por hora desde jun/2025.")],
            [(numero("Total de passagens", "gtw_passagens_hora", "passagens", "no período", ",d"), 3, 22),
             (numero("Velocidade média das passagens", "gtw_passagens_hora", "velocidade_media", "km/h", ",.1f"), 3, 22),
             (numero("Equipamentos ativos", "gtw_passagens_hora", "equipamentos", "com captura", ",d"), 3, 22),
             (numero("Percentual Mercosul", "gtw_passagens_hora", "pct_mercosul", "das placas", ".1%"), 3, 22)],
            [(barras("Passagens por hora do dia", "gtw_passagens_hora", "hora_dia", "passagens",
                     ordenar_por="hora_dia", crescente=True), 8, 45),
             (pizza("Passagens por classificação", "gtw_passagens_hora", "classificacao", "passagens"), 4, 45)],
            [(calor("Passagens por faixa e hora", "gtw_passagens_hora", "hora_dia", "faixa", "passagens"), 7, 45),
             (tabela("Ranking de equipamentos por passagens", "gtw_passagens_hora", ["equipamento"],
                     ["passagens", "velocidade_media"]), 5, 45)],
        ],
    },
    {
        "slug": "mapa-equipamentos-infracoes",
        "titulo": "Mapa — Equipamentos, Áreas e Infrações",
        "descricao": "Equipamentos cadastrados, áreas monitoradas, passagens e infrações no mapa.",
        "filtros": [],
        "linhas": [
            [nota("Fontes: analise.v_equipamentos, analise.v_areas_monitoradas_geojson, "
                  "analise.v_passagens_geolocalizado e analise.v_infracoes_geolocalizado (coordenadas da versão "
                  "vigente de cada equipamento).")],
            [(mapa("Equipamentos cadastrados", "gtw_equipamentos", VP_BRASIL), 6, 60),
             (poligonos("Áreas monitoradas", "gtw_areas_monitoradas", VP_BRASIL, "poligono", "areas"), 6, 60)],
            [(mapa("Passagens por equipamento", "gtw_passagens_geo", VP_BRASIL, metrica="passagens",
                   multiplicador=0.05), 6, 60),
             (mapa("Infrações por equipamento", "gtw_infracoes_geo", VP_BRASIL, metrica="infracoes",
                   multiplicador=0.5, cor=(224, 67, 85)), 6, 60)],
            [(tabela("Equipamentos com mais infrações", "gtw_infracoes_dia", ["equipamento"],
                     ["infracoes", "excesso_medio"]), 12, 40)],
        ],
    },
]


if __name__ == "__main__":
    executar(DASHBOARDS, somente_validar="--validar" in sys.argv[1:])
