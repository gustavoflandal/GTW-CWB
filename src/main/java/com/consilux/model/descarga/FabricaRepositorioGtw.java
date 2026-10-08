package com.consilux.model.descarga;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.config.PersistenceUnitProperties;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.descarga.repositorio.RepositorioException;
import com.consilux.model.descarga.repositorio.jpa.FabricaRepositorioJpa;

/**
 * Fábrica de repositórios do GTW. Baseada em JPA, utilizando banco de dados
 * MS SQL Server. Possui comportamento read-only, não aceita parâmetros externos
 * e utiliza o mesmo banco de dados que o GTW.
 * @author raoni
 */
public class FabricaRepositorioGtw extends FabricaRepositorioJpa {

	private static final String GTW_PERSISTENCE_UNIT = "DescargaGtwPersistence"; 
	
	public FabricaRepositorioGtw() throws RepositorioException {
		super(GTW_PERSISTENCE_UNIT, prepareProperties());
	}

	private static Map<String,Object> prepareProperties() throws RepositorioException {
		
		Map<String,Object> mRet = new HashMap<String,Object>();
		
		// Coloca o mesmo datasource que o GTW utiliza.
		try {
			mRet.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, Conexao.getInstance().getDataSource());
		} catch (ConexaoException ce) {
			throw new RepositorioException("Erro ao recuperar o datasource do GTW para exportação de descargas.", ce);
		}
		
		return mRet;
	}
	
}
