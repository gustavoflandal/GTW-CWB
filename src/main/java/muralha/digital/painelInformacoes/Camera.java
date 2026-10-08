package muralha.digital.painelInformacoes;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "camera")
public class Camera {
	 private int idLocal;
     private int idCamera;
     private String ipCamera;
     private String tipoCamera;
     private int relevante;

     // Getters e setters
     public int getIdLocal() { return idLocal; }
     public void setIdLocal(int idLocal) { this.idLocal = idLocal; }

     public int getIdCamera() { return idCamera; }
     public void setIdCamera(int idCamera) { this.idCamera = idCamera; }

     public String getIpCamera() { return ipCamera; }
     public void setIpCamera(String ipCamera) { this.ipCamera = ipCamera; }

     public String getTipoCamera() { return tipoCamera; }
     public void setTipoCamera(String tipoCamera) { this.tipoCamera = tipoCamera; }

     public int getRelevante() { return relevante; }
     public void setRelevante(int relevante) { this.relevante = relevante; }
}