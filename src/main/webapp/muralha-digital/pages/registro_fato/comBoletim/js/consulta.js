let ORDER_BY = 'dataCriacao';
let ORDER_DIR = 'DESC'; // 'ASC' ou 'DESC'

$(document).ready(function () {
    inicializarPagina().catch(console.error);
});

async function inicializarPagina() {
    tratarParametrosUrlEExecutarPesquisa();
    CarregarPametrosPaginacao();
    CarregarComponenteData(false);
    InicializarFiltrosRegistroFato();
    obterUsuarios();
    inicializarOrdenacaoTabela();
	await carregarCombosComBoletim();
}

/**
 * Script responsável por ler parâmetros da URL
 * e preencher automaticamente os campos do filtro
 * na tela de consulta de boletins.
 *
 * Parâmetros esperados na URL:
 * ----------------------------------------
 * - dataInicio : Data inicial no formato 'yyyy-MM-dd'
 * - dataFim    : Data final no formato 'yyyy-MM-dd'
 * - placa      : Placa do veículo (com ou sem traço)
 *
 * Caso encontre esses parâmetros:
 * - Preenche os inputs correspondentes na tela:
 *     • #dataInicioRegistroDeFato
 *     • #dataFimRegistroDeFato
 *     • #placaRegistroDeFato
 *
 * Além disso:
 * - Converte datas do formato 'yyyy-MM-dd' para 'dd/MM/yyyy'
 *   para compatibilidade com os campos da tela.
 *
 * - Executa a pesquisa automaticamente chamando executaPesquisaComBoletim(true)
 */
function tratarParametrosUrlEExecutarPesquisa() {
    const urlParams = new URLSearchParams(window.location.search);
    const registroDeFatoIds = window.registroDeFatoIds || [];

    const dataInicio = urlParams.get('dataInicio');
    const dataFim = urlParams.get('dataFim');
    const placa = urlParams.get('placa');
    const cpf = urlParams.get('cpf');

    if (dataInicio) {
        const partes = dataInicio.split("-");
        $('#dataInicioRegistroDeFatoComBoletim').val(
            partes.length === 3 ? `${partes[2]}/${partes[1]}/${partes[0]}` : dataInicio
        );
    }

    if (dataFim) {
        const partes = dataFim.split("-");
        $('#dataFimRegistroDeFatoComBoletim').val(
            partes.length === 3 ? `${partes[2]}/${partes[1]}/${partes[0]}` : dataFim
        );
    }

    if (placa) {
        $('#placaRegistroDeFatoComBoletim').val(placa);
    }

    if (cpf) {
        $('#cpfRegistroDeFatoComBoletim').val(cpf);
        removerParametroUrl('cpf');
    }

    // 🔥 Agora SIM é seguro pesquisar
    if (
        registroDeFatoIds.length === 0 ||
        (registroDeFatoIds.length === 1 && registroDeFatoIds[0].trim() === "")
    ) {
        executaPesquisaComBoletim(true);
    } else {
        removerParametroUrl('registroDeFatoId');
    }
}

function inicializarOrdenacaoTabela() {
  document.querySelectorAll('th.sortable').forEach(th => {
    th.addEventListener('click', () => {
      const field = th.getAttribute('data-sort');
      if (!field) return;

      if (ORDER_BY === field) {
        // alterna direção
        ORDER_DIR = (ORDER_DIR === 'ASC') ? 'DESC' : 'ASC';
      } else {
        ORDER_BY = field;
        ORDER_DIR = 'ASC';
      }

      atualizarIndicadoresOrdenacao();
      // reinicia pagina (se desejar) e executa pesquisa
      ReiniciaPaginaAtualComponentePaginacao();
      executaPesquisaComBoletim(true);
    });
  });

  atualizarIndicadoresOrdenacao();
}

function atualizarIndicadoresOrdenacao() {
  document.querySelectorAll('th.sortable').forEach(th => {
    th.classList.remove('sorted-asc','sorted-desc');
    const icon = th.querySelector('.sort-icon');
    // reset icons (usa FontAwesome classes)
    if (icon) {
      icon.className = 'fa fa-sort ms-1 sort-icon'; // padrao
    }
  });

  // marca o atual
  const current = document.querySelector(`th[data-sort="${ORDER_BY}"]`);
  if (current) {
    current.classList.add(ORDER_DIR === 'ASC' ? 'sorted-asc' : 'sorted-desc');
    const ic = current.querySelector('.sort-icon');
    if (ic) {
      ic.className = ORDER_DIR === 'ASC' ? 'fa fa-sort-up ms-1 sort-icon' : 'fa fa-sort-down ms-1 sort-icon';
    }
  }
}

async function carregarCombosComBoletim() {
	await Promise.all([
	    obterTiposRegistrosDeFatos(),
	    obterSituacao(),
	    obterCidade(),
	    obterGrupoUsuario()
	]);
}

function limparTabelaResultadoComBoletim() {
	$("#resultadoTabelaComBoletim").empty();
}

function executaPesquisaComBoletimBotao(reiniciarPaginaAtual)
{
	limparOrdenacaoTabelaComBoletim();
	executaPesquisaComBoletim(reiniciarPaginaAtual);
}

function executaPesquisaComBoletim(reiniciarPaginaAtual) {
	limparTabelaResultadoComBoletim();
	const urlParams = new URLSearchParams(window.location.search);
	let dataAlteracao = urlParams.get("dataUltimaAlteracao");
	let situacaoRegistroFato = urlParams.get("situacao");
	let necessitaComplemento = urlParams.get("necessitaComplemento");
	let comBoletimParam = urlParams.get("comboletim");
	let dataIniAlteracaoValue = '';
	let dataFimAlteracaoValue = '';

	const dataStr = String(dataAlteracao)
		.replace(/\//g, '-')    
		.split(' ')[0];         

	if (dataAlteracao) {
		const partes = dataStr.split("-");
		document.getElementById('dataInicioAlteracao').value =
			partes.length === 3 ? `${partes[2]}/${partes[1]}/${partes[0]}` : dataStr;
			
		const hoje = new Date();
		const dia = String(hoje.getDate()).padStart(2, '0');
		const mes = String(hoje.getMonth() + 1).padStart(2, '0');
		const ano = hoje.getFullYear();
		const hora = String(hoje.getHours()).padStart(2, '0');
		const minuto = String(hoje.getMinutes()).padStart(2, '0');

		dataIniAlteracaoValue = `${ano}-${mes}-${dia}T00:00`;
		dataFimAlteracaoValue = `${ano}-${mes}-${dia}T${hora}:${minuto}`;
	}

	if (situacaoRegistroFato) {
		document.getElementById("situacaoRegistroDeFatoComBoletim").value = situacaoRegistroFato;
	}
	
	if (necessitaComplemento) {
		document.getElementById("divFiltrosRegistroFato").style.display = "block";
		document.getElementById("infoFaltante").value = 1;
	}
	
	if (comBoletimParam) {
		document.getElementById("divFiltrosRegistroFato").style.display = "block";
		document.getElementById("comBoletim").value = comBoletimParam;
	}


	const getValue = (id) => document.getElementById(id)?.value?.trim() || "";

	const chkFiltrosAvancados = document.getElementById('chkFiltrosAvancados').checked ? 1 : 0;
	const tipoRegistroDeFato = getValue("tipoRegistroDeFatoComBoletim");
	const placa = getValue("placaRegistroDeFatoComBoletim");
	const situacao = situacaoRegistroFato != null ? situacaoRegistroFato : getValue("situacaoRegistroDeFatoComBoletim");
	let cpf = getValue("cpfRegistroDeFatoComBoletim").replace(/[^a-zA-Z0-9]/g, '');
	const cidade = getValue("cidadeRegistroDeFatoComBoletim");
	const dataIni = getValue("dataInicioRegistroDeFatoComBoletim");
	const dataFim = getValue("dataFimRegistroDeFatoComBoletim");
	const dataIniAlteracao = dataAlteracao != null ? dataIniAlteracaoValue : getValue("dataInicioAlteracao");
	const dataFimAlteracao = dataAlteracao != null ? dataFimAlteracaoValue : getValue("dataFimAlteracao");
	const dataIniFato = getValue("dataInicioFato");
	const dataFimFato = getValue("dataFimFato");
	const origemBoletim = getValue("origemBoletim");
	const infoFaltante = necessitaComplemento != null ? 1 : getValue("infoFaltante");
	const incluirVeiculos = getValue("incluirVeiculos");
	const incluirMonitorados = getValue("incluirMonitorados");
	const filtroObjeto = getValue("filtroObjeto");
	const comBoletim = comBoletimParam != null ? comBoletimParam : getValue("comBoletim");
	const tipoRegistro = getValue("tipoRegistro");
	const naturezaRegistro = getValue("naturezaRegistro");
	const tipoAcesso = getValue("tipoAcesso");
	const nomeOperador = getValue("nomeOperador");
	const nomeEnvolvido = getValue("nomeEnvolvido");
	const acessoPermitido = getValue("acessoPermitido");

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
		acao: 'obterListaComBoletim',
		tipoOcorrencia: tipoRegistroDeFato,
		chkFiltrosAvancados,
		placa,
		situacao,
		cpf,
		cidade,
		dataIni,
		dataFim,
		dataIniAlteracao,
		dataFimAlteracao,
		dataIniFato,
		dataFimFato,
		origemBoletim,
		infoFaltante,
		incluirVeiculos,
		incluirMonitorados,
		filtroObjeto,
		comBoletim,
		tipoRegistro,
		naturezaRegistro,
		tipoAcesso, 
		acessoPermitido,
		nomeOperador,
		nomeEnvolvido,
		paginacaoItensPorPagina: PAGINACAO_ITENS_POR_PAGINA,
		paginacaoOffset: PAGINACAO_OFFSET
	});

	if (dataIniSQL) params.append('dataIni', dataIniSQL);
	if (dataFimSQL) params.append('dataFim', dataFimSQL);
	
	params.append('orderBy', ORDER_BY);
	params.append('orderDir', ORDER_DIR);

	const url = "/MuralhaDigital/RegistroDeFato";
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
	executaPesquisaComBoletim(false);
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
    const xmlDoc = new DOMParser().parseFromString(event, "application/xml");
    const registros = xmlDoc.getElementsByTagName("RegistroDeFato");
    const tabela = document.getElementById("resultadoTabelaComBoletim");
	
	const getText = (node, tag, fallback = "-") =>
	    node?.getElementsByTagName(tag)[0]?.textContent?.trim() || fallback;

	const getNaturezaDesc = (reg) => {
	    const naturezaNode = reg.getElementsByTagName("naturezaTipo")[0];
	    return getText(naturezaNode, "naturezaDesc", "-");
	};

    tabela.innerHTML = "";
    const registrosAgrupados = new Map();

    for (const reg of registros) {
        const id = getText(reg, "id", null);
        if (!id) continue;

        if (!registrosAgrupados.has(id)) {
            registrosAgrupados.set(id, {
                id,
                tipo: getText(reg, "tipoDescricao"),
                situacao: getText(reg, "statusDescricao"),
                boletimSituacao: "Sem boletim",
                dataEvento: getText(reg, "dataEvento", ""),
                dataCriacao: getText(reg, "dataCriacao", ""),
                dataModificacao: getText(reg, "dataModificada", ""),
                dataEncerramento: getText(reg, "dataEncerramento", ""),
                nomeUsuario: getText(reg, "nomeUsuario"),
                privado: getText(reg, "privado", "0"),
                naturezaFato: getNaturezaDesc(reg),
                individuos: [],
                veiculos: [],
                enderecos: []
            });
        }

        const registro = registrosAgrupados.get(id);

        const boletim = reg.getElementsByTagName("boletim")[0];
        if (boletim) {
            const descricao = getText(boletim, "descricao", null);
            if (descricao) registro.boletimSituacao = descricao;
        }

        processaIndividuos(reg.getElementsByTagName("individuos")[0], registro.individuos);
        processaVeiculos(reg.getElementsByTagName("veiculos")[0], registro.veiculos);
        processaEnderecos(reg.getElementsByTagName("enderecos")[0], registro.enderecos);
    }

    tabela.innerHTML = Array.from(registrosAgrupados.values())
        .map(montarLinhaTabela)
        .join("");

    $('#txt_consulta').quicksearch('#resultadoTabelaComBoletim tr');
}

function montarLinhaTabela(reg) {
    const individuos = reg.individuos.map(i =>
        `${i.nome}${i.cpf !== "-" ? ` (CPF: ${i.cpf})` : ""}`
    ).join("<br> ") || "-";

    const veiculos = reg.veiculos.map(v =>
        `${v.placa}${v.marca !== "-" ? ` (${v.marca}, ${v.modelo}, ${v.cor})` : ""}`
    ).join("<br> ") || "-";

    const enderecos = reg.enderecos.map(e =>
        `${e.rua}, ${e.numero} - ${e.bairro} (${e.cidade})`
    ).join("<br> ") || "-";

    return `
        <tr>
            <td>${reg.tipo}</td>
            <td>${individuos}</td>
            <td>${veiculos}</td>
            <td>${enderecos}</td>
            <td>${reg.situacao}</td>
            <td>${reg.boletimSituacao}</td>
            <td>${reg.naturezaFato}</td>
            <td>${formatarDataBrasileira(reg.dataEvento)}</td>
            <td>${formatarDataBrasileira(reg.dataCriacao)}</td>
            <td>${formatarDataBrasileira(reg.dataModificacao)}</td>
            <td>${formatarDataBrasileira(reg.dataEncerramento)}</td>
            <td>${reg.privado == 1 ? "Privado" : "Público"}</td>
            <td>
                <div class="btn-group">
                    <button onclick="abrirModalEditarRegistroDeFatoAbos(${reg.id})" class="btn btn-primary">
                        <i class="fa fa-pencil"></i>
                    </button>
                    <button onclick="abrirModalHistorico(${reg.id})" class="btn btn-info">
                        <i class="fa fa-history"></i>
                    </button>
                </div>
            </td>
        </tr>
    `;
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

function limparFiltrosComBoletim() {
	// Zerar campos de texto
	document.getElementById("placaRegistroDeFatoComBoletim").value = "";
	document.getElementById("cpfRegistroDeFatoComBoletim").value = "";

	// Resetar selects para o valor vazio (que representa "Todos")
	document.getElementById("cidadeRegistroDeFatoComBoletim").value = "";
	document.getElementById("situacaoRegistroDeFatoComBoletim").value = "";
		
	//Filtros avançados
	document.getElementById("origemBoletim").value = "";
	document.getElementById("infoFaltante").value = "";
	document.getElementById("incluirVeiculos").value = "";
	document.getElementById("filtroObjeto").value = "";
	document.getElementById("comBoletim").value = "";
	document.getElementById("tipoRegistro").value = "";
	document.getElementById("naturezaRegistro").value = "";
	document.getElementById("nomeOperador").value = "";
	document.getElementById("nomeEnvolvido").value = "";	
	document.getElementById("incluirMonitorados").value = "0";
	document.getElementById('tipoAcesso').value = "";

	// Limpar campos de data (se estiver usando datetimepicker do Tempus Dominus)
	const dataInicioInput = document.getElementById("dataInicioRegistroDeFatoComBoletim");
	const dataFimInput = document.getElementById("dataFimRegistroDeFatoComBoletim");
	
	// Limpar campos de data (se estiver usando datetimepicker do Tempus Dominus)
	const dataInicioAlteracao = document.getElementById("dataInicioAlteracao");
	const dataFimAlteracao = document.getElementById("dataFimAlteracao");
	
	// Limpar campos de data (se estiver usando datetimepicker do Tempus Dominus)
	const dataInicioFato = document.getElementById("dataInicioFato");
	const dataFimFato = document.getElementById("dataFimFato");

	dataInicioInput.value = "";
	dataFimInput.value = "";
	
	//Datas de última alteração do histórico
	dataInicioAlteracao.value = "";
	dataFimAlteracao.value = "";
	
	//Datas de ocorrência do evento do fato
	dataInicioFato.value = "";
	dataFimFato.value = "";
	
	//Reabilita os campos
	document.getElementById('incluirMonitorados').disabled = false;			
	document.getElementById('placaRegistroDeFatoComBoletim').disabled = false;	
	document.getElementById('filtroObjeto').disabled = false;
	document.getElementById('incluirVeiculos').disabled = false;
	document.getElementById('nomeEnvolvido').disabled = false;
	document.getElementById('cpfRegistroDeFatoComBoletim').disabled = false;
	document.getElementById('placaRegistroDeFatoComBoletim').disabled = false;		
	document.getElementById('comBoletim').disabled = false;

	// Se estiver usando Tempus Dominus: limpar o valor do picker também
	if (dataInicioInput._td) dataInicioInput._td.clear();
	if (dataFimInput._td) dataFimInput._td.clear();
	
	const novaURL = window.location.origin + window.location.pathname;
	window.history.replaceState({}, document.title, novaURL);
}

async function obterTiposRegistrosDeFatos() {
    const urlPesquisa = "/MuralhaDigital/RegistroDeFato/Tipo";

    try {
        const data = await $.ajax({
            type: "GET",
            url: urlPesquisa,
            dataType: "xml"
        });

        const $xml = $(data);

        let sucesso = $xml.find('sucesso').text();
        let msgResposta = $xml.find('msgResposta').text();

        sucesso = (sucesso === 'true' || sucesso === '');

        if (!sucesso) {
            throw new Error(msgResposta);
        }

        const tipos = data.getElementsByTagName('RegistroDeFatoTipo');
        const select = document.getElementById('tipoRegistro');

        for (let i = 0; i < tipos.length; i++) {
            const id = tipos[i].getElementsByTagName('id')[0].textContent;
            const descricao = tipos[i].getElementsByTagName('descricao')[0].textContent;

            const option = document.createElement('option');
            option.value = id;
            option.textContent = descricao;
            select.appendChild(option);
        }

    } catch (err) {
        ErrorNotification(err.message || 'Erro ao processar requisição ao servidor!!', "");
        throw err;
    }
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
				const select = document.getElementById('situacaoRegistroDeFatoComBoletim');
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
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		}
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
				const select = document.getElementById('cidadeRegistroDeFatoComBoletim');
				// Adiciona cada situação como uma opção
				for (let i = 0; i < situacoes.length; i++) {
					const situacao = situacoes[i];
					const id = situacao.getElementsByTagName('id')[0].textContent;
					const descricao = situacao.getElementsByTagName('nome')[0].textContent;

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
			ErrorNotification('Erro ao processar requisição ao servidor!!', "");
		}
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
	const cpfInput = document.getElementById("cpfRegistroDeFatoComBoletim");

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

function removerParametroUrl(nomeParametro) {
	const url = new URL(window.location.href);
	url.searchParams.delete(nomeParametro);
	history.replaceState({}, document.title, url.toString());
}

function InicializarFiltrosRegistroFato() {
    const chkFiltrosAvancados = document.getElementById('chkFiltrosAvancados');
    const infoFaltante = document.getElementById('infoFaltante');
    const selInclusaoVeiculos = document.getElementById('incluirVeiculos');
    const divFiltrosRegistroFato = document.getElementById('divFiltrosRegistroFato');
    const selTipoRegistro = document.getElementById('tipoRegistro');
    const selNatureza = document.getElementById('naturezaRegistro');
    
    chkFiltrosAvancados.addEventListener('change', function() {
        FILTRO_REGISTRO_FATO_ATIVO = this.checked;
        divFiltrosRegistroFato.style.display = this.checked ? 'block' : 'none';
        
        if (!this.checked) {
            selTipoRegistro.value = '';
            selNatureza.innerHTML = '<option value="">Todos</option>';
            selNatureza.disabled = true;
        } else {
            CarregarTodosTiposRegistro();
            CarregarTodasNaturezas();
        }
    });
    
    infoFaltante.addEventListener('change', function() {
		const value = document.getElementById('infoFaltante').value;
		
		if(value === "1"){
			document.getElementById('filtroObjeto').disabled = true;
			document.getElementById('incluirMonitorados').disabled = true;
			document.getElementById('incluirVeiculos').disabled = true;
			document.getElementById('nomeEnvolvido').disabled = true;
			document.getElementById('cpfRegistroDeFatoComBoletim').disabled = true;
			document.getElementById('placaRegistroDeFatoComBoletim').disabled = true;
		}else{
			document.getElementById('filtroObjeto').disabled = false;
			document.getElementById('incluirMonitorados').disabled = false;
			document.getElementById('incluirVeiculos').disabled = false;
			document.getElementById('nomeEnvolvido').disabled = false;
			document.getElementById('cpfRegistroDeFatoComBoletim').disabled = false;
			document.getElementById('placaRegistroDeFatoComBoletim').disabled = false;		
		}
    });
    
   document.getElementById('origemBoletim').addEventListener('change', function() {
	    const origemValue = this.value;
	
	    if (origemValue === "") {
	        document.getElementById('comBoletim').disabled = false;
	        document.getElementById('comBoletim').value = "";
	    } else {
	        document.getElementById('comBoletim').value = 'COM_BOLETIM';
	        document.getElementById('comBoletim').disabled = true;
	    }
	});
    
    selInclusaoVeiculos.addEventListener('change', function() {
		const value = document.getElementById('incluirVeiculos').value;
		
		if(value === "0"){
			document.getElementById('incluirMonitorados').disabled = true;
			document.getElementById('incluirMonitorados').value = 0;
			document.getElementById('placaRegistroDeFatoComBoletim').disabled = true;
		}else{
			document.getElementById('incluirMonitorados').disabled = false;			
			document.getElementById('placaRegistroDeFatoComBoletim').disabled = false;
		}
    });
    
    selTipoRegistro.addEventListener('change', function() {
        const tipoId = this.value;
        selNatureza.innerHTML = '<option value="">Carregando...</option>';
        
        if (tipoId) {
            $.ajax({
                url: '/MuralhaDigital/RegistroDeFato/Natureza',
                data: { idTipo: tipoId },
                type: 'GET',
                dataType: 'xml',
                success: function(xml) {
                    selNatureza.innerHTML = '';
                    
                    $(xml).find('RegistroDeFatoNatureza').each(function() {
                        const id = $(this).find('id').text();
                        const descricao = $(this).find('naturezaDesc').text();
                        
                        const option = document.createElement('option');
                        option.value = id;
                        option.textContent = descricao;
                        selNatureza.appendChild(option);
                    });
                    
                    selNatureza.disabled = false;
                },
                error: function() {
                    selNatureza.innerHTML = '<option value="">Erro ao carregar</option>';
                }
            });
        } else {
            CarregarTodasNaturezas();
        }
    });
    
    function CarregarTodosTiposRegistro() {
        $.ajax({
            url: '/MuralhaDigital/RegistroDeFato/Tipo',
            type: 'GET',
            dataType: 'xml',
            success: function(xml) {
                selTipoRegistro.innerHTML = '<option value="">Todos</option>';
                
                $(xml).find('RegistroDeFatoTipo').each(function() {
                    const id = $(this).find('id').text();
                    const descricao = $(this).find('descricao').text();
                    
                    const option = document.createElement('option');
                    option.value = id;
                    option.textContent = descricao;
                    selTipoRegistro.appendChild(option);
                });
                
                selTipoRegistro.disabled = false;
            },
            error: function() {
                selTipoRegistro.innerHTML = '<option value="">Erro ao carregar</option>';
            }
        });
    }
    
    function CarregarTodasNaturezas() {
        $.ajax({
            url: '/MuralhaDigital/RegistroDeFato/Natureza',
            type: 'GET',
            dataType: 'xml',
            success: function(xml) {
                selNatureza.innerHTML = '<option value="">Todos</option>';
                
                $(xml).find('RegistroDeFatoNatureza').each(function() {
                    const id = $(this).find('id').text();
                    const descricao = $(this).find('naturezaDesc').text();
                    
                    const option = document.createElement('option');
                    option.value = id;
                    option.textContent = descricao;
                    selNatureza.appendChild(option);
                });
                
                selNatureza.disabled = false;
            },
            error: function() {
                selNatureza.innerHTML = '<option value="">Erro ao carregar</option>';
            }
        });
    }
    
    CarregarTodosTiposRegistro();
    CarregarTodasNaturezas();
}

function CarregarTodosTiposRegistro() {
    $.ajax({
        url: '/MuralhaDigital/RegistroDeFato/Tipo',
        type: 'GET',
        dataType: 'xml',
        success: function(xml) {
            selTipoRegistro.innerHTML = '<option value="">Todos</option>';
            
            $(xml).find('RegistroDeFatoTipo').each(function() {
                const id = $(this).find('id').text();
                const descricao = $(this).find('descricao').text();
                
                const option = document.createElement('option');
                option.value = id;
                option.textContent = descricao;
                selTipoRegistro.appendChild(option);
            });
            
            selTipoRegistro.disabled = false;
        },
        error: function() {
            selTipoRegistro.innerHTML = '<option value="">Erro ao carregar</option>';
        }
    });
}
    
function CarregarTodasNaturezas() {
    $.ajax({
        url: '/MuralhaDigital/RegistroDeFato/Natureza',
        type: 'GET',
        dataType: 'xml',
        success: function(xml) {
            selNatureza.innerHTML = '<option value="">Todos</option>';
            
            $(xml).find('RegistroDeFatoNatureza').each(function() {
                const id = $(this).find('id').text();
                const descricao = $(this).find('naturezaDesc').text();
                
                const option = document.createElement('option');
                option.value = id;
                option.textContent = descricao;
                selNatureza.appendChild(option);
            });
            
            selNatureza.disabled = false;
        },
        error: function() {
            selNatureza.innerHTML = '<option value="">Erro ao carregar</option>';
        }
    });     
      
    CarregarTodosTiposRegistro();
    CarregarTodasNaturezas();
}

function obterUsuarios(callback) {
	const url = "/MuralhaDigital/RegistroDeFato/Grupo?acao=obterTodos";

	$.ajax({
		type: "GET",
		url: url,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			try {
				const xmlDoc = new DOMParser().parseFromString(jqXHR.responseText, "application/xml");

				// === PROCESSAR USUÁRIOS ===
				const usuarios = xmlDoc.getElementsByTagName("usuario");
				const selectUsuarios = document.getElementById("nomeOperador");
				//selectUsuarios.innerHTML = "";

				window.usuariosDisponiveis = [];				

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

						window.usuariosDisponiveis.push({ id: idUsuario, nome: nome, login: login });
					}
				}

			} catch (e) {
				Swal.fire({
					icon: 'error',
					title: 'Erro ao processar dados',
					text: 'Ocorreu um erro ao processar os dados recebidos do servidor.',
				});
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			Swal.fire({
				icon: 'error',
				title: 'Erro na requisição',
				text: 'Não foi possível processar a requisição ao servidor.',
			});
		}
	});
}

function limparOrdenacaoTabelaComBoletim() {
    // Variáveis globais (crie-as se ainda não existirem)
    // Essas variáveis podem ser usadas ao montar params para o backend.
    ORDER_BY = null;
    ORDER_DIR = null;

    // Lista de classes comuns que podem indicar ordenacao — remova-as
    const classesOrdenacao = [
        'sorting', 'sorting_asc', 'sorting_desc', // datatables style
        'sort-asc', 'sort-desc', 'asc', 'desc',
        'sorted', 'is-sorted'
    ];

    // Seleciona todos os th da tabela alvo
    const ths = document.querySelectorAll('#resultadoTabelaComBoletim').closest
        ? document.querySelectorAll('#resultadoTabelaComBoletim').closest('table').querySelectorAll('th')
        : document.querySelectorAll('table#resultadoTabelaComBoletim th'); // fallback (provavelmente o primeiro funciona)

    // Se a seleção acima falhar, tenta selecionar todos os th dentro da tabela principal
    let thCollection = document.querySelectorAll('.table thead th');
    if (ths && ths.length > 0) thCollection = ths;

    thCollection.forEach(th => {
        // Remove classes comuns
        classesOrdenacao.forEach(cls => th.classList.remove(cls));

        // Remove atributos de ordenacao/data
        th.removeAttribute('data-order-by');
        th.removeAttribute('data-order-dir');
        th.removeAttribute('aria-sort');

        // Remover estilos inline de destaque (ex: backgroundColor, color)
        th.style.backgroundColor = '';
        th.style.color = '';
        th.style.fontWeight = '';

        // Reset dos ícones FontAwesome (se estiverem dentro do TH)
        // Procura <i class="fa ..."> ou <i class="fas ...">
        const icon = th.querySelector('i.fa, i.fas, i.far, i.fal, i.fab');
        if (icon) {
            // remove classes especificas de seta
            icon.classList.remove('fa-sort-up', 'fa-sort-down', 'fa-sort-asc', 'fa-sort-desc', 'fa-sort');
            // adiciona icone padrão de sort (opcional)
            icon.classList.add('fa-sort');
        } else {
            // se não houver <i>, mas houver span de ícone, tenta normalizar
            const spanIcon = th.querySelector('.sort-icon');
            if (spanIcon) {
                spanIcon.className = 'sort-icon'; // limpa classes
                spanIcon.textContent = ''; // ou coloque um caractere padrão se desejar
            }
        }
    });

    // Remove parametros de ordenacao da URL (se estiverem presentes)
    try {
        const url = new URL(window.location.href);
        if (url.searchParams.has('orderBy')) url.searchParams.delete('orderBy');
        if (url.searchParams.has('orderDir')) url.searchParams.delete('orderDir');
        history.replaceState({}, document.title, url.toString());
    } catch (e) {
        // ignore se URL inválida (raro)
    }
}

function obterGrupoUsuario() {
    var urlPesquisa = "/MuralhaDigital/RegistroDeFato/Grupo";

    const params = new URLSearchParams({
        acao: 'obterTodosPorUsuarioId'
    });

    $.ajax({
        type: "GET",
        url: urlPesquisa,
        data: params.toString(),
        dataType: "xml",

        success: function (data) {
            var $xml = $(data);

            var sucesso = $xml.find('sucesso').text();
            var msgResposta = $xml.find('msgResposta').text();

            sucesso = (sucesso === 'true' || sucesso === '');

            if (!sucesso) {
                WarningCsx_E_TimeOut_8000ms(msgResposta);
                return;
            }

            var $select = $("#acessoPermitido");

            // Limpa opções mantendo "Todos"
            $select.find("option:not(:first)").remove();

            $xml.find("grupos > grupo").each(function () {
                var idGrupo = $(this).find("id_grupo").text().trim();
                var descricao = $(this).find("descricao").text().trim();

                $select.append(
                    $("<option>", {
                        value: idGrupo,
                        text: descricao
                    })
                );
            });
        },

        error: function () {
            ErrorNotification('Erro ao processar requisição ao servidor!!', "");
        }
    });
}
(function () {
    let ajaxCount = 0;

    $(document).ajaxStart(function () {
        ajaxCount++;
        $("#global-loading").removeClass("d-none");
    });

    $(document).ajaxStop(function () {
        ajaxCount = 0;
        $("#global-loading").addClass("d-none");
    });

    $(document).ajaxError(function () {
        ajaxCount = 0;
        $("#global-loading").addClass("d-none");
    });
})();