<%@page import="com.google.common.collect.Iterables"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="java.util.HashMap"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/includes/cabecalho.jsp"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%	
	Logger logger = Logger.getLogger("listar_infracao_validacao.jsp");
	Date dt1 = Calendar.getInstance().getTime(), dt2 = null;

	EtapaProcesso etapa = EtapaProcesso.VALIDACAO;

	Remessa remessa_at = (Remessa)request.getAttribute("remessa_at");
	
	String sIdRemessa = request.getParameter("id_remessa") != null ? request.getParameter("id_remessa").trim() : null;
	sIdRemessa = sIdRemessa != null && sIdRemessa.length() == 0 ? "0" : sIdRemessa;
	Integer idRemessa = sIdRemessa != null ? Integer.valueOf(sIdRemessa) : null;
	
	String sAmostra = request.getParameter("amostra") != null ? request.getParameter("amostra").trim() : null;
	sAmostra = sAmostra != null && sAmostra.length() == 0 ? null : sAmostra;
	Boolean amostra = sAmostra != null ? sAmostra.equals("1") : false;
	
	String sMostrarTodos = request.getParameter("chkMostrarTodos") != null ? request.getParameter("chkMostrarTodos").trim() : null;
	sMostrarTodos = sMostrarTodos != null && sMostrarTodos.length() == 0 ? null : sMostrarTodos;
	Integer intMostrarTodos = sMostrarTodos != null ? Integer.valueOf(sMostrarTodos) : 0;
	
	HashMap<Integer, Remessa> remessas_ht = Remessa.buscarRemessa(false, 0, 0, 0, intMostrarTodos);
	Date data_atualizado = Calendar.getInstance().getTime();
	
	if(remessa_at == null && idRemessa != null && idRemessa > 0 && remessas_ht.containsKey(idRemessa)) {
		remessa_at = remessas_ht.get(idRemessa);
		data_atualizado = remessa_at.getDataAtualizacao();
	} else {
		if (remessas_ht.size() > 0)
			data_atualizado = Iterables.get(remessas_ht.values(), 0).getDataAtualizacao();
	}
	
	Boolean integridade = false;
	if(remessa_at != null)
		integridade = remessa_at.getIntegridadeBD();
	
	idRemessa = idRemessa != null ? idRemessa : 0;
		
	String sConsistencia = null;
	
	String sEspera = request.getParameter("espera") != null ? request.getParameter("espera").trim() : null;
	sEspera = sEspera != null && sEspera.length() == 0 ? null : sEspera;
	
	String sRevisao = request.getParameter("revisao") != null ? request.getParameter("revisao").trim() : null;
	sRevisao = sRevisao != null && sRevisao.length() == 0 ? null : sRevisao;
	
	String sInfracoesPreSelecionadas = request.getParameter("infracoes_pre_selecionadas") != null ? request.getParameter("infracoes_pre_selecionadas").trim() : null;
	sInfracoesPreSelecionadas = sInfracoesPreSelecionadas != null && sInfracoesPreSelecionadas.length() == 0 ? null : sInfracoesPreSelecionadas;

	if (sInfracoesPreSelecionadas != null && !Pattern.matches("[0-9]{0,8}",sInfracoesPreSelecionadas)) {
	    new Mensagem(response).showErro("Campo infrações pré selecionadas enviado inválido!");
	    return;
	}
	
	Integer idEnquadramento = null;
	
	Boolean espera = sEspera != null ? sEspera.equals("1") : false;
	Boolean revisao = sRevisao != null ? sRevisao.equals("1") : false;

	Boolean consistencia = null;

	List<String> lista_infracoes = null;

	Timestamp periodoIni = null; 
	Timestamp periodoFim = null; 
	
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
    
    if(revisao)
    	etapa = EtapaProcesso.REMESSA_VALIDADA;
    
    Integer total_infracoes = 0;
    if(remessa_at != null && integridade)
    	total_infracoes = InfracaoSimplificada.contaInfracoesPorEtapaProcessoUsuario(etapa.getId(),((Usuario)request.getSession().getAttribute("[usuario]")).getId(), idEnquadramento, consistencia, periodoIni, periodoFim, null, lista_infracoes, idRemessa, amostra);

	List<InfracaoSimplificada> infracoes = null;
	Integer infracoes_size = null;
	
	if (revisao || espera) {
	    if(remessa_at != null && integridade)
	    	infracoes = new ArrayList<InfracaoSimplificada>();
	    	infracoes = InfracaoSimplificada.buscaInfracaoPorEtapaProcessoUsuario(etapa.getId(),((Usuario)request.getSession().getAttribute("[usuario]")).getId(), idEnquadramento, consistencia, null, espera, periodoIni, periodoFim, lista_infracoes, idRemessa, amostra);
	    	infracoes_size = infracoes.size();
	}
%>
<%@page import="com.consilux.model.Remessa"%>
<%@page import="com.consilux.model.InfracaoSimplificada"%>
<%@page import="com.consilux.model.Processamento.EtapaProcesso"%>
<%@page import="com.consilux.model.Processamento"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="java.util.StringTokenizer"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.exportalista.ExportaLista"%>
<%@page import="com.consilux.exportalista.InfracaoCompletaBean"%>
<c:set var="etapaProcesso" value="<%=etapa.ordinal()%>" scope="request"/>
<c:set var="data_atualizado" value="<%=data_atualizado%>"/>
<c:set var="total_infracoes" value="<%=total_infracoes%>"/>
<c:set var="infracoes_size" value="<%=infracoes_size%>"/>
<c:set var="infracoes" value="<%=infracoes%>"/>
<c:set var="id_remessa" value="<%=idRemessa%>"/>
<c:set var="remessa_at" value="<%=null%>" scope="request"/>
<c:set var="id_enquadramento" value="<%=idEnquadramento%>"/>
<c:set var="mostrarTodos" value="<%=intMostrarTodos%>"/>
<c:set var="espera" value="<%=espera%>"/>
<c:set var="revisao" value="<%=revisao%>"/>
<c:set var="amostra" value="<%=amostra%>"/>
<c:set var="id_processo" value="<%=etapa.getId()%>"/>
<c:set var="processa_direto" value="<%=true%>"/>
<c:set var="consistencia" value="<%=sConsistencia%>"/>
<c:set var="infracoes_pre_selecionadas" value="<%=(lista_infracoes != null ? lista_infracoes.size() : 0)%>"/>
<c:set var="remessas" value="<%=remessas_ht.values()%>"/>
<c:set var="list_infracao_action" value="/processo/IniciarProcesso"/>
<c:set var="btn_processo_caption" value="Iniciar&nbsp;Validação&nbsp;100%"/>
<c:set var="btn_processo_amostra_caption" value="Iniciar&nbsp;Validação&nbsp;por&nbsp;Amostra"/>
<%@include file="/WEB-INF/templates/processo/list_infracao_validacao.jsp"%>
<%@include file="/includes/rodape.jsp"%>
<%
dt2 = Calendar.getInstance().getTime();
logger.info("Carregou pagina em " + (dt2.getTime() - dt1.getTime()) + " ms");
%>