/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 17/12/2008

  Descricao: Classe de controle de remessas.

  Historico:

    $Log: ExportaRemessaItr.java,v $
    Revision 1.2  2009/06/08 20:09:52  fos
    Criado métodos para liberar as conexão quando elas deixarem de ser usadas.

    Revision 1.1  2009/05/18 14:28:24  fos
    Primeira versão postada no CVS.

    Revision 1.2  2009/03/18 17:18:38  fos
    Ajustado o iterator para trabalhar com o novo formato de conexão.

    Revision 1.1  2009/01/12 12:49:42  fos
    Recuperação de repositório.


*********************************************************************************/
package com.consilux.model;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.exception.ModelException;

/**
 * Classe de controle de remessas.
 * @param <E> Classe de negócio com os dados de auto de infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.2 $ $Date: 2009/06/08 20:09:52 $ $Author: fos $
 */
public class ExportaRemessaItr<E extends ItemExportaRemessa> implements Iterable<E> {
	
	private ResultSet rs = null;
	private ExportaRemessa<E> exportaRemessa;

	private Iterator<E> itr;
	E proximoItem;
	
	public ExportaRemessaItr(ExportaRemessa<E> remessa, final boolean comBlobImagem) throws ConexaoException, SQLException, ModelException, IOException {
		
		this.rs = remessa.buscarItensRemessa(comBlobImagem);
		this.exportaRemessa = remessa;
		
		if (rs.next())
		{
			proximoItem = exportaRemessa.constroiItem(rs, comBlobImagem);
		}
		else
		{
			proximoItem = null;
		}
		
		itr = new Iterator<E>() {

			public boolean hasNext() {
				return proximoItem != null;
			}

			public E next() {
				
				E itemAtual = proximoItem;
				
				if (itemAtual != null) {
					try {
						if (rs.next())
							proximoItem = exportaRemessa.constroiItem(rs, comBlobImagem);
						else
							proximoItem = null;
					} catch (Exception ex) {
						proximoItem = null;
					}
				}
				return itemAtual;

			}
			public void remove() { }
			
		};
		
	}
	
	public Iterator<E> iterator() {
		return itr;
	}

}
