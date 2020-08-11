package com.mredrock.cyxbs.discover.map.util

import android.graphics.Typeface
import com.mredrock.cyxbs.common.BaseApp

object TypeFaceUtil {
    const val PFSCH_TYPEFACE = 1
    const val PFSCM_TYPEFACE = 2

    private var PFSCHTypeface: Typeface? = null
    private var PFSCMTypeface: Typeface? = null

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
}