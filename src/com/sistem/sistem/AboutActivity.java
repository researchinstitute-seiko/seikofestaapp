package com.sistem.sistem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.TextView;

public class AboutActivity extends Activity {

	TextView textview;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		textview=(TextView)findViewById(R.id.textview);
		InputStream is = getResources().openRawResource(R.raw.about);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String all="";
		try {
			String line;
			while (( line = reader.readLine()) != null) {all=all+line+"\n"; }
			reader.close();
		} catch (IOException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		textview.setText(all);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about, menu);
		return true;
	}

}
