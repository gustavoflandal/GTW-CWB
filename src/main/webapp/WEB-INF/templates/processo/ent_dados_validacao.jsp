<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/c.tld" prefix="c" %>
<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<tr>
    <td class="box_botoes" colspan="6">
	<table class="tabela_branca" width="100%">
		<tr>
<!--             <td width="11%"> -->
<!--                 <button id="bt_consistir" onclick="return consistir();" style="width: 100%">Consistir</button> -->
<!--             </td> -->
<!--             <td width="12%"> -->
<!--                 <button id="bt_inconsistir" onclick="return inconsistir();" style="width: 100%">Inconsistir</button> -->
<!--             </td> -->
			<td width="11%">
				<button id="bt_auditar" onclick="return auditar();" style="width: 50%">Auditar</button>
			</td>
			<td width="8%">
                <input id="txt_placa" type="text" name="placa" class="campo_texto" maxlength="7" 
                onkeypress="return confirmaCod(event); " onkeyup="verifDigitPlaca(event,this)"
                onblur="verifPlaca(this)" style="font-weight: bold"/>
            </td>
            <td width="6%"> 
            	<select id="sel_valida" name="sel_valida" 
            		onchange="ajustaInconsistencia(true)" 
            		onkeyup="ajustaInconsistencia(true)" 
            		onkeypress="return confirmaCod(event)">
            		<option value="0">Inválida</option>
            		<option value="1">Válida</option>
            	</select>
            
			    <input id="txt_inconsistencia" type="hidden" name="id_inconsistencia" class="campo_texto" maxlength="2" 
			    onkeypress="return confirmaCod(event)" onkeyup="ajustaInconsistencia(true);" />
			</td>
			<td width="63%" colspan="3">
				<table class="tabela_branca" width="100%">
					<tr>
						<td>
							<select id="sel_inconsistencia" name="inc"
				                onchange="ajustaInconsistencia(false)"
				                onkeypress="return confirmaCod(event)" >
								<c:forEach var="inc" items="${inconsistencias}">
									<option value="${inc.idInconsistencia}">${inc.descricao}</option>
								</c:forEach>
								<option value="">--selecione--</option>
							</select>
						</td>
						<c:if test="${marcas_CET != null}">
						<td width="40px">
							<input type="text" id="marca_processo" name="marca_processo"
							onkeyup="ajustaMarca(true);"
						  	maxlength="3" 
							/>
						</td>
						<td>
							<select id="sel_marca_processo" name="id_marca_processo"
								onchange="ajustaMarca(false);"
				                onkeypress="return confirmaCod(event);" >
								<option value="0"></option>
								<c:forEach var="marcaCET" items="${marcas_CET}">
									<option value="${marcaCET.id}">${marcaCET.descricao}</option>
								</c:forEach>
							</select>
						</td>
	 					</c:if>
						<c:if test="${especies != null}">
						<td>
							<select id="sel_especie_processo" name="id_especie_processo"
				                onkeypress="return confirmaCod(event)" >
								<option value="0"></option>
								<c:forEach var="especie" items="${especies}">
									<option value="${especie.id}">${especie.descricao}</option>
								</c:forEach>
							</select>
						</td>
	 					</c:if>
						<c:if test="${ufs != null}">
						<td>
							<select id="sel_uf_processo" name="uf_processo"
				                onkeypress="return confirmaCod(event)" >
								<option value=""></option>
								<c:forEach var="uf" items="${ufs}">
									<option value="${uf}">${uf}</option>
								</c:forEach>
							</select>
						</td>
	 					</c:if>
					</tr>
				</table>
            </td>
		</tr>
		<tr>
            <td width="11%" align="right">
                <button onclick="anterior();">Anterior</button>
            </td>
            <td width="12%" align="left">
                <button onclick="proximo();">Próximo</button>
            </td>
			<td width="77%" colspan="5">&nbsp;</td>
		</tr>
	</table>
    </td>
</tr>
<script type="text/javascript">
    function verifPlaca(txt) {
        if (txt.value == "<PLACA>") 
            txt.value = "";
        else if (txt.value.length > 0)
            validaPlaca(txt.value)
    }
    function verifDigitPlaca(e,txt) {
        txt.value = txt.value.toUpperCase();
        if (txt.value != "<PLACA>" && /[A-Za-z0-9]/.test(String.fromCharCode(e.keyCode))) 
            buscaCadastro(txt.value);
    }
</script>
