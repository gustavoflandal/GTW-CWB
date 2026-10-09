# TR Salvador - Sistema de Gestão de Processamento de Dados de Fiscalização Eletrônica

> Extraído do Termo de Referência TRANSALVADOR — Capítulo 5

---

## 5. Sistema de Gestão de Processamento de Dados de Fiscalização Eletrônica

O presente capítulo especifica os requisitos funcionais, técnicos e operacionais do Sistema de Gestão de Processamento de Dados de Fiscalização Eletrônica de Salvador, composto por funcionalidades integradas de Gestão e Classificação de Infrações, Alertas de Irregularidades e Inteligência de Dados, além dos requisitos aplicáveis à Central de Processamento e à Sala de Operações.

A solução tecnológica deverá representar evolução operacional, funcional e tecnológica substancial em relação aos modelos anteriormente utilizados pela CONTRATANTE, incorporando mecanismos avançados de automação, inteligência analítica, processamento em tempo real e suporte à tomada de decisão operacional.

Deverão compor obrigatoriamente a solução, entre outros recursos compatíveis com o estado da técnica:
- a) inteligência artificial embarcada nos equipamentos de campo, destinada à detecção automatizada de eventos, irregularidades e padrões operacionais;
- b) dashboards analíticos avançados, com correlação multivariada de dados operacionais, estatísticos e georreferenciados;
- c) integração em tempo real com sistemas corporativos e aplicações utilizadas pela CONTRATANTE, inclusive o sistema de estacionamento rotativo digital e demais sistemas institucionais correlatos;
- d) capacidade de resposta operacional em até **4 (quatro) segundos** para eventos críticos e operações de Blitz Eletrônica.

A presente contratação não compreende a implantação, fornecimento ou operação de central de videomonitoramento urbano, nem a disponibilização de infraestrutura física destinada ao Núcleo de Operações Assistidas – NOA.

### 5.1. Princípios Gerais de Controle de Acesso

Todos os módulos, aplicações, serviços, interfaces e componentes integrantes da solução deverão implementar mecanismos de controle de acesso compatíveis com ambientes críticos de processamento de dados e segurança da informação, observando obrigatoriamente os princípios da segregação de funções, do menor privilégio, da rastreabilidade integral das ações e da proteção dos dados tratados.

5.1.1. Segregação de funções, impedindo que um mesmo usuário acumule permissões incompatíveis com o princípio do duplo controle;

5.1.2. Menor privilégio (least privilege), assegurando que cada usuário acesse exclusivamente os recursos necessários ao desempenho de suas atribuições;

5.1.3. Rastreabilidade integral das ações executadas no sistema, mediante registros auditáveis e invioláveis;

5.1.4. Garantia da confidencialidade, integridade, disponibilidade e autenticidade das informações tratadas, em conformidade com a Lei no 13.709/2018 – LGPD.

O acesso às funcionalidades do Sistema deverá ser restrito a usuários previamente cadastrados e autorizados pela CONTRATANTE, sendo vedada a utilização de credenciais compartilhadas, genéricas ou sem vinculação individual ao agente responsável pela operação.

#### Autenticação

5.1.5. O sistema deverá implementar autenticação individualizada, exigindo, no mínimo:
- a) identificação única vinculada ao CPF, matrícula funcional ou identificador institucional definido pela CONTRATANTE;
- b) utilização obrigatória de senha pessoal, sigilosa e intransferível;
- c) política parametrizável de segurança de senhas, contemplando complexidade mínima, prazo de validade, histórico de reutilização e bloqueio automático após tentativas inválidas;
- d) encerramento automático de sessão após período configurável de inatividade;
- e) suporte nativo à autenticação multifator (MFA), sem necessidade de alteração estrutural da solução.

#### Perfis de Acesso

5.1.6. O sistema deverá permitir a criação, edição, parametrização e desativação de perfis de acesso, com permissões diferenciadas conforme a função desempenhada pelo usuário.

5.1.7. Todas as ações executadas no sistema deverão gerar registro automático, inviolável, rastreável e auditável em log operacional, sendo vedada a exclusão manual de registros por usuários comuns ou operadores da CONTRATADA.

5.1.8. Os registros de auditoria deverão permanecer íntegros, acessíveis e disponíveis para consulta pela CONTRATANTE durante toda a vigência contratual e pelo período adicional definido em política institucional de retenção de dados.

5.1.9. O log de auditoria deverá conter, no mínimo:
- a) identificação do usuário;
- b) data e horário da operação, com precisão mínima de segundos;
- c) endereço IP do terminal utilizado;
- d) funcionalidade acessada;
- e) operação executada;
- f) identificador do registro relacionado à ação.

5.1.10. Deverá ser possível realizar consultas eletrônicas nos logs mediante filtros por usuário, perfil, período, equipamento, tipo de ação ou qualquer combinação desses parâmetros, com exportação em PDF, CSV e XLS.

### 5.2. Funcionalidade de Gestão e Classificação de Infrações

A Funcionalidade de Gestão e Classificação de Infrações constitui o componente responsável pelo recebimento, armazenamento, processamento, análise, pré-classificação automatizada, validação operacional, auditoria e consolidação dos registros de infrações oriundos dos Equipamentos de Fiscalização Eletrônica.

Trata-se de solução baseada em tecnologia web, acessível por navegador, destinada a subsidiar integralmente o fluxo administrativo de tratamento das infrações de trânsito, desde a recepção inicial dos registros até sua disponibilização para integração com os sistemas oficiais da CONTRATANTE.

#### 5.2.1. Recepção, Validação e Organização dos Registros

5.2.1.1. O sistema deverá receber automaticamente os registros oriundos dos equipamentos de fiscalização, contendo imagens, vídeos, metadados e informações de telemetria associadas ao evento fiscalizado.

5.2.1.2. A recepção dos registros deverá contemplar verificação automática de integridade dos arquivos mediante utilização de função hash **SHA-256** ou superior, assegurando a detecção de corrupção, adulteração ou inconsistência dos dados transmitidos.

5.2.1.3. O sistema deverá registrar automaticamente:
- a) identificação do equipamento de origem;
- b) data e horário da captura;
- c) data e horário da recepção;
- d) quantidade de registros recebidos;
- e) quantidade de registros íntegros;
- f) quantidade de registros rejeitados e respectivos motivos.

5.2.1.4. Os registros deverão ser indexados e organizados por tipo de infração, equipamento de origem, localização, período, situação de processamento e identificação do lote.

5.2.1.5. O sistema deverá suportar recepção simultânea de registros provenientes de múltiplos equipamentos, observando capacidade compatível com o parque tecnológico previsto neste Termo de Referência, acrescido de margem mínima de expansão de 30%.

5.2.1.6. Registros recebidos fora do prazo máximo de transmissão deverão ser automaticamente sinalizados em sistema, com geração de alerta operacional e correspondente registro em log.

#### 5.2.2. Pré-classificação Automatizada de Imagens

5.2.2.1. O sistema deverá executar processamento automatizado de pré-classificação imediatamente após a recepção do registro, com análise em tempo real e conclusão do processamento inicial em até **4 (quatro) segundos** contados do evento fiscalizado. A pré-classificação e a dupla validação destinam-se à mitigação dos riscos R04 e R22 do Anexo D.

5.2.2.2. A pré-classificação deverá operar continuamente, sem necessidade de intervenção humana para início do fluxo de análise.

5.2.2.3. O mecanismo de pré-classificação automatizada deverá manter índice mínimo mensal de aderência operacional de **80%**, aferido mediante comparação entre o resultado automatizado e a validação final realizada pelos operadores e auditores da CONTRATANTE.

5.2.2.4. O sistema deverá executar leitura automática de placas (OCR/LAP) em todos os registros recepcionados, realizando conferência cruzada entre leituras de campo e leituras sistêmicas.

5.2.2.5. Divergências identificadas entre leituras automáticas deverão ser encaminhadas automaticamente para revisão operacional.

5.2.2.6. O sistema deverá disponibilizar **painel de controle operacional** contendo indicadores em tempo real relativos à taxa de assertividade, volume de registros processados, divergências identificadas e registros encaminhados para revisão manual.

#### 5.2.3. Classificação Técnica pelos Operadores

5.2.3.1. Após a pré-classificação automatizada, os registros deverão ser submetidos à análise técnica por operadores humanos.

5.2.3.2. O sistema deverá impedir tecnicamente que um mesmo registro seja validado por apenas um operador, assegurando obrigatoriamente **dupla análise independente**, sem compartilhamento prévio das classificações realizadas.

5.2.3.3. O sistema também deverá impedir que o mesmo operador realize múltiplas análises do mesmo registro em qualquer etapa do fluxo operacional.

5.2.3.4. Havendo divergência entre classificações atribuídas ao mesmo registro, o sistema deverá encaminhar automaticamente o caso para operador desempatador, sem acesso às análises anteriores.

5.2.3.5. Todos os atos de classificação deverão gerar registro automático em log operacional contendo identificação do operador, data, horário, classificação atribuída, justificativa e observações complementares.

#### 5.2.4. Interface de Análise de Imagens

5.2.4.1. A solução deverá disponibilizar interface gráfica de alta resolução destinada à análise individualizada dos registros.

5.2.4.2. A interface deverá permitir ampliação de imagem, ajuste de brilho, contraste e demais mecanismos de apoio à análise visual, sem alteração do arquivo original.

5.2.4.3. A solução deverá possuir funcionalidade de **obliteração automática e manual de imagens**, destinada à proteção da identidade visual de ocupantes e terceiros não relacionados à infração, em conformidade com a LGPD.

5.2.4.4. A reversão da obliteração somente poderá ser realizada por usuários autorizados mediante justificativa obrigatória e correspondente registro em log auditável.

5.2.4.5. O campo de leitura de placa deverá ser editável pelo operador, permanecendo todas as alterações registradas em log.

#### 5.2.5. Gestão de Lotes e Fluxo de Validação

5.2.5.1. O sistema deverá organizar os registros em lotes conforme parâmetros definidos pela CONTRATANTE.

5.2.5.2. O sistema deverá realizar seleção automática de amostras para auditoria da CONTRATANTE, observando critérios estatísticos parametrizáveis.

5.2.5.3. A CONTRATANTE poderá determinar auditoria integral de qualquer lote, independentemente do tamanho da amostra originalmente selecionada.

5.2.5.4. O sistema deverá impedir, por restrição lógica, operacional e sistêmica, a transmissão, exportação, integração ou conversão de registros não validados pela CONTRATANTE em notificações de autuação, penalidade ou qualquer outro ato administrativo sancionatório.

5.2.5.5. Nenhum registro poderá prosseguir para etapas posteriores sem validação formal da CONTRATANTE.

5.2.5.6. Todos os registros processados deverão permanecer disponíveis para consulta e auditoria retroativa durante toda a vigência contratual.

#### 5.2.6. Integração com Sistemas da CONTRATANTE

5.2.6.1. O sistema deverá possuir interoperabilidade com os sistemas corporativos utilizados pela CONTRATANTE.

5.2.6.2. As integrações deverão ocorrer mediante WebService seguro, utilizando protocolo HTTPS, autenticação segura e transmissão criptografada dos dados.

5.2.6.3. Todos os eventos de integração deverão ser registrados em log operacional.

5.2.6.4. Eventual integração com ambientes vinculados ao NOA possuirá caráter exclusivamente operacional e interoperável, sem transferência de gestão, processamento ou responsabilidade sobre infraestrutura de videomonitoramento urbano.

#### 5.2.7. Telemetria Operacional

5.2.7.1. A funcionalidade de telemetria deverá permitir monitoramento contínuo, centralizado e em tempo real do estado operacional dos equipamentos integrantes da solução.

5.2.7.2. O sistema deverá identificar automaticamente falhas de comunicação, indisponibilidade operacional, falhas de captura, interrupções elétricas e inconsistências técnicas dos equipamentos.

5.2.7.3. Todos os eventos operacionais deverão ser registrados em histórico auditável.

#### 5.2.8. Relatórios Gerenciais e Operacionais

5.2.8.1. O sistema deverá gerar relatórios automáticos e parametrizáveis contendo indicadores operacionais, estatísticos, técnicos e gerenciais.

5.2.8.2. Os relatórios deverão permitir cruzamento simultâneo de filtros temporais, geográficos, operacionais, estatísticos e funcionais.

5.2.8.3. Todos os relatórios deverão possuir visualização em tela e exportação em PDF, CSV e XLS.

5.2.8.4. A solução deverá disponibilizar dashboards analíticos em tempo real para acompanhamento operacional, estatístico e gerencial da execução contratual.

### 5.3. Funcionalidade de Alertas de Irregularidades

A Funcionalidade de Alertas de Irregularidades constitui o componente sistêmico responsável pelo recebimento, tratamento, priorização, gerenciamento operacional e encaminhamento dos alertas gerados pelos Equipamentos de Fiscalização Eletrônica.

O componente possuirá natureza operacional e complementar às atividades de fiscalização eletrônica de trânsito, destinando-se ao apoio às ações de monitoramento viário, fiscalização, gestão operacional do tráfego e resposta a eventos relevantes relacionados à mobilidade urbana.

#### 5.3.1. Recebimento e Tratamento Operacional dos Alertas

5.3.1.1. O sistema deverá receber automaticamente os alertas operacionais gerados pelos equipamentos de fiscalização e pelas funcionalidades embarcadas previstas neste Termo de Referência.

5.3.1.2. Os alertas deverão ser classificados, organizados e priorizados automaticamente conforme parâmetros definidos pela CONTRATANTE, considerando, no mínimo:
- a) tipo de evento;
- b) criticidade operacional;
- c) localização;
- d) horário;
- e) impacto potencial na fluidez ou segurança viária;
- f) regras operacionais parametrizadas pela CONTRATANTE.

5.3.1.3. O sistema deverá permitir parametrização individualizada dos tratamentos aplicáveis a cada categoria de alerta, incluindo:
- a) encaminhamento para fiscalização em campo;
- b) encaminhamento para análise operacional;
- c) registro estatístico;
- d) integração com sistemas corporativos;
- e) geração de ocorrência operacional;
- f) priorização para auditoria;
- g) demais tratamentos definidos pela CONTRATANTE.

5.3.1.4. O sistema deverá manter rastreabilidade integral do fluxo operacional dos alertas, incluindo:
- a) geração;
- b) recebimento;
- c) classificação;
- d) encaminhamento;
- e) tratamento realizado;
- f) encerramento;
- g) identificação do usuário responsável pelas intervenções executadas.

#### 5.3.2. Tipos de Alertas Operacionais

5.3.2.1. A funcionalidade deverá suportar tratamento operacional dos alertas gerados pelas funcionalidades embarcadas dos equipamentos, incluindo, no mínimo:
- a) irregularidades relacionadas à circulação ou estacionamento;
- b) circulação em desacordo com regras de uso da via;
- c) veículos com restrições operacionais ou administrativas;
- d) congestionamentos ou retenções anormais;
- e) falhas operacionais relevantes dos equipamentos;
- f) bloqueios ou interferências na circulação viária;
- g) eventos operacionais relacionados à mobilidade urbana;
- h) demais eventos parametrizados pela CONTRATANTE.

5.3.2.2. A inclusão de novos tipos de alertas poderá ser solicitada pela CONTRATANTE durante toda a vigência contratual, observada viabilidade técnica da solução.

#### 5.3.3. Interface Operacional e Visualização

5.3.3.1. O sistema deverá disponibilizar interface operacional web para acompanhamento dos alertas ativos, tratados e pendentes.

5.3.3.2. A interface deverá permitir:
- a) visualização georreferenciada dos alertas;
- b) filtros por período, tipo, equipamento, localidade e status;
- c) ordenação por criticidade;
- d) acompanhamento cronológico dos eventos;
- e) pesquisa por placa veicular, inclusive parcial;
- f) acesso aos registros e imagens vinculados ao evento.

5.3.3.3. O sistema deverá permitir configuração individualizada de perfis de exibição, prioridades operacionais, filtros automáticos e regras de notificação.

5.3.3.4. Os alertas classificados como críticos deverão possuir destaque visual diferenciado e sinalização configurável pela CONTRATANTE.

#### 5.3.4. Georreferenciamento e Áreas de Interesse

5.3.4.1. Os alertas deverão ser apresentados em mapa digital interativo, permitindo identificação espacial dos eventos e acompanhamento operacional das ocorrências registradas.

5.3.4.2. O sistema deverá permitir:
- a) visualização de alertas por região;
- b) filtros espaciais;
- c) agrupamento de ocorrências;
- d) identificação de concentração de eventos;
- e) definição de áreas de interesse operacional.

5.3.4.3. A CONTRATANTE poderá definir regras específicas para geração, priorização ou encaminhamento de alertas dentro de áreas previamente delimitadas.

#### 5.3.5. Encaminhamento e Integração Operacional

5.3.5.1. O sistema deverá permitir encaminhamento automatizado ou manual dos alertas para:
- a) equipes operacionais;
- b) sistemas corporativos da CONTRATANTE;
- c) plataformas integradas;
- d) dispositivos móveis utilizados em campo;
- e) demais ambientes operacionais autorizados pela CONTRATANTE.

5.3.5.2. Eventuais integrações com ambientes vinculados ao NOA possuirão natureza exclusivamente interoperável e acessória.

5.3.5.3. O sistema deverá possuir mecanismos automáticos de reenvio em caso de falha temporária de comunicação, preservando a integridade e rastreabilidade dos alertas processados.

#### 5.3.6. Histórico e Auditoria dos Alertas

5.3.6.1. O sistema deverá manter histórico integral dos alertas gerados, tratados, encaminhados ou encerrados durante toda a vigência contratual.

5.3.6.2. O histórico deverá permitir consultas mediante filtros por:
- a) período;
- b) equipamento;
- c) localidade;
- d) categoria do alerta;
- e) status operacional;
- f) placa veicular;
- g) usuário responsável;
- h) resultado do tratamento realizado.

5.3.6.3. Todas as ações executadas na funcionalidade deverão observar os mecanismos de rastreabilidade, auditoria e geração de logs previstos neste Termo de Referência.

5.3.6.4. O sistema deverá permitir exportação dos registros operacionais em formatos PDF, CSV e XLS.

#### 5.3.7. Integração com Sistemas, Sensores e Recursos Complementares

5.3.7.1. A solução deverá permitir integração interoperável com sensores, dispositivos e sistemas externos relacionados à operação da mobilidade urbana e à fiscalização eletrônica, observada compatibilidade técnica e análise dos protocolos de comunicação aplicáveis.

5.3.7.2. A integração poderá compreender, entre outros:
- a) sensores meteorológicos;
- b) sensores de nível de água e monitoramento de alagamentos;
- c) painéis de mensagens variáveis (PMV);
- d) sensores operacionais de tráfego;
- e) câmeras e dispositivos auxiliares vinculados às funcionalidades da solução;
- f) demais dispositivos definidos pela CONTRATANTE.

5.3.7.3. A solução deverá permitir integração complementar com plataformas de navegação e monitoramento de tráfego, tais como Google Maps, Waze ou equivalentes, sem substituição das informações geradas pelos equipamentos da CONTRATANTE.

5.3.7.4. A funcionalidade deverá suportar recursos de análise volumétrica de tráfego baseados em videodetecção e laço virtual, permitindo:
- a) contagem veicular;
- b) análise de fluxo por faixa;
- c) cálculo de ocupação da via;
- d) identificação de retenções ou congestionamentos;
- e) geração de alertas operacionais parametrizáveis.

5.3.7.5. O acesso à plataforma deverá ocorrer por meio de navegador web, mediante autenticação individualizada, controle de acesso por perfis e utilização de mecanismos de criptografia.

5.3.7.6. A parametrização dos tipos de alertas, níveis de prioridade, critérios operacionais e limites de tolerância deverá ocorrer por meio de interface gráfica acessível e parametrizável, sem necessidade de alteração do código-fonte da solução.

5.3.7.7. O sistema deverá permitir utilização de mapas, gráficos, indicadores visuais e elementos de apoio operacional para acompanhamento dos eventos e alertas tratados pela plataforma.

5.3.7.8. A solução deverá possibilitar priorização automática de eventos classificados como críticos, com destaque operacional parametrizável pela CONTRATANTE.

### 5.4. Funcionalidade de Inteligência de Dados e Apoio à Decisão Operacional

A Funcionalidade de Inteligência de Dados e Apoio à Decisão Operacional constitui o componente do Sistema destinado à consolidação, organização, correlação, análise e visualização estratégica dos dados provenientes dos equipamentos de fiscalização eletrônica, das funcionalidades sistêmicas previstas neste Termo de Referência e dos sistemas integrados autorizados pela CONTRATANTE.

A funcionalidade possuirá natureza analítica, gerencial e estratégica, destinando-se ao apoio à gestão da mobilidade urbana, ao acompanhamento da execução contratual, à fiscalização eletrônica de trânsito, ao planejamento operacional e à produção de indicadores destinados à tomada de decisão pela CONTRATANTE.

#### 5.4.1. Consolidação, Tratamento e Estruturação dos Dados

5.4.1.1. A funcionalidade deverá consolidar automaticamente dados provenientes, no mínimo:
- a) dos Equipamentos de Fiscalização Eletrônica dos Tipos A, B, C e D;
- b) da Funcionalidade de Gestão e Classificação de Infrações;
- c) da Funcionalidade de Alertas de Irregularidades;
- d) dos sistemas corporativos integrados autorizados pela CONTRATANTE;
- e) do sistema de estacionamento rotativo digital, quando integrado;
- f) de bases externas autorizadas pela CONTRATANTE, quando compatíveis.

5.4.1.2. Os dados deverão ser tratados de forma estruturada, observando procedimentos de:
- a) consolidação;
- b) normalização;
- c) organização;
- d) rastreabilidade;
- e) consistência;
- f) integridade;
- g) anonimização, quando aplicável.

5.4.1.3. O tratamento dos dados deverá observar integralmente as disposições da Lei no 13.709/2018 – LGPD, sendo vedada utilização dos dados para finalidades estranhas às atividades institucionais da CONTRATANTE.

5.4.1.4. A funcionalidade deverá manter histórico consolidado dos dados operacionais e estatísticos, permitindo análises por:
- a) equipamento;
- b) local monitorado;
- c) faixa de rolamento;
- d) logradouro;
- e) bairro;
- f) região administrativa;
- g) período;
- h) dia da semana;
- i) faixa horária;
- j) tipo de infração;
- k) classificação veicular;
- l) demais parâmetros operacionais definidos pela CONTRATANTE.

#### 5.4.2. Painéis Analíticos e Dashboards

5.4.2.1. A funcionalidade deverá disponibilizar dashboards analíticos interativos destinados ao acompanhamento operacional, estatístico e gerencial da fiscalização eletrônica.

5.4.2.2. Os dashboards deverão possuir acesso via navegador web, sem necessidade de instalação de softwares adicionais.

5.4.2.3. Os painéis analíticos deverão permitir visualização consolidada de indicadores relacionados, no mínimo:
- a) ao volume de passagens veiculares;
- b) ao volume e distribuição de infrações;
- c) à evolução temporal de infrações e alertas;
- d) ao desempenho operacional dos equipamentos;
- e) à disponibilidade da solução;
- f) aos indicadores de aproveitamento dos registros;
- g) ao comportamento do fluxo viário;
- h) aos índices de conformidade relacionados às funcionalidades integradas;
- i) aos indicadores operacionais definidos pela CONTRATANTE.

5.4.2.4. Os dashboards deverão suportar:
- a) filtros dinâmicos por período, equipamento, localidade, faixa horária, tipo de infração e classificação veicular;
- b) atualização simultânea dos indicadores apresentados;
- c) visualização gráfica e tabular;
- d) exportação em formatos PDF, CSV, XLS e PNG;
- e) parametrização de indicadores e painéis pela CONTRATANTE.

5.4.2.5. A CONTRATANTE poderá configurar dashboards personalizados, selecionando indicadores, filtros, visualizações e parâmetros operacionais conforme necessidade institucional.

5.4.2.6. A funcionalidade poderá utilizar:
- a) gráficos;
- b) mapas temáticos;
- c) séries temporais;
- d) indicadores comparativos;
- e) mapas de calor;
- f) elementos visuais de apoio à análise operacional.

#### 5.4.3. Apoio à Decisão Operacional

5.4.3.1. A funcionalidade deverá disponibilizar mecanismos de apoio à tomada de decisão operacional baseados em análise estatística e histórica dos dados consolidados pela plataforma.

5.4.3.2. O sistema deverá permitir identificação automatizada de locais com maior incidência de infrações, considerando critérios parametrizáveis definidos pela CONTRATANTE.

5.4.3.3. A funcionalidade deverá permitir geração de indicadores destinados ao apoio:
- a) ao planejamento operacional;
- b) à fiscalização de trânsito;
- c) às operações especiais;
- d) à definição de prioridades operacionais;
- e) à avaliação da efetividade das ações de fiscalização;
- f) à gestão da mobilidade urbana.

5.4.3.4. O sistema deverá permitir comparação histórica e estatística dos indicadores operacionais antes e após:
- a) implantação ou remoção de equipamentos;
- b) alterações de sinalização;
- c) mudanças operacionais;
- d) realização de operações de fiscalização;
- e) alterações regulamentares relacionadas à circulação viária.

5.4.3.5. A funcionalidade deverá gerar alertas analíticos automáticos quando identificadas variações relevantes nos indicadores monitorados, conforme parâmetros definidos pela CONTRATANTE.

5.4.3.6. Os alertas analíticos poderão compreender, entre outros:
- a) aumento atípico de infrações;
- b) degradação de desempenho operacional;
- c) redução de disponibilidade dos equipamentos;
- d) aumento de retenções viárias;
- e) alterações relevantes nos padrões operacionais monitorados.

5.4.3.7. O sistema deverá permitir emissão automática de relatórios gerenciais periódicos contendo:
- a) resumo executivo dos indicadores monitorados;
- b) comparação com períodos anteriores;
- c) evolução dos principais indicadores;
- d) alertas analíticos identificados;
- e) informações destinadas ao acompanhamento da execução contratual.

#### 5.4.4. Análise Espacial e Georreferenciamento

5.4.4.1. A funcionalidade deverá permitir análise espacial dos dados operacionais por meio de mapa digital interativo.

5.4.4.2. A solução deverá suportar:
- a) identificação georreferenciada de eventos e infrações;
- b) mapas de calor;
- c) análise de concentração de ocorrências;
- d) comparação espacial entre regiões;
- e) visualização da distribuição dos equipamentos instalados;
- f) sobreposição de camadas operacionais;
- g) delimitação de áreas de interesse operacional.

5.4.4.3. O sistema deverá permitir filtros geográficos, temporais e operacionais combinados.

5.4.4.4. A funcionalidade deverá permitir exportação de dados georreferenciados em formatos compatíveis com sistemas de informação geográfica (GIS), conforme definição da CONTRATANTE.

#### 5.4.5. Relatórios Analíticos e Gerenciais

5.4.5.1. A funcionalidade deverá permitir geração de relatórios analíticos e gerenciais relacionados, no mínimo:
- a) ao volume de tráfego;
- b) à velocidade média operacional;
- c) à taxa de ocupação viária;
- d) ao comportamento infracional;
- e) ao desempenho dos equipamentos;
- f) aos indicadores de aproveitamento;
- g) às operações de fiscalização;
- h) à efetividade das ações operacionais;
- i) às funcionalidades integradas da solução.

5.4.5.2. Os relatórios deverão permitir:
- a) filtros parametrizáveis;
- b) segmentação por equipamento, período, localidade, faixa de rolamento, horário, enquadramento e classificação veicular;
- c) exportação em PDF, CSV e XLS;
- d) emissão automática periódica;
- e) geração sob demanda pela CONTRATANTE.

#### 5.4.6. Indicadores e Métricas Operacionais

5.4.6.1. O sistema deverá permitir definição, cálculo automático e acompanhamento contínuo de indicadores operacionais e estatísticos relacionados à fiscalização eletrônica.

5.4.6.2. A funcionalidade deverá suportar, no mínimo:
- a) indicadores de infração por volume de tráfego;
- b) indicadores de variação temporal de infrações;
- c) indicadores de disponibilidade operacional;
- d) indicadores de aproveitamento dos registros;
- e) indicadores de efetividade das ações de fiscalização;
- f) indicadores de segurança viária;
- g) indicadores de conformidade operacional;
- h) indicadores relacionados às funcionalidades integradas da solução.

5.4.6.3. Os indicadores deverão ser parametrizáveis pela CONTRATANTE, permitindo:
- a) definição de metas;
- b) definição de limites de atenção e criticidade;
- c) geração automática de alertas;
- d) acompanhamento histórico;
- e) visualização gráfica comparativa.

#### 5.4.7. Repositório Central de Logs Técnicos e Operacionais

5.4.7.1. A Funcionalidade de Inteligência de Dados deverá atuar como repositório central para recebimento, armazenamento, organização, consolidação e consulta dos logs técnicos e operacionais gerados pelos equipamentos de fiscalização eletrônica e pelos sistemas integrantes da solução.

5.4.7.2. O sistema deverá receber e armazenar automaticamente os logs operacionais e técnicos relacionados:
- a) ao funcionamento dos equipamentos;
- b) aos eventos operacionais;
- c) às falhas e degradações de desempenho;
- d) às alterações de configuração;
- e) às ações realizadas por usuários;
- f) às ocorrências sistêmicas relevantes.

5.4.7.3. O repositório deverá permitir consulta e visualização de logs relacionados, no mínimo:
- a) à alimentação elétrica;
- b) à comunicação de dados;
- c) ao sincronismo temporal;
- d) às falhas de hardware;
- e) às falhas de software;
- f) aos sensores e módulos operacionais;
- g) às alterações de parâmetros;
- h) às intervenções técnicas;
- i) às atividades de processamento e formação de lotes;
- j) aos eventos operacionais relevantes.

5.4.7.4. Todos os registros de log deverão possuir:
- a) data e horário;
- b) identificação do equipamento;
- c) identificação do usuário responsável, quando aplicável;
- d) descrição do evento;
- e) rastreabilidade integral da ocorrência.

5.4.7.5. O sistema deverá permitir:
- a) filtros por período, equipamento, localidade, categoria e severidade;
- b) correlação temporal entre eventos;
- c) exportação dos registros;
- d) emissão de relatórios técnicos;
- e) auditoria operacional dos eventos registrados.

5.4.7.6. O sistema deverá manter histórico das alterações realizadas nos parâmetros operacionais dos equipamentos e funcionalidades da solução, contendo:
- a) data e horário da alteração;
- b) parâmetro alterado;
- c) valor anterior;
- d) valor atualizado;
- e) identificação do responsável pela alteração.

5.4.7.7. Os registros de logs deverão permanecer íntegros, rastreáveis e disponíveis durante toda a vigência contratual e pelo prazo adicional definido pela política de retenção de dados da CONTRATANTE.

### 5.5. Serviço de Pré-Processamento de Imagens

#### 5.5.1. Responsabilidade, Escopo e Obrigatoriedade

5.5.1.1. A CONTRATADA será responsável pela execução integral do serviço de pré-processamento de imagens, compreendendo as atividades de recepção, triagem, dupla análise técnica independente, classificação preliminar e organização operacional dos registros gerados pelos equipamentos de fiscalização eletrônica dos Tipos A, B, C e D.

5.5.1.1-A. As atividades executadas pela CONTRATADA possuirão natureza exclusivamente material, instrumental, técnica e preparatória, não compreendendo exercício de poder de polícia administrativa, validação definitiva de infrações, lavratura de autos de infração, aplicação de penalidades ou prática de atos decisórios reservados à autoridade de trânsito.

5.5.1.1-B. O serviço de pré-processamento de imagens integra a parcela de operação e manutenção dos equipamentos e sistemas dos Tipos A, B, C e D.

5.5.1.2. O serviço de pré-processamento abrangerá, no mínimo:
- a) recepção e conferência operacional dos registros transmitidos pelos equipamentos de campo, incluindo verificação de integridade dos arquivos de imagem, vídeos operacionais, metadados e demais informações associadas;
- b) realização de leitura automática de placas (OCR/LAP) quando aplicável, com complementação operacional manual nos casos de inconsistência, baixa confiabilidade ou impossibilidade de leitura automatizada;
- c) verificação da legibilidade das placas veiculares e identificação dos registros inviáveis para validação infracional;
- d) conferência preliminar da compatibilidade entre o registro capturado, o enquadramento da infração e as condições operacionais e de sinalização aplicáveis ao local monitorado;
- e) verificação da qualidade técnica das imagens e registros, incluindo nitidez, luminosidade, enquadramento, sincronismo temporal e presença das informações obrigatórias;
- f) classificação preliminar dos registros nas categorias operacionais previstas neste Termo de Referência;
- g) realização obrigatória de dupla análise técnica independente por operadores distintos;
- h) organização, indexação e disponibilização dos registros classificados para posterior validação pela TRANSALVADOR;
- i) registro integral das ações executadas durante o fluxo operacional de pré-processamento.

5.5.1.3. A CONTRATADA deverá realizar o pré-processamento de 100% dos registros capturados pelos equipamentos de fiscalização, sendo vedado o descarte automático de registros sem análise operacional prévia.

5.5.1.4. Somente poderão ser disponibilizados à validação da TRANSALVADOR os registros que tenham sido submetidos a todas as etapas obrigatórias do fluxo de pré-processamento.

#### 5.5.2. Prazos, Continuidade e Capacidade Operacional

5.5.2.1. O prazo máximo para conclusão do pré-processamento de cada lote de registros será de até **72 (setenta e duas) horas** contadas da captura do registro pelo equipamento de campo.

5.5.2.2. A CONTRATADA deverá manter estrutura operacional, tecnológica e de pessoal permanentemente dimensionada para absorção do volume máximo de registros gerados pelo parque de equipamentos previsto neste Termo de Referência.

5.5.2.3. O serviço deverá operar de forma contínua e ininterrupta.

5.5.2.4. A CONTRATADA deverá manter quantitativo suficiente de operadores, supervisores e recursos técnicos para assegurar:
- a) cumprimento dos prazos operacionais estabelecidos;
- b) continuidade da operação em regime de contingência;
- c) absorção de picos de processamento;
- d) execução simultânea das etapas de triagem, reanálise e classificação;
- e) manutenção da qualidade operacional do serviço.

5.5.2.5. A CONTRATADA deverá possuir mecanismos de contingência operacional e tecnológica destinados à continuidade do serviço em situações de falha.

#### 5.5.3. Controle Operacional, Indicadores e Relatórios

5.5.3.1. A CONTRATADA deverá disponibilizar relatórios operacionais e gerenciais relativos ao serviço de pré-processamento, contemplando, no mínimo:
- a) volume de registros recebidos;
- b) volume de registros pré-processados;
- c) volume de registros disponibilizados para validação;
- d) volume de registros descartados;
- e) índice de aproveitamento dos registros;
- f) tempo médio de processamento por registro e por lote;
- g) produtividade operacional por operador;
- h) taxa de divergência entre operadores;
- i) taxa de retrabalho operacional;
- j) quantidade de registros pendentes de processamento;
- k) backlog operacional;
- l) cumprimento dos níveis mínimos de serviço (SLA);
- m) motivos de descarte ou inconsistência classificados por categoria;
- n) histórico de classificação, reclassificação e validação operacional.

5.5.3.2. Os relatórios deverão permitir segmentação por:
- a) período;
- b) equipamento;
- c) tipo de infração;
- d) operador;
- e) lote;
- f) local monitorado;
- g) faixa de rolamento;
- h) qualquer combinação dos parâmetros anteriores.

5.5.3.3. A CONTRATADA deverá apresentar, até o 5o dia útil de cada mês, relatório consolidado referente ao mês imediatamente anterior.

5.5.3.4. Os relatórios deverão possuir visualização em tela e exportação nos formatos PDF, CSV e XLS.

5.5.3.5. A CONTRATANTE poderá solicitar, a qualquer tempo, relatórios extraordinários, demonstrativos específicos, extrações de dados ou informações complementares.

#### 5.5.4. Rastreabilidade, Segurança e Vedações

5.5.4.1. A CONTRATADA responde integralmente pela qualidade, segurança, rastreabilidade, integridade e tempestividade do serviço de pré-processamento, sendo vedada a subcontratação total ou parcial das atividades sem prévia e expressa autorização da CONTRATANTE.

5.5.4.2. É vedado à CONTRATADA alterar, manipular, sobrescrever, adulterar ou eliminar os arquivos originais de imagem, vídeos operacionais, metadados ou quaisquer registros gerados pelos equipamentos de fiscalização.

5.5.4.3. Ajustes de visualização, incluindo brilho, contraste, zoom, inversão de cores e demais mecanismos de apoio à análise operacional, somente poderão ser realizados para fins de visualização em tela, sem modificação do arquivo-fonte original.

5.5.4.4. A CONTRATADA deverá assegurar rastreabilidade integral de todas as etapas do fluxo de pré-processamento, mantendo registros completos de log.

5.5.4.5. Os registros de log deverão conter, no mínimo:
- a) identificação do usuário responsável;
- b) data e horário da operação;
- c) estação de trabalho utilizada;
- d) tipo de ação executada;
- e) identificação do registro analisado;
- f) classificação atribuída;
- g) alterações realizadas;
- h) justificativas operacionais registradas.

5.5.4.6. Os registros de auditoria e logs operacionais deverão permanecer íntegros, acessíveis e disponíveis à CONTRATANTE durante toda a vigência contratual e pelo prazo adicional mínimo de **5 (cinco) anos** após seu encerramento.

5.5.4.7. A CONTRATADA deverá observar integralmente as disposições da Lei no 13.709/2018 – LGPD.

5.5.4.8. O acesso aos registros, imagens, dados operacionais e funcionalidades do sistema deverá ocorrer mediante controle individualizado de autenticação, segregação de perfis de acesso e utilização de mecanismos de segurança da informação.

5.5.4.9. A CONTRATANTE poderá, a qualquer tempo, realizar auditorias operacionais, testes de conformidade, verificações de rastreabilidade e inspeções nos fluxos de pré-processamento executados pela CONTRATADA.

### 5.6. Infraestrutura Sistêmica de Comunicação, Processamento e Transmissão de Dados

#### 5.6.1. Arquitetura Geral da Comunicação

5.6.1.1. A solução deverá possuir arquitetura integrada de comunicação entre os equipamentos de fiscalização eletrônica em campo, a Plataforma Central de Processamento e a Sala de Operações da TRANSALVADOR.

5.6.1.2. A infraestrutura de comunicação deverá possuir mecanismos de redundância lógica e física compatíveis com a criticidade operacional da solução.

5.6.1.3–5. Dimensionamento, protocolos seguros e monitoramento da disponibilidade dos enlaces.

#### 5.6.2. Transmissão de Imagens, Registros e Dados Operacionais

5.6.2.1. As imagens, registros operacionais, dados de infrações, metadados e informações de tráfego capturados pelos equipamentos deverão ser transmitidos automaticamente à Plataforma Central de Processamento.

5.6.2.2. A transmissão dos registros infracionais poderá ocorrer de forma periódica, observados os parâmetros operacionais e requisitos de tempestividade.

5.6.2.3. Nas situações que demandem resposta imediata (Blitz Eletrônica e alertas críticos), o tempo máximo de recepção não poderá ser superior a **4 (quatro) segundos**.

5.6.2.4–5. Transmissão contínua de dados de tráfego e dados operacionais de fluxo veicular.

#### 5.6.3. Criptografia e Segurança das Comunicações

5.6.3.1–4. Criptografia, autenticação, proteção contra interceptação, atualização remota de credenciais criptográficas.

#### 5.6.4. Armazenamento Local e Reconexão Automática

5.6.4.1. Reconexão automática em caso de perda de comunicação.

5.6.4.2. Armazenamento local seguro e criptografado durante indisponibilidade de comunicação.

5.6.4.3. Autonomia mínima equivalente a **6 (seis) horas** de operação ininterrupta.

5.6.4.4–5. Retransmissão automática dos registros após restabelecimento, preservando sequência cronológica, integridade e rastreabilidade.

#### 5.6.5. Continuidade Operacional e Energia Alternativa

5.6.5.1–4. Sistema alternativo de alimentação elétrica; retorno automático à operação; registro de falta/retorno de energia em log.

#### 5.6.6. Sincronismo Temporal

5.6.6.1. Sincronismo automático com base na Hora Legal Brasileira, mediante NTP, GPS ou tecnologia equivalente.

5.6.6.2–4. Intervalos de sincronismo, sincronismo remoto e monitoramento de desvios de horário.

#### 5.6.7. Configuração Remota dos Equipamentos

5.6.7.1–5. Configuração remota com autenticação individualizada, log completo de alterações, atualização de bases cadastrais, parametrização dinâmica sem alteração de código-fonte.

### 5.7. Tecnologia de Leitura Automática de Placas Veiculares — LAP

A tecnologia LAP constitui funcionalidade obrigatória e integrada a todos os equipamentos de fiscalização eletrônica (Tipos A, B, C e D), operando de forma automatizada, contínua e autônoma.

#### 5.7.1. Padrões de Leitura e Compatibilidade

5.7.1.1. Leitura simultânea de placas Mercosul e padrão anterior.

5.7.1.2. Compatibilidade contínua com padrões veiculares nacionais.

5.7.1.3. Detecção independente de cada caractere, minimizando erros (0/O, 1/I, B/8, D/0, S/5).

5.7.1.4. Registro individual de cada placa em composições veiculares com múltiplas placas visíveis.

#### 5.7.2. Índices Mínimos de Desempenho

5.7.2.1. Índices mínimos de acerto:
- **90%** para placas padrão Mercosul;
- **85%** para placas padrão anterior ao Mercosul.

5.7.2.2. Exclusões para fins de apuração: placas ausentes, encobertas, ilegíveis ou danificadas.

5.7.2.3. Aferição mensal com metodologia formal definida conjuntamente.

5.7.2.4. Relatório técnico específico por equipamento.

5.7.2.5. Descumprimento reiterado (2 meses consecutivos ou 3 alternados em 12 meses) caracteriza inexecução parcial.

#### 5.7.3. Condições Operacionais de Funcionamento

5.7.3.1. Operação em períodos diurnos e noturnos, variações climáticas, diferentes condições de tráfego.

5.7.3.2. Sistema de iluminação auxiliar próprio.

5.7.3.3. Falha na leitura automática não impede registro operacional ou infracional.

#### 5.7.4. Score de Confiança e Encaminhamento para Revisão

5.7.4.1. Indicador de confiança (score) atribuído automaticamente a cada leitura.

5.7.4.2. Limiar parametrizável pela CONTRATANTE.

5.7.4.3. Score inferior ao limiar → encaminhamento automático para revisão operacional.

5.7.4.4. Score armazenado como metadado permanente do registro.

#### 5.7.5. Rastreabilidade das Leituras

5.7.5.1. Distinção obrigatória entre leitura embarcada, leitura central e leitura corrigida manualmente.

5.7.5.2. Log automático para toda correção manual.

5.7.5.3. Histórico integral disponível por toda vigência contratual + **5 anos**.

5.7.5.4. Consulta por equipamento, período, score, tipo de intervenção e operador.

#### 5.7.6. Associação de Dados ao Registro de Leitura

5.7.6.1. Associação automática da leitura de placa aos demais dados operacionais: data/horário, local, equipamento, faixa, classificação veicular, score, imagem, vídeos.

5.7.6.2. Classificação veicular por mecanismos próprios do equipamento, não apenas por consulta à placa.

#### 5.7.7. Vídeos Operacionais Sintéticos — Time Lapse

5.7.7.1. Geração automatizada de vídeos time lapse associados aos registros LAP.

5.7.7.2–6. Finalidade operacional, gerencial, estatística e de auditoria; descarte automático após 72h quando não vinculados a registros ativos, mediante autorização expressa da CONTRATANTE.

#### 5.7.8. Anonimização e Proteção de Dados

5.7.8.1. Anonimização automática da placa veicular em registros exclusivamente estatísticos/analíticos, conforme LGPD.

5.7.8.2. Parametrização por categoria e por finalidade, sem impacto sobre registros infracionais.

5.7.8.3. Registro em log de ativação/desativação dos mecanismos de anonimização.

#### 5.7.9. Tempo de Resposta para Alertas Operacionais

5.7.9.1. Tempo máximo entre leitura da placa e geração do alerta: **4 (quatro) segundos**.

5.7.9.2. Monitoramento contínuo do tempo médio de processamento.

5.7.9.3. Registros históricos dos tempos de resposta LAP por equipamento e período.

#### 5.7.10. Homologação da Tecnologia LAP

5.7.10.1. Homologação formal antes do início da operação regular.

5.7.10.2. Testes diurnos e noturnos, índices por padrão de placa, score, rastreabilidade, integração e tempos de resposta.

5.7.10.3. Laudo técnico assinado pelas partes.

5.7.10.4. Novos procedimentos de homologação quando identificada degradação ou atualização tecnológica.

### 5.8. Infraestrutura de Datacenter, Disponibilidade e Continuidade Operacional

A infraestrutura de datacenter corresponde ao conjunto de recursos destinados à hospedagem, processamento, armazenamento, disponibilização, proteção e recuperação dos dados, sistemas e funcionalidades integrantes da solução.

#### 5.8.1. Requisitos Gerais da Infraestrutura

5.8.1.1. Dimensionamento para volume operacional estimado, com margem técnica para expansão.

5.8.1.2. Nível mínimo **Tier III** (TIA-942) ou equivalente.

5.8.1.3. Documentação técnica comprobatória em até 30 dias do início da vigência contratual.

5.8.1.4. Aderência a ABNT NBR ISO/IEC 27001, 27002, 22301 ou equivalentes.

5.8.1.5. Interoperabilidade e segurança compatíveis com diretrizes tecnológicas do Município de Salvador.

#### 5.8.2. Disponibilidade e Desempenho

5.8.2.1. Disponibilidade mínima mensal:
- **Sistema de Gestão**: 99,5%
- **Infraestrutura de datacenter / Central de Processamento**: 99%

Calculadas excluindo manutenção programada.

5.8.2.2. Manutenções programadas comunicadas com antecedência mínima de 72 horas.

5.8.2.3. Monitoramento contínuo com registros de indisponibilidade e degradação.

5.8.2.4. Descumprimento reiterado poderá caracterizar inexecução contratual parcial.

#### 5.8.3. Redundância e Alta Disponibilidade

5.8.3.1. Mecanismos de redundância:
- a) servidores/ambientes computacionais em alta disponibilidade;
- b) armazenamento e integridade de dados redundantes;
- c) links de comunicação redundantes;
- d) redundância de alimentação elétrica;
- e) redundância de climatização e controle ambiental.

5.8.3.2. Verificação periódica dos mecanismos de failover.

5.8.3.3. Ambiente de contingência geograficamente segregado; **RTO**: até 4 horas; **RPO**: até 1 hora.

#### 5.8.4. Segurança Física e Lógica

5.8.4.1. Controles de segurança física e lógica compatíveis com a criticidade.

5.8.4.2. Segmentação de ambientes, firewalls, MFA para acessos privilegiados, criptografia em trânsito e em repouso, gestão de vulnerabilidades, correlação de eventos de segurança.

5.8.4.3. Observância integral à LGPD.

5.8.4.4. Comunicação de incidentes de segurança em até **2 horas** da identificação.

#### 5.8.5. Backup, Recuperação e Continuidade Operacional

5.8.5.1. Política de backup abrangendo dados, registros, imagens, configurações e informações da solução.

5.8.5.2. Cópias em ambiente segregado ou distinto do ambiente principal.

5.8.5.3. Retenção compatível com legislação aplicável e diretrizes institucionais.

5.8.5.4. Plano de Recuperação de Desastres (PRD) e Plano de Continuidade de Negócios (PCN).

5.8.5.5. Submissão dos planos à CONTRATANTE em até 60 dias do início da vigência contratual.

5.8.5.6. Testes periódicos dos mecanismos de recuperação.

#### 5.8.6. Monitoramento e Relatórios

5.8.6.1. Monitoramento contínuo e automatizado da infraestrutura tecnológica.

5.8.6.2. Acesso da CONTRATANTE a painéis, relatórios gerenciais e registros operacionais.

5.8.6.3. Relatórios periódicos com indicadores de disponibilidade, ocorrências, ações corretivas e eventos de segurança.
