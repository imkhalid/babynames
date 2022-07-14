package com.kausTech.babynames.di.dashboard

import com.kausTech.network.DataManager
import com.kausTech.network.NetworkModule
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [ModuleClass::class,NetworkModule::class])
interface AppComponent {

    fun dataManager(): DataManager
}