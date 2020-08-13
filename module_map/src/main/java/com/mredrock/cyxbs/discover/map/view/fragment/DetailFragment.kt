package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.databinding.MapFragmentDetailBinding
import com.mredrock.cyxbs.discover.map.util.MapAlertDialogUtil
import com.mredrock.cyxbs.discover.map.util.MapAlertDialogUtil.setOnClickListener
import com.mredrock.cyxbs.discover.map.view.activity.CollectActivity
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
    var placeId = -1 //设置地点ID，默认为-1，使用default值
    lateinit var mView: View
    lateinit var tvName: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val collectBinding : MapFragmentDetailBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.map_fragment_detail, null, false)

        collectBinding.viewModel = viewModel
        mView = collectBinding.root//inflater.inflate(R.layout.map_fragment_detail, container, false)
        if (activity == null)
            return mView
        mView.invisible()   //先隐藏，等设置头部可见高度再显示
        tvName = mView.findViewById(R.id.tv_map_place_name)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setIcon(WeakReference(view.findViewById(R.id.ll_map_icon_container)), listOf("操场", "活动中心"))
        viewModel.setDetails(WeakReference(view.findViewById(R.id.chip_group_detail_container)), listOf( "热爱跑步的请进", "太强了跑步的涛哥", "张涛男神出没", "卑微张煜在线减肥"))
        val vpAdapter = DetailViewPageAdapter()
        map_viewpager.adapter = vpAdapter
        map_viewpager.pageMargin = 24
        viewModel.setDetailPic(vpAdapter, listOf())
        tv_map_place_name.isSelected = true
        if (placeId != -1)
        viewModel.placeName.set(PlaceData.placeList[placeId].placeName)

        map_iv_start.setOnClickListener {
            //举例，传入地点ID加载收藏界面
            context?.let { it1 -> CollectActivity.actionStart(it1, 0) }
        }

        //进行测试，此处随便给了几个url
        map_tv_show_maore_pic.setOnClickListener{
            context?.let { it1 -> ShowAllPicActivity.actionStart(it1, arrayOf("https://bihu-head.oss-cn-chengdu.aliyuncs.com/Eva3.jpg" , "https://bihu-head.oss-cn-chengdu.aliyuncs.com/eva.png", "https://image.baidu.com/search/detail?ct=503316480&z=undefined&tn=baiduimagedetail&ipn=d&word=%E8%8A%9C%E6%B9%96%E5%A4%A7%E5%8F%B8%E9%A9%AC&step_word=&ie=utf-8&in=&cl=2&lm=-1&st=undefined&hd=undefined&latest=undefined&copyright=undefined&cs=1885695158,4148174646&os=1488174142,779019841&simid=3601840413,379195290&pn=0&rn=1&di=154770&ln=1036&fr=&fmq=1597312174758_R&fm=&ic=undefined&s=undefined&se=&sme=&tab=0&width=undefined&height=undefined&face=undefined&is=0,0&istype=0&ist=&jit=&bdtype=0&spn=0&pi=0&gsm=0&objurl=http%3A%2F%2Fpic1.zhimg.com%2F80%2Fv2-5cc7f1f5624d5d4ff89cf0da89f75cac_hd.jpg&rpstart=0&rpnum=0&adpicid=0&force=undefined")) }
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
}