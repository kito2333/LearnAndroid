package com.example.helloworld.ui

import android.content.Context
import android.util.AttributeSet
import android.widget.RadioGroup

class CustomRadioGroup : RadioGroup {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        measureChildren(widthMeasureSpec, heightMeasureSpec)

        var maxWidth = 0
        var totalHeight = 0
        var lineWidth = 0
        var maxLineHeight = 0
        var oldHeight: Int
        var oldWidth: Int
        val count = childCount
        for (i in 0 until count) {
            val child = getChildAt(i)
            val lp = child.layoutParams as MarginLayoutParams
            oldHeight = maxLineHeight
            oldWidth = maxWidth
            val deltaX = child.measuredWidth + lp.leftMargin + lp.rightMargin
            if (lineWidth + deltaX + paddingLeft + paddingRight > widthSize) {
                // 换行
                maxWidth = lineWidth.coerceAtLeast(oldWidth)
                lineWidth = deltaX
                totalHeight += oldHeight
                maxLineHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
            } else {
                // 不换行
                lineWidth += deltaX
                val deltaY = child.measuredHeight + lp.topMargin + lp.bottomMargin
                maxLineHeight = maxLineHeight.coerceAtLeast(deltaY)
            }

            if (i == count - 1) {
                totalHeight += maxLineHeight
                maxWidth = lineWidth.coerceAtLeast(oldWidth)
            }
        }

        maxWidth += (paddingLeft + paddingRight)
        totalHeight += (paddingTop + paddingBottom)
        setMeasuredDimension(
            if (widthMode == MeasureSpec.EXACTLY) widthSize else maxWidth,
            if (heightMode == MeasureSpec.EXACTLY) heightSize else totalHeight
        )
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val count = childCount
        var preLeft = paddingLeft
        var preTop = paddingTop
        var maxHeight = 0
        for (i in 0 until count) {
            val child = getChildAt(i)
            val lp = child.layoutParams as MarginLayoutParams
            if (preLeft + lp.leftMargin + child.measuredWidth + lp.rightMargin + paddingRight > (r - l)) {
                // 换行
                preLeft = paddingLeft
                preTop += maxHeight
                maxHeight = child.measuredHeight + lp.topMargin + lp.bottomMargin
            } else {
                maxHeight =
                    maxHeight.coerceAtLeast(child.measuredHeight + lp.topMargin + lp.bottomMargin)
            }

            val left = preLeft + lp.leftMargin
            val top = preTop + lp.topMargin
            val right = left + child.measuredWidth
            val bottom = top + child.measuredHeight
            child.layout(left, top, right, bottom)
            preLeft += lp.leftMargin + child.measuredWidth + lp.rightMargin
        }
    }
}