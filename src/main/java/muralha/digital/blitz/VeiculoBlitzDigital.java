package muralha.digital.blitz;

import java.util.Date;
import java.util.UUID;

public class VeiculoBlitzDigital {
    private UUID id;
    private String placa;
    private Date dataVeic;
    private int idLocal;
    private String descLocal;
    private int idPista;
    private int faixa;
    private Integer velocidade;
    private String classificacao;
    private int idBlitz;
    private String nomeBlitz;
    private int idUsuario;
    private Integer serieEquipamento;
    private String codigoEquipamento;
    
    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    
    public Date getDataVeic() { return dataVeic; }
    public void setDataVeic(Date dataVeic) { this.dataVeic = dataVeic; }
    
    public int getIdLocal() { return idLocal; }
    public void setIdLocal(int idLocal) { this.idLocal = idLocal; }
    
    public String getDescLocal() { return descLocal; }
    public void setDescLocal(String descLocal) { this.descLocal = descLocal; }
    
    public int getIdPista() { return idPista; }
    public void setIdPista(int idPista) { this.idPista = idPista; }
    
    public int getFaixa() { return faixa; }
    public void setFaixa(int faixa) { this.faixa = faixa; }
    
    public Integer getVelocidade() { return velocidade; }
    public void setVelocidade(Integer velocidade) { this.velocidade = velocidade; }
    
    public String getClassificacao() { return classificacao; }
    public void setClassificacao(String classificacao) { this.classificacao = classificacao; }
    
    public int getIdBlitz() { return idBlitz; }
    public void setIdBlitz(int idBlitz) { this.idBlitz = idBlitz; }
    
    public String getNomeBlitz() { return nomeBlitz; }
    public void setNomeBlitz(String nomeBlitz) { this.nomeBlitz = nomeBlitz; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public Integer getSerieEquipamento() { return serieEquipamento; }
    public void setSerieEquipamento(Integer serieEquipamento) { this.serieEquipamento = serieEquipamento; }
    
    public String getCodigoEquipamento() { return codigoEquipamento; }
    public void setCodigoEquipamento(String codigoEquipamento) { this.codigoEquipamento = codigoEquipamento; }
}