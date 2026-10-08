<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" session="false"%>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_sem_menu.jsp"%>
<%
	String sMensagem = request.getParameter("m");
	String sUrlVolta = request.getParameter("p");
%>
<br />
<br />
<br />

<div  class="row">
	<div class="col-md-4"></div>
	<div class="col-md-4">

		<!-- Material form login -->
		<div class="card border-success mx-4 my-4">
			<h5 class="bg-success text-center text-white py-4" >
				<strong>Operação concluída com sucesso.</strong>
			</h5>
			
			<div class="col-md-12 text-center">
				<h4 class="py-4">
			    	<small><%=sMensagem%></small>
			  	</h4>
			  	<button id="btOk" class="btn bg-success mb-2 text-white" onclick="document.location.href = '<%=sUrlVolta%>';"><strong>OK</strong></button>
		    </div>
		</div>
		<!-- Material form login -->
	</div>

	<div class="col-md-4"></div>
</div>

<script type="text/javascript">
	document.getElementById("btOk").focus();
</script>
<%@ include file="/includes/rodape.jsp" %>
