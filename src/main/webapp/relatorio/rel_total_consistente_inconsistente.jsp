<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
	String sLocal = request.getParameter("local");
	String sPista = request.getParameter("pista");
	String sDataIni = request.getParameter("dataini");
	String sHoraIni = request.getParameter("horaini");
	String sDataFim = request.getParameter("datafim");
	String sHoraFim = request.getParameter("horafim");
	String sProcessoConcluido = request.getParameter("processo_concluido");
	
	
	if (sLocal == null || !Pattern.matches("[0-9]{0,8}",sLocal)) {
	    new Mensagem(response).showErro("Local enviado inválido!");
	    return;
	}
	else if (sPista == null || !Pattern.matches("[0-9]",sPista)) {
	    new Mensagem(response).showErro("Pista enviada inválida!");
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
	else if (sProcessoConcluido == null || !Pattern.matches("[0-1]",sProcessoConcluido)) {
	    new Mensagem(response).showErro("Processo concluído enviada inválida!");
	    return;
	}
	    
	Date dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataIni+" "+sHoraIni+":00");
	Date dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataFim+" "+sHoraFim+":59");
	
	Integer pista;
	if (Integer.valueOf(sPista) > 0)
	    pista = Integer.valueOf(sPista);
	else
	    pista = null;

	Boolean processoConcluido;
    processoConcluido = "1".equals( sProcessoConcluido );
	
	RelatorioItr<TotalConsistenteInconsitente> listaTotalConsistenteInconsistente = new RelatorioItr<TotalConsistenteInconsitente>(
				new TotalConsistenteInconsitente(
						Integer.parseInt(sLocal),
						pista,
						new Timestamp(dtIni.getTime()),
						new Timestamp(dtFim.getTime()),
						processoConcluido 
				)
		);

    Map<Integer, String> mapLegendaRelatorio = new TreeMap<Integer, String>();
    
    Map<Tipo, Map<Integer, Integer>> mapDadosRelatorio = new TreeMap<Tipo, Map<Integer, Integer>>();
    Map<Integer, Integer> mapItem = null;
    Tipo cAnt = null;
    
    for(TotalConsistenteInconsitente item: listaTotalConsistenteInconsistente) {
    	if (!mapLegendaRelatorio.containsKey(item.getIdEnquadramento())) {
    		mapLegendaRelatorio.put(item.getIdEnquadramento(), Enquadramento.buscaEnquadramentosPorId(item.getIdEnquadramento()).getDescricao());
    	}
        if (cAnt != item.getTipo()) {
            mapItem = new HashMap<Integer, Integer>();
            
            mapDadosRelatorio.put(
            		item.getTipo(),
                    mapItem
                );
            cAnt = item.getTipo();
        }
        mapItem.put(item.getIdEnquadramento(), item.getQuantidade());
    }
	
	String map = "";
	String sFiltro = "";
	
	LocalVigente local = LocalVigente.buscaLocalVigentePorIdLocal(Integer.valueOf(sLocal));
	
	if (local != null)
		sFiltro += "Local: "+sLocal+" - "+local.getNome()+"<BR>";
    
    if (pista != null)
        sFiltro += "Pista: "+sPista+"<BR>";
	
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

<c:set var="legendaRelatorio" value="<%=mapLegendaRelatorio%>" scope="request"/>
<c:set var="contaLegendaRelatorio" value="<%=mapLegendaRelatorio.size()%>" scope="request"/>
<c:set var="dadosRelatorio" value="<%=mapDadosRelatorio%>" scope="request"/>
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
            <table class="tabela_lista" width="770">
                <tr>
                    <th class="head_tabela">Infrações Consistentes e Inconsistentes</th>
                </tr>
                <tr>
                    <td class="corpo_mensagem" align="center"><%=sFiltro%></td>
                </tr>
                <tr>
                    <td class="corpo_mensagem" align="center" width="100%">
			            <table class="tabela_grid" width="100%">
			                <tr>
			                    <td class="dado_lista_tabela_grid" align="center">
			                        CONSISTENTE-INCONSISTENTE \ ENQUADRAMENTO
			                    </td>
			                    <c:forEach var="legenda" items="${legendaRelatorio}">
			                        <td class="dado_lista_tabela_grid" align="center">
			                            &nbsp;${legenda.key}&nbsp;
			                        </td>
			                    </c:forEach>
                                <td class="dado_lista_tabela_grid" align="center">
                                    &nbsp;TOTAIS&nbsp;
                                </td>
			                </tr>
			                <c:forEach var="linha" items="${dadosRelatorio}">
			                    <tr>
			                        <td class="dado_lista_tabela_grid" align="left">
			                            ${linha.key}
			                        </td>
                                    <c:set var="tot_legenda" value="0"></c:set>
			                        <c:forEach var="legenda" items="${legendaRelatorio}">
                                        <c:set var="tot_legenda" value="${tot_legenda+linha.value[legenda.key]}"></c:set>
			                            <td class="dado_lista_tabela_grid" align="center">
			                                <c:choose>
			                                    <c:when test="${linha.value[legenda.key] > 0}">${linha.value[legenda.key]}</c:when>
			                                    <c:otherwise>0</c:otherwise>
			                                </c:choose>
			                            </td>
			                        </c:forEach>
                                    <td class="dado_lista_tabela_grid" align="center">
                                        ${tot_legenda}
                                    </td>
			                    </tr>
			                </c:forEach>
			            </table>
                        <br>
                        <table class="tabela_branca">
                            <tr>
                                <td class="dado_lista_tabela_claro" align="left" width="100%">Legenda:</td>
                            </tr>
                            <c:forEach var="legenda" items="${legendaRelatorio}">
                                <tr>
                                    <td class="dado_lista_tabela_claro" align="left" width="100%">${legenda.key} - ${legenda.value}</td>
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
