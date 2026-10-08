<!-- Importações styles -->
<link rel="stylesheet"
	href="/muralha-digital/pages/boletim/botao-cadastro/css/botao-cadastro-boletim.css">

<%-- MODO DE CHAMAR --%>
<%-- 
	Modelo 1, para chamar em forma de icone o botão.
	<jsp:include page="/muralha-digital/pages/boletim/botao-cadastro/botao-cadastro-boletim.jsp">
	    <jsp:param name="modo" value="icone" />
	</jsp:include>
	
	Modelo 2 Simplificado.
	<%@ include file="/muralha-digital/pages/boletim/botao-cadastro/botao-cadastro-boletim.jsp" %> 
 --%>
 
 <%-- PARA OBTER O ID do CADASTRO DO BOLETIM
 
	document.addEventListener('boletimSalvoCadastro', function (e) {
		console.log("Recebido ID do modal:", e.detail.boletimId);
	
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
	data-placement="top" title="Cadastro de Boletim"
	onclick="abrirCadastroBoletim()">
	<i class="fa fa-plus-square" aria-hidden="true"></i>
</button>
<%
} else {
%>
<button class="btn btn-success" onclick="abrirCadastroBoletim()">
	Cadastro de Boletim</button>
<%
}
%>
<!-- Importações específicas deste botão -->
<script
	src="/muralha-digital/pages/boletim/botao-cadastro/js/botao-cadastro-boletim.js"></script>
<!-- Importa o modal -->
<jsp:include
	page="/muralha-digital/pages/boletim/modal/modal-boletim.jsp" />