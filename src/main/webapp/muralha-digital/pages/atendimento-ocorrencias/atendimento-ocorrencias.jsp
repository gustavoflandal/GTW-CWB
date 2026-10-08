<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ include	file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>


<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
<title>Atendimento de ocorrências</title>
	<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
	<link rel="stylesheet" href="/muralha-digital/pages/atendimento-ocorrencias/assets/css/atendimento-ocorrencias.css">
	<link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.11.2/css/all.css">
	<link rel="stylesheet" href="/muralha-digital/pages/atendimento-ocorrencias/assets/css/atendimento-ocorrencias.css">
	<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
	<!--  bibliotecas conflita com qualquer select que venha de outra tela
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.18/css/bootstrap-select.min.css" integrity="sha512-ARJR74swou2y0Q2V9k0GbzQ/5vJ2RBSoCWokg4zkfM29Fb3vZEQyv0iWBMW/yvKgyHSR/7D64pFMmU8nYmbRkg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.13.18/js/bootstrap-select.min.js" integrity="sha512-yDlE7vpGDP7o2eftkCiPZ+yuUyEcaBwoJoIhdXv71KZWugFqEphIS3PU60lEkFaz8RxaVsMpSvQxMBaKVwA5xg==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	-->
	<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/jquery@3.6.4/dist/jquery.min.js"></script>
	<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
	<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">		
</head>

<%@ include	file="/muralha-digital/pages/registro_fato/comBoletim/modal/modal-registroDeFato.jsp" %>
<body>

	<div class="container-fluid mt-4">
		<h2 class="text-center mb-4">Central de atendimento</h2>
		<div class="calledopen" style="
			display: flex;
		    flex-direction: row-reverse;
		    padding: 16px;
		    margin-right: 48px;">
		</div>
		<div class="m-1" style="width: 100%; display: flex; justify-content: flex-end; padding-right: 10px; gap: 5px;">
			<button class="btn btn-success" 
			        onclick="abrirModalConfirmacao()">
			  	Novo Atendimento
			</button>
			<button class="btn btn-primary" 
			        onclick=" obterGuarnicoesStatus()">
			  	Gerenciar Guarnições
			</button>
		</div>
		
		<!-- Abas -->
		<ul class="nav nav-tabs" id="ocorrenciasTab" role="tablist">
			<li class="nav-item" role="presentation">
				<button class="nav-link active" id="nao-atendidas-tab"
					data-bs-toggle="tab" data-bs-target="#nao-atendidas" type="button"
					role="tab"
					>Ocorrências</button>
			</li>		
			<li class="nav-item" role="presentation">
			</li>					
		</ul>				

		<!-- Conteúdo das Abas -->
		<div class="tab-content p-4 border border-top-0 bg-light"
			id="ocorrenciasTabContent" style="min-height: 300px;">

			<div class="table-responsive mt-2 tab-pane fade show active" id="nao-atendidas"
				role="tabpanel">
				<table class="table table-striped align-middle"
					id="tabela-nao-atendidas">
					<thead>
						<tr>
							<th class="text-center">Tipo ocorrencia</th>							
							<th class="text-center">Origem</th>	
							<th class="text-center">Prioridade</th>						
							<th class="text-center">Data da ocorrência</th>
							<th class="text-center">Inicio atendimento</th>
							<th class="text-center">Tempo de atendimento</th>
							<th class="text-center">Status</th>
							<th class="text-center">Ação</th>
						</tr>
					</thead>
					<tbody>
						<!-- Conteúdo preenchido dinamicamente -->
					</tbody>
				</table>
			</div>

			<!-- Aba 2: Atendidas -->
			<div class="tab-pane fade" id="atendidas" role="tabpanel">
				<table class="table table-striped align-middle"
					id="tabela-atendidas">
					<thead>
						<tr>
							<th>Atendente</th>
							<th>Solicitante</th>
							<th>Data</th>
							<th>Tipo</th>
						</tr>
					</thead>
					<tbody>
						<!-- Preenchido dinamicamente -->
					</tbody>
				</table>
			</div>
		</div>
	</div>
</body>
<script src="/muralha-digital/pages/atendimento-ocorrencias/assets/js/atendimento-ocorrencia.js"></script>
</html>


<!-- Modal atendimento -->
<div class="modal fade" id="modalAtendimento" tabindex="-1" aria-labelledby="tituloModalGuarnicao" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
     <input type="hidden" id="idAtendimentoHidden"> 
      <div class="modal-header">
        <h5 class="modal-title" id="tituloModalGuarnicao">Enviar Guarnição</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>

      <div class="modal-body">
        <form id="formGuarnicao">        
		  <div id="conteudoModalAtendimento" class="p-3">
		    <h4 class="fw-bold text-center mb-3 text-primary">Informativo</h4>
		  </div>
          <hr>
          <div class="mb-3">
            <label for="tipoGuarnicao" >Guarnição</label>
            <select class="form-select" id="selGuarnicoes" required>
              <option value="">Selecione</option>
             </select>
          </div>

          <div class="mb-3">
            <label for="membrosGuarnicao" class="form-label">Observação</label>
            <textarea class="form-control" id="obsEnvioGuarnicao" placeholder="Digite aqui as informações sobre as obrigações e direcionamentos para a guarnição."></textarea>
          </div>

        </form>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
        <button type="button" class="btn btn-primary" onclick="enviarGuarnicao()">Salvar</button>
      </div>

    </div>
  </div>
</div>


<!-- Modal Detalhes -->
<div class="modal fade" id="modalDetalhes" tabindex="-1" aria-labelledby="modalDetalhesLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="modalDetalhesLabel">Detalhes da Ocorrência</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>

      <div class="modal-body" style="height: 500px; overflow-y: auto;">
       <input type="hidden" id="idRegistroFato" />
        <!-- Nav Tabs -->
        <ul class="nav nav-tabs" id="tabDetalhesHistorico" role="tablist">
          <li class="nav-item" role="presentation">
            <button class="nav-link active" id="tab-detalhes" data-bs-toggle="tab" data-bs-target="#detalhes" type="button" role="tab" aria-controls="detalhes" aria-selected="true">Detalhes</button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="tab-historico" data-bs-toggle="tab" data-bs-target="#historico" type="button" role="tab" aria-controls="historico" aria-selected="false">Histórico</button>
          </li>
          <li class="nav-item" role="presentation">
            <button class="nav-link" id="tab-documentos" data-bs-toggle="tab" data-bs-target="#documento" type="button" role="tab" aria-controls="documento" aria-selected="false">Documentos</button>
          </li>
        </ul>

        <!-- Aba detalhes -->
        <div class="tab-content pt-3">
          <!-- Aba Detalhes -->
          <div class="tab-pane fade show active" id="detalhes" role="tabpanel" aria-labelledby="tab-detalhes">
            <div id="conteudoModalDetalhes">
              Carregando detalhes...
            </div>
          </div>

          <!-- Aba Histórico -->
          <div class="tab-pane fade" id="historico" role="tabpanel" aria-labelledby="tab-historico">
            <div id="conteudoModalHistorico">
              Carregando histórico...
            </div>
          </div>
           <!-- Aba Documentos -->
          <div class="tab-pane fade" id="documento" role="tabpanel" aria-labelledby="tab-documentos">
            <div id="conteudoModalDocumentos">
              Carregando documentos...
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
       	<button type="button" class="btn btn-warning" onclick="abrirModalBO()">Detalhes do B.O.</button>
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
      </div>
    </div>
  </div>
</div>


<!-- Modal de Anexos -->
<div class="modal fade" id="modalAnexos" tabindex="-1" aria-labelledby="modalAnexosLabel" aria-hidden="true">
  <div class="modal-dialog modal-lg modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="modalAnexosLabel">Anexar Documentos</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>
      </div>
      <div class="modal-body" id=conteudoModalAnexos>        
       
      </div>
      <div class="modal-body">
        <input type="hidden" id="idOcorrenciaAtendimento" />
		<div class="mb-3">
          <label for="observacao" class="form-label">Observações:</label>
          <textarea class="form-control" type="file" id="observacao" multiple ></textarea>
        </div>
        <div class="mb-3">
          <label for="inputArquivo" class="form-label">Escolher arquivo</label>
          <input class="form-control" type="file" id="inputArquivo" multiple accept=".pdf, .jpg, .jpeg" />
        </div>

        <button class="btn btn-success" onclick="enviarAnexos()">Anexar</button>

        <hr />
        <div id="listaArquivosAnexados">
          <!-- Lista de arquivos já anexados será carregada aqui -->
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Modal de status das guarnições  -->
<div class="modal fade" id="modalStatusGuarnicoes" tabindex="-1" role="dialog" aria-labelledby="tituloModalStatusGuarnicoes" aria-hidden="true">
  <div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="tituloModalStatusGuarnicoes">Disponibilidade das Guarnições</h5>
         <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Fechar"></button>       
      </div>
      <div class="modal-body table-responsive mt-2" id="conteudoModalStatusGuarnicoes">
        <!-- Aqui o JavaScript vai preencher a lista de status -->
      </div>
     <div class="modal-footer justify-content-start">
    	<button type="button" class="btn btn-primary" onclick="abrirTelaListagemGuarnicoes()">Gerenciar</button>
	</div>
    </div>
  </div>
</div>