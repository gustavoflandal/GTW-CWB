CREATE PROCEDURE [muralha].[spu_ObterAlertasMobilePorUsuario]
    @idUsuario INT,
    @diasParam INT = 7
AS
BEGIN
    SET NOCOUNT ON;

    -- Obter os tipos de alerta que o usuário tem permissão para receber
    DECLARE @tiposAlertaPermitidos TABLE (id_tipo_alerta_ocorrencia UNIQUEIDENTIFIER)

    INSERT INTO @tiposAlertaPermitidos
    SELECT DISTINCT cgp.id_tipo_alerta_ocorrencia
    FROM dbo.sis_usuario_grupo sug
    INNER JOIN dbo.sis_grupo sg ON sg.id_grupo = sug.id_grupo
    INNER JOIN muralha.config_grupo_permissao cgp ON cgp.id_grupo = sg.id_grupo
    WHERE sug.id_usuario = @idUsuario
      AND cgp.id_tipo_registro = 'E7D115B9-E6B3-4E86-9083-F347A1917045'
      AND cgp.id_tipo_notificacao = '778F443E-9514-44E0-BCD1-F953D91042BF'
      AND cgp.ativo = 1
      AND sg.config_muralha = 1

    -- Obter os alertas que o usuário deveria ter recebido
    SELECT 
        a.id AS AlertId,
        tao.tipo AS AlertType,
        tao.descricao_sms AS AlertDescription,
        cvm.placa AS Plate,
        a.data AS AlertDate,
        a.data_mobile AS MobileSentDate,
        sa.descricao AS AlertStatus,
        lv.nome AS EquipmentName,
        lv.serie_equipamento AS EquipmentSerial,
        DATEDIFF(MINUTE, a.data, GETDATE()) AS MinutesAgo,
        COUNT(*) OVER() AS TotalAlerts
    FROM muralha.alerta a
    INNER JOIN @tiposAlertaPermitidos tap ON tap.id_tipo_alerta_ocorrencia = a.id_tipo_alerta_ocorrencia
    INNER JOIN muralha.tipo_alerta_ocorrencia tao ON tao.id = a.id_tipo_alerta_ocorrencia
    INNER JOIN muralha.cad_veiculo_monitorado cvm ON cvm.id = a.id_cad_veiculo_monitorado
    INNER JOIN muralha.status_alerta sa ON sa.id = a.id_status_alerta
    INNER JOIN muralha.alerta_veiculo av ON av.id_alerta = a.id
    INNER JOIN muralha.veiculo_tempo_real vtr ON vtr.id = av.id_veiculo_tempo_real
    INNER JOIN local_vigente lv ON lv.id_local = vtr.id_local
    WHERE a.enviado_mobile = 1
      AND a.data_mobile >= DATEADD(DAY, -@diasParam, GETDATE())
      AND a.data_mobile IS NOT NULL
    ORDER BY a.data DESC
END;