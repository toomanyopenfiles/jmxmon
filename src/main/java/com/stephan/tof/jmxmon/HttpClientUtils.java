package com.stephan.tof.jmxmon;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * update httpclient to HttpUrlConnection<br>
 * 
 * @since 2015-08-17 <br>
 * @author stevenDing <br>
 * 
 */
public class HttpClientUtils {

	private static final int readTimeout = 20000;
	private static final int connTimeout = 5000;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final HttpClientUtils httpClient = new HttpClientUtils();
	private static final int defaultRetryTimes = 3;
	public static final int errorStatusCode = 900;
	public static final int okStatusCode = 200;
	public static final String defaultContentType = "application/json; charset=utf-8";
	public static final String urlencodedContentType = "application/x-www-form-urlencoded";
	
	public static HttpClientUtils getInstance() {
		return httpClient;
	}
	
	public HttpResult post(String url, String content,String contentType){
		
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		HttpResult result = new HttpResult(); 
		int i=0;
		while(true){
			if(i>=defaultRetryTimes || result.getStatusCode()==okStatusCode){
				break;
			}
			i++;
			try{
		        URL postUrl = new URL(url);
		        // 打开连接
		        conn = (HttpURLConnection) postUrl.openConnection();
		    	conn.setConnectTimeout(connTimeout);
		    	conn.setReadTimeout(readTimeout);
		        // 设置是否向connection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true
		        conn.setDoOutput(true);
		        conn.setDoInput(true);
		        conn.setRequestMethod("POST");
		        // Post 请求不能使用缓存
		        conn.setUseCaches(false);
		        // URLConnection.setInstanceFollowRedirects是成员函数，仅作用于当前函数
		        conn.setInstanceFollowRedirects(true);
		        // 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
		        // 意思是正文是urlencoded编码过的form参数，下面我们可以看到我们对正文内容使用URLEncoder.encode进行编码
		        //conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
		        //conn.setRequestProperty("Content-Type", "text/html; charset=utf-8");
		        //conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
		        //conn.setRequestProperty("Content-Type", "text/json; charset=utf-8");
		        //conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
		        if(contentType!=null && contentType.length()>0){
		        	conn.setRequestProperty("Content-Type", contentType);
		        }else{
		        	conn.setRequestProperty("Content-Type", defaultContentType);
		        }
		        // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
		        // 要注意的是connection.getOutputStream会隐含的进行connect。
		        conn.connect();
		        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		        out.write(content.getBytes("UTF-8"));
		
		        out.flush();
		        out.close(); // flush and close
		        reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		        String line;
		        while ((line = reader.readLine()) != null) {
		        	buffer.append(line);
		        }
		        result.setContent(buffer.toString());
		        result.setStatusCode(okStatusCode);
			}catch(MalformedURLException e) {
				logger.error(e.getMessage(),e);
				result.setStatusCode(errorStatusCode);
				result.setT(e);
			}catch (IOException e){
				logger.error(e.getMessage(),e);
				result.setStatusCode(errorStatusCode);
				result.setT(e);
			}catch(Exception e){
				logger.error(e.getMessage(),e);
				result.setStatusCode(errorStatusCode);
				result.setT(e);
			}finally{
				if(reader!=null){
					try{
						reader.close();
					}catch(IOException e){
						logger.error(e.getMessage(),e);
					}
				}
				if(conn!=null){
					conn.disconnect();
				}
	        }
		}
		return result;
	}
	
	public HttpResult post(String url, String content) {
		return post(url, content, defaultContentType);
	}
	
	public class HttpResult {
		private String content;
		private int statusCode = errorStatusCode;
		private Throwable t;

		public Throwable getT() {
			return t;
		}

		public void setT(Throwable t) {
			this.t = t;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public int getStatusCode() {
			return statusCode;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}
		
		@Override
		public String toString() {
			return "statusCode :"+statusCode+" content :"+content;
		}
	}
	
}
