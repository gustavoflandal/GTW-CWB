CREATE FUNCTION [dbo].[fcn_getRelatorioTotImagensDeEquip_PorEnquadramento]
  ( @dataInicio date,
    @dataFim date )
RETURNS TABLE
AS
 RETURN 
 ( 
	SELECT DISTINCT
		lv.serie_equipamento AS [CÓD. EQUIPAMENTO],
		cep.nome_pista AS [ENDEREÇO],
		CAST(inf.data AS DATE) AS [DATA],
		SUM(CASE WHEN inf.id_enquadramento = 1 THEN 1 ELSE 0 END) AS [IMAGEM TESTE],
		SUM(CASE WHEN inf.id_enquadramento = 56732 THEN 1 ELSE 0 END) AS [PARAR SOBRE A FAIXA DE PEDESTRES NA MUDANÇA DO SINAL LUMINOSO - FISC. ELETRÔNICA],
		SUM(CASE WHEN inf.id_enquadramento = 56900 THEN 1 ELSE 0 END) AS [TRANSITAR NA FAIXA/PISTA DA ESQUERDA REGUL. CIRCULAÇÃO EXCLUSIVA DETERM. VEÍCULO],
		SUM(CASE WHEN inf.id_enquadramento = 57030 THEN 1 ELSE 0 END) AS [DEIXAR DE CONSERVAR O VEÍC. NA FAIXA A ELE DEST. PELA SINAL. DE REGUL.],
		SUM(CASE WHEN inf.id_enquadramento = 57461 THEN 1 ELSE 0 END) AS [TRANS. EM LOCAL/HORÁRIO NÃO PERM. PELA REGUL. ESTAB. PELA AUT.],
		SUM(CASE WHEN inf.id_enquadramento = 57462 THEN 1 ELSE 0 END) AS [TRANS. EM LOCAL/HORÁRIO NÃO PERMITIDO PELA REGULAMENTAÇÃO - RODÍZIO],
		SUM(CASE WHEN inf.id_enquadramento = 57463 THEN 1 ELSE 0 END) AS [TRANS. EM LOCAL/HORÁRIO NÃO PERMITIDO PELA REGULAMENTAÇÃO - VEÍCULO DE CARGA],
		SUM(CASE WHEN inf.id_enquadramento = 60503 THEN 1 ELSE 0 END) AS [AVANÇAR O SINAL VERMELHO DO SEMÁFORO - FISCALIZAÇÃO ELETRÔNICA],
		SUM(CASE WHEN inf.id_enquadramento = 74550 THEN 1 ELSE 0 END) AS [TRANS. VELOC. SUPERIOR A MÁX. PERM. EM ATÉ DE 20%],
		SUM(CASE WHEN inf.id_enquadramento = 74630 THEN 1 ELSE 0 END) AS [TRANS. VELOC. SUPERIOR A MÁX. PERM. EM MAIS DE 20% ATÉ 50%],
		SUM(CASE WHEN inf.id_enquadramento = 74710 THEN 1 ELSE 0 END) AS [TRANS. VELOC. SUPERIOR A MÁX. PERM. EM MAIS DE 50%],
		COUNT(*) AS  TOTAL
	FROM local_vigente lv (nolock)
		INNER JOIN infracao inf  (nolock)
			ON inf.id_local = lv.id_local
		INNER JOIN configuracao_equipamento_pista cep (nolock)
			ON cep.id_configuracao_equipamento = lv.id_configuracao_equipamento 
			AND inf.pista = cep.id_pista
		INNER JOIN enquadramento en (nolock) 
			ON inf.id_enquadramento = en.id_enquadramento
	WHERE
		inf.data >= @dataInicio AND inf.data < @dataFim
	GROUP BY
		lv.serie_equipamento,
		cep.nome_pista,
		CAST(inf.data AS DATE)
)



