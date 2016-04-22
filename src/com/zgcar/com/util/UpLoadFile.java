package com.zgcar.com.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

/**
 * 上传文件
 * 
 */
public class UpLoadFile {

	/**
	 * @param file
	 *            需要上传的文件
	 * @param requestURL
	 *            上传路径
	 * @return 成功true，失败false
	 */
	public synchronized static boolean toUploadFile(File file, String requestURL) {
		try {
			URL url = new URL(requestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(30000);
			conn.setConnectTimeout(3000);
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Connection", "keep-alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			/** 上传文件 */
			InputStream is = new FileInputStream(file);
			byte[] bytes = new byte[1024];
			int len = 0;
			Log.e("tag", file.length() + "");
			while ((len = is.read(bytes, 0, 1024)) != -1) {
				dos.write(bytes, 0, len);
			}
			is.close();
			dos.flush();
			dos.close();
			int res = conn.getResponseCode();
			Log.e("tag", "response code:" + res);
			if (res == 200) {
				Log.e("tag", "request success");
				return true;
			} else {
				return false;
			}
		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

}
