# Prompt de execução: Motor de Captura de Logs de Operações

> **Como usar:** preencha a seção "Parâmetros do projeto", coloque o arquivo `motor-captura-logs.md` em `docs/` no repositório e entregue este prompt inteiro a um agente de código (Claude Code ou equivalente).

---

## Papel

Você é um engenheiro de software sênior responsável por implementar, do zero, um **motor de captura de logs de operações** para sistemas web. Você escreve código de produção: testado, documentado, observável e seguro. Trabalhe em português do Brasil em toda a documentação, nomes de tabelas e mensagens para o usuário. Nomes de pacotes, funções e variáveis podem ficar em inglês, seguindo a convenção da linguagem.

## Documento de referência

Leia **inteiro** o arquivo `docs/motor-captura-logs.md` antes de escrever qualquer código. Ele é a especificação da arquitetura. Em caso de conflito entre este prompt e o documento, **este prompt prevalece**. Se encontrar uma contradição ou lacuna relevante, pare e pergunte.

## Parâmetros do projeto

Preencha antes de executar. Se algum valor ficar como `{{...}}`, **pergunte antes de começar**.

| Parâmetro | Valor |
|---|---|
| Linguagem/framework do sistema que será auditado | `{{ex.: Java 21 + Spring Boot 3 / Node 22 + NestJS / Python 3.12 + Django / Go 1.23 / .NET 8}}` |
| ORM do sistema | `{{ex.: Hibernate / Prisma / TypeORM / Django ORM / GORM / EF Core}}` |
| Banco de dados do sistema | PostgreSQL 16 |
| Sistema multi-tenant? | `{{sim / não}}` |
| Volume estimado | `{{ex.: 500 mil eventos/dia}}` |
| Linguagem do worker e da API do motor | Go 1.23 |
| Front-end da tela de configuração | React 18 + TypeScript + Vite |
| Autenticação da tela | `{{ex.: OIDC com o provedor da empresa / JWT emitido pelo sistema}}` |

## Escopo desta execução

Implementar a **Fase 1** do documento (seção 13): tudo no PostgreSQL do sistema, schema `audit`, com outbox e worker. Broker, CDC, ClickHouse e OpenSearch **ficam fora**, mas o código deve deixar o destino do worker atrás de uma interface (`EventSink`), para que a Fase 2 seja só uma nova implementação.

## Estrutura do repositório

```
motor-captura/
├── docs/
│   ├── motor-captura-logs.md        # especificação (fornecida)
│   ├── adr/                         # decisões tomadas durante a implementação
│   └── operacao.md                  # runbook: instalar, configurar, monitorar, recuperar
├── migrations/                      # SQL versionado (golang-migrate)
├── sdk/<linguagem-do-sistema>/      # biblioteca embarcada no sistema auditado
├── worker/                          # Go: outbox → evento, hash, selo, partições, retenção
├── api/                             # Go: API de configuração e consulta
├── ui/                              # React: tela de configuração
├── demo/                            # aplicação de exemplo instrumentada com o SDK
├── loadtest/                        # scripts k6
└── docker-compose.yml               # postgres + worker + api + ui + demo + prometheus
```

## Componentes e requisitos

### 1. Migrations (`migrations/`)

- Criar o schema `audit` e as 6 tabelas da seção 9 do documento, com os índices, o trigger de `NOTIFY` e as permissões da seção 9.7.
- Criar os papéis `app_user`, `audit_worker`, `audit_admin`, `auditor` e `audit_owner`. O `audit_owner` é dono das tabelas e o único que cria e remove partições.
- Criar a partição do mês atual e dos 2 meses seguintes, e uma partição `DEFAULT` para nunca perder evento por falta de partição.
- Inserir a versão 1 das regras com as **regras travadas**: login, logout, falha de login, alteração de permissões, exclusão de qualquer entidade e alteração de regras do motor.
- Toda migration tem `up` e `down`.

### 2. SDK (`sdk/<linguagem>/`)

A biblioteca que o sistema auditado importa. Requisitos:

- **API pública mínima:**
  - `Emit(evento)` para eventos operacionais: assíncrono, nunca bloqueia, nunca lança exceção para o chamador.
  - `EmitInTx(tx, evento)` para eventos de auditoria: insere na `audit.outbox` usando a transação do negócio.
  - Middleware HTTP para o framework informado.
  - Hook do ORM informado que gera `alteracoes` no formato `{"campo": [anterior, novo]}`, só para campos rastreados pelas regras.
- **Buffer** com capacidade fixa e envio em lote (tamanho e intervalo vindos das regras) para `audit.outbox`, usando inserção multi-linha ou `COPY`, numa conexão separada da do negócio.
- **Política de buffer cheio** configurável: `drop`, `spool` ou `block`. O spool usa arquivo local com append e fsync em lote (Pebble ou RocksDB se a linguagem for Go/Rust/Java; arquivo segmentado nas demais).
- **Mascaramento na origem**, antes de o evento entrar no buffer: estratégias `hash` (HMAC-SHA256 com chave configurável), `redact` e `partial`.
- **Carregamento de regras** na inicialização e recarga via `LISTEN regras_alteradas`, com reconexão automática. Se o banco estiver fora no início, usa a última cópia de regras gravada em disco.
- **Contexto:** propaga `trace_id` (W3C Trace Context / OpenTelemetry), usuário, tenant e IP. Dentro da transação, executa `SET LOCAL app.user_id` e `SET LOCAL app.trace_id`.
- **Sinal de vida:** a cada 15 s, faz upsert em `audit.agente` com a versão de regras aplicada e as métricas.
- **Métricas** Prometheus: eventos emitidos, enviados, descartados (por categoria), tamanho do buffer, tamanho do spool, latência do envio em lote.
- **Fail-open:** qualquer falha do SDK em eventos operacionais é registrada em log e métrica, nunca propagada. Eventos de auditoria seguem a transação: se a inserção na outbox falhar, a transação falha.

### 3. Worker (`worker/`, Go)

- Lê a outbox em lotes com `SELECT ... FOR UPDATE SKIP LOCKED`, agrupa por tenant e, para cada tenant, obtém `pg_advisory_xact_lock(hashtext(tenant))` antes de calcular a cadeia.
- Calcula `hash = SHA-256(hash_anterior || JSON canônico RFC 8785 do evento sem os campos de hash)`. O último hash de cada tenant é lido do banco no início do lote, nunca de cache não confiável.
- Insere em `audit.evento` através da interface `EventSink` e apaga da outbox **na mesma transação**.
- Idempotência: se o mesmo `id` de evento chegar duas vezes, o segundo é ignorado.
- Tarefas agendadas (com lock para rodar em uma instância só):
  - criar partições com 2 meses de antecedência;
  - aplicar retenção removendo partições inteiras (`DROP TABLE`), nunca `DELETE`;
  - gerar o selo diário por tenant, assinado com Ed25519. A chave privada vem de variável de ambiente ou arquivo, **nunca do banco**;
  - verificar a cadeia de hashes do dia anterior e gerar alerta em caso de quebra.
- Vários workers podem rodar ao mesmo tempo sem duplicar nem quebrar a cadeia.
- Métricas Prometheus: tamanho da outbox, atraso (idade do evento mais antigo), eventos movidos por segundo, quebras de cadeia.

### 4. API (`api/`, Go)

REST com OpenAPI 3 gerado e publicado em `/docs`.

- **Regras:** listar, criar rascunho, editar rascunho, enviar para aprovação, aprovar, rejeitar, publicar, reverter para uma versão anterior, exportar e importar em YAML.
  - O aprovador tem de ser diferente do autor (já garantido pelo `CHECK` no banco; valide também na API).
  - Aprovação é obrigatória quando a mudança **reduz** captura, amostragem ou retenção. Aumentos podem ser publicados direto.
  - Regras `travada = true` não podem ser desativadas nem ter o escopo reduzido. Retorne erro claro.
- **Simulação:** recebe um rascunho e aplica as regras sobre os últimos N eventos (padrão 5.000), devolvendo o que seria capturado, descartado e mascarado.
- **Descoberta de entidades:** lista tabelas e colunas do sistema via `information_schema`, sugerindo mascaramento para nomes como `cpf`, `senha`, `email`, `cartao`, `token`, `telefone`.
- **Agentes:** lista com status (ativo se o último sinal tiver menos de 60 s), versão de regras e métricas.
- **Consulta de eventos:** filtros por período (obrigatório), tenant, usuário, ação, recurso, resultado e trace_id; paginação por cursor (`ts`, `id`), nunca por `OFFSET`.
- **Verificação de integridade:** recalcula a cadeia de um tenant num período e compara com o selo.
- **Toda alteração feita pela API gera um evento de auditoria** (`motor.regra.*`) com autor, valor anterior e valor novo.
- Autorização por papel: `admin_motor`, `aprovador_motor`, `auditor`.

### 5. Tela de configuração (`ui/`, React)

Seções conforme a seção 10 do documento: Painel geral, Captura HTTP, Entidades e campos, Dados sensíveis, Retenção, Desempenho, Alertas, Consulta de eventos, Versões e aprovações.

- Toda edição acontece num **rascunho**. A tela mostra o diff entre o rascunho e a versão publicada antes de enviar.
- Botão **Simular** antes de enviar para aprovação.
- Regras travadas aparecem com cadeado e controles desabilitados.
- Painel com atualização a cada 10 s: eventos/s, descartes, atraso da outbox, agentes e a versão de regras de cada um (destacar agentes em versão antiga).
- Responsiva, acessível (WCAG 2.1 AA), tema claro e escuro.

### 6. Aplicação de demonstração (`demo/`)

Uma API pequena, na linguagem do sistema alvo, com as entidades `Pedido`, `Cliente` e `Produto`, login e um job agendado, instrumentada com o SDK. Serve para validar o fluxo completo e para os testes de carga.

## Requisitos não funcionais (critérios de aceite)

| Requisito | Meta |
|---|---|
| Custo do `Emit` no caminho da requisição | p99 < 50 µs |
| Custo do `EmitInTx` (inserção na outbox) | p99 < 2 ms |
| Sobrecarga total do middleware por requisição | p99 < 1 ms |
| Vazão sustentada do worker | ≥ 5.000 eventos/s numa instância com PostgreSQL local |
| Atraso da outbox em carga nominal | < 5 s |
| Perda de eventos de auditoria | Zero, inclusive com worker parado ou reiniciado no meio do lote |
| Banco fora do ar | O sistema demo continua respondendo; eventos operacionais vão para o spool e são enviados quando o banco volta |
| Propagação de nova regra | Agentes aplicam em < 5 s após a publicação |

## Testes obrigatórios

- **Unitários:** mascaramento, avaliação de regras, JSON canônico, cálculo de hash, diff de alterações.
- **Integração** com Testcontainers (PostgreSQL real): migrations up/down, outbox → evento, concorrência de vários workers sem quebrar a cadeia, permissões (o `app_user` **não** consegue `UPDATE`/`DELETE` em `audit.evento`), `LISTEN/NOTIFY`.
- **Carga** com k6 em `loadtest/`, medindo as metas da tabela acima. Registre os resultados em `docs/resultados-carga.md`.
- **Falhas:** derrubar o PostgreSQL durante a carga, matar o worker no meio de um lote, encher o buffer. Verificar que não há perda de auditoria e que o spool é reenviado.
- **Integridade:** alterar manualmente um evento (com o papel dono) e confirmar que a verificação detecta a quebra.

## Restrições

- Nenhum segredo no código ou nos arquivos de configuração versionados. Use variáveis de ambiente e um `.env.example`.
- Nenhum papel da aplicação, do worker ou da API tem `UPDATE` ou `DELETE` em `audit.evento`.
- Não use `DELETE` para retenção. Não use paginação por `OFFSET`.
- Não adicione dependências pesadas sem registrar o motivo num ADR em `docs/adr/`.
- Dados sensíveis nunca aparecem em logs do próprio motor.

## Forma de trabalho

1. **Fase 0 — Plano.** Confirme os parâmetros, liste dúvidas e apresente um plano curto com a ordem de implementação. Aguarde aprovação.
2. **Fase 1 — Migrations** e testes de integração do banco.
3. **Fase 2 — SDK** e aplicação demo.
4. **Fase 3 — Worker.**
5. **Fase 4 — API.**
6. **Fase 5 — Tela de configuração.**
7. **Fase 6 — Carga, falhas, `docs/operacao.md` e README.**

Ao final de cada fase:
- rode todos os testes e mostre o resultado real (não resuma como "passou" sem a saída);
- faça um commit com mensagem descritiva;
- informe o que foi feito, o que ficou pendente e qualquer decisão tomada que não estava na especificação.

Se uma meta de desempenho não for atingida, **não ajuste a meta**: informe o número medido, a causa provável e as opções.

## Entregáveis finais

- Código das 6 partes, com testes passando.
- `docker-compose up` sobe tudo e a demo gera eventos visíveis na tela em menos de 2 minutos.
- `README.md` com arquitetura resumida, como rodar, como integrar o SDK num sistema existente (passo a passo) e como operar.
- `docs/operacao.md` com runbook: instalação, rotação da chave de assinatura, recuperação de spool, verificação de integridade, migração para a Fase 2.
- `docs/resultados-carga.md` com os números medidos.
