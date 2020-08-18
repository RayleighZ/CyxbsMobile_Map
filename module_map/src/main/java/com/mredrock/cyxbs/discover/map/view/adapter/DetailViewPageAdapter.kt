package com.mredrock.cyxbs.discover.map.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.LogUtils
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.discover.map.R

/**
 * @date 2020-08-11
 * @author Sca RayleighZ
 */
class DetailViewPageAdapter : PagerAdapter(){ //(fm : FragmentManager) : FragmentStatePagerAdapter(fm , FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)

    var listOfImageUrls = ArrayList<String>()

    //强行刷新界面
    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getPageWidth(position: Int): Float {
        if (count == 1) {
            return 1F
        }
        return 0.8F
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return if (listOfImageUrls.isEmpty()) {
            2
        } else {
            return listOfImageUrls.size
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val item = LayoutInflater.from(context).inflate(R.layout.map_item_card_imageview , container , false)

        val iv = item.findViewById<ImageView>(R.id.iv_map_card_item)
        iv.scaleType = ImageView.ScaleType.CENTER_CROP
        /*val layoutParams = iv.layoutParams
        layoutParams.width = context.dp2px(280f)
        layoutParams.height = context.dp2px(185f)
        iv.layoutParams = layoutParams*/

        if (count == 1) {
            iv.setPadding(0, 0, context.dp2px(15F), 0)
        }
        if (listOfImageUrls.isNotEmpty()) {
            LogUtils.d("DetailVPAdapter", listOfImageUrls[position])
            Glide.with(context)
                    .asBitmap()
                    .load(listOfImageUrls[position])
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(9)))
                    .placeholder(R.drawable.map_shape_placeholder_detail_pic)
                    .error(R.drawable.map_shape_placeholder_detail_pic)
                    .into(iv)
        } else {
            Glide.with(context).load(R.drawable.map_shape_placeholder_detail_pic)
                    .into(iv)
        }
        container.addView(item)
        return item
    }

    /*override fun getItem(position: Int): Fragment {
        val cardImageFragment = CardImageFragment()

        if (listOfImageUrls.isEmpty()){
            cardImageFragment.url =""
        } else {
            cardImageFragment.url = listOfImageUrls[position]
        }

        return cardImageFragment
    }*/
}