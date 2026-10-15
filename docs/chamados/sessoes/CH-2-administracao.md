# SESSAO CH-2 — Administração, categoria própria e ícones

## Antes de qualquer código
1. Ler a seção "Complemento — administração, categoria própria e ícones" do plano e da [arquitetura](../sistema-chamados-arquitetura.md).
2. Confirmar que a migração da sessão CH-1 já foi executada no `GTW_MURALHA_DEV` (grupos 107–109 e menus 379–382).

## Objetivo
Atender o pedido do responsável após a primeira entrega: quem usa o sistema é definido pelo administrador; o sistema ganha categoria própria na tela inicial com ícones no estilo existente; o acordo de nível de serviço é configurável; a configuração aparece só para o administrador; tudo passa pelo motor de logs.

## Tarefas do plano
Detalhamento em [`22-sistema-chamados.md`](../../planos-salvador/22-sistema-chamados.md) (Tarefas 14 a 18).

| Tarefa | Tema |
|---|---|
| 14 | Migração `20261015_sistema_chamados_admin.sql`: chave `chamado_grupo_admin`, grupos 107–109 abaixo do grupo 11, categoria `sis_menu` 383 / `sis_menu_infos` 49, cards 46–48 dentro da categoria, direito do menu 382 só do administrador |
| 15 | Quatro ícones SVG em `assets/images/menu-icons/` no estilo dos menus |
| 16 | Perfil `ADMIN`, parâmetros de SLA e perfis de acesso no back-end (DAO e servlet) |
| 17 | Telas: abas "Parâmetros de SLA" e "Perfis de acesso" e visibilidade dos links por perfil |
| 18 | Teste integrado com o banco migrado e atualização da documentação |

## Escopo
- `ADMIN` é o único perfil que acessa a Configuração; enxerga todos os chamados em somente leitura (a menos que tenha outro perfil). A `EQUIPE` deixa de configurar.
- Parâmetros `chamado_sla_risco_percentual` (1–99) e `chamado_fechamento_auto_dias` (1–60), com histórico em `muralha.config_chave_valor_hist`.
- Inclusão e remoção de usuários dos três grupos de perfil na tabela `dbo.sis_usuario_grupo`, a mesma da tela legada.
- Auditoria (`AuditoriaService`, funcionalidade "Chamado"): `SALVAR_PARAMETROS_SLA`, `ADICIONAR_PERFIL`, `REMOVER_PERFIL`.

## Arquivos
- `docs/banco-de-dados/migracoes/20261015_sistema_chamados_admin.sql`
- `src/main/webapp/muralha-digital/assets/images/menu-icons/chamados-categoria.svg`, `chamados.svg`, `chamados-relatorios.svg`, `chamados-configuracao.svg`
- `PerfilChamado.java`, `ChamadoConfigDao.java`, `ChamadoConfigServlet.java` e testes `MaquinaEstadosTest`, `CadastrosChamadoTest`
- `pages/chamados/configuracao.jsp`, `assets/js/chamados/chamados-configuracao.js`, `chamados-lista.js`
- `docs/banco-de-dados/migracoes/20261015_usuarios_teste_chamados.sql` (usuários de teste, com limpeza no cabeçalho)
- `ReferenciaEdital.java` (menu 383), documentação de entrega, spec e roteiro de testes

## Critérios de aceite
- Script `_admin` idempotente (executado duas vezes sem erro) e com rollback no cabeçalho.
- `ADMIN` sozinho não possui nenhuma ação de atendimento; `veTodos` vale para GESTOR, EQUIPE e ADMIN.
- Parâmetros e perfis validados no servidor; cada alteração auditada e registrada no histórico de configuração.
- Link "Configuração" visível só ao `ADMIN`; "Relatórios" para GESTOR, EQUIPE e ADMIN; "Novo chamado" para SOLICITANTE e GESTOR.
- Teste integrado por HTTP com usuários reais do banco migrado; usuários de teste removidos ao final.
- Bateria de testes completa passando e build com sucesso.

## Resultado
Ver [`SESSAO-CH-2-relatorio.md`](../relatorios/SESSAO-CH-2-relatorio.md).
