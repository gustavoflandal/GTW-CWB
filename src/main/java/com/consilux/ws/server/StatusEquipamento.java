/**********************************************************************************

  Projeto: GTW
  Nome do Módulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 14/10/2008

  Descrição: {descr}

  Histórico:

    $Log: StatusEquipamento.java,v $
    Revision 1.8  2009/05/21 17:31:48  fos
    Colocado logs nos WS.

    Revision 1.7  2009/04/28 11:48:14  fernando
    - refatoração

    Revision 1.6  2009/04/24 17:39:14  fernando
    - alterando status de equipamento

    Revision 1.5  2009/01/16 14:01:57  fos
    Extende ao LocalWS para controle de conexão sem autenticação.

    Revision 1.4  2009/01/12 12:49:52  fos
    Recuperação de repositório.

    Revision 1.2  2008/10/23 19:31:02  fos
    Agora já chama a classe de negócio que vai avisar os servlets.

    Revision 1.1  2008/10/16 21:15:44  fos
    Primeira versão postada no CVS.


*********************************************************************************/
package com.consilux.ws.server;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.consilux.infra.LocalWS;
import com.consilux.model.disparador.DisparadorStatusEquipamento;

/**
 *
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.8 $ $Date: 2009/05/21 17:31:48 $ $Author: fos $
 */
public class StatusEquipamento extends LocalWS {
	
	public void statusAlterado(Integer idEquipamento) {

		System.out.println("WSCALL statusAlterado: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
	
		DisparadorStatusEquipamento disp = DisparadorStatusEquipamento.getInstance();
		
		disp.atualizaLocal( idEquipamento );
			
		//MonitoramentoMapServiceImpl.notificar();

		System.out.println("END_WSCALL statusAlterado: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
		
	}
}
