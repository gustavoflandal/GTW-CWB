/* Logs técnicos (TR 5.4.7) e histórico de parâmetros (5.4.7.6) */
$(document).ready(function () {

    var URL = '/MuralhaDigital/LogTecnico';
    var TAMANHO_PAGINA = 50;
    var SEVERIDADE = {
        DEBUG: { classe: 'bg-light text-dark', icone: 'bi-bug', cor: '#adb5bd' },
        INFO: { classe: 'bg-info text-dark', icone: 'bi-info-circle', cor: '#0dcaf0' },
        ALERTA: { classe: 'bg-warning text-dark', icone: 'bi-exclamation-triangle-fill', cor: '#ffc107' },
        ERRO: { classe: 'bg-danger', icone: 'bi-x-circle-fill', cor: '#dc3545' },
        CRITICO: { classe: 'bg-dark', icone: 'bi-exclamation-octagon-fill', cor: '#212529' }
    };

    var opcoes = { categorias: [], periodo_max_dias: 31, janela_correlacao_min: 10 };
    var filtroEventos = null, paginaEventos = 1;
    var filtroParametros = null, paginaParametros = 1;
    var modalCorrelacao = null;

    iniciarDatas();
    obterEquipamentosGenerico('evEquipamentos');
    obterEquipamentosGenerico('ltEquipamento');
    obterEquipamentosGenerico('rtEquipamento');
    carregarOpcoes();

    $('#btnEvPesquisar').on('click', function () { pesquisarEventos(); });
    $('#btnEvLimpar').on('click', limparEventos);
    $('#btnEvAnterior').on('click', function () { listarEventos(paginaEventos - 1); });
    $('#btnEvSeguinte').on('click', function () { listarEventos(paginaEventos + 1); });
    $('#btnEvCsv').on('click', baixarCsv);
    $('#btnEvXls').on('click', function () { GerarRelatorioXLSX('tblEventos', 'logs_tecnicos.xlsx', [7]); });
    $('#btnEvPdf').on('click', function () { GerarRelatorioPDF('tblEventos', 'logs_tecnicos.pdf', [7]); });
    $('#tblEventos tbody').on('click', '.btn[title="Correlacionar"]', function () { correlacionar($(this).data('iso')); });
    $('#btnLtGerar').on('click', gerarLinhaTempo);
    $('#linhaTempo').on('click', '.lt-ponto', function () { correlacionar($(this).data('iso')); });
    $('#btnRtGerar').on('click', gerarRelatorio);
    $('#btnRtPdf').on('click', relatorioPdf);
    $('#btnPhPesquisar').on('click', function () { pesquisarParametros(); });
    $('#btnPhAnterior').on('click', function () { listarParametros(paginaParametros - 1); });
    $('#btnPhSeguinte').on('click', function () { listarParametros(paginaParametros + 1); });
    $('#btnPhXls').on('click', function () { GerarRelatorioXLSX('tblParametros', 'historico_parametros.xlsx', []); });
    $('#btnPhPdf').on('click', function () { GerarRelatorioPDF('tblParametros', 'historico_parametros.pdf', []); });

    // ---------------------------------------------------------------- comuns

    function iniciarDatas() {
        var hoje = moment().format('YYYY-MM-DD');
        var semana = moment().subtract(6, 'days').format('YYYY-MM-DD');
        var ontem = moment().subtract(1, 'days').format('YYYY-MM-DD');
        $('#evDtInicio, #phDtInicio, #rtDtInicio').val(semana);
        $('#evDtFim, #phDtFim, #rtDtFim, #ltDtFim').val(hoje);
        $('#ltDtInicio').val(ontem);
    }

    function carregarOpcoes() {
        $.ajax({
            type: 'GET', url: URL, dataType: 'json', data: { acao: 'opcoes' },
            success: function (d) {
                opcoes = d;
                d.categorias.forEach(function (c) { $('#evCategorias').append($('<option>').val(c.valor).text(c.rotulo)); });
                d.severidades.forEach(function (s) { $('#evSeveridades').append($('<option>').val(s).text(s)); });
                d.localidades.forEach(function (l) { $('#evLocalidades').append($('<option>').val(l.id_localidade).text(l.descricao)); });
                $('#evCategorias, #evSeveridades, #evLocalidades').selectpicker('refresh');
                avisarFontes(d.fontes_ausentes);
            },
            error: function (jqXHR) { console.error('Erro ao carregar opções de logs técnicos', jqXHR.status); }
        });
    }

    function avisarFontes(lista) {
        if (lista && lista.length) {
            $('#avisoFontes').removeClass('d-none').text('Fontes indisponíveis neste banco (tabela inexistente): '
                + lista.join(', ') + '. Os demais logs são exibidos normalmente.');
        }
    }

    function periodo(prefixo) {
        var ini = $('#' + prefixo + 'DtInicio').val(), fim = $('#' + prefixo + 'DtFim').val();
        if (!ini || !fim) { aviso('Informe a data início e a data fim.'); return null; }
        var dias = moment(fim).diff(moment(ini), 'days') + 1;
        if (dias < 1) { aviso('A data fim é anterior à data início.'); return null; }
        if (dias > opcoes.periodo_max_dias) { aviso('O período máximo é de ' + opcoes.periodo_max_dias + ' dias.'); return null; }
        return { dtInicio: ini, dtFim: fim };
    }

    function equipamentoUnico(id) {
        var v = $('#' + id).val();
        if (!v || v === '0') { aviso('Selecione um equipamento.'); return null; }
        return v;
    }

    function badgeSeveridade(s) {
        var c = SEVERIDADE[s] || SEVERIDADE.INFO;
        return $('<span class="badge">').addClass(c.classe).append($('<i class="bi me-1">').addClass(c.icone))
            .append(document.createTextNode(s));
    }

    function numero(v) { return (v === null || v === undefined) ? '—' : Number(v).toLocaleString('pt-BR'); }

    function duracao(min) {
        if (min === null || min === undefined) return '—';
        var h = Math.floor(min / 60), m = min % 60;
        return h > 0 ? h + ' h ' + m + ' min' : m + ' min';
    }

    function aviso(msg) { Swal.fire({ icon: 'warning', title: 'Atenção', text: msg, confirmButtonColor: '#3085d6' }); }

    function erro(jqXHR, $tbody, colunas) {
        console.error('Erro na consulta de logs técnicos', jqXHR.status);
        var msg = jqXHR.status === 400 ? 'Filtro inválido ou página muito distante. Confira os filtros.'
            : jqXHR.status === 401 ? 'Sessão expirada. Faça login novamente.' : 'Erro ao consultar. Tente novamente.';
        if ($tbody) {
            $tbody.html('<tr><td colspan="' + colunas + '" class="text-center text-danger"></td></tr>');
            $tbody.find('td').text(msg);
        } else {
            Swal.fire({ icon: 'error', title: 'Erro', text: msg, confirmButtonColor: '#3085d6' });
        }
    }

    function linhaEvento(r, comAcao) {
        var $tr = $('<tr>');
        if (r.severidade === 'ERRO' || r.severidade === 'CRITICO') $tr.addClass('table-danger');
        else if (r.severidade === 'ALERTA') $tr.addClass('table-warning');
        $tr.append($('<td class="text-nowrap">').text(r.data_hora_formatado || ''));
        $tr.append($('<td>').text(r.equipamento || '—'));
        $tr.append($('<td>').text(r.origem));
        $tr.append($('<td>').text(r.categoria_rotulo + (r.transicao ? (r.transicao === 'INICIO' ? ' (início)' : ' (fim)') : '')));
        $tr.append($('<td>').append(badgeSeveridade(r.severidade)));
        $tr.append($('<td>').text(r.descricao || ''));
        if (comAcao !== false) {
            $tr.append($('<td>').text(r.usuario || ''));
            $tr.append($('<td class="no-export">').append(
                $('<button type="button" class="btn btn-outline-secondary btn-sm" title="Correlacionar" aria-label="Correlacionar">')
                    .attr('data-iso', r.data_hora_iso).append('<i class="bi bi-diagram-3"></i>')));
        }
        return $tr;
    }

    // ---------------------------------------------------------------- eventos

    function lerFiltroEventos() {
        var p = periodo('ev');
        if (!p) return null;
        var locais = ($('#evEquipamentos').val() || []).filter(function (v) { return v && v !== '0'; });
        return $.extend(p, {
            idLocais: locais.join(','),
            idLocalidades: ($('#evLocalidades').val() || []).join(','),
            categorias: ($('#evCategorias').val() || []).join(','),
            severidades: ($('#evSeveridades').val() || []).join(',')
        });
    }

    function pesquisarEventos() {
        var f = lerFiltroEventos();
        if (!f) return;
        filtroEventos = f;
        listarEventos(1);
    }

    function limparEventos() {
        iniciarDatas();
        $('#evEquipamentos, #evLocalidades, #evCategorias, #evSeveridades').selectpicker('deselectAll');
    }

    function listarEventos(pagina) {
        if (!filtroEventos) return;
        var $tbody = $('#tblEventos tbody');
        $tbody.html('<tr><td colspan="8" class="text-center text-muted">Carregando…</td></tr>');
        $.ajax({
            type: 'GET', url: URL, dataType: 'json',
            data: $.extend({ acao: 'listar', pagina: pagina, tamanho: TAMANHO_PAGINA }, filtroEventos),
            success: function (d) {
                paginaEventos = d.pagina;
                var maxPaginas = Math.floor(d.profundidade_max / d.tamanho);
                var paginas = Math.max(1, Math.min(Math.ceil(d.total / d.tamanho), maxPaginas));
                $('#lblEvTotal').text('(' + numero(d.total) + ')');
                $('#lblEvPagina').text('Página ' + d.pagina + ' de ' + paginas
                    + (Math.ceil(d.total / d.tamanho) > maxPaginas ? ' (refine os filtros ou use o CSV para ver tudo)' : ''));
                $('#btnEvAnterior').prop('disabled', d.pagina <= 1);
                $('#btnEvSeguinte').prop('disabled', d.pagina >= paginas);
                avisarFontes(d.fontes_ausentes);
                $tbody.empty();
                if (!d.linhas.length) {
                    $tbody.html('<tr><td colspan="8" class="text-center text-muted">Nenhum evento encontrado.</td></tr>');
                    return;
                }
                d.linhas.forEach(function (r) { $tbody.append(linhaEvento(r)); });
            },
            error: function (jqXHR) { erro(jqXHR, $tbody, 8); }
        });
    }

    function baixarCsv() {
        var f = filtroEventos || lerFiltroEventos();
        if (!f) return;
        window.location.href = URL + '?' + $.param($.extend({ acao: 'exportarCsv' }, f));
    }

    // ------------------------------------------------------------ correlação

    function correlacionar(iso) {
        if (!iso) return;
        var $tbody = $('#tblCorrelacao tbody').html('<tr><td colspan="6" class="text-center text-muted">Carregando…</td></tr>');
        $('#lblCorrelacao').text('');
        if (!modalCorrelacao) modalCorrelacao = new bootstrap.Modal(document.getElementById('modalCorrelacao'));
        modalCorrelacao.show();
        $.ajax({
            type: 'GET', url: URL, dataType: 'json', data: { acao: 'correlacionar', dataHora: iso },
            success: function (d) {
                $('#lblCorrelacao').text('Eventos de todos os equipamentos entre ' + d.janela_min + ' minutos antes e depois de '
                    + moment(iso).format('DD/MM/YYYY HH:mm:ss') + (d.limitado ? ' (lista limitada)' : '') + '.');
                $tbody.empty();
                if (!d.eventos.length) {
                    $tbody.html('<tr><td colspan="6" class="text-center text-muted">Nenhum evento na janela.</td></tr>');
                    return;
                }
                d.eventos.forEach(function (r) {
                    var $tr = linhaEvento(r, false);
                    if (r.data_hora_iso === iso) $tr.addClass('fw-bold');
                    $tbody.append($tr);
                });
            },
            error: function (jqXHR) { erro(jqXHR, $tbody, 6); }
        });
    }

    // --------------------------------------------------------- linha do tempo

    function gerarLinhaTempo() {
        var id = equipamentoUnico('ltEquipamento');
        var p = periodo('lt');
        if (!id || !p) return;
        var $alvo = $('#linhaTempo').html('<p class="text-muted text-center mb-0">Carregando…</p>');
        $.ajax({
            type: 'GET', url: URL, dataType: 'json', data: $.extend({ acao: 'linhaTempo', idLocal: id }, p),
            success: function (d) { desenharLinhaTempo(d); preencherPeriodos(d.intervalos); },
            error: function (jqXHR) {
                erro(jqXHR, null);
                $alvo.html('<p class="text-danger text-center mb-0">Não foi possível gerar a linha do tempo.</p>');
            }
        });
    }

    function desenharLinhaTempo(d) {
        var $alvo = $('#linhaTempo').empty();
        if (!d.eventos.length && !d.intervalos.length) {
            $alvo.html('<p class="text-muted text-center mb-0">Nenhum evento no período.</p>');
            return;
        }
        var ini = d.inicio, fim = d.fim, total = fim - ini;
        function pos(t) { return Math.max(0, Math.min(100, (t - ini) / total * 100)); }

        var presentes = {};
        d.eventos.forEach(function (e) { presentes[e.categoria] = true; });
        d.intervalos.forEach(function (i) { presentes[i.categoria] = true; });
        var faixas = opcoes.categorias.filter(function (c) { return presentes[c.valor]; });

        if (d.limitado) $alvo.append($('<p class="small text-warning">').text('Muitos eventos: só os primeiros foram desenhados. Reduza o período.'));
        var $escala = $('<div class="lt-faixa lt-escala">').append($('<div class="lt-rotulo">'));
        var $trilhaEscala = $('<div class="lt-trilha">');
        for (var k = 0; k <= 4; k++) {
            $trilhaEscala.append($('<span class="lt-marca">').css('left', (k * 25) + '%')
                .text(moment(ini + total * k / 4).format('DD/MM HH:mm')));
        }
        $alvo.append($escala.append($trilhaEscala));

        faixas.forEach(function (c) {
            var $faixa = $('<div class="lt-faixa">').append($('<div class="lt-rotulo">').text(c.rotulo));
            var $trilha = $('<div class="lt-trilha">');
            d.intervalos.filter(function (i) { return i.categoria === c.valor; }).forEach(function (i) {
                var f = i.fim === null ? fim : i.fim;
                var esquerda = pos(i.inicio), largura = Math.max(0.4, pos(f) - esquerda);
                $trilha.append($('<span class="lt-barra">').toggleClass('lt-aberta', i.fim === null)
                    .css({ left: esquerda + '%', width: largura + '%' })
                    .attr('title', i.grupo + ': ' + i.inicio_formatado + ' → ' + (i.fim_formatado || 'em aberto')
                        + ' (' + duracao(i.duracao_min) + ')\n' + (i.descricao_inicio || '')));
            });
            d.eventos.filter(function (e) { return e.categoria === c.valor; }).forEach(function (e) {
                var s = SEVERIDADE[e.severidade] || SEVERIDADE.INFO;
                $trilha.append($('<span class="lt-ponto" role="button" tabindex="0">').css({ left: pos(e.data_hora_ms) + '%', background: s.cor })
                    .attr('data-iso', e.data_hora_iso)
                    .attr('title', e.data_hora_formatado + ' [' + e.severidade + '] ' + (e.descricao || '')));
            });
            $alvo.append($faixa.append($trilha));
        });
    }

    function preencherPeriodos(lista) {
        var $tbody = $('#tblPeriodos tbody').empty();
        if (!lista.length) {
            $tbody.html('<tr><td colspan="6" class="text-center text-muted">Nenhum período (início → fim) no intervalo.</td></tr>');
            return;
        }
        lista.forEach(function (i) {
            var $tr = $('<tr>');
            $tr.append($('<td>').text(i.grupo));
            $tr.append($('<td>').text(i.origem));
            $tr.append($('<td class="text-nowrap">').text(i.inicio_formatado));
            $tr.append($('<td class="text-nowrap">').text(i.fim_formatado || 'em aberto'));
            $tr.append($('<td class="text-end">').text(numero(i.duracao_min)));
            $tr.append($('<td>').text((i.descricao_inicio || '') + (i.descricao_fim ? ' → ' + i.descricao_fim : '')));
            $tbody.append($tr);
        });
    }

    // ---------------------------------------------------------- relatório

    var ultimoRelatorio = null;

    function gerarRelatorio() {
        var id = equipamentoUnico('rtEquipamento');
        var p = periodo('rt');
        if (!id || !p) return;
        $('#btnRtPdf').prop('disabled', true);
        $('#relatorio').html('<p class="text-muted text-center">Carregando…</p>');
        $.ajax({
            type: 'GET', url: URL, dataType: 'json', data: $.extend({ acao: 'relatorioEquipamento', idLocal: id }, p),
            success: function (d) { ultimoRelatorio = d; desenharRelatorio(d); $('#btnRtPdf').prop('disabled', false); },
            error: function (jqXHR) {
                erro(jqXHR, null);
                $('#relatorio').html('<p class="text-danger text-center">Não foi possível gerar o relatório.</p>');
            }
        });
    }

    function tabela(id, cabecalho, linhas, vazio) {
        var $t = $('<table class="table table-sm table-hover mb-0">').attr('id', id);
        var $tr = $('<tr>');
        cabecalho.forEach(function (h) { $tr.append($('<th>').text(h)); });
        $t.append($('<thead class="table-light">').append($tr));
        var $tb = $('<tbody>');
        if (!linhas.length) {
            $tb.append($('<tr>').append($('<td class="text-center text-muted">').attr('colspan', cabecalho.length).text(vazio)));
        }
        linhas.forEach(function (l) {
            var $l = $('<tr>');
            l.forEach(function (v) { $l.append($('<td>').text(v === null || v === undefined ? '' : v)); });
            $tb.append($l);
        });
        return $('<div class="table-responsive">').append($t.append($tb));
    }

    function cartao(titulo, icone, $conteudo) {
        return $('<div class="card mb-3">').append($('<div class="card-header">').append($('<i class="bi me-1">').addClass(icone))
            .append(document.createTextNode(titulo))).append($('<div class="card-body p-0">').append($conteudo));
    }

    function desenharRelatorio(d) {
        var $r = $('#relatorio').empty();
        $r.append($('<div class="alert alert-light border">')
            .append($('<strong>').text(d.equipamento || ('Local ' + d.id_local)))
            .append(document.createTextNode(' — período ' + d.periodo + ' — ' + numero(d.total) + ' eventos'
                + (d.limitado ? ' (limitado; reduza o período)' : ''))));
        var sevs = ['DEBUG', 'INFO', 'ALERTA', 'ERRO', 'CRITICO'];
        $r.append(cartao('Eventos por categoria e severidade', 'bi-grid-3x3',
            tabela('rtContagem', ['Categoria'].concat(sevs), d.contagem.map(function (c) {
                return [c.categoria].concat(sevs.map(function (s) { return c[s]; }));
            }), 'Nenhum evento.')));
        $r.append(cartao('Períodos (início → fim)', 'bi-hourglass-split',
            tabela('rtPeriodos', ['Grupo', 'Quantidade', 'Duração no período', 'Em aberto'], d.periodos.map(function (g) {
                return [g.grupo, g.quantidade, duracao(g.duracao_min), g.em_aberto];
            }), 'Nenhum período.')));
        $r.append(cartao('Parâmetros alterados', 'bi-sliders',
            tabela('rtParametros', ['Data/hora', 'Origem', 'Alteração', 'Usuário'], d.parametros.map(function (p) {
                return [p.data_hora_formatado, p.origem, p.descricao, p.usuario];
            }), 'Nenhuma alteração de parâmetro.')));
        $r.append(cartao('Erros e críticos (mais recentes)', 'bi-exclamation-octagon',
            tabela('rtOcorrencias', ['Data/hora', 'Categoria', 'Severidade', 'Descrição'], d.ocorrencias.map(function (o) {
                return [o.data_hora_formatado, o.categoria_rotulo, o.severidade, o.descricao];
            }), 'Nenhum erro ou crítico.')));
    }

    function relatorioPdf() {
        if (!ultimoRelatorio) return;
        var doc = new jsPDF('l', 'pt');
        var d = ultimoRelatorio;
        doc.setFontSize(14);
        doc.text('Relatório técnico — ' + (d.equipamento || ('Local ' + d.id_local)), 40, 40);
        doc.setFontSize(10);
        doc.text('Período ' + d.periodo + ' — ' + d.total + ' eventos — gerado em ' + moment().format('DD/MM/YYYY HH:mm'), 40, 58);
        var y = 70;
        [['Eventos por categoria e severidade', 'rtContagem'], ['Períodos (início → fim)', 'rtPeriodos'],
         ['Parâmetros alterados', 'rtParametros'], ['Erros e críticos (mais recentes)', 'rtOcorrencias']].forEach(function (t) {
            doc.setFontSize(11);
            doc.text(t[0], 40, y + 14);
            doc.autoTable({ html: '#' + t[1], startY: y + 20, styles: { fontSize: 8 } });
            y = doc.lastAutoTable.finalY + 10;
        });
        doc.save('relatorio_tecnico_' + d.id_local + '.pdf');
    }

    // ------------------------------------------------- histórico de parâmetros

    function pesquisarParametros() {
        var p = periodo('ph');
        if (!p) return;
        filtroParametros = $.extend(p, { tipoRecurso: $('#phTipo').val(), idRecurso: $('#phIdRecurso').val(), parametro: $('#phParametro').val() });
        listarParametros(1);
    }

    function listarParametros(pagina) {
        if (!filtroParametros) return;
        var $tbody = $('#tblParametros tbody');
        $tbody.html('<tr><td colspan="9" class="text-center text-muted">Carregando…</td></tr>');
        $.ajax({
            type: 'GET', url: URL, dataType: 'json',
            data: $.extend({ acao: 'historicoParametros', pagina: pagina, tamanho: TAMANHO_PAGINA }, filtroParametros),
            success: function (d) {
                if (d.aviso) { $tbody.html('<tr><td colspan="9" class="text-center text-warning"></td></tr>'); $tbody.find('td').text(d.aviso); return; }
                paginaParametros = d.pagina;
                var paginas = Math.max(1, Math.ceil(d.total / d.tamanho));
                $('#lblPhTotal').text('(' + numero(d.total) + ')');
                $('#lblPhPagina').text('Página ' + d.pagina + ' de ' + paginas);
                $('#btnPhAnterior').prop('disabled', d.pagina <= 1);
                $('#btnPhSeguinte').prop('disabled', d.pagina >= paginas);
                $tbody.empty();
                if (!d.linhas.length) {
                    $tbody.html('<tr><td colspan="9" class="text-center text-muted">Nenhuma alteração encontrada.</td></tr>');
                    return;
                }
                d.linhas.forEach(function (r) {
                    var $tr = $('<tr>');
                    [r.data_hora_formatado, r.tipo_recurso, r.id_recurso, r.parametro].forEach(function (v) { $tr.append($('<td>').text(v || '')); });
                    $tr.append($('<td class="valor">').text(r.valor_anterior === null ? '(vazio)' : r.valor_anterior));
                    $tr.append($('<td class="valor">').text(r.valor_novo === null ? '(vazio)' : r.valor_novo));
                    [r.usuario, r.ip, r.origem].forEach(function (v) { $tr.append($('<td>').text(v || '')); });
                    $tbody.append($tr);
                });
            },
            error: function (jqXHR) { erro(jqXHR, $tbody, 9); }
        });
    }
});
