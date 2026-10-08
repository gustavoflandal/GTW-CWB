function agruparPorUsuario(alerta) {
  const usuarios = {};

  alerta.questionarios.forEach(q => {
    q.perguntas.forEach(p => {
      p.respostas.forEach(r => {
        if (!usuarios[r.usuario]) {
          usuarios[r.usuario] = [];
        }
        usuarios[r.usuario].push({
          questionario: q.titulo,
          pergunta: p.texto,
          obrigatoria: p.obrigatoria,
          respostaSimNao: r.respostaSimNao,
          respostaTexto: r.respostaTexto,
          dataResposta: r.dataResposta
        });
      });
    });
  });

  return usuarios;
}

function abrirModalHistoricoQuestionario(alerta) {
  const modal = new bootstrap.Modal(document.getElementById('modalQuestionario'));
  const tituloEl = document.getElementById('tituloAlerta');
  const conteudoEl = document.getElementById('conteudoModal');

  tituloEl.textContent = alerta.tituloAlerta;
  conteudoEl.innerHTML = '';

  const usuarios = alerta.usuarios;
  const nomesUsuarios = Object.keys(usuarios);

  if (nomesUsuarios.length === 0) {
    conteudoEl.innerHTML = `<p class="text-center mt-3">Nenhum questionário respondido para este alerta.</p>`;
    modal.show();
    return;
  }

  const accordion = document.createElement('div');
  accordion.classList.add('accordion');
  accordion.id = 'accordionUsuarios';

  nomesUsuarios.forEach((usuario, index) => {
    const respostas = usuarios[usuario];
    const headingId = `heading${index}`;
    const collapseId = `collapse${index}`;

    const item = document.createElement('div');
    item.classList.add('accordion-item');

    item.innerHTML = `
      <h2 class="accordion-header" id="${headingId}">
        <button class="accordion-button ${index > 0 ? 'collapsed' : ''}" type="button"
                data-bs-toggle="collapse" data-bs-target="#${collapseId}"
                aria-expanded="${index === 0}" aria-controls="${collapseId}">
          ${usuario}
        </button>
      </h2>
      <div id="${collapseId}" class="accordion-collapse collapse ${index === 0 ? 'show' : ''}"
           aria-labelledby="${headingId}" data-bs-parent="#accordionUsuarios">
        <div class="accordion-body"></div>
      </div>
    `;

    const corpo = item.querySelector('.accordion-body');

    respostas.forEach(r => {
      const bloco = document.createElement('div');
      bloco.classList.add('mb-3');
      bloco.innerHTML = `
        <strong>${r.pergunta}</strong> ${r.obrigatoria ? '<span class="text-danger">*</span>' : ''}<br>
        <strong>R:</strong> ${r.respostaSimNao !== undefined ? (r.respostaSimNao ? 'Sim' : 'Não') : ''}
        ${r.respostaTexto ? (r.respostaSimNao !== undefined ? ' - ' : '') + r.respostaTexto : ''}<br>
        <strong>Data da resposta:</strong> ${r.dataResposta}
        <hr>
      `;
      corpo.appendChild(bloco);
    });

    accordion.appendChild(item);
  });

  conteudoEl.appendChild(accordion);
  modal.show();
}

function buscarRespostasQuestionarios() {
  const url = `/MuralhaDigital/AlertaQuestionario?acao=obterRespostas&idAlerta=${ID_ALERTA}`;

  $.ajax({
    type: "GET",
    url: url,
    dataType: "xml",

    success: (data) => {
      $("body").removeClass("loading");

      const $xml = $(data);
      const respostas = [];
      let tipoAlerta = "";

      $xml.find("respostasQuestionario").each(function () {
        const $r = $(this);
        const dataStr = $r.find("data_resposta").text().trim();
        const dataResposta = formatarData(dataStr);
        const respostaSimples = $r.find("resposta_simples").text().trim();
        const respostaUsuario = $r.find("resposta_usuario").text().trim();
        const obrigatorio = $r.find("obrigatorio").text().trim() === "1";
        const nomeUsuario = $r.find("nome").text().trim();
        const pergunta = $r.find("pergunta").text().trim();
        tipoAlerta = $r.find("tipo_alerta").text().trim();

        respostas.push({
          usuario: nomeUsuario,
          pergunta,
          obrigatoria: obrigatorio,
          respostaSimNao:
            respostaSimples === "SIM"
              ? true
              : respostaSimples === "NÃO"
              ? false
              : undefined,
          respostaTexto: respostaUsuario,
          dataResposta,
        });
      });

      // Agrupa por usuário
      const usuariosMap = respostas.reduce((acc, r) => {
        if (!acc[r.usuario]) acc[r.usuario] = [];
        acc[r.usuario].push(r);
        return acc;
      }, {});

      const alerta = {
        tituloAlerta: "Questionário do alerta - " + tipoAlerta,
        usuarios: usuariosMap,
      };

      abrirModalHistoricoQuestionario(alerta);
    },

    error: () => {
      $("body").removeClass("loading");
      ErrorNotification("Erro ao processar requisição ao servidor!", "");
    },
  });
}

//função auxiliar para formatar a data
function formatarData(isoString) {
  if (!isoString) return "";
  const date = new Date(isoString);
  return date.toLocaleString("pt-BR", { timeZone: "America/Sao_Paulo" });
}