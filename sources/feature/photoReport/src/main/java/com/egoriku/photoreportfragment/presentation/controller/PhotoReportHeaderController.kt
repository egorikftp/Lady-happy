package com.egoriku.photoreportfragment.presentation.controller

import android.view.ViewGroup
import com.egoriku.photoreportfragment.R
import ru.surfstudio.android.easyadapter.controller.NoDataItemController
import ru.surfstudio.android.easyadapter.holder.BaseViewHolder

class PhotoReportHeaderController : NoDataItemController<PhotoReportHeaderController.Holder>() {

    override fun createViewHolder(parent: ViewGroup) = Holder(parent)

    inner class Holder(parent: ViewGroup) : BaseViewHolder(parent, R.layout.adapter_item_photo_report_header)
}