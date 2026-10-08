function preencherTabelaPassagensComBoletim(xmlString) {
  const tbody = $("#tabelaPassagensBodyComBoletim");
  tbody.empty();

  // Parse do XML
  const parser = new DOMParser();
  const xml = parser.parseFromString(xmlString, "text/xml");

  // Buscar todas as passagens
  const passagensXML = Array.from(xml.getElementsByTagName("passagemVeiculo"));

  // Criar array de objetos para a tabela
  const passagens = passagensXML.map(p => {
    const idVeiculoTempoRealNode = p.getElementsByTagName("idVeiculoTempoReal")[0];
    const idVeiculoTempoReal = idVeiculoTempoRealNode ? idVeiculoTempoRealNode.textContent : "";
    const placa = p.getElementsByTagName("placa")[0].textContent;
    const dataPassagem = p.getElementsByTagName("data")[0].textContent;
    const dataVinculo = p.getElementsByTagName("data_vinculo")[0].textContent;

    return { idVeiculoTempoReal, placa, dataPassagem, dataVinculo };
  });

  // Preencher a tabela
  passagens.forEach(p => {
    const linha = `
      <tr>
        <td>${p.placa || ""}</td>
        <td>${formatarData(p.dataVinculo)}</td>
        <td>${formatarData(p.dataPassagem)}</td>        
        <td class="text-center">
          <button title='Abrir detalhes do veículo' class="btn btn-sm btn-primary" onclick="visualizarPassagemComBoletim('${p.idVeiculoTempoReal}')">
            <i class="fa fa-search" aria-hidden="true"></i>
          </button>
        </td>
      </tr>
    `;
    tbody.append(linha);
  });
  
  $('#txt_consulta_passagemComBoletim').quicksearch('#tabelaPassagensComBoletim tr');
}

function formatarData(data) {
  if (!data) return "";
  const d = new Date(data);
  return d.toLocaleString("pt-BR");
}

function visualizarPassagemComBoletim(idVeiculoTempoReal) {
    // Guardar o id do veículo no campo oculto
    $("#id_veic").text(idVeiculoTempoReal);

    // Inicializar e abrir o modal
    const modalEl = document.getElementById('modalDetalheVeiculo');
    const modal = new bootstrap.Modal(modalEl, {
        backdrop: 'static', // impede fechar clicando fora
        keyboard: false     // impede fechar com ESC
    });
    modal.show();

    // Chama a função para carregar detalhes do veículo
    AbrirDetalhesVeiculo(idVeiculoTempoReal);
}
