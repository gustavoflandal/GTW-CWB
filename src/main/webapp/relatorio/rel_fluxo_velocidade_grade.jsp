<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	String sLocal = request.getParameter("local");
	String sPista = request.getParameter("pista");
	String sTipo = request.getParameter("tipo");
	String sAno = request.getParameter("ano");
	String sMes = request.getParameter("mes");
	String sDia = request.getParameter("dia");
	String sHora = request.getParameter("hora");

	if (sLocal == null || !Pattern.matches("[1-9][0-9]{0,7}",sLocal)) {
		new Mensagem(response).showErro("Local enviado invalido!");
		return;
	}
	else if (sPista == null || !Pattern.matches("[0-4]",sPista)) {
		new Mensagem(response).showErro("Pista enviada invalida!");
		return;
	}
	else if (sTipo == null || !Pattern.matches("[0-5]",sTipo)) {
		new Mensagem(response).showErro("Agrupamento enviado invalido!");
		return;
	}
	else if (sTipo.compareTo(""+FluxoVelocidadeGrade.Tipo.MES.ordinal()) == 0 && 
	(sAno == null || !Pattern.matches("[12][0-9]{3}",sAno))
	) {
		new Mensagem(response).showErro("Dados da database enviados invalidos!");
		return;
	}
	else if (sTipo.compareTo(""+FluxoVelocidadeGrade.Tipo.DIA.ordinal()) == 0 && 
	(sAno == null || !Pattern.matches("[12][0-9]{3}",sAno) ||
	 sMes == null || !Pattern.matches("[0-9]|[1][01]",sMes))
	) {
		new Mensagem(response).showErro("Dados da database enviados invalidos!");
		return;
	}
	else if (sTipo.compareTo(""+FluxoVelocidadeGrade.Tipo.HORA.ordinal()) == 0 && 
	(sAno == null || !Pattern.matches("[12][0-9]{3}",sAno) ||
	 sMes == null || !Pattern.matches("[0-9]|[1][01]",sMes) ||
	 sDia == null || !Pattern.matches("[1-9]|[12][0-9]|[3][01]",sDia))
	) {
		new Mensagem(response).showErro("Dados da database enviados invalidos!");
		return;
	}
	else if (sTipo.compareTo(""+FluxoVelocidadeGrade.Tipo.QUINZE.ordinal()) == 0 && 
			(sAno == null || !Pattern.matches("[12][0-9]{3}",sAno) ||
			 sMes == null || !Pattern.matches("[0-9]|[1][01]",sMes) ||
			 sDia == null || !Pattern.matches("[1-9]|[12][0-9]|[3][01]",sDia) ||
			 sHora == null || !Pattern.matches("[0-9]|[1][0-9]|[2][0-3]",sHora))
			) {
				new Mensagem(response).showErro("Dados da database enviados invalidos!");
				return;
			}
	else if (sTipo.compareTo(""+FluxoVelocidadeGrade.Tipo.MINUTO.ordinal()) == 0 && 
	(sAno == null || !Pattern.matches("[12][0-9]{3}",sAno) ||
	 sMes == null || !Pattern.matches("[0-9]|[1][01]",sMes) ||
	 sDia == null || !Pattern.matches("[1-9]|[12][0-9]|[3][01]",sDia) ||
	 sHora == null || !Pattern.matches("[0-9]|[1][0-9]|[2][0-3]",sHora))
	) {
		new Mensagem(response).showErro("Dados da database enviados invalidos!");
		return;
	}
	
	int local = Integer.parseInt(sLocal);
	int pista = Integer.parseInt(sPista);
	FluxoVelocidadeGrade.Tipo tipo = FluxoVelocidadeGrade.Tipo.values()[Integer.parseInt(sTipo)];
	int ano = Integer.parseInt(sAno);
	int mes = Integer.parseInt(sMes);
	int dia = Integer.parseInt(sDia);
	int hora = Integer.parseInt(sHora);

	Calendar dtIni = null;
	Calendar dtFim = null;
	String prefix = null;
	String sulfix = null;
	String dataBase = null;
	String grupo = null;
	Integer total = null;
	
	switch(tipo) {
		case MINUTO:
			grupo = "Minutos: ";
			dtIni = new GregorianCalendar(ano,mes,dia,hora,0,0);
			dtFim = new GregorianCalendar(ano,mes,dia,hora,59,59);
			prefix = String.format("%02d",hora)+":";
			dataBase = new SimpleDateFormat("dd/MM/yyyy 'às' HH'h'").format(dtIni.getTime());
			break;
		case QUINZE:
			grupo = "Minutos: ";
			dtIni = new GregorianCalendar(ano,mes,dia,hora,0,0);
			dtFim = new GregorianCalendar(ano,mes,dia,hora,59,59);
			prefix = String.format("%02d",hora)+":";
			sulfix = "->";
			dataBase = new SimpleDateFormat("dd/MM/yyyy 'às' HH'h'").format(dtIni.getTime());
			break;
		case HORA:
			grupo = "Horas: ";
			dtIni = new GregorianCalendar(ano,mes,dia,0,0,0);
			dtFim = new GregorianCalendar(ano,mes,dia,23,59,59);
			sulfix = "h";
			dataBase = new SimpleDateFormat("dd/MM/yyyy").format(dtIni.getTime());
			break;
		case DIA:
			grupo = "Dias: ";
			dtIni = new GregorianCalendar(ano,mes,1,0,0,0);
			dtFim = new GregorianCalendar(ano,mes+1,0,23,59,59); //Dia 0 indica 31 do mes anterior
			sulfix = new SimpleDateFormat("/MM").format(dtIni.getTime());
			dataBase = new SimpleDateFormat("MMM/yyyy").format(dtIni.getTime());
			break;
		case MES:
			grupo = "Mes: ";
			dtIni = new GregorianCalendar(ano,0,1,0,0,0);
			dtFim = new GregorianCalendar(ano+1,0,0,23,59,59); //Próximo ano.
			sulfix = new SimpleDateFormat("/yyyy").format(dtIni.getTime());
			dataBase = new SimpleDateFormat("yyyy").format(dtIni.getTime());
			break;
		case ANO:
			grupo = "Ano: ";
			dtIni = new GregorianCalendar(1900,0,0,0,0,0);
			dtFim = new GregorianCalendar(2900,0,0,0,0,0);
			dataBase = "Toda a base";
			break;
	}

	RelatorioItr<FluxoVelocidadeGrade> listaFlkuxoVelociadeGrade = new RelatorioItr<FluxoVelocidadeGrade>(
			new FluxoVelocidadeGrade(local,pista,
			new Timestamp(dtIni.getTimeInMillis()),
			new Timestamp(dtFim.getTimeInMillis()),
			tipo
		)
	);

	Map<Integer, Integer> mapTempoConta = new HashMap<Integer, Integer>();
	Map<Integer, Map<Integer, Integer>> mapVelTempo = new TreeMap<Integer, Map<Integer, Integer>>();
	Set<Integer> datas = new TreeSet<Integer>();

	int velAtual = 0;
	for(FluxoVelocidadeGrade item: listaFlkuxoVelociadeGrade) {
		if (item.getSubData() == null) { //Terminou esta velocidade...então vamos adionar os tempos:
			if (item.getVelocidade() == null) { //Soma total.
				total = item.getConta(); //Ultimo item, então estamos capturando a sumarizacao total.
				break;
			}
		 	else { //Sub-soma.
				mapVelTempo.put(velAtual, mapTempoConta);
				mapTempoConta = new HashMap<Integer, Integer>();
				continue;
		 	}
		}
		mapTempoConta.put(item.getSubData(), item.getConta());
		datas.add(item.getSubData());
		velAtual = item.getVelocidade();
	}
%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.TreeSet"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>

<%@page import="com.consilux.infra.RelatorioItr"%>
<%@page import="com.consilux.model.relatorio.FluxoVelocidadeGrade"%><br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_grid" width="<%=135+datas.size()*40 %>">
					<tr>
						<th class="head_tabela" align="center" colspan="<%=1+datas.size()%>">
							&nbsp;Dados&nbsp;estatísticos&nbsp;do&nbsp;período&nbsp;
						</th>
					<tr>
					<tr>
						<td class="dado_lista_tabela_grid" align="center">
							&nbsp;Velocidade&nbsp;
						</td>
						<td class="dado_lista_tabela_grid" align="left" colspan="<%=datas.size()%>">
							&nbsp;Quantidade&nbsp;de&nbsp;veículos&nbsp;
						</td>
					<tr>
					</tr>
				<c:forEach var="linha" items="<%=mapVelTempo%>">
					<tr>
						<td class="dado_lista_tabela_grid" align="center" width="135">
							<b>${linha.key*10}&nbsp;-&nbsp;${(linha.key+1)*10}&nbsp;km/h</b>
						</td>
						<c:set var="coluna" value="${linha.value}" />
						<c:forEach var="data" items="<%=datas%>">
							<td class="dado_lista_tabela_grid" align="center" width="40">
								${coluna[data] != null ? coluna[data] : 0}
							</td>
						</c:forEach>
					</tr>
				</c:forEach>
				<tr>
					<td class="dado_lista_tabela_grid" align="center" width="135">
						<b><%=grupo%></b>
					</td>
					<c:set var="prefix" value="<%=prefix%>" />
					<c:set var="sulfix" value="<%=sulfix%>" />
					<c:forEach var="data" items="<%=datas%>">
						<td class="dado_lista_tabela_grid" align="center">
							<b>${prefix}<fmt:formatNumber value="${data}" pattern="00"/>${sulfix}</b>
						</td>
					</c:forEach>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td align="center">
			<br />
			<table class="tabela_branca">
				<tr>
					<td class="valor_campo" width="100%">
						Data-base: <%=dataBase%><br />
						Total do periodo: <%=total!=null?total:""%>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
