function carregarSituacoesComBoletim() {
  const select = $("#idSituacaoComBoletim");
  if (!select.length) return; // evita erro se select não existe

  select.html('<option value="">Selecione</option>'); // limpa

  $.ajax({
    type: "GET",
    url: "/MuralhaDigital/RegistroDeFato/Boletim/Situacao",
    dataType: "xml",
    success: function(data, textStatus, jqXHR) {
      try {
        const xmlDoc = new DOMParser().parseFromString(jqXHR.responseText, "application/xml");
        const situacoes = xmlDoc.getElementsByTagName("Situacao");

        if (situacoes.length === 0) {
          ErrorNotification("Nenhuma situação encontrada.", "");
          return;
        }

        for (let i = 0; i < situacoes.length; i++) {
          const situacao = situacoes[i];
          const id = situacao.getElementsByTagName("id")[0].textContent;
          const descricao = situacao.getElementsByTagName("descricao")[0].textContent;
          select.append(`<option value="${id}">${descricao}</option>`);
        }
      } catch (e) {
        ErrorNotification("Erro ao processar dados recebidos.", "");
      }
    },
    error: function() {
      ErrorNotification("Erro ao processar requisição ao servidor!", "");
    }
  });
}

// Chamar só quando o modal ou aba for aberto (evitar múltiplas chamadas desnecessárias)
$(function() {
  carregarSituacoesComBoletim();
});