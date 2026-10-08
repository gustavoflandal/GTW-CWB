window.objetosComBoletim = window.objetosComBoletim || [];

function adicionarObjetoComBoletim() {
  const tipo = document.getElementById("tipoObjetoComBoletim").value.trim();
  const descricao = document.getElementById("descricaoObjetoComBoletim").value.trim();

  if (!tipo || !descricao) {
    alert("Preencha todos os campos.");
    return;
  }

  const novoObjeto = {
    tipo,
    descricao,
    status: "novo" // marca como novo
  };

  window.objetosComBoletim.push(novoObjeto);
  atualizaTabelaObjetos();
  document.getElementById("formObjetoComBoletim").reset();
}

function atualizaTabelaObjetos() {
  const tbody = document.getElementById("listObjetosComBoletim");
  tbody.innerHTML = "";

  window.objetosComBoletim.forEach((obj, index) => {
    let tipoTexto = obj.tipo;
    let descricaoTexto = obj.descricao;

    // Badge visual para novos
    if (obj.status === "novo") {
      tipoTexto += ' <span class="badge bg-success">Novo</span>';
    } else if (obj.status === "removido") {
      tipoTexto = `<del>${tipoTexto}</del>`;
      descricaoTexto = `<del>${descricaoTexto}</del>`;
    }

    // Botão de remoção apenas altera status
    const btnRemover = obj.status === "removido"
      ? ""
      : `<button class="btn btn-sm btn-danger" onclick="removerObjeto(${index})">
           <i class="fa fa-trash"></i>
         </button>`;

    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${tipoTexto}</td>
      <td>${descricaoTexto}</td>
      <td class="text-center">${btnRemover}</td>
    `;
    tbody.appendChild(tr);
  });
}

function removerObjeto(index) {
  const obj = window.objetosComBoletim[index];
  if (obj) {
    obj.status = "removido"; // não remove, só marca
    atualizaTabelaObjetos();
  }
}

function limparObjetosComBoletim() {
	window.objetosComBoletim = [];
	atualizaTabelaObjetos();
	document.getElementById("formObjetoComBoletim").reset();
}

function getObjetoComBoletim() {
  return window.objetosComBoletim;
}

function preencherTabObjetosComBoletim(xmlString) {
  const parser = new DOMParser();
  const xmlDoc = parser.parseFromString(xmlString, "application/xml");

  const objetosXML = xmlDoc.getElementsByTagName("objeto");

  // Zera a lista antes de preencher
  window.objetosComBoletim = [];

  for (let i = 0; i < objetosXML.length; i++) {
    const id = parseInt(objetosXML[i].getElementsByTagName("id")[0]?.textContent, 10) || null;
    const tipo = objetosXML[i].getElementsByTagName("tipo")[0]?.textContent || "";
    const descricao = objetosXML[i].getElementsByTagName("descricao")[0]?.textContent || "";

    if (tipo && descricao) {
      window.objetosComBoletim.push({
        id,               // id do banco
        tipo,
        descricao,
        status: "existente" // carregado do banco
      });
    }
  }

  atualizaTabelaObjetos();
}
