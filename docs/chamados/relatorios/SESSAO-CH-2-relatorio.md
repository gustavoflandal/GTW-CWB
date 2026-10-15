# Relatório CH-2 — Administração, categoria própria e ícones

## Origem

Sessão: [CH-2-administracao.md](../sessoes/CH-2-administracao.md)
Plano: [22-sistema-chamados.md](../../planos-salvador/22-sistema-chamados.md) (Tarefas 14 a 18, seção "Complemento — administração, categoria própria e ícones")

Data: 2026-10-15

## Resultado

Perfil `ADMIN` e Configuração restrita a ele; abas "Parâmetros de SLA" e "Perfis de acesso"; categoria "Sistema de Chamados" (`sis_menu` 383, `sis_menu_infos` 49) com os cartões Chamados, Relatórios e Configuração e quatro ícones SVG novos; grupos 107 a 109 abaixo do grupo 11; auditoria dos parâmetros e dos perfis pelo motor de logs. Migração executada no `GTW_MURALHA_DEV`. Teste integrado por HTTP com usuários de teste criados e removidos por script versionado.

## Commits

Intervalo `480620c..199609c`:

| Commit | Assunto |
|---|---|
| `77895ba` | Adiciona migracao de administracao e categoria do sistema de chamados |
| `49ea026` | Adiciona icones do sistema de chamados no estilo dos menus |
| `97b0b4f` | Adiciona perfil administrador, parametros de SLA e perfis de acesso dos chamados |
| `fe2bfb6` | Adiciona abas de parametros de SLA e perfis de acesso na configuracao de chamados |
| `c4f1d50` | Adiciona script de usuarios de teste do sistema de chamados |
| `4177450` | Associa a categoria Sistema de Chamados aos itens 7.2.3 e 7.2.4 do edital |
| `199609c` | Documenta teste integrado e complemento do sistema de chamados |

## Testes

- 98 testes TestNG passando (bateria completa após a Tarefa 16).
- Teste integrado por HTTP com o banco migrado: 10 de 10 casos passaram (configuração visível só ao administrador, parâmetros de SLA, inclusão e remoção de perfil, registros no motor de logs, ciclo do chamado). O roteiro foi executado com Administrador, Equipe e Solicitante.
- Migração `_admin` executada duas vezes para provar a idempotência.
- E-mail real não enviado: o SMTP local do `confGTW.xml` recusa a autenticação (`535`).

## Decisões tomadas

Rulings da fase 2 (ledger):

- **Ruling 12:** a EQUIPE deixa de acessar a Configuração (só `ADMIN`), por pedido explícito ("acesso à configuração e definição de usuários só para o administrador"). Se estiver errado: devolver `EQUIPE` em `exigir()` do `ChamadoConfigServlet` e o grupo 109 no direito do menu 382.
- **Ruling 13:** `ADMIN` enxerga todos os chamados (`veTodos`), mas não executa ações de atendimento sem outro perfil; o administrador audita sem atuar. Se estiver errado: remover `ADMIN` de `veTodos`.
- **Ruling 14:** usuários de teste criados por script versionado (decisão do responsável) e removidos ao final; diretório de anexos local `C:\Consilux\MuralhaDigital\chamados` criado para o teste. Se estiver errado: apagar a pasta.

Rulings da fase 1, que seguem valendo:

1. Scripts SQL somente escritos até a autorização do responsável (a migração só foi executada no `GTW_MURALHA_DEV` depois, em 2026-10-15).
2. Testes de navegador adiados até a migração do banco.
3. Trailer de coautoria dos commits com o modelo real que escreveu o código.
4. Testes executados direto pelo TestNG, porque o provider `surefire-testng` não está no cache Maven offline (`mvn -Dtest` não funciona neste ambiente).
5. O DAO trunca as datas a milissegundos ao gravar, para casar com `DATETIME2(3)`.
6. A negação por perfil responde HTTP 200 com `{ok:false}` em vez de 403 (desvio da spec §3.3, documentado na entrega).
7. Marcas `CUMPRIDO` e `VIOLADO` definitivas; injeção no cabeçalho do CSV corrigida.
8. Controle otimista com a versão (`dtAtualizacao`) enviada pelo cliente.
9. A saída da pausa estende o prazo de resolução gravado.
10. Correções baratas da revisão final: SweetAlert com `text`, script duplicado, auditoria fora do `try`, fechamento com `finally`, registro com `try/catch`, nomes de grupos, e-mail vazio.
11. Conflito de versão causado pelo job de SLA aceito como comportamento correto (o usuário recarrega a página).

## Pendências

- Deferidas pela revisão (menores): o script de usuários de teste faz a limpeza sem `BEGIN TRAN`; o `DELETE` do contador 2026 depende só da ausência de chamados; 4 linhas de `config_chave_valor_hist` (ids 358 a 361) ficam como log; adicionar e remover membro auditam mesmo sem efeito; a busca de usuários não tem `try/catch` (um alerta por tecla em caso de erro); o tooltip/asterisco do menu 383 não foi verificado porque o servidor estava sem rebuild.
- Perfil Gestor e as telas no navegador ficam para a homologação.
- O envio real de e-mail depende de um SMTP válido (tratado na CH-3).
