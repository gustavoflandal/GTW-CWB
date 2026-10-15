package muralha.digital.chamado;

import com.google.gson.JsonObject;

public class Prioridade {
    public int id;
    public String codigo;
    public String nome;
    public int ordem;
    public int minutosAtendimento;
    public int minutosResolucao;
    public String cor;
    public boolean ativo = true;

    public void validar() {
        if (codigo == null || !codigo.matches("^[A-Z_]{2,20}$")) {
            throw new RegraChamadoException("Código deve ter de 2 a 20 letras maiúsculas ou _.");
        }
        if (nome == null || nome.trim().isEmpty() || nome.length() > 50) {
            throw new RegraChamadoException("Nome obrigatório (até 50 caracteres).");
        }
        if (minutosAtendimento <= 0 || minutosResolucao < minutosAtendimento) {
            throw new RegraChamadoException("Prazos inválidos: atendimento > 0 e resolução ≥ atendimento.");
        }
        if (cor == null || !cor.matches("^#[0-9a-fA-F]{6}$")) {
            throw new RegraChamadoException("Cor deve estar no formato #RRGGBB.");
        }
        if (ordem < 1 || ordem > 99) {
            throw new RegraChamadoException("Ordem deve estar entre 1 e 99.");
        }
    }

    public JsonObject json() {
        JsonObject o = new JsonObject();
        o.addProperty("id", id);
        o.addProperty("codigo", codigo);
        o.addProperty("nome", nome);
        o.addProperty("ordem", ordem);
        o.addProperty("minutosAtendimento", minutosAtendimento);
        o.addProperty("minutosResolucao", minutosResolucao);
        o.addProperty("cor", cor);
        o.addProperty("ativo", ativo);
        return o;
    }
}
