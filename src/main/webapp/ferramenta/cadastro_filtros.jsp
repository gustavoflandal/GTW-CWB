<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%
    List<Enquadramento> enquadramentos = Enquadramento.buscaTodosEnquadramentos();
	List<ClasseVeiculo> classeVeiculo = ClasseVeiculo.buscaTodasClassesVeiculo();
	
    Map<String,Object> mFiltro = new HashMap<String,Object>();
    mFiltro.put("grupo", ((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
    List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,2);
    
    List<Processo> processos = Processo.buscaTodosProcessos();
    List<Inconsistencia> inconsistencias = Inconsistencia.buscaTodasInconsistencias(true);
    
    List<Processo> processosDe = Processo.buscaProcessosExecucaoAutomatica();
    
    Integer id_usuario = ((Usuario)session.getAttribute("[usuario]")).getId();
    
    Boolean desenvolvedor = false;
    
   	Configuracao conf = ConfiguracaoProvider.getInstance();
   	desenvolvedor = Usuario.usuarioPertenceAoGrupo(id_usuario, conf.getIdGrupoDesenvolvedores());

	String disabled = (String)request.getAttribute("desabilitar");
%>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="com.consilux.model.Usuario"%>

<%@page import="com.consilux.model.Processo"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="com.consilux.model.ClasseVeiculo"%>
<%@page import="com.consilux.model.beans.UsuarioBean"%>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<%@page import="com.consilux.model.Grupo"%>
<%@page import="com.consilux.model.Veiculo"%>
<%@page import="com.consilux.model.VeiculoCompletoLista"%>
<%@page import="com.consilux.model.Inconsistencia"%>
<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%><script type="text/javascript" src="/js/calendario.js"></script>
<c:set var="enquadramentos" value="<%=enquadramentos%>" />
<c:set var="classeVeiculo" value="<%=classeVeiculo%>" />
<c:set var="locais" value="<%=locais%>" />
<c:set var="disabled" value="<%=disabled%>" />
<c:set var="processosDe" value="<%=processosDe%>" />
<c:set var="processos" value="<%=processos%>" />
<c:set var="desenvolvedor" value="<%=desenvolvedor%>" />
<c:set var="inconsistencias" value="<%=inconsistencias%>" />
<c:set var="mostra_detalhes" value="<%=true%>" />
<%@ include file="/ferramenta/cadastra_filtros_atrib.jsp"%>
<%@ include file="/includes/rodape.jsp" %>