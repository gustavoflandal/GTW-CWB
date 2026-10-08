package muralha.digital.perguntasRespostas;

import java.util.Date;

public class AlertaQuestionario {
	
	private int id;
	
	private int obrigatorio;
	
	private String pergunta;
	
	private int deletado;
	
	private Date data_criacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(int obrigatorio) {
		this.obrigatorio = obrigatorio;
	}

	public String getPergunta() {
		return pergunta;
	}

	public void setPergunta(String pergunta) {
		this.pergunta = pergunta;
	}

	public int getDeletado() {
		return deletado;
	}

	public void setDeletado(int deletado) {
		this.deletado = deletado;
	}

	public Date getData_criacao() {
		return data_criacao;
	}

	public void setData_criacao(Date data_criacao) {
		this.data_criacao = data_criacao;
	}
}
