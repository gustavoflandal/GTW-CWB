<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/fn.tld" prefix="fn" %>
<%
	
	String sLocal = request.getParameter("local");
	String sPista = request.getParameter("pista");
	String sDataIni = request.getParameter("dataini");
	String sHoraIni = request.getParameter("horaini");
	String sDataFim = request.getParameter("datafim");
	String sHoraFim = request.getParameter("horafim");
	
	if (sLocal == null || !Pattern.matches("[0-9]{0,8}",sLocal)) {
	    new Mensagem(response).showErro("Local enviado inválido!");
	    return;
	}
	else if (sPista == null || !Pattern.matches("[0-9]",sPista)) {
	    new Mensagem(response).showErro("Pista enviada inválida!");
	    return; 
	}
	else if (sDataIni == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataIni)) {
	    new Mensagem(response).showErro("Data inicial enviada inválida!");
	    return;
	}
	else if (sHoraIni == null || !Pattern.matches("([01][0-9]|2[0-3]):([0-5][0-9])",sHoraIni)) {
	    new Mensagem(response).showErro("Hora inicial enviada inválida!");
	    return;
	}
	else if (sDataFim == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataFim)) {
	    new Mensagem(response).showErro("Data final enviada inválida!");
	    return;
	}
	else if (sHoraFim == null || !Pattern.matches("([0-1][0-9]|2[0-3]):([0-5][0-9])",sHoraFim)) {
	    new Mensagem(response).showErro("Hora final enviada inválida!");
	    return;
	}

	Date dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataIni+" "+sHoraIni+":00");
	Date dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataFim+" "+sHoraFim+":59");
	
    Map<String,Object> mFiltro = new HashMap<String,Object>();
	if (Integer.valueOf(sLocal) > 0) {
	    mFiltro.put("id_local", Integer.valueOf(sLocal));
	}
    
	if (Integer.valueOf(sPista) > 0) {
	    mFiltro.put("id_pista", Integer.valueOf(sPista));
	}
	
	List<Date> datas = new ArrayList<Date>();
    
    Calendar cVal = new GregorianCalendar();
	Date dtIniDatas = new SimpleDateFormat("ddMMyyyyhhmmss").parse(new SimpleDateFormat("ddMMyyyy").format(dtIni)+"000000"); 
    cVal.setTime(dtIniDatas);
    while (cVal.getTime().before(dtFim)) {
    	datas.add(cVal.getTime());
    	cVal.add(Calendar.DATE, 1);
    	cVal.set(Calendar.HOUR, 0); //Força zero hora, por causa do horário de verão.
    }

    //Faz primeiro a busca de pistas, porque não é necessário passar a data.
    List<Pista> pistas = Pista.buscarPistaPor(mFiltro);

    mFiltro.put("data_ini", new Timestamp(dtIni.getTime()));
    mFiltro.put("data_fim", new Timestamp(dtFim.getTime()));

    // pega usuário logado
    Usuario usuario = (Usuario)session.getAttribute("[usuario]");
    
	int id_usuario = usuario.getId();
	
    //Utilizando o mesmo filtro faz a busca de imagens teste.
    List<ImagemTeste> imagens = ImagemTeste.buscarImagemTestePor(mFiltro , id_usuario , null );
    
    Map<Integer, Map<Integer, Map<Date, Map<Short, ImagemTeste>>>> mapLocais = new TreeMap<Integer, Map<Integer, Map<Date, Map<Short, ImagemTeste>>>>();
    
    for (ImagemTeste i: imagens) {
    	Map<Integer, Map<Date, Map<Short, ImagemTeste>>> local; 
    	Map<Date, Map<Short, ImagemTeste>> dia; 
    	Map<Short, ImagemTeste> imagemTeste; 

    	if (mapLocais.containsKey(i.getIdLocal()))
    		local = mapLocais.get(i.getIdLocal());
    	else {
    		local = new HashMap<Integer, Map<Date, Map<Short, ImagemTeste>>>();
    		mapLocais.put(i.getIdLocal(), local);
    	}

    	if (local.containsKey(i.getPista()))
    		dia = local.get(i.getPista());
    	else {
    		dia = new HashMap<Date, Map<Short, ImagemTeste>>();
    		local.put(i.getPista(), dia);
    	}
    	
		//Pegando somente o dia, sem a hora:
    	Date dtDia = new SimpleDateFormat("ddMMyyyyhhmmss").parse(new SimpleDateFormat("ddMMyyyy").format(i.getData())+"000000"); 
    	if (dia.containsKey(dtDia))
    		imagemTeste = dia.get(dtDia);
    	else {
    		imagemTeste = new TreeMap<Short, ImagemTeste>();
    		dia.put(dtDia, imagemTeste);
    	}

    	imagemTeste.put((short)(imagemTeste.size()+1), i);
    }
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
<%@page import="com.consilux.model.ImagemTeste"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.Pista"%>
<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<br />
<c:set var="mapLocais" value="<%=mapLocais%>" />
<c:set var="pistas" value="<%=pistas%>" />
<c:set var="datas" value="<%=datas%>" />
<script type="text/javascript">
	function encadeiaVeiculoImagem(idVeiculoAtual, idVeiculoAnterior) {
		var link_imagem_anterior = document.getElementById("link_imagem_"+idVeiculoAnterior);
	    var link_imagem_atual = document.getElementById("link_imagem_"+idVeiculoAtual);
	
	    if (link_imagem_anterior) { 
	        link_imagem_anterior.idVeiculoProximo = idVeiculoAtual;
	        link_imagem_atual.idVeiculoAnterior = idVeiculoAnterior;
	    }
	    link_imagem_atual.idVeiculo = idVeiculoAtual;
	}
	function mostraImagem(idVeiculo) {

	    var w = window.open("/ferramenta/veiculo_info.jsp?id_veiculo="+idVeiculo+"&encadeado&registrar_visualizacao_veiculo", "Informações do Veículo","width=750, height=700");

  		w.aoFechar = function() {
		    window.location.reload();
	    }
	    
	}
	function getIdVeiculoAnterior(idVeiculo) {
		var ret = 0;
	    var link_imagem = document.getElementById("link_imagem_"+idVeiculo);
	    
	    if (link_imagem) {
	    	ret = link_imagem.idVeiculoAnterior; 
	    } 
	    return ret;
	}
	function getIdVeiculoProximo(idVeiculo) {
	    var ret = 0;
	    var link_imagem = document.getElementById("link_imagem_"+idVeiculo);
	    
	    if (link_imagem) {
	        ret = link_imagem.idVeiculoProximo; 
	    } 
	    return ret;
	}
</script>
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_grid">
				<tr>
					<th class="head_tabela" align="center" colspan="<%=datas.size() + 3 %>">
						&nbsp;Visualização&nbsp;de&nbsp;Imagens Teste&nbsp;
					</th>
				</tr>
				<tr>
					<td class="dado_lista_tabela_grid" align="center">
						&nbsp;<b>Código</b>&nbsp;
					</td>
					<td class="dado_lista_tabela_grid" align="center">
						&nbsp;<b>Local</b>&nbsp;
					</td>
					<td class="dado_lista_tabela_grid" align="center">
						&nbsp;<b>Pista</b>&nbsp;
					</td>
					<c:forEach var="data" items="${datas}">
						<td class="dado_lista_tabela_grid" align="center">
							<b><fmt:formatDate value="${data}" pattern="dd/MM" /></b>
	                    </td>
	                </c:forEach>
				</tr>
                <c:set var="ultimo_local" value="0" />
				<c:forEach var="pista" items="${pistas}">
					<tr>
						<td class="dado_lista_tabela_grid" align="center">
		                   	${pista.idLocal}
						</td>
						<td class="dado_lista_tabela_grid" align="right">
							${pista.nomePista}&nbsp;
						</td>
						<td class="dado_lista_tabela_grid" align="center">
		                   	${pista.pista}
						</td>
						<c:set var="local" value="${mapLocais[pista.idLocal]}" />
						<c:set var="dia" value="${local[pista.pista]}" />
						<c:forEach var="data" items="${datas}">
							<td class="dado_lista_tabela_grid" align="center" width="135px">
								<c:forEach var="imagemTeste" items="${dia[data]}">
									<c:choose>												
										<c:when test="${imagemTeste.value.visualizado}">
											<div> <a id="link_imagem_${imagemTeste.value.idVeiculo}" class='link_td' href="javascript: void(mostraImagem(${imagemTeste.value.idVeiculo}))"> <fmt:formatDate value="${imagemTeste.value.data}" type="time" /> - (<fmt:formatDate value="${imagemTeste.value.dataVisualizado}" pattern="dd/MM" />)</a> </div>  										
										</c:when> 
										<c:otherwise> 
											<div> <a style="color: red;" id="link_imagem_${imagemTeste.value.idVeiculo}" class='link_td' href="javascript: void(mostraImagem(${imagemTeste.value.idVeiculo}))"> <fmt:formatDate value="${imagemTeste.value.data}" type="time" /> </a> </div>  										
										</c:otherwise>
									</c:choose>												
								
		                            <script type="text/javascript">encadeiaVeiculoImagem('${imagemTeste.value.idVeiculo}', '${idVeiculoAnterior}')</script>
			                        <c:set var="idVeiculoAnterior" value="${imagemTeste.value.idVeiculo}" />

								</c:forEach>
							</td>
						</c:forEach>
					</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
