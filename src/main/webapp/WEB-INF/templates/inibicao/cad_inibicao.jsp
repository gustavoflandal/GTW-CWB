<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">
	function ajustaEquipamento(porCodigo) {
		var txt_equipamento = document.getElementById("txt_equipamento");
		var sel_equipamento = document.getElementById("sel_equipamento");
		if (porCodigo) {
			sel_equipamento.selectedIndex = 0;
	
			for (var i=0;i<sel_equipamento.options.length;i++) {
				if (sel_equipamento.options[i].value == txt_equipamento.value)
					sel_equipamento.selectedIndex = i;
			}
		}
		else
			txt_equipamento.value = sel_equipamento.value;
	}
	function preenchePeriodo(e, txt_data_ini, txt_hora_ini, txt_data_fim, txt_hora_fim) {
        if (txt_data_ini.value != "") {
            if (txt_hora_ini.value == "")
            	txt_hora_ini.value = "00:00";
            if (txt_data_fim.value == "")
                txt_data_fim.value = txt_data_ini.value;
            if (txt_hora_fim.value == "")
            	txt_hora_fim.value = "23:59";
        }
        else {
            txt_hora_ini.value = "";
            txt_data_fim.value = "";
            txt_hora_fim.value = "";
        }
    }

    function preenchePeriodoInfracao(e) {
        var txt_data_ini = document.getElementById('txt_data_infracao_ini');
        var txt_hora_ini = document.getElementById('txt_hora_infracao_ini');
        var txt_data_fim = document.getElementById('txt_data_infracao_fim');
        var txt_hora_fim = document.getElementById('txt_hora_infracao_fim');
        
        preenchePeriodo(e, txt_data_ini, txt_hora_ini, txt_data_fim, txt_hora_fim);
    }
	function adicionarEnquadramento() {
		var selDisponiveis = document.getElementById("sel_enquadramentos_disponiveis");
		var selSelecionados = document.getElementById("sel_enquadramentos_selecionados");
		
		for (i=selDisponiveis.length-1;i>=0;i--) {
			if (selDisponiveis.options[i].selected) {
				texto = selDisponiveis.options[i].text
				valor = selDisponiveis.options[i].value
				opt = new Option(texto, valor);
				opt.selected = true;
				ultimoInd = selSelecionados.length;
				selSelecionados.options[ultimoInd] = opt;
				selDisponiveis.options[i] = null;
			}
		}
	}
	function removerEnquadramento() {
		var selDisponiveis = document.getElementById("sel_enquadramentos_disponiveis");
		var selSelecionados = document.getElementById("sel_enquadramentos_selecionados");
		
		for (i=selSelecionados.length-1;i>=0;i--) {
			if (selSelecionados.options[i].selected) {
				texto = selSelecionados.options[i].text
				valor = selSelecionados.options[i].value
				opt = new Option(texto, valor);
				ultimoInd = selDisponiveis.length;
				selDisponiveis.options[ultimoInd] = opt;
				selSelecionados.options[i] = null;
			}
		}
	}
</script>
<br>
<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
			<form name="frm_cadastrar_inibicao" action="/ferramenta/CadastrarInibicao" method="post" accept-charset="ISO-8859-1">
	
			<table class="tabela_lista" width="800">
				<tr>
					<th class="head_tabela" width="100%" colspan="5">Inibição de Registros</t>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 1ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" colspan="4" width="70%">Nome para a Inibição</td>
					<td class="label_campo" width="30%">&nbsp;Classe</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 2ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
	
	
	
				<tr>
					<td colspan="4" class="valor_campo">
						<input id="txt_nome_inibicao" name="nome_inibicao" type="text" class="campo_texto" maxlength="50"/></td>
	
					<td class="valor_campo">
						<select id="inibicao_tipo_veiculo" name="classe_veiculo">
							<option value="">--Todas--</option>
							<c:forEach var="classe" items="${classeVeiculo}">
								<option value="${classe.idClasse}">${classe.descricao}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 3ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" colspan="3" width="55%">&nbsp;Local</td>
					<td class="label_campo" width="15%">&nbsp;Pista</td>
					<td class="label_campo">&nbsp;</td>
				</tr>
	
	
				<%-- ***************************************************************** --%>
				<%-- 4ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
				<tr>
	
	
	
					<td class="valor_campo" colspan="3">
					<table class="tabela_branca" width="100%">
						<tr>
                        <td class="valor_campo" width="10%">
                        	<input id="txt_equipamento" type="text" class="campo_texto" maxlength="7" onblur="ajustaEquipamento(true)"/>
                       	</td>
                        <td class="valor_campo" width="55%">
                            <select id="sel_equipamento" name="serie_equipamento" onchange="ajustaEquipamento(false)">
                                <option value="0" selected="selected">--equipamento--</option>
                                <c:forEach var="equip" items="${equipamentos}">
                                    <option value="${equip.serieEquipamento}">${equip.nome}</option>
                                </c:forEach>
                            </select>
                        </td>
						</tr>
					</table>
					</td>
					<td class="valor_campo">
						<select id="sel_pista" name="pista">
							<option value="0">--Todas--</option>
							<option value="1">1</option>
							<option value="2">2</option>
							<option value="3">3</option>
							<option value="4">4</option>
						</select>
					</td>
					<td>&nbsp;</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 5ª Linha (labels) --%>
				<%-- ***************************************************************** --%>
				<tr>
					<td class="label_campo" colspan="3">&nbsp;Data das Imagens:</td>
					<td class="label_campo" colspan="2">&nbsp;Período do Dia:</td>
				</tr>
	
				<%-- ***************************************************************** --%>
				<%-- 4ª Linha (campos) --%>
				<%-- ***************************************************************** --%>
	
	
	
	
				<tr>
					<td class="valor_campo" colspan="3">&nbsp;A partir: 
						<input id="txt_data_infracao_ini" type="text" name="data_ini" maxlength="10" style="width: 80px" onblur="preenchePeriodoInfracao();">
						<img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_ini'), 'dd/mm/yyyy')">
					Até
						<input id="txt_data_infracao_fim" type="text" name="data_fim" maxlength="10" style="width: 80px">
						<img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_infracao_fim'), 'dd/mm/yyyy')">
					</td>
					<td class="valor_campo" colspan="2">&nbsp;
						<input id="txt_hora_infracao_ini" type="text" name="hora_ini" class="campo_texto" maxlength="5" style="width: 40px">
						<img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_infracao_ini'), 'hh:mm')">
					Até
						<input id="txt_hora_infracao_fim" type="text" name="hora_fim" class="campo_texto" maxlength="5" style="width: 40px">
						<img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_infracao_fim'), 'hh:mm')">
					</td> 
				</tr>
				
				<tr>
					<td colspan="5">&nbsp;</td>
				</tr>
				
				<tr>
					<td class="valor_campo" colspan="2" width="50%">&nbsp;Enquadramentos Disponíveis:<br>
						<select	id="sel_enquadramentos_disponiveis" multiple="multiple" style="height: 100px;">
							<c:forEach var="enquadramento" items="${enquadramentos}">
								<c:if
									test="${enquadramento.tipoInfoEspecifica != 'V' && enquadramento.tipoInfoEspecifica != 'T'}">
									<option value="${enquadramento.idEnquadramento}">${enquadramento.idEnquadramento}
									- ${enquadramento.descricao}</option>
								</c:if>
							</c:forEach>
						</select>
					</td>
					<td class="valor_campo">
						<button onclick="adicionarEnquadramento(); return false;">&nbsp;&gt;&nbsp;</button><br><br>
						<button onclick="removerEnquadramento(); return false;">&nbsp;&lt;&nbsp;</button>
					</td>
					<td class="valor_campo" colspan="2">Enquadramentos Selecionados:<br>
						<select	id="sel_enquadramentos_selecionados" name="enquadramentos_selecionados" multiple="multiple" style="height: 100px;">
						</select>
					</td>
				</tr>
				
	
				<tr>
					<td colspan="5">&nbsp;</td>
				</tr>
	
				<tr>
					<td colspan="5" align="right">
					<table>
						<tr>
							<td class="valor_campo" style="text-align: right;">
								<button onclick="frm_cadastrar_inibicao.submit()">Próximo &gt;&gt;</button>
							</td>
						</tr>
					</table>
					</td>
				</tr>
			</table>
			</form>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>
