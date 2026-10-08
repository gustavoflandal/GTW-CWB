package muralha.digital.correlacaoplaca;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.google.gson.annotations.SerializedName;

public class VeiculoRegistro {

    private static final Logger logger = Logger.getLogger(VeiculoRegistro.class);

    private UUID id;
    private String placa;
    private Date data;

    @SerializedName("id_local")
    private int idLocal;

    @SerializedName("id_pista")
    private byte idPista;

    private short velocidade;

    @SerializedName("enviado_cliente")
    private boolean enviadoCliente;

    @SerializedName("data_enviado")
    private Date dataEnviado;

    private String classificacao;

    @SerializedName("estado_veiculo")
    private byte estadoVeiculo;

    @SerializedName("processado_tarefas_alerta")
    private byte processadoTarefasAlerta;

    @SerializedName("data_processado_tarefas_alerta")
    private Date dataProcessadoTarefasAlerta;

    @SerializedName("data_importado")
    private Date dataImportado;

    @SerializedName("id_usuario_alt")
    private Integer idUsuarioAlt;

    @SerializedName("data_alt")
    private Date dataAlt;

    @SerializedName("dados_alt_orig")
    private String dadosAltOrig;

    @SerializedName("enviado_blitz")
    private Boolean enviadoBlitz;

    @SerializedName("data_enviado_blitz")
    private Date dataEnviadoBlitz;

    @SerializedName("perfil_1")
    private String perfil1;

    @SerializedName("perfil_2")
    private String perfil2;

    @SerializedName("placa_frontal")
    private String placaFrontal;

    @SerializedName("info_adicional")
    private String infoAdicional;

    @SerializedName("numero_eixos")
    private Integer numeroEixos;

    @SerializedName("rodagem_dupla")
    private Boolean rodagemDupla;

    private Integer categoria;

    @SerializedName("placa_mercosul")
    private boolean placaMercosul;
    
    @SerializedName("id_imagem")
    private String idImagem;

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public Date getData() { return data; }
    public void setData(Date data) { this.data = data; }

    public int getIdLocal() { return idLocal; }
    public void setIdLocal(int idLocal) { this.idLocal = idLocal; }

    public byte getIdPista() { return idPista; }
    public void setIdPista(byte idPista) { this.idPista = idPista; }

    public short getVelocidade() { return velocidade; }
    public void setVelocidade(short velocidade) { this.velocidade = velocidade; }

    public boolean isEnviadoCliente() { return enviadoCliente; }
    public void setEnviadoCliente(boolean enviadoCliente) { this.enviadoCliente = enviadoCliente; }

    public Date getDataEnviado() { return dataEnviado; }
    public void setDataEnviado(Date dataEnviado) { this.dataEnviado = dataEnviado; }

    public String getClassificacao() { return classificacao; }
    public void setClassificacao(String classificacao) { this.classificacao = classificacao; }

    public byte getEstadoVeiculo() { return estadoVeiculo; }
    public void setEstadoVeiculo(byte estadoVeiculo) { this.estadoVeiculo = estadoVeiculo; }

    public byte getProcessadoTarefasAlerta() { return processadoTarefasAlerta; }
    public void setProcessadoTarefasAlerta(byte processadoTarefasAlerta) { this.processadoTarefasAlerta = processadoTarefasAlerta; }

    public Date getDataProcessadoTarefasAlerta() { return dataProcessadoTarefasAlerta; }
    public void setDataProcessadoTarefasAlerta(Date dataProcessadoTarefasAlerta) { this.dataProcessadoTarefasAlerta = dataProcessadoTarefasAlerta; }

    public Date getDataImportado() { return dataImportado; }
    public void setDataImportado(Date dataImportado) { this.dataImportado = dataImportado; }

    public Integer getIdUsuarioAlt() { return idUsuarioAlt; }
    public void setIdUsuarioAlt(Integer idUsuarioAlt) { this.idUsuarioAlt = idUsuarioAlt; }

    public Date getDataAlt() { return dataAlt; }
    public void setDataAlt(Date dataAlt) { this.dataAlt = dataAlt; }

    public String getDadosAltOrig() { return dadosAltOrig; }
    public void setDadosAltOrig(String dadosAltOrig) { this.dadosAltOrig = dadosAltOrig; }

    public Boolean getEnviadoBlitz() { return enviadoBlitz; }
    public void setEnviadoBlitz(Boolean enviadoBlitz) { this.enviadoBlitz = enviadoBlitz; }

    public Date getDataEnviadoBlitz() { return dataEnviadoBlitz; }
    public void setDataEnviadoBlitz(Date dataEnviadoBlitz) { this.dataEnviadoBlitz = dataEnviadoBlitz; }

    public String getPerfil1() { return perfil1; }
    public void setPerfil1(String perfil1) { this.perfil1 = perfil1; }

    public String getPerfil2() { return perfil2; }
    public void setPerfil2(String perfil2) { this.perfil2 = perfil2; }

    public String getPlacaFrontal() { return placaFrontal; }
    public void setPlacaFrontal(String placaFrontal) { this.placaFrontal = placaFrontal; }

    public String getInfoAdicional() { return infoAdicional; }
    public void setInfoAdicional(String infoAdicional) { this.infoAdicional = infoAdicional; }

    public Integer getNumeroEixos() { return numeroEixos; }
    public void setNumeroEixos(Integer numeroEixos) { this.numeroEixos = numeroEixos; }

    public Boolean getRodagemDupla() { return rodagemDupla; }
    public void setRodagemDupla(Boolean rodagemDupla) { this.rodagemDupla = rodagemDupla; }

    public Integer getCategoria() { return categoria; }
    public void setCategoria(Integer categoria) { this.categoria = categoria; }

    public boolean isPlacaMercosul() { return placaMercosul; }
    public void setPlacaMercosul(boolean placaMercosul) { this.placaMercosul = placaMercosul; }
    
    public String getIdImagem() { return idImagem; }
    public void setIdImagem(String idImagem) { this.idImagem = idImagem; }

    public static List<VeiculoRegistro> ObterVeiculos(
            Timestamp dataInicio,
            Timestamp dataFim,
            Integer limiteRegistros,
            Integer idLocal
    ) throws ConexaoException, SQLException {
        List<VeiculoRegistro> listaRet = new ArrayList<>();
        StringBuilder sbSQL = new StringBuilder();

        sbSQL.append("SELECT TOP ").append(limiteRegistros != null ? limiteRegistros : 10).append(" ");
        sbSQL.append("v.id, v.placa, ");
        sbSQL.append("MAX(v.data) AS data, MAX(v.id_local) AS id_local, MAX(v.id_pista) AS id_pista, ");
        sbSQL.append("MAX(v.velocidade) AS velocidade, ");
        sbSQL.append("CAST(MAX(CAST(v.enviado_cliente AS INT)) AS BIT) AS enviado_cliente, ");
        sbSQL.append("MAX(v.data_enviado) AS data_enviado, MAX(v.classificacao) AS classificacao, ");
        sbSQL.append("MAX(v.estado_veiculo) AS estado_veiculo, MAX(v.processado_tarefas_alerta) AS processado_tarefas_alerta, ");
        sbSQL.append("MAX(v.data_processado_tarefas_alerta) AS data_processado_tarefas_alerta, MAX(v.data_importado) AS data_importado, ");
        sbSQL.append("MAX(v.id_usuario_alt) AS id_usuario_alt, MAX(v.data_alt) AS data_alt, MAX(v.dados_alt_orig) AS dados_alt_orig, ");
        sbSQL.append("CAST(MAX(CAST(v.enviado_blitz AS INT)) AS BIT) AS enviado_blitz, MAX(v.data_enviado_blitz) AS data_enviado_blitz, ");
        sbSQL.append("MAX(v.perfil_1) AS perfil_1, MAX(v.perfil_2) AS perfil_2, MAX(v.placa_frontal) AS placa_frontal, ");
        sbSQL.append("MAX(v.info_adicional) AS info_adicional, MAX(v.numero_eixos) AS numero_eixos, ");
        sbSQL.append("CAST(MAX(CAST(v.rodagem_dupla AS INT)) AS BIT) AS rodagem_dupla, MAX(v.categoria) AS categoria, ");
        sbSQL.append("CAST(MAX(CAST(v.placa_mercosul AS INT)) AS BIT) AS placa_mercosul, MAX(vtri.id) AS id_imagem ");
        sbSQL.append("FROM muralha.veiculo_tempo_real v ");
        sbSQL.append("INNER JOIN muralha.veiculo_tempo_real_imagem vtri ");
        sbSQL.append("ON vtri.id_veiculo_tempo_real = v.id AND vtri.indice_imagem = 0 ");
        sbSQL.append("WHERE NOT EXISTS ( ");
        sbSQL.append("  SELECT 1 FROM muralha.veiculo_tempo_real_correcao c ");
        sbSQL.append("  WHERE c.id_veiculo = v.id ");
        sbSQL.append(") ");

        if (dataInicio != null && dataFim != null) {
            sbSQL.append("AND v.data BETWEEN ? AND ? ");
        } else if (dataInicio != null) {
            sbSQL.append("AND v.data >= ? ");
        } else if (dataFim != null) {
            sbSQL.append("AND v.data <= ? ");
        }

        if (idLocal != null) {
            sbSQL.append("AND v.id_local = ? ");
        }

        sbSQL.append("GROUP BY v.id, v.placa ");

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());

            int paramIndex = 1;
            if (dataInicio != null && dataFim != null) {
                ps.setTimestamp(paramIndex++, dataInicio);
                ps.setTimestamp(paramIndex++, dataFim);
            } else if (dataInicio != null) {
                ps.setTimestamp(paramIndex++, dataInicio);
            } else if (dataFim != null) {
                ps.setTimestamp(paramIndex++, dataFim);
            }

            if (idLocal != null) {
                ps.setInt(paramIndex++, idLocal);
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                VeiculoRegistro v = new VeiculoRegistro();
                v.setId(UUID.fromString(rs.getString("id")));
                v.setPlaca(rs.getString("placa"));
                v.setData(rs.getTimestamp("data"));
                v.setIdLocal(rs.getInt("id_local"));
                v.setIdPista(rs.getByte("id_pista"));
                v.setVelocidade(rs.getShort("velocidade"));
                v.setEnviadoCliente(rs.getBoolean("enviado_cliente"));
                v.setDataEnviado(rs.getTimestamp("data_enviado"));
                v.setClassificacao(rs.getString("classificacao"));
                v.setEstadoVeiculo(rs.getByte("estado_veiculo"));
                v.setProcessadoTarefasAlerta(rs.getByte("processado_tarefas_alerta"));
                v.setDataProcessadoTarefasAlerta(rs.getTimestamp("data_processado_tarefas_alerta"));
                v.setDataImportado(rs.getTimestamp("data_importado"));
                v.setIdUsuarioAlt(rs.getObject("id_usuario_alt") != null ? rs.getInt("id_usuario_alt") : null);
                v.setDataAlt(rs.getTimestamp("data_alt"));
                v.setDadosAltOrig(rs.getString("dados_alt_orig"));
                v.setEnviadoBlitz(rs.getObject("enviado_blitz") != null ? rs.getBoolean("enviado_blitz") : null);
                v.setDataEnviadoBlitz(rs.getTimestamp("data_enviado_blitz"));
                v.setPerfil1(rs.getString("perfil_1"));
                v.setPerfil2(rs.getString("perfil_2"));
                v.setPlacaFrontal(rs.getString("placa_frontal"));
                v.setInfoAdicional(rs.getString("info_adicional"));
                v.setNumeroEixos(rs.getObject("numero_eixos") != null ? rs.getInt("numero_eixos") : null);
                v.setRodagemDupla(rs.getObject("rodagem_dupla") != null ? rs.getBoolean("rodagem_dupla") : null);
                v.setCategoria(rs.getObject("categoria") != null ? rs.getInt("categoria") : null);
                v.setPlacaMercosul(rs.getBoolean("placa_mercosul"));
                v.setIdImagem(rs.getString("id_imagem"));

                listaRet.add(v);
            }
        } catch (Exception e) {
            throw new SQLException("Erro ao montar SQL (ObterVeiculos):: ", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("ERRO de SQL", e);
            }
        }

        return listaRet;
    }



    public static void ProcessarCorrecaoPlacas(List<Map<String, String>> loteCompleto) throws SQLException, ConexaoException {
        if (loteCompleto == null || loteCompleto.isEmpty()) return;

        String sqlInsert = "INSERT INTO muralha.veiculo_tempo_real_correcao (id_veiculo, placa_original, placa_digitada, id_usuario, data) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdate = "UPDATE muralha.veiculo_tempo_real SET placa = ? WHERE id = ?";

        Connection conn = null;
        PreparedStatement psInsert = null;
        PreparedStatement psUpdate = null;

        try {
            conn = Conexao.getConexao();
            conn.setAutoCommit(false);

            psInsert = conn.prepareStatement(sqlInsert);
            psUpdate = conn.prepareStatement(sqlUpdate);

            Timestamp agora = new Timestamp(System.currentTimeMillis());

            for (Map<String, String> item : loteCompleto) {
                String idVeiculo     = item.get("idVeiculo");
                String placaAtual    = item.get("placaAtual");
                String novaPlaca     = item.get("novaPlaca");
                String idUsuarioStr  = item.get("idUsuario");

                if (idVeiculo != null && novaPlaca != null && idUsuarioStr != null) {
                    int idUsuario = Integer.parseInt(idUsuarioStr);

                    psInsert.setString(1, idVeiculo);
                    psInsert.setString(2, placaAtual.toUpperCase());
                    psInsert.setString(3, novaPlaca.toUpperCase());
                    psInsert.setInt(4, idUsuario);
                    psInsert.setTimestamp(5, agora);
                    psInsert.addBatch();

                    if (!novaPlaca.equalsIgnoreCase(placaAtual)) {
                        psUpdate.setString(1, novaPlaca.toUpperCase());
                        psUpdate.setString(2, idVeiculo);
                        psUpdate.addBatch();
                    }
                }
            }

            psInsert.executeBatch();
            psUpdate.executeBatch();
            conn.commit();

            System.out.println("Correções registradas e placas atualizadas com sucesso.");
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new SQLException("Erro ao processar correções de placas", e);
        } finally {
            try {
                if (psInsert != null) psInsert.close();
                if (psUpdate != null) psUpdate.close();
                if (conn != null) conn.setAutoCommit(true); conn.close();
            } catch (SQLException e) {
                throw new ConexaoException("Erro ao finalizar conexão", e);
            }
        }
    }



}
