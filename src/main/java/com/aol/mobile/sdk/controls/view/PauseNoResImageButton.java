package com.aol.mobile.sdk.controls.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.aol.mobile.sdk.controls.R;
import com.aol.mobile.sdk.controls.Themed;


public class PauseNoResImageButton extends ImageButton implements Themed {
    private Paint paint;
    private int lineWidth = 3;
    private int shadowOffset = 3;
    private int mainColor;
    private int accentColor;
    private int shadowColor = Color.BLACK;
    private int activeColor;

    public static final float PCT_RECTANGLE_WIDTH = 0.054f;
    public static final float PCT_RECTANGLE_HEIGHT = 0.378f;
    public static final float PCT_RECTANGLE_SPACING = 0.089f;

    private int paddedCanvasWidth = 0;
    private int paddedCanvasHeight = 0;

    public PauseNoResImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackground(null);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        mainColor = context.getResources().getColor(R.color.default_main_color);
        accentColor = context.getResources().getColor(R.color.default_accent_color);
        activeColor = mainColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paddedCanvasWidth = canvas.getWidth() - getPaddingLeft() - getPaddingRight();
        paddedCanvasHeight = canvas.getHeight() - getPaddingTop() - getPaddingBottom();

        float halfWidth = canvas.getWidth() / 2;
        float halfHeight = canvas.getHeight() / 2;
        float radius = getMaxRadius(lineWidth, shadowOffset);

        paint.setStrokeWidth(lineWidth);
        paint.setColor(shadowColor);
        canvas.drawCircle(halfWidth + shadowOffset, halfHeight + shadowOffset, radius, paint);
        drawRectangle(canvas, paint, shadowOffset);

        paint.setStrokeWidth(lineWidth);
        paint.setColor(activeColor);
        canvas.drawCircle(halfWidth, halfHeight, radius, paint);
        drawRectangle(canvas, paint, 0);
    }

    private float getMaxRadius(int lineWidth, int shadowOffset) {
        return Math.min(paddedCanvasWidth / 2f, paddedCanvasHeight / 2f) - lineWidth - shadowOffset;
    }

    private void drawRectangle(Canvas canvas, Paint paint, int shadowOffset) {
        int canvasWidth = this.paddedCanvasWidth;
        int canvasHeight = this.paddedCanvasHeight;

        float rectangleWidth = canvasWidth * PCT_RECTANGLE_WIDTH;
        float rectangleHeight = canvasHeight * PCT_RECTANGLE_HEIGHT;
        float rectangleSpacing = canvasWidth * PCT_RECTANGLE_SPACING;

        Point leftRectangleTopLeft = new Point(
                shadowOffset + (int) ((canvasWidth - rectangleWidth * 2f - rectangleSpacing) / 2f) + getPaddingLeft(),
                shadowOffset + (int) ((canvasHeight - rectangleHeight) / 2f) + getPaddingTop());
        Point leftRectangleBotRight = new Point(
                (int) (leftRectangleTopLeft.x + rectangleWidth),
                (int) (leftRectangleTopLeft.y + rectangleHeight));

        Point rightRectangleTopLeft = new Point(
                (int) (leftRectangleTopLeft.x + rectangleWidth + rectangleSpacing),
                leftRectangleTopLeft.y);
        Point rightRectangleBotRight = new Point(
                (int) (rightRectangleTopLeft.x + rectangleWidth),
                (int) (rightRectangleTopLeft.y + rectangleHeight));


        RectF leftRectangle = new RectF(leftRectangleTopLeft.x,
                leftRectangleTopLeft.y,
                leftRectangleBotRight.x,
                leftRectangleBotRight.y);
        RectF rightRectangle = new RectF(rightRectangleTopLeft.x,
                rightRectangleTopLeft.y,
                rightRectangleBotRight.x,
                rightRectangleBotRight.y);

        canvas.drawRect(leftRectangle, paint);
        canvas.drawRect(rightRectangle, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            activeColor = accentColor;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            activeColor = mainColor;
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    @Override
    public void setMainColor(int color) {
        mainColor = color;
    }

    @Override
    public void setAccentColor(int color) {
        accentColor = color;
    }
}
