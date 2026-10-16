package muralha.digital.logtecnico;

/** Categoria normalizada de um log técnico (TR 5.4.7.1-4). */
public enum CategoriaLog {
    ENERGIA("Energia"),
    COMUNICACAO("Comunicação"),
    SINCRONISMO("Sincronismo"),
    HARDWARE("Hardware"),
    SOFTWARE("Software"),
    SENSOR("Sensor"),
    PARAMETRO("Parâmetro"),
    INTERVENCAO("Intervenção técnica"),
    PROCESSAMENTO("Processamento"),
    USUARIO("Usuário");

    private final String rotulo;

    CategoriaLog(String rotulo) { this.rotulo = rotulo; }

    public String getRotulo() { return rotulo; }

    public static CategoriaLog deTexto(String texto) {
        if (texto == null) return null;
        for (CategoriaLog c : values()) {
            if (c.name().equalsIgnoreCase(texto.trim())) return c;
        }
        return null;
    }
}
