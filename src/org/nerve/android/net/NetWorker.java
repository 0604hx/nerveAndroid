package org.nerve.android.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * @项目名称 :nerveAndroid
 * @文件名称 :NetService.java
 * @所在包 :org.nerve.android.net
 * @功能描述 :
 *
 * @创建者 :集成显卡	1053214511@qq.com
 * @公司：IBM GDC (http://www.ibm.com/)
 * @创建日期 :2013-6-10
 * @修改记录 :
 */
public class NetWorker {
	
	public static final String TAG = "NetService";
	private String encoding = "utf-8";

	/**
	 * 没有连接到网络
	 */
	public static final String NO_NET = "--NO_NET--";
	
	public NetWorker(){
		
	}
	
	public NetWorker(String encode){
		this.encoding = encode;
	}
	
	/**
	 * 判断当前网络是否可用
	 *	@param context
	 *	@return
	 */
	public static boolean isConnect(Context context) { 
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {
				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
		return false;
    }
	
	/**
	 * @方法名称 :postData
	 * @功能描述 : 统一的提交数据到服务器
	 * @返回值类型 :String
	 *	@param entity
	 *	@return
	 */
	public String postData(String location, String entity){
		HttpURLConnection conn = null;
		try{
			URL url = new URL(location);

			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "text/xml; charset=" + encoding);
			
			OutputStream outStream = conn.getOutputStream();
			
			outStream.write(entity.getBytes());
			
			outStream.flush();
			outStream.close();
			
			StringBuilder sb = new StringBuilder();
			if (conn.getResponseCode() == 200) {
				InputStream responseStream = conn.getInputStream();

				String line = "";
				BufferedReader br = new BufferedReader(new InputStreamReader(responseStream));
				
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
			}
			
			return sb.toString();
		}catch(Exception e){
			e.printStackTrace(System.out);
			return "ERROR";
		}finally{
			if(conn != null)
				conn.disconnect();
		}
	}
	
    /**
     * MAP类型数组转换成NameValuePair类型
     * @param properties  MAP类型数组
     * @return NameValuePair类型数组
     */
    private static List<NameValuePair> generatNameValuePair(Map<String, String> properties) {
       List<NameValuePair> params = new ArrayList<NameValuePair>();
       
       for(Map.Entry<String, String> e : properties.entrySet()){
    	   NameValuePair v = new BasicNameValuePair(e.getKey(), e.getValue());
    	   params.add(v);
       }
       
       return params;
    }
	
    
    /**
     * post 数据到指定的服务器
     *	@param url	服务器地址
     *	@param map	参数表
     *	@return
     */
	public String postData(String url, Map<String, String> map){
		String resultString = "";
		
		HttpPost post = new HttpPost(url);
		try{
			post.setEntity(new UrlEncodedFormEntity(generatNameValuePair(map), encoding));
			
			HttpClient client = new DefaultHttpClient();
			
			HttpResponse resp = client.execute(post);
			
			resultString = EntityUtils.toString(resp.getEntity());
			
		}catch(Exception e){
			e.printStackTrace(System.out);
		}
		
		return resultString;
	}
	
	/**
	 * 获取服务器数据
	 *	@param url
	 *	@return
	 */
	public String getData(String url){
		Log.i(TAG, "get data:" + url);
		HttpGet get = new HttpGet(url);
		String result = "";
		try{
			HttpClient client = new DefaultHttpClient();
			
			HttpResponse resp = client.execute(get);
			
			if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
				result = EntityUtils.toString(resp.getEntity(), encoding);
			}else{
				result = EntityUtils.toString(resp.getEntity(), encoding);
			}
			
		}catch(Exception e){
			Log.e(TAG, " getData 出错，url="+ url +" , message = " + e.getMessage());
			e.printStackTrace(System.out);
		}
		
		return result;
	}
}