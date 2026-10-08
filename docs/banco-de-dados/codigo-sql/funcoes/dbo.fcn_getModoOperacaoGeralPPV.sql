CREATE FUNCTION [dbo].[fcn_getModoOperacaoGeralPPV]()
RETURNS TABLE
AS
RETURN
(
	--SELECT * FROM ppv_tp_ocorrencia
	SELECT po.id_ocorrencia,
		   po.id_SAI,
		   pto.id_tpocorrencia,
		   pto.descricao AS tipo_ocorrencia,
		   po.data,
		   po.observacoes,
		   po.id_veiculo_interno
		   --CASE WHEN pto.id_tpocorrencia = 4 THEN (SELECT id_tpocorrencia FROM fcn_getModoOperacaoPesagemPPV()) ELSE 4 END AS modo_operacao_acao,
		   --CASE WHEN pto.id_tpocorrencia = 4 THEN (SELECT tipo_ocorrencia FROM fcn_getModoOperacaoPesagemPPV()) ELSE 'Liberação de veículo' END AS desc_modo_operacao_acao
	FROM   ppv_ocorrencias po (NOLOCK)
		   INNER JOIN ppv_tp_ocorrencia pto (NOLOCK)
				ON  pto.id_tpocorrencia = po.id_tpocorrencia
		   INNER JOIN (
						SELECT MAX(po_aux.id_ocorrencia) AS id_ocorrencia
						FROM   ppv_ocorrencias po_aux (NOLOCK)
						WHERE  po_aux.id_tpocorrencia IN (1,2,4,5)
		   ) modo_geral
				ON  modo_geral.id_ocorrencia = po.id_ocorrencia
	WHERE  po.id_tpocorrencia IN (1,2,4,5)
	--ORDER BY
	--	   po.id_ocorrencia DESC
)
