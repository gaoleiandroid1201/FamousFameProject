package com.android.learn.base.view.colorfultab;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.gaolei.basemodule.R;

/**
 * Created by rookie on 2018/4/24.
 */

public class ColorClipView extends View {

    private Paint paint;//画笔
    private String text = "我是不哦车网";//绘制的文本
    private int textSize = sp2px(18);//文本字体大小

    private int textWidth;//文本的宽度
    private int textHeight;//文本的高度

    private int textUnselectColor = R.color.colorPrimary;//文本未选中字体颜色
    private int textSelectedColor = R.color.colorAccent;//文本选中颜色

    private static final int DIRECTION_LEFT = 0;
    private static final int DIRECTION_RIGHT = 1;
    private static final int DIRECTION_TOP = 2;
    private static final int DIRECTION_BOTTOM = 3;

    private int mDirection = DIRECTION_LEFT;

    private Rect textRect = new Rect();//文本显示区域

    private int startX;//X轴开始绘制的坐标

    private int startY;//y轴开始绘制的坐标

    private int baseLineY;//基线的位置

    private float progress;


    public ColorClipView(Context context) {
        this(context, null);
    }

    public ColorClipView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //初始化各个属性包括画笔

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.ColorClipView);
        text = ta.getString(R.styleable.ColorClipView_text);
        textSize = ta.getDimensionPixelSize(R.styleable.ColorClipView_text_size, textSize);
//        textUnselectColor = ta.getColor(R.styleable.ColorClipView_text_unselected_color, textUnselectColor);
//        textSelectedColor = ta.getColor(R.styleable.ColorClipView_text_selected_color, textSelectedColor);
        mDirection = ta.getInt(R.styleable.ColorClipView_direction, mDirection);
        progress = ta.getFloat(R.styleable.ColorClipView_progress, 0);
        ta.recycle();//用完就得收！
        paint.setTextSize(textSize);
    }

    private int sp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                dpVal, getResources().getDisplayMetrics());
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setTextSize(int mTextSize) {
        this.textHeight = mTextSize;
        paint.setTextSize(mTextSize);
        requestLayout();
        invalidate();
    }

    public void setText(String text) {
        this.text = text;
        requestLayout();
        invalidate();
    }

    public void setDirection(int direction) {
        this.mDirection = direction;
        invalidate();
    }

    public void setTextUnselectColor(int unselectColor) {
        this.textUnselectColor = unselectColor;
        invalidate();
    }

    public void setTextSelectedColor(int selectedColor) {
        this.textSelectedColor = selectedColor;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureText();//测量文本的长宽

        int width = measureWidth(widthMeasureSpec);//通过模式的不同来测量出实际的宽度
        int height = measureHeight(heightMeasureSpec);//通过模式的不同来测量出实际的高度
        setMeasuredDimension(width, height);
        Log.e("tag", "七点" + (getMeasuredWidth() - getPaddingRight() - getPaddingLeft()));
        startX = (getMeasuredWidth() - getPaddingRight() - getPaddingLeft()) / 2 - textWidth / 2;
        startY = (textHeight - getPaddingBottom() - getPaddingTop());
    }

    private int measureHeight(int heightMeasureSpec) {
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);
        int realSize = 0;
        switch (mode) {
            case MeasureSpec.EXACTLY:
                realSize = size;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                realSize = textHeight;
                realSize += getPaddingTop() + getPaddingBottom();
                break;
        }
        realSize = mode == MeasureSpec.AT_MOST ? Math.min(realSize, size) : realSize;
        return realSize;
    }

    private int measureWidth(int widthMeasureSpec) {
        int mode = MeasureSpec.getMode(widthMeasureSpec);//通过widthMeasureSpec拿到Mode
        int size = MeasureSpec.getSize(widthMeasureSpec);//同理
        int realSize = 0;//最后返回的值
        switch (mode) {
            case MeasureSpec.EXACTLY://精确模式下直接用给出的宽度
                realSize = size;
                break;
            case MeasureSpec.AT_MOST://最大模式
            case MeasureSpec.UNSPECIFIED://未指定模式
                //这两种情况下,用测量出的宽度加上左右padding
                realSize = textWidth;
                realSize = realSize + getPaddingLeft() + getPaddingRight();
                break;
        }
        //如果mode为最大模式,不应该大于父类传入的值,所以取最小
        realSize = mode == MeasureSpec.AT_MOST ? Math.min(realSize, size) : realSize;
        return realSize;
    }

    private void measureText() {
        textWidth = (int) paint.measureText(text);//测量文本宽度
        Log.d("tag", "measureText=" + paint.measureText(text));


        //直接通过获得文本显示范围,再获得高度
        //参数里，text 是要测量的文字
        //start 和 end 分别是文字的起始和结束位置，textRect 是存储文字显示范围的对象，方法在测算完成之后会把结果写进 textRect。
        paint.getTextBounds(text, 0, text.length(), textRect);
        textHeight = textRect.height();

        //通过文本的descent线与top线的距离来测量文本高度,这是其中一种测量方法
        Paint.FontMetrics fm = paint.getFontMetrics();
        textHeight = (int) Math.ceil(fm.descent - fm.top);

        baseLineY = (int) (textHeight / 2 - (fm.bottom - fm.top) / 2 - fm.top);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //OK~开始绘制咯~
        //首先先判断方向是左还是右呢?  是上还是下呢? 真期待....
        Log.e("tag", "OnDraw");
        if (mDirection == DIRECTION_LEFT) {
            //绘制朝左的选中文字
            drawHorizontalText(canvas, textSelectedColor, startX,
                    (int) (startX + progress * textWidth));
            //绘制朝左的未选中文字
            drawHorizontalText(canvas, textUnselectColor, (int) (startX + progress
                    * textWidth), startX + textWidth);
        } else if (mDirection == DIRECTION_RIGHT) {
            //绘制朝右的选中文字
            drawHorizontalText(canvas, textSelectedColor,
                    (int) (startX + (1 - progress) * textWidth), startX
                            + textWidth);
            //绘制朝右的未选中文字
            drawHorizontalText(canvas, textUnselectColor, startX,
                    (int) (startX + (1 - progress) * textWidth));
        } else if (mDirection == DIRECTION_TOP) {
            //绘制朝上的选中文字
            drawVerticalText(canvas, textSelectedColor, startY,
                    (int) (startY + progress * textHeight));
            //绘制朝上的未选中文字
            drawVerticalText(canvas, textUnselectColor, (int) (startY + progress
                    * textHeight), startY + textHeight);
        } else {
            //绘制朝下的选中文字
            drawVerticalText(canvas, textSelectedColor,
                    (int) (startY + (1 - progress) * textHeight),
                    startY + textHeight);
            //绘制朝下的未选中文字
            drawVerticalText(canvas, textUnselectColor, startY,
                    (int) (startY + (1 - progress) * textHeight));
        }

    }

    private void drawHorizontalText(Canvas canvas, int color, int startX, int endX) {
        paint.setColor(color);
        canvas.save();
        Log.e("tag", "getMeasuredHeight" + getMeasuredHeight());
        canvas.clipRect(startX, 0, endX, getMeasuredHeight());
        canvas.drawText(text, this.startX, baseLineY, paint);
        canvas.restore();
    }

    private void drawVerticalText(Canvas canvas, int color, int startY, int endY) {
        paint.setColor(color);
        canvas.save();
        canvas.clipRect(0, startY, getMeasuredWidth(), endY);// left, top,
        canvas.drawText(text, this.startX,
                this.startY, paint);
        canvas.restore();
    }
}
