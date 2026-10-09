# Hash SHA-256 na Recepção de Imagens — Plano de Implementação

> **Para agentes:** use `superpowers:executing-plans` para executar tarefa por tarefa.

**Goal:** Calcular e armazenar hash SHA-256 de cada imagem recebida dos equipamentos; sinalizar registros com integridade comprometida e gerar alerta operacional.

**Architecture:** Adicionar colunas `sha256` e `status_integridade` em `muralha.veiculo_tempo_real_imagem`. Calcular o hash no ponto de inserção da imagem (SOAP `CSXEventsWS` ou job Quartz de importação). Registros com divergência recebem status `FALHA` e entram na fila de alertas operacionais existente.

**Tech Stack:** Java `MessageDigest.getInstance("SHA-256")` (sem dependência nova) · SQL Server · JDBC

## Global Constraints

- Sem nova dependência no `pom.xml`; usar `java.security.MessageDigest`
- Hash calculado **após** receber o byte array completo da imagem — nunca sobre stream parcial
- Imagem original não é alterada — hash é apenas metadado
- Se o equipamento envia hash esperado no cabeçalho SOAP: comparar; se não envia: apenas calcular e armazenar
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_sha256_imagem.sql` |
| Criar | `src/main/java/muralha/digital/util/HashUtil.java` |
| Modificar | `src/main/java/muralha/digital/veiculo/VeiculoImagemDAO.java` (ou equivalente de inserção) |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_sha256_imagem.sql
-- ROLLBACK:
-- ALTER TABLE muralha.veiculo_tempo_real_imagem DROP COLUMN sha256;
-- ALTER TABLE muralha.veiculo_tempo_real_imagem DROP COLUMN status_integridade;

ALTER TABLE muralha.veiculo_tempo_real_imagem
    ADD sha256             CHAR(64)     NULL,
        status_integridade VARCHAR(20)  NOT NULL DEFAULT 'OK';

CREATE INDEX ix_vtri_integridade ON muralha.veiculo_tempo_real_imagem (status_integridade)
WHERE status_integridade <> 'OK';
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_sha256_imagem.sql"
```

Esperado: sem erros, colunas adicionadas.

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_sha256_imagem.sql
git commit -m "Adiciona colunas sha256 e status_integridade em veiculo_tempo_real_imagem

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: HashUtil

- [ ] **Criar `HashUtil.java`**

```java
// src/main/java/muralha/digital/util/HashUtil.java
package muralha.digital.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtil {

    private HashUtil() {}

    /** Retorna SHA-256 em hex minúsculo do array de bytes fornecido. */
    public static String sha256Hex(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(data);
            StringBuilder sb = new StringBuilder(64);
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 está disponível em qualquer JVM >= 1.4
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }

    /**
     * Compara o hash calculado com o hash esperado.
     * @param data       bytes do arquivo
     * @param hashEsperado hash hex fornecido pelo remetente (pode ser null)
     * @return "OK" se hashEsperado==null ou se igual; "FALHA" se divergente
     */
    public static String verificarIntegridade(byte[] data, String hashEsperado) {
        String calculado = sha256Hex(data);
        if (hashEsperado == null || hashEsperado.isEmpty()) return "OK";
        return calculado.equalsIgnoreCase(hashEsperado) ? "OK" : "FALHA";
    }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

Esperado: `BUILD SUCCESS`

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/util/HashUtil.java
git commit -m "Adiciona HashUtil para cálculo de SHA-256

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: Integrar no ponto de inserção de imagens

- [ ] **Localizar o ponto de inserção de imagens**

```bash
# Procurar onde veiculo_tempo_real_imagem recebe INSERT
grep -r "veiculo_tempo_real_imagem" src/main/java --include="*.java" -l
```

O resultado mostrará o(s) arquivo(s) DAO/Service que fazem INSERT nessa tabela.

- [ ] **Modificar o método de inserção** (exemplo baseado no padrão do projeto)

```java
// No método que insere em veiculo_tempo_real_imagem, adicionar:
import muralha.digital.util.HashUtil;

// ... dentro do método de inserção, após obter os bytes da imagem:
byte[] bytesImagem = /* ... leitura do arquivo/stream ... */;
String hash = HashUtil.sha256Hex(bytesImagem);

// Adicionar as colunas sha256 e status_integridade no INSERT:
// Antes (exemplo):
//   INSERT INTO muralha.veiculo_tempo_real_imagem (id_veiculo, imagem, ...) VALUES (?, ?, ...)
// Depois:
//   INSERT INTO muralha.veiculo_tempo_real_imagem (id_veiculo, imagem, sha256, status_integridade, ...)
//   VALUES (?, ?, ?, ?, ...)
//   ps.setString(N,   hash);
//   ps.setString(N+1, "OK");   // equipamentos atuais não enviam hash, status sempre OK
```

- [ ] **Adicionar alerta para status FALHA** (para futuro — quando equipamentos passarem a enviar hash)

```java
// Após o INSERT, se status_integridade = "FALHA":
if ("FALHA".equals(statusIntegridade)) {
    // Reutilizar mecanismo existente de alertas operacionais
    // (inserir em muralha.alerta ou chamar spu_gerar_ocorrencia conforme padrão do projeto)
    // Usar o tipo de alerta existente mais próximo de "inconsistência técnica"
    AuditoriaService.registrar(request, "Recepcao", "integridade-sha256-falha",
        String.valueOf(idImagem), "Hash SHA-256 divergente para imagem " + idImagem);
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Teste**: receber uma passagem de equipamento (ou simular inserção manual) e verificar

```sql
SELECT TOP 5 id, sha256, status_integridade
FROM muralha.veiculo_tempo_real_imagem
ORDER BY id DESC;
-- Esperado: colunas sha256 preenchidas (64 chars hex), status_integridade = 'OK'
```

- [ ] **Commit**

```bash
git add src/main/java/
git commit -m "Integra cálculo de SHA-256 na inserção de imagens de passagens

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 4: Query de auditoria de integridade

- [ ] **Criar query para relatório de integridade** (para uso na PoC)

```sql
-- Query de verificação para demonstrar na PoC:
SELECT
    equipamento,
    COUNT(*) total_imagens,
    SUM(CASE WHEN status_integridade = 'OK'   THEN 1 ELSE 0 END) integras,
    SUM(CASE WHEN status_integridade = 'FALHA' THEN 1 ELSE 0 END) falhas,
    MIN(dt_recepcao) primeiro_registro,
    MAX(dt_recepcao) ultimo_registro
FROM muralha.veiculo_tempo_real_imagem vtri
JOIN muralha.veiculo_tempo_real vtr ON vtr.id = vtri.id_veiculo_tempo_real
GROUP BY equipamento
ORDER BY falhas DESC;
```

Salvar esta query em `docs/banco-de-dados/codigo-sql/relatorios/integridade_sha256.sql` para uso durante a PoC.

- [ ] **Commit**

```bash
git add docs/banco-de-dados/codigo-sql/relatorios/integridade_sha256.sql
git commit -m "Adiciona query de relatório de integridade SHA-256 por equipamento

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
