package muralha.digital.alerta.operacional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface RepositorioRetencao {
    /** Equipamentos (id_local) com dados sumarizados nas últimas N horas, limitado ao lote. */
    List<Integer> equipamentosComDados(int janelaHoras, int lote);

    /** Dados horários de um equipamento, agregados por hora (todas as faixas/classes). */
    List<RegraRetencao.DadoHora> dadosPorEquipamento(int idLocal, int janelaHoras);

    /** Verifica se já existe alerta recente para o equipamento (dentro do intervalo de espera). */
    boolean alertaRecente(int idLocal, int esperaMinutos);

    /** Grava o alerta de retenção e retorna o UUID criado. */
    UUID gravarAlerta(int idLocal, RegraRetencao.Avaliacao avaliacao, UUID idTipoCongestionamento);
}
