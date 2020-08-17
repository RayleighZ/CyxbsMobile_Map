package com.mredrock.cyxbs.discover.map.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.common.utils.extensions.invisible
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.FavoritePlace
import com.mredrock.cyxbs.discover.map.view.activity.MapActivity

class FavoriteAdapter(
        private val activity: MapActivity,
        private val dataList: List<FavoritePlace>
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = FavoriteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                    R.layout.map_recycle_item_favorite,
                    parent, false
            )
    )

    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        if (holder is FavoriteViewHolder) {
//            holder.tvName.text = item.placeNickname

            if (position == 0) {
                holder.tvName.setPadding(holder.tvName.paddingStart, 0,
                        holder.tvName.paddingEnd, holder.tvName.paddingBottom)
            }

            if (position == dataList.size - 1) {
                holder.viewHorizontalLine.invisible()
                holder.tvName.setPadding(holder.tvName.paddingStart, holder.tvName.paddingTop,
                        holder.tvName.paddingEnd, 0)
            }

            holder.itemView.setOnClickListener {
//                CyxbsToast.makeText(BaseApp.context, "点击：${item.placeNickname}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    inner class FavoriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_map_favorite_item_name)
        val viewHorizontalLine: View = view.findViewById(R.id.view_map_favorite_item_horizontal_line)
    }
}