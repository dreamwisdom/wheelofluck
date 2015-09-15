package local.isi.wheelofluck.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
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
    float degree;
    static int w = 14;
    int collisionArea = 3;  // += range >> 1 = 1-3 >> 0 = 2
    int h;
    int x1;
    int x2;
    int y;
    int speed = 10;
    float offsetDegree;                       // offset in relation to master
    List<ArrowListener> arrArrowListener;
    IEndLevel iEndLevel;

    boolean levelArrow = false;             // Arrow added automaticly at the begining of the level
    boolean isRotating = false;
    boolean isMaster = false;
    static int debugArrowID;
    static float masterDegree;                // Position of master arrow (circle)
    //static Map<Integer, Boolean> arrowHistory;
    static List<Float> arrowHist;
    static public int nbArrowsLeft;
    static public boolean removeRunnables;
    static int piercingDistance;
    static Level level;
    static int arrowHeadYOffset = -10;
    static int arrowHeadWidth = 30;
    static int arrowHeadHeight = 20;

    static Paint pArrowHead;
    Path arrowHead;

    // Constructor for the master arrow
    public Arrow(Context context, Handler handler, Level level, boolean master) {
        super(context);

        this.level = level;
        debugArrowID = 0;
        removeRunnables = false;
        init(context, handler);
        levelArrow = true;
        //arrowHistory = new HashMap<>();
        arrowHist = new ArrayList<>();
        p.setColor(Color.RED);
        isMaster = true;
        piercingDistance = GameBoard.getMidCircleRadius(ctx) - 10;

        isRotating = true;
        //int piercingDistance = GameBoard.getMidCircleRadius(ctx) - 10;
        //w = 30;
        arrow = new Rect(originXY - w/2,originXY + piercingDistance,originXY + w/2,originXY + h + piercingDistance);

        pArrowHead = new Paint();
        pArrowHead.setStyle(Paint.Style.FILL);
        pArrowHead.setColor(Color.BLACK);

        int triX = originXY;
        int triY = originXY + piercingDistance;

        arrowHead = Calculate(new Point(triX, triY + arrowHeadYOffset),
                new Point(triX - arrowHeadWidth,triY + arrowHeadHeight + arrowHeadYOffset),
                new Point(triX + arrowHeadWidth,triY + arrowHeadHeight + arrowHeadYOffset));

        handler.post(this);
    }

    // Constructor for adding pre-existing arrows in center
    public Arrow(Context context, Handler handler, float degree) {
        super(context);

        ++debugArrowID;
        init(context, handler);
        levelArrow = true;

        this.degree = degree;

        isRotating = true;

        // arrow position
        arrow = new Rect(originXY - w/2,originXY + piercingDistance,originXY + w/2,originXY + h + piercingDistance);

        // arrow head position
        int triX = originXY;
        int triY = originXY + piercingDistance;

        arrowHead = Calculate(new Point(triX, triY + arrowHeadYOffset),
                new Point(triX - arrowHeadWidth,triY + arrowHeadHeight + arrowHeadYOffset),
                new Point(triX + arrowHeadWidth,triY + arrowHeadHeight + arrowHeadYOffset));

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

        arrowHead = Calculate(new Point((x1 + x2)/2, y + arrowHeadYOffset),
                new Point((x1 + x2)/2 - arrowHeadWidth, y + arrowHeadHeight + arrowHeadYOffset),
                new Point((x1 + x2)/2 + arrowHeadWidth, y + arrowHeadHeight + arrowHeadYOffset));
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
            canvas.drawPath(arrowHead, pArrowHead);
            canvas.restore();
        }else {

            canvas.drawRect(arrow, p);
            canvas.drawPath(arrowHead, pArrowHead);
        }
    }
    private Path Calculate(Point A, Point B, Point C) {
        Path Pencil = new Path();
        Pencil.moveTo(A.x, A.y);
        Pencil.lineTo(B.x, B.y);
        Pencil.lineTo(C.x, C.y);
        return Pencil;
    }
    @Override
    public void run() {

        // Stop runnable when activity is killed
        if (!removeRunnables) {
            // Moving arrow to target
            // If arrow is too deep inside, adjust its position
            if (!isRotating && y > GameBoard.getOriginXY(ctx) + GameBoard.getMidCircleRadius(ctx) - 10) {
                // Arrow speed
                y -= 75;
                // Successful hit
                if (y < GameBoard.getOriginXY(ctx) + GameBoard.getMidCircleRadius(ctx) - 10) {
                    y = GameBoard.getOriginXY(ctx) + GameBoard.getMidCircleRadius(ctx) - 10;
                    offsetDegree = masterDegree;
                    addToHistory(offsetDegree);

                    nbArrowsLeft--;
                    arrArrowListener.get(0).ArrowHit(new ArrowEvent(nbArrowsLeft));
                    isRotating = true;
                    levelArrow = true;
                }
                arrow.set(x1, y, x2, y + h);
                arrowHead = Calculate(new Point((x1 + x2)/2, y + arrowHeadYOffset),
                        new Point((x1 + x2)/2 - arrowHeadWidth, y + arrowHeadHeight + arrowHeadYOffset),
                        new Point((x1 + x2)/2 + arrowHeadWidth, y + arrowHeadHeight + arrowHeadYOffset));
//                arrowHead = Calculate(new Point((x1 + x2)/2, y), new Point((x1 + x2)/2 - 40,y +40),
//                        new Point((x1 + x2)/2 + 40,y + 40));
            }
            if (level.isClockwise()) {
                if (degree == 360)
                    degree = 0;

                if (isRotating)
                    degree += 1;
            }else{
                if (degree == 0)
                    degree = 360;

                if (isRotating)
                    degree -= 1;
            }

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
        h = (int)((width/2 - GameBoard.getMidCircleRadius(ctx)) * 0.85);
    }

    public boolean addToHistory(float degree){

        // Print history
        String history = "";
        for (Float arrow: arrowHist){
            history += arrow + " - ";
        }

        Log.d("Collision", history);
        Log.d("Collision", "" + degree);



        boolean collision = false;

        // String degreeToCheck = "To Check ";

        // Loop on collision area
        int roundDegree = Math.round(degree);

        for (int i = (roundDegree - collisionArea); i <= roundDegree + collisionArea; i++) {
            // degreeToCheck += i + " - ";

            // Check if degree is in history
            for (Float deg: arrowHist) {
                    if (Math.round(deg) == i) {
                    collision = true;
                }
            }
        }
        //Log.d("Collision", "" + degreeToCheck);

        if(!collision){
            if (nbArrowsLeft == 1)
                iEndLevel.nextLevel();
            arrowHist.add(degree);
            Log.d("Collision", "false");
            return false;
        }else{
            Log.d("Collision", "true");
            iEndLevel.endActivity();
            return true;
        }

    }
}
