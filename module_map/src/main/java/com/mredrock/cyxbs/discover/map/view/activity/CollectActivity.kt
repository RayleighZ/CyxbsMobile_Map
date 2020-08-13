package com.mredrock.cyxbs.discover.map.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.databinding.MapActivityCollectBinding
import com.mredrock.cyxbs.discover.map.view.fragment.DetailFragment
import com.mredrock.cyxbs.discover.map.viewmodel.CollectViewModel
import kotlinx.android.synthetic.main.map_activity_collect.*
import java.lang.ref.WeakReference

class CollectActivity : BaseViewModelActivity<CollectViewModel>() {
    private var placeId: Int = 1
    override val viewModelClass = CollectViewModel::class.java
    override val isFragmentActivity = false

    companion object {
        fun actionStart(context: Context, placeId: Int) {
            val intent = Intent(context, CollectActivity::class.java)
            intent.putExtra("placeId", placeId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val collectBinding: MapActivityCollectBinding =
                DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.map_activity_collect, null, false)
        setContentView(collectBinding.root)

        collectBinding.viewModel = viewModel

        //初始化状态栏高度的透明View
        val statusBarLinearParams = view_map_collect_status_bar.layoutParams //取控件当前的布局参数
        statusBarLinearParams.height = getStatusBarHeight() //状态栏高度
        view_map_collect_status_bar.layoutParams = statusBarLinearParams

        placeId = intent.getIntExtra("placeId", 1)
        for (i: Int in PlaceData.placeList.indices) {
            if (PlaceData.placeList[i].placeId == DetailFragment.placeId) {
                viewModel.placeName.set(PlaceData.placeList[i].placeName)
                PlaceData.placeList[i].placeName?.let { LogUtils.d("CollectActivity", it) }
            }
        }

        map_edittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (this@CollectActivity.map_edittext.selectionEnd == start) {
                    this@CollectActivity.map_edittext.setSelection(this@CollectActivity.map_edittext.text.length)
                }
            }
        })

        viewModel.addRecommend(WeakReference(chipGroup_map_recommend_name), listOf("我的宿舍", "隐秘的角落", "最爱的食堂", "最常去的运动场"))

        bindingEditAndTextNum()
    }

    //展示当前输入的字符个数
    private fun bindingEditAndTextNum() {
        map_edittext.addTextChangedListener {
            viewModel.editTextCharNum.set("${it?.length}/8")
        }
    }
}
