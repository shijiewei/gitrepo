package com.zzx.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * 
 * <p>
 * Title: AESUtil
 * </p>
 * <p>
 * Description: AES加密解密方法
 * </p>
 * <p>
 * 
 * </p>
 * 
 * @author xwc1125
 * @date 2015-7-20下午1:47:56
 * 
 */
public class AESUtils {
	private static final String charset = "utf-8";

	/**
	 * 
	 * <p>
	 * Title: Encrypt
	 * </p>
	 * <p>
	 * Description: AES加密
	 * </p>
	 * <p>
	 * 
	 * </p>
	 * 
	 * @param sSrc
	 *            要加密的字串
	 * @param sKey
	 *            加密的秘钥
	 * @return 返回加密后的结果
	 * @throws Exception
	 * 
	 * @author xwc1125
	 * @date 2015-7-20下午1:46:32
	 */
	public static String Encrypt(String sSrc, String sKey) throws Exception {
		if (sSrc == null || sSrc.length() == 0 || sSrc.trim().length() == 0) {
			return null;
		}
		if (sKey == null) {
			throw new Exception("encrypt key is null");
		}
		if (sKey.length() != 16) {
			throw new Exception("encrypt key length error");
		}
		byte[] raw = sKey.getBytes(charset);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(sSrc.getBytes(charset));
		return Base64.encode(encrypted);
	}

/*	public static String Encrypt(String sSrc, String sKey, int tag)
			throws Exception {
		String key = SecurityKeyUtils.getKeyValue(sKey, tag);
		return Encrypt(sSrc, key);
	}*/

	/**
	 * 
	 * <p>
	 * Title: Decrypt
	 * </p>
	 * <p>
	 * Description: AES解密
	 * </p>
	 * <p>
	 * 
	 * </p>
	 * 
	 * @param sSrc
	 *            要解密的加密后字串
	 * @param sKey
	 *            解密秘钥
	 * @return 返回解密后的结果
	 * @throws Exception
	 * 
	 * @author xwc1125
	 * @date 2015-7-20下午1:47:06
	 */
	public static String Decrypt(String sSrc, String sKey) throws Exception {
		try {
			if (sSrc == null || sSrc.length() == 0 || sSrc.trim().length() == 0) {
				return null;
			}
			if (sKey == null) {
				throw new Exception("decrypt key is null");
			}
			if (sKey.length() != 16) {
				throw new Exception("decrypt key length error");
			}
			byte[] raw = sKey.getBytes(charset);
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			byte[] encrypted1 = Base64.decode(sSrc);
			try {
				byte[] original = cipher.doFinal(encrypted1);
				String originalString = new String(original, charset);
				return originalString;
			} catch (Exception e) {
				throw new Exception("decrypt errot", e);
			}
		} catch (Exception ex) {
			throw new Exception("decrypt errot", ex);
		}
	}

/*	public static String Decrypt(String sSrc, String sKey, int tag)
			throws Exception {
		String key = SecurityKeyUtils.getKeyValue(sKey, tag);
		return Decrypt(sSrc, key);
	}
*/
	public static byte[] hex2byte(String strhex) {
		if (strhex == null) {
			return null;
		}
		int l = strhex.length();
		if (l % 2 == 1) {
			return null;
		}
		byte[] b = new byte[l / 2];
		for (int i = 0; i != l / 2; i++) {
			b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2),
					16);
		}
		return b;
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	public static byte[] Decrypt(byte[] text, byte[] key) throws Exception {
		SecretKeySpec aesKey = new SecretKeySpec(key, "AES");
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, aesKey);
		return cipher.doFinal(text);
	}
	
	/**
	 *<p> Title: EncryptCbcIv</br>
	 *<p>Description:  aes 带向量的加密 </br>
	 * @param 
	 * @return String
	 * @author 
	 * @version 2018年12月4日
	 */
	public static String EncryptCbcIv(String sSrc, String sKey,String ivStr) throws Exception {
		try {
			if (sSrc == null || sSrc.length() == 0 || sSrc.trim().length() == 0) {
				return null;
			}
			if (sKey == null) {
				throw new Exception("encrypt key is null");
			}
			if (sKey.length() != 16) {
				throw new Exception("encrypt key length error");
			}
			if (ivStr.length() != 16) {
				throw new Exception(" iv encrypt key length error");
			}
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(charset), "AES");
			IvParameterSpec iv= new IvParameterSpec(ivStr.getBytes(charset));//new IvParameterSpec(getIV());
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypt = cipher.doFinal(sSrc.getBytes("utf-8"));
			return Base64.encode(encrypt);
		} catch (Exception ex) {
			throw new Exception("decrypt errot", ex);
		}
	}
	
	/**
	 * 
	 *<p> Title: DecryptCbcIv</br>
	 *<p>Description: aes带向量的解密 </br>
	 * @param 
	 * @return String
	 * @author 
	 * @version 2018年12月4日
	 */
	public static String DecryptCbcIv(String sSrc, String sKey,String ivStr) throws Exception {
		try {
			if (sSrc == null || sSrc.length() == 0 || sSrc.trim().length() == 0) {
				return null;
			}
			if (sKey == null) {
				throw new Exception("decrypt key is null");
			}
			if (sKey.length() != 16) {
				throw new Exception("decrypt key length error");
			}
			if (ivStr.length() != 16) {
				throw new Exception(" iv decrypt key length error");
			}
			byte[]Decrypt = Base64.decode(sSrc);
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			SecretKeySpec skeySpec = new SecretKeySpec(sKey.getBytes(charset), "AES");
			IvParameterSpec iv= new IvParameterSpec(ivStr.getBytes(charset));//new IvParameterSpec(getIV());
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);//使用解密模式初始化 密
			byte[] decrypt = cipher.doFinal(Decrypt);
			return new String (decrypt,charset);
		} catch (Exception ex) {
			throw new Exception("decrypt errot", ex);
		}
	}
}
