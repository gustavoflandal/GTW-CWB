# Gaps Críticos para PoC Salvador — Plano Mestre

> **Para agentes:** use `superpowers:executing-plans` ou `superpowers:subagent-driven-development` para executar cada plano filho na ordem indicada abaixo.

**Goal:** Eliminar os 8 gaps que causam reprovação automática na Prova de Conceito da TRANSALVADOR antes que qualquer apresentação ocorra.

**Architecture:** Cada gap é um subsistema independente com seu próprio plano. Este documento define a ordem de execução (por dependência), o critério de aceite de cada item e o checklist pré-PoC final.

**Tech Stack:** Java 13.0.1 · Tomcat 9 · SQL Server (GTW_MURALHA_DEV) · Bootstrap 5.3 · sem novos frameworks

## Global Constraints

- Nunca alterar `.setup-gtw/`, `pom.xml` ou adicionar dependências externas
- Todo DDL/DML vai em `docs/banco-de-dados/migracoes/YYYYMMDD_*.sql`
- Todo servlet novo herda de `HttpServlet`, usa `@WebServlet`, valida sessão via `Acesso.verificaAcesso()`
- Conexão sempre via `Conexao.getConexao()`, fechada em `finally`
- SQL parametrizado (`PreparedStatement`); jamais concatenar entrada do usuário
- Build de verificação: `..\.setup-gtw\build.ps1` (deve terminar `BUILD SUCCESS`)
- Commits em português no imperativo

---

## Restrição de Banco

> **IMPORTANTE — leia `00b-tabelas-complementares.md` antes de executar qualquer migração.**  
> Nenhuma tabela ou coluna existente pode ser alterada (banco compartilhado). Os planos 02–10 usam tabelas complementares no lugar de `ALTER TABLE`. Execute a migração consolidada em `00b` antes de qualquer plano individual.

---

## Ordem de Execução dos Planos

Execute nesta ordem exata — cada plano é independente, mas os de auditoria e senhas devem vir antes dos outros:

| # | Plano | Arquivo | Depende de | Tempo estimado |
|---|---|---|---|---|
| 1 | Log de Auditoria Centralizado | `01-log-auditoria.md` | — | 1 dia |
| 2 | Hash SHA-256 na Recepção | `02-sha256-integridade.md` | — | 4 h |
| 3 | Política de Senhas | `03-politica-senhas.md` | plano 01 | 1 dia |
| 4 | MFA / TOTP | `04-mfa-totp.md` | plano 03 | 1 dia |
| 5 | Dupla Análise Independente | `05-dupla-analise.md` | plano 01 | 2 dias |
| 6 | Obliteração de Imagens (LGPD) | `06-obliteracao-imagens.md` | plano 01 | 1,5 dias |
| 7 | Monitoramento de Latência SLA ≤4s | `07-sla-latencia.md` | plano 01 | 1 dia |
| 8 | Painel de Assertividade | `08-painel-assertividade.md` | plano 05 | 1 dia |

**Total estimado:** ~9 dias úteis de desenvolvimento.

---

## Checklist Pré-PoC

Antes de apresentar à TRANSALVADOR, verificar cada item abaixo:

### §5.1 — Controle de Acesso
- [ ] Login com usuário vinculado a CPF ou matrícula (plano `09-cpf-matricula.md` da Onda 2, antecipar se possível)
- [ ] Senha exige complexidade mínima (8 chars, maiúscula, número, especial)
- [ ] Bloqueio após 5 tentativas inválidas; desbloqueio por admin
- [ ] MFA disponível (pelo menos opt-in)
- [ ] Sessão expira por inatividade configurável
- [ ] Log de auditoria grava: usuário, IP, timestamp, funcionalidade, operação, ID do registro
- [ ] Consulta de log com filtros e exportação funcional

### §5.2 — Gestão e Classificação de Infrações
- [ ] Imagens recebidas têm hash SHA-256 calculado e armazenado
- [ ] Tela de análise de imagens com obliteração (manual ao menos)
- [ ] Dupla análise: operador A não vê classificação de operador B; sistema bloqueia re-análise pelo mesmo operador
- [ ] Encaminhamento automático para desempate quando operadores A e B divergem
- [ ] Painel mostrando: taxa de assertividade %, volume do dia, divergências, registros pendentes

### §5.7 — LAP / Latência
- [ ] Latência campo→servidor registrada por passagem (coluna `latencia_ms`)
- [ ] Widget operacional indicando latência média e status semáforo

### Infraestrutura
- [ ] Build limpo (`BUILD SUCCESS`) sem warnings novos
- [ ] Sistema sobe em `run.ps1` sem erros no catalina.out
- [ ] Todas as novas telas acessíveis apenas por perfil autorizado

---

## Rollback

Se qualquer migração SQL der problema:
```sql
-- Cada script de migração deve ter sua seção DROP/ROLLBACK no início (comentada)
-- Executar a seção de rollback do script em questão
```

Código Java: reverter via `git revert <hash>` — cada plano termina com um commit isolado por tarefa.
