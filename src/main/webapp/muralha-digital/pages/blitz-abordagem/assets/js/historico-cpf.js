$(document).on('click', '.btn-registro-fato', function() {
    const idRegistroFato = $(this).data('id');
    if (idRegistroFato) {
        const modalHistorico = bootstrap.Modal.getInstance(document.getElementById('modalHistoricoCpf'));
        if (modalHistorico) {
            modalHistorico.hide();
        }
        abrirModalEditarRegistroDeFatoAbos(idRegistroFato);
    }
});

$(document).on('click', '.btn-associar-registro-fato', function () {
    const idRegistroFato = $(this).data('id');

    if (!idRegistroFato) return;

    if (!window.registroFatoAssociado) {
        window.registroFatoAssociado = idRegistroFato;
        showMessage('Registro de fato associado à abordagem', true);
        return;
    }

    if (window.registroFatoAssociado === idRegistroFato) {
        window.registroFatoAssociado = null;
        showMessage('Associação do registro de fato removida.', true);
        return;
    }

    showMessage('Já existe um registro de fato associado à abordagem', false);
});

function verificarCPFCompleto(cpfInput) {
    const cpf = cpfInput.val();
    if (cpf && cpf.length === 14 && !cpf.includes('_')) {
        adicionarBotaoHistoricoCPF(cpfInput);
    } else {
        removerBotaoHistoricoCPF(cpfInput);
    }
}

function adicionarBotaoHistoricoCPF(cpfInput) {
    const pessoaCard = cpfInput.closest('.pessoa-card');
    const idPessoa = pessoaCard.attr('id');
    
    if (!pessoaCard.find('.btn-historico-cpf').length) {
        const botaoHtml = `
            <button type="button" class="btn btn-sm btn-info btn-historico-cpf mt-2" 
                    onclick="abrirHistoricoCPF('${idPessoa}')">
                <i class="bi bi-clock-history"></i> Ver Histórico
            </button>
        `;
        const cpfContainer = cpfInput.closest('.col-md-6');
        cpfContainer.append(botaoHtml);
    }
}

function removerBotaoHistoricoCPF(cpfInput) {
    cpfInput.closest('.col-md-6').find('.btn-historico-cpf').remove();
}

function abrirHistoricoCPF(idPessoa) {
    const pessoaCard = $('#' + idPessoa);
    const cpf = pessoaCard.find('.cpf').val();
    const nome = pessoaCard.find('.nome').val() || 'Pessoa';
    
    if (!cpf || cpf.length !== 14 || cpf.includes('_')) {
        showMessage('CPF inválido ou incompleto.', false);
        return;
    }
    
    $('#cpfSelecionado').text(cpf + ' - ' + nome);
    $('#containerRegistrosFato').html('<p class="text-center text-muted">Carregando registros...</p>');
    $('#containerAntecedentesCriminais').html('<p class="text-center text-muted">Carregando antecedentes...</p>');
    
    const modal = new bootstrap.Modal(document.getElementById('modalHistoricoCpf'));
    modal.show();
    
    buscarHistoricoCPF(cpf);
}

function buscarHistoricoCPF(cpf) {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'buscarHistoricoPorCpf',
            cpf: cpf.replace(/\D/g, '')
        },
        dataType: 'json',
        success: function(response) {
            if (response) {
                carregarRegistrosFatoCPF(response.registrosDeFato || []);
                carregarAntecedentesCriminais(response.antecedentesCriminais || []);
            } else {
                $('#containerRegistrosFato').html('<p class="text-center text-muted">Nenhum registro de fato encontrado.</p>');
                $('#containerAntecedentesCriminais').html('<p class="text-center text-muted">Nenhum antecedente criminal encontrado.</p>');
            }
        },
        error: function(xhr, status, error) {
            console.error('Erro ao buscar histórico por CPF:', error);
            $('#containerRegistrosFato').html('<p class="text-center text-danger">Erro ao carregar histórico.</p>');
            $('#containerAntecedentesCriminais').html('<p class="text-center text-danger">Erro ao carregar histórico.</p>');
        }
    });
}

function carregarRegistrosFatoCPF(registros) {
    const container = $('#containerRegistrosFato');
    
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
                        <button type="button" class="btn btn-sm btn-outline-primary btn-registro-fato" 
                                data-id="${registro.id}">
                            <i class="bi bi-pencil"></i> Visualizar
                        </button>
                        <button type="button" class="btn btn-sm btn-outline-success btn-associar-registro-fato"
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

function carregarAntecedentesCriminais(antecedentes) {
    const container = $('#containerAntecedentesCriminais');
    
    if (!antecedentes || antecedentes.length === 0) {
        container.html('<p class="text-center text-muted">Nenhum antecedente criminal encontrado.</p>');
        return;
    }
    
    let html = '';
    antecedentes.forEach(function(antecedente) {
        const dataFormatada = antecedente.data_ocorrencia ? new Date(antecedente.data_ocorrencia).toLocaleDateString('pt-BR') : 'Data não informada';
        
        html += `
            <div class="mb-2 p-2 border rounded">
                <div class="d-flex justify-content-between">
                    <strong>${antecedente.tipo_crime || 'Tipo não informado'}</strong>
                    <small class="text-muted">${dataFormatada}</small>
                </div>
                <div class="small">
                    ${antecedente.local_ocorrencia ? `<div><strong>Local:</strong> ${antecedente.local_ocorrencia}</div>` : ''}
                    ${antecedente.descricao ? `<div class="mt-1"><strong>Descrição:</strong> ${antecedente.descricao.substring(0, 100)}${antecedente.descricao.length > 100 ? '...' : ''}</div>` : ''}
                    ${antecedente.sentenca ? `<div class="mt-1"><strong>Sentença:</strong> ${antecedente.sentenca.substring(0, 100)}${antecedente.sentenca.length > 100 ? '...' : ''}</div>` : ''}
                </div>
            </div>
        `;
    });
    
    container.html(html);
}