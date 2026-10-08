<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.Infracao"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
	String sIdInfracao = request.getParameter("infracao");
	String sNome = request.getParameter("nome");
	String sEndereco = request.getParameter("endereco");
	String sCidade = request.getParameter("cidade");
	String sUF = request.getParameter("UF");
	String sCEP = request.getParameter("CEP");
	String sCPF = request.getParameter("CPF");
	String sRG = request.getParameter("RG");
	String sTelefone = request.getParameter("telefone");
	String sDocCNH = request.getParameter("docCNH");
	String sRegCNH = request.getParameter("regCNH");
	String sUFCNH = request.getParameter("UFCNH");
	String sDataEntrada = request.getParameter("dataEntrada");
	
	if (sIdInfracao == null || !Pattern.matches("[1-9][0-9]{0,7}",sIdInfracao)) {
		new Mensagem(response).showErro("Identificador da Infração enviado invalido!");
		return;
	}
	else if (sNome == null || sNome.length() > 60) {
		new Mensagem(response).showErro("Nome enviado invalido!");
		return;
	}
	else if (sEndereco == null || sEndereco.length() > 100) {
		new Mensagem(response).showErro("Endereço enviado invalido!");
		return;
	}
	else if (sCidade == null || sCidade.length() > 50) {
		new Mensagem(response).showErro("Cidade enviada invalida!");
		return;
	}
	else if (sUF == null || sUF.length() > 2) {
		new Mensagem(response).showErro("UF enviada invalida!");
		return;
	}
	else if (sCEP == null || !Pattern.matches("[0-9]{8}",sCEP)) {
		new Mensagem(response).showErro("CEP enviado invalido!");
		return;
	}
	else if (sCPF == null || !Pattern.matches("[0-9]{11}",sCPF)) {
		new Mensagem(response).showErro("CPF enviado invalido!");
		return;
	}
	else if (sRG == null || sRG.length() > 20) {
		new Mensagem(response).showErro("RG enviado invalido!");
		return;
	}
	else if (sTelefone == null || !Pattern.matches("[0-9]{10}",sTelefone)) {
		new Mensagem(response).showErro("Telefone enviado invalido!");
		return;
	}
	else if (sDocCNH == null || sDocCNH.length() > 10) {
		new Mensagem(response).showErro("Nº da CNH enviado inválido!");
		return;
	}
	else if (sRegCNH == null || sRegCNH.length() > 10) {
		new Mensagem(response).showErro("Registro da CNH enviado inválido!");
		return;
	}
	else if (sUFCNH == null || sUFCNH.length() > 10) {
		new Mensagem(response).showErro("UF da CNH enviada inválida!");
		return;	
	}
	else if (sDataEntrada == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataEntrada)) {
		new Mensagem(response).showErro("Data de entrada enviada inválida!");
		return;
	}
		
	String sPlaca;
	String sDataInfracao;
	try {
		Infracao infracao = Infracao.buscaInfracaoPorId(Integer.parseInt(sIdInfracao));

		if (infracao == null) {
			new Mensagem(response).showErro("Infração '"+sIdInfracao+"' inválida!");
			return;
		}

		sPlaca = infracao.getPlaca();
		sDataInfracao = new SimpleDateFormat("dd/MM/yyyy").format(infracao.getData());
	}
	catch(Exception err) {
		throw new ServletException("Erro ao verificar a infração: "+err.getMessage());
	}

%>
<%@page import="java.text.SimpleDateFormat"%>
<jsp:useBean id="nai" class="com.consilux.model.beans.NAIBean" scope="session"/>
<jsp:setProperty name="nai" property="placa" value="<%=sPlaca%>" /> 
<jsp:setProperty name="nai" property="dataInfracao" value="<%=sDataInfracao%>" /> 
<jsp:setProperty name="nai" property="*" /> 
<%@ include file="/WEB-INF/templates/cadastro/vis_nai.jsp" %>
<%@ include file="/includes/rodape.jsp" %>
