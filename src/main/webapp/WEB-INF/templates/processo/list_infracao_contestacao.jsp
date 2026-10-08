<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<br />
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">
	function preenchePeriodo(e) {
	    var txt_data_infracao_ini = document.getElementById('txt_data_infracao_ini');
	    var txt_data_infracao_fim = document.getElementById('txt_data_infracao_fim');
	
	    if (txt_data_infracao_ini.value != "") {
	        if (txt_data_infracao_fim.value == "")
	            txt_data_infracao_fim.value = txt_data_infracao_ini.value;
	    }
	    else {
	        txt_data_infracao_fim.value = "";
	    }
	}
	function selTudo(cbTudo) {
		var aChecks = document.getElementsByName("sel_infracao");
		for (i=0;i<aChecks.length;i++) {
			aChecks[i].checked = cbTudo.checked;
		}
	}

	function mostraImagem(idInfracao) {
        window.open("/infracao/infracao_imagem.jsp?id_infracao="+idInfracao+"&encadeado", "Imagem","width=750, height=700");
	}
    
    function processa() {
    	document.getElementById('frm_list_infracao').submit();
    }	

    function verifTecla(e) {
		var frm_list = document.getElementById("frm_list");
        if (e.keyCode == 13)
        	frm_list.submit();
    }
</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
				<table class="tabela_branca" width="950">
					<tr>
						<td class="dado_lista_tabela_claro" align="left" width="100%" colspan="5">Mostrando os primeiros ${infracoes_size} de ${total_infracoes} disponíveis.</td>
					</tr>
					<tr>
						<td class="dado_lista_tabela_claro" colspan="5">&nbsp;</td>
					</tr>
					
					<form id="frm_list" action="" method="get">
					
					<tr>
						
						<td class="dado_lista_tabela_claro" align="left" width="80"> 
							Número de registros inicial:
							<select id="sel_num_reg" name="num_reg" style="width: 160px"
								onchange="submit();">
	                            <option value="10" ${num_reg == 10 ? 'selected' : ''}>10</option>
	                            <option value="100" ${num_reg == 100 ? 'selected' : ''}>100</option>
	                            <option value="1000" ${num_reg == 1000 ? 'selected' : ''}>1000</option>
	                            <option value="0" ${num_reg == null ? 'selected' : ''}>TODOS</option>
	                        </select>
						</td>
						
					</tr>
					<tr>
						<td class="dado_lista_tabela_claro" align="left" colspan="2"> 
							Período:
							<table class="tabela_branca" width="90%">
								<tr>
				                    <td class="valor_campo" width="48%">
			                            <input id="txt_data_infracao_ini" type="text" name="data_infracao_ini" maxlength="10" style="width: 80px" onkeypress="verifTecla(event)" onblur="preenchePeriodo();" value="${data_infracao_ini}"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_ini'), 'dd/mm/yyyy')">
			                        </td>
				                    <td class="valor_campo" width="4%" style="text-align: center;">Até</td>
			                        <td class="valor_campo" width="48%">
			                            <input id="txt_data_infracao_fim" type="text" name="data_infracao_fim" maxlength="10" style="width: 80px" onkeypress="verifTecla(event)" value="${data_infracao_fim}"><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_fim'), 'dd/mm/yyyy')">
			                        </td>
								</tr>
							</table>
						</td>
						
					</form>
						<td class="dado_lista_tabela_claro" align="right" style="vertical-align: bottom;">
							<button onclick="document.getElementById('frm_list_infracao').submit();">${btn_processo_caption}</button>
						</td>
					</tr>
				</table>
				<br />
			<form id="frm_list_infracao" action="${list_infracao_action}" method="post">
				<table class="tabela_lista" width="950">
					<tr>
						<th class="head_tabela" width="3%">Nº</th>
						
						<th class="head_tabela" width="10%">Cód. Local</th>
						<th class="head_tabela" width="37%">Local</th>
						<th class="head_tabela" width="15%">Data Infração</th>
						<th class="head_tabela" width="10%">Cód. Enquadramento</th>
						<th class="head_tabela" width="10%">Cód. Infração</th>
						<th class="head_tabela" width="12%">Imagem</th>
					</tr>
					<% int contador = 1; %>
					<c:forEach var="infracao" varStatus="linhaInfo" items="${infracoes}">
						<tr>
							<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
							
							<td class="${css_td}" align="center"><%= contador++ %></td>
							
							<td class="${css_td}" align="center">${infracao.idLocal}</td>
							<td class="${css_td}" align="left">${infracao.local}</td>
							<td class="${css_td}" align="center"><fmt:formatDate value="${infracao.data}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td>
							<td class="${css_td}" align="center">${infracao.idEnquadramento}</td>
							<td class="${css_td}" align="center">${infracao.id}</td>
	                        <td class="${css_td}" align="center">
								<a id="link_imagem_${infracao.id}" class='link_td' href="javascript:mostraImagem(${infracao.id})">[ver&nbsp;imagem]</a>
	                            <script type="text/javascript">encadeiaInfracaoImagem('${infracao.id}', '${idInfracaoAnterior}')</script>
	                        </td>
						</tr>
	                    <c:set var="idInfracaoAnterior" value="${infracao.id}" />
					</c:forEach>
				</table>
				<table class="tabela_branca" width="950">
					
					
					<tr>
						<td class="box_botoes" colspan="3">
							<button onclick="processa()">${btn_processo_caption}</button>
						</td>
					</tr>
				</table>
				<input type="hidden" name="id_processo" value="${id_processo}" />
				<input type="hidden" id="data_infracao_ini" name="data_infracao_ini" value="${data_infracao_ini}" />
				<input type="hidden" id="data_infracao_fim" name="data_infracao_fim" value="${data_infracao_fim}" />
			</form>
		</td>
	</tr>
</table>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
