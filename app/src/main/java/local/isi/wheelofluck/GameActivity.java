package local.isi.wheelofluck;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import local.isi.wheelofluck.view.MiddleCircle;
import local.isi.wheelofluck.view.RotatingArrow;

public class GameActivity extends Activity {

    Context ctx;
    Handler handler;
    FrameLayout fl;

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

        Button btnFire = (Button) findViewById(R.id.btn_fire);

        btnFire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RotatingArrow arrow =  new RotatingArrow(ctx, handler);
                fl.addView(arrow);

                handler.post(arrow);

            }
        });
    }


}
