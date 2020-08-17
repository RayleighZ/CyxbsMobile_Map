package com.mredrock.cyxbs.discover.map.view.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.ui.BaseActivity
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.common.utils.extensions.getStatusBarHeight
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.util.MapAlertDialogUtil
import com.mredrock.cyxbs.discover.map.util.MapAlertDialogUtil.setOnClickListener
import com.mredrock.cyxbs.discover.map.view.adapter.PhotoStreamAdapter
import com.mredrock.cyxbs.discover.map.view.fragment.ShowPicFragment
import com.mredrock.cyxbs.discover.map.view.fragment.DetailFragment
import com.mredrock.cyxbs.discover.map.viewmodel.ShowAllPicViewModel
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.internal.entity.CaptureStrategy
import com.zhihu.matisse.listener.OnSelectedListener
import kotlinx.android.synthetic.main.map_activity_show_all_pic.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ShowAllPicActivity : BaseActivity() {
    private val viewModel by lazy { ViewModelProvider(this).get(ShowAllPicViewModel::class.java) }

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
        if (urls.isEmpty()) {
            tv_map_allpic_no_more.visible()
        } else {
            iv_map_placeholder.invisible()
            tv_map_allpic_no_more.invisible()
            rv_map_allpic_show_photo.adapter = PhotoStreamAdapter(urls , this , this)

            rv_map_allpic_show_photo.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }

        tv_map_allpic_share.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                ) {
                    CyxbsToast.makeText(BaseApp.context, "无权限，请手动打开存储和相机权限", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
            }

            val dialog: AlertDialog = MapAlertDialogUtil.getMapAlertDialog(this, getString(R.string.map_alert_dialog_share_photo_title),
                    getString(R.string.map_alert_dialog_share_photo_content))
            dialog.setOnClickListener(View.OnClickListener {
                dialog.cancel()
            }, View.OnClickListener {
                dialog.cancel()
                Matisse.from(this)
                        .choose(MimeType.ofImage(), false)
                        .countable(true)
                        .capture(true)
                        .captureStrategy(
                                CaptureStrategy(true, "${this.application.packageName}.fileProvider", "cyxbs"))
                        .maxSelectable(9)
//                            .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .gridExpectedSize(BaseApp.context.dp2px(120f))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .imageEngine(GlideEngine())
                        .setOnSelectedListener(OnSelectedListener { uriList: List<Uri?>?, pathList: List<String?> -> LogUtils.e("onSelected", "onSelected: pathList=$pathList") })
                        .showSingleMediaType(true)
                        .originalEnable(true)
                        .maxOriginalSize(10)
                        .autoHideToolbarOnSingleTap(true)
                        .setOnCheckedListener { isChecked: Boolean -> LogUtils.e("isChecked", "onCheck: isChecked=$isChecked") }
                        .forResult(1)
            }).show()

        }
    }

    private fun initStatusBar() {
        val statusBarLinearParams = view_map_allpic_status_bar.layoutParams
        statusBarLinearParams.height = getStatusBarHeight()
        view_map_allpic_status_bar.layoutParams = statusBarLinearParams
        LogUtils.d("ShowAllPicActivity", "hight is" + getStatusBarHeight().toString())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            viewModel.uploadPhoto(Matisse.obtainResult(data), Matisse.obtainPathResult(data), DetailFragment.placeId)
        }
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
