# 00 — Visão Geral do Sistema GTW-CWB

> Documentação de base para qualquer implementação neste projeto. Leia também `agentes/AGENTS.md` antes de alterar código.

## 1. O que é

**GTW** ("Gestão de Trânsito WEB", artefato Maven `com.consilux.gtw:gtw:3.0`, WAR publicado como contexto `ROOT`, contrato `GTW_MURALHA_DIGITAL`) é um sistema legado da **Consilux Tecnologia** para fiscalização e inteligência de trânsito. Este repositório (`GTW-CWB`) é a cópia de trabalho do produto usada neste ambiente (o sufixo "CWB" provavelmente refere-se a Curitiba — existe `logo_curitiba_pr.png` — mas o código é multi-cliente: há logotipos e ramificações para DER-ES, DER-MG, BHTrans, Betim, Ipatinga, Olinda, Umuarama etc.).

O WAR reúne **três gerações de software** que convivem no mesmo contexto web:

| Geração | Nome | Tecnologia de UI | Onde está | Propósito |
|---|---|---|---|---|
| 1 | **GTW clássico** | JSP + GWT/GXT (compilado) + CSS próprio (Verdana, azul-acinzentado) | `webapp/{cadastro,ferramenta,processo,relatorio,infracao,remessa,...}`, `webapp/gxt`, `webapp/GtwMenu`, `webapp/GtwWidgets`, `webapp/css` | Processamento de infrações de radar/PCL: importar, validar imagens, gerar remessas/AIT, cadastros, relatórios de fluxo |
| 2 | **Muralha Digital** | JSP + Bootstrap 5.3 + jQuery + Font Awesome/Bootstrap Icons | `webapp/muralha-digital/**`, pacote Java `muralha.digital.*` | Monitoramento em tempo real, alertas de veículos monitorados, blitz digital, atendimento de ocorrências, mapas, câmeras, registro de fatos, boletins |
| 3 | **Mobilidade Urbana** (dados) | — (somente dados no mesmo banco) | schema `mobilidade` | Dados de congestionamento/Waze. **Não é consumido por este código Java** (ver `banco-de-dados/README.md`). |

**Código novo deve ser escrito no padrão Muralha Digital** (geração 2) — ver `05-padrao-visual.md` e `06-padroes-de-codigo.md`. A geração 1 só deve ser alterada para correções pontuais.

## 2. Números do projeto (2026-10-08)

- ~960 arquivos `.java`: `com.consilux.*` (≈529, legado/núcleo) + `muralha.*` (≈436, Muralha Digital) + `com.abertura` (1).
- 194 servlets declarados em `web.xml` + 118 anotados com `@WebServlet` (+ 1 endpoint WebSocket + 8 serviços Axis SOAP + recurso Jersey REST).
- ~1.100 arquivos de front-end em `webapp/muralha-digital` (≈ 44 diretórios de telas em `pages/`), ~480 arquivos GXT em `webapp/gxt`.
- Banco `GTW_MURALHA_DEV` (SQL Server 2016): **746 tabelas, 123 views, 388 procedures, 302 funções, 8 triggers, 636 FKs**, schemas `dbo`, `muralha`, `mobilidade` e `ia`.

## 3. Domínio em uma página

- **Equipamento / PCL / Local / Faixa (pista)**: radares, câmeras OCR e pontos de captura; cada *local* (`dbo.local`, 470 linhas) tem um *equipamento* configurado (`dbo.configuracao_equipamento` e ~40 tabelas satélite `configuracao_equipamento_*`: pista, câmera, controlador, aferição, regra de infração, servidor…) e *faixas* (`configuracao_equipamento_pista`).
- **Passagem de veículo** = leitura de placa (OCR) com imagens. Em tempo real: `muralha.veiculo_tempo_real` (+ `veiculo_tempo_real_imagem`), ~8 milhões de linhas. No GTW clássico: `veiculo`/`infracao` + processamento.
- **Veículo monitorado** (`muralha.cad_veiculo_monitorado`): lista de placas de interesse (furto/roubo/clonado etc.). Quando uma passagem bate com um monitorado gera-se um **Alerta** (`muralha.alerta`, `alerta_veiculo`).
- **Alerta → Ocorrência → Atendimento**: o operador trata o alerta (descarta com motivo ou assina/atende), aciona **guarnições**, abre **atendimento** (`muralha.atendimento*`), gera **ocorrência** e notificações (e-mail/SMS).
- **Blitz Digital / Eletrônica**: operações de abordagem com `blitz_digital`, `blitz_abordagem`, pessoas/documentos envolvidos.
- **Registro de Fato / Boletim**: registros de ocorrências policiais (`registro_fato*`, `boletim*`).
- **Correlacionamento automático**: cruza passagens/placas (comboio, clonagem, transporte clandestino).
- **Infração / Processo / Remessa / AIT (legado)**: fluxo de fiscalização eletrônica — importação de infrações (`dbo.infracao` ~73 mil, `infracao_processo` ~100 mil, `veiculo` ~74 mil no DEV), validação de imagem, enquadramento, remessa ao órgão autuador (CET, URBS…; classes `ItemExportaRemessaCET/URBS`), notificações (AIT/NAI).
- **Usuário / Grupo / Menu / Permissão**: `sis_usuario`, `sis_grupo`, `sis_menu*`, `sis_menu_direitos` (ver `09-seguranca-e-permissoes.md`).

## 4. Mapa dos documentos

| Documento | Conteúdo |
|---|---|
| `01-stack-e-ambiente.md` | Stack imutável (`.setup-gtw`), build/run/debug, versões, bibliotecas |
| `02-arquitetura.md` | Camadas, ciclo de requisição, sessão/autenticação, acesso a dados, WebSocket, jobs, SOAP/REST, configuração |
| `03-estrutura-do-projeto.md` | Árvore de diretórios comentada e onde colocar cada coisa |
| `04-modulos-funcionais.md` | Catálogo funcional dos módulos (back + front + tabelas) |
| `05-padrao-visual.md` | **Design system**: paleta, tipografia, ícones, componentes, layout, acessibilidade |
| `06-padroes-de-codigo.md` | Convenções Java/JSP/JS/SQL observadas e as **obrigatórias** para código novo |
| `07-configuracao-e-segredos.md` | Arquivos de configuração, variáveis de ambiente, regras para segredos |
| `banco-de-dados/` | Catálogo completo do banco (tabelas, views, procs, funções, domínios, código SQL) |
| `08-integracoes.md` | Twilio, SendGrid, Facilita Móvel, SmsDev, Comtele, Bitly, Google (OAuth/Maps), Waze, câmeras, FFmpeg, Axis |
| `09-seguranca-e-permissoes.md` | Autenticação, autorização, riscos conhecidos |
| `10-guia-de-implementacao.md` | Receitas passo a passo (nova tela, endpoint, tabela, permissão, job…) |
| `11-glossario.md` | Termos e siglas |
| `12-riscos-e-debitos-tecnicos.md` | Dívidas técnicas e armadilhas |
| `referencia/` | Inventários gerados: servlets, módulos, telas, pacotes, menus/permissões |
| `agentes/` | Guias e prompts para subagents |

## 5. Regras imutáveis

1. **A stack de desenvolvimento (`.setup-gtw/`, na pasta pai deste projeto) não pode ser alterada**: JDK 13.0.1, Maven 3.9.9, Tomcat 9.0.91 (scripts) e `tomcat7-maven-plugin` para execução. Não atualizar versões, não trocar bibliotecas base, não adicionar frameworks (Spring, JPA, React, etc.).
2. **O padrão visual é obrigatório** (`05-padrao-visual.md`): paleta, fontes, ícones, componentes.
3. **Nenhum segredo no repositório.** Credenciais ficam em arquivos locais ignorados pelo git (`confGTW.xml`, `muralha-digital-config.xml`) ou variáveis de ambiente.
4. **Sem alterar o esquema do banco por conta própria**: mudanças de DDL exigem script versionado e aprovação (ver `agentes/agente-banco-de-dados.md`).
