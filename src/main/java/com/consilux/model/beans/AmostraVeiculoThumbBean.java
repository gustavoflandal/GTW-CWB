package com.consilux.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.AmostraVeiculoThumbGwtBean;

/**
 * Bean que representa uma amostra de imagem para um veículo.
 * O diferencial desta classe é que ela possui um id de uma imagem,
 * para a geração de um thumbnail. Geralmente o id da thumbnail
 * refere-se a primeira objetiva, mas não necessáriamente seja ela.  
 * @author raoni
 */
public class AmostraVeiculoThumbBean extends AmostraVeiculoBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private int idThumbnail;
	
	public int getIdThumbnail() {
		return idThumbnail;
	}

	public final void setIdThumbnail(int idThumbnail) {
		this.idThumbnail = idThumbnail;
	}

	/**
	 * Converte este bean (do GTW) para um bean do GWT (que é mais reduzido). 
	 * @return um bean do GWT.
	 */
	public AmostraVeiculoThumbGwtBean toGwtBean() {
		
		AmostraVeiculoThumbGwtBean ret = new AmostraVeiculoThumbGwtBean();
		
		ret.setData(this.getData().getTime());
		ret.setIdEquipamento(this.getIdLocal());
		ret.setPista(this.getIdPista());
		ret.setCodPistaProdam(this.getCodPistaProdam());
		ret.setMetrologica(this.isMetrologica());
		ret.setPontuacao(this.getScore());
		ret.setId(this.getIdVeiculo() != null ? this.getIdVeiculo().toString() : null);
		ret.setEscolhidaManualmente(this.isEscolhidaManualmente());
		ret.setIdThumbnail(this.getIdThumbnail());
		ret.setAplicavel(this.isAplicavel());
		return ret;
	}	
	
	/**
	 * Converte uma coleção de beans do GTW para uma lista de beans do GWT.
	 * @param colecaoOriginal
	 * @return
	 * @throws ModelException caso a colecaoOriginal seja nula.
	 */
	public static List<AmostraVeiculoThumbGwtBean> toGwtBeansThumb(Iterable<AmostraVeiculoThumbBean> colecaoOriginal)
		throws ModelException
	{
		if (colecaoOriginal == null)
			throw new ModelException("Argumento nulo: colecaoOriginal");
		
		List<AmostraVeiculoThumbGwtBean> lRet = new ArrayList<AmostraVeiculoThumbGwtBean>();
		
		for (AmostraVeiculoThumbBean beanOriginal : colecaoOriginal) {
			lRet.add(beanOriginal.toGwtBean());
		}
		return lRet;
	}
	
}
