package com.mredrock.cyxbs.discover.map.view.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.view.adapter.PhotoStreamAdapter
import com.mredrock.cyxbs.discover.map.view.fragment.ShowPicFragment
import kotlinx.android.synthetic.main.map_activity_show_all_pic.*
import kotlinx.android.synthetic.main.map_fragment_show_photo.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ShowAllPicActivity : BaseActivity() {

    private var showPicFragment: ShowPicFragment? = null

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

        iv_map_allpic_back.onClick {
            finish()
        }

        initStatusBar()

        val urls = intent.getStringArrayExtra("urls")
        if (urls.isEmpty()){
            tv_map_allpic_no_more.visible()
        } else {
            iv_map_placeholder.invisible()
            tv_map_allpic_no_more.invisible()
            rv_map_allpic_show_photo.adapter = PhotoStreamAdapter(urls , this , this)

            rv_map_allpic_show_photo.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }

    private fun initStatusBar(){
        val statusBarLinearParams = view_map_allpic_status_bar.layoutParams
        //LogUtils.d("ShowAllPicActivity" , "hight is"+getStatusBarHeight().toString())
        statusBarLinearParams.height = getStatusBarHeight()
        view_map_allpic_status_bar.layoutParams = statusBarLinearParams
        LogUtils.d("ShowAllPicActivity" , "hight is"+getStatusBarHeight().toString())
    }

    fun setImage(imageUrl : String){
        ShowPicFragment.url = imageUrl
        if (showPicFragment == null){
            showPicFragment = ShowPicFragment()
        }

        if (showPicFragment == null)
            return
        val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fm_map_show_pic, showPicFragment!!)
                    .addToBackStack("show_pic")
                    .commitAllowingStateLoss()
    }
}
