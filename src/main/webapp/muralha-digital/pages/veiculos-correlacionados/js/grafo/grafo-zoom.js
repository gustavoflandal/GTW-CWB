/**
 * Valores de zoom mínimo e máximo permitidos no grafo.
 * ZOOM_MIN define o nível de zoom mais afastado.
 * ZOOM_MAX define o nível de zoom mais aproximado.
 */
const ZOOM_MIN = 0.5;
const ZOOM_MAX = 1.5;

/**
 * Aumenta o nível de zoom do grafo.
 * O zoom é limitado pelo valor máximo definido em ZOOM_MAX.
 */
async function zoomInAsync() {
    const novoZoom = Math.min(cy.zoom() * 1.2, ZOOM_MAX);
    cy.zoom({
        level: novoZoom,
        renderedPosition: { x: cy.width() / 2, y: cy.height() / 2 },
    });
}

/**
 * Reduz o nível de zoom do grafo.
 * O zoom é limitado pelo valor mínimo definido em ZOOM_MIN.
 */
async function zoomOutAsync() {
    const novoZoom = Math.max(cy.zoom() * 0.8, ZOOM_MIN);
    cy.zoom({
        level: novoZoom,
        renderedPosition: { x: cy.width() / 2, y: cy.height() / 2 },
    });
}

/**
 * Redefine a visualização do grafo para ajustar todos os elementos na tela.
 */
async function resetViewAsync() {
    cy.fit();
}