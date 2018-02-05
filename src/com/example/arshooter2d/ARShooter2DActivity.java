package com.example.arshooter2d;





import android.R.string;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

public class ARShooter2DActivity extends Activity {

	private CameraPreview cv;
	private FrameLayout main_layout;
	// sensors test variables
	private SensorManager sensorManager;
	private int orientationSensor;
	private float headingAngle;
	private float pitchAngle;
	private float rollAngle;
	///
	private int accelerometerSensor;
	private float xAxis;
	private float yAxis;
	private float zAxis;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	//	setContentView(R.layout.activity_arshooter2_d);
		 // requesting to turn the title OFF
		// registering the sensors variables
		
		
	////
	
		
		//////
	  //  	mSensorManager.registerListener(listener = new MySensorListener(count), myAcc , SensorManager.SENSOR_DELAY_UI);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        //Camera c = getCameraInstance();
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
       main_layout = new FrameLayout(this);
      
       cv = new CameraPreview(this);
       main_layout.addView(cv);
       setContentView(main_layout);
        
	}
	

	    public boolean onTouchEvent(MotionEvent event) {
	        return cv.onTouchEvent(event);
	    }


}
