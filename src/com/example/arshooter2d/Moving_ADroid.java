
package com.example.arshooter2d;


import java.util.Timer;
import java.util.TimerTask;

//import tut.camera.CameraPreview.UpdateDrawTask;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;


public class Moving_ADroid {

	private Bitmap bitmap;	// the actual bitmap
	private int x;			// the X coordinate
	private int y;			// the Y coordinate
	
	private Speed speed;	// the speed with its directions
	

	public Moving_ADroid(Bitmap bitmap, int x, int y,float sx,float sy,int dx,int dy) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.speed = new Speed(sx,sy,dx,dy);
		
		
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}

	
	
	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}

	public void draw(Canvas canvas) {
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
		
	    
	}
	
	public void draw_corss(Canvas canvas) {
		canvas.drawBitmap(bitmap, x, y , null);
		
	    
	}
	
	
	public void update() {
		
			x += (speed.getXv() * speed.getxDirection()); 
			y += (speed.getYv() * speed.getyDirection());
		
	}
	 
	
}
