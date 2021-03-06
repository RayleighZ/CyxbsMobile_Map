package com.mredrock.cyxbs.discover.map.view.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.model.MapDataModel
import com.mredrock.cyxbs.discover.map.model.PlaceModel
import com.mredrock.cyxbs.discover.map.util.DownloadProgressDialogUtil
import com.mredrock.cyxbs.discover.map.util.DownloadProgressDialogUtil.getProgressBar
import com.mredrock.cyxbs.discover.map.util.isNetworkConnected
import com.mredrock.cyxbs.discover.map.view.adapter.ClassifyAdapter
import com.mredrock.cyxbs.discover.map.view.fragment.DetailFragment
import com.mredrock.cyxbs.discover.map.view.fragment.SearchFragment
import com.mredrock.cyxbs.discover.map.viewmodel.MapViewModel
import com.zhihu.matisse.Matisse
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_fragment_search.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class MapActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(MapViewModel::class.java) }
    private val mapPath = Environment.getExternalStorageDirectory().absolutePath + "/CQUPTMap/CQUPTMap.jpg"
    private val classifyItemList: MutableList<ClassifyPlace> = ArrayList()
    private val classifyAdapter: ClassifyAdapter = ClassifyAdapter(this, classifyItemList)
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
        if (!userState.isLogin() && isNetworkConnected()) {
            //这里只是模拟一下登录，如果有并发需求，自己设计
            Thread {
                userState.login(this, "2019213962", "062115")
            }.start()
        }

        //初始化状态栏高度的透明View
        val statusBarLinearParams = view_status_bar.layoutParams //取控件当前的布局参数
        statusBarLinearParams.height = getStatusBarHeight() //状态栏高度
        view_status_bar.layoutParams = statusBarLinearParams

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

            if (PlaceData.collectPlaceList.size == 0) {

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

                iv_map_lock.setImageResource(R.drawable.map_ic_unlock)
                iv_map.isLocked = false
            }
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

        iv_map_lock.setOnClickListener {
            if (iv_map.isLocked) {
                iv_map_lock.setImageResource(R.drawable.map_ic_unlock)
                iv_map.isLocked = false
            } else {
                iv_map_lock.setImageResource(R.drawable.map_ic_lock)
                iv_map.isLocked = true
            }
        }

        initObserver()

        //判断有无网络
        if (isNetworkConnected()) {
            viewModel.getBasicMapData()
            viewModel.getClassify()
        } else {
            CyxbsToast.makeText(BaseApp.context, "无网络，加载本地数据", Toast.LENGTH_SHORT).show()
            MapDataModel.loadMapData()
            PlaceModel.loadAllData(false) {
                LogUtils.d("MapActivity", PlaceData.placeList.size.toString())
            }
            /*PlaceModel.loadCollect(false) {
                LogUtils.d("MapActivity", PlaceData.collectPlaceList.size.toString())
            }
            PlaceModel.loadHistory(false) {
                LogUtils.d("MapActivity", PlaceData.searchHistoryList.size.toString())
            }
            PlaceModel.loadPlace(false) {
                LogUtils.d("MapActivity", PlaceData.placeList.size.toString())
            }*/
            this.mapTimeStamp = PlaceData.mapData.mapTimeStamp

            iv_map.setBackgroundColor(Color.parseColor(PlaceData.mapData.mapBackgroundColor))

            val mapPath = Environment.getExternalStorageDirectory().absolutePath + "/CQUPTMap/CQUPTMap.jpg"

            if (File(mapPath).exists()) {
                this@MapActivity.iv_map.setImage(ImageSource.uri(mapPath))
            } else {
                CyxbsToast.makeText(BaseApp.context, "本地无地图数据", Toast.LENGTH_LONG).show()
            }

            val text = getString(R.string.map_search_hot) + "风雨操场"
            et_map_search.hint = text
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

        viewModel.mCollect.observe(this, Observer<FavoritePlace> {
            it.placeId?.let { it1 ->
                PlaceData.collectPlaceList.clear()
                for (i: Int in PlaceData.placeList.indices) {
                    if (it1.contains(PlaceData.placeList[i].placeId)) {
                        PlaceData.placeList[i].isCollected = true
                        PlaceData.collectPlaceList.add(PlaceData.placeList[i])
                    }
                }
                //存储所有的收藏地点
                PlaceModel.saveAllCollect(false) { }
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

                PlaceData.mapData.mapTimeStamp = mapTimeStamp
                this@MapActivity.mapTimeStamp = mapTimeStamp

                this@MapActivity.iv_map.setBackgroundColor(Color.parseColor(mapBackgroundColor))

                viewModel.getCollect()

                PlaceModel.saveAllPlace(false) {
                    LogUtils.d("MapActivity", "存储时内存中 place" + PlaceData.placeList.size.toString())
                }

                PlaceModel.saveAllCollect(false) {
                    LogUtils.d("MapActivity", "存储时内存中 collect" + PlaceData.collectPlaceList.size.toString())
                }

                PlaceModel.saveAllHistory(false) {
                    LogUtils.d("MapActivity", " 存储时内存中 history" + PlaceData.placeList.size.toString())
                }

                val mapPath = Environment.getExternalStorageDirectory().absolutePath + "/CQUPTMap/CQUPTMap.jpg"

                if (File(mapPath).exists() && MapDataModel.getMapTimeStamp() >= mapTimeStamp) {
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
                        CyxbsToast.makeText(BaseApp.context, "无存储权限，第一次操作失败", Toast.LENGTH_LONG).show()
                    } else {
                        needGetMap = 1
                        viewModel.getMap()
                    }
                }

                MapDataModel.saveMapData()

                for (i: Int in PlaceData.placeList.indices) {
                    if (PlaceData.placeList[i].placeId == zoomInId) {
                        iv_map.setOnImageEventListener(object : OnImageEventListener {
                            override fun onImageLoaded() {
                            }

                            override fun onReady() {
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
                    iv_map.lastPlace = getPlaceById(placeId)
                    loadDetailFragment(placeId)
                }

                override fun onInterruptedByUser() {
                }

                override fun onInterruptedByNewAnim() {
                }

            })
            animationBuilder?.withDuration(700)?.withEasing(SubsamplingScaleImageView.EASE_OUT_QUAD)?.withInterruptible(false)?.start()

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
        iv_map.lastPlace = null
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

    private fun loadDetailFragment(placeId: Int) {
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
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                        this, arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
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
            1 -> if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                CyxbsToast.makeText(BaseApp.context, "无法获取存储权限，程序可能异常", Toast.LENGTH_LONG).show()
            } else if (grantResults.isEmpty() || grantResults[2] != PackageManager.PERMISSION_GRANTED) {
                CyxbsToast.makeText(BaseApp.context, "无法获取相机权限，程序可能异常", Toast.LENGTH_LONG).show()
            } else {
                if (needGetMap == 1) {
                    CyxbsToast.makeText(BaseApp.context, "成功获取权限，第二次操作成功！", Toast.LENGTH_SHORT).show()
                }
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

    override fun onStop() {
        Thread {
            PlaceModel.saveAllCollect(true) {
                LogUtils.d("MapActivity", "存储时内存中 collect" + PlaceData.collectPlaceList.size.toString())
            }
            PlaceModel.saveAllPlace(true) {
                LogUtils.d("MapActivity", "存储时内存中 place" + PlaceData.placeList.size.toString())
            }
            PlaceModel.saveAllHistory(true) {
                LogUtils.d("MapActivity", " 存储时内存中 history" + PlaceData.placeList.size.toString())
            }
        }.join()
        super.onStop()
    }

    fun pinByClassify(type: String) {
        var times = 0
        viewModel.pinByType(type) {
            for (place in PlaceData.placeList) {
                for (id in it) {
                    if (place.placeId == id) {
                        if (times == 0) {
                            supportFragmentManager.popBackStack("detailFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                            detailFragment = null
                            removeAllPin()
                        }
                        times++
                        pin(place.placeCenterX, place.placeCenterY)
                    }
                }
            }
            if (times != 0) ZoomInMin(PlaceData.mapData.mapWidth / 2f, PlaceData.mapData.mapHeight / 2f)
            iv_map_lock.setImageResource(R.drawable.map_ic_unlock)
            iv_map.isLocked = false
        }
    }

    fun getPlaceById(placeId: Int): Place? {
        for (i: Int in PlaceData.placeList.indices) {
            if (PlaceData.placeList[i].placeId == placeId) {
                return PlaceData.placeList[i]
            }
        }
        return null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            viewModel.uploadPhoto(Matisse.obtainResult(data), Matisse.obtainPathResult(data), DetailFragment.placeId)
        }
    }
}
