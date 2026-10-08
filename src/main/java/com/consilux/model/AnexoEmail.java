package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.beans.AnexoEmailBean;

public class AnexoEmail {

	public static List<AnexoEmailBean> buscaAnexos(int idEmail)	throws ConexaoException, SQLException {
		
		Connection conn = null;
		List<AnexoEmailBean> lRet = new ArrayList<AnexoEmailBean>(5);
		
		try {
			
			conn = Conexao.getConexao();
			PreparedStatement psBuscaAnexos = conn.prepareStatement("SELECT content_type, nome_arquivo, bytes_arquivo FROM anexo_email_enviar WHERE id_email_enviar = ?");
			
			// Recupera os anexos.
			psBuscaAnexos.setInt(1, idEmail);
			ResultSet rsAnexos = psBuscaAnexos.executeQuery();
			
			while (rsAnexos.next()) {
				lRet.add(new AnexoEmailBean(
					rsAnexos.getString("content_type"),
					rsAnexos.getString("nome_arquivo"),
					rsAnexos.getBytes("bytes_arquivo")));
			}	
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
		return lRet;
		
	}
	
}
