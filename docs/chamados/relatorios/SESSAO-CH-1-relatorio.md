# Relatório CH-1 — Núcleo do Sistema de Chamados

## Origem

Sessão: [CH-1-nucleo.md](../sessoes/CH-1-nucleo.md)
Plano: [22-sistema-chamados.md](../../planos-salvador/22-sistema-chamados.md) (Tarefas 1 a 13)

Data: 2026-10-15

## Resultado

Módulo `muralha.digital.chamado` implementado e integrado à Muralha Digital: migração e seed, regras puras (máquina de estados, SLA 24x7, protocolo anual), anexos validados, exportação CSV/XLSX com SHA-256, e-mails de confirmação e reclassificação, DAOs, serviço transacional, três servlets, dois jobs Quartz, quatro telas e referência aos itens 7.2.3 e 7.2.4 do edital nos menus 379 a 382. Revisão por tarefa e revisão final da sessão, com correções aplicadas.

## Commits

Intervalo `5fdb8e2..8c73555` (branch `feat/plano-22-sistema-chamados`):

| Commit | Assunto |
|---|---|
| `07430e9` | Adiciona migracao e seed do sistema de chamados (plano 22) |
| `888495c` | Adiciona maquina de estados do sistema de chamados |
| `ff2825b` | Adiciona calculo de SLA 24x7 e gerador de protocolo dos chamados |
| `a1034db` | Adiciona armazenamento validado de anexos dos chamados |
| `0b285db` | Adiciona exportacao com SHA-256 e e-mails de confirmacao dos chamados |
| `1ce35c3` | Adiciona modelos, filtros e DAOs do sistema de chamados |
| `798441e` | Trunca datas de abertura e prazos em milissegundos ao inserir chamado |
| `f54f63e` | Adiciona servico transacional do sistema de chamados |
| `7c70ad7` | Adiciona servlets do sistema de chamados |
| `4c696a7` | Abre anexo antes de definir cabecalhos e reseta resposta em erro |
| `708bc21` | Agenda jobs de SLA e de e-mail do sistema de chamados |
| `b856532` | Adiciona tela de lista e abertura de chamados |
| `9de7d0d` | Adiciona tela de detalhe e acoes do chamado |
| `4a608fe` | Adiciona telas de relatorios e configuracao de chamados |
| `710f5c5` | Documenta entrega do sistema de chamados e marca itens 7.2.3/7.2.4 no menu |
| `909c0fc` | Torna definitivas as marcas CUMPRIDO e VIOLADO do SLA |
| `a574078` | Impede injecao de linha e formula no cabecalho das exportacoes |
| `8c73555` | Confere versao nas acoes, estende prazo ao sair da pausa e endurece o servico |

## Testes

- 90 testes TestNG passando após as correções da revisão final (dez classes: `ReferenciaEditalTest`, `MaquinaEstadosTest`, `CalculadoraSlaTest`, `GeradorProtocoloTest`, `ArmazenamentoAnexoTest`, `ExportacaoChamadosTest`, `EmailChamadoTest`, `FiltroChamadosTest`, `CadastrosChamadoTest`, `ChamadoServiceRegrasTest`).
- Build `BUILD SUCCESS` com o comando do projeto.
- Os testes foram executados direto pelo TestNG (ruling 4).

## Decisões tomadas

Rulings da fase 1:

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

- Verificação no navegador (polling de 30 s, gráficos dos relatórios, PDF no cliente, fluxo do perfil Gestor): feita só por API na CH-2; fica para a homologação.
- Capítulo 7 do TR ausente do repositório: prazos por prioridade e categorias são os sugeridos pela spec e podem ser ajustados pela configuração.
- Canais de abertura por e-mail (7.2.2.c) e por mensageria (7.2.2.d) fora do escopo; a coluna `origem` está preparada.
- O polling recarrega a página atual a cada 30 s, sem o parâmetro `desde` previsto na spec.
- Itens menores conhecidos: o seed usa `MAX(id_grupo)+1` sem `ISNULL` e fixa o grupo 11 nos direitos.
