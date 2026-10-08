CREATE PROCEDURE [muralha].[spu_captura_transporte_clandestino]
@cad_veiculo_monitorado char(36)
AS
BEGIN

SET NOCOUNT ON

	CREATE TABLE #TEMP (
	id uniqueidentifier,
	placa char(7),
	data datetime,
	id_local integer)

	DECLARE 
	@passagens integer,
	@periodoini time,
	@periodofim time,
	@placa char(7),
	@local integer,
	@data datetime,
	@passagens_alerta integer,
	@passagens_real integer,
	@id_new char(36),
	@ultima_geracao datetime,
	@tipo_alerta_ocorrencia char(36),
	@status_alerta char(36),
	@guid_alerta char(36),
	@tipo_veiculo char(1)

	SET @tipo_alerta_ocorrencia = (select id from  muralha.tipo_alerta_ocorrencia where nomeinterno = 'CLANDESTINO') -- TRANSPORTE CLANDESTINO
    SET @status_alerta = (select id from  muralha.status_alerta where descricao = 'PENDENTE') -- id_status_alerta
	SET @ultima_geracao = (select ultima_geracao_transporte from muralha.controla_execucao_Job)
	SET @tipo_veiculo = CASE (select top 1 classificacao from muralha.config_alerta_transp_clandestino ORDER BY data_criacao DESC) WHEN 1 THEN 'M' WHEN 2 THEN 'P' WHEN 3 THEN 'T' WHEN 4 THEN 'O' ELSE null END

	-- Primeiro cursor para buscar as configurações de faixa de horário e número mínimo de passagens
	DECLARE configuracao_passagem CURSOR FOR 
	select qtde_passagens,periodo_inicial,periodo_final 
	from muralha.config_alerta_transp_clandestino
	where cast(getdate() as time(0)) between cast(periodo_inicial as time(0)) and cast(periodo_final as time(0)) 

	
	OPEN configuracao_passagem
	FETCH NEXT FROM configuracao_passagem 
	INTO @passagens,@periodoini, @periodofim

	-- carrega na tabela temporária só os registros do dia
	
	if (@tipo_veiculo is not null) and @tipo_veiculo <> 'O' 
	    begin
			INSERT INTO #TEMP -- #TEMP
			select id,placa,data,id_local from muralha.veiculo_tempo_real
			where placa is not null
			and cast(data as date) = cast(getdate() as date)
			and cast(data as time(0)) between cast(@periodoini as time(0)) and cast(@periodofim as time(0)) 
			and classificacao = (@tipo_veiculo)
		end
	else if (@tipo_veiculo is not null) and @tipo_veiculo = 'O' 
	    begin
			INSERT INTO #TEMP -- #TEMP
			select id,placa,data,id_local from muralha.veiculo_tempo_real
			where placa is not null
			and cast(data as date) = cast(getdate() as date)
			and cast(data as time(0)) between cast(@periodoini as time(0)) and cast(@periodofim as time(0)) 
			and classificacao in('O','C')
		end
	else  
	    begin
			INSERT INTO #TEMP -- #TEMP
			select id,placa,data,id_local from muralha.veiculo_tempo_real
			where placa is not null
			and cast(data as date) = cast(getdate() as date)
			and cast(data as time(0)) between cast(@periodoini as time(0)) and cast(@periodofim as time(0)) 
		end


	WHILE @@FETCH_STATUS = 0  
	  BEGIN 
  	      		-- Segundo cursor para buscar e totalizar as passagens dentro do período configurado em configuracao_passagem
				DECLARE passagens CURSOR FOR 
				
				--Busca os registros que estiverem dentro do período configurado em configuracao_passagem e
				--com uma quantidade de passagens >=  @passagens

				SELECT placa,id_local,cast(data as date),count(*) FROM  #TEMP -- #TEMP 
				where cast(data as date) = cast(getdate() as date) 
				and cast(data as time(0)) between cast(@periodoini as time(0)) and cast(@periodofim as time(0)) 
				and placa not in(select placa from muralha.cad_veiculo_exclusao) -- Lista de exclusão
				group by placa,	id_local,cast(data as date)
				having count(*) >= @passagens
				order by id_local,cast(data as date)

				OPEN passagens
				FETCH NEXT FROM passagens 
				INTO @placa,@local,@data,@passagens_alerta

				WHILE @@FETCH_STATUS = 0  
				BEGIN
				    -- Verifica se já existe um alerta gerado para a placa, se ouver gera só o registro da passagem.
				
				    set @guid_alerta = coalesce((select top 1 a.id_alerta 
										from muralha.alerta_veiculo a, muralha.veiculo_tempo_real b,muralha.alerta c
										where a.id_veiculo_tempo_real = b.id
										and a.id_alerta = c.id
										and c.id_tipo_alerta_ocorrencia = @tipo_alerta_ocorrencia
										and cast(b.data as date) = cast(getdate() as date) 
										and cast(b.data as time(0)) between cast(@periodoini as time(0)) and cast(@periodofim as time(0))
										and b.placa = @placa 
										and b.id_local = @local),'00000000-0000-0000-0000-000000000000')

							IF @guid_alerta <> '00000000-0000-0000-0000-000000000000' 
							  BEGIN
								INSERT INTO  muralha.alerta_veiculo
								(id_alerta,id_veiculo_tempo_real)

								select top 1 @guid_alerta,id
								from #TEMP where placa = @placa 
								and id_local = @local
								and cast(data as time(0)) > cast( @ultima_geracao as time(0)) 
								order by data desc
							  END
						   ELSE 
							  BEGIN
								set @id_new = newid()
								INSERT INTO muralha.alerta 
								(
								id
								,id_tipo_alerta_ocorrencia
								,id_cad_veiculo_monitorado
								,id_status_alerta
								,data
								,id_motivo_descarte
								,observacao	
								,id_usuario	
								,enviado_cliente
								,data_descarte
								,origem
								,lembrete_visualizado
								,id_ponto_interesse
								)
								VALUES
								(
								@id_new
								,@tipo_alerta_ocorrencia
								,@cad_veiculo_monitorado
								,@status_alerta
								,getdate()
								,null
								,null
								,2
								,0
								,null
								,'AUTOMATICO'
								,0
								,null
								)

								INSERT INTO  muralha.alerta_veiculo
								(id_alerta,id_veiculo_tempo_real)
								select @id_new,id from #TEMP 
								where placa = @placa 
								and cast(data as time(0)) between cast(@periodoini as time(0)) and cast(@periodofim as time(0)) 
								and cast(data as date) = cast(getdate() as date)
							END
				   -- Incremento do segundo cursor
				   FETCH NEXT FROM passagens 
				   INTO @placa,@local,@data,@passagens_alerta
				END
				CLOSE passagens
				DEALLOCATE passagens
		   END
		-- incremento do primeiro cursor
	  	FETCH NEXT FROM configuracao_passagem 
		INTO @passagens,@periodoini, @periodofim

		DROP TABLE #temp

		CLOSE configuracao_passagem 
		DEALLOCATE configuracao_passagem

		UPDATE muralha.controla_execucao_Job SET ultima_geracao_transporte = getdate()
END
