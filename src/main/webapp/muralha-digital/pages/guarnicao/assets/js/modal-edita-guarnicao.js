$(document).ready(function () {
    const $modal = $('#modalEditarGuarnicao');

    $modal.on('shown.bs.modal', function () {
        const id = $modal.data('id');
        const $body = $modal.find('.modal-body');

        const html = `
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="dataPlantao" class="form-label">Data do Plantão</label>
                    <input type="date" class="form-control" id="dataPlantao" name="dataPlantao" required>
                </div>

                <div class="col-md-6 mb-3">
                    <label for="odometroGuarnicao" class="form-label">Odômetro (km)</label>
                    <input type="number" class="form-control" id="odometroGuarnicao" name="odometroGuarnicao" min="0" required>
                </div>

                <div class="col-md-6 mb-3">
                    <label for="horaInicioGuarnicao" class="form-label">Hora de Início</label>
                    <input type="time" class="form-control" id="horaInicioGuarnicao" name="horaInicioGuarnicao" required>
                </div>

                <div class="col-md-6 mb-3">
                    <label for="horaTerminoGuarnicao" class="form-label">Hora de Término</label>
                    <input type="time" class="form-control" id="horaTerminoGuarnicao" name="horaTerminoGuarnicao" required>
                </div>

                <div class="mb-3">
                    <label for="setoresGuarnicao" class="form-label">Setores Patrulhados</label>
                    <textarea class="form-control" id="setoresGuarnicao" name="setoresGuarnicao" rows="3" required></textarea>
                </div>

                <div class="mb-3">
                    <label for="meioTransporte" class="form-label">Meios de Deslocamento</label>
                    <select class="selectpicker col-12" multiple id="meioTransporte" name="meioTransporte" data-live-search="true">
                        <option value="Viatura">Viatura</option>
                        <option value="Moto">Moto</option>
                        <option value="Cavalo">Cavalo</option>
                        <option value="Bicicleta">Bicicleta</option>
                    </select>
                </div>

                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                    <button id="btnSalvarGuarnicao" class="btn btn-primary">Salvar</button>
                </div>
            </div>
        `;

        $body.html(html);

        $('#btnSalvarGuarnicao').on('click', function () {
            const dados = {
                acao: 'cadastrarInfoDiaria',
                idGuarnicao: id,
                data: $('#dataPlantao').val(),
                quilometragem: $('#odometroGuarnicao').val(),
                horaIni: $('#horaInicioGuarnicao').val(),
                horaFim: $('#horaTerminoGuarnicao').val(),
                setoresPatrulhados: $('#setoresGuarnicao').val(),
                meioTransporte: $('#meioTransporte').val().join(','),
                idUsuario: 1
            };

            $.ajax({
                url: '/MuralhaDigital/Guarnicao',
                method: 'POST',
                data: dados,
                traditional: true,
                success: function (resposta) {
                    console.log("Cadastro realizado:", resposta);
                    alert("Cadastro salvo com sucesso!");
                    $modal.modal('hide');
                },
                error: function (xhr) {
                    console.error("Erro no cadastro:", xhr.responseText);
                    alert("Erro ao salvar informações.");
                }
            });
        });

        $('.selectpicker').selectpicker('refresh');
    });
});
