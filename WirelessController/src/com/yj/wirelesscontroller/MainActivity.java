package com.yj.wirelesscontroller;

import org.json.JSONException;
import org.json.JSONObject;

import com.yj.wirelesscontroller.SettingActivity.SharedPrefer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private SocketManager socketMgr;
	private boolean isConnected = false;
	private View controlPane;
	private int pos_x = -1;
	private int pos_y = -1;
	private EditText inputBox = null;
	private Button delete_btn;
	private Button input_btn;
	private String server_addr_str = null;
	private SharedPreferences sharedPreferences;
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Bundle bundle = msg.getData();
			if (bundle.getString("network").equals("connected")) {
				isConnected = true;
				Log.d("yjauyx",
						"connected to server 192.168.1.106 successfully");
			} else if (bundle.getString("network").equals("failed")) {
				Toast toast = Toast
						.makeText(
								MainActivity.this,
								"Connecting to server failed, configure server in setting first.",
								Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	};

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SharedPrefer.sp = this.getSharedPreferences("setting",
				MODE_WORLD_READABLE);
		server_addr_str = SharedPrefer.getString(Const.SERVER_ADDR);
		if (socketMgr == null) {
			socketMgr = new SocketManager(this, mHandler);
		}
		socketMgr.HOST = server_addr_str;
		if (!socketMgr.isConnected()) {
			isConnected = false;
			socketMgr.StartConnect();
		} else {
			isConnected = true;
		}
		setContentView(R.layout.activity_main);
		if (inputBox == null) {
			inputBox = (EditText) findViewById(R.id.input_box);
		}

		controlPane = (View) findViewById(R.id.panel);
		delete_btn = (Button) findViewById(R.id.delete_btn);
		input_btn = (Button) findViewById(R.id.add_btn);
		controlPane.setOnTouchListener(new OnTouchListener() {
			private boolean isClicked = false;

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if (arg1.getActionMasked() == MotionEvent.ACTION_DOWN) {
					Log.d("yjauyx", "mouse down");
					isClicked = true;
					pos_x = (int) arg1.getX();
					pos_y = (int) arg1.getY();
				} else if (arg1.getActionMasked() == MotionEvent.ACTION_UP) {
					Log.d("yjauyx", "mouse_up");
					pos_x = -1;
					pos_y = -1;
					if (isClicked) {
						Log.d("yjauyx", "clicked...");
						sendLeftClick();
					}
				} else if (arg1.getActionMasked() == MotionEvent.ACTION_MOVE) {
					int dx = (int) arg1.getX() - pos_x;
					int dy = (int) arg1.getY() - pos_y;
					if (Math.abs(dx) >= 5 || Math.abs(dy) >= 5) {
						Log.d("yjauyx", "mouse move");
						Log.d("yjauyx", "dx = " + dx + " dy = " + dy);
						isClicked = false;
						dx = Math.abs(dx) >= 5 ? dx : 0;
						dy = Math.abs(dy) >= 5 ? dy : 0;
						pos_x = (int) arg1.getX();
						pos_y = (int) arg1.getY();
						sendMouseMovement(dx, dy);
					}
				}
				return false;
			}

		});
		delete_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}

		});

		input_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				JSONObject json_cmd = new JSONObject();
				try {
					json_cmd.put(Const.OP_TARGET, "mouse");
					JSONObject op_data = new JSONObject();
					op_data.put(Const.OP_CMD, Const.OP_KEYBOARD_EVENT);
					JSONObject move_pos = new JSONObject();
					op_data.put(Const.INPUT_STR, inputBox.getText().toString());
					json_cmd.put(Const.OP_DATA, op_data);
					String json_str = json_cmd.toString();
					json_str += '\0';
					Log.d("yjauyx", json_cmd.toString());
					socketMgr.Send(json_str);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		menu.add(0, 1, 1, R.string.setting);
		menu.add(0, 2, 2, R.string.exit);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == 1) {
			Toast toast = Toast.makeText(this, "settings clicked",
					Toast.LENGTH_SHORT);
			toast.show();
			startActivity(new Intent(MainActivity.this, SettingActivity.class));

		} else if (item.getItemId() == 2) {
			finish();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (server_addr_str != null) {
			String sserver_addr_tmp = SharedPrefer.getString(Const.SERVER_ADDR);
			if (!server_addr_str.equals(sserver_addr_tmp) || !isConnected) {
				socketMgr.CloseSocket();
				socketMgr.HOST = sserver_addr_tmp;
				isConnected = false;
				socketMgr.StartConnect();
			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		socketMgr.CloseSocket();
		isConnected = false;
	}

	public void sendMouseMovement(int dx, int dy) {
		JSONObject json_cmd = new JSONObject();
		try {
			json_cmd.put(Const.OP_TARGET, "mouse");
			JSONObject op_data = new JSONObject();
			op_data.put(Const.OP_CMD, Const.OP_MOVE);
			JSONObject move_pos = new JSONObject();
			// Log.d("yjauyx","dx = "+dx);
			// Log.d("yjauyx","dy = "+dy);
			move_pos.put("x", dx);
			move_pos.put("y", dy);
			op_data.put(Const.MOVE_POS, move_pos);
			json_cmd.put(Const.OP_DATA, op_data);
			String json_str = json_cmd.toString();
			json_str += '\0';
			Log.d("yjauyx", json_cmd.toString());
			socketMgr.Send(json_str);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void sendLeftClick() {
		JSONObject json_cmd = new JSONObject();
		try {
			json_cmd.put(Const.OP_TARGET, "mouse");
			JSONObject op_data = new JSONObject();
			op_data.put(Const.OP_CMD, Const.OP_LEFT_CLICK);
			json_cmd.put(Const.OP_DATA, op_data);
			String json_str = json_cmd.toString();
			json_str += '\0';
			Log.d("yjauyx", json_cmd.toString());
			socketMgr.Send(json_str);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void sendRightClick() {

	}

	public void sendDoubleClick() {

	}

}
