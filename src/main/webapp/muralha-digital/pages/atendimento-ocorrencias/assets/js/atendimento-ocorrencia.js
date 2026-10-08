let ocorrenciasCache = [];
let historicoCache = [];
let documentoCache = [];
$(document).ready(function() 
{
	//carregarOcorrenciasNaoAtendidas();
	obterGuarnicoes();	
	obterOcorrencias();
	setInterval(obterOcorrencias, 5000);   
		
	
});	

//Busca novas ocorrencias/atendimentos
function obterOcorrencias() {    
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Atendimento",
        dataType: "xml",
        data: "acao=obterOcorrencias",
        success: function (data) {
            const $xml = $(data);        
            const lista = $xml.find("listaAtendimentos").find("atendimento");
            const ocorrencias = [];

            lista.each(function () {
                const $alerta = $(this);
				
                const id 					= $alerta.find("id_ocorrencia").text();
                const origem 				= $alerta.find("origem").text();
                const tipo 					= $alerta.find("tipoAlerta").text();
                const dataHora 				= $alerta.find("dataAlertaFormatada").text();
                const placa 				= $alerta.find("placaVeiculo").text();
                const descricao 			= $alerta.find("descricao").text();
                const origem_registro 		= $alerta.find("origemRegistro").text();
                const prioridade 			= $alerta.find("prioridade").text();
                const status 				= $alerta.find("statusOcorrencia").text();
                const dataCriacao 			= $alerta.find("dataAtendimentoFormatada").text();
                const dataEncerramento 		= $alerta.find("dataEncerramentoFormatada").text();
                const idSituacaoEnvio 		= $alerta.find("idSituacaoEnvio").text();
                const idAtendimento 		= $alerta.find("idAtendimento").text();
                const idGuarnicao 			= $alerta.find("idGuarnicao").text();
                const protocolo 			= $alerta.find("protocolo").text();
                const idAlerta 				= $alerta.find("idAlerta").text();
                const idRegistroFato		= $alerta.find("idRegistroFato").text();             
                const enderecoAlerta		= $alerta.find("endereco_alerta").text();
                const enderecoLocalEvento	= $alerta.find("endereco_local_evento").text();
                const idLocal 				= $alerta.find("id_local").text();
                const temBoletim		= $alerta.find("temBoletim").text();
                
                ocorrencias.push({
                    id: 				 id,  
                    origem: 			 origem,                  
                    placaVeiculo: 		 placa || "Sem placa",
                    data: 				 dataHora,
                    tipo: 				 tipo, 
                    descricao: 			 descricao, 
                    origem_registro: 	 origem_registro,
                    prioridade: 		 prioridade,
                    status: 			 status,
                    dataCriacao: 		 dataCriacao,
                    dataEncerramento: 	 dataEncerramento,
                    idSituacaoEnvio: 	 idSituacaoEnvio,
                    idAtendimento: 		 idAtendimento,
                    protocolo: 			 protocolo,
                    idGuarnicao: 		 idGuarnicao,
                    idAlerta: 			 idAlerta,
                    idRegistroFato:		 idRegistroFato,
                    enderecoAlerta: 	 enderecoAlerta,
                    enderecoLocalEvento: enderecoLocalEvento,
                    idLocal: 			 idLocal,
                    temBoletim:			temBoletim,
                                     
                });
            });
			ocorrenciasCache = ocorrencias;           
            PopulaOcorrencias(ocorrencias);
        },

        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao obter alertas:", errorThrown);
        }
    });
}

function obterGuarnicoes() {    
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Atendimento",
        dataType: "xml",
        data: "acao=obterGuarnicoes",
        success: function (data){
            var $xml = $(data);
            var $guarnicoes = $xml.find('guarnicao');

            $('#selGuarnicoes').empty(); // Limpa o select

            if ($guarnicoes.length === 0) {
                // Nenhuma guarnição encontrada
                $('#selGuarnicoes').append($('<option>', {
                    value: '',
                    text: 'Nenhuma guarnição disponível no momento',
                    disabled: true
                }));
            } else {
                // Adiciona opção padrão
                $('#selGuarnicoes').append($('<option>', {
                    value: '',
                    text: 'Selecione uma guarnição'
                }));

                // Preenche as opções com os dados retornados
                $guarnicoes.each(function () {
                    var $item = $(this);
                    var id = $item.find('id').text();
                    var nome = $item.find('nome').text();

                    $('#selGuarnicoes').append($('<option>', {
                        value: id,
                        text: nome
                    }));
                });
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao obter Guarnições:", errorThrown);
        }
    });
}

function PopulaOcorrencias(ocorrencias) {
	console.log('Minhas ocorrencias ', ocorrencias)
    const tabelaNaoAtendidas = document.querySelector("#tabela-nao-atendidas tbody");
    const tabelaAtendidas = document.querySelector("#tabela-atendidas tbody");

    tabelaNaoAtendidas.innerHTML = '';
    tabelaAtendidas.innerHTML = '';

    ocorrencias.forEach(ocorrencia => {
        const linha = document.createElement('tr');
        const tdTipo = document.createElement('td');
        tdTipo.textContent = ocorrencia.tipo;

        const tdOrigem = document.createElement('td');
        if (ocorrencia.origem_registro === '1') {
		  tdOrigem.textContent = "ALERTA";
		} else if (ocorrencia.origem_registro === '2') {
		  tdOrigem.textContent = "FATO COM BOLETIM";
		}
		else if (ocorrencia.origem_registro === '3') {
		  tdOrigem.textContent = "FATO SEM BOLETIM";
		} else {
		  tdOrigem.textContent = "-";
		}
        
        const tdPrioridade = document.createElement('td');  
        const prioridadeMap = {
		  "3": "Baixa",
		  "2": "Média",
		  "1": "Alta"
		};
		
		// Converte prioridade numérica em texto descritivo
		let prioridadeTexto = prioridadeMap[ocorrencia.prioridade] || "-";
             
		prioridadeTexto = prioridadeTexto.charAt(0).toUpperCase() + prioridadeTexto.slice(1).toUpperCase();

				
		const span = document.createElement('span');
			span.classList.add('badge', 'rounded-pill', 'fw-bold');
			
			// Define a cor com base na prioridade
			switch (ocorrencia.prioridade) {
			    case "1":
			        span.classList.add('bg-danger');
			        break;
			    case "2":
			        span.classList.add('bg-warning', 'text-dark');
			        break;
			    case "3":
			        span.classList.add('bg-success');
			        break;
			    default:
			        span.classList.add('bg-secondary');
			        break;
			}
			
			// Define o texto do badge
			span.textContent = prioridadeTexto;
			
			// Adiciona o span dentro da td
			tdPrioridade.appendChild(span);
        const tdObjeto = document.createElement('td');
        tdObjeto.textContent = ocorrencia.placaVeiculo;

        const tdDataOcorrencia = document.createElement('td');
        tdDataOcorrencia.textContent = ocorrencia.data;

        const tdDatainicioAtendimento = document.createElement('td');
        tdDatainicioAtendimento.textContent = ocorrencia.dataCriacao || "-";
        
        const tdtempoDecorrido = document.createElement('td');
       if (ocorrencia.dataCriacao) {
		    const tempoDecorrido = calcularTempoEntre(ocorrencia.dataCriacao, ocorrencia.dataEncerramento); // ou `dataFim`, se for esse o nome real
		    tdtempoDecorrido.textContent = tempoDecorrido;
		} else {
		    tdtempoDecorrido.textContent = "-";
		}
        
        const tdStatus = document.createElement('td');
         if (ocorrencia.dataCriacao) {
		    const inicio = parseDataBR(ocorrencia.dataCriacao);
		    const fim = ocorrencia.dataEncerramento ? parseDataBR(ocorrencia.dataEncerramento) : new Date();
		
		    if (inicio && fim) {
		        const diffMs = fim - inicio;
		        const horas = diffMs / 1000 / 60 / 60;
		
		        if (horas > 24 && (ocorrencia.idSituacaoEnvio <= "0" || ocorrencia.idSituacaoEnvio === "3")) {
		            tdStatus.textContent = "Em atraso";
		        } else {
		            tdStatus.textContent = ocorrencia.status;
		        }
		    } else {
		        tdStatus.textContent = ocorrencia.status;
		    }
		} else {
		    tdStatus.textContent = ocorrencia.status;
		}
        
 		linha.classList.add('text-center');
        linha.appendChild(tdTipo);
        linha.appendChild(tdOrigem);
        linha.appendChild(tdPrioridade);
        linha.appendChild(tdDataOcorrencia);
        linha.appendChild(tdDatainicioAtendimento);
        linha.appendChild(tdtempoDecorrido);
        linha.appendChild(tdStatus);

        if (ocorrencia.id) {
            const tdAcao = document.createElement('td');
            tdAcao.classList.add('text-center'); // Centraliza o conteúdo da célula
            // Container flexível para centralizar os botões
            const divBotoes = document.createElement('div');
            divBotoes.className = 'd-flex justify-content-center gap-1';
            // Botão Atender/Encerrar Atendimento
            const btnAtender = document.createElement('button');
            btnAtender.className = 'btn btn-sm btn-success';
            btnAtender.style.minWidth = '150px';
            
           const atendimentoFinalizado = ((ocorrencia.status === "Encerrado"));

		if (atendimentoFinalizado) {
		    btnAtender.textContent = 'Finalizado';
		    btnAtender.classList.remove('btn-success');
		    btnAtender.classList.add('btn-secondary');
		    btnAtender.disabled = true;
			} else {
			    const deveEncerrar = ((ocorrencia.status !== "-"  && ["1", "2"].includes(ocorrencia.idSituacaoEnvio)) 
			    || (ocorrencia.status === "Guarnição liberada"));
			
			    if (deveEncerrar) {
			        // Transforma o botão em "Encerrar atendimento"
			        btnAtender.classList.remove('btn-success');
			        btnAtender.classList.add('btn-danger');
			        btnAtender.textContent = 'Encerrar atendimento';
			        btnAtender.disabled = false;
			        btnAtender.addEventListener('click', () => {
							if(ocorrencia.idSituacaoEnvio != "1" && ocorrencia.status === "Em andamento"){
								Swal.fire({
			                        icon: 'error',
			                        title: 'Atenção',
			                        text: 'O atendimento ja foi aceito pela guarnição designiada. Caso queira finalizar esse atendimento entre me contato com a guarnição e solicite a finalização do atendimento.'
			                    });
								return;
							}
			            encerrarAtendimento(ocorrencia.idAtendimento, ocorrencia.idGuarnicao);
			        });
			    } else {
			        // Define o texto original
			        btnAtender.textContent = (ocorrencia.status === "-") ? 'Iniciar atendimento' : 'Enviar guarnição';
			        btnAtender.addEventListener('click', () => {
			            if (ocorrencia.status === "-") {
			                abrirModalAtendimento(ocorrencia.id, ocorrencia.origem_registro, ocorrencia.idRegistroFato);
			            } else {
			                document.getElementById("idAtendimentoHidden").value = ocorrencia.idAtendimento;
			                abrirModalEnvioGuarnicaoDireto();
			            }
			        });
			    }	
		    }
            // Botão Detalhes
            const btnDetalhes = document.createElement('button');
            btnDetalhes.className = 'btn btn-sm btn-outline-primary';
           	btnDetalhes.innerHTML = `<i class="bi bi-search"></i>`;
			btnDetalhes.title = "Detalhes da ocorrencia";
            btnDetalhes.addEventListener('click', () => {
                abrirModalDetalhes(ocorrencia.id, ocorrencia.origem_registro);
            }); 
           // Botão Anexo 
            const btnAnexos = document.createElement('button');
            btnAnexos.className = 'btn btn-sm btn-outline-primary';
            btnAnexos.innerHTML = `<i class="bi bi-paperclip"></i>`;
			btnAnexos.title = "Anexar documentos";
			if (ocorrencia.status === '-') {
			    btnAnexos.disabled = true;
			}
            btnAnexos.addEventListener('click', () => {
                abrirModalAnexos(ocorrencia.idAtendimento);
            });           
            // Botão Para B.O.          
            const btnAbrirBO = document.createElement('button');
			btnAbrirBO.className = 'btn btn-sm btn-outline-success';
			if(ocorrencia.temBoletim === '1'){
				btnAbrirBO.className = 'btn btn-sm btn-outline-primary';
				btnAbrirBO.textContent = 'Detalhar B.O.';				
			}else {
				btnAbrirBO.textContent = 'Cadastrar B.O.';
			}
			if (ocorrencia.status === '-' || ocorrencia.status === 'Encerrado') {				
			    btnAbrirBO.disabled = true;
			}
			btnAbrirBO.style.minWidth = '120px';
			btnAbrirBO.addEventListener('click', () => {
			    abrirModalBOcadastro(ocorrencia.idRegistroFato);
			});
            // Adiciona os botões ao container flex
            divBotoes.appendChild(btnAtender);
            divBotoes.appendChild(btnDetalhes);
            divBotoes.appendChild(btnAnexos);           
            divBotoes.appendChild(btnAbrirBO);    

            tdAcao.appendChild(divBotoes);
            linha.appendChild(tdAcao);

            tabelaNaoAtendidas.appendChild(linha);
        } else {
            tabelaAtendidas.appendChild(linha);
        }
    });
}

function abrirModalAtendimento(id, origem, idRegistroFato) {
    Swal.fire({
        title: "Iniciar atendimento!",
        text: "Tem certeza que deseja iniciar um atendimento?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Iniciar"
    }).then((result) => {
        if (result.isConfirmed) {          
            $.ajax({
                type: "POST",
                url: "/MuralhaDigital/Atendimento", 
                data: {
                    acao: "iniciarAtendimento",
                    idOcorrencia: id,
                    origemRegistro: origem,
                    idRegistroFato: idRegistroFato                   
                },
                success: function(response) {
	           
				var atendimento
	 				var $xml = $(response)
	 				$xml.find('atendimento').each(function () {
		                var $item = $(this);
		                atendimento = $item.find('idAtendimento').text();	               
		            }); 				
					
					document.getElementById("idAtendimentoHidden").value = atendimento;
					const modalElement = document.getElementById('modalAtendimento');
                    if (modalElement) {
 						$('#selGuarnicoes').val('');
                        $('#obsEnvioGuarnicao').val('');
                        const modal = new bootstrap.Modal(modalElement, {
			            backdrop: 'static',
			            keyboard: false
			        });
                        modal.show();
                        obterGuarnicoes();
                    } else {
                        console.error("Modal modalAtendimento não encontrado!");
                    }
                },
                error: function() {
                    Swal.fire({
                        icon: 'error',
                        title: 'Erro!',
                        text: 'Erro ao iniciar o atendimento.'
                    });
                }
            });
        }
    });
}

function abrirModalEnvioGuarnicaoDireto() {
	
	preencherModalAtendimento();
	
    const modalElement = document.getElementById('modalAtendimento');
    if (modalElement) {
        $('#selGuarnicoes').val('');
        $('#obsEnvioGuarnicao').val('');
        const modal = new bootstrap.Modal(modalElement, {
            backdrop: 'static',
            keyboard: false
        });
        modal.show();
        obterGuarnicoes();
    } else {
        console.error("Modal modalAtendimento não encontrado!");
    }
}

async function abrirModalDetalhes(id) {
	
	const ocorrencia = ocorrenciasCache.find(o => o.id === id);
	obterHistorico(ocorrencia.idAtendimento);    
    if (!ocorrencia) {
        conteudo.textContent = "Ocorrência não encontrada.";
        return;
    }try {
        // Espera o histórico carregar
        const historicos = await obterHistorico(ocorrencia.idAtendimento);
        const docs		 = await obterDocumentos(ocorrencia.idAtendimento);

        // Agora popula as duas abas do modal
        preencherModalDetalhes(ocorrencia, historicos);
        preencherDocumentos(docs)

        // Abre o modal
        const modalElement = document.getElementById('modalDetalhes');
        const modal = new bootstrap.Modal(modalElement);
        modal.show();

    } catch (error) {
        console.error("Erro ao abrir modal com histórico:", error);
    }
}

function gerarChaveSegura(origem) {
    const texto = "ignorarPrivado";
    const embaralhado = btoa(texto + "|" + origem + "|" + new Date().getTime());
    return embaralhado;
}

function preencherModalDetalhes(ocorrencia, historicos) {
	document.getElementById("idRegistroFato").value = ocorrencia.idRegistroFato
    const conteudoDetalhes = document.getElementById("conteudoModalDetalhes");
    conteudoDetalhes.innerHTML = `
        <p><strong>Protocolo:</strong> ${ocorrencia.protocolo === "" ? 'Atendimento não iniciado' : ocorrencia.protocolo}</p>
        <p><strong>Origem da ocorrência:</strong> ${ocorrencia.origem}</p>
        <p><strong>Placa:</strong> ${ocorrencia.placaVeiculo && ocorrencia.placaVeiculo.trim() ? ocorrencia.placaVeiculo : 'N/D'}</p>
        <p><strong>Tipo:</strong> ${ocorrencia.tipo}</p>
        <p><strong>Data da ocorrência:</strong> ${ocorrencia.data}</p>
        <p><strong>Endereço inicial do fato:</strong> ${ocorrencia.enderecoLocalEvento && ocorrencia.enderecoLocalEvento.trim() ? ocorrencia.enderecoLocalEvento : 'N/D'}</p>
        ${ocorrencia.enderecoAlerta && ocorrencia.enderecoAlerta.trim() ? '<p><strong>Endereço do alerta:</strong> Radar ' + ocorrencia.idLocal + '  - '  + ocorrencia.enderecoAlerta : ''}
        <p><strong>Observação:</strong> ${ocorrencia.descricao && ocorrencia.descricao.trim() ? ocorrencia.descricao : 'N/D'}</p>
    `;
    
    if (ocorrencia.origem_registro === "1") {
        const btnRedirect = document.createElement('button');
        btnRedirect.textContent = 'Mais detalhes.';
        btnRedirect.classList.add('btn', 'btn-primary', 'mt-2');
        btnRedirect.addEventListener('click', () => {
			
		sessionStorage.setItem('ignore-privado', gerarChaveSegura('atendimento'));
	    popupWindow(
	        `/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=${ocorrencia.idAlerta}`,
	        'popupAlerta',
	        window,
	        1000,  // largura
	        700    // altura
	    	);
		});
        conteudoDetalhes.appendChild(btnRedirect);
    }

    const conteudoHistorico = document.getElementById("conteudoModalHistorico");
    if (historicos.length === 0) {
        conteudoHistorico.innerHTML = "<p>Nenhum histórico encontrado.</p>";
    } else {
        let htmlHistorico = "";
        historicos.forEach(h => {
            htmlHistorico += `
                <div class="mb-3">
                    <p><strong>Tipo:</strong> ${h.descricao}</p>
                    <p><strong>Evento:</strong> ${h.evento}</p>
                    <p><strong>Data:</strong> ${h.data}</p>
                    <p><strong>Usuário:</strong> ${h.nome}</p>
                    <hr>
                </div>
            `;
        });
        conteudoHistorico.innerHTML = htmlHistorico;
    }
}

function abrirModalAnexos(id) {
	//console.log("id que vem da lista ", id)F
 	const ocorrencia = ocorrenciasCache.find(o => o.idAtendimento === id);
 	const conteudoAnexo = document.getElementById("conteudoModalAnexos");
	document.getElementById("idOcorrenciaAtendimento").value = ocorrencia.idAtendimento;
 	conteudoAnexo.innerHTML = `
        <p><strong>Protocolo:</strong> ${ocorrencia.protocolo}</p>`;

    const modal = new bootstrap.Modal(document.getElementById("modalAnexos"));
    modal.show();
}

function parseDataBR(dataStr) {
    if (!dataStr || dataStr.trim() === "") return null;

    const [dataPart, horaPart] = dataStr.split(" ");
    const [dia, mes, ano] = dataPart.split("/").map(Number);
    const [hora, minuto, segundo] = horaPart.split(":").map(Number);

    if (
        isNaN(dia) || isNaN(mes) || isNaN(ano) ||
        isNaN(hora) || isNaN(minuto) || isNaN(segundo)
    ) {
        //console.error("Data inválida:", dataStr);
        return null;
    }
    const dataFinal = new Date(ano, mes - 1, dia, hora, minuto, segundo);   
    return dataFinal;
}

function calcularTempoEntre(dataInicioStr, dataFimStr) {
    const inicio = parseDataBR(dataInicioStr);
    const fim = dataFimStr ? parseDataBR(dataFimStr) : new Date();

    if (!inicio || !fim) return "-";

    const diffMs = fim - inicio;  
    const segundos = Math.floor(diffMs / 1000) % 60;
    const minutos = Math.floor(diffMs / 1000 / 60) % 60;
    const horas = Math.floor(diffMs / 1000 / 60 / 60);
   
    return `${horas}h ${minutos}m ${segundos}s`;
}

function enviarGuarnicao() {
    var idGuarnicao = document.getElementById('selGuarnicoes').value;
    var obsGuarnicao = document.getElementById('obsEnvioGuarnicao').value;
    var idAtendimento = document.getElementById("idAtendimentoHidden").value;
    var nomeGuarnicao = document.getElementById('selGuarnicoes').selectedOptions[0].text;
    
    if(idGuarnicao === "0" || idGuarnicao ===""){
	alert('Obrigatorio selecionar uma guarnição.')
	return;
    }
    
    if(obsGuarnicao ===""){
	alert('O campo observações deve ser preenchido.')
	return;
    }
    
    $.ajax({
        type: "POST",
        url: "/MuralhaDigital/Atendimento",      
        data: {
            acao: "enviarGuarnicoes",
            idGuarnicao: idGuarnicao,
            obsGuarnicao: obsGuarnicao,
            idAtendimento: idAtendimento,
            nomeGuarnicao: nomeGuarnicao
        },        
        success: function (data) {
            Swal.fire({
                icon: 'success',
                title: 'Sucesso',
                text: data
            }).then(() => {
        // Fecha o modal após o usuário clicar em "OK" no alerta
        $('#modalAtendimento').modal('hide');
        
    		});
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao enviar Guarnição:", errorThrown);
            Swal.fire({
                icon: 'error',
                title: 'Erro!',
                text: jqXHR.responseText
            });
        }
    });
}

function encerrarAtendimento(idAtendimento, idGuarnicao){	
	 Swal.fire({
        title: "Finalizar atendimento!",
        text: "Tem certeza que deseja finalizar o atendimento??",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Sim"
    }).then((result) => {
        if (result.isConfirmed) {          
           $.ajax({
		        type: "POST",
		        url: "/MuralhaDigital/Atendimento",      
		        data: {
		            acao: "encerrarAtendimento",          
		            idAtendimento: idAtendimento,
		            idGuarnicao: idGuarnicao
		        },
                success: function (data) {			
		            Swal.fire({
		                icon: 'success',
		                title: 'Sucesso',
		                text: data
		            }).then(() => {
		        // Fecha o modal após o usuário clicar em "OK" no alerta
		        $('#modalAtendimento').modal('hide');
		        
		    		});
		        },
                 error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao enviar Guarnição:", errorThrown);
	            Swal.fire({
	                icon: 'error',
	                title: 'Erro!',
	                text: 'Erro ao finalizar atendimento!'
	            });
                }
            });
        }
    });
}

function enviarAnexos() {
    const tiposPermitidos = ['application/pdf', 'image/jpeg', 'image/png']; 
	
    var inputArquivo = document.getElementById("inputArquivo");
    var arquivo = inputArquivo.files[0];
	var tipoArquivo = arquivo.type;
	
    var request = new Object();
    request.acao    			= "anexarDocumentos";
    request.idAtendimento		= document.getElementById("idOcorrenciaAtendimento").value;
    request.obsString			= document.getElementById("observacao").value;
    request.tipoArquivo			= tipoArquivo   
    var dataString = JSON.stringify(request);	
	var formData = new FormData();
	
	formData.append("info_json", dataString);     

    if (!tiposPermitidos.includes(arquivo.type)) {
        alert(`Tipo de arquivo inválido: ${arquivo.name}`);
        inputArquivo.value = "";
        return;
    }
    formData.append("arquivo", arquivo);   
	
    $.ajax({
        type: "POST",
        url: "/MuralhaDigital/Anexo",
        data: formData,
        processData: false,
        contentType: false,
        success: function (data) {           
            Swal.fire({
                icon: 'success',
                title: 'Sucesso',
                text: data
            }).then(() => {
                $('#modalAnexos').modal('hide');
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao anexar documentos ao atendimento:", errorThrown);
            Swal.fire({
                icon: 'error',
                title: 'Erro!',
                text: 'Erro ao tentar anexar documentos ao atendimento!'
            });
        }
    });
}

function obterHistorico(idAtendimento) {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: "/MuralhaDigital/Atendimento",
            dataType: "xml",
            data: {
                acao: "obterHistorico",
                idAtendimento: idAtendimento
            },
            success: function (data) {
                const $xml = $(data);
                const lista = $xml.find("listaHistoricos").find("historico");
                
                const historicos = [];
                lista.each(function () {
                    const $historico = $(this);
                    historicos.push({
                        descricao: $historico.find("tipoHistorico").text(),
                        evento: $historico.find("evento").text(),
                        data: $historico.find("dataFormatada").text(),
                        nome: $historico.find("usuario").text()
                    });
                });         
                
                resolve(historicos);
                //console.log(data)
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erro ao obter histórico:", errorThrown);
                reject(errorThrown);
            }
        });
    });
}

function obterDocumentos(idAtendimento) {
    return new Promise((resolve, reject) => {
        $.ajax({
            type: "GET",
            url: "/MuralhaDigital/Anexo",
            dataType: "xml",
            data: {
                acao: "obterDocumentos",
                idAtendimento: idAtendimento
            },
            success: function (data) {
			const $xml = $(data);
            const lista = $xml.find("listaDocumentos").find("documento");
            const documentos = [];
                lista.each(function () {
                    const $documento = $(this);
                    documentos.push({
						id: $documento.find("id").text(),
                        detalhamento: $documento.find("detalhamento").text(),
                        tipo: $documento.find("tipo").text(), 
                        dirArquivo: $documento.find("dirArquivo").text(),                       
                    });
                });
                
                resolve(documentos);               
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erro ao obter documentos:", errorThrown);
                reject(errorThrown);
            }
        });
    });
}

function preencherDocumentos(documentos) {
    const lista = document.getElementById('conteudoModalDocumentos');
    lista.innerHTML = '';

    if (documentos.length === 0) {
        lista.innerHTML = '<p>Nenhum documento encontrado.</p>';
    } else {
       
	    documentos.forEach(doc => {
		    const container = document.createElement('div');
		    container.className = 'd-flex justify-content-between align-items-center mb-2 p-2 border rounded bg-light shadow-sm';
		
		    const infoDiv = document.createElement('div');
		    infoDiv.className = 'd-flex align-items-center';
		
		    const icon = document.createElement('span');
		    icon.innerHTML = `
		        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="#0d6efd" class="bi bi-file-earmark-text me-2" viewBox="0 0 16 16">
		          <path d="M14 4.5V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2h6.5L14 4.5z"/>
		          <path d="M6 5h4v1H6V5zm0 2h4v1H6V7zm0 2h2v1H6V9z"/>
		        </svg>
		    `;		
		    const texto = document.createElement('span');
		    texto.textContent = `${doc.detalhamento} (${doc.tipo})`;
		
		    infoDiv.appendChild(icon);
		    infoDiv.appendChild(texto);
		
		    const btn = document.createElement('a');
		    btn.href = `/MuralhaDigital/Anexo?acao=downloadDocumento&id=${doc.id}`;
		    btn.className = 'btn btn-sm btn-outline-success';
		    btn.target = '_blank';
		    btn.innerHTML = `
		        <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download me-1" viewBox="0 0 16 16">
		          <path d="M.5 9.9V11a1 1 0 0 0 1 1h13a1 1 0 0 0 1-1V9.9h-1V11H1V9.9H.5z"/>
		          <path d="M7.646 10.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 1 0-.708-.708L8.5 9.293V1.5a.5.5 0 0 0-1 0v7.793L5.354 7.146a.5.5 0 1 0-.708.708l3 3z"/>
		        </svg>
		        Baixar
		    `;
		    container.appendChild(infoDiv);
		    container.appendChild(btn);
		    lista.appendChild(container);
		});
    }
}
function popupWindow(url, windowName, win, w, h) {
    const y = win.top.outerHeight / 2 + win.top.screenY - (h / 2);
    const x = win.top.outerWidth / 2 + win.top.screenX - (w / 2);
    return win.open(
        url,
        windowName,
        `toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=no,resizable=no,copyhistory=no,width=${w},height=${h},top=${y},left=${x}`
    );
}

function obterGuarnicoesStatus() {    
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Atendimento",
        dataType: "xml",
        data: "acao=obterGuarnicoesStatus",
        success: function (data){
           const $xml = $(data);
           const lista = $xml.find('guarnicao');
           console.log("Minhas novas guarnições: ", data)            
            
            const guarnicoes  = [];

            lista.each(function () {
                const $guarnicao = $(this);
				
                const nome = $guarnicao.find("nome").text();
                const id = $guarnicao.find("id").text();
                const ativo = $guarnicao.find("ativo").text();
                const disponivel = $guarnicao.find("disponivel").text();
                const responsavel = $guarnicao.find("responsavel").text();
                ;                
                guarnicoes .push({
                    id: id, 
                    nome: nome,
                    ativo: ativo, 
                    disponivel: disponivel,
                    responsavel: responsavel,               
                                     
                });
            });
			           
            abrirModalStatusGuarnicoes(guarnicoes );          
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao obter Guarnições:", errorThrown);
        }
    });
}

function abrirModalStatusGuarnicoes(guarnicoes ) {
    const conteudo = document.getElementById("conteudoModalStatusGuarnicoes");
    let html = "<table class='table table-bordered table-striped'>";
    html += "<thead><tr><th>Guarnição</th><th>Status</th><th>Disponibilidade</th><th>Responsavel</th></tr></thead><tbody>";

    guarnicoes.forEach(g => {
        const statusAtivo = g.ativo === "1"
		    ? "<span class='badge bg-success'>Ativo</span>"
		    : "<span class='badge bg-danger'>Inativo</span>";
		
		const disponibilidade = g.disponivel === "1"
		    ? "<span class='badge bg-primary'>Disponível</span>"
		    : "<span class='badge bg-secondary'>Indisponível</span>";
        html += `
            <tr>
                <td>${g.nome}</td>
                <td>${statusAtivo}</td>
                <td>${disponibilidade}</td>
                <td>${g.responsavel}</td>
            </tr>
        `;
    });

    html += "</tbody></table>";
    conteudo.innerHTML = html;  
    $('#modalStatusGuarnicoes').modal('show');
}

document.addEventListener('boletimSalvoCadastro', function (e) {
	const idBoletim = e.detail.boletimId;
    const origem = $('#modalBoletim').data('origem'); 
    const idAtendimento = $('#modalBoletim').data('idAtendimento');
   // console.log("Recebido ID do modal:", idBoletim, "Origem:", origem, "Atendimento ", idAtendimento);
    if (origem === 'atendimento') {        
        atualizarAtendimentoComBoletim(idBoletim, idAtendimento);
    }
});

function atualizarAtendimentoComBoletim(idBoletim, idAtendimento){
    $.ajax({
        type: "POST",
        url: "/MuralhaDigital/Atendimento", 
        data: {
            acao: "vincularBoletimAtendimento",
            idBoletim: idBoletim,
            idAtendimento: idAtendimento                   
        },
        success: function(response) {       
		  console.log("Atendimento atualizado com sucesso, mas sem ações na tela.");
        },
        error: function() {
             console.log("Erro ao vincular boletim com atendimento.");
        }
    });
}

function abrirTelaListagemGuarnicoes() {
    window.open('/muralha-digital/pages/guarnicao/listagem-guarnicoes.jsp', '_blank');
}
function abrirModalBO(){
	idRegistroFato = document.getElementById("idRegistroFato").value;
	 $('#modalDetalhes').modal('hide');
	abrirModalEditarRegistroDeFatoAbos(idRegistroFato);
}

function abrirModalBOcadastro(id){	
	abrirModalEditarRegistroAtendimento(id);
}

function preencherModalAtendimento() {

    const idOcorrencia = document.getElementById('idAtendimentoHidden').value;
    const ocorrencia = ocorrenciasCache.find(o => o.idAtendimento === idOcorrencia);

    if (!ocorrencia) {
        console.warn('Ocorrência não encontrada para o ID informado.');
        return;
    }

    const conteudoAtendimento = document.getElementById("conteudoModalAtendimento");

    conteudoAtendimento.innerHTML = `
        <div class="card shadow-sm border-0">
            <div class="card-body">
                <p class="mb-2">
                    <strong>Endereço inicial do fato:</strong><br>
                    <span class="text-dark">
                        ${ocorrencia.enderecoLocalEvento && ocorrencia.enderecoLocalEvento.trim()
                            ? ocorrencia.enderecoLocalEvento
                            : 'Não disponível'}
                    </span>
                </p>

                ${ocorrencia.enderecoAlerta && ocorrencia.enderecoAlerta.trim() ? `
                    <hr>
                    <p class="mb-0">
                        <strong class="text-secondary">Endereço do alerta:</strong><br>
                        <span class="text-dark">
                            Radar ${ocorrencia.idLocal} - ${ocorrencia.enderecoAlerta}
                        </span>
                    </p>
                ` : ''}
            </div>
        </div>
    `;
}