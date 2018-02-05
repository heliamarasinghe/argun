
package com.example.arshooter2d;

import com.arshooter3d.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundManager
{
  private SoundPool soundPool;
  private int[] sm;
  Context context;
 

  public SoundManager(Context context) {
    this.context = context;
    soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
    sm = new int[3];
    sm[0] = soundPool.load(context, R.raw.destroy_group, 1);
    sm[1] = soundPool.load(context, R.raw.lose, 1);
    sm[2] = soundPool.load(context, R.raw.launch, 1);
  
  }

  public final void playSound(int sound) {
    if (true) {  
      AudioManager mgr = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);  
      float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
      float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
      float volume = streamVolumeCurrent / streamVolumeMax;
      soundPool.play(sm[sound], volume, volume, 1, 0, 1f);
    }
  }

  public final void cleanUp() {
    sm = null;
    context = null;
    soundPool.release();
    soundPool = null;
  }
}
