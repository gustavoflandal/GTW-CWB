/**
 * Empresa: Consilux Tecnologia
 * Autor: Ederson Luiz Silva
 * Data: 24/01/2012
 * 
 * Descricao: Classe javascript para o auxilio nos envios
 * de requisições ajax e recebimento das informações
 */

function CsxRemoteObject(){
	this.UrlProvider = "/ajax/";
	this.Call = function(params, CallbackFunction){
			$.ajax({url: this.UrlProvider, data: params, success: function(xml) {
				CallbackFunction(xml);
			},async: false});
	}
};