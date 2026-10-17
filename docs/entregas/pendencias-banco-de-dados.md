# Pendências de Banco de Dados — PoC TRANSALVADOR

Relação completa de todas as ações manuais necessárias no banco `GTW_MURALHA_DEV` (10.0.0.200) para ativar as funcionalidades implementadas nos Planos 01–34. Todas as migrações foram executadas no DEV em 2026-10-16, com autorização.

> **Lista oficial e atualizada dos scripts:** [`docs/banco-de-dados/migracoes/README.md`](../banco-de-dados/migracoes/README.md), que registra a situação de cada script no DEV conferida por consultas somente leitura. Este documento resume as ações por entrega; em caso de divergência, vale o README das migrações.

> ✅ **Migrações dos Planos 01–22 executadas no `GTW_MURALHA_DEV`** via `sqlcmd` (10 em 2026-10-09, ajustes de 2026-10-09 a 2026-10-12, 6 em 2026-10-13, 1 em 2026-10-14 e 4 do Sistema de Chamados em 2026-10-15). Os ajustes de dados `20261012_fix_encoding_kpi.sql` e `20261012_icones_menu_distintos.sql` não deixam rastro verificável.  
> **Atenção:** todas as migrações usam `IF NOT EXISTS` / `IF COL_LENGTH(...) IS NULL` — podem ser reexecutadas com segurança (idempotentes).  
> **Nenhuma migração faz ALTER TABLE** em tabelas existentes do sistema legado.

---

## 1. Migrações SQL — Ordem de Execução

Executar na ordem abaixo. Cada script é independente, mas a ordem respeita dependências lógicas.

| # | Script | Planos | Objetos criados | Status |
|---|---|---|---|---|
| 1 | [`20261008_log_auditoria.sql`](../banco-de-dados/migracoes/20261008_log_auditoria.sql) | 01 | Tabela `dbo.sis_log_auditoria` + índices | ✅ Executado |
| 2 | [`20261008_politica_senhas.sql`](../banco-de-dados/migracoes/20261008_politica_senhas.sql) | 03 | Tabela `dbo.sis_senha_config` (9 parâmetros). As colunas de senha ficam em `dbo.sis_usuario_complemento` (script 5); o `ALTER TABLE dbo.sis_usuario` do script é obsoleto e **não** deve ser executado | ✅ Executado (só a parte válida) |
| 3 | [`20261008_mfa_totp.sql`](../banco-de-dados/migracoes/20261008_mfa_totp.sql) | 04 | Parâmetro `mfa_obrigatorio`. As colunas `totp_secret` e `totp_habilitado` ficam em `dbo.sis_usuario_complemento` (script 5); o `ALTER TABLE dbo.sis_usuario` do script é obsoleto | ✅ Executado (só a parte válida) |
| 4 | [`20261008_dupla_analise.sql`](../banco-de-dados/migracoes/20261008_dupla_analise.sql) | 05 | Tabela `muralha.infracao_analise` + constraint UNIQUE | ✅ Executado |
| 5 | [`20261008_tabelas_complementares.sql`](../banco-de-dados/migracoes/20261008_tabelas_complementares.sql) | 02, 09, 11 | `muralha.vtr_imagem_complemento`, `dbo.sis_usuario_complemento` | ✅ Executado |
| 6 | [`20261008_complementar.sql`](../banco-de-dados/migracoes/20261008_complementar.sql) | 05, 06, 07 | `muralha.vtr_status_analise`, `muralha.vtr_imagem_obliterada`, `muralha.infracao_imagem_obliteracao`, `muralha.alerta_sla`, config `sla_latencia_threshold_ms` | ✅ Executado |
| 7 | [`20261008_sla_preproc.sql`](../banco-de-dados/migracoes/20261008_sla_preproc.sql) | 10 | Tabela `muralha.alerta_sla_preproc` | ✅ Executado |
| 8 | [`20261008_retencao_anonimizacao.sql`](../banco-de-dados/migracoes/20261008_retencao_anonimizacao.sql) | 12, 13 | `muralha.expurgo_log`, VIEW `muralha.vw_passagem_anonimizada`, config `retencao_anos`, `anonimizar_placa_padrao` | ✅ Executado |
| 9 | [`20261008_lotes_kpis.sql`](../banco-de-dados/migracoes/20261008_lotes_kpis.sql) | 14, 15 | `muralha.lote_infracao`, `muralha.lote_infracao_item`, `muralha.kpi_config` (4 KPIs padrão) | ✅ Executado |
| 10 | [`20261008_disponibilidade_incidentes.sql`](../banco-de-dados/migracoes/20261008_disponibilidade_incidentes.sql) | 18, 19 | `muralha.equipamento_disponibilidade`, `muralha.incidente_externo` + constraint UNIQUE, configs | ✅ Executado |
| 11 | [`20261013_dash_fundacao.sql`](../banco-de-dados/migracoes/20261013_dash_fundacao.sql) | 20 | Schema `dash`, tabelas de catálogo, dataset, dashboard, widget, compartilhamento, histórico | ✅ Executado |
| 12 | [`20261013_dash2_dataset_editor.sql`](../banco-de-dados/migracoes/20261013_dash2_dataset_editor.sql) | 20 | Campos adicionais de dataset, estatísticas, campos calculados | ✅ Executado |
| 13 | [`20261013_dashboards_observabilidade.sql`](../banco-de-dados/migracoes/20261013_dashboards_observabilidade.sql) | 20 | Tabela `dash.consulta_log`, `dash.dataset_estatistica` | ✅ Executado |
| 14 | [`20261013_atualizar_menus_dashboard_builder.sql`](../banco-de-dados/migracoes/20261013_atualizar_menus_dashboard_builder.sql) | 20 | Menus e permissões do Dashboard Builder | ✅ Executado |
| 15 | [`20261013_menu_catalogo_dados.sql`](../banco-de-dados/migracoes/20261013_menu_catalogo_dados.sql) | 20 | Menu 378, info 45 (Catálogo de Dados) | ✅ Executado |
| 16 | [`20261013_dash_modelador_visual.sql`](../banco-de-dados/migracoes/20261013_dash_modelador_visual.sql) | 20 | Tabelas `dash.dataset_relacao`, `dash.dataset_modelador` (modelador visual) | ✅ Executado |
| 17 | [`20261014_dados_demonstracao_dashboards.sql`](../banco-de-dados/migracoes/20261014_dados_demonstracao_dashboards.sql) | 21 | Dados de demonstração: 3 datasets, 2 painéis, 16 widgets | ✅ Executado (2026-10-14) |
| 18 | [`20261015_sistema_chamados.sql`](../banco-de-dados/migracoes/20261015_sistema_chamados.sql) | 22 | 7 tabelas `muralha.chamado*` | ✅ Executado (2026-10-15) |
| 19 | [`20261015_sistema_chamados_seed.sql`](../banco-de-dados/migracoes/20261015_sistema_chamados_seed.sql) | 22 | Prioridades, categorias, grupos 107–109, menus 379–382 e chaves `chamado_*` | ✅ Executado (2026-10-15) |
| 20 | [`20261015_sistema_chamados_admin.sql`](../banco-de-dados/migracoes/20261015_sistema_chamados_admin.sql) | 22 | Menu/card de administração (383/49) e chave `chamado_grupo_admin` | ✅ Executado (2026-10-15) |
| 21 | [`20261015_sistema_chamados_smtp.sql`](../banco-de-dados/migracoes/20261015_sistema_chamados_smtp.sql) | 22 | Chaves `chamado_smtp_*` (vazias) | ✅ Executado (2026-10-15) |

Os scripts 18–21 do Sistema de Chamados devem rodar nessa ordem, com `sqlcmd -b -f 65001` (ver [`22-sistema-chamados.md`](22-sistema-chamados.md)). `20261015_usuarios_teste_chamados.sql` é exclusivo do DEV e **nunca** deve ser executado em homologação ou produção.

### Menus, dados de exemplo e ajustes (2026-10-09 a 2026-10-12)

| Script | Planos | Descrição | Status |
|---|---|---|---|
| [`20261009_menu_modulos_desenvolvidos.sql`](../banco-de-dados/migracoes/20261009_menu_modulos_desenvolvidos.sql) | 01–19 | Entradas de menu dos módulos com tela JSP (ver [`20-menu-modulos.md`](20-menu-modulos.md)) | ✅ Executado |
| [`20261010_dashboard_builder.sql`](../banco-de-dados/migracoes/20261010_dashboard_builder.sql) | 20 | Dashboard Builder inicial (`muralha.dashboard_*`) e menus 376/377 | ✅ Executado |
| [`20261012_kpi_cor_personalizada.sql`](../banco-de-dados/migracoes/20261012_kpi_cor_personalizada.sql) | 15 | Coluna `cor_personalizada` em `muralha.kpi_config` | ✅ Executado |
| [`20261012_kpi_tipo_visual.sql`](../banco-de-dados/migracoes/20261012_kpi_tipo_visual.sql) | 15 | Coluna `tipo_visual` em `muralha.kpi_config` | ✅ Executado |
| [`20261012_fix_encoding_kpi.sql`](../banco-de-dados/migracoes/20261012_fix_encoding_kpi.sql) | 15 | Corrige o nome do KPI "Pré-aprovadas (7d)" | Sem evidência verificável (correção de dados) |
| [`20261012_icones_menu_distintos.sql`](../banco-de-dados/migracoes/20261012_icones_menu_distintos.sql) | — | Ícones distintos em `sis_menu_infos.src` | Sem evidência verificável (`UPDATE` de dados) |
| [`20261012_dados_assertividade.sql`](../banco-de-dados/migracoes/20261012_dados_assertividade.sql) | 08 | Dados de exemplo da tela de assertividade | ✅ Executado — **só DEV** |
| [`20261012_dados_log_auditoria.sql`](../banco-de-dados/migracoes/20261012_dados_log_auditoria.sql) | 01 | Registros de exemplo no log de auditoria | ✅ Executado — **só DEV** |

### Correção da auditoria (2026-10-16)

| Script | Plano | Descrição | Status |
|---|---|---|---|
| [`20261016_vtr_imagem_complemento.sql`](../banco-de-dados/migracoes/20261016_vtr_imagem_complemento.sql) | 24 | DDL idempotente de `muralha.vtr_imagem_complemento` (no DEV a tabela já existe; não faz nada) | ✅ Executado (2026-10-16): "Ja existe", nada alterado |
| [`20261016_config_mapa_api_trecho.sql`](../banco-de-dados/migracoes/20261016_config_mapa_api_trecho.sql) | 24 | Chave `mapa_api_monitoramento_trecho` | ✅ Executado (2026-10-16): chave inserida |
| [`20261016_logs_tecnicos_parametro_historico.sql`](../banco-de-dados/migracoes/20261016_logs_tecnicos_parametro_historico.sql) | 26 | Tabela `muralha.parametro_historico`, chaves `LOG_TECNICO_*`, menus da tela Logs Técnicos. Rodar com `sqlcmd -b -f 65001` | ✅ Executado (2026-10-16): tabela, 6 chaves e menus 387-388 |
| [`20261016_desativa_menu_201.sql`](../banco-de-dados/migracoes/20261016_desativa_menu_201.sql) | 24 | Desativa o menu 201 "Relatório Alt. ConfigEquip" (`Ativo = 0`) | ✅ Executado (2026-10-16): menu 201 com `Ativo = 0` |
| [`20261016_integridade_sha256_recepcao.sql`](../banco-de-dados/migracoes/20261016_integridade_sha256_recepcao.sql) | 25 | Tabelas `equipamento_integridade_config`, `vtr_imagem_hash_origem`, `vtr_imagem_integridade`; view `v_integridade_recepcao`; chaves `INTEGRIDADE_*`; tipo de alerta `INTEGRIDADE`; legado `'OK'` → `SEM_CONFERENCIA`; menus. Rodar com `sqlcmd -b -f 65001` | ✅ Executado (2026-10-16), na segunda tentativa: a primeira parou no tipo de alerta porque o `sqlcmd` usa `QUOTED_IDENTIFIER OFF`; o script ganhou `SET QUOTED_IDENTIFIER ON` e foi reexecutado. 200 000 imagens `OK` → `SEM_CONFERENCIA`; menus 384-386 |
| [`20261016_ocr_score_revisao_placas.sql`](../banco-de-dados/migracoes/20261016_ocr_score_revisao_placas.sql) | 27 | Leituras de placa com score, fila de revisão, motivos e justificativas de correção, chaves `OCR_*`, menus. Rodar com `sqlcmd -b -f 65001` | ✅ Executado (2026-10-16): tabelas já existiam (idempotente), parâmetros e menus conferidos |

### Gaps finais — Planos 32–34 (2026-10-16)

| Script | Plano | Descrição | Status |
|---|---|---|---|
| [`20261016_retry_notificacao.sql`](../banco-de-dados/migracoes/20261016_retry_notificacao.sql) | 32 | Retry de notificações em falha de comunicação: tabela de controle de tentativas, chaves `RETRY_NOTIFICACAO_*` | ✅ Executado (2026-10-16): tabela `notificacao_retry`, 3 chaves |
| [`20261016_timelapse_descarte.sql`](../banco-de-dados/migracoes/20261016_timelapse_descarte.sql) | 33 | Descarte automático de time-lapse: controle de retenção, chaves `TIMELAPSE_*` | ✅ Executado (2026-10-16): tabela `timelapse_cache`, 2 chaves |
| [`20261016_consolidacao_multifonte.sql`](../banco-de-dados/migracoes/20261016_consolidacao_multifonte.sql) | 34 | Consolidação de dados multifonte: view consolidada 4 fontes, chave `CONSOLIDACAO_FONTES_HABILITADO` | ✅ Executado (2026-10-16): view `v_dados_consolidados`, 1 chave |

### Comando de execução

```powershell
# Substituir <SENHA> pela senha do usuário consilux
$server = "10.0.0.200"
$db = "GTW_MURALHA_DEV"
$user = "consilux"
$migracoesDir = "docs\banco-de-dados\migracoes"

$scripts = @(
    "20261008_log_auditoria.sql",
    "20261008_politica_senhas.sql",
    "20261008_mfa_totp.sql",
    "20261008_dupla_analise.sql",
    "20261008_tabelas_complementares.sql",
    "20261008_complementar.sql",
    "20261008_sla_preproc.sql",
    "20261008_retencao_anonimizacao.sql",
    "20261008_lotes_kpis.sql",
    "20261008_disponibilidade_incidentes.sql"
)
# Para os Planos 20–22, seguir a ordem do README das migrações
# (docs/banco-de-dados/migracoes/README.md); os scripts do Sistema de Chamados
# exigem -b -f 65001.

foreach ($s in $scripts) {
    Write-Host "Executando $s ..."
    sqlcmd -S $server -d $db -U $user -P "<SENHA>" -i "$migracoesDir\$s"
    if ($LASTEXITCODE -ne 0) { Write-Error "FALHA em $s"; break }
    Write-Host "OK: $s"
}
```

---

## 2. Scripts Obsoletos (NÃO executar)

| Script | Motivo |
|---|---|
| `20261008_obliteracao.sql` | Usava ALTER TABLE proibido; substituído por `20261008_complementar.sql` |
| `20261008_sla_latencia.sql` | Usava ALTER TABLE proibido; substituído por `20261008_complementar.sql` |
| `20261008_dupla_analise_fix.sql` | Correção pontual já incorporada em `20261008_complementar.sql` |

---

## 3. Registro de Menus (`dbo.sis_menu_infos`)

> ✅ Os menus abaixo já são criados por [`20261009_menu_modulos_desenvolvidos.sql`](../banco-de-dados/migracoes/20261009_menu_modulos_desenvolvidos.sql) (Planos 01–19); os do Dashboard Builder, por `20261010_dashboard_builder.sql`, `20261013_atualizar_menus_dashboard_builder.sql` e `20261013_menu_catalogo_dados.sql` (menus 376–378); os do Sistema de Chamados, pelos scripts `20261015_sistema_chamados_seed.sql` e `_admin.sql` (menus 379–383). Não há INSERT manual pendente.

Telas dos Planos 01–19 com entrada de menu:

| Plano | Tela | URL sugerida | Grupo de menu |
|---|---|---|---|
| 01 | Consulta de Auditoria | `/muralha-digital/pages/auditoria/consulta-log.jsp` | Administração |
| 05 | Fila de Análise | `/muralha-digital/pages/processamento/dupla-analise/fila.jsp` | Processamento |
| 08 | Painel de Assertividade | `/muralha-digital/pages/monitoramento/assertividade/index.jsp` | Monitoramento |
| 09 | CPF/Matrícula | `/muralha-digital/pages/admin/cpf-matricula/index.jsp` | Administração |
| 10 | SLA Pré-processamento | `/muralha-digital/pages/monitoramento/sla-preproc/index.jsp` | Monitoramento |
| 11 | Exportação de Passagens | `/muralha-digital/pages/relatorios/exportacao-passagens/index.jsp` | Relatórios |
| 12 | Retenção de Dados | `/muralha-digital/pages/admin/retencao/index.jsp` | Administração |
| 13 | Passagens Anonimizadas | `/muralha-digital/pages/consulta/passagens-anonimizadas/index.jsp` | Consulta |
| 14 | Gestão de Lotes | `/muralha-digital/pages/processamento/lotes/index.jsp` | Processamento |
| 15 | Dashboard KPIs | `/muralha-digital/pages/dashboard/kpis/index.jsp` | Dashboard |
| 15 | Config KPIs | `/muralha-digital/pages/admin/kpis/index.jsp` | Administração |
| 16 | Mapa GIS | `/muralha-digital/pages/monitoramento/mapa/index.jsp` | Monitoramento |
| 17 | Time-lapse | `/muralha-digital/pages/monitoramento/timelapse/index.jsp` | Monitoramento |
| 18 | Disponibilidade | `/muralha-digital/pages/monitoramento/disponibilidade/index.jsp` | Monitoramento |

> **Nota:** as telas dos Planos 02, 03, 04 e 06 não requerem menu próprio — são funcionalidades integradas a fluxos existentes (login, análise de imagens).

---

## 4. Configurações em `muralha.config_chave_valor`

Estas configurações são inseridas automaticamente pelas migrações com valores padrão. Ajustar conforme necessidade:

| Chave | Valor padrão | Plano | Descrição |
|---|---|---|---|
| `sla_latencia_threshold_ms` | `4000` | 07 | Limiar de latência em milissegundos (P95) |
| `retencao_anos` | `5` | 12 | Período de retenção de dados (anos) |
| `anonimizar_placa_padrao` | `1` | 13 | 1=anonimizar por padrão nas consultas |
| `disponibilidade_threshold_min` | `30` | 18 | Minutos sem passagem para considerar offline |
| `waze_api_url` | _(vazio)_ | 19 | URL da API Waze for Cities |
| `waze_area_bbox` | `-13.05,-38.58,-12.85,-38.35` | 19 | Bounding box de Salvador para filtro |
| `chamado_sla_risco_percentual` | `80` | 22 | Percentual do prazo a partir do qual o chamado entra em risco de SLA |
| `chamado_fechamento_auto_dias` | `5` | 22 | Dias para fechamento automático após a solução |
| `chamado_diretorio_anexos` | `C:\Consilux\MuralhaDigital\chamados` | 22 | Diretório dos anexos dos chamados |
| `chamado_anexo_max_mb` | `10` | 22 | Tamanho máximo de anexo (MB) |
| `chamado_email_ativo` | `1` | 22 | 1 = envia e-mails de notificação dos chamados |
| `chamado_grupo_*` | ids dos grupos | 22 | Grupos Solicitante, Gestor, Equipe e Administrador |
| `chamado_smtp_*` | _(vazio)_ | 22 | SMTP próprio dos chamados; vazio usa o SMTP do `confGTW.xml` |
| `mapa_api_monitoramento_trecho` | `189.112.204.89:8003` | 24 | Endereço `host[:porta]` da API de situação do trânsito por trecho |
| `INTEGRIDADE_ORIGEM_HASH` | `AUSENTE` | 25 | Origem do hash de integridade: `PAYLOAD_SOAP`, `DADO_BRUTO:<campo>` ou `AUSENTE` (padrão; exceções por equipamento em `muralha.equipamento_integridade_config`) |
| `INTEGRIDADE_LOTE` | `200` | 25 | Imagens conferidas por execução do `JobSha256Imagem` |
| `INTEGRIDADE_JANELA_HORAS` | `24` | 25 | Só confere passagens capturadas nas últimas N horas |
| `INTEGRIDADE_ESPERA_HASH_MIN` | `10` | 25 | Minutos esperando o hash de origem antes de concluir "não recebido" |
| `INTEGRIDADE_ALERTA_HABILITADO` | `1` | 25 | 1 = divergência gera alerta em `muralha.alerta` |
| `LOG_TECNICO_PERIODO_MAX_DIAS` | `31` | 26 | Período máximo das consultas de logs técnicos |
| `LOG_TECNICO_PROCESSOS` | `NAO_FINALIZADOS` | 26 | `dbo.log_processos` na lista: `NENHUM`, `NAO_FINALIZADOS` ou `TODOS` |
| `LOG_TECNICO_PROCESSO_ATRASO_MIN` | `30` | 26 | Minutos sem atualização para um processo contar como não finalizado |
| `LOG_TECNICO_EXPORTACAO_MAX` | `50000` | 26 | Linhas máximas do CSV de logs técnicos |
| `LOG_TECNICO_JANELA_CORRELACAO_MIN` | `10` | 26 | Janela (± minutos) da correlação temporal |
| `PARAMETRO_HIST_IGNORAR_CONFIGEQUIP` | `dataHoraConfiguracao` | 26 | Campos do ConfigEquip fora do histórico de parâmetros |
| `OCR_SCORE_ORIGEM` / `OCR_SCORE_LIMIAR` | `AUSENTE` / `85` | 27 | Origem do score de OCR e limiar global (exceções por equipamento em `equipamento_ocr_config`) |
| `OCR_SCORE_ESCALA` | `100` | 27 | Escala do score: `100` (0-100) ou `1` (0-1) |
| `OCR_REVISAR_SEM_SCORE` / `OCR_REVISAR_SEM_LEITURA` | `0` / `0` | 27 | Se passagens sem score / sem placa lida vão para a fila de revisão |
| `OCR_JANELA_HORAS` / `OCR_LOTE` / `OCR_ESPERA_SCORE_MIN` | `24` / `500` / `10` | 27 | Janela, lote e espera do `JobOcrRevisao` |
| `RETRY_NOTIFICACAO_MAX` | `3` | 32 | Número máximo de tentativas de reenvio |
| `RETRY_NOTIFICACAO_INTERVALO_MIN` | `5` | 32 | Intervalo mínimo entre tentativas (minutos) |
| `RETRY_NOTIFICACAO_HABILITADO` | `1` | 32 | 1 = retry automático habilitado |
| `TIMELAPSE_RETENCAO_HORAS` | `72` | 33 | Horas de retenção antes do descarte automático |
| `TIMELAPSE_DESCARTE_HABILITADO` | `1` | 33 | 1 = descarte automático habilitado |
| `CONSOLIDACAO_FONTES_HABILITADO` | `1` | 34 | 1 = consolidação multifonte habilitada |

---

## 5. Variáveis de Ambiente

| Variável | Plano | Obrigatória | Descrição |
|---|---|---|---|
| `WAZE_API_KEY` | 19 | Não | Chave da API Waze for Cities. Sem ela, o job ignora silenciosamente |
| `GTW_CHAMADO_CHAVE_SMTP` | 22 | Só com SMTP próprio dos chamados | Chave que cifra a senha do SMTP dos chamados (ver [`07-configuracao-e-segredos.md`](../requisitos/07-configuracao-e-segredos.md) §3.1) |

---

## 6. Verificação Pós-Migração

> ✅ Verificação realizada em 2026-10-09. Todos os objetos confirmados: 16 tabelas, 1 view, 6 configs, 4 KPIs.

Validar com:

```sql
-- Tabelas criadas (esperado: 14 resultados)
SELECT TABLE_SCHEMA + '.' + TABLE_NAME AS tabela
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_NAME IN (
    'sis_log_auditoria', 'sis_senha_config',
    'infracao_analise', 'vtr_imagem_complemento', 'sis_usuario_complemento',
    'vtr_status_analise', 'vtr_imagem_obliterada', 'infracao_imagem_obliteracao',
    'alerta_sla', 'alerta_sla_preproc',
    'expurgo_log', 'lote_infracao', 'lote_infracao_item',
    'kpi_config', 'equipamento_disponibilidade', 'incidente_externo'
)
ORDER BY TABLE_SCHEMA, TABLE_NAME;

-- View criada
SELECT TABLE_SCHEMA + '.' + TABLE_NAME FROM INFORMATION_SCHEMA.VIEWS
WHERE TABLE_NAME = 'vw_passagem_anonimizada';

-- Configs inseridas
SELECT chave, valor FROM muralha.config_chave_valor
WHERE chave IN ('sla_latencia_threshold_ms','retencao_anos','anonimizar_placa_padrao',
                'disponibilidade_threshold_min','waze_api_url','waze_area_bbox');
```
