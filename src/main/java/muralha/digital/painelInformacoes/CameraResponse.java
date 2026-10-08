package muralha.digital.painelInformacoes;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "cameraResponse")
public class CameraResponse {
	private boolean sucesso;
	private List<Camera> cameras = new ArrayList<>();

	// Getters e setters

	public boolean isSucesso() {
		return sucesso;
	}

	public void setSucesso(boolean sucesso) {
		this.sucesso = sucesso;
	}

	@XmlElement(name = "camera")
	public List<Camera> getCamera() {
		return cameras;
	}

	public void setIdLocal(List<Camera> cameras) {
		this.cameras = cameras;
	}
}