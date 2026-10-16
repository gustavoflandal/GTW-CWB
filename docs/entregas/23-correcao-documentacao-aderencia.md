# Entrega — Plano 23: Correção da Documentação de Aderência

> **Data:** 2026-10-16
> **Status:** 🟡 Implementado — aguardando validação no Windows (o script de validação foi executado na nuvem; falta repetir o roteiro na máquina de desenvolvimento e conferir a planilha no Excel).
> **Fase:** 1 de 11 da correção da auditoria de 2026-10-16. Só documentação: nenhuma alteração em código Java/JSP, `pom.xml`, `.setup-gtw` ou banco.

## Escopo

Correção da documentação de aderência ao TR TRANSALVADOR: matriz canônica única com os 232 itens do Capítulo 5, referências ao TR corrigidas e citadas literalmente, nomes de classes e telas trocados pelos reais, situações sem evidência rebaixadas, status das entregas sem teste ajustados para 🟡, índices e numeração explicados, e um script que valida tudo isso.

## Origem

- Plano: [`23-correcao-documentacao-aderencia.md`](../planos-salvador/23-correcao-documentacao-aderencia.md)
- Matriz canônica resultante: [`itens-edital-x-entregas.md`](itens-edital-x-entregas.md)

## Resultado

| Seção | Itens | ✅ | ⚠️ | ❌ | Cobertura |
|---|---|---|---|---|---|
| 5.1 | 14 | 11 | 3 | 0 | 89% |
| 5.2 | 39 | 27 | 10 | 2 | 82% |
| 5.3 | 28 | 11 | 15 | 2 | 66% |
| 5.4 | 33 | 11 | 20 | 2 | 64% |
| 5.5 | 23 | 6 | 10 | 7 | 48% |
| 5.6 | 32 | 8 | 6 | 18 | 34% |
| 5.7 | 38 | 11 | 12 | 15 | 45% |
| 5.8 | 25 | 1 | 8 | 16 | 20% |
| **Total** | **232** | **86** | **84** | **62** | **55%** |

Cobertura = (✅ + 0,5 × ⚠️) ÷ itens. ✅ inclui 54 itens implementados pela PoC e 32 já atendidos pelo GTW. A queda da seção 5.6 em relação à estimativa anterior vem da inclusão dos 28 subitens antes omitidos, quase todos de equipamento.

## Itens do TR com situação alterada

"Antes" é a situação na versão anterior desta matriz (link de entrega sem ressalva = ✅). Em vários itens, a [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) dizia ✅ mesmo quando a matriz já dizia parcial (por exemplo, 5.7.4.1–4); essas linhas também foram alinhadas.

| Item | Antes | Agora | Motivo |
|---|---|---|---|
| 5.1.10 | ✅ (01, 11) | ⚠️ | Log sem filtro por perfil/equipamento e sem PDF; login e servlets do legado não instrumentados |
| 5.2.1.2 | ✅ (02) | ⚠️ | `JobSha256Imagem` calcula o hash depois da gravação e grava `'OK'` sempre |
| 5.2.2.6 | ✅ (08) | ⚠️ | Painel sem atualização em tempo real |
| 5.2.4.1–2 | ✅ (17) | ✅ (05 + legado) | Referência ao plano errado |
| 5.2.8.4, 5.4.2.4 | ✅ | ⚠️ | Evidência só parcial no código |
| 5.3.2.1, 5.3.3.4, 5.3.6.2, 5.3.7.8 | ✅ Já atendido pelo GTW | ⚠️ | O legado cobre só parte do texto do TR |
| 5.3.6.4 | ✅ (11) | ⚠️ | A consulta de alertas exporta PDF e XLS, sem CSV; a Entrega 11 não cobre alertas |
| 5.3.7.4 | ❌ | ⚠️ (a–c legado; d–e ❌) | O legado já cobre as alíneas a–c |
| 5.3.7.5 | ⚠️ (07, "latência ≤ 4 s") | ⚠️ (03/04/09) | O item trata de navegador, autenticação e criptografia; os 4 s são do Capítulo 5, caput, alínea d |
| 5.4.4.4 | ✅ (16) | ⚠️ | Só GeoJSON/KML; Shapefile nunca existiu |
| 5.4.5.1–2, 5.4.6.2–3 | ✅ (15, 21) | ⚠️ | Indicadores sem histórico nem alerta analítico (Fase 7) |
| 5.4.7.1–5, 5.4.7.7 | ✅ (01) | ⚠️ | Logs técnicos existem no legado, mas sem repositório central (Fase 4) |
| 5.4.7.6 | ⚠️ (01) | ❌ | Nada implementado para o item |
| 5.5.3.1 | ✅ (10, 21) | ⚠️ | Painel incompleto (Fase 8) |
| 5.5.4.3 | ✅ (17) | ⚠️ (05) | Brilho e contraste existem; inversão de cores não (Fase 9) |
| 5.7.4.2 | ⚠️ | ❌ | `ia.veiculo_caracteristica` não tem coluna de score (Fase 5) |
| 5.7.4.4 | ✅ Já atendido pelo GTW | ❌ | Sem revisão automática por score (Fase 5) |
| 5.7.7.1 | ✅ (17) | ⚠️ | Player existe; vídeo sintético não (Fase 10) |
| 28 subitens de 5.6 e 5.7.7 | omitidos | 5 ✅, 7 ⚠️, 16 ❌ (marcados †) | Incluídos com o texto literal do TR |

## Arquivos alterados

- **Fontes de aderência:** [`itens-edital-x-entregas.md`](itens-edital-x-entregas.md) (reescrita como matriz canônica), [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md), [`aderencia-comparativa.md`](aderencia-comparativa.md), [`rastreabilidade-tr.md`](rastreabilidade-tr.md) (nova coluna Situação e tabela Plano → Entrega → Teste), [`gaps-amarelos-pendentes.md`](gaps-amarelos-pendentes.md), [`gaps-vermelhos-pendentes.md`](gaps-vermelhos-pendentes.md), planilha `Atividades Edital Salvador 2026.xlsx` (só a aba GTW-Muralha, coluna "Obs:", 87 células).
- **Planos:** 01–19 (aviso de errata), [07](../planos-salvador/07-sla-latencia.md) (referência dos 4 s), [20](../planos-salvador/20-dashboard-builder.md) (numeração e bibliotecas), [21](../planos-salvador/21-dashboards-consulta-sql-sem-cube.md) (executado, links quebrados), [22](../planos-salvador/22-sistema-chamados.md) (executado).
- **Entregas:** 02–21 (status, errata, colunas reais de senha/MFA, migrações executadas), [20](20-menu-modulos.md) (`## Origem`), [`pendencias-banco-de-dados.md`](pendencias-banco-de-dados.md) (senha/MFA em `dbo.sis_usuario_complemento`), [`README.md`](README.md).
- **Testes:** 03–07 (notas de migração), [`README.md`](../testes/README.md).
- **Índices:** [`docs/README.md`](../README.md), [`AGENTS.md`](../agentes/AGENTS.md), [`dashboards/sessoes/README.md`](../dashboards/sessoes/README.md), [`edital-salvador/README.md`](../../docs-editais/edital-salvador/README.md), [`CLAUDE.md`](../../CLAUDE.md).
- **Novo:** [`validar_aderencia.py`](../referencia/scripts/validar_aderencia.py).

## Migrações

Nenhuma. Nenhum DDL/DML foi executado.

## Critérios de aceite

- [x] 1. `python3 -I docs/referencia/scripts/validar_aderencia.py --resumo` termina com código 0: links quebrados 0, divergências de status 0, termos proibidos 0, planilha 0 (executado na nuvem em 2026-10-16).
- [x] 2. Nas fontes de aderência não há `IntegridadeJob`, `Shapefile` nem 5.3.7.5 associado a 4 s (verificado pelo script e por `grep`).
- [x] 3. Todas as entregas têm `## Origem` com link real; a Entrega 20 explica que não tem plano próprio.
- [ ] 4. Planilha aberta no Excel com comentários e tarefas preservados (na nuvem, só foi verificado que apenas `xl/worksheets/sheet6.xml` mudou e que o arquivo abre no openpyxl).
- [ ] 5. Roteiro [`docs/testes/23-correcao-documentacao-aderencia.md`](../testes/23-correcao-documentacao-aderencia.md) repetido no Windows.

Build e testes automatizados: não se aplicam (nenhum código alterado). Se desejado, `..\.setup-gtw\build.ps1` pode ser rodado na máquina de desenvolvimento para confirmar que nada mudou; não foi executado na nuvem porque o Maven depende do repositório privado da Consilux.

## Pendências

- `analise-aderencia.pdf` está desatualizado em relação ao `.md`; regenerar.
- As outras abas da planilha não foram revisadas.
- O texto condensado de 5.6 e 5.7.7 em `TR-Salvador-Sistema.md` não foi expandido; as citações literais vieram do PDF do TR.
