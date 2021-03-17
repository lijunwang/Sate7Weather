package com.wlj.sate7weather.fragments.me

import androidx.fragment.app.Fragment
import com.wlj.sate7weather.AboutMeActivity

open class BaseFragment :Fragment(){
    internal val settingViewModel by lazy {
        (activity as AboutMeActivity).settingViewMode
    }
    internal val aboutMeActivity by lazy {
        activity as AboutMeActivity
    }
}