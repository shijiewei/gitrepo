package com.zzx.util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

/**
 * @author administrator
 * 
 *         Http Client工具类 发起http client 请求
 * 
 *         窗口 － 首选项 － Java － 代码样式 － 代码模板
 */

public class HttpClientUtil {

	private static Logger LOGGER = Logger.getLogger(HttpClientUtil.class);
	private static final String TYPE_STRING = "string";

	private static final String TYPE_BYTEARRAY = "byte[]";

	private static final int CONNECT_TIMEOUT = 100000; // 连接超时1s
	private static final int READ_TIMEOUT = 200000; // 读取超时2s

	private static HttpClientUtil instance = null;

	private HttpClientUtil() {
	}

	public static HttpClientUtil getInstance() {
		if (instance == null) {
			instance = new HttpClientUtil();
		}
		return instance;
	}

	/**
	 * 请求编码，默认使用GBK
	 */
	@SuppressWarnings("unused")
	private static String urlCharset = "utf-8";

	// /**
	// * @param urlCharset
	// * 要设置的 urlCharset。
	// */
	// public void setUrlCharset(String urlCharset) {
	// this.urlCharset = urlCharset;
	// }

	/**
	 * 获取字符串型返回结果，通过发起http post请求
	 * 
	 * @param targetUrl
	 * @param String
	 *            requestContent
	 * @return
	 * @throws Exception
	 */
	public String getResponseBodyAsString(String targetUrl,
			String requestContent, String charset) throws Exception {

		return (String) setPostRequest(targetUrl, TYPE_STRING, requestContent,
				charset);
	}

	/**
	 * 获取字符串型返回结果，通过发起http post请求
	 * 
	 * @param targetUrl
	 * @param String
	 *            requestContent
	 * @return
	 * @throws Exception
	 */
	public String getResponseBodyAsString(String targetUrl,
			String requestContent, Map<String, String> headers, String charset)
			throws Exception {
		return (String) setPostRequest(targetUrl, TYPE_STRING, requestContent,
				headers, charset);
	}

	 /**
     * 获取字符串型返回结果，通过发起http post请求
     * @param targetUrl
     * @param String requestContent
     * @return
     * @throws Exception
     */
    public String getResponseBodyAsString(String targetUrl,String requestContent,Map<String,String> headers)throws Exception{
         
        return (String)setPostRequest(targetUrl,TYPE_STRING,requestContent,headers);
    }
	
	/**
	 * 获取字符数组型返回结果，通过发起http post请求
	 * 
	 * @param targetUrl
	 * @param String
	 *            requestContent
	 * @return
	 * @throws Exception
	 */
	public byte[] getResponseBodyAsByteArray(String targetUrl,
			String requestContent, String charset) throws Exception {

		return (byte[]) setPostRequest(targetUrl, TYPE_BYTEARRAY,
				requestContent, charset);
	}

	/**
	 * 获取字符串型返回结果，通过发起http post请求
	 * 
	 * @param targetUrl
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public String getResponseBodyAsStringWithMap(String targetUrl, Map params,
			String charset) throws Exception {
		return (String) setPostRequestWithMap(targetUrl, TYPE_STRING, params,
				charset);
	}


	 public String getResponseBodyAsStringWithMap_httpget(String targetUrl,Map params,String charset,Map<String, String> headers)throws Exception{
	      
	      return (String)setGetRequestWithMap(targetUrl,TYPE_STRING,params,charset,headers);
	  }
	
	/**
	 * 获取字符数组型返回结果，通过发起http post请求
	 * 
	 * @param targetUrl
	 * @param params
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public byte[] getResponseBodyAsByteArrayWithMap(String targetUrl,
			Map params, String charset) throws Exception {
		return (byte[]) setPostRequestWithMap(targetUrl, TYPE_BYTEARRAY,
				params, charset);
	}

	   /**
     * 利用http client模拟发送http post请求
     * @param targetUrl                 请求地址
     * @param params                    请求参数<paramName,paramValue>
     * @return  Object                  返回的类型可能是：String 或者 byte[] 或者 outputStream           
     * @throws Exception
     */
    private Object setPostRequest(String targetUrl,String responseType,String requestContent,Map<String,String> headers)throws Exception{
        if(StringUtils.isBlank(targetUrl)){
            throw new IllegalArgumentException("调用HttpClientUtil.setPostRequest方法，targetUrl不能为空!");
        }

        Object responseResult = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();;
        CloseableHttpResponse response = null;

        try{
        	LOGGER.info("Request url:" + targetUrl +" data:" + requestContent);

            HttpPost httppost = new HttpPost(targetUrl);
            //设置超时
        	RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();//设置请求和传输超时时间
        	httppost.setConfig(requestConfig);
            
            StringEntity entity = new StringEntity(requestContent,"GBK");//解决中文乱码问题    
            entity.setContentEncoding("GBK");    
            //entity.setContentType("application/json");    
            httppost.setEntity(entity);  
            

//        	HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
            for (Map.Entry<String, String> entry : headers.entrySet()) {  
                httppost.addHeader(entry.getKey(), entry.getValue());
                LOGGER.info("header :" + entry.getKey() +"  "+entry.getValue());
            }
            
//            Header h1 = httppost.getFirstHeader("Authorization");  
//            System.out.println(h1);  
//            Header h2 = httppost.getLastHeader("Accept");  
//            System.out.println(h2); 
            
            response = httpClient.execute(httppost);
            //StatusLine state = response.getStatusLine();
            //if (state.getStatusCode() == HttpStatus.SC_OK){         	
            //}
                        
            //根据指定类型进行返回
            if(StringUtils.equals(TYPE_STRING,responseType)){
                responseResult = EntityUtils.toString(response.getEntity(),"GBK");
                
                LOGGER.info("resp data:" + (String)responseResult);
            }else if(StringUtils.equals(TYPE_BYTEARRAY,responseType)){
                responseResult = EntityUtils.toByteArray(response.getEntity());
                LOGGER.info("resp data:" + new String((byte[])responseResult));
            }
            
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("setPostRequest Exception:",e);
        }finally{
            //释放链接
        	response.close();
        	httpClient.close();

        }
         
        return responseResult;
    }
	
	
	/**
	 * 利用http client模拟发送http post请求
	 * 
	 * @param targetUrl
	 *            请求地址
	 * @param params
	 *            请求参数<paramName,paramValue>
	 * @return Object 返回的类型可能是：String 或者 byte[] 或者 outputStream
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private Object setPostRequest(String targetUrl, String responseType,
			String requestContent, String charset) throws Exception {
		if (charset == null) {
			charset = HTTP.UTF_8;
		}
		if ("".equals(targetUrl)) {
			throw new IllegalArgumentException(
					"调用HttpClientUtil.setPostRequest方法，targetUrl不能为空!");
		}

		Object responseResult = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		;
		CloseableHttpResponse response = null;
		try {
			/*LOGGER.info("\n" + "Request url:" + targetUrl + " data:"
					+ requestContent);*/
			HttpPost httppost = new HttpPost(targetUrl);
			// 设置超时
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(READ_TIMEOUT)
					.setConnectTimeout(CONNECT_TIMEOUT).build();// 设置请求和传输超时时间
			httppost.setConfig(requestConfig);

			StringEntity entity = new StringEntity(requestContent, charset);// 解决中文乱码问题
			entity.setContentEncoding(charset);
			httppost.setEntity(entity);
			response = httpClient.execute(httppost);
			// 根据指定类型进行返回
			if (TYPE_STRING.equals(responseType)) {
				responseResult = EntityUtils.toString(response.getEntity(),
						charset);

				/*LOGGER.info("\n" + "resp data:" + (String) responseResult);*/
			} else if (TYPE_BYTEARRAY.equals(responseType)) {
				responseResult = EntityUtils.toByteArray(response.getEntity());
				/*LOGGER.info("\n" + "resp data:"
						+ new String((byte[]) responseResult));*/
			}
		} catch (Exception e) {
			/*LOGGER.error("\n" + "setPostRequest Exception:", e);*/
		} finally {
			// 释放链接
			response.close();
			httpClient.close();
		}
		return responseResult;
	}

	/**
	 * 利用http client模拟发送http post请求
	 * 
	 * @param targetUrl
	 *            请求地址
	 * @param params
	 *            请求参数<paramName,paramValue>
	 * @return Object 返回的类型可能是：String 或者 byte[] 或者 outputStream
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private Object setPostRequest(String targetUrl, String responseType,
			String requestContent, Map<String, String> headers, String charset)
			throws Exception {
		if (charset == null) {
			charset = HTTP.UTF_8;
		}
		if ("".equals(targetUrl)) {
			throw new IllegalArgumentException(
					"调用HttpClientUtil.setPostRequest方法，targetUrl不能为空!");
		}
		Object responseResult = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		;
		CloseableHttpResponse response = null;
		try {
			/*LOGGER.info("Request url:" + targetUrl + " data:" + requestContent);*/
			HttpPost httppost = new HttpPost(targetUrl);
			// 设置超时
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(READ_TIMEOUT)
					.setConnectTimeout(CONNECT_TIMEOUT).build();// 设置请求和传输超时时间
			httppost.setConfig(requestConfig);

			StringEntity entity = new StringEntity(requestContent, charset);// 解决中文乱码问题
			entity.setContentEncoding(charset);
			httppost.setEntity(entity);

			for (Map.Entry<String, String> entry : headers.entrySet()) {
				httppost.addHeader(entry.getKey(), entry.getValue());
				LOGGER.info("header :" + entry.getKey() + "  "
						+ entry.getValue());
			}
			response = httpClient.execute(httppost);
			// 根据指定类型进行返回
			if (TYPE_STRING.equals(responseType)) {
				responseResult = EntityUtils.toString(response.getEntity(),
						charset);
				/*LOGGER.info("resp data:" + (String) responseResult);*/
			} else if (TYPE_BYTEARRAY.equals(responseType)) {
				responseResult = EntityUtils.toByteArray(response.getEntity());
				/*LOGGER.info("resp data:" + new String((byte[]) responseResult));*/
			}
		} catch (Exception e) {
			/*LOGGER.error("setPostRequest Exception:", e);*/
		} finally {
			// 释放链接
			response.close();
			httpClient.close();
		}
		return responseResult;
	}

	/**
	 * 利用http client模拟发送http post请求
	 * 
	 * @param targetUrl
	 *            请求地址
	 * @param params
	 *            请求参数<paramName,paramValue>
	 * @return Object 返回的类型可能是：String 或者 byte[] 或者 outputStream
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unused", "deprecation" })
	private Object setPostRequestWithMap(String targetUrl, String responseType,
			Map params, String charset) throws Exception {
		if (charset == null) {
			charset = HTTP.UTF_8;
		}
		if ("".equals(targetUrl)) {
			throw new IllegalArgumentException(
					"调用HttpClientUtil.setPostRequest方法，targetUrl不能为空!");
		}

		Object responseResult = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		CloseableHttpResponse response = null;

		NameValuePair[] nameValuePairArr = null;
		UrlEncodedFormEntity entity = null;
		try {
			HttpPost httppost = new HttpPost(targetUrl);
			// 设置超时
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(READ_TIMEOUT)
					.setConnectTimeout(CONNECT_TIMEOUT).build();// 设置请求和传输超时时间
			httppost.setConfig(requestConfig);
			if (params != null) {
				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				Set key = params.keySet();
				Iterator keyIte = key.iterator();
				while (keyIte.hasNext()) {
					Object paramName = keyIte.next();
					Object paramValue = params.get(paramName);
					if (paramName instanceof String
							&& paramValue instanceof String) {
						nvps.add(new BasicNameValuePair((String) paramName,
								(String) paramValue));
					}
				}
				entity = new UrlEncodedFormEntity(nvps);
				entity.setContentEncoding(charset);
				httppost.setEntity(entity);
			}
			/*LOGGER.info("\n" + "Request url:" + targetUrl + " data:"
					+ entity.toString());*/
			response = httpClient.execute(httppost);
			// 根据指定类型进行返回
			if (TYPE_STRING.equals(responseType)) {
				responseResult = EntityUtils.toString(response.getEntity(),
						charset);

				/*LOGGER.info("\n" + "resp data:" + (String) responseResult);*/
			} else if (TYPE_BYTEARRAY.equals(responseType)) {
				responseResult = EntityUtils.toByteArray(response.getEntity());
				/*LOGGER.info("\n" + "resp data:"
						+ new String((byte[]) responseResult));*/
			}
		} catch (Exception e) {
			/*LOGGER.error("\n" + "setPostRequestWithMap Exception:", e);*/
			e.printStackTrace();
		} finally {
			// 释放链接
			if(response != null){
				response.close();
			}
			if(httpClient != null){
				httpClient.close();
			}
		}
		return responseResult;
	}

	/**
	 * 利用http client模拟发送http post请求
	 * 
	 * @param targetUrl
	 *            请求地址
	 * @param params
	 *            请求参数<paramName,paramValue>
	 * @return Object 返回的类型可能是：String 或者 byte[] 或者 outputStream
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private Object setGetRequestWithMap(String targetUrl, String responseType,
			Map<String, String> params, String charset) throws Exception {
		if (charset == null)
			charset = HTTP.UTF_8;
		if ("".equals(targetUrl)) {
			throw new IllegalArgumentException(
					"调用HttpClientUtil.setPostRequest方法，targetUrl不能为空!");
		}
		Object responseResult = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		;
		CloseableHttpResponse response = null;

		HttpGet httpget = null;
		String url = targetUrl;
		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> pairs = new ArrayList<NameValuePair>(
						params.size());
				for (Map.Entry<String, String> entry : params.entrySet()) {
					String value = entry.getValue();
					if (value != null) {
						pairs.add(new BasicNameValuePair(entry.getKey(), value));
					}
				}
				url += "?"
						+ EntityUtils.toString(new UrlEncodedFormEntity(pairs,
								charset));
			}
			httpget = new HttpGet(url);
			// 设置超时
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(READ_TIMEOUT)
					.setConnectTimeout(CONNECT_TIMEOUT).build();// 设置请求和传输超时时间
			httpget.setConfig(requestConfig);
			/*LOGGER.info("\n" + "Request url:" + url);*/
			response = httpClient.execute(httpget);
			// 根据指定类型进行返回
			if (TYPE_STRING.equals(responseType)) {
				responseResult = EntityUtils.toString(response.getEntity(),
						charset);
				/*LOGGER.info("\n" + "resp data:" + (String) responseResult);*/
			} else if (TYPE_BYTEARRAY.equals(responseType)) {
				responseResult = EntityUtils.toByteArray(response.getEntity());
				/*LOGGER.info("\n" + "resp data:"
						+ new String((byte[]) responseResult));*/
			}

		} catch (Exception e) {
			e.printStackTrace();
			/*LOGGER.error("\n" + "setGetRequestWithMap Exception:", e);*/
		} finally {
			// 释放链接
			response.close();
			httpClient.close();
		}
		return responseResult;
	}
	
	/**
	 * 
	 * <p>Title: requestByGetMethod</br>
	 * <p>Description: TODO(模拟发送http get 请求)</br>
	 * @tags  @param url
	 * @tags  @return
	 */
	public String requestByGetMethod(String url){
        //创建默认的httpClient实例
        CloseableHttpClient httpClient =HttpClients.createDefault();
        try {
            //用get方法发送http请求
            HttpGet get = new HttpGet(url);
            System.out.println("执行get请求:"+get.getURI());
            CloseableHttpResponse httpResponse = null;
            //发送get请求
            httpResponse = httpClient.execute(get);
            try{
                //response实体
                HttpEntity entity = httpResponse.getEntity();
                if (null != entity){
                	return EntityUtils.toString(entity);                    
                }
            }
            finally{
                httpResponse.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally{
            try{
            	httpClient.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
		return null;
 
    }
	
	   /**
     * 利用http client模拟发送http post请求
     * @param targetUrl                 请求地址
     * @param params                    请求参数<paramName,paramValue>
     * @param headers
     * @return  Object                  返回的类型可能是：String 或者 byte[] 或者 outputStream           
     * @throws Exception
     */
    private Object setGetRequestWithMap(String targetUrl,String responseType,Map<String,String> params,String charset,Map<String, String> headers)throws Exception{
        if(StringUtils.isBlank(targetUrl)){
            throw new IllegalArgumentException("调用HttpClientUtil.setPostRequest方法，targetUrl不能为空!");
        }

        Object responseResult = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();;
        CloseableHttpResponse response = null;

        NameValuePair[] nameValuePairArr = null;
        UrlEncodedFormEntity entity = null;
        HttpGet httpget = null;
        String url = targetUrl ;
        try{
        	//LOGGER.info("Request url:" + targetUrl +" data:" + requestContent);

            
            //StringEntity entity = new StringEntity(requestContent,"GBK");//解决中文乱码问题    
            //entity.setContentEncoding("GBK");    
            //entity.setContentType("application/json");    
            //httppost.setEntity(entity);  
        	
        	if(params != null && !params.isEmpty()){
                List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
                for(Map.Entry<String,String> entry : params.entrySet()){
                    String value = entry.getValue();
                    if(value != null){
                        pairs.add(new BasicNameValuePair(entry.getKey(),value));
                    }
                }
                url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, charset));
            }
            
            /*
            if(params != null){
       
                List <NameValuePair> nvps = new ArrayList <NameValuePair>();
     
                Set key = params.keySet();
                Iterator keyIte = key.iterator();
                while(keyIte.hasNext()){
                    Object paramName = keyIte.next();
                    Object paramValue = params.get(paramName);
                    if(paramName instanceof String && paramValue instanceof String){
                    	nvps.add(new BasicNameValuePair((String)paramName,(String)paramValue));
                    }
                }
                LOGGER.info("nvps:" + nvps.size() +  "  data:" +nvps.toString());
                entity = new UrlEncodedFormEntity(nvps);
                entity.setContentEncoding("GBK");   
             */   
         

                
            //}
        	 	
            httpget  = new HttpGet(url);   
            
            for (Map.Entry<String, String> entry : headers.entrySet()) {  
                System.out.println("key = " + entry.getKey() + " and value = " + entry.getValue());  
                httpget.addHeader(entry.getKey(), entry.getValue());
            }
            
            //设置超时
        	RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();//设置请求和传输超时时间
        	httpget.setConfig(requestConfig);
            //LOGGER.info("Request url:" + targetUrl +" data:" + entity.toString());
            LOGGER.info("Request url:" + url );
            

            response = httpClient.execute(httpget);
            
            StatusLine state = response.getStatusLine();
            LOGGER.info("resp state:" + state);
            
            // 请求结束，返回结果  
            //responseResult = EntityUtils.toString(response.getEntity()); 
            
           
            
            //根据指定类型进行返回
            if(StringUtils.equals(TYPE_STRING,responseType)){
                responseResult = EntityUtils.toString(response.getEntity(),charset);
                
                LOGGER.info("resp data:" + (String)responseResult);
            }else if(StringUtils.equals(TYPE_BYTEARRAY,responseType)){
                responseResult = EntityUtils.toByteArray(response.getEntity());
                
                LOGGER.info("resp data:" + new String((byte[])responseResult));
            }
            
        }catch(Exception e){
            e.printStackTrace();
            LOGGER.error("setGetRequestWithMap Exception:",e);
        }finally{
            //释放链接
        	response.close();
        	httpClient.close();

        }
         
        return responseResult;
    } 
	
    public String getResponseBodyAsStringWithMap_httpget(String targetUrl,Map params)throws Exception{
        
        return (String)setGetRequestWithMap(targetUrl,TYPE_STRING,params);
    }  
    
  public String getResponseBodyAsStringWithMap_httpget(String targetUrl,Map params,String charset)throws Exception{
        
        return (String)setGetRequestWithMap(targetUrl,TYPE_STRING,params,charset);
    }
  
  /**
   * 利用http client模拟发送http post请求
   * @param targetUrl                 请求地址
   * @param params                    请求参数<paramName,paramValue>
   * @return  Object                  返回的类型可能是：String 或者 byte[] 或者 outputStream           
   * @throws Exception
   */
  private Object setGetRequestWithMap(String targetUrl,String responseType,Map<String,String> params)throws Exception{
      if(StringUtils.isBlank(targetUrl)){
          throw new IllegalArgumentException("调用HttpClientUtil.setPostRequest方法，targetUrl不能为空!");
      }

      Object responseResult = null;
      CloseableHttpClient httpClient = HttpClients.createDefault();;
      CloseableHttpResponse response = null;

      NameValuePair[] nameValuePairArr = null;
      UrlEncodedFormEntity entity = null;
      HttpGet httpget = null;
      String url = targetUrl ;
      try{
      	//LOGGER.info("Request url:" + targetUrl +" data:" + requestContent);

          
          //StringEntity entity = new StringEntity(requestContent,"GBK");//解决中文乱码问题    
          //entity.setContentEncoding("GBK");    
          //entity.setContentType("application/json");    
          //httppost.setEntity(entity);  
      	
      	if(params != null && !params.isEmpty()){
              List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());
              for(Map.Entry<String,String> entry : params.entrySet()){
                  String value = entry.getValue();
                  if(value != null){
                      pairs.add(new BasicNameValuePair(entry.getKey(),value));
                  }
              }
              url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs, "GBK"));
          }
          
          /*
          if(params != null){
     
              List <NameValuePair> nvps = new ArrayList <NameValuePair>();
   
              Set key = params.keySet();
              Iterator keyIte = key.iterator();
              while(keyIte.hasNext()){
                  Object paramName = keyIte.next();
                  Object paramValue = params.get(paramName);
                  if(paramName instanceof String && paramValue instanceof String){
                  	nvps.add(new BasicNameValuePair((String)paramName,(String)paramValue));
                  }
              }
              LOGGER.info("nvps:" + nvps.size() +  "  data:" +nvps.toString());
              entity = new UrlEncodedFormEntity(nvps);
              entity.setContentEncoding("GBK");   
           */   
       

              
          //}
      	 	
          httpget  = new HttpGet(url);   
          
          //设置超时
      	RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(READ_TIMEOUT).setConnectTimeout(CONNECT_TIMEOUT).build();//设置请求和传输超时时间
      	httpget.setConfig(requestConfig);
          //LOGGER.info("Request url:" + targetUrl +" data:" + entity.toString());
          LOGGER.info("Request url:" + url );
          response = httpClient.execute(httpget);
          
          //StatusLine state = response.getStatusLine();
          //if (state.getStatusCode() == HttpStatus.SC_OK){         	
          //}
          
          // 请求结束，返回结果  
          //responseResult = EntityUtils.toString(response.getEntity()); 
          
         
          
          //根据指定类型进行返回
          if(StringUtils.equals(TYPE_STRING,responseType)){
              responseResult = EntityUtils.toString(response.getEntity(),"GBK");
              
              LOGGER.info("resp data:" + (String)responseResult);
          }else if(StringUtils.equals(TYPE_BYTEARRAY,responseType)){
              responseResult = EntityUtils.toByteArray(response.getEntity());
              
              LOGGER.info("resp data:" + new String((byte[])responseResult));
          }
          
      }catch(Exception e){
          e.printStackTrace();
          LOGGER.error("setGetRequestWithMap Exception:",e);
      }finally{
          //释放链接
      	response.close();
      	httpClient.close();

      }
       
      return responseResult;
  }    
  
  
    
}
