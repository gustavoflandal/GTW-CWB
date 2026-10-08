package muralha.digital.registroDeFato;

import java.io.Serializable;

public class RegistroDeFatoDocumento implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;                    // identidade PK
    private Long idRegistroFato;          // FK para registro de fato
    private String tipo;                  // varchar(20), NOT NULL
    private String dirArquivo;            // varchar(200), NOT NULL
    private String detalhamento;          // varchar(300), NULL
    private Integer idAtendimento;        // int, NULL

    public RegistroDeFatoDocumento() {
    }

    // Getters e setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getIdRegistroFato() {
        return idRegistroFato;
    }

    public void setIdRegistroFato(Long idRegistroFato) {
        this.idRegistroFato = idRegistroFato;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDirArquivo() {
        return dirArquivo;
    }

    public void setDirArquivo(String dirArquivo) {
        this.dirArquivo = dirArquivo;
    }

    public String getDetalhamento() {
        return detalhamento;
    }

    public void setDetalhamento(String detalhamento) {
        this.detalhamento = detalhamento;
    }

    public Integer getIdAtendimento() {
        return idAtendimento;
    }

    public void setIdAtendimento(Integer idAtendimento) {
        this.idAtendimento = idAtendimento;
    }
    
    public boolean equalsConteudo(RegistroDeFatoDocumento outro) {
        if (outro == null) return false;
        return equalsNullable(tipo, outro.tipo)
            && equalsNullable(dirArquivo, outro.dirArquivo)
            && equalsNullable(detalhamento, outro.detalhamento)
            && equalsNullable(idAtendimento, outro.idAtendimento);
    }

    private boolean equalsNullable(Object a, Object b) {
        return a == null ? b == null : a.equals(b);
    }
}
