/**********************************************************************************
  Projeto: Muralha Digital
  Empresa: Consilux Tecnologia
  Autor: Thiago Guislotti
  Data: 29/05/2025
 *********************************************************************************/
package muralha.digital.veiculosCorrelacionados;

import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.concurrent.*;
import java.util.stream.*;
import org.apache.log4j.Logger;
import com.consilux.lib.Conexao;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe responsável por realizar a consulta à procedure de correlação de placas.
 *
 * O resultado da consulta é uma lista genérica de mapas (sem uso de model específico),
 * que será convertida para JSON no Servlet responsável.
 */
@XmlRootElement(name = "VeiculosCorrelacionados")
@XmlAccessorType(XmlAccessType.FIELD)
public class VeiculosCorrelacionados {

    private static final Logger logger = Logger.getLogger(VeiculosCorrelacionados.class);

    /** Janela de tempo em minutos para considerar passagens como correlacionadas. */
    private static final int JANELA_CORRELACAO_MINUTOS = 3;

    /** Janela de tempo em segundos, derivada de {@link #JANELA_CORRELACAO_MINUTOS}. */
    private static final int JANELA_CORRELACAO_SEGUNDOS = JANELA_CORRELACAO_MINUTOS * 60;

    /**
     * Executa a procedure de correlação de placas no banco de dados.
     *
     * @param placa            Placa do veículo
     * @param dataInicio       Data e hora de início
     * @param dataFim          Data e hora de fim
     * @param tempoPermanencia  Tempo de permanência em minutos
    *  @param numMinPassagensCorrelacionadas    Número mínimo de passagens correlacionadas
     * @return Lista de mapas contendo os registros retornados da procedure.
     * @throws SQLException Caso ocorra erro ao acessar o banco de dados.
     */
    public static List<Map<String, Object>> buscarVeiculosCorrelacionados(String placa, Date dataInicio, Date dataFim, int tempoPermanencia, int numMinPassagensCorrelacionadas) throws SQLException {
        List<Map<String, Object>> resultadoDoBanco = new ArrayList<>();

        Connection conn = null;
        CallableStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = Conexao.getConexao();
            stmt = conn.prepareCall("{call muralha.spu_correlacionamento_placas(?, ?, ?, ?, ?, ?)}");
            
            // Parâmetros na ordem correta, conforme a definição da procedure:
            stmt.setString(1, placa);                      // @placa_informada
            stmt.setTimestamp(2, new Timestamp(dataInicio.getTime())); // @data_inicio
            stmt.setTimestamp(3, new Timestamp(dataFim.getTime()));    // @data_final
            stmt.setInt(4, JANELA_CORRELACAO_MINUTOS);      // @tempo_passagem_minutos
            stmt.setInt(5, 1);                             // @considerar_antes_depois
            stmt.setInt(6, numMinPassagensCorrelacionadas);             // @num_min_passagens_correlacionadas
            
            rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                for (int i = 1; i <= colCount; i++) {
                	item.put(meta.getColumnLabel(i), rs.getObject(i));
                }
                resultadoDoBanco.add(item);
            }

        } catch (Exception e) {
            logger.error("Erro ao executar a stored procedure de correlação", e);
            throw new SQLException("Erro ao consultar dados de veículos correlacionados", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                logger.warn("Erro ao fechar recursos de banco de dados", ex);
            }
        }

        // Retorna o resultado bruto do banco. O filtro será feito no Servlet.
        return resultadoDoBanco;
    }

    /**
     * Executa a procedure de correlação para múltiplas placas no banco de dados.
     * Para cada placa, executa a SPU e consolida os resultados, adicionando o campo placa_principal.
     *
     * @param placas           Array de placas dos veículos
     * @param dataInicio       Data e hora de início
     * @param dataFim          Data e hora de fim
     * @param tempoPermanencia  Tempo de permanência em minutos
    *  @param numMinPassagensCorrelacionadas    Número mínimo de passagens correlacionadas
     * @return Mapa com estrutura normalizada {veiculos: [], correlacoes: []}
     * @throws SQLException Caso ocorra erro ao acessar o banco de dados.
     * Retorna estrutura normalizada: {veiculos: [], correlacoes: []}
     */
    public static Map<String, Object> buscarVeiculosCorrelacionadosMultiplos(String[] placas, Date dataInicio, Date dataFim, int tempoPermanencia, int numMinPassagensCorrelacionadas) throws SQLException {
        Map<String, Map<String, Object>> veiculosMap = new LinkedHashMap<>();
        Map<String, List<Map<String, Object>>> correlacoesPorPlaca = new LinkedHashMap<>();

        List<String> placasValidas = Arrays.stream(placas)
            .filter(p -> p != null && !p.trim().isEmpty())
            .collect(Collectors.toList());

        if (placasValidas.size() <= 1) {
            for (String placaPrincipal : placasValidas) {
                consolidarResultadoPlaca(placaPrincipal, veiculosMap, correlacoesPorPlaca,
                    buscarVeiculosCorrelacionados(placaPrincipal, dataInicio, dataFim, tempoPermanencia, numMinPassagensCorrelacionadas));
            }
        } else {
            ExecutorService executor = Executors.newFixedThreadPool(Math.min(placasValidas.size(), 3));
            try {
                List<CompletableFuture<AbstractMap.SimpleEntry<String, List<Map<String, Object>>>>> futures = placasValidas.stream()
                    .map(placaPrincipal -> CompletableFuture.supplyAsync(() -> {
                        try {
                            List<Map<String, Object>> resultado = buscarVeiculosCorrelacionados(
                                placaPrincipal, dataInicio, dataFim, tempoPermanencia, numMinPassagensCorrelacionadas);
                            return new AbstractMap.SimpleEntry<>(placaPrincipal, resultado);
                        } catch (SQLException e) {
                            logger.error("Erro ao buscar correlações para placa: " + placaPrincipal, e);
                            return new AbstractMap.SimpleEntry<>(placaPrincipal, Collections.<Map<String, Object>>emptyList());
                        }
                    }, executor))
                    .collect(Collectors.toList());

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                for (CompletableFuture<AbstractMap.SimpleEntry<String, List<Map<String, Object>>>> future : futures) {
                    AbstractMap.SimpleEntry<String, List<Map<String, Object>>> entry = future.get();
                    consolidarResultadoPlaca(entry.getKey(), veiculosMap, correlacoesPorPlaca, entry.getValue());
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Erro ao aguardar execução paralela de correlações", e);
                throw new SQLException("Erro na execução paralela de correlações", e);
            } finally {
                executor.shutdown();
            }
        }

        Map<String, Object> resultado = new HashMap<>();
        resultado.put("veiculos", new ArrayList<>(veiculosMap.values()));
        resultado.put("correlacoes", correlacoesPorPlaca);
        
        return resultado;
    }

    /**
     * Consolida os resultados de uma placa principal nos mapas de veículos e correlações.
     *
     * @param placaPrincipal      Placa que originou a busca
     * @param veiculosMap         Mapa acumulador de veículos (deduplicado por placa)
     * @param correlacoesPorPlaca Mapa acumulador de correlações agrupadas por placa principal
     * @param resultadoDaPlaca    Registros retornados pela SP para a placa principal
     */
    private static void consolidarResultadoPlaca(String placaPrincipal, Map<String, Map<String, Object>> veiculosMap,
            Map<String, List<Map<String, Object>>> correlacoesPorPlaca, List<Map<String, Object>> resultadoDaPlaca) {
        correlacoesPorPlaca.put(placaPrincipal, new ArrayList<>());

        for (Map<String, Object> registro : resultadoDaPlaca) {
            String placaRegistro = (String) registro.get("placa");
            if (placaRegistro == null) {
                placaRegistro = "null";
            }

            if (!veiculosMap.containsKey(placaRegistro)) {
                Map<String, Object> dadosVeiculo = new HashMap<>();
                dadosVeiculo.put("placa", registro.get("placa"));
                dadosVeiculo.put("tipo_veiculo", registro.get("tipo_veiculo"));
                dadosVeiculo.put("manchas", registro.get("manchas"));
                dadosVeiculo.put("monitorado", registro.get("monitorado"));
                dadosVeiculo.put("alerta", registro.get("alerta"));
                dadosVeiculo.put("boletim", registro.get("boletim"));
                dadosVeiculo.put("antecedentes", registro.get("antecedentes"));
                dadosVeiculo.put("supervisionado", registro.get("supervisionado"));
                dadosVeiculo.put("periodo_predominante", registro.get("periodo_predominante"));
                dadosVeiculo.put("tipos_alerta", registro.get("tipos_alerta"));
                dadosVeiculo.put("tipos_registro_fato", registro.get("tipos_registro_fato"));
                dadosVeiculo.put("sem_ocr_registro_fato", registro.get("sem_ocr_registro_fato"));
                veiculosMap.put(placaRegistro, dadosVeiculo);
            }

            Map<String, Object> correlacao = new HashMap<>();
            correlacao.put("placa", placaRegistro);
            correlacao.put("incidencia", registro.get("incidencia"));
            correlacao.put("passagens", registro.get("passagens"));
            correlacoesPorPlaca.get(placaPrincipal).add(correlacao);
        }
    }
    
    /**
     * Executa a stored procedure de Detalhes do veículo no banco de dados.
     *
     * @param placa            Placa do veículo
     * @param dataInicio       Data e hora de início (formato esperado pelo banco)
     * @param dataFim          Data e hora de fim (formato esperado pelo banco)
     * @return Lista de mapas com os resultados de Detalhes do veículo
     * @throws SQLException    Se ocorrer erro ao acessar o banco
     */
    public static List<Map<String, Object>> buscarDetalhesVeiculo(String placa, Date dataInicio, Date dataFim) throws SQLException {
        List<Map<String, Object>> resultado = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Map<String, Object> jsonFinal = new HashMap<>();

        boolean placaValida = placa != null && !placa.isEmpty() && !placa.equalsIgnoreCase("null");
        if (!placaValida) {
            throw new SQLException("Parâmetro 'placa' inválido para detalhes do veículo.");
        }

        try {
            conn = Conexao.getConexao();

            // ============================
            // INFORMACOES DO VEICULO
            // ============================
            stmt = conn.prepareStatement("SELECT * FROM muralha.fcn_ObterInfoVeiculo(?)");
            stmt.setString(1, placa);
            rs = stmt.executeQuery();
            Map<String, Object> informacoes = new HashMap<>();
            if (rs.next()) {
                informacoes.put("placa", rs.getString("placa"));
                informacoes.put("marca", rs.getString("marca"));
                informacoes.put("modelo", rs.getString("modelo"));
                informacoes.put("cor", rs.getString("cor"));
                informacoes.put("anoFabricacao", rs.getInt("anoFabricacao"));
            }
            rs.close();
            stmt.close();

            // ============================
            // IMAGEM MAIS RECENTE
            // ============================
            try {
                String sqlImagem = 
                    "SELECT TOP 1 IMG.imagem " +
                    "FROM muralha.veiculo_tempo_real VTR " +
                    "INNER JOIN muralha.veiculo_tempo_real_imagem IMG ON IMG.id_veiculo_tempo_real = VTR.id " +
                    "WHERE VTR.placa = ? AND VTR.data BETWEEN ? AND ? " +
                    "ORDER BY VTR.data DESC";
                stmt = conn.prepareStatement(sqlImagem);
                stmt.setString(1, placa);
                stmt.setTimestamp(2, new Timestamp(dataInicio.getTime()));
                stmt.setTimestamp(3, new Timestamp(dataFim.getTime()));
                rs = stmt.executeQuery();
                if (rs.next()) {
                    byte[] imagemBytes = rs.getBytes("imagem");
                    if (imagemBytes != null && imagemBytes.length > 0) {
                        String base64 = java.util.Base64.getEncoder().encodeToString(imagemBytes);
                        informacoes.put("imagem_base64", "data:image/webp;base64," + base64);
                    }
                }
                rs.close();
                stmt.close();
            } catch (Exception imgEx) {
                logger.warn("Erro ao buscar imagem do veículo: " + imgEx.getMessage());
            }

            jsonFinal.put("informacoes", informacoes);

            // ============================
            // PROPRIETARIO
            // ============================
            stmt = conn.prepareStatement("SELECT * FROM muralha.fcn_ObterInfoProprietarioVeiculo(?)");
            stmt.setString(1, placa);
            rs = stmt.executeQuery();
            Map<String, Object> proprietario = new HashMap<>();
            if (rs.next()) {
                proprietario.put("nome", rs.getString("nome"));
                proprietario.put("sobrenome", rs.getString("sobrenome"));
                proprietario.put("cpf", rs.getString("cpf"));
                proprietario.put("dataNascimento", rs.getString("dataNascimento"));
                proprietario.put("endereco", rs.getString("endereco"));
                proprietario.put("telefone", rs.getString("telefone"));
                proprietario.put("email", rs.getString("email"));
            }
            rs.close();
            stmt.close();

            // ============================
            // ANTECEDENTES (por PLACA)
            // ============================
            stmt = conn.prepareStatement("SELECT * FROM muralha.fcn_ObterInfoAntecedentesProprietarioPorPlaca(?) ORDER BY data_ocorrencia");
            stmt.setString(1, placa);
            rs = stmt.executeQuery();
            List<Map<String, Object>> antecedentes = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> ant = new HashMap<>();
                ant.put("TipoCrime", rs.getString("tipo_crime"));
                ant.put("DataOcorrencia", rs.getString("data_ocorrencia"));
                ant.put("LocalOcorrencia", rs.getString("local_ocorrencia"));
                ant.put("Descricao", rs.getString("descricao"));
                ant.put("Sentenca", rs.getString("sentenca"));
                antecedentes.add(ant);
            }
            if (!antecedentes.isEmpty()) {
                proprietario.put("AntecedentesCriminais", antecedentes);
            }
            jsonFinal.put("proprietario", proprietario);
            rs.close();
            stmt.close();
            
            // ============================
            // ALERTAS
            // ============================
            stmt = conn.prepareStatement("SELECT * FROM muralha.fcn_ObterInfoAlerta(?) ORDER BY data");
            stmt.setString(1, placa);
            rs = stmt.executeQuery();
            List<Map<String, Object>> alertas = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> alerta = new HashMap<>();
                alerta.put("data", rs.getTimestamp("data").toLocalDateTime().toString());
                alerta.put("observacao", rs.getString("observacao"));
                alerta.put("origem", rs.getString("origem"));
                alerta.put("tipo", rs.getString("tipo"));
                alerta.put("status", rs.getString("status"));
                alerta.put("alertaId", rs.getString("alertaId"));
                alertas.add(alerta);
            }
            jsonFinal.put("alertas", alertas);
            rs.close();
            stmt.close();
    
            // ============================
            // REGISTRO DE FATO POR PLACA 
            // ============================
            stmt = conn.prepareStatement("SELECT * FROM muralha.fcn_ObterInfoRegistroFatoPorPlaca(?)");
            stmt.setString(1, placa);
            rs = stmt.executeQuery();
            List<Map<String, Object>> todosOsRegistros = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("registroFatoId", rs.getInt("registroFatoId"));
                item.put("temBoletim", rs.getInt("temBoletim"));
                item.put("boletimId", rs.getInt("boletimId"));
                item.put("data", rs.getString("data"));
                item.put("tipoOcorrencia", rs.getString("tipoOcorrencia"));
                item.put("descricao", rs.getString("descricao"));
                item.put("situacaoAtual", rs.getString("situacaoAtual"));
                item.put("statusRegistroFato", rs.getString("statusRegistroFato"));
                todosOsRegistros.add(item);
            }
            jsonFinal.put("boletimOcorrencia", todosOsRegistros);
            rs.close();
            stmt.close();

            // =====================================
            // REGISTRO DE FATO COM ALERTA POR PLACA
            // =====================================
	        stmt = conn.prepareStatement("SELECT * FROM muralha.fcn_ObterInfoBoletimOcorrenciaAlertaPorPlaca(?)");
	        stmt.setString(1, placa);
	        rs = stmt.executeQuery();
	        Map<String, List<Map<String, Object>>> boletinsPorAlerta = new HashMap<>();
	        while (rs.next()) {
	            String alertaId = rs.getString("alertaId");
	            Map<String, Object> boletim = new HashMap<>();
	            boletim.put("registroFatoId", rs.getInt("registroFatoId"));
	            boletim.put("boletimId", rs.getInt("boletimId"));
	            boletim.put("data", rs.getString("data"));
	            boletim.put("tipoOcorrencia", rs.getString("tipoOcorrencia"));
	            boletim.put("descricao", rs.getString("descricao"));
	            boletim.put("situacaoAtual", rs.getString("situacaoAtual"));
	            boletinsPorAlerta.computeIfAbsent(alertaId, k -> new ArrayList<>()).add(boletim);
	        }
	        rs.close();
	        stmt.close();

	        // Atualiza os alertas com seus boletins (se houver)
	        for (Map<String, Object> alerta : alertas) {
		        String alertaId = (String) alerta.get("alertaId");
		        List<Map<String, Object>> boletins = boletinsPorAlerta.get(alertaId);
		        if (boletins != null) {
		            alerta.put("boletimOcorrencia", boletins);
		        }
	        }
	        
	        resultado.add(jsonFinal);
	        
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("Erro ao consultar detalhes do veículo", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
      
        return resultado;
    }

    /**
     * Busca as passagens de um veículo com paginação.
     * Quando placasAlvo é informado, marca cada passagem que coincide com uma passagem
     * de placa alvo no mesmo local dentro de uma janela de 3 minutos.
     * Quando apenasCorrelacionadas é true, retorna apenas passagens correlacionadas.
     *
     * @param placa                  Placa do veículo
     * @param dataInicio             Data e hora de início
     * @param dataFim                Data e hora de fim
     * @param pagina                 Número da página (1-indexed)
     * @param tamanhoPagina          Quantidade de registros por página
     * @param placasAlvo             Placas alvo para verificação de correlação (pode ser null)
     * @param apenasCorrelacionadas  Quando true, filtra apenas passagens correlacionadas
     * @return Mapa contendo lista de passagens, total de registros e informações de paginação.
     * @throws SQLException Se ocorrer erro ao acessar o banco
     */
    public static Map<String, Object> buscarPassagensVeiculo(String placa, Date dataInicio, Date dataFim, int pagina, int tamanhoPagina, String[] placasAlvo, boolean apenasCorrelacionadas) throws SQLException {
        Map<String, Object> resultado = new HashMap<>();
        List<Map<String, Object>> passagens = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        int offset = (pagina - 1) * tamanhoPagina;
        int totalRegistros = 0;

        boolean semPlaca = (placa == null || placa.trim().isEmpty());
        String filtroPlaca = semPlaca ? "(VTR.placa IS NULL OR VTR.placa = '')" : "VTR.placa = ?";

        boolean verificarCorrelacao = placasAlvo != null && placasAlvo.length > 0;
        String placeholders = "";
        if (verificarCorrelacao) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < placasAlvo.length; i++) {
                if (i > 0) {
                    sb.append(",");
                }
                sb.append("?");
            }
            placeholders = sb.toString();
        }

        String filtroCorrelacao = "";
        if (apenasCorrelacionadas && verificarCorrelacao) {
            filtroCorrelacao =
                "AND EXISTS (" +
                    "SELECT 1 FROM muralha.veiculo_tempo_real VALVO " +
                    "WHERE VALVO.placa IN (" + placeholders + ") " +
                    "AND VALVO.id_local = VTR.id_local " +
                    "AND VALVO.data BETWEEN DATEADD(SECOND, -" + JANELA_CORRELACAO_SEGUNDOS + ", VTR.data) AND DATEADD(SECOND, " + JANELA_CORRELACAO_SEGUNDOS + ", VTR.data)" +
                ") ";
        }

        try {
            conn = Conexao.getConexao();

            String sqlCount =
                "SELECT COUNT(DISTINCT VTR.id) AS total " +
                "FROM muralha.veiculo_tempo_real VTR " +
                "WHERE " + filtroPlaca + " AND VTR.data BETWEEN ? AND ? " +
                filtroCorrelacao;
            stmt = conn.prepareStatement(sqlCount);
            int countParamIdx = 1;
            if (!semPlaca) {
                stmt.setString(countParamIdx++, placa);
            }
            stmt.setTimestamp(countParamIdx++, new Timestamp(dataInicio.getTime()));
            stmt.setTimestamp(countParamIdx++, new Timestamp(dataFim.getTime()));
            if (apenasCorrelacionadas && verificarCorrelacao) {
                for (String pa : placasAlvo) {
                    stmt.setString(countParamIdx++, pa.trim());
                }
            }
            rs = stmt.executeQuery();
            if (rs.next()) {
                totalRegistros = rs.getInt("total");
            }
            rs.close();
            stmt.close();

            String sqlPassagens =
                "SELECT VTR.id AS passagem_id, VTR.data AS data_hora, VTR.id_local, L.nome AS nome_local " +
                "FROM muralha.veiculo_tempo_real VTR " +
                "OUTER APPLY (SELECT TOP 1 nome FROM dbo.local WHERE id_local = VTR.id_local) L " +
                "WHERE " + filtroPlaca + " AND VTR.data BETWEEN ? AND ? " +
                filtroCorrelacao +
                "ORDER BY VTR.data DESC " +
                "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
            stmt = conn.prepareStatement(sqlPassagens);

            int paramIndex = 1;
            if (!semPlaca) {
                stmt.setString(paramIndex++, placa);
            }
            stmt.setTimestamp(paramIndex++, new Timestamp(dataInicio.getTime()));
            stmt.setTimestamp(paramIndex++, new Timestamp(dataFim.getTime()));
            if (apenasCorrelacionadas && verificarCorrelacao) {
                for (String pa : placasAlvo) {
                    stmt.setString(paramIndex++, pa.trim());
                }
            }
            stmt.setInt(paramIndex++, offset);
            stmt.setInt(paramIndex++, tamanhoPagina);
            rs = stmt.executeQuery();

            List<String> idsPassagens = new ArrayList<>();
            while (rs.next()) {
                Map<String, Object> pass = new HashMap<>();
                String passagemId = rs.getString("passagem_id");
                pass.put("passagemId", passagemId);
                pass.put("dataHora", rs.getTimestamp("data_hora") != null ? rs.getTimestamp("data_hora").toLocalDateTime().toString() : null);
                pass.put("idLocal", rs.getInt("id_local"));
                pass.put("nomeLocal", rs.getString("nome_local") != null ? rs.getString("nome_local").trim() : null);
                passagens.add(pass);
                idsPassagens.add(passagemId);
            }
            rs.close();
            stmt.close();

            if (verificarCorrelacao && !idsPassagens.isEmpty()) {
                StringBuilder idPlaceholders = new StringBuilder();
                for (int i = 0; i < idsPassagens.size(); i++) {
                    if (i > 0) {
                        idPlaceholders.append(",");
                    }
                    idPlaceholders.append("CAST(? AS UNIQUEIDENTIFIER)");
                }

                String sqlCorrelacao =
                    "SELECT VTR.id AS passagem_id, " +
                        "STUFF((" +
                            "SELECT DISTINCT ',' + VALVO.placa " +
                            "FROM muralha.veiculo_tempo_real VALVO " +
                            "WHERE VALVO.placa IN (" + placeholders + ") " +
                            "AND VALVO.id_local = VTR.id_local " +
                            "AND VALVO.data BETWEEN DATEADD(SECOND, -" + JANELA_CORRELACAO_SEGUNDOS + ", VTR.data) AND DATEADD(SECOND, " + JANELA_CORRELACAO_SEGUNDOS + ", VTR.data) " +
                            "FOR XML PATH('')" +
                        "), 1, 1, '') AS placas_correlacionadas " +
                    "FROM muralha.veiculo_tempo_real VTR " +
                    "WHERE VTR.id IN (" + idPlaceholders.toString() + ")";
                stmt = conn.prepareStatement(sqlCorrelacao);

                int corrParamIdx = 1;
                for (String pa : placasAlvo) {
                    stmt.setString(corrParamIdx++, pa.trim());
                }
                for (String id : idsPassagens) {
                    stmt.setString(corrParamIdx++, id);
                }
                rs = stmt.executeQuery();

                Map<String, String> correlacaoMap = new HashMap<>();
                while (rs.next()) {
                    correlacaoMap.put(rs.getString("passagem_id"), rs.getString("placas_correlacionadas"));
                }
                rs.close();
                stmt.close();

                for (Map<String, Object> pass : passagens) {
                    String placasCorrelacionadas = correlacaoMap.get(pass.get("passagemId"));
                    pass.put("correlacionada", placasCorrelacionadas != null);
                    pass.put("placasCorrelacionadas", placasCorrelacionadas != null ? placasCorrelacionadas.trim() : null);
                }
            }

        } catch (Exception e) {
            logger.error("Erro ao buscar passagens do veículo", e);
            throw new SQLException("Erro ao consultar passagens do veículo", e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                logger.warn("Erro ao fechar recursos de banco de dados", ex);
            }
        }

        int totalPaginas = (int) Math.ceil((double) totalRegistros / tamanhoPagina);

        resultado.put("passagens", passagens);
        resultado.put("paginaAtual", pagina);
        resultado.put("tamanhoPagina", tamanhoPagina);
        resultado.put("totalRegistros", totalRegistros);
        resultado.put("totalPaginas", totalPaginas);

        return resultado;
    }
}