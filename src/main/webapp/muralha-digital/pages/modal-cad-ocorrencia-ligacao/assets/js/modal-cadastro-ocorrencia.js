let modal;

$(document).ready(function (){	
	const modalElement = document.getElementById("modalCadastrarOcorrencia")
	modal = new bootstrap.Modal(modalElement, {
	backdrop: 'static',
	keyboard: false
	});	
	
	CarregarComponenteDataHoraModal();
	carregarTipos();
	carregarCidades();
	carregarTiposOcorrencias();
	
	//Formatação de máscara do CPF
	$('#cpf').mask('000.000.000-00');
	
	//Aplica o select2 nas tags select
	$('select').select2({
	  theme: 'bootstrap4',
      dropdownParent: $('#modalCadastrarOcorrencia'),
      width: '100%',
      placeholder: "Selecione o tipo da ocorrência",      
    });
});

function abrirModal() {
  	 var myModal = document.getElementById("modalCadastrarOcorrencia");
		const modal = new bootstrap.Modal(myModal, {
        backdrop: 'static',
        keyboard: false
    });
    modal.show();
}

function fecharModal() {
	if(modal)
		modal.hide();
}

function limparCampos(){
	//Limpa os campos de input da modal
	document.getElementById('formModalCadastrarOcorrencia').reset();
	
	//Limpa os campos do select
	$('#formModalCadastrarOcorrencia select').each(function () {
        $(this).val(0).trigger('change');
    });
    
    // Remove bordas vermelhas
    document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));

    // Remove mensagens de erro
    document.querySelectorAll('.error-message').forEach(el => el.remove());
}

//Busca os tipos de solicitantes de ocorrência 
function carregarTipos() {
    try {
        $.ajax({
            type: "GET",
            url: "/MuralhaDigital/OcorrenciaLigacao",
            dataType: "xml",
            data: "acao=obterTiposSolicitante",
            success: function (data) {
  
                const $select = $("#selSolicitante");     
                
                $(data).find("solicitantes").each(function () {
		        const id = $(this).find("id").text();
		        const descricao = $(this).find("descricao").text();
		
		        $select.append($("<option>").val(id).text(descricao));
		    	});
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erro ao obter alertas:", errorThrown);
            }
        });
    } catch (e) {
        console.error("Erro interno na função carregarTipos:", e);
    }
}

//Filtra somente por cidades com idEstado 13
function carregarCidades() {
    try {
        $.ajax({
            type: "GET",
            url: "/MuralhaDigital/CidadesMinasGerais",
            dataType: "xml",
            data: "acao=buscarCidades",
            success: function (data) {		
  
                const $select = $("#selCidades");     
                
                $(data).find("cidades").each(function () {
		        const id = $(this).find("id").text();
		        const nomeCidade = $(this).find("nome").text();
		        $select.append($("<option>").val(id).text(nomeCidade));
		    	});
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erro ao obter cidades:", errorThrown);
            }
        });
    } catch (e) {
        console.error("Erro interno na função carregarCidades:", e);
    }
}

//Busca os tipos de ocorrências para cadastro
function carregarTiposOcorrencias() {
    try {
        $.ajax({
            type: "GET",
            url: "/MuralhaDigital/OcorrenciaLigacao",
            dataType: "xml",
            data: "acao=obterTiposOcorrencias",
            success: function (data) {	
  
                const $select = $("#selTipoOcorrencia");     
                
                $(data).find("tiposOcorrencias").each(function () {
		        const id = $(this).find("id").text();
		        const descricao = $(this).find("descricao").text();
		
		        $select.append($("<option>").val(id).text(descricao));
		    	});
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erro ao obter tiposOcorrencias:", errorThrown);
            }
        });
    } catch (e) {
        console.error("Erro interno na função carregarTiposOcorrencias:", e);
    }
}

function cadastrarOcorrenciaLigacao(){
	const form = document.getElementById('formModalCadastrarOcorrencia');
	const formData = new FormData(form);	

	const valido = validarCampos(formData);
	
	if (valido) {	
		const dadosOcorrencia = Object.fromEntries(formData.entries());
		
		dadosOcorrencia.dataHoraOcorrido = formatarDataParaISO(dadosOcorrencia.dataHoraOcorrido); 
		dadosOcorrencia.cpf = dadosOcorrencia.cpf.replace(/\D/g, '');
		
		try {
			$.ajax({
				type: "POST",
				
				url: "/MuralhaDigital/OcorrenciaLigacao",
				data: {
					acao: "cadastrarOcorrencia",
			        dataHoraOcorrido: dadosOcorrencia.dataHoraOcorrido,
			        idTipoSolicitante: dadosOcorrencia.selSolicitante,
			        nomeSolicitante: dadosOcorrencia.nomeSolicitante,
			        cpfSolicitante: dadosOcorrencia.cpf,
			        idTipoOcorrencia: dadosOcorrencia.selTipoOcorrencia,
			        idCidade: dadosOcorrencia.selCidades,
			        bairro: dadosOcorrencia.bairro,
			        rua: dadosOcorrencia.rua,
			        numero: dadosOcorrencia.numero,
			        complemento: dadosOcorrencia.complemento,
			        nomeVitima: dadosOcorrencia.nomeVitima,
			        detalhamento: dadosOcorrencia.observacoes,
			        existeArmaEnvolvida: dadosOcorrencia.selEnvolvArmas
			    },			    
				success: function(response) {				 	
				    Swal.fire({
				        icon: 'success',
				        title: 'Sucesso!',
				        text: 'Cadastro realizado com sucesso.',
				        timer: 3000,
				        showConfirmButton: false
				    });
				},
				error: function(jqXHR, textStatus, errorThrown) {					
				    Swal.fire({
				        icon: 'error',
				        title: 'Erro',
				        text: 'Falha ao cadastrar a ocorrência: ' + errorThrown,
				    });
				}
			});
		}catch (e) {
			console.error("Erro interno:", e);
		}
	}
}

function validarCampos(formData) {
    const camposObrigatorios = [
        'selSolicitante', 'cpf', 'nomeSolicitante', 'nomeVitima',
        'selEnvolvArmas', 'dataHoraOcorrido', 'selTipoOcorrencia',
        'observacoes', 'selCidades', 'bairro', 'rua','numero','complemento'
    ];

    // Limpa erros anteriores
    document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
    document.querySelectorAll('.error-message').forEach(el => el.remove());

    let primeiroCampoInvalido = null;

    for (const campo of camposObrigatorios) {
        const valor = formData.get(campo);
        const input = document.querySelector(`[name="${campo}"]`);

        const invalido = !valor || valor === "0";

        if (invalido && input) {
            // Marca o campo como inválido
            if (input.classList.contains('select2-hidden-accessible')) {
                // Trata SELECT2
                const container = $(input).next('.select2-container').find('.select2-selection');
                container.addClass('input-error');

                if (!container.parent().find('.error-message').length) {
                    const erro = $('<div class="error-message">Campo obrigatório</div>');
                    container.parent().append(erro);
                }
            } else if (campo === 'dataHoraOcorrido') {
                // Trata o campo com input-group
                const grupoData = document.getElementById('grupoDataInicioModal');
                grupoData.classList.add('input-error');

                if (!grupoData.parentNode.querySelector('.error-message')) {
                    const erro = document.createElement('div');
                    erro.className = 'error-message';
                    erro.innerText = 'Campo obrigatório';
                    grupoData.parentNode.appendChild(erro);
                }
            } else {
                // Campos normais
                input.classList.add('input-error');

                if (!input.parentNode.querySelector('.error-message')) {
                    const erro = document.createElement('div');
                    erro.className = 'error-message';
                    erro.innerText = 'Campo obrigatório';
                    input.parentNode.appendChild(erro);
                }
            }

            // Salva o primeiro campo inválido para dar foco depois
            if (!primeiroCampoInvalido) {
                primeiroCampoInvalido = input;
            }
        }
    }

    if (primeiroCampoInvalido) {
        primeiroCampoInvalido.scrollIntoView({ behavior: 'smooth', block: 'center' });
        return false;
    }
    
    return true;
}

function formatarDataParaISO(dataBr) {
	const [data, hora] = dataBr.split(' ');
	const [dia, mes, ano] = data.split('/');
	return `${ano}-${mes}-${dia} ${hora}:00`;
}