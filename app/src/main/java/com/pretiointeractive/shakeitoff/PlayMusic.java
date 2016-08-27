package com.pretiointeractive.shakeitoff;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

/**
 * Created by Gyanesh Mishra on 2016-08-27.
 */
public class PlayMusic extends AsyncTask<Void, Void, Void> {

    public static MediaPlayer mp;

    public PlayMusic(Context mContext){
        mp = MediaPlayer.create(mContext, R.raw.michael_scream_short);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(mp.isPlaying()){
            return null;
        }
        mp.start();
        return null;
    }

    public void setOnCompleteListener(MediaPlayer.OnCompletionListener listener){
        mp.setOnCompletionListener(listener);
    }

    public void stop(){
        mp.stop();
    }

    public void pause(){
        mp.pause();
    }

    public void reset(){
        mp.reset();
    }

    public boolean isPlaying(){
        return mp.isPlaying();
    }
}
