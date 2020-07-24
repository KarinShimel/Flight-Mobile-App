package com.example.newflightmobileapp

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.View.OnTouchListener

class JoyStickView : SurfaceView, SurfaceHolder.Callback, OnTouchListener {
    private var centerX = 0f
    private var centerY = 0f
    private var baseRadius = 0f
    private var hatRadius = 0f
    private var joystickCallback: JoystickListener? = null
    private val rat = 5

    interface JoystickListener {
        fun onJoystickMoved(
            xPercent: Float,
            yPercent: Float,
            id: Int
        )
    }
    //The function set up dimensions
    private fun setUpDimensions() {
        centerX = width / 2.toFloat()
        centerY = height / 2.toFloat()
        baseRadius = Math.min(width, height) / 3.toFloat()
        hatRadius = Math.min(width, height) / 5.toFloat()
    }

    constructor(context: Context?) : super(context) {
        holder.addCallback(this)
        setOnTouchListener(this)
        if (context is JoystickListener) joystickCallback = context
    }

    constructor(
        context: Context?,
        attributeSet: AttributeSet?,
        style: Int
    ) : super(context, attributeSet, style) {
        holder.addCallback(this)
        setOnTouchListener(this)
        if (context is JoystickListener) joystickCallback = context
    }

    constructor(context: Context?, attributes: AttributeSet?) : super(
        context,
        attributes
    ) {
        holder.addCallback(this)
        setOnTouchListener(this)
        if (context is JoystickListener) joystickCallback = context
    }
    // draw joystick
    private fun drawJoystick(newX: Float, newY: Float) {
        if (holder.surface.isValid) {
            val myCanvas = this.holder.lockCanvas()
            val colors = Paint()
            myCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            colors.setARGB(255, 201, 218, 247)
            myCanvas.drawCircle(centerX, centerY, baseRadius, colors)
            colors.setARGB(255, 130, 157, 200)
            myCanvas.drawCircle(newX, newY, hatRadius, colors)
            holder.unlockCanvasAndPost(myCanvas)
        }
    }
    //The function create the surface
    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        setUpDimensions()
        drawJoystick(centerX, centerY)
    }

    override fun surfaceChanged(
        surfaceHolder: SurfaceHolder,
        i: Int,
        i1: Int,
        i2: Int
    ) {
    }

    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {}
    override fun onTouch(view: View, e: MotionEvent): Boolean {
        if (view == this) {
            if (e.action != MotionEvent.ACTION_UP) {
                val displac = Math.sqrt(
                    Math.pow(
                        e.x - centerX.toDouble(),
                        2.0
                    ) + Math.pow(e.y - centerY.toDouble(), 2.0)
                ).toFloat()
                if (displac < baseRadius) {
                    drawJoystick(e.x, e.y)
                    id
                    joystickCallback!!.onJoystickMoved(
                        (e.x - centerX) / baseRadius,
                        (e.y - centerY) / baseRadius, id)
                } else {
                    val ratio = baseRadius / displac
                    val constrainedX = centerX + (e.x - centerX) * ratio
                    val constrainedY = centerY + (e.y - centerY) * ratio
                    drawJoystick(constrainedX, constrainedY)
                    joystickCallback!!.onJoystickMoved(
                        (constrainedX - centerX) / baseRadius,
                        (constrainedY - centerY) / baseRadius, id)
                }
            } else {
                drawJoystick(centerX, centerY)
                joystickCallback!!.onJoystickMoved(0f, 0f, id)
            }
        }
        return true
    }
}