
CREATE TRIGGER [dbo].[TG_veiculo_estatistica_verificar_placa_mercosul]
ON [dbo].[veiculo_estatistica]
AFTER INSERT
AS
BEGIN
    UPDATE dbo.veiculo_estatistica
    SET placa_mercosul = CASE 
        WHEN LEN(i.placa) = 7 
        AND i.placa LIKE '[A-Z][A-Z][A-Z][0-9][A-Z][0-9][0-9]'
        THEN 1
        ELSE 0
    END
    FROM inserted i
    WHERE dbo.veiculo_estatistica.id_veiculo_unic = i.id_veiculo_unic;
END;
