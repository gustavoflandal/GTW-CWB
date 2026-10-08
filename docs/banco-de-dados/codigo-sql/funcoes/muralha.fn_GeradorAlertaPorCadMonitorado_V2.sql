CREATE FUNCTION [muralha].[fn_GeradorAlertaPorCadMonitorado_V2]
(
    @IdTipoAlertaOcorrencia UNIQUEIDENTIFIER,
    @PlacaEntrada CHAR(7)
)
RETURNS TABLE
AS
RETURN
(
	--DECLARE  @IdTipoAlertaOcorrencia UNIQUEIDENTIFIER = '0349F722-DFDE-4080-9E3B-D65F1C058EDC', @PlacaEntrada CHAR(7) = 'TDR7A52';
    WITH Posicoes AS
    (
        SELECT posicao FROM (VALUES (1),(2),(3),(4),(5),(6),(7)) AS P(posicao)
    ),
    Parametros AS
    (
        SELECT agora = GETDATE(), hoje = CAST(GETDATE() AS DATE)
    ),
    Configuracao AS
    (
        SELECT TOP (1)
			   erros_permitidos
        FROM   muralha.config_semelhanca_placa
        WHERE  data_exclusao IS NULL
        ORDER BY
			   data_cadastro DESC
    ),
    Cadastros AS
    (
        SELECT cad.id,
			   cad.placa,
			   cad.erros_permitidos_placa
        FROM   muralha.cad_veiculo_monitorado cad
			   CROSS JOIN Parametros p
        WHERE  cad.id_tipo_alerta_ocorrencia = @IdTipoAlertaOcorrencia
			   AND
			   (
					cad.data_fim IS NULL OR p.hoje BETWEEN cad.data_inicio AND cad.data_fim
			   )
			   AND
			   (
					cad.data_inativacao IS NULL OR p.agora <= cad.data_inativacao
			   )
    ),
    Validacao AS
    (
        SELECT c.id,
			   c.placa,
			   c.erros_permitidos_placa,
			   quantidade_erros =
					SUM
					(
						CASE
							WHEN SUBSTRING(@PlacaEntrada, p.posicao, 1)
							   <> SUBSTRING(c.placa, p.posicao, 1)
							THEN 1
							ELSE 0
						END
					),
			   diferencas_desc =
					STUFF
					(
						(
							SELECT
								'; Pos ' +
								CAST(p2.posicao AS VARCHAR(1)) +
								': ' +
								SUBSTRING(@PlacaEntrada, p2.posicao, 1) +
								' vs ' +
								SUBSTRING(c.placa, p2.posicao, 1)
							FROM Posicoes p2
							WHERE SUBSTRING(@PlacaEntrada, p2.posicao, 1)
							   <> SUBSTRING(c.placa, p2.posicao, 1)
							ORDER BY p2.posicao
							FOR XML PATH(''), TYPE
						).value('.', 'VARCHAR(120)'),
						1,
						2,
						''
					),
               possui_asterisco =
					MAX
					(
						CASE
							WHEN SUBSTRING(c.placa, p.posicao, 1) = '*'
							THEN 1
							ELSE 0
						END
					),
               diferencas_parcial =
					SUM
					(
						CASE
							WHEN SUBSTRING(c.placa, p.posicao, 1) <> '*'
							 AND SUBSTRING(@PlacaEntrada, p.posicao, 1)
								 <> SUBSTRING(c.placa, p.posicao, 1)
							THEN 1
							ELSE 0
						END
					)

        FROM   Cadastros c
			   CROSS JOIN Posicoes p
        GROUP BY
			   c.id,
			   c.placa,
			   c.erros_permitidos_placa
    )


    SELECT id_cad_veic = v.id,
		   placa_cad = v.placa,
		   placa_entrada = @PlacaEntrada,
		   erros = CASE WHEN v.possui_asterisco = 1 THEN 0 ELSE v.quantidade_erros END,
		   com_semelhanca = CASE WHEN v.possui_asterisco = 0 AND v.quantidade_erros > 0 THEN 1 ELSE NULL END,
		   com_semelhanca_erros = CASE WHEN v.possui_asterisco = 0 AND v.quantidade_erros > 0 THEN v.quantidade_erros ELSE NULL END,
		   com_semelhanca_desc = CASE WHEN v.possui_asterisco = 0 AND v.quantidade_erros > 0 THEN v.diferencas_desc ELSE NULL END
    FROM   Validacao v
		   CROSS JOIN Configuracao cfg
    WHERE  (
				-- PLACA PARCIAL
				v.possui_asterisco = 1 AND v.diferencas_parcial = 0
           )
           OR
           (
				-- PLACA NORMAL
				v.possui_asterisco = 0 AND v.erros_permitidos_placa IS NOT NULL AND v.quantidade_erros <= v.erros_permitidos_placa
           )
           OR
           (
				-- PLACA NORMAL usando configuração global
				v.possui_asterisco = 0 AND v.erros_permitidos_placa IS NULL AND cfg.erros_permitidos IS NOT NULL AND v.quantidade_erros <= cfg.erros_permitidos
           )
);
