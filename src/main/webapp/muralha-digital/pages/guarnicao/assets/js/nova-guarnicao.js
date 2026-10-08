$(document).ready(function () {
	$('.selectpicker').selectpicker();
    $('#formGuarnicao').submit(function (event) {
        event.preventDefault();
    });
    listarUsuariosGuarnicao();

    $('#btnFecharTelefone').on('click', function () {
        console.log("clicou aqui")
        const modalTelefone = bootstrap.Modal.getInstance(document.getElementById('modalTelefone'));
        modalTelefone.hide();
    
        setTimeout(() => {
            const modalCriar = bootstrap.Modal.getOrCreateInstance(document.getElementById('modalCriarGuarnicao'));
            modalCriar.show();
        }, 400);
    });

    $('#salvarTelefone').on('click', function () {
        const idUsuario = $('#modalTelefone').data('idUsuario');
        const telefone = $('#inputTelefoneUsuario').val().trim();

        if (!telefone) {
            showMessage("Informe um telefone válido.", false);
            return;
        }

        $.ajax({
            url: '/MuralhaDigital/Guarnicao',
            type: 'POST',
            data: {
                acao: 'atualizarTelefone',
                idUsuario: idUsuario,
                telefone: telefone
            },
            dataType: 'json',
            success: function (response) {
                showMessage("Telefone salvo com sucesso.", true);

                const modalTelefone = bootstrap.Modal.getInstance(document.getElementById('modalTelefone'));
                modalTelefone.hide();

                setTimeout(() => {
                    const modalCriar = bootstrap.Modal.getOrCreateInstance(document.getElementById('modalCriarGuarnicao'));
                    modalCriar.show();
                    listarUsuariosGuarnicao(); 
                }, 400);
            },
            error: function (xhr, status, error) {
                let errorMessage = 'Erro ao atualizar telefone.';
                if (xhr.responseJSON && xhr.responseJSON.erro) {
                    errorMessage = xhr.responseJSON.erro;
                }
                showMessage(errorMessage, false);
            }
        });
    });

    
});

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
}

function salvarGuarnicao() {
    const nomeGuarnicaoValue = $('#nomeGuarnicao').val();
    const meiosDeslocamento = $('#meiosDeslocamento').val() || []; 
    const integrantes = $('#selectIntegrantes').val() || [];
    const idUsuarioResponsavel = $('#responsavelGuarnicao').val();
    
    if(meiosDeslocamento.length <= 0){
		$('#erroMeiosDeslocamento').text('O responsável deve estar incluído entre os integrantes.').show();
		return
	} else {
		$('#erroMeiosDeslocamento').hide(); 
	}

	if (!integrantes.includes(idUsuarioResponsavel)) {
	    $('#erroIntegrantes').text('O responsável deve estar incluído entre os integrantes.').show();
	    return;
	} else {
	    $('#erroIntegrantes').hide(); 
	}

    const dadosGuarnicao = {
        nomeGuarnicao: nomeGuarnicaoValue,
        meiosDeslocamento: meiosDeslocamento,
        integrantes: integrantes,
        idUsuarioResponsavel: idUsuarioResponsavel,
        idUsuarioCriacao: usuarioID,
        disponivel: 1
    };

    $.ajax({
        url: '/MuralhaDigital/Guarnicao',
        type: 'POST',
        data: {
            acao: 'cadastrar',
            nome: dadosGuarnicao.nomeGuarnicao,
            meiosDeslocamento: dadosGuarnicao.meiosDeslocamento.join(','), 
            integrantes: dadosGuarnicao.integrantes.join(','),
            id_usuario_responsavel: dadosGuarnicao.idUsuarioResponsavel,
            id_usuario_criacao: dadosGuarnicao.idUsuarioCriacao,
            disponivel: dadosGuarnicao.disponivel
        },
        dataType: 'json',
        success: function(response) {
            if (response.success) {
                showMessage(response.message, true);
                $('#formGuarnicao')[0].reset();
                $('.selectpicker').selectpicker('refresh'); 
            } else {
                showMessage(response.message, false);
            }
            setTimeout(() => location.reload(), 100);
        },
        error: function(xhr, status, error) {
            let errorMessage = 'Erro ao conectar com o servidor.';
            if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMessage = xhr.responseJSON.message;
            } else if (xhr.responseText) {
                errorMessage = xhr.responseText;
            }
            console.error("Erro AJAX:", status, error, xhr.responseText);
            showMessage(`Erro: ${errorMessage}`, false);
            setTimeout(() => location.reload(), 100);
        }
    });
}

function listarUsuariosGuarnicao() {
    const idGrupo = 44;

    $.ajax({
        url: '/MuralhaDigital/Guarnicao',
        type: 'GET',
        data: {
            acao: 'listarIntegrantes',
            idGrupo: idGrupo
        },
        dataType: 'json',
        success: function (usuarios) {
            const selectResponsavel = $('#responsavelGuarnicao');
            const selectIntegrantes = $('#selectIntegrantes');

            selectResponsavel.empty();
            selectIntegrantes.empty();

            if (!usuarios || usuarios.length === 0) {
                const msg = '<option disabled selected>Nenhum usuário encontrado</option>';
                selectResponsavel.append(msg);
                selectIntegrantes.append(msg);
                selectIntegrantes.selectpicker('refresh');
                return;
            }

            selectResponsavel.append('<option disabled selected>Selecione o responsável</option>');

            usuarios.forEach(function (usuario) {
                const hasTelefone = !!usuario.telefone;
                const telefone = hasTelefone ? ` - ${usuario.telefone}` : '       [adicione um telefone]';
                const telefoneDisplay = hasTelefone ? ` - ${usuario.telefone}` : '';
                const nomeDisplay = `${usuario.nome}${telefoneDisplay}`;
            
                const optionResponsavel = $('<option></option>')
                    .val(usuario.id)
                    .text(`${usuario.nome} (${usuario.usuario})`);
            
                const optionIntegrante = $('<option></option>')
                    .val(usuario.id)
                    .text(nomeDisplay)
                    .attr('data-id', usuario.id);
            
                if (!hasTelefone) {
                    optionIntegrante.attr('data-missing-phone', 'true');
                }
            
                selectResponsavel.append(optionResponsavel);
                selectIntegrantes.append(optionIntegrante);
            });

            selectIntegrantes.selectpicker('refresh');

            $('#selectIntegrantes').on('changed.bs.select', function (e, clickedIndex) {
                const $clickedOption = $(this).find('option').eq(clickedIndex);
                const isMissingPhone = $clickedOption.attr('data-missing-phone') === 'true';
                const idUsuario = $clickedOption.val();
                const nomeUsuario = $clickedOption.text();
            
                if (isMissingPhone) {
                    // Fecha o modal principal antes de abrir o de telefone
                    document.activeElement.blur();

                    const modalCriar = bootstrap.Modal.getInstance(document.getElementById('modalCriarGuarnicao'));
                    modalCriar.hide();
            
                    setTimeout(() => {
                        $('#modalTelefone').find('#nomeUsuarioTelefone').text(nomeUsuario);
                        $('#modalTelefone').data('idUsuario', idUsuario);
                        const modalTelefone = document.getElementById('modalTelefone');
                         const modal = new bootstrap.Modal(modalTelefone, {
					        backdrop: 'static',
					        keyboard: false
					    });
                        modal.show();
                    }, 300); 
                }
            });
            
        },
        error: function (xhr, status, error) {
            console.error('Erro ao buscar usuários:', error);

            $('#responsavel')
                .append('<option disabled selected>Erro ao carregar responsáveis</option>');

            $('#selectIntegrantes')
                .append('<option disabled selected>Erro ao carregar integrantes</option>')
                .selectpicker('refresh');
        }
    });
}

$('#salvarGuarnicao').on('click', function () {
    $('#erroIntegrantes').hide(); 
    $('#erroMeiosDeslocamento').hide();
    salvarGuarnicao();
});