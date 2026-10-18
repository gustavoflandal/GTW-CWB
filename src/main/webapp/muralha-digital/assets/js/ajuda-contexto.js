(function() {
  'use strict';
  // Trecho do endereço da tela -> seção da Central de Ajuda (id em pages/ajuda/index.jsp).
  // Vence o trecho mais longo encontrado no endereço.
  var mapa = {
    'dashboard/builder':              'dashboard-builder',
    'dashboard/kpis':                 'dashboard-kpis',
    'dashboards/calendario':          'calendario-intensidade',
    'dashboards/dashboard':           'dashboard-principal',
    'mapa-alertas-ocorrencias':       'mapa-alertas',
    'mapa-situacao-transito':         'mapa-transito',
    'mapa-equipamento':               'mapa-equipamentos',
    'mapa-interativo':                'mapa-interativo',
    'mapa-calor':                     'mapa-calor',
    'mapa-passagens':                 'mapa-passagens',
    'mancha-monitorada':              'mancha-monitorada',
    'monitoramento/sla-latencia':     'sla-latencia',
    'monitoramento/assertividade':    'assertividade',
    'monitoramento/sla-preproc':      'sla-preproc',
    'monitoramento/timelapse':        'timelapse',
    'monitoramento/disponibilidade':  'disponibilidade',
    'monitoramento/mapa':             'mapa-incidentes',
    'monitoramento/integridade':      'integridade',
    'monitoramento/logs-tecnicos':    'logs-tecnicos',
    'monitoramento/pre-processamento':'pre-processamento',
    'MuralhaDigital/PreProcessamento':'pre-processamento',
    'MuralhaDigital/RelatorioGerencial':'relatorios-gerenciais',
    'relatorios/gerencial':           'relatorios-gerenciais',
    'MuralhaDigital/SsoAnalise':      'plataforma-analises',
    'MuralhaDigital/TipoAlertaConfig':'tipos-alerta',
    'tipos-alerta':                   'tipos-alerta',
    'analise/embedding':              'plataforma-analises',
    'processamento/revisao-placas':   'revisao-placas',
    'analises/paineis':               'paineis-analise',
    'analises/widgets':               'paineis-analise',
    'analises/datasets':              'catalogo-datasets',
    'analises/catalogo':              'catalogo-dados',
    'chamados/relatorios':            'chamados-relatorios',
    'chamados/configuracao':          'chamados-configuracao',
    'chamados':                       'chamados',
    'auditoria/consulta-log':         'log-auditoria',
    'consulta/log':                   'log-consulta',
    'consulta/passagens-anonimizadas':'passagens-anonimizadas',
    'consulta-alerta-ocorrencia':     'consulta-alertas',
    'consulta-veiculo-de-carga':      'consulta-carga',
    'consulta-veiculo':               'consulta-veiculo',
    'atendimento-ocorrencias':        'atendimento-ocorrencias',
    'alerta-tratativa':               'tratar-alerta',
    'configuracao-monitoramento':     'config-monitoramento',
    'configuracoes/parametros':       'parametros-sistema',
    'admin/datasets':                 'cadastro-datasets',
    'admin/kpis':                     'config-kpis',
    'admin/cpf-matricula':            'cpf-matricula',
    'admin/retencao':                 'retencao-dados',
    'inteligencia/consolidacao':      'consolidacao',
    'processamento/dupla-analise':    'dupla-analise',
    'processamento/lotes':            'gestao-lotes',
    'correcao-placas-lote':           'correcao-placas',
    'veiculo-tempo-real':             'veiculos-tempo-real',
    'video-passagem-semaforo':        'video-semaforo',
    'video-passagem-tempo-real':      'video-passagem-tr',
    'video-monitoramento':            'video-monitoramento',
    'video-ao-vivo':                  'video-ao-vivo',
    'visualizacao-cameras':           'visualizacao-cameras',
    'veiculos-correlacionados':       'veiculos-correlacionados',
    'perfil-comportamental':          'perfil-comportamental',
    'ponto-interesse':                'pontos-interesse',
    'painel-informacoes':             'painel-informacoes',
    'pesquisa-rapida':                'pesquisa-rapida',
    'blitz-abordagem':                'blitz-abordagem',
    'blitz-eletronica':               'blitz-eletronica',
    'blitz-ostensiva':                'blitz-ostensiva',
    'blitz':                          'blitz-gestao',
    'mosaico':                        'mosaico-videos',
    'monitorado':                     'monitorados',
    'guarnicao':                      'guarnicoes',
    'meu-perfil':                     'mfa',
    'correlacao':                     'correlacao',
    'linha-tempo':                    'linha-tempo',
    'juncao-base':                    'juncao-base',
    'registro_fato':                  'registros-fato',
    'boletim':                        'boletins',
    'assinatura':                     'assinatura-digital',
    // Menu Relatórios: cada relatório aponta para a sua linha na seção 15.1
    'relatorios/quantidade-passagens':                  'rel-quantidade-passagens',
    'relatorios/relatorio-fluxo-passagens-veiculares':  'rel-fluxo-passagens',
    'relatorios/fluxo-mensal-classificacao':            'rel-fluxo-mensal',
    'relatorios/relatorio-fluxo-veicular-rota':         'rel-fluxo-rota',
    'relatorios/relatorio-passagens-rota-leaflet':      'rel-passagem-rota',
    'relatorios/relatorio-pesquisa-rota-leaflet':       'rel-passagem-rota',
    'relatorios/velocidade-media':                      'rel-velocidade-media',
    'relatorios/distribuicao-faixa-velocidade':         'rel-faixa-velocidade',
    'relatorios/distribuicao-porte-veicular':           'rel-porte-veicular',
    'relatorios/taxa-ocupacao-via':                     'rel-ocupacao-via',
    'relatorios/relatorio-evolucao-semanal':            'rel-evolucao-semanal',
    'relatorios/permanencia-veiculo':                   'rel-permanencia',
    'relatorios/alertas-detalhado':                     'rel-alertas-detalhado',
    'relatorios/relatorio-detalhado-alertas':           'rel-alertas-irregularidades',
    'relatorios/ocorrencias-detalhado':                 'rel-irregularidades',
    'relatorios/extrato-alerta-ocorrencia':             'rel-extrato-alertas',
    'relatorios/relatorio-estatistico-alarmes':         'rel-estatistico-alarmes',
    'relatorios/relatorio-veiculos-monitorados-modelo': 'rel-monitorados-modelo',
    'relatorios/relatorio-distribuicao-fatos':          'rel-fatos-tipo-intervalo',
    'relatorios/relatorio-estatistico-fato':            'rel-principais-tipos-fato',
    'relatorios/relatorio-estatistico-tipo-fatos':      'rel-tipo-fato',
    'relatorios/relatorio-pendencias':                  'rel-pendencias-fato',
    'relatorios/sessao-usuario':                        'rel-sessao',
    'relatorios/imagens-exportadas':                    'rel-imagens-exportadas',
    'relatorios/exportacao':          'exportacao-passagens',
    'relatorios/placas-veiculares':   'rel-placas',
    'relatorios/acoes-alarmes':       'rel-acoes-alarmes',
    'relatorios/auditoria':           'rel-auditoria',
    'relatorios/pesquisa-veiculos':   'rel-pesquisa-veiculos',
    // Menu Gráficos: seção 15.2
    'relatorios/grafico-veiculos-porte':        'graf-veiculos-porte',
    'relatorios/grafico-veiculos':              'graf-veiculos-periodo',
    'relatorios/grafico-velocidade-media':      'graf-velocidade-media',
    'relatorios/grafico-ocorrencia-faixa-vel':  'graf-ocorrencia-faixa',
    'relatorios/grafico-ocorrencia-porte-veic': 'graf-ocorrencia-porte',
    'relatorios':                     'relatorios'
  };

  // Tela inicial e categorias (muralha_principal.jsp?menu_info_pai=N): seção da categoria no manual.
  var categorias = {
    '1': 'dashboards', '6': 'consultas', '7': 'mapas', '15': 'analise', '16': 'cadastro',
    '17': 'monitoramento', '29': 'blitz', '49': 'chamados'
  };

  function secaoDaTela(path, search) {
    if (path.indexOf('muralha_principal.jsp') !== -1) {
      var pai = new URLSearchParams(search).get('menu_info_pai');
      return (pai && categorias[pai]) || 'home';
    }
    var keys = Object.keys(mapa);
    keys.sort(function(a, b) { return b.length - a.length; });
    for (var i = 0; i < keys.length; i++) {
      if (path.indexOf(keys[i]) !== -1) return mapa[keys[i]];
    }
    return null;
  }

  window.addEventListener('DOMContentLoaded', function() {
    var link = document.getElementById('btnAjuda');
    if (!link) return;
    if (window.location.pathname.indexOf('/pages/ajuda/') !== -1) return;
    var secao = secaoDaTela(window.location.pathname, window.location.search);
    if (secao) link.href = '/muralha-digital/pages/ajuda/index.jsp#' + secao;
  });
})();
