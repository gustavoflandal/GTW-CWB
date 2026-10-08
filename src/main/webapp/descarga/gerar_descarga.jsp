<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<%@page import="com.consilux.infra.beans.Tuple"%>
<%@page import="com.consilux.model.descarga.Descarga"%>
<%@page import="java.util.Date"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%!
	private Logger logger = Logger.getLogger(Descarga.class); 
%>
<br />
<br />
<%
	long tamanhoMidia = ConfiguracaoProvider.getInstance().getConfiguracaoDescarga().getTamanhoMidia();
	Date dataIni;
	Date dataFim;
	
	try {
		Tuple<Date,Date> periodoDescarga = Descarga.buscarProximoPeriodoDescarga(tamanhoMidia);
		dataIni = periodoDescarga.getKey();
		dataFim = periodoDescarga.getValue();
	}
	catch (Exception ex) {
		new Mensagem(response).showErro("Não foi possível buscar as datas para a geração de descarga!");
		logger.error("Erro ao buscar as datas da descarga: "+ex.getMessage(), ex);
		return;
	}
%>
<c:set var="dataIni" value="<%=dataIni%>" />
<c:set var="dataFim" value="<%=dataFim%>" />
<c:set var="tamanhoMidia" value="<%=tamanhoMidia%>" />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_descarga" action="/descarga/GerarDescarga" method="get">
				<table class="tabela_branca" width="250">
					<tr>
						<th class="head_tabela" width="100%" colspan="3">Gerar Descarga</th>
					</tr>
					<tr>
						<td class="label_campo" width="40%">Início:</td>
                        <td class="valor_campo">
                            <input type="hidden" name="dataini" value='<fmt:formatDate pattern="dd/MM/yyyy" value="${dataIni}" />'>
                            <fmt:formatDate pattern="dd/MM/yyyy" value="${dataIni}" />
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Até</td>
                        <td class="valor_campo">
                            <input type="hidden" name="datafim" value='<fmt:formatDate pattern="dd/MM/yyyy" value="${dataFim}" />'>
                            <fmt:formatDate pattern="dd/MM/yyyy" value="${dataFim}" />
                        </td>
                    </tr>
					<tr>
						<td class="box_botoes" colspan="5" width="100%">
							<button id="btEnvio" onclick="this.submit()">Gerar</button>
						</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
