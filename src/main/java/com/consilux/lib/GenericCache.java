package com.consilux.lib;

import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Classe que implementa um cache genérico (multi-thread) de objetos.
 * O tempo de vida do objeto no cache é controlado por FIFO. 
 * @author raoni
 * @param <TKey> o tipo da chave do objeto
 * @param <TValue> o tipo do objeto
 */
public class GenericCache<TKey,TValue> {

	ConcurrentLinkedQueue<TKey> filaCache;
	ConcurrentHashMap<TKey,TValue> mapaCache;
	int quantidadeMaxima;
	Set<CacheEventListener<TKey>> cacheListeners;
	
	private static ExecutorService threadPool = Executors.newFixedThreadPool(10);
	
	/**
	 * Tamanho default do cache.
	 */
	public static final int TAMANHO_DEFAULT = 100;
	
	/**
	 * Cria um novo cache de objetos com o tamanho default de (100) objetos.
	 * Após este valor, os objetos serão reciclados (FIFO).
	 */
	public GenericCache() {
		this(TAMANHO_DEFAULT);
	}	
	
	/**
	 * Cria um novo cache de objetos.
	 * @param quantidadeMaxima a quantidade máxima de objetos permitidos no cache.
	 * Após este valor, os objetos serão reciclados (FIFO).
	 */
	public GenericCache(int quantidadeMaxima) {
		
		if (quantidadeMaxima > 0)
			this.quantidadeMaxima = quantidadeMaxima;
		else
			this.quantidadeMaxima  = TAMANHO_DEFAULT;
		
		this.filaCache = new ConcurrentLinkedQueue<TKey>();
		this.mapaCache = new ConcurrentHashMap<TKey, TValue>();
		this.quantidadeMaxima= quantidadeMaxima;
		this.cacheListeners = new HashSet<CacheEventListener<TKey>>();
	}
	
	/**
	 * Provoca um refresh do objeto (move ele para o fim da fila).
	 * @param chave a chave do objeto
	 */
	public void refreshObject(TKey chave) {
		
		if (chave != null && filaCache.contains(chave))
		{
			filaCache.remove(chave);
			filaCache.offer(chave);
			onCacheEvent(CacheEventType.REFRESH, chave);
		}
	}
	
	/**
	 * Adiciona um objeto ao cache
	 * @param chave a chave do objeto a ser adicionado.
	 * @param objeto o objeto a ser adicionado.
	 */
	public void addObjectToCache(TKey chave, TValue objeto) {
		if (chave != null && objeto != null)
		{
			TValue valorExistente = mapaCache.get(chave);
			
			// Achou o valor (é uma chave/valor que já existe).
			if (valorExistente != null)
			{
				// Reposiciona no começo
				refreshObject(chave);
			}
			else
			{
				// Não achou a chave/valor. É um novo par. Adicione.
				
				// Se o cache estiver "cheio", abre espaço
				while (mapaCache.size() >= quantidadeMaxima) {
					TKey itemRemover = filaCache.poll();
					removeObject(itemRemover);
				}				
				
				// Por fim, adiciona ao cache.
				filaCache.offer(chave);
				mapaCache.put(chave, objeto);
				onCacheEvent(CacheEventType.ADD, chave);
			}
		}
	}
	
	
	/**
	 * Verifica se o cache contém uma chave. 
	 * @param chave a chave a se verificar.
	 * @return true ou false
	 */
	public final boolean containsKey(TKey chave) {
		return mapaCache.containsKey(chave);
	}
	
	/**
	 * Agenda a execução de uma função de maneira assíncrona. 
	 * @param asyncTask a função que será executada.
	 * @return um objeto Future, ou null
	 */
	public Future<?> submitTask(Runnable asyncTask) {
		
		if (asyncTask != null) {
			return threadPool.submit(asyncTask);
		} else {
			return null;
		}
	}
	
	/**
	 * Remove um objeto ao cache.
	 * @param chave a a chave do objeto a ser removido. 
	 */
	public void removeObject(TKey chave) {

		if (mapaCache.containsKey(chave))
		{
			filaCache.remove(chave);
			mapaCache.remove(chave);
			onCacheEvent(CacheEventType.REMOVE, chave);
		}
	}	
	
	/**
	 * Busca um objeto do cache 
	 * @param chave a chave do objeto que se deseja buscar.
	 * @return o objeto desejado ou null, caso não encontre.
	 */
	public TValue findObject(TKey chave) {
		return mapaCache.get(chave);
	}
	
	/**
	 * Recupera o tamanho do cache.
	 * @return
	 */
	public int getSize () {
		return mapaCache.size();
	}
	
	/**
	 * Recupera o tamanho da fila cache.
	 * @return
	 */
	public int getQueueSize () {
		return filaCache.size();
	}	
	
	/**
	 * Adiciona um listener a ser notificado sobre eventos de caching.
	 * @param listener o listener a ser adicionado.
	 */
	public final void addCacheListener(CacheEventListener<TKey> listener) {
		cacheListeners.add(listener);
	}
	
	/**
	 * Remove um listener da lista de notificação de eventos. 
	 * @param listener o listener a ser removido.
	 */
	public final void removeCacheListener(CacheEventListener<TKey> listener) {
		cacheListeners.remove(listener);
	}
	
	/**
	 * Sobe um evento para todos os listeners registrados. Deve ser invocado pelas classes
	 * filhas quando algum evento acontecer.
	 * @param eventType
	 * @param cacheEntry
	 */
	protected final void onCacheEvent(CacheEventType eventType, TKey cacheEntry) {
		for(CacheEventListener<TKey> listener : cacheListeners) {
			listener.CacheEvent(this, eventType, cacheEntry);
		}
	}
	
	/**
	 * Recupera o entrySet utilizado no map interno.
	 * @return
	 */
	public Set<Entry<TKey, TValue>> entrySet() {
		return mapaCache.entrySet();
	}
	
}
