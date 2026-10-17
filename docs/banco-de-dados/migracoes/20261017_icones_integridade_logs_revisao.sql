-- ============================================================
-- Migração: ícones próprios para os cards de Integridade (Plano 25),
-- Logs Técnicos (Plano 26) e Revisão de Placas (Plano 27)
-- Data: 2026-10-17
--
-- Por quê: as migrações de 2026-10-16 cadastraram os três cards com a
-- imagem genérica /muralha-digital/assets/images/analise/analise.jpg,
-- deixando ícones iguais para funcionalidades diferentes.
--
-- Só altera sis_menu_infos.src (DML). Localiza os cards pelo href porque
-- os id_menu foram gerados dinamicamente. Idempotente.
--
-- Rollback:
--   UPDATE dbo.sis_menu_infos SET src = '/muralha-digital/assets/images/analise/analise.jpg'
--   WHERE href IN ('/muralha-digital/pages/monitoramento/integridade/index.jsp',
--                  '/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp',
--                  '/muralha-digital/pages/processamento/revisao-placas/index.jsp');
-- ============================================================
SET NOCOUNT ON;
SET QUOTED_IDENTIFIER ON;

UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/integridade-imagens.svg'
 WHERE href = '/muralha-digital/pages/monitoramento/integridade/index.jsp';

UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/logs-tecnicos.svg'
 WHERE href = '/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp';

UPDATE dbo.sis_menu_infos
   SET src = '/muralha-digital/assets/images/menu-icons/revisao-placas.svg'
 WHERE href = '/muralha-digital/pages/processamento/revisao-placas/index.jsp';

SELECT id_infos, id_menu, RTRIM(href) AS href, RTRIM(src) AS src
  FROM dbo.sis_menu_infos
 WHERE href IN ('/muralha-digital/pages/monitoramento/integridade/index.jsp',
                '/muralha-digital/pages/monitoramento/logs-tecnicos/index.jsp',
                '/muralha-digital/pages/processamento/revisao-placas/index.jsp');
