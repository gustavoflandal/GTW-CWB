
  CREATE FUNCTION [dbo].[fcn_getRelatorio2MedicaoFluxoVeicular](@Data_Ini DATETIME, @Data_Fim DATETIME, @Id_Local INT, @Id_Pista INT)  
RETURNS TABLE  
AS  
RETURN  
(  
   
 --DECLARE @Data_Ini DATETIME = '2020-09-01 00:00:00.000', @Data_Fim DATETIME = '2020-09-30 23:59:59.000', @Id_Local INT = 5, @Id_Pista INT = NULL  
 SELECT CONVERT(VARCHAR(10), datas.Data, 103) AS dia,  
     DATEPART(WEEKDAY, datas.Data) AS dia_semana,  
     CASE WHEN DATEPART(WEEKDAY, datas.Data) = 1 THEN 'Domingo'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 2 THEN 'Segunda'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 3 THEN 'Terça'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 4 THEN 'Quarta'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 5 THEN 'Quinta'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 6 THEN 'Sexta'  
    WHEN DATEPART(WEEKDAY, datas.Data) = 7 THEN 'Sábado'  
     END AS dia_semana_desc,  
     fluxo.veiculos_detectados AS fluxo_veicular,  
     fluxo.velocidade_media,  
     fluxo.velocidade_maxima,  
     COALESCE(infracao.infracoes_registradas, 0) AS autos_detectados,  
     --ROUND(CAST(fluxo.infracoes_registradas AS FLOAT) / NULLIF(CAST(fluxo.veiculos_detectados AS FLOAT),0), 2) AS porc_autos_detectados,  
     COALESCE(infracao.infracoes_validas, 0) AS autos_validos,  
     --ROUND(CAST(fluxo.infracoes_validas AS FLOAT) / NULLIF(CAST(fluxo.veiculos_detectados AS FLOAT), 0), 2) AS porc_autos_validos,  
     --ROUND(CAST(fluxo.infracoes_validas AS FLOAT) / NULLIF(CAST(fluxo.infracoes_registradas AS FLOAT), 0), 2) AS aproveitamento  
     COALESCE(infracao.invalidas_tecnicos, 0) AS invalidas_tecnicos,  
     COALESCE(infracao.invalidas_nao_tecnicos, 0) AS invalidas_nao_tecnicos  
 FROM   dbo.fcn_ObterDatasPeriodo(@Data_Ini, @Data_Fim) datas  
     LEFT JOIN (  
    --DECLARE @Data_Ini DATETIME = '2019-06-01 00:00:00.000', @Data_Fim DATETIME = '2019-06-30 23:59:59.000', @Id_Local INT = 43, @Id_Pista INT = NULL  
    SELECT vp.dia,  
        DATEPART(WEEKDAY, vp.dia) AS dia_semana,  
        SUM(vp.veiculos_detectados) AS veiculos_detectados,  
        AVG(vp.velocidade_media) AS velocidade_media,  
        MAX(vp.velocidade_maxima) AS velocidade_maxima,  
        SUM(vp.infracoes_registradas) AS infracoes_registradas,  
        SUM(vp.infracoes_validas) AS infracoes_validas  
    FROM   dbo.fcn_getVeiculoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp  
    WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
    GROUP BY  
        vp.dia  
     ) fluxo  
    ON  fluxo.dia = datas.Data  
     LEFT JOIN (   
    --DECLARE @Data_Ini DATETIME = '2019-06-01 00:00:00.000', @Data_Fim DATETIME = '2019-06-30 23:59:59.000', @Id_Local INT = 43, @Id_Pista INT = NULL  
    SELECT vp.dia,  
        DATEPART(WEEKDAY, vp.dia) AS dia_semana,  
        SUM(vp.infracoes_registradas) AS infracoes_registradas,  
        SUM(vp.infracoes_validas) AS infracoes_validas,  
        SUM(vp.invalidas_tecnicos) AS invalidas_tecnicos,  
        SUM(vp.invalidas_nao_tecnicos) AS invalidas_nao_tecnicos  
    FROM   dbo.fcn_getInfracaoSumarizadoRelatorioLocal(@Data_Ini, @Data_Fim, @Id_Local) vp  
    WHERE  vp.id_pista = CASE WHEN @Id_Pista IS NULL THEN vp.id_pista ELSE @Id_Pista END  
    GROUP BY  
        vp.dia  
     ) AS infracao  
    ON  infracao.dia = datas.Data  
  
)   
