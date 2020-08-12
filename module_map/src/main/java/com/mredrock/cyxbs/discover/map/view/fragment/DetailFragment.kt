package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.util.MapAlertDialogUtil
import com.mredrock.cyxbs.discover.map.util.MapAlertDialogUtil.setOnClickListener
import com.mredrock.cyxbs.discover.map.view.activity.CollectActivity
import com.mredrock.cyxbs.discover.map.view.adapter.DetailViewPageAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.map_fragment_detail.*
import java.lang.ref.WeakReference


/**
 * @date 2020-08-10
 * @author Sca RayleighZ
 */
class DetailFragment : BaseViewModelFragment<DetailViewModel>() {
    override val viewModelClass = DetailViewModel::class.java
    lateinit var placeName: String
    lateinit var mView: View
    lateinit var tvName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(R.layout.map_fragment_detail, container, false)
        if (activity == null)
            return mView
        mView.invisible()   //先隐藏，等设置头部可见高度再显示
        tvName = mView.findViewById(R.id.tv_map_place_name)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setIcon(WeakReference(view.findViewById(R.id.ll_map_icon_container)), listOf("操场", "活动中心"))
        viewModel.setDetails(WeakReference(view.findViewById(R.id.chip_group_detail_container)), listOf("情侣不少", "热爱跑步的请进", "太强了跑步的涛哥", "张涛男神出没", "卑微张煜在线减肥"))
        val vpAdapter = DetailViewPageAdapter()
        map_viewpager.adapter = vpAdapter
        map_viewpager.pageMargin = 24
        viewModel.setDetailPic(vpAdapter, listOf())
        tv_map_place_name.isSelected = true
        viewModel.placeName.set(placeName)

        map_iv_start.setOnClickListener {
            context?.let { it1 -> CollectActivity.actionStart(it1, 1) }
        }

        //动态设置头部可见高度
        val params = (mView.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior
        map_textview2?.let {
            it.post {
                if (behavior is BottomSheetBehavior) {
                    behavior.peekHeight = it.top + BaseApp.context.getStatusBarHeight()
                }
                mView.visible()     //设置完高度再显示，要不然会从大到小闪一下
            }
        }

        //viewModel.setDetailPic(vpAdapter , listOf("https://bihu-head.oss-cn-chengdu.aliyuncs.com/Eva3.jpg" , "https://bihu-head.oss-cn-chengdu.aliyuncs.com/eva.png" , "瞎数据"))

        map_textview3.setOnClickListener {
            context?.let {
                val dialog: AlertDialog = MapAlertDialogUtil.getMapAlertDialog(it, getString(R.string.map_alert_dialog_share_photo_title),
                        getString(R.string.map_alert_dialog_share_photo_content))
                dialog.setOnClickListener(View.OnClickListener {
                    dialog.cancel()
                }).show()
            }
        }
    }

    fun setName(placeName: String) {
        this.placeName = placeName
    }
}