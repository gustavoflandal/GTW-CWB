<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	String sPlaca = request.getParameter("placa");
    String sIdEnquadramento = request.getParameter("id_enquadramento");

	if (sPlaca != null && !ExpValida.PLACA_LIKE.validar(sPlaca) && !ExpValida.PLACA_LIKE_MERCOSUL.validar(sPlaca)) {
        new MensagemJS(response).showErro("Placa enviada invalida!");
        return;
	}
    if (sIdEnquadramento != null && !Pattern.matches("[0-9]{1,8}",sIdEnquadramento)) {
        new MensagemJS(response).showErro("Enquadramento selecionado inválido!");
        return;
    }

    Map<String,Object> mFiltro = new HashMap<String,Object>();
    
    mFiltro.put("placa",sPlaca);
    if (sIdEnquadramento != null && Integer.valueOf(sIdEnquadramento) > 0)
        mFiltro.put("i.id_enquadramento",Integer.valueOf(sIdEnquadramento));
    
    List<Isento> isentos = Isento.buscaIsentoPor(mFiltro);
    
%>
<%@page import="com.consilux.model.Cadastro"%>
<%@page import="com.consilux.model.CadastroBD"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.List"%>

<%@page import="com.consilux.model.Isento"%>
<%@page import="com.consilux.infra.ExpValida"%>
<c:set var="isentos" value="<%=isentos%>" />
<table class="tabela_branca" width="100%">
    <tr>
        <td align="center">
	        <table class="tabela_lista" width="950">
	            <tr>
	                <th class="head_tabela" width="5%">Placa</th>
	                <th class="head_tabela" width="5%">Enquadramento</th>
	                <th class="head_tabela" width="10%">Data Início</th>
	                <th class="head_tabela" width="10%">Horário Início</th>
	                <th class="head_tabela" width="10%">Data Fim</th>
	                <th class="head_tabela" width="10%">Horário Fim</th>
	                <th class="head_tabela" width="30%">Motivo</th>
	                <th class="head_tabela" width="10%">Entrada Cadastro</th>
	                <th class="head_tabela" width="10%">Última Confirmação</th>
	            </tr>
	            <c:forEach var="isento" varStatus="linhaInfo" items="${isentos}">
	                <tr>
	                    <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                        <td class="${css_td}" align="center" style="${!isento.vigente ? 'color: #FF0000;' : ''}">${isento.placa}</td>
                        <td class="${css_td}" align="center">${isento.idEnquadramento}</td>
	                    <td class="${css_td}" align="center"><fmt:formatDate value="${isento.dataInicio}" type="date" pattern="dd/MM/yyyy" /></td>
	                    <td class="${css_td}" align="center"><fmt:formatDate value="${isento.horarioInicio}" type="time" /></td>
	                    <td class="${css_td}" align="center"><fmt:formatDate value="${isento.dataFim}" type="date" pattern="dd/MM/yyyy" /></td>
	                    <td class="${css_td}" align="center"><fmt:formatDate value="${isento.horarioFim}" type="time" /></td>
                        <td class="${css_td}" align="center">${isento.motivo}</td>
	                    <td class="${css_td}" align="center"><fmt:formatDate value="${isento.dataEntradaCadastro}" type="date" pattern="dd/MM/yyyy" /></td>
	                    <td class="${css_td}" align="center"><fmt:formatDate value="${isento.dataUltimoCadastro}" type="date" pattern="dd/MM/yyyy" /></td>
	                </tr>
	            </c:forEach>
	        </table>
        </td>
    </tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
