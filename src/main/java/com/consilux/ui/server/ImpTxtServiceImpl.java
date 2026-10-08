package com.consilux.ui.server;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.servlet.ferramentas.ImpTxt;
import com.consilux.ui.client.ImpTxtService;
import com.consilux.ui.client.beans.ImpTxtFileGwtBean;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.google.common.collect.Lists;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ImpTxtServiceImpl extends RemoteServiceServlet implements
		ImpTxtService {

	private static final long serialVersionUID = -1425986586606114378L;
	private static String[] UNIDADE = {" Bytes"," KB"," MB"," GB"}; 

	@Override
	public List<ImpTxtFileGwtBean> getImpTxtFiles(PagingLoadConfig config) {
		Configuracao conf = null;
		
		while (ImpTxt.isReceivingFile.get());
		
		List<ImpTxtFileGwtBean> arquivos = new ArrayList<ImpTxtFileGwtBean>();
		
		conf = ConfiguracaoProvider.getInstance();
		String dirPath = conf.getImpTxtDir();
		File impTxtDir = new File(dirPath);
		File[] files = impTxtDir.listFiles();
		ImpTxtFileGwtBean bean = null;
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.S");
		Date d = new Date();
		long tamanho = 0;
		String unidade = null;
		for (File file: files) {
			bean = new ImpTxtFileGwtBean();
			bean.setNome(file.getName());
			d.setTime(file.lastModified());
			bean.setData(fmt.format(d));
			bean.set("date_time",d.getTime());
			tamanho = file.length();
			bean.set("size",Long.valueOf(tamanho));
			int i = 0;
			while (tamanho > 1024){
				tamanho = tamanho/1024;
				i++;
			}
			unidade = UNIDADE[i];
			
			bean.setTamanho(String.valueOf(tamanho)+unidade);
			arquivos.add(bean);
		}
		
		String field = config.getSortField();
		if (field != null && !"".equals(field)){
			
			final String direction = config.getSortDir().name();
			final String sortField;
			final Class<?> clazz;
			if ("tamanho".equals(field)){
				sortField = "size";
				clazz = Long.class;
			} else if ("data".equals(field)){
				sortField = "date_time";
				clazz = Long.class;
			} else {
				sortField = "nome";
				clazz = String.class;
			}
			
			Collections.sort(arquivos, new Comparator<ImpTxtFileGwtBean>() {
				@Override
				public int compare(ImpTxtFileGwtBean o1, ImpTxtFileGwtBean o2) {
					int result = 0;
					Object value1 = o1.get(sortField);
					Object value2 = o2.get(sortField);
					Method m;
					try {
						m = clazz.getMethod("compareTo", clazz);
					
						if (direction.equals("ASC")){
							result = (Integer)m.invoke(value1, value2);
						} else {
							result = (Integer)m.invoke(value2, value1);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					return result;
				}
			});
		}
		
		int limit  = config.getLimit(),
			offset = config.getOffset();
		List<ImpTxtFileGwtBean> sublist = 
			arquivos.subList(offset*limit, Math.min((offset*limit) + limit, arquivos.size()) -1);
		arquivos = Lists.newArrayList(sublist);
		int size;
		if ((size = arquivos.size()) > 0){
			arquivos.get(0).set("total",size);
		}
		return arquivos;
	}

}
