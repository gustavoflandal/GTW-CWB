package muralha.digital.guarnicao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.SQLException;


import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.google.gson.JsonObject;

import muralha.digital.acessos.Usuario;

public class Guarnicao {

	private static final Logger logger = Logger.getLogger(Guarnicao.class);

	private int             id;
	private String   		nome;
	private int 			id_usuario_responsavel;
	private Date 			data_criacao;
	private String			data_criacao_formatado;
	private String			data_alteracao_formatado;

	public String getResponsavel() {
		return responsavel;
	}

	private int 			id_usuario_criacao;
	private Date 			data_ult_alt;
	private int 			id_usuario_alt;
	private int 			disponivel;
	private String			responsavel;
	private String			nomeResponsavel;
	private List<Usuario>   integrantes = new ArrayList<>();
	private List<Integer> 	idsIntegrantes;
	private int 			ativo;
	private String			meiosDeslocamento;


	// Getters e setters

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public List<Usuario> getIntegrantes() {
	    return integrantes;
	}

	public void setIntegrantes(List<Usuario> integrantes) {
	    this.integrantes = integrantes;
	}
	
	public void setIdsIntegrantes(List<Integer> idsIntegrantes) {
	    this.idsIntegrantes = idsIntegrantes;
	}

	public List<Integer> getIdsIntegrantes() {
	    return idsIntegrantes;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public int getId_usuario_responsavel() {
		return id_usuario_responsavel;
	}
	public void setId_usuario_responsavel(int id_usuario_responsavel) {
		this.id_usuario_responsavel = id_usuario_responsavel;
	}
	
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public Date getData_criacao() {
		return data_criacao;
	}
	public void setData_criacao(Date data_criacao) {
		this.data_criacao = data_criacao;
		this.data_criacao_formatado = new SimpleDateFormat("dd/MM/yyyy").format(data_criacao);
	}
	
	public String getData_criacao_formatado() {
		return data_criacao_formatado;
	}
	public void setData_criacao_formatado(String data_criacao_formatado) {
		this.data_criacao_formatado = data_criacao_formatado;
	}
	

	public int getId_usuario_criacao() {
		return id_usuario_criacao;
	}
	
	public void setId_usuario_criacao(int id_usuario_criacao) {
		this.id_usuario_criacao = id_usuario_criacao;
	}

	public Date getData_ult_alt() {
		return data_ult_alt;
	}
	
	public void setData_ult_alt(Date data_ult_alt) {
	    this.data_ult_alt = data_ult_alt;

	    if (data_ult_alt != null) {
	        this.data_alteracao_formatado = new SimpleDateFormat("dd/MM/yyyy").format(data_ult_alt);
	    } else {
	        this.data_alteracao_formatado = "";
	    }
	}


    public String getData_alteracao_formatado() {
		return data_alteracao_formatado;
	}
    
	public void setData_alteracao_formatado(String data_alteracao_formatado) {
		this.data_alteracao_formatado = data_alteracao_formatado;
	}

	public int getId_usuario_alt() {
		return id_usuario_alt;
	}
	public void setId_usuario_alt(int id_usuario_alt) {
		this.id_usuario_alt = id_usuario_alt;
	}

	public int getDisponivel() {
		return disponivel;
	}
	public void setDisponivel(int disponivel) {
		this.disponivel = disponivel;
	}
	
	public String getMeiosDeslocamento() {
		return meiosDeslocamento;
	}
	
	public void setMeiosDeslocamento(String meiosDeslocamento) {
		this.meiosDeslocamento = meiosDeslocamento;
	}
	

	public int getAtivo() {
		return ativo;
	}
	public void setAtivo(int ativo) {
		this.ativo = ativo;
	}
	public static List<Usuario> listarTodosUsuarios() {
		List<Usuario> lista = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT id_usuario, usuario, telefone FROM sis_usuario";

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();

			while (rs.next()) {
				Usuario usuario = new Usuario(
					rs.getInt("id_usuario"),
					rs.getString("usuario"),
					null,   
					null,   
					true,   
					""      
				);
				usuario.setTelefone(rs.getString("telefone"));
				lista.add(usuario);
			}

			logger.info("Usuários carregados: total " + lista.size());
		} catch (Exception e) {
			logger.error("Erro ao listar todos os usuários", e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos (listarTodosUsuarios): " + e.getMessage(), e);
			}
		}

		return lista;
	}

	public static Usuario buscarUsuarioPorId(int idUsuario) {
		Usuario usuario = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			String sql = "SELECT id_usuario, usuario, telefone FROM sis_usuario WHERE id_usuario = ?";

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sql);
			ps.setInt(1, idUsuario);
			rs = ps.executeQuery();

			if (rs.next()) {
				usuario = new Usuario(
					rs.getInt("id_usuario"),
					rs.getString("usuario"),
					null,   
					null,   
					true,   
					""      
				);
				usuario.setTelefone(rs.getString("telefone"));
			}

			logger.info("Usuário encontrado: ID " + idUsuario);
		} catch (Exception e) {
			logger.error("Erro ao buscar usuário simples por ID: " + idUsuario, e);
		} finally {
			try {
				if (rs != null) rs.close();
				if (ps != null) ps.close();
				if (conn != null) conn.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos (buscarUsuarioPorId): " + e.getMessage(), e);
			}
		}

		return usuario;
	}


	// =====================================
	// Buscar guarnição por ID (sem throws)
	// =====================================
	public static Guarnicao buscarPorId(int idGuarnicao) {
		Guarnicao guarnicao = null;
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			JsonObject meiosDeslocametoPorGuarnicao = Guarnicao.retornaMeiosDeslocamentoPorGuarnicao(idGuarnicao);
			StringBuilder sb = new StringBuilder();
			List<Usuario> integrantes = buscarIntegrantes(idGuarnicao);
			sb.append(" SELECT id, nome, id_usuario_responsavel, data_criacao, id_usuario_criacao, data_ult_alt, id_usuario_alt ");
			sb.append(" FROM muralha.guarnicao WHERE id = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sb.toString());
			ps.setInt(1, idGuarnicao);
			rs = ps.executeQuery();

			if (rs.next()) {
				guarnicao = new Guarnicao();
				guarnicao.setId(rs.getInt("id"));
				guarnicao.setNome(rs.getString("nome"));
				guarnicao.setId_usuario_responsavel(rs.getInt("id_usuario_responsavel"));
				guarnicao.setResponsavel(rs.getString("nome"));
				guarnicao.setData_criacao(rs.getDate("data_criacao"));
				guarnicao.setId_usuario_criacao(rs.getInt("id_usuario_criacao"));
				guarnicao.setData_ult_alt(rs.getDate("data_ult_alt"));
				guarnicao.setId_usuario_alt(rs.getInt("id_usuario_alt"));
            	guarnicao.setIntegrantes(integrantes);
				guarnicao.setMeiosDeslocamento(meiosDeslocametoPorGuarnicao.get("descricao").getAsString());


				Usuario responsavel = buscarUsuarioPorId(guarnicao.getId_usuario_responsavel());
				if (responsavel != null) {
					guarnicao.setResponsavel(responsavel.getUsuario());
					guarnicao.setNomeResponsavel(responsavel.getNome());
				} else {
					logger.warn("Responsável não encontrado para a guarnição ID: " + idGuarnicao);
				}

				logger.info("Guarnição carregada: ID " + guarnicao.getId() + " Nome: " + guarnicao.getNome());
			} else {
				logger.info("Nenhuma guarnição encontrada com ID: " + idGuarnicao);
			}
		} catch (Exception e) {
			logger.error("Erro ao buscar guarnição por ID: " + idGuarnicao, e);
		} finally {
			try {
				if (conn != null) conn.close();
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos (buscarPorId): " + e.getMessage(), e);
			}
		}
		return guarnicao;
	}

	public static GuarnicaoDiario buscarGuarnicaoDiarioPorId(int idGuarnicaoDiario) {
    GuarnicaoDiario diario = null;
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT id, id_guarnicao, data, quilometragem, hora_ini, hora_fim, ");
        sb.append(" setores_patrulhados, meio_transporte, data_cadastro, id_usuario ");
        sb.append(" FROM muralha.guarnicao_diario WHERE id = ? ");

        conn = Conexao.getConexao();
        ps = conn.prepareStatement(sb.toString());
        ps.setInt(1, idGuarnicaoDiario);
        rs = ps.executeQuery();

        if (rs.next()) {
            diario = new GuarnicaoDiario();
            diario.setId(rs.getInt("id"));
            diario.setIdGuarnicao(rs.getInt("id_guarnicao"));
            diario.setData(rs.getDate("data"));
            diario.setQuilometragem(rs.getDouble("quilometragem"));
            diario.setHoraIni(rs.getString("hora_ini"));
            diario.setHoraFim(rs.getString("hora_fim"));
            diario.setSetoresPatrulhados(rs.getString("setores_patrulhados"));
            diario.setMeioTransporte(rs.getString("meio_transporte"));
            diario.setDataCadastro(rs.getTimestamp("data_cadastro"));
            diario.setIdUsuario(rs.getInt("id_usuario"));

            // (Opcional) buscar objeto Guarnicao associado
            Guarnicao guarnicao = buscarPorId(diario.getIdGuarnicao());
            diario.setGuarnicao(guarnicao);

            // (Opcional) buscar nome do usuário, se necessário
            Usuario usuario = buscarUsuarioPorId(diario.getIdUsuario());
            diario.setUsuario(usuario);

            logger.info("Guarnição Diário carregada: ID " + diario.getId());
        } else {
            logger.info("Nenhum registro encontrado em guarnicao_diario com ID: " + idGuarnicaoDiario);
        }

    } catch (Exception e) {
        logger.error("Erro ao buscar guarnicao_diario por ID: " + idGuarnicaoDiario, e);
    } finally {
        try {
            if (conn != null) conn.close();
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        } catch (Exception e) {
            logger.error("Erro ao fechar recursos (buscarGuarnicaoDiarioPorId): " + e.getMessage(), e);
        }
    }

    return diario;
}

public static List<GuarnicaoDiario> listarGuarnicoesDiariasPorGuarnicao(int idGuarnicao) {
    List<GuarnicaoDiario> lista = new ArrayList<>();
    Connection conn = null;
    PreparedStatement ps = null;
    ResultSet rs = null;

    try {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT id, id_guarnicao, data, quilometragem, hora_ini, hora_fim, ");
        sb.append(" setores_patrulhados, meio_transporte, data_cadastro, id_usuario ");
        sb.append(" FROM muralha.guarnicao_diario WHERE id_guarnicao = ? ORDER BY data DESC ");

        conn = Conexao.getConexao();
        ps = conn.prepareStatement(sb.toString());
        ps.setInt(1, idGuarnicao);
        rs = ps.executeQuery();

        while (rs.next()) {
            GuarnicaoDiario diario = new GuarnicaoDiario();
            diario.setId(rs.getInt("id"));
            diario.setIdGuarnicao(rs.getInt("id_guarnicao"));
            diario.setData(rs.getDate("data"));
            diario.setQuilometragem(rs.getDouble("quilometragem"));
            diario.setHoraIni(rs.getString("hora_ini"));
            diario.setHoraFim(rs.getString("hora_fim"));
            diario.setSetoresPatrulhados(rs.getString("setores_patrulhados"));
            diario.setMeioTransporte(rs.getString("meio_transporte"));
            diario.setDataCadastro(rs.getTimestamp("data_cadastro"));
            diario.setIdUsuario(rs.getInt("id_usuario"));

            // Carrega os objetos relacionados (opcional)
            diario.setGuarnicao(buscarPorId(rs.getInt("id_guarnicao")));
            diario.setUsuario(buscarUsuarioPorId(rs.getInt("id_usuario")));

            lista.add(diario);
        }

    } catch (Exception e) {
        logger.error("Erro ao listar guarnições diárias por guarnição: " + idGuarnicao, e);
    } finally {
        try {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            logger.error("Erro ao fechar recursos em listarGuarnicoesDiariasPorGuarnicao", e);
        }
    }

    return lista;
}

	// ==================================================
	// Buscar integrantes da guarnição (usuários vinculados)
	// ==================================================
	public static List<Usuario> buscarIntegrantes(int idGuarnicao) {
		List<Usuario> lista = new ArrayList<>();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT u.id_usuario, u.nome ");
			sb.append(" FROM muralha.guarnicao_integrante gi ");
			sb.append(" INNER JOIN sis_usuario u ON gi.id_usuario = u.id_usuario ");
			sb.append(" WHERE gi.id_guarnicao = ? ");

			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sb.toString());
			ps.setInt(1, idGuarnicao);
			rs = ps.executeQuery();

			while (rs.next()) {
				Usuario u = new Usuario(
					rs.getInt("id_usuario"),
					null, 
					rs.getString("nome"),
					null, 
					true, 
					"" 
				);
				lista.add(u);
			}
			logger.info("Integrantes carregados para guarnição ID: " + idGuarnicao + " Total: " + lista.size());
		} catch (Exception e) {
			logger.error("Erro ao buscar integrantes da guarnição: " + idGuarnicao, e);
		} finally {
			try {
				if (conn != null) conn.close();
				if (ps != null) ps.close();
				if (rs != null) rs.close();
			} catch (Exception e) {
				logger.error("Erro ao fechar recursos (buscarIntegrantes): " + e.getMessage(), e);
			}
		}
		return lista;
	}

	public static boolean cadastrarGuarnicaoDiario(GuarnicaoDiario diario) {
    Connection conn = null;
    PreparedStatement ps = null;

    try {
        String sql = "INSERT INTO muralha.guarnicao_diario (" +
                     "id_guarnicao, data, quilometragem, hora_ini, hora_fim, " +
                     "setores_patrulhados, meio_transporte, data_cadastro, id_usuario) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)";

        conn = Conexao.getConexao();
        ps = conn.prepareStatement(sql);

        ps.setInt(1, diario.getIdGuarnicao());
        ps.setDate(2, new java.sql.Date(diario.getData().getTime()));
        ps.setDouble(3, diario.getQuilometragem());
        ps.setString(4, diario.getHoraIni());
        ps.setString(5, diario.getHoraFim());
        ps.setString(6, diario.getSetoresPatrulhados());
        ps.setString(7, diario.getMeioTransporte());
        ps.setInt(8, diario.getIdUsuario());

        int linhasAfetadas = ps.executeUpdate();
        return linhasAfetadas > 0;

    } catch (Exception e) {
        logger.error("Erro ao cadastrar guarnição_diario", e);
        return false;
    } finally {
        try {
            if (ps != null) ps.close();
            if (conn != null) conn.close();
        } catch (Exception e) {
            logger.error("Erro ao fechar conexão ao cadastrar guarnição_diario", e);
        }
    }
}

	public static int salvar(Guarnicao guarnicao) {
	    int idGerado = -1;
	    String sql = "INSERT INTO muralha.guarnicao (nome, id_usuario_responsavel, id_usuario_criacao, disponivel, data_criacao) " +
	                 "VALUES (?, ?, ?, ?, GETDATE())";

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setString(1, guarnicao.getNome());
	        stmt.setInt(2, guarnicao.getId_usuario_responsavel());
	        stmt.setInt(3, guarnicao.getId_usuario_criacao());
	        stmt.setInt(4, guarnicao.getDisponivel());

	        int rows = stmt.executeUpdate();

	        if (rows > 0) {
	            ResultSet generatedKeys = stmt.getGeneratedKeys();
	            if (generatedKeys.next()) {
	                idGerado = generatedKeys.getInt(1);
	            }
	        }

	    } catch (Exception e) {
	        Logger.getLogger(Guarnicao.class).error("Erro ao salvar guarnição: " + e.getMessage(), e);
	    }

	    return idGerado;
	}

	public static boolean atualizar(Guarnicao guarnicao) {
	    String sql = "UPDATE muralha.guarnicao " +
	                 "SET nome = ?, id_usuario_responsavel = ?, data_ult_alt = ?, id_usuario_alt = ? " +
	                 "WHERE id = ?";

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, guarnicao.getNome());
	        stmt.setInt(2, guarnicao.getId_usuario_responsavel());
	        stmt.setTimestamp(3, new Timestamp(System.currentTimeMillis())); 
	        stmt.setInt(4, guarnicao.getId_usuario_alt()); 
	        stmt.setInt(5, guarnicao.getId()); 

	        int rows = stmt.executeUpdate();

	        return rows > 0;

	    } catch (Exception e) {
	        Logger.getLogger(Guarnicao.class).error("Erro ao atualizar guarnição: " + e.getMessage(), e);
	        return false;
	    }
	}


public static boolean salvarIntegrantes(int idGuarnicao, List<Integer> integrantes) {
    if (integrantes == null || integrantes.isEmpty()) return true;

    String sql = "INSERT INTO muralha.guarnicao_integrante (id_guarnicao, id_usuario) VALUES (?, ?)";

    try (Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        for (int idUsuario : integrantes) {
            stmt.setInt(1, idGuarnicao);
            stmt.setInt(2, idUsuario);
            stmt.addBatch();
        }

        stmt.executeBatch();
        return true;

    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro ao salvar integrantes da guarnição: " + e.getMessage(), e);
        return false;
    }
}

public static boolean atualizarIntegrantes(int idGuarnicao, List<Integer> novosIntegrantes) {
    String deleteSQL = "DELETE FROM muralha.guarnicao_integrante WHERE id_guarnicao = ?";
    String insertSQL = "INSERT INTO muralha.guarnicao_integrante (id_guarnicao, id_usuario) VALUES (?, ?)";

    try (Connection conn = Conexao.getConexao()) {
        conn.setAutoCommit(false); 

        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSQL);
             PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

            deleteStmt.setInt(1, idGuarnicao);
            deleteStmt.executeUpdate();

            if (novosIntegrantes != null && !novosIntegrantes.isEmpty()) {
                for (int idUsuario : novosIntegrantes) {
                    insertStmt.setInt(1, idGuarnicao);
                    insertStmt.setInt(2, idUsuario);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            conn.commit(); 
            return true;

        } catch (Exception e) {
            conn.rollback(); 
            Logger.getLogger(Guarnicao.class).error("Erro ao atualizar integrantes da guarnição: " + e.getMessage(), e);
            return false;
        }

    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro na conexão ao atualizar integrantes da guarnição: " + e.getMessage(), e);
        return false;
    }
}


public static boolean atualizarTelefone(int idUsuario, String novoTelefone) {
    String sql = "UPDATE sis_usuario SET telefone = ? WHERE id_usuario = ?";
    boolean atualizado = false;

    try (Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, novoTelefone);
        stmt.setInt(2, idUsuario);

        int rows = stmt.executeUpdate();
        atualizado = (rows > 0);

    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro ao atualizar telefone do usuário (ID: " + idUsuario + "): " + e.getMessage(), e);
    }

    return atualizado;
}

public static boolean deletarGuarnicao(int idGuarnicao) {
    boolean deletado = false;

    try {
        List<JsonObject> integrantes = listarUsuariosPorGuarnicao(idGuarnicao);

        for (JsonObject integrante : integrantes) {
            int idIntegrante = integrante.get("id").getAsInt();
            boolean removido = deletarIntegrantePorId(idIntegrante);

            if (!removido) {
                Logger.getLogger(Guarnicao.class).warn("Não foi possível remover integrante ID: " + idIntegrante);
            }
        }

        String sql = "DELETE FROM muralha.guarnicao WHERE id = ?";
        try (Connection conn = Conexao.getConexao();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idGuarnicao);
            int rows = stmt.executeUpdate();
            deletado = (rows > 0);
        }

    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro ao deletar guarnição e seus integrantes (ID: " + idGuarnicao + "): " + e.getMessage(), e);
    }

    return deletado;
}


public static List<JsonObject> listarUsuariosPorGuarnicao(int idGuarnicao) {
    List<JsonObject> lista = new ArrayList<>();
    String sql = "SELECT * FROM muralha.guarnicao_integrante WHERE id_guarnicao = ?";

    try (Connection conn = Conexao.getConexao();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idGuarnicao);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("id", rs.getInt("id"));
                obj.addProperty("id_guarnicao", rs.getInt("id_guarnicao"));
                obj.addProperty("id_usuario", rs.getInt("id_usuario"));
                lista.add(obj);
            }
        }

    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro ao listar integrantes da guarnição (ID: " + idGuarnicao + "): " + e.getMessage(), e);
    }

    return lista;
}

public static boolean deletarIntegrantePorId(int idIntegrante) {
    String sql = "DELETE FROM muralha.guarnicao_integrante WHERE id = ?";
    boolean excluido = false;

    try (Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, idIntegrante);
        int rows = stmt.executeUpdate();
        excluido = (rows > 0);

    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro ao deletar integrante (ID: " + idIntegrante + "): " + e.getMessage(), e);
    }

    return excluido;
}

public static boolean ativarDesativarGuarnicao(int idGuarnicao, int acao) {
	
    String sql = "update muralha.guarnicao set ativo = ? where id = ?";
    boolean sucesso = false;

    try (Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, acao);
        stmt.setInt(2, idGuarnicao);
        int rows = stmt.executeUpdate();
        sucesso = (rows > 0);

    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro ao deletar integrante (ID: " + idGuarnicao + "): " + e.getMessage(), e);
    }

    return sucesso;
}

public static boolean atualizarGuarnicao(String nome, int idUsuarioResponsavel, int idGuarnicao) {
    String sql = "update muralha.guarnicao set nome = ?, id_usuario_responsavel = ? where id = ?";
    boolean atualizado = false;

    try (Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, nome);
        stmt.setInt(2, idUsuarioResponsavel);
        stmt.setInt(2, idGuarnicao);

        int rows = stmt.executeUpdate();
        atualizado = (rows > 0);

    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro ao atualizar guarnição (ID: " + idGuarnicao + "): " + e.getMessage(), e);
    }

    return atualizado;
}

public static JsonObject retornaMeiosDeslocamentoPorGuarnicao(int idGuarnicao) {
    JsonObject obj = null;
    String sql = "select * from muralha.guarnicao_meio_deslocamento where id_guarnicao = ?";

    try (Connection conn = Conexao.getConexao();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, idGuarnicao);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                obj = new JsonObject();
                obj.addProperty("id", rs.getInt("id"));
                obj.addProperty("id_guarnicao", rs.getInt("id_guarnicao"));
                obj.addProperty("descricao", rs.getString("descricao")); 
            }
        }

    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro ao listar meio de deslocamento da guarnição (ID: " + idGuarnicao + "): " + e.getMessage(), e);
    }

    return obj;
}

public static int salvarMeiosDeslocamento(int idGuarnicao, List<String> meiosDeslocamento) {
	String valorMeiosDeslocamento = String.join(",", meiosDeslocamento);
    int idGerado = -1;
    String sql = "insert into muralha.guarnicao_meio_deslocamento (id, id_guarnicao, descricao) values (?, ?, ?)";

    try (Connection conn = Conexao.getConexao();
         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    	idGerado = obterProximoId();

    	stmt.setInt(1, idGerado);
        stmt.setInt(2, idGuarnicao);
        stmt.setString(3, valorMeiosDeslocamento);

        int rows = stmt.executeUpdate();

        if (rows > 0) {
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                idGerado = generatedKeys.getInt(1);
            }
        }

    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro ao salvar guarnição: " + e.getMessage(), e);
    }

    return idGerado;
}

public static int atualizarMeiosDeslocamento(int idGuarnicao, List<String> meiosDeslocamento) {
    String valorMeiosDeslocamento = String.join(",", meiosDeslocamento);
    int linhasAfetadas = -1;

    String updateSQL = "UPDATE muralha.guarnicao_meio_deslocamento SET descricao = ? WHERE id_guarnicao = ?";

    try (Connection conn = Conexao.getConexao()) {
        conn.setAutoCommit(false);

        try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {

            updateStmt.setString(1, valorMeiosDeslocamento);
            updateStmt.setInt(2, idGuarnicao);

            linhasAfetadas = updateStmt.executeUpdate();

            if (linhasAfetadas == 0) {
                String insertSQL = "INSERT INTO muralha.guarnicao_meio_deslocamento (id, id_guarnicao, descricao) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                    int novoId = obterProximoId();
                    insertStmt.setInt(1, novoId);
                    insertStmt.setInt(2, idGuarnicao);
                    insertStmt.setString(3, valorMeiosDeslocamento);
                    insertStmt.executeUpdate();
                    linhasAfetadas = 1;
                }
            }

            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            Logger.getLogger(Guarnicao.class).error("Erro ao atualizar meios de deslocamento: " + e.getMessage(), e);
        }
    } catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro na conexão ao atualizar meios de deslocamento: " + e.getMessage(), e);
    }

    return linhasAfetadas;
}



private static int obterProximoId() {
    int proximoId = 1;
    String sql = "SELECT COALESCE(MAX(id), 0) + 1 AS proximo_id FROM muralha.guarnicao_meio_deslocamento";

    try (Connection conn = Conexao.getConexao();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            proximoId = rs.getInt("proximo_id");
        }
    }
    catch (Exception e) {
        Logger.getLogger(Guarnicao.class).error("Erro ao salvar guarnição: " + e.getMessage(), e);
    }

    return proximoId;
}


}
