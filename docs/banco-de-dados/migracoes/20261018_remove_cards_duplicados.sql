-- ============================================================
-- Migração: remove cards que duplicam um botão de outra tela
-- Data: 2026-10-18
--
-- Por quê: estas telas já são abertas por um botão na tela principal do módulo, com as
-- mesmas permissões; o card no menu era um segundo caminho para o mesmo lugar.
--
--   Card removido                 | Acesso que permanece                          | Permissão
--   Configuracao KPIs  (372)      | KPIs → botão "Configurar"                     | grupos 41, 42 (iguais)
--   Catalogo de Dados  (378)      | Catálogo de Datasets → botão do catálogo      | grupos 11, 41, 42 (iguais)
--   Guarnição          (306)      | Central de Atendimento → botão "Gerenciar"    | grupos 6, 11, 41, 42, 43 (iguais)
--   Relatorios de Chamados (381)  | Chamados → botão "Relatórios" (Gestor, Equipe, Admin) | grupos 11, 108, 109
--   Configuracao de Chamados (382)| Chamados → botão "Configuração" (Admin)       | grupo 11
--
-- Só apaga a linha do card em dbo.sis_menu_infos. dbo.sis_menu e dbo.sis_menu_direitos ficam
-- como estão: a permissão de cada tela continua valendo para quem a abre pelo botão.
-- Idempotente.
--
-- Executar com: sqlcmd -b -I -f 65001 -i <arquivo>   (somente com autorização humana)
-- Rollback (recria os cards exatamente como estavam; id_infos não é IDENTITY):
--   INSERT INTO dbo.sis_menu_infos (id_infos, id_menu, src, descricao, href, descricao_detalhada, ordenacao, menu_pai) VALUES
--    (11, 306, '/muralha-digital/assets/images/guarnicao/guarnicao.jpg', N'Guarnição', '/muralha-digital/pages/guarnicao/listagem-guarnicoes.jsp', N'Cadastro de Guarnição', 5, 16),
--    (41, 372, '/muralha-digital/assets/images/menu-icons/config-kpis.svg', 'Configuracao KPIs', '/muralha-digital/pages/admin/kpis/index.jsp', 'Configuracao de KPIs', 7, 16),
--    (45, 378, '/muralha-digital/assets/images/cadastro-basico/cadastro-basico2.jpg', 'Catalogo de Dados', '/muralha-digital/pages/analises/catalogo/index.jsp', 'Gerenciamento de tabelas, colunas e relacoes para dashboards', 10, 16),
--    (47, 381, '/muralha-digital/assets/images/menu-icons/chamados-relatorios.svg', 'Relatorios de Chamados', '/muralha-digital/pages/chamados/relatorios.jsp', 'Indicadores de atendimento por periodo, categoria e equipamento', 2, 49),
--    (48, 382, '/muralha-digital/assets/images/menu-icons/chamados-configuracao.svg', 'Configuracao de Chamados', '/muralha-digital/pages/chamados/configuracao.jsp', 'Prioridades, prazos e categorias de chamados', 3, 49);
-- ============================================================
SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

DELETE FROM dbo.sis_menu_infos
WHERE href IN (
    '/muralha-digital/pages/admin/kpis/index.jsp',
    '/muralha-digital/pages/analises/catalogo/index.jsp',
    '/muralha-digital/pages/guarnicao/listagem-guarnicoes.jsp',
    '/muralha-digital/pages/chamados/relatorios.jsp',
    '/muralha-digital/pages/chamados/configuracao.jsp'
);
PRINT CONCAT('Cards removidos: ', @@ROWCOUNT);
GO

-- Menus e permissões continuam ativos (acesso pelos botões).
SELECT m.id_menu, RTRIM(m.descricao) AS menu, m.Ativo,
       (SELECT COUNT(*) FROM dbo.sis_menu_direitos d WHERE d.id_menu = m.id_menu) AS grupos,
       (SELECT COUNT(*) FROM dbo.sis_menu_infos i WHERE i.id_menu = m.id_menu) AS cards
FROM dbo.sis_menu m
WHERE m.id_menu IN (306, 372, 378, 381, 382)
ORDER BY m.id_menu;
GO
