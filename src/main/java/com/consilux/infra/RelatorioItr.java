/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 25/01/2006

  Descricao: Classe pai para iteração dos itens nos relatórios.

  Historico:

    $Log: RelatorioItr.java,v $
    Revision 1.4  2009/06/08 20:09:40  fos
    Criado métodos para liberar as conexão quando elas deixarem de ser usadas.

    Revision 1.3  2009/01/12 12:49:50  fos
    Recuperação de repositório.

    Revision 1.1  2008/02/06 19:20:25  fos
    Carga da infração na nova tela funcional.

    Revision 1.2  2007/03/16 12:56:16  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.infra;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;

/**
 * Classe pai para iteração dos itens nos relatórios.
 * @param <E> Classe de negócio com os dados a serem mostrados no item.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/06/08 20:09:40 $ $Author: fos $
 */
public class RelatorioItr<E extends Relatorio> implements Iterable<E> {
	private static Logger logger = Logger.getLogger(RelatorioItr.class); 
	private ResultSet rs = null;
	private E relatorio;
	private boolean podeProx = false;
	private boolean verificado = false;
	/**
	 * Monta o relatório e já aponta para o primeiro item.
	 * @param relatorio Classe de negócio.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public RelatorioItr(E relatorio) throws ConexaoException, SQLException {
		this.rs = relatorio.montaRel();
		this.relatorio = relatorio;
	}
	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<E> iterator() {
		return new Itr();
	}
	
	private class Itr implements Iterator<E> {

		public boolean hasNext() {
			try {
				if (!verificado) {
					podeProx = rs.next();
					verificado = true;
					if (!podeProx) { //Terminou...limpado a conexão...
						relatorio.finaliza();
					}
				}
				return podeProx;
			}
			catch(SQLException err) {
				return false;
			}
		}

		public E next() {
			try {
				if (!verificado)
					podeProx = rs.next();
				relatorio.constroi(rs);
				verificado = false;
				if (!podeProx) { //Terminou...limpado a conexão...
					relatorio.finaliza();
				}
			}
			catch(SQLException err) {
				logger.error("Erro ao iterar o relatório!", err);
				return null;
			}
			return relatorio;
		}

		public void remove() { }
		
	}

}
