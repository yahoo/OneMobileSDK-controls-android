package com.aol.mobile.sdk.controls.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageButton;

import com.aol.mobile.sdk.controls.R;
import com.aol.mobile.sdk.controls.Themed;


public class PlayNoResImageButton extends ImageButton implements Themed {
    private Paint paint;
    private int lineWidth = 3;
    private int shadowOffset = 3;
    private int mainColor;
    private int accentColor;
    private int shadowColor = Color.BLACK;
    private int activeColor;

    private final float PCT_TRIANGLE_LEFT_MARGIN = 0.556f;
    private final float PCT_TRIANGLE_TOP_MARGIN = 0.496f;
    private final float PCT_TRIANGLE_WIDTH = 0.216f;
    private final float PCT_TRIANGLE_HEIGHT = 0.384f;

    private int paddedCanvasWidth = 0;
    private int paddedCanvasHeight = 0;

    public PlayNoResImageButton(Context context, AttributeSet attrs) {
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
        drawTriangle(canvas, paint, shadowOffset);

        paint.setStrokeWidth(lineWidth);
        paint.setColor(activeColor);
        canvas.drawCircle(halfWidth, halfHeight, radius, paint);
        drawTriangle(canvas, paint, 0);
    }

    private float getMaxRadius(int lineWidth, int shadowOffset) {
        return Math.min(paddedCanvasWidth / 2f, paddedCanvasHeight / 2f) - lineWidth - shadowOffset;
    }

    private void drawTriangle(Canvas canvas, Paint paint, int shadowOffset) {
        int canvasWidth = this.paddedCanvasWidth;
        int canvasHeight = this.paddedCanvasHeight;

        float triangleWidth = canvasWidth * PCT_TRIANGLE_WIDTH;
        float triangleHeight = canvasHeight * PCT_TRIANGLE_HEIGHT;

        Point topLeft = new Point(
                shadowOffset + (int) ((canvasWidth - triangleWidth) * PCT_TRIANGLE_LEFT_MARGIN) + getPaddingLeft(),
                shadowOffset + (int) ((canvasHeight - triangleHeight) * PCT_TRIANGLE_TOP_MARGIN) + getPaddingTop());
        Point right = new Point(
                (int) (topLeft.x + triangleWidth),
                shadowOffset + (int) (canvasHeight * PCT_TRIANGLE_TOP_MARGIN) + getPaddingTop());
        Point botLeft = new Point(
                topLeft.x,
                (int) (topLeft.y + triangleHeight));

        Path trianglePath = new Path();
        trianglePath.moveTo(topLeft.x, topLeft.y);
        trianglePath.lineTo(right.x, right.y);
        trianglePath.lineTo(botLeft.x, botLeft.y);
        trianglePath.lineTo(topLeft.x, topLeft.y);
        trianglePath.close();

        canvas.drawPath(trianglePath, paint);
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
