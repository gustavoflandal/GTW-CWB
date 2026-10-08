<%--
    permanencia-veiculo.jsp
    Página JSP que apresenta a interface do relatório de Permanência do Veículo ao usuário.
    Permite selecionar data início, data final e placa, visualizar resultados e acionar a geração do relatório.
    Interage com o JavaScript para enviar parâmetros ao backend.
--%>
<%@ page language="java" pageEncoding="utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp"%>
<html lang="pt-br">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>Relatório de Permanência do Veículo</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
</head>
<body>
    <div class="container">
        <h2 class="text-center"><strong>Relatório de Permanência do Veículo</strong></h2>
        <form id="formRelatorio">
            <div class="row gy-3">
                <div class="col-sm-3">
                    <div class="mb-3">
                        <div class="form-group">
                            <label for="dataInicio" class="form-label">Data Início:</label>
                            <input type="date" class="form-control" id="data_inicio" name="data_inicio" required pattern="\d{4}-\d{2}-\d{2}" />
                        </div>
                    </div>
                </div>
                <div class="col-sm-3">
                    <div class="mb-3">
                        <div class="form-group">
                            <label for="dataFim" class="form-label">Data Fim:</label>
                            <input type="date" class="form-control" id="data_fim" name="data_fim" required pattern="\d{4}-\d{2}-\d{2}" />
                        </div>
                    </div>
                </div>
                <div class="col-sm-3">
                    <div class="mb-3">
                        <div class="form-group">
                            <label for="placa" class="form-label">Placa:</label>
                            <input type="text" class="form-control" id="placa" name="placa" required maxlength="8" />
                        </div>
                    </div>
                </div>
            </div>
            <div class="row gy-3">
                <div class="col-sm-3">
                    <button type="button" class="btn btn-primary" id="btnGerarExcel">Gerar Excel</button>
                </div>
            </div>
        </form>
    </div>
    <script src="js/permanencia-veiculo.js"></script>
    <script>
    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('btnGerarExcel').addEventListener('click', function() {
            var dataInicio = document.getElementById('data_inicio').value;
            var dataFim = document.getElementById('data_fim').value;
            var placa = document.getElementById('placa').value;
            if (!dataInicio || !dataFim || !placa) {
                alert('Preencha todos os campos!');
                return;
            }
            var regexData = /^\d{4}-\d{2}-\d{2}$/;
            if (!regexData.test(dataInicio) || !regexData.test(dataFim)) {
                alert('Formato de data inválido! Use yyyy-MM-dd.');
                return;
            }
            window.location.href = '/relatorio/RelatorioPermanenciaVeiculo?data_inicio=' + dataInicio + '&data_fim=' + dataFim + '&placa=' + placa;
        });
    });
    </script>
</body>
</html>
