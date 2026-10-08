package muralha.digital.blitz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.sql.Timestamp;
import java.net.URL;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.fasterxml.jackson.databind.ObjectMapper;

import muralha.digital._ini.Inicializacao;
import muralha.digital.acessos.Usuario;
import muralha.digital.alerta.Alerta;
import muralha.digital.guarnicao.Guarnicao;
import muralha.digital.registroDeFato.RegistroFatoDTO;
import muralha.digital.websocket.ClienteSessoes;
import muralha.digital.blitz.VeiculoBlitzDTO;

@XmlRootElement(name = "Blitzes")
public class Blitzes {
    
    private List<BlitzDigital> listaBlitz;
    private static Thread timerBlitz;
    private static boolean timerAtivo = false;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Object GEOLOCK = new Object();
    private static long ultimoGeocoding = 0;

    private static Date lastExecutionTime = null;

    private static final Object LOCK = new Object();
    
    @XmlElementWrapper(name = "ListaBlitz")
    @XmlElement(name = "Blitz")
    public List<BlitzDigital> getListaBlitz() {
        return listaBlitz;
    }
    
    public void setListaBlitz(List<BlitzDigital> listaBlitz) {
        this.listaBlitz = listaBlitz;
    }

    private static final Logger logger = Logger.getLogger(Blitzes.class);

    public static List<BlitzDigital> listarTodas() {
        List<BlitzDigital> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT bd.id, bd.nome_blitz, bd.titulo_notificacao, bd.descricao, " +
                        "bd.data_inicio, bd.data_fim, bd.data_criacao, bd.ativo, " +
                        "bd.notificar_agentes_proximos, bd.raio_notificacao_km, " +
                        "CASE WHEN COUNT(ba.id) > 0 THEN 1 ELSE 0 END as temAbordagensAssociadas " +
                        "FROM muralha.blitz_digital bd " +
                        "LEFT JOIN muralha.blitz_abordagem ba ON bd.id = ba.id_blitz_digital " +
                        "GROUP BY bd.id, bd.nome_blitz, bd.titulo_notificacao, bd.descricao, " +
                        "bd.data_inicio, bd.data_fim, bd.data_criacao, bd.ativo, " +
                        "bd.notificar_agentes_proximos, bd.raio_notificacao_km " +
                        "ORDER BY bd.data_criacao DESC";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                BlitzDigital blitz = new BlitzDigital();
                blitz.setId(rs.getInt("id"));
                blitz.setNome_blitz(rs.getString("nome_blitz"));
                blitz.setTitulo_notificacao(rs.getString("titulo_notificacao"));
                blitz.setDescricao(rs.getString("descricao"));
                blitz.setData_inicio(rs.getTimestamp("data_inicio"));
                blitz.setData_fim(rs.getTimestamp("data_fim"));
                blitz.setData_criacao(rs.getTimestamp("data_criacao"));
                blitz.setAtivo(rs.getInt("ativo"));
                blitz.setNotificar_agentes_proximos(rs.getInt("notificar_agentes_proximos"));
                blitz.setRaio_notificacao_km(rs.getBigDecimal("raio_notificacao_km"));
                blitz.setTem_abordagens_associadas(rs.getInt("temAbordagensAssociadas") == 1);
                
                lista.add(blitz);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar blitz digitais", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return lista;
    }

    public static List<BlitzDigital> listarTodasAtivas() {
        List<BlitzDigital> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT id, nome_blitz, titulo_notificacao, descricao, " +
                "data_inicio, data_fim, data_criacao, ativo, " +
                "notificar_agentes_proximos, raio_notificacao_km, endereco " +
                "FROM muralha.blitz_digital " +
                "WHERE ativo = 1 " +
                "AND data_inicio <= GETDATE() " + 
                "AND (data_fim IS NULL OR data_fim >= GETDATE()) " + 
                "ORDER BY data_criacao DESC";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                BlitzDigital blitz = new BlitzDigital();
                blitz.setId(rs.getInt("id"));
                blitz.setNome_blitz(rs.getString("nome_blitz"));
                blitz.setTitulo_notificacao(rs.getString("titulo_notificacao"));
                blitz.setDescricao(rs.getString("descricao"));
                blitz.setEndereco(rs.getString("endereco"));
                blitz.setData_inicio(rs.getTimestamp("data_inicio"));
                blitz.setData_fim(rs.getTimestamp("data_fim"));
                blitz.setData_criacao(rs.getTimestamp("data_criacao"));
                blitz.setAtivo(rs.getInt("ativo"));
                blitz.setNotificar_agentes_proximos(rs.getInt("notificar_agentes_proximos"));
                blitz.setRaio_notificacao_km(rs.getBigDecimal("raio_notificacao_km"));
                
                lista.add(blitz);
            }
        } catch (Exception e) {
            logger.error("Erro ao listar blitz digitais", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return lista;
    }

    public static List<BlitzDigital> listarTodasAtivasAutomaticas() {
        List<BlitzDigital> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT " +
                        "    bd.id, " +
                        "    bd.nome_blitz, " +
                        "    bd.titulo_notificacao, " +
                        "    bd.descricao, " +
                        "    bd.data_inicio, " +
                        "    bd.data_fim, " +
                        "    bd.data_criacao, " +
                        "    bd.ativo, " +
                        "    bd.notificar_agentes_proximos, " +
                        "    bd.raio_notificacao_km, " +
                        "    bd.endereco " +
                        "FROM " +
                        "    muralha.blitz_digital bd " +
                        "    INNER JOIN muralha.blitz_tipo bt ON bd.id_tipo_blitz = bt.id " +
                        "WHERE " +
                        "    bd.ativo = 1 " +
                        "    AND bd.data_inicio <= GETDATE() " +
                        "    AND (bd.data_fim IS NULL OR bd.data_fim >= GETDATE()) " +
                        "    AND bt.codigo = 'AUTOMATICA' " +
                        "ORDER BY " +
                        "    bd.data_criacao DESC";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                BlitzDigital blitz = new BlitzDigital();
                blitz.setId(rs.getInt("id"));
                blitz.setNome_blitz(rs.getString("nome_blitz"));
                blitz.setTitulo_notificacao(rs.getString("titulo_notificacao"));
                blitz.setDescricao(rs.getString("descricao"));
                blitz.setEndereco(rs.getString("endereco"));
                blitz.setData_inicio(rs.getTimestamp("data_inicio"));
                blitz.setData_fim(rs.getTimestamp("data_fim"));
                blitz.setData_criacao(rs.getTimestamp("data_criacao"));
                blitz.setAtivo(rs.getInt("ativo"));
                blitz.setNotificar_agentes_proximos(rs.getInt("notificar_agentes_proximos"));
                blitz.setRaio_notificacao_km(rs.getBigDecimal("raio_notificacao_km"));
                
                lista.add(blitz);
            }
        } catch (Exception e) {
            logger.error("Erro ao listar blitz digitais automaticas", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return lista;
    }

    public static List<Usuario> listarUsuariosAG() {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT u.id_usuario, u.usuario, u.nome, u.email, u.telefone " +
                        "FROM dbo.sis_usuario u " +
                        "INNER JOIN dbo.sis_usuario_grupo ug ON u.id_usuario = ug.id_usuario " +
                        "INNER JOIN dbo.sis_grupo g ON ug.id_grupo = g.id_grupo " +
                        "WHERE g.descricao LIKE '%Agente de Guarnição%' " +
                        "AND u.ativo = 1 " +
                        "ORDER BY u.nome";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setUsuario(rs.getString("usuario"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setTelefone(rs.getString("telefone"));
                usuarios.add(usuario);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar usuários AG", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return usuarios;
    }

    public static List<Guarnicao> listarGuarnicoesAtivas() {
        List<Guarnicao> guarnicoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id, nome FROM muralha.guarnicao WHERE ativo = 1 ORDER BY nome";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Guarnicao guarnicao = new Guarnicao();
                guarnicao.setId(rs.getInt("id"));
                guarnicao.setNome(rs.getString("nome"));
                guarnicoes.add(guarnicao);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar guarnições", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return guarnicoes;
    }

    public static BlitzDigital obterBlitzPorId(int idBlitz) {
        BlitzDigital blitz = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id, nome_blitz, titulo_notificacao, descricao, " +
                        "data_inicio, data_fim, data_criacao, ativo, endereco, " +
                        "notificar_agentes_proximos, raio_notificacao_km, id_tipo_blitz " + 
                        "FROM muralha.blitz_digital " +
                        "WHERE id = ?";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            rs = ps.executeQuery();

            if (rs.next()) {
                blitz = new BlitzDigital();
                blitz.setId(rs.getInt("id"));
                blitz.setNome_blitz(rs.getString("nome_blitz"));
                blitz.setTitulo_notificacao(rs.getString("titulo_notificacao"));
                blitz.setDescricao(rs.getString("descricao"));
                blitz.setEndereco(rs.getString("endereco"));
                blitz.setData_inicio(rs.getTimestamp("data_inicio"));
                blitz.setData_fim(rs.getTimestamp("data_fim"));
                blitz.setData_criacao(rs.getTimestamp("data_criacao"));
                blitz.setAtivo(rs.getInt("ativo"));
                blitz.setNotificar_agentes_proximos(rs.getInt("notificar_agentes_proximos"));
                blitz.setRaio_notificacao_km(rs.getBigDecimal("raio_notificacao_km"));
                blitz.setId_tipo_blitz(rs.getInt("id_tipo_blitz"));
            }

        } catch (Exception e) {
            logger.error("Erro ao obter blitz por ID", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return blitz;
    }

    public static List<Local> listarLocaisVigentes() {
        List<Local> locais = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id_local, " +
                        "sequencia_local, posicao_lat, posicao_lon, nome " +
                        "FROM local_vigente (NOLOCK) " +
                        "WHERE desativado = 0 " +
                        "ORDER BY nome";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                Local local = new Local();
                local.setId_local(rs.getInt("id_local"));
                local.setSequencia_local(rs.getInt("sequencia_local"));
                local.setNome(rs.getString("nome"));
                local.setPosicao_lat(rs.getDouble("posicao_lat"));
                local.setPosicao_lon(rs.getDouble("posicao_lon"));
                locais.add(local);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar locais vigentes", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return locais;
    }

    public static List<Local> listarLocaisBlitz(int idBlitz) {
        List<Local> locais = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT l.id_local, l.sequencia_local, l.nome " +
                        "FROM local_vigente l (NOLOCK) " +
                        "INNER JOIN muralha.blitz_local bl ON l.id_local = bl.id_local " +
                        "WHERE bl.id_blitz_digital = ? " +
                        "AND l.desativado = 0 " +
                        "ORDER BY l.nome";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            rs = ps.executeQuery();

            while (rs.next()) {
                Local local = new Local();
                local.setId_local(rs.getInt("id_local"));
                local.setSequencia_local(rs.getInt("sequencia_local"));
                local.setNome(rs.getString("nome"));
                locais.add(local);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar locais da blitz", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return locais;
    }

    public static List<Usuario> listarUsuariosBlitz(int idBlitz) {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT u.id_usuario, u.usuario, u.nome, u.email, u.telefone " +
                        "FROM muralha.blitz_usuario bu " +
                        "INNER JOIN dbo.sis_usuario u ON bu.id_usuario = u.id_usuario " +
                        "WHERE bu.id_blitz_digital = ? " +
                        "ORDER BY u.nome";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            rs = ps.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setUsuario(rs.getString("usuario"));
                usuario.setNome(rs.getString("nome"));
                usuario.setEmail(rs.getString("email"));
                usuario.setTelefone(rs.getString("telefone"));
                usuarios.add(usuario);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar usuários da blitz", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return usuarios;
    }

    public static List<Guarnicao> listarGuarnicoesBlitz(int idBlitz) {
        List<Guarnicao> guarnicoes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT g.id, g.nome " +
                        "FROM muralha.blitz_guarnicao bg " +
                        "INNER JOIN muralha.guarnicao g ON bg.id_guarnicao = g.id " +
                        "WHERE bg.id_blitz_digital = ? " +
                        "ORDER BY g.nome";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            rs = ps.executeQuery();

            while (rs.next()) {
                Guarnicao guarnicao = new Guarnicao();
                guarnicao.setId(rs.getInt("id"));
                guarnicao.setNome(rs.getString("nome"));
                guarnicoes.add(guarnicao);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar guarnições da blitz", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return guarnicoes;
    }

    public static String verificarTipoAssociacao(int idBlitz) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sqlUsuarios = "SELECT COUNT(*) as count FROM muralha.blitz_usuario WHERE id_blitz_digital = ?";
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sqlUsuarios);
            ps.setInt(1, idBlitz);
            rs = ps.executeQuery();
            
            if (rs.next() && rs.getInt("count") > 0) {
                return "usuario";
            }
            
            rs.close();
            ps.close();
            
            String sqlGuarnicoes = "SELECT COUNT(*) as count FROM muralha.blitz_guarnicao WHERE id_blitz_digital = ?";
            ps = conn.prepareStatement(sqlGuarnicoes);
            ps.setInt(1, idBlitz);
            rs = ps.executeQuery();
            
            if (rs.next() && rs.getInt("count") > 0) {
                return "guarnicao";
            }

        } catch (Exception e) {
            logger.error("Erro ao verificar tipo de associação", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return "usuario";
    }

    public static int salvarBlitz(BlitzDigital blitz) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int idGerado = 0;

        try {
            String sql;
            
            if (blitz.getData_inicio() != null) {
                sql = "INSERT INTO muralha.blitz_digital " +
                    "(nome_blitz, titulo_notificacao, descricao, data_inicio, data_fim, ativo, " +
                    "notificar_agentes_proximos, raio_notificacao_km, id_tipo_blitz, endereco) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            } else {
                sql = "INSERT INTO muralha.blitz_digital " +
                    "(nome_blitz, titulo_notificacao, descricao, data_fim, ativo, " +
                    "notificar_agentes_proximos, raio_notificacao_km, id_tipo_blitz, endereco) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            }
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            
            ps.setString(1, blitz.getNome_blitz());
            ps.setString(2, blitz.getTitulo_notificacao());
            ps.setString(3, blitz.getDescricao());
            
            int paramIndex = 4;
            
            if (blitz.getData_inicio() != null) {
                ps.setTimestamp(paramIndex, blitz.getData_inicio());
                paramIndex++;
            }
            
            if (blitz.getData_fim() != null) {
                ps.setTimestamp(paramIndex, blitz.getData_fim());
                paramIndex++;
            } else {
                ps.setNull(paramIndex, java.sql.Types.TIMESTAMP);
                paramIndex++;
            }
            
            ps.setInt(paramIndex, blitz.getAtivo());
            paramIndex++;
            ps.setInt(paramIndex, blitz.getNotificar_agentes_proximos());
            paramIndex++;
            
            if (blitz.getRaio_notificacao_km() != null) {
                ps.setBigDecimal(paramIndex, blitz.getRaio_notificacao_km());
            } else {
                ps.setNull(paramIndex, java.sql.Types.DECIMAL);
            }
            paramIndex++;
            
            ps.setInt(paramIndex, blitz.getId_tipo_blitz() != null ? blitz.getId_tipo_blitz() : 1);

            paramIndex++;
            if (blitz.getEndereco() != null) {
                ps.setString(paramIndex, blitz.getEndereco());
            } else {
                ps.setNull(paramIndex, java.sql.Types.VARCHAR);
            }
            
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idGerado = rs.getInt(1);
            }

        } catch (Exception e) {
            logger.error("Erro ao salvar blitz", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return idGerado;
    }

    public static boolean salvarLocais(int idBlitz, String locaisStr) {
        String[] locais = locaisStr.split(",");
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO muralha.blitz_local (id_blitz_digital, id_local) VALUES (?, ?)";
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            for (String local : locais) {
                String[] partes = local.split("_");
                int idLocal = Integer.parseInt(partes[0]);
                
                ps.setInt(1, idBlitz);
                ps.setInt(2, idLocal);
                ps.addBatch();
            }

            int[] resultados = ps.executeBatch();
            return resultados.length == locais.length;

        } catch (Exception e) {
            logger.error("Erro ao salvar locais da blitz", e);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    public static boolean salvarUsuarios(int idBlitz, String usuariosStr) {
        String[] usuarios = usuariosStr.split(",");
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO muralha.blitz_usuario (id_blitz_digital, id_usuario) VALUES (?, ?)";
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            for (String usuario : usuarios) {
                int idUsuario = Integer.parseInt(usuario);
                ps.setInt(1, idBlitz);
                ps.setInt(2, idUsuario);
                ps.addBatch();
            }

            int[] resultados = ps.executeBatch();
            return resultados.length == usuarios.length;

        } catch (Exception e) {
            logger.error("Erro ao salvar usuários da blitz", e);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    public static boolean salvarGuarnicoes(int idBlitz, String guarnicoesStr) {
        String[] guarnicoes = guarnicoesStr.split(",");
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO muralha.blitz_guarnicao (id_blitz_digital, id_guarnicao) VALUES (?, ?)";
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            for (String guarnicao : guarnicoes) {
                int idGuarnicao = Integer.parseInt(guarnicao);
                ps.setInt(1, idBlitz);
                ps.setInt(2, idGuarnicao);
                ps.addBatch();
            }

            int[] resultados = ps.executeBatch();
            return resultados.length == guarnicoes.length;

        } catch (Exception e) {
            logger.error("Erro ao salvar guarnições da blitz", e);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    public static boolean ativarDesativarBlitz(int idBlitz, int ativo) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE muralha.blitz_digital SET ativo = ? WHERE id = ?";
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, ativo);
            ps.setInt(2, idBlitz);
            
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            logger.error("Erro ao ativar/desativar blitz", e);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    public static boolean excluirBlitz(int idBlitz) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);

            String sql;

            sql = "DELETE FROM muralha.blitz_documento " +
                "WHERE id_abordagem IN (" +
                "SELECT id FROM muralha.blitz_abordagem WHERE id_blitz_digital = ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            ps.executeUpdate();
            ps.close();

            sql = "DELETE FROM muralha.blitz_abordagem_imagem " +
                "WHERE id_abordagem IN (" +
                "SELECT id FROM muralha.blitz_abordagem WHERE id_blitz_digital = ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            ps.executeUpdate();
            ps.close();

            sql = "DELETE FROM muralha.blitz_pessoa_envolvida " +
                "WHERE id_abordagem IN (" +
                "SELECT id FROM muralha.blitz_abordagem WHERE id_blitz_digital = ?)";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            ps.executeUpdate();
            ps.close();

            sql = "DELETE FROM muralha.blitz_abordagem WHERE id_blitz_digital = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            ps.executeUpdate();
            ps.close();

            sql = "DELETE FROM muralha.blitz_tipo_alerta WHERE id_blitz_digital = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            ps.executeUpdate();
            ps.close();

            sql = "DELETE FROM muralha.blitz_usuario WHERE id_blitz_digital = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            ps.executeUpdate();
            ps.close();

            sql = "DELETE FROM muralha.blitz_guarnicao WHERE id_blitz_digital = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            ps.executeUpdate();
            ps.close();

            sql = "DELETE FROM muralha.blitz_local WHERE id_blitz_digital = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            ps.executeUpdate();
            ps.close();

            sql = "DELETE FROM muralha.blitz_digital WHERE id = ?";
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            int rowsAffected = ps.executeUpdate();

            conn.commit();
            return rowsAffected > 0;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                logger.error("Erro ao fazer rollback", ex);
            }
            logger.error("Erro ao excluir blitz", e);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    public static boolean atualizarBlitz(BlitzDigital blitz, String locaisStr, String tipoAssociacao, String associadosStr, String tiposAlertaStr) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);

            BlitzDigital blitzOriginal = obterBlitzPorId(blitz.getId());

            String sqlUpdateBlitz = "UPDATE muralha.blitz_digital SET " +
                    "nome_blitz = ?, titulo_notificacao = ?, descricao = ?, " +
                    "data_inicio = ?, data_fim = ?, ativo = ?, " +
                    "notificar_agentes_proximos = ?, raio_notificacao_km = ?, " +
                    "id_tipo_blitz = ?, endereco = ? " +
                    "WHERE id = ?";

            ps = conn.prepareStatement(sqlUpdateBlitz);
            ps.setString(1, blitz.getNome_blitz());
            ps.setString(2, blitz.getTitulo_notificacao());
            ps.setString(3, blitz.getDescricao());

            if (blitz.getData_inicio() != null) {
                ps.setTimestamp(4, blitz.getData_inicio());
            } else {
                ps.setTimestamp(4, blitzOriginal.getData_inicio());
            }

            ps.setTimestamp(5, blitz.getData_fim());
            ps.setInt(6, blitz.getAtivo());
            ps.setInt(7, blitz.getNotificar_agentes_proximos());

            if (blitz.getRaio_notificacao_km() != null) {
                ps.setBigDecimal(8, blitz.getRaio_notificacao_km());
            } else {
                ps.setNull(8, java.sql.Types.DECIMAL);
            }

            ps.setInt(9, blitz.getId_tipo_blitz());
            ps.setString(10, blitz.getEndereco());
            ps.setInt(11, blitz.getId());
            ps.executeUpdate();
            ps.close();

            if (blitz.getId_tipo_blitz() == 1) {
                if (locaisStr != null) {
                    String sqlDeleteLocais = "DELETE FROM muralha.blitz_local WHERE id_blitz_digital = ?";
                    ps = conn.prepareStatement(sqlDeleteLocais);
                    ps.setInt(1, blitz.getId());
                    ps.executeUpdate();
                    ps.close();

                    if (!locaisStr.isEmpty()) {
                        String[] locais = locaisStr.split(",");
                        String sqlInsertLocais = "INSERT INTO muralha.blitz_local (id_blitz_digital, id_local) VALUES (?, ?)";
                        ps = conn.prepareStatement(sqlInsertLocais);

                        for (String local : locais) {
                            String[] partes = local.split("_");
                            int idLocal = Integer.parseInt(partes[0]);
                            ps.setInt(1, blitz.getId());
                            ps.setInt(2, idLocal);
                            ps.addBatch();
                        }
                        ps.executeBatch();
                        ps.close();
                    }
                }
            }

            String sqlDeleteUsuarios = "DELETE FROM muralha.blitz_usuario WHERE id_blitz_digital = ?";
            ps = conn.prepareStatement(sqlDeleteUsuarios);
            ps.setInt(1, blitz.getId());
            ps.executeUpdate();
            ps.close();

            String sqlDeleteGuarnicoes = "DELETE FROM muralha.blitz_guarnicao WHERE id_blitz_digital = ?";
            ps = conn.prepareStatement(sqlDeleteGuarnicoes);
            ps.setInt(1, blitz.getId());
            ps.executeUpdate();
            ps.close();

            String[] associados = associadosStr.split(",");
            if ("usuario".equals(tipoAssociacao)) {
                String sqlInsertUsuarios = "INSERT INTO muralha.blitz_usuario (id_blitz_digital, id_usuario) VALUES (?, ?)";
                ps = conn.prepareStatement(sqlInsertUsuarios);

                for (String associado : associados) {
                    int idUsuario = Integer.parseInt(associado);
                    ps.setInt(1, blitz.getId());
                    ps.setInt(2, idUsuario);
                    ps.addBatch();
                }
            } else {
                String sqlInsertGuarnicoes = "INSERT INTO muralha.blitz_guarnicao (id_blitz_digital, id_guarnicao) VALUES (?, ?)";
                ps = conn.prepareStatement(sqlInsertGuarnicoes);

                for (String associado : associados) {
                    int idGuarnicao = Integer.parseInt(associado);
                    ps.setInt(1, blitz.getId());
                    ps.setInt(2, idGuarnicao);
                    ps.addBatch();
                }
            }

            ps.executeBatch();
            ps.close();

            String sqlDeleteTiposAlerta = "DELETE FROM muralha.blitz_tipo_alerta WHERE id_blitz_digital = ?";
            ps = conn.prepareStatement(sqlDeleteTiposAlerta);
            ps.setInt(1, blitz.getId());
            ps.executeUpdate();
            ps.close();

            if (tiposAlertaStr != null && !tiposAlertaStr.isEmpty()) {
                String[] tiposAlerta = tiposAlertaStr.split(",");
                String sqlInsertTiposAlerta = "INSERT INTO muralha.blitz_tipo_alerta (id_blitz_digital, id_tipo_alerta_ocorrencia) VALUES (?, ?)";
                ps = conn.prepareStatement(sqlInsertTiposAlerta);

                for (String tipoAlerta : tiposAlerta) {
                    ps.setInt(1, blitz.getId());
                    ps.setString(2, tipoAlerta);
                    ps.addBatch();
                }
                ps.executeBatch();
                ps.close();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                logger.error("Erro ao fazer rollback", ex);
            }
            logger.error("Erro ao atualizar blitz", e);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    public static List<TipoAlerta> listarTiposAlerta() {
        List<TipoAlerta> tipos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id, tipo, descricao FROM muralha.tipo_alerta_ocorrencia WHERE tarefa_ativa = 1 ORDER BY tipo";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                TipoAlerta tipo = new TipoAlerta();
                tipo.setId(rs.getString("id"));
                tipo.setTipo(rs.getString("tipo"));
                tipo.setDescricao(rs.getString("descricao"));
                tipos.add(tipo);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar tipos de alerta", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return tipos;
    }

    public static List<TipoAlerta> listarTiposAlertaBlitz(int idBlitz) {
        List<TipoAlerta> tipos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT tao.id, tao.tipo, tao.descricao " +
                        "FROM muralha.blitz_tipo_alerta bta " +
                        "INNER JOIN muralha.tipo_alerta_ocorrencia tao ON bta.id_tipo_alerta_ocorrencia = tao.id " +
                        "WHERE bta.id_blitz_digital = ? AND bta.ativo = 1 " +
                        "ORDER BY tao.tipo";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            rs = ps.executeQuery();

            while (rs.next()) {
                TipoAlerta tipo = new TipoAlerta();
                tipo.setId(rs.getString("id"));
                tipo.setTipo(rs.getString("tipo"));
                tipo.setDescricao(rs.getString("descricao"));
                tipos.add(tipo);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar tipos de alerta da blitz", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return tipos;
    }

    public static boolean salvarTiposAlerta(int idBlitz, String tiposAlertaStr) {
        if (tiposAlertaStr == null || tiposAlertaStr.isEmpty()) {
            return true; // Não há tipos para salvar - considera sucesso
        }
        
        String[] tiposAlerta = tiposAlertaStr.split(",");
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            String sql = "INSERT INTO muralha.blitz_tipo_alerta (id_blitz_digital, id_tipo_alerta_ocorrencia) VALUES (?, ?)";
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);

            for (String tipoAlerta : tiposAlerta) {
                ps.setInt(1, idBlitz);
                ps.setString(2, tipoAlerta);
                ps.addBatch();
            }

            int[] resultados = ps.executeBatch();
            return resultados.length == tiposAlerta.length;

        } catch (Exception e) {
            logger.error("Erro ao salvar tipos de alerta da blitz", e);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    public static List<String> listarTiposEnvolvimento() {
        List<String> tipos = new ArrayList<>();
        tipos.add("CONDUTOR");
        tipos.add("PASSAGEIRO");
        tipos.add("PROPRIETARIO");
        return tipos;
    }
    
    public static List<String> listarSituacoesDocumento() {
        List<String> situacoes = new ArrayList<>();
        situacoes.add("VALIDO");
        situacoes.add("VENCIDO");
        situacoes.add("IRREGULAR");
        situacoes.add("SUSPENSO");
        return situacoes;
    }
    
    public static List<String> listarTiposDocumento() {
        List<String> tipos = new ArrayList<>();
        tipos.add("CNH");
        tipos.add("RG");
        tipos.add("CPF");
        tipos.add("CRLV");
        tipos.add("ANTT");
        tipos.add("CRV");
        tipos.add("SEGURO");
        return tipos;
    }

    public static Long salvarAbordagemCompleta(
        BlitzAbordagem abordagem, 
        List<BlitzPessoaEnvolvida> pessoas,
        List<BlitzDocumento> documentos,
        List<BlitzImagemAbordagem> imagens
    ) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Long idAbordagemGerado = null;
        
        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);
            
            String sqlAbordagem = "INSERT INTO muralha.blitz_abordagem " +
                                "(id_blitz_digital, id_agente, id_alerta, id_local, " +
                                "placa_veiculo, data_abordagem, latitude, longitude, " +
                                "id_status, motivo_cancelamento, observacoes, id_registro_fato, " +
                                "id_abordagem_origem, id_veiculo_tempo_real, id_resultado, cor_veiculo, marca_veiculo, modelo_veiculo, tipo_veiculo) " + 
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            ps = conn.prepareStatement(sqlAbordagem, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, abordagem.getId_blitz_digital());
            ps.setInt(2, abordagem.getId_agente());
            
            if (abordagem.getId_alerta() != null && !abordagem.getId_alerta().isEmpty()) {
                ps.setString(3, abordagem.getId_alerta());
            } else {
                ps.setNull(3, java.sql.Types.VARCHAR);
            }
            
            if (abordagem.getId_local() != null) {
                ps.setInt(4, abordagem.getId_local());
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            
            ps.setString(5, abordagem.getPlaca_veiculo());
            ps.setTimestamp(6, abordagem.getData_abordagem());
            
            if (abordagem.getLatitude() != null) {
                ps.setDouble(7, abordagem.getLatitude());
            } else {
                ps.setNull(7, java.sql.Types.DECIMAL);
            }
            
            if (abordagem.getLongitude() != null) {
                ps.setDouble(8, abordagem.getLongitude());
            } else {
                ps.setNull(8, java.sql.Types.DECIMAL);
            }
            
            Integer idStatus = null;
            if (abordagem.getStatus() != null && !abordagem.getStatus().isEmpty()) {
                idStatus = obterIdStatusAbordagemPorCodigo(abordagem.getStatus());
            }
            
            if (idStatus != null) {
                ps.setInt(9, idStatus);
            } else {
                ps.setNull(9, java.sql.Types.INTEGER);
            }
            
            ps.setString(10, abordagem.getMotivo_cancelamento());
            ps.setString(11, abordagem.getObservacoes());
            
            if (abordagem.getId_registro_fato() != null) {
                ps.setLong(12, abordagem.getId_registro_fato());
            } else {
                ps.setNull(12, java.sql.Types.BIGINT);
            }
            
            String origem = abordagem.getOrigemAbordagem();
            if (origem != null && !origem.isEmpty()) {
                if ("MANUAL".equals(origem)) {
                    ps.setInt(13, 1);
                } else if ("ALERTA".equals(origem)) {
                    ps.setInt(13, 2);
                } else if ("BLITZ OSTENSIVA".equals(origem)) {
                    ps.setInt(13, 3);
                } else {
                    ps.setNull(13, java.sql.Types.TINYINT);
                }
            } else {
                ps.setNull(13, java.sql.Types.TINYINT);
            }
            
            if (abordagem.getId_veiculo_tempo_real() != null && 
                !abordagem.getId_veiculo_tempo_real().isEmpty() && 
                !"null".equals(abordagem.getId_veiculo_tempo_real())) {
                try {
                    ps.setObject(14, java.util.UUID.fromString(abordagem.getId_veiculo_tempo_real()));
                } catch (IllegalArgumentException e) {
                    ps.setNull(14, java.sql.Types.OTHER);
                }
            } else {
                ps.setNull(14, java.sql.Types.OTHER);
            }
            
            if (abordagem.getId_resultado() != null) {
                ps.setInt(15, abordagem.getId_resultado());
            } else {
                ps.setNull(15, java.sql.Types.INTEGER);
            }
            
            ps.setString(16, abordagem.getCor_veiculo());
            ps.setString(17, abordagem.getMarca_veiculo());
            ps.setString(18, abordagem.getModelo_veiculo());
            ps.setString(19, abordagem.getTipo_veiculo());
            
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idAbordagemGerado = rs.getLong(1);
            }
            rs.close();
            ps.close();
            
            if (idAbordagemGerado == null) {
                throw new SQLException("Falha ao obter ID da abordagem");
            }
            
            Map<Long, Long> mapPessoas = new HashMap<>();
            if (pessoas != null && !pessoas.isEmpty()) {
                String sqlPessoa = "INSERT INTO muralha.blitz_pessoa_envolvida " +
                                "(id_abordagem, cpf, nome_completo, data_nascimento, " +
                                "tipo_envolvimento, telefone, sexo, email, observacoes) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                for (int i = 0; i < pessoas.size(); i++) {
                    BlitzPessoaEnvolvida pessoa = pessoas.get(i);
                    
                    ps = conn.prepareStatement(sqlPessoa, PreparedStatement.RETURN_GENERATED_KEYS);
                    
                    ps.setLong(1, idAbordagemGerado);
                    
                    if (pessoa.getCpf() != null && !pessoa.getCpf().isEmpty()) {
                        ps.setString(2, pessoa.getCpf());
                    } else {
                        ps.setNull(2, java.sql.Types.VARCHAR);
                    }
                    
                    ps.setString(3, pessoa.getNome_completo());
                    
                    if (pessoa.getData_nascimento() != null) {
                        ps.setDate(4, new java.sql.Date(pessoa.getData_nascimento().getTime()));
                    } else {
                        ps.setNull(4, java.sql.Types.DATE);
                    }
                    
                    ps.setString(5, pessoa.getTipo_envolvimento());
                    ps.setString(6, pessoa.getTelefone());
                    
                    if (pessoa.getSexo() != null) {
                        ps.setString(7, String.valueOf(pessoa.getSexo()));
                    } else {
                        ps.setNull(7, java.sql.Types.CHAR);
                    }
                    
                    ps.setString(8, pessoa.getEmail());
                    ps.setString(9, pessoa.getObservacoes());
                    
                    ps.executeUpdate();
                    
                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        Long idPessoaGerado = rs.getLong(1);
                        if (pessoa.getId() != null) {
                            mapPessoas.put(pessoa.getId(), idPessoaGerado);
                        }
                    }
                    rs.close();
                    ps.close();
                }
            }
            
            if (documentos != null && !documentos.isEmpty()) {
                String sqlDocumento = "INSERT INTO muralha.blitz_documento " +
                                    "(id_abordagem, id_pessoa, tipo_documento, numero_documento, " +
                                    "nome_titular, validade, situacao, observacoes) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                for (int i = 0; i < documentos.size(); i++) {
                    BlitzDocumento documento = documentos.get(i);
                    
                    ps = conn.prepareStatement(sqlDocumento, PreparedStatement.RETURN_GENERATED_KEYS);
                    
                    ps.setLong(1, idAbordagemGerado);
                    
                    if (documento.getId_pessoa() != null && mapPessoas.containsKey(documento.getId_pessoa())) {
                        ps.setLong(2, mapPessoas.get(documento.getId_pessoa()));
                    } else {
                        ps.setNull(2, java.sql.Types.BIGINT);
                    }
                    
                    ps.setString(3, documento.getTipo_documento());
                    ps.setString(4, documento.getNumero_documento());
                    ps.setString(5, documento.getNome_titular());
                    
                    if (documento.getValidade() != null) {
                        ps.setDate(6, new java.sql.Date(documento.getValidade().getTime()));
                    } else {
                        ps.setNull(6, java.sql.Types.DATE);
                    }
                    
                    ps.setString(7, documento.getSituacao());
                    ps.setString(8, documento.getObservacoes());
                    
                    ps.executeUpdate();
                    
                    rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        Long idDocumentoGerado = rs.getLong(1);
                        
                        if (documento.getArquivos() != null && !documento.getArquivos().isEmpty()) {
                            salvarArquivosDocumento(conn, idDocumentoGerado, documento.getArquivos());
                        }
                    }
                    rs.close();
                    ps.close();
                }
            }
            
            if (imagens != null && !imagens.isEmpty()) {
                String sqlImagem = "INSERT INTO muralha.blitz_abordagem_imagem " +
                    "(id_abordagem, nome_arquivo, caminho_arquivo, tipo_arquivo, id_usuario) " +
                    "VALUES (?, ?, ?, ?, ?)";
                
                for (BlitzImagemAbordagem img : imagens) {
                    ps = conn.prepareStatement(sqlImagem);
                    
                    ps.setLong(1, idAbordagemGerado);
                    ps.setString(2, img.getNome_arquivo_original());
                    ps.setString(3, img.getCaminho_arquivo());
                    ps.setString(4, img.getTipo_arquivo());
                    ps.setInt(5, img.getId_usuario());
                    
                    ps.executeUpdate();
                    ps.close();
                }
            }
            
            conn.commit();
            return idAbordagemGerado;
            
        } catch (Exception e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                logger.error("Erro ao fazer rollback", ex);
            }
            logger.error("Erro ao salvar abordagem completa", e);
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    private static void salvarArquivosDocumento(Connection conn, Long idDocumento, List<BlitzDocumentoArquivo> arquivos) throws SQLException {
        if (arquivos == null || arquivos.isEmpty()) return;
        
        String sqlArquivo = "INSERT INTO muralha.blitz_documento_arquivo " +
                        "(id_documento, caminho_arquivo, nome_arquivo_original, tipo_arquivo) " +
                        "VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sqlArquivo)) {
            for (BlitzDocumentoArquivo arquivo : arquivos) {
                ps.setLong(1, idDocumento);
                ps.setString(2, arquivo.getCaminho_arquivo());
                ps.setString(3, arquivo.getNome_arquivo_original());
                ps.setString(4, arquivo.getTipo_arquivo());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    
    public static BlitzAbordagem obterAbordagemPorId(Long idAbordagem) {
        BlitzAbordagem abordagem = null;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM muralha.blitz_abordagem WHERE id = ?";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, idAbordagem);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                abordagem = new BlitzAbordagem();
                abordagem.setId(rs.getLong("id"));
                abordagem.setId_blitz_digital(rs.getInt("id_blitz_digital"));
                abordagem.setId_agente(rs.getInt("id_agente"));
                abordagem.setId_resultado(rs.getInt("id_resultado"));
                abordagem.setId_alerta(rs.getString("id_alerta"));
                abordagem.setId_local(rs.getInt("id_local"));
                abordagem.setPlaca_veiculo(rs.getString("placa_veiculo"));
                abordagem.setData_abordagem(rs.getTimestamp("data_abordagem"));
                abordagem.setLatitude(rs.getDouble("latitude"));
                abordagem.setLongitude(rs.getDouble("longitude"));
                abordagem.setId_status(rs.getInt("id_status"));
                abordagem.setStatus(buscarCodigoAbordagemPorId(rs.getInt("id_status")));
                abordagem.setMotivo_cancelamento(rs.getString("motivo_cancelamento"));
                abordagem.setObservacoes(rs.getString("observacoes"));
                abordagem.setId_registro_fato(rs.getLong("id_registro_fato"));
                abordagem.setData_criacao(rs.getTimestamp("data_criacao"));
            }
            
        } catch (Exception e) {
            logger.error("Erro ao obter abordagem por ID", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return abordagem;
    }
    
    public static List<BlitzAbordagem> listarAbordagensPorBlitz(Integer idBlitzDigital) {
        List<BlitzAbordagem> abordagens = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM muralha.blitz_abordagem " +
                        "WHERE id_blitz_digital = ? " +
                        "ORDER BY data_abordagem DESC";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitzDigital);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                BlitzAbordagem abordagem = new BlitzAbordagem();
                abordagem.setId(rs.getLong("id"));
                abordagem.setId_blitz_digital(rs.getInt("id_blitz_digital"));
                abordagem.setId_agente(rs.getInt("id_agente"));
                abordagem.setId_resultado(rs.getInt("id_resultado"));
                abordagem.setId_alerta(rs.getString("id_alerta"));
                abordagem.setId_local(rs.getInt("id_local"));
                abordagem.setPlaca_veiculo(rs.getString("placa_veiculo"));
                abordagem.setData_abordagem(rs.getTimestamp("data_abordagem"));
                abordagem.setLatitude(rs.getDouble("latitude"));
                abordagem.setLongitude(rs.getDouble("longitude"));
                abordagem.setId_status(rs.getInt("id_status"));
                abordagem.setStatus(buscarCodigoAbordagemPorId(rs.getInt("id_status")));
                abordagem.setMotivo_cancelamento(rs.getString("motivo_cancelamento"));
                abordagem.setObservacoes(rs.getString("observacoes"));
                abordagem.setId_registro_fato(rs.getLong("id_registro_fato"));
                abordagem.setData_criacao(rs.getTimestamp("data_criacao"));
                abordagens.add(abordagem);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao listar abordagens por blitz", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return abordagens;
    }
    
    public static List<BlitzPessoaEnvolvida> listarPessoasPorAbordagem(Long idAbordagem) {
        List<BlitzPessoaEnvolvida> pessoas = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM muralha.blitz_pessoa_envolvida " +
                        "WHERE id_abordagem = ? " +
                        "ORDER BY tipo_envolvimento, nome_completo";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, idAbordagem);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                BlitzPessoaEnvolvida pessoa = new BlitzPessoaEnvolvida();
                pessoa.setId(rs.getLong("id"));
                pessoa.setId_abordagem(rs.getLong("id_abordagem"));
                pessoa.setCpf(rs.getString("cpf"));
                pessoa.setNome_completo(rs.getString("nome_completo"));
                pessoa.setData_nascimento(rs.getDate("data_nascimento"));
                pessoa.setTipo_envolvimento(rs.getString("tipo_envolvimento"));
                pessoa.setTelefone(rs.getString("telefone"));
                
                String sexo = rs.getString("sexo");
                if (sexo != null && !sexo.isEmpty()) {
                    pessoa.setSexo(sexo.charAt(0));
                }
                
                pessoa.setEmail(rs.getString("email"));
                pessoa.setObservacoes(rs.getString("observacoes"));
                pessoa.setData_criacao(rs.getTimestamp("data_criacao"));
                pessoas.add(pessoa);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao listar pessoas por abordagem", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return pessoas;
    }
    
    public static List<BlitzDocumento> listarDocumentosPorAbordagem(Long idAbordagem) {
        List<BlitzDocumento> documentos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT d.* FROM muralha.blitz_documento d " +
                        "WHERE d.id_abordagem = ? " +
                        "ORDER BY d.tipo_documento";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, idAbordagem);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                BlitzDocumento documento = new BlitzDocumento();
                documento.setId(rs.getLong("id"));
                documento.setId_abordagem(rs.getLong("id_abordagem"));
                documento.setId_pessoa(rs.getLong("id_pessoa"));
                documento.setTipo_documento(rs.getString("tipo_documento"));
                documento.setNumero_documento(rs.getString("numero_documento"));
                documento.setNome_titular(rs.getString("nome_titular"));
                documento.setValidade(rs.getDate("validade"));
                documento.setSituacao(rs.getString("situacao"));
                documento.setObservacoes(rs.getString("observacoes"));
                documento.setData_criacao(rs.getTimestamp("data_criacao"));
                
                carregarArquivosDocumento(documento);
                
                documentos.add(documento);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao listar documentos por abordagem", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return documentos;
    }

    private static void carregarArquivosDocumento(BlitzDocumento documento) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT * FROM muralha.blitz_documento_arquivo WHERE id_documento = ?";
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, documento.getId());
            rs = ps.executeQuery();
            
            while (rs.next()) {
                BlitzDocumentoArquivo arquivo = new BlitzDocumentoArquivo();
                arquivo.setId(rs.getLong("id"));
                arquivo.setId_documento(rs.getLong("id_documento"));
                arquivo.setCaminho_arquivo(rs.getString("caminho_arquivo"));
                arquivo.setNome_arquivo_original(rs.getString("nome_arquivo_original"));
                arquivo.setTipo_arquivo(rs.getString("tipo_arquivo"));
                arquivo.setData_criacao(rs.getTimestamp("data_criacao"));
                
                documento.addArquivo(arquivo);
            }
        } catch (Exception e) {
            logger.error("Erro ao carregar arquivos do documento", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    public static List<Map<String, Object>> listarAbordagensComFiltro(Date dataInicio, Date dataFim, Integer idBlitz) {
        List<Map<String, Object>> abordagens = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT ba.*, bd.nome_blitz, su.nome as nome_agente ");
            sql.append("FROM muralha.blitz_abordagem ba ");
            sql.append("INNER JOIN muralha.blitz_digital bd ON ba.id_blitz_digital = bd.id ");
            sql.append("INNER JOIN dbo.sis_usuario su ON ba.id_agente = su.id_usuario ");
            sql.append("WHERE 1=1 ");
            
            List<Object> parametros = new ArrayList<>();
            
            if (dataInicio != null) {
                sql.append("AND ba.data_abordagem >= ? ");
                parametros.add(new java.sql.Timestamp(dataInicio.getTime()));
            }
            
            if (dataFim != null) {
                sql.append("AND ba.data_abordagem <= ? ");
                parametros.add(new java.sql.Timestamp(dataFim.getTime()));
            }
            
            if (idBlitz != null) {
                sql.append("AND ba.id_blitz_digital = ? ");
                parametros.add(idBlitz);
            }
            
            sql.append("ORDER BY ba.data_abordagem DESC");
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql.toString());
            
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> abordagem = new HashMap<>();
                abordagem.put("id", rs.getLong("id"));
                abordagem.put("id_blitz_digital", rs.getInt("id_blitz_digital"));
                abordagem.put("id_agente", rs.getInt("id_agente"));
                abordagem.put("id_resultado", rs.getInt("id_resultado"));
                abordagem.put("id_alerta", rs.getString("id_alerta"));
                abordagem.put("id_local", rs.getInt("id_local"));
                abordagem.put("placa_veiculo", rs.getString("placa_veiculo"));
                abordagem.put("data_abordagem", rs.getTimestamp("data_abordagem"));
                abordagem.put("latitude", rs.getDouble("latitude"));
                abordagem.put("longitude", rs.getDouble("longitude"));
                abordagem.put("id_status", rs.getInt("id_status"));
                abordagem.put("status", buscarCodigoAbordagemPorId(rs.getInt("id_status")));
                abordagem.put("motivo_cancelamento", rs.getString("motivo_cancelamento"));
                abordagem.put("observacoes", rs.getString("observacoes"));
                abordagem.put("id_registro_fato", rs.getLong("id_registro_fato"));
                abordagem.put("data_criacao", rs.getTimestamp("data_criacao"));
                abordagem.put("nome_blitz", rs.getString("nome_blitz"));
                abordagem.put("nome_agente", rs.getString("nome_agente"));
                abordagens.add(abordagem);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao listar abordagens com filtro", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return abordagens;
    }

    public static Map<String, Object> obterAbordagemDetalhada(Long idAbordagem) {
        Map<String, Object> resultado = new HashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sqlAbordagem = "SELECT ba.*, bd.nome_blitz, l.nome as nome_local, su.nome as nome_agente " +
                                "FROM muralha.blitz_abordagem ba " +
                                "INNER JOIN muralha.blitz_digital bd ON ba.id_blitz_digital = bd.id " +
                                "LEFT JOIN dbo.[local] l ON ba.id_local = l.id_local " +
                                "INNER JOIN dbo.sis_usuario su ON ba.id_agente = su.id_usuario " +
                                "WHERE ba.id = ?";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sqlAbordagem);
            ps.setLong(1, idAbordagem);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> abordagem = new HashMap<>();
                abordagem.put("id", rs.getLong("id"));
                abordagem.put("id_blitz_digital", rs.getInt("id_blitz_digital"));
                abordagem.put("id_agente", rs.getInt("id_agente"));
                abordagem.put("id_resultado", rs.getInt("id_resultado"));
                abordagem.put("id_alerta", rs.getString("id_alerta"));
                abordagem.put("id_local", rs.getInt("id_local"));
                abordagem.put("placa_veiculo", rs.getString("placa_veiculo"));
                abordagem.put("marca_veiculo", rs.getString("marca_veiculo"));
                abordagem.put("modelo_veiculo", rs.getString("modelo_veiculo"));
                abordagem.put("tipo_veiculo", rs.getString("tipo_veiculo"));
                abordagem.put("cor_veiculo", rs.getString("cor_veiculo"));
                abordagem.put("data_abordagem", rs.getTimestamp("data_abordagem"));
                abordagem.put("latitude", rs.getDouble("latitude"));
                abordagem.put("longitude", rs.getDouble("longitude"));
                abordagem.put("id_status", rs.getInt("id_status"));
                abordagem.put("status", buscarCodigoAbordagemPorId(rs.getInt("id_status")));
                abordagem.put("resultado", buscarResultadoAbordagemPorId(rs.getInt("id_resultado")));
                abordagem.put("motivo_cancelamento", rs.getString("motivo_cancelamento"));
                abordagem.put("observacoes", rs.getString("observacoes"));
                abordagem.put("id_registro_fato", rs.getLong("id_registro_fato"));
                abordagem.put("id_veiculo_tempo_real", rs.getString("id_veiculo_tempo_real"));
                abordagem.put("data_criacao", rs.getTimestamp("data_criacao"));
                abordagem.put("nome_blitz", rs.getString("nome_blitz"));
                abordagem.put("nome_local", rs.getString("nome_local"));
                abordagem.put("nome_agente", rs.getString("nome_agente"));
                
                resultado.put("abordagem", abordagem);
                
                String idAlerta = rs.getString("id_alerta");
                if (idAlerta != null && !idAlerta.isEmpty()) {
                    rs.close();
                    ps.close();
                    
                    String sqlAlerta = "SELECT a.*, tao.tipo as tipo_alerta, cadv.placa " +
                                    "FROM muralha.alerta a " +
                                    "INNER JOIN muralha.tipo_alerta_ocorrencia tao ON a.id_tipo_alerta_ocorrencia = tao.id " +
                                    "LEFT JOIN muralha.cad_veiculo_monitorado cadv ON a.id_cad_veiculo_monitorado = cadv.id " +
                                    "WHERE a.id = ?";
                    
                    ps = conn.prepareStatement(sqlAlerta);
                    ps.setString(1, idAlerta);
                    rs = ps.executeQuery();
                    
                    if (rs.next()) {
                        Map<String, Object> alerta = new HashMap<>();
                        alerta.put("id", rs.getString("id"));
                        alerta.put("tipo_alerta", rs.getString("tipo_alerta"));
                        alerta.put("placa", rs.getString("placa"));
                        alerta.put("data", rs.getTimestamp("data"));
                        resultado.put("alerta", alerta);
                    }
                }
            }
            
        } catch (Exception e) {
            logger.error("Erro ao obter abordagem detalhada", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return resultado;
    }

    public static boolean encerrarBlitz(int idBlitz) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            String sql = "UPDATE muralha.blitz_digital " +
                        "SET data_fim = GETDATE(), ativo = 0 " +
                        "WHERE id = ?";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            logger.error("Erro ao encerrar blitz", e);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
    }

    public static List<Map<String, Object>> listarRegistrosFato() {
        List<Map<String, Object>> registros = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = 
                "SELECT rf.id, " +
                "       rf.data_evento, " +
                "       rf.data_criacao, " +
                "       rfn.natureza_desc " +
                "FROM muralha.registro_fato rf " +
                "LEFT JOIN muralha.registro_fato_natureza rfn ON rfn.id = rf.id_tipo_natureza " +
                "WHERE rf.id_status = 1" + // Status ativo
                "ORDER BY rf.data_evento DESC";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> registro = new HashMap<>();
                Long id = rs.getLong("id");
                Date dataEvento = rs.getTimestamp("data_evento");
                Date dataCriacao = rs.getTimestamp("data_criacao");
                String naturezaDesc = rs.getString("natureza_desc");
                
                registro.put("id", id);
                registro.put("descricao", naturezaDesc);
                registro.put("tipo", naturezaDesc);
                
                String dataFormatada = "";
                if (dataEvento != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    dataFormatada = sdf.format(dataEvento);
                }

                String dataCriacaoFormatada = "";
                if (dataCriacaoFormatada != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    dataCriacaoFormatada = sdf.format(dataCriacao);
                }
                registro.put("data_formatada", dataFormatada);
                registro.put("data_criacao", dataCriacaoFormatada);
                
                registros.add(registro);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao listar registros de fato", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return registros;
    }

    public static Map<String, Object> obterDetalhesRegistroFato(Long idRegistroFato) {
        Map<String, Object> registro = new HashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = 
                "SELECT rf.*, rft.tipo_desc, rfs.descricao as status_desc, " +
                "       rfn.natureza_desc, rfnd.natureza_delituosa_desc, rfnd.codigo_penal_lei " +
                "FROM muralha.registro_fato rf " +
                "LEFT JOIN muralha.registro_fato_tipo rft ON rft.id = rf.id_tipo " +
                "LEFT JOIN muralha.registro_fato_status rfs ON rfs.id = rf.id_status " +
                "LEFT JOIN muralha.registro_fato_natureza rfn ON rfn.id = rf.id_tipo_natureza " +
                "LEFT JOIN muralha.registro_fato_natureza_delituosa rfnd ON rfnd.id_registro_natureza = rfn.id " +
                "WHERE rf.id = ?";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, idRegistroFato);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                registro.put("id", rs.getLong("id"));
                registro.put("tipo_desc", rs.getString("tipo_desc"));
                registro.put("status_desc", rs.getString("status_desc"));
                registro.put("natureza_desc", rs.getString("natureza_desc"));
                registro.put("natureza_delituosa_desc", rs.getString("natureza_delituosa_desc"));
                registro.put("codigo_penal_lei", rs.getString("codigo_penal_lei"));
                registro.put("tem_boletim", rs.getInt("tem_boletim"));
                registro.put("privado", rs.getInt("privado"));
                registro.put("data_evento", rs.getTimestamp("data_evento"));
                registro.put("data_criacao", rs.getTimestamp("data_criacao"));
                registro.put("data_encerramento", rs.getTimestamp("data_encerramento"));
            }
            
        } catch (Exception e) {
            logger.error("Erro ao obter detalhes do registro de fato", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return registro;
    }

    public static List<BlitzDigital> listarBlitzAtivasPorLocal(int idLocal) {
        List<BlitzDigital> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT DISTINCT bd.id, bd.nome_blitz, bd.titulo_notificacao, bd.descricao, " +
                        "bd.data_inicio, bd.data_fim, bd.data_criacao, bd.ativo, " +
                        "bd.notificar_agentes_proximos, bd.raio_notificacao_km " +
                        "FROM muralha.blitz_digital bd " +
                        "INNER JOIN muralha.blitz_local bl ON bd.id = bl.id_blitz_digital " +
                        "WHERE bd.ativo = 1 " +
                        "AND bl.id_local = ? " +
                        "ORDER BY bd.data_criacao DESC";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idLocal);
            rs = ps.executeQuery();

            while (rs.next()) {
                BlitzDigital blitz = new BlitzDigital();
                blitz.setId(rs.getInt("id"));
                blitz.setNome_blitz(rs.getString("nome_blitz"));
                blitz.setTitulo_notificacao(rs.getString("titulo_notificacao"));
                blitz.setDescricao(rs.getString("descricao"));
                blitz.setData_inicio(rs.getTimestamp("data_inicio"));
                blitz.setData_fim(rs.getTimestamp("data_fim"));
                blitz.setData_criacao(rs.getTimestamp("data_criacao"));
                blitz.setAtivo(rs.getInt("ativo"));
                blitz.setNotificar_agentes_proximos(rs.getInt("notificar_agentes_proximos"));
                blitz.setRaio_notificacao_km(rs.getBigDecimal("raio_notificacao_km"));
                
                lista.add(blitz);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar blitz digitais por local", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return lista;
    }

    public static Map<String, Object> obterInformacoesAlerta(String idAlerta) {
        Map<String, Object> info = new HashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT " +
                        "a.id as id_alerta, " +
                        "cv.placa as placa, " +
                        "vtr.id_local as id_local, " +
                        "cv.id_registro_fato as id_registro_fato " +
                        "FROM muralha.alerta a " +
                        "INNER JOIN muralha.alerta_veiculo av ON a.id = av.id_alerta " +
                        "INNER JOIN muralha.veiculo_tempo_real vtr ON av.id_veiculo_tempo_real = vtr.id " +
                        "LEFT JOIN muralha.cad_veiculo_monitorado cv ON a.id_cad_veiculo_monitorado = cv.id " +
                        "WHERE a.id = ?";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setString(1, idAlerta);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                info.put("id_alerta", rs.getString("id_alerta"));
                info.put("placa", rs.getString("placa"));
                info.put("id_local", rs.getObject("id_local") != null ? rs.getInt("id_local") : null);
                info.put("id_registro_fato", rs.getObject("id_registro_fato") != null ? rs.getLong("id_registro_fato") : null);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao obter informações do alerta", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return info;
    }

    public static boolean verificarSeExisteAbordagemNoBanco(String idAlerta) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = "SELECT COUNT(*) as total FROM muralha.blitz_abordagem WHERE id_alerta = ?";
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setString(1, idAlerta);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
            
        } catch (Exception e) {
            logger.error("Erro ao verificar se existe abordagem", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return false;
    }

    public static boolean verificarSeAlertaEhDeBlitz(String idAlerta) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            String sql = 
                "SELECT CASE WHEN EXISTS ( " +
                "    SELECT 1 " +
                "    FROM muralha.alerta_veiculo av " +
                "    INNER JOIN muralha.veiculo_tempo_real vtr ON av.id_veiculo_tempo_real = vtr.id " +
                "    INNER JOIN muralha.blitz_local bl ON vtr.id_local = bl.id_local " +
                "    INNER JOIN muralha.blitz_digital bd ON bl.id_blitz_digital = bd.id " +
                "    INNER JOIN muralha.blitz_tipo bt ON bd.id_tipo_blitz = bt.id " +
                "    WHERE av.id_alerta = ? " +
                "    AND bt.codigo = 'AUTOMATICA' " +
                "    AND bd.data_inicio IS NOT NULL " +
                "    AND GETDATE() >= bd.data_inicio " +
                "    AND (bd.data_fim IS NULL OR GETDATE() <= bd.data_fim) " +
                "    AND vtr.data >= bd.data_inicio " +
                "    AND (bd.data_fim IS NULL OR vtr.data <= bd.data_fim) " +
                "    AND vtr.data >= bd.data_criacao " +
                "    AND NOT EXISTS ( " +
                "        SELECT 1 FROM muralha.blitz_tipo_alerta bta " +
                "        WHERE bta.id_blitz_digital = bd.id " +
                "        AND bta.ativo = 1 " +
                "        AND bta.id_tipo_alerta_ocorrencia != ( " +
                "            SELECT a.id_tipo_alerta_ocorrencia " +
                "            FROM muralha.alerta a " +
                "            WHERE a.id = av.id_alerta " +
                "        ) " +
                "    ) " +
                ") THEN 1 ELSE 0 END";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setString(1, idAlerta);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 1;
            }
            
        } catch (Exception e) {
            logger.error("Erro ao verificar se alerta é de blitz", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return false;
    }

    public static Map<String, Object> obterInformacoesBlitzOstensiva(int idBlitz, String idVeiculoTempoReal) {
        Map<String, Object> info = new HashMap<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
        	String sql = "SELECT " +
                    "vtr.placa as placa, " +
                    "vtr.id as id_veiculo_tempo_real, " +
                    "rfp.id_registro_fato as id_registro_fato_passagem " +
                    "FROM muralha.veiculo_tempo_real vtr " +
                    "LEFT JOIN muralha.registro_fato_passagem_veic rfp ON vtr.id = rfp.id_veiculo " +
                    "WHERE vtr.id = ?";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            try {
                ps.setObject(1, java.util.UUID.fromString(idVeiculoTempoReal));
            } catch (IllegalArgumentException e) {
                ps.setObject(1, idVeiculoTempoReal);
            }
            rs = ps.executeQuery();

            if (rs.next()) {
                info.put("id_blitz", idBlitz);
                info.put("placa", rs.getString("placa"));
                info.put("id_veiculo_tempo_real", rs.getObject("id_veiculo_tempo_real") != null ? rs.getString("id_veiculo_tempo_real") : null);
                info.put("id_registro_fato_passagem", rs.getObject("id_registro_fato_passagem") != null ? rs.getLong("id_registro_fato_passagem") : null);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao obter informações da blitz ostensiva", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return info;
    }
    
    public static HistoricoCPF buscarHistoricoPorCpf(String cpf) {
        HistoricoCPF historico = new HistoricoCPF();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // Remover formatação do CPF recebido
            String cpfLimpo = cpf.replaceAll("[^0-9]", "");
            
            conn = Conexao.getConexao();
            
            // 1. Buscar antecedentes criminais
            String sqlAntecedentes = "SELECT ac.*, p.nome, p.sobrenome, p.data_nascimento, p.endereco, p.telefone, p.email " +
                                    "FROM muralha.antecedentes_criminais ac " +
                                    "INNER JOIN muralha.proprietario p ON ac.id_proprietario = p.id " +
                                    "WHERE REPLACE(REPLACE(p.cpf, '.', ''), '-', '') LIKE ? " +
                                    "ORDER BY ac.data_ocorrencia DESC";
            
            ps = conn.prepareStatement(sqlAntecedentes);
            ps.setString(1, "%" + cpfLimpo + "%");
            rs = ps.executeQuery();
            
            List<AntecedenteCriminal> antecedentes = new ArrayList<>();
            while (rs.next()) {
                AntecedenteCriminal antecedente = new AntecedenteCriminal();
                antecedente.setId(rs.getInt("id"));
                antecedente.setId_proprietario(rs.getInt("id_proprietario"));
                antecedente.setTipo_crime(rs.getString("tipo_crime"));
                antecedente.setData_ocorrencia(rs.getDate("data_ocorrencia"));
                antecedente.setLocal_ocorrencia(rs.getString("local_ocorrencia"));
                antecedente.setDescricao(rs.getString("descricao"));
                antecedente.setSentenca(rs.getString("sentenca"));
                antecedentes.add(antecedente);
            }
            historico.setAntecedentesCriminais(antecedentes);
            
            rs.close();
            ps.close();
            
            // 2. Buscar registros de fato com JOIN para pegar tipo_desc
            String sqlRegistrosFato = "SELECT rf.id, rf.id_tipo, rf.id_status, rf.tem_boletim, rf.id_usuario, " +
                                    "rf.data_criacao, rf.data_encerramento, rf.privado, rf.id_tipo_natureza, " +
                                    "rf.data_modificacao, rf.data_evento, " +
                                    "rft.tipo_desc, " +
                                    "rfn.natureza_desc " +
                                    "FROM muralha.registro_fato rf " +
                                    "INNER JOIN muralha.registro_fato_individuo rfi ON rf.id = rfi.id_registro_fato " +
                                    "LEFT JOIN muralha.registro_fato_tipo rft ON rf.id_tipo = rft.id " +
                                    "LEFT JOIN muralha.registro_fato_natureza rfn ON rf.id_tipo_natureza = rfn.id " +
                                    "WHERE REPLACE(REPLACE(rfi.cpf, '.', ''), '-', '') LIKE ? " +
                                    "ORDER BY rf.data_evento DESC";

            ps = conn.prepareStatement(sqlRegistrosFato);
            ps.setString(1, "%" + cpfLimpo + "%");
            rs = ps.executeQuery();

            List<RegistroFatoDTO> registros = new ArrayList<>();
            while (rs.next()) {
                RegistroFatoDTO registro = new RegistroFatoDTO();
                registro.setId(rs.getInt("id"));
                registro.setTipoRegistro(rs.getInt("id_tipo"));
                registro.setIdStatus(rs.getInt("id_status"));
                registro.setPrivado(rs.getInt("privado"));
                
                String tipoDesc = rs.getString("tipo_desc");
                String naturezaDesc = rs.getString("natureza_desc");
                
                StringBuilder descricao = new StringBuilder();
                if (tipoDesc != null) {
                    descricao.append(tipoDesc);
                }
                if (naturezaDesc != null) {
                    if (descricao.length() > 0) descricao.append(" - ");
                    descricao.append(naturezaDesc);
                }
                
                registro.setDetalhamentoFato(descricao.toString());
                
                java.sql.Timestamp timestamp = rs.getTimestamp("data_evento");
                if (timestamp != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    registro.setDataHoraOcorrido(sdf.format(new Date(timestamp.getTime())));
                }
                
                registros.add(registro);
            }
            historico.setRegistrosDeFato(registros);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar histórico por CPF: " + cpf, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return historico;
    }

    public static HistoricoPlaca buscarHistoricoPorPlaca(String placa) {
        HistoricoPlaca historico = new HistoricoPlaca();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            conn = Conexao.getConexao();
            
            // 1. Buscar registros de fato
            String sqlRegistrosFato = "SELECT rf.id, rf.id_tipo, rf.id_status, rf.tem_boletim, rf.id_usuario, " +
                                    "rf.data_criacao, rf.data_encerramento, rf.privado, rf.id_tipo_natureza, " +
                                    "rf.data_modificacao, rf.data_evento, " +
                                    "rft.tipo_desc, " +
                                    "rfn.natureza_desc " +
                                    "FROM muralha.registro_fato rf " +
                                    "INNER JOIN muralha.registro_fato_veiculo rfv ON rf.id = rfv.id_registro_fato " +
                                    "LEFT JOIN muralha.registro_fato_tipo rft ON rf.id_tipo = rft.id " +
                                    "LEFT JOIN muralha.registro_fato_natureza rfn ON rf.id_tipo_natureza = rfn.id " +
                                    "WHERE rfv.placa = ? " +
                                    "ORDER BY rf.data_evento DESC";
            
            ps = conn.prepareStatement(sqlRegistrosFato);
            ps.setString(1, placa);
            rs = ps.executeQuery();
            
            List<RegistroFatoDTO> registros = new ArrayList<>();
            while (rs.next()) {
                RegistroFatoDTO registro = new RegistroFatoDTO();
                registro.setId(rs.getInt("id"));
                registro.setTipoRegistro(rs.getInt("id_tipo"));
                registro.setIdStatus(rs.getInt("id_status"));
                registro.setPrivado(rs.getInt("privado"));
                
                String tipoDesc = rs.getString("tipo_desc");
                String naturezaDesc = rs.getString("natureza_desc");
                
                StringBuilder descricao = new StringBuilder();
                if (tipoDesc != null) {
                    descricao.append(tipoDesc);
                }
                if (naturezaDesc != null) {
                    if (descricao.length() > 0) descricao.append(" - ");
                    descricao.append(naturezaDesc);
                }
                
                registro.setDetalhamentoFato(descricao.toString());
                
                java.sql.Timestamp timestamp = rs.getTimestamp("data_evento");
                if (timestamp != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    registro.setDataHoraOcorrido(sdf.format(new Date(timestamp.getTime())));
                }
                
                registros.add(registro);
            }
            historico.setRegistrosDeFato(registros);
            
            rs.close();
            ps.close();
            
            // 2. Buscar alertas 
            String sqlAlertas = "SELECT " +
                            "a.id, " +
                            "a.id_tipo_alerta_ocorrencia, " +
                            "a.id_cad_veiculo_monitorado, " +
                            "a.id_status_alerta, " +
                            "a.data, " +
                            "a.observacao, " +
                            "a.id_usuario, " +
                            "a.enviado_cliente, " +
                            "a.data_enviado, " +
                            "a.id_motivo_descarte, " +
                            "a.lembrete_visualizado, " +
                            "a.id_ponto_interesse, " +
                            "a.alerta_vinculado, " +
                            "a.id_alerta_vinculado, " +
                            "a.com_semelhanca, " +
                            "a.com_semelhanca_erros, " +
                            "a.com_semelhanca_desc, " +
                            "a.assinado, " +
                            "tao.tipo as tipo_alerta_desc, " +
                            "sa.descricao as status_alerta_desc, " +
                            "vtr.placa as placa_veiculo, " +
                            "vtr.data as data_veiculo " +
                            "FROM muralha.alerta a " +
                            "INNER JOIN muralha.alerta_veiculo av ON a.id = av.id_alerta " +
                            "INNER JOIN muralha.veiculo_tempo_real vtr ON av.id_veiculo_tempo_real = vtr.id " +
                            "INNER JOIN muralha.tipo_alerta_ocorrencia tao ON a.id_tipo_alerta_ocorrencia = tao.id " +
                            "INNER JOIN muralha.status_alerta sa ON a.id_status_alerta = sa.id " +
                            "WHERE vtr.placa = ? " +
                            "ORDER BY a.data DESC";
            
            ps = conn.prepareStatement(sqlAlertas);
            ps.setString(1, placa);
            rs = ps.executeQuery();
            
            List<Alerta> alertas = new ArrayList<>();
            while (rs.next()) {
                Alerta alerta = new Alerta();
                
                String idStr = rs.getString("id");
                if (idStr != null) {
                    try {
                        alerta.setId(java.util.UUID.fromString(idStr));
                    } catch (IllegalArgumentException e) {
                        // Se não for UUID válido, deixa null
                    }
                }
                
                String idTipoAlertaStr = rs.getString("id_tipo_alerta_ocorrencia");
                if (idTipoAlertaStr != null) {
                    try {
                        alerta.setIdTipoAlerta(java.util.UUID.fromString(idTipoAlertaStr));
                    } catch (IllegalArgumentException e) {
                        // Se não for UUID válido, deixa null
                    }
                }
                
                alerta.setTipoAlerta(rs.getString("tipo_alerta_desc"));
                alerta.setDescAlerta(rs.getString("tipo_alerta_desc"));
                
                String idCadVeicStr = rs.getString("id_cad_veiculo_monitorado");
                if (idCadVeicStr != null) {
                    try {
                        alerta.setIdCadVeicMonitorado(java.util.UUID.fromString(idCadVeicStr));
                    } catch (IllegalArgumentException e) {
                        // Se não for UUID válido, deixa null
                    }
                }
                
                String idStatusAlertaStr = rs.getString("id_status_alerta");
                if (idStatusAlertaStr != null) {
                    try {
                        alerta.setIdStatusAlerta(java.util.UUID.fromString(idStatusAlertaStr));
                    } catch (IllegalArgumentException e) {
                        // Se não for UUID válido, deixa null
                    }
                }
                
                alerta.setStatusAlertaDesc(rs.getString("status_alerta_desc"));
                alerta.setDataAlerta(rs.getTimestamp("data"));
                alerta.setPlacaVeiculo(rs.getString("placa_veiculo"));
                alerta.setDataVeiculo(rs.getTimestamp("data_veiculo"));
                alerta.setEnviadoAoCliente(rs.getInt("enviado_cliente"));
                alerta.setDataEnviadoCliente(rs.getTimestamp("data_enviado"));
                
                String idMotivoDescarteStr = rs.getString("id_motivo_descarte");
                if (idMotivoDescarteStr != null) {
                    try {
                        alerta.setIdMotivoDescarte(java.util.UUID.fromString(idMotivoDescarteStr));
                    } catch (IllegalArgumentException e) {
                        // Se não for UUID válido, deixa null
                    }
                }
                
                alerta.setObservacao(rs.getString("observacao"));
                alerta.setIdUsuario(rs.getInt("id_usuario"));
                
                String idPontoInteresseStr = rs.getString("id_ponto_interesse");
                if (idPontoInteresseStr != null) {
                    try {
                        alerta.setIdPontoInteresse(java.util.UUID.fromString(idPontoInteresseStr));
                    } catch (IllegalArgumentException e) {
                        // Se não for UUID válido, deixa null
                    }
                }
                
                alerta.setAlertaVinculado(rs.getBoolean("alerta_vinculado"));
                
                String idAlertaVinculadoStr = rs.getString("id_alerta_vinculado");
                if (idAlertaVinculadoStr != null) {
                    try {
                        alerta.setIdAlertaVinculado(java.util.UUID.fromString(idAlertaVinculadoStr));
                    } catch (IllegalArgumentException e) {
                        // Se não for UUID válido, deixa null
                    }
                }
                
                alerta.setCom_semelhanca(rs.getInt("com_semelhanca"));
                alerta.setCom_semelhanca_erros(rs.getInt("com_semelhanca_erros"));
                alerta.setCom_semelhanca_desc(rs.getString("com_semelhanca_desc"));
                alerta.setAssinado(rs.getBoolean("assinado"));
                
                alertas.add(alerta);
            }
            historico.setAlertas(alertas);
            
        } catch (Exception e) {
            logger.error("Erro ao buscar histórico por placa: " + placa, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return historico;
    }

    public static List<VeiculoBlitzDTO> obterPassagensBlitz(Date dataReferencia) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        List<VeiculoBlitzDTO> lista = new ArrayList<>();

        try {
            String sql = "EXEC muralha.spu_obterVeiculosBlitzWebsocket ?";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setTimestamp(1, new Timestamp(dataReferencia.getTime()));
            rs = ps.executeQuery();

            while (rs.next()) {
                VeiculoBlitzDTO dto = new VeiculoBlitzDTO();

                String uuidStr = rs.getString("idVeiculoTempoReal");
                dto.setIdVeiculoTempoReal(uuidStr != null ? uuidStr : null);

                dto.setPlaca(rs.getString("placa"));
                dto.setData(rs.getTimestamp("data"));
                dto.setIdLocal(rs.getInt("id_local"));
                dto.setIdPista(rs.getInt("id_pista"));
                dto.setVelocidade(rs.getInt("velocidade"));
                dto.setClassificacao(rs.getString("classificacao"));
                dto.setBlitzes(rs.getString("blitzes"));
                dto.setIdAlerta(rs.getInt("id_alerta"));
                dto.setTipoAlerta(rs.getString("tipo_alerta"));
                dto.setObservacaoAlerta(rs.getString("observacao_alerta"));

                lista.add(dto);
            }

        }
        catch (Exception e) {
            logger.error("Erro ao executar obterPassagensBlitz(): " + e.getMessage(), e);
        }
        finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            }
            catch (Exception e) {
                logger.error("Erro ao fechar recursos do banco em obterPassagensBlitz(): " + e.getMessage(), e);
            }
        }

        return lista;
    }

    private static String formatarData(Date data) {
        if (data == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(data);
    }

    public static void iniciarTimer() {
        synchronized (LOCK) {
            if (timerAtivo)
                return;

            timerAtivo = true;
            lastExecutionTime = new Date(System.currentTimeMillis() - 5000);

            timerBlitz = new Thread(() -> {

                while (timerAtivo) {
                    try {

                        List<VeiculoBlitzDTO> passagens =
                            obterPassagensBlitz(lastExecutionTime);

                        lastExecutionTime = new Date();

                        if (!passagens.isEmpty()) {
                            String payload = montarJson(passagens);
                            ClienteSessoes.EnviaTextoClientes("BLITZ-DIGITAL", payload);
                        }

                        Thread.sleep(5000);

                    } catch (InterruptedException e) {
                        logger.info("Timer Blitz Digital interrompido");
                        Thread.currentThread().interrupt();
                        break;
                    }
                    catch (Exception e) {
                        logger.error("Erro no timer Blitz Digital", e);
                    }
                }
            });

            timerBlitz.start();
        }
    }

    public static void pararTimer() {
        synchronized (LOCK) {
            timerAtivo = false;

            if (timerBlitz != null) {
                timerBlitz.interrupt();
                timerBlitz = null;
            }

            lastExecutionTime = null;
        }
    }

    public static String montarJson(List<VeiculoBlitzDTO> passagens)
    {
        try
        {
            Map<String, Object> payload = new HashMap<>();
            payload.put("passagens", passagens);

            return mapper.writeValueAsString(payload);
        }
        catch (Exception e)
        {
            logger.error("Erro ao montar JSON Blitz Digital", e);
            return "{}";
        }
    }

    public static List<BlitzImagemAbordagem> listarImagensPorAbordagem(Long idAbordagem) {
        List<BlitzImagemAbordagem> imagens = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT i.* FROM muralha.blitz_abordagem_imagem i " +
                        "WHERE i.id_abordagem = ? " +
                        "ORDER BY i.data_criacao";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setLong(1, idAbordagem);
            rs = ps.executeQuery();

            while (rs.next()) {
                BlitzImagemAbordagem imagem = new BlitzImagemAbordagem();
                imagem.setId(rs.getLong("id"));
                imagem.setId_abordagem(rs.getLong("id_abordagem"));
                imagem.setNome_arquivo_original(rs.getString("nome_arquivo"));
                imagem.setCaminho_arquivo(rs.getString("caminho_arquivo"));
                imagem.setTipo_arquivo(rs.getString("tipo_arquivo"));
                imagem.setId_usuario(rs.getInt("id_usuario"));
                imagem.setData_criacao(rs.getTimestamp("data_criacao"));
                imagens.add(imagem);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar imagens da abordagem", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }

        return imagens;
    }

    public static Integer obterIdStatusAbordagemPorCodigo(String codigo) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer id = null;

        try {
            String sql = 
            "SELECT id " +
            "FROM muralha.blitz_abordagem_status " +
            "WHERE codigo = ? AND ativo = 1";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setString(1, codigo);
            rs = ps.executeQuery();

            if (rs.next()) {
                id = rs.getInt("id");
                logger.info("ID do status da abordagem para o código '" + codigo + "': " + id);
            }
        } catch (Exception e) {
            logger.error("Erro ao buscar ids da abordagem", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }

        return id;
    }

    public static String buscarCodigoAbordagemPorId(int idStatus) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String codigo = null;

        try {
            String sql =
                "SELECT codigo " +
                "FROM muralha.blitz_abordagem_status " +
                "WHERE id = ?";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idStatus);
            rs = ps.executeQuery();

            if (rs.next()) {
                codigo = rs.getString("codigo");
            }

        } catch (Exception e) {
            logger.error("Erro ao buscar código do status da abordagem", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }

        return codigo;
    }

    public static List<TipoBlitz> listarTiposBlitz() {
        List<TipoBlitz> tipos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id, codigo, descricao FROM muralha.blitz_tipo WHERE ativo = 1 ORDER BY id";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                TipoBlitz tipo = new TipoBlitz();
                tipo.setId(rs.getInt("id"));
                tipo.setCodigo(rs.getString("codigo"));
                tipo.setDescricao(rs.getString("descricao"));
                tipos.add(tipo);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar tipos de blitz", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return tipos;
    }

    public static List<BlitzAbordagemResultado> listarResultadosAbordagem() {
        List<BlitzAbordagemResultado> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql =
                "SELECT id, descricao, id_status " +
                "FROM muralha.blitz_abordagem_resultado " +
                "WHERE ativo = 1 " +
                "ORDER BY descricao";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                BlitzAbordagemResultado r = new BlitzAbordagemResultado();
                r.setId(rs.getInt("id"));
                r.setDescricao(rs.getString("descricao"));
                r.setIdStatus(rs.getInt("id_status"));
                lista.add(r);
            }

        } catch (Exception e) {
            logger.error("Erro ao listar resultados da abordagem", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }

        return lista;
    }

    public static String buscarResultadoAbordagemPorId(int idResultado) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String descricao = null;

        try {
            String sql =
                "SELECT descricao " +
                "FROM muralha.blitz_abordagem_resultado " +
                "WHERE id = ?";

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idResultado);
            rs = ps.executeQuery();

            if (rs.next()) {
                descricao = rs.getString("descricao");
            }

        } catch (Exception e) {
            logger.error("Erro ao buscar código do resultado da abordagem", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }

        return descricao;
    }

    public static Map<String, String> obterInformacoesVeiculoPorPlaca(String placa) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Map<String, String> veiculoInfo = null;
        
        try {
            String sql = "SELECT " +
                        "    cad_marca.descricao AS marca, " +
                        "    cad_modelo.descricao AS modelo, " +
                        "    cad_tipo.descricao AS tipo_veiculo, " +
                        "    cad_cor.descricao AS cor_veiculo " +
                        "FROM cad_veiculo " +
                        "LEFT JOIN cad_marca ON cad_veiculo.id_marca = cad_marca.id_marca " +
                        "LEFT JOIN cad_modelo ON cad_veiculo.id_modelo = cad_modelo.id_modelo " +
                        "LEFT JOIN cad_tipo ON cad_veiculo.id_tipo = cad_tipo.id_tipo " +
                        "LEFT JOIN cad_cor ON cad_veiculo.id_cor = cad_cor.id_cor " +
                        "WHERE cad_veiculo.placa = ?";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setString(1, placa);
            rs = ps.executeQuery();
            
            if (rs.next()) {
                veiculoInfo = new HashMap<>();
                veiculoInfo.put("marca", rs.getString("marca"));
                veiculoInfo.put("modelo", rs.getString("modelo"));
                veiculoInfo.put("tipo", rs.getString("tipo_veiculo"));
                veiculoInfo.put("cor", rs.getString("cor_veiculo"));
            } else {
                logger.info("Nenhum veículo encontrado para a placa: " + placa);
            }
            
        } catch (Exception e) {
            logger.error("Erro ao buscar informações do veículo para placa: " + placa, e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return veiculoInfo;
    }

    public static JSONObject realizarGeocoding(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            return null;
        }

        synchronized (GEOLOCK) {
            long agora = System.currentTimeMillis();
            long diff = agora - ultimoGeocoding;

            if (diff < 1000) {
                try {
                    Thread.sleep(1000 - diff);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            ultimoGeocoding = System.currentTimeMillis();
        }

        try {
            String apiKey = Inicializacao.BlitzDigitalApiKeyGeocoding;
            String urlStr = "https://geocode.maps.co/search?q="
                    + URLEncoder.encode(endereco, "UTF-8")
                    + "&api_key=" + apiKey;

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int status = conn.getResponseCode();

            InputStream stream = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(stream, "UTF-8"));

            StringBuilder responseStr = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                responseStr.append(inputLine);
            }

            in.close();

            JSONArray arr = new JSONArray(responseStr.toString());

            logger.error("Resposta geocoding: " + responseStr.toString());

            if (arr.length() > 0) {
                JSONObject obj = arr.getJSONObject(0);

                JSONObject retorno = new JSONObject();
                retorno.put("lat", obj.getString("lat"));
                retorno.put("lon", obj.getString("lon"));

                return retorno;
            }

            return null;

        } catch (Exception e) {
            return null;
        }
    }

    public static List<Integer> listarIdsLocaisBlitz(int idBlitz) {
        List<Integer> idsLocais = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            String sql = "SELECT id_local FROM muralha.blitz_local WHERE id_blitz_digital = ?";
            
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idBlitz);
            rs = ps.executeQuery();

            while (rs.next()) {
                idsLocais.add(rs.getInt("id_local"));
            }

        } catch (Exception e) {
            logger.error("Erro ao listar IDs dos locais da blitz", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }
        return idsLocais;
    }

    public static List<Map<String, Object>> obterPassagensReaisBlitz(List<Integer> idsLocais, Date dataReferencia) {
        List<Map<String, Object>> passagens = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        boolean filtrarPorPlacaTeste = false;
        boolean somenteComObservacao = false;

        try {
            if (idsLocais == null || idsLocais.isEmpty()) {
                return passagens;
            }

            // Monta a string com os placeholders para os IDs dos locais
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < idsLocais.size(); i++) {
                if (i > 0) placeholders.append(",");
                placeholders.append("?");
            }

            StringBuilder sql = new StringBuilder();

            sql.append("SELECT TOP 10 ")
            .append("vtr.id as idVeiculoTempoReal, ")
            .append("vtr.placa, ")
            .append("vtr.data, ")
            .append("vtr.id_local as idLocal, ")
            .append("vtr.id_pista as idPista, ")
            .append("vtr.velocidade, ")
            .append("vtr.classificacao, ")
            .append("a.id as id_alerta, ")
            .append("tao.tipo as tipo_alerta, ")
            .append("a.observacao as observacao_alerta ")
            .append("FROM muralha.veiculo_tempo_real vtr ")
            .append("LEFT JOIN muralha.alerta_veiculo av ON vtr.id = av.id_veiculo_tempo_real ")
            .append("LEFT JOIN muralha.alerta a ON av.id_alerta = a.id ")
            .append("LEFT JOIN muralha.tipo_alerta_ocorrencia tao ON a.id_tipo_alerta_ocorrencia = tao.id ")
            .append("WHERE vtr.id_local IN (").append(placeholders).append(") ");

            // FLAG 1: filtro por placa
            if (filtrarPorPlacaTeste) {
                sql.append("AND vtr.placa = 'TAI2J79' ");
            }

            // FLAG 2: observação OU data
            if (somenteComObservacao) {
                sql.append("AND a.observacao IS NOT NULL AND a.observacao <> '' ");
            } else {
                sql.append("AND vtr.data < ? ");
            }

            sql.append("ORDER BY vtr.data DESC");

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sql.toString());

            int index = 1;

            for (Integer idLocal : idsLocais) {
                ps.setInt(index++, idLocal);
            }

            if (!somenteComObservacao) {
                ps.setTimestamp(index, new Timestamp(dataReferencia.getTime()));
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> passagem = new HashMap<>();

                passagem.put("idVeiculoTempoReal", rs.getString("idVeiculoTempoReal"));
                passagem.put("placa", rs.getString("placa"));
                passagem.put("data", rs.getTimestamp("data"));
                passagem.put("idLocal", rs.getInt("idLocal"));
                passagem.put("idPista", rs.getInt("idPista"));
                passagem.put("velocidade", rs.getInt("velocidade"));
                passagem.put("classificacao", rs.getString("classificacao"));
                passagem.put("id_alerta", rs.getString("id_alerta"));
                passagem.put("tipo_alerta", rs.getString("tipo_alerta"));
                passagem.put("observacao_alerta", rs.getString("observacao_alerta"));

                passagens.add(passagem);
            }

        } catch (Exception e) {
            logger.error("Erro ao obter passagens reais para blitz", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos", e);
            }
        }

        return passagens;
    }
}