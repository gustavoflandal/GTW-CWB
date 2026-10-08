package com.consilux.ui.tabela;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.consilux.infra.RelatorioItr;
import com.consilux.model.ImagensEmProcessamento;
import com.consilux.model.exception.ModelException;

/**
 * Classe que representa uma tabela (linahs = pistas, colunas = dias)
 * de amostra imagensa. 
 * @author raoni
 */

public class TabelaImagensEmProcessamento extends ArrayList<LinhaImagensEmProcessamento> {

	// Atenção: esta classe é um map que relaciona as linhas (pistas) com as colunas
	// (dias). Foi escolhido LinkedHashMap porque ele mantém a ordem de inserção.
	private static final long serialVersionUID = 8501813652950402046L;
	private List<Integer> enquadramentos = new ArrayList<Integer>();
	
	/**
	 * Constrói uma lista de .
	 * @param imagensEmProcessamento
	 * @throws ModelException caso ocorram erros com o iterator.
	 */
	public TabelaImagensEmProcessamento(RelatorioItr<ImagensEmProcessamento> itr) throws ModelException {
		if (itr == null)
			throw new ModelException("Argumento nulo: itr");
		
		int idProcessoAnt = 0;
		Date dataAnt = new Date(0);
		boolean esperaAnt = false;
		LinhaImagensEmProcessamento data = null;
		
		for (ImagensEmProcessamento i : itr) {
			if (idProcessoAnt != i.getIdProcesso() ||
				!dataAnt.equals(i.getData()) || esperaAnt != i.getEspera()) {
				
				data = new LinhaImagensEmProcessamento(
						i.getIdProcesso(),
						i.getNomeProcesso(),
						i.getData(),
						i.getEspera()
						);
				super.add(data);
				idProcessoAnt = i.getIdProcesso();
				dataAnt = i.getData();
				esperaAnt = i.getEspera();
			}
			//Criando sublista de enquadramentos encontrados...
			if (!enquadramentos.contains(i.getIdEnquadramento()))
				enquadramentos.add(i.getIdEnquadramento());
			
			data.addTotalEnquadramento(i.getIdEnquadramento(), i.getTotal());
		}
	}

	/**
	 * Retorna o valor do campo 'enquadramentos' atual.
	 * @return the enquadramentos
	 */
	public List<Integer> getEnquadramentos() {
		Collections.sort(this.enquadramentos);
		return this.enquadramentos;
	}
}

