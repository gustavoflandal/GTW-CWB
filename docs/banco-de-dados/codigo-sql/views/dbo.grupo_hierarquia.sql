
CREATE view [dbo].[grupo_hierarquia] AS
WITH arvore_grupo (id_grupo, descricao, id_grupo_pai, Level)
AS
(
SELECT g.id_grupo, g.descricao, g.id_grupo_pai, 0 AS Level
	FROM sis_grupo g (nolock)
	WHERE g.id_grupo_pai IS NULL
UNION ALL
SELECT g.id_grupo, g.descricao, g.id_grupo_pai, Level + 1
	FROM sis_grupo g (nolock)
		INNER JOIN arvore_grupo AS ag
			ON g.id_grupo_pai = ag.id_grupo
)
SELECT id_grupo, descricao, Level
FROM arvore_grupo


