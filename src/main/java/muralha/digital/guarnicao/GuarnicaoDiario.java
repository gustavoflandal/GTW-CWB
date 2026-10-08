package muralha.digital.guarnicao;

import java.sql.Date;
import java.sql.Timestamp;

import muralha.digital.acessos.Usuario;

public class GuarnicaoDiario {

    private int id;
    private int idGuarnicao;
    private Date data;
    private double quilometragem;
    private String horaIni;
    private String horaFim;
    private String setoresPatrulhados;
    private String meioTransporte;
    private Timestamp dataCadastro;
    private int idUsuario;

    private Guarnicao guarnicao;
    private Usuario usuario;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdGuarnicao() {
        return idGuarnicao;
    }

    public void setIdGuarnicao(int idGuarnicao) {
        this.idGuarnicao = idGuarnicao;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public double getQuilometragem() {
        return quilometragem;
    }

    public void setQuilometragem(double quilometragem) {
        this.quilometragem = quilometragem;
    }

    public String getHoraIni() {
        return horaIni;
    }

    public void setHoraIni(String horaIni) {
        this.horaIni = horaIni;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public String getSetoresPatrulhados() {
        return setoresPatrulhados;
    }

    public void setSetoresPatrulhados(String setoresPatrulhados) {
        this.setoresPatrulhados = setoresPatrulhados;
    }

    public String getMeioTransporte() {
        return meioTransporte;
    }

    public void setMeioTransporte(String meioTransporte) {
        this.meioTransporte = meioTransporte;
    }

    public Timestamp getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Timestamp dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Guarnicao getGuarnicao() {
        return guarnicao;
    }

    public void setGuarnicao(Guarnicao guarnicao) {
        this.guarnicao = guarnicao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
