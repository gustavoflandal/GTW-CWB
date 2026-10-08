window.linksComBoletim = window.linksComBoletim || [];

function adicionarLinkComBoletim() {
    const urlInput = document.getElementById("urlLinkComBoletim");
    const detalhamentoInput = document.getElementById("detalhamentoLinkComBoletim");

    const url = urlInput.value.trim();
    const detalhamento = detalhamentoInput.value.trim();

    if (!url) {
        alert("Por favor, informe a URL.");
        return;
    }

    try {
        new URL(url); // Validação básica
    } catch (e) {
        alert("URL inválida. Verifique o formato (ex: https://exemplo.com).");
        return;
    }

    window.linksComBoletim.push({
        id: null,        // null porque é novo
        url,
        detalhamento,
        status: "novo"
    });

    atualizarTabelaLinksComBoletim();
    document.getElementById("formLinksComBoletim").reset();
}

function removerLinkComBoletim(index) {
    const link = window.linksComBoletim[index];
    if (!link) return;

    if (link.status === "novo") {
        // Remove da lista imediatamente
        window.linksComBoletim.splice(index, 1);
    } else {
        // Apenas marca como removido
        link.status = "removido";
    }

    atualizarTabelaLinksComBoletim();
}

function atualizarTabelaLinksComBoletim() {
    const tbody = document.getElementById("listLinksComBoletim");
    tbody.innerHTML = "";

    window.linksComBoletim.forEach((link, index) => {
        let urlDisplay = link.url;
        let detalhamentoDisplay = link.detalhamento || "-";

        if (link.status === "removido") {
            urlDisplay = `<del>${urlDisplay}</del>`;
            detalhamentoDisplay = `<del>${detalhamentoDisplay}</del>`;
        } else if (link.status === "novo") {
            urlDisplay += ' <span class="badge bg-success">Novo</span>';
        }

        const btnRemover = link.status === "removido"
            ? ""
            : `<button class="btn btn-sm btn-danger" title="Remover" onclick="removerLinkComBoletim(${index})">
                    <i class="fa fa-trash"></i>
               </button>`;

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${index + 1}</td>
            <td class="text-break text-start"><a href="${link.url}" target="_blank">${urlDisplay}</a></td>
            <td class="text-start">${detalhamentoDisplay}</td>
            <td class="text-center">${btnRemover}</td>
        `;
        tbody.appendChild(tr);
    });
}

function getLinksComBoletim() {
    return window.linksComBoletim;
}

function limparLinksComBoletim() {
    window.linksComBoletim = [];
    atualizarTabelaLinksComBoletim();
    document.getElementById("formLinksComBoletim").reset();
}

function preencherTabLinksComBoletim(xmlString) {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlString, "application/xml");

    window.linksComBoletim = [];

    const links = xmlDoc.querySelectorAll("RegistroDeFato > links > link");

    links.forEach(linkNode => {
        const id = parseInt(linkNode.querySelector("id")?.textContent?.trim()) || null;
        const url = linkNode.querySelector("url")?.textContent?.trim() || "";
        const detalhamento = linkNode.querySelector("detalhamento")?.textContent?.trim() || "";

        if (url) {
            window.linksComBoletim.push({
                id,
                url,
                detalhamento,
                status: "existente"
            });
        }
    });

    atualizarTabelaLinksComBoletim();
}
