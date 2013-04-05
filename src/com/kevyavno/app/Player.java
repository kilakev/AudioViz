package com.kevyavno.app;

import java.io.IOException;

import com.kevyavno.visualizer.R;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;


public class Player extends Service {
//	private static final Player instance = new Player();
	public static MediaPlayer mPlayer;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate() {
		super.onCreate();
		mPlayer = MediaPlayer.create(this, R.raw.test);
	    mPlayer.setLooping(true);
	    Intent i = new Intent("com.kevyavno.app.PlayingMusic");
	    sendBroadcast(i);
	}
	public int onStartCommand(Intent intent, int flags, int startId) {
		mPlayer.start();
		return START_STICKY;
	}
	public void onDestroy() {
		if (mPlayer != null)
	    {
		  mPlayer.stop();
	      mPlayer.release();
	      mPlayer = null;
	    }
	}
	
	  public static void startPressed() throws IllegalStateException, IOException
	  {
	    if(mPlayer.isPlaying())
	    {
	      return;
	    }
	    mPlayer.prepare();
	    mPlayer.start();
	  }

	  public static void stopPressed()
	  {
	    mPlayer.stop();
	  }
	  
	  public static MediaPlayer getPlayer() {
		  return mPlayer;
	  }
}
