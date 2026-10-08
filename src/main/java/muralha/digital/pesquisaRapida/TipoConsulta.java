package muralha.digital.pesquisaRapida;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

@XmlEnum
public enum TipoConsulta {
    @XmlEnumValue("1")
    VEICULO(1),

    @XmlEnumValue("2")
    CPF(2),

    @XmlEnumValue("3")
    NOME(3);

    private final int codigo;

    TipoConsulta(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static TipoConsulta fromCodigo(int codigo) {
        for (TipoConsulta tipo : values()) {
            if (tipo.getCodigo() == codigo) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo de consulta inválido: " + codigo);
    }
}
