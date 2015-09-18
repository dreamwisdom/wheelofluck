package local.isi.wheelofluck;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Range;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

    Context ctx;
    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String PREFS_LEVEL = "level";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//            Range<Integer> volume =new Range<Integer>(0,100);
        ctx=this;
//        MediaPlayer mp = MediaPlayer.create(ctx, R.raw.arrow);
//        mp.start();
        setContentView(R.layout.activity_main);

        // Btn start
        ImageView btnStart = (ImageView) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get saved level
                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                int level = settings.getInt("level", 1);

                Intent gameIntent = new Intent(MainActivity.this, GameActivity.class);
                gameIntent.putExtra(PREFS_LEVEL, level);
                startActivity(gameIntent);
            }
        });

        // Btn cheat
        ImageView btnCheat = (ImageView) findViewById(R.id.btn__cheat);
        btnCheat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gameIntent = new Intent(MainActivity.this, DebugActivity.class);
                startActivity(gameIntent);
            }
        });
//Btn exit
        ImageView btnQuitter = (ImageView) findViewById(R.id.btn_quitter);
        btnQuitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //Btn Option
        ImageView btnOption = (ImageView) findViewById(R.id.btn_option);
        btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

                // set title
                alertDialogBuilder.setTitle("Option");

//                 set dialog message
                alertDialogBuilder
                        .setMessage("Click yes to exit!")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, just close
                                // the dialog box and do nothing
                                dialog.cancel();
                            }
                        });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
