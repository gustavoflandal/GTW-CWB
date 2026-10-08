document.addEventListener('DOMContentLoaded', () => {
	obterGruposComBoletim();
});

function obterGruposComBoletim(callback) {
	const url = "/MuralhaDigital/RegistroDeFato/Grupo?acao=obterTodos";

	$.ajax({
		type: "GET",
		url: url,
		dataType: "xml",
		success: function(data, textStatus, jqXHR) {
			try {
				const xmlDoc = new DOMParser().parseFromString(jqXHR.responseText, "application/xml");

				// === PROCESSAR GRUPOS ===
				const grupos = xmlDoc.getElementsByTagName("grupo");
				const selectGrupos = document.getElementById("selectGruposComBoletim");
				selectGrupos.innerHTML = "";

				window.gruposDisponiveis = [];

				for (let i = 0; i < grupos.length; i++) {
					const grupo = grupos[i];
					const idGrupo = grupo.getElementsByTagName("id_grupo")[0].textContent.trim();
					const descricao = grupo.getElementsByTagName("descricao")[0].textContent.trim();

					const option = document.createElement("option");
					option.value = idGrupo;
					option.textContent = descricao;
					selectGrupos.appendChild(option);

					window.gruposDisponiveis.push({ id: idGrupo, nome: descricao });
				}

				$('#selectGruposComBoletim').selectpicker('refresh');

				// === PROCESSAR USUÁRIOS ===
				const usuarios = xmlDoc.getElementsByTagName("usuario");
				const selectUsuarios = document.getElementById("selectUsuariosComBoletim");
				selectUsuarios.innerHTML = "";

				window.usuariosDisponiveis = [];

				for (let i = 0; i < usuarios.length; i++) {
					const usuario = usuarios[i];

					const idNode = usuario.getElementsByTagName("id_usuario")[0];
					const nomeNode = usuario.getElementsByTagName("nome")[0];
					const loginNode = usuario.getElementsByTagName("usuario")[0];

					const idUsuario = idNode ? idNode.textContent.trim() : null;
					const nome = nomeNode ? nomeNode.textContent.trim() : null;
					const login = loginNode ? loginNode.textContent.trim() : null;

					if (idUsuario && nome && login) {
						const option = document.createElement("option");
						option.value = idUsuario;
						option.textContent = `${nome} (${login})`;
						selectUsuarios.appendChild(option);

						window.usuariosDisponiveis.push({ id: idUsuario, nome: nome, login: login });
					}
				}

				$('#selectUsuariosComBoletim').selectpicker('refresh');

			} catch (e) {
				Swal.fire({
					icon: 'error',
					title: 'Erro ao processar dados',
					text: 'Ocorreu um erro ao processar os dados recebidos do servidor.',
				});
			}
		},
		error: function(jqXHR, textStatus, errorThrown) {
			Swal.fire({
				icon: 'error',
				title: 'Erro na requisição',
				text: 'Não foi possível processar a requisição ao servidor.',
			});
		}
	});
}



