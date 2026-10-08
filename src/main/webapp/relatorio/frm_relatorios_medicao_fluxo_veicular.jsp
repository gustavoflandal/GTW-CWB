<%@page import="com.consilux.model.Pista"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="com.consilux.model.relatorio.rj.RelatorioEditalRJ"%>
<%@page import="com.consilux.model.LocalVigente"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>

<%
	String sRelatorio = request.getParameter("relatorio") != null ? request.getParameter("relatorio").trim() : null;
	sRelatorio = sRelatorio != null && sRelatorio.length() == 0 ? null : sRelatorio;
	String idRelatorio = sRelatorio != null ? String.valueOf(sRelatorio) : "0";
	
	Integer intRelatorio = Integer.valueOf( idRelatorio.split("_")[0] );
	
	String sRelatorioAux = request.getParameter("relatorio_aux") != null ? request.getParameter("relatorio_aux").trim() : null;
	sRelatorioAux = sRelatorioAux != null && sRelatorioAux.length() == 0 ? null : sRelatorioAux;
	Integer idRelatorioAux = sRelatorioAux != null ? Integer.valueOf(sRelatorioAux) : 0;
	
	String sFiltroLocal = request.getParameter("filtro_local") != null ? request.getParameter("filtro_local").trim() : null;
	sFiltroLocal = sFiltroLocal != null && sFiltroLocal.length() == 0 ? null : sFiltroLocal;
	Boolean filtroLocal = sFiltroLocal != null ? Boolean.parseBoolean(sFiltroLocal) : true;

	String sMes = request.getParameter("mes") != null ? request.getParameter("mes").trim() : null;
	sMes = sMes != null && sMes.length() == 0 ? null : sMes;
	String mes = sMes != null ? sMes.toString() : "";
	
	String sAno = request.getParameter("ano") != null ? request.getParameter("ano").trim() : null;
	sAno = sAno != null && sAno.length() == 0 ? null : sAno;
	String ano = sAno != null ? String.valueOf(sAno) : "";	

	String sLocal = request.getParameter("local") != null ? request.getParameter("local").trim() : null;
	sLocal = sLocal != null && sLocal.length() == 0 ? null : sLocal;
	Integer idLocal = sLocal != null ? Integer.valueOf(sLocal) : 0;
	
	List<LocalVigente> locais = LocalVigente.buscaLocalVigente();
	List<RelatorioEditalRJ> relatorios = RelatorioEditalRJ.buscaRelatorios(RelatorioEditalRJ.TipoRelatorioEditalRJ.FLUXO_VEICULAR.GetID());

	Map<String,Object> mFiltros = new HashMap<String,Object>();
	List<Pista> pistas = new ArrayList<Pista>();
	if (idLocal > 0) {
		mFiltros.put("lv.id_local", idLocal);
		pistas = Pista.buscarPistaPor(mFiltros);
	}
%>

<c:set var="locais" value="<%=locais%>" />
<c:set var="relatorios" value="<%=relatorios%>" />
<c:set var="idRelatorio" value="<%=idRelatorio%>" />
<c:set var="mes" value="<%=mes%>" />
<c:set var="ano" value="<%=ano%>" />
<c:set var="idLocal" value="<%=idLocal%>" />
<c:set var="idRelatorioAux" value="<%=idRelatorioAux%>" />
<c:set var="filtroLocal" value="<%=filtroLocal%>" />
<c:set var="intRelatorio" value="<%=intRelatorio %>" />
<c:set var="pistas" value="<%=pistas %>" />

<script type="text/javascript" src="/js/funcoes.js"></script>
<br/>
<br/>
<br/>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<form id="frm_filtro_relatorio" action="" method="get">
			    <table class="tabela_branca" width="550">
					
                    <tr>
                        <th class="head_tabela" width="100%" colspan="6">Relatórios de Fluxo Veicular</th>
                    </tr>
                    <tr><td>&nbsp;</td> </tr>
                    
                    <tr>
                    	<td class="label_campo" colspan="1">Relatório:</td>
                    	<td class="valor_campo" colspan="4">
                          	<select id="sel_relatorio" name="relatorio" onchange="setIdRelatorio();limparLocal();submit();">
                          		<option value="0" selected="selected">-- Relatório --</option>
                              	<c:forEach var="relatorio" items="${relatorios}">
                                  	<option value="${relatorio.idRelatorio}_${relatorio.filtroPorLocal}" ${idRelatorioAux == relatorio.idRelatorio ? 'selected' : ''}>${relatorio.nomeRelatorioFormatado}</option>
                              	</c:forEach>
                          	</select>
                      	</td>
                    </tr>
                    
                    <c:if test="${intRelatorio > 0}">
                    <tr>
                        <td class="label_campo" colspan="1" width="12%">Mês:</td>
                        <td class="valor_campo" colspan="1">
                            <input id="txt_mes" type="text" name="mes" class="campo_texto" maxlength="2" value="${mes}">
                        </td>
                        <c:if test="${filtroLocal == false}">
                        	<td class="valor_campo" colspan="1" width="84%"></td>
                        </c:if>
					</tr>
                    
                    <tr>
                        <td class="label_campo" colspan="1">Ano:</td>
                        <td class="valor_campo" colspan="1" width="8%">
                            <input id="txt_ano" type="text" name="ano" class="campo_texto" maxlength="4" value="${ano}">
                        </td>
                    </tr>
                    
                    <c:if test="${filtroLocal == true}">
                    <tr>
					    <td class="label_campo" colspan="1" width="12%">Local:</td>
					    <td class="valor_campo" colspan="1" width="8%">
					    	<input id="txt_local" type="text" name="id_local" class="campo_texto" maxlength="4" onblur="setIdRelatorio();ajustaLocal(true)" value="${idLocal == 0 ? '' : idLocal}"/>
					    	<input id="txt_local_old" type="hidden" name="id_local_old" value="${idLocal == 0 ? '' : idLocal}" />
					    </td>
					    <td class="valor_campo" colspan="4">
						    <select id="sel_local" name="local" onchange="setIdRelatorio();ajustaLocal(false);">
								<option value="0">-- Local --</option>
								<c:forEach var="local" items="${locais}">
									<option value="${local.idLocal}" ${idLocal == local.idLocal ? 'selected' : ''}>${local.nome}</option>
								</c:forEach>
							</select>
					    </td>
					</tr>
					</c:if>
					</c:if>
				</table>
				<input type="hidden" id="id_filtro_local" name="filtro_local" />
				<input type="hidden" id="id_relatorio_aux" name="relatorio_aux" />
			</form>
			
			<form id="frm_RelatoriosFluxoVeicular" action="/relatorio/rj/RelatoriosFluxoVeicular" method="get" target="_blank">
				<table class="tabela_branca" width="550">
					<tr>
						<td class="box_botoes" colspan="6" width="100%" >
							<button id="btEnvio" onclick="setField();this.submit();" >Gerar Relatório</button>
						</td>
					</tr>
					<tr>
						<td class="corpo_mensagem" colspan="6" align="left"><div id="div_mens"></div></td>
					</tr>
				</table>
				<input type="hidden" name="relatorio" />
				<input type="hidden" name="mes" />
				<input type="hidden" name="ano" />
				<input type="hidden" name="local" />
			</form>
		</td>
	</tr>
</table>

<c:if test="${filtroLocal == true}">
<c:if test="${idLocal > 0}">
<center>
<table class="tabela_grid">
			
<tr>
<th class="head_tabela" align="center" width="60">Pista</th>
<th class="head_tabela" align="center">Descrição</th>
</tr>

<c:forEach var="p" items="${pistas }">
<tr>
<td class="dado_lista_tabela_grid" align="center" width="60"><b>Pista ${p.codPistaAlternativo}</b></td>
<td class="dado_lista_tabela_grid" align="left">${p.nomePista }</td>
</tr>

</c:forEach>

</table>
</center>
</c:if>
</c:if>

<script type="text/javascript">

	function setIdRelatorio() {
		var id_relatorio_aux = document.getElementById("id_relatorio_aux");
		var filtro_local = document.getElementById("id_filtro_local");
		
		var relatorio = document.getElementById("sel_relatorio").value;
	    var relatorioFiltroLocal = relatorio.split("_");

	    id_relatorio_aux.value = relatorioFiltroLocal[0];
	    filtro_local.value = relatorioFiltroLocal[1];
	    
	}
	
	function limparLocal() {
		var filtro_local = document.getElementById("id_filtro_local");

		var txt_local = document.getElementById("txt_local");
		var sel_local = document.getElementById("sel_local");
		
		if (txt_local && sel_local)
		{
		    if (!(filtro_local.value == "true")) {
		    	document.getElementById("txt_local").value = "";
		    	document.getElementById("sel_local").selectedIndex = 0;
		    }
		}
	}

	function ajustaLocal(porCodigo) {
        var txt_local = document.getElementById("txt_local");
        var sel_local = document.getElementById("sel_local");
        var txt_local_old = document.getElementById("txt_local_old");
        
        if (porCodigo) {
            sel_local.selectedIndex = 0;

            for (var i=0;i<sel_local.options.length;i++) {
                if (sel_local.options[i].value == txt_local.value)
                    sel_local.selectedIndex = i;
            }
        }
        else {
            txt_local.value = sel_local.value == 0 ? '' : sel_local.value;
        }
        
        if (txt_local_old.value != txt_local.value) {
        	txt_local_old.value = txt_local.value;
        	document.getElementById("frm_filtro_relatorio").submit();
        }
    }
    
	function setField() {
		var frm_filtro_relatorio = document.forms["frm_filtro_relatorio"];
		var frm_RelatoriosFluxoVeicular = document.forms["frm_RelatoriosFluxoVeicular"];
		
		var relatorio_set = frm_filtro_relatorio["relatorio"];
		var mes_set = frm_filtro_relatorio["mes"];
		var ano_set = frm_filtro_relatorio["ano"];
		var local_set = frm_filtro_relatorio["local"];
		
		frm_RelatoriosFluxoVeicular["relatorio"].value = relatorio_set.value;
		frm_RelatoriosFluxoVeicular["mes"].value = mes_set.value;
		frm_RelatoriosFluxoVeicular["ano"].value = ano_set.value;
		frm_RelatoriosFluxoVeicular["local"].value = local_set.value;
		
	}
    
    document.getElementById("btEnvio").disabled = false;
</script>