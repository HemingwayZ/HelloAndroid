package com.hemingway.editpic;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;

import java.io.Serializable;

/**
 * Created by felix on 2017/11/22 下午6:13.
 */

public class IMGPath implements Serializable {

    protected Path path;

    private int color = Color.RED;

    private float scale = 1;

    private float width = BASE_MOSAIC_WIDTH;

    private float penSize = BASE_DOODLE_WIDTH;

    private IMGMode mode = IMGMode.DOODLE;

    public static final float BASE_DOODLE_WIDTH = 20f;

    public static final float BASE_MOSAIC_WIDTH = 72f;

    public IMGPath() {
        this(new Path());
    }

    public IMGPath(Path path) {
        this(path, IMGMode.DOODLE);
    }

    public IMGPath(Path path, IMGMode mode) {
        this(path, mode, Color.RED);
    }

    public IMGPath(Path path, IMGMode mode, int color) {
        this(path, mode, color, BASE_MOSAIC_WIDTH);
    }

    public IMGPath(Path path, IMGMode mode, int color, float width) {
        this.path = path;
        this.mode = mode;
        this.color = color;
        this.width = width;
        if (mode == IMGMode.MOSAIC) {
            path.setFillType(Path.FillType.EVEN_ODD);
        }
    }
    public IMGPath(Path path, IMGMode mode, int color, float width, float penSize) {
        this.path = path;
        this.mode = mode;
        this.color = color;
        this.width = width;
        this.penSize = penSize;
        if (mode == IMGMode.MOSAIC) {
            path.setFillType(Path.FillType.EVEN_ODD);
        }
    }

    public IMGPath(Path path, IMGMode mode, int color, float width, float penSize, float scale) {
        this.path = path;
        this.mode = mode;
        this.color = color;
        this.width = width;
        this.penSize = penSize;
        this.scale = scale;
        if (mode == IMGMode.MOSAIC) {
            path.setFillType(Path.FillType.EVEN_ODD);
        }
    }
    public Path getPath() {
        return path;
    }

    public float getScale() {
        return scale<1?1:scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public IMGMode getMode() {
        return mode;
    }

    public void setPenSize(float penSize) {
        this.penSize = penSize;
    }

    public void setMode(IMGMode mode) {
        this.mode = mode;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getWidth() {
        return width;
    }

    public float getPenSize() {
        return penSize;
    }

    public void onDrawDoodle(Canvas canvas, Paint paint) {
        if (mode == IMGMode.DOODLE) {
            paint.setColor(color);
            paint.setStrokeWidth(penSize);
            // rewind
            canvas.drawPath(path, paint);
        }
    }

    public void onDrawMosaic(Canvas canvas, Paint paint) {
        if (mode == IMGMode.MOSAIC) {
            paint.setStrokeWidth(width);
            canvas.drawPath(path, paint);
        }
    }

    public void transform(Matrix matrix) {
        path.transform(matrix);
    }
}
