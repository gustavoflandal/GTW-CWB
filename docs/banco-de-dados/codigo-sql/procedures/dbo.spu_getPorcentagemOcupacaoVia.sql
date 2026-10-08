--DECLARE @data_inicio DATETIME = '2021-12-10 10:00:00'
--DECLARE @data_fim DATETIME = '2021-12-10 12:00:00'

--EXEC dbo.spu_getPorcentagemOcupacaoVia @data_inicio, @data_fim, NULL


---- sp_helptext spu_getPorcentagemOcupacaoVia 

CREATE PROCEDURE [dbo].[spu_getPorcentagemOcupacaoVia]  
 @Data_Ini DATETIME,  
 @Data_Fim DATETIME,  
 @Id_Local INT  
AS  

-- 	(cast(sum(ocupacao) AS DECIMAL)/600000.00)*100 AS percentual_ocupacao 

 --DECLARE @Data_Ini DATE = '2021-12-01', @Data_Fim DATE = '2021-12-01', @Id_Local INT = NULL  
 DECLARE @temp_fluxo AS TABLE (id_local INT, id_pista TINYINT, ocupacao BIGINT)  
 DECLARE @temp_dados AS TABLE (serie_equipamento INT, local CHAR(100), faixa_1 BIGINT, faixa_2 BIGINT, faixa_3 BIGINT, faixa_4 BIGINT, faixa_5 BIGINT, faixa_6 BIGINT, faixa_7 BIGINT, faixa_8 BIGINT, total_faixas BIGINT)  
  
 DECLARE @TempoTotal BIGINT = (CAST(DATEDIFF(SECOND, @Data_Ini, @Data_Fim) AS BIGINT) * CAST(1000 AS BIGINT)) --+ CAST(86400000 AS BIGINT)
 --SELECT @TempoTotal

 SET NOCOUNT ON;  
  
 INSERT INTO @temp_fluxo  
 SELECT vs.id_local,  
     vs.pista,  
     SUM(vs.ocupacao) AS ocupacao  
 FROM   veiculo_sumarizado vs  
 WHERE  vs.data BETWEEN @Data_Ini AND @Data_Fim  
     AND vs.id_local = ISNULL(@Id_Local, vs.id_local)  
 GROUP BY  
     id_local,  
     vs.pista  
  
 INSERT INTO @temp_dados  
 SELECT serie_equipamento,  
     local,  
     faixa_1,  
     faixa_2,  
     faixa_3,  
     faixa_4,  
     faixa_5,  
     faixa_6,  
     faixa_7,  
     faixa_8,  
     (SELECT SUM(c)  
   FROM (VALUES(ISNULL(faixa_1, 0)),(ISNULL(faixa_2, 0)),(ISNULL(faixa_3, 0)),(ISNULL(faixa_4, 0)),(ISNULL(faixa_5, 0)),(ISNULL(faixa_6, 0)),(ISNULL(faixa_7, 0)),(ISNULL(faixa_8, 0))) T (c)) AS total_faixas  
 FROM   (  
    SELECT lv.serie_equipamento,  
        RTRIM(lv.nome) AS local,  
        'faixa_' + LTRIM(RTRIM(CAST(lv.cod_pista_alternativo AS VARCHAR(2)))) AS faixa,  
        f.ocupacao  
    FROM   local_pista_vigente lv  
        INNER JOIN @temp_fluxo f  
       ON  f.id_local = lv.id_local  
        AND f.id_pista = lv.id_pista  
    WHERE  lv.desativado = 0  
        AND lv.id_local = ISNULL(@Id_Local, lv.id_local)  
     ) r  
 PIVOT  (  
    SUM(r.ocupacao)  
    FOR r.faixa IN ([faixa_1],[faixa_2],[faixa_3],[faixa_4],[faixa_5],[faixa_6],[faixa_7],[faixa_8])  
     ) cont  
  
 SELECT serie_equipamento,  
     local,  
     faixa_1, CASE WHEN faixa_1 IS NOT NULL AND faixa_1 > 0 THEN CAST(faixa_1 AS FLOAT) / CAST(@TempoTotal AS FLOAT(53)) ELSE NULL END AS porcent_faixa_1,  
     faixa_2, CASE WHEN faixa_2 IS NOT NULL AND faixa_2 > 0 THEN CAST(faixa_2 AS FLOAT) / CAST(@TempoTotal AS FLOAT(53)) ELSE NULL END AS porcent_faixa_2,  
     faixa_3, CASE WHEN faixa_3 IS NOT NULL AND faixa_3 > 0 THEN CAST(faixa_3 AS FLOAT) / CAST(@TempoTotal AS FLOAT(53)) ELSE NULL END AS porcent_faixa_3,  
     faixa_4, CASE WHEN faixa_4 IS NOT NULL AND faixa_4 > 0 THEN CAST(faixa_4 AS FLOAT) / CAST(@TempoTotal AS FLOAT(53)) ELSE NULL END AS porcent_faixa_4,  
     faixa_5, CASE WHEN faixa_5 IS NOT NULL AND faixa_5 > 0 THEN CAST(faixa_5 AS FLOAT) / CAST(@TempoTotal AS FLOAT(53)) ELSE NULL END AS porcent_faixa_5,  
     faixa_6, CASE WHEN faixa_6 IS NOT NULL AND faixa_6 > 0 THEN CAST(faixa_6 AS FLOAT) / CAST(@TempoTotal AS FLOAT(53)) ELSE NULL END AS porcent_faixa_6,  
     faixa_7, CASE WHEN faixa_7 IS NOT NULL AND faixa_7 > 0 THEN CAST(faixa_7 AS FLOAT) / CAST(@TempoTotal AS FLOAT(53)) ELSE NULL END AS porcent_faixa_7,  
     faixa_8, CASE WHEN faixa_8 IS NOT NULL AND faixa_8 > 0 THEN CAST(faixa_8 AS FLOAT) / CAST(@TempoTotal AS FLOAT(53)) ELSE NULL END AS porcent_faixa_8,  
     total_faixas  
 FROM   @temp_dados  
 ORDER BY  
     serie_equipamento  
