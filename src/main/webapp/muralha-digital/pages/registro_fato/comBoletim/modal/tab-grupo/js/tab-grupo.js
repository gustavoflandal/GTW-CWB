// Array global que representa o DTO
window.registroDeFatoUsuarioGrupoDTO = {
    grupos: [],
    usuarios: []
};

// Atualiza grupos selecionados
function atualizarGruposSelecionadosComBoletim() {
    const idsGrupos = $('#selectGruposComBoletim').val() || [];
    window.registroDeFatoUsuarioGrupoDTO.grupos = idsGrupos.map(Number);
}

// Atualiza usuários selecionados
function atualizarUsuariosSelecionadosComBoletim(){
    const idsUsuarios = $('#selectUsuariosComBoletim').val() || [];
    window.registroDeFatoUsuarioGrupoDTO.usuarios = idsUsuarios.map(Number);
}

// Retorna os dados para envio ao backend
function getGruposUsuariosComBoletim() {
    return window.registroDeFatoUsuarioGrupoDTO;
}

// Limpa selects e array global
function limparGrupoComBoletim() {
    window.registroDeFatoUsuarioGrupoDTO = { grupos: [], usuarios: [] };
    $('#selectGruposComBoletim').selectpicker('deselectAll');
    $('#selectUsuariosComBoletim').selectpicker('deselectAll');
}

// Popula select de grupos
function popularSelectGrupos(grupos) {
    const gruposSelect = $('#selectGruposComBoletim');
    gruposSelect.empty();

    grupos.forEach(grupo => {
        const option = $('<option></option>')
            .attr('value', grupo.id)
            .text(grupo.nome);
        gruposSelect.append(option);
    });

    gruposSelect.selectpicker('refresh');
}

// Preenche selects a partir do XML vindo do backend
function preencherTabGruposComBoletim(xmlString) {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlString, "application/xml");

    const usuarioGrupoNodes = xmlDoc.querySelectorAll("RegistroDeFato > usuarioGrupos > usuarioGrupo");

    const gruposXML = [];
    const usuariosXML = [];

    usuarioGrupoNodes.forEach(node => {
        const idGrupoNode = node.querySelector("idGrupo");
        if (idGrupoNode) {
            gruposXML.push(idGrupoNode.textContent.trim());
        }

        const idUsuarioNode = node.querySelector("idUsuario");
        if (idUsuarioNode) {
            usuariosXML.push(idUsuarioNode.textContent.trim());
        }
    });

    // Marca os grupos que vieram do XML no select de grupos
    $('#selectGruposComBoletim').selectpicker('val', gruposXML);
    atualizarGruposSelecionadosComBoletim();

    // Marca os usuários que vieram do XML no select de usuários
    $('#selectUsuariosComBoletim').selectpicker('val', usuariosXML);
    atualizarUsuariosSelecionadosComBoletim();
}

function habilitarGrupoCamposBoletim(habilitar) {
    toggleCampo('selectGruposComBoletim', habilitar, 'avisoSelectGrupos');
    toggleCampo('selectUsuariosComBoletim', habilitar, 'avisoSelectUsuarios');
}

function toggleCampo(idCampo, habilitar, idAviso) {
    const campo = document.getElementById(idCampo);
    const aviso = document.getElementById(idAviso);

    if (!campo) return;

    campo.disabled = !habilitar;

    // Atualiza bootstrap-select
    if ($(campo).hasClass("selectpicker")) {
        $(campo).selectpicker('refresh');
    }

    // Exibir ou esconder aviso
    if (aviso) {
        if (!habilitar) {
            aviso.classList.remove("d-none");
            setTimeout(() => aviso.classList.add("show"), 10);
        } else {
            aviso.classList.remove("show");
            setTimeout(() => aviso.classList.add("d-none"), 300);
        }
    }
}

