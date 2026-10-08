function obterLocalizacaoDoUsuario() {
    return new Promise((resolve, reject) => {
        const cookies = document.cookie.split(';');
        let isWebView = false;
        let latCookie = null;
        let lonCookie = null;
        
        for (let cookie of cookies) {
            const [name, value] = cookie.trim().split('=');
            if (name === 'isMobileApp' && value === 'true') {
                isWebView = true;
            }
            if (name === 'latitude') {
                latCookie = parseFloat(value);
            }
            if (name === 'longitude') {
                lonCookie = parseFloat(value);
            }
        }
        
        if (isWebView && latCookie && lonCookie) {
            resolve({
                latitude: latCookie,
                longitude: lonCookie,
                fonte: 'webview_cookie'
            });
            return;
        }
        
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    resolve({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude,
                        fonte: 'navigator_geolocation'
                    });
                },
                (error) => {
                    obterLocalizacaoPorIP()
                        .then(resolve)
                        .catch(reject);
                },
                {
                    enableHighAccuracy: true,
                    timeout: 10000,
                    maximumAge: 0
                }
            );
        } else {
            obterLocalizacaoPorIP()
                .then(resolve)
                .catch(reject);
        }
    });
}

function obterLocalizacaoPorIP() {
    return new Promise((resolve, reject) => {
        fetch('https://ipapi.co/json/')
            .then(response => response.json())
            .then(data => {
                if (data.latitude && data.longitude) {
                    resolve({
                        latitude: data.latitude,
                        longitude: data.longitude,
                        fonte: 'ip_api'
                    });
                } else {
                    reject(new Error('Localização não disponível'));
                }
            })
            .catch(error => {
                reject(new Error('Erro ao obter localização por IP'));
            });
    });
}
