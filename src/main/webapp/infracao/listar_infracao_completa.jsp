<%@page import="com.consilux.model.ClasseVeiculo"%>
<%@page import="com.consilux.model.InfracaoCompletaLista"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>

<%
    Map<String,Object> mFiltro = new HashMap<String,Object>();
    mFiltro.put("grupo", ((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
    InfracaoCompletaBean.setListaParaRelatorio(null); //Limpando uma consulta prévia na memória.
    
    List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,4);
    if(locais.size()==0)
    {
    	locais = LocalVigente.buscaLocalVigenteCAV();
    }
    
    List<Enquadramento> enquadramentos = Enquadramento.buscaTodosEnquadramentos();
    List<Processo> processos = Processo.buscaTodosProcessos();
    List<Inconsistencia> inconsistencias = Inconsistencia.buscaTodasInconsistencias(true);
    List<EnquadramentoRegraInfracao> enquadRegraInfracao = EnquadramentoRegraInfracao.buscaTodosEnquadraRegra();
    List<Produto> produtos = Produto.buscarTodosProdutos();
    List<ClasseVeiculo> classesVeiculos = ClasseVeiculo.buscaTodasClassesVeiculoCAV();
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
<%@page import="com.consilux.model.EnquadramentoRegraInfracao"%>
<%@page import="com.consilux.model.Produto"%>

<c:set var="enquadramentos"      value="<%=enquadramentos%>" />
<c:set var="locais"              value="<%=locais%>" />
<c:set var="processos"           value="<%=processos%>" />
<c:set var="inconsistencias"     value="<%=inconsistencias%>" />
<c:set var="mostra_detalhes"     value="<%=true%>" />
<c:set var="enquadRegraInfracao" value="<%=enquadRegraInfracao%>" />
<c:set var="produtos"            value="<%=produtos%>" />
<c:set var="classesVeiculos"     value="<%=classesVeiculos%>" />

<script type="text/javascript" src="/js/calendario.js"></script>

<%@ include file="/WEB-INF/templates/infracao/consulta_infracao_completa.jsp"%>
<%@ include file="/includes/rodape.jsp" %>