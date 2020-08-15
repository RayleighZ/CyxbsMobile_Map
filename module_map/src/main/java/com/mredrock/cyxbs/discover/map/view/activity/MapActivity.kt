package com.mredrock.cyxbs.discover.map.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
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
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.BasicMapData
import com.mredrock.cyxbs.discover.map.bean.ClassifyData
import com.mredrock.cyxbs.discover.map.bean.ClassifyData.ClassifyPlace
import com.mredrock.cyxbs.discover.map.bean.FavoritePlace
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.database.PlaceDatabase
import com.mredrock.cyxbs.discover.map.model.MapDataModel
import com.mredrock.cyxbs.discover.map.util.DownloadProgressDialogUtil
import com.mredrock.cyxbs.discover.map.util.DownloadProgressDialogUtil.getProgressBar
import com.mredrock.cyxbs.discover.map.util.isOnlineByPing
import com.mredrock.cyxbs.discover.map.view.adapter.ClassifyAdapter
import com.mredrock.cyxbs.discover.map.view.adapter.FavoriteAdapter
import com.mredrock.cyxbs.discover.map.view.fragment.DetailFragment
import com.mredrock.cyxbs.discover.map.view.fragment.SearchFragment
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_fragment_search.*
import kotlinx.android.synthetic.main.map_pop_window_no_favorite.view.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class MapActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(MapViewModel::class.java) }
    private val mapPath = Environment.getExternalStorageDirectory().absolutePath + "/CQUPTMap/CQUPTMap.jpg"

    //    private lateinit var popWindow: PopupWindow
    private val classifyItemList: MutableList<ClassifyPlace> = ArrayList()

    //    private val favoriteItemList: MutableList<FavoritePlace> = ArrayList()
    private val classifyAdapter: ClassifyAdapter = ClassifyAdapter(this, classifyItemList)

    //    private val favoriteAdapter: FavoriteAdapter = FavoriteAdapter(this, favoriteItemList)
    private var searchFragmentIsShowing: Boolean = false        //判断搜索fragment是否显示
    private var detailFragment: DetailFragment? = null
    var searchFragment: SearchFragment? = null
    private var downloadProgressDialog: AlertDialog? = null
    private var isCancelDownloadMap = false
    private var mapTimeStamp = 0L       //从服务器拿到的地图时间戳
    private var needGetMap = 0

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

        getPermissions()
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

//        iv_map.setMaximumDpi(10)

        //初始化我的收藏弹出菜单
//        val popWindowView: View = LayoutInflater.from(this).inflate(R.layout.map_pop_window_no_favorite, null)
//        val linearLayoutManager = LinearLayoutManager(this)
//        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
//        popWindowView.rv_map_pop_window.layoutManager = linearLayoutManager
//        popWindowView.rv_map_pop_window.adapter = favoriteAdapter
//        popWindowView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
//        popWindow = PopupWindow(popWindowView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true) //-2 是包裹内容，-1 是填充父窗体
//        popWindow.setBackgroundDrawable(getDrawable(android.R.color.transparent))//透明背景
//        popWindow.isOutsideTouchable = true
//        popWindow.isFocusable = true
//        popWindow.animationStyle = R.style.PopWindow_Anim_Style

        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_map_classify.layoutManager = layoutManager
        rv_map_classify.adapter = classifyAdapter

        et_map_search.setOnTouchListener { v, event ->
            if (getDetailFragmentState() == BottomSheetBehavior.STATE_EXPANDED) {
                detailFragmentScroll(BottomSheetBehavior.STATE_COLLAPSED)
            }
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

                iv_map_back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.map_anl_push_left_out))
                tv_map_cancel_search.startAnimation(AnimationUtils.loadAnimation(this, R.anim.map_anl_push_right_in))
                tv_map_cancel_search.visible()
                val lp = this@MapActivity.et_map_search.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = dp2px(15f)
                lp.marginEnd = 0
                et_map_search.layoutParams = lp
                iv_map_back.gone()
            }

            false
        }

        tv_map_cancel_search.setOnClickListener {
            val lp = et_map_search.layoutParams as ConstraintLayout.LayoutParams
            lp.marginStart = 0
            lp.marginEnd = dp2px(15f)
            et_map_search.layoutParams = lp
            iv_map_back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.map_anl_push_left_in))
            iv_map_back.visible()
            tv_map_cancel_search.startAnimation(AnimationUtils.loadAnimation(this, R.anim.map_anl_push_right_out))
            tv_map_cancel_search.gone()
            iv_map_back.callOnClick()
        }

        cl_map_favorite.setOnClickListener {
            //test data
//            PlaceData.collectPlaceList.add(PlaceData.placeList[0])
//            PlaceData.collectPlaceList.add(PlaceData.placeList[5])
//            PlaceData.collectPlaceList.add(PlaceData.placeList[30])
//            PlaceData.collectPlaceList.add(PlaceData.placeList[20])
//            PlaceData.collectPlaceList.add(PlaceData.placeList[13])
//            PlaceData.collectPlaceList.add(PlaceData.placeList[27])
//            PlaceData.collectPlaceList.add(PlaceData.placeList[10])

            if (PlaceData.collectPlaceList.size == 0) {
//                popWindowView.rv_map_pop_window.gone()
//                popWindowView.tv_map_no_favorite.visible()

                val toast: Toast = CyxbsToast.makeText(BaseApp.context, R.string.map_my_favorite_no_favorite_toast, Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, toast.xOffset, toast.yOffset)
                toast.show()
            } else {
                supportFragmentManager.popBackStack("detailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                detailFragment = null

                removeAllPin()
                for (i: Int in PlaceData.collectPlaceList.indices) {
                    pin(PlaceData.collectPlaceList[i].placeCenterX, PlaceData.collectPlaceList[i].placeCenterY)
                }
                ZoomInMin(PlaceData.mapData.mapWidth / 2f, PlaceData.mapData.mapHeight / 2f)
//                favoriteAdapter.notifyDataSetChanged()
            }

//            cl_map_favorite.measure(0, 0)
//            val location = IntArray(2)
//            cl_map_favorite.getLocationOnScreen(location)
//            cl_map_favorite.measure(0, 0)
//            val windowPos: IntArray? = calculatePopWindowPos(cl_map_favorite, popWindowView)
//            popWindow.showAtLocation(cl_map_favorite, Gravity.TOP or Gravity.START, (windowPos?.get(0)
//                    ?: 0) - dp2px(15f), (windowPos?.get(1) ?: 0))
//            popWindow.update()
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

                        //恢复边距
                        this@MapActivity.et_map_search.post {
                            this@MapActivity.iv_map_search_clear.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                            this@MapActivity.et_map_search.setPadding(
                                    this@MapActivity.et_map_search.paddingLeft,
                                    this@MapActivity.et_map_search.paddingTop,
                                    this@MapActivity.et_map_search.paddingLeft,
                                    this@MapActivity.et_map_search.paddingBottom
                            )
                        }

                        searchFragment?.run {
                            searchResultList.clear()
                            changeAdapter(1)

                            tv_map_search_history?.visible()
                            tv_map_search_delete_all?.visible()
                        }
                    } else {
                        var isFind = false

                        //避免输入的字与右侧叉号重叠
                        this@MapActivity.et_map_search.post {
                            this@MapActivity.iv_map_search_clear.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                            this@MapActivity.et_map_search.setPadding(
                                    this@MapActivity.et_map_search.paddingLeft,
                                    this@MapActivity.et_map_search.paddingTop,
                                    this@MapActivity.iv_map_search_clear.measuredWidth,
                                    this@MapActivity.et_map_search.paddingBottom
                            )
                        }
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

        //判断有无网络
        if (true) {
            viewModel.getBasicMapData()
            viewModel.getClassify()
        } else {
            CyxbsToast.makeText(BaseApp.context, "无网络，加载本地数据", Toast.LENGTH_SHORT).show()
            MapDataModel.loadMapData()
            this.mapTimeStamp = PlaceData.mapData.mapTimeStamp

            iv_map.setBackgroundColor(Color.parseColor(PlaceData.mapData.mapBackgroundColor))

            val mapPath = Environment.getExternalStorageDirectory().absolutePath + "/CQUPTMap/CQUPTMap.jpg"
            if (File(mapPath).exists()) {
                this@MapActivity.iv_map.setImage(ImageSource.uri(mapPath))
            } else {
                CyxbsToast.makeText(BaseApp.context, "本地无地图数据", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initObserver() {
        viewModel.mDownloadProgress.observe(this, Observer<Float> {
            downloadProgressDialog?.let { it1 ->
                it1.setOnCancelListener {
                    viewModel.mDisposable?.dispose()
                    CyxbsToast.makeText(BaseApp.context, getString(R.string.map_cancel_download_map), Toast.LENGTH_LONG).show()
                    downloadProgressDialog = null
                    isCancelDownloadMap = true
                }

            }
            if ((it * 100).toInt() >= 100 || it == 0f) {
                downloadProgressDialog?.let { it1 ->
                    it1.hide()
                    downloadProgressDialog = null
                }
            } else {
                if (downloadProgressDialog == null && !isCancelDownloadMap) {
                    downloadProgressDialog = DownloadProgressDialogUtil.getDownloadProgressDialog(this, getString(R.string.map_activity_download_map))
                }
                downloadProgressDialog?.let { it1 ->
                    it1.setCanceledOnTouchOutside(false)
                    it1.show()
                    it1.getProgressBar()?.progress = (it * 100).toInt()
                }
            }
        })

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

        viewModel.mMapPath.observe(this, Observer<String> {
            PlaceData.mapData.mapTimeStamp = this@MapActivity.mapTimeStamp
            this@MapActivity.iv_map.setImage(ImageSource.uri(it))
        })

        viewModel.mCollect.observe(this, Observer<List<FavoritePlace>> {
            val placeIdList: MutableList<Int> = ArrayList()
            for (i: Int in it.indices) {
                placeIdList.add(it[i].placeId)
            }
            PlaceData.collectPlaceList.clear()
            for (i: Int in PlaceData.placeList.indices) {
                if (placeIdList.contains(PlaceData.placeList[i].placeId)) {
                    PlaceData.placeList[i].isCollected = true
                    PlaceData.collectPlaceList.add(PlaceData.placeList[i])
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
                    PlaceData.placeList.clear()
                    PlaceData.placeList.addAll(it1)
                }
                PlaceData.mapData.mapBackgroundColor = mapBackgroundColor ?: "#FFFFFF"
                PlaceData.mapData.mapHeight = mapHeight
                PlaceData.mapData.mapWidth = mapWidth
                PlaceData.mapData.mapUrl = mapUrl
                PlaceData.mapData.zoomInId = zoomInId
                this@MapActivity.mapTimeStamp = mapTimeStamp

                this@MapActivity.iv_map.setBackgroundColor(Color.parseColor(mapBackgroundColor))

                val mapPath = Environment.getExternalStorageDirectory().absolutePath + "/CQUPTMap/CQUPTMap.jpg"
                MapDataModel.saveMapData()

                viewModel.getCollect()

//                if (File(mapPath).exists() && PlaceData.mapData.mapTimeStamp >= mapTimeStamp) {
                if (File(mapPath).exists()) {
                    this@MapActivity.iv_map.setImage(ImageSource.uri(mapPath))
                } else {
                    if (ContextCompat.checkSelfPermission(
                                    BaseApp.context,
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(
                                    BaseApp.context,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        CyxbsToast.makeText(BaseApp.context, "无存储权限，操作失败", Toast.LENGTH_LONG).show()
                    } else {
                        needGetMap = 1
                        viewModel.getMap()
                    }
                }

                for (i: Int in PlaceData.placeList.indices) {
                    if (PlaceData.placeList[i].placeId == zoomInId) {
                        iv_map.setOnImageEventListener(object : OnImageEventListener {
                            override fun onImageLoaded() {
                            }

                            override fun onReady() {
//                                pinAndZoomIn(1558f, 8714f, 1)    //大门测试数据
                                pinAndZoomIn(PlaceData.placeList[i].placeCenterX,
                                        PlaceData.placeList[i].placeCenterY,
                                        PlaceData.placeList[i].placeId)
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

    fun pinAndZoomIn(x: Float, y: Float, placeId: Int) {
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
            animationBuilder?.withOnAnimationEventListener(object : SubsamplingScaleImageView.OnAnimationEventListener {
                override fun onComplete() {
                    loadDetailFragment(placeId)
                }

                override fun onInterruptedByUser() {
                }

                override fun onInterruptedByNewAnim() {
                }

            })
            animationBuilder?.withDuration(700)?.withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)?.withInterruptible(false)?.start()

            //TODO:在MVVM的Model里进行数据库操作
            Thread(Runnable {
                // TODO: 2020/8/14 0014 下面两行会导致崩溃
//                val placeArray = PlaceDatabase.getDataBase(this@MapActivity)
//                        .getPlaceDao().queryAllPlaces()
                // TODO: placeArray[0]下标越界
//                PlaceData.placeList.add(placeArray[0])
                //PlaceData.placeList[0].placeName?.let { LogUtils.d("MapActivity" , it) }
            }).start()

            detailFragmentScroll(BottomSheetBehavior.STATE_COLLAPSED)
        }
    }

    fun pin(x: Float, y: Float) {
        if (iv_map.isReady) {
            val center = PointF(x, y)
            val density = resources.displayMetrics.densityDpi.toFloat()
            var pin = BitmapFactory.decodeResource(this.resources, R.drawable.map_ic_pin)
            val w = density / 420f * pin.width
            val h = density / 420f * pin.height
            pin = Bitmap.createScaledBitmap(pin, w.toInt(), h.toInt(), true)
            iv_map.addPin(pin, center)
        }
    }

    fun ZoomInMin(x: Float, y: Float) {
        if (iv_map.isReady) {
            val animationBuilder: AnimationBuilder? = iv_map.animateScaleAndCenter(iv_map.minScale, PointF(x, y))
            animationBuilder?.withDuration(700)?.withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)?.withInterruptible(false)?.start()
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
                val lp = et_map_search.layoutParams as ConstraintLayout.LayoutParams
                lp.marginStart = 0
                lp.marginEnd = dp2px(15f)
                et_map_search.layoutParams = lp
                iv_map_back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.map_anl_push_left_in))
                iv_map_back.visible()
                tv_map_cancel_search.startAnimation(AnimationUtils.loadAnimation(this, R.anim.map_anl_push_right_out))
                tv_map_cancel_search.gone()
                et_map_search.setText("")
            } else {
                //只有detailFragment在栈中
                if (getDetailFragmentState() == BottomSheetBehavior.STATE_EXPANDED) {
                    detailFragmentScroll(BottomSheetBehavior.STATE_COLLAPSED)
                } else {
                    supportFragmentManager.popBackStack("detailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    detailFragment = null
                }
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

    private fun loadDetailFragment(placeId: Int) {
//        val redRockBottomSheetDialog = RedRockBottomSheetDialog(BaseApp.context)
//        val view = View.inflate(this, R.layout.map_fragment_detail, null)
//        redRockBottomSheetDialog.setContentView(view)
        DetailFragment.placeId = placeId
        //进一步优化，此处不每一次都产生一次对象，而是地点改变时传递新的id
        if (detailFragment == null) {
            val transaction = supportFragmentManager.beginTransaction()
            detailFragment = DetailFragment()
            detailFragment?.let {
                transaction.replace(R.id.fm_detail, it)
                        .addToBackStack("detailFragment")
                        .commitAllowingStateLoss()
            }
        } else {
            detailFragment?.refresh(placeId)
        }
    }

    private fun detailFragmentScroll(state: Int) {
        val params = fm_detail?.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior is BottomSheetBehavior) {
            if (behavior.state != state) {
                behavior.state = state
            }
        }
    }

    private fun getDetailFragmentState(): Int {
        val params = fm_detail?.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        if (behavior is BottomSheetBehavior) {
            return behavior.state
        }
        return -1
    }

    private fun getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 1
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            1 -> if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                CyxbsToast.makeText(BaseApp.context, "无法获取存储权限，程序可能异常", Toast.LENGTH_LONG).show()
            } else {
                if (File(mapPath).exists()) {
                    this@MapActivity.iv_map.setImage(ImageSource.uri(mapPath))
                } else {
                    viewModel.getMap()
                    needGetMap = 2
                }
            }
        }
    }

    fun addHot(placeId: Int) {
        viewModel.addHot(placeId)
    }
}
