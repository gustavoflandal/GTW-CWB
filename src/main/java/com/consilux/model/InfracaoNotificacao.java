/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 13/03/2007

  Descricao: Classe de negócio para busca de notificações.

  Historico:

    $Log: InfracaoNotificacao.java,v $
    Revision 1.5  2009/03/12 13:07:36  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.4  2009/01/12 12:49:42  fos
    Recuperação de repositório.

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


 *********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.NAIBean;

/**
 * Classe de negócio para busca de notificações.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.5 $ $Date: 2009/03/12 13:07:36 $ $Author: raoni $
 */
public class InfracaoNotificacao {
	/**
	 * Ajusta os dados do condutor infrator no BD.
	 * @param nai Bean com os dados do infrator
	 * @return True se o dado foi alterado com sucesso ou False se não foi alterado.
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static Boolean indentificaCondutor(NAIBean nai) throws ConexaoException, ParseException {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("UPDATE infracao_notificacao");
		sbSQL.append("	SET");
		sbSQL.append("		infrator = ?,");
		sbSQL.append("		endereco = ?,");
		sbSQL.append("		cidade = ?,");
		sbSQL.append("		uf = ?,");
		sbSQL.append("		cep = ?,");
		sbSQL.append("		cpf_cnpj = ?,");
		sbSQL.append("		rg = ?,");
		sbSQL.append("		telefone = ?,");
		sbSQL.append("		cnh_doc = ?,");
		sbSQL.append("		cnh_reg = ?,");
		sbSQL.append("		cnh_uf = ?,");
		sbSQL.append("		data_entrada = ?");
		sbSQL.append("	WHERE");
		sbSQL.append("		infracao = ?");

		Connection conn = null;
		PreparedStatement ps = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			ps.setString(1, (nai.getNome()));
			ps.setString(2, (nai.getEndereco()));
			ps.setString(3, (nai.getCidade()));
			ps.setString(4, (nai.getUF()));
			ps.setString(5, (nai.getCEP()));
			ps.setString(6, (nai.getCPF()));
			ps.setString(7, (nai.getRG()));
			ps.setString(8, (nai.getTelefone()));
			ps.setString(9, (nai.getDocCNH()));
			ps.setString(10, (nai.getRegCNH()));
			ps.setString(11, (nai.getUFCNH()));
			ps.setDate(12, new java.sql.Date(new SimpleDateFormat("dd/MM/yyyy").parse(nai.getDataEntrada()).getTime()));
			ps.setInt(13, Integer.parseInt(nai.getInfracao()));

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}		
		finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
	}
}
