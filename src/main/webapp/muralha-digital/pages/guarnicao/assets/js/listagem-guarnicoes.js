$(document).ready(function () {
    listarGuarnicoes();

	   $('#btnNovaGuarnicao').click(function () {
	    const modalElement = document.getElementById('modalCriarGuarnicao');
	    const modal = new bootstrap.Modal(modalElement, {
	        backdrop: 'static',
	        keyboard: false
	    });
	    modal.show();
	});

    $('#tabela-guarnicoes tbody').on('click', '.btn[title="Visualizar"]', function () {
        const row = $(this).closest('tr');
        const idGuarnicao = row.data('id');
        $('#visualizaNome').text(row.find('td:eq(0)').text());
        $('#visualizaResponsavel').text(row.find('td:eq(1)').text());
        $('#visualizaData').text(row.find('td:eq(2)').text());
        $('#modalVisualizaGuarnicao').data('id', idGuarnicao);

        const modal = new bootstrap.Modal(document.getElementById('modalVisualizaGuarnicao'), {
	        backdrop: 'static',
	        keyboard: false
	    });
        modal.show();
    });
    
        $('#tabela-guarnicoes tbody').on('click', '.btn[title="Atualizar"]', function () {
        const row = $(this).closest('tr');
        const idGuarnicao = row.data('id');
        $('#visualizaNome').text(row.find('td:eq(0)').text());
        $('#visualizaResponsavel').text(row.find('td:eq(1)').text());
        $('#visualizaData').text(row.find('td:eq(2)').text());
        $('#modalAtualizaGuarnicao').data('id', idGuarnicao);

		const modal = new bootstrap.Modal(document.getElementById('modalAtualizaGuarnicao'), {
		    backdrop: 'static', 
		    keyboard: false      
		});
		modal.show();

    });

    $('#tabela-guarnicoes tbody').on('click', '.btn[title="Editar"]', function () {
        console.log("Botão Editar clicado!");
        const row = $(this).closest('tr');
        const idGuarnicao = row.data('id');
        $('#modalEditarGuarnicao').data('id', idGuarnicao);

        const modalElement = document.getElementById('modalEditarGuarnicao');
        console.log("Elemento do Modal Editar encontrado:", modalElement);

        if (modalElement) {
            const modalInstance = new bootstrap.Modal(modalElement, {
		    backdrop: 'static', 
		    keyboard: false      
		});
            console.log("Instância do Modal de Editar criada:", modalInstance); 
            modalInstance.show();
            console.log("Chamado modalInstance.show() para Editar."); 
        } else {
            console.error("ERRO: Elemento modalEditarGuarnicao não encontrado no DOM!");
        }
    });
    
	$(document).on('click', '.btn-deletar', function () {
	    const id = $(this).data('id');
	    const acaoSoft = $(this).data('soft-delete');
	    if (confirm("Tem certeza que deseja alterar o status desta guarnição?")) {
	        deletarGuarnicao(id, acaoSoft);
	        setTimeout(() => location.reload(), 100);
	    }
	});

});

function deletarGuarnicao(idGuarnicao, acaoSoft){
	let mirror = acaoSoft == 0 ? 1 : 0;
	$.ajax({
		type: "POST",
		url: "/MuralhaDigital/Guarnicao",
		dataType: "json",
		data: {
                acao: 'deletarGuarnicao',
                idGuarnicao: idGuarnicao,
                acaoSoft: mirror
            },
		success: function (data) {
			console.log(data)
		},
		error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao listar guarnições:", errorThrown);
        }
	})
}

function listarGuarnicoes() {
    $.ajax({
        type: "GET",
        url: "/MuralhaDigital/Guarnicao",
        dataType: "json",
        data: "acao=listar",
        success: function (data) {
            const $tbody = $('#tabela-guarnicoes tbody');
            $tbody.empty();

            if (data.length === 0) {
                $tbody.append('<tr><td colspan="4" class="text-center">Nenhuma guarnição encontrada.</td></tr>');
                return;
            }

            data.forEach(function (guarnicao) {
				let iconSoftDelete = guarnicao.ativo == 0 ? 'bi bi-arrow-counterclockwise' : 'bi-trash';
				let colorSoftDelete = guarnicao.ativo != 0 ? 'btn-outline-danger' : 'btn-outline-success';
                const row = `<tr data-id="${guarnicao.id}">
                    <td>${guarnicao.nome}</td>
                    <td>${guarnicao.responsavel || '-'}</td>
                    <td>${guarnicao.data_criacao_formatado || '-'}</td>
                    <td>
                        <button class="btn btn-outline-primary btn-sm" title="Visualizar">
                            <i class="bi bi-search"></i>
                        </button>
                        <button class="btn btn-outline-secondary btn-sm" title="Editar">
                            <i class="bi bi-plus"></i>
                        </button>
                        <button class="btn btn-outline-secondary btn-sm" title="Atualizar">
                            <i class="bi bi-pen"></i>
                        </button>
				        <button class="btn-deletar btn ${colorSoftDelete} btn-sm" data-id="${guarnicao.id}" data-soft-delete="${guarnicao.ativo}" title="Deletar">
				            <i class="bi ${iconSoftDelete}"></i>
				        </button>
                    </td>
                </tr>`;
                $tbody.append(row);
            });
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erro ao listar guarnições:", errorThrown);
            const $tbody = $('#tabela-guarnicoes tbody');
            $tbody.empty().append('<tr><td colspan="4" class="text-danger text-center">Erro ao carregar guarnições.</td></tr>');
        }
    });
}