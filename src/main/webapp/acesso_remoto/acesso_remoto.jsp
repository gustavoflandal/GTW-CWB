<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%
	String sLocal = request.getParameter("id_local");

	if (sLocal == null || !ExpValida.INTEIRO.validar(sLocal)) {
		new Mensagem(response).showErro("Local enviado invalido!");
		return;
	}
	
	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("local", Integer.parseInt(sLocal));

	List<LocalStatus> locais = LocalStatus.buscaLocalStatusPor(mFiltro);

	if (locais.size() == 0) {
		new Mensagem(response).showErro("Local não encontrado!","javascript:window.close();");
		return;
	}
	
	String sHost = locais.get(0).getIp();
	Integer serieEquipamento = locais.get(0).getSerieEquipamento();

	Usuario usuarioAtual = (Usuario) request.getSession().getAttribute("[usuario]");	
	
	Evento.incluirEventoCSX(new EventoCSX(EventoCSX.TipoEvento.REMOTE_OPENED,
			usuarioAtual.getUsuario(), Integer.toString(serieEquipamento)));
%>

<%@page import="com.consilux.infra.ExpValida"%>
<%@page import="com.consilux.model.Mensagem"%>

<%@page import="com.consilux.model.LocalStatus"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.consilux.model.Evento"%>
<%@page import="com.consilux.model.EventoCSX"%>
<%@page import="com.consilux.model.EventoCSX.TipoEvento"%>
<%@page import="com.consilux.model.Usuario"%>
<c:set var="host" value="<%=sHost%>" scope="request"/>
<c:set var="id_local" value="<%=sLocal%>" scope="request"/>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript">
	function avisa_vivo(idLocal) {
		$.get('/AcessoRemotoPolling', { id_local: idLocal }, function(xml) {
			if (trataRetorno(xml)) {
				if ($("RETORNO",xml).text() != 'OK') {
					alert('Conexão com o servidor GTW perdida!');
				}
				else {
					window.setTimeout("avisa_vivo("+idLocal+");", 5000);
				}
			}
			else {
				alert(4);
			}
		});
	}
</script>
<table class="tabela_branca" width="100%">
	<applet code="AcessoRemoto.class" width="1028" height="796" archive="/acesso_remoto/gtw_acesso_remoto.jar">
		 <param name="host" value="${host}">   
		 <param name="Show Controls" value="Yes">
	</applet>   
</table>
<br />
<br />
<script type="text/javascript">
	avisa_vivo(${id_local});
</script>
<%@ include file="/includes/rodape.jsp" %>
