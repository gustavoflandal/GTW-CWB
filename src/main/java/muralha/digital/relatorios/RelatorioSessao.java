
package muralha.digital.relatorios;

import muralha.digital.relatorios.RelatorioSessaoUsuario;


import com.consilux.infra.Relatorio;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.google.gwt.rpc.server.Pair;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RelatorioSessao {

    String dataInicial;
    String dataFinal;

    public RelatorioSessao(String dataInicial, String dataFinal) throws SQLException { 
        this.dataInicial = dataInicial;
        this.dataFinal   = dataFinal;
    }

    public List<RelatorioSessaoUsuario> getDados() throws ConexaoException {
        List<RelatorioSessaoUsuario> dados = new ArrayList<>();
        
        String sql =    "select " + 
                        "    sllu.id_usuario, " + 
                        "    su.nome, " + 
                        "    su.email, " + 
                        "    su.usuario, " + 
                        "    sllu.data_logon, " + 
                        "    sllu.data_logoff " + 
                        "from " + 
                        "    sis_logon_logoff_usuario sllu " + 
                        "    left join " + 
                        "        sis_usuario su " + 
                        "        on su.id_usuario = sllu.id_usuario " + 
                        "where " + 
                        "    sllu.data_logon > '" + this.dataInicial + "'" + 
                        "    and ( sllu.data_logoff < '" + this.dataFinal + "'" +
                        "    or sllu.data_logoff is null ) " + 
                        "order by " + 
                        "    data_logon;";

        try (Connection conn = Conexao.getConexao();
             Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                RelatorioSessaoUsuario usuario = new RelatorioSessaoUsuario(
                    rs.getInt("id_usuario"),
                    rs.getString("nome"),
                    rs.getString("email"),
                    rs.getString("usuario"),
                    rs.getString("data_logon"),
                    rs.getString("data_logoff")
                );
                // usuario.printInfo();
                dados.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dados;
    }

    public List<Map.Entry<String, String>> getDadosNavegacao(int usuario, String dataInicial, String dataFinal) throws ConexaoException {
        List<Map.Entry<String, String>> dados = new ArrayList<Map.Entry<String, String>>();

        String sql =    " select sl.descricao, sl.data " + 
                        " from sis_log sl " +
                        " where id_usuario = " + usuario +
                        "    and sl.data between '" + dataInicial + "' " +
                        "    and '" + dataFinal + "'" +
                        " order by sl.data;";

        try (Connection conn = Conexao.getConexao();
             Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

                    String descricao = null;

                    while (rs.next()) {
                        // dados.add(new java.util.AbstractMap.SimpleEntry<>(
                        //     rs.getString("descricao"), 
                        //     rs.getString("data")
                        // ));

                        String descricao_aux = rs.getString("descricao");
                        // String data_aux      = rs.getString("data");

                        if (!descricao_aux.equals(descricao)) {
                            descricao = descricao_aux;
                            // data      = data_aux;
                            dados.add(new java.util.AbstractMap.SimpleEntry<>(
                                descricao, 
                                rs.getString("data")
                            ));
                        }
                        
                    }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dados;
    }
}