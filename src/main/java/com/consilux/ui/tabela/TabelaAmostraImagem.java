package com.consilux.ui.tabela;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.time.DateUtils;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.AmostraImagem;
import com.consilux.model.beans.AmostraVeiculoBean;
import com.consilux.model.exception.ModelException;

/**
 * Classe que representa uma tabela (linhas = pistas, colunas = dias)
 * de amostra imagensa. 
 * @author raoni
 */
public class TabelaAmostraImagem extends LinkedHashMap<PistaAmostraImagem, List<AmostraVeiculoBean>> {

	// Atenção: esta classe é um map que relaciona as linhas (pistas) com as colunas
	// (dias). Foi escolhido LinkedHashMap porque ele mantém a ordem de inserção.
	
	private static final long serialVersionUID = 8501813652950402046L;

	/**
	 * Constrói uma tabela de amostra imagens, baseado em um período.
	 * @param amostraImagemIterator
	 * @throws ModelException caso ocorram erros com o iterator.
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public TabelaAmostraImagem(Calendar diaInicio, Calendar diaFim)
	throws ModelException, SQLException, ConexaoException
	{

		if (diaInicio.after(diaFim))
			throw new ModelException("Argumentos inválidos: diaInicio não pode ser posterior a diaFim");

		Connection conn = null;
		
		try {
			// Vamos pegar uma conexão, e vamos utilizar a mesma em todas as chamadas ao banco aqui dentro.
			// Métodos que recebam esta conexão NÃO devem fechar ela. É responsabilidade deste método de
			// fechar esta conexão, pois foi aqui que ela foi aberta.
			conn = Conexao.getConexao();
			
			// Busca as pistas para as quais precisamos de amostras.
			List<PistaAmostraImagem> listaPistas = PistaAmostraImagem.buscarPistasAmostraImagem(conn, diaInicio, diaFim);
			
			// Itera em todas as pistas
			for (final PistaAmostraImagem currPista : listaPistas)
			{
				// Busca as amostras que estão no banco de dados (ATENÇÃO: é uma fila).
				Queue<AmostraVeiculoBean> amostrasNoBanco = AmostraImagem.buscaAmostrasPeriodo(conn, diaInicio, diaFim, currPista);
				
				// Prepara uma lista para armazenar as amostras (desta pista).
				List<AmostraVeiculoBean> listaAmostras = new ArrayList<AmostraVeiculoBean>();
				
				// Trunca as datas (permanecendo somente ano,mes e dia)
				Calendar currDia = DateUtils.truncate(diaInicio, Calendar.DATE);
				Calendar maxDia = DateUtils.truncate(diaFim, Calendar.DATE);
				
				AmostraVeiculoBean currAmostra;
				@SuppressWarnings("unused")
				boolean pistaAtiva;
				
				while (!currDia.after(maxDia))
				{
					// Tenta pegar uma amostra da fila que veio do banco (obs: feito com peek) 
					currAmostra = amostrasNoBanco.peek();
					
					if (currAmostra != null ) {
						// Se conseguiu, testa para ver se a amostra é do dia correto
						if (currDia.equals(DateUtils.truncate(currAmostra.getData(), Calendar.DATE)))
						{
							// Se o dia está correto, remove a amostra da fila.
							currAmostra = amostrasNoBanco.remove();
						} else {
							// Senão, cria uma amostra dummy.
							currAmostra = criarAmostraDummy(currDia, currPista);
						}
						
					} else {
						// Se não conseguiu obter uma amostra da fila (ou seja: fila está vazia), cria uma amostra "Dummy"
						currAmostra = criarAmostraDummy(currDia, currPista);
					}

					// Verifica se a pista já estava ativa neste dia (e depois ajusta o bean).
					pistaAtiva = currPista.getDataInicio().before(currDia);
					
					// verifica se é o primeiro dia de inicio de operação
					if(!currPista.getDataInicio().before(currDia) && !currPista.getDataInicio().after(currDia)){
						pistaAtiva = true;
					}
					
					// Adiciona a amostra corrente na lista de amostras desta pista. 
					listaAmostras.add(currAmostra);
					
					// E incrementa um dia no laço
					currDia.add(Calendar.DAY_OF_MONTH, 1);
					
					// Trunca a parte "TIME" (ou seja: permanece apenas ano,mes,dia) 
					currDia = DateUtils.truncate(currDia, Calendar.DATE);
				}
				
				// Adiciona a pista corrente com suas amostras.
				super.put(currPista, listaAmostras);
			}
		}
		finally
		{
			if (conn != null)
				conn.close();
		}
	}
	
	private static AmostraVeiculoBean criarAmostraDummy(Calendar dia, PistaAmostraImagem pista)
	{
		// Clonado o dia, para não utilizar o mesmo objeto que veio como parâmetro
		Calendar diaClone = Calendar.getInstance();
		diaClone.setTime(dia.getTime());
		
		return new AmostraVeiculoBean(diaClone, pista.getIdLocal(),
			pista.getPista().byteValue(), pista.isMetrologica(), false, false,
			pista.getSerieEquipamento(), pista.getNomePista(),
			pista.getCodPistaAlternativo(), pista.getCodPistaProdam(),
			pista.getCodPista(), true);
	}
	
	public static AmostraVeiculoBean criarAmostraDummy(Calendar dia, Integer idLocal, Integer idPista, boolean metrologica)
	{
		return new AmostraVeiculoBean(dia, idLocal, idPista.byteValue(), metrologica, false, false,
			0, "", 0, 0, 0, true);
	}

}
