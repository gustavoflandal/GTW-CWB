function cadastroRegistroDeFatoComBoletim() {
	abrirModalConfirmacao();
}

document.addEventListener('registroDeFatoSalvoCadastro', function(event) {
	const id = event.detail.id;

	// Só executa se a tela tiver definido essa função
	if (typeof window.onRegistroDeFatoSalvo === 'function') {
		window.onRegistroDeFatoSalvo(id);
	}
});