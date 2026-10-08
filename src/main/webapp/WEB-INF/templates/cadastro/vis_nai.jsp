<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<br />
<br />
<br />
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_branca" width="700">
				<tr>
					<th class="head_tabela" width="100%" colspan="6">Confimação de Identificação do Condutor Infrator</th>
				</tr>
				<tr>
					<td class="label_campo" width="15%">Infração:</td>
					<td class="visualiza_campo" width="20%"><jsp:getProperty name="nai" property="infracao" /></td>
					<td class="label_campo" width="10%">&nbsp;Placa:</td>
					<td class="visualiza_campo" width="20%"><jsp:getProperty name="nai" property="placa" /></td>
					<td class="label_campo" width="15%">&nbsp;Data Infração:</td>
					<td class="visualiza_campo" width="20%"><jsp:getProperty name="nai" property="dataInfracao" /></td>
				</tr>
				<tr>
					<td class="label_campo" width="15%">Nome:</td>
					<td class="visualiza_campo" width="85%" colspan="5"><jsp:getProperty name="nai" property="nome" /></td>
				</tr>
				<tr>
					<td class="label_campo" width="15%">Endereço:</td>
					<td class="visualiza_campo" width="85%" colspan="5"><jsp:getProperty name="nai" property="endereco" /></td>
				</tr>
				<tr>
					<td class="label_campo" width="15%">Cidade:</td>
					<td width="50%" colspan="3">
						<table class="tabela_branca" cellspacing="0" width="100%">
							<tr>
								<td width="80%" class="visualiza_campo">
									<jsp:getProperty name="nai" property="cidade" />
								</td>
								<td width="10%" class="label_campo">
									&nbsp;UF:
								</td>
								<td width="10%" class="visualiza_campo">
									<jsp:getProperty name="nai" property="UF" />
								</td>
							</tr>
						</table>
					</td>
					<td class="label_campo" width="15%">&nbsp;CEP:</td>
					<td class="visualiza_campo" width="20%"><jsp:getProperty name="nai" property="CEP" /></td>
				</tr>
				<tr>
					<td class="label_campo">CPF:</td>
					<td class="visualiza_campo"><jsp:getProperty name="nai" property="CPF" /></td>
					<td class="label_campo">&nbsp;RG:</td>
					<td class="visualiza_campo"><jsp:getProperty name="nai" property="RG" /></td>
					<td class="label_campo">&nbsp;Telefone:</td>
					<td class="visualiza_campo"><jsp:getProperty name="nai" property="telefone" /></td>
				</tr>
				<tr>
					<td class="label_campo">Nº CNH:</td>
					<td class="visualiza_campo"><jsp:getProperty name="nai" property="docCNH" /></td>
					<td class="label_campo">&nbsp;Registro:</td>
					<td class="visualiza_campo"><jsp:getProperty name="nai" property="regCNH" /></td>
					<td class="label_campo">&nbsp;UF:</td>
					<td class="visualiza_campo"><jsp:getProperty name="nai" property="UFCNH" /></td>
				</tr>
				<tr>
					<td class="label_campo" width="15%">Data Entrada:</td>
					<td class="visualiza_campo" width="20%"><jsp:getProperty name="nai" property="dataEntrada" /></td>
					<td class="visualiza_campo" width="65%" colspan="4">&nbsp;</td>
				</tr>
				<tr>
					<td class="box_botoes" colspan="6" width="100%">
						<button onclick="window.location = 'atu_nai.jsp';">Concluir</button>
						<button onclick="history.back();">Voltar</button>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
