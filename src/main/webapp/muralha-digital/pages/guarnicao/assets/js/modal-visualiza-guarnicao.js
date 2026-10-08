$(document).ready(function () {
    const $modal = $('#modalVisualizaGuarnicao');

    $modal.on('shown.bs.modal', function () {
        const id = $modal.data('id');
        const $body = $modal.find('.modal-body');

        if (id) {
            $.ajax({
                url: '/MuralhaDigital/Guarnicao',
                method: 'GET',
                data: {
                    acao: 'buscarPorId',
                    id: id
                },
                success: function (guarnicao) {

                    const html = `
                    <div class="container mt-3 mb-3">

                        <!-- Nav Tabs -->
                        <ul class="nav nav-tabs mb-3" id="guarnicaoTabs" role="tablist">
                            <li class="nav-item" role="presentation">
                                <button class="nav-link active" id="dados-tab" data-bs-toggle="tab" data-bs-target="#dados" type="button" role="tab" aria-controls="dados" aria-selected="true">
                                    Dados
                                </button>
                            </li>
                            <li class="nav-item" role="presentation">
                                <button class="nav-link" id="historico-tab" data-bs-toggle="tab" data-bs-target="#historico" type="button" role="tab" aria-controls="historico" aria-selected="false">
                                    Histórico
                                </button>
                            </li>
                        </ul>

                        <!-- Tab Contents -->
                        <div class="tab-content" id="guarnicaoTabsContent">
                            <!-- Aba Dados -->
                            <div class="tab-pane fade show active" id="dados" role="tabpanel" aria-labelledby="dados-tab">
                                <div class="row g-4">
                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold text-secondary">Nome da Guarnição</label>
                                        <div class="border rounded p-2 bg-light">${guarnicao.nome || 'Nome não disponível'}</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold text-secondary">Responsável</label>
                                        <div class="border rounded p-2 bg-light">${guarnicao.responsavel}</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold text-secondary">Integrantes</label>
                                        <div style="min-height: 42.2px;" class="border rounded p-2 bg-light">${guarnicao.integrantes.length === 0 ? 'nenhum usuário informado' : guarnicao.integrantes.map(m => m.nome).join(', ')}</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold text-secondary">Meios de deslocamento</label>
                                        <div class="border rounded p-2 bg-light">${guarnicao.meiosDeslocamento}</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold text-secondary">Data de Criação</label>
                                        <div class="border rounded p-2 bg-light">${guarnicao.data_criacao_formatado || guarnicao.data_criacao || 'Data não disponível'}</div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-semibold text-secondary">Última Alteração</label>
                                        <div class="border rounded p-2 bg-light">${guarnicao.data_alteracao_formatado || '—'}</div>
                                    </div>
                                </div>
                            </div>

                            <!-- Aba Histórico -->
                            <div class="tab-pane fade" id="historico" role="tabpanel" aria-labelledby="historico-tab">
                                <div class="p-3 border rounded bg-light text-muted">Carregando histórico...</div>
                            </div>
                        </div>
                    </div>
                    `;

                    $body.html(html);

                    $.ajax({
                        url: '/MuralhaDigital/Guarnicao',
                        method: 'GET',
                        data: {
                            acao: 'listarGuarnicoesDiariasPorGuarnicao',
                            idGuarnicao: id
                        },
                        success: function (historicos) {
                            const $historicoTab = $('#historico');

                            if (!historicos || historicos.length === 0) {
                                $historicoTab.html('<div class="p-3 border rounded bg-light text-muted">Nenhum histórico disponível no momento.</div>');
                            } else {
                                let historicoHtml = `
                                    <div class="table-responsive">
                                        <table class="table table-bordered table-striped align-middle">
                                            <thead class="table-light">
                                                <tr>
                                                    <th>Data</th>
                                                    <th>Hora Início</th>
                                                    <th>Hora Fim</th>
                                                    <th>Quilometragem</th>
                                                    <th>Setores Patrulhados</th>
                                                    <th>Transporte</th>
                                                    <th>Usuário</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                `;

                                historicos.forEach(h => {
	console.log(h)
                                    const data = h.data ? new Date(h.data).toLocaleDateString('pt-BR') : '—';
                                    historicoHtml += `
                                        <tr>
                                            <td>${h.guarnicao.data_criacao_formatado}</td>
                                            <td>${h.horaIni.split(':').slice(0, 2).join(':') || '—'}</td>
                                            <td>${h.horaFim.split(':').slice(0, 2).join(':') || '—'}</td>
                                            <td>${h.quilometragem || '—'} Km</td>
                                            <td>${h.setoresPatrulhados || '—'}</td>
                                            <td>${h.meioTransporte || '—'}</td>
                                            <td>${h.usuario?.usuario || '—'}</td>
                                        </tr>
                                    `;
                                });

                                historicoHtml += `
                                            </tbody>
                                        </table>
                                    </div>
                                `;

                                $historicoTab.html(historicoHtml);
                            }
                        },
                        error: function () {
                            $('#historico').html('<div class="text-danger p-3">Erro ao carregar o histórico da guarnição.</div>');
                        }
                    });

                },
                error: function (xhr, status, error) {
                    console.error("Erro ao buscar guarnição:", error);
                    $body.html('<p class="text-danger">Erro ao carregar os dados da guarnição.</p>');
                }
            });
        } else {
            console.warn("ID da guarnição não encontrado no modal.");
            $body.html('<p class="text-warning">Guarnição não identificada.</p>');
        }
    });
});
