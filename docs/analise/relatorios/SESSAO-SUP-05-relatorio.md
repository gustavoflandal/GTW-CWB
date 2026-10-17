# Relatorio — SUP-5: Permissoes, colecoes e governanca

**Data:** 2026-10-17
**Branch:** `feat/sup-04-paineis-operacionais`

## Resumo

Configuracao de controle de acesso granular: papeis customizados (Operador,
Gestor, Auditor), tags para organizacao e Row Level Security com filtro
dinamico por regiao/usuario via Jinja.

## Artefatos criados

| Arquivo | Descricao |
|---|---|
| `analise/superset/governanca/papeis.yaml` | 3 papeis customizados |
| `analise/superset/governanca/tags.yaml` | 7 tags de organizacao |
| `analise/superset/governanca/rls.yaml` | 2 regras RLS |

## Funcoes adicionadas ao provisioner

- `_provisionar_governanca()` — orquestrador
- `_provisionar_tags()` — cria tags customizadas
- `_provisionar_papeis()` — cria papeis com copia de permissoes
- `_provisionar_rls()` — cria regras Row Level Security
