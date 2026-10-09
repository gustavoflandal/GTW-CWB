# Testes — Plano 01: Log de Auditoria

**Data:** 2026-10-08  
**Ambiente:** localhost:8080 / GTW_MURALHA_DEV  
**Build:** SUCCESS

---

## T01 — Tabela criada no banco

```sql
SELECT TOP 1 * FROM dbo.sis_log_auditoria;
```

**Resultado esperado:** 0 rows, sem erro  
**Resultado:** ✓ Tabela existe e está acessível

---

## T02 — Tela de consulta acessível

**URL:** `http://localhost:8080/muralha-digital/pages/auditoria/consulta-log.jsp`  
**Resultado esperado:** Tela renderiza sem erro 500 no Tomcat  
**Resultado:** pendente execução manual

---

## T03 — Endpoint retorna JSON

```
GET /MuralhaDigital/Auditoria?acao=consultar
```

**Resultado esperado:** `{"ok":true,"registros":[]}`  
**Resultado:** pendente execução manual

---

## T04 — AuditoriaService registra evento

Após login no sistema, verificar:

```sql
SELECT TOP 5 * FROM dbo.sis_log_auditoria ORDER BY dt_operacao DESC;
```

**Resultado esperado:** Linha com `funcionalidade='Acesso'`, `operacao='login'`  
**Resultado:** pendente instrumentação da tela de login (Tarefa 5 do plano)

---

## Observações

- Tarefa 5 (instrumentar servlets críticos) ainda não executada — registro de auditoria não será gerado automaticamente até que os servlets sejam instrumentados
- Exportação CSV e XLS dependem de dados na tabela para teste completo
