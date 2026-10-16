package muralha.digital.integridade;

import java.sql.SQLException;

/** Acesso a dados usado pelo {@link ProcessadorIntegridade}; a implementação real é {@link JdbcRepositorioIntegridade}. */
public interface RepositorioIntegridade {

    /** Hash enviado pelo Captura e gravado em muralha.vtr_imagem_hash_origem; nulo se não houver. */
    String hashPayload(String idImagem) throws SQLException;

    /** ia.veiculo_caracteristica.dado_bruto da captura; nulo se não houver. */
    String dadoBruto(String idCaptura) throws SQLException;

    /** Grava o resultado em vtr_imagem_integridade e vtr_imagem_complemento. */
    void gravar(ImagemPendente imagem, OrigemHash origem, ResultadoConferencia resultado) throws SQLException;

    /** Se já existe alerta de integridade para a passagem. */
    boolean existeAlerta(String idVeiculoTempoReal) throws SQLException;

    /** Cria o alerta operacional (muralha.alerta + alerta_veiculo). @return false se o tipo de alerta não estiver cadastrado */
    boolean gerarAlerta(ImagemPendente imagem, ResultadoConferencia resultado) throws SQLException;

    /** Registra a divergência em dbo.sis_log_auditoria. */
    void auditar(ImagemPendente imagem, ResultadoConferencia resultado);
}
