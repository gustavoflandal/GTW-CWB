-- =============================================
-- Author:      Gustavoi Landal
-- Create date: 13 de setembro de 2025
-- Description: Procedure para buscar fatos pendentes (registros com dados faltantes)
-- =============================================

CREATE PROCEDURE [muralha].[sp_RelatorioFatosPendentes]
    @data_inicio DATETIME,
    @data_fim DATETIME
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        rf.id,
        rft.tipo_desc AS tipo,
        rfs.descricao AS status,
        su.nome AS usuario,
        rf.data_criacao,
        rf.data_encerramento,
        rf.privado,
        RTRIM(
            CASE WHEN rf.id_tipo IS NULL THEN 'Falta Tipo; ' ELSE '' END +
            CASE WHEN rf.id_status IS NULL THEN 'Falta Status; ' ELSE '' END +
            CASE WHEN rf.id_usuario IS NULL THEN 'Falta Usuário; ' ELSE '' END +
            CASE WHEN rf.data_criacao IS NULL THEN 'Falta Data Criação; ' ELSE '' END +
            CASE WHEN rfe.id IS NULL THEN 'Falta Endereço; ' ELSE '' END +
            CASE WHEN rfn.id IS NULL THEN 'Falta Natureza; ' ELSE '' END
        ) AS Faltas
    FROM muralha.registro_fato rf
    LEFT JOIN muralha.registro_fato_endereco rfe ON rf.id = rfe.id_registro_fato
    LEFT JOIN muralha.registro_fato_natureza rfn ON rf.id_tipo = rfn.id_registro_tipo
    LEFT JOIN muralha.registro_fato_tipo rft ON rf.id_tipo = rft.id
    LEFT JOIN muralha.registro_fato_status rfs ON rf.id_status = rfs.id
    LEFT JOIN dbo.sis_usuario su ON rf.id_usuario = su.id_usuario
    WHERE
        (rf.id_tipo IS NULL OR
         rf.id_status IS NULL OR
         rf.id_usuario IS NULL OR
         rf.data_criacao IS NULL OR
         rfe.id IS NULL OR
         rfn.id IS NULL)
        AND (rf.data_criacao BETWEEN @data_inicio AND @data_fim)
    ORDER BY rf.data_criacao DESC;

END
