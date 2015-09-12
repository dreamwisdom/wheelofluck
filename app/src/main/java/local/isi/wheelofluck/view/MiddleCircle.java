package local.isi.wheelofluck.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import local.isi.wheelofluck.info.GameBoard;

public class MiddleCircle extends View {

    Paint p;
    int originXY;
    Context ctx;

    public MiddleCircle(Context context) {
        super(context);

        this.ctx = context;
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
    }
}
