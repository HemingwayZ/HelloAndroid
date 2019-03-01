package com.ihemingway.helloworld.widget;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

/**
 * ToggleView
 */
public class MBToggleView extends View {
    private final int DEFAULT_COLOR_PRIMARY = 0xffFFFFFF;//0xFF0C0C0C;
    private final int DEFAULT_COLOR_PRIMARY_DARK = 0xffFFFFFF;//0xFF3AC652

    private final float RATIO_ASPECT = 0.26f;
    private final float ANIMATION_SPEED = 0.03f; // (0,1]
    private static final int STATE_SWITCH_ON = 4; // you change value you die
    private static final int STATE_SWITCH_ON2 = 3;
    private static final int STATE_SWITCH_OFF2 = 2;
    private static final int STATE_SWITCH_OFF = 1;

    private final AccelerateInterpolator interpolator = new AccelerateInterpolator(2);
    private final Paint paint = new Paint();
    private final Path sPath = new Path();
    private final Path bPath = new Path();
    private final RectF bRectF = new RectF();
    private float sAnim, bAnim;
    private RadialGradient shadowGradient;

    private int state = STATE_SWITCH_OFF;
    private int lastState;
    private boolean isCanVisibleDrawing = false;
    /**
     * unused
     */
    private OnClickListener mOnClickListener;
    private int colorPrimary;
    private int colorPrimaryDark;
    private boolean hasShadow = true;
    private boolean isOpened;

    private int mWidth, mHeight;
    private int actuallyDrawingAreaLeft;
    private int actuallyDrawingAreaRight;
    private int actuallyDrawingAreaTop;
    private int actuallyDrawingAreaBottom;

    private float sWidth, sHeight;
    private float sLeft, sTop, sRight, sBottom;
    private float sCenterX, sCenterY;
    private float sScale;

    private float bOffset;
    private float bRadius, bStrokeWidth;
    private float bWidth;
    private float bLeft, bTop, bRight, bBottom, bHeight;
    private float bOnLeftX, bOn2LeftX, bOff2LeftX, bOffLeftX;

    private float shadowReservedHeight;

    private OnToggleChangedListener toggleListener;
    private Rect mTextRect =new Rect();
    String leftText = "OFF";
    String rightText = "ON";

    public MBToggleView(Context context) {
        this(context, null);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MBToggleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        lastState = state;

        if (colorPrimary == DEFAULT_COLOR_PRIMARY && colorPrimaryDark == DEFAULT_COLOR_PRIMARY_DARK) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    TypedValue primaryColorTypedValue = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.colorPrimary, primaryColorTypedValue, true);
                    if (primaryColorTypedValue.data > 0) colorPrimary = primaryColorTypedValue.data;
                    TypedValue primaryColorDarkTypedValue = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.colorPrimaryDark, primaryColorDarkTypedValue, true);
                    if (primaryColorDarkTypedValue.data > 0)
                        colorPrimaryDark = primaryColorDarkTypedValue.data;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setColor(DEFAULT_COLOR_PRIMARY);
    }

    public void setColor(int newColorPrimary) {
        setColor(newColorPrimary, DEFAULT_COLOR_PRIMARY_DARK);
    }

    public void setColor(int newColorPrimary, int newColorPrimaryDark) {
        colorPrimary = newColorPrimary;
        colorPrimaryDark = newColorPrimaryDark;
        invalidate();
    }

    public void setShadow(boolean shadow) {
        hasShadow = shadow;
        invalidate();
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean isOpened) {
        int wishState = isOpened ? STATE_SWITCH_ON : STATE_SWITCH_OFF;
        if (wishState == state) {
            return;
        }
        refreshState(wishState);
    }

    public void toggleSwitch(boolean isOpened) {
        int wishState = isOpened ? STATE_SWITCH_ON : STATE_SWITCH_OFF;

        if (wishState == state) {
            return;
        }
        if ((wishState == STATE_SWITCH_ON && (state == STATE_SWITCH_OFF || state == STATE_SWITCH_OFF2))
                || (wishState == STATE_SWITCH_OFF && (state == STATE_SWITCH_ON || state == STATE_SWITCH_ON2))) {
            sAnim = 1;
        }
        bAnim = 1;
        refreshState(wishState);
    }

    private void refreshState(int newState) {
        if (!isOpened && newState == STATE_SWITCH_ON) {
            isOpened = true;
        } else if (isOpened && newState == STATE_SWITCH_OFF) {
            isOpened = false;
        }
        lastState = state;
        state = newState;
        postInvalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int resultWidth;
        if (widthMode == MeasureSpec.EXACTLY) {
            resultWidth = widthSize;
        } else {
            resultWidth = (int) (86 * getResources().getDisplayMetrics().density + 0.5f)
                    + getPaddingLeft() + getPaddingRight();
            if (widthMode == MeasureSpec.AT_MOST) {
                resultWidth = Math.min(resultWidth, widthSize);
            }
        }
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int resultHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            resultHeight = heightSize;
        } else {
            int selfExpectedResultHeight = (int) (resultWidth * RATIO_ASPECT) + getPaddingTop() + getPaddingBottom();
            resultHeight = selfExpectedResultHeight;
            if (heightMode == MeasureSpec.AT_MOST) {
                resultHeight = Math.min(resultHeight, heightSize);
            }
        }
        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        isCanVisibleDrawing = mWidth > getPaddingLeft() + getPaddingRight() && mHeight > getPaddingTop() + getPaddingBottom();

        if (isCanVisibleDrawing) {
            int actuallyDrawingAreaWidth = mWidth - getPaddingLeft() - getPaddingRight();
            int actuallyDrawingAreaHeight = mHeight - getPaddingTop() - getPaddingBottom();

            if (actuallyDrawingAreaWidth * RATIO_ASPECT < actuallyDrawingAreaHeight) {
                actuallyDrawingAreaLeft = getPaddingLeft();
                actuallyDrawingAreaRight = mWidth - getPaddingRight();
                int heightExtraSize = (int) (actuallyDrawingAreaHeight - actuallyDrawingAreaWidth * RATIO_ASPECT);
                actuallyDrawingAreaTop = getPaddingTop() + heightExtraSize / 2;
                actuallyDrawingAreaBottom = getHeight() - getPaddingBottom() - heightExtraSize / 2;
            } else {
                int widthExtraSize = (int) (actuallyDrawingAreaWidth - actuallyDrawingAreaHeight / RATIO_ASPECT);
                actuallyDrawingAreaLeft = getPaddingLeft() + widthExtraSize / 2;
                actuallyDrawingAreaRight = getWidth() - getPaddingRight() - widthExtraSize / 2;
                actuallyDrawingAreaTop = getPaddingTop();
                actuallyDrawingAreaBottom = getHeight() - getPaddingBottom();
            }

            shadowReservedHeight = (int) ((actuallyDrawingAreaBottom - actuallyDrawingAreaTop) * 0.09f);
            sLeft = actuallyDrawingAreaLeft;
            sTop = actuallyDrawingAreaTop + shadowReservedHeight;
            sRight = actuallyDrawingAreaRight;
            sBottom = actuallyDrawingAreaBottom - shadowReservedHeight;

            sWidth = sRight - sLeft;
            sHeight = sBottom - sTop;
            sCenterX = (sRight + sLeft) / 2;
            sCenterY = (sBottom + sTop) / 2;

            bLeft = sLeft;
            bTop = sTop;
            bBottom = sBottom;
            bHeight = sBottom - sTop;
            bWidth = sRight / 2;
            bRight = sLeft + bWidth;
//            bRight = bLeft + bBottom-bTop;
            final float halfHeightOfS = bWidth / 2; // OfB
//            final float halfHeightOfS = (sBottom - sTop); // OfB
            bRadius = halfHeightOfS * 1f;//控制里面button半径
            bOffset = bRadius * 0.2f; // offset of switching
            bStrokeWidth = (halfHeightOfS - bRadius) * 2;
            bOnLeftX = sRight - bWidth;
            bOn2LeftX = bOnLeftX - bOffset;
            bOffLeftX = sLeft;
            bOff2LeftX = bOffLeftX + bOffset;
            sScale = 1 - bStrokeWidth / sHeight;

            sPath.reset();
            RectF sRectF = new RectF();
            sRectF.top = sTop;
            sRectF.bottom = sBottom;
            sRectF.left = sLeft;
            sRectF.right = sLeft + sHeight;
            sPath.arcTo(sRectF, 90, 180);
            sRectF.left = sRight - sHeight;
            sRectF.right = sRight;
            sPath.arcTo(sRectF, 270, 180);
            sPath.close();

            bRectF.left = bLeft;
            bRectF.right = bRight;
            bRectF.top = bTop + bStrokeWidth / 2;
            bRectF.bottom = bBottom - bStrokeWidth / 2;
            float bCenterX = (bRight + bLeft) / 2;
            float bCenterY = (bBottom + bTop) / 2;

            shadowGradient = new RadialGradient(bCenterX, bCenterY, bRadius, 0xff000000, 0x00000000, Shader.TileMode.CLAMP);
        }
    }

    private void calcBPath(float percent) {
        bPath.reset();
        bRectF.top = bTop;
        bRectF.bottom = bBottom;
        bRectF.left = bLeft + bStrokeWidth / 2;
        bRectF.right = bLeft + bHeight;
        bPath.arcTo(bRectF, 90, 180);
        bRectF.left = bRight - bHeight + percent * bOffset + bStrokeWidth / 2;
        bRectF.right = bRight + percent * bOffset - bStrokeWidth / 2;
        bPath.arcTo(bRectF, 270, 180);
        bPath.close();
    }

    private float calcBTranslate(float percent) {
        float result = 0;
        switch (state - lastState) {
            case 1:
                if (state == STATE_SWITCH_OFF2) {
                    result = bOffLeftX; // off -> off2
                } else if (state == STATE_SWITCH_ON) {
                    result = bOnLeftX - (bOnLeftX - bOn2LeftX) * percent; // on2 -> on
                }
                break;
            case 2:
                if (state == STATE_SWITCH_ON) {
                    result = bOnLeftX - (bOnLeftX - bOffLeftX) * percent; // off2 -> on
                } else if (state == STATE_SWITCH_ON) {
                    result = bOn2LeftX - (bOn2LeftX - bOffLeftX) * percent;  // off -> on2
                }
                break;
            case 3:
                result = bOnLeftX - (bOnLeftX - bOffLeftX) * percent; // off -> on
                break;
            case -1:
                if (state == STATE_SWITCH_ON2) {
                    result = bOn2LeftX + (bOnLeftX - bOn2LeftX) * percent; // on -> on2
                } else if (state == STATE_SWITCH_OFF) {
                    result = bOffLeftX;  // off2 -> off
                }
                break;
            case -2:
                if (state == STATE_SWITCH_OFF) {
                    result = bOffLeftX + (bOn2LeftX - bOffLeftX) * percent;  // on2 -> off
                } else if (state == STATE_SWITCH_OFF2) {
                    result = bOff2LeftX + (bOnLeftX - bOff2LeftX) * percent;  // on -> off2
                }
                break;
            case -3:
                result = bOffLeftX + (bOnLeftX - bOffLeftX) * percent;  // on -> off
                break;
            default: // init
            case 0:
                if (state == STATE_SWITCH_OFF) {
                    result = bOffLeftX; //  off -> off
                } else if (state == STATE_SWITCH_ON) {
                    result = bOnLeftX; // on -> on
                }
                break;
        }
        return result - bOffLeftX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isCanVisibleDrawing) return;

        paint.setAntiAlias(true);
        final boolean isOn = (state == STATE_SWITCH_ON || state == STATE_SWITCH_ON2);
        // Draw background
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(isOn ? colorPrimary : 0xffFFFFFF);
        canvas.drawPath(sPath, paint);

        sAnim = sAnim - ANIMATION_SPEED > 0 ? sAnim - ANIMATION_SPEED : 0;
        bAnim = bAnim - ANIMATION_SPEED > 0 ? bAnim - ANIMATION_SPEED : 0;

        final float dsAnim = interpolator.getInterpolation(sAnim);
        final float dbAnim = interpolator.getInterpolation(bAnim);
        // Draw background animation
        final float scale = 1;//sScale * (isOn ? dsAnim : 1 - dsAnim);
        final float scaleOffset = (sRight - sCenterX - bRadius) * (isOn ? 1 - dsAnim : dsAnim);
        canvas.save();
        paint.setColor(0xFFFFFFFF);
        canvas.drawPath(sPath, paint);
        canvas.restore();
        // To prepare center bar path
        canvas.save();
        canvas.translate(calcBTranslate(dbAnim), shadowReservedHeight);
        final boolean isState2 = (state == STATE_SWITCH_ON2 || state == STATE_SWITCH_OFF2);
        calcBPath(isState2 ? 1 - dbAnim : dbAnim);
        // Use center bar path to draw shadow
        canvas.translate(0, -shadowReservedHeight);
        // draw bar
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xffFDE633);
        canvas.drawPath(bPath, paint);
        canvas.restore();
        //draw text
        canvas.save();
        paint.setStyle(Paint.Style.FILL);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(0xff303030);
        paint.setFakeBoldText(!isOn);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,isOn?10:10.5f,getContext().getResources().getDisplayMetrics()));


        paint.getTextBounds(leftText,0,leftText.length(),mTextRect);
        float distance = (paint.getFontMetrics().bottom-paint.getFontMetrics().top)/2-paint.getFontMetrics().bottom;
        canvas.drawText(leftText,mWidth/4.0f-mTextRect.width()/2.0f,mHeight/2.0f+distance,paint);
        paint.setFakeBoldText(isOn);
        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,!isOn?10:10.5f,getContext().getResources().getDisplayMetrics()));
        paint.getTextBounds(rightText,0,rightText.length(),mTextRect);
        distance = (paint.getFontMetrics().bottom-paint.getFontMetrics().top)/2-paint.getFontMetrics().bottom;
        canvas.drawText(rightText,mWidth/4.0f*3-mTextRect.width()/2.0f,mHeight/2.0f+distance,paint);
        canvas.restore();

        paint.reset();


        if (sAnim > 0 || bAnim > 0) invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if ((state == STATE_SWITCH_ON || state == STATE_SWITCH_OFF)
                && (sAnim * bAnim == 0)) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return true;
                case MotionEvent.ACTION_UP:

                    if (mOnClickListener != null) {
                        mOnClickListener.onClick(this);
                        return true;
                    }


                    lastState = state;

                    bAnim = 1;

                    switch (state) {
                        case STATE_SWITCH_OFF:
                            refreshState(STATE_SWITCH_ON);
                            listener.toggleToOn(MBToggleView.this);
                            if (toggleListener != null) {
                                toggleListener.toggle(MBToggleView.this, true);
                            }
                            break;
                        case STATE_SWITCH_ON:
                            refreshState(STATE_SWITCH_OFF);
                            listener.toggleToOff(MBToggleView.this);
                            if (toggleListener != null) {
                                toggleListener.toggle(MBToggleView.this, false);
                            }
                            break;
                    }

                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    public interface OnStateChangedListener {
        void toggleToOn(MBToggleView view);

        void toggleToOff(MBToggleView view);
    }

    public interface OnToggleChangedListener {
        void toggle(MBToggleView view, boolean isOpened);
    }

    private OnStateChangedListener listener = new OnStateChangedListener() {
        @Override
        public void toggleToOn(MBToggleView view) {
            toggleSwitch(true);
        }

        @Override
        public void toggleToOff(MBToggleView view) {
            toggleSwitch(false);
        }
    };

    public void setOnStateChangedListener(OnStateChangedListener listener) {
        if (listener == null) throw new IllegalArgumentException("empty listener");
        this.listener = listener;
    }

    public void setOnToggleChangedListener(OnToggleChangedListener toggleListener) {
        if (toggleListener == null) throw new IllegalArgumentException("empty toggleListener");
        this.toggleListener = toggleListener;
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.isOpened = isOpened;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        this.isOpened = ss.isOpened;
        this.state = this.isOpened ? STATE_SWITCH_ON : STATE_SWITCH_OFF;
        invalidate();
    }

    @SuppressLint("ParcelCreator")
    static final class SavedState extends BaseSavedState {
        private boolean isOpened;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            isOpened = 1 == in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(isOpened ? 1 : 0);
        }
    }

}
