/* Tela Tipos de Alerta — criticidade, cor, ícone e tratamentos (TipoAlertaConfigServlet) */
(function ($) {
    'use strict';

    var URL = (typeof urlRoot !== 'undefined' ? urlRoot : '/') + 'MuralhaDigital/TipoAlertaConfig';
    var ICONES_SUGERIDOS = [
        'bi-eye', 'bi-exclamation-triangle', 'bi-exclamation-triangle-fill', 'bi-exclamation-octagon-fill',
        'bi-shield-exclamation', 'bi-shield-lock', 'bi-truck', 'bi-tools', 'bi-bank', 'bi-calendar-x', 'bi-files',
        'bi-question-circle', 'bi-cone-striped', 'bi-person-dash', 'bi-stoplights', 'bi-sign-no-parking',
        'bi-people', 'bi-car-front', 'bi-car-front-fill', 'bi-bell', 'bi-bell-fill', 'bi-camera', 'bi-geo-alt',
        'bi-lightning', 'bi-fire', 'bi-sign-stop', 'bi-signpost-split'
    ];
    var REGEX_ICONE = /^bi-[a-z0-9]+(-[a-z0-9]+)*$/;

    var catalogo = { tratamentos: [], cores: [] };
    var tipos = [];
    var tipoEmEdicao = null;
    var tratamentosCarregados = {};

    function aviso(icone, titulo, texto) {
        return Swal.fire({ icon: icone, title: titulo, text: texto || '', confirmButtonColor: '#3085d6' });
    }

    function mensagemErro(xhr, padrao) {
        if (xhr && xhr.status === 401) return 'Sua sessão expirou. Entre novamente no sistema.';
        if (xhr && xhr.status === 403) return 'Seu perfil não tem permissão para esta configuração.';
        return padrao;
    }

    function iconeValido(icone) {
        return !icone || REGEX_ICONE.test(icone);
    }

    function previa(tipo, cor, icone) {
        var $p = $('<span class="previa-alerta"></span>').addClass(cor ? 'previa-' + cor : 'previa-sem-cor');
        if (icone && REGEX_ICONE.test(icone)) $p.append($('<i></i>').addClass('bi ' + icone));
        $p.append($('<span></span>').text(tipo));
        return $p;
    }

    function rotuloTratamentos(t) {
        if (!t.tratamentos) return $('<span class="text-muted small">Nenhum</span>');
        var texto = t.tratamentos + (t.tratamentos === 1 ? ' ativo' : ' ativos');
        if (t.automaticos) texto += ' · ' + t.automaticos + ' automático' + (t.automaticos === 1 ? '' : 's');
        return $('<span class="small"></span>').text(texto);
    }

    function montarLinha(t) {
        var $tr = $('<tr></tr>').attr('data-id', t.id);

        $tr.append($('<td class="text-center"></td>').text(t.prioridade));

        var $tipo = $('<td></td>').append($('<div class="fw-semibold"></div>').text(t.tipo));
        if (t.descricao) $tipo.append($('<div class="small text-muted"></div>').text(t.descricao));
        if (!t.ativo) $tipo.append($('<span class="badge bg-secondary">Inativo no cadastro</span>'));
        $tr.append($tipo);

        $tr.append($('<td class="col-previa"></td>').append(previa(t.tipo, t.cor, t.icone)));

        var idCritico = 'critico-' + t.id;
        var $switch = $('<div class="form-check form-switch d-flex justify-content-center"></div>')
            .append($('<input class="form-check-input campo-critico" type="checkbox" role="switch">')
                .attr('id', idCritico).prop('checked', !!t.critico))
            .append($('<label class="visually-hidden"></label>').attr('for', idCritico).text('Crítico'));
        $tr.append($('<td></td>').append($switch));

        var $cor = $('<select class="form-select form-select-sm campo-cor"></select>').attr('aria-label', 'Cor de ' + t.tipo);
        $cor.append($('<option value="">Sem cor (padrão azul)</option>'));
        catalogo.cores.forEach(function (c) {
            $cor.append($('<option></option>').val(c.codigo).text(c.rotulo));
        });
        $cor.val(t.cor || '');
        $tr.append($('<td></td>').append($cor));

        var $icone = $('<input type="text" class="form-control form-control-sm campo-icone" list="sugestoesIcone" maxlength="50" placeholder="ex.: bi-eye">')
            .attr('aria-label', 'Ícone de ' + t.tipo).val(t.icone || '');
        $tr.append($('<td></td>').append($icone)
            .append($('<div class="invalid-feedback">Use uma classe do Bootstrap Icons (bi-…).</div>')));

        $tr.append($('<td class="col-tratamentos"></td>').append(rotuloTratamentos(t)));

        var $acoes = $('<td class="text-center text-nowrap"></td>')
            .append($('<button type="button" class="btn btn-primary btn-sm btn-salvar me-1" disabled title="Salvar criticidade, cor e ícone">' +
                '<i class="bi bi-check-lg"></i> Salvar</button>'))
            .append($('<button type="button" class="btn btn-outline-secondary btn-sm btn-tratamentos" title="Configurar tratamentos">' +
                '<i class="bi bi-diagram-3"></i> Tratamentos</button>'));
        $tr.append($acoes);
        return $tr;
    }

    function renderizar() {
        var termo = ($('#buscaTipo').val() || '').trim().toLowerCase();
        var $tbody = $('#tabelaTipos tbody').empty();
        var visiveis = tipos.filter(function (t) { return !termo || t.tipo.toLowerCase().indexOf(termo) >= 0; });
        if (!visiveis.length) {
            $tbody.append('<tr><td colspan="8" class="text-center">Nenhum tipo de alerta encontrado.</td></tr>');
        }
        visiveis.forEach(function (t) { $tbody.append(montarLinha(t)); });
        var criticos = tipos.filter(function (t) { return t.critico; }).length;
        var automaticos = tipos.filter(function (t) { return t.automaticos > 0; }).length;
        $('#resumoTipos').text(tipos.length + ' tipos · ' + criticos + ' críticos · ' + automaticos + ' com tratamento automático');
    }

    function tipoPorId(id) {
        return tipos.filter(function (t) { return t.id === id; })[0];
    }

    function estadoDaLinha($tr) {
        return {
            critico: $tr.find('.campo-critico').is(':checked'),
            cor: $tr.find('.campo-cor').val() || '',
            icone: ($tr.find('.campo-icone').val() || '').trim()
        };
    }

    function aoAlterarLinha() {
        var $tr = $(this).closest('tr');
        var t = tipoPorId($tr.attr('data-id'));
        var e = estadoDaLinha($tr);
        var valido = iconeValido(e.icone);
        $tr.find('.campo-icone').toggleClass('is-invalid', !valido);
        var alterado = e.critico !== !!t.critico || e.cor !== (t.cor || '') || e.icone !== (t.icone || '');
        $tr.toggleClass('alterado', alterado);
        $tr.find('.btn-salvar').prop('disabled', !alterado || !valido);
        $tr.find('.col-previa').empty().append(previa(t.tipo, e.cor, e.icone));
    }

    function salvarLinha() {
        var $tr = $(this).closest('tr');
        var t = tipoPorId($tr.attr('data-id'));
        var e = estadoDaLinha($tr);
        if (!iconeValido(e.icone)) return;
        var $btn = $(this).prop('disabled', true);
        $.post(URL, { acao: 'salvarConfig', idTipo: t.id, critico: e.critico ? '1' : '0', cor: e.cor, icone: e.icone })
            .done(function () {
                t.critico = e.critico; t.cor = e.cor || null; t.icone = e.icone || null;
                $tr.removeClass('alterado');
                Swal.fire({ icon: 'success', title: 'Configuração salva', text: t.tipo, timer: 1600, showConfirmButton: false });
            })
            .fail(function (xhr) {
                $btn.prop('disabled', false);
                aviso('error', 'Não foi possível salvar', mensagemErro(xhr, 'Verifique os valores e tente novamente.'));
            });
    }

    function abrirTratamentos() {
        var t = tipoPorId($(this).closest('tr').attr('data-id'));
        tipoEmEdicao = t;
        $('#modalTratamentosTitulo').text('Tratamentos — ' + t.tipo);
        var $tbody = $('#tabelaTratamentos tbody').empty()
            .append('<tr><td colspan="4" class="text-center">Carregando…</td></tr>');
        bootstrap.Modal.getOrCreateInstance(document.getElementById('modalTratamentos'), { backdrop: 'static', keyboard: false }).show();

        $.getJSON(URL, { acao: 'tratamentos', idTipo: t.id })
            .done(function (lista) {
                tratamentosCarregados = {};
                lista.forEach(function (x) { tratamentosCarregados[x.tratamento] = x; });
                $tbody.empty();
                catalogo.tratamentos.forEach(function (c) {
                    var atual = tratamentosCarregados[c.codigo] || { ativo: false, automatico: false, parametrosJson: '' };
                    var $tr = $('<tr></tr>').attr('data-tratamento', c.codigo);
                    var idAtivo = 'trat-ativo-' + c.codigo, idAuto = 'trat-auto-' + c.codigo;
                    $tr.append($('<td class="text-center"></td>').append(
                        $('<input class="form-check-input trat-ativo" type="checkbox">').attr('id', idAtivo)
                            .prop('checked', !!atual.ativo)));
                    $tr.append($('<td></td>').append($('<label class="form-check-label"></label>').attr('for', idAtivo).text(c.descricao)));
                    var $auto = $('<input class="form-check-input trat-auto" type="checkbox" role="switch">').attr('id', idAuto)
                        .prop('checked', !!atual.automatico && c.executavel).prop('disabled', !c.executavel);
                    var $celAuto = $('<td class="text-center"></td>').append(
                        $('<div class="form-check form-switch d-inline-block"></div>').append($auto)
                            .append($('<label class="visually-hidden"></label>').attr('for', idAuto).text('Automático')));
                    if (!c.executavel) {
                        $celAuto.attr('title', 'Depende do operador').append($('<div class="small text-muted">Manual</div>'));
                    }
                    $tr.append($celAuto);
                    $tr.append($('<td></td>').append(
                        $('<textarea class="form-control form-control-sm trat-parametros" rows="1" maxlength="2000"></textarea>')
                            .attr('aria-label', 'Parâmetros de ' + c.descricao).val(atual.parametrosJson || ''))
                        .append($('<div class="invalid-feedback">Informe um objeto JSON, por exemplo {"equipe": "GTO"}.</div>')));
                    $tbody.append($tr);
                });
            })
            .fail(function (xhr) {
                $tbody.empty().append($('<tr><td colspan="4" class="text-danger text-center"></td></tr>')
                    .find('td').text(mensagemErro(xhr, 'Erro ao carregar os tratamentos.')).end());
            });
    }

    function parametrosValidos(texto) {
        if (!texto) return true;
        try {
            var v = JSON.parse(texto);
            return v !== null && typeof v === 'object' && !Array.isArray(v);
        } catch (e) {
            return false;
        }
    }

    function salvarTratamentos() {
        if (!tipoEmEdicao) return;
        var envios = [];
        var invalido = false;
        $('#tabelaTratamentos tbody tr[data-tratamento]').each(function () {
            var $tr = $(this);
            var codigo = $tr.attr('data-tratamento');
            var ativo = $tr.find('.trat-ativo').is(':checked');
            var automatico = $tr.find('.trat-auto').is(':checked');
            var parametros = ($tr.find('.trat-parametros').val() || '').trim();
            var ok = parametrosValidos(parametros);
            $tr.find('.trat-parametros').toggleClass('is-invalid', !ok);
            if (!ok) { invalido = true; return; }
            var antes = tratamentosCarregados[codigo];
            var mudou = antes
                ? (antes.ativo !== ativo || antes.automatico !== automatico || (antes.parametrosJson || '') !== parametros)
                : ativo;
            if (mudou) {
                envios.push({ acao: 'salvarTratamento', idTipo: tipoEmEdicao.id, tratamento: codigo,
                    ativo: ativo ? '1' : '0', automatico: automatico ? '1' : '0', parametrosJson: parametros });
            }
        });
        if (invalido) { aviso('warning', 'Parâmetros inválidos', 'Corrija os campos destacados.'); return; }
        if (!envios.length) { aviso('info', 'Nada a salvar', 'Nenhum tratamento foi alterado.'); return; }

        var $btn = $('#btnSalvarTratamentos').prop('disabled', true);
        var fila = envios.slice();
        (function proximo() {
            if (!fila.length) {
                $btn.prop('disabled', false);
                bootstrap.Modal.getInstance(document.getElementById('modalTratamentos')).hide();
                Swal.fire({ icon: 'success', title: 'Tratamentos salvos', text: tipoEmEdicao.tipo, timer: 1600, showConfirmButton: false });
                carregarTipos();
                return;
            }
            $.post(URL, fila.shift()).done(proximo).fail(function (xhr) {
                $btn.prop('disabled', false);
                aviso('error', 'Não foi possível salvar', mensagemErro(xhr, 'Verifique os tratamentos e tente novamente.'));
            });
        })();
    }

    function carregarTipos() {
        return $.getJSON(URL, { acao: 'listar' })
            .done(function (lista) { tipos = lista; renderizar(); })
            .fail(function (xhr) {
                $('#tabelaTipos tbody').empty().append($('<tr><td colspan="8" class="text-danger text-center"></td></tr>')
                    .find('td').text(mensagemErro(xhr, 'Erro ao carregar os tipos de alerta.')).end());
            });
    }

    $(function () {
        ICONES_SUGERIDOS.forEach(function (i) { $('#sugestoesIcone').append($('<option></option>').val(i)); });
        $('#buscaTipo').on('input', renderizar);
        $('#tabelaTipos')
            .on('change input', '.campo-critico, .campo-cor, .campo-icone', aoAlterarLinha)
            .on('click', '.btn-salvar', salvarLinha)
            .on('click', '.btn-tratamentos', abrirTratamentos);
        $('#tabelaTratamentos').on('change', '.trat-auto', function () {
            if ($(this).is(':checked')) $(this).closest('tr').find('.trat-ativo').prop('checked', true);
        });
        $('#btnSalvarTratamentos').on('click', salvarTratamentos);

        $.getJSON(URL, { acao: 'catalogo' })
            .done(function (c) { catalogo = c; carregarTipos(); })
            .fail(function (xhr) {
                $('#tabelaTipos tbody').empty().append($('<tr><td colspan="8" class="text-danger text-center"></td></tr>')
                    .find('td').text(mensagemErro(xhr, 'Erro ao carregar a configuração.')).end());
            });
    });
})(jQuery);
