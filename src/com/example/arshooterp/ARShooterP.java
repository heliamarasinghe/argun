package com.example.arshooterp;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.arshooter3d.R;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;

public class ARShooterP extends Activity {

	 private CameraPreview cam_preview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		/* set Full Screen */
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);  
		/* set window with no title bar */
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		

		
		/* create camera view */
        cam_preview = new CameraPreview(this);//,tracker_);
		
		setContentView(cam_preview);
		/* append Overlay */
		addContentView(cam_preview.getOverlay(), new LayoutParams 
				(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	}
	/* onDestroy */
	@Override
	protected void onDestroy() {
		
		
		super.onDestroy();
	}

	/* onStart */
	@Override
	protected void onStart() {
       
		
	
		super.onStart();
	}

	/* onPause */
	@Override
	protected void onPause() {
		
		super.onPause();
	}

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
	//	getMenuInflater().inflate(R.menu.activity_arshooter_p, menu);
		return true;
	}

}
