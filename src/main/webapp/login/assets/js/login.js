const GOOGLE_CLIENT_ID = '259896241090-3qh2c9u7d9tteku7pqf1h5l399ribi5p.apps.googleusercontent.com';
const REDIRECT_URI = location.origin + '/callback';
const SCOPE = 'openid email profile';
const RESPONSE_TYPE = 'code';

function setCookie(name, value, days) {
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}

function logar_com_google(destino)
{
	const state = Math.random().toString(36).substring(2);
	setCookie('oauth_state', state, 1); // Store state in cookie for 1 day
	let url = 'https://accounts.google.com/o/oauth2/v2/auth?client_id=' + encodeURIComponent(GOOGLE_CLIENT_ID) + '&redirect_uri=' + encodeURIComponent(REDIRECT_URI) + '&response_type=' + RESPONSE_TYPE + '&scope=' + encodeURIComponent(SCOPE) + '&access_type=online&prompt=select_account&state='+ state;
	if(destino === 'novo_acesso') {
		setCookie('oauth_destino', 'novo_acesso', 1);
	}
	window.location.href = url;
}
