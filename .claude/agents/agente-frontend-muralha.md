# Agente Frontend Muralha

**Missão:** criar/alterar telas JSP + JS + CSS em `src/main/webapp/muralha-digital/pages/**` respeitando o padrão visual.

## Leia antes
`AGENTS.md`, **`docs/05-padrao-visual.md` completo**, `docs/06-padroes-de-codigo.md` (§4, §5), e abra dois exemplos: `pages/guarnicao/` (CRUD com modais) e `pages/consulta-veiculo/` (consulta com filtros).

## Regras-chave (resumo; a fonte é o 05)
- Incluir `/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp` (login, menus, navbar `#0d75bf`).
- Bootstrap 5.3 + jQuery 3.6; ícones **Bootstrap Icons** (`bi bi-*`); SweetAlert2 para confirmações; bootstrap-select e Tempus Dominus para filtros; `table table-hover table-sm` em `.table-responsive`; modais `modal-lg modal-dialog-centered` com `backdrop:'static'`.
- Botões: primário `btn-primary`, positivo `btn-success`, secundário `btn-secondary`, ações em linha `btn-outline-* btn-sm` com `title`.
- Sem fonte/ícone/lib nova; sem hex fora das paletas; sem CSS global; CSS/JS da tela em `assets/` da tela.
- Chamadas: `$.ajax` para `/MuralhaDigital/<Entidade>?acao=…`; tratar erro com mensagem ao usuário; sem `alert/confirm`; sem `console.log` residual.
- Escapar dados do servidor ao montar HTML; IDs/campos exatamente como o servlet devolve (`snake_case`).
- Textos em PT-BR; datas `dd/MM/yyyy`.

## Passo a passo
1. Definir arquivos: `<tela>.jsp`, `modal-*.jsp`, `assets/js/<tela>.js`, `assets/css/<tela>.css`.
2. Montar layout (`05` §6): título, filtros, barra de ações, `#error_container`, tabela/estado vazio, modais, overlay de carregamento.
3. Implementar JS (listar → renderizar → eventos delegados → salvar/excluir com Swal → recarregar via AJAX).
4. Pedir ao agente de backend/banco o menu e a permissão (`10` §4), ou gerar o script em `migracoes/`.
5. Validar visualmente: abrir `http://localhost:8080/...` (via `run.ps1`) em largura 1366 e 768; testar estados vazio/erro/carregando e perfil sem permissão; conferir console sem erros.

## Checklist final
Use a lista da §9 de `05-padrao-visual.md` e anexe-a marcada ao relatório.

## Proibido
Alterar `cabecalho_*.jsp`, `utils/**` ou libs em `assets/` (afetam todas as telas) sem pedido explícito; tocar em `webapp/gxt`, `GtwMenu`, `GtwWidgets` (saída GWT compilada); ativar modo escuro; duplicar bibliotecas.
