package com.consilux.conf;

import java.util.List;
import java.util.Map;

import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.model.ExportaRemessa;
import com.consilux.model.remessa.RemessaFactory;

/**
 * Interface padrão que possui as características de configuração
 * do sistema. 
 * @author raoni
 */
public interface Configuracao {

	public static final String ARQ_CONF = "/WEB-INF/confGTW.xml";
	public static final String ARQ_CONF_APP = "/META-INF/confAPP.xml";
	
	public static final String ARQ_CONF_TESTES = "/confGTW-teste.xml";
	
	public abstract ConfiguracaoArmazenamento getConfiguracaoArmazenamento();
	public abstract List<ConfiguracaoServidorSmtp> getListaServidoresSmtp();
	public abstract Map<String, ConfiguracaoWS> getMapWS();
	public abstract String getAssuntoEmailVeiculoMonitorado();
	public abstract ConfiguracaoRemessa getConfiguracaoRemessa();
	public abstract ConfiguracaoChaveValor getConfiguracaoChaveValor(); 
	public abstract ConfiguracaoMapa getConfiguracaoMapa();
	public abstract ConfiguracaoDescarga getConfiguracaoDescarga();
	public abstract ConfiguracaoExportaImagens getConfiguracaoExportaImagens();
	public abstract ConfiguracaoExportaTrafego getConfiguracaoExportaTrafego();
	
	/**
	 * Retorna o host do BD.
	 * @return Host do BD
	 */

	public abstract String getHost();

	/**
	 * Retorna o database do BD.
	 * @return Database do BD
	 */
	public abstract String getDatabase();

	/**
	 * Retorna o usuário do BD
	 * @return Usuário do BD
	 */
	public abstract String getUser();

	/**
	 * Retorna a senha do BD	
	 * @return Senha do BD
	 */
	public abstract String getPassword();

	/**
	 * Retorna se o sistema irá trabalhar com segestão de placa ou não.	
	 * @return true se trabalha com segestão de placa, false se não.
	 */
	public abstract boolean getComSugestaoPlaca();

	/**
	 * Retorna se o sistema irá trabalhar com obliteração ou não.	
	 * @return true se trabalha com obliteração, false se não.
	 */
	public abstract boolean getComObliteracao();
	
	/**
	 * Retorna se o sistema irá trabalhar com ajuste de brilho e contraste.	
	 * @return true se trabalha com obliteração, false se não.
	 */
	public abstract boolean getComAjusteImagem();
	
	/**
	 * Retorna se o sistema irá confirmar a obliteração ou não.	
	 * @return true se confirma a obliteração, false se não.
	 */
	public abstract boolean getConfirmaObliteracao();

	/**
	 * Retorna o tamanho da tarja para o sistema controlar a obliteração.	
	 * @return O tamanho em pixels da tarja em relação a imagem.
	 */
	public abstract int getTamanhoTarja();

	/**
	 * Retorna se o sistema é necessário preencher a marca durante o processo.	
	 * @return true se trabalha com marca no processo, false se não.
	 */
	public abstract boolean getComMarcaProcesso();

	/**
	 * Retorna se o sistema é necessário preencher a espécie durante o processo.	
	 * @return true se trabalha com espécie no processo, false se não.
	 */
	public abstract boolean getComEspecieProcesso();

	/**
	 * Retorna se o sistema é necessário preencher a uf durante a validação caso não encontre o cadastro.	
	 * @return true se trabalha com uf na validação, false se não.
	 */
	public abstract boolean getComUfValidacao();

	/**
	 * Retorna o id do grupo que define os técnicos.
	 * @return O id do grupo que reune os técnicos.
	 */
	public abstract Integer getIdGrupoTecnico();

	/**
	 * Retorna o ID do grupo que tem permissão em editar as configurações dos equipamentos
	 * @return Retorna o ID do grupo
	 */
	public abstract int getIdGrupoPermissaoEditarConfigEquip();

	/**
	 * Retorna o ID do grupo que tem permissão de criar um novo equipamento
	 * @return
	 */
	public abstract int getIdGrupoPermissaoNovoEquip();

	/**
	 * Retorna o id do grupo equipamento.
	 * @return O id do grupo equipamento.
	 */
	public abstract int getIdGrupoEquipamento();

	public abstract int getIdGrupoDigitadores();

	public abstract int getIdGrupoAuditores();

	/**
	 * Retorna o ID do grupo desenvolvedores
	 * @return
	 */
	public abstract int getIdGrupoDesenvolvedores();

	/**
	 * Retorna o ID do grupo de gerente de auditores
	 * @return
	 */
	public abstract int getIdGrupoGerenteAuditores();

	/**
	 * Retorna a velocidade que o sistema deve alertar no processamento.
	 * @return Velocidade limite para alerta.
	 */
	public abstract int getAlertaVelocidade();

	/**
	 * Retorna se o sistema deve verificar se a placa digitada é isenta.	
	 * @return true se deve verificar a placa no processo, false se não.
	 */
	public abstract boolean getAlertaIsento();

	/**
	 * Retorna o nome do layout de protocolo para remessa que deve ser utilizado no sistema.
	 * @return Nome do layout.
	 */
	public abstract String getLayoutPREM();

	/**
	 * Retorna o tipo de remesa a ser gerada para infrações de velocidade.
	 * @return Tipo da remessa.
	 */
	public abstract String getTipoRemessaVELOCIDADE();

	/**
	 * Retorna o tipo de remesa a ser gerada para infrações de rodízio.
	 * @return Tipo da remessa.
	 */
	public abstract String getTipoRemessaRODIZIO();

	/**
	 * Retorna o tipo de remesa a ser gerada para infrações de zona máxima de restrição de caminhão.
	 * @return Tipo da remessa.
	 */
	public abstract String getTipoRemessaZMRC();

	/**
	 * Retorna o tipo de remesa a ser gerada para infrações de zona máxima de restrição de fretados.
	 * @return Tipo da remessa.
	 */
	public abstract String getTipoRemessaZMRF();

	public abstract String getTipoRemessaConversao();
	
	public abstract String getTipoRemessaRetorno();

	public abstract String getTipoRemessaAvancoSinal();

	public abstract String getTipoRemessaParadaFaixa();

	public abstract String getTipoRemessaFaixaExclusiva();

	public abstract String getTipoRemessaNaoConservarFaixa();
	
	public abstract String getTipoRemessaGeral();
	
	/**
	 * Retorna nome do arquivo com o logo da empresa.
	 * @return Nome do arquivo.
	 */
	public abstract String getLogoEmpresa();

	/**
	 * Retorna nome do arquivo com o logo da empresa vertical.
	 * @return Nome do arquivo.
	 */
	public abstract String getLogoEmpresaVertical();

	/**
	 * Retorna nome do diretorio onde serao salvos os arquivos para o ImpTxt
	 * @return Nome do diretorio
	 */
	public abstract String getImpTxtDir();

	/**
	 * Recupera a classe que herda de ExportaRemessa que deve ser
	 * utilizada para a exportação de remessas.
	 * @return a classe que herda ExportaRemessa.
	 * @throws ConfiguracaoException
	 */
	public abstract Class<? extends ExportaRemessa<?>> getImplementacaoExportaRemesssa()
			throws ConfiguracaoException;

	/**
	 * Recupera a classe que herda de RemessaFactory que deve ser
	 * utilizada para a geração de remessas.
	 * @return a classe que herda RemessaFactory.
	 * @throws ConfiguracaoException
	 */
	public abstract Class<? extends RemessaFactory> getImplementacaoGeraRemesssa()
			throws ConfiguracaoException;	
	
	/**
	 * Recupera o nome do contrato.
	 * @return o nome do contrato
	 * @throws ConfiguracaoException
	 */
	public abstract String getNomeContrato() throws ConfiguracaoException;
	
	/**
	 * Recupera a identificação do cliente
	 * @return o nome do contrato
	 * @throws ConfiguracaoException
	 */
	public abstract String getIdentificacaoCliente() throws ConfiguracaoException;
	
	
	/**
	 * Valor que define se na validação (processo 3) será permitido processar com agendamento
	 * (antigo processa-direto) para as infrações consistentes.
	 * @return true se o contrato permite validação agendada de consistentes.
	 * @throws ConfiguracaoException
	 */
	public abstract boolean isComValidacaoConsistentesAgendamento() throws ConfiguracaoException;	
}