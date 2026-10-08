//import static org.testng.AssertJUnit.*;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.List;
//import java.util.Set;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.Validation;
//import javax.validation.Validator;
//import javax.validation.ValidatorFactory;
//
//import org.testng.annotations.DataProvider;
//import org.testng.annotations.Test;
//
//import com.consilux.model.ItemExportaRemessa;
//import com.consilux.model.ItemExportaRemessaURBS;
//import com.consilux.model.ModeloInstrumento;
//import com.consilux.model.Remessa;
//import com.consilux.model.exception.ModelException;
//import com.consilux.model.remessa.ExportaRemessaURBS;
//
///**
// * Classe de testes para teste da exportação de remessas.
// * @author Raoni Meira Gabriel
// * @version $Revision$ $Date$ $Author$
// */
//@Test(groups = { "completo" })
//public class ExportaRemessaTest {
//
//	//
//	// JSR - 303
//	// http://docs.jboss.org/hibernate/stable/validator/reference/en-US/pdf/hibernate_reference.pdf
//	//
//	
//	private static final String ITEM_DATA_PROVIDER_GOOD = "test1";
//	private static final String ITEM_DATA_PROVIDER_BAD = "test2";
//	
//	private static final String ITEM_AND_REMESSA_DATA_PROVIDER_GOOD = "test3";
//	private static final String ITEM_AND_REMESSA_DATA_PROVIDER_BAD = "test4";
//	
//	private static Remessa REMESSA_URBS_VELOCIDADE = createRemessaUrbs();
//	public static byte[] BLOB_URBS_VELOCIDADE = createBlobUrbsVelocidade();
//	
//	/**
//	 * Método que cria uam remessa (FAKE) da URBS
//	 * @return
//	 */
//	private static Remessa createRemessaUrbs()
//	{
//		Calendar dataRemessa = Calendar.getInstance();
//		dataRemessa.set(2010, Calendar.JUNE, 8, 14, 31, 42);
//		Remessa remessaURBS = new Remessa(4, 1060, dataRemessa.getTime(),
//			null, 7189, 3707142, 3714330, 7, "CR1", 0);
//		
//		return remessaURBS;
//	}
//	
//	/**
//	 * Método que cria um blob (FAKE) para infrações de velocidade
//	 * @return
//	 */
//	private static byte[] createBlobUrbsVelocidade() {
//
//		// Pega o blob do arquivo "URBS_infracao_194441.jpg" para
//		// colocar no bean.
//		
//		byte[] blobImagem = null;
//		FileInputStream fis = null;
//		
//		try {
//			URI caminhoBlob = ExportaRemessaTest.class.getResource("URBS_infracao_194441.jpg").toURI();
//			File arquivoBlob = new File(caminhoBlob); 
//			blobImagem = new byte[(int) arquivoBlob.length()];
//			fis = new FileInputStream(arquivoBlob);
//			fis.read(blobImagem, 0, blobImagem.length);
//		}
//		catch (Exception ex)
//		{
//			throw new AssertionError("Erro ao inicializar imagenm (BLOB) para testes unitários.");
//		}
//		finally
//		{
//			if (fis != null)
//			{
//				try {
//					fis.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		return blobImagem;
//	}
//
//	private static List<ItemExportaRemessaURBS> createItemExportaRemessUrbs(boolean createBad) {
//		
//		List<ItemExportaRemessaURBS> lRet = new ArrayList<ItemExportaRemessaURBS>();
//		
//		Calendar dataAfericao = Calendar.getInstance();
//		dataAfericao.set(2010, Calendar.MAY, 6, 0, 0, 0);
//		Calendar dataInfracao = Calendar.getInstance();
//		dataInfracao.set(2010, Calendar.MAY, 25, 4, 21, 42);
//
//		Calendar dataValidadeAfericao = Calendar.getInstance();
//		dataValidadeAfericao.set(2011, Calendar.JUNE, 6, 0, 0, 0);
//
//		if (createBad)
//		{
//			// Cria itens "ruins" para realizar testes ao contrário.
//			
//			// Um item com informações nulas.
//			ItemExportaRemessaURBS itemUrbsRuim = new ItemExportaRemessaURBS (
//				null, null, null, null, null, null, null, null, null, null, null,
//				null, null, null, null, null, ModeloInstrumento.PORTATIL, null, null, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);
//
//			// Um item aparentemente bom, mas sem id_imagem  (nulo)
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), null, dataValidadeAfericao.getTime() );
//			lRet.add(itemUrbsRuim);			
//
//			// Um item aparentemente bom, mas com id_imagem negativo
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), -1, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);			
//			
//			// Um item aparentemente bom, mas com id_imagem zero
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 0, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);	
//			
//			// Um item aparentemente bom, mas com nome muito grande.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"UM TESTE INCOMODA MUITO A GENTE, DOIS TESTES INCOMODAM MUITO MAIS, TRES TESTES INCOMODAM MUITO A GENTE, QUATRO TESTES...",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);
//			
//			// Um item aparentemente bom, mas com placa muito grande.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "ABCDEFG1234567", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);			
//			
//			// Um item aparentemente bom, mas com id infração nulo.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				null, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);					
//			
//			// Um item aparentemente bom, mas com id infração negativo.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				-1, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);					
//			
//			// Um item aparentemente bom, mas com id infração zero.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				0, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);					
//			
//			// Um item aparentemente bom, mas com id enquadramento nulo.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				null, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);					
//
//			// Um item aparentemente bom, mas com id enquadramento zero.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				0, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);					
//			
//			// Um item aparentemente bom, mas com id enquadramento negativo.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				-1, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);				
//			
//			// Um item aparentemente bom, mas com id enquadramento 1 (imagem-teste).
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				1, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);					
//			
//			// Um item aparentemente bom, mas é um enquadramento de velocidade (74550)
//			// e a velocidade é nula.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//					194441, 3709109, "CR1",
//					"W", "RAD", "AJE4841", "PR",
//					159911, dataInfracao.getTime(),
//					"PE AGOSTINHO X FRANCISCO ROCHA",
//					74550, 60, null, 9905123,
//					113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);			
//			
//			// Um item aparentemente bom, mas é enquadramento de velocidade
//			// e a velocidade é zero.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 0, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);
//
//			// Um item aparentemente bom, mas com velocidade medida negativa.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, -1, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441,dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);
//			
//			// Um item aparentemente bom, mas com velocidade muito alta.
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 500, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);
//
//			// Um item aparentemente bom, mas com uma data da infração no futuro
//			dataInfracao = Calendar.getInstance();
//			dataInfracao.add(Calendar.DAY_OF_YEAR, 1);
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);
//			
//			// Um item aparentemente bom, mas com a aferição vencida
//			dataInfracao = Calendar.getInstance();
//			dataAfericao = Calendar.getInstance();
//			dataValidadeAfericao = Calendar.getInstance();
//			
//			dataAfericao.add(Calendar.YEAR, -2);
//			dataValidadeAfericao.add(Calendar.YEAR, -1);
//			
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);			
//			
//			// Reajusta as datas, aos seus valores originais
//			dataAfericao = Calendar.getInstance();
//			dataAfericao.set(2010, Calendar.MAY, 6, 0, 0, 0);
//			dataInfracao = Calendar.getInstance();
//			dataInfracao.set(2010, Calendar.MAY, 25, 4, 21, 42);
//			dataValidadeAfericao = Calendar.getInstance();
//			dataValidadeAfericao.set(2011, Calendar.JUNE, 6, 0, 0, 0);
//			
//			// Um item aparentemente bom, mas com id agente nulo
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				null, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);				
//			
//			// Um item aparentemente bom, mas com id agente zero
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				0, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);				
//			
//			// Um item aparentemente bom, mas com uf agente nulo
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, null, ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);				
//			
//			// Um item aparentemente bom, mas com uf agente muito grande
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "ABC", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);				
//			
//			// Um item aparentemente bom, mas com uf agente vazio
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);			
//			
//			// Um item aparentemente bom, mas com uf agente que não é um UF válido
//			itemUrbsRuim = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "ZZ", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			lRet.add(itemUrbsRuim);					
//			
//		} else {
//			
//			////////////////////////////////////////////////////////////////////////////////////////////
//			// Um item BOM
//			////////////////////////////////////////////////////////////////////////////////////////////
//			ItemExportaRemessaURBS itemUrbsBom = new ItemExportaRemessaURBS(
//				194441, 3709109, "CR1",
//				"W", "RAD", "AJE4841", "PR",
//				159911, dataInfracao.getTime(),
//				"PE AGOSTINHO X FRANCISCO ROCHA",
//				74550, 60, 74, 9905123,
//				113, "PR", ModeloInstrumento.FIXO, dataAfericao.getTime(), 194441, dataValidadeAfericao.getTime());
//			
//			lRet.add(itemUrbsBom);
//			
//		}
//		return lRet;
//	}
//	
//	////////////////////////////////////////////////////////////////////////////////////////////
//	// Data Provider, que entrega uma array de arrays. São x itens, por y parâmetros,
//	// que serão fornecidos aos métodos que consumirem este provider.
//	// 1) = ItemExportaRemessaURBS
//	////////////////////////////////////////////////////////////////////////////////////////////	
//	@DataProvider(name = ITEM_DATA_PROVIDER_GOOD)
//	public Object[][] providerUrbsItensOnlyGood() {
//		
//		List<ItemExportaRemessaURBS> itensURBS = createItemExportaRemessUrbs(false);
//		Object[][] mParams = new Object[itensURBS.size()][1];
//		
//		for (int i = 0; i < itensURBS.size(); i++)
//		{
//			mParams[i] = new Object[] { itensURBS.get(i) };
//		}
//		return mParams;
//	}
//	
//	// Mesmo provider que acima, mas que gera itens "ruins" 
//	@DataProvider(name = ITEM_DATA_PROVIDER_BAD)
//	public Object[][] providerUrbsItensOnlyBad() {
//		
//		List<ItemExportaRemessaURBS> itensURBS = createItemExportaRemessUrbs(true);
//		Object[][] mParams = new Object[itensURBS.size()][1];
//		
//		for (int i = 0; i < itensURBS.size(); i++)
//		{
//			mParams[i] = new Object[] { itensURBS.get(i) };
//		}
//		return mParams;
//	}	
//	
//	////////////////////////////////////////////////////////////////////////////////////////////
//	// Data Provider, que entrega uma array de arrays. São x itens, por y parâmetros,
//	// que serão fornecidos aos métodos que consumirem este provider.
//	// 1) = ItemExportaRemessaURBS
//	// 2) = Remessa
//	////////////////////////////////////////////////////////////////////////////////////////////	
//	@DataProvider(name = ITEM_AND_REMESSA_DATA_PROVIDER_GOOD )
//	public Object[][] providerUrbsItensAndRemessaGood() {
//		
//		List<ItemExportaRemessaURBS> itensURBS = createItemExportaRemessUrbs(false);
//		Object[][] mParams = new Object[itensURBS.size()][2];
//		
//		for (int i = 0; i < itensURBS.size(); i++)
//		{
//			mParams[i] = new Object[] { itensURBS.get(i), REMESSA_URBS_VELOCIDADE };
//		}
//		return mParams;
//	}
//	
//	// Mesmo provider que acima, mas que gera itens "ruins"
//	@DataProvider(name = ITEM_AND_REMESSA_DATA_PROVIDER_BAD)
//	public Object[][] providerUrbsItensAndRemessaBad() {
//		
//		List<ItemExportaRemessaURBS> itensURBS = createItemExportaRemessUrbs(true);
//		Object[][] mParams = new Object[itensURBS.size()][2];
//		
//		for (int i = 0; i < itensURBS.size(); i++)
//		{
//			mParams[i] = new Object[] { itensURBS.get(i), REMESSA_URBS_VELOCIDADE };
//		}
//		return mParams;
//	}	
//	
//	@Test(dataProvider = ITEM_DATA_PROVIDER_GOOD)
//	public void testItemNotNull(ItemExportaRemessa itemValidar)
//	{
//		assertNotNull("Item nulo", itemValidar);
//	}
//	
//	@Test(dataProvider = ITEM_AND_REMESSA_DATA_PROVIDER_GOOD, dependsOnMethods = { "testItemNotNull"} )
//	public void testExportaURBSGood(ItemExportaRemessaURBS itemValidar, Remessa remessaValidar) throws ModelException
//	{
//		// Testa o construtor sem parâmetros
//		ExportaRemessaURBS exportaRemessa = new ExportaRemessaURBS();
//		
//		// Testa setters
//		exportaRemessa.setComObliteracao(false);
//		exportaRemessa.setRemessa(remessaValidar);
//		
//		// Testa validações
//		exportaRemessa.preValidarItem(itemValidar);
//		exportaRemessa.validarItem(itemValidar);
//
//		// Testa a remessa qual está sendo exportada
//		Remessa remessa = exportaRemessa.getRemessa();
//		assertSame("Remessa foi trocada durante a exportação.", remessa, remessaValidar);
//
//		// Testa o tipo da Remessa
//		assertSame("Tipo da remessa inválido", REMESSA_URBS_VELOCIDADE.getTipo(), remessaValidar.getTipo());
//
//		// Testa o código externo da Remessa
//		assertSame("Código externo da remessa inválido", REMESSA_URBS_VELOCIDADE.getTipo(), remessaValidar.getTipo());
//		
//		// Testa o nome do arquivo TXT
//		String nomeArquivoTxt = exportaRemessa.getNomeArquivoZip();
//		assertNotNull("Nome do arquivo TXT não pode ser nulo.", nomeArquivoTxt);
//		assertEquals("Tamanho do nome do arquivo TXT inválido", 12, nomeArquivoTxt.length());
//		
//		// Testa o nome do arquivo ZIP
//		String nomeArquivoZip = exportaRemessa.getNomeArquivoZip();
//		assertNotNull("Nome do arquivo ZIP não pode ser nulo.", nomeArquivoZip);
//		assertEquals("Tamanho do nome do arquivo ZIP inválido", 12, nomeArquivoZip.length());
//		
//		// Testa o cabeçalho da remessa. URBS não possui cabeçalho .
//		String cabecalhoRemessa = exportaRemessa.getCabecalhoRemessa();
//		assertNull("URBS não deve ter cabeçalho na remessa", cabecalhoRemessa);
//	}
//
//	@Test(dataProvider = ITEM_AND_REMESSA_DATA_PROVIDER_BAD, expectedExceptions = ModelException.class)
//	public void testExportaURBSBad(ItemExportaRemessaURBS itemValidar, Remessa remessaValidar) throws ModelException
//	{
//		// Observar que este método pode lançar exceções: "throws ModelException"
//		// e que na anotation definimos que se for esta exceção, não tem problema:
//		// expectedExceptions = ModelException.class, pois é justamente para CAUSAR exceção.
//		
//		ExportaRemessaURBS exportaRemessa = new ExportaRemessaURBS();
//		exportaRemessa.setComObliteracao(false);
//		exportaRemessa.setRemessa(remessaValidar);
//		
//		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//		Validator validator = factory.getValidator();
//		Set<ConstraintViolation<ItemExportaRemessaURBS>> violations = validator.validate(itemValidar);
//		
//		if (violations.size() > 0)
//		{
//			// Ocorreu um erro de validação, lançamos uma exceção, mas este
//			// comportamento já era experado.
//			throw new ModelException(violations.iterator().next().getMessage());
//		} else {
//			// Não ocorreu erro de validação mas que DEVERIA TER OCORRIDO.
//			// Neste caso, temos um o problema... 
//			throw new AssertionError("Passou INDEVIDAMENTE no teste testExportaURBSBad.");
//		}
//	}
//	
//}
