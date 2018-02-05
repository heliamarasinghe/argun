package com.arshooter3d;

import system.ArActivity;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class DArCameraPreviewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Activity theCurrentActivity = this;
		//final Setup aSetupInstance = new DebugSetup();
		ArActivity.startWithSetup(this, new DArCameraPreviewSetup());
		//setContentView(R.layout.activity_debug);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_debug, menu);
		return true;
	}

}
