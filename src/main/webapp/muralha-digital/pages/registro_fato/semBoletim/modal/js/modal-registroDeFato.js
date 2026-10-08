let contadorEnvolvidos = 1;
let contadorVeiculos = 1;
let contadorObjetos = 1;
let tiposRegistrosFatos = [];
let tiposIndividuos = [];
let tiposAlertas = [];
let gruposDisponiveis = [];
let usuariosDisponiveis = [];
let envolvidosOriginais = [];
let veiculosOriginais = [];
let objetosOriginais = [];
let gruposOriginais = [];
let usuariosOriginais = [];
let registroFatoId;
let enderecoId;
let marker;
let latitude;
let longitude;

$(document).ready(function (){	
	
	CarregarComponenteDataHoraModal(true);
	aplicarEstilosSelect();
		
	obterTiposRegistrosDeFatos();
	obterTiposIndividuos();
	obterTiposEnderecoEvento();		
	ObterTiposAlertasOcorrencias();	
	obterGruposSemBoletim();
	
	$('#modalCadastrarFato').on('shown.bs.modal', function() {
        $('#tab1-tab').click();
    });
	
	//Formatação de máscara do CPF
	$('#cpf').mask('000.000.000-00');  
    
    document.querySelectorAll('[data-bs-toggle="tab"]').forEach(tab => {
	  tab.addEventListener('shown.bs.tab', () => {
	    verificaAbaEMapa();
	  });
	});
		
	window.addEventListener('load', verificaAbaEMapa);

    $('#liTab6').addClass('d-none');

    $('#selPrivado').on('change', function() {
        const valorSelecionado = $(this).val();

        if (valorSelecionado === '1') { // O valor "1" corresponde a "Sim"
            // Se for "Sim", remova a classe d-none para mostrar a aba
            $('#liTab6').removeClass('d-none');
			popularSelectComOpcoes('selGrupos', gruposDisponiveis);
			popularSelectComOpcoes('selUsuarios', usuariosDisponiveis);
        } else {
            // Se for "Não", adicione a classe d-none para esconder a aba
            $('#liTab6').addClass('d-none');

            // Se a aba de grupos estiver ativa, mude para a primeira aba
            if ($('#tab6-tab').hasClass('active')) {
                $('#tab1-tab').tab('show'); // use a função .tab('show') do jQuery Bootstrap
            }
        }
    });
	
	let debounceTimeout;
	 $(document).on('change', '#selCidades', function () {
    	atualizarPosicaoMapa(parseInt(this.value));
	});
	
	const inputCep = document.getElementById('cep');
		
	inputCep.addEventListener('input', () => {						
		
	    clearTimeout(debounceTimeout);
	    debounceTimeout = setTimeout(() => {

	        buscarEnderecoPorCep();
	    }, 2000); 
	});
});

function aplicarEstilosSelect(){
	$('#selAtendimento').select2({
			theme: 'bootstrap4',
	        dropdownParent: $('#modalCadastrarFato'),
			placeholder: 'Selecione...'
	    });
		
	$('#selPrivado').select2({
			theme: 'bootstrap4',
	        dropdownParent: $('#modalCadastrarFato'),
			placeholder: 'Selecione...',
	    });
			
	$('#selEnvolvArmas').select2({
			theme: 'bootstrap4',
	        dropdownParent: $('#modalCadastrarFato'),
			placeholder: 'Selecione...',
	    });
				
	$('#selTipo').select2({
			theme: 'bootstrap4',
	        dropdownParent: $('#modalCadastrarFato'),
			placeholder: 'Selecione...',
	    });
						
	$('#selCidades').select2({
			theme: 'bootstrap4',
	        dropdownParent: $('#modalCadastrarFato'),
			placeholder: 'Selecione...',
	    });
		
	$('#selTipoEvento').select2({
			theme: 'bootstrap4',
	        dropdownParent: $('#modalCadastrarFato'),
			placeholder: 'Local do evento',
	    });
		
	$('#selGrupos').select2({
	        dropdownParent: $('#modalCadastrarFato'),
			placeholder: 'Selecione...',
	    });
	
	$('#selUsuarios').select2({
	        dropdownParent: $('#modalCadastrarFato'),
			placeholder: 'Selecione...',
	    });
}

function obterTiposRegistrosDeFatos() {
	var urlPesquisa = "/MuralhaDigital/RegistroDeFato/Tipo";

	return $.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				const parser = new DOMParser();
				const xmlDoc = parser.parseFromString(jqXHR.responseText, "application/xml");
				const tipos = xmlDoc.getElementsByTagName('RegistroDeFatoTipo');
				const select = document.getElementById('selTipo');
				// Adiciona cada tipo na ordem recebida
				for (let i = 0; i < tipos.length; i++) {
					const tipo = tipos[i];
					const id = tipo.getElementsByTagName('id')[0].textContent;
					const descricao = tipo.getElementsByTagName('descricao')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
				}
				tiposRegistrosFatos = tipos;
			} else {
				ErrorNotification(msgResposta, "");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading");
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}

function obterTiposIndividuos() {
	var urlPesquisa = "/MuralhaDigital/RegistroDeFato/IndividuoTipo?acao=obterLista";

	return $.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				const parser = new DOMParser();
				const xmlDoc = parser.parseFromString(jqXHR.responseText, "application/xml");
				const tipos = xmlDoc.getElementsByTagName('RegistroDeFato');
				tiposIndividuos = tipos;
			} else {
				ErrorNotification(msgResposta, "");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading");
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}

function obterTiposEnderecoEvento() {
	var urlPesquisa = "/MuralhaDigital/RegistroDeFato/EnderecoEvento";

	return $.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				const parser = new DOMParser();
				const xmlDoc = parser.parseFromString(jqXHR.responseText, "application/xml");
				const tipos = xmlDoc.getElementsByTagName('RegistroDeFatoEnderecoEvento');
				const select = document.getElementById(`selTipoEvento`);
				for (let i = 0; i < tipos.length; i++) {
						const tipo = tipos[i];
						const id = tipo.getElementsByTagName('id')[0].textContent;
						const descricao = tipo.getElementsByTagName('descricao')[0].textContent;
	
						const option = document.createElement('option');
						option.value = id;
						option.textContent = descricao;
						select.appendChild(option);
					}	
			} else {
				ErrorNotification(msgResposta, "");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading");
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}

function adicionarEnvolvido() {
	const container = document.getElementById('envolvidosContainer');
	const index = contadorEnvolvidos;

	const novoHTML = `
		<div class="card mb-2">
			<div class="card-header" data-bs-toggle="collapse" href="#envolvido${index}" style="cursor: pointer;">
				Envolvido #${index}
			</div>
			<div id="envolvido${index}" class="collapse show">
				<div class="card-body">
					<div class="row">
						<div class="mb-3 col-md-5">
							<label>Nome:</label>
							<input class="form-control" name="envolvidos[${index}].nome" id="nomeEnvolvido${index}" placeholder="Ex.: João de Souza" maxlength="255">
						</div>
						<div class="mb-3 col-md-3">
							<label>CPF:</label>
							<input class="form-control cpf-mask" name="envolvidos[${index}].cpf" type="text" id="cpf${index}" placeholder="Ex.: 123.456.789-11" maxlength="14">
						</div>
						<div class="mb-3 col-md-4">
							<label>Tipo de Envolvimento:</label>
							<select class="form-select select-tipo" name="envolvidos[${index}].tipoEnvolvimento" id="selTipoEnvolvimento${index}" data-minimum-results-for-search="8">
								<option value="">Obrigatório</option>
							</select>
						</div>
					</div>
					<div class="row">
						<div class="mb-3 col-md-8">
							<label><strong>E-mail:</strong></label>
							<input class="form-control" type="email" placeholder="Ex.: joaodesouza@gmail.com" id="email${index}" name="envolvidos[${index}].email" maxlength="255">
						</div>
						<div class="mb-3 col-md-4">
							<label><strong>DDD + Telefone:</strong></label>
							<div class="d-flex gap-2">
								<div style="max-width: 70px; flex-shrink: 0;" class="position-relative">
									<input class="form-control" type="tel" placeholder="DDD" id="ddd${index}" name="envolvidos[${index}].ddd" maxlength="2">
									<div class="invalid-feedback">
										Informe o DDD
									</div>
								</div>
								<div class="flex-grow-1 position-relative">
									<input class="form-control" type="tel" placeholder="Ex.: 912345678" id="telefone${index}" name="envolvidos[${index}].telefone" maxlength="9">
									<div class="invalid-feedback">
										Informe o telefone
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="mb-3">
						<label><strong>Detalhamento:</strong></label>
						<textarea class="form-control" rows="4" id="detalhamento${index}" name="envolvidos[${index}].detalhamento" placeholder="Detalhamento do envolvimento do(s) indivíduo(s)"></textarea>
					</div>
					<button type="button" class="btn btn-sm btn-danger" style="margin-top: 12px" onclick="removerEnvolvido(this)">Remover</button>
				</div>
			</div>
		</div>
	`;

	container.insertAdjacentHTML('beforeend', novoHTML);

	// Reaplica select2 ao novo select
	$(`#envolvido${index} .select-tipo`).select2({
		theme: 'bootstrap4',
		dropdownParent: $('#modalCadastrarFato'),
		width: '100%',
		placeholder: "Selecione o tipo de envolvimento"
	});

	// Aplica a máscara de CPF
	$(`#envolvido${index} .cpf-mask`).mask('000.000.000-00');

	// Preenche opções do select
	preencherTipoEnvolvimento(index);

	contadorEnvolvidos++;
}

function preencherTipoEnvolvimento(index){	
	const select = document.getElementById(`selTipoEnvolvimento${index}`);
	for (let i = 0; i < tiposIndividuos.length; i++) {
					const tipo = tiposIndividuos[i];
					const id = tipo.getElementsByTagName('id')[0].textContent;
					const descricao = tipo.getElementsByTagName('descricao')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
				}					
}

function removerEnvolvido(botao) {
	contadorEnvolvidos--;
    botao.closest('.card').remove();
}

function adicionarVeiculo() {
	const container = document.getElementById('veiculosContainer');
	const index = contadorVeiculos;
	
	const dataId = `dataMonitorado${index}`;

	const novoHTML = `
		<div class="card mb-2">
			<div class="card-header" data-bs-toggle="collapse" href="#veiculo${index}" style="cursor: pointer;">
				Veículo #${index}
			</div>
			<div id="veiculo${index}" class="collapse show">
				<div class="card-body">
					<div class="row">
						<div class="mb-3 col-md-4">
							<label for="placaVeiculo${index}"><strong>Placa:</strong></label>
							<input class="form-control" type="text" placeholder="Ex.: ABC0123"
								name="veiculos[${index}].placa" id="placaVeiculo${index}" maxlength="7">
						</div>
						<div class="mb-3 col-md-4">
							<label for="corVeiculo${index}"><strong>Cor:</strong></label>
							<input class="form-control" type="text" placeholder="Ex.: BRANCA"
								name="veiculos[${index}].cor" id="corVeiculo${index}" maxlength="50">
						</div>
						<div class="mb-3 col-md-4">
							<label for="marcaVeiculo${index}"><strong>Marca:</strong></label>
							<input class="form-control" type="text" placeholder="Ex.: RENAULT"
								name="veiculos[${index}].marca" id="marcaVeiculo${index}" maxlength="50">
						</div>
					</div>
					<div class="row">
						<div class="mb-3 col-md-12">
							<label for="modeloVeiculo${index}"><strong>Modelo:</strong></label>
							<textarea class="form-control" rows="4"
								placeholder="Ex.: DUSTER Intense Plus 1.6 CVT Modelo 2026 Branca"
								name="veiculos[${index}].modelo" id="modeloVeiculo${index}" maxlength="255"></textarea>
						</div>
					</div>
					<div class="col-md-6 mb-2">
						<div class="form-check form-check-inline">
							<label class="form-check-label" for="cadastrarMonitorado${index}">Cadastrar como Veículo Monitorado</label>
							<input class="form-check-input toggle-extra" type="checkbox" id="cadastrarMonitoradoValue${index}">							
						</div>
					</div>
					<div class="row extra-fields d-none">
						<div class="mb-3 col-md-6">
							<label for="data"><strong>Data Início:</strong></label>
							<div class="input-group" id="${dataId}">
								<input name="veiculos[${index}].dataInicio" id="${dataId}.dataInicio" type="text" class="form-control" readonly/>
									<span class="input-group-text" data-td-target="#${dataId}" data-td-toggle="datetimepicker">
										<span class="fas fa-calendar"></span>
									</span>
							</div>
						</div>
						<div class="mb-3 col-md-6">
							<label for="tipoAlerta"><strong>Tipo Alerta:</strong></label>
							<select class="form-select" name="alertas[${index}].tipo" id="alertas${index}">
								<option value="">Selecione...</option>
							</select>
						</div>
						<div class="mb-3 col-md-12">
							<label for="modeloVeiculo${index}"><strong>Descrição:</strong></label>
							<textarea class="form-control" rows="4"
								placeholder="Ex.: O veículo foi roubado em plena luz do dia."
								name="veiculos[${index}].descricaoMonitorado" id="descricaoMonitorado${index}" maxlength="255"></textarea>
						</div>
					</div>
					<button type="button" class="btn btn-sm btn-danger" style="margin-top: 12px" onclick="removerVeiculo(this)">Remover</button>
				</div>
			</div>
		</div>
	`;

	container.insertAdjacentHTML('beforeend', novoHTML);
	
	new tempusDominus.TempusDominus(document.getElementById(dataId), {
		localization: {
			locale: 'pt-BR'
		}
	});

	// Captura o checkbox recém-criado e adiciona evento para exibir/ocultar campos extras
	const card = container.lastElementChild;
	const checkbox = card.querySelector('.toggle-extra');
	const extraFields = card.querySelector('.extra-fields');

	checkbox.addEventListener('change', function () {
		if (this.checked) {
			extraFields.classList.remove('d-none');
		} else {
			extraFields.classList.add('d-none');
		}
	});
	
	preencherTiposAlerta(index);

	contadorVeiculos++;
}

function preencherTiposAlerta(index){	
	const select = document.getElementById(`alertas${index}`);	
	for (let i = 0; i < tiposAlertas.length; i++) {
					const tipo = tiposAlertas[i];
					const id = tipo.getElementsByTagName('id')[0].textContent;
					const descricao = tipo.getElementsByTagName('tipo')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
			}	
}

function removerVeiculo(botao) {
	botao.closest('.card').remove();
	contadorVeiculos--;
}

function adicionarObjeto() {
	const container = document.getElementById('objetosContainer');
	const index = contadorObjetos;

	const novoHTML = `
		<div class="card mb-3">
			<div class="card-header" data-bs-toggle="collapse" href="#objeto${index}" style="cursor: pointer;">
				Objeto #${index}
			</div>
			<div id="objeto${index}" class="collapse show">
				<div class="card-body">
					<div class="row">
						<div class="mb-3 col-md-12">
							<label for="tipoObjeto${index}"><strong>Tipo:</strong></label>
							<select class="form-select" name="objetos[${index}].tipo" id="tipoObjeto${index}">
								<option value="Celular">Celular</option>
								<option value="Carteira">Carteira</option>
								<option value="Bolsa">Bolsa</option>
								<option value="Joias">Jóias</option>
								<option value="Relogios">Relógios</option>
								<option value="Eletronicos">Eletrônicos (Notebook, tablet, câmera, etc.)</option>
								<option value="Chaves">Chaves</option>
								<option value="Oculos">Óculos</option>
								<option value="Armas">Armas</option>
								<option value="Municoes">Munições</option>
								<option value="Explosivos">Explosivos</option>
								<option value="Medicamentos">Medicamentos</option>
							</select>
						</div>
					</div>
					<div class="row">
						<div class="mb-3 col-md-12">
							<label for="descricaoObjeto${index}"><strong>Descrição:</strong></label>
							<textarea class="form-control" rows="4"
								placeholder="Ex.: A vítima teve seu celular roubado durante um assalto."
								name="objetos[${index}].descricao" id="descricaoObjeto${index}"></textarea>
						</div>
					</div>
					<button type="button" class="btn btn-sm btn-danger" style="margin-top: 12px" onclick="removerObjeto(this)">Remover</button>
				</div>
			</div>
		</div>
	`;

	container.insertAdjacentHTML('beforeend', novoHTML);
	contadorObjetos++;
}

function removerObjeto(botao) {
	const card = botao.closest('.card');
	card.remove();
}

function cadastrarCompleto(){
	
	const valido = validarCampos();
	
	if(!valido){
		return;
	}
	
	const envolvidos = capturarEnvolvidos();
	const veiculos = capturarVeiculos();
	const objetos = capturarObjetos();
	const grupos = capturarGrupos();
	const usuarios = capturarUsuarios();
	const dataHoraOcorrido = document.getElementById("dataInicioModal").value; 
	const dataHoraConvertida = formatarDataParaISO(dataHoraOcorrido); 
	const cep = document.getElementById("cep").value.replace("-","");
	
	if(envolvidos.length < 1){
		alert("É obrigatório cadastrar no mínimo um envolvido.")
		return;
	}
	
	const dados = {
		registroFato: {
			atendimentoPermitido: parseInt(document.getElementById("selAtendimento").value),
			envolvimentoArmas: parseInt(document.getElementById("selEnvolvArmas").value),
			privado: parseInt(document.getElementById("selPrivado").value),
			tipoRegistro: parseInt(document.getElementById("selTipo").value),
			dataHoraOcorrido: dataHoraConvertida,
			detalhamentoFato: document.getElementById("observacoes").value || '',
			idsGrupos: grupos,
			idsUsuarios: usuarios
		},
		localizacao: {
			cidadeId: parseInt(document.getElementById("selCidades").value),
			tipoEnderecoEvento: 1,
			cep: cep,
			bairro: document.getElementById("bairro").value,
			rua: document.getElementById("rua").value,
			complemento: document.getElementById("complemento").value,
			numero: document.getElementById("numeroRua").value,
			latitude: parseFloat(document.getElementById("lat").value),
			longitude: parseFloat(document.getElementById("long").value)
		},
		envolvidos,
		veiculos,
		objetos,
	};
		
	url = `/MuralhaDigital/RegistroDeFato?acao=cadastrarSemBoletim`
	
	$.ajax({
		type: "POST",
		url: url,
		contentType: "application/json",
		dataType: "xml",
		data: JSON.stringify(dados),
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				alert("Cadastro realizado com sucesso!")
				const placas = [];		
				dados.veiculos.forEach(veiculo => {
					if(veiculo.cadastrarMonitorado){
						placas.push(veiculo.placa)
					}
				})
				
				if(placas.length > 0){
					const redirecionar = confirm("Gostaria de cadastrar algum desses veículos como supervisionado?")
					
					if(redirecionar){
						const placasParam = encodeURIComponent(placas.join(","));
						window.location.href = "../../monitorado/consulta.jsp?placas="+placasParam;
					}
				}
			} else {
				ErrorNotification(msgResposta, "");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading");
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});	
}

function capturarEnvolvidos() {
    const cards = document.querySelectorAll('#envolvidosContainer .card');
    const envolvidos = [];

    cards.forEach((card) => {
        const envolvido = {
            nome: card.querySelector('[name$="].nome"]')?.value || '',
            cpf: card.querySelector('[name$="].cpf"]')?.value || '',
            tipoEnvolvimento: parseInt(card.querySelector('[name$="].tipoEnvolvimento"]')?.value) || '',
            email: card.querySelector('[name$="].email"]')?.value || '',
            ddd: parseInt(card.querySelector('[name^="envolvidos["][name$="].ddd"]')?.value), 
            telefone: parseInt(card.querySelector('[name$="].telefone"]')?.value),
            detalhamento: card.querySelector('[name$="].detalhamento"]')?.value || ''
        };

        envolvidos.push(envolvido);
    });

    return envolvidos;
}

function capturarVeiculos() {
    const cards = document.querySelectorAll('#veiculosContainer .card');
    const veiculos = [];

    cards.forEach((card) => {
        const veiculo = {
			id: card.querySelector('input[name$=".id"]')?.value || null,
            placa: card.querySelector('[name$="].placa"]')?.value || '',
            cor: card.querySelector('[name$="].cor"]')?.value || '',
            marca: card.querySelector('[name$="].marca"]')?.value || '',
            modelo: card.querySelector('[name$="].modelo"]')?.value || '',
            cadastrarMonitorado: false,
            dataInicio: null,
            idTipoAlertaOcorrencia: null,
            descricao: ''           
        };
        
        // Se o checkbox estiver marcado, captura campos extras
        const checkbox = card.querySelector('.toggle-extra');
        if (checkbox && checkbox.checked) {
            veiculo.cadastrarMonitorado = true;
            veiculo.idTipoAlertaOcorrencia = card.querySelector('[name^="alertas"]')?.value || '';
            veiculo.descricao = card.querySelector('[name$="].descricaoMonitorado"]')?.value || '';       
            var dataInicio = card.querySelector('[name$="].dataInicio"]')?.value || '';
            const dataConvertida = formatarDataParaISO(dataInicio)
            veiculo.dataInicio = dataConvertida;
        }

        veiculos.push(veiculo);
    });

    return veiculos;
}

function capturarObjetos() {
    const cards = document.querySelectorAll('#objetosContainer .card');
    const objetos = [];

    cards.forEach((card) => {
        const objeto = {
            tipo: card.querySelector('[name$="].tipo"]')?.value.toUpperCase() || '',
            descricao: card.querySelector('[name$="].descricao"]')?.value || '',
        };

        objetos.push(objeto);
    });

    return objetos;
}

function capturarGrupos(){
	
	const grupos = document.getElementById("selGrupos");	
	const dadosGrupos = [];
	
	for (let i = 0; i < grupos.options.length; i++) {
	    if (grupos.options[i].selected) {
	        dadosGrupos.push(grupos.options[i].value);
	    }
	}	
	
	return dadosGrupos;
}

function capturarUsuarios(){
	
	const usuarios = document.getElementById("selUsuarios");
	const dadosUsuarios = [];
	
	for (let i = 0; i < usuarios.options.length; i++) {
		if (usuarios.options[i].selected) {
		    dadosUsuarios.push(usuarios.options[i].value);
		}
	}	
	
	return dadosUsuarios;
}

function validarCampos() {
    const formElement = document.getElementById('formModalCadastrarFato');

    if (!formElement) {
        console.error("❌ Formulário não encontrado.");
        return false;
    }

    // Limpa erros anteriores
    document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));
    document.querySelectorAll('.error-message').forEach(el => el.remove());

	const camposComuns = [
	    'selAtendimento', 'selPrivado', 'selEnvolvArmas', 'selTipo', 'dataHoraOcorrido', 'observacoes',
	    'selCidades', 'cep', 'bairro', 'rua', 'lat', 'long', 'numeroRua'
	];
    
    const camposEnvolvidos = ['nome', 'cpf', 'tipoEnvolvimento', 'email', 'ddd', 'telefone', 'detalhamento'];   
    const camposVeiculos = ['placa', 'cor', 'marca', 'modelo'];
	const camposObjetos = ['tipo', 'descricao'];


    const validoComuns = validarCamposObrigatorios(camposComuns);
    const validoEnvolvidos = validarGrupos('#envolvidosContainer', camposEnvolvidos);
    const validoVeiculos = validarGrupos('#veiculosContainer', camposVeiculos);
    const validoObjetos = validarGrupos('#objetosContainer', camposObjetos);

    return validoComuns && validoEnvolvidos && validoVeiculos && validoObjetos;
}

function formatarDataParaISO(dataBr) {
	const dataLimpa = dataBr.replace(',', '');
	const [data, hora] = dataLimpa.split(' ');
	const [dia, mes, ano] = data.split('/');
	return `${ano}-${mes}-${dia} ${hora}:00`;
}

function validarGrupos(containerSelector, camposObrigatoriosPorGrupo) {
    const container = document.querySelector(containerSelector);
    if (!container) return true;
    const grupos = container.children;

    let primeiroCampoInvalido = null;
    let todosValidos = true;

    for (let grupo of grupos) {
        for (const campo of camposObrigatoriosPorGrupo) {
	
            // Busca input dentro do grupo pelo atributo name, que contém o campo, tipo:
            // veiculos[0].placa, veiculos[1].cor, etc.           
            const input = [...grupo.querySelectorAll('input, textarea, select')]
    			.find(i => new RegExp(`\\[\\d+\\]\\.${campo}$`).test(i.name));

            const valor = input?.value;
            const invalido = !valor || valor === "10" || valor === '';

            if (invalido && input) {
                todosValidos = false;

                if (input.classList.contains('select2-hidden-accessible')) {
                    const containerSelect2 = $(input).next('.select2-container').find('.select2-selection');
                    containerSelect2.addClass('input-error');

                    if (!containerSelect2.parent().find('.error-message').length) {
                        const erro = $('<div class="error-message">Campo obrigatório</div>');
                        containerSelect2.parent().append(erro);
                    }
                } else {
                    input.classList.add('input-error');

                    if (!input.parentNode.querySelector('.error-message')) {
                        const erro = document.createElement('div');
                        erro.className = 'error-message';
                        erro.innerText = 'Campo obrigatório';
                        input.parentNode.appendChild(erro);
                    }
                }

                if (!primeiroCampoInvalido) {
                    primeiroCampoInvalido = input;
                }
            }
        }
    }

    if (primeiroCampoInvalido) {
        primeiroCampoInvalido.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    return todosValidos;
}

function validarCamposObrigatorios(campos) {
    let primeiroCampoInvalido = null;
    let todosValidos = true;

    for (const campo of campos) {
        const input = document.querySelector(`[name="${campo}"]`);
        const valor = input?.value;

        const invalido = !valor || valor === "10" || valor === '';

        if (invalido && input) {
            todosValidos = false;

            if (input.classList.contains('select2-hidden-accessible')) {
                const containerSelect2 = $(input).next('.select2-container').find('.select2-selection');
                containerSelect2.addClass('input-error');

                if (!containerSelect2.parent().find('.error-message').length) {
                    const erro = $('<div class="error-message">Campo obrigatório</div>');
                    containerSelect2.parent().append(erro);
                }
            } else {
                input.classList.add('input-error');

                if (!input.parentNode.querySelector('.error-message')) {
                    const erro = document.createElement('div');
                    erro.className = 'error-message';
                    erro.innerText = 'Campo obrigatório';
                    input.parentNode.appendChild(erro);
                }
            }

            if (!primeiroCampoInvalido) {
                primeiroCampoInvalido = input;
            }
        }
    }

    if (primeiroCampoInvalido) {
        primeiroCampoInvalido.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    return todosValidos;
}

function ObterTiposAlertasOcorrencias() {
	var urlPesquisa = "/MuralhaDigital/AlertaOcorrencia/Tipo";

	$.ajax({
		type: "GET",
		url: urlPesquisa,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			const parser = new DOMParser();
			const xmlDoc = parser.parseFromString(event, "application/xml");
			const tiposAlertasLista = xmlDoc.getElementsByTagName("TipoAlertaOcorrencia");
			
			tiposAlertas = tiposAlertasLista;
		},
		error: function(jqXHR, textStatus, errorThrown) {
			AlertCsx_E_TimeOut_8000ms('Erro ao processar requisição ao servidor!!');
		},
		beforeSend: function(jqXHR, settings) { },
		complete: function(jqXHR, textStatus) { }
	});
}

function verificaAbaEMapa() {
  const abaEndereco = document.querySelector('#tab5');
  if (abaEndereco && abaEndereco.classList.contains('active')) {
    setTimeout(() => {
      inicializaOuAtualizaMapa();
    }, 100);
  }
}

function inicializaOuAtualizaMapa() {	
  if (!window.mapaSemBoletim) {	
		window.mapaSemBoletim = L.map('map').setView([-23.55052, -46.633308], 13);
	    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
	      attribution: '&copy; OpenStreetMap contributors'
	    }).addTo(window.mapaSemBoletim);		    	    
		configurarCliqueMapa();		
	}	

	if(latitude != null & longitude != null){
		atualizarPosicaoMapa();
	}
}


function configurarCliqueMapa() {
	if (!window.mapaSemBoletim) return;

	window.mapaSemBoletim.on('click', async function(e) {
		const latlng = e.latlng;

		if (window.marcadorEnderecoSemBoletim) {
			window.marcadorEnderecoSemBoletim.setLatLng(latlng);
		} else {
			window.marcadorEnderecoSemBoletim = L.marker(latlng, { draggable: true }).addTo(window.mapaSemBoletim);

			window.marcadorEnderecoSemBoletim.on('dragend', async function(event) {
				const pos = event.target.getLatLng();
				atualizarLatLngInputs(pos.lat, pos.lng);
				const address = await buscarEnderecoPorLatLng(pos.lat, pos.lng);
				preencherInputsEndereco(address);
			});
		}

		atualizarLatLngInputs(latlng.lat, latlng.lng);
		const address = await buscarEnderecoPorLatLng(latlng.lat, latlng.lng);
		preencherInputsEndereco(address);		
	});
}

function adicionarMarker(coords) {
  // Se já existe um marcador, o remove antes de adicionar um novo
  if (marker) {
    window.mapaSemBoletim.removeLayer(marker);
  }

  // Cria um novo marcador nas coordenadas fornecidas e o adiciona ao mapa
  marker = L.marker(coords).addTo(window.mapaSemBoletim);
}

async function buscarEnderecoPorLatLng(lat, lng) {
	const url = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lng}&addressdetails=1`;

	try {
		const response = await fetch(url, {
			headers: { 'Accept-Language': 'pt-BR' }
		});
		if (!response.ok) throw new Error('Erro no reverse geocoding');
		const data = await response.json();
		return data.address;
	} catch (e) {
		console.error(e);
		return null;
	}
}

function atualizarLatLngInputs(lat, lng) {
	document.getElementById('lat').value = lat.toFixed(6);
	document.getElementById('long').value = lng.toFixed(6);
}

function preencherInputsEndereco(address) {
	if (!address) return;

	document.getElementById('rua').value = address.road || '';
	document.getElementById('bairro').value = address.suburb || address.neighbourhood || '';
	document.getElementById('cep').value = address.postcode || '';

	// Seleciona a cidade no select
	const cidadeSelect = document.getElementById('selCidades');
	const cidadeNome = address.city || address.town || address.village || '';

	if (cidadeNome) {
		const options = cidadeSelect.options;
		let achou = false;
		const cidadeNomeNorm = normalizeString(cidadeNome);
		for (let i = 0; i < options.length; i++) {
			const option = cidadeSelect.options[i];
			const optionTextNorm = normalizeString(option.text);
			if (cidadeNomeNorm == optionTextNorm) {
				cidadeSelect.selectedIndex = i;
				achou = true;
				break;
			}
		}
		if (!achou) {
			cidadeSelect.selectedIndex = 0;
		}
	} else {
		cidadeSelect.selectedIndex = 0;
	}

	// Limpa complemento e número para evitar dados errados
	document.getElementById('complemento').value = '';
	document.getElementById('numeroRua').value = '';
}

function normalizeString(str) {
  return str
    .normalize("NFD")              // separa letras e acentos
    .replace(/[\u0300-\u036f]/g, "") // remove acentos
    .replace(/\s+/g, "")           // remove todos os espaços
    .toLowerCase();                // minúsculas
}

function atualizarPosicaoMapa(idCidade) {
    let coords = null;
         
    if(latitude != null & longitude != null && window.mapaSemBoletim){
		window.mapaSemBoletim.setView([latitude, longitude], 13);
		const coordenadasMarker = [latitude, longitude];
		adicionarMarker(coordenadasMarker);
		return;
	}
    
    switch (idCidade) {
        case 1:
            coords = [-21.38903205756166, -42.69261755828259];
            break;
        case 2:
            coords = [-21.530773545365246, -42.64393279342847];
            break;
        case 3:
            coords = [-21.313761347828674, -42.86540285020945];
            break;
        case 4:
            coords = [-21.416461081219605, -42.815983764463205];
            break;
        case 5:
            coords = [-25.424038547678077, -49.26784184701615];
            break;
        case 6:
            coords = [-23.55086007158461, -46.64542941580277];
            break;
    }

    if (coords && window.mapaSemBoletim) {
        window.mapaSemBoletim.setView(coords, 13);
        return;
    }
}

function buscarEnderecoPorCep() {
    const cep = document.getElementById('cep').value;

    if (!cep) return;

    const url = `https://brasilapi.com.br/api/cep/v2/${cep}`;

    $.ajax({
        type: "GET",
        url: url,
        dataType: "json",
        success: function(data) {
            if(data != null){
				const lat = data.location.coordinates.latitude;
				const long = data.location.coordinates.longitude;
				document.getElementById('bairro').value = data.neighborhood;
				document.getElementById('rua').value = data.street;
				document.getElementById('lat').value = lat;
				document.getElementById('long').value = long;				
				window.mapaSemBoletim.setView([lat, long], 21);
			}
        },
        error: function(err) {
            console.error("Erro ao buscar CEP:", err);
        }
    });
}

function abrirModalPorId(idFato) {
	
	console.log("É esse mesmo")
	
	registroFatoId = null;
	enderecoId = null;
	registroFatoId = idFato;
			
	limparCampos();  
	
    $.ajax({
        url: `/MuralhaDigital/RegistroDeFato?acao=buscarFatoSemBoletimPorId&idRegistroFato=${idFato}`,
        method: 'GET',
        dataType: 'xml',
        success: function (xml) {
            preencherModal(xml);
            desabilitarCampos();
                        
            const myModal = new bootstrap.Modal(document.getElementById('modalCadastrarFato'), {
		        backdrop: 'static',
		        keyboard: false
    		});
    		
    		console.log("btnCadastrarModalRegistroFato:", document.getElementById('btnCadastrarModalRegistroFato'));
    		   		
	        document.getElementById('btnCadastrarModalRegistroFato').style.display = 'none';
	        document.getElementById('btnAtualizar').style.display = 'inline-block';
	        document.getElementById('tituloOutrosEnderecos').style.display = 'inline-block';   
			document.getElementById('tituloTipoEndereco').style.display = 'inline-block';  
			document.getElementById('tituloEnderecoDinamico').style.display = 'inline-block';  
    		   		
    		myModal.show();
        },
        error: function (err) {
            console.error("Erro ao buscar fato:", err);
            Swal.fire("Erro", "Não foi possível carregar as informações.", "error");
        }
    });
}

function preencherModal(xml) {
    const $xml = $(xml);   
	
	envolvidosOriginais = [];
    veiculosOriginais = [];
    objetosOriginais = [];
	
	let dataTexto = $xml.find('registro_fato > dataCriacao').text();
	const date = new Date(dataTexto);
	const dataFormatada = date.toLocaleDateString('pt-BR',{
		day: '2-digit',
		month: '2-digit',
		year: 'numeric',
		hour: '2-digit',
		minute: '2-digit'
	});
	
	let privado = $xml.find('registro_fato > privado').text().trim();
	let tipo = $xml.find('registro_fato > idTipo').text().trim();
	let permiteAtendimento = $xml.find('registro_fato > permiteAtendimento').text().trim();
	let envolvimentoArmas = $xml.find('registro_fato > envolvimentoArmas').text().trim();

    // ----- Aba 1: Fato Ocorrido -----
    $('#selPrivado').val(privado).trigger('change');
    $('#selTipo').val(tipo);
    $('#dataInicioModal').val(dataFormatada);	
    $('#observacoes').val($xml.find('registro_fato > detalhamento').text() || '');
    $('#selTipo').val(tipo).trigger('change');
    $('#selAtendimento').val(permiteAtendimento).trigger('change');
    $('#selEnvolvArmas').val(envolvimentoArmas).trigger('change');    

    // ----- Aba 2: Envolvidos -----
    $('#envolvidosContainer').empty();
	$xml.find('envolvidos').each(function () {
        const id = $(this).find('id').text() || null;
	    const envolvido = {
            id: id, // Adiciona o ID ao objeto
	        nome: $(this).find('nome').text(),
	        cpf: $(this).find('cpf').text(),
	        tipoEnvolvimento: $(this).find('tipoEnvolvimento').text(),
	        email: $(this).find('email').text(),
	        ddd: $(this).find('ddd').text(),
	        telefone: $(this).find('telefone').text(),
	        detalhamento: $(this).find('detalhamento').text(),
	        descricao: $(this).find('descricao').text(),
	    };
        
        envolvidosOriginais.push({...envolvido}); 
	    preencherEnvolvido(envolvido);
	});

	// ----- Aba 3: Veículos -----
	$('#veiculosContainer').empty();

	$xml.find('veiculos').filter(function () {
	    return $(this).find('placa').text().trim() !== '' ||
	           $(this).find('cor').text().trim() !== '' ||
	           $(this).find('marca').text().trim() !== '' ||
	           $(this).find('modelo').text().trim() !== '';
	}).each(function () {
	    const idVeiculo = $(this).find('id').text() || null;
	    const veiculo = {
            id: idVeiculo,
	        placa: $(this).find('placa').text(),
	        cor: $(this).find('cor').text(),
	        marca: $(this).find('marca').text(),
	        modelo: $(this).find('modelo').text(),
	        dataInicio: $(this).find('dataInicio').text(),
	        descricao: $(this).find('descricaoMonitorado').text(),
            monitorado: ($(this).find('descricaoMonitorado').text().trim() !== '') 
	    };        
        veiculosOriginais.push({...veiculo});
	    preencherModalVeiculos(veiculo);
	});

    // ----- Aba 4: Objetos -----
	$xml.find('objetos').filter(function () {
	    return $(this).find('tipo').text().trim() !== '';
	}).each(function () {
	    const id = $(this).find('id').text() || null;
	    const objeto = {
            id: id, // Adiciona o ID ao objeto
	        tipo: $(this).find('tipo').text(),
	        descricao: $(this).find('descricao').text(),
	    };
        
        objetosOriginais.push({...objeto});
	    preencherModalObjetos(objeto);
	});

    // ----- Aba 5: Endereço -----
    
    latitude = null;
    longitude = null;
    
    let idCidade = $xml.find('localizacao > cidadeId').text().trim();
    latitude = $xml.find('localizacao > latitude').text();
    longitude = $xml.find('localizacao > longitude').text();
    
    $('#selCidades').val(idCidade).trigger('change');        
    $('#cep').val($xml.find('localizacao > cep').text());
    $('#bairro').val($xml.find('localizacao > bairro').text());
    $('#rua').val($xml.find('localizacao > rua').text());
    $('#numeroRua').val($xml.find('localizacao > numero').text());
    $('#complemento').val($xml.find('localizacao > complemento').text());
    $('#lat').val($xml.find('localizacao > latitude').text());
    $('#long').val($xml.find('localizacao > longitude').text());
	
	$('#containerEnderecosDinamicosGeral').empty();
	const containerGeral = $('#containerEnderecosDinamicosGeral');
	
	$xml.find('enderecos').each(function () {
	    const endereco = {
	        tipoEndereco: $(this).find('tipoEnderecoEventoDescricao').text(),
	        bairro: $(this).find('bairro').text(),
	        rua: $(this).find('rua').text(),
	        numero: $(this).find('numero').text(),
	        complemento: $(this).find('complemento').text(),
	        cep: $(this).find('cep').text(),
	    };
	
	    if (endereco.bairro !== '') {
	        const enderecoCompleto = `${endereco.rua} - ${endereco.numero}, ${endereco.bairro}, ${endereco.cep}`;
	        
	        // 1. Cria o contêiner para a linha completa, com alinhamento na base
	        const row = $('<div>').addClass('row d-flex align-items-baseline');
	        
	        // 2. Cria a coluna do tipo de endereço
	        const colTipo = $('<div>').addClass('col-md-4');
	        colTipo.append($('<span>').text(endereco.tipoEndereco));
	
	        // 3. Cria a coluna do endereço com a linha vertical e adiciona padding
	        const colEndereco = $('<div>').addClass('col-md-8 border-start ps-3');
	        colEndereco.append($('<span>').text(enderecoCompleto));
	        if (endereco.complemento) {
	            colEndereco.append($('<br>'));
	            colEndereco.append($('<span>').text(endereco.complemento));
	        }
	
	        // 4. Adiciona as colunas à linha principal
	        row.append(colTipo);
	        row.append(colEndereco);
	
	        // 5. Adiciona a linha completa ao contêiner geral
	        containerGeral.append(row);
	        
	        // 6. Adiciona o separador horizontal
	        containerGeral.append($('<hr>').addClass('my-3')); 
	    }
	});
     
    // Se tiver mapa, já atualiza:
    atualizarPosicaoMapa(idCidade);
    
    // ----- Aba 6: Grupo -----
    
    // Passo 1: Popula os selects com todas as opções disponíveis
    popularSelectComOpcoes('selGrupos', gruposDisponiveis);
    popularSelectComOpcoes('selUsuarios', usuariosDisponiveis);

    // Passo 2: Extrai os IDs que precisam ser selecionados do XML
    const idsGruposSelecionados = [];
    $xml.find('idsGrupos').each(function() {
        idsGruposSelecionados.push(parseInt($(this).text()));
    });
    
    const idsUsuariosSelecionados = [];
    $xml.find('idsUsuarios').each(function() {
        idsUsuariosSelecionados.push(parseInt($(this).text()));
    });
    
    // Passo 3: Marca as opções corretas nos selects
    preencherSelectMultiplo('selGrupos', idsGruposSelecionados);
    preencherSelectMultiplo('selUsuarios', idsUsuariosSelecionados);
    
    // ----- Aba 7: Passagens -----
    
    const $tabelaPassagens = $('#tabelaPassagensSemBoletim');
    $tabelaPassagens.find('tbody').remove(); // Remove o corpo da tabela

    const $tbody = $('<tbody>').appendTo($tabelaPassagens);

    $xml.find('passagens').each(function() {
        const placa = $(this).find('placa').text();
        const dataPassagemTexto = $(this).find('data_passagem').text();
        const idVeiculo = $(this).find('id_veiculo').text();
        
        // Formata a data para um formato mais legível
        const data = new Date(dataPassagemTexto);
        const dataPassagemFormatada = data.toLocaleDateString('pt-BR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

        const $tr = $('<tr>');
        $tr.append($('<td>').text(placa));
        $tr.append($('<td>').text(dataPassagemFormatada));

        const $btnAcao = $(`<button type="button" class="btn btn-sm btn-info me-1" onclick="visualizarPassagemComBoletim('${idVeiculo}')"><i class="fa fa-pencil" aria-hidden="true"></i></button>`);
        const $tdAcao = $('<td class="text-center">').append($btnAcao);
        $tr.append($tdAcao);

        $tbody.append($tr);
    });
    $('#txt_consulta_passagemSemBoletim').quicksearch('#tabelaPassagensSemBoletim tr');
}

function visualizarPassagemComBoletim(idVeiculoTempoReal) {
    // Guardar o id do veículo no campo oculto
    $("#id_veic").text(idVeiculoTempoReal);

    // Inicializar e abrir o modal
    const modalEl = document.getElementById('modalDetalheVeiculo');
    const modal = new bootstrap.Modal(modalEl, {
        backdrop: 'static', // impede fechar clicando fora
        keyboard: false     // impede fechar com ESC
    });
    modal.show();

    // Chama a função para carregar detalhes do veículo
    AbrirDetalhesVeiculo(idVeiculoTempoReal);
}

function preencherEnvolvido(dados) {	
    const container = document.getElementById('envolvidosContainer');
    const index = contadorEnvolvidos;
	
    const novoHTML = `
        <div class="card mb-2" data-item-id="${dados.id || ''}">
            <div class="card-header" data-bs-toggle="collapse" href="#envolvido${index}" style="cursor: pointer;">
                Envolvido #${index}
            </div>
            <div id="envolvido${index}" class="collapse show">
                <div class="card-body">
                    <div class="row">
                        <div class="mb-3 col-md-5">
                            <label>Nome:</label>
                            <input class="form-control" 
                                   name="envolvidos[${index}].nome" 
                                   id="nomeEnvolvido${index}" 
                                   value="${dados.nome || ''}" 
                                   placeholder="Ex.: João de Souza" maxlength="255">
                        </div>
                        <div class="mb-3 col-md-3">
                            <label>CPF:</label>
                            <input class="form-control cpf-mask" 
                                   name="envolvidos[${index}].cpf" 
                                   type="text" 
                                   id="cpf${index}" 
                                   value="${dados.cpf || ''}" 
                                   placeholder="Ex.: 123.456.789-11" maxlength="14">
                        </div>
                        <div class="mb-3 col-md-4">
                            <label>Tipo de Envolvimento:</label>
                            <select class="form-select select-tipo" 
                                    name="envolvidos[${index}].tipoEnvolvimento" 
                                    id="selTipoEnvolvimento${index}" 
                                    data-minimum-results-for-search="8">
                                <option value="${dados.tipoEnvolvimento}">${dados.descricao}</option>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="mb-3 col-md-8">
                            <label><strong>E-mail:</strong></label>
                            <input class="form-control" 
                                   type="email" 
                                   placeholder="Ex.: joaodesouza@gmail.com" 
                                   id="email${index}" 
                                   name="envolvidos[${index}].email" 
                                   value="${dados.email || ''}" 
                                   maxlength="255">
                        </div>
                        <div class="mb-3 col-md-4">
                            <label><strong>DDD + Telefone:</strong></label>
                            <div class="d-flex gap-2">
                                <div style="max-width: 70px; flex-shrink: 0;" class="position-relative">
                                    <input class="form-control" 
                                           type="number" 
                                           placeholder="DDD" 
                                           id="ddd${index}" 
                                           name="envolvidos[${index}].ddd" 
                                           value="${dados.ddd || ''}" 
                                           maxlength="2">
                                    <div class="invalid-feedback">
                                        Informe o DDD
                                    </div>
                                </div>
                                <div class="flex-grow-1 position-relative">
                                    <input class="form-control" 
                                           type="number" 
                                           placeholder="Ex.: 912345678" 
                                           id="telefone${index}" 
                                           name="envolvidos[${index}].telefone" 
                                           value="${dados.telefone || ''}" 
                                           maxlength="9">
                                    <div class="invalid-feedback">
                                        Informe o telefone
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="mb-3">
                        <label><strong>Detalhamento:</strong></label>
                        <textarea class="form-control" rows="4" 
                                  id="detalhamento${index}" 
                                  name="envolvidos[${index}].detalhamento" 
                                  placeholder="Detalhamento do envolvimento do(s) indivíduo(s)">${dados.detalhamento || ''}</textarea>
                    </div>
                    <button type="button" class="btn btn-sm btn-danger" style="margin-top: 12px" onclick="removerEnvolvido(this)">Remover</button>
                </div>
            </div>
        </div>
    `;

    container.insertAdjacentHTML('beforeend', novoHTML);
    
    const novoCardElemento = container.lastElementChild;
    desabilitarCamposCard(novoCardElemento);

    // Reaplica select2 ao novo select
    $(`#envolvido${index} .select-tipo`).select2({
        theme: 'bootstrap4',
        dropdownParent: $('#modalCadastrarFato'),
        width: '100%',
        placeholder: "Selecione o tipo de envolvimento"
    });

    // Aplica a máscara de CPF
    $(`#envolvido${index} .cpf-mask`).mask('000.000.000-00');

    // Preenche opções do select e seleciona a correta
    preencherTipoEnvolvimento(index, dados.tipoEnvolvimento);

    contadorEnvolvidos++;
}

function preencherModalVeiculos(veiculo) {
	
    const container = document.getElementById('veiculosContainer');
    const index = contadorVeiculos;

    const dataId = `dataMonitorado${index}`;

    const novoHTML = `
         <div class="card mb-2" data-item-id="${veiculo.id || ''}">
         	<input type="hidden" name="veiculos[${index}].id" value="${veiculo.id || ''}">
            <div class="card-header" data-bs-toggle="collapse" href="#veiculo${index}" style="cursor: pointer;">
                Veículo #${index}
            </div>
            <div id="veiculo${index}" class="collapse show">
                <div class="card-body">
                    <div class="row">
                        <div class="mb-3 col-md-4">
                            <label for="placaVeiculo${index}"><strong>Placa:</strong></label>
                            <input class="form-control" type="text" placeholder="Ex.: ABC0123"
                                name="veiculos[${index}].placa" id="placaVeiculo${index}" maxlength="7"
                                value="${veiculo.placa || ''}">
                        </div>
                        <div class="mb-3 col-md-4">
                            <label for="corVeiculo${index}"><strong>Cor:</strong></label>
                            <input class="form-control" type="text" placeholder="Ex.: BRANCA"
                                name="veiculos[${index}].cor" id="corVeiculo${index}" maxlength="50"
                                value="${veiculo.cor || ''}">
                        </div>
                        <div class="mb-3 col-md-4">
                            <label for="marcaVeiculo${index}"><strong>Marca:</strong></label>
                            <input class="form-control" type="text" placeholder="Ex.: RENAULT"
                                name="veiculos[${index}].marca" id="marcaVeiculo${index}" maxlength="50"
                                value="${veiculo.marca || ''}">
                        </div>
                    </div>
                    <div class="row">
                        <div class="mb-3 col-md-12">
                            <label for="modeloVeiculo${index}"><strong>Modelo:</strong></label>
                            <textarea class="form-control" rows="4"
                                placeholder="Ex.: DUSTER Intense Plus 1.6 CVT Modelo 2026 Branca"
                                name="veiculos[${index}].modelo" id="modeloVeiculo${index}" maxlength="255">${veiculo.modelo || ''}</textarea>
                        </div>
                    </div>
                    <div class="col-md-6 mb-2">
                        <div class="form-check form-check-inline">
                            <label class="form-check-label" for="cadastrarMonitorado${index}">Cadastrar como Veículo Monitorado</label>
                            <input class="form-check-input toggle-extra" type="checkbox" id="cadastrarMonitoradoValue${index}" ${veiculo.monitorado ? 'checked' : ''}>                            
                        </div>
                    </div>
                    <div class="row extra-fields ${veiculo.monitorado ? '' : 'd-none'}">
                        <div class="mb-3 col-md-6">
                            <label for="data"><strong>Data Início:</strong></label>
                            <div class="input-group" id="${dataId}">
                                <input name="veiculos[${index}].dataInicio" id="${dataId}.dataInicio" type="text" class="form-control" readonly
                                    value="${veiculo.dataInicio || ''}" />
                                <span class="input-group-text" data-td-target="#${dataId}" data-td-toggle="datetimepicker">
                                    <span class="fas fa-calendar"></span>
                                </span>
                            </div>
                        </div>
                        <div class="mb-3 col-md-6">
                            <label for="tipoAlerta"><strong>Tipo Alerta:</strong></label>
                            <select class="form-select" name="alertas[${index}].tipo" id="alertas${index}">
                                <option value="">Selecione...</option>
                            </select>
                        </div>
                        <div class="mb-3 col-md-12">
                            <label for="descricaoMonitorado${index}"><strong>Descrição:</strong></label>
                            <textarea class="form-control" rows="4"
                                placeholder="Ex.: O veículo foi roubado em plena luz do dia."
                                name="veiculos[${index}].descricaoMonitorado" id="descricaoMonitorado${index}" maxlength="255">${veiculo.descricao || ''}</textarea>
                        </div>
                    </div>
                    <button type="button" class="btn btn-sm btn-danger" style="margin-top: 12px" onclick="removerVeiculo(this)">Remover</button>
                </div>
            </div>
        </div>
    `;

    container.insertAdjacentHTML('beforeend', novoHTML);
    
    const novoCardElemento = container.lastElementChild;
    desabilitarCamposCard(novoCardElemento);

    new tempusDominus.TempusDominus(document.getElementById(dataId), {
        localization: { locale: 'pt-BR' }
    });

    const card = container.lastElementChild;
    const checkbox = card.querySelector('.toggle-extra');
    const extraFields = card.querySelector('.extra-fields');

    checkbox.addEventListener('change', function () {
        if (this.checked) {
            extraFields.classList.remove('d-none');
        } else {
            extraFields.classList.add('d-none');
        }
    });

    preencherTiposAlerta(index);
    contadorVeiculos++;
}

function preencherModalObjetos(dados) {
	
    const container = document.getElementById('objetosContainer');
    const index = contadorObjetos;

    const novoHTML = `
        <div class="card mb-3" data-item-id="${dados.id || ''}">
            <div class="card-header" data-bs-toggle="collapse" href="#objeto${index}" style="cursor: pointer;">
                Objeto #${index}
            </div>
            <div id="objeto${index}" class="collapse show">
                <div class="card-body">
                    <div class="row">
                        <div class="mb-3 col-md-12">
                            <label for="tipoObjeto${index}"><strong>Tipo:</strong></label>
                            <select class="form-select" name="objetos[${index}].tipo" id="tipoObjeto${index}">
                                <option value="Celular" ${dados.tipo === 'CELULAR' ? 'selected' : ''}>Celular</option>
                                <option value="Carteira" ${dados.tipo === 'CARTEIRA' ? 'selected' : ''}>Carteira</option>
                                <option value="Bolsa" ${dados.tipo === 'BOLSA' ? 'selected' : ''}>Bolsa</option>
                                <option value="Joias" ${dados.tipo === 'JOIAS' ? 'selected' : ''}>Jóias</option>
                                <option value="Relogios" ${dados.tipo === 'RELOGIOS' ? 'selected' : ''}>Relógios</option>
                                <option value="Eletronicos" ${dados.tipo === 'ELETRONICOS' ? 'selected' : ''}>Eletrônicos (Notebook, tablet, câmera, etc.)</option>
                                <option value="Chaves" ${dados.tipo === 'CHAVES' ? 'selected' : ''}>Chaves</option>
                                <option value="Oculos" ${dados.tipo === 'OCULOS' ? 'selected' : ''}>Óculos</option>
                                <option value="Armas" ${dados.tipo === 'ARMAS' ? 'selected' : ''}>Armas</option>
                                <option value="Municoes" ${dados.tipo === 'MUNICOES' ? 'selected' : ''}>Munições</option>
                                <option value="Explosivos" ${dados.tipo === 'EXPLOSIVOS' ? 'selected' : ''}>Explosivos</option>
                                <option value="Medicamentos" ${dados.tipo === 'MEDICAMENTOS' ? 'selected' : ''}>Medicamentos</option>
                            </select>
                        </div>
                    </div>
                    <div class="row">
                        <div class="mb-3 col-md-12">
                            <label for="descricaoObjeto${index}"><strong>Descrição:</strong></label>
                            <textarea class="form-control" rows="4"
                                      placeholder="Ex.: A vítima teve seu celular roubado durante um assalto."
                                      name="objetos[${index}].descricao" 
                                      id="descricaoObjeto${index}">${dados.descricao || ''}</textarea>
                        </div>
                    </div>
                    <button type="button" class="btn btn-sm btn-danger" style="margin-top: 12px" onclick="removerObjeto(this)">Remover</button>
                </div>
            </div>
        </div>
    `;

    container.insertAdjacentHTML('beforeend', novoHTML);
    
    const novoCardElemento = container.lastElementChild;
    desabilitarCamposCard(novoCardElemento);
    
    contadorObjetos++;
}

function obterGruposSemBoletim() {
  const url = "/MuralhaDigital/RegistroDeFato/Grupo?acao=obterTodos";

  $.ajax({
    type: "GET",
    url: url,
    dataType: "xml",
    success: function(data, textStatus, jqXHR) {
      try {
        const xmlDoc = data;
        const gruposNode = xmlDoc.getElementsByTagName("grupos")[0];

        if (!gruposNode) {
          console.log("Tag 'grupos' não encontrada no XML.");
          return;
        }

        const grupos = gruposNode.getElementsByTagName("grupo");
        const select = document.getElementById("selGrupos");
        select.innerHTML = "";
        gruposDisponiveis = [];

        if (grupos.length === 0) {
          Swal.fire({
            icon: 'info',
            title: 'Nenhum grupo encontrado',
            text: 'Nenhum grupo foi retornado para o usuário.',
            timer: 3000,
            showConfirmButton: false
          });
          return;
        }

        for (let i = 0; i < grupos.length; i++) {
          const grupo = grupos[i];
          const idGrupo = grupo.getElementsByTagName("id_grupo")[0].textContent.trim();
          const descricao = grupo.getElementsByTagName("descricao")[0].textContent.trim();

          const option = document.createElement("option");
          option.value = idGrupo;
          option.textContent = descricao;
          select.appendChild(option);
          gruposDisponiveis.push({
            id: idGrupo,
            nome: descricao
          });
        }

        // === PROCESSAR USUÁRIOS ===
        const usuarios = xmlDoc.getElementsByTagName("usuario");
        const selectUsuarios = document.getElementById("selUsuarios");
        selectUsuarios.innerHTML = "";
        usuariosDisponiveis = [];

        for (let i = 0; i < usuarios.length; i++) {
          const usuario = usuarios[i];
          const idNode = usuario.getElementsByTagName("id_usuario")[0];
          const nomeNode = usuario.getElementsByTagName("nome")[0];
          const loginNode = usuario.getElementsByTagName("usuario")[0];

          const idUsuario = idNode ? idNode.textContent.trim() : null;
          const nome = nomeNode ? nomeNode.textContent.trim() : null;
          const login = loginNode ? loginNode.textContent.trim() : null;

          if (idUsuario && nome && login) {
            const option = document.createElement("option");
            option.value = idUsuario;
            option.textContent = `${nome} (${login})`;
            selectUsuarios.appendChild(option);
            usuariosDisponiveis.push({
              id: idUsuario,
              nome: nome,
              login: login
            });
          }		  
        }
					
		popularSelectComOpcoes('selGrupos', gruposDisponiveis);
		popularSelectComOpcoes('selUsuarios', usuariosDisponiveis);
		
      } catch (e) {
        Swal.fire({
          icon: 'error',
          title: 'Erro ao processar dados',
          text: 'Ocorreu um erro ao processar os dados recebidos do servidor.'
        });
        console.error("Erro na função success:", e);
      }
    },
    error: function(jqXHR, textStatus, errorThrown) {
      Swal.fire({
        icon: 'error',
        title: 'Erro na requisição',
        text: 'Não foi possível processar a requisição ao servidor.'
      });
    }
  });
}

function popularSelectComOpcoes(selectId, listaDeItens) {	
	
    const select = document.getElementById(selectId);
    if (!select) return;

    // Limpa as opções existentes
    select.innerHTML = '';
    
    // Adiciona uma opção vazia
    const optionVazia = document.createElement('option');
    optionVazia.value = "";
    optionVazia.textContent = "Selecione...";
    select.appendChild(optionVazia);

    listaDeItens.forEach(item => {
        const option = document.createElement('option');
        option.value = item.id;
        option.textContent = item.nome;
        select.appendChild(option);
    });
}

function preencherSelectMultiplo(selectId, idsDoServidor) {
    const selectElement = document.getElementById(selectId);
    if (!selectElement) return;

    const idsParaMarcar = new Set(idsDoServidor);

    for (const option of selectElement.options) {
        const idDaOpcao = parseInt(option.value);
        option.selected = idsParaMarcar.has(idDaOpcao);
    }
}

function compararListas(originais, novos, camposParaComparar) {
    const resultados = {
        adicionados: [],
        editados: [],
        removidos: []
    };

    const originaisMap = new Map();
    originais.forEach(item => {
        if (item.id) {
            originaisMap.set(item.id, item);
        }
    });

    novos.forEach(itemNovo => {
        if (!itemNovo.id) {
            resultados.adicionados.push(itemNovo);
        } else {
            const itemOriginal = originaisMap.get(itemNovo.id);
            if (itemOriginal) {
                let foiEditado = false;
                camposParaComparar.forEach(campo => {
                    // Compara o valor do campo
                    if (itemOriginal[campo] != itemNovo[campo]) {
                        foiEditado = true;
                    }
                });

                if (foiEditado) {
                    resultados.editados.push(itemNovo);
                }
                originaisMap.delete(itemNovo.id);
            } else {
                resultados.adicionados.push(itemNovo);
            }
        }
    });

    originaisMap.forEach(itemRemovido => {
        resultados.removidos.push(itemRemovido);
    });

    return resultados;
}

function salvarEdicaoCompleta() {
	
    const valido = validarCampos();
    if (!valido) return;

    const envolvidosNovos = capturarEnvolvidos();
    const veiculosNovos = capturarVeiculos();
    const objetosNovos = capturarObjetos();
    const gruposNovos = capturarGrupos();
    const usuariosNovos = capturarUsuarios();

    // Compara as listas novas com as originais
    const diffEnvolvidos = compararListas(envolvidosOriginais, envolvidosNovos, ['nome', 'cpf', 'tipoEnvolvimento', 'email', 'ddd', 'telefone', 'detalhamento']);
    const diffVeiculos = compararListas(veiculosOriginais, veiculosNovos, ['placa', 'cor', 'marca', 'modelo']);
    const diffObjetos = compararListas(objetosOriginais, objetosNovos, ['tipo', 'descricao']);
    const diffGrupos = compararListas(gruposOriginais, gruposNovos, ['id', 'nome']);
	const diffUsuarios = compararListas(usuariosOriginais, usuariosNovos, ['id', 'nome', 'login']);
	
	const dataHoraOcorrido = document.getElementById("dataInicioModal").value; 
	const dataHoraConvertida = formatarDataParaISO(dataHoraOcorrido); 	
    
    const dados = {
		registroFato: {
			id: registroFatoId,
			atendimentoPermitido: parseInt(document.getElementById("selAtendimento").value),
			envolvimentoArmas: parseInt(document.getElementById("selEnvolvArmas").value),
			privado: parseInt(document.getElementById("selPrivado").value),
			tipoRegistro: parseInt(document.getElementById("selTipo").value),
			dataHoraOcorrido: dataHoraConvertida,
			detalhamentoFato: document.getElementById("observacoes").value || '',
		},
		localizacao: {
			cidadeId: parseInt(document.getElementById("selCidades").value),
			tipoEnderecoEvento: parseInt(document.getElementById("selTipoEvento").value),
			cep: document.getElementById("cep").value.replace("-",""),
			bairro: document.getElementById("bairro").value,
			rua: document.getElementById("rua").value,
			complemento: document.getElementById("complemento").value,
			numero: document.getElementById("numeroRua").value,
			latitude: parseFloat(document.getElementById("lat").value),
			longitude: parseFloat(document.getElementById("long").value)
		},
		envolvidos: diffEnvolvidos,
		veiculos: diffVeiculos,
		objetos: diffObjetos,
		grupos: diffGrupos,
		usuarios: diffUsuarios	
	}

    url = `/MuralhaDigital/RegistroDeFato?acao=atualizarSemBoletim`

    $.ajax({
        type: "POST",
        url: url,
        contentType: "application/json",
        dataType: "xml",
        data: JSON.stringify(dados),
        		success: function(data, textStatus, jqXHR) {
			var event = jqXHR.responseText;
			var xmlDoc = $.parseXML(event);
			var $xml = $(xmlDoc);

			var sucesso = $xml.find('sucesso').text();
			var msgResposta = $xml.find('msgResposta').text();

			sucesso = (sucesso === 'true' || sucesso === '');

			if (sucesso) {
				alert("Cadastro realizado com sucesso!")
			} else {
				ErrorNotification(msgResposta, "");
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			$("body").removeClass("loading");
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		},
    });
}

function desabilitarCampos(){
	const camposNormais = document.querySelectorAll('#selEnvolvArmas, #cep');
    
	camposNormais.forEach(campo => {
        campo.disabled = true;
    });
    
    // Desabilita os componentes Select2
    $('#selTipo').prop('disabled', true);
    $('#selCidades').prop('disabled', true);
    $('#selTipoEvento').prop('disabled', false);
    
    // Desabilita o input da data
    $('#dataInicioModal').prop('disabled', true);
}

function desabilitarCamposCard(card) {
    if (!card) {
        console.error("Card não foi fornecido para a função desabilitarCamposCard.");
        return;
    }

    // Seleciona todos os campos de formulário dentro do card
    const camposCards = card.querySelectorAll('input, textarea, select');
    
    camposCards.forEach(campo => {
        // Desabilita o campo
        campo.disabled = true;

        // Se o campo for um Select2, desabilita-o corretamente
        if ($(campo).hasClass('select2-hidden-accessible')) {
            $(campo).prop('disabled', true);
        }
    });
}

function habilitarCampos(){	
	
	// Campos de input e select2
    const camposNormais = document.querySelectorAll(
        '#selEnvolvArmas, #cep'
    );
    
    // Habilita os campos "normais" (selects, inputs)
    camposNormais.forEach(campo => {
        campo.disabled = false;
    });

    // Habilita os componentes Select2
    $('#selTipo').prop('disabled', false);
    $('#selCidades').prop('disabled', false);
    
    // Habilita o input da data
    $('#dataInicioModal').prop('disabled', false)
}

function  AbrirListaAlertasCadMonitorado(){
	const id = registroFatoId
	console.log("id ", id)
	 if (id != null) {window.open(
            "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?registro-fato=" + id,
            "_blank" // abre em nova aba
        );
      /*  window.location.href = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?cad-veiculo-monitorado=" + ID;*/
    } else {
        alert("ID do veículo monitorado não encontrado.");
    }
}