CREATE PROCEDURE [dbo].[spu_finaliza_importacao_targets]    
AS    
    
SET NOCOUNT ON    
    
DECLARE @Amostra     int   = 1000,    
  @Tempo_Processamento  varchar(8) = '00:10:00',    
  @Data_Fim     datetime    
    
SET @Data_Fim = GETDATE() + @Tempo_Processamento    
    
WHILE GETDATE() < @Data_Fim    
    
 BEGIN    
    
  BEGIN TRY    
       
	UPDATE veiculo_target SET veiculo_target.id_arquivo_csx5 = arquivos_importados.id_arquivo FROM arquivos_importados (NOLOCK)
	WHERE veiculo_target.nome_arquivo = arquivos_importados.nome_arquivo
	AND   veiculo_target.id_arquivo_csx5 IS NULL

    UPDATE veiculo_target SET veiculo_target.id_veiculo = sub1.id_veiculo FROM     
    (    
    SELECT TOP(100) v.id_veiculo, vt.id_alvo, vt.seq_deteccao_doppler, vt.id_local, vt.data_arquivo, vt.id_arquivo_csx5 FROM veiculo v (NOLOCK)     
    JOIN veiculo_target vt (NOLOCK)     
    ON      
      v.seq_deteccao_doppler = vt.seq_deteccao_doppler AND     
      v.id_arquivo = vt.id_arquivo_csx5
    
    WHERE     
    --v.id_veiculo = 162595 AND     
	--v.data >= GETDATE() - 1 AND   
    vt.id_veiculo IS NULL     
    ) AS sub1    
    WHERE        
    veiculo_target.seq_deteccao_doppler = sub1.seq_deteccao_doppler AND     
    veiculo_target.id_arquivo_csx5 = sub1.id_arquivo_csx5 
    
    IF @@ROWCOUNT = 0    
     BREAK    
    
  END TRY     
    
  BEGIN CATCH    
    
   PRINT 'ERRO [' + ERROR_MESSAGE() + '] EM [' + CAST(ERROR_LINE() AS VARCHAR) + ']'    
    
   IF @@TRANCOUNT > 0     
    ROLLBACK    
    
   BREAK    
    
  END CATCH    
    
 END    
    


