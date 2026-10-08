<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%
Map<String,Object> mFiltro = new HashMap<String,Object>();
mFiltro.put("grupo",((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,2);
%>
<%@page import="com.consilux.model.relatorio.FluxoVelocidadeGrade"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.consilux.model.LocalVigente"%>
<br />
<br />
<br />
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript">
	function ajustaAno() {
		var sel_ano = document.getElementById("sel_ano");
		var ano_atual = new Date().getFullYear(); 
		sel_ano.options.length = 0;
		sel_ano.options[0] = new Option(ano_atual-1,ano_atual-1);
		sel_ano.options[1] = new Option(ano_atual,ano_atual);
		sel_ano.options[2] = new Option(ano_atual+1,ano_atual+1);
	}
	function ajustaDia() {
		var sel_ano = document.getElementById("sel_ano");
		var sel_mes = document.getElementById("sel_mes");
		var sel_dia = document.getElementById("sel_dia");
		var max_dia = 0;
		switch(parseInt(sel_mes.value)+1) {
			case 4:
			case 6:
			case 9:
			case 11:
				max_dia = 30;
				break;
			case 2:
				if (parseInt(sel_ano.value) % 4 == 0)
					max_dia = 29;
				else
					max_dia = 28;
				break;
			default:
				max_dia = 31;
		}
		sel_dia.options.length = 0;
		for(var i=1;i<=max_dia;i++) {
			sel_dia.options[i-1] = new Option(adicZeroEsquerda(i,2),i);
		}
	}
	function ajustaDatabase() {
		var sel_database = document.getElementById("sel_database");
		var sel_ano = document.getElementById("sel_ano");
		var sel_mes = document.getElementById("sel_mes");
		var sel_dia = document.getElementById("sel_dia");
		var sel_hora = document.getElementById("sel_hora");
		sel_ano.style.visibility = "hidden";
		sel_mes.style.visibility = "hidden";
		sel_dia.style.visibility = "hidden";
		sel_hora.style.visibility = "hidden";
		switch(sel_database.value) {
			case "<%=FluxoVelocidadeGrade.Tipo.MES.ordinal()%>":
				sel_ano.style.visibility = "visible";
				break;
			case "<%=FluxoVelocidadeGrade.Tipo.DIA.ordinal()%>":
				sel_ano.style.visibility = "visible";
				sel_mes.style.visibility = "visible";
				break;
			case "<%=FluxoVelocidadeGrade.Tipo.HORA.ordinal()%>":
				sel_ano.style.visibility = "visible";
				sel_mes.style.visibility = "visible";
				sel_dia.style.visibility = "visible";
				break;
			case "<%=FluxoVelocidadeGrade.Tipo.QUINZE.ordinal()%>":
			case "<%=FluxoVelocidadeGrade.Tipo.MINUTO.ordinal()%>":
				sel_ano.style.visibility = "visible";
				sel_mes.style.visibility = "visible";
				sel_dia.style.visibility = "visible";
				sel_hora.style.visibility = "visible";
				break;
		}
	}
	function ajustaLocal(porCodigo) {
		var txt_local = document.getElementById("txt_local");
		var sel_local = document.getElementById("sel_local");
		if (porCodigo) {
			sel_local.selectedIndex = 0;

			for (var i=0;i<sel_local.options.length;i++) {
				if (sel_local.options[i].value == txt_local.value)
					sel_local.selectedIndex = i;
			}
		}
		else
			txt_local.value = sel_local.value;
	}
</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_rel" action="rel_fluxo_velocidade_grade.jsp" method="get">
				<table class="tabela_branca" width="500">
					<tr>
						<th class="head_tabela" width="100%" colspan="5">&nbsp;Relatório&nbsp;estatísticos&nbsp;do&nbsp;período&nbsp;</th>
					</tr>
					<tr>
						<td class="label_campo" width="25%">Local:</td>
						<td class="valor_campo" width="10%"><input id="txt_local" type="text" name="id_local" class="campo_texto" maxlength="4" onblur="ajustaLocal(true)"/></td>
						<td class="valor_campo" width="45%">
							<select id="sel_local" name="local" onchange="ajustaLocal(false)">
								<option value="0" selected="selected">--local--</option>
								<c:forEach var="local" items="<%=locais%>">
									<option value="${local.idLocal}">${local.nome}</option>
								</c:forEach>
							</select>
						</td>
						<td class="valor_campo" width="7%">Pista:</td>
						<td class="valor_campo" width="13%">
							<select name="pista">
								<option value="0" selected="selected">Todas</option>
								<option value="1">1</option>
								<option value="2">2</option>
								<option value="3">3</option>
								<option value="4">4</option>
							</select>
						</td>
					</tr>
					<tr>
						<td class="label_campo">Agrupado por:</td>
						<td class="valor_campo" colspan="4">
							<select id="sel_database" name="tipo" style="width: 30%;" onchange="ajustaDatabase()">
								<option value="<%=FluxoVelocidadeGrade.Tipo.ANO.ordinal()%>">Ano</option>
								<option value="<%=FluxoVelocidadeGrade.Tipo.MES.ordinal()%>">Mes</option>
								<option value="<%=FluxoVelocidadeGrade.Tipo.DIA.ordinal()%>">Dia</option>
								<option value="<%=FluxoVelocidadeGrade.Tipo.HORA.ordinal()%>" selected>Hora</option>
								<option value="<%=FluxoVelocidadeGrade.Tipo.QUINZE.ordinal()%>">15 min</option>
								<option value="<%=FluxoVelocidadeGrade.Tipo.MINUTO.ordinal()%>">Minuto</option>
							</select>
						</td>
					</tr>
					<tr>
						<td class="label_campo">Data-base:</td>
						<td class="valor_campo" colspan="4">
							<select id="sel_ano" name="ano" style="width: 30%; visibility: hidden" onchange="ajustaDia()"></select>
							<select id="sel_mes" name="mes" style="width: 25%; visibility: hidden" onchange="ajustaDia()">
								<option value="0">Janeiro</option>
								<option value="1">Fevereiro</option>
								<option value="2">Março</option>
								<option value="3">Abril</option>
								<option value="4">Maio</option>
								<option value="5">Junho</option>
								<option value="6">Julho</option>
								<option value="7">Agosto</option>
								<option value="8">Setembro</option>
								<option value="9">Outubro</option>
								<option value="10">Novembro</option>
								<option value="11">Dezembro</option>
							</select>
							<select id="sel_dia" name="dia" style="width: 25%; visibility: hidden;">
							</select>
							<select id="sel_hora" name="hora" style="width: 15%; visibility: hidden;">
								<c:forEach var="i" begin="0" end="23">
									<option value="${i}">${i}</option>
								</c:forEach>
							</select>
						</td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="5" width="100%">
							<button id="btEnvio" onclick="enviar('btEnvio','frm_rel','div_mens');">Visualizar</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="5" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
	ajustaLocal(true);
	ajustaAno();
	ajustaDatabase();
	ajustaDia();
    document.getElementById("btEnvio").disabled = false;
</script>
<%@ include file="/includes/rodape.jsp" %>
