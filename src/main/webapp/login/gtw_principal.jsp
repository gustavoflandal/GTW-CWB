<%@page import="com.consilux.model.ItemIndicadoresImportacao"%>
<%@page import="com.consilux.model.IndicadoresImportacao"%>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<%@page import="com.consilux.model.Grupo"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.model.IndicadoresProcessamento"%>
<%@page import="com.consilux.infra.SessaoConstantes"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<%@ include file="/includes/cabecalho.jsp"%>
<%
	String sUltimoAcesso = "";
	String sSessoes = "";
	String sVersao = "";
	String sNumeroBuild = "";

	Calendar cal = Calendar.getInstance();
	SimpleDateFormat formatoMes = new SimpleDateFormat("MMMM",new Locale("pt", "BR"));
	Date atual = cal.getTime();
	cal.add(Calendar.MONTH, -1);
	Date anterior = cal.getTime();
	String mesAtual = formatoMes.format(atual);
	String mesAnterior = formatoMes.format(anterior);

	// Indicadores de importação
	SimpleDateFormat formatoMesInteiro = new SimpleDateFormat("MM",new Locale("pt", "BR"));
	SimpleDateFormat formatoAnoInteiro = new SimpleDateFormat("yyyy",new Locale("pt", "BR"));
	Integer intMesAtual = Integer.parseInt(formatoMesInteiro.format(atual));
	Integer intAnoAtual = Integer.parseInt(formatoAnoInteiro.format(atual));
	Integer intMesAnterior = Integer.parseInt(formatoMesInteiro.format(anterior));
	Integer intAnoAnterior = Integer.parseInt(formatoAnoInteiro.format(anterior));
	Integer intDiasAtrasoMesAtual = 0;
	Integer intDiasAtrasoMesAnterior = 0;
	Boolean possuiIndicadorImportacao = false;
	
	SimpleDateFormat formatoInicioMes = new SimpleDateFormat("01/MM/yyyy", new Locale("pt", "BR"));
	SimpleDateFormat formataData = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
	
	Date inicioMesAtual = null;
	Date inicioMesAnterior = null;
	
	String strDataAtual = formatoInicioMes.format(atual);
	String strDataAnterior = formatoInicioMes.format(anterior);
	inicioMesAtual = formatoInicioMes.parse(strDataAtual);
	inicioMesAnterior = formatoInicioMes.parse(strDataAnterior);
	
	cal.setTime(inicioMesAtual);
	cal.add(Calendar.MONTH, 1);
	cal.add(Calendar.DAY_OF_YEAR, -1);
	Date fimMesAtual = cal.getTime();
	String strFimMesAtual = formataData.format(fimMesAtual);
	
	cal.setTime(inicioMesAtual);
	cal.add(Calendar.DAY_OF_YEAR, -1);
	Date fimMesAnterior = cal.getTime();
	String strFimMesAnterior = formataData.format(fimMesAnterior);
	
	sSessoes = String.valueOf(SessoesAtivas.getSessoesAtivas());

	Versao v = Versao.getInstance();
	sVersao = v.getVersaoStr();
	sNumeroBuild = v.getNumeroBuild();
	Date dataBuild = v.getDataBuild();
	String nomeContrato = "["+ ConfiguracaoProvider.getInstance().getNomeContrato()+ "]";

	Configuracao conf = ConfiguracaoProvider.getInstance();
	String sGrupoSupervisor = conf.getConfiguracaoChaveValor().get("grupo_supervisor");

	Usuario usu = (Usuario) request.getSession().getAttribute(SessaoConstantes.SESSAO_USUARIO);

	Boolean ehSupervisor = Usuario.usuarioPertenceAoGrupo(usu.getId(),Integer.valueOf(sGrupoSupervisor)) ||
			usu.getId().equals(135) || usu.getId().equals(28) || usu.getId().equals(125);;

	IndicadoresProcessamento indicadoresProcessamento = null;
	if (ehSupervisor)
		indicadoresProcessamento = IndicadoresProcessamento.buscaIndicadores();

	// Indicadores de importação
	IndicadoresImportacao indicadoresImportacao = new IndicadoresImportacao();
	if (ehSupervisor) {
		intDiasAtrasoMesAtual = indicadoresImportacao.diasComImportacaoAtrasada(intMesAtual, intAnoAtual);
		//intDiasAtrasoMesAnterior = indicadoresImportacao.diasComImportacaoAtrasada(intMesAnterior, intAnoAnterior);

// 		if (intDiasAtrasoMesAtual > 0 || intDiasAtrasoMesAnterior > 0) {
// 			possuiIndicadorImportacao = true;
// 		}
// 		if (intDiasAtrasoMesAtual > 0) {
// 			possuiIndicadorImportacao = true;
// 		}
	}

	List<GrupoBean> grupos = Grupo.buscaGruposPorIdUsuario(usu.getId());
	Integer linhas_grupos = grupos.size() + 1;
%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="com.consilux.model.LogonLogoff"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.infra.SessoesAtivas"%>
<%@page import="com.consilux.conf.Versao"%>
<%@page import="java.util.Date"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%><br />
<c:set var="indicadoresProcessamento" value="<%=indicadoresProcessamento%>" scope="request" />
<c:set var="intDiasAtrasoMesAtual" value="<%=intDiasAtrasoMesAtual%>" scope="request" />
<%-- <c:set var="intDiasAtrasoMesAnterior" value="<%=intDiasAtrasoMesAnterior%>" scope="request" /> --%>
<c:set var="possuiIndicadorImportacao" value="<%=possuiIndicadorImportacao%>" scope="request" />
<c:set var="strInicioMesAtual" value="<%=strDataAtual%>" scope="request" />
<c:set var="strFimMesAtual" value="<%=strFimMesAtual%>" scope="request" />
<c:set var="strInicioMesAnterior" value="<%=strDataAnterior%>" scope="request" />
<c:set var="strFimMesAnterior" value="<%=strFimMesAnterior%>" scope="request" />
<c:set var="grupos" value="<%=grupos%>" scope="request" />
<c:set var="linhas_grupos" value="<%=linhas_grupos%>" scope="request" />

<script type="text/javascript" src="../js/jquery.js"></script>
<script type="text/javascript" src="../js/jquery-ui.min.js"></script>
<script type="text/javascript" src="../js/funcoes.js"></script>
<script type="text/javascript" src="../js/multiplas_imagens.js"></script>
<script type="text/javascript" src="../js/obliteracao.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI.js"></script>
<script type="text/javascript" src="../GtwClientLogger/GtwClientLogger.nocache.js"></script>
<script type="text/javascript" src="../js/pixastic.min.js"></script>
<script type="text/javascript" src="../js/ajustabrilho.js"></script>
<script type="text/javascript" src="../js/brilhoimagem.js"></script>
<script type="text/javascript" src="../js/CsxRemoteObject.js"></script>
<script type="text/javascript" src="../js/imagem_perfil.js"></script>

<script type="text/javascript">

	function geraRelatorioMesAtual() {
		
		var url = "/relatorio/RelatorioFuncionamento?dataini=" + "${strInicioMesAtual}" + "&datafim=" + "${strFimMesAtual}" + "&relFixoHidden=true";
		
		$.get('/relatorio/RelatorioFuncionamento', {
		  dataini: "${strInicioMesAtual}",
		  datafim: "${strFimMesAtual}",
		  relFixoHidden: true,
		  async: false,
		  success: function(response){
				  window.open(url);
		  }
		});
	}
	
	function geraRelatorioMesAnterior() {
		
		var url = "/relatorio/RelatorioFuncionamento?dataini=" + "${strInicioMesAnterior}" + "&datafim=" + "${strFimMesAnterior}" + "&relFixoHidden=true";
		
		$.get('/relatorio/RelatorioFuncionamento', {
		  dataini: "${strInicioMesAnterior}",
		  datafim: "${strFimMesAnterior}",
		  relFixoHidden: true,
		  async: false,
		  success: function(response){
 			  window.open(url);
		  }
		});
	}
	
</script>


<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_lista" width="500">
				<tr>
					<th class="head_tabela" width="100%" colspan="4">Informação do
						Sistema</th>
				</tr>
				<tr>
					<td class="visualiza_campo" colspan="4" style="text-align: center;">GTW
						versão: <%=sVersao%>-r<%=sNumeroBuild%></td>
				</tr>
				<tr>
					<td class="visualiza_campo" colspan="4" style="text-align: center;">Usuário
						atual: <%=usu.getNome()%>, Sessões ativas: <%=sSessoes%></td>
				</tr>

				<tr>
					<td align="center">

						<table>

							<tr>
								<td class="visualiza_campo" rowspan="${linhas_grupos}">Grupos:&nbsp;&nbsp;&nbsp;</td>
							</tr>

							<c:forEach var="linha" items="${grupos}">
								<tr>
									<td class="visualiza_campo">${linha.descricao}</td>
								</tr>
							</c:forEach>

						</table>

					</td>
				</tr>

				<c:if test="${indicadoresProcessamento != null}">
					<tr>
						<th class="head_tabela" colspan="4">Indicadores de
							Processamento</th>
					</tr>

					<tr>
						<td class="label_campo" colspan="3">Atraso atual no
							Processamento:</td>
						<td class="visualiza_campo" style="text-align: center;"><c:choose>
								<c:when test="${indicadoresProcessamento.emAtraso}">
									<font color="red">
										${indicadoresProcessamento.diasAtrasoAtual} dias </font>
								</c:when>
								<c:when test="${indicadoresProcessamento.emAlerta}">
									<font color="orange">
										${indicadoresProcessamento.diasAtrasoAtual} dias </font>
								</c:when>
								<c:otherwise>
			                        	${indicadoresProcessamento.diasAtrasoAtual} dias
								</c:otherwise>
							</c:choose></td>
					</tr>
					
					<tr>
						<td class="label_campo" colspan="3">Atraso atual de Lotes Reprovados:</td>
						<td class="visualiza_campo" style="text-align: center;">
			                        	${indicadoresProcessamento.diasAtrasoReprovado} dias
						</td>
					</tr>

					<tr>
						<td class="label_campo" colspan="3">Atraso atual na Geração
							de Remessas:</td>
						<td class="visualiza_campo" style="text-align: center;"><c:choose>
								<c:when test="${indicadoresProcessamento.remessaEmAtraso}">
									<font color="red">
										${indicadoresProcessamento.diasAtrasoRemessa} dias </font>
								</c:when>
								<c:when test="${indicadoresProcessamento.remessaEmAlerta}">
									<font color="orange">
										${indicadoresProcessamento.diasAtrasoRemessa} dias </font>
								</c:when>
								<c:otherwise>
			                        	${indicadoresProcessamento.diasAtrasoRemessa} dias
								</c:otherwise>
							</c:choose></td>
					</tr>

					<tr>
						<td class="label_campo" colspan="3">Atraso atual na
							Validação:</td>
						<td class="visualiza_campo" style="text-align: center;"><c:choose>
								<c:when test="${indicadoresProcessamento.validacaoEmAtraso}">
									<font color="red">
										${indicadoresProcessamento.diasAtrasoValidacao} dias </font>
								</c:when>
								<c:when test="${indicadoresProcessamento.validacaoEmAlerta}">
									<font color="orange">
										${indicadoresProcessamento.diasAtrasoValidacao} dias </font>
								</c:when>
								<c:otherwise>
			                        	${indicadoresProcessamento.diasAtrasoValidacao} dias
								</c:otherwise>
							</c:choose></td>
					</tr>

					<tr>
						<td class="label_campo" colspan="3">Imagem/Dia com prazo
							excedido em <%=mesAtual%>:
						</td>
						<td class="visualiza_campo" style="text-align: center;">
							${indicadoresProcessamento.totalEstouroPrazoProcessamento} imgs.
						</td>
					</tr>
					<tr>
						<td class="label_campo" colspan="3">Imagem/Dia com prazo
							excedido em <%=mesAnterior%>:
						</td>
						<td class="visualiza_campo" style="text-align: center;">
							${indicadoresProcessamento.totalEstouroPrazoProcessamentoAnterior}
							imgs.</td>
					</tr>
					<tr>
						<td class="label_campo" colspan="3">Erros de processamento em
							<%=mesAtual%>:
						</td>
						<td class="visualiza_campo" style="text-align: center;">
							${indicadoresProcessamento.errosProcessamentoMesAtual} imgs.
						</td>
					</tr>
					<tr>
						<td class="label_campo" colspan="3">Erros de processamento em
							<%=mesAnterior%>:
						</td>
						<td class="visualiza_campo" style="text-align: center;">
							${indicadoresProcessamento.errosProcessamentoMesAnterior} imgs.
						</td>
					</tr>
					
					<tr>
						<th class="head_tabela" colspan="4">Cadastro de Isentos</th>
					</tr>
					
					<tr>
						<td class="label_campo" colspan="3">Atraso atual na importação de isentos:</td>
						<td class="visualiza_campo" style="text-align: center;">
							${indicadoresProcessamento.diasAtrasoIsento} dias.</td>
					</tr>
					
					<tr>
						<td class="label_campo" colspan="3">Situação da importação de isentos no CAV:</td>
						<td class="visualiza_campo" style="text-align: center;">
							${indicadoresProcessamento.arquivosVerificadosDesc}</td>
					</tr>
					<tr>
						<td class="label_campo" colspan="3">Última atualização do CAV:</td>
						<td class="visualiza_campo" style="text-align: center;"><fmt:formatDate value="${indicadoresProcessamento.dataArquivosCAV}" type="BOTH" pattern="dd/MM/yyyy HH:mm:ss" dateStyle="SHORT" timeStyle="SHORT" /></td>
					</tr>
					
					<tr>
						<th class="head_tabela" colspan="4">Movimentos de Lote</th>
					</tr>
					
					<tr>
						<td class="label_campo" colspan="3">Erros de Exportação de Movimentos de Lote:</td>
						<td class="visualiza_campo" style="text-align: center;">
							${indicadoresProcessamento.errosRemessaAutomatico}
						</td>
					</tr>
					
				</c:if>

				<!-- Indicadores de importação -->
				<c:if test="${possuiIndicadorImportacao != false}">
					<tr>
						<td>&nbsp;</td>
					</tr>
					<tr>
						<th class="head_tabela" colspan="4">Indicadores de Importação</th>
					</tr>

					<tr>
						<td class="label_campo" colspan="3">Dias sem infrações importadas em <%=mesAtual%> (por local/faixa):</td>
						<td class="visualiza_campo" style="text-align: center;">
							<c:choose>
								<c:when test="${intDiasAtrasoMesAtual > 0}">
									<a class='link_td' href="javascript:geraRelatorioMesAtual()" title="Gerar Relatório">
										<font color="red">${intDiasAtrasoMesAtual} dias.</font>
									</a>
								</c:when>
								<c:otherwise>
      								<a class='link_td' href="javascript:geraRelatorioMesAtual()" title="Gerar Relatório">
										<font color="black">${intDiasAtrasoMesAtual} dias.</font>
									</a>
								</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</c:if>

			</table>
		</td>
	</tr>
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp"%>
