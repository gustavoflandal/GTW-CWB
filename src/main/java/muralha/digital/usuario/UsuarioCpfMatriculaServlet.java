package muralha.digital.usuario;

import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/MuralhaDigital/UsuarioCpfMatricula")
public class UsuarioCpfMatriculaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();
            result.add("usuarios", listarUsuarios(conn));
            result.addProperty("ok", true);
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!new Acesso(req, resp, true).verificaAcesso(false)) return;

        resp.setContentType("application/json; charset=UTF-8");
        JsonObject result = new JsonObject();
        Connection conn = null;
        try {
            conn = Conexao.getConexao();

            int idUsuario;
            try {
                idUsuario = Integer.parseInt(req.getParameter("idUsuario"));
            } catch (NumberFormatException e) {
                result.addProperty("ok", false);
                result.addProperty("erro", "ID do usuário inválido");
                resp.getWriter().print(new Gson().toJson(result));
                return;
            }

            String cpfRaw = req.getParameter("cpf");
            String matricula = req.getParameter("matricula");

            String cpf = null;
            if (cpfRaw != null && !cpfRaw.trim().isEmpty()) {
                cpf = UsuarioCpfUtil.normalizar(cpfRaw.trim());
                if (cpf == null) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "CPF inválido");
                    resp.getWriter().print(new Gson().toJson(result));
                    return;
                }
            }

            if (matricula != null) {
                matricula = matricula.trim();
                if (matricula.isEmpty()) {
                    matricula = null;
                } else if (matricula.length() > 20) {
                    result.addProperty("ok", false);
                    result.addProperty("erro", "Matrícula deve ter no máximo 20 caracteres");
                    resp.getWriter().print(new Gson().toJson(result));
                    return;
                }
            }

            salvar(conn, idUsuario, cpf, matricula);
            result.addProperty("ok", true);

        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().contains("UQ_suc_cpf")) {
                result.addProperty("ok", false);
                result.addProperty("erro", "CPF já cadastrado para outro usuário");
            } else if (e.getMessage() != null && e.getMessage().contains("UQ_suc_matricula")) {
                result.addProperty("ok", false);
                result.addProperty("erro", "Matrícula já cadastrada para outro usuário");
            } else {
                result.addProperty("ok", false);
                result.addProperty("erro", e.getMessage());
            }
        } catch (Exception e) {
            result.addProperty("ok", false);
            result.addProperty("erro", e.getMessage());
        } finally {
            if (conn != null) try { conn.close(); } catch (Exception ignored) {}
        }
        resp.getWriter().print(new Gson().toJson(result));
    }

    private JsonArray listarUsuarios(Connection conn) throws SQLException {
        JsonArray arr = new JsonArray();
        String sql =
            "SELECT su.id_usuario, su.usuario, su.nome, su.ativo, " +
            "  COALESCE(suc.cpf, '') AS cpf, " +
            "  COALESCE(suc.matricula, '') AS matricula " +
            "FROM dbo.sis_usuario su WITH (NOLOCK) " +
            "LEFT JOIN dbo.sis_usuario_complemento suc " +
            "  ON suc.id_usuario = su.id_usuario " +
            "WHERE su.ativo = 1 " +
            "ORDER BY su.nome";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                JsonObject row = new JsonObject();
                row.addProperty("idUsuario", rs.getInt("id_usuario"));
                row.addProperty("usuario", rs.getString("usuario").trim());
                row.addProperty("nome", rs.getString("nome").trim());
                row.addProperty("cpf", rs.getString("cpf").trim());
                row.addProperty("matricula", rs.getString("matricula").trim());
                arr.add(row);
            }
        }
        return arr;
    }

    private void salvar(Connection conn, int idUsuario, String cpf, String matricula)
            throws SQLException {
        String sql =
            "MERGE dbo.sis_usuario_complemento AS alvo " +
            "USING (SELECT ? AS id_usuario) AS src " +
            "ON alvo.id_usuario = src.id_usuario " +
            "WHEN MATCHED THEN UPDATE SET cpf = ?, matricula = ? " +
            "WHEN NOT MATCHED THEN INSERT (id_usuario, cpf, matricula) " +
            "  VALUES (src.id_usuario, ?, ?);";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, cpf);
            ps.setString(3, matricula);
            ps.setString(4, cpf);
            ps.setString(5, matricula);
            ps.executeUpdate();
        }
    }
}
