window.individuosBoletim = window.individuosBoletim || [];
function adicionarEnvolvido() {
	const id_tipo_envolvimento = document.getElementById("tipoEnvolvimento");
	const nome = document.getElementById("nome");
	const cpf = document.getElementById("cpfModalBoletim");
	const detalhe_envolvimento = document.getElementById("detalheEnvolvimento");

	let valido = true;
	let mensagensErro = [];

	// Limpa validações anteriores
	[id_tipo_envolvimento, nome, cpf].forEach(el => el.classList.remove("is-invalid"));

	// Valida tipo de envolvimento
	if (!id_tipo_envolvimento.value) {
		id_tipo_envolvimento.classList.add("is-invalid");
		valido = false;
	}

	// Valida nome
	if (!nome.value.trim()) {
		nome.classList.add("is-invalid");
		valido = false;
	}

	// Valida CPF
	const cpfNumerico = cpf.value.replace(/\D/g, '');

	if (!cpf.value.trim() || !cpfValido(cpfNumerico)) {
		cpf.classList.add("is-invalid");
		valido = false;
		mensagensErro.push("CPF inválido.");
	}

	if (!valido) {
		let mensagem = "Verifique os campos marcados em vermelho.";
		if (mensagensErro.length > 0) {
			mensagem += "\n" + mensagensErro.join("\n");
		}
		ErrorNotification("Campos obrigatórios inválidos", mensagem);
		return;
	}

	// Adiciona à lista
	window.individuosBoletim.push({
		id_tipo_envolvimento: id_tipo_envolvimento.value,
		nome: nome.value.trim(),
		cpf: cpfNumerico,
		detalhe_envolvimento: detalhe_envolvimento.value.trim()
	});

	atualizarTabelaIndividuos();
	document.getElementById("formEnvolvido").reset();
}

function atualizarTabelaIndividuos() {
	const tbody = document.getElementById("listIndividuos");
	tbody.innerHTML = "";

	window.individuosBoletim.forEach((item, index) => {
		const tr = document.createElement("tr");
		tr.innerHTML = `
        <td>
            <input type="hidden" name="tipo_envolvimento[]" value="${item.id_tipo_envolvimento}">
            ${tipoParaTexto(item.id_tipo_envolvimento)}
        </td>
        <td>
            <input type="hidden" name="nome[]" value="${item.nome}">
            ${item.nome}
        </td>
        <td>
            <input type="hidden" name="cpf[]" value="${item.cpf}">
            ${formatarCPF(item.cpf)}
        </td>
        <td>
            <input type="hidden" name="detalhe_envolvimento[]" value="${item.detalhe_envolvimento}">
            ${item.detalhe_envolvimento}
        </td>
        <td>
        	<div class="d-flex justify-content-center">
        		<button class="btn btn-sm btn-danger" title='Remover' onclick="removerLinha(this)"><i class="fa fa-trash"></i></button>
        	</div>
        </td>
    `;
		tbody.appendChild(tr);
	});
}

function removerLinha(botao) {
	const row = botao.closest("tr");
	row.remove();
}

function getIndividuosBoletim() {
	return window.individuosBoletim;
}

document.addEventListener("DOMContentLoaded", function() {
	const cpfInput = document.getElementById("cpfModalBoletim");

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

function formatarCPF(cpf) {
	const numeros = cpf.replace(/\D/g, "").padStart(11, "0").slice(0, 11);

	if (numeros.length !== 11) return cpf;

	return numeros.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
}

function tipoParaTexto(id) {
	const select = document.getElementById("tipoEnvolvimento");
	const option = select.querySelector(`option[value="${id}"]`);
	return option ? option.textContent : "";
}

function limparCamposEListaIndividuos() {
	document.getElementById("formEnvolvido").reset();
	document.getElementById("listIndividuos").innerHTML = "";
	window.individuosBoletim = [];
}

function bloquearCamposIndividuos() {
	const form = document.getElementById('formEnvolvido');
	if (!form) return;

	const elements = form.querySelectorAll('input, textarea, button , select');
	elements.forEach(el => el.disabled = true);
}

function desbloquearCamposIndividuos() {
	const form = document.getElementById('formEnvolvido');
	if (!form) return;

	const elements = form.querySelectorAll('input, textarea, button, select');
	elements.forEach(el => el.disabled = false);
}

function cpfValido(cpf) {
	cpf = cpf.replace(/[^\d]+/g, '');

	if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false;

	let soma = 0, resto;

	for (let i = 1; i <= 9; i++) soma += parseInt(cpf[i - 1]) * (11 - i);
	resto = (soma * 10) % 11;
	if (resto === 10 || resto === 11) resto = 0;
	if (resto !== parseInt(cpf[9])) return false;

	soma = 0;
	for (let i = 1; i <= 10; i++) soma += parseInt(cpf[i - 1]) * (12 - i);
	resto = (soma * 10) % 11;
	if (resto === 10 || resto === 11) resto = 0;
	if (resto !== parseInt(cpf[10])) return false;

	return true;
}

function SuccessNotification(title, text) {
	//precisa <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
		title: title,
		icon: "success",
		text: text,
	});
}

function ErrorNotification(title, text) {
	//precisa <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
		title: title,
		icon: "error",
		text: text,
	});
}
