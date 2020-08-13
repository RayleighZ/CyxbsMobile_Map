package com.mredrock.cyxbs.discover.map.view.adapter

import android.R.attr.button
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.visible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.ClassifyData.ClassifyPlace
import com.mredrock.cyxbs.discover.map.view.activity.MapActivity


class ClassifyAdapter(
        private val activity: MapActivity,
        private val dataList: List<ClassifyPlace>
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var selectedIndex = -1      //-1是没选择任何

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ClassifyViewHolder(
            LayoutInflater.from(parent.context).inflate(
                    R.layout.map_recycle_item_classify,
                    parent, false
            )
    )


    override fun getItemCount(): Int = dataList.size

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder is ClassifyViewHolder && selectedIndex != holder.adapterPosition) {
            holder.tvName.background = null
        }
        super.onViewAttachedToWindow(holder)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        if (holder is ClassifyViewHolder) {
            if (item.isHot) {
                holder.ivHot.visible()
            } else {
                holder.ivHot.gone()
            }
            holder.tvName.text = item.title ?: ""
            if (selectedIndex != position) {
                holder.tvName.background = null
            }
            holder.itemView.setOnClickListener {
                //显示圆角矩形逻辑
                if (selectedIndex != -1) {
                    val viewHolder = getViewHolderInWindow(activity.getClassifyRecyclerView(), selectedIndex)
                    if (viewHolder is ClassifyViewHolder) {
                        viewHolder.tvName.background = null
                    }
                }
                selectedIndex = position
                holder.tvName.setBackgroundResource(R.drawable.map_shape_classify_item_selected)
                val alpha: ObjectAnimator = ObjectAnimator.ofFloat(holder.tvName, "alpha", 0f, 1f)
                alpha.duration = 300
                alpha.start()

                //在下面做点击逻辑
                CyxbsToast.makeText(BaseApp.context, "点击了：${holder.tvName.text}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class ClassifyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivHot: ImageView = view.findViewById(R.id.iv_map_classify_hot)
        val tvName: TextView = view.findViewById(R.id.tv_map_classify_name)
    }

    private fun getViewHolderInWindow(recyclerView: RecyclerView, index: Int): RecyclerView.ViewHolder? {
        return recyclerView.findViewHolderForAdapterPosition(index)
    }
}