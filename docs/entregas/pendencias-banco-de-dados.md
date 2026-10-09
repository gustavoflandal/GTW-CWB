# Pendências de Banco de Dados — PoC TRANSALVADOR

Relação completa de todas as ações manuais necessárias no banco `GTW_MURALHA_DEV` (10.0.0.200) para ativar as funcionalidades implementadas nos Planos 01–19.

> **Atenção:** todas as migrações usam `IF NOT EXISTS` / `IF COL_LENGTH(...) IS NULL` — podem ser reexecutadas com segurança (idempotentes).  
> **Nenhuma migração faz ALTER TABLE** em tabelas existentes do sistema legado.

---

## 1. Migrações SQL — Ordem de Execução

Executar na ordem abaixo. Cada script é independente, mas a ordem respeita dependências lógicas.

| # | Script | Planos | Objetos criados | Pré-requisito |
|---|---|---|---|---|
| 1 | [`20261008_log_auditoria.sql`](../banco-de-dados/migracoes/20261008_log_auditoria.sql) | 01 | Tabela `dbo.sis_log_auditoria` + índices | — |
| 2 | [`20261008_politica_senhas.sql`](../banco-de-dados/migracoes/20261008_politica_senhas.sql) | 03 | Tabela `dbo.sis_senha_config` (8 parâmetros), colunas em `dbo.sis_usuario` | — |
| 3 | [`20261008_mfa_totp.sql`](../banco-de-dados/migracoes/20261008_mfa_totp.sql) | 04 | Colunas `totp_secret`, `totp_habilitado` em `dbo.sis_usuario` | #2 |
| 4 | [`20261008_dupla_analise.sql`](../banco-de-dados/migracoes/20261008_dupla_analise.sql) | 05 | Tabela `muralha.infracao_analise` + constraint UNIQUE | — |
| 5 | [`20261008_tabelas_complementares.sql`](../banco-de-dados/migracoes/20261008_tabelas_complementares.sql) | 02, 09, 11 | `muralha.vtr_imagem_complemento`, `dbo.sis_usuario_complemento` | — |
| 6 | [`20261008_complementar.sql`](../banco-de-dados/migracoes/20261008_complementar.sql) | 05, 06, 07 | `muralha.vtr_status_analise`, `muralha.vtr_imagem_obliterada`, `muralha.infracao_imagem_obliteracao`, `muralha.alerta_sla`, config `sla_latencia_threshold_ms` | — |
| 7 | [`20261008_sla_preproc.sql`](../banco-de-dados/migracoes/20261008_sla_preproc.sql) | 10 | Tabela `muralha.alerta_sla_preproc` | — |
| 8 | [`20261008_retencao_anonimizacao.sql`](../banco-de-dados/migracoes/20261008_retencao_anonimizacao.sql) | 12, 13 | `muralha.expurgo_log`, VIEW `muralha.v_vtr_anonimizado`, config `retencao_anos`, `anonimizar_placa_padrao` | — |
| 9 | [`20261008_lotes_kpis.sql`](../banco-de-dados/migracoes/20261008_lotes_kpis.sql) | 14, 15 | `muralha.lote_infracao`, `muralha.lote_infracao_item`, `muralha.kpi_config` (4 KPIs padrão) | — |
| 10 | [`20261008_disponibilidade_incidentes.sql`](../banco-de-dados/migracoes/20261008_disponibilidade_incidentes.sql) | 18, 19 | `muralha.equipamento_disponibilidade`, `muralha.incidente_externo` + constraint UNIQUE, configs | — |

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

Após executar as migrações, registrar as novas telas no menu do sistema. Os INSERTs abaixo devem ser adaptados conforme a estrutura real de `dbo.sis_menu_infos` (verificar colunas `id_pai`, `ordem`, `nome`, `url`, `icone`).

| Plano | Tela | URL sugerida | Grupo de menu |
|---|---|---|---|
| 01 | Consulta de Auditoria | `/muralha-digital/pages/auditoria/consulta-log.jsp` | Administração |
| 05 | Fila de Análise | `/muralha-digital/pages/processamento/fila-analise/fila.jsp` | Processamento |
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

> **Nota:** as telas dos Planos 02, 03, 04, 06 e 07 não requerem menu próprio — são funcionalidades integradas a fluxos existentes (login, análise de imagens, painel SLA).

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

---

## 5. Variáveis de Ambiente

| Variável | Plano | Obrigatória | Descrição |
|---|---|---|---|
| `WAZE_API_KEY` | 19 | Não | Chave da API Waze for Cities. Sem ela, o job ignora silenciosamente |

---

## 6. Verificação Pós-Migração

Após executar todos os scripts, validar com:

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
WHERE TABLE_NAME = 'v_vtr_anonimizado';

-- Configs inseridas
SELECT chave, valor FROM muralha.config_chave_valor
WHERE chave IN ('sla_latencia_threshold_ms','retencao_anos','anonimizar_placa_padrao',
                'disponibilidade_threshold_min','waze_api_url','waze_area_bbox');
```
