package com.mredrock.cyxbs.discover.map.view.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.PointF
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.AnimationBuilder
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView.OnImageEventListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.service.ServiceManager
import com.mredrock.cyxbs.common.service.account.IAccountService
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.BasicMapData
import com.mredrock.cyxbs.discover.map.bean.ClassifyData
import com.mredrock.cyxbs.discover.map.bean.ClassifyData.ClassifyPlace
import com.mredrock.cyxbs.discover.map.bean.FavoritePlace
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.database.PlaceDatabase
import com.mredrock.cyxbs.discover.map.view.adapter.ClassifyAdapter
import com.mredrock.cyxbs.discover.map.view.adapter.FavoriteAdapter
import com.mredrock.cyxbs.discover.map.view.fragment.DetailFragment
import com.mredrock.cyxbs.discover.map.view.fragment.SearchFragment
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_fragment_search.*
import kotlinx.android.synthetic.main.map_pop_window_no_favorite.view.*
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList


class MapActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(MapViewModel::class.java) }
    private lateinit var popWindow: PopupWindow
    private val classifyItemList: MutableList<ClassifyPlace> = ArrayList()
    private val favoriteItemList: MutableList<FavoritePlace> = ArrayList()
    private val classifyAdapter: ClassifyAdapter = ClassifyAdapter(this, classifyItemList)
    private val favoriteAdapter: FavoriteAdapter = FavoriteAdapter(this, favoriteItemList)
    private var searchFragmentIsShowing: Boolean = false        //判断搜索fragment是否显示
    private lateinit var detailFragment: DetailFragment
    var searchFragment: SearchFragment? = null

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

        val userState = ServiceManager.getService(IAccountService::class.java).getVerifyService()
        if (!userState.isLogin()) {
            //这里只是模拟一下登录，如果有并发需求，自己设计
            Thread {
                userState.login(this, "2019213962", "062115")
            }.start()
        }

        //初始化状态栏高度的透明View
        val statusBarLinearParams = view_status_bar.layoutParams //取控件当前的布局参数
        statusBarLinearParams.height = getStatusBarHeight() //状态栏高度
        view_status_bar.layoutParams = statusBarLinearParams

        //初始化我的收藏弹出菜单
        val popWindowView: View = LayoutInflater.from(this).inflate(R.layout.map_pop_window_no_favorite, null)
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        popWindowView.rv_map_pop_window.layoutManager = linearLayoutManager
        popWindowView.rv_map_pop_window.adapter = favoriteAdapter
        popWindowView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        popWindow = PopupWindow(popWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true) //-2 是包裹内容，-1 是填充父窗体
        popWindow.setBackgroundDrawable(getDrawable(android.R.color.transparent))//透明背景
        popWindow.isOutsideTouchable = true
        popWindow.isFocusable = true
        popWindow.animationStyle = R.style.PopWindow_Anim_Style

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_map_classify.layoutManager = layoutManager
        rv_map_classify.adapter = classifyAdapter

        //test data
        val favoritePlace = FavoritePlace()
        favoritePlace.placeNickname = "食堂"
        favoriteItemList.add(favoritePlace)

        val favoritePlace2 = FavoritePlace()
        favoritePlace2.placeNickname = "我的收藏"
        favoriteItemList.add(favoritePlace2)

        val favoritePlace3 = FavoritePlace()
        favoritePlace3.placeNickname = "我的收藏1"
        favoriteItemList.add(favoritePlace3)

        val favoritePlace4 = FavoritePlace()
        favoritePlace4.placeNickname = "我的收藏22222"
        favoriteItemList.add(favoritePlace4)
        favoriteItemList.add(favoritePlace4)
        favoriteItemList.add(favoritePlace4)
        favoriteItemList.add(favoritePlace4)
        favoriteItemList.add(favoritePlace4)
        favoriteItemList.add(favoritePlace4)
        favoriteItemList.add(favoritePlace4)
        favoriteItemList.clear()

        iv_map.setImage(ImageSource.resource(R.drawable.map_ic_map))

        et_map_search.setOnTouchListener { v, event ->
            showKeyBoard(et_map_search)
            if (event?.action == MotionEvent.ACTION_UP && !searchFragmentIsShowing) {
                if (searchFragment == null) {
                    searchFragment = SearchFragment()
                }
                searchFragment?.let {
                    val transaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.ll_map_search_container, it)
                            .addToBackStack("searchFragment")
                            .commitAllowingStateLoss()
                    searchFragmentIsShowing = true
                }
            }

            false
        }

        cl_map_favorite.setOnClickListener {
            if (favoriteItemList.size == 0) {
                popWindowView.rv_map_pop_window.gone()
                popWindowView.tv_map_no_favorite.visible()

                val toast: Toast = CyxbsToast.makeText(BaseApp.context, R.string.map_my_favorite_no_favorite_toast, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, toast.xOffset, toast.yOffset)
                toast.show()
            } else {
                favoriteAdapter.notifyDataSetChanged()
            }

            cl_map_favorite.measure(0, 0)
            val location = IntArray(2)
            cl_map_favorite.getLocationOnScreen(location)
            cl_map_favorite.measure(0, 0)
            val windowPos: IntArray? = calculatePopWindowPos(cl_map_favorite, popWindowView)
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

                        searchFragment?.run {
                            searchResultList.clear()
                            changeAdapter(1)

                            tv_map_search_history?.visible()
                            tv_map_search_delete_all?.visible()
                        }
                    } else {
                        var isFind = false
                        searchFragment?.let {
                            it.changeAdapter(0)
                            it.tv_map_search_history?.gone()
                            it.tv_map_search_delete_all?.gone()

                            it.searchResultList.clear()
                            it.adapter.notifyDataSetChanged()
                            for (i: Int in PlaceData.placeList.indices) {
                                s.run {
                                    if (PlaceData.placeList[i].placeName?.contains(this) == true) {
                                        it.searchResultList.add(0, PlaceData.placeList[i])
                                        it.adapter.notifyItemInserted(0)
                                        isFind = true
                                    }
                                }
                            }
                            if (!isFind) {
                                CyxbsToast.makeText(BaseApp.context, R.string.map_search_no_find, Toast.LENGTH_SHORT).show()
                            }
                        }
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
            hideKeyBoard()
        }

        initObserver()

        viewModel.getBasicMapData()
        viewModel.getClassify()
    }

    private fun initObserver() {
        viewModel.mClassify.observe(this, Observer<ClassifyData> {
            it.buttonInfo?.run {
                classifyItemList.clear()
                classifyItemList.addAll(this)
                classifyAdapter.notifyDataSetChanged()
            }
        })

        viewModel.mHot.observe(this, Observer<String> {
            it.run {
                if (this != null && this != "") {
                    val text = this@MapActivity.getString(R.string.map_search_hot) + this
                    this@MapActivity.et_map_search.hint = text
                } else {
                    val text = this@MapActivity.getString(R.string.map_search_hot) + "风雨操场"
                    this@MapActivity.et_map_search.hint = text
                }
            }
        })

        viewModel.mBasicMapData.observe(this, Observer<BasicMapData> {
            it?.run {
                if (hotWord != null && hotWord != "") {
                    val text = this@MapActivity.getString(R.string.map_search_hot) + hotWord
                    this@MapActivity.et_map_search.hint = text
                } else {
                    this@MapActivity.viewModel.getHot()
                }
                placeList?.let { it1 ->
                    PlaceData.placeList.addAll(it1)
                }
                PlaceData.mapData.mapBackgroundColor = mapBackgroundColor
                PlaceData.mapData.mapHeight = mapHeight
                PlaceData.mapData.mapWidth = mapWidth
                PlaceData.mapData.mapUrl = mapUrl
                PlaceData.mapData.zoomInId = zoomInId

                //测试数据
                val place = Place()
                val buildingRect = Place.BuildingRect()
                buildingRect.buildingTop = 7662f
                buildingRect.buildingLeft = 3452f
                buildingRect.buildingBottom = 7893f
                buildingRect.buildingRight = 3812f
                place.placeName = "老图书馆"
                place.buildingRectList = ArrayList()
                place.buildingRectList?.add(buildingRect)
                place.tagTop = 7884f
                place.tagLeft = 3335f
                place.tagBottom = 7956f
                place.tagRight = 3569f
                place.placeCenterX = 3644f
                place.placeCenterY = 7800f

                PlaceData.placeList.add(place)

                for (i: Int in PlaceData.placeList.indices) {
                    if (PlaceData.placeList[i].placeId == zoomInId) {
                        iv_map.setOnImageEventListener(object : OnImageEventListener {
                            override fun onImageLoaded() {
                            }

                            override fun onReady() {
                                pinAndZoomIn(1558f, 8714f)    //大门测试数据
//                                pinAndZoomIn(PlaceData.placeList[i].placeCenterX, PlaceData.placeList[i].placeCenterY)

//                                    //地点测试数据
//                                    val place = Place()
//                                    val buildingRect = Place.BuildingRect()
//                                    buildingRect.buildingTop = 7662f
//                                    buildingRect.buildingLeft = 3452f
//                                    buildingRect.buildingBottom = 7893f
//                                    buildingRect.buildingRight = 3812f
//                                    place.placeName = "老图书馆"
//                                    place.buildingRectList = ArrayList()
//                                    place.buildingRectList?.add(buildingRect)
//                                    place.tagTop = 7884f
//                                    place.tagLeft = 3335f
//                                    place.tagBottom = 7956f
//                                    place.tagRight = 3569f
//                                    place.placeCenterX = 3644f
//                                    place.placeCenterY = 7800f
//
//                                    PlaceData.placeList.clear()
//                                    PlaceData.placeList.add(place)
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
                        break
                    }
                }
            }
        })
    }

    fun pinAndZoomIn(x: Float, y: Float) {
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
            loadDetailFragment()
//
//            //TODO:在MVVM的Model里进行数据库操作
            Thread(Runnable {
                val placeArray = PlaceDatabase.getDataBase(this@MapActivity)
                        .getPlaceDao().queryAllPlaces()
                // TODO: placeArray[0]下标越界
//                PlaceData.placeList.add(placeArray[0])
                //PlaceData.placeList[0].placeName?.let { LogUtils.d("MapActivity" , it) }
            }).start()
        }
    }

    fun removeAllPin() {
        iv_map.removeAllPin()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            //searchFragment在栈中
            if (supportFragmentManager.popBackStackImmediate("searchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)) {
                searchFragment = null
                et_map_search.setText("")
            } else {
                //只有detailFragment在栈中
//                detailFragmentScrollToBottom()
                supportFragmentManager.popBackStack("detailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
            }
        } else {
            if (et_map_search.isFocused) {
                window.decorView.findViewById<View>(android.R.id.content).requestFocus()
            } else {
                super.onBackPressed()
            }
        }
    }

    fun hideKeyBoard() {
        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(window.decorView.windowToken, 0)
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

    fun loadDetailFragment() {
//        val redRockBottomSheetDialog = RedRockBottomSheetDialog(BaseApp.context)
//        val view = View.inflate(this, R.layout.map_fragment_detail, null)
//        redRockBottomSheetDialog.setContentView(view)
        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        detailFragment = DetailFragment()
        detailFragment.setName("这是一个巨长的地点名称haha")
        transaction.replace(R.id.fm_detail, detailFragment)
        transaction.addToBackStack("detailFragment")
        transaction.commitAllowingStateLoss()
    }

    private fun detailFragmentScrollToBottom() {
        val params = fm_detail?.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior is BottomSheetBehavior) {
            //拿到下方tabs的y坐标，即为我要的偏移量
//            val y: Float = binding.tabs.getY()
            //注意传递负值
            behavior.setExpandedOffset(-50)
        }
    }
}
