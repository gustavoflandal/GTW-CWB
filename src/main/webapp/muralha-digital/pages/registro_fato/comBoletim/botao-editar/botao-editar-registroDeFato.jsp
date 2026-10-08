<!-- Importações styles -->
<link rel="stylesheet"
	href="/muralha-digital/pages/boletim/botao-visualizar/css/botao-editar-boletim.css">
<%-- MODO DE CHAMAR --%>
<%-- 
	Modelo, para chamar em forma de icone o botão passando o ID.
	<jsp:include page="/muralha-digital/pages/boletim/botao-visualizar/botao-visualizar-boletim.jsp">
	    <jsp:param name="id" value="<%= boletimId %>"/>
	</jsp:include>
 --%>

<!-- Botão reutilizável -->
<%
String id = request.getParameter("id");
if (id == null || id.trim().isEmpty()) {
	id = "";
}
%>
<button class="btn btn-primary" data-toggle="tooltip"
	data-placement="top" title="Detalhar Boletim"
	onclick="abrirVisualizarBoletim('<%=id%>')">
	<i class="fas fa-eye" aria-hidden="true"></i>
</button>
<!-- Importações específicas deste botão -->
<script
	src="/muralha-digital/pages/registro_fato/comBoletim/botao-editar/js/botao-editar-registroDeFato.js"></script>
<!-- Importa o modal -->
<jsp:include
	page="/muralha-digital/pages/registro_fato/comBoletim/modal/modal-registroDeFato.jsp" />