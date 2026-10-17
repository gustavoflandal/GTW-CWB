# Testes — Entrega 33: Descarte de Time-lapse

## Testes unitários (TestNG)

| # | Classe | Método | Verificação |
|---|---|---|---|
| 1 | `TimelapseDescarteTest` | `testDescarteRegistrosAntigos` | Registros com `dt_geracao` > 72h são removidos |
| 2 | `TimelapseDescarteTest` | `testRetencaoConfiguravelViaConfig` | Período de retenção obedece `TIMELAPSE_RETENCAO_HORAS` |
| 3 | `TimelapseDescarteTest` | `testRegistrosRecentesPreservados` | Registros dentro do período não são removidos |
| 4 | `TimelapseDescarteTest` | `testDesabilitadoViaConfig` | Com `TIMELAPSE_DESCARTE_HABILITADO=false`, nenhuma remoção |
| 5 | `TimelapseDescarteTest` | `testRegistroSessaoNoCache` | Ao carregar frames, sessão é registrada em `timelapse_cache` |
| 6 | `TimelapseDescarteTest` | `testCamposDoRegistroCache` | Registro contém id_local, período, total_frames, id_usuario |

**Total: 6 testes**

## Teste manual no navegador

| # | Passo | Esperado |
|---|---|---|
| 1 | Acessar tela de time-lapse e carregar frames | Registro aparece em `timelapse_cache` |
| 2 | Verificar logs do Tomcat para `[Muralha] Descarte timelapse` | Job executa a cada 60 min |
| 3 | Alterar `TIMELAPSE_RETENCAO_HORAS` para 0 e aguardar | Todos os registros são removidos |
| 4 | Alterar `TIMELAPSE_DESCARTE_HABILITADO` para false | Job não remove nada |
