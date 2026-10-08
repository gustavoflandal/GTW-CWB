package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio responsável pelas amostras de imagem.
 * @author raoni
 */
public class ExportaAmostras {

	public static ExportaComprovacaoImagemIterator exportaAmostrasPeriodo(Date dataInicial, Date dataFinal)
	throws ModelException, SQLException, ConexaoException
	{
		if (dataInicial == null)
			throw new ModelException("Argumento nulo: dataInicial");

		if (dataFinal == null)
			throw new ModelException("Argumento nulo: dataFinal");

		if (dataInicial.after(dataFinal))
			throw new ModelException("Argumento inválido: data inicial deve ser anterior a data final.");
		
		Connection conn = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		ExportaComprovacaoImagemIterator itr = null;
		
		conn = Conexao.getConexao();
		cs = conn.prepareCall("{call spu_exporta_amostras_periodo(?, ?)}");
		
		cs.setDate(1, new java.sql.Date(dataInicial.getTime()));
		cs.setDate(2, new java.sql.Date(dataFinal.getTime()));
		
		if (cs.execute()) {
			rs = cs.getResultSet();
			itr = new ExportaComprovacaoImagemIterator(rs, conn, true);
		}
		
		return itr;
	}

}
