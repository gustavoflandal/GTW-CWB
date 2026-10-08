package muralha.digital.registroDeFato;

import javax.xml.bind.annotation.*;
import java.sql.Timestamp;
import java.util.Date;

@XmlRootElement(name = "anotacao")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatoAnotacao {

    private Integer id;

    @XmlElement(name = "idRegistroFato")
    private Long idRegistroFato;

    private String texto;

    private Integer idUsuario;          // id_usuario da tabela

    private Date dataCriacao; 
    
    private String nomeUsuario;

    // GETTERS / SETTERS

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

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }
    
    public boolean equalsConteudo(RegistroDeFatoAnotacao outro) {
        if (outro == null) return false;

        return equalsNullable(this.texto, outro.texto)
            && equalsNullable(this.idUsuario, outro.idUsuario)
            && equalsNullable(this.dataCriacao, outro.dataCriacao)
            && equalsNullable(this.nomeUsuario, outro.nomeUsuario);
    }

    private boolean equalsNullable(Object x, Object y) {
        return (x == null ? y == null : x.equals(y));
    }
}
