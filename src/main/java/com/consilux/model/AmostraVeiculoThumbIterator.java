package com.consilux.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.consilux.model.beans.AmostraVeiculoThumbBean;

public class AmostraVeiculoThumbIterator extends GenericSqlIterator<AmostraVeiculoThumbBean> {

	public AmostraVeiculoThumbIterator(ResultSet rs, Connection conn, boolean autoCloseConnection) throws IllegalStateException {
		super(rs, conn, autoCloseConnection);
	}

	@Override
	protected AmostraVeiculoThumbBean montarElemento() throws SQLException, InstantiationException, IllegalAccessException {
		
		AmostraVeiculoThumbBean delegateBean = AmostraVeiculoIterator.parseCore(AmostraVeiculoThumbBean.class, rs);
		delegateBean.setIdThumbnail(rs.getInt("id_thumbnail"));
		
		return delegateBean;
	}	
}
