<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<html>
<head>
    <meta charset="ISO-8859-1">
    <title>Abrir Alerta</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
    
    <!-- jQuery e jQuery UI -->
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
    <script src="https://code.jquery.com/ui/1.13.2/jquery-ui.min.js"></script>
</head>
<body>
<div class="container mt-5">
    <h2 class="mb-4">Abertura de Alerta</h2>

    <form id="formAlerta" action="AlertaServlet" method="post">
        <!-- Tipo de Alerta (UUID) -->
        <div class="form-group">
            <label for="tipoAlerta">Tipo de Alerta</label>
            <select class="form-control" id="tipoAlerta" name="idTipoAlertaOcorrencia" required>
                <option value="">Selecione</option>
                <option value="95631582-96B2-4220-9612-12131BE4923C">Veículo Roubado</option>
                <option value="AE93F81A-DF6D-41B5-AFC6-99B3438C291D">Veículo Furtado</option>
                <option value="6631DC43-779F-4BFF-A329-B9653D056708">Clonagem de Placa</option>
            </select>
        </div>

			<div class="form-group">
			    <label for="veiculoPlaca">Placa do Veículo</label>
			    <input type="text" class="form-control" id="veiculoPlaca" placeholder="Digite a placa..." autocomplete="off" required>
			    <input type="hidden" id="idVeiculoMonitorado" name="idVeiculoMonitorado">
			</div>
			

        <!-- Descrição opcional -->
        <div class="form-group">
            <label for="descricao">Observações (opcional)</label>
            <textarea class="form-control" id="descricao" name="descricao" rows="3" placeholder="Detalhes adicionais..."></textarea>
        </div>

        <a href="atendimento-ocorrencias.jsp" class="btn btn-secondary">Cancelar</a>
        <button type="submit" class="btn btn-primary">Criar Alerta</button>
    </form>
</div>

<script>
$(function () {
    // Autocomplete para placa
    $("#veiculoPlaca").autocomplete({
        source: function (request, response) {
            $.ajax({
                url: "/MuralhaDigital/Alerta",
                data: {
                    acao: "obterVeiculosMonitorados",
                    placa: request.term
                },
                dataType: "json",
                success: function (data) {
                    let veiculos = [];

                    // Tenta extrair o array de veículos corretamente
                    if (Array.isArray(data)) {
                        veiculos = data;
                    } else if (data && Array.isArray(data.veiculos)) {
                        veiculos = data.veiculos;
                    } else {
                        console.error("Formato inesperado da resposta:", data);
                        alert("Erro ao buscar veículos. Tente novamente ou contate o suporte.");
                        return;
                    }
                    console.log(data)

                    // Mapeia os resultados para o autocomplete
                    const resultados = veiculos.map(function (item) {
                        return {
                            label: item.placa,
                            value: item.placa,
                            id: item.id
                        };
                    });

                    response(resultados);
                }
,
                error: function (xhr) {
                    console.error("Erro ao buscar placas:", xhr);
                    response([]);
                }
            });
        },
        minLength: 3,
        select: function (event, ui) {
            $("#idVeiculoMonitorado").val(ui.item.id);
        }
    });

    // Submissão via AJAX
    $("#formAlerta").on("submit", function (e) {
        e.preventDefault();

        const tipoAlerta = $("#tipoAlerta").val();
        const veiculo = $("#idVeiculoMonitorado").val();
        const descricao = $("#descricao").val();

        if (!veiculo) {
            alert("Selecione uma placa válida.");
            return;
        }

        const dados = {
            acao: "criarAlerta",
            tipoAlerta: tipoAlerta,
            veiculo: veiculo,  // Aqui enviamos o ID
            descricao: descricao
        };

        $.ajax({
            type: "POST",
            url: "/MuralhaDigital/Alerta",
            data: dados,
            dataType: "text",
            success: function (response) {
                alert("Alerta criado com sucesso!");
                window.location.href = "atendimento-ocorrencias.jsp";
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erro ao criar alerta:", errorThrown);
                alert("Erro ao criar alerta.");
            }
        });
    });
});

$(function () {
    $("#veiculoPlaca").autocomplete({
        source: function (request, response) {
            $.ajax({
                url: "/MuralhaDigital/Alerta",
                data: {
                    acao: "obterVeiculosMonitorados",
                    placa: request.term
                },
                dataType: "json",
                success: function (data) {
                    let veiculos = [];

                    if (Array.isArray(data)) {
                        veiculos = data;
                    } else if (data && Array.isArray(data.veiculos)) {
                        veiculos = data.veiculos;
                    } else {
                        console.error("Formato inesperado da resposta:", data);
                        response([]);
                        return;
                    }

                    const resultados = veiculos.map(function (item) {
                        return {
                            label: item.placa,
                            value: item.placa,
                            id: item.id
                        };
                    });

                    response(resultados);
                },
                error: function (xhr) {
                    console.error("Erro ao buscar placas:", xhr);
                    response([]);
                }
            });
        },
        minLength: 3,
        select: function (event, ui) {
            console.log("Selecionado:", ui.item);
            $("#idVeiculoMonitorado").val(ui.item.id);
        }
    });
});

</script>

</body>
</html>
