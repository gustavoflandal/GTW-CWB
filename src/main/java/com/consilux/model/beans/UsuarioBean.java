/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 14/12/2007

  Descricao: Bean de informações do formulário de cadastro de usuário.

  Historico:

    $Log: UsuarioBean.java,v $
    Revision 1.7  2009/05/08 18:25:51  raoni
    Adicionado "this" para evitar confusão.

    Revision 1.6  2009/04/23 18:33:33  raoni
    Adicionaod construtor padrão.

    Revision 1.5  2009/01/12 12:49:45  fos
    Recuperação de repositório.

    Revision 1.3  2008/08/12 13:02:44  fos
    Agora busca o id do grupo de equipamentos do usuário.

    Revision 1.2  2007/12/17 20:08:26  fos
    Colocado alguns atributos que faltavam para visualização.

    Revision 1.1  2007/12/17 16:52:59  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model.beans;

import com.consilux.ui.client.beans.UsuarioGwtBean;

/**
 * Bean de informações do formulário de cadastro de usuário.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.7 $ $Date: 2009/05/08 18:25:51 $ $Author: raoni $
 */
public class UsuarioBean {
	
	private Integer id = 0;
	private String usuario = "";
	private String nome = "";
	private String senha = "";
	private String email = "";
	private Boolean alterarSenha = false;
	private Boolean ativo = true;
	private Integer idGrupoEquipamentoConfig = 0;
	private Integer codigoAgente = null;
	private String agenteUF = null;
	
	public UsuarioBean() {
	}
	
	public UsuarioBean(UsuarioGwtBean usuarioGtwBean) {
		this.id = usuarioGtwBean.getIdUsuario();
		this.usuario = usuarioGtwBean.getLogin();
		this.nome = usuarioGtwBean.getNome();
		this.senha = usuarioGtwBean.getSenha();
		this.email = usuarioGtwBean.getEmail();
		this.alterarSenha = usuarioGtwBean.isAlterarSenha();
		this.ativo = usuarioGtwBean.isAtivo();
		this.idGrupoEquipamentoConfig = usuarioGtwBean.getIdGrupoEquipamento();
		this.codigoAgente = usuarioGtwBean.getCodigoAgente();
		this.agenteUF = usuarioGtwBean.getUfAgente();
	}
	
	/**
	 * @return Retorna o valor de idGrupoEquipamentoConfig atual.
	 */
	public Integer getIdGrupoEquipamentoConfig() {
		return idGrupoEquipamentoConfig;
	}
	/**
	 * @param idGrupoEquipamentoConfig Novo valor para o atributo idGrupoEquipamentoConfig.
	 */
	public void setIdGrupoEquipamentoConfig(Integer idGrupoEquipamentoConfig) {
		this.idGrupoEquipamentoConfig = idGrupoEquipamentoConfig;
	}
	/**
	 * @return Retorna o valor de usuario atual.
	 */
	public String getUsuario() {
		return usuario;
	}
	/**
	 * @param usuario Novo valor para o atributo usuario.
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	/**
	 * @return Retorna o valor de nome atual.
	 */
	public String getNome() {
		return nome;
	}
	/**
	 * @param nome Novo valor para o atributo nome.
	 */
	public void setNome(String nome) {
		this.nome = nome;
	}
	/**
	 * @return Retorna o valor de senha atual.
	 */
	public String getSenha() {
		return senha;
	}
	/**
	 * @param senha Novo valor para o atributo senha.
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}
	/**
	 * @return Retorna o valor de email atual.
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email Novo valor para o atributo email.
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * @return Retorna o valor de alterarSenha atual.
	 */
	public boolean isAlterarSenha() {
		return alterarSenha;
	}
	/**
	 * @return Retorna o valor de alterarSenha atual em formato String.
	 */
	public String getAlterarSenhaStr() {
		return (alterarSenha ? "Sim" : "Não");
	}
	/**
	 * @param alterar_senha Novo valor para o atributo alterar_senha.
	 */
	public void setAlterarSenha(boolean alterarSenha) {
		this.alterarSenha = alterarSenha;
	}
	/**
	 * @return Retorna o valor de ativo atual.
	 */
	public boolean isAtivo() {
		return ativo;
	}
	/**
	 * @return Retorna o valor de ativo atual em formato String.
	 */
	public String getAtivoStr() {
		return (ativo ? "Sim" : "Não");
	}
	/**
	 * @param ativo Novo valor para o atributo ativo.
	 */
	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	/**
	 * @return Retorna o valor de id atual.
	 */
	public Integer getId() {
		return id;
	}
	/**
	 * @param id Novo valor para o atributo id.
	 */
	public void setId(Integer id) {
		this.id = id;
	}

	
	/**
	 * @return Retorna o valor do código agente.
	 */
	public Integer getCodigoAgente() {
		return codigoAgente;
	}

	/**
	 * @param codigoAgente novo valor para o atributo codigoAgente;
	 */
	public void setCodigoAgente(Integer codigoAgente) {
		this.codigoAgente = codigoAgente;
	}

	public String getAgenteUF() {
		return agenteUF;
	}

	public void setAgenteUF(String agenteUF) {
		this.agenteUF = agenteUF;
	}
	
}
