CREATE FUNCTION [dbo].[fcn_LocalImagensDefeituosaAtualizaPainel] (@DataInicial DATETIME, @DataFinal DATETIME, @HoraInicial TIME, @HoraFinal TIME)  
RETURNS TABLE  
AS  
RETURN  
(  
 --DECLARE @DataInicial DATETIME = GETDATE() - 1, @DataFinal DATETIME = GETDATE(), @HoraInicial TIME = '07:10', @HoraFinal TIME = '17:20'
 --SELECT @DataInicial, @DataFinal, @HoraInicial, @HoraFinal  
 SELECT v.id_local,  
     v.pista AS id_pista,  
     COUNT(*) AS trafego,  
     --SUM(CASE WHEN vf.CAPTURED_IMAGE = 1 THEN 1 ELSE 0 END) AS trafegoImagemCapturada,  
     --SUM(CASE WHEN vf.OCR_PROCESSED  = 1 THEN 1 ELSE 0 END) AS trafegoOCRprocessado,  
     --SUM(CASE WHEN vf.CAPTURED_IMAGE = 1 AND vf.DEFECTIVE_IMAGE = 1 THEN 1 ELSE 0 END) AS trafegoImagemDef,  
     --SUM(CASE WHEN vf.OCR_PROCESSED  = 1 AND (placa IS NULL) THEN 1 ELSE 0 END) AS sem_placa_reconhecida  
		SUM(case when (flag & 4194304) <> 0 then 1 else 0 end) as trafegoImagemCapturada,
		SUM(case when (flag & 8388608) <> 0 then 1 else 0 end) as trafegoOCRprocessado,
		SUM(case when (flag & 4194304) <> 0  and (flag & 2097152) <> 0 then 1 else 0 end) as trafegoImagemDef,
		SUM(case when (flag & 8388608) <> 0  and (placa is null) then 1 else 0 end) as sem_placa_reconhecida
 --SELECT *
 FROM   veiculo_pesquisa_sumariza v (NOLOCK)  
    -- INNER JOIN veiculo_flag_atualiza_painel vf (NOLOCK)  
    --ON  v.id_veiculo_unic = vf.id_veiculo_unic  
 WHERE  v.data BETWEEN @DataInicial AND @DataFinal  
     AND (  
     (  
      @HoraInicial <= @HoraFinal  
      AND   
      CAST(v.data AS TIME) BETWEEN @HoraInicial AND @Horafinal  
     )   
     OR  
     (  
      @HoraInicial > @HoraFinal  
      AND NOT (CAST(v.data AS TIME) BETWEEN @Horafinal AND @HoraInicial)  
     )  
     )  
 GROUP BY  
     v.id_local,  
     v.pista  
)  
