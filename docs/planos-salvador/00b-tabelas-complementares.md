# Tabelas Complementares — Restrição de Banco Compartilhado

> **Leia antes de executar os planos 02–10.**

## Regra

O banco `GTW_MURALHA_DEV` atende outros sistemas. Nenhuma tabela ou coluna existente pode ser alterada. Todos os campos novos são mantidos em **tabelas complementares** com FK para a tabela original.

## Consolidação das tabelas complementares

Os planos 02, 03, 04, 05, 06, 07, 09 e 10 geram três tabelas complementares ao invés de `ALTER TABLE`:

| Tabela complementar | Substitui ALTER em | Planos |
|---|---|---|
| `muralha.vtr_complemento` | `muralha.veiculo_tempo_real` | 05, 07, 10 |
| `muralha.vtr_imagem_complemento` | `muralha.veiculo_tempo_real_imagem` | 02, 06 |
| `dbo.sis_usuario_complemento` | `dbo.sis_usuario` | 03, 04, 09 |

### Migração consolidada — executar uma única vez

```sql
-- docs/banco-de-dados/migracoes/20261008_tabelas_complementares.sql
-- ROLLBACK: DROP TABLE muralha.vtr_complemento, muralha.vtr_imagem_complemento, dbo.sis_usuario_complemento;

-- ── 1. Complemento de veiculo_tempo_real ─────────────────────────────────────
-- ATENÇÃO: muralha.veiculo_tempo_real.id é uniqueidentifier (não bigint!)
CREATE TABLE muralha.vtr_complemento (
    id_vtr          UNIQUEIDENTIFIER PRIMARY KEY,  -- mesmo PK de muralha.veiculo_tempo_real
    status_analise  VARCHAR(20)  NULL,   -- plano 05: PENDENTE/PRE_APROVADA/DESEMPATE/REPROVADA
    latencia_ms     INT          NULL,   -- plano 07: ms entre captura e recepção
    dt_preclass     DATETIME2    NULL    -- plano 10: quando saiu de PENDENTE
);

-- ── 2. Complemento de veiculo_tempo_real_imagem ──────────────────────────────
-- ATENÇÃO: muralha.veiculo_tempo_real_imagem.id é uniqueidentifier (não bigint!)
CREATE TABLE muralha.vtr_imagem_complemento (
    id_imagem       UNIQUEIDENTIFIER PRIMARY KEY,  -- mesmo PK de muralha.veiculo_tempo_real_imagem
    sha256          CHAR(64)     NULL,   -- plano 02
    status_integridade VARCHAR(20) NOT NULL DEFAULT 'OK',  -- plano 02
    obliterada      BIT          NOT NULL DEFAULT 0,       -- plano 06
    id_original     UNIQUEIDENTIFIER NULL   -- plano 06: preenchido apenas na cópia obliterada
);

-- ── 3. Complemento de sis_usuario ────────────────────────────────────────────
CREATE TABLE dbo.sis_usuario_complemento (
    id_usuario      INT PRIMARY KEY,     -- mesmo PK de dbo.sis_usuario
    senha_hash      VARCHAR(64)  NULL,   -- plano 03: SHA-256 hex (nova hash)
    senha_historico VARCHAR(MAX) NULL,   -- plano 03: JSON array dos últimos N hashes
    tentativas_invalidas INT NOT NULL DEFAULT 0,  -- plano 03
    bloqueado_ate   DATETIME2    NULL,   -- plano 03
    senha_trocada_em DATETIME2   NULL,  -- plano 03
    totp_secret     VARCHAR(200) NULL,   -- plano 04
    totp_habilitado BIT          NOT NULL DEFAULT 0,  -- plano 04
    cpf             CHAR(11)     NULL,   -- plano 09
    matricula       VARCHAR(20)  NULL    -- plano 09
);

CREATE UNIQUE INDEX uq_comp_cpf       ON dbo.sis_usuario_complemento(cpf)       WHERE cpf IS NOT NULL;
CREATE UNIQUE INDEX uq_comp_matricula ON dbo.sis_usuario_complemento(matricula) WHERE matricula IS NOT NULL;
```

Execute este script **no lugar** das migrações individuais de cada plano listado acima.

---

## Impacto nos planos: como adaptar o código Java

Todos os `INSERT`/`UPDATE` que antes usavam colunas via `ALTER TABLE` agora usam `UPSERT` nas tabelas complementares:

### Padrão de UPSERT para tabelas complementares (SQL Server)

```sql
-- Exemplo para vtr_complemento (status_analise):
-- id_vtr é uniqueidentifier → ps.setString(N, uuid.toString()) no Java
MERGE muralha.vtr_complemento AS target
USING (SELECT CAST(? AS UNIQUEIDENTIFIER) AS id_vtr, ? AS status_analise) AS source ON target.id_vtr = source.id_vtr
WHEN MATCHED THEN UPDATE SET status_analise = source.status_analise
WHEN NOT MATCHED THEN INSERT (id_vtr, status_analise) VALUES (source.id_vtr, source.status_analise);
```

### Padrão de INSERT com complemento

```java
// 1. INSERT na tabela principal (sem as novas colunas)
//    ps.executeUpdate(); → obter o uniqueidentifier gerado
//    Para recuperar o id gerado:
//    ps = conn.prepareStatement("INSERT INTO ... OUTPUT INSERTED.id VALUES (...)");
//    rs = ps.executeQuery(); rs.next(); String uuid = rs.getString("id");

// 2. INSERT na tabela complementar com o mesmo id (uniqueidentifier como String)
String upsert = "MERGE muralha.vtr_imagem_complemento AS t " +
    "USING (SELECT CAST(? AS UNIQUEIDENTIFIER) id_imagem, ? sha256, 'OK' status_integridade) AS s " +
    "ON t.id_imagem = s.id_imagem " +
    "WHEN NOT MATCHED THEN INSERT (id_imagem, sha256, status_integridade) " +
    "VALUES (s.id_imagem, s.sha256, s.status_integridade);";
ps.setString(1, uuidString);  // UUID como String ex: "550e8400-e29b-41d4-a716-446655440000"
ps.setString(2, sha256hex);
```

### SELECTs com JOIN para os novos campos

```sql
-- Ao consultar passagens com status, latência e nome do local/equipamento:
SELECT vtr.id, vtr.placa, vtr.data, vtr.id_local, l.nome AS local_nome,
       vtr.id_pista, vtr.velocidade, vtr.classificacao,
       c.status_analise, c.latencia_ms, c.dt_preclass
FROM muralha.veiculo_tempo_real vtr
LEFT JOIN muralha.vtr_complemento c ON c.id_vtr = vtr.id
LEFT JOIN dbo.local l ON l.id_local = vtr.id_local AND l.sequencia_local = 1

-- Ao consultar imagens com hash e obliteração:
SELECT vtri.*, ic.sha256, ic.status_integridade, ic.obliterada, ic.id_original
FROM muralha.veiculo_tempo_real_imagem vtri
LEFT JOIN muralha.vtr_imagem_complemento ic ON ic.id_imagem = vtri.id

-- Ao consultar usuário com MFA e senha:
SELECT u.*, uc.tentativas_invalidas, uc.bloqueado_ate, uc.totp_habilitado, uc.totp_secret
FROM dbo.sis_usuario u
LEFT JOIN dbo.sis_usuario_complemento uc ON uc.id_usuario = u.id_usuario
```

---

## Planos afetados: seções específicas a ignorar

| Plano | Seção a ignorar | Substituir por |
|---|---|---|
| `02-sha256-integridade.md` | Tarefa 1 (ALTER TABLE vtri) | Script `00b` acima |
| `03-politica-senhas.md` | Tarefa 1 (ALTER TABLE sis_usuario) | Script `00b` acima |
| `04-mfa-totp.md` | Tarefa 1 (ALTER TABLE sis_usuario) | Script `00b` acima |
| `05-dupla-analise.md` | Tarefa 1 (ALTER TABLE vtr) | Script `00b` acima |
| `06-obliteracao-imagens.md` | Tarefa 1 (ALTER TABLE vtri) | Script `00b` acima |
| `07-sla-latencia.md` | Tarefa 1 (ALTER TABLE vtr) | Script `00b` acima |
| `09-cpf-matricula.md` | Tarefa 1 (ALTER TABLE sis_usuario) | Script `00b` acima |
| `10-sla-preprocessamento.md` | Tarefa 1 (ALTER TABLE vtr) | Script `00b` acima |

Nas Tarefas 2+ de cada plano, ao ver `ps.setString(N, sha256)` com `INSERT INTO muralha.veiculo_tempo_real_imagem`, dividir em dois statements: INSERT na tabela principal (sem as novas colunas) + MERGE na tabela complementar correspondente.

Todos os SELECTs que filtram por `status_analise`, `latencia_ms`, `sha256`, `obliterada`, `totp_habilitado`, `cpf`, `matricula` devem usar `LEFT JOIN` com a tabela complementar.
