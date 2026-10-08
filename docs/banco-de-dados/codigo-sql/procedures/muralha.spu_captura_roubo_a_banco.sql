



CREATE PROCEDURE [muralha].[spu_captura_roubo_a_banco]
@cad_veiculo_monitorado char(36)
AS
BEGIN

DECLARE 
@monitorar_comboio bit,
@data_inicio datetime,
@data_final datetime,
@ultima_geracao datetime,
@tipo_alerta_ocorrencia char(36),
@status_alerta char(36)


SET @tipo_alerta_ocorrencia = (select id from  muralha.tipo_alerta_ocorrencia where nomeinterno = 'SRBANCO') -- SUSPEITA DE ROUBO A BANCO
SET @status_alerta = (select id from  muralha.status_alerta where descricao = 'PENDENTE') -- id_status_alerta
SET @ultima_geracao = (select coalesce(ultima_geracao_roubo,getdate()) from muralha.controla_execucao_Job) 

CREATE TABLE #LOCAL 
(
	id_local integer
)

insert into #local select id_local from muralha.ponto_interesse_equipamentos 
	where id_ponto_interesse in(select id_ponto_interesse from muralha.ponto_interesse where id_tipo = 1 and ativo = 1)

CREATE TABLE #TABELA
(
	Seq varchar(50),
	id varchar(50),
	placa varchar(10),
	id_local integer,
	data datetime,
	tipo varchar(50)
)

insert into #tabela 
SELECT 
	Seq= ROW_NUMBER() OVER(ORDER BY vtr.id_local,vtr.data ASC),
	vtr.id,
	vtr.placa,
	vtr.id_local,
	a.data,
	tao.tipo
FROM muralha.alerta a 
INNER JOIN muralha.tipo_alerta_ocorrencia tao
	ON tao.id = a.id_tipo_alerta_ocorrencia
INNER JOIN muralha.alerta_veiculo av
	ON av.id_alerta = a.id
INNER JOIN muralha.veiculo_tempo_real vtr
	ON vtr.id = av.id_veiculo_tempo_real
WHERE tao.nomeinterno in ('ROUBADO','FURTADO','SEQUESTRO') 
	--and a.data > @ultima_geracao 
and cast(vtr.data as date) = cast(getdate() as date)
and vtr.id_local  in(select id_local from #local)
order by vtr.id_local,vtr.data asc

if (exists(select * from muralha.cad_veiculo_monitorado where id_tipo_alerta_ocorrencia=@tipo_alerta_ocorrencia))
	begin
	   set @monitorar_comboio = 1
	   DECLARE MonitorarComboioRouboBanco CURSOR FOR

	   select 
	   coalesce(data_inicio,DATEADD(hh,-24,getdate())) as data_inicio, 
	   coalesce(data_fim,DATEADD(hh,24,getdate())) as data_fim
	   from muralha.cad_veiculo_monitorado 
	   where id_tipo_alerta_ocorrencia=@tipo_alerta_ocorrencia

	   OPEN MonitorarComboioRouboBanco
	   FETCH NEXT FROM MonitorarComboioRouboBanco 
	   INTO @data_inicio,@data_final
	   CLOSE MonitorarComboioRouboBanco 
	   DEALLOCATE MonitorarComboioRouboBanco
	end


if (@data_inicio < getdate()) and (@data_final > getdate())
  begin
	DECLARE AlertasCursor CURSOR FOR   
	with
	base1 as (SELECT seq,id,placa,id_local,data,tipo from #tabela),
	base2 as (SELECT seq,id,placa,id_local,data,tipo from #tabela)

	select a.id id1,a.seq seq1,a.id_local local1,a.placa placa1,a.tipo tipo1,a.data data1,
		   b.id id2,b.seq seq2,b.id_local local2,b.placa placa2,b.tipo tipo2,b.data data2, 
	       datediff(second,a.data,b.data) as tempo 
	from base1 a left outer join base2 b 
	  on datepart(day,a.data) = datepart(day,b.data) and
	  datepart(month,a.data) = datepart(month,b.data) and
	  datepart(year,a.data) = datepart(year,b.data) and
	  datepart(hh,a.data) = datepart(hh,b.data) and
	  a.seq = b.seq-1
	where b.seq is not null and a.placa <> b.placa
	and datediff(second,a.data,b.data) > 0
	order by a.seq

	OPEN AlertasCursor

DECLARE
@guid1 char(50),
@guid2 char(50),
@seq1 integer,
@local1 integer,
@placa1 char(10),
@tipo1 char(50),
@data1 datetime,
@seq2 integer,
@local2 integer,
@placa2 char(10),
@tipo2 char(50),
@data2 datetime,
@tempo integer,
@tempo_passagem integer,
@placa_ant char(10),
@hora1 char(8),
@hora2 char(8),
@alerta bit,
@id_new char(36)

set @placa_ant = ''
set @alerta = 0
set @tempo_passagem = (select tempo_entre_passagens_sec from muralha.config_alerta_comboio)

SET NOCOUNT ON

FETCH NEXT FROM AlertasCursor 
INTO @guid1,@seq1, @local1, @placa1, @tipo1,@data1,@guid2,@seq2,@local2,@placa2,@tipo2,@data2,@tempo

	WHILE @@FETCH_STATUS = 0  
		BEGIN 
		  IF (@tempo <=	@tempo_passagem) and (@placa1 <> @placa2)
		     BEGIN
			    IF (@placa1 <> @placa_ant)  
					BEGIN
					  IF @alerta = 0
					     begin
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

							,@tipo_alerta_ocorrencia  -- id_tipo_alerta_ocorrencia roubo a banco
							,@cad_veiculo_monitorado  -- id_cad_veiculo_monitorado roubo a banco
							,@status_alerta           -- id_status_alerta
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
						 end
					INSERT INTO muralha.alerta_veiculo
					(
					 id_alerta,
					 id_veiculo_tempo_real
					)
					VALUES
					(
					@id_new,
					@guid1
					)
					END
					set @placa_ant = @placa2

					INSERT INTO muralha.alerta_veiculo
					(
					 id_alerta,
					 id_veiculo_tempo_real
					)
					VALUES
					(
					@id_new,
					@guid2
					)
			 END
		  ELSE
			 BEGIN
				set @alerta = 0
			 END
		  FETCH NEXT FROM AlertasCursor
		  INTO @guid1,@seq1, @local1, @placa1, @tipo1,@data1,@guid2,@seq2,@local2,@placa2,@tipo2,@data2,@tempo
		END
UPDATE muralha.controla_execucao_job SET ultima_geracao_roubo = DATEADD(ss,@tempo_passagem,getdate())
DROP TABLE #LOCAL
DROP TABLE #TABELA
CLOSE AlertasCursor 
DEALLOCATE AlertasCursor

end
end
