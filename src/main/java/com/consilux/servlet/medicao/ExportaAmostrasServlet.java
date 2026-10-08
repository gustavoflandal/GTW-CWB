package com.consilux.servlet.medicao;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.consilux.model.Acesso;
import com.consilux.model.ExportaComprovacaoImagemIterator;
import com.consilux.model.ExportaAmostras;
import com.consilux.model.Mensagem;
import com.consilux.model.TipoMime;
import com.consilux.model.beans.ExportaComprovacaoImagemBean;
import com.consilux.model.exception.ModelException;

public class ExportaAmostrasServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constrói o objeto 
	 */
	public ExportaAmostrasServlet() {
		super();
	}   	

	// %[argument_index$][flags][width]conversion

	private static String FORMATO_NOME = "%1$d-%2$04d-%3$04d\\%4$tY%4$tm%4$td%5$s%6$s%7$s%8$s%9$d%10$s";
	// %1 Nº série do equipamento
	// %2 cod_pista (também chamado de COD_LOCAL pela CET)
	// %3 cod_pista_prodam (também chamado de COD_FAIXA pela CET)
	// %4 Data (AnoMesDia)
	// %5 Metrológica / Não metrológica ->   (M/N)
	// %6 Tipo (OBJETIVA/PANORAMICA)   (O/P)
	// %7 Sequencia da imagem (1, 2a, 3a, etc)		OBS: HARDCODED COMO "1", pois por default
	//													sempre vamos buscar a primeira imagem, seja ela OBJ ou PAN
	
	// %8 Qualificador: (TST/INF/OUT)	Teste,Infração,Outros

	// %9 id_imagem (DO GTW)
	// %10 extensão do arquivo (.jpg)

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// Se o usuário não tem acesso...então cai fora!		
		if (!new Acesso(request, response, true).verificaAcesso())
			return; 

		String sDataIni = request.getParameter("dataini");
		String sDataFim = request.getParameter("datafim");

		if (sDataIni == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataIni)) {
			new Mensagem(response).showErro("Data inicial enviada inválida!");
			return;
		}

		if (sDataFim == null || !Pattern.matches("([0-2][0-9]|3[01])/(0[1-9]|1[012])/([12][0-9]{3})",sDataFim)) {
			new Mensagem(response).showErro("Data final enviada inválida!");
			return;
		}

		Date dataInicio;
		Date dataFim;

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		try {
			dataInicio = dateFormat.parse(sDataIni + " 00:00:00");
			dataFim = dateFormat.parse(sDataFim + " 23:59:59");
		} catch (Exception e) {
			throw new ServletException(e);
		}

		ExportaComprovacaoImagemIterator itr = null;
		ZipOutputStream zip = null;
		
		try {
			itr = ExportaAmostras.exportaAmostrasPeriodo(dataInicio, dataFim);
			if (!itr.hasNext())
			{
				new Mensagem(response).showErro("Não existem amostras para este período.");
				return;
			}
			String nomeArquivoZip = "Amostras_" + String.format("%1$td_%1$tm_%1$tY.zip", new Date());
			zip = criaArquivoSaidaZip(nomeArquivoZip, response);
			zip.setMethod(ZipOutputStream.DEFLATED);
			zip.setLevel(0);
			String nomeArquivo;

			ExportaComprovacaoImagemBean img;
			while (itr.hasNext())
			{
				// Passa para o próximo no iterator. 
				img = itr.next();
				
				// A objetiva sempre vai ter. 
				nomeArquivo = String.format(ExportaAmostrasServlet.FORMATO_NOME,
					img.getSerieEquipamento(),			// 1
					img.getCodigoPista(),				// 2
					img.getCodigoPistaProdam(), 		// 3
					img.getDataImagem(),				// 4
					img.isMetrologica() ? "M" : 'N',	// 5
					"O",								// 6
					"1",								// 7	HARDCODED: A objetiva que vem do banco é sempra a "1ª" 
					img.getQualificador(),				// 8
					img.getIdImagemObj(),				// 9
					TipoMime.JPG.getExtensao()			// 10
				); 
				adicArquivoNoZip(zip, nomeArquivo, img.getBlobImagemObj());
				
				// Pode ser nulo (pois pode ter ou não a panorâmica). 
				if (img.getIdImagemPan() != null)
				{
					// A objetiva sempre vai ter. 
					nomeArquivo = String.format(ExportaAmostrasServlet.FORMATO_NOME,
							img.getSerieEquipamento(),			// 1
							img.getCodigoPista(),				// 2
							img.getCodigoPistaProdam(), 		// 3
							img.getDataImagem(),				// 4
							img.isMetrologica() ? "M" : 'N',	// 5
							"P",								// 6 HARDCODED: A panorâmica que vem do banco é sempra a "1ª"
							"1",								// 7
							img.getQualificador(),				// 8
							img.getIdImagemPan(),				// 9
							TipoMime.JPG.getExtensao()			// 10
					); 
					adicArquivoNoZip(zip, nomeArquivo, img.getBlobImagemPan());
				}
				
			}
		} catch (ModelException e) {
			new Mensagem(response).showErro(e.getMessage());
			return;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
		finally {
			if (itr != null)
				itr.close();
			if (zip != null) {
				try {
					zip.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	} 

	private ZipOutputStream criaArquivoSaidaZip(String sArq, HttpServletResponse response) throws ServletException, IOException {
		ZipOutputStream zip;

		response.setContentType("application/zip");
		response.setHeader("Content-Disposition","attachment; filename=\"" + sArq + "\"");

		zip = new ZipOutputStream(response.getOutputStream());
		return zip;
	}

	private void adicArquivoNoZip(ZipOutputStream zip, String nomeArquivo, byte[] conteudo) throws IOException, ModelException {
		if (conteudo == null)
			throw new ModelException("Conteúdo de imagem vazio.");
		
		zip.putNextEntry(new ZipEntry(nomeArquivo));
		zip.write(conteudo);
		zip.closeEntry();
	}	

}
