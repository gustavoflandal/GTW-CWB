CREATE FUNCTION [dbo].[fcn_getRelatorioDetecInv](@dataInicio DATE, @dataFim DATE)  
RETURNS TABLE  
AS  
RETURN  
(  

--DECLARE @dataInicio DATE = '2021-06-04', @dataFim DATE = '2021-06-14'
SELECT 
sub1.data_hora, CAST(sub1.proprietario AS INT) AS serie_equipamento, COALESCE(dbo.TryConvertInt(sub1.pista),0) AS pista, 
sub1.descarte collate SQL_Latin1_General_Cp1251_CS_AS AS [Motivo Descarte],
--sub1.descarte,
CASE WHEN ISNUMERIC(sub1.velocidade)=1 THEN CAST(sub1.velocidade AS INT) ELSE 0 END AS velocidade, sub1.delta
 FROM 
(
SELECT ev.data_hora, evp.proprietario, 

CASE WHEN ISNUMERIC(SUBSTRING(ev.mensagem,19,1))=1 THEN SUBSTRING(ev.mensagem,19,1) ELSE
CASE WHEN ISNUMERIC(SUBSTRING(ev.mensagem,27,1))=1 THEN SUBSTRING(ev.mensagem,27,1) ELSE 
CASE WHEN ISNUMERIC(SUBSTRING(ev.mensagem,39,1))=1 THEN SUBSTRING(ev.mensagem,39,1) ELSE 
CASE WHEN ISNUMERIC(SUBSTRING(ev.mensagem,42,1))=1 THEN SUBSTRING(ev.mensagem,42,1) ELSE 
CASE WHEN ISNUMERIC(SUBSTRING(ev.mensagem,51,1))=1 THEN SUBSTRING(ev.mensagem,51,1) ELSE 
CASE WHEN ISNUMERIC(SUBSTRING(ev.mensagem,53,1))=1 THEN SUBSTRING(ev.mensagem,53,1) ELSE 
CASE WHEN ISNUMERIC(SUBSTRING(ev.mensagem,62,1))=1 THEN SUBSTRING(ev.mensagem,62,1) ELSE
'0' END END END END END END END AS pista,

CASE WHEN ev.mensagem LIKE 'Descarte%' THEN SUBSTRING(ev.mensagem,22,LEN(ev.mensagem)-21) ELSE 
CASE WHEN ev.mensagem LIKE 'Veículo inválido%' THEN SUBSTRING(ev.mensagem,31,LEN(ev.mensagem)-30) ELSE
CASE WHEN ev.mensagem LIKE 'Perfis dos sensores não correspondem ao mesmo veículo%' THEN SUBSTRING(ev.mensagem,0,54) ELSE
CASE WHEN ev.mensagem LIKE 'Perfil não corresponde à velocidade medida; pista %' THEN SUBSTRING(ev.mensagem,0,43) ELSE
CASE WHEN ev.mensagem LIKE 'Velocidade: % não exibida no DIV. Pista: %' THEN SUBSTRING(ev.mensagem,44,LEN(ev.mensagem)-43) ELSE
CASE WHEN ev.mensagem LIKE 'Velocidade:%' THEN 'Velocidade exibida no DIV' ELSE
CASE WHEN ev.mensagem LIKE 'Erro - MonitorVeiculo - Veiculo descartado na pista %. Tempo de acionamento do laco menor que o minimo permitido. %' THEN SUBSTRING(ev.mensagem,56,57) ELSE
CASE WHEN ev.mensagem LIKE 'Erro - MonitorVeiculo - Veiculo descartado na pista %. Diferenca entre deltas maior que 15%. %' THEN SUBSTRING(ev.mensagem,56,36) ELSE
ev.mensagem END END END END END END END END AS descarte,

CASE WHEN ev.mensagem LIKE 'Velocidade:%' THEN SUBSTRING(ev.mensagem,13,2) ELSE '0' END AS velocidade,

CASE WHEN ev.mensagem LIKE 'Erro - MonitorVeiculo - Veiculo descartado na pista %. Tempo de acionamento do laco menor que o minimo permitido. %' AND LEN(ev.mensagem) > 100 THEN
SUBSTRING(ev.mensagem,115,LEN(ev.mensagem)-114) ELSE
CASE WHEN ev.mensagem LIKE 'Erro - MonitorVeiculo - Veiculo descartado na pista%' AND LEN(ev.mensagem) > 100 THEN
SUBSTRING(ev.mensagem,94,LEN(ev.mensagem)-93) ELSE 'N/D' END END AS delta

FROM eventos_csx ev (NOLOCK)
JOIN eventos_csx_desc_proprietario evp (NOLOCK) ON ev.id_proprietario = evp.id_proprietario
WHERE CAST(ev.data_hora AS DATE) BETWEEN @dataInicio AND @dataFim
AND ev.id_evento = 24 AND ISNUMERIC(evp.proprietario)=1 AND LEN(LTRIM(RTRIM(evp.proprietario)))>0
)
AS sub1

);
