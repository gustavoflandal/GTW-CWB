package com.consilux.servlet.processamento;

import java.sql.SQLException;
import java.util.List;
import java.util.Map.Entry;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.CacheEventListener;
import com.consilux.lib.CacheEventType;
import com.consilux.model.InfracaoRecenteCache;
import com.consilux.model.VeiculoImagem;
import com.consilux.model.VeiculoImagemCache;

/**
 * Servlet que realiza o trabalho de mediação entre os caches de infração e de imagem.
 * @author raoni
 */
public class CacheMediatorServlet extends HttpServlet  {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CacheMediatorServlet.class);
	
	private final static VeiculoImagemCache cacheImagens = VeiculoImagemCache.getInstance();
	private final static InfracaoRecenteCache cacheInfracoes = InfracaoRecenteCache.getInstance();

	@Override
	public void init(final ServletConfig config) throws ServletException {
		
		// Adiciona um listener ao cache de imagens para que ele nos avise quando alguma
		// imagem for removida do cache.
		cacheImagens.addCacheListener(new CacheEventListener<Integer>() {
			@Override
			public void CacheEvent(Object sender, CacheEventType eventType, Integer idImagem) {
				if (eventType == CacheEventType.REMOVE) {

					// Loga a operação.
					logger.debug("Imagem [" + idImagem +"] removida do cache. size = [" +
					cacheImagens.getSize() + "], queueSize = [" + cacheImagens.getQueueSize() + "]");
					
					// Procura no cache de infrações a primeira infração que possua esta imagem.
					Integer idInfracao = null; 
					for (Entry<Integer,List<Integer>> inf : cacheInfracoes.entrySet()) {
						if (inf.getValue().contains(idImagem)) {
							idInfracao = inf.getKey();  
							break;
						}
					}
					
					// Se achou a infração, manda o cache de infrações remover ela.
					if (idInfracao != null) {
						cacheInfracoes.removeObject(idInfracao);
					}
					
				}
			}
		});		
		
		
		// Adiciona um listener ao cache de infrações para que ele nos avise quando alguma
		// infração for adicionada ao cache.
		cacheInfracoes.addCacheListener(new CacheEventListener<Integer>() {
			@Override
			public void CacheEvent(Object sender, CacheEventType eventType, final Integer idInfracao) {
				
				switch (eventType) {
					
					case ADD:
						// Uma infração foi adicionada ao cache de infrações. Neste caso, devemos buscar
						// as suas imagens e adicionar (as imagens) ao cache de imagens.
						
						// Loga a operação.
						logger.debug("Infração [" + idInfracao +"] adicionada ao cache. size = [" +
								cacheInfracoes.getSize() + "], queueSize = [" + cacheInfracoes.getQueueSize() + "]");
						
						// Cria um work item (que vai realizar a busca no banco e preparar o retorno) 
						Runnable asyncTask = criaWorkItemBuscaImagens(idInfracao);
						
						cacheInfracoes.submitTask(asyncTask);
						break;
					
					case REMOVE:
						logger.debug("Infração [" + idInfracao +"] removida do cache.");					
						break;
					
					case REFRESH:
						
						logger.debug("Infração [" + idInfracao +"] sofreu REFRESH. size = [" +
								cacheInfracoes.getSize() + "], queueSize = [" + cacheInfracoes.getQueueSize() + "]");
						
						List<Integer> listaImagens = cacheInfracoes.findObject(idInfracao);
						if (listaImagens != null) {
							for (Integer idImagem : listaImagens) {
								cacheImagens.refreshObject(idImagem);
							}
						}
						break;
				}
				
			}
		});
		
	}

	/**
	 * Método auxiliar que cria um work item para buscar as imagens de uma infração no banco de dados.
	 * @param idInfracao
	 * @return
	 */
	private Runnable criaWorkItemBuscaImagens(final Integer idInfracao) {
		
		return new Runnable () {
			
			@Override
			public void run() {
				
				long inicio = System.currentTimeMillis();
				
				logger.debug("WorkItemBuscaImagens. thread [" + Thread.currentThread().hashCode() + "]");
				try {
					List<VeiculoImagem> lRet = VeiculoImagem.buscaVeiculoImagemPorIdInfracaoBD(idInfracao);
					logger.debug("WorkItemBuscaImagens. thread [" + Thread.currentThread().hashCode() + "], gastou [" +
							(System.currentTimeMillis() - inicio) + "] msecs.");
					
					for (VeiculoImagem imgResult : lRet) {
						cacheImagens.addObjectToCache(imgResult.getIdImagem(), imgResult);
						logger.debug("Imagem [" + imgResult.getIdImagem() +"] adicionada ao cache. size = ["
							+ cacheImagens.getSize() + "], queueSize = [" + cacheImagens.getQueueSize() + "]");					
					}
				} catch (SQLException se) {
					logger.error("Erro na busca assíncrona de imagens. id_infracao = [" + idInfracao +"]", se);
				} catch (ConexaoException ce) {
					logger.error("Erro na busca assíncrona de imagens. id_infracao = [" + idInfracao +"]", ce);
				}
			}
		};
	}
	
}
