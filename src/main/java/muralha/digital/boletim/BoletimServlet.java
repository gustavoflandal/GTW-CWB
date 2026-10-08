package muralha.digital.boletim;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;

import muralha.digital.util.Paginacao;
import muralha.digital.util.RespostaRequisicaoXML;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
@WebServlet("/MuralhaDigital/Boletim")
public class BoletimServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(BoletimServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	private static String UPLOAD_DIRECTORY = "";

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    request.setCharacterEncoding("UTF-8");
	    response.setCharacterEncoding("UTF-8");
	    response.setContentType("application/xml; charset=UTF-8");
	    final Acesso acesso = new Acesso(request, response, true);
	    if (!acesso.verificaAcesso()) {
	        new Mensagem(response).showErro("Usuário não autenticado!", "/login/abertura-sistemas.jsp");
	        return;
	    }

	    try {
	        Map<String, Object> dados = lerJson(request);
	        BoletimLocal boletimLocal = mapearBoletimLocal(dados);
	        Integer boletimLocalId = BoletimLocais.inserirBoletimLocal(boletimLocal);

	        Boletim boletim = mapearBoletim(dados, acesso.getUsuario().getId());
	        boletim.setIdLocal(boletimLocalId);
	        Integer boletimId = Boletins.inserirBoletim(boletim);

	        List<Map<String, Object>> documentos = extrairLista(dados, "documentos");
	        List<Map<String, Object>> individuos = extrairLista(dados, "individuos");
	        List<Map<String, Object>> veiculos = extrairLista(dados, "veiculos");
	        List<Map<String, Object>> apreensoes = extrairLista(dados, "apreensoes");

	        if (documentos != null && !documentos.isEmpty()) {
	        	ObterDir();
	            List<BoletimDocumento> listaDocumentos = mapearBoletinsDocumentoComSalvamento(documentos, boletimId);
	            boolean sucessoDocumentos = BoletimDocumentos.inserirListaBoletimDocumento(listaDocumentos);

	            if (!sucessoDocumentos) {
	                logger.warn("Alguns documentos não foram inseridos corretamente.");
	            }
	        }

	        if (apreensoes != null && !apreensoes.isEmpty()) {
	            List<BoletimApreensao> listaApreensoes = mapearBoletinsApreensao(apreensoes, boletimId);
	            boolean sucessoApreensoes = BoletimApreensoes.inserirListaBoletimApreensao(listaApreensoes);
	            if (!sucessoApreensoes) {
	                logger.warn("Algumas apreensões não foram inseridas corretamente.");
	            }
	        }

	        if (veiculos != null && !veiculos.isEmpty()) {
	            List<BoletimVeiculo> listaVeiculos = mapearBoletinsVeiculo(veiculos, boletimId);
	            boolean sucessoVeiculos = BoletimVeiculos.inserirListaBoletimVeiculo(listaVeiculos);
	            if (!sucessoVeiculos) {
	                logger.warn("Alguns veículos não foram inseridos corretamente.");
	            }
	        }

	        if (individuos != null && !individuos.isEmpty()) {
	            List<BoletimIndividuo> listaIndividuos = mapearBoletinsIndividuo(individuos, boletimId);
	            boolean sucessoIndividuos = BoletimIndividuos.inserirListaBoletimIndividuo(listaIndividuos);
	            if (!sucessoIndividuos) {
	                logger.warn("Alguns indivíduos não foram inseridos corretamente.");
	            }
	        }

	        enviarMensagemXML(response, true, "Boletim cadastrado com sucesso! ID do boletim: " + boletimId);

	    } catch (NumberFormatException e) {
	        logger.error("Erro de conversão numérica: ", e);
	        enviarMensagemXML(response, false, "Erro no formato dos dados numéricos");
	    } catch (ClassCastException e) {
	        logger.error("Erro de tipo nos dados: ", e);
	        enviarMensagemXML(response, false, "Estrutura dos dados incorreta");
	    } catch (Exception e) {
	        logger.error("Erro ao cadastrar boletim: ", e);
	        enviarMensagemXML(response, false, "Ocorreu um erro ao cadastrar boletim");
	    }
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Validando acesso do usuário
		final Acesso acessoUsuario = new Acesso(request, response, true);
		if (!acessoUsuario.verificaAcesso()) {
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}

		try {
			String msg = null;
			String strAcao = request.getParameter("acao");

			if (strAcao == null || strAcao.equals("")) {
				msg = "Ação não informada!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			if (strAcao.equals("obterLista")) {
				ObterTodos(request, response);
			} else if (strAcao.equals("obterPorId")) {
				ObterPorId(request, response);
			} else if (strAcao.equals("obterListaPorIds")) {
				ObterListaPorIds(request, response);
			} else {
				msg = "Ação não reconhecida!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar boletins!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	protected void ObterTodos(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Paginacao paginacao = new Paginacao(request);
			if (!paginacao.OperacaoValida()) {
				String msg = "Dados de paginação não informados corretamente!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dataIni = null, dataFim = null;
			Integer idSituacao = null, idTipoOcorrencia = null, idCidade = null;
			String placa = null, cpf = null;
			String strDataIni = request.getParameter("dataIni");
			String strDataFim = request.getParameter("dataFim");
			String strSituacao = request.getParameter("situacao");
			String strPlaca = request.getParameter("placa");
			String strCpf = request.getParameter("cpf");
			String strCidade = request.getParameter("cidade");
			String strTipoOcorrencia = request.getParameter("tipoOcorrencia");

			if (strDataIni != null && !strDataIni.trim().equals("")) {
				dataIni = sdf.parse(strDataIni);
			}

			if (strDataFim != null && !strDataFim.trim().equals("")) {
				dataFim = sdf.parse(strDataFim);
			}

			if (strSituacao != null && !strSituacao.trim().equals("") && !strSituacao.trim().equals("0")) {
				idSituacao = Integer.parseInt(strSituacao);
			}

			if (strTipoOcorrencia != null && !strTipoOcorrencia.trim().equals("")
					&& !strTipoOcorrencia.trim().equals("0")) {
				idTipoOcorrencia = Integer.parseInt(strTipoOcorrencia);
			}

			if (strPlaca != null && !strPlaca.trim().equals("")) {
				placa = strPlaca.trim();
			}

			if (strCpf != null && !strCpf.trim().equals("")) {
				cpf = strCpf.trim();
			}

			if (strCidade != null && !strCidade.trim().equals("")) {
				idCidade = Integer.parseInt(strCidade);
			}

			Boletins boletins = Boletins.ObterListaBoletins(dataIni, dataFim, idSituacao, idTipoOcorrencia, placa, cpf,
					idCidade, paginacao);
			EnviarRespostaXML(response, boletins);

		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar lista de boletins!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	protected void ObterPorId(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    try {
	        String strId = request.getParameter("id");
	        Integer id;

	        if (strId == null || strId.trim().equals("") || strId.trim().equals("0")) {
	            String msg = "Identificador do registro não informado!";
	            logger.error(msg);
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	            return;
	        }

	        try {
	            id = Integer.parseInt(strId);
	        } catch (Exception e) {
	            String msg = "Erro ao preparar dados para consulta!";
	            logger.error(msg, e);
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	            return;
	        }

	        // Chama o método que busca o boletim pelo ID
	        Boletins boletim = Boletins.ObterBoletimPorId(id);

	        if (boletim == null) {
	            String msg = "Boletim não encontrado!";
	            logger.warn(msg);
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	            return;
	        }

	        // Serializa o boletim em XML e envia como resposta
	        EnviarRespostaXML(response, boletim);

	    } catch (Exception e) {
	        String msg = "Ocorreu um erro ao consultar boletim!";
	        logger.error(msg, e);
	        respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    }
	}
	
	protected void ObterListaPorIds(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {
	    try {
	        String strIds = request.getParameter("boletimIds");

	        if (strIds == null || strIds.trim().isEmpty()) {
	            String msg = "Parâmetro 'boletimIds' não informado!";
	            logger.error(msg);
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	            return;
	        }

	        // Divide os IDs por vírgula e tenta converter para Integer (ou UUID se for o caso)
	        List<Integer> ids;
	        try {
	            ids = Arrays.stream(strIds.split(","))
	                    .map(String::trim)
	                    .filter(s -> !s.isEmpty())
	                    .map(Integer::valueOf) // ou UUID::fromString se forem GUIDs
	                    .collect(Collectors.toList());
	        } catch (Exception e) {
	            String msg = "Erro ao converter os boletimIds!";
	            logger.error(msg, e);
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	            return;
	        }

	        // Busca os boletins usando os IDs
	        Boletins boletins = Boletins.ObterBoletinsPorIds(ids);

	        if (boletins == null) {
	            String msg = "Nenhum boletim encontrado!";
	            logger.warn(msg);
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	            return;
	        }

	        // Serializa os boletins em XML
	        EnviarRespostaXML(response, boletins);

	    } catch (Exception e) {
	        String msg = "Ocorreu um erro ao consultar os boletins!";
	        logger.error(msg, e);
	        respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    }
	}

	private void EnviarRespostaXML(HttpServletResponse response, Boletins boletins) throws JAXBException, IOException {
		JAXBContext context;
		try {
			// Formando dados para envio
			context = JAXBContext.newInstance(Boletins.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marsHall.marshal(boletins, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

			logger.info("EnviarRespostaXML():: Registros enviados: "
					+ Integer.toString(boletins.getListaBoletins().size()));
			boletins = null;

		} catch (Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de boletim!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	private void enviarMensagemXML(HttpServletResponse response, boolean sucesso, String msg) {
		RespostaRequisicaoXML resposta = new RespostaRequisicaoXML();
		resposta.setSucesso(sucesso);
		resposta.setMsgResposta(msg);

		// Formando dados para envio
		JAXBContext evidencia_context;
		try {
			evidencia_context = JAXBContext.newInstance(RespostaRequisicaoXML.class);
			Marshaller marshaller = evidencia_context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marshaller.marshal(resposta, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

		} catch (Exception e) {
			String msgErro = "Erro gravíssimo ao preparar resposta a requisição! " + e.getMessage();
			logger.error(msgErro, e);
			return;
		}
	}

	@SuppressWarnings("unchecked")
	private Boletim mapearBoletim(Map<String, Object> dados, int idUsuario) {
		Map<String, Object> boletimMap = (Map<String, Object>) dados.get("boletim");

		Boletim boletim = new Boletim();
		boletim.setIdTipo(parseInt(boletimMap.get("idTipo")));
		boletim.setIdSituacao(parseInt(boletimMap.get("idSituacao")));
		boletim.setDetalhamento(String.valueOf(boletimMap.get("detalhamento")));
		boletim.setPermitirAtendimento(parseInt(boletimMap.get("permite_atendimento")));
		boletim.setIdUsuario(idUsuario);
		boletim.setDataCriacao(new Date());

		return boletim;
	}

	@SuppressWarnings("unchecked")
	private BoletimLocal mapearBoletimLocal(Map<String, Object> dados) {
		Map<String, Object> boletimMap = (Map<String, Object>) dados.get("boletim");

		BoletimLocal boletimLocal = new BoletimLocal();
		boletimLocal.setIdCidade(parseInt(boletimMap.get("id_cidade")));
		boletimLocal.setBairro(String.valueOf(boletimMap.get("bairro")));
		boletimLocal.setRua(String.valueOf(boletimMap.get("rua")));
		boletimLocal.setNumero(parseInt(boletimMap.get("numero")));
		boletimLocal.setComplemento(String.valueOf(boletimMap.get("complemento")));
		return boletimLocal;
	}

	private List<BoletimApreensao> mapearBoletinsApreensao(List<Map<String, Object>> apreensoes, int boletimId) {
		List<BoletimApreensao> lista = new ArrayList<>();

		for (Map<String, Object> item : apreensoes) {
			BoletimApreensao ap = new BoletimApreensao();
			ap.setIdBoletim(boletimId);
			ap.setTipo((String) item.get("tipo"));
			ap.setDescricao((String) item.get("descricao"));
			lista.add(ap);
		}
		return lista;
	}

	private List<BoletimVeiculo> mapearBoletinsVeiculo(List<Map<String, Object>> dados, Integer idBoletim) {
		List<BoletimVeiculo> veiculos = new ArrayList<>();
		for (Map<String, Object> map : dados) {
			BoletimVeiculo v = new BoletimVeiculo();
			v.setIdBoletim(idBoletim);
			v.setPlaca((String) map.get("placa"));
			v.setCor((String) map.get("cor"));
			v.setMarca((String) map.get("marca"));
			v.setModelo((String) map.get("modelo"));
			veiculos.add(v);
		}
		return veiculos;
	}

	private List<BoletimIndividuo> mapearBoletinsIndividuo(List<Map<String, Object>> lista, Integer boletimId) {
		List<BoletimIndividuo> resultado = new ArrayList<>();

		for (Map<String, Object> item : lista) {
			BoletimIndividuo individuo = new BoletimIndividuo();
			individuo.setIdBoletim(boletimId);
			individuo.setIdTipoEnvolvimento(parseInt(item.get("id_tipo_envolvimento")));
			individuo.setDetalheEnvolvimento(item.get("detalhe_envolvimento").toString());
			individuo.setNome(item.get("nome").toString());
			individuo.setCpf(item.get("cpf").toString());
			resultado.add(individuo);
		}
		return resultado;
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> extrairLista(Map<String, Object> dados, String chave) {
		return (List<Map<String, Object>>) dados.getOrDefault(chave, Collections.emptyList());
	}

	private Map<String, Object> lerJson(HttpServletRequest request) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(request.getReader(), new TypeReference<Map<String, Object>>() {
		});
	}

	private int parseInt(Object valor) {
		return Integer.parseInt(String.valueOf(valor));
	}

	private List<BoletimDocumento> mapearBoletinsDocumentoComSalvamento(List<Map<String, Object>> dados, Integer idBoletim) throws IOException {
	    List<BoletimDocumento> documentos = new ArrayList<>();
	    for (Map<String, Object> doc : dados) {
	        String tipo = (String) doc.get("tipo");
	        String nome = (String) doc.get("nome");
	        String detalhamento = (String) doc.get("detalhamento");
	        String conteudoBase64 = (String) doc.get("conteudoBase64");

	        // Salvar o arquivo em disco
	        String caminhoArquivo = salvarArquivoBoletim(idBoletim, nome, conteudoBase64);

	        // Montar o objeto BoletimDocumento
	        BoletimDocumento bd = new BoletimDocumento();
	        bd.setIdBoletim(idBoletim);
	        bd.setTipo(tipo);
	        bd.setDetalhamento(detalhamento);
	        bd.setDirArquivo(caminhoArquivo);

	        documentos.add(bd);
	    }

	    return documentos;
	}
	
	private static String salvarArquivoBoletim(Integer idBoletim, String nomeOriginal, String conteudoBase64) throws IOException {
	    if (nomeOriginal == null || nomeOriginal.trim().isEmpty()) {
	        throw new IllegalArgumentException("Nome do arquivo inválido");
	    }

	    if (conteudoBase64.contains(",")) {
	        conteudoBase64 = conteudoBase64.substring(conteudoBase64.indexOf(",") + 1);
	    }

	    byte[] dados = Base64.getDecoder().decode(conteudoBase64);

	    String pastaBoletim = UPLOAD_DIRECTORY + File.separator + "boletim_" + idBoletim + File.separator +
	            new SimpleDateFormat("dd_MM_yyyy").format(new Date());

	    File diretorio = new File(pastaBoletim);
	    if (!diretorio.exists()) {
	        diretorio.mkdirs();
	    }

	    String caminhoCompleto = pastaBoletim + File.separator + nomeOriginal;
	    try (FileOutputStream fos = new FileOutputStream(caminhoCompleto)) {
	        fos.write(dados);
	    }

	    return caminhoCompleto;
	}
	
	private void ObterDir()
	{
		boolean possuiDir = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_justificativa") != null && ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_justificativa") != "";
		if(possuiDir) {
			UPLOAD_DIRECTORY =  ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_justificativa");
		}else {
			UPLOAD_DIRECTORY =
			        System.getProperty("user.home") + File.separator + "Desktop" + File.separator + "BoletimDocumentos";
		}
		logger.info(UPLOAD_DIRECTORY);
	}
}