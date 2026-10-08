package muralha.digital.relatorios;

import java.sql.Timestamp;

import com.consilux.model.TipoMime;

public class RelatorioValidacao
{
	private boolean filtroValido;
	private String mensagem;
	private Timestamp dataInicioFiltro;
	private Timestamp dataFimFiltro;
	private String tipoAgrupamento;
	private TipoMime formato;
	
	public boolean isFiltroValido() {
		return filtroValido;
	}
	public void setFiltroValido(boolean filtroValido) {
		this.filtroValido = filtroValido;
	}
	
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	public Timestamp getDataInicioFiltro() {
		return dataInicioFiltro;
	}
	public void setDataInicioFiltro(Timestamp dataInicioFiltro) {
		this.dataInicioFiltro = dataInicioFiltro;
	}
	
	public Timestamp getDataFimFiltro() {
		return dataFimFiltro;
	}
	public void setDataFimFiltro(Timestamp dataFimFiltro) {
		this.dataFimFiltro = dataFimFiltro;
	}
	
	public String getTipoAgrupamento() {
		return tipoAgrupamento;
	}
	public void setTipoAgrupamento(String tipoAgrupamento) {
		this.tipoAgrupamento = tipoAgrupamento;
	}
	
	public TipoMime getFormato() {
		return formato;
	}
	public void setFormato(TipoMime formato) {
		this.formato = formato;
	}
}
