/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 25/01/2006

  Descricao: Classe pai para relatórios.

  Historico:

    $Log: Relatorio.java,v $
    Revision 1.5  2009/06/08 20:09:40  fos
    Criado métodos para liberar as conexão quando elas deixarem de ser usadas.

    Revision 1.4  2009/01/12 12:49:50  fos
    Recuperação de repositório.

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.infra;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Classe pai para relatórios.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.5 $ $Date: 2009/06/08 20:09:40 $ $Author: fos $
 */
public abstract class Relatorio implements Cloneable {
	/**
	 * Método onde será implementado a busca dos dados no BD.
	 * @return ResultSet com os dados a serem apresentados
	 * @throws SQLException
	 */
	protected abstract ResultSet montaRel() throws SQLException;
	/**
	 * Método onde será materializada a classe filho com os seus respectivos atributos.
	 * @param rs ResultSet com os dados a serem mostrados no relatório.
	 * @throws SQLException
	 */
	protected abstract void constroi(ResultSet rs) throws SQLException;
	protected abstract void finaliza() throws SQLException;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
