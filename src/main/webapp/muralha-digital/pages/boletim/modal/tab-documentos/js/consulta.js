function baixarDocumentoPorId(id) {
	const params = new URLSearchParams({
		acao: 'downloadDocumento',
		id
	});
	const url = `/MuralhaDigital/Boletim/Doducmento?${params.toString()}`;

	SuccessNotification("Arquivo sera Baixado em alguns instantes!", "");
	// Usamos fetch para obter o arquivo como blob
	fetch(url)
		.then(response => {
			const contentType = response.headers.get("Content-Type");

			if (!response.ok || !contentType || !contentType.includes("application/octet-stream")) {
				// tenta ler o XML de erro e exibir a mensagem
				return response.text().then(text => {
					const parser = new DOMParser();
					const xmlDoc = parser.parseFromString(text, "text/xml");
					const msgErro = xmlDoc.querySelector("msgResposta")?.textContent || "Erro ao baixar documento!";
					throw new Error(msgErro);
				});
			}

			const disposition = response.headers.get("Content-Disposition");
			let nomeArquivo = "documento.dat";

			if (disposition && disposition.indexOf("filename=") !== -1) {
				const match = disposition.match(/filename="?([^"]+)"?/);
				if (match && match[1]) {
					nomeArquivo = decodeURIComponent(match[1]);
				}
			}

			return response.blob().then(blob => ({ blob, nomeArquivo }));
		})
		.then(({ blob, nomeArquivo }) => {
			const link = document.createElement("a");
			link.href = URL.createObjectURL(blob);
			link.download = nomeArquivo;
			document.body.appendChild(link);
			link.click();
			link.remove();
			URL.revokeObjectURL(link.href);
		})
		.catch(error => {
			console.error(error);
			ErrorNotification("Erro ao baixar documento!", "");
		});
}

function SuccessNotification(title, text) {
	//necessario <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
		title: title,
		icon: "success",
		text: text,
	});
}

function ErrorNotification(title, text) {
	if (!title) {
		title = 'Erro ao processar requisição ao servidor!!';
	}
	//necessario <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
		title: title,
		icon: "error",
		text: text,
	});
}
