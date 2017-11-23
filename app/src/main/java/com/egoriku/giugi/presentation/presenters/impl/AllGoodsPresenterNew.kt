package com.egoriku.giugi.presentation.presenters.impl

import com.egoriku.giugi.R
import com.egoriku.giugi.adapter.ToysItem
import com.egoriku.giugi.data.Toy
import com.egoriku.giugi.domain.interactors.Params
import com.egoriku.giugi.domain.interactors.allgoods.CategoriesUseCase
import com.egoriku.giugi.domain.models.CategoriesModel
import com.egoriku.giugi.external.AnalyticsInterface
import com.egoriku.giugi.external.TrackingConstants
import com.egoriku.giugi.presentation.presenters.AllGoodsMVP
import com.egoriku.giugi.presentation.presenters.base.BasePresenter
import com.egoriku.giugi.rx.DefaultObserver
import javax.inject.Inject

class AllGoodsPresenterNew
@Inject constructor(private val analyticsInterface: AnalyticsInterface,
                    private val getCategoriesUseCase: CategoriesUseCase)
    : BasePresenter<AllGoodsMVP.View>(), AllGoodsMVP.Presenter {

    override fun attachView(view: AllGoodsMVP.View) {
        super.attachView(view)
        analyticsInterface.trackPageView(TrackingConstants.VIEW_ALL_GOODS)
    }

    override fun detachView() {
        getCategoriesUseCase.dispose()
        super.detachView()
    }

    override fun getCategories() {
        view?.showLoading()
        getCategoriesUseCase.execute(GetCategoriesObserver(), Params.EMPTY)

        val list = mutableListOf<Any>()
        list.add(ToysItem(Toy("1", R.drawable.ic1)))
        list.add(ToysItem(Toy("2", R.drawable.ic2)))
        list.add(ToysItem(Toy("3", R.drawable.ic3)))
        list.add(ToysItem(Toy("4", R.drawable.ic4)))
        list.add(ToysItem(Toy("5", R.drawable.ic5)))
        list.add(ToysItem(Toy("6", R.drawable.ic6)))
        list.add(ToysItem(Toy("7", R.drawable.ic7)))
        list.add(ToysItem(Toy("8", R.drawable.ic8)))
    }

    override fun onGetCategoriesSuccess(categoriesModel: CategoriesModel) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGetCategoriesFailure(e: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCategoryClickSuccessTracking() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCategoryClickFailureTracking(e: Throwable) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGetCategoriesSuccessTracking() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onGetCategoriesFailureTracking() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private inner class GetCategoriesObserver : DefaultObserver<CategoriesModel>() {

        override fun onComplete() {
        }

        override fun onError(exception: Throwable) {
            onGetCategoriesFailureTracking()
            onGetCategoriesFailure(exception)
        }

        @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
        override fun onNext(categoriesModel: CategoriesModel) {
            onGetCategoriesSuccessTracking()
            onGetCategoriesSuccess(categoriesModel)
        }
    }
}