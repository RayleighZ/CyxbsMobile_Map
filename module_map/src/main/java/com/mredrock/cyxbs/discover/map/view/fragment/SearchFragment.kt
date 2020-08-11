package com.mredrock.cyxbs.discover.map.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.discover.map.R
import com.mredrock.cyxbs.discover.map.view.activity.MapActivity

class SearchFragment : BaseFragment() {

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

    override fun onDestroy() {
        super.onDestroy()

        if (activity is MapActivity) {
            (activity as MapActivity).changeSearchFragmentIsShowing()
        }
    }
}