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
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_rel" action="/relatorio/rel_produtividade_detalhado.jsp" method="get">
                <table class="tabela_branca" width="500">
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">Produtividade Detalhado</th>
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
                        <td class="label_campo">Janela de Minutos:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_janela_minutos" type="text" name="janela_minutos" class="campo_texto" maxlength="10" style="width: 70px">
                        </td>
                    </tr>
					<tr>
						<td class="box_botoes" colspan="5" width="100%">
							<button id="btEnvio" onclick="enviar('btEnvio','frm_rel','div_mens');">Visualizar</button>
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
