package com.consilux.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import com.consilux.model.beans.AmostraVeiculoBean;

public class AmostraVeiculoIterator extends GenericSqlIterator<AmostraVeiculoBean>
{

	public AmostraVeiculoIterator(ResultSet rs, Connection conn, boolean autoCloseConnection) throws IllegalStateException {
		super(rs, conn, autoCloseConnection);
	}

	public final static <T extends AmostraVeiculoBean> T parseCore(Class<T> clazz, ResultSet resultSet)
		throws SQLException, InstantiationException, IllegalAccessException {

		int tmpInt;
		long tmpLong;
		String tmpString;
		
		T bean = clazz.newInstance();
		
		Calendar data = Calendar.getInstance();
		data.setTime(resultSet.getDate("data"));
		bean.setData(data);
		bean.setIdLocal(resultSet.getInt("id_local"));
		bean.setIdPista(resultSet.getByte("id_pista"));
		bean.setFixada(resultSet.getBoolean("fixada"));
		bean.setMetrologica(resultSet.getBoolean("metrologica"));
		bean.setEscolhidaManualmente(resultSet.getBoolean("manual"));

		bean.setSerieEquipamento(resultSet.getInt("serie_equipamento"));
		bean.setNomeLocal(resultSet.getString("nome_pista").trim());
		bean.setCodPistaAlternativo(resultSet.getInt("cod_pista_alternativo"));
		bean.setCodPistaProdam(resultSet.getInt("cod_pista_prodam"));		
		bean.setCodPista(resultSet.getInt("cod_pista"));
		bean.setAplicavel(resultSet.getBoolean("aplicavel"));
		
		// Tratamento especial para colunas que podem vir nulas do SQL
		tmpString = resultSet.getString("tipo");
		bean.setTipo(resultSet.wasNull() ? null : tmpString);
		
		tmpLong = resultSet.getLong("id_veiculo");
		bean.setIdVeiculo(resultSet.wasNull() ? null : tmpLong);
		
		tmpInt = resultSet.getInt("id_infracao");
		bean.setIdInfracao(resultSet.wasNull() ? null : tmpInt);
		
		tmpInt = resultSet.getInt("score_total");
		bean.setScore(resultSet.wasNull() ? null : tmpInt);
		
		bean.setPistaAtiva(true);
		
		return bean;
	}	
	
	@Override
	protected AmostraVeiculoBean montarElemento() throws SQLException, InstantiationException, IllegalAccessException {
		return parseCore(AmostraVeiculoBean.class, rs);
	}
}