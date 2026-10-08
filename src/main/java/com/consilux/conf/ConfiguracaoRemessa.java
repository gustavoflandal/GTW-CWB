package com.consilux.conf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.consilux.model.ExportaRemessa;
import com.consilux.model.TipoRemessa;
import com.consilux.model.remessa.RemessaFactory;

public class ConfiguracaoRemessa implements Serializable {

	private static final long serialVersionUID = -6137489995409096563L;

	private String layoutPrem;
	private String logotipoEmpresa;
	private String logotipoEmpresaVertical;
	private Class<? extends ExportaRemessa<?>> implementacaoExportaRemessa;
	private Class<? extends RemessaFactory> implementacaoGeraRemessa;
	private Map<String, TipoRemessa> mapaTipos = new HashMap<String, TipoRemessa>();
	
	public ConfiguracaoRemessa() {
		// Construtor padrão sem parâmetros, padrão JavaBeans		
	}

	public ConfiguracaoRemessa(String layoutPrem, String logotipoEmpresa, String logotipoEmpresaVertical,
			Class<? extends ExportaRemessa<?>> implementacaoExportaRemessa,
			Map<String, TipoRemessa> mapaTipos, Class<? extends RemessaFactory> implementacaoGeraRemessa) {
		this.layoutPrem = layoutPrem;
		this.logotipoEmpresa = logotipoEmpresa;
		this.logotipoEmpresaVertical = logotipoEmpresaVertical;
		this.implementacaoExportaRemessa = implementacaoExportaRemessa;
		this.implementacaoGeraRemessa = implementacaoGeraRemessa;
		
		if (mapaTipos != null && mapaTipos.size() > 0)
			this.mapaTipos.putAll(mapaTipos);
	}

	public String getLayoutPrem() {
		return layoutPrem;
	}

	public void setLayoutPrem(String layoutPrem) {
		this.layoutPrem = layoutPrem;
	}

	public String getLogotipoEmpresa() {
		return logotipoEmpresa;
	}

	public void setLogotipoEmpresa(String logotipoEmpresa) {
		this.logotipoEmpresa = logotipoEmpresa;
	}

	public String getLogotipoEmpresaVertical() {
		return logotipoEmpresaVertical;
	}

	public void setLogotipoEmpresaVertical(String logotipoEmpresaVertical) {
		this.logotipoEmpresaVertical = logotipoEmpresaVertical;
	}

	public Class<? extends ExportaRemessa<?>> getImplementacaoExportaRemessa() {
		return implementacaoExportaRemessa;
	}

	public Class<? extends RemessaFactory> getImplementacaoGeraRemessa() {
		return implementacaoGeraRemessa;
	}	
	
	public void setImplementacaoGeraRemessa(Class<? extends RemessaFactory> implementacaoGeraRemessa) {
		this.implementacaoGeraRemessa = implementacaoGeraRemessa;
	}

	public void setImplementacaoExportaRemessa(Class<? extends ExportaRemessa<?>> implementacaoExportaRemessa) {
		this.implementacaoExportaRemessa = implementacaoExportaRemessa;
	}

	public Map<String,TipoRemessa> getMapaTipos() {
		return mapaTipos;
	}

	public void setMapaTipos(Map<String, TipoRemessa> mapaTipos) {
		this.mapaTipos = mapaTipos;
	}
	
	public String getCodigoRemessa(TipoRemessa tipoRemessa) {
		return tipoRemessa.getCodigo();
	}
	
	public String getModeloAIT(String codigo){
		for (TipoRemessa tipoRemessa : mapaTipos.values()) {
			if (tipoRemessa.getCodigo().compareTo(codigo) == 0)
				return tipoRemessa.getModeloAIT();	
		}
		return null;
	}
}
