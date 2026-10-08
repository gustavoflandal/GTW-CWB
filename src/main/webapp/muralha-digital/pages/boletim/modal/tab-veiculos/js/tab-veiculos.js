window.veiculosModal = window.veiculosModal || [];
function limparPlaca(valor) {
	return valor.replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
}

function adicionarVeiculo() {
	const placaEl = document.getElementById("placa");
	const corEl = document.getElementById("cor");
	const marcaEl = document.getElementById("marca");
	const modeloEl = document.getElementById("modelo");

	let valido = true;

	// Resetar validação visual
	[placaEl, corEl, marcaEl, modeloEl].forEach(el => el.classList.remove("is-invalid"));

	// Validação manual
	if (!placaEl.value.trim()) { placaEl.classList.add("is-invalid"); valido = false; }
	if (!corEl.value.trim()) { corEl.classList.add("is-invalid"); valido = false; }
	if (!marcaEl.value.trim()) { marcaEl.classList.add("is-invalid"); valido = false; }
	if (!modeloEl.value.trim()) { modeloEl.classList.add("is-invalid"); valido = false; }

	if (!valido) return;

	window.veiculosModal.push({
		placa: limparPlaca(placaEl.value),
		cor: corEl.value.trim().toUpperCase(),
		marca: marcaEl.value.trim().toUpperCase(),
		modelo: modeloEl.value.trim().toUpperCase()
	});

	atualizarTabelaVeiculos();
	document.getElementById("formVeiculo").reset();
}

function atualizarTabelaVeiculos() {
	const tbody = document.getElementById("listVeiculos");
	tbody.innerHTML = "";

	veiculosModal.forEach((v, index) => {
		const tr = document.createElement("tr");
		tr.innerHTML = `
                <td>
                    <input type="hidden" name="placa[]" value="${v.placa}">
                    ${v.placa}
                </td>
                <td>
                    <input type="hidden" name="cor[]" value="${v.cor}">
                    ${v.cor}
                </td>
                <td>
                    <input type="hidden" name="marca[]" value="${v.marca}">
                    ${v.marca}
                </td>
                <td>
                    <input type="hidden" name="modelo[]" value="${v.modelo}">
                    ${v.modelo}
                </td>
                <td class="text-center">
                    <button type="button" class="btn btn-sm btn-danger" title='Remover' onclick="removerVeiculo(${index})"><i class="fa fa-trash"></i></button>
                </td>
            `;
		tbody.appendChild(tr);
	});
}

function removerVeiculo(index) {
	window.veiculosModal.splice(index, 1);
	atualizarTabelaVeiculos();
}

function getVeiculosBoletim()
{
	return window.veiculosModal;
}

function limparCamposEListaVeiculos() {
  const form = document.getElementById('formVeiculo');
  if (!form) return;

  // Limpa os campos do formulário
  form.reset();

  // Limpa o corpo da tabela (tbody)
  const tbody = document.getElementById('listVeiculos');
  if (tbody) {
    tbody.innerHTML = '';
  }
  window.veiculosModal = [];
}

function bloquearCamposVeiculos() {
  const form = document.getElementById('formVeiculo');
  if (!form) return;

  const elements = form.querySelectorAll('input, select, textarea, button');
  elements.forEach(el => {
    // Desabilita todos os inputs e o botão de submit
    el.disabled = true;
  });
}

function desbloquearCamposVeiculos() {
  const form = document.getElementById('formVeiculo');
  if (!form) return;

  const elements = form.querySelectorAll('input, select, textarea, button');
  elements.forEach(el => {
    el.disabled = false;
  });
}