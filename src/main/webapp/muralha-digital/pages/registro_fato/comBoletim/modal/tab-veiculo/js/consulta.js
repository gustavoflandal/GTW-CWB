/**
 * Evento principal que é executado quando o DOM da página está totalmente carregado.
 */
document.addEventListener('DOMContentLoaded', function () {
	const checkbox = document.getElementById('habilitarMonitoramentoComBoletim');
	const secaoMonitoramento = document.getElementById('secaoMonitoramentoComBoletim');

	if (!checkbox || !secaoMonitoramento) {
        console.error("Elementos essenciais (checkbox ou seção de monitoramento) não encontrados no DOM.");
        return;
    }

	// 1. Define o estado VISUAL inicial: a seção começa oculta.
	secaoMonitoramento.style.display = 'none';
	
    // 2. Define a função que irá finalizar a configuração (desabilitar os campos).
    const finalizarConfiguracaoInicial = () => {
        secaoMonitoramento.querySelectorAll('input, select, textarea').forEach(campo => {
			campo.disabled = true;
		});
    };

    // 3. Chama a função para carregar os dados e passa a função acima como callback.
    // Agora, os campos só serão desabilitados DEPOIS que o select for preenchido.
    ObterTiposAlertasOcorrenciasComBoletim(finalizarConfiguracaoInicial);

	// 4. Adiciona o evento 'change' ao checkbox (esta parte não muda).
	checkbox.addEventListener('change', function () {
		const habilitado = this.checked;
		
        // Mostra ou oculta a seção
		secaoMonitoramento.style.display = habilitado ? 'block' : 'none';
        
        // Habilita ou desabilita todos os campos dentro da seção
		secaoMonitoramento.querySelectorAll('input, select, textarea').forEach(campo => {
			campo.disabled = !habilitado;
		});
	});
});

function ObterTiposAlertasOcorrenciasComBoletim(onComplete) {
  const urlPesquisa = "/MuralhaDigital/AlertaOcorrencia/Tipo";
  const select = $('#tipoAlertaComBoletim');

  $.ajax({
    type: "GET",
    url: urlPesquisa,
    dataType: "xml",
    success: function (data, textStatus, jqXHR) {
      var event = jqXHR.responseText;
      var xmlDoc = $.parseXML(event);
      var $xml = $(xmlDoc);

      var sucesso = $xml.find('sucesso').text();
      var msgResposta = $xml.find('msgResposta').text();

      sucesso = (sucesso === 'true' || sucesso === '');

      select.empty(); // Limpa opções existentes

      if (sucesso) {
        select.append('<option value="">Selecione...</option>');
		$xml.find('TipoAlertaOcorrencia').each(function () {
		  var id = $(this).find('id').text();
		  var tipo = $(this).find('tipo').text();
		  select.append(`<option value="${id}">${tipo}</option>`);
		});
      } else {
        select.append('<option value="">Nenhum tipo encontrado</option>');
        Swal.fire({
          icon: 'warning',
          title: 'Atenção',
          text: msgResposta,
          confirmButtonColor: '#d33'
        });
      }
    },
    error: function () {
      select.empty();
      select.html('<option value="">Erro ao carregar</option>');
      Swal.fire({
        icon: 'error',
        title: 'Erro',
        text: 'Erro ao processar requisição ao servidor!',
        confirmButtonColor: '#d33'
      });
    },
    complete: function () {
      // Executa o callback DEPOIS que a requisição terminar, se ele foi fornecido.
      if (onComplete && typeof onComplete === 'function') {
        onComplete();
      }
    }
  });
}
