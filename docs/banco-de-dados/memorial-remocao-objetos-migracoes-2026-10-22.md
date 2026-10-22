# Memorial: remoção dos objetos de banco criados pelas migrações (2026-10-22)

Registro das ações executadas em 2026-10-22 no banco **`GTW_MURALHA_DEV`** (servidor 10.0.0.200, instância `WIN-3ORE9IP90L3`). A operação removeu as tabelas, views, procedures e schemas criados pelos scripts de [`migracoes/`](migracoes/README.md) entre 2026-10-08 e 2026-10-19.

| Item | Valor |
|---|---|
| Data | 2026-10-22 |
| Banco | `GTW_MURALHA_DEV` (DEV, compartilhado) |
| Login usado | `consilux` (credencial do `confGTW.xml` local; a senha não foi gravada em arquivo) |
| Autorização | Pedido explícito do responsável pelo projeto (Gustavo Flandal) na sessão de trabalho, conforme exige o [CLAUDE.md](../../CLAUDE.md) para DDL no DEV |
| Executor | Claude Code (agente), com `sqlcmd -b -I -f 65001` |
| Scripts | [`20261022_remove_tabelas_migracoes.sql`](migracoes/20261022_remove_tabelas_migracoes.sql) e [`20261022_remove_schemas_dash_analise.sql`](migracoes/20261022_remove_schemas_dash_analise.sql) |
| Backup prévio | `D:\GTW-CWB-backup\rollback-migracoes-20261022\` (fora do repositório) |

## 1. Pedido e definição do escopo

O pedido inicial foi voltar o banco ao estado anterior às migrações. Durante o trabalho, o responsável restringiu o escopo em três passos:

1. **Tabelas:** apenas as tabelas criadas pelas migrações registradas na pasta.
2. **Views:** incluir as views criadas nesta fase que estão documentadas.
3. **Schemas:** remover os schemas criados nesta fase e documentados (`dash` e `analise`). Para isso foi preciso remover antes as três procedures que ainda restavam em `analise`.

Ficaram **fora do escopo** e não foram alterados:

- os dados que as migrações inseriram ou atualizaram em tabelas legadas: `dbo.sis_menu`, `dbo.sis_menu_infos`, `dbo.sis_menu_direitos`, `dbo.sis_grupo` (grupos 107–109), `muralha.config_chave_valor`, `muralha.tipo_alerta_ocorrencia`, além dos usuários e grupos de teste;
- as colunas acrescentadas em `dbo.sis_usuario` (ver §6);
- os objetos criados no período que não vêm de nenhum script da pasta (ver §5).

## 2. Análise feita antes da execução (somente leitura)

1. **Scripts:** levantamento dos `CREATE`, `ALTER` e `DROP` e do DML de todos os 60 scripts de `migracoes/`.
2. **Banco:** consulta a `sys.objects` de todos os objetos de usuário com `create_date >= 2026-10-08`. O resultado foi cruzado com os scripts: só entrou na lista de remoção o objeto criado por um script da pasta.
3. **Dependências:**
   - nenhuma tabela fora da lista tinha FK apontando para uma tabela da lista;
   - as FKs das tabelas da lista para tabelas legadas (`sis_usuario`, `muralha.alerta`, `muralha.tipo_alerta_ocorrencia`) foram removidas junto com as próprias tabelas;
   - nenhuma view era `WITH SCHEMABINDING`.
4. **Documentação:** cada view e cada schema foi conferido na pasta `docs/` (catálogo do banco, `docs/analise/`, sessões e entregas).
5. **Tamanho:** o banco tem cerca de 748 GB alocados e as tabelas a remover somavam cerca de 390 MB. Um backup completo não era viável. O último backup diferencial do servidor antes da operação é de 2026-10-22 00:01 (`U:\BACKUP_DEV\GTW_MURALHA_DEV\DIFF\`).

## 3. Backup feito antes da remoção

O backup fica em `D:\GTW-CWB-backup\rollback-migracoes-20261022\`, na máquina do desenvolvedor e fora do repositório, porque contém dados:

| Conteúdo | Formato |
|---|---|
| 65 tabelas | `<schema>.<tabela>.bcp` (bcp nativo, `-n`) + `<schema>.<tabela>.fmt` (arquivo de formato). 138 MB ao todo; nenhuma falha de exportação |
| 17 views | `views\<schema>.<view>.sql`, com a definição vigente em 2026-10-22 (inclui as alterações feitas pelos scripts posteriores) |
| 3 procedures | `procedures\analise.<procedure>.sql`, com a definição vigente em 2026-10-22 |

## 4. O que foi removido

### 4.1 Views (17), pelo script `20261022_remove_tabelas_migracoes.sql`

| View | Script de origem |
|---|---|
| `muralha.vw_passagem_anonimizada` | `20261008_retencao_anonimizacao.sql` |
| `muralha.v_dashboard_alertas_resumo`, `muralha.v_dashboard_passagens_diarias` | `20261010_dashboard_builder.sql` |
| `muralha.v_dados_consolidados` | `20261016_consolidacao_multifonte.sql` |
| `muralha.v_integridade_recepcao` | `20261016_integridade_sha256_recepcao.sql` |
| `analise.v_alertas`, `v_areas_monitoradas_geojson`, `v_assertividade_dia`, `v_disponibilidade_dia`, `v_equipamentos`, `v_preprocessamento`, `v_sla_latencia_dia`, `v_sla_preproc_dia` | `20261017_analise_camada_dados.sql` (algumas alteradas depois por `20261017_analise_correcao_camada.sql` e `20261017_analise_versao_equipamento.sql`) |
| `analise.v_local_vigente`, `v_infracoes_geolocalizado`, `v_passagens_geolocalizado` | `20261017_analise_correcao_camada.sql` |
| `analise.v_local_versao` | `20261017_analise_versao_equipamento.sql` |

### 4.2 Tabelas (65), pelo script `20261022_remove_tabelas_migracoes.sql`

O número entre parênteses é a quantidade de linhas no momento da remoção.

**`dbo` (3):** `sis_log_auditoria` (94), `sis_senha_config` (9), `sis_usuario_complemento` (0).

**`dash` (15):** `catalogo_tabela` (5), `catalogo_coluna` (30), `catalogo_relacao` (4), `dataset` (5), `dataset_campo` (29), `dashboard` (4), `widget` (16), `compartilhamento` (0), `historico_versao` (2), `dataset_filtro_fixo` (0), `dataset_tabela` (9), `consulta_log` (552), `dataset_estatistica` (5), `dataset_relacao` (2), `dataset_modelador` (0).

**`analise` (4):** `carga_log` (58), `mat_passagens_hora` (62.116), `mat_infracoes_dia` (1.020), `mat_areas_monitoradas` (4).

**`muralha` (43):**

| Grupo | Tabelas |
|---|---|
| Planos 01–19 (2026-10-08) | `vtr_complemento` (0), `vtr_imagem_complemento` (351.200), `infracao_analise` (60), `vtr_status_analise` (30), `vtr_imagem_obliterada` (0), `infracao_imagem_obliteracao` (0), `alerta_sla` (3.061), `alerta_sla_preproc` (301), `lote_infracao` (1), `lote_infracao_item` (0), `kpi_config` (12), `expurgo_log` (74), `equipamento_disponibilidade` (0), `incidente_externo` (0) |
| Dashboard Builder inicial (2026-10-10) | `dashboard_dataset` (4), `dashboard_painel` (2), `dashboard_widget` (5) |
| Chamados (2026-10-15) | `chamado` (3), `chamado_anexo` (0), `chamado_categoria` (22), `chamado_historico` (13), `chamado_notificacao` (3), `chamado_prioridade` (4), `chamado_protocolo` (1) |
| Alertas, KPIs, integridade, OCR, logs (2026-10-16) | `tipo_alerta_config` (15), `tipo_alerta_tratamento` (6), `alerta_retencao` (0), `equipamento_integridade_config` (1), `vtr_imagem_hash_origem` (0), `vtr_imagem_integridade` (200.000), `kpi_config_meta` (12), `kpi_historico` (1.233), `relatorio_periodico` (0), `parametro_historico` (17), `vtr_placa_leitura` (847.315), `equipamento_ocr_config` (0), `vtr_ocr_avaliacao` (0), `vtr_revisao_placa` (0), `motivo_correcao_placa` (7), `vtr_correcao_justificativa` (0), `notificacao_retry` (0), `timelapse_cache` (0) |
| Tipos de alerta (2026-10-18) | `alerta_tratamento_execucao` (0) |

### 4.3 Procedures (3) e schemas (2), pelo script `20261022_remove_schemas_dash_analise.sql`

- `analise.spu_carga_passagens_hora`, `analise.spu_carga_infracoes_dia` e `analise.spu_carga_areas_monitoradas`, que vêm dos scripts `20261017_analise_*`.
- Schema `dash`, criado por `20261013_dash_fundacao.sql`. Estava vazio depois do primeiro script.
- Schema `analise`, criado por `20261016_analise_fundacao.sql`.

Antes da remoção foi confirmado que nenhum usuário do banco tinha esses schemas como padrão, que não havia permissões concedidas sobre eles e que não havia tipos definidos pelo usuário dentro deles.

### 4.4 Resultado

| Script | Saída |
|---|---|
| `20261022_remove_tabelas_migracoes.sql` | `Views removidas: 17 \| Tabelas removidas: 65`. Lista "ainda existe" vazia. Código de saída 0 |
| `20261022_remove_schemas_dash_analise.sql` | `Schemas dash e analise removidos.` Lista "schema ainda existe" vazia. Código de saída 0 |

Os dois scripts rodam em transação (`XACT_ABORT ON`), só aceitam executar no `GTW_MURALHA_DEV` e são idempotentes. Antes da execução, a sintaxe foi conferida com `SET PARSEONLY ON`.

## 5. Objetos do período que foram mantidos (não vêm das migrações)

Estes objetos foram criados no mesmo período, mas não há script deles em `migracoes/`. Por isso não foram removidos:

| Objeto | Tipo | Criado em |
|---|---|---|
| `dbo.imagem_pan_selecionada` | tabela | 2026-10-22 14:48 |
| `dbo.log_alerta_arquivo` | tabela (FK para `dbo.log_alerta`) | 2026-10-19 |
| `muralha.bkp_20261020_sis_menu`, `bkp_20261020_sis_menu_direitos`, `bkp_20261020_sis_menu_infos` | tabelas (cópias de segurança de menus) | 2026-10-20 |
| `dbo.spu_atualizar_classe_veiculo` | procedure | 2026-10-15 |
| `muralha.spu_getAproveitamentoRegistros`, `spu_getComportamentoFluxoViario`, `spu_getConformidadeFuncionalidadesIntegradas`, `spu_getDesempenhoOperacionalEquipamentos`, `spu_getDisponibilidadeEquipamentos`, `spu_getDistribuicaoInfracoesPorEnquadramento`, `spu_getEvolucaoTemporalInfracoesAlertas`, `spu_getIndicesClassificacaoVeicular`, `spu_ObterNovosAlertasGTW` | procedures | 2026-10-15 a 2026-10-19 |

Uma delas, **`muralha.spu_getConformidadeFuncionalidadesIntegradas`**, lê tabelas que foram removidas e passa a falhar quando executada.

## 6. Pontos de atenção

1. **A aplicação na branch `main` depende dos objetos removidos.** No DEV, os módulos de Chamados, KPIs, Dashboards e Análises, Auditoria, Integridade, Revisão de Placas, Alertas e Lotes passam a dar erro. O login continua funcionando, porque a leitura do MFA em `login_action.jsp` trata a falta de `sis_usuario_complemento`.
2. **Os menus continuam ativos.** As entradas em `sis_menu`, `sis_menu_infos` e `sis_menu_direitos` criadas pelas migrações continuam visíveis e apontam para telas sem tabela.
3. **Colunas em `dbo.sis_usuario`.** Existem as colunas `senha_hash`, `senha_historico`, `tentativas_invalidas`, `bloqueado_ate`, `dt_ultima_troca_senha`, `fl_troca_obrigatoria`, `totp_secret` e `totp_habilitado`. Elas foram criadas pelo `ALTER TABLE` de `20261008_politica_senhas.sql` e `20261008_mfa_totp.sql` antes de esse trecho ser comentado como obsoleto, ao contrário do que registram o README das migrações e `docs/entregas/pendencias-banco-de-dados.md`. Estão vazias nos 42 usuários e não foram removidas.
4. **Diferença em `dbo.sis_menu`.** A tabela tem 172 linhas, contra 245 na cópia `muralha.bkp_20261020_sis_menu` de 2026-10-20. Essa diferença não foi causada por esta operação, que não alterou `sis_menu`.
5. **O catálogo do banco está desatualizado.** `docs/banco-de-dados/tabelas/`, `objetos-programaveis.md` e `codigo-sql/` ainda descrevem os objetos removidos. Para regenerar o catálogo, usar [`scripts/extrair-catalogo.ps1`](scripts/extrair-catalogo.ps1).

## 7. Como desfazer

1. Reexecutar, na ordem do [README das migrações](migracoes/README.md), os scripts de criação. A recriação dos schemas está em `20261013_dash_fundacao.sql` e `20261016_analise_fundacao.sql`. Não executar os trechos marcados como obsoletos.
2. Recarregar os dados de cada tabela a partir do backup:
   ```powershell
   bcp GTW_MURALHA_DEV.<schema>.<tabela> in "<pasta>\<schema>.<tabela>.bcp" -f "<pasta>\<schema>.<tabela>.fmt" -E -S 10.0.0.200 -U consilux
   ```
   O `-E` preserva as identidades.
3. Recriar as views e as procedures com os arquivos de `views\` e `procedures\` do backup. Eles contêm a definição que estava em vigor no momento da remoção.
4. Se for preciso o estado exato do banco inteiro, pedir ao DBA a restauração do backup diferencial de 2026-10-22 00:01 sobre o último backup completo.
