// Função helper para detectar e tratar redirecionamentos de autenticação
function verificarRedirecionamento(jqXHR, textStatus) {
	// Detecta: status 0 (error/redirect) com resposta HTML, ou 302
	if ((jqXHR.status === 0 || jqXHR.status === 302) && jqXHR.responseText && jqXHR.responseText.indexOf('<') === 0) {
		if (typeof FlutterReauthChannel !== 'undefined') {
			FlutterReauthChannel.postMessage('reauth');
			return true;
		}
	}
	return false;
}

$().ready(function () 
{	
	obterAlertasPendentesAssinatura();
});

function chamaPaginaPrincipal() 
{
	var end =  "/muralha-digital/muralha_principal.jsp";

	window.location.href = end;
	return false;
}

function AbrirPagina(destino)
{
	var end =  destino;
	window.location.href = end;
	return false;
}

function obterAlertasPendentesAssinatura() {
    var urlPesquisa = urlRoot + "MuralhaDigital/Alerta";
    var dataStringPesquisa = "acao=obterAlertasPendentesAssinatura";

    const badge = document.getElementById("qtdeAlertasNaoAssinados");
    const link = document.getElementById("linkAlertas");

    $.ajax({
        type: "GET",
        url: urlPesquisa,
        data: dataStringPesquisa,
        dataType: "text",
        success: function(data) {
            var qtdeAlertas = parseInt(data);

            if (qtdeAlertas > 0) {
				if(badge)
				{
                	badge.style.display = "flex";
                	badge.textContent = qtdeAlertas > 99 ? "99+" : qtdeAlertas;
            	}

                // habilita clique
                if(link)
                {
	                link.removeAttribute("disabled");
	                link.style.pointerEvents = "auto";
	                link.style.opacity = "1";
               }
            } else {
				// badge.style.display = "none";
                if(badge)
                {
                 	badge.style.display = "flex";
                 	badge.textContent = 0;
                }

				// desabilita clique removendo href
                if(link)
					link.removeAttribute("href");

            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (!verificarRedirecionamento(jqXHR, textStatus)) {
                console.error("Erro ao processar requisição ao servidor!");
            }
        }
    });
}


/*

function abrirVeiculosTempoReal() 
{	
	var end =  "/muralha-digital/pages/tempo-real/veiculos-tempo-real.jsp";

	window.location.href = end;
	return false;
}	

function abrirMapaCalor() 
{	
	var end =  "/muralha-digital/pages/mapa-calor/mapa-calor.jsp";

	window.location.href = end;
	return false;
}

function abrirMapaDispositivos() 
{	
	var end =  "/muralha-digital/pages/mapa-equipamento/mapa-dispositivos.jsp";

	window.location.href = end;
	return false;
}

function abrirConsultaCadMonitorado()
{	
	var end =  "/muralha-digital/pages/monitorado/consulta.jsp";

	window.location.href = end;
	return false;
}		


function abrirConsultaAlertasOcorrencias()
{	
	var end =  "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp";

	window.location.href = end;
	return false;
}		


function abrirCorrelacao() 
{	
	var end =  "/muralha-digital/pages/correlacao/correlacao.jsp";

	window.location.href = end;
	return false;
}	

function abrirParametros() 
{	
	var end =  "/muralha-digital/pages/configuracoes/parametros.jsp";

	window.location.href = end;
	return false;
}	
*/

	
	
	
	
