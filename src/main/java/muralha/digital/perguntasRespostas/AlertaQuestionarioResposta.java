package muralha.digital.perguntasRespostas;

import java.util.Date;
import java.util.UUID;

public class AlertaQuestionarioResposta {
	public int id;
	
	public UUID id_alerta;
	
	public int id_questionario_alerta;
	
	public int id_usuario_resposta;
	
	public String resposta_simples;
	
	public String resposta_livre_usuario;
	
	public Date data_criacao;

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

	public int getId_questionario_alerta() {
		return id_questionario_alerta;
	}

	public void setId_questionario_alerta(int id_questionario_alerta) {
		this.id_questionario_alerta = id_questionario_alerta;
	}

	public int getId_usuario_resposta() {
		return id_usuario_resposta;
	}

	public void setId_usuario_resposta(int id_usuario_resposta) {
		this.id_usuario_resposta = id_usuario_resposta;
	}

	public String getResposta_simples() {
		return resposta_simples;
	}

	public void setResposta_simples(String resposta_simples) {
		this.resposta_simples = resposta_simples;
	}

	public String getResposta_livre_usuario() {
		return resposta_livre_usuario;
	}

	public void setResposta_livre_usuario(String resposta_livre_usuario) {
		this.resposta_livre_usuario = resposta_livre_usuario;
	}

	public Date getData_criacao() {
		return data_criacao;
	}

	public void setData_criacao(Date data_criacao) {
		this.data_criacao = data_criacao;
	}
}
