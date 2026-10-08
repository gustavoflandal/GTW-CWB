<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<tr>
	<td class="box_botoes" colspan="6">
	<table class="tabela_branca" width="100%">
		<tr>
			<td width="11%">
	       		<button id="bt_consistir" onclick="consistir();" style="width: 100%">Consistir</button>
			</td>
			<td width="12%">
                <button id="bt_inconsistir" onclick="inconsistir();" style="width: 100%">Inconsistir</button>
			</td>
	
			
			<td width="5%">
			   <input id="txt_placa" type="hidden" name="placa"/>
			   <input id="txt_inconsistencia" type="text" name="id_inconsistencia"
				class="campo_texto" maxlength="2" onkeyup="ajustaInconsistencia(true)"
				onkeypress="return confirmaCod(event)" />
		    </td>
			<td width="35%">
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
			<td width="27%"></td>
            <td width="5%">
                <button onclick="anterior();">Anterior</button>
            </td>
            <td width="5%">
                <button onclick="proximo();">Próximo</button>
            </td>
		</tr>
	</table>
	</td>
</tr>
<script type="text/javascript">
    document.getElementById("txt_inconsistencia").focus();
    document.getElementById("txt_inconsistencia").select();
</script>

