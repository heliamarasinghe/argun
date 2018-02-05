package com.arshooter3d;


import java.util.Arrays;
import java.util.List;

import com.arshooter3d.support.ExamplesAdapter;
import com.example.arshooter2d.ARShooter2DActivity;
import com.example.arshooterp.ARShooterP;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

public class ARShooterSelectMode extends ListActivity {
	private ExampleItem[] mItems = { 
			new ExampleItem("Play AR Shooter 2D", ARShooter2DActivity.class),
			new ExampleItem("Play AR Shooter 3D", ARShooter3DActivity.class),
			new ExampleItem("Wanna Shoot em ?", ARShooterP.class),
	    	//new ExampleItem("Wanna Shoot em ?", DArCameraPreviewActivity.class),
			new ExampleItem("Help", ARShooter3DActivity.class),
			new ExampleItem("Quit",this)};
	private MediaPlayer introTrack;

	public void onCreate(Bundle savedInstanceState) {
		String[] strings = new String[mItems.length];
		for (int i = 0; i < mItems.length; i++) {
			strings[i] = mItems[i].title;
		}
		List<String> list = Arrays.asList(strings);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		
		
		setListAdapter(new ExamplesAdapter(this, list));

		TextView linkView = (TextView) findViewById(R.id.textView1);
		//linkView.setPadding(10, 0, 10, 20);
		//linkView.setTextSize(20);
		linkView.setText(getString(R.string.headding));
		
		linkView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) 70);
		//unit parameter is TypedValue.COMPLEX_UNIT_PX and the size parameter is the percent of total width of the screen
		//linkView.setTextColor(0xaafcf94c);
		//linkView.setLinkTextColor(0xaaffffff);

		Linkify.addLinks(linkView, Linkify.WEB_URLS);

		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/AgentOrange.ttf");
		linkView.setTypeface(font);
	}
	@Override
	public void onDestroy(){
		super.onDestroy();
		//killApp(true);
	  finish();
	}

	@Override
	public void onResume(){
		super.onResume();
		introTrack = MediaPlayer.create(ARShooterSelectMode.this, R.raw.intro);
		introTrack.start();
		introTrack.setLooping(true);
	}

	public void onListItemClick(ListView parent, View v, int position, long id) {
		if (position==4)
		{
			Log.d("aaa",""+position);
			onDestroy();
		    // Exit();
		}
		else
		startActivity(new Intent(this, mItems[position].exampleClass));
	}
	@Override
	public void onPause(){
		super.onPause();
		introTrack.release();
	}
	

	class ExampleItem {
		public String title;
		public Class<?> exampleClass;
		//ARShooterSelectMode ARM;

		public ExampleItem(String title, Class<?> exampleClass) {
			this.title = title;
			this.exampleClass = exampleClass;
		}
		public ExampleItem(String title,ARShooterSelectMode nn) {
			this.title = title;
			//this.ARM = nn;
		//	nn.onDestroy();
			//ARM.onDestroy();
		}
	}
}