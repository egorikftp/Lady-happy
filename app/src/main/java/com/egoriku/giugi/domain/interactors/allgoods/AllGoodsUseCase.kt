package com.egoriku.giugi.domain.interactors.allgoods

import com.egoriku.giugi.domain.interactors.Params
import com.egoriku.giugi.domain.interactors.base.BaseUseCase
import io.reactivex.Observable

/**
 * Implementation of {@link BaseUseCase} that represents a UseCase/Interactor
 */

class AllGoodsUseCase(private val allGoodsRepository: ): BaseUseCase() {



    override fun getObservable(params: Params): Observable<*> {

    }

}