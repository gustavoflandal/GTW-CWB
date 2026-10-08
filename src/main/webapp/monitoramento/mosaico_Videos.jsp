<%@page import="com.consilux.model.MosaicoVideo"%>
<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ include file="/includes/cabecalho.jsp" %>

<%
	ArrayList<MosaicoVideo> listMosaico = new ArrayList<MosaicoVideo>();
	MosaicoVideo mosaico = new MosaicoVideo();
	//listMosaico = mosaico.buscaTodasEquipamentos();
	listMosaico = mosaico.buscaTodosEquipamentosBD();
%>
<c:set var="listEquipamentos" value="<%=listMosaico%>" />
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>

<script type="text/javascript" >


	function Start()
	{
		window.setInterval(function()
		{ 
			//alert("Estou sendo redirecionado por tempo maximo de acesso aos videos!!");
			redirecionaTelaInicial();
		}, (1000 * 60 * 10)); //Tempo de 10 Minutos
		
		
	}
	
	function redirecionaTelaInicial()
	{
		//this.sendRedirect("/relatorio/frm_relatorios_CAV_CAI.jsp","","width=550, height=300");
		window.location.href = "/login/gtw_principal.jsp";
	}

	$(function(){
	    
	    window.onload = function(){
	       <c:forEach items="${listEquipamentos}" var="listEquip">
	        $("#videos").append(createDivEmbedVideo("${listEquip.idLocal}", 
	        										   "${listEquip.descEquipamento}", 
	        										   "${listEquip.intWidth}",
	        										   "${listEquip.intHeight}",
	        										   "${listEquip.ip}",
	        										   "${listEquip.qualidade}",
	        										   "${listEquip.frameRate}"));
	       </c:forEach>
	    }
	    
	    function createDivEmbedVideo(numID, descricao, valorWidth, valorHeight, ip, qualidade,frameRate){
		    
	    	var html = '<div style="float: left;">';
	    	    html+= '	<div> <td class="dado_lista_tabela_claro" align="center"> '+ descricao +' </td> </div>';
	            html+= ' 	<div id="div_video_'+numID+'">';
	        	html+= ' 		<embed  id="Embed_video_'+numID+'"' ;
	        	html+= ' 			type="application/x-vlc-plugin" ';
	        	html+= ' 			pluginspage="http://www.videolan.org" ';
	        	html+= ' 			name="Embed_video_'+numID+'" ';
	        	html+= '  			autoplay="yes" ';
	        	html+= ' 			loop="yes" ';
	        	html+= ' 			width="' + valorWidth + '" ';
	        	html+= ' 			height="' + valorHeight + ' "';
	        	html+= ' 			target="http://' + ip + '/api/mjpegvideo.cgi?Quality='+ qualidade +'&FrameRate='+ frameRate +'&Resolution=' +valorWidth+ 'x' + valorHeight+ '" ';
	        	html+= ' 		</embed> ';
	            html += '	</div>';
	            html += '</div>';
	            html += '<div style="float: left;">';
	            html +=		'<th class="head_tabela" width="4%">&nbsp;</th>';
	            html += '</div>';
	
	            return html;
	    }	
	});
</script>

	<table class=dado_lista_tabela_claro width="95%" align="center">
		
		<tr><td class="dado_lista_tabela_claro" width="3%">&nbsp;</td></tr>
		<tr>
			<td class="label_campo" width="100%">Mosaíco de Vídeos AO VIVO de Equipamentos</td>
		</tr>
		<tr><td class="dado_lista_tabela_claro" width="3%">&nbsp;</td></tr>
	</table>
	
	<table class="tabela_lista" width="95%" align="center">
	
		<tr>
			
			<th class="head_tabela" width="15%">Local</th>
			<th class="head_tabela" width="0.2%"></th>
			<th class="head_tabela" width="85%">Equipamento</th>
<!-- 			<th class="head_tabela" width="0.2%"></th> -->
<!-- 			<th class="head_tabela" width="10%">Frame Rate</th> -->
<!-- 			<th class="head_tabela" width="0.2%"></th> -->
<!-- 			<th class="head_tabela" width="10%">Qualidade</th> -->
<!-- 			<th class="head_tabela" width="0.2%"></th> -->
<!-- 			<th class="head_tabela" width="15%">IP</th> -->
<!-- 			<th class="head_tabela" width="0.2%"></th> -->
<!-- 			<th class="head_tabela" width="10%">Width</th> -->
<!-- 			<th class="head_tabela" width="0.2%"></th> -->
<!-- 			<th class="head_tabela" width="10%">Height</th> -->
			
		</tr>
		
		<c:forEach items="${listEquipamentos}" var="listEquip" varStatus="linhaInfo">
		<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
		<tr> 
		   <td class="${css_td}" align="center">${listEquip.idLocal}<td>
		   <td class="${css_td}" align="left">${listEquip.descEquipamento}<td>
<%-- 		   <td class="${css_td}" align="center">${listEquip.frameRate}<td> --%>
<%-- 		   <td class="${css_td}" align="center">${listEquip.qualidade}<td> --%>
<%-- 		   <td class="${css_td}" align="left">${listEquip.ip}<td> --%>
<%-- 		   <td class="${css_td}" align="center">${listEquip.intWidth}<td> --%>
<%-- 		   <td class="${css_td}" align="center">${listEquip.intHeight}<td> --%>
		</tr>
		</c:forEach>
	</table>
	
	<table class=dado_lista_tabela_claro id="videos" width="95%" align="center">
		<tr><td class="dado_lista_tabela_claro" width="3%">&nbsp;</td></tr>
	</table>
	
	<script>Start();</script>
	
<%@ include file="/includes/rodape.jsp" %>
