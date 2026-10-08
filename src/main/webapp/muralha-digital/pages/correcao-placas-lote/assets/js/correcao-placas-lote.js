let mostrarCarrossel = false;
let modalResumoInstance;
let veiculosRecebidos = []; 

$(document).ready(function() {
	ObterEquipamentosCombo()
});

function ObterEquipamentosCombo()
{
	obterEquipamentosGenerico("dispositivo");
}

function gerarLote() {
  mostrarCarrossel = true;

  const carrossel = document.getElementById('carouselCards');
  const filter = document.getElementById('filter');
  const containerProcessar = document.getElementById('processar-div');

  carregarVeiculos(); 

  carrossel.removeAttribute('hidden');
  containerProcessar.removeAttribute('hidden');
  filter.setAttribute('hidden', true);
}

function carregarVeiculos() {
  const dataInicio = document.getElementById('dataInicio').value;
  const dataFim    = document.getElementById('dataFim').value;
  const limite     = document.getElementById('limite').value; 
  const idLocal    = document.getElementById('dispositivo').value;
  
  $.ajax({
    url: '/MuralhaDigital/CorrecaoPlaca',
    type: 'GET',
    data: { 
      acao: 'listarVeiculos',
      dataInicio: dataInicio ? dataInicio + " 12:00:00" : "", 
      dataFim: dataFim ? dataFim + " 12:00:00" : "",        
      limite: limite,
      idLocal: idLocal
    },
    dataType: 'json',
    success: function(lista) {
      console.log("Veículos recebidos:", lista);
      veiculosRecebidos = lista; 
      gerarCarrossel(lista);
    },
    error: function(xhr, status, error) {
      console.error("Erro ao carregar veículos:", error);
      alert("Não foi possível carregar os veículos.");
    }
  });
}


function gerarCarrossel(listaVeiculos) {
  const carouselInner = document.querySelector("#carouselCards .carousel-inner");
  carouselInner.innerHTML = "";

  if (!listaVeiculos || listaVeiculos.length === 0) {
    carouselInner.innerHTML = `
      <div class="carousel-item active">
        <div class="text-center p-5">Nenhum veículo encontrado.</div>
      </div>`;
    
    document.getElementById("carouselCards").removeAttribute("hidden");
    document.getElementById("processar-div").removeAttribute("hidden");
    return;
  }

  const tamanhoGrupo = 6;
  for (let i = 0; i < listaVeiculos.length; i += tamanhoGrupo) {
    const grupo = listaVeiculos.slice(i, i + tamanhoGrupo);
    const isActive = i === 0 ? "active" : "";
    let itemHTML = `<div class="carousel-item ${isActive}"><div class="row">`;

    grupo.forEach((veiculo) => {
      const placaAtual = veiculo.placa || "N/A";
      const idVeiculo = veiculo.id || "";
      const idImagem = veiculo.id_imagem || "";
      
      const imagemSrc = idVeiculo
        ? `/MuralhaDigital/Veiculo/Imagem?acao=ImagemByIdImg&id=${idImagem}`
        : `/MuralhaDigital/assets/img/teste.jpg`;

      itemHTML += `
        <div class="col-12 col-md-4 margin-bottom">
          <div class="card" data-id-veiculo="${idVeiculo}">
            <div class="zoom-container">
              <img src="${imagemSrc}" class="zoom-image" alt="Imagem com zoom">
              <div class="zoom-lens"></div>
            </div>
            <div class="card-body">
            <div class="placas-container">
              <div class="form-group">
                <label class="form-label">Placa Atual</label>
                <input type="text" class="form-control text-center" style="text-transform: uppercase;" value="${placaAtual}" readonly>
              </div>
              <div class="arrow">➡</div>
              <div class="form-group">
                <label class="form-label">Nova Placa</label>
                <input type="text" class="form-control nova-placa" style="text-transform: uppercase;" placeholder="Digite a nova placa" maxlength="7">
              </div>
              </div>
            </div>
          </div>
        </div>
      `;
    });

    itemHTML += `</div></div>`;
    carouselInner.insertAdjacentHTML("beforeend", itemHTML);
  }

  document.getElementById("carouselCards").removeAttribute("hidden");
  document.getElementById("processar-div").removeAttribute("hidden");

  ativarZoom();
  grupoSlides();
  atualizarLegendaCarrossel();

  const carouselElement = document.getElementById('carouselCards');
  const carouselInstance = bootstrap.Carousel.getInstance(carouselElement);

  if (!carouselInstance) {
    new bootstrap.Carousel(carouselElement, {
      interval: false,
      ride: false
    });
  } else {
    carouselInstance.to(0);
  }
}


function ativarZoom() {
  const zoomContainers = document.querySelectorAll('.zoom-container');

  zoomContainers.forEach(container => {
    const zoomImage = container.querySelector('.zoom-image');
    const zoomLens = container.querySelector('.zoom-lens');

    if (!zoomImage || !zoomLens) return;

    function aplicarZoom(e) {
      const rect = zoomImage.getBoundingClientRect();
      const x = e.clientX - rect.left;
      const y = e.clientY - rect.top;

      const lensSize = 150;
      const offsetX = x - lensSize / 2;
      const offsetY = y - lensSize / 2;

      zoomLens.style.left = `${offsetX}px`;
      zoomLens.style.top = `${offsetY}px`;
      zoomLens.style.display = 'block';
      zoomLens.style.backgroundImage = `url('${zoomImage.src}')`;

      const zoomFactor = 1;
      const realWidth = zoomImage.naturalWidth * zoomFactor;
      const realHeight = zoomImage.naturalHeight * zoomFactor;

      zoomLens.style.width = `${lensSize}px`;
      zoomLens.style.height = `${lensSize}px`;
      zoomLens.style.backgroundSize = `${realWidth}px ${realHeight}px`;
      zoomLens.style.backgroundPosition = `-${x * (realWidth / zoomImage.width) - lensSize / 2}px -${y * (realHeight / zoomImage.height) - lensSize / 2}px`;
    }

    function esconderZoom() {
      zoomLens.style.display = 'none';
      container.classList.remove('hide-cursor');
    }

    zoomImage.onload = () => {
      container.addEventListener('mousemove', aplicarZoom);
      container.addEventListener('mouseleave', esconderZoom);
    };

    if (zoomImage.complete) {
      container.addEventListener('mousemove', aplicarZoom);
      container.addEventListener('mouseleave', esconderZoom);
    }
  });
}

function grupoSlides() {
  const carousel = document.getElementById('carouselCards');
  const legenda = document.getElementById('carouselLegenda');

  carousel.addEventListener('slid.bs.carousel', function (event) {
    const totalSlides = carousel.querySelectorAll('.carousel-item').length;
    const currentIndex = [...carousel.querySelectorAll('.carousel-item')].indexOf(event.relatedTarget) + 1;

    legenda.innerHTML = `<span class="badge bg-primary">Grupo ${currentIndex} de ${totalSlides}</span>`;
  });
}

function atualizarLegendaCarrossel() {
  const carousel = document.getElementById('carouselCards');
  const legenda = document.getElementById('carouselLegenda');

  if (!carousel || !legenda) return;

  const totalSlides = carousel.querySelectorAll('.carousel-item').length;
  const activeSlide = carousel.querySelector('.carousel-item.active');
  const currentIndex = [...carousel.querySelectorAll('.carousel-item')].indexOf(activeSlide) + 1;

  legenda.innerHTML = `<span class="badge bg-primary">Grupo ${currentIndex} de ${totalSlides}</span>`;
}

function mostrarResumo() {
  const listaResumo = document.getElementById('listaResumo');
  listaResumo.innerHTML = '';

  const cards = document.querySelectorAll('.card');
  let houveAlteracao = false;

  cards.forEach((card, index) => {
    const placaAtualInput = card.querySelector('input[readonly]');
    const novaPlacaInput = card.querySelector('.nova-placa');

    const placaAtual = placaAtualInput?.value.trim();
    const novaPlaca = novaPlacaInput?.value.trim().toUpperCase();

    if (placaAtual && novaPlaca && novaPlaca !== placaAtual) {
      houveAlteracao = true;

      const item = document.createElement('li');
      item.className = 'list-group-item d-flex justify-content-between align-items-center';
      item.innerHTML = `
        <span>Placa ${placaAtual} alterada para ${novaPlaca}</span>
        <button class="btn btn-sm btn-danger" onclick="removerAlteracao(${index})">❌</button>
      `;
      listaResumo.appendChild(item);
    }
  });

  if (!modalResumoInstance) {
    modalResumoInstance = new bootstrap.Modal(document.getElementById('modalResumo'));
  }

  modalResumoInstance.show();
}

function removerAlteracao(index) {
  const cards = document.querySelectorAll('.card');
  const card = cards[index];
  const novaPlacaInput = card.querySelector('.nova-placa');

  if (novaPlacaInput) {
    novaPlacaInput.value = '';
  }

  mostrarResumo();
}

function salvarAlteracoes() {
  const placasAlteradas = [];

  veiculosRecebidos.forEach(veiculo => {
    const card = document.querySelector(`.card[data-id-veiculo="${veiculo.id}"]`);
    if (!card) return;

    const novaPlaca = card.querySelector('.nova-placa')?.value.trim().toUpperCase();
    const placaAtual = veiculo.placa?.trim().toUpperCase();

    const item = {
      placaAtual,
      novaPlaca,
      idVeiculo: veiculo.id,
      idUsuario: usuarioID
    };

    if (novaPlaca && novaPlaca !== placaAtual) {
      placasAlteradas.push(item);
    }
  });

  const loteCompleto = veiculosRecebidos.map(veiculo => {
    const card = document.querySelector(`.card[data-id-veiculo="${veiculo.id}"]`);
    const novaPlaca = card?.querySelector('.nova-placa')?.value.trim().toUpperCase() || veiculo.placa;

    return {
      placaAtual: veiculo.placa?.trim().toUpperCase(),
      novaPlaca,
      idVeiculo: veiculo.id,
      idUsuario: usuarioID
    };
  });

  $.ajax({
    url: '/MuralhaDigital/CorrecaoPlaca?acao=atualizarPlacas',
    type: 'POST',
    contentType: 'application/json',
    data: JSON.stringify({ loteCompleto }),
    success: function(response) {
      alert("Processamento concluído com sucesso!");

      const modal = document.getElementById('modalResumo');
      const modalInstance = bootstrap.Modal.getInstance(modal);
      if (modalInstance) modalInstance.hide();

      document.querySelectorAll('.nova-placa').forEach(input => input.value = '');
      const listaResumo = document.getElementById('listaResumo');
      if (listaResumo) listaResumo.innerHTML = '';

      veiculosRecebidos = [];
      location.reload();
    },
    error: function() {
      alert("Erro ao processar alterações.");
    }
  });
}

