/**********************************************************************************

  Projeto: GTW
  Nome do Módulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 17/10/2008

  Descrição: {descr}

  Histórico:

    $Log: DisparadorEventos.java,v $
    Revision 1.6  2009/05/08 18:49:22  fos
    Colocado sleep na thread para previdir 100% do CPU enquando aguarda.

    Revision 1.5  2009/04/28 11:51:10  fernando
    - refatoração
    - alterado a execução do trabalho. (Pode ser repetitivo ou apenas uma vez)

    Revision 1.4  2009/01/12 12:49:49  fos
    Recuperação de repositório.

    Revision 1.2  2008/11/11 16:23:32  fos
    O construtor da classe, não precisa ser visível por outras classes.

    Revision 1.1  2008/10/23 19:27:57  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.infra;

import org.apache.log4j.Logger;


/**
 *
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.6 $ $Date: 2009/05/08 18:49:22 $ $Author: fos $
 */
public abstract class DisparadorEventos implements Runnable {
	private static Logger logger = Logger.getLogger(DisparadorEventos.class); 
	private Thread threadDisparo;
	private Boolean repetirExecucao;
	private Boolean executando = false;
	
	
	protected DisparadorEventos(){
		
		this( true );
		
	}
	
	protected DisparadorEventos( Boolean repetirExecucao ) {
		
		this.repetirExecucao = repetirExecucao;
		
		threadDisparo = new Thread(this, "Disparo Eventos");
		threadDisparo.start();
		
		while (!executando) {
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				logger.warn("Thread interrompida.", e);
			}
		}
		
	}
	
	protected abstract void executar();
	
	public final void disparar() {
		synchronized (threadDisparo) {

			threadDisparo.notify();
		}
	}
	public Boolean aguardar() {
		Boolean bRet = false;
		try {
			synchronized (this) {
				wait();
			}
			bRet = true;
		}
		catch (InterruptedException e) {
			logger.warn("Thread interrompida.", e);
		}
		return bRet;
	}
	public final void run() {
		try {
			{
				synchronized (threadDisparo) {
					executando = true;
					threadDisparo.wait();
					executar();
				}
				synchronized (this) {
					notifyAll();
				}
			} while ( repetirExecucao );
			
		}
		catch (Exception e) {
			logger.warn("Thread interrompida.", e);
		}
	}
}
