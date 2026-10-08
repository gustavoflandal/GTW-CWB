package com.consilux.servlet.processamento;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.model.Acesso;
import com.consilux.model.InfracoesSemEscala;
import com.consilux.model.Mensagem;

/**
 * Servlet implementation class InconsistirImagensForaEscala
 */
public class InconsistirImagensForaEscala extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InconsistirImagensForaEscala() {
        super();
        // TODO Auto-generated constructor stub
    }

    class DataLocalInfo {
    	private Date data = null;
    	private Integer local = null;
    	private Integer numero_linhas_afetadas = 0;
    	
    	private DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    	private DateFormat df_out = new SimpleDateFormat("dd/MM/yyyy");
    	
    	private Exception exception = null;
    	
		public String getDataStr() {
			return df_out.format(data);
		}
		public Date getData() {
			return data;
		}
		public DataLocalInfo setData(String data) {
			try {
				this.data = df.parse(data);
			} catch (ParseException e) {
				this.data = null;
				this.exception = e;
			}
			return this;
		}
		public Integer getLocal() {
			return local;
		}
		public DataLocalInfo setLocal(String local) {
			try {
				this.local = Integer.parseInt(local);
			}catch (NumberFormatException e) {
				this.local = null;
				this.exception = e;
			}
			
			return this;
		}
		public Integer getNumeroLinhasAfetadas() {
			return numero_linhas_afetadas;
		}
		public DataLocalInfo setNumeroLinhasAfetadas(Integer numero_linhas_afetadas) {
			this.numero_linhas_afetadas = numero_linhas_afetadas;
			
			return this;
		}
		public Exception getException() {
			return exception;
		}

		public boolean Inicializado() {
			return (data != null) && (local != null);
		}
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Acesso acesso = new Acesso(request, response, true);

		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!");
			return; // O usuário não tem acesso...então cai fora!
		}
		
		List<DataLocalInfo> data_local = new ArrayList<DataLocalInfo>();
		
		String[] data_local_str_array = request.getParameterValues("sel_imagens");
		if (data_local_str_array != null && data_local_str_array.length > 0)
		for(String data_local_str : data_local_str_array) {
			String[] partes = data_local_str.split("_");
			if(partes.length == 2)
			data_local.add(new DataLocalInfo().setData(partes[0]).setLocal(partes[1]));
		}
			
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		response.getWriter().write("<link rel=\"stylesheet\" href=\"/css/gtw.css\" media=\"screen\" type=\"text/css\">");
		response.getWriter().write("<div class=\"valor_campo\" style=\"text-align: center;\" >");
		
		response.getWriter().write("<h3>Reposicionando Infrações</h3><BR/>");
		
		response.getWriter().write("<div class=\"valor_campo\" style=\"text-align: left; width: 600; margin: auto;\" >");
		
		if(data_local == null || data_local.size() == 0) {
			response.getWriter().write("Nenhum data/local foi Selecionado!<BR/>");
		}else { 
			for(DataLocalInfo dli : data_local) {
			
			int registros = InfracoesSemEscala.ReposicionarInfracoesSemEscala(dli.getData(), dli.getLocal());
			dli.setNumeroLinhasAfetadas(registros);
			
			response.getWriter().write("Reposicionando [" + dli.getNumeroLinhasAfetadas() + "] infrações do dia [" + dli.getDataStr() + "] do Local [" + dli.getLocal() + "] <BR/>");
			
			response.getWriter().write("<BR/>");
			}
		}
		
		response.getWriter().write("</div>");
					
		response.getWriter().write("<BR/>");
		response.getWriter().write("<button onclick=\"window.close()\">Retornar a tela de Seleção</button>");
		response.getWriter().write("</div>");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
