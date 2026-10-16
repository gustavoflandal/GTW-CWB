# Testes — Plano 26: Logs Técnicos e Histórico de Parâmetros

Roteiro para a [Entrega 26](../entregas/26-logs-tecnicos-historico-parametros.md). Executar a partir de `D:\GTW-CWB`.

## Pré-requisitos

- Build: `..\.setup-gtw\build.ps1`; servidor: `..\.setup-gtw\run.ps1`.
- Migrações executadas com autorização (`sqlcmd -b -f 65001 -i …`): `20261016_logs_tecnicos_parametro_historico.sql` e `20261016_desativa_menu_201.sql`.
- Usuário dos grupos 41 ou 42.

## T01 — Testes automatizados

```powershell
..\.setup-gtw\gtw.ps1 -ModulePath D:\GTW-CWB -Goals test -AllowInsecure `
  -MavenArgs '-Dexec.skip=true','-Dtest=FontesLogTecnicoTest,ClassificacaoCsxTest,CorrelacionadorIntervalosTest,HistoricoParametrosTest,FiltroERelatorioLogTecnicoTest'
```

**Esperado:** 42 testes, 0 falhas. No Windows (2026-10-16): ✅ 42/42.

## T02 — Migração

```sql
SELECT OBJECT_ID('muralha.parametro_historico');
SELECT chave, valor FROM muralha.config_chave_valor WHERE chave LIKE 'LOG[_]TECNICO[_]%' OR chave = 'PARAMETRO_HIST_IGNORAR_CONFIGEQUIP';
SELECT Ativo FROM dbo.sis_menu WHERE id_menu = 201;
```

**Esperado:** tabela criada, seis chaves, menu 201 com `Ativo = 0`. Rodar de novo só imprime "Ja existe"/"ja inativo". ✅ 2026-10-16: tabela criada, seis chaves, menus 387-388, menu 201 com `Ativo = 0`.

## T03 — Eventos

Análise de Dados → Logs Técnicos, aba Eventos, últimos 7 dias, sem filtros → Pesquisar.

**Esperado:** eventos do Captura, de conexão/energia, SLA e auditoria misturados por data; selo de severidade; linhas de ERRO/CRÍTICO em vermelho e ALERTA em amarelo; total e paginação. Filtrar equipamento 1 + categoria Energia e Comunicação + severidade ALERTA: só linhas desse equipamento e dessas categorias. Filtrar uma localidade: só equipamentos dela. Período acima de 31 dias: aviso. CSV: BOM, filtro no cabeçalho, rodapé `# SHA-256:`; registro `exportar-csv` em `sis_log_auditoria`. ⏳ Pendente.

## T04 — Correlação temporal

Na aba Eventos, botão Correlacionar de um evento de conexão.

**Esperado:** modal com os eventos de **todos** os equipamentos de 10 min antes a 10 min depois (parâmetro `LOG_TECNICO_JANELA_CORRELACAO_MIN`), o evento escolhido em negrito. ⏳ Pendente.

## T05 — Linha do tempo

Aba Linha do tempo, equipamento 1, últimos 2 dias → Gerar.

**Esperado:** uma faixa por categoria presente; barras para os períodos (ex.: Comunicação desconectado → conectado), listradas quando em aberto; pontos coloridos por severidade com descrição ao passar o mouse; clicar num ponto abre a correlação. Tabela de períodos com início, fim e duração. No DEV de 2026-10-16 a consulta equivalente devolveu 2 124 eventos e 11 períodos. ⏳ Pendente.

## T06 — Relatório técnico

Aba Relatório técnico, equipamento 1, últimos 7 dias → Gerar → PDF.

**Esperado:** quatro quadros (eventos por categoria e severidade, períodos com duração no período, parâmetros alterados, erros e críticos); PDF paisagem com os quatro; registro `relatorio-equipamento` em `sis_log_auditoria`. ⏳ Pendente.

## T07 — Histórico de parâmetros

1. Configuração de KPIs: mudar o limiar "ok" de um KPI. **Esperado:** na aba Histórico de parâmetros, recurso KPI, linha `threshold_ok` com valor anterior e novo, usuário e IP.
2. Retenção de dados: mudar os anos. **Esperado:** linha `CONFIG_CHAVE_VALOR / retencao_anos / valor`.
3. Integridade das Imagens → Configuração: mudar a origem de um equipamento. **Esperado:** linha `EQUIPAMENTO / <id_local> / integridade.origem_hash`; na aba Eventos do mesmo equipamento, um evento de Parâmetro.
4. ConfigEquipApp: gravar uma configuração alterando um campo (ex.: limite de velocidade). **Esperado:** linhas `EQUIPAMENTO / <id_local>` só com os campos alterados (sem `dataHoraConfiguracao`), origem `ConfigEquip`.
5. Sem a tabela (antes da migração): as gravações continuam funcionando e o log tem "Histórico de parâmetros não gravado".

⏳ Pendente.

## T08 — Menu 201 desativado

GTW clássico: o item "Relatório Alt. ConfigEquip" não aparece; abrir `/relatorio/RelatorioAlteracaoConfigEquip` mostra "Relatório desativado…". ⏳ Pendente.

## T09 — Permissão

Sem sessão: `GET /MuralhaDigital/LogTecnico?acao=opcoes` → 401. Usuário sem os grupos 41/42 (depois da migração): redirecionado. ⏳ Pendente.
