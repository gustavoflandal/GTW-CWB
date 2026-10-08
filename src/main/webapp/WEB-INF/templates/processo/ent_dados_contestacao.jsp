<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<tr>
	<td class="box_botoes" colspan="6">
	<table class="tabela_branca" width="80%">
		<tr>
			<td width="23%">
                <button id="bt_inconsistir" onclick="processar();" style="width: 100%">Confirma</button>
			</td>
	
			
			<td width="5%">
			   <input id="txt_inconsistencia" type="text" name="id_inconsistencia"
				class="campo_texto" maxlength="1" 
				onkeypress="return recebe_digito(event); "
				onkeyup="ajustaInconsistencia(true)" />
		    </td>
			<td width="62%">
				<select id="sel_inconsistencia" name="inc"
	                onchange="ajustaInconsistencia(false)" >
					<c:forEach var="inc" items="${inconsistencias}">
						<option value="${inc.idInconsistencia}">${inc.descricao}</option>
					</c:forEach>
					<option value="">--selecione--</option>
				</select>
            </td>
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

