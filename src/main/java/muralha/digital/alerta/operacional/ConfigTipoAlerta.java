package muralha.digital.alerta.operacional;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Configuração complementar de um tipo de alerta: criticidade, cor, ícone. */
public class ConfigTipoAlerta {

    private final UUID idTipo;
    private final String tipo;
    private boolean critico;
    private String cor;
    private String icone;

    public ConfigTipoAlerta(UUID idTipo, String tipo, boolean critico, String cor, String icone) {
        this.idTipo = idTipo;
        this.tipo = tipo;
        this.critico = critico;
        this.cor = cor;
        this.icone = icone;
    }

    public UUID getIdTipo() { return idTipo; }
    public String getTipo() { return tipo; }
    public boolean isCritico() { return critico; }
    public String getCor() { return cor; }
    public String getIcone() { return icone; }

    public void setCritico(boolean critico) { this.critico = critico; }
    public void setCor(String cor) { this.cor = cor; }
    public void setIcone(String icone) { this.icone = icone; }

    /** Cores que o popup de notificação sabe exibir (toastr + classes de notificacao.css), com o rótulo da tela. */
    public static final Map<String, String> CORES;
    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("error", "Vermelho");
        m.put("orange", "Laranja");
        m.put("warning", "Amarelo");
        m.put("success", "Verde");
        m.put("info", "Azul");
        m.put("purple", "Roxo");
        m.put("brown", "Marrom");
        m.put("pink", "Rosa");
        CORES = Collections.unmodifiableMap(m);
    }

    public static boolean corValida(String cor) {
        if (cor == null || cor.trim().isEmpty()) return true;
        return CORES.containsKey(cor.trim());
    }

    /** Classe do Bootstrap Icons (ex.: bi-exclamation-triangle-fill). */
    public static boolean iconeValido(String icone) {
        if (icone == null || icone.trim().isEmpty()) return true;
        return icone.trim().matches("^bi-[a-z0-9]+(-[a-z0-9]+)*$") && icone.trim().length() <= 50;
    }
}
