<%@ include file="/includes/cabecalho.jsp" %>
<%@page import="com.consilux.model.Processo"%>
<%@page import="java.util.Collection"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="f" %>
<%
	List<Processo> processosRemessa = Processo.buscaProcessosRemessa();
%>
<c:set var="processo" value="<%=processosRemessa%>"/>
<%@page import="com.consilux.model.TipoRemessa"%><script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>
<script type="text/javascript" src="/js/CsxRemoteObject.js"></script>
<script type="text/javascript" src="ImpNotificacao.js"></script>

<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
			<form name="frm_listar" action="" target="listar" method="post">
				<table class="tabela_branca" width="600" >
					<tr>
						<th class="head_tabela" width="100%" colspan="5">Notificação AIT</th>
					</tr>

					<tr>
						<td class="label_campo" width="30%">Notificação por infração:</td>
						<td class="valor_campo" width="100%" colspan="4">
							<select id="tipo_remessa" name="tipo_remessa">
								<option value="0">TODOS</option>
								<c:forEach var="processo" items="<%=processosRemessa%>">
                               		<option value="${processo.idProcesso}">${processo.nome}</option>
                            	</c:forEach>
							</select>
						</td>
					</tr>
					
					<tr>
						<td class="label_campo" >Tipo notificação: </td>
						<td class="valor_campo" colspan="4">
							<input id="tipo_nai" type="radio" name="tipo_notif" class="campo_texto" value="NAI"/>NAI
							<input id="tipo_nip" type="radio" name="tipo_notif" class="campo_texto" value="NIP"/>NIP
							<select id="sel_id_tipo_impressao" name="tipo_impressao" style="width: 50%">
								<option value="0" >NAI</option>
								<option value="1">NIP</option>
							</select>
						</td>
					</tr>
					
					<tr>
						<td class="label_campo">Data inicial:</td>
						<td class="valor_campo" colspan="4">
							<input id="txt_dataini" type="text" name="dataini" class="campo_texto" maxlength="10" style="width: 110px">
							<img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataini'), 'dd/mm/yyyy')">
						</td>
					</tr>
					
					<tr>
						<td class="label_campo">Data final:</td>
						<td class="valor_campo" colspan="4">
							<input id="txt_datafim" type="text" name="datafim" class="campo_texto" maxlength="10" style="width: 110px">
							<img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_datafim'), 'dd/mm/yyyy')">
						</td>
					</tr>
					<tr>
						<td colspan="4" align="right">
							<input id="btnPesquisar" type="button" value="Pesquisar" onclick="btnPesquisar_OnClick()"/>
						</td>
					</tr>
					<tr>
						<td colspan="5">
							<div id="divTable"></div>
						</td>
					</tr>
				</table>
			</form>
	</tr>
</table>

<script type="text/javascript">
	ajustaDatas("txt_dataini", "txt_datafim");
    document.getElementById("sel_id_tipo_remessa").focus();
</script>
<%@ include file="/includes/rodape.jsp" %>