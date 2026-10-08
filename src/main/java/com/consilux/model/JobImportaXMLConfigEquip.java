/**********************************************************************************

  Projeto: GTW

  Nome do Modulo: com.consilux.model

  Empresa: Consilux Tecnologia

  Autor: fos
  Data: 01/04/2010

  Descricao: Processa infrações em processos que possuem processamento automático.

  Historico:

    $Log$

 *********************************************************************************/
package com.consilux.model;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Semaphore;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.xml.sax.SAXException;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.exception.ModelException;

/**
 * XXX
 * @author Fernando de Souza
 * @version $Revision$ $Date$ $Author$
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobImportaXMLConfigEquip implements Job {

	private static Logger logger = Logger.getLogger(JobImportaXMLConfigEquip.class);
	private static Semaphore semaforoExecucao = new Semaphore(1);
	private static String MSG_ERRO = "Não foi possível definir o usuário padrão do sistema.";

	public void execute(JobExecutionContext cetx) throws JobExecutionException {

		logger.info("Iniciando JobImportaXMLConfigEquip");

		try {
			boolean lockExclusivo = false;
			try {
				// tenta obter o lock.
				lockExclusivo = semaforoExecucao.tryAcquire();
				if (!lockExclusivo) {
					logger.info("Já existe um JobImportaXMLConfigEquip em execução.");
					return;
				}
				
				// try/catch, caso ocorra erro ao incluir o configEquip não existentes, não deve deixar de atualizar a sequencia_local dos demais arquivos.
				try{
					incluirConfigEquipNaoExtistentes();
				}
				catch (Exception ex) {
						logger.error("Erro ao tentar incluir ConfigEquip não extistentes.", ex);
				}
				
				// atualiza sequencia_local dos configEquips existentes.
				//atualizarSequenciaLocalVeiculoImportacao(); // será feito na job do sql

				logger.info("Finalizando JobImportaXMLConfigEquip");
			}
			finally {
				if (lockExclusivo)
					semaforoExecucao.release();
			}
		}
		catch (Exception ex) {
			logger.error("Erro ao executar JobImportaXMLConfigEquip.", ex);
		}
		logger.info("JobImportaXMLConfigEquip finalizado.");
	}

//	private void atualizarSequenciaLocalVeiculoImportacao() throws ConexaoException, SQLException {
//
//		Connection con = null;
//		CallableStatement cs = null;
//
//		try {
//
//			con = Conexao.getConexao();
//
//			String sSQL = "{call spu_finaliza_importacao_sequencia_local}";
//			cs = con.prepareCall(sSQL);
//			cs.execute();
//		}
//		finally {
//
//			if( cs != null )
//				cs.close();
//
//			if (con != null)
//				con.close();
//		}
//
//	}

	private void incluirConfigEquipNaoExtistentes() throws ModelException, ConexaoException, ConfiguracaoException, SQLException, IllegalArgumentException, ParserConfigurationException, IOException, SAXException, IllegalAccessException, InstantiationException  {

		Configuracao conf = ConfiguracaoProvider.getInstance();

		if (!conf.getConfiguracaoChaveValor().containsKey("usuario_sistema"))
			throw new ModelException(MSG_ERRO);

		try {

			int idUsuarioSistema = Integer.parseInt(conf.getConfiguracaoChaveValor().get("usuario_sistema")); 

			boolean erro = false;
			ConfigEquipPendenteImportacao configEquipPendente = ConfigEquipPendenteImportacao.getNextConfigEquipPendenteImportacao();
			while(!erro && configEquipPendente != null){
				TConfigEquip ce = TConfigEquip.deserializeConfigEquip( configEquipPendente.getXmlConfiguracao() );

				// garantir que será criado nova sequencia.
				ce.local.sequenciaLocal = 0;

				// garantir que será usado o da configuração
				ce.idGrupoEquipamento = 0;

				logger.info("JobImportaXMLConfigEquip esta incluindo uma nova configuração... Equipamento:[" + ce.serie + "]" );
				try {
					ConfiguracaoEquipamento.incluirConfigEquip( ce , idUsuarioSistema , true ); // incluir novo ce, sem torna-lo vigente
				} catch(Exception e) {
					logger.error("Não foi possível inserir o ConfigEquip!");
					erro = true;
				}
				
				if(!erro)
				configEquipPendente = ConfigEquipPendenteImportacao.getNextConfigEquipPendenteImportacao();
			}

		}
		catch (NumberFormatException fe) {
			logger.error(MSG_ERRO, fe);
			throw new ModelException(MSG_ERRO, fe);
		}
	}
}
