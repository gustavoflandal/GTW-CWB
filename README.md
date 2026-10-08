# GTW-CWB

Sistema legado de **Gestão de Trânsito WEB (GTW)** da Consilux Tecnologia, voltado à fiscalização e à inteligência de trânsito. O WAR (`com.consilux.gtw:gtw:3.0`, contexto `ROOT`) reúne duas gerações de software no mesmo contexto web:

| Geração | Descrição | Onde está |
|---|---|---|
| **GTW clássico** | JSP + GWT/GXT. Importação e validação de infrações, remessas/AIT, cadastros e relatórios | `src/main/webapp/{cadastro,processo,relatorio,...}`, `gxt/`, pacote `com.consilux.*` |
| **Muralha Digital** | JSP + Bootstrap 5.3 + jQuery. Monitoramento em tempo real, alertas de veículos monitorados, blitz digital, atendimentos, mapas e câmeras | `src/main/webapp/muralha-digital/`, pacote `muralha.digital.*` |

Código novo deve seguir o padrão **Muralha Digital**. O GTW clássico só recebe correções pontuais.

## Stack

- Java 13 (JDK 13.0.1), Maven 3.9, Tomcat 9
- Servlet 4.0, JSP/JSTL 1.2, JDBC, Gson
- SQL Server (banco `GTW_MURALHA_DEV`)
- Front-end: Bootstrap 5.3, Bootstrap Icons, SweetAlert2

O JDK, o Maven e o Tomcat ficam em `D:\GTW-CWB\.setup-gtw\`, uma pasta irmã deste repositório que **não** é versionada. Ela não deve ser alterada.

## Como construir e executar

A partir de `D:\GTW-CWB\GTW-CWB`, no PowerShell:

```powershell
..\.setup-gtw\build.ps1     # clean install -DskipTests; gera o WAR em target\
..\.setup-gtw\run.ps1       # tomcat7:run -> http://localhost:8080/
..\.setup-gtw\debug.ps1 -ProjectName 'GTW-CWB' -DebugPort 5005
```

## Configuração e segredos

- `confGTW.xml` e `muralha-digital-config.xml` (em `WEB-INF`) são locais e ignorados pelo git. Copie os arquivos `.example` correspondentes e ajuste os valores.
- Chaves de integrações (Twilio, SendGrid, Google etc.) são lidas de variáveis de ambiente. Veja [docs/07-configuracao-e-segredos.md](docs/07-configuracao-e-segredos.md).
- Nunca versione segredos. O push é bloqueado pelo secret scanning do GitHub.

## Estrutura

```
src/main/java/
  com/consilux/           núcleo e GTW clássico
  muralha/digital/<mod>/  Muralha Digital (<Entidade>Servlet)
src/main/webapp/
  muralha-digital/pages/  telas novas
  cadastro/ processo/ relatorio/ gxt/ ...   GTW clássico
docs/                     documentação do projeto
docs-editais/             editais e documentos de referência
relatorios/               modelos de relatórios
.claude/                  agentes e prompts do Claude Code
```

## Documentação

O índice completo está em [docs/README.md](docs/README.md). Principais documentos:

- [Visão geral](docs/00-visao-geral.md)
- [Stack e ambiente](docs/01-stack-e-ambiente.md)
- [Arquitetura](docs/02-arquitetura.md)
- [Módulos funcionais](docs/04-modulos-funcionais.md)
- [Padrão visual](docs/05-padrao-visual.md)
- [Padrões de código](docs/06-padroes-de-codigo.md)
- [Segurança e permissões](docs/09-seguranca-e-permissoes.md)
- [Guia de implementação](docs/10-guia-de-implementacao.md)
- [Banco de dados](docs/banco-de-dados/)

## Regras para contribuir

- Não alterar `pom.xml` (versões e dependências) nem `.setup-gtw/` sem aprovação, e não adicionar novos frameworks ou bibliotecas.
- Telas novas seguem o [padrão visual](docs/05-padrao-visual.md).
- Todo código novo exige sessão e permissão, SQL parametrizado, recursos fechados, UTF-8 e textos em português do Brasil.
- O banco `GTW_MURALHA_DEV` é somente leitura sem autorização. Alterações de DDL ou DML entram como script em `docs/banco-de-dados/migracoes/`.
- Commits em português, no imperativo.
