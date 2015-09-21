package local.isi.wheelofluck.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;

import local.isi.wheelofluck.info.GameBoard;

public class MiddleCircle extends View {

    Paint p;
    int originXY;
    Context ctx;
    int lv;

    public MiddleCircle(Context context, int lv) {
        super(context);

        this.ctx = context;
        this.lv = lv;
        // Color
        p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.RED);

        // Find middle
        originXY = GameBoard.getOriginXY(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(originXY, originXY, GameBoard.getMidCircleRadius(ctx), p);

        Paint t = new Paint();
        t.setColor(Color.BLACK);
//        t.setTextSize(100);   // Galaxy s5
        t.setTextSize((int)(GameBoard.getMidCircleRadius(ctx) * 0.5));
        t.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        if(lv <10){
            int offsetX = GameBoard.getMidCircleRadius(ctx)/7;
            int offsetY = GameBoard.getMidCircleRadius(ctx)/5;
            canvas.drawText("" + lv, originXY- offsetX, originXY + offsetY, t);
        }else{
            int offsetX = GameBoard.getMidCircleRadius(ctx)/3;
            int offsetY = GameBoard.getMidCircleRadius(ctx)/5;
            canvas.drawText("" + lv, originXY- offsetX, originXY + offsetY, t);
        }


    }
}
