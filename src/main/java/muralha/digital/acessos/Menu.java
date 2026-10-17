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
	public boolean isAtendeEdital() {
		return !getItensEditalList().isEmpty();
	}
	public String getTituloEdital() {
		java.util.List<String> itens = getItensEditalList();
		if (itens.isEmpty()) return "";
		return "Itens do edital (TR TRANSALVADOR):<br>" + String.join("<br>", itens);
	}
	public String getItensEditalTexto() {
		return String.join(", ", getItensEditalList());
	}
	private java.util.List<String> getItensEditalList() {
		java.util.List<String> itens = ReferenciaEdital.itens(idMenu);
		if (itens.isEmpty()) {
			itens = ReferenciaEdital.itensPorHref(href);
		}
		return itens;
	}
}
