# Entrega — Plano 09: CPF e Matrícula

**Data:** 2026-10-08  
**Status:** ✅ Entregue²

## Escopo

Tela administrativa para gerenciar CPF e matrícula dos usuários do sistema, com validação de CPF (dígitos verificadores) no servidor e persistência via tabela complementar `dbo.sis_usuario_complemento` (sem ALTER TABLE em `dbo.sis_usuario`).

## Requisito do TR atendido

- §5.1 — Controle de acesso com identificação funcional dos operadores: vinculação de CPF e matrícula ao cadastro, garantindo rastreabilidade individualizada.

## Arquivos produzidos

| Arquivo | Descrição |
|---|---|
| `src/main/java/muralha/digital/usuario/UsuarioCpfUtil.java` | Utilitário de validação de CPF: normalização (remove pontuação), rejeição de sequências iguais, verificação dos dígitos verificadores |
| `src/main/java/muralha/digital/usuario/UsuarioCpfMatriculaServlet.java` | Servlet GET (listar) + POST (salvar via MERGE em `sis_usuario_complemento`). Valida CPF, matrícula e trata constraints UNIQUE |
| `src/main/webapp/muralha-digital/pages/admin/cpf-matricula/index.jsp` | JSP com tabela de usuários ativos, modal de edição com campos CPF e matrícula, formatação de CPF no front-end |

## Arquitetura

- **Tabela complementar** `dbo.sis_usuario_complemento`: já existente na migração `20261008_tabelas_complementares.sql`, com colunas `cpf CHAR(11)` e `matricula VARCHAR(20)` + índices UNIQUE filtrados
- **MERGE upsert**: se o registro do usuário já existe na complementar, atualiza; senão, insere — mesmo padrão dos Planos 05-07
- **Validação server-side**: CPF validado por `UsuarioCpfUtil.normalizar()` antes de qualquer persistência
- **LEFT JOIN** na listagem: `sis_usuario LEFT JOIN sis_usuario_complemento` com COALESCE para valores default

## Critérios de aceite

- [x] Build compila sem erros
- [ ] Tela carrega e lista usuários ativos
- [ ] CPF válido é salvo e exibido formatado
- [ ] CPF inválido é rejeitado com mensagem clara
- [ ] CPF duplicado é rejeitado pela constraint UNIQUE
- [ ] Matrícula duplicada é rejeitada
- [ ] Campos podem ser limpos (NULL)

## Pendências

- Registrar menu no banco via INSERT em `dbo.sis_menu_infos`
- Teste manual completo (ver `docs/testes/09-cpf-matricula.md`)
