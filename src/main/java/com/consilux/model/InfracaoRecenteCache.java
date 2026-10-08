package com.consilux.model;

import java.util.List;

import com.consilux.lib.GenericCache;

/**
 * Implementação do cache para infrações recentes. SINGLETON.
 * @author raoni
 */
public class InfracaoRecenteCache extends GenericCache<Integer,List<Integer>> {

	private static InfracaoRecenteCache instance = new InfracaoRecenteCache(512);
	
	private InfracaoRecenteCache() {
		// ctor privato, pois é SINGLETON.		
		super();
	}	
	
	private InfracaoRecenteCache(int quantidadeMaxima) {
		// ctor privato, pois é SINGLETON.
		super(quantidadeMaxima);
	}
	
	public static InfracaoRecenteCache getInstance() {
		return instance;
	}
	
}
