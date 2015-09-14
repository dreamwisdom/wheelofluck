package local.isi.wheelofluck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import local.isi.wheelofluck.iface.IEndLevel;
import local.isi.wheelofluck.info.GameBoard;
import local.isi.wheelofluck.view.MiddleCircle;
import local.isi.wheelofluck.view.Arrow;

public class GameLevel extends Activity implements ArrowListener, IEndLevel {

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
        int lv = getIntent().getIntExtra("level", 0);
        Arrow.nbArrowsLeft = GameBoard.getLevel(lv).getNbArrow();
        Arrow masterArrow = new Arrow(ctx, handler, true);
        fl.addView(masterArrow);

        // Init level
        GameBoard.initLevel(ctx, handler, fl, lv);

        // First arrow
        arrow = new Arrow(ctx, handler);
        arrow.addArrowListener(this);
        arrow.addEndActivity(this);
        fl.addView(arrow);

        timeStamp = SystemClock.elapsedRealtime();

        // Level msg
        TextView tvLevel = (TextView) findViewById(R.id.tv_level);
        tvLevel.setText("Level: " + lv);

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
                arrow.addEndActivity(this);

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
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Arrow.removeRunnables = true;
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // Kill activity on back button and ?
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        Arrow.removeRunnables = true;
//        finish();
    }

    @Override
    public void endActivity() {

            new AlertDialog.Builder(ctx)
                    .setTitle("Game over")
                    .setMessage("Main menu")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO
                            Arrow.removeRunnables = true;
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

    }

    @Override
    public void nextLevel() {
        // kill all runnable
        Arrow.removeRunnables = true;

        // show dialog
        new AlertDialog.Builder(ctx)
                .setTitle("Success")
                .setMessage("Next level?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO
                        Intent intent = getIntent();
                        int level = intent.getIntExtra("level", 0);
                        getIntent().putExtra("level", ++level);
                        startActivity(getIntent());
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
