package muralha.digital.registroDeFato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

import muralha.digital.util.Paginacao;

@XmlRootElement(name = "RegistroDeFatos")
@XmlAccessorType(XmlAccessType.FIELD)
public class RegistroDeFatos {

	@XmlTransient
	private static Logger logger = LogManager.getLogger(RegistroDeFatos.class);

	@XmlElementWrapper(name = "ListaRegistroDeFatos")
	@XmlElement(name = "RegistroDeFato")
	private List<RegistroDeFato> listaRegistroDeFatos;

	private Paginacao paginacao;

	public List<RegistroDeFato> getListaRegistroDeFatos() {
		return listaRegistroDeFatos;
	}

	public void setListaBoletins(List<RegistroDeFato> listaRegistroDeFatos) {
		this.listaRegistroDeFatos = listaRegistroDeFatos;
	}

	public Paginacao getPaginacao() {
		return paginacao;
	}

	public void setPaginacao(Paginacao paginacao) {
		this.paginacao = paginacao;
	}

	public RegistroDeFatos() {
		super();
		this.listaRegistroDeFatos = new ArrayList<>();
	}

	public static RegistroDeFatos ObterListaRegistroDeFatos(Date dataIni, Date dataFim, Integer idStatus,
            Integer idTipoRegistro, String placa, String cpf, Integer idCidade, Integer origemBoletim, Integer comBoletim, 
            Integer infoFaltante, Integer incluirVeiculos, Integer incluirMonitorados, String nomeEnvolvido, 
            Integer operadorCadastro, Integer tipoRegistro, Integer naturezaRegistro, String filtroObjeto, 
            Date dataIniAlteracao, Date dataFimAlteracao, Integer chkFiltrosAvancados, 
            Date dataIniFato, Date dataFimFato, Integer tipoAcesso, Integer acessoPermitido, Integer idUsuarioLogado,
            Paginacao paginacao, String orderBy, String orderDir) throws ConexaoException, SQLException {

        RegistroDeFatos retorno = new RegistroDeFatos();
        Map<Long, RegistroDeFato> registrosMap = new LinkedHashMap<>();

        StringBuilder sbSQL = new StringBuilder();
        StringBuilder sbWhere = new StringBuilder(); 

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        boolean erro = false;
        String msgErro = "";

        // Gera a string de ordenação segura antes de montar a query
        String sqlOrdenacao = montarClausulaOrdenacao(orderBy, orderDir);

        try {
            int paramIndex = 1;
            Map<Integer, Object> mapaParametros = new LinkedHashMap<>();

            // --- MONTAGEM DOS FILTROS (WHERE) ---
            sbWhere.append(" WHERE 1=1 ");        

            if (dataIni != null) {
                sbWhere.append(" AND rf.data_criacao >= ? ");
                mapaParametros.put(paramIndex++, new java.sql.Timestamp(dataIni.getTime()));
            }
            if (dataFim != null) {
                sbWhere.append(" AND rf.data_criacao <= ? ");
                mapaParametros.put(paramIndex++, new java.sql.Timestamp(dataFim.getTime()));
            }
            if (idStatus != null) {
                sbWhere.append(" AND rf.id_status = ?");
                mapaParametros.put(paramIndex++, idStatus);
            }
            if (idTipoRegistro != null) {
                sbWhere.append(" AND rf.id_tipo = ?");
                mapaParametros.put(paramIndex++, idTipoRegistro);
            }
            if (idCidade != null) {
                sbWhere.append(" AND rfe.id_cidade = ?");
                mapaParametros.put(paramIndex++, idCidade);
            }
            
            if (placa != null && !placa.isEmpty() && (incluirVeiculos == null || incluirVeiculos == 1) && infoFaltante == null) {
                String[] placas = placa.split(",");
                List<String> placasLimpa = new ArrayList<>();
                for (String p : placas) {
                    if (p != null && !p.trim().isEmpty()) {
                        placasLimpa.add(p.trim().toUpperCase());
                    }
                }

                if (!placasLimpa.isEmpty()) {
                    sbWhere.append(" AND EXISTS ( ");
                    sbWhere.append("     SELECT 1 FROM muralha.registro_fato_veiculo rfv2 ");
                    sbWhere.append("     WHERE rfv2.id_registro_fato = rf.id ");
                    sbWhere.append("     AND rfv2.placa IN (");

                    for (int i = 0; i < placasLimpa.size(); i++) {
                        if (i > 0) sbWhere.append(", ");
                        sbWhere.append("?");
                        mapaParametros.put(paramIndex++, placasLimpa.get(i));
                    }
                    sbWhere.append(") ) ");
                }
            }
            
            if (cpf != null && !cpf.isEmpty() && infoFaltante == null) {
                sbWhere.append(" AND REPLACE(REPLACE(REPLACE(rfi.cpf, '.', ''), '-', ''), ' ', '') = ?");
                mapaParametros.put(paramIndex++, cpf.replaceAll("\\D", ""));
            }
            
            if(chkFiltrosAvancados == 1) {
                if (origemBoletim != null) {
                    sbWhere.append(" AND rfo.origem = ? AND rf.tem_boletim = 1");
                    mapaParametros.put(paramIndex++, origemBoletim);
                }
                if(comBoletim != null && origemBoletim == null) {
                    sbWhere.append(" AND rf.tem_boletim = ? ");
                    mapaParametros.put(paramIndex++, comBoletim);
                }
                if(operadorCadastro != null) {
                    sbWhere.append(" AND sisu.id_usuario = ? ");
                    mapaParametros.put(paramIndex++, operadorCadastro);
                }
                if(tipoRegistro != null) {
                    sbWhere.append(" AND rft.id = ? ");
                    mapaParametros.put(paramIndex++,  tipoRegistro);
                }
                if(naturezaRegistro != null) {
                    sbWhere.append(" AND rfn.id = ? ");
                    mapaParametros.put(paramIndex++,  naturezaRegistro);
                }
                if (dataIniAlteracao != null || dataFimAlteracao != null) {
                    sbWhere.append(" AND rfo.data_ultimo_historico IS NOT NULL ");
                }
                if (dataIniAlteracao != null) {
                    sbWhere.append(" AND rfo.data_ultimo_historico >= ? ");
                    mapaParametros.put(paramIndex++, new java.sql.Timestamp(dataIniAlteracao.getTime()));
                }
                if (dataFimAlteracao != null) {
                    sbWhere.append(" AND rfo.data_ultimo_historico <= ? ");
                    mapaParametros.put(paramIndex++, new java.sql.Timestamp(dataFimAlteracao.getTime()));
                }
                if (dataIniFato != null) {
                    sbWhere.append(" AND f.data_hora_evento >= ? ");
                    mapaParametros.put(paramIndex++, new java.sql.Timestamp(dataIniFato.getTime()));
                }
                if (dataFimFato != null) {
                    sbWhere.append(" AND f.data_hora_evento <= ? ");
                    mapaParametros.put(paramIndex++, new java.sql.Timestamp(dataFimFato.getTime()));
                }
                if(tipoAcesso != null) {
                    sbWhere.append(" AND rf.privado = ? ");
                    mapaParametros.put(paramIndex++, tipoAcesso);
                }
                
                if (acessoPermitido != null) {
                    sbWhere.append(" AND EXISTS ( ");
                    sbWhere.append("     SELECT 1 ");
                    sbWhere.append("     FROM muralha.registro_fato_usuario_grupo rfug_ap ");
                    sbWhere.append("     WHERE rfug_ap.id_registro_fato = rf.id ");
                    sbWhere.append("     AND rfug_ap.id_grupo = ? ");
                    sbWhere.append(" ) ");
                    mapaParametros.put(paramIndex++, acessoPermitido);
                }
                
                if(infoFaltante == null) {                                        
                    if(incluirVeiculos != null && incluirVeiculos == 0) {
                        sbWhere.append(" AND rfv.id is null ");
                    }
                    
                    if(incluirVeiculos != null && incluirVeiculos == 1) {
                        sbWhere.append(" AND rfv.id is not null ");
                    }        
                    
                    if(nomeEnvolvido != null && !nomeEnvolvido.trim().isEmpty()) {
                        sbWhere.append(" AND UPPER(rfi.nome) LIKE UPPER(?) ");
                        mapaParametros.put(paramIndex++, "%" + nomeEnvolvido + "%");
                    }    
                    
                    if(filtroObjeto != null && !filtroObjeto.trim().isEmpty()) {
                        sbWhere.append(" AND UPPER(rfobj.tipo ) LIKE UPPER(?) ");
                        mapaParametros.put(paramIndex++, "%" + filtroObjeto + "%");
                    }
                    if(incluirMonitorados != null && incluirMonitorados == 1) {
                        sbWhere.append(" AND cvm.id_registro_fato is not null ");
                    }
                } else {
                    sbWhere.append(" AND (  ");
                    sbWhere.append(" rfi.id is null ");
                    sbWhere.append(" OR  rfv.id is null ");
                    sbWhere.append(" OR rfobj.id is null ");
                    sbWhere.append(" ) ");
                }
            }

            sbWhere.append(" AND (");
            sbWhere.append("     EXISTS (");
            sbWhere.append("         SELECT 1 FROM muralha.registro_fato_usuario_grupo rfug");
            sbWhere.append("         WHERE rfug.id_registro_fato = rf.id");
            sbWhere.append("         AND (");
            sbWhere.append("             rfug.id_usuario = ?");
            sbWhere.append("             OR rfug.id_grupo IN (SELECT ug.id_grupo FROM dbo.sis_usuario_grupo ug WHERE ug.id_usuario = ?)");
            sbWhere.append("         )");
            sbWhere.append("     )");
            sbWhere.append("     OR");
            sbWhere.append("     (");
            sbWhere.append("         NOT EXISTS (SELECT 1 FROM muralha.registro_fato_usuario_grupo rfug WHERE rfug.id_registro_fato = rf.id)");
            sbWhere.append("         AND (rf.privado = 0 OR rf.id_usuario = ?)");
            sbWhere.append("     )");
            sbWhere.append(" )");

            mapaParametros.put(paramIndex++, idUsuarioLogado);
            mapaParametros.put(paramIndex++, idUsuarioLogado);
            mapaParametros.put(paramIndex++, idUsuarioLogado);

         // --- INICIO DA QUERY COM CTES ---
            sbSQL.append(" WITH PaginacaoCTE AS ( ");
            sbSQL.append("      SELECT rfo.id_registro_fato, ");
            sbSQL.append("      rf.data_criacao, rft.tipo_desc, rs.descricao, rfo.origem_descricao, sisu.nome, rf.id ");

            sbSQL.append("      FROM muralha.vw_registro_fato_origem rfo ");
            sbSQL.append("      INNER JOIN muralha.registro_fato rf ON rf.id = rfo.id_registro_fato ");
            sbSQL.append("      INNER JOIN dbo.sis_usuario sisu ON sisu.id_usuario = rf.id_usuario ");
            sbSQL.append("      INNER JOIN muralha.registro_fato_tipo rft ON rft.id = rf.id_tipo ");            
            sbSQL.append("      INNER JOIN muralha.registro_fato_natureza rfn ON rfn.id_registro_tipo = rft.id ");
            sbSQL.append("      INNER JOIN muralha.registro_fato_natureza_delituosa rfnd ON rfnd.id_registro_natureza = rfn.id ");
            sbSQL.append("      LEFT JOIN muralha.fato f ON f.id_registro_fato = rf.id");
            sbSQL.append("      LEFT JOIN muralha.registro_fato_status rs ON rs.id = rf.id_status "); 

            if (idCidade != null) {
                sbSQL.append("     LEFT JOIN muralha.registro_fato_endereco rfe ON rfe.id_registro_fato = rf.id ");
            }
            if (cpf != null && !cpf.isEmpty() || infoFaltante != null) {
                sbSQL.append("     LEFT JOIN muralha.registro_fato_individuo rfi ON rfi.id_registro_fato = rf.id ");
            }

            if(chkFiltrosAvancados == 1){            
                if (infoFaltante != null) {
                    sbSQL.append("     LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id ");
                    sbSQL.append("     LEFT JOIN muralha.registro_fato_objeto rfobj ON rfobj.id_registro_fato = rf.id ");
                }
                if(infoFaltante == null && incluirVeiculos != null && incluirVeiculos == 0) {
                    sbSQL.append("     LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id ");
                }
                if(infoFaltante == null && (incluirVeiculos == null || incluirVeiculos != 0)) {
                    sbSQL.append("     LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id ");
                    
                    if(incluirMonitorados == 1) {
                        sbSQL.append("     LEFT JOIN muralha.cad_veiculo_monitorado cvm ON cvm.id_registro_fato = rf.id ");
                    }
                }                
                if(filtroObjeto != null && !filtroObjeto.trim().isEmpty() && infoFaltante == null) {
                    sbSQL.append("     LEFT JOIN muralha.registro_fato_objeto rfobj ON rfobj.id_registro_fato = rf.id ");
                }
            }

            sbSQL.append(sbWhere.toString());

            //sbSQL.append(" GROUP BY rfo.id_registro_fato, rf.data_criacao, rft.tipo_desc, rs.descricao, rfo.origem_descricao, sisu.nome, rf.id, rf.data_evento, rf.data_encerramento ");

            sbSQL.append(sqlOrdenacao);

            sbSQL.append(paginacao.QueryPaginacao());
            sbSQL.append(" ), ");

            sbSQL.append(" ContagemCTE AS ( ");
            sbSQL.append("      SELECT COUNT(DISTINCT rfo.id_registro_fato ) AS total_registros ");
            sbSQL.append("      FROM muralha.vw_registro_fato_origem rfo ");
            sbSQL.append("      INNER JOIN muralha.registro_fato rf ON rf.id = rfo.id_registro_fato ");
            sbSQL.append("      INNER JOIN dbo.sis_usuario sisu ON sisu.id_usuario = rf.id_usuario ");
            sbSQL.append("      INNER JOIN muralha.registro_fato_tipo rft ON rft.id = rf.id_tipo ");
            sbSQL.append("      INNER JOIN muralha.registro_fato_natureza rfn ON rfn.id_registro_tipo = rft.id ");
            sbSQL.append("      INNER JOIN muralha.registro_fato_natureza_delituosa rfnd ON rfnd.id_registro_natureza = rfn.id ");
            sbSQL.append("      LEFT JOIN muralha.fato f ON f.id_registro_fato = rf.id");
            sbSQL.append("      LEFT JOIN muralha.registro_fato_status rs ON rs.id = rf.id_status "); // Mantém simetria, mas não afeta count

            // Repete os Joins condicionais para o Count
            if (idCidade != null) {
                sbSQL.append("     LEFT JOIN muralha.registro_fato_endereco rfe ON rfe.id_registro_fato = rf.id ");
            }
            if (cpf != null && !cpf.isEmpty() || infoFaltante != null) {
                sbSQL.append("     LEFT JOIN muralha.registro_fato_individuo rfi ON rfi.id_registro_fato = rf.id ");
            }
            
            if(chkFiltrosAvancados == 1){
                if (infoFaltante != null) {
                    sbSQL.append("     LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id ");
                    sbSQL.append("     LEFT JOIN muralha.registro_fato_objeto rfobj ON rfobj.id_registro_fato = rf.id ");
                }
                if(infoFaltante == null && incluirVeiculos != null && incluirVeiculos == 0) {
                    sbSQL.append("     LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id ");
                }
                if(infoFaltante == null && (incluirVeiculos == null || incluirVeiculos != 0)) {
                    sbSQL.append("     LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id ");
                    
                    if(incluirMonitorados == 1) {
                        sbSQL.append("     LEFT JOIN muralha.cad_veiculo_monitorado cvm ON cvm.id_registro_fato = rf.id ");
                    }
                }            
                if(filtroObjeto != null && !filtroObjeto.trim().isEmpty() && infoFaltante == null) {
                    sbSQL.append("     LEFT JOIN muralha.registro_fato_objeto rfobj ON rfobj.id_registro_fato = rf.id ");
                }
            }
            
            sbSQL.append(sbWhere.toString());
            sbSQL.append(" ) ");

            // --- QUERY FINAL DE SELEÇÃO ---
            sbSQL.append(" SELECT ");
            sbSQL.append(" rfo.origem, rfo.origem_descricao, rfo.data_ultimo_historico, ");
            sbSQL.append(" rf.id, rf.id_status, rs.descricao AS status, rf.id_tipo, rft.tipo_desc AS tipo_registro, rf.privado, ");
            sbSQL.append(" rf.data_criacao, rf.data_modificacao, rf.data_encerramento, rf.data_evento, rf.id_usuario, su.usuario AS usuario_criacao, su.nome AS nome_usuario_criacao,");
            sbSQL.append(" rfe.id AS endereco_id, rfe.id_cidade, rfe.bairro, rfe.rua, rfe.numero, rfe.cep, rfe.complemento,");
            sbSQL.append(" rfi.id AS individuo_id, rfi.nome AS individuo_nome, rfi.cpf AS individuo_cpf,");
            sbSQL.append(" rfv.id AS veiculo_id, rfv.placa AS veiculo_placa, rfv.marca AS veiculo_marca, rfv.cor AS veiculo_cor, rfv.modelo AS veiculo_modelo,");
            sbSQL.append(" c.id AS cidade_id, c.nome AS cidade_nome, c.id_estado,");
            sbSQL.append(" b.id AS boletim_id, b.id_situacao AS boletim_situacao, b.data_hora_evento, b.detalhamento,");
            sbSQL.append(" b.id_usuario AS boletim_id_usuario, b.data_criacao AS boletim_data_criacao,");
            sbSQL.append(" b.data_encerramento AS boletim_data_encerramento, b.permite_atendimento,");
            sbSQL.append(" bs.id AS boletim_situacao_id, bs.descricao AS boletim_situacao_descricao, ");
	    	sbSQL.append(" rfn.id AS natureza_id, ");
	    	sbSQL.append(" rfn.natureza_desc, ");
	    	sbSQL.append(" rfn.requer_bo AS natureza_requer_bo, ");
            sbSQL.append(" ct.total_registros ");

            sbSQL.append(" FROM muralha.vw_registro_fato_origem rfo");
            sbSQL.append(" INNER JOIN muralha.registro_fato rf ON rf.id = rfo.id_registro_fato ");
            sbSQL.append(" INNER JOIN dbo.sis_usuario sisu ON sisu.id_usuario = rf.id_usuario ");
            sbSQL.append(" INNER JOIN muralha.registro_fato_tipo rft ON rft.id = rf.id_tipo ");
            sbSQL.append(" INNER JOIN muralha.registro_fato_natureza rfn ON rfn.id_registro_tipo = rft.id ");
            sbSQL.append(" INNER JOIN muralha.registro_fato_natureza_delituosa rfnd ON rfnd.id_registro_natureza = rfn.id ");
            
            // JOIN com a PaginacaoCTE (que já filtrou e paginou os IDs)
            sbSQL.append(" INNER JOIN PaginacaoCTE pcte ON pcte.id_registro_fato = rf.id");
            sbSQL.append(" CROSS JOIN ContagemCTE ct");
            
            sbSQL.append(" LEFT JOIN muralha.registro_fato_status rs ON rs.id = rf.id_status");
            sbSQL.append(" LEFT JOIN sis_usuario su ON su.id_usuario = rf.id_usuario");
            sbSQL.append(" LEFT JOIN muralha.registro_fato_endereco rfe ON rfe.id_registro_fato = rf.id");
            sbSQL.append(" LEFT JOIN muralha.cidade c ON c.id = rfe.id_cidade");
            sbSQL.append(" LEFT JOIN muralha.registro_fato_individuo rfi ON rfi.id_registro_fato = rf.id");
            sbSQL.append(" LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id");
            sbSQL.append(" LEFT JOIN muralha.boletim b ON b.id_registro_fato = rf.id");
            sbSQL.append(" LEFT JOIN muralha.boletim_situacao bs ON bs.id = b.id_situacao");
            sbSQL.append(" LEFT JOIN muralha.registro_fato_objeto rfobj ON rfobj.id_registro_fato = rf.id");
            sbSQL.append(" LEFT JOIN muralha.cad_veiculo_monitorado cvm ON cvm.id_registro_fato = rf.id");
            sbSQL.append(" LEFT JOIN muralha.fato f ON f.id_registro_fato = rf.id");

            // Ordenação final para garantir a apresentação visual (já que o INNER JOIN poderia embaralhar)
            sbSQL.append(sqlOrdenacao);
            sbSQL.append(", rfi.id, rfv.id"); // Critérios secundários de desempate/agrupamento

            conn = Conexao.getConexao();
            ps = conn.prepareStatement(sbSQL.toString());

            int finalParamIndex = 1;
            // Loop 1: Parâmetros para PaginacaoCTE
            for (Map.Entry<Integer, Object> entry : mapaParametros.entrySet()) {
                ps.setObject(finalParamIndex++, entry.getValue());
            }

            // Loop 2: Parâmetros para ContagemCTE
            for (Map.Entry<Integer, Object> entry : mapaParametros.entrySet()) {
                ps.setObject(finalParamIndex++, entry.getValue());
            }

            if (!erro) {
                rs = ps.executeQuery();

                while (rs.next()) {
                    if (paginacao.TotalRegistros() == 0)
                        paginacao.TotalRegistros(rs.getInt("total_registros"));

                    Long idRegistro = rs.getLong("id");
                    RegistroDeFato item = registrosMap.get(idRegistro);

                    if (item == null) {
                        item = new RegistroDeFato();
                        item.setId(idRegistro);
                        item.setIdTipo(rs.getInt("id_tipo"));
                        item.setIdStatus(rs.getInt("id_status"));
                        item.setDataCriacao(rs.getTimestamp("data_criacao"));
                        item.setDataEncerramento(rs.getTimestamp("data_encerramento"));
                        item.setIdUsuario(rs.getInt("id_usuario"));
                        item.setTipoDescricao(rs.getString("tipo_registro"));
                        item.setStatusDescricao(rs.getString("status"));
                        item.setNomeUsuario(rs.getString("nome_usuario_criacao"));
                        item.setDataEvento(rs.getTimestamp("data_evento"));
                        item.setDataModificada(rs.getTimestamp("data_modificacao"));
                        item.setPrivado(rs.getInt("privado"));;
                        registrosMap.put(idRegistro, item);
                    }

                    // Adicionar Individuo
                    Integer idIndividuo = rs.getObject("individuo_id") != null ? rs.getInt("individuo_id") : null;
                    if (idIndividuo != null
                            && item.getIndividuos().stream().noneMatch(i -> idIndividuo.equals(i.getId()))) {
                        RegistroDeFatoIndividuo individuo = new RegistroDeFatoIndividuo();
                        individuo.setId(idIndividuo);
                        individuo.setIdRegistroFato(idRegistro);
                        individuo.setNome(rs.getString("individuo_nome"));
                        individuo.setCpf(rs.getString("individuo_cpf"));
                        item.getIndividuos().add(individuo);
                    }

                    // Adicionar Veiculo
                    Integer idVeiculo = rs.getObject("veiculo_id") != null ? rs.getInt("veiculo_id") : null;
                    if (idVeiculo != null && item.getVeiculos().stream().noneMatch(v -> idVeiculo.equals(v.getId()))) {
                        RegistroDeFatoVeiculo veiculo = new RegistroDeFatoVeiculo();
                        veiculo.setId(idVeiculo);
                        veiculo.setIdRegistroFato(idRegistro);
                        veiculo.setPlaca(rs.getString("veiculo_placa"));
                        veiculo.setCor(rs.getString("veiculo_cor"));
                        veiculo.setMarca(rs.getString("veiculo_marca"));
                        veiculo.setModelo(rs.getString("veiculo_modelo"));
                        item.getVeiculos().add(veiculo);
                    }

                    // Adicionar Endereco
                    Integer idEndereco = rs.getObject("endereco_id") != null ? rs.getInt("endereco_id") : null;
                    if (idEndereco != null
                            && item.getEnderecos().stream().noneMatch(e -> idEndereco.equals(e.getId()))) {
                        RegistroDeFatoEndereco endereco = new RegistroDeFatoEndereco();
                        endereco.setId(idEndereco);
                        endereco.setIdRegistroFato(idRegistro);
                        endereco.setIdCidade(rs.getObject("id_cidade") != null ? rs.getInt("id_cidade") : null);
                        endereco.setBairro(rs.getString("bairro"));
                        endereco.setRua(rs.getString("rua"));
                        endereco.setNumero(rs.getInt("numero"));
                        endereco.setCep(rs.getString("cep"));
                        endereco.setComplemento(rs.getString("complemento"));

                        Cidade cidade = new Cidade();
                        cidade.setId(rs.getInt("cidade_id"));
                        cidade.setNome(rs.getString("cidade_nome"));
                        cidade.setIdEstado(rs.getInt("id_estado"));
                        endereco.setCidade(cidade);

                        item.getEnderecos().add(endereco);
                    }

                    int idBoletim = rs.getInt("boletim_id");
                    if (!rs.wasNull()) {
                        Boletim boletim = new Boletim();
                        boletim.setId(idBoletim);
                        boletim.setIdRegistroFato(idRegistro);
                        boletim.setIdSituacao(rs.getInt("boletim_situacao"));

                        BoletimSituacao situacao = new BoletimSituacao();
                        situacao.setId(rs.getObject("boletim_situacao_id") != null ? rs.getInt("boletim_situacao_id") : null);
                        situacao.setDescricao(rs.getString("boletim_situacao_descricao"));
                        boletim.setSituacao(situacao);

                        boletim.setDataHoraEvento(rs.getTimestamp("data_hora_evento"));
                        boletim.setDetalhamento(rs.getString("detalhamento"));
                        boletim.setIdUsuario(rs.getInt("boletim_id_usuario"));
                        boletim.setDataCriacao(rs.getTimestamp("boletim_data_criacao"));
                        boletim.setDataEncerramento(rs.getTimestamp("boletim_data_encerramento"));
                        boletim.setPermiteAtendimento(rs.getInt("permite_atendimento"));

                        item.getBoletins().add(boletim);
                    }
                    
    	            //natureza
    	            Integer naturezaId = rs.getObject("natureza_id") != null
    	                    ? rs.getInt("natureza_id")
    	                    : null;

    	            if (naturezaId != null) {
    	                RegistroDeFatoNatureza natureza = new RegistroDeFatoNatureza();
    	                natureza.setId(naturezaId);
    	                natureza.setIdRegistroTipo(rs.getInt("id_tipo"));
    	                natureza.setNaturezaDesc(rs.getString("natureza_desc"));
    	                natureza.setRequerBo(rs.getObject("natureza_requer_bo") != null
    	                        ? rs.getBoolean("natureza_requer_bo")
    	                        : null);

    	                item.setNaturezaTipo(natureza);
    	            }
                }

                retorno.setListaBoletins(new ArrayList<>(registrosMap.values()));
                retorno.setPaginacao(paginacao);
            }

        } catch (Exception e) {
            erro = true;
            msgErro = "Erro ao obter registros de fato!";
            logger.error(msgErro + ": " + e.getMessage(), e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (Exception e) {
                logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
            }

            if (erro) throw new SQLException("Erro ao consultar registros de fato!");
        }

        return retorno;
    }
	
	private static String montarClausulaOrdenacao(String campo, String direcao) {
	    // 1. Valida a direção (ASC ou DESC). Padrão DESC se vier vazio.
	    String dirSql = (direcao != null && direcao.equalsIgnoreCase("ASC")) ? "ASC" : "DESC";

	    // 2. Garante que o campo não seja nulo
	    if (campo == null) campo = "";

	    String colunaSql;

	    switch (campo) {
	        // --- Campos Simples (Tabelas já presentes na CTE) ---
	        case "tipoDescricao":
	            colunaSql = "rft.tipo_desc";
	            break;
	            
	        case "statusDescricao":
	            colunaSql = "rs.descricao";
	            break;

	        case "dataEvento":
	            colunaSql = "rf.data_evento";
	            break;

	        case "dataCriacao":
	            colunaSql = "rf.data_criacao";
	            break;
	            
	        case "dataModificacao":
	            colunaSql = "rf.data_modificacao";
	            break;

	        case "dataEncerramento":
	            colunaSql = "rf.data_encerramento";
	            break;
	            
	        case "naturezaFato":
	            colunaSql = "rfn.natureza_desc";
	            break;
	            
	        case "privadoFato":
	            colunaSql = "rf.privado";
	            break;

	        // --- Campos Complexos (Tabelas 1:N) ---
	        // Como um fato pode ter vários indivíduos/veículos, ordenamos pelo "primeiro" encontrado
	        // Isso evita quebrar a paginação ou duplicar registros na contagem
	        
	        case "nome_cpf":
	            colunaSql = "(SELECT TOP 1 nome FROM muralha.registro_fato_individuo WHERE id_registro_fato = rf.id ORDER BY nome)";
	            break;

	        case "veiculo_placa":
	            colunaSql = "(SELECT TOP 1 placa FROM muralha.registro_fato_veiculo WHERE id_registro_fato = rf.id ORDER BY placa)";
	            break;

	        case "endereco":
	            colunaSql = "(SELECT TOP 1 rua FROM muralha.registro_fato_endereco WHERE id_registro_fato = rf.id ORDER BY rua)";
	            break;
	            
	        case "boletimSituacao":
	             colunaSql = "(SELECT TOP 1 bs.descricao FROM muralha.boletim b JOIN muralha.boletim_situacao bs ON bs.id = b.id_situacao WHERE b.id_registro_fato = rf.id)";
	             break;
	        // --- Padrão ---
	        default:
	            colunaSql = "rf.data_criacao"; 
	            break;
	    }

	    return " ORDER BY " + colunaSql + " " + dirSql + " ";
	}
	
	public static RegistroDeFatos ObterListaRegistroDeFatosSemBoletim(Date dataIni, Date dataFim, Integer idStatus, Integer idTipoRegistro,
	        String placa, String cpf, Integer idCidade,Integer idUsuarioLogado, Paginacao paginacao) throws ConexaoException, SQLException {

	    RegistroDeFatos retorno = new RegistroDeFatos();
	    List<Long> idsRegistroFato = new ArrayList<>();
	    
	    StringBuilder sbSQL = new StringBuilder();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    boolean erro = false;
	    String msgErro = "";

	    try {
	    
	    	sbSQL.append("select rf.id, ");	    
	    	sbSQL.append(paginacao.QueryTotalRegistros());
	    	sbSQL.append(" from muralha.registro_fato rf ");
	    	sbSQL.append((" join muralha.registro_fato_endereco rfe on rfe.id_registro_fato = rf.id "));
	    	
	    	//ATIVO
	    	sbSQL.append("where rf.tem_boletim = 0 ");
	    	
	        int paramIndex = 1;
	        Map<Integer, Object> mapaParametros = new LinkedHashMap<>();

	        if (dataIni != null) {
	            sbSQL.append(" AND rf.data_criacao >= ? ");
	            mapaParametros.put(paramIndex++, new java.sql.Date(dataIni.getTime()));
	        }
	        if (dataFim != null) {
	            sbSQL.append(" AND rf.data_criacao <= ? ");
	            mapaParametros.put(paramIndex++, new java.sql.Date(dataFim.getTime()));
	        }	 
	        if (idStatus != null) {
	            sbSQL.append(" AND rf.id_status = ? ");
	            mapaParametros.put(paramIndex++, idStatus);
	        }	   
	        if (idTipoRegistro != null) {
	            sbSQL.append(" AND rf.id_tipo = ? ");
	            mapaParametros.put(paramIndex++, idTipoRegistro);
	        }	   
	        if (idCidade != null) {
	            sbSQL.append(" AND rfe.id_cidade = ? ");
	            mapaParametros.put(paramIndex++, idCidade);
	        }	   
	        if (cpf != null && !cpf.isEmpty()) {
	            sbSQL.append(" AND EXISTS ( ");
	            sbSQL.append("     SELECT 1 FROM muralha.registro_fato_individuo rfi2 ");
	            sbSQL.append("     WHERE rfi2.id_registro_fato = rf.id AND rfi2.cpf = ? ");
	            sbSQL.append(" ) ");
	            mapaParametros.put(paramIndex++, cpf);
	        }
	        if (placa != null && !placa.isEmpty()) {
	            String[] placas = placa.split(",");
	            List<String> placasLimpa = new ArrayList<>();
	            for (String p : placas) {
	                if (p != null && !p.trim().isEmpty()) {
	                    placasLimpa.add(p.trim().toUpperCase());
	                }
	            }

	            if (!placasLimpa.isEmpty()) {
	                sbSQL.append(" AND EXISTS ( ");
	                sbSQL.append("     SELECT 1 FROM muralha.registro_fato_veiculo rfv2 ");
	                sbSQL.append("     WHERE rfv2.id_registro_fato = rf.id ");
	                sbSQL.append("     AND rfv2.placa IN (");
	                
	                for (int i = 0; i < placasLimpa.size(); i++) {
	                    if (i > 0) sbSQL.append(", ");
	                    sbSQL.append("?");
	                    mapaParametros.put(paramIndex++, placasLimpa.get(i));
	                }
	                
	                sbSQL.append(") ) ");
	            }
	        }
	        	        	        	        
	        // VERIFICA A REGRA DE VISIBILIDADE COM A PRIORIDADE CORRETA
	        sbSQL.append(" AND (");

	        // CASO 1: A prioridade máxima. Se existe registro na tabela de permissão, a regra é SÓ essa.
	        sbSQL.append("     EXISTS (");
	        sbSQL.append("         SELECT 1 FROM muralha.registro_fato_usuario_grupo rfug");
	        sbSQL.append("         WHERE rfug.id_registro_fato = rf.id");
	        sbSQL.append("         AND (");
	        sbSQL.append("             rfug.id_usuario = ?"); // O usuário logado está diretamente na permissão
	        sbSQL.append("             OR rfug.id_grupo IN (SELECT ug.id_grupo FROM dbo.sis_usuario_grupo ug WHERE ug.id_usuario = ?)"); // O usuário logado está em um grupo permitido
	        sbSQL.append("         )");
	        sbSQL.append("     )");

	        sbSQL.append("     OR");

	        // CASO 2: Se não existe NENHUMA permissão na tabela de prioridade, vale a regra do campo 'privado'.
	        sbSQL.append("     (");
	        sbSQL.append("         NOT EXISTS (SELECT 1 FROM muralha.registro_fato_usuario_grupo rfug WHERE rfug.id_registro_fato = rf.id)");
	        sbSQL.append("         AND (rf.privado = 0 OR rf.id_usuario = ?)"); // Ou o registro é público, ou o usuário logado é o dono
	        sbSQL.append("     )");

	        sbSQL.append(" )");

	        mapaParametros.put(paramIndex++, idUsuarioLogado); // Parâmetro para rfug.id_usuario
	        mapaParametros.put(paramIndex++, idUsuarioLogado); // Parâmetro para ug.id_usuario
	        mapaParametros.put(paramIndex++, idUsuarioLogado); // Parâmetro para rf.id_usuario
	        
	        sbSQL.append(" group by rf.id, rf.id_tipo, rf.id_status, rf.tem_boletim, rf.id_usuario, rf.data_criacao, rf.data_encerramento, rf.privado, rfe.id_registro_fato");
	        sbSQL.append(" ORDER BY rf.data_criacao DESC ");
	        sbSQL.append(paginacao.QueryPaginacao());

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());

	        for (Map.Entry<Integer, Object> entry : mapaParametros.entrySet()) {
	            Object value = entry.getValue();
	            int index = entry.getKey();

	            if (value instanceof String) {
	                ps.setString(index, (String) value);
	            } else if (value instanceof Integer) {
	                ps.setInt(index, (Integer) value);
	            } else if (value instanceof java.sql.Date) {
	                ps.setDate(index, (java.sql.Date) value);
	            } else {
	                erro = true;
	                msgErro = "Erro ao preparar parâmetros de filtro.";
	                break;
	            }
	        }
	        
	        if (!erro) {
	            rs = ps.executeQuery();
	            
	            while (rs.next()) {
	                if (paginacao.TotalRegistros() == 0) {
	                    paginacao.TotalRegistros(rs.getInt("total_registros"));
	                }
	                
	                idsRegistroFato.add(rs.getLong("id"));
	            }
	        }
	        
	        if(idsRegistroFato.isEmpty()) {
	        	return retorno;
	        }
	        	        
	        retorno = BuscarResultadoRegistroFato(idsRegistroFato, paginacao);
	        
	    } catch (Exception e) {
	        erro = true;
	        msgErro = "Erro ao obter registros de fato!";
	        logger.error(msgErro + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }

	        if (erro) throw new SQLException("Erro ao consultar registros de fato!");
	    }
	    
	    return retorno;
	}
	
	public static RegistroDeFatos ObterListaRegistroDeFatos(Date dataIni, Date dataFim, String nome, 
	        String placa, String cpf, Integer idUsuarioLogado, Paginacao paginacao) throws ConexaoException, SQLException {

	    RegistroDeFatos retorno = new RegistroDeFatos();
	    List<Long> idsRegistroFato = new ArrayList<>();
	    
	    StringBuilder sbSQL = new StringBuilder();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    boolean erro = false;
	    String msgErro = "";

	    try {
	    
	    	sbSQL.append("select rf.id, ");
	    	sbSQL.append(paginacao.QueryTotalRegistros() );
	    	sbSQL.append(" from muralha.registro_fato rf ");
	    	
	    	//ATIVO
	    	sbSQL.append("where rf.id_status = 1 ");
	    	
	        int paramIndex = 1;
	        Map<Integer, Object> mapaParametros = new LinkedHashMap<>();

	        if (dataIni != null) {
	            sbSQL.append(" AND rf.data_criacao >= ? ");
	            mapaParametros.put(paramIndex++, new java.sql.Date(dataIni.getTime()));
	        }
	        if (dataFim != null) {
	            sbSQL.append(" AND rf.data_criacao <= ? ");
	            mapaParametros.put(paramIndex++, new java.sql.Date(dataFim.getTime()));
	        }
	        if (nome != null && !nome.isEmpty()) {
	            sbSQL.append(" AND EXISTS ( ");
	            sbSQL.append("     SELECT 1 FROM muralha.registro_fato_individuo rfi2 ");
	            sbSQL.append("     WHERE  rfi2.id_registro_fato = rf.id and rfi2.nome like ? ");
	            sbSQL.append(" ) ");
	            mapaParametros.put(paramIndex++, "%" + nome + "%");
	        }
	        if (cpf != null && !cpf.isEmpty()) {
	            sbSQL.append(" AND EXISTS ( ");
	            sbSQL.append("     SELECT 1 FROM muralha.registro_fato_individuo rfi2 ");
	            sbSQL.append("     WHERE rfi2.id_registro_fato = rf.id AND rfi2.cpf = ? ");
	            sbSQL.append(" ) ");
	            mapaParametros.put(paramIndex++, cpf);
	        }
	        if (placa != null && !placa.isEmpty()) {
	            sbSQL.append(" AND EXISTS ( ");
	            sbSQL.append("     SELECT 1 FROM muralha.registro_fato_veiculo rfv2 ");
	            sbSQL.append("     WHERE rfv2.id_registro_fato = rf.id ");
	            sbSQL.append("     AND rfv2.placa = ? ");
	            sbSQL.append(") ");
	            mapaParametros.put(paramIndex++, placa);
	        }
	        	        	        	        
	        // VERIFICA A REGRA DE VISIBILIDADE COM A PRIORIDADE CORRETA
	        sbSQL.append(" AND (");

	        // CASO 1: A prioridade máxima. Se existe registro na tabela de permissão, a regra é SÓ essa.
	        sbSQL.append("     EXISTS (");
	        sbSQL.append("         SELECT 1 FROM muralha.registro_fato_usuario_grupo rfug");
	        sbSQL.append("         WHERE rfug.id_registro_fato = rf.id");
	        sbSQL.append("         AND (");
	        sbSQL.append("             rfug.id_usuario = ?"); // O usuário logado está diretamente na permissão
	        sbSQL.append("             OR rfug.id_grupo IN (SELECT ug.id_grupo FROM dbo.sis_usuario_grupo ug WHERE ug.id_usuario = ?)"); // O usuário logado está em um grupo permitido
	        sbSQL.append("         )");
	        sbSQL.append("     )");

	        sbSQL.append("     OR");

	        // CASO 2: Se não existe NENHUMA permissão na tabela de prioridade, vale a regra do campo 'privado'.
	        sbSQL.append("     (");
	        sbSQL.append("         NOT EXISTS (SELECT 1 FROM muralha.registro_fato_usuario_grupo rfug WHERE rfug.id_registro_fato = rf.id)");
	        sbSQL.append("         AND (rf.privado = 0 OR rf.id_usuario = ?)"); // Ou o registro é público, ou o usuário logado é o dono
	        sbSQL.append("     )");

	        sbSQL.append(" )");
	        
	        sbSQL.append(" ORDER BY rf.data_criacao DESC ");
	        sbSQL.append(paginacao.QueryPaginacao());

	        mapaParametros.put(paramIndex++, idUsuarioLogado); // Parâmetro para rfug.id_usuario
	        mapaParametros.put(paramIndex++, idUsuarioLogado); // Parâmetro para ug.id_usuario
	        mapaParametros.put(paramIndex++, idUsuarioLogado); // Parâmetro para rf.id_usuario
	        
	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());

	        for (Map.Entry<Integer, Object> entry : mapaParametros.entrySet()) {
	            Object value = entry.getValue();
	            int index = entry.getKey();

	            if (value instanceof String) {
	                ps.setString(index, (String) value);
	            } else if (value instanceof Integer) {
	                ps.setInt(index, (Integer) value);
	            } else if (value instanceof java.sql.Date) {
	                ps.setDate(index, (java.sql.Date) value);
	            } else {
	                erro = true;
	                msgErro = "Erro ao preparar parâmetros de filtro.";
	                break;
	            }
	        }
	        
	        if (!erro) {
	            rs = ps.executeQuery();
	            
	            while (rs.next()) {
	                if (paginacao.TotalRegistros() == 0) {
	                    paginacao.TotalRegistros(rs.getInt("total_registros"));
	                }
	                
	                idsRegistroFato.add(rs.getLong("id"));
	            }
	        }
	        
	        if(!idsRegistroFato.isEmpty()) {
	        	retorno = BuscarResultadoRegistroFato(idsRegistroFato, paginacao);
	        }	        
	        
	    } catch (Exception e) {
	        erro = true;
	        msgErro = "Erro ao obter registros de fato!";
	        logger.error(msgErro + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }

	        if (erro) throw new SQLException("Erro ao consultar registros de fato!");
	    }
	    
	    return retorno;
	}
	
	public static FatoSemBoletimCompletoDTO ObterFatoSemBoletimPorId(int registroFatoId) throws ConexaoException, SQLException {

	    StringBuilder sbSQL = new StringBuilder();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;
	    Set<Integer> usuariosSet = new HashSet<>();
	    Set<Integer> gruposSet = new HashSet<>();

	    boolean erro = false;
	    String msgErro = "";

	    try {
	    	//registro_fato
	    	sbSQL.append("SELECT ");
	    	sbSQL.append("rf.id AS id_fato, rf.id_tipo AS id_tipo_fato, rf.id_status AS id_status_fato, ");
	    	sbSQL.append("rf.tem_boletim, rf.id_usuario, rf.data_criacao, rf.privado, rf.data_encerramento, ");
	    		    	
	    	//fato
	    	sbSQL.append("f.permite_atendimento, f.detalhamento as observacoes_fato, f.existe_arma_envolvida, ");
	    	
	    	//registro_fato_endereco -> lista
	    	sbSQL.append("e.id_cidade AS endereco_cidadeId, e.cep AS endereco_cep, e.bairro AS endereco_bairro, e.rua AS endereco_rua, e.id as endereco_id, ");
	    	sbSQL.append("e.numero AS endereco_numero, e.complemento AS endereco_complemento, e.id_tipo_evento, e.latitude AS endereco_latitude, e.longitude AS endereco_longitude, ");
	    	sbSQL.append("ev.descricao as descricao_tipo_evento, ");
	    	
	    	//registro_fato_individuo -> lista
	    	sbSQL.append("rfe.id as id_individuo, rfe.nome AS individuo_nome, rfe.cpf AS individuo_cpf, rfe.email AS individuo_email, rfe.ddd AS individuo_ddd, ");
	    	sbSQL.append("rfe.telefone AS individuo_telefone, rfe.detalhe_envolvimento AS individuo_detalhe_envolvimento, ");
	    	sbSQL.append("rfe.id_tipo_envolvimento AS individuo_id_tipo_envolvimento, rfit.descricao AS tipo_envolvimento_descricao, ");
	    		    	
	    	//registro_fato_veiculo -> lista
	    	sbSQL.append("rfv.placa AS veiculo_placa, rfv.cor AS veiculo_cor, rfv.marca AS veiculo_marca, rfv.modelo AS veiculo_modelo, rfv.id as id_veiculo_registro_fato, ");
	    	
	    	//registro_fato_objeto -> lista
	    	sbSQL.append("rfo.id as id_objeto, rfo.tipo AS objeto_tipo, rfo.descricao AS objeto_descricao, rfo.id AS objeto_id, rfo.id_registro_fato, ");

	    	//registro_fato_usuario_grupo -> lista
	    	sbSQL.append("rfug.id_usuario as id_usuario_fato, rfug.id_grupo as id_grupo_fato, "); 
	    	
	    	sbSQL.append("rfpv.id AS passagem_id, rfpv.id_registro_fato AS passagem_id_registro_fato, ");
	    	sbSQL.append("rfpv.id_veiculo AS passagem_id_veiculo, rfpv.id_usuario AS passagem_id_usuario, rfpv.data AS passagem_data, ");
	    	sbSQL.append("vtr.placa AS passagem_placa, vtr.id AS passagem_id_veiculo_tempo_real ");

	    	sbSQL.append("FROM muralha.registro_fato rf ");
	    	sbSQL.append("JOIN muralha.fato f on f.id_registro_fato = rf.id ");
	    	sbSQL.append("JOIN muralha.registro_fato_endereco e ON e.id_registro_fato = rf.id ");
	    	sbSQL.append("JOIN muralha.registro_fato_endereco_evento ev on ev.id = e.id_tipo_evento ");
	    	sbSQL.append("LEFT JOIN muralha.registro_fato_individuo rfe ON rfe.id_registro_fato = rf.id ");
	    	sbSQL.append("LEFT JOIN muralha.registro_fato_individuo_tipo rfit ON rfit.id = rfe.id_tipo_envolvimento ");
	    	sbSQL.append("LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id ");
	    	sbSQL.append("LEFT JOIN muralha.registro_fato_objeto rfo ON rfo.id_registro_fato = rf.id ");
	    	sbSQL.append("LEFT JOIN muralha.registro_fato_usuario_grupo rfug on rfug.id_registro_fato = rf.id ");
	    	sbSQL.append("LEFT JOIN muralha.registro_fato_passagem_veic rfpv on rfpv.id_registro_fato = rf.id ");
	    	sbSQL.append("LEFT JOIN muralha.cad_veiculo_monitorado cvm on cvm.id = rfpv.id_veiculo ");
	    	sbSQL.append("LEFT JOIN muralha.veiculo_tempo_real vtr ON rfpv.id_veiculo = vtr.id ");
	    	sbSQL.append("WHERE rf.id = ? ");
	    	sbSQL.append("ORDER BY e.id asc");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());
	        ps.setInt(1, registroFatoId);

	        rs = ps.executeQuery();

	        FatoSemBoletimCompletoDTO fatoCompleto = new FatoSemBoletimCompletoDTO();
	        fatoCompleto.envolvidos = new ArrayList<>();
	        fatoCompleto.veiculos = new ArrayList<>();
	        fatoCompleto.objetos = new ArrayList<>();
	        fatoCompleto.idsUsuarios = new ArrayList<>();
	        fatoCompleto.idsGrupos = new ArrayList<>();
	        fatoCompleto.passagens = new ArrayList<>();
	        fatoCompleto.enderecos = new ArrayList<>();

	        Set<Integer> objetosRegistrados = new HashSet<>();
	        Set<Integer> passagensRegistradas = new HashSet<>();
	        Set<Integer> enderecosRegistrados = new HashSet<>();
	        Set<Integer> envolvidosRegistrados = new HashSet<>();
	        Set<Integer> veiculosRegistrados = new HashSet<>();

	        boolean registroBasePreenchido = false;

	        while (rs.next()) {

	        	//RegistroDeFato
	            if (!registroBasePreenchido) {
	                RegistroDeFato rf = new RegistroDeFato();
	                rf.setId(rs.getLong("id_fato"));
	                rf.setIdTipo(rs.getInt("id_tipo_fato"));
	                rf.setIdStatus(rs.getInt("id_status_fato"));
	                rf.setTemBoletim(rs.getInt("tem_boletim"));
	                rf.setIdUsuario(rs.getInt("id_usuario"));
	                rf.setDataCriacao(rs.getTimestamp("data_criacao"));
	                rf.setDataEncerramento(rs.getTimestamp("data_encerramento"));
	                rf.setPrivado(rs.getInt("privado"));
	                rf.setDetalhamento(rs.getString("observacoes_fato"));
	                rf.setPermiteAtendimento(rs.getInt("permite_atendimento"));
	                rf.setEnvolvimentoArmas(rs.getInt("existe_arma_envolvida"));
	                fatoCompleto.registro_fato = rf;
	                
	                //Endereco
	                Localizacao loc = new Localizacao();
	                enderecosRegistrados.add(rs.getInt("endereco_id"));
	                loc.setCidadeId(rs.getInt("endereco_cidadeId"));
	                loc.setTipoEnderecoEvento(rs.getInt("id_tipo_evento"));
	                loc.setCep(rs.getString("endereco_cep"));
	                loc.setRua(rs.getString("endereco_rua"));
	                loc.setBairro(rs.getString("endereco_bairro"));
	                loc.setNumero(rs.getString("endereco_numero"));
	                loc.setComplemento(rs.getString("endereco_complemento"));
	                loc.setLatitude(rs.getDouble("endereco_latitude"));
	                loc.setLongitude(rs.getDouble("endereco_longitude"));
	                fatoCompleto.localizacao = loc;

	                registroBasePreenchido = true;
	            }

	            Integer idEnvolvido = rs.getInt("id_individuo");
	            if (idEnvolvido != null && idEnvolvido != 0 && !envolvidosRegistrados.contains(idEnvolvido)) {
	                    EnvolvidoDTO envolvido = new EnvolvidoDTO();
	                    envolvido.setId(rs.getInt("id_individuo"));
	                    envolvido.setNome(rs.getString("individuo_nome"));
	                    envolvido.setCpf(rs.getString("individuo_cpf"));
	                    envolvido.setTipoEnvolvimento(rs.getInt("individuo_id_tipo_envolvimento"));
	                    envolvido.setEmail(rs.getString("individuo_email"));
	                    envolvido.setDdd(rs.getString("individuo_ddd"));
	                    envolvido.setTelefone(rs.getString("individuo_telefone"));
	                    envolvido.setDetalhamento(rs.getString("individuo_detalhe_envolvimento"));
	                    envolvido.setDescricao(rs.getString("tipo_envolvimento_descricao"));

	                    fatoCompleto.envolvidos.add(envolvido);
	                    envolvidosRegistrados.add(idEnvolvido);
	                }
	            
	            // Veículos
	            Integer idVeiculo = rs.getInt("id_veiculo_registro_fato");
	            if (idVeiculo != null && idVeiculo != 0 && !veiculosRegistrados.contains(idVeiculo)) {
	            	
	                VeiculoDTO veiculo = new VeiculoDTO();
	                veiculo.setId(rs.getInt("id_veiculo_registro_fato"));
	                veiculo.setPlaca(rs.getString("veiculo_placa"));
	                veiculo.setMarca(rs.getString("veiculo_marca"));
	                veiculo.setModelo(rs.getString("veiculo_modelo"));
	                veiculo.setCor(rs.getString("veiculo_cor"));

	                fatoCompleto.veiculos.add(veiculo);
	                veiculosRegistrados.add(idVeiculo);
	            }

	            // Objetos (verifica pelo tipo + descricao)
	            String tipoObj = rs.getString("objeto_tipo");
	            String descObj = rs.getString("objeto_descricao");
	            Integer objExiste = rs.getInt("objeto_id");
	                if (!objetosRegistrados.contains(objExiste)) {
	                    RegistroFatoObjeto obj = new RegistroFatoObjeto();
	                    obj.setId(rs.getInt("objeto_id"));
	                    obj.setId_registro_fato(rs.getInt("id_registro_fato"));
	                    obj.setTipo(tipoObj);
	                    obj.setDescricao(descObj);

	                    fatoCompleto.objetos.add(obj);
	                    objetosRegistrados.add(objExiste);
	                }
	                
                // --- Adiciona IDs de Usuários ---
                int idUsuario = rs.getInt("id_usuario_fato");
                if (idUsuario != 0 && usuariosSet.add(idUsuario)) {
                    fatoCompleto.idsUsuarios.add(idUsuario);
                }
                
                // --- Adiciona IDs de Grupos ---
                int idGrupo = rs.getInt("id_grupo_fato");
                if (idGrupo != 0 && gruposSet.add(idGrupo)) {
                    fatoCompleto.idsGrupos.add(idGrupo);
                }
                
                //Endereços adicionais
                Integer idEndereco = rs.getInt("endereco_id");
                if(idEndereco != null && !enderecosRegistrados.contains(idEndereco)) {
                	enderecosRegistrados.add(idEndereco);
                	Localizacao localizacao = new Localizacao();
                	localizacao.setCidadeId(rs.getInt("endereco_cidadeId"));
                	localizacao.setTipoEnderecoEvento(rs.getInt("id_tipo_evento"));
                	localizacao.setCep(rs.getString("endereco_cep"));
                	localizacao.setRua(rs.getString("endereco_rua"));
                	localizacao.setBairro(rs.getString("endereco_bairro"));
                	localizacao.setNumero(rs.getString("endereco_numero"));
                	localizacao.setComplemento(rs.getString("endereco_complemento"));
                	localizacao.setLatitude(rs.getDouble("endereco_latitude"));
                	localizacao.setLongitude(rs.getDouble("endereco_longitude"));
                	localizacao.setTipoEnderecoEventoDescricao(rs.getString("descricao_tipo_evento"));
	                fatoCompleto.enderecos.add(localizacao);
                }
                
	            // Passagens de Veículos
	            Integer idPassagem = rs.getObject("passagem_id") != null ? rs.getInt("passagem_id") : null;
	            Integer passagenJaRegistrada = idPassagem;
	            if (idPassagem != null && !passagensRegistradas.contains(passagenJaRegistrada)) {
	            	passagensRegistradas.add(passagenJaRegistrada);
	            	RegistroDeFatoPassagemVeiculo passagem = new RegistroDeFatoPassagemVeiculo();
	                passagem.setId(idPassagem);
	                passagem.setId_registro_fato(rs.getLong("passagem_id_registro_fato"));
	                passagem.setId_veiculo(rs.getObject("passagem_id_veiculo") != null ? UUID.fromString(rs.getString("passagem_id_veiculo")) : null);
	                passagem.setId_usuario(rs.getObject("passagem_id_usuario") != null ? rs.getInt("passagem_id_usuario") : null);
	                passagem.setData_passagem(rs.getDate("passagem_data"));
	                passagem.setPlaca(rs.getString("passagem_placa"));
	                fatoCompleto.passagens.add(passagem);
	            }
	            
	        }

	        return fatoCompleto;

	    } catch (Exception e) {
	        erro = true;
	        msgErro = "Erro ao buscar informações do fato!";
	        logger.error(msgErro + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }

	        if (erro) throw new SQLException("Erro ao consultar o fato!");
	    }

	    return null;
	}
	
	public static Long CadastrarRegistroFatoSemBoletim(RegistroFatoDTO registroFato, int idUsuario) 
	        throws ConexaoException, SQLException {

	    StringBuilder sbSQL = new StringBuilder();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    boolean erro = false;
	    String msgErro = "";
	    Long idGerado = null;

		int privado = registroFato.getPrivado();
		int tipoFato = registroFato.getTipoRegistro();

	    try {
	        sbSQL.append("INSERT INTO muralha.registro_fato ");
	        sbSQL.append("(id_status, id_tipo, tem_boletim,id_usuario, data_criacao, data_encerramento, privado)");
	        sbSQL.append("VALUES (?, ?, ?, ?, GETDATE(), NULL, ?)");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);

	        int idStatus = 1;        
	        int temBoletim = 0;   

	        ps.setInt(1, idStatus);
	        ps.setInt(2, tipoFato);
	        ps.setInt(3, temBoletim);
	        ps.setInt(4, idUsuario);
	        ps.setInt(5, privado);

	        int linhasAfetadas = ps.executeUpdate();

	        if (linhasAfetadas > 0) {
	            rs = ps.getGeneratedKeys();
	            if (rs.next()) {
	                idGerado = rs.getLong(1);
	            }
	        }

	    } catch (Exception e) {
	        erro = true;
	        msgErro = "Erro ao cadastrar fato!";
	        logger.error(msgErro + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }

	        if (erro) throw new SQLException(msgErro);
	    }

	    return idGerado;
	}
	
	public static int CadastrarFatoSemBoletim(Long idRegistroFato, RegistroFatoDTO registroFato, int idUsuario) 
	        throws ConexaoException, SQLException {

	    StringBuilder sbSQL = new StringBuilder();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    boolean erro = false;
	    String msgErro = "";
	    int idGerado = -1;	    

	    try {
	        sbSQL.append("INSERT INTO muralha.fato ");
	        sbSQL.append("(id_registro_fato, id_situacao, data_hora_evento, detalhamento, existe_arma_envolvida, id_usuario, data_criacao, data_encerramento, permite_atendimento) ");
	        sbSQL.append("VALUES (?, 1, ?, ?, ?, ?, GETDATE(), NULL, ?)");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);

	        ps.setLong(1, idRegistroFato);
	        ps.setString(2, registroFato.getDataHoraOcorrido());
	        ps.setString(3, registroFato.getDetalhamentoFato());
	        ps.setInt(4, registroFato.getEnvolvimentoArmas());
	        ps.setInt(5, idUsuario);
	        ps.setInt(6, registroFato.getAtendimentoPermitido());

	        int linhasAfetadas = ps.executeUpdate();

	        if (linhasAfetadas > 0) {
	            rs = ps.getGeneratedKeys();
	            if (rs.next()) {
	                idGerado = rs.getInt(1);
	            }
	        }

	    } catch (Exception e) {
	        erro = true;
	        msgErro = "Erro ao cadastrar fato!";
	        logger.error(msgErro + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }

	        if (erro) throw new SQLException(msgErro);
	    }

	    return idGerado;
	}
	
	public static Long inserirRegistroFato(RegistroDeFato registro) throws SQLException, ConexaoException {
	    String sql = "INSERT INTO muralha.registro_fato (id_tipo, id_status, tem_boletim, id_usuario, data_criacao, privado, data_evento, id_tipo_natureza) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
	    
	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	    	Calendar calendarioUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	        ps.setInt(1, registro.getIdTipo());
	        ps.setInt(2, registro.getIdStatus());
	        ps.setInt(3, registro.getTemBoletim());
	        ps.setInt(4, registro.getIdUsuario());
	        ps.setTimestamp(5, new Timestamp(registro.getDataCriacao().getTime())); 
	        ps.setInt(6, registro.getPrivado() != null ? registro.getPrivado() : 0);
	        
	        if (registro.getDataEvento() != null) {
	            ps.setTimestamp(7, new Timestamp(registro.getDataEvento().getTime()), calendarioUTC);
	        } else {
	            ps.setNull(7, java.sql.Types.TIMESTAMP);
	        }

	        if (registro.getIdNaturezaTipo() != null) {
	            ps.setInt(8, registro.getIdNaturezaTipo());
	        } else {
	            ps.setNull(8, java.sql.Types.INTEGER);
	        }
	        
	        int rows = ps.executeUpdate();
	        if (rows == 0) return null;

	        try (ResultSet rs = ps.getGeneratedKeys()) {
	            if (rs.next()) {
	                return rs.getLong(1);
	            }
	        }
	    }
	    return null;
	}
	
	public static RegistroDeFato ObterRegistroDeFatoPorIdComBoletim(Integer id, Integer idUsuarioLogado) throws ConexaoException, SQLException {
	    RegistroDeFato item = null;

	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    StringBuilder sbSQL = new StringBuilder();

	    try {  	
	    	sbSQL.append(" SELECT ");
	    	sbSQL.append(" rf.data_criacao, rf.data_encerramento, rf.data_evento, rf.id_usuario, su.usuario AS usuario_criacao, su.nome AS nome_usuario_criacao,");
	    	sbSQL.append(" rf.data_criacao, rf.data_encerramento, rf.id_usuario, su.usuario AS usuario_criacao, su.nome AS nome_usuario_criacao,");
	    	sbSQL.append(" rf.privado, ");
	    	sbSQL.append(" rfe.id AS endereco_id, rfe.id_cidade, rfe.id_tipo_evento, rfe.bairro, rfe.rua, rfe.numero, rfe.cep, rfe.complemento,");
	    	sbSQL.append(" rfe.latitude, rfe.longitude,");
	    	sbSQL.append(" rfi.id AS individuo_id, rfi.nome AS individuo_nome, rfi.cpf AS individuo_cpf,");
	    	sbSQL.append(" rfi.id_tipo_envolvimento AS individuo_id_tipo_envolvimento,");
	    	sbSQL.append(" rfit.descricao AS individuo_tipo_envolvimento_desc,");
	    	sbSQL.append(" rfi.detalhe_envolvimento AS individuo_detalhe_envolvimento,");
	    	sbSQL.append(" rfi.ddd AS individuo_ddd, rfi.telefone AS individuo_telefone, rfi.email AS individuo_email,");
	    	sbSQL.append(" rfv.id AS veiculo_id, rfv.placa AS veiculo_placa, rfv.marca AS veiculo_marca, rfv.cor AS veiculo_cor, rfv.modelo AS veiculo_modelo,");
	    	sbSQL.append(" c.id AS cidade_id, c.nome AS cidade_nome, c.id_estado,");
	    	sbSQL.append(" b.id AS boletim_id, b.id_situacao AS boletim_situacao, b.data_hora_evento, b.detalhamento,");
	    	sbSQL.append(" b.id_usuario AS boletim_id_usuario, b.data_criacao AS boletim_data_criacao,");
	    	sbSQL.append(" b.data_encerramento AS boletim_data_encerramento, b.permite_atendimento,");
	    	sbSQL.append(" bs.id AS boletim_situacao_id, bs.descricao AS boletim_situacao_descricao,");
	    	sbSQL.append(" rfo.id AS objeto_id, rfo.tipo AS objeto_tipo, rfo.descricao AS objeto_descricao,");
	    	sbSQL.append(" ba.id AS apreensao_id, ba.tipo AS apreensao_tipo, ba.descricao AS apreensao_descricao,");
	    	sbSQL.append(" rd.id AS documento_id, rd.tipo AS documento_tipo, rd.dir_arquivo AS documento_dir_arquivo, rd.detalhamento AS documento_detalhamento, rd.id_atendimento AS documento_id_atendimento,");
	    	sbSQL.append(" rl.id AS link_id, rl.url AS link_url, rl.detalhamento AS link_detalhamento,");
	    	sbSQL.append(" rfg.id AS grupo_id, rfg.id_usuario AS grupo_id_usuario, rfg.id_grupo AS grupo_id_grupo,");
	    	sbSQL.append(" rfpv.id AS passagem_id, rfpv.id_registro_fato AS passagem_id_registro_fato,");
	    	sbSQL.append(" rfpv.id_veiculo AS passagem_id_veiculo, rfpv.id_usuario AS passagem_id_usuario, rfpv.data AS passagem_data,");
	    	sbSQL.append(" vtr.placa AS passagem_placa, vtr.id AS passagem_id_veiculo_tempo_real, ");
	    	sbSQL.append(" rfee.descricao AS endereco_evento_descricao");
	    	
	    	sbSQL.append(" FROM muralha.registro_fato rf");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_status rs ON rs.id = rf.id_status");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_tipo rft ON rft.id = rf.id_tipo");
	    	sbSQL.append(" LEFT JOIN sis_usuario su ON su.id_usuario = rf.id_usuario");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_endereco rfe ON rfe.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.cidade c ON c.id = rfe.id_cidade");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_individuo rfi ON rfi.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.boletim b ON b.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.boletim_situacao bs ON bs.id = b.id_situacao");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_objeto rfo ON rfo.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.boletim_apreensao ba ON ba.id_boletim = b.id");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_individuo_tipo rfit ON rfit.id = rfi.id_tipo_envolvimento");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_documento rd ON rd.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_link rl ON rl.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_usuario_grupo rfg ON rfg.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_passagem_veic rfpv ON rfpv.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.veiculo_tempo_real vtr ON rfpv.id_veiculo = vtr.id ");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_endereco_evento rfee ON rfee.id = rfe.id_tipo_evento ");

	    	sbSQL.append(" WHERE rf.id = ?");
	    	sbSQL.append(" AND rf.tem_boletim = 1");
	    	sbSQL.append(" AND (rf.privado = 0 OR rf.id_usuario = ?)");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());
	        ps.setInt(1, id);              // ID do registro de fato
	        ps.setInt(2, idUsuarioLogado); // só permite visualizar se público ou dono
	        
	        rs = ps.executeQuery();

	        while (rs.next()) {
	            if (item == null) {
	                item = new RegistroDeFato();
	                item.setId(rs.getLong("id"));
	                item.setIdTipo(rs.getInt("id_tipo"));
	                item.setIdStatus(rs.getInt("id_status"));
	                item.setPrivado(rs.getInt("privado"));
	                item.setDataCriacao(rs.getTimestamp("data_criacao"));
	                item.setDataEncerramento(rs.getTimestamp("data_encerramento"));
	                item.setIdUsuario(rs.getInt("id_usuario"));
	                item.setTipoDescricao(rs.getString("tipo_registro"));
	                item.setStatusDescricao(rs.getString("status"));
	                item.setNomeUsuario(rs.getString("nome_usuario_criacao"));
	                item.setDataEvento(rs.getTimestamp("data_evento"));
	            }

	            // Individuos
	            Integer idIndividuo = rs.getObject("individuo_id") != null ? rs.getInt("individuo_id") : null;
	            if (idIndividuo != null && item.getIndividuos().stream().noneMatch(i -> idIndividuo.equals(i.getId()))) {
	            	RegistroDeFatoIndividuo individuo = new RegistroDeFatoIndividuo();
	            	individuo.setId(rs.getInt("individuo_id"));
	            	individuo.setIdRegistroFato(item.getId());
	            	individuo.setNome(rs.getString("individuo_nome"));
	            	individuo.setCpf(rs.getString("individuo_cpf"));
	            	individuo.setIdTipoEnvolvimento(
	            	    rs.getObject("individuo_id_tipo_envolvimento") != null ? rs.getInt("individuo_id_tipo_envolvimento") : null
	            	);
	            	individuo.setDetalheEnvolvimento(rs.getString("individuo_detalhe_envolvimento"));
	            	individuo.setDdd(
	            	    rs.getObject("individuo_ddd") != null ? rs.getInt("individuo_ddd") : null
	            	);
	            	individuo.setTelefone(
	            	    rs.getObject("individuo_telefone") != null ? rs.getString("individuo_telefone") : null
	            	);
	            	individuo.setEmail(rs.getString("individuo_email"));

	            	// Preencher o objeto tipoEnvolvimento
	            	RegistroDeFatoIndividuoTipo tipo = new RegistroDeFatoIndividuoTipo();
	            	tipo.setId(individuo.getIdTipoEnvolvimento());
	            	tipo.setDescricao(rs.getString("individuo_tipo_envolvimento_desc"));
	            	individuo.setTipoEnvolvimento(tipo);
	                item.getIndividuos().add(individuo);
	            }

	            // Veículos
	            Integer idVeiculo = rs.getObject("veiculo_id") != null ? rs.getInt("veiculo_id") : null;
	            if (idVeiculo != null && item.getVeiculos().stream().noneMatch(v -> idVeiculo.equals(v.getId()))) {
	                RegistroDeFatoVeiculo veiculo = new RegistroDeFatoVeiculo();
	                veiculo.setId(idVeiculo);
	                veiculo.setIdRegistroFato(item.getId());
	                veiculo.setPlaca(rs.getString("veiculo_placa"));
	                veiculo.setCor(rs.getString("veiculo_cor"));
	                veiculo.setMarca(rs.getString("veiculo_marca"));
	                veiculo.setModelo(rs.getString("veiculo_modelo"));
	                item.getVeiculos().add(veiculo);
	            }

	            // Endereço
	            Integer idEndereco = rs.getObject("endereco_id") != null ? rs.getInt("endereco_id") : null;
	            if (idEndereco != null && item.getEnderecos().stream().noneMatch(e -> idEndereco.equals(e.getId()))) {
	                RegistroDeFatoEndereco endereco = new RegistroDeFatoEndereco();
	                endereco.setId(idEndereco);
	                endereco.setIdRegistroFato(item.getId());
	                endereco.setIdCidade(rs.getObject("id_cidade") != null ? rs.getInt("id_cidade") : null);
	                endereco.setIdTipoEvento(rs.getObject("id_tipo_evento") != null ? rs.getInt("id_tipo_evento") : null);
	                endereco.setBairro(rs.getString("bairro"));
	                endereco.setRua(rs.getString("rua"));
	                endereco.setNumero(rs.getInt("numero"));
	                endereco.setCep(rs.getString("cep"));
	                endereco.setComplemento(rs.getString("complemento"));
	                
	                // Latitude e longitude
	                endereco.setLatitude(rs.getObject("latitude") != null ? rs.getBigDecimal("latitude") : null);
	                endereco.setLongitude(rs.getObject("longitude") != null ? rs.getBigDecimal("longitude") : null);

	                Cidade cidade = new Cidade();
	                cidade.setId(rs.getInt("cidade_id"));
	                cidade.setNome(rs.getString("cidade_nome"));
	                cidade.setIdEstado(rs.getInt("id_estado"));
	                endereco.setCidade(cidade);
	                
	                RegistroDeFatoEnderecoEvento evento = new RegistroDeFatoEnderecoEvento();
	                evento.setId(endereco.getIdTipoEvento());
	                evento.setDescricao(rs.getString("endereco_evento_descricao"));
	                endereco.setEvento(evento);

	                item.getEnderecos().add(endereco);
	            }

	            //Boletim
	            int idBoletim = rs.getInt("boletim_id");
	            if (!rs.wasNull()) {
	                Boletim boletim = item.getBoletins()
	                    .stream()
	                    .filter(b -> b.getId().equals(idBoletim))
	                    .findFirst()
	                    .orElse(null);

	                if (boletim == null) {
	                    boletim = new Boletim();
	                    boletim.setId(idBoletim);
	                    boletim.setIdRegistroFato(item.getId());
	                    boletim.setIdSituacao(rs.getInt("boletim_situacao"));

	                    BoletimSituacao situacao = new BoletimSituacao();
	                    situacao.setId(rs.getObject("boletim_situacao_id") != null ? rs.getInt("boletim_situacao_id") : null);
	                    situacao.setDescricao(rs.getString("boletim_situacao_descricao"));
	                    boletim.setSituacao(situacao);

	                    boletim.setDataHoraEvento(rs.getTimestamp("data_hora_evento"));
	                    boletim.setDetalhamento(rs.getString("detalhamento"));
	                    boletim.setIdUsuario(rs.getInt("boletim_id_usuario"));
	                    boletim.setDataCriacao(rs.getTimestamp("boletim_data_criacao"));
	                    boletim.setDataEncerramento(rs.getTimestamp("boletim_data_encerramento"));
	                    boletim.setPermiteAtendimento(rs.getInt("permite_atendimento"));

	                    boletim.setApreensoes(new ArrayList<>()); // Garante lista
	                    item.getBoletins().add(boletim);
	                }

	                // Apreensão
	                Integer idApreensao = rs.getObject("apreensao_id") != null ? rs.getInt("apreensao_id") : null;
	                if (idApreensao != null) {
	                    boolean jaExiste = boletim.getApreensoes()
	                        .stream()
	                        .anyMatch(a -> idApreensao.equals(a.getId()));

	                    if (!jaExiste) {
	                        BoletimApreensao apreensao = new BoletimApreensao();
	                        apreensao.setId(idApreensao);
	                        apreensao.setIdBoletim(boletim.getId());
	                        apreensao.setTipo(rs.getString("apreensao_tipo"));
	                        apreensao.setDescricao(rs.getString("apreensao_descricao"));
	                        boletim.getApreensoes().add(apreensao);
	                    }
	                }
	            }
	            
	            //objeto
	            Integer idObjeto = rs.getObject("objeto_id") != null ? rs.getInt("objeto_id") : null;
	            if (idObjeto != null && item.getObjetos().stream().noneMatch(o -> idObjeto.equals(o.getId()))) {
	                RegistroDeFatoObjeto objeto = new RegistroDeFatoObjeto();
	                objeto.setId(idObjeto);
	                objeto.setIdRegistroFato(item.getId());
	                objeto.setTipo(rs.getString("objeto_tipo"));
	                objeto.setDescricao(rs.getString("objeto_descricao"));
	                item.getObjetos().add(objeto);
	            }
	            
	            // Documentos
	            Integer idDocumento = rs.getObject("documento_id") != null ? rs.getInt("documento_id") : null;
	            if (idDocumento != null && item.getDocumentos().stream().noneMatch(d -> idDocumento.equals(d.getId()))) {
	                RegistroDeFatoDocumento documento = new RegistroDeFatoDocumento();
	                documento.setId(idDocumento);
	                documento.setIdRegistroFato(item.getId());
	                documento.setTipo(rs.getString("documento_tipo"));
	                documento.setDirArquivo(rs.getString("documento_dir_arquivo"));
	                documento.setDetalhamento(rs.getString("documento_detalhamento"));
	                documento.setIdAtendimento(rs.getObject("documento_id_atendimento") != null ? rs.getInt("documento_id_atendimento") : null);

	                item.getDocumentos().add(documento);
	            }
	            
	            // Link
	            Integer idLink = rs.getObject("link_id") != null ? rs.getInt("link_id") : null;
	            if (idLink != null && item.getLinks().stream().noneMatch(l -> idLink.equals(l.getId()))) {
	                RegistroDeFatoLink link = new RegistroDeFatoLink();
	                link.setId(idLink);
	                link.setIdRegistroFato(item.getId());
	                link.setUrl(rs.getString("link_url"));
	                link.setDetalhamento(rs.getString("link_detalhamento"));
	                item.getLinks().add(link);
	            }
	            
	            //Grupos
	            Integer idGrupoRegistro = rs.getObject("grupo_id") != null ? rs.getInt("grupo_id") : null;
	            Integer idUsuarioGrupo = rs.getObject("grupo_id_usuario") != null ? rs.getInt("grupo_id_usuario") : null;
	            Integer idGrupo = rs.getObject("grupo_id_grupo") != null ? rs.getInt("grupo_id_grupo") : null;

	            if (idGrupoRegistro != null) {
	                RegistroDeFatoUsuarioGrupo grupo = new RegistroDeFatoUsuarioGrupo();
	                grupo.setId(idGrupoRegistro);
	                grupo.setIdRegistroFato(item.getId());
	                grupo.setIdUsuario(idUsuarioGrupo);
	                grupo.setIdGrupo(idGrupo);
	                item.getUsuarioGrupos().add(grupo);
	            }
	            
	            // Passagens de Veículos
	            Integer idPassagem = rs.getObject("passagem_id") != null ? rs.getInt("passagem_id") : null;
	            if (idPassagem != null && item.getPassagensVeiculo().stream().noneMatch(p -> idPassagem.equals(p.getId()))) {
	                RegistroFatoPassagemVeiculo passagem = new RegistroFatoPassagemVeiculo();
	                passagem.setId(idPassagem);
	                passagem.setIdRegistroFato(rs.getLong("passagem_id_registro_fato"));
	                passagem.setIdVeiculo(rs.getObject("passagem_id_veiculo") != null ? UUID.fromString(rs.getString("passagem_id_veiculo")) : null);
	                passagem.setIdUsuario(rs.getObject("passagem_id_usuario") != null ? rs.getInt("passagem_id_usuario") : null);
	                passagem.setData(rs.getTimestamp("passagem_data"));
	                passagem.setPlaca(rs.getString("passagem_placa"));
	                passagem.setIdVeiculoTempoReal(
	                	    rs.getObject("passagem_id_veiculo_tempo_real") != null ? UUID.fromString(rs.getString("passagem_id_veiculo_tempo_real")) : null
	                	);
	                item.getPassagensVeiculo().add(passagem);
	            }
	            
	        }

	    } catch (Exception e) {
	        logger.error("Erro ao obter registro de fato com boletim: " + e.getMessage(), e);
	        throw new SQLException("Erro ao consultar registro de fato!");
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }
	    }
	    
	    if (item != null && item.getEnderecos() != null) {
	        item.getEnderecos().sort(Comparator.comparingInt(RegistroDeFatoEndereco::getId));
	    }

	    return item;
	}
	
	public static boolean atualizarRegistroFato(RegistroDeFato registro) throws SQLException, ConexaoException {
	    String sql = "UPDATE muralha.registro_fato "
	               + "SET id_status = ?, id_tipo_natureza = ?, privado = ?, tem_boletim = ?, data_encerramento = ?, data_modificacao = ?, data_evento = ? "
	               + "WHERE id = ?";

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	    	Calendar calendarioUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	        if (registro.getIdStatus() != null) {
	            ps.setInt(1, registro.getIdStatus());
	        } else {
	            ps.setNull(1, java.sql.Types.INTEGER);
	        }
	        
	        if (registro.getIdNaturezaTipo() != null) {
	            ps.setInt(2, registro.getIdNaturezaTipo());
	        } else {
	            ps.setNull(2, java.sql.Types.INTEGER);
	        }

	        ps.setInt(3, registro.getPrivado() != null ? registro.getPrivado() : 0);

	        ps.setInt(4, registro.getTemBoletim() != null ? registro.getTemBoletim() : 0);
	        
	        if (registro.getDataEncerramento() != null) {
	            ps.setTimestamp(5, new java.sql.Timestamp(registro.getDataEncerramento().getTime()));
	        } else {
	            ps.setNull(5, java.sql.Types.TIMESTAMP);
	        }
	        
	        if (registro.getDataModificada() != null) {
	            ps.setTimestamp(6, new java.sql.Timestamp(registro.getDataModificada().getTime()));
	        } else {
	            ps.setNull(6, java.sql.Types.TIMESTAMP);
	        }

	        if (registro.getDataEvento() != null) {
	            ps.setTimestamp(7, new java.sql.Timestamp(registro.getDataEvento().getTime()), calendarioUTC);
	        } else {
	            ps.setNull(7, java.sql.Types.TIMESTAMP);
	        }

	        ps.setLong(8, registro.getId());

	        int rows = ps.executeUpdate();
	        return rows > 0;
	    }
	}
	
	public static void CadastrarFatoUsuarioGrupo(List<Integer> idsGrupos, List<Integer> idsUsuarios, Long idRegistroFato) throws SQLException, ConexaoException {
	    String sql = "insert into muralha.registro_fato_usuario_grupo(id_registro_fato, id_usuario, id_grupo) values (?,?,?)";
	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	    	
	        if (idsGrupos != null && !idsGrupos.isEmpty()) {
	            for (Integer idGrupo : idsGrupos) {
	                ps.setLong(1, idRegistroFato);
	                ps.setNull(2, java.sql.Types.INTEGER);
	                ps.setInt(3, idGrupo);
	                ps.addBatch();
	            }
	            
	            ps.executeBatch();
	        }
	        
	        if (idsUsuarios != null && !idsUsuarios.isEmpty()) {
	            for (Integer idUsuario : idsUsuarios) {
	                ps.setLong(1, idRegistroFato);
	                ps.setInt(2, idUsuario);
	                ps.setNull(3, java.sql.Types.INTEGER);
	                ps.addBatch();
	            }
	            
	            ps.executeBatch();
	        }	 	        	       
	    }
	}
	
	/**
	 * Verifica se um usuário tem permissão para acessar um registro de fato específico,
	 * seguindo a regra de negócio de prioridade.
	 * * @param idRegistroFato O ID do registro de fato a ser verificado.
	 * @param idUsuarioLogado O ID do usuário que está tentando o acesso.
	 * @return true se o usuário tiver permissão, false caso contrário.
	 * @throws SQLException se ocorrer um erro de banco de dados.
	 */
	public static boolean verificarAcessoRegistroFato(Integer idRegistroFato, Integer idUsuarioLogado) throws SQLException {
	    boolean temAcesso = false;
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    StringBuilder sbSQL = new StringBuilder();
	    // Usamos "SELECT 1" pois só queremos saber se a linha existe, o que é mais performático.
	    sbSQL.append("SELECT 1 FROM muralha.registro_fato rf WHERE rf.id = ? AND (");

	    // CASO 1: A prioridade máxima. Se existe registro na tabela de permissão, a regra é SÓ essa.
	    sbSQL.append("     EXISTS (");
	    sbSQL.append("         SELECT 1 FROM muralha.registro_fato_usuario_grupo rfug");
	    sbSQL.append("         WHERE rfug.id_registro_fato = rf.id");
	    sbSQL.append("         AND (");
	    sbSQL.append("             rfug.id_usuario = ?"); // O usuário logado está diretamente na permissão
	    sbSQL.append("             OR rfug.id_grupo IN (SELECT ug.id_grupo FROM dbo.sis_usuario_grupo ug WHERE ug.id_usuario = ?)"); // O usuário logado está em um grupo permitido
	    sbSQL.append("         )");
	    sbSQL.append("     )");

	    sbSQL.append("     OR");

	    // CASO 2: Se não existe NENHUMA permissão na tabela de prioridade, vale a regra do campo 'privado'.
	    sbSQL.append("     (");
	    sbSQL.append("         NOT EXISTS (SELECT 1 FROM muralha.registro_fato_usuario_grupo rfug WHERE rfug.id_registro_fato = rf.id)");
	    sbSQL.append("         AND (rf.privado = 0 OR rf.id_usuario = ?)"); // Ou o registro é público, ou o usuário logado é o dono
	    sbSQL.append("     )");

	    sbSQL.append(" )");

	    try {
	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());
	        
	        ps.setInt(1, idRegistroFato); // rf.id
	        ps.setInt(2, idUsuarioLogado);   // rfug.id_usuario
	        ps.setInt(3, idUsuarioLogado);   // ug.id_usuario
	        ps.setInt(4, idUsuarioLogado);   // rf.id_usuario

	        rs = ps.executeQuery();
	        
	        // Se a consulta retornar qualquer linha, significa que as condições foram atendidas.
	        if (rs.next()) {
	            temAcesso = true;
	        }

	    } catch (Exception e) {
	        // logger.error("Erro ao verificar acesso ao registro de fato: " + e.getMessage(), e);
	        throw new SQLException("Erro ao verificar acesso ao registro de fato!");
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            // logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }
	    }

	    return temAcesso;
	}
	
	public static boolean AtualizarFato(RegistroFatoDTO fato) throws SQLException, ConexaoException {
	    String sql = "update muralha.fato set id_situacao = ?, data_hora_evento = ?, detalhamento = ?, existe_arma_envolvida = ?, permite_atendimento = ?\r\n"
	    		+ "where id_registro_fato = ?";

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	    	
	    	ps.setInt(1, fato.getIdStatus());
	    	ps.setString(2, fato.getDataHoraOcorrido());
	    	ps.setString(3, fato.getDetalhamentoFato());
	    	ps.setInt(4, fato.getEnvolvimentoArmas());
	    	ps.setInt(5, fato.getAtendimentoPermitido());
	    	ps.setInt(6, fato.getId());

	        int rows = ps.executeUpdate();

	        return rows > 0; // retorna true se atualizou algum registro
	    }
	}
	
	public static boolean AtualizarFatoPorRegistroFatoId(RegistroFatoDTO fato,long idRegistroFato) throws SQLException, ConexaoException {
	    String sql = "update muralha.fato set id_situacao = ?, data_hora_evento = ?, detalhamento = ?, existe_arma_envolvida = ?, permite_atendimento = ?\r\n"
	    		+ "where id_registro_fato = ?";

	    try (Connection conn = Conexao.getConexao();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	    	
	    	ps.setInt(1, fato.getIdStatus());
	    	ps.setString(2, fato.getDataHoraOcorrido());
	    	ps.setString(3, fato.getDetalhamentoFato());
	    	ps.setInt(4, fato.getEnvolvimentoArmas());
	    	ps.setInt(5, fato.getAtendimentoPermitido());
	    	ps.setLong(6, idRegistroFato);

	        int rows = ps.executeUpdate();

	        return rows > 0; // retorna true se atualizou algum registro
	    }
	}
	
	public static RegistroDeFatos BuscarResultadoRegistroFato(List<Long> idsRegistroFato, Paginacao paginacao) throws SQLException, ConexaoException{

		RegistroDeFatos retorno = new RegistroDeFatos();
		Map<Long, RegistroDeFato> registrosMap = new LinkedHashMap<>();
		
		StringBuilder sbSQL = new StringBuilder();
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		boolean erro = false;
		String msgErro = "";
		
		try {		
		    sbSQL.append(" SELECT rf.id, "); 
		    sbSQL.append("rf.id_status, ");
		    sbSQL.append("rs.descricao AS status, ");
		    sbSQL.append("rf.id_tipo, ");
		    sbSQL.append("rft.tipo_desc AS tipo_registro, ");
		    sbSQL.append("rf.data_criacao, ");
		    sbSQL.append("rf.data_encerramento, ");
		    sbSQL.append("rf.id_usuario, ");
		    sbSQL.append("su.usuario AS usuario_criacao, ");
		    sbSQL.append("su.nome AS nome_usuario_criacao, ");
		    sbSQL.append("rfe.id AS endereco_id, ");
		    sbSQL.append("rfe.id_cidade, ");
		    sbSQL.append("rfe.bairro, "); 
		    sbSQL.append("rfe.rua, ");
		    sbSQL.append("rfe.numero, "); 
		    sbSQL.append("rfe.cep, ");
		    sbSQL.append("rfe.complemento, ");
		    sbSQL.append("rfi.id AS individuo_id, ");
		    sbSQL.append("rfi.nome AS individuo_nome, ");
		    sbSQL.append("rfi.cpf AS individuo_cpf, ");
		    sbSQL.append("rfv.id AS veiculo_id, ");
		    sbSQL.append("rfv.placa AS veiculo_placa, ");
		    sbSQL.append("rfv.marca AS veiculo_marca, ");
		    sbSQL.append("rfv.cor AS veiculo_cor, ");
		    sbSQL.append("rfv.modelo AS veiculo_modelo, ");
		    sbSQL.append("c.id AS cidade_id, ");
		    sbSQL.append("c.nome AS cidade_nome, ");
		    sbSQL.append("c.id_estado ");
		    sbSQL.append("FROM muralha.registro_fato rf ");
		    sbSQL.append("LEFT JOIN muralha.registro_fato_status rs ON rs.id = rf.id_status "); 
		    sbSQL.append("LEFT JOIN muralha.registro_fato_tipo rft ON rft.id = rf.id_tipo ");
		    sbSQL.append("LEFT JOIN sis_usuario su ON su.id_usuario = rf.id_usuario ");
		    sbSQL.append("LEFT JOIN muralha.registro_fato_endereco rfe ON rfe.id_registro_fato = rf.id " ); 
		    sbSQL.append("LEFT JOIN muralha.cidade c ON c.id = rfe.id_cidade ");
		    sbSQL.append("LEFT JOIN muralha.registro_fato_individuo rfi ON rfi.id_registro_fato = rf.id " ); 
		    sbSQL.append("LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id " );
		    sbSQL.append("WHERE rf.id IN (");
		    
		    //Monta dinâmicamente a lista de ids de registro de fato para realizar a pesquisa
		    for (int i = 0; i < idsRegistroFato.size(); i++) {
		        sbSQL.append("?");
		        if (i < idsRegistroFato.size() - 1) sbSQL.append(", ");
		    }
		    sbSQL.append(") ");
		    
		    conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());
		    
	        //Adiciona os ids para a pesquisa
		    int paramIndex = 1;
		    for (Long id : idsRegistroFato) {
		        ps.setLong(paramIndex++, id);
		    }
			
		    if (!erro) {
		        rs = ps.executeQuery();
		
		        while (rs.next()) {
		            Long idRegistro = rs.getLong("id");
		            RegistroDeFato item = registrosMap.get(idRegistro);
		
		            if (item == null) {
		                item = new RegistroDeFato();
		                item.setId(idRegistro);
		                item.setIdTipo(rs.getInt("id_tipo"));
		                item.setIdStatus(rs.getInt("id_status"));
		                item.setDataCriacao(rs.getTimestamp("data_criacao"));
		                item.setDataEncerramento(rs.getTimestamp("data_encerramento"));
		                item.setIdUsuario(rs.getInt("id_usuario"));
		                item.setTipoDescricao(rs.getString("tipo_registro"));
		                item.setStatusDescricao(rs.getString("status"));
		                item.setNomeUsuario(rs.getString("nome_usuario_criacao"));
		                registrosMap.put(idRegistro, item);
		            }
		
		            Integer idIndividuo = rs.getInt("individuo_id");
		            if (idIndividuo != null && idIndividuo > 0) {
		                boolean jaAdicionado = item.getIndividuos().stream()
		                    .anyMatch(i -> i.getId() != null && i.getId().equals(idIndividuo));
		                
		                if (!jaAdicionado) {
		                    RegistroDeFatoIndividuo individuo = new RegistroDeFatoIndividuo();
		                    individuo.setId(idIndividuo);
		                    individuo.setIdRegistroFato(idRegistro); 
		                    individuo.setNome(rs.getString("individuo_nome"));
		                    individuo.setCpf(rs.getString("individuo_cpf"));
		                    item.getIndividuos().add(individuo);
		                }
		            }
		            
		            Integer idVeiculo = rs.getObject("veiculo_id") != null ? rs.getInt("veiculo_id") : null;
		            if (idVeiculo != null && idVeiculo > 0) {
		                boolean jaAdicionado = item.getVeiculos().stream()
		                    .anyMatch(v -> v.getId().equals(idVeiculo));
		
		                if (!jaAdicionado) {
		                    RegistroDeFatoVeiculo veiculo = new RegistroDeFatoVeiculo();
		                    veiculo.setId(idVeiculo);
		                    veiculo.setIdRegistroFato(idRegistro);
		                    veiculo.setPlaca(rs.getString("veiculo_placa"));
		                    veiculo.setCor(rs.getString("veiculo_cor"));
		                    veiculo.setMarca(rs.getString("veiculo_marca"));
		                    veiculo.setModelo(rs.getString("veiculo_modelo"));
		
		                    item.getVeiculos().add(veiculo);
		                }
		            }
		            
		            Integer idEndereco = rs.getObject("endereco_id") != null ? rs.getInt("endereco_id") : null;
		            if (idEndereco != null && idEndereco > 0) {
		                boolean jaAdicionado = item.getEnderecos().stream()
		                    .anyMatch(e -> e.getId() != null && e.getId().equals(idEndereco));
		
		                if (!jaAdicionado) {
		                    RegistroDeFatoEndereco endereco = new RegistroDeFatoEndereco();
		                    endereco.setId(idEndereco);
		                    endereco.setIdRegistroFato(idRegistro);
		                    endereco.setIdCidade(rs.getObject("id_cidade") != null ? rs.getInt("id_cidade") : null);
		                    endereco.setBairro(rs.getString("bairro"));
		                    endereco.setRua(rs.getString("rua"));
		                    endereco.setNumero(rs.getInt("numero"));
		                    endereco.setCep(rs.getString("cep"));
		                    endereco.setComplemento(rs.getString("complemento"));
		                    
		                    // cidade
		                    Cidade cidade = new Cidade();
		                    cidade.setId(rs.getInt("cidade_id"));
		                    cidade.setNome(rs.getString("cidade_nome"));
		                    cidade.setIdEstado(rs.getInt("id_estado"));
		                    endereco.setCidade(cidade);
		
		                    item.getEnderecos().add(endereco);
		                }
		            }
		        }
		
		        retorno.setListaBoletins(new ArrayList<>(registrosMap.values()));
		        retorno.setPaginacao(paginacao);
		    }
		
		} catch (Exception e) {
		    erro = true;
		    msgErro = "Erro ao obter registros de fato!";
		    logger.error(msgErro + ": " + e.getMessage(), e);
		} finally {
		    try {
		        if (rs != null) rs.close();
		        if (ps != null) ps.close();
		        if (conn != null) conn.close();
		    } catch (Exception e) {
		        logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
		    }
		
		    if (erro) throw new SQLException("Erro ao consultar registros de fato!");
		    }
		
		    return retorno;
		}
	
	public static RegistroDeFato ObterRegistroDeFatoPorId(Long id, Integer idUsuarioLogado) throws ConexaoException, SQLException {
	    RegistroDeFato item = null;

	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    StringBuilder sbSQL = new StringBuilder();

	    try {  	
	    	sbSQL.append(" SELECT ");
	    	sbSQL.append(" rf.id, rf.id_status, rs.descricao AS status, rf.id_tipo, rf.id_tipo_natureza, rft.tipo_desc AS tipo_registro, rf.tem_boletim,");
	    	sbSQL.append(" rf.data_criacao, rf.data_encerramento, rf.data_modificacao, rf.data_evento, rf.id_usuario, su.usuario AS usuario_criacao, su.nome AS nome_usuario_criacao,");
	    	sbSQL.append(" rf.privado, ");
	    	sbSQL.append(" rfn.natureza_desc AS natureza_tipo_descricao, ");
	    	sbSQL.append(" rfe.id AS endereco_id, rfe.id_cidade, rfe.id_tipo_evento, rfe.bairro, rfe.rua, rfe.numero, rfe.cep, rfe.complemento,");
	    	sbSQL.append(" rfe.latitude, rfe.longitude,");
	    	sbSQL.append(" rfi.id AS individuo_id, rfi.nome AS individuo_nome, rfi.cpf AS individuo_cpf,");
	    	sbSQL.append(" rfi.id_tipo_envolvimento AS individuo_id_tipo_envolvimento,");
	    	sbSQL.append(" rfit.descricao AS individuo_tipo_envolvimento_desc,");
	    	sbSQL.append(" rfi.detalhe_envolvimento AS individuo_detalhe_envolvimento,");
	    	sbSQL.append(" rfi.ddd AS individuo_ddd, rfi.telefone AS individuo_telefone, rfi.email AS individuo_email,");
	    	sbSQL.append(" rfv.id AS veiculo_id, rfv.placa AS veiculo_placa, rfv.marca AS veiculo_marca, rfv.cor AS veiculo_cor, rfv.modelo AS veiculo_modelo,");
	    	sbSQL.append(" c.id AS cidade_id, c.nome AS cidade_nome, c.id_estado,");
	    	sbSQL.append(" b.id AS boletim_id, b.id_situacao AS boletim_situacao, b.data_hora_evento, b.detalhamento,");
	    	sbSQL.append(" b.id_usuario AS boletim_id_usuario, b.data_criacao AS boletim_data_criacao,");
	    	sbSQL.append(" b.data_encerramento AS boletim_data_encerramento, b.permite_atendimento,");
	    	sbSQL.append(" bs.id AS boletim_situacao_id, bs.descricao AS boletim_situacao_descricao,");
	    	sbSQL.append(" rfo.id AS objeto_id, rfo.tipo AS objeto_tipo, rfo.descricao AS objeto_descricao,");
	    	sbSQL.append(" ba.id AS apreensao_id, ba.tipo AS apreensao_tipo, ba.descricao AS apreensao_descricao,");
	    	sbSQL.append(" rd.id AS documento_id, rd.tipo AS documento_tipo, rd.dir_arquivo AS documento_dir_arquivo, rd.detalhamento AS documento_detalhamento, rd.id_atendimento AS documento_id_atendimento,");
	    	sbSQL.append(" rl.id AS link_id, rl.url AS link_url, rl.detalhamento AS link_detalhamento,");
	    	sbSQL.append(" rfg.id AS grupo_id, rfg.id_usuario AS grupo_id_usuario, rfg.id_grupo AS grupo_id_grupo,");
	    	sbSQL.append(" rfpv.id AS passagem_id, rfpv.id_registro_fato AS passagem_id_registro_fato,");
	    	sbSQL.append(" rfpv.id_veiculo AS passagem_id_veiculo, rfpv.id_usuario AS passagem_id_usuario, rfpv.data AS passagem_data,");
	    	sbSQL.append(" vtr.placa AS passagem_placa, vtr.id AS passagem_id_veiculo_tempo_real, ");
	    	sbSQL.append(" vtr.data as data_passagem_veiculo, ");
	    	sbSQL.append(" rfee.descricao AS endereco_evento_descricao, ");
	    	sbSQL.append(" f.id AS fato_id, f.id_situacao AS fato_id_situacao, f.data_hora_evento AS fato_data_hora_evento, f.detalhamento AS fato_detalhamento, ");
	    	sbSQL.append(" f.existe_arma_envolvida AS fato_existe_arma_envolvida, f.id_usuario AS fato_id_usuario, f.data_criacao AS fato_data_criacao, ");
	    	sbSQL.append(" f.data_encerramento AS fato_data_encerramento, f.permite_atendimento AS fato_permite_atendimento, ");
	    	
	    	sbSQL.append(" su_anotacao.nome AS anotacao_usuario_nome, ");
	    	sbSQL.append(" su_anotacao.usuario AS anotacao_usuario_login, ");
	    	
	    	sbSQL.append(" rfa.id AS anotacao_id, rfa.texto AS anotacao_texto, ");
	    	sbSQL.append(" rfa.id_usuario AS anotacao_id_usuario, rfa.data_criacao AS anotacao_data_criacao ");
	    	
	    	sbSQL.append(" FROM muralha.registro_fato rf");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_status rs ON rs.id = rf.id_status");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_tipo rft ON rft.id = rf.id_tipo");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_natureza rfn ON rfn.id_registro_tipo = rf.id_tipo ");
	    	sbSQL.append(" LEFT JOIN sis_usuario su ON su.id_usuario = rf.id_usuario");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_endereco rfe ON rfe.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.cidade c ON c.id = rfe.id_cidade");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_individuo rfi ON rfi.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_veiculo rfv ON rfv.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.boletim b ON b.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.boletim_situacao bs ON bs.id = b.id_situacao");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_objeto rfo ON rfo.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.boletim_apreensao ba ON ba.id_boletim = b.id");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_individuo_tipo rfit ON rfit.id = rfi.id_tipo_envolvimento");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_documento rd ON rd.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_link rl ON rl.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_usuario_grupo rfg ON rfg.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_passagem_veic rfpv ON rfpv.id_registro_fato = rf.id");
	    	sbSQL.append(" LEFT JOIN muralha.veiculo_tempo_real vtr ON rfpv.id_veiculo = vtr.id ");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_endereco_evento rfee ON rfee.id = rfe.id_tipo_evento ");
	    	sbSQL.append(" LEFT JOIN muralha.fato f ON f.id_registro_fato = rf.id ");
	    	sbSQL.append(" LEFT JOIN muralha.registro_fato_anotacao rfa ON rfa.id_registro_fato = rf.id ");
	    	sbSQL.append(" LEFT JOIN sis_usuario su_anotacao ON su_anotacao.id_usuario = rfa.id_usuario ");
	    	
	    	sbSQL.append(" WHERE rf.id = ?");
	    	sbSQL.append(" AND (rf.privado = 0 OR rf.id_usuario = ?)");

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString());
	        ps.setLong(1, id);              // ID do registro de fato
	        ps.setInt(2, idUsuarioLogado); // só permite visualizar se público ou dono
	        
	        rs = ps.executeQuery();

	        while (rs.next()) {
	            if (item == null) {
	                item = new RegistroDeFato();
	                item.setId(rs.getLong("id"));
	                item.setIdTipo(rs.getInt("id_tipo"));
	                item.setIdNaturezaTipo(rs.getObject("id_tipo_natureza") != null ? rs.getInt("id_tipo_natureza") : null);
	                item.setIdStatus(rs.getInt("id_status"));
	                item.setPrivado(rs.getInt("privado"));
	                item.setDataCriacao(rs.getTimestamp("data_criacao"));
	                item.setDataEncerramento(rs.getTimestamp("data_encerramento"));
	                item.setDataModificada(rs.getTimestamp("data_modificacao"));
	                item.setIdUsuario(rs.getInt("id_usuario"));
	                item.setTipoDescricao(rs.getString("tipo_registro"));
	                item.setStatusDescricao(rs.getString("status"));
	                item.setNomeUsuario(rs.getString("nome_usuario_criacao"));
	                item.setTemBoletim(rs.getInt("tem_boletim"));
	                item.setDataEvento(rs.getTimestamp("data_evento"));
	                if (item.getIdNaturezaTipo() != null) {

	                    RegistroDeFatoNatureza natureza = new RegistroDeFatoNatureza();
	                    natureza.setId(item.getIdNaturezaTipo());
	                    natureza.setNaturezaDesc(rs.getString("natureza_tipo_descricao"));
	                    natureza.setIdRegistroTipo(item.getIdTipo());
	                    
	                    item.setNaturezaTipo(natureza);
	                }
	            }
	            
	            //fato
	            Integer idFato = rs.getObject("fato_id") != null ? rs.getInt("fato_id") : null;
	            if (idFato != null && item.getFatos().stream().noneMatch(f -> idFato.equals(f.getId()))) {
	                Fato fato = new Fato();
	                fato.setId(idFato);
	                fato.setIdRegistroFato(item.getId());
	                fato.setIdSituacao(rs.getInt("fato_id_situacao"));
	                fato.setDataHoraEvento(rs.getTimestamp("fato_data_hora_evento"));
	                fato.setDetalhamento(rs.getString("fato_detalhamento"));
	                fato.setExisteArmaEnvolvida(rs.getInt("fato_existe_arma_envolvida"));
	                fato.setIdUsuario(rs.getInt("fato_id_usuario"));
	                fato.setDataCriacao(rs.getTimestamp("fato_data_criacao"));
	                fato.setDataEncerramento(rs.getTimestamp("fato_data_encerramento"));
	                fato.setPermiteAtendimento(rs.getInt("fato_permite_atendimento"));
	                item.getFatos().add(fato);
	            }

	            // Individuos
	            Integer idIndividuo = rs.getObject("individuo_id") != null ? rs.getInt("individuo_id") : null;
	            if (idIndividuo != null && item.getIndividuos().stream().noneMatch(i -> idIndividuo.equals(i.getId()))) {
	            	RegistroDeFatoIndividuo individuo = new RegistroDeFatoIndividuo();
	            	individuo.setId(rs.getInt("individuo_id"));
	            	individuo.setIdRegistroFato(item.getId());
	            	individuo.setNome(rs.getString("individuo_nome"));
	            	individuo.setCpf(rs.getString("individuo_cpf"));
	            	individuo.setIdTipoEnvolvimento(
	            	    rs.getObject("individuo_id_tipo_envolvimento") != null ? rs.getInt("individuo_id_tipo_envolvimento") : null
	            	);
	            	individuo.setDetalheEnvolvimento(rs.getString("individuo_detalhe_envolvimento"));
	            	individuo.setDdd(
	            	    rs.getObject("individuo_ddd") != null ? rs.getInt("individuo_ddd") : null
	            	);
	            	individuo.setTelefone(
	            	    rs.getObject("individuo_telefone") != null ? rs.getString("individuo_telefone") : null
	            	);
	            	individuo.setEmail(rs.getString("individuo_email"));

	            	// Preencher o objeto tipoEnvolvimento
	            	RegistroDeFatoIndividuoTipo tipo = new RegistroDeFatoIndividuoTipo();
	            	tipo.setId(individuo.getIdTipoEnvolvimento());
	            	tipo.setDescricao(rs.getString("individuo_tipo_envolvimento_desc"));
	            	individuo.setTipoEnvolvimento(tipo);
	                item.getIndividuos().add(individuo);
	            }

	            // Veículos
	            Integer idVeiculo = rs.getObject("veiculo_id") != null ? rs.getInt("veiculo_id") : null;
	            if (idVeiculo != null && item.getVeiculos().stream().noneMatch(v -> idVeiculo.equals(v.getId()))) {
	                RegistroDeFatoVeiculo veiculo = new RegistroDeFatoVeiculo();
	                veiculo.setId(idVeiculo);
	                veiculo.setIdRegistroFato(item.getId());
	                veiculo.setPlaca(rs.getString("veiculo_placa"));
	                veiculo.setCor(rs.getString("veiculo_cor"));
	                veiculo.setMarca(rs.getString("veiculo_marca"));
	                veiculo.setModelo(rs.getString("veiculo_modelo"));
	                item.getVeiculos().add(veiculo);
	            }

	            // Endereço
	            Integer idEndereco = rs.getObject("endereco_id") != null ? rs.getInt("endereco_id") : null;
	            if (idEndereco != null && item.getEnderecos().stream().noneMatch(e -> idEndereco.equals(e.getId()))) {
	                RegistroDeFatoEndereco endereco = new RegistroDeFatoEndereco();
	                endereco.setId(idEndereco);
	                endereco.setIdRegistroFato(item.getId());
	                endereco.setIdCidade(rs.getObject("id_cidade") != null ? rs.getInt("id_cidade") : null);
	                endereco.setIdTipoEvento(rs.getObject("id_tipo_evento") != null ? rs.getInt("id_tipo_evento") : null);
	                endereco.setBairro(rs.getString("bairro"));
	                endereco.setRua(rs.getString("rua"));
	                endereco.setNumero(rs.getInt("numero"));
	                endereco.setCep(rs.getString("cep"));
	                endereco.setComplemento(rs.getString("complemento"));
	                
	                // Latitude e longitude
	                endereco.setLatitude(rs.getObject("latitude") != null ? rs.getBigDecimal("latitude") : null);
	                endereco.setLongitude(rs.getObject("longitude") != null ? rs.getBigDecimal("longitude") : null);

	                Cidade cidade = new Cidade();
	                cidade.setId(rs.getInt("cidade_id"));
	                cidade.setNome(rs.getString("cidade_nome"));
	                cidade.setIdEstado(rs.getInt("id_estado"));
	                endereco.setCidade(cidade);
	                
	                RegistroDeFatoEnderecoEvento evento = new RegistroDeFatoEnderecoEvento();
	                evento.setId(endereco.getIdTipoEvento());
	                evento.setDescricao(rs.getString("endereco_evento_descricao"));
	                endereco.setEvento(evento);

	                item.getEnderecos().add(endereco);
	            }

	            //Boletim
	            int idBoletim = rs.getInt("boletim_id");
	            if (!rs.wasNull()) {
	                Boletim boletim = item.getBoletins()
	                    .stream()
	                    .filter(b -> b.getId().equals(idBoletim))
	                    .findFirst()
	                    .orElse(null);

	                if (boletim == null) {
	                    boletim = new Boletim();
	                    boletim.setId(idBoletim);
	                    boletim.setIdRegistroFato(item.getId());
	                    boletim.setIdSituacao(rs.getInt("boletim_situacao"));

	                    BoletimSituacao situacao = new BoletimSituacao();
	                    situacao.setId(rs.getObject("boletim_situacao_id") != null ? rs.getInt("boletim_situacao_id") : null);
	                    situacao.setDescricao(rs.getString("boletim_situacao_descricao"));
	                    boletim.setSituacao(situacao);

	                    boletim.setDataHoraEvento(rs.getTimestamp("data_hora_evento"));
	                    boletim.setDetalhamento(rs.getString("detalhamento"));
	                    boletim.setIdUsuario(rs.getInt("boletim_id_usuario"));
	                    boletim.setDataCriacao(rs.getTimestamp("boletim_data_criacao"));
	                    boletim.setDataEncerramento(rs.getTimestamp("boletim_data_encerramento"));
	                    boletim.setPermiteAtendimento(rs.getInt("permite_atendimento"));

	                    boletim.setApreensoes(new ArrayList<>()); // Garante lista
	                    item.getBoletins().add(boletim);
	                }

	                // Apreensão
	                Integer idApreensao = rs.getObject("apreensao_id") != null ? rs.getInt("apreensao_id") : null;
	                if (idApreensao != null) {
	                    boolean jaExiste = boletim.getApreensoes()
	                        .stream()
	                        .anyMatch(a -> idApreensao.equals(a.getId()));

	                    if (!jaExiste) {
	                        BoletimApreensao apreensao = new BoletimApreensao();
	                        apreensao.setId(idApreensao);
	                        apreensao.setIdBoletim(boletim.getId());
	                        apreensao.setTipo(rs.getString("apreensao_tipo"));
	                        apreensao.setDescricao(rs.getString("apreensao_descricao"));
	                        boletim.getApreensoes().add(apreensao);
	                    }
	                }
	            }
	            
	            //objeto
	            Integer idObjeto = rs.getObject("objeto_id") != null ? rs.getInt("objeto_id") : null;
	            if (idObjeto != null && item.getObjetos().stream().noneMatch(o -> idObjeto.equals(o.getId()))) {
	                RegistroDeFatoObjeto objeto = new RegistroDeFatoObjeto();
	                objeto.setId(idObjeto);
	                objeto.setIdRegistroFato(item.getId());
	                objeto.setTipo(rs.getString("objeto_tipo"));
	                objeto.setDescricao(rs.getString("objeto_descricao"));
	                item.getObjetos().add(objeto);
	            }
	            
	            // Documentos
	            Integer idDocumento = rs.getObject("documento_id") != null ? rs.getInt("documento_id") : null;
	            if (idDocumento != null && item.getDocumentos().stream().noneMatch(d -> idDocumento.equals(d.getId()))) {
	                RegistroDeFatoDocumento documento = new RegistroDeFatoDocumento();
	                documento.setId(idDocumento);
	                documento.setIdRegistroFato(item.getId());
	                documento.setTipo(rs.getString("documento_tipo"));
	                documento.setDirArquivo(rs.getString("documento_dir_arquivo"));
	                documento.setDetalhamento(rs.getString("documento_detalhamento"));
	                documento.setIdAtendimento(rs.getObject("documento_id_atendimento") != null ? rs.getInt("documento_id_atendimento") : null);

	                item.getDocumentos().add(documento);
	            }
	            
	            // Link
	            Integer idLink = rs.getObject("link_id") != null ? rs.getInt("link_id") : null;
	            if (idLink != null && item.getLinks().stream().noneMatch(l -> idLink.equals(l.getId()))) {
	                RegistroDeFatoLink link = new RegistroDeFatoLink();
	                link.setId(idLink);
	                link.setIdRegistroFato(item.getId());
	                link.setUrl(rs.getString("link_url"));
	                link.setDetalhamento(rs.getString("link_detalhamento"));
	                item.getLinks().add(link);
	            }
	            
	          //Grupos
	            Integer idGrupoRegistro = rs.getObject("grupo_id") != null ? rs.getInt("grupo_id") : null;
	            if (idGrupoRegistro != null && item.getUsuarioGrupos().stream().noneMatch(g -> idGrupoRegistro.equals(g.getId()))) {
	                RegistroDeFatoUsuarioGrupo grupo = new RegistroDeFatoUsuarioGrupo();
	                grupo.setId(idGrupoRegistro);
	                grupo.setIdRegistroFato(item.getId());

	                Integer idUsuarioGrupo = rs.getObject("grupo_id_usuario") != null ? rs.getInt("grupo_id_usuario") : null;
	                Integer idGrupo = rs.getObject("grupo_id_grupo") != null ? rs.getInt("grupo_id_grupo") : null;

	                grupo.setIdUsuario(idUsuarioGrupo);
	                grupo.setIdGrupo(idGrupo);
	                item.getUsuarioGrupos().add(grupo);
	            }
	            
	            // Passagens de Veículos
	            Integer idPassagem = rs.getObject("passagem_id") != null ? rs.getInt("passagem_id") : null;
	            if (idPassagem != null && item.getPassagensVeiculo().stream().noneMatch(p -> idPassagem.equals(p.getId()))) {
	                RegistroFatoPassagemVeiculo passagem = new RegistroFatoPassagemVeiculo();
	                passagem.setId(idPassagem);
	                passagem.setIdRegistroFato(rs.getLong("passagem_id_registro_fato"));
	                passagem.setIdVeiculo(rs.getObject("passagem_id_veiculo") != null ? UUID.fromString(rs.getString("passagem_id_veiculo")) : null);
	                passagem.setIdUsuario(rs.getObject("passagem_id_usuario") != null ? rs.getInt("passagem_id_usuario") : null);
	                passagem.setData(rs.getTimestamp("passagem_data"));
	                passagem.setPlaca(rs.getString("passagem_placa"));
	                passagem.setIdVeiculoTempoReal(
	                	    rs.getObject("passagem_id_veiculo_tempo_real") != null ? UUID.fromString(rs.getString("passagem_id_veiculo_tempo_real")) : null
	                	);
	                passagem.setData_vinculo(rs.getTimestamp("data_passagem_veiculo"));
	                item.getPassagensVeiculo().add(passagem);
	            }
	            
	         // Anotações
	            Integer idAnotacao = rs.getObject("anotacao_id") != null ? rs.getInt("anotacao_id") : null;

	            if (idAnotacao != null &&
	                item.getAnotacoes().stream().noneMatch(a -> idAnotacao.equals(a.getId()))) {

	            	RegistroDeFatoAnotacao anotacao = new RegistroDeFatoAnotacao();
	                anotacao.setId(idAnotacao);
	                anotacao.setIdRegistroFato(item.getId());
	                anotacao.setTexto(rs.getString("anotacao_texto"));
	                anotacao.setIdUsuario(rs.getInt("anotacao_id_usuario"));
	                anotacao.setDataCriacao(rs.getTimestamp("anotacao_data_criacao"));
	                anotacao.setNomeUsuario(rs.getString("anotacao_usuario_nome"));

	                item.getAnotacoes().add(anotacao);
	            }
	        }

	    } catch (Exception e) {
	        logger.error("Erro ao obter registro de fato com boletim: " + e.getMessage(), e);
	        throw new SQLException("Erro ao consultar registro de fato!");
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }
	    }
	    
	    if (item != null && item.getEnderecos() != null) {
	        item.getEnderecos().sort(Comparator.comparingInt(RegistroDeFatoEndereco::getId));
	    }

	    return item;
	}
	
	public static int CadastrarFato(Long idRegistroFato, RegistroFatoDTO registroFato, int idUsuario) 
	        throws ConexaoException, SQLException {

	    StringBuilder sbSQL = new StringBuilder();
	    Connection conn = null;
	    PreparedStatement ps = null;
	    ResultSet rs = null;

	    boolean erro = false;
	    String msgErro = "";
	    int idGerado = -1;	    

	    try {
	        sbSQL.append("INSERT INTO muralha.fato ");
	        sbSQL.append("(id_registro_fato, id_situacao, data_hora_evento, detalhamento, existe_arma_envolvida, id_usuario, data_criacao, data_encerramento, permite_atendimento) ");
	        sbSQL.append("VALUES (?, 1, ?, ?, ?, ?, GETDATE(), NULL, ?)");
	        
	        Calendar calendarioUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"));

	        conn = Conexao.getConexao();
	        ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);

	        ps.setLong(1, idRegistroFato);
	        ps.setString(2, registroFato.getDataHoraOcorrido());
	        ps.setString(3, registroFato.getDetalhamentoFato());
	        ps.setInt(4, registroFato.getEnvolvimentoArmas());
	        ps.setInt(5, idUsuario);
	        ps.setInt(6, registroFato.getAtendimentoPermitido());

	        int linhasAfetadas = ps.executeUpdate();

	        if (linhasAfetadas > 0) {
	            rs = ps.getGeneratedKeys();
	            if (rs.next()) {
	                idGerado = rs.getInt(1);
	            }
	        }

	    } catch (Exception e) {
	        erro = true;
	        msgErro = "Erro ao cadastrar fato!";
	        logger.error(msgErro + ": " + e.getMessage(), e);
	    } finally {
	        try {
	            if (rs != null) rs.close();
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            logger.error("Erro ao fechar recursos: " + e.getMessage(), e);
	        }

	        if (erro) throw new SQLException(msgErro);
	    }

	    return idGerado;
	}
}