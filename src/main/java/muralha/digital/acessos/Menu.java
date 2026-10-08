package muralha.digital.acessos;

public class Menu 
{
	private int 		idMenuInfo;
	private int 		idMenu;
	private String 		src;
	private String		descricao;
	private String 		href;
	private String 		descDetalhada;
		
	public int getIdMenuInfo() {
		return idMenuInfo;
	}
	public void setIdMenuInfo(int idMenuInfo) {
		this.idMenuInfo = idMenuInfo;
	}
	public int getIdMenu() {
		return idMenu;
	}
	public void setIdMenu(int idMenu) {
		this.idMenu = idMenu;
	}
	public String getSrc() {
		return src;
	}
	public void setSrc(String src) {
		this.src = src;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getDescDetalhada() {
		return descDetalhada;
	}
	public void setDescDetalhada(String descDetalhada) {
		this.descDetalhada = descDetalhada;
	}
}
