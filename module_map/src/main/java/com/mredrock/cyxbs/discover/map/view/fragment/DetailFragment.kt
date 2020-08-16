package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.databinding.MapFragmentDetailBinding
import com.mredrock.cyxbs.discover.map.util.MapAlertDialogUtil
import com.mredrock.cyxbs.discover.map.util.MapAlertDialogUtil.setOnClickListener
import com.mredrock.cyxbs.discover.map.view.activity.ShowAllPicActivity
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
    lateinit var mView: View
    lateinit var tvName: TextView
    lateinit var vpAdapter: DetailViewPageAdapter
    lateinit var curPlace: Place

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val collectBinding: MapFragmentDetailBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.map_fragment_detail, null, false)

        collectBinding.viewModel = viewModel
        mView = collectBinding.root//inflater.inflate(R.layout.map_fragment_detail, container, false)
        if (activity == null)
            return mView
        mView.invisible()   //先隐藏，等设置头部可见高度再显示
        tvName = mView.findViewById(R.id.tv_map_place_name)
        return mView
    }

    fun refresh(id: Int) {
        placeId = id
        if (placeId != -1) {
            for (i: Int in PlaceData.placeList.indices) {
                if (PlaceData.placeList[i].placeId == placeId) {
                    curPlace = PlaceData.placeList[i]
                    viewModel.placeName.set(curPlace.placeName)
                    viewModel.curPlace = curPlace
                    viewModel.setDetail(WeakReference(ll_map_icon_container), WeakReference(chip_group_detail_container), vpAdapter)

                    if (curPlace.isCollected) {
                        map_iv_keep.setImageResource(R.drawable.map_ic_collected)
                    } else {
                        map_iv_keep.setImageResource(R.drawable.map_ic_stared)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel.setIcon(WeakReference(view.findViewById(R.id.ll_map_icon_container)), listOf("操场", "活动中心"))
        // viewModel.setDetails(WeakReference(view.findViewById(R.id.chip_group_detail_container)), listOf("热爱跑步的请进", "太强了跑步的涛哥", "张涛男神出没", "卑微张煜在线减肥"))
        vpAdapter = DetailViewPageAdapter()
        map_viewpager.adapter = vpAdapter
        map_viewpager.pageMargin = 24
        viewModel.setDetailPic(vpAdapter, listOf())
        tv_map_place_name.isSelected = true
        refresh(placeId)

        map_iv_keep.setOnClickListener {
            if (curPlace.isCollected) {
                context?.let {
                    val dialog: AlertDialog = MapAlertDialogUtil.getMapAlertDialog(it, "取消收藏",
                            "是否将该地点从“我的收藏”中移出")
                    dialog.setOnClickListener(
                            View.OnClickListener {
                                dialog.cancel()
                            },
                            View.OnClickListener {
                                dialog.cancel()
                                viewModel.delKeep(WeakReference(map_iv_keep))
                            }
                    ).show()
                }
            } else {
                viewModel.addKeep(WeakReference(map_iv_keep))
            }
        }

        map_tv_show_maore_pic.setOnClickListener {
            context?.let { it1 ->
                val arrayList = ArrayList<String>(viewModel.listOfPicUrls)
                ShowAllPicActivity.actionStart(it1,arrayList.toTypedArray() )
            }
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

        map_tv_share_photo.setOnClickListener {
            context?.let {
                val dialog: AlertDialog = MapAlertDialogUtil.getMapAlertDialog(it, getString(R.string.map_alert_dialog_share_photo_title),
                        getString(R.string.map_alert_dialog_share_photo_content))
                dialog.setOnClickListener(View.OnClickListener {
                    dialog.cancel()
                }).show()
            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(activity, R.anim.map_anl_show)
        } else {
            AnimationUtils.loadAnimation(activity, R.anim.map_anl_hide)
        }
    }

    companion object {
        //设置地点ID，默认为-1，使用default值
        var placeId: Int = -1
    }
}