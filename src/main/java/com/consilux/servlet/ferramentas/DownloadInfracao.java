package com.consilux.servlet.ferramentas;

import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.model.InfracaoCompleta;
import com.consilux.model.LocalVigente;
import com.consilux.model.Mensagem;
import com.consilux.model.MensagemJS;
import com.consilux.model.TipoMime;
import com.consilux.model.Veiculo;
import com.consilux.model.VeiculoImagem;
import com.consilux.model.Video;

/**
 * Servlet implementation class DownloadInfracao
 */
public class DownloadInfracao extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private static Logger logger = Logger.getLogger(DownloadInfracao.class);
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadInfracao() {
        super();
    }

    private ZipOutputStream criaArquivoSaidaZip(String sArq, HttpServletResponse response) throws ServletException, IOException {
		ZipOutputStream zip;
		
		response.setContentType(TipoMime.ZIP.getTipo());
		response.setHeader("Content-Disposition","attachment; filename=\"" + sArq + "\"");
		
		zip = new ZipOutputStream(response.getOutputStream());
		
		// Desliga a compressão no ZIP (pois JPG não adianta nada de qualquer maneira).
		zip.setMethod(ZipOutputStream.DEFLATED);
		zip.setLevel(0);		
		
		return zip;
	}
	
	@SuppressWarnings("unused")
	private void adicArquivoNoZip(ZipOutputStream zip, String nome, StringBuilder conteudo)
	throws IOException {
		
       	  zip.putNextEntry(new ZipEntry(nome));
       	  PrintStream ps = new PrintStream(zip); 
       	  ps.print(conteudo.toString());
       	  ps.flush();;
          zip.closeEntry();
          
	}	
	
	private void adicArquivoNoZip(ZipOutputStream zip, String nome, byte[] conteudo)
	throws IOException {

       	  zip.putNextEntry(new ZipEntry(nome));
       	  zip.write(conteudo);
          zip.closeEntry();
	}
	
	private void raiseAndLogError(String message, Throwable rootCause, HttpServletResponse response) throws IOException {
		
		logger.error(message, rootCause);
		new Mensagem(response).showErro(message, null, true);
	}
//	private void adicArquivoNoZip(ZipOutputStream zip, String nome) 
//	throws IOException {
//		
//		 zip.putNextEntry(new ZipEntry(nome + "/"));
//         zip.closeEntry();
//	}
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String s_id_infracao = request.getParameter("id_infracao");
		
		if (s_id_infracao == null || !Pattern.matches("[0-9]{1,8}",s_id_infracao)) {
			new MensagemJS(response).showErro("Identificador da infracao enviado invalido!");
			return;
		}
		
		ZipOutputStream zip = null;
		try 
		{
			Integer id_infracao = Integer.parseInt(s_id_infracao);
			InfracaoCompleta ic = InfracaoCompleta.buscaInfracaoPorId(id_infracao);
			List<VeiculoImagem> vi = VeiculoImagem.buscaVeiculoImagemPorIdInfracaoBD(id_infracao);
			LocalVigente lv = LocalVigente.buscaLocalVigentePorIdLocal(ic.getIdLocal());
			
			String placa = ic.getPlaca();
			if (placa == null)
			{
				Veiculo v = Veiculo.buscaVeiculoPorId( ic.getIdVeiculo() );
				placa = v.getPlaca();
			}
			if (placa == null)
			{
				placa = "ND";
			}
			
			String nomeArquivoZip = id_infracao + "_" + placa + "_" + ic.getIdEnquadramento() + "_" + 
					lv.getSerieEquipamento() + "_" + format.format(ic.getDataVeiculo()) + ".zip";
			zip = criaArquivoSaidaZip(nomeArquivoZip, response);
			
			for (VeiculoImagem veiculoImagem : vi) {
				String nomeArquivo = id_infracao + "_" + veiculoImagem.getIdImagem() + "_" + placa + "_" + ic.getIdEnquadramento() + "_" + 
						lv.getSerieEquipamento() + "_" + format.format(ic.getDataVeiculo()) + "_" + veiculoImagem.getNomeTipoImagem() + 
						".jpg";
				adicArquivoNoZip(zip, nomeArquivo, 	veiculoImagem.getImagem());
			}
			
			Video v1 = Video.buscaVideoPorIdVeiculo(ic.getIdVeiculo(), 1);
			if (v1 != null)
			{
				String nomeArquivo = id_infracao + "_" + placa + "_" + ic.getIdEnquadramento() + "_" + 
						lv.getSerieEquipamento() + "_" + format.format(ic.getDataVeiculo()) + "_video1.avi";
				adicArquivoNoZip(zip, nomeArquivo, 	v1.getVideo());
			}
			
			Video v2 = Video.buscaVideoPorIdVeiculo(ic.getIdVeiculo(), 2);
			if (v2 != null)
			{
				String nomeArquivo = id_infracao + "_" + placa + "_" + ic.getIdEnquadramento() + "_" + 
						lv.getSerieEquipamento() + "_" + format.format(ic.getDataVeiculo()) + "_video2.avi";
				adicArquivoNoZip(zip, nomeArquivo, 	v2.getVideo());
			}
			
		}
		catch(Exception e) {
			logger.error("Erro ao baixar infracao: ", e);
			raiseAndLogError(e.getMessage(), e, response);
		}
		finally 
		{
			if (zip != null) {
				try {
					zip.close();
				}
				catch (Exception e) {}
			}
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}

}
