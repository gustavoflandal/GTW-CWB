/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 03/04/2007

  Descricao: Servlet para envio de informações completas sobre infração.

  Historico:

    $Log: InfoInfracaoCompleta.java,v $
    Revision 1.14  2009/06/01 18:41:55  fos
    Consertado expressão regular de inteiros.

    Revision 1.13  2009/04/24 21:08:47  fos
    Colocado stack trace.

    Revision 1.12  2009/03/24 21:40:09  fos
    Agora a informação de obliteração é buscada em outro servlet.

    Revision 1.11  2009/01/28 19:29:02  fos
    Agora trás a inconsistência junto à infração.

    Revision 1.10  2009/01/12 12:49:46  fos
    Recuperação de repositório.

    Revision 1.8  2008/08/29 20:52:18  fos
    Renomeada métodos e atributos para enquadramento.

    Revision 1.7  2008/08/27 14:00:41  fos
    ASSIGNED - bug 17: Seleção/Visualização das múltiplas imagens da infração.
    http://bugzilla.consilux.net/show_bug.cgi?id=17

    Revision 1.6  2008/08/20 13:41:28  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.5  2008/08/12 13:04:18  fos
    Agora retorna informações sobre a obliteração.

    Revision 1.4  2008/07/31 21:11:43  fos
    Agora não pega as informações de cadastro junto com a infração e sim pelo servlet específico pra isso.

    Revision 1.3  2008/07/23 14:31:18  fos
    Agora envia junto com as informações da infração, a imagem selecionada.

    Revision 1.2  2008/05/14 21:46:14  fos
    Agora busca as informações específicas da infração.

    Revision 1.1  2008/05/08 21:32:07  fos
    Alterações dos nomes das classes.

    Revision 1.2  2008/02/28 18:43:49  fos
    Agora envia a inconsitencia junto com a infração para o cliente.

    Revision 1.1  2008/02/06 19:20:26  fos
    Carga da infração na nova tela funcional.

    Revision 1.1  2007/04/11 12:09:19  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.servlet.ajax;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.model.Cadastro;
import com.consilux.model.CadastroBD;
import com.consilux.model.CadastroProcesso;
import com.consilux.model.Enquadramento;
import com.consilux.model.InfoEspecificaInfracao;
import com.consilux.model.InfracaoCompleta;
import com.consilux.model.InfracaoProcesso;
import com.consilux.model.Isento;
import com.consilux.model.Usuario;
import com.consilux.model.Veiculo;
import com.consilux.model.VeiculoImagem;
import com.consilux.ui.client.beans.ImagemGwtBean;
import com.consilux.ui.client.beans.VeiculoImagensGwtBean;

 /**
 * Servlet para envio de informações completas sobre infração.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.14 $ $Date: 2009/06/01 18:41:55 $ $Author: fos $
 */
public class InfoInfracaoCompleta extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int INCONSISTENCIA_VALIDACAO_PADRAO = 50;
	private static Logger logger = Logger.getLogger(InfoInfracaoCompleta.class); 
	/**
	 * Constrói o objeto
	 */
	public InfoInfracaoCompleta() {
		super();
	}   	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String sIdInfracao = request.getParameter("id_infracao");
		
		if (sIdInfracao == null || !Pattern.matches("[1-9][0-9]{0,8}",sIdInfracao))
			throw new ServletException("Identificador da Infração enviado invalido!");

		Date inicio = new Date();

		InfracaoCompleta infracao;
		Veiculo veic = null;
		Usuario usuario = null;
		Cadastro cadastro = null;
		CadastroProcesso cp = null;
		InfracaoProcesso ip = null;
		Enquadramento enquadramento = null;
		ImagemGwtBean vi_pan = null;
		long ini1 = 0;
		int qtdeImagens = 0;
		try {
			ini1 = System.currentTimeMillis();
			infracao = InfracaoCompleta.buscaInfracaoPorId(Integer.parseInt(sIdInfracao));
			logger.debug("buscaInfracaoPorId: " + (System.currentTimeMillis() - ini1));
			
			ini1 = System.currentTimeMillis();
			enquadramento = Enquadramento.buscaEnquadramentosPorId(infracao.getIdEnquadramento());
			logger.debug("buscaEnquadramentosPorId: " + (System.currentTimeMillis() - ini1));
			
			ini1 = System.currentTimeMillis();
			veic = Veiculo.buscaVeiculoPorId(infracao.getIdVeiculo());
			logger.debug("buscaVeiculoPorId: " + (System.currentTimeMillis() - ini1));
			
			VeiculoImagensGwtBean vi = VeiculoImagem.buscaVeiculoImagemPorIdVeiculo(infracao.getIdVeiculo());
			qtdeImagens = vi.getListaImagens().size();
			for(ImagemGwtBean i : vi.getListaImagens()) {
				if (i.isPanoramica()) {
					vi_pan = i;
					break;
				}
			}
			
			String i_placa = infracao.getPlaca() != null ? infracao.getPlaca() : veic.getPlaca();
			
			ini1 = System.currentTimeMillis();
			if(i_placa != null) {
				if (!i_placa.trim().equals(""))
					cp = CadastroProcesso.buscarPorPlaca(i_placa);
			}
			logger.debug("CadastroProcesso.buscarPorPlaca: " + (System.currentTimeMillis() - ini1));
			
			ini1 = System.currentTimeMillis();
			if(i_placa != null) {
				if (!i_placa.trim().equals(""))
					cadastro = CadastroBD.buscaCadastroPorPlaca(i_placa);
			}
			logger.debug("CadastroBD.buscaCadastroPorPlaca: " + (System.currentTimeMillis() - ini1));
		}
		catch(Exception err) {
			err.printStackTrace();
			throw new ServletException("Erro ao buscar a infracao: "+err.getMessage());
		}

		
		try {
			SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");
			String nao_disponivel = "N/D";
			//String nao_disponivel = "";
			
			AjaxXMLConstr xml = new AjaxXMLConstr("infracao_completa");
				xml.adicCampo("OPERADOR", nao_disponivel);
			
				xml.adicCampo("DATA_ANALISE", dateformat.format(Calendar.getInstance().getTime()));
			
			xml.adicCampo("ID_INFRACAO", String.valueOf(infracao.getId()));
			xml.adicCampo("LOCAL", String.valueOf(infracao.getIdLocal()));
			
			xml.adicCampo("SERIE_EQUIPAMENTO", nao_disponivel);
			
			xml.adicCampo("CODIGO_EQUIPAMENTO_PRODAM", "0");
			
			xml.adicCampo("DESCLOCAL", infracao.getNomeLocal());
			xml.adicCampo("PISTA", String.valueOf(infracao.getPista()));
			xml.adicCampo("AREA_PISTA", String.valueOf(infracao.getAreaPista()));
			xml.adicCampo("CLASSIFICACAO_TARJA", String.valueOf(infracao.getClassificacaoTarja()));
			
			if(Enquadramento.isEnquadramentoVelocidade(infracao.getIdEnquadramento())) {
				xml.adicCampo("VELOCIDADE", String.valueOf( infracao.getVelocidadeVeiculo()));
				xml.adicCampo("VELOCIDADE_LIMITE", String.valueOf(infracao.getVelocidadeLimite()));
			} else {
				xml.adicCampo("VELOCIDADE", "0");
				xml.adicCampo("VELOCIDADE_LIMITE", "0");
			}
						
			// ORDEM: infracao_processo, movimento_importacao, infracao
			String s_placa = (ip != null && ip.getPlaca() != null && ip.getConcluido() == 1) ? ip.getPlaca() : null;
			
			if(s_placa == null)
			s_placa = infracao.getPlaca() != null ? infracao.getPlaca() : veic.getPlaca();
			
			s_placa = s_placa != null ? s_placa : "";
			
			xml.adicCampo("PLACA", s_placa.trim());
			xml.adicCampo("PLACA_CAI", nao_disponivel);	
			
			if (s_placa.length() == 7) {
				xml.adicCampo("PLACA_P1", s_placa.substring(0, 3));
				xml.adicCampo("PLACA_P2", s_placa.substring(3, 7));
			}
			else {
				xml.adicCampo("PLACA_P1", "");
				xml.adicCampo("PLACA_P2", "");
			}
			
			List<Isento> isentos = new ArrayList<Isento>();
			
			ini1 = System.currentTimeMillis();
			if (infracao.getIdEnquadramento() >= 57461 && infracao.getIdEnquadramento() <= 57463)
			{
				Map<String,Object> mFiltro = new HashMap<String,Object>();
				mFiltro.put("data_validade", infracao.getDataVeiculo());
				mFiltro.put("area", infracao.getAreaPista()); 
				
				isentos = Isento.buscaRapidaIsentoPor(s_placa, infracao.getIdEnquadramento(), infracao.getDataVeiculo(), mFiltro);
			} 
			logger.debug("Isento.buscaRapidaIsentoPor: " + (System.currentTimeMillis() - ini1));
			
			if(isentos.size() > 0) {
				Isento isento_at = isentos.get(0);
				xml.adicCampo("ISENCAO", "SIM");
				xml.adicCampo("ISENCAO_PERIODO", String.format("%s - %s", dateformat.format(isento_at.getDataInicio()), dateformat.format(isento_at.getDataFim())));
			}
			else {
				xml.adicCampo("ISENCAO", "NÃO");
				xml.adicCampo("ISENCAO_PERIODO", "NÃO HÁ");
			}
			
			// ORDEM: infracao_processo, movimento_importacao, infracao
			xml.adicCampo("MARCA", 
			 	cp != null && cp.getMarca() != null ? cp.getMarca() : 
				(cadastro != null && cadastro.getMarcaCliente() != null 
				? cadastro.getMarcaCliente() : nao_disponivel));
			xml.adicCampo("ID_MARCA", 
				cp != null && cp.getIdMarcaCet() != null ? String.valueOf(cp.getIdMarcaCet()) :
				(cadastro != null && cadastro.getIdMarcaCliente() != null 
				? String.valueOf(cadastro.getIdMarcaCliente()) : nao_disponivel));
			
			xml.adicCampo("MARCA_CAI", nao_disponivel);
			xml.adicCampo("ID_MARCA_CAI", nao_disponivel);
			
			xml.adicCampo("MODELO", cadastro != null && cadastro.getMarca() != null ? cadastro.getMarca() : nao_disponivel);
			
			xml.adicCampo("ESPECIE",
				cp != null && cp.getEspecie() != null ? cp.getEspecie() :
				(cadastro != null && cadastro.getEspecieDisponivel() != null 
				? cadastro.getEspecieDisponivel() : nao_disponivel));
			
			xml.adicCampo("CATEGORIA", cadastro != null && cadastro.getCategoria() != null ? cadastro.getCategoria() : nao_disponivel);
			xml.adicCampo("COR", cadastro != null && cadastro.getCor() != null ? cadastro.getCor() : nao_disponivel);
			xml.adicCampo("ANO", cadastro != null && cadastro.getAno() != null ? String.valueOf(cadastro.getAno()) : nao_disponivel);
			xml.adicCampo("MUNICIPIO", cadastro != null && cadastro.getLocalidade() != null ? cadastro.getLocalidade() : nao_disponivel);
			
			xml.adicCampo("UF", 
					cp != null && cp.getUF() != null ? cp.getUF() : 
					(cadastro != null && cadastro.getLocalidade() != null 
					? cadastro.getUfDisponivel() : nao_disponivel));
			
			xml.adicCampo("INCONSISTENCIA_VALIDACAO_PADRAO", String.valueOf(INCONSISTENCIA_VALIDACAO_PADRAO));
			
			xml.adicCampo("STATUS_VALIDACAO", "-1");
			xml.adicCampo("INCONSISTENCIA_PROCESSO", "-1");
			
			xml.adicCampo("INCONSISTENCIA", String.valueOf(infracao.getIdInconsistencia()));
			xml.adicCampo("STATUS", (infracao.getIdInconsistencia() > 0 ? "Inc" : "C") + "onsistente");
			
			xml.adicCampo("MOTIVO_INCONSISTENCIA", "Nenhuma");
			
			xml.adicCampo("INFRACAO", String.valueOf(infracao.getId()));
			xml.adicCampo("ID_VEICULO", String.valueOf(veic.getIdVeiculo()));
			xml.adicCampo("COM_VIDEO", String.valueOf(veic.getComVideo()));
			xml.adicCampo("DATA", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(infracao.getDataVeiculo()));
			xml.adicCampo("ID_ENQUADRAMENTO", String.valueOf(infracao.getIdEnquadramento()));
			if (enquadramento != null) {
				xml.adicCampo("DESCRICAO_ENQUADRAMENTO", enquadramento.getDescricao());
			}
			else {
				xml.adicCampo("DESCRICAO_ENQUADRAMENTO", nao_disponivel);
			}
			InfoEspecificaInfracao infoEspec = InfoEspecificaInfracao.buscaPorInfracao(infracao.getId());
			xml.adicCampo("TIT_INFO_ESPEC", infoEspec.getTituloEspecifica());
			for(int i = 0; i < infoEspec.getDescricaoEspecifica().size(); i++)
			{
				xml.adicCampo("INFO_ESPEC_" + Integer.toString(i + 1), infoEspec.getDescricaoEspecifica().get(i));
			}
			
			xml.adicCampo("INFRACAO_PROCESSO", String.valueOf(infracao.getInfracaoProcesso()));
			
			if (infracao.getIdImagemPAN() > 0)
				xml.adicCampo("ID_IMAGEM_PAN", String.valueOf(infracao.getIdImagemPAN()));
			else if (vi_pan != null)
				xml.adicCampo("ID_IMAGEM_PAN", String.valueOf(vi_pan.getId()));
			else 
				xml.adicCampo("ID_IMAGEM_PAN", "0");
			
			if (infracao.getIdImagemOBJ() != null && infracao.getIdImagemOBJ() > 0)
				xml.adicCampo("ID_IMAGEM_OBJ", String.valueOf(infracao.getIdImagemOBJ()));
			else
				xml.adicCampo("ID_IMAGEM_OBJ", String.valueOf(veic.getIdImagemObj()));
			
			xml.adicCampo("EQUIPAMENTO_CAPTURA_FRONTAL", String.valueOf(infracao.getEquipamentoCapturaFrontal()));
						
			xml.adicCampo("DECISAO_AUDITOR", nao_disponivel);
			
			xml.adicCampo("NOME_AUDITOR", usuario != null ? usuario.getNome() : nao_disponivel);
			
			xml.adicCampo("REGISTRO_AUDITOR", 	nao_disponivel);
			
			xml.adicCampo("DATA_AUDITORIA", dateformat.format(Calendar.getInstance().getTime()));
			
			xml.adicCampo("OBSERVACAO", "");
			
			xml.adicCampo("COM_PESAGEM", String.valueOf(infracao.getComPesagem()));
			
			xml.adicCampo("QTDE_IMAGENS", String.valueOf(qtdeImagens));
			
			xml.dump(response);
		}
		catch(Exception err) {
			err.printStackTrace();
			throw new ServletException("Erro ao montar o XML: "+err.getMessage());
		}
		logger.info("[TEMPO] InfracaoCompleta: " + (new Date().getTime() - inicio.getTime()));
	}  	
}