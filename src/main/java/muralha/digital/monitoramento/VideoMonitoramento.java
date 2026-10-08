package muralha.digital.monitoramento;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;


@XmlRootElement(name = "VideoMonitoramento")
@XmlAccessorType (XmlAccessType.FIELD)
public class VideoMonitoramento
{
	
    @XmlTransient
	private static final Logger logger = Logger.getLogger(VideoMonitoramento.class);
	
	private UUID id;
	private Integer idLocal;
	private Integer serieEquipamento;
	private String ipCamera;
	private String tipoCamera;
	private Date dataHora;
	private String endereco;
	private String formato;
	private Date dataImportacao;
	
	private String listaIdsVideos;
	private String listaEnderecoVideos;
	
	public String dataHoraFormatada = "";
	public String dataImportacaoFormatada = "";
	public String horaFormatada = "";
	public String horaImportacaoFormatada = "";
	
	public VideoMonitoramento() {}

	public UUID getId() { return id; }
	public void setId(UUID id) { this.id = id; }
	
	public Integer getIdLocal() { return idLocal; }
	public void setIdLocal(Integer idLocal) { this.idLocal = idLocal; }
	
	public Integer getSerieEquipamento() { return serieEquipamento; }
	public void setSerieEquipamento(Integer serieEquipamento) { this.serieEquipamento = serieEquipamento; }

	public String getIpCamera() { return ipCamera; }
	public void setIpCamera(String ipCamera) { this.ipCamera = ipCamera; }
	
	public String getTipoCamera() { return tipoCamera; }
	public void setTipoCamera(String tipoCamera) { this.tipoCamera = tipoCamera; }

	public Date getDataHora() { return dataHora; }
	public void setDataHora(Date dataHora) { this.dataHora = dataHora; }

	public String getEndereco() { return endereco; }
	public void setEndereco(String endereco) { this.endereco = endereco; }

	public String getFormato() { return formato; }
	public void setFormato(String formato) { this.formato = formato; }

	public Date getDataImportacao() { return dataImportacao; }
	public void setDataImportacao(Date dataImportacao) { this.dataImportacao = dataImportacao; }
	
	public String getListaIdsVideos() { return listaIdsVideos; }
	public void setListaIdsVideos(String listaIdsVideos) { this.listaIdsVideos = listaIdsVideos; }
	
	public String getListaEnderecoVideos() { return listaEnderecoVideos; }
	public void setListaEnderecoVideos(String listaEnderecoVideos) { this.listaEnderecoVideos = listaEnderecoVideos; }

	public String getDataHoraFormatada() { return new SimpleDateFormat("dd/MM/yyyy").format(dataHora); }
	public String getDataImportacaoFormatada() { return new SimpleDateFormat("dd/MM/yyyy").format(dataImportacao); } 

	public String getHoraFormatada() { return new SimpleDateFormat("HH:mm:ss").format(dataHora); }
	public String getHoraImportacaoFormatada() { return new SimpleDateFormat("HH:mm:ss").format(dataImportacao); }
}
