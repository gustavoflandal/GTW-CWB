<%@page import="com.consilux.model.documento.Documento"%>
<%@page import="com.consilux.model.documento.Documento.Classficador"%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="com.consilux.model.medicao.ProcessoMedicao"%>
<%@page import="com.consilux.infra.ExpValida"%>
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<script type="text/javascript" src="/js/documentos.js"></script>
<script type="text/javascript" src="/js/jquery.js"></script>
<%
	String sIdProcessoMedicao = request.getParameter("id_processo_medicao") != null ? request.getParameter("id_processo_medicao").trim() : null;
	sIdProcessoMedicao = sIdProcessoMedicao != null && sIdProcessoMedicao.length() == 0 ? null : sIdProcessoMedicao;

	if (sIdProcessoMedicao == null || !ExpValida.NATURAL.validar(sIdProcessoMedicao)) {
        new MensagemJS(response).showErro("Identificador do processo enviado inválido!");
        return;
    }
	
    ProcessoMedicao processoMedicao = ProcessoMedicao.buscarProcessoMedicaoPorId(Integer.valueOf(sIdProcessoMedicao));
    Usuario usuarioResponsavel = Usuario.buscaUsuarioPorIdUsuario(processoMedicao.getIdUsuarioResponsavel());
    
%>
<c:set var="processoMedicao" value="<%=processoMedicao%>" />
<c:set var="usuarioResponsavel" value="<%=usuarioResponsavel%>" />
<c:set var="classificador_doc_protocolo_exportacao" value="<%=Classficador.PROTOCOLO_CD_COMPROVACAO.getId()%>" />
<c:set var="classificador_doc_planilha_complemento" value="<%=Classficador.PLANILHA_CD_COMPROVACAO.getId()%>" />
<c:set var="classificador_doc_protocolo_complemento" value="<%=Classficador.PROTOCOLO_CD_COMPLEMENTO_COMPROVACAO.getId()%>" />
<c:set var="classificador_doc_planilha_quantitativos" value="<%=Classficador.PLANILHA_QUANTITATIVOS.getId()%>" />
<script type="text/javascript">
	function atualizaProcessoMedicao() {
		$.ajax({url: '/ajax/AtualizaProcessoMedicao?id_processo_medicao='+${processoMedicao.id}, success: function(xml) {
			window.location.reload();
        }, async: false});
	}
</script>
<table class="tabela_branca" width="700" style="height: 100%" align="center">
	<tr>
		<td align="center" valign="top">
            <table class="tabela_branca" width="100%">
                <tr>
                    <th class="head_tabela" colspan="7">INFORMAÇÕES DO PROCESSO DE MEDIÇÃO</th>
                </tr>
                <tr>
                    <td class="label_campo" width="10%">Nº do Processo</td>
                    <td class="label_campo" width="35%">Data Criação</td>
                    <td class="label_campo" width="15%">Mês/Ano Base</td>
                    <td class="label_campo" width="40%" colspan="3">Estágio Atual</td>
                </tr>
                <tr>
                    <td class="visualiza_campo">${processoMedicao.id}</td>
                    <td class="visualiza_campo"><fmt:formatDate value="${processoMedicao.dataCriacao}"  pattern="dd/MM/yyyy HH:mm:ss"/></td>
                    <td class="visualiza_campo"><fmt:formatNumber value="${processoMedicao.mes}" pattern="00"/>/<fmt:formatNumber value="${processoMedicao.ano}" pattern="0000"/></td>
                    <td class="visualiza_campo" colspan="3">${processoMedicao.estagioProcesso}</td>
                </tr>
                <tr>
                    <th class="head_tabela" colspan="7">ETAPAS DO PROCESSO</th>
                </tr>
                <tr>
                    <td class="label_campo" colspan="7">Responsável</td>
                </tr>
                <tr>
                    <td class="visualiza_campo" colspan="7">${usuarioResponsavel.nome}</td>
                </tr>
				<tr>
					<td class="box_botoes" colspan="7" width="100%">
						<button onclick="window.location='/medicao/ExportaComprovacaoImagens?id_processo_medicao=${processoMedicao.id}'">CD Comprovação Imagens</button>
						<button onclick="window.location='/medicao/ExportaComprovacaoImagens?id_processo_medicao=${processoMedicao.id}&complementar=1'">CD Complementar Imagens</button>
						<button onclick="window.location='/medicao/CartaImagensComprovacao?id_processo_medicao=${processoMedicao.id}'">Pedido Análise Imagens</button>
					</td>
				</tr>
                <tr>
                    <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                    <td class="label_campo" colspan="7">>> IMAGENS PARA COMPROVAÇÃO:</td>
                </tr>
                <tr>
                    <td class="label_campo" colspan="2">Data da Entrega</td>
                    <td class="label_campo" colspan="5">Data do Protocolo</td>
                </tr>
                <tr>
                    <td class="visualiza_campo" colspan="2"><fmt:formatDate value="${processoMedicao.dataExportacao}"  pattern="dd/MM/yyyy HH:mm:ss"/></td>
                    <td class="visualiza_campo" colspan="1"><fmt:formatDate value="${processoMedicao.dataProtocoloExportacao}"  pattern="dd/MM/yyyy HH:mm:ss"/></td>
                    <td class="visualiza_campo" colspan="4" style="text-align: right;">
                    	<c:if test="${processoMedicao.dataProtocoloExportacao != null}">
                    		<button onclick="listaDoc(${classificador_doc_protocolo_exportacao},${processoMedicao.id},window.atualizaProcessoMedicao)">Visualizar Protocolo</button>
                    	</c:if>
                    	<button onclick="anexarDoc(${classificador_doc_protocolo_exportacao},${processoMedicao.id},window.atualizaProcessoMedicao)">Anexar Protocolo</button>
					</td>
                </tr>
                <tr>
                    <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                    <td class="label_campo" colspan="7">>> PLANILHA DE RETORNO DAS IMAGENS PARA COMPROVAÇÃO:</td>
                </tr>
                <tr>
                    <td class="label_campo" colspan="2">Data do Recebimento</td>
                    <td class="label_campo" colspan="5">&nbsp;</td>
                </tr>
                <tr>
                    <td class="visualiza_campo" colspan="2"><fmt:formatDate value="${processoMedicao.dataRetornoExportacao}"  pattern="dd/MM/yyyy HH:mm:ss"/></td>
                    <td class="visualiza_campo" colspan="1">&nbsp;</td>
                    <td class="visualiza_campo" colspan="4" style="text-align: right;">
                    	<c:if test="${processoMedicao.dataRetornoExportacao != null}">
                    		<button onclick="listaDoc(${classificador_doc_planilha_complemento},${processoMedicao.id},window.atualizaProcessoMedicao)">Visualizar Planilha</button>
                    	</c:if>
                    	<button onclick="window.location='/documentos/AnexarDoc?classificador=${classificador_doc_planilha_complemento}&identificador=${processoMedicao.id}'">Anexar Planilha</button>
                    </td>
                </tr>
                <tr>
                    <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                    <td class="label_campo" colspan="7">>> IMAGENS PARA COMPLEMENTO DA COMPROVAÇÃO:</td>
                </tr>
                <tr>
                    <td class="label_campo" colspan="2">Data da Entrega</td>
                    <td class="label_campo" colspan="5">Data do Protocolo</td>
                </tr>
                <tr>
                    <td class="visualiza_campo" colspan="2"><fmt:formatDate value="${processoMedicao.dataComplemento}"  pattern="dd/MM/yyyy HH:mm:ss"/></td>
                    <td class="visualiza_campo" colspan="1"><fmt:formatDate value="${processoMedicao.dataProtocoloComplemento}"  pattern="dd/MM/yyyy HH:mm:ss"/></td>
                    <td class="visualiza_campo" colspan="4" style="text-align: right;">
                    	<c:if test="${processoMedicao.dataProtocoloComplemento != null}">
                    		<button onclick="listaDoc(${classificador_doc_protocolo_complemento},${processoMedicao.id},window.atualizaProcessoMedicao)">Visualizar Protocolo</button>
                    	</c:if>
	                    <button onclick="window.location='/documentos/AnexarDoc?classificador=${classificador_doc_protocolo_complemento}&identificador=${processoMedicao.id}'">Anexar Protocolo</button>
                   	</td>
                </tr>
                <tr>
                    <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                    <td class="label_campo" colspan="7">>> PLANILHA FINAL DE QUANTITATIVOS:</td>
                </tr>
                <tr>
                    <td class="label_campo" colspan="2">Data do Recebimento</td>
                    <td class="label_campo" colspan="5">&nbsp;</td>
                </tr>
                <tr>
                    <td class="visualiza_campo" colspan="2"><fmt:formatDate value="${processoMedicao.dataPlanilhaQuantitativos}"  pattern="dd/MM/yyyy HH:mm:ss"/></td>
                    <td class="visualiza_campo" colspan="1">&nbsp;</td>
                    <td class="visualiza_campo" colspan="4" style="text-align: right;">
                    	<c:if test="${processoMedicao.dataPlanilhaQuantitativos != null}">
                    		<button onclick="listaDoc(${classificador_doc_planilha_quantitativos},${processoMedicao.id},window.atualizaProcessoMedicao)">Visualizar Planilha</button>
                    	</c:if>
                    	<button onclick="window.location='/documentos/AnexarDoc?classificador=${classificador_doc_planilha_quantitativos}&identificador=${processoMedicao.id}'">Anexar Planilha</button>
                    </td>
                </tr>
                <tr>
                    <td class="visualiza_campo" colspan="2"><fmt:formatDate value="${processoMedicao.dataPlanilhaQuantitativos}"  pattern="dd/MM/yyyy HH:mm:ss"/></td>
                    <td class="visualiza_campo" colspan="5">&nbsp;</td>
                </tr>
         	</table>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>