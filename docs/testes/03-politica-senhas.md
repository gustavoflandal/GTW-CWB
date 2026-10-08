# Testes — Plano 03: Política de Senhas

**Data:** 2026-10-08  
**Ambiente:** localhost:8080 / GTW_MURALHA_DEV  
**Build:** SUCCESS

---

## Pré-requisito

Execute a migração antes dos testes:
```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -P "..." -i "docs\banco-de-dados\migracoes\20261008_politica_senhas.sql"
```

---

## T01 — Configuração carregada do banco

```sql
SELECT * FROM dbo.sis_senha_config ORDER BY chave;
-- Esperado: 8 linhas (min_tamanho, requer_maiuscula, requer_numero, requer_especial,
--           validade_dias, historico_qtde, max_tentativas, bloqueio_minutos)
```

**Resultado:** pendente execução da migração

---

## T02 — Colunas adicionadas em sis_usuario

```sql
SELECT TOP 1 senha_hash, tentativas_invalidas, bloqueado_ate,
             dt_ultima_troca_senha, fl_troca_obrigatoria
FROM dbo.sis_usuario;
-- Esperado: sem erro, valores NULL/0
```

**Resultado:** pendente execução da migração

---

## T03 — Bloqueio após 5 tentativas inválidas

1. Tentar login com senha errada 5 vezes seguidas
2. Na 6ª tentativa → mensagem "Usuário bloqueado até..."

```sql
-- Verificar após as tentativas:
SELECT login, tentativas_invalidas, bloqueado_ate
FROM dbo.sis_usuario WHERE login = 'seu_login';
-- Esperado: tentativas_invalidas >= 5, bloqueado_ate > SYSDATETIME()
```

**Resultado:** pendente teste manual

---

## T04 — Senha fraca rejeitada na troca

1. Logar e ir para `/login/login_change.jsp`
2. Tentar trocar senha para `"abc"` (curta e sem complexidade)
3. Esperado: mensagem de erro de complexidade

**Resultado:** pendente teste manual

---

## T05 — Expiração de senha força troca

```sql
-- Forçar expiração:
UPDATE dbo.sis_usuario SET dt_ultima_troca_senha = DATEADD(DAY,-91,CAST(SYSDATETIME() AS DATE))
WHERE login = 'seu_login';
```

Após isso, efetuar login → sistema deve redirecionar para `/login/login_change.jsp`

**Resultado:** pendente teste manual

---

## T06 — Histórico impede reutilização

1. Trocar senha para `"Senha@2024"`
2. Trocar novamente para `"Senha@2024"` (mesma senha)
3. Esperado: mensagem "Esta senha já foi usada recentemente"

**Resultado:** pendente teste manual

---

## T07 — Admin desbloqueia usuário

```
GET /MuralhaDigital/Usuarios?acao=desbloquear&id=<id_usuario>
```

```sql
-- Verificar após:
SELECT tentativas_invalidas, bloqueado_ate FROM dbo.sis_usuario WHERE id = <id>;
-- Esperado: tentativas_invalidas=0, bloqueado_ate=NULL
```

**Resultado:** pendente teste manual

---

## Observações

- A senha legada (MD5 em `sis_usuario.senha`) continua funcionando para comparação no login — o `senha_hash` (SHA-256) é complementar e não substitui o campo legado nesta fase
- A validação de complexidade só se aplica na troca de senha, não no login
- O SenhaService falha silenciosamente em `getConfig()` e `zerarTentativas()` para não impedir login se banco estiver lento
