CREATE VIEW muralha.v_grupo_supervisionado
AS
    SELECT *
    FROM sis_grupo
    WHERE id_grupo IN (42, 11);