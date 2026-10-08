<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>
<%
	String sSemCabecalho = request.getParameter("sc");
	if (sSemCabecalho != null && "true".equals(sSemCabecalho)) {
%>
	<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_sem_menu.jsp"%>	
<%
	} else {
%>
	<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_sem_menu.jsp"%>	
<%
	}
	String sMensagem = request.getParameter("m");
	String sUrlVolta = request.getParameter("p");
%>
<br />
<br />
<br />

<div  class="row">
	<div class="col-md-4"></div>
	<div class="col-md-4">

		<div class="card border-warning mx-4 my-4">
			<h5 class="bg-warning text-center text-dark py-4" >
				<strong>A operação não pode ser concluída.</strong>
			</h5>
			
			<div class="col-md-12 text-center">
				<h4 class="py-4">
			    	<small><%=sMensagem%></small>
			  	</h4>
			  	<button id="btVoltar" class="btn btn-warning text-dark mb-2" onclick="document.location.href = '<%=sUrlVolta%>';">Voltar</button>
		    </div>
		</div>
	</div>

	<div class="col-md-4"></div>
</div>

<script type="text/javascript">
	document.getElementById("btVoltar").focus();
</script>
<%@ include file="/includes/rodape.jsp" %>
