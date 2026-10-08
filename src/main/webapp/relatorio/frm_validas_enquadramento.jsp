<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
	String[] meses = new DateFormatSymbols(new Locale("pt", "BR")).getMonths();
	List<String> anos = new ArrayList<String>();
	for (int i = Calendar.getInstance().get(Calendar.YEAR) - 3; i < Calendar.getInstance().get(Calendar.YEAR) + 3; i++)
	{
		anos.add(Integer.toString(i));
	}
%>

<c:set var="meses" value="<%=meses%>" />
<c:set var="anos" value="<%=anos%>" />
<%@page import="java.util.TreeMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.DateFormatSymbols"%>
<%@page import="java.util.Locale"%><br/>
<br/>
<br/>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_validas_enquadramentos" action="/relatorio/RelatorioValidasEnquadramento" method="GET" target="_blank">
				<table class="tabela_branca" width="300">
					<tr>
						<th class="head_tabela" width="100%" colspan="2">Relatório</th>
					</tr>				
					<tr>
						<th class="head_tabela" width="100%" colspan="2">VálidasXEnquadramentoXFaixa</th>
					</tr>
					<tr>
	                    <td class="label_campo" width="40%">Mês:</td>
	                    <td class="valor_campo" width="60%">
	                    
                            <select id="sel_mes" name="mes" ">
							<option value="0" selected="selected">--mês--</option>
							<c:forEach var="i" begin="1" end="12" step="1" varStatus ="status">
								<option value="${i}">${meses[i-1]}</option> 
							</c:forEach>                                
                            </select>
                        </td>
					</tr>
					<tr>
	                    <td class="label_campo" width="40%">Ano:</td>
	                    <td class="valor_campo" width="60%">
                            <select id="sel_ano" name="ano" ">
                                <option value="-1" selected="selected">--ano--</option>
                                <c:forEach var="ano" items="${anos}">
									 <option value="${ano}">${ano}</option>
								</c:forEach>
                            </select>
                        </td>
					</tr>
					<tr>
	                    <td class="label_campo" width="40%">Válidas:</td>
	                    <td class="valor_campo" width="60%">
	                    	<input type="checkbox" id="check_validas" name="validas" checked="checked"></input>
                        </td>	                    
					</tr>					
					<tr>
						<td class="box_botoes" colspan="2">
							<button onclick="document.getElementById('frm_validas_enquadramentos').submit()">Visualizar</button><br>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="2">(necessário Excel)</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
