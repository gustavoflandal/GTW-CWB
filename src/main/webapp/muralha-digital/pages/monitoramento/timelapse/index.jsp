<%@ page contentType="text/html; charset=UTF-8" language="java" pageEncoding="UTF-8" %>
<%@ include file="/muralha-digital/utils/credenciais/cabecalho_bootstrap_simples.jsp" %>

<div class="container-fluid py-3">
  <h4 class="mb-3"><i class="bi bi-play-circle me-2"></i>Time-lapse de Passagens</h4>

  <div class="card mb-3">
    <div class="card-body">
      <div class="row g-2 align-items-end">
        <div class="col-md-3">
          <label class="form-label">Equipamento</label>
          <select id="selEquip" class="form-select"></select>
        </div>
        <div class="col-md-3">
          <label class="form-label">Inicio</label>
          <input type="datetime-local" id="dtInicio" class="form-control">
        </div>
        <div class="col-md-3">
          <label class="form-label">Fim</label>
          <input type="datetime-local" id="dtFim" class="form-control">
        </div>
        <div class="col-md-2">
          <label class="form-label">FPS</label>
          <input type="range" id="fps" class="form-range" min="1" max="10" value="3">
          <small id="lblFps" class="text-muted">3 fps</small>
        </div>
        <div class="col-md-1">
          <button class="btn btn-primary w-100" onclick="carregar()">
            <i class="bi bi-search"></i>
          </button>
        </div>
      </div>
    </div>
  </div>

  <div class="row">
    <div class="col-md-8">
      <div class="card">
        <div class="card-body text-center">
          <img id="frameImg" src="" alt="frame"
               style="max-width:100%;max-height:500px;display:none;border-radius:4px;">
          <div id="semImagem" class="text-muted py-5">
            <i class="bi bi-camera-video display-4"></i>
            <p class="mt-2">Selecione um equipamento e periodo</p>
          </div>
        </div>
        <div class="card-footer">
          <div class="d-flex align-items-center gap-2">
            <button class="btn btn-sm btn-outline-secondary" onclick="anterior()">
              <i class="bi bi-skip-backward"></i>
            </button>
            <button class="btn btn-sm btn-primary" id="btnPlay" onclick="togglePlay()">
              <i class="bi bi-play-fill"></i>
            </button>
            <button class="btn btn-sm btn-outline-secondary" onclick="proximo()">
              <i class="bi bi-skip-forward"></i>
            </button>
            <input type="range" id="slider" class="form-range flex-grow-1" min="0" value="0"
                   oninput="irPara(this.value)">
            <span id="lblFrame" class="text-muted small">0/0</span>
          </div>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card h-100">
        <div class="card-header">Informacoes do frame</div>
        <div class="card-body">
          <dl class="row mb-0">
            <dt class="col-5">Data/Hora</dt><dd class="col-7" id="infoData">&mdash;</dd>
            <dt class="col-5">Placa</dt>     <dd class="col-7" id="infoPlaca">&mdash;</dd>
            <dt class="col-5">Frame</dt>      <dd class="col-7" id="infoFrame">&mdash;</dd>
          </dl>
        </div>
      </div>
    </div>
  </div>
</div>

<script>
var ctx = '<%= request.getContextPath() %>';
var frames = [], frameAtual = 0, timer = null, reproduzindo = false;

document.getElementById('fps').addEventListener('input', function() {
  document.getElementById('lblFps').textContent = this.value + ' fps';
  if (reproduzindo) { pararPlay(); iniciarPlay(); }
});

function carregarEquips() {
  fetch(ctx + '/MuralhaDigital/Timelapse?acao=equipamentos')
    .then(function(r) { return r.json(); })
    .then(function(d) {
      if (!d.ok) return;
      var sel = document.getElementById('selEquip');
      sel.innerHTML = '';
      d.equipamentos.forEach(function(e) {
        var opt = document.createElement('option');
        opt.value = e.idLocal;
        opt.textContent = e.nome + ' (ID ' + e.idLocal + ')';
        sel.appendChild(opt);
      });
    });
}

function carregar() {
  pararPlay();
  frames = [];
  frameAtual = 0;
  var idLocal = document.getElementById('selEquip').value;
  var ini = document.getElementById('dtInicio').value.replace('T', ' ');
  var fim = document.getElementById('dtFim').value.replace('T', ' ');
  var params = 'acao=frames&idLocal=' + encodeURIComponent(idLocal);
  if (ini) params += '&dtInicio=' + encodeURIComponent(ini);
  if (fim) params += '&dtFim=' + encodeURIComponent(fim);

  fetch(ctx + '/MuralhaDigital/Timelapse?' + params)
    .then(function(r) { return r.json(); })
    .then(function(d) {
      if (!d.ok || !d.frames || d.frames.length === 0) {
        Swal.fire('Aviso', 'Nenhum frame encontrado para o periodo selecionado.', 'info');
        return;
      }
      frames = d.frames;
      document.getElementById('slider').max = frames.length - 1;
      exibirFrame(0);
    });
}

function exibirFrame(idx) {
  if (!frames.length) return;
  idx = Math.max(0, Math.min(idx, frames.length - 1));
  frameAtual = idx;
  var f = frames[idx];
  var img = document.getElementById('frameImg');
  img.src = ctx + '/MuralhaDigital/Veiculo/Imagem?id=' + f.idImagem;
  img.style.display = '';
  document.getElementById('semImagem').style.display = 'none';
  document.getElementById('slider').value = idx;
  document.getElementById('lblFrame').textContent = (idx + 1) + '/' + frames.length;
  document.getElementById('infoData').textContent = f.dtCaptura || '—';
  document.getElementById('infoPlaca').textContent = f.placa || '—';
  document.getElementById('infoFrame').textContent = (idx + 1) + ' de ' + frames.length;
}

function irPara(v) { exibirFrame(parseInt(v)); }
function proximo() { exibirFrame(frameAtual + 1); }
function anterior() { exibirFrame(frameAtual - 1); }

function iniciarPlay() {
  var ms = Math.round(1000 / parseInt(document.getElementById('fps').value));
  timer = setInterval(function() {
    if (frameAtual >= frames.length - 1) { pararPlay(); return; }
    exibirFrame(frameAtual + 1);
  }, ms);
  reproduzindo = true;
  document.getElementById('btnPlay').innerHTML = '<i class="bi bi-pause-fill"></i>';
}

function pararPlay() {
  if (timer) clearInterval(timer);
  timer = null;
  reproduzindo = false;
  document.getElementById('btnPlay').innerHTML = '<i class="bi bi-play-fill"></i>';
}

function togglePlay() { reproduzindo ? pararPlay() : iniciarPlay(); }

carregarEquips();
</script>
