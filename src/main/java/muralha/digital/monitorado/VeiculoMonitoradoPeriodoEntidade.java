package muralha.digital.monitorado;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.UUID;
import java.time.LocalTime;

@XmlRootElement(name = "VeiculoMonitoradoPeriodo")
@XmlAccessorType(XmlAccessType.FIELD)
public class VeiculoMonitoradoPeriodoEntidade {

    private UUID id;
    private UUID idCadVeiculoMonitorado;
    private Integer diaSemana;
    
    @JsonFormat(pattern = "HH:mm")
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaInicio;

    @JsonFormat(pattern = "HH:mm")
    @XmlJavaTypeAdapter(LocalTimeAdapter.class)
    private LocalTime horaFim;
   
    private Date dataCadastro;
    private Integer idUsuario;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getIdCadVeiculoMonitorado() {
        return idCadVeiculoMonitorado;
    }

    public void setIdCadVeiculoMonitorado(UUID idCadVeiculoMonitorado) {
        this.idCadVeiculoMonitorado = idCadVeiculoMonitorado;
    }

    public Integer getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(Integer diaSemana) {
        this.diaSemana = diaSemana;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(LocalTime horaFim) {
        this.horaFim = horaFim;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}
