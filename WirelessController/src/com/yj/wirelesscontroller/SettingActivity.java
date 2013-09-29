package com.yj.wirelesscontroller;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class SettingActivity extends Activity {

	private TextView server_edit;
	private TextView mouse_edit;
	
	public static class SharedPrefer {
		public static SharedPreferences sp;

		public static void add(String key, String value) {
			SharedPreferences.Editor editor = sp.edit();
			editor.putString(key, value);
			editor.commit();
		}

		public static void delete(String key) {
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(key);
			editor.commit();
		}

		public static void clear() {
			SharedPreferences.Editor editor = sp.edit();
			editor.clear();
			editor.commit();
		}

		public static String getString(String key) {
			return sp.getString(key, "null");
		}
	}

	class SettingClickListener implements OnClickListener {
		private int id = 0;

		public SettingClickListener(int index) {
			id = index;
		}

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			switch (id) {
			case 0:
				startActivity(new Intent(SettingActivity.this, EditServerActivity.class));
				break;
			case 1:
				break;
			default:
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		server_edit = (TextView) findViewById(R.id.setting_server);
		mouse_edit = (TextView) findViewById(R.id.setting_mouse);
		setListener();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
		return true;
	}
	public void setListener() {
		 server_edit.setOnClickListener(new SettingClickListener(0));
	}

}
