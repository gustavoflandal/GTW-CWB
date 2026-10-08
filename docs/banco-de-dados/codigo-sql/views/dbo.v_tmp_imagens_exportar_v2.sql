CREATE VIEW v_tmp_imagens_exportar_v2
AS
	SELECT i.imagem,
		   t.nome_arquivo
	FROM   temp_imagens_exportar_dados t
		   JOIN imagem i
				ON  i.id_imagem = t.id_imagem
