
CREATE FUNCTION [dbo].[fcn_ValidarGeracaoLM] (@id_remessa INT)

RETURNS TABLE AS RETURN 
(
	select 
		(erro +	'. Qtde Infracoes: ' +  
		cast(qtdeInfracoes as varchar(3)) + 
		CHAR(13) + Char(10)) as resultado

		from (
					--SELECT TOP(1) sub1.qtdeInfracoes, sub1.erro FROM 
					--(
					--SELECT 
					--	r.id_remessa,
					--	r.total_infracao - COUNT(ir.id_infracao) AS qtdeInfracoes,
					--	'Numero infracoes divergente' AS erro 
					--FROM remessa r (NOLOCK)
					--JOIN infracao_remessa ir (NOLOCK) ON r.id_remessa = ir.id_remessa
					--WHERE r.id_remessa = @id_remessa
					--GROUP BY r.id_remessa, r.total_infracao
					--) AS sub1

					--UNION ALL 

					SELECT 
						count(i.id_infracao) as qtdeInfracoes, 
						'Veiculo Grupo C sem Entre Faixa' as erro
					FROM infracao i (NOLOCK) 
					JOIN infracao_remessa ir (NOLOCK) ON i.id_infracao = ir.id_infracao
					JOIN remessa r (NOLOCK) ON ir.id_remessa = r.id_remessa
					JOIN veiculo v (NOLOCK) ON i.id_veiculo = v.id_veiculo
					WHERE r.id_remessa = @id_remessa AND 
					--i.data > '2016-11-21' AND
					i.id_local BETWEEN 7800 AND 7900 AND 
					v.entre_faixa IS NULL

					UNION ALL 

					-- DECLARE @id_remessa INT 
					select 
						count(id_infracao) as qtdeInfracoes, 
						'Imagens sem PLACA' as erro
					from fcn_ObterDadosRemessa(@id_remessa)
					where id_inconsistencia = 0
					and (placa = '' or placa is null)

					UNION ALL

					-- DECLARE @id_remessa INT 
					select 
						count(id_infracao) as qtdeInfracoes, 
						'Cód.Prodam zerados' as erro
					from fcn_ObterDadosRemessa(@id_remessa)
					where (cod_pista = 0 or cod_pista_prodam = 0)

					UNION ALL

					--select 
					--	count(i.id_infracao) as qtdeInfracoes, 
					--	'Prodam errado equip. Estáticos' as erro
					--from fcn_ObterDadosRemessa(@id_remessa) fc
					--inner join infracao i (NOLOCK)
					--	on i.id_infracao = fc.id_infracao
					--inner join (
					--			select 
					--				lv.id_local
					--			from local_vigente lv (NOLOCK)
					--				inner join configuracao_equipamento ce (NOLOCK)
					--					on ce.id_configuracao_equipamento = lv.id_configuracao_equipamento
					--				inner join produto p (NOLOCK)
					--					on p.id_produto = ce.id_produto
					--			where p.id_produto = 3
					--		) as estaticos
					--on estaticos.id_local = i.id_local
					--and cod_pista_prodam not in (select codigo_prodam from equipamento_estatico (NOLOCK))

					--UNION ALL

					--select 
					--	count(i.id_infracao) as qtdeInfracoes, 
					--	'Série Equipamento errado Estáticos' as erro
					--from fcn_ObterDadosRemessa(@id_remessa) fc
					--inner join infracao i (NOLOCK)
					--	on i.id_infracao = fc.id_infracao
					--inner join veiculo v (NOLOCK)
					--	ON i.id_veiculo = v.id_veiculo 
					--inner join (
					--			select 
					--				lv.id_local
					--			from local_vigente lv (NOLOCK)
					--				inner join configuracao_equipamento ce (NOLOCK)
					--					on ce.id_configuracao_equipamento = lv.id_configuracao_equipamento
					--				inner join produto p (NOLOCK)
					--					on p.id_produto = ce.id_produto
					--			where p.id_produto = 3
					--		) as estaticos
					--on estaticos.id_local = i.id_local
					--and v.serie_equipamento not in (select serie_equipamento from equipamento_estatico (NOLOCK))

					--UNION ALL

					-- DECLARE @id_remessa INT 
					--select 
					--	count(*) as qtdeInfracoes, 
					--	'Infrações/Veículos Duplicados' as erro
					--from (
					--				SELECT	
					--					id_imagem_local, 
					--					data, 
					--					id_local, 
					--					id_enquadramento, 
					--					COUNT(*) cnt
					--				FROM infracao i (NOLOCK)
					--				INNER JOIN infracao_remessa ir (NOLOCK) 
					--					ON i.id_infracao = ir.id_infracao
					--				WHERE 
					--					id_enquadramento > 1 
					--					AND i.id_processo <> 99
					--				GROUP BY 
					--					id_imagem_local, 
					--					data, 
					--					id_local, 
					--					id_enquadramento
					--				HAVING 
					--					COUNT(*) > 1
					--) as result

					--UNION ALL


					select 
						count(*) as qtdeInfracoes, 
						'Infrações/Veículos Duplicados - TARJA' as erro
					from (
							SELECT	
								id_imagem_local, 
								data, 
								id_enquadramento, 
								COUNT(*) cnt
							FROM infracao i (NOLOCK)
							WHERE 
								id_enquadramento > 1 
								AND i.id_processo <> 99
								AND i.data > '2018-06-22'
							GROUP BY 
								id_imagem_local, 
								data, 
								id_enquadramento
							HAVING 
								COUNT(*) > 1
					) as result

					UNION ALL

					--select 
					--	count(*) as qtdeInfracoes, 
					--	'Inconsistencia de informação' as erro
					-- from (
					--				select 
					--					LEN(r.sequencia) as tamSequencia, sequencia,
					--					LEN(r.id_imagem_local) as tamImagemLocal, r.id_imagem_local,
					--					LEN(r.id_enquadramento) as tamEnquadramento, r.id_enquadramento,
					--					LEN(r.cod_pista) as tamCodPista, r.cod_pista,
					--					LEN(r.nome_local) as tamNomeLocal, r.nome_local,
					--					LEN(r.cod_pista_prodam) as tamCodEquipamento, r.cod_pista_prodam,
					--					LEN(r.cod_operador) as tamOperador, r.cod_operador
					--				from fcn_ObterDadosRemessa(@id_remessa) r
					--				where 
					--					(LEN(r.sequencia) > 4 or r.sequencia = 0 or r.sequencia is null)  OR
					--					(LEN(r.id_imagem_local) > 7 or r.id_imagem_local = 0 or r.id_imagem_local is null) OR
					--					(LEN(r.id_enquadramento) > 5 or r.id_enquadramento = 0 or r.id_enquadramento is null) OR
					--					(LEN(r.cod_pista) > 4 or r.cod_pista = 0 or r.cod_pista is null) OR
					--					(LEN(r.nome_local) > 80 or r.nome_local = '' or r.nome_local is null) OR
					--					(LEN(r.cod_pista_prodam) > 4 or r.cod_pista_prodam = 0 or r.cod_pista_prodam is null) OR
					--					(LEN(r.cod_operador) > 6 or r.cod_operador = 0 or r.cod_operador is null) 
					--	) as validacoesGerais

					--UNION ALL

					-- DECLARE @id_remessa INT 
					select 
						count(r.id_infracao) as qtdeInfracoes, 
						'Enq.Velociade com dados zerado' as erro
					from fcn_ObterDadosRemessa(@id_remessa) r
					inner join enquadramento_regra_infracao eri (NOLOCK)
						on eri.id_enquadramento = r.id_enquadramento
					where (eri.tipo_apait = 'QV') and 
						(
							(r.velocidade = 0 or r.velocidade is null) OR
							(r.velocidade_considerada = 0 or r.velocidade_considerada is null) OR
							(r.velocidade_limite = 0 or r.velocidade_limite is null)
						)


					--UNION ALL

					-- DECLARE @id_remessa INT 
					--select 
					--	count(r.id_infracao) as qtdeInfracoes,
					--	'Obliteração zeradas ou nulas' as erro
					--from fcn_ObterDadosRemessa(@id_remessa) r
					--where r.com_obliteracao = 1
					--and r.id_inconsistencia = 0
					--and 
					--	(
					--		(r.x is null or r.y is null or r.altura is null or r.largura is null)  OR 
					--		(r.altura = 0 or r.largura = 0)
					--	)
			) as final
	where qtdeInfracoes > 0
)
