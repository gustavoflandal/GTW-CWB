/**
 * estado.js — store minimo (objeto + assinantes).
 * Mantem o JSON do painel/widget em edicao.
 */

const assinantes = [];
let dados = {};

export const estado = {
    get() {
        return dados;
    },

    set(novosDados) {
        dados = Object.assign({}, dados, novosDados);
        assinantes.forEach(fn => fn(dados));
    },

    assinar(fn) {
        assinantes.push(fn);
        return () => {
            const idx = assinantes.indexOf(fn);
            if (idx >= 0) assinantes.splice(idx, 1);
        };
    },

    limpar() {
        dados = {};
        assinantes.forEach(fn => fn(dados));
    }
};
