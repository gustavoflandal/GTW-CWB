package muralha.digital.lote;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Regras da Gestao de Lotes que nao dependem do banco. */
public final class LoteRegras {

    public static final String ACAO_TELA = "/muralha-digital/pages/processamento/lotes/index.jsp";
    public static final int MAX_ITENS = 500;

    private LoteRegras() {}

    public static List<UUID> idsValidos(String csv) {
        Set<UUID> ids = new LinkedHashSet<>();
        if (csv != null) {
            for (String parte : csv.split(",")) {
                String id = parte.trim();
                if (id.isEmpty()) continue;
                try {
                    ids.add(UUID.fromString(id));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Identificador de infração inválido: " + id);
                }
            }
        }
        if (ids.isEmpty()) throw new IllegalArgumentException("Nenhuma infração selecionada.");
        if (ids.size() > MAX_ITENS) throw new IllegalArgumentException("Selecione no máximo " + MAX_ITENS + " infrações por vez.");
        return new ArrayList<>(ids);
    }
}
