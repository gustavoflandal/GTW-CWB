package muralha.digital.registroDeFato;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class RegistroDeFatoUsuarioGrupos {
    /**
     * Busca todos os usuários vinculados aos grupos informados.
     * @param grupos Lista de ids de grupos
     * @return Lista de UsuarioGrupo (idGrupo + idUsuario)
     */
    public static List<UsuarioGrupo> obterUsuariosPorGrupos(List<Integer> grupos) throws ConexaoException, SQLException {
        if (grupos == null || grupos.isEmpty()) {
            return Collections.emptyList();
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id_grupo, id_usuario FROM sis_usuario_grupo WHERE id_grupo IN (");

        String placeholders = grupos.stream().map(g -> "?").collect(Collectors.joining(", "));
        sql.append(placeholders);
        sql.append(")");

        List<UsuarioGrupo> resultado = new ArrayList<>();

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < grupos.size(); i++) {
                ps.setInt(i + 1, grupos.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UsuarioGrupo ug = new UsuarioGrupo();
                    ug.setIdGrupo(rs.getInt("id_grupo"));
                    ug.setIdUsuario(rs.getInt("id_usuario"));
                    resultado.add(ug);
                }
            }
        }

        return resultado;
    }

    /**
     * Insere os registros na tabela registro_fato_usuario_grupo vinculando usuários aos grupos
     * para um determinado registro de fato.
     *
     * @param idRegistroFato ID do registro de fato
     * @param usuariosGrupos Lista de UsuarioGrupo para inserir
     */
    public static void inserirUsuariosGruposRegistroFato(List<RegistroDeFatoUsuarioGrupo> usuariosGruposRegistroFato) throws ConexaoException, SQLException {
        if (usuariosGruposRegistroFato == null || usuariosGruposRegistroFato.isEmpty()) {
            return;
        }

        String sql = "INSERT INTO muralha.registro_fato_usuario_grupo (id_registro_fato, id_usuario, id_grupo) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (RegistroDeFatoUsuarioGrupo rg : usuariosGruposRegistroFato) {
            	
                ps.setLong(1, rg.getIdRegistroFato());
                ps.setInt(2, rg.getIdUsuario());
                
                if(rg.getIdGrupo() != null) {
                	ps.setInt(3, rg.getIdGrupo());
                }else {
                	ps.setNull(3, java.sql.Types.INTEGER);
                }
                                           
                ps.addBatch();
            }

            ps.executeBatch();
        }
    }
    
    public static void inserirRegistroUsuarioGrupoRegistroFato(RegistroDeFatoUsuarioGrupo registroUsuarioGrupo) throws ConexaoException, SQLException {
        if (registroUsuarioGrupo == null) {
            return;
        }

        String sql = "INSERT INTO muralha.registro_fato_usuario_grupo (id_registro_fato, id_usuario, id_grupo) VALUES (?, ?, ?)";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {

        	ps.setLong(1, registroUsuarioGrupo.getIdRegistroFato());
        	ps.setObject(2, registroUsuarioGrupo.getIdUsuario(), java.sql.Types.INTEGER);
        	ps.setObject(3, registroUsuarioGrupo.getIdGrupo(), java.sql.Types.INTEGER);

            ps.executeUpdate();
        }
    }
    
    public static List<Integer> obterIdsGruposPorRegistroFato(Long idRegistroFato) throws ConexaoException, SQLException {
        List<Integer> ids = new ArrayList<>();
        String sql = "SELECT id_grupo FROM muralha.registro_fato_usuario_grupo WHERE id_registro_fato = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRegistroFato);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt("id_grupo"));
                }
            }
        }
        return ids;
    }
    
    public static void removerRegistroUsuarioGrupoRegistroFato(Long idRegistroFato, Integer idGrupo) throws ConexaoException, SQLException {
        String sql = "DELETE FROM muralha.registro_fato_usuario_grupo WHERE id_registro_fato = ? AND id_grupo = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRegistroFato);
            ps.setInt(2, idGrupo);
            ps.executeUpdate();
        }
    }
    
    public static void removerRegistroUsuarioGrupoRegistroFatoPorUsuario(Long idRegistroFato, Integer idUsuario) 
            throws ConexaoException, SQLException {
        String sql = "DELETE FROM muralha.registro_fato_usuario_grupo WHERE id_registro_fato = ? AND id_usuario = ?";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRegistroFato);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        }
    }
    
    public static List<Integer> obterIdsUsuariosPorRegistroFato(Long idRegistroFato) throws ConexaoException, SQLException {
        List<Integer> idsUsuarios = new ArrayList<>();
        String sql = "SELECT id_usuario FROM muralha.registro_fato_usuario_grupo WHERE id_registro_fato = ? AND id_usuario IS NOT NULL";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idRegistroFato);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    idsUsuarios.add(rs.getInt("id_usuario"));
                }
            }
        }
        return idsUsuarios;
    }
}
