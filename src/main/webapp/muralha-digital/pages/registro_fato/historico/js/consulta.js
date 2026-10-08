/**
 * Busca o histórico de um registro de fato.
 * Esta função é assíncrona e retorna uma Promise com a lista de históricos.
 * @param {number} idRegistro O ID do registro a ser consultado.
 * @returns {Promise<Array>} Uma Promise que resolve com um array de objetos de histórico.
 */
function obterHistoricoPorIdRegistro(idRegistro) {
	const params = new URLSearchParams({
		acao: 'obterHistoricoPorId',
		idRegistro: idRegistro
	});
    return new Promise((resolve, reject) => {
        const url = `/MuralhaDigital/RegistroDeFato/Historico`;

        $.ajax({
            type: "GET",
            url: url,
            dataType: "xml",
			data: params.toString(),
            success: function(data, textStatus, jqXHR) {
                try {
                    const xmlText = jqXHR.responseText;
                    const parser = new DOMParser();
                    const xmlDoc = parser.parseFromString(xmlText, "application/xml");
                    
                    if (xmlDoc.getElementsByTagName("parsererror").length) {
                        throw new Error("Erro ao parsear o XML de resposta.");
                    }
					
					const sucessoNode = xmlDoc.querySelector("sucesso");
					const isSuccess = sucessoNode ? sucessoNode.textContent === 'true' : true;
					
					if (!isSuccess) {
					    const msgNode = xmlDoc.querySelector("msgResposta");
					    const mensagemDeErro = msgNode ? msgNode.textContent : "Ocorreu um erro na solicitação.";
					    
					    reject(new Error(mensagemDeErro));
					    return;
					}

                    const historicoNodes = xmlDoc.querySelectorAll("RegistroDeFatoHistorico");
                    const listaHistoricos = [];

                    for (let node of historicoNodes) {
                        const item = {
                            idHistorico: node.querySelector("idHistorico")?.textContent || 'N/A',
                            tipoOperacao: node.querySelector("tipoOperacao")?.textContent || 'N/A',
                            dataAlteracao: node.querySelector("dataAlteracao")?.textContent || 'N/A',
                            idUsuario: node.querySelector("idUsuario")?.textContent || 'N/A',
                            dadosAnteriores: node.querySelector("dadosAnteriores")?.textContent || '{}',
                            dadosNovos: node.querySelector("dadosNovos")?.textContent || '{}',
							nomeUsuario: node.querySelector("nomeUsuario")?.textContent || '{}',
                        };
                        listaHistoricos.push(item);
                    }
                    
                    // A Promise foi cumprida com sucesso. Entregamos os dados.
                    resolve(listaHistoricos);

                } catch (e) {
                    // Se ocorrer um erro no processamento, a Promise é rejeitada.
                    reject(e);
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                // A chamada AJAX falhou. A Promise é rejeitada com o erro.
                reject(new Error(`Erro na requisição: ${errorThrown}`));
            }
        });
    });
}

/**
 * Carrega as listas de todos os usuários e grupos do sistema e as armazena
 * em variáveis globais para serem usadas como "dicionários" de tradução em toda a aplicação.
 * Esta função NÃO manipula nenhum elemento HTML.
 * @returns {Promise<void>} Uma promise que é resolvida quando os dados estão prontos.
 */
function carregarDadosDeSuporte() {
    return new Promise((resolve, reject) => {
        const url = "/MuralhaDigital/RegistroDeFato/Grupo?acao=obterTodos";

        $.ajax({
            type: "GET",
            url: url,
            dataType: "xml",
            success: function(data, textStatus, jqXHR) {
                try {
                    const xmlDoc = new DOMParser().parseFromString(jqXHR.responseText, "application/xml");

                    // === PROCESSAR GRUPOS ===
                    const grupos = xmlDoc.getElementsByTagName("grupo");
                    window.gruposDisponiveis = []; // Inicializa a variável global

                    for (let i = 0; i < grupos.length; i++) {
                        const grupo = grupos[i];
                        const idGrupo = grupo.getElementsByTagName("id_grupo")[0].textContent.trim();
                        const descricao = grupo.getElementsByTagName("descricao")[0].textContent.trim();
                        window.gruposDisponiveis.push({ id: idGrupo, nome: descricao });
                    }
                    
                    // === PROCESSAR USUÁRIOS ===
                    const usuarios = xmlDoc.getElementsByTagName("usuario");
                    window.usuariosDisponiveis = []; // Inicializa a variável global
                    
                    for (let i = 0; i < usuarios.length; i++) {
                        const usuario = usuarios[i];
                        const idUsuario = usuario.getElementsByTagName("id_usuario")[0]?.textContent.trim();
                        const nome = usuario.getElementsByTagName("nome")[0]?.textContent.trim();
                        const login = usuario.getElementsByTagName("usuario")[0]?.textContent.trim();

                        if (idUsuario && nome) {
                            window.usuariosDisponiveis.push({ id: idUsuario, nome: nome, login: login });
                        }
                    }
                    resolve(); // Avisa que a operação terminou com sucesso

                } catch (e) {
                    console.error("Erro ao processar XML de usuários/grupos:", e);
                    reject(e); // Avisa que deu erro no processamento
                }
            },
            error: function(jqXHR, textStatus, errorThrown) {
                console.error("Erro ao carregar usuários/grupos:", errorThrown);
                reject(new Error(errorThrown)); // Avisa que deu erro na requisição
            }
        });
    });
}