# CLAUDE.md — GTW-CWB

Sistema legado Java/JSP (Tomcat, SQL Server) da Consilux: **GTW clássico** + **Muralha Digital**. Documentação completa em [`docs/README.md`](docs/README.md); regras para agentes em [`docs/agentes/AGENTS.md`](docs/agentes/AGENTS.md).

## Regras que nunca mudam
- **Não alterar** `D:\GTW-CWB\.setup-gtw\` (JDK 13.0.1, Maven, Tomcat 9, scripts) nem `pom.xml` (versões/dependências) sem aprovação explícita. Sem novos frameworks/bibliotecas.
- **Padrão visual obrigatório**: `docs/05-padrao-visual.md` (Bootstrap 5.3, navbar `#0d75bf`, Bootstrap Icons, SweetAlert2…).
- **Sem segredos no git.** `confGTW.xml` e `muralha-digital-config.xml` (WEB-INF) são locais e ignorados; use os `.example`. Chaves via variáveis de ambiente (`docs/07-configuracao-e-segredos.md`).
- **Banco** `GTW_MURALHA_DEV` (10.0.0.200): somente leitura sem autorização; DDL/DML só como script em `docs/banco-de-dados/migracoes/`.
- Código novo: sessão + permissão (`docs/06-padroes-de-codigo.md` §1.1), SQL parametrizado, recursos fechados, UTF-8, PT-BR.

## Comandos (a partir de `D:\GTW-CWB\GTW-CWB`)
```powershell
..\.setup-gtw\build.ps1     # clean install -DskipTests (WAR em target\GTW_MURALHA_DIGITAL\...)
..\.setup-gtw\run.ps1       # tomcat7:run → http://localhost:8080/
..\.setup-gtw\debug.ps1 -ProjectName 'GTW-CWB' -DebugPort 5005
```

## Mapa rápido
- Back-end novo: `src/main/java/muralha/digital/<modulo>/` (`<Entidade>Servlet` em `/MuralhaDigital/<Entidade>?acao=…`, JSON/Gson, JDBC via `Conexao.getConexao()`).
- Telas novas: `src/main/webapp/muralha-digital/pages/<tela>/` (+ `assets/js|css`), incluindo `utils/credenciais/cabecalho_bootstrap_simples.jsp`.
- Legado (só correções pontuais): `com.consilux.*`, `webapp/{cadastro,processo,relatorio,…}`, `gxt/`, `GtwMenu/`, `GtwWidgets/` (saída GWT compilada).
- Índices: `docs/referencia/` (servlets, módulos, telas, pacotes, menus); banco: `docs/banco-de-dados/`.

## Git
Repositório do projeto = esta pasta (`origin` → github.com/gustavoflandal/GTW-CWB, branch `main`). O push é bloqueado pelo secret scanning se houver segredo — não usar bypass. Commits em português, imperativo.
