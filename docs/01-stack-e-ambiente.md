# 01 — Stack, Ambiente, Build e Execução

> **A stack de desenvolvimento é imutável.** Toda a stack vive em `.setup-gtw/` (pasta irmã deste projeto: `D:\GTW-CWB\.setup-gtw`). Nada ali pode ser alterado, atualizado ou substituído. Esta página documenta o que existe para que novos desenvolvimentos respeitem os limites.

> Documentação específica da stack também em `D:\GTW-CWB\.setup-gtw\STACK.md` (scripts, settings, restauração do ambiente).

## 1. Estrutura do workspace

```
D:\GTW-CWB\                      ← workspace (repositório git local sem commits; NÃO é o repositório do GitHub)
├── .setup-gtw\                  ← STACK DE DESENVOLVIMENTO (NÃO ALTERAR; não vai para o GitHub)
│   ├── jdk-13.0.1\              ← JDK usado por build e execução
│   ├── apache-maven\            ← Maven 3.9.9
│   ├── apache-tomcat-9.0.91\    ← Tomcat 9 standalone (deploy do WAR)
│   ├── m2.7z                    ← cache de repositório Maven (artefatos legados/internos)
│   ├── settings-legacy-archiva.xml  ← settings.xml do Maven (Central + Archiva legado HTTP + Jaspersoft)
│   ├── setup-java-maven.ps1     ← define JAVA_HOME/PATH só na sessão
│   ├── gtw.ps1  build.ps1  run.ps1  debug.ps1   ← wrappers (build / run / debug)
│   └── README.md
└── GTW-CWB\                     ← ESTE PROJETO (repositório git → github.com/gustavoflandal/GTW-CWB, branch main)
    ├── pom.xml, src\, docs\, relatorios\, nginx-1.26.2\, ffmpeg\, target\ ...
```

## 2. Versões e bibliotecas principais

| Item | Versão / Observação |
|---|---|
| Java (build/run) | **JDK 13.0.1** (`maven-compiler-plugin` com `source/target 13`; propriedade `maven.compiler.*` = 14 é ignorada pelo plugin) |
| Maven | instalado: 3.9.6 (scripts/README citam 3.9.9) com `settings-legacy-archiva.xml` — detalhes em `.setup-gtw\STACK.md` |
| Servlet API | 4.0 (web.xml 4.0), JSP/JSTL 1.2, taglibs `c.tld/fmt.tld/fn.tld/sql.tld` locais |
| Execução local | `tomcat7-maven-plugin 2.2` (`tomcat7:run`, porta **8080**, path `/`, URIEncoding UTF-8) |
| Container de deploy | Tomcat 9.0.91 em `.setup-gtw` (a API `provided` do pom é `tomcat-servlet-api 7.0.52`) |
| Banco | SQL Server (JDBC `mssql-jdbc 9.2.1.jre8` + `commons-dbcp 1.4` pool; SQL Server 2016 SP2 no DEV) |
| Front legado | GWT 2.1.0 / GXT 2.2.1 (somente runtime compilado; **fontes GWT não estão neste repositório**) |
| Front atual | Bootstrap 5.3.x + jQuery 3.6 + Font Awesome 5.15 + Bootstrap Icons 1.x + SweetAlert2 11 + bootstrap-select 1.14 + Tempus Dominus 6.7.7 + DataTables 1.11 + Chart.js + Google Maps JS / Leaflet 1.9 + SheetJS/jsPDF (exportação) |
| JSON | Gson 2.8.8 (padrão nos servlets Muralha); Jackson 2.12.5 também presente |
| Agendamento | Quartz 2.3.2 (RAMJobStore, 4 threads — `quartz.properties`) |
| Relatórios | JasperReports 6.10.0 (`.jrxml` em `relatorios/`), iText 2.1.7, POI 4.1.1, JXL 2.6.12, JFreeChart 1.0.19 |
| SOAP | Apache Axis 1.4 (`server-config.wsdd`) |
| Log | log4j 1.2.17 (`src/main/resources/log4j.xml`) |
| Mídia | FFmpeg (`ffmpeg/ffmpeg.exe`, ignorado no git), `jave-core 2.7.3`, `webp-imageio` |
| Mensageria externa | Twilio 8.21.0, SendGrid 4.7.2, Facilita Móvel SDK, Unirest 1.4.9 |
| Testes | TestNG 6.8.21, Mockito 1.10.19, HtmlUnit 2.36.0 (cobertura praticamente nula — 6 `.java` em `src/test`) |
| Proxy de câmeras | nginx 1.26.2 em `nginx-1.26.2/` (porta 8185 → Tomcat 64000, proxy para câmeras RTSP-over-WebSocket) |

> **Não adicionar** dependências novas ao `pom.xml` sem aprovação explícita; não migrar para Java > 13 nem para outro container; não introduzir frameworks de front (React/Vue/Angular) nem build de front (npm/webpack).

## 3. Como compilar, executar e depurar

Execute de dentro de `D:\GTW-CWB\GTW-CWB` (ou use `-ProjectName 'GTW-CWB'`). Os wrappers configuram `JAVA_HOME`/`PATH` apenas para a sessão.

```powershell
# Build (clean install, sem testes, sem exec-maven-plugin)
..\.setup-gtw\build.ps1
# Saída: target\GTW_MURALHA_DIGITAL\gtw-3.0-r<buildNumber>\ROOT.war

# Run (tomcat7:run em foreground) → http://localhost:8080/
..\.setup-gtw\run.ps1

# Debug (JDWP porta 5005; -Suspend para esperar o debugger)
..\.setup-gtw\debug.ps1 -ProjectName 'GTW-CWB' -DebugPort 5005
```

Equivalente Maven: `mvn -s ..\.setup-gtw\settings-legacy-archiva.xml clean install -DskipTests -Dexec.skip=true -U -Dmaven.wagon.http.allowInsecure=true ...`.

Particularidades do build:
- `buildnumber-maven-plugin` gera `${buildNumber}` na fase `validate` (`doCheck/doUpdate=false`) — o nome do WAR inclui o número.
- `maven-antrun-plugin` e `exec-maven-plugin` (xdelta, gera `.xwar`) rodam na fase `install`; **sempre** use `-Dexec.skip=true` (o `xdelta` não existe no ambiente).
- Dependências internas `gtw-widgets-muralha-digital:1.0.0` e `descarga-core:1.0.2` vêm do cache `.m2` (restaurado de `m2.7z`) / Archiva legado; sem elas o build falha.
- Se um artefato quebrar: apagar `_remote.repositories` e `*.lastUpdated` do artefato em `%USERPROFILE%\.m2\repository` (ver README de `.setup-gtw`).

## 4. Arquivos de configuração locais (não versionados)

| Arquivo | Para quê | Template versionado |
|---|---|---|
| `src/main/webapp/WEB-INF/confGTW.xml` | Conexão SQL Server (host/db/user/senha), mapas, processamento, SMTP, grupos, remessas, etc. | `confGTW.xml.example` |
| `src/main/webapp/WEB-INF/muralha-digital-config.xml` | Flags e credenciais do Muralha (SMS Twilio/Facilita/SmsDev/Comtele, Bitly, geocoding, diretórios FFmpeg, regras de consulta de placa, equipamentos/câmeras) | `muralha-digital-config.xml.example` |
| Variáveis de ambiente | `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `SENDGRID_API_KEY`, `TWILIO_TEST_ACCOUNT_SID`, `TWILIO_TEST_AUTH_TOKEN` | — |

Detalhes em `07-configuracao-e-segredos.md` (veja também `08-integracoes.md`).

## 5. Ambiente de banco (DEV)

- Servidor `10.0.0.200`, banco `GTW_MURALHA_DEV`, usuário `consilux` (senha **não** documentada; está em `confGTW.xml` local).
- Para consultas manuais: `sqlcmd` (ODBC 17) com `-C` (confia no certificado) e `SQLCMDPASSWORD` em variável de ambiente; ou `System.Data.SqlClient` no PowerShell (`TrustServerCertificate=True;Encrypt=False`).
