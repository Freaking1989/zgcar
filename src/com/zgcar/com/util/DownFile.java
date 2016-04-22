package com.zgcar.com.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

public class DownFile {

	/**
	 * @param path
	 *            下载地址
	 * @param downFile
	 *            本地接收文件
	 * @return
	 */
	public static boolean downFile(String path, File downFile) {
		try {
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setDefaultUseCaches(true);
			httpURLConnection.setDoOutput(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();
			int length = httpURLConnection.getContentLength();
			if (httpURLConnection.getResponseCode() == 200) {
				InputStream inputStream = httpURLConnection.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				OutputStream outputStream = new FileOutputStream(downFile);
				byte[] buffer = new byte[1024];
				int b;

				while ((b = bis.read(buffer, 0, 1024)) != -1) {
					Log.e("DownFile", "源文件长度：" + length);
					outputStream.write(buffer, 0, b);
				}
				inputStream.close();
				outputStream.flush();
				outputStream.close();
				httpURLConnection.disconnect();

				Log.e("DownFile", "下载下的文件长度：" + downFile.length());
				if (length == -1) {
					Log.e("下载成功", "12");
					return true;
				}
				if (length == downFile.length()) {
					Log.e("下载成功", "12");
					return true;
				}
				return false;
			} else {
				return false;
			}

		} catch (MalformedURLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}

	public static boolean upDateApp(String path, File downFile,
			OnDownLoadLisener onDownLoadLisener) {
		try {
			Log.e("gengxin", "下载地址：" + path);
			URL url = new URL(path);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url
					.openConnection();
			httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
			httpURLConnection.setConnectTimeout(5000);
			httpURLConnection.setDefaultUseCaches(true);
			httpURLConnection.setDoOutput(false);
			httpURLConnection.setDoInput(true);
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.connect();
			int length = httpURLConnection.getContentLength();
			Log.e("gengxin", httpURLConnection.getResponseCode() + "");
			if (httpURLConnection.getResponseCode() == 200) {
				InputStream inputStream = httpURLConnection.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				OutputStream outputStream = new FileOutputStream(downFile);
				byte[] buffer = new byte[1024];
				int b;
				while ((b = bis.read(buffer, 0, 1024)) != -1) {
					outputStream.write(buffer, 0, b);
					int a = (int) (downFile.length() * 100 / length);
					Log.e("DownFile",
							"源文件长度：" + length + "已下载：" + downFile.length());
					onDownLoadLisener.update(a + "%");
				}
				inputStream.close();
				outputStream.flush();
				outputStream.close();
				httpURLConnection.disconnect();

				Log.e("DownFile", "下载下的文件长度：" + downFile.length());
				if (length == -1) {
					Log.e("下载成功", "12");
					return true;
				}
				if (length == downFile.length()) {
					onDownLoadLisener.downSucceed();
					Log.e("下载成功", "12");
					return true;
				}
				onDownLoadLisener.downFailed();
				return false;
			} else {
				onDownLoadLisener.downFailed();
				return false;
			}

		} catch (MalformedURLException e) {
			onDownLoadLisener.downFailed();
			return false;
		} catch (IOException e) {
			onDownLoadLisener.downFailed();
			return false;
		}
	}

	public interface OnDownLoadLisener {

		public void update(String str);

		public void downSucceed();

		public void downFailed();

	}

}
