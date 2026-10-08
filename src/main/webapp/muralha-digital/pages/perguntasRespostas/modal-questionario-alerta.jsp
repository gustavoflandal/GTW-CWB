<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
		<title>Modal para gerenciamento dos questionários dos alertas</title>
		
		<script src="https://cdn.jsdelivr.net/npm/jquery@3.6.4/dist/jquery.min.js"></script>
    	<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
		<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
		<script src="/muralha-digital/pages/perguntasRespostas/js/questionario-alerta.js"></script>
		<script src="/muralha-digital/pages/perguntasRespostas/css/questionario-alerta.css"></script>
		
		<%@ include file="/muralha-digital/pages/perguntasRespostas/iframe-tela-tratativa-alerta.jsp" %>
	</head>
	<body>		
		<div class="modal fade" id="modalQuestionarioAlerta" aria-labelledby="labelModalQuestionarioAlerta" aria-hidden="true">
		  <div class="modal-dialog modal-lg">
		    <div class="modal-content">
		      <div class="modal-body">
				<div>
					 <div style="text-align: center;" class="mb-3">
					 	<strong><span style="font-size: 25px">Questionário do alerta</span></strong>
					 </div>		
					 <div id="containerQuestionario">
					 </div>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" id="btnVerificarInfos" class="btn btn-primary" onClick="abrirTelaAlerta()">Verificar alerta</button>
			        <!-- <button type="button" class="btn btn-secondary" onClick="fecharModal()">Fechar</button> -->
			        <button type="button" id="btnCadastrar" class="btn btn-success" onClick="cadastrarRespostas()">Salvar</button>			        
		      	</div>
		      </div>		
		    </div>
		  </div>
	</body>
</html>