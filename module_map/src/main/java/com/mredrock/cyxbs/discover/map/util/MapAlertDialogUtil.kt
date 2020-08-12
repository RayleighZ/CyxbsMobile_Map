package com.mredrock.cyxbs.discover.map.util

import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.mredrock.cyxbs.discover.map.R

object MapAlertDialogUtil {
    var dialogView: View? = null

    fun getMapAlertDialog(context: Context, title: String, content: String): AlertDialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        dialogView = View.inflate(context, R.layout.map_alert_dialog, null)
        builder.setView(dialogView)
        builder.setCancelable(true)
        val tvTitle: TextView? = dialogView?.findViewById(R.id.tv_map_alert_dialog_title) //设置标题
        val tvContent: TextView? = dialogView?.findViewById(R.id.tv_map_alert_dialog_content) //内容

        tvTitle?.text = title
        tvContent?.text = content

        return builder.create()
    }

    fun AlertDialog.setOnClickListener(
            negativeOnClickListener: View.OnClickListener? = null,
            positiveOnClickListener: View.OnClickListener? = null): AlertDialog {
        val btnNegative: Button? = dialogView?.findViewById(R.id.btn_map_alert_dialog_negative) //取消按钮
        val btnPositive: Button? = dialogView?.findViewById(R.id.btn_map_alert_dialog_positive) //确定按钮
        negativeOnClickListener?.let {
            btnNegative?.setOnClickListener(it)
        }
        positiveOnClickListener?.let {
            btnPositive?.setOnClickListener(it)
        }
        return this
    }
}