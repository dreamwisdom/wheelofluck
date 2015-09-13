package local.isi.wheelofluck.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

import local.isi.wheelofluck.entities.Level;
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
    boolean isRotating = false;
    int speed = 10;

    // Constructor for adding existing arrrow in center
    public Arrow(Context context, Handler handler, int degree) {
        super(context);

        init(context, handler);

        this.degree = degree;
        isRotating = true;
        int piercingDistance = GameBoard.getMidCircleRadius(ctx) - 10;
        arrow = new Rect(originXY - w/2,originXY + piercingDistance,originXY + w/2,originXY + h + piercingDistance);
        handler.post(this);
    }

    // Constructor for adding players arrow
    public Arrow(Context context, Handler handler) {
        super(context);

        init(context, handler);

        //int piercingDistance = GameBoard.getMidCircleRadius(ctx) - 10;
        //arrow = new Rect(originXY - w/2,originXY + piercingDistance,originXY + w/2,originXY + h + piercingDistance);
        int height = GameBoard.getHeight(ctx);
        x1 = originXY - w/2;
        x2 = originXY + w/2;
        y = (int)(height * 0.8);
        arrow = new Rect(x1 ,y ,x2 ,y + h);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if(isRotating) {
            degree += 1;
            canvas.save();
            canvas.rotate(degree, originXY, originXY);
            canvas.drawRect(arrow, p);
            canvas.restore();
        }else{
            canvas.drawRect(arrow, p);
        }
    }

    @Override
    public void run() {

            // Moving arrow to target
            // If arrow is too deep inside, ajust its position
            if (!isRotating && y > GameBoard.getOriginXY(ctx) + GameBoard.getMidCircleRadius(ctx) - 10) {
                y -= 75;
                if (y < GameBoard.getOriginXY(ctx) + GameBoard.getMidCircleRadius(ctx) - 10) {
                    y = GameBoard.getOriginXY(ctx) + GameBoard.getMidCircleRadius(ctx) - 10;
                }
                arrow.set(x1, y, x2, y + h);
            } else {
                isRotating = true;
            }

            invalidate();
            handler.postDelayed(this, speed);
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
        w = 15;
        h = (int)((width/2 - GameBoard.getMidCircleRadius(ctx)) * 0.85);
    }
}
