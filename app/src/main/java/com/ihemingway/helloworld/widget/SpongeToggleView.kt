package com.ihemingway.helloworld.widget

import android.content.Context
import android.graphics.*
import android.os.Parcelable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

/**
 * Created by Hemingway on 2019/2/26
 * Desc:((One)TWO)
 */
class SpongeToggleView : View {
    private var mPaint: Paint = Paint()
    private var mRectF: RectF = RectF(0.0f, 0.0f, dip2Px(86.0f), dip2Px(20.0f))
    //    private var mInnerRectF:RectF = RectF(0.0f,0.0f,)
    private var mTextRect: Rect = Rect()
    private var mTextPaint: TextPaint = TextPaint()
    private var textLeft: String = "单张"
    private var textRight: String = "多张"

    constructor(ctx: Context) : super(ctx) {
        init()
    }

    override fun onSaveInstanceState(): Parcelable {
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        invalidate()
    }
    private fun init() {
        mPaint.color = Color.WHITE
        mPaint.isAntiAlias = true
        mTextPaint.color = Color.parseColor("#303030")
        val fontSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10.0f, context.resources.displayMetrics)
        mTextPaint.textSize = fontSize
        mTextPaint.isAntiAlias = true
        mTextPaint.typeface = Typeface.SANS_SERIF
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.translate(0.0f, 0.0f)
        mPaint.color = Color.WHITE
        mRectF.set(0.0f, 0.0f, dip2Px(86.0f), dip2Px(20.0f))
        canvas?.drawRoundRect(mRectF, mRectF.height() / 2, mRectF.height() / 2, mPaint)
        mPaint.color = Color.parseColor("#ffFDE633")

        //inner rect
        var isTextBold: Boolean = true
        var moveX: Float = 0.0f
//        if (currentX < 0) {
//            moveX = 0.0f
//            isTextBold = true
//        } else
            if (currentX < mRectF.width() / 2) {
            moveX = 0.0f
            isTextBold = true
        } else{
//            moveX = currentX
            moveX = mRectF.width() / 2
            isTextBold = false
        }
//        else {
//            moveX = mRectF.width() / 2
//            isTextBold = false
//        }
        mRectF.set(moveX, 0.0f, mRectF.width() / 2 + moveX, mRectF.height())
        canvas?.drawRoundRect(mRectF, mRectF.height() / 2, mRectF.height() / 2, mPaint)

        mTextPaint.isFakeBoldText = isTextBold
        mTextPaint.getTextBounds(textLeft, 0, textLeft.length, mTextRect)
        //文字上下居中
        val distance = (mTextPaint.fontMetrics.bottom - mTextPaint.fontMetrics.top) / 2 - mTextPaint.fontMetrics.bottom
        //leftText
        //canvas?.drawText(textLeft, mRectF.width() / 2 - mTextRect.width() / 2, mRectF.height() / 2 + mTextRect.height() / 2, mTextPaint)
        canvas?.drawText(textLeft, mRectF.width() / 2 - mTextRect.width() / 2, mRectF.centerY() + distance, mTextPaint)
        //rightText
        mTextPaint.isFakeBoldText = !isTextBold
        canvas?.translate(mRectF.width(), 0.0f)
        mTextPaint.getTextBounds(textRight, 0, textRight.length, mTextRect)
        canvas?.drawText(textRight, mRectF.width() / 2 - mTextRect.width() / 2, mRectF.centerY() + distance, mTextPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(mRectF.width().toInt(), mRectF.height().toInt())
    }

    private fun dip2Px(dpValue: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.resources.displayMetrics)
    }

    var currentX = 0.0f
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                currentX = event.x
            }
            MotionEvent.ACTION_MOVE -> {
                currentX = event.x
            }
            MotionEvent.ACTION_UP -> {
                currentX = event.x
            }
        }
        invalidate()
        return true

    }
}