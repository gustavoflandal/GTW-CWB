package com.consilux.model;

import java.io.File;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.lib.Conexao;

//import eu.medsea.util.MimeUtil;

public class ArquivoVideoBean {
	
	private static Logger logger = LogManager.getLogger(ArquivoVideoBean.class);

	private Integer idArquivo;
	private Integer idLocal;
	private Date dataEntrada;
	private String nomeArquivo;
	private String tipo;
	private byte[] dados;
	private String caminhoArquivo;
	private Integer idUsuario;
	private Integer idUsuarioValida;
	private Date dataValida;
	private InputStream stream;
	
	private Date dataVideo;
	
	public Integer getIdArquivo() {
		return idArquivo;
	}
	public void setIdArquivo(Integer idArquivo) {
		this.idArquivo = idArquivo;
	}
	public Integer getIdLocal() {
		return idLocal;
	}
	public void setIdLocal(Integer idLocal) {
		this.idLocal = idLocal;
	}
	public Date getDataEntrada() {
		return dataEntrada;
	}
	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}
	public String getNomeArquivo() {
		return nomeArquivo;
	}
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public byte[] getDados() {
		return dados;
	}
	public void setDados(byte[] dados) {
		this.dados = dados;
	}
	public Date getDataVideo() {
		return dataVideo;
	}
	public void setDataVideo(Date dataVideo) {
		this.dataVideo = dataVideo;
	}
	
	public static void Salvar(ArquivoVideoBean bean) {
		
		Connection conn = null;
		CallableStatement cs = null;
		
		try 
		{ 
			String caminho_arquivo = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor().get("diretorio_videos");
			if (caminho_arquivo != null)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				
				caminho_arquivo += "/" + bean.idLocal + "/" + sdf.format(bean.dataVideo);
				
				File fdir = new File(caminho_arquivo);
				if (!fdir.exists())
					fdir.mkdir();
				
				caminho_arquivo += "/" + bean.nomeArquivo;
				
				FileUtils.copyInputStreamToFile(bean.getStream(), new File(caminho_arquivo));
			}
			if (bean.tipo == null)
            {
            	File f = new File(caminho_arquivo);
            	bean.tipo = "xxx";//MimeUtil.getMimeType(f);
            }
			
			conn = Conexao.getConexao();
			cs = conn.prepareCall("INSERT INTO videos_fis (id_local,	data_entrada,	data_video,	nome_arquivo,	tipo,	caminho_arquivo,	id_usuario_upload) VALUES (?,?,?,?,?,?,?)");
			cs.setInt(1, bean.idLocal);
			cs.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			cs.setTimestamp(3, new Timestamp(bean.dataVideo.getTime()));
			cs.setString(4, bean.nomeArquivo);
			cs.setString(5, bean.tipo);
			cs.setString(6, caminho_arquivo);
			cs.setInt(7, bean.idUsuario);
			
			cs.execute();
		} 
		catch(Exception e)
		{
			logger.error("ao salvar arquivo", e);
		}
		finally {
			try {
				if (cs != null)
					cs.close();
				if (conn != null)
					conn.close();
			}
			catch (Exception e) {}
		}
		
	}
	
	public static void AtualizaDataValida(ArquivoVideoBean avb) {
		
		Connection conn = null;
		CallableStatement cs = null;
		
		try 
		{
			conn = Conexao.getConexao();
			cs = conn.prepareCall("UPDATE videos_fis SET id_usuario_valida = ?, data_valida = ? WHERE id_arquivo = ?");
			cs.setInt(1, avb.getIdUsuarioValida());
			cs.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			cs.setInt(3, avb.getIdArquivo());
			cs.execute();
		}
		catch(Exception e)
		{
			logger.error("Erro ao atualizar video", e);
		}
		finally {
			try {
				if (cs != null)
					cs.close();
				if (conn != null)
					conn.close();
			} catch (Exception e2) {
			}
		}
	}

	 
	public static List<ArquivoVideoBean> ObterVideosLocal(Integer id_local) {
		List<ArquivoVideoBean> avbl = new ArrayList<ArquivoVideoBean>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT * FROM videos_fis (NOLOCK) WHERE id_local = ? ORDER BY data_video ");
			ps.setInt(1, id_local);
			
			rs = ps.executeQuery();
			while (rs.next())
			{
				ArquivoVideoBean avb = new ArquivoVideoBean();
				avb.setIdArquivo(rs.getInt(1));
				avb.setIdLocal(rs.getInt(2));
				avb.setDataEntrada(rs.getTimestamp(3));
				avb.setDataVideo(rs.getTimestamp(4));
				avb.setNomeArquivo(rs.getString(5));
				avb.setTipo(rs.getString(6));
				avb.setCaminhoArquivo(rs.getString(7));
				avb.setIdUsuario(rs.getInt(8));
				avb.setIdUsuarioValida(rs.getInt(9));
				avb.setDataValida(rs.getTimestamp(10));
				//avb.setDados(FileUtils.readFileToByteArray(new File(avb.getCaminhoArquivo())));
				
				avbl.add(avb);
			}
		}
		catch(Exception e) 
		{
			logger.error("ao obter videos", e);
		}
		finally 
		{
			try 
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {}
		}
		
		return avbl;
	}
	
	public static ArquivoVideoBean ObterVideo(Integer id_video) {
		ArquivoVideoBean avb = null;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT * FROM videos_fis (NOLOCK) WHERE id_arquivo = ? ");
			ps.setInt(1, id_video);
			
			rs = ps.executeQuery();
			while (rs.next())
			{
				avb = new ArquivoVideoBean();
				avb.setIdArquivo(rs.getInt(1));
				avb.setIdLocal(rs.getInt(2));
				avb.setDataEntrada(rs.getTimestamp(3));
				avb.setDataVideo(rs.getTimestamp(4));
				avb.setNomeArquivo(rs.getString(5));
				avb.setTipo(rs.getString(6));
				avb.setCaminhoArquivo(rs.getString(7));
				avb.setIdUsuario(rs.getInt(8));
				avb.setIdUsuarioValida(rs.getInt(9));
				avb.setDataValida(rs.getTimestamp(10));
				//avb.setDados(FileUtils.readFileToByteArray(new File(avb.getCaminhoArquivo())));
			}
		}
		catch(Exception e) 
		{
			logger.error("ao obter videos", e);
		}
		finally 
		{
			try 
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {}
		}
		
		return avb;
	}
	
	public static List<Integer> ObterVideos(Integer id_local, Date data_ini, Date data_fim) {
		
		List<Integer> videos = new ArrayList<Integer>();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT id_arquivo FROM videos_fis (NOLOCK) WHERE id_local = ? AND data_video BETWEEN ? AND ?");
			ps.setInt(1, id_local);
			ps.setTimestamp(2, new Timestamp(data_ini.getTime()));
			ps.setTimestamp(3, new Timestamp(data_fim.getTime()));
			
			rs = ps.executeQuery();
			while (rs.next())
			{
				videos.add(rs.getInt(1));
			}
			
			logger.debug("Obteve " + videos.size() + " videos");
		}
		catch(Exception e) 
		{
			logger.error("ao obter videos", e);
		}
		finally 
		{
			try 
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {}
		}
		
		return videos;
	}
	
	public static SimpleEntry<Date, Date> ObterDataVideos() {
		
		SimpleEntry<Date, Date> ret = null;
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try 
		{
			conn = Conexao.getConexao();
			ps = conn.prepareStatement("SELECT MIN(vf.data_video) min_data, MAX(vf.data_video) max_data FROM videos_fis vf (NOLOCK) ");
			
			rs = ps.executeQuery();
			if (rs.next())
			{
				ret = new SimpleEntry<Date, Date>(rs.getTimestamp(1), rs.getTimestamp(2));
			}
		}
		catch(Exception e) 
		{
			logger.error("ao obter videos", e);
		}
		finally 
		{
			try 
			{
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			}
			catch(Exception e) {}
		}
		
		return ret;
	}
	
	public String getCaminhoArquivo() {
		return caminhoArquivo;
	}
	public void setCaminhoArquivo(String caminhoArquivo) {
		this.caminhoArquivo = caminhoArquivo;
	}
	public Integer getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
	public Integer getIdUsuarioValida() {
		return idUsuarioValida;
	}
	public void setIdUsuarioValida(Integer idUsuarioValida) {
		this.idUsuarioValida = idUsuarioValida;
	}
	public Date getDataValida() {
		return dataValida;
	}
	public void setDataValida(Date dataValida) {
		this.dataValida = dataValida;
	}
	public InputStream getStream() {
		return stream;
	}
	public void setStream(InputStream stream) {
		this.stream = stream;
	}
}
