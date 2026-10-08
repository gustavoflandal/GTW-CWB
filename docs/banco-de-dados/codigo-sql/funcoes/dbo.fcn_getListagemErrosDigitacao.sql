CREATE FUNCTION [dbo].[fcn_getListagemErrosDigitacao](@dataInicio DATE, @dataFim DATE)
RETURNS TABLE
AS
RETURN
(

	select
		i.id_infracao as [Cód. Infração],
		i.data as [Data Registro],
		ip_dig.data as [Data Dig.],
		RTRIM(dig_u.nome) as [Usuário Dig.],
		RTRIM(inc_dig.descricao) as [Critério Dig.],
		ipd_dig.placa as [Placa Dig.],
		CASE 
			WHEN ip_dig_final.id_infracao_processo IS NOT NULL 
				THEN 1 
		END AS [Foi Escolhido],
		ip_val.data as [Data Val.],
		RTRIM(val_u.nome) as [Usuário Val.],
		RTRIM(inc_val.descricao) as [Critério Val.],
		ipd_val.placa as [Placa Val.]
	from infracao i (nolock)
		-- digitação
		INNER JOIN infracao_processo ip_dig (nolock)
			ON	ip_dig.id_infracao = i.id_infracao 
			AND ip_dig.id_processo IN (2,24) -- digitação 
			AND ip_dig.status_processo = 0 -- processado
		INNER JOIN infracao_processo_usuario ipu_dig (nolock)
			ON	ipu_dig.id_infracao = i.id_infracao
			AND ipu_dig.id_infracao_processo = ip_dig.id_infracao_processo
			AND ipu_dig.id_processo = ip_dig.id_processo
			AND ipu_dig.id_usuario = ip_dig.id_usuario
		LEFT JOIN infracao_processo ip_dig_final (nolock)
			ON	ip_dig_final.id_infracao_processo = ip_dig.id_infracao_processo
			AND ip_dig_final.id_infracao_processo = (SELECT MAX(id_infracao_processo) 
														FROM infracao_processo (nolock)
														WHERE	id_infracao = i.id_infracao 
															AND id_processo IN (2,24)  -- digitação
															AND status_processo = 0)
		LEFT JOIN infracao_processo_digitacao ipd_dig (nolock)
			ON ipd_dig.id_infracao_processo = ip_dig.id_infracao_processo
		INNER JOIN sis_usuario dig_u (nolock)
			ON dig_u.id_usuario = ip_dig.id_usuario 
		INNER JOIN inconsistencia inc_dig (nolock)
			ON inc_dig.id_inconsistencia = ip_dig.id_inconsistencia
			
		-- validacao
		INNER JOIN infracao_processo ip_val (nolock)
			ON	ip_val.id_infracao = i.id_infracao 
			AND ip_val.id_processo = 3 -- validação	
			AND ip_val.status_processo = 0 -- processado
		INNER JOIN infracao_processo_usuario ipu_val (nolock)
			ON	ipu_val.id_infracao = i.id_infracao
			AND ipu_val.id_infracao_processo = ip_val.id_infracao_processo
			AND ipu_val.id_processo = ip_val.id_processo
			AND ipu_val.id_usuario = ip_val.id_usuario
		LEFT JOIN infracao_processo_digitacao ipd_val (nolock)
			ON ipd_val.id_infracao_processo = ip_val.id_infracao_processo		
		JOIN sis_usuario val_u (nolock)
			on val_u.id_usuario = ip_val.id_usuario 
		JOIN inconsistencia inc_val (nolock)
			ON inc_val.id_inconsistencia = ip_val.id_inconsistencia
	WHERE
		(
			(ipd_dig.placa <> ipd_val.placa AND ipd_dig.placa IS NOT NULL AND ipd_val.placa is NOT NULL ) OR --placas diferentes
			( ip_dig.id_inconsistencia <> ip_val.id_inconsistencia ) -- inconsistencias diferentes
		)
		AND ip_val.id_inconsistencia <> 9
		AND ip_val.data >= CAST(@dataInicio AS DATETIME) AND ip_val.data < CAST(@dataFim AS DATETIME) + 1
)



