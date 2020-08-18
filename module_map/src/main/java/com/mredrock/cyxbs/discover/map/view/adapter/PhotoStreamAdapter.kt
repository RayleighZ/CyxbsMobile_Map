package com.mredrock.cyxbs.discover.map.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.utils.extensions.dp2px
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.view.activity.ShowAllPicActivity
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.windowManager

/**
 * @date 2020-08-13
 * @author Sca RayleighZ
 */
class PhotoStreamAdapter(private val arrayOfUrls: Array<String>, private val context: Context  , private val activity : ShowAllPicActivity) : RecyclerView.Adapter<PhotoStreamAdapter.InnerViewHolder>() {
    inner class InnerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView : PhotoView = itemView.findViewById<PhotoView>(R.id.iv_map_pic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder = InnerViewHolder(
            LayoutInflater.from(parent.context).inflate(
                    R.layout.map_recycler_item_photo,
                    parent, false
            )
    )

    override fun getItemCount(): Int {
        return arrayOfUrls.size
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {

        val wm = context.windowManager
        val width = wm.defaultDisplay.width
        val widthOfPic = (width - 30) / 2
        holder.imageView.onClick {
            activity.setImage(arrayOfUrls[position])
        }
        if (position % 2 == 0){
            holder.imageView.setPadding(0 , 0 , BaseApp.context.dp2px(4.5f) , 0 )
        }
        else{
            holder.imageView.setPadding(BaseApp.context.dp2px(4.5f) , 0 , 0, 0 )
        }

        Glide.with(context).load(arrayOfUrls[position])
                .apply(RequestOptions.bitmapTransform(RoundedCorners(BaseApp.context.dp2px(9F))))
                .placeholder(R.drawable.map_shape_placeholder_detail_pic)
                .error(R.drawable.map_shape_placeholder_detail_pic)
                .override(widthOfPic, 112)
                .into(holder.imageView)
    }
}