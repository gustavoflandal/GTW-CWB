package com.consilux.model.medicao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.ExportaComprovacaoImagemIterator;
import com.consilux.model.exception.ModelException;

/**
 * Classe de negócio responsável pelas amostras de imagem.
 * @author raoni
 */
public class ExportaComprovacaoImagem {

	public static ExportaComprovacaoImagemIterator exportaComprovacaoImagem(Integer idProcessoMedicao, Boolean complementar)
	throws ModelException, SQLException, ConexaoException
	{
		if (idProcessoMedicao == null)
			throw new ModelException("Argumento nulo: idProcessoMedicao");

		Connection conn = null;
		CallableStatement cs = null;
		ResultSet rs = null;
		ExportaComprovacaoImagemIterator itr = null;
		
		conn = Conexao.getConexao();
		cs = conn.prepareCall("{call spu_exportar_imagens_comprovacao(?, ?)}");
		
		cs.setInt(1, idProcessoMedicao);
		cs.setBoolean(2, complementar);
		
		if (cs.execute()) {
			rs = cs.getResultSet();
			itr = new ExportaComprovacaoImagemIterator(rs, conn, true);
		}
		
		return itr;
	}

}
