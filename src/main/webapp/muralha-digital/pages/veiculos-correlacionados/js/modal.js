/**
 * Função para mostrar um toast personalizado
 * @param {string} mensagem - Mensagem a ser exibida no toast.
 * @param {'sucesso' | 'erro' | 'alerta'} tipo - Tipo do toast, define a cor.
 * @param {number} duracao - Duração do toast em milissegundos.
 */
async function mostrarToastAsync(mensagem, tipo = 'sucesso', duracao = 5000) {
    const toast = document.createElement('div');
    toast.className = `toast-custom toast-${tipo}`;

    toast.innerHTML = `
        <div class="toast-close" title="Fechar">&times;</div>
        <div class="toast-body">${mensagem}</div>
        <div class="toast-progress"></div>
    `;

    // Aplica a duração dinamicamente na barra
    const progresso = toast.querySelector('.toast-progress');
    progresso.style.animation = `progressBar ${duracao}ms linear forwards`;

    // Insere o toast no container
    document.getElementById('toast-container').appendChild(toast);

    // Remove após o tempo
    const timeoutId = setTimeout(() => toast.remove(), duracao);

    // Fechar manualmente
    toast.querySelector('.toast-close').addEventListener('click', () => {
        clearTimeout(timeoutId);
        toast.remove();
    });
}