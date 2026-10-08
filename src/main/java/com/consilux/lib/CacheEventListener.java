package com.consilux.lib;

/**
 * INterface que define um listener genérico para eventos ocorrido em um cache.
 * @author raoni
 * @param <TKey> o tipo da chave utilizado no cache.
 */
public interface CacheEventListener<TKey> {

	void CacheEvent(Object sender, CacheEventType eventType, TKey cacheEntry);
	
}
