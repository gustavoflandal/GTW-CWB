
CREATE FUNCTION [dbo].[fcn_pesquisaIsento]
  ( @placa char(7),
    @id_enquadramento int,
    @data_base datetime )
RETURNS @ret TABLE (
	[placa] [char](7) NOT NULL,
	[id_enquadramento] [int] NOT NULL,
	[area] [int] NOT NULL,
	[id_localidade] [int] NULL,
	[modalidade] [char](2) NULL,
	[data_inicio] [date] NULL,
	[data_fim] [date] NULL,
	[horario_inicio] [time](0) NULL,
	[horario_fim] [time](0) NULL,
	[data_atualizacao] [datetime] NULL,
	[id_arquivo] [int] NOT NULL
)
AS
BEGIN

		INSERT INTO @ret
		SELECT 
			ci.placa,id_enquadramento,
			area,id_localidade,modalidade,
			data_inicio,
			data_fim,
			horario_inicio,
			horario_fim,
			data_atualizacao,
			id_arquivo
		FROM 
			cad_isento ci (nolock)
		WHERE ci.placa = @placa AND ci.id_arquivo = (SELECT MAX(cia.id_arquivo) 
								FROM cad_isento_arquivo cia (nolock)
								WHERE	cia.id_enquadramento = @id_enquadramento
								AND     cia.data_hora <= @data_base)
	
	RETURN

END
