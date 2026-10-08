<%@page import="com.consilux.model.Contestacao"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<%@page import="com.consilux.model.Grupo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%

	Logger log = LogManager.getLogger("listar_infracao_contestacao.jsp");

	EtapaProcesso etapa = EtapaProcesso.CONTESTACAO;
	int cav = 0;

	Acesso acesso = new Acesso(request, response, true);
	Usuario usr = acesso.getUsuario();
	List<GrupoBean> grupos = Grupo.buscaGruposPorIdUsuario(usr.getId());
	for (GrupoBean g : grupos) {
		log.debug(g.getId() + " => " + g.getDescricao());
		if (g.getDescricao().trim().equals("Contestação CAV")) // Contestação CAV
		{
			etapa = EtapaProcesso.CONTESTACAO_CAV;
			cav = 1;
		}
	}

	String sNumReg = request.getParameter("num_reg") != null ? request.getParameter("num_reg").trim() : null;
	sNumReg = sNumReg != null && sNumReg.length() == 0 ? null : sNumReg;

	String sDataInfracaoIni = request.getParameter("data_infracao_ini") != null ? request.getParameter("data_infracao_ini").trim() : null;
	sDataInfracaoIni = sDataInfracaoIni != null && sDataInfracaoIni.length() == 0 ? null : sDataInfracaoIni;
	
	String sDataInfracaoFim = request.getParameter("data_infracao_fim") != null ? request.getParameter("data_infracao_fim").trim() : null;
	sDataInfracaoFim = sDataInfracaoFim != null && sDataInfracaoFim.length() == 0 ? null : sDataInfracaoFim;
	
	if (sNumReg != null && !Pattern.matches("[0-9]{0,7}",sNumReg)) {
	    new Mensagem(response).showErro("Número de registros inválido!");
	    return;
	}
	if (sDataInfracaoIni != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataInfracaoIni)) {
        new Mensagem(response).showErro("Data inicial do período enviada inválida!");
        return;
    }
    if (sDataInfracaoFim != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataInfracaoFim)) {
        new Mensagem(response).showErro("Data final do período enviada inválida!");
        return;
    }
    
    Timestamp periodoIni = null; 
	Timestamp periodoFim = null; 
	
	Integer numReg = sNumReg != null ? Integer.valueOf(sNumReg) : 10;
	numReg = numReg != null && numReg > 0 ? numReg : null;
	
    if (sDataInfracaoIni != null) {
        periodoIni = new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataInfracaoIni+" 00:00:00").getTime());
        periodoFim = new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataInfracaoFim+" 23:59:59").getTime());
    }

    log.debug("INICIANDO CONTAGEM DE INFRACOES");
    Integer total_infracoes = Contestacao.ContarInfracoes(etapa.getId());
    log.debug("CONTAGEM DE INFRACOES CONCLUIDA");
    
    log.debug("INICIANDO BUSCA DE INFRACOES");
    List<InfracaoSimplificada> infracoes = InfracaoSimplificada.buscaInfracaoContestacao(etapa.getId(), periodoIni, periodoFim, numReg);
    log.debug("BUSCA DE INFRACOES CONCLUIDA");
	
	Integer infracoes_size = infracoes.size();
%>
<%@page import="com.consilux.model.InfracaoSimplificada"%> 
<%@page import="com.consilux.model.Processamento.EtapaProcesso"%>
<%@page import="com.consilux.model.Processamento"%>

<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.util.Date"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.exportalista.ExportaLista"%>
<%@page import="com.consilux.exportalista.InfracaoCompletaBean"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Calendar"%>
<c:set var="cav" value="<%=cav %>" />
<c:set var="processa_direto" value="<%=true%>" />
<c:set var="etapaProcesso" value="<%=etapa.ordinal()%>" scope="request"/>
<c:set var="total_infracoes" value="<%=total_infracoes%>" />
<c:set var="infracoes_size" value="<%=infracoes_size%>" />
<c:set var="infracoes" value="<%=infracoes%>" />
<c:set var="num_reg" value="<%=numReg%>" />
<c:set var="id_processo" value="<%=etapa.getId()%>" />
<c:set var="data_infracao_ini" value="<%=sDataInfracaoIni%>" />
<c:set var="data_infracao_fim" value="<%=sDataInfracaoFim%>" />
<c:set var="list_infracao_action" value="/processo/IniciarProcesso" />
<c:set var="btn_processo_caption" value="Iniciar"/>
<%@ include file="/WEB-INF/templates/processo/list_infracao_contestacao.jsp" %>
<%@ include file="/includes/rodape.jsp" %>
