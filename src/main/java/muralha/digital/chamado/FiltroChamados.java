package muralha.digital.chamado;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Filtros da lista/relatorio. O SQL usa os aliases c (chamado) e cat (categoria). */
public class FiltroChamados {

    public static final int TAMANHO_PAGINA = 50;

    public String protocolo;
    public String status;
    public Integer idPrioridade;
    public Integer idCategoria;
    public Integer idLocal;
    public LocalDate dtInicio;
    public LocalDate dtFim;
    /** Preenchido pelo servidor quando o usuario so ve os proprios chamados. */
    public Integer idSolicitante;
    public int pagina = 1;

    public String where(List<Object> params) {
        StringBuilder sb = new StringBuilder(" WHERE 1=1");
        if (idSolicitante != null) {
            sb.append(" AND c.id_usuario_solicitante = ?");
            params.add(idSolicitante);
        }
        if (protocolo != null) {
            String limpo = protocolo.replaceAll("[^A-Za-z0-9-]", "");
            if (!limpo.isEmpty()) {
                sb.append(" AND c.protocolo LIKE ?");
                params.add("%" + limpo + "%");
            }
        }
        if (status != null && !status.isEmpty()) {
            try {
                params.add(StatusChamado.valueOf(status).name());
            } catch (IllegalArgumentException e) {
                throw new RegraChamadoException("Status inválido.");
            }
            sb.append(" AND c.status = ?");
        }
        if (idPrioridade != null) {
            sb.append(" AND c.id_prioridade = ?");
            params.add(idPrioridade);
        }
        if (idCategoria != null) {
            sb.append(" AND (c.id_categoria = ? OR cat.id_pai = ?)");
            params.add(idCategoria);
            params.add(idCategoria);
        }
        if (idLocal != null) {
            sb.append(" AND c.id_local = ?");
            params.add(idLocal);
        }
        if (dtInicio != null) {
            sb.append(" AND c.dt_abertura >= ?");
            params.add(Timestamp.valueOf(dtInicio.atStartOfDay()));
        }
        if (dtFim != null) {
            sb.append(" AND c.dt_abertura < ?");
            params.add(Timestamp.valueOf(dtFim.plusDays(1).atStartOfDay()));
        }
        return sb.toString();
    }

    /** Texto legivel dos filtros, usado no cabecalho das exportacoes. */
    public String descricao() {
        List<String> partes = new ArrayList<>();
        String protocoloLimpo = protocolo == null ? "" : protocolo.replaceAll("[^A-Za-z0-9-]", "");
        if (!protocoloLimpo.isEmpty()) partes.add("protocolo " + protocoloLimpo);
        if (status != null && !status.isEmpty()) partes.add("status " + status);
        if (idPrioridade != null) partes.add("prioridade #" + idPrioridade);
        if (idCategoria != null) partes.add("categoria #" + idCategoria);
        if (idLocal != null) partes.add("equipamento #" + idLocal);
        if (dtInicio != null) partes.add("de " + dtInicio);
        if (dtFim != null) partes.add("até " + dtFim);
        if (idSolicitante != null) partes.add("somente chamados do solicitante");
        return partes.isEmpty() ? "sem filtros" : String.join(", ", partes);
    }

    public static FiltroChamados deRequest(HttpServletRequest req) {
        FiltroChamados f = new FiltroChamados();
        f.protocolo = texto(req, "protocolo");
        f.status = texto(req, "status");
        f.idPrioridade = inteiro(req, "idPrioridade");
        f.idCategoria = inteiro(req, "idCategoria");
        f.idLocal = inteiro(req, "idLocal");
        f.dtInicio = data(req, "dtInicio");
        f.dtFim = data(req, "dtFim");
        Integer pagina = inteiro(req, "pagina");
        f.pagina = pagina == null || pagina < 1 ? 1 : pagina;
        return f;
    }

    private static String texto(HttpServletRequest req, String nome) {
        String v = req.getParameter(nome);
        return v == null || v.trim().isEmpty() ? null : v.trim();
    }

    private static Integer inteiro(HttpServletRequest req, String nome) {
        String v = texto(req, nome);
        try {
            return v == null ? null : Integer.valueOf(v);
        } catch (NumberFormatException e) {
            throw new RegraChamadoException("Parâmetro inválido: " + nome);
        }
    }

    private static LocalDate data(HttpServletRequest req, String nome) {
        String v = texto(req, nome);
        try {
            return v == null ? null : LocalDate.parse(v);
        } catch (Exception e) {
            throw new RegraChamadoException("Data inválida: " + nome);
        }
    }
}
