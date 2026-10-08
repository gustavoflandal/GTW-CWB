CREATE PROCEDURE [dbo].[spu_criar_inibicao_infracao]   
(  
 @descricao varchar(200),  
 @pista int,  
 @serie_equipamento int,  
 @id_enquadramento int,  
 @id_classe char(1),  
 @data_inicio date,  
 @data_fim date,  
 @horario_inicio time,  
 @horario_fim time,  
 @id_usuario int,  
 @id_inconsistencia int = null  
)  
AS  
  
 BEGIN TRY   
     
  BEGIN TRANSACTION   
  
   INSERT INTO cad_inibicao_infracao with (rowlock)  
       (  
       descricao,  
       pista,  
       serie_equipamento,  
       id_enquadramento,  
       id_classe,  
       data_inicio,  
       data_fim,  
       horario_inicio,  
       horario_fim,  
       id_usuario,  
       id_filtro_relacionado  
       )  
    VALUES  
       (  
       @descricao,  
       @pista,  
       @serie_equipamento,  
       @id_enquadramento,  
       @id_classe,  
       @data_inicio,  
       @data_fim,  
       @horario_inicio,  
       @horario_fim,  
       @id_usuario,  
       NULL)  
       
  COMMIT  
    
  RETURN @@IDENTITY       
       
 END TRY   
  
 BEGIN CATCH   
   
  IF (@@TRANCOUNT > 0)   
   ROLLBACK   
   
  EXEC spu_replica_erro   
     
  RETURN 0   
     
 END CATCH  
        
  
  
  
