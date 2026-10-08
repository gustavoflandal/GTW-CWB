/**********************************************************************************

 Projeto: GTW
 Nome do Modulo: GTW

 Empresa: Consilux Tecnologia

 Autor: Fernando de Souza
 Data: 16/01/2008

 Descricao: Bean de informação de menu.

 Historico:

   $Log: MenuBean.java,v $
   Revision 1.5  2009/04/22 14:56:43  raoni
   Adicionado construtor com parâmetros.

   Revision 1.4  2009/04/20 19:36:59  raoni
   Modificado campo nivel para short (WORD) para ficar compativel com o bean do GWT (no banco é smallint).

   Revision 1.3  2009/01/12 12:49:45  fos
   Recuperação de repositório.

   Revision 1.1  2008/01/17 11:11:40  fernando
   MenuBean - Versão inicial do CVS


*********************************************************************************/
package com.consilux.model.beans;

import java.util.ArrayList;
import java.util.List;

import com.consilux.model.MenuURL;

/**
 *
 * @author Fernando de Souza - Consilux Tecnologia
 * @version $Revision: 1.5 $ $Date: 2009/04/22 14:56:43 $ $Author: raoni $
 */
public class MenuBean {
	private int id = 0;
	private Integer idPai;
	private short nivel = 0;
	private String menu = "";
	private String descricao = "";
	private String tipo = "";
	private String acao = "";
	private List<MenuURL> listaURL = new ArrayList<MenuURL>();
	private List<MenuBean> listaSubMenu = new ArrayList<MenuBean>();
	
	public MenuBean(int idMenu, Integer idPai, short nivel, String menu,
			String descricao, String tipo, String acao) {
		
		this.id = idMenu;
		this.idPai = idPai;
		this.nivel = nivel;
		this.menu = menu;
		this.descricao = descricao;
		this.tipo = tipo;
		this.acao = acao;
	}
	
	public List<MenuURL> getListaURL() {
		return listaURL;
	}
	
	public List<MenuBean> getListaSubMenu() {
		return listaSubMenu;
	}

	public void setListaSubMenu(List<MenuBean> listaSubMenu) {
		this.listaSubMenu = listaSubMenu;
	}

	/**
	 * @return Retorna o valor de id atual.
	 */
	public int getId() {
		return id;
	}
	/**
	 * @return Retorna o valor de idPai atual.
	 */
	public Integer getIdPai() {
		return idPai;
	}
	/**
	 * @return Retorna o valor de nivel atual.
	 */
	public short getNivel() {
		return nivel;
	}
	/**
	 * @return Retorna o valor de menu atual.
	 */
	public String getMenu() {
		return menu;
	}
	/**
	 * @return Retorna o valor de descricao atual.
	 */
	public String getDescricao() {
		return descricao;
	}
	/**
	 * @return Retorna o valor de tipo atual.
	 */
	public String getTipo() {
		return tipo;
	}
	/**
	 * @return Retorna o valor de acao atual.
	 */
	public String getAcao() {
		return acao;
	}
	/**
	 * @param id Novo valor para o atributo id.
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @param idPai Novo valor para o atributo idPai.
	 */
	public void setIdPai(Integer idPai) {
		this.idPai = idPai;
	}
	/**
	 * @param nivel Novo valor para o atributo nivel.
	 */
	public void setNivel(short nivel) {
		this.nivel = nivel;
	}
	/**
	 * @param menu Novo valor para o atributo menu.
	 */
	public void setMenu(String menu) {
		this.menu = menu;
	}
	/**
	 * @param descricao Novo valor para o atributo descricao.
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	/**
	 * @param tipo Novo valor para o atributo tipo.
	 */
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	/**
	 * @param acao Novo valor para o atributo acao.
	 */
	public void setAcao(String acao) {
		this.acao = acao;
	}
	
}

