<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript">
	function buscaInfoInfracao(idInfracao) {
		$.get('/ajax/InfoInfracao', { id_infracao: idInfracao }, function(xml){
			document.getElementById("txt_placa").value = $("PLACA",xml).text();
			document.getElementById("txt_data_infracao").value = $("DATA",xml).text();
		});
	}
</script>
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_nai" action="conf_nai.jsp" method="post">
				<table class="tabela_branca" width="700">
					<tr>
						<th class="head_tabela" width="100%" colspan="6">Formulário de Identificação do Condutor Infrator</th>
					</tr>
					<tr>
						<td class="label_campo" width="15%">Infração:</td>
						<td class="valor_campo" width="20%"><input id="txt_infracao" type="text" name="infracao" class="campo_texto" maxlength="15" onblur="buscaInfoInfracao(this.value)" value="<jsp:getProperty name="nai" property="infracao" />"/></td>
						<td class="label_campo" width="10%">&nbsp;Placa:</td>
						<td class="valor_campo" width="20%"><input id="txt_placa" type="text" name="placa" class="campo_texto" disabled value="<jsp:getProperty name="nai" property="placa" />"/></td>
						<td class="label_campo" width="15%">&nbsp;Data Infração:</td>
						<td class="valor_campo" width="20%"><input id="txt_data_infracao" type="text" name="dataInfracao" class="campo_texto" disabled value="<jsp:getProperty name="nai" property="dataInfracao" />"/></td>
					</tr>
					<tr>
						<td class="label_campo" width="15%">Nome:</td>
						<td class="valor_campo" width="85%" colspan="5"><input id="txt_nome" type="text" name="nome" class="campo_texto" maxlength="60" value="<jsp:getProperty name="nai" property="nome" />"/></td>
					</tr>
					<tr>
						<td class="label_campo" width="15%">Endereço:</td>
						<td class="valor_campo" width="85%" colspan="5"><input id="txt_endereco" type="text" name="endereco" class="campo_texto" maxlength="100" value="<jsp:getProperty name="nai" property="endereco" />"/></td>
					</tr>
					<tr>
						<td class="label_campo" width="15%">Cidade:</td>
						<td width="50%" colspan="3">
							<table class="tabela_branca" cellspacing="0" width="100%">
								<tr>
									<td width="80%" class="valor_campo">
										<input id="txt_cidade" type="text" name="cidade" class="campo_texto" maxlength="50" value="<jsp:getProperty name="nai" property="cidade" />"/>
									</td>
									<td width="10%" class="label_campo">
										&nbsp;UF:
									</td>
									<td width="10%" class="valor_campo">
										<input id="txt_uf" type="text" name="UF" class="campo_texto" maxlength="2" value="<jsp:getProperty name="nai" property="UF" />"/>
									</td>
								</tr>
							</table>
						</td>
						<td class="label_campo" width="15%">&nbsp;CEP:</td>
						<td class="valor_campo" width="20%"><input id="txt_cep" type="text" name="CEP" class="campo_texto" maxlength="8" value="<jsp:getProperty name="nai" property="CEP" />"/></td>
					</tr>
					<tr>
						<td class="label_campo">CPF:</td>
						<td class="valor_campo"><input id="txt_cpf" type="text" name="CPF" class="campo_texto" maxlength="11" value="<jsp:getProperty name="nai" property="CPF" />"/></td>
						<td class="label_campo">&nbsp;RG:</td>
						<td class="valor_campo"><input id="txt_rg" type="text" name="RG" class="campo_texto" maxlength="20" value="<jsp:getProperty name="nai" property="RG" />"/></td>
						<td class="label_campo">&nbsp;Telefone:</td>
						<td class="valor_campo"><input id="txt_telefone" type="text" name="telefone" class="campo_texto" maxlength="10" value="<jsp:getProperty name="nai" property="telefone" />"/></td>
					</tr>
					<tr>
						<td class="label_campo">Nº CNH:</td>
						<td class="valor_campo"><input id="txt_doc_cnh" type="text" name="docCNH" class="campo_texto" maxlength="10" value="<jsp:getProperty name="nai" property="docCNH" />"/></td>
						<td class="label_campo">&nbsp;Registro:</td>
						<td class="valor_campo"><input id="txt_reg_cnh" type="text" name="regCNH" class="campo_texto" maxlength="10" value="<jsp:getProperty name="nai" property="regCNH" />"/></td>
						<td class="label_campo">&nbsp;UF:</td>
						<td class="valor_campo"><input id="txt_uf_cnh" type="text" name="UFCNH" class="campo_texto" maxlength="2" value="<jsp:getProperty name="nai" property="UFCNH" />"/></td>
					</tr>
					<tr>
						<td class="label_campo" width="15%">Data Entrada:</td>
						<td class="valor_campo" width="20%">
							<input id="txt_data_entrada" type="text" name="dataEntrada" class="campo_texto" maxlength="10" style="width: 80%" value="<jsp:getProperty name="nai" property="dataEntrada" />"/>
							<img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_entrada'), 'dd/mm/yyyy')">
						</td>
						<td class="valor_campo" width="65%" colspan="4">&nbsp;</td>
					</tr>
					<tr>
						<td class="box_botoes" colspan="6" width="100%">
							<button onclick="document.getElementById('frm_nai').submit()">Avançar</button><br>
						</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
