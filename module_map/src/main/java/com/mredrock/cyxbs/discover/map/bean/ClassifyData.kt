package com.mredrock.cyxbs.discover.map.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 *  搜索栏下面的分类地点
 */
class ClassifyData : Serializable {
    @SerializedName("button_info")
    var buttonInfo: List<ClassifyPlace>? = null

    class ClassifyPlace : Serializable {
        @SerializedName("title")
        var title: String? = null

        @SerializedName("code")
        var code: String? = null

        @SerializedName("is_hot")
        var isHot: Boolean = false
    }
}