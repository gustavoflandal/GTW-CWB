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
	
	if (sLocal == null || !Pattern.matches("[1-9][0-9]{0,7}",sLocal)) {
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
	    
	Date dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataIni+" "+sHoraIni+":00");
	Date dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataFim+" "+sHoraFim+":59");
	
	Integer pista;
	if (Integer.valueOf(sPista) > 0)
	    pista = Integer.valueOf(sPista);
	else
	    pista = null;
	
	RelatorioItr<FluxoTempo> listaFluxoTempo = new RelatorioItr<FluxoTempo>(
				new FluxoTempo(Integer.parseInt(sLocal),
				pista,
				new Timestamp(dtIni.getTime()),
				new Timestamp(dtFim.getTime())
			)
		);

	FluxoTempoBean ftb;
	List<FluxoTempoBean> lftb = new ArrayList<FluxoTempoBean>();
	
    Map<Byte,XYSeries> mapSeries = new HashMap<Byte,XYSeries>(); 
    for(FluxoTempo item: listaFluxoTempo) {
    	ftb = new FluxoTempoBean();
		ftb.setAno(item.getAno());
		ftb.setConta(item.getConta());
		ftb.setDia(item.getDia());
		ftb.setHora(item.getHora());
		ftb.setMes(item.getMes());
		ftb.setPista(item.getPista());
		lftb.add(ftb);
		
        XYSeries dadosGrafico = mapSeries.get(item.getPista());
        if (dadosGrafico == null) {
	        dadosGrafico = new XYSeries("Pista "+item.getPista());
	        mapSeries.put(item.getPista(),dadosGrafico);
        }
        dadosGrafico.add(
                new GregorianCalendar(
                        item.getAno(),
                        item.getMes()-1,
                        item.getDia(),
                        item.getHora()
                        ,0
                    ).getTimeInMillis(),item.getConta()
            );
    }
    
    FluxoTempoBean.setListaParaRelatorio(lftb);
    
    GraficoLinhaTempo grafico = new GraficoLinhaTempo(session, out);
    for(XYSeries serie: mapSeries.values()) {
        grafico.addSeries(serie);
    }
	
	grafico.setLargura(1000);
	grafico.setAltura(300);
	grafico.setLegendaX("Horário");
	grafico.setLegendaY("Veículos");
//	grafico.setDateFormat(new SimpleDateFormat("H'h'"));
	
	String url = "";
	String map = "";
	String sFiltro = "";
	
	LocalVigente local = LocalVigente.buscaLocalVigentePorIdLocal(Integer.valueOf(sLocal));
	
	if (local != null)
		sFiltro += "Local: "+sLocal+" - "+local.getNome()+"<BR>";
    
    if (pista != null)
        sFiltro += "Pista: "+sPista+"<BR>";
	
	sFiltro += "Periodo: "+sDataIni+" "+sHoraIni+" até "+sDataFim+" "+sHoraFim;

	try {
		grafico.gerar();
		map = grafico.dumpImageMap();
		url = "/graficos/MostraGrafico?filename="+grafico.getArquivo();
	}
	catch(GraficoException e) {
		url = "/images/grafico_vazio.png";
	}
	
	
    
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

<%@page import="com.consilux.exportalista.FluxoTempoBean"%><c:set var="grafico" value="<%=grafico%>" scope="request"/>
<c:set var="series" value="<%=mapSeries%>" scope="request"/>
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="770">
				<tr>
					<th class="head_tabela" width="5%">Fluxo de veículo no período</th>
				</tr>
				<tr>
					<td class="corpo_mensagem" align="center"><%=sFiltro%></td>
				</tr>
				<tr>
					<td class="corpo_mensagem" align="center"><img src="<%=url%>" width=1000 height=300 border=0 usemap="#<%=map%>"></td>
				</tr>
			</table>
			<br/>
			<form name=FormExportar" action="/relatorio/ExportarPlanilhaFluxoXTempo" method="get">
				<input type="hidden" name ="urlImagem" value="<%=url%>"/>
				<input type="hidden" name ="local" value="<%=local.getIdLocal()+" "+local.getNome()%>}"/>
				<!-- <button id="btEnvio" onclick="enviar('btEnvio','frm_grafic','div_mens');">Visualizar</button> -->
				<button id="btEnvio" onclick="javascript:form.submit();">Exportar Planilha</button>
			</form>
            <jsp:include page="/WEB-INF/templates/relatorio/list_dados_grafico_tempo.jsp" />
		</td>
	</tr>
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
