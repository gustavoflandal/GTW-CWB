CREATE PROCEDURE [dbo].[spu_Monitoramento]  
AS  
  
SET NOCOUNT ON  
  
DECLARE @Relatorio  varchar(max)= '',  
  @Linha   int   = 90,  
  @Dias_Imagens int   = 30,  
  @Cabecalho  varchar(100)= '[C011_GTW] - Relatório de Importações Pendentes',  
  @SubCabecalho varchar(100),  
  @Data   DATE,  
  @Quantidade  int,  
  @UltimaData  datetime,  
  @Email   varchar(100)  
  
SET @Relatorio = @Relatorio +  
REPLICATE('=',@Linha) + CHAR(10) + CHAR(10) + @Cabecalho + CHAR(10) + CHAR(10) + REPLICATE('=',@Linha) + CHAR(10) + CHAR(10)  
  
-------------------------------------------------------------------------------------  
SELECT @UltimaData = MAX(data_importacao)  
FROM arquivos_importados (NOLOCK)  
  
SET @Relatorio = @Relatorio +   
'Data do último arquivo importado :  ' + CAST(@UltimaData as varchar) + ' ( Há ' + dbo.fcn_FormataNumero(DATEDIFF(minute,@UltimaData,getdate())) + ' minuto(s) )' +CHAR(10) + CHAR(10) +  
REPLICATE('=',@Linha) + CHAR(10) + CHAR(10)  
  
-------------------------------------------------------------------------------------  
SET @SubCabecalho = '[C011_GTW] FINALIZA - Importações Pendentes'  
  
SET @Relatorio = @Relatorio +   
@SubCabecalho + CHAR(10) + CHAR(10) +  
REPLICATE('-',@Linha) + CHAR(10) + CHAR(10)  
  
IF NOT EXISTS (SELECT 1   
     FROM veiculo_importacao vi (NOLOCK)  
      LEFT JOIN veiculo_estatistica ve (NOLOCK)  
       ON vi.id_veiculo_unic = ve.id_veiculo_unic  
     WHERE vi.importar = 1)  
 BEGIN  
  SET @Relatorio = @Relatorio +  
  'NÃO HÁ ESTATÍSTICAS PENDENTES DE IMPORTAÇÃO !!!' + CHAR(10)  
 END  
  
ELSE   
  
 BEGIN  
  
  DECLARE Cursor_Imp_Estatisticas CURSOR FOR   
  select cast(data as DATE), count(id_veiculo_unic)  
   from veiculo_importacao (NOLOCK)  
   where importar = 1  
   group by cast(data as DATE)  
   order by cast(data as DATE)  
  
  OPEN Cursor_Imp_Estatisticas  
  
  FETCH NEXT FROM Cursor_Imp_Estatisticas  
  INTO @Data, @Quantidade  
  
  WHILE @@FETCH_STATUS = 0  
  BEGIN  
  
   SET @Relatorio = @Relatorio + CAST(@Data as varchar) + ' - ' + dbo.fcn_FormataNumero(@Quantidade) + ' Estatísticas' + CHAR(10)  
  
   FETCH NEXT FROM Cursor_Imp_Estatisticas   
   INTO @Data, @Quantidade  
     
  END   
  
  CLOSE Cursor_Imp_Estatisticas  
  DEALLOCATE Cursor_Imp_Estatisticas  
  
 END  
  
SET @Relatorio = @Relatorio + CHAR(10) +   
REPLICATE('-',@Linha) + CHAR(10) + CHAR(10)  
  
IF NOT EXISTS (SELECT 1   
     FROM veiculo_importacao vi (NOLOCK)  
      INNER JOIN imagem_importacao ii (NOLOCK)  
       ON vi.id_veiculo_unic = ii.id_veiculo_unic  
     WHERE vi.importar = 1)  
 BEGIN  
  SET @Relatorio = @Relatorio +  
  'NÃO HÁ IMAGENS PENDENTES DE IMPORTAÇÃO !!!' + CHAR(10)  
 END  
  
ELSE   
  
 BEGIN  
  
  DECLARE Cursor_Imp_Imagens CURSOR FOR   
  select cast(vi.data as DATE), count(vi.id_veiculo_unic)  
   from veiculo_importacao vi (NOLOCK)  
    inner join imagem_importacao ii (NOLOCK)  
     on vi.id_veiculo_unic = ii.id_veiculo_unic  
   where vi.importar = 1  
   group by cast(vi.data as DATE)  
   order by cast(vi.data as DATE)  
  
  OPEN Cursor_Imp_Imagens  
  
  FETCH NEXT FROM Cursor_Imp_Imagens  
  INTO @Data, @Quantidade  
  
  WHILE @@FETCH_STATUS = 0  
  BEGIN  
  
   SET @Relatorio = @Relatorio + CAST(@Data as varchar) + ' - ' + dbo.fcn_FormataNumero(@Quantidade) + ' Imagens' + CHAR(10)  
  
   FETCH NEXT FROM Cursor_Imp_Imagens   
   INTO @Data, @Quantidade  
     
  END   
  
  CLOSE Cursor_Imp_Imagens  
  DEALLOCATE Cursor_Imp_Imagens  
  
 END  
  
SET @Relatorio = @Relatorio + CHAR(10) +  
REPLICATE('-',@Linha) + CHAR(10) + CHAR(10)  
  
PRINT @Relatorio  
  
SET @Email = 'sistemas@consilux.com.br'  
  
EXEC msdb.dbo.sp_send_dbmail   
 @Profile_name = 'SP-SERVER-CAI',  
 --@Profile_name = 'RJ-SERVER-CAI',  
 @recipients = @Email,  
 @subject = @Cabecalho,  
 @body = @Relatorio,  
 @body_format = 'TEXT'   
