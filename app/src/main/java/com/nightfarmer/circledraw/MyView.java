package com.nightfarmer.circledraw;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by zhangfan on 16-8-3.
 */
public class MyView extends View {

    private Paint paint;
    private Bitmap img;
    private int centerX;
    private int centerY;
    private ValueAnimator valueAnimator;

    private static final int MaxSpeed = 25;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    int angle = 0;
    float animSpeed = 50;

    private void init(Context context) {
        paint = new Paint();
        this.img = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        valueAnimator = ValueAnimator.ofFloat(MaxSpeed, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animSpeed = (Float) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float x = 0;
        float y = 0;

        angle += animSpeed;
        angle = angle % 360;

        for (int i = 0; i < 5; i++) {
            int i1 = angle + 360 / 5 * i;
            y = (float) (Math.sin(i1 * Math.PI / 180) * 200);
            x = (float) (Math.cos(i1 * Math.PI / 180) * 200);

            x += centerX;
            y += centerY;
            canvas.drawBitmap(img, x - img.getWidth() / 2, y - img.getHeight() / 2, paint);
        }
    }


    float touchAnglePre = 0;
    float touchSpeed = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                valueAnimator.cancel();
                animSpeed = 0;
                touchSpeed = 0;
                float x = event.getX() - centerX;
                float y = event.getY() - centerY;
                touchAnglePre = (float) (Math.atan(x / y) / 2 / Math.PI * 360);
                break;
            case MotionEvent.ACTION_MOVE:
                float x1 = event.getX() - centerX;
                float y1 = event.getY() - centerY;
                float touchAngle = (float) (Math.atan(x1 / y1) / 2 / Math.PI * 360);
                if (touchAnglePre / Math.abs(touchAnglePre) == touchAngle / Math.abs(touchAngle)) {
                    touchSpeed = (touchAnglePre - touchAngle) * 2;
                }
                angle += touchSpeed;
                touchAnglePre = touchAngle;
                invalidate();
                break;

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                valueAnimator.setFloatValues(Math.abs(touchSpeed) > MaxSpeed ? MaxSpeed * (touchSpeed / Math.abs(touchSpeed)) : touchSpeed, 0);
                valueAnimator.start();
                break;
            default:

        }
        return true;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
    }
}
