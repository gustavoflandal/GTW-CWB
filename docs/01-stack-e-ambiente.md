# Stack e Ambiente de Desenvolvimento — GTW-CWB

> **Versões são imutáveis.** Nenhuma ferramenta, dependência ou configuração desta pasta pode ser alterada sem aprovação explícita do responsável pelo projeto. Qualquer alteração em `.setup-gtw/` ou `pom.xml` é proibida por padrão (ver `CLAUDE.md`).

---

## Ferramentas do `.setup-gtw/`

Toda a stack de desenvolvimento está isolada em `D:\GTW-CWB\.setup-gtw\`. Os scripts configuram `JAVA_HOME` e `MAVEN_HOME` na sessão PowerShell antes de executar — não é necessário instalar nada globalmente.

| Ferramenta | Versão | Localização em `.setup-gtw/` |
|---|---|---|
| **JDK** | **13.0.1** | `jdk-13.0.1/` |
| **Apache Maven** | **3.9.9** | `apache-maven/` (baixado automaticamente se ausente) |
| **Apache Tomcat** | **9.0.91** | `apache-tomcat-9.0.91/` |
| **Maven settings legado** | — | `settings-legacy-archiva.xml` |
| **Maven repository local** | — | `m2/.m2/repository/` |

### JDK 13.0.1

- Caminho: `.setup-gtw\jdk-13.0.1\`
- `JAVA_HOME` configurado por `setup-java-maven.ps1`
- O `pom.xml` compila com `source=13` e `target=13` (maven-compiler-plugin 3.8.1)
- **Não usar** JDK 14+ — o pom.xml tem propriedade `maven.compiler.source=14` comentada mas o plugin efetivo usa 13

### Apache Maven 3.9.9

- Caminho: `.setup-gtw\apache-maven\`
- Se a pasta não existir, `setup-java-maven.ps1` baixa e extrai automaticamente do Apache CDN
- `MAVEN_HOME` configurado na sessão pelo mesmo script
- Settings ativo: `.setup-gtw\settings-legacy-archiva.xml` (passado via `-s` em todo build)
- O settings desabilita o HTTP-blocker do Maven 3.8+ para permitir o repositório HTTP legado da Consilux (`http://arquivos.consilux.com.br:8183/`)

### Apache Tomcat 9.0.91

- Caminho: `.setup-gtw\apache-tomcat-9.0.91\`
- Porta HTTP: **8080** (definida em `conf/server.xml` e `pom.xml` → tomcat7-maven-plugin)
- Porta de shutdown: **8005**
- Contexto da aplicação: `/` (ROOT) — `<gtw.contexto>ROOT</gtw.contexto>` no pom.xml
- Configuração de sessão: **240 minutos** de timeout (definido em `webapps/GTW-CWB/WEB-INF/web.xml`)
- Contexto customizado: `webapps/GTW-CWB/META-INF/context.xml` (desabilita `Pragma: no-cache`)
- **Os arquivos de configuração do Tomcat instalado em `.setup-gtw` NÃO são usados durante o `run.ps1`** — o Tomcat embutido do `tomcat7-maven-plugin` usa a configuração do `pom.xml` e o `src/main/webapp/`

---

## Scripts de build e execução

Todos em `.setup-gtw\`. Devem ser chamados a partir da pasta `D:\GTW-CWB\` (ou com `-ProjectName GTW-CWB`).

### `build.ps1` — compilar e gerar WAR

```powershell
# Da pasta D:\GTW-CWB\:
..\.setup-gtw\build.ps1 -ProjectName 'GTW-CWB'
```

Executa: `mvn clean install -DskipTests -Dexec.skip=true -U -s settings-legacy-archiva.xml`  
Resultado esperado: `BUILD SUCCESS`  
WAR gerado em: `target\GTW_MURALHA_DIGITAL\<versao>\ROOT.war`

### `run.ps1` — subir o servidor local

```powershell
..\.setup-gtw\run.ps1 -ProjectName 'GTW-CWB'
```

Executa: `mvn tomcat7:run -Dexec.skip=true -e -X -s settings-legacy-archiva.xml`  
URL: `http://localhost:8080/`  
O servidor fica em foreground; `Ctrl+C` para parar.

### `debug.ps1` — subir com depurador remoto

```powershell
..\.setup-gtw\debug.ps1 -ProjectName 'GTW-CWB' -DebugPort 5005
# Com JVM suspensa (espera attach do IDE antes de iniciar):
..\.setup-gtw\debug.ps1 -ProjectName 'GTW-CWB' -DebugPort 5005 -Suspend
```

Configura `MAVEN_OPTS` com JDWP: `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005`  
Conectar no IDE: Remote Debug → `localhost:5005`

### `gtw.ps1` — orquestrador genérico (não chamar diretamente)

Script interno chamado pelos três anteriores. Responsável por:
1. Chamar `setup-java-maven.ps1` (configura `JAVA_HOME` e `MAVEN_HOME`)
2. Localizar o `pom.xml` do projeto
3. Montar os argumentos Maven e executar

### `setup-java-maven.ps1` — configurar ambiente Java/Maven

Chamado automaticamente pelo `gtw.ps1`. Configura `JAVA_HOME` e `PATH` para o JDK 13.0.1 e o Maven 3.9.9 da pasta local, sem alterar o sistema operacional.

---

## Configurações sensíveis — arquivos locais (não versionados)

| Arquivo | Localização | Conteúdo |
|---|---|---|
| `confGTW.xml` | `WEB-INF/` (dentro do Tomcat ou do `src/main/webapp/WEB-INF/`) | Conexão com banco, parâmetros GTW clássico |
| `muralha-digital-config.xml` | `WEB-INF/` | Parâmetros da Muralha Digital (SMS, e-mail, etc.) |

Esses arquivos estão no `.gitignore`. Use os arquivos `.example` correspondentes como template. Segredos reais (senhas, chaves de API) devem ser definidos via variáveis de ambiente — ver `docs/07-configuracao-e-segredos.md`.

---

## Dependências principais do `pom.xml`

> **Não alterar versões.** A lista abaixo é referência; o `pom.xml` é a fonte de verdade.

| Dependência | Versão | Uso |
|---|---|---|
| `com.google.code.gson:gson` | **2.8.8** | Serialização JSON em todos os servlets |
| `org.quartz-scheduler:quartz` | **2.3.2** | Jobs agendados (Agendador.java) |
| `log4j:log4j` | **1.2.17** | Logging (Logger.getLogger) |
| `com.microsoft.sqlserver:mssql-jdbc` | **9.2.1.jre8** | Driver JDBC SQL Server |
| `commons-dbcp:commons-dbcp` | **1.4** | Pool de conexões (Conexao.getConexao()) |
| `org.apache.commons:commons-pool2` | **2.4.3** | Pool subjacente ao DBCP |
| `net.sf.jasperreports:jasperreports` | **6.10.0** | Relatórios |
| `com.google.gwt:gwt-user` | **2.1.0** | GWT (GTW clássico — não usar em código novo) |
| `com.extjs:gxt` | **2.2.1** | GXT (GTW clássico — não usar em código novo) |
| `javax.websocket:javax.websocket-api` | **1.1** | WebSocket (Muralha Digital) |
| `com.twilio.sdk:twilio` | **8.21.0** | SMS via Twilio |
| `com.fasterxml.jackson.core:jackson-databind` | **2.12.5** | Jackson (usado pelo Twilio) |
| `org.apache.httpcomponents:httpclient` | **4.5.10** | HTTP client interno |
| `commons-fileupload:commons-fileupload` | **1.4** | Upload de arquivos |
| `org.apache.poi:poi` | **4.1.1** | Manipulação XLS |
| `org.apache.poi:poi-ooxml` | **4.1.1** | Manipulação XLSX |
| `com.lowagie:itext` | **2.1.7** | Geração de PDF |
| `commons-io:commons-io` | **2.6** | Utilitários de I/O |
| `org.apache.commons:commons-lang3` | **3.9** | Utilitários de String/Object |
| `org.apache.axis:axis` | **1.4** | Web Services SOAP (ConfigEquipApp) |
| `javax.mail / com.sun.mail` | **1.6.2** | E-mail |
| `ws.schild:jave-core` + `jave-nativebin-win64` | **2.7.3** | Conversão de vídeo (ffmpeg wrapper) |
| `org.sejda.imageio:webp-imageio` | **0.1.6** | Suporte a imagens WebP |

### Dependências internas Consilux (repositório privado Archiva)

| Dependência | Versão | Uso |
|---|---|---|
| `com.consilux.gtw:gtw-widgets-muralha-digital` | **1.0.0** | Widgets GWT/GXT da Muralha |
| `com.consilux.gtw:descarga-core` | **1.0.2** | Lógica de descarga de dados |
| `facilita-movel-sdk:facilita-movel-sdk` | **1** | SDK SMS Facilita Móvel |

> Essas dependências ficam no repositório privado `http://arquivos.consilux.com.br:8183/`. O `settings-legacy-archiva.xml` é necessário para que o Maven as baixe.

---

## Repositório Maven legado

O projeto usa um repositório Archiva interno da Consilux via HTTP (não HTTPS). Por isso:

- Os scripts passam `-Dmaven.wagon.http.allowInsecure=true` e flags relacionados
- O `settings-legacy-archiva.xml` desabilita o HTTP-blocker padrão do Maven 3.8+
- O repositório local fica em `.setup-gtw\m2\.m2\repository\` (não em `~/.m2/`)

---

## Propriedades do projeto (pom.xml)

| Propriedade | Valor |
|---|---|
| `groupId` | `com.consilux.gtw` |
| `artifactId` | `gtw` |
| `version` | `3.0` |
| `packaging` | `war` |
| `gtw.contrato` | `GTW_MURALHA_DIGITAL` |
| `gtw.contexto` | `ROOT` |
| `maven.compiler.source/target` (efetivo) | `13` (via plugin, sobrescreve a propriedade 14) |
| `tomcat7-maven-plugin` porta | `8080` |
| `session-timeout` | `240` minutos |

---

## Restrições para subagentes

1. **Nunca editar** `.setup-gtw/` — qualquer alteração quebra o ambiente de todos os desenvolvedores
2. **Nunca editar** `pom.xml` sem aprovação — versões de dependências são congeladas
3. **Nunca adicionar** novas dependências Maven — usar apenas o que já está no `pom.xml`
4. **Usar apenas** APIs do JDK 13 e das bibliotecas listadas acima
5. **Build de verificação** após qualquer alteração de código Java: `..\.setup-gtw\build.ps1 -ProjectName 'GTW-CWB'` deve terminar com `BUILD SUCCESS`
6. **Não é necessário** instalar JDK, Maven ou Tomcat globalmente — tudo está em `.setup-gtw/`
