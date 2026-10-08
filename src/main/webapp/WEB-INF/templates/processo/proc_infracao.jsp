<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.consilux.model.Processamento"%>
<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<%@page import="com.consilux.model.Inconsistencia"%>
<%@page import="java.util.List"%>
<%
	Boolean comObliteracao = ConfiguracaoProvider.getInstance().getComObliteracao();
	Boolean comAjusteImagem = ConfiguracaoProvider.getInstance().getComAjusteImagem();
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
<c:set var="com_ajuste_imagem" value="<%=comAjusteImagem%>" scope="session"/>
<c:set var="confirma_obliteracao" value="<%=confirmaObliteracao%>" scope="request"/>
<c:set var="com_marca_processo" value="<%=comMarcaProcesso%>" scope="request"/>
<c:set var="com_especie_processo" value="<%=comEspecieProcesso%>" scope="request"/>
<c:set var="com_uf_validacao" value="<%=comUfValidacao%>" scope="request"/>
<c:set var="tamanho_tarja" value="<%=tamanhoTarja%>" scope="request"/>
<c:set var="alerta_velocidade" value="<%=alertaVelocidade%>" scope="request"/>
<c:set var="alerta_isento" value="<%=alertaIsento%>" scope="request"/>
<link REL=StyleSheet HREF="/css/jquery-ui.css" TYPE="text/css">
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/multiplas_imagens.js"></script>
<script type="text/javascript" src="/js/obliteracao.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="/GtwClientLogger/GtwClientLogger.nocache.js"></script>
<script type="text/javascript" src="/js/pixastic.min.js"></script>
<script type="text/javascript" src="/js/ajustabrilho.js"></script>
<script type="text/javascript" src="/js/brilhoimagem.js"></script>
<script type="text/javascript" src="/js/CsxRemoteObject.js"></script>
<script type="text/javascript" src="/js/imagem_perfil.js"></script>
<script type="text/javascript" src="/zoom-master/jquery.zoom.js"></script>

<script type="text/javascript">
	var div_ajuste = false;
	var escondidos = false;
	tamanhoTarja = ${tamanho_tarja};
	
	function consistir() {
		consistir(0);
	}
	
	function consistir(origem) {
		
		if (origem == 0) {
			alert ('consistir chamado de outro lugar');
			return false;
		}
		
// 		if (!obrigaObliteracaoMinima()){
// 			return false;	
// 		}
		
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
        
        if (!validaConsistencia()){
          return false;
        }
        
        var div_zoom = document.getElementById("div_zoom");
	     if (div_zoom && div_zoom.style.display != "none") { //XXXXX
	     	mostraDIVImagemPerfil1('div_imagem');
	     	
	     	var img_veiculo_zoom = document.getElementById("img_veiculo_zoom");
	     	img_veiculo_zoom.src = "";
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
		
		$.blockUI({ message: '<h1 id="blocked"><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false, fadeTime: 0, fadeIn: 0});
    	var ref_processar = iniciaTempoProcessamento(id_infracao,0,'PROCESSAR [ID_INCONSISTENCIA:0|ID_PROCESSO:${id_processo}]');
        tempo = finalizaTempoProcessamento(ref_log_processamento);
		params["tempo"] = tempo; 
		$.get('/ajax/processamento/Processar', params, function(xml){
				var idInfracao = $("ID_INFRACAO",xml).text();
				var idInfracaoProcesso = $("ID_INFRACAO_PROCESSO",xml).text();
				if (trataRetorno(xml)) {

			        enviaObliteracao(idInfracaoProcesso);
					
					// XXX: X2: Envia Ajuste
					if ("${com_ajuste_imagem}" == "true") {
					enviaImagemAjuste(o("txt_brilho").value, o("txt_contraste").value);
					o("txt_brilho").value = "0";
					criar_slider("brilho", 150, 1.0);
					o("txt_contraste").value = "0";
					criar_slider("contraste", 3, 0.1);
					CancelaAjuste();
					}
					
					carregaInfracao(idInfracao);
				}
				
				finalizaTempoProcessamento(ref_processar);
		        		        
                $.unblockUI({fadeTime: 0, fadeOut: 0});
		});

		
		return false;
	}
	
	function inconsistir() {
        var div_infracao = document.getElementById("div_infracao");
        var id_infracao = div_infracao.innerHTML;
        var sel_inconsistencia = document.getElementById("sel_inconsistencia");
		var txt_inconsistencia = document.getElementById("txt_inconsistencia");
		var sel_inconsistencia_d = document.getElementById("sel_inconsistencia_drag");
		if (document.getElementById("txt_placa")) {
            var txt_placa = document.getElementById("txt_placa");
		}

		if (!sel_inconsistencia_d)
			ajustaInconsistencia(false);

		if (!validaInconsistencia()){
		  return false;
		}
        
		var div_zoom = document.getElementById("div_zoom");
	     if (div_zoom && div_zoom.style.display != "none") { //XXXXX
	     	mostraDIVImagemPerfil1('div_imagem');
	     	
	     	var img_veiculo_zoom = document.getElementById("img_veiculo_zoom");
	     	img_veiculo_zoom.src = "";
	     }
		
		$.blockUI({ message: '<h1 id="blocked"><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false, fadeTime: 0, fadeIn: 0});
    	var ref_processar = iniciaTempoProcessamento(id_infracao,0,'PROCESSAR [ID_INCONSISTENCIA:'+sel_inconsistencia.value+'|ID_PROCESSO:${id_processo}]');
        tempo = finalizaTempoProcessamento(ref_log_processamento);
        
		var params = {
                acao: <%=Processamento.Acao.INCONSISTE.ordinal()%>,
                id_processo: ${id_processo},
                id_inconsistencia: txt_inconsistencia.value,
                infracao: id_infracao,
                tempo: tempo,
            };
		
		if (txt_placa) {
            params["placa"] = txt_placa.value;
		}
        
		$.get('/ajax/processamento/Processar', params, function(xml){
				var idInfracao = $("ID_INFRACAO",xml).text();
				
				if (trataRetorno(xml)) { 
					
					// XXX: X2: Cancela Ajuste
					if ("${com_ajuste_imagem}" == "true") {
					o("txt_brilho").value = "0";
					criar_slider("brilho", 150, 1.0);
					o("txt_contraste").value = "0";
					criar_slider("contraste", 3, 0.1);
					CancelaAjuste();
					}
					
				    carregaInfracao(idInfracao);
				}
				
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
			var idInfracao = $("ID_INFRACAO",xml).text();
			if (trataRetorno(xml)) 
			    carregaInfracao(idInfracao);
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
			var idInfracao = $("ID_INFRACAO",xml).text();
            if (idInfracao > 0) {
            	
            	// XXX: X2: Cancela Ajuste
				if ("${com_ajuste_imagem}" == "true") {
				o("txt_brilho").value = "0";
				criar_slider("brilho", 150, 1.0);
				o("txt_contraste").value = "0";
				criar_slider("contraste", 3, 0.1);
				CancelaAjuste();
				}
            	
                carregaInfracao(idInfracao);
            }
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
            var idInfracao = $("ID_INFRACAO",xml).text();
            if (idInfracao > 0) {
            	
            	// XXX: X2: Cancela Ajuste
				if ("${com_ajuste_imagem}" == "true") {
				o("txt_brilho").value = "0";
				criar_slider("brilho", 150, 1.0);
				o("txt_contraste").value = "0";
				criar_slider("contraste", 3, 0.1);
				CancelaAjuste();
				}
            	
                carregaInfracao(idInfracao);
            }
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
            var idInfracao = $("ID_INFRACAO",xml).text();
            if (trataRetorno(xml))
                carregaInfracao(idInfracao);
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
				
			if($("COM_VIDEO",xml).text() == "0") {
				var link_video = document.getElementById("link_video");
				link_video.style = "pointer-events: none; cursor: default; color: gray;";
			}	
			
			document.getElementById("video_type").value = $("COM_VIDEO_ALT",xml).text();
				
			document.getElementById("div_local").innerHTML = $("LOCAL",xml).text()+" - "+$("DESCLOCAL",xml).text();
			document.getElementById("div_pista").innerHTML = $("PISTA",xml).text();
			document.getElementById("div_placa").innerHTML = $("PLACA",xml).text();
			document.getElementById("div_infracao").innerHTML = $("ID_INFRACAO",xml).text();
			document.getElementById("div_velocidade").innerHTML = $("VELOCIDADE",xml).text();
			document.getElementById("div_velocidade_regul").innerHTML = $("VELOCIDADE_LIMITE",xml).text();
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
			if (document.getElementById("txt_inconsistencia"))
				document.getElementById("txt_inconsistencia").value = $("INCONSISTENCIA",xml).text();
			
			document.getElementById("com_captura_frontal").value = $("EQUIPAMENTO_CAPTURA_FRONTAL",xml).text();
			document.getElementById("area_pista").value = $("AREA_PISTA",xml).text();

	        if (document.getElementById("txt_placa")) {
	            document.getElementById("txt_placa").value = $("PLACA",xml).text();
	        }
	        if (document.getElementById("txt_placa_p1")) {
	        	document.getElementById("txt_placa_p1").value = $("PLACA_P1",xml).text();
	        }
	        if (document.getElementById("txt_placa_p2")) {
	        	document.getElementById("txt_placa_p2").value = $("PLACA_P2",xml).text();
	        }
			if (document.getElementById("placa_cadastro")) {
				document.getElementById("placa_cadastro").value = "";
			}

	        if (document.getElementById("txt_placa")) {
	            buscaCadastro(document.getElementById("txt_placa").value);
	        }

            if (document.getElementById("txt_placa_p2")) {
            	document.getElementById("txt_placa_p2").focus();
            }
            else if (document.getElementById("txt_placa") && $('#txt_placa').is(':visible') == true && 
           			(document.getElementById("txt_placa").value == '' ||
               	 document.getElementById("txt_inconsistencia").value != '')
               ) {
               document.getElementById("txt_placa").focus();   
               document.getElementById("txt_placa").select();
            }
//             else if (document.getElementById("sel_inconsistencia_drag")) {
//             	document.getElementById("sel_inconsistencia_drag").focus();
//             }
            else {
                document.getElementById("txt_inconsistencia").focus();
                document.getElementById("txt_inconsistencia").select();
            }
             
            carregaImagemPerfil($("INFRACAO",xml).text(), $("ID_IMAGEM_OBJ",xml).text(), 'I', $("ID_VEICULO",xml).text());
            
            /// XXX X1 IMAGEM AJUSTE
//             CancelaAjuste();
            if ("${com_ajuste_imagem}" == "true") {
            buscaImagemAjuste();
            }
            
            if (document.getElementById("sel_inconsistencia_drag"))
            {
            	ajustaInconsistenciaNovo();
            }
            
            var div_zoom = document.getElementById("div_zoom");
            if (!(div_zoom && div_zoom.style.display != "none")) {
            	buscaObliteracaoInfracao(id_infracao);
            }
            
            carregaInfracaoProcesso($("INFRACAO_PROCESSO",xml).text());
            
            finalizaTempoProcessamento(ref_log_infracao_completa);
            $.unblockUI();
		});
	}
    
    function carregaInfracaoProcesso(idInfracaoProcesso) {

		var sel_inconsistencia_d = document.getElementById("sel_inconsistencia_drag");

        if (document.getElementById("sel_marca_processo"))
        	document.getElementById("sel_marca_processo").value = 0;

       	if (document.getElementById("sel_especie_processo"))
           	document.getElementById("sel_especie_processo").value = 0;

       	if (document.getElementById("sel_uf_processo"))
           	document.getElementById("sel_uf_processo").value = '';

        if (idInfracaoProcesso > 0) {          
        	$.ajax({url: '/ajax/InfoInfracaoProcesso?id_infracao_processo='+idInfracaoProcesso, success: function(xml) {
            	document.getElementById("sel_inconsistencia").value = $("INCONSISTENCIA",xml).text();
            	
            	if (document.getElementById("sel_inconsistencia").value != $("INCONSISTENCIA",xml).text())
            		alert("Motivo de Invalidação '"+ $("INCONSISTENCIA",xml).text()+"' não disponível, avise o supervisor.");
            	
            	if ($("PLACA",xml).text() != "") {
		            if (document.getElementById("txt_placa")) {
		                document.getElementById("txt_placa").value = $("PLACA",xml).text();
		                if (document.getElementById("txt_placa_p1"))
		                	document.getElementById("txt_placa_p1").value = $("PLACA",xml).text().substring(0,3);
		                if (document.getElementById("txt_placa_p2"))
		                	document.getElementById("txt_placa_p2").value = $("PLACA",xml).text().substring(3,7);
		            }
            	}

	            if ("${com_obliteracao}" == "true") //com_obliteracao == true
		            buscaObliteracaoProcesso(idInfracaoProcesso);
                
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
                if(sel_inconsistencia_d) {
                	document.getElementById("txt_inconsistencia").value = $("INCONSISTENCIA",xml).text();
                	ajustaInconsistenciaNovo();
                }
                else {
	            	ajustaInconsistencia(false);
                }
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

		document.getElementById("div_marca").innerHTML = "<b><font color='red'>BUSCANDO CADASTRO...<font><b>";
    	var ref_log_cadastro = iniciaTempoProcessamento(id_infracao,0,'CADASTRO ['+placa+']');
        $.get('/ajax/InfoCadastro', { placa: placa }, function(xml) {

        	var descErro = $("ERRO",xml).text();
        	if (descErro && descErro != "") {
        		document.getElementById("div_marca").innerHTML = "<b><font color='red'>" + descErro + "<font><b>";
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

    function verificaAlertaVelocidade(somenteEnfase) {
        var div_info_espec_1 = document.getElementById("div_info_espec_1");
        
        var velocidade 				= parseInt(document.getElementById("div_velocidade").innerHTML);
        var velocidade_regul 		= parseInt(document.getElementById("div_velocidade_regul").innerHTML);
        
        if(velocidade_regul > 0) {
        	
        	var velocidade_limite = velocidade_regul * 2;
        
        	if (velocidade > velocidade_limite) {
        		if (somenteEnfase) {
        			div_info_espec_1.style.fontSize = 'large';
        			div_info_espec_1.style.color = '#FF0000';
        		}
        		else
            		return confirm("Atenção! Velocidade superior a "+ velocidade_limite +"km/h, prosseguir assim mesmo?");
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
        
        if (txt_placa && ${id_processo} > 1 && !validaPlaca(txt_placa.value)) { 
            txt_placa.focus();
            txt_placa.select();
            return false;
        }
        
        if (sel_marca_processo && "${com_marca_processo}" == "true") //com_marca_processo == true
        	if (!validaCadastroMarcaProcesso())
            	return false;
        
        if (sel_especie_processo && "${com_especie_processo}" == "true") //com_especie_processo == true
        	if (!validaCadastroEspecieProcesso())
            	return false;

    	if (sel_uf_processo && "${com_uf_validacao}" == "true") //com_uf_processo == true
        	if (!validaCadastroUfProcesso())
            	return false;
    	
    	if (txt_placa && ${id_processo} > 1 && "${alerta_isento}" == "true") { 
    		if (!verificaAlertaIsento(txt_placa.value, div_id_enquadramento.innerHTML, div_data.innerHTML, area_pista.value)) {
        		return false;
    		}
    	}
        
        return true;
    }

    function validaInconsistencia() {
        var txt_inconsistencia = document.getElementById("txt_inconsistencia");
        var txt_placa = document.getElementById("txt_placa");
        var txt_categoria = document.getElementById("div_categoria");

        if (!(txt_inconsistencia.value > 0)) { 
            alert('Selecione uma inconsistencia!');
            txt_inconsistencia.focus();
            txt_inconsistencia.select();
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
	
	function ajustaInconsistenciaNovo() {
		var txt_inconsistencia = document.getElementById("txt_inconsistencia");
		var sel_inconsistencia = document.getElementById("sel_inconsistencia_drag");
		var div_inc_at = document.getElementById("div_inc_at");

		sel_inconsistencia.selectedIndex = -1; // não deixar nenhuma inconsistencia selecionada

		var inc = parseInt(txt_inconsistencia.value); 
		
		if (inc > 0)
		{
			for (var i=0;i<sel_inconsistencia.options.length;i++) {
				if (sel_inconsistencia.options[i].value == txt_inconsistencia.value)
				{
					sel_inconsistencia.selectedIndex = i;
					div_inc_at.innerHTML = sel_inconsistencia.options[i].innerHTML; 
				}
			}
		}
		else 
		{
			div_inc_at.innerHTML = "N/D";
		}
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
			mostraImagem(String.fromCharCode(e.charCode)-1);
        }
		else if (e.target == txt_inconsistencia && /[a-zA-Z]/.test(String.fromCharCode(e.charCode))) {
			if (txt_placa) {
				txt_placa.focus();
				txt_inconsistencia.value = 0;
				ajustaInconsistencia(true);
				
				if (e.charCode >= 65 && e.charCode <= 90) {
					txt_placa.value = e.key;
					ret = false;
				}
				else if (e.charCode >= 97 && e.charCode <= 122) {
					charCode = e.charCode - 32;
					txt_placa.value = String.fromCharCode(charCode);
					ret = false;
				}
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
    //Chamada automaticamente pelo onUnLoad do cabeçalho.
    function aoFechar() {
    	salvarTemposProcessamento();
    }
    //Chamada automaticamente pelo onLoad do cabeçalho.
    function aoAbrir() {
    	atual();
    }
    
    /*
    *	Autor     : Ederson Luiz
    *   Propósito : Criar uma tela aparte para controle de brilho e contraste 
    */
    function brilho_contraste(){
    	if(!div_ajuste)
    	{
    		$("#div_ajuste").show("fast");
    		div_ajuste = true;
    	}
    	else
    	{
    		$("#div_ajuste").hide("fast");
    		div_ajuste = false;
    	}
    }
    
    /*
     *	Autor     : Ederson Luiz
     *  Propósito : Facilitar o acesso a objetos DOM dentro do html
     */
    function o(idObj){
    	return document.getElementById(idObj);
    }
     
	/*
	 *	Autor     : Ederson Luiz
	 *  Propósito : Facilitar a criação do objeto slider
	 *	Obs		  : O nome do input e a da div devem ter o mesmo nome com o prefixo diferente
	 *              sendo o sld para a div e o txt para o input
	 */
    function criar_slider(idName, vlRange, vlStep){
    	$(document).ready(function(){$("#sld_"+idName).slider({
    		max: vlRange,
			min: vlRange*-1,
			step: vlStep,
			value: 0,
			slide: function(event, ui){o("txt_"+idName).value = ui.value; CancelaAjuste(); AjustaImagem(); }
		})});
    }
	 
	 /*
	 *	Autor     : Ederson Luiz
	 *  Propósito : Voltar para a imagem original sem as alterações ajuste
	 *				de brilho e contraste
	 */
	function CancelaAjuste() {
		Pixastic.revert(document.getElementById("img_veiculo"));
	}
	
	/*
	 *	Autor     : Ederson Luiz
	 *  Propósito : Ajustar brilho e contraste da imagem 
	 */
	function AjustaImagem() {
		Pixastic.revert(document.getElementById("img_veiculo"));
		Pixastic.process(document.getElementById("img_veiculo"), "brightness", {
			brightness : $("#txt_brilho").val(),
			contrast : $("#txt_contraste").val(),
			legacy : false
		});
	}
	
	/*
	 *	Autor     : Ederson Luiz
	 *  Propósito : Zerar brilho da imagem 
	 */
	function zerar_brilho() {
		 o("txt_brilho").value = "0";
		 CancelaAjuste();
		 criar_slider("brilho", 150, 1.0);
		 AjustaImagem();
	 }
	
	/*
	 *	Autor     : Ederson Luiz
	 *  Propósito : Zerar contraste da imagem 
	 */
	function zerar_contraste() {
		 o("txt_contraste").value = "0";
		 CancelaAjuste();
		 criar_slider("contraste", 3, 0.1);
		 AjustaImagem();
	 }
	
	function recebe_inconsistencia() {

		var txt_placa 			= document.getElementById("txt_placa");
		var txt_placa_p1 		= document.getElementById("txt_placa_p1");
		var txt_placa_p2 		= document.getElementById("txt_placa_p2");
		var txt_inconsistencia 	= document.getElementById("txt_inconsistencia");
		
		if (txt_placa_p2) {
			if (txt_placa_p2.value.length > 2)
				txt_placa.value = txt_placa_p1.value + txt_placa_p2.value;
			else 
				txt_placa.value = "";
		}
		
		if (txt_placa.value.length == 7) {
			buscaCadastro(txt_placa.value);
		}
		
		ajustaInconsistencia(true);
	}
	
	function recebe_digito(e) {
		
		var txt_inconsistencia 	= document.getElementById("txt_inconsistencia");
		
		if (e.keyCode == 13) {
			if (txt_inconsistencia.value == "0")
				consistir();
			else
				inconsistir();
			
			return false;
		} else if (e.ctrlKey && (e.keyCode == 37)) {
            anterior();
            
            return false;
        }
		else if (e.ctrlKey && (e.keyCode == 39)) {
            proximo();
            
            return false;
        }
        else if (e.ctrlKey && String.fromCharCode(e.charCode) == "e") {
        	window.open("/ferramenta/listar_cadastro_veiculo.jsp","Consulta","height=340,width=480");
            
            return false;
        }
        else if (e.ctrlKey && String.fromCharCode(e.charCode) == "E") {
        	window.open("/ferramenta/listar_cadastro_veiculo.jsp","Consulta","height=340,width=480");
            
            return false;
        }
		else if (e.ctrlKey && String.fromCharCode(e.charCode) == ",") {
            proximaListaImagens();
            
            return false;
        }
		else if (e.ctrlKey && String.fromCharCode(e.charCode) == ".") {
            anteriorListaImagens();
            
            return false;
        }
		else if (e.ctrlKey && /[1-9]/.test(String.fromCharCode(e.charCode))) {
			mostraImagem(String.fromCharCode(e.charCode)-1);
			
			return false;
        }
		
		var txt_placa 			= document.getElementById("txt_placa");
		var txt_placa_p1 		= document.getElementById("txt_placa_p1");
		var txt_placa_p2 		= document.getElementById("txt_placa_p2");
		var placa               = "";
		var placa_p1 			= "";
		var placa_p2 			= "";
		var charCode 			= 0;
		var retorno 			= true;
		
		if (e.charCode >= 48 && e.charCode <= 57) {
			
			if (txt_placa_p2) {
				if (txt_placa_p2.value.length < 4)
					placa_p2 = txt_placa_p2.value;
				
				txt_placa_p2.value = placa_p2 + e.key;
				
				retorno = false;
			} else {
				if (txt_placa.value.length == 7) {
					placa = txt_placa.value.substring(0,3);
					txt_placa.value = placa + e.key;
				}
				else if (txt_placa.value.length >= 3 && txt_placa.value.length < 7) 
				{
					placa = txt_placa.value;
					txt_placa.value = placa + e.key;
				}
				retorno = false;
			}
		}
		else if (!e.ctrlKey) {
			
			if (txt_placa_p1) {
				if (txt_placa_p1.value.length < 3)
					placa_p1 = txt_placa_p1.value;
				
				if (e.charCode >= 65 && e.charCode <= 90) {
					txt_placa_p1.value = placa_p1 + e.key;
					retorno = false;
				}
				else if (e.charCode >= 97 && e.charCode <= 122) {
					charCode = e.charCode - 32;
					txt_placa_p1.value = placa_p1 + String.fromCharCode(charCode);
					retorno = false;
				}
			} else {
				if ((e.charCode >= 65 && e.charCode <= 90) || (e.charCode >= 97 && e.charCode <= 122))
				{
					retorno = false;
					if (txt_placa.value.length == 7)
						txt_placa.value = "";
				}
				
				if (txt_placa.value.length < 3 || txt_placa.value.length == 4) {
					placa = txt_placa.value;
					
					if (e.charCode >= 65 && e.charCode <= 90) {
						txt_placa.value = placa + e.key;
						retorno = false;
					}
					else if (e.charCode >= 97 && e.charCode <= 122) {
						charCode = e.charCode - 32;
						txt_placa.value = placa + String.fromCharCode(charCode);
						retorno = false;
					}
				}
			}
		}
		
		if (txt_placa_p2) {
			if (txt_placa_p2.value.length > 0 && txt_placa_p2.value.length < 3) 
				txt_inconsistencia.value = txt_placa_p2.value;
			else 
				txt_inconsistencia.value = 0;
			
			txt_placa_p2.focus();
		}
		
		return retorno;
	}
	
	function inconsistencia_dblclick() 
	{
		var txt_inconsistencia = document.getElementById("txt_inconsistencia");
		var sel_inconsistencia = document.getElementById("sel_inconsistencia_drag");
		
		txt_inconsistencia.value = sel_inconsistencia.value;
		
		inconsistir();
	}
	function verificaAcaoMouse(event) {
		event.preventDefault();
		
		if (event.button == 1 || event.button == 2) {
			consistir(1);
		}
		
		event.preventDefault();
	}
	function recebe_digito_restrito(e) {
		if (e.ctrlKey && (e.keyCode == 37)) {
            anterior();
        }
		else if (e.ctrlKey && (e.keyCode == 39)) {
            proximo();
        }
		else if (e.ctrlKey && String.fromCharCode(e.charCode) == ",") {
            proximaListaImagens();
        }
		else if (e.ctrlKey && String.fromCharCode(e.charCode) == ".") {
            anteriorListaImagens();
        }
		else if (e.ctrlKey && /[1-9]/.test(String.fromCharCode(e.charCode))) {
			mostraImagem(String.fromCharCode(e.charCode)-1);
        }
		return false;
	}
	
	function AbrirTelaConsultaCadastro() {
		window.open("/ferramenta/listar_cadastro_veiculo.jsp","Consulta","height=340,width=480");
		return false;
	}
	
	function mostraVideo(pVideoSel) 
	{
		var video_sel = document.getElementById("video_sel");
		if (video_sel)
		{
			video_sel.value = pVideoSel;
		}
		
		atual();
	}
	
	function downloadInfracao() {
		var id_infracao = document.getElementById("div_infracao").innerHTML;
		var url = "../infracao/DownloadInfracao?id_infracao=" + id_infracao;
		var file_path = url;
		var a = document.createElement('A');
		a.href = file_path;
		a.download = file_path.substr(file_path.lastIndexOf('/') + 1);
		document.body.appendChild(a);
		a.click();
		document.body.removeChild(a);
	}
</script>
<style>
    #link_video {
        display: none;
    }
    #downloadInfracao {
        display: none;
    }
</style>
<table class="tabela_branca" width="100%" oncontextmenu="return false;">
	<tr>
		<td align="right" width="20%" valign="top">
			<div id="div_miniaturas" style="position: relative;"></div>
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
						<div name="div_conteudo" id="div_imagem" style="position: relative;" 
									ondblclick="consistir(1);"
									oncontextmenu="return false;"
		                    		onmousedown="mouseObliteracao(event,'down')"
		                            onmouseup="mouseObliteracao(event,'up'); verificaAcaoMouse(event);"
		                            onmousemove="mouseObliteracao(event,'move')">
		                        <div id="div_obliteracao" style="background-color: black; position: absolute;"
		                            onmousedown="mouseObliteracao(event,'down')"
		                            onmouseup="mouseObliteracao(event,'up')"
		                            onmousemove="mouseObliteracao(event,'move')"
		                        >
		                        </div>
		                        <img id="img_veiculo" width="640" height="480">
	                    </div>
	                    
	                    <div id="div_lista_imagens" style="position: relative;"></div>
	                    
	                    <!-- TODO: FELIPE -->
	                    <div name="div_conteudo" id="div_perfil" style="position: relative; display: none;">
                        	<iframe id="if_perfil" width="640" height="480" style="border: none"></iframe>
                        </div>
                        <div name="div_conteudo" id="div_video" style="position: relative; display: none;">
                        	<iframe id="if_video" width="640" height="480" style="border: none"></iframe>
                        	<div id="div_lista_videos" style="position: relative;">
                      
<!-- XXX:FELIPE: SELECAO DE VIDEO -->
<a style='text-decoration: none;' href='javascript:mostraVideo(1)'>1</a>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a style='text-decoration: none;' href='javascript:mostraVideo(2)'>2</a>
                        	
                        	</div>
                        </div>
                        <div name="div_conteudo" id="div_zoom" 		style="position: relative; display: none;">
                        	<span class='zoom' id='sp_zoom' >
                        		<img id="img_veiculo_zoom" width="640" height="480">
                        	</span>
                        </div>
	                    
	                    <div id="div_ajuste" class="visualiza_campo" style="position: absolute; top:200px; left: 100px; width: 260px; height: 75px;
	                    		display:none; border:1px solid #b5b5b5; padding: 5px; background-color: #eAeAeA">
	                    		
	                    		<div style="float:left; width: 50px; height: 38px;">
	                    			<div>Brilho:</div>
	                    			<input style="width:40px;" type="text" id="txt_brilho" name="txt_brilho" class="campo_texto" maxlength="3"/> 
	                    		</div>
	                    		<div style="float:left; width:130px; height: 38px;">
	                    			<div style="width: 100%; height: 18px"></div>
	                    			<div id="sld_brilho" style="float:left; width:120px;"></div>
	                    		</div>
	                    		<div style="float:left; width:55px; height: 38px;">
	                    			<div style="float:left; width:100%; height: 13px;"></div>
	                    			<button onclick="zerar_brilho();">Restaurar</button>
	                    		</div>
	                    		<script>criar_slider("brilho", 150, 1.0);</script>
	                    		
	                    		<div style="float:left; width: 50px; height: 38px;">
	                    			<div>Contraste:</div>
	                    			<input style="width:40px;" type="text" id="txt_contraste" name="txt_contraste" class="campo_texto" maxlength="3"/>
	                    		</div>
	                    		<div style="float:left; width:130px; height: 38px;">
	                    			<div style="width: 100%; height: 18px"></div>
	                    			<div id="sld_contraste" style="float:left; width:120px;"></div>
	                    		</div>
	                    		<div style="float:left; width:55px; height: 38px;">
	                    			<div style="float:left; width:100%; height: 13px;"></div>
	                    			<button onclick="zerar_contraste();">Restaurar</button>
	                    		</div>
	                    		<script>criar_slider("contraste", 3, 0.1);</script>
							</div>	 
							
							<!-- TODO: FELIPE -->
							<a class="link_td" id="link_imagem" onclick="mostraDIVImagemPerfil('div_imagem')">Imagem</a>&nbsp;&nbsp;
							<a class="link_td" id="link_zoom"   onclick="mostraDIVImagemPerfil('div_zoom')">  Zoom  </a>&nbsp;&nbsp;
							<a class="link_td" id="link_perfil" onclick="mostraDIVImagemPerfil('div_perfil')">Perfil</a>&nbsp;&nbsp;
							<a class="link_td" id="link_video"  onclick="mostraDIVImagemPerfil('div_video')" >Video </a>&nbsp;&nbsp;
							<a id="downloadInfracao" href="#" onclick="downloadInfracao(); return false;">Download</a>
                    </td>
					<td class="visualiza_campo"><div id="div_placa" style="font-size: large"></div></td>
					<td class="label_campo" rowspan="9" colspan="2">
<!-- 						<img id="img_croqui_pista" width="180" height="240" style="border: #000000 1px solid;"> -->

<c:if test="${id_processo == 1}">

	Enquadramento Selecionado: ${sIdEnquadramento} <br/>
	Inconsistência atual: <div id="div_inc_at">N/D</div>

	<select id="sel_inconsistencia_drag" name="inc" size="10" style="width: 400px" onkeydown="return false;" ondblclick="inconsistencia_dblclick();" >
	
		<c:forEach var="inc" items="${inconsistencias}">
			<option value="${inc.idInconsistencia}" style="font-size: 14px;">
				${inc.idInconsistenciaStr} - ${inc.descricao}
			</option>
		</c:forEach>
			
	</select>
	
	<button onclick="anterior();" style="width: 190px">Anterior</button>&nbsp;&nbsp;&nbsp;
	<button onclick="proximo();"  style="width: 190px">Próximo</button>
	
</c:if>

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
                    <td colspan="2">
                        <c:if test="${com_ajuste_imagem == true}">
                        	<button onclick="brilho_contraste();">Brilho / Contraste</button>
                        </c:if>
                    </td>
                    <td colspan="2">
                		<button id="bt_LimparDIVs" onclick="limparDIVs();">Limpar Obliterações</button>
					</td>
                    
		            <td align="center" colspan="3">
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
       		<button onclick="AbrirTelaConsultaCadastro();">Consulta Cadastro</button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        	<button onclick="carregaInfracao(0);">Sair</button>
      	</td> 
    </tr>
</table>
<input type="hidden" name="id_imagem" id="id_imagem" value="${id_imagem}">
<input type="hidden" name="video_sel" id="video_sel" value="1">
<input type="hidden" name="video_type" id="video_type" value="0">
<input type="hidden" name="equipamento_captura_frontal" id="com_captura_frontal" value="${com_captura_frontal}">
<input type="hidden" name="placa_cadastro" id="placa_cadastro" value="">
<input type="hidden" name="area_pista" id="area_pista">
<div id="div_velocidade" 		style="visibility: hidden;"></div>
<div id="div_velocidade_regul"  style="visibility: hidden;"></div>

<script type="text/javascript">
	document.addEventListener("draggesture", function(event){event.preventDefault();},true);
	document.addEventListener("mouseup", function(event){mouseObliteracao(event,'up')},true);
	document.addEventListener("mousemove", function(event){mouseObliteracao(event,'move')},true);
	//Cria 'flusher' do log de tempo.
	setInterval('salvarTemposProcessamento();', 5000);
</script>
<%@ include file="/includes/rodape.jsp" %>
