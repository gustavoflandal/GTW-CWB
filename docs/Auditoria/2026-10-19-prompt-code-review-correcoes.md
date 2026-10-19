# Prompt para Code Review e Correção dos Problemas da Auditoria

> **Data:** 2026-10-19
> **Uso:** copie o bloco da seção 2 (prompt mestre) para um agente de código (ex.: Claude Code) aberto na raiz do repositório. Na seção 3 há variantes por lote para dividir o trabalho em PRs menores.
> **Base:** [Relatório de Auditoria](2026-10-19-relatorio-auditoria.md) · [Sugestões de Melhoria](2026-10-19-sugestoes-melhoria.md)

---

## 1. Como usar

1. **Faça antes a Fase 0 fora do código**: rotacione as credenciais e aplique os bloqueios de nginx descritos na matriz P0 do relatório. Nenhum prompt substitui a rotação de segredos.
2. Rode **um lote por vez** (seção 3), cada um num branch e PR próprios. Lotes pequenos facilitam a revisão humana e o teste manual no Windows.
3. Para cada lote, o agente deve gerar um plano em `docs/planos-salvador/` e uma entrega em `docs/entregas/` com a seção **"## Origem"** apontando para o plano ([`CLAUDE.md`](../../CLAUDE.md)). O próximo número livre hoje é o **35**; confirme antes de criar.
4. Revise o PR com o checklist da seção 4 antes de aprovar.

---

## 2. Prompt mestre

```text
Você é um engenheiro sênior de segurança e manutenção de sistemas Java legados, trabalhando no
repositório GTW-CWB (Java 13 / JSP / Tomcat 9 / SQL Server 2016; GTW clássico + Muralha Digital).
Responda e escreva commits, comentários e documentação em português do Brasil.

## Objetivo
Fazer code review e corrigir os problemas levantados na auditoria de 2026-10-19, no LOTE indicado
ao final deste prompt, com o menor diff correto possível, sem regressões e com testes.

## Leitura obrigatória (antes de qualquer alteração)
1. CLAUDE.md (regras que nunca mudam).
2. docs/agentes/AGENTS.md e docs/planos-salvador/00c-errata-correcoes.md.
3. docs/requisitos/06-padroes-de-codigo.md (§1.1 sessão + permissão; §2 conexão/transação),
   docs/requisitos/09-seguranca-e-permissoes.md, docs/requisitos/05-padrao-visual.md.
4. docs/auditoria/2026-10-19-relatorio-auditoria.md (achados, IDs, arquivo:linha).
5. docs/auditoria/2026-10-19-sugestoes-melhoria.md (abordagem recomendada para cada tema).

## Regras invioláveis
- NÃO alterar pom.xml nem .setup-gtw/ e NÃO adicionar bibliotecas. Se a correção exigir isso,
  pare, descreva a necessidade e peça aprovação explícita.
- NÃO executar DDL/DML no banco GTW_MURALHA_DEV. Mudanças de banco (inclusive procedures) só
  como script idempotente em docs/banco-de-dados/migracoes/AAAAMMDD_descricao.sql, registrado
  no README da pasta, marcado como "pendente de execução".
- NÃO usar ALTER TABLE em tabelas existentes; use tabelas complementares (00b-tabelas-complementares.md).
- NÃO commitar segredos. Nunca copie para código, teste, log ou documentação o valor de uma
  senha/chave encontrada; mascare (ex.: "AIza…"). Segredos vêm de variável de ambiente ou do
  confGTW.xml/muralha-digital-config.xml locais (ver 07-configuracao-e-segredos.md).
- Código legado (com.consilux.*, webapp/{cadastro,processo,relatorio,…}) recebe só correções
  pontuais; não refatore além do necessário para o achado.
- Não desabilitar, pular ou apagar testes para "ficar verde".
- Não inventar referências a documentos inexistentes; cite apenas arquivos reais.
- Padrões obrigatórios em todo código tocado: sessão (SessaoConstantes.SESSAO_USUARIO) +
  permissão (Acesso.verificaAcesso) em servlet; idUsuario sempre da sessão, nunca do request;
  SQL com PreparedStatement e "?"; try-with-resources; nenhum campo mutável em servlet;
  sem e.getMessage() na resposta ao cliente (mensagem genérica + log com stack);
  escape de saída (<c:out>/fn:escapeXml em JSP; função esc() em JS); SweetAlert2 em vez de
  alert/confirm; arquivos em UTF-8; logger log4j em vez de System.out/printStackTrace.

## Processo para CADA achado do lote
1. Localize: abra o arquivo:linha citado e confirme que o problema ainda existe no HEAD. Se não
   existir ou se a evidência estiver errada, registre "não reproduz" com a justificativa e siga.
2. Avalie o alcance: procure ocorrências do mesmo padrão (grep) no módulo e liste-as. Corrija as
   do mesmo lote; as de fora do escopo, apenas registre na entrega como "pendência".
3. Teste primeiro, quando viável: escreva um teste TestNG (src/test/java/<mesmo pacote>) que
   falhe com o código atual (Mockito já está no classpath para HttpServletRequest/Response).
4. Corrija com a menor mudança correta, reaproveitando utilitários existentes antes de criar novos.
   Se criar utilitário (ex.: ArquivoSeguro, XmlSeguro, esc()), coloque-o no local indicado nas
   Sugestões de Melhoria e use-o em todos os pontos do lote.
5. Verifique:
   - compilação: se o Maven não estiver disponível no ambiente (as dependências com.consilux.gtw:*
     vêm de repositório interno), compile ao menos as classes alteradas com javac usando o
     classpath disponível e declare isso na entrega; o build oficial é ..\.setup-gtw\build.ps1;
   - testes novos e afetados executados (TestNG), com o resultado copiado na entrega;
   - python3 -I docs/referencia/scripts/validar_aderencia.py sem erro;
   - releia o próprio diff de forma adversarial: o que quebraria em produção? tela legítima
     bloqueada? usuário perdendo acesso? encoding? JSON inválido?
6. Documente: atualize docs/referencia/* se rotas/servlets mudarem e marque o status do item
   correspondente (S/R/M) em docs/requisitos/12-riscos-e-debitos-tecnicos.md.

## Code review (antes de corrigir e de novo ao final)
Para cada arquivo do lote, revise também, mesmo que não citado na auditoria:
- autenticação/autorização, IDOR (dado de outro usuário/grupo), CSRF em escrita;
- injeção (SQL, comando, caminho, XML/XXE, cabeçalho), XSS de saída;
- vazamento de recurso, transação, concorrência (estado compartilhado), timeouts;
- tratamento de erro (catch vazio, exceção engolida que vira "sucesso"), logs com dado sensível.
Novos achados relevantes: corrija se forem do mesmo tipo e escopo do lote; senão, registre com
ID novo (prefixo "REV-") na entrega, com severidade, arquivo:linha e recomendação.

## Entregáveis do lote
1. Branch próprio; commits pequenos, em português, no imperativo
   (ex.: "Torna o ValidaSessao fail-closed para rotas da Muralha").
2. docs/planos-salvador/NN-<slug>.md: escopo, achados (IDs), abordagem, riscos, critérios de aceite.
3. docs/entregas/NN-<slug>.md com: Data, Status, Escopo, "## Origem" (link relativo ao plano),
   tabela Item → Arquivos, Migrações (se houver, todas "pendente de execução"),
   Critérios de aceite (checkbox, com o que foi e o que não foi verificado e onde),
   Pendências, e um roteiro de teste manual em docs/testes/NN-<slug>.md.
4. Resumo final na resposta: tabela "ID | situação (corrigido / não reproduz / pendente) |
   arquivos | teste", lista do que exige aprovação (pom.xml, execução de migração, rotação de
   segredo, limpeza de histórico) e riscos de regressão para o teste manual.

## Critérios de parada
Pare e pergunte antes de: alterar pom.xml/.setup-gtw; reescrever histórico do git; mudar
comportamento visível de login/permissão sem plano de transição (ex.: Acesso fail-closed sem
inventário de rotas); apagar dados; mexer em procedure sem script de rollback.

## LOTE A EXECUTAR
<cole aqui um dos lotes da seção 3>
```

---

## 3. Lotes (cole um por vez no final do prompt mestre)

### Lote 1: Recuperação de senha e contas (P1)

```text
LOTE 1 — Recuperação de senha e tomada de conta.
Achados: SEC-AUTH-04, SEC-AUTH-05, SEC-AUTH-06, SEC-AUTH-07, SEC-AUTH-08, SEC-AUTH-18.
Arquivos principais: muralha/digital/notificacao/NotificacaoServlet.java, RecuperacaoSenha.java,
ServicoEmailMuralha.java, muralha/digital/acessos/UsuarioServlet.java, acessos/Usuario.java,
e as telas JS/JSP de recuperação que chamam essas ações.
Resultado esperado:
- atualizarsenha recebe só token + novaSenha; o usuário vem do token; o token expira (30 min),
  é de uso único e é invalidado; aplica SenhaService.validarComplexidade e histórico;
- o token é enviado apenas ao e-mail/telefone cadastrado; não aceita destinatario/idUsuario do cliente;
- buscarusuario devolve resposta genérica (sem id/e-mail/telefone);
- acessoViaToken e desbloquear exigem sessão (+ permissão de administrador para desbloquear) e POST;
- testes TestNG cobrindo: sem token → nega; token expirado → nega; token reutilizado → nega;
  idUsuario no request é ignorado.
Atualize as telas para o novo contrato sem quebrar o fluxo do usuário.
```

### Lote 2: Filtro de sessão, `Acesso` e endpoints sem login (P1)

```text
LOTE 2 — Controle de acesso centralizado.
Achados: SEC-AUTH-01, SEC-AUTH-02, SEC-AUTH-03, SEC-AUTH-13, SEC-AUTH-39, SEC-SQL-02 (bypass
?sistema=, 48 servlets) e a lista "Servlets sem verificação de sessão" da §2 do relatório,
incluindo FE-29/FE-30 (parametros.jsp, video-ao-vivo.jsp, detalhes-fato.jsp, base-template.jsp).
Arquivos principais: com/consilux/infra/ValidaSessao.java, com/consilux/model/Acesso.java,
muralha/digital/websocket/ControleAcessoEnvio.java, ClienteSessoes.java e os servlets listados.
Resultado esperado:
- ValidaSessao fail-closed com getServletPath() e lista exata de rotas públicas; 401 JSON para
  /MuralhaDigital/* e AJAX, redirecionamento ao login no resto;
- ANTES de tornar Acesso fail-closed, gere um inventário (docs/referencia/) das rotas sem cadastro
  em sis_menu cruzando docs/referencia/servlets-e-endpoints.md com uma consulta de leitura; se não
  houver acesso ao banco, entregue a consulta pronta e deixe o fail-closed atrás de um parâmetro
  de configuração desligado por padrão, documentando o passo de ativação;
- catch vazio em Acesso → nega e registra log;
- remover o bypass ?sistema= (chamadas internas passam a usar a camada de modelo);
- WebSocket valida a HttpSession no handshake (Configurator.modifyHandshake) e usa o id da sessão;
- servlets listados com o padrão §1.1 de 06-padroes-de-codigo.md;
- testes: sem sessão → 401; rota pública → passa; exceção em Acesso → nega; URL com ";/login/" → bloqueia.
```

### Lote 3: Injeção, arquivos e XML (P1/P2)

```text
LOTE 3 — Injeção e manipulação de arquivos.
Achados: SEC-SQL-01, SEC-SQL-03, SEC-SQL-04, SEC-SQL-05, SEC-SQL-07, SEC-AUTH-09 (ffmpeg/leitura
de arquivo), SEC-AUTH-10, SEC-AUTH-11 (remover credencial FTP do código; ler de configuração),
SEC-AUTH-12, SEC-AUTH-14, SEC-AUTH-36, SEC-AUTH-37, SEC-AUTH-38, CFG-28, CFG-29, PERF-01.
Resultado esperado:
- toda entrada do usuário em SQL via "?"; listas IN com placeholders gerados; TOP/limites com teto;
- utilitário muralha/digital/util/ArquivoSeguro (canonicalPath dentro da base, nome UUID com
  extensão de lista branca, magic bytes) usado em todos os pontos de leitura/gravação do lote;
- VerVideoMonitoramentoServlet: recebe id, resolve caminho no banco, ProcessBuilder(List) com
  -protocol_whitelist file, streaming com suporte a Range, temporário apagado em finally, sessão + Acesso;
- utilitário XmlSeguro (disallow-doctype-decl, FEATURE_SECURE_PROCESSING) usado por
  SerializadorXML e AjaxXMLLeitor;
- documentos de registro de fato/boletim filtrados pelo grupo do usuário;
- SEC-SQL-06 (procedures com EXEC(@sql)): gerar scripts sp_executesql em migracoes/ com rollback,
  "pendente de execução", e validação de entrada no Java enquanto isso;
- testes: payload com aspas/"; DROP" não altera a consulta; "..\\" é rejeitado; XML com DOCTYPE é rejeitado.
```

### Lote 4: Senhas, login, MFA, sessão e CSRF (P2/P3)

```text
LOTE 4 — Credenciais e sessão.
Achados: SEC-AUTH-15, 16, 17, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33; FE-10; LOG-01; LOG-02.
Resultado esperado:
- SenhaService: corrigir u.login → u.usuario e negar o login se a checagem falhar (teste de bloqueio após N falhas);
- PBKDF2WithHmacSHA256 (JDK) com sal na coluna existente senha_hash, re-hash transparente no login
  a partir do MD5 legado, comparação com MessageDigest.isEqual; remover login por hash e eco do hash;
- ordem do login: IP (getRemoteAddr / X-Real-IP de proxy confiável) → credencial → bloqueio → MFA →
  troca obrigatória → criação da sessão → redirecionamento só para caminho relativo;
- login Google e login do app passam pelas mesmas verificações; REDIRECT_URI por configuração;
- MFA com limite de tentativas, antirreuso de time-step e reautenticação para desativar;
- web.xml: cookie-config (http-only, secure), tracking-mode COOKIE, error-page genérica (página nova
  sem parâmetros refletidos); remover cookies manuais e a variável JS jsessionid;
- filtro CSRF (token por sessão em <meta>, enviado por wrapper em utilidades.js, validado em POST/PUT/DELETE);
- GuestTokenServlet: dashboards permitidos por grupo; UsuarioServiceImpl com Acesso; remover override
  vazio de checkPermutationStrongName;
- remover do log tokens, hash de senha e prints de login_action_app.jsp.
Atenção: mudanças de login exigem roteiro de teste manual detalhado (usuário comum, admin, MFA, Google, app).
```

### Lote 5: Estabilidade, dados e jobs (P2)

```text
LOTE 5 — Confiabilidade e desempenho.
Achados: CONF-01…11, CONF-14, CONF-15, DADOS-01…10, PERF-02…05, CFG-33, LOG-04, LOG-05.
Resultado esperado:
- Conexao.java: removeAbandoned(300) + logAbandoned, socketTimeout/queryTimeout, maxIdle reduzido,
  singleton thread-safe, getStackTrace só com TRACE ligado (sem mudar versão do commons-dbcp);
- vazamentos corrigidos (Usuario.alteraUsuario, VeiculoMonitorado.getRemoveStatement,
  CamerasMonitoramentoAoVivo, ExportaAmostras, ExportaComprovacaoImagem) com try-with-resources;
- servlets sem campos mutáveis (Processar, ExportarImagensServlet, ExportarDescargaServlet,
  CadastrarFiltroServlet, DesabilitaFiltroServlet, ReprovarRemessa); SimpleDateFormat → DateTimeFormatter;
- grupo de câmeras por sessão; coleções compartilhadas thread-safe;
- ServletContextListener de desligamento (threads, executores, Quartz); polling só com clientes;
- Quartz: threadCount 10 (quartz.properties), @DisallowConcurrentExecution em todos os jobs, log4j;
- timeouts em todas as chamadas HTTP de saída;
- RetencaoJob: NÃO registrar "SUCESSO" sem expurgo; status SOMENTE_CONTAGEM até haver script de
  expurgo em lotes aprovado (entregar o script em migracoes/ como pendente);
- filtros de data como intervalo semiaberto; teto em Paginacao; N+1 de AlertasOcorrencias;
- log4j root INFO; JsonResposta.erro genérico com id de correlação no lugar de e.getMessage().
```

### Lote 6: Front-end e padrão visual (P2/P3)

```text
LOTE 6 — XSS de saída, bibliotecas e padrão visual.
Achados: FE-01…06, FE-08, FE-09, FE-11…16, FE-20, FE-22…28, FE-31…36; CFG-04/CFG-05 na parte de código
(remover credenciais e chave fixa das JSP/JS; ler a chave do Maps da configuração; câmeras via proxy).
Resultado esperado:
- includes/erro*.jsp, sucesso*.jsp, confirma.jsp, sim_nao.jsp e demais JSPs listadas com escape
  correto por contexto (HTML, atributo, JS); parâmetro de retorno só com caminho relativo;
- função esc() única em muralha-digital/utils/utilidades.js (a partir de Chamados.esc) aplicada em
  todos os arquivos FE-13…16; handlers inline com dado → data-* + addEventListener;
- eval removido (mapa de funções); setInterval/setTimeout sem string;
- jQuery ≥ 3.5 e Bootstrap 5.3.x apenas via cabeçalho nas telas Muralha tocadas; versões de CDN fixadas e SRI;
- alert/confirm → SweetAlert2 (helpers Ui.alerta/Ui.confirmar em utilidades.js);
- arquivos convertidos para UTF-8 com pageEncoding e lang="pt-BR", com conferência visual de acentos;
- remover arquivos *-original.*, *.bak e o .sql servido em assets/.
Não altere a saída GWT compilada (gxt/, GtwMenu/, GtwWidgets/).
```

### Lote 7: Testes, build e limpeza (P2/P4)

```text
LOTE 7 — Qualidade contínua.
Achados: TST-01, TST-02, TST-03, MAN-02, MAN-03, MAN-04, MAN-07, MAN-08, SEC-AUTH-34, SEC-AUTH-35.
Resultado esperado:
- src/test/testng-customsuite.xml executando os testes unitários de muralha.digital.* (excluindo
  integração/HtmlUnit); documentar o comando em docs/requisitos/01-stack-e-ambiente.md;
- testes de regressão de segurança que ainda faltarem (Acesso, ValidaSessao, SenhaService, TOTP,
  AuditoriaService, RetencaoJob, ArquivoSeguro, XmlSeguro);
- ServletBase/JsonResposta para reduzir a duplicação nos servlets tocados (sem refatoração em massa);
- remover mapeamentos mortos do web.xml (/teste e classes inexistentes), AdminServlet e *.jws;
  corrigir a declaração dupla de Inicializacao;
- listar (sem aplicar) as mudanças de pom.xml desejadas: surefire, mockito-all, MaxPermSize,
  compiler 14×13, dependências da §7 das Sugestões — como pedido de aprovação na entrega.
```

---

## 4. Checklist do revisor humano (para cada PR)

- [ ] A entrega tem **"## Origem"** com link real para o plano, e o plano existe.
- [ ] Nenhuma alteração em `pom.xml` ou `.setup-gtw/` (ou há aprovação registrada).
- [ ] Nenhum segredo no diff (`git diff | grep -nE "AIza|password|senha|secret|token"` revisado).
- [ ] Migrações novas estão em `docs/banco-de-dados/migracoes/`, são idempotentes, têm rollback e estão marcadas como pendentes.
- [ ] Todo servlet tocado verifica sessão e `Acesso`, e pega o `idUsuario` da sessão.
- [ ] Sem SQL concatenado com entrada, sem `e.getMessage()` ao cliente, sem campo mutável em servlet.
- [ ] Saída escapada (`<c:out>`/`esc()`), sem `alert()` novo, arquivos em UTF-8.
- [ ] Testes novos falham sem a correção e passam com ela; o resultado está na entrega.
- [ ] O roteiro de teste manual (`docs/testes/NN-*.md`) cobre perfis reais (usuário comum, administrador, sem login).
- [ ] O status dos itens em `12-riscos-e-debitos-tecnicos.md` e no relatório foi atualizado.

---

## 5. Prompt curto, só de code review (sem correção)

Para revisar um PR qualquer contra os padrões levantados nesta auditoria:

```text
Revise o diff deste branch em relação ao main no repositório GTW-CWB, sem alterar arquivos.
Leia antes CLAUDE.md, docs/requisitos/06-padroes-de-codigo.md e
docs/auditoria/2026-10-19-relatorio-auditoria.md.
Aponte, com severidade (Crítica/Alta/Média/Baixa), arquivo:linha, cenário concreto de falha e
correção sugerida, apenas problemas reais de: sessão/permissão ausente ou idUsuario vindo do
request; SQL/comando/caminho/XML com entrada do usuário; XSS de saída (JSP sem escape, innerHTML
sem esc()); segredo no código; vazamento de Connection/Statement/ResultSet; campo mutável em
servlet; exceção engolida ou e.getMessage() devolvido ao cliente; chamada HTTP sem timeout;
alteração em pom.xml/.setup-gtw; DDL/DML fora de docs/banco-de-dados/migracoes/; entrega sem
"## Origem"; alert()/confirm() novos; arquivo não UTF-8. Ignore estilo e preferências.
Se nada relevante for encontrado, diga isso explicitamente.
```
