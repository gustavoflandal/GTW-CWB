# Correções Pontuais da Auditoria — Plano 24 (Fase 2 da auditoria de 2026-10-16)

> **Status:** Executado (2026-10-16) — ver [Entrega 24](../entregas/24-correcoes-pontuais-auditoria.md).
> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) (a §1 e a §10 foram corrigidas nesta fase).
> **Depende de:** [Plano 23](23-correcao-documentacao-aderencia.md) (Fase 1).

**Objetivo:** corrigir os defeitos pontuais que a auditoria de 2026-10-16 encontrou no código e nos scripts, sem mudar a stack nem executar nada no banco.

## Referência ao Edital (TR TRANSALVADOR)

Esta fase corrige defeitos; nenhum item do TR muda de situação na [matriz canônica](../entregas/itens-edital-x-entregas.md). O item 6 completa a integração com a Waze da [Entrega 19](../entregas/19-sensores-externos.md).

## Tarefas

- [x] 1. **Prioridade de tipo de alerta.** A auditoria apontou `WHERE prioridade = ?` em `ConfiguracaoSons.ObterPrioridade` como erro. Na verdade a tela (`configuracao-monitoramento/assets/js/configuracao.js`) envia o **valor** da prioridade para saber se outro tipo já o usa, então a busca por `prioridade` está certa; trocar por `WHERE id = ?` quebraria a checagem. O defeito real: o próprio tipo em edição não era excluído (regravar a prioridade atual era recusado como duplicada) e o parâmetro se chamava `id`. Correção: `ObterPrioridade(prioridade, idTipo)` com `AND id <> ?`, `setInt` e validação do número; a tela envia `prioridade` e `idTipo` (o servlet ainda aceita `id`). Teste `ConfiguracaoSonsPrioridadeTest`.
- [x] 2. **`RelatorioAlteracaoConfigEquip` (menu 201).** `doGet` vazio. Não existe tabela com valor anterior e novo dos parâmetros, mas `dbo.local` guarda versões por `sequencia_local` (`data_atualizacao`) e `dbo.configuracao_equipamento` guarda `data_inicio`, `data_fim`, `data_modificacao` e `id_usuario` da última alteração. Implementada a consulta mínima sobre esses dados (opção recomendada; a escolha foi pedida ao usuário em 2026-10-16 e ainda não veio, então pode virar "desativar o menu"): local e período da tela existente, filtro pelo grupo de equipamentos do usuário, período de até 366 dias, até 5 000 linhas, resultado em `relatorio/res_alteracoes_configequip.jsp`. O histórico completo fica para a Fase 4 (5.4.7.6). Teste `RelatorioAlteracaoConfigEquipTest`.
- [x] 3. **`AuditoriaService`.** Passa a ler `SessaoConstantes.SESSAO_USUARIO` primeiro (objeto `com.consilux.model.Usuario`, gravado por todos os logins) e `objUsuario` só como reserva. A errata §1 e §10 mandava usar `getIdUsuario()`, que não existe em `com.consilux.model.Usuario` (o método é `getId()`); a errata foi corrigida.
- [x] 4. **`muralha.vtr_imagem_complemento`.** Script próprio e idempotente `20261016_vtr_imagem_complemento.sql`; o cabeçalho do script obsoleto deixa de mandar descartar a tabela; README de migrações atualizado.
- [x] 5. **IP fixo em `maps-config.js`.** Chave `mapa_api_monitoramento_trecho` em `muralha.config_chave_valor` (script `20261016_config_mapa_api_trecho.sql`), lida por `MapaConfigServlet` (`/MuralhaDigital/MapaConfig?acao=obterApiMonitoramentoTrecho`, sessão e acesso verificados, formato `host[:porta]` validado). Documentado em `07-configuracao-e-segredos.md` §4.1. Teste `MapaConfigServletTest`.
- [x] 6. **Waze `jams`.** `IncidenteImportJob` grava também os congestionamentos com tipo `JAM`. Correção junto: a data do incidente passa a ser o `pubMillis` do item (antes era a hora da importação), senão a chave única `(fonte, id_externo, dt_ocorrencia)` nunca barrava a regravação do mesmo alerta. Teste `IncidenteImportJobTest` com JSON de exemplo.
- [x] 7. **`Funcoes.invertImage` / `ObterImagemModificada`.** Confirmado sem chamadas no código. Mantido, sem alteração, para possível uso na Fase 9 (inversão de cores, 5.5.4.3).

## Critérios de aceite

1. `..\.setup-gtw\build.ps1` compila sem erro.
2. Os testes TestNG novos (`ConfiguracaoSonsPrioridadeTest`, `MapaConfigServletTest`, `IncidenteImportJobTest`, `RelatorioAlteracaoConfigEquipTest`) passam.
3. Roteiro manual [`docs/testes/24-correcoes-pontuais-auditoria.md`](../testes/24-correcoes-pontuais-auditoria.md) executado.
4. `python -I docs/referencia/scripts/validar_aderencia.py` continua sem erro.
