package com.android.learn.base.view.colorfultab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class ColorClipTestView extends View {
    float per = 0;
    float x = 0, y = 0;

    public ColorClipTestView(Context context) {
        super(context);
    }

    public ColorClipTestView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorClipTestView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(80);
        paint.setStyle(Paint.Style.FILL);

        paint.setTextSize(68);
        canvas.save();
        canvas.drawText("我是火车王", 200, 200, paint);
        canvas.restore();
        paint.setColor(Color.RED);
        canvas.clipRect(200, 0, 320 * per, 400);
        Log.e("per", "per:" + per);
        canvas.drawText("我是火车王", 200, 200, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e("log", "ACTION_DOWN");
                x = event.getRawX();
                y = event.getRawY();
                return true;
            case MotionEvent.ACTION_MOVE:
                Log.e("log", "ACTION_MOVE");
                float currentX = event.getRawX();
                float currentY = event.getRawY();
                if (currentY > y) {
                    per = ((currentY - y) / y) * 10;
                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void move(Canvas canvas, Paint paint, float per) {
        canvas.save();
        float right = 300 * per;

        canvas.restore();
    }
}
