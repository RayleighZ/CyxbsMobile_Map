package com.mredrock.cyxbs.discover.map.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.AnimationBuilder
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.OnImageEventListener
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.ClassifyPlace
import com.mredrock.cyxbs.discover.map.bean.FavoritePlace
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.view.adapter.ClassifyAdapter
import com.mredrock.cyxbs.discover.map.view.adapter.FavoriteAdapter
import com.mredrock.cyxbs.discover.map.view.fragment.SearchFragment
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_pop_window_no_favorite.view.*
import java.util.*
import kotlin.collections.ArrayList


class MapActivity : BaseActivity() {
    private lateinit var popWindow: PopupWindow
    private val classifyItemList: MutableList<ClassifyPlace> = ArrayList()
    private val favoriteItemList: MutableList<FavoritePlace> = ArrayList()
    private val classifyAdapter: ClassifyAdapter = ClassifyAdapter(this, classifyItemList)
    private val favoriteAdapter: FavoriteAdapter = FavoriteAdapter(this, favoriteItemList)
    private var searchFragmentIsShowing: Boolean = false        //判断搜索fragment是否显示

    override val isFragmentActivity = false

    fun changeSearchFragmentIsShowing() {
        searchFragmentIsShowing = !searchFragmentIsShowing
    }

    fun getClassifyRecyclerView(): RecyclerView {
        return rv_map_classify
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.map_activity_map)

        //初始化状态栏高度的透明View
        val statusBarLinearParams = view_status_bar.layoutParams //取控件当前的布局参数
        statusBarLinearParams.height = getStatusBarHeight() //状态栏高度
        view_status_bar.layoutParams = statusBarLinearParams

        //初始化我的收藏弹出菜单
        val view: View = LayoutInflater.from(this).inflate(R.layout.map_pop_window_no_favorite, null)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        view.rv_map_pop_window.layoutManager = linearLayoutManager
        view.rv_map_pop_window.adapter = favoriteAdapter
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        popWindow = PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true) //-2 是包裹内容，-1 是填充父窗体
        popWindow.setBackgroundDrawable(getDrawable(android.R.color.transparent))//透明背景
        popWindow.isOutsideTouchable = true
        popWindow.isFocusable = true

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_map_classify.layoutManager = layoutManager
        rv_map_classify.adapter = classifyAdapter

        //test data
        val classifyPlace = ClassifyPlace()
        classifyPlace.isHot = false
        classifyPlace.name = "食堂"
        classifyItemList.add(classifyPlace)

        val classifyPlace2 = ClassifyPlace()
        classifyPlace2.isHot = true
        classifyPlace2.name = "入校报到点"
        classifyItemList.add(classifyPlace2)

        val classifyPlace3 = ClassifyPlace()
        classifyPlace3.isHot = false
        classifyPlace3.name = "运动场"
        classifyItemList.add(classifyPlace3)

        val classifyPlace4 = ClassifyPlace()
        classifyPlace4.isHot = false
        classifyPlace4.name = "入校报到点444444444"
        classifyItemList.add(classifyPlace4)

        val classifyPlace5 = ClassifyPlace()
        classifyPlace5.isHot = true
        classifyPlace5.name = "运动场5555555555"
        classifyItemList.add(classifyPlace5)

        classifyAdapter.notifyDataSetChanged()

        //test data
        val favoritePlace = FavoritePlace()
        favoritePlace.name = "食堂"
        favoriteItemList.add(favoritePlace)

        val favoritePlace2 = FavoritePlace()
        favoritePlace2.name = "我的收藏"
        favoriteItemList.add(favoritePlace2)

        val favoritePlace3 = FavoritePlace()
        favoritePlace3.name = "我的收藏1"
        favoriteItemList.add(favoritePlace3)

        val favoritePlace4 = FavoritePlace()
        favoritePlace4.name = "我的收藏2"
        favoriteItemList.add(favoritePlace4)

        favoriteAdapter.notifyDataSetChanged()

        et_map_search.hint = "大家都在搜：风雨操场"

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

        iv_map.setImage(ImageSource.resource(R.drawable.map_ic_map))

        et_map_search.setOnTouchListener { v, event ->
            showKeyBoard(et_map_search)
            if (event?.action == MotionEvent.ACTION_UP && !searchFragmentIsShowing) {
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.ll_map_search_container, SearchFragment())
                        .addToBackStack(null)
                        .commitAllowingStateLoss()
                searchFragmentIsShowing = true
            }
            false
        }

        cl_map_favorite.setOnClickListener {
            cl_map_favorite.measure(0, 0)
            val location = IntArray(2)
            cl_map_favorite.getLocationOnScreen(location)
            cl_map_favorite.measure(0, 0)
            val windowPos: IntArray? = calculatePopWindowPos(cl_map_favorite, view)
            popWindow.showAtLocation(cl_map_favorite, Gravity.TOP or Gravity.START, (windowPos?.get(0)
                    ?: 0) - dp2px(15f), (windowPos?.get(1) ?: 0))
            popWindow.update()
        }

        window.decorView.findViewById<View>(android.R.id.content).setOnTouchListener { _, _ -> // rel.setFocusable(true);
            //收起键盘
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(window.decorView.windowToken, 0)
            false
        }

        et_map_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                iv_map_search_clear.visibility = View.VISIBLE
                s?.isEmpty()?.run {
                    if (this) {
                        iv_map_search_clear.visibility = View.GONE
                    }
                }
            }
        })
        iv_map_search_clear.setOnClickListener {
            et_map_search.setText("")
        }

        iv_map_back.setOnClickListener {
            onBackPressed()

            //收起键盘
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(window.decorView.windowToken, 0)
        }
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

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
        if (et_map_search.isFocusable) {
            window.decorView.findViewById<View>(android.R.id.content).requestFocus()
        }
    }

    fun showKeyBoard(editText: EditText?) {
        if (editText != null) {
            editText.isFocusable = true
            editText.isFocusableInTouchMode = true
            editText.requestFocus()
            val timer = Timer()
            timer.schedule(object : TimerTask() {
                override fun run() {
                    try {
                        val inputManager = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        inputManager.showSoftInput(editText, 0)
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                }
            }, 300)
        }
    }

    /**
     * 计算出来的位置，y方向就在anchorView的上面和下面对齐显示，x方向就是与屏幕右边对齐显示
     * 如果anchorView的位置有变化，就可以适当自己额外加入偏移来修正
     * @param anchorView 呼出window的view
     * @param contentView  window的内容布局
     * @return window显示的左上角的xOff,yOff坐标
     */
    private fun calculatePopWindowPos(anchorView: View, contentView: View): IntArray? {
        val windowPos = IntArray(2)
        val anchorLoc = IntArray(2)
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc)
        val anchorHeight = anchorView.height
        // 获取屏幕的高宽
        val defaultDisplay = windowManager.defaultDisplay
        val point = Point()
        defaultDisplay.getSize(point)
        val screenHeight: Int = point.y
        val screenWidth: Int = point.x
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        // 计算contentView的高宽
        val windowHeight = contentView.measuredHeight
        val windowWidth = contentView.measuredWidth
        // 判断需要向上弹出还是向下弹出显示
        val isNeedShowUp = screenHeight - anchorLoc[1] - anchorHeight < windowHeight
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth
            windowPos[1] = anchorLoc[1] - windowHeight
        } else {
            windowPos[0] = screenWidth - windowWidth
            windowPos[1] = anchorLoc[1] + anchorHeight
        }
        return windowPos
    }
}
