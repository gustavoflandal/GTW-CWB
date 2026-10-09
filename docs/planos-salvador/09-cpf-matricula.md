# CPF e Matrícula no Cadastro de Usuários — Plano de Implementação

> **Para agentes:** use `superpowers:executing-plans`.

**Goal:** Adicionar campos `cpf` e `matricula` ao cadastro de usuários do sistema, com validação de formato no back-end e persistência em `dbo.sis_usuario`.

**Architecture:** Adicionar colunas `cpf CHAR(11)` e `matricula VARCHAR(20)` em `dbo.sis_usuario`. Criar `UsuarioService.java` com validação de CPF (dígitos verificadores). Adaptar servlet de cadastro/edição de usuários existente e a tela de perfil.

**Tech Stack:** Java 13 · SQL Server · Bootstrap 5.3

## Global Constraints

- CPF armazenado sem pontos/traços (11 dígitos numéricos)
- Matrícula: alfanumérico, max 20 caracteres, único por registro (UNIQUE nullable)
- Validação de CPF no servidor (não apenas no cliente)
- Build: `..\.setup-gtw\build.ps1`

---

## Arquivos

| Ação | Caminho |
|---|---|
| Criar | `docs/banco-de-dados/migracoes/20261008_cpf_matricula.sql` |
| Criar | `src/main/java/muralha/digital/usuario/UsuarioCpfUtil.java` |
| Modificar | Servlet de usuários existente (buscar por `sis_usuario` em servlets) |
| Modificar | JSP de cadastro/edição de usuários |

---

### Tarefa 1: Migração SQL

- [ ] **Criar script**

```sql
-- docs/banco-de-dados/migracoes/20261008_cpf_matricula.sql
-- ROLLBACK:
--   ALTER TABLE dbo.sis_usuario DROP CONSTRAINT uq_usuario_cpf;
--   ALTER TABLE dbo.sis_usuario DROP CONSTRAINT uq_usuario_matricula;
--   ALTER TABLE dbo.sis_usuario DROP COLUMN cpf, matricula;

ALTER TABLE dbo.sis_usuario ADD
    cpf       CHAR(11)     NULL,
    matricula VARCHAR(20)  NULL;

-- CPF único (ignora NULLs — apenas quando preenchido)
ALTER TABLE dbo.sis_usuario
    ADD CONSTRAINT uq_usuario_cpf       UNIQUE (cpf)       WHERE cpf IS NOT NULL;
ALTER TABLE dbo.sis_usuario
    ADD CONSTRAINT uq_usuario_matricula UNIQUE (matricula)  WHERE matricula IS NOT NULL;
```

- [ ] **Executar**

```powershell
sqlcmd -S 10.0.0.200 -d GTW_MURALHA_DEV -i "docs\banco-de-dados\migracoes\20261008_cpf_matricula.sql"
```

- [ ] **Commit**

```bash
git add docs/banco-de-dados/migracoes/20261008_cpf_matricula.sql
git commit -m "Adiciona colunas cpf e matricula em sis_usuario com constraint unique

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 2: UsuarioCpfUtil

- [ ] **Criar `UsuarioCpfUtil.java`**

```java
// src/main/java/muralha/digital/usuario/UsuarioCpfUtil.java
package muralha.digital.usuario;

public final class UsuarioCpfUtil {

    private UsuarioCpfUtil() {}

    /**
     * Remove formatação e valida dígitos verificadores.
     * @param cpf CPF com ou sem pontos/traços
     * @return CPF apenas com 11 dígitos, ou null se inválido
     */
    public static String normalizar(String cpf) {
        if (cpf == null) return null;
        String nums = cpf.replaceAll("[^0-9]", "");
        if (nums.length() != 11) return null;
        // Rejeitar sequências iguais (000...000, 111...111 etc.)
        if (nums.chars().distinct().count() == 1) return null;
        if (!validarDigitos(nums)) return null;
        return nums;
    }

    private static boolean validarDigitos(String cpf) {
        int sum = 0;
        for (int i = 0; i < 9; i++) sum += (cpf.charAt(i) - '0') * (10 - i);
        int d1 = 11 - (sum % 11); if (d1 >= 10) d1 = 0;
        if (d1 != (cpf.charAt(9) - '0')) return false;

        sum = 0;
        for (int i = 0; i < 10; i++) sum += (cpf.charAt(i) - '0') * (11 - i);
        int d2 = 11 - (sum % 11); if (d2 >= 10) d2 = 0;
        return d2 == (cpf.charAt(10) - '0');
    }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/muralha/digital/usuario/UsuarioCpfUtil.java
git commit -m "Adiciona UsuarioCpfUtil com validação de dígitos verificadores

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 3: Integrar no servlet de usuários

- [ ] **Localizar servlet de cadastro de usuários**

```bash
grep -r "sis_usuario" src/main/java --include="*.java" -l
grep -r "INSERT.*sis_usuario\|UPDATE.*sis_usuario" src/main/java --include="*.java" -l
```

- [ ] **Adicionar tratamento de CPF e matrícula nos métodos de salvar**

```java
// No método que processa cadastro/edição de usuário:
import muralha.digital.usuario.UsuarioCpfUtil;

// Obter parâmetros
String cpfRaw     = req.getParameter("cpf");
String matricula  = req.getParameter("matricula");

// Validar e normalizar CPF
String cpf = null;
if (cpfRaw != null && !cpfRaw.trim().isEmpty()) {
    cpf = UsuarioCpfUtil.normalizar(cpfRaw.trim());
    if (cpf == null) {
        // retornar erro ao cliente
        // Ex (JSON): {"ok":false,"erro":"CPF inválido"}
        // ou redirecionar com mensagem de erro conforme padrão da tela
        resp.getWriter().print("{\"ok\":false,\"erro\":\"CPF inválido\"}");
        return;
    }
}

// Validar matrícula
if (matricula != null) {
    matricula = matricula.trim();
    if (matricula.isEmpty()) matricula = null;
    else if (matricula.length() > 20) {
        resp.getWriter().print("{\"ok\":false,\"erro\":\"Matrícula deve ter no máximo 20 caracteres\"}");
        return;
    }
}

// No INSERT ou UPDATE, incluir:
// ps.setString(N, cpf);       // null se não informado
// ps.setString(N+1, matricula);
```

- [ ] **Tratar SQLException de violação de constraint UNIQUE**

```java
// Ao executar INSERT/UPDATE, capturar:
catch (SQLException e) {
    if (e.getMessage().contains("uq_usuario_cpf")) {
        // retornar: "CPF já cadastrado para outro usuário"
    } else if (e.getMessage().contains("uq_usuario_matricula")) {
        // retornar: "Matrícula já cadastrada para outro usuário"
    } else {
        throw e;
    }
}
```

- [ ] **Build**

```powershell
..\.setup-gtw\build.ps1
```

- [ ] **Commit**

```bash
git add src/main/java/
git commit -m "Integra CPF e matrícula no servlet de cadastro de usuários

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```

---

### Tarefa 4: Campos na JSP de cadastro

- [ ] **Adicionar campos na tela de cadastro/edição de usuário**

```html
<!-- Adicionar no formulário de cadastro de usuário, após o campo login: -->
<div class="mb-3">
  <label for="cpf" class="form-label">CPF</label>
  <input type="text" class="form-control" id="cpf" name="cpf"
         maxlength="14" placeholder="000.000.000-00"
         value="${usuario.cpf != null ? usuario.cpf : ''}">
  <div class="form-text">Apenas números; pontuação é opcional.</div>
</div>
<div class="mb-3">
  <label for="matricula" class="form-label">Matrícula</label>
  <input type="text" class="form-control" id="matricula" name="matricula"
         maxlength="20"
         value="${usuario.matricula != null ? usuario.matricula : ''}">
</div>
```

- [ ] **Build e teste**

```powershell
..\.setup-gtw\build.ps1
..\.setup-gtw\run.ps1
```

1. Acessar tela de cadastro/edição de usuário
2. Informar CPF válido (ex.: 529.982.247-25) → salvar → verificar no banco:

```sql
SELECT id, login, cpf, matricula FROM dbo.sis_usuario ORDER BY id DESC;
```

3. Tentar salvar CPF inválido → verificar mensagem de erro
4. Tentar duplicar CPF → verificar mensagem de unicidade

- [ ] **Commit**

```bash
git add src/main/webapp/
git commit -m "Adiciona campos CPF e matrícula no formulário de cadastro de usuários

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>"
```
