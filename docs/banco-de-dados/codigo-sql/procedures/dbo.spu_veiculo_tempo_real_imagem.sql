CREATE PROCEDURE [dbo].[spu_veiculo_tempo_real_imagem]    
@id uniqueidentifier,@id_veiculo_tempo_real uniqueidentifier,@imagem image,@indice_imagem tinyint AS     
    
INSERT INTO [muralha].[veiculo_tempo_real_imagem] ([id], [id_veiculo_tempo_real], [imagem], [indice_imagem])     
VALUES (@id, @id_veiculo_tempo_real, @imagem, @indice_imagem)    
