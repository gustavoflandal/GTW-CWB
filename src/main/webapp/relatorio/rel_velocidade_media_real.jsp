<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
	String sLocal = request.getParameter("local");
	if (sLocal == null || !Pattern.matches("[1-9][0-9]{0,7}",sLocal)) {
		new Mensagem(response).showErro("Local enviado inválido!");
		return;
	}
	
	GregorianCalendar dtFim = new GregorianCalendar();
	GregorianCalendar dtIni = new GregorianCalendar();
	dtIni.set(Calendar.HOUR, dtFim.get(Calendar.HOUR)-3);
	
	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("local",new Integer(sLocal));
	mFiltro.put("datahora_ini",new Timestamp(dtIni.getTimeInMillis()));
	mFiltro.put("datahora_fim",new Timestamp(dtFim.getTimeInMillis()));
	List<LocalEstatistica> estats = LocalEstatistica.buscaLocalEstatisticaPor(mFiltro,"3");
	
	if (estats.size() == 0) {
		new Mensagem(response).showErro("Local não disponível no momento.");
		return;
	}
	
	XYSeries dadosGrafico = new XYSeries("Velocidade");

	long lOffSet = (dtIni.getTimeInMillis()/60000L);
	for(LocalEstatistica le: estats) {
		for (long l=lOffSet+1; l<(le.getDataHora().getTime()/60000L); l++) {
			dadosGrafico.add((l*60000L),null);
		}
		dadosGrafico.add(le.getDataHora().getTime(),le.getVelMed());
		lOffSet = (le.getDataHora().getTime()/60000L);
	}

	GraficoLinhaTempo grafico = new GraficoLinhaTempo(session, out);
	grafico.addSeries(dadosGrafico);
	grafico.setLargura(1000);
	grafico.setAltura(300);
	grafico.setLegendaX("Horário");
	grafico.setLegendaY("Velocidade (km/h)");
	
	DecimalFormat df = new DecimalFormat("#.# km/h");
	DecimalFormatSymbols dfs = new DecimalFormatSymbols();
	dfs.setDecimalSeparator('.');
	df.setDecimalFormatSymbols(dfs);
	grafico.setNumberFormat(df);
	grafico.setDateFormat(new SimpleDateFormat("HH:mm"));

	String url = "";
	String map = "";
	String sFiltro = "";
	
	LocalVigente local = LocalVigente.buscaLocalVigentePorIdLocal(Integer.valueOf(sLocal));
	DateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	if (local != null)
		sFiltro += "Local: "+sLocal+" - "+local.getNome()+"<BR>";
	
	sFiltro += "Periodo: "+fmt.format(dtIni.getTime())+" até "+fmt.format(dtFim.getTime());

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
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="com.consilux.model.LocalEstatistica"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.lib.exception.GraficoException"%><br />
<br />
<script type="text/javascript">
	atuRef = window.setTimeout("window.location.reload(true);",60000);
</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="770">
				<tr>
					<th class="head_tabela" width="5%">Velocidade média dos veículos em tempo real</th>
				</tr>
				<tr>
					<td class="corpo_mensagem" align="center"><%=sFiltro%></td>
				</tr>
				<tr>
					<td class="corpo_mensagem" align="center"><img src="<%=url%>" width=1000 height=300 border=0 usemap="#<%=map%>"></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
