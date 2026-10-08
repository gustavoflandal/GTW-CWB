$(document).ready(function() {	
	
	const modalInstance = new bootstrap.Modal(document.getElementById('modalCadastrarFato'), {
	    backdrop: 'static',
	    keyboard: false
	});
			
	CarregarPametrosPaginacao();
	carregarCombos();
	CarregarComponenteData(true);
});

async function carregarCombos() {
	$("body").addClass("loading");

	await obterTiposRegistrosDeFatos();
	await obterSituacao();
	await obterCidade();
	await obterTiposIndividuos();
	
	$("body").removeClass("loading");
}

function limparTabelaResultado() {
	$("#resultadoTabela").empty();
}

function executaPesquisaPorIds(registroDeFatoIds) {
	limparTabelaResultado();
	if (!Array.isArray(registroDeFatoIds) || registroDeFatoIds.length === 0) {
		console.warn("Nenhum registroDeFatoId foi informado.");
		return;
	}

	// Montar os parâmetros da requisição
	const params = new URLSearchParams();
	params.append("acao", "obterListaPorIds");
	params.append("registroDeFatoIds", registroDeFatoIds.join(",")); // separa os GUIDs com vírgula

	// Construir a URL base da API ou do endpoint
		const url = "/MuralhaDigital/Boletim";

	// Exibir loading, se necessário
	$("body").addClass("loading");

	$.ajax({
		type: "GET",
		url: url,
		data: params.toString(),
		dataType: "xml",

		success: (data, textStatus, jqXHR) => {
			const responseText = jqXHR.responseText;
			const $xml = $($.parseXML(responseText));

			const sucesso = $xml.find("sucesso").text();
			const msg = $xml.find("msgResposta").text();

			if (sucesso === "true" || sucesso === "") {
				processaDados(responseText);
			} else {
				ErrorNotification(msg, "");
			}

			$("body").removeClass("loading");
		},

		error: () => {
			$("body").removeClass("loading");
			ErrorNotification("Erro ao processar requisição ao servidor!!", "");
		},

		complete: (jqXHR) => {
			AtualizarParametrosComponentePaginacao(jqXHR.responseText);
			CriarComponentePaginacao();
		}
	});
}


function executaPesquisa(reiniciarPaginaAtual) {
	limparTabelaResultado();
	const getValue = (id) => document.getElementById(id)?.value?.trim() || "";

	const tipoBoletim = getValue("tipoRegistroDeFato");
	const placa = getValue("placaRegistroDeFato");
	const situacao = getValue("situacaoRegistroDeFato");
	let cpf = getValue("cpfRegistroDeFato").replace(/[^a-zA-Z0-9]/g, '');
	const cidade = getValue("cidadeRegistroDeFato");
	const dataIni = getValue("dataInicioRegistroDeFato");
	const dataFim = getValue("dataFimRegistroDeFato");

	const formatarDataSQL = (dataPtBr) => {
		if (!dataPtBr) return null;
		const momentDate = moment(dataPtBr, 'DD/MM/YYYY', true);
		return momentDate.isValid() ? momentDate.format('YYYY-MM-DD') : null;
	};

	const dataIniSQL = formatarDataSQL(dataIni);
	const dataFimSQL = formatarDataSQL(dataFim);

	if (reiniciarPaginaAtual) {
		ReiniciaPaginaAtualComponentePaginacao();
	}

	const params = new URLSearchParams({
		acao: 'obterLista',
		tipoOcorrencia: tipoBoletim,
		placa,
		situacao,
		cpf,
		cidade,
		paginacaoItensPorPagina: PAGINACAO_ITENS_POR_PAGINA,
		paginacaoOffset: PAGINACAO_OFFSET
	});

	if (dataIniSQL) params.append('dataIni', dataIniSQL);
	if (dataFimSQL) params.append('dataFim', dataFimSQL);

	const url = "/MuralhaDigital/RegistroDeFato?acao=obterListaSemBoletim";

	$("body").addClass("loading");

	$.ajax({
		type: "GET",
		url,
		data: params.toString(),
		dataType: "xml",

		success: (data, textStatus, jqXHR) => {
			const responseText = jqXHR.responseText;
			const $xml = $($.parseXML(responseText));

			const sucesso = $xml.find("sucesso").text();
			const msg = $xml.find("msgResposta").text();

			if (sucesso === "true" || sucesso === "") {
				processaDados(responseText);
			} else {
				ErrorNotification(msg, "");
			}

			$("body").removeClass("loading");
		},

		error: () => {
			$("body").removeClass("loading");
			ErrorNotification("Erro ao processar requisição ao servidor!!", "");
		},

		complete: (jqXHR) => {
			AtualizarParametrosComponentePaginacao(jqXHR.responseText);
			CriarComponentePaginacao();
		}
	});
}

function AtualizarItensPaginacao() {
	executaPesquisa(false);
}

function adicionaUnicos(array, item, chaveFunc) {
  const chave = chaveFunc(item);
  if (!array.some(e => chaveFunc(e) === chave)) {
    array.push(item);
  }
}

function processaIndividuos(individuosWrapper, listaIndividuos) {
  if (!individuosWrapper) return;
  const individuos = individuosWrapper.getElementsByTagName("individuo");
  for (const ind of individuos) {
    const nome = ind.getElementsByTagName("nome")[0]?.textContent?.trim() || "-";
    const cpf = ind.getElementsByTagName("cpf")[0]?.textContent?.trim() || "-";
    adicionaUnicos(listaIndividuos, { nome, cpf }, x => x.cpf.toUpperCase());
  }
}

function processaVeiculos(veiculosWrapper, listaVeiculos) {
  if (!veiculosWrapper) return;
  const veiculos = veiculosWrapper.getElementsByTagName("veiculo");
  for (const v of veiculos) {
    const placa = v.getElementsByTagName("placa")[0]?.textContent?.trim() || "-";
    const marca = v.getElementsByTagName("marca")[0]?.textContent?.trim() || "-";
    const modelo = v.getElementsByTagName("modelo")[0]?.textContent?.trim() || "-";
    const cor = v.getElementsByTagName("cor")[0]?.textContent?.trim() || "-";
    adicionaUnicos(listaVeiculos, { placa, marca, modelo, cor }, x => x.placa.toUpperCase());
  }
}

function processaEnderecos(enderecosWrapper, listaEnderecos) {
  if (!enderecosWrapper) return;
  const enderecos = enderecosWrapper.getElementsByTagName("endereco");
  for (const e of enderecos) {
    const rua = e.getElementsByTagName("rua")[0]?.textContent?.trim() || "-";
    const numero = e.getElementsByTagName("numero")[0]?.textContent?.trim() || "-";
    const bairro = e.getElementsByTagName("bairro")[0]?.textContent?.trim() || "-";
    const cep = e.getElementsByTagName("cep")[0]?.textContent?.trim() || "-";
    const cidadeElem = e.getElementsByTagName("cidade")[0];
    const cidade = cidadeElem?.getElementsByTagName("nome")[0]?.textContent?.trim() || "-";

    adicionaUnicos(listaEnderecos, { rua, numero, bairro, cep, cidade }, x =>
      `${x.rua}|${x.numero}|${x.bairro}|${x.cep}|${x.cidade}`
    );
  }
}

function processaDados(event) {
  const parser = new DOMParser();
  const xmlDoc = parser.parseFromString(event, "application/xml");
  const registros = xmlDoc.getElementsByTagName("RegistroDeFato");
  const tabela = document.getElementById("resultadoTabela");
  tabela.innerHTML = "";

  const registrosAgrupados = {};

  for (const reg of registros) {
    const id = reg.getElementsByTagName("id")[0]?.textContent;
    if (!id) continue;

    if (!registrosAgrupados[id]) {
      registrosAgrupados[id] = {
        id,
        tipo: reg.getElementsByTagName("tipoDescricao")[0]?.textContent?.trim() || "-",
        situacao: reg.getElementsByTagName("statusDescricao")[0]?.textContent?.trim() || "-",
        dataCriacao: reg.getElementsByTagName("dataCriacao")[0]?.textContent || "",
        nomeUsuario: reg.getElementsByTagName("nomeUsuario")[0]?.textContent?.trim() || "-",
        individuos: [],
        veiculos: [],
        enderecos: []
      };
    }

    const registro = registrosAgrupados[id];

    processaIndividuos(reg.getElementsByTagName("individuos")[0], registro.individuos);
    processaVeiculos(reg.getElementsByTagName("veiculos")[0], registro.veiculos);
    processaEnderecos(reg.getElementsByTagName("enderecos")[0], registro.enderecos);
  }

  // Ordena por data de criação DESC
  const registrosOrdenados = Object.values(registrosAgrupados).sort((a, b) => {
    return new Date(b.dataCriacao) - new Date(a.dataCriacao);
  });

  // Monta o HTML fora do loop para performance
  let html = "";
  for (const reg of registrosOrdenados) {
    const individuosFormatados = reg.individuos.map(ind =>
      `${ind.nome}${ind.cpf !== "-" ? ` (CPF: ${ind.cpf})` : ""}`
    ).join("<br> ") || "-";

    const veiculosFormatados = reg.veiculos.map(veic =>
      `${veic.placa}${veic.marca !== "-" ? ` (${veic.marca}, ${veic.modelo}, ${veic.cor})` : ""}`
    ).join("<br> ") || "-";

    const dataFormatada = formatarDataBrasileira(reg.dataCriacao);

    html += `
      <tr>
        <td>${reg.tipo}</td>
        <td>${individuosFormatados}</td>
        <td>${veiculosFormatados}</td>
        <td>${reg.situacao}</td>
        <td>${dataFormatada}</td>
		<td>
		  <div class="btn-group" role="group">

		    <button onclick="abrirModalPorId(${reg.id})" title='Detalhar' class="btn btn-primary">
		      <i class="fa fa-pencil"></i>
		    </button> 
		    <button onclick="abrirModalHistorico(${reg.id})" title='Ver Histórico' class="btn btn-info">
		      <i class="fa fa-history" aria-hidden="true"></i>
		    </button>
		    </div>
		</td>
      </tr>`;
  }

  tabela.innerHTML = html;

  $('#txt_consulta').quicksearch('#resultadoTabela tr');
}

function formatarDataBrasileira(dataISO) {
	if (!dataISO) return ""; // Se a data estiver vazia, retorna vazio

	const data = new Date(dataISO);

	// Extrai dia, mês, ano, horas e minutos
	const dia = String(data.getDate()).padStart(2, '0');
	const mes = String(data.getMonth() + 1).padStart(2, '0');
	const ano = data.getFullYear();
	const horas = String(data.getHours()).padStart(2, '0');
	const minutos = String(data.getMinutes()).padStart(2, '0');

	return `${dia}/${mes}/${ano} ${horas}:${minutos}`;
}

function limparFiltros() {
	// Zerar campos de texto
	document.getElementById("placaRegistroDeFato").value = "";
	document.getElementById("cpfRegistroDeFato").value = "";

	// Resetar selects para o valor vazio (que representa "Todos")
	document.getElementById("tipoRegistroDeFato").value = "";
	document.getElementById("cidadeRegistroDeFato").value = "";
	document.getElementById("situacaoRegistroDeFato").value = "";

	// Limpar campos de data (se estiver usando datetimepicker do Tempus Dominus)
	const dataInicioInput = document.getElementById("dataInicioRegistroDeFato");
	const dataFimInput = document.getElementById("dataFimRegistroDeFato");

	dataInicioInput.value = "";
	dataFimInput.value = "";

	// Se estiver usando Tempus Dominus: limpar o valor do picker também
	if (dataInicioInput._td) dataInicioInput._td.clear();
	if (dataFimInput._td) dataFimInput._td.clear();
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
				const select = document.getElementById('tipoRegistroDeFato');
				const selectModal = document.getElementById('selTipo');
				// Adiciona cada tipo na ordem recebida
				for (let i = 0; i < tipos.length; i++) {
					const tipo = tipos[i];
					const id = tipo.getElementsByTagName('id')[0].textContent;
					const descricao = tipo.getElementsByTagName('descricao')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
					
					const optionModal = document.createElement('option');
					optionModal.value = id;
					optionModal.textContent = descricao;
					selectModal.appendChild(optionModal);
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

function obterSituacao() {
	var urlPesquisa = "/MuralhaDigital/RegistroDeFato/Situacao";

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
				const listaSituacoes = xmlDoc.getElementsByTagName('ListaSituacoes')[0];
				const situacoes = listaSituacoes.getElementsByTagName('Situacao');
				const select = document.getElementById('situacaoRegistroDeFato');
				// Adiciona cada situação como uma opção
				for (let i = 0; i < situacoes.length; i++) {
					const situacao = situacoes[i];
					const id = situacao.getElementsByTagName('id')[0].textContent;
					const descricao = situacao.getElementsByTagName('descricao')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
				}
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
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



function obterCidade() {
	var urlPesquisa = "/MuralhaDigital/RegistroDeFato/Cidade";

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
				const listaSituacoes = xmlDoc.getElementsByTagName('ListaCidades')[0];
				const situacoes = listaSituacoes.getElementsByTagName('Cidade');
				const select = document.getElementById('cidadeRegistroDeFato');
				const selectModal = document.getElementById('selCidades');
				// Adiciona cada situação como uma opção
				for (let i = 0; i < situacoes.length; i++) {
					const situacao = situacoes[i];
					const id = situacao.getElementsByTagName('id')[0].textContent;
					const descricao = situacao.getElementsByTagName('nome')[0].textContent;

					const option = document.createElement('option');
					option.value = id;
					option.textContent = descricao;
					select.appendChild(option);
					
					const optionModal = document.createElement('option');
					optionModal.value = id;
					optionModal.textContent = descricao;
					selectModal.appendChild(optionModal);
				}
			} else {
				WarningCsx_E_TimeOut_8000ms(msgResposta);
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

function SuccessNotification(title, text) {
	//necessario <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
		title: title,
		icon: "success",
		text: text,
	});
}

function ErrorNotification(title, text) {
	if (!title) {
		title = 'Erro ao processar requisição ao servidor!!';
	}
	//necessario <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
		title: title,
		icon: "error",
		text: text,
	});
}

document.addEventListener("DOMContentLoaded", function() {
	const cpfInput = document.getElementById("cpfRegistroDeFato");

	cpfInput.addEventListener("input", function() {
		let value = cpfInput.value.replace(/\D/g, "");
		if (value.length > 11) value = value.slice(0, 11);

		let formatted = value;
		if (value.length > 9) {
			formatted = value.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
		} else if (value.length > 6) {
			formatted = value.replace(/(\d{3})(\d{3})(\d{1,3})/, "$1.$2.$3");
		} else if (value.length > 3) {
			formatted = value.replace(/(\d{3})(\d{1,3})/, "$1.$2");
		}

		cpfInput.value = formatted;
	});
});

document.addEventListener('DOMContentLoaded', function () {
	const urlParams = new URLSearchParams(window.location.search);
	const registroDeFatoIds = window.registroDeFatoIds || [];

	const dataInicio = urlParams.get('dataInicio');
	const dataFim = urlParams.get('dataFim');
	const placa = urlParams.get('placa');
	const cpf = urlParams.get('cpf');

	if (dataInicio) {
		const partes = dataInicio.split("-");
		document.getElementById('dataInicioRegistroDeFato').value =
			partes.length === 3 ? `${partes[2]}/${partes[1]}/${partes[0]}` : dataInicio;
	}

	if (dataFim) {
		const partes = dataFim.split("-");
		document.getElementById('dataFimRegistroDeFato').value =
			partes.length === 3 ? `${partes[2]}/${partes[1]}/${partes[0]}` : dataFim;
	}

	if (placa) {
		document.getElementById('placaRegistroDeFato').value = placa;
	}

	if (cpf) {
		document.getElementById('cpfRegistroDeFato').value = cpf;
		removerParametroUrl('cpf');
	}

	if (registroDeFatoIds.length === 0 || (registroDeFatoIds.length === 1 && registroDeFatoIds[0].trim() === "")) {
		executaPesquisa(true);
	} else {
		executaPesquisaPorIds(registroDeFatoIds);
		removerParametroUrl('registroDeFatoId');
	}
});


function removerParametroUrl(nomeParametro) {
	const url = new URL(window.location.href);
	url.searchParams.delete(nomeParametro);
	history.replaceState({}, document.title, url.toString());
}

function abrirModal(cadastrar){
	$('#containerEnderecosDinamicosGeral').empty();
    if(cadastrar){		
        limparCampos();
        document.getElementById('btnAtualizar').style.display = 'none';
        document.getElementById('btnCadastrarModalRegistroFato').style.display = 'inline-block';   
		document.getElementById('tituloOutrosEnderecos').style.display = 'none';   
		document.getElementById('tituloTipoEndereco').style.display = 'none';  
		document.getElementById('tituloEnderecoDinamico').style.display = 'none';   	
        habilitarCampos();
    }else{
        document.getElementById('btnCadastrarModalRegistroFato').style.display = 'none';
        document.getElementById('btnAtualizar').style.display = 'inline-block';
        document.getElementById('tituloOutrosEnderecos').style.display = 'inline-block';   
		document.getElementById('tituloTipoEndereco').style.display = 'inline-block';  
		document.getElementById('tituloEnderecoDinamico').style.display = 'inline-block';     		     
    }
        
    $('#modalCadastrarFato').modal({
        backdrop: 'static',
        keyboard: false
    }).modal('show');       
}

function fecharModal(){
	 $('#selTipoEvento').prop('disabled', true);
    $('#modalCadastrarFato').modal('hide');
}

function limparCampos(){	
	//Limpa os campos de input da modal
	document.getElementById('formModalCadastrarFato').reset();

	const selectGrupos = document.getElementById('selGrupos');
	selectGrupos.innerHTML = '';
	
	const selectUsuarios = document.getElementById('selUsuarios');
	selectUsuarios.innerHTML = '';
	
	//Limpa os campos do select
	$('#formModalCadastrarFato select').each(function () {
        $(this).val(0).trigger('change');
    });
    
    const cep = document.getElementById('cep');
    cep.innerHTML = '';
    
    const bairro = document.getElementById('bairro');
    bairro.innerHTML = '';
    
    const rua = document.getElementById('rua');
    rua.innerHTML = '';
    
    const numeroRua = document.getElementById('numeroRua');
    numeroRua.innerHTML = '';
    
    const lat = document.getElementById('lat');
    lat.innerHTML = '';
    
    const long = document.getElementById('long');
    long.innerHTML = '';
    
    const complemento = document.getElementById('complemento');
    complemento.innerHTML = '';
    
    //Remove todos os cards de veículos
    const containerVeiculos = document.getElementById('veiculosContainer');
    containerVeiculos.innerHTML = '';
        
    //Remove todos os cards de objetos
    const containerObjetos = document.getElementById('objetosContainer');
    containerObjetos.innerHTML = '';
    
    //Remove todos os cards de envolvidos
    const containerEnvolvidos =  document.getElementById('envolvidosContainer');
    containerEnvolvidos.innerHTML = '';
    
    //Reseta os contadores
    contadorVeiculos = 1;
    contadorObjetos = 1;
    contadorEnvolvidos = 1;	
    latitude = null;
    longitude = null;
    
    // Remove bordas vermelhas
    document.querySelectorAll('.input-error').forEach(el => el.classList.remove('input-error'));

    // Remove mensagens de erro
    document.querySelectorAll('.error-message').forEach(el => el.remove());
}