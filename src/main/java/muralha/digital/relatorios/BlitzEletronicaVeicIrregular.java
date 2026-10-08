package muralha.digital.relatorios;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.lib.Conexao;
import com.consilux.lib.RelatorioVisual;
import com.consilux.model.Acesso;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;

import muralha.digital.util.RespostaRequisicaoXML;
import muralha.digital.veiculo.Veiculo;
import muralha.digital.veiculo.Veiculos;
import muralha.digital.veiculo.imagem.VeiculoImagem;
import muralha.digital.veiculo.imagem.VeiculoImagens;

@WebServlet("/MuralhaDigital/BlitzEletronica/Imprimir")
public class BlitzEletronicaVeicIrregular extends HttpServlet implements javax.servlet.Servlet {
	
	private static final long serialVersionUID = 1L;
	private static RespostaRequisicaoXML respostaXML = new RespostaRequisicaoXML();
	public static final String DIR_RELATORIOS = "/muralha-digital/relatorios/";
	public static final String DIR_IMAGENS = "/WEB-INF/img/";
	private static Logger logger = Logger.getLogger(BlitzEletronicaVeicIrregular.class);  
	
	public BlitzEletronicaVeicIrregular() {
		super();
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (!new Acesso(request, response, true).verificaAcesso(false))
		{
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return;
		}

		String sIdVeiculo = request.getParameter("idVeiculo");

		if (sIdVeiculo == null || sIdVeiculo.length() == 0)
		{
    		String msg = "Veículo não informada!";
    		logger.error(msg);	
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}
		
		respostaXML.EnviarRespostaRequisicaoXML(response, true, "Filtros validados com sucesso!");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		if (!new Acesso(request, response, true).verificaAcesso(false))
		{
			new Mensagem(response).showErro("Usuário não atenticado!", "javascript:window.close();");
			return;
		}

		String sIdVeiculo = request.getParameter("idVeiculo");

		if (sIdVeiculo == null || sIdVeiculo.length() == 0)
		{
    		String msg = "Veículo não informada!";
    		logger.error(msg);	
    		respostaXML.EnviarRespostaRequisicaoXML(response, false, msg);
    		return;
		}

		String sArquivoJasper;
		UUID id;
		Veiculo veiculo = new Veiculo();
	    List<ByteArrayInputStream> lbimgs = new ArrayList<ByteArrayInputStream>();
		
		try
		{
			id = UUID.fromString(sIdVeiculo);
			veiculo = Veiculos.ObterVeiculoPorId(id);
			VeiculoImagem img = VeiculoImagens.ObterImagemPorIdVeicTpImagem(id,0);
			//Converter imagem para jpg antes de adicionar a lista - webp não funciona no jasper report
			InputStream is = new ByteArrayInputStream(img.getImagem());
			BufferedImage bi = ImageIO.read(is);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi, "jpg", baos);
			byte[] imagem = baos.toByteArray();
			lbimgs.add(new ByteArrayInputStream(imagem));
		}
		catch (Exception e)
		{
    		String msg = "Erro ao imprimir veículo irregular!";
    		logger.error(msg, e);	
    		new Mensagem(response).showErro(msg, "javascript:window.close();");
			return;
		}
		
		sArquivoJasper = DIR_RELATORIOS + "BlitzEletronicaVeicIrregularV3.jasper";
		RelatorioVisual relatorio = new RelatorioVisual(getServletContext().getRealPath(sArquivoJasper));
		
		relatorio.adicParametro("IMGS_VEIC", lbimgs);
		relatorio.adicParametro("DIR_IMAGENS", getServletContext().getRealPath(DIR_IMAGENS) + System.getProperty("file.separator"));
		relatorio.adicParametro("ID_VEICULO", id.toString());
		
		Connection conn = null;
		try
		{
			conn = Conexao.getConexao();
			Statement st = conn.createStatement();
			try
			{
				relatorio.preencheRelatorio(conn);
				st = conn.createStatement();
			}
			finally
			{
				st.close();
			}
			
			String nomeArquivo = (veiculo != null && veiculo.getPlaca() != null && veiculo.getPlaca().trim() != "" ? String.format("veiculo-irregular-%s.pdf", veiculo.getPlaca().trim()) : "veiculo-irregular.pdf");
			response.setContentType(TipoMime.PDF.getTipo());
			response.setHeader("Content-Disposition","inline; filename=\""+nomeArquivo+"\"");
		    relatorio.exportReportToPdfStream(response.getOutputStream());
		} 
		catch (Exception e)
		{
			String msg = "Erro ao imprimir veículo irregular!";
    		logger.error(msg, e);	
    		new Mensagem(response).showErro(msg, "javascript:window.close();");
			return;
		}
		finally
		{
			if (conn != null)
			{
				try
				{
					conn.close();
				}
				catch (SQLException e)
				{
					logger.error("Erro ao fechar a conexão!", e);
				}
			}
		}
	}
}