# Testes — Plano 22: Sistema de Gerenciamento de Chamados

Roteiro manual. Os testes automatizados (98 TestNG: módulo `chamado` + `ReferenciaEditalTest`) rodam sem banco; este roteiro cobre a integração com banco, servidor, SMTP e navegador.

## Pré-requisitos

- Migração `20261015_sistema_chamados.sql`, seed `20261015_sistema_chamados_seed.sql` e complemento `20261015_sistema_chamados_admin.sql` executados em `docs/banco-de-dados/migracoes/` (com `sqlcmd -b -f 65001`: `-b` faz o `sqlcmd` sair com erro e `-f 65001` preserva os acentos do seed).
- Três usuários de teste, um em cada grupo criado pelo seed: "Chamados - Solicitante", "Chamados - Gestor do contrato" e "Chamados - Equipe tecnica", e um usuário do grupo 11 (Administradores) para a configuração. Um segundo usuário solicitante é necessário para o teste de visibilidade (T08). Em desenvolvimento, `20261015_usuarios_teste_chamados.sql` cria um administrador e um solicitante de teste (a seção de limpeza no cabeçalho os remove).
- Diretório de anexos existente e gravável, conforme `chamado_diretorio_anexos` em `muralha.config_chave_valor` (padrão `C:\Consilux\MuralhaDigital\chamados`).
- SMTP configurado (primeiro servidor de `ConfiguracaoProvider`) e `chamado_email_ativo = 1`.
- Servidor rodando (`..\.setup-gtw\run.ps1`); jobs `ChamadoSlaJob` e `ChamadoEmailJob` registrados no log de inicialização.

## Casos de teste

### T01 — Lista e abertura de chamado (Tarefa 10, solicitante)

Abrir `http://localhost:8080/muralha-digital/pages/chamados/index.jsp` com o usuário solicitante.

1. Botão "Novo chamado" visível; "Relatórios" e "Configuração" ocultos.
2. Abrir chamado em "Equipamento → Falha de energia" sem equipamento: o navegador exige o campo.
3. Abrir com equipamento, prioridade Crítica e um PDF.
4. Abrir outro com título `<b>teste</b>`.
5. Exportar CSV, XLS e PDF da lista.

**Resultado esperado:** (3) SweetAlert com protocolo `CH-2026-000001`, prioridade e prazos (+1 h / +4 h); (4) o título aparece literalmente na lista, sem negrito; (5) os três arquivos baixam e o rodapé do CSV e o PDF mostram o mesmo SHA-256.

### T02 — Ciclo do chamado no detalhe (Tarefa 11, equipe e solicitante)

1. **Equipe:** abrir o chamado do T01. Aparecem "Assumir", "Iniciar atendimento", "Cancelar" e "Reclassificar". Iniciar com observação.
2. **Equipe:** "Aguardar solicitante".
3. **Solicitante (autor):** em outra janela, aguardar até 30 s e usar "Responder" com texto e um PNG.
4. **Equipe:** "Reclassificar" para Alta com justificativa; depois conferir `SELECT * FROM muralha.chamado_notificacao`.
5. **Equipe:** "Resolver". **Solicitante:** "Avaliar e fechar" com 4 estrelas.

**Resultado esperado:** (1) SLA de atendimento "Cumprido"; (2) badge "relógio pausado" e contador de resolução parado; (3) a tela atualiza sozinha em até 30 s, o chamado volta para "Em atendimento" e o anexo aparece no evento do histórico; (4) prazos recalculados, histórico com "Prioridade alterada de Crítica para Alta" e notificação `RECLASSIFICACAO`; (5) status Fechado com o card de avaliação.

### T03 — Concorrência (Tarefa 11)

Abrir o mesmo chamado em duas janelas da equipe. Resolver em uma e, sem recarregar, usar "Aguardar" na outra.

**Resultado esperado:** mensagem "Chamado alterado por outro usuário; recarregue a página."

### T04 — Relatórios (Tarefa 12, gestor)

Abrir `relatorios.jsp` com o gestor, agrupar por mês e depois por "Equipamento" e "Categoria"; exportar CSV, XLS e PDF.

**Resultado esperado:** cards, gráfico e tabela; os três formatos baixam com o mesmo SHA-256.

### T05 — Acesso aos relatórios pelo solicitante (Tarefa 12)

Abrir `relatorios.jsp` com o solicitante.

**Resultado esperado:** acesso bloqueado (página negada por `sis_menu_direitos`).

### T06 — Configuração de prioridades (Tarefa 12, administrador)

1. Em `configuracao.jsp`, mudar "Média" para 120 / 720 min e abrir um chamado novo com prioridade Média.
2. Tentar salvar resolução 30 menor que atendimento 60.

**Resultado esperado:** (1) prazos de +2 h / +12 h; (2) mensagem de validação.

### T07 — Desativação de subcategoria (Tarefa 12)

Desativar uma subcategoria em `configuracao.jsp`.

**Resultado esperado:** some do modal "Novo chamado"; a lista continua mostrando os chamados antigos que a usam.

### T08 — Visibilidade por perfil (Tarefa 11)

Com outro solicitante, abrir `detalhe.jsp?protocolo=CH-2026-000001` e `/MuralhaDigital/Chamado?acao=anexo&id=1`.

**Resultado esperado:** o detalhe volta para a lista (404) e o anexo responde JSON `Chamado não encontrado.`

### T09 — SLA acelerado

1. Em `configuracao.jsp`, criar ou ajustar uma prioridade de teste com 2 min de atendimento e 5 min de resolução.
2. Abrir um chamado com essa prioridade e aguardar o `ChamadoSlaJob` (execução a cada 5 min).

**Resultado esperado:** o histórico registra "SLA de atendimento: EM_RISCO" e depois "VENCIDO"; a lista mostra os badges correspondentes.

### T10 — Fechamento automático

1. Com autorização: `UPDATE muralha.config_chave_valor SET valor='0' WHERE chave='chamado_fechamento_auto_dias'`.
2. Deixar um chamado em `RESOLVIDO` e aguardar o job.
3. Restaurar o valor: `UPDATE muralha.config_chave_valor SET valor='5' WHERE chave='chamado_fechamento_auto_dias'`.

**Resultado esperado:** o chamado `RESOLVIDO` fecha em até 5 min como "não avaliado" (`fechado_automaticamente = 1`).

### T11 — E-mail

1. Abrir um chamado e consultar `SELECT status, tentativas, erro FROM muralha.chamado_notificacao ORDER BY id DESC`.
2. Com SMTP inválido, abrir outro chamado e acompanhar a notificação por 5 minutos.
3. Abrir chamado com usuário sem e-mail cadastrado.

**Resultado esperado:** (1) `ENVIADO` e e-mail recebido com protocolo, prioridade e prazos; (2) a notificação chega a `ERRO` após 5 tentativas; (3) `ERRO` imediato com `Solicitante sem e-mail`, e a abertura ocorre normalmente.

### T12 — Anexo malicioso

Anexar um `.exe` renomeado para `.pdf` na abertura de um chamado.

**Resultado esperado:** mensagem "O conteúdo não corresponde à extensão do arquivo"; nenhum arquivo novo no diretório de anexos e nenhum chamado criado.

### T13 — Auditoria

Abrir `pages/auditoria/consulta-log.jsp` e filtrar a funcionalidade "Chamado".

**Resultado esperado:** o log mostra ABRIR, as transições executadas nos testes anteriores e EXPORTAR.

### T14 — Menu e referência ao edital

Abrir a tela inicial do Muralha Digital com um usuário de cada grupo.

**Resultado esperado:** a categoria "Sistema de Chamados" aparece na tela inicial para os três grupos e para o administrador; dentro dela, o card "Chamados" para todos, "Relatórios de Chamados" só para gestor, equipe e administrador, e "Configuração de Chamados" só para o administrador (ver T17). Cada card traz o asterisco vermelho do edital e o tooltip lista os itens 7.2.3.x e 7.2.4 correspondentes (a categoria, 7.2.3 e 7.2.4).

### T15 — Configuração só para o administrador (complemento)

1. Com o administrador (grupo 11), abrir `configuracao.jsp`.
2. Com o solicitante, o gestor e a equipe, abrir `configuracao.jsp` pela URL e chamar `GET /MuralhaDigital/ChamadoConfig?acao=parametros`.

**Resultado esperado:** (1) abas "Parâmetros de SLA", "Perfis de acesso", "Prioridades e prazos" e "Categorias"; (2) página negada (redireciona para `/login/gtw_principal.jsp`) e a API responde `{ok:false, erro:"Seu perfil não permite esta operação."}`.

### T16 — Parâmetros de SLA (complemento)

1. Na aba "Parâmetros de SLA", salvar risco 75 % e fechamento automático em 4 dias; recarregar.
2. Tentar salvar risco 0 ou 100 e fechamento 0 ou 61.
3. Restaurar 80 % / 5 dias.

**Resultado esperado:** (1) os valores persistem; (2) mensagens "O percentual de risco deve estar entre 1 e 99." e "O prazo de fechamento automático deve estar entre 1 e 60 dias.", sem alterar o banco; (3) cada alteração gera linha em `muralha.config_chave_valor_hist` e auditoria `SALVAR_PARAMETROS_SLA`.

### T17 — Perfis de acesso (complemento)

1. Na aba "Perfis de acesso", buscar um usuário pelo nome ou login e incluí-lo como Solicitante.
2. Com esse usuário, entrar no sistema e consultar `GET /MuralhaDigital/Chamado?acao=perfil`.
3. Tentar incluir alguém no perfil `ADMIN` pela API (`acao=adicionarMembro&perfil=ADMIN`).
4. Remover o usuário do perfil.

**Resultado esperado:** (1) o usuário aparece no cartão do perfil (mesma tabela `dbo.sis_usuario_grupo` da tela legada de usuários); (2) `["SOLICITANTE"]` e a categoria "Sistema de Chamados" com o card "Chamados"; (3) `{ok:false, erro:"Este perfil não é gerenciado por aqui."}`; (4) o perfil some e a auditoria registra `ADICIONAR_PERFIL` e `REMOVER_PERFIL`.

### T18 — Categoria e ícones (complemento)

Abrir `/login/muralha_principal.jsp` e clicar em "Sistema de Chamados" (`?menu_info_pai=49`).

**Resultado esperado:** card da categoria com `menu-icons/chamados-categoria.svg`; dentro dela, os cards com `chamados.svg`, `chamados-relatorios.svg` e `chamados-configuracao.svg`, conforme os direitos do T14.

### T19 — Motor de logs (complemento)

Abrir `pages/auditoria/consulta-log.jsp` com funcionalidade "Chamado" ou consultar `SELECT funcionalidade, operacao, id_registro, descricao FROM dbo.sis_log_auditoria WHERE funcionalidade = 'Chamado'`.

**Resultado esperado:** `ABRIR`, as transições (`INICIAR`, `AGUARDAR`, `RESPONDER`, `RESOLVER`, `AVALIAR`…), `RECLASSIFICAR`, `EXPORTAR`, `SALVAR_PARAMETROS_SLA`, `ADICIONAR_PERFIL` e `REMOVER_PERFIL`, com o usuário e o IP de quem executou.

## Execução de 2026-10-15

Teste integrado por HTTP (sessões `curl` com cookie, sem navegador) no servidor local com a build da branch `feat/plano-22-sistema-chamados`, banco `GTW_MURALHA_DEV` já migrado. Usuários de teste `TESTE.CHAMADO.ADM` (grupo 11) e `TESTE.CHAMADO.SOL` (sem grupos; incluído como Solicitante pelo administrador), criados por `20261015_usuarios_teste_chamados.sql` e removidos ao final pela seção de limpeza do mesmo script. Resultado: **10 de 10 casos passaram**.

| # | Caso | Resultado | Evidência |
|---|---|---|---|
| 1 | Categoria e ícones; Configuração só para o administrador (T14, T15, T18) | Passou | ADM: tela inicial com "Sistema de Chamados" e `chamados-categoria.svg`; `?menu_info_pai=49` com `chamados.svg`, `chamados-relatorios.svg` e `chamados-configuracao.svg`. SOL: categoria e `chamados.svg` presentes, relatórios e configuração ausentes; `configuracao.jsp` e `relatorios.jsp` → 302 para `/login/gtw_principal.jsp` |
| 2 | Parâmetros de SLA (T16) | Passou | `parametros` → 80/5; salvar 75/4 → `ok:true` e GET confirma; 0, 100 e fechamento 0 → `ok:false` com a mensagem de faixa; restaurado 80/5 |
| 3 | Perfis de acesso (T17) | Passou | `buscarUsuarios&termo=TESTE` encontrou o SOL; `adicionarMembro SOLICITANTE` → `ok:true`; `perfis` lista o SOL; `adicionarMembro ADMIN` → "Este perfil não é gerenciado por aqui." |
| 4 | Solicitante sem acesso à configuração (T15) | Passou | SOL: `ChamadoConfig?acao=parametros` → `ok:false` "Seu perfil não permite esta operação."; `Chamado?acao=perfil` → `["SOLICITANTE"]` |
| 5 | Abertura com anexos (T01, T12) | Passou | Com PDF válido + "PDF" com bytes `MZ`: "O conteúdo não corresponde à extensão do arquivo: falso.pdf" e nenhum chamado criado; só com o PDF: `CH-2026-000001`, Crítica, prazos +60/+240 min; banco com histórico `ABERTURA`, anexo com SHA-256 igual ao do arquivo em `C:\Consilux\MuralhaDigital\chamados\2026\CH-2026-000001\` e notificação `CONFIRMACAO_ABERTURA` |
| 6 | Ciclo completo e concorrência (T02, T03) | Passou | `iniciar` → `EM_ATENDIMENTO` com `sla_atendimento = CUMPRIDO`; `aguardar` com versão antiga → "Chamado alterado por outro usuário; recarregue a página."; `aguardar`, `responder` (SOL), `reclassificar` para Alta (prazos +120/+480 min a partir da abertura e notificação `RECLASSIFICACAO`), `resolver`, `avaliar` nota 4 → `FECHADO` |
| 7 | Visibilidade (T08) | Passou | ADM só com `ADMIN`: `listar` e `detalhe` mostram os chamados, `acoesPermitidas` vazio num chamado `ABERTO` e `iniciar`/`cancelar` recusados; SOL com protocolo inexistente e com protocolo de outro solicitante → HTTP 404 `{"ok":false,"erro":"Chamado não encontrado."}` (também no POST de ação) |
| 8 | Exportação e indicadores (T01, T04) | Passou | SOL: CSV, XLSX (aba Metadados) e JSON com o mesmo SHA-256 (`8a798bb4…`); ADM: `indicadores&agrupamento=mes` com a linha `2026-10`; SOL em `indicadores` → recusado |
| 9 | Motor de logs (T13, T19) | Passou | `sis_log_auditoria` (funcionalidade "Chamado") com `ABRIR` (3), `INICIAR`, `AGUARDAR`, `RESPONDER`, `RECLASSIFICAR`, `RESOLVER`, `AVALIAR`, `EXPORTAR` (4), `SALVAR_PARAMETROS_SLA` (2), `ADICIONAR_PERFIL` (3) e `REMOVER_PERFIL` (2) |
| 10 | Jobs e e-mail (T11) | Passou | Log: "Job Chamados SLA agendada a cada 5 minutos." e "Job Chamados e-mail agendada a cada 1 minuto."; notificações dos usuários de teste com tentativas crescentes e status final `ERRO` ("Falha no envio SMTP"), sem envio real |

Limpeza: a seção de limpeza de `20261015_usuarios_teste_chamados.sql` removeu os dois usuários, seus grupos, complemento, logons e `sis_log`, os chamados `CH-2026-000001` a `000003` com histórico, anexos e notificações, e o contador de protocolo de 2026 (criado pelo teste); as pastas de anexos foram apagadas do disco e os parâmetros de SLA ficaram em 80 % / 5 dias. Os registros de `dbo.sis_log_auditoria` e de `muralha.config_chave_valor_hist` foram mantidos como log.

Observação: o SMTP configurado no ambiente local recusa a autenticação (`535 5.7.8 BadCredentials`), por isso nenhuma mensagem saiu do servidor; o envio com sucesso (T11, item 1) continua dependendo de um SMTP válido.
