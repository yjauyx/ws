package com.yj.wirelesscontroller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SocketManager implements Runnable {

	public static String HOST = "192.168.1.107";
	private static final int PORT = 8080;
	private Socket socket = null;
	private BufferedReader in = null;
	private PrintWriter out = null;
	private String content = "";
	private Context context;
	private Handler activity_handler;

	private final int STATE_OPEN = 1;// socket打开
	private final int STATE_CLOSE = 1 << 1;// socket关闭
	private final int STATE_CONNECT_START = 1 << 2;// 开始连接server
	private final int STATE_CONNECT_SUCCESS = 1 << 3;// 连接成功
	private final int STATE_CONNECT_FAILED = 1 << 4;// 连接失败
	private final int STATE_CONNECT_WAIT = 1 << 5;// 等待连接

	private int state = STATE_CONNECT_START;
	private boolean isSendAllowed = true;

	public SocketManager(Context context, Handler aHandler) {
		this.context = context;
		activity_handler = aHandler;
	}

	public void StartConnect() {
		try {
			new Thread() {
				public void run() {
					try {
						state = STATE_CONNECT_START;
						socket = new Socket();
						socket.connect(new InetSocketAddress(HOST, PORT),
								15 * 1000);
						state = STATE_CONNECT_SUCCESS;
						in = new BufferedReader(new InputStreamReader(
								socket.getInputStream()));
						out = new PrintWriter(
								new BufferedWriter(new OutputStreamWriter(
										socket.getOutputStream())), true);
						Message msg = new Message();
						Bundle b = new Bundle();// 存放数据
						b.putString("network", "connected");
						msg.setData(b);
						activity_handler.sendMessage(msg);
						Log.d("yjauyx", "connected to server");
						JSONObject json = new JSONObject();
						json.put(Const.ID_NAME, "controller");
						out.println(json.toString());

					} catch (Exception e) {
						e.printStackTrace();
						Message msg = new Message();
						Bundle b = new Bundle();// 存放数据
						b.putString("network", "failed");
						msg.setData(b);
						activity_handler.sendMessage(msg);
					}
				}
			}.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			// ShowDialog("login exception" + ex.getMessage());
		}

		new Thread(SocketManager.this).start();
	}

	public boolean isConnected() {
		if (socket != null) {
			return socket.isConnected();
		}
		return false;
	}

	public void CloseSocket() {
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public int Send(String data) {

		if (socket.isConnected()) {
			out.println(data);
		}
		return 0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while (true) {
				if (socket.isConnected()) {
					if (!socket.isInputShutdown()) {
						if ((content = in.readLine()) != null) {
							content += "\n";
							Log.d("yjauyx", "received data: " + content);
							// mHandler.sendMessage(mHandler.obtainMessage());
						} else {

						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
