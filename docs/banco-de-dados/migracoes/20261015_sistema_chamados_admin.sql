-- =============================================================================
-- Complemento: Sistema de Gerenciamento de Chamados (Plano 22) — administracao e categoria
-- Data: 2026-10-15 — idempotente. Executar depois de 20261015_sistema_chamados_seed.sql.
-- ROLLBACK:
--   DELETE FROM dbo.sis_menu_direitos WHERE id_menu = 383;
--   DELETE FROM dbo.sis_menu_infos    WHERE id_infos = 49;
--   DELETE FROM dbo.sis_menu          WHERE id_menu = 383;
--   UPDATE dbo.sis_menu_infos SET menu_pai = 16, ordenacao = 11,
--          src = '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg' WHERE id_infos = 46;
--   UPDATE dbo.sis_menu_infos SET menu_pai = 15, ordenacao = 7,
--          src = '/muralha-digital/assets/images/analise/analise.jpg' WHERE id_infos = 47;
--   UPDATE dbo.sis_menu_infos SET menu_pai = 16, ordenacao = 12,
--          src = '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg' WHERE id_infos = 48;
--   INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
--     SELECT CAST(valor AS INT), NULL, 382 FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_equipe';
--   UPDATE dbo.sis_grupo SET id_grupo_pai = NULL WHERE id_grupo IN (SELECT CAST(valor AS INT)
--          FROM muralha.config_chave_valor WHERE chave IN ('chamado_grupo_solicitante','chamado_grupo_gestor','chamado_grupo_equipe'));
--   DELETE FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_admin';
-- =============================================================================

-- 1) Grupo administrador (Administradores = 11) e hierarquia dos grupos de perfil
IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_admin')
    INSERT INTO muralha.config_chave_valor (chave, valor) VALUES ('chamado_grupo_admin', '11');

-- Grupos abaixo do grupo 11 aparecem na tela legada de usuarios (GWT)
UPDATE dbo.sis_grupo SET id_grupo_pai = 11
WHERE id_grupo IN (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor
                   WHERE chave IN ('chamado_grupo_solicitante','chamado_grupo_gestor','chamado_grupo_equipe'))
  AND (id_grupo_pai IS NULL OR id_grupo_pai <> 11);
PRINT 'Grupo admin e hierarquia verificados';
GO

-- 2) Categoria, cards e direitos (lote unico: o RETURN interrompe tudo se os IDs ja estiverem ocupados)
IF EXISTS (SELECT 1 FROM dbo.sis_menu WHERE id_menu = 383 AND acao NOT LIKE '%menu_info_pai=49%')
   OR EXISTS (SELECT 1 FROM dbo.sis_menu_infos WHERE id_infos = 49 AND href NOT LIKE '%menu_info_pai=49%')
BEGIN
    RAISERROR('sis_menu 383 ou sis_menu_infos 49 ja usados por outro modulo. Ajuste este script e ReferenciaEdital.java.', 16, 1);
    RETURN;
END

DECLARE @gSol INT = (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_solicitante');
DECLARE @gGes INT = (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_gestor');
DECLARE @gEqu INT = (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_equipe');
DECLARE @gAdm INT = (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_admin');

INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
SELECT 383, N'Sistema de Chamados', '/login/muralha_principal.jsp?menu_info_pai=49', 'U', 1, 222,
       N'Sistema de Chamados', 1, 'Muralha-Digital'
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu m WHERE m.id_menu = 383);

INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
SELECT 49, 383, '/muralha-digital/assets/images/menu-icons/chamados-categoria.svg', N'Sistema de Chamados',
       N'/login/muralha_principal.jsp?menu_info_pai=49&desc=Sistema de Chamados', N'Central de suporte técnico', 10, NULL
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu_infos i WHERE i.id_infos = 49);

-- Cards 46/47/48 passam para a categoria 49 (UPDATE e naturalmente idempotente)
UPDATE i SET menu_pai = 49, ordenacao = v.ord, src = v.src
FROM dbo.sis_menu_infos i
JOIN (VALUES (46, 1, '/muralha-digital/assets/images/menu-icons/chamados.svg'),
             (47, 2, '/muralha-digital/assets/images/menu-icons/chamados-relatorios.svg'),
             (48, 3, '/muralha-digital/assets/images/menu-icons/chamados-configuracao.svg')) v(id_infos, ord, src)
  ON v.id_infos = i.id_infos
WHERE i.menu_pai IS NULL OR i.menu_pai <> 49 OR i.ordenacao <> v.ord OR i.src <> v.src;

-- Direitos da categoria 383: todos os perfis e o administrador
INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT v.g, NULL, 383
FROM (VALUES (@gSol), (@gGes), (@gEqu), (@gAdm)) v(g)
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos d WHERE d.id_grupo = v.g AND d.id_menu = 383);

-- Configuracao (382): somente administrador
DELETE FROM dbo.sis_menu_direitos WHERE id_menu = 382 AND id_grupo <> @gAdm;
INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT @gAdm, NULL, 382
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos d WHERE d.id_grupo = @gAdm AND d.id_menu = 382);
PRINT 'Categoria, cards e direitos verificados';
GO
