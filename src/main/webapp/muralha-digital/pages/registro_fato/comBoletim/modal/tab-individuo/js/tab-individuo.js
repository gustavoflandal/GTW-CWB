window.envolvidosComBoletim = window.envolvidosComBoletim || [];

document.addEventListener("DOMContentLoaded", function () {
	const cpfInput = document.getElementById("cpfComBoletim");
	const dddInput = document.getElementById("dddComBoletim");
	const telefoneInput = document.getElementById("telefoneComBoletim");

	// Máscara para CPF
	cpfInput.addEventListener("input", function (e) {
		let value = e.target.value.replace(/\D/g, "");
		if (value.length > 11) value = value.substring(0, 11);
		value = value.replace(/(\d{3})(\d)/, "$1.$2");
		value = value.replace(/(\d{3})(\d)/, "$1.$2");
		value = value.replace(/(\d{3})(\d{1,2})$/, "$1-$2");
		e.target.value = value;
	});

	// Limita DDD a 2 dígitos
	dddInput.addEventListener("input", function (e) {
		e.target.value = e.target.value.replace(/\D/g, "").substring(0, 2);
	});

	// Limita telefone a 9 dígitos
	telefoneInput.addEventListener("input", function (e) {
		let value = e.target.value.replace(/\D/g, ""); // Remove tudo que não for número

		if (value.length > 9) value = value.substring(0, 9);

		if (value.length > 5) {
			e.target.value = value.replace(/(\d{5})(\d{1,4})/, "$1-$2"); // Ex: 91234-5678
		} else if (value.length > 4) {
			e.target.value = value.replace(/(\d{4})(\d{1,1})/, "$1-$2"); // Ex: 1234-5
		} else {
			e.target.value = value;
		}
	});
});

function adicionarEnvolvidoComBoletim() {
	const select = document.getElementById("tipoEnvolvimentoComBoletim");
	const idTipoEnvolvimento = select.value.trim();
	const descricaoTipoEnvolvimento = select.options[select.selectedIndex].text;

	const cpfInput = document.getElementById("cpfComBoletim");
	const nomeInput = document.getElementById("nomeComBoletim");
	const dddInput = document.getElementById("dddComBoletim");
	const telefoneInput = document.getElementById("telefoneComBoletim");
	const emailInput = document.getElementById("emailComBoletim");

	const cpf = cpfInput.value.trim();
	const nome = nomeInput.value.trim();
	const ddd = dddInput.value.trim();
	const telefone = telefoneInput.value.trim();
	const email = emailInput.value.trim();
	const detalheEnvolvimento = document.getElementById("detalheEnvolvimentoComBoletim").value.trim();

	// Limpa erros anteriores
	[cpfInput, nomeInput, select, dddInput, telefoneInput, emailInput].forEach(el => el.classList.remove("is-invalid"));

	let erro = false;

	if (!idTipoEnvolvimento) {
		select.classList.add("is-invalid");
		erro = true;
	}

	const cpfNumerico = cpf.replace(/\D/g, "");

	if (!cpf) {
	    cpfInput.classList.add("is-invalid");
	    Swal.fire("Campo obrigatório", "Informe o CPF.", "warning");
	    return;
	} else if (cpfNumerico.length < 11) {
	    cpfInput.classList.add("is-invalid");
	    Swal.fire("CPF incompleto", "O CPF deve conter 11 dígitos.", "error");
	    return;
	} else if (!validarCPF(cpfNumerico)) {
	    cpfInput.classList.add("is-invalid");
	    Swal.fire("CPF inválido", "Digite um CPF válido.", "error");
	    return;
	}

	if (!nome) {
		nomeInput.classList.add("is-invalid");
		erro = true;
	}

	if (ddd && !/^\d{2}$/.test(ddd)) {
		dddInput.classList.add("is-invalid");
		erro = true;
	}

	const telefoneNumerico = telefone.replace(/\D/g, "");

	if (telefone && !/^\d{8,9}$/.test(telefoneNumerico)) {
		telefoneInput.classList.add("is-invalid");
		erro = true;
	}

	if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
		emailInput.classList.add("is-invalid");
		erro = true;
	}

	if (erro) {
		Swal.fire("Campos obrigatórios", "Verifique os campos destacados!", "warning");
		return;
	}

	const envolvido = {
	    id: null,
	    idTipoEnvolvimento,
	    descricaoTipoEnvolvimento,
	    cpf: cpf.replace(/\D/g, ""),
	    nome,
	    ddd: ddd.replace(/\D/g, ""),
	    telefone: telefone.replace(/\D/g, ""),
	    email,
	    detalheEnvolvimento,
	    status: "novo"
	};

	window.envolvidosComBoletim.push(envolvido);
	atualizarTabelaEnvolvidosComBoletim();
	limparFormularioEnvolvidoComBoletim();
}

function removerEnvolvidoComBoletim(index) {
    const envolvido = window.envolvidosComBoletim[index];

    if (envolvido.status === "novo") {
        window.envolvidosComBoletim.splice(index, 1);
    } else {
        envolvido.status = "removido";
    }

    atualizarTabelaEnvolvidosComBoletim();
}

function atualizarTabelaEnvolvidosComBoletim() {
    const tbody = document.getElementById("listIndividuosComBoletim");
    tbody.innerHTML = "";

    window.envolvidosComBoletim.forEach((envolvido, index) => {
        const tr = document.createElement("tr");

        let nomeTexto = envolvido.nome;
        let cpfTexto = formatarCPF(envolvido.cpf);
        let dddTexto = envolvido.ddd || "";
        let telefoneTexto = formatarTelefone(envolvido.telefone);
        let emailTexto = envolvido.email || "";
        let detalheTexto = envolvido.detalheEnvolvimento || "";

        // Adiciona badge visual
        if (envolvido.status === "novo") {
            nomeTexto += ' <span class="badge bg-success">Novo</span>';
        } else if (envolvido.status === "removido") {
            nomeTexto = `<del>${nomeTexto}</del>`;
            cpfTexto = `<del>${cpfTexto}</del>`;
            dddTexto = `<del>${dddTexto}</del>`;
            telefoneTexto = `<del>${telefoneTexto}</del>`;
            emailTexto = `<del>${emailTexto}</del>`;
            detalheTexto = `<del>${detalheTexto}</del>`;
        }

        // Botão de remoção apenas altera status para "removido"
        const btnRemover = envolvido.status === "removido"
            ? ''
            : `<button class="btn btn-sm btn-danger" title="Remover" onclick="removerEnvolvidoComBoletim(${index})">
                   <i class="fa fa-trash"></i>
               </button>`;

        tr.innerHTML = `
            <td>${envolvido.descricaoTipoEnvolvimento || envolvido.idTipoEnvolvimento}</td>
            <td>${nomeTexto}</td>
            <td>${cpfTexto}</td>
            <td>${dddTexto}</td>
            <td>${telefoneTexto}</td>
            <td>${emailTexto}</td>
            <td>${detalheTexto}</td>
            <td class="text-center">${btnRemover}</td>
        `;

        tbody.appendChild(tr);
    });
}

function formatarCPF(cpf) {
	if (!cpf) return "";
	cpf = cpf.toString().padStart(11, "0");
	return cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
}

function formatarTelefone(telefone) {
	if (!telefone) return "";
	telefone = telefone.toString();
	if (telefone.length === 9) {
		return telefone.replace(/(\d{5})(\d{4})/, "$1-$2"); // 91234-5678
	} else if (telefone.length === 8) {
		return telefone.replace(/(\d{4})(\d{4})/, "$1-$2"); // 1234-5678
	}
	return telefone;
}

function limparFormularioEnvolvidoComBoletim() {
	document.getElementById("formEnvolvidoComBoletim").reset();
}

function limparCamposIndividuoComBoletim() {
	// Limpa os campos do formulário
	document.getElementById("formEnvolvidoComBoletim").reset();

	// Remove classes de erro (Bootstrap)
	document.getElementById("tipoEnvolvimentoComBoletim").classList.remove("is-invalid");
	document.getElementById("cpfComBoletim").classList.remove("is-invalid");
	document.getElementById("nomeComBoletim").classList.remove("is-invalid");

	// Limpa a lista de envolvidos
	window.envolvidosComBoletim = [];

	// Atualiza a tabela (remove todas as linhas)
	atualizarTabelaEnvolvidosComBoletim();
}

function getIndividuosComBoletim()
{
	return window.envolvidosComBoletim;
}

function validarCPF(cpf) {
	cpf = cpf.replace(/[^\d]+/g, '');
	if (cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false;
	let soma = 0, resto;

	for (let i = 1; i <= 9; i++) soma += parseInt(cpf.charAt(i - 1)) * (11 - i);
	resto = (soma * 10) % 11;
	if (resto === 10 || resto === 11) resto = 0;
	if (resto !== parseInt(cpf.charAt(9))) return false;

	soma = 0;
	for (let i = 1; i <= 10; i++) soma += parseInt(cpf.charAt(i - 1)) * (12 - i);
	resto = (soma * 10) % 11;
	if (resto === 10 || resto === 11) resto = 0;

	return resto === parseInt(cpf.charAt(10));
}

function preencherTabEnvolvidosComBoletim(xmlString) {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlString, "application/xml");

    window.envolvidosComBoletim = [];

    const individuos = xmlDoc.getElementsByTagName("individuo");

    for (let i = 0; i < individuos.length; i++) {
        const ind = individuos[i];

        const id = ind.getElementsByTagName("id")[0]?.textContent || null;
        const cpf = ind.getElementsByTagName("cpf")[0]?.textContent || "";
        const nome = ind.getElementsByTagName("nome")[0]?.textContent || "";
        const idTipoEnvolvimento = ind.getElementsByTagName("idTipoEnvolvimento")[0]?.textContent || "";
        const detalheEnvolvimento = ind.getElementsByTagName("detalheEnvolvimento")[0]?.textContent || "";
        const ddd = ind.getElementsByTagName("ddd")[0]?.textContent || "";
        const telefone = ind.getElementsByTagName("telefone")[0]?.textContent || "";
        const email = ind.getElementsByTagName("email")[0]?.textContent || "";

        const tipoEnvolvimentoEl = ind.getElementsByTagName("tipoEnvolvimento")[0];
        const descricaoTipoEnvolvimento = tipoEnvolvimentoEl?.getElementsByTagName("descricao")[0]?.textContent || "";

        window.envolvidosComBoletim.push({
            id,
            idTipoEnvolvimento,
            descricaoTipoEnvolvimento,
            cpf,
            nome,
            ddd,
            telefone,
            email,
            detalheEnvolvimento,
            status: "existente"
        });
    }

    atualizarTabelaEnvolvidosComBoletim();
}

