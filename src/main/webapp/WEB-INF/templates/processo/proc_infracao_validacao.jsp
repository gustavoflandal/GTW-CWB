<%@page import="com.consilux.infra.SessaoConstantes"%>
<%@page import="com.consilux.model.Usuario"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.consilux.model.Processamento"%>
<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<%
	Integer tamanhoTarja = ConfiguracaoProvider.getInstance().getTamanhoTarja();
	Integer alertaVelocidade = ConfiguracaoProvider.getInstance().getAlertaVelocidade();
	Boolean alertaIsento = ConfiguracaoProvider.getInstance().getAlertaIsento();
	
	String sGrupoDesob = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("grupo_desoblitera_imagens");
	Integer iGrupoDesob = Integer.parseInt(sGrupoDesob);
	
	Usuario usu = (Usuario)request.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
	Boolean ehUsuarioDesobliteracao = Usuario.usuarioPertenceAoGrupo(usu.getId(), iGrupoDesob);
%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="com.consilux.infra.ExpValida"%>
<c:set var="tamanho_tarja" value="<%=tamanhoTarja%>" scope="request"/>
<c:set var="alerta_velocidade" value="<%=alertaVelocidade%>" scope="request"/>
<c:set var="alerta_isento" value="<%=alertaIsento%>" scope="request"/>
<c:set var="ehUsuarioDesobliteracao" value="<%=ehUsuarioDesobliteracao%>" scope="request"/>
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

<script type="text/javascript">
	var div_ajuste = false;
	var escondidos = false;
	tamanhoTarja = ${tamanho_tarja};
	var id_imagem_obj = 0;
	

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
 		
		var div_id_remessa = document.getElementById("div_id_remessa");
		var id_remessa = div_id_remessa.innerHTML;
		var div_imagens_amostragem = document.getElementById("div_imagens_amostragem");
		var imagens_amostragem = -1;
		if (div_imagens_amostragem.innerHTML != "N/D")
			imagens_amostragem = parseInt(div_imagens_amostragem.innerHTML);
		
 		var txtobservacao = document.getElementById("txt_obs").value;
        var divobservacao = document.getElementById("txt_obs_alt").value;
        if(txtobservacao == divobservacao)
        	txtobservacao = "";
        
        if (!validaConsistencia()){
          return false;
        }
        
        var params = {
            acao: <%=Processamento.Acao.CONSISTE.ordinal()%>,
            id_processo: ${id_processo},
            id_imagem: id_imagem.value,
            infracao: id_infracao,
            observacao: txtobservacao,
            id_remessa: id_remessa,
            imagens_amostragem: imagens_amostragem
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
			
				var strReprovar = $("REPROVAR",xml).text();
				var idInfracaoProxima = $("ID_INFRACAO",xml).text();
				var idInfracaoProcesso = $("ID_INFRACAO_PROCESSO",xml).text();
				var reprovar = 0;
				
				reprovar = parseInt(strReprovar);
				
				if ( reprovar > 0 ) {
					
		    		if ( confirm("Lote será Reprovado por erro(s) ao sair da Validação. Pressione OK para rever a análise ou CANCELAR para reprovar o Lote.") ) {
		    			carregaInfracao(id_infracao);
		    		}
		    		else {
		    			ReprovarLoteCAV();
		    		}
				} else {
			
					if (trataRetorno(xml)) {
	
						enviaObliteracaoValidacao(idInfracaoProcesso);
						
						carregaInfracao(idInfracaoProxima);
					}
				}
				
		        finalizaTempoProcessamento(ref_processar);
                $.unblockUI({fadeTime: 0, fadeOut: 0});
                
		});
		
		return true;
	}
	
	function inconsistir() {
        var div_infracao = document.getElementById("div_infracao");
        var id_infracao = div_infracao.innerHTML;
        var sel_inconsistencia = document.getElementById("sel_inconsistencia");
		var txt_inconsistencia = document.getElementById("txt_inconsistencia");
		if (document.getElementById("txt_placa"))
            var txt_placa = document.getElementById("txt_placa");
        if (document.getElementById("sel_marca_processo"))
            var sel_marca_processo = document.getElementById("sel_marca_processo");
        if (document.getElementById("sel_especie_processo"))
            var sel_especie_processo = document.getElementById("sel_especie_processo");
        if (document.getElementById("sel_uf_processo"))
            var sel_uf_processo = document.getElementById("sel_uf_processo");
        
		var div_id_remessa = document.getElementById("div_id_remessa");
		var id_remessa = div_id_remessa.innerHTML;
		var div_imagens_amostragem = document.getElementById("div_imagens_amostragem");
		var imagens_amostragem = -1;
		if (div_imagens_amostragem.innerHTML != "N/D")
			imagens_amostragem = parseInt(div_imagens_amostragem.innerHTML);
		
        var txtobservacao = document.getElementById("txt_obs").value;
       	var divobservacao = document.getElementById("txt_obs_alt").value;
        if(txtobservacao == divobservacao)
        	txtobservacao = "";
		
        if(txt_inconsistencia.value != "")
			ajustaInconsistencia(false);

		if (!validaInconsistencia()){
		  return false;
		}
		

		$.blockUI({ message: '<h1 id="blocked"><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false, fadeTime: 0, fadeIn: 0});
    	var ref_processar = iniciaTempoProcessamento(id_infracao,0,'PROCESSAR [ID_INCONSISTENCIA:'+sel_inconsistencia.value+'|ID_PROCESSO:${id_processo}]');
        tempo = finalizaTempoProcessamento(ref_log_processamento);
        
		var params = {
				acao: <%=Processamento.Acao.INCONSISTE.ordinal()%>,
				id_processo: ${id_processo},
                id_inconsistencia: sel_inconsistencia.value,
                observacao: txtobservacao,
                infracao: id_infracao,
                tempo: tempo,
                id_remessa: id_remessa,
                imagens_amostragem: imagens_amostragem,
            };
		
		if (txt_placa)
            params["placa"] = txt_placa.value;
        if (document.getElementById("sel_marca_processo"))
            params["id_marca_processo"] = sel_marca_processo.value;
        if (document.getElementById("sel_especie_processo"))
            params["id_especie_processo"] = sel_especie_processo.value;
        if (document.getElementById("sel_uf_processo"))
            params["uf_processo"] = sel_uf_processo.value;
		
		$.get('/ajax/processamento/Processar', params, function(xml) {
				
				var idInfracaoProxima = $("ID_INFRACAO",xml).text();
				var strReprovar = $("REPROVAR",xml).text();
				var reprovar = 0;
				
				reprovar = parseInt(strReprovar);
				
				if ( reprovar > 0 ) {
				
		    		if ( confirm("Lote será Reprovado por erro(s) ao sair da Validação. Pressione OK para rever a análise ou CANCELAR para reprovar o Lote.") ) {
		    			carregaInfracao(id_infracao);
		    		}
		    		else {
		    			ReprovarLoteCAV();
		    		}
				} else {
				
					if (trataRetorno(xml)) {
					    carregaInfracao(idInfracaoProxima);
					}
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
	// YYYZ
	function carregaImagemObl() {
		limparDIVs();
		
		var div_infracao_des = document.getElementById("div_infracao_des");
		
		if(div_infracao_des)
			div_infracao_des.innerHTML = ''; 
				
		mostraDIVImagemPerfil('div_imagem');
	}
	function acertoObliteracao() {
		limparDIVs();
		
		var div_infracao = document.getElementById("div_infracao");
		var div_infracao_des = document.getElementById("div_infracao_des");
		
		if(div_infracao && div_infracao_des)
			div_infracao_des.innerHTML = div_infracao.innerHTML; 
				
		atual();
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
            if (idInfracao > 0)
                carregaInfracao(idInfracao);
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
            if (idInfracao > 0)
                carregaInfracao(idInfracao);
           	$.unblockUI();
            return;
        });
       	$.unblockUI();
    }
    
    function pendente() {
        var div_infracao = document.getElementById("div_infracao");
        $.blockUI({ message: '<h1><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false });
        $.get('/ajax/processamento/Processar', {
          acao: <%=Processamento.Acao.PENDENTE.ordinal()%>,
          id_processo: ${id_processo},
          infracao: div_infracao.innerHTML
          }, function(xml) {
            var idInfracao = $("ID_INFRACAO",xml).text();
            if (idInfracao > 0)
                carregaInfracao(idInfracao);
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
    
    function carregaInconsistencias(id_infracao) {
    	    	
    	$.get('/ajax/Inconsistencias', { id_infracao: id_infracao }, function(xml) {
    		alert($("inconsistencias",xml).childNodes());
    	});
    }

	function carregaInfracao(id_infracao) { /// xxx
		if (!(id_infracao > 0)) {
			window.location = "/processo/FinalizarProcesso";
			return;
		}

		// TODO: XXX
        $.blockUI({ message: '<h1><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false });
    	var ref_log_infracao_completa = iniciaTempoProcessamento(id_infracao,0,'INFRACAO_COMPLETA [ID_PROCESSO:${id_processo}]');
		$.get('/ajax/InfoInfracaoCompleta', { id_infracao: id_infracao }, function(xml) {
			if (window.iniciaRestanteSessao)
				iniciaRestanteSessao();
			
			if($("COM_VIDEO",xml).text() == "0") {
				var link_video = document.getElementById("link_video");
				link_video.style = "pointer-events: none; cursor: default; color: gray;";
			}	
			
			document.getElementById("div_infracao").innerHTML = $("ID_INFRACAO",xml).text();
			document.getElementById("div_inconsistencia").innerHTML = $("INCONSISTENCIA",xml).text();
			document.getElementById("div_inconsistencia_processo").innerHTML = $("INCONSISTENCIA_PROCESSO",xml).text();
			document.getElementById("div_velocidade").innerHTML = $("VELOCIDADE",xml).text();
			document.getElementById("div_velocidade_regul").innerHTML = $("VELOCIDADE_LIMITE",xml).text();
						
			document.getElementById("div_movimento_lote" 		).innerHTML = $("NUMERO_LOTE"					,xml).text();
			document.getElementById("div_revisao_lote" 			).innerHTML = $("REVISAO_LOTE" 					,xml).text();
			document.getElementById("div_imagens_lote" 			).innerHTML = $("QUANTIDADE_IMAGENS_LOTE"		,xml).text();
			document.getElementById("div_imagens_validaveis" 	).innerHTML = $("QUANTIDADE_IMAGENS_AUDITAVEIS"	,xml).text();
			document.getElementById("div_imagens_amostragem" 	).innerHTML = $("QUANTIDADE_IMAGENS_AMOSTRA"	,xml).text();
			document.getElementById("div_erros_aprovacao" 		).innerHTML = $("QUANTIDADE_ERROS_APROVACAO"	,xml).text();
			document.getElementById("div_erros" 				).innerHTML = $("QUANTIDADE_ERROS"				,xml).text();
			document.getElementById("div_id_remessa" 			).innerHTML = $("ID_REMESSA"		 			,xml).text();
			document.getElementById("div_status" 				).innerHTML = $("STATUS"						,xml).text();
			document.getElementById("div_motivo_inconsistencia"	).innerHTML = $("MOTIVO_INCONSISTENCIA"			,xml).text();
			document.getElementById("div_isencao" 				).innerHTML = $("ISENCAO"						,xml).text();
			document.getElementById("div_isencao_dig" 			).innerHTML = $("ISENCAO"						,xml).text();
			document.getElementById("div_isencao_periodo" 		).innerHTML = $("ISENCAO_PERIODO"				,xml).text();
			document.getElementById("div_isencao_periodo_dig" 	).innerHTML = $("ISENCAO_PERIODO"				,xml).text();
			document.getElementById("div_nome_operador" 		).innerHTML = $("OPERADOR"						,xml).text();
			document.getElementById("div_data_analise" 			).innerHTML = $("DATA_ANALISE"					,xml).text();
			document.getElementById("div_placa"					).innerHTML = $("PLACA_CAI"						,xml).text();
			document.getElementById("txt_placa"                 ).value     = $("PLACA"                         ,xml).text();
			document.getElementById("div_at_marca_id"           ).innerHTML = $("ID_MARCA"                      ,xml).text();
			document.getElementById("div_at_marca" 				).innerHTML = $("ID_MARCA_CAI",xml).text() + ' - ' + $("MARCA_CAI",xml).text();
			document.getElementById("div_id_enquadramento_desc"	).innerHTML = $("ID_ENQUADRAMENTO",xml).text() + " - " + $("DESCRICAO_ENQUADRAMENTO",xml).text();
			document.getElementById("div_id_enquadramento"  	).innerHTML = $("ID_ENQUADRAMENTO",xml).text();
            document.getElementById("div_data"					).innerHTML = $("DATA"							,xml).text();
            document.getElementById("div_local"					).innerHTML = $("LOCAL",xml).text()+" - "+$("DESCLOCAL",xml).text();
            document.getElementById("div_pista"					).innerHTML = $("PISTA"							,xml).text();
            
            document.getElementById("txt_obs"                   ).value = "";
            document.getElementById("txt_obs_alt"               ).value = "";
            document.getElementById("txt_obs"                   ).value = $("OBSERVACAO"                    ,xml).text();
            document.getElementById("txt_obs_alt"               ).value = $("OBSERVACAO"                    ,xml).text();
            
            document.getElementById("div_nome_aud"				).innerHTML = $("NOME_AUDITOR"					,xml).text();
            document.getElementById("div_reg_aud"				).innerHTML = $("REGISTRO_AUDITOR"				,xml).text();
            document.getElementById("div_data_aud"				).innerHTML = $("DATA_AUDITORIA"				,xml).text();
			document.getElementById("div_equipProdam"			).innerHTML = $("CODIGO_EQUIPAMENTO_PRODAM"		,xml).text();
            
			document.getElementById("com_captura_frontal").value = $("EQUIPAMENTO_CAPTURA_FRONTAL",xml).text();
			document.getElementById("area_pista").value = $("AREA_PISTA",xml).text();

			//buscaObliteracaoInfracao(id_infracao);
			limparDIVs();
			
            carregaInfracaoProcesso($("INFRACAO_PROCESSO",xml).text());
            
            var tipo_img = 'I';
            var div_infracao_des = document.getElementById("div_infracao_des");
            if(div_infracao_des) {
            	if(div_infracao_des.innerHTML == $("INFRACAO",xml).text()) {
            		tipo_img = 'ID';
            		var link_desob = document.getElementById("link_desob");
            		if(link_desob)
            		   link_desob.style = "pointer-events: none; cursor: default; color: gray;";
            	}
            	else {
            		div_infracao_des.innerHTML = '';
            		var link_desob = document.getElementById("link_desob");
            		if(link_desob)
            		   link_desob.style = '';
            	}
            }
            
            // TODO: FELIPE: 
            carregaImagemPerfil($("INFRACAO",xml).text(), $("ID_IMAGEM_OBJ",xml).text(), tipo_img, $("ID_VEICULO",xml).text());
            
            var inconsistencia_processo = parseInt($("INCONSISTENCIA_PROCESSO",xml).text());
            if (inconsistencia_processo >= 0) {
            	document.getElementById("lbl_revisao").style.visibility = "visible";
            	document.getElementById("btn_pendente").style.visibility = "visible";
            	ajustaInconsistenciaProcesso();
            }
            else {
            	document.getElementById("lbl_revisao").style.visibility = "hidden";
            	document.getElementById("btn_pendente").style.visibility = "hidden";
            	document.getElementById("sel_valida").selectedIndex = 0;
            	ajustaInconsistencia(true);
            }
            
            document.getElementById("chk_erro_oblit").checked = false;
            document.getElementById("div_erro_oblit").style.visibility = "hidden";
            
            document.getElementById("sel_valida").focus();
            
            finalizaTempoProcessamento(ref_log_infracao_completa);
            $.unblockUI();
            
		});
	}
    
    function carregaInfracaoProcesso(idInfracaoProcesso) {
        ajustaInconsistencia(true);

        if (document.getElementById("sel_marca_processo")){
        	document.getElementById("sel_marca_processo").value = 0;
        	document.getElementById("marca_processo").value = '';
        }
       	if (document.getElementById("sel_especie_processo"))
           	document.getElementById("sel_especie_processo").value = 0;

       	if (document.getElementById("sel_uf_processo"))
           	document.getElementById("sel_uf_processo").value = '';

       	// Agora é o carregaInfracao que busca a placa.
        if (document.getElementById("txt_placa")) {
            buscaCadastro(document.getElementById("txt_placa").value);
        }

        if (idInfracaoProcesso > 0) {          
//         	alert("Entrou no if InfoInfracaoProcesso");
        	$.ajax({url: '/ajax/InfoInfracaoProcesso?id_infracao_processo='+idInfracaoProcesso, success: function(xml) {
            	document.getElementById("sel_inconsistencia").value = $("INCONSISTENCIA",xml).text();
            	
            	if (document.getElementById("sel_inconsistencia").value != $("INCONSISTENCIA",xml).text())
            		alert("Inconsistência '"+ $("INCONSISTENCIA",xml).text()+"' não disponível, avise o supervisor.");
            	
//             	alert("Placa encontrada na consulta InfoInfracaoProcesso: " + $("PLACA",xml).text());
	            if (document.getElementById("txt_placa") && $("PLACA",xml).text().trim() != "") {
	                document.getElementById("txt_placa").value = $("PLACA",xml).text();
	                buscaCadastro(document.getElementById("txt_placa").value);
	            }
                
            }, async: false});
	    }
    }

    function buscaCadastro(placa) {
    	var txt_placa = document.getElementById("txt_placa");
        var div_infracao = document.getElementById("div_infracao");
        var id_infracao = parseInt(div_infracao.innerHTML);
    	var placa_cadastro = document.getElementById("placa_cadastro");
    	
    	var sel_marca_processo = document.getElementById("sel_marca_processo");
    	var sel_especie_processo = document.getElementById("sel_especie_processo");
    	var sel_uf_processo = document.getElementById("sel_uf_processo");
		
    	var div_inconsistencia_processo = document.getElementById("div_inconsistencia_processo");
    	var inconsistencia_processo = -1;
    	
    	if(div_inconsistencia_processo)
    		inconsistencia_processo = parseInt(document.getElementById("div_inconsistencia_processo").innerHTML);
    	
    	document.getElementById("div_marca").innerHTML = "&nbsp;";
        document.getElementById("div_cor").innerHTML = "&nbsp;";
        document.getElementById("div_ano").innerHTML = "&nbsp;";
        document.getElementById("div_especie").innerHTML = "&nbsp;";
        document.getElementById("div_uf").innerHTML = "&nbsp;";
        document.getElementById("div_tipo").innerHTML = "&nbsp;";
        document.getElementById("div_categoria").innerHTML = "&nbsp;";
        document.getElementById("div_localidade").innerHTML = "&nbsp;";
        
        document.getElementById("div_isencao_dig").innerHTML = "&nbsp;";
        document.getElementById("div_isencao_periodo_dig").innerHTML = "&nbsp;";
        
        if (placa.length != 7)
            return;
		
		var id_infracao = document.getElementById("div_infracao").innerHTML;
		
    	var ref_log_cadastro = iniciaTempoProcessamento(id_infracao,0,'CADASTRO ['+placa+']');
        $.get('/ajax/InfoCadastro', { placa: placa, id_infracao: id_infracao }, function(xml) {

        	var descErro = $("ERRO",xml).text();
        	if (descErro && descErro != "") {
        		document.getElementById("div_marca").innerHTML = "<b><font color='red'>" + descErro + "<font><b>";
	        	return;
        	}
        	
//         	alert("Placa encontrada na consulta InfoCadastro: " + $("PLACA",xml).text());
//         	alert("IF - txt_placa: " + txt_placa.value.trim());
//         	alert("IF - PLACA: " + $("PLACA",xml).text());

        	//Se a placa veio direto da infração e o usuário já está com uma placa diferente no txt, 
        	//então ignora, porque o cadastro chegou atrasado.
        	if (txt_placa && ($("PLACA",xml).text() != txt_placa.value)) {
//         		alert("Entrou na validação e saiu do método");
        		return;
        	}
        	document.getElementById("div_marca_valor").innerHTML = $("MARCA",xml).text();
            document.getElementById("div_marca").innerHTML = $("MARCA",xml).text() != "" ? $("MARCA",xml).text() : "<strong>Veículo não constante no Cadastro de São Paulo</strong>"; //Artifício técnico para não alterar o layout da tabela
            document.getElementById("div_cor").innerHTML = $("COR",xml).text();
            document.getElementById("div_ano").innerHTML = $("ANO",xml).text();
            document.getElementById("div_especie").innerHTML = $("ESPECIE",xml).text() != "" ? $("ESPECIE",xml).text() : "&nbsp;"; //Artifício técnico para não alterar o layout da tabela
            document.getElementById("div_uf").innerHTML = $("UF",xml).text() != "" ? $("UF",xml).text() : "&nbsp;"; //Artifício técnico para não alterar o layout da tabela
            document.getElementById("div_tipo").innerHTML = $("TIPO",xml).text() != "" ? $("TIPO",xml).text() : "&nbsp;" //Artifício técnico para não alterar o layout da tabela
            document.getElementById("div_categoria").innerHTML = $("CATEGORIA",xml).text();
            document.getElementById("div_localidade").innerHTML = $("LOCALIDADE",xml).text();
            
            if($("MARCA",xml).text() != "") {
            	document.getElementById("div_isencao_dig").innerHTML = $("ISENCAO",xml).text();
            	document.getElementById("div_isencao_periodo_dig").innerHTML = $("ISENCAO_PERIODO",xml).text();
            }
            
            if (sel_marca_processo) {
                if ($("MARCA",xml).text() == "") {
	                document.getElementById("marca_processo").value = document.getElementById("div_at_marca_id").innerHTML;
	                ajustaMarca(true);
                } else {
                	sel_marca_processo.selectedIndex = 0;
                	ajustaMarca(false);
                }
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
    	if (sel_especie_processo && document.getElementById("div_especie").innerHTML == "&nbsp;" &&
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
        var marcaTxtSel = document.getElementById("sel_marca_processo");
        var marcaTxt = document.getElementById("marca_processo");
        var marca = document.getElementById("div_marca_valor").innerHTML;

        var div_inconsistencia = document.getElementById("div_inconsistencia");
        var div_infracao = document.getElementById("div_infracao");
        var div_infracao_des = document.getElementById("div_infracao_des");
        
        var id_inconsistencia = 0;
        if(div_inconsistencia)
        	id_inconsistencia = parseInt(div_inconsistencia.innerHTML);
        
        if (txt_inconsistencia.value != '' && txt_inconsistencia.value > 0) { 
            alert('Existe uma inconsistência selecionada!');
            txt_inconsistencia.focus();
            txt_inconsistencia.select();
            return false;
        }
     
        if(marca != ""){
        	if (marcaTxt.value !="") {
	        	marcaTxt.value == "";
	        	alert('Marca não pode ser modificada. A marca do veículo já consta no Cadastro São Paulo.');
	        	document.getElementById('marca_processo').value="";
	        	document.getElementById('sel_marca_processo').selectedIndex = 0;
	        	document.getElementById('marca_processo').focus();
	            return false;
	        }
        }

        if(marca =="") {
	        if((marcaTxt.value =="" || marcaTxt.value == "0")) {
	            alert('Favor informar uma Marca válida, pois veículo não se encontra no cadastro São Paulo.');
	        	document.getElementById('marca_processo').value="";
	        	document.getElementById('sel_marca_processo').selectedIndex = 0;
	        	document.getElementById('marca_processo').focus();
	            return false;
	        }
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
        
        if(div_infracao && div_infracao_des) {
        	if(id_inconsistencia > 0 || div_infracao.innerHTML == div_infracao_des.innerHTML) {
        		if (document.getElementById("lbl_revisao") && document.getElementById("lbl_revisao").style.visibility == "hidden") {
	        		if(!obrigaObliteracaoMinima())
		            return false;
        		}
        	}
        }
       	if (!validaCadastroMarcaProcesso())
           	return false;
        
       	if (!validaCadastroEspecieProcesso())
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
        var sel_inconsistencia = document.getElementById("sel_inconsistencia");
        var txt_placa = document.getElementById("txt_placa");
        var marcaTxt = document.getElementById("marca_processo");
        var marca = document.getElementById("div_marca_valor").innerHTML;
        var sel_valida = document.getElementById("sel_valida");

        if (!(txt_inconsistencia.value > 0)) { 
            alert('Selecione uma inconsistencia!');
           	sel_valida.focus();
            return false;
        }    
        
        if(marca != ""){
        	if (marcaTxt.value !="") {
	        	marcaTxt.value == "";
	        	alert('Marca não pode ser modificada. A marca do veículo já consta corretamente no Cadastro São Paulo.');
	        	document.getElementById('marca_processo').value="";
	        	document.getElementById('sel_marca_processo').selectedIndex = 0;
	        	document.getElementById('marca_processo').focus();
	            return false;
	        }
        }

        return true;
    }
    
    // XXXX: FELIPE
    function ajustaMarca(porCodigo) {
    	var marca_processo = document.getElementById("marca_processo");
    	var sel_marca_processo 	= document.getElementById("sel_marca_processo");
    	
    	if(porCodigo) {
    		for(var i=0;i<sel_marca_processo.options.length;i++) {
    			if(sel_marca_processo[i].value == marca_processo.value)
    				sel_marca_processo.selectedIndex = i;
    		}
    	} else {
    		if(sel_marca_processo.selectedIndex == 0)
    			marca_processo.value = '';
    		else 
    			marca_processo.value = sel_marca_processo.value;
    	}
    }

    function ajustaInconsistenciaProcesso() {
    	var txt_inconsistencia = document.getElementById("txt_inconsistencia");
    	var sel_inconsistencia = document.getElementById("sel_inconsistencia");
    	var div_inconsistencia = document.getElementById("div_inconsistencia_processo");
    	var sel_valida = document.getElementById("sel_valida");
    	
    	var inconsistencia = parseInt(div_inconsistencia.innerHTML);
    	
    	txt_inconsistencia.value = inconsistencia;
    	
    	for (var i=0;i<sel_inconsistencia.options.length;i++) {
			if (sel_inconsistencia.options[i].value == txt_inconsistencia.value)
				sel_inconsistencia.selectedIndex = i;
		}
    	
    	if(inconsistencia == 0) {
    		sel_valida.selectedIndex = 1;
    	} else {
    		sel_valida.selectedIndex = 0;
    	}
    }
    
	function ajustaInconsistencia(porCodigo) {
		var txt_inconsistencia = document.getElementById("txt_inconsistencia");
		var sel_inconsistencia = document.getElementById("sel_inconsistencia");
		var div_inconsistencia = document.getElementById("div_inconsistencia");
		var sel_valida = document.getElementById("sel_valida");
		
		var id_inconsistencia = parseInt(div_inconsistencia.innerHTML);
		
		if(porCodigo) {
			if(sel_valida.selectedIndex == 1) {
				txt_inconsistencia.value = 0;
			} else {
				if(id_inconsistencia > 0)
					txt_inconsistencia.value = id_inconsistencia;
				else 
					txt_inconsistencia.value = "";
			}
			for (var i=0;i<sel_inconsistencia.options.length;i++) {
				if (sel_inconsistencia.options[i].value == txt_inconsistencia.value)
					sel_inconsistencia.selectedIndex = i;
				}
			txt_inconsistencia.value = sel_inconsistencia.value;
		}
		else {		
			txt_inconsistencia.value = sel_inconsistencia.value;
			if(parseInt(txt_inconsistencia.value) > 0)
				sel_valida.selectedIndex = 0;
			else
				sel_valida.selectedIndex = 1;
		}
	}
	
	function auditar() {
		var txt_inconsistencia = document.getElementById("txt_inconsistencia");
		if (txt_inconsistencia.value == "0")
			consistir();
		else
			inconsistir();
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
			auditar();
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
    //Chamada automáticamente pelo onUnLoad do cabeçalho.
    function aoFechar() {
    	salvarTemposProcessamento();
    }
    //Chamada automáticamente pelo onLoad do cabeçalho.
    function aoAbrir() {
    	atual();
    }
    
    /*
     *	Autor     : Ederson Luiz
     *  Propósito : Facilitar o acesso a objetos DOM dentro do html
     */
    function o(idObj){
    	return document.getElementById(idObj);
    }
    
    document.addEventListener("draggesture", function(event){event.preventDefault();},true);
	document.addEventListener("mouseup", function(event){mouseObliteracao(event,'up')},true);
	document.addEventListener("mousemove", function(event){mouseObliteracao(event,'move')},true);
	//Cria 'flusher' do log de tempo.
	setInterval('salvarTemposProcessamento();', 5000);

    function verifPlaca(txt) {
        if (txt.value == "<PLACA>") 
            txt.value = "";
        else if (txt.value.length > 0)
            validaPlaca(txt.value)
    }
    
    function verifDigitPlaca(e,txt) {
        txt.value = txt.value.toUpperCase();
        if (txt.value != "<PLACA>" && /[A-Za-z0-9`]/.test(String.fromCharCode(e.keyCode))) //[A-Za-z0-9`] A CRASE É POR CAUSA DO 0-ZERO DIGITADO DO LADO DIREITO QUE O KEYCODE = `
        	buscaCadastro(txt.value);
    }
    
    //Luiz Fernando Amaral 13/01/2015
    //Reprovar o lote setando a coluna reprvado = 1
     function ReprovarLoteCAV() {

 		var idRemessa = document.getElementById("div_id_remessa").innerHTML;
 		
  		var jqxhr = $.get("/remessa/ReprovarRemessa", {
			  acao: "ReprovarMovimento",
			  idRemessa: idRemessa
		}, function(xml){
			var erro = "";
			erro = $("RETORNO",xml).text();
			alert(erro);	
			window.location = "/processo/FinalizarProcesso";	
		});
		
		return;
	} 
     
</script>

<table class="tabela_branca" width="100%">
	<tr>
		<td align="left" width="40%">
			<table>
				<tr>
					<td class="label_campo">Nº do lote:</td>
					<td class="visualiza_campo"><div id="div_movimento_lote"></div></td>
					<td class="label_campo">Revisão:</td>
					<td class="visualiza_campo"><div id="div_revisao_lote"></div></td>
				</tr>
				<tr>
					<td colspan="2" class="label_campo">Nº imagens lote:</td>
					<td class="visualiza_campo"><div id="div_imagens_lote"></div></td>
				</tr>
				<tr>		
					<td colspan="2" class="label_campo">Nº imagens auditáveis:</td>
					<td class="visualiza_campo"><div id="div_imagens_validaveis"></div></td>
				</tr>				
				
				<tr>		
					<td colspan="2" class="label_campo">Nº imagens auditáveis (Amostra):</td>
					<td class="visualiza_campo"><div id="div_imagens_amostragem"></div></td>
				</tr>
				
				<tr></tr>

				<tr>
					<td class="label_campo">Erros para aprovação: </td>
					<td class="visualiza_campo"><div id="div_erros_aprovacao"></div></td>
					<td class="label_campo">Status: </td>
					<td class="visualiza_campo"><div id="div_status"></div></td>
				</tr>
				<tr>
					<td colspan="2" class="label_campo">Inconsistência:</td>
					<td colspan="2" class="visualiza_campo"><div id="div_motivo_inconsistencia"></div></td>
				</tr>
				<tr>
					<td class="label_campo">Isenção: </td>
					<td class="visualiza_campo"><div id="div_isencao"></div></td>
					<td class="label_campo">Período:</td>
					<td class="visualiza_campo"><div id="div_isencao_periodo"></div></td>
				</tr>
				<tr>
					<td colspan="2" class="label_campo">Operador:</td>
					<td colspan="2" class="visualiza_campo"><div id="div_nome_operador"></div></td>
				</tr>
				<tr>
					<td class="label_campo">Data:</td>
					<td class="visualiza_campo"><div id="div_data_analise"></div></td>
				</tr>
				
				<tr>	
					<td class="label_campo">Placa:	</td>
					<td class="visualiza_campo"><div id="div_placa"></div></td>

					<td  class="label_campo">Marca:</td>
					<td  class="visualiza_campo"><div id="div_at_marca"></div></td>
				</tr>
				
				<tr>
					<td class="label_campo" >Modelo: </td>
					<td class="visualiza_campo" colspan="4"><div id="div_marca"></div></td>
					<td class="visualiza_campo" colspan="3"><div id="div_marca_valor" style="visibility: hidden;"></div></td>
				</tr>
				<tr>
					<td class="label_campo" >Cor: </td>
					<td class="visualiza_campo"><div id="div_cor"></div></td>
					
					<td class="label_campo">Localidade: </td>
					<td class="visualiza_campo"><div id="div_localidade"></div></td>

					<td class="label_campo" >Ano: </td>
					<td class="visualiza_campo"><div id="div_ano"></div></td>
				</tr>
				<tr>
					<td class="label_campo">Categoria: </td>
					<td class="visualiza_campo"><div id="div_categoria"></div></td>
					
					<td class="label_campo">Tipo: </td>
					<td class="visualiza_campo"><div id="div_tipo"></div></td>
				</tr>
				
				<tr>
					<td class="label_campo" >Espécie: </td>
					<td class="visualiza_campo"><div id="div_especie"></div></td>

					<td class="label_campo" >UF: </td>
					<td class="visualiza_campo"><div id="div_uf"></div></td>
				</tr>

				<tr>				
					<td colspan="2" class="label_campo">Placa Consulta: </td>
					<td>
		                <input id="txt_placa" type="text" name="placa" class="campo_texto" maxlength="7"  
		                onkeypress="return confirmaCod(event); " onkeyup="verifDigitPlaca(event,this)" 
		                onblur="verifPlaca(this)" style="font-weight: bold"/>
		            </td>
				</tr>
				
				<tr>
					<td colspan="2" class="label_campo" >Marca Consulta:</td>
					<td >
						<select id="sel_marca_processo" name="id_marca_processo"
							onchange="ajustaMarca(false);"
			                onkeypress="return confirmaCod(event); " >
							<option value="0"></option>
							<c:forEach var="marcaCET" items="${marcas_CET}">
								<option value="${marcaCET.id}">${marcaCET.descricao}</option>
							</c:forEach>
						</select>
					</td>
					<c:if test="${marcas_CET != null}">
					<td class="label_campo" >
						<input type="text" id="marca_processo" name="marca_processo"
						onkeyup="ajustaMarca(true);"
					  	maxlength="3" 
						/>
					</td>					
					</c:if>
				</tr>				
				<tr>
					<td class="label_campo">Isenção: </td>
					<td class="visualiza_campo"><div id="div_isencao_dig"></div></td>
					
					<td class="label_campo">Período: </td>
					<td class="visualiza_campo"><div id="div_isencao_periodo_dig"></div></td>
				</tr>				
				<tr>
					<td  class="label_campo">Enquadramento:</td>
					<td colspan="4" class="visualiza_campo"><div id="div_id_enquadramento_desc"></div></td>
				</tr>				
				<tr>
					<td  class="label_campo">Data/Hora:</td>
					<td colspan="3" class="visualiza_campo"><div id="div_data"></div></td>
				</tr>
				<tr>
					<td  class="label_campo">Local:</td>
					<td colspan="3" class="visualiza_campo"><div id="div_local"></div></td>
				</tr>
				<tr>
					<td class="label_campo">Faixa:</td>
					<td class="visualiza_campo"><div id="div_pista"></div></td>
					
					<td colspan="2" class="label_campo">Equipamento CET:</td>
					<td class="visualiza_campo"><div id="div_equipProdam"></div></td>
				</tr>
				<tr>
					<td colspan="2" class="label_campo">Motivo da Validação:</td>
					<td colspan="2" class="label_campo">
						<select id="sel_inconsistencia" name="inc"
			                onchange="ajustaInconsistencia(false)"
			                onkeypress="return confirmaCod(event); " >
							<c:forEach var="inc" items="${inconsistencias}">
								<option value="${inc.idInconsistencia}">${inc.descricao}</option>
							</c:forEach>
							<option value="">--selecione--</option>
						</select>
					</td>
				</tr>
				
				<tr>
					<td colspan="2" class="label_campo">Observação:</td>
					<td colspan="2" class="label_campo">
		                <input id="txt_obs" type="text" name="txt_obs" class="campo_texto"  />
		            </td>
				
				<tr>	
					<td colspan="2" class="label_campo">Decisão do auditor:	</td>
					<td> 
		            	<select id="sel_valida" name="sel_valida" 
		            		onchange="ajustaInconsistencia(true)" 
		            		onkeyup="ajustaInconsistencia(true); return confirmaCod(event); " 
		            		onkeypress="return confirmaCod(event); ">
		            		<option value="0">Inválida</option>
		            		<option value="1">Válida</option>
		            	</select>
						<input id="txt_inconsistencia" type="hidden" name="id_inconsistencia" class="campo_texto" maxlength="2" />
					</td> 
					<td><label id="lbl_revisao" class="valor_campo" style="color: red; visibility: hidden;">(Revisão)</label></td>
				</tr>
				<tr>
					<td colspan="2" class="label_campo">Nome do auditor:</td>
					<td colspan="2" class="visualiza_campo"><div id="div_nome_aud"></div></td>
				</tr>
				<tr>
					<td colspan="2" class="label_campo">Registro do auditor:</td>
					<td colspan="2" class="visualiza_campo"><div id="div_reg_aud"></div></td>
				</tr>
				<tr>
					<td colspan="2" class="label_campo">Data da auditoria:</td>
					<td colspan="2" class="visualiza_campo"><div id="div_data_aud"></div></td>
				</tr>
				<tr>
					<td colspan="3">
						<button id="bt_auditar" onclick="return auditar();" style="width: 50%">Auditar</button>
					</td>
					<td colspan="3">
						<button id="link_acerto" onclick="acertoObliteracao();">Acerto de Obliteração</button>
					</td>
				</tr>
				<tr>
				    <td colspan="3">
				    <button onclick="anterior();">Anterior</button>
				    <button onclick="proximo();">Próximo</button>
				    <button id="btn_pendente" style="visibility: hidden;" onclick="pendente();">Último</button>
				    </td>
					<td colspan="3" align="center">
					<button onclick="carregaInfracao(0);">Sair</button>
        			<button  onclick="espera();">Espera</button>
        			</td>
				</tr>
								
			</table>
		</td>
		
		<td class="visualiza_campo" colspan="3" rowspan="16">	                    
			<div name="div_conteudo" id="div_imagem" style="position: relative;" 
            		onmousedown="mouseObliteracao(event,'down')"
                    onmouseup="mouseObliteracao(event,'up')"
                    onmousemove="mouseObliteracao(event,'move')">
                <div id="div_obliteracao" style="background-color: black; position: absolute;"
                    onmousedown="mouseObliteracao(event,'down')"
                    onmouseup="mouseObliteracao(event,'up')"
                    onmousemove="mouseObliteracao(event,'move')"
                    onmouseover="escondeObliteracao()"
                    onmouseout="mostraObliteracao()"></div>
                
                <img id="img_veiculo" width="640" height="480">
				<div id="div_lista_imagens" style="position: relative;"></div>
            </div>
	                    
            <!-- TODO: FELIPE -->
            <div name="div_conteudo" id="div_video" style="position: relative; display: none;">
            	<iframe id="if_video" width="640" height="480" style="border: none"></iframe>
            </div>
            <div name="div_conteudo" id="div_imagem_des" style="position: relative; display: none;">
            	<img id="img_veiculo_des" width="640" height="480">
            </div>
            
            <!-- TODO: FELIPE -->
				<a class="link_td" id="link_imagem" onclick="mostraDIVImagemPerfil('div_imagem');">Imagem</a>&nbsp;&nbsp;
				<a class="link_td" id="link_video"  onclick="mostraDIVImagemPerfil('div_video');" >Video</a>
				<c:if test="${ehUsuarioDesobliteracao}">
				&nbsp;&nbsp;
				<a class="link_td" id="link_desob"  onclick="mostraDIVImagemPerfil('div_imagem_des')">Imagem Desobliterada</a>
				</c:if>
				<div id="div_erro_oblit" style="visibility: hidden;">
				<input id="chk_erro_oblit" type="checkbox">Considerar erro de Obliteração 
				</div>
        </td>
		
		<td align="left">
		</td>
		
		<td align="right" width="20%" valign="top">
			<div id="div_miniaturas" style="position: relative;"></div>
		</td>
	</tr>
		
</table>

<input type="hidden" name="id_imagem" id="id_imagem" value="${id_imagem}">
<input type="hidden" name="equipamento_captura_frontal" id="com_captura_frontal" value="${com_captura_frontal}">
<input type="hidden" name="placa_cadastro" id="placa_cadastro" value="">
<input type="hidden" name="area_pista" id="area_pista">
<input type="hidden" name="txt_obs_alt" id="txt_obs_alt">
<div id="div_infracao" 	style="visibility: hidden;"></div>
<div id="div_infracao_des" style="visibility: hidden;"></div>
<div id="div_at_marca_id" 	style="visibility: hidden;"></div>
<div id="div_inconsistencia" style="visibility: hidden;"></div>
<div id="div_inconsistencia_processo" style="visibility: hidden;"></div>
<div id="div_id_enquadramento" style="visibility: hidden;"></div>
<div id="div_velocidade" style="visibility: hidden;"></div>
<div id="div_velocidade_regul" style="visibility: hidden;"></div>
<div id="div_erros" style="visibility: hidden;"></div>
<div id="div_id_remessa" style="visibility: hidden;"></div>

<%@ include file="/includes/rodape.jsp" %>
