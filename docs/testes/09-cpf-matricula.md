# Testes — Plano 09: CPF e Matrícula

Data: 2026-10-08

## Pré-requisitos

- Migração `20261008_tabelas_complementares.sql` executada no banco GTW_MURALHA_DEV
- Servidor rodando em `http://localhost:8080/`
- Usuário autenticado com permissão de administrador

---

## T01 — Tela carrega com lista de usuários

**Procedimento:**
1. Acessar `http://localhost:8080/muralha-digital/pages/admin/cpf-matricula/index.jsp`
2. Verificar que a tabela exibe usuários ativos com colunas: Usuário, Nome, CPF, Matrícula, Ação

**Resultado esperado:** Tabela preenchida sem erro 500. Usuários inativos não aparecem.

**Status:** ⏳ Aguarda teste manual

---

## T02 — Endpoint GET retorna dados

**Procedimento:**
```
GET /MuralhaDigital/UsuarioCpfMatricula
```

**Resultado esperado:** JSON `{"ok":true,"usuarios":[{"idUsuario":N,"usuario":"...","nome":"...","cpf":"","matricula":""},...]}`

**Status:** ⏳ Aguarda teste manual

---

## T03 — Salvar CPF válido

**Procedimento:**
1. Clicar no botão editar de um usuário
2. Digitar CPF válido: `529.982.247-25`
3. Clicar Salvar

**Resultado esperado:** Modal fecha, SweetAlert de sucesso, tabela atualiza com CPF formatado `529.982.247-25`.

**Status:** ⏳ Aguarda teste manual

---

## T04 — CPF inválido é rejeitado

**Procedimento:**
1. Editar um usuário
2. Digitar CPF inválido: `000.000.000-00` ou `123.456.789-00`
3. Clicar Salvar

**Resultado esperado:** SweetAlert com mensagem "CPF inválido". Dado não é salvo.

**Status:** ⏳ Aguarda teste manual

---

## T05 — CPF duplicado é rejeitado

**Procedimento:**
1. Salvar CPF `529.982.247-25` para o usuário A
2. Tentar salvar o mesmo CPF para o usuário B

**Resultado esperado:** SweetAlert com mensagem "CPF já cadastrado para outro usuário".

**Status:** ⏳ Aguarda teste manual

---

## T06 — Salvar matrícula

**Procedimento:**
1. Editar um usuário
2. Digitar matrícula: `MAT-2026-001`
3. Clicar Salvar

**Resultado esperado:** Matrícula salva e exibida na tabela.

**Status:** ⏳ Aguarda teste manual

---

## T07 — Matrícula duplicada é rejeitada

**Procedimento:**
1. Salvar matrícula `MAT-2026-001` para o usuário A
2. Tentar salvar a mesma matrícula para o usuário B

**Resultado esperado:** SweetAlert com mensagem "Matrícula já cadastrada para outro usuário".

**Status:** ⏳ Aguarda teste manual

---

## T08 — Limpar CPF e matrícula

**Procedimento:**
1. Editar um usuário que já tem CPF e matrícula
2. Apagar ambos os campos (deixar em branco)
3. Clicar Salvar

**Resultado esperado:** Campos ficam vazios na tabela. No banco, valores são NULL.

**Status:** ⏳ Aguarda teste manual

---

## T09 — Matrícula excede 20 caracteres

**Procedimento:**
1. Editar um usuário
2. Digitar matrícula com mais de 20 caracteres

**Resultado esperado:** Campo `maxlength="20"` impede digitação além de 20 chars. Se bypassado, servidor retorna erro.

**Status:** ⏳ Aguarda teste manual
