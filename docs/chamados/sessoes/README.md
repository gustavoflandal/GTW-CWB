# Sessões do Sistema de Chamados

Registro das sessões de trabalho do Plano 22 (Sistema de Gerenciamento de Chamados, itens 7.2.3 e 7.2.4 do edital). Cada sessão agrupa tarefas do plano, com objetivo, escopo, arquivos e critérios de aceite; o resultado de cada uma está no relatório correspondente.

Referências:
- Arquitetura: [`sistema-chamados-arquitetura.md`](../sistema-chamados-arquitetura.md)
- Manual do usuário: [`manual-sistema-chamados.md`](../manual-sistema-chamados.md)
- Plano: [`22-sistema-chamados.md`](../../planos-salvador/22-sistema-chamados.md)

## Plano de sessões

| Sessão | Título | Tarefas do plano | Escopo principal | Documento | Relatório |
|---|---|---|---|---|---|
| CH-1 | Núcleo do sistema de chamados | 1 a 13 | Migração e seed, regras puras (estados, SLA, protocolo), anexos, exportação, e-mail, DAOs, serviço transacional, servlets, jobs, telas, referência ao edital e documentação | [CH-1-nucleo.md](CH-1-nucleo.md) | [SESSAO-CH-1-relatorio.md](../relatorios/SESSAO-CH-1-relatorio.md) |
| CH-2 | Administração, categoria e ícones | 14 a 18 | Migração `_admin`, ícones SVG, perfil `ADMIN`, parâmetros de SLA, perfis de acesso, telas de configuração, teste integrado | [CH-2-administracao.md](CH-2-administracao.md) | [SESSAO-CH-2-relatorio.md](../relatorios/SESSAO-CH-2-relatorio.md) |
| CH-3 | SMTP configurável pelo administrador | 19 | Chaves `chamado_smtp_*`, senha cifrada (AES-GCM), envio com JavaMail próprio, aba "E-mail (SMTP)" | [CH-3-smtp.md](CH-3-smtp.md) | [SESSAO-CH-3-relatorio.md](../relatorios/SESSAO-CH-3-relatorio.md) |

A Tarefa 20 (organização desta pasta `docs/chamados/`) é só documental e não constitui sessão de código.

## Ordem de execução

As sessões foram executadas em sequência, e cada uma depende da anterior.

```
CH-1 (núcleo)
  └── CH-2 (administração)
        └── CH-3 (SMTP)
```

## Stack

- **Back-end**: Java 13.0.1, Servlet puro, JDBC (SQL Server), Gson, Quartz, commons-fileupload, Apache POI, JavaMail
- **Front-end**: JSP + Bootstrap 5.3, Bootstrap Icons, SweetAlert2, Chart.js 4.4.1 (relatórios) e jsPDF (PDF no cliente)
- **Testes**: TestNG, executados direto (o provider `surefire-testng` não está no cache Maven offline)
- **Restrições**: nenhuma dependência nova no `pom.xml`; `.setup-gtw/` intocado; DDL/DML somente em scripts de `docs/banco-de-dados/migracoes/`

## Como cada sessão foi conduzida

1. Ler `CLAUDE.md`, a arquitetura e o plano.
2. Executar tarefa a tarefa (TDD nas regras puras), com commit por tarefa.
3. Revisar cada tarefa e, ao final, a sessão inteira.
4. Registrar o resultado no relatório da sessão.
