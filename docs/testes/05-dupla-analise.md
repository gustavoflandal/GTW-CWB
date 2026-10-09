# Testes — Plano 05: Dupla Análise Independente

**Data:** 2026-10-08  
**Ambiente:** localhost:8080 / GTW_MURALHA_DEV  
**Build:** SUCCESS

---

## Pré-requisito

Execute a migração:
```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -P "..." -i "docs\banco-de-dados\migracoes\20261008_dupla_analise.sql"
```

---

## T01 — Tabelas criadas

```sql
SELECT TOP 1 * FROM muralha.infracao_analise;
SELECT TOP 1 status_analise FROM muralha.veiculo_tempo_real;
-- Esperado: sem erro; status_analise = 'AGUARDANDO_ANALISE' para todos os registros existentes
```

**Resultado:** pendente execução da migração

---

## T02 — Fila retorna próxima infração

```
GET /MuralhaDigital/InfracaoAnalise?acao=proximaFila
-- Esperado: {"ok":true,"infracao":{"id":"...","placa":"...","data":"...",...}}
-- OU: {"ok":true,"filaVazia":true} se não houver registros
```

**Resultado:** pendente teste manual

---

## T03 — Primeiro operador classifica

1. Usuário A acessa `fila.jsp` → clica "Próxima Infração" → redirecionado para `analisar.jsp`
2. Seleciona "Válida" → clica "Confirmar Análise"
3. Esperado: resposta `{"ok":true,"novoStatus":"PRIMEIRA_ANALISE"}`
4. Verificar banco:
```sql
SELECT id_infracao, id_usuario, sequencia, classificacao
FROM muralha.infracao_analise ORDER BY id DESC;
-- Esperado: 1 linha com sequencia=1, classificacao='VALIDA'

SELECT id, status_analise FROM muralha.veiculo_tempo_real
WHERE id = '<id_da_infracao>';
-- Esperado: status_analise = 'PRIMEIRA_ANALISE'
```

**Resultado:** pendente teste manual

---

## T04 — Mesmo operador não pode analisar duas vezes

Usuário A tenta acessar `fila.jsp` novamente → a infração já analisada NÃO deve aparecer  
(exclusão via `NOT IN (SELECT id_infracao WHERE id_usuario = ?)`)

**Resultado:** pendente teste manual

---

## T05 — Segundo operador concorda → PRE_APROVADA

1. Usuário B acessa `fila.jsp` → vê a infração com status PRIMEIRA_ANALISE
2. Classifica como "Válida"
3. Esperado: `{"ok":true,"novoStatus":"PRE_APROVADA"}`

**Resultado:** pendente teste manual

---

## T06 — Divergência envia para DESEMPATE

1. Recomeçar com nova infração
2. Usuário A classifica "Válida", Usuário B classifica "Inválida"
3. Esperado: `{"ok":true,"novoStatus":"DESEMPATE"}`
4. Usuário C (desempatador) classifica "Inválida" → `{"ok":true,"novoStatus":"REPROVADA"}`

**Resultado:** pendente teste manual

---

## T07 — Indicadores refletem o fluxo

```
GET /MuralhaDigital/InfracaoAnalise?acao=indicadores
-- Esperado: array com contagens por status
```

**Resultado:** pendente teste manual

---

## Observações

- A constraint `UNIQUE (id_infracao, id_usuario)` garante exclusividade no banco — não depende apenas da aplicação
- O endpoint de imagem `VeiculoTempoReal?acao=obterImagem&id=...` pode não existir — neste caso a imagem não carrega mas o fluxo de análise funciona
- Necessário cadastrar o menu no banco via INSERT em `dbo.sis_menu_infos`
