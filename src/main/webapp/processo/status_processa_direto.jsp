<%@page import="com.consilux.model.Mensagem"%><%@page import="com.consilux.model.Inconsistencia"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>

<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.infra.ExpValida"%>
<%@page import="com.consilux.model.Processamento"%><script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/progresso.js"></script>
<%
	String sTipo = request.getParameter("tipo");
	String sIdInconsistencia = request.getParameter("id_inconsistencia") != null ? request.getParameter("id_inconsistencia").trim() : null;
	sIdInconsistencia = sIdInconsistencia != null && sIdInconsistencia.length() == 0 ? null : sIdInconsistencia;
	
	Processamento proc = (Processamento)request.getSession().getAttribute("[processamento]");

	if (sTipo != null && !ExpValida.NATURAL_COM_ZERO.validar(sTipo)) {
	    new Mensagem(response).showErro("Tipo de ação enviado inválido!");
	    return;
	}
	if (sIdInconsistencia != null && !ExpValida.NATURAL_COM_ZERO.validar(sIdInconsistencia)) {
	    new Mensagem(response).showErro("Identificador da inconsistência enviado inválido!");
	    return;
	}
	if (proc == null) {
	    new Mensagem(response).showErro("Sessão não iniciada!");
	    return;
	}
	
	if ("2".equals(sTipo) && sIdInconsistencia != null) {
		Integer idInconsistencia = Integer.valueOf(sIdInconsistencia);
		proc.setIdInconsistencia(idInconsistencia);
	}
%>
<br />
<br />
<br />
<script type="text/javascript">
	function iniciaStatus() {
        var div_progresso = document.getElementById("div_progresso");
		var linha_botao = document.getElementById("linha_botao");
		
        disparaProgresso('/processo/ProcessarDireto', div_progresso, this.fim);
        linha_botao.style.display = "none";
	}
	function fim() {
		var linha_botao = document.getElementById("linha_botao");
		linha_botao.style.display = "";
	}
</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="400">
				<tr>
					<th class="head_tabela" width="100%" colspan="2">Processamento</th>
				</tr>
				<tr>
					<td class="corpo_mensagem" width="100%" colspan="2">Progresso:</td>
				</tr>
				<tr>
					<td class="visualiza_campo" width="100%" colspan="2" style="text-align: center"><div id="div_progresso">0</div><br /></td>
				</tr>
				<tr id="linha_botao">
					<td class="box_botoes" style="padding-top: 0px;"" colspan="2">
						<button id="btSair" onclick="window.location = '/processo/FinalizarProcesso'">Sair</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
	document.getElementById("btSair").focus();
	iniciaStatus();
</script>
<%@ include file="/includes/rodape.jsp" %>
