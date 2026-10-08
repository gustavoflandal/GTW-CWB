package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class Boletins {

    private static Logger logger = LogManager.getLogger(Boletins.class);

    /**
     * Insere um boletim vinculado a um registro de fato
     * 
     * @param boletim
     * @param idRegistroFato
     * @param idUsuario
     * @return id gerado do boletim
     * @throws SQLException
     * @throws ConexaoException
     */
    public static Integer inserirBoletim(Boletim boletim, Long idRegistroFato, int idUsuario) 
            throws SQLException, ConexaoException {
        String sql = "INSERT INTO muralha.boletim "
                   + "(id_registro_fato, id_situacao, data_hora_evento, detalhamento, id_usuario, data_criacao, permite_atendimento) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
        	Calendar calendarioUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            ps.setLong(1, idRegistroFato);
            ps.setInt(2, boletim.getIdSituacao());
            if (boletim.getDataHoraEvento() != null) {
                ps.setTimestamp(3, new Timestamp(boletim.getDataHoraEvento().getTime()), calendarioUTC);
            } else {
                // Se a data não vier no objeto, usamos a hora atual como fallback.
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()), calendarioUTC);
            }
            ps.setString(4, boletim.getDetalhamento());
            ps.setInt(5, idUsuario);
            ps.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
            ps.setInt(7, boletim.getPermiteAtendimento() != null ? boletim.getPermiteAtendimento() : 0);

            int rows = ps.executeUpdate();
            if (rows == 0) return null;

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao inserir boletim: ", e);
            throw e;
        }
        return null;
    }
    
    public static boolean atualizarBoletim(Boletim boletim) throws SQLException, ConexaoException {
        String sql = "UPDATE muralha.boletim SET id_situacao = ?, permite_atendimento = ?, detalhamento = ?, data_hora_evento = ? WHERE id = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            Calendar calendarioUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

            ps.setInt(1, boletim.getIdSituacao());
            ps.setInt(2, boletim.getPermiteAtendimento() != null ? boletim.getPermiteAtendimento() : 0);
            
            ps.setString(3, boletim.getDetalhamento());
            
            if (boletim.getDataHoraEvento() != null) {
                ps.setTimestamp(4, new Timestamp(boletim.getDataHoraEvento().getTime()), calendarioUTC);
            } else {
                ps.setNull(4, java.sql.Types.TIMESTAMP);
            }
            
            ps.setInt(5, boletim.getId());

            return ps.executeUpdate() > 0;
            
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao atualizar boletim: ", e);
            throw e;
        }
    }


    /**
     * Insere uma apreensão vinculada a um boletim
     * 
     * @param apreensao
     * @return true se inseriu corretamente
     * @throws SQLException
     * @throws ConexaoException
     */
    public static boolean inserirBoletimApreensao(BoletimApreensao apreensao) 
            throws SQLException, ConexaoException {
        String sql = "INSERT INTO muralha.boletim_apreensao (id_boletim, tipo, descricao) VALUES (?, ?, ?)";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, apreensao.getIdBoletim());
            ps.setString(2, apreensao.getTipo());
            ps.setString(3, apreensao.getDescricao());

            return ps.executeUpdate() == 1;
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao inserir boletim_apreensao: ", e);
            throw e;
        }
    }
    
    public static boolean atualizarBoletimApreensao(BoletimApreensao apreensao) throws SQLException, ConexaoException {
        String sql = "UPDATE muralha.boletim_apreensao SET tipo = ?, descricao = ? WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, apreensao.getTipo());
            ps.setString(2, apreensao.getDescricao());
            ps.setInt(3, apreensao.getId());

            return ps.executeUpdate() == 1;
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao atualizar boletim_apreensao: ", e);
            throw e;
        }
    }
    
    public static boolean removerBoletimApreensao(Integer idApreensao) throws SQLException, ConexaoException {
        String sql = "DELETE FROM muralha.boletim_apreensao WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idApreensao);
            return ps.executeUpdate() == 1;
        } catch (SQLException | ConexaoException e) {
            logger.error("Erro ao remover boletim_apreensao: ", e);
            throw e;
        }
    }
}
