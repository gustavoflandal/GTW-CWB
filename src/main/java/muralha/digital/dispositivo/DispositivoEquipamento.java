package muralha.digital.dispositivo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "DispositivoEquipamento")
@XmlAccessorType (XmlAccessType.FIELD)
public class DispositivoEquipamento
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(DispositivoEquipamento.class);
	
	private int 		idDispositivo;
	private int 		serieEquipamento;
	private String		descDispositivo;
	private double 		latitude;
	private double 		longitude;
	private boolean		conectado;
	private int			statusTrafego;
	private int			passagensDiasRecentes;
	private int			passagensUltimaHora;
	private int			passagensUltimos15Min;
	private int			velMediaUltimos15Min;
	private int			infracoesRegistradas;
	private String		jsonFluxoVelMediaDiario;
	private String		codigosEquipamentos;
	private int			pistaSentido;
	private String      categoria;
	private Camera      camera;
	private List<Camera> cameras;

	//Usado para os quantitativos
	//nos mapas
	private int			quantitativo;
	private String 		tipo;
	private String 		tipoRegistro;
	
	
	public DispositivoEquipamento() {}

	public int getIdDispositivo() {
		return idDispositivo;
	}

	public void setIdDispositivo(int idDispositivo) {
		this.idDispositivo = idDispositivo;
	}

	public int getSerieEquipamento() {
		return serieEquipamento;
	}

	public void setSerieEquipamento(int serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	public String getDescDispositivo() {
		return descDispositivo;
	}

	public void setDescDispositivo(String descDispositivo) {
		this.descDispositivo = descDispositivo;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public boolean isConectado() {
		return conectado;
	}

	public void setConectado(boolean conectado) {
		this.conectado = conectado;
	}

	public int getStatusTrafego() {
		return statusTrafego;
	}

	public void setStatusTrafego(int statusTrafego) {
		this.statusTrafego = statusTrafego;
	}

	public int getPassagensDiasRecentes() {
		return passagensDiasRecentes;
	}

	public void setPassagensDiasRecentes(int passagensDiasRecentes) {
		this.passagensDiasRecentes = passagensDiasRecentes;
	}

	public int getPassagensUltimaHora() {
		return passagensUltimaHora;
	}

	public void setPassagensUltimaHora(int passagensUltimaHora) {
		this.passagensUltimaHora = passagensUltimaHora;
	}

	public int getQuantitativo() {
		return quantitativo;
	}

	public void setQuantitativo(int quantitativo) {
		this.quantitativo = quantitativo;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getTipoRegistro() {
		return tipoRegistro;
	}

	public void setTipoRegistro(String tipoRegistro) {
		this.tipoRegistro = tipoRegistro;
	}

	public int getPassagensUltimos15Min() {
		return passagensUltimos15Min;
	}

	public void setPassagensUltimos15Min(int passagensUltimos15Min) {
		this.passagensUltimos15Min = passagensUltimos15Min;
	}

	public int getVelMediaUltimos15Min() {
		return velMediaUltimos15Min;
	}

	public void setVelMediaUltimos15Min(int velMediaUltimos15Min) {
		this.velMediaUltimos15Min = velMediaUltimos15Min;
	}

	public int getInfracoesRegistradas() {
		return infracoesRegistradas;
	}

	public void setInfracoesRegistradas(int infracoesRegistradas) {
		this.infracoesRegistradas = infracoesRegistradas;
	}

	public String getJsonFluxoVelMediaDiario() {
		return jsonFluxoVelMediaDiario;
	}

	public void setJsonFluxoVelMediaDiario(String jsonFluxoVelMediaDiario) {
		this.jsonFluxoVelMediaDiario = jsonFluxoVelMediaDiario;
	}

	public String getCodigosEquipamentos() {
		return codigosEquipamentos;
	}
	public void setCodigosEquipamentos(String codigosEquipamentos) {
		this.codigosEquipamentos = codigosEquipamentos;
	}

	public int getPistaSentido() {
		return pistaSentido;
	}

	public void setPistaSentido(int pistaSentido) {
		this.pistaSentido = pistaSentido;
	}

	public String getCategoria() {
		return categoria;
	}
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}

	public Camera getCamera() {
		return camera;
	}

	public void setTipoRegistro(Camera camera) {
		this.camera = camera;
	}

	public List<Camera> getCameras() {
	    return cameras;
	}

	public void setCameras(List<Camera> cameras) {
	    this.cameras = cameras;
	}
}