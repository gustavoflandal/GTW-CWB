/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Thiago Surgik
  Data: 27/09/2016

*********************************************************************************/
package com.consilux.model.relatorio.rj;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * @author Thiago Surgik - Consilux Tecnologia
 * @since 27/09/2016
 */
public  class RelatorioEditalRJ implements Serializable {
	
	private static final long serialVersionUID = 2035188461287315850L;
	private Integer idRelatorio;
	private Integer idTipoRelatorio;
	private String nomeRelatorio;
	private String nomeRelatorioFormatado;
	private Boolean filtroPorLocal;
	
	public enum TipoRelatorioEditalRJ {
		FLUXO_VEICULAR(1),
		TEMPO_PERCURSO(2),
		NONE(0);
	
	    private final int id;
	    
	    TipoRelatorioEditalRJ(int id) {
	    	this.id = id;
	    }
	    
	    public int GetID() {
	    	return id;
	    }
	    
	    public boolean Compare(int i) {
	    	return id == i;
	    }
	    
        public static TipoRelatorioEditalRJ GetValue(int id)
        {
        	TipoRelatorioEditalRJ[] fv = TipoRelatorioEditalRJ.values();
            for(int i = 0; i < fv.length; i++)
            {
                if(fv[i].Compare(id))
                    return fv[i];
            }
            return TipoRelatorioEditalRJ.NONE;
        }
	}
	
	public enum FluxoVeicular {
		RELATORIO_1(1),
		RELATORIO_2(2),
		RELATORIO_3(3),
		RELATORIO_4(4),
		RELATORIO_5(5),
		RELATORIO_6(6),
		RELATORIO_7(7),
		RELATORIO_8(8),
		RELATORIO_9(9),
		RELATORIO_10(10),
		RELATORIO_11(11),
		RELATORIO_12(12),
		RELATORIO_13(13),
		RELATORIO_14(14),
		RELATORIO_15(15),
		RELATORIO_16(16),
		None(0);
	
	    private final int id;
	    
	    FluxoVeicular(int id) {
	    	this.id = id;
	    }
	    
	    public int GetID() {
	    	return id;
	    }
	    
	    public boolean Compare(int i) {
	    	return id == i;
	    }
	    
        public static FluxoVeicular GetValue(int id)
        {
        	FluxoVeicular[] fv = FluxoVeicular.values();
            for(int i = 0; i < fv.length; i++)
            {
                if(fv[i].Compare(id))
                    return fv[i];
            }
            return FluxoVeicular.None;
        }
	}
	
	public enum TempoPercurso {
		RELATORIO_1(1),
		RELATORIO_2(2),
		RELATORIO_3(3),
		RELATORIO_4(4),
		RELATORIO_5(5),
		RELATORIO_6(6),
		RELATORIO_7(7),
		RELATORIO_8(8),
		RELATORIO_9(9),
		RELATORIO_10(10),
		None(0);
	
	    private final int id;
	    
	    TempoPercurso(int id) {
	    	this.id = id;
	    }
	    
	    public int GetID() {
	    	return id;
	    }
	    
	    public boolean Compare(int i) {
	    	return id == i;
	    }
	    
        public static TempoPercurso GetValue(int id)
        {
        	TempoPercurso[] fv = TempoPercurso.values();
            for(int i = 0; i < fv.length; i++)
            {
                if(fv[i].Compare(id))
                    return fv[i];
            }
            return TempoPercurso.None;
        }
	}
	
	public RelatorioEditalRJ(Integer idRelatorio, Integer idTipoRelatorio, String nomeRelatorio, String nomeRelatorioFormatado, Boolean filtroProLocal) {
		super();
		this.idRelatorio = idRelatorio;
		this.idTipoRelatorio = idTipoRelatorio;
		this.nomeRelatorio = nomeRelatorio;
		this.nomeRelatorioFormatado = nomeRelatorioFormatado;
		this.filtroPorLocal = filtroProLocal;
	}

	
	public Integer getIdRelatorio() {
		return idRelatorio;
	}
	public void setIdRelatorio(Integer idRelatorio) {
		this.idRelatorio = idRelatorio;
	}

	
	public Integer getIdTipoRelatorio() {
		return idTipoRelatorio;
	}
	public void setIdTipoRelatorio(Integer idTipoRelatorio) {
		this.idTipoRelatorio = idTipoRelatorio;
	}

	
	public String getNomeRelatorio() {
		return nomeRelatorio;
	}
	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}
	
	
	public String getNomeRelatorioFormatado() {
		return nomeRelatorioFormatado;
	}
	public void setNomeRelatorioFormatado(String nomeRelatorioFormatado) {
		this.nomeRelatorioFormatado = nomeRelatorioFormatado;
	}


	public Boolean getFiltroPorLocal() {
		return filtroPorLocal;
	}
	public void setFiltroPorLocal(Boolean filtroPorLocal) {
		this.filtroPorLocal = filtroPorLocal;
	}
	
	
	public static List<RelatorioEditalRJ> buscaRelatorios(Integer idTipoRelatorio) throws ConexaoException, SQLException {
		
		List<RelatorioEditalRJ> lRet = new ArrayList<RelatorioEditalRJ>();
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append(" SELECT rm.id_relatorio_edital_rj ");
		sbSQL.append("		 ,trm.id_tipo_relatorio_edital_rj ");
		sbSQL.append("		 ,trm.descricao ");
		sbSQL.append("		 ,rm.nome_relatorio ");
		sbSQL.append("		 ,'Relatório ' + LTRIM(RTRIM(RIGHT('00' + CAST(rm.id_relatorio_edital_rj AS VARCHAR(2)), 2))) + ' - ' + rm.nome_relatorio AS nome_relatorio_formatado ");
		sbSQL.append("		 ,rm.filtro_por_local ");
		sbSQL.append(" FROM   relatorio_edital_rj rm (NOLOCK) ");
		sbSQL.append(" 		  INNER JOIN tipo_relatorio_edital_rj trm (NOLOCK) ");
		sbSQL.append(" 			   ON  trm.id_tipo_relatorio_edital_rj = rm.id_tipo_relatorio_edital_rj ");
		sbSQL.append(" WHERE  trm.id_tipo_relatorio_edital_rj = ? ");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
	
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idTipoRelatorio);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				lRet.add(new RelatorioEditalRJ(rs.getInt("id_relatorio_edital_rj"),
										  	  rs.getInt("id_tipo_relatorio_edital_rj"),
										  	  rs.getString("nome_relatorio"),
										  	  rs.getString("nome_relatorio_formatado"),
										  	  rs.getBoolean("filtro_por_local")
										  	 )
						);
			}
			
		} catch (Exception e) {
			throw new SQLException("Erro ao montar SQL.", e);
		}
		
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();							
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}			
		}
		return lRet;
	}
	
}
