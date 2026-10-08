<%@page import="com.consilux.infra.SessaoConstantes"%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
    String sIdVeiculo = request.getParameter("id_veiculo") != null ? request.getParameter("id_veiculo").trim() : null;
    sIdVeiculo = sIdVeiculo != null && sIdVeiculo.length() == 0 ? null : sIdVeiculo;
    
    String sEncadeado = request.getParameter("encadeado");
    Boolean encadeado = ( sEncadeado != null );
    
    String sRegistrarVisualizacao = request.getParameter("registrar_visualizacao_veiculo");
    boolean registrarVisualizacao = sRegistrarVisualizacao != null;

    if (sIdVeiculo == null || !ExpValida.LONGO.validar(sIdVeiculo)) {
        new MensagemJS(response).showErro("Identificador de veículo enviado inválido!");
        return;
    }
    
    String sGrupoDesob = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("grupo_desoblitera_imagens");
	Integer iGrupoDesob = Integer.parseInt(sGrupoDesob);
	
	Usuario usu = (Usuario)request.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);
	Boolean ehUsuarioDesobliteracao = Usuario.usuarioPertenceAoGrupo(usu.getId(), iGrupoDesob);
	
	Integer intGrupoVisualizaTarget = 30;
	Boolean usuarioGrupoVisualizaTarget = Usuario.usuarioPertenceAoGrupo(usu.getId(), intGrupoVisualizaTarget);
%>

<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="com.consilux.infra.ExpValida"%>
<c:set var="idVeiculo" value="<%=sIdVeiculo%>" />
<c:set var="encadeado" value="<%=encadeado%>"/>
<c:set var="registrarVisualizacao" value="<%=registrarVisualizacao%>" />
<c:set var="ehUsuarioDesobliteracao" value="<%=ehUsuarioDesobliteracao%>"/>
<c:set var="usuarioGrupoVisualizaTarget" value="<%=usuarioGrupoVisualizaTarget%>"/>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/multiplas_imagens.js"></script>
<script type="text/javascript" src="/js/imagem_perfil.js"></script>
<script type="text/javascript" src="/js/obliteracao.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="/GtwClientLogger/GtwClientLogger.nocache.js"></script>
<script type="text/javascript">
	var idVeiculoAtual = ${idVeiculo};
	var registrarVisualizacao = ${registrarVisualizacao};
	var usuarioGrupoVisualizaTarget = ${usuarioGrupoVisualizaTarget};

	
	function atual() {
		carregaVeiculo(idVeiculoAtual);
    }

	function anterior() {
		idVeiculoAnterior = window.opener.getIdVeiculoAnterior(idVeiculoAtual);
        carregaVeiculo(idVeiculoAnterior);
	}
	
    function proximo() {
    	idVeiculoProximo = window.opener.getIdVeiculoProximo(idVeiculoAtual);
        carregaVeiculo(idVeiculoProximo);
    }
    
	function carregaVeiculo(idVeiculo) {
		if (!(idVeiculo > 0)) {
			return;
		}
		
		$.get('/ajax/InfoVeiculo', { id_veiculo: idVeiculo , registrar_visualizacao: registrarVisualizacao }, function(xml){
			
			if($("COM_VIDEO",xml).text() == "0") {
				var link_video = document.getElementById("link_video");
				link_video.style = "pointer-events: none; cursor: default; color: gray;"
			}
			
			document.getElementById("video_type").value = $("COM_VIDEO_ALT",xml).text();
			
			if($("COM_TARGET",xml).text() == "0") {
				if (document.getElementById("div_target_velocidade").style.display == "") {
					document.getElementById("div_imagem").style.display = "";
					document.getElementById("div_target_velocidade").style.display = "none";
				}
				if (document.getElementById("div_target_ponto").style.display == "") {
					document.getElementById("div_imagem").style.display = "";
					document.getElementById("div_target_ponto").style.display = "none";
				}
				
				var link_target_velocidade = document.getElementById("link_target_velocidade");
				if (link_target_velocidade) {
					link_target_velocidade.style = "pointer-events: none; cursor: default; color: gray;"
				}

				var link_target_ponto = document.getElementById("link_target_ponto");
				if (link_target_ponto) {
					link_target_ponto.style = "pointer-events: none; cursor: default; color: gray;"
				}
			} else {
				var link_target_velocidade = document.getElementById("link_target_velocidade");
				if (link_target_velocidade) {
					link_target_velocidade.removeAttribute("style");
				}

				var link_target_ponto = document.getElementById("link_target_ponto");
				if (link_target_ponto) {
					link_target_ponto.removeAttribute("style");
				}
			}
			
			document.getElementById("div_placa").innerHTML = $("PLACA",xml).text();
			document.getElementById("div_veiculo").innerHTML = idVeiculo;
			document.getElementById("div_porte").innerHTML = $("PORTE_VEICULO",xml).text();
            buscaClasse($("CLASSE",xml).text());
            buscaCadastro($("PLACA",xml).text());

			carregaImagemPerfil(idVeiculo, $("ID_IMAGEM_OBJ",xml).text(),'V');
			
            idVeiculoAtual = idVeiculo;
		});
	}

    function buscaCadastro(placa) {

    	document.getElementById("div_marca").innerHTML = "&nbsp;";
        document.getElementById("div_cor").innerHTML = "&nbsp;";
        document.getElementById("div_ano").innerHTML = "&nbsp;";
        document.getElementById("div_especie").innerHTML = "&nbsp;";
        document.getElementById("div_tipo").innerHTML = "&nbsp;";
        document.getElementById("div_categoria").innerHTML = "&nbsp;";
        document.getElementById("div_situacao").innerHTML = "&nbsp;";
        document.getElementById("div_localidade").innerHTML = "&nbsp;";
        document.getElementById("div_atualizacao").innerHTML = "&nbsp;";
        
        if (placa.length != 7)
            return;
        
        $.get('/ajax/InfoCadastro', { placa: placa }, function(xml) {
            document.getElementById("div_marca").innerHTML = $("MARCA",xml).text() != "" ? $("MARCA",xml).text() : "&nbsp;"; //Artifício técnico para não alterar o layout da tabela
            document.getElementById("div_cor").innerHTML = $("COR",xml).text();
            document.getElementById("div_ano").innerHTML = $("ANO",xml).text();
            document.getElementById("div_especie").innerHTML = $("ESPECIE",xml).text();
            document.getElementById("div_tipo").innerHTML = $("TIPO",xml).text() != "" ? $("TIPO",xml).text() : "&nbsp;" //Artifício técnico para não alterar o layout da tabela
            document.getElementById("div_categoria").innerHTML = $("CATEGORIA",xml).text();
            document.getElementById("div_situacao").innerHTML = $("SITUACAO",xml).text();
            document.getElementById("div_localidade").innerHTML = $("LOCALIDADE",xml).text();
            document.getElementById("div_atualizacao").innerHTML = $("ATUALIZACAO",xml).text();
        });
    }
    function buscaClasse(idClasse) {
    	document.getElementById("div_classe").innerHTML = "&nbsp;";
        
        if (idClasse == "") {
            return;
        }
        
        $.get('/ajax/InfoClasse', { id_classe: idClasse }, function(xml) {
            document.getElementById("div_classe").innerHTML = $("CLASSE",xml).text();
        });
    }
	function aoAbrir() {
		atual();
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
</script>
<style>
    #link_video {
        display: none;
    }
    #link_download_infracao {
        display: none;
    }
</style>
<body onunload="if (aoFechar) aoFechar();">
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_branca" width="650">
				<tr>
					<td class="visualiza_campo" colspan="2">
						<a class="link_td" id="link_imagem" onclick="mostraDIVImagemPerfil('div_imagem')">Imagem</a>&nbsp;&nbsp;
						<a class="link_td" id="link_perfil" onclick="mostraDIVImagemPerfil('div_perfil')">Perfil</a>&nbsp;&nbsp;
						<a class="link_td" id="link_video"  onclick="mostraDIVImagemPerfil('div_video')" >Video</a>
						<c:if test="${usuarioGrupoVisualizaTarget}">
						<a class="link_td" id="link_target_velocidade" onclick="mostraDIVImagemPerfil('div_target_velocidade')">Target (Velocidade)</a>&nbsp;&nbsp;
						<a class="link_td" id="link_target_ponto" onclick="mostraDIVImagemPerfil('div_target_ponto')">Target (Pontos)</a>&nbsp;&nbsp;
						</c:if>
						<c:if test="${ehUsuarioDesobliteracao}">
						&nbsp;&nbsp;
						<a class="link_td" id="link_desob"  onclick="mostraDIVImagemPerfil('div_imagem_des')">Imagem Desobliterada</a>
						</c:if>
					</td>
	 			 </tr>
			 </table>
		 	 <table class="tabela_branca" width="650">
				<tr>
					<td class="label_campo" width="10%">Veículo</td>
					<td class="label_campo" width="15%">Classe</td>
					<td class="label_campo" width="65%">Porte</td>
				</tr>
				<tr>
                    <td class="visualiza_campo" width="10%"><div id="div_veiculo"></div></td>
                    <td class="visualiza_campo" width="15%"><div id="div_classe"></div></td>
                    <td class="visualiza_campo" width="65%"><div id="div_porte"></div></td>
				</tr>
				<tr>
                    <td class="label_campo" colspan="3" style="text-align: center">
                    	<div name="div_conteudo" id="div_imagem"> 
							<table class="tabela_branca" width="100%">
								<tr>
									<td align="center">
					                    <div style="position: relative;">
					                        <img id="img_veiculo" width="640" height="480">
					                    </div>
				                    </td>
								</tr>
				                <tr>
				                    <td colspan="2">
				                        <div name="div_conteudo" id="div_lista_imagens" style='position: relative;'></div>
				                    </td>
				                </tr>
							</table>
						</div>
                        <div name="div_conteudo" id="div_perfil" style="position: relative; display: none;">
                        	<iframe id="if_perfil" width="640" height="480" style="border: none"></iframe>
                        </div>
                        <div name="div_conteudo" id="div_video" style="position: relative; display: none;">
                        	<iframe id="if_video" width="640" height="480" style="border: none"></iframe>
                        	
<!-- XXX:FELIPE: SELECAO DE VIDEO -->
<a style='text-decoration: none;' href='javascript:mostraVideo(1)'>1</a>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<a style='text-decoration: none;' href='javascript:mostraVideo(2)'>2</a>
                        	
                        </div>
                        <div name="div_conteudo" id="div_target_velocidade" style="position: relative; display: none;">
                        	<iframe id="if_target_velocidade" width="640" height="480" style="border: none"></iframe>
                        </div>
                        <div name="div_conteudo" id="div_target_ponto" style="position: relative; display: none;">
                        	<iframe id="if_target_ponto" width="640" height="480" style="border: none"></iframe>
                        </div>
                        <div name="div_conteudo" id="div_imagem_des" style="position: relative; display: none;">
                        	<img id="img_veiculo_des" width="640" height="480">
                        </div>
                    </td>
				</tr>
			</table>
			<table class="tabela_branca" width="750">
                <tr>
                    <td class="label_campo" width="10%">Placa Lida</td>
                    <td class="label_campo" width="30%">Marca</td>
                    <td class="label_campo" width="10%">Cor</td>
                    <td class="label_campo" width="10%">Ano</td>
                    <td class="label_campo" colspan="2" width="40%">Espécie</td>
                </tr>
                <tr>
                    <td class="visualiza_campo"><div id="div_placa"></div></td>
					<td class="visualiza_campo"><div id="div_marca"></div></td>
					<td class="visualiza_campo"><div id="div_cor"></div></td>
					<td class="visualiza_campo"><div id="div_ano"></div></td>
					<td class="visualiza_campo" colspan="2"><div id="div_especie"></div></td>
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
					<tr>
					    <td class="box_botoes" colspan="6">
						    <table class="tabela_branca" width="100%">
						        <tr>
                                    <td width="10%">
                                        <button onclick="print();">Imprimir</button>
                                    </td>
<!-- 								<td width="3%"><input type="checkbox" id="cbObliteracao" onclick="verifObliteracao();"></td> -->
<!-- 									<td class="dado_lista_tabela_claro" align="left" width="67%">Com obliteração</td> -->
						            <td width="10%">
	                                    <c:if test="${encadeado}">
						       	            <button onclick="anterior();">Anterior</button>
	                                    </c:if>
						            </td>
						            <td width="10%">
                                        <c:if test="${encadeado}">
								            <button onclick="proximo();">Próximo</button>
                                        </c:if>
						            </td>
						        </tr>
						    </table>
					    </td>
					</tr>
            </table>
		</td>
	</tr>
</table>
<input type="hidden" name="video_type" id="video_type" value="0">
<input type="hidden" name="id_imagem" id="id_imagem" readonly="readonly">
<input type="hidden" name="video_sel" id="video_sel" value="1">
</body>
<%@ include file="/includes/rodape.jsp" %>
