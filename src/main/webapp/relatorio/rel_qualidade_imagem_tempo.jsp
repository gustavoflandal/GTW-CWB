<%@page import="java.text.NumberFormat"%>
<%@page import="com.consilux.model.relatorio.QualidadeImagemTempo"%>
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
	
	if (sLocal == null || !Pattern.matches("[1-9][0-9]{0,3}",sLocal)) {
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
	
	RelatorioItr<QualidadeImagemTempo> listaQualidadeImagemTempo = new RelatorioItr<QualidadeImagemTempo>(
		new QualidadeImagemTempo(Integer.parseInt(sLocal),
		pista,
		new Timestamp(dtIni.getTime()),
		new Timestamp(dtFim.getTime())
	)
		);
	
    Map<Byte,XYSeries> mapSeriesImagemDef = new HashMap<Byte,XYSeries>(); 
    Map<Byte,XYSeries> mapSeriesPercNaoRec = new HashMap<Byte,XYSeries>(); 
    Map<Byte,XYSeries> mapSeriesQtdTesteDef = new HashMap<Byte,XYSeries>(); 
    Map<Byte,XYSeries> mapSeriesQtdTesteLap = new HashMap<Byte,XYSeries>(); 
    for(QualidadeImagemTempo item: listaQualidadeImagemTempo) {
        XYSeries dadosGraficoPercNaoRec = mapSeriesPercNaoRec.get(item.getPista());
        XYSeries dadosGraficoImagemDef = mapSeriesImagemDef.get(item.getPista());
        XYSeries dadosGraficoQtdTesteDef = mapSeriesQtdTesteDef.get(item.getPista());
        XYSeries dadosGraficoQtdTesteLap = mapSeriesQtdTesteLap.get(item.getPista());
        if (dadosGraficoImagemDef == null) {
            dadosGraficoImagemDef = new XYSeries("Pista "+item.getPista());
            dadosGraficoPercNaoRec = new XYSeries("Pista "+item.getPista());
            dadosGraficoQtdTesteDef = new XYSeries("Pista "+item.getPista());
            dadosGraficoQtdTesteLap = new XYSeries("Pista "+item.getPista());
            mapSeriesImagemDef.put(item.getPista(),dadosGraficoImagemDef);
            mapSeriesPercNaoRec.put(item.getPista(),dadosGraficoPercNaoRec);
            mapSeriesQtdTesteDef.put(item.getPista(),dadosGraficoQtdTesteDef);
            mapSeriesQtdTesteLap.put(item.getPista(),dadosGraficoQtdTesteLap);
        }
        dadosGraficoImagemDef.add(
                new GregorianCalendar(
                        item.getAno(),
                        item.getMes()-1,
                        item.getDia(),
                        item.getHora(),
                        0
                    ).getTimeInMillis(),item.getPercentualImagemDefeituosa() != null ? item.getPercentualImagemDefeituosa()*100 : 0
            );
        dadosGraficoPercNaoRec.add(
                new GregorianCalendar(
                        item.getAno(),
                        item.getMes()-1,
                        item.getDia(),
                        item.getHora(),
                        0
                    ).getTimeInMillis(),item.getPercentualNaoReconhecimento() != null ? item.getPercentualNaoReconhecimento()*100 : 0
            );
        dadosGraficoQtdTesteDef.add(
                new GregorianCalendar(
                        item.getAno(),
                        item.getMes()-1,
                        item.getDia(),
                        item.getHora(),
                        0
                    ).getTimeInMillis(),item.getQuantidadeVeiculosTesteDefeituosa()
            );
        dadosGraficoQtdTesteLap.add(
                new GregorianCalendar(
                        item.getAno(),
                        item.getMes()-1,
                        item.getDia(),
                        item.getHora(),
                        0
                    ).getTimeInMillis(),item.getQuantidadeVeiculosTesteLap()
            );
    }
    
    GraficoLinhaTempo grafico = new GraficoLinhaTempo(session, out);
	grafico.addLegendaY("Imagens defeituosas (%)");
	grafico.addValorMaximo(100d);
    for(XYSeries serie: mapSeriesImagemDef.values()) {
        grafico.addSeries(serie);
    }
	grafico.criaNovoDataSet();
	grafico.addLegendaY("Placas não reconhecidas (%)");
	grafico.addValorMaximo(100d);
    for(XYSeries serie: mapSeriesPercNaoRec.values()) {
        grafico.addSeries(serie);
    }
	grafico.criaNovoDataSet();
	grafico.addLegendaY("Imagens efetuado teste de defeitusa");
	
    for(XYSeries serie: mapSeriesQtdTesteDef.values()) {
        grafico.addSeries(serie);
    }
	grafico.criaNovoDataSet();
	grafico.addLegendaY("Imagens efetuado teste de LAP");
    for(XYSeries serie: mapSeriesQtdTesteLap.values()) {
        grafico.addSeries(serie);
    }
    
	grafico.setLargura(1000);
	grafico.setAltura(1200);
	grafico.setLegendaX("Horário");
	grafico.setValorMaximo(105d);

	
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
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.lib.exception.GraficoException"%>
<%@page import="com.consilux.infra.RelatorioItr"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%><br />
<c:set var="grafico" value="<%=grafico%>" scope="request"/>
<c:set var="series" value="<%=mapSeriesImagemDef%>" scope="request"/>
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="770">
				<tr>
					<th class="head_tabela" width="5%">Qualidade de Imagem no Período</th>
				</tr>
				<tr>
					<td class="corpo_mensagem" align="center"><%=sFiltro%></td>
				</tr>
				<tr>
					<td class="corpo_mensagem" align="center"><img src="<%=url%>" width=1000 height=1200 border=0 usemap="#<%=map%>"></td>
				</tr>
			</table>
            <jsp:include page="/WEB-INF/templates/relatorio/list_dados_grafico_tempo.jsp" />
		</td>
	</tr>
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
