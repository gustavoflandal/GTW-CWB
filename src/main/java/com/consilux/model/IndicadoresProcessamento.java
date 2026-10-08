/**
 * 
 */
package com.consilux.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;

/**
 * @author fos
 *
 */
public class IndicadoresProcessamento {
	public static final int NUMERO_DIAS_ATRASO = 8;
	public static final int NUMERO_DIAS_ATRASO_REMESSA = 12;
	private Integer diasAtrasoAtual;
	private Integer diasAtrasoRemessa;
	private Integer diasAtrasoValidacao;
	private Integer totalEstouroPrazoProcessamento;
	private Integer totalEstouroPrazoProcessamentoAnterior;
	private Integer totalErrosProcessamento;
	private Integer totalErrosProcessamentoAnterior;
	private Integer diasAtrasoIsento;
	private Integer diasAtrasoReprovado;
	private Integer arquivosVerificados;
	private Date dataArquivosCAV;
	private Integer errosRemessaAutomatico;
	private Integer errosProcessamentoMesAtual;
	private Integer errosProcessamentoMesAnterior;
	
	private static AcessoArquivosFTP acessoFTP = new AcessoArquivosFTP();

	/**
	 * @param diasAtrasoAtual
	 * @param totalEstouroPrazoProcessamento
	 * @param totalErrosProcessamento
	 * @param diasAtrasoIsento
	 */
	public IndicadoresProcessamento(Integer diasAtrasoAtual, Integer diasAtrasoReprovado, Integer diasAtrasoRemessa,Integer diasAtrasoValidacao,
			Integer totalEstouroPrazoProcessamento, Integer totalEstouroPrazoProcessamentoAnterior,
			Integer totalErrosProcessamento, Integer totalErrosProcessamentoAnterior,
			Integer diasAtrasoIsento, Integer arquivosVerificados, Date dataArquivosCAV, int errosRemessaAutomatico,
			Integer errosProcessamentoMesAtual, Integer errosProcessamentoMesAnterior) {
		super();
		this.diasAtrasoAtual = diasAtrasoAtual;
		this.diasAtrasoReprovado = diasAtrasoReprovado;
		this.diasAtrasoRemessa = diasAtrasoRemessa;
		this.diasAtrasoValidacao = diasAtrasoValidacao;
		this.totalEstouroPrazoProcessamento = totalEstouroPrazoProcessamento;
		this.totalEstouroPrazoProcessamentoAnterior = totalEstouroPrazoProcessamentoAnterior;
		this.totalErrosProcessamento = totalErrosProcessamento;
		this.totalErrosProcessamentoAnterior = totalErrosProcessamentoAnterior;
		this.diasAtrasoIsento = diasAtrasoIsento;
		this.arquivosVerificados = arquivosVerificados;
		this.dataArquivosCAV = dataArquivosCAV;
		this.errosRemessaAutomatico = Integer.valueOf(errosRemessaAutomatico);
		this.errosProcessamentoMesAtual = errosProcessamentoMesAtual;
		this.errosProcessamentoMesAnterior = errosProcessamentoMesAnterior;
	}

	public static IndicadoresProcessamento buscaIndicadores() throws ConexaoException, SQLException, IOException {
		
		StringBuilder sbSQL = new StringBuilder();
		IndicadoresProcessamento ret = null;
		
		sbSQL.append("SELECT dias_atraso_atual, ");
		sbSQL.append("	     dias_atraso_reprovado, ");
		sbSQL.append("       dias_atraso_remessa, ");
		sbSQL.append("       dias_atraso_validacao, ");
		sbSQL.append("	     total_atraso, ");
		sbSQL.append("	     total_atraso_ant, ");
		sbSQL.append("	     total_erro, ");
		sbSQL.append("	     total_erro_ant, ");
		sbSQL.append("	     dias_atraso_cad_isento, ");
		sbSQL.append("	     arquivos_verificados, "); 
		sbSQL.append("	     data_arquivos_cav, ");
		sbSQL.append("       erros_remessa_automatico, ");
		sbSQL.append("	     erros_mes_atual, ");
		sbSQL.append("       erros_mes_anterior ");
		sbSQL.append(" FROM  fcn_IndicadoresProcessamentoPrincipal_alt(?) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, NUMERO_DIAS_ATRASO);
			
			rs = ps.executeQuery();
			if (rs.next()) {
				ret = new IndicadoresProcessamento(
						rs.getInt("dias_atraso_atual"),
						rs.getInt("dias_atraso_reprovado"),
						rs.getInt("dias_atraso_remessa"),
						rs.getInt("dias_atraso_validacao"),
						rs.getInt("total_atraso"),
						rs.getInt("total_atraso_ant"),
						rs.getInt("total_erro"),
						rs.getInt("total_erro_ant"),
						rs.getInt("dias_atraso_cad_isento"),
						rs.getInt("arquivos_verificados"),
						rs.getTimestamp("data_arquivos_cav"), 
				        rs.getInt("erros_remessa_automatico"),
				        rs.getInt("erros_mes_atual"),
						rs.getInt("erros_mes_anterior")
					);
			}
		}		
		catch(Exception e) {}
		finally {
			if (conn != null)
				conn.close();							
		}
		return ret;				
	}

	
	public static Integer[] buscaErrosProcessamentoFTP() throws IOException {
		
		Integer[] errosProcessamento = new Integer[2];
		
		Integer intErrosMesAnterior = 0;
		Integer intErrosMesAtual = 0;
		
		
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat formatoMes = new SimpleDateFormat("MM", new Locale("pt", "BR"));
		SimpleDateFormat formatoAno = new SimpleDateFormat("yyyy", new Locale("pt", "BR"));
		Date atual = cal.getTime();
		cal.add(Calendar.MONTH, -1);
		Date anterior = cal.getTime();
		
		
		String mesAnterior = formatoMes.format(anterior);
		String anoAnterior = formatoAno.format(anterior);
		String enderecoMesAnterior = "/PROCESSAR/Medicao" + "/" + anoAnterior + "/" + mesAnterior;
		String nomeArquivoMesAnterior = null;
		
		List<String> arquivosMesAnterior = acessoFTP.ListarArquivosDir(enderecoMesAnterior);
		
		if (!arquivosMesAnterior.isEmpty()) {
			for(int i = 0; i < arquivosMesAnterior.size(); i++){
				
				String nomeRelatorio = arquivosMesAnterior.get(i);
				if(nomeRelatorio.contains("Erros na Valida")) {
					nomeArquivoMesAnterior = nomeRelatorio;
					break;
				}
			}
			
			intErrosMesAnterior = obterErrosProcessamentoFTP(enderecoMesAnterior, nomeArquivoMesAnterior);
		}
		
		errosProcessamento[0] = intErrosMesAnterior;
		
		String mesAtual = formatoMes.format(atual);
		String anoAtual = formatoAno.format(atual);
		String enderecoMesAtual = "/PROCESSAR/Medicao" + "/" + anoAtual + "/" + mesAtual;
		
		String nomeArquivoMesAtual = null;
		
		List<String> arquivosMesAtual = acessoFTP.ListarArquivosDir(enderecoMesAtual);
		
		if (!arquivosMesAtual.isEmpty()) {
			for(int i = 0; i < arquivosMesAtual.size(); i++){
				
				String nomeRelatorio = arquivosMesAtual.get(i);
				if(nomeRelatorio.contains("Erros na Valida")) {
					nomeArquivoMesAtual = nomeRelatorio;
					break;
				}
			}
				
			intErrosMesAtual = obterErrosProcessamentoFTP(enderecoMesAtual, nomeArquivoMesAtual);
		}
		
		errosProcessamento[1] = intErrosMesAtual;

		return errosProcessamento;
		
	}
	
	public static int obterErrosProcessamentoFTP(String endereco, String nomeArquivo) throws IOException {
  
		Double dblValor = 0.0;
		Integer intValor = 0;
		byte[] arquivoFTP = null;
		
        try {
        	arquivoFTP = acessoFTP.ObterArquivo(endereco + "/" + nomeArquivo);
        	ByteArrayInputStream arquivo = new ByteArrayInputStream(arquivoFTP);
  
        	HSSFWorkbook workbook = new HSSFWorkbook(arquivo);
  
            HSSFSheet sheet = workbook.getSheetAt(0);
  
            Integer linha = (sheet.getLastRowNum() - 3);
                
            Row row = sheet.getRow(linha);
            Cell cell = row.getCell(2);
            
            dblValor = cell.getNumericCellValue();
            intValor = dblValor.intValue();
            
            workbook.close();
                
        } catch (Exception e) {
        	e.printStackTrace();
            System.out.println("Arquivo de erros de validação não encontrado!");
        }
       
        return intValor;
	}
 	
	
	/**
	 * @return the diasAtrasoAtual
	 */
	public Integer getDiasAtrasoAtual() {
		return diasAtrasoAtual;
	}

	/**
	 * @return the diasAtrasoRemessa
	 */
	public Integer getDiasAtrasoRemessa() {
		return diasAtrasoRemessa;
	}

	/**
	 * @return the diasAtrasoValidacao
	 */
	public Integer getDiasAtrasoValidacao() {
		return diasAtrasoValidacao;
	}

	/**
	 * @return the totalEstouroPrazoProcessamento
	 */
	public Integer getTotalEstouroPrazoProcessamento() {
		return totalEstouroPrazoProcessamento;
	}
	
	public Integer getTotalEstouroPrazoProcessamentoAnterior() {
		return totalEstouroPrazoProcessamentoAnterior;
	}

	/**
	 * @return the totalErrosProcessamento
	 */
	public Integer getTotalErrosProcessamento() {
		return totalErrosProcessamento;
	}
	
	public Integer getTotalErrosProcessamentoAnterior()
	{
		return totalErrosProcessamentoAnterior;
	}
	
	public Boolean getValidacaoEmAtraso() {
		return diasAtrasoRemessa > (NUMERO_DIAS_ATRASO_REMESSA * 0.75);
	}
	
	public Boolean getValidacaoEmAlerta() {
		return diasAtrasoRemessa > (NUMERO_DIAS_ATRASO_REMESSA * 0.5);
	}
	
	public Boolean getRemessaEmAtraso() {
		return diasAtrasoRemessa > (NUMERO_DIAS_ATRASO * 0.75);
	}
	
	public Boolean getRemessaEmAlerta() {
		return diasAtrasoRemessa > (NUMERO_DIAS_ATRASO * 0.5);
	}
	
	public Boolean getEmAtraso() {
		return diasAtrasoAtual > (NUMERO_DIAS_ATRASO * 0.75);
	}
	
	public Boolean getEmAlerta() {
		return diasAtrasoAtual > (NUMERO_DIAS_ATRASO * 0.5);
	}

	/**
	 * @return the diasAtrasoIsento
	 */
	public Integer getDiasAtrasoIsento() {
		return diasAtrasoIsento;
	}

	public Integer getDiasAtrasoReprovado() {
		return diasAtrasoReprovado;
	}

	public Integer getArquivosVerificados() {
		return arquivosVerificados;
	}

	public void setArquivosVerificados(Integer arquivosVerificados) {
		this.arquivosVerificados = arquivosVerificados;
	}

	public Date getDataArquivosCAV() {
		return dataArquivosCAV;
	}

	public void setDataArquivosCAV(Date dataArquivosCAV) {
		this.dataArquivosCAV = dataArquivosCAV;
	}

	public String getArquivosVerificadosDesc() {
		if (arquivosVerificados == 3) {
			return "OK";
		}
		if (arquivosVerificados > 0) {
			return "Parcial";
		}
		return "Pendente";
	}
	
	public Integer getErrosRemessaAutomatico() {
		return errosRemessaAutomatico;
	}
		  
	public void setErrosRemessaAutomatico(Integer errosRemessaAutomatico) {
		this.errosRemessaAutomatico = errosRemessaAutomatico;
	}


	public Integer getErrosProcessamentoMesAtual() {
		return errosProcessamentoMesAtual;
	}
	public void setErrosProcessamentoMesAtual(Integer errosProcessamentoMesAtual) {
		this.errosProcessamentoMesAtual = errosProcessamentoMesAtual;
	}

	
	public Integer getErrosProcessamentoMesAnterior() {
		return errosProcessamentoMesAnterior;
	}
	public void setErrosProcessamentoMesAnterior(
			Integer errosProcessamentoMesAnterior) {
		this.errosProcessamentoMesAnterior = errosProcessamentoMesAnterior;
	}
}
