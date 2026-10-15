// src/main/webapp/muralha-digital/assets/js/chamados/chamados-detalhe.js
(function () {
  var esc = Chamados.esc;
  var protocolo = new URLSearchParams(location.search).get('protocolo') || '';
  var dados = null, defasagem = 0, prioridades = [];

  var ACOES = {
    assumir:       { rotulo: 'Assumir', icone: 'bi-person-check', cor: 'outline-primary' },
    iniciar:       { rotulo: 'Iniciar atendimento', icone: 'bi-play-circle', cor: 'primary' },
    aguardar:      { rotulo: 'Aguardar solicitante', icone: 'bi-hourglass-split', cor: 'outline-warning' },
    responder:     { rotulo: 'Responder', icone: 'bi-reply', cor: 'primary' },
    resolver:      { rotulo: 'Resolver', icone: 'bi-check2-circle', cor: 'success' },
    avaliar:       { rotulo: 'Avaliar e fechar', icone: 'bi-star', cor: 'success' },
    reabrir:       { rotulo: 'Reabrir', icone: 'bi-arrow-counterclockwise', cor: 'outline-danger' },
    cancelar:      { rotulo: 'Cancelar', icone: 'bi-x-circle', cor: 'outline-danger' },
    reclassificar: { rotulo: 'Reclassificar prioridade', icone: 'bi-sort-down', cor: 'outline-secondary' }
  };

  function texto(id, v) { document.getElementById(id).textContent = v == null || v === '' ? '—' : v; }

  async function carregar() {
    var d = await Chamados.get('Chamado', { acao: 'detalhe', protocolo: protocolo });
    if (!d.ok) { if (!dados) location.href = 'index.jsp'; return; }
    dados = d;
    defasagem = new Date(d.servidorAgora) - new Date();
    var c = d.chamado;
    texto('bcProtocolo', c.protocolo);
    texto('tTitulo', c.titulo);
    document.getElementById('tStatus').innerHTML = Chamados.badgeStatus(c.status);
    texto('dProtocolo', c.protocolo);
    document.getElementById('dPrioridade').innerHTML = Chamados.badgePrioridade(c.prioridade, c.corPrioridade) +
      (c.prioridadeSolicitada !== c.prioridade ? ' <span class="text-muted">(solicitada: ' + esc(c.prioridadeSolicitada) + ')</span>' : '');
    texto('dCategoria', c.categoria);
    texto('dEquipamento', c.equipamento);
    texto('dSolicitante', c.solicitante);
    texto('dResponsavel', c.responsavel);
    texto('dAbertura', Chamados.dataHora(c.abertura));
    texto('dDescricao', c.descricao);
    texto('pAtendimento', Chamados.dataHora(c.prazoAtendimento));
    texto('pResolucao', Chamados.dataHora(c.prazoResolucao));
    document.getElementById('sAtendimento').innerHTML = Chamados.badgeSla(c.slaAtendimento);
    document.getElementById('sResolucao').innerHTML = Chamados.badgeSla(c.slaResolucao) +
      (c.pausado && c.status === 'AGUARDANDO_SOLICITANTE' ? ' <span class="badge text-bg-light">relógio pausado</span>' : '');
    if (c.avaliacaoNota || c.fechadoAutomaticamente) {
      document.getElementById('cardAvaliacao').classList.remove('d-none');
      document.getElementById('dAvaliacao').innerHTML = c.avaliacaoNota
        ? '<span class="text-warning">' + '★'.repeat(c.avaliacaoNota) + '☆'.repeat(5 - c.avaliacaoNota) + '</span><p class="mb-0 mt-1">' + esc(c.avaliacaoComentario || '') + '</p>'
        : '<span class="text-muted">Fechado automaticamente — não avaliado.</span>';
    }
    historico(d.historico, d.anexos);
    anexos(d.anexos);
    acoes(d.acoesPermitidas);
  }

  function historico(itens, anx) {
    var ul = document.getElementById('lstHistorico');
    ul.innerHTML = itens.map(function (h) {
      var arquivos = anx.filter(function (a) { return a.idHistorico === h.id; })
        .map(function (a) { return '<a class="me-2" href="' + esc(linkAnexo(a.id)) + '"><i class="bi bi-paperclip"></i>' + esc(a.nome) + '</a>'; }).join('');
      var mudanca = h.statusNovo && h.statusAnterior !== h.statusNovo
        ? Chamados.badgeStatus(h.statusAnterior) + ' → ' + Chamados.badgeStatus(h.statusNovo) : '';
      return '<li class="list-group-item small">' +
        '<div class="d-flex gap-2"><strong>' + esc(h.usuario) + '</strong><span class="text-muted">' + Chamados.dataHora(h.dtEvento) +
        '</span><span class="badge text-bg-light">' + esc(h.tipo) + '</span><span class="ms-auto">' + mudanca + '</span></div>' +
        '<div style="white-space:pre-wrap">' + esc(h.texto || '') + '</div>' + (arquivos ? '<div class="mt-1">' + arquivos + '</div>' : '') +
        '</li>';
    }).join('');
  }

  function linkAnexo(id) { return (window.CTX || '') + '/MuralhaDigital/Chamado?acao=anexo&id=' + encodeURIComponent(id); }

  function anexos(itens) {
    document.getElementById('lstAnexos').innerHTML = itens.length
      ? itens.map(function (a) {
          return '<li class="list-group-item"><a href="' + esc(linkAnexo(a.id)) + '"><i class="bi bi-paperclip me-1"></i>' + esc(a.nome) +
            '</a><div class="text-muted" title="SHA-256">' + esc(a.sha256.substring(0, 16)) + '…</div></li>';
        }).join('')
      : '<li class="list-group-item text-muted">Nenhum anexo.</li>';
  }

  function acoes(lista) {
    var div = document.getElementById('acoes');
    div.innerHTML = '';
    lista.forEach(function (codigo) {
      var a = ACOES[codigo];
      if (!a) return;
      var b = document.createElement('button');
      b.className = 'btn btn-sm btn-' + a.cor;
      b.innerHTML = '<i class="bi ' + a.icone + ' me-1"></i>' + esc(a.rotulo);
      b.addEventListener('click', function () { executar(codigo); });
      div.appendChild(b);
    });
  }

  async function executar(codigo) {
    var fd = new FormData();
    fd.append('acao', codigo);
    fd.append('protocolo', protocolo);
    var r;
    if (codigo === 'avaliar') {
      r = await Swal.fire({
        title: 'Avaliar atendimento',
        html: '<div class="mb-2" id="estrelas">' + [1, 2, 3, 4, 5].map(function (n) {
                return '<input type="radio" class="btn-check" name="nota" id="n' + n + '" value="' + n + '"><label class="btn btn-outline-warning me-1" for="n' + n + '">' + n + ' ★</label>';
              }).join('') + '</div><textarea id="swTexto" class="form-control" rows="3" placeholder="Comentário (opcional)" maxlength="1000"></textarea>',
        showCancelButton: true, confirmButtonText: 'Enviar avaliação',
        preConfirm: function () {
          var n = document.querySelector('input[name=nota]:checked');
          if (!n) { Swal.showValidationMessage('Escolha uma nota de 1 a 5.'); return false; }
          return { nota: n.value, texto: document.getElementById('swTexto').value };
        }
      });
      if (!r.isConfirmed) return;
      fd.append('nota', r.value.nota);
      fd.append('texto', r.value.texto);
    } else if (codigo === 'reclassificar') {
      if (!prioridades.length) prioridades = (await Chamados.get('Chamado', { acao: 'opcoes' })).prioridades || [];
      r = await Swal.fire({
        title: 'Reclassificar prioridade',
        html: '<select id="swPri" class="form-select mb-2">' + prioridades.map(function (p) {
                return '<option value="' + p.id + '">' + esc(p.nome) + '</option>';
              }).join('') + '</select><textarea id="swTexto" class="form-control" rows="3" placeholder="Justificativa (obrigatória)" maxlength="2000"></textarea>',
        showCancelButton: true, confirmButtonText: 'Reclassificar',
        didOpen: function () { document.getElementById('swPri').value = dados.chamado.idPrioridade; },
        preConfirm: function () {
          var t = document.getElementById('swTexto').value.trim();
          if (!t) { Swal.showValidationMessage('Informe a justificativa.'); return false; }
          return { idPrioridade: document.getElementById('swPri').value, texto: t };
        }
      });
      if (!r.isConfirmed) return;
      fd.append('idPrioridade', r.value.idPrioridade);
      fd.append('texto', r.value.texto);
    } else {
      var obrigatorio = codigo !== 'assumir';
      r = await Swal.fire({
        title: ACOES[codigo].rotulo,
        html: '<textarea id="swTexto" class="form-control" rows="4" maxlength="2000" placeholder="' +
              (obrigatorio ? 'Observação (obrigatória)' : 'Observação (opcional)') + '"></textarea>' +
              (codigo === 'responder' ? '<input id="swArquivos" type="file" class="form-control mt-2" multiple accept=".pdf,.png,.jpg,.jpeg">' : ''),
        showCancelButton: true, confirmButtonText: 'Confirmar',
        preConfirm: function () {
          var t = document.getElementById('swTexto').value.trim();
          if (obrigatorio && !t) { Swal.showValidationMessage('Informe a observação.'); return false; }
          var arqs = document.getElementById('swArquivos');
          if (arqs && arqs.files.length > 5) { Swal.showValidationMessage('No máximo 5 arquivos.'); return false; }
          return { texto: t, arquivos: arqs ? Array.from(arqs.files) : [] };
        }
      });
      if (!r.isConfirmed) return;
      fd.append('texto', r.value.texto);
      r.value.arquivos.forEach(function (f) { fd.append('arquivos', f); });
    }
    var d = await Chamados.post('Chamado', fd);
    if (d.ok) {
      Swal.fire({ icon: 'success', title: 'Chamado atualizado', timer: 1500, showConfirmButton: false });
      carregar();
    }
  }

  function contador(prazoIso, idDestino, cumprido) {
    var el = document.getElementById(idDestino);
    if (!prazoIso || cumprido) { el.textContent = ''; return; }
    var ms = new Date(prazoIso) - (new Date().getTime() + defasagem);
    var neg = ms < 0; ms = Math.abs(ms);
    var h = Math.floor(ms / 3600000), m = Math.floor(ms % 3600000 / 60000);
    el.textContent = (neg ? 'Vencido há ' : 'Restam ') + h + 'h ' + m + 'min';
  }

  setInterval(function () {
    if (!dados) return;
    var c = dados.chamado;
    var terminal = c.status === 'FECHADO' || c.status === 'CANCELADO';
    contador(c.prazoAtendimento, 'cAtendimento', terminal || !!c.primeiroAtendimento);
    contador(c.prazoResolucao, 'cResolucao', terminal || !!c.resolucao || c.pausado);
  }, 1000);

  carregar();
  setInterval(carregar, 30000);
})();
