package com.consilux.exportalista;

import java.util.List;

public abstract class ExportaLista {
	
	
	private static List<? extends ExportaLista> lista;
	
	public static void setListaParaRelatorio(List<? extends ExportaLista> lista2){
		
		lista = lista2;
	}
	
	public static List<? extends ExportaLista> getListaParaRelatorio(){
		
		return lista;
	}

}
