package com.consilux.model.descarga;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;

import com.consilux.conf.Configuracao;
import com.consilux.conf.ConfiguracaoProvider;
import com.consilux.conf.Versao;
import com.consilux.conf.exception.ConfiguracaoException;
import com.consilux.infra.Funcoes;
import com.consilux.infra.exception.ConexaoException;
import com.consilux.lib.CSXISOImageFileHandler;
import com.consilux.model.TipoRemessa;
import com.consilux.model.descarga.beans.DescargaBean;
import com.consilux.model.descarga.beans.ImagemComBlobDescargaBean;
import com.consilux.model.descarga.beans.ImagemDescargaBean;
import com.consilux.model.descarga.beans.VeiculoDescargaBean;
import com.consilux.model.descarga.repositorio.DescargaRepositorio;
import com.consilux.model.descarga.repositorio.FabricaRepositorio;
import com.consilux.model.descarga.repositorio.ImagemComBlobRepositorio;
import com.consilux.model.descarga.repositorio.InfracaoRepositorio;
import com.consilux.model.descarga.repositorio.RepositorioException;
import com.consilux.model.descarga.repositorio.VeiculoRepositorio;
import com.consilux.model.descarga.repositorio.jpa.DerbyOpenMode;
import com.consilux.model.descarga.repositorio.jpa.FabricaRepositorioDerby;
import com.consilux.model.exception.ModelException;

import de.tu_darmstadt.informatik.rbg.hatlak.iso9660.ConfigException;
import de.tu_darmstadt.informatik.rbg.hatlak.iso9660.ISO9660RootDirectory;
import de.tu_darmstadt.informatik.rbg.hatlak.iso9660.impl.CreateISO;
import de.tu_darmstadt.informatik.rbg.hatlak.iso9660.impl.ISO9660Config;
import de.tu_darmstadt.informatik.rbg.hatlak.joliet.impl.JolietConfig;
import de.tu_darmstadt.informatik.rbg.hatlak.rockridge.impl.RockRidgeConfig;
import de.tu_darmstadt.informatik.rbg.mhartle.sabre.HandlerException;
import de.tu_darmstadt.informatik.rbg.mhartle.sabre.StreamHandler;

/**
 * Classe de negócio responsável por exportar uma descarga.
 * @author raoni
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class ExportaDescarga implements Job {

	private static Logger logger = Logger.getLogger(ExportaDescarga.class);
	private Map<Integer,String> mapaMd5;
	
	/**
	 */
	public ExportaDescarga() {
	}
	/**
	 * Exporta uma descarga para uma ISO (para posterior gravação em CD/DVD)
	 * @throws RepositorioException
	 * @throws ModelException
	 * @throws ConfiguracaoException
	 * @throws IOException
	 * @throws ConfigException
	 * @throws HandlerException
	 */
	public void exportarDescarga(Integer idDescarga) throws RepositorioException, ModelException, ConfiguracaoException, IOException, ConfigException, HandlerException { 
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		// Fábrica de Origem (GTW - SQL SERVER)
		FabricaRepositorio fabSrc = null;
		DescargaRepositorio repDescargaSrc = null;
		ImagemComBlobRepositorio repImagemSrc = null;
		VeiculoRepositorio repVeiculoSrc = null;
		InfracaoRepositorio repInfracaoSrc = null;
		
		// Fábrica de Destino (DERBY)
		FabricaRepositorio fabDst = null;
		DescargaRepositorio repDescargaDst = null;
		
		try {

			logger.debug("Iniciando repositórios JPA...");
			// Abre o repositórios de Origem (do GTW, SQL SERVER)
			fabSrc = new FabricaRepositorioGtw();
			repDescargaSrc = fabSrc.obterRepositorioDescarga();
			repVeiculoSrc = fabSrc.obterRepositorioVeiculo();
			repImagemSrc = fabSrc.obterRepositorioImagemComBlob();
			repInfracaoSrc = fabSrc.obterRepositorioInfracao();
			Map<Integer,String> mapMD5 = new HashMap<Integer, String>();
			
			// Verifica se o GTW (SQL SERVER) possui a descarga em questão.
			if (!repDescargaSrc.possuiDescarga(idDescarga)) {
				throw new ModelException("Tentativa de exportar uma descarga não existente. id_descarga = [" + idDescarga + "]");				
			}
			
			logger.debug("Recuperando objetos do BD...");
			// Recupera o bean da descarga
			DescargaBean descargaBeanSrc = repDescargaSrc.buscarBeanPorId(idDescarga);
			
			if (descargaBeanSrc == null) {
				throw new ModelException("Não foi possível localizar descarga no GTW. id_descarga = [" + idDescarga + "]");
			}
			
			logger.debug("Iniciando contadores...");
			// Verifica as quantidades de veículos / imagens versus ao que está armazenado no bean da descarga
			Integer qtdVeiculos = repVeiculoSrc.contarBeansPorIdDescarga(idDescarga);
			Integer qtdImagens = repImagemSrc.contarBeansPorIdDescarga(idDescarga);
			Integer qtdInfracoes = repInfracaoSrc.contarBeansPorIdDescarga(idDescarga);

			if (qtdVeiculos.intValue() != descargaBeanSrc.getTotalVeiculos().intValue()) {
				throw new ModelException("Quantidade inválida de veículos na descarga.");
			}
			
			if (qtdImagens.intValue() != descargaBeanSrc.getTotalImagens().intValue()) {
				throw new ModelException("Quantidade inválida de imagens na descarga.");
			}
			
			if (qtdInfracoes.intValue() != descargaBeanSrc.getTotalInfracoes().intValue()) {
				throw new ModelException("Quantidade inválida de infrações na descarga.");
			}
			
			// Recupera as datas da descarga
			Date diaInicio = descargaBeanSrc.getDiaInicio();
			Date diaFim = descargaBeanSrc.getDiaFim();
			
			logger.info("Período da descarga: '"+df.format(diaInicio)+"' até '"+df.format(diaFim)+"'.");
			
			// Diretório onde vão ficar os arquivos de ISO.
			File isoDir = new File(ConfiguracaoProvider.getInstance().getConfiguracaoDescarga().getDiretorioSaida());
			if (isoDir.exists()) {
				logger.info("Diretorio final (saída de ISO) [" + isoDir.getPath() + "]");
			}
			else {
				throw new ModelException("Diretorio final (saída de ISO) [" + isoDir.getPath() + "] não existe.");
			}

			File targetIso = new File(isoDir.getPath() + File.separatorChar + Descarga.getIsoFileName(idDescarga, diaInicio, diaFim));

			if (targetIso.exists()) {
				throw new ModelException("Arquivo ISO '"+targetIso.getPath()+"' já existe!");
			}

			// Diretório de arquivos temporários (do sistema operacional)
			String tempDir = System.getProperty("java.io.tmpdir");
			
			// Diretório de construção de descargas
			File buildDir = new File(tempDir + File.separatorChar + "ROOT_DESCARGA"
					+ "_" + ConfiguracaoProvider.getInstance().getNomeContrato());
			logger.info("Diretorio de criação de descargas: " + buildDir.getPath());
			
			File blobsDir = new File(buildDir.getPath() + File.separatorChar + "imagens");
			File dadosDir = new File(buildDir.getPath() + File.separatorChar + "dados");
			
			// Se já existe o diretorio temporario, então limpe ele.
			if (buildDir.exists()) {
				logger.debug("Limpando diretório de criação de descargas: " + buildDir.getPath());
				if (!deleteDirectoryRecursively(buildDir)) {
					throw new ModelException("Não foi possível limpar o diretório de criação de descargas.");
				}
			}
			
			// Cria a árvore do diretório RAIZ
			logger.debug("Criando diretório RAIZ para exportação de descargas: " + buildDir.getPath());
			if (!buildDir.mkdirs())	{
				throw new ModelException("Não foi possível criar o diretório RAIZ de exportação de descargas.");
			}

			// Cria a árvore do diretório de BLOBs
			logger.debug("Criando diretório de imagens para exportação de imagens: " + blobsDir.getPath());
			if (!blobsDir.mkdirs()) {
				throw new ModelException("Não foi possível criar o diretório imagens de exportação de imagens.");
			}			
			
			
			// Cria o diretório 'dados'
			logger.debug("Criando diretório de imagens para exportação de dados: " + dadosDir.getPath());
			if (!dadosDir.mkdirs()) {
				throw new ModelException("Não foi possível criar o diretório dados para exportação de dados.");
			}			
			
			// Copia o JAR do APP (o aplicativo de visualização de descargas).
			// Também vai gerar um novo conf pro APP e injetar no JAR.
			String appJar = ConfiguracaoProvider.getInstance().getConfiguracaoDescarga().getAppJar();
			File arqSrc = new File(appJar);
			if (!arqSrc.exists()) {
				throw new ModelException("Não foi possível localizar o JAR do APP em [" + appJar + "]");
			}
			File arqDst = new File(buildDir.getPath() + File.separatorChar + arqSrc.getName());
			logger.info("Copiando JAR do APP: [" + arqSrc.getPath() + "] para [" + arqDst.getPath() + "]");
			
			try {
				transferAppJar(arqSrc, arqDst, makeAppConf());;
			}
			catch (IOException iox) {
				throw new ModelException("Erro ao copiar o JAR do APP de [" + arqSrc.getAbsolutePath() + "] para [" + arqDst.getAbsolutePath() + "]." , iox);
			}
			
			// Copia arquivos "externos" para a raiz Ex: o INF, icone, readme, PDF, etc.
			List<String> arquivoExternos = ConfiguracaoProvider.getInstance().getConfiguracaoDescarga().getArquivosExternos();
			for (String arq : arquivoExternos) {
				arqSrc = new File(arq);
				arqDst = new File(buildDir.getPath() + File.separatorChar + arqSrc.getName());
				logger.info("Copiando arquivo: [" + arqSrc.getPath() + "] para [" + arqDst.getPath() + "]");
				try {
					Funcoes.copyFile(arqSrc, arqDst);
				}
				catch (IOException iox) {
					throw new ModelException("Erro ao copiar arquivo ["+arqSrc.getPath()+"]externo para .", iox);
				}
			}
			
			// Cria o repositório de Destino (DERBY)
			File derbyDatabase = new File(dadosDir.getPath() + File.separatorChar + FabricaRepositorioDerby.DESCARGA_DERBY_FILE_NAME);
			fabDst = new FabricaRepositorioDerby(derbyDatabase, DerbyOpenMode.CREATE_NEW);
			repDescargaDst = fabDst.obterRepositorioDescarga();
			
			// Cria o bean da descarga (o bean de destino, do Derby)
			DescargaBean descargaBeanDst = new DescargaBean();
			descargaBeanDst.setId(descargaBeanSrc.getId());
			descargaBeanDst.setDataCriacao(descargaBeanSrc.getDataCriacao());
			descargaBeanDst.setTotalImagens(descargaBeanSrc.getTotalImagens());
			descargaBeanDst.setTotalInfracoes(descargaBeanSrc.getTotalInfracoes());
			descargaBeanDst.setTotalVeiculos(descargaBeanSrc.getTotalVeiculos());
			descargaBeanDst.setDiaInicio(descargaBeanSrc.getDiaInicio());
			descargaBeanDst.setDiaFim(descargaBeanSrc.getDiaFim());
			descargaBeanDst.setIdentificacaoCliente(descargaBeanSrc.getIdentificacaoCliente());
			
			// Escreve os blobs no diretorio da descarga
			long startTime = System.currentTimeMillis();
			long contador = 0;
			
			logger.info("Transferindo imagens para a descarga...");
			
			// Transfere os blobs (do GTW) para o diretorio temporario de imagens
//			int i = 0;

			for (ImagemComBlobDescargaBean imagemDescargaBean : repImagemSrc.buscarBeansPorIdDescarga(idDescarga))
			{
				String nomeArquivo = blobsDir.getPath() + File.separatorChar + imagemDescargaBean.getIdPasta()
					+ File.separatorChar + imagemDescargaBean.getNomeArquivo();
				
				File arquivoDestino = new File(nomeArquivo);
				File subPasta = arquivoDestino.getParentFile();
				
				if (!subPasta.exists())
				{
					subPasta.mkdir();
				}
				
				try {
					byte dados[] = imagemDescargaBean.getBytesImagem();
					
					mapMD5.put(imagemDescargaBean.getId(), Funcoes.geraMD5(dados));
					
					writeBytes(arquivoDestino, dados);
					
					imagemDescargaBean.setBytesImagem(null);
					contador++;
					
				}
				catch (Exception iox) {
					throw new ModelException("Erro ao escrever arquivo de imagem (BLOB) da descarga. id_imagem =[" + imagemDescargaBean.getId() + "]", iox );
				}
//				i++;
			}
			
			this.mapaMd5 = mapMD5;

			long endTime = System.currentTimeMillis();
			logger.info("[" + contador + "] imagens transferidas para a descarga em [" + (endTime - startTime) + "] msecs.");
			
			// Escreve os dados (do GTW) no Derby.
			logger.info("Transferindo dados para o Derby...");
			
			repDescargaDst.salvar(descargaBeanDst);
			
			// Redimensiona a lista de veículos no destino já com o tamanho certo (para evitar o processo de alargamento)
			descargaBeanDst.setListaVeiculos(new ArrayList<VeiculoDescargaBean>(qtdVeiculos.intValue()));
			
			logger.info("Buscando árvore de dados no banco de dados do GTW...");
			Iterable<VeiculoDescargaBean> itr = repVeiculoSrc.buscarVeiculosPorIdDescarga(idDescarga);
			
			for (VeiculoDescargaBean beanVeiculo : itr) {
				
				for (ImagemDescargaBean beanImagem: beanVeiculo.getListaImagens()) {
					beanImagem.setMd5Imagem(mapMD5.get(beanImagem.getId()));
				}

				descargaBeanDst.getListaVeiculos().add(beanVeiculo);
				beanVeiculo.setDescarga(descargaBeanDst);
				
			}
			
			logger.info("Gravando árvore de dados no Derby...");
			repDescargaDst.salvar(descargaBeanDst);
			logger.info("Dados transferidos para o Derby.");
			
			// Fecha a fábrica de origem (GTW).
			logger.debug("Fechando banco de dados de origem (GTW).");
			if (fabSrc != null)
			{
				try {
					fabSrc.fecharFabrica();
					fabSrc = null;
				}
				catch (RepositorioException re) {
					throw new ModelException("Erro ao fechar a fábrica de repositórios do GTW (SQL SERVER) durante exportação de descarga.", re);
				}
			}
			
			// Fecha a fábrica de destino (Derby).
			logger.debug("Fechando banco de dados de destino (Derby).");
			if (fabDst != null) {
				try {
					fabDst.fecharFabrica();
					fabDst = null;
				}
				catch (RepositorioException re) {
					throw new ModelException("Erro ao fechar a fábrica de repositórios do DERBY durante exportação de descarga.", re);

				}
			}
			
			// Cria um JAR/ZIP da database do Derby
			logger.debug("Criando ZIP/JAR do banco Derby.");
			File derbyDatabaseJar = new File(dadosDir.getPath() + File.separatorChar + FabricaRepositorioDerby.DESCARGA_DERBY_FILE_NAME + ".jar");
			
			makePlainJar(derbyDatabase, derbyDatabaseJar, Versao.getInstance().getManifest()); //Por motivos de performance do banco de dados derby.
		    
			// Limpa a estrutura temporaria do Derby (o dir)
			logger.debug("Limpando estrutura do banco Derby.");
			deleteDirectoryRecursively(derbyDatabase);
			
			// Cria a ISO propriamente dita.
			String volumeName = Descarga.getIsoVolumeName(idDescarga);

			makeIso(buildDir, targetIso, volumeName);
			
			// Limpa o diretório temporário.
			logger.debug("Limpando estrutura temporária da Descarga.");
			deleteDirectoryRecursively(buildDir);
			
		}
		finally {
			
			// Fecha a fábrica de origem
			if (fabSrc != null)	{
				try {
					fabSrc.fecharFabrica();
				}
				catch (RepositorioException re) {
					throw new ModelException("Erro ao fechar a fábrica de repositórios do GTW (SQL SERVER) durante exportação de descarga.", re);
				}
			}
			
			// Fecha a fábrica de destino
			if (fabDst != null)	{
				try {
					fabDst.fecharFabrica();
				}
				catch (RepositorioException re) {
					throw new ModelException("Erro ao fechar a fábrica de repositórios do DERBY durante exportação de descarga.", re);
				}
			}
		}
	}

	/**
	 * Deleta um diretório, de maneira recursiva.
	 * @param rootDir
	 * @return true se deletou tudo, false se o processo foi interrompido.
	 */
	private static boolean deleteDirectoryRecursively(File rootDir) {
		  
	    if(rootDir != null && rootDir.isDirectory() && rootDir.exists()) {
	    	
	      for (File subFile : rootDir.listFiles())
	      {
	    	  if(subFile.isDirectory())
	    	  {
		      		if(!deleteDirectoryRecursively(subFile))
			      		return false;
		      }
		      else
		      {
		      	if (!subFile.delete())
		      		return false;
		      }

	      }
	      return(rootDir.delete());
	    }
	    else {
	    	return false;
	    }
	}

	/**
	 * Escreve um array de bytes em um arquivo de destino.
	 * @param arquivoDestino o arquivo de destino.
	 * @param conteudo os bytes a serem escritos
	 * @throws IOException
	 */
	private void writeBytes(File arquivoDestino, byte[] conteudo) throws IOException {
		
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(arquivoDestino);
			fos.write(conteudo);
		}
		finally
		{
			if (fos != null)
				fos.close();
		}
	}

	
	private void makePlainJar(File srcDir, File dstJar, Manifest manifest) throws ModelException, IOException {
		
		if (srcDir == null)
			throw new ModelException("Parâmetro inválido: srcDir não pode ser nulo.");

		if (dstJar == null)
			throw new ModelException("Parâmetro inválido: dstJar não pode ser nulo.");
		
		if (!srcDir.exists())
			throw new ModelException("Parâmetro inválido: srcDir não existe.");

		if (!srcDir.isDirectory())
			throw new ModelException("Parâmetro inválido: srcDir não é um diretório.");
		
	    JarOutputStream outJar = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(dstJar)), manifest);
	    outJar.setMethod(JarOutputStream.STORED);
	    byte[] buf = new byte[4096];
	    int rootDirCut = srcDir.getParentFile().getCanonicalPath().length();

	    Queue<File> filaDirs = new LinkedList<File>();
	    filaDirs.offer(srcDir);
	    
		CRC32 crc = new CRC32();
	    while (!filaDirs.isEmpty())
	    {
	    	File currDir = filaDirs.poll();
	    	
		    for (File srcFile : currDir.listFiles())
		    {
		    	// Só estamos intressados nos arquivos propriamente ditos. Diretórios colocamos na fila.
		    	if (srcFile.isDirectory())
		    	{
	    			filaDirs.offer(srcFile);
		    		continue;
		    	}
		    	else
		    	{
		    		String fileName = srcFile.getCanonicalPath();
		    		fileName = fileName.substring(1 + rootDirCut);
			        fileName = fileName.replace('\\', '/');
			        
					crc.reset();
			        InputStream in = new BufferedInputStream(new FileInputStream(srcFile));
			        
			        ByteArrayOutputStream bos = new ByteArrayOutputStream();

			        int len; //Método burro!, porque têm que ler 2x a stream, primeiro para calcular o CRC, depois para gravar.
			        while ((len = in.read(buf)) > 0) {
			        	crc.update(buf, 0, len);
			        	bos.write(buf, 0, len);
			        }
			        in.close();

			        JarEntry jarEntry = new JarEntry(fileName);
			        jarEntry.setTime(srcFile.lastModified());
			        jarEntry.setSize(srcFile.length());
			        jarEntry.setCrc(crc.getValue());
			        outJar.putNextEntry(jarEntry);
			        
			        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
			        while ((len = bis.read(buf)) > 0) {
			        	outJar.write(buf, 0, len);
			        }
			        bis.close();
			        outJar.closeEntry();
		    	}
		    }
	    }
	    outJar.close();
	}
	
	private void makeIso(File buildDir, File targetIso, String volumeName) throws ConfigException, HandlerException, FileNotFoundException {
		
		// Valida os parâmetros
		assert (buildDir != null);
		assert (targetIso != null);
		assert (volumeName != null);
		
		assert (buildDir.exists() && buildDir.isDirectory());
		assert (!targetIso.exists());
		
		Date createDate = new Date();
		String publisher = "Consilux Tecnologia";
		String dataPreparer = "GTW";
		
		// Raiz da ISO
		ISO9660RootDirectory isoRoot = new ISO9660RootDirectory();
		isoRoot.addContentsRecursively(buildDir);

		// Configurações (ISO9660)
		ISO9660Config iso9660Config = new ISO9660Config();
		iso9660Config.allowASCII(false);
		
	    // Level 1: File names are limited to eight characters with a three-character extension, using upper
		// case letters, numbers and underscore only. The maximum depth of directories is eight.
		
	    // Level 2: File names are not limited to 11 characters (the 8.3 format) but can be up to the maximum
		// allowed by the 1 byte counter in the directory entry and the filename length byte counter. Typically,
		// this is close to 180 characters, depending on how many extended attributes are present.
		
	    // Level 3: Files are allowed to be non-contiguous (i.e., fragmented), principally to allow packet
		// writing or incremental CD recording).
		
		// ATENÇÃO: o ideal seria colocar 3, mas a lib de ISO ainda não suporta e levanta uma exceção.
		iso9660Config.setInterchangeLevel(2);
		
		iso9660Config.restrictDirDepthTo8(true);
		iso9660Config.setPublisher(publisher);
		iso9660Config.setVolumeID(volumeName);
		iso9660Config.setDataPreparer(dataPreparer);
		iso9660Config.forceDotDelimiter(true);
		iso9660Config.setCreateDate(createDate);

		// Configurações (RockRidge)
		RockRidgeConfig rrConfig = new RockRidgeConfig();
		rrConfig.setMkisofsCompatibility(false);
		rrConfig.hideMovedDirectoriesStore(true);
		rrConfig.forcePortableFilenameCharacterSet(true);
		
		// Configurações (Joliet)
		JolietConfig jolietConfig = new JolietConfig();
		jolietConfig.setPublisher(publisher);
		jolietConfig.setVolumeID(volumeName);
		jolietConfig.setDataPreparer(dataPreparer);
		jolietConfig.forceDotDelimiter(true);
		jolietConfig.setCreateDate(createDate);
		
		StreamHandler streamHandler = new CSXISOImageFileHandler(targetIso);
		CreateISO iso = new CreateISO(streamHandler, isoRoot);
	
		logger.info("Criando ISO [" + targetIso.getPath() +"]...");
		iso.process(iso9660Config, rrConfig, jolietConfig, null);
		logger.info("ISO criada.");
			
		streamHandler.endDocument();
		logger.info("Arquivo ISO liberado.");
	}


	private void transferAppJar(File srcJar, File dstJar, String appConf) throws IOException {
		
        JarFile src = null;
        JarOutputStream dst = null;
        
        HashSet<String> ignoreFiles = new HashSet<String>();
        ignoreFiles.add("META-INF/MANIFEST.MF");
        ignoreFiles.add(Configuracao.ARQ_CONF_APP.substring(1));
        ignoreFiles.add("about.html");
        ignoreFiles.add("license.html");
        ignoreFiles.add("license.txt");
        ignoreFiles.add("readme.html");
        ignoreFiles.add("readme.txt");
        ignoreFiles.add("plugin.properties");
        ignoreFiles.add("plugin.xml");
        ignoreFiles.add(".options");
        ignoreFiles.add("properties.dtd");
        ignoreFiles.add("PropertyList-1.0.dtd");
        ignoreFiles.add("digesterRules.xml");
        
        ignoreFiles.add("license/LICENSE");
        ignoreFiles.add("license/LICENSE.dom-documentation.txt");
        ignoreFiles.add("license/LICENSE.dom-software.txt");
        ignoreFiles.add("license/LICENSE.sax.txt");
        ignoreFiles.add("license/NOTICE");
        ignoreFiles.add("license/README.dom.txt");
        ignoreFiles.add("license/README.sax.txt");
        
        ignoreFiles.add("META-INF/LICENSE");
        ignoreFiles.add("META-INF/LICENSE.txt");
        ignoreFiles.add("META-INF/NOTICE");
        ignoreFiles.add("META-INF/NOTICE.txt");
        
        ignoreFiles.add("META-INF/maven/com.consilux.gtw/ver-descarga/pom.properties");
        ignoreFiles.add("META-INF/maven/com.consilux.gtw/ver-descarga/pom.xml");
        ignoreFiles.add("META-INF/maven/commons-beanutils/commons-beanutils/pom.properties");
        ignoreFiles.add("META-INF/maven/commons-beanutils/commons-beanutils/pom.xml");
        ignoreFiles.add("META-INF/maven/commons-collections/commons-collections/pom.properties");
        ignoreFiles.add("META-INF/maven/commons-collections/commons-collections/pom.xml");
        ignoreFiles.add("META-INF/maven/commons-configuration/commons-configuration/pom.properties");
        ignoreFiles.add("META-INF/maven/commons-configuration/commons-configuration/pom.xml");
        ignoreFiles.add("META-INF/maven/commons-lang/commons-lang/pom.properties");
        ignoreFiles.add("META-INF/maven/commons-lang/commons-lang/pom.xml");
        ignoreFiles.add("META-INF/maven/commons-logging/commons-logging/pom.properties");
        ignoreFiles.add("META-INF/maven/commons-logging/commons-logging/pom.xml");
        ignoreFiles.add("META-INF/maven/log4j/log4j/pom.properties");
        ignoreFiles.add("META-INF/maven/log4j/log4j/pom.xml");
        ignoreFiles.add("META-INF/maven/net.sf.jasperreports/jasperreports/pom.properties");
        ignoreFiles.add("META-INF/maven/net.sf.jasperreports/jasperreports/pom.xml");
        
        try {	
        	src = new JarFile(srcJar);
        	dst = new JarOutputStream(new FileOutputStream(dstJar), src.getManifest());
        	
	        Enumeration<? extends JarEntry> entries = src.entries();
	        while (entries.hasMoreElements()) {
	            JarEntry e = entries.nextElement();
	            
	            if (!ignoreFiles.contains(e.getName()))
	            {
	            	dst.putNextEntry(e);
	            }
	            
	            if (!e.isDirectory() && !ignoreFiles.contains(e.getName())) {
	            	Funcoes.copyBytes(src.getInputStream(e), dst);
	            }
	            
	            dst.closeEntry();
	        }

	        byte[] utf8Bytes = appConf.getBytes("UTF8");
			JarEntry confEntry = new JarEntry(Configuracao.ARQ_CONF_APP.substring(1));
			dst.putNextEntry(confEntry);
			dst.write(utf8Bytes);
			dst.closeEntry();
        }
        finally {
        	if (src != null)
        	{
        		src.close();
        	}
        	if (dst != null)
        	{
        		dst.close();
        	}
        }
	}
	
	private String makeAppConf() {
		
		StringBuilder sbCONF = new StringBuilder();
		
		sbCONF.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
		sbCONF.append("\r\n");
		sbCONF.append("<config>\r\n");
		sbCONF.append("  <descarga>\r\n");
		sbCONF.append("    <ait>\r\n");
		for (TipoRemessa tr : ConfiguracaoProvider.getInstance().getConfiguracaoRemessa().getMapaTipos().values())
		{
			sbCONF.append("      <tipo codigo=\"" + tr.getCodigo() + "\" modelo_ait=\"" + tr.getModeloAIT() + "\"/>\r\n");
		}
		sbCONF.append("    </ait>\r\n");
		sbCONF.append("  </descarga>\r\n");
		sbCONF.append("</config>");
		
		return sbCONF.toString();
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Integer idDescarga;
		
		try {
			logger.info("Verificando se existe descarga a ser exportada...");
			idDescarga = buscaPrimeiraDescargaParaExportar();
			
		}
		catch (Exception e) {
			logger.error("Erro ao buscar descarga a ser exportada: "+e.getMessage(), e);
			return;
		}
		
		if (idDescarga == null) {
			logger.info("Não existe nenhuma descarga para ser exportada.");
			return;
		}
		
		logger.info("A descarga ["+idDescarga+"] ainda não foi exportada...");
		logger.info("Iniciando processo de exportação...");
		
		try {
			Date dIni = new Date();
			exportarDescarga(idDescarga);
			Descarga.confirmaExportacaoDescarga(idDescarga, this.mapaMd5);
			logger.info("Descarga exportada com sucesso em ["+String.valueOf((new Date().getTime()-dIni.getTime())/1000)+"] segs.");
		} 
		catch (Exception e) {
			logger.error("Erro ao exportar a descarga: "+e.getMessage(), e);
		}
	}

	private Integer buscaPrimeiraDescargaParaExportar() throws ConexaoException, SQLException, ModelException {
		Map<String, Object> mFiltros = new HashMap<String, Object>();
		mFiltros.put("nao_exportada", null);
		List<DescargaBean> l = Descarga.buscarDescargaPor(mFiltros);
		
		if (l.size() < 1) {
			return null;
		}
		
		return l.get(0).getId();
	}
	/**
	 * @return the mapaMd5
	 */
	public Map<Integer, String> getMapaMd5() {
		return mapaMd5;
	}
	
}