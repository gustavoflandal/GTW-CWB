package muralha.digital.mapainterativo;

import java.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class GuarnicaoLocalizacao {
    // =========================
    // Campos - Atendimento
    // =========================
    private int idUsuario;
    private int idGuarnicao;
    private String nome;
    private String telefone;
    private boolean disponivel;
    private String latitude;
    private String longitude;
    private String nomeGuarnicao;
    private String responsavel;


 

    // =========================
    // Getters e Setters
    // =========================

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdGuarnicao() {
        return idGuarnicao;
    }

    public void setIdGuarnicao(int idGuarnicao) {
        this.idGuarnicao = idGuarnicao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }
    
    public String getNomeGuarnicao() {
        return nomeGuarnicao;
    }

    public void setNomeGuarnicao(String nomeGuarnicao) {
        this.nomeGuarnicao = nomeGuarnicao;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    
    public static List<GuarnicaoLocalizacao> ObterIntegrantesGuarnicao(int idGuarnicao) throws SQLException, ConexaoException {
        List<GuarnicaoLocalizacao> listaRet = new ArrayList<>();
        StringBuilder sbSQL = new StringBuilder();

        sbSQL.append(" SELECT                      ");
        sbSQL.append(" gi.id_usuario,              ");
        sbSQL.append(" gi.id_guarnicao,            ");
        sbSQL.append(" su.nome,                    ");
        sbSQL.append(" g.nome as nomeGuarnicao,    ");
        sbSQL.append(" su.telefone,                ");
        sbSQL.append(" g.disponivel,               ");
        sbSQL.append(" l.latitude,                 ");
        sbSQL.append(" l.longitude,                ");
        sbSQL.append(" suResp.nome AS responsavel, ");
        sbSQL.append(" suResp.telefone AS telefoneResponsavel ");
        sbSQL.append(" FROM muralha.guarnicao_integrante gi                                         ");
        sbSQL.append(" LEFT JOIN sis_usuario su ON su.id_usuario = gi.id_usuario                    ");
        sbSQL.append(" LEFT JOIN muralha.guarnicao g ON gi.id_guarnicao = g.id                      ");
        sbSQL.append(" LEFT JOIN muralha.agente_localizacao_atual l ON l.id_usuario = gi.id_usuario ");
        sbSQL.append(" LEFT JOIN sis_usuario suResp ON suResp.id_usuario = g.id_usuario_responsavel ");

        if (idGuarnicao != 0) {
            sbSQL.append(" WHERE gi.id_guarnicao = ? ");
        }

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sbSQL.toString())) {

            if (idGuarnicao != 0) {
                ps.setInt(1, idGuarnicao);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GuarnicaoLocalizacao guarnicao = new GuarnicaoLocalizacao();

                    guarnicao.setIdUsuario(rs.getInt("id_usuario"));
                    guarnicao.setIdGuarnicao(rs.getInt("id_guarnicao"));
                    guarnicao.setNome(rs.getString("nome"));
                    guarnicao.setNomeGuarnicao(rs.getString("nomeGuarnicao"));
                    guarnicao.setTelefone(rs.getString("telefone"));
                    guarnicao.setDisponivel(rs.getBoolean("disponivel"));
                    guarnicao.setLatitude(rs.getString("latitude"));
                    guarnicao.setLongitude(rs.getString("longitude"));
                    guarnicao.setResponsavel(rs.getString("responsavel"));

                    listaRet.add(guarnicao);
                }
            }
        }

        return listaRet;
    }


 
}
