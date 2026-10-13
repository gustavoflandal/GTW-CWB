<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

<div class="container py-4" style="max-width: 960px;">

  <div class="d-flex align-items-center mb-4 gap-2">
    <h3 class="mb-0"><i class="bi bi-question-circle me-2"></i>Central de Ajuda</h3>
    <a href="javascript:history.back()" class="btn btn-sm btn-outline-secondary ms-auto">
      <i class="bi bi-arrow-left me-1"></i>Voltar
    </a>
  </div>

  <div class="input-group mb-4">
    <span class="input-group-text"><i class="bi bi-search"></i></span>
    <input type="text" id="campoBusca" class="form-control" placeholder="Buscar no help..." onkeyup="filtrarSections()">
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
            <li><a href="#home">Tela Inicial (Home)</a></li>
            <li><a href="#dashboards">Dashboards</a></li>
            <li><a href="#dashboard-builder">Dashboard Builder</a></li>
            <li><a href="#mapas">Mapas</a></li>
          </ol>
        </div>
        <div class="col-md-4">
          <ol start="7">
            <li><a href="#tempo-real">Tempo Real e Vídeos</a></li>
            <li><a href="#alertas">Alertas e Ocorrências</a></li>
            <li><a href="#consultas">Consultas de Veículos</a></li>
            <li><a href="#blitz">Blitz e Fiscalização</a></li>
            <li><a href="#processamento">Processamento</a></li>
            <li><a href="#registros">Registros e Boletins</a></li>
          </ol>
        </div>
        <div class="col-md-4">
          <ol start="13">
            <li><a href="#cadastro">Cadastro e Configuração</a></li>
            <li><a href="#analise">Análise de Dados</a></li>
            <li><a href="#relatorios">Relatórios</a></li>
            <li><a href="#modo-escuro">Modo Escuro</a></li>
          </ol>
        </div>
      </div>
    </div>
  </div>

  <!-- 1. Visão Geral -->
  <div class="card mb-3 secao-help" id="visao-geral">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">1. Visão Geral do Sistema</h5></div>
    <div class="card-body">
      <p>O <strong>Cinturão de Segurança (Muralha Digital)</strong> é um sistema integrado de monitoramento urbano que utiliza câmeras, sensores e leitores automáticos de placas (OCR) para:</p>
      <ul>
        <li><strong>Monitorar o tráfego</strong> em tempo real, capturando passagens de veículos e gerando alertas automáticos.</li>
        <li><strong>Identificar veículos de interesse</strong> (furto, roubo, mandados judiciais) através de cruzamento com bases oficiais.</li>
        <li><strong>Gerar relatórios estatísticos</strong> de fluxo veicular, velocidade média, distribuição por porte e faixa horária.</li>
        <li><strong>Apoiar operações policiais</strong> com blitz eletrônica, cerco eletrônico e rastreamento de rotas.</li>
        <li><strong>Fiscalizar infrações</strong> de trânsito com processamento de imagens e dupla análise.</li>
      </ul>
      <div class="alert alert-info">
        <i class="bi bi-info-circle me-1"></i>
        A navegação principal está na <strong>barra superior (navbar)</strong>. Os módulos estão organizados em cards na tela Home, agrupados por categoria: Dashboards, Mapas, Análise de Dados e Cadastro e Configuração.
      </div>
    </div>
  </div>

  <!-- 2. Login -->
  <div class="card mb-3 secao-help" id="login">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">2. Login e Autenticação</h5></div>
    <div class="card-body">
      <h6>Acesso ao Sistema</h6>
      <ol>
        <li>Acesse a URL do sistema no navegador.</li>
        <li>Informe seu <strong>login</strong> e <strong>senha</strong>.</li>
        <li>Clique em <strong>"Entrar"</strong>.</li>
      </ol>
      <h6>Tempo de Sessão</h6>
      <p>O tempo restante da sessão é exibido no canto superior direito da navbar. Ao expirar, o sistema redireciona para a tela de login. Marque a opção <strong>"Manter conectado"</strong> para sessões prolongadas.</p>
      <h6>Alterar Senha</h6>
      <p>Clique no ícone <i class="fas fa-user"></i> no canto superior direito e selecione <strong>"Alterar Senha"</strong>.</p>
      <h6 id="mfa">Autenticação Multifator (MFA)</h6>
      <p>Se habilitada, acesse <strong>Meu Perfil → Configurar MFA</strong> para vincular um aplicativo autenticador (Google Authenticator, Authy, etc.). Configure lendo o QR Code exibido na tela com o aplicativo e informe o código gerado para confirmar a vinculação.</p>
    </div>
  </div>

  <!-- 3. Tela Inicial -->
  <div class="card mb-3 secao-help" id="home">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">3. Tela Inicial (Home)</h5></div>
    <div class="card-body">
      <p>A tela Home apresenta os módulos disponíveis em formato de <strong>cards com ícones</strong>, organizados por categoria. Clique em qualquer card para acessar o módulo correspondente.</p>
      <h6>Categorias de Módulos</h6>
      <table class="table table-sm table-bordered">
        <thead class="table-light"><tr><th>Categoria</th><th>Descrição</th></tr></thead>
        <tbody>
          <tr><td><strong>Dashboards</strong></td><td>Painéis de indicadores, calendário de intensidade, KPIs e Dashboard Builder.</td></tr>
          <tr><td><strong>Mapas</strong></td><td>Mapas interativos de equipamentos, calor, alertas, passagens e trânsito.</td></tr>
          <tr><td><strong>Análise de Dados</strong></td><td>SLA de latência, assertividade OCR, pré-processamento 72h, auditoria.</td></tr>
          <tr><td><strong>Cadastro e Configuração</strong></td><td>Configuração de KPIs, gestão de lotes, datasets, monitorados, pontos de interesse.</td></tr>
        </tbody>
      </table>
      <h6 id="pesquisa-rapida">Pesquisa Rápida</h6>
      <p>Na navbar, utilize o campo <strong>"Pesquisa Rápida"</strong> para localizar veículos por placa diretamente de qualquer tela. Digite a placa (parcial ou completa) e pressione Enter.</p>
    </div>
  </div>

  <!-- 4. Dashboards -->
  <div class="card mb-3 secao-help" id="dashboards">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">4. Dashboards</h5></div>
    <div class="card-body">
      <h6 id="dashboard-principal">4.1 Dashboard Principal</h6>
      <p>Exibe indicadores gerais do sistema: total de passagens, alertas ativos, equipamentos online/offline, e gráficos de tendência. Acesse via <strong>Home → Dashboards → Dashboard</strong>.</p>

      <h6 id="calendario-intensidade">4.2 Calendário de Intensidade</h6>
      <p>Heatmap no formato de calendário mostrando o volume de passagens por dia. Cores mais intensas indicam maior volume. Útil para identificar padrões sazonais e dias atípicos.</p>

      <h6 id="dashboard-kpis">4.3 Dashboard de KPIs</h6>
      <p>Indicadores de performance configuráveis. Os KPIs são definidos na tela de <a href="#config-kpis">Configuração de KPIs</a> (módulo Cadastro e Configuração).</p>
      <ol>
        <li>Selecione o período desejado.</li>
        <li>Os indicadores são atualizados automaticamente.</li>
        <li>Passe o mouse sobre os gráficos para ver valores detalhados.</li>
      </ol>
    </div>
  </div>

  <!-- 5. Dashboard Builder -->
  <div class="card mb-3 secao-help" id="dashboard-builder">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">5. Dashboard Builder</h5></div>
    <div class="card-body">
      <p>Ferramenta para criação de <strong>dashboards personalizados</strong> com widgets configuráveis.</p>

      <h6>5.1 Criar um Dashboard</h6>
      <ol>
        <li>Acesse <strong>Dashboards → Dashboard Builder</strong>.</li>
        <li>Clique em <strong>"Novo Dashboard"</strong>.</li>
        <li>Informe o nome e clique em <strong>"Salvar"</strong>.</li>
      </ol>

      <h6>5.2 Adicionar Widgets</h6>
      <p>Clique em <strong>"+ Adicionar Widget"</strong> e selecione o tipo:</p>
      <table class="table table-sm table-bordered">
        <thead class="table-light"><tr><th>Tipo</th><th>Descrição</th><th>Exemplo de Uso</th></tr></thead>
        <tbody>
          <tr><td><i class="bi bi-card-text me-1"></i><strong>Card</strong></td><td>Exibe um valor numérico com título e ícone.</td><td>Total de passagens hoje, equipamentos ativos.</td></tr>
          <tr><td><i class="bi bi-bar-chart me-1"></i><strong>Gráfico</strong></td><td>Gráfico de barras, linhas, pizza ou área.</td><td>Passagens por hora, distribuição por tipo.</td></tr>
          <tr><td><i class="bi bi-table me-1"></i><strong>Tabela</strong></td><td>Tabela dinâmica com dados do dataset.</td><td>Últimas passagens, alertas pendentes.</td></tr>
          <tr><td><i class="bi bi-geo-alt me-1"></i><strong>Mapa</strong></td><td>Mapa com marcadores baseados em coordenadas.</td><td>Localização de equipamentos, pontos de interesse.</td></tr>
        </tbody>
      </table>

      <h6>5.3 Configurar um Widget</h6>
      <ol>
        <li>Selecione o <strong>dataset</strong> (fonte de dados SQL cadastrada previamente em <a href="#cadastro-datasets">Cadastro de Datasets</a>).</li>
        <li>Configure as opções do widget (título, subtítulo, tipo de gráfico, colunas).</li>
        <li>Escolha as colunas do dataset para eixos, rótulos e valores.</li>
        <li>Ajuste o <strong>tamanho</strong> (pequeno, médio, grande, extra-grande).</li>
        <li>Clique em <strong>"Salvar Widget"</strong>.</li>
      </ol>

      <h6>5.4 Datasets</h6>
      <p>Os widgets consomem dados de <strong>datasets</strong> — consultas SQL cadastradas pelo administrador em <a href="#cadastro-datasets">Cadastro de Datasets</a>.</p>
      <div class="alert alert-warning">
        <i class="bi bi-exclamation-triangle me-1"></i>
        Apenas usuários com permissão de administrador podem cadastrar ou editar datasets.
      </div>

      <h6>5.5 Reorganizar Widgets</h6>
      <p>Arraste e solte os widgets para reorganizar o layout do dashboard. A posição e tamanho são salvos automaticamente.</p>

      <h6>5.6 Gerenciar Dashboards</h6>
      <ul>
        <li><strong>Editar:</strong> Clique no ícone de lápis para renomear ou editar widgets.</li>
        <li><strong>Excluir:</strong> Clique no ícone de lixeira para remover o dashboard e todos os seus widgets.</li>
        <li><strong>Alternar:</strong> Use o seletor para trocar entre dashboards salvos.</li>
      </ul>
    </div>
  </div>

  <!-- 6. Mapas -->
  <div class="card mb-3 secao-help" id="mapas">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">6. Mapas</h5></div>
    <div class="card-body">
      <h6 id="mapa-equipamentos">6.1 Mapa de Equipamentos</h6>
      <p>Exibe a localização de todos os dispositivos (câmeras, sensores OCR) no mapa. Ícones com cor indicam o status: <span class="text-success">verde</span> (online), <span class="text-danger">vermelho</span> (offline), <span class="text-warning">amarelo</span> (intermitente). Clique em um equipamento para ver detalhes e últimas passagens.</p>

      <h6 id="mapa-calor">6.2 Mapa de Calor</h6>
      <p>Visualiza a concentração de passagens veiculares como mapa de calor. Áreas mais quentes indicam maior fluxo. Filtre por período e tipo de veículo para análise específica.</p>

      <h6 id="mapa-interativo">6.3 Mapa Interativo</h6>
      <p>Mapa geral com múltiplas camadas sobrepostas: equipamentos, alertas, ocorrências, pontos de interesse. Ative/desative camadas no painel lateral para personalizar a visualização.</p>

      <h6 id="mapa-alertas">6.4 Mapa de Alertas e Ocorrências</h6>
      <p>Plota alertas e ocorrências georreferenciados no mapa. Clique no marcador para ver detalhes da ocorrência, imagem da passagem e dados do veículo.</p>

      <h6 id="mapa-transito">6.5 Mapa de Situação de Trânsito</h6>
      <p>Mapa com camada de trânsito em tempo real, integrando dados de sensores do sistema e fontes externas para mostrar congestionamentos e fluxo.</p>

      <h6 id="mapa-incidentes">6.6 Mapa de Incidentes Externos</h6>
      <p>Incidentes reportados por fontes externas (Waze, etc.) plotados no mapa do sistema, permitindo correlação com dados internos.</p>

      <h6 id="mancha-monitorada">6.7 Mancha Monitorada</h6>
      <p>Visualização da área de cobertura dos equipamentos, mostrando o perímetro monitorado pelo sistema e possíveis áreas sem cobertura.</p>

      <h6 id="mapa-passagens">6.8 Mapa de Passagens</h6>
      <p>Visualização no mapa das passagens de um veículo específico, mostrando o trajeto percorrido com marcadores nos pontos de captura.</p>
    </div>
  </div>

  <!-- 7. Tempo Real e Vídeos -->
  <div class="card mb-3 secao-help" id="tempo-real">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">7. Tempo Real e Vídeos</h5></div>
    <div class="card-body">
      <h6 id="veiculos-tempo-real">7.1 Veículos em Tempo Real</h6>
      <p>Exibe as passagens de veículos capturadas em tempo real. A tela é atualizada automaticamente conforme novas leituras chegam dos equipamentos. Mostra placa, imagem, equipamento, velocidade e horário.</p>

      <h6 id="video-ao-vivo">7.2 Vídeo ao Vivo</h6>
      <p>Streaming de vídeo ao vivo das câmeras do sistema. Selecione a câmera desejada na lista de equipamentos para iniciar a transmissão.</p>

      <h6 id="video-monitoramento">7.3 Vídeo Monitoramento</h6>
      <p>Consulta e reprodução de vídeos gravados pelos equipamentos. Filtre por câmera, data e horário para localizar gravações específicas.</p>

      <h6 id="video-semaforo">7.4 Vídeo de Passagem em Semáforo</h6>
      <p>Reprodução de vídeos de passagens em semáforo, utilizados para validação de infrações de avanço de sinal.</p>

      <h6 id="video-passagem-tr">7.5 Vídeo de Passagem em Tempo Real</h6>
      <p>Vídeo da passagem do veículo capturada em tempo real, sincronizado com os dados da leitura OCR.</p>

      <h6 id="mosaico-videos">7.6 Mosaico de Vídeos</h6>
      <p>Grade multi-câmera para acompanhamento simultâneo de múltiplos pontos. Configure o layout (2x2, 3x3, 4x4) e selecione as câmeras que deseja visualizar.</p>

      <h6 id="visualizacao-cameras">7.7 Visualização de Câmeras</h6>
      <p>Painel para visualizar e gerenciar as câmeras do sistema, com status de conectividade e informações de configuração.</p>

      <h6 id="painel-informacoes">7.8 Painel de Informações</h6>
      <p>Painel informativo para exibição em telas de monitoramento (TV/Projetor) com dados consolidados em tempo real: contadores, gráficos e alertas recentes.</p>
    </div>
  </div>

  <!-- 8. Alertas e Ocorrências -->
  <div class="card mb-3 secao-help" id="alertas">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">8. Alertas e Ocorrências</h5></div>
    <div class="card-body">
      <h6>8.1 Como funcionam os alertas</h6>
      <p>Quando um veículo monitorado (cadastrado com restrição) é captado por um equipamento, o sistema gera um <strong>alerta automático</strong>. O alerta aparece na navbar (ícone <i class="fas fa-bell text-warning"></i>) e na tela de tratativa.</p>

      <h6 id="tratar-alerta">8.2 Tratar um Alerta</h6>
      <ol>
        <li>Clique no alerta na navbar ou acesse <strong>Consulta de Alertas</strong>.</li>
        <li>Visualize a imagem da passagem e os dados do veículo.</li>
        <li>Preencha o <strong>questionário de tratativa</strong> com as informações relevantes.</li>
        <li>Defina a ação: <em>encaminhar guarniçao, registrar ocorrência, descartar</em>.</li>
        <li>Assine o alerta para finalizar o tratamento.</li>
      </ol>

      <h6 id="atendimento-ocorrencias">8.3 Atendimento de Ocorrências</h6>
      <p>Quando um alerta resulta em ação de campo, uma <strong>ocorrência</strong> é registrada com: local, horário, guarniçao envolvida, relato textual e boletim vinculado. A tela de atendimento permite abrir, editar e finalizar ocorrências.</p>

      <h6 id="consulta-alertas">8.4 Consulta de Alertas e Ocorrências</h6>
      <p>Acesse <strong>Consulta de Alertas/Ocorrências</strong> para filtrar por período, status (tratado, pendente, descartado), usuário responsável e tipo de alerta. Exportação disponível em Excel e PDF.</p>
    </div>
  </div>

  <!-- 9. Consultas de Veículos -->
  <div class="card mb-3 secao-help" id="consultas">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">9. Consultas de Veículos</h5></div>
    <div class="card-body">
      <h6 id="consulta-veiculo">9.1 Consulta por Placa</h6>
      <ol>
        <li>Acesse <strong>Consulta de Veículos</strong> ou use a <a href="#pesquisa-rapida">Pesquisa Rápida</a> na navbar.</li>
        <li>Digite a placa (parcial ou completa).</li>
        <li>O sistema retorna todas as passagens do veículo com: data/hora, equipamento, imagem e velocidade.</li>
        <li>Clique em uma passagem para ver detalhes e imagem ampliada.</li>
      </ol>

      <h6 id="consulta-carga">9.2 Consulta de Veículo de Carga</h6>
      <p>Consulta específica para veículos de carga, com filtros adicionais por porte, eixo e classificação. Permite análise de circulação de veículos pesados nas vias monitoradas.</p>

      <h6 id="passagens-anonimizadas">9.3 Passagens Anonimizadas</h6>
      <p>Consulta de passagens com dados sensíveis anonimizados (placa parcialmente oculta), em conformidade com a LGPD. Utilizado para fins estatísticos sem exposição de dados pessoais.</p>

      <h6 id="veiculos-correlacionados">9.4 Veículos Correlacionados</h6>
      <p>Identifica veículos que trafegam juntos (mesma rota, horários próximos). Útil para investigação de comboios, escolta ou associação entre veículos suspeitos.</p>

      <h6 id="correlacao">9.5 Correlação de Passagens</h6>
      <p>Análise de correlação entre passagens de diferentes equipamentos, permitindo identificar padrões de deslocamento e rotas mais utilizadas.</p>

      <h6 id="linha-tempo">9.6 Linha do Tempo</h6>
      <p>Timeline de todas as passagens de um veículo, mostrando o percurso cronológico com localização no mapa. Permite reconstruir o trajeto completo do veículo.</p>

      <h6 id="perfil-comportamental">9.7 Perfil Comportamental</h6>
      <p>Análise do padrão de deslocamento de um veículo: horários frequentes, rotas habituais, frequência de passagem e anomalias de comportamento.</p>

      <h6 id="juncao-base">9.8 Junção de Base</h6>
      <p>Cruzamento de bases de dados externas (CSV, planilhas) com as passagens do sistema. Permite identificar veículos de interesse a partir de listas externas.</p>

      <h6 id="log-consulta">9.9 Consulta de Log (Legado)</h6>
      <p>Consulta de logs do sistema na versão legada. Para a versão atualizada, utilize o <a href="#log-auditoria">Log de Auditoria</a>.</p>
    </div>
  </div>

  <!-- 10. Blitz e Fiscalização -->
  <div class="card mb-3 secao-help" id="blitz">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">10. Blitz e Fiscalização</h5></div>
    <div class="card-body">
      <h6 id="blitz-gestao">10.1 Gestão de Blitz</h6>
      <p>Crie e gerencie operações de blitz. Uma blitz reúne um conjunto de abordagens realizadas em um local e período específicos.</p>
      <ol>
        <li>Acesse <strong>Blitz → Listagem de Blitz</strong>.</li>
        <li>Clique em <strong>"Nova Blitz"</strong>.</li>
        <li>Informe local, data, horário e guarniçao responsável.</li>
        <li>Durante a operação, registre abordagens a partir da blitz criada.</li>
      </ol>

      <h6 id="blitz-abordagem">10.2 Abordagens</h6>
      <p>Registro detalhado de cada abordagem realizada em uma blitz: placa do veículo, dados do condutor, documentação verificada, resultado (liberado, apreendido, notificado). A listagem permite consultar histórico por CPF e placa.</p>

      <h6 id="blitz-eletronica">10.3 Blitz Eletrônica</h6>
      <p>Detecção automática de veículos irregulares pelos equipamentos (sem licenciamento, com mandado de apreensão, etc.). A tela lista os veículos detectados com as irregularidades encontradas no cruzamento com bases oficiais.</p>

      <h6 id="blitz-ostensiva">10.4 Blitz Ostensiva</h6>
      <p>Controle de operações de fiscalização ostensiva em campo, com suporte a passagens relacionadas dos veículos abordados para análise de histórico.</p>
    </div>
  </div>

  <!-- 11. Processamento -->
  <div class="card mb-3 secao-help" id="processamento">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">11. Processamento</h5></div>
    <div class="card-body">
      <h6 id="gestao-lotes">11.1 Gestão de Lotes</h6>
      <p>Gerencie lotes de processamento de infrações de trânsito. Cada lote contém um conjunto de imagens/capturas para análise.</p>
      <ol>
        <li>Acesse <strong>Cadastro e Configuração → Gestão de Lotes</strong>.</li>
        <li>Crie um novo lote informando período e equipamento.</li>
        <li>Acompanhe o status: <em>Aberto, Em Processamento, Concluído</em>.</li>
        <li>Visualize estatísticas de cada lote: total de imagens, aprovadas, reprovadas.</li>
      </ol>

      <h6 id="dupla-analise">11.2 Dupla Análise</h6>
      <p>Processo de validação onde duas pessoas independentes analisam a mesma imagem de infração para garantir precisão.</p>
      <ol>
        <li>Acesse a <strong>Fila de Dupla Análise</strong> para ver imagens pendentes.</li>
        <li>Para cada imagem, verifique placa, tipo de infração e enquadramento.</li>
        <li>Aprove ou rejeite a imagem com justificativa.</li>
        <li>Divergências entre os dois analistas são encaminhadas para revisão.</li>
      </ol>

      <h6 id="correcao-placas">11.3 Correção de Placas em Lote</h6>
      <p>Correção em massa de placas com erro de leitura OCR. O sistema sugere correções baseadas em padrões conhecidos (ex.: confusão entre 0/O, 1/I, 8/B).</p>
    </div>
  </div>

  <!-- 12. Registros e Boletins -->
  <div class="card mb-3 secao-help" id="registros">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">12. Registros e Boletins</h5></div>
    <div class="card-body">
      <h6 id="registros-fato">12.1 Registro de Fato</h6>
      <p>Registro formal de fatos relevantes observados durante o monitoramento. Existem dois tipos:</p>
      <ul>
        <li><strong>Com Boletim:</strong> Registro vinculado a um Boletim de Ocorrência policial. Inclui abas para endereço, indivíduos envolvidos, veículos, objetos apreendidos e documentos.</li>
        <li><strong>Sem Boletim:</strong> Registro de fato sem vínculo policial, para situações que não geram B.O. mas precisam ser documentadas.</li>
      </ul>
      <p>Ambos possuem filtros de consulta por período, tipo de fato e status.</p>

      <h6 id="boletins">12.2 Boletins de Ocorrência</h6>
      <p>Consulta e cadastro de Boletins de Ocorrência vinculados ao sistema. Cada boletim pode conter múltiplas abas: dados do boletim, indivíduos, veículos, apreensões e documentos anexos.</p>

      <h6 id="assinatura-digital">12.3 Assinatura Digital</h6>
      <p>Assinatura digital de imagens e documentos do sistema. Permite assinar imagens de passagens e documentos gerados, garantindo autenticidade e integridade com validação posterior.</p>
    </div>
  </div>

  <!-- 13. Cadastro e Configuração -->
  <div class="card mb-3 secao-help" id="cadastro">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">13. Cadastro e Configuração</h5></div>
    <div class="card-body">
      <h6 id="monitorados">13.1 Veículos Monitorados</h6>
      <p>Cadastro de placas de interesse para monitoramento. Quando um veículo cadastrado é captado, um alerta é gerado automaticamente.</p>
      <ol>
        <li>Acesse <strong>Cadastro e Configuração → Monitorados</strong>.</li>
        <li>Clique em <strong>"Novo Monitorado"</strong>.</li>
        <li>Informe a placa, motivo (furto, mandado, investigação) e período de validade.</li>
        <li>O veículo passa a ser rastreado por todos os equipamentos do sistema.</li>
      </ol>

      <h6 id="pontos-interesse">13.2 Pontos de Interesse</h6>
      <p>Cadastre locais relevantes (bancos, órgãos públicos, escolas) no mapa do sistema.</p>
      <ol>
        <li>Acesse <strong>Cadastro e Configuração → Ponto de Interesse</strong>.</li>
        <li>Clique no mapa para selecionar a localização.</li>
        <li>Preencha nome, descrição, tipo e equipamentos associados.</li>
        <li>Clique em <strong>"Salvar"</strong>.</li>
      </ol>

      <h6 id="config-kpis">13.3 Configuração de KPIs</h6>
      <p>Defina os indicadores de performance que serão exibidos no <a href="#dashboard-kpis">Dashboard de KPIs</a>. Configure nome, consulta SQL, meta e formato de exibição para cada indicador.</p>

      <h6 id="cadastro-datasets">13.4 Cadastro de Datasets</h6>
      <p>Cadastre consultas SQL que alimentam os widgets do <a href="#dashboard-builder">Dashboard Builder</a>.</p>
      <ol>
        <li>Acesse <strong>Cadastro e Configuração → Cadastro de Datasets</strong>.</li>
        <li>Informe o nome descritivo e a query SQL (somente SELECT).</li>
        <li>Clique em <strong>"Testar"</strong> para visualizar os dados retornados.</li>
        <li>Salve o dataset para disponibilizá-lo no Dashboard Builder.</li>
      </ol>
      <div class="alert alert-warning">
        <i class="bi bi-exclamation-triangle me-1"></i>
        Apenas usuários administradores podem criar e editar datasets.
      </div>

      <h6 id="guarnicoes">13.5 Guarniçoes</h6>
      <p>Gerencie as equipes de campo (guarniçoes) que atendem alertas e ocorrências. Cadastre a composição da guarniçao (membros, viatura), status (ativa, inativa) e área de atuação.</p>

      <h6 id="config-monitoramento">13.6 Configuração de Monitoramento</h6>
      <p>Configure regras de monitoramento: faixas de velocidade para alertas, horários de operação, tipos de veículo a monitorar e áreas de interesse.</p>

      <h6 id="parametros-sistema">13.7 Parâmetros do Sistema</h6>
      <p>Configurações gerais do sistema: tempo de sessão, integração com bases externas (Sinesp, Detran), parâmetros de OCR, URLs de serviços.</p>

      <h6 id="cpf-matricula">13.8 CPF / Matrícula</h6>
      <p>Administração de vínculos entre CPF e matrícula funcional dos operadores do sistema. Utilizado para controle de acesso e auditoria.</p>

      <h6 id="retencao-dados">13.9 Retenção de Dados</h6>
      <p>Configure políticas de retenção: por quanto tempo imagens, passagens e logs são mantidos no sistema antes da exclusão automática, em conformidade com a LGPD.</p>
    </div>
  </div>

  <!-- 14. Análise de Dados -->
  <div class="card mb-3 secao-help" id="analise">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">14. Análise de Dados</h5></div>
    <div class="card-body">
      <h6 id="sla-latencia">14.1 SLA de Latência</h6>
      <p>Monitora o tempo entre a captura da passagem pelo equipamento e a disponibilização no sistema. Exibe indicadores de conformidade com o SLA contratual, com gráficos de evolução e alertas de desvio.</p>

      <h6 id="assertividade">14.2 Painel de Assertividade</h6>
      <p>Taxa de acerto da leitura OCR (reconhecimento automático de placas). Compara leituras automáticas com validações manuais para medir a precisão dos equipamentos.</p>

      <h6 id="sla-preproc">14.3 SLA de Pré-processamento 72h</h6>
      <p>Controla o prazo de 72 horas para pré-processamento das infrações, conforme exigência regulatória. Exibe percentual de conformidade e imagens fora do prazo.</p>

      <h6 id="timelapse">14.4 Time-lapse de Passagens</h6>
      <p>Animação temporal mostrando o fluxo de veículos ao longo do tempo no mapa. Reproduz o tráfego de um período selecionado em velocidade acelerada, permitindo visualizar padrões de fluxo.</p>
      <ol>
        <li>Selecione o período e os equipamentos.</li>
        <li>Clique em <strong>"Reproduzir"</strong> para iniciar a animação.</li>
        <li>Use os controles de velocidade para ajustar o ritmo.</li>
      </ol>

      <h6 id="disponibilidade">14.5 Disponibilidade dos Equipamentos</h6>
      <p>Status de disponibilidade de cada sensor/câmera: tempo online, tempo offline, porcentagem de uptime e histórico detalhado de falhas e reconexões.</p>

      <h6 id="log-auditoria">14.6 Log de Auditoria</h6>
      <p>Registro centralizado de todas as ações dos usuários no sistema: consultas realizadas, alterações de cadastro, tratativas de alertas, login/logout. Filtre por usuário, tipo de ação e período.</p>
    </div>
  </div>

  <!-- 15. Relatórios -->
  <div class="card mb-3 secao-help" id="relatorios">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">15. Relatórios</h5></div>
    <div class="card-body">
      <p>O sistema oferece diversos relatórios acessíveis pelo menu <strong>Relatórios</strong> na navbar. Todos permitem filtro por período e exportação.</p>

      <table class="table table-sm table-bordered">
        <thead class="table-light"><tr><th>Relatório</th><th>Descrição</th></tr></thead>
        <tbody>
          <tr><td><strong>Quantidade de Passagens</strong></td><td>Volume de passagens por período, equipamento ou faixa horária.</td></tr>
          <tr><td><strong>Velocidade Média</strong></td><td>Velocidade média registrada por equipamento e período.</td></tr>
          <tr><td><strong>Distribuição por Porte</strong></td><td>Distribuição de veículos por porte (leve, pesado, moto).</td></tr>
          <tr><td><strong>Distribuição por Faixa de Velocidade</strong></td><td>Quantidade de veículos por faixa de velocidade.</td></tr>
          <tr><td><strong>Fluxo Mensal por Classificação</strong></td><td>Fluxo mensal separado por tipo de veículo.</td></tr>
          <tr><td><strong>Alertas Detalhado</strong></td><td>Relatório analítico de todos os alertas com detalhes de tratativa.</td></tr>
          <tr><td><strong>Ocorrências Detalhado</strong></td><td>Relatório analítico de ocorrências registradas.</td></tr>
          <tr><td><strong>Permanência de Veículo</strong></td><td>Tempo de permanência de veículos em áreas monitoradas.</td></tr>
          <tr><td><strong>Taxa de Ocupação da Via</strong></td><td>Ocupação por via/equipamento ao longo do tempo.</td></tr>
          <tr><td><strong>Evolução Semanal</strong></td><td>Evolução semanal de indicadores do sistema.</td></tr>
          <tr><td><strong>Estatístico de Alarmes</strong></td><td>Estatísticas consolidadas de alarmes gerados.</td></tr>
          <tr><td><strong>Sessão de Usuário</strong></td><td>Auditoria de sessões: login, logout, ações realizadas.</td></tr>
          <tr id="rel-placas"><td><strong>Placas Veiculares</strong></td><td>Relatório de placas veiculares capturadas por equipamento e período.</td></tr>
          <tr id="rel-acoes-alarmes"><td><strong>Ações e Alarmes</strong></td><td>Relatório de ações tomadas sobre alarmes gerados pelo sistema.</td></tr>
          <tr id="rel-auditoria"><td><strong>Auditoria</strong></td><td>Relatório de auditoria das operações realizadas no sistema.</td></tr>
          <tr id="rel-pesquisa-veiculos"><td><strong>Pesquisas de Veículos</strong></td><td>Relatório de pesquisas de veículos realizadas pelos operadores.</td></tr>
          <tr id="exportacao-passagens"><td><strong>Exportação de Passagens</strong></td><td>Exportação em massa de dados de passagens (CSV/Excel).</td></tr>
          <tr><td><strong>Fluxo Veicular por Rota</strong></td><td>Fluxo de veículos por rotas definidas no sistema.</td></tr>
          <tr><td><strong>Acompanhamento</strong></td><td>Relatório de acompanhamento geral das operações.</td></tr>
          <tr><td><strong>Distribuição de Fatos</strong></td><td>Distribuição de registros de fatos por tipo e período.</td></tr>
          <tr><td><strong>Pendências de Registro</strong></td><td>Registros de fato com pendências a serem resolvidas.</td></tr>
        </tbody>
      </table>

      <h6>Como gerar um relatório</h6>
      <ol>
        <li>Selecione o relatório no menu <strong>Relatórios</strong> ou <strong>Gráficos</strong>.</li>
        <li>Defina os filtros: período, equipamento, tipo de veículo.</li>
        <li>Clique em <strong>"Gerar"</strong> ou <strong>"Consultar"</strong>.</li>
        <li>Use os botões <strong>Excel</strong>, <strong>PDF</strong> ou <strong>Imprimir</strong> para exportar.</li>
      </ol>
    </div>
  </div>

  <!-- 16. Modo Escuro -->
  <div class="card mb-3 secao-help" id="modo-escuro">
    <div class="card-header bg-primary text-white"><h5 class="mb-0">16. Modo Escuro</h5></div>
    <div class="card-body">
      <p>O sistema possui modo claro e escuro para melhor conforto visual.</p>
      <h6>Como alternar</h6>
      <ol>
        <li>Na navbar, clique no ícone <i class="bi bi-moon-stars"></i> (lua) para ativar o <strong>Modo Escuro</strong>.</li>
        <li>Clique no ícone <i class="bi bi-sun"></i> (sol) para voltar ao <strong>Modo Claro</strong>.</li>
      </ol>
      <p>A preferência é salva no navegador e mantida entre sessões.</p>
    </div>
  </div>

  <div class="text-center text-muted mb-4">
    <small>Cinturão de Segurança — Muralha Digital &copy; Consilux</small>
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
