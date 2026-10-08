package muralha.digital.blitz;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Enumeration;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONArray;
import org.json.JSONObject;

import com.consilux.model.Acesso;
import com.consilux.conf.ConfiguracaoProvider;

import muralha.digital.acessos.Usuario;
import muralha.digital.guarnicao.Guarnicao;
import muralha.digital.veiculo.Veiculos;
import muralha.digital._ini.Inicializacao;

@WebServlet("/MuralhaDigital/Blitz")
public class BlitzServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(BlitzServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!new Acesso(request, response, true).verificaAcesso(false)) {
            enviarRespostaJSON(response, false, "Usuário não autenticado!");
            return;
        }

        final Acesso acessoUsuario = new Acesso(request, response, true);
        if (!acessoUsuario.verificaAcesso()) {
            enviarRespostaJSON(response, false, "Usuário não autenticado!");
            return;
        }
        
        try {
            String msg = null;
            String strAcao = request.getParameter("acao");
            
            if (strAcao == null || strAcao == "") {
                msg = "Ação não informada!";
                logger.error(msg);    
                enviarRespostaJSON(response, false, msg);
                return;
            }
            
            else if (strAcao.equals("listar"))
                listarBlitz(response);
            else if (strAcao.equals("listarAtivas"))
                listarBlitzAtivas(response);
            else if (strAcao.equals("listarBlitzAtivasAutomaticas"))
                listarBlitzAtivasAutomaticas(response);
            else if (strAcao.equals("listarLocais"))
                listarLocais(response);
            else if (strAcao.equals("listarUsuariosAG"))
                listarUsuariosAG(response);
            else if (strAcao.equals("listarGuarnicoes"))
                listarGuarnicoes(response);
            else if (strAcao.equals("obterBlitz"))
                obterBlitz(request, response);
            else if (strAcao.equals("listarLocaisBlitz"))
                listarLocaisBlitz(request, response);
            else if (strAcao.equals("listarUsuariosBlitz"))
                listarUsuariosBlitz(request, response);
            else if (strAcao.equals("listarGuarnicoesBlitz"))
                listarGuarnicoesBlitz(request, response);
            else if (strAcao.equals("verificarTipoAssociacao"))
                verificarTipoAssociacao(request, response);
            else if (strAcao.equals("listarTiposAlerta"))
                listarTiposAlerta(response);
            else if (strAcao.equals("listarTiposAlertaBlitz"))
                listarTiposAlertaBlitz(request, response);
            else if (strAcao.equals("listarTiposEnvolvimento"))
                listarTiposEnvolvimento(response);
            else if (strAcao.equals("listarSituacoesDocumento"))
                listarSituacoesDocumento(response);
            else if (strAcao.equals("listarTiposDocumento"))
                listarTiposDocumento(response);
            else if (strAcao.equals("obterAbordagem"))
                obterAbordagem(request, response);
            else if (strAcao.equals("listarAbordagensPorBlitz"))
                listarAbordagensPorBlitz(request, response);
            else if (strAcao.equals("listarPessoasAbordagem"))
                listarPessoasAbordagem(request, response);
            else if (strAcao.equals("listarDocumentosAbordagem"))
                listarDocumentosAbordagem(request, response);
            else if (strAcao.equals("listarImagensAbordagem"))
                listarImagensAbordagem(request, response);
            else if (strAcao.equals("listarAbordagensFiltro"))
                listarAbordagensFiltro(request, response);
            else if (strAcao.equals("obterAbordagemDetalhada"))
                obterAbordagemDetalhada(request, response);
            else if (strAcao.equals("obterImagemDocumento"))
                obterImagemDocumento(request, response);
            else if (strAcao.equals("obterImagemAbordagem"))
                obterImagemAbordagem(request, response);
            else if (strAcao.equals("downloadDocumento"))
                downloadDocumento(request, response);
            else if (strAcao.equals("listarRegistrosFato"))
                listarRegistrosFato(response);
            else if (strAcao.equals("obterDetalhesRegistroFato"))
                obterDetalhesRegistroFato(request, response);
            else if (strAcao.equals("listarBlitzAtivasPorLocal"))
                listarBlitzAtivasPorLocal(request, response);
            else if (strAcao.equals("obterInformacoesAlerta"))
                obterInformacoesAlerta(request, response);
            else if (strAcao.equals("verificarAbordagemPorAlerta"))
                verificarAbordagemPorAlerta(request, response);
            else if (strAcao.equals("verificarAlertaBlitz"))
                verificarAlertaBlitz(request, response);
            else if (strAcao.equals("obterInformacoesBlitzOstensiva"))
                obterInformacoesBlitzOstensiva(request, response);
            else if (strAcao.equals("buscarHistoricoPorCpf"))
                buscarHistoricoPorCpf(request, response);
            else if (strAcao.equals("buscarHistoricoPorPlaca"))
                buscarHistoricoPorPlaca(request, response);
            else if (strAcao.equals("listarTiposBlitz"))
                listarTiposBlitz(response);
            else if (strAcao.equals("listarResultadosAbordagem")) 
                listarResultadosAbordagem(request, response);
            else if (strAcao.equals("obterInformacoesVeiculoPorPlaca")) 
                obterInformacoesVeiculoPorPlaca(request, response);
            else if (strAcao.equals("listarIdsLocaisBlitz"))
                listarIdsLocaisBlitz(request, response);
            else if (strAcao.equals("geocodingMapa")) 
                geocodingMapa(request, response);
            else if (strAcao.equals("obterPassagensReaisBlitz"))
                obterPassagensReaisBlitz(request, response);
            else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida.");
            }
        } catch (Exception e) {
            logger.error("Erro no doGet da BlitzServlet", e);
            enviarRespostaJSON(response, false, "Erro interno no servidor.");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!new Acesso(request, response, true).verificaAcesso(false)) {
            enviarRespostaJSON(response, false, "Usuário não autenticado!");
            return;
        }

        Integer idUsuario = null;
        final Acesso acessoUsuario = new Acesso(request, response, true);
        if (!acessoUsuario.verificaAcesso()) {
            enviarRespostaJSON(response, false, "Usuário não autenticado!");
            return;
        } else {
            idUsuario = acessoUsuario.getUsuario().getId();
        }

        try {
            String msg = null;
            String strAcao = null;
            Map<String, String> parametros = new HashMap<>();
            List<FileItem> arquivos = new ArrayList<>();
            
            boolean isMultipart = ServletFileUpload.isMultipartContent(request);
            
            if (isMultipart) {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                List<FileItem> items = upload.parseRequest(request);
                
                for (FileItem item : items) {
                    if (item.isFormField()) {
                        parametros.put(item.getFieldName(), item.getString("UTF-8"));
                        if ("acao".equals(item.getFieldName())) {
                            strAcao = item.getString("UTF-8");
                        }
                    } else {
                        arquivos.add(item);
                    }
                }
            } else {
                strAcao = request.getParameter("acao");
                Enumeration<String> paramNames = request.getParameterNames();
                while (paramNames.hasMoreElements()) {
                    String paramName = paramNames.nextElement();
                    parametros.put(paramName, request.getParameter(paramName));
                }
            }
            
            if (strAcao == null || strAcao.isEmpty()) {
                msg = "Ação não informada!";
                logger.error(msg);    
                enviarRespostaJSON(response, false, msg);
                return;
            }
            
            if ("cadastrar".equalsIgnoreCase(strAcao))
                cadastrarBlitz(request, response, idUsuario, parametros);
            else if ("atualizar".equalsIgnoreCase(strAcao))
                atualizarBlitz(request, response, idUsuario, parametros);
            else if ("excluirBlitz".equalsIgnoreCase(strAcao))
                excluirBlitz(request, response);
            else if ("salvarAbordagem".equalsIgnoreCase(strAcao))
                salvarAbordagem(request, response, idUsuario, parametros, arquivos, isMultipart);
            else if ("encerrarBlitz".equalsIgnoreCase(strAcao))
                encerrarBlitz(request, response);
            else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação POST inválida.");
            }
        } catch (Exception e) {
            logger.error("Erro no doPost da BlitzServlet", e);
            enviarRespostaJSON(response, false, "Erro interno no servidor.");
        }
    }

    private void listarTiposEnvolvimento(HttpServletResponse response) throws IOException {
        try {
            List<String> tipos = Blitzes.listarTiposEnvolvimento();
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray tiposArray = new JSONArray();
            
            for (String tipo : tipos) {
                tiposArray.put(tipo);
            }
            
            jsonResponse.put("tiposEnvolvimento", tiposArray);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar tipos de envolvimento!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarSituacoesDocumento(HttpServletResponse response) throws IOException {
        try {
            List<String> situacoes = Blitzes.listarSituacoesDocumento();
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray situacoesArray = new JSONArray();
            
            for (String situacao : situacoes) {
                situacoesArray.put(situacao);
            }
            
            jsonResponse.put("situacoesDocumento", situacoesArray);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar situações de documento!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarTiposDocumento(HttpServletResponse response) throws IOException {
        try {
            List<String> tipos = Blitzes.listarTiposDocumento();
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray tiposArray = new JSONArray();
            
            for (String tipo : tipos) {
                tiposArray.put(tipo);
            }
            
            jsonResponse.put("tiposDocumento", tiposArray);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar tipos de documento!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void obterAbordagem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idAbordagemStr = request.getParameter("idAbordagem");
            
            if (idAbordagemStr == null || idAbordagemStr.isEmpty()) {
                enviarRespostaJSON(response, false, "ID da abordagem não informado!");
                return;
            }
            
            Long idAbordagem = Long.parseLong(idAbordagemStr);
            BlitzAbordagem abordagem = Blitzes.obterAbordagemPorId(idAbordagem);
            
            if (abordagem != null) {
                JSONObject jsonResponse = new JSONObject();
                JSONObject abordagemJson = new JSONObject();
                
                abordagemJson.put("id", abordagem.getId());
                abordagemJson.put("id_blitz_digital", abordagem.getId_blitz_digital());
                abordagemJson.put("id_agente", abordagem.getId_agente());
                abordagemJson.put("id_alerta", abordagem.getId_alerta());
                abordagemJson.put("id_local", abordagem.getId_local());
                abordagemJson.put("placa_veiculo", abordagem.getPlaca_veiculo());
                abordagemJson.put("data_abordagem", abordagem.getData_abordagem() != null ? abordagem.getData_abordagem().toString() : "");
                abordagemJson.put("latitude", abordagem.getLatitude());
                abordagemJson.put("longitude", abordagem.getLongitude());
                abordagemJson.put("status", abordagem.getStatus());
                abordagemJson.put("motivo_cancelamento", abordagem.getMotivo_cancelamento());
                abordagemJson.put("observacoes", abordagem.getObservacoes());
                abordagemJson.put("id_registro_fato", abordagem.getId_registro_fato());
                abordagemJson.put("data_criacao", abordagem.getData_criacao() != null ? abordagem.getData_criacao().toString() : "");
                
                jsonResponse.put("abordagem", abordagemJson);
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
                response.getWriter().flush();
            } else {
                enviarRespostaJSON(response, false, "Abordagem não encontrada!");
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao obter abordagem!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarAbordagensPorBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idBlitzStr = request.getParameter("idBlitz");
            
            if (idBlitzStr == null || idBlitzStr.isEmpty()) {
                enviarRespostaJSON(response, false, "ID da blitz não informado!");
                return;
            }
            
            Integer idBlitz = Integer.parseInt(idBlitzStr);
            List<BlitzAbordagem> abordagens = Blitzes.listarAbordagensPorBlitz(idBlitz);
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray abordagensArray = new JSONArray();
            
            for (BlitzAbordagem abordagem : abordagens) {
                JSONObject abordagemJson = new JSONObject();
                
                abordagemJson.put("id", abordagem.getId());
                abordagemJson.put("id_blitz_digital", abordagem.getId_blitz_digital());
                abordagemJson.put("id_agente", abordagem.getId_agente());
                abordagemJson.put("id_alerta", abordagem.getId_alerta());
                abordagemJson.put("id_local", abordagem.getId_local());
                abordagemJson.put("placa_veiculo", abordagem.getPlaca_veiculo());
                abordagemJson.put("data_abordagem", abordagem.getData_abordagem() != null ? abordagem.getData_abordagem().toString() : "");
                abordagemJson.put("latitude", abordagem.getLatitude());
                abordagemJson.put("longitude", abordagem.getLongitude());
                abordagemJson.put("status", abordagem.getStatus());
                abordagemJson.put("motivo_cancelamento", abordagem.getMotivo_cancelamento());
                abordagemJson.put("observacoes", abordagem.getObservacoes());
                abordagemJson.put("id_registro_fato", abordagem.getId_registro_fato());
                abordagemJson.put("data_criacao", abordagem.getData_criacao() != null ? abordagem.getData_criacao().toString() : "");
                
                abordagensArray.put(abordagemJson);
            }
            
            jsonResponse.put("abordagens", abordagensArray);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar abordagens por blitz!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarPessoasAbordagem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idAbordagemStr = request.getParameter("idAbordagem");
            
            if (idAbordagemStr == null || idAbordagemStr.isEmpty()) {
                enviarRespostaJSON(response, false, "ID da abordagem não informado!");
                return;
            }
            
            Long idAbordagem = Long.parseLong(idAbordagemStr);
            List<BlitzPessoaEnvolvida> pessoas = Blitzes.listarPessoasPorAbordagem(idAbordagem);
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray pessoasArray = new JSONArray();
            
            for (BlitzPessoaEnvolvida pessoa : pessoas) {
                JSONObject pessoaJson = new JSONObject();
                
                pessoaJson.put("id", pessoa.getId());
                pessoaJson.put("id_abordagem", pessoa.getId_abordagem());
                pessoaJson.put("cpf", pessoa.getCpf());
                pessoaJson.put("nome_completo", pessoa.getNome_completo());
                pessoaJson.put("data_nascimento", pessoa.getData_nascimento() != null ? pessoa.getData_nascimento().toString() : "");
                pessoaJson.put("tipo_envolvimento", pessoa.getTipo_envolvimento());
                pessoaJson.put("telefone", pessoa.getTelefone());
                pessoaJson.put("sexo", pessoa.getSexo() != null ? pessoa.getSexo().toString() : "");
                pessoaJson.put("email", pessoa.getEmail());
                pessoaJson.put("observacoes", pessoa.getObservacoes());
                pessoaJson.put("data_criacao", pessoa.getData_criacao() != null ? pessoa.getData_criacao().toString() : "");
                
                pessoasArray.put(pessoaJson);
            }
            
            jsonResponse.put("pessoas", pessoasArray);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar pessoas da abordagem!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarDocumentosAbordagem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idAbordagemStr = request.getParameter("idAbordagem");
            
            if (idAbordagemStr == null || idAbordagemStr.isEmpty()) {
                enviarRespostaJSON(response, false, "ID da abordagem não informado!");
                return;
            }
            
            Long idAbordagem = Long.parseLong(idAbordagemStr);
            List<BlitzDocumento> documentos = Blitzes.listarDocumentosPorAbordagem(idAbordagem);
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray documentosArray = new JSONArray();
            
            for (BlitzDocumento documento : documentos) {
                JSONObject documentoJson = new JSONObject();
                
                documentoJson.put("id", documento.getId());
                documentoJson.put("id_abordagem", documento.getId_abordagem());
                documentoJson.put("id_pessoa", documento.getId_pessoa());
                documentoJson.put("tipo_documento", documento.getTipo_documento());
                documentoJson.put("numero_documento", documento.getNumero_documento());
                documentoJson.put("nome_titular", documento.getNome_titular());
                documentoJson.put("validade", documento.getValidade() != null ? documento.getValidade().toString() : "");
                documentoJson.put("situacao", documento.getSituacao());
                documentoJson.put("observacoes", documento.getObservacoes());
                documentoJson.put("data_criacao", documento.getData_criacao() != null ? documento.getData_criacao().toString() : "");
                
                JSONArray arquivosArray = new JSONArray();
                if (documento.getArquivos() != null && !documento.getArquivos().isEmpty()) {
                    for (BlitzDocumentoArquivo arquivo : documento.getArquivos()) {
                        JSONObject arquivoJson = new JSONObject();
                        arquivoJson.put("id", arquivo.getId());
                        arquivoJson.put("id_documento", arquivo.getId_documento());
                        arquivoJson.put("caminho_arquivo", arquivo.getCaminho_arquivo());
                        arquivoJson.put("nome_arquivo_original", arquivo.getNome_arquivo_original());
                        arquivoJson.put("tipo_arquivo", arquivo.getTipo_arquivo());
                        arquivoJson.put("data_criacao", arquivo.getData_criacao() != null ? arquivo.getData_criacao().toString() : "");
                        arquivosArray.put(arquivoJson);
                    }
                }
                documentoJson.put("arquivos", arquivosArray);
                
                documentosArray.put(documentoJson);
            }
            
            jsonResponse.put("documentos", documentosArray);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar documentos da abordagem!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void salvarAbordagem(HttpServletRequest request, HttpServletResponse response, 
        Integer idUsuario, Map<String, String> parametros, 
        List<FileItem> arquivos, boolean isMultipart
    ) throws IOException {
        try {
            String idBlitzDigitalStr = parametros.get("id_blitz_digital");
            String idAlerta = parametros.get("id_alerta");
            String idLocalStr = parametros.get("id_local");
            String placaVeiculo = parametros.get("placa_veiculo");
            String dataAbordagemStr = parametros.get("data_abordagem");
            String latitudeStr = parametros.get("latitude");
            String longitudeStr = parametros.get("longitude");
            String status = parametros.get("status");
            String motivoCancelamento = parametros.get("motivo_cancelamento");
            String observacoes = parametros.get("observacoes");
            String idRegistroFatoStr = parametros.get("id_registro_fato");
            String pessoasJsonStr = parametros.get("pessoas");
            String documentosJsonStr = parametros.get("documentos");
            String origemAbordagem = parametros.get("origem_abordagem"); 
            String idVeiculoTempoReal = parametros.get("id_veiculo_tempo_real"); 
            String idResultadoStr = parametros.get("id_resultado");
            String marcaVeiculo = parametros.get("marca_veiculo");
            String modeloVeiculo = parametros.get("modelo_veiculo");
            String tipoVeiculo = parametros.get("tipo_veiculo");
            String corVeiculo = parametros.get("cor_veiculo");
            
            if (idBlitzDigitalStr == null || idBlitzDigitalStr.isEmpty() || status == null || status.isEmpty()) {
                enviarRespostaJSON(response, false, "Parâmetros obrigatórios não informados!");
                return;
            }
            
            BlitzAbordagem abordagem = new BlitzAbordagem();
            abordagem.setId_blitz_digital(Integer.parseInt(idBlitzDigitalStr));
            abordagem.setId_agente(idUsuario);
            
            if (idAlerta != null && !idAlerta.isEmpty()) {
                abordagem.setId_alerta(idAlerta);
            }
            
            if (idLocalStr != null && !idLocalStr.isEmpty()) {
                abordagem.setId_local(Integer.parseInt(idLocalStr));
            }
            
            if (origemAbordagem != null && !origemAbordagem.isEmpty()) {
                abordagem.setOrigemAbordagem(origemAbordagem);
            }
            
            if (idVeiculoTempoReal != null && !idVeiculoTempoReal.isEmpty() && !"null".equals(idVeiculoTempoReal)) {
                abordagem.setId_veiculo_tempo_real(idVeiculoTempoReal);
            } else {
                abordagem.setId_veiculo_tempo_real(null);
            }
            
            if (placaVeiculo != null && !placaVeiculo.isEmpty()) {
                abordagem.setPlaca_veiculo(placaVeiculo);
            } else {
                abordagem.setPlaca_veiculo(null);
            }
            
            if (dataAbordagemStr != null && !dataAbordagemStr.isEmpty()) {
                abordagem.setData_abordagem(java.sql.Timestamp.valueOf(dataAbordagemStr.replace("T", " ") + ":00"));
            } else {
                abordagem.setData_abordagem(new java.sql.Timestamp(System.currentTimeMillis()));
            }

            if (idResultadoStr != null && !idResultadoStr.isEmpty()) {
                abordagem.setId_resultado(Integer.parseInt(idResultadoStr));
            }
            
            if (latitudeStr != null && !latitudeStr.isEmpty()) {
                abordagem.setLatitude(Double.parseDouble(latitudeStr));
            }
            
            if (longitudeStr != null && !longitudeStr.isEmpty()) {
                abordagem.setLongitude(Double.parseDouble(longitudeStr));
            }
            
            abordagem.setStatus(status);
            abordagem.setMotivo_cancelamento(motivoCancelamento);
            abordagem.setObservacoes(observacoes);
            abordagem.setCor_veiculo(corVeiculo);
            abordagem.setMarca_veiculo(marcaVeiculo);
            abordagem.setModelo_veiculo(modeloVeiculo);
            abordagem.setTipo_veiculo(tipoVeiculo);
            
            if (idRegistroFatoStr != null && !idRegistroFatoStr.isEmpty()) {
                abordagem.setId_registro_fato(Long.parseLong(idRegistroFatoStr));
            }
            
            List<BlitzPessoaEnvolvida> pessoas = new ArrayList<>();
            if (pessoasJsonStr != null && !pessoasJsonStr.isEmpty()) {
                JSONArray pessoasJson = new JSONArray(pessoasJsonStr);
                for (int i = 0; i < pessoasJson.length(); i++) {
                    JSONObject pessoaJson = pessoasJson.getJSONObject(i);
                    BlitzPessoaEnvolvida pessoa = new BlitzPessoaEnvolvida();
                    
                    pessoa.setCpf(pessoaJson.optString("cpf", null));
                    pessoa.setNome_completo(pessoaJson.optString("nome_completo", null));
                    
                    String dataNascimentoStr = pessoaJson.optString("data_nascimento", null);
                    if (dataNascimentoStr != null && !dataNascimentoStr.isEmpty()) {
                        pessoa.setData_nascimento(java.sql.Date.valueOf(dataNascimentoStr));
                    }
                    
                    pessoa.setTipo_envolvimento(pessoaJson.optString("tipo_envolvimento", null));
                    pessoa.setTelefone(pessoaJson.optString("telefone", null));
                    
                    String sexoStr = pessoaJson.optString("sexo", null);
                    if (sexoStr != null && !sexoStr.isEmpty()) {
                        pessoa.setSexo(sexoStr.charAt(0));
                    }
                    
                    pessoa.setEmail(pessoaJson.optString("email", null));
                    pessoa.setObservacoes(pessoaJson.optString("observacoes", null));
                    
                    if (pessoaJson.has("id_temp")) {
                        pessoa.setId(pessoaJson.getLong("id_temp"));
                    }
                    
                    pessoas.add(pessoa);
                }
            }
            
            List<BlitzDocumento> documentos = new ArrayList<>();
            if (documentosJsonStr != null && !documentosJsonStr.isEmpty()) {
                JSONArray documentosJson = new JSONArray(documentosJsonStr);
                for (int i = 0; i < documentosJson.length(); i++) {
                    JSONObject documentoJson = documentosJson.getJSONObject(i);
                    BlitzDocumento documento = new BlitzDocumento();
                    
                    documento.setId_pessoa(documentoJson.optLong("id_pessoa", 0) > 0 ? documentoJson.getLong("id_pessoa") : null);
                    documento.setTipo_documento(documentoJson.optString("tipo_documento", null));
                    documento.setNumero_documento(documentoJson.optString("numero_documento", null));
                    documento.setNome_titular(documentoJson.optString("nome_titular", null));
                    
                    String validadeStr = documentoJson.optString("validade", null);
                    if (validadeStr != null && !validadeStr.isEmpty()) {
                        documento.setValidade(java.sql.Date.valueOf(validadeStr));
                    }
                    
                    documento.setSituacao(documentoJson.optString("situacao", null));
                    documento.setObservacoes(documentoJson.optString("observacoes", null));
                    
                    documentos.add(documento);
                }
            }

            List<BlitzImagemAbordagem> imagensAbordagem = new ArrayList<>();

            if (isMultipart && arquivos != null && !arquivos.isEmpty()) {
                for (FileItem arquivo : arquivos) {
                    if (arquivo.isFormField() || arquivo.getSize() == 0) {
                        continue;
                    }

                    String fieldName = arquivo.getFieldName();

                    if (fieldName.startsWith("arquivosDocumento_")) {
                        String nomeOriginal = Paths.get(arquivo.getName()).getFileName().toString();
                        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
                        String nomeUnico = "doc_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + extensao;
                        String caminhoCompleto = Inicializacao.BlitzDigitalDiretorioDocumentos + File.separator + nomeUnico;

                        File destino = new File(caminhoCompleto);
                        arquivo.write(destino);

                        int indiceDoc = Integer.parseInt(fieldName.split("_")[1]);

                        if (indiceDoc >= 0 && indiceDoc < documentos.size()) {
                            BlitzDocumento doc = documentos.get(indiceDoc);

                            BlitzDocumentoArquivo arquivoDoc = new BlitzDocumentoArquivo();
                            arquivoDoc.setCaminho_arquivo(caminhoCompleto);
                            arquivoDoc.setNome_arquivo_original(nomeOriginal);
                            arquivoDoc.setTipo_arquivo(arquivo.getContentType());

                            doc.addArquivo(arquivoDoc);
                        }
                    } else if ("imagensAbordagem".equals(fieldName)) {
                        String nomeOriginal = Paths.get(arquivo.getName()).getFileName().toString();
                        String extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
                        String nomeUnico = "img_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + extensao;
                        String caminhoCompleto = Inicializacao.BlitzDigitalDiretorioImagens + File.separator + nomeUnico;

                        File destino = new File(caminhoCompleto);
                        arquivo.write(destino);

                        BlitzImagemAbordagem imagem = new BlitzImagemAbordagem();
                        imagem.setNome_arquivo_original(nomeOriginal);
                        imagem.setCaminho_arquivo(caminhoCompleto);
                        imagem.setTipo_arquivo(arquivo.getContentType());
                        imagem.setId_usuario(idUsuario);

                        imagensAbordagem.add(imagem);
                    }
                }
            }

            Long idAbordagemGerado = Blitzes.salvarAbordagemCompleta(abordagem, pessoas, documentos, imagensAbordagem);
            
            if (idAbordagemGerado != null) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("sucesso", true);
                jsonResponse.put("mensagem", "Abordagem registrada com sucesso!");
                jsonResponse.put("id_abordagem", idAbordagemGerado);
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
                response.getWriter().flush();
            } else {
                enviarRespostaJSON(response, false, "Erro ao salvar abordagem!");
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao salvar abordagem!";
            logger.error(msg, e);
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void atualizarBlitz(HttpServletRequest request, HttpServletResponse response, 
        Integer idUsuario, Map<String, String> parametros
    ) throws IOException {
        try {
            String idBlitzStr = parametros.get("idBlitz");
            String nomeBlitz = parametros.get("nomeBlitz");
            String tituloNotificacao = parametros.get("tituloNotificacao");
            String descricao = parametros.get("descricao");
            String dataInicio = parametros.get("dataInicio");
            String dataFim = parametros.get("dataFim");
            String locaisStr = parametros.get("locais");
            String tipoAssociacao = parametros.get("tipoAssociacao");
            String associadosStr = parametros.get("associados");
            String ativoStr = parametros.get("ativo");
            String tiposAlertaStr = parametros.get("tiposAlerta");
            String notificarAgentesProximosStr = parametros.get("notificarAgentesProximos");
            String raioNotificacaoKmStr = parametros.get("raioNotificacaoKm");
            String endereco = parametros.get("endereco");

            if (idBlitzStr == null || idBlitzStr.isEmpty() ||
                nomeBlitz == null || nomeBlitz.isEmpty() || 
                tituloNotificacao == null || tituloNotificacao.isEmpty() ||
                associadosStr == null || associadosStr.isEmpty()) {

                String msg = "Parâmetros obrigatórios não informados!";
                logger.error(msg);
                enviarRespostaJSON(response, false, msg);
                return;
            }

            int idBlitz = Integer.parseInt(idBlitzStr);

            BlitzDigital blitzExistente = Blitzes.obterBlitzPorId(idBlitz);
            if (blitzExistente == null) {
                enviarRespostaJSON(response, false, "Blitz não encontrada!");
                return;
            }

            int idTipoBlitz = blitzExistente.getId_tipo_blitz();

            if (idTipoBlitz == 1 && (locaisStr == null || locaisStr.isEmpty())) {
                String msg = "Para blitz automática, é necessário selecionar pelo menos um local (radar)!";
                logger.error(msg);
                enviarRespostaJSON(response, false, msg);
                return;
            }

            int ativo = (ativoStr != null && !ativoStr.isEmpty()) ? Integer.parseInt(ativoStr) : 1;
            int notificarAgentesProximos = (notificarAgentesProximosStr != null && !notificarAgentesProximosStr.isEmpty()) ? 
                Integer.parseInt(notificarAgentesProximosStr) : 0;

            BigDecimal raioNotificacaoKm = null;
            if (notificarAgentesProximos == 1 && raioNotificacaoKmStr != null && !raioNotificacaoKmStr.isEmpty()) {
                raioNotificacaoKm = new BigDecimal(raioNotificacaoKmStr);
            }

            BlitzDigital blitz = new BlitzDigital();
            blitz.setId(idBlitz);
            blitz.setNome_blitz(nomeBlitz);
            blitz.setTitulo_notificacao(tituloNotificacao);
            blitz.setDescricao(descricao);
            blitz.setAtivo(ativo);
            blitz.setNotificar_agentes_proximos(notificarAgentesProximos);
            blitz.setRaio_notificacao_km(raioNotificacaoKm);
            blitz.setId_tipo_blitz(idTipoBlitz);
            blitz.setEndereco(endereco);

            java.sql.Timestamp dataInicioTimestamp = null;
            if (dataInicio != null && !dataInicio.isEmpty()) {
                dataInicioTimestamp = converterParaTimestampBrasilia(dataInicio);
                blitz.setData_inicio(dataInicioTimestamp);
            }

            if (dataFim != null && !dataFim.isEmpty()) {
                java.sql.Timestamp dataFimTimestamp = converterParaTimestampBrasilia(dataFim);

                if (dataInicioTimestamp != null && dataFimTimestamp.before(dataInicioTimestamp)) {
                    enviarRespostaJSON(response, false, "A data de fim não pode ser anterior à data de início.");
                    return;
                }

                blitz.setData_fim(dataFimTimestamp);
            } else {
                blitz.setData_fim(null);
            }

            boolean sucesso = Blitzes.atualizarBlitz(
                blitz,
                locaisStr,
                tipoAssociacao,
                associadosStr,
                tiposAlertaStr
            );

            if (sucesso) {
                String msg = "Blitz atualizada com sucesso!";
                logger.info(msg + " (ID " + idBlitz + ")");
                enviarRespostaJSON(response, true, msg);
            } else {
                String msg = "Erro ao atualizar blitz!";
                logger.error(msg);
                enviarRespostaJSON(response, false, msg);
            }

        } catch (Exception e) {
            String msg = "Ocorreu um erro ao atualizar blitz!";
            logger.error(msg, e);
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarBlitz(HttpServletResponse response) throws IOException {
        try {
            List<BlitzDigital> lista = Blitzes.listarTodas();
            enviarRespostaJSON(response, lista);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar blitz digitais!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
            return;
        }
    }

    private void listarBlitzAtivas(HttpServletResponse response) throws IOException {
        try {
            List<BlitzDigital> lista = Blitzes.listarTodasAtivas();
            enviarRespostaJSON(response, lista);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar as blitz digitais ativas!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
            return;
        }
    }

    private void listarBlitzAtivasAutomaticas(HttpServletResponse response) throws IOException {
        try {
            List<BlitzDigital> lista = Blitzes.listarTodasAtivasAutomaticas();
            enviarRespostaJSON(response, lista);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar as blitz digitais automáticas e ativas!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
            return;
        }
    }

    private void listarLocais(HttpServletResponse response) throws IOException {
        try {
            List<Local> locais = Blitzes.listarLocaisVigentes();
            enviarRespostaLocaisJSON(response, locais);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar locais!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
            return;
        }
    }

    private void listarUsuariosAG(HttpServletResponse response) throws IOException {
        try {
            List<Usuario> usuarios = Blitzes.listarUsuariosAG();
            enviarRespostaUsuariosJSON(response, usuarios);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar usuários!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
            return;
        }
    }

    private void listarGuarnicoes(HttpServletResponse response) throws IOException {
        try {
            List<Guarnicao> guarnicoes = Blitzes.listarGuarnicoesAtivas();
            enviarRespostaGuarnicoesJSON(response, guarnicoes);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar guarnições!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
            return;
        }
    }

    private void cadastrarBlitz(HttpServletRequest request, HttpServletResponse response, 
        Integer idUsuario, Map<String, String> parametros
    ) throws IOException {
        try {
            String nomeBlitz = parametros.get("nomeBlitz");
            String tituloNotificacao = parametros.get("tituloNotificacao");
            String descricao = parametros.get("descricao");
            String dataInicio = parametros.get("dataInicio");
            String dataFim = parametros.get("dataFim");
            String locaisStr = parametros.get("locais");
            String tipoAssociacao = parametros.get("tipoAssociacao");
            String associadosStr = parametros.get("associados");
            String ativoStr = parametros.get("ativo");
            String tiposAlertaStr = parametros.get("tiposAlerta");
            String notificarAgentesProximosStr = parametros.get("notificarAgentesProximos");
            String raioNotificacaoKmStr = parametros.get("raioNotificacaoKm");
            String idTipoBlitzStr = parametros.get("idTipoBlitz");
            String endereco = parametros.get("endereco");

            if (nomeBlitz == null || nomeBlitz.isEmpty() || 
                tituloNotificacao == null || tituloNotificacao.isEmpty() ||
                associadosStr == null || associadosStr.isEmpty()) {

                String msg = "Parâmetros obrigatórios não informados!";
                logger.error(msg);    
                enviarRespostaJSON(response, false, msg);
                return;
            }
            
            int idTipoBlitz = (idTipoBlitzStr != null && !idTipoBlitzStr.isEmpty()) ? 
                Integer.parseInt(idTipoBlitzStr) : 1; // default para AUTOMATICA
            
            if (idTipoBlitz == 1 && (locaisStr == null || locaisStr.isEmpty())) {
                String msg = "Para blitz automática, é necessário selecionar pelo menos um local (radar)!";
                logger.error(msg);    
                enviarRespostaJSON(response, false, msg);
                return;
            }

            int ativo = (ativoStr != null && !ativoStr.isEmpty()) ? Integer.parseInt(ativoStr) : 1;
            int notificarAgentesProximos = (notificarAgentesProximosStr != null && !notificarAgentesProximosStr.isEmpty()) ? 
                Integer.parseInt(notificarAgentesProximosStr) : 0;
            BigDecimal raioNotificacaoKm = null;
            
            if (notificarAgentesProximos == 1 && raioNotificacaoKmStr != null && !raioNotificacaoKmStr.isEmpty()) {
                raioNotificacaoKm = new BigDecimal(raioNotificacaoKmStr);
            }

            java.sql.Timestamp dataInicioTimestamp = null;

            BlitzDigital blitz = new BlitzDigital();
            blitz.setNome_blitz(nomeBlitz);
            blitz.setTitulo_notificacao(tituloNotificacao);
            blitz.setDescricao(descricao);
            
            if (dataInicio != null && !dataInicio.isEmpty()) {
                dataInicioTimestamp = converterParaTimestampBrasilia(dataInicio);
                java.sql.Timestamp dataAtual = new java.sql.Timestamp(System.currentTimeMillis());
                
                if (dataInicioTimestamp.before(dataAtual)) {
                    enviarRespostaJSON(response, false, "A data de início não pode ser anterior à data/hora atual.");
                    return;
                }
                
                blitz.setData_inicio(dataInicioTimestamp);
            }
            
            if (dataFim != null && !dataFim.isEmpty()) {
                java.sql.Timestamp dataFimTimestamp = converterParaTimestampBrasilia(dataFim);
                
                if (dataInicioTimestamp != null && dataFimTimestamp.before(dataInicioTimestamp)) {
                    enviarRespostaJSON(response, false, "A data de fim não pode ser anterior à data de início.");
                    return;
                }
                
                blitz.setData_fim(dataFimTimestamp);
            }
            
            blitz.setAtivo(ativo);
            blitz.setNotificar_agentes_proximos(notificarAgentesProximos);
            blitz.setRaio_notificacao_km(raioNotificacaoKm);

            blitz.setId_tipo_blitz(idTipoBlitz);
            blitz.setEndereco(endereco);

            int idBlitz = Blitzes.salvarBlitz(blitz);
            
            if (idBlitz > 0) {
                boolean sucessoLocais = true;
                
                if (idTipoBlitz == 1) {
                    sucessoLocais = Blitzes.salvarLocais(idBlitz, locaisStr);
                }
                
                boolean sucessoAssociacao = false;
                boolean sucessoTiposAlerta = true;

                if (tiposAlertaStr != null && !tiposAlertaStr.isEmpty()) {
                    sucessoTiposAlerta = Blitzes.salvarTiposAlerta(idBlitz, tiposAlertaStr);
                }
                
                if ("usuario".equals(tipoAssociacao)) {
                    sucessoAssociacao = Blitzes.salvarUsuarios(idBlitz, associadosStr);
                } else {
                    sucessoAssociacao = Blitzes.salvarGuarnicoes(idBlitz, associadosStr);
                }

                if (sucessoLocais && sucessoAssociacao && sucessoTiposAlerta) {
                    String msg = "Blitz " + (idTipoBlitz == 1 ? "Automática" : "Manual") + " cadastrada com sucesso!";
                    logger.info(msg + " (ID " + idBlitz + ")");
                    enviarRespostaJSON(response, true, msg);
                } else {
                    String msg = "Erro ao salvar associações da blitz!";
                    logger.error(msg);
                    enviarRespostaJSON(response, false, msg);
                }
            } else {
                String msg = "Erro ao salvar blitz!";
                logger.error(msg);
                enviarRespostaJSON(response, false, msg);
            }
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao cadastrar blitz!";
            logger.error(msg, e);
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void excluirBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idBlitz = Integer.parseInt(request.getParameter("idBlitz"));

            boolean sucesso = Blitzes.excluirBlitz(idBlitz);

            if (sucesso) {
                String msg = "Blitz excluída com sucesso!";
                enviarRespostaJSON(response, true, msg);
            } else {
                String msg = "Falha ao excluir blitz!";
                enviarRespostaJSON(response, false, msg);
            }

        } catch (Exception e) {
            String msg = "Ocorreu um erro ao excluir blitz!";
            logger.error(msg, e);
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void obterBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idBlitz = Integer.parseInt(request.getParameter("idBlitz"));
            BlitzDigital blitz = Blitzes.obterBlitzPorId(idBlitz);
            
            if (blitz != null) {
                enviarRespostaBlitzUnicaJSON(response, blitz);
            } else {
                String msg = "Blitz não encontrada!";
                enviarRespostaJSON(response, false, msg);
            }
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao obter blitz!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarLocaisBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idBlitz = Integer.parseInt(request.getParameter("idBlitz"));
            List<Local> locais = Blitzes.listarLocaisBlitz(idBlitz);
            enviarRespostaLocaisJSON(response, locais);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar locais da blitz!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarUsuariosBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idBlitz = Integer.parseInt(request.getParameter("idBlitz"));
            List<Usuario> usuarios = Blitzes.listarUsuariosBlitz(idBlitz);
            enviarRespostaUsuariosJSON(response, usuarios);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar usuários da blitz!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarGuarnicoesBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idBlitz = Integer.parseInt(request.getParameter("idBlitz"));
            List<Guarnicao> guarnicoes = Blitzes.listarGuarnicoesBlitz(idBlitz);
            enviarRespostaGuarnicoesJSON(response, guarnicoes);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar guarnições da blitz!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void verificarTipoAssociacao(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idBlitz = Integer.parseInt(request.getParameter("idBlitz"));
            String tipo = Blitzes.verificarTipoAssociacao(idBlitz);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("tipo", tipo);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao verificar tipo de associação!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    // Métodos para enviar respostas JSON
    private void enviarRespostaJSON(HttpServletResponse response, boolean sucesso, String mensagem) throws IOException {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("sucesso", sucesso);
        jsonResponse.put("mensagem", mensagem);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse.toString());
        response.getWriter().flush();
    }

    private void enviarRespostaJSON(HttpServletResponse response, List<BlitzDigital> lista) throws Exception {
        JSONObject jsonResponse = new JSONObject();
        JSONArray blitzArray = new JSONArray();
        
        for (BlitzDigital blitz : lista) {
            JSONObject blitzJson = new JSONObject();
            blitzJson.put("id", blitz.getId());
            blitzJson.put("nome_blitz", blitz.getNome_blitz());
            blitzJson.put("titulo_notificacao", blitz.getTitulo_notificacao());
            blitzJson.put("descricao", blitz.getDescricao());
            blitzJson.put("data_inicio", blitz.getData_inicio() != null ? blitz.getData_inicio().toString() : "");
            blitzJson.put("data_fim", blitz.getData_fim() != null ? blitz.getData_fim().toString() : "");
            blitzJson.put("data_criacao", blitz.getData_criacao() != null ? blitz.getData_criacao().toString() : "");
            blitzJson.put("ativo", blitz.getAtivo());
            blitzJson.put("notificar_agentes_proximos", blitz.getNotificar_agentes_proximos());
            blitzJson.put("raio_notificacao_km", blitz.getRaio_notificacao_km());
            blitzJson.put("id_tipo_blitz", blitz.getId_tipo_blitz() != null ? blitz.getId_tipo_blitz() : 1);
            blitzJson.put("endereco", blitz.getEndereco() != null ? blitz.getEndereco() : "");
            blitzJson.put("tem_abordagens_associadas", blitz.getTem_abordagens_associadas());
            blitzArray.put(blitzJson);
        }
        
        jsonResponse.put("blitzes", blitzArray);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(jsonResponse.toString());
        response.getWriter().flush();
    }

    private void enviarRespostaLocaisJSON(HttpServletResponse response, List<Local> locais) throws Exception {
        JSONObject jsonResponse = new JSONObject();
        JSONArray locaisArray = new JSONArray();
        
        for (Local local : locais) {
            JSONObject localJson = new JSONObject();
            localJson.put("id_local", local.getId_local());
            localJson.put("sequencia_local", local.getSequencia_local());
            localJson.put("nome", local.getNome());
            localJson.put("posicao_lat", local.getPosicao_lat());
            localJson.put("posicao_lon", local.getPosicao_lon());
            locaisArray.put(localJson);
        }
        
        jsonResponse.put("locais", locaisArray);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(jsonResponse.toString());
        response.getWriter().flush();
    }

    private void enviarRespostaUsuariosJSON(HttpServletResponse response, List<Usuario> usuarios) throws Exception {
        JSONObject jsonResponse = new JSONObject();
        JSONArray usuariosArray = new JSONArray();
        
        for (Usuario usuario : usuarios) {
            JSONObject usuarioJson = new JSONObject();
            usuarioJson.put("id_usuario", usuario.getIdUsuario());
            usuarioJson.put("usuario", usuario.getUsuario());
            usuarioJson.put("nome", usuario.getNome());
            usuarioJson.put("email", usuario.getEmail());
            usuarioJson.put("telefone", usuario.getTelefone());
            usuariosArray.put(usuarioJson);
        }
        
        jsonResponse.put("usuarios", usuariosArray);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(jsonResponse.toString());
        response.getWriter().flush();
    }

    private void enviarRespostaGuarnicoesJSON(HttpServletResponse response, List<Guarnicao> guarnicoes) throws Exception {
        JSONObject jsonResponse = new JSONObject();
        JSONArray guarnicoesArray = new JSONArray();
        
        for (Guarnicao guarnicao : guarnicoes) {
            JSONObject guarnicaoJson = new JSONObject();
            guarnicaoJson.put("id", guarnicao.getId());
            guarnicaoJson.put("nome", guarnicao.getNome());
            guarnicoesArray.put(guarnicaoJson);
        }
        
        jsonResponse.put("guarnicoes", guarnicoesArray);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(jsonResponse.toString());
        response.getWriter().flush();
    }

    private void enviarRespostaBlitzUnicaJSON(HttpServletResponse response, BlitzDigital blitz) throws Exception {
        JSONObject jsonResponse = new JSONObject();
        JSONObject blitzJson = new JSONObject();
        
        blitzJson.put("id", blitz.getId());
        blitzJson.put("nome_blitz", blitz.getNome_blitz());
        blitzJson.put("titulo_notificacao", blitz.getTitulo_notificacao());
        blitzJson.put("descricao", blitz.getDescricao());
        blitzJson.put("data_inicio", blitz.getData_inicio() != null ? blitz.getData_inicio().toString() : "");
        blitzJson.put("data_fim", blitz.getData_fim() != null ? blitz.getData_fim().toString() : "");
        blitzJson.put("data_criacao", blitz.getData_criacao() != null ? blitz.getData_criacao().toString() : "");
        blitzJson.put("ativo", blitz.getAtivo());
        blitzJson.put("notificar_agentes_proximos", blitz.getNotificar_agentes_proximos());
        blitzJson.put("raio_notificacao_km", blitz.getRaio_notificacao_km());
        blitzJson.put("id_tipo_blitz", blitz.getId_tipo_blitz() != null ? blitz.getId_tipo_blitz() : 1);
        blitzJson.put("endereco", blitz.getEndereco() != null ? blitz.getEndereco() : "");
        
        jsonResponse.put("blitz", blitzJson);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(jsonResponse.toString());
        response.getWriter().flush();
    }

    private void listarTiposAlerta(HttpServletResponse response) throws IOException {
        try {
            List<TipoAlerta> tiposAlerta = Blitzes.listarTiposAlerta();
            enviarRespostaTiposAlertaJSON(response, tiposAlerta);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar tipos de alerta!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarTiposAlertaBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idBlitz = Integer.parseInt(request.getParameter("idBlitz"));
            List<TipoAlerta> tiposAlerta = Blitzes.listarTiposAlertaBlitz(idBlitz);
            enviarRespostaTiposAlertaJSON(response, tiposAlerta);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar tipos de alerta da blitz!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void enviarRespostaTiposAlertaJSON(HttpServletResponse response, List<TipoAlerta> tiposAlerta) throws Exception {
        JSONObject jsonResponse = new JSONObject();
        JSONArray tiposAlertaArray = new JSONArray();
        
        for (TipoAlerta tipo : tiposAlerta) {
            JSONObject tipoJson = new JSONObject();
            tipoJson.put("id", tipo.getId());
            tipoJson.put("tipo", tipo.getTipo());
            tipoJson.put("descricao", tipo.getDescricao());
            tiposAlertaArray.put(tipoJson);
        }
        
        jsonResponse.put("tiposAlerta", tiposAlertaArray);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(jsonResponse.toString());
        response.getWriter().flush();
    }

    private java.sql.Timestamp converterParaTimestampBrasilia(String dataHoraString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            sdf.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
            
            Date date = sdf.parse(dataHoraString);
            return new java.sql.Timestamp(date.getTime());
            
        } catch (Exception e) {
            logger.error("Erro ao converter data/hora: " + dataHoraString, e);
            return null;
        }
    }

    private void listarAbordagensFiltro(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String dataInicioStr = request.getParameter("dataInicio");
            String dataFimStr = request.getParameter("dataFim");
            String idBlitzStr = request.getParameter("idBlitz");
            
            Date dataInicio = null;
            Date dataFim = null;
            Integer idBlitz = null;
            
            if (dataInicioStr != null && !dataInicioStr.isEmpty()) {
                dataInicio = java.sql.Date.valueOf(dataInicioStr);
            }
            
            if (dataFimStr != null && !dataFimStr.isEmpty()) {
                dataFim = java.sql.Date.valueOf(dataFimStr);
                Calendar cal = Calendar.getInstance();
                cal.setTime(dataFim);
                cal.set(Calendar.HOUR_OF_DAY, 23);
                cal.set(Calendar.MINUTE, 59);
                cal.set(Calendar.SECOND, 59);
                dataFim = cal.getTime();
            }
            
            if (idBlitzStr != null && !idBlitzStr.isEmpty()) {
                idBlitz = Integer.parseInt(idBlitzStr);
            }
            
            List<Map<String, Object>> abordagens = Blitzes.listarAbordagensComFiltro(dataInicio, dataFim, idBlitz);
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray abordagensArray = new JSONArray();
            
            for (Map<String, Object> abordagem : abordagens) {
                JSONObject abordagemJson = new JSONObject();
                
                for (Map.Entry<String, Object> entry : abordagem.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Timestamp) {
                        abordagemJson.put(entry.getKey(), value.toString());
                    } else if (value instanceof Date) {
                        abordagemJson.put(entry.getKey(), ((Date) value).toString());
                    } else {
                        abordagemJson.put(entry.getKey(), value);
                    }
                }
                
                abordagensArray.put(abordagemJson);
            }
            
            jsonResponse.put("abordagens", abordagensArray);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar abordagens com filtro!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void obterAbordagemDetalhada(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idAbordagemStr = request.getParameter("idAbordagem");
            
            if (idAbordagemStr == null || idAbordagemStr.isEmpty()) {
                enviarRespostaJSON(response, false, "ID da abordagem não informado!");
                return;
            }
            
            Long idAbordagem = Long.parseLong(idAbordagemStr);
            Map<String, Object> resultado = Blitzes.obterAbordagemDetalhada(idAbordagem);
            
            if (resultado.containsKey("abordagem")) {
                JSONObject jsonResponse = new JSONObject();
                
                Map<String, Object> abordagemMap = (Map<String, Object>) resultado.get("abordagem");
                JSONObject abordagemJson = new JSONObject();
                
                for (Map.Entry<String, Object> entry : abordagemMap.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Timestamp) {
                        abordagemJson.put(entry.getKey(), value.toString());
                    } else if (value instanceof Date) {
                        abordagemJson.put(entry.getKey(), ((Date) value).toString());
                    } else {
                        abordagemJson.put(entry.getKey(), value);
                    }
                }
                
                jsonResponse.put("abordagem", abordagemJson);
                
                if (resultado.containsKey("alerta")) {
                    Map<String, Object> alertaMap = (Map<String, Object>) resultado.get("alerta");
                    JSONObject alertaJson = new JSONObject();
                    
                    for (Map.Entry<String, Object> entry : alertaMap.entrySet()) {
                        Object value = entry.getValue();
                        if (value instanceof Timestamp) {
                            alertaJson.put(entry.getKey(), value.toString());
                        } else if (value instanceof Date) {
                            alertaJson.put(entry.getKey(), ((Date) value).toString());
                        } else {
                            alertaJson.put(entry.getKey(), value);
                        }
                    }
                    
                    jsonResponse.put("alerta", alertaJson);
                }
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
                response.getWriter().flush();
            } else {
                enviarRespostaJSON(response, false, "Abordagem não encontrada!");
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao obter abordagem detalhada!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void encerrarBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idBlitzStr = request.getParameter("idBlitz");
            
            if (idBlitzStr == null || idBlitzStr.isEmpty()) {
                String msg = "ID da blitz não informado!";
                logger.error(msg);    
                enviarRespostaJSON(response, false, msg);
                return;
            }

            int idBlitz = Integer.parseInt(idBlitzStr);
            
            BlitzDigital blitzExistente = Blitzes.obterBlitzPorId(idBlitz);
            if (blitzExistente == null) {
                enviarRespostaJSON(response, false, "Blitz não encontrada!");
                return;
            }

            boolean sucesso = Blitzes.encerrarBlitz(idBlitz);

            if (sucesso) {
                String msg = "Blitz encerrada com sucesso!";
                logger.info(msg + " (ID " + idBlitz + ")");
                enviarRespostaJSON(response, true, msg);
            } else {
                String msg = "Erro ao encerrar blitz!";
                logger.error(msg);
                enviarRespostaJSON(response, false, msg);
            }

        } catch (Exception e) {
            String msg = "Ocorreu um erro ao encerrar blitz!";
            logger.error(msg, e);
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void obterImagemDocumento(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String caminhoArquivo = request.getParameter("caminho");
        
        if (caminhoArquivo == null || caminhoArquivo.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Caminho do arquivo não informado!");
            return;
        }
        
        try {
            File arquivo = new File(caminhoArquivo);
            
            if (!arquivo.exists() || !arquivo.isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo não encontrado!");
                return;
            }
            
            String nomeArquivo = arquivo.getName();
            String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1).toLowerCase();
            
            String contentType;
            switch (extensao) {
                case "jpg":
                case "jpeg":
                    contentType = "image/jpeg";
                    break;
                case "png":
                    contentType = "image/png";
                    break;
                case "pdf":
                    contentType = "application/pdf";
                    break;
                default:
                    contentType = "application/octet-stream";
            }
            
            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "inline; filename=\"" + nomeArquivo + "\"");
            response.setContentLength((int) arquivo.length());
            
            try (InputStream in = new FileInputStream(arquivo);
                OutputStream out = response.getOutputStream()) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            
        } catch (Exception e) {
            logger.error("Erro ao obter imagem do documento", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar arquivo");
        }
    }

    private void downloadDocumento(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String caminhoArquivo = request.getParameter("caminho");
        
        if (caminhoArquivo == null || caminhoArquivo.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Caminho do arquivo não informado!");
            return;
        }
        
        try {
            File arquivo = new File(caminhoArquivo);
            
            if (!arquivo.exists() || !arquivo.isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo não encontrado!");
                return;
            }
            
            String nomeArquivo = arquivo.getName();
            
            String mimeType = getMimeType(arquivo);

            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            response.setContentType(mimeType);

            response.setHeader("Content-Disposition", "attachment; filename=\"" + nomeArquivo + "\"");
            response.setContentLength((int) arquivo.length());
            
            try (InputStream in = new FileInputStream(arquivo);
                OutputStream out = response.getOutputStream()) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            
        } catch (Exception e) {
            logger.error("Erro ao fazer download do documento", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao processar arquivo");
        }
    }

    private String getMimeType(File file) {
        String name = file.getName().toLowerCase();

        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (name.endsWith(".png")) {
            return "image/png";
        } else if (name.endsWith(".gif")) {
            return "image/gif";
        } else if (name.endsWith(".pdf")) {
            return "application/pdf";
        }

        return "application/octet-stream";
    }

    private void listarRegistrosFato(HttpServletResponse response) throws IOException {
        try {
            List<Map<String, Object>> registros = Blitzes.listarRegistrosFato();
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray registrosArray = new JSONArray();
            
            for (Map<String, Object> registro : registros) {
                JSONObject registroJson = new JSONObject();
                
                Long id = (Long) registro.get("id");
                String descricao = (String) registro.get("descricao");
                String tipo = (String) registro.get("tipo");
                String dataFormatada = (String) registro.get("data_formatada");
                String dataCriacao = (String) registro.get("data_criacao");
                
                if (dataFormatada == null || dataFormatada.isEmpty()) {
                    dataFormatada = dataCriacao;
                }

                String descricaoCompleta = descricao + " - " + dataFormatada;
                
                registroJson.put("id", id);
                registroJson.put("descricao", descricaoCompleta);
                registroJson.put("tipo", tipo);
                registroJson.put("data_formatada", dataFormatada);
                
                registrosArray.put(registroJson);
            }
            
            jsonResponse.put("registrosFato", registrosArray);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar registros de fato!";
            logger.error(msg, e);
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void obterDetalhesRegistroFato(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idRegistroFatoStr = request.getParameter("idRegistroFato");
            
            if (idRegistroFatoStr == null || idRegistroFatoStr.isEmpty()) {
                enviarRespostaJSON(response, false, "ID do registro de fato não informado!");
                return;
            }
            
            Long idRegistroFato = Long.parseLong(idRegistroFatoStr);
            Map<String, Object> registroFato = Blitzes.obterDetalhesRegistroFato(idRegistroFato);
            
            if (registroFato != null && !registroFato.isEmpty()) {
                JSONObject jsonResponse = new JSONObject();
                JSONObject registroJson = new JSONObject();
                
                for (Map.Entry<String, Object> entry : registroFato.entrySet()) {
                    Object value = entry.getValue();
                    if (value instanceof Date) {
                        registroJson.put(entry.getKey(), ((Date) value).toString());
                    } else if (value instanceof Timestamp) {
                        registroJson.put(entry.getKey(), value.toString());
                    } else {
                        registroJson.put(entry.getKey(), value);
                    }
                }
                
                jsonResponse.put("registroFato", registroJson);
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
                response.getWriter().flush();
            } else {
                enviarRespostaJSON(response, false, "Registro de fato não encontrado!");
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao obter detalhes do registro de fato!";
            logger.error(msg, e);
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarBlitzAtivasPorLocal(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idLocal = Integer.parseInt(request.getParameter("idLocal"));
            List<BlitzDigital> lista = Blitzes.listarBlitzAtivasPorLocal(idLocal);
            enviarRespostaJSON(response, lista);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar blitz por local!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void obterInformacoesAlerta(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idAlerta = request.getParameter("idAlerta");
            
            if (idAlerta == null || idAlerta.isEmpty()) {
                enviarRespostaJSON(response, false, "ID do alerta não informado!");
                return;
            }
            
            Map<String, Object> info = Blitzes.obterInformacoesAlerta(idAlerta);
            
            if (info != null && !info.isEmpty()) {
                JSONObject jsonResponse = new JSONObject();
                
                for (Map.Entry<String, Object> entry : info.entrySet()) {
                    jsonResponse.put(entry.getKey(), entry.getValue());
                }
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
                response.getWriter().flush();
            } else {
                enviarRespostaJSON(response, false, "Alerta não encontrado!");
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao obter informações do alerta!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void verificarAbordagemPorAlerta(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idAlerta = request.getParameter("idAlerta");
            
            if (idAlerta == null || idAlerta.isEmpty()) {
                enviarRespostaJSON(response, false, "ID do alerta não informado!");
                return;
            }
            
            boolean existe = Blitzes.verificarSeExisteAbordagemNoBanco(idAlerta);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("existe", existe);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao verificar abordagem por alerta!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void verificarAlertaBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idAlerta = request.getParameter("idAlerta");
            
            if (idAlerta == null || idAlerta.isEmpty()) {
                enviarRespostaJSON(response, false, "ID do alerta não informado!");
                return;
            }
            
            boolean isAlertaBlitz = Blitzes.verificarSeAlertaEhDeBlitz(idAlerta);
            
            JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("ehAlertaBlitz", isAlertaBlitz);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao verificar se alerta é de blitz!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void obterInformacoesBlitzOstensiva(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idBlitzStr = request.getParameter("idBlitz");
            String idVeiculoTempoReal = request.getParameter("idVeiculoTempoReal");
            
            if (idBlitzStr == null || idBlitzStr.isEmpty() || idVeiculoTempoReal == null || idVeiculoTempoReal.isEmpty()) {
                enviarRespostaJSON(response, false, "Parâmetros obrigatórios não informados!");
                return;
            }
            
            Map<String, Object> info = Blitzes.obterInformacoesBlitzOstensiva(Integer.parseInt(idBlitzStr), idVeiculoTempoReal);
            
            if (info != null && !info.isEmpty()) {
                JSONObject jsonResponse = new JSONObject();
                
                for (Map.Entry<String, Object> entry : info.entrySet()) {
                    jsonResponse.put(entry.getKey(), entry.getValue());
                }
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
                response.getWriter().flush();
            } else {
                enviarRespostaJSON(response, false, "Não foi possível obter informações da blitz ostensiva!");
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao obter informações da blitz ostensiva!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void buscarHistoricoPorCpf(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String cpf = request.getParameter("cpf");
            
            if (cpf == null || cpf.isEmpty()) {
                enviarRespostaJSON(response, false, "CPF não informado!");
                return;
            }
            
            HistoricoCPF historico = Blitzes.buscarHistoricoPorCpf(cpf);
            
            if (historico != null) {
                JSONObject jsonResponse = new JSONObject();
                
                JSONArray registrosArray = new JSONArray();
                if (historico.getRegistrosDeFato() != null) {
                    for (muralha.digital.registroDeFato.RegistroFatoDTO registro : historico.getRegistrosDeFato()) {
                        JSONObject registroJson = new JSONObject();
                        registroJson.put("id", registro.getId());
                        registroJson.put("atendimentoPermitido", registro.getAtendimentoPermitido());
                        registroJson.put("envolvimentoArmas", registro.getEnvolvimentoArmas());
                        registroJson.put("privado", registro.getPrivado());
                        registroJson.put("tipoRegistro", registro.getTipoRegistro());
                        registroJson.put("idStatus", registro.getIdStatus());
                        registroJson.put("dataHoraOcorrido", registro.getDataHoraOcorrido());
                        registroJson.put("detalhamentoFato", registro.getDetalhamentoFato());
                        registroJson.put("idsGrupos", registro.getIdsGrupos() != null ? new JSONArray(registro.getIdsGrupos()) : new JSONArray());
                        registroJson.put("idsUsuarios", registro.getIdsUsuarios() != null ? new JSONArray(registro.getIdsUsuarios()) : new JSONArray());
                        registrosArray.put(registroJson);
                    }
                }
                
                JSONArray antecedentesArray = new JSONArray();
                if (historico.getAntecedentesCriminais() != null) {
                    for (muralha.digital.blitz.AntecedenteCriminal antecedente : historico.getAntecedentesCriminais()) {
                        JSONObject antecedenteJson = new JSONObject();
                        antecedenteJson.put("id", antecedente.getId());
                        antecedenteJson.put("id_proprietario", antecedente.getId_proprietario());
                        antecedenteJson.put("tipo_crime", antecedente.getTipo_crime());
                        antecedenteJson.put("data_ocorrencia", antecedente.getData_ocorrencia() != null ? antecedente.getData_ocorrencia().toString() : "");
                        antecedenteJson.put("local_ocorrencia", antecedente.getLocal_ocorrencia());
                        antecedenteJson.put("descricao", antecedente.getDescricao());
                        antecedenteJson.put("sentenca", antecedente.getSentenca());
                        antecedentesArray.put(antecedenteJson);
                    }
                }
                
                jsonResponse.put("registrosDeFato", registrosArray);
                jsonResponse.put("antecedentesCriminais", antecedentesArray);
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
                response.getWriter().flush();
                
            } else {
                enviarRespostaJSON(response, false, "Erro ao buscar histórico por CPF!");
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao buscar histórico por CPF!";
            logger.error(msg, e);
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void buscarHistoricoPorPlaca(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String placa = request.getParameter("placa");
            
            if (placa == null || placa.isEmpty()) {
                enviarRespostaJSON(response, false, "Placa não informada!");
                return;
            }
            
            HistoricoPlaca historico = Blitzes.buscarHistoricoPorPlaca(placa);
            
            if (historico != null) {
                JSONObject jsonResponse = new JSONObject();
                
                JSONArray registrosArray = new JSONArray();
                if (historico.getRegistrosDeFato() != null) {
                    for (muralha.digital.registroDeFato.RegistroFatoDTO registro : historico.getRegistrosDeFato()) {
                        JSONObject registroJson = new JSONObject();
                        registroJson.put("id", registro.getId());
                        registroJson.put("atendimentoPermitido", registro.getAtendimentoPermitido());
                        registroJson.put("envolvimentoArmas", registro.getEnvolvimentoArmas());
                        registroJson.put("privado", registro.getPrivado());
                        registroJson.put("tipoRegistro", registro.getTipoRegistro());
                        registroJson.put("idStatus", registro.getIdStatus());
                        registroJson.put("dataHoraOcorrido", registro.getDataHoraOcorrido());
                        registroJson.put("detalhamentoFato", registro.getDetalhamentoFato());
                        registroJson.put("idsGrupos", registro.getIdsGrupos() != null ? new JSONArray(registro.getIdsGrupos()) : new JSONArray());
                        registroJson.put("idsUsuarios", registro.getIdsUsuarios() != null ? new JSONArray(registro.getIdsUsuarios()) : new JSONArray());
                        registrosArray.put(registroJson);
                    }
                }
                
                JSONArray alertasArray = new JSONArray();
                if (historico.getAlertas() != null) {
                    for (muralha.digital.alerta.Alerta alerta : historico.getAlertas()) {
                        JSONObject alertaJson = new JSONObject();
                        alertaJson.put("id", alerta.getId() != null ? alerta.getId().toString() : "");
                        alertaJson.put("idTipoAlerta", alerta.getIdTipoAlerta() != null ? alerta.getIdTipoAlerta().toString() : "");
                        alertaJson.put("tipoAlerta", alerta.getTipoAlerta());
                        alertaJson.put("descAlerta", alerta.getDescAlerta());
                        alertaJson.put("idCadVeicMonitorado", alerta.getIdCadVeicMonitorado() != null ? alerta.getIdCadVeicMonitorado().toString() : "");
                        alertaJson.put("placaCadastro", alerta.getPlacaCadastro());
                        alertaJson.put("dataCadVeicMonitorado", alerta.getDataCadVeicMonitorado() != null ? alerta.getDataCadVeicMonitorado().toString() : "");
                        alertaJson.put("idStatusAlerta", alerta.getIdStatusAlerta() != null ? alerta.getIdStatusAlerta().toString() : "");
                        alertaJson.put("statusAlertaDesc", alerta.getStatusAlertaDesc());
                        alertaJson.put("dataAlerta", alerta.getDataAlerta() != null ? alerta.getDataAlerta().toString() : "");
                        alertaJson.put("dataPassagem", alerta.getDataPassagem() != null ? alerta.getDataPassagem().toString() : "");
                        alertaJson.put("enviadoAoCliente", alerta.getEnviadoAoCliente());
                        alertaJson.put("dataEnviadoCliente", alerta.getDataEnviadoCliente() != null ? alerta.getDataEnviadoCliente().toString() : "");
                        alertaJson.put("placaVeiculo", alerta.getPlacaVeiculo());
                        alertaJson.put("dataVeiculo", alerta.getDataVeiculo() != null ? alerta.getDataVeiculo().toString() : "");
                        alertaJson.put("idMotivoDescarte", alerta.getIdMotivoDescarte() != null ? alerta.getIdMotivoDescarte().toString() : "");
                        alertaJson.put("motivoDescarte", alerta.getMotivoDescarte());
                        alertaJson.put("observacao", alerta.getObservacao());
                        alertaJson.put("idUsuario", alerta.getIdUsuario());
                        alertaJson.put("usuario", alerta.getUsuario());
                        alertaJson.put("nomeUsuario", alerta.getNomeUsuario());
                        alertaJson.put("descartado", alerta.isDescartado());
                        alertaJson.put("idOcorrencia", alerta.getIdOcorrencia() != null ? alerta.getIdOcorrencia().toString() : "");
                        alertaJson.put("ocorrenciaGerada", alerta.isOcorrenciaGerada());
                        alertaJson.put("ocorrenciaComNotificacao", alerta.isOcorrenciaComNotificacao());
                        alertaJson.put("idTipoRegistro", alerta.getIdTipoRegistro() != null ? alerta.getIdTipoRegistro().toString() : "");
                        alertaJson.put("tipoRegistro", alerta.getTipoRegistro());
                        alertaJson.put("lembrete", alerta.getLembrete());
                        alertaJson.put("idStatusOcorrencia", alerta.getIdStatusOcorrencia() != null ? alerta.getIdStatusOcorrencia().toString() : "");
                        alertaJson.put("statusOcorrencia", alerta.getStatusOcorrencia());
                        alertaJson.put("ocorrenciaFinalizada", alerta.isOcorrenciaFinalizada());
                        alertaJson.put("obsFinalizarOcorrencia", alerta.getObsFinalizarOcorrencia());
                        alertaJson.put("idPontoInteresse", alerta.getIdPontoInteresse() != null ? alerta.getIdPontoInteresse().toString() : "");
                        alertaJson.put("nomePontoInteresse", alerta.getNomePontoInteresse());
                        alertaJson.put("alertaVinculado", alerta.isAlertaVinculado());
                        alertaJson.put("idAlertaVinculado", alerta.getIdAlertaVinculado() != null ? alerta.getIdAlertaVinculado().toString() : "");
                        alertaJson.put("idLocal", alerta.getIdLocal());
                        alertaJson.put("serieEquipamento", alerta.getSerieEquipamento());
                        alertaJson.put("idPista", alerta.getIdPista());
                        alertaJson.put("faixa", alerta.getFaixa());
                        alertaJson.put("velocidade", alerta.getVelocidade());
                        alertaJson.put("totalRegistros", alerta.getTotalRegistros());
                        alertaJson.put("emAtendimentoPor", alerta.getEmAtendimentoPor());
                        alertaJson.put("atendido", alerta.getAtendido());
                        alertaJson.put("permiteAtendimento", alerta.isPermiteAtendimento());
                        alertaJson.put("permiteAlterarAtendimento", alerta.isPermiteAlterarAtendimento());
                        alertaJson.put("supervisionado", alerta.getSupervisionado());
                        alertaJson.put("assinado", alerta.isAssinado());
                        alertaJson.put("com_semelhanca", alerta.getCom_semelhanca());
                        alertaJson.put("com_semelhanca_erros", alerta.getCom_semelhanca_erros());
                        alertaJson.put("com_semelhanca_desc", alerta.getCom_semelhanca_desc());
                        alertaJson.put("possuiFato", alerta.isPossuiFato());
                        alertaJson.put("id_registro_fato", alerta.getId_registro_fato());
                        alertaJson.put("idUsuarioResponsavel", alerta.getIdUsuarioResponsavel());
                        alertaJson.put("possui_bo_alerta", alerta.getPossui_bo_alerta());
                        alertaJson.put("nao_assinados", alerta.getNao_assinados());
                        
                        alertasArray.put(alertaJson);
                    }
                }
                
                jsonResponse.put("registrosDeFato", registrosArray);
                jsonResponse.put("alertas", alertasArray);
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
                response.getWriter().flush();
                
            } else {
                enviarRespostaJSON(response, false, "Erro ao buscar histórico por placa!");
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao buscar histórico por placa!";
            logger.error(msg, e);
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void listarImagensAbordagem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idAbordagemStr = request.getParameter("idAbordagem");

            if (idAbordagemStr == null || idAbordagemStr.isEmpty()) {
                enviarRespostaJSON(response, false, "ID da abordagem não informado!");
                return;
            }

            Long idAbordagem = Long.parseLong(idAbordagemStr);
            List<BlitzImagemAbordagem> imagens = Blitzes.listarImagensPorAbordagem(idAbordagem);

            JSONObject jsonResponse = new JSONObject();
            JSONArray imagensArray = new JSONArray();

            for (BlitzImagemAbordagem imagem : imagens) {
                JSONObject imagemJson = new JSONObject();

                imagemJson.put("id", imagem.getId());
                imagemJson.put("id_abordagem", imagem.getId_abordagem());
                imagemJson.put("nome_arquivo_original", imagem.getNome_arquivo_original());
                imagemJson.put("caminho_arquivo", imagem.getCaminho_arquivo());
                imagemJson.put("tipo_arquivo", imagem.getTipo_arquivo());
                imagemJson.put("id_usuario", imagem.getId_usuario());
                imagemJson.put("data_criacao", imagem.getData_criacao() != null ? imagem.getData_criacao().toString() : "");

                imagensArray.put(imagemJson);
            }

            jsonResponse.put("imagens", imagensArray);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();

        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar imagens da abordagem!";
            logger.error(msg, e);
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void obterImagemAbordagem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String caminhoArquivo = request.getParameter("caminho");

        if (caminhoArquivo == null || caminhoArquivo.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Caminho do arquivo não informado!");
            return;
        }

        try {
            File arquivo = new File(caminhoArquivo);

            if (!arquivo.exists() || !arquivo.isFile()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Arquivo não encontrado!");
                return;
            }

            String nomeArquivo = arquivo.getName();
            String extensao = nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1).toLowerCase();

            String contentType;
            switch (extensao) {
                case "jpg":
                case "jpeg":
                    contentType = "image/jpeg";
                    break;
                case "png":
                    contentType = "image/png";
                    break;
                default:
                    contentType = "application/octet-stream";
            }

            response.setContentType(contentType);
            response.setHeader(
                "Content-Disposition",
                "inline; filename=\"" + nomeArquivo + "\""
            );
            response.setContentLength((int) arquivo.length());

            try (InputStream in = new FileInputStream(arquivo);
                OutputStream out = response.getOutputStream()) {

                byte[] buffer = new byte[4096];
                int bytesRead;

                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

        } catch (Exception e) {
            logger.error("Erro ao obter imagem da abordagem", e);
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Erro ao processar arquivo"
            );
        }
    }

    private void listarTiposBlitz(HttpServletResponse response) throws IOException {
        try {
            List<TipoBlitz> tipos = Blitzes.listarTiposBlitz();
            enviarRespostaTiposBlitzJSON(response, tipos);
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar tipos de blitz!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void enviarRespostaTiposBlitzJSON(HttpServletResponse response, List<TipoBlitz> tipos) throws Exception {
        JSONObject jsonResponse = new JSONObject();
        JSONArray tiposArray = new JSONArray();
        
        for (TipoBlitz tipo : tipos) {
            JSONObject tipoJson = new JSONObject();
            tipoJson.put("id", tipo.getId());
            tipoJson.put("codigo", tipo.getCodigo());
            tipoJson.put("descricao", tipo.getDescricao());
            tiposArray.put(tipoJson);
        }
        
        jsonResponse.put("tiposBlitz", tiposArray);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(jsonResponse.toString());
        response.getWriter().flush();
    }

    private void listarResultadosAbordagem(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json;charset=UTF-8");

        JSONArray jsonArray = new JSONArray();

        try {
            List<BlitzAbordagemResultado> resultados =
                Blitzes.listarResultadosAbordagem();

            for (BlitzAbordagemResultado r : resultados) {
                JSONObject obj = new JSONObject();
                obj.put("id", r.getId());
                obj.put("descricao", r.getDescricao());
                obj.put("id_status", r.getIdStatus());
                jsonArray.put(obj);
            }

            response.getWriter().write(
                new JSONObject().put("resultados", jsonArray).toString()
            );

        } catch (Exception e) {
            logger.error("Erro ao listar resultados da abordagem", e);
        }
    }

    private void obterInformacoesVeiculoPorPlaca(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String placa = request.getParameter("placa");
            
            if (placa == null || placa.isEmpty()) {
                enviarRespostaJSON(response, false, "Placa não informada!");
                return;
            }
            
            Map<String, String> veiculoInfo = Blitzes.obterInformacoesVeiculoPorPlaca(placa);
            
            if (veiculoInfo != null) {
                JSONObject jsonResponse = new JSONObject();
                JSONObject veiculoJson = new JSONObject();
                
                veiculoJson.put("marca", veiculoInfo.get("marca"));
                veiculoJson.put("modelo", veiculoInfo.get("modelo"));
                veiculoJson.put("tipo", veiculoInfo.get("tipo"));
                veiculoJson.put("cor", veiculoInfo.get("cor"));
                
                jsonResponse.put("veiculo", veiculoJson);
                jsonResponse.put("sucesso", true);
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
                response.getWriter().flush();
            } else {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("sucesso", true);
                jsonResponse.put("mensagem", "Veículo não encontrado ou informações não disponíveis");
                jsonResponse.put("veiculo", new JSONObject());
                
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write(jsonResponse.toString());
                response.getWriter().flush();
            }
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao buscar informações do veículo!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void geocodingMapa(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String endereco = request.getParameter("endereco");
        PrintWriter out = response.getWriter();

        if (endereco == null || endereco.trim().isEmpty()) {
            out.write("{}");
            return;
        }

        try {
            JSONObject resultado = Blitzes.realizarGeocoding(endereco);

            if (resultado != null) {
                out.write(resultado.toString());
            } else {
                out.write("{}");
            }
        } catch (Exception e) {
            out.write("{}");
        }
    }

    private void listarIdsLocaisBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idBlitzStr = request.getParameter("idBlitz");
            
            if (idBlitzStr == null || idBlitzStr.isEmpty()) {
                enviarRespostaJSON(response, false, "ID da blitz não informado!");
                return;
            }
            
            int idBlitz = Integer.parseInt(idBlitzStr);
            List<Integer> idsLocais = Blitzes.listarIdsLocaisBlitz(idBlitz);
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray idsLocaisArray = new JSONArray();
            
            for (Integer idLocal : idsLocais) {
                idsLocaisArray.put(idLocal);
            }
            
            jsonResponse.put("idsLocais", idsLocaisArray);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            String msg = "Ocorreu um erro ao listar IDs dos locais da blitz!";
            logger.error(msg, e);    
            enviarRespostaJSON(response, false, msg);
        }
    }

    private void obterPassagensReaisBlitz(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String idBlitzStr = request.getParameter("idBlitz");
            String dataReferenciaStr = request.getParameter("dataReferencia");
            
            if (idBlitzStr == null || idBlitzStr.isEmpty()) {
                enviarRespostaJSON(response, false, "ID da blitz não informado!");
                return;
            }
            
            int idBlitz = Integer.parseInt(idBlitzStr);
            
            // Pega os locais da blitz
            List<Integer> idsLocais = Blitzes.listarIdsLocaisBlitz(idBlitz);
            
            if (idsLocais.isEmpty()) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("passagens", new JSONArray());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(jsonResponse.toString());
                return;
            }
            
            Date dataReferencia = null;
            if (dataReferenciaStr != null && !dataReferenciaStr.isEmpty()) {
                try {
                    dataReferencia = java.sql.Timestamp.from(java.time.Instant.parse(dataReferenciaStr));
                } catch (Exception e) {
                    dataReferencia = new Date();
                }
            } else {
                dataReferencia = new Date();
            }
            
            List<Map<String, Object>> passagens = Blitzes.obterPassagensReaisBlitz(idsLocais, dataReferencia);
            
            JSONObject jsonResponse = new JSONObject();
            JSONArray passagensArray = new JSONArray();
            
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            
            for (Map<String, Object> passagem : passagens) {
                JSONObject passagemJson = new JSONObject();
                
                for (Map.Entry<String, Object> entry : passagem.entrySet()) {
                    if (entry.getValue() instanceof Date) {
                        passagemJson.put(entry.getKey(), sdf.format((Date) entry.getValue()));
                    } else if (entry.getValue() instanceof Timestamp) {
                        passagemJson.put(entry.getKey(), sdf.format(new Date(((Timestamp) entry.getValue()).getTime())));
                    } else {
                        passagemJson.put(entry.getKey(), entry.getValue());
                    }
                }
                
                passagensArray.put(passagemJson);
            }
            
            jsonResponse.put("passagens", passagensArray);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(jsonResponse.toString());
            response.getWriter().flush();
            
        } catch (Exception e) {
            logger.error("Erro ao obter passagens reais para blitz", e);
            enviarRespostaJSON(response, false, "Erro interno no servidor.");
        }
    }
}