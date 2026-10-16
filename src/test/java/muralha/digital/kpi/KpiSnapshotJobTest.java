package muralha.digital.kpi;

import org.testng.Assert;
import org.testng.annotations.Test;

public class KpiSnapshotJobTest {

    @Test
    public void statusOkQuandoValorAcimaThresholdOk() {
        Assert.assertEquals(KpiSnapshotJob.calcularStatus(1500, 1000, 100), "OK");
    }

    @Test
    public void statusWarnQuandoValorEntreThresholds() {
        Assert.assertEquals(KpiSnapshotJob.calcularStatus(500, 1000, 100), "WARN");
    }

    @Test
    public void statusCritQuandoValorAbaixoThresholdWarn() {
        Assert.assertEquals(KpiSnapshotJob.calcularStatus(50, 1000, 100), "CRIT");
    }

    @Test
    public void statusOkInvertidoQuandoThresholdOkMenorQueWarn() {
        Assert.assertEquals(KpiSnapshotJob.calcularStatus(3, 5, 10), "OK");
    }

    @Test
    public void statusWarnInvertido() {
        Assert.assertEquals(KpiSnapshotJob.calcularStatus(7, 5, 10), "WARN");
    }

    @Test
    public void statusCritInvertido() {
        Assert.assertEquals(KpiSnapshotJob.calcularStatus(15, 5, 10), "CRIT");
    }

    @Test
    public void statusOkValorExatoNoThreshold() {
        Assert.assertEquals(KpiSnapshotJob.calcularStatus(1000, 1000, 100), "OK");
    }

    @Test
    public void statusOkValorExatoNoThresholdInvertido() {
        Assert.assertEquals(KpiSnapshotJob.calcularStatus(5, 5, 10), "OK");
    }
}
