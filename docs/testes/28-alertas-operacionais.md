# Testes — Plano 28: Alertas Operacionais

Roteiro para a [Entrega 28](../entregas/28-alertas-operacionais.md). Executar a partir de `D:\GTW-CWB`.

## Pré-requisitos

- Build: `..\.setup-gtw\build.ps1`; servidor: `..\.setup-gtw\run.ps1`.
- Migração `docs/banco-de-dados/migracoes/20261016_alertas_operacionais.sql` executada com autorização (`sqlcmd -b -f 65001 -i …`).
- Usuário do grupo 42 (configuração de alertas).

## T01 — Testes automatizados

```powershell
..\.setup-gtw\gtw.ps1 -ModulePath D:\GTW-CWB -Goals test -AllowInsecure `
  -MavenArgs '-Dexec.skip=true','-Dtest=RegraRetencaoTest,ProcessadorRetencaoTest,ConfigETratamentoTest'
```

**Esperado:** 20 testes, 0 falhas. No Windows (2026-10-16): ✅ 20/20.

## T02 — Migração

```sql
SELECT COUNT(*) AS tipos FROM muralha.tipo_alerta_ocorrencia;
SELECT id_tipo_alerta_ocorrencia, critico, cor, icone FROM muralha.tipo_alerta_config;
SELECT t.tipo, tr.tratamento, tr.automatico FROM muralha.tipo_alerta_tratamento tr
  JOIN muralha.tipo_alerta_ocorrencia t ON t.id = tr.id_tipo_alerta_ocorrencia WHERE tr.ativo = 1;
SELECT chave, valor FROM muralha.config_chave_valor WHERE chave LIKE 'RETENCAO[_]%';
SELECT OBJECT_ID('muralha.alerta_retencao', 'U') AS tabela_existe;
```

**Esperado:** 13 tipos (9 existentes + 4 novos), 13 configs de cor, 6 tratamentos seed, 6 parâmetros `RETENCAO_*`, tabela `alerta_retencao` existe. Rodar de novo só imprime "Ja existe". ⏳ Pendente.

## T03 — Configuração de tipos de alerta

1. Navegar para Alertas > Tipos de Alerta (`/MuralhaDigital/TipoAlertaConfig`).
2. Alterar a cor do tipo "Roubado" para `red` e o ícone para `bi-shield-exclamation`. Salvar.
3. Marcar "Roubado" como crítico.

**Esperado:** as alterações persistem ao recarregar; `tipo_alerta_config` reflete as mudanças; `sis_log_auditoria` registra a operação. No popup de notificação, o alerta "Roubado" usa a nova cor. ⏳ Pendente.

## T04 — Tratamentos de um tipo

1. Na tela de tipos, selecionar "Congestionamento/Retenção" e ver tratamentos.
2. Adicionar o tratamento `CAMPO` (manual). Salvar.
3. Verificar que `REGISTRO_ESTATISTICO` (automático) já existe do seed.

**Esperado:** 3 tratamentos listados (REGISTRO_ESTATISTICO automático, ANALISE_OPERACIONAL manual, CAMPO manual); a tentativa de inserir um tratamento duplicado deve retornar erro de constraint. ⏳ Pendente.

## T05 — Detecção de retenção/congestionamento

1. No DEV, inserir dados simulados em `dbo.veiculo_sumarizado` para um `id_local` com 3 horas consecutivas de velocidade 10, ocupação 600, tráfego 50:
```sql
DECLARE @hoje DATE = CAST(GETDATE() AS DATE);
INSERT INTO dbo.veiculo_sumarizado (data, hora, id_local, media_velocidade, ocupacao, trafego)
VALUES (@hoje, DATEPART(HOUR, GETDATE())-3, 999, 10.0, 600, 50),
       (@hoje, DATEPART(HOUR, GETDATE())-2, 999, 8.0, 700, 45),
       (@hoje, DATEPART(HOUR, GETDATE())-1, 999, 12.0, 550, 40);
```
2. Aguardar o job (10 min) ou reiniciar o servidor.

**Esperado:** alerta de tipo "Congestionamento/Retenção" gerado em `muralha.alerta` para `id_local = 999`; `muralha.alerta_retencao` com `horas_consecutivas = 3`, `severidade = 'MEDIA'`, velocidade e ocupação médias; tratamento `REGISTRO_ESTATISTICO` executado automaticamente (log de auditoria). Outro job dentro de 60 min não gera duplicata (espera de reincidência). ⏳ Pendente.

## T06 — Filtros avançados na consulta de alertas

1. Na consulta de alertas (`consulta-alerta-ocorrencia`), verificar os novos filtros: localidade, usuário responsável e resultado.
2. Filtrar por localidade específica.
3. Filtrar por resultado "PENDENTE".

**Esperado:** os filtros reduzem os resultados corretamente; a combinação de filtros funciona; um resultado inválido não é aceito. ⏳ Pendente.

## T07 — Exportação CSV

1. Na consulta de alertas, pesquisar com filtros (ex: tipo "Roubado", último mês).
2. Clicar no botão CSV.

**Esperado:** o download gera um arquivo `.csv` com as colunas da tabela, codificação UTF-8, separador ponto e vírgula. ⏳ Pendente.

## T08 — Cores dinâmicas no popup de notificação

1. Abrir o sistema com um usuário que receba notificações.
2. Verificar no console do navegador que `carregarCoresTipoAlerta()` carregou sem erro.
3. Se possível, gerar um alerta de tipo "Roubado" (com a cor alterada no T03).

**Esperado:** a notificação usa a cor configurada no banco, não a cor fixa anterior. Se o banco não estiver acessível, usa o fallback do mapa fixo. ⏳ Pendente.
