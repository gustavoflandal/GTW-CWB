package muralha.digital.atendimento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroFato {

    private static final Logger logger = Logger.getLogger(RegistroFato.class);

    // Campos de registro_fato_endereco
    private Double latitude;
    private Double longitude;
    private String rua;
    private String numero;
    private String bairro;
    private Integer idTipoEvento;
    private Integer idRegistroFato;
    private Integer idEndereco;
    private Integer idTipo;

    // Campos de registro_fato_endereco_evento
    private Integer idEvento;
    private String descricaoEvento;

    // Campos de registro_fato_veiculo
    private String placa;

    // Campos de registro_fato
    private Integer id;
    private Integer idStatus;
    private Integer temBoletim;
    private Integer idUsuario;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEncerramento;
    private Integer privado;

    // ================== Getters/Setters ==================
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getRua() { return rua; }
    public void setRua(String rua) { this.rua = rua; }

    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public Integer getIdTipoEvento() { return idTipoEvento; }
    public void setIdTipoEvento(Integer idTipoEvento) { this.idTipoEvento = idTipoEvento; }

    public Integer getIdRegistroFato() { return idRegistroFato; }
    public void setIdRegistroFato(Integer idRegistroFato) { this.idRegistroFato = idRegistroFato; }

    public Integer getIdEndereco() { return idEndereco; }
    public void setIdEndereco(Integer idEndereco) { this.idEndereco = idEndereco; }

    public Integer getIdEvento() { return idEvento; }
    public void setIdEvento(Integer idEvento) { this.idEvento = idEvento; }

    public String getDescricaoEvento() { return descricaoEvento; }
    public void setDescricaoEvento(String descricaoEvento) { this.descricaoEvento = descricaoEvento; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    public Integer getIdTipo() { return idTipo; }
    public void setIdTipo(Integer idTipo) { this.idTipo = idTipo; }

    public Integer getIdStatus() { return idStatus; }
    public void setIdStatus(Integer idStatus) { this.idStatus = idStatus; }

    public Integer getTemBoletim() { return temBoletim; }
    public void setTemBoletim(Integer temBoletim) { this.temBoletim = temBoletim; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }

    public LocalDateTime getDataEncerramento() { return dataEncerramento; }
    public void setDataEncerramento(LocalDateTime dataEncerramento) { this.dataEncerramento = dataEncerramento; }

    public Integer getPrivado() { return privado; }
    public void setPrivado(Integer privado) { this.privado = privado; }

    // ================== Método de consulta ==================
    public static List<RegistroFato> listarRegistroFato(List<Integer> tipo) {
        List<RegistroFato> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT rfe.latitude, rfe.longitude, rfe.rua, rfe.numero, rfe.bairro, " +
            "       rfe.id_tipo_evento, rfe.id_registro_fato, rfe.id AS id_endereco, " +
            "       rfee.id AS id_evento, rfee.descricao, " +
            "       v.placa, r.id, r.id_status, r.id_tipo, r.tem_boletim, r.id_usuario, " +
            "       r.data_criacao, r.data_encerramento, r.privado " +
            "FROM muralha.registro_fato r " +
            "LEFT JOIN muralha.registro_fato_endereco rfe ON rfe.id_registro_fato = r.id " +
            "LEFT JOIN muralha.registro_fato_endereco_evento rfee ON rfee.id = rfe.id_tipo_evento " +
            "LEFT JOIN muralha.registro_fato_veiculo v ON v.id_registro_fato = r.id " +
            "WHERE rfee.id IN (1, 3) "
        );

        if (tipo != null && !tipo.isEmpty()) {
            String placeholders = tipo.stream()
                                      .map(t -> "?")
                                      .collect(Collectors.joining(", "));
            sql.append(" AND r.id_tipo IN (").append(placeholders).append(") ");
        }

        sql.append("ORDER BY v.placa");

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            if (tipo != null && !tipo.isEmpty()) {
                for (Integer t : tipo) {
                    ps.setInt(paramIndex++, t);
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RegistroFato obj = new RegistroFato();

                    obj.setLatitude(rs.getObject("latitude") != null ? rs.getDouble("latitude") : null);
                    obj.setLongitude(rs.getObject("longitude") != null ? rs.getDouble("longitude") : null);
                    obj.setRua(rs.getString("rua"));
                    obj.setNumero(rs.getObject("numero") != null ? rs.getString("numero") : null);
                    obj.setBairro(rs.getString("bairro"));
                    obj.setIdTipoEvento(rs.getObject("id_tipo_evento") != null ? rs.getInt("id_tipo_evento") : null);
                    obj.setIdRegistroFato(rs.getObject("id_registro_fato") != null ? rs.getInt("id_registro_fato") : null);
                    obj.setIdEndereco(rs.getObject("id_endereco") != null ? rs.getInt("id_endereco") : null);

                    obj.setIdEvento(rs.getObject("id_evento") != null ? rs.getInt("id_evento") : null);
                    obj.setDescricaoEvento(rs.getString("descricao"));
                    obj.setIdTipo(rs.getInt("id_tipo"));

                    obj.setPlaca(rs.getString("placa"));
                    obj.setId(rs.getObject("id") != null ? rs.getInt("id") : null);
                    obj.setIdStatus(rs.getObject("id_status") != null ? rs.getInt("id_status") : null);
                    obj.setTemBoletim(rs.getObject("tem_boletim") != null ? rs.getInt("tem_boletim") : null);
                    obj.setIdUsuario(rs.getObject("id_usuario") != null ? rs.getInt("id_usuario") : null);

                    Timestamp tsCriacao = rs.getTimestamp("data_criacao");
                    if (tsCriacao != null) {
                        obj.setDataCriacao(tsCriacao.toLocalDateTime());
                    }

                    Timestamp tsEnc = rs.getTimestamp("data_encerramento");
                    if (tsEnc != null) {
                        obj.setDataEncerramento(tsEnc.toLocalDateTime());
                    }

                    obj.setPrivado(rs.getObject("privado") != null ? rs.getInt("privado") : null);

                    lista.add(obj);
                }

                logger.info("Registros retornados: " + lista.size());
            }

        } catch (Exception e) {
            logger.error("Erro ao listar registros de fato", e);
            throw new RuntimeException("Erro ao listar registros de fato", e);
        }

        return lista;
    }

    
    public static List<Map<String, Object>> listarRegistroFatoTipo() {
        List<Map<String, Object>> lista = new ArrayList<>();

        String sql = "SELECT id, tipo_desc FROM muralha.registro_fato_tipo";

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", rs.getInt("id"));
                item.put("tipo_desc", rs.getString("tipo_desc"));
                lista.add(item);
            }

            logger.info("Tipos de registro retornados: " + lista.size());

        } catch (Exception e) {
            logger.error("Erro ao listar tipos de registro de fato", e);
            throw new RuntimeException("Erro ao listar tipos de registro de fato", e);
        }

        return lista;
    }

}
