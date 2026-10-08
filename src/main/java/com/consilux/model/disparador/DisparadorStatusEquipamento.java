/**********************************************************************************

  Projeto: GTW
  Nome do Módulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 17/10/2008

  Descrição: {descr}

  Histórico:

    $Log: DisparadorStatusEquipamento.java,v $
    Revision 1.4  2009/04/28 11:48:49  fernando
    - refatoração

    Revision 1.3  2009/01/12 12:49:49  fos
    Recuperação de repositório.

    Revision 1.1  2008/10/23 19:27:57  fos
    Primeira versão postada no CVS.


 *********************************************************************************/
package com.consilux.model.disparador;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.consilux.model.LocalStatus;
import com.consilux.model.LocalStatus.StatusConexao;
import com.consilux.model.LocalStatus.StatusDIV;
import com.consilux.model.LocalStatus.StatusEnergia;

/**
 *
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.4 $ $Date: 2009/04/28 11:48:49 $ $Author: fernando $
 */
public class DisparadorStatusEquipamento {

	private static Logger logger = Logger.getLogger(DisparadorStatusEquipamento.class); 
	private static final long TEMPO_MAXIMO = 15000;
	private static DisparadorStatusEquipamento instancia = null;
	private Integer idEquipamento;
	private StatusConexao statusConexao;
	private StatusEnergia statusEnergia;
	private StatusDIV statusDIV;
	private long atualizado = new Date().getTime();


	/* (non-Javadoc)
	 * @see com.consilux.infra.DisparadorEventos#executar()
	 */
	public synchronized void atualizaLocal( int idEquipamento )  {
		try {

			this.idEquipamento = idEquipamento;

			Map<String, Object> mFiltros = new HashMap<String, Object>();
			mFiltros.put("serie_equipamento", idEquipamento );
			List<LocalStatus> localStatus = LocalStatus.buscaLocalStatusPor(mFiltros);

			if ( localStatus.size() > 0 ){

				this.statusConexao = localStatus.get(0).getStatusConexao();
				this.statusDIV = localStatus.get(0).getStatusDIV();
				this.statusEnergia = localStatus.get(0).getStatusEnergia();

				notificar();
			}

		}
		catch (Exception e) {
			logger.error("Exceção externa WS.", e);
		}
	}

	private synchronized void notificar(){

		this.atualizado = new Date().getTime();
		notifyAll();

	};

	public synchronized boolean aguardar(){

		try {

			wait( TEMPO_MAXIMO );

			return (atualizado + TEMPO_MAXIMO) > new Date().getTime();

		} catch (InterruptedException e) {
			logger.warn("Tempo máximo excedido.", e);
			return false;
		}

	};

	public static DisparadorStatusEquipamento getInstance() {

		if (instancia == null) {
			instancia =  new DisparadorStatusEquipamento();
		}
		return instancia;
	}

	/**
	 * @return Retorna o valor de statusConexao atual.
	 */
	public StatusConexao getStatusConexao() {
		return statusConexao;
	}

	/**
	 * @return Retorna o valor de idEquipamento atual.
	 */
	public Integer getIdEquipamento() {
		return idEquipamento;
	}

	/**
	 * @return Retorna o valor de statusEnergia atual.
	 */
	public StatusEnergia getStatusEnergia() {
		return statusEnergia;
	}

	/**
	 * @return Retorna o valor de statusDIV atual.
	 */
	public StatusDIV getStatusDIV() {
		return statusDIV;
	}

}
