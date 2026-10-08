<%@page import="com.consilux.model.ManutencaoN.ManutencaoComentario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.model.ManutencaoN.Manutencao"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="org.apache.log4j.LogManager"%>
<%@page import="com.consilux.model.Grupo"%>
<%@page import="com.consilux.model.ManutencaoN.ManutencaoOcorrencia"%>
<%@page import="com.consilux.model.ManutencaoN.ManutencaoCausa"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@page import="com.consilux.model.ManutencaoN"%>
<%@page import="java.util.Map"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>
<script type="text/javascript" src="/js/calendario.js"></script>
<script type="text/javascript" src="/js/horario.js"></script>

<%

Logger logger = LogManager.getLogger("CadastroManutencao");

boolean finalizada = request.getParameter("finalizada") != null && request.getParameter("finalizada").equals("1");
boolean encaminhar = request.getParameter("encaminhar") != null && request.getParameter("encaminhar").equals("1");

Map<Integer, String> estados = ManutencaoN.ObterEstados();
Map<Integer, String> estaticos = ManutencaoN.ObterEstaticos(); 
Map<String, String> grupos = ManutencaoN.ObterGrupoAutuador();
List<ManutencaoCausa> causas = ManutencaoN.ObterCausas();
List<ManutencaoOcorrencia> ocorrencias = ManutencaoN.ObterOcorrencias();

Map<Integer, Manutencao> manutencoes = ManutencaoN.ObterManutencoes();

Map<String,Object> mFiltro = new HashMap<String,Object>();
mFiltro.put("grupo",((Usuario)session.getAttribute("[usuario]")).getIdGrupoEquipamentoConfig());
List<LocalVigente> locais = LocalVigente.buscaLocalVigentePor(mFiltro,2);

List<Usuario> usuarios = Usuario.buscaUsuarioPorGrupo(7);

Integer id_manutencao = null;
Manutencao manutencao_at = null;
if (request.getParameter("id_manutencao") != null 				&& Pattern.matches("^[0-9]+$", request.getParameter("id_manutencao")))
	id_manutencao = Integer.parseInt(request.getParameter("id_manutencao"));
if (id_manutencao != null) {
	manutencao_at = manutencoes.get(id_manutencao);
}

Integer estado = 0, id_local = 0, pista = 0, serie_equipamento = 0, ocorrencia = 0, num_oficio = 0, ano_oficio = 0, tecnico_resp = 0, tecnico_aux = 0;
String data_ocorrencia ="", hora_ocorrencia = "", grupo_autuador = "", descricao = "", data_inicio = "", data_fim = "";
List<Integer> causas_sel = new ArrayList<Integer>();
List<ManutencaoComentario> comentarios = new ArrayList<ManutencaoComentario>();

if (manutencao_at != null) {
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat shf = new SimpleDateFormat("HH:mm");
	
	try {
		if (manutencao_at.getIdStatus() != null)
		estado = manutencao_at.getIdStatus();
		if (manutencao_at.getIdLocal() != null)
		id_local = manutencao_at.getIdLocal();
	 	if (manutencao_at.getIdPista() != null)
		pista = manutencao_at.getIdPista();
	 	if (manutencao_at.getSerieEquipamento() != null)
	 	serie_equipamento = manutencao_at.getSerieEquipamento();
	 	if (manutencao_at.getIdOcorrencia() != null)
	 	ocorrencia = manutencao_at.getIdOcorrencia();
	 	if (manutencao_at.getNumeroOficio() != null)
	 	num_oficio = manutencao_at.getNumeroOficio();
	 	if (manutencao_at.getAnoOficio() != null)
	 	ano_oficio = manutencao_at.getAnoOficio();
	 	if (manutencao_at.getIdTecnico() != null)
	 	tecnico_resp = manutencao_at.getIdTecnico();
	 	if (manutencao_at.getIdAuxiliar() != null)
	 	tecnico_aux = manutencao_at.getIdAuxiliar();
	 	if (manutencao_at.getDataOcorrencia() != null) {
		data_ocorrencia = sdf.format(manutencao_at.getDataOcorrencia());
		hora_ocorrencia = shf.format(manutencao_at.getDataOcorrencia());
	 	}
	 	if (manutencao_at.getTipoGrupoAutuador() != null)
		grupo_autuador = manutencao_at.getTipoGrupoAutuador();
	 	if (manutencao_at.getDescricao() != null)
		descricao = manutencao_at.getDescricao();
	 	if (manutencao_at.getDataInicio() != null) {
		data_inicio = sdf.format(manutencao_at.getDataInicio());
		data_fim = sdf.format(manutencao_at.getDataPrevisto());
	 	}
	 	finalizada = manutencao_at.getDataConclusao() != null;
	 	encaminhar = manutencao_at.isEncaminhar();
	 	
	 	causas_sel = ManutencaoN.ObterCausas(manutencao_at.getIdManutencao());
	 	comentarios = ManutencaoN.ObterComentarios(manutencao_at.getIdManutencao());
	}
	catch (Exception e) { logger.error("Carregar pagina", e); }
}


%>

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
	function ajustaEstado(porCodigo) {
		
		var sel_estado 		= document.getElementById("sel_estado");
		var chk_finalizada 	= document.getElementById("chk_finalizada");
		var txt_sel_estado_ant = document.getElementById("txt_sel_estado_ant");
		
		if (porCodigo) {
			if (sel_estado[sel_estado.selectedIndex].innerHTML == "Encerrado")
				chk_finalizada.checked = true;
			else
				chk_finalizada.checked = false;
		}
		else {
			
			if (chk_finalizada.checked) {
				txt_sel_estado_ant.value = sel_estado.selectedIndex;
				
				for (var i=0;i<sel_estado.options.length;i++) {
					if (sel_estado[i].innerHTML == "Encerrado")
						sel_estado.selectedIndex = i;
				}
			}
			else {
				sel_estado.selectedIndex = txt_sel_estado_ant.value;
			}
		}
			
		
	}
	function countChar() {
        
        var txt_comentario = document.getElementById("txt_comentario");
        var charNum = document.getElementById("charNum");
        
        charNum.innerHTML = 300 - txt_comentario.value.length;
        
    }
	
	var map={"â":"a","Â":"A","à":"a","À":"A","á":"a","Á":"A","ã":"a","Ã":"A","ê":"e","Ê":"E","è":"e","È":"E","é":"e","É":"E","î":"i","Î":"I","ì":"i","Ì":"I","í":"i","Í":"I","õ":"o","Õ":"O","ô":"o","Ô":"O","ò":"o","Ò":"O","ó":"o","Ó":"O","ü":"u","Ü":"U","û":"u","Û":"U","ú":"u","Ú":"U","ù":"u","Ù":"U","ç":"c","Ç":"C"};

	function removerAcentos(s)
	{
		s.value = s.value.replace(/[\W\[\] ]/g,function(a){return map[a]||a});
	}
	
	function ajustaOcorrencia() {
		var sel_ocorrencia    = document.getElementById("sel_ocorrencia");
		var txt_num_oficio    = document.getElementById("txt_num_oficio");
		var txt_ano_oficio    = document.getElementById("txt_ano_oficio");
		var txt_local         = document.getElementById("txt_local");
		var txt_descricao     = document.getElementById("txt_descricao");
		var txt_descricao     = document.getElementById("txt_descricao");
		var txt_descricao_ant = document.getElementById("txt_descricao_ant");
		var sel_estado        = document.getElementById("sel_estado");
		var txt_data_ocorrencia = document.getElementById("txt_data_ocorrencia");
		var txt_hora_ocorrencia = document.getElementById("txt_hora_ocorrencia");
		var serie_equipamento = document.getElementById("serie_equipamento");
		
		var sel_ocorrencia_texto = sel_ocorrencia[sel_ocorrencia.selectedIndex].innerHTML;
		var sel_ocorrencia_pt = sel_ocorrencia_texto.split("-");
		
		txt_num_oficio.value = sel_ocorrencia_pt[0];
		txt_ano_oficio.value = sel_ocorrencia_pt[1];
		
		if (txt_local.value != sel_ocorrencia_pt[2])
		{
			txt_local.value = sel_ocorrencia_pt[2];
			ajustaLocal(true);
		}
		if (txt_descricao.value == "" || txt_descricao.value == txt_descricao_ant.value)
		{
			txt_descricao_ant.value = sel_ocorrencia_pt[3];
			txt_descricao.value = sel_ocorrencia_pt[3];
		}
		
		for (var i=0;i<sel_estado.options.length;i++) {
			if (sel_estado[i].innerHTML == sel_ocorrencia_pt[4])
				sel_estado.selectedIndex = i;
		}
		ajustaEstado(true);
		
		txt_data_ocorrencia.value = sel_ocorrencia_pt[5];
		txt_hora_ocorrencia.value = sel_ocorrencia_pt[6];
		
		serie_equipamento.selectedIndex = 0;
		
		for (var i=0;i<serie_equipamento.options.length;i++) {
			if (serie_equipamento[i].innerHTML == sel_ocorrencia_pt[7])
				serie_equipamento.selectedIndex = i;
		}
	}
</script>

<c:set var="id_manutencao"  value="<%=id_manutencao%>" />
<c:set var="manutencoes"    value="<%=manutencoes.values()%>" />
<c:set var="finalizada" 	value="<%=finalizada%>" />
<c:set var="encaminhar" 	value="<%=encaminhar%>" />
<c:set var="estados" 		value="<%=estados%>" />
<c:set var="grupos" 		value="<%=grupos%>" />
<%-- <c:set var="id_local" value="<%=idLocal%>" /> --%>
<c:set var="locais" 		value="<%=locais%>" />
<c:set var="estaticos" 		value="<%=estaticos%>" />
<c:set var="causas" 		value="<%=causas%>" />
<c:set var="ocorrencias" 	value="<%=ocorrencias%>" />
<c:set var="usuarios" 		value="<%=usuarios%>" />

<c:set var="estado"            value="<%=estado%>"/>
<c:set var="data_ocorrencia"   value="<%=data_ocorrencia%>"/>
<c:set var="hora_ocorrencia"   value="<%=hora_ocorrencia%>"/>
<c:set var="id_local"          value="<%=id_local%>"/>
<c:set var="pista"             value="<%=pista%>"/>
<c:set var="grupo_autuador"    value="<%=grupo_autuador%>"/>
<c:set var="serie_equipamento" value="<%=serie_equipamento%>"/>
<c:set var="descricao" value="<%=descricao%>"/>

<c:set var="causas_sel"     value="<%=causas_sel%>"/>
<c:set var="data_inicio" value="<%=data_inicio%>"/>
<c:set var="data_fim" value="<%=data_fim%>"/>
<c:set var="ocorrencia" value="<%=ocorrencia%>"/>

<c:set var="num_oficio" value="<%=num_oficio%>"/>
<c:set var="ano_oficio" value="<%=ano_oficio%>"/>

<c:set var="tecnico_resp" value="<%=tecnico_resp%>"/>
<c:set var="tecnico_aux"  value="<%=tecnico_aux%>"/>

<c:set var="comentarios" value="<%=comentarios%>" />

<br/>
<br/>

<center>

<form id="frm_id_manutencao" method="get">

<!-- <table border="1"> -->
<table>

<tr> <td class="label_campo">Manutenção Nº:</td> <td colspan="2">
<select id="sel_manutencao" name="id_manutencao" onchange="submit();" style="width: 200px;">
<option value="0" selected>--Nova Manutenção--</option>
<c:forEach var="m" items="${manutencoes}">
	<c:set var="selecionador" value="${m.idManutencao == id_manutencao ? 'selected' : ''}" />
	<option value="${m.idManutencao}" ${selecionador}>${m.descricaoExt}</option>
</c:forEach>
</select> 
</td>

</form>

<form id="frm_manutencao" action="/manutencao/CadastroManutencao" method="get">

<td class="label_campo">Estado:</td>
<td colspan="2">
<select id="sel_estado" name="estado" onchange="ajustaEstado(true);">
<c:forEach var="e" items="${estados}">
	<c:set var="selecionador" value="${e.key == estado ? 'selected' : ''}" />
	<option value="${e.key}" ${selecionador}>${e.value}</option>
</c:forEach>
</select>
</td>
</tr>

<tr> <td class="label_campo"><strong>Data da Falha*:</strong></td> 
<td><input id="txt_data_ocorrencia" type="text" name="data_ocorrencia" maxlength="10" style="width: 80px" onblur="preenchePeriodo();" value="${data_ocorrencia}"></td>
<td><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_ocorrencia'), 'dd/mm/yyyy')"></td>
<td><input id="txt_hora_ocorrencia" type="text" name="hora_ocorrencia" class="campo_texto" maxlength="5" style="width: 40px" value="${hora_ocorrencia}"></td>
<td><img src="/images/calendario/relogio.png" align="top" onclick="mostraHorario(this, document.getElementById('txt_hora_ocorrencia'), 'hh:mm')"></td> 
<td width="100px"></td> <td width="100px"></td> <td width="100px"></td>
</tr>

<tr> <td class="label_campo"><strong>Local*:</strong></td>
<td><input id="txt_local" type="text" name="id_local" class="campo_texto" maxlength="4" style="width: 100px" onblur="ajustaLocal(true)" value="${id_local}"/></td>
<td colspan="2">
<select id="sel_local" name="local" onchange="ajustaLocal(false)" style="width: 200px">
    <option value="0" selected="selected">--local--</option>
    <c:forEach var="local" items="${locais}">
    	<c:set var="selecionador" value="${local.idLocal == id_local ? 'selected' : ''}" />
        <option value="${local.idLocal}" ${selecionador}>${local.nome}</option>
    </c:forEach>
</select>
</td>
<td class="label_campo">Pista:</td>
<td><select name="pista">
	<option value="0" selected="selected">--Todas--</option>
	<c:forEach var="i" begin="1" end="13">
		<c:set var="selecionador" value="${pista == i ? 'selected' : ''}" />
		<option value="${i}" ${selecionador}>${i}</option>
	</c:forEach>
</select>
</td>
<td class="label_campo">Série (Estático):</td>
<td>
<select id="serie_equipamento" name="serie_equipamento">
<option value="0">--N/A--</option>
<c:forEach var="e" items="${estaticos}">
	<c:set var="selecionador" value="${serie_equipamento == e.key ? 'selected' : ''}" />
    <option value="${e.key}" ${selecionador}>${e.value}</option>
</c:forEach>
</select>
</td>
</tr>

<tr>
<td class="label_campo">Grupo Autuador:</td>
<td>
<select id="grupo_autuador" name="grupo_autuador" style="width: 100px">
<option value="">--Todos--</option>
<c:forEach var="g" items="${grupos}">
	<c:set var="selecionador" value="${g.value == grupo_autuador ? 'selected' : ''}" />
	<option value="${g.key}" ${selecionador}>${g.value}</option>
</c:forEach>
</select>
</td>
<td class="label_campo"><strong>Descrição*:</strong></td>
<td colspan="5"><input id="txt_descricao" type="text" name="descricao" class="campo_texto" onblur="removerAcentos(this);" value="${descricao}"/></td>
</tr>

<tr>
<td class="label_campo"><strong>Causa(s)*:</strong></td>
<td colspan="3">
<select multiple="multiple" name="causas">
<c:forEach var="c" items="${causas}">
	<c:set var="selecionador" value="${causas_sel.contains(c.idCausa) ? 'selected' : ''}" />
	<option value="${c.idCausa}" ${selecionador}>${c.descricao}</option>
</c:forEach>
</select>
</td>
<td width="100px"></td> <td width="100px"></td> <td width="100px"></td> <td width="100px"></td>
</tr>

<tr>
<td class="label_campo"><strong>Data Início*:</strong></td>
<td><input id="txt_data_inicio" type="text" name="data_inicio" maxlength="10" style="width: 80px" onblur="preenchePeriodo();" value="${data_inicio}"></td>
<td><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_inicio'), 'dd/mm/yyyy')"></td>
<td class="label_campo"><strong>Data Previsto*:</strong></td>
<td><input id="txt_data_fim" type="text" name="data_fim" maxlength="10" style="width: 80px" onblur="preenchePeriodo();" value="${data_fim}"></td>
<td><img src="/images/calendario/calendario.png" align="top" onclick="mostraCalendario(this, document.getElementById('txt_data_fim'), 'dd/mm/yyyy')"></td>
<td colspan="2" class="label_campo"><input type="checkbox" id="chk_finalizada" name="finalizada" onclick="ajustaEstado(false);" value="1" ${finalizada ? 'checked' : ''}/>Manutenção Finalizada</td>
</tr>

<tr>
<td class="label_campo">Ocorrência:</td>
<td colspan="2" width="200px">
<select id="sel_ocorrencia" name="ocorrencia" onchange="ajustaOcorrencia()">
<option value="">--N/D--</option>
<c:forEach var="o" items="${ocorrencias}">
	<c:set var="selecionador" value="${ocorrencia == o.idOcorrencia ? 'selected' : ''}" />
	<option value="${o.idOcorrencia}" ${selecionador}>${o.descricaoExt}</option>
</c:forEach>
</select> 
</td>
<td class="label_campo">Número Ofício:</td><td><input type="text" id="txt_num_oficio" name="num_oficio" value="${num_oficio}"/> </td>
<td class="label_campo">Ano Ofício:</td><td><input type="text" id="txt_ano_oficio" name="ano_oficio" value="${ano_oficio}"   /> </td>
<td class="label_campo"><input type="checkbox" name="encaminhar" value="1" ${encaminhar ? 'checked' : ''}/>Encaminhar</td> 
</tr>

<tr>
<td class="label_campo"><strong>Técnico*:</strong></td><td colspan="2">
<select id="sel_tecnico_resp" name="tecnico_resp">
<option>--Selecione--</option>
<c:forEach var="u" items="${usuarios}">
	<c:set var="selecionador" value="${u.id == tecnico_resp ? 'selected' : ''}" />
	<option value="${u.id}" ${selecionador}>${u.nome}</option>
</c:forEach>
</select>
</td>
<td class="label_campo">Auxiliar:</td><td colspan="2">
<select id="sel_tecnico_aux" name="tecnico_aux">
<option>--N/D--</option>
<c:forEach var="u" items="${usuarios}">
	<c:set var="selecionador" value="${u.id == tecnico_aux ? 'selected' : ''}" />
	<option value="${u.id}" ${selecionador}>${u.nome}</option>
</c:forEach>
</select>
</td>
<td width="100px"></td><td width="100px"></td>
</tr>

</table>

<c:if test="${comentarios.size() > 0}">
<table>
<thead><tr><th>Data Comentário</th><th>Nome Usuário</th><th width="600px">Comentário</th></tr></thead>
<c:forEach var="c" items="${comentarios}">
<tr><td class="label_campo"><fmt:formatDate	value="${c.data}" type="both" pattern="dd/MM/yyyy HH:mm:ss" /></td><td class="label_campo">${c.nomeUsuario}</td><td class="texto_pequeno" style="text-align: left;">${c.comentarioExt}</td></tr>
</c:forEach>
</table>
</c:if>

<table>
<thead><tr><th width="810px">Comentário</th></tr></thead>
<tr><td><textarea rows="3" cols="20" id="txt_comentario" name="comentario" maxlength="300" onblur="removerAcentos(this);"  onkeyup="countChar();"></textarea><div id="charNum" class="label_campo">300</div></td></tr>
</table>


<br/>
<br/>

<button>Salvar</button>

<input type="hidden" id="txt_descricao_ant" value="${descricao_ant}" />
<input type="hidden" id="txt_id_manutencao" name="id_manutencao" value="${id_manutencao}" />
<input type="hidden" id="txt_sel_estado_ant" value="0" /> 

</form>

</center>

<%@ include file="/includes/rodape.jsp" %>