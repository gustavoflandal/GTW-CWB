
CREATE VIEW [dbo].[descarga_descarga] AS
SELECT id_descarga,
      dia_inicio,
      dia_fim,
      data_criacao,
      total_veiculos,
      total_imagens,
      total_infracoes,
      id_usuario,
      data_confirmacao,
      id_usuario_confirmacao,
      (select valor 
			from chave_valor (nolock) 
			where chave = 'identificacao_cliente') as identificacao_cliente
  FROM descarga (nolock)


