<%@page import="com.consilux.model.IntegracaoSequenciaImagem"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_gwt.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%

String s_horas = request.getParameter("horas");
int horas = 72;

if (s_horas != null)
	horas = Integer.parseInt(s_horas);

List<IntegracaoSequenciaImagem> isis = IntegracaoSequenciaImagem.ObterIntegracaoSequenciaImagens(horas); 

%>

<c:set var="horas" value="<%=horas%>" />

<br />
<br />
<br />
<script type="text/javascript" src="/js/download.jQuery.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>

<script type="text/javascript">

function mostra_mensagem(linha) {
	var input_linha = document.getElementById("mensagem_erro_"+linha);
	if (input_linha) {
		alert(input_linha.value);
	}
	return false;
}
	
</script>

<center>
<form>
<table>
<tr>
<td>Filtro de Horas:</td>
<td><select name="horas" id="sel_horas" onchange="submit();">
  <option ${horas == 72 ? 'selected' : ''} value="72">72 horas</option>
  <option ${horas == 48 ? 'selected' : ''} value="48">48 horas</option>
  <option ${horas == 24 ? 'selected' : ''} value="24">24 horas</option>
  <option ${horas == 12 ? 'selected' : ''} value="12">12 horas</option>
  <option ${horas == 2 ? 'selected' : ''} value="2">2 horas</option>
</select></td>
</tr>
</table>
</form>
</center>

<table class="tabela_branca" width="100%">
<thead>
<tr>
<td class="head_tabela" >Local</td>					<td class="head_tabela" >Pista</td>						<td class="head_tabela" >Nº Seq.</td>
<td class="head_tabela" >Inc</td>					<td class="head_tabela" >Data da Imagem</td>			<td class="head_tabela" >Nome Arquivo GCT</td>
<td class="head_tabela" >Nome Arquivo CETAI</td>	<td class="head_tabela" >Nome Arquivo Consilux</td>
</tr>
</thead>	

<tbody>

<c:forEach var="isi" varStatus="linhaInfo" items="<%=isis%>">
<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
<c:if test="${linhaInfo.count > 1 && isi.incrementoAnterior == 0}">
<tr>
<td class="dado_lista_tabela_claro" colspan="8" align="center">&nbsp;</td>
</tr>
</c:if>
<c:if test="${isi.incrementoAnterior > 1}">
<tr>
<td class="dado_lista_tabela_claro" colspan="8" align="center"><h1>ATENÇÃO!!! Furo de Numeração Sequencial Detectado!!! Verificar registro abaixo!!</h1></td>
</tr>
</c:if>
<tr>
<td class="${css_td}" align="center">${isi.idLocal}</td>						<td class="${css_td}" align="center">${isi.idPista}</td>										
<td class="${css_td}" align="center">${isi.idImagemLocal}</td>					<td class="${css_td}" align="center">${isi.incrementoAnterior}</td>		
<td class="${css_td}" align="center"><fmt:formatDate value="${isi.dataHora}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
<c:if test="${isi.tamanho > 0}">
<td class="${css_td}" align="left"><a href="/Integracao/ArquivoIntegracao?URL=${isi.URL}"> ${isi.nomeIntegracaoGct} </a>	<fmt:formatNumber type = "number"  maxFractionDigits = "0" value = "${isi.tamanho}" />		</td>
</c:if>
<c:if test="${isi.tamanho == 0}">
<td class="${css_td}" align="left">${isi.nomeIntegracaoGct} 			</td>
</c:if>	
<c:if test="${isi.arquivoRI}">
<c:if test="${isi.processado}">
<td class="${css_td}" align="center" style="color: green;">${isi.nomeIntegracao}
</td>
</c:if>
<c:if test="${!isi.processado}">
<td class="${css_td}" align="center" style="color: red;">${isi.nomeIntegracao}
<c:if test="${isi.mensagemErro != null}">
<input type="hidden" id="mensagem_erro_${linhaInfo.count}" name="mensagem_erro" value="${isi.mensagemErro}">
<a href="#" onclick="mostra_mensagem(${linhaInfo.count})">ERRO</a>
</c:if>
</td>
</c:if>
</c:if>
<c:if test="${!isi.arquivoRI}">
<td class="${css_td}" align="center">${isi.nomeIntegracao}</td>
</c:if>
<td class="${css_td}" align="center">${isi.nomeConsilux}</td>
</tr>
</c:forEach>
</tbody>
</table>

<%@ include file="/includes/rodape.jsp" %>

