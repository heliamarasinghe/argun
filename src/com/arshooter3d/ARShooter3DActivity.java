package com.arshooter3d;


import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ARShooter3DActivity extends ArShooterLoading implements OnTouchListener, SensorEventListener{
	private final float ALPHA = 0.8f;
	private final int SENSITIVITY = 4;
	private ARShooter3DRenderer mRenderer;
	private CameraPreview mPreview;
	private SensorManager mSensorManager;
	private Camera mCamera;
	private float mGravity[];
	private MediaPlayer play3dTrack;
	

	
    @SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     // -- set this before setRenderer() then use setBackgroundColor(0) in the renderer
        setGLBackgroundTransparent(true);
        
        mRenderer = new ARShooter3DRenderer(this);
        mRenderer.setSurfaceView(mSurfaceView);
        super.setRenderer(mRenderer);
        mSurfaceView.setOnTouchListener(this);

        
        
        // Create an instance of Camera
        mCamera = getCameraInstance();
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        mLayout.addView(mPreview, 0);
        
        
       
        
        //code to start DroidAR functions
        //ArActivity.startWithSetup(this, new DebugSetup());
		
        initLoader();
        //set gravity for accelerometer
        mGravity = new float[3];
        mGravity[0] = 0f;
        mGravity[1] = 0f;
        mGravity[2] = 0f;
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_FASTEST);
    }
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(event.getAction() == MotionEvent.ACTION_DOWN)
			mRenderer.getObjectAt(event.getX(), event.getY());
		return super.onTouchEvent(event);
	}
	
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation/keyboard change
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
	}
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			mGravity[0] = ALPHA *mGravity[0] + (1 - ALPHA) * event.values[0];
			mGravity[1] = ALPHA *mGravity[1] + (1 - ALPHA) * event.values[1];
			mGravity[2] = ALPHA *mGravity[2] + (1 - ALPHA) * event.values[2];

			mRenderer.setAccelerometerValues(event.values[1] - mGravity[1]* SENSITIVITY,
					event.values[0] - mGravity[0] * SENSITIVITY, mGravity[2]);
		}
		
	}
	@Override
	public void onResume(){
		super.onResume();
		play3dTrack = MediaPlayer.create(ARShooter3DActivity.this, R.raw.play3d);
		play3dTrack.start();
		play3dTrack.setLooping(true);
	}
	@Override
	public void onPause(){
		super.onPause();
		play3dTrack.release();
		mCamera.release();
		mCamera = null;
	}
}
