<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<script src="assets/js/componente-mapa-radares.js"></script>

<div id="containerMapaLocais" style="display:none;">

    <div class="mb-3">
        <label class="form-label">Modo de seleção</label>

        <div class="form-check">
            <input class="form-check-input" type="radio" name="modoSelecaoMapa"
                   value="poligono" checked>
            <label class="form-check-label">Polígono</label>
        </div>

        <div class="form-check">
            <input class="form-check-input" type="radio" name="modoSelecaoMapa"
                   value="raio">
            <label class="form-check-label">Ponto + Raio</label>
        </div>
    </div>

    <div class="mb-3">
        <label class="form-label">Endereço</label>
        <div class="input-group">
            <input type="text" class="form-control" id="inputEnderecoMapa">
            <button type="button"
                    class="btn btn-outline-secondary"
                    id="btnCentralizarMapa">
                Centralizar mapa
            </button>
        </div>
    </div>

    <input type="hidden" id="raioRadaresMapa">

    <div id="loadingGeocoding" style="display:none; height:500px; align-items:center; justify-content:center;">
        <div class="spinner-border text-primary"></div>
    </div>

    <div id="mapaBlitz" style="height: 500px;"></div>

    <div class="mt-3 text-end">
        <button type="button"
                class="btn btn-success"
                id="btnAplicarLocaisMapa">
            Aplicar seleção
        </button>
    </div>

    <hr>
</div>
