<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.ferramenta.AgendaEstatico"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="com.consilux.ui.client.beans.PrioridadeGwtBean"%>
<%@page import="com.consilux.ui.client.beans.NivelGwtBean"%>
<%@page import="com.consilux.ui.client.beans.CategoriaGwtBean"%>
<%@page import="com.consilux.ui.client.beans.EventoGwtBean"%>
<%@page import="com.consilux.model.Evento"%>
<%@page import="com.consilux.model.ferramenta.EventoManual"%>
<%@page import="com.consilux.model.ferramenta.EventoManualGrupo"%>
<%@include file="/includes/cabecalho.jsp" %>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript" src="/js/jquery.blockUI.js"></script>

<%
	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("grupo", ((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
    List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,4);
    if(locais.size()==0){
    	locais = LocalVigente.buscaLocalVigenteCAV();
    }
    
	 Integer idEventoManualCategoria = 1;
	 List<EventoManualGrupo> eventoManualCategorias = EventoManualGrupo.listarEventoManualGrupoPorId(idEventoManualCategoria);
//     Integer idEvento = 8;
//     List<EventoGwtBean> eventos = EventoManual.listarEventoPorId(idEvento);
//     List<CategoriaGwtBean> categorias = Evento.listarCategorias();
//     List<PrioridadeGwtBean> prioridades = Evento.listarPrioridades();
//     List<NivelGwtBean> niveis = Evento.listarNiveis();
    
%>

<c:set var="locais" value="<%=locais%>" />
<c:set var="eventoManualCategorias" value="<%=eventoManualCategorias%>" />
<%-- <c:set var="eventos" value="<%=eventos%>" /> --%>
<%-- <c:set var="categorias" value="<%=categorias%>" /> --%>
<%-- <c:set var="prioridades" value="<%=prioridades%>" /> --%>
<%-- <c:set var="niveis" value="<%=niveis%>" /> --%>

<script type="text/javascript">

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
	 
	 function validaCheckbox(tipoEvento){
	    var tipoEventoInicio = document.getElementById("tipoEventoInicio");
	    var tipoEventoRetorno = document.getElementById("tipoEventoRetorno");
	    
	    if(tipoEvento == "inicio" && tipoEventoRetorno.checked){
	    	tipoEventoInicio.checked = true;
	    	tipoEventoRetorno.checked = false;
	    }else if(tipoEvento == "retorno" && tipoEventoInicio.checked){
	    	tipoEventoInicio.checked = false;
	    	tipoEventoRetorno.checked = true;
	    }	    
    }

</script>

<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="cad_evento_manual" action="/ferramentas/CadastrarEventoManual" method="Post">
                <table class="tabela_branca" width="500">
					
					<tr><td>&nbsp;</td> </tr>
					<tr>
                        <th class="head_tabela" width="100%" colspan="5">Cadastro de Evento - Manual</th>
                    </tr>
                    <tr><td>&nbsp;</td> </tr>
                    
                    <tr>
                    	<td class="label_campo" colspan="3">Local:</td>
						<td class="valor_campo" width="10%">
							<input id="txt_local" type="text" name="id_local" class="campo_texto" maxlength="4" onblur="ajustaLocal(true)" value="${id_local}"/>
						</td>
						
						<td class="valor_campo">
							<select id="sel_local" name="local" onchange="ajustaLocal(false)">
								<option value="0" selected="selected">--local--</option>
								<c:forEach var="local" items="${locais}">
									<option value="${local.idLocal}">${local.nome}</option>
								</c:forEach>
							</select>
						</td>
                    </tr>
                    
                    <tr>
                        <td class="label_campo" colspan="4">Data Evento:</td>
                        <td class="valor_campo" colspan="2">
                            <input id="txt_data_evento" type="text" name="dataEvento" class="campo_texto" maxlength="10" style="width: 70px">
                            <img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_evento'), 'dd/mm/yyyy')">
                        </td>
                    </tr>
                    <tr>
                        <td class="label_campo" colspan="4">Hora Evento:</td>
                    	<td class="valor_campo" colspan="2">
                            <input id="txt_hora_evento" type="text" name="horaEvento" class="campo_texto" maxlength="5" style="width: 70px">
                            <img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_evento'), 'hh:mm')">
                        </td>
                    </tr>

                    <tr hidden="true">
                        <td class="label_campo" colspan="3">Grupo:</td>
                        <td class="valor_campo" width="10%">
							<input id="txt_espaco" type="hidden" name="espaco" class="campo_espaco"/>
						</td>
                        <td class="valor_campo">
                            <select id="sel_evento_manual_categoria" name="id_evento_manual_categoria">
								<c:forEach var="eventoManualCategoria" items="${eventoManualCategorias}">
									<option value="${eventoManualCategoria.idEventoManualCategoria}">${eventoManualCategoria.descricao}</option>
								</c:forEach>
							</select>
                        </td>
                    </tr>
                    
					<tr id="gerarTipoEvento">
						<td class="label_campo" colspan="4">Tipo:</td>
						<td class="valor_campo" colspan="1">
							<input type="checkbox" id="tipoEventoInicio" name="selTipoEvento" value="chkInicio" style="vertical-align: middle;" onclick="validaCheckbox('inicio');">Inicio
							<input type="checkbox" id="tipoEventoRetorno" name="selTipoEvento" value="chkRetorno" style="vertical-align: middle;" onclick="validaCheckbox('retorno');">Retorno
                        </td>
					</tr>
                    
                    
<!--                     <tr> -->
<!--                     	<td class="label_campo" colspan="4">Categoria:</td> -->
<!-- 						<td class="valor_campo" width="100%" > -->
<!-- 							<select id="sel_categoria" name="id_categoria"> -->
<!-- 								<option value="-1" selected="selected">--categoria--</option> -->
<%-- 								<c:forEach var="categoria" items="${categorias}"> --%>
<%-- 									<option value="${categoria.id}">${categoria.descricao}</option> --%>
<%-- 								</c:forEach> --%>
<!-- 							</select> -->
<!-- 						</td> -->
<!--                     </tr> -->
                    
<!--                    <tr> -->
<!--                     	<td class="label_campo" colspan="4">Evento:</td> -->
<!-- 						<td class="valor_campo" width="100%" > -->
<!-- 							<select id="sel_evento" name="id_evento"> -->
<%-- 								<c:forEach var="evento" items="${eventos}"> --%>
<%-- 									<option value="${evento.id}">${evento.descricao}</option> --%>
<%-- 								</c:forEach> --%>
<!-- 							</select> -->
<!-- 						</td> -->
<!--                     </tr> -->
                    
<!--                     <tr> -->
<!--                     	<td class="label_campo" colspan="4">Prioridade:</td> -->
<!-- 						<td class="valor_campo" width="100%" > -->
<!-- 							<select id="sel_prioridade" name="id_prioridade"> -->
<!-- 								<option value="-1" selected="selected">--prioridade--</option> -->
<%-- 								<c:forEach var="prioridade" items="${prioridades}"> --%>
<%-- 									<option value="${prioridade.id}">${prioridade.descricao}</option> --%>
<%-- 								</c:forEach> --%>
<!-- 							</select> -->
<!-- 						</td> -->
<!--                     </tr> -->
                    
<!--                     <tr> -->
<!--                     	<td class="label_campo" colspan="4">Nível:</td> -->
<!-- 						<td class="valor_campo" width="100%" > -->
<!-- 							<select id="sel_nivel" name="id_nivel"> -->
<!-- 								<option value="-1" selected="selected">--nível--</option> -->
<%-- 								<c:forEach var="nivel" items="${niveis}"> --%>
<%-- 									<option value="${nivel.id}">${nivel.descricao}</option> --%>
<%-- 								</c:forEach> --%>
<!-- 							</select> -->
<!-- 						</td> -->
<!--                     </tr> -->
                    
<!--                     <tr> -->
<!--                     	<td class="label_campo" colspan="4">Mensagem:</td> -->
<!--                     	<td class="textarea" colspan="2"> -->
<!--                     		<textarea maxlength="512" id="txt_mensagem" name="mensagem" cols="30" rows="2" placeholder="Digite a mensagem relacionada ao evento..." /></textarea> -->
<!--                     	</td> -->
<!--                     </tr> -->
                    
					<tr>
						<td class="box_botoes" width="10%" colspan="5" style="text-align: center;" >
							<button id="btGravarEvento" style="width: 100px;" onclick="this.submit();">Gravar</button>
						</td>
					</tr>
				</table>
			</form>
		</td>
	</tr>
</table>
