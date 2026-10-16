/* Revisão de placas: fila por score de OCR (TR 5.7.4.3, 5.7.5.4), correção com justificativa (5.7.5.2), N placas (5.7.1.4) */
$(document).ready(function () {

    var URL = '/MuralhaDigital/RevisaoPlaca';
    var URL_CONFIG = '/MuralhaDigital/OcrConfig';
    var URL_IMAGEM = '/MuralhaDigital/Veiculo/Imagem?acao=ImagemByIdImg&id=';
    var TAMANHO_PAGINA = 50;
    var MOTIVO = {
        SCORE_BAIXO: 'Score abaixo do limiar', SEM_LEITURA: 'Sem placa lida',
        DIVERGENCIA_CAMPO_CENTRAL: 'Divergência equipamento × central', SCORE_AUSENTE: 'Score não informado',
        MANUAL: 'Intervenção manual'
    };
    var SITUACAO = {
        PENDENTE: '<span class="badge bg-warning text-dark"><i class="bi bi-hourglass-split me-1"></i>Pendente</span>',
        CORRIGIDA: '<span class="badge bg-primary"><i class="bi bi-pen me-1"></i>Corrigida</span>',
        CONFIRMADA: '<span class="badge bg-success"><i class="bi bi-check2-circle me-1"></i>Conferida</span>'
    };
    var INTERVENCAO = { FILA: 'Fila', LOTE: 'Lote', CONSULTA: 'Consulta' };

    var filtroAtual = null, paginaAtual = 1, motivos = [], minimo = 10, idVeiculoAtual = null, modal = null;

    iniciarDatas();
    obterEquipamentosGenerico('fEquipamentos');
    carregarMotivos();
    carregarOperadores();
    carregarConfiguracao();

    $('#btnPesquisar').on('click', pesquisar);
    $('#btnLimpar').on('click', function () {
        iniciarDatas();
        $('#fEquipamentos').selectpicker('deselectAll');
        $('#fSituacao').val('PENDENTE');
        $('#fMotivo, #fIntervencao, #fOperador, #fScoreMin, #fScoreMax').val('');
    });
    $('#btnAnterior').on('click', function () { listar(paginaAtual - 1); });
    $('#btnSeguinte').on('click', function () { listar(paginaAtual + 1); });
    $('#btnXls').on('click', function () { GerarRelatorioXLSX('tblFila', 'revisao_placas.xlsx', [9]); });
    $('#btnPdf').on('click', function () { GerarRelatorioPDF('tblFila', 'revisao_placas.pdf', [9]); });
    $('#tblFila tbody').on('click', '.btn[title="Revisar"]', function () { abrir($(this).closest('tr').data('id')); });
    $('#rvAdicionar').on('click', function () { adicionarLinha('REBOQUE', ''); });
    $('#rvAdicionais').on('click', '.btn[title="Remover"]', function () { $(this).closest('.rv-adicional').remove(); });
    $('#rvMotivoCorrecao').on('change', obrigatoriedade);
    $('#rvSalvar').on('click', function () { salvar(false); });
    $('#rvConferir').on('click', function () { salvar(true); });
    $('#btnSalvarPadrao').on('click', salvarPadrao);
    $('#cOrigem').on('change', function () { alternarCampo($('#cCampo'), $(this).val()); });
    $('#tblConfig tbody').on('change', '.sel-origem', function () { alternarCampo($(this).closest('tr').find('.txt-campo'), $(this).val()); });
    $('#tblConfig tbody').on('click', '.btn[title="Salvar"]', function () { salvarEquipamento($(this).closest('tr')); });

    // ---------------------------------------------------------------- utilitários

    function iniciarDatas() {
        $('#fDtFim').val(moment().format('YYYY-MM-DD'));
        $('#fDtInicio').val(moment().subtract(6, 'days').format('YYYY-MM-DD'));
    }

    function aviso(msg, icone) { Swal.fire({ icon: icone || 'warning', title: icone === 'success' ? 'Pronto' : 'Atenção', text: msg, confirmButtonColor: '#3085d6' }); }

    function numero(v) { return v === null || v === undefined ? '—' : Number(v).toLocaleString('pt-BR'); }

    function erro(jqXHR, $tbody, colunas) {
        console.error('Erro na revisão de placas', jqXHR.status);
        var msg = jqXHR.status === 400 ? 'Filtro inválido. Confira o período e os valores.'
            : jqXHR.status === 401 ? 'Sessão expirada. Faça login novamente.' : 'Erro ao consultar. Tente novamente.';
        if ($tbody) { $tbody.html('<tr><td colspan="' + colunas + '" class="text-center text-danger"></td></tr>'); $tbody.find('td').text(msg); }
        else aviso(msg, 'error');
    }

    // ---------------------------------------------------------------- fila

    function carregarMotivos() {
        $.getJSON(URL, { acao: 'motivos' }, function (d) {
            motivos = d.motivos;
            minimo = d.justificativa_min;
            if (!d.estrutura_instalada) {
                $('#avisoEstrutura').removeClass('d-none').text('A fila de revisão depende da migração 20261016_ocr_score_revisao_placas.sql, ainda não executada neste banco.');
            }
        }).fail(function (jqXHR) { console.error('Erro ao carregar motivos', jqXHR.status); });
    }

    function carregarOperadores() {
        $.getJSON(URL, { acao: 'operadores' }, function (d) {
            (d.operadores || []).forEach(function (o) { $('#fOperador').append($('<option>').val(o.id).text(o.usuario)); });
        });
    }

    function pesquisar() {
        var ini = $('#fDtInicio').val(), fim = $('#fDtFim').val();
        if (!ini || !fim) { aviso('Informe a data início e a data fim.'); return; }
        var dias = moment(fim).diff(moment(ini), 'days') + 1;
        if (dias < 1) { aviso('A data fim é anterior à data início.'); return; }
        if (dias > 92) { aviso('O período máximo é de 92 dias.'); return; }
        filtroAtual = {
            dtInicio: ini, dtFim: fim,
            idLocais: ($('#fEquipamentos').val() || []).filter(function (v) { return v && v !== '0'; }).join(','),
            situacao: $('#fSituacao').val(), motivo: $('#fMotivo').val(), intervencao: $('#fIntervencao').val(),
            idOperador: $('#fOperador').val(), scoreMin: $('#fScoreMin').val(), scoreMax: $('#fScoreMax').val()
        };
        listar(1);
    }

    function listar(pagina) {
        if (!filtroAtual) return;
        var $tbody = $('#tblFila tbody').html('<tr><td colspan="10" class="text-center text-muted">Carregando…</td></tr>');
        $.ajax({
            type: 'GET', url: URL, dataType: 'json', data: $.extend({ acao: 'listar', pagina: pagina, tamanho: TAMANHO_PAGINA }, filtroAtual),
            success: function (d) {
                if (d.aviso) { $tbody.html('<tr><td colspan="10" class="text-center text-warning"></td></tr>'); $tbody.find('td').text(d.aviso); return; }
                paginaAtual = d.pagina;
                var paginas = Math.max(1, Math.ceil(d.total / d.tamanho));
                $('#lblTotal').text('(' + numero(d.total) + ')');
                $('#lblPagina').text('Página ' + d.pagina + ' de ' + paginas);
                $('#btnAnterior').prop('disabled', d.pagina <= 1);
                $('#btnSeguinte').prop('disabled', d.pagina >= paginas);
                $tbody.empty();
                if (!d.linhas.length) { $tbody.html('<tr><td colspan="10" class="text-center text-muted">Nenhuma passagem encontrada.</td></tr>'); return; }
                d.linhas.forEach(function (r) {
                    var placas = (r.placa || '(sem leitura)') + (r.placa_frontal ? ' / F ' + r.placa_frontal : '') + (r.adicionais ? ' +' + r.adicionais : '');
                    var $tr = $('<tr>').attr('data-id', r.id_veiculo);
                    if (r.situacao === 'PENDENTE') $tr.addClass('table-warning');
                    $tr.append($('<td class="text-nowrap">').text(r.dt_passagem_formatado));
                    $tr.append($('<td>').text(r.equipamento || ('Local ' + r.id_local)));
                    $tr.append($('<td class="text-nowrap">').text(placas));
                    $tr.append($('<td class="text-end">').text(numero(r.score_minimo)));
                    $tr.append($('<td class="text-end">').text(numero(r.limiar)));
                    $tr.append($('<td>').text(MOTIVO[r.motivo] || r.motivo).attr('title', r.detalhe || ''));
                    $tr.append($('<td>').html(SITUACAO[r.situacao] || ''));
                    $tr.append($('<td>').text(r.intervencao ? INTERVENCAO[r.intervencao] : '—'));
                    $tr.append($('<td>').text(r.operador || '—'));
                    $tr.append($('<td class="no-export">').append($('<button type="button" class="btn btn-outline-primary btn-sm" title="Revisar" aria-label="Revisar">')
                        .append('<i class="bi bi-search"></i>')));
                    $tbody.append($tr);
                });
            },
            error: function (jqXHR) { erro(jqXHR, $tbody, 10); }
        });
    }

    // ---------------------------------------------------------------- revisão

    function abrir(idVeiculo) {
        idVeiculoAtual = idVeiculo;
        if (!modal) modal = new bootstrap.Modal(document.getElementById('modalRevisao'), { backdrop: 'static' });
        $('#rvImagens').html('<p class="text-muted">Carregando…</p>');
        $('#rvLeituras tbody, #rvHistorico tbody, #rvAdicionais').empty();
        $('#rvJustificativa').val('');
        var $sel = $('#rvMotivoCorrecao').empty().append($('<option value="">').text('-- Selecione --'));
        motivos.forEach(function (m) { $sel.append($('<option>').val(m.id).text(m.descricao)); });
        obrigatoriedade();
        modal.show();
        $.getJSON(URL, { acao: 'passagem', idVeiculo: idVeiculo }, function (d) {
            $('#rvCabecalho').text(d.data_formatado + ' — ' + (d.equipamento || '') + ' — faixa ' + d.faixa + ' — ' + d.velocidade + ' km/h');
            $('#rvPlaca').val(d.placa || '');
            $('#rvFrontal').val(d.placa_frontal || '');
            if (d.revisao) {
                $('#rvMotivo').removeClass('d-none').text((MOTIVO[d.revisao.motivo] || d.revisao.motivo) + (d.revisao.detalhe ? ': ' + d.revisao.detalhe : ''));
            } else {
                $('#rvMotivo').addClass('d-none');
            }
            var $img = $('#rvImagens').empty();
            if (!d.imagens.length) $img.html('<p class="text-muted">Passagem sem imagem.</p>');
            d.imagens.forEach(function (id) { $img.append($('<img alt="Imagem da passagem">').attr('src', URL_IMAGEM + encodeURIComponent(id))); });
            var $lt = $('#rvLeituras tbody');
            if (!d.leituras.length) $lt.html('<tr><td colspan="4" class="text-muted text-center">Sem leituras registradas.</td></tr>');
            d.leituras.forEach(function (l) {
                $lt.append($('<tr>').append($('<td>').text(l.origem), $('<td>').text(l.tipo), $('<td>').text(l.placa || '(sem leitura)'),
                    $('<td class="text-end">').text(numero(l.score))));
                if (l.origem === 'MANUAL' && l.ordem > 2) adicionarLinha(l.tipo, l.placa);
            });
            var $h = $('#rvHistorico tbody');
            if (!d.historico.length) $h.html('<tr><td colspan="4" class="text-muted text-center">Nenhuma correção.</td></tr>');
            d.historico.forEach(function (h) {
                $h.append($('<tr>').append($('<td class="text-nowrap">').text(h.data_formatado),
                    $('<td>').text((h.placa_original || '—') + ' → ' + (h.placa_digitada || '—')),
                    $('<td>').text(h.usuario || ''), $('<td>').text((h.motivo || '') + (h.justificativa ? ': ' + h.justificativa : ''))));
            });
        }).fail(function (jqXHR) { modal.hide(); erro(jqXHR, null); });
    }

    function adicionarLinha(tipo, placa) {
        var $linha = $('<div class="rv-adicional">');
        var $tipo = $('<select class="form-select form-select-sm" aria-label="Tipo da placa adicional">')
            .append($('<option value="REBOQUE">').text('Reboque'), $('<option value="SEMIRREBOQUE">').text('Semirreboque')).val(tipo);
        var $placa = $('<input type="text" class="form-control form-control-sm text-uppercase" maxlength="7" aria-label="Placa adicional">').val(placa || '');
        $linha.append($tipo, $placa, $('<button type="button" class="btn btn-outline-danger btn-sm" title="Remover" aria-label="Remover">').append('<i class="bi bi-trash"></i>'));
        $('#rvAdicionais').append($linha);
    }

    function obrigatoriedade() {
        var m = motivoSelecionado();
        $('#rvObrig').toggleClass('d-none', !(m && m.exige_texto));
    }

    function motivoSelecionado() {
        var id = $('#rvMotivoCorrecao').val();
        return motivos.filter(function (m) { return String(m.id) === id; })[0];
    }

    function salvar(conferir) {
        var m = motivoSelecionado();
        if (conferir && (!m || !m.confirmacao)) {
            var conf = motivos.filter(function (x) { return x.confirmacao; })[0];
            if (conf) { $('#rvMotivoCorrecao').val(conf.id); m = conf; }
        }
        if (!m) { aviso('Selecione o motivo.'); return; }
        var texto = $.trim($('#rvJustificativa').val());
        if (m.exige_texto && texto.length < minimo) { aviso('Descreva a justificativa (mínimo ' + minimo + ' caracteres).'); return; }
        var adicionais = [];
        var invalida = false;
        $('#rvAdicionais .rv-adicional').each(function () {
            var p = $.trim($(this).find('input').val()).toUpperCase();
            if (!p) return;
            if (!/^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$/.test(p)) invalida = true;
            adicionais.push($(this).find('select').val() + ':' + p);
        });
        if (invalida) { aviso('Placa adicional inválida: use AAA9999 ou AAA9A99.'); return; }
        var dados = {
            acao: 'corrigir', idVeiculo: idVeiculoAtual, idMotivo: m.id, justificativa: texto,
            placa: conferir ? '' : $.trim($('#rvPlaca').val()).toUpperCase(),
            placaFrontal: conferir ? '' : $.trim($('#rvFrontal').val()).toUpperCase(),
            adicionais: conferir ? '' : adicionais.join(',')
        };
        Swal.fire({
            title: conferir ? 'Conferir a leitura?' : 'Salvar a correção?', icon: 'question', showCancelButton: true,
            confirmButtonText: 'Confirmar', cancelButtonText: 'Cancelar', confirmButtonColor: '#3085d6', cancelButtonColor: '#d33'
        }).then(function (r) {
            if (!r.isConfirmed) return;
            $.post(URL, dados, function (res) {
                if (!res.sucesso) { aviso(res.mensagem); return; }
                modal.hide();
                aviso(res.mensagem, 'success');
                listar(paginaAtual);
            }, 'json').fail(function (jqXHR) { erro(jqXHR, null); });
        });
    }

    // ---------------------------------------------------------- configuração

    function alternarCampo($campo, origem) {
        var usa = origem === 'DADO_BRUTO';
        $campo.prop('disabled', !usa);
        if (!usa) $campo.val('');
    }

    function carregarConfiguracao() {
        $.ajax({
            type: 'GET', url: URL_CONFIG, dataType: 'json', data: { acao: 'listar' },
            success: function (d) {
                $('#itemAbaConfig').removeClass('d-none');
                $('#cOrigem').val(d.origem_padrao);
                $('#cCampo').val(d.campo_padrao || '');
                alternarCampo($('#cCampo'), d.origem_padrao);
                $('#cLimiar').val(d.limiar);
                $('#lblParametros').text('Escala do score: ' + (d.escala === 1 ? '0-1' : '0-100') + '; sem score vai para revisão: '
                    + (d.revisar_sem_score ? 'sim' : 'não') + '; sem placa lida vai para revisão: ' + (d.revisar_sem_leitura ? 'sim' : 'não') + '; job a cada 5 min, ' + d.lote + ' passagens por execução, últimas '
                    + d.janela_horas + ' h; espera do score: ' + d.espera_score_min + ' min. Ajuste em muralha.config_chave_valor (chaves OCR_*).');
                var $tb = $('#tblConfig tbody').empty();
                d.equipamentos.forEach(function (e) {
                    var $sel = $('<select class="form-select form-select-sm sel-origem" aria-label="Origem do score">');
                    [['', 'Padrão'], ['TABELA_LEITURA', 'TABELA_LEITURA'], ['DADO_BRUTO', 'DADO_BRUTO'], ['AUSENTE', 'AUSENTE']]
                        .forEach(function (o) { $sel.append($('<option>').val(o[0]).text(o[1])); });
                    $sel.val(e.origem_score || '');
                    var $campo = $('<input type="text" class="form-control form-control-sm txt-campo" maxlength="100" aria-label="Campo do dado bruto">').val(e.campo || '');
                    alternarCampo($campo, e.origem_score || '');
                    var $lim = $('<input type="number" class="form-control form-control-sm txt-limiar" min="0" max="100" step="0.1" aria-label="Limiar">')
                        .val(e.limiar === null || e.limiar === undefined ? '' : e.limiar).attr('placeholder', 'padrão');
                    $tb.append($('<tr>').attr('data-id', e.id_local).append($('<td>').text(e.equipamento || ''), $('<td>').text(e.serie || ''),
                        $('<td>').append($sel), $('<td>').append($campo), $('<td>').append($lim),
                        $('<td>').append($('<button type="button" class="btn btn-outline-primary btn-sm" title="Salvar" aria-label="Salvar">').append('<i class="bi bi-check-lg"></i>'))));
                });
            },
            error: function () { /* sem permissão de configuração: aba oculta */ }
        });
    }

    function enviarConfig(dados, pergunta) {
        Swal.fire({ title: 'Confirmar', text: pergunta, icon: 'question', showCancelButton: true, confirmButtonText: 'Salvar',
            cancelButtonText: 'Cancelar', confirmButtonColor: '#3085d6', cancelButtonColor: '#d33' }).then(function (r) {
            if (!r.isConfirmed) return;
            $.post(URL_CONFIG, dados, function (res) {
                aviso(res.mensagem, res.sucesso ? 'success' : 'warning');
                if (res.sucesso) carregarConfiguracao();
            }, 'json').fail(function () { aviso('Não foi possível salvar a configuração.', 'error'); });
        });
    }

    function salvarPadrao() {
        enviarConfig({ acao: 'salvarPadrao', origem: $('#cOrigem').val(), campo: $('#cCampo').val(), limiar: $('#cLimiar').val() },
            'Alterar a origem e o limiar padrão do score de OCR?');
    }

    function salvarEquipamento($tr) {
        enviarConfig({ acao: 'salvarEquipamento', idLocal: $tr.data('id'), origem: $tr.find('.sel-origem').val(),
            campo: $tr.find('.txt-campo').val(), limiar: $tr.find('.txt-limiar').val() },
            'Salvar a configuração de score de "' + $tr.find('td:first').text() + '"?');
    }
});
