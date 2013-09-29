package com.yj.wirelesscontroller;

import com.yj.wirelesscontroller.SettingActivity.SharedPrefer;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.EditText;

public class EditServerActivity extends Activity {

	private EditText server_addr_edit;
	private String server_addr_str;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_server);
		server_addr_edit = (EditText) findViewById(R.id.server_addr);
		server_addr_str = SharedPrefer.getString(Const.SERVER_ADDR);
		if(server_addr_str.equals("null")){
			server_addr_str = "192.168.1.1";
		}
		server_addr_edit.setText(server_addr_str);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.edit_server, menu);
		return true;
	}

	@Override
	public void onPause(){
		super.onPause();
		SharedPrefer.add(Const.SERVER_ADDR, server_addr_edit.getText().toString());
	}

}
