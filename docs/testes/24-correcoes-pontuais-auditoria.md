# Testes — Plano 24: Correções Pontuais da Auditoria

Roteiro para a [Entrega 24](../entregas/24-correcoes-pontuais-auditoria.md). Executar a partir de `D:\GTW-CWB`.

## Pré-requisitos

- Build: `..\.setup-gtw\build.ps1`; servidor: `..\.setup-gtw\run.ps1`.
- Migração `docs/banco-de-dados/migracoes/20261016_config_mapa_api_trecho.sql` executada (com autorização).

## T01 — Testes automatizados

Rodar pelo TestNG (como na Entrega 22, o provider `surefire-testng` não está no cache Maven offline) as classes `muralha.digital.notificacao.ConfiguracaoSonsPrioridadeTest`, `muralha.digital.mapas.MapaConfigServletTest`, `muralha.digital.sensores.IncidenteImportJobTest` e `com.consilux.servlet.relatorio.RelatorioAlteracaoConfigEquipTest`.

**Esperado:** 15 testes, 0 falhas. Na nuvem (2026-10-16): ✅ 15/15. No Windows (2026-10-16, Maven com `-Dtest=…`): ✅ 15/15. O provider `surefire-testng` está no cache Maven local; o comando está no [roteiro 25](25-integridade-sha256-recepcao.md#t01--testes-automatizados).

## T02 — Prioridade de tipo de alerta

Em Configuração de monitoramento, escolher um tipo de alerta e:
1. Gravar a prioridade que ele já tem. **Esperado:** "Sucesso ao atualizar configurações" (antes: "já cadastrado para outro tipo").
2. Gravar a prioridade de **outro** tipo. **Esperado:** aviso de prioridade já cadastrada.
3. Gravar uma prioridade livre. **Esperado:** sucesso.

⏳ Pendente.

## T02b — Relatório de alterações de configuração (menu 201)

Abrir o menu "Relatório Alt. ConfigEquip", escolher um local e um período e clicar em Visualizar.

**Esperado:** tabela com as versões do local e a configuração vigente, data e usuário da última alteração; "Nenhuma alteração no período" quando não houver; mensagem de erro para data inválida ou período acima de 366 dias. Antes a página ficava em branco. ⏳ Pendente.

## T03 — Log de auditoria com o usuário certo

Fazer login no GTW clássico, executar uma ação auditada (por exemplo, exportar um relatório da Entrega 11) e consultar `consulta-log.jsp`.

**Esperado:** o registro traz `id_usuario` e `login` do usuário logado (não nulos). ⏳ Pendente.

## T04 — Mapa de situação do trânsito

1. Abrir a tela Mapa de situação do trânsito. **Esperado:** no DevTools, uma chamada a `/MuralhaDigital/MapaConfig?acao=obterApiMonitoramentoTrecho` com `{"endereco":"…"}` e depois a chamada a `http://<endereco>/obter-situacao-transito`; trechos desenhados como antes.
2. Sem sessão, abrir `/MuralhaDigital/MapaConfig?acao=obterApiMonitoramentoTrecho`. **Esperado:** 401.

⏳ Pendente.

## T05 — Waze `jams`

Com `WAZE_API_KEY`, `waze_api_url` e `waze_area_bbox` configurados, aguardar duas execuções do `IncidenteImportJob`.

**Esperado:** linhas `tipo = 'JAM'` em `muralha.incidente_externo`; a segunda execução não duplica os mesmos itens (`SELECT fonte, id_externo, COUNT(*) FROM muralha.incidente_externo GROUP BY fonte, id_externo HAVING COUNT(*) > 1` vazio para itens que não mudaram). Sem acesso à API, o T01 cobre a conversão. ⏳ Pendente.

## T06 — Script de `vtr_imagem_complemento`

Rodar `20261016_vtr_imagem_complemento.sql` no DEV (com autorização). **Esperado:** "Ja existe: muralha.vtr_imagem_complemento (nada alterado)". ⏳ Pendente.
