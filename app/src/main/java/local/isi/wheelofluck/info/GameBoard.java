package local.isi.wheelofluck.info;

import android.content.Context;
import android.util.DisplayMetrics;

public class GameBoard {

    // Return width of the game board
    public static int getWidth(Context ctx){
        DisplayMetrics metric = ctx.getResources().getDisplayMetrics();
        int screenWidth = metric.widthPixels;
        int screenHeight = metric.heightPixels;
        return Math.min(screenWidth,screenHeight);
    }

    // Return the center coords of the game board
    public static int getOriginXY(Context ctx){
        return getWidth(ctx) /2;
    }

    public static int getMidCircleRadius(Context ctx){
        return getWidth(ctx) / 6;
    }

}
