package com.mredrock.cyxbs.discover.map.view.widget

//import com.mredrock.cyxbs.common.component.CyxbsToast
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.NinePatchDrawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View.OnTouchListener
import android.widget.Toast
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.discover.map.config.PlaceData
import kotlin.math.sqrt

class PinView @JvmOverloads constructor(context: Context?, attr: AttributeSet? = null) : SubsamplingScaleImageView(context, attr) {
    private val gestureDetector = GestureDetector(MySimpleOnGestureListener())
    private val paint = Paint()
    private val vPin = PointF()
    private var pinVisibilityList: MutableList<Boolean> = ArrayList()
    private var pinPositionList: MutableList<PointF> = ArrayList()
    private var sPinList: MutableList<PointF> = ArrayList()
    private var sPinSize = PointF()
//    private var pinList: MutableList<ImageView> = ArrayList()
    private var pinBitmapList: MutableList<Bitmap> = ArrayList()
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
            viewToSourceCoord(pinBitmapList[i].width.toFloat(), pinBitmapList[i].height.toFloat(), sPinSize)
            val vX = vPin.x - pinBitmapList[i].width / 2
            val vY = vPin.y - pinBitmapList[i].height
            pinPositionList[i].set(sPinList[i].x - sPinSize.x / 2, sPinList[i].y - sPinSize.x)
            if (pinVisibilityList[i]) {
                canvas.drawBitmap(pinBitmapList[i], vX, vY, paint)
            }
        }

    }

    init {
        setOnTouchListener(OnTouchListener { view, motionEvent -> gestureDetector.onTouchEvent(motionEvent) })
    }

    fun convertDrawableToBitmap(drawable: Drawable?): Bitmap? {
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable is NinePatchDrawable) {
            val bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            if (drawable.getOpacity() !== PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight())
            drawable.draw(canvas)
            bitmap
        } else {
            null
        }
    }

    inner class MySimpleOnGestureListener() : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            if (this@PinView.isReady) {
                val sCoord: PointF? = this@PinView.viewToSourceCoord(e.x, e.y)
                for (i: Int in pinBitmapList.indices) {
                    if (sCoord?.x ?: 0f >= pinPositionList[i].x
                            && sCoord?.x ?: 0f <= pinPositionList[i].x + sPinSize.x
                            && sCoord?.y ?: 0f >= pinPositionList[i].y
                            && sCoord?.y ?: 0f <= pinPositionList[i].y + sPinSize.x) {
                        pinVisibilityList[i] = false
                        invalidate()
                        return true
                    }
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
        override fun onDoubleTap(e: MotionEvent): Boolean {
            if (this@PinView.isReady) {
                val sCoord: PointF? = this@PinView.viewToSourceCoord(e.x, e.y)
                val x: Float = (sCoord?.x ?: 0f)
                val y: Float = (sCoord?.y ?: 0f)
                for (i: Int in PlaceData.placeList.indices) {
                    if ((sqrt((x - PlaceData.placeList[i].buildingX) * (x - PlaceData.placeList[i].buildingX)
                                    + (y - PlaceData.placeList[i].buildingY) * (y - PlaceData.placeList[i].buildingY))
                            <= PlaceData.placeList[i].buildingR) ||
                            (sqrt((x - PlaceData.placeList[i].tagX) * (x - PlaceData.placeList[i].tagX)
                                    + (y - PlaceData.placeList[i].tagY) * (y - PlaceData.placeList[i].tagY))
                                    <= PlaceData.placeList[i].tagR)) {
                        CyxbsToast.makeText(BaseApp.context, PlaceData.placeList[i].placeName.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
            return true
        }
    }
}