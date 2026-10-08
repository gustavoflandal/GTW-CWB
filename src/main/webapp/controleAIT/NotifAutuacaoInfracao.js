/*remoteObject = new CsxRemoteObject();
remoteObject.UrlProvider = '/ajax/NfAutuacaoInfracao';
aguarde = new Aguarde();

GetNotificacaoAITResult = function(xml){
	o("divTable").innerHTML = $("HTML", xml).text();
	aguarde.Hide();
}

function GetNotificacaoAIT(nrAuto, stNotificacao){
	aguarde.Show();
	p = {
			Action:'GetNotificacaoAIT', 
			nrAuto: nrAuto,
			stNotificacao: stNotificacao
	}
	remoteObject.Call(p, GetNotificacaoAITResult);
}

function btnPesquisar_OnClick(){
	GetNotificacaoAIT(o('txt_auto').value, $('#status_notif').val());
}
*/
window.open(
		'rodando.jsf',
		'Rodando',
		'toolbar=no,location=no,status=no,menubar=no,scrollbars=no,resizable=yes,width=600,height=480'
);