package com.egoriku.mainfragment.presentation.fragment

import com.egoriku.corelib_kt.arch.BaseContract
import com.egoriku.ui.BaseView

interface MainPageContract {

    interface View : BaseView {
        fun initViews()
        fun showInformation()
    }

    interface Presenter : BaseContract.Presenter<View>
}