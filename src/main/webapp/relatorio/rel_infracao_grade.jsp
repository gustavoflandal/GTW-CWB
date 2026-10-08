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
	String sIdProcesso = request.getParameter("id_processo");
    String sIdEnquadramento = request.getParameter("id_enquadramento");
	String sComInconsistente = request.getParameter("com_inconsistente");
	String sGrupoLocal = request.getParameter("com_grupo_local");
	String sGrupoPista = request.getParameter("com_grupo_pista");

	if (sIdProcesso != null && !Pattern.matches("[0-9]{1,8}",sIdProcesso)) {
		new Mensagem(response).showErro("Identificador do processo enviado inválido!");
		return;
	}
	else if (sIdEnquadramento == null || !Pattern.matches("[0-9]{1,8}",sIdEnquadramento)) {
        new Mensagem(response).showErro("Identificador do enquadramento enviado inválido!");
        return;
    }
	else if (sLocal == null || !Pattern.matches("[0-9][0-9]{0,7}",sLocal)) {
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
	else if (sTipo.compareTo(""+InfracaoGrade.Tipo.MES.ordinal()) == 0 && 
	(sAno == null || !Pattern.matches("[12][0-9]{3}",sAno))
	) {
		new Mensagem(response).showErro("Dados da database enviados invalidos!");
		return;
	}
	else if (sTipo.compareTo(""+InfracaoGrade.Tipo.DIA.ordinal()) == 0 && 
	(sAno == null || !Pattern.matches("[12][0-9]{3}",sAno) ||
	 sMes == null || !Pattern.matches("[0-9]|[1][01]",sMes))
	) {
		new Mensagem(response).showErro("Dados da database enviados invalidos!");
		return;
	}
	else if (sTipo.compareTo(""+InfracaoGrade.Tipo.HORA.ordinal()) == 0 && 
	(sAno == null || !Pattern.matches("[12][0-9]{3}",sAno) ||
	 sMes == null || !Pattern.matches("[0-9]|[1][01]",sMes) ||
	 sDia == null || !Pattern.matches("[1-9]|[12][0-9]|[3][01]",sDia))
	) {
		new Mensagem(response).showErro("Dados da database enviados invalidos!");
		return;
	}
	if (sComInconsistente != null && !sComInconsistente.equals("true")) {
		new Mensagem(response).showErro("Indicação de inconsistentes inválido!");
		return;
	}
	if (sGrupoLocal != null && !sGrupoLocal.equals("true")) {
		new Mensagem(response).showErro("Indicação de grupo local inválido!");
		return;
	}
	if (sGrupoPista != null && !sGrupoPista.equals("true")) {
		new Mensagem(response).showErro("Indicação de grupo pista inválido!");
		return;
	}

	Boolean comInconsistente = Boolean.valueOf(sComInconsistente);
	Boolean grupoLocal = Boolean.valueOf(sGrupoLocal);
	Boolean grupoPista = grupoLocal && Boolean.valueOf(sGrupoPista); //Se está agrupado por local, automáticamente também está por pista.
	
	int idProcesso = Integer.parseInt(sIdProcesso);
	int idLocal = Integer.parseInt(sLocal);
	int idPista = Integer.parseInt(sPista);
	InfracaoGrade.Tipo tipo = InfracaoGrade.Tipo.values()[Integer.parseInt(sTipo)];
	int ano = Integer.parseInt(sAno);
	int mes = Integer.parseInt(sMes);
	int dia = Integer.parseInt(sDia);
	int hora = Integer.parseInt(sHora);

	Calendar dtIni = null;
	Calendar dtFim = null;
	String sulfixX = null;
    String sulfixY = null;
	String dataBase = null;
	String grupoX = null;
    String grupoY = null;
	
	switch(tipo) {
		case HORA:
            grupoY = "Horas";
	grupoX = "Minuto: ";
	dtIni = new GregorianCalendar(ano,mes,dia,0,0,0);
	dtFim = new GregorianCalendar(ano,mes,dia,23,59,59);
            sulfixY = "h";
            sulfixX = "min";
	dataBase = new SimpleDateFormat("dd/MM/yyyy").format(dtIni.getTime());
	break;
		case DIA:
            grupoY = "Dias";
	grupoX = "Hora: ";
	dtIni = new GregorianCalendar(ano,mes,1,0,0,0);
	dtFim = new GregorianCalendar(ano,mes+1,0,23,59,59); //Dia 0 indica 31 do mes anterior
	sulfixY = new SimpleDateFormat("/MM/yyyy").format(dtIni.getTime());
            sulfixX = "h";
	dataBase = new SimpleDateFormat("MMM/yyyy").format(dtIni.getTime());
	break;
		case MES:
            grupoY = "Meses";
	grupoX = "Dia&nbsp;do&nbsp;Mes: ";
	dtIni = new GregorianCalendar(ano,0,1,0,0,0);
	dtFim = new GregorianCalendar(ano+1,0,0,23,59,59); //Próximo ano.
            sulfixY = new SimpleDateFormat("/yyyy").format(dtIni.getTime());
	dataBase = new SimpleDateFormat("yyyy").format(dtIni.getTime());
	break;
		case ANO:
	grupoY = "Anos";
	      grupoX = "Mes&nbsp;do&nbsp;Ano: ";
	dtIni = new GregorianCalendar(1900,0,0,0,0,0);
	dtFim = new GregorianCalendar(2900,0,0,0,0,0);
	dataBase = "Toda a base";
	break;
	}
	
    Map<String,Object> mFiltro = new HashMap<String,Object>();

	if (Integer.valueOf(sLocal) > 0) {
	    mFiltro.put("id_local", Integer.valueOf(sLocal));
	}
    
	if (Integer.valueOf(sPista) > 0) {
	    mFiltro.put("id_pista", Integer.valueOf(sPista));
	}
	
    //Faz primeiro a busca de pistas ou pistas, porque não é necessário passar a data.
    List<Pista> pistas = null;
    if (grupoPista)
    	pistas = Pista.buscarPistaPor(mFiltro);

	Integer idEnquadramento = 0;
    if (Integer.valueOf(sIdEnquadramento) > 0)
    	idEnquadramento = Integer.valueOf(sIdEnquadramento);

    List<Local> locais = null;
    if (grupoLocal)
    	locais = Local.listarLocaisVigentes();
	

	RelatorioItr<InfracaoGrade> listaInfracaoGrade = new RelatorioItr<InfracaoGrade>(
			new InfracaoGrade(idLocal,idPista,
			new Timestamp(dtIni.getTimeInMillis()),
			new Timestamp(dtFim.getTimeInMillis()),
			tipo,
			idProcesso,
			idEnquadramento,
			comInconsistente,
			grupoLocal,
			grupoPista
		)
	);
    Map<Integer, Map<Integer, Map<Integer, Map<Integer, InfracaoGrade>>>> mapLocais = new TreeMap<Integer, Map<Integer, Map<Integer, Map<Integer, InfracaoGrade>>>>();
    List<Integer> listX = new ArrayList<Integer>();
    Map<Integer, Integer> mapTotaisY = new TreeMap<Integer, Integer>();
    Map<Integer, Map<Integer, InfracaoGrade>> mapGeral = new TreeMap<Integer, Map<Integer, InfracaoGrade>>();
    
    for(InfracaoGrade item: listaInfracaoGrade) {
    	Map<Integer, InfracaoGrade> listY = new HashMap<Integer, InfracaoGrade>();
        InfracaoGrade i = (InfracaoGrade)item.clone();

        if (!listX.contains(item.getX())) {
        	listX.add(item.getX());
        }

        if (mapTotaisY.containsKey(item.getX()))
    		mapTotaisY.put(item.getX(), mapTotaisY.get(item.getX()) + item.getConta());
    	else
    		mapTotaisY.put(item.getX(), item.getConta());
        
        if (grupoLocal) {
	    	Map<Integer, Map<Integer, Map<Integer, InfracaoGrade>>> local;
	        Map<Integer, Map<Integer, InfracaoGrade>> pista;
	
	    	if (mapLocais.containsKey(item.getIdLocal()))
	    		local = mapLocais.get(item.getIdLocal());
	    	else {
	    		local = new TreeMap<Integer, Map<Integer, Map<Integer, InfracaoGrade>>>();
	    		mapLocais.put(item.getIdLocal(), local);
	    	}
	
	    	if (grupoPista) {
		    	if (local.containsKey(item.getPista()))
		    		pista = local.get(item.getPista());
		    	else {
		    		pista = new TreeMap<Integer, Map<Integer, InfracaoGrade>>();
		    		local.put(item.getPista(), pista);
		    	}
		
		    	if (pista.containsKey(item.getY()))
		    		listY = pista.get(item.getY());
		    	else {
		    		listY = new HashMap<Integer, InfracaoGrade>();
		    		pista.put(item.getY(), listY);
		    	}
	    	}
	    	else { //Cria uma pseudo pista 0.
		    	if (local.containsKey(0))
		    		pista = local.get(0);
		    	else {
		    		pista = new TreeMap<Integer, Map<Integer, InfracaoGrade>>();
		    		local.put(0, pista);
		    	}
		    	if (pista.containsKey(item.getY()))
		    		listY = pista.get(item.getY());
		    	else {
		    		listY = new HashMap<Integer, InfracaoGrade>();
		    		pista.put(item.getY(), listY);
		    	}
	    	}
	    	
	    	listY.put(item.getX(), i);
        }
        else {
        	mapLocais = null;
	    	if (mapGeral.containsKey(item.getY()))
	    		listY = mapGeral.get(item.getY());
	    	else {
	    		listY = new HashMap<Integer, InfracaoGrade>();
	    		mapGeral.put(item.getY(), listY);
	    	}

	    	listY.put(item.getX(), i);
        }
    }
    if (pistas != null)
    	Collections.sort(pistas);
    
    Collections.sort(listX);
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
<%@page import="com.consilux.model.relatorio.InfracaoGrade"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Collections"%>
<%@page import="com.consilux.model.Pista"%>
<%@page import="com.consilux.model.Local"%><br />
<c:set var="array_vazio" value="<%=new Integer[1]%>" />
<c:set var="mapLocais" value="<%=mapLocais%>" />
<c:set var="pista_def" value="<%=0%>" />
<c:set var="pistas" value="<%=pistas%>" />
<c:set var="locais" value="<%=locais%>" />
<c:set var="mapGeral" value="<%=mapGeral%>" />
<c:set var="mapTotaisY" value="<%=mapTotaisY%>" />
<c:set var="listX" value="<%=listX%>" />
<c:set var="sulfixX" value="<%=sulfixX%>" />
<c:set var="sulfixY" value="<%=sulfixY%>" />
<c:set var="tot_geral" value="0"></c:set>
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_grid" width="<%=450+(listX.size()*30)+40%>">
				<tr>
					<th class="head_tabela" align="center" colspan="<%=4+listX.size()+1%>">
						&nbsp;Dados&nbsp;estatísticos&nbsp;do&nbsp;período&nbsp;
					</th>
				<tr>
				<tr>
					<c:if test="${locais != null || pistas != null}">
	                    <td class="dado_lista_tabela_grid" align="center" width="30">
	                        &nbsp;Cód. Pista/Local&nbsp;
	                    </td>
	                    <td class="dado_lista_tabela_grid" align="center" width="450">
	                        &nbsp;Nome Pista/Local&nbsp;
	                    </td>
	                    <td class="dado_lista_tabela_grid" align="center" width="20">
	                        &nbsp;Faixa&nbsp;
	                    </td>
                    </c:if>
					<td class="dado_lista_tabela_grid" align="center">
						<b>&nbsp;<%=grupoY+"/"+grupoX%>&nbsp;</b>
					</td>
                    <c:forEach var="item" items="${listX}">
						<td class="dado_lista_tabela_grid" align="center">
							&nbsp;<fmt:formatNumber value="${item}" pattern="00"/>${sulfixX}&nbsp;
						</td>
					</c:forEach>
                    <td class="dado_lista_tabela_grid" align="center">
                        <b>&nbsp;TOTAIS&nbsp;</b>
                    </td>
				</tr>
					<c:choose>
						<c:when test="${pistas != null}">
							<c:set var="locais_pistas" value="${pistas}"></c:set>
						</c:when>
						<c:when test="${locais != null}">
							<c:set var="locais_pistas" value="${locais}"></c:set>
						</c:when>
						<c:otherwise>
							<c:set var="locais_pistas" value="${array_vazio}"></c:set>
						</c:otherwise>
					</c:choose>
					<c:forEach var="local_pista" items="${locais_pistas}">
					<c:choose>
						<c:when test="${mapLocais != null}">
							<c:choose>
								<c:when test="${pistas != null}">
										<c:set var="listY" value="${mapLocais[local_pista.idLocal][local_pista.pista]}"></c:set>
								</c:when>
								<c:otherwise>
										<c:set var="listY" value="${mapLocais[local_pista.idLocal][pista_def]}"></c:set>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<c:set var="listY" value="${mapGeral}"></c:set>
						</c:otherwise>
					</c:choose>		
						<c:forEach var="y" items="${listY}">
							<tr>
								<c:if test="${locais != null || pistas != null}">
									<td class="dado_lista_tabela_grid" align="center">
					                   	${pistas != null ? (local_pista.codPista > 0 ? local_pista.codPista : local_pista.idLocal) : (locais != null ? local_pista.idLocal : '')}
									</td>
									<td class="dado_lista_tabela_grid" align="center">
					                   	${pistas != null ? local_pista.nomePista : (locais != null ? local_pista.nome : '')}
									</td>
									<td class="dado_lista_tabela_grid" align="center">
					                   	${pistas != null ? local_pista.codPistaAlternativo : ''}
									</td>
								</c:if>
								<td class="dado_lista_tabela_grid" align="center" width="135">
									&nbsp;${y.key}${sulfixY}&nbsp;
								</td>
                        <c:set var="tot_linha" value="0"></c:set>
						<c:forEach var="x" items="${listX}">
	                        <c:set var="conta" value="${y.value[x] != null ? y.value[x].conta : 0}"></c:set>
                            <c:set var="tot_linha" value="${tot_linha+conta}"></c:set>
							<td class="dado_lista_tabela_grid" align="center" width="40">
								${conta}
							</td>
						</c:forEach>
                        <c:set var="tot_geral" value="${tot_geral+tot_linha}"></c:set>
                        <td class="dado_lista_tabela_grid" align="center">
                            <b>${tot_linha}</b>
                        </td>
					</tr>
						</c:forEach>
					</c:forEach>
				<tr>
					<td class="dado_lista_tabela_grid" align="center" width="135" colspan="${(locais != null ? 3 : 0)+1}">
						<b>TOTAIS: </b>
					</td>
                    <c:forEach var="item" items="${mapTotaisY}">
	                    <c:set var="tot" value="${item.value}"></c:set>
						<td class="dado_lista_tabela_grid" align="center">
							<b>&nbsp;&nbsp;${tot}&nbsp;&nbsp;</b>
						</td>
					</c:forEach>
                    <td class="dado_lista_tabela_grid" align="center">
	                    <b>${tot_geral}</b>
                    </td>
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
						Total do período: ${tot_geral}
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
