/*
===============================================================================
Nome da Função: [muralha].[fcn_perfil_comportamental_base_locais_com_mancha]

Descrição:
    Retorna todos os locais (PCLs) vigentes com suas coordenadas geográficas,
    nome, e os dados da área monitorada associada (mancha), se houver.

Parâmetros:
    Nenhum

Retorno: Tabela
Autor: Thiago Guilsotti
Data de Criação: 28/07/2025
Versão: 1.1 - Aplicação de LTRIM/RTRIM para nomes

Observações:
    - Útil para enriquecer visualizações geoespaciais com contexto de monitoramento.
    - Os locais sem associação com área monitorada também são retornados (via LEFT JOIN).
	
Exemplo de uso:
    SELECT * FROM muralha.fcn_perfil_comportamental_base_locais_com_mancha();
===============================================================================
*/
CREATE   FUNCTION muralha.fcn_perfil_comportamental_base_locais_com_mancha()
RETURNS TABLE
AS
RETURN
(
    SELECT 
        DLV.id_local, 
        LTRIM(RTRIM(DLV.nome)) AS nome_local,
        DLV.posicao_lat AS lat, 
        DLV.posicao_lon AS lng,
        MAM.id_area_monitorada,
        LTRIM(RTRIM(MAM.nome_area_monitorada)) AS nome_area_monitorada
    FROM dbo.local_vigente DLV
    OUTER APPLY
    (
        SELECT TOP 1 
            MEAM.id_area_monitorada,
            MAM.nome AS nome_area_monitorada
        FROM muralha.equipamentos_area_monitorada MEAM
        INNER JOIN muralha.area_monitorada MAM
            ON MEAM.id_area_monitorada = MAM.id
        WHERE 
            MEAM.id_equipamento = DLV.id_local
            AND MAM.deletado = 0
    ) MAM
);