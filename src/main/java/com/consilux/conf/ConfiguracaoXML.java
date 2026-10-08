/**********************************************************************************

  Projeto: GTW
  Nome do Modulo: GTW

  Empresa: Consilux Tecnologia

  Autor: Fernando Oliveira da Silva
  Data: 26/12/2006

  Descricao: Classe de controle da configuração geral do sistema.

  Historico:

    $Log: Configuracao.java,v $
    Revision 1.14  2009/06/01 18:40:57  fos
    Adicionado o parâmetro para alerta de velocidade.

    Revision 1.13  2009/05/08 18:47:00  fos
    Criado parâmetro para indicar se a espécie deve ser controlada no processamento.

    Revision 1.12  2009/04/23 18:52:46  fernando
    novos métodos:
      - getIdGrupoPermissaoNovoEquip
      - getIdGrupoPermissaoEditConfigEquip

    Revision 1.11  2009/04/13 16:40:13  fos
    Retirado parâmetro antigo de configuração equipamento.

    Revision 1.10  2009/03/25 14:01:29  raoni
    Adicionado parâmetro de configuração para grupo_equipamento.

    Revision 1.9  2009/01/12 12:49:51  fos
    Recuperação de repositório.

    Revision 1.7  2008/11/11 16:22:14  fos
    Retirado obrigação de preecher com usuário e senha, porque o novo driver JDBC do SQLServer pode autenticar pelo windows.

    Revision 1.6  2008/08/12 12:58:00  fos
    Agora carrega a configuração de obliteração.

    Revision 1.5  2007/05/04 13:56:02  fos
    Agora carrega também carrega a informação do database a ser acessado.

    Revision 1.4  2007/04/17 18:00:38  fos
    Ajustado o pacote da classe ConfiguracaoException.

    Revision 1.3  2007/03/16 12:55:48  fos
    Ajustes para documentação.


*********************************************************************************/
package com.consilux.conf;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;

import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileHandler;

import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.ExpValida;
import com.consilux.model.ExportaRemessa;
import com.consilux.model.TipoGrupo;
import com.consilux.model.TipoRemessa;
import com.consilux.model.remessa.RemessaFactory;

/**
 * Classe para controle de configurações do sistema, baseada em arquivo XML.
 * @author Fernando Oliveira da Silva - Consilux Tecnologia
 * @version $Revision: 1.14 $ $Date: 2009/06/01 18:40:57 $ $Author: fos $
 */
public class ConfiguracaoXML implements Configuracao {

	@SuppressWarnings("unused")
	private static final long serialVersionUID = 4424838520006505005L;
	
	private ConfiguracaoArmazenamento configuracaoArmazenamento;
	private ConfiguracaoProcessamento configuracaoProcessamento;
	private ConfiguracaoRemessa configuracaoRemessa;
	private ConfiguracaoGrupos configuracaoGrupos;
	private ConfiguracaoEquipamento configuracaoEquipamento;
	private List<ConfiguracaoServidorSmtp> listaServidoresSmtp = new ArrayList<ConfiguracaoServidorSmtp>();
	private Map<String, ConfiguracaoWS> mapWS = new TreeMap<String, ConfiguracaoWS>();
	private String assuntoEmailVeiculoMonitorado;
	private ConfiguracaoChaveValor configuracaoChaveValor;
	private ConfiguracaoMapa configuracaoMapa;
	private ConfiguracaoDescarga configuracaoDescarga;
	private ConfiguracaoExportaImagens configuracaoExportaImagens;
	private ConfiguracaoExportaTrafego configuracaoExportaTrafego;
	
	/**
	 * Constrói uma configuração.
	 * @throws ConfiguracaoException
	 */
	public ConfiguracaoXML(String arquivoConf) throws ConfiguracaoException {
		
		try {
			XMLConfiguration config = new XMLConfiguration();
			FileHandler fh = new FileHandler(config);
			fh.load(new File(arquivoConf));
			
			carregarConfiguracoesArmazenamento(config);
			carregarConfiguracoesProcessamento(config);
			carregarConfiguracoesGrupos(config);
			carregarConfiguracoesRemessa(config);
			carregarConfiguracoesEmail(config);
			carregarConfiguracoesWS(config);
			carregarConfiguracoesChaveValor(config);
			carregarConfiguracoesMapa(config);
			carregarConfiguracoesDescarga(config);
			carregarConfiguracoesExportaImagens(config);
			carregarConfiguracoesExportaTrafego(config);
			
		} catch (ConfigurationException err) {
			err.printStackTrace();
			throw new ConfiguracaoException("Não foi possível ler o arquivo de configuração!\n" + err.getMessage(), err);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void carregarConfiguracoesDescarga(XMLConfiguration config) throws ConfiguracaoException {
		
		String tmpString;
		Integer tmpInteger;
		Long tmpLong;
		Boolean tmpBoolean;
		
		configuracaoDescarga = new ConfiguracaoDescarga();

		tmpBoolean = config.getBoolean("descarga.ativo", Boolean.FALSE);
		configuracaoDescarga.setAtivo(tmpBoolean);

		tmpString = config.getString("descarga.diretorio_saida");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'descarga.diretorio_saida' não encontrado.");
		configuracaoDescarga.setDiretorioSaida(tmpString);
		
		tmpString = config.getString("descarga.app_jar");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'descarga.app_jar' não encontrado.");
		configuracaoDescarga.setAppJar(tmpString);
		
		try {
			tmpInteger = config.getInteger("descarga.numero_maximo_dias_final_descarga", 0);
			configuracaoDescarga.setNumeroMaximoDiasFinalDescarga(tmpInteger);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'descarga.numero_maximo_dias_final_descarga' não encontrado.");			
		}

		try {
			tmpLong = config.getLong("descarga.tamanho_midia", 0);
			configuracaoDescarga.setTamanhoMidia(tmpLong);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'descarga.numero_maximo_dias_final_descarga' não encontrado.");			
		}

		String horarioJobExportacao = config.getString("descarga.horario_job_exportacao");
		if (horarioJobExportacao != null && !ExpValida.HORA_MINUTO.validar(horarioJobExportacao))
			throw new ConfiguracaoException("Parâmetro 'descarga.horario_job_exportacao' está em um formato inválido.");
		configuracaoDescarga.setHorarioJobExportacao(horarioJobExportacao);
		
		List<?> listaArquivos = config.getList("descarga.arquivos_externos.arquivo");
		configuracaoDescarga.setArquivosExternos((List<String>) listaArquivos);
	}

	/**
	 * Carrega as configurações relacionadas ao mapa.
	 * @param config
	 * @throws ConfiguracaoException
	 */
	private void carregarConfiguracoesMapa(XMLConfiguration config) throws ConfiguracaoException {
		String tmpString;
		Double tmpDouble;
		configuracaoMapa = new ConfiguracaoMapa();
		
		tmpString = config.getString("mapa.centro.latitude");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'mapa.centro.latitude' não encontrado.");
		try {
			tmpDouble = Double.parseDouble(tmpString);
		} catch (NumberFormatException nfe) {
			throw new ConfiguracaoException("Parâmetro 'mapa.centro.latitude' inválido: "+nfe.getLocalizedMessage());
		}
		configuracaoMapa.setLatitudeCentro(tmpDouble);
		
		tmpString = config.getString("mapa.centro.longitude");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'mapa.centro.longitude' não encontrado.");
		try {
			tmpDouble = Double.parseDouble(tmpString);
		} catch (NumberFormatException nfe) {
			throw new ConfiguracaoException("Parâmetro 'mapa.centro.longitude' inválido: "+nfe.getLocalizedMessage());
		}
		configuracaoMapa.setLongitudeCentro(tmpDouble);
		
		tmpString = config.getString("mapa.google_maps_key");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'mapa.google_maps_key' não encontrado.");
		configuracaoMapa.setGoogleMapsKey(tmpString);
	}
	

	/**
	 * Carrega as configurações relacionadas ao conjunto de chave/valor.
	 * @param config
	 * @throws ConfiguracaoException
	 */
	private void carregarConfiguracoesChaveValor(XMLConfiguration config) throws ConfiguracaoException {

		configuracaoChaveValor = new ConfiguracaoChaveValor();
		
		List<?> listaChaves = config.getList("config_chave_valor.chave_valor.[@chave]");
		List<?> listaValores = config.getList("config_chave_valor.chave_valor.[@valor]");
		
		for (int i = 0; i < listaChaves.size(); i++) {
			configuracaoChaveValor.put(listaChaves.get(i).toString(), listaValores.get(i).toString());
		}		

		if (!configuracaoChaveValor.containsKey("nome_contrato"))
			throw new ConfiguracaoException("Parâmetro requerido 'config_chave_valor.nome_contrato' não encontrado.");
		
		if (!configuracaoChaveValor.containsKey("grupo_equipamento"))
			throw new ConfiguracaoException("Parâmetro requerido 'config_chave_valor.grupo_equipamento' não encontrado.");
		
		configuracaoEquipamento = new ConfiguracaoEquipamento();
		configuracaoEquipamento.setNomeContrato(configuracaoChaveValor.get("nome_contrato"));
		configuracaoEquipamento.setIdentificacaoCliente(configuracaoChaveValor.get("identificacao_cliente"));
		
		try {
			configuracaoEquipamento.setIdGrupo(Integer.parseInt(configuracaoChaveValor.get("grupo_equipamento")));
		} catch (NumberFormatException  ex)
		{
			throw new ConfiguracaoException("Parâmetro requerido 'equipamento.grupo' não é um número inteiro.", ex);
		}
			
	}

	/**
	 * Carrega as configurações relacionadas ao armazenamento.
	 * @param config
	 * @throws ConfiguracaoException
	 */
	private void carregarConfiguracoesArmazenamento(XMLConfiguration config) throws ConfiguracaoException {
		
		String tmpString = null;
		ConfiguracaoServidorSql configuracaoServidorSql = new ConfiguracaoServidorSql();
		configuracaoArmazenamento = new ConfiguracaoArmazenamento();
		
		tmpString = config.getString("armazenamento.servidor_sql.host");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'armazenamento.servidor_sql.host' não encontrado.");
		configuracaoServidorSql.setHost(tmpString);

		tmpString = config.getString("armazenamento.servidor_sql.database");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'armazenamento.servidor_sql.database' não encontrado.");			
		configuracaoServidorSql.setDatabase(tmpString);	
		
		tmpString = config.getString("armazenamento.servidor_sql.user");
//		if (tmpString == null)
//			throw new ConfiguracaoException("Parâmetro requerido 'armazenamento.servidor_sql.user' não encontrado.");			
		configuracaoServidorSql.setUser(tmpString);
		
		tmpString = config.getString("armazenamento.servidor_sql.password");
//		if (tmpString == null)
//			throw new ConfiguracaoException("Parâmetro requerido 'armazenamento.servidor_sql.password' não encontrado.");			
		configuracaoServidorSql.setPassword(tmpString);

		tmpString = config.getString("armazenamento.importador_txt.diretorio");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'armazenamento.importador_txt.diretorio' não encontrado.");			

		configuracaoArmazenamento.setConfiguracaoServidorSql(configuracaoServidorSql);
		configuracaoArmazenamento.setDiretorioImportadorTxt(tmpString);
		
	}
	
	/**
	 * Carrega as configurações relacionadas ao processamento.
	 * @param config
	 * @throws ConfiguracaoException
	 */
	private void carregarConfiguracoesProcessamento(XMLConfiguration config) throws ConfiguracaoException {
		
		Boolean tmpBoolean = null;
		Integer tmpInteger = null;
		configuracaoProcessamento = new ConfiguracaoProcessamento();
		
		config.setThrowExceptionOnMissing(true);
		try {
			tmpBoolean = config.getBoolean("processamento.com_sugestao_placa");
			configuracaoProcessamento.setComSugestaoPlaca(tmpBoolean);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.com_sugestao_placa' não encontrado.");			
		}

		try {
			tmpBoolean = config.getBoolean("processamento.com_obliteracao");
			configuracaoProcessamento.setComObliteracao(tmpBoolean);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.com_obliteracao' não encontrado.");			
		}
		
		try {
			tmpBoolean = config.getBoolean("processamento.com_ajuste_imagem");
			configuracaoProcessamento.setComAjustaImagem(tmpBoolean);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.com_ajuste_imagem' não encontrado.");			
		}

		try {
			tmpBoolean = config.getBoolean("processamento.com_marca_processo", Boolean.FALSE);
			configuracaoProcessamento.setComMarcaProcesso(tmpBoolean);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.com_marca_processo' não encontrado.");			
		}		
		
		try {
			tmpBoolean = config.getBoolean("processamento.com_especie_processo", Boolean.FALSE);
			configuracaoProcessamento.setComEspecieProcesso(tmpBoolean);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.com_especie_processo' não encontrado.");			
		}
		
		try {
			tmpBoolean = config.getBoolean("processamento.com_uf_validacao", Boolean.FALSE);
			configuracaoProcessamento.setComUfValidacao(tmpBoolean);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.com_uf_validacao' não encontrado.");			
		}

		try {
			tmpBoolean = config.getBoolean("processamento.com_validacao_consistentes_agendamento", Boolean.FALSE);
			configuracaoProcessamento.setComValidacaoConsistentesAgendamento(tmpBoolean);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.com_validacao_consistentes_agendamento.");			
		}		
		
		try {
			tmpBoolean = config.getBoolean("processamento.confirma_obliteracao", Boolean.FALSE);
			configuracaoProcessamento.setConfirmaObliteracao(tmpBoolean);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.confirma_obliteracao' não encontrado.");			
		}	
		
		try {
			tmpInteger = config.getInteger("processamento.alerta_velocidade", 0);
			configuracaoProcessamento.setAlertaVelocidade(tmpInteger);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.alerta_velocidade' não encontrado.");			
		}	
		
		try {
			tmpBoolean = config.getBoolean("processamento.alerta_isento", Boolean.TRUE);
			configuracaoProcessamento.setAlertaIsento(tmpBoolean);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.alerta_isento' não encontrado.");			
		}	

		try {
			tmpInteger = config.getInteger("processamento.tamanho_tarja", 0);
			configuracaoProcessamento.setTamanhoTarja(tmpInteger);
		} catch (NoSuchElementException ex) {
			throw new ConfiguracaoException("Parâmetro requerido 'processamento.tamanho_tarja' não encontrado.");			
		}		
		
	}
	
	/**
	 * Carrega as configurações relacionadas à Remessa.
	 * @param config
	 * @throws ConfiguracaoException
	 */
	@SuppressWarnings("unchecked")
	private void carregarConfiguracoesRemessa(XMLConfiguration config) throws ConfiguracaoException {
	
		String tmpString = null;
		Class<? extends RemessaFactory> implementacaoCriacao = null;
		Class<? extends ExportaRemessa<?>> implementacaoExportacao = null;
		configuracaoRemessa = new ConfiguracaoRemessa();

		List<?> listaDescRemessa = config.getList("remessa.tipos.tipo.[@descricao]");
		List<?> listaCodigoRemessa = config.getList("remessa.tipos.tipo.[@codigo]");
		List<?> listaIdProcessos = config.getList("remessa.tipos.tipo.[@id_processo]");
		List<?> listaAutoInicial = config.getList("remessa.tipos.tipo.[@auto_inicial]");
		List<?> listaAutoFinal = config.getList("remessa.tipos.tipo.[@auto_final]");
		List<?> listaSerieInicial = config.getList("remessa.tipos.tipo.[@serie_inicial]");
		List<?> listaSerieFinal = config.getList("remessa.tipos.tipo.[@serie_final]");
		List<?> listaSiglaCliente = config.getList("remessa.tipos.tipo.[@sigla_cliente]");
		List<?> listaModeloAIT = config.getList("remessa.tipos.tipo.[@modelo_ait]");
		
		
		if (listaDescRemessa == null || listaDescRemessa.size() == 0)
			throw new ConfiguracaoException("Configuração com erro. Para as remessas," +
			"não foi especificado nenhuma descrição.");			

		if (listaCodigoRemessa == null || listaCodigoRemessa.size() == 0)
			throw new ConfiguracaoException("Configuração com erro. Para as remessas," +
			"não foi especificado nenhum codigo.");			
		
		String codigoRemessa;
		String descRemessa;
		Integer idProcesso;
		Integer autoInicial;
		Integer autoFinal;
		String serieInicial;
		String serieFinal;
		String modeloAIT;
		String siglaCliente;
		Map<String, TipoRemessa> mapaTiposRemessa = new HashMap<String, TipoRemessa>();
		
		for (int i = 0; i < listaDescRemessa.size(); i++) {
			descRemessa = listaDescRemessa.get(i).toString();
			codigoRemessa = listaCodigoRemessa.get(i).toString();
			idProcesso = Integer.parseInt(listaIdProcessos.get(i).toString());
			autoInicial = Integer.parseInt(listaAutoInicial.get(i).toString());
			autoFinal = Integer.parseInt(listaAutoFinal.get(i).toString());
			serieInicial = listaSerieInicial.get(i).toString();
			serieFinal = listaSerieFinal.get(i).toString();
			modeloAIT = listaModeloAIT.get(i).toString();
			siglaCliente = listaSiglaCliente.get(i).toString();
			mapaTiposRemessa.put(descRemessa, 
					new TipoRemessa(
							codigoRemessa, 
							descRemessa,
							idProcesso,
							autoInicial,
							autoFinal,
							serieInicial,
							serieFinal,
							modeloAIT,
							siglaCliente
					)
			);
		}
		configuracaoRemessa.setMapaTipos(mapaTiposRemessa);
		
		tmpString = config.getString("remessa.exportador.layout_prem");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'remessa.exportador.layout_prem' não encontrado.");
		configuracaoRemessa.setLayoutPrem(tmpString);
		
		tmpString = config.getString("remessa.exportador.logotipo_empresa");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'remessa.exportador.logotipo_empresa' não encontrado.");
		configuracaoRemessa.setLogotipoEmpresa(tmpString);
		
		tmpString = config.getString("remessa.exportador.logotipo_empresa_vertical");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'remessa.exportador.logotipo_empresa_vertical' não encontrado.");
		configuracaoRemessa.setLogotipoEmpresaVertical(tmpString);
		
		// Carrega a implementação responsável pela Criação/Geração da remessa
		tmpString = config.getString("remessa.gerador");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'remessa.gerador' não encontrado.");
		
		try {
			implementacaoCriacao = (Class<? extends RemessaFactory>)
				Class.forName(tmpString).asSubclass(RemessaFactory.class);
		} catch (ClassNotFoundException e) {
			throw new ConfiguracaoException("Configuração com erro. Não foi possível localizar a classe [" +
					tmpString + " definida como criação de remessa.");
		}
		configuracaoRemessa.setImplementacaoGeraRemessa(implementacaoCriacao);			
		
		// Carrega a implementação responsável pela Exportação da remessa
		tmpString = config.getString("remessa.exportador.implementacao");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'remessa.exportador.implementacao' não encontrado.");
		
		try {
			implementacaoExportacao = (Class<? extends ExportaRemessa<?>>)
				Class.forName(tmpString).asSubclass(ExportaRemessa.class);
		} catch (ClassNotFoundException e) {
			throw new ConfiguracaoException("Configuração com erro. Não foi possível localizar a classe [" +
					tmpString + " definida como exportação de remessa.");
		}
		configuracaoRemessa.setImplementacaoExportaRemessa(implementacaoExportacao);
		
	}
	
	
	/**
	 * Carrega as configurações relacionadas à Exportação de Imagens.
	 * @param config
	 * @throws ConfiguracaoException
	 */
	private void carregarConfiguracoesExportaImagens(XMLConfiguration config) throws ConfiguracaoException {
		String tmpString = null;
		configuracaoExportaImagens = new ConfiguracaoExportaImagens();
		
		tmpString = config.getString("exporta_imagens.ativo");
		if (tmpString == null || !(tmpString.equals("true") || tmpString.equals("false")))
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_imagens.ativo' não encontrado.");
		configuracaoExportaImagens.setAtivo(tmpString.equals("true"));
		
		tmpString = config.getString("exporta_imagens.comando_logon");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_imagens.comando_logon' não encontrado.");
		configuracaoExportaImagens.setCmdLogon(tmpString);
		
		tmpString = config.getString("exporta_imagens.diretorio");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_imagens.diretorio' não encontrado.");
		configuracaoExportaImagens.setDiretorio(tmpString);
		
		tmpString = config.getString("exporta_imagens.comando_logoff");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_imagens.comando_logoff' não encontrado.");
		configuracaoExportaImagens.setCmdLogoff(tmpString);
		
		tmpString = config.getString("exporta_imagens.horario_job");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_imagens.horario_job' não encontrado.");
		configuracaoExportaImagens.setHorarioJob(tmpString);
		
		tmpString = config.getString("exporta_imagens.dias_retroativo_minimo");
		if (tmpString == null || !ExpValida.NATURAL_COM_ZERO.validar(tmpString))
			throw new ConfiguracaoException("Parâmetro requerido 'dias_retroativo_minimo' não encontrado.");
		configuracaoExportaImagens.setDiasRetroativoMinimo(Integer.valueOf(tmpString));
		
		tmpString = config.getString("exporta_imagens.dias_retroativo_maximo");
		if (tmpString == null || !ExpValida.NATURAL_COM_ZERO.validar(tmpString))
			throw new ConfiguracaoException("Parâmetro requerido 'dias_retroativo_maximo' não encontrado.");
		configuracaoExportaImagens.setDiasRetroativoMaximo(Integer.valueOf(tmpString));
	}

	
	/**
	 * Carrega as configurações relacionadas à Exportação de Tráfego.
	 * @param config
	 * @throws ConfiguracaoException
	 */
	private void carregarConfiguracoesExportaTrafego(XMLConfiguration config) throws ConfiguracaoException {
		String tmpString = null;
		configuracaoExportaTrafego = new ConfiguracaoExportaTrafego();
		
		tmpString = config.getString("exporta_trafego.ativo");
		if (tmpString == null || !(tmpString.equals("true") || tmpString.equals("false")))
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_trafego.ativo' não encontrado.");
		configuracaoExportaTrafego.setAtivo(tmpString.equals("true"));
		
		tmpString = config.getString("exporta_trafego.comando_logon");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_trafego.comando_logon' não encontrado.");
		configuracaoExportaTrafego.setCmdLogon(tmpString);
		
		tmpString = config.getString("exporta_trafego.diretorio");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_trafego.diretorio' não encontrado.");
		configuracaoExportaTrafego.setDiretorio(tmpString);
		
		tmpString = config.getString("exporta_trafego.comando_logoff");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_trafego.comando_logoff' não encontrado.");
		configuracaoExportaTrafego.setCmdLogoff(tmpString);
		
		tmpString = config.getString("exporta_trafego.codigo_empresa");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_trafego.codigo_empresa' não encontrado.");
		configuracaoExportaTrafego.setCodigoEmpresa(tmpString);
		
		tmpString = config.getString("exporta_trafego.codigo_tipo_equipamento");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_trafego.codigo_tipo_equipamento' não encontrado.");
		configuracaoExportaTrafego.setCodigoTipoEquipamento(tmpString);
		
		tmpString = config.getString("exporta_trafego.tipo_equipamento");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_trafego.tipo_equipamento' não encontrado.");
		configuracaoExportaTrafego.setTipoEquipamento(tmpString);
		
		tmpString = config.getString("exporta_trafego.horario_job");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'exporta_trafego.horario_job' não encontrado.");
		configuracaoExportaTrafego.setHorarioJob(tmpString);
		
		tmpString = config.getString("exporta_trafego.dias_retroativo_maximo");
		if (tmpString == null || !ExpValida.NATURAL_COM_ZERO.validar(tmpString))
			throw new ConfiguracaoException("Parâmetro requerido 'dias_retroativo_maximo' não encontrado.");
		configuracaoExportaTrafego.setDiasRetroativoMaximo(Integer.valueOf(tmpString));
	}
	
	/**
	 * Carrega as configurações relacionadas aos servidores de email.
	 * @param config
	 * @throws ConfiguracaoException
	 */
	private void carregarConfiguracoesEmail(XMLConfiguration config) throws ConfiguracaoException {
		
		ConfiguracaoServidorSmtp servidorSmtp;
		listaServidoresSmtp = new ArrayList<ConfiguracaoServidorSmtp>();
		
		List<?> listaHost = config.getList("email.smtp_servers.smtp_server.host");
		List<?> listaUser = config.getList("email.smtp_servers.smtp_server.user");
		List<?> listaPassword = config.getList("email.smtp_servers.smtp_server.password");
		List<?> listaMail = config.getList("email.smtp_servers.smtp_server.mail");
		List<?> listaPort = config.getList("email.smtp_servers.smtp_server.port");

		if (listaHost == null || listaUser.size() == 0)
			throw new ConfiguracaoException("Configuração com erro. Para os servidores smtp," +
			" não foi especificado nenhum host.");

		if (listaUser == null || listaUser.size() == 0)
			throw new ConfiguracaoException("Configuração com erro. Para os servidores smtp," +
			" não foi especificado nenhum usuário.");

		if (listaPassword == null || listaPassword.size() == 0)
			throw new ConfiguracaoException("Configuração com erro. Para os servidores smtp," +
			" não foi especificado nenhuma senha.");

		if (listaMail == null || listaMail.size() == 0)
			throw new ConfiguracaoException("Configuração com erro. Para os servidores smtp," +
			" não foi especificado nenhum email.");
		
		if (listaPort == null || listaPort.size() == 0)
			throw new ConfiguracaoException("Configuração com erro. Para os servidores smtp," +
			" não foi especificado nenhuma porta.");

		for (int i = 0; i < listaHost.size(); i++) {
			
			servidorSmtp = new ConfiguracaoServidorSmtp();
			servidorSmtp.setHost(listaHost.get(i).toString());
			servidorSmtp.setUser(listaUser.get(i).toString());
			servidorSmtp.setPassword(listaPassword.get(i).toString());
			servidorSmtp.setMail(listaMail.get(i).toString());
			servidorSmtp.setPort(Integer.parseInt(listaPort.get(i).toString()));
			
			listaServidoresSmtp.add(servidorSmtp);
		}
		
		String tmpString = config.getString("email.assunto_veiculo_monitorado");
		if (tmpString == null)
			throw new ConfiguracaoException("Parâmetro requerido 'email.assunto_veiculo_monitorado' não encontrado.");
		assuntoEmailVeiculoMonitorado = tmpString;
		
	}
	
	/**
	 * Carrega as configurações relacionadas aos WS.
	 * @param config
	 * @throws ConfiguracaoException
	 */
	private void carregarConfiguracoesWS(XMLConfiguration config) throws ConfiguracaoException {
		
		ConfiguracaoWS ws;
		
		List<?> listaNomeInterface = config.getList("webservice.wss.ws.nome_interface");
		List<?> listaEndereco = config.getList("webservice.wss.ws.endereco");

		if (listaNomeInterface == null || listaNomeInterface.size() == 0)
			throw new ConfiguracaoException("Configuração com erro. Para os WS Address," +
			" não foi especificado nenhum nome de interface.");

		if (listaEndereco == null || listaEndereco.size() == 0)
			throw new ConfiguracaoException("Configuração com erro. Para os WS Address," +
			" não foi especificado nenhum endereco.");



		for (int i = 0; i < listaNomeInterface.size(); i++) {
			
			ws = new ConfiguracaoWS();
			ws.setNomeInterface(listaNomeInterface.get(i).toString());
			ws.setEndereco(listaEndereco.get(i).toString());
			
			mapWS.put(ws.getNomeInterface(), ws);
		}
	}
	/**
	 * Carrega as configurações relacionadas aos grupos de usuários do sistema.
	 * @param config
	 * @throws ConfiguracaoException
	 */
	private void carregarConfiguracoesGrupos(XMLConfiguration config) throws ConfiguracaoException {
		
		configuracaoGrupos = new ConfiguracaoGrupos();
		
		List<?> listaCodigos = config.getList("grupos.grupo.[@codigo]");
		List<?> listaIds = config.getList("grupos.grupo.[@id]");
		int currId;
		String currCodigo;
		
		for (int i = 0; i < listaCodigos.size(); i++) {
			
			currId = Integer.parseInt(listaIds.get(i).toString());
			currCodigo = listaCodigos.get(i).toString();
			
			if (TipoGrupo.DIGITADORES.getCodigo().equalsIgnoreCase(currCodigo))
				configuracaoGrupos.setIdGrupoDigitadores(currId);
			else if (TipoGrupo.AUDITORES.getCodigo().equalsIgnoreCase(currCodigo))
				configuracaoGrupos.setIdGrupoAuditores(currId);
			else if (TipoGrupo.TECNICOS.getCodigo().equalsIgnoreCase(currCodigo))
				configuracaoGrupos.setIdGrupoTecnicos(currId);
			else if (TipoGrupo.EDITAR_EQUIPAMENTO.getCodigo().equalsIgnoreCase(currCodigo))
				configuracaoGrupos.setIdGrupoEditarConfigEquip(currId);
			else if (TipoGrupo.NOVO_EQUIPAMENTO.getCodigo().equalsIgnoreCase(currCodigo))
				configuracaoGrupos.setIdGrupoNovoEquipamento(currId);
			else if (TipoGrupo.DESENVOLVEDORES.getCodigo().equalsIgnoreCase(currCodigo))
				configuracaoGrupos.setIdGrupoDesenvolvedores(currId);
			else if (TipoGrupo.GERENTE_AUDITORES.getCodigo().equalsIgnoreCase(currCodigo))
				configuracaoGrupos.setIdGrupoGerenteAuditores(currId);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getConfiguracaoArmazenamento()
	 */
	public ConfiguracaoArmazenamento getConfiguracaoArmazenamento() {
		return configuracaoArmazenamento;
	}

	public ConfiguracaoRemessa getConfiguracaoRemessa() {
		return configuracaoRemessa;
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getListaServidoresSmtp()
	 */
	public List<ConfiguracaoServidorSmtp> getListaServidoresSmtp() {
		return listaServidoresSmtp;
	}
	
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getAssuntoEmailVeiculoMonitorado()
	 */
	public String getAssuntoEmailVeiculoMonitorado() {
		return assuntoEmailVeiculoMonitorado;
	}	
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.Configuracao#getMapWS()
	 */
	@Override
	public Map<String, ConfiguracaoWS> getMapWS() {
		return mapWS;
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getHost()
	 */
	
	public String getHost() {
		return configuracaoArmazenamento.getConfiguracaoServidorSql().getHost();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getDatabase()
	 */
	public String getDatabase() {
		return configuracaoArmazenamento.getConfiguracaoServidorSql().getDatabase();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getUser()
	 */
	public String getUser() {
		return configuracaoArmazenamento.getConfiguracaoServidorSql().getUser();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getPassword()
	 */
	public String getPassword() {
		return configuracaoArmazenamento.getConfiguracaoServidorSql().getPassword();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getComSugestaoPlaca()
	 */
	public boolean getComSugestaoPlaca() {
		return configuracaoProcessamento.isComSugestaoPlaca();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getComObliteracao()
	 */
	public boolean getComObliteracao() {
		return configuracaoProcessamento.isComObliteracao();
	}
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getConfirmaObliteracao()
	 */
	public boolean getConfirmaObliteracao() {
		return configuracaoProcessamento.isConfirmaObliteracao();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getTamanhoTarja()
	 */
	public int getTamanhoTarja() {
		return configuracaoProcessamento.getTamanhoTarja();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getComMarcaProcesso()
	 */
	public boolean getComMarcaProcesso() {
		return configuracaoProcessamento.isComMarcaProcesso();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getComEspecieProcesso()
	 */
	public boolean getComEspecieProcesso() {
		return configuracaoProcessamento.isComEspecieProcesso();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getComUfValidacao()
	 */
	public boolean getComUfValidacao() {
		return configuracaoProcessamento.isComUfValidacao();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getIdGrupoTecnico()
	 */
	public Integer getIdGrupoTecnico() {
		return configuracaoGrupos.getIdGrupoTecnicos();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getIdGrupoPermissaoEditarConfigEquip()
	 */
	public int getIdGrupoPermissaoEditarConfigEquip() {
		return configuracaoGrupos.getIdGrupoEditarConfigEquip();
	}

	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getIdGrupoPermissaoNovoEquip()
	 */
	public int getIdGrupoPermissaoNovoEquip() {
		return configuracaoGrupos.getIdGrupoNovoEquipamento();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getIdGrupoEquipamento()
	 */
	public int getIdGrupoEquipamento() {
		return configuracaoEquipamento.getIdGrupo();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getIdGrupoDigitadores()
	 */
	public int getIdGrupoDigitadores(){
		return configuracaoGrupos.getIdGrupoDigitadores();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getIdGrupoAuditores()
	 */
	public int getIdGrupoAuditores(){
		return configuracaoGrupos.getIdGrupoAuditores();
	}	
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.Configuracao#getIdGrupoPermissaoDesenvolvedores()
	 */
	@Override
	public int getIdGrupoDesenvolvedores() {
		return configuracaoGrupos.getIdGrupoDesenvolvedores();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.Configuracao#getIdGrupoGerenteAuditores()
	 */
	@Override
	public int getIdGrupoGerenteAuditores() {
		return configuracaoGrupos.getIdGrupoGerenteAuditores();
	}
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getAlertaVelocidade()
	 */
	public int getAlertaVelocidade() {
		return configuracaoProcessamento.getAlertaVelocidade();
	}

	public boolean getAlertaIsento() {
		return configuracaoProcessamento.getAlertaIsento();
	}

	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getLayoutPREM()
	 */
	public String getLayoutPREM() {
		return configuracaoRemessa.getLayoutPrem();
	}

	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getTipoRemessaVELOCIDADE()
	 */
	public String getTipoRemessaVELOCIDADE() {
		return configuracaoRemessa.getMapaTipos().get("Velocidade").getCodigo();
	}

	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getTipoRemessaRODIZIO()
	 */
	public String getTipoRemessaRODIZIO() {
		return configuracaoRemessa.getMapaTipos().get("Rodizio").getCodigo();
	}

	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getTipoRemessaZMRC()
	 */
	public String getTipoRemessaZMRC() {
		return configuracaoRemessa.getMapaTipos().get("ZMRC").getCodigo();
	}

	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getTipoRemessaZMRF()
	 */
	public String getTipoRemessaZMRF() {
		return configuracaoRemessa.getMapaTipos().get("ZMRF").getCodigo();
	}

	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getTipoRemessaRetornoConversao()
	 */
	public String getTipoRemessaRetorno() {
		return configuracaoRemessa.getMapaTipos().get("Retorno Proibido").getCodigo();
	}

	public String getTipoRemessaConversao() {
		return configuracaoRemessa.getMapaTipos().get("Conversão Proibida").getCodigo();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getTipoRemessaAvancoSinal()
	 */
	public String getTipoRemessaAvancoSinal() {
		return configuracaoRemessa.getMapaTipos().get("Avanço Sinal").getCodigo();
	}

	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getTipoRemessaParadaFaixa()
	 */
	public String getTipoRemessaParadaFaixa() {
		return configuracaoRemessa.getMapaTipos().get("Parada Faixa").getCodigo();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.Configuracao#getTipoRemessaFaixaExclusiva()
	 */
	public String getTipoRemessaFaixaExclusiva() {
		return configuracaoRemessa.getMapaTipos().get("Faixa Exclusiva").getCodigo();
	}

	public String getTipoRemessaNaoConservarFaixa() {
		return configuracaoRemessa.getMapaTipos().get("Faixa Proibida").getCodigo();
	}
	
	public String getTipoRemessaGeral() {
		return configuracaoRemessa.getMapaTipos().get("Geral").getCodigo();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getLogoEmpresa()
	 */
	public String getLogoEmpresa() {
		return configuracaoRemessa.getLogotipoEmpresa();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getLogoEmpresaVertical()
	 */
	public String getLogoEmpresaVertical() {
		return configuracaoRemessa.getLogotipoEmpresaVertical();
	}

	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getImpTxtDir()
	 */
	public String getImpTxtDir(){
		return configuracaoArmazenamento.getDiretorioImportadorTxt();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.ConfiguracaoProvider#getImplementacaoExportaRemesssa()
	 */
	public Class<? extends ExportaRemessa<?>> getImplementacaoExportaRemesssa() throws ConfiguracaoException
	{
		return configuracaoRemessa.getImplementacaoExportaRemessa();
	}
	
	/* (non-Javadoc)
	 * @see com.consilux.conf.Configuracao#getConfiguracaoDescarga()
	 */
	public ConfiguracaoDescarga getConfiguracaoDescarga() {
		return configuracaoDescarga;
	}
	
	public Class<? extends RemessaFactory> getImplementacaoGeraRemesssa() throws ConfiguracaoException
	{
		return configuracaoRemessa.getImplementacaoGeraRemessa();
	}
	
	@Override
	public String getNomeContrato() throws ConfiguracaoException {
		return configuracaoEquipamento.getNomeContrato();
	}

	@Override
	public String getIdentificacaoCliente() throws ConfiguracaoException {
		return configuracaoEquipamento.getIdentificacaoCliente();
	}	
	
	@Override
	public ConfiguracaoChaveValor getConfiguracaoChaveValor() {
		return configuracaoChaveValor;
	}

	@Override
	public ConfiguracaoMapa getConfiguracaoMapa() {
		return configuracaoMapa;
	}
	
	/**
	 * Valor que define se na validação (processo 3) será permitido processar com agendamento
	 * (antigo processa-direto) para as infrações consistentes.
	 * @return true se o contrato permite validação agendada de consistentes.
	 * @throws ConfiguracaoException
	 */
	public boolean isComValidacaoConsistentesAgendamento() throws ConfiguracaoException {
		return configuracaoProcessamento.isComValidacaoConsistentesAgendamento();
	}

	@Override
	public ConfiguracaoExportaImagens getConfiguracaoExportaImagens() {
		return configuracaoExportaImagens;
	}


	@Override
	public boolean getComAjusteImagem() {
		return configuracaoProcessamento.isComAjustaImagem();
	}
	
	@Override
	public ConfiguracaoExportaTrafego getConfiguracaoExportaTrafego() {
		// TODO Auto-generated method stub
		return configuracaoExportaTrafego;
	}
}



