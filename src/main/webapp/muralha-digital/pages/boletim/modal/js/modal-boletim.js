modo = "";
function abrirModalCadastroBoletim() {
	modo = "cadastro";
	// tab-boletim.js
	configuraModalCadastrar();
	limpaCamposModalBoletim();
	exibirModalBoletim();
}

async function abrirModalVisualizarBoletim(idStringInt) {
	let id = typeof idStringInt === "string" ? parseInt(idStringInt, 10) : idStringInt;

	if (isNaN(id) || id <= 0) {
		ErrorNotificationBoletimModal("⚠️ ID inválido. O valor precisa ser um número inteiro positivo.", "");
		return;
	}

	modo = "detalhes";
	limpaCamposModalBoletim();

	try {
		// boletim/modal/tab-boletim/consulta.js
		await buscarInformacaoBoletimId(id);
		exibirModalBoletim();
	} catch (error) {
		ErrorNotificationBoletimModal("Modal não será exibido devido a erro na busca do boletim.", "");
	}
}

function exibirModalBoletim() {
	const modal = new bootstrap.Modal(document.getElementById('modalBoletim'));
	modal.show();
}

function fecharModal() {
	const modalElement = document.getElementById("modalBoletim");
	const modalInstance = bootstrap.Modal.getInstance(modalElement);
	if (modalInstance) {
		modalInstance.hide();
	}
}

function limpaCamposModalBoletim() {
	atualizarTituloModal();
	// Limpa Tab Boletim
	limparCamposEListaBoletim();
	// Limpa Veiculos 
	limparCamposEListaVeiculos();
	// Limpa Tab Individuo
	limparCamposEListaIndividuos();
	//Limpa Tab Apreensoes
	limparCamposEListaApreensoes();
	//Limpa Tab Documentos
	limparCamposEListaDocumentos();
}

function atualizarTituloModal() {
	const tituloModal = document.getElementById('modalPadraoLabel');
	if (modo === 'cadastro') {
		tituloModal.textContent = 'Cadastro de Boletim';
	} else if (modo === 'detalhes') {
		tituloModal.textContent = 'Detalhes do Boletim';
	}
}


function SuccessNotificationBoletimModal(title, text) {
	//precisa <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
		title: title,
		icon: "success",
		text: text,
	});
}

function ErrorNotificationBoletimModal(title, text) {
	//precisa <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
		title: title,
		icon: "error",
		text: text,
	});
}