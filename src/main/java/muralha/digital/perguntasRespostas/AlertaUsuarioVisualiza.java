package muralha.digital.perguntasRespostas;

import java.util.UUID;

public class AlertaUsuarioVisualiza {
	public int id;
	
	public UUID id_alerta;

	public int id_usuario;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public UUID getId_alerta() {
		return id_alerta;
	}

	public void setId_alerta(UUID id_alerta) {
		this.id_alerta = id_alerta;
	}

	public int getId_usuario() {
		return id_usuario;
	}

	public void setId_usuario(int id_usuario) {
		this.id_usuario = id_usuario;
	}
}
