// Gerencia o canvas overlay para seleção de área de obliteração

let oblCanvas, oblCtx, oblRetangulos = [], oblDesenhando = false, oblStart = {};

function iniciarObliteracao(imgId) {
  if (oblCanvas) return; // já iniciado
  const img = document.getElementById(imgId);
  oblCanvas = document.createElement('canvas');
  oblCanvas.width  = img.offsetWidth;
  oblCanvas.height = img.offsetHeight;
  oblCanvas.style.cssText = 'position:absolute;top:0;left:0;cursor:crosshair;';
  img.parentNode.style.position = 'relative';
  img.parentNode.appendChild(oblCanvas);
  oblCtx = oblCanvas.getContext('2d');
  oblRetangulos = [];

  oblCanvas.addEventListener('mousedown', function(e) {
    const r = oblCanvas.getBoundingClientRect();
    oblStart = { x: e.clientX - r.left, y: e.clientY - r.top };
    oblDesenhando = true;
  });

  oblCanvas.addEventListener('mousemove', function(e) {
    if (!oblDesenhando) return;
    const r = oblCanvas.getBoundingClientRect();
    const x = e.clientX - r.left, y = e.clientY - r.top;
    _renderCanvas(imgId);
    oblCtx.strokeStyle = '#ff0000'; oblCtx.lineWidth = 2;
    oblCtx.strokeRect(oblStart.x, oblStart.y, x - oblStart.x, y - oblStart.y);
  });

  oblCanvas.addEventListener('mouseup', function(e) {
    if (!oblDesenhando) return;
    oblDesenhando = false;
    const r = oblCanvas.getBoundingClientRect();
    const x = e.clientX - r.left, y = e.clientY - r.top;
    const img = document.getElementById(imgId);
    const scaleX = img.naturalWidth  / img.offsetWidth;
    const scaleY = img.naturalHeight / img.offsetHeight;
    oblRetangulos.push({
      x: Math.round(Math.min(oblStart.x, x) * scaleX),
      y: Math.round(Math.min(oblStart.y, y) * scaleY),
      w: Math.round(Math.abs(x - oblStart.x) * scaleX),
      h: Math.round(Math.abs(y - oblStart.y) * scaleY)
    });
    _renderCanvas(imgId);
  });
}

function _renderCanvas(imgId) {
  if (!oblCtx) return;
  oblCtx.clearRect(0, 0, oblCanvas.width, oblCanvas.height);
  const img = document.getElementById(imgId);
  const scaleX = img.offsetWidth  / img.naturalWidth;
  const scaleY = img.offsetHeight / img.naturalHeight;
  oblRetangulos.forEach(function(rect) {
    oblCtx.fillStyle = 'rgba(0,0,0,0.85)';
    oblCtx.fillRect(rect.x * scaleX, rect.y * scaleY, rect.w * scaleX, rect.h * scaleY);
  });
}

function confirmarObliteracao(idImagem) {
  if (oblRetangulos.length === 0) {
    Swal.fire('Atenção', 'Selecione pelo menos uma área para obliterar.', 'warning');
    return;
  }
  Swal.fire({
    title: 'Confirmar obliteração?',
    text: oblRetangulos.length + ' área(s) serão obliteradas. O original é preservado.',
    icon: 'warning', showCancelButton: true,
    confirmButtonText: 'Confirmar', cancelButtonText: 'Cancelar'
  }).then(function(result) {
    if (!result.isConfirmed) return;
    $.post('/MuralhaDigital/Obliteracao', {
      acao: 'aplicar',
      idImagem: idImagem,
      coordenadas: JSON.stringify(oblRetangulos)
    }, function(r) {
      if (r.ok) {
        Swal.fire('Obliteração aplicada!', 'Original preservado. Cópia obliterada criada.', 'success');
        cancelarObliteracao();
      } else {
        Swal.fire('Erro', r.erro, 'error');
      }
    }).fail(function() {
      Swal.fire('Erro', 'Falha na comunicação com o servidor.', 'error');
    });
  });
}

function cancelarObliteracao() {
  oblRetangulos = [];
  if (oblCanvas) {
    oblCanvas.remove();
    oblCanvas = null;
    oblCtx = null;
  }
}
