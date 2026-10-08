--drop table [muralha].[veiculo_tempo_real_info]

--CREATE TABLE [muralha].[veiculo_tempo_real_info](
--	[id] [int] NOT NULL primary key IDENTITY,
--	[id_veiculo_tempo_real] [uniqueidentifier] NOT NULL,
--	[perfil_1] varchar(2000) NULL,
--	[perfil_2] varchar(2000) NULL,
--	[placa_frontal] varchar(7) NULL
--)

--ALTER TABLE [muralha].[veiculo_tempo_real_info]  WITH CHECK ADD  CONSTRAINT [FK_muralha_info_veiculo_tempo_real] FOREIGN KEY([id_veiculo_tempo_real])
--REFERENCES [muralha].[veiculo_tempo_real] ([id])
--GO

Alter table [muralha].[veiculo_tempo_real] add  [perfil_1] varchar(8000) NULL,
												[perfil_2] varchar(8000) NULL,
												[placa_frontal] varchar(7) NULL,
												[info_adicional] varchar(1000) NULL

select top (100) *from [muralha].[veiculo_tempo_real] order by data desc

select * from muralha.config_envio_tempo_real_equipamento order by data_exibicao_atualizacao desc



