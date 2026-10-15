// src/main/webapp/muralha-digital/assets/js/chamados/chamados-lista.js
(function () {
  var esc = Chamados.esc;
  var opcoes = null, pagina = 1, ultimaVersao = {}, timer = null;

  function filtros() {
    var p = {};
    new FormData(document.getElementById('formFiltros')).forEach(function (v, k) { if (v) p[k] = v; });
    return p;
  }

  function preencher(select, itens, valor, rotulo) {
    itens.forEach(function (i) {
      var o = document.createElement('option');
      o.value = valor(i); o.textContent = rotulo(i);
      select.appendChild(o);
    });
  }

  async function iniciar() {
    var perfil = await Chamados.get('Chamado', { acao: 'perfil' });
    if (!perfil.ok) return;
    var p = perfil.perfis;
    if (p.indexOf('SOLICITANTE') >= 0 || p.indexOf('GESTOR') >= 0) document.getElementById('btnNovo').classList.remove('d-none');
    if (p.indexOf('GESTOR') >= 0 || p.indexOf('EQUIPE') >= 0) document.getElementById('lnkRelatorios').classList.remove('d-none');
    if (p.indexOf('EQUIPE') >= 0) document.getElementById('lnkConfig').classList.remove('d-none');

    opcoes = await Chamados.get('Chamado', { acao: 'opcoes' });
    if (!opcoes.ok) return;
    preencher(document.getElementById('fStatus'), Object.keys(Chamados.ROTULO_STATUS), function (s) { return s; },
      function (s) { return Chamados.ROTULO_STATUS[s][0]; });
    preencher(document.getElementById('fPrioridade'), opcoes.prioridades, function (x) { return x.id; }, function (x) { return x.nome; });
    preencher(document.getElementById('fCategoria'), opcoes.categorias.filter(function (c) { return !c.idPai; }),
      function (x) { return x.id; }, function (x) { return x.nome; });
    preencher(document.getElementById('fEquipamento'), opcoes.equipamentos, function (x) { return x.idLocal; }, function (x) { return x.nome; });
    preencherNovo();
    await carregar();
    timer = setInterval(carregar, 30000);
  }

  async function carregar() {
    var d = await Chamados.get('Chamado', Object.assign(filtros(), { acao: 'listar', pagina: pagina }));
    if (!d.ok) return;
    var tbody = document.getElementById('tblChamados');
    var html = '';
    d.itens.forEach(function (c) {
      var mudou = ultimaVersao[c.protocolo] && ultimaVersao[c.protocolo] !== c.dtAtualizacao;
      ultimaVersao[c.protocolo] = c.dtAtualizacao;
      var url = 'detalhe.jsp?protocolo=' + encodeURIComponent(c.protocolo);
      html += '<tr class="' + (mudou ? 'table-info' : '') + '" style="cursor:pointer" data-url="' + esc(url) + '">' +
        '<td><a href="' + esc(url) + '"><strong>' + esc(c.protocolo) + '</strong></a></td>' +
        '<td>' + esc(c.titulo) + '</td><td class="small">' + esc(c.categoria) + '</td>' +
        '<td class="small">' + esc(c.equipamento || '—') + '</td>' +
        '<td>' + Chamados.badgePrioridade(c.prioridade, c.corPrioridade) + '</td>' +
        '<td>' + Chamados.badgeStatus(c.status) + '</td><td class="small">' + esc(c.responsavel || '—') + '</td>' +
        '<td class="small">' + Chamados.dataHora(c.abertura) + '</td>' +
        '<td>' + Chamados.badgeSla(c.slaAtendimento) + '</td><td>' + Chamados.badgeSla(c.slaResolucao) + '</td></tr>';
    });
    tbody.innerHTML = html || '<tr><td colspan="10" class="text-center text-muted py-4">Nenhum chamado encontrado.</td></tr>';
    tbody.querySelectorAll('tr[data-url]').forEach(function (tr) {
      tr.addEventListener('click', function (e) { if (e.target.tagName !== 'A') location.href = tr.dataset.url; });
    });
    setTimeout(function () { tbody.querySelectorAll('tr.table-info').forEach(function (tr) { tr.classList.remove('table-info'); }); }, 3000);
    document.getElementById('lblTotal').textContent = d.total + ' chamado(s)';
    document.getElementById('lblAtualizado').textContent = 'Atualizado às ' + new Date().toLocaleTimeString('pt-BR');
    paginar(d.total);
  }

  function paginar(total) {
    var paginas = Math.max(1, Math.ceil(total / 50));
    var ul = document.getElementById('paginacao');
    ul.innerHTML = '';
    for (var i = 1; i <= paginas && i <= 20; i++) {
      var li = document.createElement('li');
      li.className = 'page-item' + (i === pagina ? ' active' : '');
      var a = document.createElement('a');
      a.className = 'page-link'; a.href = '#'; a.textContent = i; a.dataset.p = i;
      a.addEventListener('click', function (e) { e.preventDefault(); pagina = Number(this.dataset.p); carregar(); });
      li.appendChild(a); ul.appendChild(li);
    }
  }

  function preencherNovo() {
    var selCat = document.getElementById('nCategoria');
    var selSub = document.getElementById('nSubcategoria');
    var selPri = document.getElementById('nPrioridade');
    preencher(selCat, opcoes.categorias.filter(function (c) { return !c.idPai; }), function (x) { return x.id; }, function (x) { return x.nome; });
    preencher(document.getElementById('nEquipamento'), opcoes.equipamentos, function (x) { return x.idLocal; }, function (x) { return x.nome; });
    preencher(selPri, opcoes.prioridades, function (x) { return x.id; }, function (x) { return x.nome; });
    selPri.value = (opcoes.prioridades.find(function (p) { return p.codigo === 'MEDIA'; }) || opcoes.prioridades[0] || {}).id;

    function mostrarPrazos() {
      var p = opcoes.prioridades.find(function (x) { return String(x.id) === selPri.value; });
      document.getElementById('nPrazos').textContent = p
        ? 'Prazo de atendimento: ' + horas(p.minutosAtendimento) + ' · resolução: ' + horas(p.minutosResolucao) + ' (24x7)'
        : '';
    }
    selPri.addEventListener('change', mostrarPrazos);
    mostrarPrazos();

    selCat.addEventListener('change', function () {
      selSub.innerHTML = '<option value="">Selecione</option>';
      var cat = opcoes.categorias.find(function (c) { return String(c.id) === selCat.value; });
      var filhas = opcoes.categorias.filter(function (c) { return String(c.idPai) === selCat.value; });
      if (cat && filhas.length === 0) filhas = [cat];
      preencher(selSub, filhas, function (x) { return x.id; }, function (x) { return x.nome; });
      var exige = cat && cat.exigeEquipamento;
      document.getElementById('divEquipamento').classList.toggle('d-none', !exige);
      document.getElementById('nEquipamento').required = !!exige;
    });
  }

  function horas(min) { return min % 60 === 0 ? (min / 60) + ' h' : min + ' min'; }

  async function abrir(e) {
    e.preventDefault();
    var form = document.getElementById('formNovo');
    var arquivos = document.getElementById('nArquivos').files;
    if (arquivos.length > 5) { Swal.fire('Atenção', 'Envie no máximo 5 arquivos.', 'warning'); return; }
    var fd = new FormData(form);
    fd.append('acao', 'abrir');
    var btn = document.getElementById('btnSalvarNovo');
    btn.disabled = true;
    try {
      var d = await Chamados.post('Chamado', fd);
      if (!d.ok) return;
      bootstrap.Modal.getInstance(document.getElementById('modalNovo')).hide();
      form.reset();
      await Swal.fire({
        icon: 'success', title: 'Chamado registrado',
        html: '<p class="mb-1">Protocolo: <strong>' + esc(d.protocolo) + '</strong></p>' +
              '<p class="mb-1">Prioridade: ' + esc(d.prioridade) + '</p>' +
              '<p class="mb-1">Prazo previsto para atendimento: ' + Chamados.dataHora(d.prazoAtendimento) + '</p>' +
              '<p class="mb-0">Prazo previsto para resolução: ' + Chamados.dataHora(d.prazoResolucao) + '</p>' +
              '<p class="small text-muted mt-2">Uma confirmação foi enviada para o seu e-mail.</p>'
      });
      carregar();
    } finally {
      btn.disabled = false;
    }
  }

  document.getElementById('formFiltros').addEventListener('submit', function (e) { e.preventDefault(); pagina = 1; carregar(); });
  document.getElementById('formFiltros').addEventListener('reset', function () { setTimeout(function () { pagina = 1; carregar(); }, 0); });
  document.getElementById('btnNovo').addEventListener('click', function () { new bootstrap.Modal(document.getElementById('modalNovo')).show(); });
  document.getElementById('formNovo').addEventListener('submit', abrir);
  document.getElementById('btnCsv').addEventListener('click', function () {
    Chamados.baixar('ChamadoRelatorio', Object.assign(filtros(), { acao: 'exportar', formato: 'csv', origem: 'lista' }));
  });
  document.getElementById('btnXls').addEventListener('click', function () {
    Chamados.baixar('ChamadoRelatorio', Object.assign(filtros(), { acao: 'exportar', formato: 'xlsx', origem: 'lista' }));
  });
  document.getElementById('btnPdf').addEventListener('click', function () {
    Chamados.exportarPdf(Object.assign(filtros(), { origem: 'lista' }));
  });
  iniciar();
})();
