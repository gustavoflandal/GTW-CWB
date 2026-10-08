<%@page import="com.consilux.model.ClasseVeiculo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%
	String sIdProcesso = request.getParameter("id_processo") != null ? request.getParameter("id_processo").trim() : null;

    List<Enquadramento> enquadramentos = Enquadramento.buscaTodosEnquadramentos();

    Map<String,Object> mFiltro = new HashMap<String,Object>();
    mFiltro.put("grupo", ((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
    List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,2);
    
    List<Processo> processos = Processo.buscaTodosProcessos();
    List<Inconsistencia> inconsistencias = Inconsistencia.buscaTodasInconsistencias(true);
    List<Produto> produtos = Produto.buscarTodosProdutos();
    List<ClasseVeiculo> classesVeiculos = ClasseVeiculo.buscaTodasClassesVeiculoCAV();
    
    InfracaoCompletaBean.setListaParaRelatorio(null); //Limpando uma consulta prévia na memória.
%>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.consilux.model.Enquadramento"%>

<%@page import="com.consilux.model.Processo"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="com.consilux.model.beans.UsuarioBean"%>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<%@page import="com.consilux.model.Grupo"%>
<%@page import="com.consilux.model.Veiculo"%>
<%@page import="com.consilux.model.VeiculoCompletoLista"%>
<%@page import="com.consilux.model.Inconsistencia"%>
<%@page import="com.consilux.exportalista.InfracaoCompletaBean"%>
<%@page import="com.consilux.model.Produto"%>
<%@page import="com.consilux.model.Usuario"%>
<c:set var="enquadramentos" value="<%=enquadramentos%>" />
<c:set var="locais" value="<%=locais%>" />
<c:set var="id_processo" value="<%=sIdProcesso%>" />
<c:set var="processos" value="<%=processos%>" />
<c:set var="inconsistencias" value="<%=inconsistencias%>" />
<c:set var="produtos" value="<%=produtos%>" />
<c:set var="classesVeiculos" value="<%=classesVeiculos%>" />
<c:set var="mostra_detalhes" value="<%=false%>" />
<script type="text/javascript" src="/js/calendario.js"></script>
<%@ include file="/WEB-INF/templates/infracao/consulta_infracao_completa.jsp"%>
<%@ include file="/includes/rodape.jsp" %>