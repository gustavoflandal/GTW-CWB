package muralha.digital.chamado;

import java.util.Set;

public enum PerfilChamado {
    SOLICITANTE, GESTOR, EQUIPE;

    /** Gestor do contrato e equipe tecnica enxergam todos os chamados (spec D9). */
    public static boolean veTodos(Set<PerfilChamado> perfis) {
        return perfis.contains(GESTOR) || perfis.contains(EQUIPE);
    }
}
