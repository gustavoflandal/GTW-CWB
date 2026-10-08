CREATE PROCEDURE [dbo].[spu_insertVeiculo] 
   @id_veiculo_local int,
   @data datetime,
   @velocidade decimal(6,1),
   @comprimento decimal (6,1),
   @pista tinyint,
   @flag int,
   @segundos decimal(6,3),
   @id_veiculo_unic int,
   @id_local int,
   @id_classe char(1)
AS

INSERT INTO veiculo with (rowlock) (
   id_veiculo_local,
   data,
   velocidade,
   comprimento,
   pista,
   flag,
   segundos,
   id_veiculo_unic,
   id_local,
   id_classe)
VALUES (
   @id_veiculo_local,
   @data,
   @velocidade,
   @comprimento,
   @pista,
   @flag,
   @segundos,
   @id_veiculo_unic,
   @id_local,
   @id_classe)


