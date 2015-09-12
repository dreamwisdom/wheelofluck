package local.isi.wheelofluck;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Chronometer;
import android.widget.FrameLayout;

import local.isi.wheelofluck.view.MiddleCircle;
import local.isi.wheelofluck.view.Arrow;

public class GameActivity extends Activity {

    Context ctx;
    Handler handler;
    FrameLayout fl;
    Arrow arrow;
    long timeStamp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ctx = this;
        handler = new Handler();

        // Adding middle circle to layout
        MiddleCircle centerCircle = new MiddleCircle(this);
        fl = (FrameLayout) findViewById(R.id.game_activity);
        fl.addView(centerCircle);

        arrow = new Arrow(ctx, handler);
        fl.addView(arrow);

        timeStamp = SystemClock.elapsedRealtime();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_DOWN) {




            if(SystemClock.elapsedRealtime() - timeStamp > 300) {
                // reset timer
                timeStamp = SystemClock.elapsedRealtime();

                // Play sound
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.arrow);
                mp.start();
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }
                });

                arrow.launch();
                arrow = new Arrow(ctx, handler);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fl.addView(arrow);
                    }
                }, 200);
            }


        }

        return super.onTouchEvent(event);
    }
}
