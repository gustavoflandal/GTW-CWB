package muralha.digital.blitz;

import java.sql.Timestamp;

public class BlitzAbordagem {
    private Long id;
    private Integer id_blitz_digital;
    private Integer id_agente;
    private Integer id_resultado;
    private String id_alerta;
    private Integer id_local;
    private String placa_veiculo;
    private Timestamp data_abordagem;
    private Double latitude;
    private Double longitude;
    private Integer id_status;
    private String status;
    private String motivo_cancelamento;
    private String observacoes;
    private Long id_registro_fato;
    private Timestamp data_criacao;
    private String origemAbordagem;
    private String id_veiculo_tempo_real;
    private String cor_veiculo;
    private String tipo_veiculo;
    private String modelo_veiculo;
    private String marca_veiculo;
    
    public BlitzAbordagem() {
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public String getCor_veiculo() {
        return cor_veiculo;
    }

    public void setCor_veiculo(String cor_veiculo) {
        this.cor_veiculo = cor_veiculo;
    }

    public String getTipo_veiculo() {
        return tipo_veiculo;
    }

    public void setTipo_veiculo(String tipo_veiculo) {
        this.tipo_veiculo = tipo_veiculo;
    }

    public String getModelo_veiculo() {
        return modelo_veiculo;
    }

    public void setModelo_veiculo(String modelo_veiculo) {
        this.modelo_veiculo = modelo_veiculo;
    }

    public String getMarca_veiculo() {
        return marca_veiculo;
    }

    public void setMarca_veiculo(String marca_veiculo) {
        this.marca_veiculo = marca_veiculo;
    }
    
    public Integer getId_blitz_digital() {
        return id_blitz_digital;
    }
    
    public void setId_blitz_digital(Integer id_blitz_digital) {
        this.id_blitz_digital = id_blitz_digital;
    }
    
    public Integer getId_agente() {
        return id_agente;
    }
    
    public void setId_agente(Integer id_agente) {
        this.id_agente = id_agente;
    }
    
    public String getId_alerta() {
        return id_alerta;
    }
    
    public void setId_alerta(String id_alerta) {
        this.id_alerta = id_alerta;
    }
    
    public Integer getId_local() {
        return id_local;
    }
    
    public void setId_local(Integer id_local) {
        this.id_local = id_local;
    }
    
    public String getPlaca_veiculo() {
        return placa_veiculo;
    }
    
    public void setPlaca_veiculo(String placa_veiculo) {
        this.placa_veiculo = placa_veiculo;
    }
    
    public Timestamp getData_abordagem() {
        return data_abordagem;
    }
    
    public void setData_abordagem(Timestamp data_abordagem) {
        this.data_abordagem = data_abordagem;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Integer getId_status() {
        return id_status;
    }

    public Integer getId_resultado() {
        return id_resultado;
    }

    public void setId_resultado(Integer id_resultado) {
        this.id_resultado = id_resultado;
    }
    
    public void setId_status(Integer id_status) {
        this.id_status = id_status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMotivo_cancelamento() {
        return motivo_cancelamento;
    }
    
    public void setMotivo_cancelamento(String motivo_cancelamento) {
        this.motivo_cancelamento = motivo_cancelamento;
    }
    
    public String getObservacoes() {
        return observacoes;
    }
    
    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
    
    public Long getId_registro_fato() {
        return id_registro_fato;
    }
    
    public void setId_registro_fato(Long id_registro_fato) {
        this.id_registro_fato = id_registro_fato;
    }
    
    public Timestamp getData_criacao() {
        return data_criacao;
    }
    
    public void setData_criacao(Timestamp data_criacao) {
        this.data_criacao = data_criacao;
    }

    public String getOrigemAbordagem() {
        return origemAbordagem;
    }

    public void setOrigemAbordagem(String origemAbordagem) {
        this.origemAbordagem = origemAbordagem;
    }

    public String getId_veiculo_tempo_real() {
        return id_veiculo_tempo_real;
    }

    public void setId_veiculo_tempo_real(String id_veiculo_tempo_real) {
        this.id_veiculo_tempo_real = id_veiculo_tempo_real;
    }
}