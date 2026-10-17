package muralha.digital.notificacao;

import org.testng.annotations.Test;

import java.util.UUID;

import static org.testng.Assert.*;

public class RetryNotificacaoTest {

    @Test
    public void statusNaoEnviadoConhecido() {
        UUID naoEnviado = StatusNotificacao.Status.NAO_ENVIADO.GetID();
        assertNotNull(naoEnviado);
        assertEquals(naoEnviado, UUID.fromString("65453CA3-6E31-4740-B082-531306AD7016"));
    }

    @Test
    public void statusPendenteConhecido() {
        UUID pendente = StatusNotificacao.Status.PENDENTE.GetID();
        assertNotNull(pendente);
        assertEquals(pendente, UUID.fromString("99AF55C6-2446-4B98-BBCD-83663C504C79"));
    }

    @Test
    public void statusEnviadoConhecido() {
        UUID enviado = StatusNotificacao.Status.ENVIADO.GetID();
        assertNotNull(enviado);
        assertEquals(enviado, UUID.fromString("AE081E1E-A323-41A7-8428-55A0B398E545"));
    }

    @Test
    public void getValueRetornaPendentePorUuid() {
        StatusNotificacao.Status s = StatusNotificacao.Status.GetValue(
            UUID.fromString("99AF55C6-2446-4B98-BBCD-83663C504C79"));
        assertEquals(s, StatusNotificacao.Status.PENDENTE);
    }

    @Test
    public void getValueRetornaNaoEnviadoPorUuid() {
        StatusNotificacao.Status s = StatusNotificacao.Status.GetValue(
            UUID.fromString("65453CA3-6E31-4740-B082-531306AD7016"));
        assertEquals(s, StatusNotificacao.Status.NAO_ENVIADO);
    }

    @Test
    public void getValueRetornaNoneParaUuidDesconhecido() {
        StatusNotificacao.Status s = StatusNotificacao.Status.GetValue(
            UUID.fromString("00000000-0000-0000-0000-000000000000"));
        assertEquals(s, StatusNotificacao.Status.None);
    }

    @Test
    public void compareIdentificaStatusCorreto() {
        assertTrue(StatusNotificacao.Status.PENDENTE.Compare(
            UUID.fromString("99AF55C6-2446-4B98-BBCD-83663C504C79")));
        assertFalse(StatusNotificacao.Status.PENDENTE.Compare(
            UUID.fromString("AE081E1E-A323-41A7-8428-55A0B398E545")));
    }

    @Test
    public void jobRetryInstanciavelSemErro() {
        JobRetryNotificacao job = new JobRetryNotificacao();
        assertNotNull(job);
    }
}
