# Análise Comparativa — Requisitos de Sistema: Edital de Curitiba × Edital de Salvador

**Data:** 2026-10-15
**Escopo:** somente requisitos de sistema (software da Central de Processamento de Imagens — CPI, Sistema de Gestão de Dados de Tráfego em Tempo Real, Sistema de Relatórios e integrações). Ficam fora hardware, gabinete, câmeras, energia, obras, sinalização e instalação física.
**Sistema avaliado:** GTW clássico + Muralha Digital (este repositório, branch `main`).

## Fontes

| Documento | Uso |
|---|---|
| [Anexo II — Especificações Técnicas (Curitiba)](../../docs-editais/edital-curitiba/Anexo_II_Especificacoes_Tecnicas.md) | Requisitos de Curitiba (capítulos 3, 4, 6–14 e 18–20, só a parte de sistema) |
| [Itens de sistema do edital de Salvador 2026](../../docs-editais/edital-salvador/edital-salvador-2026.md) | Lista de itens de Salvador indicada pelo responsável |
| [TR Salvador — Sistema (Cap. 5)](../../docs-editais/edital-salvador/TR-Salvador-Sistema.md) | Texto integral dos itens 5.x de Salvador |
| [TR Salvador — PoC (Cap. 21 e Anexo A)](../../docs-editais/edital-salvador/TR-Salvador-POC.md) | Itens da prova de conceito de Salvador |
| [Análise de aderência — Salvador](../../docs-editais/edital-salvador/analise-aderencia.md) | Situação de cada item de Salvador no GTW/Muralha |
| [Itens do edital × entregas](../entregas/itens-edital-x-entregas.md) e [Relatórios de entrega](../entregas/README.md) | O que foi entregue (entregas 01–22) |
| [Catálogo funcional dos módulos](../requisitos/04-modulos-funcionais.md) | Módulos, telas, servlets e tabelas existentes |
| [Sistema de Chamados — arquitetura](../chamados/sistema-chamados-arquitetura.md) | Entrega 22 |
| [Construtor de Dashboards — arquitetura](../dashboards/construtor-dashboards-arquitetura-new.md) e [manual](../dashboards/manual-dashboard-builder.md) | Entrega 21 |
| [Gaps vermelhos pendentes (Salvador)](../entregas/gaps-vermelhos-pendentes.md) | Pendências de Salvador que coincidem com Curitiba |

Pontos duvidosos foram conferidos no código-fonte com buscas pontuais (`src/main/java`, `src/main/webapp`). Quando a busca não encontrou nada, a linha diz **"sem evidência"**.

## Legenda

| Símbolo | Significado |
|---|---|
| ✅ Atendido | Requisito com correspondente em Salvador e já existente no GTW/Muralha |
| 🟡 Parcial | Existe no sistema, mas precisa de ajuste ou complemento |
| 🔴 Desenvolver | Não existe no sistema |
| ⚪ + (✅/🟡/🔴) | Sem paralelo em Salvador (exclusivo de Curitiba), seguido da situação no sistema |

Numeração: "Curitiba 9.11" = item do Anexo II; "Salvador 5.2.2.6" = item do TR de Salvador; "POC 5.2.8" = item do Anexo A da PoC de Salvador. Itens próximos foram agrupados numa mesma linha quando têm a mesma solução.

---

## 1. Resumo executivo

Foram analisadas **127 linhas de requisito de sistema** de Curitiba. Cada linha reúne um ou mais itens numerados do Anexo II que têm a mesma solução.

| Situação | Linhas | % |
|---|---|---|
| ✅ Concordante com Salvador e já atendido | 39 | 31% |
| 🟡 Concordante com Salvador, atendido em parte | 51 | 40% |
| 🔴 Concordante com Salvador, a desenvolver | 8 | 6% |
| ⚪ Exclusivo de Curitiba (sem paralelo em Salvador) | 29 | 23% |
| **Total** | **127** | 100% |

Situação no sistema dos 29 itens exclusivos de Curitiba: 3 ✅ já existem, 13 🟡 parciais e 13 🔴 a desenvolver.

Visão consolidada pelo sistema, sem considerar Salvador: **42 atendidos (33%)**, **64 parciais (50%)** e **21 a desenvolver (17%)**.

O que conta a favor: o **Muralha Digital já é, na prática, o "cerco eletrônico" do capítulo 14** (alertas de roubado, clonado, clandestino, sequestro e comboio; semelhança de placa configurável; correlação fraca, média e forte em grafo; mapas em camadas; tratativa com anotações contributivas). O **GTW clássico já cobre o núcleo da CPI** (validação consistente ou inconsistente com motivo, critério técnico ou não técnico, AIT em PDF, remessas). E as entregas de Salvador (log de auditoria, SHA-256, obliteração, MFA/TOTP, retenção, exportação, KPIs, construtor de dashboards) atendem boa parte dos requisitos transversais.

### Desenvolvimentos mais relevantes

1. **Cadeia de custódia imutável e verificável** (Curitiba 9.19.9–9.19.10). Hash encadeado, assinatura digital com carimbo de tempo e verificador independente. Será demonstrada no teste de conceito.
2. **Interface de recepção para vários fornecedores e importação de coleta manual** (Curitiba 4.1.3.23–4.1.3.24, 4.4.10.7–4.4.10.8, 14.6.39, 19.9.14). Especificação pública e endpoint para os lotes 2 e 3, além de importação por pen drive e Wi‑Fi com conferência de integridade.
3. **Reconhecimento de padrões no fluxo da CPI** (Curitiba 8.2.1–8.2.5, 8.4, 8.5, 8.7, 4.1.3.17–4.1.3.18). Atributos de emergência, viatura, carga perigosa (com código ONU), táxi e carro-forte; alerta bloqueante ao auditor; processamento em servidor das imagens dos lotes 2 e 3; relatórios mensais.
4. **Ajustes da CPI às regras de Curitiba** (Curitiba 3.11.7–3.11.15, 4.5.7–4.5.10, 9.15–9.17, 10.2.4, 20.1.22.3). Imagem teste a cada 30 min, categoria "descartada", remessas de inconsistentes com motivo, fórmula de aproveitamento com tabela de desconto e consulta de passagens com 19 campos.
5. **Integração com a plataforma corporativa de infrações e com as bases do Paraná** (Curitiba 9.12–9.14, 11.2, 12.2–12.3). Envio de AIT, NAT e NAP por webservice com os leiautes da contratante; recebimento de dados enriquecidos; consulta à base nacional.
6. **Gestão de tráfego em tempo real** (Curitiba 14.1, 14.6.28–14.6.31, 14.6.80–14.6.83, 14.6.89). Lentidão e congestionamento por segmento e sentido, mapa de calor do viário com dados de GPS, comparativos com semana, mês e ano anteriores, e alerta de anomalia.
7. **Segurança da comunicação e infraestrutura de software** (Curitiba 6.4, 9.19.1.1–9.19.5, 14.3). TLS com autenticação mútua usando certificados da Prefeitura, VPN, NTP (ntp.br) e balanceamento de carga com replicação.
8. **Painéis públicos e incorporáveis** (Curitiba 14.6.7, 14.6.11, 14.6.18, 14.6.12, 14.6.16). Publicação sem credenciais, com exportação, incorporação em HTML, tendência e outliers, e novos tipos de gráfico.
9. **Aplicativo Android e ações complementares** (Curitiba 14.6.62–14.6.67). WhatsApp e Telegram, designação automática por proximidade, aceite e anotações em campo.
10. **Contagem de pedestres e bicicletas** (Curitiba 14.6.84–14.6.88, 14.6.95). Recepção, armazenamento e relatório.

---

## 2. Matriz por capítulo de Curitiba

### Capítulo 3 — Requisitos gerais (parte de sistema)

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 3.3.1 | Enquadramentos fiscalizados definidos por local por Ordem de Serviço | 5.6.7 | ✅ Atendido | GTW clássico `configuracao_equipamento*`, `ConfigEquipWS` | Cadastro de enquadramento por equipamento já existe |
| 3.5.2–3.5.3 | Resultado do autoteste enviado à CPI com log consultável; registros de teste identificados | 5.2.7.2, 5.4.7.2–5.4.7.3 | 🟡 Parcial | `eventos_csx`; enquadramento "imagem teste" (`Infracao.java`); [Entrega 01](../entregas/01-log-auditoria.md) | Falta um tipo de evento "autoteste" e consulta dedicada |
| 3.6.2.3 | Equipamento "ativo" só se aferido, protocolado, energizado e transmitindo | 5.2.7.1, 5.8.2.1 | 🟡 Parcial | [Entrega 18](../entregas/18-disponibilidade.md) (disponibilidade por última passagem) | Falta compor o status com a validade da aferição e o protocolo na SMDT |
| 3.7.9, 4.4.9.7, 9.19.6–9.19.7 | Receber a fila pendente após reconexão (o equipamento guarda pelo menos 48 h) | 5.6.4.1–5.6.4.5, 5.2.1.6 | 🟡 Parcial | Recepção SOAP `CSXEventsWS`; [Entrega 10](../entregas/10-sla-preprocessamento.md) | A recepção de registros atrasados já funciona; falta sinalizar cada registro fora do prazo (Salvador 5.2.1.6 também está parcial). A autonomia de 48 h é do equipamento |
| 3.10.1–3.10.2, 4.2.2.2 | Contagem volumétrica e classificação de todos os veículos em 4 categorias | 5.6.2.4–5.6.2.5, 5.7.6.2 | ✅ Atendido | `veiculo_tempo_real`; relatórios `fluxo-mensal-classificacao.jsp`, `distribuicao-porte-veicular.jsp` | — |
| 3.11.2 | Receber em tempo real as imagens de todos os veículos, infratores ou não | 5.2.1.1, 5.6.2.1 | ✅ Atendido | `veiculo_tempo_real_imagem`, jobs Quartz de recepção | — |
| 3.11.3–3.11.5, 9.2 | Classificar como CONSISTENTE ou INCONSISTENTE, com motivos definidos pela contratante | 5.2.3.1, 5.5.1.2(f) | ✅ Atendido | GTW `processo/*` (`proc_infracao_validacao.jsp`, motivo de inconsistência); [Entrega 05](../entregas/05-dupla-analise.md) | Cadastrar os motivos 3.11.5.1–3.11.5.11 como parâmetros |
| 3.11.7–3.11.9 | Entregar consistentes e inconsistentes em remessas distintas, com o motivo anexado | 5.2.5.1, 5.2.5.3 | 🟡 Parcial | `remessa/*`, `infracao_remessa*`; [Entrega 14](../entregas/14-gestao-lotes.md) | Sem evidência de remessa de inconsistentes acompanhada do motivo |
| 3.11.10, 9.10 | Obliteração de condutores e passageiros | 5.2.4.3–5.2.4.4 | ✅ Atendido | [Entrega 06](../entregas/06-obliteracao-imagens.md) | — |
| 3.11.11 | Nenhuma edição da imagem original além da obliteração; integridade garantida | 5.5.4.2, 5.2.1.2 | ✅ Atendido | [Entrega 02](../entregas/02-sha256-integridade.md); originais preservados | — |
| 3.11.12–3.11.13, 9.7 | Distinguir APROVEITÁVEL, INCONSISTENTE e DESCARTADA (motivo externo autorizado); critérios TÉCNICOS e NÃO TÉCNICOS | 5.5.3.1(m) | 🟡 Parcial | `consulta_infracao_completa.jsp` ("Problemas Não Técnicos"); `Relatorio2FluxoVeicular` | Técnico e não técnico já existem. A categoria "descartada com autorização da SMDT" não tem evidência |
| 3.11.14 | Aprovar imagens válidas por faixa antes do início da operação | 5.7.10.1–5.7.10.3 (homologação, análogo) | 🔴 Desenvolver | Sem evidência de fluxo de homologação | Salvador 5.7.10 também não está implementado |
| 3.11.15, 4.1.3.20, 20.1.22.3, 20.1.24 | Aproveitamento técnico mínimo de 90% pela fórmula de Curitiba, com imagens teste no cálculo e faixas de desconto | 5.4.6.2(d), 5.5.3.1(e), POC 6.4.2 | 🟡 Parcial | Relatórios RJ (`Relatorio2FluxoVeicular`, `Relatorio10FluxoVeicular`) com taxa que desconta não técnicos; [Entrega 08](../entregas/08-painel-assertividade.md) | Ajustar a fórmula, incluir as imagens teste e calcular o desconto mensal por faixa |

### Capítulo 4 — Requisitos específicos (parte de sistema)

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 4.1.1–4.1.2, 4.2.1, 4.3.1, 19.9.2–19.9.5 | Processar os enquadramentos 74550, 74630, 74710, 75870, 57030, 57461, 57463, 60503, 56732, 60411 e 60412 (no teste também 56810, 56900 e 59910) | 5.2.1.4 | 🟡 Parcial | Tabela de enquadramentos do GTW (`enq.id_enquadramento`) | Sem evidência do cadastro desses códigos com descrição e tarja de Curitiba. Conferir no banco (somente leitura) |
| 4.1.3.2–4.1.3.3, 4.3.2.1–4.3.2.5, 7.4 | Dados legais da tarja (coordenada decimal, faixa, Inmetro, verificações metrológicas, retardo ou permanência) disponíveis na CPI para o AIT | 5.7.6.1 | 🟡 Parcial | Tabela `infracao`; cadastro de equipamento | A tarja é gravada pelo equipamento. Sem evidência de coordenadas decimais e datas metrológicas como metadados |
| 4.1.3.5, 4.1.3.11, 6.1 | Transmissão on-line em tempo real para a CPI | 5.6.2.1, 5.6.2.3 | ✅ Atendido | `CSXEventsWS`; [Entrega 07](../entregas/07-sla-latencia.md) | A segurança do canal está na linha 6.4 |
| 4.1.3.8.1, 4.1.3.8.7, 9.19.2 | Imagens cifradas antes do envio; assinatura digital do resultado da medição, verificável depois | 5.2.1.2, 5.6.3 | 🟡 Parcial | [Entrega 02](../entregas/02-sha256-integridade.md) (SHA-256 na recepção); módulo `assinatura` | Falta decifrar e validar a assinatura do equipamento na recepção e registrar o resultado |
| 4.1.3.10, 4.4.9.4, 4.4.9.6, 19.9.14.1–19.9.14.3 | Coleta manual (pen drive) e transferência direta (Wi‑Fi) com importação na central | 5.6.4.2; POC 5.2.3, 5.2.5 | 🟡 Parcial | GTW `descarga/*`, dependência `descarga-core`; `IntegracaoSequenciaImagem` | Falta um roteiro de importação demonstrável no teste de conceito, com conferência de integridade |
| 4.1.3.12–4.1.3.13, 4.1.3.15–4.1.3.16 | Relatórios de passagem (velocidade e horário), contagem por dia e hora, VDM, infratores por local e período, estatísticas por enquadramento e equipamento com gráficos | 5.2.8.1, 5.4.5.1(a, b, d) | ✅ Atendido | `quantidade-passagens.jsp`, `relatorio-fluxo-passagens-veiculares.jsp`, GTW `relatorio/*`, menu Gráficos | Conferir se o cálculo do VDM segue a Res. 798 |
| 4.1.3.14 | Lista de infratores com placa, categoria, espécie e tipo | 5.4.5.1(d) | 🟡 Parcial | Relatórios de infrações do GTW | Espécie e tipo dependem da base de veículos do Paraná (Curitiba 9.12 e 12.3) |
| 4.1.3.17 | Relação de imagens de veículos de emergência em serviço, não autuados | — | ⚪ + 🟡 | `cad_isento` (`Isento.java`) | A isenção já existe; faltam o relatório e o vínculo com a detecção de Curitiba 8.2.1–8.2.2 |
| 4.1.3.18 | Relação de veículos de carga perigosa com código ONU | — | ⚪ + 🔴 | Sem evidência (nenhuma ocorrência de código ONU no código) | Depende de Curitiba 8.2.3 |
| 4.1.3.4, 4.1.3.19 | Relógio sincronizado com o servidor; horário de verão | 5.6.6.1–5.6.6.4 | 🟡 Parcial | Sem evidência de NTP no sistema (Salvador 5.6.6.1 está parcial) | Ver Curitiba 9.19.5 |
| 4.1.3.21, 6.2–6.3, 6.6 | Guardar 60 meses em nuvem, com busca ágil e backup feito pela contratante | 5.2.5.6, 5.7.5.3, 5.8.5.1–5.8.5.3 | 🟡 Parcial | [Entrega 12](../entregas/12-retencao-dados.md) (retenção configurável); consultas de passagens | Sem evidência de nuvem nem de backup em autoatendimento (Salvador 5.8.5.1 não implementado) |
| 4.1.3.22 | Velocidade regulamentada por faixa e por categoria de veículo | 5.6.7 | 🟡 Parcial | `configuracao_equipamento*` | Sem evidência de limite por categoria |
| 4.1.3.23–4.1.3.24, 14.6.33, 14.6.39 | Integrar dados dos lotes 2 e 3 (outros fornecedores) e de outros municípios, no padrão definido pelo Lote 1 | 5.4.1.1(a, f), 5.6.1.1 | 🟡 Parcial | `CSXEventsWS` (SOAP próprio); `JuncaoBase` (CSV) | Falta uma especificação pública de integração e um endpoint para terceiros |
| 4.2.2.6 | Alarme na CPI para cada elemento do display (indicador e luz intermitente) | 5.2.7.2 | 🟡 Parcial | Eventos de falha de equipamento (`eventos_csx`) | Faltam tipos de evento específicos do display |
| 4.3.3.1.6, 4.3.3.2.5 | Vídeo MPEG4 de 5 s a 5 fps das infrações de semáforo, disponível na CPI | 5.2.1.1 (vídeos), 5.7.7 | 🟡 Parcial | `video-passagem-semaforo.jsp`, `ConverterVideo.java` (MP4), `ItemExportaRemessaCET` | Sem evidência do vínculo do vídeo à infração na tela de validação |
| 4.3.3.5.1 | Log na CPI de amarelo piscante e de falha na sequência semafórica | 5.4.7.3(f, j) | 🟡 Parcial | Repositório de logs ([Entrega 01](../entregas/01-log-auditoria.md)), `eventos_csx` | Faltam os novos tipos de evento |
| 3.6.2.1, 4.3.3.6 | Operação em horário programado | 5.6.7; POC 5.2.4 | ✅ Atendido | `ConfigEquipWS` (ativação e desativação remotas) | — |
| 4.4.2, 4.4.8, 4.4.10.3–4.4.10.4 | Pesagem e classificação: 12 classes, eixos, distância entre eixos, PBT, peso por eixo, comprimento e intervalo entre veículos | — | ⚪ + 🟡 | `muralha.digital.veiculo.VeiculoDeCarga*` (PBT), `veiculo_pesagem`, `Dimensao.java`; tela `consulta-veiculo-de-carga/` | Sem evidência das 12 classes (Res. 882 e Portaria 268) |
| 4.4.3, 4.4.9.8 | Receber dados 24x7, sem tratamento, mantendo os originais | 5.6.2.4–5.6.2.5, 5.5.4.2 | ✅ Atendido | Recepção contínua; originais preservados | — |
| 4.4.10.7–4.4.10.8 | Leiaute do arquivo de tráfego e pesagem, definido pelo Lote 1 (pacote, equipamento, eventos, eixos) | — | ⚪ + 🔴 | Sem evidência | Documento de interface a publicar (ver 4.1.3.23) |
| 4.4.10.10 | Sistema para extrair relatórios dos dados de pesagem | — | ⚪ + 🟡 | `VeiculoDeCargaRelatorio.java` | Relatórios por classe, eixo e PBT a definir com a contratante |
| 4.5.1, 4.5.3, 4.6.1, 4.6.3, 14.6.36–14.6.37, 14.6.44 | Vídeo ao vivo das câmeras de contexto, com pelo menos 4 conexões simultâneas e sem gravação | 5.3.7.2(e) | ✅ Atendido | `video-ao-vivo/`, `visualizacao-cameras/`, `mosaico/` (nginx RTSP sobre WebSocket) | Garantir que o vídeo ao vivo não seja gravado (o FFmpeg grava só clipes de passagem) |
| 4.5.7–4.5.10, 4.6.7–4.6.10, 9.3 | Imagem teste a cada 30 min por faixa; imagem sem veículo só se não houve fluxo; classificar como aproveitável ou não | — | ⚪ + 🟡 | `ImagemTeste.java`, tabela `imagem_teste`, `DadosMedicao` | Sem evidência das regras de periodicidade e de validação contra o fluxo |
| 4.5.13, 4.6.13, 10.2.7 | Velocidade média de percurso entre pontos da mesma via | — | ⚪ + 🟡 | `Relatorio1TempoPercurso`, `Relatorio10TempoPercurso` (relatórios RJ do GTW clássico) | Levar ao módulo web de relatórios do Muralha |

### Capítulo 6 — Transmissão e armazenamento (parte de sistema)

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 6.4, 9.19.1.1, 9.19.3–9.19.4 | Canal entre equipamento e CPI cifrado por TLS com autenticação mútua, certificados da Prefeitura, link dedicado ou VPN | 5.6.3, 5.2.6.2 | 🔴 Desenvolver | Sem evidência (Salvador 5.6.3 também não implementado) | Envolve infraestrutura e o código de recepção |
| 6.5 | 10 chaves de acesso simultâneo da contratante à nuvem | — | ⚪ + 🔴 | Sem evidência | Infraestrutura e contrato |

As demais linhas do capítulo 6 (6.1, 6.2, 6.3 e 6.6) estão agrupadas com Curitiba 4.1.3.5 e 4.1.3.21.

### Capítulo 7 — OCR

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 7.1–7.2 | OCR 24x7, com todas as placas lidas registradas por serviço web da CPI | 5.2.2.4, 5.7.1 | ✅ Atendido | `CSXEventsWS`, `veiculo_tempo_real` | — |
| 7.3.1 | Placas no padrão antigo e no Mercosul | 5.7.1.1 | ✅ Atendido | LAP atual (análise de aderência §7) | — |
| 7.5 | Acerto mínimo de 90% de dia e 85% à noite | 5.7.2.1–5.7.2.4; POC 6.4.3 | 🟡 Parcial | [Entrega 08](../entregas/08-painel-assertividade.md) | Salvador mede por padrão de placa; Curitiba, por período (dia ou noite). Faltam essa segmentação e a aferição formal |
| 7.6 | Associação a bancos de dados específicos (listas) | 5.3.1.1, 5.4.1.1(f) | ✅ Atendido | `cad_veiculo_monitorado`, `JuncaoBase` | — |
| 7.7 | Configurar 3 tamanhos de imagem (panorâmica, objetiva e traseira) | — | ⚪ + 🟡 | `veiculo_tempo_real_imagem` (várias imagens por passagem) | Sem evidência de parametrização por equipamento |
| 7.8 | Integrar as leituras do OCR com outros sistemas | 5.2.6.1, 5.3.5.1(b) | 🟡 Parcial | [Entrega 11](../entregas/11-exportacao-padronizada.md) | Sem evidência de API para terceiros |

### Capítulo 8 — Reconhecimento de padrões por imagem

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 8.2.1–8.2.5, 8.3 | Detectar emergência, viatura, carga perigosa, táxi e carro-forte, com alerta ao Sistema de Gestão | — (a POC 5.2.8 de Salvador pede outros padrões) | ⚪ + 🔴 | Sem evidência; `ia.veiculo_caracteristica` guarda só cor, marca e modelo | A detecção é embarcada no equipamento (4.5.12); a CPI precisa receber, guardar e exibir esses atributos |
| 8.2.6–8.2.7 | Cor e marca detectadas automaticamente | 5.7.6.2; POC 6.4.4 | ✅ Atendido | `ia.veiculo_caracteristica` | Acurácia não aferida (análise de aderência §9) |
| 8.2.8 | Texto de adesivos lido por OCR | — | ⚪ + 🟡 | `texto_adesivo` no cadastro de monitorados | O campo existe no lado do alerta. Sem evidência de recepção do texto lido na passagem |
| 8.4 | Processar em servidor os mesmos padrões para as imagens dos lotes 2 e 3 | — | ⚪ + 🔴 | Sem evidência | Exige motor de IA no servidor (componente novo, depende de aprovação) |
| 8.5 | ALERTA BLOQUEANTE ao auditor para infrações de emergência, viatura e táxi | — | ⚪ + 🔴 | Sem evidência | Bloquear a validação até a decisão explícita do auditor |
| 8.6–8.7 | Guardar imagens de carga perigosa; relatório mensal desses veículos com a classificação | — | ⚪ + 🔴 | Sem evidência (a consulta de veículos de carga não trata carga perigosa) | — |

### Capítulo 9 — Central de Processamento de Imagens (CPI)

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 9.1 | Plataforma web para auditoria das imagens de infração | 5.2.4.1 | ✅ Atendido | GTW `processo/*`; [Entrega 05](../entregas/05-dupla-analise.md) | — |
| 9.4–9.5 | Acesso por usuário e senha, privilégios, cadastro em grupos | 5.1.5–5.1.6 | ✅ Atendido | `sis_grupo`, `config_grupo_permissao`; [Entrega 03](../entregas/03-politica-senhas.md) | — |
| 9.6, 14.6.45 | Log das ações dos usuários | 5.1.7–5.1.10 | ✅ Atendido | [Entrega 01](../entregas/01-log-auditoria.md) | — |
| 9.8 | Digitar ou corrigir placa lida errado | 5.2.4.5 | ✅ Atendido | `/CorrecaoPlaca`, `veiculo_tempo_real_correcao` | — |
| 9.9 | Zoom e brilho sem salvar na imagem | 5.2.4.2, 5.5.4.3 | ✅ Atendido | `proc_infracao_validacao.jsp` | — |
| 9.11 | Gerar AIT das imagens consistidas | 5.2.5.4–5.2.5.5 | ✅ Atendido | `/ait/AITPDF`, `controleAIT/*`, Jasper `AIT*.jrxml` | Parametrizar o leiaute de Curitiba |
| 9.12 | Integração com a base de veículos do Paraná (fornecida pela contratante) | 5.2.6.1, 5.4.1.1(f) | 🟡 Parcial | `JuncaoBase` (importação CSV de base de veículos) | Sem evidência de integração on-line; conflita com Curitiba 12.1 (ver Riscos) |
| 9.13 | Até 1.000 consultas por mês à base nacional, por conta da contratada | — | ⚪ + 🔴 | Sem evidência de integração com base nacional no código | — |
| 9.14 | Gerar arquivos de NAT e NAP para impressão | 5.2.5.4 | 🟡 Parcial | `/notificacao/NotificacaoPDF` | Sem evidência de NAP (penalidade); falta o leiaute de Curitiba |
| 9.15–9.16 | Consultar todas as passagens com os 19 campos mínimos | 5.2.1.4, 5.7.6.1 | 🟡 Parcial | `consulta-veiculo/`, `consulta-veiculo-de-carga/` | Faltam comprimento, as marcas de 8.2, texto de adesivo e eixos/PBT na mesma consulta |
| 9.17 | Consultar imagens de infração com filtros (consistência, tipo de inconsistência, emergência, viatura ou táxi) | 5.2.1.4 | 🟡 Parcial | `consulta_infracao_completa.jsp` | O filtro de emergência, viatura e táxi depende de 8.2 |
| 9.18 | Logs de todos os equipamentos e estado atual de comunicação e energia | 5.2.7.1–5.2.7.3, 5.4.7 | 🟡 Parcial | [Entrega 18](../entregas/18-disponibilidade.md), `fcn_getAlertaLocaisOffLine`, `fcn_getAlertaLocaisConexaoInstavel`, `eventos_csx` | Na lista de Salvador, o item 5.4.7 (logs do equipamento pesquisáveis no Muralha) também está pendente |
| 9.19.1 | SLA de 99,5% dos canais de comunicação | 5.8.2.1 | 🟡 Parcial | [Entrega 18](../entregas/18-disponibilidade.md) (uptime) | Mede pela última passagem, não pelo enlace |
| 9.19.5 | Servidores e equipamentos sincronizados por NTP (ntp.br) pelo menos duas vezes ao dia | 5.6.6.1 | 🟡 Parcial | Sem evidência no sistema | Infraestrutura mais monitoramento de desvio |
| 9.19.8 | Cumprimento da LGPD | 5.1.4, 5.4.1.3, 5.7.8 | ✅ Atendido | [Entrega 06](../entregas/06-obliteracao-imagens.md), [Entrega 13](../entregas/13-anonimizacao-placas.md), [Entrega 12](../entregas/12-retencao-dados.md) | — |
| 9.19.9–9.19.10 | Cadeia de custódia com integridade, imutabilidade, verificação independente e trilha imutável, demonstrada no teste de conceito | 5.1.3, 5.1.7, 5.2.1.2 | 🟡 Parcial | [Entrega 02](../entregas/02-sha256-integridade.md) (SHA-256 por imagem); [Entrega 01](../entregas/01-log-auditoria.md) | Faltam encadeamento criptográfico, assinatura com carimbo de tempo e verificador independente |

### Capítulo 10 — Relatórios

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 10.1, 10.3 | Plataforma web de relatórios; novos relatórios sob demanda, sem custo | 5.2.8, 5.5.3.5 | ✅ Atendido | Cerca de 35 relatórios do Muralha; [Entrega 21](../entregas/21-dashboard-builder.md) | — |
| 10.2.1–10.2.3 | Totais por local: infrações, veículos, imagens válidas e inválidas | 5.4.5.1, 5.5.3.1 | ✅ Atendido | GTW `relatorio/*` (aproveitamento de imagens), `ContagemPassagensPorLocalServlet` | — |
| 10.2.4 | Aproveitamento líquido por local | 5.4.6.2(d) | 🟡 Parcial | Ver a linha 3.11.15 | Mesma fórmula de Curitiba |
| 10.2.5 | Velocidade do 85º percentil por local | — | ⚪ + ✅ | `LocalVigente.velocidade85Percentil`; relatório RJ (`ItemFluxoVeicular`) | Disponibilizar também no menu de relatórios do Muralha |
| 10.2.6 | Velocidade média por local | 5.4.5.1(b) | ✅ Atendido | `velocidade-media.jsp`, `RelatorioVelocidadeMedia` | — |

O item 10.2.7 está na linha 4.5.13.

### Capítulos 11 e 12 — Notificações e plataforma corporativa

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 11.2 | Enviar à plataforma corporativa os arquivos de AIT, NAT e NAP nos leiautes da contratante; integração SNE | 5.2.6.1–5.2.6.3 | 🔴 Desenvolver | `LoteServlet`, ação `enviar`: só muda o status para ENVIADO | A integração real precisa ser desenvolvida |
| 12.2 | Webservice com a plataforma corporativa: infrações validadas e arquivos, com segurança e prazos | 5.2.6.1–5.2.6.3 | 🔴 Desenvolver | Idem; Salvador 5.2.6.3 também não implementado | — |
| 12.3 | Receber dados enriquecidos de veículo e proprietário, com uso restrito à finalidade | 5.4.1.1(d), 5.4.1.3 | 🔴 Desenvolver | Sem evidência | — |

### Capítulo 13 — Horas técnicas de desenvolvimento

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 13.1–13.3 | Integrações com bancos e registros de terceiros, APIs e dados de tráfego para outros órgãos, novas funcionalidades | 5.3.2.2, 5.3.7.1 | 🟡 Parcial | Equipe e padrão de módulos ([padrões de código](../requisitos/06-padroes-de-codigo.md)) | Sem evidência de API pública de dados de tráfego (13.2.3) |

### Capítulo 14 — Gestão de tráfego e segurança em tempo real (cerco eletrônico)

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 14.1, 14.6.26 | Mapa de calor sobre o viário com dados de GPS e apps de mobilidade, guardados como geometrias das vias | 5.4.4.2(b), 5.3.7.3 | 🟡 Parcial | `mapa-calor/` (por passagens); [Entrega 19](../entregas/19-sensores-externos.md) (incidentes do Waze) | Falta uma camada por segmento de via com dados de GPS |
| 14.2, 14.5, 14.6.17 | Interface ergonômica, em PT-BR, com documentação e ajuda em PT-BR | 5.4.2.2 | ✅ Atendido | `pages/ajuda/`; manuais em `docs/` | — |
| 14.3 | Balanceamento de carga em vários nós, com replicação | 5.8.3.1 | 🔴 Desenvolver | Sem evidência (Salvador 5.8.3.1 não implementado) | — |
| 14.4, 14.6.1–14.6.5 | Servidor em ambiente virtual com Windows Server 2022 ou Linux LTS | 5.8.1.1 | ✅ Atendido | Tomcat 9 + SQL Server; scripts de `.setup-gtw` | Validar no ambiente virtual da contratante |
| 14.6.6 | Drill across, up, down e through; publicar painéis na intranet e na internet | 5.4.2.4, 5.4.2.5 | 🟡 Parcial | [Entrega 21](../entregas/21-dashboard-builder.md) (drill-down, filtro cruzado) | Sem evidência de drill-up e drill-through |
| 14.6.7 | Painéis públicos sem credenciais, com exportação XLS/XLSX/CSV | — | ⚪ + 🔴 | A marca `publico` só deixa o painel visível a usuários autenticados (`DashboardBuilderServlet`) | — |
| 14.6.8 | Log dos processos de carga, atualização e distribuição; navegadores Edge, Firefox, Chrome e Safari | 5.4.7.1 | 🟡 Parcial | `dash.consulta_log` | Sem processo de carga (ETL) com log e alerta |
| 14.6.9, 14.6.20 | Painéis responsivos em smartphones e tablets | 5.4.2.2 | 🟡 Parcial | Bootstrap 5.3; grade de 12 colunas | Sem validação em dispositivos móveis |
| 14.6.10 | Fontes OLAP ou relacionais e dados não estruturados; métricas reutilizáveis | 5.4.1.1 | 🟡 Parcial | Catálogo e datasets do schema `dash` (relacional) | Sem evidência de suporte a arquivos não estruturados |
| 14.6.11, 14.6.19, 14.6.25 | Incorporar painéis e objetos visuais em páginas HTML externas, com ou sem controle de acesso; componentes de terceiros | — | ⚪ + 🔴 | Sem evidência | — |
| 14.6.12 | Linhas de tendência, séries temporais e detecção de outliers | 5.4.2.6(c), 5.4.3.5 | 🟡 Parcial | Widget `linhas.js` (séries temporais) | Sem evidência de tendência e outliers |
| 14.6.13–14.6.14, 14.6.21 | Combinar atributos como métricas ou dimensões; relacionamento automático; criação no navegador | 5.4.2.5 | ✅ Atendido | Modelador visual com sugestões por FK e por nome ([Entrega 21](../entregas/21-dashboard-builder.md)) | — |
| 14.6.15, 14.6.24 | Hierarquias sem limite de níveis; estruturas multidimensionais | 5.4.2.4 | 🟡 Parcial | `drill-down.js`, `pivot.js` | Limite de níveis não documentado |
| 14.6.16 | Tipos de gráfico, incluindo treemap, cascata, bolha com 3 variáveis e eixo duplo | 5.4.2.6 | 🟡 Parcial | 12 tipos de widget ([Entrega 21](../entregas/21-dashboard-builder.md)) | Faltam treemap, cascata e bolha |
| 14.6.18 | Incorporar dados ou gráficos externos via API | — | ⚪ + 🔴 | Sem evidência | — |
| 14.6.22–14.6.23 | Funções de métrica (texto, datas, lógicas, estatísticas como mediana, moda, quartis, percentis, variância e desvio) e operadores de intervalo | 5.4.6.1 | 🟡 Parcial | `GramaticaCampoCalculado` | Sem evidência das funções estatísticas |
| 14.6.27 | Senha de uso único (OTP) com validade de 30 s | 5.1.5(e) | ✅ Atendido | [Entrega 04](../entregas/04-mfa-totp.md) (TOTP) | O MFA é opcional por usuário; permitir torná-lo obrigatório se exigido |
| 14.6.28 | Mapa da situação do trânsito em tempo real, por regiões, agrupando por arquivos georreferenciados | 5.3.4.2(a), 5.4.4.2(d) | 🟡 Parcial | `mapa-situacao-transito/` (consome API externa `obter-situacao-transito`), `mancha-monitorada/` | Importar as regiões de Curitiba por arquivo (shapefile ou GeoJSON) |
| 14.6.29, 14.6.31 | Linhas de congestionamento com sentido; última lentidão dos últimos 30 min e picos | 5.3.2.1(d), 5.3.7.4(d) | 🔴 Desenvolver | Sem evidência (Salvador 5.3.7.4 não implementado; ver [gaps vermelhos](../entregas/gaps-vermelhos-pendentes.md)) | — |
| 14.6.30 | Mapa com as ocorrências em tempo real, filtros e câmeras com um clique | 5.3.4.1, 5.4.4.2 | ✅ Atendido | `mapa-interativo/`, `mapa-alertas-ocorrencias/`, `mapa-equipamento/` | Confirmar a integração com as câmeras do monitoramento público municipal |
| 14.6.32 | Download em CSV da série histórica do dia | 5.2.8.3 | ✅ Atendido | [Entrega 11](../entregas/11-exportacao-padronizada.md) | — |
| 14.6.38 | IA para padrões comportamentais (dinâmica criminal) | — | ⚪ + 🟡 | `perfil-comportamental/`, `correlacionamento_automatico*` | A análise estatística existe; IA propriamente dita, não |
| 14.6.40–14.6.43 | Retenção: leituras durante o contrato (acesso rápido por 90 dias e lento depois), imagens por 30 e 90 dias, imagens de infratores durante o contrato | 5.7.5.3, 5.8.5.3 | 🟡 Parcial | [Entrega 12](../entregas/12-retencao-dados.md) | Sem evidência de armazenamento em camadas (rápida e lenta) |
| 14.6.46 | Tela de configuração: ligar e desligar regras de alerta; número de caracteres de semelhança | 5.3.7.6, 5.3.1.3 | ✅ Atendido | `configuracao-monitoramento/`; `ConfiguracaoEquipamento` (`QtdeErrosSemelhanca`) | — |
| 14.6.47–14.6.48 | Mapa em camadas com alertas e veículos monitorados; alertas em tempo real com cores e sons por tipo | 5.3.3.4, 5.3.4.1 | ✅ Atendido | `mapa-alertas-ocorrencias/`, `ConfiguracaoSons`, WebSocket `ALERTA-NOTIFICACAO` | — |
| 14.6.49–14.6.50, 14.6.54 | Tipos mínimos (roubado, clonado, clandestino, sequestro, comboio e outros); cadastro manual com semelhança e anotações | 5.3.2.1, 5.3.2.2 | ✅ Atendido | `TipoAlertaOcorrencia` (roubado, furtado, clandestino, sequestro, clonado); `cad_veiculo_monitorado` (cor, marca, `texto_adesivo`); `anotacao_contributiva` | — |
| 14.6.51 | Detecção automática de clonado (deslocamento impossível ou características divergentes) | — | ⚪ + 🟡 | Regra `clonado` em `ConfiguracaoEquipamento` | A lógica fica no banco e não foi verificada; divergência por texto de adesivo depende de 8.2.8 |
| 14.6.52 | Detecção automática de transporte clandestino (horários regulares, concentração, cruzamento com linhas de ônibus) | — | ⚪ + 🟡 | Regra `clandestino` (janelas de manhã e tarde, número de passagens) | Sem evidência do cruzamento com o cadastro de ônibus |
| 14.6.53 | Comboio suspeito (2 ou mais veículos, mais de 3 pontos, ponto de interesse, seguindo carro-forte) | — | ⚪ + 🟡 | Regra `comboio` (intervalo), `ponto-interesse/` | "Seguindo carro-forte" depende de 8.2.5 |
| 14.6.55–14.6.56 | Pop-up em qualquer tela, com miniatura, som e cor, abrindo a tratativa | 5.3.3.4 | 🟡 Parcial | Barra de navegação com contador e som (WebSocket) | Sem evidência de miniatura no pop-up |
| 14.6.57–14.6.59 | Cadastro e busca de alertas com coringas, características, expiração, destinatários e tipo de notificação | 5.3.3.2, 5.3.3.3, 5.3.6.2 | ✅ Atendido | `monitorado/`, `/GrupoNotificacao`, `/TipoNotificacao`, `consulta-alerta-ocorrencia/` | — |
| 14.6.60–14.6.61 | Tratativa (descartar, validar com edição, vincular, desativar, anotar), que transforma o alerta em ocorrência | 5.3.1.3, 5.3.1.4 | ✅ Atendido | `/Alerta` (`descartarAlerta`, `processarAlertasVinculados`), `/Ocorrencia` (`gerarOcorrencia`), `motivo_descarte` | Confirmar a edição de cor e marca na validação |
| 14.6.62 | Ações complementares: SMS, WhatsApp, Telegram, e-mail; designação a usuário ou grupo | 5.3.5.1, 5.3.1.3(a) | 🟡 Parcial | Pacote `notificacao` (SendGrid/SMTP, Twilio e outros provedores de SMS), `atendimento/`, `guarnicao/` | Sem evidência de WhatsApp e Telegram |
| 14.6.63–14.6.66 | App Android com tratativa, designação automática por proximidade, aceite, imagens e anotações | 5.3.5.1(d) | 🔴 Desenvolver | `agente_localizacao_atual/hist` (localização de agentes), `blitz-abordagem/` (web) | Salvador 5.3.5.1 também está parcial (sem app) |
| 14.6.67, 14.6.92 | Consulta e relatório de ações complementares (encerrar, redesignar) | — | ⚪ + 🟡 | `atendimento-ocorrencias/` | A entidade "ação complementar" não está modelada |
| 14.6.68–14.6.70 | Monitoramento em tempo real por equipamento ou grupo: último veículo com 2 miniaturas; criar alerta a partir da passagem | — | ⚪ + ✅ | `veiculo-tempo-real/veiculos-passagem.jsp` | Confirmar a criação de alerta a partir da passagem |
| 14.6.71–14.6.72 | Monitoramento de veículos de interesse por filtro (placa, classe, cor, marca, modelo, adesivo) | — | ⚪ + 🟡 | `veiculo-tempo-real/` | Sem evidência de filtro por cor e por adesivo |
| 14.6.73–14.6.74 | Consulta de veículos com coringas e características; miniaturas; gerar alerta, ver trajeto no mapa, corrigir dados | 5.3.3.2(e) | ✅ Atendido | `consulta-veiculo/`, `mapa-passagens/`, `/Veiculo` (`alterarPlaca`), parâmetro `ExigirPlacaCompleta` | O filtro por texto de adesivo depende de 8.2.8 |
| 14.6.75–14.6.76 | Exibir dados de terceiros: proprietário, antecedentes, alertas das polícias | 5.4.1.1(f) | 🟡 Parcial | Tabelas `proprietario*`, `antecedentes_criminais`, `registro_fato*` (com boletim) | Falta a integração com as bases do Paraná e das forças policiais |
| 14.6.77–14.6.78 | Grafo de veículos correlacionados (fraca, média ou forte, configurável) | — | ⚪ + ✅ | `veiculos-correlacionados/` (Cytoscape); parâmetros `qtd_pas_correlacao_baixa/media/alta` | — |
| 14.6.79 | Mapa com gráficos de colunas de alertas e ocorrências por tipo e período | 5.3.7.7, 5.4.4.2 | ✅ Atendido | Mapas → "Quantitativos em camadas"; `/AlertaOcorrencia` (`QuantitativosAlertasOcorrencias`) | — |
| 14.6.80–14.6.81 | Dashboard com filtros e gráficos de VDM, ocupação e velocidade média, comparados com semana, mês e ano anteriores | 5.4.2.3, 5.4.2.4, 5.4.3.4, 5.4.3.7(b) | 🟡 Parcial | `taxa-ocupacao-via.jsp`, `velocidade-media.jsp`, [Entrega 15](../entregas/15-kpis-configuraveis.md), [Entrega 21](../entregas/21-dashboard-builder.md) | Sem evidência de comparativo com períodos anteriores (Salvador 5.4.3.7 não implementado) |
| 14.6.82 | Alerta automático de anomalia em relação à semana, ao mês e ao ano anteriores | 5.4.3.5–5.4.3.6 | 🟡 Parcial | [Entrega 15](../entregas/15-kpis-configuraveis.md) (metas e alertas de KPI) | Sem detecção por comparação histórica |
| 14.6.83 | Mapa com colunas de VDM, velocidade média e ocupação por equipamento | 5.4.4.2(c, d) | 🟡 Parcial | `mapa-equipamento/`; "Quantitativos em camadas" (só alertas) | Sem evidência de indicadores de tráfego no mapa |
| 14.6.84–14.6.88, 14.6.95 | Contagem de pedestres e bicicletas (até 5 equipamentos) e relatório | — | ⚪ + 🔴 | Sem evidência | — |
| 14.6.89 | Relatórios em Excel e PDF: VDM (total, por classe, por marca e modelo), velocidade média, ocupação por classe, alertas de tráfego | 5.4.5.1(a–c), 5.4.5.2 | 🟡 Parcial | Relatórios de velocidade e ocupação; [Entrega 11](../entregas/11-exportacao-padronizada.md) | Sem evidência da quebra por marca e modelo nem do relatório de alertas de tráfego |
| 14.6.90–14.6.91 | Relatórios de alertas e ocorrências do cerco eletrônico | 5.3.6.4 | ✅ Atendido | `relatorio-detalhado-alertas.jsp`, `ocorrencias-detalhado.jsp`, `extrato-alerta-ocorrencia.jsp` | — |
| 14.6.93–14.6.94 | Relatórios de detecção de carro-forte e de cargas perigosas | — | ⚪ + 🔴 | Sem evidência | Depende de 8.2 |

As linhas 14.6.34–14.6.35 (video wall) tratam de hardware e ficaram fora da contagem.

### Capítulos 18, 19 e 20 — Amostra, teste de conceito e relatórios contratuais (parte de sistema)

| Item Curitiba | Requisito (resumo) | Item(ns) Salvador correspondente(s) | Situação no GTW/Muralha | Evidência (módulo/tela/plano/entrega) | Observação |
|---|---|---|---|---|---|
| 18.5.4–18.5.5, 18.6 | Amostra: CPI e Sistema de Gestão de Tráfego funcionando por 48 h | POC 6.2 (operação ininterrupta por 2 dias) | 🟡 Parcial | Sistema em operação; [roteiro de testes manuais](../entregas/roteiro-testes-manuais.md) | Precisa de ambiente de demonstração com equipamento no padrão de Curitiba e todos os itens P1 prontos |
| 18.9.13 (sic), 19.9.15 | Verificar todas as funcionalidades de software do termo | POC 5.2.8.2–5.2.8.3 | 🟡 Parcial | Esta análise | Depende dos itens 🔴 e 🟡 de prioridade P1 |
| 20.1.3.1–20.1.3.3 | Relatórios mensais de acompanhamento: contagem volumétrica diária por classe, autuações por equipamento e por dia e hora | 5.4.3.7, 5.5.3.3 | 🟡 Parcial | Relatórios existentes; [gaps vermelhos](../entregas/gaps-vermelhos-pendentes.md) (relatório mensal) | Sem emissão automática periódica (Salvador 5.5.3.3 e 5.4.3.7 também pendentes) |

O teste de transferência e importação (19.9.14) está na linha 4.1.3.10. Os enquadramentos do teste (19.9.2–19.9.5) estão na linha 4.1.1.

---

## 3. Pontos concordantes

Requisitos que Curitiba e Salvador exigem igualmente e que o GTW/Muralha já atende (✅), agrupados por tema.

### 3.1 Recepção, armazenamento e integridade
- Recepção em tempo real de todas as passagens e imagens: Curitiba 3.11.2, 4.1.3.5, 7.1–7.2 ↔ Salvador 5.2.1.1, 5.6.2.1, 5.2.2.4 ↔ `CSXEventsWS`, `veiculo_tempo_real`, `veiculo_tempo_real_imagem`.
- Recepção de dados 24x7 com os originais preservados: Curitiba 4.4.3, 4.4.9.8, 3.11.11 ↔ Salvador 5.6.2.4–5.6.2.5, 5.5.4.2, 5.2.1.2 ↔ [Entrega 02](../entregas/02-sha256-integridade.md).
- Contagem e classificação de todos os veículos: Curitiba 3.10.1–3.10.2 ↔ Salvador 5.7.6.2 ↔ relatórios de fluxo por classificação.

### 3.2 Validação de infrações (CPI)
- Plataforma web de auditoria, classificação consistente ou inconsistente com motivo: Curitiba 9.1, 9.2, 3.11.3–3.11.5 ↔ Salvador 5.2.3.1, 5.2.4.1 ↔ GTW `processo/*`, [Entrega 05](../entregas/05-dupla-analise.md).
- Correção de placa, zoom e brilho sem alterar o original: Curitiba 9.8–9.9 ↔ Salvador 5.2.4.2, 5.2.4.5, 5.5.4.3 ↔ `/CorrecaoPlaca`, `proc_infracao_validacao.jsp`.
- Obliteração: Curitiba 3.11.10, 9.10 ↔ Salvador 5.2.4.3–5.2.4.4 ↔ [Entrega 06](../entregas/06-obliteracao-imagens.md).
- Geração de AIT: Curitiba 9.11 ↔ Salvador 5.2.5.4–5.2.5.5 ↔ `/ait/AITPDF`, `controleAIT/*`.

### 3.3 Segurança, acesso e logs
- Usuário, senha, grupos e privilégios: Curitiba 9.4–9.5 ↔ Salvador 5.1.5–5.1.6 ↔ `sis_grupo`, [Entrega 03](../entregas/03-politica-senhas.md).
- Log de todas as ações: Curitiba 9.6, 14.6.45 ↔ Salvador 5.1.7–5.1.10 ↔ [Entrega 01](../entregas/01-log-auditoria.md).
- OTP de 30 s: Curitiba 14.6.27 ↔ Salvador 5.1.5(e) ↔ [Entrega 04](../entregas/04-mfa-totp.md).
- LGPD: Curitiba 9.19.8 ↔ Salvador 5.1.4, 5.4.1.3, 5.7.8 ↔ [Entregas 06](../entregas/06-obliteracao-imagens.md), [12](../entregas/12-retencao-dados.md) e [13](../entregas/13-anonimizacao-placas.md).

### 3.4 Equipamentos e configuração
- Enquadramento por local e operação em horário programado: Curitiba 3.3.1, 3.6.2.1, 4.3.3.6 ↔ Salvador 5.6.7; POC 5.2.4 ↔ `configuracao_equipamento*`, `ConfigEquipWS`.
- Vídeo ao vivo das câmeras de contexto: Curitiba 4.5.3, 4.6.3, 14.6.36 ↔ Salvador 5.3.7.2(e) ↔ `video-ao-vivo/`, `visualizacao-cameras/`.

### 3.5 OCR e características do veículo
- Padrão antigo e Mercosul; associação a listas: Curitiba 7.3.1, 7.6 ↔ Salvador 5.7.1.1, 5.3.1.1 ↔ LAP atual, `cad_veiculo_monitorado`.
- Cor e marca: Curitiba 8.2.6–8.2.7 ↔ Salvador 5.7.6.2; POC 6.4.4 ↔ `ia.veiculo_caracteristica`.

### 3.6 Relatórios
- Relatórios de passagens, VDM, infratores e estatísticas com gráficos: Curitiba 4.1.3.12–4.1.3.16 ↔ Salvador 5.2.8.1, 5.4.5.1 ↔ menus Relatórios e Gráficos.
- Plataforma web, novos relatórios sob demanda, totais por local e velocidade média: Curitiba 10.1–10.3, 10.2.1–10.2.3, 10.2.6 ↔ Salvador 5.2.8, 5.4.5.1, 5.5.3.5 ↔ [Entrega 21](../entregas/21-dashboard-builder.md), `velocidade-media.jsp`.
- Exportação em CSV: Curitiba 14.6.32 ↔ Salvador 5.2.8.3 ↔ [Entrega 11](../entregas/11-exportacao-padronizada.md).

### 3.7 Cerco eletrônico e alertas (Muralha Digital)
- Configuração de regras e de semelhança de placa: Curitiba 14.6.46 ↔ Salvador 5.3.7.6, 5.3.1.3 ↔ `configuracao-monitoramento/`.
- Tipos de alerta, cadastro manual e anotações: Curitiba 14.6.49–14.6.50, 14.6.54 ↔ Salvador 5.3.2.1–5.3.2.2 ↔ `TipoAlertaOcorrencia`, `monitorado/`.
- Mapa em camadas com cores e sons; mapa de ocorrências e de quantitativos: Curitiba 14.6.30, 14.6.47–14.6.48, 14.6.79 ↔ Salvador 5.3.3.4, 5.3.4.1, 5.3.7.7, 5.4.4.2 ↔ `mapa-alertas-ocorrencias/`, `mapa-interativo/`.
- Busca, tratativa, vínculo e ocorrência: Curitiba 14.6.57–14.6.61 ↔ Salvador 5.3.1.3–5.3.1.4, 5.3.3.2–5.3.3.3, 5.3.6.2 ↔ `/Alerta`, `/Ocorrencia`, `consulta-alerta-ocorrencia/`.
- Consulta de veículos com coringas e trajeto: Curitiba 14.6.73–14.6.74 ↔ Salvador 5.3.3.2(e) ↔ `consulta-veiculo/`, `mapa-passagens/`.
- Relatórios de alertas e ocorrências: Curitiba 14.6.90–14.6.91 ↔ Salvador 5.3.6.4 ↔ relatórios `relatorio-detalhado-alertas.jsp`, `ocorrencias-detalhado.jsp`.

### 3.8 Painéis analíticos e infraestrutura
- Modelagem de dados no navegador com relacionamento automático: Curitiba 14.6.13–14.6.14, 14.6.21 ↔ Salvador 5.4.2.5 ↔ [Entrega 21](../entregas/21-dashboard-builder.md).
- Interface e documentação em PT-BR; servidor em ambiente virtual: Curitiba 14.2, 14.4, 14.5, 14.6.17 ↔ Salvador 5.4.2.2, 5.8.1.1 ↔ `pages/ajuda/`, Tomcat 9 + SQL Server.

---

## 4. Desenvolvimentos necessários

**Prioridade:** **P1** = exigido na amostra ou no teste de conceito (Curitiba 18.5.4–18.5.5, 18.9, 19.9.13–19.9.15; demonstração explícita em 9.19.10 e 19.9.14) ou núcleo da CPI e do cerco eletrônico. **P2** = necessário para o início da operação. **P3** = depende de definição da contratante ou é evolução.

**Padrão de solução** (todos os itens): pacote `muralha.digital.<modulo>`, `<Entidade>Servlet` em `/MuralhaDigital/<Entidade>?acao=…`, JSON/Gson, JDBC via `Conexao.getConexao()`, sessão e permissão ([padrões de código](../requisitos/06-padroes-de-codigo.md) §1.1), telas em `pages/<tela>/` no [padrão visual](../requisitos/05-padrao-visual.md), jobs Quartz e tabelas complementares no schema `muralha`, com scripts em [`docs/banco-de-dados/migracoes/`](../banco-de-dados/migracoes/). Nenhum framework novo sem aprovação explícita.

### 4.1 CPI e validação de infrações

| Item(ns) Curitiba | O que falta | Proposta de solução | Dependências | Complexidade | Prioridade |
|---|---|---|---|---|---|
| 9.19.9–9.19.10 (+ 4.1.3.8.7, 9.19.2) | Cadeia de custódia com encadeamento, trilha imutável e verificação independente | Pacote `custodia`: tabela `muralha.custodia_registro` (id do registro, tipo de evento, hash SHA-256 do conteúdo, hash do elo anterior, assinatura, data e hora UTC). Os eventos são recepção, validação, invalidação, correção de placa, obliteração e geração de AIT. A assinatura usa `java.security.Signature` (RSA ou ECDSA do próprio JDK) com chave da contratada; a contratante pode exigir carimbo do tempo de uma ACT. Gravação só por INSERT (trigger impede UPDATE e DELETE). `CustodiaServlet` (`verificarRegistro`, `verificarCadeia`, `exportarProva`) e tela `pages/custodia/` para verificação independente. Reaproveita o SHA-256 da [Entrega 02](../entregas/02-sha256-integridade.md) e o log da [Entrega 01](../entregas/01-log-auditoria.md) | Carimbo do tempo pode exigir biblioteca ou serviço externo (aprovação); custódia de chaves | Alta | P1 |
| 3.11.7–3.11.9, 3.11.12–3.11.13, 9.7 | Remessas distintas para consistentes e inconsistentes, com motivo; categoria "descartada" | Estender a [Entrega 14](../entregas/14-gestao-lotes.md): coluna `tipo_lote` (CONSISTENTE, INCONSISTENTE, DESCARTADA) e motivo obrigatório nos itens de inconsistentes; tabela `muralha.motivo_invalidacao` com natureza TÉCNICO, NÃO TÉCNICO ou DESCARTE AUTORIZADO, mapeada para os motivos 3.11.5.1–3.11.5.11 | Critérios da contratante (3.11.3) | Média | P1 |
| 3.11.15, 4.1.3.20, 10.2.4, 20.1.22.3, 20.1.24 | Aproveitamento pela fórmula de Curitiba, com imagens teste e desconto | Função SQL `fcn_aproveitamento_curitiba(periodo, local)` mais relatório e KPI ([Entrega 15](../entregas/15-kpis-configuraveis.md)) com as faixas de desconto (5% a 20%) parametrizadas em `config_chave_valor` | Item 4.5.7; esclarecer a fórmula (ver Riscos) | Média | P1 |
| 4.5.7–4.5.10, 4.6.7–4.6.10, 9.3 | Imagem teste a cada 30 min por faixa e validação contra o fluxo | Job Quartz `ImagemTesteJob`: confere por faixa se houve imagem teste em 30 min, cruza com o fluxo em `veiculo_tempo_real` e gera pendência ou alerta. Fila de classificação APROVEITÁVEL ou NÃO APROVEITÁVEL reaproveitando `imagem_teste` | Equipamento envia a imagem com marcação de teste | Média | P1 |
| 3.11.14 | Homologação inicial de imagens por faixa | Tela `pages/homologacao-faixa/` com checklist por faixa (incluindo panorâmica), aprovação pela contratante e laudo em PDF; tabela `muralha.homologacao_faixa` | — | Baixa | P2 |
| 9.15–9.17 | Consulta de passagens com 19 campos e filtros de infração | Ampliar `/Veiculo` (`consultaPorFiltrosTela`) e a view de consulta com comprimento, marcas de 8.2, texto de adesivo, classe de pesagem, eixos, PBT e peso por eixo. Novos filtros em `consulta_infracao_completa.jsp` (emergência, viatura, táxi) | Itens 8.2 e 4.4.10.4 | Média | P1 |
| 4.3.3.1.6, 4.3.3.2.5 | Vídeo MPEG4 da infração de semáforo ligado à infração | Associar o vídeo recebido à infração (tabela `muralha.infracao_video`) e exibir o player na tela de validação e no detalhe | Envio pelo equipamento | Baixa | P1 |
| 4.1.1–4.1.2, 4.3.1, 19.9.2–19.9.5 | Enquadramentos e tarjas de Curitiba | Script de carga dos enquadramentos (incluindo 56810, 56900 e 59910 do teste) com descrição CTB e leiaute da tarja (4.3.4) | Confirmar o cadastro atual no banco (somente leitura) | Baixa | P1 |
| 4.1.3.2–4.1.3.3, 7.4 | Metadados legais na CPI | Colunas complementares (coordenada decimal, faixa, número Inmetro, série, datas de verificação metrológica, retardo ou permanência) em tabela `muralha.infracao_metadado_legal`, usadas no AIT | Leiaute do equipamento | Baixa | P1 |

### 4.2 Notificações (NAT/NAP) e integrações com o DETRAN-PR e o sistema corporativo

| Item(ns) Curitiba | O que falta | Proposta de solução | Dependências | Complexidade | Prioridade |
|---|---|---|---|---|---|
| 11.2, 12.2 | Envio real de AIT, NAT, NAP e infrações validadas à plataforma corporativa | Pacote `integracao`: `IntegracaoCorporativaJob` (fila com reenvio e backoff), tabelas `muralha.integracao_envio` e `muralha.integracao_log` (Salvador 5.2.6.3), cliente HTTPS com `httpclient`/`unirest-java` (já no `pom.xml`). A ação `enviar` do `LoteServlet` passa a enfileirar. O bloqueio de não validados já existe ([Entrega 14](../entregas/14-gestao-lotes.md)) | Leiautes, protocolo e credenciais da contratante; escopo do SNE | Alta | P2 |
| 9.14 | Arquivos de NAT e NAP no leiaute de Curitiba | Novos modelos Jasper (NAT e NAP) e gerador de arquivo em lote com o leiaute definido | Leiautes da contratante | Média | P2 |
| 12.3, 9.12, 14.6.75–14.6.76 | Recebimento dos dados enriquecidos (veículo, proprietário, alertas policiais) e consulta à base do Paraná | Mesmo pacote `integracao`: endpoint de recebimento e tabela `muralha.veiculo_enriquecido` com finalidade e expiração (LGPD); exibição em `consulta-veiculo/` e na tratativa de alerta; reaproveitar `proprietario*` e `antecedentes_criminais` | Interface da contratante; convênios com as polícias | Alta | P2 |
| 9.13 | Até 1.000 consultas por mês à base nacional | Cliente da base nacional com cota mensal controlada (`muralha.consulta_base_nacional`) e log | Credenciamento junto ao órgão gestor | Média | P3 |
| 4.1.3.14 | Espécie e tipo do veículo no relatório de infratores | Usar os dados enriquecidos (12.3) no relatório | Item 12.3 | Baixa | P3 |

### 4.3 OCR e reconhecimento de padrões

| Item(ns) Curitiba | O que falta | Proposta de solução | Dependências | Complexidade | Prioridade |
|---|---|---|---|---|---|
| 8.2.1–8.2.5, 8.2.8, 8.3 | Atributos de padrão na passagem e alerta ao Sistema de Gestão | Tabela `muralha.veiculo_padrao` (id da passagem, tipo: EMERGENCIA, VIATURA, CARGA_PERIGOSA com código ONU, TAXI, CARRO_FORTE; texto de adesivo; score). Recepção no `CSXEventsWS` (campo opcional) e novos tipos em `tipo_alerta_ocorrencia` | Leiaute do equipamento; IA embarcada (4.5.12) | Média | P1 |
| 8.5 | Alerta bloqueante ao auditor | Na validação (GTW `processo/*` e [dupla análise](../entregas/05-dupla-analise.md)): modal que exige confirmação e justificativa quando a infração tem padrão EMERGÊNCIA, VIATURA ou TÁXI, com registro no log de auditoria | Item 8.2 | Baixa | P1 |
| 8.4 | Processar em servidor os padrões das imagens dos lotes 2 e 3 | Serviço de inferência separado (fora do Tomcat), chamado por fila; o resultado grava em `veiculo_padrao`. Alternativa: contratar um motor de terceiros por API | Aprovação de componente novo; GPU ou servidor dedicado | Alta | P1 |
| 8.6–8.7, 4.1.3.17–4.1.3.18, 14.6.93–14.6.94 | Relatórios de emergência, carga perigosa (ONU) e carro-forte | Relatórios em `pages/relatorios/` (motivo, servlet, procedure e JSP) com a classificação consistente ou inconsistente; envio mensal automático | Item 8.2 | Baixa | P2 |
| 7.5 | Acerto do OCR separado por período (dia ou noite) | Estender o painel da [Entrega 08](../entregas/08-painel-assertividade.md) com a dimensão período, metas de 90% e 85% e relatório de aferição | Amostra de referência para aferição | Baixa | P1 |
| 7.7 | Três tamanhos de imagem configuráveis | Parâmetro por equipamento em `configuracao_equipamento` e tipo de imagem em `veiculo_tempo_real_imagem` | Equipamento | Baixa | P2 |
| 7.8, 13.2.3 | API para outros sistemas e órgãos | `ApiDadosServlet` (REST/JSON, autenticação por token, limite de taxa, log), com dados de passagens anonimizados quando aplicável ([Entrega 13](../entregas/13-anonimizacao-placas.md)) | Definição de consumidores | Média | P3 |

### 4.4 Relatórios

| Item(ns) Curitiba | O que falta | Proposta de solução | Dependências | Complexidade | Prioridade |
|---|---|---|---|---|---|
| 14.6.89 | VDM por marca e modelo; ocupação por classe; relatório de alertas de tráfego | Novos relatórios no padrão de `pages/relatorios/`; exportação Excel/PDF ([Entrega 11](../entregas/11-exportacao-padronizada.md)) | Item 14.6.82 | Média | P1 |
| 10.2.5, 10.2.7, 4.5.13 | 85º percentil e velocidade entre dois pontos no Muralha | Portar para servlets do Muralha os cálculos dos relatórios RJ (`ItemFluxoVeicular`, `Relatorio1TempoPercurso`) | — | Baixa | P1 |
| 20.1.3.1–20.1.3.3 | Emissão mensal automática | Job Quartz `RelatorioMensalJob` (gera PDF/XLSX, guarda e envia por e-mail). Atende também Salvador 5.4.3.7 e 5.5.3.3 | — | Média | P2 |
| 4.4.10.10 | Relatórios de pesagem por classe, eixo e PBT | Relatórios sobre `veiculo_pesagem` com as 12 classes | Item 4.4.10.3 | Baixa | P2 |

### 4.5 Gestão de tráfego, cerco eletrônico, mapa de calor e painéis públicos

| Item(ns) Curitiba | O que falta | Proposta de solução | Dependências | Complexidade | Prioridade |
|---|---|---|---|---|---|
| 14.6.29, 14.6.31, 14.1, 14.6.26 | Lentidão e congestionamento por segmento e sentido; mapa de calor do viário com GPS e apps | Pacote `trafego`: job de importação dos dados de mobilidade (modelo da [Entrega 19](../entregas/19-sensores-externos.md)), tabela `muralha.trafego_segmento` (geometria em GeoJSON, sentido, velocidade, nível, janela de 30 min) e camada Leaflet ou Google Maps com cores por nível; acúmulo por hora para os picos | Fonte de dados (GPS ou app) fornecida pela contratante | Alta | P1 |
| 14.6.80–14.6.83 | Comparativo com semana, mês e ano anteriores; alerta de anomalia; indicadores de tráfego no mapa | `TrafegoIndicadorServlet` (VDM, ocupação e velocidade média com séries comparativas), job de detecção de anomalia (desvio em relação à média histórica, limite configurável) que gera alerta de tipo "tráfego", e colunas por equipamento no `mapa-equipamento/` | — | Média | P1 |
| 14.6.28 | Regiões de Curitiba por arquivo georreferenciado | Importar shapefile ou GeoJSON das regionais para `muralha.regiao_geo` e agrupar no mapa | Arquivo da contratante | Média | P2 |
| 14.6.7, 14.6.11, 14.6.19, 14.6.25, 14.6.18 | Painéis públicos sem login, incorporáveis e com dados externos | `PainelPublicoServlet` só de leitura, com token por painel, limitado a datasets marcados como públicos e agregados ou anonimizados; exportação CSV/XLSX; incorporação por iframe com cabeçalhos CSP; widget "fonte externa" (URL JSON com lista branca) | Política de publicação (LGPD) | Média | P2 |
| 14.6.12, 14.6.16, 14.6.22–14.6.23, 14.6.6, 14.6.15 | Tendência, outliers, treemap, cascata, bolha; funções estatísticas; drill-up e drill-through | Novos widgets na [Entrega 21](../entregas/21-dashboard-builder.md); funções estatísticas em `GramaticaCampoCalculado` (no SQL Server 2016: `PERCENTILE_CONT`, `STDEV`, `VAR`); drill-up pelo breadcrumb e drill-through para a consulta detalhada | Componentes de gráfico já usados no projeto | Média | P2 |
| 14.6.9, 14.6.20, 14.6.8 | Responsividade validada em celular; log de carga | Testes e ajustes em celular e tablet; log de execução dos jobs de carga com alerta de falha | — | Baixa | P2 |
| 14.6.62 | WhatsApp e Telegram | Novos canais no pacote `notificacao` por HTTP (`httpclient`), com fila e reenvio (atende também Salvador 5.3.5.3) | Contas e modelos de mensagem aprovados | Média | P2 |
| 14.6.63–14.6.67, 14.6.92 | App Android e ações complementares | Entidade `muralha.acao_complementar` (ocorrência, designado, status, aceite, anotações, anexos), `AcaoComplementarServlet` e tela de consulta; designação automática pelo `agente_localizacao_atual`. Para o app: nativo Android ou PWA empacotada, em projeto separado | Aprovação de stack nova (fora de Java/JSP) | Alta | P2 |
| 14.6.55–14.6.56, 14.6.71–14.6.72 | Pop-up com miniatura; filtros por cor e adesivo no tempo real | Incluir miniatura no XML do WebSocket `ALERTA-NOTIFICACAO` (sem quebrar o contrato atual) e filtros em `veiculo-tempo-real/` | Item 8.2.8 | Baixa | P1 |
| 14.6.51–14.6.53 | Critérios de clonado, clandestino e comboio | Revisar as procedures das regras; incluir divergência de características, cruzamento com linhas de ônibus e "seguindo carro-forte" | Itens 8.2.5 e 8.2.8; cadastro de linhas | Média | P1 |
| 14.6.84–14.6.88, 14.6.95 | Pedestres e bicicletas | Pacote `contagem`: recepção, tabela `muralha.contagem_nao_motorizada` e relatório | Equipamentos definidos pela contratante | Baixa | P3 |
| 14.6.38 | IA de padrões comportamentais | Evoluir `perfil-comportamental/` e `correlacionamento_automatico` com detecção de rotinas e anomalias | Definição de escopo | Alta | P3 |

### 4.6 Segurança, acesso e logs

| Item(ns) Curitiba | O que falta | Proposta de solução | Dependências | Complexidade | Prioridade |
|---|---|---|---|---|---|
| 6.4, 9.19.1.1, 9.19.3–9.19.4 | TLS com autenticação mútua e VPN | Terminação mTLS no nginx (já usado no projeto) com `ssl_verify_client` e CA da Prefeitura, repassando a identidade do equipamento ao `CSXEventsWS`, que confere o certificado com o cadastro do equipamento | Certificados emitidos pela Prefeitura (9.19.3) | Média | P1 |
| 9.19.5, 4.1.3.4 | NTP (ntp.br) e monitoramento de desvio | Configurar NTP nos servidores e criar job que compara a hora do equipamento (no evento) com a do servidor e alerta desvios | Infraestrutura | Baixa | P1 |
| 9.18, 3.5.2, 4.2.2.6, 4.3.3.5.1 | Tela de logs de equipamento com comunicação e energia; novos tipos de evento | `LogEquipamentoServlet` e tela `pages/monitoramento/log-equipamento/` sobre `eventos_csx`, com filtros (Salvador 5.4.7.5); novos tipos: autoteste, display, amarelo piscante, falha de sequência e energia | Leiaute dos eventos do equipamento | Média | P1 |
| 3.6.2.3, 9.19.1 | Status "ativo" composto e SLA de enlace | Estender a [Entrega 18](../entregas/18-disponibilidade.md) com aferição (validade), protocolo na SMDT e heartbeat de comunicação | Cadastro de aferições | Baixa | P2 |
| 14.6.27 | OTP obrigatório | Parâmetro de obrigatoriedade de MFA por grupo na [Entrega 04](../entregas/04-mfa-totp.md) | — | Baixa | P2 |

### 4.7 Infraestrutura de software

| Item(ns) Curitiba | O que falta | Proposta de solução | Dependências | Complexidade | Prioridade |
|---|---|---|---|---|---|
| 4.1.3.23–4.1.3.24, 4.4.10.7–4.4.10.8, 14.6.39 | Especificação pública de integração para os lotes 2 e 3 e para outros municípios | Documento de interface (passagem, infração, imagem, vídeo, evento, pesagem, pacote) e endpoint REST/JSON além do SOAP atual, com validação de esquema, SHA-256 e autenticação mTLS | Itens 6.4 e 9.19.9 | Alta | P1 |
| 4.1.3.10, 4.4.9.4, 19.9.14.1–19.9.14.3 | Importação de coleta manual e Wi‑Fi demonstrável | Tela `pages/importacao-coleta/` sobre `descarga-core`: upload do pacote, conferência de hash, deduplicação e relatório de importação | Formato do pacote do equipamento | Média | P1 |
| 14.3 | Balanceamento de carga e replicação | Vários Tomcat atrás do nginx com sessão persistente (WebSocket), thread `Alertas` e jobs Quartz em modo cluster (JobStore JDBC), SQL Server com replicação ou AlwaysOn | Infraestrutura; testes de concorrência das threads atuais | Alta | P2 |
| 4.1.3.21, 6.3, 6.5–6.6, 14.6.40–14.6.43 | 60 meses em nuvem; armazenamento em camadas; backup em autoatendimento; 10 chaves de acesso | Política de camadas na [Entrega 12](../entregas/12-retencao-dados.md) (banco principal com 90 dias, banco auxiliar ou armazenamento externo para o histórico), exportação de backup pela contratante e contas de acesso de leitura | Provedor de nuvem | Média | P2 |
| 4.4.2, 4.4.10.3–4.4.10.4 | 12 classes de pesagem | Tabela de classes (Res. 882 e Portaria 268) e mapeamento eixos → classe | Leiaute 4.4.10.8 | Baixa | P2 |
| 4.1.3.22 | Limite de velocidade por categoria | Coluna por categoria em `configuracao_equipamento*` | Equipamento | Baixa | P3 |

---

## 5. Itens de Salvador sem correspondência em Curitiba

Já entregues ou em andamento, não são exigidos por Curitiba, mas podem ser oferecidos como diferencial:

- **Dupla análise independente com desempate** (Salvador 5.2.3.2–5.2.3.4): [Entrega 05](../entregas/05-dupla-analise.md). Curitiba exige só a classificação pela contratada (3.11.6).
- **Pré-classificação em até 4 s e aderência de 80%** (Salvador 5.2.2.1, 5.2.2.3) e **SLA de latência de 4 s** (Salvador 5.7.9): [Entregas 07](../entregas/07-sla-latencia.md) e [08](../entregas/08-painel-assertividade.md).
- **Amostragem estatística para auditoria e gestão de lotes** (Salvador 5.2.5.2): [Entrega 14](../entregas/14-gestao-lotes.md).
- **SLA de pré-processamento de 72 h** (Salvador 5.5.2.1): [Entrega 10](../entregas/10-sla-preprocessamento.md).
- **CPF ou matrícula, política de senhas e inatividade** (Salvador 5.1.5 a, c, d): [Entregas 09](../entregas/09-cpf-matricula.md) e [03](../entregas/03-politica-senhas.md).
- **Time-lapse e inversão de cores** (Salvador 5.7.7, 5.5.4.3): [Entrega 17](../entregas/17-timelapse.md).
- **Anonimização estatística de placas** (Salvador 5.7.8): [Entrega 13](../entregas/13-anonimizacao-placas.md). Útil para os painéis públicos de Curitiba 14.6.7.
- **Exportação GIS** (Salvador 5.4.4.4): [Entrega 16](../entregas/16-exportacao-gis.md).
- **Sensores meteorológicos, de alagamento e PMV** (Salvador 5.3.7.2): [Entrega 19](../entregas/19-sensores-externos.md) (incidentes externos).
- **Score de confiança do LAP e registro de várias placas** (Salvador 5.7.4, 5.7.1.4): ainda parciais ou pendentes em Salvador.
- **Datacenter Tier III, RTO e RPO, PRD e PCN** (Salvador 5.8): Curitiba não exige certificação de datacenter.
- **Integração com estacionamento rotativo** (Salvador 5.4.1.1 e).
- **Sistema de Chamados** (Salvador 7.2.3): [Entrega 22](../entregas/22-sistema-chamados.md). Curitiba não pede software de chamados, mas a manutenção corretiva prevê "pronto atendimento aos chamados de suporte técnico" (Curitiba 20.4 i), e o módulo pode registrar esse atendimento.

---

## 6. Riscos e pontos a esclarecer no edital de Curitiba

1. **Quem enriquece os dados do veículo.** Curitiba 9.12–9.13 põe na CPI a consulta à base do Paraná e até 1.000 consultas nacionais por conta da contratada. Já Curitiba 12.1 e 12.3 atribuem o enriquecimento à contratante, pela plataforma corporativa. Pedir esclarecimento sobre a origem dos dados de AIT, NAT e NAP.
2. **Formato das notificações.** Curitiba 9.11 e 9.14 pedem que a CPI gere AIT, NAT e NAP "para impressão", mas Curitiba 11.1 diz que impressão e expedição ficam com a contratante. Confirmar se a entrega é PDF ou arquivo de dados, quais são os leiautes, o protocolo e o escopo do SNE.
3. **IA no servidor × IA só embarcada.** Curitiba 8.4 exige processamento em servidor para as imagens dos lotes 2 e 3, enquanto 4.5.12 e 4.6.12 proíbem IA remota nos equipamentos do Lote 1. O motor de IA no servidor é componente novo (precisa de aprovação, conforme as regras do projeto) e tem custo de infraestrutura.
4. **Padrão de integração definido só depois do contrato** (Curitiba 4.1.3.24, 4.4.10.7). A amostra e o teste de conceito (Curitiba 18 e 19) acontecem antes. É preciso um formato provisório publicado, e há risco de retrabalho.
5. **Cadeia de custódia no teste de conceito** (Curitiba 9.19.9–9.19.10). Os critérios de aceite da "verificação independente" não estão definidos. Se for exigido carimbo do tempo de uma ACT, há dependência externa.
6. **Painéis públicos sem credenciais** (Curitiba 14.6.7) × "acessível apenas por usuário e senha" (Curitiba 14.6.45) × LGPD (Curitiba 9.19.8). Limitar a dados agregados ou anonimizados e confirmar o que pode ser publicado.
7. **Aplicativo Android em loja oficial** (Curitiba 14.6.63). Fica fora da stack atual (Java/JSP); exige aprovação e esforço próprio de desenvolvimento e publicação.
8. **Balanceamento de carga** (Curitiba 14.3). A arquitetura atual tem threads em memória (`Alertas`, WebSocket da Blitz) e jobs Quartz sem cluster; a mudança é estrutural.
9. **Fórmula de aproveitamento.** Curitiba 3.11.15 define o aproveitamento como "diferença" entre aproveitáveis e inconsistentes, mas 4.1.3.20 e 20.1.22.3 usam percentual. Confirmar a fórmula exata, inclusive o tratamento das imagens teste (20.1.24) e das descartadas.
10. **Numeração e referências inconsistentes no Anexo II.** 9.2 cita a alínea "J" e 4.4.1 cita a alínea "G", mas as seções são numeradas. 4.5.6 cita 4.5.4 (deveria ser 4.5.5). 14.6.56, 14.6.69, 14.6.70 e 14.6.72 citam itens trocados (14.6.48 e 14.6.64 em vez de 14.6.55 e 14.6.68). O item "18.9.,13" aparece no cap. 19 e 18.9 remete a 20.1.22.3. Em 6.6 consta "60 (dias)" e em 6.3, 60 meses. O prazo de 19.1.1 está como "xx horas".
11. **Dependências da contratante:** certificados digitais (9.19.3), base de veículos do Paraná (9.12), leiautes corporativos (11.2, 12.2), dados de GPS e apps de mobilidade (14.1, 14.6.26), câmeras do monitoramento público (14.6.30), bases policiais (14.6.75), regiões georreferenciadas (14.6.28) e equipamentos de contagem de pedestres (14.6.84).
12. **Volume do teste de conceito.** Com o limite de velocidade em 35 km/h (19.9.12), o volume de "infrações" será alto. Validar a capacidade de recepção e de validação em 48 h, com as transferências Wi‑Fi e por pen drive (19.9.14) feitas sem dispositivo intermediário.
13. **Horas técnicas** (Lote 1, item 12: 1.000 h/mês). É preciso equipe dimensionada para as integrações de 13.2 e para os novos relatórios sem custo de 10.3.
