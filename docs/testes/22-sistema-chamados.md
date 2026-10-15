# Testes — Plano 22: Sistema de Gerenciamento de Chamados

Roteiro manual. Os testes automatizados (76 do módulo `chamado` + `ReferenciaEditalTest`) rodam sem banco; este roteiro cobre a integração com banco, servidor, SMTP e navegador.

## Pré-requisitos

- Migração `20261015_sistema_chamados.sql` e seed `20261015_sistema_chamados_seed.sql` executados em `docs/banco-de-dados/migracoes/` (com `sqlcmd -b -f 65001`: `-b` faz o `sqlcmd` sair com erro e `-f 65001` preserva os acentos do seed).
- Três usuários de teste, um em cada grupo criado pelo seed: "Chamados – Solicitante", "Chamados – Gestor do contrato" e "Chamados – Equipe técnica". Um segundo usuário solicitante é necessário para o teste de visibilidade (T08).
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

### T06 — Configuração de prioridades (Tarefa 12, equipe)

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

**Resultado esperado:** o card "Chamados" aparece em "Cadastro e Configuração" para os três grupos; "Configuração de Chamados" só para a equipe; "Relatórios de Chamados" em "Análise de Dados" só para gestor e equipe. Cada card traz o asterisco vermelho do edital e o tooltip lista os itens 7.2.3.x e 7.2.4 correspondentes.
