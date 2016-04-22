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
	 *            ���ص�ַ
	 * @param downFile
	 *            ���ؽ����ļ�
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
					Log.e("DownFile", "Դ�ļ����ȣ�" + length);
					outputStream.write(buffer, 0, b);
				}
				inputStream.close();
				outputStream.flush();
				outputStream.close();
				httpURLConnection.disconnect();

				Log.e("DownFile", "�����µ��ļ����ȣ�" + downFile.length());
				if (length == -1) {
					Log.e("���سɹ�", "12");
					return true;
				}
				if (length == downFile.length()) {
					Log.e("���سɹ�", "12");
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
			Log.e("gengxin", "���ص�ַ��" + path);
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
							"Դ�ļ����ȣ�" + length + "�����أ�" + downFile.length());
					onDownLoadLisener.update(a + "%");
				}
				inputStream.close();
				outputStream.flush();
				outputStream.close();
				httpURLConnection.disconnect();

				Log.e("DownFile", "�����µ��ļ����ȣ�" + downFile.length());
				if (length == -1) {
					Log.e("���سɹ�", "12");
					return true;
				}
				if (length == downFile.length()) {
					onDownLoadLisener.downSucceed();
					Log.e("���سɹ�", "12");
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
