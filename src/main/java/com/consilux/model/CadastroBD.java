/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 29/04/2008

  Descricao: Classe concreta para busca do cadastro de veículo.

  Historico:

    $Log: CadastroBD.java,v $
    Revision 1.10.2.3  2009/06/21 19:39:30  fernando
    ada a marca_processo já qu
    Revision 1.10.2.2  2009/06/19 22:41:13  fernando
    ada a marca_processo já que a coluna não exi
    Revision 1.10.2.1  2009/06/10 20:42:26  fos
    ada a marca_processo já que a coluna não e
    Revision 1.13  2009/05/19 11:32:58  fos
    Agora o cadastro busca também na base de processamento mesmo quando existe o cadastro.

    Revision 1.12  2009/05/08 18:52:26  fos
    Agora armazena a espécie para realimentação do cadastro.

    Revision 1.11  2009/04/03 15:44:19  fos
    Unificada a busca de marca, marca_processo e modelo.

    Revision 1.10  2009/03/12 13:07:38  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.9  2009/03/03 21:33:07  fos
    Retirada a marca_processo já que a coluna não existe mais da view.

    Revision 1.8  2009/01/28 19:23:44  fos
    Colocado marcação para não bloquear a query.

    Revision 1.7  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.5  2008/08/29 20:51:35  fos
    Agora faz pesquisa por veículo isento.

    Revision 1.4  2008/08/27 14:00:41  fos
    ASSIGNED - bug 17: Seleção/Visualização das múltiplas imagens da infração.
    http://bugzilla.consilux.net/show_bug.cgi?id=17

    Revision 1.3  2008/07/23 14:24:13  fos
    Agora possui um método para buscar uma lista de cadastros.

    Revision 1.2  2008/05/09 21:15:07  fos
    Consertado do setObject para o setString.

    Revision 1.1  2008/05/08 21:25:30  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 *
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.10.2.3 $ $Date: 2009/06/21 19:39:30 $ $Author: fernando $
 */
public class CadastroBD extends Cadastro {
	private static Logger logger = Logger.getLogger(CadastroBD.class); 

	
	/**
	 * Constrói o objeto CadastroBD com os seus respectivos atributos.
	 * @param placa Placa do veículo.
	 * @param idMarca Identificador da marca.
	 * @param marca Marca do veículo segundo o cadastro
	 * @param marcaCET Marca CET do veículo segundo o cadastro
	 * @param idMarcaProcesso Marca CET do veículo segundo o cadastro gerado pelos digitadores.
	 * @param idEspecieProcesso Espécie do veículo segundo o cadastro gerado pelos digitadores.
	 * @param ufEspecieProcesso UF do veículo segundo o cadastro gerado pelos digitadores.
	 * @param cor Cor do veículo segundo o cadastro
	 * @param ano Ano do veículo segundo o cadastro
	 * @param idEspecie Identificador da espécie.
	 * @param especie Espécie qual se encaixa o veículo segundo o cadastro
	 * @param uf UF qual se encaixa o veículo segundo o cadastro
	 * @param tipo Tipo do veículo segundo o cadastro
	 * @param categoria Categoria qual se encaixa o veículo segundo o cadastro
	 * @param situacao Situação atual do veículo no cadastro
	 * @param localidade Localidade do veículo segundo o cadastro
	 * @param dataAtualizacao Data da última atualização do veículo no cadastro
	 */
	private CadastroBD(String placa, Integer idMarca, String marca, Integer idMarcaCliente, String marcaCliente, Integer idMarcaProcesso, String marcaProcesso, 
			Integer idEspecieProcesso, String ufProcesso, String especieProcesso, String cor, Integer ano,
			Integer idEspecie, String especie, String uf, String tipo, String categoria, String situacao,
			String localidade, Date dataAtualizacao, String classificacaoCad) {
		super(placa, idMarca, marca, idMarcaCliente, marcaCliente, idMarcaProcesso, marcaProcesso, idEspecieProcesso, ufProcesso, especieProcesso, cor, ano, idEspecie, especie, uf, tipo, categoria, situacao, localidade, dataAtualizacao, classificacaoCad);
	}

	/**
	 * Busca o cadastro do veículo do banco de dados.
	 * @param placa Placa do veículo a ser buscado.
	 * @return Objeto Cadastro materializado ou null se não encontrar a placa no cadastro.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public static Cadastro buscaCadastroPorPlaca(String placa) throws ConexaoException, SQLException {
		
		StringBuilder sbSQL = new StringBuilder();
		
		// TODO: Cuidado: (NOLOCK) provoca Dirty Read.....
		sbSQL.append("SELECT");
		sbSQL.append("	id_marca,");
		sbSQL.append("	marca,");
		sbSQL.append("	id_marca_cet,");
		sbSQL.append("	marca_cet,");
		sbSQL.append("	id_marca_processo,");
		sbSQL.append("	marca_processo,");
		sbSQL.append("	id_especie_processo,");
		sbSQL.append("	uf_processo,");
		sbSQL.append("	especie_processo,");
		sbSQL.append("	cor,");
		sbSQL.append("	ano,");
		sbSQL.append("	id_especie,");
		sbSQL.append("	especie,");
		sbSQL.append("	uf,");
		sbSQL.append("	tipo,");
		sbSQL.append("	categoria,");
		sbSQL.append("	situacao,");
		sbSQL.append("	localidade,");
		sbSQL.append("	data_atualizacao, ");
		sbSQL.append("	classificacao_cad ");
		sbSQL.append("FROM");
		sbSQL.append("	cadastro_veiculo WITH(NOLOCK) ");
		sbSQL.append("WHERE ");
		sbSQL.append("	placa = CAST(? AS CHAR(7))"); //Usado o CAST porque caso contrário o banco não usa índice.
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setString(1, placa);
			Date inicio = new Date();
			rs = ps.executeQuery();
			logger.debug("[TEMPO] executeQuery cad_veiculo ["+placa+"]: "+(new Date().getTime() - inicio.getTime()));
			
			if (rs.next()) {
				return new CadastroBD(
						placa,
						rs.getInt("id_marca"),
						rs.getString("marca"),
						rs.getInt("id_marca_cet"),
						rs.getString("marca_cet"),
						rs.getInt("id_marca_processo") > 0 ? rs.getInt("id_marca_processo") : null,
						rs.getString("marca_processo"),
						rs.getInt("id_especie_processo") > 0 ? rs.getInt("id_especie_processo") : null,
						rs.getString("uf_processo"),
						rs.getString("especie_processo"),
						rs.getString("cor"),
						rs.getInt("ano") > 0 ? rs.getInt("ano") : null,
						rs.getInt("id_especie"),
						rs.getString("especie"),
						rs.getString("uf"),
						rs.getString("tipo"),
						rs.getString("categoria"),
						rs.getString("situacao"),
						rs.getString("localidade"),
						rs.getDate("data_atualizacao"),
						rs.getString("classificacao_cad")
				);
			}
			else {
				sbSQL.setLength(0);
				sbSQL.append("SELECT m.id_marca_cet, m.descricao as descricao_marca,");
				sbSQL.append("e.id_especie, e.descricao as descricao_especie,u.uf ");
				sbSQL.append("FROM (SELECT CAST(? AS CHAR(7)) as placa) as p "); //Usado o CAST porque caso contrário o banco não usa índice.
				sbSQL.append("LEFT JOIN (SELECT mcp.placa, mcp.id_marca_cet, mc.descricao FROM cad_marca_cet_processo mcp ");
				sbSQL.append("			 LEFT JOIN cad_marca_cet mc ON mc.id_marca_cet = mcp.id_marca_cet) as m on m.placa = p.placa ");
				sbSQL.append("LEFT JOIN (SELECT ep.placa, ep.id_especie, e.descricao FROM cad_especie_processo ep ");
				sbSQL.append("			 LEFT JOIN cad_especie e ON e.id_especie = ep.id_especie) as e on e.placa = p.placa ");
				sbSQL.append("LEFT JOIN (SELECT up.placa, up.uf FROM cad_uf_processo up) as u on u.placa = p.placa");

				ps = conn.prepareStatement(sbSQL.toString());
				ps.setString(1, placa);
				
				inicio = new Date();
				rs = ps.executeQuery();
				logger.debug("[TEMPO] executeQuery cad_veiculo_realiment ["+placa+"]: "+(new Date().getTime() - inicio.getTime()));
				if (rs.next()) {
					return new CadastroBD(
							placa,
							null,
							null,
							null,
							null,
							rs.getInt("id_marca_cet"),
							rs.getString("descricao_marca"),
							rs.getInt("id_especie"),
							rs.getString("uf"),
							rs.getString("descricao_especie"),
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null,
							null
					);
				}
				else 
					return null;
			}
		}		
		finally {
			if (conn != null)
				conn.close();							
		}		
	}
	/**
	 * Lista os cadastros de veículo do banco de dados conforme os critérios passados.
	 * @param mFiltros Filtros para a busca, regras implementadas: placa, marca, isento_enquadramento.
	 * @return Lista de Cadastros encontrados.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public static List<Cadastro> buscaCadastroPor(Map<String,Object> mFiltros) throws ConexaoException, SQLException {
		List<Cadastro> lRet = new ArrayList<Cadastro>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT top 100"); // proteção contra muitos registros
		sbSQL.append("	placa,");
		sbSQL.append("	id_marca,");
		sbSQL.append("	marca,");
		sbSQL.append("	id_marca_cet,");
		sbSQL.append("	marca_cet,");
		sbSQL.append("	cor,");
		sbSQL.append("	ano,");
		sbSQL.append("	id_especie,");
		sbSQL.append("	especie,");
		sbSQL.append("	uf,");
		sbSQL.append("	tipo,");
		sbSQL.append("	categoria,");
		sbSQL.append("	situacao,");
		sbSQL.append("	localidade,");
		sbSQL.append("	data_atualizacao, ");
		sbSQL.append("	classificacao_cad ");
		sbSQL.append("FROM");
		sbSQL.append("	cadastro_veiculo ");
		sbSQL.append("WHERE ");

		//Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("placa", "(placa LIKE CAST(? AS VARCHAR(15)))");
		mRegras.put("marca", "(marca LIKE ?)");
		mRegras.put("localidade", "(localidade LIKE ?)");
		mRegras.put("categoria", "(categoria LIKE ?)");
		mRegras.put("especie", "(especie LIKE ?)");
		mRegras.put("tipo", "(tipo LIKE ?)");
		mRegras.put("isento_enquadramento", "EXISTS (SELECT id_enquadramento FROM cad_isento WHERE placa=cadastro_veiculo.placa AND id_enquadramento = ?)");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			
			//Ajustando os valores dos parâmetros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new CadastroBD(
						rs.getString("placa"),
						rs.getInt("id_marca"),
						rs.getString("marca"),
						rs.getInt("id_marca_cet"),
						rs.getString("marca_cet"),
						null,
						null,
						null,
						null,
						null,
						rs.getString("cor"),
						rs.getInt("ano") > 0 ? rs.getInt("ano") : null,
						rs.getInt("id_especie"),
						rs.getString("especie"),
						rs.getString("uf"),
						rs.getString("tipo"),
						rs.getString("categoria"),
						rs.getString("situacao"),
						rs.getString("localidade"),
						rs.getDate("data_atualizacao"),
						rs.getString("classificacao_cad")
					)
				);
			}			
		} catch (ModelException e) {
			throw new SQLException("Erro ao montar SQL.", e);
		}		
		finally {
			if (conn != null)
				conn.close();							
		}	
		
		return lRet;
	}
}
