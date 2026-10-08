var todosLocais = [];

$(document).ready(function () {
    listarBlitz();

    $('#btnNovaBlitzAutomatica').click(function () {
        abrirModalCriacao(1); // 1 = AUTOMATICA
    });

    $('#btnNovaBlitzManual').click(function () {
        abrirModalCriacao(2); // 2 = MANUAL
    });

    $('#btnVoltar').click(function() {
        // Verifica se veio de uma página anterior no histórico
        if (document.referrer) {
            window.history.back();
        } else {
            // Se não houver página anterior (webview), redireciona para uma página padrão
            window.location.href = '/login/muralha_principal.jsp';
        }
    });

    $('#tabela-blitz tbody').on('click', '.btn-visualizar', function () {
        const row = $(this).closest('tr');
        const idBlitz = row.data('id');
        visualizarBlitz(idBlitz);
    });

    $('#tabela-blitz tbody').on('click', '.btn-editar', function () {
        const row = $(this).closest('tr');
        const idBlitz = row.data('id');
        editarBlitz(idBlitz);
    });

    $('#tabela-blitz tbody').on('click', '.btn-excluir', function () {
        const row = $(this).closest('tr');
        const idBlitz = row.data('id');
        
        if (confirm("Tem certeza que deseja excluir esta Blitz ?")) {
            excluirBlitz(idBlitz, ativo);
        }
    });
});

function abrirModalCriacao(tipoBlitz) {
    const modalElement = document.getElementById('modalCriarBlitz');

    $('#modalCriarBlitz').data('tipoBlitz', tipoBlitz);
    
    const modal = new bootstrap.Modal(modalElement, {
        backdrop: 'static',
        keyboard: false
    });
    
    let titulo = 'Nova Blitz Digital';
    if (tipoBlitz === 1) {
        titulo = 'Nova Blitz Automática';
    } else if (tipoBlitz === 2) {
        titulo = 'Nova Blitz Manual';
    }
    $('#modalBlitzLabel').text(titulo);
    
    modal.show();
    carregarDadosModal();
}

$(document).on('hidden.bs.modal', '#modalCriarBlitz', function () {
    $('#formBlitz')[0].reset();
    $('.selectpicker').selectpicker('refresh').prop('disabled', false);
    $('input, textarea').prop('readonly', false);
    $('input[type="checkbox"]').prop('disabled', false);
    
    $('input[name="tipoAssociacao"][value="usuario"]').prop('checked', true);
    $('#containerUsuarios').show();
    $('#containerGuarnicoes').hide();
    $('input[name="tipoAssociacao"]').prop('disabled', false);
    
    $('#notificarAgentesProximos').prop('checked', false);
    $('#containerRaioNotificacao').hide();
    $('#raioNotificacaoKm').prop('required', false).val('');
    $('#dataCriacao').val('');
    $('#endereco').val('');
    
    $('input[name="tipoAssociacao"]').parent().find('small.text-muted').remove();
    
    $('#salvarBlitz').show().text('Salvar Blitz');
    $('#modalBlitzLabel').text('Nova Blitz Digital');
    
    $('#modalCriarBlitz').removeData('idBlitz');
    $('#modalCriarBlitz').removeData('tipoBlitz');
    
    $('#encerrarBlitz').remove();

    if (typeof resetarMapaBlitz === 'function') {
        resetarMapaBlitz();
    }

    $('.selectpicker').selectpicker('refresh');
});

function listarBlitz() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Blitz",
        dataType: "json",
        data: "acao=listar",
        success: function (response) {
            const $tbody = $('#tabela-blitz tbody');
            $tbody.empty();

            if (!response.blitzes || response.blitzes.length === 0) {
                $tbody.append('<tr><td colspan="7" class="text-center">Nenhuma blitz encontrada.</td></tr>');
                return;
            }

            response.blitzes.forEach(function(blitz) {
                const id = blitz.id;
                const nome_blitz = blitz.nome_blitz;
                const titulo_notificacao = blitz.titulo_notificacao;
                const data_inicio = blitz.data_inicio;
                const data_fim = blitz.data_fim;
                const ativo = blitz.ativo === true || blitz.ativo === 'true' || blitz.ativo === 1 || blitz.ativo === '1';
                const dataCriacao = blitz.data_criacao;
                const temAbordagensAssociadas = blitz.tem_abordagens_associadas === true || blitz.tem_abordagens_associadas === 'true' || blitz.tem_abordagens_associadas === 1 || blitz.tem_abordagens_associadas === '1';
                
                const dataInicioFormatada = data_inicio ? formatarData(data_inicio) : '-';
                const dataFimFormatada = data_fim ? formatarData(data_fim) : '-';
                
                const status = ativo ? 
                    '<span class="badge bg-success">Ativa</span>' : 
                    '<span class="badge bg-danger">Inativa</span>';
                
                // Só pode excluir se:
                // 1. A data fim ainda não passou (blitz não finalizada)
                // 2. Não tem abordagens associadas
                const agora = new Date();
                const dataFimObj = data_fim ? new Date(data_fim) : null;
                
                const blitzNaoFinalizada = !dataFimObj || dataFimObj > agora;
                const podeExcluir = blitzNaoFinalizada && !temAbordagensAssociadas;
                
                // Pode editar apenas se ativa
                const podeEditar = ativo;
                
                let tooltipExclusao = "Excluir Blitz";
                let tooltipEdicao = "Editar Blitz";
                
                if (!podeExcluir) {
                    if (temAbordagensAssociadas) {
                        tooltipExclusao = "Não é possível excluir: esta Blitz possui abordagens associadas";
                    } else if (!blitzNaoFinalizada) {
                        tooltipExclusao = "Não é possível excluir: Blitz já foi encerrada";
                    }
                }
                
                if (!podeEditar) {
                    tooltipEdicao = "Não é possível editar: Blitz inativa";
                }
                
                let btnEditarHtml;
                if (podeEditar) {
                    btnEditarHtml = `<button class="btn btn-outline-secondary btn-sm btn-editar" title="Editar Blitz" data-id="${id}">
                        <i class="bi bi-pen"></i>
                    </button>`;
                } else {
                    btnEditarHtml = `<span class="d-inline-block" tabindex="0" data-toggle="tooltip" title="${tooltipEdicao}">
                        <button class="btn btn-outline-secondary btn-sm" style="pointer-events: none;" disabled>
                            <i class="bi bi-pen"></i>
                        </button>
                    </span>`;
                }
                
                let btnExcluirHtml;
                if (podeExcluir) {
                    btnExcluirHtml = `<button class="btn btn-outline-danger btn-sm btn-excluir" title="Excluir Blitz" data-id="${id}">
                        <i class="bi bi-trash"></i>
                    </button>`;
                } else {
                    btnExcluirHtml = `<span class="d-inline-block" tabindex="0" data-toggle="tooltip" title="${tooltipExclusao}">
                        <button class="btn btn-outline-secondary btn-sm" style="pointer-events: none;" disabled>
                            <i class="bi bi-trash"></i>
                        </button>
                    </span>`;
                }
                
                const row = `<tr data-id="${id}" data-ativo="${ativo}" data-data-inicio="${data_inicio}" data-data-criacao="${dataCriacao}">
                    <td>${nome_blitz}</td>
                    <td>${titulo_notificacao}</td>
                    <td>${dataInicioFormatada}</td>
                    <td>${dataFimFormatada}</td>
                    <td>${status}</td>
                    <td>
                        <button class="btn btn-outline-primary btn-sm btn-visualizar" title="Visualizar Blitz" data-id="${id}">
                            <i class="bi bi-search"></i>
                        </button>
                        ${btnEditarHtml}
                        ${btnExcluirHtml}
                    </td>
                </tr>`;
                $tbody.append(row);
            });
            
            $('[data-toggle="tooltip"]').tooltip({
                placement: 'top',
                trigger: 'hover'
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao listar blitz:", errorThrown);
            const $tbody = $('#tabela-blitz tbody');
            $tbody.empty().append('<tr><td colspan="7" class="text-danger text-center">Erro ao carregar blitz.</td></tr>');
        }
    });
}

function excluirBlitz(idBlitz) {
    if (confirm("Tem certeza que deseja EXCLUIR permanentemente esta blitz?")) {
        $("body").addClass("loading");
        $.ajax({
            type: "POST",
            url: "/MuralhaDigital/Blitz",
            dataType: "json",
            data: {
                acao: 'excluirBlitz',
                idBlitz: idBlitz
            },
            success: function (response) {
                const sucesso = response.sucesso;
                const mensagem = response.mensagem;
                
                if (sucesso) {
                    listarBlitz();
                } else {
                    alert('Erro: ' + mensagem);
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erro ao excluir blitz:", errorThrown);
                alert('Erro ao excluir blitz.');
            },
            complete: function() {
                $("body").removeClass("loading");
            }
        });
    }
}

function formatarData(dataString) {
    if (!dataString) return '-';
    const date = new Date(dataString);
    return date.toLocaleString('pt-BR');
}

function visualizarBlitz(idBlitz) {
    $("body").addClass("loading");
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Blitz",
        dataType: "json",
        data: { acao: 'obterBlitz', idBlitz: idBlitz },
        success: function (response) {
            if (response.blitz) {
                abrirModalVisualizacao(response.blitz);
            } else {
                alert('Blitz não encontrada.');
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao carregar blitz:", errorThrown);
            alert('Erro ao carregar dados da blitz.');
        },
        complete: function() {
            $("body").removeClass("loading");
        }
    });
}

function editarBlitz(idBlitz) {
    $("body").addClass("loading");
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Blitz",
        dataType: "json",
        data: { acao: 'obterBlitz', idBlitz: idBlitz },
        success: function (response) {
            if (response.blitz) {
                abrirModalEdicao(response.blitz);
            } else {
                alert('Blitz não encontrada.');
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao carregar blitz:", errorThrown);
            alert('Erro ao carregar dados da blitz.');
        },
        complete: function() {
            $("body").removeClass("loading");
        }
    });
}

function abrirModalVisualizacao(blitz) {
    const modalElement = document.getElementById('modalCriarBlitz');
    const modal = new bootstrap.Modal(modalElement, {
        backdrop: 'static',
        keyboard: false
    });
    
    $('#modalBlitzLabel').text('Visualizar Blitz Digital');
    $('#salvarBlitz').hide();
    $('#modalCriarBlitz').data('idBlitz', blitz.id);
    $('#modalCriarBlitz').data('tipoBlitz', blitz.id_tipo_blitz);
    
    $("body").addClass("loading");
    carregarDadosModal().then(() => {
        carregarDadosModalVisualizacao(blitz);
        modal.show();
    }).catch(error => {
        console.error('Erro ao carregar dados do modal:', error);
        alert('Erro ao carregar dados para visualização.');
    }).finally(() => {
        $("body").removeClass("loading");
    });
}

function abrirModalEdicao(blitz) {
    const ativa = blitz.ativo === true || blitz.ativo === 'true' || blitz.ativo === 1 || blitz.ativo === '1';
    
    if (!ativa) {
        alert('Não é possível editar uma blitz inativa.');
        return;
    }
    
    const modalElement = document.getElementById('modalCriarBlitz');
    const modal = new bootstrap.Modal(modalElement, {
        backdrop: 'static',
        keyboard: false
    });
    
    $('#modalBlitzLabel').text('Editar Blitz Digital');
    $('#salvarBlitz').show().text('Atualizar Blitz');
    $('#modalCriarBlitz').data('idBlitz', blitz.id);
    $('#modalCriarBlitz').data('tipoBlitz', blitz.id_tipo_blitz);

    if (!$('#encerrarBlitz').length) {
        $('#salvarBlitz').before('<button type="button" class="btn btn-danger" id="encerrarBlitz">Encerrar Blitz</button>');
    }
    
    $("body").addClass("loading");
    carregarDadosModal().then(() => {
        carregarDadosModalEdicao(blitz);
        modal.show();
    }).catch(error => {
        console.error('Erro ao carregar dados do modal:', error);
        alert('Erro ao carregar dados para edição.');
    }).finally(() => {
        $("body").removeClass("loading");
    });
}

function carregarDadosModal() {
    return new Promise((resolve, reject) => {
        Promise.all([
            carregarLocaisPromise(),
            carregarUsuariosPromise(), 
            carregarGuarnicoesPromise(),
            carregarTiposAlertaPromise()
        ]).then(() => {
            resolve();
        }).catch(error => {
            reject(error);
        });
    });
}

function carregarTiposAlertaAssociados(idBlitz, modoVisualizacao) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: { acao: 'listarTiposAlertaBlitz', idBlitz: idBlitz },
            dataType: 'json',
            success: function(response) {
                const select = $('#selectTiposAlerta');
                const valoresSelecionados = [];
                
                if (response.tiposAlerta && response.tiposAlerta.length > 0) {
                    response.tiposAlerta.forEach(function(tipo) {
                        valoresSelecionados.push(tipo.id.toString());
                    });
                }
                
                select.selectpicker('val', valoresSelecionados);
                if (modoVisualizacao) {
                    select.selectpicker('refresh').prop('disabled', true);
                }
                resolve();
            },
            error: function(error) {
                console.error('Erro ao carregar tipos de alerta associados:', error);
                reject(error);
            }
        });
    });
}

function carregarTiposAlertaPromise() {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: { acao: 'listarTiposAlerta' },
            dataType: 'json',
            success: function(response) {
                const select = $('#selectTiposAlerta');
                select.empty();
                
                if (response.tiposAlerta && response.tiposAlerta.length > 0) {
                    response.tiposAlerta.forEach(function(tipo) {
                        select.append($('<option></option>')
                            .val(tipo.id)
                            .text(tipo.tipo));
                    });
                }
                
                select.selectpicker('refresh');
                resolve();
            },
            error: function(error) {
                console.error('Erro ao carregar tipos de alerta:', error);
                reject(error);
            }
        });
    });
}

function carregarLocaisPromise() {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: { acao: 'listarLocais' },
            dataType: 'json',
            success: function(response) {
                const select = $('#selectLocais');
                select.empty();
                todosLocais = [];
                
                if (response.locais && response.locais.length > 0) {
                    response.locais.forEach(function(local) {
                        todosLocais.push(local.id_local + '_' + local.sequencia_local);

                        select.append($('<option></option>')
                            .val(local.id_local + '_' + local.sequencia_local)
                            .text(local.nome));
                    });
                }

                $('#todosLocais').val(todosLocais);
                
                select.selectpicker('refresh');
                resolve();
            },
            error: function(error) {
                console.error('Erro ao carregar locais:', error);
                reject(error);
            }
        });
    });
}

function carregarUsuariosPromise() {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: { acao: 'listarUsuariosAG' },
            dataType: 'json',
            success: function(response) {
                const select = $('#selectUsuarios');
                select.empty();
                
                if (response.usuarios && response.usuarios.length > 0) {
                    response.usuarios.forEach(function(usuario) {
                        select.append($('<option></option>')
                            .val(usuario.id_usuario)
                            .text(usuario.nome + ' (' + usuario.usuario + ')'));
                    });
                }
                
                select.selectpicker('refresh');
                resolve();
            },
            error: function(error) {
                console.error('Erro ao carregar usuários:', error);
                reject(error);
            }
        });
    });
}

function carregarGuarnicoesPromise() {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: { acao: 'listarGuarnicoes' },
            dataType: 'json',
            success: function(response) {
                const select = $('#selectGuarnicoes');
                select.empty();
                
                if (response.guarnicoes && response.guarnicoes.length > 0) {
                    response.guarnicoes.forEach(function(guarnicao) {
                        select.append($('<option></option>')
                            .val(guarnicao.id)
                            .text(guarnicao.nome));
                    });
                }
                
                select.selectpicker('refresh');
                resolve();
            },
            error: function(error) {
                console.error('Erro ao carregar guarnições:', error);
                reject(error);
            }
        });
    });
}

function carregarDadosModalVisualizacao(blitz) {
    $('#nomeBlitz').val(blitz.nome_blitz).prop('readonly', true);
    $('#tituloNotificacao').val(blitz.titulo_notificacao).prop('readonly', true);
    $('#descricao').val(blitz.descricao).prop('readonly', true);

    if (blitz.endereco) {
        $('#endereco').val(blitz.endereco).prop('readonly', true);
    }
    
    const dataInicio = blitz.data_inicio;
    const dataFim = blitz.data_fim;
    
    if (dataInicio) {
        $('#dataInicio').val(formatarDataParaInput(dataInicio)).prop('readonly', true);
    }
    if (dataFim) {
        $('#dataFim').val(formatarDataParaInput(dataFim)).prop('readonly', true);
    }
    
    if (blitz.data_criacao) {
        $('#dataCriacao').val(blitz.data_criacao);
    }
    
    const notificarAgentesProximos = blitz.notificar_agentes_proximos === true || blitz.notificar_agentes_proximos === 1 || blitz.notificar_agentes_proximos === '1';
    $('#notificarAgentesProximos').prop('checked', notificarAgentesProximos).prop('disabled', true);
    
    if (notificarAgentesProximos) {
        $('#containerRaioNotificacao').show();
        $('#raioNotificacaoKm').val(blitz.raio_notificacao_km).prop('readonly', true);
    } else {
        $('#containerRaioNotificacao').hide();
        $('#raioNotificacaoKm').prop('required', false);
    }
    
    $('#ativo').prop('checked', blitz.ativo === true || blitz.ativo === 'true' || blitz.ativo === 1 || blitz.ativo === '1').prop('disabled', true);
    
    carregarDadosAssociacaoVisualizacao(blitz.id);
    
    $('.selectpicker').selectpicker('refresh').prop('disabled', true);
    $('input[name="tipoAssociacao"]').prop('disabled', true);
}

function carregarDadosModalEdicao(blitz) {
    $('#nomeBlitz').val(blitz.nome_blitz).prop('readonly', false);
    $('#tituloNotificacao').val(blitz.titulo_notificacao).prop('readonly', false);
    $('#descricao').val(blitz.descricao).prop('readonly', false);

    if (blitz.endereco) {
        $('#endereco').val(blitz.endereco).prop('readonly', false);
    }
    
    const dataInicio = blitz.data_inicio;
    const dataFim = blitz.data_fim;
    
    if (dataInicio) {
        $('#dataInicio').val(formatarDataParaInput(dataInicio)).prop('readonly', false);
    }
    if (dataFim) {
        $('#dataFim').val(formatarDataParaInput(dataFim)).prop('readonly', false);
    }
    
    if (blitz.data_criacao) {
        $('#dataCriacao').val(blitz.data_criacao);
    }
    
    const notificarAgentesProximos = blitz.notificar_agentes_proximos === true || blitz.notificar_agentes_proximos === 1 || blitz.notificar_agentes_proximos === '1';
    $('#notificarAgentesProximos').prop('checked', notificarAgentesProximos).prop('disabled', false);
    
    if (notificarAgentesProximos) {
        $('#containerRaioNotificacao').show();
        $('#raioNotificacaoKm').val(blitz.raio_notificacao_km).prop('readonly', false);
        $('#raioNotificacaoKm').prop('required', true);
    } else {
        $('#containerRaioNotificacao').hide();
        $('#raioNotificacaoKm').prop('required', false);
    }
    
    // Desabilitar checkbox ativo na edição
    $('#ativo').prop('checked', blitz.ativo === true || blitz.ativo === 'true' || blitz.ativo === 1 || blitz.ativo === '1').prop('disabled', true);
    
    $('input[name="tipoAssociacao"]').prop('disabled', true);
    
    carregarDadosAssociacaoEdicao(blitz.id);
    
    if (blitz.id_tipo_blitz === 2) {
        $('#selectLocais').removeAttr('required');
    }

    $('.selectpicker').selectpicker('refresh').prop('disabled', false);
    
    $('input[name="tipoAssociacao"]').each(function() {
        $(this).parent().append('<small class="text-muted d-block">Não é possível alterar o tipo de associação na edição</small>');
    });
}

function formatarDataParaInput(dataString) {
    if (!dataString) return '';
    
    const date = new Date(dataString);
    
    // Ajustar para o fuso horário local
    const timezoneOffset = date.getTimezoneOffset() * 60000;
    const localDate = new Date(date.getTime() - timezoneOffset);
    
    return localDate.toISOString().slice(0, 16);
}

function carregarDadosAssociacaoVisualizacao(idBlitz) {
    return Promise.all([
        carregarLocaisAssociados(idBlitz, true),
        carregarTipoAssociacaoVisualizacao(idBlitz),
        carregarTiposAlertaAssociados(idBlitz, true)
    ]);
}

function carregarDadosAssociacaoEdicao(idBlitz) {
    return Promise.all([
        carregarLocaisAssociados(idBlitz, false),
        carregarTipoAssociacaoEdicao(idBlitz),
        carregarTiposAlertaAssociados(idBlitz, false)
    ]);
}

function carregarLocaisAssociados(idBlitz, modoVisualizacao) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: { acao: 'listarLocaisBlitz', idBlitz: idBlitz },
            dataType: 'json',
            success: function(response) {
                const select = $('#selectLocais');
                const valoresSelecionados = [];
                
                if (response.locais && response.locais.length > 0) {
                    response.locais.forEach(function(local) {
                        const valor = local.id_local + '_' + local.sequencia_local;
                        valoresSelecionados.push(valor);
                    });
                }
                
                select.selectpicker('val', valoresSelecionados);
                if (modoVisualizacao) {
                    select.selectpicker('refresh').prop('disabled', true);
                }
                resolve();
            },
            error: function(error) {
                console.error('Erro ao carregar locais associados:', error);
                reject(error);
            }
        });
    });
}

function carregarTipoAssociacaoVisualizacao(idBlitz) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: { acao: 'verificarTipoAssociacao', idBlitz: idBlitz },
            dataType: 'json',
            success: function(response) {
                const tipo = response.tipo;
                if (tipo === 'usuario') {
                    $('input[name="tipoAssociacao"][value="usuario"]').prop('checked', true);
                    $('#containerUsuarios').show();
                    $('#containerGuarnicoes').hide();
                    carregarUsuariosAssociados(idBlitz, true).then(resolve);
                } else {
                    $('input[name="tipoAssociacao"][value="guarnicao"]').prop('checked', true);
                    $('#containerUsuarios').hide();
                    $('#containerGuarnicoes').show();
                    carregarGuarnicoesAssociadas(idBlitz, true).then(resolve);
                }
                $('.selectpicker').selectpicker('refresh');
            },
            error: function(error) {
                console.error('Erro ao carregar tipo de associação:', error);
                reject(error);
            }
        });
    });
}

function carregarTipoAssociacaoEdicao(idBlitz) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: { acao: 'verificarTipoAssociacao', idBlitz: idBlitz },
            dataType: 'json',
            success: function(response) {
                const tipo = response.tipo;
                if (tipo === 'usuario') {
                    $('input[name="tipoAssociacao"][value="usuario"]').prop('checked', true);
                    $('#containerUsuarios').show();
                    $('#containerGuarnicoes').hide();
                    carregarUsuariosAssociados(idBlitz, false).then(resolve);
                } else {
                    $('input[name="tipoAssociacao"][value="guarnicao"]').prop('checked', true);
                    $('#containerUsuarios').hide();
                    $('#containerGuarnicoes').show();
                    carregarGuarnicoesAssociadas(idBlitz, false).then(resolve);
                }
                
                $('input[name="tipoAssociacao"]').prop('disabled', true);
                $('.selectpicker').selectpicker('refresh');
            },
            error: function(error) {
                console.error('Erro ao carregar tipo de associação:', error);
                reject(error);
            }
        });
    });
}

function carregarUsuariosAssociados(idBlitz, modoVisualizacao) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: { acao: 'listarUsuariosBlitz', idBlitz: idBlitz },
            dataType: 'json',
            success: function(response) {
                const select = $('#selectUsuarios');
                const valoresSelecionados = [];
                
                if (response.usuarios && response.usuarios.length > 0) {
                    response.usuarios.forEach(function(usuario) {
                        valoresSelecionados.push(usuario.id_usuario.toString());
                    });
                }
                
                select.selectpicker('val', valoresSelecionados);
                if (modoVisualizacao) {
                    select.selectpicker('refresh').prop('disabled', true);
                }
                resolve();
            },
            error: function(error) {
                console.error('Erro ao carregar usuários associados:', error);
                reject(error);
            }
        });
    });
}

function carregarGuarnicoesAssociadas(idBlitz, modoVisualizacao) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: '/MuralhaDigital/Blitz',
            type: 'GET',
            data: { acao: 'listarGuarnicoesBlitz', idBlitz: idBlitz },
            dataType: 'json',
            success: function(response) {
                const select = $('#selectGuarnicoes');
                const valoresSelecionados = [];
                
                if (response.guarnicoes && response.guarnicoes.length > 0) {
                    response.guarnicoes.forEach(function(guarnicao) {
                        valoresSelecionados.push(guarnicao.id.toString());
                    });
                }
                
                select.selectpicker('val', valoresSelecionados);
                select.selectpicker('refresh');
                
                if (modoVisualizacao) {
                    select.prop('disabled', true);
                    select.selectpicker('refresh');
                }
                resolve();
            },
            error: function(error) {
                console.error('Erro ao carregar guarnições associadas:', error);
                reject(error);
            }
        });
    });
}

function excluirBlitz(idBlitz, ativo) {
    $("body").addClass("loading");
    $.ajax({
        type: "POST",
        url: "/MuralhaDigital/Blitz",
        dataType: "json",
        data: {
            acao: 'excluirBlitz',
            idBlitz: idBlitz,
            ativo: !ativo
        },
        success: function (response) {
            const sucesso = response.sucesso;
            const mensagem = response.mensagem;
            
            if (sucesso) {
                listarBlitz();
            } else {
                alert('Erro: ' + mensagem);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao excluir blitz:", errorThrown);
            alert('Erro ao excluir blitz.');
        },
        complete: function() {
            $("body").removeClass("loading");
        }
    });
}
