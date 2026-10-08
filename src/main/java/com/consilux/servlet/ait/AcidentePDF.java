/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 09/01/2009

  Descricao: Servlet para visualização de notificações geradas externamente.

  Historico:

    $Log: AcidentePDF.java,v $
    Revision 1.1.2.6  2009/06/21 15:38:03  charles.maske
    *** empty log message ***

    Revision 1.1.2.5  2009/06/16 21:38:00  charles.maske
    Versao final

    Revision 1.1.2.4  2009/06/16 19:35:52  charles.maske
    Cadastro e controle de Palms OK

    Revision 1.1.2.3  2009/06/15 21:01:52  charles.maske
    Relatorio pdf 

    Revision 1.1.2.2  2009/06/15 18:14:29  charles.maske
    Relatorio de acidentes

    Revision 1.1.2.1  2009/06/10 20:44:02  fos
    Migrado alterações do HEAD para o branch.

    Revision 1.2  2009/06/01 18:41:23  fos
    Consertado expressão regular de inteiros.

    Revision 1.1  2009/03/18 17:20:42  fos
    Primeira versão postada no CVS.

    Revision 1.3  2009/01/16 18:44:32  fos
    Agora envio o diretório das imagens ao Jasper.

    Revision 1.2  2009/01/16 14:01:23  fos
    Agora utiliza biblioteca indireta para o JasperReports.

    Revision 1.1  2009/01/12 12:49:45  fos
    Recuperação de repositório.


*********************************************************************************/
package com.consilux.servlet.ait;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
 
 /**
 * Servlet para visualização de notificações geradas externamente.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.1.2.6 $ $Date: 2009/06/21 15:38:03 $ $Author: charles.maske $
 */
public class AcidentePDF extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String ARQ_ACIDENTE = "/WEB-INF/relatorio/Acidentes.jasper";
	
	/**
	 * Constrói o objeto 
	 */
	public AcidentePDF() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if (!new Acesso(request, response, true).verificaAcesso())
			return; //O usuário não tem acesso...então cai fora!

		//String sIdLogradouro = request.getParameter("idLogradouro");
		String sEndereco = request.getParameter("endereco");
		String sDataIni = request.getParameter("dataini");
		String sHoraIni = request.getParameter("horaini");
		String sDataFim = request.getParameter("datafim");
		String sHoraFim = request.getParameter("horafim");
		String sTipoGravidade = request.getParameter("tipoGravidade-hidden");
		String sTipoIluminacao = request.getParameter("tipoIluminacao-hidden");
		String sTipoArea = request.getParameter("tipoArea-hidden");
		String sTipoEquipamentoSeguranca = request.getParameter("tipoEquipamentoSeguranca-hidden");
		String sSexo = request.getParameter("sexo-hidden");
		String sFaixaEtaria = request.getParameter("faixaEtaria-hidden");
		String sTipoVeiculo = request.getParameter("tipoVeiculo-hidden");
		String sTipoAcidente = request.getParameter("tipoAcidente-hidden");
		String sEnvolvidos = request.getParameter("envolvidos-hidden");
		String sSituacaoCondutor = request.getParameter("situacaoCondutor-hidden");
		String sEstadoVeiculo = request.getParameter("estadoVeiculo-hidden");
		String sEstadoPneus = request.getParameter("estadoPneus-hidden");
		String sDanosCausados = request.getParameter("danosCausados-hidden");
		String sOrigemVeiculo = request.getParameter("origemVeiculo-hidden");
		String sSinalizacaoSemaforica = request.getParameter("sinalizacaoSemaforica-hidden");
		String sTipoPista = request.getParameter("tipoPista-hidden");
		String sOutrasSinalizacoes = request.getParameter("outrasSinalizacoes-hidden");
		String sCondicoesPista = request.getParameter("condicoesPista-hidden");
		String sCondicoesTempo = request.getParameter("condicoesTempo-hidden");
		String sCaracteristicasVia = request.getParameter("caracteristicasVia-hidden");
		
		
		if (sDataIni == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataIni)) {
		    new Mensagem(response).showErro("Data inicial enviada inválida!");
		    return;
		}
		else if (sHoraIni == null || !Pattern.matches("([01][0-9]|2[0-3]):([0-5][0-9])",sHoraIni)) {
		    new Mensagem(response).showErro("Hora inicial enviada inválida!");
		    return;
		}
		else if (sDataFim == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataFim)) {
			new Mensagem(response).showErro("Data final enviada inválida!");
		    return;
		}
		else if (sHoraFim == null || !Pattern.matches("([0-1][0-9]|2[0-3]):([0-5][0-9])",sHoraFim)) {
			new Mensagem(response).showErro("Hora final enviada inválida!");
		    return;
		}
		else if (sTipoGravidade == null || !Pattern.matches("[0-9]{0,9}",sTipoGravidade)) {
			new Mensagem(response).showErro("Identificador do Tipo de Gravidade enviado invalido!","javascript:window.close();");
			return;
		}
		else if (sTipoIluminacao == null || !Pattern.matches("[0-9]{0,9}",sTipoIluminacao)) {
			new Mensagem(response).showErro("Identificador do Tipo de Iluminação enviado invalido!","javascript:window.close();");
			return;
		}
		else if (sTipoArea == null || !Pattern.matches("[0-9]{0,9}",sTipoArea)) {
			new Mensagem(response).showErro("Identificador do Tipo de Área enviado invalido!","javascript:window.close();");
			return;
		}
		else if (sTipoEquipamentoSeguranca == null || !Pattern.matches("[0-9]{0,9}",sTipoEquipamentoSeguranca)) {
			new Mensagem(response).showErro("Identificador do Tipo de Equipamento Segurança enviado invalido!","javascript:window.close();");
			return;
		}
		else if (sSexo == null || !Pattern.matches("[0-9]{0,9}",sSexo)) {
			new Mensagem(response).showErro("Identificador do Sexo enviado invalido!","javascript:window.close();");
			return;
		}
		else if (sFaixaEtaria == null || !Pattern.matches("[0-9]{0,9}",sFaixaEtaria)) {
			new Mensagem(response).showErro("Identificador de Feixa Etária enviado invalido!","javascript:window.close();");
			return;
		}
		else if (sTipoVeiculo == null || !Pattern.matches("[0-9]{0,9}",sTipoVeiculo)) {
			new Mensagem(response).showErro("Identificador do Tipo de Veículo enviado invalido!","javascript:window.close();");
			return;
		}
		else if (sTipoAcidente == null || !Pattern.matches("[0-9]{0,9}",sTipoAcidente)) {
			new Mensagem(response).showErro("Identificador do Tipo de Acidente enviado invalido!","javascript:window.close();");
			return;
		}
		else if (sEnvolvidos == null || !Pattern.matches("[0-9]{0,9}",sEnvolvidos)) {
			new Mensagem(response).showErro("Identificador de Envolvidos enviado invalido!","javascript:window.close();");
			return;
		}
		else if (sSituacaoCondutor == null || !Pattern.matches("[0-9]{0,9}",sSituacaoCondutor)) {
			new Mensagem(response).showErro("Identificador de Situação de Condutor enviado invalido!","javascript:window.close();");
			return;
		}else if (sEstadoVeiculo == null || !Pattern.matches("[0-9]{0,9}",sEstadoVeiculo)) {
			new Mensagem(response).showErro("Identificador de Estado do Veículo enviado invalido!","javascript:window.close();");
			return;
		}else if (sEstadoPneus == null || !Pattern.matches("[0-9]{0,9}",sEstadoPneus)) {
			new Mensagem(response).showErro("Identificador de Estado dos Pneus enviado invalido!","javascript:window.close();");
			return;
		}else if (sDanosCausados == null || !Pattern.matches("[0-9]{0,9}",sDanosCausados)) {
			new Mensagem(response).showErro("Identificador de Danos Causados enviado invalido!","javascript:window.close();");
			return;
		}else if (sOrigemVeiculo == null || !Pattern.matches("[0-9]{0,9}",sOrigemVeiculo)) {
			new Mensagem(response).showErro("Identificador de Origem Veiculo enviado invalido!","javascript:window.close();");
			return;
		}else if (sSinalizacaoSemaforica == null || !Pattern.matches("[0-9]{0,9}",sSinalizacaoSemaforica)) {
			new Mensagem(response).showErro("Identificador de Sinalizacao Semaforica enviado invalido!","javascript:window.close();");
			return;
		}else if (sTipoPista == null || !Pattern.matches("[0-9]{0,9}",sTipoPista)) {
			new Mensagem(response).showErro("Identificador de Tipo de Pista enviado invalido!","javascript:window.close();");
			return;
		}else if (sOutrasSinalizacoes == null || !Pattern.matches("[0-9]{0,9}",sOutrasSinalizacoes)) {
			new Mensagem(response).showErro("Identificador de Outras Sinalizacoes enviado invalido!","javascript:window.close();");
			return;
		}else if (sCondicoesPista == null || !Pattern.matches("[0-9]{0,9}",sCondicoesPista)) {
			new Mensagem(response).showErro("Identificador de Condicoes da Pista enviado invalido!","javascript:window.close();");
			return;
		}else if (sCondicoesTempo == null || !Pattern.matches("[0-9]{0,9}",sCondicoesTempo)) {
			new Mensagem(response).showErro("Identificador de Condicoes do Tempo enviado invalido!","javascript:window.close();");
			return;
		}else if (sCaracteristicasVia == null || !Pattern.matches("[0-9]{0,9}",sCaracteristicasVia)) {
			new Mensagem(response).showErro("Identificador de Caracteristicas da Via enviado invalido!","javascript:window.close();");
			return;
		}
		
		Date dtIni = null;
		Date dtFim = null;
		try {
			dtIni = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataIni+" "+sHoraIni+":00");
			dtFim = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(sDataFim+" "+sHoraFim+":59");
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(ARQ_ACIDENTE));
		
		relatorio.adicParametro("ENDERECO", sEndereco != null ? "%"+sEndereco+"%" : "%");
        relatorio.adicParametro("DATA_INI", new Timestamp(dtIni.getTime()));
        relatorio.adicParametro("DATA_FIM", new Timestamp(dtFim.getTime()));
        relatorio.adicParametro("TIPO_GRAVIDADE", Integer.parseInt(sTipoGravidade));
        relatorio.adicParametro("TIPO_ILUMINACAO", Integer.parseInt(sTipoIluminacao));
        relatorio.adicParametro("TIPO_AREA", Integer.parseInt(sTipoArea));
        relatorio.adicParametro("TIPO_EQUIPAMENTO_SEGURANCA", Integer.parseInt(sTipoEquipamentoSeguranca));
        relatorio.adicParametro("SEXO", Integer.parseInt(sSexo));
        relatorio.adicParametro("FAIXA_ETARIA", Integer.parseInt(sFaixaEtaria));
        relatorio.adicParametro("TIPO_VEICULO", Integer.parseInt(sTipoVeiculo));
        relatorio.adicParametro("TIPO_ACIDENTE", Integer.parseInt(sTipoAcidente));
        relatorio.adicParametro("ENVOLVIDOS", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("SITUACAO_CONDUTOR", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("ESTADO_VEICULO", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("ESTADO_PNEUS", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("DANOS_CAUSADOS", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("ORIGEM_VEICULO", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("SINALIZACAO_SEMAFORICA", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("TIPO_PISTA", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("OUTRAS_SINALIZACOES", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("CONDICOES_PISTA", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("CONDICOES_TEMPO", Integer.parseInt(sEnvolvidos));
        relatorio.adicParametro("CARACTERISTICAS_VIA", Integer.parseInt(sEnvolvidos));
        
		
		try {
			relatorio.preencheRelatorio();
			response.setContentType("application/save");
			response.setHeader("Content-Disposition","attachment; filename=\"Acidentes.pdf\"");
		    relatorio.exportReportToPdfStream(response.getOutputStream());
		}
		catch (Exception e) {
			e.printStackTrace();
			new ServletException("Erro ao gerar o relatório: "+e.getMessage());
		}
	}
}