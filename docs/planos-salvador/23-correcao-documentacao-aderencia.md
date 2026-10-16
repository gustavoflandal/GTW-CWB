# Correção da Documentação de Aderência — Plano 23 (Fase 1 da auditoria de 2026-10-16)

> **Status:** Executado (2026-10-16) — ver [Entrega 23](../entregas/23-correcao-documentacao-aderencia.md). Só documentação: nenhum código Java/JSP, `pom.xml` ou banco foi alterado.
> **Errata:** ver correções em [`00c-errata-correcoes.md`](00c-errata-correcoes.md) antes de usar o código dos planos 01–19.

**Objetivo:** corrigir os erros que a auditoria de 2026-10-16 encontrou na documentação de aderência ao TR TRANSALVADOR (referências erradas ao TR, nomes de classes e telas que não existem, situações otimistas demais, matrizes divergentes entre si, numeração e índices desalinhados) e deixar um script que detecte a volta desses erros.

**Princípios:** (1) uma única matriz de status, as demais derivam dela; (2) "✅" só com evidência no código ou no banco; (3) nenhuma referência a item do TR ou documento que não exista; (4) citações do TR literais, copiadas do PDF.

## Referência ao Edital (TR TRANSALVADOR)

Esta fase não implementa requisito: corrige a documentação de todos os itens do Capítulo 5 (232 itens). Os itens com situação alterada estão listados na [Entrega 23](../entregas/23-correcao-documentacao-aderencia.md#itens-do-tr-com-situação-alterada).

## Decisões

| Tema | Decisão |
|---|---|
| Fonte primária | [`docs/entregas/itens-edital-x-entregas.md`](../entregas/itens-edital-x-entregas.md) passa a ser a matriz canônica. [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md), [`aderencia-comparativa.md`](../entregas/aderencia-comparativa.md), [`rastreabilidade-tr.md`](../entregas/rastreabilidade-tr.md) e a aba GTW-Muralha da planilha seguem os status dela. |
| Categorias | ✅ Implementado (PoC), ✅ Já atendido pelo GTW, ⚠️ Parcial, ❌ Não implementado. O status "⚠️→🔧" deixa de existir. Marcas: † situação atribuída na revisão de 2026-10-16 (item antes omitido); ‡ obrigação contratual ou de equipamento, fora do software. |
| Cobertura | (✅ + 0,5 × ⚠️) ÷ itens, por seção e global. |
| Requisito de 4 s | O tempo de 4 segundos é do Capítulo 5, caput, alínea d (e §5.2.2.1). O §5.3.7.5 trata de navegador, autenticação e criptografia; o Plano 07 e as fontes de aderência foram corrigidos. |
| Status das entregas | Entrega com critério de aceite não executado fica "🟡 Implementado — aguardando teste/migração". Aplicado às entregas 08–21, além das 08, 10 e 15–18 apontadas pela auditoria. |
| Planilha | Editada direto no XML da aba (`xl/worksheets/sheet6.xml`), sem openpyxl, para preservar comentários encadeados e tarefas do Excel. |

## Tarefas

- [x] 1. Corrigir a referência do requisito de 4 s (análise, comparativa, gaps amarelos, Plano 07, matriz → Entregas 03/04/09 para 5.3.7.5).
- [x] 2. Trocar nomes inexistentes pelos reais: `IntegridadeJob` → `JobSha256Imagem`; `auditoria/index.jsp` → `consulta-log.jsp`; `PoliticaSenhaServlet` → `SenhaService`; `TotpServlet` → `TotpService`/`MfaConfigServlet`; `CpfMatriculaServlet` → `UsuarioCpfMatriculaServlet`; `DuplaAnaliseServlet` → `InfracaoAnaliseServlet`; `AnonimizacaoServlet` → `ConsultaAnonimizadaServlet`; `SlaPreProcessamentoServlet` → `SlaPreprocessamentoServlet` (incluindo a coluna "Obs:" da planilha).
- [x] 3. Rebaixar situações sem evidência: 5.2.1.2, 5.2.2.6, 5.3.2.1, 5.3.6.4, 5.3.7.4 (a–c parcial pelo legado, d–e ❌), 5.4.4.4 (sem Shapefile), 5.4.6.3, 5.4.7.1–7 (5.4.7.6 ❌), 5.7.4.1–4, 5.7.7; 5.2.4.1–2 e 5.5.4.3 passam a apontar para o Plano 05 (não 17).
- [x] 4. Citar o TR literalmente nos gaps amarelos (5.2.1.3, 5.3.1.3, 5.3.4.2, 5.4.1.1, 5.4.5.1, 5.5.3.1, 5.5.4.5) e vermelhos (5.3.7.4, 5.5.3.1).
- [x] 5. Montar a matriz canônica com os 232 itens, incluir os 28 subitens antes omitidos de 5.6 e 5.7.7, recalcular cobertura e alinhar as outras três fontes e a planilha.
- [x] 6. Explicar a numeração Plano 20 → Entrega 21 → Teste 20 e a origem da Entrega 20 (pendência da Entrega 17).
- [x] 7. Atualizar índices: `docs/README.md`, `docs/entregas/README.md`, `docs/testes/README.md`, `docs/agentes/AGENTS.md`, `docs/dashboards/sessoes/README.md`, `docs-editais/edital-salvador/README.md`.
- [x] 8. Atualizar estados congelados: Planos 21 e 22 executados; migrações dos planos 03–08 executadas no DEV; colunas reais de senha/MFA em `dbo.sis_usuario_complemento`.
- [x] 9. Ajustar status das entregas sem teste executado para 🟡.
- [x] 10. Inserir aviso de errata nos planos 01–19 e citar a errata nas entregas afetadas (02, 03, 04, 05, 06, 07, 09, 12, 14).
- [x] 11. Corrigir os links quebrados do Plano 21 (arquivos do Cube removidos na Tarefa 9).
- [x] 12. Registrar no `CLAUDE.md` a exceção das bibliotecas vendorizadas do Dashboard Builder.
- [x] 13. Criar [`docs/referencia/scripts/validar_aderencia.py`](../referencia/scripts/validar_aderencia.py) (só biblioteca padrão): links relativos, divergência de status entre as fontes e a planilha, termos proibidos e resumo da cobertura.

## Critérios de aceite

1. `python -I docs/referencia/scripts/validar_aderencia.py` termina com código 0: zero links quebrados, zero divergências, zero termos proibidos, planilha alinhada.
2. Nas fontes de aderência não aparece `IntegridadeJob`, `Shapefile` nem 5.3.7.5 associado a 4 segundos.
3. Cada entrega tem `## Origem` com link para plano real; a Entrega 20 explica que não tem plano.
4. A planilha abre no Excel com os comentários e tarefas preservados.

## Próximas fases

Cada fase abaixo terá plano, entrega e roteiro próprios (a partir do número 24), branch `fix/auditoria-fase-NN-<assunto>` e PR, e só começa após aprovação da anterior.

| Fase | Assunto | Itens do TR |
|---|---|---|
| 2 | Bugs pontuais: `ConfiguracaoSons.ObterPrioridade`, `RelatorioAlteracaoConfigEquip` (menu 201), `AuditoriaService` × errata, script próprio de `muralha.vtr_imagem_complemento`, IP fixo do `maps-config.js`, array `jams` da Waze | — |
| 3 | Integridade SHA-256 real: hash na recepção e comparação com o hash de origem (hoje `JobSha256Imagem` grava `'OK'` sempre) | 5.2.1.2, 5.2.1.3 d–f |
| 4 | Repositório central de logs técnicos e histórico de parâmetros | 5.4.7, 5.2.7.2–3 |
| 5 | Score de OCR, revisão automática e múltiplas placas | 5.7.4, 5.2.2.5, 5.7.1.4, 5.7.5.2, 5.7.5.4 |
| 6 | Alertas operacionais | 5.3.1.3, 5.3.2.1, 5.3.3.3–4, 5.3.4.2–3, 5.3.6, 5.3.7.4 d–e, 5.3.7.6 |
| 7 | Indicadores com histórico, alertas analíticos e relatório periódico | 5.4.3.5–7, 5.4.6.2–3, 5.5.3.3 |
| 8 | Painéis de assertividade e de pré-processamento | 5.2.2.3, 5.2.2.6, 5.5.3.1–4 |
| 9 | Acabamentos de baixo custo: filtros e PDF no log de auditoria, inversão de cores nas telas de análise, mais camadas na exportação GIS, remoção da v1 do builder se não houver uso | 5.1.10, 5.5.4.3, 5.4.4.4 |
| 10 | Time-lapse parametrizado (MP4 via FFmpeg atrás de chave, desligado por padrão) | 5.7.7 |
| 11 | Envio do Captura por socket com fallback | 5.2.2.1, 5.6.2.3, 5.6.4 |
| Final | Gaps amarelos ([`gaps-amarelos-pendentes.md`](../entregas/gaps-amarelos-pendentes.md)) | vários |
