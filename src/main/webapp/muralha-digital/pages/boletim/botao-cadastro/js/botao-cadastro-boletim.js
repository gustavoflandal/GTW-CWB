function abrirCadastroBoletim(origem = null, idAtendimento = null) {
    abrirModalCadastroBoletim();

    if (origem) {
        $('#modalBoletim').data('origem', origem);
        document.getElementById("ocultar").style.display = "none";
        document.getElementById("ocultar1").style.display = "none";
        document.getElementById("ocultar2").style.display = "none";
    } else {
        $('#modalBoletim').removeData('origem');
    }
    
    if (idAtendimento) {
        $('#modalBoletim').data('idAtendimento', idAtendimento);
    } else {
        $('#modalBoletim').removeData('idAtendimento');
    }
}

document.addEventListener('boletimSalvoCadastro', function (event) {
  const id = event.detail.id;

  // Só executa se a tela tiver definido essa função
  if (typeof window.onBoletimSalvo === 'function') {
    window.onBoletimSalvo(id);
  }
});