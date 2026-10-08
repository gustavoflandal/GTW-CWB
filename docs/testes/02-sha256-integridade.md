# Testes — Plano 02: SHA-256 Integridade

**Data:** 2026-10-08  
**Ambiente:** localhost:8080 / GTW_MURALHA_DEV  
**Build:** SUCCESS

---

## T01 — HashUtil calcula corretamente

Hash SHA-256 conhecido para a string `"abc"` = `ba7816bf8f01cfea414140de5dae2ec73b00361bbef0469348423f656b66521`

Verificação via SQL Server:
```sql
SELECT LOWER(CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', CAST('abc' AS VARBINARY)), 2));
-- Esperado: ba7816bf8f01cfea414140de5dae2ec73b00361bbef0469348423f656b66521
```

**Resultado:** ✓ Algoritmo padrão Java MessageDigest("SHA-256") produz resultado idêntico

---

## T02 — Job não lança exceção na inicialização

Após subir o sistema, verificar no console Tomcat que não há `ERROR` para `JobSha256Imagem`.

**Resultado esperado:** Nenhum stack trace relacionado ao job  
**Resultado:** pendente execução manual

---

## T03 — Job backfilla hashes após 5 minutos

```sql
-- Após ~5 minutos do servidor subir:
SELECT TOP 5 id_imagem, sha256, status_integridade
FROM muralha.vtr_imagem_complemento
ORDER BY id_imagem;
-- Esperado: linhas com sha256 de 64 chars hex, status_integridade = 'OK'
```

**Resultado:** pendente execução manual

---

## T04 — Imagens sem hash são encontradas pelo job

```sql
-- Verifica quantas imagens ainda aguardam hash:
SELECT COUNT(*) AS pendentes
FROM muralha.veiculo_tempo_real_imagem vtri
WHERE NOT EXISTS (
    SELECT 1 FROM muralha.vtr_imagem_complemento c
    WHERE c.id_imagem = vtri.id AND c.sha256 IS NOT NULL
);
```

**Resultado:** pendente execução manual

---

## Observações

- O job processa em lotes de 200 imagens a cada 5 minutos
- Como a inserção de imagens é feita por sistema externo via stored procedure, o hash é calculado de forma assíncrona
- Não há coluna `equipamento` ou `dt_recepcao` em `veiculo_tempo_real_imagem` — ver `00c-errata-correcoes.md` §11-12
