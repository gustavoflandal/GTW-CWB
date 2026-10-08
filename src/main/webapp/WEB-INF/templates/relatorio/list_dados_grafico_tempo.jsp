<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>

<%@page import="org.jfree.data.xy.XYDataItem"%>
<%@page import="java.util.Date"%><br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_branca">
				<tr>
					<td class="dado_lista_tabela_claro" align="left" width="100%">Dados do gráfico: </td>
				</tr>
			</table>
			<br />
			<table class="tabela_branca" width="100%">
			    <tr>
		            <c:forEach var="serie" varStatus="linhaInfo" items="${series}">
	                    <td align="center" valign="top">
							<table class="tabela_lista">
								<tr>
									<th class="head_tabela" width="100%" colspan="2">${serie.value.key}</th>
								</tr>
			                    <tr>
			                        <th class="head_tabela" width="50%">${grafico.legendaX}</th>
			                        <th class="head_tabela" width="50%">${grafico.legendaY}</th>
			                    </tr>
								<c:forEach var="item" varStatus="linhaInfo" items="${serie.value.items}">
									<tr>
			                            <c:set var="data" value='<%=new Date(((XYDataItem)pageContext.findAttribute("item")).getX().longValue())%>' />
										<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
										<td class="${css_td}" align="center"><fmt:formatDate value="${data}" type="both" pattern="dd/MM/yyyy" /></td>
			                            <td class="${css_td}" align="center"><fmt:formatNumber value="${item.y}" type="number" minFractionDigits="2" maxFractionDigits="2" /></td>
									</tr>
								</c:forEach>
							</table>
			                <br />
                        </td>
		            </c:forEach>
                </tr>
            </table>
		</td>
	</tr>
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
