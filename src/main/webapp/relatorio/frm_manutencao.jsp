<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho_gwt.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%
	    
    Map<String,Object> mFiltro = new HashMap<String,Object>();
    mFiltro.put("grupo",((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
    List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,2);
%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.Mensagem"%>
<%@page import="com.consilux.model.LocalVigente"%>

<br />
<br />
<br />
<script type="text/javascript" src="/js/download.jQuery.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">
	
	function ajustaDatas() {
		var txt_dataini = document.getElementById("txt_dataini");
		var txt_horaini = document.getElementById("txt_horaini");
		var txt_datafim = document.getElementById("txt_datafim");
		var txt_horafim = document.getElementById("txt_horafim");
		var dtAgora = new Date();
		
		if (txt_dataini.value == "") {
			txt_dataini.value = adicZeroEsquerda(dtAgora.getDate(),2)+"/"+adicZeroEsquerda(dtAgora.getMonth()+1,2)+"/"+dtAgora.getFullYear();
		}
		if (txt_horaini.value == "") {
			txt_horaini.value = "00:00";
		}
		if (txt_datafim.value == "") {
			txt_datafim.value = txt_dataini.value;
		}
		if (txt_horafim.value == "") {
			txt_horafim.value = "23:59";
		}
	}



	function enviarParametros() {

		var dataIni = document.getElementById("txt_dataini").value;
		var horaIni = document.getElementById("txt_horaini").value;
		var dataFim = document.getElementById("txt_datafim").value;
		var horaFim = document.getElementById("txt_horafim").value;

		$.download('/RelatorioManutencao', { 
			dataIni: dataIni,
			dataFim: dataFim,
			horaIni: horaIni,
			horaFim: horaFim
			}, 'get');
		
	}

	
</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_grafic" action="/RelatorioManutencao" method="get">
                <table class="tabela_branca" width="500">
                    <tr>
                        <th class="head_tabela" width="100%" colspan="5">Relatório de Manutenção</th>
                    </tr>
                    <tr>
                        <td class="label_campo">Data inicial:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_dataini" type="text" name="dataini" class="campo_texto" maxlength="10" style="width: 110px">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataini'), 'dd/mm/yyyy')">
                            <input id="txt_horaini" type="text" name="horaini" class="campo_texto" maxlength="8" style="width: 70px">
                            <img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_horaini'), 'hh:mm')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo">Data final:</td>
                        <td class="valor_campo" colspan="4">
                            <input id="txt_datafim" type="text" name="datafim" class="campo_texto" maxlength="10" style="width: 110px">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_datafim'), 'dd/mm/yyyy')">
                            <input id="txt_horafim" type="text" name="horafim" class="campo_texto" maxlength="5" style="width: 70px">
                            <img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_horafim'), 'hh:mm')">
                        </td>
                    </tr>

						<td class="box_botoes" colspan="5" width="100%">
							<button id="btEnvio" onclick="enviarParametros();">Visualizar</button>
						</td>

					<tr>
						<td class="corpo_mensagem" colspan="3" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
	ajustaDatas();
    document.getElementById("btEnvio").disabled = false;
</script>
<%@ include file="/includes/rodape.jsp" %>

