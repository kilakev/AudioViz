package com.kevyavno.app;



import com.kevyavno.visualizer.R;
import com.kevyavno.visualizer.VisualizerController;
import com.kevyavno.visualizer.VisualizerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DisplayFragment extends Fragment{
	private VisualizerView mVisualizerView;
	private static VisualizerController control;
	private Boolean isWave = false;
	Intent playerIntent;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_display_fragment, null);
		mVisualizerView = (VisualizerView) view.findViewById(R.id.visualizerView);
		playerIntent = new Intent(this.getActivity(),Player.class);
		
		// Create UI components here.
		return view;
	}
	public VisualizerView getVizView() {
		return mVisualizerView;
	}
	public void setController(VisualizerController c) {
		control=c;
	}
	@Override
	public void onResume()
	{
		super.onResume();
		init();
		
	}

	@Override
	public void onPause()
	{
		cleanUp();
		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		cleanUp();
		super.onDestroy();
	}

	private void init()
	{
		control.addView(mVisualizerView);
		if (isWave) {
			mVisualizerView.toggleWave();
		}
		// We need to link the visualizer view to the media player so that
		// it displays something
	}
	
	public void toggleWave() {
		isWave = !isWave;
	}

	private void cleanUp()
	{
		getActivity().stopService(playerIntent);
		control.release();
	}
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_display_fragment, menu);
		return true;
	}*/
	
}
