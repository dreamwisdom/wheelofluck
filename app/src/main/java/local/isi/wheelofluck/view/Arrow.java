package local.isi.wheelofluck.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import local.isi.wheelofluck.entities.Level;
import local.isi.wheelofluck.events.ArrowEvent;
import local.isi.wheelofluck.iface.ArrowListener;
import local.isi.wheelofluck.iface.IEndLevel;
import local.isi.wheelofluck.info.GameBoard;

public class Arrow extends View implements Runnable {

    Context ctx;
    Rect arrow;
    Handler handler;
    Paint p;
    int originXY;
    int degree;
    int w;
    int h;
    int x1;
    int x2;
    int y;
    int speed = 10;
    int offsetDegree;                       // offset in relation to master
    List<ArrowListener> arrArrowListener;
    IEndLevel iEndLevel;

    boolean levelArrow = false;             // Arrow added automaticly at the begining of the level
    boolean isRotating = false;
    boolean isMaster = false;
    static int debugArrowID;
    static int masterDegree;                // Position of master arrow (circle)
    static Map<Integer, Boolean> arrowHistory;
    static public int nbArrowsLeft;
    static public boolean removeRunnables;

    // Constructor for the master arrow
    public Arrow(Context context, Handler handler, boolean master) {
        super(context);

        debugArrowID = 0;
        removeRunnables = false;
        init(context, handler);
        levelArrow = true;
        arrowHistory = new HashMap<>();
        p.setColor(Color.RED);
        isMaster = true;

        isRotating = true;
        int piercingDistance = GameBoard.getMidCircleRadius(ctx) - 10;
        w = 20;
        arrow = new Rect(originXY - w/2,originXY + piercingDistance,originXY + w/2,originXY + h + piercingDistance);

        handler.post(this);
    }

    // Constructor for adding existing arrow in center
    public Arrow(Context context, Handler handler, Level level, int degree) {
        super(context);

        ++debugArrowID;
        init(context, handler);
        levelArrow = true;

        this.degree = degree;
        isRotating = true;
        int piercingDistance = GameBoard.getMidCircleRadius(ctx) - 10;
        arrow = new Rect(originXY - w/2,originXY + piercingDistance,originXY + w/2,originXY + h + piercingDistance);
        addToHistory(degree);
        handler.post(this);
    }

    // Constructor for adding players arrows
    public Arrow(Context context, Handler handler) {
        super(context);

        ++debugArrowID;
        init(context, handler);
        arrArrowListener = new ArrayList<>();

        //int piercingDistance = GameBoard.getMidCircleRadius(ctx) - 10;
        //arrow = new Rect(originXY - w/2,originXY + piercingDistance,originXY + w/2,originXY + h + piercingDistance);
        int height = GameBoard.getHeight(ctx);
        x1 = originXY - w/2;
        x2 = originXY + w/2;
        y = (int)(height * 0.8);
        arrow = new Rect(x1 ,y ,x2 ,y + h);
    }

    public void addArrowListener(ArrowListener arrowListener){
        arrArrowListener.add(arrowListener);
    }
    public void addEndActivity(IEndLevel activity){
        this.iEndLevel = activity;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(isRotating) {
            canvas.save();
            canvas.rotate(degree, originXY, originXY);
            canvas.drawRect(arrow, p);
            canvas.restore();
        }else {
            canvas.drawRect(arrow, p);
        }
    }

    @Override
    public void run() {

        // Stop runnable when activity is killed
        if (!removeRunnables) {

            if (degree == 360)
                degree = 0;

            // Moving arrow to target
            // If arrow is too deep inside, ajust its position
            if (!isRotating && y > GameBoard.getOriginXY(ctx) + GameBoard.getMidCircleRadius(ctx) - 10) {
                // Arrow speed
                y -= 75;
                if (y < GameBoard.getOriginXY(ctx) + GameBoard.getMidCircleRadius(ctx) - 10) {
                    y = GameBoard.getOriginXY(ctx) + GameBoard.getMidCircleRadius(ctx) - 10;
                    offsetDegree = masterDegree;
                    addToHistory(offsetDegree);
                }
                arrow.set(x1, y, x2, y + h);
            } else if (!levelArrow) {
                nbArrowsLeft--;
                arrArrowListener.get(0).ArrowHit(new ArrowEvent(nbArrowsLeft));
                isRotating = true;
                levelArrow = true;
            }

            if(isRotating)
                degree += 1;

            // Update master arrow
            if (isMaster) {
                masterDegree = degree;
                //Log.d("collision MasterDegree", "" + masterDegree);
            }


            invalidate();
            handler.postDelayed(this, speed);
        }
    }

    public void launch(){
        handler.post(this);
    }

    private void init(Context ctx, Handler handler){

        this.handler = handler;
        this.ctx = ctx;

        // Color
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.BLUE);

        // Find middle
        int width = GameBoard.getWidth(ctx);
        originXY = GameBoard.getOriginXY(ctx);

        // Arrow Size
        w = 10;
        h = (int)((width/2 - GameBoard.getMidCircleRadius(ctx)) * 0.85);
    }

    public boolean addToHistory(int degree){

        String history = "";
        // Print history
        for (Map.Entry<Integer, Boolean> entry : arrowHistory.entrySet())
        {
            history += entry.getKey() + " - ";
            //System.out.print(entry.getKey() + "/" + entry.getValue() + " - ");
            //Log.d("Collision", entry.getKey() + "/" + entry.getValue());
        }
        Log.d("Collision", history);
        //System.out.println();
        Log.d("Collision", "" + degree);

        int range = 1;  // += range >> 1 = 1-3 >> 0 = 2

        boolean collision = false;

        for (int i = degree - range; i < degree + range; i++){
            if(arrowHistory.get(i) != null){
                collision = true;
            }
        }

        if(!collision){
            if (nbArrowsLeft == 1)
                iEndLevel.nextLevel();
            arrowHistory.put(degree, true);
            Log.d("Collision", "false");
            return false;
        }else{
            Log.d("Collision", "true");
            iEndLevel.endActivity();
            return true;
        }

    }
}
