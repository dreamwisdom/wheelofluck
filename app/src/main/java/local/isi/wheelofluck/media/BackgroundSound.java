package local.isi.wheelofluck.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;

import local.isi.wheelofluck.R;

public class BackgroundSound extends AsyncTask<Void, Void, Void> {

    Context ctx;
    MediaPlayer player;

    public BackgroundSound(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected Void doInBackground(Void... params) {
        player = MediaPlayer.create(ctx, R.raw.guitar);
        player.setLooping(true); // Set looping
        player.setVolume(100,100);
        player.start();

        return null;
    }

    public void stop(){
        player.stop();
        player.release();
    }

}