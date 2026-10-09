package muralha.digital.imagem;

import com.consilux.lib.Conexao;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.*;
import java.util.List;
import java.util.Map;

public final class ObliteracaoService {

    private ObliteracaoService() {}

    /**
     * Aplica obliteração e persiste cópia obliterada + registro de auditoria.
     *
     * @param idImagemOriginal UUID (String) da linha em veiculo_tempo_real_imagem
     * @param coordenadasJson  JSON array: [{"x":int,"y":int,"w":int,"h":int},...]
     * @param idUsuario        quem aplicou
     * @return UUID da imagem obliterada criada
     */
    public static String aplicar(String idImagemOriginal, String coordenadasJson, int idUsuario)
            throws Exception {

        byte[] bytesOriginais = buscarBytesImagem(idImagemOriginal);
        if (bytesOriginais == null)
            throw new IllegalArgumentException("Imagem não encontrada: " + idImagemOriginal);

        List<Map<String, Object>> coords = new Gson().fromJson(coordenadasJson,
            new TypeToken<List<Map<String, Object>>>(){}.getType());
        byte[] bytesObliterados = aplicarRetangulos(bytesOriginais, coords);

        Connection conn = Conexao.getConexao();
        conn.setAutoCommit(false);
        try {
            String idVeiculo = buscarIdVeiculo(conn, idImagemOriginal);

            // Inserir cópia obliterada (id gerado pelo DEFAULT newid())
            String idObliterada;
            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.veiculo_tempo_real_imagem " +
                    "(id_veiculo_tempo_real, imagem, obliterada, id_original) " +
                    "OUTPUT INSERTED.id " +
                    "VALUES (?,?,1,?)")) {
                ps.setString(1, idVeiculo);
                ps.setBytes(2, bytesObliterados);
                ps.setString(3, idImagemOriginal);
                try (ResultSet rs = ps.executeQuery()) {
                    rs.next();
                    idObliterada = rs.getString(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO muralha.infracao_imagem_obliteracao " +
                    "(id_imagem_original, id_imagem_obliterada, tipo, coordenadas_json, id_usuario_aplicou) " +
                    "VALUES (?,?,'M',?,?)")) {
                ps.setString(1, idImagemOriginal);
                ps.setString(2, idObliterada);
                ps.setString(3, coordenadasJson);
                ps.setInt(4, idUsuario);
                ps.executeUpdate();
            }

            conn.commit();
            return idObliterada;
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    public static void reverter(String idImagemOriginal, int idUsuario, String justificativa)
            throws Exception {
        Connection conn = Conexao.getConexao();
        conn.setAutoCommit(false);
        try {
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE muralha.infracao_imagem_obliteracao " +
                    "SET revertida=1, dt_reversao=SYSDATETIME(), " +
                    "id_usuario_reverteu=?, justificativa_reversao=? " +
                    "WHERE id_imagem_original=? AND revertida=0")) {
                ps.setInt(1, idUsuario);
                ps.setString(2, justificativa);
                ps.setString(3, idImagemOriginal);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM muralha.veiculo_tempo_real_imagem " +
                    "WHERE id_original=? AND obliterada=1")) {
                ps.setString(1, idImagemOriginal);
                ps.executeUpdate();
            }
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    private static byte[] buscarBytesImagem(String id) throws Exception {
        Connection conn = Conexao.getConexao();
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT imagem FROM muralha.veiculo_tempo_real_imagem " +
                "WHERE id=? AND obliterada=0")) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBytes("imagem");
            }
        } finally {
            conn.close();
        }
        return null;
    }

    private static String buscarIdVeiculo(Connection conn, String idImagem) throws Exception {
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT id_veiculo_tempo_real FROM muralha.veiculo_tempo_real_imagem WHERE id=?")) {
            ps.setString(1, idImagem);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        }
        throw new IllegalArgumentException("Imagem sem veiculo associado: " + idImagem);
    }

    private static byte[] aplicarRetangulos(byte[] original,
            List<Map<String, Object>> coords) throws Exception {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(original));
        Graphics2D g = img.createGraphics();
        g.setColor(Color.BLACK);
        for (Map<String, Object> rect : coords) {
            int x = ((Number) rect.get("x")).intValue();
            int y = ((Number) rect.get("y")).intValue();
            int w = ((Number) rect.get("w")).intValue();
            int h = ((Number) rect.get("h")).intValue();
            g.fillRect(x, y, w, h);
        }
        g.dispose();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String fmt = (original[0] == (byte) 0xFF) ? "jpg" : "png";
        ImageIO.write(img, fmt, out);
        return out.toByteArray();
    }
}
