# Entrega — Plano 02: Hash SHA-256 de Integridade de Imagens

**Data de entrega:** 2026-10-08  
**Commit principal:** `7de3357`  
**Status:** ✅ Entregue

---

## Origem

- Plano [`02-sha256-integridade.md`](../planos-salvador/02-sha256-integridade.md) — Hash SHA-256 de Integridade
- **Requisitos TR:** §5.2.1.2 (verificação de integridade SHA-256 na recepção)
- **Análise de aderência:** [`analise-aderencia.md`](../../docs-editais/edital-salvador/analise-aderencia.md) §2

## O que foi entregue

### Banco de dados
- Tabela complementar `muralha.vtr_imagem_complemento` (UNIQUEIDENTIFIER PK, coluna `sha256`, `status_integridade`)
- Script: [`docs/banco-de-dados/migracoes/20261008_tabelas_complementares.sql`](../banco-de-dados/migracoes/20261008_tabelas_complementares.sql)

> **Decisão de arquitetura:** O INSERT em `veiculo_tempo_real_imagem` é feito pelo sistema externo via stored procedure `dbo.spu_veiculo_tempo_real_imagem` — não é interceptável em Java. O hash é calculado de forma assíncrona por um Quartz Job.

### Back-end
| Arquivo | Responsabilidade |
|---|---|
| `muralha/digital/util/HashUtil.java` | Calcula SHA-256 hex de `byte[]` via `MessageDigest` |
| `muralha/digital/integridade/JobSha256Imagem.java` | Quartz Job: processa lotes de 200 imagens a cada 5 min, MERGE em `vtr_imagem_complemento` |
| `com/consilux/servlet/ferramentas/Agendador.java` | Registra e agenda `JobSha256Imagem` no Quartz scheduler |

---

## Critérios de aceite

| # | Critério | Status |
|---|---|---|
| 1 | Tabela `muralha.vtr_imagem_complemento` criada | ✅ |
| 2 | `HashUtil.sha256Hex()` produz hash correto (compatível com SQL Server `HASHBYTES('SHA2_256',...)`) | ✅ |
| 3 | Job registrado no Agendador sem erro de inicialização | ✅ (build ok) |
| 4 | Após ~5 min, imagens existentes recebem hash em `vtr_imagem_complemento` | ⏳ Verificação manual pendente |
| 5 | MERGE nunca duplica registros | ✅ (cláusula MERGE ON id_imagem) |

---

## Limitações conhecidas

- Imagens inseridas pelo sistema externo só recebem hash na próxima execução do job (até 5 min de delay)
- Se a tabela `veiculo_tempo_real_imagem` estiver vazia no ambiente de dev, o job não produz saída visível

---

## Como testar

```sql
-- Após ~5 min do servidor subir:
SELECT TOP 5 id_imagem, sha256, status_integridade
FROM muralha.vtr_imagem_complemento ORDER BY id_imagem;
-- Esperado: sha256 com 64 chars hex, status_integridade = 'OK'

-- Verificar pendentes:
SELECT COUNT(*) AS pendentes
FROM muralha.veiculo_tempo_real_imagem vtri
WHERE NOT EXISTS (
    SELECT 1 FROM muralha.vtr_imagem_complemento c
    WHERE c.id_imagem = vtri.id AND c.sha256 IS NOT NULL
);
```

Testes detalhados: [`docs/testes/02-sha256-integridade.md`](../testes/02-sha256-integridade.md)
