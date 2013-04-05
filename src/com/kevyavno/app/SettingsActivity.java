package com.kevyavno.app;

import com.kevyavno.visualizer.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class SettingsActivity extends Activity {
	int redVal,greenVal,blueVal;
	SeekBar redBar,greenBar, blueBar;
	ImageView previewBox;
	Button saveBtn;
	
	//Preferences variables
	public static final String PREF_NAME = "colours";
	SharedPreferences pref;
	Editor prefEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		//load preferences
		pref = getApplicationContext().getSharedPreferences(PREF_NAME, 0); //0 for private mode
		prefEditor = pref.edit();
		
		//initialize colors
		redVal = pref.getInt("red", 56);
		greenVal = pref.getInt("green", 138);
		blueVal = pref.getInt("blue",252);
		
		redBar = (SeekBar) findViewById(R.id.redVal);
		greenBar = (SeekBar) findViewById(R.id.greenVal);
		blueBar = (SeekBar) findViewById(R.id.blueVal);
		previewBox = (ImageView) findViewById(R.id.colourPreview);
		
		redBar.setOnSeekBarChangeListener(changeListener);
		redBar.setProgress(redVal);
		greenBar.setOnSeekBarChangeListener(changeListener);
		greenBar.setProgress(greenVal);
		blueBar.setOnSeekBarChangeListener(changeListener);
		blueBar.setProgress(blueVal);
		
		previewBox.setBackgroundColor(Color.argb(200, redVal, greenVal, blueVal));
		saveBtn = (Button) findViewById(R.id.saveBtn);
		saveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	OnSeekBarChangeListener changeListener = new OnSeekBarChangeListener() {

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			switch (seekBar.getId()) {
				case R.id.redVal: redVal = progress; break;
				case R.id.greenVal: greenVal = progress; break;
				case R.id.blueVal: blueVal = progress; break;
			}
			previewBox.setBackgroundColor(Color.argb(200, redVal, greenVal, blueVal));
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
	};
	protected void onDestroy() {
		super.onDestroy();
		prefEditor.putInt("red", redVal);
		prefEditor.putInt("green", greenVal);
		prefEditor.putInt("blue", blueVal);
		prefEditor.commit();
		Intent i = new Intent("com.kevyavno.app.ColourChange");
	    sendBroadcast(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_settings, menu);
		return true;
	}

}
