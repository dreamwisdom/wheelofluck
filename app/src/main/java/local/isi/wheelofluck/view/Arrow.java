package local.isi.wheelofluck.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import local.isi.wheelofluck.entities.Level;
import local.isi.wheelofluck.events.ArrowEvent;
import local.isi.wheelofluck.iface.ArrowListener;
import local.isi.wheelofluck.iface.IEndLevel;
import local.isi.wheelofluck.info.GameBoard;

public class Arrow extends View implements Runnable {

    Context ctx;
    Rect arrow;
    static Handler handler;
    Paint p;                                    // arrow color
    int originXY;
    float degree;
    static int w;
    int collisionArea = 5;  // += range >> 1 = 1-3 >> 0 = 2
    static int h;
    int x1;
    int x2;
    int y;

    float offsetDegree;                       // offset in relation to master
    List<ArrowListener> arrArrowListener;
    IEndLevel iEndLevel;

    boolean levelArrow = false;               // Arrow added automaticly at the begining of the level
    boolean isRotating = false;
    boolean isMaster = false;

    // Static variables
    static int debugArrowID;
    static float masterDegree;                // Position of master arrow (circle)
    static List<Float> arrowHist;
    static public int nbArrowsLeft;
    static public boolean removeRunnables;
    static int piercingDistance;
    static Level level;
    static int arrowHeadYOffset;
    static int arrowHeadWidth;
    static int arrowHeadHeight;
    static Paint pArrowHead;

    // Speed Management
    static boolean linearSpeed;
    static boolean normalSpeed = true;
    static int timeSpeedInterval;
    static float speed1Modifier;
    static float speed2Modifier;
    static float baseSpeed = 1;
    static float speed = baseSpeed;
    static long speedTimeStamp;


    Path arrowHead;
    Rect arrowTail;
    Paint pArrowTail;

    // init static variable
    public static void init(Context ctx, Level gLevel, Handler ghandler){

        handler = ghandler;
        arrowHist = new ArrayList<>();

        level = gLevel;
        arrowHeadYOffset = -(int)(GameBoard.getMidCircleRadius(ctx) * 0.06);
        piercingDistance = GameBoard.getMidCircleRadius(ctx) - (int)(GameBoard.getMidCircleRadius(ctx) * 0.06);

        // init arrow size
        int screenWidth = GameBoard.getWidth(ctx);
        arrowHeadWidth = (int)(screenWidth * 0.02);
        arrowHeadHeight = (int)(screenWidth * 0.02);
        w = (int)(screenWidth * 0.012);

        pArrowHead = new Paint();
        pArrowHead.setStyle(Paint.Style.FILL);
        pArrowHead.setColor(Color.BLACK);

        nbArrowsLeft = GameBoard.getLevel(gLevel.getLevel()).getNbArrow();

        // init game speed
        linearSpeed = level.getSpeedInterval() == 0;
        speed1Modifier = level.getSpeed1Modifier();
        speed2Modifier = level.getSpeed2Modifier();
        timeSpeedInterval = level.getSpeedInterval();
        speedTimeStamp = SystemClock.elapsedRealtime();
    }

    // Constructor for the master arrow
    public Arrow(Context context, boolean master) {
        super(context);

        this.ctx = context;

        debugArrowID = 0;
        removeRunnables = false;
        initArrow(context, handler);
        levelArrow = true;

        p.setColor(Color.parseColor("#00000000"));
        isMaster = true;
        isRotating = true;
        //w = 30;
        arrow = new Rect(originXY - w/2,originXY + piercingDistance,originXY + w/2,originXY + h + piercingDistance);

        int triX = originXY;
        int triY = originXY + piercingDistance;

        arrowHead = Calculate(new Point(triX, triY + arrowHeadYOffset),
                new Point(triX - arrowHeadWidth,triY + arrowHeadHeight + arrowHeadYOffset),
                new Point(triX + arrowHeadWidth,triY + arrowHeadHeight + arrowHeadYOffset));

        arrowTail = new Rect(originXY - w*2,
                originXY + piercingDistance + h/2,
                originXY + w*2,
                originXY + piercingDistance + h - h/9);
        pArrowTail = new Paint();
        pArrowTail.setColor(Color.parseColor("#00000000"));

        handler.post(this);
    }

    // Constructor for adding pre-existing arrows in center
    public Arrow(Context context, float degree) {
        super(context);

        ++debugArrowID;
        initArrow(context, handler);
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

        arrowTail = new Rect(originXY - (int)(w*1.5),
                originXY + (int)(piercingDistance + h*.6),
                originXY + (int)(w*1.5),
                originXY + piercingDistance + h - h/9);
        pArrowTail = new Paint();
        pArrowTail.setColor(Color.parseColor("#997ABA7A"));

        p.setColor(Color.parseColor("#FF7ABA7A"));
        addToHistory(degree);
        handler.post(this);
    }

    // Constructor for adding players (ready to shoot) arrows
    public Arrow(Context context) {
        super(context);

        ++debugArrowID;
        initArrow(context, handler);
        arrArrowListener = new ArrayList<>();

        int height = GameBoard.getHeight(ctx);
        x1 = originXY - w/2;
        x2 = originXY + w/2;
        y = (int)(height * 0.8);
        arrow = new Rect(x1 ,y ,x2 ,y + h);

        arrowHead = Calculate(new Point((x1 + x2)/2, y + arrowHeadYOffset),
                new Point((x1 + x2)/2 - arrowHeadWidth, y + arrowHeadHeight + arrowHeadYOffset),
                new Point((x1 + x2)/2 + arrowHeadWidth, y + arrowHeadHeight + arrowHeadYOffset));

        arrowTail = new Rect(originXY - (int)(w*1.5),
                y + (int)(h*.6),
                originXY + (int)(w*1.5),
                y + h - h/9);
        pArrowTail = new Paint();
        pArrowTail.setColor(Color.parseColor("#99CDB99C"));
    }

    public void addArrowListener(ArrowListener arrowListener){
        arrArrowListener.add(arrowListener);
    }
    public void addEndActivity(IEndLevel activity){
        this.iEndLevel = activity;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (isRotating) {
            canvas.save();
            canvas.rotate(degree, originXY, originXY);
            canvas.drawRect(arrow, p);
            canvas.drawPath(arrowHead, pArrowHead);
            canvas.drawRect(arrowTail, pArrowTail);
            canvas.restore();
        } else {
            canvas.drawRect(arrow, p);
            canvas.drawPath(arrowHead, pArrowHead);
            canvas.drawRect(arrowTail, pArrowTail);
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
            if (!isRotating && y > GameBoard.getOriginXY(ctx) + piercingDistance) {
                // Arrow shooting speed
                y -= 75;
                // Successful hit
                if (y < GameBoard.getOriginXY(ctx) + piercingDistance) {
                    y = GameBoard.getOriginXY(ctx) + piercingDistance;
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

                arrowTail = new Rect(originXY - (int)(w*1.5),
                        y + (int)(h*.6),
                        originXY + (int)(w*1.5),
                        y + h - h/9);
            }

            if (!linearSpeed) {
                if (SystemClock.elapsedRealtime() - speedTimeStamp > timeSpeedInterval * 1000) {
                    speedTimeStamp = SystemClock.elapsedRealtime();
                    if (normalSpeed){
                        speed = baseSpeed * speed2Modifier;
                    }else{
                        speed = baseSpeed * speed1Modifier;
                    }
                    normalSpeed = !normalSpeed;
                }
            }

            if (level.isClockwise()) {
                if (degree == 360)
                    degree = 0;

                if (isRotating)
                    degree += speed;
            }else{
                if (degree == 0)
                    degree = 360;

                if (isRotating)
                    degree -= speed;
            }

            // Update master arrow
            if (isMaster) {
                masterDegree = degree;
                //Log.d("collision MasterDegree", "" + masterDegree);
            }

            invalidate();
            handler.post(this);
        }
    }

    public void launch(){
        handler.post(this);
    }

    private void initArrow(Context ctx, Handler handler){

        this.handler = handler;
        this.ctx = ctx;

        // Color
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.parseColor("#FFCDB99C"));

        // Find middle
        int width = GameBoard.getWidth(ctx);
        originXY = GameBoard.getOriginXY(ctx);

        // Arrow Size
        h = (int)((width/2 - GameBoard.getMidCircleRadius(ctx)) * 0.85);
    }

    public boolean addToHistory(float degree) {

        // Print history
        String history = "";
        for (Float arrow : arrowHist) {
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
            for (Float deg : arrowHist) {
                if (Math.round(deg) == i) {
                    collision = true;
                }
            }
        }
        //Log.d("Collision", "" + degreeToCheck);

        if (!collision) {
            if (nbArrowsLeft == 1)
                iEndLevel.nextLevel();
            arrowHist.add(degree);
            Log.d("Collision", "false");
            return false;
        } else {
            Log.d("Collision", "true");
            iEndLevel.endActivity();
            return true;
        }
    }



}

