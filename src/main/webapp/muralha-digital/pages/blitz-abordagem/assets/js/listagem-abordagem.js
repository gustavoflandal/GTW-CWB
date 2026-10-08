$(document).ready(function () {
    carregarBlitz();
    listarAbordagens();

    $('#btnFiltrar').click(function () {
        listarAbordagens();
    });

    $('#btnVoltar').on('click', function () {
        if (document.referrer) {
            window.history.back();
        } else {
            window.location.href = '/login/muralha_principal.jsp';
        }
    });

    $('#btnNovaAbordagem').click(function () {
        window.location.href = '/muralha-digital/pages/blitz-abordagem/criar-abordagem.jsp';
    });

    $('#tabela-abordagem').on('click', 'tbody tr', function (e) {
        if (!$(e.target).closest('.btn-visualizar').length) {
            const idAbordagem = $(this).data('id');
            if (idAbordagem) {
                window.location.href = '/muralha-digital/pages/blitz-abordagem/detalhes-abordagem.jsp?id=' + idAbordagem;
            }
        }
    });

    $('#tabela-abordagem').on('click', '.btn-visualizar', function (e) {
        e.stopPropagation();
        const idAbordagem = $(this).closest('tr').data('id');
        window.location.href = '/muralha-digital/pages/blitz-abordagem/detalhes-abordagem.jsp?id=' + idAbordagem;
    });
});

function carregarBlitz() {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { acao: 'listar' },
        dataType: 'json',
        success: function(response) {
            const select = $('#selectBlitz');
            select.empty();
            select.append('<option value="">Todas as Blitz</option>');
            
            if (response.blitzes && response.blitzes.length > 0) {
                response.blitzes.forEach(function(blitz) {
                    select.append($('<option></option>')
                        .val(blitz.id)
                        .text(blitz.nome_blitz));
                });
            }
        },
        error: function(error) {
            console.error('Erro ao carregar blitz:', error);
        }
    });
}

function listarAbordagens() {
    const dataInicio = $('#dataInicio').val();
    const dataFim = $('#dataFim').val();
    const idBlitz = $('#selectBlitz').val();

    const params = {
        acao: 'listarAbordagensFiltro',
        dataInicio: dataInicio,
        dataFim: dataFim,
        idBlitz: idBlitz
    };

    $('body').addClass('loading');
    
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: params,
        dataType: 'json',
        success: function(response) {
            const $tbody = $('#tabela-abordagem tbody');
            $tbody.empty();

            if (!response.abordagens || response.abordagens.length === 0) {
                $tbody.append('<tr><td colspan="6" class="text-center">Nenhuma abordagem encontrada.</td></tr>');
                return;
            }

            response.abordagens.forEach(function(abordagem) {
                const status = abordagem.status;
                const statusClass = status === 'LIBERADO' ? 'badge-liberado' : 'badge-cancelado';
                const statusText = status === 'LIBERADO' ? 'Liberado' : 'Retido';
                
                const row = `<tr data-id="${abordagem.id}">
                    <td><small>${abordagem.placa_veiculo || '-'}</small></td>
                    <td><small>${abordagem.nome_blitz || '-'}</small></td>
                    <td><small>${formatarData(abordagem.data_abordagem)}</small></td>
                    <td><small>${abordagem.nome_agente || '-'}</small></td>
                    <td><small><span class="badge ${statusClass}">${statusText}</span></small></td>
                    <td>
                        <button class="btn btn-outline-primary btn-sm btn-visualizar" title="Visualizar">
                            <i class="bi bi-search"></i>
                        </button>
                    </td>
                </tr>`;
                $tbody.append(row);
            });
        },
        error: function(error) {
            console.error('Erro ao listar abordagens:', error);
            const $tbody = $('#tabela-abordagem tbody');
            $tbody.empty().append('<tr><td colspan="6" class="text-danger text-center">Erro ao carregar abordagens.</td></tr>');
        },
        complete: function() {
            $('body').removeClass('loading');
        }
    });
}

function formatarData(dataString) {
    if (!dataString) return '-';
    const date = new Date(dataString);
    if (isNaN(date.getTime())) return '-';
    
    const dia = date.getDate().toString().padStart(2, '0');
    const mes = (date.getMonth() + 1).toString().padStart(2, '0');
    const ano = date.getFullYear();
    const horas = date.getHours().toString().padStart(2, '0');
    const minutos = date.getMinutes().toString().padStart(2, '0');
    
    return `${dia}/${mes}/${ano} ${horas}:${minutos}`;
}