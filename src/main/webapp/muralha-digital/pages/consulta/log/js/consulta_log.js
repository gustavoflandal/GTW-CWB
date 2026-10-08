$( document ).ready(function() 
{
	
	obterEquipamentosGenerico("selEquipamento");
   
});

function gerarExeclLog() {
    // Valida os campos antes de prosseguir
    if (!validaCampos()) {
        return;
    }

    // Coleta a data e o equipamento selecionado
    const dataInput = document.getElementById('data_ini');
    const dataValor = dataInput.value; // formato yyyy-MM-dd
    //const equipSelecionado = $('#selEquipamento').val();
    const select = document.getElementById('selEquipamento');
	const equipSelecionado = select.value;                         // valor (ex: ID do equipamento)
	const equipDescricao = select.options[select.selectedIndex].text; // texto visível (descrição)
	const equipCodigo = equipDescricao.split('-')[0].trim();
	console.log(equipCodigo)

    console.log('Equipamento selecionado:', equipSelecionado);
    console.log('Data selecionada:', dataValor);

    if (!dataValor || !equipSelecionado) {
        alert("Preencha todos os campos antes de continuar.");
        return;
    }

    // Monta a URL para chamar a servlet
    const urlServlet = '/MuralhaDigital/Ftp';
    var dataString = "dataIniValor=" + encodeURIComponent(dataValor) +
                 	 "&equip_sel=" + encodeURIComponent(equipSelecionado) +
                 	 "&equipCodigo=" + encodeURIComponent(equipCodigo);

    showLoadingOverlay();

    // Faz a requisição para a servlet (esperando arquivo como resposta)
    $.ajax({
        type: "GET",
        url: urlServlet,
        data: dataString,
        xhrFields: {
            responseType: 'blob' // Recebe o arquivo binário (ZIP, CSV, etc.)
        },
        success: function (data, textStatus, jqXHR) {
            hideLoadingOverlay();

            // Cria o blob e baixa o arquivo automaticamente
            const blob = new Blob([data], { type: jqXHR.getResponseHeader("Content-Type") || 'application/octet-stream' });
            const link = document.createElement('a');
            link.href = window.URL.createObjectURL(blob);
            link.download = `Logs_${equipSelecionado}_${dataValor}.zip`; // nome do arquivo baixado
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);

            alert("Download concluído com sucesso!");
        },
        error: function (jqXHR, textStatus, errorThrown) {
            hideLoadingOverlay();
            console.error("Erro no download:", errorThrown);
            alert('Nenhum arquivo encontrado, verifique seus filtros ou entre em contato com Suporte.');
        }
    });
}

	

function validaCampos() {

    var dataIniInput = document.getElementById('data_ini').value;
    var equip_sel = document.getElementById('selEquipamento').value;
    
    if (!dataIniInput) {
        alert("Data inicial obrigatória!!!");
        return false;
    }
    if (!equip_sel) {
        alert("Equipamento é obrigatorio!!!");
        return false;
    }
    
   

    return true;
}

function showLoadingOverlay() {
    $('#loading-overlay').show();
}

function hideLoadingOverlay() {
    $('#loading-overlay').hide();
}

function limpar_campos()
{	
	document.getElementById("data_ini").value = "";	
	document.getElementById("selEquipamento").value = "";
		$('#selEquipamento').selectpicker("refresh");
} 
