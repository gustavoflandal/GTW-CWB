package muralha.digital.logtecnico;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Filtros do repositório de logs técnicos (TR 5.4.7.5 a): período, equipamentos,
 * localidades, categorias e severidades. Coleções vazias = sem filtro.
 * O fim do período é exclusivo.
 */
public final class FiltroLogTecnico {

    public static final int MAXIMO_EQUIPAMENTOS = 500;

    private final Timestamp inicio;
    private final Timestamp fim;
    private final Set<Integer> idLocais;
    private final Set<Integer> idLocalidades;
    private final EnumSet<CategoriaLog> categorias;
    private final EnumSet<SeveridadeLog> severidades;

    public FiltroLogTecnico(Timestamp inicio, Timestamp fim, Set<Integer> idLocais, Set<Integer> idLocalidades,
            Set<CategoriaLog> categorias, Set<SeveridadeLog> severidades) {
        this.inicio = inicio;
        this.fim = fim;
        this.idLocais = idLocais == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(idLocais));
        this.idLocalidades = idLocalidades == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(idLocalidades));
        this.categorias = categorias == null || categorias.isEmpty() ? EnumSet.noneOf(CategoriaLog.class) : EnumSet.copyOf(categorias);
        this.severidades = severidades == null || severidades.isEmpty() ? EnumSet.noneOf(SeveridadeLog.class) : EnumSet.copyOf(severidades);
    }

    /**
     * Lê os parâmetros da tela.
     * @param dtInicio      aaaa-mm-dd (obrigatória)
     * @param dtFim         aaaa-mm-dd (obrigatória, inclusiva)
     * @param maxDias       período máximo (parâmetro LOG_TECNICO_PERIODO_MAX_DIAS)
     * @throws IllegalArgumentException com mensagem em português
     */
    public static FiltroLogTecnico de(String dtInicio, String dtFim, String idLocais, String idLocalidades,
            String categorias, String severidades, int maxDias) {
        LocalDate ini = data(dtInicio, "inicial");
        LocalDate fim = data(dtFim, "final");
        if (fim.isBefore(ini)) throw new IllegalArgumentException("A data final é anterior à inicial.");
        if (ChronoUnit.DAYS.between(ini, fim) + 1 > maxDias) {
            throw new IllegalArgumentException("O período máximo é de " + maxDias + " dias.");
        }
        Set<Integer> locais = inteiros(idLocais, "Equipamento");
        if (locais.size() > MAXIMO_EQUIPAMENTOS) {
            throw new IllegalArgumentException("Selecione no máximo " + MAXIMO_EQUIPAMENTOS + " equipamentos.");
        }
        EnumSet<CategoriaLog> cats = EnumSet.noneOf(CategoriaLog.class);
        for (String t : partes(categorias)) {
            CategoriaLog c = CategoriaLog.deTexto(t);
            if (c == null) throw new IllegalArgumentException("Categoria inválida: " + t);
            cats.add(c);
        }
        EnumSet<SeveridadeLog> sevs = EnumSet.noneOf(SeveridadeLog.class);
        for (String t : partes(severidades)) {
            SeveridadeLog s = SeveridadeLog.deTexto(t);
            if (s == null) throw new IllegalArgumentException("Severidade inválida: " + t);
            sevs.add(s);
        }
        return new FiltroLogTecnico(Timestamp.valueOf(ini.atStartOfDay()), Timestamp.valueOf(fim.plusDays(1).atStartOfDay()),
            locais, inteiros(idLocalidades, "Localidade"), cats, sevs);
    }

    /** Mesmo filtro com outro conjunto de equipamentos (localidades já resolvidas). */
    public FiltroLogTecnico comEquipamentos(Set<Integer> locais) {
        return new FiltroLogTecnico(inicio, fim, locais, Collections.emptySet(), categorias, severidades);
    }

    private static LocalDate data(String texto, String nome) {
        if (texto == null || texto.trim().isEmpty()) throw new IllegalArgumentException("Informe a data " + nome + ".");
        try {
            return LocalDate.parse(texto.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Data " + nome + " inválida (use aaaa-mm-dd).");
        }
    }

    private static Set<Integer> inteiros(String csv, String nome) {
        Set<Integer> r = new LinkedHashSet<>();
        for (String t : partes(csv)) {
            try {
                int v = Integer.parseInt(t);
                if (v > 0) r.add(v);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(nome + " inválido: " + t);
            }
        }
        return r;
    }

    private static Set<String> partes(String csv) {
        Set<String> r = new LinkedHashSet<>();
        if (csv == null) return r;
        for (String p : csv.split(",")) {
            String t = p.trim();
            if (!t.isEmpty()) r.add(t);
        }
        return r;
    }

    public Timestamp getInicio() { return inicio; }

    public Timestamp getFim() { return fim; }

    public Set<Integer> getIdLocais() { return idLocais; }

    public Set<Integer> getIdLocalidades() { return idLocalidades; }

    public Set<CategoriaLog> getCategorias() { return categorias; }

    public Set<SeveridadeLog> getSeveridades() { return severidades; }

    /** Se o filtro restringe equipamentos (ou localidades). */
    public boolean restringeEquipamento() { return !idLocais.isEmpty() || !idLocalidades.isEmpty(); }

    public boolean aceita(CategoriaLog c) { return categorias.isEmpty() || categorias.contains(c); }

    public boolean aceita(SeveridadeLog s) { return severidades.isEmpty() || severidades.contains(s); }
}
