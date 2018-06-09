package com.goldingmedia.ethernet;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class HtmlRequest {


	public static HashMap<String, String> getHtmlResult(BasicNameValuePair[] bnvp, String uriAPI) {
		HttpPost httpRequest = new HttpPost(uriAPI);// 建立HTTP POST联机
		List <NameValuePair> params = new ArrayList <NameValuePair>();// Post运作传?变量必须用NameValuePair[]数组储存
		for (int i = 0; i < bnvp.length; i++) {
			params.add(bnvp[i]);
		}
		try {
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));// 发出http请求
			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);// 取得http响应
			HashMap<String, String> ret = new HashMap<String, String>();
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if(statusCode == 200) {
				String retStr = EntityUtils.toString(httpResponse.getEntity());
				Log.i("", "--ret--"+retStr);
				try {
					JSONTokener jsonParser = new JSONTokener(retStr);
					JSONObject retStrJson = (JSONObject) jsonParser.nextValue();
					String status = retStrJson.getString("status");
					if (status.equals("1")) {
						ret.put("netStatus", "s1");
						JSONObject result = retStrJson.getJSONObject("result");
						Iterator<?> iterator = result.keys();
						while (iterator.hasNext()) {
							String key = (String) iterator.next();
							String value = result.optString(key);
							ret.put(key, value);
						}
					} else {
						ret.put("netStatus", "s0");
						return ret;
					}
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			} else {
				ret.put("netStatus", statusCode+"");
			}
			return ret;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static HashMap<String, String> getHtmlResult(String path){// 从后台接口获取付款信息
		try {
			URL url = new URL(path.trim());
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setReadTimeout(10000);// 设置读取超时为10秒
			urlConnection.setConnectTimeout(10000);// 设置连接网络超时为10秒
			urlConnection.setRequestMethod("GET");
			HashMap<String, String> ret = new HashMap<String, String>();
			int statusCode = urlConnection.getResponseCode();
			if(200 == urlConnection.getResponseCode()){
				InputStream is =urlConnection.getInputStream();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int len = 0;
				while(-1 != (len = is.read(buffer))){
					baos.write(buffer,0,len);
					baos.flush();
				}
				String retStr =  baos.toString("utf-8");
				Log.i("", "--ret--"+retStr);
				try {
					JSONTokener jsonParser = new JSONTokener(retStr);
					JSONObject retStrJson = (JSONObject) jsonParser.nextValue();
					String status = retStrJson.getString("status");
					if (status.equals("1")) {
						ret.put("netStatus", "s1");
						JSONObject result = retStrJson.getJSONObject("result");
						Iterator<?> iterator = result.keys();
						while (iterator.hasNext()) {
							String key = (String) iterator.next();
							String value = result.optString(key);
							ret.put(key, value);
						}
					} else {
						ret.put("netStatus", "s0");
						return ret;
					}
				} catch (JSONException ex) {
					ex.printStackTrace();
				}
			} else {
				ret.put("netStatus", statusCode+"");
			}
			return ret;
		}  catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
}