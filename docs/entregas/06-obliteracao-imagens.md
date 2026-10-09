# Entrega 06 — Obliteração de Imagens (LGPD)

**Data:** 2026-10-08  
**Status:** ✅ Implementado — migração SQL pendente de execução manual

---

## Escopo

Permite a obliteração manual de áreas sensíveis (rostos, ocupantes) em imagens de infrações, em conformidade com a LGPD. O arquivo original nunca é modificado; uma cópia com retângulos pretos é armazenada em tabela complementar separada (`muralha.vtr_imagem_obliterada`). A reversão exige justificativa e gera log de auditoria.

---

## Arquivos produzidos

| Ação | Arquivo |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_complementar.sql` (tabelas `vtr_imagem_obliterada` + `infracao_imagem_obliteracao`) |
| Criar | `src/main/java/muralha/digital/imagem/ObliteracaoService.java` |
| Criar | `src/main/java/muralha/digital/imagem/ObliteracaoServlet.java` |
| Modificar | `src/main/java/muralha/digital/processamento/InfracaoAnaliseDAO.java` |
| Modificar | `src/main/webapp/muralha-digital/pages/processamento/dupla-analise/analisar.jsp` |
| Criar | `src/main/webapp/muralha-digital/assets/js/processamento/obliteracao.js` |

> Migração antiga `20261008_obliteracao.sql` marcada como OBSOLETA (usava ALTER TABLE proibido).

---

## Critérios de aceite

| # | Critério | Status |
|---|---|---|
| 1 | Original imutável — tabela `veiculo_tempo_real_imagem` não é alterada | ✅ |
| 2 | Cópia obliterada em tabela complementar `vtr_imagem_obliterada` com `id_imagem_original` | ✅ |
| 3 | Coordenadas persistidas em `infracao_imagem_obliteracao` | ✅ |
| 4 | Reversão requer justificativa + log auditoria | ✅ |
| 5 | Canvas HTML5 para seleção de área na tela de análise | ✅ |
| 6 | Build bem-sucedido | ✅ |

---

## Decisões técnicas

- **Tabela complementar:** em vez de adicionar colunas `obliterada` e `id_original` em `veiculo_tempo_real_imagem` (~10M linhas), a cópia obliterada é gravada em `muralha.vtr_imagem_obliterada` — sem ALTER TABLE, sem lock exclusivo.
- **Java puro (`BufferedImage` + `Graphics2D`):** sem dependência externa; compatível com JDK 13.
- **Detecção de formato:** bytes iniciais `0xFF` → JPEG; demais → PNG.
- **Escala do canvas:** coordenadas são convertidas para a resolução natural da imagem antes de enviar ao servidor.

---

## Pendências

- Migração `20261008_complementar.sql` pendente de execução manual:
  ```powershell
  sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -U consilux -P "..." -i "docs\banco-de-dados\migracoes\20261008_complementar.sql"
  ```
- Obliteração automática (tipo `'A'`) não implementada neste plano.
- Permissão específica `OBLITERACAO_REVERTER` não criada — reversão usa apenas autenticação de sessão.

---

## Testes

Roteiro em [`docs/testes/06-obliteracao-imagens.md`](../testes/06-obliteracao-imagens.md)
