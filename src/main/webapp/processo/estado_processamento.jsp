<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	String sLocal = request.getParameter("local");
	String sDataIni = request.getParameter("dataini");
	String sHoraIni = request.getParameter("horaini");
	String sDataFim = request.getParameter("datafim");
	String sHoraFim = request.getParameter("horafim");
	
	if (sLocal == null || !Pattern.matches("[0-9]{0,8}",sLocal)) {
	    new Mensagem(response).showErro("Local enviado inválido!");
	    return;
	}
	else if (sDataIni == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataIni)) {
	    new Mensagem(response).showErro("Data inicial enviada inválida!");
	    return;
	}
	else if (sHoraIni == null || !Pattern.matches("([01][0-9]|2[0-3]):([0-5][0-9])",sHoraIni)) {
	    new Mensagem(response).showErro("Hora inicial enviada inválida!");
	    return;
	}
	else if (sDataFim == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataFim)) {
	    new Mensagem(response).showErro("Data final enviada inválida!");
	    return;
	}
	else if (sHoraFim == null || !Pattern.matches("([0-1][0-9]|2[0-3]):([0-5][0-9])",sHoraFim)) {
	    new Mensagem(response).showErro("Hora final enviada inválida!");
	    return;
	}
	    
	Date dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataIni+" "+sHoraIni+":00");
	Date dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataFim+" "+sHoraFim+":59");
	
	List<EstadoProcessamento> estado_processos = EstadoProcessamento.buscaEstadoProcessamento(Integer.parseInt(sLocal), dtIni, dtFim);

	List<JanelaProcessamento> janela_processos = JanelaProcessamento.buscaJanelaProcessamento(Integer.parseInt(sLocal), dtIni, dtFim);
	String sFiltro = "";
	
	LocalVigente local = LocalVigente.buscaLocalVigentePorIdLocal(Integer.valueOf(sLocal));
	
	if (local != null)
		sFiltro += "Local: "+sLocal+" - "+local.getNome()+"<BR>";
    
	sFiltro += "Período: "+sDataIni+" "+sHoraIni+" até "+sDataFim+" "+sHoraFim;

%>
<%@page import="org.jfree.data.xy.XYSeries"%>
<%@page import="com.consilux.infra.GraficoLinhaTempo"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.model.relatorio.FluxoTempo"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.lib.exception.GraficoException"%>
<%@page import="com.consilux.infra.RelatorioItr"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>

<%@page import="com.consilux.model.relatorio.MotivoConsistenteInconsitente"%>
<%@page import="java.util.SortedMap"%>
<%@page import="java.util.TreeMap"%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="com.consilux.model.Inconsistencia"%>

<%@page import="com.consilux.model.relatorio.TotalConsistenteInconsitente"%>
<%@page import="com.consilux.model.relatorio.TotalConsistenteInconsitente.Tipo"%>


<%@page import="com.consilux.model.EstadoProcessamento"%>

<%@page import="com.consilux.model.JanelaProcessamento"%>
<c:set var="estado_processos" value="<%=estado_processos%>" scope="request"/>
<c:set var="janela_processos" value="<%=janela_processos%>" scope="request"/>
<script type="text/javascript">
	function mostraDetalhesInfracao(idInfracao) {
		window.open("/infracao/infracao_completa.jsp?id_infracao="+idInfracao,"Detalhes","width=1024, height=500");
	}
</script>
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
            <table class="tabela_lista" width="770">
                <tr>
                    <th class="head_tabela">Estado Atual do Processamento</th>
                </tr>
                <tr>
                    <td class="corpo_mensagem" align="center"><%=sFiltro%></td>
                </tr>
                <tr>
                    <td class="corpo_mensagem" align="center" width="100%">
			            <table class="tabela_lista" width="100%">
			                <tr>
			                    <th class="head_tabela" width="25%">Processo</th>
			                    <th class="head_tabela" width="15%">Número de Infrações</th>
			                    <th class="head_tabela" width="15%">Primeira Infração</th>
			                    <th class="head_tabela" width="15%">Data</th>
			                    <th class="head_tabela" width="15%">Última Infração</th>
			                    <th class="head_tabela" width="15%">Data</th>
			                </tr>
			                <c:forEach var="estado_processo" varStatus="linhaInfo" items="${estado_processos}">
			                    <tr>
			                        <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
			                        <td class="${css_td}" align="center">${estado_processo.descricaoProcesso}</td>
                                    <td class="${css_td}" align="center">${estado_processo.conta}</td>
                                    <td class="${css_td}" align="center">${estado_processo.idInfracaoInicial}&nbsp;<a class='link_td' href="javascript:mostraDetalhesInfracao(${estado_processo.idInfracaoInicial})">[...]</a></td>
                                    <td class="${css_td}" align="center"><fmt:formatDate value="${estado_processo.dataInicial}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
                                    <td class="${css_td}" align="center">${estado_processo.idInfracaoFinal}&nbsp;<a class='link_td' href="javascript:mostraDetalhesInfracao(${estado_processo.idInfracaoFinal})">[...]</a></td>
                                    <td class="${css_td}" align="center"><fmt:formatDate value="${estado_processo.dataFinal}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
			                    </tr>
			                </c:forEach>
			            </table>
                    </td>
                </tr>
                <tr>
                    <td class="corpo_mensagem" align="center" width="100%">
			            <table class="tabela_lista" width="100%">
			                <tr>
			                    <th class="head_tabela" width="30%">Usuário Atual</th>
			                    <th class="head_tabela" width="50%">Processo</th>
			                    <th class="head_tabela" width="10%">Nº Infrações</th>
			                    <th class="head_tabela" width="10%">Ação</th>
			                </tr>

			                <c:forEach var="janela_processo" varStatus="linhaInfo" items="${janela_processos}">
			                    <tr>
			                        <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
			                        <td class="${css_td}" align="center">${janela_processo.usuario}</td>
                                    <td class="${css_td}" align="center">${janela_processo.nomeProcesso}</td>
                                    <td class="${css_td}" align="center">${janela_processo.conta}</td>
                                    <td class="${css_td}" align="center"><a class='link_td' href="/FinalizarJanelaProcessamento?id_usuario=${janela_processo.idUsuario}&id_processo=${janela_processo.idProcesso}">Limpar Janela</a></td>
			                    </tr>
			                </c:forEach>
			            </table>
                    </td>
                </tr>
            </table>
		</td>
	</tr>
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
