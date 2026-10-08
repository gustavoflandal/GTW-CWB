<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Cadastro de Nova Guarnição</title>
<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>

<!-- Bootstrap CSS -->
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/css/bootstrap-select.min.css" integrity="sha512-mR/b5Y7FRsKqrYZou7uysnOdCIJib/7r5QeJMFvLNHNhtye3xJp1TdJVPLtetkukFn227nKpXD9OjUc09lx97Q==" crossorigin="anonymous" referrerpolicy="no-referrer" />
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet"><!-- Bootstrap Select -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/css/bootstrap-select.min.css">
<link rel="stylesheet" href="assets/css/nova-guarnicao.css">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Bootstrap Select CSS -->
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/css/bootstrap-select.min.css">

<!-- Bootstrap Select JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap-select@1.13.14/dist/js/bootstrap-select.min.js"></script>
<script>


</script>
</head>
<body>
    <div class="container mt-3 mb-3">
        <h2 class="mb-4 text-center">Cadastro de Nova Guarnição</h2>

        <form id="formGuarnicao">
            <div class="mb-3">
                <label for="nomeGuarnicao" class="form-label">Nome da Guarnição</label>
                <input type="text" class="form-control" id="nomeGuarnicao" name="nome" required>
            </div>

            <div class="mb-3">
                <label for="responsavel" class="form-label">Responsável</label>
                <select class="form-select select-guarnicao" id="responsavelGuarnicao" name="responsavel" required>
                </select>
            </div>

            <div class="mb-3">
                <label for="selectIntegrantes" class="form-label space-full">Integrantes</label>
                <select class="selectpicker select-guarnicao space-full" placeholder="selecione os integrantes" id="selectIntegrantes" name="selectIntegrantes" multiple data-live-search="true">
				</select>
				<div id="erroIntegrantes" class="text-danger mt-1" style="display: none;"></div>
            </div>

            <div class="mb-3">
                <label for="meiosDeslocamento" class="form-label space-full">Meios de Deslocamento</label>
                <select class="selectpicker select-guarnicao space-full" placeholder="selecione os meios de deslocamento" multiple id="meiosDeslocamento" name="meiosDeslocamento" data-live-search="true">
				    <option value="Viatura">Viatura</option>
				    <option value="Moto">Moto</option>
				    <option value="Cavalo">Cavalo</option>
				    <option value="Bicicleta">Bicicleta</option>
				</select>
				<div id="erroMeiosDeslocamento" class="text-danger mt-1" style="display: none;"></div>
            </div>

            <div id="messageBox" class="message-box hidden"></div>

            <div class="d-flex justify-content-between">
                <button class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                <button id="salvarGuarnicao" class="btn btn-success">Salvar Guarnição</button>
            </div>
        </form>
    </div>

    <script src="assets/js/nova-guarnicao.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/js/jquery.quicksearch.js"></script>
		<script type="text/javascript" src="/muralha-digital/assets/jquery/jquery.mask-1.14.16.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
		
		<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-select/1.14.0-beta2/js/bootstrap-select.min.js" integrity="sha512-FHZVRMUW9FsXobt+ONiix6Z0tIkxvQfxtCSirkKc5Sb4TKHmqq1dZa8DphF0XqKb3ldLu/wgMa8mT6uXiLlRlw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
</body>
</html>
