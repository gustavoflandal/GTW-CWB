 <%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>

<%@page import="java.util.List"%>
<%@page import="java.util.ArrayList"%>
<%@page import="muralha.digital.consulta.TipoRegistro"%>
<%@page import="muralha.digital.consulta.TiposRegistros"%>

<%
	List<TipoRegistro> tiposRegistro = TiposRegistros.ObterListaTiposRegistro();
%>

<c:set var="tiposRegistro" value="<%=tiposRegistro%>" />

<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>

<html lang="en">
	

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>GTW - Consulta de Alertas e Ocorrências</title>
		<meta name="viewport" content="width=device-width, initial-scale=1">
		
		<link rel="stylesheet" href="/muralha-digital/pages/consulta-alerta-ocorrencia/css/consulta.css">
		<link rel="stylesheet" href="/muralha-digital/assets/css/pagina-carregando.css">
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />

		<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
		<script src="/muralha-digital/assets/js/componente-data-hora.js"></script>
		<script src="/muralha-digital/pages/consulta-alerta-ocorrencia/js/consulta.js"></script>
		<script src="/muralha-digital/assets/js/carregar-combo-equipamentos.js"></script>
		
	</head>
	<%@ include	file="/muralha-digital/pages/registro_fato/comBoletim/modal/modal-registroDeFato.jsp" %>		
	
	<body class="homepage">

		<%@ include file="/muralha-digital/utils/modal-info-alert.jsp" %>
		<%@ include file="/muralha-digital/pages/monitorado/modal-editar-monitorado.jsp" %> 
		<div class="container">
			<div class="row gy-3">
				<div class="col-sm-12">
      				<div class="mb-3">
						<h2 id="tituloTelaConsulta" class="text-center"><strong>Consulta de Alertas/Ocorrências</strong></h2>
					</div>
				</div>
			</div>
		</div>
		
		<div id="divFiltrosConsulta" class="container">
			<div class="row gy-3">
				<div class="col-sm-2">
      				<div class="mb-3">
		           		<div class="form-group">
			                <label for="selTipo" class='form-label'>Tipo:</label>
			
			                <select class="form-select" id="selTipo" name="tipos" onchange="obterStatusAlertaOcorrencia();">
			                	<c:forEach var="tipo" items="${tiposRegistro}">
			                        <option value="${tipo.id}">${tipo.descricao}</option>
			                    </c:forEach>
			                </select>
			                
			        	</div>
		        	</div>
	            </div>
                <div class="col-sm-4">
      				<div class="mb-3">
	                	<div class="form-group">
			                <label for="selStatus" class='form-label'>Status:</label>
			                <select class="form-select" id="selStatus" name="status">
			                    <option value="0" selected="selected">--Selecione o Status--</option>
			                </select>
		                </div>
					</div>
				</div>
	            <div class="col-sm-2">
      				<div class="mb-3">
			    		<div class="form-group">
		                	<label for="placaConsulta" class='form-label'>Placa:</label>
							<input name="placa" id="placaConsulta" placeholder="Ex.: AAA1111" type="text" maxlength="7" class="form-control text-uppercase placa">
						</div>
					</div>
                </div>
		        <div class="col-sm-2">
      				<div class="mb-3">
			            <div class="form-group">
			            	<label for="dataInicio" class='form-label'>Data Inicio:</label>
							<div class='input-group' id='dataInicio' data-td-target-input='nearest' data-td-target-toggle='nearest'>
							  	<input id='dataInicioInput' type='text' class='form-control' data-td-target='#dataInicio' readonly/>
							   	<span class='input-group-text' data-td-target='#dataInicio' data-td-toggle='datetimepicker'>
							     	<span class='fas fa-calendar'></span>
							   	</span>
							</div>
			            </div>
		            </div>
		        </div>
		        <div class="col-sm-2">
      				<div class="mb-3">
			            <div class="form-group">
			            	<label for="dataFim" class='form-label'>Data Fim:</label>
							<div class='input-group log-event' id='dataFim' data-td-target-input='nearest' data-td-target-toggle='nearest'>
							  	<input id='dataFimInput' type='text' class='form-control' data-td-target='#dataFim' readonly/>
							   	<span class='input-group-text' data-td-target='#dataFim' data-td-toggle='datetimepicker'>
							     	<span class='fas fa-calendar'></span>
							   	</span>
							</div>
			            </div>
		            </div>
		        </div>
			</div>
	           	
		    <div class="row gy-3">
		    	<div class="col-sm-7">
      				<div class="mb-3">
	           			<div class="form-group">
			                <label for="selEquipamento" class='form-label'>Equipamento:</label>
			                <select class="selectpicker form-control border" data-live-search="true" id="selEquipamento" name="equipamento" data-style="btn-white" data-size="20" data-none-selected-text="--Todos os equipamentos--">
			                    <option value="0" selected="selected">--Todos os Equipamentos--</option>
			                </select>
		                </div>
	                </div>
                </div>
           		<div class="col-sm-5">
      				<div class="mb-3">
	           			<div class="form-group">
			                <label for="selTipoAlertaOcorrencia" class='form-label'>Tipo Alerta/Ocorrência:</label>
			                <select class="form-select" id="selTipoAlertaOcorrencia" name="tiposAlertaOcorrencia">
			                    <option value="0" selected="selected">--Selecione o Tipo Alerta/Ocorrência--</option>
			                </select>
		                </div>
	                </div>
                </div>
		    </div>
		    
			<div class="row" id="divChkSupervisionado" style="display: none;">
			  <div class="col-12 mb-2">
				  <div class="border rounded p-3 bg-light">
					  <h6 class="mb-0"><i class="bi bi-funnel"></i> Filtros de Supervisão</h6>
					  <hr class="mt-0 mb-1"/>
				      <div class="row g-3">
				        <div class="col-md-4">
				          <div class="form-check">
				            <input class="form-check-input" type="checkbox" value="1" id="chkSupervisionado">
				            <label class="form-check-label" for="chkSupervisionado">
				              Apenas supervisionado
				            </label>
				          </div>
				        </div>
				        <div class="col-md-4">
				          <div class="form-check">
				            <input class="form-check-input" type="checkbox" value="1" id="chkAssinadosPendentes">
						<label class="form-check-label" for="chkAssinadosPendentes">
						  Assinados pendentes de supervisão
						</label>
				          </div>
				        </div>
				        <div class="col-md-4">
				          <div class="form-check">
				            <input class="form-check-input" type="checkbox" value="1" id="chkPrivado">
				            <label class="form-check-label" for="chkPrivado">
				              Apenas privado
				            </label>
				          </div>
				        </div>
			      	</div>
				  </div>
			  </div>
			</div>
           	
			<div class="row gy-3">
				<div class="col-sm-6">
      				<div class="mb-3">
						<div class="d-grid gap-2">
							<button id="pesquisar" class="btn btn-primary" type="button" onclick="executaPesquisa(true)">PESQUISAR</button>
						</div>
					</div>
				</div>
				<div class="col-sm-6">
      				<div class="mb-3">
						<div class="d-grid gap-2">
							<button id="limparFiltros" class="btn btn-warning" type="button" onclick="limparFiltrosAlerta()">LIMPAR FILTROS</button>
						</div>
					</div>
				</div>
           	</div>
        </div>
           
		<div class="container">
			<div class="row">
				<div class="col-sm-12">
      				<div class="mb-3">
				  		<h5 id="tituloListaResultado"><small>Lista de alertas/ocorrências:</small></h5>
		
						<div class="row">
							<div class="col-sm-11">
					            <div class="form-group input-group">
									<span class="input-group-text">
										<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-search" viewBox="0 0 16 16">
					  						<path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001c.03.04.062.078.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1.007 1.007 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0z"></path>
										</svg>
									</span>
									<input name="consulta" id="txt_consulta" placeholder="Consultar" type="text" class="form-control">
								</div>
							</div>
							<div class="col-sm-1">
								<div class="form-group input-group d-grid d-md-flex justify-content-md-end h-100 btn-relatorios">
									<button id="gerarPDF" type="button" class="btn btn-outline-danger" title="Exportar PDF" onclick="GerarRelatorio('pdf')">
						                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-pdf-fill" viewBox="0 0 16 16">
										  	<path d="M5.523 12.424c.14-.082.293-.162.459-.238a7.878 7.878 0 0 1-.45.606c-.28.337-.498.516-.635.572a.266.266 0 0 1-.035.012.282.282 0 0 1-.026-.044c-.056-.11-.054-.216.04-.36.106-.165.319-.354.647-.548zm2.455-1.647c-.119.025-.237.05-.356.078a21.148 21.148 0 0 0 .5-1.05 12.045 12.045 0 0 0 .51.858c-.217.032-.436.07-.654.114zm2.525.939a3.881 3.881 0 0 1-.435-.41c.228.005.434.022.612.054.317.057.466.147.518.209a.095.095 0 0 1 .026.064.436.436 0 0 1-.06.2.307.307 0 0 1-.094.124.107.107 0 0 1-.069.015c-.09-.003-.258-.066-.498-.256zM8.278 6.97c-.04.244-.108.524-.2.829a4.86 4.86 0 0 1-.089-.346c-.076-.353-.087-.63-.046-.822.038-.177.11-.248.196-.283a.517.517 0 0 1 .145-.04c.013.03.028.092.032.198.005.122-.007.277-.038.465z"/>
										  	<path fill-rule="evenodd" d="M4 0h5.293A1 1 0 0 1 10 .293L13.707 4a1 1 0 0 1 .293.707V14a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2a2 2 0 0 1 2-2zm5.5 1.5v2a1 1 0 0 0 1 1h2l-3-3zM4.165 13.668c.09.18.23.343.438.419.207.075.412.04.58-.03.318-.13.635-.436.926-.786.333-.401.683-.927 1.021-1.51a11.651 11.651 0 0 1 1.997-.406c.3.383.61.713.91.95.28.22.603.403.934.417a.856.856 0 0 0 .51-.138c.155-.101.27-.247.354-.416.09-.181.145-.37.138-.563a.844.844 0 0 0-.2-.518c-.226-.27-.596-.4-.96-.465a5.76 5.76 0 0 0-1.335-.05 10.954 10.954 0 0 1-.98-1.686c.25-.66.437-1.284.52-1.794.036-.218.055-.426.048-.614a1.238 1.238 0 0 0-.127-.538.7.7 0 0 0-.477-.365c-.202-.043-.41 0-.601.077-.377.15-.576.47-.651.823-.073.34-.04.736.046 1.136.088.406.238.848.43 1.295a19.697 19.697 0 0 1-1.062 2.227 7.662 7.662 0 0 0-1.482.645c-.37.22-.699.48-.897.787-.21.326-.275.714-.08 1.103z"/>
										</svg>
									</button>
									<button id="gerarExcel" type="button" class="btn btn-outline-success" title="Exportar Excel" onclick="GerarRelatorio('xls')">
						                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-earmark-excel-fill" viewBox="0 0 16 16">
									  		<path d="M9.293 0H4a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h8a2 2 0 0 0 2-2V4.707A1 1 0 0 0 13.707 4L10 .293A1 1 0 0 0 9.293 0zM9.5 3.5v-2l3 3h-2a1 1 0 0 1-1-1zM5.884 6.68 8 9.219l2.116-2.54a.5.5 0 1 1 .768.641L8.651 10l2.233 2.68a.5.5 0 0 1-.768.64L8 10.781l-2.116 2.54a.5.5 0 0 1-.768-.641L7.349 10 5.116 7.32a.5.5 0 1 1 .768-.64z"></path>
										</svg>
									</button>
								</div>
							</div>
						</div>
					</div>
					
				  	<div class="table-responsive">         
						<table id="tabela" class="table table-bordered table-hover align-middle">
						    
						    <thead class="table-secondary">
						      <tr>
						        <th scope="col"><small>Tipo</small></th>
						        <th scope="col"><small>Tipo Alerta/Ocorrência</small></th>
						        <th scope="col"><small>Status</small></th>
						        <th scope="col"><small>Placa</small></th>
						        <th scope="col"><small>Data</small></th>
						        <th scope="col" class="w-8"><small>Visualização</small></th>
						        <th scope="col"><small>Ação</small></th>
						     </tr>
						    </thead>
						    
						    <tbody></tbody>
						    
						</table>
					</div>
					<%@ include file="/muralha-digital/utils/paginacao/componente-paginacao.jsp" %>
				</div>
			</div>
		</div>
		<div class="overlay"></div>
	</body>
	<%@ include	file="/muralha-digital/pages/registro_fato/comBoletim/modal/modal-registroDeFato.jsp" %>
	
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf/2.5.1/jspdf.umd.min.js" integrity="sha512-qZvrmS2ekKPF2mSznTQsxqPgnpkI4DNTlrdUmTzrDgektczlKNRRhy5X5AAOnx5S09ydFYWWNSfcEqDTTHgtNA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/jspdf-autotable/3.5.23/jspdf.plugin.autotable.min.js" integrity="sha512-P3z5YHtqjIxRAu1AjkWiIPWmMwO9jApnCMsa5s0UTgiDDEjTBjgEqRK0Wn0Uo8Ku3IDa1oer1CIBpTWAvqbmCA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.core.min.js" integrity="sha512-UhlYw//T419BPq/emC5xSZzkjjreRfN3426517rfsg/XIEC02ggQBb680V0VvP+zaDZ78zqse3rqnnI5EJ6rxA==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
	<script src="/muralha-digital/utils/export-table.js"></script>
</html>