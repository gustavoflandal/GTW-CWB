package com.consilux.servlet.medicao;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.medicao.ProcessoMedicao;
import com.consilux.model.medicao.ProcessoMedicao.EstagioProcesso;

/**
 * Servlet implementation class IniciaMedicao
 */
public class FixarAmostra extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(FixarAmostra.class); 
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FixarAmostra() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Se o usuário não tem acesso...então cai fora!		
		if (!new Acesso(request, response, true).verificaAcesso())
			return; 


		// Se o usuário não tem acesso...então cai fora!		
		if (!new Acesso(request, response, true).verificaAcesso())
			return; 

		String sDataIni = request.getParameter("dataini");
		String sDataFim = request.getParameter("datafim");
		String sOk = request.getParameter("ok");

		if (sDataIni == null || !ExpValida.DATA.validar(sDataIni)) {
			new Mensagem(response).showErro("Data inicial enviada inválida!");
			return;
		}

		else if (sDataFim == null || !ExpValida.DATA.validar(sDataFim)) {
			new Mensagem(response).showErro("Data final enviada inválida!");
			return;
		}
		else if (sOk != null && !sOk.equals("1")) {
			new Mensagem(response).showErro("Flag enviado inválido!");
			return;
		}

		Date dataInicio;
		Date dataFim;

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		try {
			dataInicio = dateFormat.parse(sDataIni + " 00:00:00");
			dataFim = dateFormat.parse(sDataFim + " 23:59:59");
		}
		catch (Exception e) {
			throw new ServletException(e);
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataInicio);

		Integer mes = cal.get(Calendar.MONTH);
		Integer ano = cal.get(Calendar.YEAR);

		ProcessoMedicao proc = null;
		try {
			proc = ProcessoMedicao.buscarProcessoMedicaoPorMesAno(mes+1, ano);
		} 
		catch (Exception err) {
			new Mensagem(response).showErro("Não foi possível verificar se o processo já foi iniciado: "+err.getMessage());
			logger.error("Não foi possível verificar se o processo já foi iniciado.", err);
			return;
		}

		if (sOk == null) {
			String sUrl = request.getRequestURI()+"?"+request.getQueryString()+"&ok=1";
			String descMesAno = DateFormatSymbols.getInstance().getMonths()[mes]+"/"+ano;

			if (proc == null)
				new Mensagem(response).showConfirma("Deseja realmente iniciar o processo de medição para o mês de '"+descMesAno+"' com as imagens do período de '"+sDataIni+"' até '"+sDataFim+"'?",sUrl);
			else if (proc.getEstagioProcesso() == EstagioProcesso.INICIO)
				new Mensagem(response).showConfirma("Deseja realmente adicionar na medição de '"+descMesAno+"' as imagens do período de '"+sDataIni+"' até '"+sDataFim+"'?",sUrl);
			else if (proc.getEstagioProcesso() == EstagioProcesso.RETORNO_IMAGENS_COMPROVACAO)
				new Mensagem(response).showConfirma("Deseja realmente adicionar na medição de '"+descMesAno+"' O COMPLEMENTO das imagens do período de '"+sDataIni+"' até '"+sDataFim+"'?",sUrl);
			else {
				new Mensagem(response).showErro("Não é possível fixar imagens na etapa '"+proc.getEstagioProcesso()+"' da medição.");
				logger.warn("Não é possível mais fixar imagens na etapa '"+proc.getEstagioProcesso()+"' da medição.");
				return;
			}

			return;
		}
		
		try {
			if (proc == null)
				proc = ProcessoMedicao.criarProcessoMedicao(mes+1, ano);
			
			if (proc.adicPeriodo(dataInicio, dataFim)) {
				new Mensagem(response).showSucesso("Processo atualizado com sucesso!<br>" +
						   "Clique <a href='#' onclick='window.open(\"/medicao/processo_medicao.jsp?id_processo_medicao=" +
						   proc.getId() + "\",\"Detalhes\",\"width=700, height=160\")'>&lt;aqui&gt;</a> " +
						   "para ver detalhes.");
			}
			else {
				new Mensagem(response).showErro("Não foi possível criar o processo!");
				logger.error("Processo não atualizado, erro desconhecido.");
			}
		}
		catch (Exception err) {
			new Mensagem(response).showErro("Não foi possível criar o processo: "+err.getMessage());
			logger.error("Não foi possível criar o processo.", err);
		}
	}
}
