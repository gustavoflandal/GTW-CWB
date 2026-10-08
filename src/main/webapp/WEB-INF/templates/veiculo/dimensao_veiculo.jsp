<%@page import="java.util.ArrayList"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>

<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
<!-- 			<table class="tabela_branca" width="100%"> -->
<!--                 <tr><td>&nbsp;</td></tr> -->
<!--                 <tr><th class="head_tabela" width="100%" colspan="5">Classificação QFV</th></tr> -->
<!-- 				<tr> -->
<%-- 					<c:forEach var="imagem" varStatus="linhaInfo" items="${imagens}"> --%>
<%-- 						<td id="id_classificacao_${imagem.classificacaoQfv}" align="center"> --%>
<%-- 		                    <div id="id_classificacao_${imagem.classificacaoQfv}" style="position: relative;"> --%>
<%-- 		                        <img id="id_classificacao_${imagem.classificacaoQfv}" src="/ajax/ImgClassificacaoQFV?id_classificacao=${imagem.classificacaoQfv}" alt="${imagem.classificacaoQfv}" title="${imagem.classificacaoQfv}" style="position: relative; width: 195px; height: 105px;" border="2"> --%>
<%-- 		                        <p style="font-size: x-small; font-style: italic; font-weight: bold; margin-top: 1px;">${imagem.classificacaoQfv}</p> --%>
<!-- 		                    </div> -->
<!-- 	                    </td> -->
<%-- 					</c:forEach> --%>
<!-- 				</tr> -->
<!-- 			</table> -->
			<table class="tabela_branca" width="100%">
                <tr><td>&nbsp;</td></tr>
                <tr><th class="head_tabela" width="100%" colspan="5">Classificação QFV</th></tr>
				<tr>
					<td id="td_classificacao_vazio" align="center" style="visibility: hidden">
	                    <div id="div_classificacao_vazio" style="position: relative;">
	                        <img id="img_ClassificaoQFV_vazio" src="../utils/imagesClassificacaoQFV/SemClassif.bmp" style="position: relative;">
	                        <p id="legenda_vazio" style="font-size: x-small; font-style: italic; font-weight: bold; margin-top: 1px;"></p>
	                    </div>
                    </td>
					<c:forEach var="imagem" varStatus="linhaInfo" items="${imagens}">
						<td id="td_classificacao_${imagem.classificacaoQfv}" align="center">
		                    <div id="div_classificacao_${imagem.classificacaoQfv}" style="position: relative;">
		                        <img id="img_ClassificaoQFV_${imagem.classificacaoQfv}" style="position: relative; width: 195px; height: 105px;" border="2">
		                        <p id="legenda_${imagem.classificacaoQfv}" style="font-size: x-small; font-style: italic; font-weight: bold; margin-top: 1px;"></p>
		                    </div>
	                    </td>
					</c:forEach>
				</tr>
			</table>
			<table class="tabela_branca" width="100%">
                <tr><th class="head_tabela" width="100%" colspan="5">Dimensões</th></tr>
				<tr>
					<td class="label_campo" width="10%" style="text-align: center;">PBT</td>
					<td class="label_campo" width="15%" style="text-align: center;">Excesso PBT</td>
					<td class="label_campo" width="25%" style="text-align: center;">Altura</td>
					<td class="label_campo" width="25%" style="text-align: center;">Comprimento</td>
					<td class="label_campo" width="25%" style="text-align: center;">Largura</td>
				</tr>
				<tr>
					<td class="visualiza_campo" style="text-align: center;"><fmt:formatNumber type="number" maxFractionDigits="2">${pesoDimensao.pbt}</fmt:formatNumber> <c:if test="${pesoDimensao.pbt != null}">kg</c:if></td>
					<td class="visualiza_campo" style="text-align: center;"><fmt:formatNumber type="number" maxFractionDigits="2">${pesoDimensao.excessoPbt}</fmt:formatNumber> <c:if test="${pesoDimensao.excessoPbt != null}">kg</c:if></td>
					<td class="visualiza_campo" style="text-align: center;"><fmt:formatNumber type="number" maxFractionDigits="2">${pesoDimensao.altura}</fmt:formatNumber> <c:if test="${pesoDimensao.altura != null}">metros</c:if></td>
					<td class="visualiza_campo" style="text-align: center;"><fmt:formatNumber type="number" maxFractionDigits="2">${pesoDimensao.comprimento}</fmt:formatNumber> <c:if test="${pesoDimensao.comprimento != null}">metros</c:if></td>
					<td class="visualiza_campo" style="text-align: center;"><fmt:formatNumber type="number" maxFractionDigits="2">${pesoDimensao.largura}</fmt:formatNumber> <c:if test="${pesoDimensao.largura != null}">metros</c:if></td>
				</tr>
			</table>
			<table class="tabela_branca" width="49%" style="float: left;">
				<tr><td>&nbsp;</td></tr>
                <tr><th class="head_tabela" width="100%" colspan="3">Eixos</th></tr>
				<tr>
					<td class="label_campo" width="20%" style="text-align: center;">Eixo</td>
					<td class="label_campo" width="20%" style="text-align: center;">Peso</td>
					<td class="label_campo" width="60%" style="text-align: center;">Distância eixo anterior</td>
				</tr>
				<c:forEach var="eixo" varStatus="linhaInfo" items="${eixos}">
					<tr>
					<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						<td class="${css_td}" align="center">${eixo.descEixo}</td>
						<td class="${css_td}" align="center"><fmt:formatNumber type="number" maxFractionDigits="2">${eixo.peso}</fmt:formatNumber></td>
						<td class="${css_td}" align="center"><fmt:formatNumber type="number" maxFractionDigits="2">${eixo.distanciaEixoAnterior}</fmt:formatNumber></td>
					</tr>
				</c:forEach>
			</table>
			<table class="tabela_branca" width="49%" style="float: right;">
				<tr><td>&nbsp;</td></tr>
                <tr><th class="head_tabela" width="100%" colspan="3">Grupos</th></tr>
				<tr>
					<td class="label_campo" width="33%" style="text-align: center;">Grupo</td>
					<td class="label_campo" width="33%" style="text-align: center;">Peso</td>
					<td class="label_campo" width="33%" style="text-align: center;">Excesso</td>
				</tr>
				<c:forEach var="grupo" varStatus="linhaInfo" items="${grupos}">
					<tr>
					<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						<td class="${css_td}" align="center">${grupo.descGrupo}</td>
						<td class="${css_td}" align="center"><fmt:formatNumber type="number" maxFractionDigits="2">${grupo.peso}</fmt:formatNumber></td>
						<td class="${css_td}" align="center"><fmt:formatNumber type="number" maxFractionDigits="2">${grupo.excesso}</fmt:formatNumber></td>
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>

<script type="text/javascript">
	$(document).ready(function() {
		ajustarClassificacaoQFV('${pesoDimensao.todasClassificacaoQfv}');
	});
	
 	function ajustarClassificacaoQFV(classificacao)
 	{
 		
 		if (classificacao == '') {
 			
 			bloquearImgQFV_semVeic(false);
 			
 		} else {
 			
 			bloquearImgQFV_semVeic(true);
 			
	 		var quebra = classificacao.split("-"); 		
	 		var qtde = 0;
	 		var legenda = true;
	 		
	 		//Limita qtde de classificações em três
	 		//Existe apenas 3 caixinhas na tela
	 		if(quebra.length > 3)
	 			qtde = 3;
	 		else
	 			qtde = quebra.length;
	 		
	 		for (var i = 0; i < qtde; i++) 
	 		{
	 			var classif = quebra[i].replace(" ", ""); 			
	 			var caminho = "../utils/imagesClassificacaoQFV/" + classif 	+ ".bmp";
	 			legenda = true;
	 			
	 			if (!doesFileExist(caminho))
	 			{
	 				var caminho = "../utils/imagesClassificacaoQFV_Outros/" + classif + ".bmp";
	 				if(!doesFileExist(caminho))
	 				{
	 					var caminho = "../utils/imagesClassificacaoQFV/SemClassif.bmp";
	 					legenda = false;
	 				} 				
	 			}
	 			
	 			var id = i+1;
	 			document.getElementById('img_ClassificaoQFV_'+classif).src = caminho;
	 			if (legenda) {document.getElementById('legenda_'+classif).innerHTML = classif;}
			}
 		}
 										    
 	}

 	function doesFileExist(urlToFile)
 	{
 	    var xhr = new XMLHttpRequest();
 	    xhr.open('HEAD', urlToFile, false);
 	    xhr.send();

 	    if (xhr.status == "404") {
 	        console.log("File doesn't exist: " +urlToFile );
 	        return false;
 	    } else {
 	        console.log("File exists: " + urlToFile);
 	        return true;
 	    }
 	} 	
 	
 	function bloquearImgQFV_semVeic(bloquear) {
 		var td_classificacao_vazio = document.getElementById("td_classificacao_vazio");
 		var img_ClassificaoQFV_vazio = document.getElementById("img_ClassificaoQFV_vazio");
 		
 		if (bloquear) {
 			img_ClassificaoQFV_vazio.src = "";
 			img_ClassificaoQFV_vazio.width = "0";
 			img_ClassificaoQFV_vazio.height = "0";
 			img_ClassificaoQFV_vazio.style.border = "hidden";
 			td_classificacao_vazio.style.visibility = "hidden";
 		} else {
 			img_ClassificaoQFV_vazio.src = '../utils/imagesClassificacaoQFV/SemClassif.bmp';
 			img_ClassificaoQFV_vazio.width = "195";
 			img_ClassificaoQFV_vazio.height = "105";
 			img_ClassificaoQFV_vazio.style.border = "solid";
 			td_classificacao_vazio.style.visibility = "visible";
 		}
 	}

</script>