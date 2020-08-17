package com.mredrock.cyxbs.discover.map.view.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.view.activity.MapActivity

class PinView @JvmOverloads constructor(context: Context?, attr: AttributeSet? = null) : SubsamplingScaleImageView(context, attr) {
    private val gestureDetector = GestureDetector(MySimpleOnGestureListener())
    private val paint = Paint()
    private val vPin = PointF()
    private var pinVisibilityList: MutableList<Boolean> = ArrayList()
    private var pinPositionList: MutableList<PointF> = ArrayList()
    private var sPinList: MutableList<PointF> = ArrayList()
    private var sPinSize = PointF()
    var isLocked = false
    var lastPlace: Place? = null

    private var pinBitmapList: MutableList<Bitmap> = ArrayList()

    fun removeAllPin() {
        pinBitmapList.clear()
        pinVisibilityList.clear()
        pinPositionList.clear()
        sPinList.clear()
        invalidate()
    }

    fun addPin(pin: Bitmap, sPin: PointF) {
        sPinList.add(sPin)
        pinBitmapList.add(pin)
        pinPositionList.add(PointF(0f, 0f))
        pinVisibilityList.add(true)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady) {
            return
        }
        paint.isAntiAlias = true
        for (i: Int in pinBitmapList.indices) {
            sourceToViewCoord(sPinList[i], vPin)
//            viewToSourceCoord(pinBitmapList[i].width.toFloat(), pinBitmapList[i].height.toFloat(), sPinSize)
            sPinSize.set(pinBitmapList[i].width.toFloat(), pinBitmapList[i].height.toFloat())
            val vX = vPin.x - pinBitmapList[i].width / 2
            val vY = vPin.y - pinBitmapList[i].height
            pinPositionList[i].set(sPinList[i].x - sPinSize.x / 2, sPinList[i].y - sPinSize.y)
            if (pinVisibilityList[i]) {
                canvas.drawBitmap(pinBitmapList[i], vX, vY, paint)
            }
        }

    }

    init {
        setOnTouchListener { view, motionEvent -> gestureDetector.onTouchEvent(motionEvent) }
    }

    inner class MySimpleOnGestureListener() : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (isLocked) {
                CyxbsToast.makeText(BaseApp.context, "请取消锁定后对地图进行操作", Toast.LENGTH_SHORT).show()
                return true
            }
            if (this@PinView.isReady && !isLocked) {
                val sCoord: PointF? = this@PinView.viewToSourceCoord(e.x, e.y)
                val x: Float = (sCoord?.x ?: 0f)
                val y: Float = (sCoord?.y ?: 0f)
                for (i: Int in pinBitmapList.indices) {
                    if (pinVisibilityList[i] && sCoord?.x ?: 0f >= pinPositionList[i].x
                            && sCoord?.x ?: 0f <= pinPositionList[i].x + sPinSize.x
                            && sCoord?.y ?: 0f >= pinPositionList[i].y
                            && sCoord?.y ?: 0f <= pinPositionList[i].y + sPinSize.y) {
                        pinVisibilityList[i] = false
                        invalidate()
                        return true
                    }
                }
                var isFind = false
                lastPlace?.let {
                    if (x >= it.tagLeft && x <= it.tagRight &&
                            y >= it.tagTop && y <= it.tagBottom) {
                        return true
                    }

                    it.buildingRectList?.indices?.let { it1 ->
                        for (i: Int in it1) {
                            it.buildingRectList?.get(i)?.run {
                                if (x in buildingLeft..buildingRight &&
                                        y >= buildingTop && y <= buildingBottom) {
                                    return true
                                }
                            }
                        }
                    }
                }

                out@ for (i: Int in PlaceData.placeList.indices) {
                    val place = PlaceData.placeList[i]
                    if (x >= place.tagLeft && x <= place.tagRight &&
                            y >= place.tagTop && y <= place.tagBottom) {
                        context?.run {
                            if (this is MapActivity) {
                                lastPlace = place
                                this.removeAllPin()
                                this.pinAndZoomIn(PlaceData.placeList[i].placeCenterX,
                                        PlaceData.placeList[i].placeCenterY,
                                        PlaceData.placeList[i].placeId)
                            }
                        }
                        break@out
                    }
                    PlaceData.placeList[i].buildingRectList?.run {
                        for (j: Int in indices) {
                            val buildingRect = place.buildingRectList?.get(j)
                            buildingRect?.let {
                                if (x >= it.buildingLeft && x <= it.buildingRight &&
                                        y >= it.buildingTop && y <= it.buildingBottom) {
                                    context?.run {
                                        if (this is MapActivity) {
                                            lastPlace = place
                                            this.removeAllPin()
                                            this.pinAndZoomIn(PlaceData.placeList[i].placeCenterX,
                                                    PlaceData.placeList[i].placeCenterY,
                                                    PlaceData.placeList[i].placeId)
                                        }
                                    }
                                    isFind = true
                                }
                            }
                            if (isFind) break
                        }
                    }
                    if (isFind) break
                }
            }
            return true
        }

        //        override fun onLongPress(e: MotionEvent) {
//            if (imageView.isReady()) {
//                val sCoord: PointF = imageView.viewToSourceCoord(e.x, e.y)
//                Toast.makeText(getApplicationContext(), "Long press: " + sCoord.x.toInt() + ", " + sCoord.y.toInt(), Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(getApplicationContext(), "Long press: Image not ready", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        override fun onDoubleTap(e: MotionEvent): Boolean {
//            return true
//        }
    }
}