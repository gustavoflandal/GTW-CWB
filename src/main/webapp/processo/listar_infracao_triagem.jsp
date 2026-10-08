<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%

	EtapaProcesso etapa = EtapaProcesso.TRIAGEM;
	
	List<Enquadramento> enquadramentos = Enquadramento.buscaEnquadramentoDisponivelPorIdProcesso(etapa.getId());
	
	String sIdEnquadramento = request.getParameter("id_enquadramento") != null ? request.getParameter("id_enquadramento").trim() : null;
	sIdEnquadramento = sIdEnquadramento != null && sIdEnquadramento.length() == 0 ? null : sIdEnquadramento;
	
	String sNumReg = request.getParameter("num_reg") != null ? request.getParameter("num_reg").trim() : null;
	sNumReg = sNumReg != null && sNumReg.length() == 0 ? null : sNumReg;
	
	String sConsistencia = request.getParameter("consistencia") != null ? request.getParameter("consistencia").trim() : null;
	sConsistencia = sConsistencia != null && sConsistencia.length() == 0 ? null : sConsistencia;

	String sEspera = request.getParameter("espera") != null ? request.getParameter("espera").trim() : null;
	sEspera = sEspera != null && sEspera.length() == 0 ? null : sEspera;

	String sInfracoesPreSelecionadas = request.getParameter("infracoes_pre_selecionadas") != null ? request.getParameter("infracoes_pre_selecionadas").trim() : null;
	sInfracoesPreSelecionadas = sInfracoesPreSelecionadas != null && sInfracoesPreSelecionadas.length() == 0 ? null : sInfracoesPreSelecionadas;

	String sDataInfracaoIni = request.getParameter("data_infracao_ini") != null ? request.getParameter("data_infracao_ini").trim() : null;
	sDataInfracaoIni = sDataInfracaoIni != null && sDataInfracaoIni.length() == 0 ? null : sDataInfracaoIni;
	
	String sHoraInfracaoIni = request.getParameter("hora_infracao_ini") != null ? request.getParameter("hora_infracao_ini").trim() : null;
	sHoraInfracaoIni = sHoraInfracaoIni != null && sHoraInfracaoIni.length() == 0 ? null : sHoraInfracaoIni;
	
	String sDataInfracaoFim = request.getParameter("data_infracao_fim") != null ? request.getParameter("data_infracao_fim").trim() : null;
	sDataInfracaoFim = sDataInfracaoFim != null && sDataInfracaoFim.length() == 0 ? null : sDataInfracaoFim;
	
	String sHoraInfracaoFim = request.getParameter("hora_infracao_fim") != null ? request.getParameter("hora_infracao_fim").trim() : null;
	sHoraInfracaoFim = sHoraInfracaoFim != null && sHoraInfracaoFim.length() == 0 ? null : sHoraInfracaoFim;

	if (sIdEnquadramento != null && !Pattern.matches("[0-9]{0,8}",sIdEnquadramento)) {
	    new Mensagem(response).showErro("Identificador de enquadramento enviado inválido!");
	    return;
	}
	if (sNumReg != null && !Pattern.matches("[0-9]{0,7}",sNumReg)) {
	    new Mensagem(response).showErro("Número de registros inválido!");
	    return;
	}
	if (sConsistencia != null && !Pattern.matches("[0-2]",sConsistencia)) {
	    new Mensagem(response).showErro("Seleção de consistência inválida!");
	    return;
	}
    if (sDataInfracaoIni != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataInfracaoIni)) {
        new Mensagem(response).showErro("Data inicial do período enviada inválida!");
        return;
    }
    if (sDataInfracaoFim != null && !Pattern.matches("([012][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataInfracaoFim)) {
        new Mensagem(response).showErro("Data inicial do período enviada inválida!");
        return;
    }
    if (sHoraInfracaoIni != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])",sHoraInfracaoIni)) {
        new Mensagem(response).showErro("Hora inicial do período enviada inválida!");
        return;
    }
    if (sHoraInfracaoFim != null && !Pattern.matches("([01][0-9]|2[0123]):([0-5][0-9])",sHoraInfracaoFim)) {
        new Mensagem(response).showErro("Hora final do período enviada inválida!");
        return;
    }
    if ((sDataInfracaoIni != null || sHoraInfracaoIni != null || sDataInfracaoFim != null || sHoraInfracaoFim != null) &&
    	(sDataInfracaoIni == null || sHoraInfracaoIni == null || sDataInfracaoFim == null || sHoraInfracaoFim == null)) {
        new Mensagem(response).showErro("Período incompleto!");
        return;
    }
	if (sInfracoesPreSelecionadas != null && !Pattern.matches("[0-9]{0,8}",sInfracoesPreSelecionadas)) {
	    new Mensagem(response).showErro("Campo infrações pré selecionadas enviado inválido!");
	    return;
	}
	
	Integer idEnquadramento = sIdEnquadramento != null ? Integer.valueOf(sIdEnquadramento) : null;
	idEnquadramento = idEnquadramento != null && idEnquadramento > 0 ? idEnquadramento : null;
	
	Integer numReg = sNumReg != null ? Integer.valueOf(sNumReg) : 10;
	numReg = numReg != null && numReg > 0 ? numReg : null;

	Boolean consistencia = null;
	if (sConsistencia == null || sConsistencia.equals("0"))	
		consistencia = null;
	else if (sConsistencia.equals("1"))	
		consistencia = true;
	else if (sConsistencia.equals("2"))	
		consistencia = false;

	Boolean espera = sEspera != null ? sEspera.equals("1") : false;
	
	List<String> lista_infracoes = null;

	//Verifica se deve sugerir uma data...
	if (sIdEnquadramento == null) { //Não foi passado nenhum filtro, porque caso venha os qualquer filtro o enquadramento é 0 ou maior que zero
		//Buscando a primeira infração disponível...
	    List<InfracaoSimplificada> infracoesSug = InfracaoSimplificada.buscaInfracaoPorEtapaProcessoUsuario(etapa.getId(),((Usuario)request.getSession().getAttribute("[usuario]")).getId(), null, null, 1, null, null, null, null, null, null, null);
		if (infracoesSug.size() > 0) {
			GregorianCalendar cal = new GregorianCalendar();
			cal.setTime(infracoesSug.get(0).getData());
			sDataInfracaoIni = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
			sHoraInfracaoIni = "00:00";
			cal.add(Calendar.DATE, 1); //Pula pro próximo dia...
			sDataInfracaoFim = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
			sHoraInfracaoFim = "23:59";
		}
    }

	Timestamp periodoIni = null; 
	Timestamp periodoFim = null; 
	
    if (sDataInfracaoIni != null) {
        periodoIni = new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataInfracaoIni+" "+sHoraInfracaoIni+":00").getTime());
        periodoFim = new Timestamp(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataInfracaoFim+" "+sHoraInfracaoFim+":59").getTime());
    }
    
    List<? extends ExportaLista> infracoesSel = null;
    
    if (sInfracoesPreSelecionadas != null && Integer.valueOf(sInfracoesPreSelecionadas) > 0) {
   		infracoesSel = InfracaoCompletaBean.getListaParaRelatorio();
   		if (infracoesSel != null) {
	   		lista_infracoes = new ArrayList<String>(infracoesSel.size());
	   		for (ExportaLista e: infracoesSel) {
	   			lista_infracoes.add(String.valueOf(((InfracaoCompletaBean) e).getId()));
	   		}
   		}
    }

    Integer total_infracoes = InfracaoSimplificada.contaInfracoesPorEtapaProcessoUsuario(etapa.getId(),((Usuario)request.getSession().getAttribute("[usuario]")).getId(), idEnquadramento, consistencia, periodoIni, periodoFim, lista_infracoes);
    List<InfracaoSimplificada> infracoes = InfracaoSimplificada.buscaInfracaoPorEtapaProcessoUsuario(etapa.getId(),((Usuario)request.getSession().getAttribute("[usuario]")).getId(), idEnquadramento, consistencia, numReg, espera, periodoIni, periodoFim, lista_infracoes);
	
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
<c:set var="processa_direto" value="<%=true%>" />
<c:set var="etapaProcesso" value="<%=etapa.ordinal()%>" scope="request"/>
<c:set var="total_infracoes" value="<%=total_infracoes%>" />
<c:set var="infracoes_size" value="<%=infracoes_size%>" />
<c:set var="infracoes" value="<%=infracoes%>" />
<c:set var="id_enquadramento" value="<%=idEnquadramento%>" />
<c:set var="num_reg" value="<%=numReg%>" />
<c:set var="espera" value="<%=espera%>" />
<c:set var="id_processo" value="<%=etapa.getId()%>" />
<c:set var="consistencia" value="<%=sConsistencia%>" />
<c:set var="infracoes_pre_selecionadas" value="<%=(lista_infracoes != null ? lista_infracoes.size() : 0)%>" />
<c:set var="enquadramentos" value="<%=enquadramentos%>" />
<c:set var="data_infracao_ini" value="<%=sDataInfracaoIni%>" />
<c:set var="hora_infracao_ini" value="<%=sHoraInfracaoIni%>" />
<c:set var="data_infracao_fim" value="<%=sDataInfracaoFim%>" />
<c:set var="hora_infracao_fim" value="<%=sHoraInfracaoFim%>" />
<c:set var="list_infracao_action" value="/processo/IniciarProcesso" />
<c:set var="btn_processo_caption" value="Iniciar&nbsp;Triagem"/>
<%@ include file="/WEB-INF/templates/processo/list_infracao.jsp" %>
<%@ include file="/includes/rodape.jsp" %>
