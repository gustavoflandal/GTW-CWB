package muralha.digital.chamado;

import java.util.Set;

public enum PerfilChamado {
    SOLICITANTE, GESTOR, EQUIPE, ADMIN;

    /** Gestor, equipe tecnica e administrador enxergam todos os chamados (spec D9). */
    public static boolean veTodos(Set<PerfilChamado> perfis) {
        return perfis.contains(GESTOR) || perfis.contains(EQUIPE) || perfis.contains(ADMIN);
    }
}
