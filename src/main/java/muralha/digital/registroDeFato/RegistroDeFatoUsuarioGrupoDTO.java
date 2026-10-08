package muralha.digital.registroDeFato;

import java.util.List;

public class RegistroDeFatoUsuarioGrupoDTO {
    private List<Integer> grupos;   // ids de grupos
    private List<Integer> usuarios; // ids de usuários

    // Getters e Setters
    public List<Integer> getGrupos() { return grupos; }
    public void setGrupos(List<Integer> grupos) { this.grupos = grupos; }

    public List<Integer> getUsuarios() { return usuarios; }
    public void setUsuarios(List<Integer> usuarios) { this.usuarios = usuarios; }
}
