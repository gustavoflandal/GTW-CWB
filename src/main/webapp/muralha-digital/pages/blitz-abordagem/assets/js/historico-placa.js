$(document).on('click', '.btn-registro-fato-placa', function() {
    const idRegistroFato = $(this).data('id');
    if (idRegistroFato) {
        const modalHistorico = bootstrap.Modal.getInstance(document.getElementById('modalHistoricoPlaca'));
        if (modalHistorico) {
            modalHistorico.hide();
        }
        abrirModalEditarRegistroDeFatoAbos(idRegistroFato);
    }
});

$(document).on('click', '.btn-associar-registro-fato-placa', function () {
    const idRegistroFato = $(this).data('id');

    if (!idRegistroFato) return;

    if (!window.registroFatoAssociado) {
        window.registroFatoAssociado = idRegistroFato;
        showMessage('Registro de fato associado à abordagem.', true);
        return;
    }

    if (window.registroFatoAssociado === idRegistroFato) {
        window.registroFatoAssociado = null;
        showMessage('Associação do registro de fato removida.', true);
        return;
    }

    showMessage('Já existe um registro de fato associado à abordagem.', false);
});

$(document).on('click', '.btn-tratar-alerta', function () {
    const idAlerta = $(this).data('id');
    if (idAlerta) {
        window.open(
            "/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=" + idAlerta,
            "_blank"
        );
    }
});

function verificarPlacaCompleta(placaInput) {
    const placa = placaInput.val();
    if (placa && placa.length >= 7) {
        adicionarBotaoHistoricoPlaca(placaInput);
    } else {
        removerBotaoHistoricoPlaca(placaInput);
    }
}

function adicionarBotaoHistoricoPlaca(placaInput) {
    const container = placaInput.closest('.mb-3');
    
    if (!container.find('.btn-historico-placa').length) {
        const botaoHtml = `
            <button type="button" class="btn btn-sm btn-info btn-historico-placa mt-2" 
                    onclick="abrirHistoricoPlaca()">
                <i class="bi bi-clock-history"></i> Ver Histórico da Placa
            </button>
        `;
        container.append(botaoHtml);
    }
}

function removerBotaoHistoricoPlaca(placaInput) {
    placaInput.closest('.mb-3').find('.btn-historico-placa').remove();
}

function abrirHistoricoPlaca() {
    const placa = $('#placaVeiculo').val();
    
    if (!placa || placa.length < 7) {
        showMessage('Placa inválida ou incompleta.', false);
        return;
    }
    
    $('#placaSelecionada').text(placa);
    $('#containerInformacoesVeiculo').html('<p class="text-center text-muted">Carregando informações...</p>');
    $('#containerRegistrosFatoPlaca').html('<p class="text-center text-muted">Carregando registros...</p>');
    $('#containerAlertas').html('<p class="text-center text-muted">Carregando alertas...</p>');
    
    const modal = new bootstrap.Modal(document.getElementById('modalHistoricoPlaca'));
    modal.show();
    
    buscarHistoricoPlaca(placa);
    buscarInformacoesVeiculoPorPlaca(placa);
}

function buscarHistoricoPlaca(placa) {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'buscarHistoricoPorPlaca',
            placa: placa,
        },
        dataType: 'json',
        success: function(response) {
            if (response) {
                carregarRegistrosFatoPlaca(response.registrosDeFato || []);
                carregarAlertas(response.alertas || []);
            } else {
                $('#containerRegistrosFatoPlaca').html('<p class="text-center text-muted">Nenhum registro de fato encontrado.</p>');
                $('#containerAlertas').html('<p class="text-center text-muted">Nenhum alerta encontrado.</p>');
            }
        },
        error: function(xhr, status, error) {
            console.error('Erro ao buscar histórico por placa:', error);
            $('#containerRegistrosFatoPlaca').html('<p class="text-center text-danger">Erro ao carregar histórico.</p>');
            $('#containerAlertas').html('<p class="text-center text-danger">Erro ao carregar histórico.</p>');
        }
    });
}

function buscarInformacoesVeiculoPorPlaca(placa) {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'obterInformacoesVeiculoPorPlaca',
            placa: placa,
        },
        dataType: 'json',
        success: function(response) {
            if (response && response.sucesso) {
                exibirInformacoesVeiculo(response.veiculo);
            } else {
                exibirInformacoesVeiculo(null);
            }
        },
        error: function(xhr, status, error) {
            console.error('Erro ao buscar informações do veículo:', error);
            exibirInformacoesVeiculo(null);
        }
    });
}

function exibirInformacoesVeiculo(veiculo) {
    const container = $('#containerInformacoesVeiculo');
    
    if (!veiculo) {
        container.html('<p class="text-center text-muted">Informações do veículo não disponíveis.</p>');
        return;
    }
    
    let html = '';
    
    if (window.innerWidth < 768) {
        html = `
            <div class="col-12">
                <div><strong>Marca:</strong> ${veiculo.marca || 'Não informada'}</div>
                <div><strong>Modelo:</strong> ${veiculo.modelo || 'Não informado'}</div>
                <div><strong>Tipo:</strong> ${veiculo.tipo || 'Não informado'}</div>
                <div><strong>Cor:</strong> ${veiculo.cor || 'Não informada'}</div>
            </div>
        `;
    } else {
        html = `
            <div class="col-md-3">
                <div><strong>Marca:</strong></div>
                <div>${veiculo.marca || 'Não informada'}</div>
            </div>
            <div class="col-md-3">
                <div><strong>Modelo:</strong></div>
                <div>${veiculo.modelo || 'Não informado'}</div>
            </div>
            <div class="col-md-3">
                <div><strong>Tipo:</strong></div>
                <div>${veiculo.tipo || 'Não informado'}</div>
            </div>
            <div class="col-md-3">
                <div><strong>Cor:</strong></div>
                <div>${veiculo.cor || 'Não informada'}</div>
            </div>
        `;
    }
    
    container.html(html);
}

function carregarRegistrosFatoPlaca(registros) {
    const container = $('#containerRegistrosFatoPlaca');
    
    if (!registros || registros.length === 0) {
        container.html('<p class="text-center text-muted">Nenhum registro de fato encontrado.</p>');
        return;
    }
    
    let html = '';
    registros.forEach(function(registro) {
        const dataFormatada = registro.dataHoraOcorrido ? new Date(registro.dataHoraOcorrido).toLocaleDateString('pt-BR') : 'Data não informada';
        
        const tipo = registro.detalhamentoFato || 'Registro de Fato';
        
        const privado = registro.privado === 1 ? '<span class="badge bg-warning ms-2">PRIVADO</span>' : '';
        const temBoletim = registro.temBoletim === 1 ? '<span class="badge bg-info ms-2">COM BOLETIM</span>' : '';
        
        html += `
            <div class="mb-2 p-2 border rounded">
                <div class="d-flex justify-content-between align-items-start">
                    <div>
                        <strong>${tipo}</strong>
                        ${privado}
                        ${temBoletim}
                    </div>
                    <small class="text-muted">${dataFormatada}</small>
                </div>
                <div class="small mt-1 d-flex justify-content-between align-items-center">
                    <div>
                        ${registro.idStatus === 1 ? '<span class="badge bg-success">Ativo</span>' : 
                        registro.idStatus === 2 ? '<span class="badge bg-secondary">Encerrado</span>' : 
                        '<span class="badge bg-light text-dark">Desconhecido</span>'}
                        <small class="text-muted ms-2">ID: ${registro.id}</small>
                    </div>
                    <div class="d-flex gap-1">
                        <button type="button" class="btn btn-sm btn-outline-primary btn-registro-fato-placa" 
                                data-id="${registro.id}">
                            <i class="bi bi-pencil"></i> Visualizar
                        </button>
                        <button type="button" class="btn btn-sm btn-outline-success btn-associar-registro-fato-placa"
                                data-id="${registro.id}">
                            <i class="bi bi-link-45deg"></i> Associar
                        </button>
                    </div>
                </div>
            </div>
        `;
    });
    
    container.html(html);
}

function carregarAlertas(alertas) {
    const container = $('#containerAlertas');
    
    if (!alertas || alertas.length === 0) {
        container.html('<p class="text-center text-muted">Nenhum alerta encontrado.</p>');
        return;
    }
    
    let html = '';
    alertas.forEach(function(alerta) {
        const dataFormatada = alerta.dataAlerta ? new Date(alerta.dataAlerta).toLocaleString('pt-BR') : 'Data não informada';
        const tipoAlerta = alerta.tipoAlerta || alerta.descAlerta || 'Alerta';
        const status = alerta.statusAlertaDesc || 'Status desconhecido';
        const placa = alerta.placaVeiculo || alerta.placaCadastro || 'Placa não informada';
        
        html += `
            <div class="mb-2 p-2 border rounded">
                <div class="d-flex justify-content-between">
                    <strong>${tipoAlerta}</strong>
                    <small class="text-muted">${dataFormatada}</small>
                </div>
                <div class="small">
                    <div><strong>Placa:</strong> ${placa}</div>
                    <div><strong>Status:</strong> ${status}</div>
                    ${alerta.observacao ? `<div class="mt-1"><strong>Observação:</strong> ${alerta.observacao.substring(0, 100)}${alerta.observacao.length > 100 ? '...' : ''}</div>` : ''}
                </div>
                <div class="mt-2 text-end">
                    <button type="button" class="btn btn-sm btn-warning btn-tratar-alerta" 
                            data-id="${alerta.id}">
                        <i class="bi bi-gear"></i> Visualizar
                    </button>
                </div>
            </div>
        `;
    });
    
    container.html(html);
}