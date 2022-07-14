package com.kausTech.babynames.di.dashboard

import com.kausTech.network.DataManager
import com.kausTech.network.NetworkModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [ModuleClass::class,NetworkModule::class]
)
@DashboardScope
interface ComponentClass {
    @Component.Builder
    interface Builder {
        fun build(): ComponentClass
    }

    fun getViewModel() : DashViewModel
    fun dataManager(): DataManager
}