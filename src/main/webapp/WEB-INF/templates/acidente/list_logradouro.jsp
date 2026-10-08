<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
    <tr>
        <td align="center">
            <table class="tabela_lista" width="770">
                <tr>
                    <th class="head_tabela" width="85%">Logradouro</th>
                    <th class="head_tabela" width="15%">Ação</th>
                </tr>
                <c:forEach var="logradouro" varStatus="linhaInfo" items="${logradouros}">
                    <tr>
                        <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                        <td class="${css_td}" align="center"><a href="visualizar_logradouro.jsp?id_logradouro=${logradouro.id}" class="link_td">${logradouro.descricao}</a></td>
                        <td class="${css_td}" align="center"><a href="${frm_action}?id_logradouro=${logradouro.id}" class="link_td">${frm_text}</a></td>
                    </tr>
                </c:forEach>
            </table>
            <br />
        </td>
    </tr>
</table>
<br />
<br />