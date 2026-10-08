<!-- Importações styles -->
<link rel="stylesheet"
	href="/muralha-digital/pages/registro_fato/comBoletim/botao-cadastro/css/botao-cadastro-registroDeFato.css">

<%-- MODO DE CHAMAR --%>
<%-- 
	Modelo 1, para chamar em forma de icone o botão.
	<jsp:include page="/muralha-digital/pages/registro_fato/comBoletim/botao-cadastro/botao-cadastro-registroDeFato.jsp">
	    <jsp:param name="modo" value="icone" />
	</jsp:include>
	
	Modelo 2 Simplificado.
	<%@ include file="/muralha-digital/pages/registro_fato/comBoletim/botao-cadastro/botao-cadastro-registroDeFato.jsp" %> 
 --%>
 
 <%-- PARA OBTER O ID do CADASTRO DO Registro de Fato
 
	document.addEventListener('registroDeFatomSalvoCadastro', function (e) {
		console.log("Recebido ID do modal:", e.detail.registroDeFatoId);
	
		// Continue o fluxo necessário (atualizar tela, redirecionar, etc.)
	});
  
  --%>

<!-- Botão reutilizável -->
<%
String modo = request.getParameter("modo");
if (modo == null) {
	modo = "padrao"; // ou qualquer valor default
}

if ("icone".equals(modo)) {
%>
<button class="btn btn-success" data-toggle="tooltip"
	data-placement="top" title="Cadastro de Registro de Fato"
	onclick="cadastroRegistroDeFatoComBoletim()">
	<i class="fa fa-plus-square" aria-hidden="true"></i>
</button>
<%
} else {
%>
<button class="btn btn-success" onclick="abrirModalConfirmacao()">
	Cadastro de Fatos</button>
<%
}
%>
<!-- Importações específicas deste botão -->
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/botao-cadastro/js/botao-cadastro-registroDeFato.js"></script>
<!-- Importa o modal -->
<jsp:include
	page="/muralha-digital/pages/registro_fato/comBoletim/modal/modal-registroDeFato.jsp" />