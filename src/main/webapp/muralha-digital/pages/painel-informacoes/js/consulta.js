function BuscarInformacoes(dispositivoId) {
	const url = "/MuralhaDigital/PainelInformacao/Camera";
	if (dispositivoId == null) {
		dispositivoId = 1;
	}

	const params = {
		dispositivoId: dispositivoId,
		acao: "ObterCamerasPorDispositivoId"
	};

	return $.ajax({
		type: "GET",
		url: url,
		data: params,
		dataType: "text",
		success: function(responseText) {
			const $xml = $($.parseXML(responseText));

			const sucesso = $xml.find("sucesso").text();
			const msg = $xml.find("msgResposta").text();

			if (sucesso === "true" || sucesso === "") {
				return responseText;
			} else {
				Swal.fire({
					icon: "error",
					title: "Erro",
					text: msg || "Erro na resposta da consulta."
				});
				return null;
			}
		},
		error: function() {
			Swal.fire({
				icon: "error",
				title: "Erro",
				text: "Erro ao processar requisição ao servidor!"
			});
			return null;
		},
		complete: function() {
			$("body").removeClass("loading");
		}
	});
}

function buscarLeituraPlacas(dataInicio, dataFim, cameras) {
	const url = "/MuralhaDigital/PainelInformacao/Camera";

	// transforma array de câmeras em CSV
	const camerasParam = cameras.join(",");

	const params = {
		acao: "buscarLeituraPlacas",
		dataInicio: dataInicio,
		dataFim: dataFim,
		cameras: camerasParam
	};

	return $.ajax({
		type: "GET",
		url: url,
		data: params,
		dataType: "text", // backend retorna XML
		success: function(responseText) {
			const $xml = $($.parseXML(responseText));

			const sucesso = $xml.find("sucesso").text();
			const msg = $xml.find("msgResposta").text();

			if (sucesso === "true" || sucesso === "") {
				return responseText;
			} else {
				Swal.fire({
					icon: "error",
					title: "Erro",
					text: msg || "Erro na resposta da consulta."
				});
				return null;
			}
		},
		error: function() {
			Swal.fire({
				icon: "error",
				title: "Erro",
				text: "Erro ao processar requisição ao servidor!"
			});
			return null;
		},
		complete: function() {
			$("body").removeClass("loading");
		}
	});
}

// Função para buscar totalizações no backend
async function obterTotalInformacoes(periodo = "24") {
	const response = await fetch(
		`/MuralhaDigital/PainelInformacao/TotalInformacoes?acao=ObterTotalInformacoes&periodo=${periodo}`,
		{ method: "GET", headers: { "Accept": "application/xml" } }
	);

	if (!response.ok) throw new Error("Erro ao buscar totalizações");

	const xmlText = await response.text();
	const parser = new DOMParser();
	const xmlDoc = parser.parseFromString(xmlText, "application/xml");

	const indicadoresNodes = xmlDoc.getElementsByTagName("indicadores");
	const indicadores = [];
	for (let i = 0; i < indicadoresNodes.length; i++) {
		const ind = indicadoresNodes[i];
		indicadores.push({
			modulo: ind.getElementsByTagName("modulo")[0]?.textContent || "",
			titulo: ind.getElementsByTagName("titulo")[0]?.textContent || "",
			valor: parseInt(ind.getElementsByTagName("valor")[0]?.textContent || "0", 10)
		});
	}

	return indicadores; // apenas retorna os dados
}

async function consultaNotificacaoStatus() {
	try {
		const idsLocais = await obterEquipamentosPainelInformacao();
		if (!idsLocais || idsLocais.length === 0) {
			console.warn("Nenhum equipamento encontrado.");
			return [];
		}

		const notificacoes = await buscarStatusCameras(idsLocais);
		if (!notificacoes || notificacoes.length === 0) {
			console.log("Nenhuma notificação encontrada.");
			return [];
		}

		return notificacoes; // ✅ só devolve o resultado

	} catch (err) {
		console.error("Erro em buscarCamerasStatus:", err);
		return [];
	}
}

async function obterEquipamentosPainelInformacao() {
	var urlPesquisa = urlRoot + "MuralhaDigital/Equipamento";
	var dataStringPesquisa = "acao=obterListaEquipamentos";

	return new Promise((resolve, reject) => {
		$.ajax({
			type: "GET",
			url: urlPesquisa,
			data: dataStringPesquisa,
			dataType: "xml",

			success: function(data, textStatus, jqXHR) {
				var event = jqXHR.responseText;
				var xmlDoc = $.parseXML(event);
				var $xml = $(xmlDoc);

				var sucesso = $xml.find("sucesso").text();
				var msgResposta = $xml.find("msgResposta").text();

				sucesso = (sucesso === "true" || sucesso === "");

				if (sucesso) {
					const idsLocais = [];
					$xml.find("Equipamento").each(function() {
						const idLocal = $(this).find("idLocal").text();
						if (idLocal) idsLocais.push(parseInt(idLocal, 10));
					});
					resolve(idsLocais); // ✅ devolve os ids
				} else {
					WarningCsx_E_TimeOut_8000ms(msgResposta);
					resolve([]); // devolve vazio, mas não quebra
				}
			},

			error: function(jqXHR, textStatus, errorThrown) {
				AlertCsx_E_TimeOut_8000ms("Erro ao processar requisição ao servidor!!");
				reject(errorThrown);
			}
		});
	});
}

async function buscarStatusCameras(cameras) {
    const url = "/MuralhaDigital/PainelInformacao/Camera";
    const camerasParam = cameras.join(",");
    const params = {
        acao: "ObterStatusCamera",
        cameras: camerasParam,
    };

    return $.ajax({
        type: "GET",
        url: url,
        data: params,
        dataType: "text" // backend retorna XML
    }).then(responseText => {
        const $xml = $($.parseXML(responseText));
        const sucesso = $xml.find("sucesso").text();

        if (!(sucesso === "true" || sucesso === "")) {
            Swal.fire({
                icon: "error",
                title: "Erro",
                text: $xml.find("msgResposta").text() || "Erro na resposta da consulta."
            });
            return [];
        }

        const notificacoes = [];
        $xml.find("Dispositivo").each(function() {
            const conectado = $(this).find("conectado").text() === "true";
            const seComunicou = $(this).find("seComunicou").text() === "true";
            const nome = $(this).find("descDispositivo").text().trim();
            const ultimaDataRaw = $(this).find("ultimaDataRegistrada").text();
            const ultimaData = formatarDataHora(ultimaDataRaw); // 🔹 converte para dd/MM/yyyy HH:mm

            if (!conectado && !seComunicou) {
                notificacoes.push({
                    msg: `Fora do ar - ${nome}${ultimaData !== "-" ? " (última comunicação: " + ultimaData + ")" : ""}`,
                    tipo: "error",
                    prioridade: 2,
                    ultimaData: ultimaDataRaw
                });
            } else if (!conectado && seComunicou) {
                notificacoes.push({
                    msg: `Apresentou instabilidade - ${nome}${ultimaData !== "-" ? " (última comunicação: " + ultimaData + ")" : ""}`,
                    tipo: "warning",
                    prioridade: 1,
                    ultimaData: ultimaDataRaw
                });
            }
        });
        return notificacoes;
    }).catch(() => {
        Swal.fire({
            icon: "error",
            title: "Erro",
            text: "Erro ao processar requisição ao servidor!"
        });
        return [];
    });
}

async function buscarRecursoServidor() {
    const url = "/MuralhaDigital/PainelInformacao/Camera";
    const params = { acao: "ObterRecursoServidor" };

    return $.ajax({
        type: "GET",
        url: url,
        data: params,
        dataType: "text" // backend retorna XML
    })
    .then(responseText => {
        const $xml = $($.parseXML(responseText));
        const sucesso = $xml.find("sucesso").text();

        if (!(sucesso === "true" || sucesso === "")) {
            Swal.fire({
                icon: "error",
                title: "Erro",
                text: $xml.find("msgResposta").text() || "Erro na resposta da consulta."
            });
            return [];
        }

        // Monta array de indicadores a partir do XML
        const indicadores = [];
        $xml.find("RecursoServidorResponse > indicadores").each(function() {
            const nome = $(this).find("nome").text();
            const valorAtual = parseFloat($(this).find("valor").text());
            const unidade = $(this).find("unidade").text();
            const percentualText = $(this).find("percentual").text();
            const percentual = percentualText ? parseFloat(percentualText) : null;

            indicadores.push({ nome, valorAtual, unidade, percentual });
        });

        return indicadores;

    })
    .catch(() => {
        Swal.fire({
            icon: "error",
            title: "Erro",
            text: "Erro ao processar requisição ao servidor!"
        });
        return [];
    });
}


