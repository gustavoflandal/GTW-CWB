package muralha.digital.relatorios;

import java.text.SimpleDateFormat;

public class RelatorioSessaoUsuario {
    
    private final int id_usuario;
    private final String nome;
    private final String email;
    private final String login;
    private final String data_hora_login;
    private final String data_hora_logout;
    private final String duracao_sessao;

    public RelatorioSessaoUsuario(int id_usuario, String nome, String email, String login, String data_hora_login, String data_hora_logout) {
        this.id_usuario       = id_usuario;
        this.nome             = nome;
        this.email            = email;
        this.login            = login;
        this.data_hora_login  = data_hora_login;
        this.data_hora_logout = data_hora_logout;
        
        this.duracao_sessao   = calcularDuracaoSessao(data_hora_login, data_hora_logout);
    }

    /**
     * Calcula a duração da sessão em formato HH:MM:SS.
     * @param inicio Horário de início da sessão no formato "YYYY-MM-DD HH:mm:ss"
     * @param fim Horário de fim da sessão no formato "YYYY-MM-DD HH:mm:ss" ou null
     * @return Duração da sessão no formato "HH:MM:SS" ou "??:??:??" se fim for null
     */
    public static String calcularDuracaoSessao(String inicio, String fim) {
        if (fim == null || fim.trim().isEmpty()) { return null; }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            java.util.Date dataInicio = sdf.parse(inicio);
            java.util.Date dataFim    = sdf.parse(fim);
            
            long diffMillis = dataFim.getTime() - dataInicio.getTime();
            
            if (diffMillis < 0) return null;
            
            long segundos = diffMillis / 1000;
            long horas    = segundos / 3600;
            long minutos  = (segundos % 3600) / 60;
            long seg      = segundos % 60;
            
            return String.format("%02d:%02d:%02d", horas, minutos, seg);
        } catch (Exception e) {
            return null;
        }
    }

    public void printInfo() {
        System.out.println("ID Usuário: " + id_usuario);
        System.out.println("Nome: " + nome);
        System.out.println("Email: " + email);
        System.out.println("Login: " + login);
        System.out.println("Data/Hora Login: " + data_hora_login);
        System.out.println("Data/Hora Logout: " + data_hora_logout);
        System.out.println("Duração Sessão: " + duracao_sessao);
    }

    public int getIdUsuario()         { return id_usuario;       }
    public String getNome()           { return nome;             }
    public String getLogin()          { return login;            }
    public String getDataHoraLogin()  { return data_hora_login;  }
    public String getDataHoraLogout() { return data_hora_logout; }
    public String getDuracaoSessao()  { return duracao_sessao;   }

}