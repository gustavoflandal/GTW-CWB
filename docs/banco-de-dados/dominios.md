# Tabelas de domínio (valores atuais)

Tabelas de apoio pequenas (<= 40 linhas) cujo nome sugere domínio (tipo, status, situação, categoria, motivo, perfil...). Valores em 2026-10-08 no ambiente DEV; podem diferir em produção.

## dbo.cad_categoria (14)

| id_categoria | descricao |
|---|---|
| 0 | ************                   |
| 1 | PARTICULAR                     |
| 2 | ALUGUEL                        |
| 3 | OFICIAL                        |
| 4 | EXPERIENCIA                    |
| 5 | APRENDIZAGEM                   |
| 6 | FABRICANTE                     |
| 7 | CHEFE M DIPL                   |
| 8 | C. CONSULAR                    |
| 9 | O. INTERNAC.                   |
| 10 | CORPO DIPLOM                   |
| 11 | ADMINISTRATI                   |
| 12 | A COOP INTER                   |
| 20 | ************                   |

## dbo.cad_situacao (9)

| id_situacao | descricao |
|---|---|
| 0 | Indefinido                     |
| 1 | REG.OUTRA UF                   |
| 2 | BAIXADO                        |
| 3 | COPIA PRONTUAR.                |
| 4 | BLQ.ORD.JUDIC.                 |
| 5 | BLOQ. IND. ADM.                |
| 6 | BLOQ. ROUBO                    |
| 7 | PLACA A APLICAR                |
| 8 | PLACA RESERVADA                |

## dbo.cad_tipo (30)

| id_tipo | descricao |
|---|---|
| 0 | N/D                            |
| 1 | BICICLETA                      |
| 2 | CICLOMOTOR                     |
| 3 | MOTONETA                       |
| 4 | MOTOCICLETA                    |
| 5 | TRICICLO                       |
| 6 | AUTOMOVEL                      |
| 7 | MICROONIBUS                    |
| 8 | ONIBUS                         |
| 9 | BONDE                          |
| 10 | REBOQUE                        |
| 11 | SEMI-REBOQUE                   |
| 12 | CHARRETE                       |
| 13 | CAMIONETA                      |
| 14 | CAMINHAO                       |
| 15 | CARROCA                        |
| 16 | CARRO DE MAO                   |
| 17 | CAMINHAO TRATOR                |
| 18 | TRATOR RODAS                   |
| 19 | TRATOR ESTEIRAS                |
| 20 | TRATOR MISTO                   |
| 21 | QUADRICICLO                    |
| 22 | CHASSI-PLATAFORMA              |
| 23 | CAMINHONETE                    |
| 24 | SIDE-CAR                       |
| 25 | UTILITARIO                     |
| 26 | MOTOR-CASA                     |
| 97 | CICLOMOTOR/ES                  |
| 98 | FABRICANTE                     |
| 99 | INEXISTENTE                    |

## dbo.cad_tipo_cet (6)

| id_tipo_cet | descricao |
|---|---|
| 4 | MOTOCICLETA/MOTONETA                |
| 6 | AUTOMOVEL                           |
| 8 | ONIBUS/MICROONIBUS                  |
| 25 | CAMINHAO/CAMIONETA                  |
| 59 | OUTROS                              |
| 60 | MISTO                               |

## dbo.classe_veiculo (10)

| id_classe | descricao | id_classe_git | id_classe_tr |
|---|---|---|---|
| C | Caminhão        | 3 | C |
| T | Camionete       | 2 | P |
| M | Motocicleta     | 1 | M |
|   | N/A             | 1 | P |
| O | Onibus          | 3 | O |
| P | Veíc. Passeio   | 1 | P |
| V | Van/Furgão      | 4 | V |
| F | Carro Forte     | 5 | F |
| B | Moto com baú    | 6 | B |
| Q | Caminhão tanque | 7 | Q |

## dbo.log_tipo (22)

| id | tipo | subtipo | descricao |
|---|---|---|---|
| 11 | 1 | 1 | Login de usuário |
| 12 | 1 | 2 | Logout de usuário |
| 13 | 1 | 3 | Alteração de configurações |
| 14 | 1 | 4 | Cadastramento de senhas |
| 15 | 1 | 5 | Manutenção aberta |
| 16 | 1 | 6 | Manutenção fechada |
| 17 | 1 | 7 | Modo aferição iniciado |
| 18 | 1 | 8 | Modo aferição finalizado |
| 19 | 1 | 9 | Numeração sequencial reiniciada |
| 21 | 2 | 1 | Com energia |
| 22 | 2 | 2 | Sem energia (bateria) |
| 23 | 2 | 3 | Falha nos sensores |
| 24 | 2 | 4 | Tensão da bateria |
| 25 | 2 | 5 | Temperatura interna |
| 26 | 2 | 6 | Falha na comunicação com a câmera |
| 27 | 2 | 7 | Falha na sequência semafórica |
| 28 | 2 | 8 | Sequência semafórica correta |
| 110 | 1 | 10 | Ajuste de relógio |
| 111 | 1 | 11 | Mudança de horário de verão |
| 112 | 1 | 12 | Conectado ao servidor |
| 113 | 1 | 13 | Desconectado do servidor |
| 114 | 1 | 14 | Cópia de Arquivos |

## dbo.sis_usuario_status (3)

| id_status | id_usuario | status_login | data_atualizacao |
|---|---|---|---|
| 2 | 1162 | A | 09/17/2026 08:01:16 |
| 3 | 1 | A | 09/17/2026 07:32:43 |
| 4 | 1158 | A | 09/17/2026 08:01:39 |

## dbo.status_tempo_real (35)

| serie_equipamento | versao | tempo_executando | status_copia | ultima_deteccao | data_atualizacao |
|---|---|---|---|---|---|
| 3700013 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700019 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 1000100 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 01/06/2026 14:12:23 |
| 3200038 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 08/03/2026 09:36:11 |
| 3700006 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700017 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700023 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700039 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 1000001 | 20260812.01DAT11     | 59157 | opening tcp connection to csx-sede-cwb.no-ip.net port 20021 … | 10/08/2026 15:38:53 | 10/08/2026 15:39:10 |
| 1000201 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 11/02/2025 02:02:39 |
| 5800005 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 11/30/2025 02:03:37 |
| 3700001 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700018 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700037 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700016 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700022 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700021 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700032 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 1000101 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 08/14/2025 02:02:38 |
| 3700014 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700026 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700038 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700011 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700020 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700031 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700035 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 1000202 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 11/02/2025 02:02:39 |
| 3700007 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700025 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700012 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700027 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 3700036 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |
| 5800102 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 10/07/2025 02:02:36 |
| 1000102 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 11/19/2025 02:03:08 |
| 3700002 |                      | 0 |                                                             … | 12/30/1899 00:00:00 | 06/23/2025 16:46:03 |

## dbo.tipo_imagem (14)

| id_tipo_imagem | nome | numero |
|---|---|---|
| 1 | OBJ             | 1 |
| 3 | OBJ             | 2 |
| 4 | OBJ             | 3 |
| 5 | OBJ             | 4 |
| 6 | OBJ             | 5 |
| 7 | OBJ             | 6 |
| 8 | OBJ             | 7 |
| 13 | OBJ             | 8 |
| 14 | OBJ             | 9 |
| 2 | PAN             | 1 |
| 9 | PAN             | 2 |
| 10 | PAN             | 3 |
| 11 | PAN             | 4 |
| 12 | PAN             | 5 |

## dbo.tipo_relatorio_edital_rj (2)

| id_tipo_relatorio_edital_rj | descricao |
|---|---|
| 1 | Fluxo Veicular/Fiscalização Eletrônica |
| 2 | Tempo de Percurso/Fiscalização Eletrônica |

## dbo.tipo_validacao_velocidade_target (4)

| id_validacao_velocidade_target | descricao |
|---|---|
| 0 | Velocidade inválida |
| 1 | Velocidade válida |
| 2 | Timeout no target |
| 3 | Veículo não possui target |

## dbo.tipo_video (2)

| id_tipo_video | nome | numero |
|---|---|---|
| 1 | PAN             | 1 |
| 2 | PAN             | 2 |

## mobilidade.bloqueio_tipo (2)

| id | descricao |
|---|---|
| 1 | TOTAL |
| 2 | PARCIAL |

## mobilidade.radar_tipo_equipamento (10)

| id_tipo_equipamento | descricao |
|---|---|
| 1 | Grupo A1 Tipo I |
| 2 | Grupo A1 Tipo II |
| 3 | Grupo A2 Tipo I |
| 4 | Grupo A2 Tipo II |
| 5 | Grupo B |
| 6 | Grupo C Existentes |
| 7 | Grupo Barreira |
| 8 | Grupo Balanca |
| 9 | Grupo C Semipórticos |
| 10 | Grupo C Pórticos |

## mobilidade.sis_bloqueio_categoria (2)

| id | nome |
|---|---|
| 1 | Programado |
| 2 | Emergencial |

## mobilidade.sis_bloqueio_tipo (6)

| id | categoria_id | nome |
|---|---|---|
| 1 | 1 | Evento |
| 2 | 1 | Obra |
| 3 | 2 | Acidente |
| 4 | 2 | Alagamento |
| 5 | 2 | Árvore Caída |
| 6 | 2 | Veículo Quebrado |

## mobilidade.tipo_regra_trafego_test (1)

| id | descricao |
|---|---|
| 1 | Velocidade máxima |

## muralha.atendimento_guarnicao_situacao (5)

| id | descricao |
|---|---|
| 1 | Aguardando resposta |
| 2 | Em operação |
| 3 | Recusado |
| 4 | Finalizado |
| 5 | Cancelado pela Central |

## muralha.atendimento_guarnicao_tipo_desfecho (8)

| id | descricao |
|---|---|
| 1 | Endereço não encontrado |
| 2 | Orientação às partes envolvidas |
| 3 | Vítima conduzida ao hospital |
| 4 | Suspeito detidoo |
| 5 | Encaminhamento para delegacia |
| 6 | Registro de boletim de ocorrência |
| 7 | Apreensão de objetos ou materiais |
| 8 | Encaminhamento para outros órgãos |

## muralha.atendimento_historico_tipo (8)

| id | descricao |
|---|---|
| 1 | Abertura |
| 2 | Encerramento |
| 3 | Envio de guarnição |
| 4 | Aceite da guarnição |
| 5 | Recusa da guarnição |
| 6 | Documento |
| 7 | Informações da guarnição no local |
| 8 | Endereço |

## muralha.atendimento_situacao (4)

| id | descricao |
|---|---|
| 1 | Em atraso |
| 2 | Em andamento |
| 3 | Guarnição liberada |
| 4 | Encerrado |

## muralha.blitz_abordagem_status (2)

| id | codigo | descricao | ativo | data_criacao |
|---|---|---|---|---|
| 1 | LIBERADO | Abordagem liberada | True | 01/28/2026 17:54:51 |
| 2 | RETIDO | Abordagem realizada | True | 01/28/2026 17:54:51 |

## muralha.blitz_tipo (2)

| id | codigo | descricao | ativo | data_criacao |
|---|---|---|---|---|
| 1 | AUTOMATICA | Blitz Automática | True | 02/03/2026 09:45:31 |
| 2 | MANUAL | Blitz Manual | True | 02/03/2026 09:45:31 |

## muralha.blitz_tipo_alerta (4)

| id | id_blitz_digital | id_tipo_alerta_ocorrencia | data_associacao | ativo |
|---|---|---|---|---|
| 1 | 11 | 19b86a23-2cd6-43ed-a596-62935ea3980a | 02/03/2026 16:48:30 | True |
| 8 | 19 | cf6ebc36-56ca-430b-9d3d-f7d9ff1e82f3 | 02/11/2026 18:04:52 | True |
| 10 | 20 | ae93f81a-df6d-41b5-afc6-99b3438c291d | 02/12/2026 16:42:44 | True |
| 12 | 30 | 19b86a23-2cd6-43ed-a596-62935ea3980a | 03/09/2026 17:59:49 | True |

## muralha.boletim_situacao (4)

| id | descricao |
|---|---|
| 1 | Aberto |
| 2 | Em andamento |
| 3 | Concluido |
| 4 | Cancelado |

## muralha.config_alarme_tipo (12)

| id | tipo | prioridade | habilitado | id_tipo_alerta |
|---|---|---|---|---|
| 1 | MONITORAMENTO SUPERVISIONADO | 1 | False |  |
| 2 | MONITORAMENTO SIMPLES | 2 | True |  |
| 3 | EXATIDÃO PLACA | 3 | True |  |
| 4 | SEMELHANÇA PLACA | 4 | True |  |
| 5 | VEÍCULO ROUBADO | 5 | True | 95631582-96b2-4220-9612-12131be4923c |
| 6 | VEÍCULO FURTADO | 5 | True | 0349f722-dfde-4080-9e3b-d65f1c058edc |
| 7 | VEÍCULO CLONADO | 5 | True | cf6ebc36-56ca-430b-9d3d-f7d9ff1e82f3 |
| 8 | TRANSPORTE CLANDESTINO | 5 | True | ae93f81a-df6d-41b5-afc6-99b3438c291d |
| 9 | VEÍCULO SUSPEITO DE SEQUESTRO RELÂMPAGO | 5 | True | cb8d5c4b-1822-4868-a2f9-0153b50212da |
| 10 | VEÍCULO SUSPEITO DE ROUBO A BANCO | 5 | True | feef9500-83c0-4942-a8aa-aed77e20ba5b |
| 11 | VEICULO MONITORADO | 5 | True | 9d31a265-a663-4836-bf00-2309fc0d5e33 |
| 12 | VEÍCULO COM ATRASO DE LICENCIAMENTO | 5 | True | 6631dc43-779f-4bff-a329-b9653d056708 |

## muralha.fato_situacao (2)

| id | descricao |
|---|---|
| 1 | Ativo |
| 2 | Encerrado |

## muralha.motivo_descarte (2)

| id | descricao |
|---|---|
| 7fabc2e1-87b4-4dd3-b9ad-40c8af249b8e | PLACA INCORRETA |
| 67240968-0d40-4137-9c9d-4573a0652971 | ALERTA INVÁLIDO |

## muralha.motivo_invalido_correlacionamento_automatico_placa (4)

| id | descricao |
|---|---|
| 1 | Leitura de OCR inválida |
| 2 | Correlacionamento inválido |
| 3 | Imagem borrada/pixelada |
| 4 | Leitura de OCR inválida |

## muralha.ponto_interesse_tipos (4)

| id | descricao |
|---|---|
| 1 | BANCO |
| 2 | ORGÃO GOVERNAMENTAL |
| 3 | ESTABELECIMENTO |
| 4 | OUTROS |

## muralha.registro_fato_individuo_tipo (15)

| id | descricao |
|---|---|
| 2 | Vítima |
| 3 | Autor do Fato |
| 4 | Testemunha |
| 5 | Comunicante |
| 6 | Averiguado |
| 7 | Condutor |
| 8 | Proprietário |
| 9 | Responsável Legal |
| 10 | Acompanhante |
| 11 | Interessado |
| 12 | Vizinho |
| 13 | Familiar |
| 14 | Amigo |
| 15 | Conhecido |
| 16 | Solicitante |

## muralha.registro_fato_natureza (25)

| id | id_registro_tipo | natureza_desc | requer_bo |
|---|---|---|---|
| 1 | 1 | Furto simples | 1 |
| 2 | 2 | Roubo à mão armada | 1 |
| 3 | 3 | Extravio de documento | 0 |
| 4 | 4 | Dano simples | 1 |
| 5 | 5 | Fraude eletrônica | 1 |
| 6 | 6 | Ameaça verbal | 1 |
| 7 | 7 | Lesão corporal leve | 1 |
| 8 | 8 | Pessoa desaparecida | 1 |
| 9 | 9 | Localização de desaparecido | 0 |
| 10 | 10 | Lesão corporal doméstica | 1 |
| 11 | 11 | Medida judicial descumprida | 1 |
| 12 | 12 | Deixar de prover cuidados | 1 |
| 13 | 13 | Com vítima | 1 |
| 14 | 13 | Sem vítima | 0 |
| 15 | 14 | Grafite em bem público | 1 |
| 16 | 15 | Monitoramento vinculado a denúncia | 1 |
| 17 | 16 | Identificação de veículo roubado | 1 |
| 18 | 17 | Veículo de interesse investigativo | 0 |
| 19 | 18 | Grupo de veículos em suspeita | 0 |
| 20 | 19 | Transporte irregular de passageiros | 1 |
| 21 | 20 | Veículo monitorado em roubo planejado | 1 |
| 22 | 21 | Identificação via sistema | 0 |
| 23 | 22 | Monitoramento de veículo furtado | 1 |
| 24 | 23 | Placa clonada identificada | 1 |
| 25 | 24 | Análise de correlação | 1 |

## muralha.registro_fato_natureza_delituosa (17)

| id | id_registro_natureza | natureza_delituosa_desc | codigo_penal_lei |
|---|---|---|---|
| 1 | 1 | Furto | Art. 155 - CP |
| 2 | 2 | Roubo | Art. 157 - CP |
| 3 | 4 | Dano | Art. 163 - CP |
| 4 | 5 | Estelionato | Art. 171 - CP |
| 5 | 6 | Ameaça | Art. 147 - CP |
| 6 | 7 | Lesão corporal | Art. 129 - CP |
| 7 | 10 | Lesão corporal | Art. 129, §9º - CP |
| 8 | 11 | Descumprimento de decisão | Art. 330 - CP |
| 9 | 12 | Abandono de incapaz | Art. 133 - CP |
| 10 | 15 | Pichação | Art. 65 - Lei 9.605/1998 |
| 11 | 16 | Sequestro Relâmpago | Art. 158 - CP |
| 12 | 17 | Roubo de veículo | Art. 157 - CP |
| 13 | 20 | Exercício ilegal de atividade | Art. 47 - LCP |
| 14 | 21 | Roubo qualificado | Art. 157, §3º - CP |
| 15 | 22 | Infração administrativa | CTB - Art. 230, V |
| 16 | 23 | Furto | Art. 155 - CP |
| 17 | 24 | Adulteração de sinal | Art. 311 - CP |

## muralha.registro_fato_status (2)

| id | descricao |
|---|---|
| 1 | Ativo |
| 2 | Encerrado |

## muralha.registro_fato_tipo (24)

| id | tipo_desc |
|---|---|
| 1 | Furto |
| 2 | Roubo |
| 3 | Perda ou Extravio |
| 4 | Dano |
| 5 | Estelionato |
| 6 | Ameaça |
| 7 | Lesão Corporal |
| 8 | Desaparecimento de Pessoa |
| 9 | Localização de Pessoa Desaparecida |
| 10 | Violência Doméstica |
| 11 | Descumprimento de Medida Protetiva |
| 12 | Abandono de Incapaz |
| 13 | Acidente de Trânsito |
| 14 | Pichação |
| 15 | Veículo Suspeito de Sequestro Relâmpago |
| 16 | Veículo Roubado |
| 17 | Veículo Monitorado |
| 18 | Comboio de Veículos |
| 19 | Transporte Clandestino |
| 20 | Veículo Suspeito de Roubo à Banco |
| 21 | Veículo com Atraso de Licenciamento |
| 22 | Veículo Furtado |
| 23 | Veículo Clonado |
| 24 | Suspeita de Atividade Criminosa |

## muralha.status_alerta (4)

| id | descricao | descricao_detalhada |
|---|---|---|
| 15ebba5f-c805-449e-83cc-227ed3b3ad3c | OCORRÊNCIA |  |
| 5479c6d9-7381-4492-99be-442ef2e741b0 | PENDENTE |  |
| 298f5a62-c799-4ca9-8220-6b08f8664534 | DESCARTADO |  |
| ca5e4ae0-501e-48e2-9652-a1cf7aa340bb | VINCULADO | Este stauts significa que houve a geração de ocorrência a pa… |

## muralha.status_correlacionamento_automatico (3)

| id | descricao |
|---|---|
| 1 | pendente |
| 2 | vinculado |
| 3 | inibido |

## muralha.status_notificacao (3)

| id | descricao |
|---|---|
| 65453ca3-6e31-4740-b082-531306ad7016 | NÃO ENVIADO |
| ae081e1e-a323-41a7-8428-55a0b398e545 | ENVIADO |
| 99af55c6-2446-4b98-bbcd-83663c504c79 | PENDENTE |

## muralha.status_ocorrencia (6)

| id | descricao |
|---|---|
| acaade8a-2d4e-4e93-9f1f-0de9185b576e | FINALIZADO |
| 3c0612d6-3950-4861-8ca4-2a261ae787af | EM ABERTO |
| 9ce69c57-59d6-4611-ac1e-3690779fa68f | FINALIZADO - SEM SOLUÇÃO |
| 0033d5bc-8e4f-4b10-bc5e-8119b768f566 | FINALIZADO - VEÍCULO RECUPERADO |
| 81038ad9-2206-4956-a34e-8c844c673a62 | FINALIZADO - APREENDIDO |
| 6077fc54-fd5b-42c0-9ee1-8fb3612e48bd | NOTIFICAÇÃO ENVIADA |

## muralha.tipo_alerta_ocorrencia (10)

| id | tipo | descricao | descricao_sms | permite_monitorado_sem_placa | tarefa_ativa | data_alteracao | id_usuario | nomeinterno | prioridade |
|---|---|---|---|---|---|---|---|---|---|
| cb8d5c4b-1822-4868-a2f9-0153b50212da | Veículo Suspeito de Sequestro Relâmpago |  | Veíc. Seq.Relâmpago | True | True | 01/12/2022 17:54:35 | 2 | SEQUESTRO            | 14 |
| 95631582-96b2-4220-9612-12131be4923c | Veículo Roubado |  | Veículo Roubado | False | True | 01/12/2022 17:54:35 | 2 | ROUBADO              | 12 |
| 9d31a265-a663-4836-bf00-2309fc0d5e33 | Veiculo Monitorado |  | Veículo Monitorado | False | True | 04/04/2022 09:00:20 | 2 | MONITORADO           | 1 |
| 0ab34bea-2f45-4d83-b7cb-53a3440da8a6 | Outros |  | Outros | False | True | 10/01/2026 11:11:42 | 2 | OUTROS               | 9 |
| 19b86a23-2cd6-43ed-a596-62935ea3980a | Comboio de Veículos |  | Comboio de Veículos | True | True | 01/14/2022 11:57:05 | 2 | COMBOIO              | 99 |
| ae93f81a-df6d-41b5-afc6-99b3438c291d | Transporte Clandestino |  | Transp. Clandestino | True | True | 01/12/2022 17:54:35 | 2 | CLANDESTINO          | 3 |
| feef9500-83c0-4942-a8aa-aed77e20ba5b | Veículo Suspeito de Roubo à Banco |  | Veíc. Roubo Banco | True | True | 01/12/2022 17:54:35 | 2 | SRBANCO              | 5 |
| 6631dc43-779f-4bff-a329-b9653d056708 | Veículo com Atraso de Licenciamento |  | Atraso Licenciamento | False | True | 08/22/2023 19:42:15 | 2 | LICENCIAMENTO        | 6 |
| 0349f722-dfde-4080-9e3b-d65f1c058edc | Veículo Furtado |  | Veículo Furtado | False | True | 01/12/2022 17:54:35 | 2 | FURTADO              | 7 |
| cf6ebc36-56ca-430b-9d3d-f7d9ff1e82f3 | Veículo Clonado |  | Veículo Clonado | True | True | 01/12/2022 17:54:35 | 2 | CLONADO              | 8 |

## muralha.tipo_notificacao (3)

| id | descricao |
|---|---|
| 2c434cfd-f581-4fa0-b3e3-45a7367bf05e | SMS |
| 3a3f1f17-6ec3-4fca-9195-5b160a091779 | EMAIL |
| 778f443e-9514-44e0-bcd1-f953d91042bf | POPUP |

## muralha.tipo_ocorrencia_status (32)

| id | id_tipo_ocorrencia | id_status_ocorrencia |
|---|---|---|
| 17d7d4c2-0262-4ee9-b974-02adaaf2ee79 | feef9500-83c0-4942-a8aa-aed77e20ba5b | 9ce69c57-59d6-4611-ac1e-3690779fa68f |
| 5227ffc1-0c4e-4dcd-9a65-078460b52be3 | 95631582-96b2-4220-9612-12131be4923c | 9ce69c57-59d6-4611-ac1e-3690779fa68f |
| 1787a96a-9130-4daf-ac61-0dc1df5f2bad | cb8d5c4b-1822-4868-a2f9-0153b50212da | acaade8a-2d4e-4e93-9f1f-0de9185b576e |
| f830b073-d609-40ff-ab71-0dedd73fdaae | ae93f81a-df6d-41b5-afc6-99b3438c291d | 3c0612d6-3950-4861-8ca4-2a261ae787af |
| 7039a1f9-43d3-4921-801a-10f6260a3c42 | cf6ebc36-56ca-430b-9d3d-f7d9ff1e82f3 | 6077fc54-fd5b-42c0-9ee1-8fb3612e48bd |
| ad5bd0be-b557-4068-bc2b-1bbf21c133bf | ae93f81a-df6d-41b5-afc6-99b3438c291d | acaade8a-2d4e-4e93-9f1f-0de9185b576e |
| 25d906c4-c1d0-4382-8408-2483b965e8fe | cf6ebc36-56ca-430b-9d3d-f7d9ff1e82f3 | 81038ad9-2206-4956-a34e-8c844c673a62 |
| 1c9504b6-8a80-42bd-989d-29d511bfd4b3 | 95631582-96b2-4220-9612-12131be4923c | 6077fc54-fd5b-42c0-9ee1-8fb3612e48bd |
| 23595e24-3318-4b7f-ae0e-2e135be50d4a | 0349f722-dfde-4080-9e3b-d65f1c058edc | 9ce69c57-59d6-4611-ac1e-3690779fa68f |
| 4bddfb1e-7a0a-46ec-ab60-3ce41fb74090 | 0349f722-dfde-4080-9e3b-d65f1c058edc | acaade8a-2d4e-4e93-9f1f-0de9185b576e |
| 12a728de-fc29-4ecd-afbd-4092dae8260d | cf6ebc36-56ca-430b-9d3d-f7d9ff1e82f3 | 3c0612d6-3950-4861-8ca4-2a261ae787af |
| ed18100c-4e48-4482-912d-5bcb09998a63 | feef9500-83c0-4942-a8aa-aed77e20ba5b | acaade8a-2d4e-4e93-9f1f-0de9185b576e |
| 60024fcd-7231-4b31-9372-5ca306389779 | cb8d5c4b-1822-4868-a2f9-0153b50212da | 3c0612d6-3950-4861-8ca4-2a261ae787af |
| 930ed772-a16b-49b9-afc6-5db839b83be4 | 0349f722-dfde-4080-9e3b-d65f1c058edc | 0033d5bc-8e4f-4b10-bc5e-8119b768f566 |
| 6c16a73c-d69c-4560-bede-6060c77fd244 | feef9500-83c0-4942-a8aa-aed77e20ba5b | 6077fc54-fd5b-42c0-9ee1-8fb3612e48bd |
| 092bcf27-2184-48b9-a001-61f9788092a2 | 9d31a265-a663-4836-bf00-2309fc0d5e33 | 3c0612d6-3950-4861-8ca4-2a261ae787af |
| 352dfe8f-8444-4cad-b29c-6b99336c4899 | feef9500-83c0-4942-a8aa-aed77e20ba5b | 3c0612d6-3950-4861-8ca4-2a261ae787af |
| 2a3f183f-eb40-46c6-a94e-772b831b50d4 | cb8d5c4b-1822-4868-a2f9-0153b50212da | 9ce69c57-59d6-4611-ac1e-3690779fa68f |
| 583ff3de-bf92-4433-91f0-78358037bcfc | cf6ebc36-56ca-430b-9d3d-f7d9ff1e82f3 | acaade8a-2d4e-4e93-9f1f-0de9185b576e |
| 1cf08d24-2101-445e-a9bf-7dd3d849db29 | 9d31a265-a663-4836-bf00-2309fc0d5e33 | acaade8a-2d4e-4e93-9f1f-0de9185b576e |
| 43af8eb8-25ca-43a0-a3d0-7f74bc1f0edc | ae93f81a-df6d-41b5-afc6-99b3438c291d | 9ce69c57-59d6-4611-ac1e-3690779fa68f |
| 570a3804-ed71-4cef-b4fe-8867c5874cd2 | 95631582-96b2-4220-9612-12131be4923c | 0033d5bc-8e4f-4b10-bc5e-8119b768f566 |
| bfa4f1d1-ffd7-4c4d-b934-96d3456fbc7e | cf6ebc36-56ca-430b-9d3d-f7d9ff1e82f3 | 9ce69c57-59d6-4611-ac1e-3690779fa68f |
| 6728ae66-bedf-4b1a-9d01-a3ec41ed3e55 | cb8d5c4b-1822-4868-a2f9-0153b50212da | 6077fc54-fd5b-42c0-9ee1-8fb3612e48bd |
| 2dda8c1b-6f4f-4c23-94a0-abd29c690170 | 95631582-96b2-4220-9612-12131be4923c | 3c0612d6-3950-4861-8ca4-2a261ae787af |
| d22790d3-a69b-4545-9f32-b4bde5d658b5 | 95631582-96b2-4220-9612-12131be4923c | acaade8a-2d4e-4e93-9f1f-0de9185b576e |
| eb3f2ea5-e9ee-4468-b9a7-b732465e5b8d | 9d31a265-a663-4836-bf00-2309fc0d5e33 | 6077fc54-fd5b-42c0-9ee1-8fb3612e48bd |
| ae43b98b-61b6-4d7d-a129-cd17bc0e2c4c | 9d31a265-a663-4836-bf00-2309fc0d5e33 | 9ce69c57-59d6-4611-ac1e-3690779fa68f |
| fbf6f458-30de-4243-8e53-cff392c72db0 | ae93f81a-df6d-41b5-afc6-99b3438c291d | 6077fc54-fd5b-42c0-9ee1-8fb3612e48bd |
| a6d75886-28e2-4051-a771-ddcb076276e4 | 0349f722-dfde-4080-9e3b-d65f1c058edc | 3c0612d6-3950-4861-8ca4-2a261ae787af |
| f9c55a35-5884-4392-8136-e474033160d3 | ae93f81a-df6d-41b5-afc6-99b3438c291d | 81038ad9-2206-4956-a34e-8c844c673a62 |
| 47eedf37-0739-4868-b16c-fe115ab8a0d1 | 0349f722-dfde-4080-9e3b-d65f1c058edc | 6077fc54-fd5b-42c0-9ee1-8fb3612e48bd |

## muralha.tipo_registro (2)

| id | descricao |
|---|---|
| 5511cef5-c1a0-450b-99b3-6fca8668d243 | OCORRÊNCIA |
| e7d115b9-e6b3-4e86-9083-f347a1917045 | ALERTA |

