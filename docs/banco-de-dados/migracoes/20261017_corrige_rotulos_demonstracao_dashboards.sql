/*
 * Correcao dos paineis de demonstracao do Dashboard Builder (paineis 101-102, widgets 1001-1016)
 *
 * Origem: auditoria de tooltips e referencias cruzadas do modulo de dashboards
 *         (docs/entregas/auditoria-dashboards-tooltips.md), Planos 20 e 21.
 *
 * O que muda (somente linhas da demonstracao; nada fora do schema dash.*):
 *   1. Rotulos dos campos dos datasets 101-103 com acentuacao correta. Esses rotulos
 *      sao exibidos nos tooltips, legendas, eixos e cabecalhos dos widgets.
 *   2. Periodo unico: KPIs, barras, pizza/rosca e tabelas passam a usar os mesmos
 *      "ultimos 12 meses" dos graficos de linha/area. Antes os KPIs somavam todo o
 *      historico enquanto as linhas mostravam 12 meses (totais que nao batiam) e o
 *      filtro global "De/Ate" do painel so alcancava os widgets de linha/area.
 *      Sem granularidade, o tempo apenas filtra o periodo (nao agrupa por mes).
 *   3. Titulos que nao correspondiam a metrica:
 *      - 1010 "Vel. Media Infracoes" dizia "km/h acima do limite", mas a medida e a
 *        media da velocidade considerada (absoluta), nao o excesso;
 *      - 1011 "Alertas Ativos" contava todos os alertas, sem filtro de situacao.
 *   4. Titulos, nomes e descricoes com acentuacao (exibidos na tela).
 *
 * Pre-requisito: 20261014_dados_demonstracao_dashboards.sql aplicado.
 * Idempotente: pode ser executado mais de uma vez.
 * Rollback: reexecutar 20261014_dados_demonstracao_dashboards.sql (recria a demonstracao).
 *
 * Execucao (arquivo em UTF-8):
 *   sqlcmd -S <servidor> -d GTW_MURALHA_DEV -U <usuario> -P <senha> -b -I -f 65001 -i 20261017_corrige_rotulos_demonstracao_dashboards.sql
 */

SET NOCOUNT ON;
SET XACT_ABORT ON;
BEGIN TRANSACTION;
BEGIN TRY

IF (SELECT COUNT(*) FROM dash.widget WHERE id BETWEEN 1001 AND 1016) <> 16
    THROW 50001, 'Demonstracao incompleta: esperados os widgets 1001-1016 (aplique 20261014_dados_demonstracao_dashboards.sql)', 1;

-- =====================================================================
-- 1. Rotulos dos campos (tooltips, legendas e cabecalhos)
-- =====================================================================
UPDATE c SET c.rotulo = v.rotulo
FROM dash.dataset_campo c
JOIN (VALUES
    (101, 'classificacao',           N'Classificação'),
    (101, 'velocidade_media',        N'Velocidade Média (km/h)'),
    (101, 'velocidade_total',        N'Velocidade Total'),
    (101, 'total_passagens',         N'Total de Passagens'),
    (102, 'data',                    N'Data do Alerta'),
    (103, 'total_infracoes',         N'Total de Infrações'),
    (103, 'data',                    N'Data da Infração'),
    (103, 'velocidade_media',        N'Velocidade Média Considerada (km/h)'),
    (103, 'velocidade_limite_media', N'Velocidade Limite Média (km/h)')
) AS v (id_dataset, alias, rotulo)
  ON c.id_dataset = v.id_dataset AND c.nome_alias = v.alias;

PRINT 'Rotulos de campos atualizados: ' + CAST(@@ROWCOUNT AS VARCHAR(10));

-- =====================================================================
-- 2. Paineis
-- =====================================================================
UPDATE dash.dashboard
   SET descricao = N'Visão geral das passagens de veículos, velocidades e classificações por equipamento nos últimos 12 meses.'
 WHERE id = 101;

UPDATE dash.dashboard
   SET nome = N'Painel de Segurança',
       descricao = N'Monitoramento de alertas e infrações nos últimos 12 meses.'
 WHERE id = 102;

-- =====================================================================
-- 3. Widgets — periodo unico, titulos e subtitulos
-- =====================================================================
UPDATE w SET w.titulo = v.titulo, w.consulta_json = v.consulta, w.visual_json = v.visual
FROM dash.widget w
JOIN (VALUES
    -- Painel 101
    (1001, N'Total de Passagens',
     N'{"measures":["total_passagens"],"dimensions":[],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":1}',
     N'{"formato":"inteiro","subtitulo":"Passagens nos últimos 12 meses","icone":"bi-car-front","thresholds":[{"valor":0,"cor":"#dc3545","rotulo":"Baixo"},{"valor":1000,"cor":"#ffc107","rotulo":"Normal"},{"valor":10000,"cor":"#28a745","rotulo":"Alto"}]}'),
    (1002, N'Velocidade Média',
     N'{"measures":["velocidade_media"],"dimensions":[],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":1}',
     N'{"formato":"decimal","subtitulo":"km/h, média dos últimos 12 meses","icone":"bi-speedometer2","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"Baixa"},{"valor":40,"cor":"#ffc107","rotulo":"Moderada"},{"valor":80,"cor":"#dc3545","rotulo":"Alta"}]}'),
    (1003, N'Total de Alertas',
     N'{"measures":["total_alertas"],"dimensions":[],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":1}',
     N'{"formato":"inteiro","subtitulo":"Alertas nos últimos 12 meses","icone":"bi-bell","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"OK"},{"valor":100,"cor":"#ffc107","rotulo":"Atenção"},{"valor":500,"cor":"#dc3545","rotulo":"Crítico"}]}'),
    (1004, N'Total de Infrações',
     N'{"measures":["total_infracoes"],"dimensions":[],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":1}',
     N'{"formato":"inteiro","subtitulo":"Infrações nos últimos 12 meses","icone":"bi-exclamation-triangle","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"OK"},{"valor":100,"cor":"#ffc107","rotulo":"Moderado"},{"valor":1000,"cor":"#dc3545","rotulo":"Alto"}]}'),
    (1005, N'Passagens por Equipamento',
     N'{"measures":["total_passagens"],"dimensions":["equipamento"],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":15}',
     N'{"empilhado":false,"rotulos":true,"paleta":"azul","orientacao":"horizontal"}'),
    (1006, N'Distribuição por Classificação',
     N'{"measures":["total_passagens"],"dimensions":["classificacao"],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":10}',
     N'{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"vertical"}'),
    (1007, N'Passagens por Mês',
     N'{"measures":["total_passagens"],"dimensions":[],"timeDimensions":[{"dimension":"data","granularity":"month","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":100}',
     N'{"empilhado":false,"rotulos":false,"paleta":"azul","orientacao":"vertical"}'),
    (1008, N'Detalhe por Equipamento',
     N'{"measures":["total_passagens","velocidade_media"],"dimensions":["equipamento","classificacao"],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":50}',
     N'{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"vertical"}'),
    -- Painel 102
    (1009, N'Total de Infrações',
     N'{"measures":["total_infracoes"],"dimensions":[],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":1}',
     N'{"formato":"inteiro","subtitulo":"Infrações nos últimos 12 meses","icone":"bi-shield-exclamation","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"OK"},{"valor":500,"cor":"#ffc107","rotulo":"Atenção"},{"valor":2000,"cor":"#dc3545","rotulo":"Crítico"}]}'),
    (1010, N'Velocidade Média nas Infrações',
     N'{"measures":["velocidade_media"],"dimensions":[],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":1}',
     N'{"formato":"decimal","subtitulo":"km/h considerada, média dos últimos 12 meses","icone":"bi-speedometer","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"Baixa"},{"valor":60,"cor":"#ffc107","rotulo":"Moderada"},{"valor":90,"cor":"#dc3545","rotulo":"Alta"}]}'),
    (1011, N'Total de Alertas',
     N'{"measures":["total_alertas"],"dimensions":[],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":1}',
     N'{"formato":"inteiro","subtitulo":"Alertas nos últimos 12 meses","icone":"bi-bell-fill","thresholds":[{"valor":0,"cor":"#28a745","rotulo":"OK"},{"valor":50,"cor":"#ffc107","rotulo":"Atenção"},{"valor":200,"cor":"#dc3545","rotulo":"Crítico"}]}'),
    (1012, N'Infrações por Equipamento',
     N'{"measures":["total_infracoes"],"dimensions":["equipamento"],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":15}',
     N'{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"horizontal"}'),
    (1013, N'Alertas por Tipo',
     N'{"measures":["total_alertas"],"dimensions":["tipo_alerta"],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":10}',
     N'{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"vertical"}'),
    (1014, N'Infrações por Mês',
     N'{"measures":["total_infracoes"],"dimensions":[],"timeDimensions":[{"dimension":"data","granularity":"month","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":100}',
     N'{"empilhado":false,"rotulos":false,"paleta":"verde","orientacao":"vertical"}'),
    (1015, N'Alertas por Mês',
     N'{"measures":["total_alertas"],"dimensions":[],"timeDimensions":[{"dimension":"data","granularity":"month","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":100}',
     N'{"empilhado":false,"rotulos":false,"paleta":"azul","orientacao":"vertical"}'),
    (1016, N'Detalhe de Infrações por Local',
     N'{"measures":["total_infracoes","velocidade_media","velocidade_limite_media"],"dimensions":["equipamento"],"timeDimensions":[{"dimension":"data","dateRange":"last 12 months"}],"filters":[],"order":{},"limit":50}',
     N'{"empilhado":false,"rotulos":true,"paleta":"padrao","orientacao":"vertical"}')
) AS v (id, titulo, consulta, visual)
  ON w.id = v.id;

IF @@ROWCOUNT <> 16
    THROW 50002, 'Esperadas 16 atualizacoes de widget', 1;

PRINT 'Widgets da demonstracao atualizados: 16';

COMMIT TRANSACTION;
PRINT 'Correcao da demonstracao concluida.';
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
