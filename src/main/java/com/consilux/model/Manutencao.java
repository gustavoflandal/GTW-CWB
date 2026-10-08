package com.consilux.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.lib.UShort;
import com.consilux.model.exception.ModelException;
import com.consilux.ui.client.beans.ManutencaoAtividadeGwtBean;
import com.consilux.ui.client.beans.ManutencaoGwtBean;

/**
 * Classe de negócio, utilizada para representar manutenção de equipamentos.
 * @author raoni
 */
public class Manutencao {

	private int idManutencao;
	private int idUsuario;
	private int idConfiguracaoEquipamento;
	
	private int idTecnico;
	private Date dataCriacao;
	private Date dataUtilizacao;
	
	private String senha;
	private boolean acessoParam;
	private boolean realizada;
	
	private Date dataInicio;
	private Date dataFim;
	private String solucao;
	private List<ManutencaoAtividade> listaAtividades;

	public Manutencao() {
		this.idManutencao = 0;
		this.idUsuario = 0;
		this.senha = "";
		this.idConfiguracaoEquipamento = 0;
		this.idTecnico = 0;
		this.dataCriacao = new Date();
		this.dataUtilizacao = new Date();
		this.realizada = false;
		this.acessoParam = false;
		this.dataInicio = null; 
		this.dataFim = null;
		this.solucao = null;
		this.listaAtividades = new ArrayList<ManutencaoAtividade>();		
	}
	
	public static Manutencao fromGwtBean(ManutencaoGwtBean bean)
		throws ModelException, ConexaoException, SQLException {
		
		if (bean == null)
			throw new ModelException("Bean de manutenção nulo");
		
		Manutencao mnu = new Manutencao();
		mnu.idManutencao = bean.getIdManutencao();

		String usuCriador = bean.getNomeUsuario();
		usuCriador = usuCriador != null ? usuCriador.trim() : "";
		Usuario usuarioCriador = Usuario.buscaUsuarioPorUsuario(usuCriador);
		if (usuarioCriador == null)
			throw new ModelException("Não foi possível localizar o usuário.");

		String nomeTecnico = bean.getNomeTecnico();
		nomeTecnico = nomeTecnico != null ? nomeTecnico.trim() : "";
		Usuario tecnico = Usuario.buscaUsuarioPorNome(nomeTecnico);
		if (tecnico == null)
			throw new ModelException("Não foi possível localizar o técnico.");
		
		LocalVigente local = null;
		try {
			local = LocalVigente.buscaLocalVigentePorIdLocal(bean.getIdLocal());
			if (local == null)
				throw new ConexaoException("Não foi possível localizar o local.");
		} catch (SQLException e) {
			throw new ConexaoException("Erro de SQL");			
		}
		
		mnu.idUsuario = usuarioCriador.getId();
		mnu.idTecnico = tecnico.getId();
		mnu.senha = bean.getSenha();
		mnu.idConfiguracaoEquipamento = local.getIdConfiguracaEquipamento(); 
		mnu.dataCriacao = bean.getDataCriacao();
		mnu.dataUtilizacao = bean.getDataUtilizacao();
		mnu.realizada = bean.isRealizada();
		mnu.acessoParam = bean.isAcessoParam();
		mnu.dataInicio = bean.getDataInicio(); 
		mnu.dataFim = bean.getDataFim();
		mnu.solucao = bean.getSolucao();
		mnu.listaAtividades = new ArrayList<ManutencaoAtividade>();
		ManutencaoAtividade atv;
		for (ManutencaoAtividadeGwtBean atvBean : bean.getAtividades()) {
			atv = new ManutencaoAtividade(atvBean.getIdManutencaoAtividade(),
					atvBean.getIdManutencao(), atvBean.getPista(),
					atvBean.getManutencaoDescricao().getIdManutencaoDescricao());
			mnu.listaAtividades.add(atv);
		}
		
		return mnu;
	}
	
	public int getIdManutencao() {
		return idManutencao;
	}

	public void setIdManutencao(int id_manutencao) {
		this.idManutencao = id_manutencao;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int id_usuario) {
		this.idUsuario = id_usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public int getIdTecnico() {
		return idTecnico;
	}

	public void setIdTecnico(int id_tecnico) {
		this.idTecnico = id_tecnico;
	}

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public Date getDataUtilizacao() {
		return dataUtilizacao;
	}

	public void setDataUtilizacao(Date dataUtilizacao) {
		this.dataUtilizacao = dataUtilizacao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public boolean isRealizada() {
		return realizada;
	}

	public void setRealizada(boolean realizada) {
		this.realizada = realizada;
	}

	public String getSolucao() {
		return solucao;
	}

	public void setSolucao(String solucao) {
		this.solucao = solucao;
	}

	public boolean isAcessoParam() {
		return acessoParam;
	}

	public void setAcessoParam(boolean acessoParam) {
		this.acessoParam = acessoParam;
	}

	public int getIdConfiguracaoEquipamento() {
		return idConfiguracaoEquipamento;
	}

	public void setIdConfiguracaoEquipamento(int id_configuracao_equipamento) {
		this.idConfiguracaoEquipamento = id_configuracao_equipamento;
	}

	public List<ManutencaoAtividade> getAtividades() {
		return Collections.unmodifiableList(listaAtividades);		
	}

	/**
	 * Busca as manutenções pendentes do BD.
	 * @return Lista com objetos ManutencaoGwtBean materializados.
	 * @throws ConexaoException
	 */	
	public static List<ManutencaoGwtBean> listarManutencaoPendente() throws ConexaoException {
		
		List<ManutencaoGwtBean> lRet = new ArrayList<ManutencaoGwtBean>();
		ManutencaoGwtBean bean;
		StringBuilder sb = new StringBuilder();
		
		sb.append("SELECT");
		sb.append("  mnt.id_manutencao, mnt.data_criacao,");
		sb.append("  mnt.data_utilizacao, loc.id_local,");
		sb.append("  tec.usuario, mnt.senha, usu.usuario,");
		sb.append("  loc.nome,  mnt.acesso_param,");
		sb.append("  mnt.realizada, mnt.data_inicio,");
		sb.append("  mnt.data_fim, mnt.solucao");
		sb.append(" FROM");
		sb.append("  manutencao mnt WITH (NOLOCK)");
		sb.append("  INNER JOIN [local] AS loc");
		sb.append("   ON loc.id_configuracao_equipamento = mnt.id_configuracao_equipamento");
		sb.append("  INNER JOIN sis_usuario AS usu");
		sb.append("   ON usu.id_usuario = mnt.id_usuario");
		sb.append("  INNER JOIN sis_usuario AS tec");
		sb.append("   ON tec.id_usuario = mnt.id_tecnico");		
		sb.append("  WHERE");
		sb.append("  mnt.realizada = 0");
		sb.append("  AND mnt.data_utilizacao BETWEEN ? AND ?");		
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sb.toString());
			Calendar dataInferior = Calendar.getInstance();
			dataInferior.set(Calendar.HOUR_OF_DAY, 0);
			dataInferior.set(Calendar.MINUTE, 0);
			dataInferior.set(Calendar.SECOND, 0);
			dataInferior.set(Calendar.MILLISECOND, 0);
			dataInferior.add(Calendar.DATE, -30);
			Calendar dataSuperior = Calendar.getInstance();
			dataSuperior.set(Calendar.HOUR_OF_DAY, 23);
			dataSuperior.set(Calendar.MINUTE, 59);
			dataSuperior.set(Calendar.SECOND, 59);
			dataSuperior.set(Calendar.MILLISECOND, 999);
			dataSuperior.add(Calendar.DATE, 2);			

			ps.setDate(1, new java.sql.Date(dataInferior.getTime().getTime()));
			ps.setDate(2, new java.sql.Date(dataSuperior.getTime().getTime()));
			
			rs = ps.executeQuery();
			while (rs.next()) {
				bean = new ManutencaoGwtBean();
				bean.setIdManutencao(rs.getInt(1));
				bean.setDataCriacao(new Date(rs.getTimestamp(2).getTime()));
				bean.setDataUtilizacao(new Date (rs.getDate(3).getTime()));
				bean.setIdLocal(rs.getInt(4));
				bean.setNomeTecnico(rs.getString(5).trim());
				bean.setSenha(rs.getString(6));
				bean.setNomeUsuario(rs.getString(7).trim());
				bean.setNomeLocal(rs.getString(8).trim());
				bean.setAcessoParam(rs.getBoolean(9));
				bean.setRealizada(rs.getBoolean(10));
				
				java.sql.Date dataInicio = rs.getDate(11);
				java.sql.Date dataFim = rs.getDate(12);
				
				bean.setDataInicio(dataInicio != null ? new Date(dataInicio.getTime()) : null);
				bean.setDataFim(dataFim != null ? new Date(dataFim.getTime()) : null);
				
				bean.setSolucao(rs.getString(13));
				lRet.add(bean);
			}
			rs.close();
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}
		finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException e) {
				throw new ConexaoException("ERRO de SQL", e);
			}
		}
		
		return lRet;
	}	
	
	private int obterSequenciaSenha(Connection conn) throws ConexaoException{
		int nSenha = 0;
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		StringBuilder sbSQL = new StringBuilder();
		
		sbSQL.append("SELECT");
		sbSQL.append("  1 + COALESCE (COUNT(*), 0)");
		sbSQL.append(" FROM");
		sbSQL.append("  manutencao mnu WITH (NOLOCK)");
		sbSQL.append(" WHERE");
		sbSQL.append("  mnu.id_tecnico = ?");
		sbSQL.append("  AND mnu.data_utilizacao = ?");		
		sbSQL.append("  AND mnu.id_configuracao_equipamento = ?");

		
		try {
			ps = conn.prepareStatement(sbSQL.toString());
			
			ps.setInt(1, this.idTecnico);
			ps.setDate(2, new java.sql.Date(this.dataUtilizacao.getTime()));
			ps.setInt(3, this.idConfiguracaoEquipamento);
			
			rs = ps.executeQuery();
			if (!rs.next())
				throw new ConexaoException("ERRO ao tentar obter sequência de senha.");
			
			nSenha = rs.getInt(1);
			
		} catch (SQLException e) {
			throw new ConexaoException("ERRO de SQL", e);
		}
		
		return nSenha;
	}
	
	
	public static Manutencao incluirManutencao(Manutencao manutencao)
	throws ConexaoException, SQLException, ModelException {
		
		Date bean_data_inicio = manutencao.getDataInicio();
		Date bean_data_fim =  manutencao.getDataFim();
		String bean_solucao = manutencao.getSolucao();
		
		Manutencao ret = null;
		StringBuilder sbSQL = new StringBuilder();

		sbSQL.append("INSERT INTO manutencao (");
		sbSQL.append("  id_usuario,");
		sbSQL.append("  id_configuracao_equipamento,");
		sbSQL.append("  id_tecnico,");
		sbSQL.append("  data_criacao,");
		sbSQL.append("  data_utilizacao,");
		sbSQL.append("  senha,");
		sbSQL.append("  acesso_param,");
		sbSQL.append("  realizada");
		if (bean_data_inicio != null)
			sbSQL.append(", data_inicio,");
		if (bean_data_fim != null)
			sbSQL.append(", data_fim,");
		if (bean_solucao != null)
			sbSQL.append(", solucao");
		sbSQL.append("	) VALUES (?,?,?,?,?,?,?,?");
		
		if (bean_data_inicio != null)
			sbSQL.append(",?");
		if (bean_data_fim != null)
			sbSQL.append(",?");
		if (bean_solucao != null)
			sbSQL.append(",?");
		
		sbSQL.append(")");
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			int nSenha = manutencao.obterSequenciaSenha(conn);
			int idLocal = Local.buscaLocalPorIdConfigEquip(conn,
					manutencao.idConfiguracaoEquipamento).getIdLocal();
			
			String nomeTecnico = Usuario.buscaUsuarioPorIdUsuario(
					conn, manutencao.idTecnico).getUsuario();
			
			ps = conn.prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);
			
			//Ajustando os valores dos parametros:		
			ps.setInt(1, manutencao.getIdUsuario());
			ps.setInt(2, manutencao.getIdConfiguracaoEquipamento());
			ps.setInt(3, manutencao.getIdTecnico());
			ps.setTimestamp(4, new Timestamp(manutencao.getDataCriacao().getTime()));
			ps.setDate(5, new java.sql.Date(manutencao.getDataUtilizacao().getTime()));
			
			manutencao.setSenha(Manutencao.gerarSenha(idLocal, nomeTecnico.toUpperCase(),
					manutencao.dataUtilizacao, nSenha, manutencao.isAcessoParam()));
			
			ps.setString(6, manutencao.getSenha());
			ps.setBoolean(7, manutencao.isAcessoParam());
			ps.setBoolean(8, manutencao.isRealizada());			

			// Código para inserir as datas (apenas se necessário)
			int i = 9;
			if (bean_data_inicio != null) {
				ps.setDate(i, new java.sql.Date(bean_data_inicio.getTime()));
				i++;
			}
			if (bean_data_fim != null) {
				ps.setDate(i, new java.sql.Date(bean_data_fim.getTime()));
				i++;
			}
			if (bean_solucao != null) {
				ps.setString(i, bean_solucao);
				i++;
			}
			
			// Insere a manutenção no banco.
			if (ps.executeUpdate() <= 0) {
				// Se não houve rows, é erro. 
				conn.rollback();
				throw new ModelException("A inserção da manutenção não ocorreu");
			}
			
			// Pegando o identity.
			rs = ps.getGeneratedKeys();
			if (!rs.next()) {
				// Se não houve identity gerado, é erro. 
				conn.rollback();				
				throw new ModelException("A inserção da manutenção não ocorreu");
			}
			ret = manutencao;
			ret.setIdManutencao(rs.getInt(1));

			// Fecha o ResultSet (pois vamos criar outro).
			rs.close();
			
			sbSQL.setLength(0);
			sbSQL.append("INSERT INTO manutencao_atividade (");
			sbSQL.append("  id_manutencao,");
			sbSQL.append("  pista,");
			sbSQL.append("  id_manutencao_descricao");
			sbSQL.append("	) VALUES (?,?,?)");			
			ps = conn .prepareStatement(sbSQL.toString(), Statement.RETURN_GENERATED_KEYS);
			
			//Inserindo as respectivas atividades.
			for (ManutencaoAtividade atv : manutencao.getAtividades()) {
				atv.setIdManutencao(ret.getIdManutencao());
				
				// Ajustando os parâmetros e inserindo cada uma das atividades.
				ps.setInt(1, ret.getIdManutencao());
				ps.setByte(2, atv.getPista());
				ps.setInt(3, atv.getIdManutencaoDescricao());
				
				// Insere a atividade no banco.
				if (ps.executeUpdate() <= 0) {
					// Se não houve rows, é erro. 
					conn.rollback();
					throw new ModelException("A inserção da manutenção não ocorreu");
				}				
				
				// Pegando o identity da atividade.
				rs = ps.getGeneratedKeys();
				if (!rs.next()) {
					// Se não houve identity gerado, é erro. 
					conn.rollback();
					throw new ModelException("A inserção das atividades não ocorreu");
				}
				atv.setIdAtividade(rs.getInt(1));
				rs.close();
				ps.clearParameters();
			}
			conn.commit();
			
		} catch (SQLException ex) {
				conn.rollback();
				throw ex;
		} catch (ConexaoException ex) {
			conn.rollback();
			throw ex;
		}
		catch (Exception ex) {
			conn.rollback();
			throw new ModelException("Erro não esperado.", ex);
		}
		finally {
			if (rs != null)
				rs.close();
			if (ps != null)
				ps.close();
			if (conn != null)
				conn.close();
		}
		return ret;
	}

	
	/**
	 * Método que gera a senha (compatível com a rotina escrita em Delphi do GC)
	 * @param idLocal o Id do local que será executada a manutenção.
	 * @param usuario o nome do usuário que está criando a manutenção.
	 * @param data a data que ocorrerá a manutenção.
	 * @param n o número de vezes que já existe a senha. 
	 * @param acessoParam indica se será permitido o acesso aos parâmetros.
	 * @return
	 */
	public static String gerarSenha(int idLocal, String usuario, Date data, int n, boolean acessoParam) {
		UShort D,M,Y, Tot;
		String Chave;
		int X;
		int JK, JS;
		String S, K;
		String Result;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		
		D = new com.consilux.lib.UShort(cal.get(Calendar.DAY_OF_MONTH));
		M = new UShort(1 + cal.get(Calendar.MONTH));
		Y = new UShort(cal.get(Calendar.YEAR));
		
		Tot = new UShort(idLocal).Add(Y).Mul(M).Mul(D).Mul(new UShort(n));
		
		if (acessoParam)
		  	Tot.Mul(new UShort(3));
		
		S = usuario;
		if (usuario.length() < 5) {
			for (int i = 1; i <= 10 - usuario.length(); i++) {
				S += Integer.toString(i);
			}
		}

		while (true) {
			Chave = S;
			if (Chave.length() % 2 > 0)
			   	Chave += Chave.charAt(1);
			
			S = "";
			X = Chave.length() / 2;
			for (int i = 1; i <= X; i++) {
			   S += Integer.toHexString(Chave.charAt(0) ^ Chave.charAt(Chave.length() - 1)).toUpperCase();
			   Chave = Chave.substring(1, Chave.length() - 1);
			}
			if (S.length() <= 5)
				break;
		}
		
		K = Tot.toString();
		Result = "";
		for (int i = 0; i < S.length(); i++) {
			JK = Character.getNumericValue(K.charAt((i + 1) % K.length()));
			JS = Character.getNumericValue(S.charAt(i));
			Result += Integer.toHexString(JK ^ JS).toUpperCase();
		}
		
	  	for (int i = 0; i < 5 - Result.length(); i++) {
	  		Result = Result + K.charAt(i);
	  	}

		return Result;
	}	

}