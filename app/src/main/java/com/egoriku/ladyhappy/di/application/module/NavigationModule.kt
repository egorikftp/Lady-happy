package com.egoriku.ladyhappy.di.application.module

import com.egoriku.core.di.ApplicationScope
import com.egoriku.core.di.utils.INavigationHolder
import com.egoriku.core.di.utils.IRouter
import com.egoriku.ladyhappy.di.tools.NavigationHolder
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone

@Module
class NavigationModule {

    private var cicerone: Cicerone<ru.terrakok.cicerone.Router> = Cicerone.create()

    @ApplicationScope
    @Provides
    fun provideRouter(): IRouter = com.egoriku.ladyhappy.di.tools.Router(cicerone.router)

    @ApplicationScope
    @Provides
    fun provideNavigationHolder(): INavigationHolder = NavigationHolder(cicerone.navigatorHolder)
}