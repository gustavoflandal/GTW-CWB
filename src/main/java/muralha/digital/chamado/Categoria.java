package muralha.digital.chamado;

import com.google.gson.JsonObject;

public class Categoria {
    public int id;
    public String nome;
    public Integer idPai;
    public String nomePai;
    /** Efetivo: subcategoria herda o valor da categoria (spec §2.1). */
    public boolean exigeEquipamento;
    public boolean ativo = true;

    public void validar() {
        if (nome == null || nome.trim().isEmpty() || nome.length() > 100) {
            throw new RegraChamadoException("Nome obrigatório (até 100 caracteres).");
        }
    }

    public JsonObject json() {
        JsonObject o = new JsonObject();
        o.addProperty("id", id);
        o.addProperty("nome", nome);
        o.addProperty("idPai", idPai);
        o.addProperty("nomePai", nomePai);
        o.addProperty("exigeEquipamento", exigeEquipamento);
        o.addProperty("ativo", ativo);
        return o;
    }
}
