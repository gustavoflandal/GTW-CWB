<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<tr>
	<td class="box_botoes" colspan="8">
	<table class="tabela_branca" width="100%">
		<tr>
            <td width="10%">
                <button id="bt_consistir" onclick="consistir();" style="width: 100%">Consistir</button>
            </td>
            <td width="10%">
                <button id="bt_inconsistir" onclick="inconsistir();" style="width: 100%">Inconsistir</button>
            </td>
			<td width="4%">
				<input id="txt_placa" type="hidden" name="placa"/>
                <input id="txt_placa_p1" type="text" class="campo_texto" maxlength="3" onkeypress="return recebe_digito(event); " onkeyup="recebe_inconsistencia();" style="font-weight: bold"/>
            </td>
            
            <td width="5%"> 
			    <input id="txt_placa_p2" type="text" class="campo_texto" maxlength="4" onkeypress="return recebe_digito(event); " onkeyup="recebe_inconsistencia();" style="font-weight: bold"/>
			</td>
			
			<td width="3%">
				<input id="txt_inconsistencia" type="text" name="id_inconsistencia" class="campo_texto" maxlength="2" 
			    onkeypress="return confirmaCod(event)" onkeyup="ajustaInconsistencia(true);"/>
			</td>
			
			<td width="68%" colspan="4">
				<table class="tabela_branca" width="100%">
					<tr>
						<td width="55%">
							<select id="sel_inconsistencia" name="inc"
				                onchange="ajustaInconsistencia(false)"
				                onkeypress="return confirmaCod(event)" >
				                <option value="0">Consistente</option>
								<c:forEach var="inc" items="${inconsistencias}">
									<option value="${inc.idInconsistencia}">${inc.descricao}</option>
								</c:forEach>
								<option value="">--selecione--</option>
							</select>
						</td>
						<c:if test="${marcas_CET != null}">
						<td width="18%">
							<select id="sel_marca_processo" name="id_marca_processo"
				                onkeypress="return confirmaCod(event)" >
								<option value="0"></option>
								<c:forEach var="marcaCET" items="${marcas_CET}">
									<option value="${marcaCET.id}">${marcaCET.descricao}</option>
								</c:forEach>
							</select>
						</td>
	 					</c:if>
						<c:if test="${especies != null}">
						<td width="15%">
							<select id="sel_especie_processo" name="id_especie_processo"
				                onkeypress="return confirmaCod(event)" >
								<option value="0"></option>
								<c:forEach var="especie" items="${especies}">
									<option value="${especie.id}">${especie.descricao}</option>
								</c:forEach>
							</select>
						</td>
	 					</c:if>
	 					<td width="12%">
							<select id="sel_classificacao_veiculo" name="id_classificacao_veiculo" onkeypress="return confirmaCod(event)" >
								<option value="">N/D</option>
								<option value="Leve">Leve</option>
								<option value="Pesado">Pesado</option>
							</select>
						</td>
					</tr>
				</table>
            </td>
		</tr>
		<tr>
            <td width="10%" align="right">
                <button onclick="anterior();">Anterior</button>
            </td>
            <td width="10%" align="left">
                <button onclick="proximo();">Próximo</button>
            </td>
			<td width="80%" colspan="5">&nbsp;</td>
		</tr>
	</table>
	</td>
</tr>
<script type="text/javascript">
    function verifPlaca(txt) {
        if (txt.value == "<PLACA>") 
            txt.value = "";
        else if (txt.value.length > 0) {
            if (!validaPlaca(txt.value))
              txt.value = "";
        }
    }
    function verifDigitPlaca(e,txt) {
        txt.value = txt.value.toUpperCase();
        if (txt.value != "<PLACA>") {
            buscaCadastro(txt.value);
        }
    }
</script>