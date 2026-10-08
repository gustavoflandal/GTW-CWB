package muralha.digital.blitz;

import java.sql.Timestamp;

public class BlitzImagemAbordagem {

    private Long id;
    private Long id_abordagem;
    private String nome_arquivo_original;
    private String caminho_arquivo;
    private String tipo_arquivo;
    private Integer id_usuario;
    private Timestamp data_criacao;

    public BlitzImagemAbordagem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId_abordagem() {
        return id_abordagem;
    }

    public void setId_abordagem(Long id_abordagem) {
        this.id_abordagem = id_abordagem;
    }

    public String getNome_arquivo_original() {
        return nome_arquivo_original;
    }

    public void setNome_arquivo_original(String nome_arquivo_original) {
        this.nome_arquivo_original = nome_arquivo_original;
    }

    public String getCaminho_arquivo() {
        return caminho_arquivo;
    }

    public void setCaminho_arquivo(String caminho_arquivo) {
        this.caminho_arquivo = caminho_arquivo;
    }

    public String getTipo_arquivo() {
        return tipo_arquivo;
    }

    public void setTipo_arquivo(String tipo_arquivo) {
        this.tipo_arquivo = tipo_arquivo;
    }

    public Integer getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(Integer id_usuario) {
        this.id_usuario = id_usuario;
    }

    public Timestamp getData_criacao() {
        return data_criacao;
    }

    public void setData_criacao(Timestamp data_criacao) {
        this.data_criacao = data_criacao;
    }
}
