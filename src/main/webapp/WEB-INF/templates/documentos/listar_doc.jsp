<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_branca" width="400"> 
				<tr>
					<td class="label_campo" colspan="3">Arquivos relacionados ao link:</td>
			    </tr>
				<tr>
					<th class="head_tabela" align="center" width="20%">Data</th>
					<th class="head_tabela" align="center" width="60%">Nome</th>
					<th class="head_tabela" align="center" width="20%">Ação</th>
				</tr>
				<c:forEach var="documento" varStatus="linhaInfo" items="${documentos}">
					<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
					<tr>
						<td class="${css_td}" align="center"><fmt:formatDate value="${documento.dataCriacao}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
						<td class="${css_td}" align="center">${documento.nomeArquivo}</td>
						<td class="${css_td}" align="center"><a class='link_td' href="/documentos/BaixarDoc?id_documento=${documento.idDocumento}">Ver</a></td>
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
