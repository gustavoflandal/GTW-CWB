<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%

	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("id_grupo_equipamento",((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
	List<Usuario> usuarios = Usuario.buscaUsuarioPor(mFiltro);
%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.Usuario"%>

<c:set var="usuarios" value="<%=usuarios%>" />
<br />
<br />
<br />
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">
function ajustaDatas() {
	var txt_dataini = document.getElementById("txt_dataini");
	var txt_datafim = document.getElementById("txt_datafim");
	var dtAgora = new Date();
	var dtAntes = new Date();
	dtAntes.setDate(dtAntes.getDate()-5);
	
	
	if (txt_dataini.value == "") {
		txt_dataini.value = adicZeroEsquerda(dtAntes.getDate(),2)+"/"+adicZeroEsquerda(dtAntes.getMonth()+1,2)+"/"+dtAntes.getFullYear();
	}
	if (txt_datafim.value == "") {
		txt_datafim.value = adicZeroEsquerda(dtAgora.getDate(),2)+"/"+adicZeroEsquerda(dtAgora.getMonth()+1,2)+"/"+dtAgora.getFullYear();
	}
}</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_grafic" action="/relatorio/rel_produtividade_operadores.jsp" method="get">
                <table class="tabela_branca" width="500">
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">Relatório de Produtividade</th>
                    </tr>
                    <tr>
                        <td class="label_campo" width="25%">Usuario:</td>
                        <td class="valor_campo" width="55%">
                            <select id="sel_usuario" name="usuario">
                                <option value="0" selected="selected">--usuario--</option>
                                <c:forEach var="usuario" items="${usuarios}">
                                    <option value="${usuario.id}">${usuario.usuario}</option>
                                </c:forEach>
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
						<td class="box_botoes" colspan="5" width="100%">
							<button id="btEnvio" onclick="enviar('btEnvio','frm_grafic','div_mens');">Visualizar</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="3" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
	ajustaDatas();
    document.getElementById("btEnvio").disabled = false;
</script>
<%@ include file="/includes/rodape.jsp" %>
