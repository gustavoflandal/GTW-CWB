CREATE PROCEDURE [muralha].[spu_captura_ocorrencia]
AS
BEGIN

DECLARE
@id char(36),
@ativa integer,
@tipo char(20)

	DECLARE alerta_ocorrencia CURSOR FOR 

	select a.id,b.nomeinterno,tarefa_ativa 
	from muralha.cad_veiculo_monitorado a, muralha.tipo_alerta_ocorrencia b
	where a.id_tipo_alerta_ocorrencia = b.id
	and data_inicio is not null
	and coalesce(data_fim,getdate()+1) > getdate()
	and tarefa_ativa = 1
	and nomeinterno in('COMBOIO','SRBANCO','CLANDESTINO')
	AND a.placa IS NULL
	order by coalesce(data_fim,getdate()+1) desc

	OPEN alerta_ocorrencia
	
	FETCH NEXT FROM alerta_ocorrencia 
	INTO @id,@tipo,@ativa

	WHILE @@FETCH_STATUS = 0   
	  BEGIN
	     IF  (@tipo = 'COMBOIO') -- COMBOIO
			BEGIN
				EXEC muralha.spu_captura_comboio @id
			END
	     IF  (@tipo = 'SRBANCO') -- ROUBO A BANCO
			BEGIN
				EXEC muralha.spu_captura_roubo_a_banco @id
			END

	     IF  (@tipo = 'CLANDESTINO') -- TRANSPORTE CLANDESTINO
			BEGIN
				EXEC muralha.spu_captura_transporte_clandestino @id
			END

	    FETCH NEXT FROM alerta_ocorrencia 
	    INTO @id,@tipo,@ativa
	  END
	
	CLOSE alerta_ocorrencia
	DEALLOCATE alerta_ocorrencia
END
