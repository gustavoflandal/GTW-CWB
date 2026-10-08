
CREATE VIEW [dbo].[cad_isento_pesquisa]
AS
	SELECT [placa]
      ,[id_enquadramento]
      ,[area]
      ,[id_localidade]
      ,[modalidade]
      ,[data_inicio]
      ,[data_fim]
      ,[horario_inicio]
      ,[horario_fim]
      ,[data_atualizacao]
      ,[id_arquivo]
	FROM [cad_isento] (nolock)
	UNION
	SELECT [placa]
      ,[id_enquadramento]
      ,[area]
      ,[id_localidade]
      ,[modalidade]
      ,[data_inicio]
      ,[data_fim]
      ,[horario_inicio]
      ,[horario_fim]
      ,[data_atualizacao]
      ,[id_arquivo]
	FROM [cad_isento_ant] (nolock)




