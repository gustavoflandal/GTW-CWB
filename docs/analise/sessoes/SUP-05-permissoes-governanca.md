# SUP-5 — Permissoes, colecoes e governanca

## Escopo

Configurar o controle de acesso granular do Superset (papéis, RLS, tags)
e integrar com os grupos do GTW. Definições declarativas em YAML para
reprodutibilidade.

## Artefatos

### Papéis customizados (governanca/papeis.yaml)

| Papel | Base | Descrição |
|---|---|---|
| Operador | Gamma | Visualiza dashboards, sem criação |
| Gestor | Alpha | Cria gráficos e explora dados, sem SQL Lab |
| Auditor | Gamma | Somente leitura + acesso ao log de ações |

### Tags (governanca/tags.yaml)

| Tag | Uso |
|---|---|
| oficial | Dashboards homologados pela Consilux |
| operacional | Acompanhamento em tempo real |
| sla | Indicadores de SLA e disponibilidade |
| fiscalizacao | Infrações, passagens, pré-processamento |
| manutencao | Equipamentos e alertas técnicos |
| georreferenciado | Mapas e análise espacial |
| administrativo | Conteúdo restrito à administração |

### Row Level Security (governanca/rls.yaml)

| Regra | Clausula | Papéis |
|---|---|---|
| Restringir por região | WHERE area_monitorada_id IN (subselect por usuário) | Operador |
| Auditores veem tudo | 1=1 (documentação) | Auditor, Admin |

### Configuração

- `FEATURE_FLAGS.TAGGING_SYSTEM = True` em superset_config.py
- `JINJA_CONTEXT_ADDONS` com `current_username()` para RLS dinâmico
- Provisioner estendido: `_provisionar_governanca`, `_provisionar_tags`, `_provisionar_papeis`, `_provisionar_rls`

## Tarefas

- [x] Criar YAMLs de papéis, tags e RLS em `analise/superset/governanca/`
- [x] Habilitar TAGGING_SYSTEM no superset_config.py
- [x] Configurar JINJA_CONTEXT_ADDONS para RLS
- [x] Implementar funções de provisionamento no provisionar.py
- [x] Atualizar Dockerfile para copiar `governanca/`
- [x] Build verde
- [ ] Testar criação de papéis e RLS no Docker
- [ ] Validar que Operador não vê dados de outra região

## Critérios de aceite

| Critério | Status |
|---|---|
| Papéis customizados declarativos | Concluído |
| Tags para organização de dashboards | Concluído |
| RLS com filtro por região/usuário | Concluído |
| Jinja context para current_username() | Concluído |
| Provisioner idempotente | Concluído |
| Teste de isolamento por região | Pendente — requer Docker |
