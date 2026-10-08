package com.consilux.infra;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UpArq {
	private static final long MAX_TAM_ANEXO = 10 * (1024*1024); //10MB
	private List<FileItem> arqs; 
	private Map<String, String> campos; 
	
	/**
	 * @param httpServletRequest
	 * @throws FileUploadException 
	 */
    public UpArq(HttpServletRequest httpServletRequest) throws FileUploadException {
		DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(4096);
        // the location for saving data that is larger than getSizeThreshold()
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));

        ServletFileUpload upload = new ServletFileUpload(factory);
        // maximum size before a FileUploadException will be thrown
        upload.setSizeMax(MAX_TAM_ANEXO);

        this.arqs = new ArrayList<FileItem>();
        this.campos = new HashMap<String, String>();
		for (FileItem fi: (List<FileItem>)upload.parseRequest(httpServletRequest)) {
			if (fi.isFormField()) 
				this.campos.put(fi.getFieldName(), fi.getString());
			else if (fi.getSize() > 0)
				this.arqs.add(fi);
		}
	}

	/**
	 * @return the arqs
	 */
	public List<FileItem> getArqs() {
		return arqs;
	}

	public Map<String, String> getCampos() {
		return campos;
	}
}
