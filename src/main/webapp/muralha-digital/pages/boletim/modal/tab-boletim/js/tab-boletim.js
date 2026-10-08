function getBoletim() {
	const tipo = document.getElementById("selTipoBoletim");
	const situacao = document.getElementById("selSituacaoBoletim");
	const cidade = document.getElementById("selCidadeBoletim");
	const detalhamento = document.getElementById("idDetalhamentoBoletim");
	const rua = document.getElementById("idRuaBoletim");
	const bairro = document.getElementById("idBairroBoletim");
	const numero = document.getElementById("idNumeroBoletim");
	const permitir = document.getElementById("permitirAtendimento").checked;
	let valido = true;
	const camposObrigatorios = [tipo, situacao, cidade, detalhamento, rua, bairro, numero];

	// Remove visual anterior de erro
	camposObrigatorios.forEach(el => el.classList.remove("is-invalid"));

	// Valida cada campo
	camposObrigatorios.forEach(el => {
		if (!el.value.trim() || el.value.trim() === "0") {
			el.classList.add("is-invalid");
			valido = false;
		}
	});

	if (!valido) {
		ErrorNotification("Por favor, preencha todos os campos obrigatórios na aba Boletim.","");
		return null;
	}
	const permite_atendimento = permitir ? 1 : 0;

	const boletimTab = {
		idTipo: tipo.value,
		idSituacao: situacao.value,
		id_cidade: cidade.value,
		detalhamento: detalhamento.value.trim(),
		rua: rua.value.trim(),
		bairro: bairro.value.trim(),
		numero: numero.value.trim(),
		complemento: document.getElementById("complementoBoletim").value.trim(),
		permite_atendimento
	};
	return boletimTab;
}

function atualizarContador() {
	const textarea = document.getElementById('idDetalhamentoBoletim');
	const contador = document.getElementById('idContadorBoletim');
	contador.textContent = textarea.value.length;
}


function limparCamposEListaBoletim() {
  const form = document.getElementById('formBoletim');
  if (!form) return;

  form.reset();

  const contador = document.getElementById('idContadorBoletim');
  if (contador) contador.textContent = '0';

  const tabela = document.getElementById('tabelaBoletim');
  if (tabela) {
    const tbody = tabela.querySelector('tbody');
    if (tbody) tbody.innerHTML = '';
  }
}

function bloquearCamposBoletim() {
  const form = document.getElementById('formBoletim');
  if (!form) return;

  const elements = form.querySelectorAll('input, select, textarea');
  elements.forEach(el => {
    el.disabled = true;
  });
}

function desbloquearCamposBoletim() {
  const form = document.getElementById('formBoletim');
  if (!form) return;

  const elements = form.querySelectorAll('input, select, textarea');
  elements.forEach(el => {
    el.disabled = false;
  });
}

function SuccessNotification(title,text)
{
	//precisa <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
	  title: title,
	  icon: "success",
	  text: text,
	});
}

function ErrorNotification(title, text)
{
	//precisa <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	Swal.fire({
	  title: title,
	  icon: "error",
	  text: text,
	});
}