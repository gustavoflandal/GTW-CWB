package com.consilux.model;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import com.consilux.infra.CriptografiaAES;
import com.consilux.lib.Conexao;

public class ProcessarObliteracaoPermanente extends Thread {

	private final static Logger logger = Logger
			.getLogger(ProcessarObliteracaoPermanente.class);
	private int idInfracaoProcesso;
	private List<Rectangle> rec;
	private Boolean erroObliteracao;
	private Integer infracaoDesobliteracao;

	public ProcessarObliteracaoPermanente(int idInfracaoProcesso, Rectangle rec, Boolean erroObliteracao, Integer infracaoDesobliteracao) {
		this.idInfracaoProcesso = idInfracaoProcesso;
		this.rec = new ArrayList<Rectangle>();
		this.rec.add(rec);
		this.erroObliteracao = erroObliteracao;
		this.infracaoDesobliteracao = infracaoDesobliteracao;
	}
	
	public ProcessarObliteracaoPermanente(int idInfracaoProcesso, List<Rectangle> rec, Boolean erroObliteracao, Integer infracaoDesobliteracao) {
		this.idInfracaoProcesso = idInfracaoProcesso;
		this.rec = rec;
		this.erroObliteracao = erroObliteracao;
		this.infracaoDesobliteracao = infracaoDesobliteracao;
	}
	
	public ProcessarObliteracaoPermanente(int idInfracaoProcesso, Boolean erroObliteracao, Integer infracaoDesobliteracao) {
		this.idInfracaoProcesso = idInfracaoProcesso;
		this.rec = new ArrayList<Rectangle>();
		this.erroObliteracao = erroObliteracao;
		this.infracaoDesobliteracao = infracaoDesobliteracao;
	}
	
	public void AdicionarObliteracao(int x, int y, int largura, int altura)
	{
		rec.add(new Rectangle(x, y, largura, altura));
	}
	public void AdicionarObliteracao(Rectangle r)
	{
		rec.add(r);
	}

	@Override
	public void run() {

		logger.info("INICIANDO PROCESSO");

		try {
			CriptografiaAES criptografia = new CriptografiaAES();
			AcessoStorageInterface acesso = AcessoStorageProvider.ObterInterface();
			
			InfracaoProcesso ip = InfracaoProcesso
					.buscaInfracaoProcessoPorId(idInfracaoProcesso);
			VeiculoImagem vi = VeiculoImagem
					.buscaVeiculoImagemPorIdImagem(ip.getIdImagem());
			boolean manter_obl_cai = !(infracaoDesobliteracao.equals(ip.getIdInfracao()));
			String caminho = vi.getCaminho();
			byte[] bImg = vi.getImagem();
			List<byte[]> blobImagemOblit = new ArrayList<byte[]>();
			List<Rectangle> obliteracoesAtuais = null;
			if(manter_obl_cai)
				obliteracoesAtuais = new ArrayList<Rectangle>();

			if (rec != null && caminho != null) {
				
				String caminho_obl = caminho.replace(".JPG", ".OBL");
				
				// Caso a imagem já tenha uma obliteração, desenha a imagem novamente
				try {
					byte[] bOblitAtual = acesso.ObterArquivo(caminho_obl);
					
					if(bOblitAtual != null) {
						byte[] bImgDesob = InfracaoObliteracao.desobliteraImagem(bImg, bOblitAtual, obliteracoesAtuais);
						
						bImg = bImgDesob;
						bOblitAtual = null;
						bImgDesob = null;
					}
				} catch(Exception e) {}
				
				int tam_at = rec.size();
				
				if(obliteracoesAtuais != null) {
				rec.addAll(obliteracoesAtuais);
				obliteracoesAtuais.clear();
				}
				
				// Ajuste de cálculo de tamanho da obliteração
				BufferedImage imagem = ImageIO.read(new ByteArrayInputStream(bImg));
				int img_largura = imagem.getWidth();
				int img_altura = imagem.getHeight();
				imagem = null; 
				
				for(int t = 0; t<rec.size(); t++) {	
					if(t<tam_at) {
					rec.get(t).x = Double.valueOf( Math.floor( (img_largura * rec.get(t).x) / 640 ) ).intValue();
					rec.get(t).y = Double.valueOf( Math.floor( (img_altura * rec.get(t).y) / 480 ) ).intValue();
					rec.get(t).width = Double.valueOf( Math.floor( (img_largura * rec.get(t).width) / 640 ) ).intValue();
					rec.get(t).height = Double.valueOf( Math.floor( (img_altura * rec.get(t).height) / 480 ) ).intValue();
					}
					logger.info("X: " + rec.get(t).x + "; Y: " + rec.get(t).y + "; Altura: " + rec.get(t).height + "; Largura: " + rec.get(t).width);
					
					blobImagemOblit.add( InfracaoObliteracao.obterParteObliterada(
							bImg, 
							rec.get(t).x, 
							rec.get(t).y,
							rec.get(t).height, 
							rec.get(t).width) );
				}
				
				for(int t = 0; t<rec.size(); t++) {
					bImg = InfracaoObliteracao.obliteraImagem(
							bImg, 
							rec.get(t).x, 
							rec.get(t).y,
							rec.get(t).height, 
							rec.get(t).width);
				}
				
				StringBuilder sParteOblit = new StringBuilder();
				for(int t = 0; t<rec.size(); t++) {
					sParteOblit.append(rec.get(t).x);
					sParteOblit.append(';');
					sParteOblit.append(rec.get(t).y);
					sParteOblit.append(';');
					sParteOblit.append(Base64Utils.EncodeBase64(blobImagemOblit.get(t)));
					sParteOblit.append("\r\n");
				}
				blobImagemOblit.clear();
				blobImagemOblit = null;
				
				byte[] bParteOblitEnc = criptografia.criptografaAES(sParteOblit.toString().getBytes());
				sParteOblit = null;
				acesso.EnviarArquivo(caminho.replace(".JPG", ".OBL"), bParteOblitEnc);
				
				acesso.EnviarArquivo(caminho, bImg);
				
				logger.info("IMAGEM " + caminho + " OBLITERADA PERMANENTE");
			}

		} catch (Exception e) {
			logger.error("Erro ao OBLITERAR imagem PERMANENTE", e);
		}

		ProcessarErroObliteracao();
		
		logger.info("FIM PROCESSO");
	}
	
	private void ProcessarErroObliteracao()
	{
		Connection conn = null;
		
		StringBuilder sbSQL = new StringBuilder();
		sbSQL.append("UPDATE infracao_processo WITH(ROWLOCK) "); 
		if(erroObliteracao == null || erroObliteracao == false)
			sbSQL.append("SET erro_oblit = NULL ");
		else
			sbSQL.append("SET erro_oblit = ? ");
		sbSQL.append("WHERE id_infracao_processo = ? ");
		
		int indice = 1, atualizado = 0;
		
		try {
			conn = Conexao.getConexao();
			PreparedStatement ps = conn.prepareCall(sbSQL.toString());
			if(erroObliteracao == true)
				ps.setBoolean(indice++, erroObliteracao);
			ps.setInt(indice, idInfracaoProcesso);
			atualizado = ps.executeUpdate();
		} catch(Exception e) {
			logger.error("Erro ao ProcessarErroObliteracao", e);
		} finally {
			try {
				if(conn != null)
					conn.close();
			} catch(Exception e) { logger.error("Erro ao fechar conexão", e); }
		}
		
		logger.info("ALTERADO " + atualizado + " INFRACAO_PROCESSO " + idInfracaoProcesso + " ERRO OBLI: " + erroObliteracao);
	}

}
