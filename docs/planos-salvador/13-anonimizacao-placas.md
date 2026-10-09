# Anonimização de Placas (View LGPD) — Plano de Implementação

> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Criar view SQL que mascara os últimos 3 dígitos das placas (`ABC-****`) para acesso por perfis sem permissão de ver placa completa; implementar controle de acesso por perfil no servlet de consulta de passagens.

**Architecture:** View `muralha.vw_passagem_anonimizada` substitui dígitos finais por `*`. Servlet verifica permissão `VER_PLACA_COMPLETA` na sessão; se ausente, usa a view anonimizada. Sem alteração nas tabelas originais.

**Tech Stack:** SQL Server (VIEW) · Java Servlet · Bootstrap 5.3

## Global Constraints

- View não altera dados originais — somente leitura
- Permissão `VER_PLACA_COMPLETA` verificada via mecanismo existente (`funcoes` do usuário na sessão)
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_anonimizacao_placas.sql` |
| Modificar | Servlet de consulta de passagens (buscar por `veiculo_tempo_real` em servlets) |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_anonimizacao_placas.sql
-- ROLLBACK: DROP VIEW muralha.vw_passagem_anonimizada;

CREATE OR ALTER VIEW muralha.vw_passagem_anonimizada AS
SELECT
    id,
    -- Mascarar últimos 3 caracteres da placa: ABC1D23 → ABC-****
    CASE
        WHEN placa IS NULL THEN NULL
        WHEN LEN(placa) >= 4
            THEN LEFT(placa, LEN(placa) - 3) + '****'
        ELSE '****'
    END AS placa,
    equipamento,
    dt_captura_equipamento,
    dt_recepcao_servidor,
    status_analise,
    latencia_ms,
    obliterada,
    id_original
FROM muralha.veiculo_tempo_real;

-- Permissão no sistema (inserir se não existir)
IF NOT EXISTS (SELECT 1 FROM dbo.sis_funcao WHERE funcao = 'VER_PLACA_COMPLETA')
    INSERT INTO dbo.sis_funcao (funcao, descricao)
    VALUES ('VER_PLACA_COMPLETA', 'Permite visualizar a placa completa nas consultas de passagens');
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_anonimizacao_placas.sql"
```

- [ ] **Verificar**

```sql
-- Testar mascaramento:
SELECT TOP 5 placa, equipamento FROM muralha.vw_passagem_anonimizada;
-- Esperado: placas com últimos 3 caracteres substituídos por ****
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_anonimizacao_placas.sql
git commit -m "Cria view de passagens com placa anonimizada e permissão VER_PLACA_COMPLETA

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: Integrar verificação de permissão no servlet

- [ ] **Localizar servlet que consulta passagens/infrações**

```bash
grep -r "veiculo_tempo_real\b" src/main/java --include="*.java" -l
```

- [ ] **Adicionar verificação de permissão e seleção condicional de fonte de dados**

```java
// No método que consulta passagens, antes de montar a query:

boolean podeVerPlacaCompleta = usuarioPossuiFuncao(req, "VER_PLACA_COMPLETA");
String fonte = podeVerPlacaCompleta
    ? "muralha.veiculo_tempo_real"
    : "muralha.vw_passagem_anonimizada";

// Substituir na query:
// SELECT id, placa, equipamento, ... FROM muralha.veiculo_tempo_real WHERE ...
// por:
// SELECT id, placa, equipamento, ... FROM " + fonte + " WHERE ...
```

- [ ] **Criar método auxiliar de verificação de função (se não existir)**

```java
// No servlet ou em classe de utilidade de sessão:
private boolean usuarioPossuiFuncao(HttpServletRequest req, String funcao) {
    try {
        Object usuario = req.getSession().getAttribute("usuario");
        if (usuario == null) return false;
        // O objeto usuario tem método getFuncoes() que retorna Collection<String>
        // Adaptar conforme o tipo real do objeto na sessão do projeto
        Object funcoes = usuario.getClass().getMethod("getFuncoes").invoke(usuario);
        if (funcoes instanceof java.util.Collection) {
            return ((java.util.Collection<?>) funcoes).stream()
                .anyMatch(f -> funcao.equals(f.toString()));
        }
    } catch (Exception ignored) {}
    return false;
}
```

> **Nota:** Se o projeto usa outro mecanismo para verificar funções (ex.: `Acesso.temFuncao(req, "VER_PLACA_COMPLETA")`), adaptar a chamada para seguir o padrão existente.

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Login com usuário **sem** a função `VER_PLACA_COMPLETA`
2. Acessar consulta de passagens → placas devem aparecer anonimizadas (`ABC-****`)
3. Login com usuário **com** a função `VER_PLACA_COMPLETA`
4. Mesma consulta → placas completas visíveis

- [ ] **Commit**

```bash
git add src/main/java/
git commit -m "Aplica anonimização de placa por perfil de acesso usando view LGPD

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
