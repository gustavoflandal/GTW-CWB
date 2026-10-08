<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<br/>
<br/>
<br/>


<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_relatorios_CAV_CAI" action="/relatorio/RelatoriosMedicao_CAV_CAI" method="get" target="_blank">
                <table class="tabela_branca" width="500">
                     <tr>
                        <th class="head_tabela" width="100%" colspan="3">Processo de Medição - Relatórios gerados no CAV</th>
                    </tr>
                    <tr><td>&nbsp;</td> </tr>
                    <tr>
                        <td class="label_campo" colspan="1">Mês de geração:</td>
                        <td class="valor_campo" colspan="1">
                            <input id="txt_mes" type="text" name="mes" class="campo_texto" maxlength="2" style="width: 40px">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo" colspan="1">Ano de geração:</td>
                        <td class="valor_campo" colspan="1">
                            <input id="txt_ano" type="text" name="ano" class="campo_texto" maxlength="4" style="width: 80px">
                        </td>
                    </tr>
                    
<!--                     <tr><td>&nbsp;</td> </tr> -->
                     
<!--                     <tr id="gerarPartes1"> -->
<!--                         <td class="valor_campo" colspan="2"> -->
<!-- 							<input type="checkbox" name="chkTodos"  value="1" checked="checked" style="vertical-align: middle;">Todos os Relatórios -->
<!--                         </td> -->
<!-- 					</tr> -->
					
					<tr><td>&nbsp;</td> </tr>
					
					<tr id="gerarPartes2">
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chk4Minutos" style="vertical-align: middle;">4 Minutos Fixo
                        </td>
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chkFuncionamentoEstatico" style="vertical-align: middle;">Funcionamento Estático
                        </td>
					</tr>
					<tr id="gerarPartes3">
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chkInfracoesFixo" style="vertical-align: middle;">Imagens Fixo
                        </td>
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chkInfracoesEstatico" style="vertical-align: middle;">Imagens Estático
                        </td>
					</tr>					
					<tr id="gerarPartes4">
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chkFluxoFixo" style="vertical-align: middle;">Fluxo na Via Fixo
                        </td>
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chkFluxoEstatico" style="vertical-align: middle;">Fluxo na Via Estático
                        </td>
					</tr>					
					<tr id="gerarPartes5">
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chkAtraso" style="vertical-align: middle;">Atraso de Imagens
                        </td>
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chkErros" style="vertical-align: middle;">Erros de Validação
                        </td>
					</tr>
					<tr id="gerarPartes6">
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chkInfracoesConsistFixo" style="vertical-align: middle;">Infrações Consistentes Fixo
                        </td>
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chkInfracoesConsistEstatico" style="vertical-align: middle;">Infrações Consistentes Estático
                        </td>
					</tr>		
					<tr id="gerarPartes7">
                        <td class="valor_campo" colspan="2">
							<input type="checkbox" name="selRelatorio" value="chkLotesReprovados" style="vertical-align: middle;">Lotes Reprovados
                        </td>
					</tr>		
					
					<tr><td>&nbsp;</td> </tr>
				
					<tr>
						<td class="box_botoes" colspan="3" width="100%" >
							<button id="btEnvio">Importar Medição</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="3" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
<script type="text/javascript">
    document.getElementById("btEnvio").disabled = false;
</script>