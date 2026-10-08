# 03 — Estrutura do Projeto e Onde Colocar Cada Coisa

## 1. Raiz (`D:\GTW-CWB\GTW-CWB`)

| Caminho | Conteúdo | Observação |
|---|---|---|
| `pom.xml` | Build Maven (`war`, `com.consilux.gtw:gtw:3.0`) | Não alterar sem aprovação |
| `src/main/java` | Código Java (~960 arquivos) | ver §2 |
| `src/main/resources` | `log4j.xml`, `quartz.properties`, `imagens/logo_*.png` (logos de clientes) | |
| `src/main/webapp` | Raiz web (JSP, JS, CSS, imagens, WEB-INF) | ver §3 |
| `src/test` | Testes (TestNG; 6 `.java`) | cobertura mínima |
| `relatorios/` | 57 modelos JasperReports `.jrxml` (AIT CET/URBS, PREM, fluxo, etiquetas, validação…) | compilados em tempo de execução |
| `nginx-1.26.2/` | nginx (porta 8185 → Tomcat 64000; proxy de câmeras RTSP/WebSocket) | |
| `ffmpeg/` | `ffmpeg.exe` (ignorado pelo git; 108 MB) | baixar separadamente |
| `ip_permitidos.txt`, `.checkstyle`, `.pmd`, `.project`, `.settings`, `.externalToolBuilders` | Artefatos Eclipse/checkstyle/PMD | |
| `target/` | Saída de build (ignorado) | |
| `docs/` | **Esta documentação** | |

## 2. Código Java (`src/main/java`)

| Pacote raiz | Arquivos | Papel |
|---|---|---|
| `com.consilux.model` | 253 | Núcleo de negócio do GTW clássico: `Usuario`, `Acesso`, `Infracao*`, `Remessa*`, `Enquadramento`, `Cadastro*`, `Job*` (Quartz), `Acesso{FTP,SFTP,Compartilhamento}`, `ExportaRemessa*`… |
| `com.consilux.servlet` | 159 | Servlets do GTW clássico por área: `ajax`, `processamento`, `remessa`, `relatorio`, `ferramentas`, `medicao`, `ait`, `notificacao`, `log`… |
| `com.consilux.infra` | 36 | Infra: `SessaoConstantes`, `SessoesAtivas`, `SessaoFinaliza*`, `ValidaSessao` (filtro), `CacheFilter`, `Funcoes` (utilitários 47 KB), `ServicoEmail`, criptografia AES, gráficos |
| `com.consilux.ws` | 26 | Web services Axis: `server/` (serviços expostos) e `client/` (stubs) |
| `com.consilux.conf` | 19 | Configuração: `ConfiguracaoXML` (lê `confGTW.xml`), `Configuracao*`, `Versao` |
| `com.consilux.lib` | 14 | `Conexao` (pool), `ConnectionAdapter`, `GenericCache`, `RelatorioVisual`, `Grafico` |
| `com.consilux.ui` | 14 | Implementações servidoras de GWT-RPC (`ui.server.*ServiceImpl`) |
| `com.consilux.menu`, `.log`, `.exportalista`, `.rest`, `.Utils` | 1–3 cada | Menu GWT, log de tempo, exportação, recurso REST |
| `com.abertura.sistemas` | 1 | `SistemasServlet` (`/Abertura/SistemasConsilux`) |
| `muralha.digital.*` | ~436 | **Muralha Digital** — um subpacote por módulo (ver `04-modulos-funcionais.md`) |
| `muralha.configuracaoequipamento` | — | Servlets de configuração de equipamento |

Estrutura típica de um módulo Muralha (`muralha.digital.<modulo>`):

```
<Entidade>Servlet.java   ← @WebServlet("/MuralhaDigital/<Entidade>"), despacha por ?acao=
<Entidade>.java          ← bean/POJO + métodos estáticos de acesso a dados (JDBC)
<Entidades>.java         ← coleção/listagens e consultas agregadas
<Entidade>Result.java    ← wrappers de resposta (alguns módulos)
```

Inventário completo: `referencia/inventario-pacotes-java.md` e `referencia/modulos-backend-muralha.md`.

## 3. Web (`src/main/webapp`)

| Caminho | Geração | Conteúdo |
|---|---|---|
| `muralha-digital/pages/<tela>/` | 2 | Telas do Muralha (44 diretórios). Cada uma: `*.jsp` (+ `modal-*.jsp`), `assets/js/`, `assets/css/` |
| `muralha-digital/assets/` | 2 | Bibliotecas locais (`MDB-Free`, `bootstrap5`, `datepicker` (Tempus Dominus/moment), `jquery`, `css`, `js`, `images`, `sounds`) |
| `muralha-digital/utils/` | 2 | Cabeçalhos (`credenciais/cabecalho_*.jsp`), modais genéricos (`modal-info-alert`), notificações (`notificacao/` toastr), paginação (`paginacao/`), bootbox, `maps-config.js`, `export-table.js` |
| `muralha-digital/relatorios/` | 2 | Páginas de relatórios Muralha |
| `login/` | comum | Login, abertura de sistemas, recuperação/troca de senha, Google login, imagens de fundo |
| `includes/` | 1 | `cabecalho.jsp` (GTW clássico), `rodape.jsp`, `erro*.jsp`, `sucesso*.jsp`, `confirma.jsp`, `sim_nao.jsp` |
| `css/` | 1 | `gtw.css` (tema Verdana), `GtwMenu.css`, `GtwWidgets.css`, `gtw-gwt.css`, `calendario.css`, jQuery UI |
| `cadastro/ ferramenta/ processo/ relatorio/ infracao/ remessa/ medicao/ descarga/ monitoramento/ notificacao/ amostras/ manutencao/ controleAIT/ configuracoesequipamento/ acesso_remoto/ ait/ usuario/` | 1 | JSPs do GTW clássico por área funcional |
| `gxt/`, `GtwMenu/`, `GtwWidgets/`, `GtwClientLogger/` | 1 | Saída compilada GWT/GXT (**não editar à mão**; fontes GWT não estão no repositório) |
| `images/`, `js/`, `utils/`, `zoom-master/`, `video/`, `wsdl/` | 1 | Estáticos e utilitários legados |
| `WEB-INF/` | — | `web.xml`, `server-config.wsdd`, TLDs, `confGTW.xml` (local), `muralha-digital-config.xml` (local), `*.example`, serviços Axis (`InfoEquipamentoService`, …), `templates/*.jsp`, `relatorio/*.jasper` |
| `META-INF/` | — | `context.xml`, `MANIFEST.MF` |

## 4. Onde colocar o código novo

| Quero… | Coloque em |
|---|---|
| Nova tela Muralha | `webapp/muralha-digital/pages/<nome-em-kebab-case>/<nome>.jsp` + `assets/js/<nome>.js` + `assets/css/<nome>.css` |
| Modal reutilizado por várias telas | `webapp/muralha-digital/pages/<dono>/modal-<nome>.jsp` (+ `.js`) e `<%@ include %>` nas telas |
| Componente comum | `webapp/muralha-digital/utils/<componente>/` (ou `assets/js` se for apenas JS) |
| Novo endpoint | `muralha.digital.<modulo>.<Entidade>Servlet` (`/MuralhaDigital/<Entidade>`) |
| Regra/consulta de dados | métodos na classe `<Entidade>`/`<Entidades>` do mesmo pacote, ou procedure/função em `muralha.*` |
| Novo job | classe `Job*` em `muralha.digital.<modulo>` + registro em `Agendador` |
| Novo parâmetro configurável | `muralha.config_chave_valor` (preferencial) ou `muralha-digital-config.xml` |
| Relatório Jasper | `relatorios/<Nome>.jrxml` + servlet em `com.consilux.servlet.relatorio` |

Nomenclatura: diretórios de tela em `kebab-case` (exceções legadas: `registro_fato`, `perguntasRespostas`); classes Java em `PascalCase` em português; pacotes em minúsculas; JSON/colunas em `snake_case`; ações (`acao`) em `camelCase` verbo+objeto (`listarGuarnicoes`, `obterAlertaPorId`).
