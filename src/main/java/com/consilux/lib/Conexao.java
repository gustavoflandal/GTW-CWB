/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 21/12/2006

  Descricao: Classe de controle da conexão com BD.

  Historico:

    $Log: Conexao.java,v $
    Revision 1.34  2009/05/26 14:50:29  raoni
    Habilitado o teste quando as conexões do pool ficam em idle e definido o numero máximo de conexões.

    Revision 1.33  2009/05/22 14:13:33  raoni
    Ajusta parâmetros para manter uma determinada quantidade de conexões em idle. Define tempo máximo de espera. Caso exceda o tempo, ocorrerá exceção.

    Revision 1.32  2009/05/18 14:24:45  fos
    Agora utiliza o BasicDataSet do commons da Apache e não do tomcat.

    Revision 1.31  2009/05/18 12:16:06  raoni
    Ajustado para utilizar o DBCP via nossa configuração, ao invés de utilizar JNDI e descoberta do "resource".

    Revision 1.29  2009/05/15 17:35:10  raoni
    Removido a lógica que associava uam conexão a Thread do Tomcat.
    Agora, sempre será devolvido uma nova conexão (do pool do driver).

    Revision 1.28  2009/05/12 14:04:10  raoni
    Ajustado semáforo. Ajustado processo de obtenção do pool.

    Revision 1.27  2009/05/11 17:31:37  raoni
    Define automaticamente o "AutoCommit" das novas conexões para false.

    Revision 1.26  2009/05/11 13:06:48  raoni
    Modificado o pool para utilizar diretamente do driver do SQL Server. Foi removido c3p0 e também o wrapper.

    Revision 1.25  2009/04/24 20:20:29  raoni
    Ajustado parâmetros para obtencao do pool e adicionado informacoes para controle (log no nivel INFO)

    Revision 1.24  2009/04/24 16:51:57  raoni
    Aprimorado controle sobre conexões.

    Revision 1.23  2009/04/20 19:35:41  raoni
    Adicionado teste para verificar o estado das Conexoes que já estão associadas a alguma Thread e remove-las (caso seja necesário)

    Revision 1.22  2009/04/17 20:45:25  raoni
    Configurado alguns parâmetros para tentar evitar a queda de conexões, e a reciclagem. Precisa averiguar o desempenho.

    Revision 1.21  2009/03/12 19:48:16  raoni
    Adicionado lagica para ammarrar uma Thread em uma Conexão.

    Revision 1.20  2009/03/12 15:02:36  raoni
    Modificado para permitir diferencca no debug de conexao fechada com close() ou com finalize.

    Revision 1.19  2009/03/12 13:11:32  raoni
    Configurados parametros de inicialização para valores mais adequados (após o refactoring).

    Revision 1.18  2009/03/10 20:00:56  raoni
    Aumentado o tamanho do pool máximo. Aumentado o tamanho do pool mínimo. Aumentado o incremento ao crescer o pool.

    Revision 1.17  2009/03/10 12:04:51  raoni
    Modificado para utilizar o connection pool do c3p0.

    Revision 1.16  2009/02/06 20:09:20  fos
    Feito ajuste temporário para não cair a conexão mais.

    Revision 1.15  2009/01/16 13:57:53  fos
    Agora retorna o objeto conexão para ser persistido na sessão.

    Revision 1.14  2009/01/12 12:49:46  fos
    Recuperação de repositório.

    Revision 1.12  2008/11/11 16:24:20  fos
    Agora verifica se foi passado o usuário e a senha caso, não seja passado tenta autenticar pelo windows.

    Revision 1.11  2008/08/25 13:36:35  fos
    Tentativa de deixar a classe que controla a conexão com o BD serializável.

    Revision 1.10  2008/07/23 14:23:17  fos
    Retirado o objeto Connection do JDBC da serialização.

    Revision 1.9  2008/07/10 19:47:59  fos
    Agora reinicia a conexão caso esta estaja desconectada.

    Revision 1.8  2008/02/21 21:06:08  fos
    Consertado comentarios	.
    Agora o objeto conexao não se preocupa com a sessão.

    Revision 1.7  2008/01/17 17:10:56  fos
    Feito melhorias para retirar warnings.

    Revision 1.6  2007/07/06 13:03:29  fos
    Agora o pool de conexões requisita também a desconexão.

    Revision 1.5  2007/05/04 13:56:20  fos
    Agora carrega também carrega a informação do database a ser acessado.

    Revision 1.4  2007/04/17 18:00:38  fos
    Ajustado o pacote da classe ConfiguracaoException.

    Revision 1.3  2007/04/17 17:41:26  fos
    Foi retirado o parâmetro para funcionar com cursores porque não executava Stored Procedures

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


 *********************************************************************************/
package com.consilux.lib;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.exception.ConexaoException;
/**
 * Classe de controle da conexão com BD.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.34 $ $Date: 2009/05/26 14:50:29 $ $Author: raoni $
 */
public class Conexao implements Serializable {

	private static final long serialVersionUID = 8101056114772440095L;
	
	private BasicDataSource dataSource;
	private static Conexao s_Instance;
	private static Logger logger = Logger.getLogger(Conexao.class); 

	static final String MSG_CONN = "Conexão %1$s do pool. hashCode = [0x%2$X] active=[%3$d] idle=[%4$d] maxActive=[%5$d]";
	
	/**
	 * Construtor do objeto.
	 */
	private Conexao() throws ConexaoException
	{
		try {
			dataSource = new BasicDataSource();
			
			Configuracao conf = ConfiguracaoProvider.getInstance();
			
			// Obtém o host das configurações.
			String host = conf.getHost();
			if (host == null || host.length() == 0)
				throw new ConfiguracaoException("Erro de configuração: não existe host definido.");


			// Configurações para o pool.
			// http://commons.apache.org/dbcp/configuration.html
			
			dataSource.setUrl("jdbc:sqlserver://" + host);
			dataSource.setDriverClassName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			dataSource.setInitialSize(20);
			dataSource.setMaxIdle(400);
			dataSource.setMinIdle(20);
			dataSource.setMaxActive(400);
			dataSource.setValidationQuery("SELECT 1");
			dataSource.setTestOnReturn(true);
			dataSource.setTestWhileIdle(true);
			dataSource.setTimeBetweenEvictionRunsMillis(1000 * 60 * 3);
			dataSource.setNumTestsPerEvictionRun(10);
			dataSource.setMaxWait(2000);

			// Define a database do sistema.
			dataSource.setDefaultCatalog(conf.getDatabase());
			
			// Se tiver um usuário, utilize este usuário.
			String userName = conf.getUser();
			if (userName != null && userName.length() > 0) {
				dataSource.setUsername(userName);
				dataSource.setPassword(conf.getPassword());
			} else {
				// Caso contrário, use autenticação do Windows.
				dataSource.addConnectionProperty("integratedSecurity", "true");
			}
			
			// Configurações extras (específicas do driver SQL Server).
			dataSource.addConnectionProperty("packetSize", "16384");
			
			int hashCode = this.hashCode();
			logger.info(String.format(MSG_CONN, "Iniciando Conexões", hashCode,
					dataSource.getNumActive(), dataSource.getNumIdle(),
					dataSource.getMaxActive()
			));			
			
			
		} catch (ConfiguracaoException ex) {
			logger.error(ex.getLocalizedMessage());
			throw new ConexaoException("Erro ao obter as configurações do sistema", ex);
		}
		
	}

	/**
	 * Obtém a instância deste Singleton.
	 * @return Conexão JDBC
	 * @throws ConexaoException
	 */
	public static Conexao getInstance() throws ConexaoException {
		if (s_Instance == null)
			s_Instance = new Conexao();

		return s_Instance;
	}

	/**
	 * Obtém a instância deste Singleton.
	 * Método existe apenas para manter compatibilidade.
	 * @return Conexão JDBC
	 * @throws ConexaoException
	 */
	public static Conexao initConexao() throws ConexaoException {
		return Conexao.getInstance();
	}

	/**
	 * Obtém uma conexão JDBC do pool.
	 * @return Conexão JDBC
	 * @throws ConexaoException
	 */
	public static Connection getConexao() throws ConexaoException {

		if (s_Instance == null)
			s_Instance = new Conexao();

		StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[2];
		logger.trace(String.format("%1$s solicitou uma conexão. Quem chamou? %2$s, linha %3$d",
				stackTraceElement.getClassName(), stackTraceElement.getMethodName(),
				stackTraceElement.getLineNumber())
				   );
		
		try {
			Connection poolConnection = s_Instance.dataSource.getConnection();
			poolConnection.setAutoCommit(true);

			Connection conn = new ConnectionAdapter(poolConnection) {
				@Override
				public void close() throws SQLException {
					int hashCode = realConnection.hashCode();
					realConnection.close();
					logger.trace(String.format(MSG_CONN, "devolvida", hashCode,
						s_Instance.dataSource.getNumActive(), s_Instance.dataSource.getNumIdle(),
						s_Instance.dataSource.getMaxActive()
					));					
				}
			};
			
			int hashCode = poolConnection.hashCode();
			logger.trace(String.format(MSG_CONN, "solicitada", hashCode,
					s_Instance.dataSource.getNumActive(), s_Instance.dataSource.getNumIdle(),
					s_Instance.dataSource.getMaxActive()
			));			
			
			return conn;
		} catch (SQLException ex) {
			logger.error("Erro ao obter uma conexão.", ex);
			throw new ConexaoException("Erro ao obter uma conexão.", ex);
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	
}
