<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.consilux.model.Processamento"%>
<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<%
	Boolean comObliteracao = ConfiguracaoProvider.getInstance().getComObliteracao();
	Boolean confirmaObliteracao = ConfiguracaoProvider.getInstance().getConfirmaObliteracao();
	Boolean comMarcaProcesso = ConfiguracaoProvider.getInstance().getComMarcaProcesso();
	Boolean comEspecieProcesso = ConfiguracaoProvider.getInstance().getComEspecieProcesso();
	Boolean comUfValidacao = ConfiguracaoProvider.getInstance().getComUfValidacao();
	Integer tamanhoTarja = ConfiguracaoProvider.getInstance().getTamanhoTarja();
	Integer alertaVelocidade = ConfiguracaoProvider.getInstance().getAlertaVelocidade();
	Boolean alertaIsento = ConfiguracaoProvider.getInstance().getAlertaIsento();
%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="com.consilux.infra.ExpValida"%>
<c:set var="com_obliteracao" value="<%=comObliteracao%>" scope="request"/>
<c:set var="com_obliteracao" value="${com_obliteracao && forca_sem_obliteracao != '1'}" scope="request"/>
<c:set var="confirma_obliteracao" value="<%=confirmaObliteracao%>" scope="request"/>
<c:set var="com_marca_processo" value="<%=comMarcaProcesso%>" scope="request"/>
<c:set var="com_especie_processo" value="<%=comEspecieProcesso%>" scope="request"/>
<c:set var="com_uf_validacao" value="<%=comUfValidacao%>" scope="request"/>
<c:set var="tamanho_tarja" value="<%=tamanhoTarja%>" scope="request"/>
<c:set var="alerta_velocidade" value="<%=alertaVelocidade%>" scope="request"/>
<c:set var="alerta_isento" value="<%=alertaIsento%>" scope="request"/>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/multiplas_imagens.js"></script>
<script type="text/javascript" src="/js/obliteracao.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="/GtwClientLogger/GtwClientLogger.nocache.js"></script>
<script type="text/javascript">
	var escondidos = false;
	
	function consistir() {
        var div_infracao = document.getElementById("div_infracao");
        var id_infracao = div_infracao.innerHTML;
        var id_imagem = document.getElementById("id_imagem");
        if (document.getElementById("txt_placa"))
            var txt_placa = document.getElementById("txt_placa");
        if (document.getElementById("sel_marca_processo"))
            var sel_marca_processo = document.getElementById("sel_marca_processo");
        if (document.getElementById("sel_especie_processo"))
            var sel_especie_processo = document.getElementById("sel_especie_processo");
        if (document.getElementById("sel_uf_processo"))
            var sel_uf_processo = document.getElementById("sel_uf_processo");
        if (document.getElementById("obliteracao_x")) {
	        var obliteracao_x = document.getElementById("obliteracao_x");
	        var obliteracao_y = document.getElementById("obliteracao_y");
	        var obliteracao_largura = document.getElementById("obliteracao_largura");
	        var obliteracao_altura = document.getElementById("obliteracao_altura");
        }

        
        if (!validaConsistencia()){
          return false;
        }
       
        var params = {
            acao: <%=Processamento.Acao.CONSISTE.ordinal()%>,
            id_processo: ${id_processo},
            id_imagem: id_imagem.value,
            infracao: id_infracao,
        };
        
        if (txt_placa)
            params["placa"] = txt_placa.value;
        if (document.getElementById("sel_marca_processo"))
            params["id_marca_processo"] = sel_marca_processo.value;
        if (document.getElementById("sel_especie_processo"))
            params["id_especie_processo"] = sel_especie_processo.value;
        if (document.getElementById("sel_uf_processo"))
            params["uf_processo"] = sel_uf_processo.value;
        if (document.getElementById("obliteracao_x")) {
            params["obliteracao_x"] = obliteracao_x.value;
            params["obliteracao_y"] = obliteracao_y.value;
            params["obliteracao_largura"] = obliteracao_largura.value;
            params["obliteracao_altura"] = obliteracao_altura.value;
        }

		$.blockUI({ message: '<h1 id="blocked"><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false, fadeTime: 0, fadeIn: 0});
    	var ref_processar = iniciaTempoProcessamento(id_infracao,0,'PROCESSAR [ID_INCONSISTENCIA:0|ID_PROCESSO:${id_processo}]');
        tempo = finalizaTempoProcessamento(ref_log_processamento);
		params["tempo"] = tempo; 
		$.get('/ajax/processamento/Processar', params, function(xml){
				var ret = $("INFRACAO",xml).text();
				if (trataRetorno(xml))
				    carregaInfracao(ret);
		        finalizaTempoProcessamento(ref_processar);
                $.unblockUI({fadeTime: 0, fadeOut: 0});
		});
		if (txt_placa)
			txt_placa.value = "";
		
		return true;
	}
	
	function inconsistir() {
        var div_infracao = document.getElementById("div_infracao");
        var id_infracao = div_infracao.innerHTML;
        var sel_inconsistencia = document.getElementById("sel_inconsistencia");
		var txt_inconsistencia = document.getElementById("txt_inconsistencia");

		ajustaInconsistencia(false);

		if (!validaInconsistencia()){
		  return false;
		}

		$.blockUI({ message: '<h1 id="blocked"><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false, fadeTime: 0, fadeIn: 0});
    	var ref_processar = iniciaTempoProcessamento(id_infracao,0,'PROCESSAR [ID_INCONSISTENCIA:'+sel_inconsistencia.value+'|ID_PROCESSO:${id_processo}]');
        tempo = finalizaTempoProcessamento(ref_log_processamento);
		$.get('/ajax/processamento/Processar', {
				acao: <%=Processamento.Acao.INCONSISTE.ordinal()%>,
	            id_processo: ${id_processo},
                id_inconsistencia: sel_inconsistencia.value,
                infracao: id_infracao,
                tempo: tempo
			}, function(xml){
				var ret = $("INFRACAO",xml).text();
				if (trataRetorno(xml)) 
				    carregaInfracao(ret);
		        finalizaTempoProcessamento(ref_processar);
                $.unblockUI({fadeTime: 0, fadeOut: 0});
		});

		return true;
	}
	
	function atual() {
		$.get('/ajax/processamento/Processar', {
		  acao: <%=Processamento.Acao.ATUAL.ordinal()%>,
          id_processo: ${id_processo},
          infracao: '0'
		  }, function(xml) {
			var ret = $("INFRACAO",xml).text();
			if (trataRetorno(xml)) 
			    carregaInfracao(ret);
		});
		document.title = "${strEtapaProcesso}";
	}
	
	function anterior() {
        var div_infracao = document.getElementById("div_infracao");
        $.blockUI({ message: '<h1><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false });
		$.get('/ajax/processamento/Processar', { 
		  acao: <%=Processamento.Acao.ANTERIOR.ordinal()%>, 
          id_processo: ${id_processo},
          infracao: div_infracao.innerHTML
		  }, function(xml) {
			var infracao = $("INFRACAO",xml).text();
            if (infracao > 0)
                carregaInfracao(infracao);
            $.unblockUI();
		});
	}
	
    function proximo() {
        var div_infracao = document.getElementById("div_infracao");
        $.blockUI({ message: '<h1><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false });
        $.get('/ajax/processamento/Processar', {
          acao: <%=Processamento.Acao.PROXIMO.ordinal()%>,
          id_processo: ${id_processo},
          infracao: div_infracao.innerHTML
          }, function(xml) {
            var infracao = $("INFRACAO",xml).text();
            if (infracao > 0)
                carregaInfracao(infracao);
           	$.unblockUI();
            return;
        });
       	$.unblockUI();
    }

    function mostraDetalhes() {
        var div_infracao = document.getElementById("div_infracao");
        var idInfracao= div_infracao.innerHTML;

        window.open("/infracao/infracao_completa.jsp?id_infracao="+idInfracao,"Detalhes","width=1024, height=600");
	}

    function espera() {
        var div_infracao = document.getElementById("div_infracao");
        $.blockUI({ message: '<h1><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false });
        tempo = finalizaTempoProcessamento(ref_log_processamento);
        $.get('/ajax/processamento/Processar', {
          acao: <%=Processamento.Acao.ESPERA.ordinal()%>,
          id_processo: ${id_processo},
          infracao: div_infracao.innerHTML,
          tempo: tempo
          }, function(xml) {
            var ret = $("INFRACAO",xml).text();
            if (trataRetorno(xml))
                carregaInfracao(ret);
            $.unblockUI();
            return;
        });
        $.unblockUI();
    }
    function carregaImagemCroqui(idLocal, pista) {
        var img_croqui_pista = document.getElementById("img_croqui_pista");
        if (idLocal > 0 && pista > 0) {
        	img_croqui_pista.src = "/ajax/ImgLocalPistaCroqui?id_local="+idLocal+"&pista="+pista;
        }
        else {
        	img_croqui_pista.src = "";
        }
    }
	function carregaInfracao(id_infracao) {
		if (!(id_infracao > 0)) {
			window.location = "/processo/FinalizarProcesso";
			return;
		}

        $.blockUI({ message: '<h1><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false });
    	var ref_log_infracao_completa = iniciaTempoProcessamento(id_infracao,0,'INFRACAO_COMPLETA [ID_PROCESSO:${id_processo}]');
		$.get('/ajax/InfoInfracaoCompleta', { id_infracao: id_infracao }, function(xml) {
			if (window.iniciaRestanteSessao)
				iniciaRestanteSessao()
				
			document.getElementById("div_local").innerHTML = $("LOCAL",xml).text()+" - "+$("DESCLOCAL",xml).text();
			document.getElementById("div_pista").innerHTML = $("PISTA",xml).text();
			document.getElementById("div_placa").innerHTML = $("PLACA",xml).text();
			document.getElementById("div_infracao").innerHTML = $("INFRACAO",xml).text();
			document.getElementById("div_data").innerHTML = $("DATA",xml).text();
            document.getElementById("div_id_enquadramento").innerHTML = $("ID_ENQUADRAMENTO",xml).text();
            document.getElementById("div_descricao_enquadramento").innerHTML = $("DESCRICAO_ENQUADRAMENTO",xml).text();
            document.getElementById("div_tit_info_espec").innerHTML = $("TIT_INFO_ESPEC",xml).text();
            if (document.getElementById("div_info_espec_1")) {
				document.getElementById("div_info_espec_1").innerHTML = $("INFO_ESPEC_1",xml).text();
            }
            if (document.getElementById("div_info_espec_2")) {
				document.getElementById("div_info_espec_2").innerHTML = $("INFO_ESPEC_2",xml).text();
            }
            if (document.getElementById("div_info_espec_3")) {
				document.getElementById("div_info_espec_3").innerHTML = $("INFO_ESPEC_3",xml).text();
            }
            if (document.getElementById("div_info_espec_4")) {
				document.getElementById("div_info_espec_4").innerHTML = $("INFO_ESPEC_4",xml).text();
            }
            if (document.getElementById("div_info_espec_5")) {
				document.getElementById("div_info_espec_5").innerHTML = $("INFO_ESPEC_5",xml).text();
            }
			document.getElementById("sel_inconsistencia").value = $("INCONSISTENCIA",xml).text();
			document.getElementById("com_captura_frontal").value = $("EQUIPAMENTO_CAPTURA_FRONTAL",xml).text();
			document.getElementById("area_pista").value = $("AREA_PISTA",xml).text();

	        if (document.getElementById("txt_placa")) {
	            document.getElementById("txt_placa").value = $("PLACA",xml).text();

	            if (document.getElementById("txt_placa").value != '' &&
                    !(document.getElementById("sel_inconsistencia").value > 0)) { //Se trouxe a placa e não tinha inconsistência > 0, retira a consitência, para obrigar o usuário a observar melhor a infração.
                	document.getElementById("sel_inconsistencia").selectedIndex = -1;
                }
	        }
			
            carregaListaImagensInfracao($("ID_INFRACAO",xml).text(), $("ID_IMAGEM_OBJ",xml).text());
                
            if ("${com_obliteracao}" == "true") //com_obliteracao == true
	            buscaObliteracao(id_infracao);
            
            carregaInfracaoProcesso($("INFRACAO_PROCESSO",xml).text());

	        if (document.getElementById("txt_placa")) {
	            buscaCadastro($("PLACA",xml).text());
	        }

            carregaImagemCroqui($("LOCAL",xml).text(), $("PISTA",xml).text());

            if (document.getElementById("txt_placa") && 
           			(document.getElementById("txt_placa").value == '' ||
                	 document.getElementById("txt_inconsistencia").value != '')
                ) {
                document.getElementById("txt_placa").focus();   
                document.getElementById("txt_placa").select();
            }
            else {
                document.getElementById("txt_inconsistencia").focus();
                document.getElementById("txt_inconsistencia").select();
            }
            
            verificaAlertaVelocidade(true);
            
            finalizaTempoProcessamento(ref_log_infracao_completa);
            $.unblockUI();
		});
	}
    
    function carregaInfracaoProcesso(idInfracaoProcesso) {
        ajustaInconsistencia(false);

        if (document.getElementById("sel_marca_processo"))
        	document.getElementById("sel_marca_processo").value = 0;

       	if (document.getElementById("sel_especie_processo"))
           	document.getElementById("sel_especie_processo").value = 0;

       	if (document.getElementById("sel_uf_processo"))
           	document.getElementById("sel_uf_processo").value = '';

       	/* Agora é o carregaInfracao que busca a placa.
        if (document.getElementById("txt_placa")) {
            document.getElementById("txt_placa").value = "";
            if (document.getElementById("div_placa").innerHTML.length > 0) {//Se já tem placa digitada e confirmada..então mostra:
                document.getElementById("txt_placa").value = document.getElementById("div_placa").innerHTML;
                buscaCadastro(document.getElementById("txt_placa").value);
            }
        }
        */

        if (idInfracaoProcesso > 0) {          
        	$.ajax({url: '/ajax/InfoInfracaoProcesso?id_infracao_processo='+idInfracaoProcesso, success: function(xml) {
            	document.getElementById("sel_inconsistencia").value = $("INCONSISTENCIA",xml).text();
            	
            	if (document.getElementById("sel_inconsistencia").value != $("INCONSISTENCIA",xml).text())
            		alert("Inconsistência '"+ $("INCONSISTENCIA",xml).text()+"' não disponível, avise o supervisor.");
            	
	            if (document.getElementById("txt_placa")) {
	                document.getElementById("txt_placa").value = $("PLACA",xml).text();
	                buscaCadastro(document.getElementById("txt_placa").value);
	            }

	            if (document.getElementById("obliteracao_x")) {
                    document.getElementById("obliteracao_x").value = $("X",xml).text();
                    document.getElementById("obliteracao_y").value = $("Y",xml).text();
                    document.getElementById("obliteracao_largura").value = $("LARGURA",xml).text();
                    document.getElementById("obliteracao_altura").value = $("ALTURA",xml).text();
                    verifObliteracao();
                }

                if ($("ID_IMAGEM",xml).text() > 0) {
                    carregaImagem($("ID_IMAGEM",xml).text());
                    selecionaImagem(); //Força a seleção prévia...
                }
                
                if (document.getElementById("sel_marca_processo")) {
	                if ($("ID_MARCA_PROCESSO",xml).text() > 0) {
	                	document.getElementById("sel_marca_processo").value = $("ID_MARCA_PROCESSO",xml).text();
	                }
                }
                if (document.getElementById("sel_especie_processo")) {
	                if ($("ID_ESPECIE_PROCESSO",xml).text() > 0) {
	                	document.getElementById("sel_especie_processo").value = $("ID_ESPECIE_PROCESSO",xml).text();
	                }
                }
                if (document.getElementById("sel_uf_processo")) {
	                if ($("UF_PROCESSO",xml).text() != '') {
	                	document.getElementById("sel_uf_processo").value = $("UF_PROCESSO",xml).text();
	                }
                }
	            ajustaInconsistencia(false);
            }, async: false});
	    }
    }

    function buscaCadastro(placa) {
    	var txt_placa = document.getElementById("txt_placa");
        var div_infracao = document.getElementById("div_infracao");
        var id_infracao = parseInt(div_infracao.innerHTML);
    	var placa_cadastro = document.getElementById("placa_cadastro");

    	//Verificando se está fazendo a mesma consulta...
    	if (placa.length == 7 && placa_cadastro.value == placa)
        	return;
    	else
    		placa_cadastro.value = placa;
        	
    	
    	sel_marca_processo = document.getElementById("sel_marca_processo");
    	sel_especie_processo = document.getElementById("sel_especie_processo");
    	sel_uf_processo = document.getElementById("sel_uf_processo");

    	document.getElementById("div_marca").innerHTML = "&nbsp;";
        document.getElementById("div_cor").innerHTML = "&nbsp;";
        document.getElementById("div_ano").innerHTML = "&nbsp;";
        document.getElementById("div_especie").innerHTML = "&nbsp;";
        document.getElementById("div_uf").innerHTML = "&nbsp;";
        document.getElementById("div_tipo").innerHTML = "&nbsp;";
        document.getElementById("div_categoria").innerHTML = "&nbsp;";
        document.getElementById("div_situacao").innerHTML = "&nbsp;";
        document.getElementById("div_localidade").innerHTML = "&nbsp;";
        document.getElementById("div_atualizacao").innerHTML = "&nbsp;";

        if (placa.length != 7)
            return;
/*//Se deixar isso ativo, quando o usuário volta para a infração anterior some a marca/especie que ele selecionou anteriormente. 
        if (sel_marca_processo)
        	sel_marca_processo.value = 0;
        
        if (sel_especie_processo)
        	sel_especie_processo.value = 0;
*/
		document.getElementById("div_marca").innerHTML = "<b><font color='red'>BUSCANDO CADASTRO...<font><b>";
    	var ref_log_cadastro = iniciaTempoProcessamento(id_infracao,0,'CADASTRO ['+placa+']');
        $.get('/ajax/InfoCadastro', { placa: placa }, function(xml) {

        	var descErro = $("ERRO",xml).text();
        	if (descErro && descErro != "") {
        		document.getElementById("div_marca").innerHTML = "<b><font color='red'>" + descErro + "<font><b>";
	        	return;
        	}

        	//Se a placa veio direto da infração e o usuário já está com uma placa diferente no txt, 
        	//então ignora, porque o cadastro chegou atrasado.
        	if (txt_placa && ($("PLACA",xml).text() != txt_placa.value)) {
        		return;
        	}
        	
            document.getElementById("div_marca").innerHTML = $("MARCA",xml).text() != "" ? $("MARCA",xml).text() : "&nbsp;"; //Artifício técnico para não alterar o layout da tabela
            document.getElementById("div_cor").innerHTML = $("COR",xml).text();
            document.getElementById("div_ano").innerHTML = $("ANO",xml).text();
            document.getElementById("div_especie").innerHTML = $("ESPECIE",xml).text() != "" ? $("ESPECIE",xml).text() : "&nbsp;"; //Artifício técnico para não alterar o layout da tabela
            document.getElementById("div_uf").innerHTML = $("UF",xml).text() != "" ? $("UF",xml).text() : "&nbsp;"; //Artifício técnico para não alterar o layout da tabela
            document.getElementById("div_tipo").innerHTML = $("TIPO",xml).text() != "" ? $("TIPO",xml).text() : "&nbsp;" //Artifício técnico para não alterar o layout da tabela
            document.getElementById("div_categoria").innerHTML = $("CATEGORIA",xml).text();
            document.getElementById("div_situacao").innerHTML = $("SITUACAO",xml).text();
            document.getElementById("div_localidade").innerHTML = $("LOCALIDADE",xml).text();
            document.getElementById("div_atualizacao").innerHTML = $("ATUALIZACAO",xml).text();
            if (sel_marca_processo) {
                if ($("MARCA",xml).text() == "" && $("ID_MARCA_DISPONIVEL",xml).text() > 0) {
                	sel_marca_processo.value = $("ID_MARCA_DISPONIVEL",xml).text()
                }
                else
                	sel_marca_processo.selectedIndex = 0;
            }
            if (sel_especie_processo) {
                if ($("ESPECIE",xml).text() == "" && $("ID_ESPECIE_DISPONIVEL",xml).text() > 0) {
                	sel_especie_processo.value = $("ID_ESPECIE_DISPONIVEL",xml).text()
                }
                else
                	sel_especie_processo.selectedIndex = 0;
            }
            if (sel_uf_processo) {
                if ($("UF",xml).text() == "" && $("UF_DISPONIVEL",xml).text() != "") {
                	sel_uf_processo.value = $("UF_DISPONIVEL",xml).text()
                }
                else
                	sel_uf_processo.selectedIndex = 0;
            }
            finalizaTempoProcessamento(ref_log_cadastro);
        });
    }

    function validaPlaca(placa) {
        var regexp = /<%=ExpValida.PLACA.getPattern()%>/
        var regexp1= /<%=ExpValida.PLACA_MERCOSUL.getPattern()%>/
        if (!regexp.test(placa) && !regexp1.test(placa)) { 
            alert("Placa inválida!");
            return false;
        }
        
        return true;
    }

    function confirmaObliteracao() {
        var obliteracao_largura = document.getElementById("obliteracao_largura");
        var com_captura_frontal = document.getElementById("com_captura_frontal");
        if (!obliteracao_largura.value > 0 && com_captura_frontal.value == 'true') {
            return confirm("Imagem sem OBLITERAÇÃO, prosseguir assim mesmo?");
        } 
        return true;
    }

    function verificaAlertaVelocidade(somenteEnfase) {
        var div_info_espec_1 = document.getElementById("div_info_espec_1");
        var info_espec_1 = div_info_espec_1.innerHTML;
        var posKm = info_espec_1.indexOf('km/h');
        if (posKm > 8) {
        	var velocidade = parseInt(info_espec_1.substring(info_espec_1.indexOf(':')+2, posKm));
        	if (velocidade > ${alerta_velocidade}) {
        		if (somenteEnfase) {
        			div_info_espec_1.style.fontSize = 'large';
        			div_info_espec_1.style.color = '#FF0000';
        		}
        		else
            		return confirm("Atenção! Velocidade superior a "+${alerta_velocidade}+"km/h, prosseguir assim mesmo?");
	        }
        	else {
    			div_info_espec_1.style.fontSize = '11px';
    			div_info_espec_1.style.color = '#000000';
        	}
        } 
        return true;
    }

    function verificaAlertaIsento(placa, id_enquadramento, data_hora, area) {
        var bRet = true;
		var data = data_hora.substring(0,10);		
        var div_infracao = document.getElementById("div_infracao");
        var id_infracao = parseInt(div_infracao.innerHTML);

        //Utiliza o $.ajax, ou invés do $.get, porque precisa ser síncrono.
    	var ref_log_isento = iniciaTempoProcessamento(id_infracao,0,'ISENTO ['+placa+']');
        $.ajax({url: '/ajax/InfoIsento?placa='+placa+'&id_enquadramento='+id_enquadramento+'&data='+data+'&area='+area, success: function(xml) {

			var descErro = $("ERRO",xml).text();
        	if (descErro && descErro != "") {
        		alert(descErro);
	        	return;
        	}

            var placa = $("PLACA",xml).text();
            var data_inicio = $("DATA_INICIO",xml).text();
            var horario_inicio = $("HORARIO_INICIO",xml).text();
            var data_fim = $("DATA_FIM",xml).text();
            var horario_fim = $("HORARIO_FIM",xml).text();
            var motivo = $("MOTIVO",xml).text();
            var id_inconsistencia = $("ID_INCONSISTENCIA",xml).text();

            var mensagem = "";

			if (placa != "") {
				mensagem = 'Veículo ISENTO!\n'

				if (data_inicio.length > 0 || data_fim.length > 0)
	    	      	mensagem += 'PERÍODO: '+data_inicio+' até '+data_fim+'\n';
				if (motivo != "")
					mensagem += 'MOTIVO: '+motivo;

				alert(mensagem);
				
				if (id_inconsistencia > 0) {
					document.getElementById("sel_inconsistencia").value = id_inconsistencia;
					ajustaInconsistencia(false);
				}
				
				bRet = false;
			}
            finalizaTempoProcessamento(ref_log_isento);
        }, async: false});
         
        return bRet;
    }

    function validaCadastroMarcaProcesso() {
        var sel_marca_processo = document.getElementById("sel_marca_processo");
    	if (document.getElementById("div_marca").innerHTML == "&nbsp;" &&
    		!(sel_marca_processo.value > 0)) {
        	alert('Cadastro não encontrado, selecione a MARCA!');
        	sel_marca_processo.focus();
        	return false;
    	}
    	return true;
    }                             
    function validaCadastroEspecieProcesso() {
        var sel_especie_processo = document.getElementById("sel_especie_processo");
    	if (document.getElementById("div_especie").innerHTML == "&nbsp;" &&
    		!(sel_especie_processo.value > 0)) {
        	alert('Cadastro não encontrado, selecione a ESPÉCIE!');
        	sel_especie_processo.focus();
        	return false;
    	}
    	return true;
    }     
                            
    function validaCadastroUfProcesso() {
        var sel_uf_processo = document.getElementById("sel_uf_processo");
    	if (document.getElementById("div_uf").innerHTML == "&nbsp;" &&
    		(sel_uf_processo.value == '')) {
        	alert('Cadastro não encontrado, selecione a UF!');
        	sel_uf_processo.focus();
        	return false;
    	}
    	return true;
    }                             

    function validaConsistencia() {
        var txt_inconsistencia = document.getElementById("txt_inconsistencia");
        var txt_placa = document.getElementById("txt_placa");
        var sel_marca_processo = document.getElementById("sel_marca_processo");
        var sel_especie_processo = document.getElementById("sel_especie_processo");
        var sel_uf_processo = document.getElementById("sel_uf_processo");
        var div_id_enquadramento = document.getElementById("div_id_enquadramento");
        var div_data = document.getElementById("div_data");
        var area_pista = document.getElementById("area_pista");

        if (txt_inconsistencia.value != '' && txt_inconsistencia.value > 0) { 
            alert('Existe uma inconsistência selecionada!');
            txt_inconsistencia.focus();
            txt_inconsistencia.select();
            return false;
        }

        if (txt_inconsistencia.value != 0 || txt_inconsistencia.value == '') { 
            alert('Marque a consistência!');
            txt_inconsistencia.focus();
            txt_inconsistencia.select();
            return false;
        }
        
        if (txt_placa && !validaPlaca(txt_placa.value)) { 
            txt_placa.focus();
            txt_placa.select();
            return false;
        }

        if ("${com_obliteracao}" == "true" && "${confirma_obliteracao}" == "true" ) //com_obliteracao == true
            if (!confirmaObliteracao())
                return false;
        
        if (sel_marca_processo && "${com_marca_processo}" == "true") //com_marca_processo == true
        	if (!validaCadastroMarcaProcesso())
            	return false;
        
        if (sel_especie_processo && "${com_especie_processo}" == "true") //com_especie_processo == true
        	if (!validaCadastroEspecieProcesso())
            	return false;

    	if (sel_uf_processo && "${com_uf_validacao}" == "true") //com_uf_processo == true
        	if (!validaCadastroUfProcesso())
            	return false;

    	if (!verificaAlertaVelocidade())
        	return false;
    	
    	if (txt_placa && "${alerta_isento}" == "true") { 
    		if (!verificaAlertaIsento(txt_placa.value, div_id_enquadramento.innerHTML, div_data.innerHTML, area_pista.value)) {
    			txt_placa.select();
        		return false;
    		}
    	}
        
        return true;
    }

    function validaInconsistencia() {
        var txt_inconsistencia = document.getElementById("txt_inconsistencia");
        var txt_placa = document.getElementById("txt_placa");

        if (!(txt_inconsistencia.value > 0)) { 
            alert('Selecione uma inconsistencia!');
            txt_inconsistencia.focus();
            txt_inconsistencia.select();
            return false;
        }
        if (txt_placa && txt_placa.value.length > 0) { 
            alert('Retire a placa!');
            txt_placa.focus();
            txt_placa.select();
            return false;
        }

		
        
        return true;
    }

	function ajustaInconsistencia(porCodigo) {
		var txt_inconsistencia = document.getElementById("txt_inconsistencia");
		var sel_inconsistencia = document.getElementById("sel_inconsistencia");
		if (porCodigo) {
			sel_inconsistencia.selectedIndex = -1; // não deixar nenhuma inconsistencia selecionada

			for (var i=0;i<sel_inconsistencia.options.length;i++) {
				if (sel_inconsistencia.options[i].value == txt_inconsistencia.value)
					sel_inconsistencia.selectedIndex = i;
			}
		}
		else {
			txt_inconsistencia.value = sel_inconsistencia.value;
		}
		realcaBotao();
	}
	
	function realcaBotao() {
        var txt_inconsistencia = document.getElementById("txt_inconsistencia");
        var bt_consistir = document.getElementById("bt_consistir");
        var bt_inconsistir = document.getElementById("bt_inconsistir");

        if (txt_inconsistencia.value == "0") {
            bt_inconsistir.style.fontWeight = "normal";
            bt_consistir.style.fontWeight = "bold";
        }
        else if (txt_inconsistencia.value > 0) {
            bt_inconsistir.style.fontWeight = "bold";
            bt_consistir.style.fontWeight = "normal";
        }
        else {
            bt_inconsistir.style.fontWeight = "normal";
            bt_consistir.style.fontWeight = "normal";
        }
        
	}
	
	function confirmaCod(e) {
		if ($('.blockUI').length > 0)
			return false;
		var ret = false;
		var txt_inconsistencia = document.getElementById("txt_inconsistencia");
		var txt_placa = document.getElementById("txt_placa");
		if (e.ctrlKey && (e.keyCode == 13)) {
            selecionaImagem();
        }
		else if (e.keyCode == 13) {
			if (txt_inconsistencia.value == "0")
				consistir();
			else
				inconsistir();
		}
		else if (e.ctrlKey && (e.keyCode == 37)) {
            anterior();
        }
		else if (e.ctrlKey && (e.keyCode == 39)) {
            proximo();
        }
        else if (e.ctrlKey && String.fromCharCode(e.charCode) == "e") {
            if (escondidos)
                mostraElementos();
            else
                escondeElementos();
        }
		else if (e.ctrlKey && String.fromCharCode(e.charCode) == ",") {
            proximaListaImagens();
        }
		else if (e.ctrlKey && String.fromCharCode(e.charCode) == ".") {
            anteriorListaImagens();
        }
		else if (e.ctrlKey && /[1-9]/.test(String.fromCharCode(e.charCode))) {
            selecaoListaImagens(String.fromCharCode(e.charCode));
        }
		else if (e.target == txt_inconsistencia && /[a-zA-Z]/.test(String.fromCharCode(e.charCode))) {
			if (txt_placa) {
				txt_placa.focus();
				txt_placa.value = String.fromCharCode(e.charCode);
				txt_inconsistencia.value = 0;
				ajustaInconsistencia(true);
			}
        }
		else {
			ret = true; //Como ninguém capturou o evento, então deixa o browser fazer o trabalho dele.
		}
        return ret; //Avisa o browser pra não se meter no evento;
	}
    function escondeElementos() {
    	escondeObliteracao();
        escondidos = true;
    }
    function mostraElementos() {
        mostraObliteracao();
        escondidos = false;
    }
    function gravaObliteracao() {
        var txt_inconsistencia = document.getElementById("txt_inconsistencia");
        var txt_placa = document.getElementById("txt_placa");
        
        if (posIniMouseObliteracao == null)
            return;
        
        if (txt_placa)
            txt_placa.focus();
        else
            txt_inconsistencia.focus();

    	var div_obliteracao = document.getElementById("div_obliteracao");
        var img_veiculo = document.getElementById("img_veiculo");
        var obliteracao_x = document.getElementById("obliteracao_x");
        var obliteracao_y = document.getElementById("obliteracao_y");
        var obliteracao_largura = document.getElementById("obliteracao_largura");
        var obliteracao_altura = document.getElementById("obliteracao_altura");

        var x = parseInt(div_obliteracao.style.left.substring(0, div_obliteracao.style.left.length-2));
        var y = parseInt(div_obliteracao.style.top.substring(0, div_obliteracao.style.top.length-2));
        var largura = div_obliteracao.style.width.substring(0, div_obliteracao.style.width.length-2);
        var altura = div_obliteracao.style.height.substring(0, div_obliteracao.style.height.length-2);

        if (largura > 0 && altura > 0) {
            
			if (y < ${tamanho_tarja}) { //Ajustando obliteração para não sobrepor a tarja.
				altura = altura - (${tamanho_tarja} - y);
				altura = altura < 0 ? 0 : altura;
				y = ${tamanho_tarja};
			}

            obliteracao_x.value = x;
            obliteracao_y.value = y;
            obliteracao_largura.value = largura; 
            obliteracao_altura.value = altura;
        }
        else {
            obliteracao_x.value = "";
	        obliteracao_y.value = "";
	        obliteracao_largura.value = ""; 
	        obliteracao_altura.value = "";
        } 
        
        posIniMouseObliteracao = null;
    }
    //Chamada automáticamente pelo onUnLoad do cabeçalho.
    function aoFechar() {
    	salvarTemposProcessamento();
    }
    //Chamada automáticamente pelo onLoad do cabeçalho.
    function aoAbrir() {
    	atual();
    }
</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="right" width="20%" valign="top">
			<div id="div_miniaturas"></div>
		</td>
		<td align="center" width="60%">
			<table class="tabela_lista" width="1000">
				<tr>
					<td class="label_campo" width="5%">Local:</td>
					<td class="visualiza_campo" width="45%" colspan="2"><div id="div_local"></div></td>
                    <td class="label_campo" width="34%">Placa</td>
					<td class="label_campo" width="8%">Pista:</td>
                    <td class="visualiza_campo" width="8%"><div id="div_pista"></div></td>
				</tr>
                <tr>
					<td class="visualiza_campo" colspan="3" rowspan="16">
	                    <div style="position: relative;">
	                        <div id="div_obliteracao" style="background-color: black; position: absolute;"
	                            onmousedown="mouseObliteracao(event,'down')"
	                            onmouseup="mouseObliteracao(event,'up')"
	                            onmousemove="mouseObliteracao(event,'move')"
	                            onmouseover="escondeObliteracao()"
	                            onmouseout="mostraObliteracao()"
	                        ></div>
	                        <img id="img_veiculo" width="640" height="480" 
	                            onmousedown="mouseObliteracao(event,'down')"
	                            onmouseup="mouseObliteracao(event,'up')"
	                            onmousemove="mouseObliteracao(event,'move')"
	                        >
	                    </div>
                    </td>
					<td class="visualiza_campo"><div id="div_placa" style="font-size: large"></div></td>
					<td class="label_campo" rowspan="9" colspan="2">
						<img id="img_croqui_pista" width="180" height="240" style="border: #000000 1px solid;">
                    </td>
				</tr>
				<tr>
					<td class="visualiza_campo"></td>
				</tr>
				<tr>
					<td class="label_campo">Infração</td>
				</tr>
				<tr>
					<td class="visualiza_campo"><div id="div_infracao"></div></td>
				</tr>
				<tr>
					<td class="label_campo">Data Infração</td>
				</tr>
				<tr>
					<td class="visualiza_campo"><div id="div_data"></div></td>
				</tr>
				<tr>
					<td class="label_campo">Enquadramento</td>
				</tr>
				<tr>
					<td class="visualiza_campo"><div id="div_id_enquadramento"></div></td>
				</tr>
				<tr>
					<td class="label_campo">Descrição Enquadramento</td>
				</tr>
				<tr>
					<td class="visualiza_campo" colspan="3"><div id="div_descricao_enquadramento"></div></td>
                </tr>
				<tr>
				    <td class="visualiza_campo" colspan="3"><div id="div_tit_info_espec" style="font-size: medium;"></div></td>
				</tr>
				<tr>
				    <td class="visualiza_campo" colspan="3"><div id="div_info_espec_1"></div></td>
				</tr>
				<tr>
				    <td class="visualiza_campo" colspan="3"><div id="div_info_espec_2"></div></td>
				</tr>
				<tr>
				    <td class="visualiza_campo" colspan="3"><div id="div_info_espec_3"></div></td>
				</tr>
				<tr>
				    <td class="visualiza_campo" colspan="3"><div id="div_info_espec_4"></div></td>
				</tr>				
				<tr>
				    <td class="visualiza_campo" colspan="3"><div id="div_info_espec_5"></div></td>
				</tr>				
                <tr>
                    <td colspan="3">
                        <div id="div_lista_imagens" style="position: relative;"></div>
                    </td>
		            <td align="center" colspan="2">
		                <button onclick="espera();">Colocar na Espera</button>
		            </td>
                    <td align="center">
						<c:if test="${showInfo==1}">
        	            	<a class='link_td' href="javascript:mostraDetalhes()">[+ info]</a>
						</c:if>
                    </td>
	           	</tr>
			</table>
			<table class="tabela_lista" width="900">
				<tr>
					<td class="label_campo" colspan="2" width="40%">Marca</td>
					<td class="label_campo" width="10%">Cor</td>
					<td class="label_campo" width="10%">Ano</td>
					<td class="label_campo" width="30%">Espécie</td>
					<td class="label_campo" width="10%">UF</td>
				</tr>
				<tr>
					<td class="visualiza_campo" colspan="2"><div id="div_marca"></div></td>
					<td class="visualiza_campo"><div id="div_cor"></div></td>
					<td class="visualiza_campo"><div id="div_ano"></div></td>
					<td class="visualiza_campo"><div id="div_especie"></div></td>
					<td class="visualiza_campo"><div id="div_uf"></div></td>
				</tr>
				<tr>
					<td class="label_campo">Tipo</td>
					<td class="label_campo">Categoria</td>
					<td class="label_campo" colspan="2">Situação</td>
					<td class="label_campo">Localidade</td>
					<td class="label_campo">Atualização</td>
				</tr>
				<tr>
					<td class="visualiza_campo"><div id="div_tipo"></div></td>
					<td class="visualiza_campo"><div id="div_categoria"></div></td>
					<td class="visualiza_campo" colspan="2"><div id="div_situacao"></div></td>
					<td class="visualiza_campo"><div id="div_localidade"></div></td>
					<td class="visualiza_campo"><div id="div_atualizacao"></div></td>
				</tr>
                <jsp:include page="${ent_dados}"></jsp:include>
			</table>
		</td>
		<td align="left">
		</td>
	</tr>
</table>
<table class="tabela_branca" align="center" width="100%">
	<tr>
       	<td width="100%" align="center">
        	<button onclick="carregaInfracao(0);">Sair</button>
      	</td>
    </tr>
</table>
<input type="hidden" name="id_imagem" id="id_imagem" value="${id_imagem}">
<input type="hidden" name="equipamento_captura_frontal" id="com_captura_frontal" value="${com_captura_frontal}">
<input type="hidden" name="placa_cadastro" id="placa_cadastro" value="">
<c:if test="${com_obliteracao}">
	<input type="hidden" name="obliteracao_x" id="obliteracao_x" value="${obliteracao_x}">
	<input type="hidden" name="obliteracao_y" id="obliteracao_y" value="${obliteracao_y}">
	<input type="hidden" name="obliteracao_largura" id="obliteracao_largura" value="${obliteracao_largura}">
	<input type="hidden" name="obliteracao_altura" id="obliteracao_altura" value="${obliteracao_altura}">
</c:if>
<input type="hidden" name="area_pista" id="area_pista">
<script type="text/javascript">
	document.addEventListener("draggesture", function(event){event.preventDefault();},true);
	document.addEventListener("mouseup", function(event){mouseObliteracao(event,'up')},true);
	document.addEventListener("mousemove", function(event){mouseObliteracao(event,'move')},true);
	//Cria 'flusher' do log de tempo.
	setInterval('salvarTemposProcessamento();', 5000);
</script>
<%@ include file="/includes/rodape.jsp" %>
