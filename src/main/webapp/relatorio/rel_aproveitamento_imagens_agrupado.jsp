
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%><%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="java.util.Date"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="com.consilux.model.relatorio.AproveitamentoImagensAgrupado"%>
<%
	///////////////////////////////////////////////////////////////////////////////////////
	// Primeira Parte: Obter os valores da request.
	///////////////////////////////////////////////////////////////////////////////////////

	String sDataInicio = request.getParameter("dataInicio");
	String sDataFim = request.getParameter("dataFim");

    ///////////////////////////////////////////////////////////////////////////////////////
	// Segunda Parte: Validação 
    ///////////////////////////////////////////////////////////////////////////////////////
	
    if (sDataInicio == null || (sDataInicio != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataInicio))) {
        new MensagemJS(response).showErro("Data inicial do período inválida!");
        return;
    }
    if (sDataFim == null && (sDataFim != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})", sDataFim))) {
        new MensagemJS(response).showErro("Data final do período inválida!");
        return;
    }
	
    ///////////////////////////////////////////////////////////////////////////////////////
	// Terceira Parte: Conversão e execução 
    ///////////////////////////////////////////////////////////////////////////////////////
    
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date dataInicio = dateFormat.parse(sDataInicio);
    Date dataFim = dateFormat.parse(sDataFim);
    
    if (dataInicio.after(dataFim)) {
        new MensagemJS(response).showErro("Data inicial deve ser anterior à data final!");
        return;
    }    
    
	String corpoRelatorio = AproveitamentoImagensAgrupado.getRelatorio(
			dataInicio,
			dataFim);

	out.print(corpoRelatorio);

%>
<%@ include file="/includes/rodape.jsp" %>