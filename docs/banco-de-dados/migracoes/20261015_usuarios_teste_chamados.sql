-- =============================================================================
-- Usuarios de teste do Sistema de Gerenciamento de Chamados (Plano 22, Tarefa 18)
-- Data: 2026-10-15 — idempotente. EXCLUSIVO do banco de desenvolvimento GTW_MURALHA_DEV: o script se recusa
-- a rodar em qualquer outro banco (guarda IF DB_NAME() abaixo, com SET NOEXEC ON/OFF).
-- Executar depois de 20261015_sistema_chamados_admin.sql. Os usuarios sao removidos ao fim do teste
-- com a secao de LIMPEZA abaixo.
--
-- Senha: o hash gravado e o MD5 hexadecimal (minusculo) que o login aceita
-- (com.consilux.model.Usuario.comparaSenha -> com.consilux.infra.Funcoes.geraMD5).
-- A senha em texto claro foi gerada no momento do teste e entregue fora do repositorio.
-- Sem troca obrigatoria nem MFA: alterar_senha = 0; complemento com senha_trocada_em = agora
-- (SenhaService.senhaExpirada) e totp_habilitado = 0 (login_action.jsp).
--
-- LIMPEZA / ROLLBACK (reproduzivel; os registros de dbo.sis_log_auditoria ficam, pois sao o log):
--   -- 1) Antes, listar os protocolos para apagar as pastas de anexos em disco:
--   --    SELECT protocolo FROM muralha.chamado WHERE id_usuario_solicitante IN (SELECT id_usuario FROM dbo.sis_usuario
--   --      WHERE usuario IN ('TESTE.CHAMADO.ADM','TESTE.CHAMADO.SOL'));
--   --    e remover C:\Consilux\MuralhaDigital\chamados\<ano>\<protocolo>\ de cada um.
--   SET XACT_ABORT ON;
--   BEGIN TRAN;
--   DECLARE @u TABLE (id INT PRIMARY KEY);
--   INSERT INTO @u SELECT id_usuario FROM dbo.sis_usuario WHERE usuario IN ('TESTE.CHAMADO.ADM','TESTE.CHAMADO.SOL');
--   DECLARE @c TABLE (id BIGINT PRIMARY KEY);
--   INSERT INTO @c SELECT id FROM muralha.chamado
--    WHERE id_usuario_solicitante IN (SELECT id FROM @u) OR id_usuario_responsavel IN (SELECT id FROM @u);
--   DELETE FROM muralha.chamado_anexo       WHERE id_chamado IN (SELECT id FROM @c);
--   DELETE FROM muralha.chamado_notificacao WHERE id_chamado IN (SELECT id FROM @c);
--   DELETE FROM muralha.chamado_historico   WHERE id_chamado IN (SELECT id FROM @c);
--   DELETE FROM muralha.chamado             WHERE id IN (SELECT id FROM @c);
--   DELETE FROM muralha.chamado_protocolo   WHERE ano = 2026
--      AND NOT EXISTS (SELECT 1 FROM muralha.chamado WHERE protocolo LIKE 'CH-2026-%');  -- contador criado pelo teste
--   DELETE FROM dbo.sis_usuario_grupo        WHERE id_usuario IN (SELECT id FROM @u);
--   DELETE FROM dbo.sis_usuario_complemento  WHERE id_usuario IN (SELECT id FROM @u);
--   DELETE FROM dbo.sis_logon_logoff_usuario WHERE id_usuario IN (SELECT id FROM @u);
--   DELETE FROM dbo.sis_log_detalhe WHERE id_log IN (SELECT id_log FROM dbo.sis_log WHERE id_usuario IN (SELECT id FROM @u));
--   DELETE FROM dbo.sis_log                  WHERE id_usuario IN (SELECT id FROM @u);
--   DELETE FROM dbo.sis_usuario              WHERE id_usuario IN (SELECT id FROM @u);
--   -- Parametros de SLA de volta ao padrao (80 / 5):
--   UPDATE muralha.config_chave_valor SET valor = '80' WHERE chave = 'chamado_sla_risco_percentual';
--   UPDATE muralha.config_chave_valor SET valor = '5'  WHERE chave = 'chamado_fechamento_auto_dias';
--   COMMIT;
-- =============================================================================

-- Guarda: somente no banco de desenvolvimento
IF DB_NAME() <> N'GTW_MURALHA_DEV'
BEGIN
    RAISERROR('Script exclusivo do banco de desenvolvimento GTW_MURALHA_DEV.', 16, 1);
    SET NOEXEC ON;
END

-- 1) Usuarios (e-mails em dominio .invalid de proposito: a notificacao termina em ERRO, sem envio real)
INSERT INTO dbo.sis_usuario (usuario, senha, nome, email, ativo, alterar_senha, id_grupo_equipamento)
SELECT 'TESTE.CHAMADO.ADM', '090ec34a1cf3be338f58848634051e70', 'Teste Chamados Administrador',
       'teste.chamado.adm@example.invalid', 1, 0, 9
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_usuario WHERE usuario = 'TESTE.CHAMADO.ADM');

INSERT INTO dbo.sis_usuario (usuario, senha, nome, email, ativo, alterar_senha, id_grupo_equipamento)
SELECT 'TESTE.CHAMADO.SOL', '090ec34a1cf3be338f58848634051e70', 'Teste Chamados Solicitante',
       'teste.chamado.sol@example.invalid', 1, 0, 9
WHERE NOT EXISTS (SELECT 1 FROM dbo.sis_usuario WHERE usuario = 'TESTE.CHAMADO.SOL');
PRINT 'Usuarios de teste verificados';

-- 2) Administrador no grupo 11 (Administradores). O solicitante fica sem grupos: o ADMIN o inclui
--    pela aba "Perfis de acesso" durante o teste.
INSERT INTO dbo.sis_usuario_grupo (id_usuario, id_grupo)
SELECT u.id_usuario, 11 FROM dbo.sis_usuario u
WHERE u.usuario = 'TESTE.CHAMADO.ADM'
  AND NOT EXISTS (SELECT 1 FROM dbo.sis_usuario_grupo g WHERE g.id_usuario = u.id_usuario AND g.id_grupo = 11);

-- 3) Complemento: senha recem-trocada (nao expira) e MFA desligado
MERGE dbo.sis_usuario_complemento AS alvo
USING (SELECT id_usuario FROM dbo.sis_usuario WHERE usuario IN ('TESTE.CHAMADO.ADM','TESTE.CHAMADO.SOL')) AS src
   ON alvo.id_usuario = src.id_usuario
WHEN MATCHED THEN UPDATE SET senha_trocada_em = SYSDATETIME(), totp_habilitado = 0, totp_secret = NULL,
                             tentativas_invalidas = 0, bloqueado_ate = NULL
WHEN NOT MATCHED THEN INSERT (id_usuario, senha_trocada_em, totp_habilitado, tentativas_invalidas)
                      VALUES (src.id_usuario, SYSDATETIME(), 0, 0);
PRINT 'Grupos e complemento verificados';
GO
SET NOEXEC OFF;
