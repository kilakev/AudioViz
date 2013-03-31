package com.kevyavno.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

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
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
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
	}


	public void startPressed(View view) throws IllegalStateException, IOException
	{
			Player.startPressed();
	}
	public void stopPressed(View view)
	{ 
		  Player.stopPressed();
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
			registerReceiver(myReceiver,new IntentFilter("com.kevyavno.app.PlayingMusic"));
			myReceiverIsRegistered = true;
		}
	}
	public class ReceiveMessages extends BroadcastReceiver {

		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			controller.link(Player.getPlayer());
		}
		
	}
}
