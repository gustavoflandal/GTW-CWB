# 08 — Integrações Externas

| Integração | Onde no código | Configuração | Uso |
|---|---|---|---|
| **SQL Server** | `com.consilux.lib.Conexao` | `confGTW.xml` `armazenamento/servidor_sql` | Único banco de dados (DEV: `10.0.0.200` / `GTW_MURALHA_DEV`) |
| **Twilio (SMS)** | `muralha.digital.notificacao.ServicoSMS` (`EnviarSMS_Twilio`, `…_RecuperacaoSenha_Twilio`) | `IntegracaoSMS/Twilio` + env `TWILIO_TEST_*` | SMS de notificação de alertas/ocorrências e código de recuperação de senha |
| **Facilita Móvel (SMS)** | `ServicoSMS.EnviarSMS_FacilitaMovel` / `enviaSmsFacilitaMovel` (SDK `facilita-movel-sdk`) | `IntegracaoSMS/FacilitaMovel` | Provedor alternativo de SMS |
| **SmsDev (SMS)** | `ServicoSMS.EnviarSMS_SMSDEV` | `IntegracaoSMS/SmsDev` | Provedor alternativo |
| **Comtele (SMS)** | `ServicoSMS.EnviarSMS_Comtele` | `IntegracaoSMS/Comtele` (endpoint `https://sms.comtele.com.br/api/v2/send`) | Provedor alternativo |
| **SendGrid / SMTP (e-mail)** | `ServicoEmailMuralha`, `com.consilux.infra.ServicoEmail`, `JobEnviaEmail*` | env `SENDGRID_API_KEY`; `confGTW.xml` `email/smtp_servers` | E-mails de alerta/ocorrência/recuperação de senha; logs de erro (SMTPAppender) |
| **Bitly** | `muralha.digital.util.EncurtadorURL` | `BitlyUrlShortener` | Encurta o link da página de tratativa enviado por SMS |
| **Google OAuth 2.0** | `muralha.digital.google.LoginGoogle` (`/callback`), `login/google/*.html`, `cabecalho_token_google.jsp` | env `GOOGLE_CLIENT_ID/SECRET` | Login corporativo; usuário precisa existir em `sis_usuario` |
| **Google Maps JS API** | `muralha-digital/utils/maps-config.js`, telas de mapas | `confGTW.xml` `mapa/google_maps_key`; chave em `maps-config.js` | Mapas operacionais, mapa de calor, geocoding de blitz (`api_key_geocoding`) |
| **Leaflet** | relatório de rota, mapas pontuais | CDN unpkg | Rotas/trajetos |
| **Waze / Mobilidade Urbana** | **dados** no schema `mobilidade` (`tokens_waze`, `schedule_wazedirect`, `congestionamentos*`, `alertas*`…); `muralha.token_acesso_mobilidade_urbana` | — | Alimentado por processo externo; o Java deste projeto praticamente não consome |
| **Câmeras (Pumatronix/Dahua, RTSP)** | `muralha.digital.monitoramento`, `painelInformacoes`, `nginx.conf`, `visualizacao-cameras/camera-api/*` | `muralha-digital-config.xml` `equipamentos`; nginx porta 8185 | Vídeo ao vivo/passagem, snapshots, status de câmera; nginx faz proxy `ws://<nginx>/<ip>:<porta>/rtspoverwebsocket` e `/cam/realmonitor` |
| **FFmpeg / JAVE** | `muralha.digital.monitoramento.VerVideoMonitoramentoServlet`, `com.consilux.model.ArquivoVideoBean` | `VideosMonitoramento` | Conversão/recorte de vídeo (`ogv`) |
| **Equipamentos PCL / radares (SOAP Axis)** | `com.consilux.ws.server.*`, `server-config.wsdd`; clientes `com.consilux.ws.client.*` | endpoints `/services/{InfoEquipamento,StatusEquipamento,ConfigEquip,ConfigEquipWS,CSXEventsWS,VeiculoMonitoradoWS,Version}` | Equipamentos enviam eventos/status; consultam configuração e lista de veículos monitorados (`IWantedPlateListWS`) |
| **FTP / SFTP / SMB (arquivos)** | `com.consilux.model.Acesso{FTP,SFTP,Compartilhamento,Diretorio}`, `AcessoStorageProvider`, jobs de exportação | `confGTW.xml` (`config_chave_valor` `senha_ftp`/`senha_smb`, `exporta_*`) | Troca de imagens e remessas com órgãos autuadores e servidores de captura |
| **Órgãos autuadores (remessas)** | `ItemExportaRemessa{CET,CET_VM,URBS}`, `ExportaRemessa`, modelos Jasper `AIT*`/`PREM*` | `confGTW.xml` `remessa` | Geração de arquivos de remessa e AIT |
| **Assinatura digital de imagem** | `muralha.digital.assinatura.*` (`KeystoreAutoGenerator`, `AssinaturaImagemService`) | `AssinaturaConfig` | Assina imagens de passagem para uso como prova; endpoint `/muralha-digital/assinatura/*` |
| **App móvel de campo** | `login_action_app.jsp`, `login_app.jsp`, tokens (`spu_ppv_sis_usuario_token*`), cookie `isMobileApp`, agentes (`agente_localizacao_*`) | — | Agentes de guarnição enviam localização e recebem alertas; login por token |
| **Jasper / Excel / PDF** | `relatorios/*.jrxml`, POI, JXL, iText | — | Relatórios e exportações |

## Contratos de mensagem (WebSocket `/ClientesWebSocket`)
- Cliente → servidor (texto): `<qualquer>_<qualquer>_<TIPO>-<SUBTIPO>[-<tipoContexto>-<infoAdicional>[-…-<idUsuario>]]`, onde `<TIPO>-<SUBTIPO>` ∈ {`ALERTA-NOTIFICACAO`, `VEICULO-TEMPOREAL`, `BLITZ-DIGITAL`, `BLITZ-ELETRONICA`} (`Constantes.ClientesWS`). Mínimo de 3 segmentos separados por `_`.
- Servidor → cliente: **XML** montado por `Alertas`/`VeiculoTempoReal`/`Blitzes` (consulte as classes para o esquema atual; qualquer mudança é *breaking change* para `cabecalho.js`, `notificacoes_nao_tratadas.js` e as telas de tempo real).

## Webhooks / saídas externas
Não há webhooks. Saídas externas: SMS, e-mail, Bitly, FTP/SFTP, Google, remessas.
