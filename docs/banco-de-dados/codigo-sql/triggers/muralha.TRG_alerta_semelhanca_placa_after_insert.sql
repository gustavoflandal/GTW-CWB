CREATE TRIGGER muralha.TRG_alerta_semelhanca_placa_after_insert
ON muralha.alerta
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Declaração de variáveis para armazenar os resultados da função
    DECLARE @com_semelhanca INT, @com_semelhanca_erros TINYINT, @com_semelhanca_desc VARCHAR(120), @placa_cad VARCHAR(7), @placa_veiculo VARCHAR(7);

    -- Obtém as placas e os resultados da função fn_CompararPlacas
    SELECT @placa_cad = c.placa,
		   @placa_veiculo = v.placa,
		   @com_semelhanca = COALESCE((SELECT semelhante FROM muralha.fn_CompararPlacas(c.placa, v.placa)), 0),
		   @com_semelhanca_erros = (SELECT diferencas FROM muralha.fn_CompararPlacas(c.placa, v.placa)),
		   @com_semelhanca_desc = (SELECT caracteres_diferentes FROM muralha.fn_CompararPlacas(c.placa, v.placa))
    FROM   inserted i
		   INNER JOIN muralha.cad_veiculo_monitorado c
				ON  c.id = i.id_cad_veiculo_monitorado
		   INNER JOIN muralha.alerta_veiculo av
				ON  av.id_alerta = i.id
		   INNER JOIN muralha.veiculo_tempo_real v
				ON  v.id = av.id_veiculo_tempo_real;

    -- Atualiza o registro inserido com os resultados
    UPDATE a
    SET    a.com_semelhanca = @com_semelhanca,
		   a.com_semelhanca_erros = @com_semelhanca_erros,
		   a.com_semelhanca_desc = @com_semelhanca_desc
    FROM   muralha.alerta a
		   INNER JOIN inserted i
				ON  a.id = i.id
    WHERE  @placa_cad IS NOT NULL AND @placa_veiculo IS NOT NULL;
END;
