//import static org.testng.AssertJUnit.assertEquals;
//import static org.testng.AssertJUnit.assertSame;
//import static org.testng.AssertJUnit.assertNull;
//import static org.testng.AssertJUnit.assertNotNull;
//
//import org.testng.annotations.Test;
//
//import com.consilux.lib.GenericCache;
//import com.consilux.model.VeiculoImagem;
//
//
///**
// * Classe de testes para o mecanismo de cache (prefetch) de imagens.
// * @author raoni
// */
//@Test(groups = { "completo" })
//public class CacheImagensTest {
//
//	@Test()
//	public void testCacheSizeDefault()
//	{
//		for (int i = 0; i < GenericCache.TAMANHO_DEFAULT; i++) {
//			VeiculoImagem dmyImagem = new VeiculoImagem(i, "OBJ");
//			VeiculoImagem.getCacheImagens().addObjectToCache(dmyImagem.getIdImagem(), dmyImagem);
//		}
//		assertEquals("Tamanho do cache", GenericCache.TAMANHO_DEFAULT, VeiculoImagem.getCacheImagens().getSize());
//	}
//
//	@Test()
//	public void testCacheHit()
//	{
//		VeiculoImagem dmyImagem = new VeiculoImagem(1, "OBJ");
//		VeiculoImagem.getCacheImagens().addObjectToCache(dmyImagem.getIdImagem(), dmyImagem);
//		
//		VeiculoImagem imgCacheHit = VeiculoImagem.getCacheImagens().findObject(1);
//		assertSame("Cache hit", dmyImagem, imgCacheHit);
//	}
//	
//	@Test()
//	public void testCacheMiss()
//	{
//		VeiculoImagem dmyImagem = new VeiculoImagem(1, "OBJ");
//		VeiculoImagem.getCacheImagens().addObjectToCache(dmyImagem.getIdImagem(), dmyImagem);
//		
//		VeiculoImagem imgCacheMiss = VeiculoImagem.getCacheImagens().findObject(777);
//		assertNull("Cache miss", imgCacheMiss);
//	}	
//	
//	@Test()
//	public void testCacheCycle()
//	{
//		
//		for (int i = 0; i < GenericCache.TAMANHO_DEFAULT + 1; i++) {
//			VeiculoImagem dmyImagem = new VeiculoImagem(i, "OBJ");
//			VeiculoImagem.getCacheImagens().addObjectToCache(dmyImagem.getIdImagem(), dmyImagem);
//		}
//		assertEquals("Tamanho do cache", GenericCache.TAMANHO_DEFAULT, VeiculoImagem.getCacheImagens().getSize());
//		
//		VeiculoImagem imgCacheMiss = VeiculoImagem.getCacheImagens().findObject(0);
//		assertNull("Cache miss", imgCacheMiss);
//		
//		VeiculoImagem imgCacheHit = VeiculoImagem.getCacheImagens().findObject(100);
//		assertNotNull("Cache hit", imgCacheHit);
//	}	
//	
//}
