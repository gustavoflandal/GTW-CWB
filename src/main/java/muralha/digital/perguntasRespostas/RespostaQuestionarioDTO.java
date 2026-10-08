package muralha.digital.perguntasRespostas;

import java.util.Date;

public class RespostaQuestionarioDTO {
	
	private String pergunta;
	
	private Integer obrigatorio;
	
	private Integer id_usuario_resposta;
	
	private String nome;
	
	private Date data_resposta;
	
	private String resposta_simples;
	
	private String resposta_usuario;
	
	private String tipo_alerta;

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public Integer getObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(Integer obrigatorio) {
		this.obrigatorio = obrigatorio;
	}

	public Integer getId_usuario_resposta() {
		return id_usuario_resposta;
	}

	public void setId_usuario_resposta(Integer id_usuario_resposta) {
		this.id_usuario_resposta = id_usuario_resposta;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Date getData_resposta() {
		return data_resposta;
	}

	public void setData_resposta(Date data_resposta) {
		this.data_resposta = data_resposta;
	}

	public String getResposta_simples() {
		return resposta_simples;
	}

	public void setResposta_simples(String resposta_simples) {
		this.resposta_simples = resposta_simples;
	}

	public String getResposta_usuario() {
		return resposta_usuario;
	}

	public void setResposta_usuario(String resposta_usuario) {
		this.resposta_usuario = resposta_usuario;
	}

	public String getTipo_alerta() {
		return tipo_alerta;
	}

	public void setTipo_alerta(String tipo_alerta) {
		this.tipo_alerta = tipo_alerta;
	}
}
