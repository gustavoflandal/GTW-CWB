package muralha.digital.auditoria;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

/**
 * Wrapper de resposta que memoriza o código de status HTTP.
 *
 * O servlet-api de compilação do projeto é anterior ao 3.0 e não expõe
 * {@code HttpServletResponse.getStatus()}; este wrapper intercepta os métodos
 * que definem o status ({@code setStatus}, {@code sendError}, {@code sendRedirect})
 * para que o {@link CapturaLogFilter} possa registrá-lo sem depender daquela API.
 */
public class RespostaComStatus extends HttpServletResponseWrapper {

    private int statusCapturado = 200;

    public RespostaComStatus(HttpServletResponse response) {
        super(response);
    }

    public int getStatusCapturado() {
        return statusCapturado;
    }

    @Override
    public void setStatus(int sc) {
        this.statusCapturado = sc;
        super.setStatus(sc);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void setStatus(int sc, String sm) {
        this.statusCapturado = sc;
        super.setStatus(sc, sm);
    }

    @Override
    public void sendError(int sc) throws IOException {
        this.statusCapturado = sc;
        super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        this.statusCapturado = sc;
        super.sendError(sc, msg);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        this.statusCapturado = 302;
        super.sendRedirect(location);
    }
}
