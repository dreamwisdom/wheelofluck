package local.isi.wheelofluck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.TextView;

import local.isi.wheelofluck.entities.Level;
import local.isi.wheelofluck.events.ArrowEvent;
import local.isi.wheelofluck.iface.ArrowListener;
import local.isi.wheelofluck.iface.IEndLevel;
import local.isi.wheelofluck.info.GameBoard;
import local.isi.wheelofluck.view.MiddleCircle;
import local.isi.wheelofluck.view.Arrow;

public class GameActivity extends Activity implements ArrowListener, IEndLevel {

    Context ctx;
    Handler handler;
    FrameLayout fl;
    Arrow arrow;
    long timeStamp;
    TextView tvArrow;
    int level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change behavior of volume button
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        setContentView(R.layout.activity_game);
        ctx = this;
        handler = new Handler();
        int lv = getIntent().getIntExtra("level", 0);
        // Adding middle circle to layout
        MiddleCircle centerCircle = new MiddleCircle(this, lv);
        fl = (FrameLayout) findViewById(R.id.game_activity);
        fl.addView(centerCircle);

        // init master arrow

        Level level = GameBoard.getLevel(lv);
        Arrow.init(ctx, level, handler);
        Arrow masterArrow = new Arrow(ctx, true);
        fl.addView(masterArrow);

        // Init level
        GameBoard.initLevel(ctx, fl, lv);

        // First arrow
        arrow = new Arrow(ctx);
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
                arrow = new Arrow(ctx);
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
        Log.d("xxxdebug", "in onKeyDown");
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            Arrow.removeRunnables = true;
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void endActivity() {
        Log.d("xxxdebug", "in endActivity");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Game over")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Arrow.removeRunnables = true;
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void nextLevel() {
        Log.d("xxxdebug", "in Next level");
        // kill all runnable
        Arrow.removeRunnables = true;

        Intent intent = getIntent();
        level = intent.getIntExtra("level", 0) + 1;

        // save level
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(MainActivity.PREFS_LEVEL, level);
        editor.commit();

        // show dialog
        new AlertDialog.Builder(ctx)
                .setTitle("Success")
                .setMessage("Next level?")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        getIntent().putExtra("level", level);
                        startActivity(getIntent());
                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Arrow.removeRunnables = true;
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("xxxdebug", "in onPause");
        Arrow.removeRunnables = true;
        finish();
    }
}
