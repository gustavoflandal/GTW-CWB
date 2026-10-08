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
                    <th class="head_tabela" width="25%">Usuario</th>
                    <th class="head_tabela" width="60%">Nome</th>
                    <th class="head_tabela" width="15%">Ação</th>
                </tr>
                <c:forEach var="usuario" varStatus="linhaInfo" items="${usuarios}">
                    <tr>
                        <c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                        <td class="${css_td}" align="center"><a href="visualizar_usuario.jsp?id_usuario=${usuario.id}" class="link_td">${usuario.usuario}</a></td>
                        <td class="${css_td}" align="left">${usuario.nome}</td>
                        <td class="${css_td}" align="center"><a href="${frm_action}?id_usuario=${usuario.id}" class="link_td">${frm_text}</a></td>
                    </tr>
                </c:forEach>
            </table>
            <br />
        </td>
    </tr>
</table>
<br />
<br />