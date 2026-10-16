package muralha.digital.ocr;

import java.sql.SQLException;
import java.util.List;

/** Acesso a dados do {@link ProcessadorOcr}; a implementação real é {@link JdbcRepositorioOcr}. */
public interface RepositorioOcr {

    /** Leituras já gravadas em muralha.vtr_placa_leitura para a passagem (ex.: pela recepção ou OCR central). */
    List<LeituraPlaca> leiturasGravadas(String idVeiculoTempoReal) throws SQLException;

    /** ia.veiculo_caracteristica.dado_bruto da captura; nulo se não houver. */
    String dadoBruto(String idCaptura) throws SQLException;

    /** Grava leituras novas (não sobrescreve as existentes). */
    void gravarLeituras(PassagemPendente p, List<LeituraPlaca> leituras) throws SQLException;

    /** Marca a passagem como avaliada e, se for o caso, coloca na fila de revisão. */
    void registrarAvaliacao(PassagemPendente p, RegraRevisao.Avaliacao avaliacao, double limiar) throws SQLException;
}
