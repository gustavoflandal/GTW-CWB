window.listaVeiculosComBoletim = [];

function adicionarVeiculoComBoletim() {
	// pegar valores dos inputs
	const placa = document.getElementById('placaComBoletim').value.trim().toUpperCase();
	const cor = document.getElementById('corComBoletim').value.trim();
	const marca = document.getElementById('marcaComBoletim').value.trim();
	const modelo = document.getElementById('modeloComBoletim').value.trim();

	// checkbox de monitoramento
	const monitoradoAtivo = document.getElementById('habilitarMonitoramentoComBoletim').checked;

	let idTipoAlerta = null; 
    let descricaoTipoAlerta = null;
	let dataInicio = null; 
	let dataFim = null;
	let descricao = null;
	let nome = null;

	// validação simples: verificar se placa já foi adicionada
	if (window.listaVeiculosComBoletim.some(v => v.placa === placa)) {
		Swal.fire({
            icon: 'warning',
            title: 'Atenção',
            text: 'Veículo com esta placa já está na lista!',
            confirmButtonColor: '#007bff'
        });
		return;
	}

	// Se monitoramento estiver ativo, valida campos obrigatórios
	if (monitoradoAtivo) {
		const selectTipoAlerta = document.getElementById('tipoAlertaComBoletim');

		idTipoAlerta = selectTipoAlerta.value.trim(); 
        descricaoTipoAlerta = selectTipoAlerta.options[selectTipoAlerta.selectedIndex].text;

		const inputDataInicio = document.getElementById('dataInicioComBoletim');
		if (inputDataInicio) {
			dataInicio = inputDataInicio.value ? inputDataInicio.value : null;
		}

		descricao = document.getElementById('descricaoComBoletim').value.trim();
		nome = document.getElementById('nomeMonitorado').value.trim();

		if (!idTipoAlerta) {
			Swal.fire({
                icon: 'warning',
                title: 'Atenção',
                text: 'Selecione o Tipo do Alerta.',
                confirmButtonColor: '#007bff'
            });
			return;
		}
		if (!descricao) {
			Swal.fire({
                icon: 'warning',
                title: 'Atenção',
                text: 'Preencha a Descrição.',
                confirmButtonColor: '#007bff'
            });
			return;
		}
		if (descricao.length > 300) {
			Swal.fire({
                icon: 'warning',
                title: 'Atenção',
                text: 'A descrição deve ter no máximo 300 caracteres.',
                confirmButtonColor: '#007bff'
            });
			return;
		}
		if (nome.length > 200) {
			Swal.fire({
                icon: 'warning',
                title: 'Atenção',
                text: 'O nome deve ter no máximo 200 caracteres.',
                confirmButtonColor: '#007bff'
            });
			return;
		}
	}

	// adiciona no array
	window.listaVeiculosComBoletim.push({
		id: null,
		placa,
		cor,
		marca,
		modelo,
		monitorado: monitoradoAtivo,
		idTipoAlerta,
        descricaoTipoAlerta,
		dataInicio,
		dataFim: null,
		descricao,
		nome,
		status: 'novo'
	});

	// atualiza a tabela
	atualizarTabelaVeiculosComBoletim();

	// limpa o formulário
	document.getElementById('formVeiculoComBoletim').reset();
	document.getElementById('placaComBoletim').focus();
	document.getElementById('secaoMonitoramentoComBoletim').style.display = 'none';
}


function removerVeiculoComBoletim(placa) {
	const veiculo = window.listaVeiculosComBoletim.find(v => v.placa === placa);
	if (veiculo) {
		if (veiculo.status === 'novo') {
			window.listaVeiculosComBoletim = window.listaVeiculosComBoletim.filter(v => v.placa !== placa);
		} else {
			veiculo.status = 'removido';
		}
		atualizarTabelaVeiculosComBoletim();
	}
}

function atualizarTabelaVeiculosComBoletim() {
	const tbody = document.getElementById('listVeiculosComBoletim');
	tbody.innerHTML = '';

	window.listaVeiculosComBoletim.forEach((veiculo, index) => {
		let placaDisplay = veiculo.placa;
		let corDisplay = veiculo.cor;
		let marcaDisplay = veiculo.marca;
		let modeloDisplay = veiculo.modelo;

		if (veiculo.status === 'removido') {
			placaDisplay = `<del>${placaDisplay}</del>`;
			corDisplay = `<del>${corDisplay}</del>`;
			marcaDisplay = `<del>${marcaDisplay}</del>`;
			modeloDisplay = `<del>${modeloDisplay}</del>`;
		} else if (veiculo.status === 'novo') {
			placaDisplay += ' <span class="badge bg-success">Novo</span>';
		}

		const btnRemover = veiculo.status === 'removido' ? '' :
			`<button class="btn btn-sm btn-danger" onclick="removerVeiculoComBoletim('${veiculo.placa}')">
				<i class="fa fa-trash"></i>
			</button>`;

		const tr = document.createElement('tr');
		tr.innerHTML = `
			<td>${placaDisplay}</td>
			<td>${corDisplay}</td>
			<td>${marcaDisplay}</td>
			<td>${modeloDisplay}</td>
			<td class="text-center justify-content-center d-flex gap-2">${btnRemover}
				<a href="/muralha-digital/pages/consulta-veiculo/consulta.jsp?placa=${placaDisplay}" target="_blank" class="btn btn-warning d-flex align-items-center justify-content-center p-2"
			          title="Pesquisa por passagem de veículo">
					<svg xmlns="http://www.w3.org/2000/svg" width="15" height="20" viewBox="0 0 256 256"><path fill="#000000" d="M248 136a8 8 0 0 0-8 8v16h-44.69L177 141.66l50.34-50.35a16 16 0 0 0 0-22.62l-56-56a16 16 0 0 0-22.63 0L2.92 158.94A10 10 0 0 0 10 176h39.37l35.32 35.31a16 16 0 0 0 22.62 0L165.66 153L184 171.31a15.86 15.86 0 0 0 11.31 4.69H240v16a8 8 0 0 0 16 0v-48a8 8 0 0 0-8-8M160 24l12.69 12.69L49.37 160H24.46ZM96 200l-32-32L184 48l32 32Z"/></svg>
			  	</a>
			</td>
		`;
		tbody.appendChild(tr);
	});
}

function limparCamposVeiculoComBoletim() {
    // limpa formulário
    document.getElementById("formVeiculoComBoletim").reset();

    // limpa array global
    window.listaVeiculosComBoletim = [];

    // limpa tabela no DOM
    document.getElementById("listVeiculosComBoletim").innerHTML = "";

    // esconde seção monitoramento
    document.getElementById('secaoMonitoramentoComBoletim').style.display = 'none';
}

function getVeiculosComBoletim() {
	return window.listaVeiculosComBoletim;
}

// evento para mostrar/ocultar seção monitoramento
document.addEventListener('DOMContentLoaded', function () {
	const checkbox = document.getElementById('habilitarMonitoramentoComBoletim');
	if (!checkbox) return; // evita erro se o elemento não existir

	checkbox.addEventListener('change', function () {
		const ativo = this.checked;
		const secao = document.getElementById('secaoMonitoramentoComBoletim');
		secao.style.display = ativo ? 'block' : 'none';
		secao.querySelectorAll('input, select, textarea').forEach(campo => {
			campo.disabled = !ativo;
		});
	});
});

function preencherTabVeiculosComBoletim(xmlString) {
  // Parse o XML
  const parser = new DOMParser();
  const xmlDoc = parser.parseFromString(xmlString, "application/xml");

  // Limpa a lista atual
  window.listaVeiculosComBoletim = [];

  // Pega todos os elementos <veiculo>
  const veiculos = xmlDoc.querySelectorAll("RegistroDeFato > veiculos > veiculo");

  veiculos.forEach(veiculoNode => {
	const id = parseInt(veiculoNode.querySelector("id")?.textContent?.trim()) || null;
    const placa = veiculoNode.querySelector("placa")?.textContent?.trim() || "";
    const cor = veiculoNode.querySelector("cor")?.textContent?.trim() || "";
    const marca = veiculoNode.querySelector("marca")?.textContent?.trim() || "";
    const modelo = veiculoNode.querySelector("modelo")?.textContent?.trim() || "";

    // Adiciona na lista global
    window.listaVeiculosComBoletim.push({
	  id,
      placa,
      cor,
      marca,
      modelo,
      monitorado: false,
      idTipoAlerta: null,
      descricaoTipoAlerta: null,
	  dataInicio: null,
      descricao: null,
      nome: null,
	  status: null
    });
  });

  // Atualiza a tabela
  atualizarTabelaVeiculosComBoletim();
}

