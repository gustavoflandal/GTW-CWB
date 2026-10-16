package muralha.digital.logtecnico;

import com.consilux.infra.SerializadorXML;
import com.consilux.lib.Conexao;
import com.consilux.model.ConfiguracaoEquipamento;
import com.consilux.model.LocalVigente;
import com.consilux.model.TConfigEquip;
import muralha.digital.util.ConfigChaveValor;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Histórico de parâmetros da configuração de equipamento (ConfigEquip), chamado por
 * {@code ConfiguracaoEquipamento.incluirConfigEquip} — usado pelo web service ConfigEquipWS e pela
 * importação de XML. Compara a versão vigente com a nova e grava uma linha por campo alterado.
 * Nunca lança exceção: falha no histórico não pode impedir a gravação da configuração.
 */
public final class HistoricoConfigEquip {

    private static final Logger logger = Logger.getLogger(HistoricoConfigEquip.class);

    public static final String CHAVE_IGNORAR = "PARAMETRO_HIST_IGNORAR_CONFIGEQUIP";
    static final String IGNORAR_PADRAO = "dataHoraConfiguracao";

    private HistoricoConfigEquip() {}

    /** XML da configuração vigente do equipamento, ou nulo (equipamento novo ou erro). */
    public static String xmlVigente(LocalVigente vigente) {
        if (vigente == null) return null;
        try {
            return serializar(ConfiguracaoEquipamento.buscarConfigEquipPorId(vigente.getIdConfiguracaEquipamento()).getTconfigEqup());
        } catch (Throwable t) {
            logger.warn("Histórico de ConfigEquip: não foi possível ler a configuração vigente do local "
                + vigente.getIdLocal() + ": " + t.getMessage());
            return null;
        }
    }

    /** Grava as diferenças entre a configuração anterior e a nova. */
    public static void registrar(LocalVigente vigente, String xmlAnterior, TConfigEquip nova, Integer idUsuario) {
        if (vigente == null || xmlAnterior == null || nova == null) return;
        try (Connection conn = Conexao.getConexao()) {
            Set<String> ignorar = new HashSet<>(Arrays.asList(
                ConfigChaveValor.obter(conn, CHAVE_IGNORAR, IGNORAR_PADRAO).split("\\s*,\\s*")));
            List<AlteracaoParametro> alts = HistoricoParametros.compararConfigEquip(xmlAnterior, serializar(nova), ignorar);
            HistoricoParametros.registrarSemFalhar(conn, HistoricoParametros.RECURSO_EQUIPAMENTO,
                String.valueOf(vigente.getIdLocal()), alts, new ContextoAlteracao(idUsuario, login(conn, idUsuario), null, "ConfigEquip"));
        } catch (Throwable t) {
            logger.warn("Histórico de ConfigEquip não gravado para o local " + vigente.getIdLocal() + ": " + t.getMessage());
        }
    }

    static String serializar(TConfigEquip ce) throws Exception {
        SerializadorXML ser = new SerializadorXML();
        ser.saveObject(ce, TConfigEquip.NAME_CLASS);
        StringBuffer buf = new StringBuffer();
        ser.saveToStringBuffer(buf);
        return buf.toString();
    }

    private static String login(Connection conn, Integer idUsuario) {
        if (idUsuario == null) return "sistema";
        try (PreparedStatement ps = conn.prepareStatement("SELECT usuario FROM dbo.sis_usuario WHERE id_usuario = ?")) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString(1) : null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}
