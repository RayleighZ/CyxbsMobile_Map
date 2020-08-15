package com.mredrock.cyxbs.discover.map.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.component.CyxbsToast
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.view.activity.MapActivity
import com.mredrock.cyxbs.discover.map.view.fragment.SearchFragment

class SearchAdapter(
        private val fragment: SearchFragment,
        private val dataList: List<Place>,
        private val isHistory: Boolean
) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            if (isHistory) {
                SearchHistoryViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                                R.layout.map_recycle_item_search,
                                parent, false
                        ))
            } else {
                SearchViewHolder(
                        LayoutInflater.from(parent.context).inflate(
                                R.layout.map_recycle_item_search_result,
                                parent, false
                        ))
            }


    override fun getItemCount(): Int = dataList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = dataList[position]

        if (holder is SearchViewHolder) {
            holder.tvName.text = item.placeName

            holder.itemView.setOnClickListener {
                //放到首位
                for (i: Int in PlaceData.searchHistoryList.indices) {
                    if (PlaceData.searchHistoryList[i].placeId == item.placeId) {
                        PlaceData.searchHistoryList.removeAt(i)
                        break
                    }
                }
                PlaceData.searchHistoryList.add(0, item)
                fragment.historyAdapter.notifyDataSetChanged()

                fragment.activity?.run {
                    supportFragmentManager.popBackStackImmediate("searchFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                    if (this is MapActivity) {
                        this.addHot(item.placeId)
                        this.removeAllPin()
                        this.pinAndZoomIn(item.placeCenterX, item.placeCenterY, item.placeId)
                        this.hideKeyBoard()
                        this.searchFragment = null
                    }
                }
            }
        } else if (holder is SearchHistoryViewHolder) {
            holder.tvName.text = item.placeName

            holder.itemView.setOnClickListener {
                //放到首位
                for (i: Int in PlaceData.searchHistoryList.indices) {
                    if (PlaceData.searchHistoryList[i].placeId == item.placeId) {
                        PlaceData.searchHistoryList.removeAt(i)
                        break
                    }
                }
                PlaceData.searchHistoryList.add(0, item)
                fragment.historyAdapter.notifyDataSetChanged()

                fragment.activity?.run {
                    supportFragmentManager.popBackStack()
                    if (this is MapActivity) {
                        this.addHot(item.placeId)
                        this.removeAllPin()
                        this.pinAndZoomIn(item.placeCenterX, item.placeCenterY, item.placeId)
                        this.hideKeyBoard()
                        this.searchFragment = null
                    }
                }
            }

            holder.ivDelete.setOnClickListener {
                PlaceData.searchHistoryList.remove(item)
                notifyItemRemoved(position)
                CyxbsToast.makeText(BaseApp.context, "点击：${item.placeName}删除", Toast.LENGTH_SHORT)
            }
        }
    }

    inner class SearchViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_map_item_search_name)
    }

    inner class SearchHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_map_item_search_history_name)
        val ivDelete: ImageView = view.findViewById(R.id.iv_map_item_search_delete)
    }
}