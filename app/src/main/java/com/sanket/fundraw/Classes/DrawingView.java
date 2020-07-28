package com.sanket.fundraw.Classes;

import android.graphics.Color;
import android.view.View;
import android.content.Context;
import android.util.AttributeSet;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.TypedValue;

import com.sanket.fundraw.R;

import java.util.ArrayList;
import java.util.HashMap;

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private int initialColor = 0xFF660000;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;
    private boolean erase=false;
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Path> undonePaths = new ArrayList<Path>();

    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    private void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();

        drawPaint.setColor(initialColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;

        drawPaint.setStrokeWidth(brushSize);

        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    public void setErase(boolean isErase){
        erase=isErase;
        if(erase)
            drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else
            drawPaint.setXfermode(null);
    }

    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldwidth, int oldheight) {
        super.onSizeChanged(w, h, oldwidth, oldheight);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);

        for (Path p : paths) {
            canvas.drawPath(p, drawPaint);
        }
        canvas.drawPath(drawPath,drawPaint);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;

            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;

            case MotionEvent.ACTION_UP:
                drawCanvas.drawPath(drawPath, drawPaint);
                paths.add(drawPath);
                drawPath.reset();
                break;

            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void setColor(String newColor){
        invalidate();
        initialColor = Color.parseColor(newColor);
        drawPaint.setColor(initialColor);
    }


}