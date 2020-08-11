package com.mredrock.cyxbs.discover.map.viewmodel

import androidx.databinding.ObservableField
import com.mredrock.cyxbs.common.viewmodel.BaseViewModel

/**
 * @date 2020-08-12
 * @author Sca RayleighZ
 */
class CollectViewModel : BaseViewModel() {
    val placeName = ObservableField<String>()
    val editTextCharNum = ObservableField<String>("4/8")
}