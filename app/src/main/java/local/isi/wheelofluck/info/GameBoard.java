package local.isi.wheelofluck.info;

import android.content.Context;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

import local.isi.wheelofluck.entities.Level;
import local.isi.wheelofluck.view.Arrow;

public class GameBoard {

    // Return width of the game board
    public static int getWidth(Context ctx){
        DisplayMetrics metric = ctx.getResources().getDisplayMetrics();
//        int screenWidth = metric.widthPixels;
//        int screenHeight = metric.heightPixels;
//        return Math.min(screenWidth,screenHeight);
        return metric.widthPixels;
    }

    // Return the center coords of the game board
    public static int getOriginXY(Context ctx){
        return getWidth(ctx) /2;
    }

    public static int getMidCircleRadius(Context ctx){
        return getWidth(ctx) / 6;
    }

    public static int getHeight(Context ctx){
        DisplayMetrics metric = ctx.getResources().getDisplayMetrics();
        return metric.heightPixels;
    }

    public static Level getLevel(int lv){

        List<Level> levels = new ArrayList<>();
        boolean clockwise = true;

        int startArrows = 5;
        int startObstacle = 3;
        int speed = 10;

        for(int i = 1; i <= 100; i++){

            clockwise = !clockwise;
            if(clockwise)
                startArrows++;

            Level level = new Level(clockwise, startArrows, startObstacle, speed);
            levels.add(level);
        }

        return levels.get(lv - 1);
    }

    public static void initLevel(Context ctx, Handler handler, FrameLayout fl, int lv){
        Level level = GameBoard.getLevel(lv);

        int nbArrow = level.getNbArrow();
        int degreeIncrement = 360/ nbArrow;
        int degree = 0;

        for (int i = 0; i < nbArrow; i++){
            Arrow arrow = new Arrow(ctx, handler, degree);
            fl.addView(arrow);
            degree += degreeIncrement;
        }

    }

}
