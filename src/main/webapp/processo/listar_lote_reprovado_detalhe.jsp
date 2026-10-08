<%@page import="java.util.List"%>
<%@page import="com.consilux.model.LoteReprovadoDetalhe"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Remessa"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	String sIdRemessa = request.getParameter("id_remessa") != null ? request.getParameter("id_remessa").trim() : null;
	sIdRemessa = sIdRemessa != null && sIdRemessa.length() == 0 ? null : sIdRemessa;

	if (sIdRemessa == null || !Pattern.matches("[1-9][0-9]{0,7}",sIdRemessa)) {
        new MensagemJS(response).showErro("Identificador de remessa enviado inválido!");
        return;
    }

	Remessa remessa = null; 
	List<LoteReprovadoDetalhe> lista = null;
	int registros = 0;
	
	if (sIdRemessa != null)
	{
    	remessa = Remessa.buscarRemessaPorId(Integer.parseInt(sIdRemessa));
    	lista = LoteReprovadoDetalhe.ObterDetalhe(remessa.getIdRemessa());
    	registros = lista.size();
	}
%>
<c:set var="remessa" 	value="<%=remessa%>" />
<c:set var="lista" 		value="<%=lista%>" />
<c:set var="registros" 	value="<%=registros%>" />

<script type="text/javascript">
</script>

<table class="tabela_branca" width="600" style="height: 100%" align="center">
	<tr>
		<td align="center" valign="top">
            <table class="tabela_branca" width="100%">
                <tr>
                    <th class="head_tabela" width="100%" colspan="6">Detalhes do Lote Reprovado</th>
                </tr>
                
                <c:if test="${registros == 0}">
                <tr>
                    <th class="head_tabela" width="100%" colspan="6">Não existem detalhes para este Lote Reprovado</th>
                </tr>
                </c:if>
                
                <c:if test="${registros > 0}">
                
                <c:forEach var="r" items="<%=lista%>">
                
                <tr>
                    <td class="label_campo">ID Infração</td>
                    <td class="label_campo" colspan="2">Nome do Auditor</td>
                    <td class="label_campo">Erro de Obliteração</td>
                </tr>
                <tr>
                    <td class="visualiza_campo">${r.idInfracao}</td>
                    <td class="visualiza_campo" colspan="2">${r.nomeAgente}</td>
                    <td class="visualiza_campo">${r.erroObliteracao}</td>
                </tr>
                
                <tr>
                    <td class="label_campo" colspan="2">Inconsistencia CAI</td>
                    <td class="label_campo" colspan="2">Inconsistencia CAV</td>
                </tr>
                <tr>
                    <td class="visualiza_campo" colspan="2">${r.inconsistenciaCAI}</td>
                    <td class="visualiza_campo" colspan="2">${r.inconsistenciaCAV}</td>
                </tr>
                <tr>
                	<td class="label_campo">Placa CAI</td>
                    <td class="label_campo">Placa CAV</td>
                    <td class="label_campo">Marca CAI</td>
                    <td class="label_campo">Marca CAV</td>
                    
                </tr>
                <tr>
                    <td class="visualiza_campo">${r.placaCAI}</td>
                    <td class="visualiza_campo">${r.placaCAV}</td>
                    <td class="visualiza_campo">${r.marcaCAI}</td>
                    <td class="visualiza_campo">${r.marcaCAV}</td>
                </tr>
                
                </c:forEach>
                
                </c:if>
                
                
            </table>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>