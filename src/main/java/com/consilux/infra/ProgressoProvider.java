package com.consilux.infra;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

public class ProgressoProvider {
	static ProgressoProvider pv = null;
	private Map<Integer,Progresso> pList = null;
	
	private ProgressoProvider() {
		pList = new HashMap<Integer, Progresso>();
	}

	public Progresso criaProgresso(HttpSession se, Integer id) {
		Progresso p = new Progresso(se, this, id);
		pList.put(id, p);
		return p;
	}

	public void removeProgresso(Integer id) {
		pList.remove(id);
	}

	public Progresso getProgresso(Integer valueOf) {
		return pList.get(valueOf);
	}
	
	public static ProgressoProvider getInstance() {
		if (pv == null)
			pv = new ProgressoProvider();
		return pv;
	}

}
