// =========================
// 1. Dados e Constantes
// =========================
let totalizacoesData = {};
let chartPainelInfo;

const iconesNotificacoes = {
	info: "bi-info-circle text-primary",
	warning: "bi-exclamation-triangle text-warning",
	error: "bi-x-circle text-danger",
	success: "bi-check-circle text-success"
};

// =========================
// 2. Funções Utilitárias
// =========================
function formatarDataHora(isoString) {
    if (!isoString) return "-";
    try {
        const data = new Date(isoString);
        if (isNaN(data.getTime())) return "-"; // caso inválido

        const dia = String(data.getDate()).padStart(2, "0");
        const mes = String(data.getMonth() + 1).padStart(2, "0");
        const ano = data.getFullYear();
        const horas = String(data.getHours()).padStart(2, "0");
        const minutos = String(data.getMinutes()).padStart(2, "0");

        return `${dia}/${mes}/${ano} ${horas}:${minutos}`;
    } catch {
        return "-";
    }
}

function adicionarNotificacao(mensagem, tipo = "info", dataHora) {
    $("#notificacoesContainer").append(`
        <div class="list-group-item">
            <div class="d-flex align-items-center">
                <i class="bi ${iconesNotificacoes[tipo]} me-2"></i>
                <div>
                    <div>${mensagem}</div>
                    <small class="text-muted">${dataHora}</small>
                </div>
            </div>
        </div>
    `);
}

// =========================
// 3. Renderização do Dashboard
// =========================
function renderTotalizacoes(periodo) {
  $("#painelTotalizacoes").empty();
  (totalizacoesData[periodo] || []).forEach(item => {
    const card = `
      <div class="col">
        <div class="card shadow-sm h-100 totalizacao-card text-white" 
             data-modulo="${item.modulo}" 
             style="cursor:pointer; background-color:${item.cor}; border-radius:12px;">
          <div class="card-body d-flex flex-column justify-content-between">
            <!-- Número grande no topo -->
            <p class="card-text display-6 fw-bold text-start mb-2">${item.valor}</p>
            
            <!-- Título embaixo (mostra inteiro) -->
            <h6 class="card-title fw-bold text-start mb-0">
              ${item.titulo}
            </h6>
          </div>
        </div>
      </div>`;
    $("#painelTotalizacoes").append(card);
  });
}

function renderPainelInfo(periodo, chartPainelInfo) {
	if (!chartPainelInfo) return;

	const dados = totalizacoesData[periodo] || [];
	const labels = dados.map(d => d.titulo || "");
	const values = dados.map(d => Number(d.valor) || 0);
	
	// <-- MUDANÇA AQUI: Apenas mapeia as cores que já existem nos dados
	const bg = dados.map(d => d.cor);

	chartPainelInfo.data = {
		labels: labels,
		datasets: [{ label: `Últimas ${periodo}h`, data: values, backgroundColor: bg }]
	};
	chartPainelInfo.update();
}

// =========================
// 4. Carregamento de Dados
// =========================
async function atualizarDashboard(periodo, chartPainelInfo) {
	renderTotalizacoes(periodo);
	renderPainelInfo(periodo, chartPainelInfo);
}

async function carregarTotalizacoes(periodo = "24") {
	try {
		mostrarLoading("cardTotalInforacoes");
		const indicadores = await obterTotalInformacoes(periodo);
		const dadosComCores = indicadores.map((item, i) => {
			return {
				...item,
				cor: `hsl(${i * 360 / indicadores.length}, 70%, 50%)`
			};
		});

		// Armazena os dados já com as cores
		totalizacoesData[periodo] = dadosComCores; 
		
		await atualizarDashboard(periodo, chartPainelInfo);
		esconderLoading("cardTotalInforacoes");
	} catch (err) {
		esconderLoading("cardTotalInforacoes");
		console.error("Erro ao carregar totalizações:", err);
		Swal.fire("Erro", "Não foi possível carregar as informações do painel.", "error");
	}
}

async function carregarNotificacaoCameras() {
    try {
		mostrarLoading("cardNotificacaoImportante");
        const notificacoes = await consultaNotificacaoStatus();
        $("#notificacoesContainer").empty();
		esconderLoading("cardNotificacaoImportante");
        if (!notificacoes.length) return;

        // Ordena: com data primeiro (mais recentes), depois sem data
        const ordenadas = [...notificacoes].sort((a, b) => {
            if (a.ultimaData && b.ultimaData) {
                return new Date(b.ultimaData) - new Date(a.ultimaData);
            }
            if (a.ultimaData) return -1;
            if (b.ultimaData) return 1;
            return 0;
        });

        ordenadas.forEach(n => {
            adicionarNotificacao(
                n.msg,
                n.tipo,
                formatarDataHora(n.ultimaData)
            );
        });
    } catch (err) {
		esconderLoading("cardNotificacaoImportante");
        console.error("Erro ao carregar notificações:", err);
    }
}

async function carregarRecursoServidorPainel() {
	try {
		mostrarLoading("recursoServidorCard");
		const indicadores = await buscarRecursoServidor();
		esconderLoading("recursoServidorCard");
		indicadores.forEach(ind => {
			switch (ind.nome) {
				case "Capacidade de Armazenamento":
					if (ind.percentual != null) {
						const percentual = parseFloat(ind.percentual); // garante que seja número
						$("#progressCapacidade")
							.css("width", percentual + "%")
							.text(percentual + "%");
						$("#capacidadeArmazenamento").text(percentual + "%");
					} else {
						$("#progressCapacidade").css("width", "0%").text("0%");
						$("#capacidadeArmazenamento").text("--");
					}
					break;
				case "Quantidade de Imagens":
					$("#totalImagens").text(ind.valorAtual.toLocaleString());
					break;
				case "Dias Armazenados":
					$("#diasArmazenados").text(ind.valorAtual + " dias");
					break;
				case "Passagens Veiculares Registradas":
					$("#totalPassagens").text(ind.valorAtual.toLocaleString() + " registros");
					break;
			}
		});
	} catch (err) {
		esconderLoadingArmazenamento("recursoServidorCard");
		console.error("Erro ao carregar recursos do servidor:", err);
	}
}

// =========================
// 5. Eventos
// =========================
window.abrirHistoricoNotificacoes = function() {
	const notificacoes = $("#notificacoesContainer").children();
	let htmlNotificacoes = "";
	notificacoes.each(function() { htmlNotificacoes += `<div class="mb-2">${$(this).html()}</div>`; });

	Swal.fire({
		title: "Histórico de Notificações",
		html: `<div style="max-height:400px; overflow-y:auto; text-align:left;">${htmlNotificacoes}</div>`,
		width: "700px",
		showCloseButton: true,
		confirmButtonText: "Fechar",
		customClass: { popup: 'p-0' }
	});
};

async function buscarPercentualPlacas() {
	const selected = $("#cameras").val() || [];
	const dataInicioStr = $("#datetimepickerInicial").val();
	const dataFimStr = $("#datetimepickerFinal").val();

	try {
		// validações
		if (selected.length === 0) {
			Swal.fire({ icon: "warning", title: "Atenção", text: "Selecione pelo menos uma câmera" });
			return;
		}

		if (!dataInicioStr || !dataFimStr) {
			Swal.fire({ icon: "warning", title: "Atenção", text: "Informe as datas de início e fim" });
			return;
		}

		if (new Date(dataFimStr) < new Date(dataInicioStr)) {
			Swal.fire({ icon: "error", title: "Datas inválidas", text: "A data final não pode ser menor que a inicial" });
			return;
		}

		// limpa gráfico e lista
		window.chartPlacas.data.labels = [];
		window.chartPlacas.data.datasets[0].data = [];
		window.chartPlacas.data.datasets[0].backgroundColor = [];
		$("#listaPercentuais").empty();

		mostrarLoading("cardLeituraPlacas");

		// chamada AJAX usando Promise
		const responseText = await buscarLeituraPlacas(dataInicioStr, dataFimStr, selected);

		const $xml = $($.parseXML(responseText));
		const percentuais = {};

		$xml.find("LeiturasPlaca > Leitura").each(function() {
			const idLocal = $(this).find("idLocal").text();
			const percentual = parseFloat($(this).find("percentual").text()) || 0;
			percentuais[idLocal] = percentual;
		});

		const selectedCameras = $("#cameras option:selected").map(function() {
			return {
				id: $(this).val(),
				nome: $(this).text()
			};
		}).get();

		// popula gráfico e lista
		selectedCameras.forEach((c, i) => {
			const valor = percentuais[c.id] ?? 0;
			$("#listaPercentuais").append(`
        <li class="list-group-item">
          <span class="camera-percentual">${valor}%</span>
          <span class="camera-nome">${c.nome}</span>
        </li>
      `);
			window.chartPlacas.data.labels.push(encurtarNome(c.nome));
			window.chartPlacas.data.datasets[0].data.push(valor);
			window.chartPlacas.data.datasets[0].backgroundColor.push(
				`hsl(${(i * 360 / selectedCameras.length)},70%,50%)`
			);
		});

		// Tooltip com nomes completos
		window.chartPlacas.options.plugins.tooltip.callbacks = {
			label: function(context) {
				const c = selectedCameras[context.dataIndex];
				const valor = context.raw;
				return `${c.nome}: ${valor}%`;
			}
		};

		window.chartPlacas.update();
	} catch (err) {
		console.error("Erro ao buscar percentuais:", err);
		Swal.fire({ icon: "error", title: "Erro", text: "Ocorreu um erro ao buscar as leituras." });
	} finally {
		esconderLoading("cardLeituraPlacas");
	}
}

function limparFiltrosPlacas() {
	// 1. Limpa datas
	$("#datetimepickerInicial").val("");
	$("#datetimepickerFinal").val("");

	// 2. Limpa seleção das câmeras
	$("#cameras").val([]).selectpicker("refresh");

	// 3. Limpa gráfico
	if (window.chartPlacas) {
		window.chartPlacas.data.labels = [];
		window.chartPlacas.data.datasets[0].data = [];
		window.chartPlacas.data.datasets[0].backgroundColor = [];
		window.chartPlacas.update();
	}

	// 4. Limpa lista
	$("#listaPercentuais").empty();
}


function encurtarNome(nome, max = 25) {
	if (nome.length <= max) return nome;
	return nome.substring(0, max) + "...";
}

// =========================
// 6. Inicialização
// =========================
$(document).ready(function() {
	obterEquipamentosGenerico("cameras");

	// Charts
	window.chartPlacas = new Chart(document.getElementById('chartPlacas'), {
		type: 'bar',
		data: { labels: [], datasets: [{ label: 'Leitura (%)', data: [], backgroundColor: [] }] },
		options: { responsive: true, maintainAspectRatio: false, scales: { y: { beginAtZero: true, max: 100 } } }
	});

	chartPainelInfo = new Chart(document.getElementById('chartPainelInfo'), {
		type: 'bar',
		data: { labels: [], datasets: [{ label: 'Últimas 24h', data: [], backgroundColor: [] }] },
		options: { responsive: true, maintainAspectRatio: false, scales: { y: { beginAtZero: true } } }
	});

	// Flatpickr
	flatpickr.localize(flatpickr.l10ns.pt);
	flatpickr("#datetimepickerInicial", { enableTime: true, dateFormat: "Y-m-d H:i:S", time_24hr: true });
	flatpickr("#datetimepickerFinal", { enableTime: true, dateFormat: "Y-m-d H:i:S", time_24hr: true });
	carregarRecursoServidorPainel();
	carregarNotificacaoCameras();
	// Inicializa Dashboard padrão (24h)
	carregarTotalizacoes();

	// Filtro de período
	$(".filtro-periodo").on("click", function() {
		const periodo = $(this).data("periodo");
		$(".filtro-periodo").removeClass("active");
		$(this).addClass("active");
		carregarTotalizacoes(periodo);
	});

	// Clique nos cards de totalizações
	$(document).on("click", ".totalizacao-card", function() {
		const modulo = $(this).data("modulo");

		// Pega período selecionado pelo botão ativo (24h ou 48h)
		const periodo = $(".filtro-periodo.active").data("periodo") || "24";
		
	const agora = new Date();
	const dataInicio = new Date(agora.getTime() - periodo * 60 * 60 * 1000);
	const pad = n => n.toString().padStart(2, "0");
	
	const dataCompleta = `${pad(dataInicio.getDate())}/${pad(dataInicio.getMonth() + 1)}/${dataInicio.getFullYear()} ${pad(dataInicio.getHours())}:${pad(dataInicio.getMinutes())}`;
	const queryString = encodeURIComponent(dataCompleta);

		Swal.fire({
			title: 'Mais informações',
			html: `<p><b>Indicador:</b> ${modulo}</p><p><b>Período:</b> Últimas ${periodo}h</p>`,
			icon: 'info',
			showCancelButton: true,
			confirmButtonText: 'Acessar a tela'
		}).then((result) => {
			if (result.isConfirmed) {
				// Mapeia o nome do indicador para a URL desejada
				let url = "";
				switch (modulo) {
					case "fatos":
						url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?dataInicio=" + queryString;
						break;
					case "fatosEncerrados":
						url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?dataInicio=" + queryString + "&situacao=2";
						break;
					case "fatosSemBoletim":
						url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?dataInicio=" + queryString + "&comboletim=SEM_BOLETIM";
						break;
					case "alarmesSimples":
						url = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?dataInicio=" + queryString;
						break;
					case "alarmesSupervisionados":
						url = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?dataInicio=" + queryString + "&supervisionado=true";
						break;
					case "alarmesNaoAssinados":
						url = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?dataInicio=" + queryString + "&pendente=true";
						break;
					case "alarmesSemConcordancia":
						url = "/muralha-digital/pages/consulta-alerta-ocorrencia/consulta.jsp?dataInicio=" + queryString;
						break;
					case "fatosAlterados":
						url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?dataUltimaAlteracao=" + queryString;
						break;
					case "veiculosRemovidos":
						url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?dataInicio=" + queryString;
						break;
					case "veiculosAlterados":
						url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?dataInicio=" + queryString;
						break;
					case "fatosComplemento":
						url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?dataInicio=" + queryString + "&necessitaComplemento=true";
						break;
					case "fatosAnotacoes":
						url = "/muralha-digital/pages/registro_fato/comBoletim/consulta.jsp?dataInicio=" + queryString + "&necessitaComplemento=true";
						break;
					default:
						url = "/MuralhaDigital/PainelInformacao";
				}

				// Inclui período na URL se precisar
				//url += `?periodo=${periodo}`;

				// Abre em nova aba
				window.open(url, "_blank");
			}
		});
	});
});

function mostrarLoading(cardId) {
	const card = document.getElementById(cardId);
	if (!card) return;
	const overlay = card.querySelector(".loading-overlay");
	if (overlay) overlay.classList.add("active");
}

function esconderLoading(cardId) {
	const card = document.getElementById(cardId);
	if (!card) return;
	const overlay = card.querySelector(".loading-overlay");
	if (overlay) overlay.classList.remove("active");
}

