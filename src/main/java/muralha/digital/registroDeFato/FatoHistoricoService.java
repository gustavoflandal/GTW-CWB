package muralha.digital.registroDeFato;

import com.consilux.infra.exception.ConexaoException;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Classe responsável por gerenciar o histórico de alterações do RegistroDeFato.
 * ignore campos nulos e vazios, simplificando os métodos de conversão de mapa.
 */
public class FatoHistoricoService {

	// ===================================================================================
	// ObjectMapper
	// ===================================================================================

	/**
	 * Instância única e pré-configurada do ObjectMapper para performance e
	 * consistência. É uma prática recomendada criar o ObjectMapper apenas uma vez e
	 * reutilizá-lo.
	 */
	private static final ObjectMapper objectMapper = createObjectMapper();

	private static ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		// Configuração para não incluir campos com valores nulos ou vazios (listas
		// vazias, strings vazias)
		// NOTA: NON_EMPTY já cobre o caso de NON_NULL, então apenas ele é necessário.
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		return mapper;
	}

	// ===================================================================================
	// 1. MÉTODO PRINCIPAL DE ORQUESTRAÇÃO
	// ===================================================================================

	public void adicionarRegistroFatoHistorico(Long idRegistro, String tipoOperacao, Integer idUsuario,
			RegistroDeFato registroAnterior) throws SQLException, ConexaoException {

		if (!"INSERT".equalsIgnoreCase(tipoOperacao) && !"UPDATE".equalsIgnoreCase(tipoOperacao)) {
			throw new IllegalArgumentException("Tipo de operação inválido. Use 'INSERT' ou 'UPDATE'.");
		}

		RegistroDeFatoHistorico historico = new RegistroDeFatoHistorico();
		historico.setIdRegistro(idRegistro);
		historico.setTipoOperacao(tipoOperacao);
		historico.setIdUsuario(idUsuario);

		if ("INSERT".equalsIgnoreCase(tipoOperacao)) {
			historico.setDadosAnteriores(null);
			historico.setDadosNovos(converterRegistroParaJson(registroAnterior));
		} else { // UPDATE
			// Busca o registro atualizado do banco para comparar com o estado anterior.
			RegistroDeFato registroAtual = RegistroDeFatos.ObterRegistroDeFatoPorId(idRegistro, idUsuario);
			historico.setDadosAnteriores(converterRegistroParaJson(registroAnterior));
			historico.setDadosNovos(gerarDiffJson(registroAnterior, registroAtual));
		}

		// Persiste o registro de histórico gerado no banco de dados.
		RegistroDeFatoHistoricos.gravarHistoricoNoBanco(historico);
	}

	// ===================================================================================
	// 2. LÓGICA DE GERAÇÃO DE DIFERENÇAS (DIFF)
	// ===================================================================================

	private String gerarDiffJson(RegistroDeFato anterior, RegistroDeFato atual) {
		if (anterior == null || atual == null)
			return null;

		Map<String, Object> diffMap = new LinkedHashMap<>();
		diffMap.put("id", atual.getId()); // O ID é sempre incluído para referência.

		// Compara campos simples
		compararEAdicionar(diffMap, "idTipo", anterior.getIdTipo(), atual.getIdTipo());
		compararEAdicionar(diffMap, "idNaturezaTipo", anterior.getIdNaturezaTipo(), atual.getIdNaturezaTipo());
		compararEAdicionar(diffMap, "dataEvento", anterior.getDataEvento(), atual.getDataEvento());
		compararEAdicionar(diffMap, "idStatus", anterior.getIdStatus(), atual.getIdStatus());
		
	    if (atual.getIdStatus() != null && atual.getIdStatus().equals(2) && !Objects.equals(anterior.getIdStatus(), atual.getIdStatus())) {
	        // Se a condição for verdadeira, adiciona uma observação ao histórico
	        diffMap.put("observacaoAlteracao", "Registro de Fato finalizado pelo usuário.");
	    }
		
		compararEAdicionar(diffMap, "privado", anterior.getPrivado(), atual.getPrivado());
		compararEAdicionar(diffMap, "detalhamento", anterior.getDetalhamento(), atual.getDetalhamento());
		compararEAdicionar(diffMap, "permiteAtendimento", anterior.getPermiteAtendimento(),
				atual.getPermiteAtendimento());
		compararEAdicionar(diffMap, "envolvimentoArmas", anterior.getEnvolvimentoArmas(), atual.getEnvolvimentoArmas());

		// Compara objetos aninhados (Endereço)
		RegistroDeFatoEndereco endAnterior = (anterior.getEnderecos() != null && !anterior.getEnderecos().isEmpty())
				? anterior.getEnderecos().get(0)
				: null;
		RegistroDeFatoEndereco endAtual = (atual.getEnderecos() != null && !atual.getEnderecos().isEmpty())
				? atual.getEnderecos().get(0)
				: null;
		Map<String, Object> diffEndereco = gerarEnderecoDiffMap(endAnterior, endAtual);
		if (diffEndereco != null) {
			diffMap.put("endereco", diffEndereco);
		}

	    Boletim boletimAnterior = (anterior.getBoletins() != null && !anterior.getBoletins().isEmpty()) ? anterior.getBoletins().get(0) : null;
	    Boletim boletimAtual = (atual.getBoletins() != null && !atual.getBoletins().isEmpty()) ? atual.getBoletins().get(0) : null;
	    
	    Map<String, Object> diffBoletim = gerarBoletimDiffMap(boletimAnterior, boletimAtual);
	    if (diffBoletim != null) {
	        diffMap.put("boletim", diffBoletim);
	    }
		compararEAdicionarAlteracoesDeLista(diffMap, "individuos", anterior.getIndividuos(), atual.getIndividuos());
		compararEAdicionarAlteracoesDeLista(diffMap, "veiculos", anterior.getVeiculos(), atual.getVeiculos());
		compararEAdicionarAlteracoesDeLista(diffMap, "objetos", anterior.getObjetos(), atual.getObjetos());
		compararEAdicionarAlteracoesDeLista(diffMap, "documentos", anterior.getDocumentos(), atual.getDocumentos());
		compararEAdicionarAlteracoesDeLista(diffMap, "links", anterior.getLinks(), atual.getLinks());
		compararEAdicionarAlteracoesDeLista(diffMap, "usuarioGrupos", anterior.getUsuarioGrupos(), atual.getUsuarioGrupos());
		compararEAdicionarAlteracoesDeLista(diffMap, "passagensVeiculo", anterior.getPassagensVeiculo(), atual.getPassagensVeiculo());
		compararEAdicionarAlteracoesDeLista(diffMap, "anotacoes", anterior.getAnotacoes(), atual.getAnotacoes());
		if (diffMap.size() <= 1) {
			return null;
		}

		return converterMapParaJson(diffMap);
	}

	private Map<String, Object> gerarEnderecoDiffMap(RegistroDeFatoEndereco anterior, RegistroDeFatoEndereco atual) {
		if (atual == null)
			return null;
		if (anterior == null)
			return converterEnderecoParaMap(atual);

		Map<String, Object> diff = new LinkedHashMap<>();
		diff.put("id", atual.getId());

		compararEAdicionar(diff, "cep", anterior.getCep(), atual.getCep());
		compararEAdicionar(diff, "bairro", anterior.getBairro(), atual.getBairro());
		compararEAdicionar(diff, "rua", anterior.getRua(), atual.getRua());
		compararEAdicionar(diff, "numero", anterior.getNumero(), atual.getNumero());
		compararEAdicionar(diff, "complemento", anterior.getComplemento(), atual.getComplemento());
		compararEAdicionar(diff, "latitude", anterior.getLatitude(), atual.getLatitude());
		compararEAdicionar(diff, "longitude", anterior.getLongitude(), atual.getLongitude());
		compararEAdicionar(diff, "idCidade", anterior.getIdCidade(), atual.getIdCidade());
		compararEAdicionar(diff, "idTipoEvento", anterior.getIdTipoEvento(), atual.getIdTipoEvento());

		return diff.size() > 1 ? diff : null;
	}
	
	private Map<String, Object> gerarBoletimDiffMap(Boletim anterior, Boletim atual) {
	    if (atual == null) return null;

	    if (anterior == null) return converterBoletimParaMap(atual);

	    Map<String, Object> diff = new LinkedHashMap<>();
	    diff.put("id", atual.getId());

	    compararEAdicionar(diff, "detalhamento", anterior.getDetalhamento(), atual.getDetalhamento());
	    compararEAdicionar(diff, "permitirAtendimento", anterior.getPermiteAtendimento(), atual.getPermiteAtendimento());
	    
	    compararEAdicionarAlteracoesDeLista(diff, "apreensoes", anterior.getApreensoes(), atual.getApreensoes());

	    return diff.size() > 1 ? diff : null;
	}

	/**
	 * Compara duas listas e identifica os itens adicionados e removidos.
	 * Adiciona um mapa com as chaves "adicionados" e "removidos" ao diffMap principal.
	 */
	private <T> void compararEAdicionarAlteracoesDeLista(Map<String, Object> diffMap, String key, List<T> listaAnterior, List<T> listaAtual) {
		// Garante que as listas não sejam nulas para evitar NullPointerException nos streams.
		List<T> anteriorSafe = (listaAnterior == null) ? new ArrayList<>() : listaAnterior;
		List<T> atualSafe = (listaAtual == null) ? new ArrayList<>() : listaAtual;

		// Itens adicionados: Estão na lista atual, mas não estavam na anterior.
		List<T> adicionados = atualSafe.stream()
				.filter(itemAtual -> anteriorSafe.stream()
						.noneMatch(itemAnterior -> equalsConteudo(itemAnterior, itemAtual)))
				.collect(Collectors.toList());

		// Itens removidos: Estavam na lista anterior, mas não estão na atual.
		List<T> removidos = anteriorSafe.stream()
				.filter(itemAnterior -> atualSafe.stream()
						.noneMatch(itemAtual -> equalsConteudo(itemAnterior, itemAtual)))
				.collect(Collectors.toList());
		
		// Só adiciona ao mapa de diferenças se houver alguma alteração.
		if (!adicionados.isEmpty() || !removidos.isEmpty()) {
			Map<String, List<T>> alteracoes = new LinkedHashMap<>();
			
			if (!adicionados.isEmpty()) {
				alteracoes.put("adicionados", adicionados);
			}
			if (!removidos.isEmpty()) {
				alteracoes.put("removidos", removidos);
			}
			
			diffMap.put(key, alteracoes);
		}
	}
	

	// ===================================================================================
	// 3. LÓGICA DE CONVERSÃO PARA JSON (Com simplificações)
	// ===================================================================================
	private String converterRegistroParaJson(RegistroDeFato registro) {
		if (registro == null) {
			return null;
		}
		// Etapa 1: Chama o especialista em converter o objeto para um mapa.
		Map<String, Object> mapa = converterRegistroParaMap(registro);

		// Etapa 2: Chama o especialista em converter o mapa para uma string JSON.
		return converterMapParaJson(mapa);
	}

	private Map<String, Object> converterRegistroParaMap(RegistroDeFato registro) {
		if (registro == null)
			return null;

		Map<String, Object> mapa = new LinkedHashMap<>();

		// Mapeamento dos campos simples
		mapa.put("id", registro.getId());
		mapa.put("idTipo", registro.getIdTipo());
		mapa.put("idNaturezaTipo", registro.getIdNaturezaTipo());
		mapa.put("naturezaTipoDescricao", registro.getNaturezaTipo().getNaturezaDesc());
		mapa.put("dataEvento", registro.getDataEvento());
		mapa.put("idStatus", registro.getIdStatus());
		mapa.put("temBoletim", registro.getTemBoletim());
		mapa.put("idUsuario", registro.getIdUsuario());
		// Adicionando outros campos que podem ser úteis no histórico
		mapa.put("dataCriacao", registro.getDataCriacao());
		mapa.put("dataEncerramento", registro.getDataEncerramento());
		mapa.put("tipoDescricao", registro.getTipoDescricao());
		mapa.put("statusDescricao", registro.getStatusDescricao());
		mapa.put("nomeUsuario", registro.getNomeUsuario());
		mapa.put("privado", registro.getPrivado());
		mapa.put("detalhamento", registro.getDetalhamento());
		mapa.put("permiteAtendimento", registro.getPermiteAtendimento());
		mapa.put("envolvimentoArmas", registro.getEnvolvimentoArmas());

		// Mapeamento de objetos aninhados
		if (registro.getEnderecos() != null && !registro.getEnderecos().isEmpty()) {
			mapa.put("endereco", converterEnderecoParaMap(registro.getEnderecos().get(0)));
		}

		if (registro.getBoletins() != null && !registro.getBoletins().isEmpty()) {
			if (registro.getBoletins().size() == 1) {
				mapa.put("boletim", converterBoletimParaMap(registro.getBoletins().get(0)));
			} else {
				mapa.put("boletins", registro.getBoletins());
			}
		}

		mapa.put("individuos", registro.getIndividuos());
		mapa.put("veiculos", registro.getVeiculos());
		
		mapa.put("objetos", registro.getObjetos());
		mapa.put("documentos", registro.getDocumentos());
		mapa.put("links", registro.getLinks());
		mapa.put("usuarioGrupos", registro.getUsuarioGrupos());
		mapa.put("passagensVeiculo", registro.getPassagensVeiculo());
		mapa.put("anotacoes", registro.getAnotacoes());
		return mapa;
	}

	private Map<String, Object> converterEnderecoParaMap(RegistroDeFatoEndereco endereco) {
		if (endereco == null)
			return null;
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("id", endereco.getId());
		mapa.put("cep", endereco.getCep());
		mapa.put("bairro", endereco.getBairro());
		mapa.put("rua", endereco.getRua());
		mapa.put("numero", endereco.getNumero());
		mapa.put("complemento", endereco.getComplemento());
		mapa.put("latitude", endereco.getLatitude());
		mapa.put("longitude", endereco.getLongitude());
		mapa.put("idCidade", endereco.getIdCidade());
		mapa.put("idTipoEvento", endereco.getIdTipoEvento());
		mapa.put("nomeCidade", endereco.getCidade().getNome());
		return mapa;
	}

	private Map<String, Object> converterBoletimParaMap(Boletim boletim) {
		if (boletim == null)
			return null;
		Map<String, Object> mapa = new LinkedHashMap<>();
		mapa.put("id", boletim.getId());
		mapa.put("detalhamento", boletim.getDetalhamento());
		mapa.put("situacao", boletim.getSituacao());
		mapa.put("apreensoes", boletim.getApreensoes());
		mapa.put("apreensoes", boletim.getApreensoes());
		mapa.put("permitirAtendimento", boletim.getPermiteAtendimento());
		return mapa;
	}

	private String converterMapParaJson(Map<String, Object> mapa) {
		if (mapa == null || mapa.isEmpty())
			return null;
		try {
			return objectMapper.writeValueAsString(mapa);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Erro ao converter mapa para JSON", e);
		}
	}

	// ===================================================================================
	// 4. UTILITÁRIOS DE COMPARAÇÃO
	// ===================================================================================

	private void compararEAdicionar(Map<String, Object> diffMap, String key, Object anterior, Object atual) {
		if (!Objects.equals(anterior, atual)) {
			diffMap.put(key, atual);
		}
	}

	private boolean equalsConteudo(Object a, Object b) {
		if (a == null || b == null || a.getClass() != b.getClass()) {
			return false;
		}

		// --- Verificações existentes ---
        if (a instanceof Boletim)
            return ((Boletim) a).equalsConteudo((Boletim) b);
		if (a instanceof RegistroDeFatoIndividuo)
			return ((RegistroDeFatoIndividuo) a).equalsConteudo((RegistroDeFatoIndividuo) b);
		if (a instanceof RegistroDeFatoVeiculo)
			return ((RegistroDeFatoVeiculo) a).equalsConteudo((RegistroDeFatoVeiculo) b);
		if (a instanceof RegistroDeFatoObjeto)
			return ((RegistroDeFatoObjeto) a).equalsConteudo((RegistroDeFatoObjeto) b);
		if (a instanceof RegistroDeFatoDocumento)
			return ((RegistroDeFatoDocumento) a).equalsConteudo((RegistroDeFatoDocumento) b);
		if (a instanceof RegistroDeFatoLink)
			return ((RegistroDeFatoLink) a).equalsConteudo((RegistroDeFatoLink) b);
		if (a instanceof RegistroDeFatoUsuarioGrupo)
			return ((RegistroDeFatoUsuarioGrupo) a).equalsConteudo((RegistroDeFatoUsuarioGrupo) b);
		if (a instanceof RegistroFatoPassagemVeiculo)
			return ((RegistroFatoPassagemVeiculo) a).equalsConteudo((RegistroFatoPassagemVeiculo) b);
        if (a instanceof BoletimApreensao)
            return ((BoletimApreensao) a).equalsConteudo((BoletimApreensao) b);
        if (a instanceof RegistroDeFatoAnotacao)
            return ((RegistroDeFatoAnotacao) a).equalsConteudo((RegistroDeFatoAnotacao) b);
		// Fallback para o método equals padrão, caso nenhuma verificação específica corresponda.
		return a.equals(b);
	}
}