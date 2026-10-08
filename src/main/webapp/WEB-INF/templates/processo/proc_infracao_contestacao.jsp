<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.consilux.model.Processamento"%>
<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<%@page import="com.consilux.model.Inconsistencia"%>
<%@page import="java.util.List"%>

<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="com.consilux.infra.ExpValida"%>

<link REL=StyleSheet HREF="/css/jquery-ui.css" TYPE="text/css">
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/multiplas_imagens.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="/GtwClientLogger/GtwClientLogger.nocache.js"></script>
<script type="text/javascript" src="/js/imagem_perfil.js"></script>

<script type="text/javascript">
	var div_ajuste = false;
	var escondidos = false;
	var visualizouPanoramica = false;
	var requerPanoramica = false;
	var id_imagem_obj = 0;
	
	function processar() {
        var div_infracao = document.getElementById("div_infracao");
        var id_infracao = div_infracao.innerHTML;
		var txt_inconsistencia = document.getElementById("txt_inconsistencia");
		
		$.blockUI({ message: '<h1 id="blocked"><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false, fadeTime: 0, fadeIn: 0});
    	var ref_processar = iniciaTempoProcessamento(id_infracao,0,'PROCESSAR [ID_INCONSISTENCIA:'+sel_inconsistencia.value+'|ID_PROCESSO:${id_processo}]');
        tempo = finalizaTempoProcessamento(ref_log_processamento);
        
		var params = {
                acao: <%=Processamento.Acao.INCONSISTE.ordinal()%>,
                id_processo: ${id_processo},
                id_inconsistencia: txt_inconsistencia.value,
                infracao: id_infracao,
                tempo: tempo
            };
        
		$.get('/ajax/processamento/Processar', params, function(xml){
				var idInfracao = $("ID_INFRACAO",xml).text();
				
				if (trataRetorno(xml)) { 
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

    function mostraDetalhes() {
        var div_infracao = document.getElementById("div_infracao");
        var idInfracao= div_infracao.innerHTML;

        window.open("/infracao/infracao_completa.jsp?id_infracao="+idInfracao,"Detalhes","width=1024, height=600");
	}

	function carregaInfracao(id_infracao) {
		if (!(id_infracao > 0)) {
			window.location = "/processo/FinalizarProcesso";
			return;
		}

        $.blockUI({ message: '<h1><img src="/images/busy.gif" /> Aguarde...</h1>', focusInput: false });
    	var ref_log_infracao_completa = iniciaTempoProcessamento(id_infracao,0,'INFRACAO_COMPLETA [ID_PROCESSO:${id_processo}]');
		$.get('/ajax/InfoContestacao', { id_infracao: id_infracao }, function(xml) {
			if (window.iniciaRestanteSessao)
				iniciaRestanteSessao()
				
 			document.getElementById("div_infracao").innerHTML = $("ID_INFRACAO",xml).text();

			document.getElementById("div_data_infracao").innerHTML = $("DATA_INFRACAO",xml).text();
			document.getElementById("div_local").innerHTML = $("LOCAL",xml).text();
			document.getElementById("div_pista").innerHTML = $("CODIGO_FAIXA",xml).text();
			document.getElementById("div_faixa").innerHTML = $("FAIXA",xml).text();
			document.getElementById("div_enquadramento").innerHTML = $("ENQUADRAMENTO",xml).text();
			
			document.getElementById("div_data_cai").innerHTML = $("DATA_CAI",xml).text();
			document.getElementById("div_analise_cai").innerHTML = $("ANALISE_CAI",xml).text();
			document.getElementById("div_usuario_cai").innerHTML = $("USUARIO_CAI",xml).text();
			document.getElementById("div_placa_cai").innerHTML = $("PLACA_CAI",xml).text();
			document.getElementById("div_marca_cai").innerHTML = $("MARCA_CAI",xml).text();
			document.getElementById("div_especie_cai").innerHTML = $("ESPECIE_CAI",xml).text();
			document.getElementById("div_cor_cai").innerHTML = $("COR_CAI",xml).text();
			document.getElementById("div_modelo_cai").innerHTML = $("MODELO_CAI",xml).text();
			document.getElementById("div_isento_cai").innerHTML = $("ISENCAO_CAI",xml).text();
			document.getElementById("div_periodo_cai").innerHTML = $("ISENCAO_PERIODO_CAI",xml).text();
			document.getElementById("div_motivo_cai").innerHTML = $("ISENCAO_MOTIVO_CAI",xml).text();
			
			document.getElementById("div_data_cav").innerHTML = $("DATA_CAV",xml).text();
			document.getElementById("div_analise_cav").innerHTML = $("ANALISE_CAV",xml).text();
			document.getElementById("div_usuario_cav").innerHTML = $("USUARIO_CAV",xml).text();
			document.getElementById("div_placa_cav").innerHTML = $("PLACA_CAV",xml).text();
			document.getElementById("div_marca_cav").innerHTML = $("MARCA_CAV",xml).text();
			document.getElementById("div_especie_cav").innerHTML = $("ESPECIE_CAV",xml).text();
			document.getElementById("div_cor_cav").innerHTML = $("COR_CAV",xml).text();
			document.getElementById("div_modelo_cav").innerHTML = $("MODELO_CAV",xml).text();
			document.getElementById("div_isento_cav").innerHTML = $("ISENCAO_CAV",xml).text();
			document.getElementById("div_periodo_cav").innerHTML = $("ISENCAO_PERIODO_CAV",xml).text();
			document.getElementById("div_motivo_cav").innerHTML = $("ISENCAO_MOTIVO_CAV",xml).text();
			
			document.getElementById("div_divergencia").innerHTML = $("DIVERGENCIA",xml).text();
			
			document.getElementById("txt_inconsistencia").value = $("DECISAO",xml).text();
			ajustaInconsistencia(true);
            
	        document.getElementById("txt_inconsistencia").focus();
            document.getElementById("txt_inconsistencia").select();
            
            // TODO: FELIPE: 
            carregaImagemPerfil($("ID_INFRACAO",xml).text(), $("ID_IMAGEM_OBJ",xml).text(), 'I', $("ID_VEICULO",xml).text());
            
            finalizaTempoProcessamento(ref_log_infracao_completa);
            $.unblockUI();
		});
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
	}
	
function recebe_digito(e) {
		
		if (e.keyCode == 13) {
			processar();
			return false;
		}
	
		var txt_inconsistencia 	= document.getElementById("txt_inconsistencia");
		var charCode 			= 0;
		var retorno 			= true;
		
		if (e.charCode >= 48 && e.charCode <= 51) {
			
			txt_inconsistencia.value = e.key;
			
			retorno = false;
		}
		
		return retorno;
	}
	
    //Chamada automáticamente pelo onUnLoad do cabeçalho.
    function aoFechar() {
    	salvarTemposProcessamento();
    }
    //Chamada automáticamente pelo onLoad do cabeçalho.
    function aoAbrir() {
    	atual();
    }
	
	function AbrirTelaConsultaCadastro() {
		window.open("/ferramenta/listar_cadastro_veiculo.jsp","Consulta","height=340,width=480");
		return false;
	}
</script>

<table class="tabela_branca" width="100%" oncontextmenu="return false;">
	<tr>
	
		<td>
		
		<table>
		
		<tr>
		<td class="label_campo" style="text-align: center;" colspan="2"><h3>Dados da Imagem</h3></td><td></td>
		</tr>
		<tr>
		<td class="label_campo">Nº Infração</td><td class="label_campo">Data da Infração</td><td></td>
		</tr>
		<tr>
		<td class="visualiza_campo"> <div id="div_infracao"></div> </td><td class="visualiza_campo"> <div id="div_data_infracao"></div> </td>  <td></td>
		</tr>
		<tr>
		<td class="label_campo" colspan="2">Local</td><td class="label_campo">Código da Faixa</td>
		</tr>
		<tr>
		<td class="visualiza_campo" colspan="2"> <div id="div_local"></div> </td><td class="visualiza_campo"> <div id="div_pista"></div> </td>
		</tr>
		<tr>
		<td class="label_campo">Faixa</td><td class="label_campo" colspan="2">Enquadramento</td>
		</tr>
		<tr>
		<td class="visualiza_campo"> <div id="div_faixa"></div> </td><td class="visualiza_campo" colspan="2"> <div id="div_enquadramento"></div> </td>
		</tr>
		
		<tr>
		<td class="label_campo" style="text-align: center;" colspan="2"><h3>Análise do CAI</h3></td><td class="visualiza_campo"> <div id="div_usuario_cai"></div> </td>
		</tr>
		<tr>
		<td class="label_campo">Data da Análise</td><td class="label_campo" colspan="2">Análise</td>
		</tr>
		<tr>
		<td class="visualiza_campo"> <div id="div_data_cai"></div> </td><td class="visualiza_campo" colspan="2"> <div id="div_analise_cai"></div> </td>
		</tr>
		<tr>
		<td class="label_campo">Placa</td><td class="label_campo">Marca Resumo</td><td class="label_campo">Espécie</td>
		</tr>
		<tr>
		<td class="visualiza_campo"> <div id="div_placa_cai"></div> </td><td class="visualiza_campo"> <div id="div_marca_cai"></div> </td><td class="visualiza_campo"> <div id="div_especie_cai"></div> </td>
		</tr>
		<tr>
		<td class="label_campo">Cor</td><td class="label_campo" colspan="2">Marca/Modelo</td>
		</tr>
		<tr>
		<td class="visualiza_campo"> <div id="div_cor_cai"></div> </td><td class="visualiza_campo" colspan="2"> <div id="div_modelo_cai"></div> </td>
		</tr>
		<tr>
		<td class="label_campo">Isento?</td><td class="label_campo">Período</td><td class="label_campo">Motivo</td>
		</tr>
		<tr>
		<td class="visualiza_campo"> <div id="div_isento_cai"></div> </td><td class="visualiza_campo"> <div id="div_periodo_cai"></div> </td><td class="visualiza_campo"> <div id="div_motivo_cai"></div> </td>
		</tr>
		
		<tr>
		<td class="label_campo" style="text-align: center;" colspan="2"><h3>Análise do CAV</h3></td><td class="visualiza_campo"> <div id="div_usuario_cav"></div> </td>
		</tr>
		<tr>
		<td class="label_campo">Data da Análise</td><td class="label_campo" colspan="2">Análise</td>
		</tr>
		<tr>
		<td class="visualiza_campo"> <div id="div_data_cav"></div> </td><td class="visualiza_campo" colspan="2"> <div id="div_analise_cav"></div> </td>
		</tr>
		<tr>
		<td class="label_campo">Placa</td><td class="label_campo">Marca Resumo</td><td class="label_campo">Espécie</td>
		</tr>
		<tr>
		<td class="visualiza_campo"> <div id="div_placa_cav"></div> </td><td class="visualiza_campo"> <div id="div_marca_cav"></div> </td><td class="visualiza_campo"> <div id="div_especie_cav"></div> </td>
		</tr>
		<tr>
		<td class="label_campo">Cor</td><td class="label_campo" colspan="2">Marca/Modelo</td>
		</tr>
		<tr>
		<td class="visualiza_campo"> <div id="div_cor_cav"></div> </td><td class="visualiza_campo" colspan="2"> <div id="div_modelo_cav"></div> </td>
		</tr>
		<tr>
		<td class="label_campo">Isento?</td><td class="label_campo">Período</td><td class="label_campo">Motivo</td>
		</tr>
		<tr>
		<td class="visualiza_campo"> <div id="div_isento_cav"></div> </td><td class="visualiza_campo"> <div id="div_periodo_cav"></div> </td><td class="visualiza_campo"> <div id="div_motivo_cav"></div> </td>
		</tr>
		
		<tr>
		<td class="label_campo" colspan="3">Divergência</td><td></td>
		</tr>
		<tr>
		<td class="visualiza_campo" colspan="2"> <div id="div_divergencia"></div> </td>
		
		<td>
		<c:if test="${id_processo == 90}">
		<a href="javascript:mostraDetalhes();">Ver Histórico</a>
		</c:if>
		</td>
		
		</tr>
		<tr>
		<td class="label_campo" style="text-align: center;" colspan="2"><h3>Motivo da Contestação</h3></td><td></td>
		</tr>
		<tr>
		<jsp:include page="${ent_dados}"></jsp:include>
		</tr>
		
		</table>
	
	
		</td>
	
		<td>
		<div name="div_conteudo" id="div_imagem" style="position: relative;">
	                        <img id="img_veiculo" width="640" height="480">
							<div id="div_lista_imagens" style="position: relative;"></div>
                    </div>
		</td>
		
		<td>
			<div id="div_miniaturas" style="position: relative;"></div>
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

<script type="text/javascript">
	//Cria 'flusher' do log de tempo.
	setInterval('salvarTemposProcessamento();', 5000);
</script>
<%@ include file="/includes/rodape.jsp" %>
