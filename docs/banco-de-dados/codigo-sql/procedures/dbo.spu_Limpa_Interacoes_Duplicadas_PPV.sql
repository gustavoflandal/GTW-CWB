CREATE PROCEDURE [dbo].[spu_Limpa_Interacoes_Duplicadas_PPV]
AS

begin tran
delete from ppv_veiculos_interacao_XML_detalhes
where id_interacao in (

							select 
							d3.id_interacao
							from ppv_veiculos_interacao_XML_detalhes d3
							where d3.id_interacao in (
															select 
															d2.id_interacao
															from ppv_veiculos_interacao_XML_detalhes d2
															where d2.id_veiculo in (
																						select 
																						v.id_veiculo
																						from ppv_veiculo v
																						inner join ppv_veiculos_interacao_XML_detalhes d
																							on v.id_veiculo = d.id_veiculo
																						where v.id_local = 1005
																						--and placa  = 'CUD9560'
																						group by v.id_veiculo
																						having count(d.id_interacao) > 1
																					 )
														)
							group by d3.id_interacao
							having count(d3.id_veiculo) = 2
						)

commit

begin tran
delete from ppv_veiculos_interacoes_XML
where id_interacao in (

						select 
							i.id_interacao
							from ppv_veiculos_interacoes_XML i
							left join ppv_veiculos_interacao_XML_detalhes d
							on i.id_interacao = d.id_interacao
							where d.id_interacao is null
					 )

commit
