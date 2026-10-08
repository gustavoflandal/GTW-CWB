package com.consilux.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.consilux.model.beans.ExportaComprovacaoImagemBean;

public class ExportaComprovacaoImagemIterator extends GenericSqlIterator<ExportaComprovacaoImagemBean> {

	public ExportaComprovacaoImagemIterator(ResultSet rs, Connection conn, boolean autoCloseConnection)
			throws IllegalStateException {
		super(rs, conn, autoCloseConnection);
	}

	@Override
	protected ExportaComprovacaoImagemBean montarElemento() throws SQLException {
		
//		serie_equipamento,
//		cod_pista,
//		cod_pista_prodam,
//		data,
//		metrologica,
//		qualificador,
//		id_imagem_obj,
//		img_obj,	   (BLOB)
//		id_imagem_pan, (Pode vir nulo)
//		img_pan        (BLOB) (Pode vir nulo)
		
		Integer idImagemPan = rs.getInt("id_imagem_pan");
		if (rs.wasNull())
			idImagemPan = null;
		
		byte[] blobImagemPan = rs.getBytes("img_pan");
		if (rs.wasNull())
			blobImagemPan = null;		
		
		ExportaComprovacaoImagemBean bean = new ExportaComprovacaoImagemBean(
				rs.getInt("serie_equipamento"),
				rs.getInt("cod_pista"),
				rs.getInt("cod_pista_prodam"),
				new Date(rs.getTimestamp("data").getTime()),
				rs.getBoolean("metrologica"),
				rs.getString("qualificador"),
				rs.getInt("id_imagem_obj"),
				rs.getBytes("img_obj"),
				idImagemPan,
				blobImagemPan
			);
		
		return bean;
	}

}
