var VOLTAR_MODAL_LT = false;
var MODAL_ORIGEM_LT = '';

function ObterDataStringConsulta(idAlerta, idVeiculo)
{
	if (idAlerta != null)
		return "acao=obterLinhaTempo" + "&idAlerta=" + idAlerta;
	else if (idVeiculo != null)
		return "acao=obterLinhaTempo" + "&idVeiculo=" + idVeiculo;
	else
		return null;
}

function ExecutarPesquisaLinhaTempo(tituloModal, idAlerta, idVeiculo)
{
//	console.log("ExecutarPesquisaLinhaTempo");
	ExibirSpinnerModalLinhaTempo();
	
	LimparModalLinhaTempo();
	AtualizarTituloModalLinhaTempo(tituloModal);
		
	try
	{
		var dataStringPesquisa = ObterDataStringConsulta(idAlerta, idVeiculo);
//		console.log(dataStringPesquisa);
									
		var urlPesquisa = urlRoot + "MuralhaDigital/LinhaTempo";
	
	    $.ajax({
	        type: 		"GET",
	        url: 		urlPesquisa,
	        data: 		dataStringPesquisa,
	        dataType:	"xml",
	           
	        //if received a response from the server
	        success: 	function( data, textStatus, jqXHR) 
	        {
	        	var event = jqXHR.responseText;
	        	var xmlDoc = $.parseXML( event );
				var $xml = $(xmlDoc);
	    			
				var sucesso = $xml.find('sucesso').text();
				var msgResposta = $xml.find('msgResposta').text();
				
				sucesso = (sucesso === 'true' || sucesso === '');
				
				if (sucesso) {
					ProcessarDadosLinhaTempo( jqXHR.responseText );
				} else {
					WarningModalCsx_E_TimeOut_8000ms_v2(msgResposta, MSG_CONTAINER_MODAL);
				}
				
				OcultarSpinnerModalLinhaTempo();
	        },
	
	        error: 		function(jqXHR, textStatus, errorThrown)
	        {
	        	AlertModalCsx_E_TimeOut_8000ms_v2('Erro ao processar requisição ao servidor!!', MSG_CONTAINER_MODAL);
				OcultarSpinnerModalLinhaTempo();
	        },
	        beforeSend: function(jqXHR, settings){},
	        complete: 	function(jqXHR, textStatus){}
	 
	    });
    }
    catch(e)
    {
		console.log("Erro ao executar pesquisa de passagens para o mapa!!");
		OcultarSpinnerModalLinhaTempo();
	}
}

function ProcessarDadosLinhaTempo(evento)
{
//    console.log("ProcessarDadosLinhaTempo");

    const xmlDoc = $.parseXML(evento);
    const $xml = $(xmlDoc);
    const $itens = $xml.find('LinhaTempo');

    if ($itens.length === 0)
    {
        console.warn("Nenhum item de LinhaTempo encontrado.");
        return;
    }

    const itensHtml = $itens.map(function (index)
    {
        const $item = $(this);

        const passo = $item.find('passo').text().trim();
        const nomePasso = $item.find('nomePasso').text().trim();
        const dataFormatada = $item.find('dataFormatada').text().trim();
        const tempo = $item.find('tempo').text().trim();

//        console.log(`passo: ${passo}, nomePasso: ${nomePasso}, data: ${dataFormatada}, tempo: ${tempo}`);

        // Alterna entre 'left' e 'right', começando com 'left'
        const lado = index % 2 === 0 ? 'left' : 'right';

        return `
            <li class="timeline-item">
                <div class="timeline-icon">${escapeHtml(passo)}</div>
                <div class="timeline-content ${lado}">
                    <h5>${escapeHtml(nomePasso)}</h5>
                    <p>${escapeHtml(dataFormatada)}</p>
                    <p class="time-diff">${escapeHtml(tempo)}</p>
                </div>
            </li>
        `;
    }).get().join("");

    const htmlFinal = `<ul class="timeline">${itensHtml}</ul>`;

    const modalLinhaTempoBody = document.getElementById("modalLinhaTempoBody");
    if (modalLinhaTempoBody)
        modalLinhaTempoBody.innerHTML = htmlFinal;
}

function escapeHtml(str)
{
    return String(str).replace(/[&<>"']/g, function (match)
    {
        const map = {
            '&': '&amp;',
            '<': '&lt;',
            '>': '&gt;',
            '"': '&quot;',
            "'": '&#39;'
        };
        return map[match];
    });
}

function ExibirSpinnerModalLinhaTempo()
{
	var spinAtualizaLinhaTempo = document.getElementById('spinAtualizaLinhaTempo');
	if (spinAtualizaLinhaTempo)
		spinAtualizaLinhaTempo.style.visibility='visible';
}
function OcultarSpinnerModalLinhaTempo()
{
	var spinAtualizaLinhaTempo = document.getElementById('spinAtualizaLinhaTempo');
	if (spinAtualizaLinhaTempo)
		spinAtualizaLinhaTempo.style.visibility='hidden';	
}

function AtualizarTituloModalLinhaTempo(tituloModal)
{
	var linhaTempoLabel = document.getElementById("linhaTempoLabel");
	
	if (linhaTempoLabel)
		linhaTempoLabel.innerHTML = tituloModal;
}

function LimparModalLinhaTempo()
{
	var linhaTempoLabel = document.getElementById("linhaTempoLabel");
	var modalLinhaTempoBody = document.getElementById("modalLinhaTempoBody");
	
	if (linhaTempoLabel)
		linhaTempoLabel.innerHTML = "Linha do Tempo";
		
	if (modalLinhaTempoBody)
		modalLinhaTempoBody.innerHTML = "";
}


function LinhaTempoVeiculoExterno(nomeModalOrigem, tituloModalLinhaTempo, idVeiculo)
{
	VOLTAR_MODAL_LT = true;
	MODAL_ORIGEM_LT = nomeModalOrigem;
	OcultarModalOrigemLT();
	MostrarModalLinhaTempo();
	ExecutarPesquisaLinhaTempo(tituloModalLinhaTempo, null, idVeiculo);
}

function CarregarModalLinhaTempoVeiculoExterno(placa)
{
	var placaCad = document.getElementById("idPlacaCad");
	if (placaCad)
		placaCad.value = placa;
	
	ObterTiposAlertasOcorrencias();
	SetarDataInicio();
}

function OcultarModalOrigemLT()
{
	if (MODAL_ORIGEM_LT != '')
		$(MODAL_ORIGEM_LT).modal('hide');
}

function MostrarModalOrigemLT()
{
	if (MODAL_ORIGEM_LT != '')
		$(MODAL_ORIGEM_LT).modal('show');
}

function OcultarModalLinhaTempo()
{
	$("#linhaTempo").modal('hide');
}

function MostrarModalLinhaTempo()
{
	$("#linhaTempo").modal('show');
}

function fecharModalLinhaTempo()
{
	$('#linhaTempo').modal('hide');
	
	if (VOLTAR_MODAL_LT)
	{
		VOLTAR_MODAL_LT = false;
		LimparModalLinhaTempo();
		OcultarModalLinhaTempo();
		MostrarModalOrigemLT();
	}
}