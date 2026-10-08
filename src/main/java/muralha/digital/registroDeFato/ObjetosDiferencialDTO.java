package muralha.digital.registroDeFato;

import java.util.List;

public class ObjetosDiferencialDTO {
    private List<RegistroFatoObjeto> adicionados;
    private List<RegistroFatoObjeto> editados;
    private List<RegistroFatoObjeto> removidos;

    // Getters e Setters
    public List<RegistroFatoObjeto> getAdicionados() {
        return adicionados;
    }

    public void setAdicionados(List<RegistroFatoObjeto> adicionados) {
        this.adicionados = adicionados;
    }

    public List<RegistroFatoObjeto> getEditados() {
        return editados;
    }

    public void setEditados(List<RegistroFatoObjeto> editados) {
        this.editados = editados;
    }

    public List<RegistroFatoObjeto> getRemovidos() {
        return removidos;
    }

    public void setRemovidos(List<RegistroFatoObjeto> removidos) {
        this.removidos = removidos;
    }
}
