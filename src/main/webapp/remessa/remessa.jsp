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

    Remessa remessa = Remessa.buscarRemessaPorId(Integer.parseInt(sIdRemessa));
%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="com.consilux.model.InfracaoCompleta"%>
<%@page import="com.consilux.model.InfoEspecificaInfracao"%>
<%@page import="com.consilux.model.Cadastro"%>
<%@page import="com.consilux.model.CadastroBD"%>
<%@page import="com.consilux.model.InfracaoProcesso"%>
<%@page import="com.consilux.model.InfracaoRemessa"%>
<%@page import="com.consilux.model.Remessa"%>
<%@page import="com.consilux.model.ExportaRemessa"%>
<c:set var="remessa" value="<%=remessa%>" />
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript">
	function excluir() {
		if (confirm('Deseja realmente excluir esta remessa?')) {
			$.get('/ajax/remessa/ExcluirRemessa', { id_remessa: ${remessa.idRemessa} }, function(xml){
				if (trataRetorno(xml))
				    window.close();
			});
		}
	}
	
	function exportar(idRemessa, revisao)
	{
		if(confirm('Movimento: ' + idRemessa + '; Revisao: ' + revisao + '; Deseja continuar?')) {
			window.location='/remessa/ExportarRemessa?id_remessa=${remessa.idRemessa}&tipo=ml';
			return true;
		}
		else {
			return false;
		}
	}
</script>
<table class="tabela_branca" width="600" style="height: 100%" align="center">
	<tr>
		<td align="center" valign="top">
            <table class="tabela_branca" width="100%">
                <tr>
                    <th class="head_tabela" width="100%" colspan="6">Movimento de Lote</th>
                </tr>
                <tr>
                    <td class="label_campo" width="20%">Movimento</td>
                    <td class="label_campo" width="35%">Data do Movimento</td>
                    <td class="label_campo" width="35%">Data de Validação</td>
                    <td class="label_campo" width="10%">Revisão</td>
                </tr>
                <tr>
                    <td class="visualiza_campo">${remessa.idRemessa}</td>
                    <td class="visualiza_campo"><fmt:formatDate value="${remessa.data}" type="date" pattern="dd/MM/yyyy" /></td>
                    <td class="visualiza_campo"><fmt:formatDate value="${remessa.dataValidacao}" type="date" pattern="dd/MM/yyyy" /></td>
                    <td class="visualiza_campo">${remessa.revisao}</td>
                </tr>
                <tr>
                    <td class="label_campo" width="20%">Código Externo</td>
                    <td class="label_campo" width="35%">Auto Inicial</td>
                    <td class="label_campo" width="35%">Auto Final</td>
                    <td class="label_campo" width="10%">Total de Infrações</td>
                </tr>
                <tr>
                    <td class="visualiza_campo">${remessa.codigoExterno}</td>
                    <td class="visualiza_campo">${remessa.autoInicial}</td>
                    <td class="visualiza_campo">${remessa.autoFinal}</td>
                    <td class="visualiza_campo">${remessa.totalInfracao}</td>
                </tr>
				<tr>
				<td class="box_botoes" colspan="4" width="100%">
                      	<button onclick="exportar(${remessa.idRemessa},${remessa.revisao});">Exportar Movimento de Lote</button>
                      	<c:if test="${remessa.remessaValidada}">
                      	<button onclick="window.location='/remessa/ExportarRemessa?id_remessa=${remessa.idRemessa}&tipo=lv'">Exportar Lote Validado</button>
                      	</c:if>
                      	<button onclick="window.location='/remessa/PREM?id_remessa=${remessa.idRemessa}'">PREM</button>
                      	<button onclick="window.location='/remessa/EtiquetaRemessa?id_remessa=${remessa.idRemessa}'">Etiqueta</button>
                      	<button onclick="excluir()">Excluir</button>
				</td>
				</tr>
            </table>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>