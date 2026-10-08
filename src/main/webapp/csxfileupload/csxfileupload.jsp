<%@page import="com.consilux.infra.SessaoConstantes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<c:set var="id_imagem" value='<%=request.getParameter("id_imagem")%>' scope="request"/>
<c:set var="sub_diretorio" value='<%=request.getParameter("sub_diretorio")%>' scope="request"/>
<script type="text/javascript">
	function mostraImagem(idImagem) {
		window.open("/ajax/ImgVeiculo?id_imagem="+idImagem, "Imagem","width=750, height=700");
	}
</script>
	<applet code=com.consilux.init.CSXFileUpload 
	        archive="/csxfileupload/CSXFileUpload-0.0.1-SNAPSHOT-jar-with-dependencies.jar"
	        width=0 height=0>
	         <param name="url_destino" value="/servlet/UpImgVeiculo;jsessionid=${jsessionid}?id_imagem=${id_imagem}"/>
	         <param name="sub_diretorio" value="imagens/${sub_diretorio}"/>
	         <param name="mascara" value="*_${id_imagem}.jpg"/>
	         <param name="js_saida" value="mostraImagem(${id_imagem})"/>
	</applet>
<%@ include file="/includes/rodape.jsp" %>
