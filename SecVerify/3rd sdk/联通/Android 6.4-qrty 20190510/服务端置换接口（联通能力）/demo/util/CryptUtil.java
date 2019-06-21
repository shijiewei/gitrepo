package com.zzx.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptUtil {
	private static final String desAlgorithm = "DESede/CBC/NoPadding";

	private static final String desKeyAlgorithm = "DESede";

	private static final char DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static final byte defaultIV[] = new byte[] { '0', '0', '0', '0', '0', '0', '0', '0' };

	public CryptUtil() {
	}

	private static SecretKey KeyGenerator(String keyStr) {
		byte input[] = null;
		try {
			input = md5Hex(keyStr).substring(0, 24).getBytes("GBK");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		SecretKey triDesKey = new SecretKeySpec(input, desKeyAlgorithm);

		return triDesKey;
	}

	public static String encryptBy3DesAndBase64(String content, String keyStr) {
		return encryptBy3DesAndBase64(content, keyStr, "UTF-8");
	}

	public static String decryptBy3DesAndBase64(String content, String keyStr) {
		return decryptBy3DesAndBase64(content, keyStr, "UTF-8");
	}

	public static String encryptBy3DesAndBase64(String content, String keyStr, String encoding) {
		byte output[] = null;
		byte input[] = null;
		try {
			int contentByteNum = content.getBytes(encoding).length;
			int residue = contentByteNum % 8;
			
			if (0 != residue) {
				int padLen = 8 - residue;
				StringBuffer strBuf = new StringBuffer(content);
				for (int i = 0; i < padLen; i++) {
					strBuf.append(' ');
				}
				input = (new String(strBuf)).getBytes(encoding);
			} else {
				input = content.getBytes(encoding);
			}
			output = encryptBy3Des(input, keyStr);
			return Base64.encode(output).replaceAll("[\\n\\r]", "");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decryptBy3DesAndBase64(String content, String keyStr, String encoding) {
		byte output[] = null;
		byte input[] = null;
		try {
			input = Base64.decode(content);
			output = decryptBy3Des(input, keyStr);
			String retStr = new String(output, encoding);
			return (retStr.trim());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] encryptBy3Des(byte content[], String keyStr) {
		return cryptBy3Des(keyStr, 1, null, content);
	}

	public static byte[] decryptBy3Des(byte content[], String keyStr) {
		return cryptBy3Des(keyStr, 2, null, content);
	}

	public static byte[] cryptBy3Des(String keyStr, int cryptModel, byte iv[], byte content[]) {
		Cipher cipher = null;
		SecretKey key = KeyGenerator(keyStr);
		IvParameterSpec IVSpec = iv == null ? IvGenerator(defaultIV) : IvGenerator(iv);
		try {
			cipher = Cipher.getInstance(desAlgorithm);
			cipher.init(cryptModel, key, IVSpec);
			return cipher.doFinal(content);
		} catch (NoSuchAlgorithmException e0) {
			e0.printStackTrace();
		} catch (NoSuchPaddingException e1) {
			e1.printStackTrace();
		} catch (InvalidKeyException e2) {
			e2.printStackTrace();
		} catch (InvalidAlgorithmParameterException e3) {
			e3.printStackTrace();
		} catch (IllegalBlockSizeException e4) {
			e4.printStackTrace();
		} catch (BadPaddingException e5) {
			e5.printStackTrace();
		}
		return null;
	}

	public static String md5Hex(String content) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		try {
			md5.update(content.getBytes("GBK"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return new String(encodeHex(md5.digest()));
	}

	public static char[] encodeHex(byte data[]) {
		int len = data.length;
		char out[] = new char[len << 1];
		int i = 0;
		int j = 0;
		for (; i < len; i++) {
			out[j++] = DIGITS[(0xf0 & data[i]) >>> 4];
			out[j++] = DIGITS[0xf & data[i]];
		}
		return out;
	}

	private static IvParameterSpec IvGenerator(byte b[]) {
		IvParameterSpec IV = new IvParameterSpec(b);
		return IV;
	}
}
