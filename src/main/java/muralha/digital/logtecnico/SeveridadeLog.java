package muralha.digital.logtecnico;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Severidade normalizada, na ordem crescente de gravidade. */
public enum SeveridadeLog {
    DEBUG, INFO, ALERTA, ERRO, CRITICO;

    /** Domínio dbo.eventos_csx_desc_nivel: 0 INFO, 1 ALERTA, 2 ERRO, 3 CRÍTICO, 4 DEBUG. */
    public static SeveridadeLog doNivelCsx(int idNivel) {
        switch (idNivel) {
            case 1: return ALERTA;
            case 2: return ERRO;
            case 3: return CRITICO;
            case 4: return DEBUG;
            default: return INFO;
        }
    }

    /** Inverso de {@link #doNivelCsx}: ids de nível do Captura que correspondem às severidades. */
    public static List<Integer> niveisCsx(Collection<SeveridadeLog> severidades) {
        List<Integer> ids = new ArrayList<>();
        for (int nivel = 0; nivel <= 4; nivel++) {
            if (severidades.contains(doNivelCsx(nivel))) ids.add(nivel);
        }
        return ids;
    }

    public static SeveridadeLog deTexto(String texto) {
        if (texto == null) return null;
        for (SeveridadeLog s : values()) {
            if (s.name().equalsIgnoreCase(texto.trim())) return s;
        }
        return null;
    }
}
