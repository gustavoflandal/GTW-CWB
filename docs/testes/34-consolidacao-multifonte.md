# Testes — Entrega 34: Consolidação Multifonte

## Testes unitários (TestNG)

| # | Classe | Método | Verificação |
|---|---|---|---|
| 1 | `ConsolidacaoTest` | `testResumoRetornaTodasAsFontes` | Ação `resumo` lista 4 fontes com totais |
| 2 | `ConsolidacaoTest` | `testConsultarSemFiltro` | Retorna registros de todas as fontes até o limite |
| 3 | `ConsolidacaoTest` | `testFiltrarPorFonte` | Com parâmetro `fonte=ALERTA`, só retorna alertas |
| 4 | `ConsolidacaoTest` | `testFiltrarPorPeriodo` | Filtro `dtInicio`/`dtFim` restringe resultados |
| 5 | `ConsolidacaoTest` | `testLimiteRespeitado` | Parâmetro `limite` é respeitado (máx 1000) |
| 6 | `ConsolidacaoTest` | `testListarFontes` | Ação `fontes` retorna array com nomes das fontes |
| 7 | `ConsolidacaoTest` | `testViewConsolidadaSchemaCorreto` | View retorna 10 colunas no schema esperado |
| 8 | `ConsolidacaoTest` | `testAcaoInvalidaRetornaErro` | Ação desconhecida retorna `ok: false` |

**Total: 8 testes**

## Teste manual no navegador

| # | Passo | Esperado |
|---|---|---|
| 1 | Acessar `inteligencia/consolidacao/index.jsp` | Cards de resumo carregam com totais por fonte |
| 2 | Selecionar fonte "ALERTA" e consultar | Tabela mostra apenas registros de alertas |
| 3 | Filtrar por período com datas válidas | Resultados restritos ao intervalo |
| 4 | Verificar badges coloridos na tabela | Cada fonte tem cor distinta |
| 5 | Verificar responsividade em tela estreita | Layout se adapta sem scroll horizontal |
