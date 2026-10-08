<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%
	String sFiltroRelatorio = request.getParameter("filtroRelatorio");

	if (sFiltroRelatorio != null && !Pattern.matches("[0-2]",sFiltroRelatorio)) {
	    new Mensagem(response).showErro("Filtro enviado inválido!");
	    return;
	}
	
	Boolean consistentes = null;
	
	if ("1".equals(sFiltroRelatorio))
		consistentes = true;
	else if ("2".equals(sFiltroRelatorio))
		consistentes = false;
	
	List<Integer> enquadramentos = new ArrayList<Integer>();
	RelatorioItr<ImagensEmProcessamento> itr = new RelatorioItr<ImagensEmProcessamento>(new ImagensEmProcessamento(consistentes));
	TabelaImagensEmProcessamento dados = new TabelaImagensEmProcessamento(itr);
	
	List<DadosPendentesImportacao> itrDadosImp = DadosPendentesImportacao.buscaDadosPendentesImportacao();
	 
%>
<%@page import="com.consilux.model.Acesso"%>
<%@page import="com.consilux.model.ImagensEmProcessamento"%>
<%@page import="com.consilux.model.DadosPendentesImportacao"%>
<%@page import="com.consilux.infra.RelatorioItr"%>
<%@page import="com.consilux.ui.tabela.TabelaImagensEmProcessamento"%>

<%@page import="java.util.regex.Pattern"%>
<c:set var="dados" value="<%=dados%>"/>
<c:set var="itrDadosImp" value="<%=itrDadosImp%>"/>
<c:set var="processoAnterior" value=""/>
<c:set var="enquadramentos" value="<%=dados.getEnquadramentos()%>" />
<c:set var="num_enquadramentos" value="<%=dados.getEnquadramentos().size()%>"/>
<c:set var="tp" value="0"/>
<c:set var="tg" value="0"/>
<br />

<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_branca" width="800">
				<tr>
					<form action="" method="get">
						<td width="33%" class="dado_lista_tabela_claro"><input type="radio" name="filtroRelatorio" value="0" onchange="submit();" ${param.filtroRelatorio == '0' ? 'checked' : ''} />Todos</td>
						<td width="34%" class="dado_lista_tabela_claro"><input type="radio" name="filtroRelatorio" value="1" onchange="submit();" ${param.filtroRelatorio == '1' ? 'checked' : ''} />Consistente</td>
						<td width="33%" class="dado_lista_tabela_claro"><input type="radio" name="filtroRelatorio" value="2" onchange="submit();" ${param.filtroRelatorio == '2' ? 'checked' : ''} />Inconsistente</td>
					</form>
				</tr>
			</table>
			<table class="tabela_lista" width="800">
				<tr>
					<th class="head_tabela" colspan="${3+num_enquadramentos}">Imagens em Processamento</th>
				</tr>
				<tr>
					<th class="head_tabela" align="center" width="10%" rowspan="2">Data</th>
					<th class="head_tabela" align="center" width="20%" rowspan="2">Quantidade de Infrações</th>
					<th class="head_tabela" align="center" width="60%" colspan="${num_enquadramentos}">Enquadramentos</th>
					<th class="head_tabela" align="center" width="10%" rowspan="2">em espera</th>
				</tr>
				<tr>
					<c:forEach var="enquadramento" items="${enquadramentos}">
						<th class="head_tabela" align="center">${enquadramento}</th>
					</c:forEach>
				</tr>
				
				<c:forEach var="linha" varStatus="linhaInfo" items="${dados}">
					<c:set var="totallinha" value="${linha.total}" />
					<c:if test="${linha.nomeProcesso != processoAnterior}">
						<c:if test="${processoAnterior != ''}">
							<tr>
								<th class="head_tabela" align="left" colspan="2">Total ${processoAnterior}</th>
								<th class="head_tabela" align="center" colspan="${1+num_enquadramentos}">${tp}</th>
							</tr>
							<c:set var="tp" value="0" />
							<tr>
								<td colspan="3">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="3">&nbsp;</td>
							</tr>
						</c:if>
						<tr>
							<td class="head_tabela" align="center" colspan="${3+num_enquadramentos}" bgcolor="darkgray">${linha.nomeProcesso}</td>
						</tr>
					</c:if>
					<c:set var="tp" value="${tp + totallinha}" />
					<c:set var="tg" value="${tg + totallinha}" />
					<tr>
						<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						<td class="${css_td}" align="center">
							<c:choose>
								<c:when test="${linha.atrasado}">
									<font color="red">
										<fmt:formatDate value="${linha.data}" type="date" pattern="dd/MM/yyyy" />
									</font>
								</c:when>
								<c:otherwise>
									<fmt:formatDate value="${linha.data}" type="date" pattern="dd/MM/yyyy" />
								</c:otherwise>
							</c:choose>
						</td>
						<td class="${css_td}" align="center">${linha.total}</td>
						<c:forEach var="enquadramento" items="${enquadramentos}">
							<td class="${css_td}" align="center">
								${linha.totalEnquadramento[enquadramento] != null ? linha.totalEnquadramento[enquadramento] : 0}
							</td>	
						</c:forEach>
						<td class="${css_td}" align="center">${linha.espera ? 'sim' : 'não'}</td>
					</tr>
					<c:set var="processoAnterior" value="${linha.nomeProcesso}" />
				</c:forEach>
	
				<tr align="center" >
				<th class="head_tabela" align="left" colspan="2">Total ${processoAnterior}</th>
				<th class="head_tabela" align="center" colspan="${1+num_enquadramentos}">${tp}</th>
				</tr>
							
				<tr><td colspan="3">&nbsp;</td></tr>
				<tr><td colspan="3">&nbsp;</td></tr>
				
				
				<tr align="center" >
					<th class="head_tabela" align="center" colspan="${3+num_enquadramentos}" width="100%">Total Geral</th>
				</tr>
				<tr align="center" >
					<td class="dado_lista_tabela_claro" align="center" colspan="${3+num_enquadramentos}" width="100%"><b>${tg}</b></td>
				</tr>
			</table>
			<br/>
			<br/>
			<table class="tabela_lista" width="700">
				<tr>
					<th class="head_tabela" align="center" colspan="3">Dados pendentes de importação</th>
				</tr>
				<tr>
					<th class="head_tabela" align="center" width="40%">Data</th>
					<th class="head_tabela" align="center" width="30%">Quant. de veículos</th>
					<th class="head_tabela" align="center" width="30%">Quant. de veículos c/ imagem</th>
				</tr>
				<c:forEach var="itemDado" varStatus="linhaInfo" items="${itrDadosImp}">
					<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
					<tr>
						<td class="${css_td}" align="center"><fmt:formatDate value="${itemDado.data}" type="date" pattern="dd/MM/yyyy" /></td>
						<td class="${css_td}" align="center">${itemDado.totalVeiculos}</td>
						<td class="${css_td}" align="center">${itemDado.totalVeiculosComImagem}</td>
					</tr>
				</c:forEach>
			</table>
			<br/>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp"%>
