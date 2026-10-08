package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Acesso;
import com.consilux.model.InfracaoSimplificada;
import com.consilux.model.Mensagem;
import com.consilux.model.beans.FiltroBean;
import com.consilux.model.exception.ModelException;

/**
 * Servlet implementation class CadastrarFiltroServlet
 */
public class CadastrarFiltroServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CadastrarFiltroServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	private final String ERRO_SQL_CRITERIO = "O critério digitado é inválido.";
	private final String ERRO_NOME_FILTRO = "O filtro deve possuir um nome.";
	private final String ERRO_DATE_VALIDADE = "Informe corretamente data e hora de validade.";
	private final String ERRO_SET_INCONSISTENCIA_ESPERA = "É preciso informar uma inconsistencia ou espera para alterar a infração.";
	private final String DERRO_DADOS_MINIMOS = "É necessario preencher ao menos 1 dos dandos mínimos: Enquadramento, Data Inicial, Data Final ou Local.";
	private String enviar_erro = null;

	private final String CONFIRMA = "OK";

	private Logger logger = Logger.getLogger(CadastrarFiltroServlet.class);

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response);
		
		if (!acesso.verificaAcesso(false))
			throw new ServletException("Usuário não atenticado!");


		boolean dadosMinimos = false;

		Integer idUsuario = acesso.getUsuario().getId();
		
		enviar_erro = null;

		FiltroBean fb = new FiltroBean();

		String txt_nome_filtro = request.getParameter("txt_nome_filtro");
		//String filtro_inconsistencias = request.getParameter("filtro_inconsistencias");
		String filtro_enquadramento = request.getParameter("filtro_enquadramento");
		String filtro_processo = request.getParameter("filtro_processo");
		String txt_local = request.getParameter("txt_local");
		String sel_local = request.getParameter("sel_local");
		String sPista = request.getParameter("pista");
		String filtro_tipo_veiculo = request.getParameter("filtro_tipo_veiculo");
		
		
		String txt_data_infracao_ini = request.getParameter("txt_data_infracao_ini") != null ? request.getParameter("txt_data_infracao_ini").trim() : null;
		txt_data_infracao_ini = txt_data_infracao_ini != null && txt_data_infracao_ini.length() == 0 ? null : txt_data_infracao_ini;

		String txt_data_infracao_fim = request.getParameter("txt_data_infracao_fim") != null ? request.getParameter("txt_data_infracao_fim").trim() : null;
		txt_data_infracao_fim = txt_data_infracao_fim != null && txt_data_infracao_fim.length() == 0 ? null : txt_data_infracao_fim;
		
		String txt_hora_infracao_ini = request.getParameter("txt_hora_infracao_ini") != null ? request.getParameter("txt_hora_infracao_ini").trim() : null;
		txt_hora_infracao_ini = txt_hora_infracao_ini != null && txt_hora_infracao_ini.length() == 0 ? null : txt_hora_infracao_ini;

		String txt_hora_infracao_fim = request.getParameter("txt_hora_infracao_fim") != null ? request.getParameter("txt_hora_infracao_fim").trim() : null;
		txt_hora_infracao_fim = txt_hora_infracao_fim != null && txt_hora_infracao_fim.length() == 0 ? null : txt_hora_infracao_fim;
		
		String sTotalInfracoes = request.getParameter("totalInfracoes") != null ? request.getParameter("totalInfracoes").trim() : null;
		sTotalInfracoes = sTotalInfracoes != null && sTotalInfracoes.length() == 0 ? null : sTotalInfracoes;

		String validade_data = request.getParameter("validade_data");
		String validade_hora = request.getParameter("validade_hora");
		String sql_criterio = request.getParameter("sql_criterio");
		String confirmacao = request.getParameter("confirmacao");
		String set_inconsistencia = request.getParameter("set_inconsistencia");
		String espera = request.getParameter("espera");


		if("".equals(txt_nome_filtro.trim())){
			enviar_erro = ERRO_NOME_FILTRO;
			logger.warn(ERRO_NOME_FILTRO);
		}

		//Integer id_filtro_inconsistencias = null;
		Integer id_filtro_enquadramento = null;
		Integer id_filtro_processo = null;


		//Teste se foi selecionado algum filtro para o enquadramento para preencher os dados mínimos
		if(!"".equalsIgnoreCase(filtro_enquadramento) && filtro_enquadramento != null){
			id_filtro_enquadramento = Integer.parseInt(filtro_enquadramento);
			dadosMinimos = true;
		}
		if(id_filtro_enquadramento == null || id_filtro_enquadramento < 1){
			id_filtro_enquadramento = null;
			dadosMinimos = false;
		}
		//Teste se foi selecionado algum filtro para o enquadramento para preencher os dados mínimos
		if(!"".equalsIgnoreCase(filtro_processo) && filtro_processo != null){
			id_filtro_processo = Integer.parseInt(filtro_processo);
			dadosMinimos = true;
		}
		if(id_filtro_processo == null || id_filtro_processo < 1){
			id_filtro_processo = null;
			dadosMinimos = false;
		}

		Integer id_local = null;
		Integer pista = null;

		//Teste se foi selecionado algum filtro para o local para preencher os dados mínimos
		if(!"".equalsIgnoreCase(txt_local) && txt_local != null && !"0".equals(txt_local)){
			id_local = Integer.parseInt(txt_local);
			dadosMinimos = true;
		}
		
		if (sPista != null && ExpValida.NATURAL.validar(sPista)) {
			pista = Integer.valueOf(sPista);
		}

		//Teste se foi selecionado algum filtro para o classe veiculo para preencher os dados mínimos
		if(!"null".equalsIgnoreCase(filtro_tipo_veiculo)){
			dadosMinimos = true;
		}

		if(null != sql_criterio && !"".equalsIgnoreCase(sql_criterio.trim())){
			dadosMinimos = true;
		}

	    if (txt_data_infracao_ini != null && !ExpValida.DATA.validar(txt_data_infracao_ini)) {
	        new Mensagem(response).showErro("Data inicial da infração enviada inválida!");
	        return;
	    }
	    if (txt_data_infracao_fim != null && !ExpValida.DATA.validar(txt_data_infracao_fim)) {
	        new Mensagem(response).showErro("Data final da infração enviada inválida!");
	        return;
	    }
	    
	    if (txt_hora_infracao_ini != null && !ExpValida.HORA_MINUTO.validar(txt_hora_infracao_ini)) {
	        new Mensagem(response).showErro("Hora inicial da infração enviada inválida!");
	        return;
	    }
	    
	    if (txt_hora_infracao_fim != null && !ExpValida.HORA_MINUTO.validar(txt_hora_infracao_fim)) {
	        new Mensagem(response).showErro("Hora final da infração enviada inválida!");
	        return;
	    }

	    if (sTotalInfracoes != null && !ExpValida.NATURAL.validar(sTotalInfracoes)) {
	        new Mensagem(response).showErro("Total de infrações enviado inválido!");
	        return;
	    }

		Date dtIni = null;
		Date dtFim = null;
		Date dtValidade = null;
		Integer set_id_inconsistencia = null;
		Boolean bEspera = null;

		if(!"".equalsIgnoreCase(set_inconsistencia) && set_inconsistencia != null && !"-1".equals(set_inconsistencia))
			set_id_inconsistencia = Integer.parseInt(set_inconsistencia);
		
		if (espera != null && espera.equals("checked"))
			bEspera = true;
		
		if(set_id_inconsistencia == null && (bEspera == null)){
			enviar_erro = ERRO_SET_INCONSISTENCIA_ESPERA;
			logger.warn(ERRO_SET_INCONSISTENCIA_ESPERA);
		}
		try {

			if(!"".equalsIgnoreCase(txt_data_infracao_ini) && txt_data_infracao_ini != null){
				if(!"".equalsIgnoreCase(txt_hora_infracao_ini) && txt_hora_infracao_ini != null){
					dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(txt_data_infracao_ini.trim() + " " + txt_hora_infracao_ini.trim());
					dadosMinimos = true;
				}
			}

			if(!"".equalsIgnoreCase(txt_data_infracao_fim) && txt_data_infracao_fim != null){
				if(!"".equalsIgnoreCase(txt_hora_infracao_fim) && txt_hora_infracao_fim != null){
					dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(txt_data_infracao_fim.trim() + " " + txt_hora_infracao_fim.trim()+":59");
					dadosMinimos = true;
				}
			}
			
			if (dtIni != null && dtFim != null && dtIni.after(dtFim)) {
		        new Mensagem(response).showErro("Data inicial é posterior a data final!");
				return;
			}

			if(!"".equalsIgnoreCase(validade_data) && validade_data != null){
				if(!"".equalsIgnoreCase(validade_hora) && validade_hora != null){
					dtValidade = new SimpleDateFormat("dd/MM/yyyy HH:mm").parse(validade_data.trim() + " " + validade_hora.trim());
				}
				else{
					enviar_erro = ERRO_DATE_VALIDADE;
					logger.warn(ERRO_DATE_VALIDADE);
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

		fb.setNomeFiltro(txt_nome_filtro);
		fb.setDtIni(dtIni);
		fb.setDtFim(dtFim);
		fb.setDtValidade(dtValidade);
		fb.setIdclasse(filtro_tipo_veiculo);
		fb.setIdFiltroEnquadramento(id_filtro_enquadramento);
		fb.setIdFiltroProcesso(id_filtro_processo);
		fb.setIdLocal(id_local);
		fb.setIdPista(pista);
		fb.setSetIdInconsistencia(set_id_inconsistencia);
		fb.setSetEspera(bEspera);
		fb.setSqlCriterio(sql_criterio);
		fb.setIdUsuario(idUsuario);

		FiltroDAO cfd = new FiltroDAO();

		if(!CONFIRMA.equalsIgnoreCase(confirmacao) && null == confirmacao){
			try {
				if(enviar_erro == null && dadosMinimos){

					List<Integer> listIdInfracao = cfd.buscarInfracoesExistentesParaFiltro(fb);
					if(listIdInfracao.size() > 0){

						List<InfracaoSimplificada> lInfracaoSimplificada = new ArrayList<InfracaoSimplificada>();
						StringBuilder sb = new StringBuilder();
						
						//pega apenas as 100 primeiras
						int i = 0;
						while(i < listIdInfracao.size()){
							
							sb.append(listIdInfracao.get(i));
							InfracaoSimplificada infracaoSimplificada = null;
							
							if(i < 100){
								infracaoSimplificada = InfracaoSimplificada.buscaInfracaoPorId(listIdInfracao.get(i));
								lInfracaoSimplificada.add(infracaoSimplificada);
							}
							
							i++;
							if(i > listIdInfracao.size())
								sb.append(";");
						}
						
						request.setAttribute("infracoes", lInfracaoSimplificada);
						request.setAttribute("totalInfracoes", lInfracaoSimplificada.size());
					}

					request.setAttribute("desabilitar", "disabled=\"disabled\"");
					request.setAttribute("somente_leitura_select", "onfocus=\"this.defaultIndex=this.selectedIndex;\" onchange=\"this.selectedIndex=this.defaultIndex;\"");
					request.setAttribute("somente_leitura", "readonly=\"readonly\"");

					request.setAttribute("txt_nome_filtro", txt_nome_filtro);
					request.setAttribute("filtro_enquadramento", filtro_enquadramento);
					request.setAttribute("filtro_processo", filtro_processo);
					request.setAttribute("txt_local", txt_local);
					request.setAttribute("sel_local", sel_local);				
					request.setAttribute("pista", sPista);
					request.setAttribute("filtro_tipo_veiculo", filtro_tipo_veiculo);
					request.setAttribute("txt_data_infracao_ini", txt_data_infracao_ini);
					request.setAttribute("txt_data_infracao_fim", txt_data_infracao_fim);
					request.setAttribute("txt_hora_infracao_ini", txt_hora_infracao_ini);
					request.setAttribute("txt_hora_infracao_fim", txt_hora_infracao_fim);

					request.setAttribute("sql_criterio", sql_criterio);

					request.setAttribute("validade_data", validade_data);
					request.setAttribute("validade_hora", validade_hora);
					request.setAttribute("set_inconsistencia", set_inconsistencia);
					request.setAttribute("espera", espera);

					String goUrl = "javascript: history.back()";
					goUrl = URLEncoder.encode(goUrl, "UTF-8");
					
					RequestDispatcher rd = request.getRequestDispatcher("/ferramenta/cadastro_filtros_confirmacao.jsp");
					rd.forward(request, response);

				}
			} catch (ConexaoException e) {
				logger.error(e.getMessage());
			} catch (SQLException e) {
				logger.info(ERRO_SQL_CRITERIO);
				enviar_erro = ERRO_SQL_CRITERIO+" ERRO: \n"+e.getMessage();
			} catch (ModelException e) {
				logger.error(e.getMessage());
			}
		}

		if(CONFIRMA.equalsIgnoreCase(confirmacao) || enviar_erro != null){
			try {

				if(!dadosMinimos){
					enviar_erro = DERRO_DADOS_MINIMOS;
				}

				String goUrl = "javascript: history.back()";

				if(enviar_erro != null || !dadosMinimos){
					try {
						goUrl = URLEncoder.encode(goUrl, "UTF-8");
						enviar_erro = URLEncoder.encode(enviar_erro, "UTF-8");
						response.sendRedirect("/includes/erro.jsp?m="+enviar_erro+"&p="+goUrl);
					}catch(Exception e){
						logger.error(e.getMessage());
					}
				}else{
					
					//mover infrações aki nesse ponto
					Integer idFiltro = cfd.cadastrar(fb);
					
					request.setAttribute("txt_nome_filtro", "");
					request.setAttribute("filtro_inconsistencias", "");
					request.setAttribute("filtro_enquadramento", "");
					request.setAttribute("filtro_processo", "");
					request.setAttribute("txt_local", "");
					request.setAttribute("pista", "-1");
					request.setAttribute("filtro_tipo_veiculo", "");
					request.setAttribute("txt_data_infracao_ini", "");
					request.setAttribute("txt_data_infracao_fim", "");
					request.setAttribute("txt_hora_infracao_ini", "");
					request.setAttribute("txt_hora_infracao_fim", "");

					request.setAttribute("validade_data", "");
					request.setAttribute("validade_hora", "");
					request.setAttribute("set_inconsistencia", "");
					request.setAttribute("boolean", "");
					
					if (sTotalInfracoes != null) {
						new Mensagem(response).showSimNao("Filtro cadastrado com sucesso, deseja reposicionar as infrações em processo?", "/processo/IniciarReposicionamento?id_filtro="+idFiltro,"/ferramenta/cadastro_filtros.jsp");
					}
					else {
						new Mensagem(response).showSucesso("Filtro cadastrado com sucesso.");
					}
				}


			} catch (ConexaoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
