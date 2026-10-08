package com.consilux.model;

import org.apache.commons.codec.binary.Base64;

public class Base64Utils {
	
	public static String EncodeBase64(byte[] byte_array)
    {
		return Base64.encodeBase64String(byte_array);
    }

    public static byte[] DecodeBase64(String base64_string)
    {
        return Base64.decodeBase64(base64_string);
    }
	
}
