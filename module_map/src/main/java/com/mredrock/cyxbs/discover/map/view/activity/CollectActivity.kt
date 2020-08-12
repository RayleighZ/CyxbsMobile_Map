package com.mredrock.cyxbs.discover.map.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.viewmodel.CollectViewModel
import kotlinx.android.synthetic.main.map_activity_collect.*
import kotlinx.android.synthetic.main.map_activity_map.*

class CollectActivity : BaseViewModelActivity<CollectViewModel>() {

    override val viewModelClass = CollectViewModel::class.java
    override val isFragmentActivity = false

    companion object{
        fun actionStart(context: Context , placeId : Int){
            val intent = Intent(context , CollectActivity::class.java)
            intent.putExtra("placeId" , placeId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_collect)

        //初始化状态栏高度的透明View
        val statusBarLinearParams = view_map_collect_status_bar.layoutParams //取控件当前的布局参数
        statusBarLinearParams.height = getStatusBarHeight() //状态栏高度
        view_map_collect_status_bar.layoutParams = statusBarLinearParams

        viewModel.placeName.set(PlaceData.placeList[intent.getIntExtra("placeId" , 0)].placeName)
        PlaceData.placeList[intent.getIntExtra("placeId" , 1)].placeName?.let { LogUtils.d("CollectActivity" , it) }
    }
}
