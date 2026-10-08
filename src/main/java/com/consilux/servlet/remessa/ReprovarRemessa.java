/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Fernando Amaral
  Data: 14/01/2015

  Descricao: Servlet que dispara a reprovação do lote por erros

*********************************************************************************/
package com.consilux.servlet.remessa;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.infra.AjaxXMLConstr;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.Acesso;
import com.consilux.model.AvisoMovimentoReprovado;
import com.consilux.model.Mensagem;
import com.consilux.model.Remessa;
import com.consilux.model.exception.ModelException;


public class ReprovarRemessa extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(ReprovarRemessa.class); 
	
	private Integer idRemessa;
	private String nomeArquivo;
	private static final String msgErro = 	 "A Reprovação do Lote não pode ser completada por erros" +
			   								 " de processamento no Banco de Dados. Favor entrar em contato com a" +
			   								 " administração do sistema.";
	
	/**
	 * Constrói o objeto
	 */
	public ReprovarRemessa() {
		super();
	}   	
	
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Acesso acesso = new Acesso(request, response, true); 
		
		if (!acesso.verificaAcesso(false)) {
			new Mensagem(response).showErro("Usuário não atenticado!", "/login/gtw_principal.jsp");
			return; //O usuário não tem acesso...então cai fora!
		}
		
		try {
			
			//Luiz Amaral 13/01/2015
			//Reprovar o Lote dentro a tabela Remessa (reprovado = 1)
			String sAcao = "";
			if(request.getParameter("acao") != null)  {sAcao = request.getParameter("acao");}
			
			if(sAcao.equals("ReprovarMovimento")){
				
				String sidRemessa = request.getParameter("idRemessa");
				idRemessa = Integer.parseInt(sidRemessa);
				obterRemessa(idRemessa);

				if(getIdRemessa() > 0 && !getNomeArquivo().equals("")){
					
					String retorno = ReprovarLote(idRemessa);
					ReprovarLotePendentes(idRemessa);
					ReprovarLoteIteracao(idRemessa);
				
					AvisoMovimentoReprovado aviso = new AvisoMovimentoReprovado(getIdRemessa(), 
																			    getNomeArquivo(), 
																				"", 
																				"", 
																				true);
					aviso.run();
					mostraMensagemUsuario(response, retorno);
				}else{
					mostraMensagemUsuario(response, msgErro);
				}
			}

		}
		catch(Exception err) {
			logger.error("Erro ao Reprovar Lote.", err);
			mostraMensagemUsuario(response, msgErro);
			return;
		}
		
	}  	

	
	//Luiz Amaral 13/01/2015
	//Reprocação de Lote da tela de Validação
	public String ReprovarLote(Integer idRemessa) throws Exception {

		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;

		Remessa rem = Remessa.buscarRemessaPorId(idRemessa);
		
		try {
			sbSQL.append(" 		UPDATE remessa WITH(ROWLOCK)");
			sbSQL.append(" 			SET reprovado = 1 ");
			sbSQL.append(" 		WHERE id_remessa = ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idRemessa);
			
			ps.executeUpdate();
			
			String msg = "Lote: " + 
						 rem.getDescricaoApaitAlt() + 
						 " Reprovado com sucesso!";
			
			logger.info(msg);
			return msg;
			
		}catch(SQLException ex){
			
			String msg = "A Reprovação do Lote não pode ser completada por erros" +
					   " de processamento no Banco de Dados. Favor entrar em contato com a" +
					   " administração do sistema." + " Mov: " + (rem != null ? rem.getDescricaoApait() : "N/D");
			
			logger.error("Erro ao entregar XML de resposta.", ex);
			logger.error (msg);
			return msg;
		}
		finally {
			if (conn != null)
				conn.close();
			if (ps != null)
				ps.close();
		}
	}
	
	public String ReprovarLotePendentes(Integer idRemessa) throws Exception {

		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;

		Remessa rem = Remessa.buscarRemessaPorId(idRemessa);
		
		try {
			sbSQL.append(" 		UPDATE movimentos_pendentes WITH(ROWLOCK)");
			sbSQL.append(" 			SET reprovado = 1 ");
			sbSQL.append(" 		WHERE id_remessa = ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idRemessa);
			
			ps.executeUpdate();
			
			String msg = "Lote: " + 
						 rem.getDescricaoApaitAlt() + 
						 " Reprovado com sucesso!";
			
			logger.info(msg);
			return msg;
			
		}catch(SQLException ex){
			
			String msg = "A Reprovação do Lote não pode ser completada por erros" +
					   " de processamento no Banco de Dados. Favor entrar em contato com a" +
					   " administração do sistema." + " Mov: " + (rem != null ? rem.getDescricaoApait() : "N/D");
			
			logger.error("Erro ao entregar XML de resposta.", ex);
			logger.error (msg);
			return msg;
		}
		finally {
			if (conn != null)
				conn.close();
			if (ps != null)
				ps.close();
		}
	}
	
	public String ReprovarLoteIteracao(Integer idRemessa) throws Exception {

		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;

		Remessa rem = Remessa.buscarRemessaPorId(idRemessa);
		
		try {
			sbSQL.append(" 		UPDATE remessa_iteracao WITH(ROWLOCK)");
			sbSQL.append(" 			SET data_fim = GETDATE() ");
			sbSQL.append(" 		WHERE id_remessa = ? ");
			
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idRemessa);
			
			ps.executeUpdate();
			
			String msg = "Lote: " + 
						 rem.getDescricaoApaitAlt() + 
						 " Reprovado com sucesso!";
			
			logger.info(msg);
			return msg;
			
		}catch(SQLException ex){
			
			String msg = "A Reprovação do Lote não pode ser completada por erros" +
					   " de processamento no Banco de Dados. Favor entrar em contato com a" +
					   " administração do sistema." + " Mov: " + (rem != null ? rem.getDescricaoApait() : "N/D");
			
			logger.error("Erro ao entregar XML de resposta.", ex);
			logger.error (msg);
			return msg;
		}
		finally {
			if (conn != null)
				conn.close();
			if (ps != null)
				ps.close();
		}
	}
	
	//Luiz Amaral 13/01/2015
	//Retorno de resposta ao usuário em caso de erros
	private void mostraMensagemUsuario(HttpServletResponse response, String mensagem) {
		AjaxXMLConstr xml;
		try {
			xml = new AjaxXMLConstr("ErrosReprovarLote");
			xml.adicCampo("RETORNO", mensagem);
			xml.dump(response);
		}
		catch (Exception e) {
			logger.error("Erro ao entregar XML de resposta.", e);
		}
	}
	
	
	
	/**
	 * Buscar a remessa com os dados de idinterno e nome do arquivo
	 * @author Luiz Fernando Amaral - Consilux Tecnologia
	 * Data: 15/01/2015
	 */
	public void obterRemessa(Integer idRemessa) throws ConexaoException, SQLException, ModelException {
		
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append(" SELECT   ");
		sbSQL.append(" 	ma.nome_arquivo  ");
		sbSQL.append(" FROM remessa  r (NOLOCK) ");
		sbSQL.append(" JOIN movimento_arquivo ma (NOLOCK)  ");
		sbSQL.append(" 	on r.id_movimento_arquivo = ma.id_movimento_arquivo  ");
		sbSQL.append(" WHERE   ");
		sbSQL.append(" 	r.id_remessa = ?  ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setLong(1, idRemessa);
			
			rs = ps.executeQuery();
			
			if(rs.next()){
				setNomeArquivo(rs.getString("nome_arquivo"));
			}
				
		}catch (SQLException e) {
			logger.error("ERRO de SQL ao obterRemessa - Processo de Reprovação de Lote.", e);
			throw new ModelException("ERRO de SQL ao obterRemessa - Processo de Reprovação de Lote", e);
		}	
		finally {
			if (conn != null)
				conn.close();
		}
	}


	public Integer getIdRemessa() {
		return idRemessa;
	}


	public void setIdRemessa(Integer idRemessa) {
		this.idRemessa = idRemessa;
	}


	public String getNomeArquivo() {
		return nomeArquivo;
	}


	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}	
	
}
