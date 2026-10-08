<%@page import="javax.naming.spi.DirStateFactory.Result"%>
<%@page import="com.consilux.model.Remessa"%>
<%@page import="com.consilux.model.RemessaIteracao"%>
<%@page import="com.consilux.model.JobBuscaRemessasPendentes"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@include file="/includes/cabecalho.jsp"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%	

	RemessaIteracao ri = (RemessaIteracao) request.getSession().getAttribute("remessa_iteracao");
	Remessa remessa = null;
	boolean amostra = false;
	int validado = 0;
	int pendente = 0;
	boolean reprovado = false;
	
	if (ri == null) {
		response.sendRedirect("/login/gtw_principal.jsp");
	}
	else { 
		
	JobBuscaRemessasPendentes.AtualizarRemessa(ri.getIdRemessa());
	
	remessa = Remessa.buscarRemessaPorId(ri.getIdRemessa());
	amostra = remessa.possuiAmostra();
	validado = amostra ? (remessa.getTamanhoAmostraReal() - remessa.getTamanhoAmostraValidavel()) : remessa.getTotalInfracoesValidadas();
	pendente = amostra ? remessa.getTamanhoAmostraValidavel() : remessa.getTotalInfracoesValidaveis();
	reprovado = amostra && (remessa.getErrosProcessamento() >= remessa.getRe());
	
	if (pendente == 0) {
		ri.Finalizar();
	}
	}
%>

<c:set var="remessa" value="<%=remessa%>" scope="request"/>
<c:set var="ri" value="<%=ri%>" scope="request"/>
<c:set var="amostra" value="<%=amostra%>" scope="request"/>
<c:set var="validado" value="<%=validado%>" scope="request"/>
<c:set var="pendente" value="<%=pendente%>" scope="request"/>
<c:set var="reprovado" value="<%=reprovado%>" scope="request"/>

<br />
<br />

<table>
<tr>
<td width="200px"></td>
<td class="dado_lista_tabela_claro">
Processo iniciado em <fmt:formatDate value="${ri.dataInicio}" type="both" pattern="dd/MM/yyyy HH:mm:ss"/>
</td>
</tr>
<tr>
<td width="200px"></td>
<td class="dado_lista_tabela_claro">
Movimento de Lote: ${remessa.descricaoApait}
</td>
</tr>

<tr>
<td width="200px"></td>
<td class="dado_lista_tabela_claro">

<c:if test="${reprovado}">
Movimento de Lote <strong>Reprovado</strong> por exceder quantidade de erros permitida: ${remessa.errosProcessamento} / ${remessa.ac} Permitidos
</td>
</tr>
<tr>
<td width="200px"></td>
<td class="dado_lista_tabela_claro">
</c:if>

<c:if test="${pendente == 0}">
<c:if test="${amostra}">
Movimento de Lote concluído por Amostra
</c:if>
<c:if test="${!amostra}">
Movimento de Lote concluído 100%
</c:if>
</c:if>

<c:if test="${pendente > 0}">
<c:if test="${amostra}">
Movimento de Lote iniciado por Amostra.
</td>
</tr>
<tr>
<td width="200px"></td>
<td class="dado_lista_tabela_claro">
Imagens pendentes na Amostra: <strong>${pendente}</strong>
</c:if>
<c:if test="${!amostra}">
Movimento de Lote iniciado em 100%
</td>
</tr>
<tr>
<td width="200px"></td>
<td class="dado_lista_tabela_claro">
Imagens pendentes: <strong>${pendente}</strong>
</c:if>
</c:if>

</td>
</tr>

<tr>
<td width="200px"></td>
<td class="dado_lista_tabela_claro">
<a href="iniciar_validacao.jsp">Selecionar Próximo Movimento de Lote para Validação</a>

</td>
</tr>

<tr>
<td width="200px"></td>
<td class="dado_lista_tabela_claro">
<a href="/login/gtw_principal.jsp">Sair</a>
</td>
</tr>


</table>

<%@ include file="/includes/rodape.jsp" %>