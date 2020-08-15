package com.mredrock.cyxbs.discover.map.util

import android.content.Context
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.discover.map.R

object DownloadProgressDialogUtil {
    var dialogView: View? = null
    var pbProgress: ProgressBar? = null

    fun getDownloadProgressDialog(context: Context, title: String): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        MapAlertDialogUtil.dialogView = View.inflate(context, R.layout.map_download_progress_dialog, null)
        builder.setView(MapAlertDialogUtil.dialogView)
        builder.setCancelable(true)
        val tvTitle: TextView? = MapAlertDialogUtil.dialogView?.findViewById(R.id.tv_map_download_progress_title) //设置标题
        pbProgress = MapAlertDialogUtil.dialogView?.findViewById(R.id.pb_map_download_progress)
        tvTitle?.text = title
        pbProgress?.progress = 0

        return builder.create()
    }

    fun AlertDialog.getProgressBar(): ProgressBar? {
        return pbProgress
    }
}

fun isOnlineByPing(ipAddress: String): Boolean {
    try {
        val process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 5000 $ipAddress")
        val status = process.waitFor()
        return status == 0
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}