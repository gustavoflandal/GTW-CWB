window.apreensoes = window.apreensoes || [];
function adicionarApreensao() {
	const tipoEl = document.getElementById("tipo");
	const descricaoEl = document.getElementById("descricao");

	let valido = true;

	// Resetar classes de validação
	[tipoEl, descricaoEl].forEach(el => el.classList.remove("is-invalid"));

	if (!tipoEl.value.trim()) {
		tipoEl.classList.add("is-invalid");
		valido = false;
	}
	if (!descricaoEl.value.trim()) {
		descricaoEl.classList.add("is-invalid");
		valido = false;
	}

	if (!valido) return;

	window.apreensoes.push({
		tipo: tipoEl.value.trim().toUpperCase(),
		descricao: descricaoEl.value.trim()
	});

	atualizarTabelaApreensoes();
	document.getElementById("formApreensao").reset();
}

function atualizarTabelaApreensoes() {
	const tbody = document.getElementById("listApreensoes");
	tbody.innerHTML = "";

	window.apreensoes.forEach((item, index) => {
		const tr = document.createElement("tr");
		tr.innerHTML = `
                <td>
                    <input type="hidden" name="tipo[]" value="${item.tipo}">
                    ${item.tipo}
                </td>
                <td>
                    <input type="hidden" name="descricao[]" value="${item.descricao}">
                    ${item.descricao}
                </td>
                <td class="text-center">
                    <button type="button" title='Remover' class="btn btn-sm btn-danger" onclick="removerApreensao(${index})"><i class="fa fa-trash"></i></button>
                </td>
            `;
		tbody.appendChild(tr);
	});
}

function removerApreensao(index) {
	window.apreensoes.splice(index, 1);
	atualizarTabelaApreensoes();
}

function getApreensoesBoletim()
{
	return window.apreensoes;
}

function limparCamposEListaApreensoes() {
  const form = document.getElementById('formApreensao');
  if (!form) return;

  // Limpa os campos do formulário
  form.reset();

  // Limpa o tbody da tabela
  const tbody = document.getElementById('listApreensoes');
  if (tbody) {
    tbody.innerHTML = '';
  }
  window.apreensoes = [];
}

function bloquearCamposApreensoes() {
  const form = document.getElementById('formApreensao');
  if (!form) return;

  const elements = form.querySelectorAll('input, textarea, button');
  elements.forEach(el => {
    el.disabled = true;
  });
}

function desbloquearCamposApreensoes() {
  const form = document.getElementById('formApreensao');
  if (!form) return;

  const elements = form.querySelectorAll('input, textarea, button');
  elements.forEach(el => {
    el.disabled = false;
  });
}