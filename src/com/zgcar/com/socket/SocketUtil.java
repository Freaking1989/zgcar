package com.zgcar.com.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.zgcar.com.R;
import com.zgcar.com.entity.FinalVariableLibrary;
import com.zgcar.com.util.ErrorCode;
import com.zgcar.com.util.Util;

public class SocketUtil {

	private static Socket socket;
	private static JSONObject data;
	public static int errorCode;
	private static boolean isClose;

	/**
	 * 连接服务器发送数据
	 * 
	 * @param jsonStr
	 *            需要发送的json数据
	 * @return 服务器连接成功返回true否则返回false
	 */
	public static synchronized boolean connectService(String jsonStr) {
		try {
			Log.e("SocketUtil", "发送的JSON数据:"+jsonStr);
			byte[] buffer = new byte[1024];
			int length = -1;
			String str = null;
			StringBuffer allStr = new StringBuffer();
			InputStream in = null;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					if (socketConnect(i * 3 + j)) {
						break;
					}
				}
				socket.setSoTimeout(5000);
				PrintWriter writer = new PrintWriter(new OutputStreamWriter(
						socket.getOutputStream(), "utf-8"));
				writer.write(jsonStr);
				writer.flush();
				if (!socket.isClosed() && socket.isConnected()
						&& !socket.isInputShutdown()) {
					in = socket.getInputStream();
				}
				while (true) {
					try {
						if (!socket.isClosed() && socket.isConnected()
								&& !socket.isInputShutdown()) {
							length = in.read(buffer);
							if (length == -1) {
								break;
							}
							str = new String(buffer, 0, length, "utf-8");
							allStr = allStr.append(str);
						} else {
							break;
						}
					} catch (Exception e) {
						e.getStackTrace();
						break;
					}
				}
				close(false);
				writer.close();
				in.close();
				if (isClose) {
					Log.e("SocketUtil", "Socket关闭");
					break;
				}
				if (!allStr.toString().equals("")) {
					break;
				}

			}
			String jsonData = allStr.toString();
			Log.e("SocketUtil", "服务端数据" + jsonData);
			data = new JSONObject(jsonData);
			errorCode = data.getInt("ret");
			if (errorCode == 0) {
				return true;
			} else {
				return false;
			}
		} catch (UnknownHostException e) {
			Log.e("SocketUtil", "UnknownHostException");
			return false;
		} catch (IOException e) {
			Log.e("SocketUtil", "IOException");
			return false;
		} catch (JSONException e) {
			Log.e("SocketUtil", "服务器传送的数据不是Json格式");
			return false;
		}
	}

	private static boolean socketConnect(int m) {
		socket = new Socket();
		try {
			Log.e("SocketUtil", "Socket第" + m + "此连接");
			socket.connect(new InetSocketAddress(FinalVariableLibrary.ip,
					FinalVariableLibrary.DSTPORT1), 3000);
		} catch (IOException e2) {
			return false;
		}
		return true;
	}

	public static synchronized String isFail(Context context) {
		if (data != null) {
			String msg = ErrorCode.getErrorMsg(errorCode, context);
			data = null;
			return msg;
		} else {
			if (Util.isConnected(context)) {
				return context.getString(R.string.error_code_default);
			} else {
				return context.getString(R.string.can_not_connect_server);
			}
		}
	}

	/**
	 * 关闭网络连接
	 */
	public static synchronized void close(boolean isClose) {
		SocketUtil.isClose = isClose;
		if (socket != null) {
			try {
				socket.shutdownInput();
				socket.shutdownOutput();
				socket.close();
				socket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return;
		}
	}

	/**
	 * @return  服务器JSONObject对象数据
	 */
	public static JSONObject getData() {
		JSONObject data2 = data;
		data = null;
		return data2;
	}

}
