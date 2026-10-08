<html>	
	<head>
		<meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
		<title>Modal para gerenciamentos dos parâmetros em grade</title>											
	</head>
	
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
	<link rel="stylesheet" href="/muralha-digital/pages/consulta-veiculo/css/modal-passagem-veiculo.css">
	
	<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
	<script src="/muralha-digital/pages/consulta-veiculo/js/modal-passagem-veiculo.js"></script>	
	
	<body>
		<!-- Modal para gerenciar os parâmetros da visualização de imagens em grade  -->
			<div class="modal fade" id="modalParametrosGrade" tabindex="-1" aria-labelledby="labelModalParametrosGrade" aria-hidden="true">
			  <div class="modal-dialog modal-lg">
			    <div class="modal-content">
			    
			    <div class="modal-header pt-2 pb-2">
	               	<h5 class="modal-title" id="staticBackdropLabel"><strong>Gerenciamento dos parâmetros para visualização em grade</strong></h5>
       			</div>		    
			      <div class="modal-body">
			      	<div class="row">
				      <div class="mb-3 col-md-6">
				        <label style="margin-bottom: 4px"><strong>Imagem original do veículo?</strong></label>
				        <select class="form-select" name="imgOriginal" id="imgOriginal">
				          <option value="0">Não</option>
				          <option value="1" selected>Sim</option>
				        </select>
				      </div>
				      <div class="mb-3 col-md-6">
				        <label style="margin-bottom: 4px"><strong>Quantidade de imagens</strong></label>
				        <select class="form-select" name="qtdImagens" id="qtdImagens">
				          <option value="12" selected>12</option>
				          <option value="18">18</option>
				          <option value="24">24</option>
				          <option value="30">30</option>
				        </select>
				      </div>
			      	</div>
			      </div>
			      
      			<div class="modal-footer pt-2 pb-2">
					<button type="button" class="btn btn-secondary me-1" onclick="fecharModalParametros()">Fechar</button>
					<button type="button" class="btn btn-danger" onclick="cancelarModoGrade()">Cancelar Visualização Grade</button>
	  				<button type="button" class="btn btn-success" onclick="confirmarModoGrade()">Confirmar Visualização Grade</button>
	      		</div>		
			    </div>
			  </div>
			</div>
	</body>
	
	<script src="/muralha-digital/pages/consulta-veiculo/js/modal-parametros-visualizacao-grade.js"></script>
	
</html>