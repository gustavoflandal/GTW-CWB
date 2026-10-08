package com.consilux.model;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;

public class ManutencaoN {

	private static Logger logger = org.apache.log4j.LogManager.getLogger(ManutencaoN.class);
	
	private static Map<Integer, String> lista_estados = ObterEstados();
	
	public static class ManutencaoCausa {
		
		private int idCausa;
		private String descricao;
		private boolean tecnico, oficio;
		
		public ManutencaoCausa(int idCausa, String descricao, boolean tecnico, boolean oficio) {
			this.idCausa = idCausa;
			this.descricao = descricao;
			this.tecnico = tecnico;
			this.oficio = oficio;
		}
		
		public boolean isTecnico() {
			return tecnico;
		}
		public void setTecnico(boolean tecnico) {
			this.tecnico = tecnico;
		}
		public boolean isOficio() {
			return oficio;
		}
		public void setOficio(boolean oficio) {
			this.oficio = oficio;
		}
		public String getDescricao() {
//			return "AAAA";
			if (tecnico)
			return "Técnico - " + descricao;
			return "Não Técnico - " + descricao;
		}
		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}
		public int getIdCausa() {
			return idCausa;
		}
		public void setIdCausa(int idCausa) {
			this.idCausa = idCausa;
		}
		
	}
	
	public static class ManutencaoOcorrencia {
		private Integer idOcorrencia,	idLocal,	serieEquipamento, cod_pista, numeroOficio, anoOficio;
		private Date dataHora, dataCadastro;
		private String motivoResumido, 	nomeArquivo,	caminhoArquivo,	usuarioCadastro,		tipoMime,	extensaoArquivo,	estado;
		
		public ManutencaoOcorrencia(Integer idOcorrencia,Integer 	idLocal,Integer 	serieEquipamento,Integer  cod_pista,Integer  numeroOficio,Integer  anoOficio,
		Date dataHora, Date  dataCadastro, String motivoResumido,String  	nomeArquivo,String 	caminhoArquivo,String 	usuarioCadastro,String 		tipoMime,String 	extensaoArquivo,String 	estado) 
		{
			this.idOcorrencia = idOcorrencia;	
			this.idLocal = idLocal;
			this.serieEquipamento = serieEquipamento; 
			this.cod_pista = cod_pista; 
			this.numeroOficio = numeroOficio; 
			this.anoOficio = anoOficio;
			this.dataHora = dataHora; 
			this.dataCadastro = dataCadastro;
			this.motivoResumido = motivoResumido; 	
			this.nomeArquivo = nomeArquivo;	
			this.caminhoArquivo = caminhoArquivo;	
			this.usuarioCadastro = usuarioCadastro;		
			this.tipoMime = tipoMime;	
			this.extensaoArquivo = extensaoArquivo;	
			this.estado =estado;
		}
		
		public Integer getCod_pista() {
			return cod_pista;
		}
		public void setCod_pista(Integer cod_pista) {
			this.cod_pista = cod_pista;
		}
		public Integer getSerieEquipamento() {
			return serieEquipamento;
		}
		public void setSerieEquipamento(Integer serieEquipamento) {
			this.serieEquipamento = serieEquipamento;
		}
		public Integer getIdLocal() {
			return idLocal;
		}
		public void setIdLocal(Integer idLocal) {
			this.idLocal = idLocal;
		}
		public String getCaminhoArquivo() {
			return caminhoArquivo;
		}
		public void setCaminhoArquivo(String caminhoArquivo) {
			this.caminhoArquivo = caminhoArquivo;
		}
		public Integer getIdOcorrencia() {
			return idOcorrencia;
		}
		public void setIdOcorrencia(Integer idOcorrencia) {
			this.idOcorrencia = idOcorrencia;
		}
		public String getEstado() {
			return estado;
		}
		public void setEstado(String estado) {
			this.estado = estado;
		}
		public Integer getAnoOficio() {
			return anoOficio;
		}
		public void setAnoOficio(Integer anoOficio) {
			this.anoOficio = anoOficio;
		}
		public Integer getNumeroOficio() {
			return numeroOficio;
		}
		public void setNumeroOficio(Integer numeroOficio) {
			this.numeroOficio = numeroOficio;
		}
		public Date getDataHora() {
			return dataHora;
		}
		public void setDataHora(Date dataHora) {
			this.dataHora = dataHora;
		}
		public Date getDataCadastro() {
			return dataCadastro;
		}
		public void setDataCadastro(Date dataCadastro) {
			this.dataCadastro = dataCadastro;
		}
		public String getMotivoResumido() {
			return motivoResumido;
		}
		public void setMotivoResumido(String motivoResumido) {
			this.motivoResumido = motivoResumido;
		}
		public String getNomeArquivo() {
			return nomeArquivo;
		}
		public void setNomeArquivo(String nomeArquivo) {
			this.nomeArquivo = nomeArquivo;
		}
		public String getUsuarioCadastro() {
			return usuarioCadastro;
		}
		public void setUsuarioCadastro(String usuarioCadastro) {
			this.usuarioCadastro = usuarioCadastro;
		}
		public String getTipoMime() {
			return tipoMime;
		}
		public void setTipoMime(String tipoMime) {
			this.tipoMime = tipoMime;
		}
		public String getExtensaoArquivo() {
			return extensaoArquivo;
		}
		public void setExtensaoArquivo(String extensaoArquivo) {
			this.extensaoArquivo = extensaoArquivo;
		}
		
		public String getDescricaoExt()
		{
			return "" + (this.numeroOficio    != null ? this.numeroOficio   : "N/D") + "-" +
						(this.anoOficio       != null ? this.anoOficio      : "N/D") + "-" +
						(this.idLocal         != null ? this.idLocal        : "N/D") + "-" + 
						(this.motivoResumido  != null ? this.motivoResumido : "N/D") + "-" + 
						(this.estado          != null ? this.estado         : "N/D") + "-" +
						(new SimpleDateFormat("dd/MM/yyyy").format(dataHora)) + "-" +
						(new SimpleDateFormat("HH:mm").format(dataHora)) + "-" +
						(this.serieEquipamento != null ? this.serieEquipamento : "N/D");
		}
	} 

	public static class Manutencao {
		private Integer idManutencao; 
		private Integer idStatus;
		private String descricao;
		private Integer idLocal;
		private Integer serieEquipamento;
		private Integer idPista;
		private String tipoGrupoAutuador;
		private Date dataOcorrencia;
		private Date dataCadastro;
		private Date dataInicio;
		private Date dataPrevisto;
		private Integer idOcorrencia;
		private Integer numeroOficio;
		private Integer anoOficio;
		private Integer idTecnico;
		private Integer idAuxiliar;
		private Date dataConclusao;
		private Date dataUltimaAlteracao;
		private Integer idUltimoUsuario;
		private boolean encaminhar;
		
		public Manutencao(Integer idManutencao, Integer idStatus, String descricao, Integer idLocal, Integer serieEquipamento, Integer idPista, 
				String tipoGrupoAutuador, Date dataOcorrencia, Date dataCadastro, Date dataInicio, Date dataPrevisto, 
				Integer idOcorrencia, Integer numeroOficio, Integer anoOficio, Integer idTecnico, Integer idAuxiliar, 
				Date dataConclusao, Date dataUltimaAlteracao, Integer idUltimoUsuario, boolean encaminhar)
		{
			this.idManutencao = idManutencao; 
			this.idStatus = idStatus;
			this.descricao = descricao;
			this.idLocal = idLocal;
			this.serieEquipamento = serieEquipamento;
			this.idPista = idPista;
			this.tipoGrupoAutuador = tipoGrupoAutuador;
			this.dataOcorrencia = dataOcorrencia;
			this.dataCadastro = dataCadastro;
			this.dataInicio = dataInicio;
			this.dataPrevisto = dataPrevisto;
			this.idOcorrencia = idOcorrencia;
			this.numeroOficio = numeroOficio;
			this.anoOficio = anoOficio;
			this.idTecnico = idTecnico;
			this.idAuxiliar = idAuxiliar;
			this.dataConclusao = dataConclusao;
			this.dataUltimaAlteracao = dataUltimaAlteracao;
			this.idUltimoUsuario = idUltimoUsuario;
			this.setEncaminhar(encaminhar);
		}
		
		public Integer getIdManutencao() {
			return idManutencao;
		}
		public void setIdManutencao(Integer idManutencao) {
			this.idManutencao = idManutencao;
		}
		public Integer getIdStatus() {
			return idStatus;
		}
		public void setIdStatus(Integer idStatus) {
			this.idStatus = idStatus;
		}
		public String getDescricao() {
			return descricao;
		}
		public void setDescricao(String descricao) {
			this.descricao = descricao;
		}
		public Integer getIdLocal() {
			return idLocal;
		}
		public void setIdLocal(Integer idLocal) {
			this.idLocal = idLocal;
		}
		public Integer getSerieEquipamento() {
			return serieEquipamento;
		}
		public void setSerieEquipamento(Integer serieEquipamento) {
			this.serieEquipamento = serieEquipamento;
		}
		public Integer getIdPista() {
			return idPista;
		}
		public void setIdPista(Integer idPista) {
			this.idPista = idPista;
		}
		public String getTipoGrupoAutuador() {
			return tipoGrupoAutuador;
		}
		public void setTipoGrupoAutuador(String tipoGrupoAutuador) {
			this.tipoGrupoAutuador = tipoGrupoAutuador;
		}
		public Date getDataOcorrencia() {
			return dataOcorrencia;
		}
		public void setDataOcorrencia(Date dataOcorrencia) {
			this.dataOcorrencia = dataOcorrencia;
		}
		public Date getDataCadastro() {
			return dataCadastro;
		}
		public void setDataCadastro(Date dataCadastro) {
			this.dataCadastro = dataCadastro;
		}
		public Date getDataInicio() {
			return dataInicio;
		}
		public void setDataInicio(Date dataInicio) {
			this.dataInicio = dataInicio;
		}
		public Date getDataPrevisto() {
			return dataPrevisto;
		}
		public void setDataPrevisto(Date dataPrevisto) {
			this.dataPrevisto = dataPrevisto;
		}
		public Integer getIdOcorrencia() {
			return idOcorrencia;
		}
		public void setIdOcorrencia(Integer idOcorrencia) {
			this.idOcorrencia = idOcorrencia;
		}
		public Integer getNumeroOficio() {
			return numeroOficio;
		}
		public void setNumeroOficio(Integer numeroOficio) {
			this.numeroOficio = numeroOficio;
		}
		public Integer getAnoOficio() {
			return anoOficio;
		}
		public void setAnoOficio(Integer anoOficio) {
			this.anoOficio = anoOficio;
		}
		public Integer getIdTecnico() {
			return idTecnico;
		}
		public void setIdTecnico(Integer idTecnico) {
			this.idTecnico = idTecnico;
		}
		public Integer getIdAuxiliar() {
			return idAuxiliar;
		}
		public void setIdAuxiliar(Integer idAuxiliar) {
			this.idAuxiliar = idAuxiliar;
		}
		public Date getDataConclusao() {
			return dataConclusao;
		}
		public void setDataConclusao(Date dataConclusao) {
			this.dataConclusao = dataConclusao;
		}
		public Date getDataUltimaAlteracao() {
			return dataUltimaAlteracao;
		}
		public void setDataUltimaAlteracao(Date dataUltimaAlteracao) {
			this.dataUltimaAlteracao = dataUltimaAlteracao;
		}
		public Integer getIdUltimoUsuario() {
			return idUltimoUsuario;
		}
		public void setIdUltimoUsuario(Integer idUltimoUsuario) {
			this.idUltimoUsuario = idUltimoUsuario;
		}

		public boolean isEncaminhar() {
			return encaminhar;
		}

		public void setEncaminhar(boolean encaminhar) {
			this.encaminhar = encaminhar;
		}
		
		public String getDescricaoExt()
		{
			return "" + (this.idManutencao    != null ? this.idManutencao   : "N/D") + "-" +
						(this.idLocal         != null ? this.idLocal        : "N/D") + "-" + 
						(this.descricao  	  != null ? this.descricao 		: "N/D") + "-" + 
						(this.idStatus        != null && lista_estados.get(this.idStatus) != null ? lista_estados.get(this.idStatus)         : "N/D") + "-" +
						(new SimpleDateFormat("dd/MM/yyyy").format(dataOcorrencia)) + "-" +
						(new SimpleDateFormat("HH:mm").format(dataOcorrencia));
		}
	}
	
	public static class ManutencaoComentario {
		private Integer idComentario, idManutencao, idUsuario;
		private Date data;
		private String comentario, nomeUsuario;
		
		public ManutencaoComentario(Integer idComentario, Integer idManutencao, Integer idUsuario, Date data, String comentario) {
			this.idComentario = idComentario;
			this.idManutencao = idManutencao;
			this.idUsuario = idUsuario;
			this.data = data;
			this.comentario = comentario;
		}
		
		public ManutencaoComentario(Integer idComentario, Integer idManutencao, String nomeUsuario, Date data, String comentario) {
			this.idComentario = idComentario;
			this.idManutencao = idManutencao;
			this.nomeUsuario = nomeUsuario;
			this.data = data;
			this.comentario = comentario;
		}
		
		public Integer getId_manutencao() {
			return idManutencao;
		}
		public void setId_manutencao(Integer id_manutencao) {
			this.idManutencao = id_manutencao;
		}
		public Integer getIdUsuario() {
			return idUsuario;
		}
		public void setIdUsuario(Integer idUsuario) {
			this.idUsuario = idUsuario;
		}
		public Date getData() {
			return data;
		}
		public void setData(Date data) {
			this.data = data;
		}
		public String getComentario() {
			return comentario;
		}
		public void setComentario(String comentario) {
			this.comentario = comentario;
		}
		public Integer getIdComentario() {
			return idComentario;
		}
		public void setIdComentario(Integer idComentario) {
			this.idComentario = idComentario;
		}

		public String getNomeUsuario() {
			return nomeUsuario;
		}

		public void setNomeUsuario(String nomeUsuario) {
			this.nomeUsuario = nomeUsuario;
		}
		
		public String getComentarioExt() {
			return comentario.replace("\r\n", "<BR/>").replace("\n", "<BR/>");
		}
		
//		id_manutencao INT NOT NULL,
//		id_usuario INT NOT NULL,
//		data DATETIME NOT NULL DEFAULT(GETDATE()),
//		comentario VARCHAR(300) NULL
	}
	
	public static Map<Integer, String> ObterEstados()
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map<Integer, String> map = new HashMap<Integer,String>();
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT * FROM manutencao_status (NOLOCK)");
			rs = ps.executeQuery();
			while(rs.next())
				map.put(rs.getInt(1), rs.getString(2));
		}
		catch(Exception e)
		{
		
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e){}
		}
		
		return map;
	}
	
	public static Map<Integer, String> ObterEstaticos()
	{ 
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map<Integer, String> map = new HashMap<Integer,String>();
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT serie_equipamento FROM equipamento_estatico (NOLOCK)");
			rs = ps.executeQuery();
			while(rs.next())
				map.put(rs.getInt(1), rs.getString(1));
		}
		catch(Exception e)
		{
		
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e){}
		}
		
		return map;
	}
	
	public static Map<String, String> ObterGrupoAutuador()
	{ 
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map<String, String> map = new HashMap<String,String>();
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT tipo_apait, descricao_apait, id_enquadramento FROM v_enquadramentos_manutencao");
			rs = ps.executeQuery();
			while(rs.next())
				map.put(rs.getString(1), rs.getString(2));
		}
		catch(Exception e)
		{
		
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e){}
		}
		
		return map;
	}
	
	public static List<ManutencaoCausa> ObterCausas()
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<ManutencaoCausa> map = new ArrayList<ManutencaoCausa>();
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT * FROM manutencao_causa_desc (NOLOCK)");
			rs = ps.executeQuery();
			while(rs.next())
				map.add(new ManutencaoCausa(rs.getInt(1), rs.getString(2), rs.getBoolean(3), rs.getBoolean(4)));
		}
		catch(Exception e)
		{
		
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e){}
		}
		
		return map;
	}

	public static List<Integer> ObterCausas(Integer idManutencao)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<Integer> map = new ArrayList<Integer>();
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT mc.id_causa FROM manutencao_causa mc (NOLOCK) WHERE mc.id_manutencao = ?");
			ps.setInt(1, idManutencao);
			rs = ps.executeQuery();
			while(rs.next())
				map.add(rs.getInt(1));
		}
		catch(Exception e)
		{
		
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e){}
		}
		
		return map;
	}
	
	public static void AtualizarCausas(Integer idManutencao, List<Integer> causas) {
		
		Connection conn = null;
		CallableStatement cs = null;
		
		try
		{
			conn = Conexao.getConexao();
			cs = conn.prepareCall("DELETE manutencao_causa WHERE id_manutencao = ?");
			cs.setInt(1, idManutencao);
			cs.execute();
			
			for (int i : causas) {
				cs = conn.prepareCall("INSERT INTO manutencao_causa (id_manutencao, id_causa) VALUES (?,?)");
				cs.setInt(1, idManutencao);
				cs.setInt(2, i);
				cs.execute();
			}
		}
		catch(Exception e)
		{
			
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (cs != null)
					cs.close();
			}
			catch(Exception e){}
		}
	}
	
	public static List<ManutencaoOcorrencia> ObterOcorrencias()
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<ManutencaoOcorrencia> map = new ArrayList<ManutencaoOcorrencia>();
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT * FROM ocorrencia (NOLOCK) WHERE numero_oficio IS NOT NULL ORDER BY ano_oficio DESC, numero_oficio DESC");
			rs = ps.executeQuery();
			while(rs.next())
				map.add(new ManutencaoOcorrencia(rs.getInt("id_ocorrencia"),rs.getInt("id_local"),rs.getInt("serie_equipamento"),rs.getInt("cod_pista"),rs.getInt("numero_oficio"),
						rs.getInt("ano_oficio"),new Date(rs.getTimestamp("data_hora").getTime()),new Date(rs.getTimestamp("data_cadastro").getTime()),
						rs.getString("motivo_resumido"),rs.getString("nome_arquivo"),rs.getString("caminho_arquivo"),rs.getString("usuario_cadastro"),
						rs.getString("tipo_mime"), rs.getString("extensao_arquivo"), rs.getString("estado")));
		}
		catch(Exception e)
		{
		
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e){}
		}
		
		return map;
	}

	public static Map<Integer, Manutencao> ObterManutencoes() { return ObterManutencoes(null);}
	
	public static Map<Integer, Manutencao> ObterManutencoes(Integer estado)
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		Map<Integer, Manutencao> map = new HashMap<Integer, Manutencao>();
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT id_manutencao	,id_status	,descricao	,id_local	,serie_equipamento	,id_pista	, RTRIM(tipo_grupo_autuador) tipo_grupo_autuador	,data_ocorrencia	,data_cadastro	,data_inicio	,data_previsto	,id_ocorrencia	,numero_oficio	,ano_oficio	,id_tecnico	,id_auxiliar	,data_conclusao	,data_ultima_alteracao	,id_ultimo_usuario	,encaminhar FROM manutencao (NOLOCK) ORDER BY 1 DESC");
			rs = ps.executeQuery();
			while(rs.next()) {
				Manutencao m = new Manutencao(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getString(7), rs.getTimestamp(8), 
						rs.getTimestamp(9) , rs.getDate(10), rs.getDate(11), rs.getInt(12), rs.getInt(13), rs.getInt(14), rs.getInt(15), rs.getInt(16), 
						rs.getDate(17), rs.getDate(18), rs.getInt(19), rs.getBoolean(20));
				map.put(m.getIdManutencao(), m);
			}
		}
		catch(Exception e)
		{
			logger.error("ao carregar manutencoes: ", e);
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e){}
		}
		
		return map;
	}
	
	public static int InserirManutencao(Manutencao man)	{
		Connection conn = null;
		CallableStatement ps = null;
		
		int res = 0;
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareCall("{? = call spu_manutencao_inserir (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			
			ps.registerOutParameter(1, java.sql.Types.INTEGER);
			
			ps.setInt(2, man.getIdStatus());
			ps.setString(3, man.getDescricao());
			ps.setInt(4, man.getIdLocal());
			
			if (man.getSerieEquipamento() == null || man.getSerieEquipamento() == 0)
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, man.getSerieEquipamento());
			
			if (man.getIdPista() == null || man.getIdPista() == 0)
				ps.setNull(6, Types.INTEGER);
			else
				ps.setInt(6, man.getIdPista());
			
			if (man.getTipoGrupoAutuador() == null || man.getTipoGrupoAutuador() == "")
				ps.setNull(7, Types.VARCHAR);
			else
				ps.setString(7, man.getTipoGrupoAutuador());
			
			if (man.getDataOcorrencia() == null)
				ps.setNull(8, Types.TIMESTAMP);
			else
				ps.setTimestamp(8, new Timestamp(man.getDataOcorrencia().getTime()));
				
			if (man.getDataCadastro() == null)
				ps.setNull(9, Types.TIMESTAMP);
			else
				ps.setTimestamp(9, new Timestamp(man.getDataCadastro().getTime()));
			
			if (man.getDataInicio() == null)
				ps.setNull(10, Types.TIMESTAMP);
			else
				ps.setTimestamp(10, new Timestamp(man.getDataInicio().getTime()));
			
			if (man.getDataPrevisto() == null)
				ps.setNull(11, Types.TIMESTAMP);
			else
				ps.setTimestamp(11, new Timestamp(man.getDataPrevisto().getTime()));
			
			if (man.getIdOcorrencia() == null || man.getIdOcorrencia() == 0)
				ps.setNull(12, Types.INTEGER);
			else
				ps.setInt(12, man.getIdOcorrencia());
			
			if (man.getNumeroOficio() == null || man.getNumeroOficio() == 0)
				ps.setNull(13, Types.INTEGER);
			else
				ps.setInt(13, man.getNumeroOficio());
				
			if (man.getAnoOficio() == null || man.getAnoOficio() == 0)
				ps.setNull(14, Types.INTEGER);
			else
				ps.setInt(14, man.getAnoOficio());
			
			if (man.getIdTecnico() == null || man.getIdTecnico() == 0)
				ps.setNull(15, Types.INTEGER);
			else
				ps.setInt(15, man.getIdTecnico());
			
			if (man.getIdAuxiliar() == null || man.getIdAuxiliar() == 0)
				ps.setNull(16, Types.INTEGER);
			else
				ps.setInt(16, man.getIdAuxiliar());
			
			if (man.getDataConclusao() == null)
				ps.setNull(17, Types.TIMESTAMP);
			else
				ps.setTimestamp(17, new Timestamp(man.getDataConclusao().getTime()));
			
			if (man.getDataUltimaAlteracao() == null)
				ps.setNull(18, Types.TIMESTAMP);
			else
				ps.setTimestamp(18, new Timestamp(man.getDataUltimaAlteracao().getTime()));
			
			if (man.getIdUltimoUsuario() == null || man.getIdUltimoUsuario() == 0)
				ps.setNull(19, Types.INTEGER);
			else
				ps.setInt(19, man.getIdUltimoUsuario());
			
			ps.setBoolean(20, man.isEncaminhar());
			
			ps.execute();

			res = ps.getInt(1);
		}
		catch(Exception e)
		{
			logger.error("Erro ao cadastrar", e);
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e){}
		}
		return res;
	}
	
	public static int AtualizarManutencao(Manutencao man) 	{
		Connection conn = null;
		CallableStatement ps = null;
		
		int res = 0;
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareCall("{call spu_manutencao_alterar (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
			
			ps.setInt(1, man.getIdManutencao());
			
			ps.setInt(2, man.getIdStatus());
			ps.setString(3, man.getDescricao());
			ps.setInt(4, man.getIdLocal());
			
			if (man.getSerieEquipamento() == null || man.getSerieEquipamento() == 0)
				ps.setNull(5, Types.INTEGER);
			else
				ps.setInt(5, man.getSerieEquipamento());
			
			if (man.getIdPista() == null || man.getIdPista() == 0)
				ps.setNull(6, Types.INTEGER);
			else
				ps.setInt(6, man.getIdPista());
			
			if (man.getTipoGrupoAutuador() == null || man.getTipoGrupoAutuador() == "")
				ps.setNull(7, Types.VARCHAR);
			else
				ps.setString(7, man.getTipoGrupoAutuador());
			
			if (man.getDataOcorrencia() == null)
				ps.setNull(8, Types.TIMESTAMP);
			else
				ps.setTimestamp(8, new Timestamp(man.getDataOcorrencia().getTime()));
				
			if (man.getDataCadastro() == null)
				ps.setNull(9, Types.TIMESTAMP);
			else
				ps.setTimestamp(9, new Timestamp(man.getDataCadastro().getTime()));
			
			if (man.getDataInicio() == null)
				ps.setNull(10, Types.TIMESTAMP);
			else
				ps.setTimestamp(10, new Timestamp(man.getDataInicio().getTime()));
			
			if (man.getDataPrevisto() == null)
				ps.setNull(11, Types.TIMESTAMP);
			else
				ps.setTimestamp(11, new Timestamp(man.getDataPrevisto().getTime()));
			
			if (man.getIdOcorrencia() == null || man.getIdOcorrencia() == 0)
				ps.setNull(12, Types.INTEGER);
			else
				ps.setInt(12, man.getIdOcorrencia());
			
			if (man.getNumeroOficio() == null || man.getNumeroOficio() == 0)
				ps.setNull(13, Types.INTEGER);
			else
				ps.setInt(13, man.getNumeroOficio());
				
			if (man.getAnoOficio() == null || man.getAnoOficio() == 0)
				ps.setNull(14, Types.INTEGER);
			else
				ps.setInt(14, man.getAnoOficio());
			
			if (man.getIdTecnico() == null || man.getIdTecnico() == 0)
				ps.setNull(15, Types.INTEGER);
			else
				ps.setInt(15, man.getIdTecnico());
			
			if (man.getIdAuxiliar() == null || man.getIdAuxiliar() == 0)
				ps.setNull(16, Types.INTEGER);
			else
				ps.setInt(16, man.getIdAuxiliar());
			
			if (man.getDataConclusao() == null)
				ps.setNull(17, Types.TIMESTAMP);
			else
				ps.setTimestamp(17, new Timestamp(man.getDataConclusao().getTime()));
			
			if (man.getDataUltimaAlteracao() == null)
				ps.setNull(18, Types.TIMESTAMP);
			else
				ps.setTimestamp(18, new Timestamp(man.getDataUltimaAlteracao().getTime()));
			
			if (man.getIdUltimoUsuario() == null || man.getIdUltimoUsuario() == 0)
				ps.setNull(19, Types.INTEGER);
			else
				ps.setInt(19, man.getIdUltimoUsuario());
			
			ps.setBoolean(20, man.isEncaminhar());
			
			ps.execute();
		}
		catch(Exception e)
		{
			logger.error("Erro ao cadastrar", e);
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
			}
			catch(Exception e){}
		}
		return res;
	}
	
	public static int InserirComentario(Integer id_manutencao, String comentario, Integer id_usuario) {
		
		int ret = 0;
		
		Connection conn = null;
		CallableStatement cs = null;
		
		try
		{
			conn = Conexao.getConexao();
			cs = conn.prepareCall("{? = call spu_manutencao_comentario_inserir (?,?,?)}");
			cs.registerOutParameter(1, Types.INTEGER);
			cs.setInt(2, id_manutencao);
			cs.setInt(3, id_usuario);
			cs.setString(4, comentario);
			cs.execute();
			ret = cs.getInt(1);
		}
		catch (Exception e)
		{
			logger.error("Ao inserir comentario: ",e);
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (cs != null)
					cs.close();
			}
			catch(Exception e){}
		}
		
		return ret;
	}
	
	public static List<ManutencaoComentario> ObterComentarios(Integer idManutencao) {
//		int ret = 0;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		List<ManutencaoComentario> comentarios = new ArrayList<ManutencaoComentario>();
		
		try
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT mc.*, su.nome FROM manutencao_comentarios mc (NOLOCK) JOIN sis_usuario su (NOLOCK) ON mc.id_usuario = su.id_usuario WHERE id_manutencao = ?");
			ps.setInt(1, idManutencao);
			rs = ps.executeQuery();
			while(rs.next())
				comentarios.add(new ManutencaoComentario(rs.getInt(1), idManutencao, rs.getString(6), rs.getTimestamp(4), rs.getString(5)));
		}
		catch (Exception e)
		{
			logger.error("Ao obter comentarios: ",e);
		}
		finally
		{ 
			try
			{
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			}
			catch(Exception e){}
		}
		
		return comentarios;
	}
	
	public static Manutencao ObterManutencao(Integer idManutencao) {
		return null;
	}
}
