const activeCharts = {};

function transformarDadosGraficoDiaSemana(dadosApi) {
    const mapaDias = {
        "Sunday": { nome: "Dom", ordem: 0, total: 0 }, "Monday": { nome: "Seg", ordem: 1, total: 0 },
        "Tuesday": { nome: "Ter", ordem: 2, total: 0 }, "Wednesday": { nome: "Qua", ordem: 3, total: 0 },
        "Thursday": { nome: "Qui", ordem: 4, total: 0 }, "Friday": { nome: "Sex", ordem: 5, total: 0 },
        "Saturday": { nome: "Sáb", ordem: 6, total: 0 }
    };
    if (dadosApi && dadosApi.length > 0) {
        dadosApi.forEach(item => { if (mapaDias[item.dia_semana]) { mapaDias[item.dia_semana].total += item.total_passagens; } });
    }
    return Object.values(mapaDias).sort((a, b) => a.ordem - b.ordem).map(diaInfo => ({ dia: diaInfo.nome, total: diaInfo.total }));
}

function transformarDadosHeatmap(dadosApi) {
    const diasDaSemana = [
        { chave: "Sunday", nome: "Dom", ordem: 0 }, { chave: "Monday", nome: "Seg", ordem: 1 },
        { chave: "Tuesday", nome: "Ter", ordem: 2 }, { chave: "Wednesday", nome: "Qua", ordem: 3 },
        { chave: "Thursday", nome: "Qui", ordem: 4 }, { chave: "Friday", nome: "Sex", ordem: 5 },
        { chave: "Saturday", nome: "Sáb", ordem: 6 }
    ];
    const dadosAgrupados = {};
    diasDaSemana.forEach(dia => { dadosAgrupados[dia.chave] = Array(24).fill(0); });
    let maximoPassagens = 0;
    if (dadosApi && dadosApi.length > 0) {
        dadosApi.forEach(item => {
            if (dadosAgrupados[item.dia_semana] && item.hora >= 0 && item.hora <= 23) {
                dadosAgrupados[item.dia_semana][item.hora] += item.total_passagens;
                if (dadosAgrupados[item.dia_semana][item.hora] > maximoPassagens) {
                    maximoPassagens = dadosAgrupados[item.dia_semana][item.hora];
                }
            }
        });
    }
    return diasDaSemana.sort((a, b) => a.ordem - b.ordem).map(dia => ({
        dia: dia.nome,
        valores: dadosAgrupados[dia.chave].map(totalPassagens => ({
            probabilidade: maximoPassagens > 0 ? totalPassagens / maximoPassagens : 0, passagens: totalPassagens
        }))
    }));
}

function transformarDadosTabelaPCL(dadosApi) {
    if (!dadosApi) return [];
    return dadosApi.sort((a, b) => b.total_passagens - a.total_passagens).map(item => ({ local: item.nome_local, total: item.total_passagens }));
}

function renderizarGraficoBarras(elementId, dados) {
    const canvas = document.getElementById(elementId);
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    if (activeCharts[elementId]) { activeCharts[elementId].destroy(); }
    const dadosFormatados = transformarDadosGraficoDiaSemana(dados);
    if (!dadosFormatados || dadosFormatados.length === 0 || dadosFormatados.every(d => d.total === 0)) {
        ctx.clearRect(0, 0, canvas.width, canvas.height); return;
    }
    activeCharts[elementId] = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: dadosFormatados.map(d => d.dia),
            datasets: [{ label: 'Total', data: dadosFormatados.map(d => d.total), backgroundColor: 'rgba(54, 162, 235, 0.6)', borderColor: 'rgba(54, 162, 235, 1)', borderWidth: 1 }]
        },
        options: { responsive: true, maintainAspectRatio: false, scales: { y: { beginAtZero: true } }, plugins: { legend: { display: false } } }
    });
}

function getColorForValue(value) {
    const c1 = { r: 255, g: 255, b: 204 }, c2 = { r: 253, g: 141, b: 60 }, c3 = { r: 177, g: 0, b: 38 };
    let r, g, b;
    if (value < 0.5) {
        const t = value * 2; r = Math.round(c1.r * (1 - t) + c2.r * t); g = Math.round(c1.g * (1 - t) + c2.g * t); b = Math.round(c1.b * (1 - t) + c2.b * t);
    } else {
        const t = (value - 0.5) * 2; r = Math.round(c2.r * (1 - t) + c3.r * t); g = Math.round(c2.g * (1 - t) + c3.g * t); b = Math.round(c2.b * (1 - t) + c3.b * t);
    }
    return `rgb(${r}, ${g}, ${b})`;
}

function renderizarGraficoCalorProbabilidade(containerId, dados) {
    const container = document.getElementById(containerId);
    if (!container || !dados) return;
    container.innerHTML = '';
    const table = document.createElement('table');
    table.className = 'heatmap-grid';
    const thead = document.createElement('thead');
    const headerRow = document.createElement('tr');
    headerRow.innerHTML = '<th></th>' + Array.from({ length: 24 }, (_, i) => `<th>${String(i).padStart(2, '0')}h</th>`).join('');
    thead.appendChild(headerRow); table.appendChild(thead);
    const tbody = document.createElement('tbody');
    dados.forEach(diaInfo => {
        const row = document.createElement('tr');
        row.innerHTML = `<td class="day-label">${diaInfo.dia}</td>` + diaInfo.valores.map(v => `<td class="data-cell" style="background-color: ${getColorForValue(v.probabilidade)}" title="Passagens: ${v.passagens}\nProbabilidade: ${v.probabilidade.toFixed(2)}"></td>`).join('');
        tbody.appendChild(row);
    });
    table.appendChild(tbody); container.appendChild(table);
}

function renderizarInfoVeiculo(info) {
    if (!info) return;
    document.getElementById('placa-veiculo').textContent = info.placa || 'N/A';
    document.getElementById('marca-veiculo').textContent = info.marca || 'N/A';
    document.getElementById('modelo-veiculo').textContent = info.modelo || 'N/A';
    document.getElementById('cor-veiculo').textContent = info.cor || 'N/A';
    document.getElementById('ano-veiculo').textContent = info.anoFabricacao || info.ano_fabricacao || 'N/A';
    const vehicleImage = document.querySelector('.vehicle-image-container img');
    if (vehicleImage && (info.imagem || info.image)) { vehicleImage.src = `data:image/jpeg;base64,${info.imagem || info.image}`; }
}

function formatarTempoMinutos(totalMinutos) {
    if (isNaN(totalMinutos) || totalMinutos < 0) return "0m";
    const dias = Math.floor(totalMinutos / 1440);
    const horas = Math.floor((totalMinutos % 1440) / 60);
    const minutos = Math.floor(totalMinutos % 60);
    let resultado = "";
    if (dias > 0) resultado += `${dias}d `;
    if (horas > 0) resultado += `${horas}h `;
    if (minutos >= 0) resultado += `${minutos}m`;
    return resultado.trim() || "0m";
}

function configurarFiltroGeralMancha(dadosPermanencia, dadosPassagens, totalPeriodoMinutos) {
    const selectMancha = document.getElementById('filtro-mancha-geral');
    const displayTempo = document.getElementById('tempo-permanencia-display');

    console.log("Dados de permanência recebidos:", JSON.parse(JSON.stringify(dadosPermanencia)));

    selectMancha.innerHTML = '';
    selectMancha.onchange = null;

    if (!dadosPermanencia || dadosPermanencia.length === 0) {
        selectMancha.classList.add('d-none');
        displayTempo.textContent = '0m';
        return;
    }

    const tempoTotalDentro = dadosPermanencia.reduce((total, item) => total + (item.tempo_total_estadia_min || 0), 0);
    const tempoForaMinutos = Math.max(0, totalPeriodoMinutos - tempoTotalDentro);

    console.log(`Tempo total do período: ${totalPeriodoMinutos.toFixed(2)} min.`);
    console.log(`Tempo DENTRO das manchas: ${tempoTotalDentro.toFixed(2)} min.`);
    console.log(`Tempo FORA da mancha: ${tempoForaMinutos.toFixed(2)} min.`);
    
    const options = ['<option value="">Visão Geral (Todas as Manchas)</option>'];
    if (tempoForaMinutos > 0) {
        options.push('<option value="fora">Tempo fora da mancha</option>');
    }

    const manchasMap = new Map();
    dadosPermanencia.forEach(item => {
        if (item.id_area_monitorada_entrada && !manchasMap.has(item.id_area_monitorada_entrada)) {
            manchasMap.set(item.id_area_monitorada_entrada, item.nome_area_monitorada_entrada || `Mancha ${item.id_area_monitorada_entrada}`);
        }
    });
    Array.from(manchasMap.entries()).sort((a, b) => a[1].localeCompare(b[1])).forEach(([id, nome]) => {
        options.push(`<option value="${id}">${nome}</option>`);
    });
    selectMancha.innerHTML = options.join('');
    
    displayTempo.textContent = formatarTempoMinutos(tempoTotalDentro);

    selectMancha.onchange = () => {
        const valor = selectMancha.value;
        let tempoExibido = 0;
        let dadosGrafico = dadosPassagens;

        if (valor === "") {
            tempoExibido = tempoTotalDentro;
        } else if (valor === "fora") {
            tempoExibido = tempoForaMinutos;
            dadosGrafico = dadosPassagens;
        } else {
            const idNum = parseInt(valor, 10);
            tempoExibido = dadosPermanencia
                .filter(item => item.id_area_monitorada_entrada === idNum)
                .reduce((total, item) => total + (item.tempo_total_estadia_min || 0), 0);
            dadosGrafico = dadosPassagens.filter(p => p.id_area_monitorada === idNum);
        }
        displayTempo.textContent = formatarTempoMinutos(tempoExibido);
        renderizarGraficoBarras('passagens-semana-chart', dadosGrafico);
    };

    if (options.length > 1) {
        selectMancha.classList.remove('d-none');
    } else {
        selectMancha.classList.add('d-none');
    }
}

function renderizarTabelaPCL(passagens) {
    const corpoTabela = document.getElementById('pcl-table-body');
    if (!corpoTabela) return;
    corpoTabela.innerHTML = '';
    if (passagens && passagens.length > 0) {
        passagens.forEach(p => { corpoTabela.innerHTML += `<tr><td>${p.local}</td><td>${p.total}</td></tr>`; });
    } else {
        corpoTabela.innerHTML = '<tr><td colspan="2">Nenhuma passagem registrada.</td></tr>';
    }
}

function renderizarLegendaGraficoCalor(containerId) {
    const container = document.getElementById(containerId);
    if (!container) return;
    container.innerHTML = `<div class="legend-title">Probabilidade</div><div class="legend-content"><div class="legend-color-bar"></div><div class="legend-labels"><span>Alta</span><span>Média</span><span>Baixa</span></div></div>`;
}