<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

<style>
  .secao-help h6 { margin-top: 1.1rem; font-weight: 600; }
  .secao-help h6:first-child { margin-top: 0; }
  .caminho { font-size: .85rem; color: #6c757d; }
</style>

<div class="container py-4" style="max-width: 960px;">

  <div class="d-flex align-items-center mb-4 gap-2">
    <h3 class="mb-0"><i class="bi bi-question-circle me-2"></i>Central de Ajuda</h3>
    <a href="javascript:history.back()" class="btn btn-sm btn-outline-secondary ms-auto">
      <i class="bi bi-arrow-left me-1"></i>Voltar
    </a>
  </div>

  <div class="input-group mb-4">
    <span class="input-group-text"><i class="bi bi-search"></i></span>
    <input type="text" id="campoBusca" class="form-control" placeholder="Buscar na ajuda..." onkeyup="filtrarSections()">
  </div>

  <!-- Sumário -->
  <div class="card mb-4" id="sumario">
    <div class="card-header"><h5 class="mb-0"><i class="bi bi-list-ul me-2"></i>Sumário</h5></div>
    <div class="card-body">
      <div class="row">
        <div class="col-md-4">
          <ol>
            <li><a href="#visao-geral">Visão Geral do Sistema</a></li>
            <li><a href="#login">Login e Autenticação</a></li>
            <li><a href="#home">Tela Inicial e Navegação</a></li>
            <li><a href="#registros">Registro de Fato</a></li>
            <li><a href="#monitoramento">Monitoramento</a></li>
            <li><a href="#atendimento-ocorrencias">Central de Atendimento</a></li>
          </ol>
        </div>
        <div class="col-md-4">
          <ol start="7">
            <li><a href="#consultas">Consulta de Veículos</a></li>
            <li><a href="#mapas">Mapas</a></li>
            <li><a href="#analise">Análise de Dados</a></li>
            <li><a href="#dashboards">Dashboards</a></li>
            <li><a href="#paineis-analise">Painéis de Análise</a></li>
            <li><a href="#blitz">Blitz Digital</a></li>
          </ol>
        </div>
        <div class="col-md-4">
          <ol start="13">
            <li><a href="#chamados">Sistema de Chamados</a></li>
            <li><a href="#cadastro">Cadastro e Configuração</a></li>
            <li><a href="#relatorios">Relatórios e Gráficos</a></li>
            <li><a href="#outras-telas">Telas abertas a partir de outras telas</a></li>
            <li><a href="#modo-escuro">Modo Escuro</a></li>
          </ol>
        </div>
      </div>
      <p class="small text-muted mb-0 mt-2">
        <i class="bi bi-info-circle me-1"></i>O botão <i class="bi bi-question-circle"></i> da barra superior abre esta ajuda já na seção da tela em que você está.
        Os cards exibidos dependem das permissões do seu grupo de usuários: se um módulo descrito aqui não aparece para você, solicite acesso ao administrador.
      </p>
    </div>
  </div>

  <!-- 1. Visão Geral -->
  <div class="card mb-3 secao-help" id="visao-geral">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">1. Visão Geral do Sistema</h5></div>
    <div class="card-body">
      <p>O <strong>Cinturão de Segurança (Muralha Digital)</strong> é um sistema integrado de monitoramento urbano que utiliza câmeras, sensores e leitores automáticos de placas (OCR) para:</p>
      <ul>
        <li><strong>Monitorar o tráfego</strong> em tempo real, capturando passagens de veículos e gerando alertas automáticos.</li>
        <li><strong>Identificar veículos de interesse</strong> (furto, roubo, clonagem, mandados) por cruzamento com o cadastro de monitorados.</li>
        <li><strong>Garantir a qualidade dos dados</strong>: integridade das imagens (SHA-256), score de leitura OCR, SLA de latência e de pré-processamento, disponibilidade dos equipamentos.</li>
        <li><strong>Analisar e apresentar indicadores</strong> em dashboards, painéis configuráveis, plataforma de análises e relatórios.</li>
        <li><strong>Apoiar operações</strong> com blitz, abordagens, atendimento de ocorrências e registro de fatos.</li>
        <li><strong>Gerenciar o suporte técnico</strong> por meio do Sistema de Chamados.</li>
      </ul>
      <div class="alert alert-info mb-0">
        <i class="bi bi-info-circle me-1"></i>
        Os módulos ficam em <strong>cards na Tela Inicial</strong>, organizados em categorias, e também no menu <strong>Processamento</strong> da barra superior. Veja a <a href="#home">seção 3</a>.
      </div>
    </div>
  </div>

  <!-- 2. Login -->
  <div class="card mb-3 secao-help" id="login">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">2. Login e Autenticação</h5></div>
    <div class="card-body">
      <h6>Acesso ao Sistema</h6>
      <ol>
        <li>Acesse o endereço do sistema no navegador.</li>
        <li>Informe seu <strong>login</strong> e <strong>senha</strong>.</li>
        <li>Clique em <strong>"Entrar"</strong>. Se o MFA estiver ativo para você, informe também o código do aplicativo autenticador.</li>
      </ol>
      <p>Após tentativas de senha incorretas o usuário pode ser bloqueado, conforme a política de senhas definida pelo administrador.</p>
      <h6>Tempo de Sessão</h6>
      <p>Seu login e o tempo restante da sessão aparecem no canto superior direito da barra. Ao expirar, o sistema volta para a tela de login.</p>
      <h6>Alterar Senha e Encerrar Sessão</h6>
      <p>Clique no ícone <i class="fas fa-user"></i> no canto superior direito e escolha <strong>"Alterar Senha"</strong> ou <strong>"Encerrar Sessão"</strong>.</p>
      <h6 id="mfa">Autenticação Multifator (MFA)</h6>
      <p>A configuração do MFA fica na tela <code>/muralha-digital/pages/meu-perfil/configurar-mfa.jsp</code> (ainda sem atalho no menu; o administrador pode informar o endereço). Leia o QR Code com um aplicativo autenticador (Google Authenticator, Microsoft Authenticator, Authy etc.) e informe o código gerado para confirmar. A partir daí, o login passa a pedir o código do aplicativo depois da senha.</p>
    </div>
  </div>

  <!-- 3. Tela Inicial e Navegação -->
  <div class="card mb-3 secao-help" id="home">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">3. Tela Inicial e Navegação</h5></div>
    <div class="card-body">
      <p>A Tela Inicial apresenta os módulos em <strong>cards com ícones</strong>. Alguns cards abrem diretamente uma tela; outros abrem uma <strong>categoria</strong> com novos cards. O <span class="text-danger fw-bold">*</span> vermelho abaixo de um card indica funcionalidade que atende itens do edital; passe o mouse sobre ele para ver os itens.</p>
      <h6>Categorias e módulos da Tela Inicial</h6>
      <table class="table table-sm table-bordered">
        <thead class="table-light"><tr><th>Card</th><th>Conteúdo</th></tr></thead>
        <tbody>
          <tr><td><a href="#registros"><strong>Registro de Fato</strong></a></td><td>Registro de fatos com boletim de ocorrência.</td></tr>
          <tr><td><a href="#monitoramento"><strong>Monitoramento</strong></a></td><td>Veículos monitorados, alertas e ocorrências, veículos em tempo real, vídeo.</td></tr>
          <tr><td><a href="#atendimento-ocorrencias"><strong>Central de Atendimento</strong></a></td><td>Atendimento de ocorrências e gestão das guarnições.</td></tr>
          <tr><td><a href="#consultas"><strong>Consulta de Veículos</strong></a></td><td>Consulta de passagens, veículos de carga e revisão de placas.</td></tr>
          <tr><td><a href="#mapas"><strong>Mapas</strong></a></td><td>Áreas monitoradas, mapa interativo, calor, equipamentos, incidentes e quantitativos.</td></tr>
          <tr><td><a href="#analise"><strong>Análise de Dados</strong></a></td><td>Indicadores de qualidade e operação, auditoria, logs, relatórios gerenciais e plataforma de análises.</td></tr>
          <tr><td><a href="#dashboards"><strong>Dashboards</strong></a></td><td>Painel informativo, dashboard principal e KPIs.</td></tr>
          <tr><td><a href="#paineis-analise"><strong>Painéis de Análise</strong></a></td><td>Painéis montados pelo próprio usuário com widgets.</td></tr>
          <tr><td><a href="#blitz"><strong>Blitz Digital</strong></a></td><td>Cadastro de blitz, blitz ostensiva e abordagens.</td></tr>
          <tr><td><a href="#chamados"><strong>Sistema de Chamados</strong></a></td><td>Abertura e acompanhamento de chamados técnicos, relatórios e configuração.</td></tr>
          <tr><td><a href="#cadastro"><strong>Cadastro e Configuração</strong></a></td><td>Gestão de lotes, catálogo de datasets (e de dados), pontos de interesse e configuração do monitoramento.</td></tr>
        </tbody>
      </table>

      <h6 id="navegacao">Barra superior</h6>
      <table class="table table-sm table-bordered">
        <thead class="table-light"><tr><th>Item</th><th>Função</th></tr></thead>
        <tbody>
          <tr><td><strong>Home</strong></td><td>Volta à Tela Inicial.</td></tr>
          <tr><td><strong>Processamento</strong></td><td>Lista os mesmos módulos da Tela Inicial, para acesso rápido de qualquer tela.</td></tr>
          <tr><td><strong>Relatórios</strong> e <strong>Gráficos</strong></td><td>Relatórios e gráficos estatísticos (<a href="#relatorios">seção 15</a>).</td></tr>
          <tr><td><strong>GTW</strong></td><td>Acesso à consulta de veículos do sistema GTW clássico.</td></tr>
          <tr><td><i class="bi bi-question-circle"></i> Ajuda</td><td>Abre esta Central de Ajuda na seção da tela atual.</td></tr>
          <tr><td><i class="bi bi-moon-stars"></i> Tema</td><td>Alterna entre modo claro e escuro (<a href="#modo-escuro">seção 17</a>).</td></tr>
          <tr><td><i class="fas fa-envelope text-warning"></i> Envelope (laranja)</td><td>Quantidade de alertas ainda não tratados; abre a lista de lembretes.</td></tr>
          <tr><td><i class="fas fa-bell text-warning"></i> Sino (laranja)</td><td>Alertas sob sua responsabilidade ainda sem assinatura; abre a consulta já filtrada.</td></tr>
          <tr><td><i class="fas fa-user"></i> Usuário</td><td>Login, tempo de sessão, <strong>Alterar Senha</strong> e <strong>Encerrar Sessão</strong>.</td></tr>
        </tbody>
      </table>

      <h6 id="pesquisa-rapida">Pesquisa Rápida</h6>
      <p>No cabeçalho da Tela Inicial há o seletor <strong>"Pesquisa Rápida"</strong>. Escolha o tipo de busca — <strong>Pesquisa Rápida</strong>, <strong>CPF</strong>, <strong>Nome</strong> ou <strong>Veículo</strong> —, digite o valor no campo que se abre e clique na lupa. O resultado é exibido em uma janela sobre a tela.</p>
    </div>
  </div>

  <!-- 4. Registro de Fato -->
  <div class="card mb-3 secao-help" id="registros">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">4. Registro de Fato</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Registro de Fato</p>
      <h6 id="registros-fato">4.1 Registro de Fato com Boletim</h6>
      <p>Registro formal de fatos relevantes observados durante o monitoramento, vinculado a um Boletim de Ocorrência. O cadastro tem abas para endereço, indivíduos envolvidos, veículos, objetos apreendidos e documentos. A consulta filtra por período, tipo de fato e situação.</p>
      <h6 id="boletins">4.2 Boletins de Ocorrência</h6>
      <p>O boletim é preenchido dentro do Registro de Fato (aba do boletim e botão de edição do registro), com dados do boletim, indivíduos, veículos, apreensões e documentos anexos.</p>
      <p>Os relatórios de fatos (por tipo, estatísticos e de pendências) ficam no menu <a href="#relatorios">Relatórios</a>.</p>
    </div>
  </div>

  <!-- 5. Monitoramento -->
  <div class="card mb-3 secao-help" id="monitoramento">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">5. Monitoramento</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Monitoramento</p>

      <h6 id="monitorados">5.1 Veículos Monitorados</h6>
      <p>Cadastro das placas de interesse. Quando um veículo cadastrado é captado por qualquer equipamento, o sistema gera um alerta automático.</p>
      <ol>
        <li>Abra <strong>Monitoramento → Veículos Monitorados</strong>.</li>
        <li>Inclua um novo monitorado informando a placa, o tipo de alerta (furtado, roubado, clonado, monitorado etc.) e o período de validade.</li>
        <li>A partir daí o veículo é verificado em todas as passagens capturadas.</li>
      </ol>

      <h6 id="alertas">5.2 Alerta e Ocorrência</h6>
      <p>Quando um veículo monitorado é captado, o alerta aparece como notificação na tela, com cor e ícone do tipo de alerta, e entra na contagem da barra superior (<i class="fas fa-envelope text-warning"></i> e <i class="fas fa-bell text-warning"></i>). Também existem alertas operacionais (congestionamento/retenção, bloqueio de via, estacionamento irregular, falha de equipamento, integridade de imagem divergente).</p>

      <h6 id="consulta-alertas">5.2.1 Consulta de Alertas e Ocorrências</h6>
      <p>Filtre por período, tipo de alerta, status (pendente, ocorrência, vinculado, descartado), assinatura, localidade, usuário responsável e resultado do tratamento. A lista pode ser exportada em CSV.</p>

      <h6 id="tratar-alerta">5.2.2 Tratar um Alerta</h6>
      <ol>
        <li>Clique no alerta (notificação, barra superior ou Consulta de Alertas).</li>
        <li>Confira a imagem da passagem e os dados do veículo; use a <a href="#linha-tempo">Linha do Tempo</a> e o <a href="#mapa-passagens">Mapa de Passagens</a> para ver o trajeto.</li>
        <li>Preencha o <strong>questionário de tratativa</strong>.</li>
        <li>Defina o resultado: encaminhar guarnição/registrar ocorrência, vincular ou descartar (com motivo).</li>
        <li><strong>Assine</strong> o alerta para concluir o tratamento.</li>
      </ol>

      <h6 id="veiculos-tempo-real">5.3 Veículos em Tempo Real e Veículo de Carga em Tempo Real</h6>
      <p>Passagens capturadas em tempo real, atualizadas automaticamente: placa, imagem, equipamento, velocidade e horário. O card <strong>Veículo de Carga em Tempo Real</strong> mostra apenas veículos de carga. A partir da passagem é possível abrir o <a href="#video-passagem-tr">vídeo da passagem</a>.</p>

      <h6 id="tempo-real">5.4 Vídeo</h6>
      <p id="mosaico-videos"><strong>Vídeo Monitoramento</strong> abre o mosaico de vídeos: grade com várias câmeras ao mesmo tempo. Use <strong>Configurações</strong> para escolher o layout e as câmeras, e <strong>Atualizar</strong> para recarregar as transmissões.</p>
      <p id="video-ao-vivo"><strong>Vídeo ao Vivo</strong> abre a transmissão ao vivo das câmeras; selecione a câmera desejada para iniciar.</p>
    </div>
  </div>

  <!-- 6. Central de Atendimento -->
  <div class="card mb-3 secao-help" id="atendimento-ocorrencias">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">6. Central de Atendimento</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Central de Atendimento</p>
      <p>Quando um alerta resulta em ação de campo, uma <strong>ocorrência</strong> é registrada com local, horário, guarnição envolvida, relato e boletim vinculado. A Central permite abrir, acompanhar, editar e finalizar ocorrências.</p>

      <h6 id="guarnicoes">6.1 Guarnições</h6>
      <p class="caminho">Central de Atendimento → botão <strong>Gerenciar Guarnições</strong> → <strong>Gerenciar</strong></p>
      <p>O botão <strong>Gerenciar Guarnições</strong> mostra a disponibilidade das equipes de campo; nessa janela, <strong>Gerenciar</strong> abre o cadastro de guarnições: composição (membros e viatura), situação (ativa ou inativa) e área de atuação.</p>
    </div>
  </div>

  <!-- 7. Consulta de Veículos -->
  <div class="card mb-3 secao-help" id="consultas">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">7. Consulta de Veículos</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Consulta de Veículos</p>

      <h6 id="consulta-veiculo">7.1 Consulta de Veículos</h6>
      <ol>
        <li>Informe a placa (parcial ou completa) e o período; refine por equipamento se necessário.</li>
        <li>O sistema lista as passagens com data/hora, equipamento, imagem e velocidade.</li>
        <li>Abra uma passagem para ver o detalhe do veículo, a imagem ampliada, a assinatura digital, a <a href="#linha-tempo">Linha do Tempo</a> e o <a href="#mapa-passagens">Mapa de Passagens</a>.</li>
        <li>Placas lidas com erro podem ser enviadas para a <a href="#correcao-placas">Correção de Placas em Lote</a>.</li>
      </ol>

      <h6 id="consulta-carga">7.2 Consulta de Veículos de Carga</h6>
      <p>Consulta específica de veículos de carga, com filtros adicionais por porte, eixos e classificação.</p>

      <h6 id="revisao-placas">7.3 Revisão de Placas</h6>
      <p>Fila de leituras de placa que precisam de revisão humana. Uma leitura entra na fila automaticamente quando o <strong>score</strong> do OCR fica abaixo do limiar configurado (global ou por equipamento), quando não há placa lida, quando a leitura do equipamento diverge da central ou quando o score não é informado.</p>
      <ul>
        <li><strong>Filtros:</strong> período, equipamentos, situação (Pendente, Corrigida, Conferida), motivo, faixa de score, tipo de intervenção e operador.</li>
        <li>Cada passagem mostra todas as placas lidas, com a origem da leitura (embarcada, central ou manual), o score e o limiar.</li>
        <li>Toda correção manual exige <strong>motivo</strong> e <strong>justificativa</strong> e fica registrada com o operador.</li>
      </ul>
    </div>
  </div>

  <!-- 8. Mapas -->
  <div class="card mb-3 secao-help" id="mapas">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">8. Mapas</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Mapas</p>

      <h6 id="mancha-monitorada">8.1 Cadastro de Área Monitorada</h6>
      <p>Desenhe no mapa as áreas monitoradas (perímetros) e clique em <strong>Cadastrar</strong>. Use <strong>Todas as áreas</strong> para exibir as áreas já cadastradas e <strong>Limpar</strong> para recomeçar o desenho. As áreas são usadas, por exemplo, no relatório de permanência de veículos.</p>

      <h6 id="mapa-interativo">8.2 Mapa Interativo</h6>
      <p>Mapa geral com camadas sobrepostas: equipamentos, alertas, ocorrências e pontos de interesse. Ative ou desative as camadas no painel lateral.</p>

      <h6 id="mapa-calor">8.3 Mapa de Calor</h6>
      <p>Concentração de passagens veiculares como mapa de calor: áreas mais quentes indicam maior fluxo. Filtre por período e tipo de veículo.</p>

      <h6 id="mapa-equipamentos">8.4 Mapa de Equipamentos</h6>
      <p>Localização dos equipamentos (câmeras e leitores OCR) com indicação de situação. Clique em um equipamento para ver detalhes e últimas passagens.</p>

      <h6 id="mapa-incidentes">8.5 Mapa de Incidentes</h6>
      <p>Incidentes de trânsito de fontes externas (Waze for Cities ou importação de arquivo CSV) exibidos sobre o mapa dos equipamentos, para correlação com os dados internos. A importação automática do Waze depende da chave de acesso configurada pelo administrador.</p>

      <h6 id="mapa-alertas">8.6 Quantitativos em Camadas</h6>
      <p>Quantitativos de alertas e ocorrências por região, apresentados em camadas no mapa. Defina os filtros e clique em <strong>Processar</strong>.</p>
    </div>
  </div>

  <!-- 9. Análise de Dados -->
  <div class="card mb-3 secao-help" id="analise">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">9. Análise de Dados</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Análise de Dados (14 cards)</p>

      <h6 id="sla-latencia">9.1 SLA de Latência</h6>
      <p>Mede o tempo entre a passagem do veículo e a chegada do registro ao servidor. Os cards mostram a <strong>latência média</strong> e a <strong>máxima</strong> dos últimos 5 minutos, a quantidade de passagens no período e o <strong>status do SLA</strong>. A cada 5 minutos o sistema calcula o percentil 95 (P95) e registra violação quando ele ultrapassa o limite (<em>threshold</em>); a tabela mostra esse histórico e os locais com maior latência.</p>

      <h6 id="assertividade">9.2 Assertividade</h6>
      <p>Compara a pré-classificação automática com a validação final dos operadores.</p>
      <ul>
        <li><strong>Período:</strong> Hoje, 7 dias, 30 dias ou customizado; atualização automática opcional (<strong>Auto</strong>, a cada 30 s).</li>
        <li><strong>Indicadores:</strong> taxa de concordância, volume processado, divergências, registros em revisão manual, pré-aprovadas, desempates, reprovadas e primeira análise.</li>
        <li>Quando a taxa de concordância fica <strong>abaixo de 80%</strong>, o painel exibe alerta e destaca o indicador em vermelho; o gráfico de evolução mostra a linha de meta de 80%.</li>
        <li>Tabela por operador (total analisado, concordantes e taxa) e exportação em CSV.</li>
      </ul>

      <h6 id="sla-preproc">9.3 SLA de Pré-processamento (72h)</h6>
      <p>Controla o prazo de 72 horas entre a captura e a pré-classificação. Os cards mostram o total de pendentes e quantos estão <strong>acima de 72h</strong>, <strong>entre 48h e 72h</strong> e <strong>entre 24h e 48h</strong>. A tabela detalha os pendentes por local, com o maior tempo de espera (<em>aging</em>) e o status.</p>

      <h6 id="timelapse">9.4 Time-lapse</h6>
      <p>Reproduz em sequência as imagens das passagens de um equipamento em um intervalo de tempo.</p>
      <ol>
        <li>Escolha o <strong>equipamento</strong>, o <strong>início</strong> e o <strong>fim</strong> do intervalo.</li>
        <li>Inicie a reprodução; use pausar, avançar e voltar quadro a quadro.</li>
        <li>Ajuste a velocidade em quadros por segundo (<strong>FPS</strong>).</li>
      </ol>

      <h6 id="disponibilidade">9.5 Disponibilidade</h6>
      <p>Situação dos equipamentos de captura: quantos estão <strong>online</strong> e <strong>offline</strong> agora, o <strong>uptime médio das últimas 24h</strong> e o total monitorado. A tabela mostra, por equipamento, o status, a última passagem e há quanto tempo está offline. A inatividade é detectada automaticamente e registrada em histórico.</p>

      <h6 id="log-auditoria">9.6 Log de Auditoria</h6>
      <p>Registro centralizado das ações dos usuários: logins, consultas, alterações de cadastro, tratamento de alertas, exportações e mudanças de parâmetros. Filtre por <strong>login</strong>, <strong>funcionalidade</strong>, <strong>operação</strong>, <strong>perfil</strong>, <strong>equipamento</strong> e período; cada registro mostra data/hora, IP, identificador do registro afetado e descrição.</p>

      <h6 id="integridade">9.7 Integridade das Imagens</h6>
      <p>Confere a integridade das imagens recebidas comparando o <strong>SHA-256</strong> calculado com o hash enviado pela origem.</p>
      <ul>
        <li><strong>Filtros:</strong> período, equipamentos e status: <em>Íntegro</em>, <em>Divergente</em>, <em>Sem conferência</em> (a origem não envia hash) e <em>Erro de leitura</em>.</li>
        <li><strong>Resumo por dia e equipamento:</strong> recebidas, íntegras, divergentes e rejeitadas.</li>
        <li><strong>Detalhe por imagem:</strong> captura, recepção, origem do hash, SHA-256 calculado, motivo e data da verificação.</li>
        <li>A origem do hash é configurada por equipamento. Uma imagem divergente gera alerta do tipo <em>Integridade de imagem divergente</em>.</li>
      </ul>

      <h6 id="logs-tecnicos">9.8 Logs Técnicos</h6>
      <p>Repositório único dos logs técnicos e operacionais dos equipamentos e do sistema.</p>
      <ul>
        <li><strong>Filtros:</strong> período, equipamentos, localidades, categorias e severidades.</li>
        <li>Cada evento mostra data/hora, equipamento, origem, categoria, severidade, descrição, usuário e a <strong>correlação</strong> com eventos próximos no tempo; eventos com duração aparecem como períodos de início, fim e duração.</li>
        <li><strong>Histórico de parâmetros:</strong> alterações de configuração com recurso, identificador, parâmetro, <strong>valor anterior</strong>, <strong>valor novo</strong> e responsável.</li>
        <li>Exportação dos registros e relatório técnico por equipamento.</li>
      </ul>

      <h6 id="tipos-alerta">9.9 Tipos de Alerta</h6>
      <p>Configura, para cada tipo de alerta, a criticidade, a cor e o ícone do aviso exibido aos operadores e os tratamentos de encaminhamento.</p>
      <ul>
        <li><strong>Crítico, Cor e Ícone:</strong> altere na linha do tipo (a coluna <em>Pré-visualização</em> mostra como o aviso vai aparecer) e clique em <strong>Salvar</strong>. A cor e o ícone valem para os avisos exibidos depois que o operador recarregar a página. O ícone é uma classe do Bootstrap Icons, como <code>bi-exclamation-triangle-fill</code>; há sugestões ao digitar.</li>
        <li><strong>Tratamentos:</strong> o botão abre a lista de encaminhamentos (fiscalização em campo, análise operacional, registro estatístico, integração corporativa, geração de ocorrência, priorização para auditoria, outro). Marque os aplicáveis e, quando permitido, <strong>Automático</strong>. Os automáticos são executados pelo sistema em até 1 minuto após a chegada do alerta: registro estatístico e priorização para auditoria ficam na auditoria; a geração de ocorrência muda o alerta de <em>Pendente</em> para <em>Ocorrência</em>. Os demais dependem do operador.</li>
        <li>Prioridade e descrição vêm do cadastro de tipos de ocorrência e aparecem sem edição.</li>
        <li>Toda alteração fica registrada no histórico de parâmetros (valor anterior e novo) e no Log de Auditoria.</li>
      </ul>

      <h6 id="relatorios-gerenciais">9.10 Relatórios Gerenciais</h6>
      <p>Relatórios periódicos gerados automaticamente pelo sistema:</p>
      <ul>
        <li><strong>Gerencial (mensal):</strong> resumo dos KPIs (média, mínimo, máximo e contagem por status), comparação com o mês anterior, alertas analíticos, volume de tráfego e disponibilidade dos equipamentos.</li>
        <li><strong>Pré-Processamento (mensal):</strong> as 14 métricas do pré-processamento (volumes, aproveitamento, tempo médio, produtividade por operador, divergências, retrabalho, pendentes, backlog, SLA, motivos de descarte e histórico de classificação).</li>
      </ul>
      <p>Filtre por tipo, abra o detalhe de cada relatório e exporte em <strong>CSV</strong> ou <strong>XLSX</strong>.</p>

      <h6 id="pre-processamento">9.11 Pré-Processamento</h6>
      <p>Painel operacional do pré-processamento em tempo real. Escolha o período (7, 30, 90 dias ou customizado); a atualização automática (<strong>Auto</strong>) recarrega a cada 60 s.</p>
      <ul>
        <li>Cards de volumes <strong>recebidos</strong>, <strong>processados</strong>, <strong>disponibilizados</strong> e <strong>descartados</strong>, aproveitamento, SLA de 72h, pendentes e backlog acima de 72h.</li>
        <li>Gráficos de recebidos × processados por dia e de distribuição da classificação.</li>
        <li>Tabela de produtividade por operador.</li>
      </ul>

      <h6 id="plataforma-analises">9.12 Análises (Plataforma de Análises)</h6>
      <p>Painéis interativos da plataforma de análises (Apache Superset), exibidos dentro do sistema.</p>
      <ol>
        <li>Abra o card <strong>Análises</strong> e escolha um painel na lista <strong>Painel</strong>. O grupo <em>Demonstração (dados reais)</em> traz Fluxo Veicular, Fiscalização e Infrações, Tráfego Histórico, Mobilidade Urbana (Waze) e Alertas de Veículos de Interesse; o grupo <em>Painéis operacionais</em> traz os painéis de KPIs, assertividade, SLA, disponibilidade, pré-processamento, alertas, infrações, passagens e mapa.</li>
        <li>Use a barra de filtros do painel (período e dimensões) e clique em barras ou fatias para aplicar <strong>filtro cruzado</strong> nos demais gráficos.</li>
        <li>O botão <strong>Abrir no Superset</strong> abre o painel em uma nova aba, com as ferramentas completas da plataforma.</li>
      </ol>
      <p class="small text-muted">Os painéis consultam o banco ao vivo; a primeira abertura de um painel pode levar alguns segundos e as seguintes usam cache. O acesso usa o mesmo login do sistema.</p>

      <h6 id="perfil-comportamental">9.13 Perfil Comportamental</h6>
      <p>Padrão de deslocamento de um veículo: horários frequentes, rotas habituais, frequência de passagem e anomalias de comportamento. A partir do perfil é possível abrir a Linha do Tempo e o Mapa de Passagens.</p>

      <h6 id="veiculos-correlacionados">9.14 Análise de Correlacionamentos</h6>
      <p>Identifica veículos que trafegam juntos (mesma rota em horários próximos). Útil para investigação de comboios, escolta ou associação entre veículos suspeitos.</p>
    </div>
  </div>

  <!-- 10. Dashboards -->
  <div class="card mb-3 secao-help" id="dashboards">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">10. Dashboards</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Dashboards</p>

      <h6 id="painel-informacoes">10.1 Painel Informativo</h6>
      <p>Painel para telas de monitoramento (TV ou projetor) com percentual de leitura de placas por câmera no período escolhido (data e hora inicial e final), gestão de armazenamento do servidor, total de informações e notificações importantes.</p>

      <h6 id="dashboard-principal">10.2 Dashboards (Dashboard Principal)</h6>
      <p>Indicadores gerais do sistema: total de passagens, alertas, situação dos equipamentos e gráficos de tendência. A partir dele abre-se o <a href="#calendario-intensidade">Calendário de Intensidade</a>.</p>

      <h6 id="dashboard-kpis">10.3 KPIs</h6>
      <p>Indicadores de desempenho com cor conforme os limites ideal e de alerta, e atualização automática.</p>
      <ol>
        <li>Acompanhe os cards dos indicadores; passe o mouse sobre os gráficos para ver valores.</li>
        <li>Clique em <strong>Histórico</strong> em um KPI para ver sua evolução (7, 30 ou 90 dias) com a linha de meta.</li>
      </ol>
      <p>Quando um KPI entra em situação de alerta ou crítica, o sistema registra um alerta analítico automaticamente.</p>

      <h6 id="config-kpis">10.3.1 Configuração de KPIs</h6>
      <p class="caminho">Dashboards → KPIs → botão <strong>Configurar</strong></p>
      <p>Define os indicadores exibidos no painel: nome, descrição, unidade, ordem, consulta SQL (deve retornar a coluna <code>valor</code>), limite ideal, limite de alerta, forma de exibição, cor e se o KPI está ativo.</p>
    </div>
  </div>

  <!-- 11. Painéis de Análise -->
  <div class="card mb-3 secao-help" id="paineis-analise">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">11. Painéis de Análise</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Painéis de Análise</p>
      <p>Permite que o próprio usuário monte painéis com widgets, sem escrever SQL, usando os datasets do <a href="#catalogo-datasets">Catálogo de Datasets</a>. O passo a passo completo está no <a href="/muralha-digital/pages/analises/manual.jsp">manual dos Painéis de Análise</a>.</p>

      <h6>11.1 Criar um painel e adicionar widgets</h6>
      <ol>
        <li>Na lista de painéis, crie um painel novo ou abra um existente.</li>
        <li>Clique em <strong>+ Widget</strong> e escolha o <strong>dataset</strong>.</li>
        <li>Escolha o tipo de visualização e <strong>arraste os campos</strong> para as zonas (por exemplo, dimensão no Eixo X e medida no Eixo Y/Valores). Cada zona aceita apenas os tipos de campo adequados e fica destacada durante o arraste.</li>
        <li>Para retirar um campo, clique no <strong>×</strong> ao lado do nome. Trocar o dataset limpa as zonas.</li>
        <li>Ajuste granularidade (padrão: <em>Sem agrupamento</em>), período, limite e filtros; o preview é refeito a cada alteração. Use <strong>Ver como tabela</strong> para conferir os dados.</li>
        <li>Salve. O widget novo entra abaixo dos já existentes; arraste-o para reposicionar.</li>
      </ol>

      <h6>11.2 Tipos de widget</h6>
      <p>Card de KPI, barras, linhas, área, pizza/rosca, dispersão, funil, gauge, heatmap, combinado, tabela, tabela pivot e mapa.</p>

      <h6>11.3 Uso do painel</h6>
      <ul>
        <li><strong>Filtros do painel</strong> e <strong>filtro cruzado</strong>: clicar em um elemento de um widget filtra os demais.</li>
        <li><strong>Detalhamento (drill-down)</strong> nos widgets que o permitem.</li>
        <li><strong>Exportação</strong> dos dados e do painel, <strong>compartilhamento</strong> e <strong>histórico de versões</strong>.</li>
      </ul>

      <h6 id="dashboard-builder">11.4 Dashboard Builder (versão anterior)</h6>
      <p>A ferramenta anterior de montagem de dashboards continua disponível em <code>/muralha-digital/pages/dashboard/builder/index.jsp</code>, sem card na Tela Inicial, e usa os datasets cadastrados em <a href="#cadastro-datasets">Cadastro de Datasets (versão anterior)</a>. Para novos painéis, use os Painéis de Análise.</p>
    </div>
  </div>

  <!-- 12. Blitz Digital -->
  <div class="card mb-3 secao-help" id="blitz">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">12. Blitz Digital</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Blitz Digital</p>

      <h6 id="blitz-gestao">12.1 Cadastro (Gestão de Blitz)</h6>
      <p>Crie e gerencie operações de blitz, que reúnem as abordagens realizadas em um local e período.</p>
      <ol>
        <li>Abra <strong>Blitz Digital → Cadastro</strong> e inclua uma nova blitz.</li>
        <li>Informe local, data, horário e guarnição responsável.</li>
        <li>Durante a operação, registre as abordagens a partir da blitz criada.</li>
      </ol>

      <h6 id="blitz-ostensiva">12.2 Blitz Ostensiva</h6>
      <p>Controle das operações de fiscalização ostensiva em campo, com acesso às passagens relacionadas dos veículos abordados para análise de histórico.</p>

      <h6 id="blitz-abordagem">12.3 Abordagem</h6>
      <p>Registro de cada abordagem: placa do veículo, dados do condutor, documentação verificada e resultado (liberado, apreendido, notificado). A listagem permite consultar o histórico por CPF e por placa.</p>
    </div>
  </div>

  <!-- 13. Sistema de Chamados -->
  <div class="card mb-3 secao-help" id="chamados">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">13. Sistema de Chamados</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Sistema de Chamados</p>
      <p>Central de suporte técnico. O detalhamento por perfil (solicitante, gestor, equipe e administrador) está no <a href="/muralha-digital/pages/chamados/manual.jsp">manual do Sistema de Chamados</a>.</p>

      <h6>13.1 Chamados</h6>
      <ol>
        <li>Para abrir um chamado, informe <strong>categoria</strong>, <strong>subcategoria</strong>, <strong>equipamento</strong>, <strong>prioridade</strong>, <strong>título</strong> e <strong>descrição</strong>; anexe até 5 arquivos PDF, PNG ou JPG.</li>
        <li>O chamado recebe um <strong>número de protocolo</strong> e a confirmação é enviada por e-mail.</li>
        <li>Acompanhe status, responsável e os prazos de SLA de atendimento e de resolução; a lista é atualizada a cada 30 s. Filtre por protocolo, status, prioridade, categoria, equipamento e período.</li>
        <li>Ao final, avalie o atendimento.</li>
      </ol>

      <h6 id="chamados-relatorios">13.2 Relatórios de Chamados</h6>
      <p class="caminho">Chamados → botão <strong>Relatórios</strong> (perfis Gestor, Equipe e Administrador)</p>
      <p>Indicadores por período, categoria, equipamento e prioridade, com agrupamento escolhido: total, resolvidos, cancelados, % de SLA de atendimento e de resolução, tempo médio de atendimento (TMA) e de resolução (TMR). Exportação em CSV, XLSX e PDF.</p>

      <h6 id="chamados-configuracao">13.3 Configuração de Chamados</h6>
      <p class="caminho">Chamados → botão <strong>Configuração</strong> (perfil Administrador)</p>
      <p>Abas <strong>Parâmetros de SLA</strong> (percentual de risco e fechamento automático), <strong>Perfis de acesso</strong>, <strong>Prioridades e prazos</strong>, <strong>Categorias</strong> e <strong>E-mail (SMTP)</strong>.</p>
    </div>
  </div>

  <!-- 14. Cadastro e Configuração -->
  <div class="card mb-3 secao-help" id="cadastro">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">14. Cadastro e Configuração</h5></div>
    <div class="card-body">
      <p class="caminho">Tela Inicial → Cadastro e Configuração</p>

      <p class="small text-muted">A configuração de KPIs, o cadastro de guarnições e o Catálogo de Dados são abertos pelos botões das telas a que pertencem (<a href="#config-kpis">KPIs</a>, <a href="#guarnicoes">Central de Atendimento</a> e <a href="#catalogo-dados">Catálogo de Datasets</a>).</p>

      <h6 id="gestao-lotes">14.1 Gestão de Lotes</h6>
      <p>Agrupa infrações pré-aprovadas em lotes para encaminhamento ao órgão de trânsito.</p>
      <ol>
        <li>Crie um lote e inclua as infrações (placa, local, pista e data da captura).</li>
        <li>Acompanhe a situação: <em>Rascunho</em> → <em>Enviado</em> → <em>Confirmado</em> ou <em>Cancelado</em>.</li>
        <li>A lista mostra código, descrição, situação, datas de criação e envio e a quantidade de infrações de cada lote.</li>
      </ol>

      <h6 id="catalogo-datasets">14.2 Catálogo de Datasets</h6>
      <p>Datasets usados pelos <a href="#paineis-analise">Painéis de Análise</a>: nome, tabela base, situação e versão. Os datasets são montados no modelador visual a partir das tabelas do Catálogo de Dados, sem digitar SQL.</p>

      <h6 id="catalogo-dados">14.2.1 Catálogo de Dados</h6>
      <p class="caminho">Cadastro e Configuração → Catálogo de Datasets → botão <strong>Catálogo de Dados</strong></p>
      <p>Descrição das tabelas disponíveis para análise: rótulo, schema e nome, chave primária e situação; as colunas (nome físico, tipo, papel sugerido e indicação de <strong>dado pessoal sensível</strong>) e os relacionamentos entre tabelas (origem, destino, colunas e cardinalidade).</p>

      <h6 id="pontos-interesse">14.3 Cadastro de Ponto de Interesse</h6>
      <p>Cadastre locais relevantes (bancos, órgãos públicos, escolas) no mapa.</p>
      <ol>
        <li>Abra <strong>Cadastro e Configuração → Cadastro Ponto de Interesse</strong>.</li>
        <li>Clique no mapa para marcar a localização.</li>
        <li>Preencha nome, descrição, tipo e equipamentos associados e salve.</li>
      </ol>

      <h6 id="config-monitoramento">14.4 Configuração de Monitoramento</h6>
      <p>Regras de monitoramento: faixas de velocidade para alertas, horários de operação, tipos de veículo monitorados e áreas de interesse.</p>
    </div>
  </div>

  <!-- 15. Relatórios e Gráficos -->
  <div class="card mb-3 secao-help" id="relatorios">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">15. Relatórios e Gráficos</h5></div>
    <div class="card-body">
      <p>Os menus <strong>Relatórios</strong> e <strong>Gráficos</strong> da barra superior listam os itens liberados para o seu grupo. Os relatórios gerenciais periódicos ficam em <a href="#relatorios-gerenciais">Análise de Dados → Relatórios Gerenciais</a>.</p>

      <h6>15.1 Menu Relatórios</h6>
      <table class="table table-sm table-bordered">
        <thead class="table-light"><tr><th>Relatório</th><th>Conteúdo</th></tr></thead>
        <tbody>
          <tr><td colspan="2" class="table-secondary"><strong>Tráfego</strong></td></tr>
          <tr><td>Quantidade de Passagens</td><td>Volume de passagens por período, equipamento ou faixa horária.</td></tr>
          <tr><td>Relatório de Fluxo de Passagens Veiculares</td><td>Fluxo de passagens ao longo do período.</td></tr>
          <tr><td>Relatório Fluxo Mensal por Classificação</td><td>Fluxo mensal separado por classificação do veículo.</td></tr>
          <tr><td>Relatório de Fluxo Veicular por Rota</td><td>Fluxo de veículos nas rotas definidas.</td></tr>
          <tr><td>Relatório de Passagem Veicular por Rota</td><td>Passagens de veículos por rota, com mapa.</td></tr>
          <tr><td>Velocidade Média</td><td>Velocidade média por equipamento e período.</td></tr>
          <tr><td>Distribuição por Faixa de Velocidade</td><td>Quantidade de veículos por faixa de velocidade.</td></tr>
          <tr><td>Distribuição por Porte Veicular</td><td>Distribuição dos veículos por porte.</td></tr>
          <tr><td>Taxa de Ocupação da Via</td><td>Ocupação por via/equipamento ao longo do tempo.</td></tr>
          <tr><td>Relatório Estatístico de Evolução Semanal</td><td>Evolução semanal dos indicadores.</td></tr>
          <tr><td>Relatório de Permanência dos Veículos em Área Monitorada</td><td>Tempo de permanência de veículos nas áreas cadastradas em Mapas.</td></tr>
          <tr><td colspan="2" class="table-secondary"><strong>Alertas e irregularidades</strong></td></tr>
          <tr><td>Relatório Detalhado de Alertas</td><td>Alertas com detalhes da tratativa.</td></tr>
          <tr><td>Relatório Detalhado de Alertas e Irregularidades</td><td>Alertas e irregularidades em um único relatório.</td></tr>
          <tr><td>Relatório Detalhado de Irregularidades</td><td>Irregularidades (ocorrências) registradas.</td></tr>
          <tr><td>Relatório Estatístico de Alertas e Alarmes Gerados</td><td>Estatísticas consolidadas de alertas e alarmes.</td></tr>
          <tr id="rel-acoes-alarmes"><td>Relatório de Ações de Alarmes</td><td>Ações tomadas sobre os alarmes gerados.</td></tr>
          <tr><td>Relatório de Veículos Monitorados por Modelo</td><td>Veículos monitorados agrupados por modelo.</td></tr>
          <tr><td colspan="2" class="table-secondary"><strong>Registro de fatos</strong></td></tr>
          <tr><td>Relatório de Fatos Registrados por Tipo e Intervalo</td><td>Distribuição dos fatos por tipo e período.</td></tr>
          <tr><td>Relatório Estatístico para Principais Tipos de Fato</td><td>Estatística dos tipos de fato mais frequentes.</td></tr>
          <tr><td>Relatório Estatístico por Tipo de Fato Registrado</td><td>Estatística por tipo de fato, com mapa.</td></tr>
          <tr><td>Relatório de Pendências nos Registros de Fato</td><td>Registros de fato com pendências.</td></tr>
          <tr><td colspan="2" class="table-secondary"><strong>Auditoria e operação</strong></td></tr>
          <tr id="rel-auditoria"><td>Relatório de Auditoria</td><td>Operações realizadas no sistema.</td></tr>
          <tr><td>Relatório de Sessão</td><td>Sessões de usuário: login, logout e duração.</td></tr>
          <tr id="rel-pesquisa-veiculos"><td>Relatório de Pesquisas de Veículos Efetuadas</td><td>Pesquisas de veículos feitas pelos operadores.</td></tr>
          <tr id="rel-placas"><td>Relatório de Correção de Placas Veiculares</td><td>Correções de placa feitas, por equipamento e período.</td></tr>
          <tr><td>Relatório Imagens Exportadas</td><td>Imagens de passagens exportadas pelos usuários.</td></tr>
        </tbody>
      </table>

      <h6 id="graficos">15.2 Menu Gráficos</h6>
      <table class="table table-sm table-bordered">
        <thead class="table-light"><tr><th>Gráfico</th><th>Conteúdo</th></tr></thead>
        <tbody>
          <tr><td>Veículos por Período</td><td>Quantidade de veículos ao longo do período.</td></tr>
          <tr><td>Veículos por Porte Veicular</td><td>Distribuição dos veículos por porte.</td></tr>
          <tr><td>Velocidade Média</td><td>Evolução da velocidade média.</td></tr>
          <tr><td>Distribuição Ocorrência por Faixa de Velocidade</td><td>Ocorrências por faixa de velocidade.</td></tr>
          <tr><td>Ocorrência por Porte Veicular</td><td>Ocorrências por porte do veículo.</td></tr>
        </tbody>
      </table>

      <h6>15.3 Como gerar</h6>
      <ol>
        <li>Escolha o item no menu <strong>Relatórios</strong> ou <strong>Gráficos</strong>.</li>
        <li>Defina os filtros (período, equipamento, tipo de veículo etc.).</li>
        <li>Clique em <strong>Gerar</strong> ou <strong>Consultar</strong>.</li>
        <li>Exporte pelos botões disponíveis na tela (Excel, PDF ou impressão).</li>
      </ol>
    </div>
  </div>

  <!-- 16. Telas abertas a partir de outras telas -->
  <div class="card mb-3 secao-help" id="outras-telas">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">16. Telas abertas a partir de outras telas</h5></div>
    <div class="card-body">
      <p>Estas telas não têm card próprio: são abertas a partir de outras telas ou, no caso das ferramentas administrativas, pelo endereço informado pelo administrador.</p>

      <h6 id="calendario-intensidade">16.1 Calendário de Intensidade</h6>
      <p class="caminho">Aberto a partir de Dashboards → Dashboards</p>
      <p>Mapa de calor em formato de calendário com o volume de passagens por dia; cores mais intensas indicam maior volume. Útil para identificar padrões sazonais e dias atípicos.</p>

      <h6 id="linha-tempo">16.2 Linha do Tempo</h6>
      <p class="caminho">Aberta a partir do detalhe do veículo na Consulta de Veículos, do tratamento de alerta e do Perfil Comportamental</p>
      <p>Passagens de um veículo em ordem cronológica, com localização no mapa, para reconstruir o trajeto.</p>

      <h6 id="mapa-passagens">16.3 Mapa de Passagens</h6>
      <p class="caminho">Aberto a partir das Consultas de Veículos, do tratamento de alerta e do Perfil Comportamental</p>
      <p>Trajeto de um veículo no mapa, com marcadores nos pontos de captura.</p>

      <h6 id="video-passagem-tr">16.4 Vídeo da Passagem em Tempo Real</h6>
      <p class="caminho">Aberto a partir de Monitoramento → Veículos em Tempo Real</p>
      <p>Vídeo da passagem do veículo, sincronizado com a leitura do OCR.</p>

      <h6 id="assinatura-digital">16.5 Assinatura Digital</h6>
      <p class="caminho">Aberta a partir do detalhe do veículo nas Consultas de Veículos</p>
      <p>Assinatura digital de imagens de passagens e documentos, garantindo autenticidade e integridade com validação posterior.</p>

      <h6 id="correcao-placas">16.6 Correção de Placas em Lote</h6>
      <p class="caminho">Aberta a partir da Consulta de Veículos e da Revisão de Placas</p>
      <p>Correção em massa de placas com erro de leitura do OCR, com sugestões baseadas em confusões comuns (0/O, 1/I, 8/B). Cada correção exige motivo e justificativa.</p>

      <h6>16.7 Ferramentas sem atalho no menu</h6>
      <p class="small text-muted">Disponíveis pelo endereço indicado, conforme as permissões do seu grupo.</p>
      <table class="table table-sm table-bordered">
        <thead class="table-light"><tr><th>Tela</th><th>Função</th><th>Endereço</th></tr></thead>
        <tbody>
          <tr id="dupla-analise"><td>Dupla Análise</td><td>Validação por dois analistas independentes da mesma imagem de infração; divergências vão para desempate.</td><td><code>/muralha-digital/pages/processamento/dupla-analise/fila.jsp</code></td></tr>
          <tr id="parametros-sistema"><td>Parâmetros do Sistema</td><td>Configurações gerais (integrações, parâmetros de OCR, endereços de serviços).</td><td><code>/muralha-digital/pages/configuracoes/parametros.jsp</code></td></tr>
          <tr id="retencao-dados"><td>Retenção de Dados</td><td>Prazos de guarda de imagens, passagens e logs antes da exclusão automática (LGPD).</td><td><code>/muralha-digital/pages/admin/retencao/index.jsp</code></td></tr>
          <tr id="cpf-matricula"><td>CPF / Matrícula</td><td>Vínculo entre CPF e matrícula funcional dos operadores.</td><td><code>/muralha-digital/pages/admin/cpf-matricula/index.jsp</code></td></tr>
          <tr id="cadastro-datasets"><td>Cadastro de Datasets (versão anterior)</td><td>Consultas SQL usadas pelo Dashboard Builder anterior. Apenas administradores.</td><td><code>/muralha-digital/pages/admin/datasets/index.jsp</code></td></tr>
          <tr id="exportacao-passagens"><td>Exportação de Passagens</td><td>Exportação em massa de dados de passagens (CSV/Excel).</td><td><code>/muralha-digital/pages/relatorios/exportacao-passagens/index.jsp</code></td></tr>
          <tr id="passagens-anonimizadas"><td>Passagens Anonimizadas</td><td>Passagens com placa parcialmente oculta, para uso estatístico (LGPD).</td><td><code>/muralha-digital/pages/consulta/passagens-anonimizadas/index.jsp</code></td></tr>
          <tr id="correlacao"><td>Correlação de Passagens</td><td>Correlação entre passagens de diferentes equipamentos para identificar rotas.</td><td><code>/muralha-digital/pages/correlacao/correlacao.jsp</code></td></tr>
          <tr id="juncao-base"><td>Junção de Base</td><td>Cruzamento de listas externas (CSV, planilhas) com as passagens do sistema.</td><td><code>/muralha-digital/pages/juncao-base/consulta.jsp</code></td></tr>
          <tr id="blitz-eletronica"><td>Blitz Eletrônica</td><td>Veículos irregulares detectados automaticamente pelos equipamentos.</td><td><code>/muralha-digital/pages/blitz-eletronica/veiculos-irregulares.jsp</code></td></tr>
          <tr id="mapa-transito"><td>Mapa de Situação de Trânsito</td><td>Camada de trânsito com dados dos sensores e de fontes externas.</td><td><code>/muralha-digital/pages/mapa-situacao-transito/mapa-situacao-transito.jsp</code></td></tr>
          <tr id="video-semaforo"><td>Vídeo de Passagem em Semáforo</td><td>Vídeos de passagens em semáforo para validação de avanço de sinal.</td><td><code>/muralha-digital/pages/video-passagem-semaforo/video-passagem-semaforo.jsp</code></td></tr>
          <tr id="video-monitoramento"><td>Consulta de Vídeos Gravados</td><td>Busca de gravações por câmera, data e horário.</td><td><code>/muralha-digital/pages/video-monitoramento/consulta.jsp</code></td></tr>
          <tr id="visualizacao-cameras"><td>Visualização de Câmeras</td><td>Câmeras do sistema com status de conectividade.</td><td><code>/muralha-digital/pages/visualizacao-cameras/visualizacao-cameras.jsp</code></td></tr>
          <tr id="log-consulta"><td>Consulta de Log (legado)</td><td>Logs na versão anterior; prefira o <a href="#log-auditoria">Log de Auditoria</a>.</td><td><code>/muralha-digital/pages/consulta/log/consulta_log.jsp</code></td></tr>
        </tbody>
      </table>
    </div>
  </div>

  <!-- 17. Modo Escuro -->
  <div class="card mb-3 secao-help" id="modo-escuro">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">17. Modo Escuro</h5></div>
    <div class="card-body">
      <p>O sistema possui modo claro e escuro.</p>
      <ol>
        <li>Na barra superior, clique no ícone <i class="bi bi-moon-stars"></i> (lua) para ativar o <strong>Modo Escuro</strong>.</li>
        <li>Clique no ícone <i class="bi bi-sun"></i> (sol) para voltar ao <strong>Modo Claro</strong>.</li>
      </ol>
      <p class="mb-0">A preferência fica salva no navegador.</p>
    </div>
  </div>

  <div class="text-center text-muted mb-4">
    <small>Cinturão de Segurança — Muralha Digital &copy; Consilux · Ajuda revisada em 17/10/2026</small>
  </div>
</div>

<script>
function filtrarSections() {
  var termo = document.getElementById('campoBusca').value.toLowerCase();
  var secoes = document.querySelectorAll('.secao-help');
  secoes.forEach(function(s) {
    var texto = s.textContent.toLowerCase();
    s.style.display = texto.includes(termo) ? '' : 'none';
  });
  document.getElementById('sumario').style.display = termo ? 'none' : '';
}
</script>
