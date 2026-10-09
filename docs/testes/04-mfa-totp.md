# Testes — Plano 04: MFA/TOTP

**Data:** 2026-10-08  
**Ambiente:** localhost:8080 / GTW_MURALHA_DEV  
**Build:** SUCCESS

---

## Pré-requisito

Execute as migrações dos Planos 03 e 04:
```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -P "..." -i "docs\banco-de-dados\migracoes\20261008_politica_senhas.sql"
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -P "..." -i "docs\banco-de-dados\migracoes\20261008_mfa_totp.sql"
```

---

## T01 — Colunas adicionadas em sis_usuario

```sql
SELECT TOP 1 totp_secret, totp_habilitado FROM dbo.sis_usuario;
-- Esperado: sem erro, totp_secret=NULL, totp_habilitado=0
```

**Resultado:** pendente execução da migração

---

## T02 — Tela de configuração MFA acessível

**URL:** `http://localhost:8080/muralha-digital/pages/meu-perfil/configurar-mfa.jsp`  
(requer login prévio)

**Resultado esperado:** QR Code exibido, campo de código habilitado  
**Resultado:** pendente teste manual

---

## T03 — Ativar MFA com código válido

1. Acessar `/muralha-digital/pages/meu-perfil/configurar-mfa.jsp`
2. Escanear QR Code com Google Authenticator ou similar
3. Digitar o código de 6 dígitos exibido no app
4. Clicar em "Ativar"

```sql
-- Verificar após ativação:
SELECT totp_habilitado, totp_secret FROM dbo.sis_usuario WHERE login = 'seu_login';
-- Esperado: totp_habilitado=1, totp_secret NOT NULL
```

**Resultado:** pendente teste manual

---

## T04 — Login redireciona para tela MFA

1. Com MFA ativado (T03 concluído), fazer logout e login
2. Esperado: após senha correta → tela `mfa_codigo.jsp` em vez de `abertura-sistemas.jsp`

**Resultado:** pendente teste manual

---

## T05 — Código inválido exibe erro

1. Na tela `mfa_codigo.jsp`, digitar código errado (ex: `000000`)
2. Esperado: mensagem "Código inválido. Tente novamente."
3. Nova tentativa com código correto → login completado

**Resultado:** pendente teste manual

---

## T06 — Login com MFA válido completa sessão

1. Digitar código correto do app
2. Esperado: redirecionamento para `abertura-sistemas.jsp`
3. Verificar auditoria:

```sql
SELECT TOP 1 * FROM dbo.sis_log_auditoria
WHERE operacao = 'login-mfa-ok'
ORDER BY dt_operacao DESC;
```

**Resultado:** pendente teste manual

---

## T07 — Sessão temporária expira sem MFA

Sem digitar o código, aguardar 5 minutos (timeout da sessão MFA) e tentar acessar `mfa_verificar.jsp`:  
**Esperado:** redirecionamento para `login.jsp`

**Resultado:** pendente teste manual

---

## Observações

- MFA é opt-in; não interfere em usuários sem `totp_habilitado=1`
- Se as colunas ainda não existirem (migração não executada), a query falha silenciosamente e o login segue sem MFA
- O segredo TOTP ainda não está criptografado em repouso nesta versão (ver `muralha-digital-config.xml` para futura melhoria)
- Compatível com: Google Authenticator, Microsoft Authenticator, Authy, Bitwarden
