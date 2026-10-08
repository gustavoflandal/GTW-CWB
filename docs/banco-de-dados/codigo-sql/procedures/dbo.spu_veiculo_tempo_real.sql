CREATE PROCEDURE [dbo].[spu_veiculo_tempo_real]      
@id uniqueidentifier,@placa char(7),@data datetime,@id_local int,@id_pista tinyint,@velocidade smallint,      
@enviado_cliente bit,@data_enviado datetime,@classificacao char(1),@estado_veiculo tinyint,@perfil_1 varchar(750),      
@perfil_2 varchar(738),@placa_frontal varchar(7),@info_adicional varchar(7)
AS      
      
INSERT INTO [muralha].[veiculo_tempo_real] ([id], [placa], [data], [id_local], [id_pista], [velocidade], [enviado_cliente],       
[data_enviado], [classificacao], [estado_veiculo], [perfil_1], [perfil_2], [placa_frontal], [info_adicional],[id_captura])  
VALUES (@id, @placa, @data, @id_local, @id_pista, @velocidade, @enviado_cliente, @data_enviado, @classificacao,       
@estado_veiculo, @perfil_1, @perfil_2, @placa_frontal, @info_adicional, @id)  