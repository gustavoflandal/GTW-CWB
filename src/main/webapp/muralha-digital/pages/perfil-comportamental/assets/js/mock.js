// js/mock.js

class MockDataService {
    constructor() {
        this.latencia = 100;
        // Altere esta placa para a que você quer testar
        this.placaValida = 'SEU7J11'; 
    }

    _deveRetornarDados(placa) {
        return placa && placa.toUpperCase() === this.placaValida;
    }

    fetchInformacoesVeiculo(placa) {
        return new Promise(resolve => setTimeout(() => {
            if (this._deveRetornarDados(placa)) {
                resolve({
                    placa: this.placaValida, marca: "RENAULT", modelo: "SANDERO",
                    cor: "PRATA", ano: "2018"
                });
            } else { resolve(null); }
        }, this.latencia));
    }

    fetchTempoPermanencia(placa) {
        return new Promise(resolve => setTimeout(() => {
            if (this._deveRetornarDados(placa)) { resolve({ tempo: "00:45" }); } 
            else { resolve(null); }
        }, this.latencia));
    }

    fetchPassagensDiaSemana(placa) {
        return new Promise(resolve => setTimeout(() => {
            if (this._deveRetornarDados(placa)) {
                resolve([
                    { dia: "Dom", total: 18 }, 
                    { dia: "Seg", total: 25 }, 
                    { dia: "Ter", total: 42 },
                    { dia: "Qua", total: 48 }, 
                    { dia: "Qui", total: 35 }, 
                    { dia: "Sex", total: 65 },
                    { dia: "Sáb", total: 30 }
                ]);
            } else { resolve([]); }
        }, this.latencia));
    }

    fetchDiasMaiorCirculacao(placa) {
        return new Promise(resolve => setTimeout(() => {
            if (this._deveRetornarDados(placa)) {
                resolve([
                    { dia: "Dom", total: 22 },
                    { dia: "Seg", total: 28 },
                    { dia: "Ter", total: 45 },
                    { dia: "Qua", total: 50 }, 
                    { dia: "Qui", total: 38 },
                    { dia: "Sex", total: 68 }, 
                    { dia: "Sáb", total: 32 },
                    
                ]);
            } else { resolve([]); }
        }, this.latencia));
    }

    fetchPassagensPCL(placa) {
        return new Promise(resolve => setTimeout(() => {
            if (this._deveRetornarDados(placa)) {
                resolve([
                    { local: "PCL Centro", total: 125 }, 
                    { local: "PCL Norte Shopping", total: 98 },
                    { local: "PCL Sul", total: 85 }, 
                    { local: "PCL Oeste", total: 60 },
                    { local: "PCL Leste", total: 45 },
                    { local: "PCL Aeroporto", total: 76 }
                ]);
            } else { resolve([]); }
        }, this.latencia));
    }

    fetchPontosMapaCalor(placa) {
        return new Promise(resolve => setTimeout(() => {
            if (this._deveRetornarDados(placa)) {
                resolve([
                    { lat: -25.429, lng: -49.271, weight: 5 }, { lat: -25.430, lng: -49.272, weight: 3 }
                ]);
            } else { resolve([]); }
        }, this.latencia));
    }

    fetchProbabilidadePresenca(placa) {
        return new Promise(resolve => setTimeout(() => {
            if (this._deveRetornarDados(placa)) {
                resolve([
                    { dia: 'Dom', valores: [0.1, 0.1, 0.1, 0.1, 0.1, 0.2, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.6, 0.5, 0.4, 0.3, 0.2, 0.2, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1] },
                    { dia: 'Seg', valores: [0.1, 0.1, 0.1, 0.2, 0.3, 0.5, 0.8, 0.9, 1.0, 0.8, 0.6, 0.4, 0.4, 0.5, 0.6, 0.8, 0.9, 1.0, 0.8, 0.5, 0.3, 0.2, 0.1, 0.1] },
                    { dia: 'Ter', valores: [0.1, 0.1, 0.1, 0.2, 0.3, 0.5, 0.8, 0.9, 0.9, 0.8, 0.6, 0.4, 0.4, 0.5, 0.6, 0.8, 0.9, 1.0, 0.8, 0.5, 0.3, 0.2, 0.1, 0.1] },
                    { dia: 'Qua', valores: [0.1, 0.1, 0.1, 0.2, 0.4, 0.6, 0.8, 1.0, 1.0, 0.8, 0.6, 0.4, 0.4, 0.5, 0.6, 0.8, 0.9, 1.0, 0.8, 0.5, 0.3, 0.2, 0.1, 0.1] },
                    { dia: 'Qui', valores: [0.1, 0.1, 0.1, 0.2, 0.3, 0.5, 0.8, 0.9, 0.9, 0.8, 0.6, 0.4, 0.4, 0.5, 0.6, 0.8, 0.9, 0.9, 0.8, 0.5, 0.3, 0.2, 0.1, 0.1] },
                    { dia: 'Sex', valores: [0.1, 0.1, 0.1, 0.2, 0.3, 0.5, 0.8, 0.9, 1.0, 0.8, 0.6, 0.5, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 0.9, 0.8, 0.7, 0.6, 0.4, 0.2] },
                    { dia: 'Sáb', valores: [0.1, 0.1, 0.1, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 0.9, 0.8, 0.7, 0.6, 0.7, 0.8, 0.9, 1.0, 0.9, 0.7, 0.5, 0.3] }
                ]);
            } else { 
                resolve([]); 
            }
        }, this.latencia));
    }
}

// Cria a instância que será usada pelo script principal
const apiClient = new MockDataService();