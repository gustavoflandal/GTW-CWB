CREATE   PROCEDURE spu_atualizar_classe_veiculo

@idCaptura VARCHAR,
@idClasse VARCHAR

AS

BEGIN

	update dbo.veiculo set id_classe = @idClasse where id_captura = @idCaptura;
	
	update muralha.veiculo_tempo_real set classificacao = @idClasse where id_captura = @idCaptura;

	update ia.veiculo_caracteristica set id_classificacao = @idClasse where id_captura = @idCaptura;

	update dbo.veiculo_estatistica set id_classe = @idClasse where id_captura = @idCaptura;

END