
CREATE TRIGGER [dbo].[TG_veiculo_estatistica_verifica_classificacao]
ON [dbo].[veiculo_estatistica]
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    UPDATE ve
    SET classificacao = 
        CASE 
            WHEN i.com_pesagem = 1 THEN NULL
            WHEN i.comprimento > 1.0 THEN
                CASE 
                    WHEN i.comprimento < 3.0 THEN 'Automotor'
                    WHEN i.comprimento < 6.0 THEN 'Automotor Passeio'
                    ELSE
                        CASE 
                            WHEN i.id_classe = 'O' THEN 'Automotor Ônibus'
                            ELSE 'Automotor Caminhão'
                        END
                END
            ELSE 'N.A.'
        END
    FROM veiculo_estatistica ve (NOLOCK)
    INNER JOIN inserted i ON ve.id_veiculo_unic = i.id_veiculo_unic;
END;
