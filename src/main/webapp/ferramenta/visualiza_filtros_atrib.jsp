<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>

<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="com.consilux.model.Enquadramento"%>

<%@page import="com.consilux.model.Processo"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.Enquadramento"%>
<%@page import="com.consilux.model.beans.UsuarioBean"%>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<%@page import="com.consilux.model.Grupo"%>
<%@page import="com.consilux.model.Veiculo"%>
<%@page import="com.consilux.model.VeiculoCompletoLista"%>
<%@page import="com.consilux.model.Inconsistencia"%>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">


    function desabilitar(idFiltro) {
        window.location = '/ferramenta/DesabilitaFiltroServlet?id_filtro='+idFiltro;
    }

    function confirmarDesabilitar(idFiltro){
        var ok = confirm("O filtro não poderá ser habilitado novamente. Deseja desabilitar o filtro?");
        if (ok){
        	desabilitar(idFiltro);
        }
    }
    
</script>
<body onunload="infracoesPreSelecionadas()">
<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
            	<input type="hidden" id="idFiltro" name="idFiltro">
	            <table class="tabela_lista"  width="900" >
					<tr>
						
					</tr>
				
					<tr>
						<th class="head_tabela" width="100%" colspan="12">Listar Filtros</th>
					</tr>
					<tr>
					<tr>
						<td colspan="12">
							<form action="" method="get">
	            				<table class="tabela_lista" width="100%">
									<tr>
										<td width="3%"><input type="checkbox" name="incluirExpirados" value="1" onclick="submit();" ${param.incluirExpirados == '1' ? 'checked' : ''} ></td>
										<td class="dado_lista_tabela_claro" align="left" width="97%">Mostrar inativos.</td>
									</tr>
								</table>
							</form>
						
						</td>
					</tr>
					
					<%-- ***************************************************************** --%>
	                <%-- 1ª Linha (labels) --%>
	                <%-- ***************************************************************** --%>
					<tr align="center">
						
	                    <td class="label_campo" align="center">&nbsp;Desabilitar</td>
	                    <td class="label_campo" align="center">&nbsp;ID Filtro</td>
	                    <td class="label_campo" align="center">&nbsp;Nome</td>
	                    <td class="label_campo" align="center">&nbsp;Enquadramento</td>
	                    <td class="label_campo" align="center">&nbsp;Processo</td>
	                    <td class="label_campo" align="center">&nbsp;Local</td>
	                    <td class="label_campo" align="center">&nbsp;Classe</td>
	                    <td class="label_campo" align="center">&nbsp;Inconsistencia</td>
	                    <td class="label_campo" align="center">&nbsp;Data Inicial</td>
	                    <td class="label_campo" align="center">&nbsp;Data Final</td>
	                    <td class="label_campo" align="center">&nbsp;Validade</td>
	                    <td class="label_campo" align="center">&nbsp;SQL Query</td>
					</tr>
					
	                <%-- ***************************************************************** --%>
	                <%-- 2ª Linha (campos) --%>
	                <%-- ***************************************************************** --%>
	                
                	 <c:forEach var="filtro" varStatus="linhaInfo" items="${filtroBean}">
               	 		<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                			<tr align="center">
	                			<c:if test="${filtro.dtValidade <= agora}">
	                				<td class="${css_td}" align="center">
	                	 				Desabilitado
	                	 			</td>
	                			</c:if>
                				<c:if test="${filtro.dtValidade > agora || filtro.dtValidade == null}">
	                	 			<td class="${css_td}" align="center">
	                	 				<input type="button" value="Desabilitar" onclick="javascript: confirmarDesabilitar('${filtro.idFiltro}');"></input>
	                	 			</td>
	                			</c:if>
	                			<td class="${css_td}" align="center">${filtro.idFiltro}</td>
	                    		<td class="${css_td}" align="center">${filtro.nomeFiltro}</td>
	                    		<td class="${css_td}" align="center">${filtro.idFiltroEnquadramento != 0 ? filtro.idFiltroEnquadramento : ''}</td>
	                    		<td class="${css_td}" align="center">${filtro.idFiltroProcesso != 0 ? filtro.idFiltroProcesso : ''}</td>
		                		<td class="${css_td}" align="center">${filtro.idLocal != 0 ? filtro.idLocal : ''}</td>
		                		<td class="${css_td}" align="center">${filtro.idclasse == ' ' ? 'Não identificados' : filtro.idclasse}</td>
		                		<td class="${css_td}" align="center">${filtro.setIdInconsistencia}</td>
		                		<td class="${css_td}" align="center"><fmt:formatDate value="${filtro.dtIni}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
		                		<td class="${css_td}" align="center"><fmt:formatDate value="${filtro.dtFim}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
		                		<td class="${css_td}" align="center"><fmt:formatDate value="${filtro.dtValidade}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
    	                			<td class="${css_td}" align="center">${filtro.sqlCriterio == null || desenvolvedor ? filtro.sqlCriterio : 'ND'}</td>
                   			</tr>
                       </c:forEach> 
					</table>
					</td>
					</tr>
			</table>
</body>
					
<%@ include file="/includes/rodape.jsp" %>