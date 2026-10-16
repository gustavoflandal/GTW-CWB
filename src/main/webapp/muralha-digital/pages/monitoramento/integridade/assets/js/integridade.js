/* Integridade das imagens recebidas (TR 5.2.1.2 e 5.2.1.3) */
$(document).ready(function () {

    var URL_CONSULTA = '/MuralhaDigital/IntegridadeRecepcao';
    var URL_CONFIG = '/MuralhaDigital/IntegridadeConfig';
    var PERIODO_MAXIMO_DIAS = 92;
    var TAMANHO_PAGINA = 50;

    var STATUS = {
        INTEGRO: { texto: 'Íntegra', classe: 'bg-success', icone: 'bi-check-circle' },
        DIVERGENTE: { texto: 'Divergente', classe: 'bg-danger', icone: 'bi-exclamation-triangle-fill' },
        SEM_CONFERENCIA: { texto: 'Sem conferência', classe: 'bg-secondary', icone: 'bi-dash-circle' },
        ERRO_LEITURA: { texto: 'Erro de leitura', classe: 'bg-warning text-dark', icone: 'bi-x-octagon' }
    };

    var paginaAtual = 1;
    var filtroAtual = null;

    iniciarDatas();
    obterEquipamentosGenerico('selEquipamentos');
    carregarConfiguracao();

    $('#btnPesquisar').on('click', function () { pesquisar(); });
    $('#btnLimpar').on('click', function () {
        iniciarDatas();
        $('#selEquipamentos').selectpicker('deselectAll');
        $('#selStatus').val('');
    });
    $('#btnPaginaAnterior').on('click', function () { listar(paginaAtual - 1); });
    $('#btnPaginaSeguinte').on('click', function () { listar(paginaAtual + 1); });
    $('#btnCsv').on('click', baixarCsv);
    $('#btnResumoXls').on('click', function () { GerarRelatorioXLSX('tblResumo', 'integridade_resumo.xlsx', []); });
    $('#btnResumoPdf').on('click', function () { GerarRelatorioPDF('tblResumo', 'integridade_resumo.pdf', []); });
    $('#btnDetalheXls').on('click', function () { GerarRelatorioXLSX('tblDetalhe', 'integridade_imagens.xlsx', []); });
    $('#btnDetalhePdf').on('click', function () { GerarRelatorioPDF('tblDetalhe', 'integridade_imagens.pdf', []); });
    $('#btnSalvarPadrao').on('click', salvarPadrao);
    $('#selOrigemPadrao').on('change', function () { alternarCampo($('#txtCampoPadrao'), $(this).val()); });
    $('#tblConfig tbody').on('change', '.sel-origem', function () {
        alternarCampo($(this).closest('tr').find('.txt-campo'), $(this).val());
    });
    $('#tblConfig tbody').on('click', '.btn[title="Salvar"]', function () { salvarEquipamento($(this).closest('tr')); });

    // ---------------------------------------------------------------- recepção

    function iniciarDatas() {
        var hoje = moment();
        $('#dtFim').val(hoje.format('YYYY-MM-DD'));
        $('#dtInicio').val(hoje.clone().subtract(6, 'days').format('YYYY-MM-DD'));
    }

    function lerFiltro() {
        var ini = $('#dtInicio').val(), fim = $('#dtFim').val();
        if (!ini || !fim) { aviso('Informe a data início e a data fim.'); return null; }
        var dias = moment(fim).diff(moment(ini), 'days') + 1;
        if (dias < 1) { aviso('A data fim é anterior à data início.'); return null; }
        if (dias > PERIODO_MAXIMO_DIAS) { aviso('O período máximo é de ' + PERIODO_MAXIMO_DIAS + ' dias.'); return null; }
        var locais = $('#selEquipamentos').val() || [];
        return {
            dtInicio: ini,
            dtFim: fim,
            idLocais: locais.filter(function (v) { return v && v !== '0'; }).join(','),
            status: $('#selStatus').val()
        };
    }

    function pesquisar() {
        var filtro = lerFiltro();
        if (!filtro) return;
        filtroAtual = filtro;
        carregarResumo();
        listar(1);
    }

    function carregarResumo() {
        var $tbody = $('#tblResumo tbody');
        $tbody.html('<tr><td colspan="8" class="text-center text-muted">Carregando…</td></tr>');
        $.ajax({
            type: 'GET', url: URL_CONSULTA, dataType: 'json',
            data: $.extend({ acao: 'resumo' }, filtroAtual),
            success: function (d) {
                var t = d.totais;
                $('#totRecebidos').text(numero(t.recebidos));
                $('#totIntegros').text(numero(t.integros));
                $('#totDivergentes').text(numero(t.divergentes));
                $('#totErroLeitura').text(numero(t.erro_leitura));
                $('#totSemConferencia').text(numero(t.sem_conferencia));
                $('#lblUltimaVerificacao').text(t.ultima_verificacao_formatado
                    ? 'Última verificação no período: ' + t.ultima_verificacao_formatado
                    : 'Nenhuma imagem conferida no período.');

                $tbody.empty();
                if (!d.linhas.length) {
                    $tbody.html('<tr><td colspan="8" class="text-center text-muted">Nenhuma imagem conferida no período.</td></tr>');
                    return;
                }
                d.linhas.forEach(function (r) {
                    var $tr = $('<tr>');
                    if (r.divergentes > 0) $tr.addClass('table-danger');
                    $tr.append($('<td>').text(r.dia_formatado));
                    $tr.append($('<td>').text(r.equipamento || ('Local ' + r.id_local)));
                    [r.recebidos, r.integros, r.divergentes, r.erro_leitura, r.sem_conferencia, r.rejeitados]
                        .forEach(function (v) { $tr.append($('<td class="text-end">').text(numero(v))); });
                    $tbody.append($tr);
                });
                if (d.limitado) aviso('O resumo foi limitado; reduza o período ou selecione equipamentos.');
            },
            error: function (jqXHR) { erroRequisicao(jqXHR, $tbody, 8); }
        });
    }

    function listar(pagina) {
        if (!filtroAtual) return;
        var $tbody = $('#tblDetalhe tbody');
        $tbody.html('<tr><td colspan="9" class="text-center text-muted">Carregando…</td></tr>');
        $.ajax({
            type: 'GET', url: URL_CONSULTA, dataType: 'json',
            data: $.extend({ acao: 'listar', pagina: pagina, tamanho: TAMANHO_PAGINA }, filtroAtual),
            success: function (d) {
                paginaAtual = d.pagina;
                var paginas = Math.max(1, Math.ceil(d.total / d.tamanho));
                $('#lblTotalDetalhe').text('(' + numero(d.total) + ')');
                $('#lblPagina').text('Página ' + d.pagina + ' de ' + paginas);
                $('#btnPaginaAnterior').prop('disabled', d.pagina <= 1);
                $('#btnPaginaSeguinte').prop('disabled', d.pagina >= paginas);

                $tbody.empty();
                if (!d.linhas.length) {
                    $tbody.html('<tr><td colspan="9" class="text-center text-muted">Nenhuma imagem encontrada.</td></tr>');
                    return;
                }
                d.linhas.forEach(function (r) {
                    var st = STATUS[r.status_integridade] || { texto: r.status_integridade, classe: 'bg-light text-dark', icone: 'bi-question-circle' };
                    var $badge = $('<span class="badge">').addClass(st.classe)
                        .append($('<i class="bi me-1">').addClass(st.icone)).append(document.createTextNode(st.texto));
                    var $tr = $('<tr>');
                    $tr.append($('<td>').text(r.dt_captura_formatado || ''));
                    $tr.append($('<td>').text(r.dt_recepcao_formatado || ''));
                    $tr.append($('<td>').text(r.equipamento || ('Local ' + r.id_local)));
                    $tr.append($('<td>').text(r.indice_imagem));
                    $tr.append($('<td>').append($badge));
                    $tr.append($('<td>').text(r.origem_hash || ''));
                    $tr.append($('<td>').text(r.motivo || ''));
                    $tr.append($('<td>').text(r.dt_verificacao_formatado || ''));
                    $tr.append($('<td class="hash">').text(r.sha256_calculado || '').attr('title', r.hash_origem ? 'Origem: ' + r.hash_origem : ''));
                    $tbody.append($tr);
                });
            },
            error: function (jqXHR) { erroRequisicao(jqXHR, $tbody, 9); }
        });
    }

    function baixarCsv() {
        var filtro = filtroAtual || lerFiltro();
        if (!filtro) return;
        window.location.href = URL_CONSULTA + '?' + $.param($.extend({ acao: 'exportarCsv' }, filtro));
    }

    // ------------------------------------------------------------ configuração

    function carregarConfiguracao() {
        $.ajax({
            type: 'GET', url: URL_CONFIG, dataType: 'json', data: { acao: 'listar' },
            success: function (d) {
                $('#itemAbaConfig').removeClass('d-none');
                $('#selOrigemPadrao').val(d.origem_padrao);
                $('#txtCampoPadrao').val(d.campo_padrao || '');
                alternarCampo($('#txtCampoPadrao'), d.origem_padrao);
                $('#lblParametros').text('Job a cada 5 minutos, ' + d.lote + ' imagens por execução, passagens das últimas '
                    + d.janela_horas + ' h; espera do hash de origem: ' + d.espera_hash_min + ' min; alerta de divergência: '
                    + (d.alerta_habilitado ? 'ligado' : 'desligado')
                    + '. Ajuste em muralha.config_chave_valor (chaves INTEGRIDADE_*).');
                montarTabelaConfig(d.equipamentos);
            },
            error: function () { /* sem permissão de configuração: a aba fica oculta */ }
        });
    }

    function montarTabelaConfig(lista) {
        var $tbody = $('#tblConfig tbody').empty();
        if (!lista.length) {
            $tbody.html('<tr><td colspan="5" class="text-center text-muted">Nenhum equipamento vigente.</td></tr>');
            return;
        }
        lista.forEach(function (e) {
            var origem = e.ativo ? e.origem_hash : '';
            var $sel = $('<select class="form-select form-select-sm sel-origem">')
                .attr('aria-label', 'Origem do hash de ' + (e.equipamento || e.id_local));
            [['', 'Padrão'], ['PAYLOAD_SOAP', 'PAYLOAD_SOAP'], ['DADO_BRUTO', 'DADO_BRUTO'], ['AUSENTE', 'AUSENTE']]
                .forEach(function (o) { $sel.append($('<option>').val(o[0]).text(o[1])); });
            $sel.val(origem);
            var $campo = $('<input type="text" class="form-control form-control-sm txt-campo" maxlength="100" placeholder="ex.: integridade.sha256">')
                .val(e.ativo ? (e.campo || '') : '').attr('aria-label', 'Campo do dado bruto');
            alternarCampo($campo, origem);
            var $tr = $('<tr>').attr('data-id', e.id_local);
            $tr.append($('<td>').text(e.equipamento || ''));
            $tr.append($('<td>').text(e.serie || ''));
            $tr.append($('<td>').append($sel));
            $tr.append($('<td>').append($campo));
            $tr.append($('<td>').append(
                $('<button type="button" class="btn btn-outline-primary btn-sm" title="Salvar" aria-label="Salvar">')
                    .append('<i class="bi bi-check-lg"></i>')));
            $tbody.append($tr);
        });
    }

    function alternarCampo($campo, origem) {
        var usa = origem === 'DADO_BRUTO';
        $campo.prop('disabled', !usa);
        if (!usa) $campo.val('');
    }

    function salvarPadrao() {
        enviarConfig({ acao: 'salvarPadrao', origem: $('#selOrigemPadrao').val(), campo: $('#txtCampoPadrao').val() },
            'Alterar a origem padrão do hash? Vale para todos os equipamentos sem configuração própria.');
    }

    function salvarEquipamento($tr) {
        var origem = $tr.find('.sel-origem').val();
        var dados = { acao: 'salvarEquipamento', idLocal: $tr.data('id') };
        if (origem === '') {
            $.extend(dados, { origem: 'AUSENTE', ativo: 0 });
        } else {
            $.extend(dados, { origem: origem, campo: $tr.find('.txt-campo').val(), ativo: 1 });
        }
        enviarConfig(dados, 'Salvar a origem do hash de "' + $tr.find('td:first').text() + '"?');
    }

    function enviarConfig(dados, pergunta) {
        Swal.fire({
            title: 'Confirmar', text: pergunta, icon: 'question', showCancelButton: true,
            confirmButtonText: 'Salvar', cancelButtonText: 'Cancelar',
            confirmButtonColor: '#3085d6', cancelButtonColor: '#d33'
        }).then(function (r) {
            if (!r.isConfirmed) return;
            $.ajax({
                type: 'POST', url: URL_CONFIG, dataType: 'json', data: dados,
                success: function (res) {
                    if (res.sucesso) {
                        Swal.fire({ icon: 'success', title: 'Salvo', text: res.mensagem, confirmButtonColor: '#3085d6' });
                        carregarConfiguracao();
                    } else {
                        Swal.fire({ icon: 'warning', title: 'Atenção', text: res.mensagem, confirmButtonColor: '#3085d6' });
                    }
                },
                error: function (jqXHR) {
                    console.error('Erro ao salvar configuração de integridade', jqXHR.status);
                    Swal.fire({ icon: 'error', title: 'Erro', text: 'Não foi possível salvar a configuração.', confirmButtonColor: '#3085d6' });
                }
            });
        });
    }

    // --------------------------------------------------------------- utilitários

    function numero(v) {
        return (v === null || v === undefined) ? '—' : Number(v).toLocaleString('pt-BR');
    }

    function aviso(msg) {
        Swal.fire({ icon: 'warning', title: 'Atenção', text: msg, confirmButtonColor: '#3085d6' });
    }

    function erroRequisicao(jqXHR, $tbody, colunas) {
        console.error('Erro na consulta de integridade', jqXHR.status);
        var msg = jqXHR.status === 400 ? 'Filtro inválido. Confira o período e os equipamentos.'
            : jqXHR.status === 401 ? 'Sessão expirada. Faça login novamente.'
            : 'Erro ao consultar. Tente novamente.';
        $tbody.html('<tr><td colspan="' + colunas + '" class="text-center text-danger"></td></tr>');
        $tbody.find('td').text(msg);
    }
});
