$(document).ready(function () {
    const $modal = $('#modalAtualizaGuarnicao');
    const $btnSalvar = $('#atualizasalvarGuarnicao');

    $modal.on('shown.bs.modal', function () {
        const id = $modal.data('id');
        const $body = $modal.find('.modal-body');
        $btnSalvar.prop('disabled', true); 

        if (id) {
            $.ajax({
                url: '/MuralhaDigital/Guarnicao',
                method: 'GET',
                data: { acao: 'buscarPorId', id: id },
                success: function (guarnicao) {
                    preencherModalGuarnicao(guarnicao);
                },
                error: function () {
                    $body.html('<p class="text-danger">Erro ao carregar os dados da guarnição.</p>');
                }
            });
        } else {
            $body.html('<p class="text-warning">Guarnição não identificada.</p>');
        }
    });
});

async function preencherModalGuarnicao(guarnicao) {
    const $body = $('#modalAtualizaGuarnicao .modal-body');
    const integrantes = await listarIntegrantesGuarnicaoComRetonro();

    const idsSelecionados = (guarnicao.integrantes || [])
        .filter(i => i && i.idUsuario != null)
        .map(i => i.idUsuario.toString());

    const listaMeios = guarnicao.meiosDeslocamento && guarnicao.meiosDeslocamento.trim() !== ''
    ? guarnicao.meiosDeslocamento.split(',') 
    : [];

    const meiosDeslocamento = ['Viatura', 'Moto', 'Cavalo', 'Bicicleta'];

    const optionsHtmlIntegrantes = integrantes.length
        ? integrantes.map(i => `<option value="${i.id}" ${idsSelecionados.includes(i.id.toString()) ? 'selected' : ''}>${i.nome}</option>`).join('')
        : '<option disabled>nenhum usuário informado</option>';

    const optionsHtmlResponsavel = integrantes.length
        ? integrantes.map(i => `<option value="${i.id}" ${i.id == guarnicao.id_usuario_responsavel ? 'selected' : ''}>${i.nome}</option>`).join('')
        : '<option disabled>nenhum usuário informado</option>';

    const optionsHtmlMeiosDeslocamento = meiosDeslocamento.map(i => `
        <option value="${i}" ${listaMeios.includes(i) ? 'selected' : ''}>${i}</option>`).join('');

    const html = `
        <div class="container mt-3 mb-3">
            <div class="tab-content">
                <div class="tab-pane fade show active" id="atualizadados">
                    <div class="row g-4">
                        <input type="hidden" id="atualizaidGuarnicao" value="${guarnicao.id}">
                        <div class="col-md-6">
                            <label class="form-label">Nome da Guarnição</label>
                            <input id="atualizanomeGuarnicao" type="text" class="form-control" value="${guarnicao.nome || ''}">
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Responsável</label>
                            <select id="atualizaresponsavelGuarnicao" class="selectpicker w-100" data-live-search="true">
                                ${optionsHtmlResponsavel}
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Integrantes</label>
                            <select id="atualizaselectIntegrantes" class="selectpicker w-100" multiple data-live-search="true">
                                ${optionsHtmlIntegrantes}
                            </select>
                            <div id="atualizaerroIntegrantes" class="text-danger mt-1" style="display: none;"></div>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Meios deslocamento</label>
                            <select id="atualizameiosDeslocamento" class="selectpicker w-100" multiple data-live-search="true">
                                ${optionsHtmlMeiosDeslocamento}
                            </select>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Data de Criação</label>
                            <input type="text" class="form-control" value="${guarnicao.data_criacao_formatado || guarnicao.data_criacao || ''}" readonly>
                        </div>
                        <div class="col-md-6">
                            <label class="form-label">Última Alteração</label>
                            <input type="text" class="form-control" value="${guarnicao.data_alteracao_formatado || '—'}" readonly>
                        </div>
                        <div class="d-flex justify-content-between">
                            <button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                            <button id="atualizasalvarGuarnicao" class="btn btn-success" disabled>Salvar Guarnição</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    `;

    $body.html(html);
	
	setTimeout(() => {
	    $body.find('.selectpicker').selectpicker('refresh');
	    $('#modalAtualizaGuarnicao').find('#atualizasalvarGuarnicao').prop('disabled', false);
	}, 150);

}

$(document).on('click', '#atualizasalvarGuarnicao', function () {
    const $modal = $('#modalAtualizaGuarnicao');
    
    const idGuarnicao = $modal.find('#atualizaidGuarnicao').val();
    const nomeGuarnicao = $modal.find('#atualizanomeGuarnicao').val();
    const meiosDeslocamento = $modal.find('#atualizameiosDeslocamento').val() || [];
    const integrantes = $modal.find('#atualizaselectIntegrantes').val() || [];
    const idUsuarioResponsavel = $modal.find('#atualizaresponsavelGuarnicao').val();

    console.log("Salvar:", {
        idGuarnicao,
        nomeGuarnicao,
        meiosDeslocamento,
        integrantes,
        idUsuarioResponsavel
    });

    atualizarGuarnicao(idGuarnicao, nomeGuarnicao, meiosDeslocamento, integrantes, idUsuarioResponsavel);
});


async function listarIntegrantesGuarnicaoComRetonro() {
    const idGrupo = 44;
    try {
        return await $.ajax({
            url: '/MuralhaDigital/Guarnicao',
            type: 'GET',
            data: { acao: 'listarIntegrantes', idGrupo },
            dataType: 'json'
        });
    } catch {
        console.error('Erro ao buscar integrantes');
        return [];
    }
}

function atualizarGuarnicao(idGuarnicao, nomeGuarnicao, meiosDeslocamento, integrantes, idUsuarioResponsavel) {
const idResponsavel = idUsuarioResponsavel.toString();

    if (!integrantes.includes(idResponsavel)) {
		alert('O responsável deve estar incluído entre os integrantes.');
		return;
    } else if (meiosDeslocamento.length <= 0) {
		alert('Deve conter ao menos um meio de deslocamento.');
		return;
    }

    const dadosGuarnicao = {
        idGuarnicao,
        nomeGuarnicao,
        meiosDeslocamento,
        integrantes,
        idUsuarioResponsavel,
        idUsuarioCriacao: usuarioID
    };

    $.ajax({
        url: '/MuralhaDigital/Guarnicao',
        type: 'POST',
        data: {
            acao: 'atualizarGuarnicao',
            nome: dadosGuarnicao.nomeGuarnicao,
            idGuarnicao: dadosGuarnicao.idGuarnicao,
            meiosDeslocamento: dadosGuarnicao.meiosDeslocamento.join(','),
            integrantes: dadosGuarnicao.integrantes.join(','),
            idUsuarioResponsavel: dadosGuarnicao.idUsuarioResponsavel,
            idUsuarioAlteracao: dadosGuarnicao.idUsuarioCriacao
        },
        dataType: 'json',
        success: function(response) {
            showMessage(response.message, response.success);
            if (response.success) 
            $('.selectpicker').selectpicker('refresh');
            setTimeout(() => location.reload(), 100);
        },
        error: function(xhr) {
            const msg = xhr.responseJSON?.message || xhr.responseText || 'Erro ao conectar com o servidor.';
            console.error("Erro AJAX:", xhr.statusText, xhr.responseText);
            showMessage(`Erro: ${msg}`, false);
            setTimeout(() => location.reload(), 100);
        }
    });
}