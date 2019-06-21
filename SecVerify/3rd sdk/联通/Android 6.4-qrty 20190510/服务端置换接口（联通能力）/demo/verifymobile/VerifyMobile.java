/**
 *<p> Title: VerifyMobile.java</br>
 *<p>Description: TODO(describe the file) </br>
 * @Copyright: Copyright (c) 2015
 * @author lijn
 * @version 2018年9月5日
 */
package com.zzx.verifymobile;

import java.security.PublicKey;
import java.util.TreeMap;

import javax.crypto.Cipher;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.zzx.getmobile.GetMobile;
import com.zzx.util.AESUtils;
import com.zzx.util.Base64;
import com.zzx.util.HttpClientUtil;
import com.zzx.util.MD5;
import com.zzx.util.RSAHelper;
import com.zzx.util.Uuid16;

/**
 * <p>Title: VerifyMobile</br>
 * <p>Description: TODO(ios认证) </br>
 * @author lijn
 * @version 2018年9月5日
 */
public class VerifyMobile {
	
	private static String pubKey = "";
	
	public static void main(String[] args) {
			String accessCode = "";//令牌
			Boolean result =verifyMobile(accessCode);
			System.out.println("result-->"+result);
		
	}
	
	public  static Boolean verifyMobile(String accesscode) {
		System.out.println("【verifyMobile】");
		try {
			pubKey = "";
			String api = "/api/netm/v1.0/qhbv";
			String url = "http://ms.zzx9.cn" + api;

			// 填写参数
			String apiKey = "";// 鉴权密钥
			String n = "";// 应用名称
			String c = "";// 应用版本号
			String v = "";// 应用版本名称
			String pk = "";// 应用包名（必填）zhangqy
			String paramsKey = "";// 参数密钥，随机生成的16位aes密钥，通过rsa公钥加密
			String params = "";// 参数包体，通过paramsKey加密
			String md5 = "";//MD5信息
			
			String sign = "";

			TreeMap<String, Object> paramsMap = new TreeMap<String, Object>();

			TreeMap<String, String> appMap = new TreeMap<String, String>();
			appMap.put("n", n);// 应用名称
			appMap.put("c", c);// 版本号
			appMap.put("v", v);// 版本名称
			appMap.put("pk", pk);// 应用包名
			appMap.put("md5", md5);// MD5信息 android必填
			TreeMap<String, Object> dataMap = new TreeMap<String, Object>();
			dataMap.put("accessCode", accesscode);//token值
			dataMap.put("mobile", "");//待认证的手机号
			
			paramsMap.put("app", appMap);// 应用信息
			paramsMap.put("data", dataMap);// 业务核心参数

			// 生成一个16位的随机aes密钥，对参数包体进行加密
			Gson gson = new Gson();
			String aesKey = Uuid16.create() + "";
			String iv = Uuid16.create() + "";//偏移量
			params = AESUtils.EncryptCbcIv(gson.toJson(paramsMap), aesKey, iv);
			// 使用rsa公钥对aesKey进行加密
			aesKey = aesKey+iv;
			PublicKey publicKey = RSAHelper.getPublicKey(pubKey);
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] enBytes = cipher.doFinal(aesKey.getBytes());
			paramsKey = Base64.encode(enBytes);
			System.out.println("加密后的密文:" + paramsKey);

			// 创建签名sign
			String _sign = apiKey + api + "?apiKey=" + apiKey + "&params=" + params + "&paramsKey=" + paramsKey;
			System.out.println("验签串：" + _sign);
			sign = MD5.md5(_sign);

			TreeMap<String, Object> p = new TreeMap<String, Object>();
			p.put("apiKey", apiKey);
			p.put("paramsKey", paramsKey);
			p.put("params", params);
			p.put("sign", sign);

			HttpClientUtil http = HttpClientUtil.getInstance();
			String result = http.getResponseBodyAsStringWithMap(url, p, "utf-8");
			System.out.println("【1】" + result);
			Boolean flag = Boolean.FALSE;
			JSONObject json = new JSONObject(result);
			String code = json.get("code")+"";
			String msg = json.get("msg")+"";
			if("0".equals(code)){
				Boolean obj = (Boolean) json.get("obj");
				if(obj){
					System.out.println("认证成功：待手机号码与认证手机号一致");
				}else{
					System.out.println("认证成功：待手机号码与认证手机号不一致");
				}
			}else{
				System.out.println("认证失败：code="+code+",msg="+msg);
			}
			return flag;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

}
