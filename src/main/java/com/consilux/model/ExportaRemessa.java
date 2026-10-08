/**
 * 
 */
package com.consilux.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

//import org.apache.log4j.Logger;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.Conexao;
import com.consilux.model.exception.ModelException;
 
/**
 * Classe de negócio para a exportação de remessas.
 * @author Raoni
 */
public abstract class ExportaRemessa<E extends ItemExportaRemessa> {

	//private static Logger logger = Logger.getLogger(ExportaRemessa.class);
	
	private Remessa remessa;
	private boolean preValidar;
	private boolean comObliteracao;
	private static ValidatorFactory validatorFactory; 
	private Validator validator;
	private List<ItemExportaRemessa> listaItens;
	private int tipo;
	
	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}

	protected Connection connItens;

	static  {
		validatorFactory = Validation.buildDefaultValidatorFactory();
	}
	
	/**
	 * Construtor que permite definir se desejamos pré-validar os itens (ou não)
	 * @param preValidar define se deve ocorrer pré-validação.
	 */
	public ExportaRemessa(final boolean preValidar) {
		this.preValidar = preValidar;
		this.validator = validatorFactory.getValidator();
	}
	
	/**
	 * Construtor padrão, com prévalidação habilitada.
	 */
	public ExportaRemessa() {
		this(true);
	}

	public ExportaRemessa(final Remessa remessa, final boolean comObliteracao, final Boolean preValidar) {
		this.remessa = remessa;
		this.comObliteracao = comObliteracao;		
		this.preValidar = preValidar;
	}
	
	public abstract String getNomeArquivoTXT();

	protected abstract ResultSet buscarItensRemessa(boolean comBlobImagem)
	throws SQLException, ConexaoException;

	/**
	 * Método que deve ser implementado pelas classes filhas para criar os itens.
	 * @param rs
	 * @param comBlobImagemImagem
	 * @throws ModelException
	 */	
	protected abstract E constroiItem(ResultSet rsItens, boolean comBlobImagem)	throws SQLException, ModelException, IOException;
	
	/**
	 * Realiza uma validação "comum" dos itens, independente se é uma remessa CET, URBS, etc.
	 * @param itemValidar o item a ser validado.
	 * @throws ModelException
	 */
	public final void preValidarItem(final E itemValidar) throws ModelException {
		
		if (itemValidar == null)
		{
			throw new ModelException("Item de remessa nulo.");
		}

		Set<ConstraintViolation<E>> violations = validator.validate(itemValidar);
		
		if (violations.size() > 0)
		{
			throw new ModelException(violations.iterator().next().getMessage());
		}
		
		// TODO: rever se a validação que compara a data do item deve ser realizada aqui
		// ou na própria remessa. Acredito ser responsabilidade da remessa e não do item.
		// XXX: FELIPE: Removido validação de data de infração para permitir gerar uma
		// remessa do dia atual
		if (itemValidar.getDataInfracao() == null //|| itemValidar.getDataInfracao().after(remessa.getData())
				)
		{
			throw new ModelException("Data da infração não pode ser nula nem maior que a data da remessa.");
		}

	}
	/**
	 * Método que pode ser implementado / complementado pelas classes filhas para validar os itens.
	 * @param itemValidar
	 * @throws ModelException
	 */
	protected abstract void validarItem(E itemValidar) throws ModelException;
	
	/**
	 * Realiza a validação da remessa, depois de criada mas antes de exportar (o ZIP).
	 * @throws ModelException
	 */
	public final void validarRemessa() throws ModelException
	{
		StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao validar itens da remessa.");
		
		@SuppressWarnings("unused")
		Integer currAuto = 0;
		@SuppressWarnings("unused")
		Integer prevAuto = 0;
		
		E currItem = null;
		
		try {
			// Cria um iterator, sem imagens (BLOBs), para realizar a validação.
			final ExportaRemessaItr<E> remessaItr = new ExportaRemessaItr<E>(this, true);
			
			// Declara uma lista com os itens que já passaram na validação
			List<ItemExportaRemessa> listaItensPassouValidacao = new LinkedList<ItemExportaRemessa>();
			
			for (E itemValidar : remessaItr)
			{				
				currItem = itemValidar;
				
				// Realiza a validação em cada item da remessa.
				if (this.preValidar)
				{
					preValidarItem(itemValidar);
				}
				
				validarItem(itemValidar);
				
//				// Valida se o nº dos autos está sequencial.
//				if(itemValidar.getSequenciaImagemLocal() == 0)
//				{
//					if(currAuto == 0)
//						currAuto = itemValidar.getAuto();
//					else
//					{
//						prevAuto = currAuto;
//						currAuto = itemValidar.getAuto();
//					}
//				}
//				if (prevAuto != 0 && prevAuto + 1 != currAuto)
//				{
//					sbErro.append("Falha na sequência dos autos. [");
//					sbErro.append(Integer.toString(prevAuto));
//					sbErro.append(" -> ");
//					sbErro.append(Integer.toString(currAuto));
//					sbErro.append("]");
//				}
				
				// Tudo OK com o item, pode colocar ele na lista.
				listaItensPassouValidacao.add(itemValidar);
			}
			
			// Terminou o laço de validação, armazena a lista dos itens, pois estão todos OK
			listaItens = listaItensPassouValidacao;

		}
		catch(IOException ex) {
			throw new ModelException(sbErro.toString(), ex);
		} catch (ConexaoException ex) {
			throw new ModelException(sbErro.toString(), ex);
		} catch (SQLException ex) {
			throw new ModelException(sbErro.toString(), ex);
		}
		catch (ModelException ex) {
			
			sbErro = new StringBuilder("Erro de validação");
			if (currItem != null) {
				sbErro.append(" na infração ");
				sbErro.append(Integer.toString(currItem.idInfracao));
			}
			sbErro.append("\r\n");
			sbErro.append(ex.getMessage());
			throw new ModelException(sbErro.toString() , ex);
		}
	}
	
	/**
	 * Autor: Luiz Amaral 23/10/2014
	 * Realiza a validação da remessa, depois de criada mas antes de exportar (o ZIP).
	 * @throws ConexaoException 
	 * @throws SQLException 
	 */
	public void validarGeracaoRM(Integer idRemessa) throws ModelException, ConexaoException, SQLException
	{
		StringBuilder sbSQL = new StringBuilder();
		boolean blnImprimErro = false;
		StringBuilder erro = new StringBuilder("Erro geração RM (Movimento de Lote): \r\n <br> <br>");

		sbSQL.append("	select ");
		sbSQL.append("		resultado ");
		sbSQL.append("	from fcn_ValidarGeracaoLM (?) ");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = Conexao.getConexao();
			ps = conn.prepareStatement(sbSQL.toString());
			ps.setInt(1, idRemessa);

			rs = ps.executeQuery();
			
			while(rs.next()){
				erro.append(rs.getString("resultado"));
				erro.append("<br>");
				blnImprimErro = true;
			}
			
			if (blnImprimErro) 
				throw new ModelException(erro.toString());
			
		} catch (SQLException ex) {
			StringBuilder sbErro = new StringBuilder("Erro de banco de dados ao validar Geralção do RM (Movimento de Lote).");
			throw new ModelException(sbErro.toString(), ex);
		}
		finally {
			if (conn != null)
				conn.close();
		}
	}
	
	public boolean isComObliteracao() {
		return comObliteracao;
	}

	public void setComObliteracao(final boolean comObliteracao) {
		this.comObliteracao = comObliteracao;
	}	
	
	
	public final List<ItemExportaRemessa> getlistaItens() throws ValidationException {
		
		if (listaItens == null)
		{
			throw new ValidationException("Remessa ainda não passou pela validação, lista nula.");
		}
		return listaItens;
	}
	
	/**
	 * Retorna o valor do campo 'preValidar' atual.
	 * @return the preValidar
	 */
	public boolean isPreValidar() {
		return this.preValidar;
	}

	public Remessa getRemessa() {
		return remessa;
	}

	public void setRemessa(final Remessa remessa) {
		this.remessa = remessa;
	}	
	
	public abstract String getCabecalhoRemessa();
	public abstract String getNomeArquivoZip();
	public abstract void fecharConexaoItens() throws SQLException;
	
}
