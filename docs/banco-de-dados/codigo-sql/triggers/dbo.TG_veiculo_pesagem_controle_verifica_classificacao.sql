
CREATE TRIGGER [TG_veiculo_pesagem_controle_verifica_classificacao]
ON [dbo].[veiculo_pesagem_controle]
AFTER INSERT
AS
BEGIN
    SET NOCOUNT ON;

    -- Atualizar a tabela veiculo_estatistica
    UPDATE ve
    SET classificacao = 
        CASE 
            WHEN ve.comprimento > 1.0 THEN
                CASE 
                    WHEN ve.comprimento < 3.0 THEN 'Automotor'
                    WHEN ve.comprimento < 6.0 THEN 'Automotor Passeio'
                    ELSE
                        CASE 
                            WHEN ve.id_classe = 'O' THEN 'Automotor Ônibus'
                            ELSE 'Automotor Caminhão' + CASE WHEN (SELECT COUNT(*) FROM veiculo_pesagem_eixo vpe (NOLOCK) WHERE vpe.id_veiculo_unic = i.id_veiculo_unic) > 2  THEN ' com reboque | semi-reboque' ELSE '' END
                        END
                END
            ELSE 'N.A.'
        END
    FROM veiculo_estatistica ve
    INNER JOIN inserted i ON ve.id_veiculo_unic = i.id_veiculo_unic;

    -- Atualizar a tabela veiculo
    UPDATE v
    SET classificacao = 
        CASE 
            WHEN v.comprimento > 1.0 THEN
                CASE 
                    WHEN v.comprimento < 3.0 THEN 'Automotor'
                    WHEN v.comprimento < 6.0 THEN 'Automotor Passeio'
                    ELSE
                        CASE 
                            WHEN v.id_classe = 'O' THEN 'Automotor Ônibus'
                            ELSE 'Automotor Caminhão' + CASE WHEN (SELECT COUNT(*) FROM veiculo_pesagem_eixo vpe (NOLOCK) WHERE vpe.id_veiculo_unic = i.id_veiculo_unic) > 2 THEN ' com reboque | semi-reboque' ELSE '' END
                        END
                END
            ELSE 'N.A.'
        END
    FROM veiculo v
    INNER JOIN inserted i ON v.id_veiculo_unic = i.id_veiculo_unic;
END;