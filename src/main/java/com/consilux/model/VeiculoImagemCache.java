package com.consilux.model;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.consilux.lib.GenericCache;

/**
 * Implementação do cache para veículo imagens. SINGLETON.
 * @author raoni
 */
public final class VeiculoImagemCache extends GenericCache<Integer,VeiculoImagem> {

	private static VeiculoImagemCache instance = new VeiculoImagemCache(512);
	private ConcurrentLinkedQueue<Integer> deveriaTerCache;
	private int tamanhoDeveriaTerCache;
	
	private VeiculoImagemCache() {
		// ctor privato, pois é SINGLETON.		
		super();
	}
	
	private VeiculoImagemCache(int quantidadeMaxima) {
		// ctor privato, pois é SINGLETON.
		super(quantidadeMaxima);
		deveriaTerCache = new ConcurrentLinkedQueue<Integer>();
		tamanhoDeveriaTerCache = quantidadeMaxima * 2;
	}	
	
	public static VeiculoImagemCache getInstance() {
		return instance;
	}
	
	/**
	 * Adiciona um objeto VeiculoImagem ao cache
	 * @param chave a chave do objeto a ser adicionado.
	 * @param objeto o objeto a ser adicionado.
	 */
	public void addObjectToCache(Integer idImagem, VeiculoImagem objeto) {

		// Primeiro chama o método da classe pai, que faz o serviço
		super.addObjectToCache(idImagem, objeto);

		// A seguir, um caso especial, que exites apenas para esta classe filha. 
		// Esta imagem acabou de entrar no cache. Neste caso, tambem coloca o seu
		// id em uma outra lista, para controle de "CACHE MISS"
		
		// Abre espaço (se necessário)
		while (deveriaTerCache.size() >= tamanhoDeveriaTerCache) {
			deveriaTerCache.poll();
		}
		// Coloca o id_imagem na lista dos "deveria estar em cache"
		deveriaTerCache.add(idImagem);
	}
	
	public boolean isDeveriaEstarNoCache(Integer idImagem) {
		return deveriaTerCache.contains(idImagem);
	}
	
}
