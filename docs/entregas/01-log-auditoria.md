# Entrega — Plano 01: Log de Auditoria Centralizado

**Data de entrega:** 2026-10-08  
**Commit principal:** `d2c3489` / Correção: `0f41b82`  
**Status:** ✅ Entregue

---

## O que foi entregue

### Banco de dados
- Tabela `dbo.sis_log_auditoria` com índices em `dt_operacao`, `id_usuario` e `funcionalidade`
- Script: [`docs/banco-de-dados/migracoes/20261008_log_auditoria.sql`](../banco-de-dados/migracoes/20261008_log_auditoria.sql)

### Back-end
| Arquivo | Responsabilidade |
|---|---|
| `muralha/digital/auditoria/AuditoriaService.java` | Registra eventos de auditoria de forma assíncrona (thread separada, nunca bloqueia) |
| `muralha/digital/auditoria/AuditoriaServlet.java` | `GET /MuralhaDigital/Auditoria?acao=consultar` — consulta paginada com filtros |

### Front-end
| Arquivo | Responsabilidade |
|---|---|
| `webapp/muralha-digital/pages/auditoria/consulta-log.jsp` | Tela Bootstrap 5.3 com filtros e tabela |
| `webapp/muralha-digital/assets/js/auditoria/consulta-log.js` | AJAX, paginação, exportação CSV e XLS |

---

## Critérios de aceite

| # | Critério | Status |
|---|---|---|
| 1 | Tabela `sis_log_auditoria` criada no banco | ✅ |
| 2 | `AuditoriaService.registrar()` insere assincronamente sem lançar exceção | ✅ |
| 3 | Endpoint `/MuralhaDigital/Auditoria?acao=consultar` retorna JSON paginado | ✅ (build ok) |
| 4 | Tela de consulta acessível em `/muralha-digital/pages/auditoria/consulta-log.jsp` | ✅ (build ok) |
| 5 | Exportação CSV e XLS disponível | ✅ |
| 6 | Instrumentação dos servlets críticos (login, processamento) | ⏳ Tarefa 5 pendente |

---

## Pendências

- **Tarefa 5:** instrumentar `login_action.jsp`, `ProcessamentoServlet` e servlets de relatório com `AuditoriaService.registrar()` — planejado como melhoria incremental
- **Menu:** INSERT em `dbo.sis_menu_infos` para expor a tela no menu do sistema

---

## Como testar

```sql
-- Verificar tabela:
SELECT TOP 1 * FROM dbo.sis_log_auditoria;
-- Esperado: sem erro, 0 rows até instrumentação completa

-- Após login (quando Tarefa 5 estiver pronta):
SELECT TOP 5 * FROM dbo.sis_log_auditoria ORDER BY dt_operacao DESC;
```

Testes detalhados: [`docs/testes/01-log-auditoria.md`](../testes/01-log-auditoria.md)
