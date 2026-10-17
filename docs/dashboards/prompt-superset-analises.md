# Prompt — Projeto Análises: Apache Superset como plataforma de dashboards do GTW-CWB (sessões SUP-1 a SUP-14)

> Cole este prompt numa sessão do Claude Code aberta na raiz do repositório (`D:\GTW-CWB`).
> Execute **uma sessão SUP por vez**. Ao terminar cada sessão, pare, apresente o relatório e aguarde aprovação antes da próxima.

---

## Contexto

Decisão do dono do projeto (16/10/2026): o **Apache Superset 6.0** passa a ser a plataforma de criação de dashboards e exploração self-service do GTW-CWB / Muralha Digital, substituindo o Dashboard Builder próprio (`muralha.digital.dashboards`, schema `dash`, Planos 20 e 21, Entrega 21). A prova de conceito em `poc-superset/` (compose, login único pelo GTW, 4 datasets, tema, benchmark) foi aprovada e é o ponto de partida.

Motivos registrados: o Superset entrega pronto o que o builder ainda precisaria de ~13 sessões para alcançar (catálogo semântico, construtor sem SQL, coleções/tags, filtros nativos, cross-filter, drill, alertas e relatórios). O motor de consulta do builder (`MontadorSql`, pré-agregação de `veiculo_tempo_real`) era sua parte mais madura; o que valia dele é preservado como **views e tabelas materializadas no SQL Server**, consumidas pelo Superset.

O que o GTW continua fazendo sem o Superset: telas operacionais de tempo real que dependem do WebSocket do GTW (alertas, sala de operações, KPIs do Plano 15 enquanto não migrados), autenticação, permissões de origem (`sis_grupo`) e auditoria central (`sis_log_auditoria`).

Antes de qualquer alteração, leia nesta ordem: `CLAUDE.md`, `docs/agentes/AGENTS.md`, `docs/requisitos/05-padrao-visual.md`, `docs/requisitos/06-padroes-de-codigo.md`, `docs/planos-salvador/00c-errata-correcoes.md`, `poc-superset/README.md`, `poc-superset/superset/*.py`, `docs/planos-salvador/20-dashboard-builder.md`, `docs/planos-salvador/21-dashboards-consulta-sql-sem-cube.md`, `docs/entregas/21-dashboard-builder.md`, `docs/dashboards/construtor-dashboards-arquitetura.md` e os relatórios em `docs/dashboards/relatorios/` (para saber o que existe e o que será desligado).

## Regras invioláveis

1. **Stack do GTW permanece imutável**: não alterar `.setup-gtw\` nem versões/dependências do `pom.xml`; sem novos frameworks no Java/JSP. **Exceção aprovada em 16/10/2026**: o Superset roda como **serviço separado** (Docker Compose: Superset, Postgres de metadados, Redis, nginx, workers Celery), fora do WAR do GTW. Nada do Superset entra no `pom.xml`. O código Java novo limita-se a servlets de integração (SSO, auditoria, link de menu) em pacote próprio `muralha.digital.analise`.
2. **Versões fixadas**: imagem `apache/superset:6.0.x` com tag explícita (nunca `latest`); drivers `pymssql`, `psycopg2-binary`, `redis` com versão no `Dockerfile`. Atualização de versão é uma sessão própria com teste de regressão.
3. **Banco do GTW somente leitura**: nenhum DDL/DML executado. Views, tabelas materializadas, índices e jobs viram scripts idempotentes em `docs/banco-de-dados/migracoes/AAAAMMDD_analise_<assunto>.sql` (`IF NOT EXISTS`, cabeçalho, rollback comentado), registrados em `docs/entregas/pendencias-banco-de-dados.md` como pendentes. Objetos novos ficam no schema **`analise`** (criar na primeira migração). O Superset conecta com `allow_dml = False`, sem CTAS/CVAS/upload, usando o **mesmo login da aplicação** (decisão de 16/10; sem login dedicado).
4. **Código Java**: sessão e permissão pela errata `00c` (`SessaoConstantes.SESSAO_USUARIO`, `getIdUsuario()`, `verificaAcesso(false)`), SQL parametrizado, try-with-resources, UTF-8, PT-BR, logger (nunca `System.out/err`). **Código Python** (config, security manager, scripts): PT-BR nos nomes e mensagens, sem segredo em código, tudo via `.env`; `python -m py_compile` e `ruff`/`black` se disponíveis.
5. **Segredos**: `.env` do Superset nunca versionado; `.env.exemplo` sempre atualizado. `SUPERSET_SECRET_KEY` gerada, nunca o padrão. O container do Superset **não publica porta**; só o nginx o alcança. Os cabeçalhos `X-GTW-*` são sobrescritos pelo nginx em toda requisição.
6. **Parametrização**: limiares, TTLs, intervalos de refresh, mapeamento grupo→papel, URLs e limites de linha ficam em `.env`, em `superset_config.py` lendo o `.env`, ou em `muralha.config_chave_valor` (lado GTW). Nunca hard-coded.
7. **Visual**: no GTW, Bootstrap 5.3, navbar `#0d75bf`, Bootstrap Icons, SweetAlert2. No Superset, o tema `THEME_DEFAULT`/`THEME_DARK` da PoC (cores do GTW), idioma `pt_BR`, formatos brasileiros (`D3_FORMAT`, `D3_TIME_FORMAT`), `APP_NAME` e logo do Muralha Digital. Gráficos seguem a paleta do tema, nunca cores soltas por gráfico.
8. **Documentação**: cada sessão gera `docs/analise/sessoes/SUP-NN-<assunto>.md` (escopo e tarefas) e `docs/analise/relatorios/SESSAO-SUP-NN-relatorio.md` (feito, testes, pendências), atualiza `docs/analise/README.md`, o manual do usuário `docs/analise/manual-analises.md` quando a experiência mudar, e `docs/entregas/23-analises-superset.md` (entrega única do projeto, com `## Origem` apontando para o plano `docs/planos-salvador/23-analises-superset.md`, criado na SUP-1). Nunca referenciar documento que não exista.
9. **Status honesto**: uma sessão só é "Concluída" com critérios de aceite **executados e marcados**, incluindo teste no navegador com usuário real do GTW. O que não pôde ser testado fica listado e a sessão como "Implementada — aguardando teste".
10. **Testes**: lado GTW, TestNG em `src/test/java/muralha/digital/analise/**`; lado Superset, `pytest` em `poc-superset/tests/` (ou a pasta que a SUP-1 definir) cobrindo `gtw_security.py`, provisionamento idempotente e permissões por papel, usando o cliente de teste do Flask como na validação da PoC; SQL das views com casos de verificação em `docs/banco-de-dados/migracoes/` (bloco `-- TESTE` comentado). Rode `..\.setup-gtw\build.ps1` e os testes ao fim de cada sessão; não feche sessão com build quebrado ou teste vermelho.
11. **Git**: branch `feat/sup-NN-<assunto>`, commits em português no imperativo, sem segredos (secret scanning bloqueia o push — não usar bypass), PR ao fim da sessão.
12. **Pare e pergunte** antes de: remover tabela, coluna, classe, tela ou menu do builder antigo; executar qualquer coisa no banco; alterar `nginx-1.26.2/conf/nginx.conf` do GTW em produção; mudar a versão do Superset; qualquer ação irreversível.
13. **Propostas de melhoria fora do escopo — propor sim, executar só com autorização.** Você está liberado e incentivado a identificar melhorias além das listadas (UX, desempenho, segurança, simplificação, recursos do Superset ainda não usados, ajustes no roteiro). Porém:
    - **Não implemente** melhoria que não esteja na sessão em andamento sem autorização explícita do dono do projeto nesta conversa ("autorizado", "pode fazer" ou equivalente, citando o número da proposta). Silêncio ou aprovação genérica da sessão **não** contam.
    - Registre cada proposta em `docs/analise/propostas-melhoria.md` com: `PM-NN`, data, sessão de origem, problema observado com evidência, proposta, benefício para quem opera, esforço (P/M/G), risco, impacto em banco/infra, sessões afetadas e status (`Proposta` / `Autorizada` / `Recusada` / `Implementada em SUP-NN`).
    - Apresente as propostas novas **no relatório de fim de sessão**, em seção própria. Se uma proposta for bloqueante para a sessão atual, pare e pergunte na hora.
    - Proposta autorizada entra na sessão indicada (ou em nova `SUP-NN`), com os mesmos requisitos de teste, documentação e aceite; atualize o status.
    - Proposta recusada não é reapresentada, salvo fato novo explicitado.
    - Esta regra não suspende as regras 1 a 12.

## Decisões já tomadas (não reabrir)

| Tema | Decisão |
|---|---|
| Plataforma | Apache Superset 6.0 (Apache 2.0, auto-hospedado, sem Preset/Cloud). O Dashboard Builder próprio é **desligado** ao fim do projeto (SUP-13), após migração dos painéis em uso. |
| Login | Único, pelo GTW: nginx com `auth_request` para `/MuralhaDigital/SsoAnalise`; Superset lê `X-GTW-Usuario` via `GtwSecurityManager` + `before_request` (como na PoC — no Superset 6 a rota `/login/` é sempre a tela React própria, por isso o login não usa a view padrão de `AUTH_REMOTE_USER`). Sem sessão no GTW → redireciona ao login do GTW. |
| Permissões | Fonte da verdade é `dbo.sis_grupo` / `dbo.sis_usuario_grupo` do GTW. Mapeamento grupo → papéis do Superset em `GTW_GRUPOS_PAPEIS` (`.env`), sincronizado a cada login. Papéis: `Admin`, `Alpha` (cria), `Gamma` (consome), `GTW_Leitura` (dados), mais papéis por área criados na SUP-5. Permissão por painel via `DASHBOARD_RBAC`. Usuário sem grupo mapeado não vê dados. |
| Banco | Mesmo login da aplicação, `allow_dml = False`, sem CTAS/CVAS/upload, `PREVENT_UNSAFE_DB_CONNECTIONS = True`; objetos de apoio no schema `analise`; dados pessoais (placa, CPF) **fora** dos datasets analíticos (LGPD) — quando indispensáveis, via view anonimizada do Plano 13 (`muralha.v_vtr_anonimizado`). |
| Desempenho | Nada de consulta direta a `muralha.veiculo_tempo_real` (8,2 M linhas) em painel: datasets analíticos sobre **tabelas materializadas por job** (`analise.mat_*`), atualizadas por procedure agendada no SQL Server Agent ou por job Quartz do GTW; `DATA_CACHE_CONFIG` 60 s em Redis; `ROW_LIMIT` 10.000; `SQLLAB_TIMEOUT` 30 s. Dataset bruto fica só para administradores. |
| SQL Lab | Só papel `Admin` (e `Alpha` apenas se autorizado em proposta). Operadores não escrevem SQL. |
| Relatórios e alertas | Usar `ALERT_REPORTS` do Superset (Celery + Redis + navegador headless no container de worker) — substitui as sessões DASH-22/23 do roteiro antigo. PDF/PNG do painel gerados pelo próprio Superset. Destinatários por e-mail via SMTP já usado pelo módulo de Chamados (Plano 22). |
| Busca | A busca nativa do Superset (título, descrição, tags, owner). Sem Full-Text Search no SQL Server. |
| Mapas | Camadas deck.gl com tiles **OpenStreetMap** (`DECKGL_BASE_MAP`), sem Mapbox. Áreas monitoradas (`muralha.area_monitorada`) expostas como GeoJSON via view para a camada de polígonos. |
| Embedding | Painéis operacionais que precisem aparecer **dentro** de uma tela do GTW usam o Embedded SDK (`EMBEDDED_SUPERSET`, guest token emitido por servlet do GTW); o restante abre no Superset pelo item de menu. |
| Auditoria (TR 5.1.7) | Eventos do Superset (login, criação/edição/exclusão de painel, gráfico, dataset, exportação, consulta) replicados para `dbo.sis_log_auditoria` por um `EVENT_LOGGER` customizado que chama o servlet `/MuralhaDigital/AuditoriaAnalise` (ou grava direto via `pymssql` se autorizado). |
| v1 e v2 do builder | `muralha/digital/dashboard` (v1) e `muralha/digital/dashboards` (v2, schema `dash`) saem do código na SUP-13, **se não houver uso** não migrado — comprovar e pedir confirmação. Tabelas `dash.*` e `muralha.dashboard_*` só são dropadas por script aprovado. |
| Bibliotecas vendorizadas do builder (ECharts, GridStack, AG Grid, SortableJS, X6, dagre) | Deixam de ser necessárias; removidas junto com o builder na SUP-13. |

---

## SUP-1 — Fundação: plano, ambiente e PoC promovida a base do projeto

- Criar `docs/planos-salvador/23-analises-superset.md` (goal, arquitetura, decisões acima, mapa de sessões, itens do TR atendidos: §5.2.8.4, §5.4.2.1-6, §5.4.3.5-7, §5.4.4.1-3, §5.4.5.1-2, §5.4.6.1-3, §5.5.3.1-4) e `docs/entregas/23-analises-superset.md` (esqueleto com `## Origem`).
- Mover `poc-superset/` para `analise/` na raiz do repositório (ou caminho que o dono aprovar), com `README.md`, `docker-compose.yml`, `superset/`, `nginx/`, `gtw/`, `tests/`. `.gitignore` para `.env` e volumes.
- Fixar versões no `Dockerfile` (`apache/superset:6.0.0` ou a 6.0.x mais recente, com justificativa) e nos drivers.
- Compose de **desenvolvimento** (como na PoC) e de **produção** (`docker-compose.prod.yml`: `restart: always`, limites de memória, logs em arquivo com rotação, healthchecks em todos os serviços, workers Celery ligados, volumes nomeados, rede interna).
- Copiar `SsoAnaliseServlet.java` para `src/main/java/muralha/digital/analise/`; teste TestNG do contrato (204 com cabeçalhos / 401 sem sessão / redirect com `acao=abrir`).
- Script `20261020_analise_fundacao.sql`: `CREATE SCHEMA analise`, chave `SUPERSET_URL` em `config_chave_valor`, item de menu "Análises" (raiz 222, card em Dashboards) com direitos para os grupos mapeados.
- `tests/` com pytest reproduzindo a validação da PoC (login pelo cabeçalho, forja ignorada, troca de usuário, grupo→papel, sem grupo = 0 datasets, idempotência do provisionamento).
- Aceite: `docker compose up` sobe limpo; usuário do GTW entra no Superset sem senha nova; testes verdes; build do GTW verde.

## SUP-2 — Camada de dados analítica (views e materializações)

Substitui o catálogo e o `MontadorSql` do builder por objetos no SQL Server, legíveis por qualquer ferramenta.

- `analise.mat_passagens_hora` (hora, id_local, local, faixa, classificação, placa_mercosul, passagens, soma_velocidade, velocidade_média, lat, lon), carga incremental por janela (última hora fechada + reprocesso de 24 h), índice clusterizado em (hora, id_local). Procedure `analise.spu_carga_passagens_hora` + registro em `analise.carga_log` (início, fim, linhas, erro).
- `analise.mat_infracoes_dia` (dia, id_local, local, faixa, enquadramento, limite, total, excesso médio, lat, lon).
- `analise.v_alertas` (data, tipo, descrição, prioridade, status, origem, descartado, tratado, usuário responsável — sem placa).
- `analise.v_equipamentos` (id_local, nome, lat, lon, tipo, ativo, área monitorada) e `analise.v_areas_monitoradas_geojson`.
- `analise.v_disponibilidade_dia`, `analise.v_sla_latencia_dia`, `analise.v_sla_preproc_dia`, `analise.v_assertividade_dia` (sobre as tabelas dos Planos 05, 07, 08, 10, 18) — fontes dos painéis operacionais.
- `analise.v_preprocessamento` com as 14 alíneas do TR 5.5.3.1 (recebidos, pré-processados, disponibilizados, descartados, aproveitamento, tempo médio, produtividade por operador, divergência, retrabalho, pendentes, backlog, SLA, motivos de descarte, histórico) por período/equipamento/tipo/operador/lote/local/faixa.
- Agendamento: procedure chamada por job (SQL Server Agent se disponível; senão `CargaAnaliseJob` Quartz no `Agendador` do GTW, com intervalo em `config_chave_valor`).
- Aceite: scripts idempotentes com bloco `-- TESTE`; estimativa de tempo da carga documentada; nenhum campo de dado pessoal nas views.

## SUP-3 — Datasets, métricas e semântica no Superset

- `provisionar.py` evolui para um **provisionamento declarativo** (YAML em `analise/superset/datasets/*.yaml` → import via API/CLI do Superset com UUIDs estáveis), cobrindo todos os objetos da SUP-2: rótulos em PT-BR, descrições, `is_dttm`, `groupby`, `filterable`, formatos D3 e **métricas nomeadas** (total de passagens, velocidade média, % descartados, aproveitamento, SLA cumprido, etc.). Métricas certificadas (`certified_by` = "Consilux / Edital Salvador").
- Pastas de dataset (recurso 6.0) por domínio: Passagens, Infrações, Alertas, Equipamentos, Pré-processamento, SLA.
- Dataset bruto `veiculo_tempo_real` só visível para `Admin`, com `warning_markdown`.
- Aceite: `superset import-datasources` idempotente; cada métrica com teste de valor contra consulta SQL de referência em pelo menos uma data.

## SUP-4 — Painéis operacionais padrão (migração dos painéis do builder e do Plano 15)

- Recriar no Superset, como painéis **oficiais** (tag `oficial`, owner = grupo administrador): KPIs do Plano 15, Assertividade (5.2.2.6, com limiar < 80% destacado), SLA de latência (5.2.7), Disponibilidade (5.8.2), Pré-processamento (5.5.3), Alertas por tipo/região, Infrações por equipamento/enquadramento, Passagens por hora/faixa/classificação, Mapa de equipamentos e infrações (deck.gl + áreas monitoradas).
- Filtros nativos padrão em todos: período (obrigatório, default 7 dias), equipamento, região; cross-filter ligado; drill-by configurado.
- Auto-refresh por painel conforme a decisão "tempo real" (intervalo em cada painel; o Superset expõe o controle ao usuário).
- Exportar os painéis como ZIP (`superset export-dashboards`) para `analise/superset/dashboards/` e versionar; importação idempotente faz parte do provisionamento.
- Aceite: cada painel abre em < 3 s com cache quente e < 10 s frio sobre `mat_*`; comparação de números com as telas atuais do GTW (KPIs, assertividade, SLA) documentada.

## SUP-5 — Permissões, coleções e governança

- Papéis por área no Superset (`GTW_Operacao`, `GTW_Gestao`, `GTW_Fiscalizacao`, …) mapeados em `GTW_GRUPOS_PAPEIS`; `DASHBOARD_RBAC` para publicar painel por papel; tags como coleções (`oficial`, `operacional`, `gerencial`, `rascunho`, por área).
- Fluxo: operador com `Alpha` cria em rascunho → publica para o seu papel; painéis `oficial` só editáveis por `Admin`.
- Row-level security quando necessário (ex.: grupo que só vê certos equipamentos) via regras RLS ligadas a `local`.
- Tela/documento "Como organizar painéis" no manual do usuário.
- Aceite: testes pytest de permissão por papel (quem vê o quê); usuário de grupo não mapeado não vê nenhum dataset.

## SUP-6 — Auditoria e observabilidade

- `EVENT_LOGGER` customizado (`gtw_event_logger.py`): cada ação relevante (login, dashboard/chart/dataset criado, editado, excluído, exportação, consulta SQL Lab, acesso a painel) vira registro em `dbo.sis_log_auditoria` via servlet `/MuralhaDigital/AuditoriaAnalise` (assinatura HMAC com segredo compartilhado no `.env`) — atende TR 5.1.7 para o módulo.
- Painel administrativo "Operação das Análises" no próprio Superset sobre o `action_log` e o `query` log: consultas lentas, painéis mais acessados, erros, usuários ativos, cargas `analise.carga_log` com falha.
- Healthchecks do compose e endpoint `/health` monitorados pela tela de Disponibilidade (Plano 18) via `HealthCheckServlet` do GTW ou view.
- Retenção do `action_log`/`query` do Superset (tarefa Celery) e do `analise.carga_log` (job existente de retenção, Plano 12).
- Aceite: ação feita no Superset aparece em `sis_log_auditoria` com usuário, IP, funcionalidade e id do recurso.

## SUP-7 — Alertas e relatórios agendados

- Ligar `ALERT_REPORTS`, perfil `relatorios` do compose (worker + beat + navegador headless no container), `SMTP_*` do mesmo servidor do módulo de Chamados.
- Alertas analíticos do TR 5.4.3.5-6 como **Alerts** do Superset sobre as métricas (aumento atípico de infrações, degradação de desempenho, redução de disponibilidade, aumento de retenções) com limiares parametrizados; o disparo também cria registro em `muralha.alerta` tipo ANALÍTICO via servlet, entrando no fluxo de tratativa do GTW.
- Relatório gerencial periódico (TR 5.4.3.7) e relatório mensal de pré-processamento até o 5º dia útil (TR 5.5.3.3) como **Reports** (PDF/PNG do painel + CSV) por e-mail aos grupos configurados.
- Aceite: alerta de teste dispara e-mail e registro em `muralha.alerta`; relatório agendado chega com PDF legível.

## SUP-8 — Mapas e análise espacial

- Camadas deck.gl: scatter/heatmap de infrações e passagens por equipamento, polígonos das áreas monitoradas (GeoJSON da `v_areas_monitoradas_geojson`), arcos entre equipamentos para rotas se houver dado; tiles OSM; `CORS_OPTIONS` e CSP ajustados.
- Filtro espacial por área monitorada (dimensão `area` nas views) e por região administrativa (`localidade_desc`).
- Exportação GIS: botão "exportar GeoJSON/KML" continua no GTW (Plano 16) — avaliar na sessão se o CSV com lat/lon do Superset basta ou se o servlet `Gis` deve ganhar as camadas de alertas/infrações (fecha TR 5.4.4.4).
- Aceite: mapa de infrações dos últimos 30 dias abre em < 5 s; polígonos corretos sobre 3 áreas de teste.

## SUP-9 — Embedding em telas do GTW

- Servlet `/MuralhaDigital/AnaliseEmbed` emite **guest token** (assinado com `GUEST_TOKEN_JWT_SECRET`, RLS por grupo quando aplicável) para painéis marcados como embutíveis.
- Página JSP `pages/analises/embed.jsp` com o `@superset-ui/embedded-sdk` **vendorizado** (versão fixa) exibindo o painel dentro do layout do Muralha (navbar, menu) — para a sala de operações e para a TV (modo apresentação sem filtros).
- Tema do embed herdado (`dynamic theme` do SDK 6.0).
- Aceite: painel embutido abre dentro do GTW sem pedir login; token expira em 5 min e é renovado em silêncio; usuário sem permissão vê mensagem amigável.

## SUP-10 — Manual do usuário e capacitação

- `docs/analise/manual-analises.md` em PT-BR: entrar, encontrar painéis (tags, favoritos, busca), criar gráfico sem SQL, montar painel, filtros, cross-filter, drill, exportar, compartilhar, assinar relatório, boas práticas (sempre filtrar período; usar datasets materializados).
- Vídeos curtos ou GIFs opcionais em `docs/analise/midia/`.
- Roteiro de treinamento de 2 h para operadores e de 4 h para administradores.
- Aceite: dois operadores executam o roteiro de avaliação da PoC (10 tarefas) sem ajuda, com tempos registrados.

## SUP-11 — Produção: implantação, backup e operação

- `docker-compose.prod.yml` validado no servidor de produção (ou VM dedicada): TLS no nginx (certificado da Consilux), `session_cookie_secure = True`, `force_https`, `PROXY_FIX`; integração com o nginx já existente do GTW (`nginx-1.26.2`), decidindo se o Superset fica atrás dele (`/analises/` com `SUPERSET_APP_ROOT`) ou em porta própria.
- Backup diário do Postgres de metadados (`pg_dump`) e dos exports de dashboards para o repositório de backups da Consilux; teste de restauração documentado.
- Procedimento de atualização de versão do Superset (staging → produção) e de rollback.
- Runbook `docs/analise/runbook.md`: subir/parar, logs, rotacionar segredo, adicionar grupo→papel, recriar usuário, incidentes comuns.
- Aceite: restauração de backup em ambiente limpo reproduz painéis e permissões.

## SUP-12 — Desempenho e carga

- Benchmark (`benchmark.py` evoluído) comparando `mat_*` × views × bruto, com cache frio/quente, 7/30/90 dias; registrar em `docs/analise/relatorios/`.
- Teste de carga com 20 usuários simultâneos abrindo 5 painéis (k6 ou Locust em container, fora do pom); metas: p95 < 5 s, nenhum timeout, CPU do SQL Server dentro do limite acordado.
- Ajustes: índices em `mat_*`, janela de carga, TTL de cache por dataset, `ROW_LIMIT`, workers gunicorn.
- Aceite: metas atingidas ou plano de materialização adicional aprovado.

## SUP-13 — Desligamento do Dashboard Builder próprio

- Inventário do que ainda é usado no builder (menus 376-378, `dash.consulta_log`, painéis existentes); migrar os que faltam para o Superset (SUP-4).
- Com confirmação: remover `muralha/digital/dashboard` (v1), `muralha/digital/dashboards` (v2), `pages/analises/**` do builder, `pages/dashboard/builder/**`, `pages/admin/datasets/**`, `static/js/dashboards/**`, bibliotecas vendorizadas sem outro uso, testes correspondentes; scripts de desativação de menus e de **arquivamento** (não drop) das tabelas `dash.*` e `muralha.dashboard_*` (drop só em script separado aprovado).
- Atualizar `CLAUDE.md`, `docs/README.md`, `AGENTS.md`, `04-modulos-funcionais.md`, Planos 20/21 (status "Substituído pelo Plano 23"), Entrega 21, `docs/dashboards/` (marcar histórico), `itens-edital-x-entregas.md` e matriz de aderência (itens 5.4.x → Entrega 23).
- Aceite: build verde sem o builder; nenhum link quebrado na documentação (script da Fase 1 da auditoria).

## SUP-14 — Homologação e PoC do edital

- Roteiro de demonstração para a comissão (Anexo A do TR): painéis oficiais, filtros, drill, exportação PDF/CSV/XLS, alerta analítico disparando, relatório agendado, mapa, permissões por perfil, auditoria.
- Checklist de aderência dos itens 5.2.8.4, 5.4.2-5.4.6 e 5.5.3 com evidência (print, export, log).
- Lista de pendências e propostas (`propostas-melhoria.md`) consolidada para decisão.

---

## Ao final de cada sessão, entregue

1. Resumo em até 10 linhas: o que mudou para quem opera.
2. Arquivos criados/alterados (GTW, `analise/`, docs).
3. Migrações geradas (pendentes de execução manual) e cargas/jobs novos.
4. Resultado do build do GTW, dos testes TestNG e dos testes pytest (quantidade e nomes dos novos).
5. Critérios de aceite: executados / pendentes, com o motivo.
6. Atualizações de documentação feitas (plano 23, entrega 23, manual, runbook).
7. **Propostas de melhoria** (regra 13): novas `PM-NN` desta sessão em tabela (número, resumo, benefício, esforço, risco, sessão sugerida), aguardando autorização; status das anteriores.
8. Link do PR.
