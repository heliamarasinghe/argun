package com.example.arshooter2d;



import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.arshooter3d.R;







import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;



import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
 
	private Moving_ADroid[] M_Adroid;
	
	private Moving_ADroid M_cross;
	private Canvas CameraCanvas;
	private SoundManager soundManager;
	private Paint textPaint ;

	private Context cc;
    public int GameCount=0;
    private Bitmap b;
   
    //////////////////
    private SensorManager sensorManager;
	private int orientationSensor;
	private float headingAngle;
	private float pitchAngle;
	private float rollAngle;
	///
	private boolean first_time=false;
	private float sensor_ratio_x=0.0f;
	private float sensor_ratio_y=0.0f;
	
	private int accelerometerSensor;
	private float xAxis,pre_x,new_x;
	private float yAxis,pre_y,new_y;
	private float zAxis,pre_z,new_z;
	private float gravity_x,gravity_y,gravity_z;
	private static final float NS2S = 1.0f / 1000000000.0f;
	private float[] last_values = null;
	private float[] velocity = null;
	private float[] position = null;
	private long last_timestamp = 0;
    
    public CameraPreview(Context context){//, Camera camera) {
        super(context);
    //    mCamera = camera;
        cc=context;
        textPaint = new Paint();
        soundManager=new SoundManager(context);
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        M_Adroid = new Moving_ADroid[3];//(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), 50, 50);
		//for(int i=0;i<3;i++)
        M_Adroid[0]=new Moving_ADroid(BitmapFactory.decodeResource(getResources(), R.drawable.moving_d), 50, 50,3,3,1,1);
		
        M_Adroid[1]=new Moving_ADroid(BitmapFactory.decodeResource(getResources(), R.drawable.moving_d), 50, 100,4,4,-1,1);
        M_Adroid[2]=new Moving_ADroid(BitmapFactory.decodeResource(getResources(), R.drawable.moving_d), 50, 300,6,6,1,-1);
		
        int x=(getWidth()/2)+170;
        int y=(getHeight()/2)+10;
       
    //,x-50, y-200, null); //100,50
        
        M_cross=new Moving_ADroid(BitmapFactory.decodeResource(getResources(), R.drawable.cross1), x, y,0,0,1,1);
        mHolder = getHolder(); //Get the SurfaceHolder for our SurfaceView via getHolder().
        mHolder.addCallback(this);// Register a SurfaceHolder. Callback so that we are notified when our SurfaceView is ready or changes

        
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);// Tell the SurfaceView, via the SurfaceHolder, that it has the
       // SURFACE_TYPE_PUSH_BUFFERS type (using setType()). This  indicates that something in the system will be updating the
      //  SurfaceView and providing the bitmap data to display.

        
       
   
	   
	    
      
		//////
    }
   
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
    	  synchronized (this) {
    	       this.setWillNotDraw(false); // allow to draw over this view in onDraw method
       try {
        	
       	    mCamera = Camera.open();
            mCamera.setPreviewDisplay(holder);// takes a  SurfaceHolder and arranges for the camera preview to be displayed on the related SurfaceView

            mCamera.startPreview();//Camera show the preview via startpreview()

         //   Log.d("xxx", "camera starts correectly");
        
      } catch (IOException e) {
        //  Log.d("xxx", "Error setting camera preview: " + e.getMessage());
      }
 
    	  }
    }
    
	
    @Override
    protected void onDraw(Canvas canvas){
		// A Simple Text Render to test the display
    	  Log.d("xxx", "on draw called");
    	  textPaint.setARGB(255, 200, 0, 0);
          textPaint.setTextSize(60);
          Bitmap b1=BitmapFactory.decodeResource(getResources(), R.drawable.cross1);
          Bitmap b2=BitmapFactory.decodeResource(getResources(), R.drawable.g2);
          int x=(getWidth()/2);//-20;
          int y=(getHeight()/2);//+50;
          Log.d("xxx",""+ x);
          Log.d("xxx",""+ y);
         canvas.drawBitmap(b1,x-50, y-200, null); //100,50
   	     canvas.drawBitmap(b2, x, y+240, null); //350,100
   
         render(canvas);
    
	}
    
    public void render(Canvas canvas) {

		 Log.d("xxx", "i am render ");
		 for(int i=0;i<3;i++)
			 M_Adroid[i].draw(canvas);
		 
	//	 M_cross.draw_corss(canvas);
		
		 if (GameCount>2)
		 {
	   		 canvas.drawText("WON", 300, 100, textPaint);
			 Toast toast = Toast.makeText(cc, "YOU WON GO TO NEXT LEVEL", Toast.LENGTH_LONG);
				        	toast.show();
	   		GameCount=0;
		 }
	   	   else
	   	   {
	   		canvas.drawText("", 50, 50, textPaint); 
		     update();
		   //  update_cross_location();
		    invalidate();
	   	   }
	 	 
	}
    public void update() {
		// check collision with right wall if heading right
    	 Log.d("xxx", "i am update ");
    	 for(int i=0;i<3;i++)
    	 {
		if (M_Adroid[i].getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
				&& M_Adroid[i].getX() + M_Adroid[i].getBitmap().getWidth() / 2 >= getWidth()) {
			M_Adroid[i].getSpeed().toggleXDirection();
		}
		// check collision with left wall if heading left
		if (M_Adroid[i].getSpeed().getxDirection() == Speed.DIRECTION_LEFT
				&& M_Adroid[i].getX() - M_Adroid[i].getBitmap().getWidth() / 2 <= 0) {
			M_Adroid[i].getSpeed().toggleXDirection();
		}
		// check collision with bottom wall if heading down
		if (M_Adroid[i].getSpeed().getyDirection() == Speed.DIRECTION_DOWN
				&& M_Adroid[i].getY() + M_Adroid[i].getBitmap().getHeight() / 2 >= getHeight()) {
			M_Adroid[i].getSpeed().toggleYDirection();
		}
		// check collision with top wall if heading up
		if (M_Adroid[i].getSpeed().getyDirection() == Speed.DIRECTION_UP
				&& M_Adroid[i].getY() - M_Adroid[i].getBitmap().getHeight() / 2 <= 0) {
			M_Adroid[i].getSpeed().toggleYDirection();
		}
		// Update the lone droid
		M_Adroid[i].update();
    	 }
	}
    
    public void surfaceDestroyed(SurfaceHolder holder) {
    	mCamera.release();
    	mCamera = null;
    }
    @Override
	  public boolean onTouchEvent(MotionEvent event) {
	    // do stuff
    	boolean shoot=false;
    	for(int i=0;i<3;i++)
    		if ((M_Adroid[i].getX()>(getWidth()/2)-50)&&(M_Adroid[i].getX()<(getWidth()/2)+84)&& (M_Adroid[i].getY()>(getHeight()/2)-200)&&(M_Adroid[i].getY()<(getHeight()/2)-67))//&& (GameCount<=2))
    		{
		         soundManager.playSound(1);
		         GameCount++;
		         shoot=true;
		                		
		         
    		}
    		if (!shoot)
    		
    			soundManager.playSound(0);
    		
    		  invalidate();
	    return true;
	  }
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
          //  Log.d("xxx", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void onPause() {
    	mCamera.release();
    	mCamera = null;
    }
}