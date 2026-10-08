# 07 — Configuração e Segredos

## 1. Regras
1. **Nenhuma credencial, token, chave de API ou senha no repositório** (código, XML, JS, SQL, docs, commits).
2. Valores reais ficam em: (a) arquivos locais ignorados pelo git; (b) variáveis de ambiente do processo Tomcat; (c) tabelas de configuração do banco (para parâmetros de negócio, não segredos).
3. Quem precisar de uma nova credencial adiciona o **nome** da variável/tag em um template `.example` e documenta aqui — nunca o valor.
4. Chaves que já passaram por código/arquivos deste projeto devem ser consideradas comprometidas e **rotacionadas** (ver `12-riscos-e-debitos-tecnicos.md`).

## 2. Arquivos de configuração (WEB-INF)

### 2.1 `confGTW.xml` (local) — modelo `confGTW.xml.example`
Lido por `com.consilux.conf.ConfiguracaoXML` (servlet `Configuracao`, `load-on-startup 2`) e exposto por `ConfiguracaoProvider.getInstance()`.

| Seção | Conteúdo |
|---|---|
| `armazenamento/servidor_sql` | `host`, `database`, `user`, `password` (**segredo**) do SQL Server; `importador_txt/diretorio` |
| `exporta_trafego`, `exporta_imagens` | Jobs de exportação: `ativo`, comandos de logon/logoff, `diretorio`, `horario_job` (HH:mm), janelas de dias retroativos |
| `mapa` | `centro/latitude|longitude`, `google_maps_key` (**segredo**) |
| `processamento` | Flags do processamento de infração (`com_obliteracao`, `com_ajuste_imagem`, `com_uf_validacao`, `alerta_velocidade`, `tamanho_tarja`…) |
| `grupos` | Grupos de usuários especiais (IDs) |
| `config_chave_valor` | ~60 pares chave/valor de comportamento (ex.: `senha_ftp`, `senha_smb` — **segredos**) |
| `remessa` | `tipos`, `gerador`, `exportador` (classes por órgão: CET, URBS…) |
| `email` | `smtp_servers/smtp_server` (`host`, `port`, `user`, `password` — **segredo**), `assunto_veiculo_monitorado` |
| `webservice` | Endpoints de web services externos (`wss`) |
| `descarga` | Módulo de descarga: `ativo`, `app_jar`, `diretorio_saida`, `tamanho_midia`, `horario_job_exportacao` |

### 2.2 `muralha-digital-config.xml` (local) — modelo `muralha-digital-config.xml.example`
Lido uma vez por `Inicializacao.ObterConfiguracao` (`/WEB-INF/muralha-digital-config.xml`); valores vão para campos `public static` de `muralha.digital._ini.Inicializacao`. **Tag vazia → `null`** (recurso desligado).

| Bloco | Tags |
|---|---|
| `Alertas`, `VeiculoTempoReal`, `BlitzEletronica`, `BlitzDigital` | `ativo` (0/1); BlitzDigital: `diretorio_documentos`, `diretorio_imagens`, `api_key_geocoding` (**segredo**) |
| `NotificacaoMuralha` | `EmailAtivo`, `SmsAtivo`, `IntervaloExecucaoEmailMinutos`, `IntervaloExecucaoSmsMinutos`, `LinkPaginaDetalheNotificacao` |
| `IntegracaoSMS/Twilio` | `AccountSID`, `AuthToken`, `MessagingServiceSID`, `NotifyServiceSID` (**segredos**) |
| `IntegracaoSMS/FacilitaMovel` | `FacilitaMovelUser`, `FacilitaMovelPassword` (**segredos**) |
| `IntegracaoSMS/SmsDev` | `SmsDevKey` (**segredo**) |
| `IntegracaoSMS/Comtele` | `ComteleEndpoint`, `ComteleAuthKey` (**segredo**) |
| `BitlyUrlShortener` | `BitlyShortenEndpoint`, `BitlyToken` (**segredo**), `BitlyDomain`, `BitlyGroupGuid` |
| `VideosMonitoramento` | `TempoMaximoConsultaEmMinutos`, `DirFFMPEG`, `DirExeFFMPEG`, `DirTemporarioVideos`, `FormatoVideoTemp` |
| `ConsultaVeiculos` | `LimiteConsultaAtivo`, `LimiteConsultaEmSegundos`, `TamanhoMinimoPlaca`, `ExigirPlacaCompleta`, `QtdeMaxCaracterEspecialPlaca` |
| `VeiculoMonitorado` | `ExportarCadMonitorado`, `QtdeMaxCaracterEspecialPlacaCadMon` |
| `equipamentos/equipamento` | `idLocal`, `serieEquipamento`, `nome` + `monitoramento` (`ip`, `iplocal`, `qualidade`, `framerate`, `resolution`, `tp` (ex.: `pumatronix`), `pista`) — câmeras para monitoramento ao vivo |

Obs.: caminhos `E:\Consilux\MuralhaDigital\…` e `C:\Consilux\…` são do servidor de produção; ajuste localmente.

### 2.3 Outros
| Arquivo | Uso |
|---|---|
| `src/main/resources/log4j.xml` | Logs (ver `02-arquitetura.md` §10) |
| `src/main/resources/quartz.properties` | Quartz (RAMJobStore, 4 threads) |
| `WEB-INF/server-config.wsdd` | Serviços SOAP Axis |
| `ip_permitidos.txt` | Lista de IPs permitidos (raiz do projeto) |
| `nginx-1.26.2/conf/nginx.conf` | Porta 8185, proxy para Tomcat `127.0.0.1:64000`, proxy de câmeras |
| `META-INF/context.xml` | Contexto Tomcat |
| `src/test/resources/confGTW-teste.xml` | Config de teste (senhas em branco no repositório) |

## 3. Variáveis de ambiente (processo do Tomcat)

| Variável | Quem lê | Para quê |
|---|---|---|
| `GOOGLE_CLIENT_ID` / `GOOGLE_CLIENT_SECRET` | `muralha.digital.google.LoginGoogle` | OAuth 2.0 "Login com Google" (`REDIRECT_URI` ainda fixo em `http://localhost:8080/callback` — ajustar por ambiente) |
| `SENDGRID_API_KEY` | `ServicoEmailMuralha` | Envio via SendGrid (chave de conta de teste; trocar em produção) |
| `TWILIO_TEST_ACCOUNT_SID` / `TWILIO_TEST_AUTH_TOKEN` | `ServicoSMS.EnviaSMS_RecuperacaoSenha_Twilio` | Credenciais de teste do SMS de recuperação de senha |

Como definir (Windows, Tomcat standalone): `setenv.bat` em `apache-tomcat-9.0.91\bin` **não existe** na stack (e a stack não deve ser alterada) — defina as variáveis no ambiente do usuário/serviço antes de iniciar (`setx` ou propriedades do serviço). Para `tomcat7:run`: definir na sessão do PowerShell antes de `run.ps1` (`$env:SENDGRID_API_KEY='…'`).

## 4. Parâmetros dinâmicos no banco (não são segredos)
`muralha.config_chave_valor` (+ `_hist`), `muralha.config_grupo_permissao` (+ `historico_config_grupo_permissao`), `muralha.config_alarme*`, `config_alerta*`, `config_envio_tempo_real*`, `config_monitoramento_ao_vivo*`, `config_semelhanca_placa`, `config_mobile_silencio`; `dbo.configuracao*`. Edição via telas de configuração (`configuracao-monitoramento/`, `PermissoesFuncionalidade`, `ConfiguracaoInatividade`, `ConfiguracaoRadares`, `ConfiguracaoTempoOcrBlitz`, `ConfiguracaoTempo`).

## 5. Checklist ao criar uma nova integração
1. Definir a credencial como variável de ambiente ou tag em `muralha-digital-config.xml` (+ campo em `Inicializacao`).
2. Atualizar o `.example` com tag vazia e este documento.
3. Tratar ausência da credencial (recurso desligado, log de aviso, nunca NPE).
4. Registrar a integração em `08-integracoes.md`.
