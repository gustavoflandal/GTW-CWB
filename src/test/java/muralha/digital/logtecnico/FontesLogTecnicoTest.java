package muralha.digital.logtecnico;

import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.testng.Assert.*;

/** Normalização de cada fonte do repositório de logs técnicos e montagem das consultas. */
public class FontesLogTecnicoTest {

    private static final Timestamp T = Timestamp.valueOf("2026-10-16 10:00:00");
    private static final ParametrosLogTecnico P = ParametrosLogTecnico.padrao();

    private static Map<String, Object> linha(Object... kv) {
        Map<String, Object> m = new HashMap<>();
        m.put("data_hora", T);
        for (int i = 0; i < kv.length; i += 2) m.put((String) kv[i], kv[i + 1]);
        return m;
    }

    private static FiltroLogTecnico filtro(EnumSet<CategoriaLog> cats, EnumSet<SeveridadeLog> sevs, Integer... locais) {
        return new FiltroLogTecnico(T, new Timestamp(T.getTime() + 86_400_000L),
            new HashSet<>(java.util.Arrays.asList(locais)), Collections.emptySet(), cats, sevs);
    }

    @Test
    public void capturaQuedaDeEnergiaAbrePeriodoComSeveridadeDoNivel() {
        RegistroLogTecnico r = new FontesLogTecnico.Captura().normalizar(linha("id", 10, "id_categoria", 3, "id_evento", 8,
            "id_nivel", 1, "mensagem", "Falha de energia! No-break acionado!", "usuario", "CONSILUX",
            "desc_evento", "Rede elétrica comercial sem energia. Uso de bateria.", "proprietario", "1000001", "id_local", 1));
        assertEquals(r.getCategoria(), CategoriaLog.ENERGIA);
        assertEquals(r.getSeveridade(), SeveridadeLog.ALERTA);
        assertEquals(r.getTransicao(), RegistroLogTecnico.Transicao.INICIO);
        assertEquals(r.getGrupo(), "ENERGIA");
        assertEquals(r.getIdLocal(), Integer.valueOf(1));
        assertEquals(r.getDescricao(), "Rede elétrica comercial sem energia. Uso de bateria.: Falha de energia! No-break acionado!");
        assertEquals(r.getIdReferencia(), "eventos_csx:10");
    }

    @Test
    public void capturaSemEquipamentoMostraProprietario() {
        RegistroLogTecnico r = new FontesLogTecnico.Captura().normalizar(linha("id", 1, "id_categoria", 2001, "id_evento", 2002,
            "id_nivel", 0, "proprietario", "administrador", "desc_evento", "Modificado um usuário."));
        assertNull(r.getIdLocal());
        assertEquals(r.getCategoria(), CategoriaLog.USUARIO);
        assertEquals(r.getSeveridade(), SeveridadeLog.INFO);
        assertTrue(r.getDescricao().startsWith("[administrador] "));
    }

    @Test
    public void capturaConsultaFiltraNivelECategoriaSemConcatenarEntradaDoUsuario() {
        FonteLog.Consulta c = new FontesLogTecnico.Captura().consulta(
            filtro(EnumSet.of(CategoriaLog.ENERGIA), EnumSet.of(SeveridadeLog.ERRO, SeveridadeLog.CRITICO), 7), P);
        assertTrue(c.sql.contains("lp.id_local IN (?)"));
        assertTrue(c.sql.contains("e.id_nivel IN (?,?)"));
        assertTrue(c.sql.contains("(e.id_categoria = 3 AND e.id_evento IN (6,7,8))"), c.sql);
        assertEquals(c.parametros.size(), 2 + 1 + 2);
        assertEquals(c.parametros.get(2), 7);
        assertEquals(c.parametros.subList(3, 5), java.util.Arrays.asList(2, 3));
    }

    @Test
    public void statusConexaoOfflineAbreEOnlineFecha() {
        FontesLogTecnico.StatusConexao f = new FontesLogTecnico.StatusConexao();
        RegistroLogTecnico off = f.normalizar(linha("id", 5, "id_local", 1, "status", 2));
        RegistroLogTecnico on = f.normalizar(linha("id", 6, "id_local", 1, "status", 1, "ip", "10.0.6.222"));
        assertEquals(off.getSeveridade(), SeveridadeLog.ALERTA);
        assertEquals(off.getTransicao(), RegistroLogTecnico.Transicao.INICIO);
        assertEquals(on.getTransicao(), RegistroLogTecnico.Transicao.FIM);
        assertEquals(on.getCategoria(), CategoriaLog.COMUNICACAO);
        assertTrue(on.getDescricao().contains("10.0.6.222"));
        assertEquals(off.getIdReferencia(), "status_conexao:5");
    }

    @Test
    public void statusEnergiaSoEntraQuandoACategoriaEASeveridadeBatem() {
        FontesLogTecnico.StatusEnergia f = new FontesLogTecnico.StatusEnergia();
        assertNull(f.consulta(filtro(EnumSet.of(CategoriaLog.HARDWARE), null), P));
        assertNull(f.consulta(filtro(null, EnumSet.of(SeveridadeLog.ERRO)), P));
        FonteLog.Consulta c = f.consulta(filtro(null, EnumSet.of(SeveridadeLog.ALERTA)), P);
        assertTrue(c.sql.contains("s.status IN (?)"));
        assertEquals(c.parametros.get(2), 2);
        RegistroLogTecnico r = f.normalizar(linha("id", 1, "id_local", 100, "status", 2));
        assertEquals(r.getCategoria(), CategoriaLog.ENERGIA);
        assertEquals(r.getGrupo(), "ENERGIA");
    }

    @Test
    public void manutencaoTrazInicioEConclusao() {
        Timestamp fim = Timestamp.valueOf("2026-10-16 12:00:00");
        RegistroLogTecnico r = new FontesLogTecnico.Manutencao().normalizar(linha("id", 3, "id_local", 5,
            "data_conclusao", fim, "descricao", "Troca de câmera", "status_desc", "Concluída", "usuario", "tecnico"));
        assertEquals(r.getCategoria(), CategoriaLog.INTERVENCAO);
        assertEquals(r.getDataHoraFim(), fim);
        assertEquals(r.getDescricao(), "Manutenção: Troca de câmera (Concluída)");
        assertEquals(r.getUsuario(), "tecnico");
    }

    @Test
    public void logAlertaUsaDominioLogTipoEExcluiMobilidade() {
        FontesLogTecnico.LogAlerta f = new FontesLogTecnico.LogAlerta();
        RegistroLogTecnico r = f.normalizar(linha("id", 85, "id_tipo", 22, "tipo_desc", "Sem energia (bateria)", "serial", "1000100", "id_local", 100));
        assertEquals(r.getCategoria(), CategoriaLog.ENERGIA);
        assertEquals(r.getSeveridade(), SeveridadeLog.ALERTA);
        assertEquals(r.getTransicao(), RegistroLogTecnico.Transicao.INICIO);
        FonteLog.Consulta c = f.consulta(filtro(null, null), P);
        assertFalse(c.parametros.contains(116), "tipo 116 (circulação irregular) não é log técnico");
        assertTrue(c.parametros.contains(22));
        assertNull(f.consulta(filtro(EnumSet.of(CategoriaLog.PROCESSAMENTO), null), P));
    }

    @Test
    public void disponibilidadeSoTransicoes() {
        FontesLogTecnico.Disponibilidade f = new FontesLogTecnico.Disponibilidade();
        FonteLog.Consulta c = f.consulta(filtro(null, null, 1, 2), P);
        assertTrue(c.sql.contains("LAG("));
        assertTrue(c.sql.contains("x.anterior <> x.disponivel"));
        assertEquals(c.parametros.size(), 4);
        RegistroLogTecnico r = f.normalizar(linha("id", 1, "id_local", 2, "disponivel", 0, "minutos_offline", 45));
        assertEquals(r.getTransicao(), RegistroLogTecnico.Transicao.INICIO);
        assertTrue(r.getDescricao().contains("45 min"));
    }

    @Test
    public void processosNaoFinalizadosViramErroEFicamForaComFiltroDeEquipamento() {
        FontesLogTecnico.Processos f = new FontesLogTecnico.Processos();
        assertNull(f.consulta(filtro(null, null, 1), P), "processos não têm equipamento");
        FonteLog.Consulta c = f.consulta(filtro(null, null), P);
        assertTrue(c.sql.contains("lp.data_finalizado IS NULL"));
        assertEquals(c.parametros.get(2), 30);
        assertNull(f.consulta(filtro(null, EnumSet.of(SeveridadeLog.INFO)), P), "modo padrão só mostra não finalizados");
        RegistroLogTecnico r = f.normalizar(linha("id", 9, "nome", "finaliza_importacao_imagens", "atrasado", 1,
            "data_atualizado", T));
        assertEquals(r.getSeveridade(), SeveridadeLog.ERRO);
        assertEquals(r.getCategoria(), CategoriaLog.PROCESSAMENTO);
        ParametrosLogTecnico nenhum = new ParametrosLogTecnico(31, ParametrosLogTecnico.ModoProcessos.NENHUM, 30, 50000, 10);
        assertNull(f.consulta(filtro(null, null), nenhum));
    }

    @Test
    public void slaSoViolacoes() {
        FonteLog.Consulta c = new FontesLogTecnico.SlaLatencia().consulta(filtro(null, null), P);
        assertTrue(c.sql.contains("a.violacao = 1"));
        RegistroLogTecnico r = new FontesLogTecnico.SlaLatencia().normalizar(linha("id", 1, "percentil95_ms", 5200,
            "threshold_ms", 4000, "total_passagens", 300, "locais_top", "[]"));
        assertEquals(r.getSeveridade(), SeveridadeLog.ALERTA);
        assertTrue(r.getDescricao().contains("5200 ms") && r.getDescricao().contains("4000 ms"));
        RegistroLogTecnico p = new FontesLogTecnico.SlaPreprocessamento().normalizar(linha("id", 2, "acima_72h", 10, "total_pendentes", 12));
        assertTrue(p.getDescricao().startsWith("10 registros pendentes"));
    }

    @Test
    public void auditoriaEHistoricoDeConfiguracao() {
        RegistroLogTecnico a = new FontesLogTecnico.Auditoria().normalizar(linha("id", 4, "usuario", "admin",
            "funcionalidade", "Integridade de imagens", "operacao", "exportar-csv", "id_registro", null, "descricao", "Período x"));
        assertEquals(a.getCategoria(), CategoriaLog.USUARIO);
        assertEquals(a.getDescricao(), "Integridade de imagens / exportar-csv: Período x");
        RegistroLogTecnico h = new FontesLogTecnico.ConfigChaveValorHist().normalizar(linha("id", 361, "evento",
            "chamado_fechamento_auto_dias: 4 -> 5", "usuario", "gestor"));
        assertEquals(h.getCategoria(), CategoriaLog.PARAMETRO);
        assertEquals(h.getDescricao(), "Parâmetro alterado: chamado_fechamento_auto_dias: 4 -> 5");
    }

    @Test
    public void parametroHistoricoDeEquipamentoTemIdLocal() {
        FontesLogTecnico.ParametroHistorico f = new FontesLogTecnico.ParametroHistorico();
        RegistroLogTecnico r = f.normalizar(linha("id", 1, "tipo_recurso", "EQUIPAMENTO", "id_recurso", "201",
            "parametro", "velocidadeMaxima", "valor_anterior", "60", "valor_novo", "50", "usuario", "tecnico"));
        assertEquals(r.getIdLocal(), Integer.valueOf(201));
        assertEquals(r.getDescricao(), "EQUIPAMENTO 201: velocidadeMaxima de \"60\" para \"50\"");
        RegistroLogTecnico k = f.normalizar(linha("id", 2, "tipo_recurso", "KPI", "id_recurso", "3",
            "parametro", "threshold_ok", "valor_anterior", null, "valor_novo", "80.0"));
        assertNull(k.getIdLocal());
        assertTrue(k.getDescricao().contains("(vazio)"));
        FonteLog.Consulta c = f.consulta(filtro(null, null, 201), P);
        assertTrue(c.sql.contains("ph.tipo_recurso = ?"));
        assertTrue(c.parametros.contains("201"));
    }

    @Test
    public void fontesSemEquipamentoSaemQuandoFiltraEquipamento() {
        FiltroLogTecnico comEquip = filtro(null, null, 1);
        assertNull(new FontesLogTecnico.SlaLatencia().consulta(comEquip, P));
        assertNull(new FontesLogTecnico.SlaPreprocessamento().consulta(comEquip, P));
        assertNull(new FontesLogTecnico.Auditoria().consulta(comEquip, P));
        assertNull(new FontesLogTecnico.ConfigChaveValorHist().consulta(comEquip, P));
    }

    @Test
    public void todasAsFontesTemOrigemETabelaDistintas() {
        java.util.Set<String> origens = new HashSet<>(), tabelas = new HashSet<>();
        for (FonteLog f : FontesLogTecnico.todas()) {
            assertTrue(origens.add(f.origem()));
            assertTrue(tabelas.add(f.tabela()));
            assertFalse(FontesLogTecnico.categoriasPossiveis(f).isEmpty());
        }
        assertEquals(origens.size(), 12);
    }
}
