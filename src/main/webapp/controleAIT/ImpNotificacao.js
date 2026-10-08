remoteObject = new CsxRemoteObject();
remoteObject.UrlProvider = '/ajax/ImpNotificacao';
aguarde = new Aguarde();

GetRemessaImpressaoResult = function(xml){
	o("divTable").innerHTML = $("HTML", xml).text();
	aguarde.Hide();
}

function GetRemessaImpressao(idProcesso, tpNotificacao){
	aguarde.Show();
	p = {
			Action:'GetRemessaImpressao', 
			idProcesso: idProcesso,
			tpNotificacao: tpNotificacao
	}
	remoteObject.Call(p, GetRemessaImpressaoResult);
}

function btnPesquisar_OnClick(){
	GetRemessaImpressao($('#tipo_remessa').val(), $('input[name=tipo_notif]:checked').val());
}