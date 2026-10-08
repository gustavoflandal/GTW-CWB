<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	String sPlaca = request.getParameter("placa");
	String sMarca = request.getParameter("marca");
	String sLocalidade = request.getParameter("localidade");
	String sCategoria = request.getParameter("categoria");
	String sEspecie = request.getParameter("especie");
	String sTipo = request.getParameter("tipo");
    String sIdEnquadramento = request.getParameter("id_enquadramento");

	if (sPlaca != null && !ExpValida.PLACA_LIKE.validar(sPlaca) && !ExpValida.PLACA_LIKE_MERCOSUL.validar(sPlaca)) {
        new MensagemJS(response).showErro("Placa enviada invalida!");
        return;
	}

	if (sLocalidade != null && sLocalidade.length() > 30) {
        new MensagemJS(response).showErro("Localidade enviada invalida!");
        return;
	}

	if (sCategoria != null && sCategoria.length() > 30) {
        new MensagemJS(response).showErro("Categoria enviada invalida!");
        return;
	}

	if (sTipo != null && sTipo.length() > 30) {
        new MensagemJS(response).showErro("Tipo enviada invalida!");
        return;
	}

	if (sEspecie != null && sEspecie.length() > 30) {
        new MensagemJS(response).showErro("Especie enviada invalida!");
        return;
	}

	if (sMarca != null && sMarca.length() > 33) {
        new MensagemJS(response).showErro("Marca enviado invalido!");
        return;
    }

    if (sIdEnquadramento != null && !Pattern.matches("[0-9]{1,8}",sIdEnquadramento)) {
        new MensagemJS(response).showErro("Enquadramento selecionado inválido!");
        return;
    }

    Map<String,Object> mFiltro = new HashMap<String,Object>();
    
    mFiltro.put("placa",sPlaca);
    if (sMarca != null && sMarca.length() > 0)
        mFiltro.put("marca","%"+sMarca.toUpperCase()+"%");
    if (sIdEnquadramento != null && Integer.valueOf(sIdEnquadramento) > 0)
        mFiltro.put("isento_enquadramento",Integer.valueOf(sIdEnquadramento));
    if (sLocalidade != null && sLocalidade.length() > 0)
        mFiltro.put("localidade","%"+sLocalidade.toUpperCase()+"%");
    if (sCategoria != null && sCategoria.length() > 0)
        mFiltro.put("categoria","%"+sCategoria.toUpperCase()+"%");
    if (sTipo != null && sTipo.length() > 0)
        mFiltro.put("tipo","%"+sTipo.toUpperCase()+"%");
    if (sEspecie != null && sEspecie.length() > 0)
        mFiltro.put("especie","%"+sEspecie.toUpperCase()+"%");
    
    List<Cadastro> cadastros = CadastroBD.buscaCadastroPor(mFiltro);
    
%>
<%@page import="com.consilux.model.Cadastro"%>
<%@page import="com.consilux.model.CadastroBD"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="java.util.List"%>
<%@page import="com.consilux.infra.ExpValida"%>
<c:set var="cadastros" value="<%=cadastros%>" />
<table class="tabela_branca" width="100%">
    <tr>
        <td align="center">
	        <table class="tabela_lista" width="950">
	            <tr>
	                <th class="head_tabela" width="5%">Placa</th>
	                <th class="head_tabela" width="20%">Marca</th>
	                <th class="head_tabela" width="10%">Cor</th>
	                <th class="head_tabela" width="3%">Ano</th>
	                <th class="head_tabela" width="10%">Espécie</th>
                    <th class="head_tabela" width="10%">Tipo</th>
                    <th class="head_tabela" width="10%">Categoria</th>
                    <th class="head_tabela" width="10%">Situação</th>
                    <th class="head_tabela" width="15%">Localidade</th>
                    <th class="head_tabela" width="7%">Atualização</th>
	            </tr>
	            <c:forEach var="cadastro" varStatus="linhaInfo" items="${cadastros}">
	                <tr>
	                    <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                        <td class="${css_td}" align="center">${cadastro.placa}</td>
                        <td class="${css_td}" align="center">${cadastro.marcaDisponivel}</td>
                        <td class="${css_td}" align="center">${cadastro.cor}</td>
                        <td class="${css_td}" align="center">${cadastro.ano}</td>
                        <td class="${css_td}" align="center">${cadastro.especie}</td>
                        <td class="${css_td}" align="center">${cadastro.tipo}</td>
                        <td class="${css_td}" align="center">${cadastro.categoria}</td>
                        <td class="${css_td}" align="center">${cadastro.situacao}</td>
                        <td class="${css_td}" align="center">${cadastro.localidade}</td>
	                    <td class="${css_td}" align="center"><fmt:formatDate value="${cadastro.dataAtualizacao}" type="date" pattern="dd/MM/yyyy" /></td>
	                </tr>
	            </c:forEach>
	        </table>
        </td>
    </tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
