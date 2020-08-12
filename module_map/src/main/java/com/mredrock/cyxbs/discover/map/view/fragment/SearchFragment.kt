package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.bean.Place
import com.mredrock.cyxbs.discover.map.config.PlaceData
import com.mredrock.cyxbs.discover.map.view.activity.MapActivity
import com.mredrock.cyxbs.discover.map.view.adapter.SearchAdapter
import kotlinx.android.synthetic.main.map_activity_map.*
import kotlinx.android.synthetic.main.map_fragment_search.*

class SearchFragment : BaseFragment() {

    var searchResultList: MutableList<Place> = ArrayList()
    var adapter: SearchAdapter = SearchAdapter(this, searchResultList, false)
    var historyAdapter: SearchAdapter = SearchAdapter(this, PlaceData.searchHistoryList, true)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.map_fragment_search, container, false)
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        return if (enter) {
            AnimationUtils.loadAnimation(activity, R.anim.anl_push_top_in)
        } else {
            AnimationUtils.loadAnimation(activity, R.anim.anl_push_top_out)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isClickable = true         //防止点击事件穿透Fragment到Activity
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_map_search.layoutManager = layoutManager
        rv_map_search.adapter = historyAdapter

        tv_map_search_delete_all.setOnClickListener {
            PlaceData.searchHistoryList.clear()
            historyAdapter.notifyDataSetChanged()
        }

//        changeAdapter(1)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity is MapActivity) {
            (activity as MapActivity).changeSearchFragmentIsShowing()
        }
    }

    fun changeAdapter(type: Int) {
        if (type == 0) {
            rv_map_search?.let {
                it.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        } else if (type == 1) {
            rv_map_search?.let {
                it.adapter = historyAdapter
                historyAdapter.notifyDataSetChanged()
            }
        }
    }
}