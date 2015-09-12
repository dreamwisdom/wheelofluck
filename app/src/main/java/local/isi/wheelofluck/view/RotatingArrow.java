package local.isi.wheelofluck.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.View;

import local.isi.wheelofluck.info.GameBoard;

public class RotatingArrow extends View implements Runnable {

    Rect arrow;
    Handler handler;
    Paint p;
    int originXY;
    int angle;
    Context ctx;

    public RotatingArrow(Context context, Handler handler) {
        super(context);
        this.handler = handler;

        this.ctx = context;
        int w;
        int h;

        // Color
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.BLUE);

        // Find middle
        int width = GameBoard.getWidth(context);
        originXY = GameBoard.getOriginXY(context);

        // Arrow Size
        w = 15;
        h = (int)((width/2 - GameBoard.getMidCircleRadius(ctx)) * 0.85);


        int piercingDistance = GameBoard.getMidCircleRadius(ctx) - 10;
        arrow = new Rect(originXY - w/2,originXY + piercingDistance,originXY + w/2,originXY + h + piercingDistance);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        angle += 1;
        canvas.save();
        canvas.rotate(angle, originXY, originXY);
        canvas.drawRect(arrow, p);
        canvas.restore();
    }

    @Override
    public void run() {

        invalidate();
        handler.postDelayed(this, 10);
    }
}
