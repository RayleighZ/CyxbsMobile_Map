package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.RedRockBottomSheetDialog
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.viewmodel.DetailViewModel
import java.lang.ref.WeakReference

/**
 * @date 2020-08-10
 * @author Sca RayleighZ
 */
class DetailFragment : BaseViewModelFragment<DetailViewModel>() {
    override val viewModelClass = DetailViewModel::class.java

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
    }
}