$(document).ready(function () {
    $('.selectpicker').selectpicker();
    aplicarEstiloBotoesBootstrapSelect();

    $(document).on('click', '#encerrarBlitz', function() {
        const idBlitz = $('#modalCriarBlitz').data('idBlitz');
        if (idBlitz) {
            encerrarBlitz(idBlitz);
        }
    });

    $('#modalCriarBlitz').on('show.bs.modal', function () {
        const tipoBlitz = $(this).data('tipoBlitz') || 1;
        const containerLocais = $('#selectLocais').closest('.mb-3');
        const containerEndereco = $('#containerEndereco');
        const botaoMapa = $('#btnSelecionarViaMapa');
        const isVisualizacao = $('#modalBlitzLabel').text().includes('Visualizar');
        
        setTimeout(function() {
            if (tipoBlitz === 2) { // MANUAL
                containerLocais.hide();
                botaoMapa.hide();
                containerEndereco.show();
                $('#selectLocais').removeAttr('required');
                $('#endereco').removeAttr('required');
            } else { // AUTOMATICA
                containerLocais.show();
                containerEndereco.hide();
                $('#selectLocais').attr('required', true);
                
                // Mostrar botão do mapa apenas se NÃO for visualização
                if (!isVisualizacao) {
                    botaoMapa.show();
                } else {
                    botaoMapa.hide();
                }
            }
        }, 100);
    });

    $('#notificarAgentesProximos').change(function() {
        const isChecked = $(this).is(':checked');
        if (isChecked) {
            $('#containerRaioNotificacao').show();
            $('#raioNotificacaoKm').prop('required', true);
        } else {
            $('#containerRaioNotificacao').hide();
            $('#raioNotificacaoKm').prop('required', false).val('');
        }
    });

    $('#dataInicio').on('change', function() {
        const isEdicao = $('#modalBlitzLabel').text().includes('Editar');
        const dataInicio = new Date($(this).val());
        
        if (isEdicao) {
            // Na edição: não pode ser anterior à data de criação
            const dataCriacaoStr = $('#dataCriacao').val();
            if (dataCriacaoStr) {
                const dataCriacao = new Date(dataCriacaoStr);
                
                if (dataInicio < dataCriacao) {
                    const dataCriacaoFormatada = formatarDataParaExibicao(dataCriacao);
                    alert(`A data de início não pode ser anterior à data de criação da blitz (${dataCriacaoFormatada}).`);
                    $(this).val('');
                }
            }
        } else {
            // Na criação: não pode ser anterior à data/hora atual
            const agora = new Date();
            
            if (dataInicio < agora) {
                alert('A data de início não pode ser anterior à data/hora atual.');
                $(this).val('');
            }
        }
    });

    function formatarDataParaExibicao(data) {
        return data.toLocaleString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    }
    
    $('input[name="tipoAssociacao"]').change(function() {
        const tipo = $(this).val();
        if (tipo === 'usuario') {
            $('#containerUsuarios').show();
            $('#containerGuarnicoes').hide();
        } else {
            $('#containerUsuarios').hide();
            $('#containerGuarnicoes').show();
        }
        $('.selectpicker').selectpicker('refresh');
    });

    $('#salvarBlitz').click(function() {
        salvarBlitz();
    });
});

$(document).on('loaded.bs.select', '.selectpicker', function () {
    aplicarEstiloBotoesBootstrapSelect();
});

function aplicarEstiloBotoesBootstrapSelect() {
    Array.from(document.getElementsByClassName("bs-select-all")).forEach(
        function(element, index, array) {
            element.classList.remove('btn-light');
            element.classList.add('btn-outline-success');
            element.title = "Marcar todos";
        }
    );
    
    Array.from(document.getElementsByClassName("bs-deselect-all")).forEach(
        function(element, index, array) {
            element.classList.remove('btn-light');
            element.classList.add('btn-outline-danger');
            element.title = "Desmarcar todos";
        }
    );
}

function salvarBlitz() {
    const nomeBlitz = $('#nomeBlitz').val();
    const tituloNotificacao = $('#tituloNotificacao').val();
    const descricao = $('#descricao').val();
    const dataInicio = $('#dataInicio').val();
    const dataFim = $('#dataFim').val();
    const locais = $('#selectLocais').val() || [];
    const tiposAlerta = $('#selectTiposAlerta').val() || [];
    const tipoAssociacao = $('input[name="tipoAssociacao"]:checked').val();
    const associados = tipoAssociacao === 'usuario' ? 
        $('#selectUsuarios').val() || [] : 
        $('#selectGuarnicoes').val() || [];
    const ativo = $('#ativo').is(':checked') ? 1 : 0;
    const notificarAgentesProximos = $('#notificarAgentesProximos').is(':checked') ? 1 : 0;
    const raioNotificacaoKm = $('#raioNotificacaoKm').val();
    const todosLocais = $('#todosLocais').val() ? $('#todosLocais').val().split(',') : [];
    const endereco = $('#endereco').val();

    const isEdicao = $('#modalBlitzLabel').text().includes('Editar');
    const tipoBlitz = $('#modalCriarBlitz').data('tipoBlitz') || 1;

    if (!nomeBlitz || !tituloNotificacao || associados.length === 0) {
        showMessage("Preencha todos os campos obrigatórios.", false);
        return;
    }

    // Para blitz automática, precisa de locais
    if (tipoBlitz === 1 && locais.length === 0) {
        showMessage("Para blitz automática, selecione pelo menos um local (radar).", false);
        return;
    }

    // Para blitz manual, precisa de endereço
    if (tipoBlitz === 2 && !endereco) {
        showMessage("Para blitz manual, informe o endereço.", false);
        return;
    }

    if (notificarAgentesProximos && (!raioNotificacaoKm || raioNotificacaoKm <= 0)) {
        showMessage("Informe o raio de notificação quando a opção 'Notificar agente próximo' estiver marcada.", false);
        return;
    }

    const url = '/MuralhaDigital/Blitz';
    const data = {
        nomeBlitz: nomeBlitz,
        tituloNotificacao: tituloNotificacao,
        descricao: descricao,
        dataInicio: dataInicio,
        dataFim: dataFim,
        locais: locais.join(','),
        tipoAssociacao: tipoAssociacao,
        associados: associados.join(','),
        tiposAlerta: tiposAlerta.join(','),
        ativo: ativo,
        notificarAgentesProximos: notificarAgentesProximos,
        raioNotificacaoKm: raioNotificacaoKm,
        endereco: endereco,
    };

    if (!isEdicao) {
        data.idTipoBlitz = tipoBlitz;
    }

    if (isEdicao) {
        const idBlitz = $('#modalCriarBlitz').data('idBlitz');
        data.acao = 'atualizar';
        data.idBlitz = idBlitz;
    } else {
        data.acao = 'cadastrar';
    }

    $.ajax({
        url: url,
        type: 'POST',
        data: data,
        dataType: 'json',
        success: function(response) {
            const sucesso = response.sucesso;
            const mensagem = response.mensagem;
            
            if (sucesso) {
                showMessage(mensagem, true);
                $('#formBlitz')[0].reset();
                $('.selectpicker').selectpicker('refresh');
                setTimeout(() => {
                    $('#modalCriarBlitz').modal('hide');
                    listarBlitz();
                }, 1000);
            } else {
                showMessage(mensagem, false);
            }
        },
        error: function(xhr, status, error) {
            console.error("Erro ao salvar blitz:", error);
            showMessage("Erro ao salvar blitz.", false);
        }
    });
}

function getCurrentDateTime() {
    const now = new Date();
    const year = now.getFullYear();
    const month = String(now.getMonth() + 1).padStart(2, '0');
    const day = String(now.getDate()).padStart(2, '0');
    const hours = String(now.getHours()).padStart(2, '0');
    const minutes = String(now.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

function showMessage(message, isSuccess) {
    const messageBox = $('#messageBox');
    messageBox.text(message);
    messageBox.removeClass('hidden success error');
    if (isSuccess) {
        messageBox.addClass('success');
    } else {
        messageBox.addClass('error');
    }
    messageBox.fadeIn();
    
    setTimeout(() => {
        messageBox.fadeOut();
    }, 5000);
}

function encerrarBlitz(idBlitz) {
    if (confirm("Tem certeza que deseja encerrar esta blitz? Esta ação irá definir a data fim como agora e desativar a blitz.")) {
        $("body").addClass("loading");
        $.ajax({
            type: "POST",
            url: "/MuralhaDigital/Blitz",
            dataType: "json",
            data: {
                acao: 'encerrarBlitz',
                idBlitz: idBlitz
            },
            success: function (response) {
                const sucesso = response.sucesso;
                const mensagem = response.mensagem;
                
                if (sucesso) {
                    $('#modalCriarBlitz').modal('hide');
                    listarBlitz();
                    alert('Blitz encerrada com sucesso.');
                } else {
                    alert('Erro: ' + mensagem);
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erro ao encerrar blitz:", errorThrown);
                alert('Erro ao encerrar blitz.');
            },
            complete: function() {
                $("body").removeClass("loading");
            }
        });
    }
}