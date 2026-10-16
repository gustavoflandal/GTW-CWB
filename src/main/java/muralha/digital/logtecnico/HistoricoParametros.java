package muralha.digital.logtecnico;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Helper único do histórico de parâmetros (TR 5.4.7.6): compara valores anteriores e novos e
 * grava uma linha por parâmetro alterado em muralha.parametro_historico.
 */
public final class HistoricoParametros {

    private static final Logger logger = Logger.getLogger(HistoricoParametros.class);

    public static final String RECURSO_EQUIPAMENTO = "EQUIPAMENTO";
    public static final String RECURSO_CONFIG = "CONFIG_CHAVE_VALOR";
    public static final String RECURSO_KPI = "KPI";

    static final int MAX_VALOR = 1000;
    private static final Pattern ITEM = Pattern.compile("Item_\\d+");

    private HistoricoParametros() {}

    /** Alterações entre dois mapas parâmetro → valor (inclui parâmetros incluídos e removidos), em ordem alfabética. */
    public static List<AlteracaoParametro> comparar(Map<String, String> antes, Map<String, String> depois) {
        Set<String> chaves = new TreeSet<>();
        if (antes != null) chaves.addAll(antes.keySet());
        if (depois != null) chaves.addAll(depois.keySet());
        List<AlteracaoParametro> r = new ArrayList<>();
        for (String k : chaves) {
            String a = antes == null ? null : antes.get(k);
            String d = depois == null ? null : depois.get(k);
            if (!Objects.equals(a, d)) r.add(new AlteracaoParametro(k, a, d));
        }
        return r;
    }

    /** Uma única alteração, ou lista vazia se o valor não mudou. */
    public static List<AlteracaoParametro> comparar(String parametro, String antes, String depois) {
        return Objects.equals(antes, depois) ? Collections.emptyList()
            : Collections.singletonList(new AlteracaoParametro(parametro, antes, depois));
    }

    /**
     * Planifica o XML do {@code com.consilux.infra.SerializadorXML} (formato do ConfigEquip):
     * cada {@code <property name>} folha vira "caminho.do.campo" → valor; itens de coleção
     * entram como "colecao.Item_0.campo".
     */
    public static Map<String, String> planificarXmlSerializador(String xml) {
        Map<String, String> r = new TreeMap<>();
        if (xml == null || xml.trim().isEmpty()) return r;
        try {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            f.setExpandEntityReferences(false);
            DocumentBuilder b = f.newDocumentBuilder();
            Element raiz = b.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))).getDocumentElement();
            percorrer(raiz, "", r);
        } catch (Exception e) {
            throw new IllegalArgumentException("XML de configuração inválido.", e);
        }
        return r;
    }

    private static void percorrer(Element el, String prefixo, Map<String, String> r) {
        NodeList filhos = el.getChildNodes();
        for (int i = 0; i < filhos.getLength(); i++) {
            Node n = filhos.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            Element f = (Element) n;
            String tag = f.getTagName();
            if ("property".equals(tag)) {
                String nome = f.getAttribute("name");
                if (temElementos(f)) percorrer(f, prefixo + nome + ".", r);
                else r.put(prefixo + nome, f.getTextContent());
            } else if ("class".equals(tag) && ITEM.matcher(f.getAttribute("name")).matches()) {
                percorrer(f, prefixo + f.getAttribute("name") + ".", r);
            } else {
                percorrer(f, prefixo, r);
            }
        }
    }

    private static boolean temElementos(Element el) {
        NodeList filhos = el.getChildNodes();
        for (int i = 0; i < filhos.getLength(); i++) {
            if (filhos.item(i).getNodeType() == Node.ELEMENT_NODE) return true;
        }
        return false;
    }

    /**
     * Alterações entre duas versões de ConfigEquip, ignorando campos pelo nome final ou pelo
     * caminho completo (ex.: dataHoraConfiguracao, que muda a cada gravação).
     */
    public static List<AlteracaoParametro> compararConfigEquip(String xmlAntes, String xmlDepois, Collection<String> ignorar) {
        List<AlteracaoParametro> r = new ArrayList<>();
        for (AlteracaoParametro a : comparar(planificarXmlSerializador(xmlAntes), planificarXmlSerializador(xmlDepois))) {
            String p = a.getParametro();
            String ultimo = p.substring(p.lastIndexOf('.') + 1);
            if (ignorar != null && (ignorar.contains(p) || ignorar.contains(ultimo))) continue;
            r.add(a);
        }
        return r;
    }

    /** Grava as alterações (uma linha cada). Não faz nada se a lista estiver vazia. */
    public static void registrar(Connection conn, String tipoRecurso, String idRecurso,
            List<AlteracaoParametro> alteracoes, ContextoAlteracao ctx) throws SQLException {
        if (alteracoes == null || alteracoes.isEmpty()) return;
        try (PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO muralha.parametro_historico " +
                "(tipo_recurso, id_recurso, parametro, valor_anterior, valor_novo, id_usuario, login, ip, origem) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            for (AlteracaoParametro a : alteracoes) {
                ps.setString(1, tipoRecurso);
                ps.setString(2, truncar(idRecurso, 100));
                ps.setString(3, truncar(a.getParametro(), 200));
                ps.setString(4, truncar(a.getValorAnterior(), MAX_VALOR));
                ps.setString(5, truncar(a.getValorNovo(), MAX_VALOR));
                if (ctx.getIdUsuario() != null) ps.setInt(6, ctx.getIdUsuario());
                else ps.setNull(6, Types.INTEGER);
                ps.setString(7, truncar(ctx.getLogin(), 50));
                ps.setString(8, truncar(ctx.getIp(), 45));
                ps.setString(9, truncar(ctx.getOrigem(), 60));
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    /**
     * Como {@link #registrar}, mas nunca propaga erro: o histórico não pode impedir a gravação
     * do parâmetro (ex.: migração ainda não executada). A falha vai para o log.
     */
    public static void registrarSemFalhar(Connection conn, String tipoRecurso, String idRecurso,
            List<AlteracaoParametro> alteracoes, ContextoAlteracao ctx) {
        try {
            registrar(conn, tipoRecurso, idRecurso, alteracoes, ctx);
        } catch (Exception e) {
            logger.warn("Histórico de parâmetros não gravado (" + tipoRecurso + " " + idRecurso + "): " + e.getMessage());
        }
    }

    static String truncar(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}
