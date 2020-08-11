package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.databinding.ObservableField
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.adapter.DetailViewPageAdapter
import com.mredrock.cyxbs.discover.map.viewmodel.DetailViewModel
import kotlinx.android.synthetic.main.map_fragment_detail.*
import org.jetbrains.anko.sdk27.coroutines.onTouch
import java.lang.ref.WeakReference

/**
 * @date 2020-08-10
 * @author Sca RayleighZ
 */
class DetailFragment : BaseViewModelFragment<DetailViewModel>() {
    override val viewModelClass = DetailViewModel::class.java
    lateinit var placeName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if(activity == null)
            return inflater.inflate(R.layout.map_fragment_detail, container, false)

        return inflater.inflate(R.layout.map_fragment_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setIcon(WeakReference(view.findViewById(R.id.ll_map_icon_container)) , listOf("操场" , "活动中心"))
        viewModel.setDetails(WeakReference(view.findViewById(R.id.chip_group_detail_container)), listOf("情侣不少" , "热爱跑步的请进" ,"太强了跑步的涛哥" , "张涛男神出没" , "卑微张煜在线减肥"))
        val vpAdapter = DetailViewPageAdapter()
        map_viewpager.adapter = vpAdapter
        map_viewpager.pageMargin = 24
        viewModel.setDetailPic(vpAdapter , listOf())
        tv_map_place_name.isSelected = true
        viewModel.placeName.set(placeName)
        //viewModel.setDetailPic(vpAdapter , listOf("https://bihu-head.oss-cn-chengdu.aliyuncs.com/Eva3.jpg" , "https://bihu-head.oss-cn-chengdu.aliyuncs.com/eva.png" , "瞎数据"))
    }
    fun setName(placeName : String){
        this.placeName = placeName
    }
}