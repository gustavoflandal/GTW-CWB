/**********************************************************************************

Projeto: Muralha Digital
Nome do Modulo: GTW

Empresa: Consilux Tecnologia

Autor: Thiago Surgik
Data: 01/10/2021

*********************************************************************************/

package muralha.digital.monitorado;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.mail.internet.InternetAddress;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.ConfiguracaoServidorSmtp;
import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.AcessoFTP;
import com.consilux.model.Mensagem;
import com.google.gson.Gson;

import muralha.digital._ini.Inicializacao;
import muralha.digital.acessos.Usuario;
import muralha.digital.acessos.UsuarioServlet;
import muralha.digital.consulta.AlertasOcorrencias;
import muralha.digital.consulta.TiposAlertaOcorrencias;
import muralha.digital.notificacao.ServicoEmailMuralha;
import muralha.digital.util.Paginacao;
import muralha.digital.util.RespostaRequisicaoXML;
import muralha.digital.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
@WebServlet("/MuralhaDigital/Monitorado")
public class VeiculoMonitoradoServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet 
{

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(VeiculoMonitoradoServlet.class);
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	private static final int QTDE_MAX_CARACTER_ESPECIAL_PLACA_CAD_MON = Inicializacao.QtdeMaxCaracterEspecialPlacaCadMon;
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{

		Integer idUsuario = null;
		//Validando acesso do usuário		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		if (!acessoUsuario.verificaAcesso())
		{
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp");
			return;
		}
		else
		{
			idUsuario = acessoUsuario.getUsuario().getId();
		}
		
		try 
		{
			String msg = null;
			String strAcao = request.getParameter("acao");
			
			if (strAcao == null || strAcao.equals("")) 
			{
				msg = "Ação não informada!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			boolean inserir = false, atualizar = false, encerrar = false, consultar = false;;
						
			if(strAcao.equals("inserir"))
			{
				inserir = true;
				atualizar = false;
				encerrar = false;
			}
			else if(strAcao.equals("atualizar"))
			{
				inserir = false;
				atualizar = true;
				encerrar = false;
			}
			else if(strAcao.equals("encerrar"))
			{
				inserir = false;
				atualizar = false;
				encerrar = true;
			}
			else if(strAcao.equals("exportarCadastrosAtivos"))
			{
				if (!Inicializacao.ExportarCadMonitorado)
					respostaXML.EnviarRespostaRequisicaoXML(response, false, "A sincronização de cadastros de monitoramento não está habilitada! Contate o administrador do sistema!");
				else
					ExportarCadastrosAtivos(response, true, true);
				
				return;
			}
			
			VeiculoMonitorado objValidado = ValidarParametros(request, response, inserir, atualizar, encerrar, consultar, idUsuario);
			
			if (objValidado.isValido())
			{
				objValidado.setIdUsuario(idUsuario);
				
				if (inserir)
					InserirVeiculoMonitorado(objValidado, response);
				else if (atualizar)
					AtualizarVeiculoMonitorado(objValidado, response);
				else if (encerrar)
					EncerrarVeiculoMonitorado(objValidado, response);
				
				if (Inicializacao.ExportarCadMonitorado)
					ExportarCadastrosAtivos(response, false, false);
			}
			
			return;
		}
		catch(Exception e)
		{
			logger.error("Erro ao cadastrar veículo monitorado: " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao cadastrar o veículo monitorado!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{

		//Validando acesso do usuário
		if ( ! new Acesso(request, response, true).verificaAcesso(false))  { new Mensagem(response).showErro("Usuário não atenticado!", "/login/abertura-sistemas.jsp"); return; }
		
		try 
		{	
			String msg = null;
			String strAcao = request.getParameter("acao");
			
			if (strAcao == null || strAcao.equals("")) 
			{
				msg = "Ação não informada!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			if(strAcao.equals("obterLista"))
				ObterTodos(request, response);
			if(strAcao.equals("ObterPlacasComAlerta"))
				ObterPlacasComAlerta(response);
			else if(strAcao.equals("obterPorId"))
				ObterPorId(request, response);
			else if(strAcao.equals("obterIdsMonitorarSomenteEste"))
				ObterIdsMonitorarSomenteEste(request, response);

			return;
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar veículos monitorados!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		
	}
	
	protected void ObterTodos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		boolean inserir = false, editar = false, encerrar = false, consultar = true;
		
		Paginacao paginacao = new Paginacao(request);
		if (!paginacao.OperacaoValida())
		{
			String msg = "Dados de paginação não informados corretamente!";
			logger.error(msg);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		
		final Acesso acessoUsuario = new Acesso(request, response, true);
		Integer idUsuario = acessoUsuario.getUsuario().getId();
		
		VeiculoMonitorado objValidado = ValidarParametros(request, response, inserir, editar, encerrar, consultar, null);
		
		if (objValidado.isValido())
		{
			ObterVeiculosMonitorados(objValidado.getIdTipoAlertaOcorrencia(), objValidado.getDataInicio(), objValidado.getDataFim(), objValidado.getPlaca(),
					objValidado.isApenasCadAtivo(), objValidado.isApenasPlacaComCoringa(), objValidado.isSupervisionado(), objValidado.isPrivado(), idUsuario, paginacao, response);
		}
	}
	
	private void ObterPlacasComAlerta(HttpServletResponse response) {
		try {
			List<String> placasRepetidas = VeiculosMonitorados.obterPlacasComAlerta();

			StringBuilder xml = new StringBuilder();
			xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			xml.append("<placasRepetidas>\n");

			for (String placa : placasRepetidas) {
				xml.append("  <placa>").append(placa).append("</placa>\n");
			}

			xml.append("</placasRepetidas>");

			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml.toString());
			response.getWriter().flush();

			logger.info("ObterPlacasRepetidas():: Placas enviadas: " + placasRepetidas.size());
		} catch (Exception e) {
			String msg = "Ocorreu um erro ao consultar placas repetidas!";
			logger.error(msg, e);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
		}
	}
	
	protected void ObterPorId(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try
		{
			String strId = request.getParameter("id");
			UUID id;
			
			if (strId == null || strId.trim().equals("") || strId.trim().equals("0"))
			{
				String msg = "Identificador do registro não informado!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			
			try
			{
				id = UUID.fromString(strId);
				
			}
			catch (Exception e)
			{
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg, e);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return;
			}
			Integer idUsuario = null;
			//Validando acesso do usuário		
			final Acesso acessoUsuario = new Acesso(request, response, true);
			idUsuario = acessoUsuario.getUsuario().getId();
			boolean verificaAcesso = VeiculosMonitorados.VerificaAcessoPorCadVeiMoniUsuarioId(id, idUsuario);
			if (!verificaAcesso) {
				throw new SecurityException("Acesso negado: Veículo Monitorado privado e usuário não é o dono.");
			}

			VeiculoMonitorado monitorado = VeiculosMonitorados.ObterVeiculoMonitoradoPorId(id);
			
			EnviarVeiculoMonitoradoPorIdXML(response, monitorado);
			
		}
		catch (SecurityException e) {
			String msg = "Acesso negado: Veículo Monitorado privado e usuário não é o dono.";
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar veiculos monitorados!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}

	protected void ObterIdsMonitorarSomenteEste(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		try
		{
			final Acesso acessoUsuario = new Acesso(request, response, true);
			Integer idUsuario = acessoUsuario.getUsuario().getId();
			
			List<UUID> idsMonitorarSomenteEste = VeiculosMonitorados.ObterIdsVeiculosMonitorarSomenteEste(idUsuario);
			
			// Retornar como JSON
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			Gson gson = new Gson();
			String json = gson.toJson(idsMonitorarSomenteEste);
			
			response.getWriter().write(json);
			response.getWriter().flush();
			
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao obter IDs para monitorar somente este!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private VeiculoMonitorado ValidarParametros(HttpServletRequest request, HttpServletResponse response, boolean inserir, boolean atualizar, boolean encerrar, boolean consultar, Integer idUsuario)
	{
		VeiculoMonitorado objValidado = new VeiculoMonitorado();
		objValidado.setValido(true);

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		UUID idTipoAlertaOcorrencia = null, id = null;
		Date dataIni = null, dataFim = null, dataAtual = null;
		String placa = null, nome = null;
		boolean buscarApenasCadAtivo = false, buscarApenasPlacaComCoringa = false;
		Integer idUsuarioResponsavel = null;
		int monitorarSomenteEste = 0;
		String idClasse = null;
		Integer idCor = null;
		Integer idMarca = null;
		Integer idModelo = null;
		String textoAdesivo = null;
		
		try 
		{
			String strTipoAlertaOcorrencia			= request.getParameter("tipoAlertaOcorrencia"		);
			String strDataIni 						= request.getParameter("dataIni"					);
			String strDataFim 						= request.getParameter("dataFim"					);
			String strPlaca 						= request.getParameter("placa"						);
			String strNome	 						= request.getParameter("nome"						);
			String strDescricao 					= request.getParameter("descricao"					);
			String strBuscarApenasCadAtivo 			= request.getParameter("buscarApenasCadAtivo"		);
			String strBuscarApenasPlacaComCoringa	= request.getParameter("buscarApenasPlacaComCoringa");
			String strCadAtivo 						= request.getParameter("cadAtivo"					);
			String strErrosPermitidosPlaca          = request.getParameter("erros_permitidos_placa"     );
			String strErrosPermitidosIni          	= request.getParameter("erros_permitido_ini"     	);
			String strErrosPermitidosFim         	= request.getParameter("erros_permitido_fim"     	);
			String strSupervisionado                = request.getParameter("supervisionado"             );
			String strPrivado                       = request.getParameter("privado"                    );
			String strGruposPopup                   = request.getParameter("gruposPopup"                );
			String strEquipamentosLocais            = request.getParameter("equipamentosLocais"         );
			String strHorariosPermitidos            = request.getParameter("horariosPermitidos"         );
			String strIdUsuarioResponsavel          = request.getParameter("usuario_responsavel"        );
			String strtipoAlertaOcorrenciatitulo    = request.getParameter("tipoAlertaOcorrenciatitulo" );
			String strUsuarioResponsavelNome	    = request.getParameter("usuario_responsavel_nome"	);
			String strMonitorarSomenteEste 			= request.getParameter("monitorar_somente_este"		);
			String strIdClasse 						= request.getParameter("idClasse");
			String strCor           				= request.getParameter("cor");
			String strMarca         				= request.getParameter("marca");
			String strModelo        				= request.getParameter("modelo");
			String strTextoAdesivo  				= request.getParameter("textoAdesivo");
			// Apenas para atualização de registro
			String strId = request.getParameter("id");
			strPlaca =!encerrar ? ((strPlaca != null) && (strPlaca.trim().equals("") || strPlaca.equals("0")) ? null : strPlaca.toUpperCase()) : null;
			boolean placaIncompleta = false;
			boolean placaPossuiCaracterEspecial = false;
			String strPlacaAux = null;
			if (consultar) {
				List<String> placas = new ArrayList<>();
				if (strPlaca != null && !strPlaca.trim().isEmpty()) {
					for (String p : strPlaca.split(",")) {
						if (p != null && !p.trim().isEmpty()) {
							String up = p.trim().toUpperCase();

							// valida cada placa individualmente para consulta
							if (!ExpValida.PLACA.validar(up) &&
								!ExpValida.PLACA_MERCOSUL.validar(up) &&
								!ExpValida.PLACA_PARCIAL.validar(up)) {
								
								objValidado.setValido(false);
								String msg = "Placa informada inválida: " + up;
								logger.error(msg);
								respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
								return objValidado;
							}
							placas.add(up);
						}
					}
				}
				placa = placas.isEmpty() ? null : String.join(",", placas);

			} else {
				strPlaca =!encerrar
					? ((strPlaca != null) && (strPlaca.trim().equals("") || strPlaca.equals("0")) ? null : strPlaca.toUpperCase())
					: null;

				placaIncompleta = (strPlaca != null && strPlaca.length() < 7);
				placaPossuiCaracterEspecial = (strPlaca != null) && Utils.PlacaPossuiCaracterEspecial(strPlaca);
				strPlacaAux = (strPlaca != null && placaPossuiCaracterEspecial) ? Utils.TratarCaracterEspecialPlaca(strPlaca) : strPlaca;

				placa = !encerrar ? strPlaca : null;
			}
			
			if((inserir || atualizar) && (strNome == null || strNome.equals("")))
			{
				objValidado.setValido(false);
				String msg = "Nome não informado!!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			nome = strNome;
			boolean supervisionado = "true".equalsIgnoreCase(strSupervisionado);
			boolean privado        = "true".equalsIgnoreCase(strPrivado);
			objValidado.setSupervisionado(supervisionado);
			objValidado.setPrivado(privado);
			
			if (strIdUsuarioResponsavel != null && !strIdUsuarioResponsavel.trim().isEmpty()) {
				try {
						idUsuarioResponsavel = Integer.parseInt(strIdUsuarioResponsavel.trim());
				} catch (NumberFormatException ex) {
						objValidado.setValido(false);
						String msg = "Usuário responsável inválido!";
						logger.error(msg, ex);
						respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
						return objValidado;
				}
			} else {
				idUsuarioResponsavel = null; // permitido (coluna aceita NULL)
			}
			
			/*
				Nessa parte foi separado o if com supervisionado do sem supervisionado, pois como foi feita uma nova aba para o modal, os dados
				que cadastrávamos não populavam corretamente no banco (geravam nulo), então o "strGruposPopup", que foi o único que manteve
				na parte do supervisionado fica dentro do if, o resto que está na parte de "Configurações Avançadas" fica no if abaixo.
			*/
			if ((inserir || atualizar) && supervisionado) {
				if (strGruposPopup != null && !strGruposPopup.isBlank()) {
					objValidado.setGruposPopup(Arrays.asList(strGruposPopup.split("\\s*,\\s*")));
				}
			}

			if(inserir || atualizar) {
				if (strErrosPermitidosPlaca == null || strErrosPermitidosPlaca.isBlank()) {
					objValidado.setValido(false);
					String msg = "Informe o nível de semelhança da placa!";
					logger.error(msg);
					respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
					return objValidado;
				}
				
				try {
					int errosPermitidos = Integer.parseInt(strErrosPermitidosPlaca);
					if (errosPermitidos < 0 || errosPermitidos > 4) {
						objValidado.setValido(false);
						String msg = "Nível de semelhança inválido! Informe de 0 a 4.";
						logger.error(msg);
						respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
						return objValidado;
					}
					objValidado.setErrosPermitidosPlaca(errosPermitidos);
				} catch (NumberFormatException e) {
					objValidado.setValido(false);
					String msg = "Valor inválido para nível de semelhança da placa!";
					logger.error(msg, e);
					respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
					return objValidado;
				}
				
				if (strEquipamentosLocais != null && !strEquipamentosLocais.isBlank()) {
					try {
						List<Integer> equipamentos = Arrays.stream(strEquipamentosLocais.split(","))
							.map(String::trim)
							.filter(s -> !s.isEmpty())
							.map(Integer::parseInt)
							.collect(Collectors.toList());

						objValidado.setEquipamentosLocais(equipamentos);
					} catch (Exception e) {
						objValidado.setValido(false);
						String msg = "Erro ao processar os equipamentos locais selecionados!";
						logger.error(msg, e);
						respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
						return objValidado;
					}
				}
				
				if (strHorariosPermitidos != null && !strHorariosPermitidos.isBlank()) {
					try {
						ObjectMapper mapper = new ObjectMapper();
						List<HorarioPermitido> horarios = mapper.readValue(
							strHorariosPermitidos,
							new TypeReference<List<HorarioPermitido>>() {}
						);
						
						if (!horarios.isEmpty()) {
							objValidado.setHorariosPermitidos(horarios);
						}
					} catch (Exception e) {
						objValidado.setValido(false);
						String msg = "Erro ao processar os horários permitidos!";
						logger.error(msg, e);
						respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
						return objValidado;
					}
				}
			}
			placa = !encerrar ? strPlaca : null;
			
			if (consultar && placa != null) {
				String[] arr = placa.split(",");
				for (String p : arr) {
					if (p != null && !p.trim().isEmpty()) {
						String placaLimpa = p.trim().toUpperCase();
						
						boolean valida = ExpValida.PLACA.validar(placaLimpa)
											|| ExpValida.PLACA_MERCOSUL.validar(placaLimpa)
											|| ExpValida.PLACA_PARCIAL.validar(placaLimpa);

						if (!valida) {
							objValidado.setValido(false);
							String msg = "Placa informada inválida! (" + placaLimpa + ")";
							logger.error(msg);
							respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
							return objValidado;
						}
					}
				}
			}
			
			if ( inserir && strPlacaAux != null && ( placaIncompleta || (!ExpValida.PLACA.validar(strPlacaAux) && !ExpValida.PLACA_MERCOSUL.validar(strPlacaAux)) ) )
			{
				objValidado.setValido(false);
				String msg = "Placa informada inválida!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			if ( inserir && strPlaca != null && !Utils.ValidarCaracterEspecialPlaca(strPlaca, QTDE_MAX_CARACTER_ESPECIAL_PLACA_CAD_MON) )
			{
				objValidado.setValido(false);
				String msg = String.format("Placa informada inválida! Quantidade limite de caracteres especiais (%1d) foi excedida!", QTDE_MAX_CARACTER_ESPECIAL_PLACA_CAD_MON);
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			if (inserir && (strTipoAlertaOcorrencia == null || strTipoAlertaOcorrencia.trim().equals("") || strTipoAlertaOcorrencia.trim().equals("0")))
			{
				objValidado.setValido(false);
				String msg = "Favor informar o tipo de alerta/irregularidade!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			if ( inserir && ( strDataIni == null || strDataIni.trim().equals("") ) )
			{
				objValidado.setValido(false);
				String msg = "Favor informar a data de início para cadastro!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			if ( !inserir && !atualizar && !encerrar && 
					(
						((strDataIni == null || strDataIni.trim().equals("")) && (strDataFim != null && !strDataFim.trim().equals(""))) || 
						((strDataFim == null || strDataFim.trim().equals("")) && (strDataIni != null && !strDataIni.trim().equals("")))
					)
				)
			{
				objValidado.setValido(false);
				String msg = "Favor informar datas de início e fim da pesquisa!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			if ( ( strDataIni != null && !strDataIni.trim().equals("") ) && !ExpValida.DATA.validar(strDataIni.trim()) )
			{
				objValidado.setValido(false);
				String msg = "Data início informada inválida!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			if ( ( strDataFim != null && !strDataFim.trim().equals("") ) && !ExpValida.DATA.validar(strDataFim.trim()) )
			{
				objValidado.setValido(false);
				String msg = "Data fim informada inválida!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			if (inserir || atualizar)
			{
				objValidado.setDescricao(strDescricao.equals("") ? null : strDescricao.trim());
			}

			/*
				Aqui abaixo é feito a chamada do setErrosPermitidosIni e setErrosPermitidosFim, que, entrará nesse if sempre que tiver um cadastro,
				porém, caso ele seja null ou vazio, ele gravará no banco como NULL, caso contrário, gravará o valor que o usuário informou.
			*/
			if (inserir || atualizar)
			{
				objValidado.setErrosPermitidosIni(
					(strErrosPermitidosIni == null || strErrosPermitidosIni.trim().isEmpty()) ? null : strErrosPermitidosIni.trim()
				);
			}

			if (inserir || atualizar)
			{
				objValidado.setErrosPermitidosFim(
					(strErrosPermitidosFim == null || strErrosPermitidosFim.trim().isEmpty()) ? null : strErrosPermitidosFim.trim()
				);
			}
			
			if (inserir || atualizar)
			{
				objValidado.setTipoAlertaOcorrencia(strtipoAlertaOcorrenciatitulo.equals("")? null : strtipoAlertaOcorrenciatitulo.trim());
			}
			
			if (inserir || atualizar)
			{
				objValidado.setUsuario(strUsuarioResponsavelNome.equals("")? null : strUsuarioResponsavelNome.trim());
			}
			
			if (inserir || atualizar)
			{
				if (strIdClasse == null || "0".equals(strIdClasse)) {
					idClasse = null;
				} else {
					idClasse = strIdClasse;
				}

			    idCor =
			        (strCor == null ||
			         strCor.trim().isEmpty() ||
			         strCor.equals("0"))
			        ? null
			        : Integer.parseInt(strCor.trim());

			    idMarca =
			        (strMarca == null ||
			         strMarca.trim().isEmpty() ||
			         strMarca.equals("0"))
			        ? null
			        : Integer.parseInt(strMarca.trim());

			    idModelo =
			        (strModelo == null ||
			         strModelo.trim().isEmpty() ||
			         strModelo.equals("0"))
			        ? null
			        : Integer.parseInt(strModelo.trim());

			    textoAdesivo =
			        (strTextoAdesivo == null ||
			         strTextoAdesivo.trim().isEmpty())
			        ? null
			        : strTextoAdesivo.trim();
			}	
			
	    	// Apenas para atualização de registro
			if ((atualizar || encerrar) && (strId == null || strId.trim().equals("") || strId.trim().equals("0")))
			{
				objValidado.setValido(false);
				String msg = "Identificador do registro não informado!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			try
			{
				if (strTipoAlertaOcorrencia != null && !strTipoAlertaOcorrencia.trim().equals("") && !strTipoAlertaOcorrencia.trim().equals("0"))
					idTipoAlertaOcorrencia = UUID.fromString(strTipoAlertaOcorrencia);
				
				if (strDataIni != null && !strDataIni.trim().equals(""))
					dataIni = sdf.parse(strDataIni);
				
				if (strDataFim != null && !strDataFim.trim().equals(""))
					dataFim = sdf.parse(strDataFim);
				
				dataAtual = sdf.parse(sdf.format(new Date()));
				
				if (strBuscarApenasCadAtivo != null && !strBuscarApenasCadAtivo.trim().equals(""))
					buscarApenasCadAtivo = Boolean.parseBoolean(strBuscarApenasCadAtivo);
				
				if (strBuscarApenasPlacaComCoringa != null && !strBuscarApenasPlacaComCoringa.trim().equals(""))
					buscarApenasPlacaComCoringa = Boolean.parseBoolean(strBuscarApenasPlacaComCoringa);
				
				if (atualizar && dataFim != null && strCadAtivo != null && !strCadAtivo.trim().equals(""))
				{
					boolean cadAtivo = Boolean.parseBoolean(strCadAtivo);
					encerrar = !cadAtivo;
				}

	    		// Validar se o tipo de Alerta/Ocorrência precisa ter a placa informada para monitoramento.
				if (inserir)
				{
					boolean permiteCadMonitoradoSemPlaca = TiposAlertaOcorrencias.PermiteCadMonitoradoSemPlaca(idTipoAlertaOcorrencia);
					
					if (placa == null && !permiteCadMonitoradoSemPlaca)
					{
						objValidado.setValido(false);
						String msg = "Placa não informada!";
						logger.error(msg);	
						respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
						return objValidado;
					}
					
					String msgValidacao = "Já existe um cadastro ativo para o tipo " + (placa != null ? "e placa" : "") + " informado" + (placa != null ? "s" : "") + "!";
					if (permiteCadMonitoradoSemPlaca)
					{
						boolean possuiCadastroAtivo = false;
						
		    			// Se permitir cadastro de monitoramento sem a placa, verificar se teve placa informada ou não para executar a validação correta.
		    			// Regra:
		    			//	1. Permite sem placa, mas teve a placa informada: validar se possui cadastro ativo para o mesmo tipo e mesma placa
		    			//	2. Permite sem placa e não teve a placa informada: validar se possui cadastro ativo para o mesmo tipo
						if (placa != null)
							possuiCadastroAtivo = VeiculosMonitorados.PossuiCadastroAtivo(idTipoAlertaOcorrencia, placa);
						else
							possuiCadastroAtivo = VeiculosMonitorados.PossuiCadastroAtivo(idTipoAlertaOcorrencia);
						
						if (possuiCadastroAtivo)
						{
							objValidado.setValido(false);
							logger.error(msgValidacao);	
							respostaXML.EnviarRespostaRequisicaoXML(response, false, msgValidacao);
							return objValidado;
						}
					}
					else
					{
						boolean possuiCadastroAtivo = VeiculosMonitorados.PossuiCadastroAtivo(idTipoAlertaOcorrencia, placa);
						
						if (possuiCadastroAtivo)
						{
							objValidado.setValido(false);
							logger.error(msgValidacao);	
							respostaXML.EnviarRespostaRequisicaoXML(response, false, msgValidacao);
							return objValidado;
						}
					}
				}

				// Apenas para atualização de registro
				if ((atualizar || encerrar) && (strId != null && !strId.trim().equals("") && !strId.trim().equals("0")))
					id = UUID.fromString(strId);
			}
			catch (Exception e)
			{
				objValidado.setValido(false);
				String msg = "Erro ao preparar dados para consulta!";
				logger.error(msg, e);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			if ( inserir && (dataIni != null && dataIni.before(dataAtual)) )
			{
				objValidado.setValido(false);
				String msg = "A data de início deve ser maior ou igual a data atual!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			if ( !consultar && (dataFim != null && dataFim.before(dataAtual)) )
			{
				objValidado.setValido(false);
				String msg = "A data fim deve ser maior ou igual a data atual!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}
			
			if ( (dataIni != null && dataFim != null) && dataFim.before(dataIni) )
			{
				objValidado.setValido(false);
				String msg = "A data de início deve ser menor que a data fim!";
				logger.error(msg);	
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				return objValidado;
			}

			if (strMonitorarSomenteEste != null && !strMonitorarSomenteEste.trim().equals("")) {
				monitorarSomenteEste = Integer.parseInt(strMonitorarSomenteEste);
			}
		}
		catch(Exception e)
		{
			objValidado.setValido(false);
			String msg = "Ocorreu um erro ao consultar veículos monitorados!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return objValidado;
		}
		
		if (objValidado.isValido())
		{
			objValidado.setIdTipoAlertaOcorrencia(	idTipoAlertaOcorrencia		);
			objValidado.setDataInicio(				dataIni						);
			objValidado.setDataFim(					dataFim						);
			objValidado.setPlaca(					placa						);
			objValidado.setApenasCadAtivo(			buscarApenasCadAtivo		);
			objValidado.setApenasPlacaComCoringa(	buscarApenasPlacaComCoringa	);
			objValidado.setNome(					nome						);
			objValidado.setMonitorarSomenteEste(	monitorarSomenteEste		);
		    // Dados do veículo
			objValidado.setIdClasse(idClasse);
		    objValidado.setIdCor(idCor);
		    objValidado.setIdMarca(idMarca);
		    objValidado.setIdModelo(idModelo);
		    objValidado.setTextoAdesivo(textoAdesivo);
			if (encerrar)
			{
				if (dataFim == null)
				{
					Calendar calDataFim = Calendar.getInstance();
					calDataFim.setTime(new Date());
					calDataFim.set(Calendar.HOUR, 0);
					calDataFim.set(Calendar.MINUTE, 0);
					calDataFim.set(Calendar.SECOND, 0);
					calDataFim.set(Calendar.MILLISECOND, 0);
					dataFim = calDataFim.getTime();
				}
				objValidado.setDataFim(dataFim);
				objValidado.setDataInativacao(new Date());
				objValidado.setIdUsuarioInativacao(idUsuario);
			}
			else
				objValidado.setDataFim(dataFim);
			
			if (atualizar || encerrar)
				objValidado.setId(id);
				objValidado.setId_usuario_responsavel(idUsuarioResponsavel);
		}
		
		return objValidado;
	}
	
	private void ObterVeiculosMonitorados(UUID idTipoAlertaOcorrencia, Date dataIni, Date dataFim, String placa, boolean buscaApenasCadAtivo, boolean buscarApenasPlacaComCoringa, boolean supervisionado, boolean privado,Integer idUsuario , Paginacao paginacao, HttpServletResponse response)
	{
		try
		{
			VeiculosMonitorados veiculosMonitorados = VeiculosMonitorados.ObterListaVeiculosMonitorados(idTipoAlertaOcorrencia, dataIni, dataFim, placa, buscaApenasCadAtivo, buscarApenasPlacaComCoringa,supervisionado, privado,idUsuario, paginacao);
			EnviarRespostaXML(response, veiculosMonitorados);
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao consultar veiculos monitorados!";
			logger.error(msg, e);	
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void ExportarCadastrosAtivos(HttpServletResponse response, boolean aguardar, boolean enviarRespostaTela)
	{
		try
		{
			String diretorioDestino = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_cad_monitorado");
			String arquivoDestino = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("nome_arquivo_cad_monitorado");
			
			if(enviarRespostaTela && (diretorioDestino == null || diretorioDestino.trim().length() == 0))
			{
				String msg = "Diretório do arquivo não informado!";
				logger.error(msg);
				if (enviarRespostaTela)
					respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				
				return;
			}
			if(enviarRespostaTela && (arquivoDestino == null || arquivoDestino.trim().length() == 0))
			{
				String msg = "Nome do arquivo não informado!";
				logger.error(msg);
				if (enviarRespostaTela)
					respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
				
				return;
			}
			
//			String diretorioFTP = "/OLINDA_PE/CADASTROS/cad_monitorado_ativo.json";
			String diretorioFTP = diretorioDestino+"/"+arquivoDestino;
			Gson gson = new Gson();
			
			VeiculosMonitorados veiculosMonitorados = VeiculosMonitorados.ObterCadastrosAtivos();
			
			AcessoFTP acessoFTP = new AcessoFTP(diretorioFTP);
			
			byte[] jsonCadastros = gson.toJson(veiculosMonitorados).getBytes();
			
			acessoFTP.EnviarArquivo(diretorioFTP, jsonCadastros);
			
			if (aguardar)
			{
				int max = 2200, min = 1450;
				Random random = new Random();
				int sleep = random.nextInt(max - min) + min;
				Thread.sleep(sleep);
			}
			
			if (enviarRespostaTela)
				respostaXML.EnviarRespostaRequisicaoXML(response, true, "Cadastros enviados aos equipamentos!");
		}
		catch(Exception e)
		{
			String msg = "Ocorreu um erro ao exportar os cadastros ativos!";
			logger.error(msg, e);
			if (enviarRespostaTela)
				respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			
			return;
		}
	}
	
	private void InserirVeiculoMonitorado(VeiculoMonitorado veiculoMonitorado, HttpServletResponse response)
	{
		List<ConfiguracaoServidorSmtp> servidores = ConfiguracaoProvider.getInstance().getListaServidoresSmtp();
		ConfiguracaoServidorSmtp servidor = servidores.get(0);
		ServicoEmailMuralha servicoEmail = new ServicoEmailMuralha(
			servidor.getUser(),
			servidor.getPassword(),
			servidor.getHost(),
			servidor.getPort()
		);
		
		String msg = null;
		boolean sucessoCadastro = true;
		boolean sucessoEmail = true;
		
		try
		{		
			sucessoCadastro = VeiculosMonitorados.InserirVeiculoMonitorado(veiculoMonitorado);
			
			
			if (veiculoMonitorado.getId_usuario_responsavel() != null && veiculoMonitorado.getId_usuario_responsavel() != 0) {
				List<Usuario> usuarios = UsuarioServlet.obterListaUsuariosEmail();
				List<InternetAddress> destinatarios = new ArrayList<>();
				for (Usuario u : usuarios) {
					if (u.getEmail() != null && !u.getEmail().isEmpty()) {
						destinatarios.add(new InternetAddress(u.getEmail().trim()));
					}
				}

				try {					
					sucessoEmail = servicoEmail.enviarEmailNotificacaoUsuarioResponsavel(
						new InternetAddress("admin@consilux.com"), // remetente
						destinatarios,
						veiculoMonitorado.getPlaca(),
						veiculoMonitorado.getTipoAlertaOcorrencia(),
						veiculoMonitorado.getUsuario()				            // lista de e-mails dos usuários
					);
				} catch (Exception e) {
					e.printStackTrace();
					sucessoEmail = false;
				}
			}
			
			if (sucessoCadastro)
				msg = "Veículo monitorado cadastrado com sucesso!";
			else
				msg = "Falha ao cadastrar veículo monitorado!";
			
			respostaXML.EnviarRespostaRequisicaoXML(response, sucessoCadastro, msg);
		}
		catch(Exception e)
		{
			logger.error("Erro ao InserirVeiculoMonitorado(): " + e.getMessage(), e);
			msg = "Ocorreu um erro ao cadastrar o veiculo monitorado!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void AtualizarVeiculoMonitorado(VeiculoMonitorado veiculoMonitorado, HttpServletResponse response)
	{
		List<ConfiguracaoServidorSmtp> servidores = ConfiguracaoProvider.getInstance().getListaServidoresSmtp();
		ConfiguracaoServidorSmtp servidor = servidores.get(0);
		ServicoEmailMuralha servicoEmail = new ServicoEmailMuralha(
			servidor.getUser(),
			servidor.getPassword(),
			servidor.getHost(),
			servidor.getPort()
		);
		
		String msg = null;
		boolean sucesso = true;
		boolean sucessoEmail = true;
		
		try
		{
			sucesso = VeiculosMonitorados.AtualizarVeiculoMonitorado(veiculoMonitorado);
			
			if (veiculoMonitorado.getId_usuario_responsavel() != null && veiculoMonitorado.getId_usuario_responsavel() != 0) {
				List<Usuario> usuarios = UsuarioServlet.obterListaUsuariosEmail();
				List<InternetAddress> destinatarios = new ArrayList<>();
				for (Usuario u : usuarios) {
					if (u.getEmail() != null && !u.getEmail().isEmpty()) {
						destinatarios.add(new InternetAddress(u.getEmail().trim()));
					}
				}

				try {					
					sucessoEmail = servicoEmail.enviarEmailNotificacaoUsuarioResponsavel(
						new InternetAddress("admin@consilux.com"), // remetente
						destinatarios,
						veiculoMonitorado.getPlaca(),
						veiculoMonitorado.getTipoAlertaOcorrencia(),
						veiculoMonitorado.getUsuario()				            
					);
				} catch (Exception e) {
					e.printStackTrace();
					sucessoEmail = false;
				}
			}
			
			if (sucesso)
				msg = "Veículo monitorado atualizado com sucesso!";
			else
				msg = "Falha ao atualizar veículo monitorado!";
			
			respostaXML.EnviarRespostaRequisicaoXML(response, sucesso, msg);
		}
		catch(Exception e)
		{
			logger.error("Erro ao AtualizarVeiculoMonitorado(): " + e.getMessage(), e);
			msg = "Ocorreu um erro ao atualizar o veiculo monitorado!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EncerrarVeiculoMonitorado(VeiculoMonitorado veiculoMonitorado, HttpServletResponse response)
	{
		String msg = null;
		boolean sucesso = true;
		
		try
		{
			sucesso = VeiculosMonitorados.EncerrarVeiculoMonitorado(veiculoMonitorado);
			
			if (sucesso)
				msg = "Monitoramento encerrado com sucesso";
			else
				msg = "Falha ao encerrar monitoramento";
			
			respostaXML.EnviarRespostaRequisicaoXML(response, sucesso, msg);
		}
		catch(Exception e)
		{
			logger.error("Erro ao EncerrarVeiculoMonitorado(): " + e.getMessage(), e);
			msg = "Ocorreu um erro ao encerrar o veiculo monitorado!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}
	}
	
	private void EnviarRespostaXML(HttpServletResponse response, VeiculosMonitorados veiculosMonitorados) throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(VeiculosMonitorados.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(veiculosMonitorados, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			logger.info("EnviarRespostaXML():: Registros enviados: " + Integer.toString(veiculosMonitorados.getListaVeiculosMonitorados().size()) );
			veiculosMonitorados = null;
			
		}
		catch(Exception e)
		{
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta de veículos monitorados!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
	
	private void EnviarVeiculoMonitoradoPorIdXML(HttpServletResponse response, VeiculoMonitorado monitorado) 
		throws JAXBException, IOException
	{
		JAXBContext context;
		try
		{
			//Formando dados para envio
			context = JAXBContext.newInstance(VeiculoMonitorado.class);
			Marshaller marsHall = context.createMarshaller();
			marsHall.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			StringWriter sw = new StringWriter();
			marsHall.marshal(monitorado, sw);
			String xml = sw.toString();
			sw.close();
			
			response.setHeader("Content-Type", "text/xml");
			response.setStatus(HttpServletResponse.SC_OK);
			response.getWriter().write(xml);
			response.getWriter().flush();
			
			monitorado = null;
		}
		catch(Exception e)
		{
			logger.error("Erro ao EnviarRespostaXML(): " + e.getMessage(), e);
			String msg = "Ocorreu um erro ao retornar o resultado da consulta do veículo monitorado!";
			logger.error(msg);
			respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
			return;
		}	
	}
}
