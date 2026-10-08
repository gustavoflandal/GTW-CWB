package muralha.digital.registroDeFato;

import java.util.List;

public class VeiculosDiferencialDTO {
    private List<VeiculoDTO> adicionados;
    private List<VeiculoDTO> editados;
    private List<VeiculoDTO> removidos;

    // Getters e Setters
    public List<VeiculoDTO> getAdicionados() {
        return adicionados;
    }

    public void setAdicionados(List<VeiculoDTO> adicionados) {
        this.adicionados = adicionados;
    }

    public List<VeiculoDTO> getEditados() {
        return editados;
    }

    public void setEditados(List<VeiculoDTO> editados) {
        this.editados = editados;
    }

    public List<VeiculoDTO> getRemovidos() {
        return removidos;
    }

    public void setRemovidos(List<VeiculoDTO> removidos) {
        this.removidos = removidos;
    }
}
