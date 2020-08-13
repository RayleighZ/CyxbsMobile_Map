package com.mredrock.cyxbs.discover.map.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.view.adapter.PhotoStreamAdapter
import kotlinx.android.synthetic.main.map_activity_show_all_pic.*

class ShowAllPicActivity : BaseActivity() {

    companion object{
        fun actionStart(context: Context, arrayOfUrls: Array<String>){
            val intent = Intent(context , ShowAllPicActivity::class.java)
            intent.putExtra("urls" , arrayOfUrls)
            context.startActivity(intent)
        }
    }

    override val isFragmentActivity = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.map_activity_show_all_pic)

        initStatusBar()

        val urls = intent.getStringArrayExtra("urls")
        if (urls.isEmpty()){
            rv_map_allpic_show_photo.setBackgroundResource(R.drawable.common_place_holder)
            tv_map_allpic_no_more.visibility = View.VISIBLE
        } else {
            tv_map_allpic_no_more.visibility = View.GONE
            rv_map_allpic_show_photo.adapter = PhotoStreamAdapter(urls , this)

            rv_map_allpic_show_photo.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun initStatusBar(){
        val statusBarLinearParams = view_map_allpic_status_bar.layoutParams
        statusBarLinearParams.height = getStatusBarHeight()
        view_map_allpic_status_bar.layoutParams = statusBarLinearParams
    }
}
