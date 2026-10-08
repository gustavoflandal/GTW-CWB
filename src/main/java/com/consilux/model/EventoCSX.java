/**
 * 
 */
package com.consilux.model;

import java.util.Date;

/**
 * @author fernando
 *
 */
public class EventoCSX {

	private static final int CSX_EVENT_GTW_OFFSET = 2000;
	
	private final static int CSX_EVENT_CATEGORY_GTW = CSX_EVENT_GTW_OFFSET + 1;
	private final static int CSX_EVENT_CATEGORY_GTW_REMOTE = CSX_EVENT_GTW_OFFSET + 2;
	private final static int CSX_EVENT_CATEGORY_GTW_EMAIL = CSX_EVENT_GTW_OFFSET + 3;
	
	private final static int CSX_EVENT_GTW_CREATE_USER = CSX_EVENT_GTW_OFFSET + 1;
	private final static int CSX_EVENT_GTW_UPDATE_USER = CSX_EVENT_GTW_OFFSET + 2;
	private static final int CSX_EVENT_GTW_REMOTE_OPENED = CSX_EVENT_GTW_OFFSET + 3;
	private static final int CSX_EVENT_GTW_ADD_IMP_TXT = CSX_EVENT_GTW_OFFSET + 4;
	private static final int CSX_EVENT_GTW_REMOTE_CLOSED = CSX_EVENT_GTW_OFFSET + 5;

	private static final int CSX_EVENT_GTW_EMAIL_SENT = CSX_EVENT_GTW_OFFSET + 6;
	private static final int CSX_EVENT_GTW_EMAIL_ERROR = CSX_EVENT_GTW_OFFSET + 7;

	
	public enum PrioridadeEvento
	{
		BAIXA(0),
		NORMAL(1),
		ALTA(2);
		
		PrioridadeEvento(int codigo) {
			this.codigo = codigo;
		}
		
		public int getCodigo() {
			return codigo;
		}

		private int codigo;
	}

	public enum NivelEvento {
		
		INFO(0),
		WARNING(1),
		ERROR(2),
		CRITICAL(3),
		DEBUG(4);
		
		NivelEvento(int codigo) {
			this.codigo = codigo;
		}
		
		public int getCodigo() {
			return codigo;
		}

		private int codigo;
	}
	
	public enum TipoEvento {
		
		CREATE_USER(CSX_EVENT_CATEGORY_GTW, CSX_EVENT_GTW_CREATE_USER,
			NivelEvento.INFO, PrioridadeEvento.NORMAL),
				
		UPDATE_USER(CSX_EVENT_CATEGORY_GTW, CSX_EVENT_GTW_UPDATE_USER,
			NivelEvento.INFO, PrioridadeEvento.NORMAL),

		REMOTE_OPENED(CSX_EVENT_CATEGORY_GTW_REMOTE, CSX_EVENT_GTW_REMOTE_OPENED,
			NivelEvento.INFO, PrioridadeEvento.NORMAL),
		
		ADD_IMP_TXT(CSX_EVENT_CATEGORY_GTW, CSX_EVENT_GTW_ADD_IMP_TXT,
			NivelEvento.INFO, PrioridadeEvento.NORMAL),
			
		REMOTE_CLOSED(CSX_EVENT_CATEGORY_GTW_REMOTE, CSX_EVENT_GTW_REMOTE_CLOSED,
			NivelEvento.INFO, PrioridadeEvento.NORMAL),
			
		EMAIL_SENT(CSX_EVENT_CATEGORY_GTW_EMAIL, CSX_EVENT_GTW_EMAIL_SENT,
			NivelEvento.INFO, PrioridadeEvento.NORMAL),
					
		EMAIL_ERROR(CSX_EVENT_CATEGORY_GTW_EMAIL, CSX_EVENT_GTW_EMAIL_ERROR,
			NivelEvento.WARNING, PrioridadeEvento.NORMAL);					
		
		private TipoEvento(int idCategoria, int idEvento, NivelEvento nivel,
			PrioridadeEvento prioridade) {
			
			this.idCategoria = idCategoria;
			this.idEvento = idEvento;
			this.prioridade = prioridade;
			this.nivel = nivel;
		}
		
		public int getIdCategoria() {
			return idCategoria;
		}
		public int getIdEvento() {
			return idEvento;
		}

		public NivelEvento getNivel() {
			return nivel;
		}

		private int idCategoria;
		private int idEvento;
		private PrioridadeEvento prioridade;
		private NivelEvento nivel;
	}
	
	private Integer id;
	private String proprietario;
	private Date data_hora;
	private Integer id_categoria;
	private Integer id_evento;
	private String mensagem;
	private int prioridade;
	private int nivel;
	private String usuario;
	private Integer tipoEventoManual;
	private Integer idCategoriaEventoManual;
	

	public EventoCSX(String proprietario, Date data_hora, Integer id_categoria, Integer id_evento, String mensagem,
			int prioridade, int nivel, String usuario, Integer tipoEventoManual, Integer idCategoriaEventoManual) {

		this.id = 0;
		this.proprietario = proprietario;
		this.data_hora = data_hora;
		this.id_categoria = id_categoria;
		this.id_evento = id_evento;
		this.mensagem = mensagem;
		this.prioridade = prioridade;
		this.nivel = nivel;
		this.usuario = usuario;
		this.tipoEventoManual = tipoEventoManual;
		this.idCategoriaEventoManual = idCategoriaEventoManual;
	}
	
	
	public EventoCSX(String proprietario, Date data_hora,
			Integer id_categoria, Integer id_evento, String mensagem,
			int prioridade, int nivel, String usuario) {

		this.id = 0;
		this.proprietario = proprietario;
		this.data_hora = data_hora;
		this.id_categoria = id_categoria;
		this.id_evento = id_evento;
		this.mensagem = mensagem;
		this.prioridade = prioridade;
		this.nivel = nivel;
		this.usuario = usuario;
	}
	public EventoCSX(TipoEvento tipoEvento, String usuario,
			String proprietario) {
		this(tipoEvento, usuario, proprietario, "");
	}
	
	public EventoCSX() {
	}
	
	public EventoCSX(TipoEvento tipoEvento, String usuario,
			String proprietario, String mensagem) {
	
		this.id = 0;
		this.proprietario = proprietario;
		this.data_hora = new Date();
		this.id_categoria = tipoEvento.idCategoria;
		this.id_evento = tipoEvento.getIdEvento();
		this.mensagem = mensagem;
		this.prioridade = tipoEvento.prioridade.codigo;
		this.nivel = tipoEvento.nivel.codigo;
		this.usuario = usuario;		
		
	}
	
	public Integer getId() {
		return id;
	}

	public String getProprietario() {
		return proprietario;
	}

	public Date getData_hora() {
		return data_hora;
	}

	public Integer getId_categoria() {
		return id_categoria;
	}

	public Integer getId_evento() {
		return id_evento;
	}

	public String getMensagem() {
		return mensagem;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public int getNivel() {
		return nivel;
	}
	
	public String getUsuario() {
		return usuario;
	}
	
	public void setProprietario(String proprietario) {
		this.proprietario = proprietario;
	}
	public void setData_hora(Date data_hora) {
		this.data_hora = data_hora;
	}
	public void setId_categoria(Integer id_categoria) {
		this.id_categoria = id_categoria;
	}
	public void setId_evento(Integer id_evento) {
		this.id_evento = id_evento;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}
	public void setNivel(int nivel) {
		this.nivel = nivel;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	
	public int getTipoEventoManual() {
		return tipoEventoManual;
	}
	public void setTipoEventoManual(int tipoEventoManual) {
		this.tipoEventoManual = tipoEventoManual;
	}
	
	public int getIdCategoriaEventoManual() {
		return idCategoriaEventoManual;
	}
	public void setIdCategoriaEventoManual(int idCategoriaEventoManual) {
		this.idCategoriaEventoManual = idCategoriaEventoManual;
	}
	
}
