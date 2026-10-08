package muralha.digital.registroDeFato;

import java.util.List;

public class GruposDiferencialDTO {
    private List<Integer> adicionados;
    private List<RegistroFatoObjeto> editados;
    private List<Integer> removidos;

    // Getters e Setters
    public List<Integer> getAdicionados() {
        return adicionados;
    }

    public void setAdicionados(List<Integer> adicionados) {
        this.adicionados = adicionados;
    }

    public List<Integer> getRemovidos() {
        return removidos;
    }

    public void setRemovidos(List<Integer> removidos) {
        this.removidos = removidos;
    }

	public List<RegistroFatoObjeto> getEditados() {
		return editados;
	}

	public void setEditados(List<RegistroFatoObjeto> editados) {
		this.editados = editados;
	}      
}
