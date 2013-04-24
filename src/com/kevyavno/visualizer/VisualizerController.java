package com.kevyavno.visualizer;

import java.util.ArrayList;
import java.util.List;


import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Visualizer;

public class VisualizerController {
	private Visualizer mVisualizer;
	private List<VisualizerView> views = new ArrayList<VisualizerView>();
	public void addView(VisualizerView v) {
		views.add(v);
	}
	/**
	 * Links the visualizer to a player
	 * @param player - MediaPlayer instance to link to
	 */
	public void link(MediaPlayer player)
	{
		if(player == null)
		{
			throw new NullPointerException("Cannot link to null MediaPlayer");
		}
		// Create the Visualizer object and attach it to our media player.
		mVisualizer = new Visualizer(player.getAudioSessionId());
		Equalizer mEqualizer = new Equalizer(0, player.getAudioSessionId());
		mEqualizer.setEnabled(true);
		mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

		// Pass through Visualizer data to VisualizerView
		Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener()
		{
			@Override
			public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
					int samplingRate)
			{
				for (VisualizerView v: views) {
					v.updateVisualizer(bytes);
				}

			}

			@Override
			public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
					int samplingRate)
			{
				for (VisualizerView v: views) {
					v.updateVisualizerFFT(bytes);
				}
			}
		};

		mVisualizer.setDataCaptureListener(captureListener,
				Visualizer.getMaxCaptureRate() / 2, true, true);

		// Enabled Visualizer and disable when we're done with the stream
		mVisualizer.setEnabled(true);
		player.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
		{
			@Override
			public void onCompletion(MediaPlayer mediaPlayer)
			{
				mVisualizer.setEnabled(false);
			}
		});
	}
	public void colorUpdate(int red, int green, int blue) {
		for (VisualizerView v: views) {
			v.setColour(Color.argb(200, red, green, blue));
		}
	}
	public void setGradient(Boolean bool) {
		for (VisualizerView v: views) {
			v.setGradient(bool);
		}
	}
	public void release()
	{
		mVisualizer.release();
	}
}
