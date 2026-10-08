package muralha.digital.veiculo.imagem;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class VeiculoImagem 
{
	private UUID id;
	private UUID idVeiculoTempoReal;
	private byte imagem[];
	private String imgBase64;
	private int	tpImagem;
	private int	numImagem;
	private UUID idImgObj1;
	private UUID idImgObj2;
		
	public String getImgBase64() {
		return imgBase64;
	}
	public void setImgBase64(String imgBase64) {
		this.imgBase64 = imgBase64;
	}
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}	
	public UUID getIdVeiculoTempoReal() {
		return idVeiculoTempoReal;
	}
	public void setIdVeiculoTempoReal(UUID idVeiculoTempoReal) {
		this.idVeiculoTempoReal = idVeiculoTempoReal;
	}	
	public byte[] getImagem() {
		return imagem;
	}
	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}
	public int getTpImagem() {
		return tpImagem;
	}
	public void setTpImagem(int tpImagem) {
		this.tpImagem = tpImagem;
	}
	public int getNumImagem() {
		return numImagem;
	}
	public void setNumImagem(int numImagem) {
		this.numImagem = numImagem;
	}
	public UUID getIdImgObj1() {
		return idImgObj1;
	}
	public void setIdImgObj1(UUID idImgObj1) {
		this.idImgObj1 = idImgObj1;
	}	
	public UUID getIdImgObj2() {
		return idImgObj2;
	}
	public void setIdImgObj2(UUID idImgObj2) {
		this.idImgObj2 = idImgObj2;
	}

	
	
}
