package com.mredrock.cyxbs.discover.map.util

import android.graphics.Typeface
import com.mredrock.cyxbs.common.BaseApp

object TypeFaceUtil {
    const val PFSCH_TYPEFACE = 1
    const val PFSCM_TYPEFACE = 2
    const val PFSCB_TYPEFACE = 3

    private var PFSCHTypeface: Typeface? = null
    private var PFSCMTypeface: Typeface? = null
    private var PFSCBTypeface: Typeface? = null

    fun getPFSCHTypeface(): Typeface? = if (PFSCHTypeface == null) {
        try {
            Typeface.createFromAsset(BaseApp.context.assets, "fonts/PingFang-SC-Heavy.otf")
        } catch (e: RuntimeException) {
            Typeface.DEFAULT
        }
    } else {
        PFSCHTypeface
    }

    fun getPFSCMTypeface(): Typeface? = if (PFSCMTypeface == null) {
        try {
            Typeface.createFromAsset(BaseApp.context.assets, "fonts/PingFang-SC-Medium.ttf")
        } catch (e: RuntimeException) {
            Typeface.DEFAULT
        }
    } else {
        PFSCMTypeface
    }

    fun getPFSCBTypeface(): Typeface? = if (PFSCBTypeface == null) {
        try {
            Typeface.createFromAsset(BaseApp.context.assets, "fonts/PingFang-SC-Bold.ttf")
        } catch (e: RuntimeException) {
            Typeface.DEFAULT
        }
    } else {
        PFSCBTypeface
    }
}