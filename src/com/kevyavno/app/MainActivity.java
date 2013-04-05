package com.kevyavno.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.kevyavno.visualizer.R;
import com.kevyavno.visualizer.VisualizerController;

/**
 * Demo to show how to use VisualizerView
 */
public class MainActivity extends FragmentActivity {
	public static MediaPlayer mPlayer;
	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;
	public static final int FRAGMENTS = 2;
	private FragmentPagerAdapter _fragmentPagerAdapter;
	private ViewPager _viewPager;
	VisualizerController controller;
	private List<Fragment> _fragments = new ArrayList<Fragment>();
	ReceiveMessages myReceiver = null;
	Boolean myReceiverIsRegistered = false;
	Intent playerIntent;
	Button stopBtn, startBtn, settingsBtn, aboutBtn;
	boolean menu_visible = false;
	public static final String PREF_NAME = "colours";
	SharedPreferences pref;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//assign buttons;
		stopBtn = (Button)findViewById(R.id.stopBtn);
		startBtn = (Button)findViewById(R.id.startBtn);
		settingsBtn = (Button)findViewById(R.id.settingsBtn);
		aboutBtn = (Button)findViewById(R.id.aboutBtn);
		//hide buttons initially
		stopBtn.setVisibility(View.GONE);
		startBtn.setVisibility(View.GONE);
		settingsBtn.setVisibility(View.GONE);
		aboutBtn.setVisibility(View.GONE);
		
		pref = getApplicationContext().getSharedPreferences(PREF_NAME, 0); //0 for private mode
		playerIntent = new Intent(MainActivity.this,Player.class);
		myReceiver = new ReceiveMessages();
		startService(playerIntent);


		controller = new VisualizerController();
		//Declare all views
		DisplayFragment f1 = new DisplayFragment();
		DisplayFragment f2 = new DisplayFragment();
		f1.setController(controller);
		f2.setController(controller);
		f2.toggleWave();
		
		// Create fragments.
		_fragments.add(FRAGMENT_ONE, f1);
		_fragments.add(FRAGMENT_TWO, f2);

		// Setup the fragments, defining the number of fragments, the screens and titles.
		_fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()){
			@Override
			public int getCount() {
				return FRAGMENTS;
			}
			@Override
			public Fragment getItem(final int position) {
				return _fragments.get(position);
			}
			@Override
			public CharSequence getPageTitle(final int position) {
				switch (position) {
				case FRAGMENT_ONE:
					return "Histogram";
				case FRAGMENT_TWO:
					return "Wave";
				default:
					return null;
				}
			}           
		};
		_viewPager = (android.support.v4.view.ViewPager) findViewById(R.id.pager);
		_viewPager.setAdapter(_fragmentPagerAdapter);
		final GestureDetector gd = new GestureDetector(getApplicationContext(),new MyGestureListener(this));
		_viewPager.setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				gd.onTouchEvent(arg1);
				return false;
			}
		});
		
	}
	public void screenTapped() {
		    if (!menu_visible) {
		        displayMenus();
		    } else {
		        hideMenus();
		    }
	}
	private void hideMenus() {
	    menu_visible = false;
	    //top menus
	    Animation top = AnimationUtils.loadAnimation(this, R.anim.hide_top_menu);
	    top.reset();
	    startBtn.startAnimation(top);
	    startBtn.setVisibility(View.INVISIBLE);
	    top.reset();
	    stopBtn.startAnimation(top);
	    stopBtn.setVisibility(View.INVISIBLE);
	    
	    //bottom menus
	    Animation bottom = AnimationUtils.loadAnimation(this, R.anim.hide_bottom_menu);
	    bottom.reset();
	    settingsBtn.startAnimation(bottom);
	    settingsBtn.setVisibility(View.INVISIBLE);
	    bottom.reset();
	    aboutBtn.startAnimation(bottom);
	    aboutBtn.setVisibility(View.INVISIBLE);
	    
	}
	
	private void displayMenus() {
		menu_visible = true;
	    //top menus
	    Animation top = AnimationUtils.loadAnimation(this, R.anim.show_top_menu);
	    top.reset();
	    startBtn.startAnimation(top);
	    startBtn.setVisibility(View.VISIBLE);
	    top.reset();
	    stopBtn.startAnimation(top);
	    stopBtn.setVisibility(View.VISIBLE);
	    
	    //bottom menus
	    Animation bottom = AnimationUtils.loadAnimation(this, R.anim.show_bottom_menu);
	    bottom.reset();
	    settingsBtn.startAnimation(bottom);
	    settingsBtn.setVisibility(View.VISIBLE);
	    bottom.reset();
	    aboutBtn.startAnimation(bottom);
	    aboutBtn.setVisibility(View.VISIBLE);
	}

	public void startPressed(View view) throws IllegalStateException, IOException {
			Player.startPressed();
	}
	public void stopPressed(View view) { 
		  Player.stopPressed();
	}
	public void settingsPressed(View view) {
		startActivity(new Intent(MainActivity.this,SettingsActivity.class));
	}
	public void aboutPressed(View view) {
		startActivity(new Intent(MainActivity.this,AboutActivity.class));
	}
	@Override 
	public void onPause() {
		super.onPause();
		if (myReceiverIsRegistered) {
			unregisterReceiver(myReceiver);
			myReceiverIsRegistered = false;
		}
	}
	public void onResume() {
		super.onResume();
		if (!myReceiverIsRegistered) {
			IntentFilter filter = new IntentFilter("com.kevyavno.app.PlayingMusic");
			filter.addAction("com.kevyavno.app.ColourChange");
			registerReceiver(myReceiver,filter);
			myReceiverIsRegistered = true;
		}
	}
	public class ReceiveMessages extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			String action = arg1.getAction();
			Log.v("test",action);
			if (action.equalsIgnoreCase("com.kevyavno.app.PlayingMusic")) {
				controller.link(Player.getPlayer()); 
			} else if (action.equalsIgnoreCase("com.kevyavno.app.ColourChange")) {
				int redVal,greenVal,blueVal;
				redVal = pref.getInt("red", 56);
				greenVal = pref.getInt("green", 138);
				blueVal = pref.getInt("blue", 252);
				//Log.v("test","red:" + redVal + " green:" + greenVal + " blue:" + blueVal);
				controller.colorUpdate(redVal,greenVal,blueVal);
				
			}
		}
		
	}
}
