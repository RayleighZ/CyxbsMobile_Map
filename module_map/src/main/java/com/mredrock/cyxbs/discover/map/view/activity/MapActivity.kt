package com.mredrock.cyxbs.discover.map.view.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.RelativeLayout
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.AnimationBuilder
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.OnImageEventListener
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.database.PlaceDatabase
import com.mredrock.cyxbs.discover.map.view.fragment.DetailFragment
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.map_activity_map.*
import java.lang.Exception
import java.lang.ref.WeakReference

class MapActivity : BaseViewModelActivity<MapViewModel>() {

    override val isFragmentActivity = false
    override val viewModelClass = MapViewModel::class.java

    private lateinit var detailFragment : DetailFragment

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
                /*val place = Place()
                place.placeName = "老图书馆"
                place.buildingX = 3677
                place.buildingY = 7832
                place.buildingR = 175
                place.tagX = 3440
                place.tagY = 7925
                place.tagR = 78*/

                //findViewById<FrameLayout>(R.id.fm_detail).
                //viewModel.setIcon(WeakReference(findViewById(R.id.rl_map_icon_container)) , "操场")
                loadDetailFragment()

                Thread(Runnable {
                    val placeArray = PlaceDatabase.getDataBase(this@MapActivity)
                            .getPlaceDao().queryAllPlaces()
                    PlaceData.placeList.add(placeArray[0])
                    //PlaceData.placeList[0].placeName?.let { LogUtils.d("MapActivity" , it) }
                }).start()
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

    fun loadDetailFragment() {

//        val redRockBottomSheetDialog = RedRockBottomSheetDialog(BaseApp.context)
//        redRockBottomSheetDialog.setContentView(findViewById<FrameLayout>(R.id.fm_detail))

        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        detailFragment = DetailFragment()
        transaction.replace(R.id.fm_detail, detailFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
