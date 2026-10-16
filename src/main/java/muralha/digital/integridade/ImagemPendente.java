package muralha.digital.integridade;

import java.sql.Timestamp;

/** Imagem de passagem ainda não conferida, com os dados da passagem necessários à conferência. */
public final class ImagemPendente {

    private final String idImagem;
    private final String idVeiculoTempoReal;
    private final int indiceImagem;
    private final int idLocal;
    private final Timestamp dtCaptura;
    private final Timestamp dtRecepcao;
    private final String idCaptura;
    private final byte[] imagem;

    public ImagemPendente(String idImagem, String idVeiculoTempoReal, int indiceImagem, int idLocal,
            Timestamp dtCaptura, Timestamp dtRecepcao, String idCaptura, byte[] imagem) {
        this.idImagem = idImagem;
        this.idVeiculoTempoReal = idVeiculoTempoReal;
        this.indiceImagem = indiceImagem;
        this.idLocal = idLocal;
        this.dtCaptura = dtCaptura;
        this.dtRecepcao = dtRecepcao;
        this.idCaptura = idCaptura;
        this.imagem = imagem;
    }

    public String getIdImagem() { return idImagem; }

    public String getIdVeiculoTempoReal() { return idVeiculoTempoReal; }

    public int getIndiceImagem() { return indiceImagem; }

    public int getIdLocal() { return idLocal; }

    /** veiculo_tempo_real.data */
    public Timestamp getDtCaptura() { return dtCaptura; }

    /** veiculo_tempo_real.data_importado (pode ser nulo) */
    public Timestamp getDtRecepcao() { return dtRecepcao; }

    /** veiculo_tempo_real.id_captura, chave de ia.veiculo_caracteristica (pode ser nulo) */
    public String getIdCaptura() { return idCaptura; }

    public byte[] getImagem() { return imagem; }
}
