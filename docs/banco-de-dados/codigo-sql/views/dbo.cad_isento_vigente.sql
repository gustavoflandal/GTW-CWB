
CREATE VIEW [dbo].[cad_isento_vigente]
AS
	select ci.placa, ci.id_enquadramento, ci.data_inicio, ci.data_fim, ci.horario_inicio, ci.horario_fim, ci.area, ci.id_arquivo 
		from cad_isento ci (nolock)
			join (select placa, id_enquadramento, MAX(id_arquivo) as ultimo_arquivo 
					from cad_isento (nolock)
					group by placa, id_enquadramento
					having (id_enquadramento <> 57463 
						OR (MAX(id_arquivo) = (SELECT MAX(cia.id_arquivo) 
													FROM cad_isento_arquivo cia (nolock)
													JOIN cad_arquivos_importados cai (NOLOCK) ON cia.id_arquivo = cai.id
													WHERE cia.id_enquadramento = 57463 AND cai.data_importacao IS NOT NULL
											  )
						   )
						   )
				 ) _ci 
			on ci.placa = _ci.placa 
			and ci.id_enquadramento = _ci.id_enquadramento 
			and ci.id_arquivo = _ci.ultimo_arquivo




