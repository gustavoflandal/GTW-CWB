package com.consilux.servlet.manutencao;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.Funcoes;
import com.consilux.model.Acesso;
import com.consilux.model.ManutencaoN;
import com.consilux.model.ManutencaoN.Manutencao;
import com.consilux.model.Mensagem;
import com.consilux.model.Usuario;

/**
 * Servlet implementation class CadastroManutencao
 */
public class CadastroManutencao extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static Logger logger = LogManager.getLogger(CadastroManutencao.class);
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CadastroManutencao() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		logger.info("doGet");
		
		if (!new Acesso(request, response, true).verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return; //O usuário não tem acesso...então cai fora!
		}
		
		Usuario usuario = null;
		try 
		{
			usuario = (new Acesso(request, response, true)).getUsuario();
		}
		catch(Exception e) {}
		
		boolean finalizada = request.getParameter("finalizada") != null && request.getParameter("finalizada").equals("1");
		boolean encaminhar = request.getParameter("encaminhar") != null && request.getParameter("encaminhar").equals("1");
		
		Integer idManutencao = null, idStatus = null, idLocal = null, idPista = null, serieEquipamento = null, idOcorrencia = null, numeroOficio = null, anoOficio = null, idUsuarioCadastro = null, idAuxiliar = null;
		String data_ocorrencia, hora_ocorrencia = "00:00:00", tipoGrupoAutuador, descricao, data_inicio, data_fim, comentario = null;
		List<Integer> causas_sel = new ArrayList<Integer>();
		try 
		{
		if (request.getParameter("id_manutencao") != null 		&& Pattern.matches("^[0-9]+$", request.getParameter("id_manutencao")))
			idManutencao = Integer.parseInt(request.getParameter("id_manutencao"));
		if (request.getParameter("estado") != null 				&& Pattern.matches("^[0-9]+$", request.getParameter("estado")))
			idStatus = Integer.parseInt(request.getParameter("estado"));
		if(request.getParameter("id_local") != null 			&& Pattern.matches("^[0-9]+$",request.getParameter("id_local")))
			idLocal = Integer.parseInt(request.getParameter("id_local"));
		if(request.getParameter("pista") != null 				&& Pattern.matches("^[0-9]+$",request.getParameter("pista")))
			idPista = Integer.parseInt(request.getParameter("pista"));
		if(request.getParameter("serie_equipamento") != null 	&& Pattern.matches("^[0-9]+$",request.getParameter("serie_equipamento")))
			serieEquipamento = Integer.parseInt(request.getParameter("serie_equipamento"));
		if(request.getParameter("ocorrencia") != null 			&& Pattern.matches("^[0-9]+$",request.getParameter("ocorrencia")))
			idOcorrencia = Integer.parseInt(request.getParameter("ocorrencia"));
		if(request.getParameter("num_oficio") != null 			&& Pattern.matches("^[0-9]+$",request.getParameter("num_oficio")))
			numeroOficio = Integer.parseInt(request.getParameter("num_oficio"));
		if(request.getParameter("ano_oficio") != null 			&& Pattern.matches("^[0-9]+$",request.getParameter("ano_oficio")))
			anoOficio = Integer.parseInt(request.getParameter("ano_oficio"));
		if(request.getParameter("tecnico_resp") != null			&& Pattern.matches("^[0-9]+$",request.getParameter("tecnico_resp")))
			idUsuarioCadastro = Integer.parseInt(request.getParameter("tecnico_resp"));
		if(request.getParameter("tecnico_aux") != null 			&& Pattern.matches("^[0-9]+$",request.getParameter("tecnico_aux")))
			idAuxiliar = Integer.parseInt(request.getParameter("tecnico_aux"));

		if (request.getParameterValues("causas") != null)
		{
			for(String causa_str : request.getParameterValues("causas"))
			{
				if (causa_str != null && !causa_str.equals(""))
					causas_sel.add(Integer.parseInt(causa_str));
			}
		}

		}
		catch (Exception e) { logger.error("Carregar Servlet", e); }

		data_ocorrencia = request.getParameter("data_ocorrencia");
		hora_ocorrencia = request.getParameter("hora_ocorrencia");
		tipoGrupoAutuador = request.getParameter("grupo_autuador");
		descricao = request.getParameter("descricao");
		data_inicio = request.getParameter("data_inicio");
		data_fim 	= request.getParameter("data_fim");
		comentario  = request.getParameter("comentario");
		
		if (hora_ocorrencia == null || hora_ocorrencia.equals(""))
			hora_ocorrencia = "00:00:00";
		
		logger.info("Recebeu todos Parametros!");
		
		/*
		 * 
		 *  r private int idStatus; 
			r private String descricao;
			r private int idLocal;
			r private Date dataOcorrencia;
			r private Date dataInicio;
			r private Date dataPrevisto;
			r private int idUsuarioCadastro;
		 */
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat sdhf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		Date dataOcorrencia = null;
		Date dataCadastro = null;
		Date dataInicio = null;
		Date dataPrevisto = null;
		Date dataConclusao = null;
		Date dataUltimaAlteracao = null;
		Integer idUltimoUsuario = null;
		
		try 
		{
			dataOcorrencia = sdhf.parse(data_ocorrencia + " " + hora_ocorrencia);
			dataCadastro = Calendar.getInstance().getTime();
			dataInicio = sdf.parse(data_inicio);
			dataPrevisto = sdf.parse(data_fim);
			dataConclusao = finalizada ? Calendar.getInstance().getTime() : null;
			dataUltimaAlteracao = Calendar.getInstance().getTime();
			idUltimoUsuario = usuario.getId();
		}
		catch(Exception e)
		{
			logger.error("Ao receber argumentos:", e);
		}
		
		if (descricao == null || descricao == "") {
			new Mensagem(response).showErro("Campo \"descricao\" deve ser preenchido!");
			return;
		}
		else if (idLocal == null || idLocal <= 0) {
			new Mensagem(response).showErro("Campo \"Local\" deve ser preenchido!");
			return;
		}
		else if (idLocal >= 2000 && idLocal <= 3000) {
			if (serieEquipamento == null || serieEquipamento == 0) {
				new Mensagem(response).showErro("Para equipamentos Estaticos, \"Serie (Estatico)\" deve ser preenchido!");
				return;
			}
		} 
		else if (dataOcorrencia == null) {
			new Mensagem(response).showErro("Campo \"Data da Falha\" inválido ou não preenchido!");
			return;
		}
		else if (dataInicio == null) {
			new Mensagem(response).showErro("Campo \"Data Inicio\" inválido ou não preenchido!");
			return;
		}
		else if (dataPrevisto == null) {
			new Mensagem(response).showErro("Campo \"Data Previsto\" inválido ou não preenchido!");
			return;
		}
		else if (idUsuarioCadastro == null || idUsuarioCadastro <= 0) {
			new Mensagem(response).showErro("Campo \"Tecnico\" inválido ou não preenchido!");
			return;
		}
		else if (causas_sel.size() == 0) {
			new Mensagem(response).showErro("Campo \"Causa(s)\" deve ter pelo menos 1 selecionado!");
			return;
		}
		
		logger.info("Validacao OK");
		
		try 
		{
			logger.info("Alterando manutencao: [" + descricao + "]");
			
			descricao = Funcoes.retirarAcentos(descricao);
			
			logger.info("Alterando manutencao: [" + descricao + "]");
			
			Manutencao man = new Manutencao(idManutencao, idStatus, descricao, idLocal, serieEquipamento, idPista, tipoGrupoAutuador, 
					dataOcorrencia, dataCadastro, dataInicio, dataPrevisto, idOcorrencia, numeroOficio, anoOficio, idUsuarioCadastro, 
					idAuxiliar, dataConclusao, dataUltimaAlteracao, idUltimoUsuario, encaminhar);
			
			Integer id_manutencao = 0;
			if (idManutencao == null || idManutencao == 0)
			{
				id_manutencao = ManutencaoN.InserirManutencao(man);
				logger.info("Adicionou nova Manutencao [" + id_manutencao + "]");
				new Mensagem(response).showSucesso("Adicionou nova Manutencao [" + id_manutencao + "]", "/manutencao/cadastro_manutencao.jsp");
			}
			else 
			{
				id_manutencao = idManutencao;
				ManutencaoN.AtualizarManutencao(man);
				logger.info("Alterando Manutencao [" + id_manutencao + "]");
				new Mensagem(response).showSucesso("Alterou Manutencao [" + id_manutencao + "]", "/manutencao/cadastro_manutencao.jsp");
			}
			
			if (comentario != null && !comentario.equals("")) {
				comentario = Funcoes.retirarAcentos(comentario);
				ManutencaoN.InserirComentario(id_manutencao, comentario, usuario.getId());
			}
			
			ManutencaoN.AtualizarCausas(id_manutencao, causas_sel);
			
		}
		catch(Exception e) 
		{
			logger.error("Erro ao cadastrar: ", e);
			new Mensagem(response).showErro("Erro ao cadastrar: " + e.getMessage());
			return;
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}

