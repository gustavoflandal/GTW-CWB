package com.consilux.servlet.relatorio;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.ExpValida;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;

/**
 * Servlet implementation class ListarVeiculosQualquer
 */
public class ListarVeiculosQualquer extends HttpServlet {

	private enum TipoConsulta {
		
		TODOS,
		MONITORADOS
		
	}
	
	private static final long serialVersionUID = 1L;
	private static final long PERIODO_MAXIMO_CONSULTA = 6 * 60 * 60 * 1000; // 6 horas
	
	public static final String DIR_RELATORIOS = "/WEB-INF/relatorio/";
	public static final String NOME_RELATORIOS_PDF = DIR_RELATORIOS + "lista de veiculos PDF.jasper";
	public static final String NOME_RELATORIOS_XLS = DIR_RELATORIOS + "lista de veiculos Excel.jasper";

	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ListarVeiculosQualquer() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {

		if (!new Acesso(request, response, true).verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!
    	
    	String sDataVeiculoIni = request.getParameter("data_veiculo_ini") != null ? request.getParameter("data_veiculo_ini").trim() : null;
    	sDataVeiculoIni = sDataVeiculoIni != null && sDataVeiculoIni.length() == 0 ? null : sDataVeiculoIni;

    	String sHoraVeiculoIni = request.getParameter("hora_veiculo_ini") != null ? request.getParameter("hora_veiculo_ini").trim() : null;
    	sHoraVeiculoIni = sHoraVeiculoIni != null && sHoraVeiculoIni.length() == 0 ? null : sHoraVeiculoIni;
    	
    	String sDataVeiculoFim = request.getParameter("data_veiculo_fim") != null ? request.getParameter("data_veiculo_fim").trim() : null;
    	sDataVeiculoFim = sDataVeiculoFim != null && sDataVeiculoFim.length() == 0 ? null : sDataVeiculoFim;
    	
    	String sHoraVeiculoFim = request.getParameter("hora_veiculo_fim") != null ? request.getParameter("hora_veiculo_fim").trim() : null;
    	sHoraVeiculoFim = sHoraVeiculoFim != null && sHoraVeiculoFim.length() == 0 ? null : sHoraVeiculoFim;
    	
        String sLocal = request.getParameter("local");
        String sPista = request.getParameter("pista");
        String sTipoConsulta = request.getParameter("tipo_consulta");
        String sFormato = request.getParameter("formato");
    	String sPlaca = request.getParameter("placa") != null ? request.getParameter("placa").trim() : null;
    	
    	sPlaca = sPlaca != null && sPlaca.length() == 0 ? null : sPlaca.toUpperCase();
    	
    	TipoMime formato = null; 
    	
        if (sDataVeiculoIni != null && !ExpValida.DATA.validar(sDataVeiculoIni)) {
            new Mensagem(response).showErro("Data inicial do veículo enviada inválida!", "javascript:window.close();");
            return;
        }
        if (sDataVeiculoFim != null && !ExpValida.DATA.validar(sDataVeiculoFim)) {
            new Mensagem(response).showErro("Data inicial do veículo enviada inválida!" ,"javascript:window.close();");
            return;
        }
        if (sHoraVeiculoIni != null && !ExpValida.HORA_MINUTO.validar(sHoraVeiculoIni)) {
            new Mensagem(response).showErro("Hora inicial do veículo enviada inválida!","javascript:window.close();");
            return;
        }
        if (sHoraVeiculoFim != null && !ExpValida.HORA_MINUTO.validar(sHoraVeiculoFim)) {
            new Mensagem(response).showErro("Hora final do veículo enviada inválida!","javascript:window.close();");
            return;
        }
        if ((sDataVeiculoIni != null || sHoraVeiculoIni != null || sDataVeiculoFim != null || sHoraVeiculoFim != null) &&
        	(sDataVeiculoIni == null || sHoraVeiculoIni == null || sDataVeiculoFim == null || sHoraVeiculoFim == null)) {
            new Mensagem(response).showErro("Período de infração incompleto!","javascript:window.close();");
            return;
        }
        if (sLocal == null || !ExpValida.NATURAL_COM_ZERO.validar(sLocal)) {
            new Mensagem(response).showErro("Local selecionado inválido!","javascript:window.close();");
            return;
        }
        if (sPista == null || !ExpValida.NATURAL_COM_ZERO.validar(sPista)) {
            new Mensagem(response).showErro("Pista selecionada inválida!","javascript:window.close();");
            return;
        }
        if (sPlaca != null && !ExpValida.PLACA.validar(sPlaca) && !ExpValida.PLACA_MERCOSUL.validar(sPlaca)) {
            new Mensagem(response).showErro("Placa enviada inválida!","javascript:window.close();");
            return;
        }
        
        if ( sFormato == null || !Pattern.matches("(pdf)|(xls)", sFormato ) ){
            new Mensagem(response).showErro("Formato enviado inválido!","javascript:window.close();");
            return;
        }

        if ( sTipoConsulta == null || !Pattern.matches("(all)|(mon)", sTipoConsulta ) ){
            new Mensagem(response).showErro("Tipo de Consulta enviado inválido!","javascript:window.close();");
            return;
        }

		TipoConsulta consulta = TipoConsulta.TODOS;
		if ("all".compareToIgnoreCase(sTipoConsulta) != 0){
			consulta = TipoConsulta.MONITORADOS;
		}
	
        
        if (sDataVeiculoIni == null && sPlaca == null) {
            new Mensagem(response).showErro("Pesquisa muito abregente, selecione um filtro!","javascript:window.close();");
            return;
        }
     
        Integer iLocal = Integer.valueOf( sLocal );        

		Timestamp dtIni = null;
		Timestamp dtFim = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
		
		try {
			
			dtIni = new Timestamp( dateFormat.parse(sDataVeiculoIni+" "+sHoraVeiculoIni+":00").getTime() );
			dtFim = new Timestamp( dateFormat.parse(sDataVeiculoFim+" "+sHoraVeiculoFim+":59").getTime() );

		} catch (ParseException e) {
			e.printStackTrace();
			new ServletException("Erro ao converter data: "+e.getMessage());
		}

		boolean periodoMaior6h = dtFim.getTime() - dtIni.getTime() > PERIODO_MAXIMO_CONSULTA; 
		boolean placaVazia = sPlaca == null;
		boolean todosVeiculos = TipoConsulta.TODOS.compareTo( consulta ) == 0;
		
        if ( iLocal.equals(0) && sPlaca == null && todosVeiculos ){
            new Mensagem(response).showErro("Pesquisa muito abregente! A placa e/ou o local devem ser selecionados.","javascript:window.close();");
            return;
        }

        // se não contém placa, a consulta deve ser de no máximo 6 horas com local selecionado
		if ( periodoMaior6h && placaVazia && todosVeiculos ){
            new Mensagem(response).showErro("Pesquisa muito abregente! Periodo máximo de 6 horas.");
            return;
		}
		
        // validação garante que será um dos dois
        String sArquivoJasper = ""; 
        if ( "pdf".compareToIgnoreCase( sFormato ) == 0 ){
        	formato = TipoMime.PDF;
        	sArquivoJasper = NOME_RELATORIOS_PDF;
        }
        else if ( "xls".compareToIgnoreCase(sFormato) == 0 ){
        	formato = TipoMime.XLS;
        	sArquivoJasper = NOME_RELATORIOS_XLS;
        }
        	
        String identificacaoCliente = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("identificacao_cliente");
        
		RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(sArquivoJasper));
		
		try {
		
			relatorio.adicParametro("placa", sPlaca == null ? "" : sPlaca );
			relatorio.adicParametro("is_irregular", TipoConsulta.TODOS.compareTo(consulta) == 0 ? false : true );
			relatorio.adicParametro("data_inicio", dtIni );
			relatorio.adicParametro("data_fim",  dtFim);
			relatorio.adicParametro("id_local", iLocal );
			relatorio.adicParametro("pista", Integer.valueOf(sPista) );
			relatorio.adicParametro("identificacaoCliente", identificacaoCliente );

			relatorio.preencheRelatorio();
			response.setContentType(formato != null ? formato.getTipo() : TipoMime.HTML.getTipo() );
			response.setHeader("Content-Disposition","inline; filename=\"lista de veículos." + sFormato + "\"");
			
			if ( formato.compareTo( TipoMime.PDF ) == 0 ){
				relatorio.exportReportToPdfStream(response.getOutputStream());
			}else{
				relatorio.exportReportToXlsStream(response.getOutputStream());
			}
			
		} 
		catch (Exception e) {
			e.printStackTrace();
			new ServletException("Erro ao gerar o relatório: "+e.getMessage());
		}
    	
    }
    

}
