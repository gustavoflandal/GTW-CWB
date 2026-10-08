package com.consilux.ui.tabela;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.Pista;
import com.consilux.model.exception.ModelException;

/**
 * Classe quer representa uma "linha" na tabela de amostra de imagens.
 * Herda de pista e adiciona mais alguns atributos, específicos para
 * casos de amostra imagem.
 * @author raoni
 */
public class PistaAmostraImagem extends Pista {

	private int serieEquipamento;
	private int codPistaProdam;
	private boolean metrologica;
	private Calendar dataInicio;
	
	public PistaAmostraImagem(int idLocal, int codPista, int codPistaAlternativo,
			String nomePista, int pista, int serieEquipamento, int codPistaProdam,
			boolean metrologica, Calendar dataInicio)
	{
		super(idLocal, codPista, codPistaAlternativo, nomePista, pista);
		this.serieEquipamento = serieEquipamento;
		this.codPistaProdam = codPistaProdam;
		this.metrologica = metrologica;
		this.dataInicio = dataInicio;
	}

	public int getSerieEquipamento() {
		return serieEquipamento;
	}

	public void setSerieEquipamento(int serieEquipamento) {
		this.serieEquipamento = serieEquipamento;
	}

	public int getCodPistaProdam() {
		return codPistaProdam;
	}

	public void setCodPistaProdam(int codPistaProdam) {
		this.codPistaProdam = codPistaProdam;
	}

	public boolean isMetrologica() {
		return metrologica;
	}

	public void setMetrologica(boolean metrologica) {
		this.metrologica = metrologica;
	}

	public Calendar getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Calendar dataInicio) {
		this.dataInicio = dataInicio;
	}

	// Muito cuidado com o equals e o hashCode. Esta classe é utilizada com
	// mapas, então o "contains" pode funcionar (ou não) baseado no que 
	// fizermos aqui nestes 2 métodos.
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (metrologica ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		PistaAmostraImagem other = (PistaAmostraImagem) obj;
		if (metrologica != other.metrologica)
			return false;
		return true;
	}
	
	/**
	 * Busca as pistas que devem possuir amostras de imagem para um dado período.
	 * @param conn uma conexão com o banco  
	 * @param dataInicio data de início do período desejado.
	 * @param dataFim data final do período desejado.
	 * @return
	 * @throws ModelException
	 * @throws SQLException
	 * @throws ConexaoException
	 */
	public static List<PistaAmostraImagem> buscarPistasAmostraImagem(Connection conn, Calendar dataInicio, Calendar dataFim)
	throws ModelException, SQLException
	{
		if (dataInicio == null)
			throw new ModelException("Argumento nulo: dataInicio");

		if (dataFim == null)
			throw new ModelException("Argumento nulo: dataFim");
		
		if (dataInicio.after(dataFim))
			throw new ModelException("Argumentos inválidos: dataInicio deve ser anterior a dataFim");
		
		List<PistaAmostraImagem> lRet = new ArrayList<PistaAmostraImagem>();
		CallableStatement cs = null;
		
		cs = conn.prepareCall("{call spu_busca_pistas_amostra_imagem(?, ?)}");
		cs.setDate(1, new java.sql.Date(dataInicio.getTime().getTime()));
		cs.setDate(2, new java.sql.Date(dataFim.getTime().getTime()));
		
		if (cs.execute())
		{
			ResultSet rs = cs.executeQuery();
			Calendar inicioPista;
			
			while (rs.next())
			{
				inicioPista = Calendar.getInstance();
				inicioPista.setTime(rs.getDate("data_inicio"));
				
				PistaAmostraImagem pst = new PistaAmostraImagem(
						rs.getInt("id_local"),
						rs.getInt("cod_pista"),
							rs.getInt("cod_pista_alternativo"),
							rs.getString("nome_pista"),
							rs.getShort("id_pista"),
							rs.getInt("serie_equipamento"),
							rs.getInt("cod_pista_prodam"),
							rs.getBoolean("metrologica"),
							inicioPista);
					
					lRet.add(pst);
			}
		}		
		// Atenção: não devemos fechar a conexão neste método (pois estamos recebendo ela de fora)
		// Por isso, não temos a necessidade de encapsular este método em try/finally 		
		
		return lRet;
	}
	
}
