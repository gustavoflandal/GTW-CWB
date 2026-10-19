# Migrações de banco de dados

Scripts versionados (`AAAAMMDD_descricao.sql`). Regras: idempotentes, schema explícito, compatíveis com SQL Server 2016 (nível de compatibilidade do banco: 110), rollback comentado, sem dados reais, **nunca aplicados por agentes sem autorização humana explícita**. Ver [`docs/requisitos/10-guia-de-implementacao.md`](../../requisitos/10-guia-de-implementacao.md) §6 e [`docs/agentes/AGENTS.md`](../../agentes/AGENTS.md). Catálogo do banco: [`../README.md`](../README.md).

Esta lista corresponde aos 33 scripts existentes na pasta em **2026-10-15**, mais os criados depois (seção "Correção da auditoria"). A coluna "Situação no DEV" foi conferida em `GTW_MURALHA_DEV` com consultas somente leitura (existência de tabela/coluna, contagem de linhas de configuração e menus). "Sem evidência" significa que o script não deixa rastro verificável (UPDATE de dados) ou que o objeto que ele criaria não existe.

> Vários scripts de 2026-10-08 foram marcados como **OBSOLETO** no próprio cabeçalho: usavam `ALTER TABLE` em tabelas legadas (proibido) e foram substituídos por tabelas complementares. **Não execute** os trechos de `ALTER TABLE` desses scripts; veja o detalhe em [`docs/planos-salvador/00b-tabelas-complementares.md`](../../planos-salvador/00b-tabelas-complementares.md).

## Planos Salvador (2026-10-08)

| Script | Plano | Descrição | Situação no DEV |
|---|---|---|---|
| `20261008_log_auditoria.sql` | 01 | `dbo.sis_log_auditoria` (log de auditoria centralizado) | Executado (tabela existe, 71 registros) |
| `20261008_tabelas_complementares.sql` | 02/09 | `dbo.sis_usuario_complemento`, `muralha.vtr_complemento`, `muralha.vtr_imagem_complemento` | **Obsoleto**, mas as três tabelas existem no DEV (executado). Valem `sis_usuario_complemento` e `vtr_imagem_complemento` (usada pelo `JobSha256Imagem`, ≈ 141,6 mil linhas), esta agora com script próprio, `20261016_vtr_imagem_complemento.sql`. Só `vtr_complemento` está de fato sem uso; não descartar sem decisão humana |
| `20261008_politica_senhas.sql` | 03 | `dbo.sis_senha_config` e seus parâmetros | **Obsoleto** (parcial): só a tabela `sis_senha_config` e os `INSERT` valem (existe, 9 chaves); não executar o `ALTER TABLE dbo.sis_usuario` |
| `20261008_mfa_totp.sql` | 04 | Parâmetro `mfa_obrigatorio` em `sis_senha_config` | **Obsoleto** (parcial): só o `INSERT` vale; as colunas TOTP ficam em `sis_usuario_complemento` |
| `20261008_dupla_analise.sql` | 05 | `muralha.infracao_analise` | **Obsoleto** (parcial): o `CREATE TABLE` vale (existe, 60 linhas); não executar o `ALTER TABLE` de `veiculo_tempo_real` |
| `20261008_dupla_analise_fix.sql` | 05 | `ALTER TABLE muralha.veiculo_tempo_real ADD status_analise` | **Obsoleto, NÃO EXECUTAR** (sem evidência de execução: a coluna não existe) |
| `20261008_obliteracao.sql` | 06 | `muralha.infracao_imagem_obliteracao` | **Obsoleto, NÃO EXECUTAR** o `ALTER TABLE` de `veiculo_tempo_real_imagem` (as colunas não existem); a tabela existe e é criada também por `complementar` |
| `20261008_sla_latencia.sql` | 07 | `muralha.alerta_sla` e coluna `latencia_ms` | **Obsoleto, NÃO EXECUTAR** o `ALTER TABLE` (a coluna não existe em `veiculo_tempo_real`); `alerta_sla` existe via `complementar` |
| `20261008_complementar.sql` | 05/06/07 | Substitui os `ALTER TABLE`: `vtr_status_analise`, `vtr_imagem_obliterada`, `infracao_imagem_obliteracao`, `alerta_sla` + chave `sla_latencia_threshold_ms` | Executado (as quatro tabelas existem; `vtr_status_analise` com 30 linhas) |
| `20261008_sla_preproc.sql` | 10 | `muralha.alerta_sla_preproc` | Executado (tabela existe) |
| `20261008_lotes_kpis.sql` | 14/15 | `lote_infracao`, `lote_infracao_item`, `kpi_config` + KPIs iniciais | Executado (tabelas existem; 4 KPIs) |
| `20261008_retencao_anonimizacao.sql` | 12/13 | `muralha.expurgo_log` + chaves `retencao_anos`, `anonimizar_placa_padrao` | Executado (tabela e as 2 chaves existem) |
| `20261008_disponibilidade_incidentes.sql` | 18/19 | `equipamento_disponibilidade`, `incidente_externo` + chaves de configuração | Executado (tabelas e chaves `disponibilidade_*`/`waze_*` existem) |

Os Planos 02 (SHA-256), 11 (exportação) e 16/17 (GIS, time-lapse) não têm script próprio na pasta.

## Menus, dados de exemplo e ajustes (2026-10-09 a 2026-10-12)

| Script | Plano | Descrição | Situação no DEV |
|---|---|---|---|
| `20261009_menu_modulos_desenvolvidos.sql` | 01–19 | Entradas de menu (`sis_menu`, `sis_menu_infos`, `sis_menu_direitos`) dos módulos com tela JSP | Executado (menus presentes) |
| `20261010_dashboard_builder.sql` | 20 | Dashboard Builder inicial: `muralha.dashboard_dataset`, `dashboard_painel`, `dashboard_widget` + menus | Executado (tabelas existem: 4 datasets, 2 painéis) |
| `20261012_dados_assertividade.sql` | 08 | Dados de exemplo (30 infrações analisadas por 3 analistas) para a tela de assertividade | Executado (dados em `infracao_analise`/`vtr_status_analise`) |
| `20261012_dados_log_auditoria.sql` | 01 | Registros de exemplo no log de auditoria | Executado (dados em `sis_log_auditoria`) |
| `20261012_fix_encoding_kpi.sql` | 15 | Corrige o nome do KPI "Pré-aprovadas (7d)" (encoding duplo) | Sem evidência verificável (correção de dados) |
| `20261012_icones_menu_distintos.sql` | — | Ícones distintos em `sis_menu_infos.src` | Sem evidência verificável (`UPDATE` de dados) |
| `20261012_kpi_cor_personalizada.sql` | 15 | Coluna `kpi_config.cor_personalizada` | Executado (coluna existe) |
| `20261012_kpi_tipo_visual.sql` | 15 | Coluna `kpi_config.tipo_visual` | Executado (coluna existe) |

## Construtor de Dashboards — schema `dash` (2026-10-13 e 2026-10-14)

Ordem sugerida (a mesma da criação): fundação → editor de dataset → observabilidade → modelador visual → menus → dados de demonstração.

| Ordem | Script | Sessão / Plano | Descrição | Situação no DEV |
|---|---|---|---|---|
| 1 | `20261013_dash_fundacao.sql` | DASH-1 | Schema `dash`, 9 tabelas, catálogo inicial | Executado |
| 2 | `20261013_dash2_dataset_editor.sql` | DASH-2 | `dataset_filtro_fixo`, `dataset_tabela` | Executado |
| 3 | `20261013_dashboards_observabilidade.sql` | DASH-6 | `consulta_log`, `dataset_estatistica` | Executado |
| 4 | `20261013_dash_modelador_visual.sql` | DASH-2B | `dataset_relacao`, `dataset_modelador` | Executado |
| 5 | `20261013_atualizar_menus_dashboard_builder.sql` | 20/21 | Menus 376/377 (infos 43/44) apontando para as telas novas | Executado (menus 376–383 presentes) |
| 6 | `20261013_menu_catalogo_dados.sql` | 20/21 | Menu 378, info 45 (Catálogo de Dados) | Executado |
| 7 | `20261014_dados_demonstracao_dashboards.sql` | 21 | Dados de demonstração: 3 datasets, 2 painéis, 16 widgets (idempotente; rodar com `sqlcmd -f 65001`) | Executado (2026-10-14; 15 tabelas `dash`, 4 dashboards, 16 widgets) |

## Sistema de Chamados — Plano 22 (2026-10-15)

**Ordem de execução obrigatória** (cada script depende do anterior; todos idempotentes):

1. `20261015_sistema_chamados.sql` — as 7 tabelas `muralha.chamado*` (sem alterar tabelas existentes).
2. `20261015_sistema_chamados_seed.sql` — categorias, prioridades/SLA, grupos 107–109 (Solicitante, Gestor do contrato, Equipe técnica), menus 379–382 (infos 46–48) e chaves `chamado_*` em `config_chave_valor`.
3. `20261015_sistema_chamados_admin.sql` — categoria de menu e card de administração/configuração (menu 383, info 49), direitos e chave `chamado_grupo_admin` (grupo 11).
4. `20261015_sistema_chamados_smtp.sql` — chaves `chamado_smtp_*` (inseridas vazias; o administrador preenche pela tela de configuração; a senha é gravada cifrada, com a chave na variável de ambiente `GTW_CHAMADO_CHAVE_SMTP`, ver [`docs/requisitos/07-configuracao-e-segredos.md`](../../requisitos/07-configuracao-e-segredos.md)).

| Script | Descrição | Situação no DEV |
|---|---|---|
| `20261015_sistema_chamados.sql` | Tabelas `chamado`, `chamado_anexo`, `chamado_categoria`, `chamado_historico`, `chamado_notificacao`, `chamado_prioridade`, `chamado_protocolo` | Executado (7 tabelas) |
| `20261015_sistema_chamados_seed.sql` | Seed de categorias (22), prioridades (4), grupos, menus e configuração | Executado |
| `20261015_sistema_chamados_admin.sql` | Menu/card de administração e grupo administrador | Executado (chave `chamado_grupo_admin` presente) |
| `20261015_sistema_chamados_smtp.sql` | Configuração de SMTP dos chamados | Executado (6 chaves `chamado_smtp_*`) |
| `20261015_usuarios_teste_chamados.sql` | Usuários de teste do módulo (perfis administrador e solicitante) | **Exclusivo do DEV**: o script recusa rodar em qualquer banco diferente de `GTW_MURALHA_DEV`; **nunca** executar em homologação ou produção. Sem evidência de execução em 2026-10-15 (nenhum usuário `TESTE.CHAMADO.*` encontrado) |

## Correção da auditoria (2026-10-16)

| Script | Plano | Descrição | Situação no DEV |
|---|---|---|---|
| `20261016_vtr_imagem_complemento.sql` | 24 (Plano 02) | DDL idempotente de `muralha.vtr_imagem_complemento`, antes só no script obsoleto | Executado (2026-10-16): "Ja existe", nada alterado |
| `20261016_config_mapa_api_trecho.sql` | 24 | Chave `mapa_api_monitoramento_trecho` (endereço da API de trechos, antes fixo em `maps-config.js`) | Executado (2026-10-16): chave inserida |
| `20261016_logs_tecnicos_parametro_historico.sql` | 26 | `muralha.parametro_historico`, chaves `LOG_TECNICO_*` e `PARAMETRO_HIST_IGNORAR_CONFIGEQUIP`, menus da tela Logs Técnicos. Rodar com `sqlcmd -b -f 65001` | Executado (2026-10-16): tabela, 6 chaves e menus 387-388 |
| `20261016_desativa_menu_201.sql` | 24 (decisão de 2026-10-16) | Desativa o menu 201 "Relatório Alt. ConfigEquip", sem grupos com direito e substituído pelo histórico de parâmetros do Plano 26 | Executado (2026-10-16): menu 201 com `Ativo = 0` |
| `20261016_integridade_sha256_recepcao.sql` | 25 | `equipamento_integridade_config`, `vtr_imagem_hash_origem`, `vtr_imagem_integridade`, view `v_integridade_recepcao`, chaves `INTEGRIDADE_*`, tipo de alerta `INTEGRIDADE`, legado `'OK'` → `SEM_CONFERENCIA`, menus. Rodar com `sqlcmd -b -f 65001` | Executado (2026-10-16), na segunda tentativa: a primeira parou no tipo de alerta porque o `sqlcmd` usa `QUOTED_IDENTIFIER OFF`; o script ganhou `SET QUOTED_IDENTIFIER ON` e foi reexecutado. 200 000 imagens `OK` → `SEM_CONFERENCIA`; menus 384-386 |
| `20261016_ocr_score_revisao_placas.sql` | 27 | `vtr_placa_leitura`, `equipamento_ocr_config`, `vtr_ocr_avaliacao`, `vtr_revisao_placa`, `motivo_correcao_placa` (7 motivos), `vtr_correcao_justificativa`, chaves `OCR_*`, cópia de 30 dias de placas (~850 mil linhas no DEV), menus. Rodar com `sqlcmd -b -f 65001` | **Não executado** — necessário para a fila e a justificativa em tabela. Sintaxe conferida com `SET PARSEONLY ON` |
| `20261017_corrige_rotulos_demonstracao_dashboards.sql` | 20/21 (auditoria de 2026-10-17) | Demonstração do Dashboard Builder (painéis 101-102): período único de 12 meses em todos os widgets, títulos que não batiam com a métrica, rótulos acentuados dos datasets 101-103. Só altera linhas da demonstração em `dash.*`. Rodar com `sqlcmd -b -f 65001` | **Não executado** |

## Superset, menus e demonstração (2026-10-17)

| Script | Plano | Descrição | Situação no DEV |
|---|---|---|---|
| `20261017_fix_icone_superset.sql` | 23 | Ícone do card Análises (id_menu 395), direitos dos grupos 41 e 42, chave `SUPERSET_BASE_URL` | Executado (2026-10-17) |
| `20261017_icones_integridade_logs_revisao.sql` | 25, 26 e 27 | Ícones próprios dos cards Integridade, Logs Técnicos e Revisão de Placas (só `sis_menu_infos.src`, pelo `href`) | Executado (2026-10-17) |
| `20261017_analise_mapas_geo.sql` | 23 (SUP-8) | **Obsoleta — não executar**: colunas e tabela inexistentes; substituída pela correção abaixo | Nunca executada |
| `20261017_analise_correcao_camada.sql` | 23 (SUP-2/SUP-8) | Correção da camada `analise` (só objetos desse schema): `v_local_vigente`; cargas `spu_carga_*` com janela parametrizável e coordenadas da versão vigente; `v_equipamentos`, `v_disponibilidade_dia` (horas com captura), `v_preprocessamento` (fluxo de `dbo.infracao`); `mat_areas_monitoradas` + `spu_carga_areas_monitoradas` (polígono); views geolocalizadas; carga inicial. Rodar com `sqlcmd -b -I -f 65001`. Ver [gap](../../entregas/gap-camada-analise-superset.md) | Executado (2026-10-17, 23 s): 62.116 linhas de passagens (4,05 mi de passagens desde 2025-06), 1.020 de infrações, 4 áreas; sem falhas |
| `20261017_analise_versao_equipamento.sql` | 23 (SUP-2) | `v_local_versao` (vigência de cada versão do equipamento); cargas, `v_disponibilidade_dia` e `v_preprocessamento` passam a usar a versão vigente na data do registro (o `id_local` é reaproveitado entre locais); recarga completa. Rodar com `sqlcmd -b -I -f 65001` | Executado (2026-10-17, 35 s); `v_disponibilidade_dia` responde em ~3 s |
| `20261018_tipo_alerta_config_definitivo.sql` | 28 | Configuração definitiva dos tipos de alerta: cores e ícones em `tipo_alerta_config` (com `parametro_historico`), tratamentos manuais desmarcados como automáticos, tabela `muralha.alerta_tratamento_execucao`, parâmetro `TRATAMENTO_AUTO_JANELA_MIN`, remoção de `tipo_alerta_ajuste_demo`. Rodar com `sqlcmd -b -I -f 65001` | Executado (2026-10-18) |
| `20261018_remove_cards_duplicados.sql` | 15, 20, 22 | Remove os cards de `sis_menu_infos` que duplicavam um botão de outra tela: Configuração de KPIs (KPIs → Configurar), Catálogo de Dados (Catálogo de Datasets), Guarnição (Central de Atendimento → Gerenciar Guarnições), Relatórios e Configuração de Chamados (botões da tela Chamados). Menus e permissões continuam ativos; rollback com os `INSERT` no cabeçalho. Rodar com `sqlcmd -b -I -f 65001` | Executado (2026-10-18): 5 cards removidos |
| `20261018_renomeia_card_configuracao_monitoramento.sql` | 20 | Completa o rótulo do card "Configuração" (Cadastro e Configuração, `id_infos` 9) para "Configuração de Monitoramento". Só `sis_menu_infos`; rollback no cabeçalho. Rodar com `sqlcmd -b -I -f 65001` | Executado (2026-10-18): 1 card renomeado |
| `20261019_analise_permissao_abrir_superset.sql` | 23 (SUP-9) | Funcionalidade 396 "Analises - Abrir no Superset" (sem card) com direito do grupo 11, e direito do grupo 11 à tela de Análises (395). O `SsoAnaliseServlet` só exibe o botão com esse direito e **nega** se a funcionalidade não estiver cadastrada. Rodar com `sqlcmd -b -f 65001` | Executado (2026-10-19) |
| `20261019_registro_modulos_novos.sql` | [Guia de correções](../../referencia/guia-correcoes-analise-kpis-lotes.md) §7 (SEG-02, SEG-07, SEG-11) | Registra sem card (ids 397–438) as telas e os servlets novos que estavam abertos a qualquer usuário logado (*fail-open*), com os grupos decididos em 2026-10-19; dá ao grupo 11 as telas 366–375 e 384–395; retira o grupo 41 da Configuração de KPIs (372); corrige a `acao` do 395 para `/MuralhaDigital/SsoAnalise`. Rodar **depois** do script acima, com `sqlcmd -b -I -f 65001`; rollback no cabeçalho | Executado (2026-10-19): 42 funcionalidades, 124 direitos, 1 direito retirado, 1 `acao` corrigida |
| `20261019_kpis_corrige_consultas.sql` | [Plano 35](../../planos-salvador/35-correcoes-seguranca-funcionais.md) Task 4 (FUN-04) | Corrige o `query_sql` dos KPIs 5, 7, 8, 9 e 11, que usavam colunas inexistentes (`veiculo_tempo_real.resultado_ia`, `local_vigente.ativo`, `status_alerta.status`) e por isso eram pulados pelo `KpiSnapshotJob`. Só altera o KPI cujo SQL ainda tem a coluna inexistente; o valor anterior fica em `muralha.parametro_historico` (rollback no cabeçalho). Rodar com `sqlcmd -b -I -f 65001` | Executado (2026-10-19): 5 KPIs corrigidos |
| `20261017_tipo_alerta_ajuste_demo.sql` | 28 | **Substituído em 2026-10-18** pelo script acima (a tabela foi removida). Era um **contorno temporário:** tabela nova `muralha.tipo_alerta_ajuste_demo` com 5 ajustes de cor, ícone, prioridade e descrição dos tipos de alerta; nenhuma tabela existente é alterada. Ver [gap](../../entregas/gap-tipos-alerta-ajuste-demo.md). Rodar com `sqlcmd -b -f 65001` | Executado (2026-10-17): tabela criada, 5 ajustes ativos |

## Como acrescentar uma migração

1. Nomear `AAAAMMDD_descricao.sql`, com cabeçalho (plano, data, ordem de execução e rollback comentado).
2. Preferir tabelas complementares a `ALTER TABLE` em tabelas legadas.
3. Executar somente com autorização humana e, depois, regenerar o catálogo (`../scripts/extrair-catalogo.ps1`) e atualizar esta lista.
