/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 17/05/2007

  Descricao: Classe de controle de versão do sistema

  Historico:

    $Log: Versao.java,v $
    Revision 1.4  2009/04/23 11:51:47  fos
    Agora mostra a data da BUILD

    Revision 1.3  2009/01/12 12:49:51  fos
    Recuperação de repositório.

    Revision 1.1  2007/07/06 13:01:34  fos
    Agora com controle de versão extendo.


*********************************************************************************/
package com.consilux.conf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.jar.Manifest;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import com.consilux.conf.exception.ConfiguracaoException;

/**
 * Classe de controle de versão do sistema.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/04/23 11:51:47 $ $Author: fos $
 */
public class Versao extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Versao ver = null;
	private Date dataBuild = null;
	private String numeroBuild = null;
	private String versaoStr = null;
	private Manifest manifest = null;
	public static final String ARQ_MANIFEST = "/META-INF/MANIFEST.MF";
	public Versao() { }
	/**
	 * Constrói uma configuração e mantém no ambiente.
	 * @param sc Contexto do servlet do J2EE
	 * @throws ConfiguracaoException
	 * @throws IOException 
	 */
	private Versao(ServletContext sc) throws ConfiguracaoException, IOException {
		
		File fManifest = new File(sc.getRealPath(ARQ_MANIFEST));
		FileInputStream fiManifest = new FileInputStream(fManifest);
		
		manifest = new Manifest(fiManifest);
		numeroBuild = manifest.getMainAttributes().getValue("Implementation-Build");
		versaoStr = manifest.getMainAttributes().getValue("Version");
		dataBuild = new Date(fManifest.lastModified());

	}
	/**
	 * Carrega a versão do arquivo.
	 * @return Retorna uma instância.
	 * @throws ConfiguracaoException
	 */
	public static Versao getInstance() throws ConfiguracaoException {
		if (ver == null) {
			throw new ConfiguracaoException("Versão não carregada. Erro na inicialização?");
		}
		return ver;
	}
	public String getVersaoStr() {
		return versaoStr;
	}
	/**
	 * Retorna a data da última BUILD do projeto.
	 * @return Data da BUILD.
	 */
	public Date getDataBuild() {
		return dataBuild;
	}
	/**
	 * Retorna o número da última BUILD do projeto.
	 * @return Data da BUILD.
	 */
	public String getNumeroBuild() {
		return numeroBuild;
	}
    /**
	 * @return the manifest
	 */
	public Manifest getManifest() {
		return manifest;
	}
	/* (non-Javadoc)
     * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
     */
    public void init(ServletConfig config) throws ServletException {
    	try {
    		ver = new Versao(config.getServletContext());
    	}
    	catch(Exception err) {
    		throw new ServletException("Erro ao carregar a versão. ["+err.getMessage()+"]");
    	}
    }

}
