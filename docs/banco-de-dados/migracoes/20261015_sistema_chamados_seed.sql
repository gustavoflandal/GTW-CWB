-- =============================================================================
-- Seed: Sistema de Gerenciamento de Chamados (Plano 22)
-- Data: 2026-10-15 — idempotente. Executar depois de 20261015_sistema_chamados.sql.
-- ROLLBACK:
--   DELETE FROM dbo.sis_menu_direitos WHERE id_menu BETWEEN 379 AND 382;
--   DELETE FROM dbo.sis_menu_infos    WHERE id_infos BETWEEN 46 AND 48;
--   DELETE FROM dbo.sis_menu          WHERE id_menu BETWEEN 379 AND 382;
--   DELETE FROM dbo.sis_usuario_grupo WHERE id_grupo IN (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor
--          WHERE chave IN ('chamado_grupo_solicitante','chamado_grupo_gestor','chamado_grupo_equipe'));
--   DELETE FROM dbo.sis_grupo WHERE RTRIM(descricao) IN
--          ('Chamados - Solicitante','Chamados - Gestor do contrato','Chamados - Equipe tecnica');
--   DELETE FROM muralha.config_chave_valor WHERE chave LIKE 'chamado[_]%';
--   (categorias e prioridades saem com o DROP das tabelas)
-- =============================================================================

-- 1) Prioridades (minutos de atendimento / resolucao) — valores iniciais ajustaveis por tela
INSERT INTO muralha.chamado_prioridade (codigo, nome, ordem, minutos_atendimento, minutos_resolucao, cor)
SELECT v.codigo, v.nome, v.ordem, v.min_at, v.min_res, v.cor
FROM (VALUES ('CRITICA', N'Crítica', 1,  60,  240, '#dc3545'),
             ('ALTA',    N'Alta',    2, 120,  480, '#fd7e14'),
             ('MEDIA',   N'Média',   3, 240, 1440, '#0d6efd'),
             ('BAIXA',   N'Baixa',   4, 480, 4320, '#6c757d')) v(codigo, nome, ordem, min_at, min_res, cor)
WHERE NOT EXISTS (SELECT 1 FROM muralha.chamado_prioridade p WHERE p.codigo = v.codigo);
PRINT 'Prioridades verificadas';
GO

-- 2) Categorias (dois niveis). A subcategoria herda "exige equipamento" da categoria.
DECLARE @cats TABLE (pai NVARCHAR(100), exige BIT, filho NVARCHAR(100));
INSERT INTO @cats VALUES
 (N'Equipamento', 1, N'Falha de captura/OCR'),
 (N'Equipamento', 1, N'Falha de comunicação'),
 (N'Equipamento', 1, N'Falha de energia'),
 (N'Equipamento', 1, N'Dano físico/vandalismo'),
 (N'Equipamento', 1, N'Outros'),
 (N'Sistema', 0, N'Erro de funcionalidade'),
 (N'Sistema', 0, N'Lentidão'),
 (N'Sistema', 0, N'Indisponibilidade'),
 (N'Sistema', 0, N'Outros'),
 (N'Acesso/usuário', 0, N'Criação/bloqueio de usuário'),
 (N'Acesso/usuário', 0, N'Permissões'),
 (N'Acesso/usuário', 0, N'Senha/MFA'),
 (N'Integração', 0, N'Falha de integração externa'),
 (N'Integração', 0, N'Outros'),
 (N'Dúvida/solicitação', 0, N'Dúvida de uso'),
 (N'Dúvida/solicitação', 0, N'Solicitação de melhoria'),
 (N'Dúvida/solicitação', 0, N'Outros');

INSERT INTO muralha.chamado_categoria (nome, id_pai, exige_equipamento)
SELECT DISTINCT c.pai, NULL, c.exige FROM @cats c
WHERE NOT EXISTS (SELECT 1 FROM muralha.chamado_categoria x WHERE x.id_pai IS NULL AND x.nome = c.pai);

INSERT INTO muralha.chamado_categoria (nome, id_pai, exige_equipamento)
SELECT c.filho, p.id, c.exige
FROM @cats c
JOIN muralha.chamado_categoria p ON p.id_pai IS NULL AND p.nome = c.pai
WHERE NOT EXISTS (SELECT 1 FROM muralha.chamado_categoria x WHERE x.id_pai = p.id AND x.nome = c.filho);
PRINT 'Categorias verificadas';
GO

-- 3) Parametros
INSERT INTO muralha.config_chave_valor (chave, valor)
SELECT v.chave, v.valor
FROM (VALUES ('chamado_sla_risco_percentual', '80'),
             ('chamado_fechamento_auto_dias', '5'),
             ('chamado_diretorio_anexos',     'C:\Consilux\MuralhaDigital\chamados'),
             ('chamado_anexo_max_mb',         '10'),
             ('chamado_email_ativo',          '1')) v(chave, valor)
WHERE NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor c WHERE c.chave = v.chave);
PRINT 'Parametros verificados';
GO

-- 4) Grupos de perfil (sis_grupo.id_grupo nao e IDENTITY: usa MAX+1)
DECLARE @grupos TABLE (descricao VARCHAR(50), chave VARCHAR(100));
INSERT INTO @grupos VALUES
 ('Chamados - Solicitante',        'chamado_grupo_solicitante'),
 ('Chamados - Gestor do contrato', 'chamado_grupo_gestor'),
 ('Chamados - Equipe tecnica',     'chamado_grupo_equipe');

DECLARE @descricao VARCHAR(50), @chave VARCHAR(100), @id INT;
DECLARE cur CURSOR LOCAL FAST_FORWARD FOR SELECT descricao, chave FROM @grupos;
OPEN cur;
FETCH NEXT FROM cur INTO @descricao, @chave;
WHILE @@FETCH_STATUS = 0
BEGIN
    IF NOT EXISTS (SELECT 1 FROM dbo.sis_grupo WHERE RTRIM(descricao) = @descricao)
    BEGIN
        SELECT @id = MAX(id_grupo) + 1 FROM dbo.sis_grupo;
        INSERT INTO dbo.sis_grupo (id_grupo, descricao) VALUES (@id, @descricao);
        PRINT 'Criado grupo ' + @descricao + ' = ' + CAST(@id AS VARCHAR(10));
    END
    SELECT @id = id_grupo FROM dbo.sis_grupo WHERE RTRIM(descricao) = @descricao;
    IF NOT EXISTS (SELECT 1 FROM muralha.config_chave_valor WHERE chave = @chave)
        INSERT INTO muralha.config_chave_valor (chave, valor) VALUES (@chave, CAST(@id AS VARCHAR(10)));
    FETCH NEXT FROM cur INTO @descricao, @chave;
END
CLOSE cur; DEALLOCATE cur;
GO

-- 5) Menus (lote unico: o RETURN interrompe tudo se os IDs ja estiverem ocupados)
IF EXISTS (SELECT 1 FROM dbo.sis_menu WHERE id_menu BETWEEN 379 AND 382
           AND acao NOT LIKE '/muralha-digital/pages/chamados/%')
   OR EXISTS (SELECT 1 FROM dbo.sis_menu_infos WHERE id_infos BETWEEN 46 AND 48
           AND href NOT LIKE '/muralha-digital/pages/chamados/%')
BEGIN
    RAISERROR('IDs de menu 379-382 ou infos 46-48 ja usados por outro modulo. Ajuste este script e ReferenciaEdital.java.', 16, 1);
    RETURN;
END

DECLARE @gSol INT = (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_solicitante');
DECLARE @gGes INT = (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_gestor');
DECLARE @gEqu INT = (SELECT CAST(valor AS INT) FROM muralha.config_chave_valor WHERE chave = 'chamado_grupo_equipe');

INSERT INTO dbo.sis_menu (id_menu, descricao, acao, tipo, nivel, id_pai_menu, menu, Ativo, nome_sistema)
SELECT v.id, v.descricao, v.acao, 'U', 1, 222, v.menu, 1, 'Muralha-Digital'
FROM (VALUES
 (379, 'Chamados',                 '/muralha-digital/pages/chamados/index.jsp',        'Chamados'),
 (380, 'Detalhe do Chamado',       '/muralha-digital/pages/chamados/detalhe.jsp',      'Detalhe do Chamado'),
 (381, 'Relatorios de Chamados',   '/muralha-digital/pages/chamados/relatorios.jsp',   'Relatorios de Chamados'),
 (382, 'Configuracao de Chamados', '/muralha-digital/pages/chamados/configuracao.jsp', 'Configuracao Chamados')
) v(id, descricao, acao, menu)
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu m WHERE m.id_menu = v.id);

-- Cards: 16 = Cadastro e Configuracao, 15 = Analise de Dados (detalhe.jsp nao tem card)
INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai)
SELECT v.id_infos, v.id_menu, v.src, v.descricao, v.href, v.det, v.ord, v.pai
FROM (VALUES
 (46, 379, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg', 'Chamados',
      '/muralha-digital/pages/chamados/index.jsp', 'Abertura e acompanhamento de chamados de suporte', 11, 16),
 (47, 381, '/muralha-digital/assets/images/analise/analise.jpg', 'Relatorios de Chamados',
      '/muralha-digital/pages/chamados/relatorios.jsp', 'Indicadores de atendimento por periodo, categoria e equipamento', 7, 15),
 (48, 382, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg', 'Configuracao de Chamados',
      '/muralha-digital/pages/chamados/configuracao.jsp', 'Prioridades, prazos e categorias de chamados', 12, 16)
) v(id_infos, id_menu, src, descricao, href, det, ord, pai)
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu_infos i WHERE i.id_infos = v.id_infos);

-- Direitos: 379/380 = todos os perfis; 381 = gestor e equipe; 382 = equipe. Grupo 11 = Administradores.
INSERT INTO dbo.sis_menu_direitos (id_grupo, id_usuario, id_menu)
SELECT v.g, NULL, v.m
FROM (VALUES (@gSol, 379), (@gGes, 379), (@gEqu, 379), (11, 379),
             (@gSol, 380), (@gGes, 380), (@gEqu, 380), (11, 380),
             (@gGes, 381), (@gEqu, 381), (11, 381),
             (@gEqu, 382), (11, 382)) v(g, m)
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_menu_direitos d WHERE d.id_grupo = v.g AND d.id_menu = v.m);
PRINT 'Menus e direitos verificados';
GO
