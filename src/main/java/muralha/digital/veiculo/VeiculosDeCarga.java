package muralha.digital.veiculo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.google.gson.JsonObject;

import muralha.digital.util.Paginacao;

@XmlRootElement(name = "VeiculosDeCarga")
@XmlAccessorType(XmlAccessType.FIELD)
public class VeiculosDeCarga {
    @XmlTransient
    private static final Logger logger = LogManager.getLogger(VeiculosDeCarga.class);

    @XmlElementWrapper(name = "ListaVeiculos")
    @XmlElement(name = "VeiculoDeCarga")
    private List<VeiculoDeCarga> listaVeiculos;

    private Paginacao paginacao;

    public VeiculosDeCarga() {
        super();
    }

    public List<VeiculoDeCarga> getListaVeiculos() {
        return listaVeiculos;
    }

    public void setListaVeiculos(List<VeiculoDeCarga> listaVeiculos) {
        this.listaVeiculos = listaVeiculos;
    }

    public Paginacao getPaginacao() {
        return paginacao;
    }

    public void setPaginacao(Paginacao paginacao) {
        this.paginacao = paginacao;
    }

    public static VeiculosDeCarga ObterVeiculosDeCargaPorFiltros(
            String placa, Date dataIni, Date dataFim, String equipamento, String faixa, String classificacao,
            boolean buscarApenasVeiculoComImagem, boolean consultaMapa, Paginacao paginacao,
            boolean trazer_imagens, boolean exportarConsulta, boolean modoGrade,
            String corVeiculo, String anoFabricacao, String anoModelo, String renavam, String chassi,
            String tipoVeiculo, String restricao, String marca, String modelo,
            String tipoPlaca, int filtroPlaca, int idLocalidade, boolean somenteUltimaPassagem, boolean apenasVeiculosComCarga, boolean comPesagem, boolean deveAplicarFiltroDeImagens)
            throws SQLException {

        VeiculosDeCarga retorno = new VeiculosDeCarga();
        List<VeiculoDeCarga> listaRet = new ArrayList<>();
        StringBuilder sbSQL = new StringBuilder();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erro = false;

        try {
            sbSQL.append("EXEC dbo.spu_obterVeiculosGtwPorFiltros ");
            sbSQL.append("@placa = ?, ");
            sbSQL.append("@dataIni = ?, ");
            sbSQL.append("@dataFim = ?, ");
            sbSQL.append("@equipamentos = ?, ");
            sbSQL.append("@faixa = ?, ");
            sbSQL.append("@classificacao = ?, ");
            sbSQL.append("@buscarApenasVeiculoComImagem = ?, ");
            sbSQL.append("@consultaMapa = ?, ");
            sbSQL.append("@exportarConsulta = ?, ");
            sbSQL.append("@offset = ?, ");
            sbSQL.append("@itensPorPagina = ?, ");
            sbSQL.append("@marca = ?, ");
            sbSQL.append("@modelo = ?, ");
            sbSQL.append("@idCor = ?, ");
            sbSQL.append("@anoFabricacao = ?, ");
            sbSQL.append("@anoModelo = ?, ");
            sbSQL.append("@renavam = ?, ");
            sbSQL.append("@chassi = ?, ");
            sbSQL.append("@idLocalidade = ?, ");
            sbSQL.append("@restricao = ?, ");
            sbSQL.append("@filtroPlaca = ?, ");
            sbSQL.append("@tipoPlaca = ?, ");
            sbSQL.append("@tipoVeiculo = ?, ");
            sbSQL.append("@deveAplicarFiltroDeImagens = ?, ");
            sbSQL.append("@apenas_veiculos_carga = ?, ");
            sbSQL.append("@com_pesagem = ?, ");
            sbSQL.append("@somenteUltimaPassagem = ? ");

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());

            int idx = 1;

            ps.setString(idx++, placa);
            ps.setTimestamp(idx++, dataIni != null ? new Timestamp(dataIni.getTime()) : null);
            ps.setTimestamp(idx++, dataFim != null ? new Timestamp(dataFim.getTime()) : null);
            ps.setString(idx++, equipamento);
            ps.setString(idx++, faixa);
            ps.setString(idx++, classificacao);
            ps.setBoolean(idx++, buscarApenasVeiculoComImagem);
            ps.setBoolean(idx++, consultaMapa);
            ps.setBoolean(idx++, exportarConsulta);

            if (paginacao.OperacaoValida()) {
                ps.setInt(idx++, paginacao.Offset());
                ps.setInt(idx++, paginacao.ItensPorPagina());
            } else {
                ps.setInt(idx++, 0);
                ps.setInt(idx++, 5);
            }

            ps.setString(idx++, marca);
            ps.setString(idx++, modelo);
            if (corVeiculo != null && !corVeiculo.isEmpty()) {
                ps.setInt(idx++, Integer.parseInt(corVeiculo));
            } else {
                ps.setNull(idx++, Types.INTEGER);
            }
            if (anoFabricacao != null && !anoFabricacao.isEmpty()) {
                ps.setInt(idx++, Integer.parseInt(anoFabricacao));
            } else {
                ps.setNull(idx++, Types.INTEGER);
            }
            if (anoModelo != null && !anoModelo.isEmpty()) {
                ps.setInt(idx++, Integer.parseInt(anoModelo));
            } else {
                ps.setNull(idx++, Types.INTEGER);
            }
            ps.setString(idx++, renavam);
            ps.setString(idx++, chassi);
            if (idLocalidade >= 0) {
                ps.setInt(idx++, idLocalidade);
            } else {
                ps.setNull(idx++, Types.INTEGER);
            }
            ps.setString(idx++, restricao);

            ps.setInt(idx++, filtroPlaca);
            ps.setString(idx++, tipoPlaca);
            if (tipoVeiculo != null && !tipoVeiculo.trim().isEmpty()) {
                ps.setString(idx++, tipoVeiculo);
            } else {
                ps.setNull(idx++, Types.VARCHAR);
            }

            ps.setBoolean(idx++, deveAplicarFiltroDeImagens);
            ps.setBoolean(idx++, apenasVeiculosComCarga);
            ps.setBoolean(idx++, comPesagem);
            ps.setBoolean(idx++, somenteUltimaPassagem);

            rs = ps.executeQuery();

            int registros = 0;
            while (rs.next()) {
                if (registros == 0 && !consultaMapa)
                    registros = rs.getInt("total_registros");

                VeiculoDeCarga item = new VeiculoDeCarga();
                long id = rs.getLong("id");

                item.setId(id);
                item.setPlaca(rs.getString("placa"));
                item.setData(rs.getTimestamp("data"));
                item.setIdLocal(rs.getInt("id_local"));
                item.setSerieEquipamento(rs.getInt("serie_equipamento"));
                item.setCodigoEquipamento(rs.getString("codigo_equipamento"));
                item.setNome(rs.getString("nome"));
                item.setIdPista(rs.getInt("id_pista"));
                item.setFaixa(rs.getInt("faixa"));

                long idVeiculo = rs.getLong("id_veiculo");
                item.setIdVeiculo(idVeiculo);
                
                Double latitude = rs.getDouble("latitude");
                item.setLatitude(rs.wasNull() ? null : latitude);
                
                Double longitude = rs.getDouble("longitude");
                item.setLongitude(rs.wasNull() ? null : longitude);
                
                Double comprimento = rs.getDouble("comprimento");
                item.setComprimento(rs.wasNull() ? null : comprimento);
                
                Double velocidade = rs.getDouble("velocidade");
                item.setVelocidade(rs.wasNull() ? null : velocidade);
                
                item.setIdClasse(rs.getString("id_classe"));
                item.setClassificacao(rs.getString("classificacao"));
                item.setEnviadoCliente(rs.getBoolean("enviado_cliente"));
                item.setComImagem(rs.getBoolean("com_imagem"));
                item.setPossuiCoordenadas(rs.getBoolean("possui_coordenadas"));
                item.setPossuiAlerta(rs.getBoolean("possui_alerta"));
                item.setMarca(rs.getString("marca"));
                item.setModelo(rs.getString("modelo"));
                item.setCor(rs.getString("cor"));
                item.setAnoModelo(rs.getString("ano_modelo"));
                item.setTipo(rs.getString("tipo"));
                item.setLocalidade(rs.getString("localidade"));
                item.setUf(rs.getString("uf"));
                item.setAnoFabricacao(rs.getString("ano_fabricacao"));
                item.setRenavam(rs.getString("renavam"));
                item.setChassi(rs.getString("chassi"));
                item.setRestricao(rs.getString("restricao"));
                
                Double pbt = rs.getDouble("pbt");
                item.setPbt(rs.wasNull() ? null : pbt);
                
                Double pbtc = rs.getDouble("pbtc");
                item.setPbtc(rs.wasNull() ? null : pbtc);
                
                item.setNumeroEixos(rs.getInt("numero_eixos"));
                item.setRodagemDupla(rs.getString("rodagem_dupla"));
                item.setCategoria(rs.getString("categoria"));
                item.setClassificacaoArt96(rs.getString("classificacao_art96"));
                item.setDataImportado(rs.getTimestamp("data_importado"));
                item.setPlacaMercosul(rs.getBoolean("placa_mercosul"));
                item.setCorPlaca(rs.getString("cor_placa"));
                item.setVeicAnterior(rs.getString("veic_anterior"));
                item.setVeicProximo(rs.getString("veic_proximo"));
                item.setTotalRegistros(rs.getInt("total_registros"));

                listaRet.add(item);
            }

            if (!consultaMapa) {
                paginacao.TotalRegistros(registros);
                retorno.setPaginacao(paginacao);
            }
            retorno.setListaVeiculos(listaRet);
        } catch (Exception e) {
            erro = true;
            logger.error("Erro ao obter veículos de carga: " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (ps != null)
                    ps.close();
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos do banco: " + e.getMessage(), e);
            }
            if (erro)
                throw new SQLException("Erro ao consultar os veículos de carga no banco de dados!");
        }

        return retorno;
    }

    public static VeiculoDeCargaDetalhes ObterVeiculoDeCargaPorId(long id) throws SQLException {
        VeiculoDeCargaDetalhes veic = new VeiculoDeCargaDetalhes();
        StringBuilder sbSQL = new StringBuilder();

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean erro = false;
        String msgErro;

        try {
            sbSQL.append(" EXEC dbo.spuObterDadosVeiculoGtw ? ");
            logger.debug("Executando query: " + sbSQL.toString() + " com ID: " + id);

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());
            ps.setLong(1, id);

            rs = ps.executeQuery();

            boolean hasNext = rs.next();
            logger.debug("ResultSet.next() retornou: " + hasNext);

            if (hasNext) {
                logger.debug("Preenchendo dados do veículo...");
                
                veic.setId(rs.getLong("id"));
                logger.debug("ID: " + rs.getLong("id"));
                
                veic.setPlaca(rs.getString("placa"));
                logger.debug("Placa: " + rs.getString("placa"));
                
                veic.setData(rs.getTimestamp("data"));
                logger.debug("Data: " + rs.getTimestamp("data"));
                
                // Continue com todos os setters, adicionando logs...
                veic.setIdLocal(rs.getInt("id_local"));
                veic.setSerieEquipamento(rs.getInt("serie_equipamento"));
                veic.setCodigoEquipamento(rs.getString("codigo_equipamento"));
                veic.setNome(rs.getString("nome"));
                veic.setSentido(rs.getString("sentido"));
                veic.setIdPista(rs.getInt("id_pista"));
                veic.setFaixa(rs.getInt("faixa"));
                
                Double latitude = rs.getDouble("latitude");
                veic.setLatitude(rs.wasNull() ? null : latitude);
                
                Double longitude = rs.getDouble("longitude");
                veic.setLongitude(rs.wasNull() ? null : longitude);
                
                veic.setVelocidade(rs.getInt("velocidade"));
                veic.setClassificacao(rs.getString("classificacao") != null ? rs.getString("classificacao").trim() : "");
                veic.setEnviadoCliente(rs.getBoolean("enviado_cliente"));
                veic.setComImagem(rs.getBoolean("com_imagem"));
                veic.setComPesagem(rs.getBoolean("com_pesagem"));
                veic.setPossuiCoordenadas(rs.getBoolean("possui_coordenadas"));
                veic.setMarca(rs.getString("marca"));
                veic.setModelo(rs.getString("modelo"));
                
                Double comprimento = rs.getDouble("comprimento");
                veic.setComprimento(rs.wasNull() ? null : comprimento);
                
                Double pbt = rs.getDouble("pbt");
                veic.setPbt(rs.wasNull() ? null : pbt);
                
                Double pbtc = rs.getDouble("pbtc");
                veic.setPbtc(rs.wasNull() ? null : pbtc);
                
                veic.setNumeroEixos(rs.getInt("numero_eixos"));
                
                Double e1 = rs.getDouble("E1");
                veic.setE1(rs.wasNull() ? null : e1);
                
                Double e2 = rs.getDouble("E2");
                veic.setE2(rs.wasNull() ? null : e2);
                
                Double e3 = rs.getDouble("E3");
                veic.setE3(rs.wasNull() ? null : e3);
                
                Double e4 = rs.getDouble("E4");
                veic.setE4(rs.wasNull() ? null : e4);
                
                Double e5 = rs.getDouble("E5");
                veic.setE5(rs.wasNull() ? null : e5);
                
                Double e6 = rs.getDouble("E6");
                veic.setE6(rs.wasNull() ? null : e6);
                
                Double e7 = rs.getDouble("E7");
                veic.setE7(rs.wasNull() ? null : e7);
                
                Double e8 = rs.getDouble("E8");
                veic.setE8(rs.wasNull() ? null : e8);
                
                Double e9 = rs.getDouble("E9");
                veic.setE9(rs.wasNull() ? null : e9);
                
                Double distE1E2 = rs.getDouble("distancia_E1E2");
                veic.setDistanciaE1E2(rs.wasNull() ? null : distE1E2);
                
                Double distE2E3 = rs.getDouble("distancia_E2E3");
                veic.setDistanciaE2E3(rs.wasNull() ? null : distE2E3);
                
                Double distE3E4 = rs.getDouble("distancia_E3E4");
                veic.setDistanciaE3E4(rs.wasNull() ? null : distE3E4);
                
                Double distE4E5 = rs.getDouble("distancia_E4E5");
                veic.setDistanciaE4E5(rs.wasNull() ? null : distE4E5);
                
                Double distE5E6 = rs.getDouble("distancia_E5E6");
                veic.setDistanciaE5E6(rs.wasNull() ? null : distE5E6);
                
                Double distE6E7 = rs.getDouble("distancia_E6E7");
                veic.setDistanciaE6E7(rs.wasNull() ? null : distE6E7);
                
                Double distE7E8 = rs.getDouble("distancia_E7E8");
                veic.setDistanciaE7E8(rs.wasNull() ? null : distE7E8);
                
                Double distE8E9 = rs.getDouble("distancia_E8E9");
                veic.setDistanciaE8E9(rs.wasNull() ? null : distE8E9);
                
                veic.setClassificacaoArt96(rs.getString("classificacao_art96"));
                
                logger.debug("Veículo preenchido com sucesso");
            } else {
                logger.debug("Nenhum registro encontrado para o ID: " + id);
            }
        } catch (Exception e) {
            erro = true;
            msgErro = "Erro ao obter veículo de carga!";
            logger.error(msgErro + ": " + e.getMessage(), e);
        } finally {
            try {
                if (conn != null)
                    conn.close();
                if (ps != null)
                    ps.close();
                if (rs != null)
                    rs.close();
            } catch (Exception e) {
                logger.error("Erro gravíssimo ao destruir conexão com banco de dados! " + e.getMessage(), e);
            }

            if (erro)
                throw new SQLException("Erro ao consultar o veículo de carga no banco de dados!");
        }
        return veic;
    }

    // Métodos auxiliares para listas (mantidos da versão original)
    public static List<JsonObject> ListaCores() {
        List<JsonObject> listaCores = new ArrayList<>();
        String sql = "SELECT * FROM cad_cor";

        try (Connection conn = Conexao.getConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JsonObject cor = new JsonObject();
                cor.addProperty("id_cor", rs.getInt("id_cor"));
                cor.addProperty("descricao", rs.getString("descricao"));
                listaCores.add(cor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaCores;
    }

    public static List<JsonObject> ListarUf() {
        List<JsonObject> listaUf = new ArrayList<>();
        String sql = "SELECT uf FROM cad_localidade group by uf";

        try (Connection conn = Conexao.getConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JsonObject uf = new JsonObject();
                uf.addProperty("uf", rs.getString("uf"));
                listaUf.add(uf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaUf;
    }

    public static List<JsonObject> ListarTiposVeiculo() {
        List<JsonObject> listaTipos = new ArrayList<>();
        String sql = "SELECT * FROM cad_tipo";

        try (Connection conn = Conexao.getConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JsonObject tipos = new JsonObject();
                tipos.addProperty("id_tipo", rs.getInt("id_tipo"));
                tipos.addProperty("descricao", rs.getString("descricao"));
                listaTipos.add(tipos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaTipos;
    }

    public static List<JsonObject> ListarLocalidades() {
        List<JsonObject> listaLocalidades = new ArrayList<>();
        String sql = "SELECT * FROM cad_localidade ORDER BY uf, nome";

        try (Connection conn = Conexao.getConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JsonObject localidade = new JsonObject();
                localidade.addProperty("id_localidade", rs.getInt("id_localidade"));
                localidade.addProperty("nome", rs.getString("nome"));
                localidade.addProperty("uf", rs.getString("uf"));
                listaLocalidades.add(localidade);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaLocalidades;
    }

    public static List<JsonObject> ListarMarcas() {
        List<JsonObject> listaMarcas = new ArrayList<>();
        String sql = "SELECT * FROM cad_marca_cet ORDER BY descricao";

        try (Connection conn = Conexao.getConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JsonObject marca = new JsonObject();
                marca.addProperty("id_marca_cet", rs.getInt("id_marca_cet"));
                marca.addProperty("descricao", rs.getString("descricao").trim());
                listaMarcas.add(marca);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaMarcas;
    }

    public static List<JsonObject> ListarModelos() {
        List<JsonObject> listaModelos = new ArrayList<>();
        String sql = "SELECT * FROM cad_marca ORDER BY descricao";

        try (Connection conn = Conexao.getConexao();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JsonObject modelo = new JsonObject();
                modelo.addProperty("id_marca", rs.getInt("id_marca"));
                modelo.addProperty("descricao", rs.getString("descricao").trim());
                listaModelos.add(modelo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaModelos;
    }
}