/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 29/04/2007

  Descricao: Servlet para envio de informações sobre o cadastro de um veículo.

  Historico:

    $Log: InfoCadastro.java,v $
    Revision 1.10  2009/05/19 11:34:15  fos
    Ajustada a máscara da regexp de placa veículares no Brasil.

    Revision 1.9  2009/05/08 18:58:40  fos
    Agora busca a espécie selecionada durante o processamento também.

    Revision 1.8  2009/04/03 15:52:30  fos
    Unificada a busca de marca, marca_processo e modelo.

    Revision 1.7  2009/01/12 12:49:46  fos
    Recuperação de repositório.

    Revision 1.5  2008/08/21 21:11:26  fos
    Imprime o stack trace para tentar encontrar um erro.

    Revision 1.4  2008/08/20 13:41:29  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.3  2008/07/23 14:30:06  fos
    Agora mostra a placa recusada na validação.

    Revision 1.2  2008/05/09 21:16:57  fos
    Consertada mascara de validação da placa...agora o range vai até NLU****

    Revision 1.1  2008/05/08 21:32:07  fos
    Alterações dos nomes das classes.


 *********************************************************************************/
package com.consilux.servlet.ajax;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.ExpValida;
import com.consilux.model.Acesso;
import com.consilux.model.Cadastro;
import com.consilux.model.CadastroBD;
import com.consilux.model.InfracaoCompleta;
import com.consilux.model.Isento;
import com.consilux.model.Proprietario;

/**
 * Servlet para envio de informações sobre o cadastro de um veículo.
 * 
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.10 $ $Date: 2009/05/19 11:34:15 $ $Author: fos $
 */
public class InfoCadastro extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(InfoCadastro.class);


	/**
	 * Constrói o objeto
	 */
	public InfoCadastro() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Acesso acesso = new Acesso(request, response, Boolean.valueOf(false));
		
//		if (!acesso.verificaAcesso(false))
//			throw new ServletException("Usuário não atenticado!");
    
		SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
    
	    String sPlaca = request.getParameter("placa");
	    Date inicio = new Date();
	    String nao_disponivel = "";
	    
	    boolean cav = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("habilitar_funcionalidade_cav").equals("1");
	    
	    String sIdInfracao = request.getParameter("id_infracao");
	    Integer idInfracao = Integer.valueOf(sIdInfracao != null ? Integer.parseInt(request.getParameter("id_infracao")) : 0);
	    
		try {
			
			if (sPlaca != null && !ExpValida.PLACA.validar(sPlaca) && !ExpValida.PLACA_MERCOSUL.validar(sPlaca)) {
				throw new ServletException("Placa enviada inválida!: " + sPlaca);
			}
		  
			Cadastro cadastro = null;
			try {
				cadastro = CadastroBD.buscaCadastroPorPlaca(sPlaca);
			} catch (Exception err) {
				logger.error("Erro ao buscar o cadastro [" + sPlaca + "].", err);
				throw new ServletException("Erro ao buscar a infracao: " + err.getMessage());
			}
		  
			Proprietario proprietario = null;
			
			if (!cav) {
				try {
					proprietario = Proprietario.ObterProprietario(sPlaca);
				} catch (Exception err) {
					logger.error("Erro ao buscar o cadastro do Proprietario [" + sPlaca + "].", err);
					throw new ServletException("Erro ao buscar a infracao (proprietario): " + err.getMessage());
				}
			}
	      
			Isento isento = null;
			try {
				if (idInfracao > 0) {
					InfracaoCompleta infracao = InfracaoCompleta.buscaInfracaoPorId(idInfracao);
	          
					Map<String, Object> mFiltro = new HashMap<String, Object>();
					mFiltro.put("data_validade", infracao.getDataVeiculo());
					mFiltro.put("area", infracao.getAreaPista());
	          
					List<Isento> isentos = Isento.buscaRapidaIsentoPor(sPlaca, infracao.getIdEnquadramento(), infracao.getDataVeiculo(), mFiltro);
	    		  
					if ((isentos != null) && (isentos.size() > 0))
						isento = (Isento)isentos.get(0);
				}
			} catch (Exception err) {
				logger.error("Erro ao buscar isento [" + sPlaca + "].", err);
				throw new ServletException("Erro ao buscar a infracao: " + err.getMessage());
			}
	      
//			if (cadastro == null) {
//				return;
//			}
	      
			try {
				AjaxXMLConstr xml = new AjaxXMLConstr("cadastro");
				
				if (cadastro != null) {
					xml.adicCampo("PLACA", cadastro.getPlaca());
					xml.adicCampo("MARCA", cadastro.getMarca());
					xml.adicCampo("ID_MARCA_DISPONIVEL", String.valueOf(cadastro.getIdMarcaDisponivel()));
					xml.adicCampo("ID_ESPECIE_DISPONIVEL", String.valueOf(cadastro.getIdEspecieDisponivel()));
					xml.adicCampo("UF_DISPONIVEL", String.valueOf(cadastro.getUfDisponivel()));
					xml.adicCampo("COR", cadastro.getCor());
					xml.adicCampo("ANO", cadastro.getAno() != null ? String.valueOf(cadastro.getAno()) : "");
					xml.adicCampo("ESPECIE", cadastro.getEspecie());
					xml.adicCampo("UF", cadastro.getUf());
					xml.adicCampo("TIPO", cadastro.getTipo());
					if (!cav) {
						xml.adicCampo("CLASSIFICACAO_CAD", cadastro.getClassificacaoCad());
					}
					xml.adicCampo("CATEGORIA", cadastro.getCategoria());
					xml.adicCampo("SITUACAO", cadastro.getSituacao());
					xml.adicCampo("LOCALIDADE", cadastro.getLocalidade());
					xml.adicCampo("ATUALIZACAO", cadastro.getDataAtualizacao() != null ? new SimpleDateFormat("dd/MM/yyyy").format(cadastro.getDataAtualizacao()) : "");
				}
				
				if (isento != null) {
					xml.adicCampo("ISENCAO", "SIM");
					xml.adicCampo("ISENCAO_PERIODO", String.format("%s - %s", new Object[] {dateformat.format(isento.getDataInicio()), dateformat.format(isento.getDataFim()) }));
				} else {
					xml.adicCampo("ISENCAO", "NÃO");
					xml.adicCampo("ISENCAO_PERIODO", "NÃO HÁ");
				}
				
				if (proprietario != null) {
					xml.adicCampo("PROPRIETARIO_MARCA", proprietario.getMarca());
					xml.adicCampo("PROPRIETARIO_TIPO", proprietario.getTipo());
					xml.adicCampo("PROPRIETARIO", proprietario.getProprietario());
					xml.adicCampo("PROPRIETARIO_OBSERVACAO", proprietario.getObservacao());
					xml.adicCampo("PROPRIETARIO_DATA", dateformat.format(proprietario.getData_atualizado()));
				} else {
					xml.adicCampo("PROPRIETARIO_MARCA", 				nao_disponivel);
					xml.adicCampo("PROPRIETARIO_TIPO", 					nao_disponivel);
					xml.adicCampo("PROPRIETARIO", 						nao_disponivel);
					xml.adicCampo("PROPRIETARIO_OBSERVACAO",			nao_disponivel);
					xml.adicCampo("PROPRIETARIO_DATA",					nao_disponivel);
				}
	        
				xml.dump(response);
	      
			} catch (Exception err) {
				logger.error("Erro ao montar XML de InfoCadastro.", err);
				throw new ServletException("Erro ao montar o XML: " + err.getMessage());
			}
			
			return;
		
		} catch (ServletException se) {
			try {
				
				AjaxXMLConstr xml = new AjaxXMLConstr("infracao");
				xml.adicCampo("ERRO", se.getMessage());
				xml.dump(response);
	      
			} catch (ParserConfigurationException err) {
				logger.error("Erro ao entregar informação de erro.", err);
			}
	      
			logger.info("[TEMPO] Cadastro: " + ( new Date().getTime() - inicio.getTime()) + "...[" + sPlaca + "]...[" + acesso.getUsuario().getUsuario() + "]");
	    }
	}
}
