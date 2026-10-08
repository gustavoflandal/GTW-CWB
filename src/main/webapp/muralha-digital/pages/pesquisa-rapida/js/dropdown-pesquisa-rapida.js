let tipoBuscaSelecionado = 'PesquisaRapida';

// Aqui definimos as regras para cada tipo de busca.
// Para ativar para CPF ou Veículo no futuro, basta mudar "enabled" para "true".
const autocompleteConfig = {
    Nome: {
        enabled: true, 
        minChars: 3,
        paramName: 'nome'
    },
    CPF: {
        enabled: false,
        minChars: 5,
        paramName: 'cpf'
    },
    Veiculo: {
        enabled: false,
        minChars: 4,
        paramName: 'veiculo'
    }
};

let inputBusca; // Variável para o elemento de input

// === Aplica ou remove máscara com base no tipo selecionado ===
function aplicarOuRemoverMascara(tipo, inputElement) {
  if (!inputElement) return;

  // Remove handlers anteriores
  inputElement.value = ''; // Limpa o campo ao trocar
  inputElement.removeEventListener('input', mascararCPFHandler);
  inputElement.removeEventListener('input', forcarUppercaseHandler);
  desativarAutocomplete(); // Sempre desativa primeiro para garantir

  if (tipo === 'CPF') {
    inputElement.addEventListener('input', mascararCPFHandler);
  } else if (tipo === 'Veiculo') {
    inputElement.addEventListener('input', forcarUppercaseHandler);
  }

  // ATIVAÇÃO DINÂMICA: Verifica no objeto de configuração se o autocomplete deve ser ativado
  if (autocompleteConfig[tipo] && autocompleteConfig[tipo].enabled) {
    ativarAutocomplete();
  }
}

// === Handler de máscara CPF ===
function mascararCPFHandler(e) {
  const valor = e.target.value.replace(/\D/g, '');

  let resultado = '';
  if (valor.length > 0) resultado += valor.substring(0, 3);
  if (valor.length >= 4) resultado += '.' + valor.substring(3, 6);
  if (valor.length >= 7) resultado += '.' + valor.substring(6, 9);
  if (valor.length >= 10) resultado += '-' + valor.substring(9, 11);

  e.target.value = resultado;
}

// === Handler para forçar letras maiúsculas (placas) ===
function forcarUppercaseHandler(e) {
  e.target.value = e.target.value.toUpperCase();
}

// === Atualiza visibilidade do input e aplica máscara ===
function atualizarVisibilidadeInput() {
  const tipo = tipoBuscaSelecionado;
  const input = document.getElementById('valorBusca');
  const grupoInput = document.getElementById('grupoInputBusca');
  const resultado = document.getElementById('resultadoBusca');

  if (!input || !grupoInput) return;

  if (tipo === 'PesquisaRapida') {
    grupoInput.classList.add('d-none');
    if (resultado) {
      resultado.innerHTML = '<p>Você selecionou <strong>Pesquisa Rápida</strong>.</p>';
    }
  } else {
    grupoInput.classList.remove('d-none');
    input.focus();
  }

  aplicarOuRemoverMascara(tipo, input);
}

// === Valida CPF ===
function validarCPF(cpf) {
  if (!cpf || cpf.length !== 11 || /^(\d)\1+$/.test(cpf)) return false;

  let soma = 0;
  for (let i = 0; i < 9; i++) soma += parseInt(cpf.charAt(i)) * (10 - i);

  let resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;
  if (resto !== parseInt(cpf.charAt(9))) return false;

  soma = 0;
  for (let i = 0; i < 10; i++) soma += parseInt(cpf.charAt(i)) * (11 - i);
  resto = (soma * 10) % 11;
  if (resto === 10 || resto === 11) resto = 0;

  return resto === parseInt(cpf.charAt(10));
}

// === Executa busca conforme tipo selecionado ===
function buscarPorTipoSelecionado() {
  const tipo = tipoBuscaSelecionado;
  const input = document.getElementById('valorBusca');
  if (!input) return;

  let valor = input.value.trim();

  if (!valor) {
    Swal.fire({
      icon: 'warning',
      title: 'Atenção',
      text: 'Digite um valor para buscar.',
    });
    return;
  }

  let idTipoConsulta;
  switch (tipo) {
    case 'Veiculo':
      idTipoConsulta = 1;
      break;
    case 'CPF':
      idTipoConsulta = 2;
      valor = valor.replace(/[^\d]/g, '');
      if (!validarCPF(valor)) {
        Swal.fire({
          icon: 'error',
          title: 'CPF inválido',
          text: 'Por favor, digite um CPF válido.',
        });
        return;
      }
      break;
    case 'Nome':
      idTipoConsulta = 3;
	  valor = valor.replace(/\s+/g, ' ');
      break;
    default:
      Swal.fire({
        icon: 'error',
        title: 'Erro',
        text: 'Tipo de busca inválido.',
      });
      return;
  }

  abrirModalPesquisaRapida(valor, idTipoConsulta);
}

// === Cancela busca e reseta dropdown ===
function cancelarBusca() {
  tipoBuscaSelecionado = 'PesquisaRapida';
  const dropdownLabel = document.getElementById('dropdownTipoBuscaLabel');
  const itens = document.querySelectorAll('#dropdownTipoBusca + .dropdown-menu .dropdown-item');

  if (dropdownLabel) dropdownLabel.textContent = 'Pesquisa Rápida';
  itens.forEach(i => {
    i.classList.remove('active');
    if (i.dataset.value === 'PesquisaRapida') i.classList.add('active');
  });

  atualizarVisibilidadeInput();
}

// === Evento principal ao carregar DOM ===
document.addEventListener('DOMContentLoaded', function () {
    
    // PASSO CRUCIAL: Inicialize a variável AQUI, antes de qualquer outra coisa.
    inputBusca = document.getElementById('valorBusca');

    // O resto do seu código que já estava aqui...
    const tipoBusca = document.getElementById('tipoBusca');
    const btnBuscar = document.getElementById('btnBuscarDropdown');
    const btnCancelar = document.getElementById('btnCancelarBusca');

    if (tipoBusca) tipoBusca.addEventListener('change', atualizarVisibilidadeInput);
    if (btnBuscar) btnBuscar.addEventListener('click', buscarPorTipoSelecionado);
    if (btnCancelar) btnCancelar.addEventListener('click', cancelarBusca);

    // Dropdown customizado
    document.querySelectorAll('#dropdownTipoBusca + .dropdown-menu .dropdown-item').forEach(function (item) {
        item.addEventListener('click', function (e) {
            e.preventDefault();

            tipoBuscaSelecionado = item.dataset.value;

            const label = document.getElementById('dropdownTipoBuscaLabel');
            if (label) label.textContent = item.textContent;

            document.querySelectorAll('#dropdownTipoBusca + .dropdown-menu .dropdown-item')
                .forEach(i => i.classList.remove('active'));
            item.classList.add('active');

            atualizarVisibilidadeInput();
        });
    });
    
    // Esta chamada agora vai funcionar, pois 'inputBusca' já foi definido.
    atualizarVisibilidadeInput();
});

// --- Função Utilitária de Debounce (sem alterações) ---
function debounce(func, delay) {
    let timeout;
    return function(...args) {
        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(this, args), delay);
    };
}

// --- Função GENÉRICA que busca as sugestões no backend ---
async function fetchGenericSuggestions(termo, tipo) {
    const config = autocompleteConfig[tipo];
    const resultsList = document.getElementById('autocomplete-results');

    // CORREÇÃO: Limpa o termo de busca ANTES de qualquer validação ou uso.
    // .trim() remove espaços do início e do fim, que é a causa do seu problema.
    // .replace() remove espaços duplicados no meio, melhorando a busca.
    const termoLimpo = termo.trim().replace(/\s+/g, ' ');

    // Agora, a validação de tamanho mínimo é feita no termo já limpo.
    if (!config || !config.enabled || termoLimpo.length < config.minChars) {
        resultsList.innerHTML = '';
        return;
    }
    
    try {
        // Enviamos para o backend o termo JÁ LIMPO, sem espaços extras.
        const suggestions = await BuscarSugestoesAutocomplete(termoLimpo, config.paramName);
        
        renderAutocomplete(suggestions);

    } catch (error) {
        // O erro já é registrado no console pela função de busca, mas garantimos que a lista seja limpa.
        console.error('Falha ao obter sugestões de autocomplete:', error);
        resultsList.innerHTML = ''; 
    }
}

// --- Função que renderiza as sugestões na tela (sem alterações) ---
function renderAutocomplete(suggestions) {
    const resultsList = document.getElementById('autocomplete-results');
    resultsList.innerHTML = '';

    if (suggestions.length === 0) return;

    suggestions.forEach(itemString => {
        const partes = itemString.split('|');
        const valorPrincipal = partes[0]; 
        const textoDisplay = partes[1];

        const li = document.createElement('li');
        li.className = 'list-group-item autocomplete-item';
        
        // 2. Mostra o texto de display completo na lista
        li.textContent = textoDisplay;
        
        li.addEventListener('click', () => {
            document.getElementById('valorBusca').value = valorPrincipal;
            resultsList.innerHTML = '';
            buscarPorTipoSelecionado();
        });
        resultsList.appendChild(li);
    });
}

// --- Handler de input que aciona a busca ---
function handleAutocompleteInput(event) {
    const termo = event.target.value;
    const tipo = tipoBuscaSelecionado; // Pega o tipo de busca atual
    fetchGenericSuggestions(termo, tipo); // Chama a função genérica
}

// Cria a versão "debounced" do nosso handler
const debouncedAutocompleteHandler = debounce(handleAutocompleteInput, 300);

// --- Funções para ligar e desligar o autocomplete ---
function ativarAutocomplete() {
    inputBusca.addEventListener('input', debouncedAutocompleteHandler);
    document.addEventListener('click', fecharAutocompleteOnClickFora);
}

function desativarAutocomplete() {
    inputBusca.removeEventListener('input', debouncedAutocompleteHandler);
    document.removeEventListener('click', fecharAutocompleteOnClickFora);
    document.getElementById('autocomplete-results').innerHTML = '';
}

function fecharAutocompleteOnClickFora(e) {
    const autocompleteContainer = document.getElementById('grupoInputBusca');
    // Verifica se o clique foi fora do container do input e da lista de resultados
    if (autocompleteContainer && !autocompleteContainer.contains(e.target)) {
        document.getElementById('autocomplete-results').innerHTML = '';
    }
}
