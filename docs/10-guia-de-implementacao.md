# 10 — Guia de Implementação (receitas)

Antes de qualquer receita: leia `00`, `02`, `05` e `06`; confirme se já existe algo parecido (`referencia/` e `banco-de-dados/objetos-programaveis.md`). Build/teste: `..\.setup-gtw\build.ps1` / `run.ps1` (ver `01`).

## 1. Nova tela de listagem + CRUD (Muralha)

Exemplo de referência canônico: `guarnicao` (`pages/guarnicao/`, `GuarnicaoServlet`, `Guarnicao`, `Guarnicoes`).

1. **Banco** (se precisar): tabela em `muralha.` (`id` identity, campos, `ativo`, `data_criacao`), via script em `docs/banco-de-dados/migracoes/` (§6).
2. **Domínio** `muralha.digital.<modulo>`: `Entidade.java` (POJO + métodos estáticos JDBC) e `Entidades.java` (listagens). Seguir `06` §2.
3. **Servlet** `@WebServlet("/MuralhaDigital/Entidade")` com `acao`s `listar`, `obterPorId`, `cadastrar`, `atualizar`, `deletar` (soft delete). Incluir verificação de sessão/acesso (`06` §1.1).
4. **JSP** `pages/<modulo>/listagem-<entidades>.jsp`: incluir `cabecalho_bootstrap_simples.jsp`; `h2.text-center`; botão "Nova …" (`btn btn-sm btn-success`); tabela `table table-striped align-middle` com estado "Carregando…"; modais `modal-*.jsp` incluídos no fim.
5. **JS** `assets/js/listagem-<entidades>.js`: `listar()` via `$.ajax GET acao=listar` → renderiza linhas com botões `btn-outline-* btn-sm` + `title` + `bi-*`; delegação de eventos; `Swal.fire` para confirmar/avisar.
6. **CSS** `assets/css/…` apenas se necessário.
7. **Menu e permissão**: §4.
8. **Teste manual**: login com usuário do grupo autorizado e de um grupo sem permissão; cadastrar, editar, excluir; olhar `GTW_MURALHA_DIGITAL-error.log`.
9. **Documentar**: acrescentar a tela em `04-modulos-funcionais.md` e regenerar `referencia/*` (§7).

## 2. Nova tela de consulta com filtros (grandes volumes)

Referência: `consulta-veiculo/consulta.jsp`.
- Filtros: placa (máscara + curinga), `Data Início/Fim` (Tempus Dominus), equipamento (`selectpicker multiple` carregado por `carregar-combo-equipamentos.js`), faixa, classificação, flags. Todos os filtros devem ter **limites** (período máximo, `LimiteConsultaAtivo`) e exigir **motivo** quando a consulta expõe dados pessoais.
- Servidor: usar `spu_ObterVeiculosPorFiltros`/equivalente com `TOP`/paginação; **nunca** consultar `veiculo_tempo_real` sem data/equipamento.
- Resultado: DataTables ou `twbsPagination` (servidor); exportar com SheetJS/jsPDF (`botoes-exportar-lista.css`).
- Detalhe em modal (`modal-detalhe-veiculo.jsp`) e imagens por `Veiculo/Imagem`.

## 3. Novo endpoint em módulo existente
1. Adicionar `acao` no `doGet`/`doPost` do servlet do módulo (não criar servlet novo se já existe o da entidade).
2. Implementar o método de domínio; reaproveitar `spu_*` existentes.
3. Responder JSON conforme `06` §1. Documentar a ação em `04` e `referencia/modulos-backend-muralha.md` (regenerar).
4. Não alterar o formato de respostas existentes (o JS depende de nomes de campos). Se precisar mudar, **adicione** campos.

## 4. Registrar funcionalidade, menu e permissão
1. `sis_menu` (funcionalidade protegida): `acao` = URL exata da JSP/servlet; `tipo='U'`; `nivel=1`; `id_pai_menu` = pai (ex.: 222 Anel de Segurança); `nome_sistema='Muralha-Digital'`; `Ativo=1`.
2. `sis_menu_direitos`: uma linha por grupo (`id_grupo`) que pode usar.
3. Navbar: item em `sis_menu_infos` (Processamento; `href`, `ordenacao`, `menu_pai` = id do submenu), `sis_menu_relatorio_infos` (Relatórios) ou `sis_menu_graficos_infos` (Gráficos).
4. Escopo fino (por tipo de alerta/notificação): `muralha.config_grupo_permissao`.
5. Scripts de carga em `docs/banco-de-dados/migracoes/`; executar em DEV após aprovação.

## 5. Novo relatório
1. Se agregação pesada: `muralha.spu_Relatorio<Nome>` (parâmetros: período, equipamentos CSV, tipos); testar plano de execução em volume real.
2. Servlet `/MuralhaDigital/Relatorios/<Nome>` (ou `/Relatorio/<Nome>`), verificando acesso e **registrando motivo** (`MotivoSolicitacaoRelatorio.registrarMotivoSolicitacaoRelatorio`) e auditoria.
3. JSP `pages/relatorios/<nome>.jsp` seguindo `alertas-detalhado.jsp` (form-container, `report-title`, botões `-custom`, spinner, export Excel/PDF, impressão).
4. Cadastrar em `sis_menu` + `sis_menu_relatorio_infos`.
5. Gráficos: Chart.js com paleta `05` §3.4.
6. Relatório Jasper (GTW clássico): `.jrxml` em `relatorios/`, servlet em `com.consilux.servlet.relatorio`.

## 6. Mudanças de banco de dados
- **Nunca** executar DDL/DML de estrutura sem autorização explícita. Produza o script, não o aplique.
- Local: `docs/banco-de-dados/migracoes/AAAAMMDD_HHmm_descricao.sql`; cabeçalho com objetivo, autor, ticket, ambiente alvo; idempotente; rollback comentado; schema explícito; compatível com SQL Server 2016.
- Após aprovado e aplicado, re-extrair o catálogo (`docs/banco-de-dados/README.md` §"Regenerar") e atualizar `dominios.md` se mudou domínio.
- Alterar procedures existentes: salvar a versão anterior em `codigo-sql/` (já é espelho do DEV) e anotar no PR; lembrar que há procedures consumidas por vários módulos (ver "Referencia" em `objetos-programaveis.md`).

## 7. Regenerar a documentação de referência
Os arquivos de `docs/referencia/` e `docs/banco-de-dados/` foram **gerados por script** em 2026-10-08 (varredura de `web.xml`, `@WebServlet`, JSP/JS e `sys.*` do SQL Server). Ao final de mudanças relevantes, peça a um agente para regenerar (ver `agentes/prompts/regenerar-referencia.md`) ou ajustar manualmente a seção afetada.

## 8. Novo job agendado
1. Classe `Job<Nome> implements org.quartz.Job` no módulo.
2. Registrar em `com.consilux.servlet.ferramentas.Agendador` (constante de nome + agendamento condicionado à configuração), chave de configuração em `muralha-digital-config.xml`/`config_chave_valor`.
3. Idempotência e lock (`muralha.controla_execucao_job`) quando houver risco de execução duplicada; logar início/fim/erros; **nunca** deixar o job sem `try/catch`.
4. Lembrete: Quartz usa `RAMJobStore` — reinício do Tomcat recria agendamentos a partir da config.

## 9. Novo canal de tempo real
1. Constante em `Constantes.ClientesWS`; produtor em thread própria iniciada em `ClienteSessoes.iniciaListaClientesWebSockets` (ciclo ≥ 1 s, `try/catch` por iteração, respeitar `continua`).
2. Mensagem XML estável (documentar esquema em `08`); filtrar por usuário/grupo antes de enviar (`Cliente.getIdUsuario`).
3. Front: reaproveitar o padrão de `cabecalho.js` (conexão, reconexão, parse XML).

## 10. Nova integração externa
Seguir checklist de `07` §5; isolar em classe `Servico<Nome>` no pacote do módulo; timeouts explícitos; tratar falha sem derrubar o fluxo principal; não logar credenciais.

## 11. Definição de pronto (DoD)
- [ ] Segue `05` e `06`; sem nova dependência
- [ ] Verifica sessão e permissão; escopo de dados aplicado
- [ ] Sem segredos; configuração em `.example` atualizada
- [ ] SQL parametrizado; conexões fechadas; consultas com limites
- [ ] Testado manualmente (caminho feliz, erro, sem permissão) e build `BUILD SUCCESS`
- [ ] Docs atualizadas (`04`, `referencia/`, `banco-de-dados/` se aplicável)
- [ ] Commit em PT-BR sem arquivos indevidos (`git status` limpo de `target/`, logs, configs locais)
