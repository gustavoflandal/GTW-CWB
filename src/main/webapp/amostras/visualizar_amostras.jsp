<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn" %>
<%!
	private Logger logger = Logger.getLogger(AmostraImagem.class); 
%>
<%
	String sDataIni = request.getParameter("dataini");
	String sDataFim = request.getParameter("datafim");
	
	if (sDataIni == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataIni)) {
	    new Mensagem(response).showErro("Data inicial enviada inválida!");
	    return;
	}
	else if (sDataFim == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataFim)) {
	    new Mensagem(response).showErro("Data final enviada inválida!");
	    return;
	}
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Date dtIni = sdf.parse(sDataIni);
	Date dtFim = sdf.parse(sDataFim);
	
    Calendar cVal = Calendar.getInstance();
    cVal.setTime(dtIni);
    
    List<Date> datas = new ArrayList<Date>();
    while (!cVal.getTime().after(dtFim)) {
    	cVal.set(Calendar.HOUR, 0);		 // Força zero hora(por causa do horário de verão).
    	datas.add(cVal.getTime());  	 // Coloca na lista
    	cVal.add(Calendar.DAY_OF_MONTH, 1); // Incrementa um dia (para a próxima passada do laço)
    }

    TabelaAmostraImagem tblAmostra;

    Calendar diaInicio = Calendar.getInstance();
	diaInicio.setTime(dtIni);

	Calendar diaFim = Calendar.getInstance();
	diaFim.setTime(dtFim);
    
    try {
    	Date ultimoDiaConfiavel = AmostraImagem.buscaUltimoDiaConfiavel(dtIni); 
    	
    	if (ultimoDiaConfiavel == null || dtFim.after(ultimoDiaConfiavel)) {
    		if (ultimoDiaConfiavel != null)
    	    	new Mensagem(response).showErro("Período não confiável, último dia confiável: "+sdf.format(ultimoDiaConfiavel));
    		else
    	    	new Mensagem(response).showErro("Período não confiável, não existem amostras confiáveis no período selecionado.");
    			
    	    logger.warn("Período de amostra não confiável");
    	    return;
    	}
    	
    	tblAmostra = new TabelaAmostraImagem(diaInicio, diaFim);
    	//Colocando na sessão para agilizar ao abrir o modo avançado..
    	session.setAttribute("[TabelaAmostraImagem]",tblAmostra);
	} 
    catch (Exception ex) {
		String msg = "Erro ao buscar tabela de amostras.";
		logger.error(msg, ex);
	    new Mensagem(response).showErro(msg);
	    return;
	}
	
	Long longIni = new Long(dtIni.getTime());
	Long longFim = new Long(dtFim.getTime());
%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.TreeSet"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.TreeMap"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>

<%@page import="com.consilux.infra.RelatorioItr"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Date"%>
<%@page import="com.consilux.model.AmostraImagem"%>
<%@page import="com.consilux.lib.DateUtil"%>
<%@page import="com.consilux.model.beans.AmostraVeiculoBean"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<%@page import="com.consilux.model.AmostraVeiculoIterator"%>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.LinkedList"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.consilux.ui.tabela.TabelaAmostraImagem"%>
<%@page import="com.consilux.ui.tabela.PistaAmostraImagem"%>
<%@page import="java.util.Map.Entry"%><br />
<c:set var="ponto_ruim" value="<%=AmostraImagem.PONTO_RUIM%>" />
<c:set var="ponto_regular" value="<%=AmostraImagem.PONTO_REGULAR%>" />
<c:set var="tblAmostra" value="<%=tblAmostra%>" />
<c:set var="tblAmostra" value="<%=tblAmostra%>" />
<c:set var="datas" value="<%=datas%>" />
<c:set var="dataini" value="<%=URLEncoder.encode(sDataIni, \"UTF-8\")%>" />
<c:set var="datafim" value="<%=URLEncoder.encode(sDataFim, \"UTF-8\")%>" />
<c:set var="ldataIni" value="<%=longIni%>" />
<c:set var="ldataFim" value="<%=longFim%>" />
<script type="text/javascript">
	var amostras_reg = new Array();
	function mostraImagemAmostra(cursorAmostra, dia, idLocal, idPista, metrologica) {
		var w = 650;
		var h = 804;
		var l = (screen.width/2)-(w/2);
		var t = (screen.height/2)-(h/2);
		var txt_corte = document.getElementById('txt_corte');
		
		var popUpWindow = window.open("/amostras/exibe_imagem.jsp?data_ini=" + ${ldataIni}
			+ "&data_fim=" + ${ldataFim}
			+ "&dia=" + dia
			+ "&cursor_amostra=" + cursorAmostra
			+ "&id_local=" + idLocal
			+ "&id_pista=" + idPista
			+ "&metrologica=" + metrologica
			+ "&pontos_ate=" + txt_corte.value,
			"Amostras do período", "width="+w+",height="+h+",top="+t+",left="+l);
		popUpWindow.opener = $wnd;
	}	
	function registraAmostra(td, ponto) {
		if (ponto != null) {
			td.ponto = ponto;
			td.cor_orig = td.style.color;
			amostras_reg.push(td);
		}
	}
	
	function aplicaLinhaCorte() {
		var txt_corte = document.getElementById('txt_corte');
		var valor = txt_corte.value;
		
		for (i=0; i < amostras_reg.length; i++) {
			td = amostras_reg[i];
			if (td.ponto < valor || valor == "")
				td.style.color = td.cor_orig;
			else
				td.style.color = "";
		}
	}

	function ajustaLinhaCorte(ponto_corte) {
		var txt_corte = document.getElementById('txt_corte');
		if (ponto_corte != null)
			txt_corte.value = ponto_corte;
		else
			txt_corte.value = "";
		
		aplicaLinhaCorte();
	}
	
	function fixarAmostra() {
		window.location = '/medicao/FixarAmostra?dataini=${dataini}&datafim=${datafim}';
	}
</script>
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_branca">
				<tr>
					<td class="label_campo">Linha de corte (ver pontos até) </td>
					<td class="valor_campo">
						<input type="text" id="txt_corte" name="corte" class="campo_texto" style="width: 30px"/>
						<button name="btn_aplicar" onclick="aplicaLinhaCorte()">Aplicar</button>
					</td>
					<td class="valor_campo" colspan="2">&nbsp;</td>
				</tr>
				<tr>
					<td width="33%" class="dado_lista_tabela_claro"><input type="radio" name="filtroAmostra" value="0" onchange="ajustaLinhaCorte(null);" />Ver todas imagens</td>
					<td width="34%" class="dado_lista_tabela_claro"><input type="radio" name="filtroAmostra" value="1" onchange="ajustaLinhaCorte(${ponto_ruim});" />Ver imagens ruins</td>
					<td width="33%" class="dado_lista_tabela_claro"><input type="radio" name="filtroAmostra" value="2" onchange="ajustaLinhaCorte(${ponto_regular});" />Ver imagens ruins e regulares.</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td align="center">
			<table class="tabela_grid">
				<tr>
					<th class="head_tabela" align="center" colspan="<%=datas.size()+6%>">
						&nbsp;Relatório&nbsp;de&nbsp;amostras&nbsp;
					</th>
				</tr>
				<tr>
					<td class="dado_lista_tabela_grid" align="center">
						&nbsp;<b>Série</b>&nbsp;
					</td>
					<td class="dado_lista_tabela_grid" align="center">
						&nbsp;<b>Local</b>&nbsp;
					</td>
                    <td class="dado_lista_tabela_grid" align="center">
                        &nbsp;<b>Pista</b>&nbsp;
                    </td>                    
                    <td class="dado_lista_tabela_grid" align="center">
                        &nbsp;<b>Faixa</b>&nbsp;
                    </td>
                    <td class="dado_lista_tabela_grid" align="center">
                        &nbsp;<b>Cod.Pista</b>&nbsp;
                    </td>
                    <td class="dado_lista_tabela_grid" align="center">
                        &nbsp;<b>Metrológica?</b>&nbsp;
                    </td>
					<c:forEach var="data" items="${datas}">
						<td class="dado_lista_tabela_grid" align="center">
							<b><fmt:formatDate value="${data}" pattern="dd/MM" /></b>
	                    </td>
	                </c:forEach>
				</tr>
                <c:set var="cursor_amostra" value="0" />
                <c:set var="metrologica" value="false" />
				<c:forEach var="pista" varStatus="linhaInfo" items="${tblAmostra}">
					<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />				
					<tr>
						<td class="${css_td}" align="center" width="50">
							&nbsp;${pista.key.serieEquipamento}&nbsp;
						</td>
						<td class="${css_td}" align="left" width="300">
							&nbsp;${fn:replace(pista.key.nomePista, " ", "&nbsp;")}&nbsp;
						</td>
						<td class="${css_td}" align="center" width="40">
							&nbsp;${pista.key.codPistaAlternativo}&nbsp;
						</td>
						<td class="${css_td}" align="center" width="40">
							&nbsp;${pista.key.codPistaProdam}&nbsp;
						</td>
						<td class="${css_td}" align="center" width="40">
							&nbsp;${pista.key.codPista}&nbsp;
						</td>
						<td class="${css_td}" align="center" width="100">
                        	<c:choose>
								<c:when test="${pista.key.metrologica}">
									&nbsp;SIM&nbsp;
								</c:when>
								<c:otherwise>
									&nbsp;NÃO&nbsp;
								</c:otherwise>
							</c:choose>
						</td>
						<c:forEach var="amostraDia" items="${pista.value}">
							<c:set var="cor_amostra" value="${amostraDia.fixada ? amostraDia.corFixada : amostraDia.proporcaoRGB}" />
							<c:choose>
								<c:when test="${amostraDia.metrologica}">
									<c:set var="metrologica" value="1" />
								</c:when>
								<c:otherwise>
									<c:set var="metrologica" value="0" />
								</c:otherwise>
							</c:choose>						
	                        <td id="td_amostra_${cursor_amostra}" class="${css_td}" align="center" style="${amostraDia.fixada ? 'font-weight: bold' : 'font-weight: normal'}; color: rgb(${cor_amostra.red},${cor_amostra.green},${cor_amostra.blue});">
		                        <c:choose>
			                        <c:when test="${amostraDia.pistaAtiva}">
										<c:choose>
				                        	<c:when test="${amostraDia.idVeiculo != null}">
					                        	<c:choose>
													<c:when test="${amostraDia.score != null}">
														${amostraDia.score}
													</c:when>
													<c:when test="${amostraDia.score == null}">
														&nbsp;
													</c:when>
												</c:choose>
												<br>

												<%-- Definicao especial, se for uma amostra aplicavel --%>
												<c:choose>
													<c:when test="${amostraDia.aplicavel}">
														<a id="link_amostra_${cursor_amostra}" class='link_td' href="javascript:mostraImagemAmostra(${cursor_amostra}, ${amostraDia.data.time.time}, ${amostraDia.idLocal}, ${amostraDia.idPista}, ${metrologica})">${amostraDia.tipo}</a>														
													</c:when>
													<c:otherwise>
														<a id="link_amostra_${cursor_amostra}" class='link_td' href="javascript:mostraImagemAmostra(${cursor_amostra}, ${amostraDia.data.time.time}, ${amostraDia.idLocal}, ${amostraDia.idPista}, ${metrologica})">!!</a>
													</c:otherwise>
												</c:choose>
												
											</c:when>
											<c:otherwise>
												<%-- Este é o caso quando o sistema não fez a seleção das amostras e
												também não existe amostra manual --%>
												<font color="red">???</font><br>
												<a id="link_amostra_${cursor_amostra}" class='link_td' href="javascript:mostraImagemAmostra(${cursor_amostra}, ${amostraDia.data.time.time}, ${amostraDia.idLocal}, ${amostraDia.idPista}, ${metrologica})">N/D</a>												
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<%-- Este é o caso quando a pista ainda não estava ativa no dia --%>									
										&nbsp;<br><font color="blue">N/A</font>
									</c:otherwise>
								</c:choose>
								<script type="text/javascript">registraAmostra(document.getElementById('td_amostra_${cursor_amostra}'), ${amostraDia.score == null ? 'null' : amostraDia.score})</script>
								<c:set var="cursor_amostra" value="${cursor_amostra+1}" />
	                        </td>
                        </c:forEach>
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
	<tr>
		<td align="center">
			<table class="tabela_branca">
				<tr>
					<td class="valor_campo" colspan="6">
						<br />
						<button name="btn_fixar_amostra" onclick="fixarAmostra()">Fixar Imagens para o Processo de Medição</button>
					</td>
			</table>
		</td>
	</tr>
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
