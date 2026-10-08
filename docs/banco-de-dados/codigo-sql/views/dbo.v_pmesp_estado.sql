
CREATE VIEW [dbo].[v_pmesp_estado] AS

SELECT ec.id_evento_conexao, ec.id_local, lv.nome, ec.data_conexao, sub2.desconexoes, 
COALESCE(pd.tempo_desconectado,0) AS tempo_offline,
ec.data_desconexao, ec.endereco_ip, ec.movimentos_recebidos, ec.movimentos_transmitidos, COALESCE(pa.tempo_medio,0) AS atraso, COALESCE(pa.tempo_maximo,0) AS maximo,
COALESCE(sub4.placa_lida,0) AS placa_lida, COALESCE(sub4.perda,0) AS perda, ec.data_ultimo_movimento, ec.data_atualizado 
FROM pmesp_evento_conexao ec (NOLOCK) 
JOIN (SELECT MAX(id_evento_conexao) id_evento_conexao FROM pmesp_evento_conexao (NOLOCK) WHERE id_local IS NOT NULL GROUP BY id_local) AS sub1
ON ec.id_evento_conexao = sub1.id_evento_conexao
JOIN local_vigente lv (NOLOCK) ON ec.id_local = lv.id_local
LEFT JOIN (SELECT id_local, COUNT(id_evento_conexao) desconexoes FROM pmesp_evento_conexao (NOLOCK) WHERE id_local IS NOT NULL AND data_conexao BETWEEN GETDATE() - 1 AND GETDATE() GROUP BY id_local) AS sub2
ON ec.id_local = sub2.id_local
LEFT JOIN pmesp_atraso pa ON ec.id_local = pa.id_local
LEFT JOIN (
SELECT pp.id_local, pp.placa_lida, pp.placa_enviada, (pp.placa_lida - pp.placa_enviada) AS perda FROM pmesp_perda pp 
) AS sub4 ON ec.id_local = sub4.id_local
LEFT JOIN pmesp_desconectado pd ON ec.id_local = pd.id_local
--ORDER BY 1 DESC sp_who3 
