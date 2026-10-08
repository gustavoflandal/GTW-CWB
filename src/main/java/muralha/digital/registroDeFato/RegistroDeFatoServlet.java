package muralha.digital.registroDeFato;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import muralha.digital.monitorado.VeiculoMonitorado;
import muralha.digital.monitorado.VeiculosMonitorados;
import muralha.digital.util.Paginacao;
import muralha.digital.util.RespostaRequisicaoXML;

@WebServlet("/MuralhaDigital/RegistroDeFato")
public class RegistroDeFatoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(RegistroDeFatoServlet.class);
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
			String msg = null;
			String strAcao = request.getParameter("acao");

			if (strAcao == null || strAcao.equals("")) {
				msg = "Ação não informada!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			if (strAcao.equals("cadastrarSemBoletim")) {
				CadastrarRegistroFatoSemBoletim(request, response, acesso.getUsuario().getId());
			} else if (strAcao.equals("cadastrarComBoletim")) {
				CadastrarRegistroFatoCompleto(request, response, acesso.getUsuario().getId());
			} else if (strAcao.equals("edicaoComBoletim")) {
				EdicaoRegistroFatoComBoletim(request, response, acesso.getUsuario().getId());
			} else if (strAcao.equals("atualizarSemBoletim")) {
				AtualizarRegistroFatoSemBoletim(request, response, acesso.getUsuario().getId());
			} else if (strAcao.equals("cadastrarPassagem")) {
				InserirPassagemVeiculo(request, response, acesso.getUsuario().getId());
			} else if (strAcao.equals("EncerrarMonitoramento")) {
				EncerrarMonitoramento(request, response);
			}

		} catch (NumberFormatException e) {
			logger.error("Erro de conversão numérica: ", e);
			enviarMensagemXML(response, false, "Erro no formato dos dados numéricos");
		} catch (ClassCastException e) {
			logger.error("Erro de tipo nos dados: ", e);
			enviarMensagemXML(response, false, "Estrutura dos dados incorreta");
		} catch (Exception e) {
			logger.error("Erro ao cadastrar Registro de Fato: ", e);
			enviarMensagemXML(response, false, "Ocorreu um erro ao cadastrar Registro de Fato");
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

			if (strAcao.equals("obterListaComBoletim")) {
				ObterTodosComBoletim(request, response, acessoUsuario.getUsuario().getId());
			} else if (strAcao.equals("obterListaSemBoletim")) {
				ObterTodosSemBoletim(request, response, acessoUsuario.getUsuario().getId());
			} else if (strAcao.equals("buscarFatoSemBoletimPorId")) {
				BuscarFatoSemBoletimPorId(request, response, acessoUsuario.getUsuario().getId());
			} else if (strAcao.equals("buscarFatoComBoletimPorId")) {
				BuscarFatoComBoletimPorId(request, response, acessoUsuario.getUsuario().getId());
			} else if (strAcao.equals("obterTodos")) {
				ObterTodos(request, response, acessoUsuario.getUsuario().getId());
			} else if (strAcao.equals("buscarFatoAmbosPorId")) {
				BuscarFatoPorId(request, response, acessoUsuario.getUsuario().getId());
			} else if (strAcao.equals("consultarVeiculosMonitorados")) {
				BuscarVeiculosPorIdRegistroFato(request, response);
			} else {
				msg = "Ação não reconhecida!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar Registros De Fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	protected void ObterTodosComBoletim(HttpServletRequest request, HttpServletResponse response,
			Integer idUsuarioLogado) throws ServletException, IOException {
		try {
			Paginacao paginacao = new Paginacao(request);
			if (!paginacao.OperacaoValida()) {
				String msg = "Dados de paginação não informados corretamente!";
				logger.error(msg);
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			SimpleDateFormat dateFormatIso = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			Date dataIni = null, dataFim = null, dataIniAlteracao = null, dataFimAlteracao = null, dataIniFato = null, dataFimFato = null;
			Integer idSituacao = null, idTipoOcorrencia = null, idCidade = null, origemBoletim = null, comBoletim = null, infoFaltante = null, 
					incluirVeiculos = null, incluirMonitorados = null, tipoRegistro = null, 
					naturezaRegistro = null, operadorCadastro = null,  chkFiltrosAvancados = null, tipoAcesso = null, acessoPermitido = null;
			String placa = null, cpf = null, nomeEnvolvido = null, filtroObjeto = null;
			String strDataIni = request.getParameter("dataIni");
			String strDataFim = request.getParameter("dataFim");
			String strSituacao = request.getParameter("situacao");
			String strPlaca = request.getParameter("placa");
			String strCpf = request.getParameter("cpf");
			String strCidade = request.getParameter("cidade");
			
			String strTipoOcorrencia = request.getParameter("tipoOcorrencia");
			String strOrigemBoletim = request.getParameter("origemBoletim");
			String strlInfoFaltante = request.getParameter("infoFaltante");
			String strIncluirVeiculos = request.getParameter("incluirVeiculos");
			String strIncluirMonitorados = request.getParameter("incluirMonitorados");
			String strFiltroObjeto = request.getParameter("filtroObjeto");
			String strComBoletim = request.getParameter("comBoletim");
			String strTipoRegistro = request.getParameter("tipoRegistro");
			String strNaturezaRegistro = request.getParameter("naturezaRegistro");
			String strNomeOperador = request.getParameter("nomeOperador");
			String strNomeEnvolvido = request.getParameter("nomeEnvolvido");
			String strDataIniAlteracao = request.getParameter("dataIniAlteracao");
			String strDataFimAlteracao = request.getParameter("dataFimAlteracao");
			String strChkFiltrosAvancados = request.getParameter("chkFiltrosAvancados");
			String strDataIniFato = request.getParameter("dataIniFato");
			String strDataFimFato = request.getParameter("dataFimFato");
			String strTipoAcesso = request.getParameter("tipoAcesso");
			String strAcessoPermitido = request.getParameter("acessoPermitido");

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
			
			if(strOrigemBoletim != null && !strOrigemBoletim.trim().equals("")) {
				origemBoletim = Integer.parseInt(strOrigemBoletim);
			}
			
			if(strComBoletim != null && !strComBoletim.trim().equals("")) {
				if(strComBoletim.trim().equals("COM_BOLETIM")) {
					comBoletim = 1;
				}else {
					comBoletim = 0;
				}
			}
			
			if(strlInfoFaltante != null && !strlInfoFaltante.trim().equals("")) {
				infoFaltante = Integer.parseInt(strlInfoFaltante);
			}

			if(strIncluirVeiculos != null && !strIncluirVeiculos.trim().equals("")) {
				incluirVeiculos = Integer.parseInt(strIncluirVeiculos);
			}
			
			if(strIncluirMonitorados != null && !strIncluirMonitorados.trim().equals("")) {
				incluirMonitorados = Integer.parseInt(strIncluirMonitorados);
			}
			
			if(strNomeEnvolvido != null && !strNomeEnvolvido.trim().equals("")) {
				nomeEnvolvido = strNomeEnvolvido;
			}
			
			if(strNomeOperador != null && !strNomeOperador.trim().equals("")) {
				operadorCadastro = Integer.parseInt(strNomeOperador);
			}
			
			if(strTipoRegistro != null && !strTipoRegistro.trim().equals("")) {
				tipoRegistro = Integer.parseInt(strTipoRegistro);
			}
			
			if(strNaturezaRegistro != null && !strNaturezaRegistro.trim().equals("")) {
				naturezaRegistro = Integer.parseInt(strNaturezaRegistro);
			}
			
			if(strFiltroObjeto != null && !strFiltroObjeto.trim().equals("")) {
				filtroObjeto = strFiltroObjeto;
			}
			
			if(strDataIniAlteracao != null && !strDataIniAlteracao.trim().equals("")) {										
				dataIniAlteracao = dateFormatIso.parse(strDataIniAlteracao);
			}
			
			if(strDataFimAlteracao != null && !strDataFimAlteracao.trim().equals("")) {										
				dataFimAlteracao = dateFormatIso.parse(strDataFimAlteracao);
			}
			
			if(strChkFiltrosAvancados != null && !strChkFiltrosAvancados.trim().equals("")) {										
				chkFiltrosAvancados = Integer.parseInt(strChkFiltrosAvancados);
			}
			
			if(strDataIniFato != null && !strDataIniFato.trim().equals("")) {										
				dataIniFato = dateFormatIso.parse(strDataIniFato);
			}
			
			if(strDataFimFato != null && !strDataFimFato.trim().equals("")) {										
				dataFimFato = dateFormatIso.parse(strDataFimFato);
			}
			
			if(strTipoAcesso != null && !strTipoAcesso.trim().equals("")) {
				tipoAcesso = Integer.parseInt(strTipoAcesso);
			}
			
			if(strAcessoPermitido != null && !strAcessoPermitido.trim().equals("")) {
				acessoPermitido = Integer.parseInt(strAcessoPermitido);
			}
			
	        // --- parâmetros de ordenação ---
	        String strOrderBy = request.getParameter("orderBy");
	        String strOrderDir = request.getParameter("orderDir");

	        // Defaults seguros
	        String orderBy = "dataCriacao";
	        String orderDir = "DESC";

	        if (strOrderBy != null && !strOrderBy.trim().isEmpty()) {
	            orderBy = strOrderBy.trim();
	        }

	        if (strOrderDir != null && !strOrderDir.trim().isEmpty()) {
	            String up = strOrderDir.trim().toUpperCase();
	            if ("ASC".equals(up) || "DESC".equals(up)) {
	                orderDir = up;
	            } // senão ignora e mantem default
	        }

			RegistroDeFatos registroDeFatos = RegistroDeFatos.ObterListaRegistroDeFatos(dataIni, dataFim,
					idSituacao, idTipoOcorrencia, placa, cpf, idCidade, origemBoletim, comBoletim, infoFaltante, incluirVeiculos, incluirMonitorados, 
					nomeEnvolvido, operadorCadastro, tipoRegistro, naturezaRegistro, filtroObjeto, 
					dataIniAlteracao, dataFimAlteracao, chkFiltrosAvancados, dataIniFato, dataFimFato, tipoAcesso, acessoPermitido, idUsuarioLogado, paginacao, orderBy, orderDir );
			EnviarRespostaXML(response, registroDeFatos);

		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar lista de Registro De Fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	protected void ObterTodosSemBoletim(HttpServletRequest request, HttpServletResponse response,
			Integer idUsuarioLogado) throws ServletException, IOException {
		try {
			RegistroDeFatos result = new RegistroDeFatos();
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

			RegistroDeFatos registroDeFatos = RegistroDeFatos.ObterListaRegistroDeFatosSemBoletim(dataIni, dataFim,
					idSituacao, idTipoOcorrencia, placa, cpf, idCidade, idUsuarioLogado, paginacao);

			EnviarRespostaXML(response, registroDeFatos);

		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar lista de Registro De Fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	protected void ObterTodos(HttpServletRequest request, HttpServletResponse response, Integer idUsuarioLogado)
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
			String placa = null, cpf = null, nome = null;
			String strDataIni = request.getParameter("dataIni");
			String strDataFim = request.getParameter("dataFim");
			String strPlaca = request.getParameter("placa");
			String strCpf = request.getParameter("cpf");
			String strNome = request.getParameter("nome");

			if (strDataIni != null && !strDataIni.trim().equals("")) {
				dataIni = sdf.parse(strDataIni);
			}

			if (strDataFim != null && !strDataFim.trim().equals("")) {
				dataFim = sdf.parse(strDataFim);
			}

			if (strPlaca != null && !strPlaca.trim().equals("")) {
				placa = strPlaca.trim();
			}

			if (strCpf != null && !strCpf.trim().equals("")) {
				cpf = strCpf.trim();
			}

			if (strNome != null && !strNome.trim().equals("")) {
				nome = strNome;
			}

			RegistroDeFatos registroDeFatos = RegistroDeFatos.ObterListaRegistroDeFatos(dataIni, dataFim, nome, placa,
					cpf, idUsuarioLogado, paginacao);
			EnviarRespostaXML(response, registroDeFatos);

		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar lista de Registro De Fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	public static void BuscarFatoSemBoletimPorId(HttpServletRequest request, HttpServletResponse response,
			Integer idUsuarioLogado) throws ServletException, IOException {

		try {
			String paramId = request.getParameter("idRegistroFato");
			if (paramId == null || paramId.isEmpty()) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "ID do registro de fato não informado.");
				return;
			}

			int idRegistroFato;
			try {
				idRegistroFato = Integer.parseInt(paramId);
			} catch (NumberFormatException ex) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "ID do registro de fato inválido.");
				return;
			}

			FatoSemBoletimCompletoDTO fatoCompleto = RegistroDeFatos.ObterFatoSemBoletimPorId(idRegistroFato);

			if (fatoCompleto == null) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "Fato não encontrado.");
				return;
			}

			// Envia o objeto como XML
			EnviarRespostaXMLFatoCompleto(response, fatoCompleto);

		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar o Registro de Fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}

	public static void BuscarFatoComBoletimPorId(HttpServletRequest request, HttpServletResponse response,
			Integer idUsuarioLogado) throws ServletException, IOException {

		try {
			String paramId = request.getParameter("idRegistroFato");
			if (paramId == null || paramId.isEmpty()) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "ID do registro de fato não informado.");
				return;
			}

			int idRegistroFato;
			try {
				idRegistroFato = Integer.parseInt(paramId);
			} catch (NumberFormatException ex) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "ID do registro de fato inválido.");
				return;
			}

			boolean temAcesso = RegistroDeFatos.verificarAcessoRegistroFato(idRegistroFato, idUsuarioLogado);

			if (!temAcesso) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false,
						"Fato não encontrado ou acesso não permitido.");
				return;
			}

			RegistroDeFato fatoCompleto = RegistroDeFatos.ObterRegistroDeFatoPorIdComBoletim(idRegistroFato,
					idUsuarioLogado);

			if (fatoCompleto == null) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "Fato não encontrado.");
				return;
			}

			// Envia o objeto como XML
			EnviarRespostaXMLRegistroDeFato(response, fatoCompleto);

		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar o Registro de Fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}

	public static void CadastrarRegistroFatoSemBoletim(HttpServletRequest request, HttpServletResponse response,
			Integer idUsuarioLogado) throws ServletException, IOException {

		try {

			CadastroRegistroFatoDTO dto = new ObjectMapper().readValue(request.getInputStream(),
					CadastroRegistroFatoDTO.class);

			var idRegistroFato = RegistroDeFatos.CadastrarRegistroFatoSemBoletim(dto.registroFato, idUsuarioLogado);

			if (idRegistroFato > 0) {
				RegistroDeFatos.CadastrarFatoSemBoletim(idRegistroFato, dto.registroFato, idUsuarioLogado);
				RegistroDeFatoEnderecoEventos.CadastrarEndereco(idRegistroFato, dto.localizacao);
				RegistroDeFatos.CadastrarFatoUsuarioGrupo(dto.registroFato.getIdsGrupos(),
						dto.registroFato.getIdsUsuarios(), idRegistroFato);

				if (dto.veiculos != null && !dto.veiculos.isEmpty()) {
					RegistroDeFatoVeiculos.CadastrarVeiculos(idRegistroFato, dto.veiculos);

					for (VeiculoDTO veiculo : dto.veiculos) {

						VeiculoMonitorado veiculoMonitorado = new VeiculoMonitorado();
						var cadastrarMonitorado = veiculo.getCadastrarMonitorado();

						if (cadastrarMonitorado) {

							String dataInicioStr = veiculo.getDataInicio();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							java.util.Date parsedDate = sdf.parse(dataInicioStr);
							java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());

							veiculoMonitorado.setPlaca(veiculo.getPlaca());
							veiculoMonitorado.setIdTipoAlertaOcorrencia(veiculo.getIdTipoAlertaOcorrencia());
							veiculoMonitorado.setDescricao(veiculo.getDescricao());
							veiculoMonitorado.setDataInicio(sqlDate);
							veiculoMonitorado.setIdUsuario(idUsuarioLogado);
							veiculoMonitorado.setNome(veiculo.getModelo());
							veiculoMonitorado.setAtivo(true);
							veiculoMonitorado.setPrivado(dto.registroFato.getPrivado() == 1 ? true : false);
							veiculoMonitorado.setIdRegistroFato(idRegistroFato);
							VeiculosMonitorados.InserirVeiculoMonitorado(veiculoMonitorado);
						}
					}
				}

				if (dto.envolvidos != null && !dto.envolvidos.isEmpty()) {
					RegistroDeFatoIndividuos.CadastrarIndividuos(idRegistroFato, dto.envolvidos);
				}

				if (dto.objetos != null && !dto.objetos.isEmpty()) {
					RegistroDeFatoVeiculos.CadastrarObjetos(idRegistroFato, dto.objetos);
				}
				
				if (dto.getRegistroFato().getAnotacoes() != null && !dto.getRegistroFato().getAnotacoes().isEmpty()) {
				    try {
				    	processarAnotacoesRegistroFato(dto.getRegistroFato().getAnotacoes(),idRegistroFato,idUsuarioLogado);

				    } catch (Exception ex) {
				        logger.error("Erro ao cadastrar anotacoes do registro " + idRegistroFato, ex);
				    }
				}
				
				// grava historico
				RegistroDeFato anterior = RegistroDeFatos.ObterRegistroDeFatoPorId(idRegistroFato, idUsuarioLogado);
				FatoHistoricoService historicoService = new FatoHistoricoService();
				historicoService.adicionarRegistroFatoHistorico(idRegistroFato, "INSERT", idUsuarioLogado, anterior);
			}

		} catch (Exception e) {
			String msg = "Ocorreu um erro ao cadastrar o fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	private void CadastrarRegistroFatoCompleto(HttpServletRequest request, HttpServletResponse response,
			int idUsuario) throws IOException {

		try {
			
			Boolean cadastrarBoletim = Integer.parseInt(request.getParameter("comBoletim")) == 1 ? true : false;
			
			// 1. Lê o corpo da requisição (JSON)
			StringBuilder jsonBuilder = new StringBuilder();
			try (BufferedReader reader = request.getReader()) {
				String line;
				while ((line = reader.readLine()) != null) {
					jsonBuilder.append(line);
				}
			}

			// 2. Converte o JSON para o DTO principal
			ObjectMapper mapper = new ObjectMapper();
			RegistroDeFatoComBoletimDTO dados = mapper.readValue(jsonBuilder.toString(),
					RegistroDeFatoComBoletimDTO.class);

			// 3. Monta entidade RegistroDeFato a partir do DTO
			RegistroDeFato registroEntity = new RegistroDeFato();
			registroEntity.setIdTipo(dados.getRegistro().getIdTipo());
			registroEntity.setIdStatus(dados.getRegistro().getIdStatus());
			registroEntity.setTemBoletim(dados.getRegistro().getTemBoletim());
			registroEntity.setPrivado(Boolean.TRUE.equals(dados.getRegistro().getPrivado()) ? 1 : 0);
			registroEntity.setIdUsuario(idUsuario);
			registroEntity.setDataCriacao(new Date());
			registroEntity.setDataEvento(dados.getRegistro().getDataEvento());
			registroEntity.setIdNaturezaTipo(dados.getRegistro().getIdNaturezaTipo());
			
			// 4. Insere registro_fato e obtém o ID
			Long idRegistro = RegistroDeFatos.inserirRegistroFato(registroEntity);
	
	        if (cadastrarBoletim) {
	            Integer idBoletim = 0;
	            if (dados.getBoletim() != null) {
	                Boletim boletimEntity = new Boletim();
	                boletimEntity.setIdRegistroFato(idRegistro);
	                boletimEntity.setIdSituacao(dados.getBoletim().getIdSituacao());
	                boletimEntity.setDetalhamento(dados.getBoletim().getDetalhamento());
	                boletimEntity.setPermiteAtendimento(dados.getRegistro().getPermitirAtendimento() ? 1 : 0);
	                boletimEntity.setIdUsuario(idUsuario);
	                boletimEntity.setDataCriacao(new Date());
	                boletimEntity.setDataHoraEvento(dados.getRegistro().getDataEvento());

	                idBoletim = Boletins.inserirBoletim(boletimEntity, idRegistro, idUsuario);

	                if (idBoletim > 0 && dados.getBoletim().getApreensoes() != null) {
	                    for (BoletimApreensaoDTO apreenDTO : dados.getBoletim().getApreensoes()) {
	                        BoletimApreensao apreensao = new BoletimApreensao();
	                        apreensao.setIdBoletim(idBoletim);
	                        apreensao.setTipo(apreenDTO.getTipo());
	                        apreensao.setDescricao(apreenDTO.getDescricao());
	                        Boletins.inserirBoletimApreensao(apreensao);
	                    }
	                }
	            }
	        } else {
	            RegistroFatoDTO fatoDTO = new RegistroFatoDTO();
	            fatoDTO.setAtendimentoPermitido(dados.getRegistro().getPermitirAtendimento() ? 1 : 0);
	            fatoDTO.setEnvolvimentoArmas(0);
	            fatoDTO.setPrivado(Boolean.TRUE.equals(dados.getRegistro().getPrivado()) ? 1 : 0);
	            fatoDTO.setTipoRegistro(dados.getRegistro().getIdTipo());
	            fatoDTO.setIdStatus(dados.getRegistro().getIdStatus());
	            
	            Date dataEventoLocal = dados.getRegistro().getDataEvento();
	            SimpleDateFormat formatador = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	            formatador.setTimeZone(TimeZone.getTimeZone("UTC"));
	            String dataFormatadaEmUTC = formatador.format(dataEventoLocal);
	            fatoDTO.setDataHoraOcorrido(dataFormatadaEmUTC);
	            fatoDTO.setDetalhamentoFato(dados.getRegistro().getDetalhamento());
	            fatoDTO.setEnvolvimentoArmas(Boolean.TRUE.equals(dados.getRegistro().getEnvolvimentoArma()) ? 1 : 0);
	            RegistroDeFatos.CadastrarFato(idRegistro, fatoDTO, idUsuario);
	        }

			if (dados.getIndividuos() != null) {
				for (RegistroDeFatoIndividuoDTO dtoIndividuo : dados.getIndividuos()) {
					RegistroDeFatoIndividuo individuo = new RegistroDeFatoIndividuo();

					individuo.setIdRegistroFato(idRegistro); // Vincula ao registro recém criado

					individuo.setIdTipoEnvolvimento(dtoIndividuo.getIdTipoEnvolvimento());
					individuo.setDetalheEnvolvimento(dtoIndividuo.getDetalheEnvolvimento());
					individuo.setNome(dtoIndividuo.getNome());
					individuo.setCpf(dtoIndividuo.getCpf());
					individuo.setDdd(dtoIndividuo.getDdd());
					individuo.setTelefone(dtoIndividuo.getTelefone());
					individuo.setEmail(dtoIndividuo.getEmail());
					RegistroDeFatoIndividuos.inserirIndividuo(individuo);
				}
			}

			if (dados.getEnderecos() != null) {
				for (RegistroDeFatoEnderecoDTO dtoEndereco : dados.getEnderecos()) {
					RegistroDeFatoEndereco endereco = new RegistroDeFatoEndereco();

					endereco.setIdRegistroFato(idRegistro); // vincula ao registro de fato

					endereco.setIdTipoEvento(dtoEndereco.getIdTipoEvento());
					endereco.setIdCidade(dtoEndereco.getIdCidade());
					endereco.setCep(dtoEndereco.getCep()); // já vem sem máscara
					endereco.setBairro(dtoEndereco.getBairro());
					endereco.setRua(dtoEndereco.getRua());
					endereco.setNumero(dtoEndereco.getNumero());
					endereco.setComplemento(dtoEndereco.getComplemento());
					endereco.setLatitude(dtoEndereco.getLatitude());
					endereco.setLongitude(dtoEndereco.getLongitude());

					RegistroDeFatoEnderecos.inserirEndereco(endereco); // Insere no banco
				}
			}

			if (dados.getVeiculos() != null) {
				for (RegistroDeFatoVeiculoDTO dtoVeiculo : dados.getVeiculos()) {

					RegistroDeFatoVeiculo veiculo = new RegistroDeFatoVeiculo();
					veiculo.setIdRegistroFato(idRegistro);
					veiculo.setPlaca(dtoVeiculo.getPlaca());
					veiculo.setCor(dtoVeiculo.getCor());
					veiculo.setMarca(dtoVeiculo.getMarca());
					veiculo.setModelo(dtoVeiculo.getModelo());
					RegistroDeFatoVeiculos.CadastrarVeiculo(veiculo);

					if (dtoVeiculo.isMonitorado()) {
						try {
							if (!VeiculosMonitorados.existePorPlacaERegistro(dtoVeiculo.getPlaca(), idRegistro)) {

								VeiculoMonitorado cadVeiculoMonitorado = new VeiculoMonitorado();
								cadVeiculoMonitorado.setPlaca(dtoVeiculo.getPlaca());

								try {
									UUID idTipoAlertaUuid = UUID.fromString(dtoVeiculo.getIdTipoAlerta());
									cadVeiculoMonitorado.setIdTipoAlertaOcorrencia(idTipoAlertaUuid);
								} catch (IllegalArgumentException e) {
									logger.error("ID do tipo de alerta inválido: " + dtoVeiculo.getIdTipoAlerta());
								}

								cadVeiculoMonitorado.setDescricao(dtoVeiculo.getDescricao());
								LocalDateTime agora = LocalDateTime.now();
								Date dataAgora = Date.from(agora.atZone(ZoneId.systemDefault()).toInstant());
								cadVeiculoMonitorado.setDataInicio(dataAgora);
								cadVeiculoMonitorado.setDataCadastro(dataAgora);
								cadVeiculoMonitorado.setIdUsuario(idUsuario);

								if (dtoVeiculo.getDataFim() != null && !dtoVeiculo.getDataFim().trim().isEmpty()) {
									try {
										Date dataFim = new SimpleDateFormat("yyyy-MM-dd")
												.parse(dtoVeiculo.getDataFim());
										cadVeiculoMonitorado.setDataFim(dataFim);
									} catch (Exception e) {
										cadVeiculoMonitorado.setDataFim(null);
									}
								}

								if (dtoVeiculo.getDataInicio() != null
										&& !dtoVeiculo.getDataInicio().trim().isEmpty()) {
									try {
										Date dataInicio = new SimpleDateFormat("yyyy-MM-dd")
												.parse(dtoVeiculo.getDataFim());
										cadVeiculoMonitorado.setDataInicio(dataInicio);
									} catch (Exception e) {
										cadVeiculoMonitorado.setDataInicio(dataAgora);
									}
								}

								cadVeiculoMonitorado
										.setNome((dtoVeiculo.getNome() == null || dtoVeiculo.getNome().trim().isEmpty())
												? dtoVeiculo.getDescricaoTipoAlerta() + " - Placa "
														+ dtoVeiculo.getPlaca()
												: dtoVeiculo.getNome());

								cadVeiculoMonitorado.setPrivado(dados.getRegistro().getPrivado());
								cadVeiculoMonitorado.setIdRegistroFato(idRegistro);

								VeiculosMonitorados.InserirVeiculoMonitorado(cadVeiculoMonitorado);
							}
						} catch (Exception e) {
							logger.error("Erro ao processar veículo monitorado: " + dtoVeiculo.getPlaca(), e);
						}
					}
				}
			}

			if (dados.getDocumentos() != null && !dados.getDocumentos().isEmpty()) {
				List<RegistroDeFatoDocumento> listaDocumentos = new ArrayList<>();
				ObterDir();
				for (RegistroDeFatoDocumentoDTO dtoDoc : dados.getDocumentos()) {
					RegistroDeFatoDocumento doc = new RegistroDeFatoDocumento();

					doc.setIdRegistroFato(idRegistro); // Vincula ao registro
					doc.setTipo(dtoDoc.getTipo());
					doc.setDetalhamento(dtoDoc.getDetalhamento());

					// Salvar arquivo em disco e obter caminho
					String caminhoArquivo = salvarArquivoRegistroDeFato(idRegistro, dtoDoc.getNome(),
							dtoDoc.getConteudoBase64());
					doc.setDirArquivo(caminhoArquivo);

					listaDocumentos.add(doc);
				}

				// Persistir a lista de documentos no banco
				RegistroDeFatoDocumentos.inserirListaDocumentos(listaDocumentos);
			}

			if (dados.getLinks() != null && !dados.getLinks().isEmpty()) {
				for (RegistroDeFatoLinkDTO dtoLink : dados.getLinks()) {
					RegistroDeFatoLink link = new RegistroDeFatoLink();

					// Popula os dados do link
					link.setIdRegistroFato(idRegistro); // ID retornado após inserir o registro de fato principal
					link.setUrl(dtoLink.getUrl());
					link.setDetalhamento(dtoLink.getDetalhamento());

					// Insere no banco
					RegistroDeFatoLinks.inserirLink(link);
				}
			}

			RegistroDeFatoUsuarioGrupoDTO dtoUsuariosGrupos = dados.getGrupos();

			// Inserir grupos
			if (dtoUsuariosGrupos.getGrupos() != null) {
				for (Integer idGrupo : dtoUsuariosGrupos.getGrupos()) {
					RegistroDeFatoUsuarioGrupo registro = new RegistroDeFatoUsuarioGrupo();
					registro.setIdRegistroFato(idRegistro);
					registro.setIdGrupo(idGrupo);
					registro.setIdUsuario(null); // sem usuário
					RegistroDeFatoUsuarioGrupos.inserirRegistroUsuarioGrupoRegistroFato(registro);
				}
			}

			// Inserir usuários
			if (dtoUsuariosGrupos.getUsuarios() != null) {
				for (Integer idUsuarioGrupo : dtoUsuariosGrupos.getUsuarios()) {
					RegistroDeFatoUsuarioGrupo registro = new RegistroDeFatoUsuarioGrupo();
					registro.setIdRegistroFato(idRegistro);
					registro.setIdUsuario(idUsuarioGrupo);
					registro.setIdGrupo(null); // sem grupo
					RegistroDeFatoUsuarioGrupos.inserirRegistroUsuarioGrupoRegistroFato(registro);
				}
			}

			if (dados.getObjetos() != null && !dados.getObjetos().isEmpty()) {
				for (RegistroDeFatoObjetoDTO dtoObjeto : dados.getObjetos()) {
					RegistroDeFatoObjeto objeto = new RegistroDeFatoObjeto();

					objeto.setIdRegistroFato(idRegistro); // ID principal do registro
					objeto.setTipo(dtoObjeto.getTipo());
					objeto.setDescricao(dtoObjeto.getDescricao());

					RegistroDeFatoObjetos.inserirObjeto(objeto);
				}
			}
			
			if (dados.getRegistro().getAnotacoes() != null && !dados.getRegistro().getAnotacoes().isEmpty()) {
			    try {
			    	processarAnotacoesRegistroFato(dados.getRegistro().getAnotacoes(),idRegistro,idUsuario);
			    } catch (Exception ex) {
			        logger.error("Erro ao cadastrar anotacoes do registro " + idRegistro, ex);
			    }
			}
			
			// grava historico
			RegistroDeFato anterior = RegistroDeFatos.ObterRegistroDeFatoPorId(idRegistro, idUsuario);
			FatoHistoricoService historicoService = new FatoHistoricoService();
			historicoService.adicionarRegistroFatoHistorico(idRegistro, "INSERT", idUsuario, anterior);
			respostaXML.EnviarRespostaRequisicaoXML(response, true,
					"Registro de fato com boletim cadastrado com sucesso. ID do Registro de Fato:" + idRegistro);

		} catch (SQLException | ConexaoException e) {
			logger.error("Erro ao cadastrar registro de fato com boletim: ", e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro ao cadastrar registro de fato com boletim.");
		} catch (Exception e) {
			logger.error("Erro inesperado: ", e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro inesperado na requisição.");
		}
	}

	private void EdicaoRegistroFatoComBoletim(HttpServletRequest request, HttpServletResponse response,
			int idUsuarioLogado) {
		try {
			// 1. Lê o corpo da requisição (JSON)
			StringBuilder jsonBuilder = new StringBuilder();
			try (BufferedReader reader = request.getReader()) {
				String line;
				while ((line = reader.readLine()) != null) {
					jsonBuilder.append(line);
				}
			}

			// 2. Converte o JSON para o DTO principal
			ObjectMapper mapper = new ObjectMapper();
			RegistroDeFatoComBoletimDTO dados = mapper.readValue(jsonBuilder.toString(),
					RegistroDeFatoComBoletimDTO.class);
			long idRegistro = dados.getRegistro().getId();
			// Atualiza apenas os campos permitidos para edição
			RegistroDeFato registroEntity = new RegistroDeFato();
			registroEntity.setId(dados.getRegistro().getId()); // ID do registro existente

			// Só atualiza o que veio do front
			if (dados.getRegistro().getIdStatus() != null) {
				registroEntity.setIdStatus(dados.getRegistro().getIdStatus());
				
			    if (dados.getRegistro().getIdStatus().equals(2)) {
			        registroEntity.setDataEncerramento(new java.util.Date());
			    }
			}
			
	        if (dados.getRegistro().getIdNaturezaTipo() != null) {
	            registroEntity.setIdNaturezaTipo(dados.getRegistro().getIdNaturezaTipo());
	        }

			if (dados.getRegistro().getPrivado() != null) {
				registroEntity.setPrivado(dados.getRegistro().getPrivado() ? 1 : 0);
			}
			
			if (dados.getRegistro().getDataEvento() != null) {
			    registroEntity.setDataEvento(dados.getRegistro().getDataEvento());
			}
			
	        if (dados.getRegistro().getDataEvento() != null) {
	            registroEntity.setDataEvento(dados.getRegistro().getDataEvento());
	        }
			
			registroEntity.setDataModificada(new Date());

			if (dados.getBoletim() != null) {
				registroEntity.setTemBoletim(1);
			} else {
				registroEntity.setTemBoletim(0);
			}

			RegistroDeFato anterior = RegistroDeFatos.ObterRegistroDeFatoPorId(idRegistro, idUsuarioLogado);

			// Atualiza registro no banco
			RegistroDeFatos.atualizarRegistroFato(registroEntity);
			
			if (dados.getBoletim() != null) {
	            Boletim boletimEntity = new Boletim();
	            boletimEntity.setId(dados.getBoletim().getId());
	            boletimEntity.setIdSituacao(dados.getBoletim().getIdSituacao());
	            boletimEntity.setPermiteAtendimento(dados.getRegistro().getPermitirAtendimento() ? 1 : 0);
	            boletimEntity.setDetalhamento(dados.getBoletim().getDetalhamento());
	            boletimEntity.setDataHoraEvento(dados.getRegistro().getDataEvento());
	            Integer idBoletim;
	            if (boletimEntity.getId() == null) {
	                idBoletim = Boletins.inserirBoletim(boletimEntity, idRegistro, idUsuarioLogado);
	            } else {
	                Boletins.atualizarBoletim(boletimEntity);
	                idBoletim = boletimEntity.getId();
	            }

	            if (dados.getBoletim().getApreensoes() != null) {
					for (BoletimApreensaoDTO apreensaoDTO : dados.getBoletim().getApreensoes()) {
						if ("novo".equals(apreensaoDTO.getStatus())) {
							BoletimApreensao apreensaoEntity = new BoletimApreensao();
							apreensaoEntity.setIdBoletim(idBoletim);
							apreensaoEntity.setTipo(apreensaoDTO.getTipo());
							apreensaoEntity.setDescricao(apreensaoDTO.getDescricao());
							Boletins.inserirBoletimApreensao(apreensaoEntity);

						} else if ("removido".equals(apreensaoDTO.getStatus())) {
							Boletins.removerBoletimApreensao(apreensaoDTO.getId());

						} else if ("editado".equals(apreensaoDTO.getStatus())) {
							BoletimApreensao apreensaoEntity = new BoletimApreensao();
							apreensaoEntity.setId(apreensaoDTO.getId());
							apreensaoEntity.setTipo(apreensaoDTO.getTipo());
							apreensaoEntity.setDescricao(apreensaoDTO.getDescricao());
							Boletins.atualizarBoletimApreensao(apreensaoEntity);
						}
					}
				}
	        } else {
	            RegistroFatoDTO fatoDTO = new RegistroFatoDTO();	            
	            // Popula o DTO com os dados relevantes para o Fato
	            fatoDTO.setAtendimentoPermitido(dados.getRegistro().getPermitirAtendimento() ? 1 : 0);
	            fatoDTO.setPrivado(dados.getRegistro().getPrivado() ? 1 : 0);
	            fatoDTO.setIdStatus(dados.getRegistro().getIdStatus());
	            fatoDTO.setDetalhamentoFato(dados.getRegistro().getDetalhamento());
	            fatoDTO.setEnvolvimentoArmas(Boolean.TRUE.equals(dados.getRegistro().getEnvolvimentoArma()) ? 1 : 0);
	            if (dados.getRegistro().getDataEvento() != null) {
	                Date dataEventoLocal = dados.getRegistro().getDataEvento();
	                SimpleDateFormat formatador = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	                formatador.setTimeZone(TimeZone.getTimeZone("UTC"));
	                String dataFormatadaEmUTC = formatador.format(dataEventoLocal);
	                fatoDTO.setDataHoraOcorrido(dataFormatadaEmUTC);
	            }
	            
	            RegistroDeFatos.AtualizarFatoPorRegistroFatoId(fatoDTO, idRegistro);
	        }

			if (dados.getObjetos() != null) {
				for (RegistroDeFatoObjetoDTO dtoObjeto : dados.getObjetos()) {
					RegistroDeFatoObjeto objeto = new RegistroDeFatoObjeto();
					objeto.setIdRegistroFato(idRegistro);
					objeto.setId(dtoObjeto.getId());
					objeto.setTipo(dtoObjeto.getTipo());
					objeto.setDescricao(dtoObjeto.getDescricao());

					if ("novo".equals(dtoObjeto.getStatus())) {
						RegistroDeFatoObjetos.inserirObjeto(objeto);
					} else if ("removido".equals(dtoObjeto.getStatus())) {
						if (objeto.getId() != null) {
							RegistroDeFatoObjetos.removerObjeto(objeto.getId());
						}
					}
				}
			}

			if (dados.getLinks() != null && !dados.getLinks().isEmpty()) {
				for (RegistroDeFatoLinkDTO dtoLink : dados.getLinks()) {
					RegistroDeFatoLink link = new RegistroDeFatoLink();

					link.setId(dtoLink.getId()); // importante para diferenciar existente x novo
					link.setIdRegistroFato(idRegistro);
					link.setUrl(dtoLink.getUrl());
					link.setDetalhamento(dtoLink.getDetalhamento());

					if ("novo".equals(dtoLink.getStatus())) {
						RegistroDeFatoLinks.inserirLink(link);
					} else if ("removido".equals(dtoLink.getStatus())) {
						if (link.getId() != null) {
							RegistroDeFatoLinks.removerLink(link.getId());
						}
					}
				}
			}

			if (dados.getIndividuos() != null && !dados.getIndividuos().isEmpty()) {
			    for (RegistroDeFatoIndividuoDTO dtoIndividuo : dados.getIndividuos()) {
			     
			        RegistroDeFatoIndividuo individuo = new RegistroDeFatoIndividuo();
			        individuo.setId(dtoIndividuo.getId());
			        individuo.setIdRegistroFato(idRegistro);
			        individuo.setNome(dtoIndividuo.getNome());
			        individuo.setCpf(dtoIndividuo.getCpf());
			        individuo.setDdd(dtoIndividuo.getDdd());
			        individuo.setTelefone(dtoIndividuo.getTelefone());
			        individuo.setEmail(dtoIndividuo.getEmail());
			        individuo.setIdTipoEnvolvimento(dtoIndividuo.getIdTipoEnvolvimento());
			        individuo.setDetalheEnvolvimento(dtoIndividuo.getDetalheEnvolvimento());

			        if ("novo".equals(dtoIndividuo.getStatus())) {
			            RegistroDeFatoIndividuos.inserirIndividuo(individuo);

			        } else if ("removido".equals(dtoIndividuo.getStatus())) {
			            if (individuo.getId() != null) {
			                RegistroDeFatoIndividuos.removerIndividuo(individuo.getId());
			            }
			        }
			    }
			}

			if (dados.getDocumentos() != null && !dados.getDocumentos().isEmpty()) {
				for (RegistroDeFatoDocumentoDTO dtoDoc : dados.getDocumentos()) {
					if ("novo".equals(dtoDoc.getStatus())) {
						// Inserir documento novo
						RegistroDeFatoDocumento doc = new RegistroDeFatoDocumento();
						doc.setIdRegistroFato(idRegistro);
						doc.setTipo(dtoDoc.getTipo());
						doc.setDetalhamento(dtoDoc.getDetalhamento());
						String caminhoArquivo = salvarArquivoRegistroDeFato(idRegistro, dtoDoc.getNome(),
								dtoDoc.getConteudoBase64());
						doc.setDirArquivo(caminhoArquivo);
						RegistroDeFatoDocumentos.inserirDocumento(doc);

					} else if ("removido".equals(dtoDoc.getStatus()) && dtoDoc.getId() != null) {
						// Remover documento existente
						RegistroDeFatoDocumentos.removerDocumento(dtoDoc.getId());

					}
				}
			}

			RegistroDeFatoUsuarioGrupoDTO dtoUsuariosGrupos = dados.getGrupos();

			// --- GRUPOS ---
			if (dtoUsuariosGrupos.getGrupos() != null) {
				List<Integer> idsGruposNovos = dtoUsuariosGrupos.getGrupos();
				List<Integer> idsGruposExistentes = RegistroDeFatoUsuarioGrupos
						.obterIdsGruposPorRegistroFato(idRegistro);

				// IDs para remover (existentes mas não mais enviados)
				List<Integer> idsGruposParaRemover = idsGruposExistentes.stream()
						.filter(id -> !idsGruposNovos.contains(id)).collect(Collectors.toList());

				// IDs para inserir (novos que não existiam)
				List<Integer> idsGruposParaInserir = idsGruposNovos.stream()
						.filter(id -> !idsGruposExistentes.contains(id)).collect(Collectors.toList());

				// Remover grupos
				for (Integer idGrupoRemover : idsGruposParaRemover) {
					RegistroDeFatoUsuarioGrupos.removerRegistroUsuarioGrupoRegistroFato(idRegistro, idGrupoRemover);
				}

				// Inserir novos grupos
				for (Integer idGrupoInserir : idsGruposParaInserir) {
					RegistroDeFatoUsuarioGrupo registro = new RegistroDeFatoUsuarioGrupo();
					registro.setIdRegistroFato(idRegistro);
					registro.setIdGrupo(idGrupoInserir);
					registro.setIdUsuario(null); // grupo sem usuário
					RegistroDeFatoUsuarioGrupos.inserirRegistroUsuarioGrupoRegistroFato(registro);
				}
			}

			// --- USUÁRIOS ---
			if (dtoUsuariosGrupos.getUsuarios() != null) {
				List<Integer> idsUsuariosNovos = new ArrayList<>(dtoUsuariosGrupos.getUsuarios());

				// Garantir que o usuário logado esteja sempre incluído
				if (!idsUsuariosNovos.contains(idUsuarioLogado)) {
					idsUsuariosNovos.add(idUsuarioLogado);
				}

				List<Integer> idsUsuariosExistentes = RegistroDeFatoUsuarioGrupos
						.obterIdsUsuariosPorRegistroFato(idRegistro);

				// IDs para remover (existentes mas não mais enviados, exceto usuário logado)
				List<Integer> idsUsuariosParaRemover = idsUsuariosExistentes.stream()
						.filter(id -> !idsUsuariosNovos.contains(id) && !id.equals(idUsuarioLogado))
						.collect(Collectors.toList());

				// IDs para inserir (novos que não existiam)
				List<Integer> idsUsuariosParaInserir = idsUsuariosNovos.stream()
						.filter(id -> !idsUsuariosExistentes.contains(id)).collect(Collectors.toList());

				// Remover usuários
				for (Integer idUsuarioRemover : idsUsuariosParaRemover) {
					RegistroDeFatoUsuarioGrupos.removerRegistroUsuarioGrupoRegistroFatoPorUsuario(idRegistro,
							idUsuarioRemover);
				}

				// Inserir novos usuários
				for (Integer idUsuarioInserir : idsUsuariosParaInserir) {
					RegistroDeFatoUsuarioGrupo registro = new RegistroDeFatoUsuarioGrupo();
					registro.setIdRegistroFato(idRegistro);
					registro.setIdUsuario(idUsuarioInserir);
					registro.setIdGrupo(null); // usuário sem grupo
					RegistroDeFatoUsuarioGrupos.inserirRegistroUsuarioGrupoRegistroFato(registro);
				}
			}

			if (dados.getVeiculos() != null) {
				for (RegistroDeFatoVeiculoDTO dtoVeiculo : dados.getVeiculos()) {

					// Cria entidade de veículo do registro
					RegistroDeFatoVeiculo veiculo = new RegistroDeFatoVeiculo();
					veiculo.setIdRegistroFato(idRegistro);
					veiculo.setPlaca(dtoVeiculo.getPlaca());
					veiculo.setCor(dtoVeiculo.getCor());
					veiculo.setMarca(dtoVeiculo.getMarca());
					veiculo.setModelo(dtoVeiculo.getModelo());

					// Inserir novo veículo
					if ("novo".equals(dtoVeiculo.getStatus())) {
						RegistroDeFatoVeiculos.CadastrarVeiculo(veiculo);

						// Se monitorado, inserir na tabela de monitoramento
						if (dtoVeiculo.isMonitorado()) {
							VeiculoMonitorado monitorado = new VeiculoMonitorado();
							monitorado.setPlaca(dtoVeiculo.getPlaca());
							monitorado.setIdRegistroFato(idRegistro);
							monitorado.setDescricao(dtoVeiculo.getDescricao());
							monitorado.setNome(dtoVeiculo.getNome() != null ? dtoVeiculo.getNome()
									: dtoVeiculo.getDescricaoTipoAlerta() + " - Placa " + dtoVeiculo.getPlaca());
							monitorado.setPrivado(dados.getRegistro().getPrivado());

							try {
								if (dtoVeiculo.getIdTipoAlerta() != null) {
									monitorado.setIdTipoAlertaOcorrencia(UUID.fromString(dtoVeiculo.getIdTipoAlerta()));
								}
							} catch (IllegalArgumentException e) {
								logger.error("ID do tipo de alerta inválido: " + dtoVeiculo.getIdTipoAlerta());
							}

							LocalDateTime agora = LocalDateTime.now();
							Date dataAgora = Date.from(agora.atZone(ZoneId.systemDefault()).toInstant());
							monitorado.setDataCadastro(dataAgora);
							monitorado.setDataInicio(dataAgora);

							if (dtoVeiculo.getDataFim() != null && !dtoVeiculo.getDataFim().trim().isEmpty()) {
								try {
									monitorado.setDataFim(
											new SimpleDateFormat("yyyy-MM-dd").parse(dtoVeiculo.getDataFim()));
								} catch (Exception e) {
									monitorado.setDataFim(null);
								}
							}

							if (dtoVeiculo.getDataInicio() != null && !dtoVeiculo.getDataInicio().trim().isEmpty()) {
								try {
									monitorado.setDataInicio(
											new SimpleDateFormat("yyyy-MM-dd").parse(dtoVeiculo.getDataFim()));
								} catch (Exception e) {
									monitorado.setDataFim(dataAgora);
								}
							}

							// Inserir o veículo monitorado
							UUID idVeiculoMonitorado = VeiculosMonitorados
									.InserirVeiculoMonitoradoRetornaId(monitorado, idUsuarioLogado);
						}

					} else if ("removido".equals(dtoVeiculo.getStatus())) {
						// Remove veículo
						RegistroDeFatoVeiculos.RemoverVeiculo(veiculo.getPlaca(), idRegistro);
						RegistroDeFatoVeiculos.InativarVeiculoMonitorado(veiculo.getPlaca(), idRegistro);
					}
				}
			}

			if (dados.getEnderecos() != null) {
				for (RegistroDeFatoEnderecoDTO dtoEndereco : dados.getEnderecos()) {
					RegistroDeFatoEndereco endereco = new RegistroDeFatoEndereco();
					endereco.setId(dtoEndereco.getId()); // importante para diferenciar existente x novo
					endereco.setIdRegistroFato(idRegistro);
					endereco.setIdTipoEvento(dtoEndereco.getIdTipoEvento());
					endereco.setIdCidade(dtoEndereco.getIdCidade());
					endereco.setCep(dtoEndereco.getCep());
					endereco.setBairro(dtoEndereco.getBairro());
					endereco.setRua(dtoEndereco.getRua());
					endereco.setNumero(dtoEndereco.getNumero());
					endereco.setComplemento(dtoEndereco.getComplemento());
					endereco.setLatitude(dtoEndereco.getLatitude());
					endereco.setLongitude(dtoEndereco.getLongitude());

					RegistroDeFatoEnderecos.alterarEndereco(endereco);
				}
			}
			
			if (dados.getRegistro().getAnotacoes() != null && !dados.getRegistro().getAnotacoes().isEmpty()) {

			    try {
			    	processarAnotacoesRegistroFato(dados.getRegistro().getAnotacoes(),idRegistro,idUsuarioLogado);
			    } catch (Exception ex) {
			        logger.error("Erro ao processar anotacoes do registro " + idRegistro, ex);
			        throw ex;
			    }
			}
			
			// registro historico
			FatoHistoricoService historicoService = new FatoHistoricoService();
			historicoService.adicionarRegistroFatoHistorico(idRegistro, "UPDATE", idUsuarioLogado, anterior);
			// 8. Resposta de sucesso
			respostaXML.EnviarRespostaRequisicaoXML(response, true,
					"Registro de fato com boletim cadastrado com sucesso. ID do Registro de Fato:" + idRegistro);

		} catch (SQLException | ConexaoException e) {
			logger.error("Erro ao cadastrar registro de fato com boletim: ", e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro ao cadastrar registro de fato com boletim.");
		} catch (Exception e) {
			logger.error("Erro inesperado: ", e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, "Erro inesperado na requisição.");
		}
	}

	public static void AtualizarRegistroFatoSemBoletim(HttpServletRequest request, HttpServletResponse response,
			Integer idUsuarioLogado) throws ServletException, IOException {

		try {

			AtualizarRegistroFato dto = new ObjectMapper().readValue(request.getInputStream(),
					AtualizarRegistroFato.class);

			int idRegistroFato = dto.registroFato.getId();
			
			// grava historico
			RegistroDeFato anterior = RegistroDeFatos.ObterRegistroDeFatoPorId(Long.valueOf(idRegistroFato), idUsuarioLogado);

			// Atualiza o fato
			RegistroFatoDTO fato = new RegistroFatoDTO();
			fato.setIdStatus(1);
			fato.setDataHoraOcorrido(dto.registroFato.getDataHoraOcorrido());
			fato.setDetalhamentoFato(dto.registroFato.getDetalhamentoFato());
			fato.setEnvolvimentoArmas(dto.registroFato.getEnvolvimentoArmas());
			fato.setAtendimentoPermitido(dto.registroFato.getAtendimentoPermitido());
			fato.setId(dto.registroFato.getId());
			RegistroDeFatos.AtualizarFato(fato);

			// Atualiza o endereço
			RegistroDeFatoEndereco endereco = new RegistroDeFatoEndereco();
			endereco.setIdTipoEvento(dto.localizacao.getTipoEnderecoEvento());
			endereco.setIdCidade(dto.localizacao.getCidadeId());
			endereco.setCep(dto.localizacao.getCep());
			endereco.setBairro(dto.localizacao.getBairro());
			endereco.setRua(dto.localizacao.getRua());
			endereco.setNumero(Integer.parseInt(dto.localizacao.getNumero()));
			endereco.setComplemento(dto.localizacao.getComplemento());
			RegistroDeFatoEnderecos.alterarEnderecoParcial(endereco, idRegistroFato);

			// Adiciona ou remove veículos de acordo com a lista
			TratarVeiculosFato(dto.veiculos, idRegistroFato);

			// Adiciona ou remove objetos de acordo com a lista
			TratarObjetosFato(dto.objetos, idRegistroFato);

			// Adiciona ou remove envolvidos de acordo com a lista
			TratarEnvolvidosFato(dto.envolvidos, idRegistroFato);

			// Adiciona ou remove acessos de usuários e grupos de acordo com a lista
			TratarAcessosFato(dto.grupos, dto.usuarios, idRegistroFato);
			
			if (fato.getAnotacoes() != null && !fato.getAnotacoes().isEmpty()) {
			    try {
			    	// no banco o idRegistroFato é no minimo long
			    	processarAnotacoesRegistroFato(fato.getAnotacoes(),(long)idRegistroFato,idUsuarioLogado);
			    } catch (Exception ex) {
			        logger.error("Erro ao cadastrar anotacoes do registro " + idRegistroFato, ex);
			    }
			}
			
			// grava historico
			FatoHistoricoService historicoService = new FatoHistoricoService();
			historicoService.adicionarRegistroFatoHistorico(Long.valueOf(idRegistroFato), "UPDATE", idUsuarioLogado, anterior);
			
			// Resposta de sucesso
			respostaXML.EnviarRespostaRequisicaoXML(response, true, 
				"Registro de fato sem boletim atualizado com sucesso. ID do Registro de Fato:" + idRegistroFato);

		} catch (Exception e) {
			String msg = "Ocorreu um erro ao cadastrar o fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	public static void TratarVeiculosFato(VeiculosDiferencialDTO veiculos, long idRegistroFato)
			throws ServletException, IOException, SQLException, ConexaoException {

		try {
			if (veiculos.getRemovidos() != null && !veiculos.getRemovidos().isEmpty()) {
				for (VeiculoDTO veiculoRemovido : veiculos.getRemovidos()) {

					String placaParaRemover = veiculoRemovido.getPlaca();
					RegistroDeFatoVeiculos.RemoverVeiculo(placaParaRemover, idRegistroFato);
					RegistroDeFatoVeiculos.InativarVeiculoMonitorado(placaParaRemover, idRegistroFato);
				}
			}

			if (veiculos.getAdicionados() != null && !veiculos.getAdicionados().isEmpty()) {
				for (VeiculoDTO veiculoAdicionado : veiculos.getAdicionados()) {

					RegistroDeFatoVeiculo veiculo = new RegistroDeFatoVeiculo();
					veiculo.setIdRegistroFato(idRegistroFato);
					veiculo.setCor(veiculoAdicionado.getCor());
					veiculo.setMarca(veiculoAdicionado.getMarca());
					veiculo.setPlaca(veiculoAdicionado.getPlaca());
					veiculo.setModelo(veiculoAdicionado.getModelo());

					RegistroDeFatoVeiculos.CadastrarVeiculo(veiculo);
				}
			}
		} catch (Exception e) {
			String msg = "Ocorreu um erro ao atualizar on fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(null, false, msg);
			return;
		}
	}

	public static void TratarObjetosFato(ObjetosDiferencialDTO objetos, long idRegistroFato)
			throws ServletException, IOException, SQLException, ConexaoException {

		try {

			List<RegistroDeFatoObjeto> objetosParaAdicionar = new ArrayList<>();

			if (objetos.getRemovidos() != null && !objetos.getRemovidos().isEmpty()) {
				for (RegistroFatoObjeto objetoRemovido : objetos.getRemovidos()) {
					RegistroDeFatoObjetos.removerObjeto(objetoRemovido.getId());
				}
			}

			if (objetos.getAdicionados() != null && !objetos.getAdicionados().isEmpty()) {
				for (RegistroFatoObjeto objetoAdicionado : objetos.getAdicionados()) {

					RegistroDeFatoObjeto novoObjeto = new RegistroDeFatoObjeto();
					novoObjeto.setIdRegistroFato(idRegistroFato);
					novoObjeto.setTipo(objetoAdicionado.getTipo());
					novoObjeto.setDescricao(objetoAdicionado.getDescricao());

					objetosParaAdicionar.add(novoObjeto);
				}
				if (objetosParaAdicionar != null & !objetosParaAdicionar.isEmpty()) {
					RegistroDeFatoObjetos.inserirListaObjetos(objetosParaAdicionar);
				}
			}
		} catch (Exception e) {
			String msg = "Ocorreu um erro ao atualizar os objetos do fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(null, false, msg);
			return;
		}
	}

	public static void TratarEnvolvidosFato(EnvolvidosDiferencialDTO envolvidos, long idRegistroFato)
			throws ServletException, IOException, SQLException, ConexaoException {

		try {

			List<EnvolvidoDTO> envolvidosParaAdicionar = new ArrayList<>();

			if (envolvidos.getRemovidos() != null && !envolvidos.getRemovidos().isEmpty()) {
				for (EnvolvidoDTO envolvidoRemovido : envolvidos.getRemovidos()) {
					RegistroDeFatoIndividuos.removerIndividuo(envolvidoRemovido.getId());
				}
			}

			if (envolvidos.getAdicionados() != null && !envolvidos.getAdicionados().isEmpty()) {
				for (EnvolvidoDTO envolvidoAdicionado : envolvidos.getAdicionados()) {

					EnvolvidoDTO envolvido = new EnvolvidoDTO();
					envolvido.setTipoEnvolvimento(envolvidoAdicionado.getTipoEnvolvimento());
					envolvido.setDetalhamento(envolvidoAdicionado.getDetalhamento());
					envolvido.setNome(envolvidoAdicionado.getNome());
					envolvido.setCpf(envolvidoAdicionado.getCpf());
					envolvido.setDdd(envolvidoAdicionado.getDdd());
					envolvido.setTelefone(envolvidoAdicionado.getTelefone());

					envolvidosParaAdicionar.add(envolvido);
				}

				if (envolvidosParaAdicionar != null && !envolvidosParaAdicionar.isEmpty()) {
					RegistroDeFatoIndividuos.CadastrarIndividuos(idRegistroFato, envolvidosParaAdicionar);
				}
			}
		} catch (Exception e) {
			String msg = "Ocorreu um erro ao atualizar os objetos do fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(null, false, msg);
			return;
		}
	}

	public static void TratarAcessosFato(GruposDiferencialDTO grupos, GruposDiferencialDTO usuarios,
			long idRegistroFato) throws ServletException, IOException, SQLException, ConexaoException {

		try {

			List<RegistroDeFatoUsuarioGrupo> listaUsuariosAdicionar = new ArrayList<>();

			if (grupos.getRemovidos() != null && !grupos.getRemovidos().isEmpty()) {
				for (int idsRemovidos : grupos.getRemovidos()) {
					RegistroDeFatoUsuarioGrupos.removerRegistroUsuarioGrupoRegistroFato(idRegistroFato, idsRemovidos);
				}
			}

			if (grupos.getAdicionados() != null && !grupos.getAdicionados().isEmpty()) {
				for (int grupoAdicionado : grupos.getAdicionados()) {

					RegistroDeFatoUsuarioGrupo novoGrupo = new RegistroDeFatoUsuarioGrupo();
					novoGrupo.setIdRegistroFato(idRegistroFato);
					novoGrupo.setIdGrupo(grupoAdicionado);
					novoGrupo.setIdUsuario(null);

					RegistroDeFatoUsuarioGrupos.inserirRegistroUsuarioGrupoRegistroFato(novoGrupo);
				}
			}

			if (usuarios.getRemovidos() != null && !usuarios.getRemovidos().isEmpty()) {
				for (int idsRemovidos : usuarios.getRemovidos()) {
					RegistroDeFatoUsuarioGrupos.removerRegistroUsuarioGrupoRegistroFatoPorUsuario(idRegistroFato,
							idsRemovidos);
				}
			}

			if (usuarios.getAdicionados() != null && !usuarios.getAdicionados().isEmpty()) {
				for (int usuarioAdicionado : usuarios.getAdicionados()) {

					RegistroDeFatoUsuarioGrupo novoUsuario = new RegistroDeFatoUsuarioGrupo();
					novoUsuario.setIdRegistroFato(idRegistroFato);
					novoUsuario.setIdGrupo(null);
					novoUsuario.setIdUsuario(usuarioAdicionado);

					listaUsuariosAdicionar.add(novoUsuario);
				}

				if (listaUsuariosAdicionar != null && !listaUsuariosAdicionar.isEmpty()) {
					RegistroDeFatoUsuarioGrupos.inserirUsuariosGruposRegistroFato(listaUsuariosAdicionar);
				}
			}

		} catch (Exception e) {
			String msg = "Ocorreu um erro ao atualizar os objetos do fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(null, false, msg);
			return;
		}
	}

	public static void InserirPassagemVeiculo(HttpServletRequest request, HttpServletResponse response,
			Integer idUsuarioLogado) throws ServletException, IOException {

		try {
			int idRegistroFato = Integer.parseInt(request.getParameter("idRegistroFato"));
			String idVeiculo = request.getParameter("idVeiculo");
			int idUsuario = idUsuarioLogado;

			RegistrosPassagensVeiculos.InserirPassagemVeiculo(idRegistroFato, idVeiculo, idUsuario);
		} catch (Exception e) {
			String msg = "Ocorreu um erro ao cadastrar o fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	private void EnviarRespostaXML(HttpServletResponse response, RegistroDeFatos registroDeFatos)
			throws JAXBException, IOException {
		JAXBContext context;
		try {
			// Formando dados para envio
			context = JAXBContext.newInstance(RegistroDeFatos.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marsHall.marshal(registroDeFatos, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

			logger.info("EnviarRespostaXML():: Registros enviados: "
					+ Integer.toString(registroDeFatos.getListaRegistroDeFatos().size()));
			registroDeFatos = null;

		} catch (Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de Registro de Fato!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}

	private static void EnviarRespostaXMLFatoCompleto(HttpServletResponse response,
			FatoSemBoletimCompletoDTO fatoCompleto) throws JAXBException, IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(FatoSemBoletimCompletoDTO.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marshaller.marshal(fatoCompleto, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml; charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

			logger.info("EnviarRespostaXML():: Fato enviado com sucesso. ID: " + fatoCompleto.registro_fato.getId());

		} catch (Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar os dados do fato!";
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}

	private static void EnviarRespostaXMLRegistroDeFato(HttpServletResponse response, RegistroDeFato fatoCompleto)
			throws JAXBException, IOException {
		try {
			JAXBContext context = JAXBContext.newInstance(FatoSemBoletimCompletoDTO.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

			StringWriter sw = new StringWriter();
			marshaller.marshal(fatoCompleto, sw);
			String xml = sw.toString();
			sw.close();

			response.setHeader("Content-Type", "text/xml; charset=UTF-8");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();

			logger.info("EnviarRespostaXML():: Fato enviado com sucesso. ID: " + fatoCompleto.getId());

		} catch (Exception e) {
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar os dados do fato!";
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
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

	private static void ObterDir() {
		boolean possuiDir = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor()
				.get("diretorio_justificativa") != null
				&& ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_justificativa") != "";
		if (possuiDir) {
			UPLOAD_DIRECTORY = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor()
					.get("diretorio_justificativa");
		} else {
			UPLOAD_DIRECTORY = System.getProperty("user.home") + File.separator + "Desktop" + File.separator
					+ "BoletimDocumentos";
		}
		logger.info(UPLOAD_DIRECTORY);
	}

	private static String salvarArquivoRegistroDeFato(Long idRegistro, String nomeArquivo, String conteudoBase64)
			throws IOException {
		if (nomeArquivo == null || nomeArquivo.trim().isEmpty()) {
			throw new IllegalArgumentException("Nome do arquivo inválido");
		}

		if (conteudoBase64.contains(",")) {
			conteudoBase64 = conteudoBase64.substring(conteudoBase64.indexOf(",") + 1);
		}

		byte[] dados = Base64.getDecoder().decode(conteudoBase64);

		String pastaRegistro = UPLOAD_DIRECTORY + File.separator + "registro_" + idRegistro + File.separator
				+ new SimpleDateFormat("dd_MM_yyyy").format(new Date());

		File diretorio = new File(pastaRegistro);
		if (!diretorio.exists()) {
			diretorio.mkdirs();
		}

		String caminhoCompleto = pastaRegistro + File.separator + nomeArquivo;
		try (FileOutputStream fos = new FileOutputStream(caminhoCompleto)) {
			fos.write(dados);
		}

		return caminhoCompleto;
	}

	public static void BuscarFatoPorId(HttpServletRequest request, HttpServletResponse response,
			Integer idUsuarioLogado) throws ServletException, IOException {

		try {
			String paramId = request.getParameter("idRegistroFato");
			if (paramId == null || paramId.isEmpty()) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "ID do registro de fato não informado.");
				return;
			}

			long idRegistroFato;
			try {
				idRegistroFato = Long.parseLong(paramId);
			} catch (NumberFormatException ex) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "ID do registro de fato inválido.");
				return;
			}

			boolean temAcesso = RegistroDeFatos.verificarAcessoRegistroFato((int) idRegistroFato, idUsuarioLogado);

			if (!temAcesso) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false,
						"Fato não encontrado ou acesso não permitido.");
				return;
			}

			RegistroDeFato fatoCompleto = RegistroDeFatos.ObterRegistroDeFatoPorId(idRegistroFato, idUsuarioLogado);

			if (fatoCompleto == null) {
				respostaXML.EnviarRespostaRequisicaoXML(response, false, "Fato não encontrado.");
				return;
			}

			// Envia o objeto como XML
			EnviarRespostaXMLRegistroDeFato(response, fatoCompleto);

		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar o Registro de Fato!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}
	
	public static void BuscarVeiculosPorIdRegistroFato(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    try {
	        String paramId = request.getParameter("idRegistroFato");
	        if (paramId == null || paramId.isEmpty()) {
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, "ID do registro de fato não informado.");
	            return;
	        }

	        long idRegistroFato;
	        try {
	            idRegistroFato = Long.parseLong(paramId);
	        } catch (NumberFormatException ex) {
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, "ID do registro de fato inválido.");
	            return;
	        }

	        List<VeiculoMonitorado> veiculos = VeiculosMonitorados.buscarPorIdRegistroFato(idRegistroFato);

	        if (veiculos == null || veiculos.isEmpty()) {
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Nenhum veículo encontrado para este fato.");
	            return;
	        }

	        EnviarRespostaXMLVeiculos(response, veiculos);

	    } catch (Exception e) {
	        String msg = "Ocorreu um erro ao consultar os veículos vinculados ao Registro de Fato!";
	        logger.error(msg, e);
	        respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    }
	}

	private static void EnviarRespostaXMLVeiculos(HttpServletResponse response, List<VeiculoMonitorado> veiculos)
	        throws IOException {
	    response.setContentType("application/xml; charset=UTF-8");
	    response.setCharacterEncoding("UTF-8");

	    try (PrintWriter out = response.getWriter()) {
	        StringBuilder xml = new StringBuilder();
	        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
	        xml.append("<veiculosMonitorados>\n");

	        for (VeiculoMonitorado v : veiculos) {
	            xml.append("  <veiculo>\n");
	            xml.append("    <id>").append(v.getId()).append("</id>\n");
	            xml.append("    <placa>").append(v.getPlaca()).append("</placa>\n");
	            xml.append("    <nome>").append(v.getNome()).append("</nome>\n");
	            xml.append("    <idTipoAlertaOcorrencia>").append(v.getIdTipoAlertaOcorrencia()).append("</idTipoAlertaOcorrencia>\n");
	            xml.append("    <tipoAlertaOcorrencia>").append(v.getTipoAlertaOcorrencia()).append("</tipoAlertaOcorrencia>\n");
	            xml.append("    <descricao>").append(v.getDescricao()).append("</descricao>\n");
	            xml.append("    <dataInicio>").append(v.getDataInicio()).append("</dataInicio>\n");
	            xml.append("    <dataFim>").append(v.getDataFim()).append("</dataFim>\n");
	            xml.append("    <dataCadastro>").append(v.getDataCadastro()).append("</dataCadastro>\n");
	            xml.append("    <idUsuario>").append(v.getIdUsuario()).append("</idUsuario>\n");
	            xml.append("    <usuario>").append(v.getUsuario()).append("</usuario>\n");
	            xml.append("    <nomeUsuario>").append(v.getNomeUsuario()).append("</nomeUsuario>\n");
	            xml.append("    <dataExclusao>").append(v.getDataExclusao()).append("</dataExclusao>\n");
	            xml.append("    <idUsuarioExclusao>").append(v.getIdUsuarioExclusao()).append("</idUsuarioExclusao>\n");
	            xml.append("    <motivoExclusao>").append(v.getMotivoExclusao()).append("</motivoExclusao>\n");
	            xml.append("    <dataInativacao>").append(v.getDataInativacao()).append("</dataInativacao>\n");
	            xml.append("    <idUsuarioInativacao>").append(v.getIdUsuarioInativacao()).append("</idUsuarioInativacao>\n");
	            xml.append("    <privado>").append(v.isPrivado()).append("</privado>\n");
	            xml.append("    <supervisionado>").append(v.isSupervisionado()).append("</supervisionado>\n");
	            xml.append("    <idRegistroFato>").append(v.getIdRegistroFato()).append("</idRegistroFato>\n");
	            xml.append("    <idUsuarioAtualizacao>").append(v.getIdUsuarioAtualizacao()).append("</idUsuarioAtualizacao>\n");
	            xml.append("    <dataAtualizacao>").append(v.getDataAtualizacao()).append("</dataAtualizacao>\n");
	            xml.append("    <monitorarSomenteEste>").append(v.getMonitorarSomenteEste()).append("</monitorarSomenteEste>\n");
	            xml.append("    <errosPermitidosPlaca>").append(v.getErrosPermitidosPlaca()).append("</errosPermitidosPlaca>\n");
	            xml.append("    <errosPermitidosIni>").append(v.getErrosPermitidosIni()).append("</errosPermitidosIni>\n");
	            xml.append("    <errosPermitidosFim>").append(v.getErrosPermitidosFim()).append("</errosPermitidosFim>\n");
	            xml.append("  </veiculo>\n");
	        }

	        xml.append("</veiculosMonitorados>");

	        out.write(xml.toString());
	        out.flush();
	    } catch (Exception e) {
	        String msg = "Erro ao gerar XML dos veículos monitorados.";
	        Logger.getLogger("VeiculoMonitoradoServlet").error(msg, e);
	        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, msg);
	    }
	}
	
	public static void EncerrarMonitoramento(HttpServletRequest request, HttpServletResponse response)
	        throws ServletException, IOException {

	    try {
	        String jsonBody = request.getReader().lines().collect(Collectors.joining());
	        ObjectMapper mapper = new ObjectMapper();
	        List<String> placas = mapper.readValue(jsonBody, new TypeReference<List<String>>() {});

	        if (placas == null || placas.isEmpty()) {
	            respostaXML.EnviarRespostaRequisicaoXML(response, false, "Nenhuma placa foi informada.");
	            return;
	        }

	        VeiculosMonitorados.encerrarMonitoramentoPorPlacas(placas);
	        respostaXML.EnviarRespostaRequisicaoXML(response, true, "Monitoramento encerrado para as placas informadas.");

	    } catch (Exception e) {
	        String msg = "Erro ao encerrar monitoramento dos veículos.";
	        Logger.getLogger("VeiculoMonitoradoServlet").error(msg, e);
	        respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
	    }
	}
	
	private static void processarAnotacoesRegistroFato(
	        List<RegistroFatoAnotacaoDTO> anotacoesDto, Long idRegistro, int idUsuario) throws Exception {

	    if (anotacoesDto == null || anotacoesDto.isEmpty()) {
	        return;
	    }

	    List<RegistroDeFatoAnotacao> listaParaInserir = new ArrayList<>();
	    List<Integer> idsParaRemover = new ArrayList<>();

	    try {
	        for (RegistroFatoAnotacaoDTO aDto : anotacoesDto) {

	            // normaliza id do DTO (trata primitivos/objetos)
	            Integer dtoId = null;
	            try {
	                dtoId = (aDto.getId() == 0) ? null : Integer.valueOf(aDto.getId());
	            } catch (Exception e) {
	                dtoId = null;
	            }

	            // normaliza status
	            String status = aDto.getStatus();
	            if (status == null) {
	                status = (dtoId != null && dtoId > 0) ? "existente" : "novo";
	            }

	            // marcar remoção
	            if ("removido".equalsIgnoreCase(status) && dtoId != null && dtoId > 0) {
	                idsParaRemover.add(dtoId);
	                continue;
	            }

	            // coletar apenas novos para inserir
	            if ("novo".equalsIgnoreCase(status) || dtoId == null || dtoId <= 0) {
	                RegistroDeFatoAnotacao entidade = new RegistroDeFatoAnotacao();
	                entidade.setIdRegistroFato(idRegistro);
	                entidade.setTexto(aDto.getTexto());
	                entidade.setIdUsuario(idUsuario);

	                java.sql.Timestamp ts = parseIsoToTimestamp(aDto.getDataIso());
	                entidade.setDataCriacao(ts);

	                listaParaInserir.add(entidade);
	            }
	            // demais casos (existente) => ignora (atualizacao de texto nao coberta aqui)
	        }

	        // 1) inserir novos (se houver)
	        if (!listaParaInserir.isEmpty()) {
	            try {
	                RegistroDeFatoAnotacoes.inserirListaAnotacoes(listaParaInserir);
	            } catch (Exception e) {
	                logger.error("Erro ao inserir lista de anotacoes para registro " + idRegistro, e);
	                throw e; // propaga para o chamador lidar (como sua transacao/rollback)
	            }
	        }

	        // 2) remover os marcados.
	        if (!idsParaRemover.isEmpty()) {
	            try {
	                RegistroDeFatoAnotacoes.removerListaAnotacoes(idsParaRemover);
	            } catch (Exception e) {
	                logger.error("Falha ao remover anotacoes em lote do registro " + idRegistro, e);
	            }
	        }

	    } catch (Exception ex) {
	        logger.error("Erro ao processar anotacoes do registro " + idRegistro, ex);
	        throw ex;
	    }
	}

	/////////////////////////////////////////////////////////////////////////
	// Helper para converter várias variações de ISO/strings em Timestamp
	/////////////////////////////////////////////////////////////////////////
	private static java.sql.Timestamp parseIsoToTimestamp(String dataIso) {
	    if (dataIso == null || dataIso.isBlank()) {
	        return new java.sql.Timestamp(System.currentTimeMillis());
	    }

	    String original = dataIso.trim();

	    // tenta normalizar (remove Z e transforma T em espaço)
	    String cleaned = original.replace("Z", "").replace("T", " ").trim();

	    try {
	        if (cleaned.length() == 16) { // "yyyy-MM-dd HH:mm"
	            cleaned = cleaned + ":00";
	        }
	        return java.sql.Timestamp.valueOf(cleaned);
	    } catch (IllegalArgumentException iae) {
	        // tenta formatos conhecidos
	        String[] formatos = {
	                "yyyy-MM-dd HH:mm:ss",
	                "yyyy-MM-dd HH:mm",
	                "yyyy-MM-dd'T'HH:mm:ss",
	                "yyyy-MM-dd"
	        };

	        for (String fmt : formatos) {
	            try {
	                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(fmt);
	                sdf.setLenient(false);
	                java.util.Date parsed = sdf.parse(original);
	                return new java.sql.Timestamp(parsed.getTime());
	            } catch (Exception ignored) {
	            }
	        }

	        // tenta Instant.parse (para strings com offset, ex: 2023-01-01T12:00:00Z)
	        try {
	            java.time.Instant inst = java.time.Instant.parse(original);
	            return java.sql.Timestamp.from(inst);
	        } catch (Exception ignored) {
	        }

	        // fallback para agora
	        return new java.sql.Timestamp(System.currentTimeMillis());
	    }
	}
}