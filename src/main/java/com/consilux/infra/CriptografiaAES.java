package com.consilux.infra;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.consilux.conf.ConfiguracaoChaveValor;
import com.consilux.conf.ConfiguracaoProvider;

public class CriptografiaAES {
	
	private static Logger logger = Logger.getLogger(CriptografiaAES.class); 

	private Cipher cipher_e = null, cipher_d = null;
	
	public CriptografiaAES()
	{
		ConfiguracaoChaveValor ccv = ConfiguracaoProvider.getInstance().getConfiguracaoChaveValor();
		byte[] chave 	= 	Funcoes.hexToBytes(ccv.get("cript_chave"));
		byte[] iv 		= 	Funcoes.hexToBytes(ccv.get("cript_iv"));
		
		try {
			SecretKeySpec chave_sp = new SecretKeySpec(chave, "AES");
			IvParameterSpec iv_sp = new IvParameterSpec(iv);
			
			cipher_e = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher_e.init(Cipher.ENCRYPT_MODE, chave_sp, iv_sp);
			
			cipher_d = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher_d.init(Cipher.DECRYPT_MODE, chave_sp, iv_sp);
		} catch (Exception e) {
			logger.error("Não foi possível inicializar Criptografia AES!", e);
			cipher_e = null;
		}
	}
	
	public byte[] criptografaAES(byte[] dados) throws GeneralSecurityException {
		
		byte[] bRet;
		
		if(cipher_e != null)
			bRet = cipher_e.doFinal(dados);
		else
			bRet = dados;
		
		return bRet;
	}
	
	public byte[] descriptografaAES(byte[] dados) throws GeneralSecurityException {
		
		byte[] bRet;
		
		if(cipher_d != null)
			bRet = cipher_d.doFinal(dados);
		else
			bRet = dados;
		
		return bRet;
	}
	
}
