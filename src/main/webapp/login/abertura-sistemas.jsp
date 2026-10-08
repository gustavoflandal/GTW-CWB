<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="/WEB-INF/fmt.tld" prefix="fmt"%>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js" type="text/javascript"></script>

<%@page import="com.consilux.model.ItemIndicadoresImportacao"%>
<%@page import="com.consilux.model.IndicadoresImportacao"%>
<%@page import="com.consilux.model.beans.GrupoBean"%>
<%@page import="com.consilux.model.Grupo"%>
<%@page import="java.util.Locale"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.consilux.conf.Configuracao"%>
<%@page import="com.consilux.model.IndicadoresProcessamento"%>
<%@page import="com.consilux.infra.SessaoConstantes"%>
<%@page import="com.consilux.model.Usuario"%>
<%@page import="com.consilux.model.LogonLogoff"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.consilux.infra.SessoesAtivas"%>
<%@page import="com.consilux.conf.Versao"%>
<%@page import="java.util.Date"%>
<%@page import="com.consilux.conf.ConfiguracaoProvider"%>

<%@ include file="/login/abertura-sistemas-corpo.jsp"%>


<script type="text/javascript" src="../js/funcoes.js"></script>
<script type="text/javascript" src="../js/multiplas_imagens.js"></script>
<script type="text/javascript" src="../js/obliteracao.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI.js"></script>
<script type="text/javascript" src="../GtwClientLogger/GtwClientLogger.nocache.js"></script>
<script type="text/javascript" src="../js/pixastic.min.js"></script>
<script type="text/javascript" src="../js/ajustabrilho.js"></script>
<script type="text/javascript" src="../js/brilhoimagem.js"></script>
<script type="text/javascript" src="../js/CsxRemoteObject.js"></script>
<script type="text/javascript" src="../js/imagem_perfil.js"></script>

<br />
<br />
<br />
<%@ include file="/includes/rodape.jsp"%>
