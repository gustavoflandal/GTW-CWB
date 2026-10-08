<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.consilux.model.Local"%>

<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_localsamples" action="/ExportaAmostras" method="get">
				<table class="tabela_branca" width="300">
					<tr>
						<th class="head_tabela" width="100%" colspan="3">Amostras Imagens</th>
					</tr>
					<tr>
						<td class="label_campo" width="40%">De:</td>
                        <td class="valor_campo">
                            <input id="txt_dataini" type="text" name="dataini" class="campo_texto" maxlength="10" style="width: 110px">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataini'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Até:</td>
                        <td class="valor_campo">
                            <input id="txt_datafim" type="text" name="datafim" class="campo_texto" maxlength="10" style="width: 110px">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_datafim'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
					<tr>
						<td class="box_botoes" colspan="5" width="100%">
							<button id="btEnvio" onclick="this.submit()">Gerar</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="2" width="100%">(necessário Zip Manager)</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
