
CREATE FUNCTION [muralha].[fn_GeradorAlertaPorCadMonitorado_TESTE]
(
    @IdTipoAlertaOcorrencia UNIQUEIDENTIFIER,
    @PlacaEntrada CHAR(7)
)
RETURNS @RETORNO TABLE
(
    id_cad_veic UNIQUEIDENTIFIER,
    placa_cad VARCHAR(7),
    placa_entrada VARCHAR(7),
    erros INT,
    com_semelhanca INT,
    com_semelhanca_erros INT,
    com_semelhanca_desc VARCHAR(120)
)
AS
BEGIN
    DECLARE @ErrosPermitidosPadrao INT;

    SELECT TOP 1
        @ErrosPermitidosPadrao = erros_permitidos
    FROM muralha.config_semelhanca_placa
    WHERE data_exclusao IS NULL
    ORDER BY data_cadastro DESC;

    INSERT INTO @RETORNO
    (
        id_cad_veic,
        placa_cad,
        placa_entrada,
        erros,
        com_semelhanca,
        com_semelhanca_erros,
        com_semelhanca_desc
		)
    SELECT
        cad.id,
        cad.placa,
        @PlacaEntrada,

        calc.erros,
        CASE
            WHEN calc.erros > 0 THEN 1
            ELSE NULL
        END AS com_semelhanca,
        CASE
            WHEN calc.erros > 0 THEN calc.erros
            ELSE NULL
        END AS com_semelhanca_erros,
        CASE
            WHEN calc.erros > 0 THEN calc.diferencas_desc
            ELSE NULL
        END AS com_semelhanca_desc
    FROM muralha.cad_veiculo_monitorado cad

    CROSS APPLY
    (
        SELECT
            erros =
                CASE
                    WHEN SUBSTRING(@PlacaEntrada, 1, 1)
                       <> SUBSTRING(cad.placa, 1, 1)
                    THEN 1 ELSE 0
                END
                +
                CASE
                    WHEN SUBSTRING(@PlacaEntrada, 2, 1)
                       <> SUBSTRING(cad.placa, 2, 1)
                    THEN 1 ELSE 0
                END
                +
                CASE
                    WHEN SUBSTRING(@PlacaEntrada, 3, 1)
                       <> SUBSTRING(cad.placa, 3, 1)
                    THEN 1 ELSE 0
                END
                +
                CASE
                    WHEN SUBSTRING(@PlacaEntrada, 4, 1)
                       <> SUBSTRING(cad.placa, 4, 1)
                    THEN 1 ELSE 0
                END
                +
                CASE
                    WHEN SUBSTRING(@PlacaEntrada, 5, 1)
                       <> SUBSTRING(cad.placa, 5, 1)
                    THEN 1 ELSE 0
                END
                +
                CASE
                    WHEN SUBSTRING(@PlacaEntrada, 6, 1)
                       <> SUBSTRING(cad.placa, 6, 1)
                    THEN 1 ELSE 0
                END
                +
                CASE
                    WHEN SUBSTRING(@PlacaEntrada, 7, 1)
                       <> SUBSTRING(cad.placa, 7, 1)
                    THEN 1 ELSE 0
                END,
            diferencas_desc =
                STUFF(
                    (
                        CASE
                            WHEN SUBSTRING(@PlacaEntrada, 1, 1)
                               <> SUBSTRING(cad.placa, 1, 1)
                            THEN
                                '; Pos 1: '
                                + SUBSTRING(@PlacaEntrada, 1, 1)
                                + ' vs '
                                + SUBSTRING(cad.placa, 1, 1)
                            ELSE ''
                        END

                        +
                        CASE
                            WHEN SUBSTRING(@PlacaEntrada, 2, 1)
                               <> SUBSTRING(cad.placa, 2, 1)
                            THEN
                                '; Pos 2: '
                                + SUBSTRING(@PlacaEntrada, 2, 1)
                                + ' vs '
                                + SUBSTRING(cad.placa, 2, 1)
                            ELSE ''
                        END

                        +
                        CASE
                            WHEN SUBSTRING(@PlacaEntrada, 3, 1)
                               <> SUBSTRING(cad.placa, 3, 1)
                            THEN
                                '; Pos 3: '
                                + SUBSTRING(@PlacaEntrada, 3, 1)
                                + ' vs '
                                + SUBSTRING(cad.placa, 3, 1)
                            ELSE ''
                        END

                        +
                        CASE
                            WHEN SUBSTRING(@PlacaEntrada, 4, 1)
                               <> SUBSTRING(cad.placa, 4, 1)
                            THEN
                                '; Pos 4: '
                                + SUBSTRING(@PlacaEntrada, 4, 1)
                                + ' vs '
                                + SUBSTRING(cad.placa, 4, 1)
                            ELSE ''
                        END

                        +
                        CASE
                            WHEN SUBSTRING(@PlacaEntrada, 5, 1)
                               <> SUBSTRING(cad.placa, 5, 1)
                            THEN
                                '; Pos 5: '
                                + SUBSTRING(@PlacaEntrada, 5, 1)
                                + ' vs '
                                + SUBSTRING(cad.placa, 5, 1)
                            ELSE ''
                        END

                        +
                        CASE
                            WHEN SUBSTRING(@PlacaEntrada, 6, 1)
                               <> SUBSTRING(cad.placa, 6, 1)
                            THEN
                                '; Pos 6: '
                                + SUBSTRING(@PlacaEntrada, 6, 1)
                                + ' vs '
                                + SUBSTRING(cad.placa, 6, 1)
                            ELSE ''
                        END

                        +
                        CASE
                            WHEN SUBSTRING(@PlacaEntrada, 7, 1)
                               <> SUBSTRING(cad.placa, 7, 1)
                            THEN
                                '; Pos 7: '
                                + SUBSTRING(@PlacaEntrada, 7, 1)
                                + ' vs '
                                + SUBSTRING(cad.placa, 7, 1)
                            ELSE ''
                        END
                    ), 1, 2, ''
                )
    ) calc
    CROSS APPLY
    (
        SELECT
            erros_permitidos =
                COALESCE(
                    cad.erros_permitidos_placa,
                    @ErrosPermitidosPadrao
                )
    ) config
    WHERE
        cad.id_tipo_alerta_ocorrencia = @IdTipoAlertaOcorrencia
        AND CHARINDEX('*', cad.placa) = 0
        AND
        (
            cad.data_fim IS NULL
            OR CAST(GETDATE() AS DATE)
               BETWEEN cad.data_inicio AND cad.data_fim
        )
        AND
        (
            cad.data_inativacao IS NULL
            OR GETDATE() <= cad.data_inativacao
        )
        AND config.erros_permitidos IS NOT NULL
        AND calc.erros <= config.erros_permitidos;
		INSERT INTO @RETORNO
    (
        id_cad_veic,
        placa_cad,
        placa_entrada,
        erros,
        com_semelhanca,
        com_semelhanca_erros,
        com_semelhanca_desc
    )
    SELECT
        cad.id,
        cad.placa,
        @PlacaEntrada,
        validacao.resultado,
        validacao.semelhante,
        validacao.diferencas,
        validacao.diferencas_desc
    FROM muralha.cad_veiculo_monitorado cad
    CROSS APPLY
    (
        SELECT
            resultado,
            semelhante,
            diferencas,
            diferencas_desc
        FROM muralha.fn_ValidarPlacasParciaisIguais_V2
        (
            @PlacaEntrada,
            cad.placa
        )
    ) validacao
    WHERE
        cad.id_tipo_alerta_ocorrencia = @IdTipoAlertaOcorrencia
        AND CHARINDEX('*', cad.placa) > 0
        AND
        (
            cad.data_fim IS NULL
            OR CAST(GETDATE() AS DATE)
               BETWEEN cad.data_inicio AND cad.data_fim
        )
        AND
        (
            cad.data_inativacao IS NULL
            OR GETDATE() <= cad.data_inativacao
        )
        AND validacao.resultado >= 0;
    RETURN;

END
