<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.css">

<div class="container-fluid py-3">
  <div class="d-flex align-items-center mb-3 gap-2 flex-wrap">
    <h4 class="mb-0"><i class="bi bi-map me-2"></i>Mapa de Equipamentos</h4>
    <div class="ms-auto d-flex gap-2">
      <div class="form-check form-switch d-flex align-items-center me-2">
        <input class="form-check-input" type="checkbox" id="chkIncidentes" checked>
        <label class="form-check-label ms-1" for="chkIncidentes">Incidentes</label>
      </div>
      <a href="<%= request.getContextPath() %>/MuralhaDigital/Gis?formato=geojson"
         class="btn btn-sm btn-outline-primary">
        <i class="bi bi-download me-1"></i>GeoJSON
      </a>
      <a href="<%= request.getContextPath() %>/MuralhaDigital/Gis?formato=kml"
         class="btn btn-sm btn-outline-success">
        <i class="bi bi-download me-1"></i>KML
      </a>
    </div>
  </div>
  <div id="mapa" style="height:70vh;border-radius:8px;border:1px solid #dee2e6"></div>
</div>

<script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet/1.9.4/leaflet.min.js"></script>
<script>
var ctx = '<%= request.getContextPath() %>';
var map = L.map('mapa').setView([-12.97, -38.50], 12);

L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  attribution: '&copy; OpenStreetMap contributors',
  maxZoom: 18
}).addTo(map);

fetch(ctx + '/MuralhaDigital/Gis?formato=geojson&inline=1')
  .then(function(r) { return r.json(); })
  .then(function(gj) {
    if (!gj.features || gj.features.length === 0) {
      Swal.fire('Aviso', 'Nenhum equipamento com coordenadas cadastradas.', 'info');
      return;
    }

    var layer = L.geoJSON(gj, {
      pointToLayer: function(feature, latlng) {
        var t = feature.properties.total_passagens;
        var cor = t > 1000 ? '#dc3545' : t > 100 ? '#ffc107' : '#0d75bf';
        return L.circleMarker(latlng, {
          radius: 10, fillColor: cor, color: '#fff',
          weight: 2, opacity: 1, fillOpacity: 0.85
        });
      },
      onEachFeature: function(feature, layer) {
        var p = feature.properties;
        layer.bindPopup(
          '<strong>' + p.nome + '</strong><br>' +
          'ID Local: ' + p.id_local + '<br>' +
          'Passagens: ' + Number(p.total_passagens).toLocaleString('pt-BR')
        );
      }
    }).addTo(map);

    map.fitBounds(layer.getBounds().pad(0.1));
  })
  .catch(function(err) {
    Swal.fire('Erro', 'Falha ao carregar dados GIS: ' + err.message, 'error');
  });

var incidentesLayer = null;
var coresIncidente = {
  ACCIDENT: '#dc3545', JAM: '#fd7e14', HAZARD: '#ffc107', ALERT: '#17a2b8'
};

function carregarIncidentes() {
  if (!document.getElementById('chkIncidentes').checked) {
    if (incidentesLayer) { map.removeLayer(incidentesLayer); incidentesLayer = null; }
    return;
  }
  fetch(ctx + '/MuralhaDigital/Incidente?acao=geojson')
    .then(function(r) { return r.json(); })
    .then(function(gj) {
      if (incidentesLayer) map.removeLayer(incidentesLayer);
      if (!gj.features || gj.features.length === 0) return;
      incidentesLayer = L.geoJSON(gj, {
        pointToLayer: function(feature, latlng) {
          var cor = coresIncidente[feature.properties.tipo] || '#6c757d';
          return L.circleMarker(latlng, {
            radius: 8, fillColor: cor, color: '#fff',
            weight: 1.5, opacity: 0.9, fillOpacity: 0.75
          });
        },
        onEachFeature: function(feature, layer) {
          var p = feature.properties;
          layer.bindPopup(
            '<strong>' + p.tipo + '</strong><br>' +
            (p.desc || '') + '<br>' +
            'Severidade: ' + p.sev + '<br>' +
            '<small>' + p.dt + '</small>'
          );
        }
      }).addTo(map);
    });
}

document.getElementById('chkIncidentes').addEventListener('change', carregarIncidentes);
carregarIncidentes();
setInterval(carregarIncidentes, 300000);
</script>
