package muralha.digital.monitoramento;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "EquipamentoCameras")
@XmlAccessorType (XmlAccessType.FIELD)
public class EquipamentoCameras
{
	private Integer	idLocal, serieEquipamento;
	private String nome;
	private List<Camera> cameras;
	
	public EquipamentoCameras()
	{
		cameras = new ArrayList<Camera>();
	}

	public Integer getIdLocal() { return idLocal; }
	public void setIdLocal(Integer idLocal) { this.idLocal = idLocal; }

	public Integer getSerieEquipamento() { return serieEquipamento; }
	public void setSerieEquipamento(Integer serieEquipamento) { this.serieEquipamento = serieEquipamento; }

	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }

	public List<Camera> getCameras() { return cameras; }
	public void setCameras(List<Camera> cameras) { this.cameras = cameras; }
	
	public void AdicionarCamera(Camera camera) {
		cameras.add(camera);
	}
}
