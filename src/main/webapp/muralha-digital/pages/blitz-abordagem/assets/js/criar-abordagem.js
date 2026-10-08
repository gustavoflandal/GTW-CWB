let imagensAbordagemSelecionadas = [];
window.resultadosAbordagem = [];
window.statusAbordagemAtual = null;
window.motivoLiberacaoTemp = null;

$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const idAlerta = urlParams.get('idAlerta');
    const idBlitz = urlParams.get('idBlitz');
    const idVeiculoTempoReal = urlParams.get('idVeiculoTempoReal');
    
    window.dadosAlerta = null;
    window.registroFatoAssociado = null;

    if (isMobileApp()) {
        document.getElementById('btnCapturarPlaca').style.display = 'block';
        
        window.onImageCaptured = function(imageData) {
            if (imageData && imageData.base64) {
                const byteCharacters = atob(imageData.base64);
                const byteNumbers = new Array(byteCharacters.length);
                for (let i = 0; i < byteCharacters.length; i++) {
                    byteNumbers[i] = byteCharacters.charCodeAt(i);
                }
                const byteArray = new Uint8Array(byteNumbers);
                const file = new File(
                    [byteArray], 
                    imageData.name || `imagem_${Date.now()}.jpg`, 
                    { type: imageData.mimeType || 'image/jpeg' }
                );
                
                imagensAbordagemSelecionadas.push(file);
                renderizarPreviewImagensAbordagem();
                atualizarInputFileImagensAbordagem();
            }
        };
    }
    
    if (idAlerta) {
        configurarBotoesAcaoAbordagem();
        $('#containerEditarPlaca').remove();
        carregarDadosAlerta(idAlerta);
    } else if (idBlitz && idVeiculoTempoReal) {
        configurarBotoesAcaoAbordagem();
        $('#placaVeiculo').closest('.mb-3').hide();
        $('#containerEditarPlaca').remove();
        carregarDadosBlitzOstensiva(idBlitz, idVeiculoTempoReal);
    } else {
        configurarBotoesAcaoAbordagem();
        carregarDadosIniciais();
    }

    $('#btnConcluirAbordagem').click(function () {
        finalizarAbordagem('CONCLUIDO');
    });

    $('#btnLiberarAbordagem').click(function () {
        finalizarAbordagem('LIBERADO');
    });

    $(document).on('click', '#btnConfirmarLiberacao', function() {
        const motivo = $('#motivoLiberacao').val();
        
        if (!motivo || motivo.trim() === '') {
            $('#motivoLiberacao').addClass('is-invalid');
            return;
        }
        
        $('#motivoLiberacao').removeClass('is-invalid');
        
        window.motivoLiberacaoTemp = motivo;
        
        $('#modalMotivoLiberacao').modal('hide');
        $('#motivoLiberacao').val(''); // Limpar para próxima vez
        
        salvarAbordagem();
    });

    // Limpar validação ao digitar
    $(document).on('input', '#motivoLiberacao', function() {
        $(this).removeClass('is-invalid');
    });

    $(document).on('click', '#btnEditarPlaca', function () {
        $('#placaVeiculo')
            .prop('readonly', false)
            .prop('required', true)
            .focus();

        // Quando o usuário alterar manualmente a placa, buscar informações automaticamente
        // const placa = $('#placaVeiculo').val();
        // if (placa && placa.length >= 7) {
        //     buscarInformacoesVeiculoParaCriacao(placa);
        // }

        $('#containerEditarPlaca').remove();
    });

    $(document).on('click', '#btnSelecionarImagensAbordagem', function () {
        if (isMobileApp()) {
            if (window.FlutterImageChannel) {
                window.FlutterImageChannel.postMessage(JSON.stringify({ action: 'captureImage' }));
                return;
            }
        }
        $('#imagensAbordagem').trigger('click');
    });

    $(document).on('click', '.btn-remover-imagem-abordagem', function () {
        const index = $(this).data('index');

        imagensAbordagemSelecionadas.splice(index, 1);

        renderizarPreviewImagensAbordagem();
        atualizarInputFileImagensAbordagem();
    });

    $(document).on('change', '#imagensAbordagem', function () {
        const files = Array.from(this.files);

        if (!isMobileApp()) {
            adicionarMetadadosExifWeb(files, function(filesComMetadados) {
                imagensAbordagemSelecionadas = imagensAbordagemSelecionadas.concat(filesComMetadados);
                renderizarPreviewImagensAbordagem();
                atualizarInputFileImagensAbordagem();
            });
        } else {
            imagensAbordagemSelecionadas = imagensAbordagemSelecionadas.concat(files);
            renderizarPreviewImagensAbordagem();
            atualizarInputFileImagensAbordagem();
        }
    });

    $('#placaVeiculo').on('input', function() {
        verificarPlacaCompleta($(this));

        // Adicionar busca automática de informações quando placa completa
        // const placa = $(this).val();
        // if (placa && placa.length >= 7) {
        //     buscarInformacoesVeiculoParaCriacao(placa);
        // }
    });

    let pessoaCounter = 0;
    $('#btnAddPessoa').click(function() {
        pessoaCounter++;
        const pessoaId = 'pessoa_' + pessoaCounter;
        
        const pessoaHtml = `
        <div class="pessoa-card" id="${pessoaId}">
            <button type="button" class="btn btn-sm btn-danger btn-remove" onclick="removerPessoa('${pessoaId}')">
                <i class="bi bi-x"></i>
            </button>
            <div class="row g-2">
                <div class="col-md-6">
                    <label class="form-label">CPF</label>
                    <input type="text" class="form-control cpf" placeholder="000.000.000-00" maxlength="14">
                    <div class="invalid-feedback">
                        CPF inválido. Por favor, verifique o número digitado.
                    </div>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Nome Completo *</label>
                    <input type="text" class="form-control nome" required maxlength="200">
                </div>
                <div class="col-md-4">
                    <label class="form-label">Data Nascimento</label>
                    <input type="date" class="form-control dataNascimento">
                </div>
                <div class="col-md-4">
                    <label class="form-label">Tipo *</label>
                    <select class="form-select tipo" required>
                        <option value="">Selecione</option>
                    </select>
                </div>
                <div class="col-md-4">
                    <label class="form-label">Sexo</label>
                    <select class="form-select sexo">
                        <option value="">Selecione</option>
                        <option value="M">Masculino</option>
                        <option value="F">Feminino</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Telefone</label>
                    <input type="text" class="form-control telefone" placeholder="(11) 99999-9999" maxlength="15">
                </div>
                <div class="col-md-6">
                    <label class="form-label">Email</label>
                    <input type="email" class="form-control email" maxlength="100">
                </div>
                <div class="col-12">
                    <label class="form-label">Observações</label>
                    <textarea class="form-control observacoes" rows="2" maxlength="500"></textarea>
                </div>
            </div>
        </div>`;
        
        if ($('#containerPessoas').children().length === 1 && $('#containerPessoas').children().first().hasClass('text-muted')) {
            $('#containerPessoas').empty();
        }
        
        $('#containerPessoas').append(pessoaHtml);
        carregarTiposEnvolvimentoParaElemento($('#' + pessoaId + ' .tipo'));
        
        $('#' + pessoaId + ' .cpf').mask('000.000.000-00');
        $('#' + pessoaId + ' .telefone').mask('(00) 00000-0000');

        $('#' + pessoaId + ' .cpf').on('blur', function() {
            const cpfInput = $(this);
            const cpf = cpfInput.val();
            
            if (cpf && cpf !== '') {
                if (!validarCPF(cpf)) {
                    cpfInput.addClass('is-invalid');
                } else {
                    cpfInput.removeClass('is-invalid');
                    verificarCPFCompleto(cpfInput);
                }
            } else {
                cpfInput.removeClass('is-invalid');
                verificarCPFCompleto(cpfInput);
            }
        });

        // Verificação em tempo real do CPF para mostrar/esconder botão de histórico
        $('#' + pessoaId + ' .cpf').on('input', function() {
            const cpfInput = $(this);
            
            if (cpfInput.hasClass('is-invalid')) {
                cpfInput.removeClass('is-invalid');
            }
            
            verificarCPFCompleto(cpfInput);
        });

        $('#' + pessoaId + ' .nome').on('input', function() {
            atualizarPessoasDropdown();
        });
        
        $('#' + pessoaId + ' .tipo').change(function() {
            atualizarPessoasDropdown();
        });
        
        atualizarPessoasDropdown();
    });

    let documentoCounter = 0;
    $('#btnAddDocumento').click(function() {
        documentoCounter++;
        const documentoId = 'documento_' + documentoCounter;
        
        const documentoHtml = `
        <div class="documento-card" id="${documentoId}">
            <button type="button" class="btn btn-sm btn-danger btn-remove" onclick="removerDocumento('${documentoId}')">
                <i class="bi bi-x"></i>
            </button>
            <div class="row g-2">
                <div class="col-md-6">
                    <label class="form-label">Tipo Documento *</label>
                    <select class="form-select tipoDocumento" required>
                        <option value="">Selecione</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Número *</label>
                    <input type="text" class="form-control numeroDocumento" required maxlength="100">
                </div>
                <div class="col-md-6">
                    <label class="form-label">Nome do Titular *</label>
                    <input type="text" class="form-control nomeTitular" required maxlength="200">
                </div>
                <div class="col-md-6">
                    <label class="form-label">Validade</label>
                    <input type="date" class="form-control validade">
                </div>
                <div class="col-md-6">
                    <label class="form-label">Situação *</label>
                    <select class="form-select situacao" required>
                        <option value="">Selecione</option>
                    </select>
                </div>
                <div class="col-md-6">
                    <label class="form-label">Pertence a Pessoa</label>
                    <select class="form-select pertencePessoa">
                        <option value="">Selecione a pessoa</option>
                    </select>
                </div>
                <div class="col-12">
                    <label class="form-label">Arquivos do Documento</label>
                    <div class="file-upload-container">
                        <input type="file" class="form-control arquivosDocumento" 
                            accept="image/*,application/pdf" multiple style="display: none;">
                        <div class="input-group">
                            <input type="text" class="form-control nomeArquivosDocumento" 
                                placeholder="Nenhum arquivo selecionado" readonly>
                            <button type="button" class="btn btn-outline-secondary btn-selecionar-arquivos-documento" 
                                    data-doc-id="${documentoId}">
                                <i class="bi bi-paperclip"></i> Selecionar Arquivos
                            </button>
                        </div>
                        <small class="text-muted">
                            É possível adicionar múltiplos arquivos (imagens ou PDF, máx. 10MB cada)
                        </small>
                        <div class="preview-arquivos-documento row mt-2"></div>
                    </div>
                </div>
                <div class="col-12">
                    <label class="form-label">Observações</label>
                    <textarea class="form-control observacoes" rows="2" maxlength="500"></textarea>
                </div>
            </div>
        </div>`;
        
        if ($('#containerDocumentos').children().length === 1 && $('#containerDocumentos').children().first().hasClass('text-muted')) {
            $('#containerDocumentos').empty();
        }
        
        $('#containerDocumentos').append(documentoHtml);
        carregarTiposDocumentoParaElemento($('#' + documentoId + ' .tipoDocumento'));
        carregarSituacoesDocumentoParaElemento($('#' + documentoId + ' .situacao'));
        atualizarPessoasDropdown();
        configurarUploadDocumento(documentoId);

        $('#' + documentoId + ' .tipoDocumento').change(function () {
            const tipo = $(this).val();
            const inputNumero = $('#' + documentoId + ' .numeroDocumento');

            inputNumero.unmask();
            inputNumero.val('');

            if (tipo === 'CPF') {
                inputNumero.mask('000.000.000-00');
            } else if (tipo === 'RG') {
                inputNumero.mask('00.000.000-0');
            } else if (tipo === 'CNH') {
                inputNumero.mask('00000000000');
            }
        });

        configurarUploadMultiplasImagensDocumento(documentoId);
    });
});

function adicionarMetadadosExifWeb(files, callback) {
    if (!files || files.length === 0) {
        callback([]);
        return;
    }

    navigator.geolocation.getCurrentPosition(
        function(position) {
            const latitude = position.coords.latitude;
            const longitude = position.coords.longitude;

            const promises = Array.from(files).map(file => {
                return new Promise((resolve) => {

                    // NÃO É IMAGEM → retorna direto (PDF, etc)
                    if (!file.type.startsWith('image/')) {
                        resolve(file);
                        return;
                    }

                    // SE NÃO FOR JPEG → converter
                    if (!file.type.startsWith('image/jpeg')) {
                        const reader = new FileReader();

                        reader.onload = function(e) {
                            const img = new Image();

                            img.onload = function() {
                                const canvas = document.createElement('canvas');
                                canvas.width = img.width;
                                canvas.height = img.height;

                                const ctx = canvas.getContext('2d');
                                ctx.drawImage(img, 0, 0);

                                canvas.toBlob(function(blob) {
                                    const convertedFile = new File(
                                        [blob],
                                        file.name.replace(/\.\w+$/, '.jpg'),
                                        { type: 'image/jpeg' }
                                    );

                                    processarComExif(convertedFile, resolve, latitude, longitude);
                                }, 'image/jpeg', 0.95);
                            };

                            img.onerror = function() {
                                console.error('Erro ao carregar imagem');
                                resolve(file);
                            };

                            img.src = e.target.result;
                        };

                        reader.readAsDataURL(file);
                        return;
                    }

                    processarComExif(file, resolve, latitude, longitude);
                });
            });

            Promise.all(promises).then(callback);
        },
        function(error) {
            console.warn('Erro ao obter localização:', error);
            callback(Array.from(files));
        },
        { timeout: 10000 }
    );
}

function processarComExif(file, resolve, latitude, longitude) {
    const reader = new FileReader();

    reader.onload = function(e) {
        let dataUrl = e.target.result;

        try {
            function toDMS(value) {
                const deg = Math.floor(value);
                const minFloat = (value - deg) * 60;
                const min = Math.floor(minFloat);
                const sec = Math.round((minFloat - min) * 60 * 100);

                return [
                    [deg, 1],
                    [min, 1],
                    [sec, 100]
                ];
            }

            const latRef = latitude >= 0 ? "N" : "S";
            const lonRef = longitude >= 0 ? "E" : "W";

            const now = new Date();
            const formattedDate =
                now.getFullYear() + ':' +
                String(now.getMonth() + 1).padStart(2, '0') + ':' +
                String(now.getDate()).padStart(2, '0') + ' ' +
                String(now.getHours()).padStart(2, '0') + ':' +
                String(now.getMinutes()).padStart(2, '0') + ':' +
                String(now.getSeconds()).padStart(2, '0');

            const exifObj = {
                "0th": {
                    [piexif.ImageIFD.DateTime]: formattedDate,
                },
                "Exif": {
                    [piexif.ExifIFD.DateTimeOriginal]: formattedDate,
                },
                "GPS": {
                    [piexif.GPSIFD.GPSLatitude]: toDMS(Math.abs(latitude)),
                    [piexif.GPSIFD.GPSLatitudeRef]: latRef,
                    [piexif.GPSIFD.GPSLongitude]: toDMS(Math.abs(longitude)),
                    [piexif.GPSIFD.GPSLongitudeRef]: lonRef,
                }
            };

            const exifBytes = piexif.dump(exifObj);
            const newDataUrl = piexif.insert(exifBytes, dataUrl);

            const byteString = atob(newDataUrl.split(',')[1]);
            const mimeString = newDataUrl.split(',')[0].split(':')[1].split(';')[0];

            const ab = new ArrayBuffer(byteString.length);
            const ia = new Uint8Array(ab);

            for (let i = 0; i < byteString.length; i++) {
                ia[i] = byteString.charCodeAt(i);
            }

            const blob = new Blob([ab], { type: mimeString });

            const newFile = new File(
                [blob],
                file.name.replace(/\.\w+$/, '.jpg'),
                { type: 'image/jpeg' }
            );

            resolve(newFile);

        } catch (err) {
            console.error("Erro ao inserir EXIF:", err);
            resolve(file);
        }
    };

    reader.readAsDataURL(file);
}

function carregarDadosIniciais() {
    Promise.all([
        new Promise((resolve) => {
            carregarBlitz();
            resolve();
        }),
        new Promise((resolve) => {
            carregarTiposEnvolvimento();
            resolve();
        }),
        new Promise((resolve) => {
            carregarTiposDocumento();
            resolve();
        }),
        new Promise((resolve) => {
            carregarSituacoesDocumento();
            resolve();
        }),
        new Promise((resolve) => {
            carregarResultadosAbordagem();
            resolve();
        }),
    ]).then(() => {
        setTimeout(() => {
            $('body').removeClass('loading');
            $('.overlay').hide();
        }, 300);
    });
}

function carregarBlitz() {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { acao: 'listarAtivas' },
        dataType: 'json',
        success: function(response) {
            const select = $('#selectBlitz');
            select.empty();
            select.append('<option value="">Selecione uma blitz</option>');
            
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

function carregarTiposEnvolvimento() {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { acao: 'listarTiposEnvolvimento' },
        dataType: 'json',
        success: function(response) {
            window.tiposEnvolvimento = response.tiposEnvolvimento || [];
            $('.tipo').each(function() {
                carregarTiposEnvolvimentoParaElemento($(this));
            });
        },
        error: function(error) {
            console.error('Erro ao carregar tipos de envolvimento:', error);
        }
    });
}

function carregarTiposEnvolvimentoParaElemento(element) {
    if (window.tiposEnvolvimento) {
        element.empty();
        element.append('<option value="">Selecione</option>');
        window.tiposEnvolvimento.forEach(function(tipo) {
            element.append($('<option></option>')
                .val(tipo)
                .text(tipo === 'CONDUTOR' ? 'Condutor' : 
                      tipo === 'PASSAGEIRO' ? 'Passageiro' : 'Proprietário'));
        });
    }
}

function carregarTiposDocumento() {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { acao: 'listarTiposDocumento' },
        dataType: 'json',
        success: function(response) {
            window.tiposDocumento = response.tiposDocumento || [];
            $('.tipoDocumento').each(function() {
                carregarTiposDocumentoParaElemento($(this));
            });
        },
        error: function(error) {
            console.error('Erro ao carregar tipos de documento:', error);
        }
    });
}

function carregarTiposDocumentoParaElemento(element) {
    if (window.tiposDocumento) {
        element.empty();
        element.append('<option value="">Selecione</option>');
        window.tiposDocumento.forEach(function(tipo) {
            element.append($('<option></option>').val(tipo).text(tipo));
        });
    }
}

function carregarSituacoesDocumento() {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { acao: 'listarSituacoesDocumento' },
        dataType: 'json',
        success: function(response) {
            window.situacoesDocumento = response.situacoesDocumento || [];
            $('.situacao').each(function() {
                carregarSituacoesDocumentoParaElemento($(this));
            });
        },
        error: function(error) {
            console.error('Erro ao carregar situações de documento:', error);
        }
    });
}

function carregarSituacoesDocumentoParaElemento(element) {
    if (window.situacoesDocumento) {
        element.empty();
        element.append('<option value="">Selecione</option>');
        window.situacoesDocumento.forEach(function(situacao) {
            element.append($('<option></option>')
                .val(situacao)
                .text(situacao === 'VALIDO' ? 'Válido' : 
                      situacao === 'VENCIDO' ? 'Vencido' : 
                      situacao === 'IRREGULAR' ? 'Irregular' : 'Suspenso'));
        });
    }
}

function atualizarPessoasDropdown() {
    const pessoas = [];
    $('.pessoa-card').each(function(index) {
        const nome = $(this).find('.nome').val();
        const tipo = $(this).find('.tipo').val();
        if (nome) {
            pessoas.push({
                id: index + 1,
                nome: nome,
                tipo: tipo || 'Não definido'
            });
        }
    });
    
    $('.pertencePessoa').each(function() {
        const select = $(this);
        const currentValue = select.val();
        select.empty();
        select.append('<option value="">Selecione a pessoa</option>');
        
        pessoas.forEach(function(pessoa) {
            select.append($('<option></option>')
                .val(pessoa.id)
                .text(`${pessoa.nome} (${pessoa.tipo})`)
                .prop('selected', pessoa.id.toString() === currentValue));
        });
    });
}

function removerPessoa(id) {
    $('#' + id).remove();
    if ($('#containerPessoas').children().length === 0) {
        $('#containerPessoas').html('<p class="text-center text-muted">Nenhuma pessoa adicionada</p>');
    }
    atualizarPessoasDropdown();
}

function removerDocumento(id) {
    $('#' + id).remove();
    if ($('#containerDocumentos').children().length === 0) {
        $('#containerDocumentos').html('<p class="text-center text-muted">Nenhum documento adicionado</p>');
    }
}

function salvarAbordagem() {
    const optionSelecionada = $('#selectResultadoAbordagem').find(':selected');

    if (!optionSelecionada.val()) {
        showMessage('Selecione o resultado da abordagem.', false);
        return;
    }

    if (!validarCampoPlaca()) {
        if (window.dadosAlerta) {
            showMessage('Erro: Não foi possível obter a placa do alerta.', false);
        } else {
            showMessage('Por favor, informe a placa do veículo.', false);
            $('#placaVeiculo').addClass('is-invalid');
        }
        return;
    }

    if (!$('#selectResultadoAbordagem').val()) {
        showMessage('Selecione o resultado da abordagem.', false);
        $('#selectResultadoAbordagem').focus();
        return;
    }

    $('#placaVeiculo').removeClass('is-invalid');
    if (!$('#formAbordagem')[0].checkValidity()) {
        $('#formAbordagem')[0].reportValidity();
        return;
    }

    let emailValido = true;
    $('.email').each(function() {
        const email = $(this).val();
        if (email && email !== '') {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                emailValido = false;
                $(this).addClass('is-invalid');
                showMessage('Por favor, insira um email válido.', false);
            } else {
                $(this).removeClass('is-invalid');
            }
        }
    });
    
    if (!emailValido) {
        return;
    }

    let cpfValido = true;
    $('.cpf').each(function() {
        const cpfInput = $(this);
        const cpf = cpfInput.val();
        
        if (cpf && cpf !== '') {
            if (!validarCPF(cpf)) {
                cpfValido = false;
                cpfInput.addClass('is-invalid');
                const pessoaCard = cpfInput.closest('.pessoa-card');
                const nomePessoa = pessoaCard.find('.nome').val() || 'Pessoa sem nome';
                showMessage(`CPF inválido para: ${nomePessoa}. Por favor, corrija o CPF.`, false);
            } else {
                cpfInput.removeClass('is-invalid');
            }
        }
    });
    
    if (!cpfValido) {
        return;
    }

    $('body').addClass('loading');

    obterLocalizacaoDoUsuario()
        .then((localizacao) => {
            processarEnvioAbordagem(localizacao);
        })
        .catch((error) => {
            console.warn('Localização não disponível:', error.message);
            processarEnvioAbordagem(null);
        });
}

function processarEnvioAbordagem(localizacao) {
    const pessoas = [];
    $('.pessoa-card').each(function(index) {
        let cpf = $(this).find('.cpf').val();
        let telefone = $(this).find('.telefone').val();
        
        if (cpf) cpf = cpf.replace(/\D/g, '');
        if (telefone) telefone = telefone.replace(/\D/g, '');
        
        const pessoa = {
            id_temp: index + 1,
            cpf: cpf,
            nome_completo: $(this).find('.nome').val(),
            data_nascimento: $(this).find('.dataNascimento').val(),
            tipo_envolvimento: $(this).find('.tipo').val(),
            telefone: telefone,
            sexo: $(this).find('.sexo').val(),
            email: $(this).find('.email').val(),
            observacoes: $(this).find('.observacoes').val()
        };
        pessoas.push(pessoa);
    });

    const documentos = [];
    let temArquivosParaUpload = false;
    const formData = new FormData();

    $('.documento-card').each(function(index) {
        const documentoCard = $(this);
        
        const dadosDocumento = {
            id_pessoa: documentoCard.find('.pertencePessoa').val() || null,
            tipo_documento: documentoCard.find('.tipoDocumento').val(),
            numero_documento: documentoCard.find('.numeroDocumento').val(),
            nome_titular: documentoCard.find('.nomeTitular').val(),
            validade: documentoCard.find('.validade').val(),
            situacao: documentoCard.find('.situacao').val(),
            observacoes: documentoCard.find('.observacoes').val()
        };
        
        const inputArquivos = documentoCard.find('.arquivosDocumento')[0];
        
        if (inputArquivos && inputArquivos.files && inputArquivos.files.length > 0) {
            temArquivosParaUpload = true;
            
            const files = Array.from(inputArquivos.files);

            const arquivos = [];

            files.forEach((file) => {
                arquivos.push(file);
            });

            const documentoBase = {
                ...dadosDocumento,
                arquivos: arquivos.map((f, i) => ({
                    nome: f.name,
                    index: i
                }))
            };

            const indiceDocumentoBase = documentos.length;
            documentos.push(documentoBase);

            files.forEach((file, i) => {
                formData.append('arquivosDocumento_' + indiceDocumentoBase, file);
            });
        } else {
            const documento = {
                ...dadosDocumento,
            };
            documentos.push(documento);
        }
    });

    const inputArquivosAbordagem = document.getElementById('imagensAbordagem');

    if (inputArquivosAbordagem && inputArquivosAbordagem.files.length > 0) {
        Array.from(inputArquivosAbordagem.files).forEach((file, index) => {
            formData.append('imagensAbordagem', file);
            formData.append('indicesImagensAbordagem_' + index, index);
        });

        temArquivosParaUpload = true;
    }

    let origemAbordagem = 'MANUAL';
    if (window.dadosAlerta) {
        origemAbordagem = 'ALERTA';
    } else if (window.dadosBlitzOstensiva) {
        origemAbordagem = 'BLITZ OSTENSIVA';
    }

    const dadosBase = {
        acao: 'salvarAbordagem',
        id_blitz_digital: $('#selectBlitz').val(),
        placa_veiculo: obterPlacaVeiculo(origemAbordagem),
        status: window.statusAbordagemAtual,
        observacoes: $('#observacoes').val(),
        pessoas: JSON.stringify(pessoas),
        documentos: JSON.stringify(documentos),
        origem_abordagem: origemAbordagem,
        id_resultado: $('#selectResultadoAbordagem').val(),
        id_veiculo_tempo_real: window.dadosBlitzOstensiva && window.dadosBlitzOstensiva.id_veiculo_tempo_real ? window.dadosBlitzOstensiva.id_veiculo_tempo_real : null,
        marca_veiculo: $('#marcaVeiculo').val(),
        modelo_veiculo: $('#modeloVeiculo').val(),
        tipo_veiculo: $('#tipoVeiculo').val(),
        cor_veiculo: $('#corVeiculo').val(),
        motivo_cancelamento: window.motivoLiberacaoTemp || null,
    };

    if (window.dadosAlerta) {
        // Modo alerta
        if (window.dadosAlerta.id_alerta) {
            dadosBase.id_alerta = window.dadosAlerta.id_alerta;
        }
        if (window.dadosAlerta.id_local) {
            dadosBase.id_local = window.dadosAlerta.id_local;
        }
        if (window.dadosAlerta.id_registro_fato) {
            dadosBase.id_registro_fato = window.dadosAlerta.id_registro_fato;
        }
    } else if (window.dadosBlitzOstensiva) {
        // Modo blitz ostensiva
        if (window.dadosBlitzOstensiva.id_veiculo_tempo_real) {
            dadosBase.id_veiculo_tempo_real = window.dadosBlitzOstensiva.id_veiculo_tempo_real;
        }
        if (window.dadosBlitzOstensiva.id_registro_fato_passagem) {
            dadosBase.id_registro_fato = window.dadosBlitzOstensiva.id_registro_fato_passagem;
        }
    }

    if (localizacao) {
        dadosBase.latitude = localizacao.latitude;
        dadosBase.longitude = localizacao.longitude;
    }

    if (window.registroFatoAssociado) {
        dadosBase.id_registro_fato = window.registroFatoAssociado;
    }

    if (temArquivosParaUpload) {
        Object.keys(dadosBase).forEach(key => {
            formData.append(key, dadosBase[key]);
        });
    }

    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'POST',
        data: temArquivosParaUpload ? formData : dadosBase,
        dataType: 'json',
        processData: !temArquivosParaUpload,
        contentType: temArquivosParaUpload ? false : 'application/x-www-form-urlencoded; charset=UTF-8',
        success: function(response) {
            if (response.sucesso) {
                showMessage(response.mensagem, true);
                setTimeout(() => {
                    window.location.href = 'listagem-abordagem.jsp';
                }, 1500);
            } else {
                showMessage(response.mensagem, false);
            }
        },
        error: function(xhr, status, error) {
            console.error('Erro ao salvar abordagem:', error);
            showMessage('Erro ao salvar abordagem.', false);
        },
        complete: function() {
            $('body').removeClass('loading');
        }
    });
}

function showMessage(message, isSuccess) {
    const messageBox = $('#messageBox');
    messageBox.removeClass('alert-success alert-danger').addClass('alert');
    
    if (isSuccess) {
        messageBox.addClass('alert-success');
    } else {
        messageBox.addClass('alert-danger');
    }
    
    messageBox.text(message).show();
    
    setTimeout(() => {
        messageBox.fadeOut();
    }, 5000);
}

function validarCPF(cpf) {
    cpf = cpf.replace(/\D/g, '');
    
    if (cpf.length !== 11) return false;
    
    // Verifica se é uma sequência de números iguais
    if (/^(\d)\1{10}$/.test(cpf)) return false;
    
    // Validação do primeiro dígito verificador
    let soma = 0;
    for (let i = 0; i < 9; i++) {
        soma += parseInt(cpf.charAt(i)) * (10 - i);
    }
    let resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpf.charAt(9))) return false;
    
    // Validação do segundo dígito verificador
    soma = 0;
    for (let i = 0; i < 10; i++) {
        soma += parseInt(cpf.charAt(i)) * (11 - i);
    }
    resto = (soma * 10) % 11;
    if (resto === 10 || resto === 11) resto = 0;
    if (resto !== parseInt(cpf.charAt(10))) return false;
    
    return true;
}

function configurarUploadDocumento(documentoId) {
    const docContainer = $('#' + documentoId);
    const inputArquivo = docContainer.find('.anexoArquivo');
    const btnSelecionar = docContainer.find('.btn-selecionar-arquivo');
    const nomeArquivoInput = docContainer.find('.nomeArquivo');
    const previewContainer = docContainer.find('.arquivo-preview');
    const previewNome = docContainer.find('.arquivo-nome');
    const btnRemover = docContainer.find('.btn-remover-arquivo');
    
    btnSelecionar.click(function() {
        if (isMobileApp() || /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
            // Dispositivo móvel ou webview
            inputArquivo.attr('capture', 'environment');
        }
        inputArquivo.click();
    });
    
    inputArquivo.change(function(e) {
        const file = e.target.files[0];
        if (file) {
            const maxSize = 10 * 1024 * 1024; // 10MB
            const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf'];
            
            if (!allowedTypes.includes(file.type)) {
                showMessage('Tipo de arquivo não permitido. Use JPG, PNG ou PDF.', false);
                inputArquivo.val('');
                nomeArquivoInput.val('');
                previewContainer.hide();
                return;
            }
            
            if (file.size > maxSize) {
                showMessage('Arquivo muito grande. Tamanho máximo: 10MB.', false);
                inputArquivo.val('');
                nomeArquivoInput.val('');
                previewContainer.hide();
                return;
            }
            
            nomeArquivoInput.val(file.name);
            previewNome.text(file.name);
            previewContainer.show();
        } else {
            // Quando o usuário cancela sem anexar nada
            inputArquivo.val('');
            nomeArquivoInput.val('');
            previewContainer.hide();
        }
    });
    
    btnRemover.click(function() {
        inputArquivo.val('');
        nomeArquivoInput.val('');
        previewContainer.hide();
    });
}

function isMobileApp() {
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'isMobileApp' && value === 'true') {
            return true;
        }
    }
    return false;
}

function carregarRegistrosFato() {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { acao: 'listarRegistrosFato' },
        dataType: 'json',
        success: function(response) {
            const select = $('#selectRegistroFato');
            select.empty();
            select.append('<option value="">Selecione um registro de fato</option>');
            
            if (response.registrosFato && response.registrosFato.length > 0) {
                response.registrosFato.forEach(function(registro) {
                    const texto = registro.descricao;
                    select.append($('<option></option>')
                        .val(registro.id)
                        .text(texto));
                });
            }
        },
        error: function(error) {
            console.error('Erro ao carregar registros de fato:', error);
        }
    });
}

function carregarDadosAlerta(idAlerta, idRegistroFato) {
    $('body').addClass('loading');
    
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'obterInformacoesAlerta',
            idAlerta: idAlerta
        },
        dataType: 'json',
        success: function(response) {
            if (response.id_alerta) {
                window.dadosAlerta = response;
                
                // Configurar campos ocultos ou preenchidos automaticamente
                configurarTelaModoAlerta();
                
                // Carregar blitz específicas para o local
                if (response.id_local) {
                    carregarBlitzPorLocal(response.id_local);
                } else {
                    // Se não tiver local, carregar todas as ativas
                    carregarBlitz();
                }
                
                Promise.all([
                    new Promise((resolve) => {
                        carregarTiposEnvolvimento();
                        resolve();
                    }),
                    new Promise((resolve) => {
                        carregarTiposDocumento();
                        resolve();
                    }),
                    new Promise((resolve) => {
                        carregarSituacoesDocumento();
                        resolve();
                    }),
                    new Promise((resolve) => {
                        carregarResultadosAbordagem();
                        resolve();
                    }),
                ]).then(() => {
                    setTimeout(() => {
                        $('body').removeClass('loading');
                        $('.overlay').hide();
                    }, 300);
                });
            } else {
                showMessage('Erro ao carregar informações do alerta.', false);
                carregarDadosIniciais(); // Fallback para modo manual
            }
        },
        error: function(error) {
            console.error('Erro ao carregar dados do alerta:', error);
            showMessage('Erro ao carregar dados do alerta. Carregando modo manual.', false);
            carregarDadosIniciais(); // Fallback para modo manual
        }
    });
}

function carregarDadosBlitzOstensiva(idBlitz, idVeiculoTempoReal) {
    $('body').addClass('loading');
    
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'obterInformacoesBlitzOstensiva',
            idBlitz: idBlitz,
            idVeiculoTempoReal: idVeiculoTempoReal
        },
        dataType: 'json',
        success: function(response) {
            if (response.id_blitz) {
                window.dadosBlitzOstensiva = response;
                
                configurarTelaModoBlitzOstensiva();
                
                Promise.all([
                    new Promise((resolve) => {
                        carregarBlitzEspecifica(response.id_blitz);
                        resolve();
                    }),
                    new Promise((resolve) => {
                        carregarTiposEnvolvimento();
                        resolve();
                    }),
                    new Promise((resolve) => {
                        carregarTiposDocumento();
                        resolve();
                    }),
                    new Promise((resolve) => {
                        carregarSituacoesDocumento();
                        resolve();
                    }),
                    new Promise((resolve) => {
                        carregarResultadosAbordagem();
                        resolve();
                    }),
                ]).then(() => {
                    setTimeout(() => {
                        $('body').removeClass('loading');
                        $('.overlay').hide();
                    }, 300);
                });
            } else {
                showMessage('Erro ao carregar informações da blitz ostensiva.', false);
                carregarDadosIniciais();
            }
        },
        error: function(error) {
            console.error('Erro ao carregar dados da blitz ostensiva:', error);
            showMessage('Erro ao carregar dados da blitz ostensiva. Carregando modo manual.', false);
            carregarDadosIniciais();
        }
    });
}

function configurarTelaModoAlerta() {
    $('#containerRegistroFato').hide();
    
    if (window.dadosAlerta.placa) {
        $('#placaVeiculo').val(window.dadosAlerta.placa);
        $('#placaVeiculo').prop('readonly', true);
        $('#placaVeiculo').prop('required', false);
        verificarPlacaCompleta($('#placaVeiculo'));
        buscarInformacoesVeiculoParaCriacao(window.dadosAlerta.placa);

        if ($('#containerEditarPlaca').length === 0) {
            $('#placaVeiculo').closest('.mb-3').append(`
                <div class="mt-2" id="containerEditarPlaca">
                    <button type="button" class="btn btn-link p-0" id="btnEditarPlaca">
                        Alterar placa
                    </button>
                    <div class="form-text">
                        Altere a placa somente se a identificação automática estiver incorreta.
                    </div>
                </div>
            `);
        }
    }
    
    $('#idAlertaHidden').val(window.dadosAlerta.id_alerta || '');
    $('#idLocalHidden').val(window.dadosAlerta.id_local || '');
    $('#idRegistroFatoHidden').val(window.dadosAlerta.id_registro_fato || '');
    
    $('h2').text('Nova Abordagem - Alerta');
    
    const badgeHtml = `
        <div class="alert alert-info mt-3" id="badgeAlerta">
            <i class="bi bi-info-circle"></i>
            Esta abordagem está sendo criada a partir de um alerta detectado.
            Alguns campos foram preenchidos automaticamente.
        </div>
    `;
    $('h2').after(badgeHtml);
}

function configurarTelaModoBlitzOstensiva() {
    $('#containerRegistroFato').hide();
    
    if (window.dadosBlitzOstensiva.placa) {
        // Se tem placa, exibe o campo preenchido e bloqueado
        $('#placaVeiculo').val(window.dadosBlitzOstensiva.placa);
        $('#placaVeiculo').prop('readonly', true);
        $('#placaVeiculo').prop('required', false);
        $('#placaVeiculo').closest('.mb-3').show();
        verificarPlacaCompleta($('#placaVeiculo'));
        buscarInformacoesVeiculoParaCriacao(window.dadosBlitzOstensiva.placa);

        if ($('#containerEditarPlaca').length === 0) {
            $('#placaVeiculo').closest('.mb-3').append(`
                <div class="mt-2" id="containerEditarPlaca">
                    <button type="button" class="btn btn-link p-0" id="btnEditarPlaca">
                        Alterar placa
                    </button>
                    <div class="form-text">
                        Altere a placa somente se a identificação automática estiver incorreta.
                    </div>
                </div>
            `);
        }
    } else {
        // Se NÃO tem placa, esconde o campo completamente
        $('#placaVeiculo').closest('.mb-3').hide();
        $('#containerEditarPlaca').remove();
    }
    
    $('#idBlitzHidden').val(window.dadosBlitzOstensiva.id_blitz || '');
    $('#idVeiculoTempoRealHidden').val(window.dadosBlitzOstensiva.id_veiculo_tempo_real || '');
    $('#idRegistroFatoPassagemHidden').val(window.dadosBlitzOstensiva.id_registro_fato_passagem || '');
    
    $('h2').text('Nova Abordagem - Blitz Ostensiva');
    
    const badgeHtml = `
        <div class="alert alert-info mt-3" id="badgeBlitzOstensiva">
            <i class="bi bi-info-circle"></i>
            Esta abordagem está sendo criada a partir de uma blitz ostensiva.
            ${window.dadosBlitzOstensiva.placa ? 'A placa foi identificada automaticamente.' : 'A placa não foi identificada.'}
        </div>
    `;
    $('h2').after(badgeHtml);
}

function carregarBlitzPorLocal(idLocal) {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'listarBlitzAtivasPorLocal',
            idLocal: idLocal
        },
        dataType: 'json',
        success: function(response) {
            const select = $('#selectBlitz');
            select.empty();
            
            if (response.blitzes && response.blitzes.length > 0) {
                select.append('<option value="">Selecione uma blitz</option>');
                response.blitzes.forEach(function(blitz) {
                    select.append($('<option></option>')
                        .val(blitz.id)
                        .text(blitz.nome_blitz));
                });
            } else {
                // Fallback para carregar todas as blitz
                carregarBlitz();
            }
        },
        error: function(error) {
            console.error('Erro ao carregar blitz por local:', error);
            // Fallback para carregar todas as blitz
            carregarBlitz();
        }
    });
}

function validarCampoPlaca() {
    const modoAlerta = window.dadosAlerta !== null;
    const modoBlitzOstensiva = window.dadosBlitzOstensiva !== null;
    const placa = $('#placaVeiculo').val();
    
    if (modoAlerta) {
        $('#placaVeiculo').prop('required', false);
        return true;
    } else if (modoBlitzOstensiva) {
        $('#placaVeiculo').prop('required', false);
        return true;
    } else {
        $('#placaVeiculo').prop('required', true);
        return placa && placa.trim() !== '';
    }
}

function carregarBlitzEspecifica(idBlitz) {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'obterBlitz',
            idBlitz: idBlitz
        },
        dataType: 'json',
        success: function(response) {
            const select = $('#selectBlitz');
            select.empty();
            
            if (response.blitz) {
                select.append(`<option value="${response.blitz.id}" selected>${response.blitz.nome_blitz}</option>`);
                select.prop('disabled', true);
            } else {
                carregarBlitz();
            }
        },
        error: function(error) {
            console.error('Erro ao carregar blitz específica:', error);
            carregarBlitz();
        }
    });
}

function obterPlacaVeiculo(origemAbordagem) {
    if (origemAbordagem === 'MANUAL') {
        return $('#placaVeiculo').val() || null;
    }

    if (origemAbordagem === 'ALERTA') {
        return $('#placaVeiculo').prop('readonly')
            ? (window.dadosAlerta && window.dadosAlerta.placa ? window.dadosAlerta.placa : null)
            : $('#placaVeiculo').val();
    }

    if (origemAbordagem === 'BLITZ OSTENSIVA') {
        return $('#placaVeiculo').prop('readonly')
            ? (window.dadosBlitzOstensiva && window.dadosBlitzOstensiva.placa ? window.dadosBlitzOstensiva.placa : null)
            : $('#placaVeiculo').val();
    }

    return null;
}

function renderizarPreviewImagensAbordagem() {
    const preview = $('#previewImagensAbordagem');
    const nomes = [];

    preview.empty();

    if (imagensAbordagemSelecionadas.length === 0) {
        $('#nomeArquivosAbordagem').val('');
        return;
    }

    imagensAbordagemSelecionadas.forEach((file, index) => {
        nomes.push(file.name);

        const reader = new FileReader();
        reader.onload = function (e) {
            preview.append(`
                <div class="col-4 col-md-3 mb-2 position-relative">
                    <img src="${e.target.result}"
                         class="img-fluid rounded border">

                    <button type="button"
                            class="btn btn-sm btn-danger position-absolute top-0 end-0 btn-remover-imagem-abordagem"
                            data-index="${index}">
                        <i class="bi bi-x"></i>
                    </button>
                </div>
            `);
        };
        reader.readAsDataURL(file);
    });

    $('#nomeArquivosAbordagem').val(nomes.join(', '));
}

function atualizarInputFileImagensAbordagem() {
    const input = document.getElementById('imagensAbordagem');
    const dataTransfer = new DataTransfer();

    imagensAbordagemSelecionadas.forEach(file => {
        dataTransfer.items.add(file);
    });

    input.files = dataTransfer.files;
}

function finalizarAbordagem(status) {
    const optionSelecionada = $('#selectResultadoAbordagem').find(':selected');
    const codigoResultado = optionSelecionada.val() ? optionSelecionada.text() : '';
    
    // Validações para LIBERADO
    if (status === 'LIBERADO') {
        // 6.2 - Não permitir liberar se for veículo apreendido
        if (codigoResultado === 'Veículo apreendido') {
            showMessage('Não é possível liberar um veículo apreendido.', false);
            return;
        }
        
        // 6.1 - Se for irregularidade administrativa, abre modal para motivo
        if (codigoResultado === 'Irregularidade administrativa') {
            window.statusAbordagemAtual = status;
            $('#modalMotivoLiberacao').modal('show');
            return;
        }
    }
    
    // Validações para RETIDO
    if (status === 'RETIDO') {
        // Não pode reter se for NENHUMA_IRREGULARIDADE ou ABORDAGEM_INCORRETA
        if (codigoResultado === 'Nenhuma irregularidade constatada' || 
            codigoResultado === 'Abordagem incorreta (veículo errado)') {
            showMessage('Este resultado não permite retenção do veículo.', false);
            return;
        }
    }
    
    window.statusAbordagemAtual = status;
    salvarAbordagem();
}

function configurarBotoesAcaoAbordagem() {
    const container = $('#acaoAbordagem');
    container.empty();

    container.append(`
        <button class="btn btn-primary btn-sm" onclick="finalizarAbordagem('LIBERADO')">
            <i class="bi bi-unlock"></i> Liberar
        </button>
        <button class="btn btn-danger btn-sm" onclick="finalizarAbordagem('RETIDO')">
            <i class="bi bi-lock"></i> Reter
        </button>
    `);
}

function carregarResultadosAbordagem() {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: {
            acao: 'listarResultadosAbordagem',
        },
        dataType: 'json',
        success: function (response) {
            const select = $('#selectResultadoAbordagem');
            select.empty();
            select.append('<option value="">Selecione</option>');

            if (response.resultados && response.resultados.length > 0) {
                response.resultados.forEach(function (item) {
                    select.append(
                        $('<option></option>')
                            .val(item.id)                    
                            .text(item.descricao)            
                            .data('id-status', item.id_status) 
                    );
                });
            }
        }
    });
}

function buscarInformacoesVeiculoParaCriacao(placa) {
    if (!placa || placa.length < 7) return;
    
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'obterInformacoesVeiculoPorPlaca',
            placa: placa,
        },
        dataType: 'json',
        success: function(response) {
            if (response && response.sucesso && response.veiculo) {
                const veiculo = response.veiculo;
                
                if (!$('#marcaVeiculo').val() && veiculo.marca) {
                    $('#marcaVeiculo').val(veiculo.marca.trim());
                }
                if (!$('#modeloVeiculo').val() && veiculo.modelo) {
                    $('#modeloVeiculo').val(veiculo.modelo.trim());
                }
                if (!$('#tipoVeiculo').val() && veiculo.tipo) {
                    $('#tipoVeiculo').val(veiculo.tipo.trim());
                }
                if (!$('#corVeiculo').val() && veiculo.cor) {
                    $('#corVeiculo').val(veiculo.cor.trim());
                }
            }
        },
        error: function(xhr, status, error) {
            console.error('Erro ao buscar informações do veículo:', error);
        }
    });
}

function configurarUploadMultiplasImagensDocumento(documentoId) {
    const docContainer = $('#' + documentoId);
    const inputArquivos = docContainer.find('.arquivosDocumento');
    const btnSelecionar = docContainer.find('.btn-selecionar-arquivos-documento');
    const nomeArquivosInput = docContainer.find('.nomeArquivosDocumento');
    const previewContainer = docContainer.find('.preview-arquivos-documento');
    
    // Inicializa o array no data do container
    if (!docContainer.data('arquivos')) {
        docContainer.data('arquivos', []);
    }
    
    btnSelecionar.click(function() {
        if (isMobileApp() || /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
            inputArquivos.attr('capture', 'environment');
        }
        inputArquivos.click();
    });
    
    inputArquivos.change(function(e) {
        const files = Array.from(e.target.files);
        let arquivosValidos = true;
        
        for (let file of files) {
            const maxSize = 10 * 1024 * 1024; // 10MB
            const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'application/pdf'];
            
            if (!allowedTypes.includes(file.type)) {
                showMessage('Tipo de arquivo não permitido. Use JPG, PNG ou PDF.', false);
                arquivosValidos = false;
                break;
            }
            
            if (file.size > maxSize) {
                showMessage('Arquivo muito grande. Tamanho máximo: 10MB.', false);
                arquivosValidos = false;
                break;
            }
        }
        
        if (arquivosValidos) {
            if (!isMobileApp()) {
                adicionarMetadadosExifWeb(files, function(filesComMetadados) {
                    let arquivosExistentes = docContainer.data('arquivos') || [];
                    arquivosExistentes = arquivosExistentes.concat(filesComMetadados);
                    docContainer.data('arquivos', arquivosExistentes);
                    
                    renderizarPreviewArquivosDocumento(documentoId, arquivosExistentes);
                    atualizarInputFileArquivosDocumento(documentoId, arquivosExistentes);
                    
                    const nomes = arquivosExistentes.map(f => f.name).join(', ');
                    nomeArquivosInput.val(nomes.substring(0, 100) + (nomes.length > 100 ? '...' : ''));
                });
            } else {
                let arquivosExistentes = docContainer.data('arquivos') || [];
                arquivosExistentes = arquivosExistentes.concat(files);
                docContainer.data('arquivos', arquivosExistentes);
                
                renderizarPreviewArquivosDocumento(documentoId, arquivosExistentes);
                atualizarInputFileArquivosDocumento(documentoId, arquivosExistentes);
                
                const nomes = arquivosExistentes.map(f => f.name).join(', ');
                nomeArquivosInput.val(nomes.substring(0, 100) + (nomes.length > 100 ? '...' : ''));
            }
        } else {
            inputArquivos.val('');
        }
    });
}

$(document).on('click', '.btn-remover-imagem-documento', function() {
    const index = $(this).data('index');
    const docContainer = $(this).closest('.documento-card');
    const documentoId = docContainer.attr('id');
    let arquivosSelecionados = docContainer.data('arquivos') || [];
    
    arquivosSelecionados.splice(index, 1);
    docContainer.data('arquivos', arquivosSelecionados);
    
    renderizarPreviewArquivosDocumento(documentoId, arquivosSelecionados);
    atualizarInputFileArquivosDocumento(documentoId, arquivosSelecionados);
    
    if (arquivosSelecionados.length === 0) {
        docContainer.find('.nomeArquivosDocumento').val('');
    } else {
        const nomes = arquivosSelecionados.map(f => f.name).join(', ');
        docContainer.find('.nomeArquivosDocumento').val(nomes.substring(0, 100) + (nomes.length > 100 ? '...' : ''));
    }
});

function renderizarPreviewArquivosDocumento(documentoId, arquivos) {
    const previewContainer = $('#' + documentoId).find('.preview-arquivos-documento');
    previewContainer.empty();
    
    if (!arquivos || arquivos.length === 0) {
        return;
    }
    
    previewContainer.attr('id', 'previewArquivosDocumento_' + documentoId);
    
    arquivos.forEach((file, index) => {
        const isPDF = file.type === 'application/pdf';
        const fileId = 'file_' + documentoId + '_' + index;
        
        if (isPDF) {
            previewContainer.append(`
                <div class="col-4 col-md-3 mb-2 position-relative" data-file-id="${fileId}">
                    <div class="pdf-preview border rounded p-2 text-center bg-light">
                        <i class="bi bi-file-pdf-fill" style="font-size: 3rem; color: #dc3545;"></i>
                        <small class="d-block text-truncate">${file.name}</small>
                    </div>
                    <button type="button"
                            class="btn btn-sm btn-danger position-absolute top-0 end-0 btn-remover-imagem-documento"
                            data-index="${index}">
                        <i class="bi bi-x"></i>
                    </button>
                </div>
            `);
        } else {
            const reader = new FileReader();
            reader.onload = function(e) {
                const existingDiv = previewContainer.find(`[data-file-id="${fileId}"]`);
                if (existingDiv.length === 0) {
                    previewContainer.append(`
                        <div class="col-4 col-md-3 mb-2 position-relative" data-file-id="${fileId}">
                            <img src="${e.target.result}"
                                 class="img-fluid rounded border"
                                 style="cursor: pointer;"
                                 onclick="abrirImagemModal(this.src, '${file.name}')">
                            <button type="button"
                                    class="btn btn-sm btn-danger position-absolute top-0 end-0 btn-remover-imagem-documento"
                                    data-index="${index}">
                                <i class="bi bi-x"></i>
                            </button>
                        </div>
                    `);
                }
            };
            reader.readAsDataURL(file);
        }
    });
}

function atualizarInputFileArquivosDocumento(documentoId, arquivos) {
    const input = $('#' + documentoId).find('.arquivosDocumento')[0];
    if (!input) return;
    
    const dataTransfer = new DataTransfer();
    
    arquivos.forEach(file => {
        dataTransfer.items.add(file);
    });
    
    input.files = dataTransfer.files;
}

function atualizarInputFileImagensDocumento(documentoId, imagens) {
    const input = $('#' + documentoId).find('.arquivosDocumento')[0];
    const dataTransfer = new DataTransfer();
    
    imagens.forEach(file => {
        dataTransfer.items.add(file);
    });
    
    input.files = dataTransfer.files;
}

// Função para iniciar captura OCR
function iniciarCapturaOCR() {
    if (!isMobileApp()) {
        showMessage('Funcionalidade disponível apenas no aplicativo mobile.', false);
        return;
    }

    const btn = document.getElementById('btnCapturarPlaca');
    if (btn) {
        btn.classList.add('ocr-capturing');
        btn.innerHTML = '<i class="bi bi-camera"></i> Capturando...';
    }

    // Enviar mensagem para o Flutter através do JavaScriptChannel
    if (window.FlutterOCRChannel) {
        window.FlutterOCRChannel.postMessage('start');
    }
}

// Callback que será chamado pelo Flutter quando o OCR retornar
window.receberPlacaOCR = function(plate, base64Image) {
    if (!plate || plate.length < 7) {
        showMessage('Placa inválida capturada.', false);
        
        const btn = document.getElementById('btnCapturarPlaca');
        if (btn) {
            btn.classList.remove('ocr-capturing');
            btn.innerHTML = '<i class="bi bi-camera"></i> Capturar placa';
        }
        return;
    }

    const inputPlaca = document.getElementById('placaVeiculo');
    inputPlaca.value = plate;
    inputPlaca.dispatchEvent(new Event('input', { bubbles: true }));
    
    verificarPlacaCompleta($(inputPlaca));
    buscarInformacoesVeiculoParaCriacao(plate);
    
    // Se tiver imagem, adicionar automaticamente
    if (base64Image) {
        try {
            // Converter base64 para blob
            const byteCharacters = atob(base64Image);
            const byteNumbers = new Array(byteCharacters.length);
            for (let i = 0; i < byteCharacters.length; i++) {
                byteNumbers[i] = byteCharacters.charCodeAt(i);
            }
            const byteArray = new Uint8Array(byteNumbers);
            
            const file = new File(
                [byteArray], 
                `placa_${plate}_${Date.now()}.jpg`, 
                { type: 'image/jpeg' }
            );
            
            // Adicionar às imagens selecionadas
            imagensAbordagemSelecionadas.push(file);
            
            // Atualizar preview
            renderizarPreviewImagensAbordagem();
            atualizarInputFileImagensAbordagem();
            
            showMessage('Placa capturada e imagem adicionada!', true);
        } catch (e) {
            console.error('Erro ao processar imagem:', e);
            showMessage('Placa capturada, mas erro ao adicionar imagem.', false);
        }
    } else {
        showMessage('Placa capturada com sucesso!', true);
    }
    
    const btn = document.getElementById('btnCapturarPlaca');
    if (btn) {
        btn.classList.remove('ocr-capturing');
        btn.innerHTML = '<i class="bi bi-camera"></i> Capturar placa';
    }
};