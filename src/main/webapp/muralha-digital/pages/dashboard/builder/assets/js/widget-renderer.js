var chartInstances = {};
var mapInstances = {};

function renderWidget(widget, dados, container) {
    container.innerHTML = '';
    if (!dados || !dados.rows) {
        container.innerHTML = '<div class="text-muted text-center p-2"><small>Sem dados</small></div>';
        return;
    }

    switch (widget.tipo) {
        case 'CARD':  renderCard(widget, dados, container);  break;
        case 'CHART': renderChart(widget, dados, container); break;
        case 'TABLE': renderTable(widget, dados, container); break;
        case 'MAP':   renderMap(widget, dados, container);   break;
        default:
            container.innerHTML = '<div class="text-muted text-center"><small>Tipo desconhecido</small></div>';
    }
}

function renderCard(widget, dados, container) {
    var valor = 0;
    if (dados.rows.length > 0 && dados.rows[0].length > 0) {
        valor = dados.rows[0][0];
    }

    var formatted;
    if (typeof valor === 'number') {
        formatted = valor % 1 === 0 ? valor.toLocaleString('pt-BR') : valor.toLocaleString('pt-BR', { minimumFractionDigits: 1, maximumFractionDigits: 2 });
    } else {
        formatted = valor;
    }

    var status = 'ok';
    var statusLabel = 'OK';
    if (widget.thresholdOk != null && widget.thresholdWarn != null) {
        var numVal = parseFloat(valor);
        if (widget.thresholdOk > widget.thresholdWarn) {
            if (numVal >= widget.thresholdOk) { status = 'ok'; statusLabel = 'OK'; }
            else if (numVal >= widget.thresholdWarn) { status = 'warn'; statusLabel = 'Atencao'; }
            else { status = 'crit'; statusLabel = 'Critico'; }
        } else {
            if (numVal <= widget.thresholdOk) { status = 'ok'; statusLabel = 'OK'; }
            else if (numVal <= widget.thresholdWarn) { status = 'warn'; statusLabel = 'Atencao'; }
            else { status = 'crit'; statusLabel = 'Critico'; }
        }
    }

    container.innerHTML = '<div class="db-card-valor">' + formatted + '</div>' +
        '<div class="db-card-status"><span class="badge status-' + status + '">' + statusLabel + '</span></div>';
}

function renderChart(widget, dados, container) {
    var key = 'chart-' + widget.id;
    if (chartInstances[key]) {
        chartInstances[key].destroy();
        delete chartInstances[key];
    }

    var canvas = document.createElement('canvas');
    container.appendChild(canvas);

    var labels = [];
    var values = [];
    for (var i = 0; i < dados.rows.length; i++) {
        labels.push(dados.rows[i][0] != null ? String(dados.rows[i][0]) : '');
        values.push(typeof dados.rows[i][1] === 'number' ? dados.rows[i][1] : parseFloat(dados.rows[i][1]) || 0);
    }

    var tipo = widget.tipoGrafico || 'bar';
    var cores = gerarCores(labels.length);

    var config = {
        type: tipo,
        data: {
            labels: labels,
            datasets: [{
                label: widget.titulo,
                data: values,
                backgroundColor: tipo === 'pie' || tipo === 'doughnut' ? cores : 'rgba(13, 117, 191, 0.7)',
                borderColor: tipo === 'pie' || tipo === 'doughnut' ? cores.map(function(c) { return c.replace('0.7', '1'); }) : '#0d75bf',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: { display: tipo === 'pie' || tipo === 'doughnut', position: 'bottom', labels: { font: { size: 10 } } }
            },
            scales: tipo === 'pie' || tipo === 'doughnut' ? {} : {
                y: { beginAtZero: true },
                x: { ticks: { font: { size: 10 }, maxRotation: 45 } }
            }
        }
    };

    chartInstances[key] = new Chart(canvas.getContext('2d'), config);
}

function renderTable(widget, dados, container) {
    var wrapper = document.createElement('div');
    wrapper.className = 'db-table-wrapper';
    var table = document.createElement('table');
    table.className = 'table table-sm table-striped table-hover mb-0';

    var thead = '<thead class="table-dark"><tr>';
    for (var i = 0; i < dados.headers.length; i++) {
        thead += '<th>' + escHtml(dados.headers[i]) + '</th>';
    }
    thead += '</tr></thead>';

    var tbody = '<tbody>';
    for (var r = 0; r < dados.rows.length; r++) {
        tbody += '<tr>';
        for (var c = 0; c < dados.rows[r].length; c++) {
            var val = dados.rows[r][c];
            tbody += '<td>' + (val != null ? escHtml(String(val)) : '') + '</td>';
        }
        tbody += '</tr>';
    }
    tbody += '</tbody>';

    table.innerHTML = thead + tbody;
    wrapper.appendChild(table);
    container.appendChild(wrapper);
}

function renderMap(widget, dados, container) {
    var key = 'map-' + widget.id;
    if (mapInstances[key]) {
        mapInstances[key].remove();
        delete mapInstances[key];
    }

    var mapDiv = document.createElement('div');
    mapDiv.className = 'db-map-container';
    container.appendChild(mapDiv);

    var map = L.map(mapDiv).setView([-12.97, -38.51], 12);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap'
    }).addTo(map);
    mapInstances[key] = map;

    var latIdx = -1, lngIdx = -1, labelIdx = -1;
    for (var h = 0; h < dados.headers.length; h++) {
        var hdr = dados.headers[h].toLowerCase();
        if (hdr === (widget.colunaLat || '').toLowerCase() || hdr.indexOf('lat') >= 0) latIdx = h;
        if (hdr === (widget.colunaLng || '').toLowerCase() || hdr.indexOf('lng') >= 0 || hdr.indexOf('lon') >= 0) lngIdx = h;
        if (hdr === (widget.colunaLabel || '').toLowerCase()) labelIdx = h;
    }

    if (latIdx < 0 || lngIdx < 0) {
        container.innerHTML = '<div class="text-muted text-center p-2"><small>Colunas lat/lng nao encontradas</small></div>';
        return;
    }

    var bounds = [];
    for (var r = 0; r < dados.rows.length; r++) {
        var lat = parseFloat(dados.rows[r][latIdx]);
        var lng = parseFloat(dados.rows[r][lngIdx]);
        if (isNaN(lat) || isNaN(lng)) continue;
        var label = labelIdx >= 0 ? String(dados.rows[r][labelIdx] || '') : '';
        var marker = L.marker([lat, lng]).addTo(map);
        if (label) marker.bindPopup(label);
        bounds.push([lat, lng]);
    }

    if (bounds.length > 0) {
        map.fitBounds(bounds, { padding: [20, 20] });
    }

    setTimeout(function() { map.invalidateSize(); }, 300);
}

function gerarCores(n) {
    var paleta = [
        'rgba(13,117,191,0.7)', 'rgba(40,167,69,0.7)', 'rgba(255,193,7,0.7)',
        'rgba(220,53,69,0.7)', 'rgba(111,66,193,0.7)', 'rgba(253,126,20,0.7)',
        'rgba(32,201,151,0.7)', 'rgba(214,51,132,0.7)', 'rgba(102,16,242,0.7)',
        'rgba(13,202,240,0.7)', 'rgba(108,117,125,0.7)', 'rgba(25,135,84,0.7)'
    ];
    var result = [];
    for (var i = 0; i < n; i++) {
        result.push(paleta[i % paleta.length]);
    }
    return result;
}

// escHtml definida em dashboard-grid.js
