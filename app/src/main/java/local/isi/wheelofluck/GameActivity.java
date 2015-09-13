package local.isi.wheelofluck;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import local.isi.wheelofluck.events.ArrowEvent;
import local.isi.wheelofluck.iface.ArrowListener;
import local.isi.wheelofluck.info.GameBoard;
import local.isi.wheelofluck.view.MiddleCircle;
import local.isi.wheelofluck.view.Arrow;

public class GameActivity extends Activity implements ArrowListener{

    Context ctx;
    Handler handler;
    FrameLayout fl;
    Arrow arrow;
    long timeStamp;
    TextView tvArrow;

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

        // init master arrow
        Arrow masterArrow = new Arrow(ctx, handler, true);
        fl.addView(masterArrow);

        // Init level
        int lv = getIntent().getIntExtra("level", 0);
        GameBoard.initLevel(ctx, handler, fl, lv);
        Arrow.nbArrowsLeft = GameBoard.getLevel(lv).getNbArrow();

        // First arrow
        arrow = new Arrow(ctx, handler);
        arrow.addArrowListener(this);
        fl.addView(arrow);

        timeStamp = SystemClock.elapsedRealtime();

        // Arrow left msg
        tvArrow = (TextView) findViewById(R.id.tv_arrow);
        tvArrow.setText("Arrows left" + GameBoard.getLevel(lv).getNbArrow());
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        tvArrow.layout(GameBoard.getOriginXY(ctx), GameBoard.getOriginXY(ctx), GameBoard.getOriginXY(ctx) + 40, GameBoard.getOriginXY(ctx) + 40);
//    }

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
                arrow.addArrowListener(this);

                // TODO fix - causes crashed
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

    @Override
    public void ArrowHit(ArrowEvent evt) {
        tvArrow.setText("Arrow left:" + evt.getNbArrow());
    }

    // Kill activity on back button
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if ((keyCode == KeyEvent.KEYCODE_BACK))
//        {
//            Arrow.removeRunnables = true;
//            //finish();
//        }
//        return super.onKeyDown(keyCode, event);
//    }

    // Kill activity on back button and ?
    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
    }
}
