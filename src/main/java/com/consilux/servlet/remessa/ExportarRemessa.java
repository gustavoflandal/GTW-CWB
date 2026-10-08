/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 28/02/2007

  Descricao: Servlet para visualização de notificações geradas externamente.

  Historico:

    $Log: ExportarRemessa.java,v $
    Revision 1.3  2009/05/21 14:21:38  fos
    Agora mostra o PATH do arquivo caso não consiga criar TXT para remessa.

    Revision 1.2  2009/05/18 14:29:59  fos
    Renomeada para "exporta".

    Revision 1.1  2009/03/18 17:21:07  fos
    Primeira versão postada no CVS.

    Revision 1.1  2009/01/12 12:49:46  fos
    Recuperação de repositório.

    Revision 1.4  2008/08/20 13:41:29  fos
    ASSIGNED - bug 66: Log
    http://bugzilla.consilux.net/show_bug.cgi?id=66

    Revision 1.3  2007/05/04 13:58:24  fos
    Agora redireciona o output de exe externo para a default output.

    Revision 1.2  2007/04/17 18:00:38  fos
    Ajustado o pacote da classe ConfiguracaoException.

    Revision 1.1  2007/04/11 11:57:12  fos
    Reposicionado o diretório

    Revision 1.2  2007/03/16 12:56:15  fos
    Ajustes para documentação.


 *********************************************************************************/
package com.consilux.servlet.remessa;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.infra.CriptografiaAES;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.model.AcessoStorageInterface;
import com.consilux.model.AcessoStorageProvider;
import com.consilux.model.Base64Utils;
import com.consilux.model.Infracao;
import com.consilux.model.InfracaoObliteracao;
import com.consilux.model.ItemExportaRemessa;
import com.consilux.model.ItemExportaRemessaCET;
import com.consilux.model.Mensagem;
import com.consilux.model.MensagemJS;
import com.consilux.model.Remessa;
import com.consilux.model.TipoMime;
import com.consilux.model.VeiculoImagem;
import com.consilux.model.Video;
import com.consilux.model.exception.ModelException;
import com.consilux.model.remessa.ExportaRemessaCET;
import com.consilux.servlet.remessa.ExportarRemessaTarefa.TipoExportaRemessa;

/**
 * Servlet para exportação de remessa.
 * 
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.3 $ $Date: 2009/05/21 14:21:38 $ $Author: fos $
 */
public class ExportarRemessa extends HttpServlet implements
		javax.servlet.Servlet {

	private static final long serialVersionUID = -5581577827021543717L;
	private static Logger logger = Logger.getLogger(ExportarRemessa.class);

	/**
	 * Constrói o objeto
	 */
	public ExportarRemessa() {
		super();
	}

	private void raiseAndLogError(String message, Throwable rootCause,
			HttpServletResponse response) throws IOException {

		logger.error(message, rootCause);
		new Mensagem(response).showErro(message, null, true);
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
//		if (!new Acesso(request, response, true).verificaAcesso(false)) {
//			new MensagemJS(response).showErro("Usuário não atenticado!");
//			return; // O usuário não tem acesso...então cai fora!
//		}

		String sIdRemessa = request.getParameter("id_remessa");
		if (sIdRemessa == null || !Pattern.matches("[0-9]{1,8}", sIdRemessa)) {
			new MensagemJS(response)
					.showErro("Identificador da remessa enviado inválido!");
			return;
		}
		Remessa remessa = null;

		int iTipo = -1;
		String tipo = request.getParameter("tipo");
		if (tipo != null && tipo.equalsIgnoreCase("ml"))
			iTipo = 0;
		if (tipo != null && tipo.equalsIgnoreCase("lv"))
			iTipo = 1;

		try {
			remessa = Remessa.buscarRemessaPorId(Integer.valueOf(sIdRemessa));
		} catch (Exception e) {
			raiseAndLogError("Erro ao buscar remessa no banco de dados.", e,
					response);
		}

		if (iTipo == 1) {
			String diretorio = ConfiguracaoProvider.getInstance()
					.getConfiguracaoChaveValor().get("diretorio_apait");
			if (diretorio != null && diretorio.trim().length() > 0) {
				ExportarRemessaTarefa ert = new ExportarRemessaTarefa(remessa, TipoExportaRemessa.LoteValidado);
				ert.run();
				new MensagemJS(response)
						.showSucesso("Exportação de Lote Validado iniciado em "
								+ diretorio);
				return;
			}
		}

		ExportaRemessaCET exportaRemessa = null;

		try {
			exportaRemessa = new ExportaRemessaCET();
			exportaRemessa.setRemessa(remessa);
			exportaRemessa.setComObliteracao(true);
			exportaRemessa.setTipo(iTipo);
		} catch (Exception e) {
			raiseAndLogError("Erro ao montar estrutura para exportar remessa.",
					e, response);
		}

		String nomeArquivoZip = exportaRemessa.getNomeArquivoZip();
		StringBuilder sbTXT = new StringBuilder();

		// Escreve o cabeçalho da remessa no string builder que representa o
		// TXT.
		String cabecalho = exportaRemessa.getCabecalhoRemessa();
		if (cabecalho != null)
			sbTXT.append(cabecalho + "\r\n");

		ZipOutputStream zip = null;
		String nomeArquivoTexto = null;
		String nomeArquivoImagem = null;

		AcessoStorageInterface acesso_storage = AcessoStorageProvider
				.ObterInterface();

		// Como vamos abrir o iterator (e o seu ResultSet), protege com
		// try-finally.
		try {
			CriptografiaAES criptografia = new CriptografiaAES();

			// Valida os itens da remessa.
			exportaRemessa.validarRemessa();
			exportaRemessa.validarGeracaoRM(Integer.valueOf(sIdRemessa));

			// Aqui os itens passaram a validação. Então, abre um arquivo ZIP no
			// buffer de saída.
			zip = criaArquivoSaidaZip(nomeArquivoZip, response);

			List<ItemExportaRemessa> itens_exporta = exportaRemessa
					.getlistaItens();

			StringBuilder textoTarja = new StringBuilder();

			// ItemExportaRemessa item_ant = null;

			// Itera os itens, após a validação: XXX Felipe
			for (ItemExportaRemessa item : itens_exporta) {
				if (item.getSequenciaImagemLocal() == 0) {
					if (item.comVideo()) {
						Infracao infracao = Infracao.buscaInfracaoPorId(item
								.getIdInfracao());
						Video video = Video.buscaVideoPorIdVeiculo(infracao
								.getIdVeiculo(),0);
						String nomeArquivoVideo = item
								.getNomeArquivoVideo(video.getSequenciaVideo());
						byte[] video_bytes = video.getVideo();

						if (video_bytes == null && video.getCaminho() != null) {
							video_bytes = acesso_storage.ObterArquivo(video
									.getCaminho());
						}

						adicArquivoNoZip(zip, nomeArquivoVideo, video_bytes);
					}

					// Criando uma linha, com o nome e colocando no
					// StringBuilder
					sbTXT.append(item.getLinhaRemessa() + "\r\n");
					textoTarja = item.getDadosTarja();
				}

				nomeArquivoTexto = item.getNomeArquivoTXT();

				byte[] blobImagem, blobArquivoObl = null;
				List<byte[]> blobImagemOblit = new ArrayList<byte[]>();
				InfracaoObliteracao obliteracao = item.getObliteracao();
				List<Rectangle> obliteracoes = new ArrayList<Rectangle>();
				
				blobImagem = item.getImagem();
				if (blobImagem == null) {
					VeiculoImagem vi = VeiculoImagem
							.buscaVeiculoImagemPorIdImagem(item.getIdImagem());
					blobImagem = vi.getImagem();
					blobArquivoObl = vi.getArquivoObl();
				}
				
				if (item.getSequenciaImagemLocal() == 0 && obliteracao != null && ((ItemExportaRemessaCET) item).getIdInconsistencia() == 0) {
					
					// Ajuste de cálculo de tamanho da obliteração
					BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(item.getImagem()));
					int img_largura = imagem.getWidth();
					int img_altura = imagem.getHeight();
					imagem = null; 
					
					obliteracoes = obliteracao.getListObliteracoes();
					for(int t = 0; t<obliteracoes.size(); t++) {
					obliteracoes.get(t).x = Double.valueOf( Math.floor( (img_largura * obliteracoes.get(t).x) / 640 ) ).intValue();
					obliteracoes.get(t).y = Double.valueOf( Math.floor( (img_altura * obliteracoes.get(t).y) / 480 ) ).intValue();
					obliteracoes.get(t).width = Double.valueOf( Math.floor( (img_largura * obliteracoes.get(t).width) / 640 ) ).intValue();
					obliteracoes.get(t).height = Double.valueOf( Math.floor( (img_altura * obliteracoes.get(t).height) / 480 ) ).intValue();
					
					blobImagemOblit.add( InfracaoObliteracao.obterParteObliterada(
							item.getImagem(), obliteracoes.get(t).x,
							obliteracoes.get(t).y, obliteracoes.get(t).height,
							obliteracoes.get(t).width) );
					}
					
					for(int t = 0; t<obliteracoes.size(); t++) {
					blobImagem = InfracaoObliteracao.obliteraImagem(
							blobImagem, obliteracoes.get(t).x,
							obliteracoes.get(t).y, obliteracoes.get(t).height,
							obliteracoes.get(t).width);
					}
				}

				nomeArquivoImagem = item.getNomeArquivoImagem();

				// Valida o blob.
				if (blobImagem == null || blobImagem.length == 0) {
					throw new ModelException("Imagem nula ou vazia ["
							+ item.getIdImagem() + "]");
				}

				// Adiciona a imagem (o blob) no ZIP
				adicArquivoNoZip(zip, nomeArquivoImagem, blobImagem);
				blobImagem = null;

				if (blobImagemOblit.size() > 0) {
					StringBuilder oblit_str = new StringBuilder();
					
					for(int t = 0; t<obliteracoes.size(); t++) {
						oblit_str.append(obliteracoes.get(t).x);
						oblit_str.append(';');
						oblit_str.append(obliteracoes.get(t).y);
						oblit_str.append(';');
						oblit_str.append(Base64Utils.EncodeBase64(blobImagemOblit.get(t)));
						oblit_str.append("\r\n");
					}
					blobImagemOblit.clear();
					blobImagemOblit = null;

					byte[] oblit_enc = criptografia.criptografaAES(oblit_str.toString()
							.getBytes());
					oblit_str = null;

					adicArquivoNoZip(zip,
							nomeArquivoImagem.replace(".JPG", ".OBL"),
							oblit_enc);
					oblit_enc = null;
				}
				if (blobArquivoObl != null) {
					adicArquivoNoZip(zip,
							nomeArquivoImagem.replace(".JPG", ".OBL"),
							blobArquivoObl);
					blobArquivoObl = null;
				}

				adicArquivoNoZip(zip, nomeArquivoTexto, textoTarja);
			}

			// Adiciona o conteúdo do TXT (StringBuilder) no ZIP
			String nomeArquivoTxt = exportaRemessa.getNomeArquivoTXT();
			adicArquivoNoZip(zip, nomeArquivoTxt, sbTXT);
			// adicArquivoNoZip(zip, nomeArquivoMD5, sbMD5);
		} catch (SocketException se) {
			// Erro normal que pode acontecer, quando a conexão é encerrada
			// (normalmente pelo usuario, no browser)
			logger.info("Conexão HTTP finalizada.", se);
		} catch (Exception e) {
			// Se ocorreu erro, logar e propagar a exceção.
			raiseAndLogError(e.getMessage(), e, response);
		} finally {
			try {
				exportaRemessa.fecharConexaoItens();
				if (remessa.getDataValidacao() == null)
					exportaRemessa.getRemessa().incrementarRevisaoRemessa();
			} catch (SQLException ex) {
				throw new ServletException(
						"Erro ao fechar conexão com o banco.", ex);
			} catch (ConexaoException ex) {
				throw new ServletException(
						"Erro ao obter conexão com o banco.", ex);
			}
			if (zip != null) {
				try {
					zip.close();
				} catch (Exception e) {
				}
			}
			sbTXT = null;
		}

	}

	private ZipOutputStream criaArquivoSaidaZip(String sArq,
			HttpServletResponse response) throws ServletException, IOException {
		ZipOutputStream zip;

		response.setContentType(TipoMime.ZIP.getTipo());
		response.setHeader("Content-Disposition", "attachment; filename=\""
				+ sArq + "\"");

		zip = new ZipOutputStream(response.getOutputStream());

		// Desliga a compressão no ZIP (pois JPG não adianta nada de qualquer
		// maneira).
		zip.setMethod(ZipOutputStream.DEFLATED);
		zip.setLevel(0);

		return zip;
	}

	private void adicArquivoNoZip(ZipOutputStream zip, String nome,
			StringBuilder conteudo) throws IOException {

		zip.putNextEntry(new ZipEntry(nome));
		PrintStream ps = new PrintStream(zip);
		ps.print(conteudo.toString());
		ps.flush();
		;
		zip.closeEntry();

	}

	private void adicArquivoNoZip(ZipOutputStream zip, String nome,
			byte[] conteudo) throws IOException {

		zip.putNextEntry(new ZipEntry(nome));
		zip.write(conteudo);
		zip.closeEntry();
	}
}