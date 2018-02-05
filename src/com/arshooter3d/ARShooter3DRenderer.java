package com.arshooter3d;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


import rajawali.BaseObject3D;
import rajawali.lights.PointLight;
import rajawali.math.Number3D;
import rajawali.parser.ObjParser;
import rajawali.renderer.RajawaliRenderer;
import rajawali.util.MeshExporter;
import rajawali.util.MeshExporter.ExportType;
import rajawali.util.ObjectColorPicker;
import rajawali.util.OnObjectPickedListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.arshooter3d.support.*;


public class ARShooter3DRenderer extends RajawaliRenderer implements OnObjectPickedListener {
	private PointLight mLight;							//light at gun and pointer
	private BaseObject3D gun3d;							//3D gun object
	private final int MAX_FRAMES = 50;					//max number of frames to develop smoke
	private int smokeDevelop, smokeDiminish, smokeCount;//to develop and diminish smoke
	private ExampleParticleSystem2 mParticleSystem;		//smoke behavior
	private ObjectColorPicker mPicker;					//to implement touch event 
	private SoundManager soundManager;					//make shooting sound
	private Number3D mAccValues; 						//container for accelerometer values
	private boolean smoke = false;						//to on and off smoke
	
	public ARShooter3DRenderer(Context context) {
		super(context);
		setFrameRate(60);
		soundManager=new SoundManager(context);
		mAccValues = new Number3D();
	}

	protected void initScene() {
		mPicker = new ObjectColorPicker(this);
		mPicker.setOnObjectPickedListener(this);
		mLight = new PointLight();
		mLight.setPosition(0, 0, -6);
		mLight.setPower(5);
		mCamera.setLookAt(0, 0, 0);
		mCamera.setZ(-12);

		//object file handling section
		try {
			//put .obj file to following code to obtain .ser file
			ObjParser objParser = new ObjParser(mContext.getResources(), mTextureManager, R.raw.ak47_2_obj);
			objParser.parse();
			gun3d = objParser.getParsedObject();
			MeshExporter exporter = new MeshExporter(gun3d);
		    exporter.export("ak47_2_ser", ExportType.SERIALIZED);
			/* After creating .ser file, it can be used to improve app performance
		    But material and color has to be set */ 
			/*ObjectInputStream ois = new ObjectInputStream(mContext.getResources().openRawResource(R.raw.ak47_ser));
			SerializedObject3D serializedGun = (SerializedObject3D) ois.readObject();
			ois.close();
			gun3d = new BaseObject3D(serializedGun);
		    DiffuseMaterial material = new DiffuseMaterial();
			material.setUseColor(true);
			gun3d.setMaterial(material);
			gun3d.setColor(0xff0000);*/
			
			Bitmap particleBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.smoke);
			rajawali.materials.TextureInfo particleTexture = mTextureManager.addTexture(particleBitmap);

			mParticleSystem = new ExampleParticleSystem2();
			mParticleSystem.setPointSize(600);
			mParticleSystem.addTexture(particleTexture);
			mParticleSystem.setPosition(1f, -1.5f, 0f);
			addChild(mParticleSystem);
		
			gun3d.addLight(mLight);
			gun3d.setScale(4f);
			gun3d.setPosition(3, -2, 0);
		
			//add gun in to frame
		    addChild(gun3d);
		    
		    //set gun3d touch sensitive
		    mPicker.registerObject(gun3d);

		} catch (Exception e) {
			e.printStackTrace();
		}

		//set the background color to be transparent to see camera view in background
		//we need to have called setGLBackgroundTransparent(true); in the activity for this to work.
		setBackgroundColor(0);
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		((ArShooterLoading) mContext).showLoader();
		super.onSurfaceCreated(gl, config);
		((ArShooterLoading) mContext).hideLoader();		
	}

	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		try{
			//gun rotation at each frame based on accelerometer input
			gun3d.setRotation(mAccValues.y, mAccValues.x+230f, mAccValues.z-5f);
			
			//smoke set true after touch event
			if(smoke){
				if (smokeDevelop >= MAX_FRAMES){
					smokeCount = smokeDiminish;
					smokeDiminish-=10;
					if (smokeDiminish<=0){
						smokeDevelop = 0;
						smoke = false;
						smokeCount = 0;
					}
				}
				else{
					smokeDevelop++;
					smokeCount = smokeDevelop;
					smokeDiminish = smokeDevelop;
				}
				mParticleSystem.setCurrentFrame(smokeCount);		
				mParticleSystem.setTime((float) smokeCount * .1f);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//to send object to enable touch event
	public void getObjectAt(float x, float y) {
		mPicker.getObjectAt(x, y);
	}

	//to mimic shoot
	public void onObjectPicked(BaseObject3D object) {
		soundManager.playSound(0);
		object.setZ(object.getZ() == 0 ? -2 : 0);
		smoke = true;
	}
	
	//set accelerometer input to gun movement
	public void setAccelerometerValues(float x, float y, float z) {
		mAccValues.setAll(-x, -y, -z);
	}
}
