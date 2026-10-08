
CREATE TRIGGER [TG_veiculo_tempo_real_verificar_placa_mercosul]
ON [muralha].[veiculo_tempo_real]
AFTER INSERT
AS
BEGIN
    UPDATE muralha.veiculo_tempo_real
    SET placa_mercosul = CASE 
        WHEN LEN(i.placa) = 7 
        AND i.placa LIKE '[A-Z][A-Z][A-Z][0-9][A-Z][0-9][0-9]'
        THEN 1
        ELSE 0
    END
    FROM inserted i
    WHERE muralha.veiculo_tempo_real.id = i.id;
END;
