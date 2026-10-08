package muralha.digital.guarnicao;

import java.io.IOException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.consilux.model.Usuario;
import com.google.gson.Gson;

@WebServlet("/MuralhaDigital/Guarnicao")
public class GuarnicaoServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(GuarnicaoServlet.class);
	private static final Gson gson = new Gson();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//String acao = request.getParameter("acao");

		try {
			 String msg = null;
	            String strAcao = request.getParameter("acao");


	            if(strAcao.equals("listar"))
	                listarGuarnicoes(response); 
	            
	            else if(strAcao.equals("listarIntegrantes"))
	            	listarIntegrantes(request, response);

				else if(strAcao.equals("buscarPorId"))
            		buscarGuarnicaoPorId(request, response);

				else if(strAcao.equals("buscarDetalhesPorId"))
            		buscarInfoDiariaPorId(request, response);
	            
				else if(strAcao.equals("listarGuarnicoesDiariasPorGuarnicao"))
					listarGuarnicoesDiariasPorGuarnicao(request, response);

			
			 else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida ou não informada.");
			}
		} catch (Exception e) {
			logger.error("Erro no doGet da GuarnicaoServlet", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String acao = request.getParameter("acao");

		try {
			if ("cadastrar".equalsIgnoreCase(acao)) {
				cadastrarGuarnicao(request, response);
			} 

			else if ("cadastrarInfoDiaria".equalsIgnoreCase(acao)) {
				cadastrarInfoDiaria(request, response);
			}

			else if ("atualizarTelefone".equalsIgnoreCase(acao)) {
				atualizarTelefone(request, response);
			}
			
			else if ("atualizarGuarnicao".equalsIgnoreCase(acao)) {
				atualizarGuarnicao(request, response);
			}
			
			else if ("deletarGuarnicao".equalsIgnoreCase(acao)) {
				deletarSoftGuarnicao(request, response);
			}
			
			 else {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação POST inválida ou não informada.");
			}
		} catch (Exception e) {
			logger.error("Erro no doPost da GuarnicaoServlet", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno no servidor.");
		}
	}

	private void listarGuarnicoes(HttpServletResponse response) throws IOException {
		List<Guarnicao> lista = Guarnicoes.listarTodas();
		String json = gson.toJson(lista);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	private void buscarGuarnicaoPorId(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String idParam = request.getParameter("id");
		if (idParam == null || idParam.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro 'id' não informado.");
			return;
		}

		try {
			int id = Integer.parseInt(idParam);
			Guarnicao guarnicao = Guarnicao.buscarPorId(id);

			if (guarnicao != null) {
				String json = gson.toJson(guarnicao);
				response.setContentType("application/json; charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Guarnição não encontrada.");
			}
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro 'id' inválido.");
		}
	}

		private void buscarInfoDiariaPorId(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String idParam = request.getParameter("id");
		if (idParam == null || idParam.isEmpty()) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro 'id' não informado.");
			return;
		}

		try {
			int id = Integer.parseInt(idParam);
			GuarnicaoDiario guarnicao = Guarnicao.buscarGuarnicaoDiarioPorId(id);

			if (guarnicao != null) {
				String json = gson.toJson(guarnicao);
				response.setContentType("application/json; charset=UTF-8");
				response.setCharacterEncoding("UTF-8");
				response.getWriter().write(json);
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND, "Guarnição não encontrada.");
			}
		} catch (NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro 'id' inválido.");
		}
	}

private void listarGuarnicoesDiariasPorGuarnicao(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String idParam = request.getParameter("idGuarnicao");
    if (idParam == null || idParam.isEmpty()) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro 'idGuarnicao' não informado.");
        return;
    }

    try {
        int idGuarnicao = Integer.parseInt(idParam);
        List<GuarnicaoDiario> guarnicoes = Guarnicao.listarGuarnicoesDiariasPorGuarnicao(idGuarnicao);

        String json = gson.toJson(guarnicoes);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    } catch (NumberFormatException e) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parâmetro 'idGuarnicao' inválido.");
    } catch (Exception e) {
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro ao listar guarnições diárias.");
    }
}

private void cadastrarInfoDiaria(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
        int idGuarnicao = Integer.parseInt(request.getParameter("idGuarnicao"));
        Date data = java.sql.Date.valueOf(request.getParameter("data")); 
        double quilometragem = Double.parseDouble(request.getParameter("quilometragem"));
        String horaIni = request.getParameter("horaIni");
        String horaFim = request.getParameter("horaFim");
        String setoresPatrulhados = request.getParameter("setoresPatrulhados");
        String meioTransporte = request.getParameter("meioTransporte");
        int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));

        GuarnicaoDiario diario = new GuarnicaoDiario();
        diario.setIdGuarnicao(idGuarnicao);
        diario.setData(data);
        diario.setQuilometragem(quilometragem);
        diario.setHoraIni(horaIni);
        diario.setHoraFim(horaFim);
        diario.setSetoresPatrulhados(setoresPatrulhados);
        diario.setMeioTransporte(meioTransporte);
        diario.setIdUsuario(idUsuario);

        boolean sucesso = Guarnicao.cadastrarGuarnicaoDiario(diario);

        String json = gson.toJson(diario);
        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);

        if (sucesso) {
        	//
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    } catch (Exception e) {
        logger.error("Erro ao cadastrar info diária", e);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"erro\":\"Dados inválidos ou incompletos\"}");
    }
}

private void atualizarTelefone(HttpServletRequest request, HttpServletResponse response) throws IOException {
    try {
        int idUsuario = Integer.parseInt(request.getParameter("idUsuario"));
        String novoTelefone = request.getParameter("telefone");

        boolean sucesso = Guarnicao.atualizarTelefone(idUsuario, novoTelefone);

        response.setContentType("application/json; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");

        if (sucesso) {
            response.getWriter().write("{\"mensagem\":\"Telefone atualizado com sucesso\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"erro\":\"Falha ao atualizar telefone\"}");
        }

    } catch (Exception e) {
        Logger.getLogger(getClass()).error("Erro ao atualizar telefone", e);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("{\"erro\":\"Parâmetros inválidos ou incompletos\"}");
    }
}

	/**
	 * Cadastra uma nova guarnição no banco de dados, incluindo seus integrantes.
	 * Os dados são esperados nos parâmetros da requisição.
	 * Retorna uma mensagem JSON de sucesso ou erro.
	 *
	 * @param request  Objeto HttpServletRequest contendo os parâmetros da guarnição.
	 * @param response Objeto HttpServletResponse para enviar a resposta.
	 * @throws IOException Em caso de erro de I/O.
	 */
	private void cadastrarGuarnicao(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");

	    try {
	        String nome = request.getParameter("nome");
	        String idUsuarioResponsavelStr = request.getParameter("id_usuario_responsavel");
	        String idUsuarioCriacaoStr = request.getParameter("id_usuario_criacao");
	        String integrantesStr = request.getParameter("integrantes");
	        String disponivelStr = request.getParameter("disponivel");
	        String meiosDeslocamento = request.getParameter("meiosDeslocamento");

	        List<Integer> idIntegrantes = new ArrayList<>();
	        List<String> listaMeios = new ArrayList<>();

	        if (integrantesStr != null && !integrantesStr.isEmpty()) {
	            for (String id : integrantesStr.split(",")) {
	                idIntegrantes.add(Integer.parseInt(id.trim()));
	            }
	        }
	        
	        if (meiosDeslocamento != null && !meiosDeslocamento.isEmpty()) {
	            for (String meios : meiosDeslocamento.split(",")) {
	            	listaMeios.add(meios);
	            }
	        }

	        if (nome == null || nome.isEmpty() ||
	            idUsuarioResponsavelStr == null || idUsuarioResponsavelStr.isEmpty() ||
	            idUsuarioCriacaoStr == null || idUsuarioCriacaoStr.isEmpty()) {

	            response.getWriter().write(gson.toJson(
	                new ApiResponse(false, "Parâmetros 'nome', 'id_usuario_responsavel' ou 'id_usuario_criacao' não informados.")));
	            return;
	        }

	        int idUsuarioResponsavel = Integer.parseInt(idUsuarioResponsavelStr);
	        int idUsuarioCriacao = Integer.parseInt(idUsuarioCriacaoStr);
	        int disponivel = (disponivelStr != null && !disponivelStr.isEmpty()) ? Integer.parseInt(disponivelStr) : 1;

	        Guarnicao novaGuarnicao = new Guarnicao();
	        novaGuarnicao.setNome(nome);
	        novaGuarnicao.setId_usuario_responsavel(idUsuarioResponsavel);
	        novaGuarnicao.setId_usuario_criacao(idUsuarioCriacao);
	        novaGuarnicao.setDisponivel(disponivel);

	        int idGerado = Guarnicao.salvar(novaGuarnicao);
	        int idGeradoMeiosDeslocamento = Guarnicao.salvarMeiosDeslocamento(idGerado, listaMeios);
	        if (idGerado > 0) {
	            boolean sucessoIntegrantes = Guarnicao.salvarIntegrantes(idGerado, idIntegrantes);

	            if (sucessoIntegrantes) {
	                response.getWriter().write(gson.toJson(new ApiResponse(true, "Guarnição cadastrada com sucesso!")));
	                logger.info("Guarnição '" + nome + "' cadastrada com sucesso (ID " + idGerado + " ID-MEIOS-DESLOCAMENTO " + idGeradoMeiosDeslocamento + ").");
	            } else {
	                response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro ao salvar os integrantes da guarnição.")));
	                logger.warn("Falha ao salvar integrantes da guarnição '" + nome + "'.");
	            }
	        } else {
	            response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro ao salvar a guarnição.")));
	            logger.warn("Falha ao salvar a guarnição '" + nome + "'.");
	        }

	    } catch (NumberFormatException e) {
	        logger.error("Erro de formato numérico nos parâmetros da guarnição.", e);
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro: parâmetro numérico inválido.")));

	    } catch (Exception e) {
	        logger.error("Erro ao cadastrar guarnição: " + e.getMessage(), e);
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro interno ao cadastrar guarnição.")));
	    }
	}
	
	/**
	 * Atualiza uma guarnição existente no banco de dados, incluindo seus integrantes e meios de deslocamento.
	 * Espera os dados nos parâmetros da requisição.
	 * Retorna uma mensagem JSON de sucesso ou erro.
	 *
	 * @param request  Objeto HttpServletRequest contendo os parâmetros da guarnição.
	 * @param response Objeto HttpServletResponse para enviar a resposta.
	 * @throws IOException Em caso de erro de I/O.
	 */
	private void atualizarGuarnicao(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");
	    
	    try {
	        String idStr = request.getParameter("idGuarnicao");
	        String nome = request.getParameter("nome");
	        String idUsuarioResponsavelStr = request.getParameter("idUsuarioResponsavel");
	        String integrantesStr = request.getParameter("integrantes");
	        String meiosDeslocamento = request.getParameter("meiosDeslocamento");
	        String idUsuarioAlteracaoStr = request.getParameter("idUsuarioAlteracao");

	        if (idStr == null || idStr.isEmpty() ||
	            nome == null || nome.isEmpty() ||
	            idUsuarioResponsavelStr == null || idUsuarioResponsavelStr.isEmpty()) {

	            response.getWriter().write(gson.toJson(
	                new ApiResponse(false, "Parâmetros obrigatórios 'id', 'nome' ou 'id_usuario_responsavel' não informados.")));
	            return;
	        }

	        int idGuarnicao = Integer.parseInt(idStr);
	        int idUsuarioResponsavel = Integer.parseInt(idUsuarioResponsavelStr);
	        int idUsuarioAlteracao = Integer.parseInt(idUsuarioAlteracaoStr);

	        List<Integer> idIntegrantes = new ArrayList<>();
	        if (integrantesStr != null && !integrantesStr.isEmpty()) {
	            for (String id : integrantesStr.split(",")) {
	                idIntegrantes.add(Integer.parseInt(id.trim()));
	            }
	        }

	        List<String> listaMeios = new ArrayList<>();
	        if (meiosDeslocamento != null && !meiosDeslocamento.isEmpty()) {
	            for (String meios : meiosDeslocamento.split(",")) {
	                listaMeios.add(meios.trim());
	            }
	        }

	        Guarnicao guarnicaoAtualizada = new Guarnicao();
	        guarnicaoAtualizada.setId(idGuarnicao);
	        guarnicaoAtualizada.setNome(nome);
	        guarnicaoAtualizada.setId_usuario_responsavel(idUsuarioResponsavel);
	        guarnicaoAtualizada.setId_usuario_alt(idUsuarioAlteracao);

	        boolean atualizado = Guarnicao.atualizar(guarnicaoAtualizada);

	        if (atualizado) {
	            boolean sucessoIntegrantes = Guarnicao.atualizarIntegrantes(idGuarnicao, idIntegrantes);
	            int idMeiosDeslocamento = Guarnicao.atualizarMeiosDeslocamento(idGuarnicao, listaMeios);

	            if (sucessoIntegrantes && idMeiosDeslocamento > 0) {
	                response.getWriter().write(gson.toJson(new ApiResponse(true, "Guarnição atualizada com sucesso!")));
	                logger.info("Guarnição '" + nome + "' atualizada com sucesso (ID " + idGuarnicao + ").");
	            } else {
	                response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro ao atualizar integrantes ou meios de deslocamento.")));
	                logger.warn("Falha ao atualizar integrantes ou meios de deslocamento da guarnição '" + nome + "'.");
	            }
	        } else {
	            response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro ao atualizar dados da guarnição.")));
	            logger.warn("Falha ao atualizar dados da guarnição '" + nome + "'.");
	        }

	    } catch (NumberFormatException e) {
	        logger.error("Erro de formato numérico nos parâmetros da guarnição.", e);
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro: parâmetro numérico inválido.")));

	    } catch (Exception e) {
	        logger.error("Erro ao atualizar guarnição: " + e.getMessage(), e);
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro interno ao atualizar guarnição.")));
	    }
	}


	private void listarIntegrantes(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    response.setContentType("application/json");
	    response.setCharacterEncoding("UTF-8");

	    String idGrupoStr = request.getParameter("idGrupo");

	    if (idGrupoStr == null || idGrupoStr.isEmpty()) {
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Parâmetro 'idGrupo' não informado.")));
	        return;
	    }

	    try {
	        int idGrupo = Integer.parseInt(idGrupoStr);
	        List<Usuario> usuarios = Usuario.buscaUsuarioPorGrupo(idGrupo);

	        String json = gson.toJson(usuarios);
	        response.getWriter().write(json);
	    } catch (NumberFormatException e) {
	        logger.error("Formato inválido para 'idGrupo'", e);
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Parâmetro 'idGrupo' inválido.")));
	    } catch (Exception e) {
	        logger.error("Erro ao listar integrantes do grupo", e);
	        response.getWriter().write(gson.toJson(new ApiResponse(false, "Erro ao buscar integrantes.")));
	    }
	}

	
    private static class ApiResponse {
        boolean success;
        String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
    
    private void deletarGuarnicao(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idGuarnicao = Integer.parseInt(request.getParameter("idGuarnicao"));

            boolean sucesso = Guarnicao.deletarGuarnicao(idGuarnicao);

            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            if (sucesso) {
                response.getWriter().write("{\"mensagem\":\"Guarnição deletada com sucesso\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"erro\":\"Falha ao deletar guarnição\"}");
            }

        } catch (Exception e) {
            Logger.getLogger(getClass()).error("Erro ao atualizar telefone", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"erro\":\"Parâmetros inválidos ou incompletos\"}");
        }
    }
    
    private void deletarSoftGuarnicao(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idGuarnicao = Integer.parseInt(request.getParameter("idGuarnicao"));
            int acao = Integer.parseInt(request.getParameter("acaoSoft"));

            boolean sucesso = Guarnicao.ativarDesativarGuarnicao(idGuarnicao, acao);

            response.setContentType("application/json; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");

            if (sucesso) {
                response.getWriter().write("{\"mensagem\":\"Guarnição deletada com sucesso\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"erro\":\"Falha ao deletar guarnição\"}");
            }

        } catch (Exception e) {
            Logger.getLogger(getClass()).error("Erro ao atualizar telefone", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"erro\":\"Parâmetros inválidos ou incompletos\"}");
        }
    }
}
