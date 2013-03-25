package com.kevyavno.app;

import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

import com.kevyavno.visualizer.VisualizerView;
import com.kevyavno.visualizer.R;

/**
 * Demo to show how to use VisualizerView
 */
public class MainActivity extends Activity {
  private MediaPlayer mPlayer;
  private VisualizerView mVisualizerView;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
  }

  @Override
  protected void onResume()
  {
    super.onResume();
    init();
  }

  @Override
  protected void onPause()
  {
    cleanUp();
    super.onPause();
  }

  @Override
  protected void onDestroy()
  {
    cleanUp();
    super.onDestroy();
  }

  private void init()
  {
    mPlayer = MediaPlayer.create(this, R.raw.test);
    mPlayer.setLooping(true);
    mPlayer.start();

    // We need to link the visualizer view to the media player so that
    // it displays something
    mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
    mVisualizerView.link(mPlayer);
  }

  private void cleanUp()
  {
    if (mPlayer != null)
    {
      mVisualizerView.release();
      mPlayer.release();
      mPlayer = null;
    }
  }

 

  // Actions for buttons defined in xml
  public void startPressed(View view) throws IllegalStateException, IOException
  {
    if(mPlayer.isPlaying())
    {
      return;
    }
    mPlayer.prepare();
    mPlayer.start();
  }

  public void stopPressed(View view)
  {
    mPlayer.stop();
  }

}