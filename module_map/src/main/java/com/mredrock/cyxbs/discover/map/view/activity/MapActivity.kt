package com.mredrock.cyxbs.discover.map.view.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.AnimationBuilder
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.OnImageEventListener
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import kotlinx.android.synthetic.main.map_activity_map.*
import java.lang.Exception

class MapActivity : BaseActivity() {
    override val isFragmentActivity = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.map_activity_map)

        iv_map.setImage(ImageSource.resource(R.drawable.map_ic_map))

        iv_map.setOnImageEventListener(object : OnImageEventListener {
            override fun onImageLoaded() {
            }

            override fun onReady() {
                toGate(338f, 7970f)     //随机地点测试数据
                toGate(6570f, 8710f)    //随机地点测试数据
                toGate(1558f, 8714f)    //大门测试数据

                //地点测试数据
                val place = Place()
                place.placeName = "老图书馆"
                place.buildingX = 3677
                place.buildingY = 7832
                place.buildingR = 175
                place.tagX = 3440
                place.tagY = 7925
                place.tagR = 78

                PlaceData.placeList.add(place)
            }

            override fun onTileLoadError(e: Exception?) {
            }

            override fun onPreviewReleased() {
            }

            override fun onImageLoadError(e: Exception?) {
            }

            override fun onPreviewLoadError(e: Exception?) {
            }

        })
    }

    fun toGate(x: Float, y: Float) {
        if (iv_map.isReady) {
//            val maxScale: Float = iv_map.maxScale
//            val minScale: Float = iv_map.minScale
            val scale: Float = 1f
            val center = PointF(x, y)
            val density = resources.displayMetrics.densityDpi.toFloat()
            var pin = BitmapFactory.decodeResource(this.resources, R.drawable.map_ic_pin)
            val w = density / 420f * pin.width
            val h = density / 420f * pin.height
            pin = Bitmap.createScaledBitmap(pin, w.toInt(), h.toInt(), true)
            iv_map.addPin(pin, center)
            val animationBuilder: AnimationBuilder? = iv_map.animateScaleAndCenter(scale, center)
            animationBuilder?.withDuration(1000)?.withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)?.withInterruptible(false)?.start()

        }
    }
}
