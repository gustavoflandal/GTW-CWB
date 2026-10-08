
CREATE PROCEDURE [dbo].[spu_manutencao_alterar] 
	 @id_manutencao INT
	,@id_status INT = 0
	,@descricao VARCHAR(50)
	,@id_local  INT
	,@serie_equipamento INT
	,@id_pista INT
	,@tipo_grupo_autuador VARCHAR(30)
	,@data_ocorrencia DATETIME
	,@data_cadastro DATETIME
	,@data_inicio DATETIME
	,@data_previsto DATETIME
	,@id_ocorrencia INT
	,@numero_oficio INT
	,@ano_oficio INT
	,@id_tecnico INT
	,@id_auxiliar INT
	,@data_conclusao DATETIME
	,@data_ultima_alteracao DATETIME = GETDATE
	,@id_ultimo_usuario INT
	,@encaminhar BIT
AS
BEGIN

UPDATE [dbo].[manutencao]
SET            [id_status] = @id_status
           ,[descricao] = @descricao
           ,[id_local] = @id_local
           ,[serie_equipamento] = @serie_equipamento
           ,[id_pista] = @id_pista
		   ,[id_enquadramento] = (SELECT id_enquadramento FROM v_enquadramentos_manutencao WHERE tipo_apait = @tipo_grupo_autuador)
           ,[tipo_grupo_autuador] = (SELECT descricao_apait FROM v_enquadramentos_manutencao WHERE tipo_apait = @tipo_grupo_autuador)
           ,[data_ocorrencia] = @data_ocorrencia
           ,[data_cadastro] = @data_cadastro
           ,[data_inicio] = @data_inicio
           ,[data_previsto] = @data_previsto
           ,[id_ocorrencia] = @id_ocorrencia
           ,[numero_oficio] = @numero_oficio
           ,[ano_oficio] = @ano_oficio
           ,[id_tecnico] = @id_tecnico
           ,[id_auxiliar] = @id_auxiliar
           ,[data_conclusao] = @data_conclusao
           ,[data_ultima_alteracao] = GETDATE()
           ,[id_ultimo_usuario] = @id_ultimo_usuario
           ,[encaminhar] = @encaminhar
WHERE id_manutencao = @id_manutencao

RETURN @@rowcount

END
