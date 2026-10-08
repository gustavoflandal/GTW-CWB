/**********************************************************************************

  Projeto: GTW
  Nome do Módulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Luiz Amaral
  Data: 27/11/2015

  Descrição: Classe de negócio para gravação de logs da remessa CAV no banco de dados.

*********************************************************************************/
package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

/**
 * Classe de negócio para gravação de logs das remessas no CAV - no banco de dados.
 * Luiz Amaral - Consilux Tecnologia
 * data: 27/11/2015
 */
public class LogRemessa {
	
	protected final static Logger logger = Logger.getLogger(LogRemessa.class);

	//Luiz Amaral
	//Data: 25/11/2015
	//Tabela de Log a ser usada 
	public void insereLogRemessa(int idRemessa, String motivo, String mensagem)
	{
		Connection conn = null;
		
		try{
			
			if(motivo.equals("") || mensagem.equals("") || idRemessa == 0)
			{
				logger.error("insereLogRemessa --> Informações incorretas. IdRemessa: " + idRemessa + ", motivo: " + motivo + ", mensagem: " + mensagem);
			}
			else
			{
				
				//Tratando a mensagem para não ter mais de 500 caracteres
				String msgTratada = trataMsg(mensagem);
				
				logger.info("Inserindo LogRemessa. ID_REMESSA: " + idRemessa + ", MOTIVO: " + motivo + ", MENSAGEM: " + msgTratada);
				
				//Caso haja erro no tratamento da msg então não grava
				if(!msgTratada.equals(""))
				{
					conn = Conexao.getConexao();
					StringBuilder sbSQLog = new StringBuilder();
					
					sbSQLog.append(" insert into log_analisador_remessa  ");
					sbSQLog.append(" (id_remessa, motivo, contexto_anterior, data_gravacao) ");
					sbSQLog.append(" values(?, ?, ?, ?) ");
					
					
					PreparedStatement psLog = null;
					psLog = conn.prepareStatement(sbSQLog.toString());
					Timestamp dtNow = new Timestamp(System.currentTimeMillis()); 
					
					psLog.setInt(1, idRemessa);
					psLog.setString(2, motivo);
					psLog.setString(3, msgTratada);
					psLog.setTimestamp(4, new java.sql.Timestamp(dtNow.getTime()));
					
					psLog.executeUpdate();
				}
			}
		}
		catch(Exception e){
			logger.error("insereLogRemessa --> Falha no INSERT da tabela log_analisador_remessa. IdRemessa: " + idRemessa , e);
		}
		finally 
		{
			try {
				if (conn != null)
					conn.close();
			}catch(Exception e) {
				logger.error("insereLogRemessa --> Falha ao fechar conexão com banco de dados!", e);
			}
		}
	}
	
	//Luiz Amaral
	//Data: 25/11/2015
	//Trata as mensagens para não terem mais que 500 caracteres
	public String trataMsg(String msg)
	{
		String ret = "";
		try
		{
			int tamanho = msg.length();
			
			if(tamanho > 499)
				ret = msg.substring(0, 498);
			else
				ret = msg.substring(0, tamanho); //Evita leitura errada do lenght() e tentativa de inserção maior que 500 caracteres
		}
		catch(Exception ex)
		{
			logger.error("insereLogRemessa --> Falha ao tratar mensagem. Msg: " + msg, ex);
			ret = "";
		}
		return ret;
	}
}