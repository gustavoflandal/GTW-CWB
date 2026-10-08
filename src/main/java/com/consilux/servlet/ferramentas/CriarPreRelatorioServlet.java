package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * Servlet implementation class CriarPreRelatorioServlet
 */
public class CriarPreRelatorioServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CriarPreRelatorioServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(request, response);
		doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doPost(request, response);
		doProcess(request, response);
	}

	protected void doProcess(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {

		String action  = request.getParameter("tarefa");


		String dataini  = request.getParameter("dataini");
		String horaini  = request.getParameter("horaini");
		String datafim  = request.getParameter("datafim");
		String horafim  = request.getParameter("horafim");

		Date dtIni = null;
		Date dtFim = null;
		Date hrIni = null;
		Date hrFim = null;
		
		int resultado = 0;
		try {
			dtIni = new SimpleDateFormat("dd/MM/yyyy").parse(dataini);
			dtFim = new SimpleDateFormat("dd/MM/yyyy").parse(datafim);
			hrIni = new SimpleDateFormat("HH:mm").parse(horaini);
			hrFim = new SimpleDateFormat("HH:mm").parse(horafim);

			
			Filtros filtros = new Filtros(dtIni, dtFim, hrIni, hrFim);

			switch(action.charAt(0)){
			case 'b':
				dtIni = new SimpleDateFormat("yyyy/MM/dd").parse(dataini);
				dtFim = new SimpleDateFormat("HH:MM").parse(horaini);
				resultado = executar(filtros);
				break;
			case 'c':
				break;
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConexaoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		String msg = new String();
		if(resultado>0){
			msg = "Pré-relatório gerado com sucesso, ID: " + resultado;
		}else{
			msg = "Erro ao gerar o pré-relatório.";
		}
		request.setAttribute("mensagem", msg);
		/*
		RequestDispatcher rd = request.getRequestDispatcher("/ferramenta/executar_pre-relatorio.jsp");
		rd.forward(request, response);*/

	}


	public int executar(Filtros filtros) throws ConexaoException{


		//		sbSQL.append("EXECUTE @RC = [Consilux_GTW].[dbo].[spu_cria_Relatorios_Diario_Volume_Hora_PMG] ");
		//		sbSQL.append("@Data = ? ");
		//		sbSQL.append(",@HoraInicio = ? ");
		//		sbSQL.append(",@HoraFinal = ? ");
		//		sbSQL.append(",@Usuario = ? ");
		//		sbSQL.append("GO ");

		Connection conn = null;
		CallableStatement cs = null;
		int id_gerado = 0;
		try {
			conn = Conexao.getConexao();
			cs = conn.prepareCall(
			"{? = call spu_cria_Relatorios_Diario_Volume_Hora_PMG(?, ?, ?, ?)}");
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setTimestamp(2, new Timestamp(filtros.getDataini().getTime()));
			cs.setTimestamp(3, new Timestamp(filtros.getHoraini().getTime()));
			cs.setTimestamp(4, new Timestamp(filtros.getHorafim().getTime()));
			cs.setString(5, "supervisor");

			//cs.setTimestamp(5, new Timestamp(dataFinal.getTime()));
			cs.execute();
			id_gerado = cs.getInt(1);
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally {
			try {
				if (cs != null)
					cs.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return id_gerado;
	}










	public class Filtros {

		public Filtros (Date dataini, Date datafim, Date horaini, Date horafim){
			this.dataini = dataini;
			this.datafim = datafim;
			this.horaini = horaini;
			this.horafim = horafim;
		}

		private Date dataini;
		public Date getDataini() {
			return dataini;
		}
		public void setDataini(Date dataini) {
			this.dataini = dataini;
		}
		public Date getHoraini() {
			return horaini;
		}
		public void setHoraini(Date horaini) {
			this.horaini = horaini;
		}
		public Date getDatafim() {
			return datafim;
		}
		public void setDatafim(Date datafim) {
			this.datafim = datafim;
		}
		public Date getHorafim() {
			return horafim;
		}
		public void setHorafim(Date horafim) {
			this.horafim = horafim;
		}
		private Date horaini;
		private Date datafim;
		private Date horafim;
	}

}
