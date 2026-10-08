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
	
	RelatorioItr<FluxoVelocidade> listaContagemVelociade = new RelatorioItr<FluxoVelocidade>(
				new FluxoVelocidade(Integer.parseInt(sLocal),
				pista,
				new Timestamp(dtIni.getTime()),
				new Timestamp(dtFim.getTime())
			)
		);

	DefaultCategoryDataset dadosGrafico = new DefaultCategoryDataset();
	for(FluxoVelocidade item: listaContagemVelociade) {
		dadosGrafico.addValue(item.getConta(),"Veiculos",(item.getVelocidade()*10)+" - "+((item.getVelocidade()+1)*10));
	}

	GraficoBarra grafico = new GraficoBarra(session, out);
	grafico.setDataset(dadosGrafico);
	grafico.setLargura(1000);
	grafico.setAltura(400);
	grafico.setLegendaX("Velocidade (km/h)");
	grafico.setLegendaY("Quantidade de veículos");
	
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
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="org.jfree.data.category.DefaultCategoryDataset"%>
<%@page import="com.consilux.infra.GraficoBarra"%>
<%@page import="com.consilux.model.relatorio.FluxoVelocidade"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.lib.exception.GraficoException"%>
<%@page import="com.consilux.infra.RelatorioItr"%>
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="770">
				<tr>
					<th class="head_tabela" width="5%">Fluxo de veículos por Faixa de Velocidade</th>
				</tr>
				<tr>
					<td class="corpo_mensagem" align="center"><%=sFiltro%></td>
				</tr>
				<tr>
					<td class="corpo_mensagem" align="center"><img src="<%=url%>" width=1000 height=400 border=0 usemap="#<%=map%>"></td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
