/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 04/04/2007

  Descricao: Classe de negócio para carga da imagem do veículo.

  Historico:

    $Log: VeiculoImagem.java,v $
    Revision 1.7  2009/03/12 13:07:37  raoni
    Realizado refactoring para fechar as Conexoes, Statement e ResultSet.
    Tratamento de excecoes para sempre fechar as Conexoes, Statement e ResultSet.
    Modificado de StringBuilder para StringBuffrer.
    Utilizando append do StringBuillder.
    Utilizando ArrayList.

    Revision 1.6  2009/03/10 14:35:46  raoni
    Otimizado o uso de StringBuilder.
    Utiliza LinkedList quando possível.
    Adicionado lógica para realizar o clean up (close) dos Statements.

    Revision 1.5  2009/01/12 12:49:43  fos
    Recuperação de repositório.

    Revision 1.3  2008/08/27 14:00:41  fos
    ASSIGNED - bug 17: Seleção/Visualização das múltiplas imagens da infração.
    http://bugzilla.consilux.net/show_bug.cgi?id=17

    Revision 1.2  2008/07/23 14:29:12  fos
    Agora busca a imagem somente quando o get de imagem é chamado.

    Revision 1.1  2008/02/21 21:06:27  fos
    Renomeada a classe.

    Revision 1.4  2008/02/06 19:20:24  fos
    Carga da infração na nova tela funcional.

    Revision 1.3  2007/07/06 13:04:14  fos
    Colocado unlocks explicitos nas querys.

    Revision 1.2  2007/04/18 18:45:13  fos
    Ajustes para o javadoc

    Revision 1.1  2007/04/11 12:07:15  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;

import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.ImagemGwtBean;
import com.consilux.ui.client.beans.VeiculoImagensGwtBean;

/**
 * Classe de negócio para carga da imagem do veículo.
 * 
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.7 $ $Date: 2009/03/12 13:07:37 $ $Author: raoni $
 */
public class VeiculoImagem {

	public static final float QUALIDADE_JPEG = 0.9f;
	public static final int LARGURA_IMAGEM = 640;
	public static final int ALTURA_IMAGEM = 480;
	private Integer idVeiculo;
	private Integer idImagem;
	private String nomeTipoImagem;
	private byte imagem[];
	private boolean comBlob;
	private Integer idDescarga;
	private Integer idPasta;
	private String Caminho;
	private Integer idRemessa;
	private Integer codigoExterno;
	private String tipoLote;

	private static Logger logger = Logger.getLogger(VeiculoImagem.class);
	private static AcessoStorageInterface acesso = AcessoStorageProvider.ObterInterface();

	/**
	 * @param idVeiculo
	 * @param idImagem
	 * @param nomeTipoImagem
	 * @param imagem
	 * @param comBlob
	 * @param idDescarga
	 * @param idPasta
	 */
	public VeiculoImagem(Integer idVeiculo, Integer idImagem,
			String nomeTipoImagem, byte[] imagem, Integer idDescarga,
			Integer idPasta, String Caminho, Integer idRemessa, Integer codigoExterno, String tipoLote) {
		super();
		this.idVeiculo = idVeiculo;
		this.idImagem = idImagem;
		this.nomeTipoImagem = nomeTipoImagem;
		this.imagem = imagem;
		this.comBlob = true;
		this.idDescarga = idDescarga;
		this.idPasta = idPasta;
		this.Caminho = Caminho;
		this.idRemessa = idRemessa;
		this.codigoExterno = codigoExterno;
		this.tipoLote = tipoLote;
	}

	/**
	 * @param idVeiculo
	 * @param idImagem
	 * @param nomeTipoImagem
	 * @param comBlob
	 * @param idDescarga
	 */
	public VeiculoImagem(Integer idVeiculo, Integer idImagem,
			String nomeTipoImagem, Integer idDescarga, Integer idPasta,
			String Caminho, Integer idRemessa, Integer codigoExterno, String tipoLote) {
		super();
		this.idVeiculo = idVeiculo;
		this.idImagem = idImagem;
		this.nomeTipoImagem = nomeTipoImagem;
		this.comBlob = false;
		this.idDescarga = idDescarga;
		this.idPasta = idPasta;
		this.idRemessa = idRemessa;
		this.codigoExterno = codigoExterno;
		this.tipoLote = tipoLote;
		
	}

	/**
	 * Busca imagens de veículo no BD.
	 * 
	 * @param mFiltros
	 *            Filtros para a busca.
	 * @return Lista de objetos VeiculoImagem
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws SQLException
	 */
	public static VeiculoImagensGwtBean buscaVeiculoImagemPorIdVeiculo(
			Long idVeiculo) throws ConexaoException, SQLException {

		VeiculoImagensGwtBean ret = new VeiculoImagensGwtBean();
		ret.setId(idVeiculo.toString());

		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT ");
		sbSQL.append("	vi.id_imagem, ");
		sbSQL.append("	CASE WHEN ti.nome = 'PAN' THEN CAST(1 AS BIT) ");
		sbSQL.append("	ELSE CAST(0 AS BIT) END AS is_panoramica ");
		sbSQL.append("FROM ");
		sbSQL.append("	veiculo_imagem vi WITH (NOLOCK) ");
		sbSQL.append("	JOIN imagem_info ii WITH (NOLOCK) ");
		sbSQL.append("		ON ii.id_imagem = vi.id_imagem ");
		sbSQL.append("	JOIN tipo_imagem ti WITH (NOLOCK) ");
		sbSQL.append("		ON ti.id_tipo_imagem = ii.id_tipo_imagem ");
		sbSQL.append("WHERE  ");
		sbSQL.append("	vi.id_veiculo = ? ");
		sbSQL.append("ORDER BY ");
		sbSQL.append("	vi.id_imagem ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, idVeiculo);
			rs = ps.executeQuery();

			while (rs.next()) {
				ret.getListaImagens().add(
						new ImagemGwtBean(rs.getInt("id_imagem"), rs
								.getBoolean("is_panoramica")));
			}
		} finally {
			if (conn != null)
				conn.close();
		}

		return ret;

	}

	/**
	 * Busca imagens de veículo no BD.
	 * 
	 * @param idImagem
	 *            id da imagem.
	 * @return Lista de objetos VeiculoImagem
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws SQLException
	 */
	public static VeiculoImagem buscaVeiculoImagemPorIdImagem(Integer idImagem)
			throws ConexaoException, SQLException {
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT vi.id_veiculo, ");
		sbSQL.append(" 		  ii.id_imagem, ");
		sbSQL.append(" 		  ti.nome, ");
		sbSQL.append(" 		  im.imagem, ");
		sbSQL.append(" 		  im.ds_caminho, ");
		sbSQL.append(" 		  vd.id_descarga, ");
		sbSQL.append(" 		  vd.id_pasta, ");
		sbSQL.append(" 		  r.id_remessa, ");
		sbSQL.append(" 		  r.codigo_externo, ");
		sbSQL.append(" 		  r.tipo ");
		sbSQL.append(" FROM   veiculo_imagem vi WITH (NOLOCK) ");
		sbSQL.append("		  JOIN imagem_info ii WITH (NOLOCK) ");
		sbSQL.append(" 		  	   ON  ii.id_imagem = vi.id_imagem ");
		sbSQL.append("		  JOIN tipo_imagem ti WITH (NOLOCK) ");
		sbSQL.append(" 		  	   ON  ti.id_tipo_imagem = ii.id_tipo_imagem ");
		sbSQL.append("		  LEFT JOIN imagem im WITH (NOLOCK) ");
		sbSQL.append(" 		  	   ON  im.id_imagem = ii.id_imagem ");
		sbSQL.append("		  LEFT JOIN veiculo_descarga vd ");
		sbSQL.append(" 		  	   ON  vd.id_veiculo = vi.id_veiculo ");
		sbSQL.append("		  LEFT JOIN infracao i (NOLOCK) ");
		sbSQL.append(" 		  	   ON  i.id_veiculo = vi.id_veiculo ");
		sbSQL.append("		  LEFT JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append(" 		  	   ON  ir.id_infracao = i.id_infracao ");
		sbSQL.append("		  LEFT JOIN remessa r (NOLOCK) ");
		sbSQL.append(" 		  	   ON  r.id_remessa = ir.id_remessa ");
		sbSQL.append(" WHERE  vi.id_imagem = ? ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idImagem);

			rs = ps.executeQuery();
			if (rs.next()) {
				return new VeiculoImagem(rs.getInt("id_veiculo"),
										 rs.getInt("id_imagem"),
										 rs.getString("nome"),
										 rs.getBytes("imagem"),
										 rs.getInt("id_descarga"),
										 rs.getInt("id_pasta"),
										 rs.getString("ds_caminho"),
										 rs.getInt("id_remessa"),
										 rs.getInt("codigo_externo"),
										 rs.getString("tipo"));
			} else{
				logger.error("Não foi encontrado nenhum registro em buscaVeiculoImagemPorIdImagem(). idImagem: " + idImagem);
				return null;
			}
		} 
		catch(Exception e)
		{
			logger.error("Falha na busca de Veiculo Imagem por Imagem. idImagem: " + idImagem, e);
			return null;
		}
		finally 
		{
			if (conn != null)
				conn.close();
		}
	}

	/**
	 * Busca imagens de veículo no BD.
	 * 
	 * @param listaIdInfracoes
	 *            uma lista contendo ids de infrações.
	 * @return Lista de objetos VeiculoImagem
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws SQLException
	 */
	public static List<VeiculoImagem> buscaVeiculoImagemPorIdInfracaoBD(
			List<Integer> listaIdInfracoes) throws ConexaoException,
			SQLException {

		List<VeiculoImagem> lRet = new ArrayList<VeiculoImagem>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT vi.id_veiculo, ");
		sbSQL.append(" 		  ii.id_imagem, ");
		sbSQL.append(" 		  ti.nome, ");
		sbSQL.append(" 		  im.imagem, ");
		sbSQL.append(" 		  im.ds_caminho, ");
		sbSQL.append(" 		  vd.id_descarga, ");
		sbSQL.append(" 		  vd.id_pasta, ");
		sbSQL.append(" 		  r.id_remessa, ");
		sbSQL.append(" 		  r.codigo_externo, ");
		sbSQL.append(" 		  r.tipo ");
		sbSQL.append(" FROM   infracao inf WITH (NOLOCK) ");
		sbSQL.append("		  JOIN veiculo_imagem vi WITH (NOLOCK) ");
		sbSQL.append("			   ON  vi.id_veiculo = inf.id_veiculo ");
		sbSQL.append("		  JOIN imagem_info ii WITH (NOLOCK) ");
		sbSQL.append("			   ON  ii.id_imagem = vi.id_imagem ");
		sbSQL.append("		  JOIN tipo_imagem ti WITH (NOLOCK) ");
		sbSQL.append("			   ON  ti.id_tipo_imagem = ii.id_tipo_imagem ");
		sbSQL.append("		  LEFT JOIN imagem im WITH (NOLOCK) ");
		sbSQL.append("			   ON  im.id_imagem = vi.id_imagem ");
		sbSQL.append("		  LEFT JOIN veiculo_descarga vd WITH (NOLOCK) ");
		sbSQL.append("			   ON  vd.id_veiculo = vi.id_veiculo ");
		sbSQL.append("		  LEFT JOIN infracao i (NOLOCK) ");
		sbSQL.append(" 		  	   ON  i.id_veiculo = vi.id_veiculo ");
		sbSQL.append("		  LEFT JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append(" 		  	   ON  ir.id_infracao = i.id_infracao ");
		sbSQL.append("		  LEFT JOIN remessa r (NOLOCK) ");
		sbSQL.append(" 		  	   ON  r.id_remessa = ir.id_remessa ");
		sbSQL.append(" WHERE  inf.id_infracao IN (");

		for (int i = 0; i < listaIdInfracoes.size(); i++) {
			sbSQL.append("?");
			if (i < listaIdInfracoes.size() - 1)
				sbSQL.append(",");
			else
				sbSQL.append(")");
		}

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			for (int i = 0; i < listaIdInfracoes.size(); i++) {
				ps.setInt(i + 1, listaIdInfracoes.get(i));
			}

			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.add(new VeiculoImagem(rs.getInt("id_veiculo"),
										   rs.getInt("id_imagem"),
										   rs.getString("nome"),
										   rs.getBytes("imagem"),
										   rs.getInt("id_descarga"),
										   rs.getInt("id_pasta"),
										   rs.getString("ds_caminho"),
										   rs.getInt("id_remessa"),
										   rs.getInt("codigo_externo"),
										   rs.getString("tipo")));
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		return lRet;
	}

	/**
	 * Busca imagens de veículo no BD.
	 * 
	 * @param idInfracao
	 *            id da infração.
	 * @return Lista de objetos VeiculoImagem
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws SQLException
	 */
	public static List<VeiculoImagem> buscaVeiculoImagemPorIdInfracaoBD(
			Integer idInfracao) throws ConexaoException, SQLException {

		List<VeiculoImagem> lRet = new ArrayList<VeiculoImagem>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT vi.id_veiculo, ");
		sbSQL.append("		  ii.id_imagem, ");
		sbSQL.append("		  ti.nome, im.imagem, ");
		sbSQL.append("		  im.ds_caminho, ");
		sbSQL.append("		  im.indice_imagem, ");
		sbSQL.append("		  vd.id_descarga, ");
		sbSQL.append("		  vd.id_pasta, ");
		sbSQL.append(" 		  r.id_remessa, ");
		sbSQL.append(" 		  r.codigo_externo, ");
		sbSQL.append(" 		  r.tipo ");
		sbSQL.append(" FROM   infracao inf WITH (NOLOCK) ");
		sbSQL.append("		  JOIN veiculo_imagem vi WITH (NOLOCK) ");
		sbSQL.append("			   ON  vi.id_veiculo = inf.id_veiculo ");
		sbSQL.append("		  JOIN imagem_info ii WITH (NOLOCK) ");
		sbSQL.append("			   ON  ii.id_imagem = vi.id_imagem ");
		sbSQL.append("		  JOIN tipo_imagem ti WITH (NOLOCK) ");
		sbSQL.append("			   ON  ti.id_tipo_imagem = ii.id_tipo_imagem ");
		sbSQL.append("		  LEFT JOIN imagem im WITH (NOLOCK) ");
		sbSQL.append("			   ON  im.id_imagem = vi.id_imagem ");
		sbSQL.append("		  LEFT JOIN veiculo_descarga vd WITH (NOLOCK) ");
		sbSQL.append("			   ON  vd.id_veiculo = vi.id_veiculo ");
		sbSQL.append("		  LEFT JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append(" 		  	   ON  ir.id_infracao = inf.id_infracao ");
		sbSQL.append("		  LEFT JOIN remessa r (NOLOCK) ");
		sbSQL.append(" 		  	   ON  r.id_remessa = ir.id_remessa ");
		sbSQL.append(" WHERE  inf.id_infracao = ? ");
		sbSQL.append(" ORDER BY im.indice_imagem ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idInfracao);
			rs = ps.executeQuery();

			while (rs.next()) {
				lRet.add(new VeiculoImagem(rs.getInt("id_veiculo"),
										   rs.getInt("id_imagem"),
										   rs.getString("nome"),
										   rs.getBytes("imagem"),
										   rs.getInt("id_descarga"),
										   rs.getInt("id_pasta"),
										   rs.getString("ds_caminho"),
										   rs.getInt("id_remessa"),
										   rs.getInt("codigo_externo"),
										   rs.getString("tipo")));
			}
		} finally {
			if (conn != null)
				conn.close();
		}
		return lRet;
	}

	/**
	 * Busca imagens de veículo, utilizando o cache ou o BD (se for necessário).
	 * 
	 * @param mFiltros
	 *            Filtros para a busca.
	 * @return Lista de objetos VeiculoImagem
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws SQLException
	 */
	public static VeiculoImagem buscaVeiculoImagemCache(Integer idImagem,
			Integer idUsuario) throws ConexaoException, SQLException {

		VeiculoImagem ret = VeiculoImagemCache.getInstance().findObject(
				idImagem);
		if (ret == null) {
			logger.debug("Cache MISS idImagem = [" + idImagem
					+ "], idUsuario=[" + idUsuario + "]");

			if (VeiculoImagemCache.getInstance()
					.isDeveriaEstarNoCache(idImagem)) {
				logger.warn("Cache MISS idImagem = [" + idImagem
						+ "], idUsuario=[" + idUsuario + "]");
			}
			ret = buscaVeiculoImagemPorIdImagem(idImagem);
		} else {
			logger.debug("Cache HIT [" + idImagem + "]");
		}
		return ret;
	}

	/*
	 * Nova versão: utilizar quando o sistema de obter imagem pela DLL estiver
	 * tudo OK.
	 * 
	 * public static VeiculoImagem buscaVeiculoImagemPorIdImagem(int idImagem)
	 * throws ConexaoException, SQLException, IOException {
	 * 
	 * StringBuilder sbSQL = new StringBuilder();
	 * 
	 * sbSQL.append("SELECT ");
	 * sbSQL.append("	img.id_imagem, ifo.id_tipo_imagem, img.imagem, ");
	 * sbSQL.append("	arq.nome_arquivo, vei.indice_veiculo, arq.caminho, ");
	 * sbSQL.append("	img.indice_imagem "); sbSQL.append("FROM ");
	 * sbSQL.append("	imagem img WITH (NOLOCK) ");
	 * sbSQL.append("	JOIN imagem_info ifo WITH (NOLOCK) ");
	 * sbSQL.append("		ON ifo.id_imagem = img.id_imagem ");
	 * sbSQL.append("	JOIN veiculo_imagem vmg WITH (NOLOCK) ");
	 * sbSQL.append("		ON vmg.id_imagem = img.id_imagem ");
	 * sbSQL.append("	JOIN veiculo vei WITH (NOLOCK) ");
	 * sbSQL.append("		ON vei.id_veiculo = vmg.id_veiculo ");
	 * sbSQL.append("	LEFT JOIN arquivos_importados arq WITH (NOLOCK) ");
	 * sbSQL.append("		ON arq.id_local = vei.id_local ");
	 * sbSQL.append("		AND vei.id_veiculo BETWEEN arq.id_veiculo_inicial ");
	 * sbSQL.append("		AND arq.id_veiculo_final "); sbSQL.append("WHERE ");
	 * sbSQL.append("	img.id_imagem = ?");
	 * 
	 * 
	 * Connection conn = null; PreparedStatement ps = null; ResultSet rs = null;
	 * 
	 * try { conn = Conexao.getConexao(); ps =
	 * conn.prepareStatement(sbSQL.toString()); ps.setInt(1, idImagem);
	 * 
	 * rs = ps.executeQuery();
	 * 
	 * int idTipoImagem; byte[] bytesImagem; String caminho;
	 * 
	 * String nomeArquivo; int indiceVeiculo; int indiceImagem;
	 * 
	 * if (rs.next()) {
	 * 
	 * // Campos comuns idImagem = rs.getInt("id_imagem"); idTipoImagem =
	 * rs.getInt("id_tipo_imagem"); bytesImagem = rs.getBytes("imagem");
	 * 
	 * // Colunas que só estão populadas se a imagem está // em um arquivo CSX5.
	 * nomeArquivo = rs.getString("nome_arquivo"); if (!rs.wasNull()) { caminho
	 * = rs.getString("caminho"); indiceVeiculo = rs.getInt("indice_veiculo");
	 * indiceImagem = rs.getInt("indice_imagem");
	 * 
	 * caminho = caminho.trim(); if (!caminho.endsWith("\\")) { caminho += "\\";
	 * }
	 * 
	 * long startTime = System.currentTimeMillis(); bytesImagem =
	 * BatchUtil.getInstance().getImage(caminho + nomeArquivo, indiceVeiculo,
	 * indiceImagem); long tempoGasto = System.currentTimeMillis() - startTime;
	 * logger.info(String.format(
	 * "Buscou imagem no arquivo [%1$s]. Tempo gasto: [%2$d] ms.", nomeArquivo,
	 * tempoGasto)); }
	 * 
	 * return new VeiculoImagem(idImagem, idTipoImagem, bytesImagem); } else
	 * return null;
	 * 
	 * } finally { if (rs != null) rs.close(); if (ps != null) ps.close(); if
	 * (conn != null) conn.close(); }
	 * 
	 * }
	 */

	/**
	 * Busca imagens de veículo no BD.
	 * 
	 * @param mFiltros
	 *            Filtros para a busca.
	 * @return Lista de objetos VeiculoImagem
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws SQLException
	 * @throws ModelException
	 */
	public static List<VeiculoImagem> buscaVeiculoImagemPor(
			Map<String, Object> mFiltros) throws ConexaoException,
			SQLException, ModelException {

		List<VeiculoImagem> lRet = new ArrayList<VeiculoImagem>();
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT vi.id_veiculo, ");
		sbSQL.append("		  ii.id_imagem, ");
		sbSQL.append("		  ti.nome, ");
		sbSQL.append("		  vd.id_descarga, ");
		sbSQL.append("		  vd.id_pasta, ");
		sbSQL.append(" 		  r.id_remessa, ");
		sbSQL.append(" 		  r.codigo_externo, ");
		sbSQL.append(" 		  r.tipo ");
		sbSQL.append(" FROM   veiculo_imagem vi WITH (NOLOCK) ");
		sbSQL.append("		  JOIN imagem_info ii WITH (NOLOCK) ");
		sbSQL.append("		  	   ON  ii.id_imagem = vi.id_imagem ");
		sbSQL.append("		  JOIN tipo_imagem ti WITH (NOLOCK) ");
		sbSQL.append("		  	   ON  ti.id_tipo_imagem = ii.id_tipo_imagem ");
		sbSQL.append("		  LEFT JOIN veiculo_descarga vd ");
		sbSQL.append("		  	   ON  vd.id_veiculo = vi.id_veiculo ");
		sbSQL.append("		  LEFT JOIN infracao i (NOLOCK) ");
		sbSQL.append(" 		  	   ON  i.id_veiculo = vi.id_veiculo ");
		sbSQL.append("		  LEFT JOIN infracao_remessa ir (NOLOCK) ");
		sbSQL.append(" 		  	   ON  ir.id_infracao = i.id_infracao ");
		sbSQL.append("		  LEFT JOIN remessa r (NOLOCK) ");
		sbSQL.append(" 		  	   ON  r.id_remessa = ir.id_remessa ");
		sbSQL.append(" WHERE  ");

		// Ajustando os valores do where para os filtros:
		Map<String, String> mRegras = new HashMap<String, String>();
		mRegras.put("id_imagem", "ii.id_imagem = ?");
		mRegras.put("id_veiculo", "vi.id_veiculo = ?");
		mRegras.put("id_infracao",
				"vi.id_veiculo IN (SELECT id_veiculo FROM infracao WHERE id_infracao = ?)");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			sbSQL.append(Funcoes.preparaCondicoesFiltro(mFiltros, mRegras));
			sbSQL.append(" ORDER BY ii.id_tipo_imagem");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parâmetros para os wheres:
			Funcoes.ajustaPreparedStatement(ps, 1, mFiltros.values());

			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new VeiculoImagem(rs.getInt("id_veiculo"),
										   rs.getInt("id_imagem"),
										   rs.getString("nome"),
										   rs.getInt("id_descarga"),
										   rs.getInt("id_pasta"),
										   null,
										   rs.getInt("id_remessa"),
										   rs.getInt("codigo_externo"),
										   rs.getString("tipo")));
			}
		} finally {
			if (conn != null)
				conn.close();
		}

		return lRet;
	}

	/**
	 * Retorna um array de bytes que representa a imagem.
	 * 
	 * @return Array de bytes
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws ModelException
	 * @throws IOException
	 */
	public byte[] getImagem() throws SQLException, ConexaoException,
			IOException, ModelException {
		byte[] img = null;

		if (this.Caminho != null)
			img = acesso.ObterArquivo(Caminho);
		else {
			if (this.comBlob)
				img = this.imagem;
			else
				img = getImagemBD();
		}
		
		if (img == null && this.idDescarga != null && this.idDescarga > 0)
			img = getImagemDescarga();
		
		if (img == null && this.idRemessa != null && this.idRemessa > 0)
			img = getImagemRemessa();

		return img;
	}
	
	public byte[] getArquivoObl() {
		byte[] arquivo = null;
		
		try {
		if(this.Caminho != null) {
			arquivo = acesso.ObterArquivo(Caminho.replace(".JPG", ".OBL"));
		}
		}
		catch(Exception e) {
			arquivo = null;
		}
		
		return arquivo;
	}

	public byte[] getImagemDesob() throws IOException, SQLException, ConexaoException, ModelException {
		byte[] img = null;

		if (this.Caminho != null) {
			img = acesso.ObterArquivo(Caminho);
//			byte[] arquivo_obl = acesso.ObterArquivo(Caminho.replace(".JPG", ".OBL"));
			
//			if(arquivo_obl != null)
//				img = InfracaoObliteracao.desobliteraImagem(img, arquivo_obl, null);
		}
		else {
			img = getImagem();
		}
		
		return img;
	}

	/**
	 * Retorna um array de bytes que representa a imagem.
	 * 
	 * @return Array de bytes
	 * @throws ConexaoException
	 * @throws SQLException
	 * @throws ModelException
	 * @throws IOException
	 */
	public byte[] getImagem(Connection conn) throws SQLException,
			ConexaoException, IOException, ModelException {
		byte[] img = null;

		img = getImagemBD(conn, this.idImagem);

		if (img == null && this.idDescarga != null && this.idDescarga > 0)
			img = getImagemDescarga();

		return img;
	}

	private byte[] getImagemBD() throws SQLException, ConexaoException {
		return getImagemBD(null, this.idImagem);
	}

	private static byte[] getImagemBD(Connection conexao, Integer idImagem)
			throws SQLException, ConexaoException {
		byte bRet[] = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT imagem");
		sbSQL.append("	FROM");
		sbSQL.append("		imagem WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_imagem = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = conexao == null ? Conexao.getConexao() : conexao;
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			ps.setInt(1, idImagem);

			rs = ps.executeQuery();
			if (rs.next())
				bRet = rs.getBytes("imagem");
		} finally {
			if (conexao == null) {// Se não foi passada a conexão por
									// parâmetro...
				if (conn != null)
					conn.close();
			}
		}

		return bRet;
	}

	public static Boolean verificaMD5Imagem(Integer idImagem, String md5)
			throws SQLException, ConexaoException {
		Boolean bRet = false;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("SELECT id_imagem");
		sbSQL.append("	FROM");
		sbSQL.append("		imagem_info WITH (NOLOCK) ");
		sbSQL.append("	WHERE ");
		sbSQL.append("		id_imagem = ? AND md5 = ?");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());

			// Ajustando os valores dos parametros para os wheres:
			ps.setInt(1, idImagem);
			ps.setString(2, md5);

			rs = ps.executeQuery();
			bRet = rs.next();
		} finally {
			if (conn != null)
				conn.close();
		}

		return bRet;
	}

	public byte[] getImagemArquivoLote() {
		//
		return null;
	}

	/**
	 * @return the idVeiculo
	 */
	public Integer getIdVeiculo() {
		return idVeiculo;
	}

	/**
	 * @return Retorna o valor de idImagem atual.
	 */
	public Integer getIdImagem() {
		return idImagem;
	}

	/**
	 * @return Retorna o valor de nomeTipoImagem atual.
	 */
	public String getNomeTipoImagem() {
		return nomeTipoImagem != null ? nomeTipoImagem.trim() : null;
	}

	/**
	 * @return the idDescarga
	 */
	public Integer getIdDescarga() {
		return idDescarga;
	}
	
	/**
	 * @return the idRemessa
	 */
	public Integer getIdRemessa() {
		return idRemessa;
	}
	
	/**
	 * @return the codigoExterno
	 */
	public Integer getCodigoExterno() {
		return codigoExterno;
	}
	
	/**
	 * @return the tipoLote
	 */
	public String getTipoLote() {
		return tipoLote;
	}

	/**
	 * @return the idPasta
	 */
	public Integer getIdPasta() {
		return idPasta;
	}

	private byte[] getImagemDescarga() throws IOException, ModelException {

		byte[] bRet = null;

		if (this.idDescarga == null || this.idDescarga < 1) {
			throw new ModelException("Esta imagem não foi rescarregada.");
		}

		BufferedImage bufImage = new BufferedImage(LARGURA_IMAGEM,
				ALTURA_IMAGEM, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufImage.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, LARGURA_IMAGEM, ALTURA_IMAGEM);

		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.drawString(
				"IMAGEM DESCARREGADA PARA DESCARGA Nº "
						+ String.valueOf(this.idDescarga), 130, 240);

		g.setStroke(new BasicStroke(2.0f));
		g.drawRect(100, 210, 445, 50);

		Iterator<ImageWriter> iter = ImageIO
				.getImageWritersByFormatName("jpeg");
		if (iter.hasNext()) {

			ImageWriter writer = iter.next();
			ImageWriteParam writeParams = writer.getDefaultWriteParam();
			writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			writeParams.setCompressionQuality(VeiculoImagem.QUALIDADE_JPEG);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
			writer.setOutput(ios);
			writer.write(null, new IIOImage(bufImage, null, null), writeParams);
			writer.dispose();

			bRet = baos.toByteArray();
		}

		return bRet;
	}

	public static VeiculoImagem reAdicionaImagem(Integer idImagem, String md5,
			FileItem fi) throws SQLException, ConexaoException, IOException {

		Connection conn = Conexao.getConexao();
		try {
			CallableStatement cs = conn
					.prepareCall("{call spu_readic_imagem(?, ?, ?)}");
			cs.setInt(1, idImagem);
			cs.setString(2, md5);
			cs.setBinaryStream(3, fi.getInputStream(), (int) fi.getSize());
			cs.execute();
		} finally {
			if (conn != null)
				conn.close();
		}
		return VeiculoImagem.buscaVeiculoImagemPorIdImagem(idImagem);
	}

	public String getCaminho() {
		return Caminho;
	}

	private byte[] getImagemRemessa() throws IOException, ModelException {

		byte[] bRet = null;

		if (this.idRemessa == null || this.idRemessa < 1) {
			throw new ModelException("Esta imagem não foi enviada em nenhum movimento de lote.");
		}

		BufferedImage bufImage = new BufferedImage(LARGURA_IMAGEM, ALTURA_IMAGEM, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufImage.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, LARGURA_IMAGEM, ALTURA_IMAGEM);

		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.drawString("IMAGEM ENVIADA NO MOVIMENTO DE LOTE " + this.tipoLote.trim() + ". " + String.valueOf(this.codigoExterno).trim(), 120, 240);

		g.setStroke(new BasicStroke(2.0f));
		g.drawRect(100, 210, 445, 50);

		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
		if (iter.hasNext()) {

			ImageWriter writer = iter.next();
			ImageWriteParam writeParams = writer.getDefaultWriteParam();
			writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			writeParams.setCompressionQuality(VeiculoImagem.QUALIDADE_JPEG);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageOutputStream ios = new MemoryCacheImageOutputStream(baos);
			writer.setOutput(ios);
			writer.write(null, new IIOImage(bufImage, null, null), writeParams);
			writer.dispose();

			bRet = baos.toByteArray();
		}

		return bRet;
	}
	
}
