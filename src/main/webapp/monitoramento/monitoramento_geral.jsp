<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="/includes/cabecalho.jsp" %>
<%@ taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	Map<String,Object> mFiltro = new HashMap<String,Object>();
	mFiltro.put("1", 1);
	List<LocalStatus> locais;
	boolean blnAcessoRemoto = false;
	
	try {
		locais = LocalStatus.buscaLocalStatusPor(mFiltro);
		blnAcessoRemoto = LocalStatus.validarAcessoRemoto((Usuario)session.getAttribute("[usuario]"));
	}
	catch (ConexaoException ex) {
		ex.printStackTrace();
		new Mensagem(response).showErro("Não foi possível conectar-se ao banco de dados.");
		return;
	}
	
%>
<%@page import="com.consilux.model.LocalStatus"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.regex.Pattern"%>
<c:set var="blnAcessoRemoto" value="<%=blnAcessoRemoto%>" />
<%@page import="com.consilux.model.LocalStatus.StatusConexao"%><br />
<br />
<br />
<script type="text/javascript" src="/js/funcoes.js"></script>
<script type="text/javascript" src="/js/popup.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<script type="text/javascript">
	var linhas_equipamentos = new Array();
	var tipoFiltroSelecionado = 0;
	function acessoRemoto(local) {
        window.open("/acesso_remoto/acesso_remoto.jsp?id_local="+local, "Acesso Remoto","width=1032, height=800");
	}
	function popmenu(evt,local) {
		
		if(!${blnAcessoRemoto}){
			var html = "<a href='#' onclick='acessoRemoto("+local+")' class='link_td'>Acesso Remoto</a>";
			html += "<br /><a href='/relatorio/frm_qualidade_imagem_tempo.jsp?id_local="+local+"' class='link_td'>Qualidade de Imagem</a>";
			html += "<br /><a href='/relatorio/frm_fluxo_classe_tempo.jsp?id_local="+local+"' class='link_td'>Fluxo x Classe</a>";
			html += "<br /><a href='/relatorio/frm_fluxo_tempo.jsp?id_local="+local+"' class='link_td'>Fluxo x Período</a>";
			html += "<br /><a href='/relatorio/frm_fluxo_velocidade_grade.jsp?id_local="+local+"' class='link_td'>Fluxo x Velocidade x Período</a>";
			html += "<br /><a href='/relatorio/frm_fluxo_velocidade.jsp?id_local="+local+"' class='link_td'>Fluxo x Velocidade</a>";
			html += "<br /><a href='/relatorio/frm_ocupacao_tempo.jsp?id_local="+local+"' class='link_td'>Ocupação x Período</a>";
			popup(evt, html);
		}
	
	}
	
	function aplicFiltroStatusConexao(tipo) {
		tipoFiltroSelecionado = tipo;
		switch(tipo) {
	        case 1:
                mostraDIVConexaoFiltro(<%=LocalStatus.StatusConexao.ON_LINE.getId()%>);  
                escondeDIVConexaoFiltro(<%=LocalStatus.StatusConexao.OFF_LINE.getId()%>);  
                escondeDIVConexaoFiltro(<%=LocalStatus.StatusConexao.NAO_VERIFICADO.getId()%>);  
	            break;
	        case 2: 
                escondeDIVConexaoFiltro(<%=LocalStatus.StatusConexao.ON_LINE.getId()%>);  
                mostraDIVConexaoFiltro(<%=LocalStatus.StatusConexao.OFF_LINE.getId()%>);  
                escondeDIVConexaoFiltro(<%=LocalStatus.StatusConexao.NAO_VERIFICADO.getId()%>);  
	            break;
	        default: 
                mostraDIVConexaoFiltro(<%=LocalStatus.StatusConexao.ON_LINE.getId()%>);  
	            mostraDIVConexaoFiltro(<%=LocalStatus.StatusConexao.OFF_LINE.getId()%>);  
	            mostraDIVConexaoFiltro(<%=LocalStatus.StatusConexao.NAO_VERIFICADO.getId()%>);  
		}
		contaLinhas();
	}

	function contaLinhas() {
		var contaLinha = 0;
        for (var ind in linhas_equipamentos) {
            var linha = linhas_equipamentos[ind];
            if (linha.style.display != "none")
            	contaLinha++;
        }
        if (document.getElementById('div_conta_locais'))
			document.getElementById('div_conta_locais').innerHTML =	'Mostrando '+contaLinha+' de '+linhas_equipamentos.length+' disponíveis.';
	}

	function mostraDIVConexaoFiltro(status) {
        for (var ind in linhas_equipamentos) {
            var linha = linhas_equipamentos[ind];
            if (linha.statusConexao == status)
            	linha.style.display = "";
        }
	}

	function escondeDIVConexaoFiltro(status) {
        for (var ind in linhas_equipamentos) {
            var linha = linhas_equipamentos[ind];
            if (linha.statusConexao == status)
            	linha.style.display = "none";
        }
    }

	function ajustaStatus(idEquipamento, idStatusConexao, idStatusEnergia, idStatusDIV) {
        linha_conexao = document.getElementById("tr_"+idEquipamento);
		div_conexao = document.getElementById("div_status_conexao_"+idEquipamento);
        div_energia = document.getElementById("div_status_energia_"+idEquipamento);
        div_DIV = document.getElementById("div_status_DIV_"+idEquipamento);

		if (div_conexao) {
			linha_conexao.statusConexao = idStatusConexao; 
		    switch(parseInt(idStatusConexao)) {
			    case <%=LocalStatus.StatusConexao.ON_LINE.getId()%>: 
			    	div_conexao.innerHTML = '<font color="#00AA00">On-Line</font>';
			        break;
                case <%=LocalStatus.StatusConexao.OFF_LINE.getId()%>: 
                	div_conexao.innerHTML = '<font color="#FF0000">Off-Line</font>';
                    break;
	            default: 
	            	div_conexao.innerHTML = 'Não Verificado';
		    }
		};
        if (div_energia) {
            switch(parseInt(idStatusEnergia)) {
                case <%=LocalStatus.StatusEnergia.COM_ENERGIA.getId()%>: 
                	div_energia.innerHTML = 'Com Energia';
                    break;
                case <%=LocalStatus.StatusEnergia.SEM_ENERGIA.getId()%>: 
                	div_energia.innerHTML = '<font color="#999900">Sem Energia</font>';
                    break;
                default: 
                	div_energia.innerHTML = 'Não Verificado';
            }
        };
        if (div_DIV) {
            switch(parseInt(idStatusDIV)) {
                case <%=LocalStatus.StatusDIV.OPERANTE.getId()%>: 
                	div_DIV.innerHTML = 'Operante';
                    break;
                case <%=LocalStatus.StatusDIV.NAO_OPERANTE.getId()%>: 
                	div_DIV.innerHTML = '<font color="#FF0000">Não Operante</font>';
                    break;
                default: 
                	div_DIV.innerHTML = 'Não Verificado';
            }
        };
        aplicFiltroStatusConexao(tipoFiltroSelecionado);
	} 
	function atualizaData(idEquipamento) {
        div = document.getElementById("div_data_atualizacao_"+idEquipamento);
        if (div) {
            div.innerHTML = dateFormat('dd/mm/yyyy HH:MM:ss');
        }
	}
	
	function aguardaNotificacoes() {
        $.get('/ajax/AguardaStatusEquipamento', new Array(),  
	        function(xml) {
				try{
		            idEquipamento = $("ID_EQUIPAMENTO",xml).text();

		            if( idEquipamento != "" ){

		            	idStatusConexao = $("STATUS_CONEXAO",xml).text();
			            idStatusEnergia = $("STATUS_ENERGIA",xml).text();
			            idStatusDIV = $("STATUS_DIV",xml).text();

			            ajustaStatus(idEquipamento, idStatusConexao, idStatusEnergia, idStatusDIV);
			            atualizaData(idEquipamento);
			            
					}
				}
				finally{
		            aguardaNotificacoes();
				}
	        }
       	);
	}
	
	aguardaNotificacoes();
	
</script>
<table class="tabela_branca" width="100%">
	<tr>
		<td align="center">
			<table class="tabela_branca" width="770">
				<tr>
					<td width="33%" class="dado_lista_tabela_claro"><input type="radio" id="cbTodos" name="filtroStatus" onclick="aplicFiltroStatusConexao(0);" checked />Todos</td>
					<td width="34%" class="dado_lista_tabela_claro"><input type="radio" id="cbOnline" name="filtroStatus" onclick="aplicFiltroStatusConexao(1);" />On-Line</td>
					<td width="33%" class="dado_lista_tabela_claro"><input type="radio" id="cbOffline" name="filtroStatus" onclick="aplicFiltroStatusConexao(2);" />Off-Line</td>
				</tr>
			</table>
			<table class="tabela_lista" width="770">
				<tr>
					<th class="head_tabela" width="5%">Local</th>
					<th class="head_tabela" width="35%">Nome</th>
					<th class="head_tabela" width="15%">Status Conexão</th>
                    <th class="head_tabela" width="15%">Status Energia</th>
                    <th class="head_tabela" width="15%">Status DIV</th>
                    <th class="head_tabela" width="15%">Data Atualização</th>
				</tr>
				<c:forEach var="local" varStatus="linhaInfo" items="<%=locais%>">
					<tr id="tr_${local.idEquipamento}">
						<c:set var="css_td" value="${linhaInfo.count % 2 == 0 ? 'dado_lista_tabela_escuro' : 'dado_lista_tabela_claro'}" />
						<td class="${css_td}" align="center"><a onclick="popmenu(event,${local.idLocal})" class="link_td">${local.idLocal}</a></td>
						<td class="${css_td}" align="left">${local.nome}</td>
						<td class="${css_td}" align="center">
                            <div id="div_status_conexao_${local.idEquipamento}">&nbsp;</div>
                        </td>
						<td class="${css_td}" align="center">
                            <div id="div_status_energia_${local.idEquipamento}">&nbsp;</div>
                        </td>
                        <td class="${css_td}" align="center">
                            <div id="div_status_DIV_${local.idEquipamento}">&nbsp;</div>
                        </td>
						<td class="${css_td}" align="center">
                            <div id="div_data_atualizacao_${local.idEquipamento}">
                                <fmt:formatDate value="${local.dataAtualizacao}" type="both" pattern="dd/MM/yyyy HH:mm:ss" />
                            </div>
                        <script type="text/javascript">
	                        ajustaStatus(${local.idEquipamento}, ${local.statusConexao.id}, ${local.statusEnergia.id}, ${local.statusDIV.id})
                            linhas_equipamentos.push(document.getElementById("tr_${local.idEquipamento}"));
                        </script>
					</tr>
				</c:forEach>
			</table>
			<br>
			<table class="tabela_branca" width="770">
				<tr>
					<td class="dado_lista_tabela_claro" align="left" width="100%" colspan="3"><div id="div_conta_locais"></div></td>
				</tr>
			</table>			
			<br />
		</td>
	</tr>
</table>
<script type="text/javascript">
	contaLinhas();
</script>
<br />
<br />
<%@ include file="/includes/rodape.jsp" %>
