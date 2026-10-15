package muralha.digital.chamado;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static muralha.digital.chamado.StatusChamado.*;

/** Tabela de transicoes da spec §1.2. Pura: sem banco, sem sessao. */
public final class MaquinaEstados {

    private MaquinaEstados() {
    }

    public static StatusChamado destino(StatusChamado atual, AcaoChamado acao,
                                        Set<PerfilChamado> perfis, boolean autor) {
        boolean equipe = perfis.contains(PerfilChamado.EQUIPE);
        boolean gestor = perfis.contains(PerfilChamado.GESTOR);
        switch (acao) {
            case ASSUMIR:
                if (equipe && (atual == ABERTO || atual == EM_ATENDIMENTO)) return atual;
                break;
            case INICIAR:
                if (equipe && atual == ABERTO) return EM_ATENDIMENTO;
                break;
            case AGUARDAR:
                if (equipe && atual == EM_ATENDIMENTO) return AGUARDANDO_SOLICITANTE;
                break;
            case RESPONDER:
                if ((autor || gestor) && atual == AGUARDANDO_SOLICITANTE) return EM_ATENDIMENTO;
                break;
            case RESOLVER:
                if (equipe && atual == EM_ATENDIMENTO) return RESOLVIDO;
                break;
            case AVALIAR:
                if (autor && atual == RESOLVIDO) return FECHADO;
                break;
            case REABRIR:
                if (autor && atual == RESOLVIDO) return EM_ATENDIMENTO;
                break;
            case CANCELAR:
                if (autor && atual == ABERTO) return CANCELADO;
                if (equipe && (atual == ABERTO || atual == EM_ATENDIMENTO || atual == AGUARDANDO_SOLICITANTE)) {
                    return CANCELADO;
                }
                break;
            case RECLASSIFICAR:
                if (equipe && !atual.isTerminal()) return atual;
                break;
            default:
                break;
        }
        throw new RegraChamadoException("Ação \"" + acao.getCodigo() + "\" não permitida neste momento.");
    }

    public static List<AcaoChamado> permitidas(StatusChamado atual, Set<PerfilChamado> perfis, boolean autor) {
        List<AcaoChamado> acoes = new ArrayList<>();
        for (AcaoChamado a : AcaoChamado.values()) {
            try {
                destino(atual, a, perfis, autor);
                acoes.add(a);
            } catch (RegraChamadoException ignorada) {
                // acao nao permitida: fica fora da lista
            }
        }
        return acoes;
    }
}
