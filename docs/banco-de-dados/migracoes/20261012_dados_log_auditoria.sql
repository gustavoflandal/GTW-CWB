-- Insere registros de exemplo no log de auditoria para popular filtros
-- Executar em: GTW_MURALHA_DEV (10.0.0.200)

INSERT INTO dbo.sis_log_auditoria (login, dt_operacao, ip_terminal, funcionalidade, operacao, id_registro, descricao)
VALUES
('administrador', GETDATE(), '10.0.0.1', 'Monitorado', 'cadastrar', 'MON-001', 'Cadastro de veículo monitorado ABC1D23'),
('administrador', GETDATE(), '10.0.0.1', 'Monitorado', 'consultar', NULL, 'Consulta de monitorados ativos'),
('administrador', GETDATE(), '10.0.0.1', 'Alerta', 'tratar', 'ALT-001', 'Tratativa de alerta - veículo localizado'),
('administrador', GETDATE(), '10.0.0.1', 'Alerta', 'consultar', NULL, 'Consulta de alertas do período'),
('administrador', GETDATE(), '10.0.0.1', 'Dashboard', 'visualizar', 'DSH-001', 'Acesso ao dashboard principal'),
('administrador', GETDATE(), '10.0.0.1', 'Lote', 'criar', 'LOTE-002', 'Lote criado para processamento'),
('administrador', GETDATE(), '10.0.0.1', 'Relatório', 'gerar', 'REL-001', 'Relatório de passagens gerado'),
('administrador', GETDATE(), '10.0.0.1', 'Usuário', 'login', NULL, 'Login no sistema'),
('administrador', GETDATE(), '10.0.0.1', 'KPI', 'configurar', 'KPI-001', 'Configuração de indicador de performance'),
('administrador', GETDATE(), '10.0.0.1', 'Dataset', 'criar', 'DS-001', 'Dataset criado para dashboard builder');
