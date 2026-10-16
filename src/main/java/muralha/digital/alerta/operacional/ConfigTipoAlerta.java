package muralha.digital.alerta.operacional;

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

    public static boolean corValida(String cor) {
        if (cor == null || cor.trim().isEmpty()) return true;
        return cor.trim().matches("^[a-zA-Z0-9#_-]{1,30}$");
    }
}
