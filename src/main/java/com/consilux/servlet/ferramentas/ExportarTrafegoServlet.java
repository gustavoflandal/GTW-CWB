package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.quartz.JobExecutionException;

import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.ferramenta.ExportaTrafego;

/**
 * Servlet implementation class ExportarTrafego
 */
public class ExportarTrafegoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExportarTrafegoServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, true).verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!    	
    	
		// Recupera os parâmetros
		String sData = request.getParameter("data");
		
		if (sData == null || !ExpValida.DATA.validar(sData)) {
			new Mensagem(response).showErro("Data enviada inválida!");
			return;
		}

		Date dia = null;

		try {
			dia =  new SimpleDateFormat("dd/MM/yyyy").parse(sData);
		} 
		catch (ParseException pe) {
			new Mensagem(response).showErro("Data inicial/final em formato inválido!");
			return;
		}
		
		ExportaTrafego et = new ExportaTrafego(dia);
		try {
			et.execute(null);
			new Mensagem(response).showSucesso("Data exportada com sucesso!");
		} 
		catch (JobExecutionException e) {
			new Mensagem(response).showErro("Não foi possível executar: "+e.getMessage());
			return;
		}
	}

}
