<%@page import="java.text.NumberFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.List"%>
<%@page import="java.util.regex.Pattern"%>
<%@page import="com.consilux.model.MensagemJS"%>
<%@page import="com.consilux.model.descarga.beans.DescargaBean"%>
<%@page import="com.consilux.model.descarga.Descarga"%>
<%@ include file="/includes/cabecalho_vazio.jsp" %>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt" %>
<%
	String sIdDescarga = request.getParameter("id_descarga") != null ? request.getParameter("id_descarga").trim() : null;
	sIdDescarga = sIdDescarga != null && sIdDescarga.length() == 0 ? null : sIdDescarga;

	if (sIdDescarga == null || !Pattern.matches("[1-9][0-9]{0,7}", sIdDescarga)) {
        new MensagemJS(response).showErro("Identificador de descarga enviado inválido!");
        return;
    }
	
    DescargaBean descarga = Descarga.buscarDescargaPorId(Integer.parseInt(sIdDescarga));
    String nomeISO = "[NÃO ENCONTRADO]";
	String tamanhoISO = "";
    
	File isoDir = new File(ConfiguracaoProvider.getInstance().getConfiguracaoDescarga().getDiretorioSaida());
	if (isoDir.exists()) {
		File arqIso = new File(isoDir.getPath() + File.separatorChar + Descarga.getIsoFileName(descarga.getId(), descarga.getDiaInicio(), descarga.getDiaFim()));
		if (arqIso.exists()) {
			nomeISO = arqIso.getName();
			tamanhoISO = NumberFormat.getIntegerInstance().format(arqIso.length()) + " bytes";
		}
	}
  
%>
<c:set var="descarga" value="<%=descarga%>" />
<c:set var="nomeISO" value="<%=nomeISO%>" />
<c:set var="tamanhoISO" value="<%=tamanhoISO%>" />
<table class="tabela_branca" width="600" style="height: 100%" align="center">
	<tr>
		<td align="center" valign="top">
            <table class="tabela_branca" width="100%">
                <tr>
                    <th class="head_tabela" width="100%" colspan="7">INFORMAÇÕES DA DESCARGA</th>
                </tr>
                <tr>
                    <td class="label_campo" width="10%">Descarga</td>
                    <td class="label_campo" width="25%">Data Criação</td>
                    <td class="label_campo" width="25%">Dia Início Imagens</td>
                    <td class="label_campo" width="40%" colspan="3">Dia Fim Imagens</td>
                </tr>
                <tr>
                    <td class="visualiza_campo">${descarga.id}</td>
                    <td class="visualiza_campo"><fmt:formatDate value="${descarga.dataCriacao}"  pattern="dd/MM/yyyy HH:mm:ss"/></td>
                    <td class="visualiza_campo"><fmt:formatDate value="${descarga.diaInicio}" pattern="dd/MM/yyyy"/></td>
                    <td class="visualiza_campo" colspan="3"><fmt:formatDate value="${descarga.diaFim}" pattern="dd/MM/yyyy"/></td>
                </tr>
                <tr>
                    <td class="label_campo">Veículos</td>
                    <td class="label_campo">Infrações</td>
                    <td class="label_campo">Imagens</td>
                    <td class="label_campo" colspan="3"></td>                     
                </tr>
                <tr>
                    <td class="visualiza_campo">${descarga.totalVeiculos}</td>
                    <td class="visualiza_campo">${descarga.totalInfracoes}</td>
                    <td class="visualiza_campo">${descarga.totalImagens}</td>
                    <td class="visualiza_campo" colspan="3"></td>
                </tr>
                <tr>
                    <td class="label_campo" colspan="3">Arquivo ISO</td>
                    <td class="label_campo" colspan="3">Tamanho do Arquivo</td>                     
                </tr>
                <tr>
                    <td class="visualiza_campo" colspan="3">${nomeISO}</td>
                    <td class="visualiza_campo" colspan="3">${tamanhoISO}</td>
                </tr>
				<tr>
					<td class="box_botoes" colspan="7" width="100%">
						<button onclick="window.location='/descarga/DownloadDescarga?id_descarga=${descarga.id}'">Download ISO</button>
						<button onclick="window.location='/descarga/EtiquetaDescarga?id_descarga=${descarga.id}'">Etiqueta</button>
					</td>
				</tr>
                <tr>
                    <td colspan="7">&nbsp;</td>
                </tr>
                <tr>
                    <th class="head_tabela" width="100%" colspan="7">CONFIRMAÇÃO</th>
                </tr>
                <c:if test="${descarga.dataConfirmacao != null}">
	                <tr>
	                    <td class="label_campo" colspan="2">Data de confirmação</td>
	                    <td class="label_campo" colspan="5">Usuário que confirmou</td>
	                </tr>
	                <tr>
	                    <td class="visualiza_campo" colspan="2"><fmt:formatDate value="${descarga.dataConfirmacao}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
	                    <td class="visualiza_campo" colspan="5">${descarga.usuarioConfirmacao}</td>
	                </tr>
                </c:if>
                <c:if test="${descarga.dataConfirmacao == null}">
	                <tr>
	                    <td class="label_campo" colspan="7">Enviar arquivo de confirmação:</td>
	                </tr>
	                <tr>
						<td class="box_botoes" colspan="7" width="100%">
						    <form action="/descarga/ConfirmarDescarga?id_descarga=${descarga.id}" method="POST" enctype="multipart/form-data">
						        <input type="file" name="file" size="40">
						        <br><br>
						        <button stype="submit">Enviar</button>
						    </form>
						</td>
					</tr>                
				</c:if>
         	</table>
		</td>
	</tr>
</table>
<%@ include file="/includes/rodape.jsp" %>