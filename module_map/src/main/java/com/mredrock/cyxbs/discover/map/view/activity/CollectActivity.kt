package com.mredrock.cyxbs.discover.map.view.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.viewmodel.CollectViewModel

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
        viewModel.placeName.set(PlaceData.placeList[intent.getIntExtra("placeId" , 0)].placeName)
        PlaceData.placeList[intent.getIntExtra("placeId" , 1)].placeName?.let { LogUtils.d("CollectActivity" , it) }
    }
}
