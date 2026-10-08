package muralha.digital.mapainterativo;

import java.sql.*;
import java.util.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

public class Atendimento {
    // =========================
    // Campos - Atendimento
    // =========================
    private int idAtendimento;
    private String protocolo;
    private int idOrigem;
    private long idRegistroFatoAtendimento;
    private int idSituacao;
    private String dataCriacaoAtendimento;
    private String dataEncerramentoAtendimento;
    private int idUsuarioCriacao;
    private UUID idOcorrencia;

    // =========================
    // Campos - Registro Fato
    // =========================
    private long idRegistroFato;
    private int idTipoRegistro;
    private int idStatusRegistro;
    private int temBoletim;
    private int idUsuarioRegistro;
    private String dataCriacaoRegistro;
    private String dataEncerramentoRegistro;
    private int privado;
    private String tipo;
    private String status;
    

    // =========================
    // Campos - Endereço
    // =========================
    private int idEndereco;
    private long idRegistroFatoEndereco;
    private int idTipoEvento;
    private int idCidade;
    private String cidade;
    private String cep;
    private String bairro;
    private String rua;
    private int numero;
    private String complemento;
    private double latitude;
    private double longitude;

    public static List<Atendimento> ObterAtendimentosPorTipoEvento(int idTipoEvento) throws SQLException, ConexaoException {
        List<Atendimento> listaRet = new ArrayList<>();
        StringBuilder sbSQL = new StringBuilder();

        sbSQL.append("SELECT ");
        sbSQL.append(" a.id AS id_atendimento, a.protocolo, a.id_origem, a.id_registro_fato AS id_registro_fato_atendimento, ");
        sbSQL.append(" a.id_situacao, ");
        sbSQL.append(" FORMAT(a.data_criacao, 'dd/MM/yyyy HH:mm:ss') AS data_criacao_atendimento, ");
        sbSQL.append(" FORMAT(a.data_encerramento, 'dd/MM/yyyy HH:mm:ss') AS data_encerramento_atendimento, ");
        sbSQL.append(" a.id_usuario_criacao, a.id_ocorrencia, ");
        sbSQL.append(" t.tipo_desc AS tipo,");
        sbSQL.append(" s.descricao AS status,");
        sbSQL.append(" c.nome as cidade,");
        sbSQL.append(" rf.id AS id_registro_fato, rf.id_tipo AS id_tipo_registro, rf.id_status AS id_status_registro, ");
        sbSQL.append(" rf.tem_boletim, rf.id_usuario AS id_usuario_registro, ");
        sbSQL.append(" FORMAT(rf.data_criacao, 'dd/MM/yyyy HH:mm:ss') AS data_criacao_registro, ");
        sbSQL.append(" FORMAT(rf.data_encerramento, 'dd/MM/yyyy HH:mm:ss') AS data_encerramento_registro, ");
        sbSQL.append(" rf.privado, ");
        sbSQL.append(" e.id AS id_endereco, e.id_registro_fato AS id_registro_fato_endereco, e.id_tipo_evento, e.id_cidade, ");
        sbSQL.append(" e.cep, e.bairro, e.rua, e.numero, e.complemento, e.latitude, e.longitude ");
        sbSQL.append(" FROM muralha.atendimento a ");
        sbSQL.append(" INNER JOIN muralha.registro_fato rf ON rf.id = a.id_registro_fato ");
        sbSQL.append(" LEFT JOIN muralha.registro_fato_endereco e ON rf.id = e.id_registro_fato ");
        sbSQL.append(" LEFT JOIN muralha.registro_fato_status s ON rf.id_status = s.id");
        sbSQL.append(" LEFT JOIN muralha.registro_fato_tipo t ON rf.id_tipo = t.id");
        sbSQL.append(" LEFT JOIN muralha.cidade c on c.id = e.id_cidade");
        sbSQL.append(" WHERE e.id_tipo_evento = ? ");

        try (Connection conn = Conexao.getConexao();
             PreparedStatement ps = conn.prepareStatement(sbSQL.toString())) {

            ps.setInt(1, idTipoEvento);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Atendimento a = new Atendimento();

                    // Atendimento
                    a.setIdAtendimento(rs.getInt("id_atendimento"));
                    a.setProtocolo(rs.getString("protocolo"));
                    a.setIdOrigem(rs.getInt("id_origem"));
                    a.setIdRegistroFatoAtendimento(rs.getLong("id_registro_fato_atendimento"));
                    a.setIdSituacao(rs.getInt("id_situacao"));
                    a.setStatus(rs.getString("status"));
                    a.setTipo(rs.getString("tipo"));
                    a.setDataCriacaoAtendimento(rs.getString("data_criacao_atendimento")); 
                    a.setDataEncerramentoAtendimento(rs.getString("data_encerramento_atendimento"));
                    a.setIdUsuarioCriacao(rs.getInt("id_usuario_criacao"));
                    String idOcorrenciaStr = rs.getString("id_ocorrencia");
                    if (idOcorrenciaStr != null) {
                        a.setIdOcorrencia(UUID.fromString(idOcorrenciaStr));
                    }

                    // Registro Fato
                    a.setIdRegistroFato(rs.getLong("id_registro_fato"));
                    a.setIdTipoRegistro(rs.getInt("id_tipo_registro"));
                    a.setIdStatusRegistro(rs.getInt("id_status_registro"));
                    a.setTemBoletim(rs.getInt("tem_boletim"));
                    a.setIdUsuarioRegistro(rs.getInt("id_usuario_registro"));
                    a.setDataCriacaoRegistro(rs.getString("data_criacao_registro"));
                    a.setDataEncerramentoRegistro(rs.getString("data_encerramento_registro"));
                    a.setPrivado(rs.getInt("privado"));

                    // Endereço
                    a.setIdEndereco(rs.getInt("id_endereco"));
                    a.setIdRegistroFatoEndereco(rs.getLong("id_registro_fato_endereco"));
                    a.setIdTipoEvento(rs.getInt("id_tipo_evento"));
                    a.setCidade(rs.getString("cidade"));
                    a.setIdCidade(rs.getInt("id_cidade"));
                    a.setCep(rs.getString("cep"));
                    a.setBairro(rs.getString("bairro"));
                    a.setRua(rs.getString("rua"));
                    a.setNumero(rs.getObject("numero") != null ? rs.getInt("numero") : null);
                    a.setComplemento(rs.getString("complemento"));
                    a.setLatitude(rs.getDouble("latitude"));
                    a.setLongitude(rs.getDouble("longitude"));

                    listaRet.add(a);
                }
            }
        }
        return listaRet;
    }

    // =========================
    // Getters e Setters
    // =========================

    public int getIdAtendimento() {
        return idAtendimento;
    }

    public void setIdAtendimento(int idAtendimento) {
        this.idAtendimento = idAtendimento;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(String protocolo) {
        this.protocolo = protocolo;
    }

    public int getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(int idOrigem) {
        this.idOrigem = idOrigem;
    }

    public long getIdRegistroFatoAtendimento() {
        return idRegistroFatoAtendimento;
    }

    public void setIdRegistroFatoAtendimento(long idRegistroFatoAtendimento) {
        this.idRegistroFatoAtendimento = idRegistroFatoAtendimento;
    }

    public int getIdSituacao() {
        return idSituacao;
    }

    public void setIdSituacao(int idSituacao) {
        this.idSituacao = idSituacao;
    }

    public String getDataCriacaoAtendimento() {
        return dataCriacaoAtendimento;
    }

    public void setDataCriacaoAtendimento(String dataCriacaoAtendimento) {
        this.dataCriacaoAtendimento = dataCriacaoAtendimento;
    }
    
    public String getDataCriacaoAtendimentoFormatado() {
        if (dataCriacaoAtendimento == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(dataCriacaoAtendimento);
    }

    public String getDataEncerramentoAtendimento() {
        return dataEncerramentoAtendimento;
    }

    public void setDataEncerramentoAtendimento(String dataEncerramentoAtendimento) {
        this.dataEncerramentoAtendimento = dataEncerramentoAtendimento;
    }
    
    public String getDataEncerramentoAtendimentoFormatado() {
        if (dataEncerramentoAtendimento == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(dataEncerramentoAtendimento);
    }

    public int getIdUsuarioCriacao() {
        return idUsuarioCriacao;
    }

    public void setIdUsuarioCriacao(int idUsuarioCriacao) {
        this.idUsuarioCriacao = idUsuarioCriacao;
    }

    public UUID getIdOcorrencia() {
        return idOcorrencia;
    }

    public void setIdOcorrencia(UUID idOcorrencia) {
        this.idOcorrencia = idOcorrencia;
    }
    
    public String getTipo() {
    	return tipo;
    }
    
    public void setTipo(String tipo) {
    	this.tipo = tipo;
    }
    
    public String getCidade() {
    	return cidade;
    }
    
    public void setCidade(String cidade) {
    	this.cidade = cidade;
    }
    
    public String getStatus() {
    	return status;
    }
    
    public void setStatus(String status) {
    	this.status = status;
    }

    public long getIdRegistroFato() {
        return idRegistroFato;
    }

    public void setIdRegistroFato(long idRegistroFato) {
        this.idRegistroFato = idRegistroFato;
    }

    public int getIdTipoRegistro() {
        return idTipoRegistro;
    }

    public void setIdTipoRegistro(int idTipoRegistro) {
        this.idTipoRegistro = idTipoRegistro;
    }

    public int getIdStatusRegistro() {
        return idStatusRegistro;
    }

    public void setIdStatusRegistro(int idStatusRegistro) {
        this.idStatusRegistro = idStatusRegistro;
    }

    public int getTemBoletim() {
        return temBoletim;
    }

    public void setTemBoletim(int temBoletim) {
        this.temBoletim = temBoletim;
    }

    public int getIdUsuarioRegistro() {
        return idUsuarioRegistro;
    }

    public void setIdUsuarioRegistro(int idUsuarioRegistro) {
        this.idUsuarioRegistro = idUsuarioRegistro;
    }

    public String getDataCriacaoRegistro() {
        return dataCriacaoRegistro;
    }

    public void setDataCriacaoRegistro(String dataCriacaoRegistro) {
        this.dataCriacaoRegistro = dataCriacaoRegistro;
    }

    public String getDataEncerramentoRegistro() {
        return dataEncerramentoRegistro;
    }

    public void setDataEncerramentoRegistro(String dataEncerramentoRegistro) {
        this.dataEncerramentoRegistro = dataEncerramentoRegistro;
    }

    public int getPrivado() {
        return privado;
    }

    public void setPrivado(int privado) {
        this.privado = privado;
    }

    public int getIdEndereco() {
        return idEndereco;
    }

    public void setIdEndereco(int idEndereco) {
        this.idEndereco = idEndereco;
    }

    public long getIdRegistroFatoEndereco() {
        return idRegistroFatoEndereco;
    }

    public void setIdRegistroFatoEndereco(long idRegistroFatoEndereco) {
        this.idRegistroFatoEndereco = idRegistroFatoEndereco;
    }

    public int getIdTipoEvento() {
        return idTipoEvento;
    }

    public void setIdTipoEvento(int idTipoEvento) {
        this.idTipoEvento = idTipoEvento;
    }

    public int getIdCidade() {
        return idCidade;
    }

    public void setIdCidade(int idCidade) {
        this.idCidade = idCidade;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
