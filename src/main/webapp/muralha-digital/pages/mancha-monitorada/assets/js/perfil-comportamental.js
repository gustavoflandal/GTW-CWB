let rota = [];
let rotaPath = [];
let center = {lat: -25.432947, lng: -49.270591 };

$(document).ready(function () 
{
	CarregarComponenteData(true);
	obterEquipamentosGenerico();
});

function obterPassagensVeiculo() {
	
	const placa = document.getElementById("placaConsulta").value.toUpperCase();
    var dataInicio = document.getElementById("dataInicioInput").value;
    var dataFim = document.getElementById("dataFimInput").value;   
    const selEquipamento = document.getElementById("selEquipamento").value;   
    
    const dataInicioFormatada = moment(dataInicio, "DD/MM/YYYY").format("YYYY-MM-DD") + ' 00:00:00';
	const dataFimFormatada = moment(dataFim, "DD/MM/YYYY").format("YYYY-MM-DD") + ' 23:59:59';
    
    if(!placa || !dataInicio || !dataFim){
		WarningCsx('Pesquisa muito abrangente. É obrigatório preencher campos de placa e datas.');
	}
    
    const url = `/MuralhaDigital/PerfilComportamental?acao=obterPassagensPorPlaca&placa=${placa}&dataInicio=${dataInicioFormatada}&dataFim=${dataFimFormatada}&idLocal=${selEquipamento}`;    

    fetch(url)
    .then(response => response.text())
    .then(xmlString => {
        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(xmlString, "application/xml");

        const parseError = xmlDoc.querySelector("parsererror");
        if (parseError) {
            console.error("Erro ao interpretar XML:", parseError.textContent);
            alert("Erro ao interpretar a resposta do servidor.");
            console.log("XML bruto:", xmlString);
            return;
        }

        const lista = xmlDoc.getElementsByTagName("listaPassagensVeiculos")[0];
        const passagens = lista.getElementsByTagName("PassagensVeiculo");

        const tbody = document.querySelector("#tabela tbody");
        tbody.innerHTML = ""; // Limpa o conteúdo anterior
        
        console.log("Lista: "+lista)
        console.log("Passagens: "+passagens)

        Array.from(passagens).forEach(passagem => {
            const placa = passagem.getElementsByTagName("placa")[0]?.textContent.trim() || "";
            const quantidade = passagem.getElementsByTagName("quantidadePassagens")[0]?.textContent.trim() || "";
            const intervalo = passagem.getElementsByTagName("intervaloHoras")[0]?.textContent.trim() || "";
            const periodo = passagem.getElementsByTagName("periodoPassagens")[0]?.textContent.trim() || "";
            const local = passagem.getElementsByTagName("nomeLocal")[0]?.textContent.trim() || "";

            const row = document.createElement("tr");
            row.innerHTML = `
                <td><small>${placa}</small></td>
                <td><small>${local}</small></td>
                <td><small>${quantidade}</small></td>
                <td><small>${intervalo}h</small></td>
                <td><small>${periodo}</small></td>
            `;
            tbody.appendChild(row);
        });
    })
    .catch(error => {
        console.error("Erro ao obter passagens:", error);
        alert("Ocorreu um erro ao consultar as passagens.");
    });
}

function limparFiltros(){
	document.getElementById("placaConsulta").value = "";		
	    
	$('#selEquipamento').val('0').selectpicker('refresh');
	
  	$("#dataInicioInput").val('');
    $("#dataFimInput").val('');
}

function toggleMapVisibility() {
    $('#mapContainer').toggleClass('d-none');
}

