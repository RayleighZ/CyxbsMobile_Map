package com.mredrock.cyxbs.discover.map.view.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.discover.map.R

/**
 * @date 2020-08-11
 * @author Sca RayleighZ
 */
class DetailViewPageAdapter : PagerAdapter(){

    var listOfImageUrls = ArrayList<String>()


    //强行刷新界面
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getPageWidth(position: Int): Float {
        return 0.8F
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return  if (listOfImageUrls.isEmpty()){
            2
        } else {
            listOfImageUrls.size
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val iv = ImageView(context)
        iv.scaleType = ImageView.ScaleType.FIT_CENTER
        if (listOfImageUrls.isNotEmpty()){
            LogUtils.d("DetailVPAdapter" , listOfImageUrls[position])
            Glide.with(context).load(listOfImageUrls[position])
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(9)))
                    .placeholder(R.drawable.map_shape_placeholder_detail_pic)
                    .error(R.drawable.map_shape_placeholder_detail_pic)
                    .override(280 , 185)
                    .into(iv)
        }
        else{
            Glide.with(context).load(R.drawable.map_shape_placeholder_detail_pic)
                    .override(280 , 185)
                    .into(iv)
        }
        container.addView(iv)
        return iv
    }
}