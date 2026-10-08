<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>

<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">
	function ajustaDatas() {
		var txt_dataini = document.getElementById("txt_dataini");
		var txt_datafim = document.getElementById("txt_datafim");
		var dtAgora = new Date();
		
		if (txt_dataini.value == "") {
			txt_dataini.value = adicZeroEsquerda(dtAgora.getDate(),2)+"/"+adicZeroEsquerda(dtAgora.getMonth()+1,2)+"/"+dtAgora.getFullYear();
		}
		if (txt_datafim.value == "") {
			txt_datafim.value = txt_dataini.value;
		}
	}
</script>
<table class="tabela_branca" width="100%" style="height: 100%">
	<tr>
		<td align="center" valign="top">
            <form name="frm_listar" action="/medicao/ListarProcessoMedicao" target="listar" method="get">
				<table class="tabela_branca" width="500">
					<tr>
						<th class="head_tabela" width="100%" colspan="5">Listar Processo de Medição</th>
					</tr>
					<tr>
						<td class="label_campo">Data imagem inicial:</td>
						<td class="valor_campo" colspan="4">
							<input id="txt_dataini" type="text" name="dataini" class="campo_texto" maxlength="10" style="width: 110px">
							<img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_dataini'), 'dd/mm/yyyy')">
						</td>
					</tr>
					<tr>
						<td class="label_campo">Data imagem final:</td>
						<td class="valor_campo" colspan="4">
							<input id="txt_datafim" type="text" name="datafim" class="campo_texto" maxlength="10" style="width: 110px">
							<img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_datafim'), 'dd/mm/yyyy')">
						</td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="5" width="100%">
	                       	<button onclick="frm_listar.submit()">Listar</button>
						</td>
					</tr>
				</table>
            </form>
            <table class="tabela_lista" width="650" style="height: 75%">
				<tr style="height: 100%">
					<td class="valor_campo" colspan="2">
					   <iframe name="listar" width="100%" id="frame_listar_infracao" style="height: 100%"></iframe>
					</td>
				</tr>
                <tr>
                    <td class="label_campo" width="20%">
                        Registros encontrados:
                    </td>
                    <td class="valor_campo" width="80%">
                        <div id="div_conta_registros">0</div>
                    </td>
                </tr>
			</table>
		</td>
	</tr>
</table>
<script type="text/javascript">
	ajustaDatas();
</script>
<%@ include file="/includes/rodape.jsp" %>