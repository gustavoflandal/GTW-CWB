$(document).ready(function () {
    const urlParams = new URLSearchParams(window.location.search);
    const idAbordagem = urlParams.get('id');
    
    if (idAbordagem) {
        carregarDadosAbordagem(idAbordagem);
    } else {
        window.location.href = 'listagem-abordagem.jsp';
    }

    $(document).on('click', '#btnRegistroFato', function() {
        const idRegistroFato = $(this).data('id');
        if (idRegistroFato) {
            abrirModalEditarRegistroDeFatoAbos(idRegistroFato);
        }
    });

    $('#btnAlerta').click(function () {
        const idAlerta = $(this).data('id');
        if (idAlerta) {
            window.location.href = "/muralha-digital/pages/alerta-tratativa/tratar-alerta.jsp?idAlerta=" + idAlerta;
        }
    });

    $('#btnVeiculoTempoReal').click(function () {
        const idVeiculoTempoReal = $(this).data('id');
        if (idVeiculoTempoReal) {
            window.location.href = "/muralha-digital/pages/consulta-veiculo/consulta.jsp?idVeiculoTempoReal=" + idVeiculoTempoReal;
            // AbrirDetalhesVeiculo(idVeiculoTempoReal);
        }
    });
});

function carregarDadosAbordagem(idAbordagem) {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'obterAbordagemDetalhada',
            idAbordagem: idAbordagem 
        },
        dataType: 'json',
        success: function(response) {
            if (response.abordagem) {
                $('#placaVeiculo').text(response.abordagem.placa_veiculo || '-');
                $('#marcaVeiculo').text(response.abordagem.marca_veiculo || '-');
                $('#modeloVeiculo').text(response.abordagem.modelo_veiculo || '-');
                $('#tipoVeiculo').text(response.abordagem.tipo_veiculo || '-');
                $('#corVeiculo').text(response.abordagem.cor_veiculo || '-');
                $('#nomeBlitz').text(response.abordagem.nome_blitz || '-');
                $('#nomeLocal').text(response.abordagem.nome_local || '-');
                $('#dataAbordagem').text(formatarData(response.abordagem.data_abordagem) || '-');
                $('#nomeAgente').text(response.abordagem.nome_agente || '-');
                $('#statusAbordagem').text(response.abordagem.status || '-');
                $('#resultadoAbordagem').text(response.abordagem.resultado || '-');
                $('#observacoes').text(response.abordagem.observacoes || '-');

                const motivo = response.abordagem.motivo_cancelamento;

                if (motivo && motivo.trim() !== '' && motivo.trim().toLowerCase() !== 'null') {
                    $('#motivoCancelamento').text(motivo);
                    $('#containerMotivoCancelamento').show();
                } else {
                    $('#containerMotivoCancelamento').hide();
                }
                
                // Verificar se tem id_alerta e mostrar botão
                if (response.abordagem.id_alerta) {
                    $('#btnAlerta').show().data('id', response.abordagem.id_alerta);
                } else {
                    $('#btnAlerta').hide();
                }
                
                // Verificar se tem id_veiculo_tempo_real e mostrar botão
                if (response.abordagem.id_veiculo_tempo_real) {
                    $('#btnVeiculoTempoReal').show().data('id', response.abordagem.id_veiculo_tempo_real);
                } else {
                    $('#btnVeiculoTempoReal').hide();
                }
                
                // Verificar se tem registro de fato
                if (response.abordagem.id_registro_fato) {
                    $('#btnRegistroFato').show().data('id', response.abordagem.id_registro_fato);
                } else {
                    $('#btnRegistroFato').hide();
                }
                
                carregarPessoasAbordagem(idAbordagem);                
                carregarDocumentosComImagens(idAbordagem);
                carregarImagensAbordagem(idAbordagem);
            }
        },
        error: function(error) {
            console.error('Erro ao carregar dados da abordagem:', error);
        }
    });
}

function carregarPessoasAbordagem(idAbordagem) {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { acao: 'listarPessoasAbordagem', idAbordagem: idAbordagem },
        dataType: 'json',
        success: function(response) {
            const $container = $('#containerPessoas');
            $container.empty();
            
            if (!response.pessoas || response.pessoas.length === 0) {
                $container.html('<p class="text-center">Nenhuma pessoa registrada</p>');
                return;
            }
            
            response.pessoas.forEach(function(pessoa) {
                const item = `
                <div class="card mb-2 pessoa-item">
                    <div class="card-body">
                        <h6 class="card-title">${pessoa.nome_completo || 'Nome não informado'}</h6>
                        <p class="card-text mb-1">
                            <small><strong>CPF:</strong> ${pessoa.cpf || '-'}</small><br>
                            <small><strong>Tipo:</strong> ${pessoa.tipo_envolvimento || '-'}</small><br>
                            <small><strong>Telefone:</strong> ${pessoa.telefone || '-'}</small><br>
                            <small><strong>Email:</strong> ${pessoa.email || '-'}</small><br>
                            <small><strong>Data de Nascimento:</strong> ${pessoa.data_nascimento ? formatarData(pessoa.data_nascimento, true) : '-'}</small>
                        </p>
                    </div>
                </div>`;
                $container.append(item);
            });
        },
        error: function(error) {
            console.error('Erro ao carregar pessoas:', error);
        }
    });
}

function carregarDocumentosComImagens(idAbordagem) {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: { 
            acao: 'listarDocumentosAbordagem',
            idAbordagem: idAbordagem 
        },
        dataType: 'json',
        success: function(response) {
            const container = $('#containerDocumentos');
            container.empty();

            verificarSeEstaNoApp();
            
            if (response.documentos && response.documentos.length > 0) {
                response.documentos.forEach(function(doc, docIndex) {
                    const arquivos = doc.arquivos || [];
                    const temArquivos = arquivos.length > 0;
                    
                    let arquivosHtml = '';
                    
                    if (temArquivos) {
                        let itensCarrossel = '';
                        
                        arquivos.forEach((arquivo, fileIndex) => {
                            const isPDF = arquivo.tipo_arquivo.includes('pdf') || arquivo.nome_arquivo_original.toLowerCase().endsWith('.pdf');
                            const isImage = arquivo.tipo_arquivo.includes('image') || arquivo.nome_arquivo_original.match(/\.(jpg|jpeg|png|gif|bmp)$/i);
                            
                            if (isImage) {
                                itensCarrossel += `
                                    <div class="carousel-item ${fileIndex === 0 ? 'active' : ''}">
                                        <img src="/MuralhaDigital/Blitz?acao=obterImagemDocumento&caminho=${encodeURIComponent(arquivo.caminho_arquivo)}"
                                             class="img-fluid d-block mx-auto arquivo-documento-carousel"
                                             onclick="abrirArquivoModal('${encodeURIComponent(arquivo.caminho_arquivo)}', '${arquivo.nome_arquivo_original}', '${arquivo.tipo_arquivo}')"
                                             title="${arquivo.nome_arquivo_original}">
                                    </div>`;
                            } else if (isPDF) {
                                itensCarrossel += `
                                    <div class="carousel-item ${fileIndex === 0 ? 'active' : ''}">
                                        <div class="pdf-carousel-item text-center p-3"
                                             onclick="abrirPdfModal('${encodeURIComponent(arquivo.caminho_arquivo)}', '${arquivo.nome_arquivo_original}')"
                                             style="cursor: pointer;">
                                            <i class="bi bi-file-pdf-fill" style="font-size: 5rem; color: #dc3545;"></i>
                                            <p class="mt-2 mb-0"><strong>${arquivo.nome_arquivo_original}</strong></p>
                                            <small class="text-muted">Clique para visualizar</small>
                                        </div>
                                    </div>`;
                            } else {
                                itensCarrossel += `
                                    <div class="carousel-item ${fileIndex === 0 ? 'active' : ''}">
                                        <div class="arquivo-generico-item text-center p-3"
                                             onclick="baixarArquivo('${encodeURIComponent(arquivo.caminho_arquivo)}', '${arquivo.nome_arquivo_original}')"
                                             style="cursor: pointer;">
                                            <i class="bi bi-file-earmark" style="font-size: 5rem; color: #6c757d;"></i>
                                            <p class="mt-2 mb-0"><strong>${arquivo.nome_arquivo_original}</strong></p>
                                            <small class="text-muted">Clique para baixar</small>
                                        </div>
                                    </div>`;
                            }
                        });
                        
                        if (itensCarrossel) {
                            const carouselId = `carouselDocumento_${docIndex}`;
                            arquivosHtml = `
                                <div class="documento-arquivos-container">
                                    <p class="mb-2"><strong>Arquivos do documento:</strong></p>
                                    <div id="${carouselId}" class="carousel slide documento-carousel" data-bs-ride="carousel" data-bs-interval="4000">
                                        <div class="carousel-inner">
                                            ${itensCarrossel}
                                        </div>
                                        ${arquivos.length > 1 ? `
                                            <button class="carousel-control-prev controle-arquivo" type="button" data-bs-target="#${carouselId}" data-bs-slide="prev">
                                                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                                <span class="visually-hidden">Anterior</span>
                                            </button>
                                            <button class="carousel-control-next controle-arquivo" type="button" data-bs-target="#${carouselId}" data-bs-slide="next">
                                                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                                <span class="visually-hidden">Próximo</span>
                                            </button>
                                        ` : ''}
                                    </div>
                                </div>`;
                        }
                    }
                    
                    const docHtml = `
                    <div class="documento-item p-3 mb-3">
                        <div class="row">
                            <div class="col-md-7">
                                <p><strong>Tipo:</strong> ${doc.tipo_documento || '-'}</p>
                                <p><strong>Número:</strong> ${doc.numero_documento || '-'}</p>
                                <p><strong>Titular:</strong> ${doc.nome_titular || '-'}</p>
                                <p><strong>Validade:</strong> ${formatarData(doc.validade, true)}</p>
                                <p><strong>Situação:</strong> ${doc.situacao || '-'}</p>
                                <p><strong>Observações:</strong> ${doc.observacoes || '-'}</p>
                            </div>
                            <div class="col-md-5">
                                ${arquivosHtml || '<p class="text-muted">Nenhum arquivo anexado</p>'}
                            </div>
                        </div>
                    </div>`;
                    container.append(docHtml);
                });
            } else {
                container.html('<p class="text-center text-muted">Nenhum documento registrado</p>');
            }
        },
        error: function(error) {
            console.error('Erro ao carregar documentos:', error);
            $('#containerDocumentos').html('<p class="text-center text-danger">Erro ao carregar documentos</p>');
        }
    });
}

function abrirImagemModal(src, titulo) {
    const isMobileApp = verificarSeEstaNoApp();
    
    // Extrair o caminho da URL para usar no download
    const urlParams = new URLSearchParams(src.split('?')[1]);
    const caminho = urlParams.get('caminho');
    const caminhoEncoded = encodeURIComponent(caminho || src);
    
    const modalHtml = `
    <div class="modal fade" id="imagemModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-lg modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">${titulo}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body text-center">
                    <img src="${src}" class="img-fluid" alt="${titulo}" style="max-height: 70vh;">
                </div>
                <div class="modal-footer">
                    ${gerarBotaoDownload(caminhoEncoded, titulo, isMobileApp)}
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                </div>
            </div>
        </div>
    </div>`;
    
    $('body').append(modalHtml);
    const modal = new bootstrap.Modal(document.getElementById('imagemModal'));
    modal.show();
    
    $('#imagemModal').on('hidden.bs.modal', function () {
        $(this).remove();
    });
}

function formatarData(dataString, apenasData = false) {
    if (!dataString) return '-';
    
    try {
        const dataSemMs = dataString.split('.')[0];
        const date = new Date(dataSemMs.replace(' ', 'T'));
        
        if (isNaN(date.getTime())) {
            const partes = dataString.split('-');
            if (partes.length === 3) {
                return `${partes[2]}/${partes[1]}/${partes[0]}`;
            }
            return dataString;
        }
        
        if (apenasData || (!dataString.includes(':') && !dataString.includes('T'))) {
            const dia = String(date.getDate()).padStart(2, '0');
            const mes = String(date.getMonth() + 1).padStart(2, '0');
            const ano = date.getFullYear();
            return `${dia}/${mes}/${ano}`;
        }
        
        if (dataString.includes(':') && (dataString.includes(' ') || dataString.includes('T'))) {
            const dia = String(date.getDate()).padStart(2, '0');
            const mes = String(date.getMonth() + 1).padStart(2, '0');
            const ano = date.getFullYear();
            const horas = String(date.getHours()).padStart(2, '0');
            const minutos = String(date.getMinutes()).padStart(2, '0');
            
            return `${dia}/${mes}/${ano} ${horas}:${minutos}`;
        }
        
        return date.toLocaleDateString('pt-BR');
    } catch (error) {
        console.error('Erro ao formatar data:', error);
        return dataString;
    }
}

function carregarImagensAbordagem(idAbordagem) {
    $.ajax({
        url: '/MuralhaDigital/Blitz',
        type: 'GET',
        data: {
            acao: 'listarImagensAbordagem',
            idAbordagem: idAbordagem
        },
        dataType: 'json',
        success: function (response) {
            const container = $('#containerImagensAbordagem');
            container.empty();

            if (response.imagens && response.imagens.length > 0) {
                $('#cardImagensAbordagem').show();

                let itens = '';

                response.imagens.forEach(function (imagem, index) {
                    itens += `
                        <div class="carousel-item ${index === 0 ? 'active' : ''}">
                            <img src="/MuralhaDigital/Blitz?acao=obterImagemAbordagem&caminho=${encodeURIComponent(imagem.caminho_arquivo)}&ext=${imagem.caminho_arquivo.split('.').pop()}"
                                class="img-fluid d-block mx-auto imagem-carousel"
                                onclick="abrirImagemModal(this.src, 'Imagem da Abordagem')">
                        </div>`;
                });

                const carouselHtml = `
                    <div id="carouselImagensAbordagem"
                         class="carousel slide"
                         data-bs-ride="carousel"
                         data-bs-interval="4000">
                         
                        <div class="carousel-inner">
                            ${itens}
                        </div>

                        <button class="carousel-control-prev controle-imagem"
                                type="button"
                                data-bs-target="#carouselImagensAbordagem"
                                data-bs-slide="prev">
                            <span class="carousel-control-prev-icon"></span>
                        </button>

                        <button class="carousel-control-next controle-imagem"
                                type="button"
                                data-bs-target="#carouselImagensAbordagem"
                                data-bs-slide="next">
                            <span class="carousel-control-next-icon"></span>
                        </button>
                    </div>`;

                container.append(carouselHtml);
            } else {
                $('#cardImagensAbordagem').hide();
            }
        },
        error: function () {
            $('#cardImagensAbordagem').hide();
        }
    });
}

function abrirArquivoModal(caminhoEncoded, titulo, tipo) {
    const isPDF = tipo.includes('pdf') || titulo.toLowerCase().endsWith('.pdf');
    const isMobileApp = verificarSeEstaNoApp();
    
    if (isPDF) {
        abrirPdfModal(caminhoEncoded, titulo);
    } else {
        const modalHtml = `
        <div class="modal fade" id="arquivoModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-lg modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">${titulo}</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body text-center">
                        <img src="/MuralhaDigital/Blitz?acao=obterImagemDocumento&caminho=${caminhoEncoded}" 
                             class="img-fluid" alt="${titulo}" style="max-height: 70vh;">
                    </div>
                    <div class="modal-footer">
                        ${gerarBotaoDownload(caminhoEncoded, titulo, isMobileApp)}
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                    </div>
                </div>
            </div>
        </div>`;
        
        $('#arquivoModal').remove();
        $('body').append(modalHtml);
        const modal = new bootstrap.Modal(document.getElementById('arquivoModal'));
        modal.show();
        
        $('#arquivoModal').on('hidden.bs.modal', function () {
            $(this).remove();
        });
    }
}

function abrirPdfModal(caminhoEncoded, titulo) {
    const isMobileApp = verificarSeEstaNoApp();
    
    const modalHtml = `
    <div class="modal fade" id="pdfModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-xl modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">${titulo}</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    ${isMobileApp ? 
                        `<div class="text-center p-4">
                            <i class="bi bi-file-pdf-fill" style="font-size: 5rem; color: #dc3545;"></i>
                            <p class="mt-3">Clique no botão abaixo para baixar o arquivo</p>
                        </div>` : 
                        `<iframe src="/MuralhaDigital/Blitz?acao=obterImagemDocumento&caminho=${caminhoEncoded}" 
                                style="width: 100%; height: 70vh;" frameborder="0"></iframe>`
                    }
                </div>
                <div class="modal-footer">
                    ${gerarBotaoDownload(caminhoEncoded, titulo, isMobileApp)}
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                </div>
            </div>
        </div>
    </div>`;
    
    $('#pdfModal').remove();
    $('body').append(modalHtml);
    const modal = new bootstrap.Modal(document.getElementById('pdfModal'));
    modal.show();
    
    $('#pdfModal').on('hidden.bs.modal', function () {
        $(this).remove();
    });
}

function verificarSeEstaNoApp() {
    var estaNoMobile = false;
    const cookies = document.cookie.split(';');
    for (let cookie of cookies) {
        const [name, value] = cookie.trim().split('=');
        if (name === 'isMobileApp' && value === 'true') {
            estaNoMobile = true;
        }
    }

    return estaNoMobile;
}

async function baixarNoApp(url, nomeArquivo) {
    let mensagemDebug = `=== INÍCIO DOWNLOAD ===\n`;
    mensagemDebug += `URL: ${url}\n`;
    mensagemDebug += `Arquivo: ${nomeArquivo}\n`;
    mensagemDebug += `Timestamp: ${new Date().toISOString()}\n\n`;
    
    try {
        const response = await fetch(url);
        
        if (!response.ok) {
            mensagemDebug += `\n❌ ERRO: Resposta não OK\n`;
            return;
        }

        const blob = await response.blob();
        const contentType = blob.type;
        
        mensagemDebug += `4. Blob criado\n`;
        mensagemDebug += `   Tamanho: ${blob.size} bytes\n`;
        mensagemDebug += `   Tipo: ${contentType}\n`;
        mensagemDebug += `   Nome: ${nomeArquivo}\n`;
        // mostrarDebug(mensagemDebug);

        const reader = new FileReader();

        reader.onloadend = function () {
            try {
                const base64 = reader.result.split(',')[1];
                
                const payload = JSON.stringify({
                    name: nomeArquivo,
                    base64: base64,
                    mimeType: contentType
                });
                
                if (typeof FlutterDownloadChannel !== 'undefined') {
                    FlutterDownloadChannel.postMessage(payload);
                }
                
            } catch (e) {
                console.error('Erro:', e);
            }
        };

        reader.readAsDataURL(blob);
        
    } catch (e) {
        console.error('Erro na requisição:', e);
    }
}

function gerarBotaoDownload(caminhoEncoded, titulo, isMobileApp) {
    const url = '/MuralhaDigital/Blitz?acao=downloadDocumento&caminho=' + caminhoEncoded;

    if (isMobileApp) {
        return `<button type="button"
                class="btn btn-primary"
                onclick="baixarNoApp('${url}', '${titulo}')">
                <i class="bi bi-download"></i> Baixar ${titulo}
            </button>`;
    } else {
        return `<a href="${url}"
                class="btn btn-primary"
                download="${titulo}"
                target="_blank">
                <i class="bi bi-download"></i> Baixar ${titulo}
            </a>`;
    }
}

function baixarArquivo(caminhoEncoded, nome) {
    const isMobileApp = verificarSeEstaNoApp();
    const url = '/MuralhaDigital/Blitz?acao=downloadDocumento&caminho=' + caminhoEncoded;

    // mostrarDebug(`baixarArquivo chamado\nisMobileApp: ${isMobileApp}\nURL: ${url}\nArquivo: ${nome}`);

    if (isMobileApp) {
        baixarNoApp(url, nome);
    } else {
        window.open(url, '_blank');
    }
}

// Modal de debug flutuante
function mostrarDebug(mensagem) {
    // Remove modal anterior se existir
    $('#debugModal').remove();
    
    const modalHtml = `
    <div class="modal fade" id="debugModal" tabindex="-1" aria-hidden="true" data-bs-backdrop="static">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header bg-warning">
                    <h5 class="modal-title">🐞 DEBUG</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <pre style="white-space: pre-wrap; word-break: break-all; max-height: 300px; overflow-y: auto;">${mensagem}</pre>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Fechar</button>
                    <button type="button" class="btn btn-warning" onclick="copiarDebug()">Copiar</button>
                </div>
            </div>
        </div>
    </div>`;
    
    $('body').append(modalHtml);
    const modal = new bootstrap.Modal(document.getElementById('debugModal'));
    modal.show();
    
    window.ultimaMensagemDebug = mensagem;
}

function copiarDebug() {
    if (window.ultimaMensagemDebug) {
        navigator.clipboard.writeText(window.ultimaMensagemDebug);
        alert('Copiado para área de transferência!');
    }
}