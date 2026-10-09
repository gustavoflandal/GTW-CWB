# Entrega — Plano 03: Política de Senhas

**Data de entrega:** 2026-10-08  
**Commits:** `f4adbf8` · `4729cd4` · `458282b` · `0887230`  
**Status:** ✅ Entregue (migração SQL pendente de execução manual)

---

## Origem

- Plano [`03-politica-senhas.md`](../planos-salvador/03-politica-senhas.md) — Política de Senhas
- **Requisitos TR:** §5.1.5.c (política parametrizável de senhas), §5.1.5.d (encerramento de sessão por inatividade)
- **Análise de aderência:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §1

## O que foi entregue

### Banco de dados
- Tabela `dbo.sis_senha_config` — 8 parâmetros configuráveis (tamanho mínimo, complexidade, validade, histórico, tentativas, tempo de bloqueio)
- 6 novas colunas em `dbo.sis_usuario`: `senha_hash`, `senha_historico`, `tentativas_invalidas`, `bloqueado_ate`, `dt_ultima_troca_senha`, `fl_troca_obrigatoria`
- Script: [`docs/banco-de-dados/migracoes/20261008_politica_senhas.sql`](../banco-de-dados/migracoes/20261008_politica_senhas.sql)

> ⚠️ **Ação necessária:** executar a migração SQL manualmente antes de testar.

### Back-end
| Arquivo | Responsabilidade |
|---|---|
| `muralha/digital/acesso/SenhaService.java` | Toda a lógica de política de senhas (ver abaixo) |
| `webapp/login/login_action.jsp` | Verificação de bloqueio, registro de falha, redirecionamento por expiração |
| `webapp/login/login_change_action.jsp` | Validação de complexidade, verificação de histórico, atualização de hash e auditoria |
| `muralha/digital/acessos/UsuarioServlet.java` | Nova ação `desbloquear` (`GET ?acao=desbloquear&id=X`) com auditoria |

### Funcionalidades do SenhaService

| Método | O que faz |
|---|---|
| `getConfig(chave, padrao)` | Lê parâmetro de `sis_senha_config`; usa padrão se falhar |
| `validarComplexidade(senha)` | Verifica tamanho, maiúscula, número e especial conforme config |
| `jaUsouRecentemente(idUsuario, nova)` | Compara hash SHA-256 com atual + histórico JSON |
| `atualizarSenha(idUsuario, nova)` | Salva novo hash, rotaciona histórico, zera contadores |
| `verificarBloqueio(login)` | Retorna mensagem se `bloqueado_ate > now()`, null se ok |
| `registrarFalha(login)` | Incrementa `tentativas_invalidas`; bloqueia ao atingir `max_tentativas` |
| `zerarTentativas(login)` | Limpa contadores após login bem-sucedido |
| `senhaExpirada(idUsuario)` | Compara `dt_ultima_troca_senha` com `validade_dias` |

---

## Parâmetros padrão

| Parâmetro | Valor padrão | Descrição |
|---|---|---|
| `min_tamanho` | 8 | Caracteres mínimos |
| `requer_maiuscula` | 1 | Exige letra maiúscula |
| `requer_numero` | 1 | Exige dígito numérico |
| `requer_especial` | 1 | Exige `!@#$%^&*()_+-=` |
| `validade_dias` | 90 | 0 = nunca expira |
| `historico_qtde` | 5 | Senhas anteriores bloqueadas |
| `max_tentativas` | 5 | Tentativas antes do bloqueio |
| `bloqueio_minutos` | 30 | 0 = bloqueio permanente |

---

## Critérios de aceite

| # | Critério | Status |
|---|---|---|
| 1 | `sis_senha_config` e colunas em `sis_usuario` criadas | ⏳ Aguarda execução da migração |
| 2 | 5 tentativas inválidas bloqueiam a conta por 30 min | ✅ (build ok) |
| 3 | Senha fraca rejeitada na tela de troca | ✅ (build ok) |
| 4 | Login com senha expirada redireciona para troca | ✅ (build ok) |
| 5 | Histórico impede reutilização das últimas 5 senhas | ✅ (build ok) |
| 6 | Admin pode desbloquear via `GET /MuralhaDigital/Usuarios?acao=desbloquear&id=X` | ✅ (build ok) |
| 7 | Ação de desbloqueio registrada em `sis_log_auditoria` | ✅ |

---

## Decisões de arquitetura

- **Sem bcrypt:** conforme restrição do projeto (sem novas dependências) — hash SHA-256 via `java.security.MessageDigest`
- **Senha legada preservada:** o campo MD5 (`sis_usuario.senha`) continua ativo para compatibilidade com o sistema legado; `senha_hash` é complementar
- **Falha silenciosa em `getConfig`:** se `sis_senha_config` não existir (migração não executada), o service usa valores padrão hardcoded — não bloqueia o login
- **Parâmetros em banco:** administradores podem ajustar a política sem redeploy

---

## Como testar

```sql
-- Forçar expiração para teste:
UPDATE dbo.sis_usuario
SET dt_ultima_troca_senha = DATEADD(DAY,-91,CAST(SYSDATETIME() AS DATE))
WHERE login = 'seu_login';

-- Verificar bloqueio após 5 tentativas erradas:
SELECT login, tentativas_invalidas, bloqueado_ate
FROM dbo.sis_usuario WHERE login = 'seu_login';
```

Testes detalhados: [`docs/testes/03-politica-senhas.md`](../testes/03-politica-senhas.md)
