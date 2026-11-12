# Desenho — Captura da ação de Imprimir no Motor de Logs

Data: 2026-11-12
Status: desenho para aprovação (ainda não implementado)
Relacionado: [`motor-captura-logs.md`](motor-captura-logs.md), motor em `src/main/java/muralha/digital/auditoria/`

## Origem

Extensão do Motor de Captura de Logs (auditoria). Não há plano numerado em
`docs/planos-salvador/` para este item; a entrega final deverá referenciar
este desenho e o documento do motor.

## 1. Intenção e porquê

O Motor de Captura de Logs hoje registra automaticamente, em todas as telas,
os eventos de **acesso, visualização, cadastro, alteração, exclusão,
exportação e erro** (`AuditoriaService` + `CapturaLogFilter` + `MotorRegras`).

A **ação de imprimir não é tratada como um evento próprio**:

- **Documentos gerados no servidor** (PDF via Jasper: relatórios, autos/AIT,
  etiquetas, remessas — ~37 servlets) hoje caem genericamente como
  `EXPORTACAO`, misturados com downloads de dados (CSV/XLS), **sem destacar o
  formato nem a tela de onde a impressão foi disparada**.
- **Impressão pelo navegador** (`window.print()` / Ctrl+P, em ~20 telas que já
  usam isso) **é invisível ao motor**: a impressão ocorre sobre a página já
  carregada, sem nova requisição ao servidor. Hoje não existe nenhum registro
  dessa ação.

Para auditoria e conformidade é **vital** saber **quem imprimiu, o quê, em que
formato e a partir de qual tela**. É isso que este desenho adiciona.

## 2. Goals

1. Tratar a impressão como **tipo de evento próprio: `IMPRESSAO`**, distinto de
   `EXPORTACAO`.
2. Registrar, em destaque **no registro do evento**, o **formato** da impressão
   (PDF, impressão de tela) e o **nome do documento/relatório/tela** impresso.
   "Em destaque" = campos próprios dentro do evento (vistos no detalhe da
   consulta), **sem** nova coluna/filtro na grade (decisão do cliente, §9).
3. Registrar a **tela de origem** (de onde o usuário disparou a impressão).
4. Cobrir os **dois caminhos**: documentos gerados no servidor **e** impressão
   pelo navegador.
5. Deixar tudo **configurável pela tela de regras de captura** (`sis_log_regra`)
   e pelo liga/desliga global de tipos (`sis_log_tipo_evento`) — consistente com
   o restante do motor, **sem código por relatório**.
6. **Derivação automática** do formato e do documento/tela, sem cadastro manual.

## 3. Non-goals

- Não capturar o **conteúdo** impresso (o arquivo PDF ou a imagem em si); só o
  **metadado** do evento de impressão.
- Não **bloquear/impedir** impressão.
- Não reescrever os ~37 servlets de relatório: a captura server-side é feita no
  filtro, sem instrumentar cada relatório.
- Não construir nenhuma nova tela de consulta — a consulta de logs já existe
  (`consulta-log.jsp`) e passará a exibir os eventos `IMPRESSAO` como os demais.

## 4. Anti-goals (fracasso mesmo "funcionando")

- A impressão pelo navegador continuar invisível.
- Registrar a impressão **sem** a tela de origem ou **sem** o formato.
- Exigir alteração de código a cada novo relatório.
- Qualquer perda de robustez do motor: a captura (server ou navegador) **jamais**
  pode quebrar a requisição, atrasar a resposta ou impedir a impressão.

## 5. Restrições (constraints)

- Java 13, servlet-api anterior ao 3.0 (porém `@WebServlet`/`@WebFilter` já são
  usados no projeto), SQL Server, Gson. **Sem novas bibliotecas/frameworks.**
- Banco somente `localhost` em dev (trava em `Conexao.validarHostLocal`); DDL/DML
  apenas como script em `docs/banco-de-dados/migracoes/`.
- Padrão visual Bootstrap 5.3 para qualquer UI nova (aqui a UI nova é mínima:
  apenas a opção `IMPRESSAO` na tela de regras).
- Captura **assíncrona e blindada** (padrão já existente no `AuditoriaService`).
- Código novo: sessão + permissão, SQL parametrizado, recursos fechados, UTF-8,
  PT-BR (`docs/requisitos/06-padroes-de-codigo.md`).

## 6. Decisões já tomadas

### 6.1 Novo tipo de evento `IMPRESSAO`
- Inserir o código `IMPRESSAO` em `dbo.sis_log_tipo_evento` (migração), com
  `capturar = 1` por padrão. A tela de tipos já lista os tipos a partir do banco,
  então o novo tipo aparece automaticamente para liga/desliga global.
- `AuditoriaService.derivarNivel(...)` passa a reconhecer `IMPRESSAO`.
- **Retenção:** `IMPRESSAO` **herda a política padrão** do motor (sem política
  própria em `sis_log_retencao`) — decisão do cliente (§9). Cai no catch-all de
  nível sem política específica, como qualquer outro nível sem regra dedicada.

### 6.2 Caminho server-side (documentos gerados — PDF/Jasper/autos)
- No fim do `CapturaLogFilter`/`AuditoriaService.capturarAutomatico`, além do
  método e da ação, passa a considerar o **`Content-Type` da resposta**
  (`response.getContentType()`, disponível no servlet-api do projeto).
- **Classificação:** resposta com Content-Type de documento para impressão
  (`application/pdf`) → nível `IMPRESSAO`. Download de **dados** (CSV/XLSX)
  continua `EXPORTACAO` (decisão do cliente, §9). Uma regra (`sis_log_regra`) com
  nível explícito sempre prevalece sobre essa derivação (igual ao comportamento
  atual).
- **Semântica (decisão do cliente, §9):** para documentos de servidor, o evento
  registra a **geração/abertura do PDF** como a "impressão" do documento. O ato
  físico de clicar em imprimir dentro do visualizador de PDF do navegador não
  chega ao servidor e **não** é capturado (ver Limitações, §10).
- **Formato** derivado do Content-Type: `application/pdf` → "PDF". (Mapa
  Content-Type → rótulo fica como detalhe de implementação, ver §8.)
- **Documento** derivado da `funcionalidade`/rota já calculada hoje
  (`derivarFuncionalidade`), sem instrumentar os relatórios.
- **Tela de origem** derivada do cabeçalho HTTP `Referer` (a tela que o usuário
  estava vendo quando disparou o relatório), com fallback para a própria rota.
- Esses três campos vão em `dados_extra` (ver §6.5).

### 6.3 Caminho navegador (`window.print` / Ctrl+P)
- Um **script JS compartilhado** (`impressao-captura.js`) intercepta a impressão
  de tela: escuta o evento `beforeprint` (e envolve `window.print`), e no
  disparo envia um **`POST` via `navigator.sendBeacon`** (com fallback `fetch`
  keepalive) para um **endpoint novo** `/MuralhaDigital/Impressao`, com:
  `{ telaOrigem: <URL atual + document.title>, formato: "Impressão de tela" }`.
- **Novo servlet** `ImpressaoServlet` (`@WebServlet("/MuralhaDigital/Impressao")`)
  recebe o POST, valida a **sessão** do usuário e registra via
  `AuditoriaService` com nível `IMPRESSAO`. Como é uma **rota**, fica sujeito às
  mesmas `sis_log_regra` (ex.: regra `padrao_rota = /MuralhaDigital/Impressao`,
  nível `IMPRESSAO`), fechando o requisito de "tudo na configuração de regras".
- O gancho JS é incluído no **cabeçalho comum das telas novas**
  (`utils/credenciais/cabecalho_bootstrap_simples.jsp`) e nas telas legadas que
  já chamam `window.print` (as ~20 identificadas). Cobertura universal do legado
  antigo fica como item de acompanhamento (ver §7).

### 6.4 Configuração (requisito central do pedido)
- **Nada hardcoded por relatório.** O que capturar de impressão é controlado por:
  - liga/desliga global do tipo `IMPRESSAO` em `sis_log_tipo_evento`;
  - regras por rota em `sis_log_regra` (ex.: `%/relatorio/%`, `%AcidentePDF%`,
    `/MuralhaDigital/Impressao`) com nível `IMPRESSAO`, `capturar`, `garantido`,
    `ativo`, etc.
- A tela de regras (`logs-regras`) ganha `IMPRESSAO` na lista de níveis
  selecionáveis. A migração faz o **seed** de uma regra para
  `/MuralhaDigital/Impressao` (nível `IMPRESSAO`).

### 6.5 Formato de `dados_extra`
Padroniza um bloco para impressão, preservando o payload atual quando houver:
```json
{ "formato": "PDF", "documento": "Auto de Infração", "telaOrigem": "/muralha-digital/pages/.../index.jsp" }
```
`dados_extra` **já entra no hash encadeado** (`AuditoriaService.hashEvento`),
então os novos campos passam a integrar a cadeia de integridade naturalmente,
sem mudar a função de hash.

#### 6.6 Acesso
- `ImpressaoServlet` exige **sessão autenticada** (qualquer usuário logado pode
  imprimir e, portanto, gerar o evento), mas **não** exige permissão de menu
  específica — é um endpoint interno de registro, não uma tela. **Decisão tomada
  no desenho** (minha escolha): o endpoint só registra logs e não expõe dados,
  logo não entra em `sis_menu` nem no `ControleAcesso` estrito do Plano 35.

## 7. Itens de acompanhamento (fora da fase 1)

- Cobertura universal da impressão de navegador no **legado GTW clássico** antigo
  (telas sem o cabeçalho comum e sem `window.print` explícito).
- Eventual rótulo "amigável" por documento além do derivado da rota (o cliente
  optou por derivação automática; um catálogo manual não está no escopo).

## 8. O que fica a cargo de quem implementar

- Mapa exato `Content-Type` → rótulo de formato (ex.: `application/pdf` → "PDF").
- Ponto exato de inclusão do JS para as telas legadas com `window.print`.
- Conteúdo do seed de regras na migração.
- Redação dos scripts SQL de migração em `docs/banco-de-dados/migracoes/`.
- Se lê `response.getContentType()` direto ou acrescenta um passthrough ao
  `RespostaComStatus`.
- Testes: unitário da derivação de nível/formato; verificação manual dos dois
  caminhos (PDF de relatório e `window.print` de uma tela), confirmando o evento
  `IMPRESSAO` com formato e tela de origem na consulta de logs.

## 9. Decisões confirmadas pelo cliente

1. **Geração = impressão** para documentos de servidor: registra-se o momento em
   que o PDF é gerado/aberto como a "impressão" do documento.
2. **Exibição só no detalhe**: formato/documento/tela de origem ficam em
   `dados_extra`, vistos ao abrir o evento na consulta — **sem** nova coluna ou
   filtro na grade. Entrega de back-end.
3. **Retenção herda a padrão**: `IMPRESSAO` sem política própria.
4. **Fronteira PDF × planilha**: `application/pdf` → `IMPRESSAO`; CSV/XLSX →
   `EXPORTACAO`. Regras por rota podem sobrescrever caso a caso.

## 10. Limitações conhecidas

- **Documentos de servidor (PDF):** captura-se a **geração/abertura** do PDF, não
  o ato físico de imprimir dentro do visualizador. Isso implica:
  - *falso-positivo possível:* gerou o PDF e não imprimiu → mesmo assim registra;
  - *falso-negativo possível:* reabriu/imprimiu um PDF já salvo → não registra.
  Aceito pelo cliente (§9.1) como proxy adequado.
- **Impressão de tela (`window.print`/Ctrl+P):** o gancho registra a **intenção**
  de imprimir (disparo do `beforeprint`), mesmo que o usuário cancele o diálogo
  de impressão — o navegador não informa o resultado final.
- **Legado antigo sem cabeçalho comum:** telas que não incluem o cabeçalho e não
  chamam `window.print` ficam fora da fase 1 (ver §7).
