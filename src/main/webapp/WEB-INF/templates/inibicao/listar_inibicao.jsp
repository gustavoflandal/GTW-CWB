<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript">
	function desativar(idInibicaoInfracao) {
	   if (confirm("Deseja realmente desativar a inibição '"+idInibicaoInfracao+"'?")) {
		   $.get('/ferramenta/DesativarInibicao', {
			      id_inibicao_infracao: idInibicaoInfracao
		          }, function(xml) {
		        	  alert('Inibição desativada.');
		        	  window.location.reload();
		        });
	   }
	}
    function visualizar(idInibicaoInfracao) {
        window.location = '/ferramenta/VisualizarInibicao?id_inibicao_infracao='+idInibicaoInfracao;
    }

</script>
<br>
<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
            	<input type="hidden" id="idFiltro" name="idFiltro">
	            <table class="tabela_lista"  width="900" >
					<tr>
						
					</tr>
				
					<tr>
						<th class="head_tabela" width="100%" colspan="7">Listar Inibições</th>
					</tr>
					<tr>
						<td colspan="7">
							<form action="" method="get">
	            				<table class="tabela_lista" width="100%">
									<tr>
										<td width="3%"><input type="checkbox" name="mostrar_inativos" value="1" onclick="submit();" ${param.mostrar_inativos == '1' ? 'checked' : ''} ></td>
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
	                    <td class="label_campo" align="center" width="10%">&nbsp;ID Inibição</td>
	                    <td class="label_campo" align="center" width="40%">&nbsp;Nome</td>
	                    <td class="label_campo" align="center" width="10%">&nbsp;Enquadramento</td>
	                    <td class="label_campo" align="center" width="10%">&nbsp;Data Inicial</td>
	                    <td class="label_campo" align="center" width="10%">&nbsp;Data Final</td>
	                    <td class="label_campo" align="center" width="20%" colspan="2">&nbsp;Ação</td>
					</tr>
					
	                <%-- ***************************************************************** --%>
	                <%-- 2ª Linha (campos) --%>
	                <%-- ***************************************************************** --%>
	                
                	 <c:forEach var="inibicao" varStatus="linhaInfo" items="${lista}">
               	 		<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
                			<tr align="center">
	                			<td class="${css_td}" align="center">${inibicao.bean.idInibicaoInfracao}</td>
	                			<td class="${css_td}" align="center">${inibicao.bean.descricao}</td>
	                			<td class="${css_td}" align="center">${inibicao.bean.idEnquadramento}</td>
		                		<td class="${css_td}" align="center"><fmt:formatDate value="${inibicao.bean.dataInicio}" type="date" pattern="dd/MM/yyyy" /></td>
		                		<td class="${css_td}" align="center"><fmt:formatDate value="${inibicao.bean.dataFim}" type="date" pattern="dd/MM/yyyy" /></td>
   	                			<td class="${css_td}" align="center" colspan="2">
   	                				<input type="button" value="Visulizar" onclick="javascript: visualizar('${inibicao.bean.idInibicaoInfracao}');"></input>
   	                				<c:if test="${inibicao.ativo}">
   	                					<input type="button" value="Desativar" onclick="javascript: desativar('${inibicao.bean.idInibicaoInfracao}');"></input>
   	                				</c:if>
								</td>
                   			</tr>
                       </c:forEach> 
					</table>
				</td>
			</tr>
	</table>
<%@ include file="/includes/rodape.jsp" %>
