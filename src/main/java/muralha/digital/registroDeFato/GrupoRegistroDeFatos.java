package muralha.digital.registroDeFato;

import java.sql.*;
import java.util.*;

import javax.xml.bind.annotation.*;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

@XmlRootElement(name = "GrupoRegistroDeFatos")
@XmlAccessorType(XmlAccessType.FIELD)
public class GrupoRegistroDeFatos {

    @XmlTransient
    private static final Logger logger = Logger.getLogger(GrupoRegistroDeFatos.class);

    @XmlElementWrapper(name = "grupos")
    @XmlElement(name = "grupo")
    private List<GrupoRegistroDeFato> grupos;

    public GrupoRegistroDeFatos() {
        this.grupos = new ArrayList<>();
    }

    public List<GrupoRegistroDeFato> getGrupos() {
        return grupos;
    }

    public void setGrupos(List<GrupoRegistroDeFato> grupos) {
        this.grupos = grupos;
    }

    public static GruposUsuariosResponse obterTodos() throws ConexaoException, SQLException {
        List<GrupoRegistroDeFato> listaGrupos = new ArrayList<>();
        List<UsuarioRegistroDeFato> listaUsuarios = new ArrayList<>();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();

            // 1) Buscar todos os usuários
            String sqlUsuarios = "SELECT u.id_usuario, u.usuario, u.nome, u.email, u.ativo " +
                                 "FROM dbo.sis_usuario u WHERE u.ativo = 1 ORDER BY u.nome";
            ps = conn.prepareStatement(sqlUsuarios);
            rs = ps.executeQuery();

            while (rs.next()) {
                UsuarioRegistroDeFato usuario = new UsuarioRegistroDeFato();
                usuario.setId_usuario(rs.getInt("id_usuario"));
                usuario.setUsuario(rs.getString("usuario"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setAtivo(rs.getBoolean("ativo"));

                listaUsuarios.add(usuario);
            }

            rs.close();
            ps.close();

            // 2) Buscar todos os grupos
            String sqlGrupos = "SELECT g.id_grupo, g.descricao, g.id_grupo_pai, g.pagina_inicial, " +
                               "       g.nivel_ligacao, g.config_muralha " +
                               "FROM dbo.sis_grupo g " +
                               "ORDER BY g.id_grupo";
            ps = conn.prepareStatement(sqlGrupos);
            rs = ps.executeQuery();

            while (rs.next()) {
                GrupoRegistroDeFato grupo = new GrupoRegistroDeFato();
                grupo.setId_grupo(rs.getInt("id_grupo"));
                grupo.setDescricao(rs.getString("descricao"));
                grupo.setId_grupo_pai(rs.getObject("id_grupo_pai") != null ? rs.getInt("id_grupo_pai") : null);
                grupo.setPagina_inicial(rs.getString("pagina_inicial"));
                grupo.setNivel_ligacao(rs.getObject("nivel_ligacao") != null ? rs.getInt("nivel_ligacao") : null);
                grupo.setConfig_muralha(rs.getObject("config_muralha") != null ? rs.getBoolean("config_muralha") : false);

                listaGrupos.add(grupo);
            }

        } catch (Exception e) {
            logger.error("Erro ao consultar grupos/usuários: ", e);
            throw new SQLException("Erro ao consultar grupos/usuários: ", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("Erro ao fechar conexão", e);
            }
        }

        return new GruposUsuariosResponse(listaGrupos, listaUsuarios);
    }
    
    public static List<GrupoRegistroDeFato> obterGruposPorUsuarioId(int usuarioId) throws ConexaoException, SQLException {
        List<GrupoRegistroDeFato> listaGrupos = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT g.id_grupo, g.descricao, g.id_grupo_pai, g.pagina_inicial, ");
        sql.append("       g.nivel_ligacao, g.config_muralha, ");
        sql.append("       u.id_usuario, u.usuario, u.nome, u.email, u.ativo ");
        sql.append("FROM dbo.sis_grupo g ");
        sql.append("INNER JOIN dbo.sis_usuario_grupo ug ON g.id_grupo = ug.id_grupo ");
        sql.append("LEFT JOIN dbo.sis_usuario u ON ug.id_usuario = u.id_usuario AND u.ativo = 1 ");
        sql.append("WHERE ug.id_usuario = ? ");
        sql.append("ORDER BY g.id_grupo, u.nome ");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql.toString());
            ps.setInt(1, usuarioId);
            rs = ps.executeQuery();

            Map<Integer, GrupoRegistroDeFato> gruposMap = new HashMap<>();

            while (rs.next()) {
                int idGrupo = rs.getInt("id_grupo");
                GrupoRegistroDeFato grupo = gruposMap.get(idGrupo);

                if (grupo == null) {
                    grupo = new GrupoRegistroDeFato();
                    grupo.setId_grupo(idGrupo);
                    grupo.setDescricao(rs.getString("descricao"));
                    grupo.setId_grupo_pai(rs.getObject("id_grupo_pai") != null ? rs.getInt("id_grupo_pai") : null);
                    grupo.setPagina_inicial(rs.getString("pagina_inicial"));
                    grupo.setNivel_ligacao(rs.getObject("nivel_ligacao") != null ? rs.getInt("nivel_ligacao") : null);
                    grupo.setConfig_muralha(rs.getObject("config_muralha") != null ? rs.getBoolean("config_muralha") : false);
                    grupo.setUsuarios(new ArrayList<>());

                    gruposMap.put(idGrupo, grupo);
                }

                // Se houver usuário, adiciona
                int idUsuario = rs.getInt("id_usuario");
                if (!rs.wasNull()) {
                    UsuarioRegistroDeFato usuario = new UsuarioRegistroDeFato();
                    usuario.setId_usuario(idUsuario);
                    usuario.setUsuario(rs.getString("usuario"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setEmail(rs.getString("email"));
                    usuario.setAtivo(rs.getBoolean("ativo"));

                    grupo.getUsuarios().add(usuario);
                }
            }

            listaGrupos.addAll(gruposMap.values());

        } catch (Exception e) {
            logger.error("Erro ao consultar grupos por usuário: ", e);
            throw new SQLException("Erro ao consultar grupos por usuário: ", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("Erro ao fechar conexão", e);
            }
        }

        return listaGrupos;
    }
}
