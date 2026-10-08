ID_ALERTA = null;

$(document).ready(function (){	
	buscarQuestionarios();	
	
	const modalIframe = new bootstrap.Modal(document.getElementById('modalIframe'),{
			backdrop: 'static',
			keyboard: false
		});
				
	document.getElementById('btnVerificarInfos').addEventListener('click', function() {
		abrirTelaAlerta();
	});	
});

function abrirModalQuestionario() {
	
	const myModal = new bootstrap.Modal(document.getElementById('modalQuestionarioAlerta'), {
        backdrop: 'static',
        keyboard: false
    });
    
    myModal.show();
}

function fecharModal(){
	
	const myModal = bootstrap.Modal.getInstance(document.getElementById('modalQuestionarioAlerta'));
	
	if(myModal){
		myModal.hide();
	}
}

function buscarQuestionarios() {
    var url = "/MuralhaDigital/AlertaQuestionario?acao=obterQuestionarios";

    $.ajax({
        type: "GET",
        url: url,
        dataType: "xml",

        success: (data) => {
            const $xml = $(data);
            const results = $xml.find("alertasQuestionarios");
            const divContainer = document.getElementById('containerQuestionario');

            // Converte os resultados em array para facilitar iteração por pares
            const questionarios = results.toArray();

            for (let i = 0; i < questionarios.length; i += 2) {
                const q1 = questionarios[i];
                const q2 = questionarios[i + 1];

                // Pergunta 1
                const id1 = $(q1).find("id").text();
                const pergunta1 = $(q1).find("pergunta").text();
                const obrigatorio1 = $(q1).find("obrigatorio").text();
                
               // Pergunta 1 HTML
				const htmlPergunta1 = `
				<div class="col-md-6 d-flex flex-column" data-id-questionario="${id1}">
				    <label class="form-label d-block"><strong>${pergunta1}</strong>
				    	<span style="color: red; font-size: 10px; margin-left: 6px" id="obrigatorio_${id1}" value="${obrigatorio1}">${obrigatorio1 == 0 ? '' : 'Obrigatório*'}</span> 
				    </label>
				    <div class="form-check form-check-inline mb-2">
				        <input class="form-check-input" type="radio" name="resposta_${id1}" id="sim_${id1}" value="sim">
				        <label class="form-check-label" for="sim_${id1}">Sim</label>
				    </div>
				    <div class="form-check form-check-inline">
				        <input class="form-check-input" type="radio" name="resposta_${id1}" id="nao_${id1}" value="nao">
				        <label class="form-check-label" for="nao_${id1}">Não</label>
				    </div>
				    <div class="mb-3 mt-auto" id="observacoes_${id1}">
				        <textarea class="form-control" id="observacao_${id1}" name="observacao_${id1}" rows="3" placeholder="Descreva o ocorrido..."></textarea>
				    </div>
				</div>`;

				// Pergunta 2 (pode não existir se for ímpar)
				let htmlPergunta2 = "";
				if (q2) {
				    const id2 = $(q2).find("id").text();
				    const pergunta2 = $(q2).find("pergunta").text();
				    const obrigatorio2 = $(q2).find("obrigatorio").text();
				
				    htmlPergunta2 = `
				    <div class="col-md-6 d-flex flex-column" data-id-questionario="${id2}">
				        <label class="form-label d-block"><strong>${pergunta2}</strong> 
				        	<span style="color: red; font-size: 10px; margin-left: 6px" id="obrigatorio_${id2}" value="${obrigatorio2}">${obrigatorio2 == 0 ? '' : 'Obrigatório*'}</span> 
				        </label>
				        <div class="form-check form-check-inline mb-2">
				            <input class="form-check-input" type="radio" name="resposta_${id2}" id="sim_${id2}" value="sim">
				            <label class="form-check-label" for="sim_${id2}">Sim</label>
				        </div>
				        <div class="form-check form-check-inline">
				            <input class="form-check-input" type="radio" name="resposta_${id2}" id="nao_${id2}" value="nao">
				            <label class="form-check-label" for="nao_${id2}">Não</label>				            
				        </div>
				        <div class="mb-3 mt-auto" id="observacoes_${id2}">
				            <textarea class="form-control" id="observacao_${id2}" name="observacao_${id2}" rows="3" placeholder="Descreva o ocorrido..."></textarea>
				        </div>
				    </div>`;
				}				
				
				// Linha final com Flexbox stretch
				const html = `
				<div class="row mb-3 align-items-stretch">
				    ${htmlPergunta1}
				    ${htmlPergunta2}
				</div>
				<hr>`;
				
				$(divContainer).append(html);

            }

            $("body").removeClass("loading");
        },
        error: () => {
            $("body").removeClass("loading");
            ErrorNotification("Erro ao processar requisição ao servidor!!", "");
        },
    });
}

function cadastrarRespostas(){
	container = document.getElementById('containerQuestionario');
	const campos = container.querySelectorAll('.col-md-6');
	
	let algumaIncorreta = false;
	const respostas = [];
	
	campos.forEach(campo => {
		
		const idQuestionario = campo.getAttribute('data-id-questionario');
		
		const spanObrigatorio = campo.querySelector('span');
    	const obrigatorioValue = spanObrigatorio.getAttribute('value');
		
		const radioButton = campo.querySelector('input[type="radio"]:checked');
		const radioValue = radioButton ? radioButton.value : null;
		
		const observacao = campo.querySelector('textarea');
    	const valueObservacao = observacao ? observacao.value.trim() : null;   	  	
   	
   		if(obrigatorioValue == 1){
			if ((radioValue === null || radioValue === '') && (valueObservacao === null || valueObservacao === '')) {
				algumaIncorreta = true;
			}	
		}
		
		if(radioValue !== null && radioValue !== '' || valueObservacao !== null &&  valueObservacao !== ''){
			respostas.push({
				resposta_simples: radioValue,
				resposta_livre_usuario: valueObservacao,
				id_questionario_alerta: idQuestionario,
				id_alerta: ID_ALERTA
			});			
		}
	})
	
	if (algumaIncorreta) {
	    alert("É necessário responder todas as perguntas obrigatórias");
	    return;
	}else{		
		var url = "/MuralhaDigital/AlertaQuestionario?acao=registrarRespostasUsuario";
		
	    $.ajax({
	        type: "POST",
	        url: url,
	        dataType: "xml",
	        data: JSON.stringify(respostas),
	        contentType: "application/json; charset=utf-8",
	        success: (data) => {
				alert("Respostas registradas com sucesso!")
				window.location.href = `/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=${ID_ALERTA}`
	        },
	        error: () => {
	            ErrorNotification("Erro ao processar requisição ao servidor!!", "");
	        },
	    });	
	}
}

function registrarAcessoUsuario(){
	
	var url = `/MuralhaDigital/AlertaQuestionario?acao=registrarAcessoUsuario&idAlerta=${ID_ALERTA}`

    $.ajax({
        type: "POST",
        url: url,
        dataType: "xml",
        success: (data) => {
			console.log("Registro realizado com sucesso")
        },
        error: () => {
            ErrorNotification("Erro ao processar requisição ao servidor!!", "");
        },
    });	
}

function verificarQuestionarioObrigatorio(idAlerta, callback) {
		
	ID_ALERTA = null;
	ID_ALERTA = idAlerta;
	
    var url = "/MuralhaDigital/AlertaQuestionario?acao=verificarQuestionarioObrigatorio";
    
    $.ajax({
        type: "GET",
        url: url,
        data: { idAlerta: idAlerta },
        dataType: "xml",
        success: (data) => {
            const resultText = $(data).find('result').text();
            const resultBoolean = resultText === 'true';
            
            if (resultBoolean) {
                alert("Questionário obrigatório");
                registrarAcessoUsuario(idAlerta);
                abrirModalQuestionario();
            }

            // devolve o resultado para quem chamou
            callback(resultBoolean);
        },
        error: () => {
            ErrorNotification("Erro ao processar requisição ao servidor!!", "");
            callback(false);
        },
    });
}

function abrirTelaAlerta() {
    const modalElement = document.getElementById('modalQuestionarioAlerta');
    const modal = bootstrap.Modal.getInstance(modalElement);
    
    if (modal) {
        modal.hide();
    }

    const iFrameElement = document.getElementById('modalIframe');
    let iFrameModal = bootstrap.Modal.getInstance(iFrameElement);
    
    if (!iFrameModal) {
        iFrameModal = new bootstrap.Modal(iFrameElement);
    }

    const iFrameSrc = document.getElementById('iframeAnalise');
    iFrameSrc.src = `/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=${ID_ALERTA}`;

    iFrameSrc.onload = () => {
        try {
            const doc = iFrameSrc.contentDocument || iFrameSrc.contentWindow.document;

            // IDs dos botões que devem ser desabilitados
            const ids = [
                "btnGerarOcorrencia",
                "btnAbrirModalFinalizarOcorrencia",
                "btnAbrirModalDescartar",
                "btnAbrirModalAcaoProcedimento",
                "btnAbrirModalAnotacaoContrib",
                "btnAbrirModalVeiculoMonitorado",
                "btnAbrirLinhaTempo",
                "btnAbrirMapaPassagens"
            ];

            ids.forEach(id => {
                const btn = doc.getElementById(id);
                if (btn) {
                    btn.disabled = true;              // desabilita o botão
                    btn.style.pointerEvents = "none"; // evita clique forçado
                    btn.classList.add("disabled");    // aplica classe visual (opcional, se usa Bootstrap)
                }
            });
            
            const nav = doc.querySelector("nav.navbar"); // ou outra classe seletora
			if (nav) {
			    nav.querySelectorAll("a, button").forEach(el => {
			        el.setAttribute("disabled", true);
			        el.style.pointerEvents = "none";
			        el.classList.add("disabled");
			    });
			}
            
        } catch (e) {
            console.warn("Não foi possível acessar o conteúdo do iframe:", e);
        }
    };

    iFrameModal.show();
}


function fecharTelaAlerta(){
	
	const iFrameElement = document.getElementById('modalIframe');
	const iFrame = bootstrap.Modal.getInstance(iFrameElement);
	
	if(iFrame){
		iFrame.hide();
	}	
	
	const modalElement = document.getElementById('modalQuestionarioAlerta');
	const modal = bootstrap.Modal.getInstance(modalElement);
	
	if(modal){
		modal.show();
	}
}

function simularAlerta() {
    const xmlFake = `
        <alertas>
            <listaAlertas>
                <alerta>
                    <id>837A6C5E-FCD9-460E-AF69-0375FAFFF846</id>
                    <idTipoAlerta>0349f722-dfde-4080-9e3b-d65f1c058edc</idTipoAlerta>
                    <tipoAlerta>Veículo Furtado</tipoAlerta>
                    <idVeiculoMonitorado>14071eb2-89fd-44a2-9c35-a32e9b3cbe40</idVeiculoMonitorado>
                    <placaCadastro>ZZZ0000</placaCadastro>
                    <dataCadVeicMonitorado>2025-09-10T10:00:04.270-03:00</dataCadVeicMonitorado>
                    <idStatusAlerta>5479c6d9-7381-4492-99be-442ef2e741b0</idStatusAlerta>
                    <statusAlertaDesc>PENDENTE</statusAlertaDesc>
                    <dataAlerta>2025-09-10T12:22:33.257-03:00</dataAlerta>
                    <enviadoAoCliente>0</enviadoAoCliente>
                    <equipamento>1000100 - Teste Havan</equipamento>
                    <latitude>0.0</latitude>
                    <longitude>0.0</longitude>
                    <descartado>false</descartado>
                    <ocorrenciaGerada>false</ocorrenciaGerada>
                    <ocorrenciaComNotificacao>false</ocorrenciaComNotificacao>
                    <lembrete>0</lembrete>
                    <ocorrenciaFinalizada>false</ocorrenciaFinalizada>
                    <alertaVinculado>false</alertaVinculado>
                    <idLocal>0</idLocal>
                    <serieEquipamento>0</serieEquipamento>
                    <idPista>0</idPista>
                    <faixa>0</faixa>
                    <velocidade>0</velocidade>
                    <totalRegistros>0</totalRegistros>
                    <atendido>0</atendido>
                    <permiteAtendimento>false</permiteAtendimento>
                    <permiteAlterarAtendimento>false</permiteAlterarAtendimento>
                    <supervisionado>0</supervisionado>
                    <assinado>false</assinado>
                    <dataCadVeicMonitoradoFormatada></dataCadVeicMonitoradoFormatada>
                    <horaCadVeicMonitoradoFormatada></horaCadVeicMonitoradoFormatada>
                    <dataAlertaFormatada></dataAlertaFormatada>
                    <horaAlertaFormatada></horaAlertaFormatada>
                    <dataVeiculoFormatada></dataVeiculoFormatada>
                    <horaVeiculoFormatada></horaVeiculoFormatada>
                    <com_semelhanca>0</com_semelhanca>
                    <com_semelhanca_erros>0</com_semelhanca_erros>
                    <som>/muralha-digital/assets/sounds/_alerta_simples.mp3</som>
                </alerta>
            </listaAlertas>
        </alertas>
    `;

    if (window.socketAlertas && socketAlertas.onmessage) {
        socketAlertas.onmessage({ data: xmlFake });
    }

    document.dispatchEvent(new CustomEvent("novoAlerta", { detail: xmlFake }));
}