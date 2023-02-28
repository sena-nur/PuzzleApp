package com.sena.puzzleapp

import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

class TouchListener (private val activity: PuzzleActivity): View.OnTouchListener{


    private var xDelta = 0f
    private var yDelta = 0f
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val x = event!!.rawX
        val y = event!!.rawY
        val tolerance = Math.sqrt(
            Math.pow(v!!.width.toDouble() , 2.0) +
                    Math.pow(v!!.height.toDouble(),2.0)) / 10

        val piece = v as PuzzlePiece
        if(!piece.canMove) {
            return true
        }

        val lParams = v.layoutParams as RelativeLayout.LayoutParams

        when(event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                xDelta = x- lParams.leftMargin
                yDelta = y- lParams.topMargin

                piece.bringToFront()
            }
            MotionEvent.ACTION_MOVE -> {
                lParams.leftMargin = (x- xDelta).toInt()
                lParams.leftMargin = (y- xDelta).toInt()
                v.layoutParams = lParams
            }

            MotionEvent.ACTION_UP -> {
                val xDiff = StrictMath.abs(
                    piece.xCoord - lParams.leftMargin

                )
                val yDiff = StrictMath.abs(
                    piece.yCoord - lParams.leftMargin
                )

                if(xDiff <= tolerance && yDiff <= tolerance) {
                    lParams.leftMargin = piece.xCoord
                    lParams.leftMargin= piece.yCoord
                    piece.layoutParams = lParams
                    piece.canMove = false

                    senViewToBack(piece)
                    activity.checkGameOver()

                }
            }
        }
        return true



    }

    private fun senViewToBack(child:View) {
        val parent = child.parent as ViewGroup

        if(parent != null) {
            parent.removeView(child)
            parent.addView(child,0)

        }
    }

}