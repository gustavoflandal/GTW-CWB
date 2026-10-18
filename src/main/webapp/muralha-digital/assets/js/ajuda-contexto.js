(function() {
  'use strict';
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
    'relatorios/exportacao':          'exportacao-passagens',
    'relatorios/placas-veiculares':   'rel-placas',
    'relatorios/acoes-alarmes':       'rel-acoes-alarmes',
    'relatorios/auditoria':           'rel-auditoria',
    'relatorios/pesquisa-veiculos':   'rel-pesquisa-veiculos',
    'relatorios':                     'relatorios'
  };

  window.addEventListener('DOMContentLoaded', function() {
    var link = document.getElementById('btnAjuda');
    if (!link) return;
    var path = window.location.pathname;
    var keys = Object.keys(mapa);
    keys.sort(function(a, b) { return b.length - a.length; });
    for (var i = 0; i < keys.length; i++) {
      if (path.indexOf(keys[i]) !== -1) {
        link.href = '/muralha-digital/pages/ajuda/index.jsp#' + mapa[keys[i]];
        return;
      }
    }
  });
})();
