package com.example.arshooterp;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Calendar;

import com.arshooter3d.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.hardware.Camera;
//import android.hardware.Camera.AutoFocusCallback;

import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;
public class CameraPreview  extends SurfaceView implements SurfaceHolder.Callback, PreviewCallback {
		
	
		private final static int PREVIEW_WIDTH_NORMAL	= 240;
		private final static float MAG_EYEDIST_FACE		= 3.0f;
	

		/* local members */
		private Context context_;
		private SurfaceHolder hldr_surface;
		private Camera camera_;
		private boolean lock_Preview = true;
		private int Width_prevSetting;
		private int Height_prevSetting;
		private int Width_preview;
		private int Height_preview;

	    private Bitmap Cross_Fire=null;
	    private Bitmap Gun=null;
	    private boolean face_detected=false;
	    private SoundManager sounds;
	    private int Count_people=0;
	    private int Level_count=0;
		/* Overlay Layer for additional graphics overlay */
		
		private OverlayLayer overlay_Layer;

		/* Face Detection */
		private FaceResult faces_results[];
		private static final int N_Faces = 5;
		private FaceDetector face_detect;


		/* Face Detection Threads */
		private boolean is_ThreadWorking = false;
		private Handler handler_;
		private FaceDetectThread Thread_detect = null;
		
		/* buffers for vision analysis */
		private byte[] grayBuff_;
		private int bufflen_;
		private int[] rgbs_;
		
		

		/* Constructor */
		public CameraPreview(Context context){
			super(context);
			context_ = context;
			Width_preview = Height_preview = 1;
			faces_results = new FaceResult[N_Faces];
			for(int i=0;i<N_Faces;i++)          // initialize the face result array
				faces_results[i] = new FaceResult();
			overlay_Layer = new OverlayLayer(context);
			hldr_surface = getHolder();
			hldr_surface.addCallback(this);
			hldr_surface.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			handler_ = new Handler();
			sounds=new SoundManager(context);    // initialize the sound object

			Cross_Fire=BitmapFactory.decodeResource(context.getResources(),R.drawable.cross1);
			Gun=BitmapFactory.decodeResource(context.getResources(),R.drawable.g2);
		
		}

			
		/* Overlay instance access method to be called from the ARShooterP Activity */
		public OverlayLayer getOverlay(){
			return overlay_Layer;
		}

		/* surfaceCreated */
		public void surfaceCreated(SurfaceHolder holder) {
			setKeepScreenOn(true);
			
			setupCamera();
		//	sounds.playSound(2);
		}

		/* surfaceDestroyed */
		public void surfaceDestroyed(SurfaceHolder holder) {
			setKeepScreenOn(false);
			releaseCamera();
		}

		

		/* surfaceChanged */
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			resetCameraSettings();
			camera_.setPreviewCallback(this);
			camera_.startPreview();
		}

		/* onPreviewFrame */
		public void onPreviewFrame(byte[] _data, Camera _camera) {
			if(lock_Preview)  // check if frame is lock  
				return;
			if(_data.length < bufflen_)
				return;
			// run only one analysis thread on the frame at one time
			if(!is_ThreadWorking){
				is_ThreadWorking = true;
				// copy only Y buffer
				ByteBuffer bbuffer = ByteBuffer.wrap(_data);
				bbuffer.get(grayBuff_, 0, bufflen_);
				// make sure that no new threads will work until the previous ones completed
				waitForFdetThreadComplete();
				// start thread
				Thread_detect = new FaceDetectThread(handler_);
				Thread_detect.setBuffer(grayBuff_);
				Thread_detect.start();
			}
		}

		/* onTouchEvent */
		@Override
		public boolean onTouchEvent(MotionEvent event) {
			
			if (face_detected)
			{
				sounds.playSound(1);  // if face detected play the appropriate sound and count score up
				Count_people++;
				if (Count_people>=10)  // if the score reach to 10 persons then go to the next level and reset the counters
				{
					Count_people=0;
					// display message indicate that you won and go to next level
					Toast.makeText(context_,"You Won Go To Next Level", Toast.LENGTH_LONG).show();
					Level_count++;
					
				}
				face_detected=false;
			}
			else
				// otherwise play another sound indicate that miss 
				sounds.playSound(0);
			
			
			return true;
		}
		
		


		/* setupCamera */
		private void setupCamera(){
			try {
				camera_ = Camera.open();  // open the camera
				
				camera_.setPreviewDisplay(hldr_surface);  // assign handler to preview
			} catch (IOException exception) {
				camera_.release();
				camera_ = null;
			}
		}

		/* releaseCamera */
		private void releaseCamera(){
		// release the camera object
			lock_Preview = true;
			camera_.setPreviewCallback(null);
			camera_.stopPreview();
			waitForFdetThreadComplete();
			camera_.release();
			camera_ = null;
		}
		
		/* resetCameraSettings */
		private void resetCameraSettings(){
			lock_Preview = true;
			waitForFdetThreadComplete();
			for(int i=0;i<N_Faces;i++)
				faces_results[i].clear();
			/* set parameters for onPreviewFrame */
	 		Camera.Parameters params = camera_.getParameters();
			String strPrevSizesVals = params.get("preview-size-values");
	 	
	 		int previewHeightNorm = 0;
	 		if(strPrevSizesVals!=null){
		 		String tokens[] = strPrevSizesVals.split(",");
		 		for( int i=0; i < tokens.length; i++ ){
		 	 		String tokens2[] = tokens[i].split("x");
		 		
		 			if( tokens[i].contains(Integer.toString(PREVIEW_WIDTH_NORMAL)) )
		 				previewHeightNorm = Integer.parseInt(tokens2[1]);
		 		}
	 		}
	 		else{
	 		
	 			previewHeightNorm = 160;
	 		}
		
	 		Width_prevSetting = PREVIEW_WIDTH_NORMAL;
	 		Height_prevSetting = previewHeightNorm;
		
	 		/* set preview size small for fast analysis. let say QQVGA
	 		 * by setting smaller image size, small faces will not be detected. */
	 		if(Height_prevSetting!=0)
	 			params.setPreviewSize(Width_prevSetting, Height_prevSetting);
	 	
			camera_.setParameters(params);
			/* setParameters do not work well */
	 		params = camera_.getParameters();
	 		Size size = params.getPreviewSize();
	 		Width_preview = size.width;
	 		Height_preview = size.height;
		
			// allocate work memory for analysis
			bufflen_ = Width_preview*Height_preview;
			grayBuff_ = new byte[bufflen_];
			rgbs_ = new int[bufflen_];
			float aspect = (float)Height_preview/(float)Width_preview;
			face_detect = new FaceDetector( Width_prevSetting,(int)(Width_prevSetting*aspect), N_Faces ); 
			lock_Preview = false;
		}
		
		
			 

		
		/* waitForFdetThreadComplete */
		private void waitForFdetThreadComplete(){
			if(Thread_detect == null)
				return;
			if( Thread_detect.isAlive() ){
				try {
					Thread_detect.join();
					Thread_detect = null;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		/* Overlay Layer class */
		public class OverlayLayer extends View { 
			private Paint paint_ = new Paint(Paint.ANTI_ALIAS_FLAG); 
			private Paint text_paint = new Paint(); 

			/* Constructor */
			public OverlayLayer(Context context) { 
				super(context); 
				paint_.setStyle(Paint.Style.STROKE); 
				paint_.setColor(0xFF33FF33);
				paint_.setStrokeWidth(3);
				text_paint.setARGB(255, 0, 200, 0);
				text_paint.setTextSize(20);
			} 

			/* onDraw - Draw Face rect */
			@Override 
			protected void onDraw(Canvas canvas) {
				// this method called when a face detected , by invoking invalidate method
				// which refresh the screen and automatically call the onDraw method
				
				super.onDraw(canvas);
				int w = canvas.getWidth();
				int h = canvas.getHeight();
				canvas.drawBitmap(Gun,(w-Gun.getWidth())/2,(h-Gun.getHeight()), null);
				int x = w-150;
				int y = 10;
	        	canvas.drawText("Level:"+Level_count+"  Count:"+Count_people,x,y+20,text_paint);
	        	
				float xRatio = (float)w / Width_preview; 
				float yRatio = (float)h / Height_preview;
				for(int i=0; i<N_Faces; i++){
					FaceResult face = faces_results[i];
					float eyedist = face.eyesDistance()*xRatio;
					if(eyedist==0.0f)
						continue;
					PointF midEyes = new PointF();
					face.getMidPoint(midEyes);
					face_detected=true;
					// when face detected draw the green rectangle around the detected face
					// also draw the cross fire aligned with the green rectangle
					PointF lt = new PointF(midEyes.x*xRatio-eyedist*MAG_EYEDIST_FACE*0.5f,midEyes.y*yRatio-eyedist*MAG_EYEDIST_FACE*0.5f);
					canvas.drawRect((int)(lt.x),(int)(lt.y),(int)(lt.x+eyedist*MAG_EYEDIST_FACE),(int)(lt.y+eyedist*MAG_EYEDIST_FACE), paint_); 
					canvas.drawBitmap(Cross_Fire, null , new Rect((int)lt.x, (int)lt.y,(int)(lt.x+eyedist*MAG_EYEDIST_FACE),(int)(lt.y+eyedist*MAG_EYEDIST_FACE)),paint_);
						
				}
			}
		};
		
		/* Thread Class for Face Detection */
		private class FaceDetectThread extends Thread {
			/* variables */
			private Handler handler_;
			private byte[] graybuff_ = null;

			/* Constructor */
			public FaceDetectThread(Handler handler){
				handler_ = handler;
			}
			
			/* set buffer */
			public void setBuffer(byte[] graybuff){
				graybuff_ = graybuff;
			}

			
			@Override
			public void run() {
				/* face detector only needs grayscale image */
								
				gray8toRGB32(graybuff_,Width_preview,Height_preview,rgbs_);		// Convert the captured frame to grayscal
				float aspect = (float)Height_preview/(float)Width_preview;
				int w = Width_prevSetting;
				int h = (int)(Width_prevSetting*aspect);
				float xScale = (float)Width_preview/(float)Width_prevSetting;
				float yScale = (float)Height_preview/(float)Height_prevSetting;
				Bitmap bmp = Bitmap.createScaledBitmap(Bitmap.createBitmap(rgbs_,Width_preview,Height_preview,Bitmap.Config.RGB_565),w,h,false);

				int prevfound=0,trackfound=0;
				for(int i=0; i<N_Faces; i++){
					FaceResult face = faces_results[i];
					float eyedist = face.eyesDistance();
					if(eyedist==0.0f)
						continue;
					PointF midEyes = new PointF(); 
					face.getMidPoint(midEyes);
					prevfound++;
					PointF lt = new PointF(midEyes.x-eyedist*2.5f,midEyes.y-eyedist*2.5f);
					Rect rect = new Rect((int)(lt.x),(int)(lt.y),(int)(lt.x+eyedist*5.0f),(int)(lt.y+eyedist*5.0f));
					/* fix to fit */
					rect.left = rect.left < 0 ? 0 : rect.left;
					rect.right = rect.right > w ? w : rect.right;
					rect.top = rect.top < 0 ? 0 : rect.top;
					rect.bottom = rect.bottom > h ? h : rect.bottom;
					if(rect.left >= rect.right || rect.top >= rect.bottom )
						continue;
					/* crop */
					Bitmap facebmp = Bitmap.createBitmap(bmp,rect.left,rect.top,rect.width(),rect.height());
					FaceDetector.Face[] trackface = new FaceDetector.Face[1];
					FaceDetector tracker = new FaceDetector( facebmp.getWidth(),facebmp.getHeight(),1); 
					int found = tracker.findFaces(facebmp, trackface);
					if(found!=0){
						PointF ptTrack = new PointF();
						trackface[0].getMidPoint(ptTrack);
						ptTrack.x += (float)rect.left;
						ptTrack.y += (float)rect.top;
						ptTrack.x *= xScale;
						ptTrack.y *= yScale;
						float trkEyedist = trackface[0].eyesDistance()*xScale;
						
						faces_results[i].setFace(ptTrack,trkEyedist);
						trackfound++;
					}
				}
				if(prevfound==0||prevfound!=trackfound){
					FaceDetector.Face[] fullResults = new FaceDetector.Face[N_Faces];
					face_detect.findFaces(bmp, fullResults);
					/* start to copy the results in the faces results array */
					for(int i=0; i<N_Faces; i++){
						if(fullResults[i]==null)
							faces_results[i].clear();
						else{
							PointF mid = new PointF();
							fullResults[i].getMidPoint(mid);
							mid.x *= xScale;
							mid.y *= yScale;
							float eyedist = fullResults[i].eyesDistance()*xScale;
							faces_results[i].setFace(mid,eyedist);
						}
					}
				}
				/* refresh the GUI  to draw the green square and the cross fire */
				handler_.post(new Runnable() {
					public void run() {
						overlay_Layer.postInvalidate();
						// turn off thread lock
						is_ThreadWorking= false;
					}
				});
			}
			
			/* convert 8bit grayscale to RGB32bit (fill R,G,B with Y)*/
			
			@SuppressWarnings("unused")
			private void gray8toRGB32(byte[] gray8, int width, int height, int[] rgb_32s) {
				final int endPtr = width * height;
				int ptr = 0;
				while (true) {
					if (ptr == endPtr)
						break;
					final int Y = gray8[ptr] & 0xff; 
					rgb_32s[ptr] = 0xff000000 + (Y << 16) + (Y << 8) + Y;
					ptr++;
				}
			}
		
		};

		/* Face Result Class */
		// this result class store the indices of the faces by eye locations 
		// from these points we can detect the length and width of the green rectangle 
		// that will be around the faces
		private class FaceResult extends Object {
			private PointF midEye_;
			private float eyeDist_;
			public FaceResult(){
				midEye_ = new PointF(0.0f,0.0f);
				eyeDist_ = 0.0f;
			}
			public void setFace(PointF midEye, float eyeDist){
				set_(midEye,eyeDist);
			}
			public void clear(){
				set_(new PointF(0.0f,0.0f),0.0f);
			}
			private synchronized void set_(PointF midEye, float eyeDist){
				midEye_.set(midEye);
				eyeDist_ = eyeDist;
			}
			public float eyesDistance(){
				return eyeDist_;
			}
			public void getMidPoint(PointF pt){
				pt.set(midEye_);
			}
		};
		
		
	}

//}
