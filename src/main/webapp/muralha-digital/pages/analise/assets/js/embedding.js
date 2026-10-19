/**
 * embedding.js — Embedding de dashboards do Superset no GTW (SUP-9)
 *
 * Carrega o dashboard selecionado em um iframe com guest token JWT.
 * O token é gerado pelo GuestTokenServlet e permite acesso somente
 * leitura ao dashboard sem login no Superset.
 */
(function () {
  'use strict';

  var SUPERSET_BASE = (typeof supersetBaseUrl !== 'undefined' && supersetBaseUrl) ? supersetBaseUrl : '/analise';
  var ctxPath = (typeof urlRoot !== 'undefined') ? urlRoot : '';
  var GUEST_TOKEN_URL = ctxPath + '/MuralhaDigital/AnaliseGuestToken';

  var selectDashboard = document.getElementById('selectDashboard');
  var frameAnalise = document.getElementById('frameAnalise');
  var loadingAnalise = document.getElementById('loadingAnalise');
  var placeholderAnalise = document.getElementById('placeholderAnalise');
  // So existe para quem tem direito a "Analises - Abrir no Superset"
  var btnAbrir = document.getElementById('btnAbrirSuperset');

  function mostrarBotaoAbrir(visivel, href) {
    if (!btnAbrir) return;
    if (href) btnAbrir.href = href;
    btnAbrir.style.display = visivel ? '' : 'none';
  }

  selectDashboard.addEventListener('change', function () {
    var slug = this.value;
    if (!slug) {
      frameAnalise.src = 'about:blank';
      placeholderAnalise.style.display = '';
      loadingAnalise.style.display = 'none';
      mostrarBotaoAbrir(false);
      return;
    }
    carregarDashboard(slug);
  });

  function carregarDashboard(slug) {
    placeholderAnalise.style.display = 'none';
    loadingAnalise.style.display = '';
    mostrarBotaoAbrir(false);

    // standalone=2 oculta menu e título do Superset, mas mantém a barra de filtros nativos
    var url = SUPERSET_BASE + '/superset/dashboard/' + slug + '/?standalone=2';

    frameAnalise.src = url;

    frameAnalise.onload = function () {
      loadingAnalise.style.display = 'none';
      mostrarBotaoAbrir(true, SUPERSET_BASE + '/superset/dashboard/' + slug + '/');
    };
  }
})();
