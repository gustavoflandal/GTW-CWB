const app = {
    elements: {},
    state: { map: null, heatmap: null, passagensParaRota: [], routeAnimator: null },

    init() {
        this.elements = {
            mainContent: document.getElementById('main-content'),
            messageContainer: document.getElementById('message-container'),
            messageText: document.querySelector('#message-container p'),
            toastContainer: document.getElementById('toast-container'),
            inputDataInicial: document.getElementById('data-inicial'),
            inputHoraInicial: document.getElementById('hora-inicial'),
            inputDataFinal: document.getElementById('data-final'),
            inputHoraFinal: document.getElementById('hora-final'),
            inputPlaca: document.getElementById('placa'),
            selectIntervalo: document.getElementById('intervalo'),
            btnAnalisar: document.getElementById('btn-analisar'),
            btnLimpar: document.getElementById('btn-limpar'),
        };
        this.elements.btnAnalisar.addEventListener('click', () => this.handleAnalise());
        this.elements.btnLimpar.addEventListener('click', () => this.limparFiltros());
        this.elements.selectIntervalo.addEventListener('change', () => this.aplicarIntervalo());
        this.elements.inputPlaca.addEventListener('keyup', (e) => e.target.value = e.target.value.toUpperCase());
        this.elements.inputPlaca.addEventListener('keypress', (e) => { if (e.key === 'Enter') this.handleAnalise(); });
        [this.elements.inputDataInicial, this.elements.inputHoraInicial, this.elements.inputDataFinal, this.elements.inputHoraFinal]
            .forEach(c => c.addEventListener('blur', () => this.ajustarPeriodo()));
        this.carregarDadosPelaUrl();
    },
    
    resetDashboardComponents() {
        if (window.activeCharts && activeCharts['passagens-semana-chart']) {
            activeCharts['passagens-semana-chart'].destroy();
            delete activeCharts['passagens-semana-chart'];
        }
        const canvas = document.getElementById('passagens-semana-chart');
        if (canvas) canvas.getContext('2d').clearRect(0, 0, canvas.width, canvas.height);
        const displayTempo = document.getElementById('tempo-permanencia-display');
        if (displayTempo) displayTempo.textContent = '-';
        const selectMancha = document.getElementById('filtro-mancha-geral');
        if (selectMancha) {
            selectMancha.innerHTML = '';
            selectMancha.classList.add('d-none');
            selectMancha.onchange = null;
        }
    },

    async carregarDadosPelaUrl() {
        const urlParams = new URLSearchParams(window.location.search);
        const placa = urlParams.get('placa');
        const intervalo = urlParams.get('intervalo');
        
        if (placa && intervalo) {
            this.elements.inputPlaca.value = placa;
            this.elements.selectIntervalo.value = intervalo;
            
            this.aplicarIntervalo(); 
            await this.handleAnalise();
        } else {
            this.setUIState('initial');
        }
    },

    initMap() {
        if (window.google && window.google.maps) {
            this.state.map = new google.maps.Map(document.getElementById("map-canvas"), {
                zoom: 4, center: { lat: -14.235004, lng: -51.92528 },
            });
        }
    },

    aplicarIntervalo() {
        const dias = this.elements.selectIntervalo.value;
        if (!dias) return;
        const hoje = new Date();
        const dataInicio = new Date();
        dataInicio.setDate(hoje.getDate() - parseInt(dias));
        this.elements.inputDataInicial.value = dataInicio.toISOString().slice(0, 10);
        this.elements.inputDataFinal.value = hoje.toISOString().slice(0, 10);
        this.elements.inputHoraInicial.value = "00:00";
        this.elements.inputHoraFinal.value = "23:59";
        this.ajustarPeriodo();
    },

    ajustarPeriodo() {
        const { inputDataInicial, inputHoraInicial, inputDataFinal, inputHoraFinal } = this.elements;
        if (!inputDataInicial.value) return;
        if (!inputHoraInicial.value) inputHoraInicial.value = "00:00";
        if (!inputDataFinal.value) inputDataFinal.value = inputDataInicial.value;
        if (!inputHoraFinal.value) inputHoraFinal.value = "23:59";
        inputDataFinal.min = inputDataInicial.value;
        if (new Date(`${inputDataFinal.value}T${inputHoraFinal.value}`) < new Date(`${inputDataInicial.value}T${inputHoraInicial.value}`)) {
            inputDataFinal.value = inputDataInicial.value;
            inputHoraFinal.value = inputHoraInicial.value;
            this.showToast("A data final não pode ser anterior à data inicial.", 'warning');
        }
    },

    limparFiltros() {
        ['inputDataInicial', 'inputHoraInicial', 'inputDataFinal', 'inputHoraFinal', 'inputPlaca', 'selectIntervalo']
            .forEach(key => this.elements[key].value = '');
        window.history.pushState({}, document.title, window.location.pathname);
        this.resetDashboardComponents();
        if (this.state.heatmap) this.state.heatmap.setMap(null);
        this.setUIState('initial');
    },

    validarCampos() {
        if (!this.elements.inputPlaca.value || this.elements.inputPlaca.value.length !== 7) {
            this.showToast("A placa deve conter 7 caracteres.", 'warning'); return false;
        }
        if (!this.elements.inputDataInicial.value || !this.elements.inputDataFinal.value) {
            this.showToast("Por favor, preencha as datas.", 'warning'); return false;
        }
        return true;
    },

    async handleAnalise() {
        if (!this.validarCampos()) return;
        this.resetDashboardComponents();
        const { value: placa } = this.elements.inputPlaca;
        const { value: dataInicio } = this.elements.inputDataInicial;
        const { value: dataFim } = this.elements.inputDataFinal;
        const { value: horaInicio } = this.elements.inputHoraInicial;
        const { value: horaFim } = this.elements.inputHoraFinal;

        const startDateTime = new Date(`${dataInicio}T${horaInicio}`);
        const endDateTime = new Date(`${dataFim}T${horaFim}`);
        const totalPeriodoMinutos = (endDateTime - startDateTime) / (1000 * 60);

        const url = new URL(window.location);
        url.searchParams.set('placa', placa);
        if (this.elements.selectIntervalo.value) url.searchParams.set('intervalo', this.elements.selectIntervalo.value);
        window.history.pushState({}, '', url);

        this.setUIState('loading', `Buscando dados para a placa ${placa.toUpperCase()}...`);

        try {
            let [infoVeiculo, passagens, locaisPCL] = await Promise.all([
                apiClient.fetchInformacoesVeiculo(placa, dataInicio, dataFim, horaInicio, horaFim),
                apiClient.fetchPassagensIndividuais(placa, dataInicio, dataFim, horaInicio, horaFim),
                apiClient.fetchPassagensPorPcl(placa, dataInicio, dataFim, horaInicio, horaFim)
            ]);

            infoVeiculo = infoVeiculo || [];
            passagens = passagens || [];
            locaisPCL = locaisPCL || [];
            
            if (infoVeiculo.length === 0) {
                this.setUIState('no_results', 'Nenhuma informação encontrada para a placa no período.');
                return;
            }
            
            this.setUIState('results');
            renderizarInfoVeiculo(infoVeiculo[0]);

            const mapaCoordenadas = new Map(locaisPCL.map(l => [l.id_local, { lat: l.lat, lng: l.lng }]));
            this.state.passagensParaRota = passagens.map(p => ({ ...p, ...mapaCoordenadas.get(p.id_local) })).filter(p => p.lat && p.lng);

            this.setupButtons(infoVeiculo[0]);
            renderizarTabelaPCL(transformarDadosTabelaPCL(locaisPCL));
            this.renderizarMapaDeCalor(locaisPCL);

            let [passagensDia, tempoResult, passagensHora] = await Promise.all([
                apiClient.fetchPassagensPorDia(placa, dataInicio, dataFim, horaInicio, horaFim),
                apiClient.fetchEstadiaPorManchas(placa, dataInicio, dataFim, horaInicio, horaFim),
                apiClient.fetchPassagensPorDiaHora(placa, dataInicio, dataFim, horaInicio, horaFim)
            ]);
            
            passagensDia = passagensDia || [];
            tempoResult = tempoResult || [];
            passagensHora = passagensHora || [];

            renderizarGraficoBarras('passagens-semana-chart', passagensDia);
            configurarFiltroGeralMancha(tempoResult, passagensDia, totalPeriodoMinutos);
            const dadosHeatmap = transformarDadosHeatmap(passagensHora);
            renderizarGraficoCalorProbabilidade('probabilidade-heatmap-container', dadosHeatmap);
            renderizarLegendaGraficoCalor('heatmap-legend-container');

        } catch (error) {
            console.error("Falha ao carregar dados:", error);
            this.showToast(`Falha ao carregar os dados. Verifique o console para mais detalhes.`, 'danger');
            this.setUIState('error', 'Erro ao carregar os dados.');
        }
    },

    setupButtons(veiculoInfo) {
        const btnLinhaTempo = document.getElementById('btnAbrirLinhaTempo');
        if (btnLinhaTempo) {
            btnLinhaTempo.onclick = () => {
                if (typeof MostrarModalLinhaTempo !== 'function') return;
                MSG_CONTAINER_MODAL = "#modalLinhaTempoBody"; VOLTAR_MODAL_LT = false;
                MostrarModalLinhaTempo();
                ExecutarPesquisaLinhaTempo(`Linha do Tempo - ${veiculoInfo.placa}`, null, veiculoInfo.id_veiculo);
            };
        }
        const btnMapa = document.getElementById('btnMostrarMapa');
        if (btnMapa) {
            btnMapa.onclick = () => {
                const modalEl = document.getElementById('modalVisualizarMapa');
                const modal = bootstrap.Modal.getOrCreateInstance(modalEl);
                modalEl.addEventListener('shown.bs.modal', () => this.renderizarMapaDeRota(this.state.passagensParaRota), { once: true });
                modal.show();
            };
        }
    },

    renderizarMapaDeRota(passagensRota) {
        if (typeof IniciarMapa !== 'function' || typeof PopulaMarkers !== 'function' || typeof LimparTabelaPassagensMapa !== 'function') {
            return;
        }

        try {
            if (typeof ExibirSpinnerModalMapa === 'function') ExibirSpinnerModalMapa();

            IniciarMapa();
            LimparTabelaPassagensMapa();
            
            const passagensParaModal = passagensRota.map(p => ({
                idDispositivo: p.id_local,
                serieEquipamento: p.serie_equipamento,
                descDispositivo: p.nome_local,
                latitude: p.lat,
                longitude: p.lng,
                placa: p.placa,
                dataVeicFormatada: new Date(p.data).toLocaleString('pt-BR'),
                velocidade: p.velocidade,
                lat: p.lat,
                lng: p.lng
            }));

            window.passagens = passagensParaModal;
            
            const tableRef = document.getElementById("tabelaPassagensMapa")?.getElementsByTagName('tbody')[0];
            if (tableRef) {
                passagensParaModal.forEach(p => {
                    let row = tableRef.insertRow();
                    row.insertCell(0).innerHTML = `<small>${p.serieEquipamento} - ${p.descDispositivo}</small>`;
                    row.insertCell(1).innerHTML = `<small>${p.idPista || '-'}</small>`;
                    row.insertCell(2).innerHTML = `<small>${p.dataVeicFormatada}</small>`;
                    row.insertCell(3).innerHTML = `<small>${p.placa}</small>`;
                    row.insertCell(4).innerHTML = `<small>${p.velocidade}</small>`;
                });
            }
            
            PopulaMarkers();

        } finally {
            if (typeof OcultarSpinnerModalMapa === 'function') {
                setTimeout(() => OcultarSpinnerModalMapa(), 500); 
            }
        }
    },

    renderizarMapaDeCalor(pontos) {
        if (!this.state.map) return;
        if (this.state.heatmap) this.state.heatmap.setMap(null);
        if (!pontos || pontos.length === 0) return;
        const dadosMapa = pontos.filter(p => p.lat && p.lng).map(p => ({
            location: new google.maps.LatLng(p.lat, p.lng),
            weight: p.total_passagens
        }));
        if (dadosMapa.length === 0) return;
        const bounds = new google.maps.LatLngBounds();
        dadosMapa.forEach(p => bounds.extend(p.location));
        this.state.map.fitBounds(bounds);
        this.state.heatmap = new google.maps.visualization.HeatmapLayer({
            data: dadosMapa, map: this.state.map, radius: 40, opacity: 0.8
        });
    },

    setUIState(state, message = '') {
        this.elements.mainContent.classList.add('hidden');
        this.elements.messageContainer.classList.add('hidden');
        switch (state) {
            case 'results': this.elements.mainContent.classList.remove('hidden'); break;
            case 'initial': message = 'Preencha os filtros e clique em "Analisar" para iniciar.';
            case 'loading': case 'no_results': case 'error':
                this.elements.messageContainer.classList.remove('hidden');
                this.elements.messageText.textContent = message;
                break;
        }
    },

    showToast(message, type = 'info') {
        const toastId = 'toast-' + Math.random().toString(36).substring(2, 9);
        const toastHTML = `<div id="${toastId}" class="toast align-items-center text-bg-${type} border-0" role="alert"><div class="d-flex"><div class="toast-body">${message}</div><button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast"></button></div></div>`;
        this.elements.toastContainer.insertAdjacentHTML('beforeend', toastHTML);
        const toastElement = document.getElementById(toastId);
        const toast = new bootstrap.Toast(toastElement, { delay: 3000 });
        toastElement.addEventListener('hidden.bs.toast', () => toastElement.remove());
        toast.show();
    }
};

function initMapGlobal() { app.initMap(); }
document.addEventListener('DOMContentLoaded', () => app.init());