<%@page import="com.consilux.model.relatorio.RelatorioProdutividadeDetalhado"%>
<%@page import="java.util.Calendar"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%
	String sIdUsuario = request.getParameter("usuario");
	String sJanelaMinutos = request.getParameter("janela_minutos");
	
	if (sIdUsuario == null || !ExpValida.NATURAL_COM_ZERO.validar(sIdUsuario)) {
	    new Mensagem(response).showErro("Usuário enviado inválido!");
	    return;
	}
	else if (sJanelaMinutos == null || !ExpValida.NATURAL.validar(sJanelaMinutos)) {
	    new Mensagem(response).showErro("Janela de minutos enviada inválida!");
	    return;
	}
	    
	Integer janelaMinutos = Integer.parseInt(sJanelaMinutos);
	Calendar calBase = Calendar.getInstance();
	calBase.add(Calendar.MINUTE, janelaMinutos*-1);
	
	Integer idUsuario;
	if (Integer.valueOf(sIdUsuario) > 0)
		idUsuario = Integer.valueOf(sIdUsuario);
	else
		idUsuario = null;

	List<RelatorioProdutividadeDetalhado> lrpo = RelatorioProdutividadeDetalhado.buscaRelatorio(calBase.getTime(), idUsuario);
%>
<%@page import="com.consilux.model.Acesso"%>

<%@page import="com.consilux.infra.ExpValida"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%><c:set var="lrpo" value="<%=lrpo%>" />
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
		<table class="tabela_branca" width="800">
			<tr>
				<th class="head_tabela" width="100%" colspan="11">Relatório de Produtividade Detalhado</th>
			</tr>
				<tr align="center" >
					<th class="head_tabela" align="center" width="5%">Hora</th>
					<th class="head_tabela" align="center" width="5%">Minuto</th>
					<th class="head_tabela" align="center" width="10%">Enquadramento</th>
					<th class="head_tabela" align="center" width="15%">Processo</th>
					<th class="head_tabela" align="center" width="10%">Usuário</th>
					<th class="head_tabela" align="center" width="10%">IP</th>
					<th class="head_tabela" align="center" width="5%">Qtd. Img</th>
					<th class="head_tabela" align="center" width="10%">Med. Tempo Sis.</th>
					<th class="head_tabela" align="center" width="10%">Med. Tempo Usu.</th>
					<th class="head_tabela" align="center" width="10%">Max. Tempo Usu.</th>
					<th class="head_tabela" align="center" width="10%">Min. Tempo Usu.</th>
				</tr>
				<c:forEach var="rip" varStatus="linhaInfo" items="${lrpo}">
				<tr align="center" >
					<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
					<td class="${css_td}" align="center">${rip.hora}</td>
					<td class="${css_td}" align="center">${rip.minuto}</td>
					<td class="${css_td}" align="center">${rip.idEnquadramento}</td>
					<td class="${css_td}" align="center">${rip.processo}</td>
					<td class="${css_td}" align="center">${rip.usuario}</td>
					<td class="${css_td}" align="center">${rip.ip}</td>
					<td class="${css_td}" align="center">${rip.qtdImagens}</td>
					<td class="${css_td}" align="center">${rip.mediaTempoSistema}</td>
					<td class="${css_td}" align="center">${rip.mediaTempoUsuario}</td>
					<td class="${css_td}" align="center">${rip.maiorTempoUsuario}</td>
					<td class="${css_td}" align="center">${rip.menorTempoUsuario}</td>
				</tr>
				</c:forEach>
		</table>
		</td>
	</tr>
</table>

<%@ include file="/includes/rodape.jsp"%>
