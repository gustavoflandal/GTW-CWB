package muralha.digital.ocr;

import java.sql.Timestamp;

/** Passagem ainda não avaliada pela regra de revisão. */
public final class PassagemPendente {

    private final String idVeiculoTempoReal;
    private final int idLocal;
    private final Timestamp data;
    private final Timestamp dataImportado;
    private final String idCaptura;
    private final String placa;
    private final String placaFrontal;

    public PassagemPendente(String idVeiculoTempoReal, int idLocal, Timestamp data, Timestamp dataImportado,
            String idCaptura, String placa, String placaFrontal) {
        this.idVeiculoTempoReal = idVeiculoTempoReal;
        this.idLocal = idLocal;
        this.data = data;
        this.dataImportado = dataImportado;
        this.idCaptura = idCaptura;
        this.placa = placa;
        this.placaFrontal = placaFrontal;
    }

    public String getIdVeiculoTempoReal() { return idVeiculoTempoReal; }

    public int getIdLocal() { return idLocal; }

    public Timestamp getData() { return data; }

    public Timestamp getDataImportado() { return dataImportado; }

    public String getIdCaptura() { return idCaptura; }

    public String getPlaca() { return placa; }

    public String getPlacaFrontal() { return placaFrontal; }
}
