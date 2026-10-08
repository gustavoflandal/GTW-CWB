package muralha.digital.registroDeFato;

import java.util.List;

public class EnvolvidosDiferencialDTO {
    private List<EnvolvidoDTO> adicionados;
    private List<EnvolvidoDTO> editados;
    private List<EnvolvidoDTO> removidos;

    // Getters e Setters
    public List<EnvolvidoDTO> getAdicionados() {
        return adicionados;
    }

    public void setAdicionados(List<EnvolvidoDTO> adicionados) {
        this.adicionados = adicionados;
    }

    public List<EnvolvidoDTO> getEditados() {
        return editados;
    }

    public void setEditados(List<EnvolvidoDTO> editados) {
        this.editados = editados;
    }

    public List<EnvolvidoDTO> getRemovidos() {
        return removidos;
    }

    public void setRemovidos(List<EnvolvidoDTO> removidos) {
        this.removidos = removidos;
    }
}
