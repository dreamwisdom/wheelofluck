package local.isi.wheelofluck.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import local.isi.wheelofluck.GameActivity;
import local.isi.wheelofluck.MainActivity;
import local.isi.wheelofluck.R;

public class LevelsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        // Get current level
        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
        int realLevel = settings.getInt("level", 1);

        LinearLayout ll = (LinearLayout) findViewById(R.id.levels_layout);

        int lv = 0;

        for (int i = 0; i < 20; i++){
            LinearLayout newLl = new LinearLayout(this);
            newLl.setOrientation(LinearLayout.HORIZONTAL);
            ll.addView(newLl);
            for (int a = 1; a <= 5; a++){
                lv++;
                Button btn = new Button(this);
                btn.setTextColor(Color.parseColor("#971f11"));

                if (lv > realLevel) {
                    btn.setEnabled(false);
                    btn.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                }

                // set weight (last param
                btn.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                btn.setText("" + (lv));

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int level = Integer.parseInt(((Button) v).getText().toString());
                        Intent intent = new Intent(LevelsActivity.this, GameActivity.class);
                        intent.putExtra("level", level);
                        startActivity(intent);
                        finish();
                    }
                });
                newLl.addView(btn);
            }
        }

    }


}
