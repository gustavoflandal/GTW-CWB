'use strict';

let infracaoAtual = null;

// ── Indicadores ──────────────────────────────────────────────────────────────

function carregarIndicadores() {
  $.get('/MuralhaDigital/InfracaoAnalise', { acao: 'indicadores' }, function(r) {
    if (!r.ok) return;
    const map = {};
    (r.dados || []).forEach(function(d) { map[d.status] = d.qtde; });
    const labels = {
      AGUARDANDO_ANALISE: 'Aguardando',
      PRIMEIRA_ANALISE:   '1ª Análise',
      DESEMPATE:          'Desempate',
      PRE_APROVADA:       'Pré-aprovada',
      REPROVADA:          'Reprovada'
    };
    const cores = {
      AGUARDANDO_ANALISE: 'secondary',
      PRIMEIRA_ANALISE:   'warning',
      DESEMPATE:          'danger',
      PRE_APROVADA:       'success',
      REPROVADA:          'dark'
    };
    var html = Object.keys(labels).map(function(k) {
      return '<div class="col-auto">' +
        '<div class="card text-center border-' + (cores[k] || 'secondary') + '" style="min-width:110px">' +
        '<div class="card-body py-2">' +
        '<div class="fs-4 fw-bold">' + (map[k] || 0) + '</div>' +
        '<small class="text-muted">' + labels[k] + '</small>' +
        '</div></div></div>';
    }).join('');
    $('#indicadores').html(html);
  });
}

// ── Fila ─────────────────────────────────────────────────────────────────────

function proxima() {
  $('#msgFila').text('Buscando...');
  $.get('/MuralhaDigital/InfracaoAnalise', { acao: 'proximaFila' }, function(r) {
    if (!r.ok) {
      $('#msgFila').text('Erro: ' + (r.erro || 'desconhecido'));
      return;
    }
    if (r.filaVazia) {
      $('#msgFila').text('Não há infrações aguardando análise no momento.');
      return;
    }
    infracaoAtual = r.infracao;
    sessionStorage.setItem('infracaoAtual', JSON.stringify(r.infracao));
    window.location.href = 'analisar.jsp';
  });
}

// ── Tela de análise ───────────────────────────────────────────────────────────

function preencherTela(inf) {
  $('#lblPlaca').text(inf.placa || '(sem placa)');
  $('#lblData').text(inf.data || '');
  $('#lblLocal').text(inf.idLocal || '');
  $('#lblPista').text(inf.pista || '');
  // Endpoint de imagem do projeto legado
  $('#imgInfracao').attr('src',
    '/MuralhaDigital/VeiculoTempoReal?acao=obterImagem&id=' + inf.id);
}

function ajustarFiltro() {
  var b = $('#brilho').val(), c = $('#contraste').val();
  $('#imgInfracao').css('filter', 'brightness(' + b + ') contrast(' + c + ')');
}

function resetarFiltros() {
  $('#brilho').val(1); $('#contraste').val(1);
  $('#imgInfracao').css('filter', '');
}

function salvar() {
  if (!infracaoAtual) { Swal.fire('Erro', 'Nenhuma infração carregada.', 'error'); return; }
  var classif = $('input[name=classif]:checked').val();
  if (!classif) { Swal.fire('Atenção', 'Selecione uma classificação.', 'warning'); return; }
  var justif = $('#justificativa').val().trim();
  if ((classif === 'INVALIDA' || classif === 'DUVIDA') && !justif) {
    Swal.fire('Atenção', 'Justificativa obrigatória para Inválida ou Dúvida.', 'warning');
    return;
  }
  Swal.fire({
    title: 'Confirmar análise?',
    html: 'Classificação: <strong>' + classif + '</strong>' +
          (justif ? '<br><small>' + justif + '</small>' : ''),
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: 'Confirmar',
    cancelButtonText:  'Cancelar'
  }).then(function(result) {
    if (!result.isConfirmed) return;
    $.post('/MuralhaDigital/InfracaoAnalise', {
      idInfracao:    infracaoAtual.id,
      classificacao: classif,
      justificativa: justif
    }, function(r) {
      if (!r.ok) { Swal.fire('Erro', r.erro || 'Erro ao registrar', 'error'); return; }
      sessionStorage.removeItem('infracaoAtual');
      Swal.fire('Registrado!', 'Status atualizado: ' + r.novoStatus, 'success')
        .then(function() { window.location.href = 'fila.jsp'; });
    });
  });
}

// Indicar obrigatoriedade de justificativa ao mudar classificação
$(document).on('change', 'input[name=classif]', function() {
  var v = $(this).val();
  $('#lblJustObrig').toggle(v === 'INVALIDA' || v === 'DUVIDA');
});

// ── Init ─────────────────────────────────────────────────────────────────────

$(document).ready(function() {
  var path = window.location.pathname;
  if (path.includes('fila')) {
    carregarIndicadores();
  }
  if (path.includes('analisar')) {
    var stored = sessionStorage.getItem('infracaoAtual');
    if (stored) {
      infracaoAtual = JSON.parse(stored);
      preencherTela(infracaoAtual);
    } else {
      window.location.href = 'fila.jsp';
    }
  }
});
