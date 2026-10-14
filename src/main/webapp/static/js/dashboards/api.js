/**
 * api.js — unico ponto que fala com o backend.
 * Todas as chamadas fetch passam por aqui com token CSRF.
 */
export const api = {

    async get(url) {
        try {
            const resp = await fetch(url, {
                method: 'GET',
                credentials: 'same-origin',
                headers: { 'Accept': 'application/json' }
            });
            return await resp.json();
        } catch (e) {
            return { ok: false, erro: e.message };
        }
    },

    async post(url, body) {
        try {
            const resp = await fetch(url, {
                method: 'POST',
                credentials: 'same-origin',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify(body)
            });
            return await resp.json();
        } catch (e) {
            return { ok: false, erro: e.message };
        }
    }
};
