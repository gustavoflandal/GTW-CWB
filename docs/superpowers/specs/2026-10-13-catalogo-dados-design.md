# Spec: Tela de Gerenciamento do Catálogo de Dados

**Data:** 2026-10-13
**Módulo:** Construtor de Dashboards (Plano 20)
**Status:** Aprovado

## Objetivo

Criar uma interface web para gerenciar o catálogo de tabelas, colunas e relações do Construtor de Dashboards. O catálogo alimenta o editor de datasets e a geração de modelos Cube. Hoje só é acessível via API; esta tela expõe o CRUD para usuários avançados criadores de dashboard.

## Decisões de design

- **Público-alvo:** usuários avançados que criam dashboards (não só admins técnicos)
- **Fluxo de cadastro:** somente manual (sem auto-discovery do banco)
- **Layout:** página única com 3 abas Bootstrap + offcanvas lateral para formulários
- **Padrão visual:** mesmo das telas existentes do módulo (KPIs, datasets) — Bootstrap 5.3, Bootstrap Icons, `cabecalho_bootstrap_simples.jsp`

## Arquivos

### Novo

| Arquivo | Descrição |
|---|---|
| `src/main/webapp/muralha-digital/pages/analises/catalogo/index.jsp` | Página com 3 abas (Tabelas, Colunas, Relações) + offcanvas + JS inline |

### Alterado

| Arquivo | Alteração |
|---|---|
| `src/main/java/muralha/digital/dashboards/DashCatalogoServlet.java` | Adicionar ações `salvarColuna` e `excluirRelacao` no `doPost` |

### Sem alteração

- Nenhuma tabela nova no banco (schema `dash` já possui `catalogo_tabela`, `catalogo_coluna`, `catalogo_relacao`)
- Nenhuma migração SQL
- Nenhuma dependência nova no `pom.xml`
- `api.js` reutilizado sem modificação

## Layout da JSP

### Cabeçalho

- Breadcrumb: `Análises > Catálogo de Dados`
- Título: ícone `bi-journal-code` + "Catálogo de Dados"

### Aba 1 — Tabelas

Tabela com colunas:

| Rótulo | Schema.Nome | Chave Primária | Sensível | Ativo | Ações |
|---|---|---|---|---|---|
| Passagens de Veículos | `muralha.veiculo_tempo_real` | id | Não | Sim | Editar, Desativar |

- Badge colorido por schema (`dbo` cinza, `muralha` azul)
- Botão "Nova Tabela" no topo abre offcanvas
- Botão toggle Ativar/Desativar na coluna ações

### Aba 2 — Colunas

- Dropdown no topo para selecionar tabela (filtra colunas)
- Tabela:

| Rótulo | Nome Físico | Tipo | Papel Sugerido | Sensível | Ações |
|---|---|---|---|---|---|
| Velocidade | velocidade | numerico | medida | Não | Editar |

- Badge colorido por papel: `dimensão` azul, `medida` verde, `tempo` âmbar
- Botão "Nova Coluna" (visível só com tabela selecionada)

### Aba 3 — Relações

- Tabela:

| Origem | → | Destino | Colunas | Cardinalidade | Ações |
|---|---|---|---|---|---|
| Passagens | → | Equipamentos | equipamento_id = id | many_to_one | Excluir |

- Botão "Nova Relação"

### Offcanvas (reutilizado)

Um único `offcanvas-end` (500px) com conteúdo dinâmico:

**Formulário Tabela:**
- Nome Físico (input text, obrigatório)
- Schema (select: `dbo`, `muralha`)
- Rótulo (input text, obrigatório)
- Descrição (textarea, opcional)
- Chave Primária (input text, default `id`)
- Sensível (checkbox)

**Formulário Coluna:**
- Nome Físico (input text, obrigatório)
- Rótulo (input text, obrigatório)
- Tipo (select: `texto`, `numerico`, `inteiro`, `data`, `data_hora`, `booleano`)
- Papel Sugerido (select: `dimensao`, `medida`, `tempo`, `geo`, nenhum)
- Sensível (checkbox)

**Formulário Relação:**
- Tabela Origem (select das tabelas do catálogo)
- Tabela Destino (select das tabelas do catálogo)
- Coluna Origem (input text)
- Coluna Destino (input text)
- Cardinalidade (select: `one_to_one`, `one_to_many`, `many_to_one`)

## Backend — novas ações

### `salvarColuna` (POST /api/dash/catalogo)

```json
{
  "acao": "salvarColuna",
  "id": 0,
  "idTabela": 5,
  "nomeFisico": "velocidade",
  "rotulo": "Velocidade",
  "tipo": "numerico",
  "papelSugerido": "medida",
  "sensivel": false
}
```

- `id == 0` → INSERT em `dash.catalogo_coluna`
- `id > 0` → UPDATE em `dash.catalogo_coluna WHERE id = ?`
- Validação: `nomeFisico` e `rotulo` obrigatórios, `tipo` deve ser um dos valores aceitos

### `excluirRelacao` (POST /api/dash/catalogo)

```json
{
  "acao": "excluirRelacao",
  "id": 7
}
```

- Soft delete: `UPDATE dash.catalogo_relacao SET ativo = 0 WHERE id = ?`

## Fluxo do usuário

1. Abre `analises/catalogo/index.jsp`
2. **Aba Tabelas**: vê lista, clica "Nova Tabela" → offcanvas → preenche → Salvar → tabela aparece na lista
3. **Aba Colunas**: seleciona tabela no dropdown → vê colunas → clica "Nova Coluna" → offcanvas → preenche → Salvar
4. **Aba Relações**: vê relações → clica "Nova Relação" → offcanvas → seleciona origem/destino/colunas → Salvar
5. Volta ao editor de datasets → a tabela já aparece no dropdown de tabela base

## Fora do escopo

- Importação automática de tabelas do `INFORMATION_SCHEMA` (decisão do usuário)
- Edição em massa de colunas
- Validação de existência real da tabela/coluna no banco
- Permissões granulares (usa o mesmo `Acesso.verificaAcesso` das demais telas do módulo)
