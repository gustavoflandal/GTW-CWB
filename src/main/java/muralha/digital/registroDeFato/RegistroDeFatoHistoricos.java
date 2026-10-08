package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
public class RegistroDeFatoHistoricos {

	private static Logger logger = LogManager.getLogger(RegistroDeFatoHistoricos.class);

	public static  void gravarHistoricoNoBanco(RegistroDeFatoHistorico historico)
	        throws SQLException, ConexaoException {

	    String sql = "INSERT INTO muralha.registro_fato_historico " +
	                 "(id_registro, dados_anteriores, dados_novos, tipo_operacao, id_usuario, data_alteracao) " +
	                 "VALUES (?, ?, ?, ?, ?, SYSDATETIME())";

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, historico.getIdRegistro());
	        ps.setString(2, historico.getDadosAnteriores());
	        ps.setString(3, historico.getDadosNovos());
	        ps.setString(4, historico.getTipoOperacao());
	        if (historico.getIdUsuario() != null) {
	            ps.setInt(5, historico.getIdUsuario());
	        } else {
	            ps.setNull(5, Types.INTEGER);
	        }

	        ps.executeUpdate();
	    } catch (SQLException e) {
	        logger.error("Erro ao gravar histórico do registro ID: " + historico.getIdRegistro(), e);
	        throw e;
	    }
	}

	public static RegistroDeFatoHistorico ObterUltimoHistoricoPorRegistro(Long idRegistro)
			throws ConexaoException, SQLException {

		RegistroDeFatoHistorico historico = null;

		String sql = "SELECT TOP 1 * " + "FROM muralha.registro_fato_historico " + "WHERE id_registro = ? "
				+ "ORDER BY data_alteracao DESC"; // pega o mais recente

		try (Connection conn = Conexao.getConexao(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setLong(1, idRegistro);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					historico = new RegistroDeFatoHistorico();
					historico.setIdHistorico(rs.getLong("id_historico"));
					historico.setIdRegistro(rs.getLong("id_registro"));
					historico.setDadosAnteriores(rs.getString("dados_anteriores"));
					historico.setDadosNovos(rs.getString("dados_novos"));
					historico.setTipoOperacao(rs.getString("tipo_operacao"));
					historico.setIdUsuario(rs.getObject("id_usuario") != null ? rs.getInt("id_usuario") : null);
					historico.setDataAlteracao(rs.getTimestamp("data_alteracao"));
				}
			}

		} catch (SQLException e) {
			logger.error("Erro ao buscar último histórico do registro ID: " + idRegistro, e);
			throw e;
		}

		return historico;
	}
	
	/**
	 * Busca TODOS os históricos de um Registro de Fato específico, ordenados do mais recente para o mais antigo.
	 * @param idRegistro O ID do Registro de Fato.
	 * @return Uma Lista de objetos RegistroDeFatoHistorico. A lista estará vazia se nenhum for encontrado.
	 * @throws ConexaoException
	 * @throws SQLException
	 */
	public static List<RegistroDeFatoHistoricoDTO> ListarHistoricosPorRegistro(Long idRegistro)
	        throws ConexaoException, SQLException {

	    List<RegistroDeFatoHistoricoDTO> historicosDTO = new ArrayList<>();

	    String sql = "SELECT h.*, u.nome as nome_usuario " 
	               + "FROM muralha.registro_fato_historico h "
	               + "LEFT JOIN dbo.sis_usuario u ON h.id_usuario = u.id_usuario " 
	               + "WHERE h.id_registro = ? "
	               + "ORDER BY h.data_alteracao DESC";

	    try (Connection conn = Conexao.getConexao(); 
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setLong(1, idRegistro);

	        try (ResultSet rs = ps.executeQuery()) {
	            
	            while (rs.next()) {
	                RegistroDeFatoHistoricoDTO dto = new RegistroDeFatoHistoricoDTO();
	                
	                dto.setIdHistorico(rs.getLong("id_historico"));
	                dto.setIdRegistro(rs.getLong("id_registro"));
	                dto.setDadosAnteriores(rs.getString("dados_anteriores"));
	                dto.setDadosNovos(rs.getString("dados_novos"));
	                dto.setTipoOperacao(rs.getString("tipo_operacao"));
	                dto.setIdUsuario(rs.getObject("id_usuario") != null ? rs.getInt("id_usuario") : null);
	                dto.setDataAlteracao(rs.getTimestamp("data_alteracao"));
	                dto.setNomeUsuario(rs.getString("nome_usuario") != null ? rs.getString("nome_usuario").trim() : "Usuário desconhecido");

	                historicosDTO.add(dto);
	            }
	        }

	    } catch (SQLException e) {
	        logger.error("Erro ao buscar a lista de históricos para o registro ID: " + idRegistro, e);
	        throw e;
	    }

	    return historicosDTO;
	}

}
